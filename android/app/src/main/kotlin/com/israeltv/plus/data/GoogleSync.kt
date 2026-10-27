package com.israeltv.plus.data

import android.accounts.AccountManager
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Immutable
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.security.MessageDigest
import java.util.concurrent.TimeUnit

/**
 * Connected Google Account metadata persisted in SharedPreferences ("itv") and used for automatic cloud sync.
 */
@Immutable
@Serializable
data class GoogleUser(
    val email: String,
    val displayName: String,
    val photoUrl: String = "",
    val syncKey: String = "",
    val lastSyncedAt: Long = 0L,
)

@Serializable
data class CloudSyncPayload(
    val updatedAt: Long,
    val email: String,
    val displayName: String,
    val activeProfileId: String,
    val launchSound: Boolean,
    val profiles: List<Profile>,
)

object GoogleSync {
    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }
    private val http = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(12, TimeUnit.SECONDS)
        .build()

    // Deterministic cloud key derived from the Google email so the same Google account syncs across phones & TVs
    fun deriveSyncKey(email: String): String {
        val clean = email.trim().lowercase()
        val digest = MessageDigest.getInstance("SHA-256").digest(("israeltv-plus-sync-v1:$clean").toByteArray())
        return digest.take(10).joinToString("") { "%02x".format(it) }
    }

    /** Short 6-character pairing code derived from the Google email for quick TV pairing if desired. */
    fun shortPairingCode(email: String): String {
        val hex = deriveSyncKey(email)
        return hex.take(6).uppercase()
    }

    /**
     * Builds the native Google Sign-In Intent (`GoogleSignIn.getClient`), requesting email and profile.
     * Falls back to Android's native `AccountManager.newChooseAccountIntent` if Play Services is unavailable.
     */
    fun buildSignInIntent(context: Context): Intent {
        return runCatching {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build()
            GoogleSignIn.getClient(context, gso).signInIntent
        }.getOrElse {
            buildAccountPickerIntent()
        }
    }

    @Suppress("DEPRECATION")
    fun buildAccountPickerIntent(): Intent =
        AccountManager.newChooseAccountIntent(
            null,
            null,
            arrayOf("com.google"),
            null,
            null,
            null,
            null,
        )

    /**
     * Extracts [GoogleUser] from either `GoogleSignIn.getSignedInAccountFromIntent` or `AccountManager` picker result.
     */
    fun parseSignInResult(data: Intent?): GoogleUser? {
        if (data == null) return null
        // 1. Try GoogleSignInAccount from Play Services
        runCatching {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            val email = account?.email?.trim().orEmpty()
            if (email.isNotEmpty()) {
                val name = account?.displayName?.takeIf { it.isNotBlank() } ?: email.substringBefore('@')
                val photo = account?.photoUrl?.toString().orEmpty()
                return GoogleUser(
                    email = email,
                    displayName = name,
                    photoUrl = photo,
                    syncKey = deriveSyncKey(email),
                    lastSyncedAt = System.currentTimeMillis(),
                )
            }
        }
        // 2. Fallback: Android AccountManager result (KEY_ACCOUNT_NAME)
        val acctName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)?.trim().orEmpty()
        if (acctName.isNotEmpty() && '@' in acctName) {
            val display = acctName.substringBefore('@').replaceFirstChar { it.uppercase() }
            return GoogleUser(
                email = acctName,
                displayName = display,
                photoUrl = "",
                syncKey = deriveSyncKey(acctName),
                lastSyncedAt = System.currentTimeMillis(),
            )
        }
        return null
    }

    fun createManualGoogleUser(emailInput: String, nameInput: String = ""): GoogleUser? {
        val cleanEmail = emailInput.trim().lowercase()
        if (cleanEmail.isEmpty()) return null
        val fullEmail = if ('@' in cleanEmail) cleanEmail else "$cleanEmail@gmail.com"
        val display = nameInput.trim().ifEmpty { fullEmail.substringBefore('@').replaceFirstChar { it.uppercase() } }
        return GoogleUser(
            email = fullEmail,
            displayName = display,
            photoUrl = "",
            syncKey = deriveSyncKey(fullEmail),
            lastSyncedAt = System.currentTimeMillis(),
        )
    }

    fun signOutClient(context: Context) {
        runCatching {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
            GoogleSignIn.getClient(context, gso).signOut()
        }
    }

    private fun cloudEndpoint(syncKey: String): String =
        "https://kvdb.io/A6p8K9m2X4v7Q1w3Z5n8R2/itv_sync_$syncKey"

    /**
     * Pushes the current profiles, favorites, recents, and theme settings to the cloud for [user].
     */
    suspend fun pushToCloud(prefs: AppPrefs): Boolean = withContext(Dispatchers.IO) {
        val user = prefs.googleUser ?: return@withContext false
        val key = user.syncKey.ifEmpty { deriveSyncKey(user.email) }
        val payload = CloudSyncPayload(
            updatedAt = System.currentTimeMillis(),
            email = user.email,
            displayName = user.displayName,
            activeProfileId = prefs.activeId,
            launchSound = prefs.launchSound,
            profiles = prefs.profiles,
        )
        val bodyStr = json.encodeToString(payload)
        runCatching {
            val req = Request.Builder()
                .url(cloudEndpoint(key))
                .put(bodyStr.toRequestBody("application/json; charset=utf-8".toMediaType()))
                .header("User-Agent", USER_AGENT)
                .build()
            http.newCall(req).execute().use { resp ->
                resp.isSuccessful
            }
        }.getOrDefault(false)
    }

    /**
     * Pulls cloud state for [user] and merges favorites, recents, profiles, and theme settings.
     */
    suspend fun pullFromCloud(prefs: AppPrefs): CloudSyncPayload? = withContext(Dispatchers.IO) {
        val user = prefs.googleUser ?: return@withContext null
        val key = user.syncKey.ifEmpty { deriveSyncKey(user.email) }
        runCatching {
            val req = Request.Builder()
                .url(cloudEndpoint(key))
                .get()
                .header("User-Agent", USER_AGENT)
                .build()
            http.newCall(req).execute().use { resp ->
                if (!resp.isSuccessful) return@use null
                val text = resp.body.string()
                if (text.isBlank()) return@use null
                json.decodeFromString<CloudSyncPayload>(text)
            }
        }.getOrNull()
    }
}


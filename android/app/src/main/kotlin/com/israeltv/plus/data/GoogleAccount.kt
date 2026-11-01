package com.israeltv.plus.data

import android.accounts.Account
import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.IntentSenderRequest
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import com.google.android.gms.auth.api.identity.AuthorizationRequest
import com.google.android.gms.auth.api.identity.AuthorizationResult
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.RevokeAccessRequest
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Scope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.URLEncoder

private const val FOLDER_NAME = "ישראל TV+ - הקלטות"
private const val BACKUP_NAME = "israel_tv_backup.json"
private const val DRIVE = "https://www.googleapis.com/drive/v3/files"
private const val DRIVE_UPLOAD = "https://www.googleapis.com/upload/drive/v3/files"
private const val FOLDER_MIME = "application/vnd.google-apps.folder"
private val JSON_TYPE = "application/json; charset=UTF-8".toMediaType()
private val json = Json { ignoreUnknownKeys = true }

private val SCOPES = listOf(
    Scope("https://www.googleapis.com/auth/drive.file"),
    Scope("email"),
    Scope("profile"),
)

/**
 * The Google account of the phone app. The account picker is the system's own (Google Play services), with no browser
 * and no web page. Google recognises the app by package name + signing certificate, so no client id is stored here
 * (docs/GOOGLE_SIGNIN.md). Play services keeps and renews the token.
 *
 * Sync shares israel_tv_backup.json with the TV and web apps: favourites and recents of the active profile use their
 * fields, and the phone profiles live in "phoneProfiles". Other fields of the file are left as they are.
 */
@Stable
class GoogleAccount(context: Context, private val prefs: AppPrefs, private val http: OkHttpClient, private val scope: CoroutineScope) {
    private val sp = context.getSharedPreferences("itv", Context.MODE_PRIVATE)

    var email by mutableStateOf(sp.getString("googleEmail", "") ?: "")
        private set
    var name by mutableStateOf(sp.getString("googleName", "") ?: "")
        private set
    var picture by mutableStateOf(sp.getString("googlePicture", "") ?: "")
        private set
    var lastSync by mutableLongStateOf(sp.getLong("googleLastSync", 0L))
        private set
    var busy by mutableStateOf(false)
        private set
    /** Shown under the account row; empty when all is well. */
    var problem by mutableStateOf("")
        private set

    val signedIn: Boolean get() = email.isNotEmpty()

    private fun request() = AuthorizationRequest.builder().setRequestedScopes(SCOPES).build()

    /** Starts sign-in; [launch] shows the system account picker when Play services needs the person to choose. */
    fun signIn(activity: Activity, launch: (IntentSenderRequest) -> Unit) {
        problem = ""
        busy = true
        Identity.getAuthorizationClient(activity).authorize(request())
            .addOnSuccessListener { r ->
                val pending = r.pendingIntent
                if (r.hasResolution() && pending != null) launch(IntentSenderRequest.Builder(pending.intentSender).build())
                else onAuthorized(r)
            }
            .addOnFailureListener(::onFailure)
    }

    /** The answer of the account picker. */
    fun onPickerResult(activity: Activity, data: Intent?) {
        runCatching { Identity.getAuthorizationClient(activity).getAuthorizationResultFromIntent(data) }
            .onSuccess(::onAuthorized)
            .onFailure(::onFailure)
    }

    /** Background sync (app start, leaving the app, "sync now"). Never shows UI: an expired grant only reports a problem. */
    fun sync(activity: Activity) {
        if (!signedIn || busy) return
        busy = true
        Identity.getAuthorizationClient(activity).authorize(request())
            .addOnSuccessListener { r ->
                val token = r.accessToken
                if (token == null || r.hasResolution()) {
                    busy = false
                    problem = "צריך להתחבר שוב לחשבון Google"
                } else run(token, withProfile = false)
            }
            .addOnFailureListener(::onFailure)
    }

    fun signOut(activity: Activity) {
        val account = Account(email, "com.google")
        Identity.getAuthorizationClient(activity)
            .revokeAccess(RevokeAccessRequest.builder().setAccount(account).setScopes(SCOPES).build())
        email = ""
        name = ""
        picture = ""
        lastSync = 0L
        problem = ""
        sp.edit {
            remove("googleEmail"); remove("googleName"); remove("googlePicture"); remove("googleLastSync")
        }
    }

    private fun onAuthorized(result: AuthorizationResult) {
        val token = result.accessToken
        if (token == null) onFailure(IllegalStateException("no token")) else run(token, withProfile = true)
    }

    private fun onFailure(e: Throwable) {
        busy = false
        val code = (e as? ApiException)?.statusCode
        problem = when (code) {
            CommonStatusCodes.CANCELED -> ""
            CommonStatusCodes.DEVELOPER_ERROR -> "התחברות עם Google עדיין לא הוגדרה עבור האפליקציה"
            CommonStatusCodes.NETWORK_ERROR -> "אין חיבור לאינטרנט"
            null -> "ההתחברות בוטלה"
            else -> "ההתחברות נכשלה (קוד $code). נסו שוב בעוד כמה דקות"
        }
    }

    private fun run(token: String, withProfile: Boolean) {
        scope.launch {
            val outcome = withContext(Dispatchers.IO) {
                runCatching {
                    if (withProfile) loadProfile(token)
                    syncBackup(token)
                }
            }
            outcome.onSuccess { fromCloud ->
                problem = ""
                if (fromCloud != null) prefs.applyCloud(fromCloud.profiles, fromCloud.favorites, fromCloud.recents, fromCloud.updatedAt)
                lastSync = System.currentTimeMillis()
                sp.edit { putLong("googleLastSync", lastSync) }
            }.onFailure {
                problem = "הסנכרון נכשל: ${it.message.orEmpty()}"
            }
            busy = false
        }
    }

    private suspend fun loadProfile(token: String) {
        val info = json.parseToJsonElement(call(token, Request.Builder().url("https://www.googleapis.com/oauth2/v3/userinfo"))).jsonObject
        val mail = info.text("email")
        val display = info.text("name").ifEmpty { mail.substringBefore('@') }
        val photo = info.text("picture")
        withContext(Dispatchers.Main) {
            email = mail
            name = display
            picture = photo
            sp.edit { putString("googleEmail", mail); putString("googleName", display); putString("googlePicture", photo) }
        }
    }

    private class FromCloud(val profiles: String?, val favorites: List<String>?, val recents: List<String>?, val updatedAt: Long)

    /** Newest side wins, like the web app. Returns what to apply here, or null when this device's copy was uploaded. */
    private fun syncBackup(token: String): FromCloud? {
        val folder = findId(token, "name = ${q(FOLDER_NAME)} and mimeType = ${q(FOLDER_MIME)} and trashed = false") ?: createFolder(token)
        val fileId = findId(token, "name = ${q(BACKUP_NAME)} and ${q(folder)} in parents and trashed = false")
        val cloud = fileId?.let {
            json.parseToJsonElement(call(token, Request.Builder().url("$DRIVE/$it?alt=media"))).jsonObject
        }
        val cloudAt = cloud?.get("updatedAt")?.jsonPrimitive?.longOrNull ?: 0L
        if (cloud != null && cloudAt > prefs.changedAt) {
            return FromCloud(cloud["phoneProfiles"]?.jsonPrimitive?.contentOrNull, cloud.strings("favorites"), cloud.strings("recents"), cloudAt)
        }
        val merged = JsonObject(
            cloud.orEmpty() + mapOf(
                "phoneProfiles" to JsonPrimitive(prefs.exportProfiles()),
                "favorites" to JsonArray(prefs.favorites.map(::JsonPrimitive)),
                "recents" to JsonArray(prefs.recents.map(::JsonPrimitive)),
                "updatedAt" to JsonPrimitive(prefs.changedAt),
            )
        ).toString()
        if (fileId != null) {
            call(token, Request.Builder().url("$DRIVE_UPLOAD/$fileId?uploadType=media").patch(merged.toRequestBody(JSON_TYPE)))
        } else {
            val created = call(token, Request.Builder().url(DRIVE).post(buildJsonObject {
                put("name", JsonPrimitive(BACKUP_NAME))
                put("parents", JsonArray(listOf(JsonPrimitive(folder))))
                put("mimeType", JsonPrimitive("application/json"))
            }.toString().toRequestBody(JSON_TYPE)))
            val id = json.parseToJsonElement(created).jsonObject.text("id")
            call(token, Request.Builder().url("$DRIVE_UPLOAD/$id?uploadType=media").patch(merged.toRequestBody(JSON_TYPE)))
        }
        return null
    }

    private fun findId(token: String, query: String): String? {
        val url = "$DRIVE?fields=files(id)&q=" + URLEncoder.encode(query, "UTF-8")
        val files = json.parseToJsonElement(call(token, Request.Builder().url(url))).jsonObject["files"]?.jsonArray
        return files?.firstOrNull()?.jsonObject?.text("id")
    }

    private fun createFolder(token: String): String {
        val body = buildJsonObject {
            put("name", JsonPrimitive(FOLDER_NAME))
            put("mimeType", JsonPrimitive(FOLDER_MIME))
        }.toString().toRequestBody(JSON_TYPE)
        return json.parseToJsonElement(call(token, Request.Builder().url(DRIVE).post(body))).jsonObject.text("id")
    }

    private fun call(token: String, request: Request.Builder): String =
        http.newCall(request.header("Authorization", "Bearer $token").build()).execute().use { r ->
            val body = r.body.string()
            if (!r.isSuccessful) error("HTTP ${r.code}")
            body
        }

    private fun q(value: String) = "'" + value.replace("\\", "\\\\").replace("'", "\\'") + "'"
    private fun JsonObject.text(key: String) = this[key]?.jsonPrimitive?.contentOrNull.orEmpty()
    private fun JsonObject.strings(key: String) =
        (this[key] as? JsonArray)?.mapNotNull { (it as? JsonPrimitive)?.contentOrNull }
}

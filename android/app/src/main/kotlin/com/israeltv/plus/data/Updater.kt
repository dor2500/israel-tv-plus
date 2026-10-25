package com.israeltv.plus.data

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.israeltv.plus.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File

@Immutable
data class UpdateInfo(val version: String, val code: Int, val notes: List<String>, val url: String)

@Immutable
sealed interface UpdateState {
    data object Idle : UpdateState
    data object Checking : UpdateState
    data class UpToDate(val checkedAt: Long) : UpdateState
    data class Available(val info: UpdateInfo) : UpdateState
    data class Downloading(val info: UpdateInfo, val progress: Float) : UpdateState
    data class Installing(val info: UpdateInfo) : UpdateState
    data class Failed(val message: String, val info: UpdateInfo?) : UpdateState
}

/**
 * New versions of the app: version.json on the site names the newest Android APK (the GitHub release). It is
 * downloaded here with progress and handed to the system installer (PackageInstaller session), like MainActivity did.
 */
@Stable
class Updater(private val context: Context, private val http: OkHttpClient, private val scope: CoroutineScope) {
    var state by mutableStateOf<UpdateState>(UpdateState.Idle)
        private set
    private var job: Job? = null

    val installedVersion: String get() = BuildConfig.VERSION_NAME

    init {
        // the downloaded APK (about 20 MB) is only needed until it is installed: a start after that cleans it up
        scope.launch(Dispatchers.IO) { runCatching { File(context.cacheDir, "update.apk").delete() } }
    }

    fun check() {
        if (state is UpdateState.Checking || state is UpdateState.Downloading || state is UpdateState.Installing) return
        state = UpdateState.Checking
        job = scope.launch {
            state = withContext(Dispatchers.IO) {
                runCatching {
                    val req = Request.Builder().url(REMOTE + "version.json?t=" + System.currentTimeMillis()).build()
                    http.newCall(req).execute().use { r ->
                        val root = Json.parseToJsonElement(r.body.string()).jsonObject
                        val android = root["native"]?.jsonObject?.get("android")?.jsonObject
                            ?: return@runCatching UpdateState.UpToDate(System.currentTimeMillis())
                        val code = android["app"]!!.jsonPrimitive.int
                        if (code <= BuildConfig.VERSION_CODE) return@runCatching UpdateState.UpToDate(System.currentTimeMillis())
                        val notes = root["notes"]?.jsonArray?.map { it.jsonPrimitive.content }.orEmpty()
                        UpdateState.Available(
                            UpdateInfo(android["version"]!!.jsonPrimitive.content, code, notes, android["url"]!!.jsonPrimitive.content)
                        )
                    }
                }.getOrElse { UpdateState.Failed("אין חיבור לשרת העדכונים", null) }
            }
        }
    }

    fun download(info: UpdateInfo) {
        if (Build.VERSION.SDK_INT >= 26 && !context.packageManager.canRequestPackageInstalls()) {
            // Android 8+: the user allows this app to install updates once
            runCatching {
                context.startActivity(
                    Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + context.packageName))
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            }
            state = UpdateState.Failed("אשרו התקנת עדכונים לאפליקציה ולחצו שוב", info)
            return
        }
        job?.cancel()
        state = UpdateState.Downloading(info, 0f)
        job = scope.launch {
            val apk = File(context.cacheDir, "update.apk")
            val ok = withContext(Dispatchers.IO) {
                runCatching {
                    http.newCall(Request.Builder().url(info.url).build()).execute().use { r ->
                        if (!r.isSuccessful) error("HTTP ${r.code}")
                        val total = r.body.contentLength().takeIf { it > 0 } ?: 1L
                        r.body.byteStream().use { input ->
                            apk.outputStream().use { out ->
                                val buf = ByteArray(64 * 1024)
                                var done = 0L
                                var last = 0f
                                while (true) {
                                    val n = input.read(buf)
                                    if (n < 0) break
                                    out.write(buf, 0, n)
                                    done += n
                                    val p = (done.toFloat() / total).coerceIn(0f, 1f)
                                    if (p - last >= 0.01f) { last = p; state = UpdateState.Downloading(info, p) }
                                }
                            }
                        }
                    }
                    install(apk)
                }.isSuccess
            }
            state = if (ok) UpdateState.Installing(info) else UpdateState.Failed("ההורדה נכשלה – נסו שוב", info)
        }
    }

    private fun install(apk: File) {
        val installer = context.packageManager.packageInstaller
        val params = PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL)
        if (Build.VERSION.SDK_INT >= 31) params.setRequireUserAction(PackageInstaller.SessionParams.USER_ACTION_NOT_REQUIRED)
        val id = installer.createSession(params)
        installer.openSession(id).use { session ->
            session.openWrite("app.apk", 0, apk.length()).use { out ->
                apk.inputStream().use { it.copyTo(out) }
                session.fsync(out)
            }
            val flags = PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= 31) PendingIntent.FLAG_MUTABLE else 0)
            val status = PendingIntent.getBroadcast(context, id, Intent(ACTION_INSTALL_STATUS).setPackage(context.packageName), flags)
            session.commit(status.intentSender)
        }
    }

    /** The installer's answers: ask the user to confirm, or report a failure. */
    val statusReceiver = object : BroadcastReceiver() {
        override fun onReceive(c: Context, intent: Intent) {
            when (intent.getIntExtra(PackageInstaller.EXTRA_STATUS, PackageInstaller.STATUS_FAILURE)) {
                PackageInstaller.STATUS_PENDING_USER_ACTION -> {
                    @Suppress("DEPRECATION")
                    val confirm = intent.getParcelableExtra<Intent>(Intent.EXTRA_INTENT) ?: return
                    runCatching { c.startActivity(confirm.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
                        .onFailure { state = UpdateState.Failed("ההתקנה בוטלה או נכשלה", (state as? UpdateState.Installing)?.info) }
                }
                PackageInstaller.STATUS_SUCCESS -> Unit
                else -> {
                    val info = (state as? UpdateState.Installing)?.info
                    state = UpdateState.Failed("ההתקנה בוטלה או נכשלה", info)
                }
            }
        }
    }

    companion object {
        const val ACTION_INSTALL_STATUS = "com.israeltv.plus.PHONE_INSTALL_STATUS"
    }
}

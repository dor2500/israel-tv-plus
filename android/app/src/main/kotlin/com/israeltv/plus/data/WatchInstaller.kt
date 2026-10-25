package com.israeltv.plus.data

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.os.Build
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.net.Inet4Address

/** The watch app of the latest release (wear/ in the repository). */
const val WATCH_APK_URL = "https://github.com/dor2500/israel-tv-plus/releases/latest/download/IsraelTVPlus-wearos.apk"

@Immutable
sealed interface WatchState {
    data object Idle : WatchState
    data object Pairing : WatchState
    data object Connecting : WatchState
    /** The watch asks "Allow debugging from this phone?" (watches with the older "Debug over Wi-Fi"). */
    data object Allow : WatchState
    data class Downloading(val progress: Float) : WatchState
    data class Installing(val progress: Float) : WatchState
    data class Done(val watch: String) : WatchState
    data class Failed(val message: String) : WatchState
}

/** A watch found on the Wi-Fi network: its "pair with code" or its connect endpoint (Wireless debugging, mDNS). */
@Immutable
data class WatchEndpoint(val name: String, val host: String, val port: Int)

/**
 * Puts the Wear OS app on the user's watch from the phone, without Google Play: the watch's Wireless debugging
 * (Developer options), a six-digit pairing code once, then the APK of the latest release is installed with
 * `cmd package install` over adb (see Adb.kt). The paired watch is remembered, so updates are one tap.
 */
@Stable
class WatchInstaller(private val context: Context, private val http: OkHttpClient, private val scope: CoroutineScope) {
    var state by mutableStateOf<WatchState>(WatchState.Idle)
        private set
    /** Found on the network while the screen is open: watches waiting for a pairing code, and connect endpoints. */
    val pairing = mutableStateListOf<WatchEndpoint>()
    val connect = mutableStateListOf<WatchEndpoint>()

    private val prefs = context.getSharedPreferences("watch", Context.MODE_PRIVATE)
    val savedHost: String get() = prefs.getString("host", "").orEmpty()
    val savedPort: Int get() = prefs.getInt("port", 0)
    val savedName: String get() = prefs.getString("name", "").orEmpty()
    val supported: Boolean get() = Build.VERSION.SDK_INT >= 29

    private var job: Job? = null
    private val key by lazy { AdbKey.load(context.filesDir) }
    private val busy get() = state is WatchState.Pairing || state is WatchState.Connecting || state is WatchState.Allow ||
        state is WatchState.Downloading || state is WatchState.Installing

    fun reset() { if (!busy) state = WatchState.Idle }

    /** Pairs with the code shown on the watch, then installs. [connectPort] 0 = find it on the network. */
    fun pairAndInstall(host: String, pairPort: Int, code: String, connectPort: Int) {
        if (busy) return
        if (!supported) { state = WatchState.Failed("צריך אנדרואיד 10 ומעלה בטלפון"); return }
        job = scope.launch {
            state = WatchState.Pairing
            val name = withContext(Dispatchers.IO) {
                runCatching { adbPair(key, host, pairPort, code) }
            }.getOrElse {
                state = WatchState.Failed(
                    if (it.message == "wrong code") "קוד הצימוד לא נכון – נסו שוב עם הקוד שמוצג עכשיו בשעון"
                    else "לא הצלחנו להתחבר לשעון לצימוד. ודאו שהשעון והטלפון באותה רשת Wi-Fi ושחלון הקוד עדיין פתוח בשעון"
                )
                return@launch
            }
            prefs.edit { putString("host", host); putString("name", name) }
            install(host, connectPort)
        }
    }

    /** Installs (or updates) the watch app on the watch paired before. */
    fun installAgain(host: String = savedHost, port: Int = savedPort) {
        if (busy || host.isEmpty()) return
        job = scope.launch { install(host, port) }
    }

    private suspend fun install(host: String, givenPort: Int) {
        state = WatchState.Connecting
        // the connect port changes whenever Wireless debugging is turned on again: the one found on the network wins
        val port = connect.firstOrNull { it.host == host }?.port ?: givenPort
        if (port <= 0) {
            state = WatchState.Failed("לא נמצא חיבור לשעון. במסך \"ניפוי באגים אלחוטי\" בשעון מופיעים כתובת IP ויציאה – הקלידו אותם")
            return
        }
        val apk = File(context.cacheDir, "watch-app.apk")
        val result = withContext(Dispatchers.IO) {
            runCatching {
                AdbConnection.open(key, host, port) { state = WatchState.Allow }.use { adb ->
                    prefs.edit { putInt("port", port); if (adb.model.isNotEmpty()) putString("name", adb.model) }
                    state = WatchState.Downloading(0f)
                    download(apk)
                    state = WatchState.Installing(0f)
                    apk.inputStream().use { input -> adb.install(input, apk.length()) { state = WatchState.Installing(it) } }
                }
            }
        }
        apk.delete()
        state = result.fold(
            onSuccess = { out ->
                if ("Success" in out) WatchState.Done(savedName)
                else WatchState.Failed("השעון סירב להתקין: " + out.trim().take(160))
            },
            onFailure = {
                when (state) {
                    is WatchState.Downloading -> WatchState.Failed("ההורדה מ-GitHub נכשלה – בדקו את החיבור לאינטרנט ונסו שוב")
                    is WatchState.Allow -> WatchState.Failed("השעון לא אישר את החיבור. לחצו \"אישור\" בשעון ונסו שוב")
                    else -> WatchState.Failed(
                        "לא הצלחנו להתחבר לשעון ($host:$port). ודאו ש\"ניפוי באגים אלחוטי\" פועל בשעון ושהוא באותה רשת Wi-Fi"
                    )
                }
            },
        )
    }

    private fun download(apk: File) {
        http.newCall(Request.Builder().url(WATCH_APK_URL).build()).execute().use { r ->
            if (!r.isSuccessful) throw AdbException("HTTP ${r.code}")
            val total = r.body.contentLength().takeIf { it > 0 } ?: 1L
            r.body.byteStream().use { input ->
                apk.outputStream().use { out ->
                    val buf = ByteArray(64 * 1024)
                    var done = 0L
                    while (true) {
                        val n = input.read(buf)
                        if (n < 0) break
                        out.write(buf, 0, n)
                        done += n
                        state = WatchState.Downloading((done.toFloat() / total).coerceIn(0f, 1f))
                    }
                }
            }
        }
    }

    // ---------------------------------------------------------------- finding the watch on the Wi-Fi (mDNS)

    private val nsd by lazy { context.getSystemService(Context.NSD_SERVICE) as NsdManager }
    private val listeners = mutableListOf<NsdManager.DiscoveryListener>()
    private val resolveQueue = ArrayDeque<Pair<NsdServiceInfo, Boolean>>()
    private var resolving = false

    /** Watches in Wireless debugging announce themselves (_adb-tls-pairing / _adb-tls-connect): fill in what we can. */
    fun startDiscovery() {
        if (listeners.isNotEmpty()) return
        for ((type, isPairing) in listOf("_adb-tls-pairing._tcp" to true, "_adb-tls-connect._tcp" to false)) {
            val l = object : NsdManager.DiscoveryListener {
                override fun onDiscoveryStarted(serviceType: String) = Unit
                override fun onDiscoveryStopped(serviceType: String) = Unit
                override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) = Unit
                override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) = Unit
                override fun onServiceFound(info: NsdServiceInfo) = scope.launch { enqueue(info, isPairing) }.let { }
                override fun onServiceLost(info: NsdServiceInfo) = scope.launch {
                    (if (isPairing) pairing else connect).removeAll { it.name == info.serviceName }
                }.let { }
            }
            runCatching { nsd.discoverServices(type, NsdManager.PROTOCOL_DNS_SD, l); listeners += l }
        }
    }

    fun stopDiscovery() {
        listeners.forEach { runCatching { nsd.stopServiceDiscovery(it) } }
        listeners.clear()
        resolveQueue.clear()
    }

    private fun enqueue(info: NsdServiceInfo, isPairing: Boolean) {
        resolveQueue.addLast(info to isPairing)
        resolveNext()
    }

    /** One resolve at a time (older Android versions refuse a second one while the first runs). */
    @Suppress("DEPRECATION")
    private fun resolveNext() {
        if (resolving) return
        val (info, isPairing) = resolveQueue.removeFirstOrNull() ?: return
        resolving = true
        runCatching {
            nsd.resolveService(info, object : NsdManager.ResolveListener {
                override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                    scope.launch { resolving = false; resolveNext() }
                }
                override fun onServiceResolved(s: NsdServiceInfo) {
                    scope.launch {
                        resolving = false
                        val host = s.host
                        if (host is Inet4Address && !host.isLoopbackAddress) {
                            val list = if (isPairing) pairing else connect
                            list.removeAll { it.name == s.serviceName }
                            list += WatchEndpoint(s.serviceName, host.hostAddress.orEmpty(), s.port)
                        }
                        resolveNext()
                    }
                }
            })
        }.onFailure { resolving = false }
    }
}

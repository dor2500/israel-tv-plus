package com.israeltv.plus.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.net.wifi.WifiManager
import android.provider.Settings
import android.util.Base64
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.mediarouter.media.MediaControlIntent
import androidx.mediarouter.media.MediaRouteSelector
import androidx.mediarouter.media.MediaRouter
import com.google.android.gms.cast.CastMediaControlIntent
import com.google.android.gms.cast.HlsSegmentFormat
import com.google.android.gms.cast.HlsVideoSegmentFormat
import com.google.android.gms.cast.MediaError
import com.google.android.gms.cast.MediaInfo
import com.google.android.gms.cast.MediaLoadRequestData
import com.google.android.gms.cast.MediaMetadata
import com.google.android.gms.cast.MediaStatus
import com.google.android.gms.cast.framework.CastContext
import com.google.android.gms.cast.framework.CastOptions
import com.google.android.gms.cast.framework.CastSession
import com.google.android.gms.cast.framework.OptionsProvider
import com.google.android.gms.cast.framework.SessionManagerListener
import com.google.android.gms.cast.framework.media.RemoteMediaClient
import com.google.android.gms.common.images.WebImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.HttpURLConnection
import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketTimeoutException
import java.net.URI
import java.net.URL
import java.security.MessageDigest
import kotlin.concurrent.thread

/**
 * Registered in AndroidManifest.xml so Google Cast SDK initializes with the standard
 * Chromecast Default Media Receiver (CC1AD845), which supports HLS (.m3u8) and Icecast/MP3/AAC live streams.
 */
class CastOptionsProvider : OptionsProvider {
    override fun getCastOptions(context: Context): CastOptions =
        CastOptions.Builder()
            .setReceiverApplicationId(CastMediaControlIntent.DEFAULT_MEDIA_RECEIVER_APPLICATION_ID)
            .setResumeSavedSession(true)
            .setStopReceiverApplicationWhenEndingSession(true)
            .build()

    override fun getAdditionalSessionProviders(context: Context) = null
}

enum class CastProtocol(val labelHe: String, val badgeHe: String) {
    SMART_TV_APP("שידור ישיר לאפליקציית ישראל TV+ בטלוויזיה", "אפליקציית TV+"),
    GOOGLE_CAST("שידור ישיר (Google Cast / Chromecast / Android TV)", "Google Cast"),
    DLNA("שידור ישיר ברשת (LG webOS / Samsung / DLNA)", "DLNA / Smart TV"),
    MIRACAST_ROUTE("שיקוף אלחוטי ישיר לטלוויזיה (Wireless Display)", "שיקוף ישיר"),
}

data class CastDeviceItem(
    val id: String,
    val name: String,
    val description: String,
    val protocol: CastProtocol,
    val routeId: String? = null,
    val dlnaControlUrl: String? = null,
    val castTopic: String? = null,
)

fun Channel.remoteLogoUrl(): String = when {
    logo.isEmpty() -> REMOTE + "assets/app/icon-512.png"
    logo.startsWith("http") -> logo
    else -> REMOTE + logo.removePrefix("/")
}

/**
 * Lightweight local LAN HTTP proxy server that runs on the phone while casting to a Chromecast or DLNA TV.
 * Solves all major Smart TV / Chromecast failure causes:
 *  - Follows HTTP 301/302 redirects (e.g. ottrast.com single-IP tokens) on the phone
 *  - Injects the required User-Agent and Mako entitlement tickets
 *  - Injects `Access-Control-Allow-Origin: *` CORS headers required by Chromecast CAF player
 *  - Rewrites relative/sub-playlist/segment URLs in `.m3u8` manifests so the TV fetches everything via the phone
 */
object LocalCastProxyServer {
    @Volatile
    private var serverSocket: ServerSocket? = null
    @Volatile
    private var running = false

    val port: Int
        get() = serverSocket?.localPort ?: 0

    @Synchronized
    fun ensureStarted(): Int {
        if (running && serverSocket != null && !serverSocket!!.isClosed) {
            return serverSocket!!.localPort
        }
        return runCatching {
            val ss = ServerSocket(0)
            serverSocket = ss
            running = true
            thread(start = true, isDaemon = true, name = "ItvLocalCastProxy") {
                while (running && !ss.isClosed) {
                    val client = runCatching { ss.accept() }.getOrNull() ?: break
                    thread(start = true, isDaemon = true) {
                        handleClient(client)
                    }
                }
            }
            ss.localPort
        }.getOrDefault(0)
    }

    fun getLanIp(context: Context): String? {
        runCatching {
            val wm = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
            val ipInt = wm?.connectionInfo?.ipAddress ?: 0
            if (ipInt != 0) {
                val b1 = ipInt and 0xff
                val b2 = (ipInt shr 8) and 0xff
                val b3 = (ipInt shr 16) and 0xff
                val b4 = (ipInt shr 24) and 0xff
                val candidate = "$b1.$b2.$b3.$b4"
                if (candidate != "0.0.0.0" && !candidate.startsWith("127.")) return candidate
            }
        }
        runCatching {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val nif = interfaces.nextElement()
                if (!nif.isUp || nif.isLoopback) continue
                val addrs = nif.inetAddresses
                while (addrs.hasMoreElements()) {
                    val addr = addrs.nextElement()
                    if (addr is Inet4Address && !addr.isLoopbackAddress) {
                        val ip = addr.hostAddress ?: continue
                        if (ip.startsWith("192.168.") || ip.startsWith("10.") || ip.startsWith("172.")) {
                            return ip
                        }
                    }
                }
            }
        }
        return null
    }

    fun buildProxyUrl(context: Context, targetUrl: String, auth: String = "", isRadio: Boolean = false): String? {
        val ip = getLanIp(context) ?: return null
        val p = ensureStarted()
        if (p <= 0) return null
        val enc = Base64.encodeToString(targetUrl.toByteArray(Charsets.UTF_8), Base64.URL_SAFE or Base64.NO_WRAP)
        val ext = if (isRadio) "stream.mp3" else "live.m3u8"
        return "http://$ip:$p/$ext?u=$enc&auth=${Uri.encode(auth)}"
    }

    private fun handleClient(socket: Socket) {
        socket.use { s ->
            s.soTimeout = 15_000
            val reader = BufferedReader(InputStreamReader(s.getInputStream(), Charsets.UTF_8))
            val requestLine = reader.readLine() ?: return
            val parts = requestLine.split(" ")
            if (parts.size < 2) return
            val method = parts[0].uppercase()
            val fullPath = parts[1]

            // Drain headers
            while (true) {
                val line = reader.readLine()
                if (line.isNullOrEmpty()) break
            }

            val out = s.getOutputStream()
            if (method == "OPTIONS") {
                writeHeaders(out, "200 OK", "text/plain", 0)
                return
            }

            val uri = Uri.parse("http://localhost$fullPath")
            val encodedUrl = uri.getQueryParameter("u") ?: run {
                writeHeaders(out, "400 Bad Request", "text/plain", 0)
                return
            }
            val auth = uri.getQueryParameter("auth").orEmpty()
            val rawTarget = String(Base64.decode(encodedUrl, Base64.URL_SAFE), Charsets.UTF_8)
            val resolvedTarget = runCatching {
                runBlocking { playableUrl(rawTarget, auth) }
            }.getOrDefault(rawTarget)

            val isPlaylistRequest = uri.path?.endsWith(".m3u8", ignoreCase = true) == true ||
                resolvedTarget.contains(".m3u8", ignoreCase = true)

            val conn = openFollowingRedirects(resolvedTarget) ?: run {
                writeHeaders(out, "502 Bad Gateway", "text/plain", 0)
                return
            }

            val finalUrl = conn.url.toString()
            val contentTypeHeader = conn.contentType.orEmpty()

            if (isPlaylistRequest && ! uri.path.orEmpty().startsWith("/seg")) {
                val bodyText = runCatching { conn.inputStream.bufferedReader().use { it.readText() } }.getOrNull()
                if (bodyText != null && bodyText.trimStart().startsWith("#EXTM3U")) {
                    val hostPort = s.localAddress.hostAddress + ":" + s.localPort
                    val rewritten = rewriteM3u8(bodyText, finalUrl, hostPort)
                    val bytes = rewritten.toByteArray(Charsets.UTF_8)
                    writeHeaders(out, "200 OK", "application/vnd.apple.mpegurl", bytes.size.toLong())
                    if (method != "HEAD") {
                        out.write(bytes)
                        out.flush()
                    }
                    return
                }
            }

            // Binary segment / audio stream pass-through
            val mime = when {
                contentTypeHeader.isNotBlank() -> contentTypeHeader
                finalUrl.endsWith(".ts", true) -> "video/mp2t"
                finalUrl.endsWith(".mp3", true) -> "audio/mpeg"
                else -> "application/octet-stream"
            }
            writeHeaders(out, "200 OK", mime, -1)
            if (method != "HEAD") {
                conn.inputStream.use { input ->
                    val buf = ByteArray(32 * 1024)
                    while (true) {
                        val n = input.read(buf)
                        if (n <= 0) break
                        out.write(buf, 0, n)
                    }
                    out.flush()
                }
            }
        }
    }

    private fun openFollowingRedirects(startUrl: String): HttpURLConnection? {
        var current = startUrl
        repeat(6) {
            val conn = (URL(current).openConnection() as HttpURLConnection).apply {
                instanceFollowRedirects = false
                connectTimeout = 10_000
                readTimeout = 14_000
                setRequestProperty("User-Agent", USER_AGENT)
                setRequestProperty("Accept", "*/*")
            }
            val code = runCatching { conn.responseCode }.getOrDefault(-1)
            if (code in 300..399) {
                val loc = conn.getHeaderField("Location") ?: return null
                current = runCatching { URI(current).resolve(loc).toString() }.getOrDefault(loc)
                conn.disconnect()
            } else if (code in 200..299) {
                return conn
            } else {
                conn.disconnect()
                return null
            }
        }
        return null
    }

    private fun rewriteM3u8(playlist: String, baseUrl: String, hostPort: String): String {
        val baseUri = runCatching { URI(baseUrl) }.getOrNull()
        val lines = playlist.lines()
        val sb = StringBuilder(playlist.length + 1024)
        for (rawLine in lines) {
            val line = rawLine.trim()
            if (line.isEmpty()) {
                sb.append("\n")
                continue
            }
            if (line.startsWith("#")) {
                // Rewrite URI="..." inside #EXT-X-KEY, #EXT-X-MAP, #EXT-X-MEDIA
                val rewrittenTag = Regex("""URI="([^"]+)"""").replace(line) { m ->
                    val rawRef = m.groupValues[1]
                    val abs = resolveUri(baseUri, rawRef)
                    val isSubM3u8 = abs.contains(".m3u8", ignoreCase = true)
                    val proxyEndpoint = if (isSubM3u8) "live.m3u8" else "seg"
                    val enc = Base64.encodeToString(abs.toByteArray(Charsets.UTF_8), Base64.URL_SAFE or Base64.NO_WRAP)
                    """URI="http://$hostPort/$proxyEndpoint?u=$enc""""
                }
                sb.append(rewrittenTag).append("\n")
            } else {
                val abs = resolveUri(baseUri, line)
                val isSubM3u8 = abs.contains(".m3u8", ignoreCase = true)
                val proxyEndpoint = if (isSubM3u8) "live.m3u8" else "seg"
                val enc = Base64.encodeToString(abs.toByteArray(Charsets.UTF_8), Base64.URL_SAFE or Base64.NO_WRAP)
                sb.append("http://$hostPort/$proxyEndpoint?u=$enc\n")
            }
        }
        return sb.toString()
    }

    private fun resolveUri(baseUri: URI?, ref: String): String {
        if (ref.startsWith("http://", true) || ref.startsWith("https://", true)) return ref
        if (baseUri == null) return ref
        return runCatching { baseUri.resolve(ref).toString() }.getOrDefault(ref)
    }

    private fun writeHeaders(out: OutputStream, status: String, contentType: String, contentLength: Long) {
        val headers = buildString {
            append("HTTP/1.1 $status\r\n")
            append("Content-Type: $contentType\r\n")
            if (contentLength >= 0) append("Content-Length: $contentLength\r\n")
            append("Access-Control-Allow-Origin: *\r\n")
            append("Access-Control-Allow-Methods: GET, HEAD, OPTIONS\r\n")
            append("Access-Control-Allow-Headers: *\r\n")
            append("Cache-Control: no-cache, no-store\r\n")
            append("Connection: close\r\n\r\n")
        }
        out.write(headers.toByteArray(Charsets.UTF_8))
        out.flush()
    }
}

/**
 * Manages discovery and playback handoff for:
 *  1. Direct Cast to the Israel TV+ Smart TV app (Android TV / LG webOS / Samsung Tizen / PC) on the same LAN or Google account
 *  2. Google Cast / Chromecast / Google TV / Android TV (via MediaRouter + CastContext + LocalCastProxyServer fallback)
 *  3. LG webOS / Samsung / Smart TV DLNA / UPnP AV MediaRenderers on the local Wi-Fi (via SSDP + SOAP AVTransport + LocalCastProxyServer)
 *  4. Android System Screen Mirroring (Miracast / LG Screen Share / Smart View) & external caster apps
 */
@Stable
class CastController(
    private val context: Context,
    private val scope: CoroutineScope,
) {
    private val appContext = context.applicationContext

    var devices by mutableStateOf<List<CastDeviceItem>>(emptyList())
        private set
    var isScanning by mutableStateOf(false)
        private set
    var connectedDevice by mutableStateOf<CastDeviceItem?>(null)
        private set
    var isConnecting by mutableStateOf(false)
        private set
    var isCasting by mutableStateOf(false)
        private set
    var remotePlaying by mutableStateOf(false)
        private set
    var statusMessage by mutableStateOf<String?>(null)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set
    var activeChannelId by mutableStateOf<String?>(null)
        private set

    private var castRoutes = emptyList<CastDeviceItem>()
    private var dlnaDevices = emptyList<CastDeviceItem>()
    private var smartTvAppDevices = emptyList<CastDeviceItem>()

    private var pendingChannel: Channel? = null
    private var currentCandidates = emptyList<String>()
    private var currentCandidateIndex = 0
    private var dlnaScanJob: Job? = null
    @Volatile
    private var cachedLanTopic: String? = null

    private val mediaRouter: MediaRouter? = runCatching { MediaRouter.getInstance(appContext) }.getOrNull()
    private val castContext: CastContext? = runCatching { CastContext.getSharedInstance(appContext) }.getOrNull()

    private val castSelector: MediaRouteSelector? = runCatching {
        MediaRouteSelector.Builder()
            .addControlCategory(CastMediaControlIntent.categoryForCast(CastMediaControlIntent.DEFAULT_MEDIA_RECEIVER_APPLICATION_ID))
            .addControlCategory(MediaControlIntent.CATEGORY_REMOTE_PLAYBACK)
            .build()
    }.getOrNull()

    private val allRouteSelector: MediaRouteSelector? = runCatching {
        MediaRouteSelector.Builder()
            .addControlCategory(CastMediaControlIntent.categoryForCast(CastMediaControlIntent.DEFAULT_MEDIA_RECEIVER_APPLICATION_ID))
            .addControlCategory(MediaControlIntent.CATEGORY_REMOTE_PLAYBACK)
            .addControlCategory(MediaControlIntent.CATEGORY_LIVE_VIDEO)
            .build()
    }.getOrNull()

    private val remoteClientCallback = object : RemoteMediaClient.Callback() {
        override fun onStatusUpdated() {
            val client = castContext?.sessionManager?.currentCastSession?.remoteMediaClient ?: return
            val status = client.mediaStatus ?: return
            when (status.playerState) {
                MediaStatus.PLAYER_STATE_PLAYING,
                MediaStatus.PLAYER_STATE_BUFFERING -> {
                    isConnecting = false
                    isCasting = true
                    remotePlaying = true
                    errorMessage = null
                    connectedDevice?.let { dev ->
                        statusMessage = if (status.playerState == MediaStatus.PLAYER_STATE_BUFFERING) {
                            "טוען שידור בטלוויזיה (${dev.name})…"
                        } else {
                            "משדר כעת אל ${dev.name}"
                        }
                    }
                }
                MediaStatus.PLAYER_STATE_PAUSED -> {
                    isConnecting = false
                    isCasting = true
                    remotePlaying = false
                    connectedDevice?.let { dev ->
                        statusMessage = "מושהה בטלוויזיה (${dev.name})"
                    }
                }
                MediaStatus.PLAYER_STATE_IDLE -> {
                    if (status.idleReason == MediaStatus.IDLE_REASON_ERROR) {
                        tryNextCastCandidateOrFail("הטלוויזיה לא הצליחה לפענח את מקור השידור הנוכחי")
                    }
                }
            }
        }

        override fun onMediaError(error: MediaError) {
            tryNextCastCandidateOrFail(error.reason ?: "שגיאת טעינת מדיה בטלוויזיה")
        }
    }

    private val routerCallback = object : MediaRouter.Callback() {
        override fun onRouteAdded(router: MediaRouter, route: MediaRouter.RouteInfo) = refreshCastRoutes()
        override fun onRouteChanged(router: MediaRouter, route: MediaRouter.RouteInfo) = refreshCastRoutes()
        override fun onRouteRemoved(router: MediaRouter, route: MediaRouter.RouteInfo) = refreshCastRoutes()
    }

    private val sessionListener = object : SessionManagerListener<CastSession> {
        override fun onSessionStarting(session: CastSession) {
            isConnecting = true
            errorMessage = null
            val name = session.castDevice?.friendlyName ?: connectedDevice?.name ?: "Chromecast / TV"
            statusMessage = "מתחבר אל $name…"
        }

        override fun onSessionStarted(session: CastSession, sessionId: String) {
            onCastSessionReady(session)
        }

        override fun onSessionStartFailed(session: CastSession, error: Int) {
            isConnecting = false
            isCasting = false
            remotePlaying = false
            statusMessage = null
            errorMessage = "ההתחברות אל הטלוויזיה נכשלה (קוד $error). ניתן לבחור בשידור ישיר לאפליקציית ישראל TV+ בטלוויזיה או בשיקוף מסך."
        }

        override fun onSessionEnding(session: CastSession) {}

        override fun onSessionEnded(session: CastSession, error: Int) {
            session.remoteMediaClient?.unregisterCallback(remoteClientCallback)
            if (connectedDevice?.protocol == CastProtocol.GOOGLE_CAST) {
                connectedDevice = null
                isConnecting = false
                isCasting = false
                remotePlaying = false
                statusMessage = null
            }
        }

        override fun onSessionResuming(session: CastSession, sessionId: String) {
            isConnecting = true
        }

        override fun onSessionResumed(session: CastSession, wasSuspended: Boolean) {
            onCastSessionReady(session)
        }

        override fun onSessionResumeFailed(session: CastSession, error: Int) {
            isConnecting = false
        }

        override fun onSessionSuspended(session: CastSession, reason: Int) {
            statusMessage = "החיבור לטלוויזיה הושהה זמנית…"
        }
    }

    init {
        LocalCastProxyServer.ensureStarted()
        buildSmartTvAppEntries()
        runCatching {
            val selector = allRouteSelector
            if (mediaRouter != null && selector != null) {
                mediaRouter.addCallback(selector, routerCallback, MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY)
                refreshCastRoutes()
            }
            castContext?.sessionManager?.addSessionManagerListener(sessionListener, CastSession::class.java)
            castContext?.sessionManager?.currentCastSession?.let { existing ->
                if (existing.isConnected) onCastSessionReady(existing)
            }
        }
    }

    private fun buildSmartTvAppEntries(lanTopic: String? = cachedLanTopic) {
        val account = runCatching { AppPrefs(appContext).googleUser }.getOrNull()
        val pairCode = account?.let { GoogleSync.shortPairingCode(it.email) }.orEmpty()
        val list = mutableListOf<CastDeviceItem>()
        if (account != null && pairCode.isNotBlank()) {
            list += CastDeviceItem(
                id = "smart_tv_sync:$pairCode",
                name = "טלוויזיה מחוברת לחשבון (${account.displayName})",
                description = "שידור ישיר לאפליקציית ישראל TV+ הפתוחה בטלוויזיה • 100% תאימות ערוצים",
                protocol = CastProtocol.SMART_TV_APP,
                castTopic = "itv_cast_$pairCode",
            )
        }
        list += CastDeviceItem(
            id = "smart_tv_lan",
            name = "אפליקציית ישראל TV+ בטלוויזיה (ברשת הביתית)",
            description = "מעביר מיד את הערוץ לטלוויזיה שבה פתוחה אפליקציית ישראל TV+ באותו Wi-Fi",
            protocol = CastProtocol.SMART_TV_APP,
            castTopic = lanTopic,
        )
        smartTvAppDevices = list
        mergeDevices()
    }

    private fun onCastSessionReady(session: CastSession) {
        val devName = session.castDevice?.friendlyName ?: connectedDevice?.name ?: "Google TV / Chromecast"
        val devId = session.castDevice?.deviceId ?: "cast_active"
        connectedDevice = CastDeviceItem(
            id = devId,
            name = devName,
            description = session.castDevice?.modelName ?: "Google Cast",
            protocol = CastProtocol.GOOGLE_CAST,
        )
        isConnecting = false
        isCasting = true
        errorMessage = null
        statusMessage = "מחובר אל $devName"
        session.remoteMediaClient?.registerCallback(remoteClientCallback)
        pendingChannel?.let { ch ->
            loadChannel(ch)
        }
    }

    /** Starts an active scan for Google Cast / Android TV routes, Smart TV app receivers, and LG / Samsung DLNA TVs. */
    fun startDiscovery(currentChannel: Channel? = null) {
        if (currentChannel != null) pendingChannel = currentChannel
        errorMessage = null
        LocalCastProxyServer.ensureStarted()
        buildSmartTvAppEntries()
        runCatching {
            val selector = allRouteSelector
            if (mediaRouter != null && selector != null) {
                mediaRouter.removeCallback(routerCallback)
                mediaRouter.addCallback(
                    selector,
                    routerCallback,
                    MediaRouter.CALLBACK_FLAG_PERFORM_ACTIVE_SCAN or MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY,
                )
                refreshCastRoutes()
            }
        }
        dlnaScanJob?.cancel()
        dlnaScanJob = scope.launch {
            isScanning = true
            val topic = resolveHomeLanCastTopic()
            if (topic != null) {
                cachedLanTopic = topic
                buildSmartTvAppEntries(topic)
            }
            val foundDlna = discoverDlnaDevices()
            dlnaDevices = foundDlna
            mergeDevices()
            isScanning = false
        }
    }

    fun dispose() {
        dlnaScanJob?.cancel()
        runCatching {
            mediaRouter?.removeCallback(routerCallback)
            castContext?.sessionManager?.removeSessionManagerListener(sessionListener, CastSession::class.java)
            castContext?.sessionManager?.currentCastSession?.remoteMediaClient?.unregisterCallback(remoteClientCallback)
        }
    }

    private fun refreshCastRoutes() {
        val router = mediaRouter ?: return
        val cSelector = castSelector
        val aSelector = allRouteSelector ?: return
        val list = router.routes.mapNotNull { route ->
            if (route.isDefault || !route.isEnabled || route.isBluetooth) return@mapNotNull null
            if (!route.matchesSelector(aSelector)) return@mapNotNull null
            val name = route.name.toString().trim()
            if (name.isEmpty() || name.contains("Phone", ignoreCase = true) || name.contains("טלפון")) return@mapNotNull null
            val isGoogleCast = cSelector != null && route.matchesSelector(cSelector)
            CastDeviceItem(
                id = "route:${route.id}",
                name = name,
                description = route.description?.toString()?.takeIf { it.isNotBlank() }
                    ?: if (isGoogleCast) "Google Cast / Chromecast / Android TV" else "שיקוף אלחוטי ישיר (Wireless Display)",
                protocol = if (isGoogleCast) CastProtocol.GOOGLE_CAST else CastProtocol.MIRACAST_ROUTE,
                routeId = route.id,
            )
        }.distinctBy { "${it.protocol}:${it.name.lowercase()}" }
        castRoutes = list
        mergeDevices()
    }

    private fun mergeDevices() {
        val castNames = castRoutes.filter { it.protocol == CastProtocol.GOOGLE_CAST }.map { it.name.lowercase() }.toSet()
        devices = smartTvAppDevices + castRoutes + dlnaDevices.filter { dlna ->
            dlna.name.lowercase() !in castNames || dlna.protocol == CastProtocol.DLNA
        }
    }

    /** Connects to the chosen TV receiver and immediately casts [channel]. */
    fun connectAndCast(device: CastDeviceItem, channel: Channel) {
        pendingChannel = channel
        errorMessage = null
        when (device.protocol) {
            CastProtocol.SMART_TV_APP -> {
                connectedDevice = device
                isConnecting = true
                statusMessage = "שולח את ${channel.name} אל ${device.name}…"
                loadChannel(channel)
            }
            CastProtocol.GOOGLE_CAST -> {
                val router = mediaRouter
                val route = router?.routes?.firstOrNull { it.id == device.routeId }
                val activeSession = castContext?.sessionManager?.currentCastSession
                if (activeSession != null && activeSession.isConnected && connectedDevice?.id == device.id) {
                    loadChannel(channel)
                } else if (router != null && route != null) {
                    connectedDevice = device
                    isConnecting = true
                    statusMessage = "מתחבר אל ${device.name}…"
                    router.selectRoute(route)
                } else {
                    errorMessage = "המכשיר ${device.name} אינו זמין כעת ברשת."
                }
            }
            CastProtocol.MIRACAST_ROUTE -> {
                val router = mediaRouter
                val route = router?.routes?.firstOrNull { it.id == device.routeId }
                if (router != null && route != null) {
                    router.selectRoute(route)
                    statusMessage = "מפעיל שיקוף אלחוטי אל ${device.name}…"
                } else {
                    openSystemMirroring()
                }
            }
            CastProtocol.DLNA -> {
                connectedDevice = device
                isConnecting = true
                statusMessage = "שולח שידור אל ${device.name}…"
                loadChannel(channel)
            }
        }
    }

    /** Sends [channel] to the currently connected TV receiver (Smart TV App, Google Cast, or DLNA). */
    fun loadChannel(channel: Channel) {
        pendingChannel = channel
        activeChannelId = channel.id
        errorMessage = null
        val dev = connectedDevice ?: return

        when (dev.protocol) {
            CastProtocol.SMART_TV_APP -> {
                scope.launch {
                    isConnecting = true
                    statusMessage = "מעביר לערוץ ${channel.name} בטלוויזיה…"
                    val ok = sendSmartTvAppCommand(
                        device = dev,
                        action = "cast_channel",
                        channel = channel,
                    )
                    if (ok) {
                        isConnecting = false
                        isCasting = true
                        remotePlaying = true
                        errorMessage = null
                        statusMessage = "משדר כעת את ${channel.name} באפליקציית ישראל TV+ בטלוויזיה"
                    } else {
                        isConnecting = false
                        errorMessage = "לא הצלחנו לשלוח פקודה לטלוויזיה. ודא שיש חיבור אינטרנט פעיל ושאפליקציית ישראל TV+ פתוחה בטלוויזיה."
                    }
                }
            }
            CastProtocol.GOOGLE_CAST -> {
                currentCandidates = buildGoogleCastCandidates(channel)
                currentCandidateIndex = 0
                loadCurrentGoogleCastCandidate(channel)
            }
            CastProtocol.DLNA -> {
                currentCandidates = buildDlnaCandidates(channel)
                currentCandidateIndex = 0
                loadCurrentDlnaCandidate(dev, channel)
            }
            CastProtocol.MIRACAST_ROUTE -> {}
        }
    }

    /**
     * Builds an ordered list of stream URLs optimized for Chromecast Default Media Receiver:
     *  1. Clean MPEG-TS HTTPS URLs (stripping FreeTV `?fmp4&renditions` first so Chromecast's HLS parser succeeds)
     *  2. Original HTTPS URLs
     *  3. Local LAN Proxy URLs (`http://<phone-ip>:<port>/live.m3u8?...`) which add CORS headers and handle HTTP/302 streams (`ottrast.com`, `mcquack.net`)
     */
    private fun buildGoogleCastCandidates(channel: Channel): List<String> {
        val rawList = channel.allCandidateUrls()
        val result = mutableListOf<String>()
        for (u in rawList) {
            if (u.startsWith("https://", ignoreCase = true)) {
                if (u.contains("?fmp4&renditions", ignoreCase = true)) {
                    result += u.replace("?fmp4&renditions", "", ignoreCase = true)
                }
                result += u
            }
        }
        // Add LocalCastProxyServer URLs for every candidate (solves CORS & HTTP mixed-content/redirect issues)
        for (u in rawList) {
            val clean = u.replace("?fmp4&renditions", "", ignoreCase = true)
            LocalCastProxyServer.buildProxyUrl(appContext, clean, channel.auth, channel.isRadio)?.let { result += it }
        }
        for (u in rawList) {
            if (!u.startsWith("https://", ignoreCase = true)) {
                result += u
            }
        }
        return result.distinct()
    }

    /**
     * Builds an ordered list of stream URLs optimized for LG webOS / Samsung DLNA MediaRenderers:
     *  1. Local LAN Proxy URLs (`http://<phone-ip>:<port>/live.m3u8?...`) first because DLNA renderers fail on 302 redirects and custom User-Agents
     *  2. Clean MPEG-TS direct URLs (without `?fmp4&renditions`)
     */
    private fun buildDlnaCandidates(channel: Channel): List<String> {
        val rawList = channel.allCandidateUrls()
        val result = mutableListOf<String>()
        for (u in rawList) {
            val clean = u.replace("?fmp4&renditions", "", ignoreCase = true)
            LocalCastProxyServer.buildProxyUrl(appContext, clean, channel.auth, channel.isRadio)?.let { result += it }
        }
        for (u in rawList) {
            if (u.contains("?fmp4&renditions", ignoreCase = true)) {
                result += u.replace("?fmp4&renditions", "", ignoreCase = true)
            }
            result += u
        }
        return result.distinct()
    }

    private fun loadCurrentGoogleCastCandidate(channel: Channel) {
        val candidateRaw = currentCandidates.getOrNull(currentCandidateIndex) ?: run {
            isConnecting = false
            errorMessage = "מקלט ה-Chromecast דחה את הפורמט של ${channel.name}. בחר ב-״אפליקציית ישראל TV+ בטלוויזיה״ או ב-״שיקוף מסך״."
            return
        }
        val session = castContext?.sessionManager?.currentCastSession ?: return
        val client = session.remoteMediaClient ?: return

        scope.launch {
            isConnecting = true
            statusMessage = "טוען את ${channel.name} בטלוויזיה…"
            val isLocalProxy = candidateRaw.startsWith("http://192.168.") ||
                candidateRaw.startsWith("http://10.") ||
                candidateRaw.startsWith("http://172.")
            val resolvedUrl = if (isLocalProxy) {
                candidateRaw
            } else {
                runCatching { playableUrl(candidateRaw, channel.auth) }.getOrDefault(candidateRaw)
            }
            val contentType = when {
                channel.isRadio && (resolvedUrl.endsWith(".mp3", true) || !resolvedUrl.contains(".m3u8", true)) -> "audio/mpeg"
                else -> "application/x-mpegurl"
            }
            val metadata = MediaMetadata(
                if (channel.isRadio) MediaMetadata.MEDIA_TYPE_MUSIC_TRACK else MediaMetadata.MEDIA_TYPE_TV_SHOW
            ).apply {
                putString(MediaMetadata.KEY_TITLE, channel.name)
                putString(MediaMetadata.KEY_SUBTITLE, channel.subtitle().ifEmpty { "ישראל TV+ • שידור חי" })
                addImage(WebImage(Uri.parse(channel.remoteLogoUrl())))
            }
            val mediaInfoBuilder = MediaInfo.Builder(resolvedUrl)
                .setStreamType(MediaInfo.STREAM_TYPE_LIVE)
                .setContentType(contentType)
                .setMetadata(metadata)
            if (resolvedUrl.contains("fmp4", ignoreCase = true)) {
                mediaInfoBuilder
                    .setHlsSegmentFormat(HlsSegmentFormat.FMP4)
                    .setHlsVideoSegmentFormat(HlsVideoSegmentFormat.FMP4)
            }
            val req = MediaLoadRequestData.Builder()
                .setMediaInfo(mediaInfoBuilder.build())
                .setAutoplay(true)
                .build()

            client.load(req).setResultCallback { result ->
                if (!result.status.isSuccess) {
                    tryNextCastCandidateOrFail("הערוץ דורש חיבור ישיר לאפליקציית TV+ או שיקוף מסך")
                } else {
                    isConnecting = false
                    isCasting = true
                    remotePlaying = true
                    connectedDevice?.let { dev ->
                        statusMessage = "משדר כעת את ${channel.name} אל ${dev.name}"
                    }
                }
            }
        }
    }

    private fun tryNextCastCandidateOrFail(reasonHe: String) {
        val ch = pendingChannel ?: return
        if (currentCandidateIndex + 1 < currentCandidates.size) {
            currentCandidateIndex += 1
            loadCurrentGoogleCastCandidate(ch)
        } else {
            isConnecting = false
            errorMessage = "$reasonHe (${ch.name}). מומלץ לבחור ב-״אפליקציית ישראל TV+ בטלוויזיה״ או ב-״שיקוף מסך״."
        }
    }

    private fun loadCurrentDlnaCandidate(device: CastDeviceItem, channel: Channel) {
        val controlUrl = device.dlnaControlUrl ?: run {
            isConnecting = false
            errorMessage = "כתובת שליטה של הטלוויזיה חסרה."
            return
        }
        val candidateRaw = currentCandidates.getOrNull(currentCandidateIndex) ?: run {
            isConnecting = false
            errorMessage = "הטלוויזיה ${device.name} לא הצליחה לנגן את הערוץ ישירות ב-DLNA. בחר ב-״אפליקציית ישראל TV+ בטלוויזיה״ או ב-״שיקוף מסך״."
            return
        }
        scope.launch {
            isConnecting = true
            statusMessage = "שולח את ${channel.name} אל ${device.name}…"
            val isLocalProxy = candidateRaw.startsWith("http://192.168.") ||
                candidateRaw.startsWith("http://10.") ||
                candidateRaw.startsWith("http://172.")
            val resolvedUrl = if (isLocalProxy) {
                candidateRaw
            } else {
                runCatching { playableUrl(candidateRaw, channel.auth) }.getOrDefault(candidateRaw)
            }
            val ok = sendDlnaSetAvTransportAndPlay(
                controlUrl = controlUrl,
                streamUrl = resolvedUrl,
                title = channel.name,
                isRadio = channel.isRadio,
            )
            if (ok) {
                isConnecting = false
                isCasting = true
                remotePlaying = true
                errorMessage = null
                statusMessage = "משדר כעת את ${channel.name} אל ${device.name}"
            } else if (currentCandidateIndex + 1 < currentCandidates.size) {
                currentCandidateIndex += 1
                loadCurrentDlnaCandidate(device, channel)
            } else {
                isConnecting = false
                isCasting = false
                remotePlaying = false
                errorMessage = "הטלוויזיה ${device.name} דחתה את השידור הישיר ב-DLNA. מומלץ לבחור ב-״אפליקציית ישראל TV+ בטלוויזיה״ או ב-״שיקוף מסך״."
            }
        }
    }

    /** Toggles Play/Pause on the currently connected TV receiver. */
    fun togglePlayPause() {
        val dev = connectedDevice ?: return
        when (dev.protocol) {
            CastProtocol.SMART_TV_APP -> {
                val nextPlay = !remotePlaying
                scope.launch {
                    if (sendSmartTvAppCommand(dev, if (nextPlay) "cast_play" else "cast_pause", pendingChannel)) {
                        remotePlaying = nextPlay
                        statusMessage = if (nextPlay) "משדר כעת בטלוויזיה" else "מושהה בטלוויזיה"
                    }
                }
            }
            CastProtocol.GOOGLE_CAST -> {
                val client = castContext?.sessionManager?.currentCastSession?.remoteMediaClient ?: return
                if (remotePlaying) {
                    client.pause()
                    remotePlaying = false
                } else {
                    client.play()
                    remotePlaying = true
                }
            }
            CastProtocol.DLNA -> {
                val controlUrl = dev.dlnaControlUrl ?: return
                val nextPlay = !remotePlaying
                scope.launch {
                    val action = if (nextPlay) "Play" else "Pause"
                    val body = if (nextPlay) {
                        "<u:Play xmlns:u=\"urn:schemas-upnp-org:service:AVTransport:1\"><InstanceID>0</InstanceID><Speed>1</Speed></u:Play>"
                    } else {
                        "<u:Pause xmlns:u=\"urn:schemas-upnp-org:service:AVTransport:1\"><InstanceID>0</InstanceID></u:Pause>"
                    }
                    if (sendDlnaSoap(controlUrl, action, body)) {
                        remotePlaying = nextPlay
                        statusMessage = if (nextPlay) "משדר כעת אל ${dev.name}" else "מושהה בטלוויזיה (${dev.name})"
                    }
                }
            }
            CastProtocol.MIRACAST_ROUTE -> {}
        }
    }

    /** Stops casting on the TV and returns playback to the phone. */
    fun disconnect() {
        val dev = connectedDevice
        when (dev?.protocol) {
            CastProtocol.SMART_TV_APP -> {
                scope.launch {
                    sendSmartTvAppCommand(dev, "cast_stop", pendingChannel)
                }
            }
            CastProtocol.GOOGLE_CAST -> {
                runCatching {
                    castContext?.sessionManager?.endCurrentSession(true)
                    mediaRouter?.unselect(MediaRouter.UNSELECT_REASON_STOPPED)
                }
            }
            CastProtocol.DLNA -> {
                val controlUrl = dev.dlnaControlUrl
                if (controlUrl != null) {
                    scope.launch {
                        sendDlnaSoap(
                            controlUrl,
                            "Stop",
                            "<u:Stop xmlns:u=\"urn:schemas-upnp-org:service:AVTransport:1\"><InstanceID>0</InstanceID></u:Stop>",
                        )
                    }
                }
            }
            CastProtocol.MIRACAST_ROUTE, null -> {}
        }
        connectedDevice = null
        isConnecting = false
        isCasting = false
        remotePlaying = false
        statusMessage = null
        errorMessage = null
    }

    /** Opens Android's system Cast / Miracast / LG Screen Share / Smart View picker. */
    fun openSystemMirroring(): Boolean {
        val intents = listOf(
            Intent("android.settings.CAST_SETTINGS"),
            Intent(Settings.ACTION_CAST_SETTINGS),
            Intent("android.settings.WIFI_DISPLAY_SETTINGS"),
            Intent("com.samsung.wfd.LAUNCH_WFD_PICKER_DLG"),
            Intent("miui.intent.action.MIPLAY"),
            Intent(Settings.ACTION_WIFI_SETTINGS),
        )
        for (intent in intents) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            if (runCatching { context.startActivity(intent); true }.getOrDefault(false)) {
                return true
            }
        }
        return false
    }

    /** Shares the resolved live stream URL to an external casting / player app (Web Video Caster, VLC, LG ThinQ, etc.). */
    fun openExternalCaster(channel: Channel) {
        scope.launch {
            val resolved = runCatching { playableUrl(channel) }.getOrDefault(channel.url)
            val mime = if (channel.isRadio) "audio/*" else "application/x-mpegURL"
            val viewIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(Uri.parse(resolved), mime)
                putExtra("title", channel.name)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            val chooser = Intent.createChooser(viewIntent, "שידור ${channel.name} באמצעות אפליקציה חיצונית").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            runCatching { context.startActivity(chooser) }.onFailure {
                errorMessage = "לא נמצאה אפליקציית שידור חיצונית במכשיר."
            }
        }
    }

    // =========================================================================
    // Smart TV App Direct Cast (via shared Home LAN topic + Google Sync topic)
    // =========================================================================

    private suspend fun resolveHomeLanCastTopic(): String? = withContext(Dispatchers.IO) {
        cachedLanTopic?.let { return@withContext it }
        val endpoints = listOf("https://api.ipify.org", "https://icanhazip.com")
        for (ep in endpoints) {
            val ip = runCatching {
                val conn = (URL(ep).openConnection() as HttpURLConnection).apply {
                    connectTimeout = 3500
                    readTimeout = 3500
                }
                conn.inputStream.bufferedReader().use { it.readText().trim() }
            }.getOrNull()
            if (!ip.isNullOrEmpty() && ip.length <= 64) {
                val digest = MessageDigest.getInstance("SHA-256").digest("itv_lan_$ip".toByteArray(Charsets.UTF_8))
                val hex = digest.joinToString("") { "%02x".format(it) }.take(14)
                val topic = "itv_lan_$hex"
                cachedLanTopic = topic
                return@withContext topic
            }
        }
        null
    }

    private suspend fun sendSmartTvAppCommand(
        device: CastDeviceItem,
        action: String,
        channel: Channel?,
    ): Boolean = withContext(Dispatchers.IO) {
        val topics = linkedSetOf<String>()
        device.castTopic?.takeIf { it.isNotBlank() }?.let { topics += it }
        resolveHomeLanCastTopic()?.let { topics += it }
        runCatching { AppPrefs(appContext).googleUser }
            .getOrNull()
            ?.let { GoogleSync.shortPairingCode(it.email) }
            ?.takeIf { it.isNotBlank() }
            ?.let { topics += "itv_cast_$it" }
        if (topics.isEmpty()) return@withContext false

        val payload = JSONObject().apply {
            put("type", action)
            if (channel != null) {
                put("channelId", channel.id)
                put("channelNumber", channel.number)
                put("channelName", channel.name)
            }
            put("ts", System.currentTimeMillis())
        }.toString()
        val bytes = payload.toByteArray(Charsets.UTF_8)

        var anySuccess = false
        for (topic in topics) {
            val ok = runCatching {
                val conn = (URL("https://ntfy.sh/$topic").openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    connectTimeout = 5000
                    readTimeout = 5000
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json; charset=utf-8")
                }
                conn.outputStream.use { it.write(bytes) }
                conn.responseCode in 200..299
            }.getOrDefault(false)
            if (ok) anySuccess = true
        }
        anySuccess
    }

    // =========================================================================
    // DLNA / UPnP AV Discovery (SSDP) & SOAP Control for LG webOS / Smart TVs
    // =========================================================================

    private suspend fun discoverDlnaDevices(): List<CastDeviceItem> = withContext(Dispatchers.IO) {
        val locations = linkedSetOf<String>()
        val wifi = runCatching {
            appContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
        }.getOrNull()
        val lock = runCatching {
            wifi?.createMulticastLock("israeltv_dlna_ssdp")?.apply {
                setReferenceCounted(false)
                acquire()
            }
        }.getOrNull()

        try {
            DatagramSocket().use { socket ->
                socket.broadcast = true
                socket.soTimeout = 700
                val multicastAddr = InetAddress.getByName("239.255.255.250")
                val searchTargets = listOf(
                    "urn:schemas-upnp-org:service:AVTransport:1",
                    "urn:schemas-upnp-org:device:MediaRenderer:1",
                    "ssdp:all",
                )
                for (st in searchTargets) {
                    val query = buildString {
                        append("M-SEARCH * HTTP/1.1\r\n")
                        append("HOST: 239.255.255.250:1900\r\n")
                        append("MAN: \"ssdp:discover\"\r\n")
                        append("MX: 2\r\n")
                        append("ST: $st\r\n\r\n")
                    }.toByteArray(Charsets.UTF_8)
                    val packet = DatagramPacket(query, query.size, multicastAddr, 1900)
                    runCatching { socket.send(packet) }
                }

                val deadline = System.currentTimeMillis() + 2200L
                val buf = ByteArray(4096)
                while (System.currentTimeMillis() < deadline) {
                    try {
                        val recv = DatagramPacket(buf, buf.size)
                        socket.receive(recv)
                        val resp = String(recv.data, 0, recv.length, Charsets.UTF_8)
                        val loc = Regex("(?im)^LOCATION:\\s*(.+)$").find(resp)?.groupValues?.get(1)?.trim()
                        if (!loc.isNullOrEmpty()) locations.add(loc)
                    } catch (_: SocketTimeoutException) {
                        // keep listening until deadline
                    } catch (_: Exception) {
                        break
                    }
                }
            }
        } catch (_: Exception) {
        } finally {
            runCatching { if (lock?.isHeld == true) lock.release() }
        }

        locations.mapNotNull { loc -> fetchDlnaDeviceDescription(loc) }
            .distinctBy { it.dlnaControlUrl ?: it.id }
    }

    private fun fetchDlnaDeviceDescription(locationUrl: String): CastDeviceItem? = runCatching {
        val conn = (URL(locationUrl).openConnection() as HttpURLConnection).apply {
            connectTimeout = 1500
            readTimeout = 1500
            setRequestProperty("User-Agent", USER_AGENT)
        }
        val xml = conn.inputStream.bufferedReader().use { it.readText() }
        val friendlyName = Regex("(?is)<friendlyName>\\s*([^<]+?)\\s*</friendlyName>")
            .find(xml)?.groupValues?.get(1)?.trim() ?: return null
        val manufacturer = Regex("(?is)<manufacturer>\\s*([^<]+?)\\s*</manufacturer>")
            .find(xml)?.groupValues?.get(1)?.trim().orEmpty()

        // Locate AVTransport:1 service block and its controlURL
        val serviceBlocks = Regex("(?is)<service>(.*?)</service>").findAll(xml)
        var avControlPath: String? = null
        for (m in serviceBlocks) {
            val block = m.groupValues[1]
            if (block.contains("AVTransport", ignoreCase = true)) {
                avControlPath = Regex("(?is)<controlURL>\\s*([^<]+?)\\s*</controlURL>")
                    .find(block)?.groupValues?.get(1)?.trim()
                if (!avControlPath.isNullOrEmpty()) break
            }
        }
        val controlPath = avControlPath ?: return null
        val baseUri = URI(locationUrl)
        val controlUrl = if (controlPath.startsWith("http://", true) || controlPath.startsWith("https://", true)) {
            controlPath
        } else {
            val prefix = "${baseUri.scheme}://${baseUri.Authority()}"
            if (controlPath.startsWith("/")) prefix + controlPath else "$prefix/$controlPath"
        }

        val desc = when {
            manufacturer.contains("LG", ignoreCase = true) || friendlyName.contains("LG", ignoreCase = true) ->
                "טלוויזיית LG webOS • שידור ישיר DLNA"
            manufacturer.contains("Samsung", ignoreCase = true) || friendlyName.contains("Samsung", ignoreCase = true) ->
                "טלוויזיית Samsung • שידור ישיר DLNA"
            manufacturer.isNotBlank() -> "$manufacturer • שידור ישיר DLNA"
            else -> "טלוויזיה חכמה ברשת (DLNA / UPnP)"
        }

        CastDeviceItem(
            id = "dlna:$controlUrl",
            name = friendlyName,
            description = desc,
            protocol = CastProtocol.DLNA,
            dlnaControlUrl = controlUrl,
        )
    }.getOrNull()

    private fun URI.Authority(): String = if (port != -1) "$host:$port" else host

    private suspend fun sendDlnaSetAvTransportAndPlay(
        controlUrl: String,
        streamUrl: String,
        title: String,
        isRadio: Boolean,
    ): Boolean = withContext(Dispatchers.IO) {
        // Send Stop first so the TV renderer clears any prior state
        runCatching {
            sendDlnaSoap(
                controlUrl,
                "Stop",
                "<u:Stop xmlns:u=\"urn:schemas-upnp-org:service:AVTransport:1\"><InstanceID>0</InstanceID></u:Stop>",
            )
        }
        delay(150)

        val escapedUrl = xmlEscape(streamUrl)
        val escapedTitle = xmlEscape(title)
        val upnpClass = if (isRadio) "object.item.audioItem.audioBroadcast" else "object.item.videoItem.videoBroadcast"
        val protoInfo = if (isRadio) {
            "http-get:*:audio/mpeg:DLNA.ORG_OP=01;DLNA.ORG_CI=0;DLNA.ORG_FLAGS=01700000000000000000000000000000"
        } else {
            "http-get:*:application/vnd.apple.mpegurl:*"
        }
        val didl = """<DIDL-Lite xmlns="urn:schemas-upnp-org:metadata-1-0/DIDL-Lite/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:upnp="urn:schemas-upnp-org:metadata-1-0/upnp/"><item id="0" parentID="-1" restricted="1"><dc:title>$escapedTitle</dc:title><upnp:class>$upnpClass</upnp:class><res protocolInfo="$protoInfo">$escapedUrl</res></item></DIDL-Lite>"""
        val escapedDidl = xmlEscape(didl)

        val setUriBody = """
            <u:SetAVTransportURI xmlns:u="urn:schemas-upnp-org:service:AVTransport:1">
                <InstanceID>0</InstanceID>
                <CurrentURI>$escapedUrl</CurrentURI>
                <CurrentURIMetaData>$escapedDidl</CurrentURIMetaData>
            </u:SetAVTransportURI>
        """.trimIndent()

        if (!sendDlnaSoap(controlUrl, "SetAVTransportURI", setUriBody)) return@withContext false
        delay(250)
        val playBody = """
            <u:Play xmlns:u="urn:schemas-upnp-org:service:AVTransport:1">
                <InstanceID>0</InstanceID>
                <Speed>1</Speed>
            </u:Play>
        """.trimIndent()
        sendDlnaSoap(controlUrl, "Play", playBody)
    }

    private suspend fun sendDlnaSoap(controlUrl: String, action: String, innerBody: String): Boolean =
        withContext(Dispatchers.IO) {
            runCatching {
                val envelope = """
                    <?xml version="1.0" encoding="utf-8"?>
                    <s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/" s:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/">
                        <s:Body>
                            $innerBody
                        </s:Body>
                    </s:Envelope>
                """.trimIndent()
                val bytes = envelope.toByteArray(Charsets.UTF_8)
                val conn = (URL(controlUrl).openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    connectTimeout = 4000
                    readTimeout = 4000
                    doOutput = true
                    setRequestProperty("Content-Type", "text/xml; charset=\"utf-8\"")
                    setRequestProperty("SOAPAction", "\"urn:schemas-upnp-org:service:AVTransport:1#$action\"")
                    setRequestProperty("Content-Length", bytes.size.toString())
                }
                conn.outputStream.use { it.write(bytes) }
                val code = conn.responseCode
                code in 200..299
            }.getOrDefault(false)
        }

    private fun xmlEscape(s: String): String = s
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&apos;")
}

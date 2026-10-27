package com.israeltv.plus.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.net.wifi.WifiManager
import android.provider.Settings
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.mediarouter.media.MediaControlIntent
import androidx.mediarouter.media.MediaRouteSelector
import androidx.mediarouter.media.MediaRouter
import com.google.android.gms.cast.CastMediaControlIntent
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
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.HttpURLConnection
import java.net.InetAddress
import java.net.SocketTimeoutException
import java.net.URI
import java.net.URL

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
    GOOGLE_CAST("שידור ישיר (Google Cast / Chromecast / Android TV)", "Google Cast"),
    DLNA("שידור ישיר ברשת (LG webOS / Samsung / DLNA)", "DLNA / Smart TV"),
}

data class CastDeviceItem(
    val id: String,
    val name: String,
    val description: String,
    val protocol: CastProtocol,
    val routeId: String? = null,
    val dlnaControlUrl: String? = null,
)

fun Channel.remoteLogoUrl(): String = when {
    logo.isEmpty() -> REMOTE + "assets/app/icon-512.png"
    logo.startsWith("http") -> logo
    else -> REMOTE + logo.removePrefix("/")
}

/**
 * Manages discovery and playback handoff for:
 *  1. Google Cast / Chromecast / Google TV / Android TV (via MediaRouter + CastContext)
 *  2. LG webOS / Samsung / Smart TV DLNA / UPnP AV MediaRenderers on the local Wi-Fi (via SSDP + SOAP AVTransport)
 *  3. Android System Screen Mirroring (Miracast / LG Screen Share / Smart View) & external caster apps
 */
@Stable
class CastController(
    private val context: Context,
    private val scope: CoroutineScope,
) {
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

    private var pendingChannel: Channel? = null
    private var currentCandidates = emptyList<String>()
    private var currentCandidateIndex = 0
    private var dlnaScanJob: Job? = null

    private val mediaRouter: MediaRouter? = runCatching { MediaRouter.getInstance(context) }.getOrNull()
    private val castContext: CastContext? = runCatching { CastContext.getSharedInstance(context) }.getOrNull()

    private val routeSelector: MediaRouteSelector? = runCatching {
        MediaRouteSelector.Builder()
            .addControlCategory(CastMediaControlIntent.categoryForCast(CastMediaControlIntent.DEFAULT_MEDIA_RECEIVER_APPLICATION_ID))
            .addControlCategory(MediaControlIntent.CATEGORY_REMOTE_PLAYBACK)
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
            errorMessage = "ההתחברות אל הטלוויזיה נכשלה (קוד $error). ודא שהטלפון והטלוויזיה באותה רשת Wi-Fi."
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
        runCatching {
            val selector = routeSelector
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

    /** Starts an active scan for both Google Cast / Android TV routes and LG / Samsung / DLNA TVs on LAN. */
    fun startDiscovery(currentChannel: Channel? = null) {
        if (currentChannel != null) pendingChannel = currentChannel
        errorMessage = null
        runCatching {
            val selector = routeSelector
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
        val selector = routeSelector ?: return
        val list = router.routes.mapNotNull { route ->
            if (route.isDefault || !route.isEnabled || route.isBluetooth) return@mapNotNull null
            if (!route.matchesSelector(selector)) return@mapNotNull null
            val name = route.name.toString().trim()
            if (name.isEmpty()) return@mapNotNull null
            CastDeviceItem(
                id = "cast:${route.id}",
                name = name,
                description = route.description?.toString()?.takeIf { it.isNotBlank() } ?: "Google Cast / Chromecast",
                protocol = CastProtocol.GOOGLE_CAST,
                routeId = route.id,
            )
        }.distinctBy { it.name.lowercase() }
        castRoutes = list
        mergeDevices()
    }

    private fun mergeDevices() {
        val castNames = castRoutes.map { it.name.lowercase() }.toSet()
        // Keep DLNA devices as well; if a TV supports both Google Cast and DLNA, show both with clear protocol badges
        devices = castRoutes + dlnaDevices.filter { dlna ->
            dlna.name.lowercase() !in castNames || dlna.protocol == CastProtocol.DLNA
        }
    }

    /** Connects to the chosen TV receiver and immediately casts [channel]. */
    fun connectAndCast(device: CastDeviceItem, channel: Channel) {
        pendingChannel = channel
        errorMessage = null
        when (device.protocol) {
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
            CastProtocol.DLNA -> {
                connectedDevice = device
                isConnecting = true
                statusMessage = "שולח שידור אל ${device.name}…"
                loadChannel(channel)
            }
        }
    }

    /** Sends [channel] to the currently connected TV receiver (Google Cast or DLNA). */
    fun loadChannel(channel: Channel) {
        pendingChannel = channel
        activeChannelId = channel.id
        errorMessage = null
        val dev = connectedDevice ?: return

        when (dev.protocol) {
            CastProtocol.GOOGLE_CAST -> {
                // Chromecast Default Media Receiver requires HTTPS URLs first; keep HTTP fallbacks after HTTPS ones
                val all = channel.allCandidateUrls()
                val httpsFirst = all.filter { it.startsWith("https://", ignoreCase = true) } +
                    all.filterNot { it.startsWith("https://", ignoreCase = true) }
                currentCandidates = httpsFirst.distinct()
                currentCandidateIndex = 0
                loadCurrentGoogleCastCandidate(channel)
            }
            CastProtocol.DLNA -> {
                currentCandidates = channel.allCandidateUrls()
                currentCandidateIndex = 0
                loadCurrentDlnaCandidate(dev, channel)
            }
        }
    }

    private fun loadCurrentGoogleCastCandidate(channel: Channel) {
        val candidateRaw = currentCandidates.getOrNull(currentCandidateIndex) ?: run {
            isConnecting = false
            errorMessage = "לא נמצא מקור שידור תואם ל-Chromecast עבור ${channel.name}. מומלץ להשתמש בשיקוף מסך (Miracast / LG Screen Share)."
            return
        }
        val session = castContext?.sessionManager?.currentCastSession ?: return
        val client = session.remoteMediaClient ?: return

        scope.launch {
            isConnecting = true
            statusMessage = "טוען את ${channel.name} בטלוויזיה…"
            val resolvedUrl = runCatching { playableUrl(candidateRaw, channel.auth) }.getOrDefault(candidateRaw)
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
            val mediaInfo = MediaInfo.Builder(resolvedUrl)
                .setStreamType(MediaInfo.STREAM_TYPE_LIVE)
                .setContentType(contentType)
                .setMetadata(metadata)
                .build()
            val req = MediaLoadRequestData.Builder()
                .setMediaInfo(mediaInfo)
                .setAutoplay(true)
                .build()

            client.load(req).setResultCallback { result ->
                if (!result.status.isSuccess) {
                    tryNextCastCandidateOrFail("הערוץ דורש חיבור ישיר או שיקוף מסך")
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
            errorMessage = "$reasonHe (${ch.name}). ניתן לעבור לשיקוף מסך אלחוטי עבור ערוץ זה."
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
            errorMessage = "הטלוויזיה ${device.name} לא הצליחה לנגן את הערוץ ישירות. נסה שיקוף מסך (LG Screen Share / Miracast)."
            return
        }
        scope.launch {
            isConnecting = true
            statusMessage = "שולח את ${channel.name} אל ${device.name}…"
            val resolvedUrl = runCatching { playableUrl(candidateRaw, channel.auth) }.getOrDefault(candidateRaw)
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
                errorMessage = "הטלוויזיה ${device.name} דחתה את השידור הישיר. מומלץ להשתמש ב״שיקוף מסך (LG Screen Share / Miracast)״ למטה."
            }
        }
    }

    /** Toggles Play/Pause on the currently connected TV receiver. */
    fun togglePlayPause() {
        val dev = connectedDevice ?: return
        when (dev.protocol) {
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
        }
    }

    /** Stops casting on the TV and returns playback to the phone. */
    fun disconnect() {
        val dev = connectedDevice
        when (dev?.protocol) {
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
            null -> {}
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
            Intent(Settings.ACTION_CAST_SETTINGS),
            Intent("android.settings.WIFI_DISPLAY_SETTINGS"),
            Intent("com.samsung.wfd.LAUNCH_WFD_PICKER_DLG"),
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
    // DLNA / UPnP AV Discovery (SSDP) & SOAP Control for LG webOS / Smart TVs
    // =========================================================================

    private suspend fun discoverDlnaDevices(): List<CastDeviceItem> = withContext(Dispatchers.IO) {
        val locations = linkedSetOf<String>()
        val wifi = runCatching {
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
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
                socket.soTimeout = 750
                val multicastAddr = InetAddress.getByName("239.255.255.250")
                val searchTargets = listOf(
                    "urn:schemas-upnp-org:service:AVTransport:1",
                    "urn:schemas-upnp-org:device:MediaRenderer:1",
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
        val escapedUrl = xmlEscape(streamUrl)
        val escapedTitle = xmlEscape(title)
        val upnpClass = if (isRadio) "object.item.audioItem.audioBroadcast" else "object.item.videoItem.videoBroadcast"
        val protoInfo = if (isRadio) "http-get:*:audio/mpeg:*" else "http-get:*:application/vnd.apple.mpegurl:*"
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


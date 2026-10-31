import os

path = r'C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo\android\app\src\main\kotlin\com\israeltv\plus\data\CastManager.kt'
with open(path, 'r', encoding='utf-8') as f:
    c = f.read()

# 1. imports
old_imp = 'import java.net.URL\n'
new_imp = '''import java.net.URL
import java.net.InetSocketAddress
import java.net.Socket
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
'''
if old_imp in c:
    c = c.replace(old_imp, new_imp, 1)

# 2. CastProtocol
old_proto = '''enum class CastProtocol(val labelHe: String, val badgeHe: String) {
    GOOGLE_CAST("שידור ישיר (Google Cast / Chromecast / Android TV)", "Google Cast"),
    DLNA("שידור ישיר ברשת (LG webOS / Samsung / DLNA)", "DLNA / Smart TV"),
}'''

new_proto = '''enum class CastProtocol(val labelHe: String, val badgeHe: String) {
    GOOGLE_CAST("שידור ישיר (Google Cast / Chromecast / Android TV)", "Google Cast"),
    DLNA("שידור ישיר ברשת (LG webOS / Samsung / DLNA)", "DLNA / Smart TV"),
    PLEX("Plex Live TV & DVR / נגן מדיה", "Plex"),
}'''
if old_proto in c:
    c = c.replace(old_proto, new_proto, 1)

# 3. CastDeviceItem
old_dev_item = '''data class CastDeviceItem(
    val id: String,
    val name: String,
    val description: String,
    val protocol: CastProtocol,
    val routeId: String? = null,
    val dlnaControlUrl: String? = null,
)'''

new_dev_item = '''data class CastDeviceItem(
    val id: String,
    val name: String,
    val description: String,
    val protocol: CastProtocol,
    val routeId: String? = null,
    val dlnaControlUrl: String? = null,
    val plexUrl: String? = null,
)'''
if old_dev_item in c:
    c = c.replace(old_dev_item, new_dev_item, 1)

# 4. CastProtocol.PLEX in connectAndCast
old_connect = '''            CastProtocol.DLNA -> {
                connectedDevice = device
                if (device.dlnaControlUrl != null) {
                    isConnecting = true
                    statusMessage = "שולח שידור אל ${device.name}…"
                    loadChannel(channel)
                } else {
                    statusMessage = "פותח שיקוף מסך אל ${device.name}…"
                    openSystemMirroring()
                }
            }'''

new_connect = '''            CastProtocol.DLNA -> {
                connectedDevice = device
                if (device.dlnaControlUrl != null) {
                    isConnecting = true
                    statusMessage = "שולח שידור אל ${device.name}…"
                    loadChannel(channel)
                } else {
                    statusMessage = "פותח שיקוף מסך אל ${device.name}…"
                    openSystemMirroring()
                }
            }
            CastProtocol.PLEX -> {
                connectedDevice = device
                isConnecting = false
                statusMessage = "מחובר אל ${device.name} (Live TV & DVR)"
            }'''
if old_connect in c:
    c = c.replace(old_connect, new_connect, 1)

# 5. discoverDlnaDevices and subnet probing
old_discover_start = '''    private suspend fun discoverDlnaDevices(): List<CastDeviceItem> = withContext(Dispatchers.IO) {'''

# Find the end of discoverDlnaDevices
discover_idx = c.find(old_discover_start)
assert discover_idx != -1, "discoverDlnaDevices not found"

new_discover_code = '''    private suspend fun discoverDlnaDevices(): List<CastDeviceItem> = withContext(Dispatchers.IO) {
        val locations = linkedSetOf<String>()
        val foundPlexDevices = mutableListOf<CastDeviceItem>()
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
            val multicastAddr = InetAddress.getByName("239.255.255.250")
            val broadcastAddr = InetAddress.getByName("255.255.255.255")
            val searchTargets = listOf(
                "ssdp:all",
                "upnp:rootdevice",
                "urn:schemas-upnp-org:device:MediaRenderer:1",
                "urn:schemas-upnp-org:service:AVTransport:1",
                "urn:schemas-upnp-org:device:MediaServer:1",
                "urn:plex-tv:service:PlexMediaServer:1",
                "urn:lge-com:service:webos-second-screen:1",
                "urn:dial-multiscreen-org:service:dial:1",
            )
            val packets = searchTargets.map { st ->
                val query = buildString {
                    append("M-SEARCH * HTTP/1.1\\r\\n")
                    append("HOST: 239.255.255.250:1900\\r\\n")
                    append("MAN: \\"ssdp:discover\\"\\r\\n")
                    append("MX: 2\\r\\n")
                    append("ST: $st\\r\\n\\r\\n")
                }.toByteArray(Charsets.UTF_8)
                DatagramPacket(query, query.size, multicastAddr, 1900)
            }

            // GDM discovery packets for Plex Media Server (UDP 32414)
            val gdmQuery = "M-SEARCH * HTTP/1.0\\r\\n".toByteArray(Charsets.UTF_8)
            val gdmPackets = listOf(
                DatagramPacket(gdmQuery, gdmQuery.size, multicastAddr, 32414),
                DatagramPacket(gdmQuery, gdmQuery.size, broadcastAddr, 32414),
            )

            DatagramSocket().use { socket ->
                socket.broadcast = true
                socket.soTimeout = 600

                // Send first burst
                for (packet in packets + gdmPackets) {
                    runCatching { socket.send(packet) }
                }

                // Also send through all active multicast-capable interfaces (e.g. wlan0)
                runCatching {
                    val interfaces = NetworkInterface.getNetworkInterfaces() ?: return@runCatching
                    for (ni in interfaces) {
                        if (ni.isUp && !ni.isLoopback && ni.supportsMulticast()) {
                            for (packet in packets + gdmPackets) {
                                runCatching { socket.send(packet) }
                            }
                        }
                    }
                }

                // Listen for responses with wave 2 burst at halfway
                val deadline = System.currentTimeMillis() + 3000L
                var secondWaveSent = false
                val buf = ByteArray(8192)

                while (System.currentTimeMillis() < deadline) {
                    if (!secondWaveSent && System.currentTimeMillis() > (deadline - 1800L)) {
                        secondWaveSent = true
                        for (packet in packets + gdmPackets) {
                            runCatching { socket.send(packet) }
                        }
                    }
                    try {
                        val recv = DatagramPacket(buf, buf.size)
                        socket.receive(recv)
                        val resp = String(recv.data, 0, recv.length, Charsets.UTF_8)
                        val loc = Regex("(?im)^LOCATION:\\\\s*(.+)$").find(resp)?.groupValues?.get(1)?.trim()
                        if (!loc.isNullOrEmpty()) locations.add(loc)

                        // Parse Plex GDM responses
                        if (resp.contains("plex/media-server", true) || resp.contains("Resource-Identifier", true)) {
                            val hostIp = recv.address.hostAddress
                            if (!hostIp.isNullOrEmpty()) {
                                val pName = Regex("(?im)^Name:\\\\s*(.+)$").find(resp)?.groupValues?.get(1)?.trim() ?: "שרת Plex"
                                val pPort = Regex("(?im)^Port:\\\\s*(\\\\d+)$").find(resp)?.groupValues?.get(1)?.trim()?.toIntOrNull() ?: 32400
                                foundPlexDevices.add(
                                    CastDeviceItem(
                                        id = "plex:$hostIp:$pPort",
                                        name = "Plex ($pName)",
                                        description = "שרת Plex Media Server • Live TV & DVR",
                                        protocol = CastProtocol.PLEX,
                                        plexUrl = "http://$hostIp:$pPort",
                                    )
                                )
                            }
                        }
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

        val parsedDlna = locations.mapNotNull { loc -> fetchDlnaDeviceDescription(loc) }
        val probed = probeSubnetDevices()

        (parsedDlna + foundPlexDevices + probed)
            .distinctBy { it.id }
    }

    private suspend fun probeSubnetDevices(): List<CastDeviceItem> = withContext(Dispatchers.IO) {
        val localIp = getLocalIpv4() ?: return@withContext emptyList()
        val bytes = localIp.address
        val prefix = "${bytes[0].toUByte()}.${bytes[1].toUByte()}.${bytes[2].toUByte()}."
        val myHost = bytes[3].toUByte().toInt()

        val hosts = (1..254).filter { it != myHost }
        val results = mutableListOf<CastDeviceItem>()

        // Probe in chunks to maintain low resource usage while completing fast
        val chunks = hosts.chunked(32)
        for (chunk in chunks) {
            val jobs = chunk.map { host ->
                async {
                    val ip = "$prefix$host"
                    probeHost(ip)
                }
            }
            val found = jobs.awaitAll().filterNotNull()
            results.addAll(found)
            if (results.size >= 8) break
        }
        results
    }

    private fun probeHost(ip: String): CastDeviceItem? {
        // 1. Check Plex port 32400
        if (isPortOpen(ip, 32400, 180)) {
            val dev = fetchPlexHttpIdentity(ip, 32400)
            if (dev != null) return dev
        }
        // 2. Check LG webOS TV port 3000 or 3001
        if (isPortOpen(ip, 3000, 150) || isPortOpen(ip, 3001, 150)) {
            return CastDeviceItem(
                id = "tv:lg:$ip",
                name = "טלוויזיית LG webOS ($ip)",
                description = "טלוויזיית LG webOS • שיקוף מסך / שידור אלחוטי",
                protocol = CastProtocol.DLNA,
            )
        }
        // 3. Check Google Cast port 8008
        if (isPortOpen(ip, 8008, 150)) {
            val castDev = fetchCastHttpInfo(ip)
            if (castDev != null) return castDev
        }
        // 4. Check UPnP ports
        for (port in listOf(8080, 7676, 52235)) {
            if (isPortOpen(ip, port, 120)) {
                val dev = fetchDlnaDeviceDescription("http://$ip:$port/description.xml")
                    ?: fetchDlnaDeviceDescription("http://$ip:$port/dmr/LG_DMR.xml")
                if (dev != null) return dev
            }
        }
        return null
    }

    private fun isPortOpen(ip: String, port: Int, timeoutMs: Int): Boolean = runCatching {
        Socket().use { s ->
            s.connect(InetSocketAddress(ip, port), timeoutMs)
            true
        }
    }.getOrDefault(false)

    private fun fetchPlexHttpIdentity(ip: String, port: Int): CastDeviceItem? = runCatching {
        val conn = (URL("http://$ip:$port/identity").openConnection() as HttpURLConnection).apply {
            connectTimeout = 800
            readTimeout = 800
        }
        val xml = conn.inputStream.bufferedReader().use { it.readText() }
        val machineId = Regex("(?im)machineIdentifier=\\"([^\\"]+)\\"").find(xml)?.groupValues?.get(1) ?: return null
        val ver = Regex("(?im)version=\\"([^\\"]+)\\"").find(xml)?.groupValues?.get(1) ?: ""
        CastDeviceItem(
            id = "plex:$machineId",
            name = "שרת Plex ($ip)",
            description = "Plex Media Server $ver • Live TV & DVR".trim(),
            protocol = CastProtocol.PLEX,
            plexUrl = "http://$ip:$port",
        )
    }.getOrNull()

    private fun fetchCastHttpInfo(ip: String): CastDeviceItem? = runCatching {
        val conn = (URL("http://$ip:8008/setup/eureka_info?params=name").openConnection() as HttpURLConnection).apply {
            connectTimeout = 800
            readTimeout = 800
        }
        val jsonStr = conn.inputStream.bufferedReader().use { it.readText() }
        val name = Regex("(?im)\\"name\\"\\\\s*:\\\\s*\\"([^\\"]+)\\"").find(jsonStr)?.groupValues?.get(1) ?: "Chromecast ($ip)"
        CastDeviceItem(
            id = "cast_http:$ip",
            name = name,
            description = "Google Cast / Chromecast ($ip)",
            protocol = CastProtocol.GOOGLE_CAST,
        )
    }.getOrNull()

    private fun getLocalIpv4(): InetAddress? = runCatching {
        val interfaces = NetworkInterface.getNetworkInterfaces() ?: return null
        for (ni in interfaces) {
            if (!ni.isUp || ni.isLoopback) continue
            for (addr in ni.inetAddresses) {
                if (addr is java.net.Inet4Address && !addr.isLoopbackAddress && !addr.isLinkLocalAddress) {
                    return addr
                }
            }
        }
        null
    }.getOrNull()
'''

# Find end of discoverDlnaDevices: where "private fun fetchDlnaDeviceDescription" begins
desc_idx = c.find('    private fun fetchDlnaDeviceDescription')
assert desc_idx != -1, "fetchDlnaDeviceDescription not found"

c = c[:discover_idx] + new_discover_code + '\n' + c[desc_idx:]

with open(path, 'w', encoding='utf-8') as f:
    f.write(c)
print('Successfully applied patch to CastManager.kt')

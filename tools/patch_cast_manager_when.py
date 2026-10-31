import os

path = r'C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo\android\app\src\main\kotlin\com\israeltv\plus\data\CastManager.kt'
with open(path, 'r', encoding='utf-8') as f:
    c = f.read()

# 1. in loadChannel
old_dlna_load = '''            CastProtocol.DLNA -> {
                currentCandidates = channel.allCandidateUrls()
                currentCandidateIndex = 0
                loadCurrentDlnaCandidate(dev, channel)
            }'''

new_dlna_load = '''            CastProtocol.DLNA -> {
                currentCandidates = channel.allCandidateUrls()
                currentCandidateIndex = 0
                loadCurrentDlnaCandidate(dev, channel)
            }
            CastProtocol.PLEX -> {
                isConnecting = false
                statusMessage = "שידור ערוץ ${channel.name} ב-Plex"
            }'''

if old_dlna_load in c:
    c = c.replace(old_dlna_load, new_dlna_load, 1)

# 2. in togglePlayPause
old_pause = '''            CastProtocol.DLNA -> {
                val controlUrl = dev.dlnaControlUrl ?: return
                val nextPlay = !remotePlaying
                scope.launch {
                    val action = if (nextPlay) "Play" else "Pause"
                    val body = if (nextPlay) {
                        "<u:Play xmlns:u=\\"urn:schemas-upnp-org:service:AVTransport:1\\"><InstanceID>0</InstanceID><Speed>1</Speed></u:Play>"
                    } else {
                        "<u:Pause xmlns:u=\\"urn:schemas-upnp-org:service:AVTransport:1\\"><InstanceID>0</InstanceID></u:Pause>"
                    }
                    if (sendDlnaSoap(controlUrl, action, body)) {
                        remotePlaying = nextPlay
                        statusMessage = if (nextPlay) "משדר כעת אל ${dev.name}" else "מושהה בטלוויזיה (${dev.name})"
                    }
                }
            }'''

new_pause = old_pause + '''
            CastProtocol.PLEX -> {}'''

if old_pause in c:
    c = c.replace(old_pause, new_pause, 1)

# 3. in disconnect
old_disc = '''            CastProtocol.DLNA -> {
                val controlUrl = dev.dlnaControlUrl
                if (controlUrl != null) {
                    scope.launch {
                        sendDlnaSoap(
                            controlUrl,
                            "Stop",
                            "<u:Stop xmlns:u=\\"urn:schemas-upnp-org:service:AVTransport:1\\"><InstanceID>0</InstanceID></u:Stop>",
                        )
                    }
                }
            }
            null -> {}'''

new_disc = '''            CastProtocol.DLNA -> {
                val controlUrl = dev.dlnaControlUrl
                if (controlUrl != null) {
                    scope.launch {
                        sendDlnaSoap(
                            controlUrl,
                            "Stop",
                            "<u:Stop xmlns:u=\\"urn:schemas-upnp-org:service:AVTransport:1\\"><InstanceID>0</InstanceID></u:Stop>",
                        )
                    }
                }
            }
            CastProtocol.PLEX, null -> {}'''

if old_disc in c:
    c = c.replace(old_disc, new_disc, 1)

with open(path, 'w', encoding='utf-8') as f:
    f.write(c)
print('Successfully updated when expressions in CastManager.kt')


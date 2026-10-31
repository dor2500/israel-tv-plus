"""Israel TV+ DLNA & UPnP AV Media Server

Serves all 387 verified live channels to Smart TVs (LG webOS, Samsung Tizen),
VLC, Kodi, Windows Media Player, Plex and UPnP/DLNA renderers on the local Wi-Fi.

Runs on port 8200 (HTTP) + port 1900 (SSDP discovery).
"""
import io
import json
import os
import re
import socket
import struct
import sys
import threading
import time
import urllib.parse
from http.server import HTTPServer, BaseHTTPRequestHandler
from xml.sax.saxutils import escape as xml_escape

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
CHANNELS_FILE = os.path.join(ROOT, "channels.json")
PORT = 8200
SSDP_PORT = 1900
SSDP_ADDR = "239.255.255.250"
UUID = "8e9f2a40-2026-4b8b-9d10-israeltvplus0"


def get_local_ip():
    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    try:
        s.connect(("8.8.8.8", 80))
        return s.getsockname()[0]
    except Exception:
        return "127.0.0.1"
    finally:
        s.close()


LOCAL_IP = get_local_ip()


def load_channels():
    if not os.path.exists(CHANNELS_FILE):
        return []
    with open(CHANNELS_FILE, "r", encoding="utf-8") as f:
        return json.load(f)


CHANNELS = load_channels()
CH_MAP = {ch["id"]: ch for ch in CHANNELS}

# Extract unique categories
CATEGORIES = []
for ch in CHANNELS:
    cat = ch.get("category", "שונות")
    if cat not in CATEGORIES:
        CATEGORIES.append(cat)


DEVICE_DESCRIPTION = f"""<?xml version="1.0" encoding="utf-8"?>
<root xmlns="urn:schemas-upnp-org:device-1-0" xmlns:dlna="urn:schemas-dlna-org:device-1-0">
  <specVersion>
    <major>1</major>
    <minor>0</minor>
  </specVersion>
  <device>
    <deviceType>urn:schemas-upnp-org:device:MediaServer:1</deviceType>
    <friendlyName>ישראל TV+ • שידורים חיים (DLNA)</friendlyName>
    <manufacturer>Israel TV Plus</manufacturer>
    <manufacturerURL>https://dor2500.github.io/israel-tv-plus/</manufacturerURL>
    <modelDescription>Israel TV+ Live TV DLNA Media Server</modelDescription>
    <modelName>IsraelTVPlus DLNA</modelName>
    <modelNumber>4.8.11</modelNumber>
    <modelURL>https://dor2500.github.io/israel-tv-plus/</modelURL>
    <serialNumber>20261031</serialNumber>
    <UDN>uuid:{UUID}</UDN>
    <dlna:X_DLNADOC xmlns:dlna="urn:schemas-dlna-org:device-1-0">DMS-1.50</dlna:X_DLNADOC>
    <iconList>
      <icon>
        <mimetype>image/png</mimetype>
        <width>128</width>
        <height>128</height>
        <depth>24</depth>
        <url>/icon.png</url>
      </icon>
    </iconList>
    <serviceList>
      <service>
        <serviceType>urn:schemas-upnp-org:service:ContentDirectory:1</serviceType>
        <serviceId>urn:upnp-org:serviceId:ContentDirectory</serviceId>
        <SCPDURL>/content_directory.xml</SCPDURL>
        <controlURL>/control/ContentDirectory</controlURL>
        <eventSubURL>/events/ContentDirectory</eventSubURL>
      </service>
      <service>
        <serviceType>urn:schemas-upnp-org:service:ConnectionManager:1</serviceType>
        <serviceId>urn:upnp-org:serviceId:ConnectionManager</serviceId>
        <SCPDURL>/connection_manager.xml</SCPDURL>
        <controlURL>/control/ConnectionManager</controlURL>
        <eventSubURL>/events/ConnectionManager</eventSubURL>
      </service>
    </serviceList>
  </device>
</root>"""

CONTENT_DIRECTORY_SCPD = """<?xml version="1.0" encoding="utf-8"?>
<scpd xmlns="urn:schemas-upnp-org:service-1-0">
  <specVersion><major>1</major><minor>0</minor></specVersion>
  <actionList>
    <action>
      <name>Browse</name>
      <argumentList>
        <argument><name>ObjectID</name><direction>in</direction><relatedStateVariable>A_ARG_TYPE_ObjectID</relatedStateVariable></argument>
        <argument><name>BrowseFlag</name><direction>in</direction><relatedStateVariable>A_ARG_TYPE_BrowseFlag</relatedStateVariable></argument>
        <argument><name>Filter</name><direction>in</direction><relatedStateVariable>A_ARG_TYPE_Filter</relatedStateVariable></argument>
        <argument><name>StartingIndex</name><direction>in</direction><relatedStateVariable>A_ARG_TYPE_Index</relatedStateVariable></argument>
        <argument><name>RequestedCount</name><direction>in</direction><relatedStateVariable>A_ARG_TYPE_Count</relatedStateVariable></argument>
        <argument><name>SortCriteria</name><direction>in</direction><relatedStateVariable>A_ARG_TYPE_SortCriteria</relatedStateVariable></argument>
        <argument><name>Result</name><direction>out</direction><relatedStateVariable>A_ARG_TYPE_Result</relatedStateVariable></argument>
        <argument><name>NumberReturned</name><direction>out</direction><relatedStateVariable>A_ARG_TYPE_Count</relatedStateVariable></argument>
        <argument><name>TotalMatches</name><direction>out</direction><relatedStateVariable>A_ARG_TYPE_Count</relatedStateVariable></argument>
        <argument><name>UpdateID</name><direction>out</direction><relatedStateVariable>A_ARG_TYPE_UpdateID</relatedStateVariable></argument>
      </argumentList>
    </action>
    <action>
      <name>GetSystemUpdateID</name>
      <argumentList>
        <argument><name>Id</name><direction>out</direction><relatedStateVariable>SystemUpdateID</relatedStateVariable></argument>
      </argumentList>
    </action>
  </actionList>
  <serviceStateTable>
    <stateVariable sendEvents="no"><name>A_ARG_TYPE_ObjectID</name><dataType>string</dataType></stateVariable>
    <stateVariable sendEvents="no"><name>A_ARG_TYPE_Result</name><dataType>string</dataType></stateVariable>
    <stateVariable sendEvents="no"><name>A_ARG_TYPE_BrowseFlag</name><dataType>string</dataType><allowedValueList><allowedValue>BrowseMetadata</allowedValue><allowedValue>BrowseDirectChildren</allowedValue></allowedValueList></stateVariable>
    <stateVariable sendEvents="no"><name>A_ARG_TYPE_Filter</name><dataType>string</dataType></stateVariable>
    <stateVariable sendEvents="no"><name>A_ARG_TYPE_SortCriteria</name><dataType>string</dataType></stateVariable>
    <stateVariable sendEvents="no"><name>A_ARG_TYPE_Index</name><dataType>ui4</dataType></stateVariable>
    <stateVariable sendEvents="no"><name>A_ARG_TYPE_Count</name><dataType>ui4</dataType></stateVariable>
    <stateVariable sendEvents="no"><name>A_ARG_TYPE_UpdateID</name><dataType>ui4</dataType></stateVariable>
    <stateVariable sendEvents="yes"><name>SystemUpdateID</name><dataType>ui4</dataType></stateVariable>
  </serviceStateTable>
</scpd>"""

CONNECTION_MANAGER_SCPD = """<?xml version="1.0" encoding="utf-8"?>
<scpd xmlns="urn:schemas-upnp-org:service-1-0">
  <specVersion><major>1</major><minor>0</minor></specVersion>
  <actionList>
    <action>
      <name>GetProtocolInfo</name>
      <argumentList>
        <argument><name>Source</name><direction>out</direction><relatedStateVariable>SourceProtocolInfo</relatedStateVariable></argument>
        <argument><name>Sink</name><direction>out</direction><relatedStateVariable>SinkProtocolInfo</relatedStateVariable></argument>
      </argumentList>
    </action>
  </actionList>
  <serviceStateTable>
    <stateVariable sendEvents="yes"><name>SourceProtocolInfo</name><dataType>string</dataType></stateVariable>
    <stateVariable sendEvents="yes"><name>SinkProtocolInfo</name><dataType>string</dataType></stateVariable>
  </serviceStateTable>
</scpd>"""


def build_channel_didl(ch, parent_id="0"):
    cid = ch["id"]
    name = xml_escape(ch["name"])
    is_radio = ch.get("radio") or ch.get("category") == "רדיו"
    stream_url = ch.get("url", "")
    proxy_url = f"http://{LOCAL_IP}:{PORT}/stream/{cid}"
    logo = ch.get("logo", "")
    if logo and not logo.startswith("http"):
        logo = f"https://dor2500.github.io/israel-tv-plus/{logo.lstrip('/')}"
    logo_tag = f"<upnp:albumArtURI>{xml_escape(logo)}</upnp:albumArtURI>" if logo else ""

    upnp_class = "object.item.audioItem.audioBroadcast" if is_radio else "object.item.videoItem.videoBroadcast"
    proto_info = "http-get:*:audio/mpeg:*" if is_radio else "http-get:*:application/vnd.apple.mpegurl:*"

    return (
        f'<item id="ch_{cid}" parentID="{parent_id}" restricted="1">'
        f'<dc:title>{name}</dc:title>'
        f'<upnp:class>{upnp_class}</upnp:class>'
        f'{logo_tag}'
        f'<res protocolInfo="{proto_info}">{xml_escape(stream_url)}</res>'
        f'<res protocolInfo="http-get:*:video/mp4:*">{xml_escape(proxy_url)}</res>'
        f'</item>'
    )


def build_container_didl(cid, title, parent_id="0", child_count=0):
    return (
        f'<container id="{cid}" parentID="{parent_id}" restricted="1" childCount="{child_count}">'
        f'<dc:title>{xml_escape(title)}</dc:title>'
        f'<upnp:class>object.container.storageFolder</upnp:class>'
        f'</container>'
    )


def handle_browse(obj_id, flag, starting_idx=0, req_count=0):
    items = []
    total = 0

    if flag == "BrowseMetadata":
        if obj_id == "0":
            items.append(build_container_didl("0", "ישראל TV+", "-1", len(CATEGORIES) + 1))
        elif obj_id == "cat_all":
            items.append(build_container_didl("cat_all", "כל הערוצים", "0", len(CHANNELS)))
        elif obj_id.startswith("cat_"):
            cat_name = obj_id[4:]
            sub = [c for c in CHANNELS if c.get("category") == cat_name]
            items.append(build_container_didl(obj_id, cat_name, "0", len(sub)))
        elif obj_id.startswith("ch_"):
            cid = obj_id[3:]
            if cid in CH_MAP:
                items.append(build_channel_didl(CH_MAP[cid], "0"))
        total = len(items)
    else:  # BrowseDirectChildren
        if obj_id == "0":
            # Root: All channels folder + Category folders
            items.append(build_container_didl("cat_all", f"⭐ כל הערוצים ({len(CHANNELS)})", "0", len(CHANNELS)))
            for cat in CATEGORIES:
                sub = [c for c in CHANNELS if c.get("category") == cat]
                items.append(build_container_didl(f"cat_{cat}", f"{cat} ({len(sub)})", "0", len(sub)))
            total = len(items)
        elif obj_id == "cat_all":
            for ch in CHANNELS:
                items.append(build_channel_didl(ch, "cat_all"))
            total = len(items)
        elif obj_id.startswith("cat_"):
            cat_name = obj_id[4:]
            sub = [c for c in CHANNELS if c.get("category") == cat_name]
            for ch in sub:
                items.append(build_channel_didl(ch, obj_id))
            total = len(items)

    # Slice with starting_idx and req_count
    if req_count > 0:
        sliced = items[starting_idx:starting_idx + req_count]
    else:
        sliced = items[starting_idx:]

    didl = (
        '<DIDL-Lite xmlns="urn:schemas-upnp-org:metadata-1-0/DIDL-Lite/" '
        'xmlns:dc="http://purl.org/dc/elements/1.1/" '
        'xmlns:upnp="urn:schemas-upnp-org:metadata-1-0/upnp/">'
        + "".join(sliced)
        + "</DIDL-Lite>"
    )
    return didl, len(sliced), total


class DlnaHandler(BaseHTTPRequestHandler):
    def log_message(self, format, *args):
        # Quiet log
        pass

    def do_GET(self):
        parsed = urllib.parse.urlparse(self.path)
        path = parsed.path

        if path in ["/description.xml", "/"]:
            body = DEVICE_DESCRIPTION.encode("utf-8")
            self.send_response(200)
            self.send_header("Content-Type", "text/xml; charset=utf-8")
            self.send_header("Content-Length", str(len(body)))
            self.send_header("Access-Control-Allow-Origin", "*")
            self.end_headers()
            self.wfile.write(body)
            return

        if path == "/content_directory.xml":
            body = CONTENT_DIRECTORY_SCPD.encode("utf-8")
            self.send_response(200)
            self.send_header("Content-Type", "text/xml; charset=utf-8")
            self.send_header("Content-Length", str(len(body)))
            self.end_headers()
            self.wfile.write(body)
            return

        if path == "/connection_manager.xml":
            body = CONNECTION_MANAGER_SCPD.encode("utf-8")
            self.send_response(200)
            self.send_header("Content-Type", "text/xml; charset=utf-8")
            self.send_header("Content-Length", str(len(body)))
            self.end_headers()
            self.wfile.write(body)
            return

        if path == "/channels.m3u":
            lines = ["#EXTM3U\n"]
            for ch in CHANNELS:
                name = ch["name"]
                url = ch.get("url", "")
                cat = ch.get("category", "")
                logo = ch.get("logo", "")
                if logo and not logo.startswith("http"):
                    logo = f"https://dor2500.github.io/israel-tv-plus/{logo.lstrip('/')}"
                lines.append(f'#EXTINF:-1 tvg-id="{ch["id"]}" tvg-name="{name}" tvg-logo="{logo}" group-title="{cat}",{name}\n{url}\n')
            body = "".join(lines).encode("utf-8")
            self.send_response(200)
            self.send_header("Content-Type", "audio/x-mpegurl; charset=utf-8")
            self.send_header("Content-Length", str(len(body)))
            self.send_header("Access-Control-Allow-Origin", "*")
            self.end_headers()
            self.wfile.write(body)
            return

        if path.startswith("/stream/"):
            cid = path[8:]
            if cid in CH_MAP:
                stream_url = CH_MAP[cid].get("url", "")
                self.send_response(302)
                self.send_header("Location", stream_url)
                self.send_header("Access-Control-Allow-Origin", "*")
                self.end_headers()
                return

        if path == "/icon.png":
            icon_path = os.path.join(ROOT, "icon.png")
            if os.path.exists(icon_path):
                with open(icon_path, "rb") as f:
                    body = f.read()
                self.send_response(200)
                self.send_header("Content-Type", "image/png")
                self.send_header("Content-Length", str(len(body)))
                self.end_headers()
                self.wfile.write(body)
                return

        self.send_response(404)
        self.end_headers()

    def do_POST(self):
        length = int(self.headers.get("Content-Length", 0))
        body = self.rfile.read(length).decode("utf-8", errors="ignore")

        if "/control/ContentDirectory" in self.path:
            # Parse Browse action
            m_id = re.search(r"<ObjectID>([^<]+)</ObjectID>", body)
            obj_id = m_id.group(1).strip() if m_id else "0"
            m_flag = re.search(r"<BrowseFlag>([^<]+)</BrowseFlag>", body)
            flag = m_flag.group(1).strip() if m_flag else "BrowseDirectChildren"
            m_idx = re.search(r"<StartingIndex>(\d+)</StartingIndex>", body)
            starting_idx = int(m_idx.group(1)) if m_idx else 0
            m_count = re.search(r"<RequestedCount>(\d+)</RequestedCount>", body)
            req_count = int(m_count.group(1)) if m_count else 0

            didl, count, total = handle_browse(obj_id, flag, starting_idx, req_count)
            escaped_didl = xml_escape(didl)

            resp = f"""<?xml version="1.0" encoding="utf-8"?>
<s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/" s:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/">
  <s:Body>
    <u:BrowseResponse xmlns:u="urn:schemas-upnp-org:service:ContentDirectory:1">
      <Result>{escaped_didl}</Result>
      <NumberReturned>{count}</NumberReturned>
      <TotalMatches>{total}</TotalMatches>
      <UpdateID>1</UpdateID>
    </u:BrowseResponse>
  </s:Body>
</s:Envelope>"""
            resp_bytes = resp.encode("utf-8")
            self.send_response(200)
            self.send_header("Content-Type", 'text/xml; charset="utf-8"')
            self.send_header("Content-Length", str(len(resp_bytes)))
            self.send_header("EXT", "")
            self.end_headers()
            self.wfile.write(resp_bytes)
            return

        if "/control/ConnectionManager" in self.path:
            proto = "http-get:*:application/vnd.apple.mpegurl:*,http-get:*:video/mp4:*,http-get:*:audio/mpeg:*"
            resp = f"""<?xml version="1.0" encoding="utf-8"?>
<s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/" s:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/">
  <s:Body>
    <u:GetProtocolInfoResponse xmlns:u="urn:schemas-upnp-org:service:ConnectionManager:1">
      <Source>{proto}</Source>
      <Sink></Sink>
    </u:GetProtocolInfoResponse>
  </s:Body>
</s:Envelope>"""
            resp_bytes = resp.encode("utf-8")
            self.send_response(200)
            self.send_header("Content-Type", 'text/xml; charset="utf-8"')
            self.send_header("Content-Length", str(len(resp_bytes)))
            self.end_headers()
            self.wfile.write(resp_bytes)
            return

        self.send_response(404)
        self.end_headers()


def run_ssdp():
    sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM, socket.IPPROTO_UDP)
    sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    sock.bind(("", SSDP_PORT))
    mreq = struct.pack("4sl", socket.inet_aton(SSDP_ADDR), socket.INADDR_ANY)
    sock.setsockopt(socket.IPPROTO_IP, socket.IP_ADD_MEMBERSHIP, mreq)

    print(f"[SSDP] Listening on {SSDP_ADDR}:{SSDP_PORT}...")

    def send_notify():
        while True:
            try:
                targets = [
                    "upnp:rootdevice",
                    f"uuid:{UUID}",
                    "urn:schemas-upnp-org:device:MediaServer:1",
                    "urn:schemas-upnp-org:service:ContentDirectory:1",
                    "urn:schemas-upnp-org:service:ConnectionManager:1",
                ]
                for nt in targets:
                    msg = (
                        f"NOTIFY * HTTP/1.1\r\n"
                        f"HOST: {SSDP_ADDR}:{SSDP_PORT}\r\n"
                        f"CACHE-CONTROL: max-age=1800\r\n"
                        f"LOCATION: http://{LOCAL_IP}:{PORT}/description.xml\r\n"
                        f"NT: {nt}\r\n"
                        f"NTS: ssdp:alive\r\n"
                        f"SERVER: Windows/10 UPnP/1.0 DLNADOC/1.50 IsraelTVPlus/4.8.11\r\n"
                        f"USN: uuid:{UUID}::{nt}\r\n\r\n"
                    ).encode("utf-8")
                    sock.sendto(msg, (SSDP_ADDR, SSDP_PORT))
            except Exception:
                pass
            time.sleep(45)

    threading.Thread(target=send_notify, daemon=True).start()

    while True:
        try:
            data, addr = sock.recvfrom(4096)
            text = data.decode("utf-8", errors="ignore")
            if "M-SEARCH" in text:
                m_st = re.search(r"(?im)^ST:\s*(.+)$", text)
                st = m_st.group(1).strip() if m_st else ""

                match = (
                    st in ["ssdp:all", "upnp:rootdevice"]
                    or "MediaServer" in st
                    or "ContentDirectory" in st
                    or "ConnectionManager" in st
                    or UUID in st
                )
                if match:
                    resp_st = st if st != "ssdp:all" else "urn:schemas-upnp-org:device:MediaServer:1"
                    reply = (
                        f"HTTP/1.1 200 OK\r\n"
                        f"CACHE-CONTROL: max-age=1800\r\n"
                        f"DATE: {time.strftime('%a, %d %b %Y %H:%M:%S GMT', time.gmtime())}\r\n"
                        f"EXT:\r\n"
                        f"LOCATION: http://{LOCAL_IP}:{PORT}/description.xml\r\n"
                        f"SERVER: Windows/10 UPnP/1.0 DLNADOC/1.50 IsraelTVPlus/4.8.11\r\n"
                        f"ST: {resp_st}\r\n"
                        f"USN: uuid:{UUID}::{resp_st}\r\n\r\n"
                    ).encode("utf-8")
                    sock.sendto(reply, addr)
        except Exception:
            pass


def main():
    print(f"================================================================")
    print(f"       Israel TV+ DLNA & UPnP AV Media Server v4.8.11           ")
    print(f"================================================================")
    print(f"Local IP:     {LOCAL_IP}")
    print(f"HTTP Port:    {PORT}")
    print(f"Channels:     {len(CHANNELS)} verified channels ready")
    print(f"Categories:   {len(CATEGORIES)} ({', '.join(CATEGORIES[:5])}...)")
    print(f"DLNA URL:     http://{LOCAL_IP}:{PORT}/description.xml")
    print(f"M3U URL:      http://{LOCAL_IP}:{PORT}/channels.m3u")
    print(f"================================================================")

    # Start SSDP discovery responder in background thread
    threading.Thread(target=run_ssdp, daemon=True).start()

    # Start HTTP server
    server = HTTPServer(("0.0.0.0", PORT), DlnaHandler)
    print(f"Server is running! You can open http://{LOCAL_IP}:{PORT}/ in browser or on TV.")
    try:
        server.serve_forever()
    except KeyboardInterrupt:
        print("\nStopping DLNA server...")
        server.shutdown()


if __name__ == "__main__":
    main()

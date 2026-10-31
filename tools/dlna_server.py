"""Israel TV+ Direct PC DLNA Server v4.8.12 for LG webOS & Smart TVs

Fully supports LG webOS Media Player / Photos & Video:
- Handles Browse, Search, GetSearchCapabilities, GetSortCapabilities
- Handles Root (0), Video (2, 0$1$2), Categories, and channel streaming
- Primary stream delivered as native HLS application/vnd.apple.mpegurl with direct fallback
- Unicast and Multicast SSDP to LG TV (192.168.1.104, 192.168.1.6, 192.168.1.7)
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
import urllib.request
from http.server import HTTPServer, BaseHTTPRequestHandler
from xml.sax.saxutils import escape as xml_escape

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
CHANNELS_FILE = os.path.join(ROOT, "channels.json")
LOG_FILE = os.path.join(ROOT, "dlna_activity.log")
PORT = 8200
SSDP_PORT = 1900
SSDP_ADDR = "239.255.255.250"
UUID = "8e9f2a40-2026-4b8b-9d10-israeltvplus0"


def log_event(msg):
    ts = time.strftime("%Y-%m-%d %H:%M:%S")
    line = f"[{ts}] {msg}"
    print(line, flush=True)
    try:
        with open(LOG_FILE, "a", encoding="utf-8") as f:
            f.write(line + "\n")
    except Exception:
        pass


def get_lan_ip():
    try:
        hostname = socket.gethostname()
        for info in socket.getaddrinfo(hostname, None, socket.AF_INET):
            ip = info[4][0]
            if ip.startswith("192.168.1.") or ip.startswith("10.0.") or ip.startswith("172.16."):
                return ip
    except Exception:
        pass
    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    try:
        s.connect(("8.8.8.8", 80))
        return s.getsockname()[0]
    except Exception:
        return "192.168.1.4"
    finally:
        s.close()


LOCAL_IP = get_lan_ip()
SUBNET_PREFIX = ".".join(LOCAL_IP.split(".")[:3]) + "."
BROADCAST_ADDR = SUBNET_PREFIX + "255"

# Targets including discovered LG TV addresses (104, 6, 7) and common LAN IPs
KNOWN_TARGETS = [f"{SUBNET_PREFIX}{i}" for i in [104, 6, 7, 2, 3, 5, 8, 9, 10, 11, 12, 13, 14, 15, 20, 32, 45, 60, 110, 127]]


def load_channels():
    if not os.path.exists(CHANNELS_FILE):
        return []
    with open(CHANNELS_FILE, "r", encoding="utf-8") as f:
        return json.load(f)


CHANNELS = load_channels()
CH_MAP = {ch["id"]: ch for ch in CHANNELS}

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
    <friendlyName>ישראל TV+ (שידורים חיים)</friendlyName>
    <manufacturer>Israel TV Plus</manufacturer>
    <manufacturerURL>https://dor2500.github.io/israel-tv-plus/</manufacturerURL>
    <modelDescription>Israel TV+ Live TV DLNA Media Server</modelDescription>
    <modelName>Windows Media Connect compatible (IsraelTVPlus)</modelName>
    <modelNumber>4.8.12</modelNumber>
    <modelURL>https://dor2500.github.io/israel-tv-plus/</modelURL>
    <serialNumber>20261031</serialNumber>
    <UDN>uuid:{UUID}</UDN>
    <dlna:X_DLNADOC>DMS-1.50</dlna:X_DLNADOC>
    <dlna:X_DLNACAP>av-upload,image-upload,audio-upload</dlna:X_DLNACAP>
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
      <name>Search</name>
      <argumentList>
        <argument><name>ContainerID</name><direction>in</direction><relatedStateVariable>A_ARG_TYPE_ObjectID</relatedStateVariable></argument>
        <argument><name>SearchCriteria</name><direction>in</direction><relatedStateVariable>A_ARG_TYPE_SearchCriteria</relatedStateVariable></argument>
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
      <name>GetSearchCapabilities</name>
      <argumentList>
        <argument><name>SearchCaps</name><direction>out</direction><relatedStateVariable>SearchCapabilities</relatedStateVariable></argument>
      </argumentList>
    </action>
    <action>
      <name>GetSortCapabilities</name>
      <argumentList>
        <argument><name>SortCaps</name><direction>out</direction><relatedStateVariable>SortCapabilities</relatedStateVariable></argument>
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
    <stateVariable sendEvents="no"><name>A_ARG_TYPE_BrowseFlag</name><dataType>string</dataType></stateVariable>
    <stateVariable sendEvents="no"><name>A_ARG_TYPE_Filter</name><dataType>string</dataType></stateVariable>
    <stateVariable sendEvents="no"><name>A_ARG_TYPE_SortCriteria</name><dataType>string</dataType></stateVariable>
    <stateVariable sendEvents="no"><name>A_ARG_TYPE_Index</name><dataType>ui4</dataType></stateVariable>
    <stateVariable sendEvents="no"><name>A_ARG_TYPE_Count</name><dataType>ui4</dataType></stateVariable>
    <stateVariable sendEvents="no"><name>A_ARG_TYPE_UpdateID</name><dataType>ui4</dataType></stateVariable>
    <stateVariable sendEvents="no"><name>A_ARG_TYPE_SearchCriteria</name><dataType>string</dataType></stateVariable>
    <stateVariable sendEvents="yes"><name>SearchCapabilities</name><dataType>string</dataType></stateVariable>
    <stateVariable sendEvents="yes"><name>SortCapabilities</name><dataType>string</dataType></stateVariable>
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
    proxy_url = f"http://{LOCAL_IP}:{PORT}/stream/{cid}/live.m3u8"
    logo = ch.get("logo", "")
    if logo and not logo.startswith("http"):
        logo = f"https://dor2500.github.io/israel-tv-plus/{logo.lstrip('/')}"
    logo_tag = f"<upnp:albumArtURI>{xml_escape(logo)}</upnp:albumArtURI>" if logo else ""

    upnp_class = "object.item.audioItem.audioBroadcast" if is_radio else "object.item.videoItem.videoBroadcast"

    # Direct HLS is primary for LG webOS; proxy and generic protocolInfo as fallbacks
    return (
        f'<item id="ch_{cid}" parentID="{parent_id}" restricted="1">'
        f'<dc:title>{name}</dc:title>'
        f'<upnp:class>{upnp_class}</upnp:class>'
        f'{logo_tag}'
        f'<res protocolInfo="http-get:*:application/vnd.apple.mpegurl:*">{xml_escape(stream_url)}</res>'
        f'<res protocolInfo="http-get:*:video/mp2t:*">{xml_escape(stream_url)}</res>'
        f'<res protocolInfo="http-get:*:video/mp4:*">{xml_escape(stream_url)}</res>'
        f'<res protocolInfo="http-get:*:*">{xml_escape(stream_url)}</res>'
        f'<res protocolInfo="http-get:*:application/vnd.apple.mpegurl:*">{xml_escape(proxy_url)}</res>'
        f'</item>'
    )


def build_container_didl(cid, title, parent_id="0", child_count=0):
    return (
        f'<container id="{cid}" parentID="{parent_id}" restricted="1" childCount="{child_count}">'
        f'<dc:title>{xml_escape(title)}</dc:title>'
        f'<upnp:class>object.container.storageFolder</upnp:class>'
        f'</container>'
    )


def handle_browse_or_search(obj_id, flag="BrowseDirectChildren", starting_idx=0, req_count=0, is_search=False):
    items = []

    log_event(f"handle_browse_or_search: obj_id='{obj_id}', flag='{flag}', is_search={is_search}, start={starting_idx}, count={req_count}")

    root_ids = ["0", "2", "video", "0$2", "0/video", "-1", "0$1$2", "0$1$3", "0$1"]

    if flag == "BrowseMetadata":
        if obj_id in root_ids:
            items.append(build_container_didl(obj_id, "ישראל TV+ (שידורים חיים)", "-1", len(CHANNELS)))
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
    elif is_search:
        # Search returns video items directly
        for ch in CHANNELS:
            items.append(build_channel_didl(ch, "0"))
        total = len(items)
    else:  # BrowseDirectChildren
        # If root or video container:
        # Provide the category folders AND all channel items directly,
        # so whether the TV expects folders or items, they appear immediately!
        if obj_id in root_ids:
            # Folders first
            items.append(build_container_didl("cat_all", f"⭐ כל הערוצים ({len(CHANNELS)})", obj_id, len(CHANNELS)))
            for cat in CATEGORIES:
                sub = [c for c in CHANNELS if c.get("category") == cat]
                items.append(build_container_didl(f"cat_{cat}", f"{cat} ({len(sub)})", obj_id, len(sub)))
            # Also append all channels directly to root
            for ch in CHANNELS:
                items.append(build_channel_didl(ch, obj_id))
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
        else:
            total = 0

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
        log_event(f"HTTP: {self.client_address[0]} {self.command} {self.path} - {args[0] if args else ''}")

    def do_HEAD(self):
        log_event(f"HEAD {self.path} from {self.client_address[0]}")
        parsed = urllib.parse.urlparse(self.path)
        path = parsed.path

        if "/stream/" in path:
            self.send_response(200)
            self.send_header("Content-Type", "application/vnd.apple.mpegurl")
            self.send_header("Accept-Ranges", "bytes")
            self.send_header("Access-Control-Allow-Origin", "*")
            self.send_header("transferMode.dlna.org", "Streaming")
            self.end_headers()
            return

        self.send_response(200)
        self.send_header("Content-Type", "text/xml; charset=utf-8")
        self.end_headers()

    def do_GET(self):
        log_event(f"GET {self.path} from {self.client_address[0]}")
        parsed = urllib.parse.urlparse(self.path)
        path = parsed.path

        if path in ["/description.xml", "/"]:
            body = DEVICE_DESCRIPTION.encode("utf-8")
            self.send_response(200)
            self.send_header("Content-Type", "text/xml; charset=utf-8")
            self.send_header("Content-Length", str(len(body)))
            self.send_header("Access-Control-Allow-Origin", "*")
            self.send_header("SERVER", "Windows/10 UPnP/1.0 DLNADOC/1.50 IsraelTVPlus/4.8.12")
            self.end_headers()
            self.wfile.write(body)
            return

        if path == "/content_directory.xml":
            body = CONTENT_DIRECTORY_SCPD.encode("utf-8")
            self.send_response(200)
            self.send_header("Content-Type", "text/xml; charset=utf-8")
            self.send_header("Content-Length", str(len(body)))
            self.send_header("Access-Control-Allow-Origin", "*")
            self.end_headers()
            self.wfile.write(body)
            return

        if path == "/connection_manager.xml":
            body = CONNECTION_MANAGER_SCPD.encode("utf-8")
            self.send_response(200)
            self.send_header("Content-Type", "text/xml; charset=utf-8")
            self.send_header("Content-Length", str(len(body)))
            self.send_header("Access-Control-Allow-Origin", "*")
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
            parts = [p for p in path.split("/") if p and p != "stream"]
            cid = parts[0] if parts else ""
            if cid in CH_MAP:
                stream_url = CH_MAP[cid].get("url", "")
                # Proxy the manifest directly instead of 302 redirect for LG webOS DLNA compliance
                try:
                    req = urllib.request.Request(stream_url, headers={"User-Agent": "Mozilla/5.0 IsraelTVPlus/4.8.12"})
                    with urllib.request.urlopen(req, timeout=8) as upstream:
                        data = upstream.read()
                        self.send_response(200)
                        self.send_header("Content-Type", "application/vnd.apple.mpegurl")
                        self.send_header("Content-Length", str(len(data)))
                        self.send_header("Access-Control-Allow-Origin", "*")
                        self.send_header("transferMode.dlna.org", "Streaming")
                        self.end_headers()
                        self.wfile.write(data)
                        return
                except Exception as e:
                    log_event(f"Stream proxy error for {cid}: {e}")
                    # Fallback redirect if upstream fetch fails
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

    def do_SUBSCRIBE(self):
        log_event(f'SUBSCRIBE {self.path} from {self.client_address[0]}')
        self.send_response(200)
        self.send_header('SID', f'uuid:{UUID}-sub')
        self.send_header('TIMEOUT', 'Second-1800')
        self.send_header('Content-Length', '0')
        self.end_headers()

    def do_UNSUBSCRIBE(self):
        log_event(f'UNSUBSCRIBE {self.path} from {self.client_address[0]}')
        self.send_response(200)
        self.send_header('Content-Length', '0')
        self.end_headers()

    def do_POST(self):
        length = int(self.headers.get("Content-Length", 0))
        body = self.rfile.read(length).decode("utf-8", errors="ignore")
        log_event(f"SOAP POST: {self.path} from {self.client_address[0]} - Action: {self.headers.get('SOAPAction', '')}")

        if "/control/ContentDirectory" in self.path:
            soap_action = self.headers.get("SOAPAction", "")
            if "GetSearchCapabilities" in soap_action or "GetSearchCapabilities" in body:
                resp = """<?xml version="1.0" encoding="utf-8"?>
<s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/">
  <s:Body>
    <u:GetSearchCapabilitiesResponse xmlns:u="urn:schemas-upnp-org:service:ContentDirectory:1">
      <SearchCaps>dc:title,upnp:class</SearchCaps>
    </u:GetSearchCapabilitiesResponse>
  </s:Body>
</s:Envelope>"""
            elif "GetSortCapabilities" in soap_action or "GetSortCapabilities" in body:
                resp = """<?xml version="1.0" encoding="utf-8"?>
<s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/">
  <s:Body>
    <u:GetSortCapabilitiesResponse xmlns:u="urn:schemas-upnp-org:service:ContentDirectory:1">
      <SortCaps>dc:title</SortCaps>
    </u:GetSortCapabilitiesResponse>
  </s:Body>
</s:Envelope>"""
            elif "Search" in soap_action or "<u:Search" in body:
                m_cid = re.search(r"<ContainerID>([^<]+)</ContainerID>", body)
                cid = m_cid.group(1).strip() if m_cid else "0"
                m_idx = re.search(r"<StartingIndex>(\d+)</StartingIndex>", body)
                starting_idx = int(m_idx.group(1)) if m_idx else 0
                m_count = re.search(r"<RequestedCount>(\d+)</RequestedCount>", body)
                req_count = int(m_count.group(1)) if m_count else 0

                didl, count, total = handle_browse_or_search(cid, starting_idx=starting_idx, req_count=req_count, is_search=True)
                escaped_didl = xml_escape(didl)
                resp = f"""<?xml version="1.0" encoding="utf-8"?>
<s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/">
  <s:Body>
    <u:SearchResponse xmlns:u="urn:schemas-upnp-org:service:ContentDirectory:1">
      <Result>{escaped_didl}</Result>
      <NumberReturned>{count}</NumberReturned>
      <TotalMatches>{total}</TotalMatches>
      <UpdateID>1</UpdateID>
    </u:SearchResponse>
  </s:Body>
</s:Envelope>"""
            else:  # Browse
                m_id = re.search(r"<ObjectID>([^<]+)</ObjectID>", body)
                obj_id = m_id.group(1).strip() if m_id else "0"
                m_flag = re.search(r"<BrowseFlag>([^<]+)</BrowseFlag>", body)
                flag = m_flag.group(1).strip() if m_flag else "BrowseDirectChildren"
                m_idx = re.search(r"<StartingIndex>(\d+)</StartingIndex>", body)
                starting_idx = int(m_idx.group(1)) if m_idx else 0
                m_count = re.search(r"<RequestedCount>(\d+)</RequestedCount>", body)
                req_count = int(m_count.group(1)) if m_count else 0

                didl, count, total = handle_browse_or_search(obj_id, flag, starting_idx, req_count)
                escaped_didl = xml_escape(didl)
                resp = f"""<?xml version="1.0" encoding="utf-8"?>
<s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/">
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
            proto = "http-get:*:application/vnd.apple.mpegurl:*,http-get:*:video/mp2t:*,http-get:*:video/mp4:*,http-get:*:audio/mpeg:*"
            resp = f"""<?xml version="1.0" encoding="utf-8"?>
<s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/">
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
    sender_sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM, socket.IPPROTO_UDP)
    sender_sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    sender_sock.setsockopt(socket.SOL_SOCKET, socket.SO_BROADCAST, 1)
    try:
        sender_sock.bind((LOCAL_IP, 0))
        sender_sock.setsockopt(socket.IPPROTO_IP, socket.IP_MULTICAST_IF, socket.inet_aton(LOCAL_IP))
        sender_sock.setsockopt(socket.IPPROTO_IP, socket.IP_MULTICAST_TTL, 4)
    except Exception as e:
        log_event(f"[SSDP Sender Bind Warning] {e}")

    listen_sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM, socket.IPPROTO_UDP)
    listen_sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    try:
        listen_sock.bind(("", SSDP_PORT))
        mreq = struct.pack("4s4s", socket.inet_aton(SSDP_ADDR), socket.inet_aton(LOCAL_IP))
        listen_sock.setsockopt(socket.IPPROTO_IP, socket.IP_ADD_MEMBERSHIP, mreq)
    except Exception as e:
        log_event(f"[SSDP Listener Bind Warning] {e}")

    log_event(f"[SSDP] Active on LAN {LOCAL_IP} (broadcast: {BROADCAST_ADDR})...")

    def send_notify_burst():
        targets = [
            "upnp:rootdevice",
            f"uuid:{UUID}",
            "urn:schemas-upnp-org:device:MediaServer:1",
            "urn:schemas-upnp-org:service:ContentDirectory:1",
            "urn:schemas-upnp-org:service:ConnectionManager:1",
        ]
        destinations = [(SSDP_ADDR, SSDP_PORT), (BROADCAST_ADDR, SSDP_PORT)] + [(ip, SSDP_PORT) for ip in KNOWN_TARGETS]

        while True:
            for nt in targets:
                usn = nt if nt == f"uuid:{UUID}" else f"uuid:{UUID}::{nt}"
                msg = (
                    f"NOTIFY * HTTP/1.1\r\n"
                    f"HOST: {SSDP_ADDR}:{SSDP_PORT}\r\n"
                    f"CACHE-CONTROL: max-age=1800\r\n"
                    f"LOCATION: http://{LOCAL_IP}:{PORT}/description.xml\r\n"
                    f"NT: {nt}\r\n"
                    f"NTS: ssdp:alive\r\n"
                    f"SERVER: Windows/10 UPnP/1.0 DLNADOC/1.50 IsraelTVPlus/4.8.12\r\n"
                    f"USN: {usn}\r\n\r\n"
                ).encode("utf-8")
                for dest in destinations:
                    try:
                        sender_sock.sendto(msg, dest)
                    except Exception:
                        pass
            time.sleep(10)

    threading.Thread(target=send_notify_burst, daemon=True).start()

    while True:
        try:
            data, addr = listen_sock.recvfrom(4096)
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
                    usn = resp_st if resp_st == f"uuid:{UUID}" else f"uuid:{UUID}::{resp_st}"
                    reply = (
                        f"HTTP/1.1 200 OK\r\n"
                        f"CACHE-CONTROL: max-age=1800\r\n"
                        f"DATE: {time.strftime('%a, %d %b %Y %H:%M:%S GMT', time.gmtime())}\r\n"
                        f"EXT:\r\n"
                        f"LOCATION: http://{LOCAL_IP}:{PORT}/description.xml\r\n"
                        f"SERVER: Windows/10 UPnP/1.0 DLNADOC/1.50 IsraelTVPlus/4.8.12\r\n"
                        f"ST: {resp_st}\r\n"
                        f"USN: {usn}\r\n\r\n"
                    ).encode("utf-8")
                    try:
                        sender_sock.sendto(reply, addr)
                        log_event(f"Responded to M-SEARCH from {addr[0]} for {resp_st}")
                    except Exception:
                        pass
        except Exception:
            pass


def main():
    log_event("================================================================")
    log_event("       Israel TV+ LG-Optimized DLNA Server v4.8.12              ")
    log_event("================================================================")
    log_event(f"LAN IP:       {LOCAL_IP}")
    log_event(f"Channels:     {len(CHANNELS)} ready")
    log_event(f"HTTP Port:    {PORT}")
    log_event(f"Description:  http://{LOCAL_IP}:{PORT}/description.xml")

    threading.Thread(target=run_ssdp, daemon=True).start()

    server = HTTPServer(("0.0.0.0", PORT), DlnaHandler)
    log_event(f"HTTP Server listening on 0.0.0.0:{PORT}")
    try:
        server.serve_forever()
    except KeyboardInterrupt:
        server.shutdown()


if __name__ == "__main__":
    main()

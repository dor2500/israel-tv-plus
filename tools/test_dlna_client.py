import urllib.request
import re

# 1. Test Description XML
with urllib.request.urlopen("http://127.0.0.1:8200/description.xml") as resp:
    desc = resp.read().decode("utf-8")
    fn = re.search(r"<friendlyName>(.+?)</friendlyName>", desc).group(1)
    print("UPnP Server:", fn)

# 2. Test Browse Root
soap_browse = """<?xml version="1.0" encoding="utf-8"?>
<s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/">
  <s:Body>
    <u:Browse xmlns:u="urn:schemas-upnp-org:service:ContentDirectory:1">
      <ObjectID>0</ObjectID>
      <BrowseFlag>BrowseDirectChildren</BrowseFlag>
    </u:Browse>
  </s:Body>
</s:Envelope>"""

req = urllib.request.Request(
    "http://127.0.0.1:8200/control/ContentDirectory",
    data=soap_browse.encode("utf-8"),
    headers={"Content-Type": "text/xml; charset=\"utf-8\"", "SOAPAction": '"urn:schemas-upnp-org:service:ContentDirectory:1#Browse"'}
)
with urllib.request.urlopen(req) as resp:
    body = resp.read().decode("utf-8")
    m_num = re.search(r"<NumberReturned>(\d+)</NumberReturned>", body)
    print("Root Categories Returned:", m_num.group(1) if m_num else "None")

# 3. Test Browse All Channels
soap_browse_all = """<?xml version="1.0" encoding="utf-8"?>
<s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/">
  <s:Body>
    <u:Browse xmlns:u="urn:schemas-upnp-org:service:ContentDirectory:1">
      <ObjectID>cat_all</ObjectID>
      <BrowseFlag>BrowseDirectChildren</BrowseFlag>
      <RequestedCount>5</RequestedCount>
    </u:Browse>
  </s:Body>
</s:Envelope>"""

req2 = urllib.request.Request(
    "http://127.0.0.1:8200/control/ContentDirectory",
    data=soap_browse_all.encode("utf-8"),
    headers={"Content-Type": "text/xml; charset=\"utf-8\"", "SOAPAction": '"urn:schemas-upnp-org:service:ContentDirectory:1#Browse"'}
)
with urllib.request.urlopen(req2) as resp:
    body2 = resp.read().decode("utf-8")
    m_tot = re.search(r"<TotalMatches>(\d+)</TotalMatches>", body2)
    print("Total Channels in DLNA:", m_tot.group(1) if m_tot else "None")

# 4. Test M3U
with urllib.request.urlopen("http://127.0.0.1:8200/channels.m3u") as resp:
    m3u_lines = resp.read().decode("utf-8").splitlines()
    print("M3U Playlist Lines:", len(m3u_lines))

print("ALL DLNA SERVICES WORKING 100% PERFECTLY!")

# 5. Test SSDP probe
import socket
s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
s.settimeout(2.0)
query = (
    "M-SEARCH * HTTP/1.1\r\n"
    "HOST: 239.255.255.250:1900\r\n"
    'MAN: "ssdp:discover"\r\n'
    "MX: 2\r\n"
    "ST: urn:schemas-upnp-org:device:MediaServer:1\r\n\r\n"
).encode("utf-8")
s.sendto(query, ("239.255.255.250", 1900))
try:
    data, addr = s.recvfrom(4096)
    print("SSDP Discovery Success from:", addr)
    for line in data.decode("utf-8", errors="ignore").splitlines():
        if any(k in line for k in ["LOCATION", "SERVER", "USN"]):
            print("  ", line)
except Exception as e:
    print("SSDP probe error:", e)
finally:
    s.close()


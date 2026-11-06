import urllib.request, json, re

urls = [
    "https://raw.githubusercontent.com/Free-TV/IPTV/master/playlist.m3u8",
    "https://raw.githubusercontent.com/iptv-org/iptv/master/streams/il.m3u",
    "https://raw.githubusercontent.com/iptv-org/iptv/master/streams/il_partner.m3u",
    "https://raw.githubusercontent.com/iptv-org/iptv/master/streams/il_sting.m3u",
    "https://raw.githubusercontent.com/iptv-org/iptv/master/streams/il_yes.m3u",
    "https://raw.githubusercontent.com/iptv-org/iptv/master/streams/il_hot.m3u",
    "https://raw.githubusercontent.com/Yanir-Avraham/Israel-TV-List/main/playlist.m3u",
    "https://raw.githubusercontent.com/Yanir-Avraham/Israel-TV-List/master/playlist.m3u",
    "https://raw.githubusercontent.com/kodi-il/kodi-il-repo/master/playlist.m3u",
    "https://raw.githubusercontent.com/Omer-K/Israel-IPTV/main/playlist.m3u"
]

for u in urls:
    try:
        req = urllib.request.Request(u, headers={"User-Agent": "Mozilla/5.0"})
        txt = urllib.request.urlopen(req, timeout=4).read().decode('utf-8', errors='ignore')
        print(f"Loaded {u} ({len(txt)} bytes)")
        for line in txt.splitlines():
            if any(k in line.lower() for k in ["nick", "ניק"]):
                print("   ", line)
    except Exception as e:
        # print(f"Failed {u}: {e}")
        pass


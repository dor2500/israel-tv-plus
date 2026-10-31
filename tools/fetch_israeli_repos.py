import urllib.request

urls = [
    "https://raw.githubusercontent.com/RokuIL/Live-From-Israel/master/playlist.m3u",
    "https://raw.githubusercontent.com/RokuIL/Live-From-Israel/master/israel.m3u",
    "https://raw.githubusercontent.com/szaionz/zipzup/master/channels.json",
    "https://raw.githubusercontent.com/szaionz/zipzup/main/channels.json"
]

for u in urls:
    try:
        req = urllib.request.Request(u, headers={'User-Agent': 'Mozilla/5.0'})
        with urllib.request.urlopen(req, timeout=8) as r:
            data = r.read(2048).decode('utf-8', 'ignore')
            print(f"[OK 200] {u}: {len(data)} bytes")
    except Exception as e:
        print(f"[FAIL] {u}: {e}")

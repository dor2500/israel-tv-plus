import json, urllib.request

UA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0 Safari/537.36"

with open("channels.json", encoding="utf-8") as f:
    chs = json.load(f)

pluto_channels = []
for c in chs:
    url = c.get("url", "")
    src_by = c.get("src", {}).get("by", "")
    if "jmp2.uk/plu-" in url or "pluto" in url.lower() or "pluto" in src_by.lower():
        pluto_channels.append(c)

print(f"Total Pluto channels found: {len(pluto_channels)}")
for c in pluto_channels:
    print(f" - {c['name']} ({c['id']}) -> {c['url']}")

def test_stream(c):
    url = c["url"]
    try:
        req = urllib.request.Request(url, headers={"User-Agent": UA})
        with urllib.request.urlopen(req, timeout=7) as r:
            if r.status != 200:
                return False, f"HTTP {r.status}"
            content = r.read(1024).decode("utf-8", "ignore")
            if "#EXTM3U" not in content:
                return False, "Not valid M3U8"
            return True, "OK"
    except Exception as e:
        return False, str(e)

print("\nTesting all Pluto streams...")
for c in pluto_channels:
    ok, msg = test_stream(c)
    print(f"[{'PASS' if ok else 'FAIL'}] {c['name']}: {msg}")


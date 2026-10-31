import json, urllib.request, time

UA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0 Safari/537.36"

with open("channels.json", encoding="utf-8") as f:
    chs = json.load(f)

pluto_chs = [c for c in chs if "jmp2.uk/plu-" in c["url"] or "service-stitcher.clusters.pluto.tv" in c["url"]]
print(f"Testing {len(pluto_chs)} Pluto channels...")

for c in pluto_chs:
    url = c["url"]
    try:
        req = urllib.request.Request(url, headers={"User-Agent": UA})
        with urllib.request.urlopen(req, timeout=5) as resp:
            content = resp.read(2048).decode("utf-8", "ignore")
            # Check if it has segments
            lines = [l.strip() for l in content.splitlines() if l.strip() and not l.startswith('#')]
            if not lines:
                print(f"[EMPTY] {c['name']} ({c['id']})")
                continue
            # Try loading first sub-manifest/segment
            sub_url = lines[0]
            if not sub_url.startswith("http"):
                from urllib.parse import urljoin
                sub_url = urljoin(url, sub_url)
            req2 = urllib.request.Request(sub_url, headers={"User-Agent": UA})
            with urllib.request.urlopen(req2, timeout=5) as resp2:
                if resp2.status == 200:
                    print(f"[VERIFIED] {c['name']} ({c['id']}) -> {sub_url[:50]}")
                else:
                    print(f"[SUB-ERROR {resp2.status}] {c['name']} ({c['id']})")
    except Exception as e:
        print(f"[ERROR] {c['name']} ({c['id']}): {e}")


import os, subprocess, json, urllib.request

candidates = [
    2330, 2331, 2332, 2333, 2334, 2335, 2336, 2337, 2338, 2339,
    2340, 2341, 2342, 2343, 2344, 2345, 2346, 2347, 2348, 2349,
    2350, 2351, 2352, 2353, 2354, 2355, 2356, 2357, 2358, 2359,
    2360, 2361, 2362, 2363, 2364, 2365, 2366, 2367, 2368, 2369,
    12238, 12239, 12240, 12241, 12242, 12243, 12244, 12245, 12246, 12247,
    32145, 32146, 32147, 32148, 32149, 32150, 32151, 32152, 32153, 32154
]

base_url = "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/{id}/index.m3u8"
out_dir = r"C:\Users\Dorp\.gemini\antigravity\brain\b5ac2590-7531-422b-ba90-f78b966584c0\nick_probes"
os.makedirs(out_dir, exist_ok=True)

live = []
for cid in candidates:
    url = base_url.format(id=cid)
    try:
        req = urllib.request.Request(url, headers={"User-Agent": "Mozilla/5.0"})
        with urllib.request.urlopen(req, timeout=3) as res:
            if res.status == 200:
                body = res.read().decode('utf-8', errors='ignore')
                if "#EXTM3U" in body:
                    print(f"ID {cid} is ACTIVE HLS!")
                    live.append(cid)
    except Exception:
        pass

print(f"Total active candidates: {len(live)}")
with open(os.path.join(out_dir, "live_ids.json"), "w") as f:
    json.dump(live, f)


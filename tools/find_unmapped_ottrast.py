import os, json, urllib.request, concurrent.futures

base_url = "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/{id}/index.m3u8"

all_ids = []
all_ids.extend(range(2320, 2395))
all_ids.extend(range(12200, 12290))
all_ids.extend(range(32120, 32175))

def check_id(cid):
    url = base_url.format(id=cid)
    try:
        req = urllib.request.Request(url, headers={"User-Agent": "Mozilla/5.0"})
        with urllib.request.urlopen(req, timeout=3) as res:
            if res.status == 200:
                body = res.read().decode('utf-8', errors='ignore')
                if "#EXTM3U" in body:
                    return cid
    except Exception:
        pass
    return None

print(f"Checking {len(all_ids)} IDs...")
active = []
with concurrent.futures.ThreadPoolExecutor(max_workers=20) as ex:
    for cid in ex.map(check_id, all_ids):
        if cid:
            active.append(cid)

print(f"Total active IDs on ottrast: {len(active)}")

# Load mapped
with open(r"C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo\channels.json", "r", encoding="utf-8") as f:
    chs = json.load(f)

mapped = set()
for c in chs:
    u = c.get("url", "")
    if "ottrast.com" in u:
        cid = int(u.split("/")[-2])
        mapped.add(cid)

unmapped = [cid for cid in active if cid not in mapped]
print(f"Unmapped active IDs ({len(unmapped)}): {unmapped}")

with open(r"C:\Users\Dorp\.gemini\antigravity\brain\b5ac2590-7531-422b-ba90-f78b966584c0\nick_probes\unmapped.json", "w") as f:
    json.dump(unmapped, f)

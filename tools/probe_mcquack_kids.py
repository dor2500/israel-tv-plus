import urllib.request, concurrent.futures, subprocess, os

base_url = "http://stream.mcquack.net/{id}/index.m3u8"
out_dir = r"C:\Users\Dorp\.gemini\antigravity\brain\b5ac2590-7531-422b-ba90-f78b966584c0\nick_probes\mcquack"
os.makedirs(out_dir, exist_ok=True)

def probe(cid):
    u = base_url.format(id=cid)
    try:
        req = urllib.request.Request(u, headers={"User-Agent": "Mozilla/5.0"})
        with urllib.request.urlopen(req, timeout=2.5) as r:
            if r.status == 200:
                dest = os.path.join(out_dir, f"{cid}.jpg")
                cmd = ["ffmpeg", "-y", "-t", "3", "-i", u, "-vf", "scale=640:-1", "-vframes", "1", "-q:v", "3", dest]
                subprocess.run(cmd, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL, timeout=8)
                return cid
    except Exception:
        pass
    return None

print("Probing mcquack 1..500...")
with concurrent.futures.ThreadPoolExecutor(max_workers=25) as ex:
    active = [c for c in ex.map(probe, range(1, 501)) if c]

print(f"Active mcquack streams: {len(active)}")


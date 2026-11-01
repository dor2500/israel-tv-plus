import os, subprocess, concurrent.futures, json

with open(r"C:\Users\Dorp\.gemini\antigravity\brain\b5ac2590-7531-422b-ba90-f78b966584c0\nick_probes\unmapped.json", "r") as f:
    unmapped = json.load(f)

out_dir = r"C:\Users\Dorp\.gemini\antigravity\brain\b5ac2590-7531-422b-ba90-f78b966584c0\nick_probes\frames"
os.makedirs(out_dir, exist_ok=True)

base_url = "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/{id}/index.m3u8"

def capture_one(cid):
    dest = os.path.join(out_dir, f"{cid}.jpg")
    if os.path.exists(dest) and os.path.getsize(dest) > 1000:
        return cid, True
    cmd = [
        "ffmpeg", "-y", "-t", "4", "-i", base_url.format(id=cid),
        "-vf", "scale=640:-1", "-vframes", "1", "-q:v", "3", dest
    ]
    try:
        p = subprocess.run(cmd, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL, timeout=10)
        if p.returncode == 0 and os.path.exists(dest) and os.path.getsize(dest) > 1000:
            return cid, True
    except Exception:
        pass
    return cid, False

print(f"Capturing frames for {len(unmapped)} unmapped channels...")
with concurrent.futures.ThreadPoolExecutor(max_workers=16) as ex:
    results = list(ex.map(capture_one, unmapped))

success = [cid for cid, ok in results if ok]
print(f"Successfully captured {len(success)} frames!")


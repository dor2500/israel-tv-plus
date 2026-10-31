import urllib.request, concurrent.futures, os, subprocess

ranges = []
ranges.extend(range(7100, 7250))
ranges.extend(range(19180, 19250))
ranges.extend(range(32175, 32350))
ranges.extend(range(2390, 2450))

base_url = "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/{id}/index.m3u8"
out_dir = r"C:\Users\Dorp\.gemini\antigravity\brain\b5ac2590-7531-422b-ba90-f78b966584c0\nick_probes\more_frames"
os.makedirs(out_dir, exist_ok=True)

def check_one(cid):
    url = base_url.format(id=cid)
    try:
        req = urllib.request.Request(url, headers={"User-Agent": "Mozilla/5.0"})
        with urllib.request.urlopen(req, timeout=2.5) as res:
            if res.status == 200:
                body = res.read().decode('utf-8', errors='ignore')
                if "#EXTM3U" in body:
                    # capture frame
                    dest = os.path.join(out_dir, f"{cid}.jpg")
                    cmd = ["ffmpeg", "-y", "-t", "3", "-i", url, "-vf", "scale=640:-1", "-vframes", "1", "-q:v", "3", dest]
                    subprocess.run(cmd, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL, timeout=8)
                    return cid
    except Exception:
        pass
    return None

print(f"Scanning {len(ranges)} more IDs...")
with concurrent.futures.ThreadPoolExecutor(max_workers=20) as ex:
    results = [cid for cid in ex.map(check_one, ranges) if cid]

print(f"Found {len(results)} active streams in extra ranges: {results}")

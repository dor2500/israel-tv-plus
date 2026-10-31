import os, subprocess, concurrent.futures

base_url = "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/{id}/index.m3u8"
out_dir = r"C:\Users\Dorp\.gemini\antigravity\brain\b5ac2590-7531-422b-ba90-f78b966584c0\nick_frames"
os.makedirs(out_dir, exist_ok=True)

test_ids = [
    2330, 2331, 2332, 2333, 2334, 2335, 2336, 2337, 2338, 2339,
    2340, 2341, 2342, 2343, 2344, 2345, 2356, 2357, 2358, 2359,
    2360, 2364, 2365, 2366,
    12238, 12239, 12240, 12241, 12242, 12243, 12244, 12245,
    32145, 32146, 32147, 32148, 32149, 32150, 32151, 32152, 32153, 32154
]

def capture(cid):
    url = base_url.format(id=cid)
    img_path = os.path.join(out_dir, f"frame_{cid}.jpg")
    cmd = [
        "ffmpeg", "-y", "-t", "5", "-i", url,
        "-vf", "scale=640:-1", "-vframes", "1", "-q:v", "3", img_path
    ]
    try:
        p = subprocess.run(cmd, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL, timeout=12)
        if p.returncode == 0 and os.path.exists(img_path) and os.path.getsize(img_path) > 1000:
            return cid, True
    except Exception:
        pass
    return cid, False

print("Starting frame captures in parallel...")
with concurrent.futures.ThreadPoolExecutor(max_workers=8) as ex:
    results = ex.map(capture, test_ids)
    for cid, ok in results:
        if ok:
            print(f"Captured frame for {cid}")

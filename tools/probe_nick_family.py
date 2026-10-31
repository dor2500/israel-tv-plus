import subprocess, os

base_url = "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/{id}/index.m3u8"
out_dir = r"C:\Users\Dorp\.gemini\antigravity\brain\b5ac2590-7531-422b-ba90-f78b966584c0\nick_probes\audio"
os.makedirs(out_dir, exist_ok=True)

# Let's check IDs in the 32270-32320 range or 2330-2340 or other ranges
check_ids = [
    32270, 32271, 32272, 32273, 32274, 32275, 32276, 32277, 32278, 32279,
    32280, 32281, 32282, 32283, 32284, 32285, 32286, 32287, 32288, 32289
]

for cid in check_ids:
    url = base_url.format(id=cid)
    img_dest = os.path.join(out_dir, f"{cid}.jpg")
    cmd = ["ffmpeg", "-y", "-t", "3", "-i", url, "-vf", "scale=640:-1", "-vframes", "1", "-q:v", "3", img_dest]
    p = subprocess.run(cmd, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL, timeout=8)
    if os.path.exists(img_dest) and os.path.getsize(img_dest) > 1000:
        print(f"Captured {cid}")

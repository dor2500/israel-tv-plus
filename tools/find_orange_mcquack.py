import os, glob
from PIL import Image

frames_dir = r"C:\Users\Dorp\.gemini\antigravity\brain\b5ac2590-7531-422b-ba90-f78b966584c0\nick_probes\mcquack"
matches = []

for f in sorted(glob.glob(os.path.join(frames_dir, "*.jpg"))):
    cid = os.path.basename(f).replace(".jpg", "")
    try:
        im = Image.open(f).convert("RGB")
        w, h = im.size
        corners = [
            im.crop((int(w * 0.75), 0, w, int(h * 0.3))), # top-right
            im.crop((0, 0, int(w * 0.25), int(h * 0.3))), # top-left
        ]
        orange_count = 0
        for c in corners:
            for r, g, b in c.getdata():
                if r > 200 and 80 < g < 165 and b < 60:
                    orange_count += 1
        if orange_count > 60:
            matches.append((cid, orange_count))
    except Exception:
        pass

matches.sort(key=lambda x: x[1], reverse=True)
print(f"Candidate IDs with orange logo elements in mcquack:")
for cid, cnt in matches[:20]:
    print(f"ID {cid}: {cnt} orange pixels")

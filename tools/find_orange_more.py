import os, glob
from PIL import Image

frames_dir = r"C:\Users\Dorp\.gemini\antigravity\brain\b5ac2590-7531-422b-ba90-f78b966584c0\nick_probes\more_frames"
matches = []

for f in sorted(glob.glob(os.path.join(frames_dir, "*.jpg"))):
    cid = os.path.basename(f).replace(".jpg", "")
    try:
        im = Image.open(f).convert("RGB")
        w, h = im.size
        corners = [
            im.crop((int(w * 0.75), 0, w, int(h * 0.3))), # top-right
            im.crop((0, 0, int(w * 0.25), int(h * 0.3))), # top-left
            im.crop((int(w * 0.75), int(h * 0.7), w, h)), # bottom-right
        ]
        orange_count = 0
        for c in corners:
            for r, g, b in c.getdata():
                # Nickelodeon orange
                if r > 200 and 80 < g < 165 and b < 60:
                    orange_count += 1
        if orange_count > 40:
            matches.append((cid, orange_count))
    except Exception:
        pass

matches.sort(key=lambda x: x[1], reverse=True)
print(f"Candidate IDs with orange logo elements in more_frames:")
for cid, cnt in matches[:25]:
    print(f"ID {cid}: {cnt} orange pixels")

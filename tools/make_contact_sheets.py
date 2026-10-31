import os, glob
from PIL import Image, ImageDraw, ImageFont

frames_dir = r"C:\Users\Dorp\.gemini\antigravity\brain\b5ac2590-7531-422b-ba90-f78b966584c0\nick_probes\frames"
out_dir = r"C:\Users\Dorp\.gemini\antigravity\brain\b5ac2590-7531-422b-ba90-f78b966584c0\nick_probes\sheets"
os.makedirs(out_dir, exist_ok=True)

files = sorted(glob.glob(os.path.join(frames_dir, "*.jpg")))
print(f"Total frame files: {len(files)}")

batch_size = 16
cell_w, cell_h = 320, 180

for b_idx in range(0, len(files), batch_size):
    batch = files[b_idx:b_idx+batch_size]
    sheet = Image.new("RGB", (cell_w * 4, cell_h * 4), (20, 20, 20))
    draw = ImageDraw.Draw(sheet)
    
    for i, f in enumerate(batch):
        cid = os.path.basename(f).replace(".jpg", "")
        row = i // 4
        col = i % 4
        try:
            im = Image.open(f).resize((cell_w, cell_h))
            sheet.paste(im, (col * cell_w, row * cell_h))
            draw.rectangle([col * cell_w + 5, row * cell_h + 5, col * cell_w + 100, row * cell_h + 35], fill=(0, 0, 0))
            draw.text((col * cell_w + 10, row * cell_h + 10), f"ID: {cid}", fill=(255, 255, 0))
        except Exception:
            pass
            
    out_path = os.path.join(out_dir, f"sheet_{b_idx // batch_size + 1}.jpg")
    sheet.save(out_path, quality=85)
    print(f"Saved {out_path}")

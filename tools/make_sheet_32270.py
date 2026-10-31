import os, glob
from PIL import Image, ImageDraw

audio_dir = r"C:\Users\Dorp\.gemini\antigravity\brain\b5ac2590-7531-422b-ba90-f78b966584c0\nick_probes\audio"
files = sorted(glob.glob(os.path.join(audio_dir, "*.jpg")))

cell_w, cell_h = 320, 180
cols, rows = 4, 5
sheet = Image.new("RGB", (cell_w * cols, cell_h * rows), (20, 20, 20))
draw = ImageDraw.Draw(sheet)

for i, f in enumerate(files):
    cid = os.path.basename(f).replace(".jpg", "")
    r = i // cols
    c = i % cols
    im = Image.open(f).resize((cell_w, cell_h))
    sheet.paste(im, (c * cell_w, r * cell_h))
    draw.rectangle([c * cell_w + 5, r * cell_h + 5, c * cell_w + 100, r * cell_h + 35], fill=(0, 0, 0))
    draw.text((c * cell_w + 10, r * cell_h + 10), f"ID: {cid}", fill=(255, 255, 0))

out_path = os.path.join(audio_dir, "sheet_32270.jpg")
sheet.save(out_path, quality=85)
print("Saved sheet:", out_path)

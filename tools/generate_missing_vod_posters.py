import os
from PIL import Image, ImageDraw, ImageFont

ROOT = r"C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo"
os.chdir(ROOT)

vod_dir = os.path.join(ROOT, "assets", "vod")
os.makedirs(vod_dir, exist_ok=True)

# Helper to create high-contrast, beautiful gradient posters
def create_poster(filename, title, subtitle, badge_text, theme_colors):
    w, h = 480, 720
    im = Image.new("RGB", (w, h), color=theme_colors[0])
    draw = ImageDraw.Draw(im)

    # Gradient background
    c1, c2, c3 = theme_colors
    for y in range(h):
        ratio = y / float(h)
        if ratio < 0.5:
            r1 = ratio * 2
            r = int(c1[0] * (1 - r1) + c2[0] * r1)
            g = int(c1[1] * (1 - r1) + c2[1] * r1)
            b = int(c1[2] * (1 - r1) + c2[2] * r1)
        else:
            r2 = (ratio - 0.5) * 2
            r = int(c2[0] * (1 - r2) + c3[0] * r2)
            g = int(c2[1] * (1 - r2) + c3[1] * r2)
            b = int(c2[2] * (1 - r2) + c3[2] * r2)
        draw.line([(0, y), (w, y)], fill=(r, g, b))

    # Inner decorative border
    draw.rounded_rectangle([16, 16, w - 16, h - 16], radius=24, outline=(255, 255, 255, 40), width=2)

    # Top badge
    badge_bg = (37, 99, 235)
    draw.rounded_rectangle([w // 2 - 70, 36, w // 2 + 70, 72], radius=18, fill=badge_bg)
    draw.text((w // 2, 54), badge_text, fill=(255, 255, 255), anchor="mm")

    # Center graphic
    if "ramzor" in filename:
        # Traffic light housing
        box_w, box_h = 100, 240
        bx = (w - box_w) // 2
        by = 130
        draw.rounded_rectangle([bx, by, bx + box_w, by + box_h], radius=28, fill=(15, 23, 42), outline=(255, 255, 255, 80), width=3)
        # Red, Yellow, Green lights
        draw.ellipse([bx + 18, by + 18, bx + box_w - 18, by + 82], fill=(239, 68, 68))
        draw.ellipse([bx + 18, by + 88, bx + box_w - 18, by + 152], fill=(234, 179, 8))
        draw.ellipse([bx + 18, by + 158, bx + box_w - 18, by + 222], fill=(34, 197, 94))
    elif "miller" in filename:
        # Comedy stage mic
        cx, cy = w // 2, 230
        draw.ellipse([cx - 45, cy - 65, cx + 45, cy + 25], fill=(59, 130, 246), outline=(255, 255, 255), width=3)
        draw.rectangle([cx - 12, cy + 25, cx + 12, cy + 110], fill=(148, 163, 184))
        draw.rectangle([cx - 50, cy + 110, cx + 50, cy + 125], fill=(203, 213, 225))
    elif "savta" in filename:
        # Military vehicle / running figure symbol
        cx, cy = w // 2, 230
        draw.rounded_rectangle([cx - 90, cy - 25, cx + 90, cy + 50], radius=16, fill=(180, 83, 9))
        draw.ellipse([cx - 70, cy + 30, cx - 30, cy + 70], fill=(30, 41, 59))
        draw.ellipse([cx + 30, cy + 30, cx + 70, cy + 70], fill=(30, 41, 59))
        draw.text((cx, cy + 10), "הכי מהר שלך", fill=(255, 255, 255), anchor="mm")

    # Bottom Title Container with glass effect
    bot_y = h - 220
    draw.rounded_rectangle([24, bot_y, w - 24, h - 32], radius=24, fill=(10, 15, 26))
    draw.rounded_rectangle([24, bot_y, w - 24, h - 32], radius=24, outline=(255, 255, 255, 50), width=2)

    # Title & Subtitle text
    draw.text((w // 2, bot_y + 48), title, fill=(248, 250, 252), anchor="mm")
    draw.text((w // 2, bot_y + 95), subtitle, fill=(148, 163, 184), anchor="mm")
    draw.text((w // 2, bot_y + 140), "FHD 1080p · קומדיה ישראלית", fill=(96, 165, 250), anchor="mm")

    dest = os.path.join(vod_dir, filename)
    im.save(dest, quality=92)
    print(f"Created poster: {dest}")

# 1. Ramzor
create_poster(
    "ramzor.jpg",
    "רמזור",
    "אדיר מילר · איצקו · חפר",
    "קומדיית השנה",
    [(30, 27, 75), (15, 23, 42), (2, 6, 23)]
)

# 2. Tzomet Miller
create_poster(
    "tzomet_miller.jpg",
    "צומת מילר",
    "אדיר מילר ומשפחתו",
    "עונות 1-4",
    [(12, 74, 110), (15, 23, 42), (2, 6, 23)]
)

# 3. Mivtza Savta
create_poster(
    "mivtza_savta.jpg",
    "מבצע סבתא",
    "אתה מתחיל הכי מהר שלך ולאט לאט מגביר",
    "סרט קאלט",
    [(120, 53, 15), (69, 26, 3), (15, 23, 42)]
)


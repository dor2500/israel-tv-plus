"""App icons for every platform from one drawing (PIL, drawn at 4x and reduced).

usage: python -I tools/make_icons.py
writes assets/app/icon-{48..1024}.png, icon-maskable-512.png, banner-320x180.png (Android TV), banner-1280x720.png,
       icon.ico (Windows), and the webOS icon.png (80) / largeIcon.png (130) in the project root.
"""
import os

from PIL import Image, ImageDraw, ImageFilter, ImageFont

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
OUT = os.path.join(ROOT, "assets", "app")
FONT = "C:/Windows/Fonts/arialbd.ttf"


def gradient(w, h, c1, c2):
    g = Image.new("RGB", (w, h))
    px = g.load()
    for y in range(h):
        for x in range(w):
            t = (x / w * 0.5 + y / h * 0.5)
            px[x, y] = tuple(int(a + (b - a) * t) for a, b in zip(c1, c2))
    return g


def tv_mark(size, pad=0.16, bg=True):
    """Square icon: deep blue rounded tile, glowing TV with a Star-of-David-blue screen and 'TV+'."""
    S = size * 4
    im = Image.new("RGBA", (S, S), (0, 0, 0, 0))
    if bg:
        tile = gradient(S // 8, S // 8, (8, 20, 48), (2, 6, 18)).resize((S, S), Image.BICUBIC)
        mask = Image.new("L", (S, S), 0)
        ImageDraw.Draw(mask).rounded_rectangle([0, 0, S - 1, S - 1], radius=int(S * 0.22), fill=255)
        im.paste(tile, (0, 0), mask)
    d = ImageDraw.Draw(im)
    p = int(S * pad)
    # antenna
    cx = S // 2
    top = p + int(S * 0.02)
    sy = p + int(S * 0.2)
    w = int(S * 0.035)
    d.line([(cx - int(S * 0.13), top), (cx, sy)], fill=(125, 211, 252, 255), width=w)
    d.line([(cx + int(S * 0.13), top), (cx, sy)], fill=(125, 211, 252, 255), width=w)
    # glow
    glow = Image.new("RGBA", (S, S), (0, 0, 0, 0))
    gd = ImageDraw.Draw(glow)
    box = [p, sy, S - p, S - p - int(S * 0.04)]
    gd.rounded_rectangle(box, radius=int(S * 0.1), fill=(56, 189, 248, 150))
    im.alpha_composite(glow.filter(ImageFilter.GaussianBlur(S * 0.05)))
    # screen
    scr = gradient(64, 64, (56, 189, 248), (99, 102, 241)).resize((box[2] - box[0], box[3] - box[1]), Image.BICUBIC)
    m = Image.new("L", scr.size, 0)
    ImageDraw.Draw(m).rounded_rectangle([0, 0, scr.size[0] - 1, scr.size[1] - 1], radius=int(S * 0.1), fill=255)
    im.paste(scr, (box[0], box[1]), m)
    # text
    font = ImageFont.truetype(FONT, int(S * 0.27))
    txt = "TV+"
    tb = d.textbbox((0, 0), txt, font=font)
    tx = (box[0] + box[2]) // 2 - (tb[2] - tb[0]) // 2 - tb[0]
    ty = (box[1] + box[3]) // 2 - (tb[3] - tb[1]) // 2 - tb[1]
    d.text((tx + int(S * 0.01), ty + int(S * 0.012)), txt, font=font, fill=(0, 20, 60, 120))
    d.text((tx, ty), txt, font=font, fill=(255, 255, 255, 255))
    return im.resize((size, size), Image.LANCZOS)


def banner(w, h):
    S = 4
    im = gradient(w // 4, h // 4, (10, 26, 60), (2, 6, 18)).resize((w * S, h * S), Image.BICUBIC).convert("RGBA")
    icon = tv_mark(int(h * 0.78) * S, bg=False)
    im.alpha_composite(icon, (int(w * S * 0.05), (h * S - icon.size[1]) // 2))
    d = ImageDraw.Draw(im)
    x = int(w * S * 0.05) + icon.size[0] + int(w * S * 0.03)
    room = w * S - x - int(w * S * 0.05)

    def fit(path, size, text):
        f = ImageFont.truetype(path, size)
        while d.textlength(text, font=f) > room:
            size -= 2
            f = ImageFont.truetype(path, size)
        return f
    f1 = fit(FONT, int(h * S * 0.3), "Israel TV+")
    f2 = fit("C:/Windows/Fonts/arial.ttf", int(h * S * 0.12), "Live Israeli TV & Radio")
    d.text((x, int(h * S * 0.2)), "Israel TV+", font=f1, fill=(255, 255, 255, 255))
    d.text((x, int(h * S * 0.58)), "Live Israeli TV & Radio", font=f2, fill=(125, 211, 252, 255))
    return im.resize((w, h), Image.LANCZOS)


def main():
    os.makedirs(OUT, exist_ok=True)
    for s in (48, 72, 96, 144, 192, 256, 512, 1024):
        tv_mark(s).save(os.path.join(OUT, f"icon-{s}.png"))
    # maskable: full-bleed background, artwork inside the 80% safe zone
    m = Image.new("RGBA", (512, 512), (4, 12, 32, 255))
    m.alpha_composite(tv_mark(400, bg=False), (56, 56))
    m.save(os.path.join(OUT, "icon-maskable-512.png"))
    banner(320, 180).save(os.path.join(OUT, "banner-320x180.png"))
    banner(1280, 720).save(os.path.join(OUT, "banner-1280x720.png"))
    tv_mark(256).save(os.path.join(OUT, "icon.ico"), sizes=[(16, 16), (24, 24), (32, 32), (48, 48), (64, 64), (128, 128), (256, 256)])
    tv_mark(80).save(os.path.join(ROOT, "icon.png"))
    tv_mark(130).save(os.path.join(ROOT, "largeIcon.png"))
    print("icons ->", OUT)


if __name__ == "__main__":
    main()

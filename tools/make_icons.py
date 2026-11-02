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


def mesh(S, base=(10, 10, 16)):
    """Dark tile with soft colour light (Nuvio-like): blue top-left, violet right, pink bottom."""
    im = Image.new("RGB", (S, S), base)
    for (cx, cy, r, col, a) in ((0.18, 0.12, 0.75, (59, 130, 246), 0.85), (0.95, 0.45, 0.7, (139, 92, 246), 0.75),
                                (0.45, 1.05, 0.7, (236, 72, 153), 0.6)):
        layer = Image.new("RGB", (S, S), col)
        m = Image.new("L", (S, S), 0)
        ImageDraw.Draw(m).ellipse([(cx - r / 2) * S, (cy - r / 2) * S, (cx + r / 2) * S, (cy + r / 2) * S], fill=int(255 * a))
        im = Image.composite(layer, im, m.filter(ImageFilter.GaussianBlur(S * 0.16)))
    shade = Image.new("L", (S, S), 0)   # darken the middle a little so the mark pops
    ImageDraw.Draw(shade).ellipse([S * 0.18, S * 0.18, S * 0.82, S * 0.82], fill=110)
    return Image.composite(Image.new("RGB", (S, S), base), im, shade.filter(ImageFilter.GaussianBlur(S * 0.12)))


def play_mask(S, scale=1.0):
    """Rounded play triangle (corners rounded by stroking a smaller triangle with round joints)."""
    m = Image.new("L", (S, S), 0)
    d = ImageDraw.Draw(m)
    r = S * 0.07 * scale
    cx, cy, h = S * 0.535, S * 0.5, S * 0.40 * scale
    pts = [(cx - h * 0.5 + r, cy - h * 0.58 + r * 1.2), (cx + h * 0.62 - r * 1.3, cy), (cx - h * 0.5 + r, cy + h * 0.58 - r * 1.2)]
    d.polygon(pts, fill=255)
    d.line(pts + [pts[0], pts[1]], fill=255, width=int(r * 2), joint="curve")
    for x, y in pts:
        d.ellipse([x - r, y - r, x + r, y + r], fill=255)
    return m


def app_mark(size, bg=True, mono=False, scale=1.0):
    """The app icon: dark mesh tile, white rounded play mark with a soft glow, red LIVE dot. mono = white on clear."""
    S = size * 4
    im = Image.new("RGBA", (S, S), (0, 0, 0, 0))
    if bg:
        mask = Image.new("L", (S, S), 0)
        ImageDraw.Draw(mask).rounded_rectangle([0, 0, S - 1, S - 1], radius=int(S * 0.23), fill=255)
        im.paste(mesh(S), (0, 0), mask)
    pm = play_mask(S, scale)
    if not mono:
        glow = Image.new("RGBA", (S, S), (160, 190, 255, 0))
        glow.putalpha(pm.filter(ImageFilter.GaussianBlur(S * 0.05)).point(lambda v: int(v * 0.55)))
        im.alpha_composite(glow)
    fill = gradient(32, 32, (255, 255, 255), (214, 222, 255)).resize((S, S), Image.BICUBIC).convert("RGBA") if not mono \
        else Image.new("RGBA", (S, S), (255, 255, 255, 255))
    fill.putalpha(pm)
    im.alpha_composite(fill)
    # live dot, top right of the mark
    d = ImageDraw.Draw(im)
    rr = S * 0.062 * scale
    x, y = S * (0.5 + 0.215 * scale), S * (0.5 - 0.215 * scale)
    if not mono:
        ring = S * 0.022
        d.ellipse([x - rr - ring, y - rr - ring, x + rr + ring, y + rr + ring], fill=(10, 10, 16, 255) if bg else (0, 0, 0, 0))
    d.ellipse([x - rr, y - rr, x + rr, y + rr], fill=(255, 255, 255, 255) if mono else (239, 68, 68, 255))
    return im.resize((size, size), Image.LANCZOS)


def banner(w, h):
    S = 4
    im = mesh(w * S).crop((0, (w - h) * S // 2, w * S, (w + h) * S // 2)).convert("RGBA")
    icon = app_mark(int(h * 0.62) * S)
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
    f2 = fit("C:/Windows/Fonts/arial.ttf", int(h * S * 0.12), "Live TV & Radio · Israel + World")
    d.text((x, int(h * S * 0.2)), "Israel TV+", font=f1, fill=(255, 255, 255, 255))
    d.text((x, int(h * S * 0.58)), "Live TV & Radio · Israel + World", font=f2, fill=(200, 210, 255, 255))
    return im.resize((w, h), Image.LANCZOS)


def main():
    os.makedirs(OUT, exist_ok=True)
    for s in (48, 72, 96, 144, 192, 256, 512, 1024):
        app_mark(s).save(os.path.join(OUT, f"icon-{s}.png"))
    # maskable (PWA): full-bleed background, artwork inside the 80% safe zone
    m = mesh(512).convert("RGBA")
    m.alpha_composite(app_mark(512, bg=False, scale=0.8))
    m.save(os.path.join(OUT, "icon-maskable-512.png"))
    banner(320, 180).save(os.path.join(OUT, "banner-320x180.png"))
    banner(1280, 720).save(os.path.join(OUT, "banner-1280x720.png"))
    app_mark(256).save(os.path.join(OUT, "icon.ico"), sizes=[(16, 16), (24, 24), (32, 32), (48, 48), (64, 64), (128, 128), (256, 256)])
    app_mark(80).save(os.path.join(ROOT, "icon.png"))
    app_mark(130).save(os.path.join(ROOT, "largeIcon.png"))
    # Android 8+ adaptive icon (background + foreground layers, 108dp, mark inside the 66dp safe zone) and the
    # Android 13+ themed icon (monochrome layer, tinted by the system with the wallpaper colours – Material You)
    res = os.path.join(ROOT, "platforms", "android", "res")
    for dpi, px in (("mdpi", 108), ("hdpi", 162), ("xhdpi", 216), ("xxhdpi", 324), ("xxxhdpi", 432)):
        d = os.path.join(res, "mipmap-" + dpi)
        os.makedirs(d, exist_ok=True)
        mesh(px).save(os.path.join(d, "ic_launcher_background.png"))
        app_mark(px, bg=False, scale=0.62).save(os.path.join(d, "ic_launcher_foreground.png"))
        app_mark(px, bg=False, mono=True, scale=0.62).save(os.path.join(d, "ic_launcher_monochrome.png"))
    any_dpi = os.path.join(res, "mipmap-anydpi-v26")
    os.makedirs(any_dpi, exist_ok=True)
    with open(os.path.join(any_dpi, "ic_launcher.xml"), "w", encoding="utf-8") as f:
        f.write('<?xml version="1.0" encoding="utf-8"?>\n'
                '<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">\n'
                '    <background android:drawable="@mipmap/ic_launcher_background" />\n'
                '    <foreground android:drawable="@mipmap/ic_launcher_foreground" />\n'
                '    <monochrome android:drawable="@mipmap/ic_launcher_monochrome" />\n'
                '</adaptive-icon>\n')
    print("icons ->", OUT, "+ Android adaptive / themed icon")


if __name__ == "__main__":
    main()

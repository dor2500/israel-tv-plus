"""App icons for every platform from one drawing (PIL, drawn at 4x and reduced).

usage: python -I tools/make_icons.py
writes assets/app/icon-{48..1024}.png, icon-maskable-512.png, banner-320x180.png (Android TV), banner-1280x720.png,
       icon.ico (Windows), the webOS icon.png (80) / largeIcon.png (130) / splash.png in the project root,
       the Android adaptive icon layers and the iOS app icon and splash images.
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


TILE = (14, 15, 20)       # near black
# The three ribbons of the play mark, in drawing order, on a 512 grid: from, to, colour at "from", colour at "to"
RIBBONS = (
    ((170, 130), (170, 382), (33, 212, 253), (124, 92, 255)),
    ((170, 130), (382, 256), (124, 92, 255), (255, 77, 141)),
    ((170, 382), (382, 256), (255, 122, 69), (255, 194, 75)),
)
RIBBON_WIDTH = 70


def mesh(S):
    """The tile colour, also the background layer of the adaptive icons and of the banner."""
    return Image.new("RGB", (S, S), TILE)


def along(S, a, b, c1, c2):
    """A colour fade from point a to point b. Worked out small and enlarged, which is smooth and fast."""
    n = 128
    im = Image.new("RGB", (n, n))
    px = im.load()
    dx, dy = b[0] - a[0], b[1] - a[1]
    length = dx * dx + dy * dy
    for y in range(n):
        for x in range(n):
            t = ((x / n * S - a[0]) * dx + (y / n * S - a[1]) * dy) / length
            t = max(0.0, min(1.0, t))
            px[x, y] = tuple(int(u + (v - u) * t) for u, v in zip(c1, c2))
    return im.resize((S, S), Image.BICUBIC).convert("RGBA")


def app_mark(size, bg=True, mono=False, scale=1.0):
    """The app icon: near-black tile, a play mark made of three colour ribbons. mono = white on clear."""
    S = size * 4
    im = Image.new("RGBA", (S, S), (0, 0, 0, 0))
    if bg:
        ImageDraw.Draw(im).rounded_rectangle([0, 0, S - 1, S - 1], radius=int(S * 0.2227), fill=TILE + (255,))

    def at(p):   # scaled around the centre
        return (S * (0.5 + (p[0] - 256) / 512 * scale), S * (0.5 + (p[1] - 256) / 512 * scale))

    width = S * RIBBON_WIDTH / 512 * scale
    for start, end, c1, c2 in RIBBONS:
        a, b = at(start), at(end)
        mask = Image.new("L", (S, S), 0)
        d = ImageDraw.Draw(mask)
        d.line([a, b], fill=255, width=int(width))
        for x, y in (a, b):
            d.ellipse([x - width / 2, y - width / 2, x + width / 2, y + width / 2], fill=255)
        ribbon = Image.new("RGBA", (S, S), (255, 255, 255, 255)) if mono else along(S, a, b, c1, c2)
        ribbon.putalpha(mask)
        im.alpha_composite(ribbon)
    return im.resize((size, size), Image.LANCZOS)


def splash(w, h):
    """The icon in the middle of the dark screen the apps open on."""
    im = Image.new("RGBA", (w, h), (13, 13, 13, 255))
    icon = app_mark(min(w, h) // 4)
    im.alpha_composite(icon, ((w - icon.size[0]) // 2, (h - icon.size[1]) // 2))
    return im


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
    splash(1920, 1080).save(os.path.join(ROOT, "splash.png"))
    # iOS: no transparency allowed, the system rounds the corners itself
    assets = os.path.join(ROOT, "ios", "App", "App", "Assets.xcassets")
    ios = mesh(1024).convert("RGBA")
    ios.alpha_composite(app_mark(1024, bg=False))
    ios.convert("RGB").save(os.path.join(assets, "AppIcon.appiconset", "AppIcon-512@2x.png"))
    for name in ("splash-2732x2732.png", "splash-2732x2732-1.png", "splash-2732x2732-2.png"):
        splash(2732, 2732).convert("RGB").save(os.path.join(assets, "Splash.imageset", name))
    # Android 8+ adaptive icon (background + foreground layers, 108dp, mark inside the 66dp safe zone) and the
    # Android 13+ themed icon (monochrome layer, tinted by the system with the wallpaper colours – Material You)
    res = os.path.join(ROOT, "platforms", "android", "res")
    for dpi, px in (("mdpi", 108), ("hdpi", 162), ("xhdpi", 216), ("xxhdpi", 324), ("xxxhdpi", 432)):
        d = os.path.join(res, "mipmap-" + dpi)
        os.makedirs(d, exist_ok=True)
        mesh(px).save(os.path.join(d, "ic_launcher_background.png"))
        app_mark(px, bg=False, scale=0.95).save(os.path.join(d, "ic_launcher_foreground.png"))
        app_mark(px, bg=False, mono=True, scale=0.95).save(os.path.join(d, "ic_launcher_monochrome.png"))
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

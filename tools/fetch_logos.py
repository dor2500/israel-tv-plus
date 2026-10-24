"""The real logo for every curated channel that still has a drawn text logo (add_israeli.py) or none.

Sources, in order: the Idan Plus Kodi add-on's channel images (Israeli TV + radio), the iptv-org logo database
(by the channel's iptv id), radio-browser.info favicons (radio, by stream URL). Each logo is trimmed, scaled to at
most 400 px and saved as assets/logos/<id>.png; a mostly dark logo on a transparent background gets a white plate
(it would vanish on the app's dark cards). channels.json / channels.js point to it, and the drawn SVG is removed.
add_israeli.py uses assets/logos/<id>.png from then on.

usage: python -I tools/fetch_logos.py <iptv-org logos.json> <radio-browser stations.json> [--dry]
  (iptv-org: https://iptv-org.github.io/api/logos.json; radio-browser: /json/stations/bycountrycodeexact/IL)
"""
import io
import json
import os
import sys

import requests
from PIL import Image, ImageDraw, ImageStat

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
LOGOS = os.path.join(ROOT, "assets", "logos")
UA = {"User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/130.0 Safari/537.36"}
IDAN = "https://raw.githubusercontent.com/Fishenzon/repo/master/plugin.video.idanplus/images/"
OURS = "file:"  # an existing real logo in assets/logos

# channel id -> the Idan Plus image (or one of ours) – checked by eye, 2026-10-24
MAP = {
    "now14": "14tv.png", "makan33": "makan.png", "knesset": "knesset.png", "knesset_access": "knesset.png",
    "i24news_he": "i24news.png", "i24news_fr": "i24news.png", "i24news_ar": "i24news.png", "i24news_en": "i24news.png",
    "kan_hinuchit": "23tv.jpg", "ynet_live": "ynet.jpg", "kan11_subs": OURS + "kan11.png",
    "reshet13_access": OURS + "reshet13.png",
    "kan_bet": "bet.png", "kan_gimel": "gimel.png", "kan_88": "88fm.png", "kan_tarbut": "culture.png",
    "kan_moreshet": "moreshet.png", "kan_reka": "reka.png", "kan_kol_hamusica": "music.png",
    "galgalatz_live": "glglz.jpg", "galgalatz_hits_live": "glglz.jpg", "glglz_rock": "glglz.jpg",
    "glglz_decades": "glglz.jpg", "glglz_weekend": "glglz.jpg", "glz_live": "glz.jpg",
    "eco99fm": "99fm.png", "radio_103fm": "103fm.png", "radio_102fm": "102fm.jpg", "radio_1045fm": "1045fm.jpg",
    "radio_101fm": "101fm.png", "radio_haifa": "1075fm.jpg", "radio_90fm": "90fm.jpg", "radio_darom_97": "97fm.jpg",
    "radio_darom_live": "1015fm.jpg", "galei_israel": "gly.jpg", "kol_chai": "kolhay.jpg",
    "kol_chai_music": "kolhaymusic.jpg", "lev_hamedina": "91fm.jpg", "kol_barama_live": "kolbarama.jpg",
    "kol_barama_music_live": "kolplay.png", "noshmim_mizrahit": "noshmim.jpg", "radio_diki": "diki.jpg",
    "radio_eilat": "102Eilatfm.jpg", "radio_995": "fm95.5.png", "radio_best_1064": "1064fm.jpg",
    "pervoe_radio": "891fm.png", "radio_sport5": "Sport5.png", "sport5_live": "Sport5.png", "calcala10": "10tv.png",
    "keshet_comedy": "12comedy.jpg", "keshet_drama": "12drama.jpg", "eretz_nehederet": "12eretz.jpg",
    "savri_maranan": "12savri.jpg", "keshet_music": "12music.jpg", "radios_video": "100fm.jpg",
    "kabbalah": "kabbalah.jpg", "kabbalah_ru": "kabbalah.jpg", "kabbalah_radio": "kabbalah.jpg",
    "hidabroot": "hidabroot.jpg", "channel16": "16tv.jpg", "comedy_channel": "13comedy.jpg",
    "vacation_channel": "13nofesh.jpg",
}
RADIOS = "100fm.jpg"  # every Radios 100FM channel: the station's own logo


def drawn(c):
    """No logo, or one drawn by add_israeli.py (an SVG with text)."""
    logo = c.get("logo", "")
    if not logo:
        return True
    if logo.startswith("assets/logos/") and logo.endswith(".svg"):
        p = os.path.join(ROOT, logo)
        return os.path.isfile(p) and "<text" in open(p, encoding="utf-8").read()
    return False


def finish(im):
    """Trim, scale to 400 px, and put a dark transparent logo on a white rounded plate."""
    im = im.convert("RGBA")
    bbox = im.getbbox()
    if bbox:
        im = im.crop(bbox)
    im.thumbnail((400, 400))
    alpha = im.getchannel("A")
    if alpha.getextrema()[0] < 128:  # has transparency
        mask = alpha.point(lambda a: 255 if a > 128 else 0)
        lum = ImageStat.Stat(im.convert("L"), mask).mean[0] if mask.getbbox() else 255
        if lum < 80:
            pad = max(12, im.width // 12)
            plate = Image.new("RGBA", (im.width + 2 * pad, im.height + 2 * pad), (0, 0, 0, 0))
            ImageDraw.Draw(plate).rounded_rectangle((0, 0, plate.width - 1, plate.height - 1), pad, fill=(255, 255, 255, 255))
            plate.alpha_composite(im, (pad, pad))
            im = plate
    return im


def main():
    iptv_db, rb_file = sys.argv[1:3]
    dry = "--dry" in sys.argv
    with open(os.path.join(ROOT, "channels.json"), encoding="utf-8") as f:
        chans = json.load(f)
    logos = {}
    for l in json.load(open(iptv_db, encoding="utf-8")):
        if l.get("feed"):
            continue
        w, h = l.get("width") or 0, l.get("height") or 1
        score = (l.get("format") in ("PNG", "SVG", "WebP"), 0.8 < w / h < 4, min(w, 600))
        if l["channel"] not in logos or score > logos[l["channel"]][0]:
            logos[l["channel"]] = (score, l["url"])
    favicons = {}
    for s in json.load(open(rb_file, encoding="utf-8")):
        for k in ("url", "url_resolved"):
            u = (s.get(k) or "").split("://", 1)[-1].rstrip("/")
            if u and s.get("favicon"):
                favicons[u] = s["favicon"]

    done, missing = 0, []
    for c in chans:
        if c.get("imported") or not drawn(c):
            continue
        cid = c["id"]
        src = MAP.get(cid) or (RADIOS if cid.startswith("radios_") else None)
        cands = []
        if src and src.startswith(OURS):
            cands.append(("ours", os.path.join(LOGOS, src[len(OURS):])))
        elif src:
            cands.append(("idanplus", IDAN + src))
        if c.get("iptv") in logos:
            cands.append(("iptv-org", logos[c["iptv"]][1]))
        fav = favicons.get(c["url"].split("://", 1)[-1].rstrip("/"))
        if fav:
            cands.append(("radio-browser", fav))
        for source, url in cands:
            try:
                if source == "ours":
                    data = open(url, "rb").read()
                else:
                    r = requests.get(url, headers=UA, timeout=20)
                    if r.status_code != 200 or len(r.content) < 200:
                        continue
                    data = r.content
                im = finish(Image.open(io.BytesIO(data)))
            except Exception as e:
                print("  ", cid, source, "failed:", e)
                continue
            if not dry:
                im.save(os.path.join(LOGOS, cid + ".png"), optimize=True)
                old = os.path.join(ROOT, c.get("logo", "")) if c.get("logo", "").endswith(".svg") else ""
                if old and os.path.isfile(old):
                    os.remove(old)
                c["logo"] = "assets/logos/" + cid + ".png"
            done += 1
            break
        else:
            missing.append(c)
    print(f"{done} logos; {len(missing)} without one:")
    for c in missing:
        print("  ", c["id"], "|", c["name"])
    if dry:
        return
    with open(os.path.join(ROOT, "channels.json"), "w", encoding="utf-8") as f:
        json.dump(chans, f, ensure_ascii=False, indent=2)
    with open(os.path.join(ROOT, "channels.js"), "w", encoding="utf-8") as f:
        f.write("window.CHANNELS_DATA = " + json.dumps(chans, ensure_ascii=False, indent=1) + ";\n")


if __name__ == "__main__":
    main()

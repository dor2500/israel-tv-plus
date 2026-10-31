"""Builds epg.json, the TV guide the app shows: public XMLTV feeds, matched to our channels and trimmed.

The feeds send no CORS headers and weigh 16 MB, so a TV can't read them itself. This runs on a schedule
(.github/workflows/epg.yml) and the app reads the small file from GitHub Pages.

epg.json: { "generated": epoch minutes, "channels": { channel id: [[start, minutes, title, desc, icon], ...] } }
start is in epoch minutes, sorted. desc and icon may be "".

Usage: python tools/make_epg.py [--report]
"""
import gzip
import json
import os
import re
import sys
import time
import urllib.request
import xml.etree.ElementTree as ET
from calendar import timegm

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
# earlier feeds win: iptv-epg.org has rich working posters, epgshare01 for extra Israeli channels
FEEDS = ["https://iptv-epg.org/files/epg-il.xml", "https://epgshare01.online/epgshare01/epg_ripper_IL1.xml.gz"]
HOURS_BACK, HOURS_AHEAD = 3, 36
# channels with catch-up keep their past as far as the app can play it (CATCHUP in app.js)
CATCHUP_HOURS = {"reshet13": 24, "kan11": 6, "kan_hinuchit": 6, "i24news_he": 3, "i24news_en": 3, "i24news_fr": 3}
DESC_MAX = 170

# Names the automatic match can't work out: our channel id -> normalised feed name (see norm()).
ALIASES = {
    "channel9": "9", "keshet12": "קשת", "reshet13": "רשת", "now14": "ערוץ14", "kan11": "כאן11", "kan_hinuchit": "כאןחינוכית", "i24news_he": "עבריתi24",
    "sport1_israel": "ספורט1", "sport2_israel": "ספורט2", "sport3_israel": "ספורט3", "sport4_israel": "ספורט4",
    "channel24": "24החדש", "channel_98_il": "98", "knesset": "הכנסת", "calcala10": "הכלכלה", "hot_bidur": "הוטבידורי", "hot_reality": "hotreal",
    "hot_8": "hot8", "home_plus_il": "בית+", "health_channel_il": "בריאות", "vacation_channel_il": "הנופש", "ego_channel_il": "אגו",
    "humor_channel_il": "ההומור", "il_the_style_channel_hd_il": "הסטייל", "hop_kids_israel": "הופ", "hop_yaldut_il": "ילדותית", "luli_tv_il": "hotluli",
    "yes_movies_kids_il": "yesmovieskids", "wiz_kids_il": "wiz", "fomo_tv": "fomo", "stars_kids_il": "הכוכבים", "sport5_stars_hd": "5stars",
    "one_doco_hd": "onedoco", "one_edge_hd": "sportedge", "hot_israeli_cinema": "הקולנועהי", "yam_tichoni_hd": "יםתיכוני", "bollyshow_hd": "bollyshow",
    "food_network_il": "foodnetwork", "foody_tv": "foody", "nat_geo_wild_il": "נגיאוגרפיקwild", "hidabroot": "הידברות", "cnn_intl_hd": "cnn",
    "fox_news_hd": "foxnews", "bloomberg_tv_hd": "בלומברג", "sky_news_uk": "skynews", "dw_english": "dwenglish", "i24_english_world_hd": "i24newsenglish",
}

DROP = re.compile(r"\b(fhd|uhd|hd|4k|sd|il|tv|live|israel)\b|ישראל|ישראלי|שידור חי|ערוץ|ילדים ונוער|\(.*?\)", re.I)


def norm(name):
    name = DROP.sub(" ", name.lower().replace("’", "'"))
    return re.sub(r"[^0-9a-z֐-׿+]", "", name)


def fetch(url):
    req = urllib.request.Request(url, headers={"User-Agent": "israel-tv-plus-epg"})
    with urllib.request.urlopen(req, timeout=180) as r:
        data = r.read()
    return gzip.decompress(data) if data[:2] == b"\x1f\x8b" else data


def minutes(stamp):
    """'20261031051900 +0000' -> epoch minutes"""
    m = re.match(r"(\d{4})(\d\d)(\d\d)(\d\d)(\d\d)\d\d\s*([+-])(\d\d)(\d\d)", stamp)
    y, mo, d, h, mi = (int(x) for x in m.groups()[:5])
    off = int(m.group(7)) * 60 + int(m.group(8))
    return timegm((y, mo, d, h, mi, 0)) // 60 - (off if m.group(6) == "+" else -off)


def read_feed(url):
    """{ feed channel id: {names: [...], progs: [[start, minutes, title, desc, icon], ...]} }"""
    root = ET.fromstring(fetch(url))
    now = int(time.time() // 60)
    lo, hi = now - max(CATCHUP_HOURS.values()) * 60, now + HOURS_AHEAD * 60
    out = {}
    for c in root.findall("channel"):
        out[c.get("id")] = {"names": [d.text or "" for d in c.findall("display-name")], "progs": []}
    for p in root.findall("programme"):
        c = out.get(p.get("channel"))
        title = (p.findtext("title") or "").strip()
        if c is None or not title:
            continue
        s, e = minutes(p.get("start")), minutes(p.get("stop"))
        if e <= lo or s >= hi or e <= s:
            continue
        icon = p.find("icon")
        icon_url = icon.get("src", "") if icon is not None else ""
        if "kaltura.com" in icon_url:
            icon_url = ""
        desc = " ".join((p.findtext("desc") or "").split())
        if len(desc) > DESC_MAX:
            desc = desc[:DESC_MAX].rsplit(" ", 1)[0] + "…"
        c["progs"].append([s, e - s, title, desc, icon_url])
    return out


def main():
    with open(os.path.join(ROOT, "channels.json"), encoding="utf-8") as f:
        raw = json.load(f)
    channels = raw if isinstance(raw, list) else raw["channels"]
    channels = [c for c in channels if "רדיו" not in c.get("category", "")]

    index = {}  # normalised name or id -> programmes, first feed wins
    for url in FEEDS:
        for fid, c in read_feed(url).items():
            if not c["progs"]:
                continue
            c["progs"].sort(key=lambda p: p[0])
            for key in [norm(n) for n in c["names"]] + [norm(fid), fid.lower()]:
                if key:
                    index.setdefault(key, c["progs"])

    guide, missed = {}, []
    for ch in channels:
        keys = [ALIASES.get(ch["id"], ""), (ch.get("iptv") or "").lower(), norm(ch.get("iptv") or ""), norm(ch["name"]), norm(ch.get("title") or "")]
        progs = next((index[k] for k in keys if k and k in index), None)
        if progs:
            lo = int(time.time() // 60) - CATCHUP_HOURS.get(ch["id"], HOURS_BACK) * 60
            guide[ch["id"]] = [p for p in progs if p[0] + p[1] > lo]
        else:
            missed.append(ch)

    if "--report" in sys.argv:
        sys.stdout.reconfigure(encoding="utf-8")
        print("matched", len(guide), "of", len(channels))
        print("\nFEED KEYS:", " ".join(sorted(index)))
        print("\nMISSED:")
        for ch in missed:
            print(" ", ch.get("number"), ch["id"], "|", ch["name"], "|", norm(ch["name"]))

    path = os.path.join(ROOT, "epg.json")
    with open(path, "w", encoding="utf-8") as f:
        json.dump({"generated": int(time.time() // 60), "channels": guide}, f, ensure_ascii=False, separators=(",", ":"))
    print("epg.json:", len(guide), "channels,", os.path.getsize(path) // 1024, "KB")


if __name__ == "__main__":
    main()

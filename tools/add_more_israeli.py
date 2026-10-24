"""More Israeli radio stations from radio-browser.info (country IL), added to channels.json.

Each stream is tested like in add_israeli.py (first audio bytes / HLS segment). Stations whose stream (host+path) is
already in channels.json are skipped. Logos are drawn in the same style as the other stations.
After it runs, add_israeli.py (no network needed for old entries) re-numbers the list.

usage: python -I tools/add_more_israeli.py [--dry]
"""
import json
import os
import re
import sys
import urllib.parse
from concurrent.futures import ThreadPoolExecutor

import requests

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import add_israeli as ai  # noqa: E402

HOSTS = ["de1", "nl1", "at1"]
PALETTE = [("#0c4a6e", "#38bdf8"), ("#14532d", "#4ade80"), ("#7c2d12", "#fb923c"), ("#4c1d95", "#a78bfa"),
           ("#831843", "#f472b6"), ("#1e3a8a", "#93c5fd"), ("#134e4a", "#5eead4"), ("#713f12", "#facc15"),
           ("#7f1d1d", "#fca5a5"), ("#312e81", "#c4b5fd")]


# radio-browser also lists foreign streams under IL: only clearly Israeli ones go in (Hebrew name or a known Israeli brand)
ISRAELI = re.compile(r"[\u0590-\u05ff]|100fm|kabbalah|mizrahit|rak musica|ivri|eli nisman|star 33|aviv|israel", re.I)
# stations the list already has under another name (Kan, Galgalatz, eco99, Kol Barama, Kol Hai, Radios 100FM genres ...)
DUPLICATE = re.compile(r"kan |kan$|כאן|glglz|galgalatz|גלגל|eco|אקו|88 ?fm|barama|ברמה|kol hai|קול חי|kabbalah rus|"
                       r"100% (workout|movies|nostalgia)|^eli nisman$", re.I)


def norm(url):
    p = urllib.parse.urlsplit(url.strip())
    return (p.netloc + p.path).lower().rstrip("/")


def clean(name):
    name = re.sub(r"\s+", " ", name.replace("\u200f", "").replace("\u200e", "")).strip(" -_|.")
    return name


def slug(name, n):
    s = re.sub(r"[^a-z0-9]+", "_", name.lower()).strip("_")
    return ("rb_" + s)[:40] if s else f"rb_il_{n}"


def stations():
    for h in HOSTS:
        try:
            r = requests.get(f"https://{h}.api.radio-browser.info/json/stations/bycountrycodeexact/IL",
                             params={"hidebroken": "true", "order": "clickcount", "reverse": "true"},
                             headers={"User-Agent": "israel-tv-plus"}, timeout=30)
            return r.json()
        except Exception as e:  # noqa: BLE001
            print("radio-browser", h, type(e).__name__)
    return []


def main():
    dry = "--dry" in sys.argv
    path = os.path.join(ai.ROOT, "channels.json")
    with open(path, encoding="utf-8") as f:
        chans = json.load(f)
    have_urls = {norm(c["url"]) for c in chans}
    have_ids = {c["id"] for c in chans}
    have_names = {re.sub(r"\W", "", c["name"].lower()) for c in chans}

    todo, seen = [], set()
    for i, s in enumerate(stations()):
        url = (s.get("url_resolved") or s.get("url") or "").strip()
        name = clean(s.get("name", ""))
        if not url.startswith("http") or not name or s.get("lastcheckok") != 1:
            continue
        k = norm(url)
        nk = re.sub(r"\W", "", name.lower())
        if not ISRAELI.search(name) or DUPLICATE.search(name):
            continue
        if k in have_urls or k in seen or nk in have_names or nk in seen:
            continue
        seen.update({k, nk})
        todo.append((i, name, url, s))
    print("candidates:", len(todo))
    with ThreadPoolExecutor(24) as ex:
        errs = list(ex.map(lambda t: ai.probe(t[2]), todo))

    added = []
    for (i, name, url, s), err in zip(todo, errs):
        if err:
            print("  dead", name, err)
            continue
        cid = slug(name, i)
        base, n = cid, 2
        while cid in have_ids:
            cid, n = f"{base}_{n}", n + 1
        have_ids.add(cid)
        bg, accent = PALETTE[len(added) % len(PALETTE)]
        big = name if len(name) <= 14 else name[:14].rstrip()
        tags = ", ".join([t for t in (s.get("tags") or "").split(",") if t][:3])
        logo = "" if dry else ai.write_logo(cid, big, "רדיו", bg, accent, "radio")
        added.append({"id": cid, "name": name, "title": name, "category": ai.CAT_RADIO, "categoryEn": "radio",
                      "kind": "radio", "url": url, "badge": "LIVE", "lang": "music",
                      "desc": f"{name} – תחנת רדיו ישראלית" + (f" ({tags})" if tags else ""), "logo": logo})
        print("  added", name)
    print(f"added {len(added)}, total {len(chans) + len(added)}")
    if dry or not added:
        return
    chans += added
    with open(path, "w", encoding="utf-8") as f:
        json.dump(chans, f, ensure_ascii=False, indent=2)
    ai.main()  # re-numbers (and re-writes channels.js)


if __name__ == "__main__":
    main()

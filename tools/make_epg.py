"""Programme guide (EPG): what is on now, next and later today on the app's channels -> epg.json.

Sources (public XMLTV guides, schedules only):
  - i.mjh.nz PlutoTV / SamsungTVPlus, per region: the FAST channels (channels.json "fast": true), by their own ids
  - epgshare01 (IL1) and open-epg (israel1): the Israeli broadcast channels, by the map ISRAEL below
The app downloads epg.json from the "epg" branch (raw.githubusercontent.com, CORS *); .github/workflows/epg.yml
rebuilds it every two hours.

epg.json: {"v": 1, "t": <generated, unix seconds>, "c": {"<channel id>": [[start, minutes, "title", "desc"], ...]}}
  start = unix minutes; programmes from an hour ago to WINDOW hours ahead; desc only on the first few (now / next).

usage: python -I tools/make_epg.py [out.json] [cache dir]
"""
import gzip
import io
import json
import os
import re
import sys
import time
import xml.etree.ElementTree as ET
from datetime import datetime

import requests

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
WINDOW = 10            # hours ahead
DESC_FIRST = 3         # programmes per channel that keep a description
DESC_LEN = 160
UA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0 Safari/537.36"

IL1 = "https://epgshare01.online/epgshare01/epg_ripper_IL1.xml.gz"
OPEN_IL = "https://www.open-epg.com/files/israel1.xml"
# app channel id -> the guide's channel ids, in order of preference ("il1:" epgshare01, "oe:" open-epg)
ISRAEL = {
    "kan11": ["il1:ערוץ.11.כאן.שידור.חי.il", "il1:כאן.11.il", "oe:ערוץ 11 | תאגיד השידור הישראלי.il"],
    "kan11_subs": ["il1:ערוץ.11.כאן.שידור.חי.il", "il1:כאן.11.il", "oe:ערוץ 11 | תאגיד השידור הישראלי.il"],
    "keshet12": ["il1:ערוץ.12.שידור.חי.il", "il1:קשת.il", "oe:ערוץ 12 | לוח השידורים המלא של קשת 12.il"],
    "reshet13": ["il1:רשת.il", "oe:ערוץ 13.il"],
    "reshet13_access": ["il1:רשת.il", "oe:ערוץ 13.il"],
    "now14": ["il1:ערוץ.14.שידור.חי.il", "il1:ערוץ.14.HD.il", "oe:ערוץ 14.il"],
    "i24news_he": ["il1:עברית.i24.il", "oe:i24news בעברית.il"],
    "i24news_en": ["il1:i24NEWS.ENGLISH.il"],
    "i24news_fr": ["il1:i24NEWS.FRENCH.il"],
    "i24news_ar": ["il1:i24NEWS.ARABIC.il"],
    "channel9": ["il1:ערוץ.9.שידור.חי.il", "il1:ערוץ.9.il", "oe:ערוץ 9.il"],
    "channel24": ["il1:ערוץ.24.בשידור.חי.il", "oe:ערוץ 24.il"],
    "makan33": ["il1:מכאן.il"],
    "knesset": ["il1:ערוץ.הכנסת.il", "oe:ערוץ הכנסת.il"],
    "knesset_access": ["il1:ערוץ.הכנסת.il", "oe:ערוץ הכנסת.il"],
    "kan_hinuchit": ["il1:כאן.חינוכית.il", "oe:כאן חינוכית.il"],
    "hidabroot": ["il1:ערוץ.הידברות.il"],
    "dw_english": ["il1:DW.English.HD.il"],
}
FAST = {"plu": "PlutoTV", "stvp": "SamsungTVPlus"}


def fetch(url, cache, name):
    path = os.path.join(cache, name)
    if not (os.path.isfile(path) and time.time() - os.path.getmtime(path) < 1800):
        r = requests.get(url, headers={"User-Agent": UA}, timeout=120)
        r.raise_for_status()
        with open(path, "wb") as f:
            f.write(r.content)
    raw = open(path, "rb").read()
    try:
        raw = gzip.decompress(raw)
    except OSError:
        pass
    return raw


def when(s):
    """XMLTV time "20261025125500 +0000" -> unix minutes."""
    return int(datetime.strptime(s.strip(), "%Y%m%d%H%M%S %z").timestamp()) // 60


def programmes(raw, wanted, lo, hi):
    """{guide id: [(start, stop, title, desc)]} for the wanted guide ids, overlapping [lo, hi] (unix minutes)."""
    out = {}
    for _, el in ET.iterparse(io.BytesIO(raw), events=("end",)):
        if el.tag != "programme":
            if el.tag == "channel":
                el.clear()
            continue
        cid = el.get("channel")
        if cid in wanted:
            try:
                start, stop = when(el.get("start")), when(el.get("stop"))
            except (TypeError, ValueError):
                start = stop = 0
            if stop > lo and start < hi and stop > start:
                title = (el.findtext("title") or "").strip()
                sub = (el.findtext("sub-title") or "").strip()
                desc = (el.findtext("desc") or "").strip()
                if title:
                    out.setdefault(cid, []).append((start, stop, title + (" – " + sub if sub and sub not in title else ""), desc))
        el.clear()
    for v in out.values():
        v.sort()
    return out


def compact(progs):
    rows = []
    last = 0
    for i, (start, stop, title, desc) in enumerate(progs):
        if start < last:
            continue  # overlapping duplicates in some guides
        last = stop
        d = re.sub(r"\s+", " ", desc)
        if len(d) > DESC_LEN:
            d = d[:DESC_LEN - 1].rsplit(" ", 1)[0] + "…"
        rows.append([start, stop - start, title[:90], d if len(rows) < DESC_FIRST else ""])
    for r in rows:
        if not r[3]:
            r.pop()
    return rows


def main():
    out_path = sys.argv[1] if len(sys.argv) > 1 else os.path.join(ROOT, "epg.json")
    cache = sys.argv[2] if len(sys.argv) > 2 else os.path.join(ROOT, "..", "israel-tv-plus-work", "epg")
    os.makedirs(cache, exist_ok=True)
    now = int(time.time()) // 60
    lo, hi = now - 60, now + WINDOW * 60
    with open(os.path.join(ROOT, "channels.json"), encoding="utf-8") as f:
        chans = json.load(f)
    guide = {}

    # FAST channels: the service's own guide for the channel's region
    by_file = {}
    for c in chans:
        if not c.get("fast"):
            continue
        m = re.match(r"https://jmp2\.uk/(plu|stvp)-([^.]+)", c["url"])
        if m:
            by_file.setdefault((FAST[m.group(1)], c["country"].lower()), {})[m.group(2)] = c["id"]
    for (service, region), ids in sorted(by_file.items()):
        try:
            raw = fetch(f"https://i.mjh.nz/{service}/{region}.xml.gz", cache, f"{service}_{region}.xml")
        except requests.RequestException as e:
            print("skip", service, region, e)
            continue
        got = programmes(raw, set(ids), lo, hi)
        for gid, progs in got.items():
            guide[ids[gid]] = compact(progs)
        print(f"{service} {region}: {len(got)}/{len(ids)} channels")

    # Israeli channels: two guides, the first that has something on now wins
    sources = {"il1": (IL1, "il1.xml"), "oe": (OPEN_IL, "openil.xml")}
    have = {c["id"] for c in chans}
    parsed = {}
    for key, (url, name) in sources.items():
        wanted = {g.split(":", 1)[1] for ids in ISRAEL.values() for g in ids if g.startswith(key + ":")}
        try:
            parsed[key] = programmes(fetch(url, cache, name), wanted, lo, hi)
        except (requests.RequestException, ET.ParseError) as e:
            print("skip", key, e)
            parsed[key] = {}
    for cid, ids in ISRAEL.items():
        if cid not in have:
            continue
        for g in ids:
            key, gid = g.split(":", 1)
            progs = parsed[key].get(gid)
            if progs and any(p[0] <= now < p[1] for p in progs):
                guide[cid] = compact(progs)
                break
    print("israel:", sum(1 for c in ISRAEL if c in guide), "/", sum(1 for c in ISRAEL if c in have))

    data = {"v": 1, "t": int(time.time()), "c": guide}
    with open(out_path, "w", encoding="utf-8") as f:
        json.dump(data, f, ensure_ascii=False, separators=(",", ":"))
    print(f"{len(guide)} channels, {os.path.getsize(out_path) // 1024} KB -> {out_path}")


if __name__ == "__main__":
    main()

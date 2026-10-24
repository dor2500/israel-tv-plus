"""Adds working public channels from the iptv-org database (https://github.com/iptv-org/api) to channels.json.

Every candidate is tested for real: master playlist -> variant playlist -> first media segment must download,
then ffmpeg grabs a frame (the picture must decode). Streams whose frame is identical to another channel's
(the same feed under two names) are dropped. Channels that need a custom User-Agent/Referrer, NSFW channels,
closed channels, shopping channels and everything on the iptv-org blocklist are skipped.

usage:
  python -I tools/import_iptv.py probe  [work_dir]   # download db + test streams   (~20-40 min)
  python -I tools/import_iptv.py frames [work_dir]   # grab one frame per working stream
  python -I tools/import_iptv.py pick   [work_dir]   # balanced selection (~800), probed again -> picked.json
  python -I tools/import_iptv.py merge  [work_dir]   # write channels.json + channels.js (picked.json if present)
Imported entries carry "imported": true; merge replaces them, curated entries are left alone.
"""
import json
import os
import re
import subprocess
import sys
import urllib.parse
from concurrent.futures import ThreadPoolExecutor

import requests

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
WORK = os.path.join(ROOT, "..", "israel-tv-plus-work", "iptv")
API = "https://iptv-org.github.io/api/"
UA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0 Safari/537.36"
HEADERS = {"User-Agent": UA, "Origin": "https://dor2500.github.io", "Referer": "https://dor2500.github.io/"}
FIRST_NUMBER = 201

# iptv-org ISO 639-3 -> app language code (dub.js LANG_NAMES)
LANG3 = {"heb": "he", "eng": "en", "rus": "ru", "fra": "fr", "ara": "ar", "ukr": "uk", "spa": "es", "deu": "de",
         "slk": "sk", "ita": "it", "por": "pt", "tur": "tr", "pol": "pl", "nld": "nl", "ell": "el", "ron": "ro",
         "hun": "hu", "ces": "cs", "fas": "fa", "hin": "hi", "zho": "zh", "jpn": "ja", "kor": "ko", "swe": "sv",
         "bul": "bg", "srp": "sr", "hrv": "hr", "amh": "am", "yid": "yi", "lad": "lad"}
LANG_ORDER = ["he", "en", "ru", "ar", "fr", "es", "uk", "de", "it", "pt", "tr"]  # import priority

CAT_ISRAEL = "ערוצי ישראל - שידור חי"
CAT_KIDS_HE = "ילדים ונוער (בעברית)"
CAT_KIDS_WORLD = "ילדים מהעולם (Kids World)"
CAT_NEWS = "חדשות מהעולם (News)"
CAT_MOVIES = "סרטים וסדרות (Movies & Series)"
CAT_SPORTS = "ספורט (Sports)"
CAT_MUSIC = "מוזיקה (Music TV)"
CAT_DOCU = "טבע ודוקו (Documentary & Nature)"
CAT_LIFE = "לייף סטייל ובידור"
CAT_RELIGION = "דת ואמונה (Religion)"
CAT_WORLD = "ערוצים בינלאומיים (World)"
CAT_FOOD = "אוכל ובישול (Food)"
CAT_TRAVEL = "טיולים והרפתקאות (Travel)"
CAT_COMEDY = "קומדיה (Comedy)"
CAT_SCIENCE = "מדע ולמידה (Science)"
CAT_BUSINESS = "כלכלה ועסקים (Business)"
CATS = [  # first matching iptv-org category wins
    ("kids", "kids"), ("animation", "kids"), ("business", "business"), ("news", "news"), ("legislative", "news"),
    ("weather", "news"), ("sports", "sports"), ("cooking", "food"), ("music", "music"), ("movies", "movies"),
    ("series", "movies"), ("classic", "movies"), ("comedy", "comedy"), ("travel", "travel"), ("outdoor", "travel"),
    ("documentary", "docu"), ("science", "science"), ("education", "science"), ("religious", "religion"),
    ("lifestyle", "life"), ("entertainment", "life"), ("culture", "life"), ("family", "life"), ("auto", "life"),
    ("relax", "life"),
]
CAT_NAME = {"news": CAT_NEWS, "sports": CAT_SPORTS, "music": CAT_MUSIC, "movies": CAT_MOVIES, "docu": CAT_DOCU,
            "religion": CAT_RELIGION, "life": CAT_LIFE, "world": CAT_WORLD, "food": CAT_FOOD, "travel": CAT_TRAVEL,
            "comedy": CAT_COMEDY, "science": CAT_SCIENCE, "business": CAT_BUSINESS}
CAT_KIND_HE = {"kids": "ערוץ ילדים", "news": "ערוץ חדשות", "sports": "ערוץ ספורט", "music": "ערוץ מוזיקה",
               "movies": "סרטים וסדרות", "docu": "דוקו וטבע", "religion": "ערוץ דת ואמונה",
               "life": "בידור ולייף סטייל", "world": "ערוץ כללי", "food": "אוכל ובישול", "travel": "טיולים ומסעות",
               "comedy": "ערוץ קומדיה", "science": "מדע ולמידה", "business": "כלכלה ועסקים"}
SKIP_CATS = {"xxx", "shop", "interactive"}
SKIP_IDS = {"Channel13.il",  # curated "רשת 13" already plays Channel 13
            # paid Israeli channels (Sport 5 etc.) re-streamed from a bare IP without a licence
            "5Plus.il", "5Stars.il", "HealthChannel.il", "TeenNick.il",
            "NickOnline.br"}  # Nickelodeon cartoons on an unofficial site
# pay-TV sports / premium brands: a free stream of these is a re-stream without a licence (the free ESPN8 The Ocho,
# DAZN / Pluto FAST channels and public broadcasters don't match)
PAID_RE = re.compile(r"^(ESPN(?!8)|ESPN Premium|Fox Sports|Eurosport|Star Sports|Sony Sports|Ten Cricket|Eleven Sports|"
                     r"Okko|Match[!4 ]|TyC Sports|Win[+ ]|Win Sports|Tigo Sports|Canal\+|Golf Channel|Sky |"
                     # pay-TV kids / movie brands (their free FAST spin-offs on Pluto etc. are named differently)
                     r"Disney|Nickelodeon|Nick Jr|NickToons|Cartoon Network|Cartoonito|Boomerang|Baby ?TV|HBO|Cinemax|"
                     r"Showtime|Starz|beIN(?! SPORTS XTRA))", re.I)


def load(work, name):
    path = os.path.join(work, "db", name + ".json")
    if not os.path.isfile(path):
        os.makedirs(os.path.dirname(path), exist_ok=True)
        r = requests.get(API + name + ".json", timeout=120)
        r.raise_for_status()
        with open(path, "wb") as f:
            f.write(r.content)
    with open(path, encoding="utf-8") as f:
        return json.load(f)


def norm(s):
    return re.sub(r"[^a-z0-9]", "", (s or "").lower().replace("hd", ""))


def candidates(work):
    chans = {c["id"]: c for c in load(work, "channels")}
    feeds = {}
    for f in load(work, "feeds"):
        if f.get("is_main") or f["channel"] not in feeds:
            feeds[f["channel"]] = f
    blocked = {b["channel"] for b in load(work, "blocklist")}
    with open(os.path.join(ROOT, "channels.json"), encoding="utf-8") as f:
        ours = [c for c in json.load(f) if c.get("src") != "iptv-org" and not c.get("imported")]
    our_urls = {c["url"].split("://", 1)[-1] for c in ours}
    our_iptv = {c.get("iptv") for c in ours if c.get("iptv")}

    best = {}
    for s in load(work, "streams"):
        cid = s.get("channel")
        ch = chans.get(cid)
        if not ch or cid in blocked or cid in our_iptv or ch.get("is_nsfw") or ch.get("closed") or ch.get("replaced_by"):
            continue
        if s.get("user_agent") or s.get("referrer") or s["url"].split("://", 1)[-1] in our_urls:
            continue
        if SKIP_CATS & set(ch.get("categories") or []):
            continue
        if ".m3u8" not in s["url"]:
            continue
        q = int(re.sub(r"\D", "", s.get("quality") or "") or 0)
        score = (s["url"].startswith("https"), min(q, 1080), not s.get("feed") or s.get("feed") == (feeds.get(cid) or {}).get("id"))
        if cid not in best or score > best[cid][0]:
            best[cid] = (score, s)

    out = []
    for cid, (_, s) in best.items():
        ch = chans[cid]
        feed = feeds.get(cid) or {}
        langs = [LANG3.get(l, "auto") for l in feed.get("languages") or []]
        lang = next((l for l in langs if l != "auto"), langs[0] if langs else "auto")
        out.append({"iptv": cid, "name": ch["name"], "country": ch.get("country"), "categories": ch.get("categories") or [],
                    "lang": lang, "url": s["url"], "quality": s.get("quality"), "website": ch.get("website")})
    rank = lambda c: (c["country"] != "IL", LANG_ORDER.index(c["lang"]) if c["lang"] in LANG_ORDER else 99, c["name"].lower())
    out.sort(key=rank)
    return out


def get(url, n=None):
    r = requests.get(url, headers=HEADERS, timeout=10, stream=True, allow_redirects=True)
    data = r.raw.read(n or 400000, decode_content=True)
    r.close()
    return r, data


def probe(c):
    """master -> variant -> first segment. Fills status / cors / https / error."""
    try:
        r, data = get(c["url"])
        c["cors"] = r.headers.get("Access-Control-Allow-Origin", "")
        if r.status_code != 200 or not data.lstrip().startswith(b"#EXTM3U"):
            c["error"] = f"master {r.status_code}"
            return c
        url, text = r.url, data.decode("utf-8", "replace")
        for _ in range(3):
            lines = [l.strip() for l in text.splitlines() if l.strip() and not l.startswith("#")]
            if not lines:
                c["error"] = "empty playlist"
                return c
            nxt = urllib.parse.urljoin(url, lines[0])
            if "#EXT-X-STREAM-INF" in text:
                r, data = get(nxt)
                if r.status_code != 200 or not data.lstrip().startswith(b"#EXTM3U"):
                    c["error"] = f"variant {r.status_code}"
                    return c
                url, text = r.url, data.decode("utf-8", "replace")
                continue
            if "#EXT-X-ENDLIST" in text:
                c["error"] = "vod, not live"
                return c
            r, data = get(nxt, 2048)
            if r.status_code not in (200, 206) or len(data) < 188:
                c["error"] = f"segment {r.status_code}"
                return c
            c["ok"] = True
            return c
        c["error"] = "playlist loop"
    except Exception as e:  # noqa: BLE001
        c["error"] = type(e).__name__
    return c


def cmd_probe(work):
    cands = candidates(work)
    print(len(cands), "candidates")
    done = 0
    results = []
    with ThreadPoolExecutor(48) as ex:
        for c in ex.map(probe, cands):
            results.append(c)
            done += 1
            if done % 500 == 0:
                print(done, "probed,", sum(1 for x in results if x.get("ok")), "ok", flush=True)
    with open(os.path.join(work, "probe.json"), "w", encoding="utf-8") as f:
        json.dump(results, f, ensure_ascii=False, indent=0)
    ok = [c for c in results if c.get("ok")]
    print(f"{len(results)} probed, {len(ok)} playable, https {sum(1 for c in ok if c['url'].startswith('https'))}, "
          f"cors {sum(1 for c in ok if c.get('cors'))}")


def frame(c, work):
    jpg = os.path.join(work, "frames", re.sub(r"[^\w.-]", "_", c["iptv"]) + ".jpg")
    cmd = ["ffmpeg", "-hide_banner", "-loglevel", "error", "-y", "-user_agent", UA, "-rw_timeout", "15000000",
           "-i", c["url"], "-frames:v", "1", "-vf", "scale=320:-2", jpg]
    try:
        p = subprocess.run(cmd, capture_output=True, timeout=45, text=True)
        c["frame"] = os.path.basename(jpg) if p.returncode == 0 and os.path.isfile(jpg) else None
    except subprocess.TimeoutExpired:
        c["frame"] = None
    return c


def ahash(path):
    from PIL import Image
    im = Image.open(path).convert("L").resize((16, 9))
    px = list(im.tobytes())
    m = sum(px) / len(px)
    return px, [p > m for p in px]


def cmd_frames(work):
    with open(os.path.join(work, "probe.json"), encoding="utf-8") as f:
        ok = [c for c in json.load(f) if c.get("ok")]
    os.makedirs(os.path.join(work, "frames"), exist_ok=True)
    res = []
    with ThreadPoolExecutor(24) as ex:
        for c in ex.map(lambda c: frame(c, work), ok):
            res.append(c)
            if len(res) % 500 == 0:
                print(len(res), "grabbed,", sum(1 for x in res if x.get("frame")), "with a picture", flush=True)
    good = [c for c in res if c.get("frame")]
    # identical pictures under different names = the same feed -> keep the first (higher priority) only
    hashes = []
    for c in good:
        px, bits = ahash(os.path.join(work, "frames", c["frame"]))
        flat = max(px) - min(px) < 12  # black / single colour frames say nothing
        dup = None
        if not flat:
            for other, opx, obits in hashes:
                if sum(a != b for a, b in zip(bits, obits)) < 8 and sum(abs(a - b) for a, b in zip(px, opx)) / len(px) < 8:
                    dup = other
                    break
            hashes.append((c["iptv"], px, bits))
        c["dup_of"] = dup
    with open(os.path.join(work, "frames.json"), "w", encoding="utf-8") as f:
        json.dump(res, f, ensure_ascii=False, indent=0)
    print(f"{len(ok)} playable, {len(good)} with a picture, {sum(1 for c in good if c['dup_of'])} duplicates")


def he_names(work):
    """Hebrew country / language names via Node's ICU (Python has none built in)."""
    countries = [c["code"] for c in load(work, "countries")]
    js = ("const r=new Intl.DisplayNames(['he'],{type:'region'}),l=new Intl.DisplayNames(['he'],{type:'language'});"
          "const c=JSON.parse(process.argv[1]),o={c:{},l:{}};for(const x of c){try{o.c[x]=r.of(x)}catch(e){}}"
          "for(const x of JSON.parse(process.argv[2])){try{o.l[x]=l.of(x)}catch(e){}}console.log(JSON.stringify(o))")
    p = subprocess.run(["node", "-e", js, json.dumps(countries), json.dumps(sorted(set(LANG3.values())))],
                       capture_output=True, text=True, encoding="utf-8", check=True)
    return json.loads(p.stdout)


def pick_logos(work):
    logos = {}
    for l in load(work, "logos"):
        if l.get("feed") or not l.get("in_use", True):
            continue
        w, h = l.get("width") or 0, l.get("height") or 1
        score = (l.get("format") in ("PNG", "SVG", "WebP"), 0.8 < w / h < 4, min(w, 600))
        if l["channel"] not in logos or score > logos[l["channel"]][0]:
            logos[l["channel"]] = (score, l["url"])
    return {k: v[1] for k, v in logos.items()}


def kind(c):
    cats = set(c["categories"])
    return next((k for cat, k in CATS if cat in cats), "world")


# "pick": a balanced selection (~800) out of the thousands of working streams: every Israeli / Hebrew channel,
# then per category a quota filled round-robin over countries (big / relevant countries first, a cap per country),
# preferring streams the https web app can play, HD, and channels that have a logo. The pick is probed again.
QUOTA = {"news": 400, "sports": 300, "movies": 450, "life": 350, "music": 350, "world": 500, "kids": 220, "docu": 110,
         "food": 120, "travel": 70, "comedy": 80, "science": 110, "business": 50, "religion": 100}
COUNTRY_ORDER = ["IL", "US", "UK", "FR", "DE", "ES", "IT", "RU", "UA", "CA", "AU", "AE", "QA", "SA", "EG", "LB", "TR",
                 "IN", "JP", "KR", "CN", "BR", "MX", "AR", "PT", "NL", "BE", "CH", "AT", "PL", "GR", "SE", "NO", "DK",
                 "FI", "IE", "CZ", "RO", "HU", "CL", "CO", "PE", "PH", "ID", "TH", "VN", "PK", "IR", "MA", "ZA", "NG"]


def cmd_pick(work):
    with open(os.path.join(work, "frames.json"), encoding="utf-8") as f:
        res = [c for c in json.load(f) if c.get("frame") and not c.get("dup_of")]
    logos = pick_logos(work)
    web = lambda c: c["url"].startswith("https") and bool(c.get("cors"))
    qual = lambda c: min(int(re.sub(r"\D", "", c.get("quality") or "") or 0), 1080)
    score = lambda c: (c["iptv"] in logos, web(c), qual(c), c["lang"] in LANG_ORDER)
    crank = lambda cc: COUNTRY_ORDER.index(cc) if cc in COUNTRY_ORDER else len(COUNTRY_ORDER)
    must = [c for c in res if c["country"] == "IL" or c["lang"] in ("he", "yi")]
    rest = [c for c in res if c not in must and not PAID_RE.match(c["name"])]
    pool = []
    for k, quota in QUOTA.items():
        groups = {}
        for c in rest:
            if kind(c) == k:
                groups.setdefault(c["country"] or "", []).append(c)
        for g in groups.values():
            g.sort(key=score, reverse=True)
        order = sorted(groups, key=lambda cc: (crank(cc), -len(groups[cc])))
        cap = max(8, quota // 4)  # rounds per country: small categories can use their whole pool
        want, taken, rnd = int(quota * 1.3), [], 0
        while len(taken) < want and rnd < cap:
            for cc in order:
                if rnd < len(groups[cc]) and len(taken) < want:
                    taken.append(groups[cc][rnd])
            rnd += 1
        pool.extend((k, c) for c in taken)
    print(len(must), "Israeli/Hebrew +", len(pool), "pool candidates, re-probing...")
    fresh = list(ThreadPoolExecutor(48).map(lambda c: probe(dict(c, ok=False, error=None)), must + [c for _, c in pool]))
    alive = {c["iptv"] for c in fresh if c.get("ok")}
    out = [c for c in must if c["iptv"] in alive]
    for k, quota in QUOTA.items():
        out.extend([c for kk, c in pool if kk == k and c["iptv"] in alive][:quota])
    with open(os.path.join(work, "picked.json"), "w", encoding="utf-8") as f:
        json.dump(out, f, ensure_ascii=False, indent=0)
    print(len(out), "picked;", sum(1 for c in out if web(c)), "play on the web;",
          len({c['country'] for c in out}), "countries")


def cmd_merge(work):
    picked = os.path.join(work, "picked.json")
    with open(picked if os.path.isfile(picked) else os.path.join(work, "frames.json"), encoding="utf-8") as f:
        res = [c for c in json.load(f) if c.get("frame") and not c.get("dup_of")]
    with open(os.path.join(ROOT, "channels.json"), encoding="utf-8") as f:
        ours = [c for c in json.load(f) if c.get("src") != "iptv-org" and not c.get("imported")]
    names = he_names(work)
    logos = pick_logos(work)
    taken_ids = {c["id"] for c in ours}
    taken_names = {norm(c.get("title") or c["name"]) for c in ours} | {norm(c["name"]) for c in ours}
    taken_iptv = {c.get("iptv") for c in ours if c.get("iptv")}
    taken_urls = {c["url"].split("://", 1)[-1] for c in ours} | {c.get("webUrl", "").split("://", 1)[-1] for c in ours}

    order = [CAT_ISRAEL, CAT_KIDS_HE, CAT_SPORTS, CAT_NEWS, CAT_BUSINESS, CAT_MOVIES, CAT_COMEDY, CAT_FOOD,
             CAT_KIDS_WORLD, CAT_MUSIC, CAT_DOCU, CAT_TRAVEL, CAT_SCIENCE, CAT_LIFE, CAT_RELIGION, CAT_WORLD]
    new = []
    for c in res:
        k = kind(c)
        if c["country"] == "IL":
            cat = CAT_ISRAEL if k not in ("kids",) or c["lang"] != "he" else CAT_KIDS_HE
        elif k == "kids":
            cat = CAT_KIDS_HE if c["lang"] == "he" else CAT_KIDS_WORLD
        else:
            cat = CAT_NAME[k]
        if PAID_RE.match(c["name"]):
            continue
        if norm(c["name"]) in taken_names and c["country"] == "IL" or c["iptv"] in SKIP_IDS or c["iptv"] in taken_iptv:
            continue  # curated version already there
        if c["url"].split("://", 1)[-1] in taken_urls:
            continue  # same stream under another name
        taken_urls.add(c["url"].split("://", 1)[-1])
        cid = "iptv_" + re.sub(r"[^a-z0-9]+", "_", c["iptv"].lower()).strip("_")
        if cid in taken_ids:
            continue
        taken_ids.add(cid)
        country = names["c"].get(c["country"], c["country"] or "")
        lang = "music" if k == "music" else c["lang"]
        lang_he = names["l"].get(c["lang"], "")
        q = c.get("quality") or ""
        new.append({
            "id": cid,
            "name": c["name"],
            "title": f"{c['name']} ({country})" if country else c["name"],
            "category": cat,
            "categoryEn": k,
            "url": c["url"],
            "logo": logos.get(c["iptv"], ""),
            "badge": "FHD" if q.startswith("1080") or q.startswith("2160") else ("HD" if q.startswith("720") else "SD"),
            "desc": " · ".join(x for x in (CAT_KIND_HE[k], country, lang_he) if x),
            "lang": lang,
            "country": c["country"],
            "countryHe": country,
            "iptv": c["iptv"],
            "imported": True,
        })
        if c["url"].startswith("https") and not c.get("cors"):
            new[-1]["webOk"] = False  # the https web app can't read it (no CORS); the apps can
    new.sort(key=lambda c: order.index(c["category"]))
    for i, c in enumerate(new):
        c["number"] = FIRST_NUMBER + i
    ours.sort(key=lambda c: c.get("number", 0))   # curated: 1.. in their own order, below FIRST_NUMBER
    for i, c in enumerate(ours):
        c["number"] = i + 1
    allc = ours + new
    with open(os.path.join(ROOT, "channels.json"), "w", encoding="utf-8") as f:
        json.dump(allc, f, ensure_ascii=False, indent=2)
    with open(os.path.join(ROOT, "channels.js"), "w", encoding="utf-8") as f:
        f.write("window.CHANNELS_DATA = " + json.dumps(allc, ensure_ascii=False, indent=1) + ";\n")
    by = {}
    for c in new:
        by[c["category"]] = by.get(c["category"], 0) + 1
    print(f"curated {len(ours)} + imported {len(new)} = {len(allc)}")
    for k, v in by.items():
        print(f"  {v:5d}  {k}")


def main():
    cmds = {"probe": cmd_probe, "frames": cmd_frames, "pick": cmd_pick, "merge": cmd_merge}
    if len(sys.argv) < 2 or sys.argv[1] not in cmds:
        print(__doc__)
        sys.exit(1)
    work = sys.argv[2] if len(sys.argv) > 2 else WORK
    os.makedirs(work, exist_ok=True)
    cmds[sys.argv[1]](work)


if __name__ == "__main__":
    main()

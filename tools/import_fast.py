"""Sports and kids channels from the free ad-supported (FAST) services Pluto TV and Samsung TV Plus.

The channel lists come from i.mjh.nz (https://i.mjh.nz/PlutoTV/.channels.json.gz, .../SamsungTVPlus/...), the streams
are the services' own, reached through jmp2.uk (the same redirect the app's other Pluto channels use). Every stream is
probed (master -> variant -> segment); sports pay-TV brands (import_iptv.PAID_RE), hostile channels
(import_iptv.hostile) and channels the app already has are left out. Kids channels keep their brand names: on these
services Nickelodeon, Pokémon, Peppa Pig etc. are the rights holders' own free channels. The result is written to
channels.json as imported channels with "fast": true; import_iptv.py merge keeps them and numbers them with the rest.

usage: python -I tools/import_fast.py <work dir> [--all]   (downloads the lists into <work dir>)
  --all: every channel of the services (news, movies, music, docu, ...), not only sports and kids, each sorted
         into the app's categories by the service's group name
"""
import concurrent.futures
import gzip
import json
import os
import re
import sys

import requests

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
sys.path.insert(0, os.path.join(ROOT, "tools"))
import import_iptv as ii  # noqa: E402

SERVICES = {   # list -> (stream address on jmp2.uk, service name)
    "PlutoTV": ("plu-{id}.m3u8", "Pluto TV"),
    "SamsungTVPlus": ("stvp-{id}", "Samsung TV Plus"),   # redirects to the service's Google DAI stream
}
SPORT_GROUPS = re.compile(r"sport|deport|esport|motor", re.I)
KIDS_GROUPS = re.compile(r"kid|niñ|infantil|bambini|barn|enfant|kinder", re.I)
# what is imported: kind -> (the services' group names, the app's category, the first word of the description)
KINDS = {"sports": (SPORT_GROUPS, ii.CAT_SPORTS, "ספורט"), "kids": (KIDS_GROUPS, ii.CAT_KIDS_WORLD, "ערוץ ילדים")}
# --all: the services' group names -> the app's kinds (first match wins; the rest is "life")
GROUP_KINDS = [
    ("kids", KIDS_GROUPS), ("sports", SPORT_GROUPS),
    ("news", re.compile(r"news|notici|nachricht|notizie|nyhet|actualit|info|뉴스", re.I)),
    ("music", re.compile(r"music|musik|músic|musica|musique|karaoke|음악", re.I)),
    ("food", re.compile(r"food|cook|cocina|küche|cucina|cuisine|home", re.I)),
    ("travel", re.compile(r"travel|reise|viaj|viagg|voyage|outdoor", re.I)),
    ("docu", re.compile(r"docu|doku|natur|history|histor|science|wissen|curios|ciencia|discover|knowledge|다큐", re.I)),
    ("comedy", re.compile(r"comed|komöd|sitcom|humor|south park|funny|risa", re.I)),
    ("movies", re.compile(r"movie|film|cine|kino|serie|drama|crime|action|sci-fi|thriller|western|classic|anime|novela|"
                          r"películ|드라마|영화|krimi|polar|giallo", re.I)),
    ("religion", re.compile(r"faith|relig|iglesi|gospel", re.I)),
]
SHOPPING = re.compile(r"shop|teleshop|qvc|hsn|einkauf", re.I)
BETTING = re.compile(r"(?i)(^|[^a-z])bet[0-9]|betting|casino|bookmaker")   # gambling companies' channels
# region -> (Hebrew country, language); the order is the preference when one channel airs in several regions
REGIONS = {
    "us": ("ארצות הברית", "en"), "gb": ("בריטניה", "en"), "ca": ("קנדה", "en"), "au": ("אוסטרליה", "en"),
    "nz": ("ניו זילנד", "en"), "in": ("הודו", "en"), "ie": ("אירלנד", "en"),
    "de": ("גרמניה", "de"), "at": ("אוסטריה", "de"), "ch": ("שווייץ", "de"),
    "fr": ("צרפת", "fr"), "it": ("איטליה", "it"), "es": ("ספרד", "es"), "mx": ("מקסיקו", "es"),
    "ar": ("ארגנטינה", "es"), "cl": ("צ'ילה", "es"), "co": ("קולומביה", "es"), "pe": ("פרו", "es"),
    "br": ("ברזיל", "pt"), "dk": ("דנמרק", "da"), "no": ("נורווגיה", "no"), "se": ("שוודיה", "sv"),
    "fi": ("פינלנד", "fi"), "nl": ("הולנד", "nl"), "kr": ("דרום קוריאה", "ko"),
}
LANG_HE = {"en": "אנגלית", "de": "גרמנית", "fr": "צרפתית", "it": "איטלקית", "es": "ספרדית", "pt": "פורטוגזית",
           "da": "דנית", "no": "נורווגית", "sv": "שוודית", "fi": "פינית", "nl": "הולנדית", "ko": "קוריאנית"}


def load(work, service):
    path = os.path.join(work, service + ".json")
    if not os.path.isfile(path):
        r = requests.get(f"https://i.mjh.nz/{service}/.channels.json.gz", timeout=60)
        r.raise_for_status()
        with open(path, "wb") as f:
            f.write(r.content)
    raw = open(path, "rb").read()
    try:
        raw = gzip.decompress(raw)
    except OSError:
        pass
    return json.loads(raw)


def candidates(work):
    out = {}
    order = list(REGIONS)
    for service, (slug, label) in SERVICES.items():
        data = load(work, service)
        for region, rv in data["regions"].items():
            if region not in REGIONS:
                continue
            for cid, ch in rv.get("channels", {}).items():
                group = ch.get("group") or ""
                if ALL:
                    if SHOPPING.search(group) or SHOPPING.search(ch.get("name") or ""):
                        continue
                    kind = next((k for k, groups in GROUP_KINDS if groups.search(group)), "life")
                else:
                    kind = next((k for k, (groups, _, _) in KINDS.items() if groups.search(group)), None)
                if not kind:
                    continue
                name = (ch.get("name") or "").strip()
                if not name or (kind == "sports" and ii.PAID_RE.match(name)) or BETTING.search(name):
                    continue
                if ii.hostile({"name": name}):
                    continue
                key = ii.norm(name)
                c = {"key": key, "name": name, "region": region, "service": label, "url": "https://jmp2.uk/" + slug.format(id=cid),
                     "logo": ch.get("logo") or "", "desc": (ch.get("description") or "").strip(), "fid": slug.split("-")[0] + "_" + cid,
                     "kind": kind}
                old = out.get(key)
                if old is None or order.index(region) < order.index(old["region"]):
                    out[key] = c
    return list(out.values())


ALL = "--all" in sys.argv


def category(kind):
    """kind -> (the app's category, the first words of the description)"""
    if kind in KINDS:
        return KINDS[kind][1], KINDS[kind][2]
    return ii.CAT_NAME[kind], ii.CAT_KIND_HE[kind]


def main():
    args = [a for a in sys.argv[1:] if not a.startswith("--")]
    work = args[0] if args else os.path.join(ROOT, "..", "israel-tv-plus-work", "fast")
    os.makedirs(work, exist_ok=True)
    with open(os.path.join(ROOT, "channels.json"), encoding="utf-8") as f:
        chans = json.load(f)
    chans = [c for c in chans if not c.get("fast")]
    have_names = {ii.norm(c.get("title") or c["name"]) for c in chans} | {ii.norm(c["name"]) for c in chans}
    have_urls = {c["url"].split("://", 1)[-1] for c in chans}
    cands = [c for c in candidates(work) if c["key"] not in have_names and c["url"].split("://", 1)[-1] not in have_urls]
    print(len(cands), "candidates")
    with concurrent.futures.ThreadPoolExecutor(48 if ALL else 24) as ex:
        res = list(ex.map(ii.probe, cands))
    ok = [c for c in res if c.get("ok")]
    print(len(ok), "playable")
    new = []
    kinds = [k for k, _ in GROUP_KINDS] + ["life"]
    for c in sorted(ok, key=lambda c: (kinds.index(c["kind"]), list(REGIONS).index(c["region"]), c["name"].lower())):
        country, lang = REGIONS[c["region"]]
        cat, what = category(c["kind"])
        new.append({
            "id": "fast_" + re.sub(r"[^a-z0-9]+", "_", c["fid"].lower()).strip("_"),
            "name": c["name"],
            "title": f"{c['name']} ({country})",
            "category": cat,
            "categoryEn": c["kind"],
            "url": c["url"],
            "logo": c["logo"],
            "badge": "HD",
            "desc": " · ".join(x for x in (what, country, LANG_HE.get(lang, ""), c["service"]) if x),
            "lang": lang,
            "country": c["region"].upper(),
            "countryHe": country,
            "imported": True,
            "fast": True,
            # jmp2.uk's redirect has no CORS header: browsers (the web site) can't follow it, the apps can
            "webOk": False,
        })
    allc = chans + new
    with open(os.path.join(ROOT, "channels.json"), "w", encoding="utf-8") as f:
        json.dump(allc, f, ensure_ascii=False, indent=2)
    by = {k: sum(1 for c in new if c["categoryEn"] == k) for k in kinds if any(c["categoryEn"] == k for c in new)}
    print(f"+{len(new)} channels {by} ({len(allc)} in all); now run: python -I tools/import_iptv.py merge")


if __name__ == "__main__":
    main()

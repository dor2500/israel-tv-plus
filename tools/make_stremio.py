"""Stremio addon (static: plain JSON files served by GitHub Pages, no server needed) with every channel and station.

usage: python -I tools/make_stremio.py
writes stremio/manifest.json, stremio/catalog/tv/<cat>.json, stremio/meta/tv/<id>.json, stremio/stream/tv/<id>.json
and stremio/index.html (install page). Install URL: https://dor2500.github.io/israel-tv-plus/stremio/manifest.json

Each category is a catalog (a row on Stremio's board and in Discover). Channels open with their stream (http
streams are marked notWebReady, so Stremio plays them in its own player); the TV-service tiles open the official
site. The description of every channel says where the stream comes from (same data as SOURCES.md).
"""
import json
import os
import re
import shutil

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
SITE = "https://dor2500.github.io/israel-tv-plus/"
OUT = os.path.join(ROOT, "stremio")
KIND = {"official": "✅ שידור רשמי", "fast": "✅ ערוץ FAST רשמי", "restream": "⚠️ ממסר צד שלישי"}
CAT_IDS = {
    "ערוצי ישראל - שידור חי": "israel", "ילדים ונוער (בעברית)": "kids_he", "רדיו ישראלי (Israeli Radio)": "radio",
    "מנויים ואפליקציות (TV Apps)": "apps", "סרטים וסדרות (Movies & Series)": "movies", "ספורט (Sports)": "sports",
    "ילדים מהעולם (Kids World)": "kids_world", "מוזיקה (Music TV)": "music", "טבע ודוקו (Documentary & Nature)": "docu",
    "לייף סטייל ובידור": "life", "מסורת וקהילה": "community", "ערוצים בינלאומיים (World)": "world",
    "חדשות מהעולם (News)": "news",
}
CAT_NAMES = {"ערוצי ישראל - שידור חי": "ערוצי ישראל", "ילדים ונוער (בעברית)": "ילדים בעברית",
             "רדיו ישראלי (Israeli Radio)": "רדיו ישראלי", "מנויים ואפליקציות (TV Apps)": "מנויים ואפליקציות"}


def short_cat(c):
    return CAT_NAMES.get(c) or re.sub(r"\s*\([^)]*[A-Za-z][^)]*\)\s*$", "", c)


def image(c):
    logo = c.get("logo") or ""
    if not logo:
        return SITE + "assets/app/icon-512.png"
    if logo.startswith("http"):
        return logo
    if logo.endswith(".svg"):  # Stremio apps can't draw SVG: the PNG copies made for the M3U playlists
        png = os.path.join(ROOT, "playlist-logos", c["id"] + ".png")
        if os.path.isfile(png):
            return SITE + "playlist-logos/" + c["id"] + ".png"
    return SITE + logo


def dump(path, data):
    path = os.path.join(OUT, path)
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8", newline="\n") as f:
        json.dump(data, f, ensure_ascii=False, separators=(",", ":"))


def main():
    with open(os.path.join(ROOT, "channels.json"), encoding="utf-8") as f:
        chans = json.load(f)
    with open(os.path.join(ROOT, "VERSION"), encoding="utf-8") as f:
        version = f.read().strip()
    if os.path.isdir(OUT):
        shutil.rmtree(OUT)

    cats = []
    for c in chans:
        if c["category"] not in cats:
            cats.append(c["category"])
    manifest = {
        "id": "com.israeltv.plus",
        "version": version,
        "name": "ישראל TV+",
        "description": f"{len(chans)} ערוצי טלוויזיה ותחנות רדיו בשידור חי: ערוצי ישראל, ילדים בעברית, רדיו, סרטים, ספורט ועוד. "
                       "בכל ערוץ כתוב מאיפה מגיע השידור. github.com/dor2500/israel-tv-plus",
        "logo": SITE + "assets/app/icon-512.png",
        "background": SITE + "assets/app/banner-1280x720.png",
        "types": ["tv"],
        "resources": ["catalog", {"name": "meta", "types": ["tv"], "idPrefixes": ["itv_"]},
                      {"name": "stream", "types": ["tv"], "idPrefixes": ["itv_"]}],
        "idPrefixes": ["itv_"],
        "catalogs": [{"type": "tv", "id": "itv_" + CAT_IDS.get(cat, re.sub(r"\W+", "_", cat)),
                      "name": "ישראל TV+ · " + short_cat(cat)} for cat in cats],
        "behaviorHints": {"configurable": False, "configurationRequired": False},
    }
    dump("manifest.json", manifest)

    for cat, cat_info in zip(cats, manifest["catalogs"]):
        metas = []
        for c in chans:
            if c["category"] != cat:
                continue
            src = c.get("src") or {}
            app = c.get("kind") == "app"
            desc = c.get("desc") or ""
            if app:
                desc += "\n\nשירות מנויים – נפתח באתר / באפליקציה הרשמית (צפייה עם מנוי)."
            else:
                desc += f"\n\n📡 מקור השידור: {src.get('by', '')}\n{KIND.get(src.get('kind'), '')}"
            meta = {
                "id": "itv_" + c["id"], "type": "tv", "name": f"{c['number']}. {c['name']}",
                "poster": image(c), "posterShape": "landscape", "logo": image(c), "background": image(c),
                "description": desc.strip(), "genres": [short_cat(cat)],
                "releaseInfo": "רדיו" if c.get("kind") == "radio" else ("אפליקציה" if app else "LIVE"),
            }
            metas.append({k: meta[k] for k in ("id", "type", "name", "poster", "posterShape", "description", "genres")})
            dump(f"meta/tv/itv_{c['id']}.json", {"meta": meta})

            if app:
                streams = [{"name": "ישראל TV+", "title": "פתיחה באתר הרשמי של " + c["name"], "externalUrl": c.get("site") or c["url"]}]
            else:
                title = f"{c['name']}\n📡 {src.get('by', '')}\n{KIND.get(src.get('kind'), '')}"
                http = c["url"].startswith("http:")
                streams = [{"name": "ישראל TV+\nLIVE", "title": title, "url": c["url"],
                            "behaviorHints": {"notWebReady": True, "bingeGroup": "itv-live"} if http or c.get("webOk") is False
                            else {"bingeGroup": "itv-live"}}]
                if c.get("webUrl"):  # https copy: also plays in Stremio Web
                    streams.append({"name": "ישראל TV+\nHTTPS", "title": c["name"] + "\nמקור חלופי (https)", "url": c["webUrl"],
                                    "behaviorHints": {"bingeGroup": "itv-live"}})
            dump(f"stream/tv/itv_{c['id']}.json", {"streams": streams})
        dump(f"catalog/tv/{cat_info['id']}.json", {"metas": metas})

    url = SITE + "stremio/manifest.json"
    page = f"""<!DOCTYPE html>
<html lang="he" dir="rtl"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
<title>ישראל TV+ ל-Stremio</title>
<style>body{{margin:0;background:#05070d;color:#f5f7fb;font-family:Heebo,Arial,sans-serif;display:flex;min-height:100vh;align-items:center;justify-content:center}}
main{{max-width:720px;padding:32px;text-align:center}}img{{width:120px;border-radius:24px}}h1{{font-size:36px;margin:16px 0 8px}}
p{{color:#9aa7bd;font-size:18px;line-height:1.6}}a.btn{{display:inline-block;margin:8px;padding:14px 26px;border-radius:12px;font-size:20px;font-weight:700;text-decoration:none;background:#7b5bf5;color:#fff}}
a.btn.alt{{background:#1a2438}}code{{display:block;direction:ltr;background:#121a2b;padding:12px;border-radius:10px;margin:12px 0;word-break:break-all;font-size:15px}}</style></head>
<body><main><img src="../assets/app/icon-512.png" alt=""><h1>ישראל TV+ ל-Stremio</h1>
<p>{len(chans)} ערוצים ותחנות רדיו בשידור חי, מסודרים ב-{len(cats)} קטגוריות. בכל ערוץ כתוב מאיפה מגיע השידור.</p>
<a class="btn" href="stremio://dor2500.github.io/israel-tv-plus/stremio/manifest.json">התקנה ב-Stremio</a>
<a class="btn alt" href="https://web.stremio.com/#/addons?addon={url.replace(':', '%3A').replace('/', '%2F')}">התקנה ב-Stremio Web</a>
<p>או הדביקו את הכתובת בחיפוש התוספים של Stremio:</p><code>{url}</code>
<p>ערוצי HTTP מתנגנים באפליקציות Stremio (מחשב, אנדרואיד, טלוויזיה) ולא ב-Stremio Web. דיבוב לעברית, מועדפים ומצב ילדים קיימים באפליקציה ישראל TV+ עצמה.</p>
</main></body></html>
"""
    with open(os.path.join(OUT, "index.html"), "w", encoding="utf-8", newline="\n") as f:
        f.write(page)
    print(f"stremio: {len(chans)} channels in {len(cats)} catalogs -> {url}")


if __name__ == "__main__":
    main()

"""Writes CHANNELS.md: the full channel list for GitHub (generated from channels.json).

usage: python -I tools/make_channel_list.py
One section per category (same order as the Stremio board), a table of contents with counts, and per channel:
number, logo, name, country, language, quality and where it plays (web site / apps). SOURCES.md has the
technical source details of the same channels.
"""
import json
import os
import re
import subprocess

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
SITE = "https://dor2500.github.io/israel-tv-plus/"
ORDER = ["ערוצי ישראל - שידור חי", "ילדים ונוער (בעברית)", "רדיו ישראלי (Israeli Radio)", "מסורת וקהילה",
         "חדשות מהעולם (News)", "סרטים וסדרות (Movies & Series)", "ספורט (Sports)", "ילדים מהעולם (Kids World)",
         "טבע ודוקו (Documentary & Nature)", "מוזיקה (Music TV)", "לייף סטייל ובידור", "דת ואמונה (Religion)",
         "ערוצים בינלאומיים (World)", "מנויים ואפליקציות (TV Apps)"]
ICON = {"ערוצי ישראל - שידור חי": "🇮🇱", "ילדים ונוער (בעברית)": "🧸", "רדיו ישראלי (Israeli Radio)": "📻",
        "מסורת וקהילה": "🕯️", "חדשות מהעולם (News)": "📰", "סרטים וסדרות (Movies & Series)": "🎬", "ספורט (Sports)": "⚽",
        "ילדים מהעולם (Kids World)": "🎈", "טבע ודוקו (Documentary & Nature)": "🌍", "מוזיקה (Music TV)": "🎵",
        "לייף סטייל ובידור": "🍿", "דת ואמונה (Religion)": "🙏", "ערוצים בינלאומיים (World)": "🌐",
        "מנויים ואפליקציות (TV Apps)": "📲"}
ISRAELI = set(ORDER[:4]) | {"מנויים ואפליקציות (TV Apps)"}


def md(s):
    return str(s or "").replace("|", "\\|").replace("\u2066", "").replace("\u2069", "")


def flag(cc):
    cc = {"UK": "GB"}.get(cc or "", cc or "")
    return "".join(chr(0x1F1E6 + ord(ch) - 65) for ch in cc) if re.fullmatch(r"[A-Z]{2}", cc) else ""


def lang_names(codes):
    js = ("const l=new Intl.DisplayNames(['he'],{type:'language'}),o={};"
          "for(const x of JSON.parse(process.argv[1])){try{o[x]=l.of(x)}catch(e){}}console.log(JSON.stringify(o))")
    try:
        p = subprocess.run(["node", "-e", js, json.dumps(sorted(codes))], capture_output=True, text=True,
                           encoding="utf-8", check=True)
        return json.loads(p.stdout)
    except (OSError, subprocess.CalledProcessError, ValueError):
        return {}


def logo(c):
    u = c.get("logo") or ""
    if not u:
        return ""
    if not u.startswith("http"):
        if u.endswith(".svg") and os.path.isfile(os.path.join(ROOT, "playlist-logos", c["id"] + ".png")):
            u = "playlist-logos/" + c["id"] + ".png"
    return f'<img src="{u}" width="56" alt="">'


def plays(c):
    if c.get("kind") == "app":
        return "📲 פותח את האפליקציה הרשמית"
    web = not (c.get("webOk") is False or (c["url"].startswith("http:") and not c.get("webUrl")))
    return "🌐 אתר + 📲 אפליקציות" if web else "📲 אפליקציות בלבד"


def main():
    with open(os.path.join(ROOT, "channels.json"), encoding="utf-8") as f:
        chans = json.load(f)
    names = lang_names({c.get("lang") for c in chans if c.get("lang") and len(c["lang"]) <= 3})
    names.update({"music": "מוזיקה", "auto": "—", "he": "עברית"})
    cats = sorted({c["category"] for c in chans}, key=lambda k: ORDER.index(k) if k in ORDER else 99)
    tv = sum(1 for c in chans if c.get("kind") not in ("radio", "app"))
    radio = sum(1 for c in chans if c.get("kind") == "radio")
    apps = sum(1 for c in chans if c.get("kind") == "app")
    countries = {c.get("country") or ("IL" if c["category"] in ISRAELI else "") for c in chans} - {""}
    web = sum(1 for c in chans if plays(c).startswith("🌐"))

    L = ["# 📺 כל הערוצים – ישראל TV+", "",
         f"נוצר אוטומטית מ-`channels.json` (`tools/make_channel_list.py`). **{len(chans)}** פריטים: "
         f"**{tv}** ערוצי טלוויזיה, **{radio}** תחנות רדיו ו-**{apps}** שירותי מנויים, מ-**{len(countries)}** מדינות. "
         f"{web} מהם מתנגנים גם באתר; כל השאר באפליקציות (Android, טלוויזיה, Windows, Mac, Linux) וב-Stremio.", "",
         f"**▶ צפייה:** {SITE} · **🎬 Stremio:** {SITE}stremio/ · **📋 מקור כל שידור:** [SOURCES.md](SOURCES.md)", "",
         "## תוכן", ""]
    for cat in cats:
        n = sum(1 for c in chans if c["category"] == cat)
        anchor = re.sub(r"[^\w\- ]", "", f"{cat}").strip().replace(" ", "-").lower()
        L.append(f"- {ICON.get(cat, '📺')} [{cat}](#{anchor}) – {n}")
    for cat in cats:
        items = [c for c in chans if c["category"] == cat]
        L += ["", f"## {cat}", "", f"{ICON.get(cat, '📺')} {len(items)} ערוצים", "",
              "| # | | ערוץ | מדינה | שפה | איכות | איפה מתנגן |", "|---|---|---|---|---|---|---|"]
        for c in sorted(items, key=lambda c: c["number"]):
            cc = c.get("country") or ("IL" if cat in ISRAELI else "")
            country = (flag(cc) + " " + (c.get("countryHe") or ("ישראל" if cc == "IL" else ""))).strip()
            L.append(f"| {c['number']} | {logo(c)} | **{md(c['name'])}** | {md(country)} | "
                     f"{md(names.get(c.get('lang'), c.get('lang') or ''))} | {md(c.get('badge'))} | {plays(c)} |")
    L += ["", "---", "", "השידורים שייכים לבעליהם; הרשימה נאספה ממאגרים ציבוריים ונבדקה אוטומטית שכל ערוץ משדר. "
          "ערוץ שהפסיק לשדר יוסר בבדיקה הבאה.", ""]
    with open(os.path.join(ROOT, "CHANNELS.md"), "w", encoding="utf-8", newline="\n") as f:
        f.write("\n".join(L))
    print(f"CHANNELS.md: {len(chans)} items in {len(cats)} categories, {len(countries)} countries")


if __name__ == "__main__":
    main()

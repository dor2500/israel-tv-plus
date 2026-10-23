"""Writes SOURCES.md: where every channel / station / app tile comes from (generated from channels.json).

usage: python -I tools/make_sources.py [iptv_db_dir] [radio_browser_json]
The optional iptv-org database (streams.json) and radio-browser.info dump only fill the "found in" column.
"""
import json
import os
import sys
import urllib.parse

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
WORK = os.path.join(ROOT, "..", "israel-tv-plus-work")

# host -> (who serves it, kind). kind: official = the broadcaster or its own CDN / distributor,
# fast = the channel's own free (ad-supported) stream on a FAST platform, restream = a third party re-broadcasting it
HOSTS = {
    "stream.mcquack.net": ("mcquack.net – אתר ממסר (restream) של צד שלישי", "restream"),
    "88.212.15.17": ("שרת של Antik (סלובקיה), נתיבי test_", "restream"),
    "dash3.antik.sk": ("Antik Telecom (סלובקיה), נתיבי test", "restream"),
    "176.118.197.101": ("שרת IP ללא שם (1HD)", "restream"),
    "stream8.cinerama.uz": ("Cinerama.uz – ספק IPTV באוזבקיסטן", "restream"),
    "cdn15.live-tv.cloud": ("live-tv.cloud – ספק IPTV אוקראיני", "restream"),
    "kancdn.medonecdn.net": ("כאן – תאגיד השידור (CDN רשמי)", "official"),
    "r.il.cdn-redge.media": ("ערוץ 14 – CDN רשמי (Redge)", "official"),
    "d18b0e6mopany4.cloudfront.net": ("רשת 13 – AWS CloudFront", "official"),
    "d2dffl3588mvfk.cloudfront.net": ("ערוץ FAST ישראלי – AWS CloudFront", "fast"),
    "d15ds134q59udk.cloudfront.net": ("ערוץ FAST ישראלי – AWS CloudFront", "fast"),
    "d1yd8hohnldm33.cloudfront.net": ("ערוץ FAST ישראלי – AWS CloudFront", "fast"),
    "contact.gostreaming.tv": ("GoStreaming – ספק השידור של ערוץ 9", "official"),
    "i24newshebrew-cdn.encoders.immergo.tv": ("i24NEWS – שרת רשמי", "official"),
    "i24newsfrench-cdn.encoders.immergo.tv": ("i24NEWS – שרת רשמי", "official"),
    "i24newsarabic-cdn.encoders.immergo.tv": ("i24NEWS – שרת רשמי", "official"),
    "i24newsenglish-cdn.encoders.immergo.tv": ("i24NEWS – שרת רשמי", "official"),
    "ch16israel-cdn.encoders.immergo.tv": ("ערוץ 16 – שרת רשמי", "official"),
    "playerservices.streamtheworld.com": ("Triton StreamTheWorld – מפיץ הרדיו של כאן", "official"),
    "glzwizzlv.bynetcdn.com": ("גלי צה\"ל / גלגלצ – CDN רשמי (Bynet)", "official"),
    "cdn.cybercdn.live": ("CyberCDN – ה-CDN של תחנות הרדיו עצמן", "official"),
    "cdna.streamgates.net": ("StreamGates – ה-CDN של התחנה", "official"),
    "gb25.streamgates.net": ("StreamGates – ערוצי רדיוס 100FM", "official"),
    "radio.streamgates.net": ("StreamGates – ה-CDN של התחנה", "official"),
    "eco-live.mediacast.co.il": ("eco99fm – Mediacast (רשמי)", "official"),
    "kzradio.mediacast.co.il": ("רדיו הקצה – Mediacast (רשמי)", "official"),
    "102.livecdn.biz": ("102FM – LiveCDN (רשמי)", "official"),
    "1075.livecdn.biz": ("רדיו חיפה – LiveCDN (רשמי)", "official"),
    "media2.93fm.co.il": ("קול חי – שרת התחנה", "official"),
    "live.ecast.co.il": ("eCast – שרת התחנה", "official"),
    "cast.breslevforyou.co.il": ("רדיו ברסלב – שרת התחנה", "official"),
    "cast.radionahariya.com": ("רדיו נהריה – שרת התחנה", "official"),
    "stream.panet.com": ("פאנט – הלא TV (רשמי)", "official"),
    "edge3.uk.kab.tv": ("קבלה לעם – שרת רשמי", "official"),
    "1247634592.rsc.cdn77.org": ("שלנו TV – CDN77", "official"),
    "dwamdstream102.akamaized.net": ("DW – Akamai (רשמי)", "official"),
    "live-hls-apps-aje-fa.getaj.net": ("אל-ג'זירה – שרת רשמי", "official"),
    "ncdn-live-bfm.pfd.sfr.net": ("BFM – SFR (רשמי)", "official"),
    "rbmn-live.akamaized.net": ("Red Bull TV – Akamai (רשמי)", "official"),
    "svs.itworkscdn.net": ("Asharq Discovery – ItWorks (רשמי)", "official"),
    "stream.4fun.tv": ("4FUN TV – שרת רשמי", "official"),
    "585b674743bbb.streamlock.net": ("70-80 TV – Wowza Streamlock", "official"),
    "cdn.fast.jwp.services": ("BabyFirst – JW Player FAST", "fast"),
    "newidco-babysharktv-1-us.roku.wurl.tv": ("Baby Shark TV – Wurl (Roku FAST)", "fast"),
    "dltiqboxjw21d.cloudfront.net": ("LEGO Channel – AWS CloudFront FAST", "fast"),
    "jmp2.uk": ("Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk", "fast"),
    "tv.mediacdn.ru": ("ערוץ השמש (Солнце) – CDN", "official"),
    "stream.smotrim.ru": ("Smotrim (VGTRK) – הבעלים של קרוסלה", "official"),
    "amg00793-amg00793c6-xumo-us-2669.playouts.now.amagi.tv": ("BBC Earth – Amagi / Xumo FAST", "fast"),
}
KIND_HE = {"official": "✅ רשמי", "fast": "✅ FAST חינמי", "restream": "⚠️ ממסר צד שלישי"}


def host(url):
    return urllib.parse.urlparse(url).hostname or ""


def who(url):
    h = host(url)
    return HOSTS.get(h, (h, "official" if h.endswith(".co.il") else "restream"))


def md(s):
    return str(s).replace("|", "\\|").replace("\u2066", "").replace("\u2069", "")


def main():
    db = sys.argv[1] if len(sys.argv) > 1 else os.path.join(WORK, "iptv", "db")
    rb = sys.argv[2] if len(sys.argv) > 2 else os.path.join(WORK, "dl2", "rb_il.json")
    with open(os.path.join(ROOT, "channels.json"), encoding="utf-8") as f:
        chans = json.load(f)
    iptv_urls, rb_urls = set(), set()
    if os.path.isfile(os.path.join(db, "streams.json")):
        with open(os.path.join(db, "streams.json"), encoding="utf-8") as f:
            iptv_urls = {s["url"].split("://", 1)[-1] for s in json.load(f)}
    if os.path.isfile(rb):
        with open(rb, encoding="utf-8") as f:
            for s in json.load(f):
                for k in ("url", "url_resolved"):
                    rb_urls.add(s[k].split("://", 1)[-1].split("?")[0])

    def found(c):
        u = c["url"].split("://", 1)[-1]
        if c.get("kind") == "app":
            return "Google Play + האתר הרשמי"
        if "streamtheworld" in u:
            return "radio-browser.info + בדיקת שמות הזרמים של כאן"
        if u in iptv_urls or c.get("iptv"):
            return "iptv-org" + (f" (`{c['iptv']}`)" if c.get("iptv") else "")
        if u.split("?")[0] in rb_urls:
            return "radio-browser.info"
        return "רשימה ידנית (גרסה קודמת)"

    def web(c):
        if c.get("kind") == "app":
            return "פותח אתר"
        if c.get("webOk") is False:
            return "❌ רק באפליקציה (הפניה בלי CORS)"
        if c.get("webUrl"):
            return f"✅ דרך מקור חלופי: {md(who(c['webUrl'])[0])}"
        if c["url"].startswith("http:"):
            return "❌ רק באפליקציה (HTTP)"
        return "✅"

    tv = [c for c in chans if c.get("kind") not in ("app", "radio")]
    radio = [c for c in chans if c.get("kind") == "radio"]
    apps = [c for c in chans if c.get("kind") == "app"]
    kinds = {}
    for c in tv + radio:
        k = who(c["url"])[1]
        kinds[k] = kinds.get(k, 0) + 1
    hosts = {}
    for c in tv + radio:
        hosts.setdefault(host(c["url"]), []).append(c)

    L = []
    L += ["# מקורות – מאיפה מגיע כל ערוץ", "",
          f"נוצר אוטומטית מ-`channels.json` על ידי `tools/make_sources.py`. סה\"כ **{len(chans)}** פריטים: "
          f"{len(tv)} ערוצי טלוויזיה, {len(radio)} תחנות רדיו, {len(apps)} אפליקציות/שירותים.", "",
          "## איך הערוצים נאספו ונבדקו", "",
          "| מקור מידע | מה נלקח ממנו |", "|---|---|",
          "| [iptv-org](https://github.com/iptv-org/iptv) ([API](https://iptv-org.github.io/api/)) | מאגר קהילתי פתוח של כתובות שידור ציבוריות. ממנו רוב ערוצי הטלוויזיה, הלוגואים, השפה ומזהה הערוץ (`iptv`) |",
          "| [radio-browser.info](https://www.radio-browser.info/) | מאגר פתוח של תחנות רדיו. ממנו תחנות הרדיו הישראליות (`/stations/bycountrycodeexact/IL`) |",
          "| [Free-TV/IPTV](https://github.com/Free-TV/IPTV) | רשימת ערוצים חינמיים רשמיים – שימשה להצלבה של ערוצי ישראל |",
          "| CDN של כאן + StreamTheWorld | בדקתי ידנית את שמות הזרמים של תחנות כאן (`KAN_BET`, `KAN_GIMMEL` וכו'). רשימות ה-HLS של כאן לרדיו חוזרות ריקות, לכן הרדיו מגיע מ-StreamTheWorld – המפיץ הרשמי שלהם |",
          "| Google Play | מזהי האפליקציות הרשמיות (פרטנר, סלקום, STING, yes, HOT, כאן, 12, 13, 14, ספורט 5) |",
          "| גרסאות קודמות של הפרויקט | ערוצים שנוספו ידנית לפני v2 (מסומנים \"רשימה ידנית\") |", "",
          "**בדיקות:** כל שידור נבדק לפני שנכנס (פלייליסט ← תת-פלייליסט ← קטע וידאו/שמע אמיתי, `tools/add_israeli.py`); "
          "מכל ערוץ וידאו צולמה תמונה עם ffmpeg והושוותה לשם הערוץ (`tools/check_channels.py`); תחנות הרדיו ומקורות האתר נוגנו בדפדפן Chromium אמיתי.", "",
          "## ⚖️ מצב הזכויות – חשוב לדעת", "",
          f"- **{kinds.get('official', 0)} שידורים רשמיים** – מהשרתים של הגוף המשדר עצמו או של המפיץ שלו (כאן, 13, 14, i24NEWS, גלי צה\"ל, תחנות הרדיו, DW, אל-ג'זירה...).",
          f"- **{kinds.get('fast', 0)} ערוצי FAST** – ערוצים חינמיים עם פרסומות שהבעלים מפיצים בעצמם (Pluto TV, BabyFirst, LEGO, Baby Shark...).",
          f"- **{kinds.get('restream', 0)} ממסרי צד שלישי** – בעיקר `stream.mcquack.net` ({len(hosts.get('stream.mcquack.net', []))} ערוצים). "
          "אלה **אינם** השרתים של הערוצים: אתר חיצוני קולט את השידור ומפיץ אותו מחדש. בהם נמצאים גם ערוצים בתשלום "
          "(HOT, yes, viju/TV1000, ערוצי ספורט) – סביר מאוד שההפצה הזו **ללא רישיון** מבעלי הזכויות, והם עלולים להיעלם בכל רגע. "
          "ההחלטה אם להשאיר אותם היא שלך; הסקריפטים לא מוסיפים ממסרים חדשים של ערוצים בתשלום.",
          "- **שירותי המנויים** (פרטנר, סלקום, STING, yes, HOT) – אין בפרויקט שום שידור שלהם; הכרטיס רק פותח את האפליקציה/האתר הרשמי, וצפייה דורשת מנוי.", "",
          "> הסיווג רשמי / FAST / ממסר נקבע לפי שם השרת שממנו מגיע השידור (למשל CDN של הגוף המשדר), "
          "ולא אושר מול כל גוף משדר בנפרד.", "",
          "## לפי שרת", "", "| שרת | מי זה | סוג | מספר |", "|---|---|---|---|"]
    for h, cs in sorted(hosts.items(), key=lambda x: -len(x[1])):
        name, k = who(cs[0]["url"])
        L.append(f"| `{h}` | {md(name)} | {KIND_HE[k]} | {len(cs)} |")

    def table(title, items):
        out = ["", f"## {title}", "", "| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |", "|---|---|---|---|---|---|"]
        for c in items:
            name, k = who(c["url"])
            out.append(f"| {c['number']} | {md(c['name'])} | {md(name)} | {KIND_HE[k]} | {found(c)} | {web(c)} |")
        return out

    cats = []
    for c in tv:
        if c["category"] not in cats:
            cats.append(c["category"])
    for cat in cats:
        L += table(f"📺 {cat}", [c for c in tv if c["category"] == cat])
    L += table("📻 רדיו", radio)
    L += ["", "## 📲 מנויים ואפליקציות", "", "| # | שירות | אפליקציה בטלפון | אפליקציה ב-Android TV | אתר |", "|---|---|---|---|---|"]
    for c in apps:
        L.append(f"| {c['number']} | {md(c['name'])} | `{c['android']}` | `{c['androidTv']}` | {c['site']} |")
    L += ["", "## רכיבים וספריות", "",
          "| רכיב | מקור | רישיון |", "|---|---|---|",
          "| hls.js (`assets/hls.min.js`) | [video-dev/hls.js](https://github.com/video-dev/hls.js) | Apache-2.0 |",
          "| גופן Heebo | Google Fonts | OFL |",
          "| Microsoft WebView2 (אפליקציית Windows) | חבילת NuGet `Microsoft.Web.WebView2` | רישיון Microsoft (מותר להפצה) |",
          "| Whisper בדפדפן (דיבוב מקומי) | transformers.js דרך jsDelivr | Apache-2.0 / MIT |",
          "| תרגום וקול לדיבוב | Google Gemini / OpenAI (מפתח של המשתמש), Google Translate | לפי תנאי השירות |",
          "| לוגואים של ערוצים | iptv-org / ויקימדיה; לוגואי הרדיו והאפליקציות נוצרו ב-`tools/add_israeli.py` | הלוגואים הם סימני מסחר של בעליהם |", ""]
    with open(os.path.join(ROOT, "SOURCES.md"), "w", encoding="utf-8") as f:
        f.write("\n".join(L))
    print("SOURCES.md:", len(chans), "items,", kinds)


if __name__ == "__main__":
    main()

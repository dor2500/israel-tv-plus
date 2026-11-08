import json
import os

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
ch_json_path = os.path.join(ROOT, "channels.json")

with open(ch_json_path, "r", encoding="utf-8") as f:
    channels = json.load(f)

# 1. Fix kan11 and kan_hinuchit
for c in channels:
    if c.get("id") == "kan11":
        c["url"] = "https://r.il.cdn-redge.media/livehls/oil/kancdn-live/live/kan11/live.livx/playlist.m3u8"
        c["backups"] = [
            "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/kan11/live.livx/playlist.m3u8",
            "https://kancdn.medonecdn.net/livehls/oil/kancdn-live/live/kan11/live.livx/playlist.m3u8",
            "http://stream.mcquack.net/20/index.m3u8",
            "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/kan11_subs/live.livx/playlist.m3u8"
        ]
        c["fallbackUrls"] = list(c["backups"])
        c["src"] = {
            "by": "כאן – תאגיד השידור (Redge Media CDN רשמי מהיר)",
            "kind": "official",
            "host": "r.il.cdn-redge.media",
            "found": "שידור רשמי ויציב ללא תקיעות",
            "web": "✅"
        }
    elif c.get("id") == "kan_hinuchit":
        c["url"] = "https://r.il.cdn-redge.media/livehls/oil/kancdn-live/live/kan_edu/live.livx/playlist.m3u8"
        c["backups"] = [
            "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/edu23/live.livx/playlist.m3u8",
            "https://kancdn.medonecdn.net/livehls/oil/kancdn-live/live/kan_edu/live.livx/playlist.m3u8",
            "http://stream.mcquack.net/378/index.m3u8"
        ]
        c["fallbackUrls"] = list(c["backups"])
        c["src"] = {
            "by": "כאן – תאגיד השידור (Redge Media CDN רשמי מהיר)",
            "kind": "official",
            "host": "r.il.cdn-redge.media",
            "found": "שידור רשמי ויציב ללא תקיעות",
            "web": "✅"
        }

# 2. Add new verified channels
new_channels = [
    {
        "id": "ch9_contact_il",
        "name": "ערוץ 9 ישראל – שידור אולפן ישיר HD",
        "title": "ערוץ 9 אולפן ישיר HD",
        "category": "ערוצי ישראל - שידור חי",
        "categoryEn": "israel",
        "url": "https://contact.gostreaming.tv/Con-11/index.m3u8",
        "logo": "assets/logos/ch9_contact_il.png",
        "badge": "HD",
        "desc": "שידור ישיר מתוך אולפני ערוץ 9 בישראל – חדשות, ראיונות ודיונים באיכות HD",
        "lang": "ru",
        "country": "IL",
        "countryHe": "ישראל",
        "number": 532,
        "fallbackUrls": [],
        "backups": [],
        "src": {
            "by": "contact.gostreaming.tv",
            "kind": "official",
            "host": "gostreaming.tv",
            "found": "שידור אולפן ערוץ 9 ישיר",
            "web": "✅"
        }
    },
    {
        "id": "radio_best_1064",
        "name": "הרדיו הטוב 106.4FM (The Best Radio)",
        "title": "הרדיו הטוב 106.4FM",
        "category": "רדיו ישראלי (Israeli Radio)",
        "categoryEn": "radio",
        "url": "https://cdna.streamgates.net/TheBest/live/playlist.m3u8",
        "logo": "assets/logos/radio_best_1064.png",
        "badge": "LIVE",
        "desc": "תחנת הרדיו הטוב 106.4FM – שידורי מוזיקה ואקטואליה 24/7",
        "lang": "ru",
        "country": "IL",
        "countryHe": "ישראל",
        "number": 533,
        "fallbackUrls": [],
        "backups": [],
        "src": {
            "by": "cdna.streamgates.net",
            "kind": "official",
            "host": "streamgates.net",
            "found": "שידור רשמי הרדיו הטוב",
            "web": "✅"
        }
    },
    {
        "id": "pervoe_radio",
        "name": "הרדיו הראשון 89.1FM (Первое радио)",
        "title": "הרדיו הראשון 89.1FM",
        "category": "רדיו ישראלי (Israeli Radio)",
        "categoryEn": "radio",
        "url": "https://cdn.cybercdn.live/Pervoia/Audio/icecast.audio",
        "logo": "assets/logos/pervoe_radio.png",
        "badge": "LIVE",
        "desc": "הרדיו הראשון 89.1FM בישראל – מוזיקה, חדשות, תרבות ובידור",
        "lang": "ru",
        "country": "IL",
        "countryHe": "ישראל",
        "number": 534,
        "fallbackUrls": [],
        "backups": [],
        "src": {
            "by": "cdn.cybercdn.live",
            "kind": "official",
            "host": "cybercdn.live",
            "found": "שידור רשמי הרדיו הראשון",
            "web": "✅"
        }
    },
    {
        "id": "radios_russian_891",
        "name": "רדיוס 100% רוסית 89.1 (Radios Russian)",
        "title": "רדיוס 100% רוסית",
        "category": "רדיו ישראלי (Israeli Radio)",
        "categoryEn": "radio",
        "url": "https://gb25.streamgates.net/radios-audio/891Digital/playlist.m3u8",
        "logo": "assets/logos/radios_russian_891.png",
        "badge": "LIVE",
        "desc": "ערוץ המוזיקה של רדיוס 100FM – מיטב הלהיטים ברוסית ללא הפסקה",
        "lang": "ru",
        "country": "IL",
        "countryHe": "ישראל",
        "number": 535,
        "fallbackUrls": [],
        "backups": [],
        "src": {
            "by": "gb25.streamgates.net",
            "kind": "official",
            "host": "streamgates.net",
            "found": "שידור רדיוס 100FM רשמי",
            "web": "✅"
        }
    }
]

existing_ids = {c["id"] for c in new_channels}
channels = [c for c in channels if c.get("id") not in existing_ids]
channels.extend(new_channels)
channels = sorted(channels, key=lambda c: c.get("number", 9999))

print(f"Total channels: {len(channels)}")

with open(ch_json_path, "w", encoding="utf-8", newline="\n") as f:
    json.dump(channels, f, ensure_ascii=False, indent=2)

dist_json_path = os.path.join(ROOT, "dist", "channels.json")
if os.path.exists(os.path.dirname(dist_json_path)):
    with open(dist_json_path, "w", encoding="utf-8", newline="\n") as f:
        json.dump(channels, f, ensure_ascii=False, indent=2)

ha_json_path = os.path.join(ROOT, "custom_components", "israel_tv_plus", "channels.json")
with open(ha_json_path, "w", encoding="utf-8", newline="\n") as f:
    json.dump(channels, f, ensure_ascii=False, indent=2)

ch_js_path = os.path.join(ROOT, "channels.js")
with open(ch_js_path, "w", encoding="utf-8", newline="\n") as f:
    f.write("window.CHANNELS_DATA = window.ITV_CHANNELS = " + json.dumps(channels, ensure_ascii=False, indent=1) + ";\n")

print("Saved channels.json, channels.js and custom_components channels.json successfully!")


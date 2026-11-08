import json
import os

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
ch_json_path = os.path.join(ROOT, "channels.json")

with open(ch_json_path, "r", encoding="utf-8") as f:
    channels = json.load(f)

new_channels = [
    {
        "id": "kotel_live_il",
        "name": "הכותל המערבי – שידור חי 24/7 HD",
        "title": "הכותל המערבי HD",
        "category": "מסורת וקהילה",
        "categoryEn": "heritage",
        "url": "https://cdn1.cast-tv.com/23595/Live_Kotel3_ABR/playlist.m3u8",
        "logo": "assets/logos/kotel_live_il.png",
        "badge": "1080p",
        "desc": "מצלמות הכותל המערבי בירושלים – שידור ישיר באיכות Full HD 24/7 מסביב לשעון",
        "lang": "he",
        "country": "IL",
        "countryHe": "ישראל",
        "number": 90,
        "fallbackUrls": [
            "https://cdn1.cast-tv.com/23595/Live_Kotel1_ABR/playlist.m3u8",
            "https://cdn1.cast-tv.com/23595/Live_Kotel2_ABR/playlist.m3u8"
        ],
        "backups": [
            "https://cdn1.cast-tv.com/23595/Live_Kotel1_ABR/playlist.m3u8",
            "https://cdn1.cast-tv.com/23595/Live_Kotel2_ABR/playlist.m3u8"
        ],
        "src": {
            "by": "cdn1.cast-tv.com",
            "kind": "official",
            "host": "cast-tv.com",
            "found": "שידור רשמי הקרן למורשת הכותל",
            "web": "✅"
        }
    },
    {
        "id": "freetv_lifestyle_il",
        "name": "FreeTV לייף סטייל HD",
        "title": "FreeTV לייף סטייל HD",
        "category": "ערוצי ישראל - שידור חי",
        "categoryEn": "israel",
        "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/imagin_lifestyle/live.livx/playlist.m3u8",
        "logo": "assets/logos/freetv_lifestyle_il.png",
        "badge": "HD",
        "desc": "ערוץ הלייף סטייל של FreeTV – עיצוב, בית, בריאות, אופנה וסגנון חיים ישראלי",
        "lang": "he",
        "country": "IL",
        "countryHe": "ישראל",
        "number": 529,
        "fallbackUrls": [],
        "backups": [],
        "src": {
            "by": "d1zqtf09wb8nt5.cloudfront.net",
            "kind": "official",
            "host": "cloudfront.net",
            "found": "שידור FreeTV רשמי",
            "web": "✅"
        }
    },
    {
        "id": "music_channel_il",
        "name": "ערוץ המוזיקה הישראלי 24/7 HD",
        "title": "ערוץ המוזיקה הישראלי HD",
        "category": "מוזיקה (Music TV)",
        "categoryEn": "music",
        "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/music_channel/live.livx/playlist.m3u8",
        "logo": "assets/logos/music_channel_il.png",
        "badge": "HD",
        "desc": "ערוץ המוזיקה הישראלי – מיטב הקליפים והלהיטים הישראליים ברצף 24 שעות ביממה באיכות HD",
        "lang": "he",
        "country": "IL",
        "countryHe": "ישראל",
        "number": 530,
        "fallbackUrls": [],
        "backups": [],
        "src": {
            "by": "d1zqtf09wb8nt5.cloudfront.net",
            "kind": "official",
            "host": "cloudfront.net",
            "found": "שידור FreeTV רשמי",
            "web": "✅"
        }
    },
    {
        "id": "partner_reality_il",
        "name": "ערוץ הריאליטי הישראלי HD",
        "title": "ערוץ הריאליטי HD",
        "category": "סרטים וסדרות (Movies & Series)",
        "categoryEn": "series",
        "url": "https://d2dffl3588mvfk.cloudfront.net/out/v1/d8e15050ca4148aab0ee387a5e2eb46b/index_1.m3u8",
        "logo": "assets/logos/partner_reality_il.png",
        "badge": "HD",
        "desc": "ערוץ הריאליטי הישראלי – כל תוכניות הריאליטי המובילות, התחרויות והדרמות ברצף שידורים HD",
        "lang": "he",
        "country": "IL",
        "countryHe": "ישראל",
        "number": 531,
        "fallbackUrls": [],
        "backups": [],
        "src": {
            "by": "d2dffl3588mvfk.cloudfront.net",
            "kind": "official",
            "host": "cloudfront.net",
            "found": "שידור פרטנר TV רשמי",
            "web": "✅"
        }
    }
]

# Remove any existing channels with same IDs to avoid collisions
existing_ids = {c["id"] for c in new_channels}
channels = [c for c in channels if c.get("id") not in existing_ids]

# Append new channels
channels.extend(new_channels)

# Sort by number
channels = sorted(channels, key=lambda c: c.get("number", 9999))

print(f"Total channels after addition: {len(channels)}")

# Save to channels.json
with open(ch_json_path, "w", encoding="utf-8", newline="\n") as f:
    json.dump(channels, f, ensure_ascii=False, indent=2)

# Save to dist/channels.json
dist_json_path = os.path.join(ROOT, "dist", "channels.json")
if os.path.exists(os.path.dirname(dist_json_path)):
    with open(dist_json_path, "w", encoding="utf-8", newline="\n") as f:
        json.dump(channels, f, ensure_ascii=False, indent=2)

# Save to custom_components/israel_tv_plus/channels.json
ha_json_path = os.path.join(ROOT, "custom_components", "israel_tv_plus", "channels.json")
with open(ha_json_path, "w", encoding="utf-8", newline="\n") as f:
    json.dump(channels, f, ensure_ascii=False, indent=2)

# Save to channels.js
ch_js_path = os.path.join(ROOT, "channels.js")
with open(ch_js_path, "w", encoding="utf-8", newline="\n") as f:
    f.write("window.CHANNELS_DATA = window.ITV_CHANNELS = " + json.dumps(channels, ensure_ascii=False, indent=1) + ";\n")

# Save to www/channels.js if exists
www_ch_js_path = os.path.join(ROOT, "www", "channels.js")
if os.path.exists(os.path.dirname(www_ch_js_path)):
    with open(www_ch_js_path, "w", encoding="utf-8", newline="\n") as f:
        f.write("window.CHANNELS_DATA = window.ITV_CHANNELS = " + json.dumps(channels, ensure_ascii=False, indent=1) + ";\n")

print("All channel JSON and JS files successfully written!")


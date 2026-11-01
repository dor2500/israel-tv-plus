import json, os

ROOT = r"C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo"
ch_json_path = os.path.join(ROOT, "channels.json")
ch_js_path = os.path.join(ROOT, "channels.js")

with open(ch_json_path, "r", encoding="utf-8") as f:
    channels = json.load(f)

# 1. Fix nickelodeon_il
nick_found = False
for ch in channels:
    if ch.get("id") == "nickelodeon_il" or "nickelodeon" in ch.get("name", "").lower():
        ch["url"] = "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/32148/index.m3u8"
        ch["name"] = "ערוץ ניקלודיאון ישראל HD (TeenNick / Nickelodeon)"
        ch["language"] = "he"
        ch["servers"] = [
            {"name": "ראשי (עברית)", "url": "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/32148/index.m3u8"},
            {"name": "גיבוי 1 (TeenNick+ עברית)", "url": "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/2334/index.m3u8"},
            {"name": "גיבוי 2 (Nick Jr עברית)", "url": "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/2337/index.m3u8"},
            {"name": "גיבוי 3 (Nick Jr+ עברית)", "url": "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/12240/index.m3u8"}
        ]
        nick_found = True
        print(f"Updated nickelodeon_il (#{ch.get('number')}) to Hebrew feed!")

# 2. Add extra requested channels if not already present
existing_ids = {ch.get("id") for ch in channels}
existing_urls = {ch.get("url") for ch in channels}

extra_channels = [
    {
        "id": "hot_doco_hd",
        "name": "HOT דוקו HD (HOT Doco)",
        "number": 402,
        "category": "israel",
        "url": "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/32129/index.m3u8",
        "logo": "https://upload.wikimedia.org/wikipedia/he/thumb/3/36/HOT_Doco_Logo_2022.svg/300px-HOT_Doco_Logo_2022.svg.png",
        "language": "he",
        "quality": "HD",
        "description": "ערוץ הסרטים והסדרות התיעודיים המוביל בישראל מבית HOT."
    },
    {
        "id": "channel_hayeladim_hd",
        "name": "ערוץ הילדים HD",
        "number": 403,
        "category": "kids",
        "url": "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/2347/index.m3u8",
        "logo": "https://upload.wikimedia.org/wikipedia/he/thumb/4/4e/Arutz_HaYeladim_Logo_2023.svg/300px-Arutz_HaYeladim_Logo_2023.svg.png",
        "language": "he",
        "quality": "HD",
        "description": "ערוץ הילדים – שידורי ילדים ונוער, סדרות מקור ותוכניות אהובות בעברית."
    },
    {
        "id": "junior_channel_hd",
        "name": "ערוץ ג'וניור HD (Junior)",
        "number": 404,
        "category": "kids",
        "url": "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/2366/index.m3u8",
        "logo": "https://upload.wikimedia.org/wikipedia/he/thumb/a/a2/Junior_Channel_Logo.svg/300px-Junior_Channel_Logo.svg.png",
        "language": "he",
        "quality": "HD",
        "description": "ערוץ ג'וניור לילדים בעברית – סדרות אנימציה ונוסטלגיה אהובות."
    },
    {
        "id": "baby_channel_hd",
        "name": "ערוץ בייבי HD (Baby Channel)",
        "number": 405,
        "category": "kids",
        "url": "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/2370/index.m3u8",
        "logo": "https://upload.wikimedia.org/wikipedia/he/thumb/5/52/BabyTV_Logo_2020.svg/300px-BabyTV_Logo_2020.svg.png",
        "language": "he",
        "quality": "HD",
        "description": "ערוץ בייבי המיועד לפעוטות והורים – תוכניות מעשירות ומפתחות בעברית."
    },
    {
        "id": "luli_channel_hd",
        "name": "ערוץ לולי לפעוטות HD (Luli)",
        "number": 406,
        "category": "kids",
        "url": "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/12246/index.m3u8",
        "logo": "https://upload.wikimedia.org/wikipedia/he/thumb/e/e6/Luli_Logo_2021.svg/300px-Luli_Logo_2021.svg.png",
        "language": "he",
        "quality": "HD",
        "description": "ערוץ לולי לפעוטות – שירים, סיפורים והפעלות בעברית מבית הופ!."
    },
    {
        "id": "yes_movies_kids_hd",
        "name": "yes קולנוע ילדים HD (yes Movies Kids)",
        "number": 407,
        "category": "kids",
        "url": "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/12273/index.m3u8",
        "logo": "https://upload.wikimedia.org/wikipedia/he/thumb/3/36/Yes_Movies_Kids_Logo.png/300px-Yes_Movies_Kids_Logo.png",
        "language": "he",
        "quality": "HD",
        "description": "סרטי ילדים ומשפחה מובילים בדיבוב עברי ובאיכות HD מבית yes."
    },
    {
        "id": "vacation_channel_hd",
        "name": "ערוץ החופש והנופש HD (Vacation Channel)",
        "number": 408,
        "category": "entertainment",
        "url": "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/32160/index.m3u8",
        "logo": "https://static.g-cdn.app/streams/vacation-channel-il-hd.png",
        "language": "he",
        "quality": "HD",
        "description": "ערוץ תיירות, נופש, חופשות וטיולים בישראל ובעולם."
    }
]

for ch in extra_channels:
    if ch["id"] not in existing_ids and ch["url"] not in existing_urls:
        channels.append(ch)
        print(f"Added #{ch['number']} [{ch['id']}] {ch['name']}")

# Save channels.json
with open(ch_json_path, "w", encoding="utf-8") as f:
    json.dump(channels, f, ensure_ascii=False, indent=2)
    f.write("\n")

# Preserve VOD_DATA in channels.js
vod_data_str = ""
if os.path.exists(ch_js_path):
    with open(ch_js_path, "r", encoding="utf-8") as f:
        old_js = f.read()
    if "window.VOD_DATA" in old_js:
        vod_data_str = old_js[old_js.find("window.VOD_DATA"):]

with open(ch_js_path, "w", encoding="utf-8") as f:
    f.write("// generated by tools\nwindow.CHANNELS_DATA = " + json.dumps(channels, ensure_ascii=False) + ";\n")
    if vod_data_str:
        f.write(vod_data_str)

print(f"Updated channels.json and channels.js with {len(channels)} total channels!")


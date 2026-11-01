import json, os

ROOT = r"C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo"
ch_json_path = os.path.join(ROOT, "channels.json")
ch_js_path = os.path.join(ROOT, "channels.js")

with open(ch_json_path, "r", encoding="utf-8") as f:
    channels = json.load(f)

# Update existing food_network_il (#173) to explicitly feature "ערוץ האוכל מבית החיים הטובים"
for ch in channels:
    if ch.get("id") == "food_network_il":
        ch["name"] = "ערוץ האוכל HD (מבית החיים הטובים)"
        ch["title"] = "ערוץ האוכל הישראלי מבית החיים הטובים וענני תקשורת HD"
        ch["desc"] = "ערוץ האוכל הישראלי מבית החיים הטובים וענני תקשורת – שפים ישראליים, מתכונים וקולינריה בעברית"
        ch["description"] = "ערוץ האוכל הישראלי מבית החיים הטובים וענני תקשורת – שפים ישראליים, מתכונים וקולינריה בעברית"
        ch["category"] = "israel"
        ch["categoryEn"] = "food"
        ch["url"] = "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/12209/index.m3u8"
        ch["logo"] = "assets/logos/food_channel.png"
        ch["servers"] = [
            {"name": "ראשי (ערוץ האוכל הישראלי FHD)", "url": "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/12209/index.m3u8"},
            {"name": "גיבוי 1 (ערוץ האוכל פלוס)", "url": "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/2377/index.m3u8"},
            {"name": "גיבוי 2 (ערוץ פודי Foody בישול ישראלי)", "url": "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/32154/index.m3u8"}
        ]
        ch["backups"] = [
            "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/2377/index.m3u8",
            "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/32154/index.m3u8"
        ]
        print("Updated channel #173 (food_network_il) with full Good Life / Food Channel branding!")

# Add ערוץ החיים הטובים HD (Good Life Channel) if not exists
existing_ids = {ch.get("id") for ch in channels}
existing_urls = {ch.get("url") for ch in channels}

good_life_channel = {
    "id": "good_life_channel_hd",
    "name": "ערוץ החיים הטובים HD (Good Life)",
    "title": "ערוץ החיים הטובים והלייפסטייל מבית ענני תקשורת HD",
    "number": 409,
    "category": "israel",
    "categoryEn": "lifestyle",
    "url": "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/2355/index.m3u8",
    "logo": "assets/logos/good_life_israel.png",
    "quality": "HD",
    "lang": "he",
    "language": "he",
    "description": "ערוץ החיים הטובים – תיירות, קולינריה, עיצוב הבית, לייפסטייל ואירוח ישראלי ועולמי מבית ענני תקשורת.",
    "desc": "ערוץ החיים הטובים – תיירות, קולינריה, עיצוב הבית, לייפסטייל ואירוח ישראלי ועולמי מבית ענני תקשורת.",
    "servers": [
        {"name": "ראשי (ערוץ החיים הטובים HD)", "url": "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/2355/index.m3u8"},
        {"name": "גיבוי 1 (ערוץ האוכל מבית החיים הטובים)", "url": "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/12209/index.m3u8"},
        {"name": "גיבוי 2 (ערוץ בית+ Home Plus)", "url": "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/2339/index.m3u8"}
    ],
    "backups": [
        "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/12209/index.m3u8",
        "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/2339/index.m3u8"
    ]
}

if good_life_channel["id"] not in existing_ids and good_life_channel["url"] not in existing_urls:
    channels.append(good_life_channel)
    print("Added #409 [good_life_channel_hd] ערוץ החיים הטובים HD")

# Also add dedicated Israeli Food Channel #410 in case user looks at end of list
food_channel_il_dedicated = {
    "id": "food_channel_israel_hd",
    "name": "ערוץ האוכל הישראלי HD (מבית החיים הטובים)",
    "title": "ערוץ האוכל הישראלי מבית החיים הטובים וענני תקשורת HD",
    "number": 410,
    "category": "israel",
    "categoryEn": "food",
    "url": "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/12209/index.m3u8",
    "logo": "assets/logos/food_channel.png",
    "quality": "FHD",
    "lang": "he",
    "language": "he",
    "description": "ערוץ האוכל מבית החיים הטובים וענני תקשורת – שפים מובילים, מתכונים וסדנאות בישול ואפייה בעברית.",
    "desc": "ערוץ האוכל מבית החיים הטובים וענני תקשורת – שפים מובילים, מתכונים וסדנאות בישול ואפייה בעברית.",
    "servers": [
        {"name": "ראשי (ערוץ האוכל הישראלי FHD)", "url": "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/12209/index.m3u8"},
        {"name": "גיבוי 1 (ערוץ האוכל פלוס)", "url": "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/2377/index.m3u8"},
        {"name": "גיבוי 2 (ערוץ החיים הטובים HD)", "url": "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/2355/index.m3u8"},
        {"name": "גיבוי 3 (ערוץ פודי בישול ישראלי)", "url": "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/32154/index.m3u8"}
    ],
    "backups": [
        "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/2377/index.m3u8",
        "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/2355/index.m3u8",
        "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/32154/index.m3u8"
    ]
}

if food_channel_il_dedicated["id"] not in existing_ids:
    channels.append(food_channel_il_dedicated)
    print("Added #410 [food_channel_israel_hd] ערוץ האוכל הישראלי HD (מבית החיים הטובים)")

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

print(f"Successfully saved channels.json and channels.js ({len(channels)} total channels)!")

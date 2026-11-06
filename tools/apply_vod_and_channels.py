import json, os

ROOT = r"C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo"
ch_json_path = os.path.join(ROOT, "channels.json")
ch_js_path = os.path.join(ROOT, "channels.js")
vod_json_path = os.path.join(ROOT, "vod_data.json")

with open(ch_json_path, "r", encoding="utf-8") as f:
    channels = json.load(f)

with open(vod_json_path, "r", encoding="utf-8") as f:
    vod_data = json.load(f)

# 1. Normalize categories for channel 173, 409, 410, 45
for ch in channels:
    cid = ch.get("id")
    if cid == "food_network_il":
        ch["category"] = "ערוצי אוכל (Food Channels)"
        ch["categoryEn"] = "food"
    elif cid == "good_life_channel_hd":
        ch["category"] = "ערוצי ישראל - שידור חי"
        ch["categoryEn"] = "lifestyle"
    elif cid == "food_channel_israel_hd":
        ch["category"] = "ערוצי אוכל (Food Channels)"
        ch["categoryEn"] = "food"
    elif cid == "freetv_yalduti_il" or ch.get("name", "").startswith("ערוץ ילדותי"):
        ch["category"] = "ילדים ונוער (בעברית)"
        ch["categoryEn"] = "kids_he"

# 2. Add Hala TV (411) if not present
existing_ids = {ch.get("id") for ch in channels}
if "hala_tv_hd" not in existing_ids:
    hala_tv = {
        "id": "hala_tv_hd",
        "name": "ערוץ הלא TV HD (Hala TV)",
        "title": "ערוץ הלא TV הישראלי – שידורי חדשות, תרבות ואירוח HD",
        "number": 411,
        "category": "ערוצי ישראל - שידור חי",
        "categoryEn": "israel",
        "url": "https://stream.panet.com/edge/halaTV/playlist.m3u8",
        "logo": "https://upload.wikimedia.org/wikipedia/he/thumb/4/41/Hala_TV_Logo.png/300px-Hala_TV_Logo.png",
        "badge": "HD",
        "quality": "HD",
        "language": "ar,he",
        "lang": "ar,he",
        "countryHe": "ישראל",
        "desc": "ערוץ הלא TV – הערוץ הטלוויזיוני המסחרי המוביל המשדר מישראל לציבור הערבי-ישראלי.",
        "description": "ערוץ הלא TV – הערוץ הטלוויזיוני המסחרי המוביל המשדר מישראל לציבור הערבי-ישראלי.",
        "servers": [
            {"name": "ראשי (Hala TV HD)", "url": "https://stream.panet.com/edge/halaTV/playlist.m3u8"}
        ]
    }
    channels.append(hala_tv)
    print("Added channel 411: ערוץ הלא TV HD")

# 3. Add VOD series into channels.json under category 'סדרות לצפייה ישירה (VOD)'
vod_start_num = 501
for idx, s in enumerate(vod_data):
    sid = s["id"]
    # Check if already in channels
    existing_ch = next((c for c in channels if c.get("id") == sid), None)
    episodes = s.get("episodes", [])
    first_url = episodes[0]["url"] if episodes else ""
    other_urls = [ep["url"] for ep in episodes[1:]] if len(episodes) > 1 else []
    
    vod_ch = {
        "id": sid,
        "name": f"{s['title']} FHD (VOD)",
        "title": f"{s['title']} – עונות מלאות לצפייה ישירה 1080p",
        "number": vod_start_num + idx,
        "category": "סדרות לצפייה ישירה (VOD)",
        "categoryEn": "vod",
        "url": first_url,
        "fallbackUrls": other_urls,
        "backups": other_urls,
        "logo": s["poster"],
        "badge": "VOD",
        "quality": "1080p",
        "language": "he",
        "lang": "he",
        "countryHe": "ישראל",
        "desc": s["desc"],
        "description": s["desc"],
        "isVod": True,
        "servers": [{"name": ep["title"], "url": ep["url"]} for ep in episodes]
    }
    
    if existing_ch:
        existing_ch.update(vod_ch)
    else:
        channels.append(vod_ch)
    print(f"Added/Updated VOD channel #{vod_start_num + idx}: {vod_ch['name']}")

# Save channels.json
with open(ch_json_path, "w", encoding="utf-8") as f:
    json.dump(channels, f, ensure_ascii=False, indent=2)
print(f"channels.json updated successfully with {len(channels)} total channels!")

# 4. Update channels.js
with open(ch_js_path, "r", encoding="utf-8") as f:
    js_content = f.read()

# Replace window.CHANNELS with the updated channels array
parts = js_content.split("window.VOD_DATA = ")
base_js = parts[0].strip()

# Make sure window.CHANNELS is synced
ch_json_str = json.dumps(channels, ensure_ascii=False, indent=2)
# app.js reads CHANNELS_DATA (ITV_CHANNELS for older builds) - "window.CHANNELS" left every TV / web build empty
base_js_updated = "window.CHANNELS_DATA = window.ITV_CHANNELS = " + ch_json_str + ";"

vod_json_str = json.dumps(vod_data, ensure_ascii=False, indent=2)
new_js_content = base_js_updated + "\n\nwindow.VOD_DATA = " + vod_json_str + ";\n"

with open(ch_js_path, "w", encoding="utf-8") as f:
    f.write(new_js_content)
print(f"channels.js updated successfully with all channels and full VOD_DATA!")


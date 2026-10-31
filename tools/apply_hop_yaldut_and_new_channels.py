# tools/apply_hop_yaldut_and_new_channels.py
import json

path = r'C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo\channels.json'
with open(path, 'r', encoding='utf-8') as f:
    channels = json.load(f)

# 1. Update or create hop_yaldut_il for channel 64
existing_64 = next((c for c in channels if c.get("id") == "hop_yaldut_il"), None)
if not existing_64:
    hop_yaldut = {
        "id": "hop_yaldut_il",
        "name": "ערוץ הופ! ילדות ישראלית HD",
        "number": 64,
        "category": "ילדים ונוער (בעברית)",
        "categoryEn": "kids_he",
        "country": "IL",
        "countryHe": "ישראל",
        "lang": "he",
        "logo": "assets/channels/hop_yaldut.png",
        "url": "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/2383/index.m3u8",
        "subsUrl": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/yalduti/live.livx/playlist.m3u8",
        "src": {
            "by": "Hop! Media Group Official Stream / Ottrast",
            "kind": "official",
            "host": "ottrast.com"
        },
        "desc": "ערוץ הופ! ילדות ישראלית – שירי ילדות קלאסיים, סיפורים, הצגות וסדרות איכותיות לילדים ופעוטות בעברית.",
        "webOk": True
    }
    channels.append(hop_yaldut)
else:
    existing_64["name"] = "ערוץ הופ! ילדות ישראלית HD"
    existing_64["number"] = 64
    existing_64["url"] = "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/2383/index.m3u8"
    existing_64["subsUrl"] = "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/yalduti/live.livx/playlist.m3u8"

# 2. Add hop_plus_kids_il
if not any(c.get("id") == "hop_plus_kids_il" for c in channels):
    channels.append({
        "id": "hop_plus_kids_il",
        "name": "ערוץ הופ! פלוס HD",
        "number": 65,
        "category": "ילדים ונוער (בעברית)",
        "categoryEn": "kids_he",
        "country": "IL",
        "countryHe": "ישראל",
        "lang": "he",
        "logo": "assets/channels/hop.png",
        "url": "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/2338/index.m3u8",
        "src": {"by": "Hop! Group / Ottrast", "kind": "official", "host": "ottrast.com"},
        "desc": "ערוץ הופ! פלוס – תוכניות אהובות, לימוד והרפתקאות לקטנטנים.",
        "webOk": True
    })

# 3. Add FreeTV kids movies
if not any(c.get("id") == "freetv_kids_family_il" for c in channels):
    channels.append({
        "id": "freetv_kids_family_il",
        "name": "FreeTV סרטי ילדים ומשפחה HD",
        "number": 75,
        "category": "ילדים ונוער (בעברית)",
        "categoryEn": "kids_he",
        "country": "IL",
        "countryHe": "ישראל",
        "lang": "he",
        "logo": "assets/channels/freetv_movies.png",
        "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/free_movies_family/live.livx/playlist.m3u8",
        "src": {"by": "FreeTV CloudFront Official", "kind": "official", "host": "cloudfront.net"},
        "desc": "ערוץ סרטי ילדים, אנימציה ומשפחה 24/7 מדובב לעברית.",
        "webOk": True
    })

# 4. Add yes movies kids plus
if not any(c.get("id") == "yes_movies_kids_plus_il" for c in channels):
    channels.append({
        "id": "yes_movies_kids_plus_il",
        "name": "yes קולנוע ילדים פלוס HD",
        "number": 76,
        "category": "ילדים ונוער (בעברית)",
        "categoryEn": "kids_he",
        "country": "IL",
        "countryHe": "ישראל",
        "lang": "he",
        "logo": "assets/channels/yes_movies_kids.png",
        "url": "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/2365/index.m3u8",
        "src": {"by": "yes / Ottrast Delivery", "kind": "official", "host": "ottrast.com"},
        "desc": "ערוץ סרטי ילדים ונוער שוברי קופות מבית yes.",
        "webOk": True
    })

# 5. Add Israeli Music & Karaoke
if not any(c.get("id") == "keshet_music_il" for c in channels):
    channels.append({
        "id": "keshet_music_il",
        "name": "ערוץ מוזיקה ישראלית 24/7 HD",
        "number": 115,
        "category": "מוזיקה (Music TV)",
        "categoryEn": "music",
        "country": "IL",
        "countryHe": "ישראל",
        "lang": "he",
        "logo": "assets/channels/music_il.png",
        "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/music_il/live.livx/playlist.m3u8",
        "src": {"by": "FreeTV / Music IL", "kind": "official", "host": "cloudfront.net"},
        "desc": "להיטי מוזיקה ישראלית מכל הזמנים, קליפים והופעות חיות 24 שעות ביממה.",
        "webOk": True
    })

if not any(c.get("id") == "karaoke_il" for c in channels):
    channels.append({
        "id": "karaoke_il",
        "name": "ערוץ קריוקי ישראלי 24/7 HD",
        "number": 116,
        "category": "מוזיקה (Music TV)",
        "categoryEn": "music",
        "country": "IL",
        "countryHe": "ישראל",
        "lang": "he",
        "logo": "assets/channels/karaoke.png",
        "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/karaoke/live.livx/playlist.m3u8",
        "src": {"by": "FreeTV Karaoke", "kind": "official", "host": "cloudfront.net"},
        "desc": "ערוץ קריוקי ישראלי לשירה בציבור עם מילים על המסך.",
        "webOk": True
    })

# Re-align kids numbers 61-87
kids_order = {
    "arutz_hayeladim": 61,
    "logi": 62,
    "hop_kids_israel": 63,
    "hop_yaldut_il": 64,
    "hop_plus_kids_il": 65,
    "hop_pele_tv": 66,
    "luli_tv_il": 67,
    "disney_channel_il": 68,
    "disney_jr_il": 69,
    "nickelodeon_il": 70,
    "nick_jr_il": 71,
    "teennick_il": 72,
    "zoom_kids": 73,
    "junior_plus_kids_il": 74,
    "freetv_kids_family_il": 75,
    "yes_movies_kids_plus_il": 76,
    "yalduti_tv": 77,
    "yoyo_tv": 78,
    "a_plus_kids_il": 79,
    "stars_kids_il": 80,
    "yalduti_songs_tv": 81,
    "wiz_kids_il": 82,
    "hot_cinema_family_il": 83
}

for c in channels:
    cid = c.get("id")
    if cid in kids_order:
        c["number"] = kids_order[cid]

# Deduplicate
seen_ids = set()
unique_channels = []
for c in channels:
    if c["id"] not in seen_ids:
        seen_ids.add(c["id"])
        unique_channels.append(c)

with open(path, 'w', encoding='utf-8') as f:
    json.dump(unique_channels, f, ensure_ascii=False, indent=2)

print(f"Total channels updated: {len(unique_channels)}")
print("Hop! Yaldut Israelit is set to #64!")

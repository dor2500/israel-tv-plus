import json
import re

cyrillic = re.compile(r'[\u0400-\u04FF]')
arabic = re.compile(r'[\u0600-\u06FF]')

DEAD_PLUTO_IDS = {
    'pluto_movies_action_fhd',
    'pluto_thriller_movies_fhd',
    'pluto_comedy_movies_fhd',
    'tastemade_food_hd',
    'pluto_food_hd',
    'pluto_cuisine_hd',
    'pluto_cucina_hd',
    'pluto_kfood_hd',
    'pluto_spotlight_movies_fhd',
    'pluto_crime_drama_fhd',
    'pluto_drama_movies_fhd',
    'pluto_horror_movies_fhd',
    'mlb_network_pluto_fhd',
    'fox_sports_live_fhd',
    'pluto_romantic_movies_fhd',
    'star_trek_channel_fhd',
    'totally_turtles_fhd',
    'nick_pluto_fhd',
    'vevo_90s_live_fhd',
    'garfield_friends_fhd',
    'nick_jr_pluto_fhd',
    'csi_channel_fhd',
    'peppa_pig_247',
    'dora_explorer_247',
    'baby_shark_tv'
}

PARTNER_CHANNELS = [
    {
        "id": "partner_humor_il",
        "name": "ערוץ פרטנר TV – הומור וסטנדאפ HD",
        "title": "ערוץ פרטנר TV – הומור ובידור ישראלי",
        "desc": "שידורי קומדיה, מערכונים, מופעי סטנדאפ ישראליים והתוכניות האהובות מבית פרטנר TV וסלקום.",
        "url": "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/32316/index.m3u8",
        "backup_urls": [
            "https://d15ds134q59udk.cloudfront.net/media/3b45de9e-c944-4fdc-a344-7c3042b5b104/mainManifest.m3u8"
        ],
        "category": "ערוצי פרטנר TV (Partner TV)",
        "categoryEn": "partner",
        "lang": "he",
        "country": "IL",
        "countryHe": "ישראל",
        "badge": "FHD",
        "kind": "live",
        "logo": "assets/logos/partner.svg"
    },
    {
        "id": "partner_home_design_il",
        "name": "ערוץ פרטנר TV – בית ועיצוב HD",
        "title": "ערוץ פרטנר TV – בית ועיצוב ישראלי",
        "desc": "תכנון, שיפוץ, סטיילינג, אדריכלות ועיצוב הבית הישראלי מבית פרטנר TV.",
        "url": "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/12266/index.m3u8",
        "backup_urls": [
            "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/32124/index.m3u8"
        ],
        "category": "ערוצי פרטנר TV (Partner TV)",
        "categoryEn": "partner",
        "lang": "he",
        "country": "IL",
        "countryHe": "ישראל",
        "badge": "HD",
        "kind": "live",
        "logo": "assets/logos/partner.svg"
    },
    {
        "id": "partner_reality_il",
        "name": "ערוץ פרטנר TV – ריאליטי ישראלי HD",
        "title": "ערוץ פרטנר TV – ריאליטי ישראלי 24/7",
        "desc": "ערוץ הריאליטי הישראלי של פרטנר TV – עונות מלאות, דרמות, תחרויות והרגעים הגדולים.",
        "url": "https://d2dffl3588mvfk.cloudfront.net/out/v1/d8e15050ca4148aab0ee387a5e2eb46b/index_1.m3u8",
        "backup_urls": [
            "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/12274/index.m3u8"
        ],
        "category": "ערוצי פרטנר TV (Partner TV)",
        "categoryEn": "partner",
        "lang": "he",
        "country": "IL",
        "countryHe": "ישראל",
        "badge": "HD",
        "kind": "live",
        "logo": "assets/logos/partner.svg"
    },
    {
        "id": "partner_turkish_dramas_il",
        "name": "ערוץ פרטנר TV – הדרמות הטורקיות HD",
        "title": "ערוץ פרטנר TV – ערוץ הדרמות הטורקיות",
        "desc": "ערוץ הדרמות והטלנובלות הטורקיות של פרטנר TV עם תרגום עברי מלא ברצף 24/7.",
        "url": "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/2363/index.m3u8",
        "backup_urls": [
            "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/32320/index.m3u8"
        ],
        "category": "ערוצי פרטנר TV (Partner TV)",
        "categoryEn": "partner",
        "lang": "he",
        "country": "IL",
        "countryHe": "ישראל",
        "badge": "HD",
        "kind": "live",
        "logo": "assets/logos/partner.svg"
    },
    {
        "id": "partner_good_life_il",
        "name": "ערוץ פרטנר TV – החיים הטובים HD",
        "title": "ערוץ פרטנר TV – החיים הטובים ולייף סטייל",
        "desc": "ערוץ החיים הטובים והלייף סטייל בפרטנר TV – קולינריה, טיולים, יופי ובריאות.",
        "url": "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/2355/index.m3u8",
        "backup_urls": [
            "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/imagin_lifestyle/live.livx/playlist.m3u8"
        ],
        "category": "ערוצי פרטנר TV (Partner TV)",
        "categoryEn": "partner",
        "lang": "he",
        "country": "IL",
        "countryHe": "ישראל",
        "badge": "HD",
        "kind": "live",
        "logo": "assets/logos/partner.svg"
    },
    {
        "id": "partner_kids_il",
        "name": "ערוץ פרטנר TV – עולם הילדים HD",
        "title": "ערוץ פרטנר TV – עולם הילדים והנוער",
        "desc": "ערוץ הילדים והנוער של פרטנר TV – סדרות אהובות, אנימציה, הפעלות והפקות מקור בדיבוב עברי מלא.",
        "url": "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/2338/index.m3u8",
        "backup_urls": [
            "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/2366/index.m3u8"
        ],
        "category": "ערוצי פרטנר TV (Partner TV)",
        "categoryEn": "partner",
        "lang": "he",
        "country": "IL",
        "countryHe": "ישראל",
        "badge": "HD",
        "kind": "live",
        "logo": "assets/logos/partner.svg"
    },
    {
        "id": "partner_toddlers_luli_il",
        "name": "ערוץ פרטנר TV – לולי לגיל הרך HD",
        "title": "ערוץ פרטנר TV – לולי לפעוטות וילדים",
        "desc": "ערוץ לולי בפרטנר TV – תכנים חינוכיים, שירים והפעלות מרגיעות לפעוטות ולגיל הרך.",
        "url": "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/12246/index.m3u8",
        "backup_urls": [
            "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/baby_new/live.livx/playlist.m3u8"
        ],
        "category": "ערוצי פרטנר TV (Partner TV)",
        "categoryEn": "partner",
        "lang": "he",
        "country": "IL",
        "countryHe": "ישראל",
        "badge": "HD",
        "kind": "live",
        "logo": "assets/logos/partner.svg"
    },
    {
        "id": "partner_gameshow_il",
        "name": "ערוץ פרטנר TV – השעשועונים והמשחקים HD",
        "title": "ערוץ פרטנר TV – ערוץ השעשועונים",
        "desc": "שעשועוני טלוויזיה, משחקי ידע, אתגרים וחידונים מרתקים לכל המשפחה בפרטנר TV.",
        "url": "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/32158/index.m3u8",
        "backup_urls": [
            "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/32161/index.m3u8"
        ],
        "category": "ערוצי פרטנר TV (Partner TV)",
        "categoryEn": "partner",
        "lang": "he",
        "country": "IL",
        "countryHe": "ישראל",
        "badge": "HD",
        "kind": "live",
        "logo": "assets/logos/partner.svg"
    },
    {
        "id": "partner_vacation_il",
        "name": "ערוץ פרטנר TV – הנופש והחופש HD",
        "title": "ערוץ פרטנר TV – ערוץ הנופש והתיירות",
        "desc": "חופשות, יעדי תיירות מרהיבים, מלונות ואטרקציות בישראל ובעולם בפרטנר TV.",
        "url": "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/32160/index.m3u8",
        "backup_urls": [
            "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/2361/index.m3u8"
        ],
        "category": "ערוצי פרטנר TV (Partner TV)",
        "categoryEn": "partner",
        "lang": "he",
        "country": "IL",
        "countryHe": "ישראל",
        "badge": "HD",
        "kind": "live",
        "logo": "assets/logos/partner.svg"
    },
    {
        "id": "partner_viva_il",
        "name": "ערוץ פרטנר TV – ויוה טלנובלות HD",
        "title": "ערוץ פרטנר TV – ערוץ ויוה דרמות וטלנובלות",
        "desc": "ערוץ הטלנובלות והדרמות הרומנטיות ויוה בפרטנר TV – מיטב הסדרות מאמל\"ט, ספרד וטורקיה.",
        "url": "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/32320/index.m3u8",
        "backup_urls": [
            "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/vamos/live.livx/playlist.m3u8"
        ],
        "category": "ערוצי פרטנר TV (Partner TV)",
        "categoryEn": "partner",
        "lang": "he",
        "country": "IL",
        "countryHe": "ישראל",
        "badge": "HD",
        "kind": "live",
        "logo": "assets/logos/partner.svg"
    },
    {
        "id": "partner_junior_il",
        "name": "ערוץ פרטנר TV – ג'וניור פלוס HD",
        "title": "ערוץ פרטנר TV – סדרות ג'וניור לילדים",
        "desc": "סדרות האנימציה, הרפתקאות וקומדיות לילדים ולנוער בערוץ ג'וניור בפרטנר TV.",
        "url": "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/2366/index.m3u8",
        "backup_urls": [
            "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/2338/index.m3u8"
        ],
        "category": "ערוצי פרטנר TV (Partner TV)",
        "categoryEn": "partner",
        "lang": "he",
        "country": "IL",
        "countryHe": "ישראל",
        "badge": "HD",
        "kind": "live",
        "logo": "assets/logos/partner.svg"
    },
    {
        "id": "partner_design_il",
        "name": "ערוץ פרטנר TV – עיצוב ולייף סטייל HD",
        "title": "ערוץ פרטנר TV – ערוץ העיצוב והאדריכלות",
        "desc": "עיצוב פנים, אדריכלות ישראלית ובינלאומית, סיורים בבתים מעוצבים בפרטנר TV.",
        "url": "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/32124/index.m3u8",
        "backup_urls": [
            "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/12266/index.m3u8"
        ],
        "category": "ערוצי פרטנר TV (Partner TV)",
        "categoryEn": "partner",
        "lang": "he",
        "country": "IL",
        "countryHe": "ישראל",
        "badge": "HD",
        "kind": "live",
        "logo": "assets/logos/partner.svg"
    }
]

with open("channels.json", encoding="utf-8") as f:
    chs = json.load(f)

removed_ru = []
removed_ar = []
removed_pluto = []
surviving = []

for c in chs:
    cid = c.get("id", "")
    lang = c.get("lang", "")
    name = c.get("name", "")
    desc = c.get("desc", "")
    title = c.get("title", "")
    url = c.get("url", "")
    
    # Check Russian
    is_ru = lang == "ru" or bool(cyrillic.search(name)) or bool(cyrillic.search(title)) or "רוסית" in name or "רוסית" in desc
    if is_ru:
        removed_ru.append(c)
        continue
        
    # Check Arabic
    is_ar = lang == "ar" or bool(arabic.search(name)) or bool(arabic.search(title)) or "בערבית" in name or "בערבית" in desc or "ערבית" in name
    if is_ar:
        removed_ar.append(c)
        continue
        
    # Check Dead Pluto
    if cid in DEAD_PLUTO_IDS or (("pluto.tv" in url or "pluto" in cid) and cid in DEAD_PLUTO_IDS):
        removed_pluto.append(c)
        continue
        
    surviving.append(c)

# Add Partner channels at appropriate position (before World / after Israeli live)
# Find index of end of Israeli channels
insert_idx = 0
for i, c in enumerate(surviving):
    if c.get("category") == "ערוצי ישראל - שידור חי":
        insert_idx = i + 1

surviving[insert_idx:insert_idx] = PARTNER_CHANNELS

print(f"Original count: {len(chs)}")
print(f"Removed Russian channels: {len(removed_ru)}")
print(f"Removed Arabic channels: {len(removed_ar)}")
print(f"Removed Pluto channels: {len(removed_pluto)}")
print(f"Added Partner channels: {len(PARTNER_CHANNELS)}")
print(f"New total count: {len(surviving)}")

with open("channels.json", "w", encoding="utf-8") as f:
    json.dump(surviving, f, ensure_ascii=False, indent=2)

# Write summary reports for response
with open("tools/removed_summary.json", "w", encoding="utf-8") as f:
    json.dump({
        "removed_ru": [{"id": c["id"], "name": c["name"]} for c in removed_ru],
        "removed_ar": [{"id": c["id"], "name": c["name"]} for c in removed_ar],
        "removed_pluto": [{"id": c["id"], "name": c["name"]} for c in removed_pluto],
        "added_partner": [{"id": c["id"], "name": c["name"]} for c in PARTNER_CHANNELS],
    }, f, ensure_ascii=False, indent=2)

print("Done writing channels.json and summary.")

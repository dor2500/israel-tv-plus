import json
import os
import shutil

# Load channels.json
with open('channels.json', 'r', encoding='utf-8') as f:
    channels = json.load(f)

print(f"Original channels count: {len(channels)}")

new_items = [
    {
        "id": "knesset_tv_hd",
        "name": "ערוץ הכנסת HD (Knesset Channel)",
        "title": "ערוץ הכנסת HD – שידורי המליאה והוועדות",
        "category": "ערוצי ישראל - שידור חי",
        "categoryEn": "israel",
        "logo": "https://upload.wikimedia.org/wikipedia/he/thumb/9/91/Knesset_Channel_logo.svg/300px-Knesset_Channel_logo.svg.png",
        "url": "http://stream.mcquack.net/48/index.m3u8",
        "backupUrls": [
            "http://45.156.223.202/iptv/QAP2YS2594ZQS3/2369/index.m3u8",
            "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/2369/index.m3u8"
        ],
        "channelNumber": 99,
        "number": 99,
        "language": "Hebrew",
        "lang": "he",
        "country": "IL",
        "countryHe": "ישראל",
        "badge": "HD",
        "quality": "HD",
        "resolution": "1080p",
        "desc": "ערוץ הכנסת המשדר דיוני מליאה, ועדות ומשדרים פוליטיים ואקטואליים."
    },
    {
        "id": "channel_9_israel_hd",
        "name": "ערוץ 9 ישראל HD",
        "title": "ערוץ 9 ישראל HD – שידור חי ישיר",
        "category": "ערוצי ישראל - שידור חי",
        "categoryEn": "israel",
        "logo": "https://upload.wikimedia.org/wikipedia/he/thumb/b/b3/Channel_9_Israel_logo_2021.svg/300px-Channel_9_Israel_logo_2021.svg.png",
        "url": "https://contact.gostreaming.tv/Con-11/index.m3u8",
        "backupUrls": [
            "https://hlspackager.akamaized.net/live/DB/Israel9/HLS/Israel9.m3u8"
        ],
        "channelNumber": 9,
        "number": 9,
        "language": "Hebrew/Russian",
        "lang": "he,ru",
        "country": "IL",
        "countryHe": "ישראל",
        "badge": "HD",
        "quality": "HD",
        "resolution": "1080p",
        "desc": "ערוץ 9 הישראלי המשדר חדשות, אקטואליה, תרבות ותוכן ישראלי ורוסי."
    },
    {
        "id": "ch14_now_fast",
        "name": "ערוץ 14 החדשות עכשיו (Now 14 Live)",
        "title": "עכשיו 14 – מהדורות החדשות והאקטואליה בשידור ישיר",
        "category": "ערוצי ישראל - שידור חי",
        "categoryEn": "israel",
        "logo": "https://upload.wikimedia.org/wikipedia/commons/thumb/4/4c/Now_14_Logo.svg/300px-Now_14_Logo.svg.png",
        "url": "https://r.il.cdn-redge.media/livehls/oil/ch14/live/ch14/live.livx/playlist.m3u8",
        "backupUrls": [
            "https://ch14-channel14-live.cdn.ch22.co.il/live/ch14_1/playlist.m3u8"
        ],
        "channelNumber": 140,
        "number": 140,
        "language": "Hebrew",
        "lang": "he",
        "country": "IL",
        "countryHe": "ישראל",
        "badge": "HD",
        "quality": "HD",
        "resolution": "1080p",
        "desc": "ערוץ 14 – שידורי חדשות, פטריוטים, אקטואליה ומבזקים שוטפים בטעינה מהירה."
    },
    {
        "id": "hidabroot_tv_hd",
        "name": "ערוץ הידברות HD",
        "title": "ערוץ הידברות HD – ערוץ הטלוויזיה היהודי הגדול בעולם",
        "category": "מסורת וקהילה",
        "categoryEn": "tradition",
        "logo": "https://upload.wikimedia.org/wikipedia/he/thumb/2/22/Hidabroot_Logo.png/300px-Hidabroot_Logo.png",
        "url": "https://cdn.cybercdn.live/HidabrootIL/Live97/playlist.m3u8",
        "backupUrls": [],
        "channelNumber": 97,
        "number": 97,
        "language": "Hebrew",
        "lang": "he",
        "country": "IL",
        "countryHe": "ישראל",
        "badge": "HD",
        "quality": "HD",
        "resolution": "1080p",
        "desc": "שידורי ערוץ הידברות HD: הרצאות, תוכניות אירוח, חינוך ויהדות."
    },
    {
        "id": "i24news_arabic_hd",
        "name": "ערוץ i24NEWS ערבית HD",
        "title": "ערוץ i24NEWS ערבית – שידורי אקטואליה וחדשות מישראל",
        "category": "חדשות מהעולם (News)",
        "categoryEn": "news",
        "logo": "https://upload.wikimedia.org/wikipedia/commons/thumb/4/4b/I24NEWS_logo.svg/300px-I24NEWS_logo.svg.png",
        "url": "https://i24newsarabic-cdn.encoders.immergo.tv/master.m3u8",
        "backupUrls": [
            "https://hlspackager.akamaized.net/live/DB/i24_ARABIC/HLS/i24_ARABIC.m3u8"
        ],
        "channelNumber": 331,
        "number": 331,
        "language": "Arabic",
        "lang": "ar",
        "country": "IL",
        "countryHe": "ישראל",
        "badge": "HD",
        "quality": "HD",
        "resolution": "1080p",
        "desc": "חדשות ואקטואליה ישראלית ובינלאומית בערבית מאולפני נמל יפו."
    },
    {
        "id": "i24news_english_hd",
        "name": "ערוץ i24NEWS אנגלית HD",
        "title": "ערוץ i24NEWS אנגלית – שידורי אקטואליה וחדשות מישראל",
        "category": "חדשות מהעולם (News)",
        "categoryEn": "news",
        "logo": "https://upload.wikimedia.org/wikipedia/commons/thumb/4/4b/I24NEWS_logo.svg/300px-I24NEWS_logo.svg.png",
        "url": "https://i24newsenglish-cdn.encoders.immergo.tv/master.m3u8",
        "backupUrls": [
            "https://hlspackager.akamaized.net/live/DB/i24_ENG/HLS/i24_ENG.m3u8"
        ],
        "channelNumber": 332,
        "number": 332,
        "language": "English",
        "lang": "en",
        "country": "IL",
        "countryHe": "ישראל",
        "badge": "HD",
        "quality": "HD",
        "resolution": "1080p",
        "desc": "ערוץ i24NEWS המשדר חדשות ואקטואליה בינלאומית וישראלית באנגלית."
    },
    {
        "id": "kan_hinuchit_mc",
        "name": "כאן חינוכית HD (חינוכית ילדים)",
        "title": "כאן חינוכית HD – תוכניות ילדים ונוער חינוכיות",
        "category": "ילדים ונוער (בעברית)",
        "categoryEn": "kids",
        "logo": "https://upload.wikimedia.org/wikipedia/he/thumb/8/87/Kan_Hinuchit.svg/300px-Kan_Hinuchit.svg.png",
        "url": "http://stream.mcquack.net/378/index.m3u8",
        "backupUrls": [
            "https://kancdn.medonecdn.net/livehls/oil/kancdn-live/live/kan23/live.livx/playlist.m3u8"
        ],
        "channelNumber": 80,
        "number": 80,
        "language": "Hebrew",
        "lang": "he",
        "country": "IL",
        "countryHe": "ישראל",
        "badge": "HD",
        "quality": "HD",
        "resolution": "1080p",
        "desc": "שידורי כאן חינוכית לילדים ולנוער – סדרות איכותיות ומקוריות לכל המשפחה."
    },
    {
        "id": "hop_kids_mc",
        "name": "ערוץ הופ! ילדים HD (Hop! Kids)",
        "title": "ערוץ הופ! לילדים ופעוטות – תוכניות בעברית",
        "category": "ילדים ונוער (בעברית)",
        "categoryEn": "kids",
        "logo": "https://upload.wikimedia.org/wikipedia/he/thumb/b/b3/Hop%21_Channel_Logo_2020.svg/300px-Hop%21_Channel_Logo_2020.svg.png",
        "url": "http://stream.mcquack.net/44/index.m3u8",
        "backupUrls": [
            "http://45.156.223.202/iptv/QAP2YS2594ZQS3/12244/index.m3u8",
            "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/12244/index.m3u8"
        ],
        "channelNumber": 77,
        "number": 77,
        "language": "Hebrew",
        "lang": "he",
        "country": "IL",
        "countryHe": "ישראל",
        "badge": "HD",
        "quality": "HD",
        "resolution": "1080p",
        "desc": "ערוץ הופ! המוביל בישראל לילדים וגיל הרך עם תוכניות מקור ושירים אהובים."
    },
    {
        "id": "zoom_kids_mc",
        "name": "ערוץ זום ZOOM ילדים ונוער HD",
        "title": "ערוץ זום ZOOM – סדרות ותוכניות אהובות לילדים ונוער",
        "category": "ילדים ונוער (בעברית)",
        "categoryEn": "kids",
        "logo": "https://upload.wikimedia.org/wikipedia/he/thumb/f/f6/Zoom_Channel_Logo.png/300px-Zoom_Channel_Logo.png",
        "url": "http://stream.mcquack.net/377/index.m3u8",
        "backupUrls": [
            "http://45.156.223.202/iptv/QAP2YS2594ZQS3/2359/index.m3u8",
            "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/2359/index.m3u8"
        ],
        "channelNumber": 84,
        "number": 84,
        "language": "Hebrew",
        "lang": "he",
        "country": "IL",
        "countryHe": "ישראל",
        "badge": "HD",
        "quality": "HD",
        "resolution": "1080p",
        "desc": "ערוץ זום: סדרות אקשן, קומדיות והרפתקאות בדיבוב עברי לנוער וילדים."
    },
    {
        "id": "viva_premium_mc",
        "name": "ערוץ ויוה פרימיום HD (Viva Premium)",
        "title": "ערוץ ויוה פרימיום HD – טלנובלות וסדרות דרמה מובילות",
        "category": "סרטים וסדרות (Movies & Series)",
        "categoryEn": "series",
        "logo": "https://upload.wikimedia.org/wikipedia/he/thumb/1/14/Viva_logo.svg/300px-Viva_logo.svg.png",
        "url": "http://stream.mcquack.net/43/index.m3u8",
        "backupUrls": [
            "http://45.156.223.202/iptv/QAP2YS2594ZQS3/12226/index.m3u8",
            "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/12226/index.m3u8"
        ],
        "channelNumber": 39,
        "number": 39,
        "language": "Hebrew/Spanish",
        "lang": "he,es",
        "country": "IL",
        "countryHe": "ישראל",
        "badge": "HD",
        "quality": "HD",
        "resolution": "1080p",
        "desc": "ויוה פרימיום – מיטב הטלנובלות והסדרות הבינלאומיות עם תרגום עברי מלא."
    },
    {
        "id": "good_life_mc",
        "name": "ערוץ החיים הטובים HD (Good Life)",
        "title": "ערוץ החיים הטובים HD – לייפסטייל, עיצוב ואירוח",
        "category": "ערוצי ישראל - שידור חי",
        "categoryEn": "israel",
        "logo": "https://upload.wikimedia.org/wikipedia/he/thumb/3/3d/Good_Life_Channel_Logo.png/300px-Good_Life_Channel_Logo.png",
        "url": "http://stream.mcquack.net/47/index.m3u8",
        "backupUrls": [
            "http://45.156.223.202/iptv/QAP2YS2594ZQS3/2355/index.m3u8",
            "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/2355/index.m3u8"
        ],
        "channelNumber": 32,
        "number": 32,
        "language": "Hebrew",
        "lang": "he",
        "country": "IL",
        "countryHe": "ישראל",
        "badge": "HD",
        "quality": "HD",
        "resolution": "1080p",
        "desc": "ערוץ החיים הטובים: תוכניות אירוח, עיצוב הבית, אוכל ונופש."
    },
    {
        "id": "yes_movies_action_mc",
        "name": "yes קולנוע אקשן HD (yes Movies Action)",
        "title": "yes קולנוע אקשן HD – סרטי מתח, פעולה ושוברי קופות",
        "category": "סרטים וסדרות (Movies & Series)",
        "categoryEn": "movies",
        "logo": "https://upload.wikimedia.org/wikipedia/he/thumb/a/a2/Yes_Movies_Action.svg/300px-Yes_Movies_Action.svg.png",
        "url": "http://stream.mcquack.net/380/index.m3u8",
        "backupUrls": [
            "http://45.156.223.202/iptv/QAP2YS2594ZQS3/12270/index.m3u8",
            "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/12270/index.m3u8"
        ],
        "channelNumber": 18,
        "number": 18,
        "language": "Hebrew/English",
        "lang": "he,en",
        "country": "IL",
        "countryHe": "ישראל",
        "badge": "HD",
        "quality": "HD",
        "resolution": "1080p",
        "desc": "ערוץ הסרטים המוביל ב-yes לסרטי פעולה, מתח ומד\"ב בדיבוב ותרגום עברי."
    },
    {
        "id": "hot_drama_mc",
        "name": "HOT דרמה HD (HOT Drama)",
        "title": "HOT דרמה HD – סרטי דרמה מרגשים וסדרות איכות",
        "category": "סרטים וסדרות (Movies & Series)",
        "categoryEn": "series",
        "logo": "https://upload.wikimedia.org/wikipedia/he/thumb/9/91/HOT_Drama.png/300px-HOT_Drama.png",
        "url": "http://stream.mcquack.net/381/index.m3u8",
        "backupUrls": [
            "http://45.156.223.202/iptv/QAP2YS2594ZQS3/32140/index.m3u8",
            "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/32140/index.m3u8"
        ],
        "channelNumber": 19,
        "number": 19,
        "language": "Hebrew/English",
        "lang": "he,en",
        "country": "IL",
        "countryHe": "ישראל",
        "badge": "HD",
        "quality": "HD",
        "resolution": "1080p",
        "desc": "HOT דרמה – סרטים מרגשים, הפקות מקור ודרמות מנצחות מהקולנוע העולמי."
    }
]

# Update existing channels with redundant backups:
for c in channels:
    cid = c.get('id', '')
    name = c.get('name', '')
    # Keshet 12
    if 'קשת 12' in name or cid == 'keshet_12':
        b = c.setdefault('backupUrls', [])
        if 'http://stream.mcquack.net/294/index.m3u8' not in b:
            b.append('http://stream.mcquack.net/294/index.m3u8')
    # Reshet 13
    elif 'רשת 13' in name or cid == 'reshet_13':
        b = c.setdefault('backupUrls', [])
        if 'http://stream.mcquack.net/295/index.m3u8' not in b:
            b.append('http://stream.mcquack.net/295/index.m3u8')
    # Sport 1
    elif 'ספורט 1' in name and 'רדיו' not in name:
        b = c.setdefault('backupUrls', [])
        for u in ['http://45.156.223.202/iptv/QAP2YS2594ZQS3/2353/index.m3u8',
                  'http://stream.mcquack.net/50/index.m3u8']:
            if u not in b:
                b.append(u)
    # Sport 2
    elif 'ספורט 2' in name and 'רדיו' not in name:
        b = c.setdefault('backupUrls', [])
        for u in ['http://45.156.223.202/iptv/QAP2YS2594ZQS3/2352/index.m3u8',
                  'http://stream.mcquack.net/51/index.m3u8']:
            if u not in b:
                b.append(u)
    # Sport 3
    elif 'ספורט 3' in name and 'רדיו' not in name:
        b = c.setdefault('backupUrls', [])
        if 'http://stream.mcquack.net/52/index.m3u8' not in b:
            b.append('http://stream.mcquack.net/52/index.m3u8')
    # Sport 4
    elif 'ספורט 4' in name and 'רדיו' not in name:
        b = c.setdefault('backupUrls', [])
        if 'http://stream.mcquack.net/53/index.m3u8' not in b:
            b.append('http://stream.mcquack.net/53/index.m3u8')
    # Sport 5
    elif ('ספורט 5 FHD' in name or 'ספורט 5 –' in name or cid == 'sport5_fhd') and 'פלוס' not in name and 'לייב' not in name and 'גולד' not in name and 'סטארס' not in name and 'מקס' not in name and '4K' not in name:
        b = c.setdefault('backupUrls', [])
        for u in ['http://45.156.223.202/iptv/QAP2YS2594ZQS3/32001/index.m3u8',
                  'http://stream.mcquack.net/55/index.m3u8']:
            if u not in b:
                b.append(u)
    # Sport 5 Plus
    elif 'ספורט 5 פלוס' in name:
        b = c.setdefault('backupUrls', [])
        u = 'http://45.156.223.202/iptv/QAP2YS2594ZQS3/32002/index.m3u8'
        if u not in b:
            b.append(u)
    # Sky news extra 1
    elif cid == 'sky_news_au_extra1':
        b = c.setdefault('backupUrls', [])
        u = 'https://skynewsau-live.akamaized.net/hls/live/2002690/skynewsau-extra2/master.m3u8'
        if u not in b:
            b.append(u)

# Append new channels
existing_ids = {c['id'] for c in channels}
added_count = 0
for item in new_items:
    if item['id'] not in existing_ids:
        channels.append(item)
        added_count += 1

print(f"Added {added_count} new channels! Total now: {len(channels)}")

# Write to channels.json
with open('channels.json', 'w', encoding='utf-8') as f:
    json.dump(channels, f, ensure_ascii=False, indent=1)

# Write to dist/channels.json
os.makedirs('dist', exist_ok=True)
with open('dist/channels.json', 'w', encoding='utf-8') as f:
    json.dump(channels, f, ensure_ascii=False, indent=1)

# Write to channels.js and www/channels.js
js_content = "window.ITV_CHANNELS = " + json.dumps(channels, ensure_ascii=False, indent=1) + ";\n"
with open('channels.js', 'w', encoding='utf-8') as f:
    f.write(js_content)
with open('www/channels.js', 'w', encoding='utf-8') as f:
    f.write(js_content)

print("Saved channels.json, dist/channels.json, channels.js, and www/channels.js successfully!")

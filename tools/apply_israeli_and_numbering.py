import json, os

with open('channels.json', 'r', encoding='utf-8') as f:
    chs = json.load(f)

print(f"Original channels count: {len(chs)}")

# 1. New Israeli Channels to add
new_israeli_channels = [
    {
        "id": "sport_5_4k",
        "name": "ערוץ ספורט 5 4K UHD",
        "title": "ספורט 5 4K שידור חי באיכות UHD",
        "category": "ספורט (Sports)",
        "categoryEn": "sports",
        "kind": "tv",
        "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/sport_5_4k/live.livx/playlist.m3u8",
        "badge": "4K UHD",
        "desc": "שידורי ספורט ישירים בטכנולוגיית 4K Ultra HD: ליגת האלופות, ליגת העל בכדורגל, מכבי תל אביב ביורוליג והמשחקים המרכזיים בחדות שיא.",
        "lang": "he",
        "logo": "assets/logos/sport5.png",
        "country": "IL",
        "countryHe": "ישראל",
        "backups": []
    },
    {
        "id": "one_doco",
        "name": "ערוץ ONE דוקו ספורט HD",
        "title": "ONE דוקו ספורט – סרטי תעודה וסיפורי ספורט",
        "category": "ספורט (Sports)",
        "categoryEn": "sports",
        "kind": "tv",
        "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/one_doco/live.livx/playlist.m3u8",
        "badge": "HD",
        "desc": "סרטי דוקומנטרי וסיפורי עומק של גדולי הספורטאים, מאחורי הקלעים של קבוצות ליגת העל ועולם הספורט הבינלאומי.",
        "lang": "he",
        "logo": "assets/logos/one_sport.svg",
        "country": "IL",
        "countryHe": "ישראל",
        "backups": []
    },
    {
        "id": "erez_nehederet_tv",
        "name": "ערוץ ארץ נהדרת 24/7 HD",
        "title": "ארץ נהדרת – ערוץ המערכונים הרשמי 24/7",
        "category": "ערוצי ישראל - שידור חי",
        "categoryEn": "israel",
        "kind": "tv",
        "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/erez_nehederet/live.livx/playlist.m3u8",
        "badge": "HD 24/7",
        "desc": "כל העונות, החיקויים, המערכונים הבלתי נשכחים והדמויות האהובות מתולדות ארץ נהדרת בשידור רציף 24/7 מבית קשת 12 ו-FreeTV.",
        "lang": "he",
        "logo": "assets/logos/keshet12.png",
        "country": "IL",
        "countryHe": "ישראל",
        "backups": []
    },
    {
        "id": "savri_maranan_tv",
        "name": "ערוץ סברי מרנן 24/7 HD",
        "title": "סברי מרנן – כל הפרקים והעונות 24/7",
        "category": "ערוצי ישראל - שידור חי",
        "categoryEn": "israel",
        "kind": "tv",
        "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/savri/live.livx/playlist.m3u8",
        "badge": "HD 24/7",
        "desc": "סדרת הקומדיה המשפחתית המצליחה בישראל: כל ארוחות שישי של משפחת חסון ורוזן בשידור רציף מסביב לשעון.",
        "lang": "he",
        "logo": "assets/logos/keshet12.png",
        "country": "IL",
        "countryHe": "ישראל",
        "backups": []
    },
    {
        "id": "foody_il",
        "name": "ערוץ פודי Foody בישול ישראלי HD",
        "title": "פודי Foody – מתכונים, שפים ואפייה",
        "category": "ערוצי ישראל - שידור חי",
        "categoryEn": "israel",
        "kind": "tv",
        "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/foody/live.livx/playlist.m3u8",
        "badge": "FHD",
        "desc": "ערוץ האוכל המוביל של ישראל: השפים הטובים בארץ, מתכונים לשישי, סדנאות אפייה ורעיונות לארוחות מהירות 24/7.",
        "lang": "he",
        "logo": "assets/logos/food_channel.svg",
        "country": "IL",
        "countryHe": "ישראל",
        "backups": []
    },
    {
        "id": "viva_plus_il",
        "name": "ערוץ ויוה פלוס HD (Viva Plus)",
        "title": "ויוה פלוס – הדרמות והטלנובלות המובילות בעולם",
        "category": "ערוצי ישראל - שידור חי",
        "categoryEn": "israel",
        "kind": "tv",
        "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/viva_plus/live.livx/playlist.m3u8",
        "badge": "FHD",
        "desc": "הסדרות והטלנובלות המדוברות ביותר מספרד, טורקיה, ברזיל וארגנטינה בדיבוב ובכתוביות בעברית.",
        "lang": "he",
        "logo": "assets/logos/viva_plus.png",
        "country": "IL",
        "countryHe": "ישראל",
        "backups": []
    },
    {
        "id": "karaoke_il",
        "name": "ערוץ קריוקי ישראלי 24/7 HD",
        "title": "קריוקי ישראלי – שרים את הלהיטים של ישראל",
        "category": "מוזיקה (Music TV)",
        "categoryEn": "music",
        "kind": "tv",
        "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/karaoke/live.livx/playlist.m3u8",
        "badge": "HD 24/7",
        "desc": "ערוץ הקריוקי הרשמי של ישראל עם מילים על המסך של מיטב להיטי הפופ, הרוק, והמוזיקה הים תיכונית לשירה בבית.",
        "lang": "he",
        "logo": "assets/logos/music24.svg",
        "country": "IL",
        "countryHe": "ישראל",
        "backups": []
    }
]

existing_ids = {c['id']: c for c in chs}

# Add new channels if not existing
for nc in new_israeli_channels:
    if nc['id'] not in existing_ids:
        chs.append(nc)
        existing_ids[nc['id']] = nc
        print(f"Added new channel: {nc['name']}")

# 2. Add extra backup servers to main Israeli channels
backups_to_add = {
    'keshet12': [
        'https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/keshet_12_subs/live.livx/playlist.m3u8',
        'http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/12201/index.m3u8'
    ],
    'reshet13': [
        'https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/reshet_13/live.livx/playlist.m3u8',
        'http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/12202/index.m3u8',
        'https://d15ds134q59udk.cloudfront.net/media/3b45de9e-c944-4fdc-a344-7c3042b5b104/mainManifest.m3u8'
    ],
    'now14': [
        'https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/ch14_subs/live.livx/playlist.m3u8',
        'http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/12203/index.m3u8'
    ],
    'kan11': [
        'https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/kan11_subs/live.livx/playlist.m3u8',
        'http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/12200/index.m3u8'
    ],
    'i24news_he': [
        'https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/i24_he/live.livx/playlist.m3u8',
        'http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/12205/index.m3u8'
    ],
    'channel9': [
        'https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/ch9/live.livx/playlist.m3u8',
        'http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/12206/index.m3u8'
    ],
    'kan_hinuchit': [
        'https://kancdn.medonecdn.net/livehls/oil/kancdn-live/live/23/live.livx/playlist.m3u8',
        'http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/12207/index.m3u8'
    ],
    'channel24': [
        'https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/music24/live.livx/playlist.m3u8',
        'http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/12208/index.m3u8'
    ],
    'knesset': [
        'https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/kneset/live.livx/playlist.m3u8'
    ],
    'sport1_israel': ['http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/12249/index.m3u8'],
    'sport2_israel': ['http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/12250/index.m3u8'],
    'sport3_israel': ['http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/12251/index.m3u8'],
    'sport4_israel': ['http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/12252/index.m3u8']
}

for cid, b_list in backups_to_add.items():
    if cid in existing_ids:
        cur_backups = existing_ids[cid].get('backups', [])
        for b in b_list:
            if b not in cur_backups and b != existing_ids[cid]['url']:
                cur_backups.append(b)
        existing_ids[cid]['backups'] = cur_backups

# 3. Canonical Numbering:
# Match viewers' exact remote expectations!
# 9 -> Ch9, 10 -> Calcala 10, 11 -> Kan 11, 12 -> Keshet 12, 13 -> Reshet 13, 14 -> Now 14, 15 -> i24News, 16 -> Ch16, 23 -> Hinuchit, 24 -> Ch24, 98 -> 98, 99 -> Knesset
CANONICAL_NUMBERS = {
    # Israeli news & linear
    "walla_news_il": 1,
    "ynet_live": 2,
    "walla_yes_il": 3,
    "hot_bidur": 4,
    "hot_reality": 5,
    "reshet13_comedy_il": 6,
    "home_design_israel_hd": 7,
    "gameshow_channel_il": 8,
    "channel9": 9,
    "calcala10": 10,
    "kan11": 11,
    "keshet12": 12,
    "reshet13": 13,
    "now14": 14,
    "i24news_he": 15,
    "channel16": 16,
    "erez_nehederet_tv": 17,
    "yes_docu": 18,
    "hot_8": 19,
    "home_plus_il": 20,
    "health_channel_il": 21,
    "travel_channel_il": 22,
    "kan_hinuchit": 23,
    "channel24": 24,
    "savri_maranan_tv": 25,
    "e_entertainment_il": 26,
    "ego_channel_il": 27,
    "cbs_reality_il": 28,
    "humor_channel_il": 29,
    "foody_il": 30,
    "viva_plus_il": 31,
    "viva_israel": 32,
    "karaoke_il": 33,
    "il_the_style_channel_hd_il": 34,
    "food_israel": 35,
    "hot_mind": 36,

    # Sports
    "sport_5_4k": 48,
    "one_doco": 49,
    "sport1_israel": 50,
    "sport2_israel": 51,
    "sport3_israel": 52,
    "sport4_israel": 53,
    "sport_5_hd": 54,
    "sport_5_plus_hd": 55,
    "sport_5_live_hd": 56,
    "sport_5_gold_hd": 57,
    "sport_5_starz_hd": 58,
    "one_sport_hd": 59,
    "one_2_sport_hd": 60,

    # Kids
    "arutz_hayeladim": 61,
    "logi_tv": 62,
    "hop_kids_israel": 63,
    "yalduti_tv": 64,
    "hop_pele_tv": 65,
    "luli_tv_il": 66,
    "disney_channel_il": 67,
    "disney_jr_il": 68,
    "nickelodeon_il": 69,
    "nick_jr_il": 70,
    "teennick_il": 71,
    "zoom_kids": 72,
    "junior_tv": 73,
    "yoyo_tv": 74,
    "baby_israel_tv": 75,
    "yes_movies_kids_il": 76,
    "hot_cinema_family_il": 77,
    "wiz_kids_il": 78,
    "hot_buzz_il": 79,
    "fomo_tv": 80,
    "yalduti_songs_tv": 81,
    "a_plus_kids_il": 82,
    "stars_kids_il": 83,
    "radios_disney": 84,
    "radios_kids": 85,
    "radios_stars": 86,
    "nick_jr_party_il": 87,

    # Knesset & Community
    "channel_98_il": 98,
    "knesset": 99
}

used_numbers = set(CANONICAL_NUMBERS.values())
next_num = 100

for c in chs:
    cid = c['id']
    if cid in CANONICAL_NUMBERS:
        c['number'] = CANONICAL_NUMBERS[cid]
    else:
        while next_num in used_numbers:
            next_num += 1
        c['number'] = next_num
        used_numbers.add(next_num)
        next_num += 1

chs.sort(key=lambda x: x['number'])

with open('channels.json', 'w', encoding='utf-8') as f:
    json.dump(chs, f, ensure_ascii=False, indent=2)

print(f"Total channels after update: {len(chs)}")
print("News & Canonical numbers:")
for c in chs:
    if c['number'] in [9, 10, 11, 12, 13, 14, 15, 16, 23, 24, 98, 99]:
        print(f"#{c['number']}: {c['name']} ({c['id']})")

import json, re

repo_dir = r'C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo'
channels_path = f'{repo_dir}\\channels.js'

with open(channels_path, 'r', encoding='utf-8') as f:
    txt = f.read()

m = re.search(r'window\.CHANNELS_DATA\s*=\s*(\[.*?\]);?\s*$', txt, re.DOTALL)
channels = json.loads(m.group(1))
print(f'Starting with {len(channels)} channels')

by_id = {ch['id']: ch for ch in channels}

# 1. Update disney_jr_il with verified live continuous broadcast
if 'disney_jr_il' in by_id:
    by_id['disney_jr_il']['url'] = 'http://88.212.15.50/live/test_disney_junior/playlist.m3u8'
    by_id['disney_jr_il']['badge'] = 'HD'
    by_id['disney_jr_il']['desc'] = 'ערוץ דיסני ג\'וניור ישראל – שידור חי ישיר 24/7 ללא לופים'
    by_id['disney_jr_il']['backups'] = ['http://88.212.15.50/live/test_disney_junior/playlist.m3u8']
    print('Updated disney_jr_il')

# 2. Update logi_tv
if 'logi_tv' in by_id:
    by_id['logi_tv']['url'] = 'https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/logi/live.livx/playlist.m3u8'
    by_id['logi_tv']['badge'] = 'HD'
    print('Updated logi_tv')

# 3. Add or update requested channels
new_or_updated = [
    {
        'id': 'hot_docu_il',
        'name': 'HOT 8 מדע ודוקו HD',
        'title': 'HOT 8 ערוץ הדוקו והמדע (HOT Docu HD)',
        'category': 'ערוצי ישראל - שידור חי',
        'categoryEn': 'israel',
        'url': 'http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/32142/index.m3u8',
        'backups': ['http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/12216/index.m3u8'],
        'fallbackUrls': ['http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/12216/index.m3u8'],
        'logo': 'assets/channels/hot8.png',
        'badge': 'HD',
        'desc': 'ערוץ הדוקו, המדע וההיסטוריה המוביל מבית HOT – סדרות תעודה איכותיות ויוצרים ישראליים.',
        'lang': 'he',
        'country': 'IL',
        'countryHe': 'ישראל',
        'webOk': True
    },
    {
        'id': 'hot_mind_docu_il',
        'name': 'HOT מיינד דוקו והעשרה HD',
        'title': 'ערוץ HOT Mind דוקו והעשרה HD',
        'category': 'ערוצי ישראל - שידור חי',
        'categoryEn': 'israel',
        'url': 'http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/32143/index.m3u8',
        'backups': [],
        'fallbackUrls': [],
        'logo': 'assets/logos/hot_reality.png',
        'badge': 'HD',
        'desc': 'ערוץ HOT Mind – ערוץ דוקו, מדע, מחשבה, טכנולוגיה ותרבות.',
        'lang': 'he',
        'country': 'IL',
        'countryHe': 'ישראל',
        'webOk': True
    },
    {
        'id': 'humor_2_channel_il',
        'name': 'ערוץ ההומור 2 HD',
        'title': 'ערוץ ההומור 2 – צחוק ובידור HD',
        'category': 'ערוצי ישראל - שידור חי',
        'categoryEn': 'israel',
        'url': 'http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/32316/index.m3u8',
        'backups': ['http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/32161/index.m3u8'],
        'fallbackUrls': [],
        'logo': 'assets/logos/humor.png',
        'badge': 'HD',
        'desc': 'ערוץ ההומור 2 – סטנדאפ, מערכונים, קומדיות ובידור ישראלי ברצף 24/7.',
        'lang': 'he',
        'country': 'IL',
        'countryHe': 'ישראל',
        'webOk': True
    },
    {
        'id': 'classic_comedy_il',
        'name': 'ערוץ הקומדיות הקלאסיות HD',
        'title': 'ערוץ הקומדיות הקלאסיות והישראליות HD',
        'category': 'ערוצי ישראל - שידור חי',
        'categoryEn': 'israel',
        'url': 'http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/32136/index.m3u8',
        'backups': [],
        'fallbackUrls': [],
        'logo': 'assets/logos/reshet13_comedy_il.png',
        'badge': 'HD',
        'desc': 'ערוץ הקומדיות הקלאסיות – סדרות הקומדיה האהובות מכל הזמנים, מערכונים נוסטלגיים וסרטי בורקס.',
        'lang': 'he',
        'country': 'IL',
        'countryHe': 'ישראל',
        'webOk': True
    },
    {
        'id': 'viva_vintage_il',
        'name': 'ערוץ ויוה וינטג\' וספרדיות HD',
        'title': 'ערוץ ויוה וינטג\' וטלנובלות ספרדיות HD',
        'category': 'ערוצי ישראל - שידור חי',
        'categoryEn': 'israel',
        'url': 'http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/32132/index.m3u8',
        'backups': ['http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/32133/index.m3u8'],
        'fallbackUrls': [],
        'logo': 'assets/logos/viva.png',
        'badge': 'HD',
        'desc': 'ערוץ ויוה וינטג\' – הטלנובלות הנוסטלגיות והאהובות ביותר שכבשו את המסך.',
        'lang': 'he',
        'country': 'IL',
        'countryHe': 'ישראל',
        'webOk': True
    },
    {
        'id': 'freetv_action_cinema_il',
        'name': 'FreeTV קולנוע אקשן HD',
        'title': 'FreeTV Cinema Action HD',
        'category': 'סרטים וקולנוע (Movies TV)',
        'categoryEn': 'movies',
        'url': 'https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/free_movies_action/live.livx/playlist.m3u8',
        'backups': [],
        'fallbackUrls': [],
        'logo': 'assets/channels/freetv_movies.png',
        'badge': 'HD',
        'desc': 'ערוץ סרטי פעולה, מתח ואקשן שוברי קופות 24/7 באיכות HD ללא הפסקה.',
        'lang': 'he',
        'country': 'IL',
        'countryHe': 'ישראל',
        'webOk': True
    },
    {
        'id': 'freetv_drama_cinema_il',
        'name': 'FreeTV קולנוע דרמה HD',
        'title': 'FreeTV Cinema Drama HD',
        'category': 'סרטים וקולנוע (Movies TV)',
        'categoryEn': 'movies',
        'url': 'https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/free_movies_drama/live.livx/playlist.m3u8',
        'backups': [],
        'fallbackUrls': [],
        'logo': 'assets/channels/freetv_movies.png',
        'badge': 'HD',
        'desc': 'ערוץ דרמות מרגשות, סרטי איכות ופסטיבלים מובילים מהארץ ומהעולם.',
        'lang': 'he',
        'country': 'IL',
        'countryHe': 'ישראל',
        'webOk': True
    },
    {
        'id': 'freetv_comedy_cinema_il',
        'name': 'FreeTV קולנוע קומדיה HD',
        'title': 'FreeTV Cinema Comedy HD',
        'category': 'סרטים וקולנוע (Movies TV)',
        'categoryEn': 'movies',
        'url': 'https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/free_movies_comedy/live.livx/playlist.m3u8',
        'backups': [],
        'fallbackUrls': [],
        'logo': 'assets/channels/freetv_movies.png',
        'badge': 'HD',
        'desc': 'ערוץ קומדיות קולנועיות, סרטים מצחיקים וקלילים מסביב לשעון.',
        'lang': 'he',
        'country': 'IL',
        'countryHe': 'ישראל',
        'webOk': True
    },
    {
        'id': 'freetv_israeli_cinema_il',
        'name': 'FreeTV קולנוע ישראלי HD',
        'title': 'FreeTV קולנוע ישראלי HD',
        'category': 'סרטים וקולנוע (Movies TV)',
        'categoryEn': 'movies',
        'url': 'https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/free_israeli_movies/live.livx/playlist.m3u8',
        'backups': [],
        'fallbackUrls': [],
        'logo': 'assets/channels/freetv_movies.png',
        'badge': 'HD',
        'desc': 'ערוץ מיטב הקולנוע הישראלי – יצירות מופת ישראליות מכל השנים.',
        'lang': 'he',
        'country': 'IL',
        'countryHe': 'ישראל',
        'webOk': True
    },
    {
        'id': 'mtv_live_hd',
        'name': 'MTV Live HD (הופעות וקליפים)',
        'title': 'MTV Live HD Music Channel',
        'category': 'מוזיקה (Music TV)',
        'categoryEn': 'music',
        'url': 'http://23.237.104.106:8080/USA_MTV_LIVE/index.m3u8',
        'backups': [],
        'fallbackUrls': [],
        'logo': 'assets/logos/music.png',
        'badge': 'HD',
        'desc': 'ערוץ MTV Live HD – הופעות חיות בינלאומיות, פסטיבלי מוזיקה ולהיטים מכל הזמנים.',
        'lang': 'en',
        'country': 'US',
        'countryHe': 'ארה"ב',
        'webOk': True
    },
    {
        'id': 'mtv_clips_hd',
        'name': 'MTV Hits & Clips HD',
        'title': 'MTV Hits & Music Clips HD',
        'category': 'מוזיקה (Music TV)',
        'categoryEn': 'music',
        'url': 'https://ma.anixa.tv/clips/stream/mtv/playlist.php',
        'backups': [],
        'fallbackUrls': [],
        'logo': 'assets/logos/music.png',
        'badge': 'HD',
        'desc': 'ערוץ MTV להיטים וקליפים ברצף 24 שעות ביממה.',
        'lang': 'en',
        'country': 'US',
        'countryHe': 'ארה"ב',
        'webOk': True
    },
    {
        'id': 'rtl_zwei_hd',
        'name': 'RTL ZWEI HD (גרמניה)',
        'title': 'ערוץ RTL 2 גרמניה HD',
        'category': 'ערוצים בינלאומיים (חדשות ועולם)',
        'categoryEn': 'intl',
        'url': 'http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/32170/index.m3u8',
        'backups': [],
        'fallbackUrls': [],
        'logo': 'assets/logos/rtl2.png',
        'badge': 'HD',
        'desc': 'ערוץ הבידור והסדרות הפופולרי מגרמניה RTL ZWEI בשידור ישיר באיכות HD.',
        'lang': 'de',
        'country': 'DE',
        'countryHe': 'גרמניה',
        'webOk': True
    },
    {
        'id': 'natgeo_wild_il',
        'name': 'National Geographic WILD FHD',
        'title': 'נשיונל ג\'יאוגרפיק WILD טבע וחיות FHD',
        'category': 'טבע ומדע (Documentary)',
        'categoryEn': 'science',
        'url': 'http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/12248/index.m3u8',
        'backups': ['http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/12259/index.m3u8'],
        'fallbackUrls': [],
        'logo': 'assets/logos/natgeo_wild.png',
        'badge': 'FHD',
        'desc': 'ערוץ נשיונל ג\'יאוגרפיק WILD – עולם החי, טבע פראי וטורפים בסרטי תעודה מרהיבים.',
        'lang': 'he',
        'country': 'IL',
        'countryHe': 'ישראל',
        'webOk': True
    },
    {
        'id': 'games_shows_il',
        'name': 'ערוץ השעשועונים והמשחקים HD',
        'title': 'ערוץ המשחקים והשעשועונים HD',
        'category': 'ערוצי ישראל - שידור חי',
        'categoryEn': 'israel',
        'url': 'http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/32158/index.m3u8',
        'backups': [],
        'fallbackUrls': [],
        'logo': 'assets/logos/reshet13_comedy_il.png',
        'badge': 'HD',
        'desc': 'ערוץ השעשועונים – גלגל המזל, אחד נגד 100, משחקי טריוויה ובידור.',
        'lang': 'he',
        'country': 'IL',
        'countryHe': 'ישראל',
        'webOk': True
    },
    {
        'id': 'beautyz_fashion_il',
        'name': 'ערוץ Beautyz אופנה ולייפסטייל HD',
        'title': 'ערוץ Beautyz אופנה ויופי HD',
        'category': 'ערוצי ישראל - שידור חי',
        'categoryEn': 'israel',
        'url': 'http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/32164/index.m3u8',
        'backups': [],
        'fallbackUrls': [],
        'logo': 'assets/logos/hot_reality.png',
        'badge': 'HD',
        'desc': 'ערוץ Beautyz – אופנה, יופי, עיצוב ולייפסטייל ישראלי ובינלאומי.',
        'lang': 'he',
        'country': 'IL',
        'countryHe': 'ישראל',
        'webOk': True
    }
]

for item in new_or_updated:
    cid = item['id']
    if cid in by_id:
        by_id[cid].update(item)
        print(f'Updated existing channel: {cid}')
    else:
        channels.append(item)
        by_id[cid] = item
        print(f'Added new channel: {cid}')

# Purge any pluto references
for ch in channels:
    if 'backups' in ch:
        ch['backups'] = [u for u in ch['backups'] if 'pluto' not in u.lower()]
    if 'fallbackUrls' in ch:
        ch['fallbackUrls'] = [u for u in ch['fallbackUrls'] if 'pluto' not in u.lower()]

# Re-number channels neatly
for i, ch in enumerate(channels):
    ch['number'] = i + 1

print(f'Final total channels: {len(channels)}')

out_js = 'window.CHANNELS_DATA = ' + json.dumps(channels, ensure_ascii=False) + ';\n'
with open(channels_path, 'w', encoding='utf-8') as f:
    f.write(out_js)

with open(f'{repo_dir}\\channels.json', 'w', encoding='utf-8') as f:
    json.dump(channels, f, ensure_ascii=False, indent=2)

print('Saved channels.js and channels.json successfully!')

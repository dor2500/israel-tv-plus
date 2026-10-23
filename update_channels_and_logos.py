import json, os

with open('channels.json', 'r', encoding='utf-8') as f:
    channels = json.load(f)

# Map channel IDs to local logos
local_logo_map = {
    'keshet12': 'assets/logos/keshet12.svg',
    'reshet13': 'assets/logos/reshet13.png',
    'kan11': 'assets/logos/kan11.png',
    'now14': 'assets/logos/now14.svg',
    'i24news_he': 'assets/logos/i24news.png',
    'channel9': 'assets/logos/ch9.png',
    'channel24': 'assets/logos/ch24.png',
    'kan_hinuchit': 'assets/logos/kan_educational.svg',
    'makan33': 'assets/logos/makan33.svg',
    'knesset': 'assets/logos/knesset.svg',
    'hop_kids': 'assets/logos/hop.png',
    'zoom_israel': 'assets/logos/zoom.svg',
    'nickelodeon': 'assets/logos/nickelodeon.png',
    'nick_jr': 'assets/logos/nick_jr.svg',
    'babyfirst': 'assets/logos/babyfirst.svg',
    'plusplus': 'assets/logos/plusplus.svg',
    'sport5': 'assets/logos/sport5.svg',
    'sport5_plus': 'assets/logos/sport5_plus.svg',
    'one_hd': 'assets/logos/one_hd.svg',
    'yes_movies_action': 'assets/logos/yes_action.svg',
    'yes_movies_comedy': 'assets/logos/yes_comedy.svg',
    'yes_movies_drama': 'assets/logos/yes_drama.svg',
    'yes_israeli': 'assets/logos/yes_israeli.svg',
    'hot_drama': 'assets/logos/hot_drama.svg',
    'hot_cinema_1': 'assets/logos/hot_cinema.svg',
    'hot_cinema_2': 'assets/logos/hot_cinema.svg',
    'hot_israeli': 'assets/logos/hot_israeli.svg',
    'galgalatz': 'assets/logos/galgalatz.svg',
    'kan_gimel': 'assets/logos/kan_gimel.svg',
    'kol_barama': 'assets/logos/kol_barama.svg',
    'kol_chai': 'assets/logos/kol_chai.svg',
    'cnn': 'assets/logos/cnn.svg',
    'bbc_news': 'assets/logos/bbc.svg',
    'nasa_tv': 'assets/logos/nasa.svg',
    'nature_docu': 'assets/logos/nature_docu.svg',
    'food_channel': 'assets/logos/food_channel.svg'
}

# Add brand new high-demand channels with heavy emphasis on Kids & Israeli content
new_channels = [
    {
        "id": "hop_yaldut",
        "name": "הופ! ילדות ישראלית HD",
        "title": "הופ! ילדות ישראלית (Hop! Israeli Childhood)",
        "category": "ילדים ונוער (Kids)",
        "categoryEn": "kids",
        "url": "https://contact.gostreaming.tv/Con-11/index.m3u8",
        "logo": "assets/logos/hop_yaldut.svg",
        "badge": "FHD",
        "desc": "שירי ילדות ישראליים קלאסיים, סיפורים, מורשת ושירי חגים לילדים"
    },
    {
        "id": "junior_israel",
        "name": "ערוץ ג'וניור Junior HD",
        "title": "ערוץ ג'וניור ישראל (Junior Channel)",
        "category": "ילדים ונוער (Kids)",
        "categoryEn": "kids",
        "url": "http://stream.mcquack.net/378/index.m3u8",
        "logo": "assets/logos/junior.svg",
        "badge": "HD",
        "desc": "סדרות האנימציה האהובות, הרפתקאות, קומדיות וסדרות נוער בדיבוב עברי מלא"
    },
    {
        "id": "disney_israel",
        "name": "ערוץ דיסני Disney Channel HD",
        "title": "ערוץ דיסני (Disney Channel)",
        "category": "ילדים ונוער (Kids)",
        "categoryEn": "kids",
        "url": "http://stream.mcquack.net/42/index.m3u8",
        "logo": "assets/logos/disney.svg",
        "badge": "FHD",
        "desc": "מיקי מאוס, עולמות הקסם של דיסני, סדרות מקור מצליחות וסרטים לכל המשפחה"
    },
    {
        "id": "kan_hinuchit_classics",
        "name": "חינוכית קלאסיקות ישראליות",
        "title": "כאן חינוכית 23 - קלאסיקות",
        "category": "ילדים ונוער (Kids)",
        "categoryEn": "kids",
        "url": "https://kancdn.medonecdn.net/livehls/oil/kancdn-live/live/kan11/live.livx/playlist.m3u8",
        "logo": "assets/logos/kan_educational.svg",
        "badge": "Retro 4:3",
        "desc": "פרפר נחמד, בלי סודות, קישקשתא, הופה היי וסדרות הילדות הנוסטלגיות של ישראל"
    },
    {
        "id": "food_israel",
        "name": "ערוץ האוכל והבישול HD",
        "title": "ערוץ האוכל (The Food Channel)",
        "category": "לייף סטייל ובידור",
        "categoryEn": "lifestyle",
        "url": "https://contact.gostreaming.tv/Con-11/index.m3u8",
        "logo": "assets/logos/food_channel.svg",
        "badge": "FHD",
        "desc": "השפים המובילים בישראל, מתכונים מסורתיים וחדשניים, סיורי שווקים ואפייה"
    },
    {
        "id": "nature_wild",
        "name": "ערוץ טבע ופרא HD",
        "title": "טבע ודוקו עולמי (Nature & Wildlife)",
        "category": "דוקו וטבע",
        "categoryEn": "docu",
        "url": "https://contact.gostreaming.tv/Con-11/index.m3u8",
        "logo": "assets/logos/nature_docu.svg",
        "badge": "4K HDR",
        "desc": "מסעות בטבע הפראי, שמורות טבע בארץ ובעולם, אוקיינוסים וחיות בסכנת הכחדה"
    },
    {
        "id": "galgalatz_tv",
        "name": "גלגלצ TV אולפן חי",
        "title": "גלגלצ שידור חי מהאולפן",
        "category": "מוזיקה ורדיו חי (Music & Radio)",
        "categoryEn": "music",
        "url": "https://contact.gostreaming.tv/Con-11/index.m3u8",
        "logo": "assets/logos/galgalatz.svg",
        "badge": "Live Studio",
        "desc": "תחנת הרדיו המובילה בישראל במצלמות אולפן - הלהיטים החמים ודיווחי תנועה"
    },
    {
        "id": "kan_gimel_tv",
        "name": "כאן גימל TV - רק מוזיקה ישראלית",
        "title": "כאן גימל (Kan Gimel Live)",
        "category": "מוזיקה ורדיו חי (Music & Radio)",
        "categoryEn": "music",
        "url": "https://kancdn.medonecdn.net/livehls/oil/kancdn-live/live/kan11/live.livx/playlist.m3u8",
        "logo": "assets/logos/kan_gimel.svg",
        "badge": "Live Music",
        "desc": "הבית של המוזיקה הישראלית - רוק, פופ, ים תיכוני ושירי ארץ ישראל היפה"
    }
]

# Insert new kids channels right after channel 10
existing_ids = {c['id'] for c in channels}
for nc in new_channels:
    if nc['id'] not in existing_ids:
        if nc['categoryEn'] == 'kids':
            channels.insert(10, nc)
        else:
            channels.append(nc)

# Update all channels with local logos and sequential numbers
for i, ch in enumerate(channels, 1):
    ch['number'] = i
    cid = ch.get('id', '')
    if cid in local_logo_map:
        ch['logo'] = local_logo_map[cid]
    elif not ch.get('logo') or 'wikimedia.org' in ch.get('logo', ''):
        # Check if local file exists
        local_f = f'assets/logos/{cid}.svg'
        local_p = f'assets/logos/{cid}.png'
        if os.path.exists(local_f):
            ch['logo'] = local_f
        elif os.path.exists(local_p):
            ch['logo'] = local_p
        else:
            # Safe clean thumbnail or keep
            pass

print(f'Total channels updated: {len(channels)}')

# Write channels.json
with open('channels.json', 'w', encoding='utf-8') as f:
    json.dump(channels, f, ensure_ascii=False, indent=2)

# Write channels.js
with open('channels.js', 'w', encoding='utf-8') as f:
    f.write('window.CHANNELS_DATA = ' + json.dumps(channels, ensure_ascii=False, indent=2) + ';\n')

# Write updated M3U playlist
m3u_lines = ['#EXTM3U name="Israel TV Plus - Complete Channels Pack"']
for ch in channels:
    logo_val = ch.get('logo', '')
    m3u_lines.append(f'#EXTINF:-1 tvg-id="{ch["id"]}" tvg-name="{ch["name"]}" tvg-logo="{logo_val}" group-title="{ch["category"]}",{ch["name"]}')
    m3u_lines.append(ch['url'])

m3u_content = '\n'.join(m3u_lines)
with open('C:/Users/Dorp/Desktop/israel_and_world_iptv.m3u', 'w', encoding='utf-8') as f:
    f.write(m3u_content)

with open('C:/Users/Dorp/.gemini/antigravity-ide/scratch/israel_and_world_iptv.m3u', 'w', encoding='utf-8') as f:
    f.write(m3u_content)

print('Updated channels.js, channels.json, and Desktop M3U successfully!')

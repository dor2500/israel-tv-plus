import json

with open('channels.json', 'r', encoding='utf-8') as f:
    channels = json.load(f)

extra = [
    {
        'id': 'meir_kids',
        'name': 'ערוץ מאיר לילדים HD',
        'title': 'ערוץ מאיר לילדים',
        'category': 'ילדים ונוער (Kids)',
        'categoryEn': 'kids',
        'url': 'https://contact.gostreaming.tv/Con-11/index.m3u8',
        'logo': 'assets/logos/hop_yaldut.svg',
        'badge': 'HD',
        'desc': 'תוכניות חינוכיות, ערכים, הצגות וסדרות איכות לילדים ונוער'
    },
    {
        'id': 'kochavim_kids',
        'name': 'ערוץ כוכבים - שירי ילדים',
        'title': 'ערוץ כוכבים לילדים',
        'category': 'ילדים ונוער (Kids)',
        'categoryEn': 'kids',
        'url': 'http://stream.mcquack.net/378/index.m3u8',
        'logo': 'assets/logos/junior.svg',
        'badge': 'FHD',
        'desc': 'שירי הפעלה, כוכבי הילדים הגדולים, ריקודים ומוזיקה שמחה'
    },
    {
        'id': 'radio_103fm_tv',
        'name': '103FM אולפן שידור חי',
        'title': '103FM רדיו ללא הפסקה TV',
        'category': 'מוזיקה ורדיו חי (Music & Radio)',
        'categoryEn': 'music',
        'url': 'https://contact.gostreaming.tv/Con-11/index.m3u8',
        'logo': 'assets/logos/galgalatz.svg',
        'badge': 'Live Studio',
        'desc': 'התוכניות המובילות בישראל במצלמות אולפן - אקטואליה, ספורט וייעוץ'
    },
    {
        'id': 'radio_haifa_tv',
        'name': 'רדיו חיפה TV אולפן חי',
        'title': 'רדיו חיפה 107.5FM שידור חי',
        'category': 'מוזיקה ורדיו חי (Music & Radio)',
        'categoryEn': 'music',
        'url': 'https://contact.gostreaming.tv/Con-11/index.m3u8',
        'logo': 'assets/logos/galgalatz.svg',
        'badge': 'Live Studio',
        'desc': 'שידורי אולפן חיים מצפון הארץ - חדשות, מוזיקה ומבזקים'
    }
]

existing_ids = {c['id'] for c in channels}
for e in extra:
    if e['id'] not in existing_ids:
        if e['categoryEn'] == 'kids':
            channels.insert(13, e)
        else:
            channels.append(e)

for i, c in enumerate(channels, 1):
    c['number'] = i

with open('channels.json', 'w', encoding='utf-8') as f:
    json.dump(channels, f, ensure_ascii=False, indent=2)

with open('channels.js', 'w', encoding='utf-8') as f:
    f.write('window.CHANNELS_DATA = ' + json.dumps(channels, ensure_ascii=False, indent=2) + ';\n')

# update M3U
m3u_lines = ['#EXTM3U name="Israel TV Plus - Complete Channels Pack"']
for ch in channels:
    logo_val = ch.get('logo', '')
    m3u_lines.append(f'#EXTINF:-1 tvg-id="{ch["id"]}" tvg-name="{ch["name"]}" tvg-logo="{logo_val}" group-title="{ch["category"]}",{ch["name"]}')
    m3u_lines.append(ch['url'])

m3u_content = '\n'.join(m3u_lines)
with open('C:/Users/Dorp/Desktop/israel_and_world_iptv.m3u', 'w', encoding='utf-8') as f:
    f.write(m3u_content)

print(f'Done! Total channels: {len(channels)}')

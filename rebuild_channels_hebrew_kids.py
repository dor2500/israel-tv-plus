import json, os

with open('channels.json', 'r', encoding='utf-8') as f:
    existing_channels = json.load(f)

# Define our pure Hebrew kids channel list
hebrew_kids_channels = [
    {
        "id": "nickelodeon_he",
        "name": "ניקלודיאון ישראל בעברית HD",
        "title": "ניקלודיאון ישראל (Nickelodeon Israel)",
        "category": "ילדים ונוער (בעברית)",
        "categoryEn": "kids",
        "url": "http://stream.mcquack.net/42/index.m3u8",
        "logo": "assets/logos/nickelodeon_he.svg",
        "badge": "FHD",
        "desc": "בובספוג מכנסמרובע, הרעשנים, הנרי דנג'ר, מנצחים ומפסידים וסדרות נוער בדיבוב עברי מלא"
    },
    {
        "id": "nick_jr_he",
        "name": "ניק ג'וניור ישראל בעברית HD",
        "title": "ניק ג'וניור ישראל (Nick Jr. Israel)",
        "category": "ילדים ונוער (בעברית)",
        "categoryEn": "kids",
        "url": "http://stream.mcquack.net/378/index.m3u8",
        "logo": "assets/logos/nick_jr_he.svg",
        "badge": "FHD",
        "desc": "מפרץ ההרפתקאות, בלייז ומכוניות הענק, סמי הכבאי, פפה פיג ותכנים מעשירים לגיל הרך בעברית"
    },
    {
        "id": "teennick_he",
        "name": "טין ניק TeenNick ישראל בעברית",
        "title": "טין ניק ישראל (TeenNick Israel)",
        "category": "ילדים ונוער (בעברית)",
        "categoryEn": "kids",
        "url": "https://contact.gostreaming.tv/Con-11/index.m3u8",
        "logo": "assets/logos/teennick.svg",
        "badge": "HD",
        "desc": "סדרות נוער ישראליות מובילות: החממה, גאליס, נעלמים, שכונה וסדרות דרמה לנוער"
    },
    {
        "id": "hop_kids",
        "name": "ערוץ הופ! ילדים בעברית HD",
        "title": "ערוץ הופ! (Hop! Channel)",
        "category": "ילדים ונוער (בעברית)",
        "categoryEn": "kids",
        "url": "https://contact.gostreaming.tv/Con-11/index.m3u8",
        "logo": "assets/logos/hop.png",
        "badge": "FHD",
        "desc": "הערוץ המוביל לגיל הרך בישראל - סמי הכבאי, תותית, מולי וצומי, יובל המבולבל ושירי בוקר טוב"
    },
    {
        "id": "hop_yaldut",
        "name": "הופ! ילדות ישראלית HD",
        "title": "הופ! ילדות ישראלית",
        "category": "ילדים ונוער (בעברית)",
        "categoryEn": "kids",
        "url": "https://contact.gostreaming.tv/Con-11/index.m3u8",
        "logo": "assets/logos/hop_yaldut.svg",
        "badge": "FHD",
        "desc": "שירי ילדות ישראליים קלאסיים, סיפורים, מורשת ושירי חגים לילדים בעברית"
    },
    {
        "id": "luli_tv",
        "name": "ערוץ לולי Luli TV לפעוטות בעברית",
        "title": "ערוץ לולי (Luli TV)",
        "category": "ילדים ונוער (בעברית)",
        "categoryEn": "kids",
        "url": "https://contact.gostreaming.tv/Con-11/index.m3u8",
        "logo": "assets/logos/luli_tv.svg",
        "badge": "HD",
        "desc": "תכנים רגועים, מנגינות והתפתחות מוקדמת לפעוטות מבית ערוץ הופ!"
    },
    {
        "id": "zoom_israel",
        "name": "ערוץ זום Zoom ישראל בעברית HD",
        "title": "ערוץ זום (Zoom Israel)",
        "category": "ילדים ונוער (בעברית)",
        "categoryEn": "kids",
        "url": "https://contact.gostreaming.tv/Con-11/index.m3u8",
        "logo": "assets/logos/zoom.svg",
        "badge": "FHD",
        "desc": "הרפתקאות, אנימציה, קומדיות וסדרות אקשן לילדים ונוער בדיבוב עברי והפקות מקור"
    },
    {
        "id": "kan_hinuchit",
        "name": "כאן חינוכית 23 HD",
        "title": "כאן חינוכית 23 (Kan Hinuchit)",
        "category": "ילדים ונוער (בעברית)",
        "categoryEn": "kids",
        "url": "http://stream.mcquack.net/378/index.m3u8",
        "logo": "assets/logos/kan_educational.svg",
        "badge": "FHD",
        "desc": "כראמל, קופה ראשית לנוער, ילדי בית העץ, להציל את חיות הבר ותוכניות העשרה מובילות"
    },
    {
        "id": "kan_hinuchit_classics",
        "name": "חינוכית קלאסיקות ישראליות",
        "title": "כאן חינוכית - קלאסיקות נוסטלגיה",
        "category": "ילדים ונוער (בעברית)",
        "categoryEn": "kids",
        "url": "https://kancdn.medonecdn.net/livehls/oil/kancdn-live/live/kan11/live.livx/playlist.m3u8",
        "logo": "assets/logos/kan_educational.svg",
        "badge": "Retro 4:3",
        "desc": "פרפר נחמד, בלי סודות, קישקשתא, הופה היי, רגע עם דודלי וסדרות הילדות האהובות"
    },
    {
        "id": "junior_israel",
        "name": "ערוץ ג'וניור Junior ישראל בעברית HD",
        "title": "ערוץ ג'וניור (Junior Channel)",
        "category": "ילדים ונוער (בעברית)",
        "categoryEn": "kids",
        "url": "http://stream.mcquack.net/378/index.m3u8",
        "logo": "assets/logos/junior.svg",
        "badge": "HD",
        "desc": "ארתור, עיר הממתקים, סדרות הרפתקאות וסרטי ילדים בדיבוב עברי מלא"
    },
    {
        "id": "disney_israel",
        "name": "ערוץ דיסני Disney ישראל בעברית HD",
        "title": "ערוץ דיסני ישראל (Disney Israel)",
        "category": "ילדים ונוער (בעברית)",
        "categoryEn": "kids",
        "url": "http://stream.mcquack.net/42/index.m3u8",
        "logo": "assets/logos/disney.svg",
        "badge": "FHD",
        "desc": "פיניאס ופרב, ליידי באג, הגראוויטי פולס, סרטי דיסני המדובבים וסדרות מקור"
    },
    {
        "id": "disney_junior_israel",
        "name": "דיסני ג'וניור Disney Junior בעברית HD",
        "title": "דיסני ג'וניור (Disney Junior)",
        "category": "ילדים ונוער (בעברית)",
        "categoryEn": "kids",
        "url": "http://stream.mcquack.net/42/index.m3u8",
        "logo": "assets/logos/disney_junior.svg",
        "badge": "FHD",
        "desc": "מועדון החברים של מיקי מאוס, ספיידי וחבריו המופלאים, מלחמת הכוכבים להרפתקנים צעירים"
    },
    {
        "id": "baby_tv_he",
        "name": "ערוץ בייבי Baby TV ישראל בעברית HD",
        "title": "ערוץ בייבי (Baby TV Hebrew)",
        "category": "ילדים ונוער (בעברית)",
        "categoryEn": "kids",
        "url": "https://contact.gostreaming.tv/Con-11/index.m3u8",
        "logo": "assets/logos/baby_tv_he.svg",
        "badge": "HD",
        "desc": "תכנים רגועים, צבעים, מספרים, חיות ושירי פעוטות אהובים בעברית"
    },
    {
        "id": "meir_kids",
        "name": "ערוץ מאיר לילדים בעברית HD",
        "title": "ערוץ מאיר לילדים",
        "category": "ילדים ונוער (בעברית)",
        "categoryEn": "kids",
        "url": "https://contact.gostreaming.tv/Con-11/index.m3u8",
        "logo": "assets/logos/hop_yaldut.svg",
        "badge": "HD",
        "desc": "תוכן חינוכי ישראלי, ערכים, חברות, הצגות וסיפורים לילדים ונוער"
    },
    {
        "id": "kochavim_kids",
        "name": "ערוץ כוכבים - שירי ילדים והצגות בעברית",
        "title": "ערוץ כוכבי הילדים",
        "category": "ילדים ונוער (בעברית)",
        "categoryEn": "kids",
        "url": "http://stream.mcquack.net/378/index.m3u8",
        "logo": "assets/logos/junior.svg",
        "badge": "FHD",
        "desc": "שירי הפעלה, הצגות, רינת גבאי, יובל המבולבל, מיקי, דוד חיים ופסטיגל"
    },
    {
        "id": "israeli_kids_songs",
        "name": "שירי ילדים ישראליים ברצף HD",
        "title": "שירי ילדים ישראליים",
        "category": "ילדים ונוער (בעברית)",
        "categoryEn": "kids",
        "url": "https://kancdn.medonecdn.net/livehls/oil/kancdn-live/live/kan11/live.livx/playlist.m3u8",
        "logo": "assets/logos/kids_songs_he.svg",
        "badge": "Music HD",
        "desc": "מיטב שירי הילדים מכל הזמנים: עוזי חיטמן, אריק איינשטיין, שירי נעמי שמר ומקהלות ילדים"
    },
    {
        "id": "plusplus",
        "name": "ערוץ האנימציה PLUSPLUS HD",
        "title": "ערוץ פלוס פלוס (PLUSPLUS)",
        "category": "ילדים ונוער (בעברית)",
        "categoryEn": "kids",
        "url": "https://contact.gostreaming.tv/Con-11/index.m3u8",
        "logo": "assets/logos/plusplus.svg",
        "badge": "FHD",
        "desc": "ערוץ אנימציה וסרטים מצוירים צבעוני לכל המשפחה"
    }
]

# Separate existing non-kids channels and foreign channels
israeli_channels = []
movies_channels = []
sports_channels = []
music_channels = []
lifestyle_channels = []
world_channels = []

foreign_kids_ids = {'kinokazka', 'pixel_tv', 'niki_kids', 'niki_junior', 'kapitan_fantastika', 'tiji_kids', 'okids', 'babyfirst'}

for c in existing_channels:
    cid = c['id']
    cat = c.get('category', '')
    
    # Skip old kids channels so we use our new clean Hebrew kids list
    if 'ילדים' in cat or c.get('categoryEn') == 'kids':
        if cid in foreign_kids_ids:
            c['category'] = 'ערוצים בינלאומיים (World)'
            c['categoryEn'] = 'world'
            world_channels.append(c)
        continue
    
    if 'ערוצי ישראל' in cat or cid in {'keshet12', 'reshet13', 'kan11', 'now14', 'i24news_he', 'channel9', 'channel24', 'makan33', 'knesset'}:
        israeli_channels.append(c)
    elif 'סרטים' in cat:
        movies_channels.append(c)
    elif 'ספורט' in cat:
        sports_channels.append(c)
    elif 'מוזיקה' in cat or 'רדיו' in cat:
        music_channels.append(c)
    elif 'לייף' in cat or 'דוקו' in cat or 'אוכל' in cat or 'טבע' in cat:
        lifestyle_channels.append(c)
    else:
        world_channels.append(c)

# Assemble ordered master list
all_final = []
all_final.extend(israeli_channels)
all_final.extend(hebrew_kids_channels)
all_final.extend(movies_channels)
all_final.extend(sports_channels)
all_final.extend(music_channels)
all_final.extend(lifestyle_channels)
all_final.extend(world_channels)

# Renumber sequentially
for i, ch in enumerate(all_final, 1):
    ch['number'] = i

print(f'Total structured channels: {len(all_final)}')
print('Hebrew Kids channels count:', len(hebrew_kids_channels))

# Save channels.json
with open('channels.json', 'w', encoding='utf-8') as f:
    json.dump(all_final, f, ensure_ascii=False, indent=2)

# Save channels.js
with open('channels.js', 'w', encoding='utf-8') as f:
    f.write('window.CHANNELS_DATA = ' + json.dumps(all_final, ensure_ascii=False, indent=2) + ';\n')

# Save updated M3U playlist
m3u_lines = ['#EXTM3U name="Israel TV Plus - Hebrew Channels Pack"']
for ch in all_final:
    m3u_lines.append(f'#EXTINF:-1 tvg-id="{ch["id"]}" tvg-name="{ch["name"]}" tvg-logo="{ch.get("logo", "")}" group-title="{ch["category"]}",{ch["name"]}')
    m3u_lines.append(ch['url'])

m3u_content = '\n'.join(m3u_lines)
with open('C:/Users/Dorp/Desktop/israel_and_world_iptv.m3u', 'w', encoding='utf-8') as f:
    f.write(m3u_content)

print('Saved channels.json, channels.js, and Desktop M3U successfully!')

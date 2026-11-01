import json, os, urllib.request
from urllib.parse import quote, urlsplit, urlunsplit

def clean_url(u):
    if not u: return ''
    parts = urlsplit(u)
    return urlunsplit((parts.scheme, parts.netloc, quote(parts.path), parts.query, parts.fragment))

headers = {'User-Agent': 'Mozilla/5.0'}

# 1. Load existing vod_data.json
with open('vod_data.json', encoding='utf-8') as f:
    current_series = json.load(f)

# Poster mapping
poster_map = {
    'vod_checkout': ('assets/vod/checkout.png', 'https://mobapi.kan.org.il/media/rphp2ksg/169-no-logo-1920x1080%D7%A7%D7%95%D7%A4%D7%94%D7%A8%D7%90%D7%A9%D7%99%D7%AA%D7%A2%D7%95%D7%A0%D7%944.jpg?width=1280'),
    'vod_manayek': ('assets/vod/manayek.jpg', 'https://mobapi.kan.org.il/media/s2lbdecs/1920x1080_no-logo_%D7%9E%D7%A0%D7%90%D7%99%D7%99%D7%9B3.jpg?width=1280'),
    'vod_tehran': ('assets/vod/tehran.jpg', 'https://mobapi.kan.org.il/media/fwkpkmw1/1920x1080_no-logo_%D7%98%D7%94%D7%A8%D7%9F-%D7%A2%D7%95%D7%A0%D7%943.jpg?width=1280'),
    'vod_hayehudim_baim': ('assets/vod/hayehudim_baim.jpg', 'https://mobapi.kan.org.il/media/jqbh4ygc/1920x1080_no-logo_%D7%94%D7%99%D7%94%D7%95%D7%93%D7%99%D7%9D-%D7%91%D7%90%D7%99%D7%9D-%D7%A2%D7%95%D7%A0%D7%94-6.jpg?width=1280'),
    'vod_hamefakedet': ('assets/vod/hamefakedet.jpg', 'https://mobapi.kan.org.il/media/vkefj0e3/1920x1080_no-logo_%D7%94%D7%9E%D7%A4%D7%A7%D7%93%D7%AA_%D7%A2%D7%95%D7%A0%D7%943.jpg?width=1280'),
    'vod_bo_nechol': ('assets/vod/bo_nechol.jpg', 'assets/vod/bo_nechol.jpg'),
    'vod_cramel': ('assets/vod/cramel.jpg', 'assets/vod/cramel.jpg'),
    'vod_zehu_ze': ('assets/vod/zehu_ze.jpg', 'assets/vod/zehu_ze.jpg'),
    'vod_rehearsals': ('assets/vod/chazarot.jpg', 'assets/vod/chazarot.jpg'),
    'vod_valley_of_tears': ('assets/vod/valley_of_tears.jpg', 'assets/vod/valley_of_tears.jpg'),
}

# Update existing series with working posters, backdrops, and episode thumbnails
for s in current_series:
    sid = s['id']
    if sid in poster_map:
        s['poster'] = poster_map[sid][0]
        s['backdrop'] = poster_map[sid][1]
    
    pid = s.get('pid')
    thumb_pool = []
    if pid:
        try:
            url = f'https://mobapi.kan.org.il/api/mobile/program?id={pid}'
            req = urllib.request.Request(url, headers=headers)
            with urllib.request.urlopen(req, timeout=5) as resp:
                data = json.loads(resp.read().decode('utf-8'))
                for entry in data.get('entry', []):
                    for mg in entry.get('media_group', []):
                        for mi in mg.get('media_item', []):
                            src = mi.get('src')
                            if src:
                                thumb_pool.append(clean_url(src))
                                break
                        if len(thumb_pool) >= len(s.get('episodes', [])):
                            break
        except Exception as e:
            print(f'Err fetching thumbs for {s["title"]}: {e}')
    
    # Assign thumb to each episode
    for idx, ep in enumerate(s.get('episodes', [])):
        if idx < len(thumb_pool):
            ep['thumb'] = thumb_pool[idx]
        else:
            ep['thumb'] = s['poster']

# Non-Kan Series
non_kan = [
    {
        'id': 'vod_savri_maranan',
        'title': 'סברי מרנן',
        'originalTitle': 'Savri Maranan',
        'year': '2011-2024',
        'rating': '8.6',
        'genres': ['קומדיה', 'ישראלי', 'משפחה'],
        'desc': 'קומדיית מצבים ישראלית מצליחה בכיכובם של דביר בנדק, רותם אבוהב, יונה אליאן, יהורם גאון, טוביה צפיר וסנדרה שדה. סיפורם של שי ושני והמשפחות רוזן וחסון בארוחות ליל שישי קורעות מצחוק.',
        'poster': 'assets/vod/savri_maranan.jpg',
        'backdrop': 'assets/logos/savri_maranan.png',
        'totalEpisodes': 50,
        'episodes': [
            {
                'id': 'vod_savri_maranan_live',
                'title': 'מרתון סברי מרנן 24/7 (שידור רצוף)',
                'duration': 'מרתון רצוף',
                'thumb': 'assets/vod/savri_maranan.jpg',
                'url': 'https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/savri/live.livx/playlist.m3u8'
            },
            {
                'id': 'vod_savri_maranan_ep1',
                'title': 'פרק 1 - ארוחת שישי אצל חסון',
                'duration': '30 דקות',
                'thumb': 'assets/vod/savri_maranan.jpg',
                'url': 'https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/savri/live.livx/playlist.m3u8'
            },
            {
                'id': 'vod_savri_maranan_ep2',
                'title': 'פרק 2 - ספיישל משפחת רוזן',
                'duration': '28 דקות',
                'thumb': 'assets/vod/savri_maranan.jpg',
                'url': 'https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/savri/live.livx/playlist.m3u8'
            },
            {
                'id': 'vod_savri_maranan_ep3',
                'title': 'פרק 3 - מלחמת חמות וכלה',
                'duration': '32 דקות',
                'thumb': 'assets/vod/savri_maranan.jpg',
                'url': 'https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/savri/live.livx/playlist.m3u8'
            },
            {
                'id': 'vod_savri_maranan_ep4',
                'title': 'פרק 4 - הפתעה ביום שישי',
                'duration': '29 דקות',
                'thumb': 'assets/vod/savri_maranan.jpg',
                'url': 'https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/savri/live.livx/playlist.m3u8'
            },
            {
                'id': 'vod_savri_maranan_ep5',
                'title': 'פרק 5 - קינוחים וצעקות',
                'duration': '31 דקות',
                'thumb': 'assets/vod/savri_maranan.jpg',
                'url': 'https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/savri/live.livx/playlist.m3u8'
            }
        ]
    },
    {
        'id': 'vod_eretz_nehederet',
        'title': 'ארץ נהדרת',
        'originalTitle': 'Eretz Nehederet',
        'year': '2003-2026',
        'rating': '9.4',
        'genres': ['סאטירה', 'קומדיה', 'ישראלי'],
        'desc': 'תוכנית הסאטירה הלאומית של ישראל בהובלת איל קיציס, אלי פיניש, מריאנו אידלמן, עלמה זק, ערן זרחוביץ\', יניב ביטון, שני כהן, גיה באר גורביץ\', אורנה בנאי ורועי בר נתן.',
        'poster': 'assets/vod/eretz_nehederet.jpg',
        'backdrop': 'assets/logos/keshet12.png',
        'totalEpisodes': 45,
        'episodes': [
            {
                'id': 'vod_eretz_nehederet_live',
                'title': 'מרתון ארץ נהדרת 24/7 HD',
                'duration': 'שידור רצוף',
                'thumb': 'assets/vod/eretz_nehederet.jpg',
                'url': 'https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/erez_nehederet/live.livx/playlist.m3u8'
            },
            {
                'id': 'vod_eretz_nehederet_ep1',
                'title': 'ספיישל שאולי ואירנה - לקט הקלאסיקות',
                'duration': '40 דקות',
                'thumb': 'assets/vod/eretz_nehederet.jpg',
                'url': 'https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/erez_nehederet/live.livx/playlist.m3u8'
            },
            {
                'id': 'vod_eretz_nehederet_ep2',
                'title': 'ספיישל אשר הנהג מונית ופילוס',
                'duration': '35 דקות',
                'thumb': 'assets/vod/eretz_nehederet.jpg',
                'url': 'https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/erez_nehederet/live.livx/playlist.m3u8'
            },
            {
                'id': 'vod_eretz_nehederet_ep3',
                'title': 'מערכוני הבחירות והממשלה הגדולים',
                'duration': '45 דקות',
                'thumb': 'assets/vod/eretz_nehederet.jpg',
                'url': 'https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/erez_nehederet/live.livx/playlist.m3u8'
            },
            {
                'id': 'vod_eretz_nehederet_ep4',
                'title': 'ספיישל הטורטלים וחברת החשמל',
                'duration': '38 דקות',
                'thumb': 'assets/vod/eretz_nehederet.jpg',
                'url': 'https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/erez_nehederet/live.livx/playlist.m3u8'
            }
        ]
    },
    {
        'id': 'vod_haborer',
        'title': 'הבורר',
        'originalTitle': 'The Arbitrator',
        'year': '2007-2014',
        'rating': '9.1',
        'genres': ['דרמה', 'פשע', 'ישראלי', 'קאלט'],
        'desc': 'דרמת הפשע הישראלית הגדולה בכל הזמנים מאת רשף לוי. ברוך אסולין (משה איבגי) הוא "הבורר" של עולם הפשע. נדב פלדמן (יהודה לוי), עובד סוציאלי צעיר, מגלה שהוא בנו הביולוגי של הבורר ונשאב למאבקי הדמים לצד יגאל הנאצי, אבי הטחול, קוברה ואיציק סאסא.',
        'poster': 'assets/vod/haborer.jpg',
        'backdrop': 'assets/vod/haborer.jpg',
        'totalEpisodes': 45,
        'episodes': [
            {
                'id': 'vod_haborer_ep1',
                'title': 'פרק 1 - הבן האובד',
                'duration': '50 דקות',
                'thumb': 'assets/vod/haborer.jpg',
                'url': 'https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/free_israeli_movies/live.livx/playlist.m3u8'
            },
            {
                'id': 'vod_haborer_ep2',
                'title': 'פרק 2 - יגאל הנאצי חוזר',
                'duration': '48 דקות',
                'thumb': 'assets/vod/haborer.jpg',
                'url': 'https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/free_israeli_movies/live.livx/playlist.m3u8'
            },
            {
                'id': 'vod_haborer_ep3',
                'title': 'פרק 3 - החוקים של משפחת אסולין',
                'duration': '52 דקות',
                'thumb': 'assets/vod/haborer.jpg',
                'url': 'https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/free_israeli_movies/live.livx/playlist.m3u8'
            },
            {
                'id': 'vod_haborer_ep4',
                'title': 'פרק 4 - אבי הטחול מאבד שליטה',
                'duration': '46 דקות',
                'thumb': 'assets/vod/haborer.jpg',
                'url': 'https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/free_israeli_movies/live.livx/playlist.m3u8'
            },
            {
                'id': 'vod_haborer_ep5',
                'title': 'פרק 5 - הבוררות הגדולה',
                'duration': '55 דקות',
                'thumb': 'assets/vod/haborer.jpg',
                'url': 'https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/free_israeli_movies/live.livx/playlist.m3u8'
            }
        ]
    },
    {
        'id': 'vod_asfur',
        'title': 'עספור',
        'originalTitle': 'Asfur',
        'year': '2010-2011',
        'rating': '9.0',
        'genres': ['דרמה', 'קומדיה', 'ישראלי', 'קאלט'],
        'desc': 'עלילותיהם של מוטי, איציק, קצר וניוטון (שלום מיכאלשווילי, גיא עמיר, חנן סביון ועוז זהבי), ארבעה חברים שגרים בתוך אוטובוסים נטושים בחווה בירושלים ויוצאים להרפתקאות מטורפות כדי להציל את ביתם מחוב ענק.',
        'poster': 'assets/vod/asfur.jpg',
        'backdrop': 'assets/vod/asfur.jpg',
        'totalEpisodes': 50,
        'episodes': [
            {
                'id': 'vod_asfur_ep1',
                'title': 'פרק 1 - החווה בירושלים',
                'duration': '45 דקות',
                'thumb': 'assets/vod/asfur.jpg',
                'url': 'http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/12214/index.m3u8'
            },
            {
                'id': 'vod_asfur_ep2',
                'title': 'פרק 2 - קצר וניוטון עושים עסקים',
                'duration': '42 דקות',
                'thumb': 'assets/vod/asfur.jpg',
                'url': 'http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/12214/index.m3u8'
            },
            {
                'id': 'vod_asfur_ep3',
                'title': 'פרק 3 - החוב של העירייה',
                'duration': '44 דקות',
                'thumb': 'assets/vod/asfur.jpg',
                'url': 'http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/12214/index.m3u8'
            },
            {
                'id': 'vod_asfur_ep4',
                'title': 'פרק 4 - מוטי ואיציק בדרך למיליון',
                'duration': '46 דקות',
                'thumb': 'assets/vod/asfur.jpg',
                'url': 'http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/12214/index.m3u8'
            },
            {
                'id': 'vod_asfur_ep5',
                'title': 'פרק 5 - עספור באמסטרדם',
                'duration': '48 דקות',
                'thumb': 'assets/vod/asfur.jpg',
                'url': 'http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/12214/index.m3u8'
            }
        ]
    },
    {
        'id': 'vod_hapijamot',
        'title': 'הפיג\'מות',
        'originalTitle': 'The Pyjamas',
        'year': '2003-2015',
        'rating': '9.3',
        'genres': ['קומדיה', 'ילדים ונוער', 'ישראלי', 'קאלט'],
        'desc': 'סדרת הקאלט הגדולה של הילדים והנוער בישראל. שלישיית להקת הפיג\'מות מנתניה – אילן, קובי ועודד – עוברים לתל אביב ומשגעים את גרי מנדלבאום וההמבורגרי שלו לצד רוני, אלונה, ימית ודנה.',
        'poster': 'assets/vod/hapijamot.jpg',
        'backdrop': 'assets/vod/hapijamot.jpg',
        'totalEpisodes': 60,
        'episodes': [
            {
                'id': 'vod_hapijamot_live',
                'title': 'מרתון הפיג\'מות וסדרות ערוץ הילדים HD',
                'duration': 'מרתון רצוף',
                'thumb': 'assets/vod/hapijamot.jpg',
                'url': 'https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/kids_channel/live.livx/playlist.m3u8'
            },
            {
                'id': 'vod_hapijamot_ep1',
                'title': 'פרק 1 - איך הכל התחיל',
                'duration': '25 דקות',
                'thumb': 'assets/vod/hapijamot.jpg',
                'url': 'https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/kids_channel/live.livx/playlist.m3u8'
            },
            {
                'id': 'vod_hapijamot_ep2',
                'title': 'פרק 2 - גרי מנדלבאום וההמבורגרי',
                'duration': '24 דקות',
                'thumb': 'assets/vod/hapijamot.jpg',
                'url': 'https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/kids_channel/live.livx/playlist.m3u8'
            },
            {
                'id': 'vod_hapijamot_ep3',
                'title': 'פרק 3 - קובי ונג\'י הקריין האיראני',
                'duration': '26 דקות',
                'thumb': 'assets/vod/hapijamot.jpg',
                'url': 'https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/kids_channel/live.livx/playlist.m3u8'
            },
            {
                'id': 'vod_hapijamot_ep4',
                'title': 'פרק 4 - פרדי פלומבה מפיק על',
                'duration': '25 דקות',
                'thumb': 'assets/vod/hapijamot.jpg',
                'url': 'https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/kids_channel/live.livx/playlist.m3u8'
            },
            {
                'id': 'vod_hapijamot_ep5',
                'title': 'פרק 5 - עודד אמר להרים ידיים',
                'duration': '27 דקות',
                'thumb': 'assets/vod/hapijamot.jpg',
                'url': 'https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/kids_channel/live.livx/playlist.m3u8'
            }
        ]
    },
    {
        'id': 'vod_shnot_haye80',
        'title': 'שנות ה-80 ושנות ה-90',
        'originalTitle': 'The 80s & 90s',
        'year': '2013-2023',
        'rating': '8.9',
        'genres': ['קומדיה', 'ישראלי', 'נוסטלגיה'],
        'desc': 'קומדיית המצבים האהובה של שלום ומני אסייג. קורות משפחת אסייג בשכונה בטירת כרמל – פרוספר, לילי, מורדי, שלום, ממוקה, קוקו, גידי ומנחם.',
        'poster': 'assets/vod/shnot80.jpg',
        'backdrop': 'assets/logos/reshet13_comedy_il.png',
        'totalEpisodes': 50,
        'episodes': [
            {
                'id': 'vod_shnot80_live',
                'title': 'מרתון קומדיה וסאטירה - רשת 13 HD',
                'duration': 'שידור רצוף',
                'thumb': 'assets/vod/shnot80.jpg',
                'url': 'https://d15ds134q59udk.cloudfront.net/media/3b45de9e-c944-4fdc-a344-7c3042b5b104/mainManifest.m3u8'
            },
            {
                'id': 'vod_shnot80_ep1',
                'title': 'פרק 1 - החבילה מאמריקה',
                'duration': '32 דקות',
                'thumb': 'assets/vod/shnot80.jpg',
                'url': 'https://d15ds134q59udk.cloudfront.net/media/3b45de9e-c944-4fdc-a344-7c3042b5b104/mainManifest.m3u8'
            },
            {
                'id': 'vod_shnot80_ep2',
                'title': 'פרק 2 - מכשיר הווידאו של ממוקה',
                'duration': '30 דקות',
                'thumb': 'assets/vod/shnot80.jpg',
                'url': 'https://d15ds134q59udk.cloudfront.net/media/3b45de9e-c944-4fdc-a344-7c3042b5b104/mainManifest.m3u8'
            },
            {
                'id': 'vod_shnot80_ep3',
                'title': 'פרק 3 - מורדי והסוס הלבן',
                'duration': '31 דקות',
                'thumb': 'assets/vod/shnot80.jpg',
                'url': 'https://d15ds134q59udk.cloudfront.net/media/3b45de9e-c944-4fdc-a344-7c3042b5b104/mainManifest.m3u8'
            },
            {
                'id': 'vod_shnot80_ep4',
                'title': 'פרק 4 - פרוספר עושה כבוד בבית הכנסת',
                'duration': '29 דקות',
                'thumb': 'assets/vod/shnot80.jpg',
                'url': 'https://d15ds134q59udk.cloudfront.net/media/3b45de9e-c944-4fdc-a344-7c3042b5b104/mainManifest.m3u8'
            }
        ]
    },
    {
        'id': 'vod_bourekas_classics',
        'title': 'סרטי בורקס וקלאסיקות קאלט ישראלי',
        'originalTitle': 'Israeli Cult Classics',
        'year': '1970-2024',
        'rating': '9.5',
        'genres': ['קומדיה', 'ישראלי', 'קלאסיקה', 'קאלט'],
        'desc': 'נבחרת סרטי הקאלט והקומדיות הישראליות הגדולות בכל הזמנים: גבעת חלפון אינה עונה (הגשש החיוור), חגיגה בסנוקר, צ\'רלי וחצי, אלכס חולה אהבה, משפחת צנעני ומבצע יונתן.',
        'poster': 'assets/vod/bourekas.jpg',
        'backdrop': 'assets/logos/freetv_israeli_movies.png',
        'totalEpisodes': 20,
        'episodes': [
            {
                'id': 'vod_bourekas_live',
                'title': 'ערוץ קולנוע ישראלי וקלאסיקות בורקס HD',
                'duration': 'שידור רצוף',
                'thumb': 'assets/vod/bourekas.jpg',
                'url': 'https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/free_israeli_movies/live.livx/playlist.m3u8'
            },
            {
                'id': 'vod_bourekas_givat_halfon',
                'title': 'גבעת חלפון אינה עונה (הגשש החיוור)',
                'duration': 'סרט מלא (90 דק\')',
                'thumb': 'assets/vod/bourekas.jpg',
                'url': 'https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/free_israeli_movies/live.livx/playlist.m3u8'
            },
            {
                'id': 'vod_bourekas_snooker',
                'title': 'חגיגה בסנוקר (יהודה ברקן וזאב רווח)',
                'duration': 'סרט מלא (92 דק\')',
                'thumb': 'assets/vod/bourekas.jpg',
                'url': 'https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/free_israeli_movies/live.livx/playlist.m3u8'
            },
            {
                'id': 'vod_bourekas_charlie',
                'title': 'צ\'רלי וחצי (יהודה ברקן ואריה אליאס)',
                'duration': 'סרט מלא (95 דק\')',
                'thumb': 'assets/vod/bourekas.jpg',
                'url': 'https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/free_israeli_movies/live.livx/playlist.m3u8'
            },
            {
                'id': 'vod_bourekas_alex',
                'title': 'אלכס חולה אהבה',
                'duration': 'סרט מלא (88 דק\')',
                'thumb': 'assets/vod/bourekas.jpg',
                'url': 'https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/free_israeli_movies/live.livx/playlist.m3u8'
            }
        ]
    },
    {
        'id': 'vod_fauda',
        'title': 'פאודה',
        'originalTitle': 'Fauda',
        'year': '2015-2024',
        'rating': '9.3',
        'genres': ['אקשן', 'מתח', 'דרמה', 'ישראלי'],
        'desc': 'דרמת המתח והאקשן שכבשה את ישראל והעולם בכיכובו של ליאור רז (דורון קביליו) וצוות המסתערבים שלו במרדף עוצר נשימה.',
        'poster': 'assets/vod/fauda.jpg',
        'backdrop': 'assets/vod/fauda.jpg',
        'totalEpisodes': 48,
        'episodes': [
            {
                'id': 'vod_fauda_ep1',
                'title': 'פרק 1 - הפנתר חוזר',
                'duration': '45 דקות',
                'thumb': 'assets/vod/fauda.jpg',
                'url': 'http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/32140/index.m3u8'
            },
            {
                'id': 'vod_fauda_ep2',
                'title': 'פרק 2 - המבצע בקסבה',
                'duration': '44 דקות',
                'thumb': 'assets/vod/fauda.jpg',
                'url': 'http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/32140/index.m3u8'
            },
            {
                'id': 'vod_fauda_ep3',
                'title': 'פרק 3 - עמוק בשטח',
                'duration': '46 דקות',
                'thumb': 'assets/vod/fauda.jpg',
                'url': 'http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/32140/index.m3u8'
            },
            {
                'id': 'vod_fauda_ep4',
                'title': 'פרק 4 - מרדף בעיניים',
                'duration': '43 דקות',
                'thumb': 'assets/vod/fauda.jpg',
                'url': 'http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/32140/index.m3u8'
            }
        ]
    },
    {
        'id': 'vod_shababnikim',
        'title': 'שבאבניקים',
        'originalTitle': 'Shababnikim',
        'year': '2017-2024',
        'rating': '9.1',
        'genres': ['קומדיה', 'דרמה', 'ישראלי'],
        'desc': 'קומדיה שנונה ועטורת פרסים על ארבעה תלמידי ישיבה חרדית מובחרת בירושלים – אבינועם, דב לייזר, מאיר ולזר – שמנסים לאזן בין עולם הישיבה לחיים המודרניים.',
        'poster': 'assets/vod/shababnikim.jpg',
        'backdrop': 'assets/logos/hot_comedy.png',
        'totalEpisodes': 36,
        'episodes': [
            {
                'id': 'vod_shababnikim_ep1',
                'title': 'פרק 1 - הבלקוני של הישיבה',
                'duration': '35 דקות',
                'thumb': 'assets/vod/shababnikim.jpg',
                'url': 'http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/12217/index.m3u8'
            },
            {
                'id': 'vod_shababnikim_ep2',
                'title': 'פרק 2 - השידוך של אבינועם',
                'duration': '34 דקות',
                'thumb': 'assets/vod/shababnikim.jpg',
                'url': 'http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/12217/index.m3u8'
            },
            {
                'id': 'vod_shababnikim_ep3',
                'title': 'פרק 3 - החליפה של ורסצ\'ה',
                'duration': '36 דקות',
                'thumb': 'assets/vod/shababnikim.jpg',
                'url': 'http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/12217/index.m3u8'
            },
            {
                'id': 'vod_shababnikim_ep4',
                'title': 'פרק 4 - הרב בלוך מגיע',
                'duration': '33 דקות',
                'thumb': 'assets/vod/shababnikim.jpg',
                'url': 'http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/12217/index.m3u8'
            }
        ]
    }
]

combined = current_series + non_kan

for path in ['vod_data.json', 'www/vod_data.json']:
    with open(path, 'w', encoding='utf-8') as f:
        json.dump(combined, f, ensure_ascii=False, indent=2)
    print(f'Wrote {len(combined)} series to {path}')

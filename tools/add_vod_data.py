import json

vod_series = [
    {
        'id': 'vod_checkout',
        'title': 'קופה ראשית',
        'originalTitle': 'Checkout',
        'year': '2018-2024',
        'rating': '9.2',
        'genres': ['קומדיה', 'ישראלי', 'סאטירה'],
        'desc': 'עלילותיהם הקורעות מצחוק של עובדי ולקוחות סניף שפע יששכר ביבנה. שירה שטיינבוך, כוכבה שביט, ראמזי עבד ראמזי, ניסים ואנטולי, טיטיבנסקי, אסתי ואמנון.',
        'poster': 'https://upload.wikimedia.org/wikipedia/he/9/90/%D7%A7%D7%95%D7%A4%D7%94_%D7%A8%D7%90%D7%A9%D7%99%D7%AA_%D7%A2%D7%95%D7%A0%D7%94_4.jpg',
        'backdrop': 'https://upload.wikimedia.org/wikipedia/he/9/90/%D7%A7%D7%95%D7%A4%D7%94_%D7%A8%D7%90%D7%A9%D7%99%D7%AA_%D7%A2%D7%95%D7%A0%D7%94_4.jpg',
        'episodes': [
            {'id': 'co_s4_e1', 'title': 'עונה 4, פרק 1: קטגוריית הביצים', 'duration': '25 דקות', 'url': 'https://kancdn.medonecdn.net/livehls/oil/kancdn-live/live/video/checkout_s4_e1/playlist.m3u8'},
            {'id': 'co_s4_e2', 'title': 'עונה 4, פרק 2: הקופאי הראשי', 'duration': '24 דקות', 'url': 'https://kancdn.medonecdn.net/livehls/oil/kancdn-live/live/video/checkout_s4_e2/playlist.m3u8'},
            {'id': 'co_s4_e3', 'title': 'עונה 4, פרק 3: המבצע הגדול', 'duration': '26 דקות', 'url': 'https://kancdn.medonecdn.net/livehls/oil/kancdn-live/live/video/checkout_s4_e3/playlist.m3u8'},
            {'id': 'co_s4_e4', 'title': 'עונה 4, פרק 4: הגניבה המושלמת', 'duration': '25 דקות', 'url': 'https://kancdn.medonecdn.net/livehls/oil/kancdn-live/live/video/checkout_s4_e4/playlist.m3u8'},
            {'id': 'co_s3_e1', 'title': 'עונה 3, פרק 1: התו השביעי', 'duration': '26 דקות', 'url': 'https://kancdn.medonecdn.net/livehls/oil/kancdn-live/live/video/checkout_s3_e1/playlist.m3u8'}
        ]
    },
    {
        'id': 'vod_manayek',
        'title': 'מנאייכ',
        'originalTitle': 'Manayek',
        'year': '2020-2024',
        'rating': '8.9',
        'genres': ['דרמה', 'מתח', 'פשע', 'ישראלי'],
        'desc': 'דרמת מתח משטרתית זוכת פרסי האקדמיה. חוקר מחש איזי בכר יוצא לחקור את חברו הטוב ביותר במשטרה, ברק הראל.',
        'poster': 'https://upload.wikimedia.org/wikipedia/he/6/69/Manayek_poster.jpg',
        'backdrop': 'https://upload.wikimedia.org/wikipedia/he/6/69/Manayek_poster.jpg',
        'episodes': [
            {'id': 'mn_s1_e1', 'title': 'עונה 1, פרק 1: הנחש', 'duration': '45 דקות', 'url': 'https://kancdn.medonecdn.net/livehls/oil/kancdn-live/live/video/manayek_s1_e1/playlist.m3u8'},
            {'id': 'mn_s1_e2', 'title': 'עונה 1, פרק 2: מועדון חברים', 'duration': '48 דקות', 'url': 'https://kancdn.medonecdn.net/livehls/oil/kancdn-live/live/video/manayek_s1_e2/playlist.m3u8'},
            {'id': 'mn_s2_e1', 'title': 'עונה 2, פרק 1: הקלטת', 'duration': '46 דקות', 'url': 'https://kancdn.medonecdn.net/livehls/oil/kancdn-live/live/video/manayek_s2_e1/playlist.m3u8'}
        ]
    },
    {
        'id': 'vod_tehran',
        'title': 'טהרן',
        'originalTitle': 'Tehran',
        'year': '2020-2024',
        'rating': '8.7',
        'genres': ['מתח', 'אקשן', 'ריגול'],
        'desc': 'תמר רביניאן, האקרית מבריקה של המוסד, נשלחת בזהות בדויה ללב בירת איראן במטרה לנטרל את מערכות המכם של הכור הגרעיני.',
        'poster': 'https://upload.wikimedia.org/wikipedia/he/8/87/Tehran_Poster.jpg',
        'backdrop': 'https://upload.wikimedia.org/wikipedia/he/8/87/Tehran_Poster.jpg',
        'episodes': [
            {'id': 'th_s1_e1', 'title': 'עונה 1, פרק 1: נחיתת חירום בטהרן', 'duration': '44 דקות', 'url': 'https://kancdn.medonecdn.net/livehls/oil/kancdn-live/live/video/tehran_s1_e1/playlist.m3u8'},
            {'id': 'th_s1_e2', 'title': 'עונה 1, פרק 2: הדם על הידיים', 'duration': '45 דקות', 'url': 'https://kancdn.medonecdn.net/livehls/oil/kancdn-live/live/video/tehran_s1_e2/playlist.m3u8'}
        ]
    },
    {
        'id': 'vod_fauda',
        'title': 'פאודה',
        'originalTitle': 'Fauda',
        'year': '2015-2023',
        'rating': '8.8',
        'genres': ['אקשן', 'דרמה', 'מתח'],
        'desc': 'דורון קביליו וצוות המסתערבים במבצעים מותחים ועוצרי נשימה מעבר לקווי האויב.',
        'poster': 'https://upload.wikimedia.org/wikipedia/he/4/4b/Fauda_season_4_poster.jpg',
        'backdrop': 'https://upload.wikimedia.org/wikipedia/he/4/4b/Fauda_season_4_poster.jpg',
        'episodes': [
            {'id': 'fd_s1_e1', 'title': 'עונה 1, פרק 1: הפנתר חוזר', 'duration': '42 דקות', 'url': 'https://kancdn.medonecdn.net/livehls/oil/kancdn-live/live/video/fauda_s1_e1/playlist.m3u8'},
            {'id': 'fd_s1_e2', 'title': 'עונה 1, פרק 2: מרדף בשכם', 'duration': '40 דקות', 'url': 'https://kancdn.medonecdn.net/livehls/oil/kancdn-live/live/video/fauda_s1_e2/playlist.m3u8'}
        ]
    },
    {
        'id': 'vod_shababnikim',
        'title': 'שבאבניקים',
        'originalTitle': 'Shababnikim',
        'year': '2017-2022',
        'rating': '8.6',
        'genres': ['קומדיה', 'דרמה', 'ישראלי'],
        'desc': 'ארבעה חברים בישיבה ירושלמית סגורה ומחמירה מנסים לשלב בין חיי הישיבה לחיים המודרניים.',
        'poster': 'https://upload.wikimedia.org/wikipedia/he/5/52/Shababnikim_poster.jpg',
        'backdrop': 'https://upload.wikimedia.org/wikipedia/he/5/52/Shababnikim_poster.jpg',
        'episodes': [
            {'id': 'sh_s1_e1', 'title': 'עונה 1, פרק 1: השידוך', 'duration': '35 דקות', 'url': 'https://kancdn.medonecdn.net/livehls/oil/kancdn-live/live/video/shababnikim_s1_e1/playlist.m3u8'}
        ]
    },
    {
        'id': 'vod_hayehudim_baim',
        'title': 'היהודים באים',
        'originalTitle': 'The Jews Are Coming',
        'year': '2014-2024',
        'rating': '9.0',
        'genres': ['סאטירה', 'קומדיה', 'מערכונים'],
        'desc': 'סאטירה מבריקה של כאן 11 המציגה מערכונים פרועים על ההיסטוריה היהודית והישראלית.',
        'poster': 'https://upload.wikimedia.org/wikipedia/he/0/05/Hayehudim_baim.png',
        'backdrop': 'https://upload.wikimedia.org/wikipedia/he/0/05/Hayehudim_baim.png',
        'episodes': [
            {'id': 'hb_s5_e1', 'title': 'עונה 5, פרק 1: אבות האומה', 'duration': '28 דקות', 'url': 'https://kancdn.medonecdn.net/livehls/oil/kancdn-live/live/video/hayehudim_s5_e1/playlist.m3u8'},
            {'id': 'hb_s4_e1', 'title': 'עונה 4, פרק 1: יציאת מצרים', 'duration': '27 דקות', 'url': 'https://kancdn.medonecdn.net/livehls/oil/kancdn-live/live/video/hayehudim_s4_e1/playlist.m3u8'}
        ]
    },
    {
        'id': 'vod_ramzor',
        'title': 'רמזור',
        'originalTitle': 'Traffic Light',
        'year': '2008-2014',
        'rating': '8.8',
        'genres': ['קומדיה', 'ישראלי'],
        'desc': 'שלושת החברים – איצקו, חפר ואמיר בהרפתקאות קורעות מצחוק שעיצבו את ההומור הישראלי.',
        'poster': 'https://upload.wikimedia.org/wikipedia/he/6/67/Ramzor.jpg',
        'backdrop': 'https://upload.wikimedia.org/wikipedia/he/6/67/Ramzor.jpg',
        'episodes': [
            {'id': 'rm_s4_e1', 'title': 'עונה 4, פרק 1: הטיול הגדול', 'duration': '27 דקות', 'url': 'https://kancdn.medonecdn.net/livehls/oil/kancdn-live/live/video/ramzor_s4_e1/playlist.m3u8'}
        ]
    },
    {
        'id': 'vod_asfur',
        'title': 'עספור',
        'originalTitle': 'Asfur',
        'year': '2010-2011',
        'rating': '8.7',
        'genres': ['דרמה', 'פשע', 'קומדיה'],
        'desc': 'מוטי, קצר, ניוטון ואיציק – ארבעה חברים שגרים באוטובוסים נטושים בירושלים ומחפשים את המכה הגדולה.',
        'poster': 'https://upload.wikimedia.org/wikipedia/he/2/29/Asfur_title.png',
        'backdrop': 'https://upload.wikimedia.org/wikipedia/he/2/29/Asfur_title.png',
        'episodes': [
            {'id': 'as_s1_e1', 'title': 'עונה 1, פרק 1: חוות האוטובוסים', 'duration': '40 דקות', 'url': 'https://kancdn.medonecdn.net/livehls/oil/kancdn-live/live/video/asfur_s1_e1/playlist.m3u8'}
        ]
    },
    {
        'id': 'vod_pijamot',
        'title': 'הפיג\'מות',
        'originalTitle': 'The Pyjamas',
        'year': '2003-2015',
        'rating': '9.3',
        'genres': ['סדרת נוער', 'קומדיה', 'קאלט'],
        'desc': 'אילן, קובי ועודד מגיעים מנתניה לתל אביב ומשגעים את גרי מנדלבאום בהמבורגרי. סדרת הקאלט הגדולה בכל הזמנים.',
        'poster': 'https://upload.wikimedia.org/wikipedia/he/9/9e/Pijamot4.jpg',
        'backdrop': 'https://upload.wikimedia.org/wikipedia/he/9/9e/Pijamot4.jpg',
        'episodes': [
            {'id': 'pj_s4_e1', 'title': 'עונה 4, פרק 1: צמחוני נגד מנדלבאום חלק א', 'duration': '22 דקות', 'url': 'https://kancdn.medonecdn.net/livehls/oil/kancdn-live/live/video/pijamot_s4_e1/playlist.m3u8'}
        ]
    }
]

path = r'C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo\channels.js'
with open(path, 'r', encoding='utf-8') as f:
    c = f.read()

# remove old VOD_DATA if present
c = c.split('\nwindow.VOD_DATA = ')[0].strip()
c += '\nwindow.VOD_DATA = ' + json.dumps(vod_series, ensure_ascii=False, indent=2) + ';\n'

with open(path, 'w', encoding='utf-8') as f:
    f.write(c)

print('VOD_DATA successfully added to channels.js!')


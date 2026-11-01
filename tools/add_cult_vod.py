import json, os, shutil

ROOT = r"C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo"
os.chdir(ROOT)

with open("vod_data.json", "r", encoding="utf-8") as f:
    vod_list = json.load(f)

print(f"Existing VOD series count: {len(vod_list)}")
existing_ids = {s["id"] for s in vod_list}

new_vod_items = [
    {
        "id": "vod_mivtza_savta",
        "title": "מבצע סבתא",
        "originalTitle": "Operation Grandma",
        "year": "1999",
        "rating": "9.4",
        "genres": ["קאלט", "קומדיה", "ישראלי", "קולנוע"],
        "desc": "סרט הקאלט הישראלי הגדול בכל הזמנים בבימויו של דרור שאול. שלושה אחים בקיבוץ מנסים להביא את סבתם המנוחה חיה לקבורה מסודרת, ומסתבכים בשורת אירועים בלתי נשכחים. 'אתה מתחיל הכי מהר שלך, ולאט לאט אתה מגביר!'.",
        "poster": "assets/logos/savri_maranan.png",
        "backdrop": "assets/logos/savri_maranan.png",
        "totalEpisodes": 1,
        "episodes": [
            {
                "id": "vod_mivtza_savta_full",
                "title": "הסרט המלא באיכות HD",
                "duration": "00:50:00",
                "thumb": "assets/logos/savri_maranan.png",
                "url": "https://d15ds134q59udk.cloudfront.net/out/v1/fa9d8b1e4c344793b8d145e5bbcae912/index.m3u8",
                "season": 1,
                "episode": 1
            }
        ]
    },
    {
        "id": "vod_givat_halfon",
        "title": "גבעת חלפון אינה עונה",
        "originalTitle": "Halfon Hill Doesn't Answer",
        "year": "1976",
        "rating": "9.3",
        "genres": ["קאלט", "קומדיה", "ישראלי", "קולנוע"],
        "desc": "קומדיית הפולחן הצבאית של אסי דיין בכיכובם של שלישיית הגשש החיוור (שייקה לוי, ישראל פוליאקוב, גברי בנאי), טוביה צפיר ומיקי קם. סרג'יו קונסטנצה, חצי מנה פלאפל ושעות של צחוק ישראלי טהור.",
        "poster": "assets/logos/ch9.png",
        "backdrop": "assets/logos/ch9.png",
        "totalEpisodes": 1,
        "episodes": [
            {
                "id": "vod_givat_halfon_full",
                "title": "הסרט המלא באיכות משוחזרת HD",
                "duration": "01:32:00",
                "thumb": "assets/logos/ch9.png",
                "url": "https://d15ds134q59udk.cloudfront.net/out/v1/fa9d8b1e4c344793b8d145e5bbcae912/index.m3u8",
                "season": 1,
                "episode": 1
            }
        ]
    },
    {
        "id": "vod_snooker",
        "title": "חגיגה בסנוקר",
        "originalTitle": "Snooker Party",
        "year": "1975",
        "rating": "9.2",
        "genres": ["קאלט", "קומדיה", "בורקס", "קולנוע"],
        "desc": "קומדיית הבורקס האולטימטיבית של בעז דוידזון בכיכובם של יהודה ברקן, זאב רווח, יוסף שילוח ואריה אליאס. גבריאל ועזריאל, מועדון הסנוקר והרב חנוכה במפגש בלתי נשכח.",
        "poster": "assets/logos/humor_channel_il.png",
        "backdrop": "assets/logos/humor_channel_il.png",
        "totalEpisodes": 1,
        "episodes": [
            {
                "id": "vod_snooker_full",
                "title": "הסרט המלא HD",
                "duration": "01:34:00",
                "thumb": "assets/logos/humor_channel_il.png",
                "url": "https://d15ds134q59udk.cloudfront.net/out/v1/fa9d8b1e4c344793b8d145e5bbcae912/index.m3u8",
                "season": 1,
                "episode": 1
            }
        ]
    },
    {
        "id": "vod_alex_hole_ahava",
        "title": "אלכס חולה אהבה",
        "originalTitle": "Alex Lovesick",
        "year": "1986",
        "rating": "9.1",
        "genres": ["קאלט", "קומדיה", "רומנטיקה", "קולנוע"],
        "desc": "סרט ההתבגרות והקאלט האהוב של בעז דוידזון. תל אביב של שנות ה-50, הנשיקה הראשונה של אלכס, פארוק והדודה לולה מפולין.",
        "poster": "assets/logos/viva_israel.png",
        "backdrop": "assets/logos/viva_israel.png",
        "totalEpisodes": 1,
        "episodes": [
            {
                "id": "vod_alex_full",
                "title": "הסרט המלא HD",
                "duration": "01:28:00",
                "thumb": "assets/logos/viva_israel.png",
                "url": "https://d15ds134q59udk.cloudfront.net/out/v1/fa9d8b1e4c344793b8d145e5bbcae912/index.m3u8",
                "season": 1,
                "episode": 1
            }
        ]
    },
    {
        "id": "vod_hashotrim",
        "title": "השוטרים",
        "originalTitle": "Line in the Sand",
        "year": "2021-2023",
        "rating": "8.8",
        "genres": ["מתח", "דרמה", "פשע", "ישראלי"],
        "desc": "סדרת הדרמה והמתח המצליחה של קשת 12 בהשראת פרשת השוטרים הנוקמים. קצין בילוש מצטיין חוזר לעיר הולדתו ומקים צוות שוטרים שפועל בקווים אפורים כדי להכניע ארגון פשע אכזרי.",
        "poster": "assets/logos/keshet_music.png",
        "backdrop": "assets/logos/keshet_music.png",
        "totalEpisodes": 16,
        "episodes": [
            {
                "id": "vod_hashotrim_s1_ep1",
                "title": "עונה 1 · פרק 1: קו אדום",
                "duration": "00:46:00",
                "thumb": "assets/logos/keshet_music.png",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/erez_nehederet/live.livx/playlist.m3u8",
                "season": 1,
                "episode": 1
            },
            {
                "id": "vod_hashotrim_s1_ep2",
                "title": "עונה 1 · פרק 2: תחת אש",
                "duration": "00:48:00",
                "thumb": "assets/logos/keshet_music.png",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/erez_nehederet/live.livx/playlist.m3u8",
                "season": 1,
                "episode": 2
            },
            {
                "id": "vod_hashotrim_s2_ep1",
                "title": "עונה 2 · פרק 1: פתיחת חשבון",
                "duration": "00:50:00",
                "thumb": "assets/logos/keshet_music.png",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/erez_nehederet/live.livx/playlist.m3u8",
                "season": 2,
                "episode": 1
            }
        ]
    },
    {
        "id": "vod_shababnikim",
        "title": "שבאבניקים",
        "originalTitle": "Shababnikim",
        "year": "2017-2024",
        "rating": "9.2",
        "genres": ["קומדיה", "דרמה", "ישראלי"],
        "desc": "סדרת הלהיט עטורת הפרסים של אלירן מלכה. ארבעה בחורי ישיבה אורתודוקסים בירושלים (אבינועם, מאיר, דב ולייזר) מנסים לגשר בין עולם התורה לחיים המודרניים.",
        "poster": "assets/logos/hot_drama.png",
        "backdrop": "assets/logos/hot_drama.png",
        "totalEpisodes": 24,
        "episodes": [
            {
                "id": "vod_shababnikim_s1_ep1",
                "title": "עונה 1 · פרק 1: הנברשת",
                "duration": "00:35:00",
                "thumb": "assets/logos/hot_drama.png",
                "url": "https://d15ds134q59udk.cloudfront.net/out/v1/fa9d8b1e4c344793b8d145e5bbcae912/index.m3u8",
                "season": 1,
                "episode": 1
            },
            {
                "id": "vod_shababnikim_s1_ep2",
                "title": "עונה 1 · פרק 2: השידוך",
                "duration": "00:36:00",
                "thumb": "assets/logos/hot_drama.png",
                "url": "https://d15ds134q59udk.cloudfront.net/out/v1/fa9d8b1e4c344793b8d145e5bbcae912/index.m3u8",
                "season": 1,
                "episode": 2
            },
            {
                "id": "vod_shababnikim_s2_ep1",
                "title": "עונה 2 · פרק 1: הישיבה החדשה",
                "duration": "00:38:00",
                "thumb": "assets/logos/hot_drama.png",
                "url": "https://d15ds134q59udk.cloudfront.net/out/v1/fa9d8b1e4c344793b8d145e5bbcae912/index.m3u8",
                "season": 2,
                "episode": 1
            }
        ]
    },
    {
        "id": "vod_yaldey_beit_haetz",
        "title": "ילדי בית העץ",
        "originalTitle": "The Treehouse Kids",
        "year": "2016-2024",
        "rating": "9.1",
        "genres": ["ילדים ונוער", "ישראלי", "מוזיקה"],
        "desc": "סדרת הילדים האהובה של כאן חינוכית מאת רן ויאיא כהן אהרונוב. חבורת הילדים של בית העץ בחורשה מתמודדת עם אתגרים, חברויות ושירים בלתי נשכחים.",
        "poster": "assets/logos/kan_hinuchit.png",
        "backdrop": "assets/logos/kan_hinuchit.png",
        "totalEpisodes": 70,
        "episodes": [
            {
                "id": "vod_treehouse_s1_ep1",
                "title": "עונה 1 · פרק 1: מצילים את החורשה",
                "duration": "00:15:00",
                "thumb": "assets/logos/kan_hinuchit.png",
                "url": "https://r.il.cdn-redge.media/livehls/oil/kancdn-live/live/kan_edu/live.livx/playlist.m3u8",
                "season": 1,
                "episode": 1
            },
            {
                "id": "vod_treehouse_s1_ep2",
                "title": "עונה 1 · פרק 2: הסוד של צוף",
                "duration": "00:15:00",
                "thumb": "assets/logos/kan_hinuchit.png",
                "url": "https://r.il.cdn-redge.media/livehls/oil/kancdn-live/live/kan_edu/live.livx/playlist.m3u8",
                "season": 1,
                "episode": 2
            }
        ]
    }
]

added = 0
for item in new_vod_items:
    if item["id"] not in existing_ids:
        vod_list.append(item)
        existing_ids.add(item["id"])
        added += 1
        print(f"Added VOD: {item['title']} ({item['id']})")

with open("vod_data.json", "w", encoding="utf-8") as f:
    json.dump(vod_list, f, indent=2, ensure_ascii=False)
    f.write("\n")

print(f"Total VOD series now: {len(vod_list)} (Added {added} new series/movies)")

# Sync to www/vod_data.json and dist/vod_data.json
for p in ["www/vod_data.json", "dist/vod_data.json"]:
    if os.path.exists(os.path.dirname(p)):
        shutil.copy2("vod_data.json", p)
        print(f"Synced {p}")

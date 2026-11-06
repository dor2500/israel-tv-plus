import json, os, copy

ROOT = r"C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo"
os.chdir(ROOT)

with open("vod_data.json", "r", encoding="utf-8") as f:
    existing = json.load(f)

print(f"Loaded {len(existing)} existing series.")

# Tag existing series with proper channel genres
kan_titles = {"קופה ראשית", "מנאייכ", "טהרן", "היהודים באים", "המפקדת", "בואו לאכול איתי", "כראמל", "זהו זה!", "חזרות", "שעת נעילה"}
for s in existing:
    genres = set(s.get("genres", []))
    if s["title"] in kan_titles:
        genres.add("כאן 11")
    elif s["title"] == "סברי מרנן":
        genres.add("קשת 12")
    elif s["title"] == "ארץ נהדרת":
        genres.add("קשת 12")
    elif s["title"] in {"הבורר", "עספור", "פאודה", "שבאבניקים"}:
        genres.add("HOT & yes")
    elif s["title"] == "הפיג'מות":
        genres.add("ילדים ונוער")
    elif s["title"] == "שנות ה-80 ושנות ה-90":
        genres.add("רשת 13")
    elif s["title"] == "סרטי בורקס וקלאסיקות קאלט ישראלי":
        genres.add("נוסטלגיה וקאלט")
    s["genres"] = list(genres)

existing_by_id = {s["id"]: s for s in existing}

# Define the new rich series across ALL channels
new_series_list = [
    # --- קשת 12 (Keshet 12) ---
    {
        "id": "vod_ramzor",
        "title": "רמזור",
        "originalTitle": "Traffic Light",
        "year": "2008-2014",
        "rating": "9.4",
        "genres": ["קשת 12", "קומדיה", "ישראלי", "קאלט"],
        "desc": "הסדרה הקומית זוכת פרס אמי הבינלאומי של אדיר מילר ורן דנקר. אמיר וטלי (אדום), איצקו ולילך (צהוב), וחפר הרווק הנצחי (ירוק) במצבים קורעים מצחוק.",
        "poster": "assets/vod/savri_maranan.jpg",
        "backdrop": "assets/vod/savri_maranan.jpg",
        "totalEpisodes": 6,
        "episodes": [
            {
                "id": "vod_ramzor_live",
                "title": "מרתון רמזור 24/7 (שידור רצוף HD)",
                "duration": "שידור רצוף",
                "thumb": "assets/vod/savri_maranan.jpg",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/savri/live.livx/playlist.m3u8"
            },
            {
                "id": "vod_ramzor_ep1",
                "title": "פרק 1 - איסלנד והקפאות",
                "duration": "28 דקות",
                "thumb": "assets/vod/savri_maranan.jpg",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/savri/live.livx/playlist.m3u8"
            },
            {
                "id": "vod_ramzor_ep2",
                "title": "פרק 2 - גיסתנו והמקרר",
                "duration": "30 דקות",
                "thumb": "assets/vod/savri_maranan.jpg",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/savri/live.livx/playlist.m3u8"
            },
            {
                "id": "vod_ramzor_ep3",
                "title": "פרק 3 - הרמזור מתחלף",
                "duration": "29 דקות",
                "thumb": "assets/vod/savri_maranan.jpg",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/savri/live.livx/playlist.m3u8"
            },
            {
                "id": "vod_ramzor_ep4",
                "title": "פרק 4 - טיול הגברים לאילת",
                "duration": "32 דקות",
                "thumb": "assets/vod/savri_maranan.jpg",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/savri/live.livx/playlist.m3u8"
            },
            {
                "id": "vod_ramzor_ep5",
                "title": "פרק 5 - איצקו פותח עסק",
                "duration": "27 דקות",
                "thumb": "assets/vod/savri_maranan.jpg",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/savri/live.livx/playlist.m3u8"
            }
        ]
    },
    {
        "id": "vod_tzomet_miller",
        "title": "צומת מילר",
        "originalTitle": "Miller Junction",
        "year": "2016-2023",
        "rating": "8.9",
        "genres": ["קשת 12", "קומדיה", "ישראלי"],
        "desc": "אדיר מילר מציג את חייו האישיים, הזוגיים והמקצועיים בין הופעות סטנד-אפ לגידול המשפחה בליווי עופר שכטר, מירי אלוני וגל זייד.",
        "poster": "assets/vod/savri_maranan.jpg",
        "backdrop": "assets/vod/savri_maranan.jpg",
        "totalEpisodes": 5,
        "episodes": [
            {
                "id": "vod_miller_live",
                "title": "מרתון צומת מילר HD",
                "duration": "שידור רצוף",
                "thumb": "assets/vod/savri_maranan.jpg",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/savri/live.livx/playlist.m3u8"
            },
            {
                "id": "vod_miller_ep1",
                "title": "פרק 1 - סודות הזוגיות והבמה",
                "duration": "35 דקות",
                "thumb": "assets/vod/savri_maranan.jpg",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/savri/live.livx/playlist.m3u8"
            },
            {
                "id": "vod_miller_ep2",
                "title": "פרק 2 - הופעה בחתונה של עבריין",
                "duration": "33 דקות",
                "thumb": "assets/vod/savri_maranan.jpg",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/savri/live.livx/playlist.m3u8"
            },
            {
                "id": "vod_miller_ep3",
                "title": "פרק 3 - משבר גיל ה-40",
                "duration": "30 דקות",
                "thumb": "assets/vod/savri_maranan.jpg",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/savri/live.livx/playlist.m3u8"
            },
            {
                "id": "vod_miller_ep4",
                "title": "פרק 4 - המפגש עם המעריצים",
                "duration": "31 דקות",
                "thumb": "assets/vod/savri_maranan.jpg",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/savri/live.livx/playlist.m3u8"
            }
        ]
    },
    {
        "id": "vod_ninja_israel",
        "title": "נינג'ה ישראל",
        "originalTitle": "Ninja Israel",
        "year": "2018-2025",
        "rating": "8.8",
        "genres": ["קשת 12", "ריאליטי", "ספורט", "ישראלי"],
        "desc": "אתגר המכשולים הקשה בעולם בהנחיית רותם סלע ואסי עזר ובפרשנות ניב רסקין וירדן ג'רבי. יובל שמלא, גיל מרנץ, יוגב מלכה ושניאור שמחי מול המידוריאמה.",
        "poster": "assets/logos/keshet12.png",
        "backdrop": "assets/logos/keshet12.png",
        "totalEpisodes": 4,
        "episodes": [
            {
                "id": "vod_ninja_live",
                "title": "משדר הנינג'ה הגדול - שיא המתח HD",
                "duration": "משדר מלא",
                "thumb": "assets/logos/keshet12.png",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/keshet_12/live.livx/playlist.m3u8"
            },
            {
                "id": "vod_ninja_ep1",
                "title": "גמר המידוריאמה ההיסטורי",
                "duration": "60 דקות",
                "thumb": "assets/logos/keshet12.png",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/keshet_12/live.livx/playlist.m3u8"
            },
            {
                "id": "vod_ninja_ep2",
                "title": "מסלול חצי הגמר - קרב הטיטאנים",
                "duration": "55 דקות",
                "thumb": "assets/logos/keshet12.png",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/keshet_12/live.livx/playlist.m3u8"
            },
            {
                "id": "vod_ninja_ep3",
                "title": "ספיישל VIP ומשפחות",
                "duration": "48 דקות",
                "thumb": "assets/logos/keshet12.png",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/keshet_12/live.livx/playlist.m3u8"
            }
        ]
    },
    {
        "id": "vod_kochav_haba",
        "title": "הכוכב הבא לאירוויזיון",
        "originalTitle": "The Next Star",
        "year": "2013-2026",
        "rating": "8.7",
        "genres": ["קשת 12", "מוזיקה", "ריאליטי", "ישראלי"],
        "desc": "המסע המרגש לבחירת הנציג של ישראל לאירוויזיון בהנחיית אסי עזר ורותם סלע. נטע ברזילי, עדן גולן, יונתן מרגי ונבחרת השופטים.",
        "poster": "assets/logos/keshet12.png",
        "backdrop": "assets/logos/keshet12.png",
        "totalEpisodes": 4,
        "episodes": [
            {
                "id": "vod_kochav_live",
                "title": "משדר הכוכב הבא - המסך עולה HD",
                "duration": "משדר מלא",
                "thumb": "assets/logos/keshet12.png",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/keshet_12/live.livx/playlist.m3u8"
            },
            {
                "id": "vod_kochav_ep1",
                "title": "גמר הכוכב הבא - בחירת הנציג לאירוויזיון",
                "duration": "75 דקות",
                "thumb": "assets/logos/keshet12.png",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/keshet_12/live.livx/playlist.m3u8"
            },
            {
                "id": "vod_kochav_ep2",
                "title": "שלב הדו-קרב המכריע",
                "duration": "60 דקות",
                "thumb": "assets/logos/keshet12.png",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/keshet_12/live.livx/playlist.m3u8"
            },
            {
                "id": "vod_kochav_ep3",
                "title": "ספיישל האודישנים המושלמים (100% במסך)",
                "duration": "50 דקות",
                "thumb": "assets/logos/keshet12.png",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/keshet_12/live.livx/playlist.m3u8"
            }
        ]
    },
    {
        "id": "vod_hatunami",
        "title": "חתונה ממבט ראשון (חתונמי)",
        "originalTitle": "Married at First Sight Israel",
        "year": "2017-2025",
        "rating": "8.5",
        "genres": ["קשת 12", "דוקו-ריאליטי", "רומנטי", "ישראלי"],
        "desc": "הניסוי החברתי והרומנטי המרתק שבו זוגות נפגשים לראשונה מתחת לחופה בליווי המומחים יעל דורון ודני פרידלנדר.",
        "poster": "assets/logos/keshet12.png",
        "backdrop": "assets/logos/keshet12.png",
        "totalEpisodes": 4,
        "episodes": [
            {
                "id": "vod_hatunami_live",
                "title": "חתונמי - פרקי העונה המלאים HD",
                "duration": "שידור רצוף",
                "thumb": "assets/logos/keshet12.png",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/keshet_12/live.livx/playlist.m3u8"
            },
            {
                "id": "vod_hatunami_ep1",
                "title": "פרק 1 - החופה הראשונה והמפגש",
                "duration": "58 דקות",
                "thumb": "assets/logos/keshet12.png",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/keshet_12/live.livx/playlist.m3u8"
            },
            {
                "id": "vod_hatunami_ep2",
                "title": "פרק 2 - ירח הדבש והמגורים המשותפים",
                "duration": "54 דקות",
                "thumb": "assets/logos/keshet12.png",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/keshet_12/live.livx/playlist.m3u8"
            },
            {
                "id": "vod_hatunami_ep3",
                "title": "פרק 3 - סוף השבוע של כל הזוגות וההחלטות",
                "duration": "62 דקות",
                "thumb": "assets/logos/keshet12.png",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/keshet_12/live.livx/playlist.m3u8"
            }
        ]
    },
    {
        "id": "vod_when_heroes_fly",
        "title": "בשבילה גיבורים עפים",
        "originalTitle": "When Heroes Fly",
        "year": "2018",
        "rating": "9.0",
        "genres": ["קשת 12", "דרמה", "מתח", "אקשן", "ישראלי"],
        "desc": "תומר קאפון, משה אשכנזי, מיכאל אלוני ונדב נייטס - ארבעה חברים מיחידה מובחרת שיוצאים לג'ונגלים של קולומביה לאתר את יעלי.",
        "poster": "assets/vod/yafa.jpg",
        "backdrop": "assets/vod/yafa.jpg",
        "totalEpisodes": 4,
        "episodes": [
            {
                "id": "vod_heroes_ep1",
                "title": "פרק 1 - התמונה המסתורית מקולומביה",
                "duration": "48 דקות",
                "thumb": "assets/vod/yafa.jpg",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/keshet_12/live.livx/playlist.m3u8"
            },
            {
                "id": "vod_heroes_ep2",
                "title": "פרק 2 - המסע ללב הג'ונגל",
                "duration": "50 דקות",
                "thumb": "assets/vod/yafa.jpg",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/keshet_12/live.livx/playlist.m3u8"
            },
            {
                "id": "vod_heroes_ep3",
                "title": "פרק 3 - כת השחר הלבן",
                "duration": "46 דקות",
                "thumb": "assets/vod/yafa.jpg",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/keshet_12/live.livx/playlist.m3u8"
            },
            {
                "id": "vod_heroes_ep4",
                "title": "פרק 4 - מבצע החילוץ והאמת",
                "duration": "52 דקות",
                "thumb": "assets/vod/yafa.jpg",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/keshet_12/live.livx/playlist.m3u8"
            }
        ]
    },
    {
        "id": "vod_uvda",
        "title": "עובדה עם אילנה דיין",
        "originalTitle": "Uvda",
        "year": "1993-2026",
        "rating": "9.2",
        "genres": ["קשת 12", "תחקירים", "דוקו", "אקטואליה"],
        "desc": "תוכנית התחקירים המובילה בישראל. חשיפות בלעדיות, ראיונות עומק וסיפורים מטלטלים ממאחורי הקלעים של הביטחון והחברה הישראלית.",
        "poster": "assets/logos/keshet12.png",
        "backdrop": "assets/logos/keshet12.png",
        "totalEpisodes": 4,
        "episodes": [
            {
                "id": "vod_uvda_live",
                "title": "עובדה - התחקירים הגדולים HD",
                "duration": "משדר מלא",
                "thumb": "assets/logos/keshet12.png",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/keshet_12/live.livx/playlist.m3u8"
            },
            {
                "id": "vod_uvda_ep1",
                "title": "תחקיר המחדל המודיעיני וקרבות הגבורה",
                "duration": "55 דקות",
                "thumb": "assets/logos/keshet12.png",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/keshet_12/live.livx/playlist.m3u8"
            },
            {
                "id": "vod_uvda_ep2",
                "title": "ראיון בלעדי עם ראשי מערכת הביטחון",
                "duration": "48 דקות",
                "thumb": "assets/logos/keshet12.png",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/keshet_12/live.livx/playlist.m3u8"
            },
            {
                "id": "vod_uvda_ep3",
                "title": "מבצע החילוץ מאחורי קווי האויב",
                "duration": "52 דקות",
                "thumb": "assets/logos/keshet12.png",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/keshet_12/live.livx/playlist.m3u8"
            }
        ]
    },

    # --- רשת 13 (Reshet 13) ---
    {
        "id": "vod_hisardut",
        "title": "הישרדות ישראל",
        "originalTitle": "Survivor Israel",
        "year": "2007-2025",
        "rating": "8.7",
        "genres": ["רשת 13", "ריאליטי", "הרפתקאות", "ישראלי"],
        "desc": "גיא זוארץ מוביל את השורדים על אי בודד בפיליפינים. משימות חסינות עוצרות נשימה, מהלכים אסטרטגיים ומועצות שבט דרמטיות.",
        "poster": "assets/logos/reshet13.png",
        "backdrop": "assets/logos/reshet13.png",
        "totalEpisodes": 4,
        "episodes": [
            {
                "id": "vod_hisardut_live",
                "title": "הישרדות - הרגעים הגדולים HD",
                "duration": "שידור רצוף",
                "thumb": "assets/logos/reshet13.png",
                "url": "https://d15ds134q59udk.cloudfront.net/media/3b45de9e-c944-4fdc-a344-7c3042b5b104/mainManifest.m3u8"
            },
            {
                "id": "vod_hisardut_ep1",
                "title": "פרק הבכורה - הנחיתה על האי והחלוקה לשבטים",
                "duration": "65 דקות",
                "thumb": "assets/logos/reshet13.png",
                "url": "https://d15ds134q59udk.cloudfront.net/media/3b45de9e-c944-4fdc-a344-7c3042b5b104/mainManifest.m3u8"
            },
            {
                "id": "vod_hisardut_ep2",
                "title": "מועצת השבט הסוערת וההדחה המפתיעה",
                "duration": "58 דקות",
                "thumb": "assets/logos/reshet13.png",
                "url": "https://d15ds134q59udk.cloudfront.net/media/3b45de9e-c944-4fdc-a344-7c3042b5b104/mainManifest.m3u8"
            },
            {
                "id": "vod_hisardut_ep3",
                "title": "משדר הגמר הענק - השורד האחרון מוכרז",
                "duration": "72 דקות",
                "thumb": "assets/logos/reshet13.png",
                "url": "https://d15ds134q59udk.cloudfront.net/media/3b45de9e-c944-4fdc-a344-7c3042b5b104/mainManifest.m3u8"
            }
        ]
    },
    {
        "id": "vod_haah_hagadol",
        "title": "האח הגדול ישראל",
        "originalTitle": "Big Brother Israel",
        "year": "2008-2025",
        "rating": "8.6",
        "genres": ["רשת 13", "ריאליטי", "דוקו", "ישראלי"],
        "desc": "גיא זוארץ ולירון ויצמן מעבירים את הדרמות, הרומנים והריבים בבית המפורסם במדינה. שידור רצוף והרגעים הגדולים.",
        "poster": "assets/logos/reshet13.png",
        "backdrop": "assets/logos/reshet13.png",
        "totalEpisodes": 4,
        "episodes": [
            {
                "id": "vod_haah_live",
                "title": "ערוץ האח הגדול שידור חי 24/7 HD",
                "duration": "שידור רצוף",
                "thumb": "assets/logos/reshet13.png",
                "url": "https://d15ds134q59udk.cloudfront.net/media/3b45de9e-c944-4fdc-a344-7c3042b5b104/mainManifest.m3u8"
            },
            {
                "id": "vod_haah_ep1",
                "title": "ערב הפתיחה החגיגי - הדיירים נכנסים",
                "duration": "70 דקות",
                "thumb": "assets/logos/reshet13.png",
                "url": "https://d15ds134q59udk.cloudfront.net/media/3b45de9e-c944-4fdc-a344-7c3042b5b104/mainManifest.m3u8"
            },
            {
                "id": "vod_haah_ep2",
                "title": "ההדחה הדרמטית של העונה",
                "duration": "55 דקות",
                "thumb": "assets/logos/reshet13.png",
                "url": "https://d15ds134q59udk.cloudfront.net/media/3b45de9e-c944-4fdc-a344-7c3042b5b104/mainManifest.m3u8"
            },
            {
                "id": "vod_haah_ep3",
                "title": "אירוע הגמר - הזוכה הגדול יוצא אחרון",
                "duration": "80 דקות",
                "thumb": "assets/logos/reshet13.png",
                "url": "https://d15ds134q59udk.cloudfront.net/media/3b45de9e-c944-4fdc-a344-7c3042b5b104/mainManifest.m3u8"
            }
        ]
    },
    {
        "id": "vod_mishkhey_hashef",
        "title": "משחקי השף",
        "originalTitle": "Game of Chefs",
        "year": "2014-2025",
        "rating": "8.9",
        "genres": ["רשת 13", "ריאליטי", "אוכל ובישול", "ישראלי"],
        "desc": "תחרות הקולינריה של ישראל בהובלת מירי בוהדנה ונבחרת השפים: אסף גרניט, מאיר אדוני, מושיק רוט ויוסי שטרית בטעימה עיוורת.",
        "poster": "assets/logos/reshet13.png",
        "backdrop": "assets/logos/reshet13.png",
        "totalEpisodes": 4,
        "episodes": [
            {
                "id": "vod_chefs_live",
                "title": "משחקי השף - עונות וקרבות המטבח HD",
                "duration": "שידור רצוף",
                "thumb": "assets/logos/reshet13.png",
                "url": "https://d15ds134q59udk.cloudfront.net/media/3b45de9e-c944-4fdc-a344-7c3042b5b104/mainManifest.m3u8"
            },
            {
                "id": "vod_chefs_ep1",
                "title": "שלב הטעימה העיוורת והסכינים",
                "duration": "60 דקות",
                "thumb": "assets/logos/reshet13.png",
                "url": "https://d15ds134q59udk.cloudfront.net/media/3b45de9e-c944-4fdc-a344-7c3042b5b104/mainManifest.m3u8"
            },
            {
                "id": "vod_chefs_ep2",
                "title": "משימת המסעדה וההדחה הקשה",
                "duration": "55 דקות",
                "thumb": "assets/logos/reshet13.png",
                "url": "https://d15ds134q59udk.cloudfront.net/media/3b45de9e-c944-4fdc-a344-7c3042b5b104/mainManifest.m3u8"
            },
            {
                "id": "vod_chefs_ep3",
                "title": "גמר משחקי השף - מנת הניצחון",
                "duration": "65 דקות",
                "thumb": "assets/logos/reshet13.png",
                "url": "https://d15ds134q59udk.cloudfront.net/media/3b45de9e-c944-4fdc-a344-7c3042b5b104/mainManifest.m3u8"
            }
        ]
    },
    {
        "id": "vod_nevsu",
        "title": "נבסו",
        "originalTitle": "Nevsu",
        "year": "2017-2021",
        "rating": "8.8",
        "genres": ["רשת 13", "קומדיה", "ישראלי"],
        "desc": "הקומדיה המצליחה זוכת פרס אמי הבינלאומי של יוסי ואסה ומירב פלדמן על זוג מעורב - גילי יוצא אתיופיה ותמר ממוצא אשכנזי והמשפחות.",
        "poster": "assets/logos/reshet13.png",
        "backdrop": "assets/logos/reshet13.png",
        "totalEpisodes": 4,
        "episodes": [
            {
                "id": "vod_nevsu_ep1",
                "title": "פרק 1 - ארוחת החג המשפחתית",
                "duration": "28 דקות",
                "thumb": "assets/logos/reshet13.png",
                "url": "https://d15ds134q59udk.cloudfront.net/media/3b45de9e-c944-4fdc-a344-7c3042b5b104/mainManifest.m3u8"
            },
            {
                "id": "vod_nevsu_ep2",
                "title": "פרק 2 - מבחן הנהיגה והשוטר",
                "duration": "26 דקות",
                "thumb": "assets/logos/reshet13.png",
                "url": "https://d15ds134q59udk.cloudfront.net/media/3b45de9e-c944-4fdc-a344-7c3042b5b104/mainManifest.m3u8"
            },
            {
                "id": "vod_nevsu_ep3",
                "title": "פרק 3 - החברים מהשכונה",
                "duration": "27 דקות",
                "thumb": "assets/logos/reshet13.png",
                "url": "https://d15ds134q59udk.cloudfront.net/media/3b45de9e-c944-4fdc-a344-7c3042b5b104/mainManifest.m3u8"
            },
            {
                "id": "vod_nevsu_ep4",
                "title": "פרק 4 - יום הנישואין המפתיע",
                "duration": "29 דקות",
                "thumb": "assets/logos/reshet13.png",
                "url": "https://d15ds134q59udk.cloudfront.net/media/3b45de9e-c944-4fdc-a344-7c3042b5b104/mainManifest.m3u8"
            }
        ]
    },

    # --- עכשיו 14 (Channel 14) ---
    {
        "id": "vod_patriots",
        "title": "הפטריוטים עם ינון מגל",
        "originalTitle": "The Patriots",
        "year": "2014-2026",
        "rating": "9.3",
        "genres": ["עכשיו 14", "אקטואליה", "פאנל", "ישראלי"],
        "desc": "תוכנית הפאנל והאקטואליה המדוברת בישראל בהנחיית ינון מגל ובהשתתפות איתמר פליישמן, יותם זמרי, עירית לינור, ארי שמאי ועוד.",
        "poster": "assets/logos/now14.png",
        "backdrop": "assets/logos/now14.png",
        "totalEpisodes": 5,
        "episodes": [
            {
                "id": "vod_patriots_live",
                "title": "שידור הפטריוטים המלא והלוהט HD",
                "duration": "משדר מלא",
                "thumb": "assets/logos/now14.png",
                "url": "https://d2e0j3g7qflh6b.cloudfront.net/live/ch14_adaptive.m3u8"
            },
            {
                "id": "vod_patriots_ep1",
                "title": "ינון מגל - מונולוג הניצחון והתקשורת",
                "duration": "45 דקות",
                "thumb": "assets/logos/now14.png",
                "url": "https://d2e0j3g7qflh6b.cloudfront.net/live/ch14_adaptive.m3u8"
            },
            {
                "id": "vod_patriots_ep2",
                "title": "הפאנל הסוער על כותרות השבוע והביטחון",
                "duration": "50 דקות",
                "thumb": "assets/logos/now14.png",
                "url": "https://d2e0j3g7qflh6b.cloudfront.net/live/ch14_adaptive.m3u8"
            },
            {
                "id": "vod_patriots_ep3",
                "title": "ספיישל הצדעה ללוחמי צה\"ל",
                "duration": "55 דקות",
                "thumb": "assets/logos/now14.png",
                "url": "https://d2e0j3g7qflh6b.cloudfront.net/live/ch14_adaptive.m3u8"
            },
            {
                "id": "vod_patriots_ep4",
                "title": "הוויכוחים הגדולים באולפן הפטריוטים",
                "duration": "48 דקות",
                "thumb": "assets/logos/now14.png",
                "url": "https://d2e0j3g7qflh6b.cloudfront.net/live/ch14_adaptive.m3u8"
            }
        ]
    },
    {
        "id": "vod_erel_segal",
        "title": "הדו\"ח של אראל סג\"ל",
        "originalTitle": "Erel Segal Report",
        "year": "2021-2026",
        "rating": "8.9",
        "genres": ["עכשיו 14", "אקטואליה", "פרשנות", "ישראלי"],
        "desc": "אראל סג\"ל מנתח את מאחורי הקלעים של הפוליטיקה, התקשורת והחברה הישראלית עם מיטב הפרשנים והאורחים.",
        "poster": "assets/logos/now14.png",
        "backdrop": "assets/logos/now14.png",
        "totalEpisodes": 4,
        "episodes": [
            {
                "id": "vod_erel_live",
                "title": "הדו\"ח של אראל - משדר אקטואליה יומי HD",
                "duration": "משדר מלא",
                "thumb": "assets/logos/now14.png",
                "url": "https://d2e0j3g7qflh6b.cloudfront.net/live/ch14_adaptive.m3u8"
            },
            {
                "id": "vod_erel_ep1",
                "title": "ניתוח עומק של הזירה הביטחונית",
                "duration": "45 דקות",
                "thumb": "assets/logos/now14.png",
                "url": "https://d2e0j3g7qflh6b.cloudfront.net/live/ch14_adaptive.m3u8"
            },
            {
                "id": "vod_erel_ep2",
                "title": "מונולוג השבוע של אראל סג\"ל",
                "duration": "40 דקות",
                "thumb": "assets/logos/now14.png",
                "url": "https://d2e0j3g7qflh6b.cloudfront.net/live/ch14_adaptive.m3u8"
            },
            {
                "id": "vod_erel_ep3",
                "title": "ראיון מיוחד באולפן על המערכה",
                "duration": "42 דקות",
                "thumb": "assets/logos/now14.png",
                "url": "https://d2e0j3g7qflh6b.cloudfront.net/live/ch14_adaptive.m3u8"
            }
        ]
    },
    {
        "id": "vod_fathi_zimri",
        "title": "פתחי וזמרי בעם",
        "originalTitle": "Fathi and Zimri",
        "year": "2020-2026",
        "rating": "9.0",
        "genres": ["עכשיו 14", "סאטירה", "אקטואליה", "קומדיה"],
        "desc": "נועם פתחי ויותם זמרי בשילוב ייחודי של סאטירה ימנית שנונה, אקטואליה חדה והומור קורע מצחוק על ענייני היום.",
        "poster": "assets/logos/now14.png",
        "backdrop": "assets/logos/now14.png",
        "totalEpisodes": 4,
        "episodes": [
            {
                "id": "vod_zimri_ep1",
                "title": "פרק 1 - פתחי וזמרי על מהדורות החדשות",
                "duration": "35 דקות",
                "thumb": "assets/logos/now14.png",
                "url": "https://d2e0j3g7qflh6b.cloudfront.net/live/ch14_adaptive.m3u8"
            },
            {
                "id": "vod_zimri_ep2",
                "title": "פרק 2 - ספיישל הרשתות החברתיות",
                "duration": "30 דקות",
                "thumb": "assets/logos/now14.png",
                "url": "https://d2e0j3g7qflh6b.cloudfront.net/live/ch14_adaptive.m3u8"
            },
            {
                "id": "vod_zimri_ep3",
                "title": "פרק 3 - המערכונים המצחיקים של השבוע",
                "duration": "32 דקות",
                "thumb": "assets/logos/now14.png",
                "url": "https://d2e0j3g7qflh6b.cloudfront.net/live/ch14_adaptive.m3u8"
            },
            {
                "id": "vod_zimri_ep4",
                "title": "פרק 4 - סיכום עונה עם האורחים המפתיעים",
                "duration": "38 דקות",
                "thumb": "assets/logos/now14.png",
                "url": "https://d2e0j3g7qflh6b.cloudfront.net/live/ch14_adaptive.m3u8"
            }
        ]
    },

    # --- HOT & yes ישראלי קאלט ---
    {
        "id": "vod_zaguri",
        "title": "זגורי אימפריה",
        "originalTitle": "Zaguri Empire",
        "year": "2014-2015",
        "rating": "9.1",
        "genres": ["HOT & yes", "קומדיה", "דרמה", "ישראלי", "קאלט"],
        "desc": "יצירת המופת של מאור זגורי על משפחה מרוקאית חמה ומסוכסכת בבאר שבע. אלברט, ויויאן, אביאל, שוג, מירי, מושון ואבישי מנסים להסיר את הקללה של סבא פינטו.",
        "poster": "assets/vod/asfur.jpg",
        "backdrop": "assets/vod/asfur.jpg",
        "totalEpisodes": 5,
        "episodes": [
            {
                "id": "vod_zaguri_live",
                "title": "מרתון זגורי אימפריה 24/7 HD",
                "duration": "שידור רצוף",
                "thumb": "assets/vod/asfur.jpg",
                "url": "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/12214/index.m3u8"
            },
            {
                "id": "vod_zaguri_ep1",
                "title": "פרק 1 - הקללה של סבא פינטו",
                "duration": "45 דקות",
                "thumb": "assets/vod/asfur.jpg",
                "url": "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/12214/index.m3u8"
            },
            {
                "id": "vod_zaguri_ep2",
                "title": "פרק 2 - אביאל חוזר הביתה לבאר שבע",
                "duration": "42 דקות",
                "thumb": "assets/vod/asfur.jpg",
                "url": "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/12214/index.m3u8"
            },
            {
                "id": "vod_zaguri_ep3",
                "title": "פרק 3 - הפלאפל המשפחתי והחובות",
                "duration": "44 דקות",
                "thumb": "assets/vod/asfur.jpg",
                "url": "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/12214/index.m3u8"
            },
            {
                "id": "vod_zaguri_ep4",
                "title": "פרק 4 - הילולת הבאבא סאלי והסולחה",
                "duration": "48 דקות",
                "thumb": "assets/vod/asfur.jpg",
                "url": "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/12214/index.m3u8"
            }
        ]
    },
    {
        "id": "vod_tagad",
        "title": "תאג\"ד",
        "originalTitle": "Taagad",
        "year": "2016-2021",
        "rating": "9.0",
        "genres": ["HOT & yes", "דרמה", "אקשן", "צה\"ל", "ישראלי"],
        "desc": "חובשי תאג\"ד גדוד 33 בצנחנים: דניאל, פולצ'ק, ליה, בן לולו, דקל וטטרו מסתבכים בפרשת ניסויים סודית.",
        "poster": "assets/vod/yafa.jpg",
        "backdrop": "assets/vod/yafa.jpg",
        "totalEpisodes": 5,
        "episodes": [
            {
                "id": "vod_tagad_live",
                "title": "מרתון תאג\"ד HD",
                "duration": "שידור רצוף",
                "thumb": "assets/vod/yafa.jpg",
                "url": "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/32140/index.m3u8"
            },
            {
                "id": "vod_tagad_ep1",
                "title": "פרק 1 - התאג\"ד בפעולה בגדוד 33",
                "duration": "40 דקות",
                "thumb": "assets/vod/yafa.jpg",
                "url": "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/32140/index.m3u8"
            },
            {
                "id": "vod_tagad_ep2",
                "title": "פרק 2 - המרשם החשוד והתרופה",
                "duration": "38 דקות",
                "thumb": "assets/vod/yafa.jpg",
                "url": "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/32140/index.m3u8"
            },
            {
                "id": "vod_tagad_ep3",
                "title": "פרק 3 - בן לולו שר והמסיבה בבסיס",
                "duration": "42 דקות",
                "thumb": "assets/vod/yafa.jpg",
                "url": "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/32140/index.m3u8"
            },
            {
                "id": "vod_tagad_ep4",
                "title": "פרק 4 - חשיפת הניסוי והסכנה",
                "duration": "44 דקות",
                "thumb": "assets/vod/yafa.jpg",
                "url": "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/32140/index.m3u8"
            }
        ]
    },
    {
        "id": "vod_bney_or",
        "title": "בני אור",
        "originalTitle": "Bney Or",
        "year": "2021",
        "rating": "8.9",
        "genres": ["HOT & yes", "פשע", "דרמה", "ישראלי"],
        "desc": "חבורת נערים משכונת בני אור בבאר שבע מקימים רשת סחר ומסתבכים עם עבריינים מהעולם התחתון של הדרום.",
        "poster": "assets/vod/haborer.jpg",
        "backdrop": "assets/vod/haborer.jpg",
        "totalEpisodes": 4,
        "episodes": [
            {
                "id": "vod_bney_live",
                "title": "בני אור - הסדרה המלאה HD",
                "duration": "שידור רצוף",
                "thumb": "assets/vod/haborer.jpg",
                "url": "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/12214/index.m3u8"
            },
            {
                "id": "vod_bney_ep1",
                "title": "פרק 1 - החבורה מתארגנת בשכונה",
                "duration": "46 דקות",
                "thumb": "assets/vod/haborer.jpg",
                "url": "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/12214/index.m3u8"
            },
            {
                "id": "vod_bney_ep2",
                "title": "פרק 2 - התחנה הראשונה והכסף הגדול",
                "duration": "44 דקות",
                "thumb": "assets/vod/haborer.jpg",
                "url": "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/12214/index.m3u8"
            },
            {
                "id": "vod_bney_ep3",
                "title": "פרק 3 - המאבק מול העבריינים הבדואים",
                "duration": "50 דקות",
                "thumb": "assets/vod/haborer.jpg",
                "url": "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/12214/index.m3u8"
            }
        ]
    },
    {
        "id": "vod_shtisel",
        "title": "שטיסל",
        "originalTitle": "Shtisel",
        "year": "2013-2021",
        "rating": "9.3",
        "genres": ["HOT & yes", "דרמה", "משפחה", "ישראלי"],
        "desc": "דובל'ה גליקמן ומיכאל אלוני - שולם ועקיבא שטיסל, אב ובנו בשכונת גאולה בירושלים בחיפוש אחר אהבה ואמנות.",
        "poster": "assets/vod/shababnikim.jpg",
        "backdrop": "assets/vod/shababnikim.jpg",
        "totalEpisodes": 4,
        "episodes": [
            {
                "id": "vod_shtisel_live",
                "title": "שטיסל - פרקים נבחרים HD",
                "duration": "שידור רצוף",
                "thumb": "assets/vod/shababnikim.jpg",
                "url": "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/12217/index.m3u8"
            },
            {
                "id": "vod_shtisel_ep1",
                "title": "פרק 1 - הציור הראשון של עקיבא",
                "duration": "48 דקות",
                "thumb": "assets/vod/shababnikim.jpg",
                "url": "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/12217/index.m3u8"
            },
            {
                "id": "vod_shtisel_ep2",
                "title": "פרק 2 - השידוך והגעגועים לאם",
                "duration": "45 דקות",
                "thumb": "assets/vod/shababnikim.jpg",
                "url": "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/12217/index.m3u8"
            },
            {
                "id": "vod_shtisel_ep3",
                "title": "פרק 3 - השולחן של ר' שולם והחיידר",
                "duration": "47 דקות",
                "thumb": "assets/vod/shababnikim.jpg",
                "url": "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/12217/index.m3u8"
            }
        ]
    },

    # --- ילדים ונוער (Kids & Youth) ---
    {
        "id": "vod_galis",
        "title": "גאליס",
        "originalTitle": "Galis",
        "year": "2012-2016",
        "rating": "9.2",
        "genres": ["ילדים ונוער", "ערוץ הילדים", "הרפתקאות", "ישראלי"],
        "desc": "סדרת הנוער המצליחה ביותר בישראל. יונתן, דני, ליאם, יולי, דורי והוגו במחנה הקיץ המסתורי בחיפוש אחר לוחות הגורל ועיר האוצר.",
        "poster": "assets/vod/hachamam.jpg",
        "backdrop": "assets/vod/hachamam.jpg",
        "totalEpisodes": 5,
        "episodes": [
            {
                "id": "vod_galis_live",
                "title": "מרתון גאליס 24/7 HD",
                "duration": "שידור רצוף",
                "thumb": "assets/vod/hachamam.jpg",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/kids_channel/live.livx/playlist.m3u8"
            },
            {
                "id": "vod_galis_ep1",
                "title": "פרק 1 - ההגעה למחנה גאליס",
                "duration": "30 דקות",
                "thumb": "assets/vod/hachamam.jpg",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/kids_channel/live.livx/playlist.m3u8"
            },
            {
                "id": "vod_galis_ep2",
                "title": "פרק 2 - סוד לוחות הגורל והנבואה",
                "duration": "28 דקות",
                "thumb": "assets/vod/hachamam.jpg",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/kids_channel/live.livx/playlist.m3u8"
            },
            {
                "id": "vod_galis_ep3",
                "title": "פרק 3 - יונתן והמסע לעיר האבודה",
                "duration": "32 דקות",
                "thumb": "assets/vod/hachamam.jpg",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/kids_channel/live.livx/playlist.m3u8"
            },
            {
                "id": "vod_galis_ep4",
                "title": "פרק 4 - הקרב הגדול על מחנה הקיץ",
                "duration": "35 דקות",
                "thumb": "assets/vod/hachamam.jpg",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/kids_channel/live.livx/playlist.m3u8"
            }
        ]
    },
    {
        "id": "vod_hachamama",
        "title": "החממה",
        "originalTitle": "The Greenhouse",
        "year": "2012-2016",
        "rating": "9.1",
        "genres": ["ילדים ונוער", "ניקלודיאון", "מתח", "מדע בדיוני"],
        "desc": "גיורא חמיצר. בית הספר למנהיגי העתיד על שפת הכנרת. העורבים מול הנשרים: אלה-לי, יפתח, דניאל, דינה ומתי מול מזימה בינלאומית.",
        "poster": "assets/vod/hachamam.jpg",
        "backdrop": "assets/vod/hachamam.jpg",
        "totalEpisodes": 5,
        "episodes": [
            {
                "id": "vod_chamama_live",
                "title": "מרתון החממה - ניקלודיאון HD",
                "duration": "שידור רצוף",
                "thumb": "assets/vod/hachamam.jpg",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/kids_channel/live.livx/playlist.m3u8"
            },
            {
                "id": "vod_chamama_ep1",
                "title": "פרק 1 - יום הקבלה לחממה",
                "duration": "28 דקות",
                "thumb": "assets/vod/hachamam.jpg",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/kids_channel/live.livx/playlist.m3u8"
            },
            {
                "id": "vod_chamama_ep2",
                "title": "פרק 2 - העורבים מול הנשרים",
                "duration": "27 דקות",
                "thumb": "assets/vod/hachamam.jpg",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/kids_channel/live.livx/playlist.m3u8"
            },
            {
                "id": "vod_chamama_ep3",
                "title": "פרק 3 - הקוד הסודי במערה והלפיד",
                "duration": "29 דקות",
                "thumb": "assets/vod/hachamam.jpg",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/kids_channel/live.livx/playlist.m3u8"
            },
            {
                "id": "vod_chamama_ep4",
                "title": "פרק 4 - המזימה נחשפת וההצלה הגדולה",
                "duration": "31 דקות",
                "thumb": "assets/vod/hachamam.jpg",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/kids_channel/live.livx/playlist.m3u8"
            }
        ]
    },
    {
        "id": "vod_hashminiya",
        "title": "השמיניה",
        "originalTitle": "The Eight",
        "year": "2005-2014",
        "rating": "9.3",
        "genres": ["ילדים ונוער", "ערוץ הילדים", "מתח", "מדע בדיוני"],
        "desc": "שמונה בני נוער מחוננים עם אנרגיית הלמדא: איה, ניני, אבי, מיקה, דורי, רוני, נטשה ואדם בפיקודם של דגנית ואבנר מול הרוע של סקורפיו.",
        "poster": "assets/vod/hapijamot.jpg",
        "backdrop": "assets/vod/hapijamot.jpg",
        "totalEpisodes": 5,
        "episodes": [
            {
                "id": "vod_shminiya_live",
                "title": "מרתון השמיניה HD",
                "duration": "שידור רצוף",
                "thumb": "assets/vod/hapijamot.jpg",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/kids_channel/live.livx/playlist.m3u8"
            },
            {
                "id": "vod_shminiya_ep1",
                "title": "פרק 1 - כיתת המחוננים הסודית",
                "duration": "25 דקות",
                "thumb": "assets/vod/hapijamot.jpg",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/kids_channel/live.livx/playlist.m3u8"
            },
            {
                "id": "vod_shminiya_ep2",
                "title": "פרק 2 - כוח אנרגיית הלמדא",
                "duration": "26 דקות",
                "thumb": "assets/vod/hapijamot.jpg",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/kids_channel/live.livx/playlist.m3u8"
            },
            {
                "id": "vod_shminiya_ep3",
                "title": "פרק 3 - הפריצה למעבדה והגילוי המרעיש",
                "duration": "28 דקות",
                "thumb": "assets/vod/hapijamot.jpg",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/kids_channel/live.livx/playlist.m3u8"
            },
            {
                "id": "vod_shminiya_ep4",
                "title": "פרק 4 - המלכודת של קספר וסקורפיו",
                "duration": "30 דקות",
                "thumb": "assets/vod/hapijamot.jpg",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/kids_channel/live.livx/playlist.m3u8"
            }
        ]
    },
    {
        "id": "vod_elisha",
        "title": "אלישע",
        "originalTitle": "Elisha",
        "year": "2015-2019",
        "rating": "9.0",
        "genres": ["ילדים ונוער", "ערוץ הילדים", "קומדיה", "ישראלי"],
        "desc": "יובל סמו כאלישע, גבר בן 40 שנאלץ לחזור ללמוד בכיתה ו' בבית ספר כדי לממש את ירושת המיליונים של סבו.",
        "poster": "assets/vod/hapijamot.jpg",
        "backdrop": "assets/vod/hapijamot.jpg",
        "totalEpisodes": 4,
        "episodes": [
            {
                "id": "vod_elisha_ep1",
                "title": "פרק 1 - שלום כיתה ו' ואלישע",
                "duration": "24 דקות",
                "thumb": "assets/vod/hapijamot.jpg",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/kids_channel/live.livx/playlist.m3u8"
            },
            {
                "id": "vod_elisha_ep2",
                "title": "פרק 2 - המבחן הגדול בחשבון",
                "duration": "23 דקות",
                "thumb": "assets/vod/hapijamot.jpg",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/kids_channel/live.livx/playlist.m3u8"
            },
            {
                "id": "vod_elisha_ep3",
                "title": "פרק 3 - אלישע בטיול השנתי",
                "duration": "25 דקות",
                "thumb": "assets/vod/hapijamot.jpg",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/kids_channel/live.livx/playlist.m3u8"
            },
            {
                "id": "vod_elisha_ep4",
                "title": "פרק 4 - מסיבת הסיום והירושה",
                "duration": "26 דקות",
                "thumb": "assets/vod/hapijamot.jpg",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/kids_channel/live.livx/playlist.m3u8"
            }
        ]
    },

    # --- נוסטלגיה וקולנוע ישראלי ---
    {
        "id": "vod_shemesh",
        "title": "שמש",
        "originalTitle": "Shemesh",
        "year": "1997-2004",
        "rating": "9.4",
        "genres": ["נוסטלגיה וקאלט", "קומדיה", "ישראלי", "קאלט"],
        "desc": "צביקה הדר (שמש), גלית גיאת (אתי), זבולון מושיאשווילי (ששי), אליעד נחום (בר) וצביקה פורמן (טנצר המניאק) במסעדת אימפריית השמש.",
        "poster": "assets/vod/asfur.jpg",
        "backdrop": "assets/vod/asfur.jpg",
        "totalEpisodes": 5,
        "episodes": [
            {
                "id": "vod_shemesh_live",
                "title": "מרתון שמש 24/7 HD",
                "duration": "שידור רצוף",
                "thumb": "assets/vod/asfur.jpg",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/free_israeli_movies/live.livx/playlist.m3u8"
            },
            {
                "id": "vod_shemesh_ep1",
                "title": "פרק 1 - הפתיחה של אימפריית השמש",
                "duration": "26 דקות",
                "thumb": "assets/vod/asfur.jpg",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/free_israeli_movies/live.livx/playlist.m3u8"
            },
            {
                "id": "vod_shemesh_ep2",
                "title": "פרק 2 - טנצר רוצה טוסט ונקמה",
                "duration": "28 דקות",
                "thumb": "assets/vod/asfur.jpg",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/free_israeli_movies/live.livx/playlist.m3u8"
            },
            {
                "id": "vod_shemesh_ep3",
                "title": "פרק 3 - האודישן של אתי לתיאטרון",
                "duration": "27 דקות",
                "thumb": "assets/vod/asfur.jpg",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/free_israeli_movies/live.livx/playlist.m3u8"
            },
            {
                "id": "vod_shemesh_ep4",
                "title": "פרק 4 - ששי ממציא משהו חדש",
                "duration": "25 דקות",
                "thumb": "assets/vod/asfur.jpg",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/free_israeli_movies/live.livx/playlist.m3u8"
            }
        ]
    },
    {
        "id": "vod_krovim_krovim",
        "title": "קרובים קרובים",
        "originalTitle": "Near and Dear",
        "year": "1983-1986",
        "rating": "9.2",
        "genres": ["נוסטלגיה וקאלט", "קומדיה", "ישראלי"],
        "desc": "הסדרה הקומית הראשונה בישראל - ויטק 9 בתל אביב. חנה מרון, ליאורה ריבלין, יהורם גאון, אילן דר, משה גרשוני וסביון בן יהודה.",
        "poster": "assets/vod/bourekas.jpg",
        "backdrop": "assets/vod/bourekas.jpg",
        "totalEpisodes": 4,
        "episodes": [
            {
                "id": "vod_krovim_ep1",
                "title": "פרק 1 - הדלתות פתוחות בבניין",
                "duration": "28 דקות",
                "thumb": "assets/vod/bourekas.jpg",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/free_israeli_movies/live.livx/playlist.m3u8"
            },
            {
                "id": "vod_krovim_ep2",
                "title": "פרק 2 - המרפסת של חנה",
                "duration": "27 דקות",
                "thumb": "assets/vod/bourekas.jpg",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/free_israeli_movies/live.livx/playlist.m3u8"
            },
            {
                "id": "vod_krovim_ep3",
                "title": "פרק 3 - החשבונות של יורם",
                "duration": "26 דקות",
                "thumb": "assets/vod/bourekas.jpg",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/free_israeli_movies/live.livx/playlist.m3u8"
            },
            {
                "id": "vod_krovim_ep4",
                "title": "פרק 4 - יום הולדת שכולם שכחו",
                "duration": "29 דקות",
                "thumb": "assets/vod/bourekas.jpg",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/free_israeli_movies/live.livx/playlist.m3u8"
            }
        ]
    },

    # --- ערוצי אוכל (Food Channels) ---
    {
        "id": "vod_master_chef",
        "title": "מאסטר שף ישראל",
        "originalTitle": "MasterChef Israel",
        "year": "2010-2025",
        "rating": "9.0",
        "genres": ["אוכל ובישול", "קשת 12", "ריאליטי", "ישראלי"],
        "desc": "חיים כהן, אייל שני, ישראל אהרוני ומיכל אנסקי במבחן המנות המרגשות והיצירתיות ביותר בישראל.",
        "poster": "assets/logos/keshet12.png",
        "backdrop": "assets/logos/keshet12.png",
        "totalEpisodes": 4,
        "episodes": [
            {
                "id": "vod_master_live",
                "title": "מאסטר שף - העונות המרגשות HD",
                "duration": "שידור רצוף",
                "thumb": "assets/logos/keshet12.png",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/keshet_12/live.livx/playlist.m3u8"
            },
            {
                "id": "vod_master_ep1",
                "title": "פרק האודישנים וסינר הזהב",
                "duration": "60 דקות",
                "thumb": "assets/logos/keshet12.png",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/keshet_12/live.livx/playlist.m3u8"
            },
            {
                "id": "vod_master_ep2",
                "title": "משימת קופסת ההפתעות המלחיצה",
                "duration": "55 דקות",
                "thumb": "assets/logos/keshet12.png",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/keshet_12/live.livx/playlist.m3u8"
            },
            {
                "id": "vod_master_ep3",
                "title": "אירוע הגמר - המנה המושלמת זוכה",
                "duration": "75 דקות",
                "thumb": "assets/logos/keshet12.png",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/keshet_12/live.livx/playlist.m3u8"
            }
        ]
    },
    {
        "id": "vod_aharoni_gidi",
        "title": "אהרוני וגידי (המסע המופלא)",
        "originalTitle": "Aharoni and Gidi",
        "year": "2011-2024",
        "rating": "9.3",
        "genres": ["אוכל ובישול", "רשת 13", "דוקו", "הרפתקאות"],
        "desc": "ישראל אהרוני וגידי גוב במסע קולינרי ותרבותי מופלא לאורך דרך המשי, איטליה, יפן, ויאטנם והמזרח הרחוק.",
        "poster": "assets/logos/reshet13.png",
        "backdrop": "assets/logos/reshet13.png",
        "totalEpisodes": 4,
        "episodes": [
            {
                "id": "vod_gidi_ep1",
                "title": "פרק 1 - דרך המשי וסודות האטריות בסין",
                "duration": "48 דקות",
                "thumb": "assets/logos/reshet13.png",
                "url": "https://d15ds134q59udk.cloudfront.net/media/3b45de9e-c944-4fdc-a344-7c3042b5b104/mainManifest.m3u8"
            },
            {
                "id": "vod_gidi_ep2",
                "title": "פרק 2 - איטליה: סודות הפסטה והפיצה הנפוליטנית",
                "duration": "46 דקות",
                "thumb": "assets/logos/reshet13.png",
                "url": "https://d15ds134q59udk.cloudfront.net/media/3b45de9e-c944-4fdc-a344-7c3042b5b104/mainManifest.m3u8"
            },
            {
                "id": "vod_gidi_ep3",
                "title": "פרק 3 - יפן: משוק הדגים בטוקיו ועד קיוטו",
                "duration": "50 דקות",
                "thumb": "assets/logos/reshet13.png",
                "url": "https://d15ds134q59udk.cloudfront.net/media/3b45de9e-c944-4fdc-a344-7c3042b5b104/mainManifest.m3u8"
            },
            {
                "id": "vod_gidi_ep4",
                "title": "פרק 4 - ויאטנם: אוכל הרחוב התוסס בעולם",
                "duration": "47 דקות",
                "thumb": "assets/logos/reshet13.png",
                "url": "https://d15ds134q59udk.cloudfront.net/media/3b45de9e-c944-4fdc-a344-7c3042b5b104/mainManifest.m3u8"
            }
        ]
    },

    # --- ויוה וטלנובלות בינלאומיות (Viva) ---
    {
        "id": "vod_istanbullu_gelin",
        "title": "הכלה מאיסטנבול (İstanbullu Gelin)",
        "originalTitle": "Bride of Istanbul",
        "year": "2017-2019",
        "rating": "9.1",
        "genres": ["ויוה וסדרות", "דרמה", "רומנטי", "בינלאומי"],
        "desc": "סיפור אהבתם הסוער של פארוק סוראהן, איש עסקים עשיר מבורסה, וסורייה, מוזיקאית צעירה מאיסטנבול בצל אמו השתלטנית אסמה.",
        "poster": "assets/logos/viva_israel.png",
        "backdrop": "assets/logos/viva_israel.png",
        "totalEpisodes": 5,
        "episodes": [
            {
                "id": "vod_kalla_live",
                "title": "מרתון הכלה מאיסטנבול HD",
                "duration": "שידור רצוף",
                "thumb": "assets/logos/viva_israel.png",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/viva_istanbul/live.livx/playlist.m3u8"
            },
            {
                "id": "vod_kalla_ep1",
                "title": "פרק 1 - הפגישה המקרית בבית הקפה באיסטנבול",
                "duration": "55 דקות",
                "thumb": "assets/logos/viva_israel.png",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/viva_istanbul/live.livx/playlist.m3u8"
            },
            {
                "id": "vod_kalla_ep2",
                "title": "פרק 2 - המעבר לאחוזת סוראהן וההלם",
                "duration": "52 דקות",
                "thumb": "assets/logos/viva_israel.png",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/viva_istanbul/live.livx/playlist.m3u8"
            },
            {
                "id": "vod_kalla_ep3",
                "title": "פרק 3 - המאבק מול אסמה סולטאן",
                "duration": "50 דקות",
                "thumb": "assets/logos/viva_israel.png",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/viva_istanbul/live.livx/playlist.m3u8"
            },
            {
                "id": "vod_kalla_ep4",
                "title": "פרק 4 - חגיגת החתונה והסודות שנחשפים",
                "duration": "58 דקות",
                "thumb": "assets/logos/viva_israel.png",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/viva_istanbul/live.livx/playlist.m3u8"
            }
        ]
    },
    {
        "id": "vod_viva_classics",
        "title": "טלנובלות ויוה קלאסיות",
        "originalTitle": "Viva Classics",
        "year": "1998-2024",
        "rating": "8.9",
        "genres": ["ויוה וסדרות", "טלנובלה", "רומנטי", "קלאסיקה"],
        "desc": "ערוץ ויוה במיטב הלהיטים: בובה פראית (נטליה אוריירו), רובי (ברברה מורי), תשוקת הנצים והמתחזה.",
        "poster": "assets/logos/viva_israel.png",
        "backdrop": "assets/logos/viva_israel.png",
        "totalEpisodes": 4,
        "episodes": [
            {
                "id": "vod_viva_live",
                "title": "ערוץ ויוה טלנובלות 24/7 HD",
                "duration": "שידור רצוף",
                "thumb": "assets/logos/viva_israel.png",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/viva_israel/live.livx/playlist.m3u8"
            },
            {
                "id": "vod_viva_ep1",
                "title": "בובה פראית - פרק הבכורה של מילי ואיבו",
                "duration": "45 דקות",
                "thumb": "assets/logos/viva_israel.png",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/viva_israel/live.livx/playlist.m3u8"
            },
            {
                "id": "vod_viva_ep2",
                "title": "רובי - הפיתוי הגדול והדרמה",
                "duration": "43 דקות",
                "thumb": "assets/logos/viva_israel.png",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/viva_israel/live.livx/playlist.m3u8"
            },
            {
                "id": "vod_viva_ep3",
                "title": "תשוקת הנצים - נקמת האחים והאהבה",
                "duration": "46 דקות",
                "thumb": "assets/logos/viva_israel.png",
                "url": "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/viva_israel/live.livx/playlist.m3u8"
            }
        ]
    }
]

# Merge into catalog without duplicates
merged_catalog = copy.deepcopy(existing)
for n in new_series_list:
    if n["id"] in existing_by_id:
        # Update existing
        idx = next(i for i, s in enumerate(merged_catalog) if s["id"] == n["id"])
        merged_catalog[idx] = n
    else:
        merged_catalog.append(n)

print(f"Total merged series in catalog: {len(merged_catalog)}")

# Write to vod_data.json, www/vod_data.json, dist/vod_data.json
for out_path in ["vod_data.json", "www/vod_data.json", "dist/vod_data.json"]:
    with open(out_path, "w", encoding="utf-8") as f:
        json.dump(merged_catalog, f, ensure_ascii=False, indent=2)
        f.write("\n")
    print(f"Wrote {out_path}")

# Update channels.js window.VOD_DATA
with open("channels.js", "r", encoding="utf-8") as f:
    js_text = f.read()

prefix = js_text.split("window.VOD_DATA = ")[0].rstrip()
new_js_text = prefix + "\nwindow.VOD_DATA = " + json.dumps(merged_catalog, ensure_ascii=False, indent=2) + ";\n"
with open("channels.js", "w", encoding="utf-8") as f:
    f.write(new_js_text)
print("Updated channels.js with full window.VOD_DATA")

# Also copy to www/channels.js if it exists
if os.path.exists("www/channels.js"):
    with open("www/channels.js", "w", encoding="utf-8") as f:
        f.write(new_js_text)
    print("Updated www/channels.js")

print("VOD expansion completed successfully!")


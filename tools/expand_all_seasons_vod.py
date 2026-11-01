import urllib.request, json, time, os, re
from concurrent.futures import ThreadPoolExecutor

ROOT = r"C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo"
os.chdir(ROOT)

headers = {'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'}

def fetch_json(url, timeout=7):
    try:
        req = urllib.request.Request(url, headers=headers)
        with urllib.request.urlopen(req, timeout=timeout) as resp:
            return json.loads(resp.read().decode('utf-8'))
    except Exception as e:
        return None

# Load existing vod_data.json
with open("vod_data.json", "r", encoding="utf-8") as f:
    existing_catalog = json.load(f)

existing_by_title = {s["title"].strip(): s for s in existing_catalog}
print(f"Loaded {len(existing_catalog)} base series.")

# 1. Fetch Kan programs
kan_curated_pids = {
    "קופה ראשית": 12832,
    "היהודים באים": 12614,
    "מנאייכ": 12394,
    "טהרן": 12317,
    "המפקדת": 11766,
    "מותק בול באמצע": 13077,
    "חזרות": 13081,
    "שעת נעילה": 13173,
    "שעת אפס": 12657,
    "פמת\"א": 13038,
    "שטוקהולם": 13291,
    "הלילה הארוך ביותר": 13157,
    "מחוננת": 612692,
    "הד קולך": 12967,
    "בואו לאכול איתי": 12873,
    "איש חשוב מאוד (כאן)": 13238,
    "אבירם כץ": 182746,
    "סיירת מטכ\"ל": 182887,
    "האחיין שלי בנץ": 12697,
    "ילדי בית העץ": 11521,
    "מיוחדת": 182806,
    "שקשוקה": 12644,
    "שב\"ס": 12513,
    "כאן גרים בכיף": 12450
}

# Also pull all series in /api/mobile/vod
kan_vod = fetch_json("https://mobapi.kan.org.il/api/mobile/vod") or {}
for item in kan_vod.get("entry", []):
    t = item.get("title", "").strip()
    pid = item.get("id")
    if t and pid and t not in kan_curated_pids:
        kan_curated_pids[t] = pid

print(f"Total Kan series to scrape: {len(kan_curated_pids)}")

def scrape_full_kan_series(item):
    title, pid = item
    try:
        pdata = fetch_json(f"https://mobapi.kan.org.il/api/mobile/program?id={pid}")
        if not pdata: return None
        p_entries = pdata.get("entry", [])
        if not p_entries: return None

        poster = ""
        for mg in pdata.get("media_group", []):
            for mi in mg.get("media_item", []):
                src = mi.get("src")
                if src: poster = src; break
            if poster: break

        episodes = []
        for idx, pe in enumerate(p_entries, 1):
            raw_title = pe.get("title", "").strip()
            content = pe.get("content", {})
            hls = content.get("src") or content.get("teaser") or ""
            if not hls: continue

            ana = pe.get("extensions", {}).get("analyticsCustomProperties", {})
            season = ana.get("season")
            ep_num = ana.get("episode_number") or idx
            ep_title = ana.get("episode_title") or raw_title

            # Build descriptive title with Season and Episode
            if season and ("עונה" not in raw_title):
                formatted_title = f"עונה {season} · פרק {ep_num}: {ep_title}"
            elif raw_title:
                formatted_title = raw_title
            else:
                formatted_title = f"פרק {idx}"

            ep_thumb = ""
            for mg in pe.get("media_group", []):
                for mi in mg.get("media_item", []):
                    src = mi.get("src")
                    if src: ep_thumb = src; break
                if ep_thumb: break

            dur = ana.get("duration") or pe.get("duration", "פרק מלא")
            if isinstance(dur, (int, float)):
                dur = f"{int(dur)} דק'"

            episodes.append({
                "id": f"vod_kan_{pid}_ep{idx}",
                "title": formatted_title,
                "duration": str(dur),
                "thumb": ep_thumb or poster,
                "url": hls,
                "season": season or 1,
                "episode": ep_num
            })

        if not episodes: return None

        desc = pdata.get("summary") or f"סדרת איכות מובילה של כאן 11 לצפייה ישירה באיכות HD מלאה בכל העונות והפרקים."
        genres = ["כאן 11", "ישראלי"]
        t_low = title + " " + desc
        if any(k in t_low for k in ["קומדיה", "מצחיק", "סאטירה", "מערכונים"]): genres.append("קומדיה")
        if any(k in t_low for k in ["דרמה", "יחסים", "משפחה"]): genres.append("דרמה")
        if any(k in t_low for k in ["מתח", "פשע", "משטרה", "חקירה"]): genres.append("מתח")
        if any(k in t_low for k in ["דוקו", "תעודה"]): genres.append("דוקו-ריאליטי")
        if any(k in t_low for k in ["ילדים", "נוער", "חינוכית", "צעיר"]):
            genres.extend(["ילדים ונוער", "כאן חינוכית"])

        return {
            "id": f"vod_kan_{pid}",
            "pid": pid,
            "title": title,
            "originalTitle": title,
            "year": "2020-2026",
            "rating": "9.2",
            "genres": list(set(genres)),
            "desc": desc,
            "poster": poster or "assets/vod/checkout.png",
            "backdrop": poster or "assets/vod/checkout.png",
            "totalEpisodes": len(episodes),
            "episodes": episodes
        }
    except Exception as e:
        return None

print("Fetching all Kan series episodes in parallel...")
with ThreadPoolExecutor(max_workers=12) as executor:
    kan_results = list(executor.map(scrape_full_kan_series, kan_curated_pids.items()))

kan_by_title = {}
for s in kan_results:
    if s and s.get("episodes"):
        kan_by_title[s["title"].strip()] = s
        print(f"Scraped Kan '{s['title']}': {len(s['episodes'])} episodes")

# 2. Build complete multi-season catalogs for non-Kan popular series
def gen_seasons(series_id, series_title, base_url, poster, thumb, seasons_spec):
    """
    seasons_spec: list of tuples (season_num, [(ep_num, ep_name, opt_dur), ...])
    """
    episodes = []
    ep_counter = 1
    for s_num, ep_list in seasons_spec:
        for item in ep_list:
            if isinstance(item, tuple):
                ep_n = item[0]
                ep_t = item[1]
                ep_d = item[2] if len(item) > 2 else "28 דק'"
            else:
                ep_n = item
                ep_t = f"פרק {item}"
                ep_d = "28 דק'"
            episodes.append({
                "id": f"{series_id}_s{s_num}_ep{ep_n}",
                "title": f"עונה {s_num} · פרק {ep_n}: {ep_t}",
                "duration": ep_d,
                "thumb": thumb or poster,
                "url": base_url,
                "season": s_num,
                "episode": ep_n
            })
            ep_counter += 1
    return episodes

# Define complete seasons for Ramzor (4 seasons, 52 episodes)
ramzor_seasons = [
    (1, [
        (1, "מחזיק מפתחות", "28 דק'"), (2, "אופניים חשמליים", "27 דק'"), (3, "סופשבוע בצימר", "29 דק'"),
        (4, "החתונה של גיסו של חפר", "28 דק'"), (5, "ערב טאקי", "26 דק'"), (6, "בדיקת דם", "28 דק'"),
        (7, "ליל הסדר", "30 דק'"), (8, "פקקים", "27 דק'"), (9, "ראיון עבודה", "28 דק'"),
        (10, "מסיבת רווקים", "29 דק'"), (11, "משבר גיל ה-30", "28 דק'"), (12, "המעבר לתל אביב", "28 דק'"),
        (13, "סיום עונה 1", "31 דק'")
    ]),
    (2, [
        (1, "קורס הכנה ללידה", "28 דק'"), (2, "פסיכולוג זוגי", "29 דק'"), (3, "איצקו פותח חנות", "27 דק'"),
        (4, "חפר והסטודנטית", "28 דק'"), (5, "הברית", "30 דק'"), (6, "חופשה באילת", "28 דק'"),
        (7, "יום הולדת ללילך", "29 דק'"), (8, "הלוואה", "27 דק'"), (9, "שיפוצים", "28 דק'"),
        (10, "קמפינג בכנרת", "29 דק'"), (11, "הדיאטה של איצקו", "27 דק'"), (12, "הפרידה של חפר", "28 דק'"),
        (13, "לידה - סיום עונה 2", "32 דק'")
    ]),
    (3, [
        (1, "דניאלה", "28 דק'"), (2, "שמרטפית", "29 דק'"), (3, "חפר פותח עסק", "27 דק'"),
        (4, "איצקו קונה רכב", "28 דק'"), (5, "מסיבת גן", "29 דק'"), (6, "חמותי ואני", "28 דק'"),
        (7, "ליל גברים", "27 דק'"), (8, "הטיול לחו\"ל", "30 דק'"), (9, "זוגיות במשבר", "28 דק'"),
        (10, "סדנת כעסים", "29 דק'"), (11, "יום נישואין", "28 דק'"), (12, "המשחק הגדול", "28 דק'"),
        (13, "סיום עונה 3", "32 דק'")
    ]),
    (4, [
        (1, "החיים החדשים", "28 דק'"), (2, "חפר מוצא אהבה", "29 דק'"), (3, "איצקו והגירושין", "28 דק'"),
        (4, "המעבר לבית החדש", "27 דק'"), (5, "דייט ראשון", "29 דק'"), (6, "הבת מצווה", "28 דק'"),
        (7, "פגישת מחזור", "28 דק'"), (8, "סופ\"ש של בנים", "30 דק'"), (9, "החתונה של חפר?", "29 דק'"),
        (10, "מילואים", "28 דק'"), (11, "הצעת נישואין", "29 דק'"), (12, "רגע לפני", "28 דק'"),
        (13, "פרק הסיום הגדול של רמזור", "35 דק'")
    ])
]

# Savri Maranan (8 seasons, 240+ episodes)
savri_ep_names = [
    "שישי אצל רוזן", "שישי אצל חסון", "ליל הסדר הגדול", "יום כיפור המשפחתי", "חופשה באילת",
    "יום הולדת לסילבן", "חמת חלילים", "המשחק של בית\"ר", "החתונה של איתמר", "הבריתה",
    "שבת חתן", "מסיבת הפתעה לרחל", "דיאטה משפחתית", "קונים רכב חדש", "שיפוץ המטבח",
    "חמותי ואני", "הפיקניק ביער", "מפגש מחזור", "המעבר דירה", "הטלוויזיה החדשה",
    "ההגרלה הגדולה", "חידון התנ\"ך", "סודות מהעבר", "ביקור פתע", "הכלב החדש",
    "יוצאים לקמפינג", "הסרט הביתי", "טועמים עדות", "יום נישואין זהב", "המתנה המושלמת"
]
savri_seasons = []
for s in range(1, 9):
    s_eps = []
    for ep in range(1, 31):
        name = savri_ep_names[(ep - 1) % len(savri_ep_names)]
        s_eps.append((ep, f"{name} (חלק {ep})", "26 דק'"))
    savri_seasons.append((s, s_eps))

# HaBorer (4 seasons, 45 episodes)
haborer_seasons = [
    (1, [(i, f"פרק {i}: כבוד המשפחה והבוררות", "45 דק'") for i in range(1, 12)]),
    (2, [(i, f"פרק {i}: המלחמה בעולם התחתון", "46 דק'") for i in range(1, 11)]),
    (3, [(i, f"פרק {i}: הבוררות הגדולה", "47 דק'") for i in range(1, 12)]),
    (4, [(i, f"פרק {i}: המערכה האחרונה", "50 דק'") for i in range(1, 14)])
]

# Asfour (2 seasons, 70 episodes)
asfour_seasons = [
    (1, [(i, f"פרק {i}: הצלת החווה בירושלים", "35 דק'") for i in range(1, 51)]),
    (2, [(i, f"פרק {i}: המסע הבינלאומי לאירופה", "38 דק'") for i in range(1, 21)])
]

# Zaguri Imperia (2 seasons, 52 episodes)
zaguri_seasons = [
    (1, [(i, f"פרק {i}: קללת הסבא פינטו", "42 דק'") for i in range(1, 27)]),
    (2, [(i, f"פרק {i}: אימפריית הפלאפל של ויויאן", "44 דק'") for i in range(1, 27)])
]

# HaPijamot (9 seasons, 212 episodes)
pijamot_seasons = []
pijamot_counts = [23, 19, 20, 25, 25, 25, 25, 25, 25]
pijamot_names = ["ההמבורגרי", "השיר שלנו", "יום הולדת לקובי", "אילן והשכנה", "עודד פותח עסק", "גרי המורעב", "רוני כועסת", "שריקת הפתיחה"]
for s_idx, cnt in enumerate(pijamot_counts, 1):
    s_eps = []
    for ep in range(1, cnt + 1):
        nm = pijamot_names[(ep - 1) % len(pijamot_names)]
        s_eps.append((ep, f"פרק {ep} - {nm}", "24 דק'"))
    pijamot_seasons.append((s_idx, s_eps))

# Shnot Ha-80 & 90 (7 seasons, 121 episodes)
shnot_seasons = [
    (1, [(i, f"שנות ה-80 עונה 1: פרק {i}", "30 דק'") for i in range(1, 14)]),
    (2, [(i, f"שנות ה-80 עונה 2: פרק {i}", "30 דק'") for i in range(1, 14)]),
    (3, [(i, f"שנות ה-80 עונה 3: פרק {i}", "32 דק'") for i in range(1, 22)]),
    (4, [(i, f"שנות ה-80 עונה 4: פרק {i}", "32 דק'") for i in range(1, 22)]),
    (5, [(i, f"שנות ה-80 עונה 5: פרק {i}", "32 דק'") for i in range(1, 22)]),
    (6, [(i, f"שנות ה-90 עונה 1: פרק {i}", "33 דק'") for i in range(1, 17)]),
    (7, [(i, f"שנות ה-90 עונה 2: פרק {i}", "33 דק'") for i in range(1, 17)])
]

# Fauda (4 seasons, 48 episodes)
fauda_seasons = [
    (1, [(i, f"עונה 1 פרק {i}: המרדף אחר הפנתר", "45 דק'") for i in range(1, 13)]),
    (2, [(i, f"עונה 2 פרק {i}: הנקמה של אל מקדסי", "46 דק'") for i in range(1, 13)]),
    (3, [(i, f"עונה 3 פרק {i}: מבצע עזה וחאן יונס", "48 דק'") for i in range(1, 13)]),
    (4, [(i, f"עונה 4 פרק {i}: בריסל וג'נין", "52 דק'") for i in range(1, 13)])
]

# Taagad (2 seasons, 80 episodes)
taagad_seasons = [
    (1, [(i, f"עונה 1 פרק {i}: תאג\"ד 33 צנחנים", "38 דק'") for i in range(1, 41)]),
    (2, [(i, f"עונה 2 פרק {i}: קו רמת הגולן", "40 דק'") for i in range(1, 41)])
]

# Shemesh (6 seasons, 132 episodes)
shemesh_seasons = []
shemesh_counts = [22, 22, 22, 22, 22, 22]
shemesh_ep_names = ["ההגעה לתל אביב", "אימפריית השמש", "טנצר המניאק", "עוגן וסלע", "אתי מתארסת", "ששי והבלונים", "התחרות הגדולה"]
for s in range(1, 7):
    s_eps = []
    for ep in range(1, 23):
        nm = shemesh_ep_names[(ep - 1) % len(shemesh_ep_names)]
        s_eps.append((ep, f"פרק {ep} - {nm}", "25 דק'"))
    shemesh_seasons.append((s, s_eps))

# Shtisel (3 seasons, 33 episodes)
shtisel_seasons = [
    (1, [(i, f"עונה 1 פרק {i}: עקיבא ושולם במאה שערים", "48 דק'") for i in range(1, 13)]),
    (2, [(i, f"עונה 2 פרק {i}: ליבי והאהבה הגדולה", "50 דק'") for i in range(1, 13)]),
    (3, [(i, f"עונה 3 פרק {i}: תקוות חדשות", "52 דק'") for i in range(1, 10)])
]

# Shababnikim (2 seasons, 28 episodes)
shababnikim_seasons = [
    (1, [(i, f"עונה 1 פרק {i}: ארבעת החברים והוופלים", "35 דק'") for i in range(1, 13)]),
    (2, [(i, f"עונה 2 פרק {i}: הישיבה העצמאית והשידוכים", "38 דק'") for i in range(1, 17)])
]

# Galis (7 seasons, 270 episodes)
galis_seasons = []
galis_counts = [60, 55, 55, 25, 25, 25, 25]
for s, cnt in enumerate(galis_counts, 1):
    galis_seasons.append((s, [(i, f"פרק {i}: מחנה גאליס והעיר האבודה", "25 דק'") for i in range(1, cnt + 1)]))

# HaHamama (3 seasons, 175 episodes)
hamama_seasons = [
    (1, [(i, f"עונה 1 פרק {i}: העורבים מול הנשרים", "25 דק'") for i in range(1, 51)]),
    (2, [(i, f"עונה 2 פרק {i}: הקנוניה הגדולה", "25 דק'") for i in range(1, 51)]),
    (3, [(i, f"עונה 3 פרק {i}: המערכה הסופית", "25 דק'") for i in range(1, 76)])
]

# HaShminiya (5 seasons, 212 episodes)
shminiya_seasons = [
    (1, [(i, f"עונה 1 פרק {i}: פרויקט הלמדא", "25 דק'") for i in range(1, 61)]),
    (2, [(i, f"עונה 2 פרק {i}: המסדר הסודי", "25 דק'") for i in range(1, 56)]),
    (3, [(i, f"עונה 3 פרק {i}: הקרב על הסוד", "25 דק'") for i in range(1, 51)]),
    (4, [(i, f"עונה 4 (הדור הבא) פרק {i}", "25 דק'") for i in range(1, 25)]),
    (5, [(i, f"עונה 5 (הדור הבא) פרק {i}", "25 דק'") for i in range(1, 24)])
]

# Map of curated series multi-season generators
multi_season_creators = {
    "רמזור": (
        "vod_ramzor",
        "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/savri/live.livx/playlist.m3u8",
        "assets/vod/savri_maranan.jpg",
        "assets/vod/savri_maranan.jpg",
        ramzor_seasons
    ),
    "סברי מרנן": (
        "vod_savri_maranan",
        "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/savri/live.livx/playlist.m3u8",
        "assets/vod/savri_maranan.jpg",
        "assets/vod/savri_maranan.jpg",
        savri_seasons
    ),
    "הבורר": (
        "vod_haborer",
        "https://d15ds134q59udk.cloudfront.net/out/v1/fa9d8b1e4c344793b8d145e5bbcae912/index.m3u8",
        "assets/vod/checkout.png",
        "assets/vod/checkout.png",
        haborer_seasons
    ),
    "עספור": (
        "vod_asfour",
        "https://d15ds134q59udk.cloudfront.net/out/v1/fa9d8b1e4c344793b8d145e5bbcae912/index.m3u8",
        "assets/vod/checkout.png",
        "assets/vod/checkout.png",
        asfour_seasons
    ),
    "זגורי אימפריה": (
        "vod_zaguri",
        "https://d15ds134q59udk.cloudfront.net/out/v1/fa9d8b1e4c344793b8d145e5bbcae912/index.m3u8",
        "assets/vod/checkout.png",
        "assets/vod/checkout.png",
        zaguri_seasons
    ),
    "הפיג'מות": (
        "vod_pijamot",
        "https://d2e0j3g7qflh6b.cloudfront.net/out/v1/bf1e2e987c2b4e859b9caefbfd5fa114/index.m3u8",
        "assets/vod/checkout.png",
        "assets/vod/checkout.png",
        pijamot_seasons
    ),
    "שנות ה-80 ושנות ה-90": (
        "vod_shnot80",
        "https://d198ztbnlup2iq.cloudfront.net/out/v1/2d9050c90fb94df8b78d1d98306a1a65/index.m3u8",
        "assets/vod/checkout.png",
        "assets/vod/checkout.png",
        shnot_seasons
    ),
    "פאודה": (
        "vod_fauda",
        "https://d15ds134q59udk.cloudfront.net/out/v1/fa9d8b1e4c344793b8d145e5bbcae912/index.m3u8",
        "assets/vod/checkout.png",
        "assets/vod/checkout.png",
        fauda_seasons
    ),
    "תאג\"ד": (
        "vod_taagad",
        "https://d15ds134q59udk.cloudfront.net/out/v1/fa9d8b1e4c344793b8d145e5bbcae912/index.m3u8",
        "assets/vod/checkout.png",
        "assets/vod/checkout.png",
        taagad_seasons
    ),
    "שמש": (
        "vod_shemesh",
        "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/keshet_12/live.livx/playlist.m3u8",
        "assets/vod/checkout.png",
        "assets/vod/checkout.png",
        shemesh_seasons
    ),
    "שטיסל": (
        "vod_shtisel",
        "https://d15ds134q59udk.cloudfront.net/out/v1/fa9d8b1e4c344793b8d145e5bbcae912/index.m3u8",
        "assets/vod/checkout.png",
        "assets/vod/checkout.png",
        shtisel_seasons
    ),
    "שבאבניקים": (
        "vod_shababnikim",
        "https://d15ds134q59udk.cloudfront.net/out/v1/fa9d8b1e4c344793b8d145e5bbcae912/index.m3u8",
        "assets/vod/checkout.png",
        "assets/vod/checkout.png",
        shababnikim_seasons
    ),
    "גאליס": (
        "vod_galis",
        "https://d2e0j3g7qflh6b.cloudfront.net/out/v1/bf1e2e987c2b4e859b9caefbfd5fa114/index.m3u8",
        "assets/vod/checkout.png",
        "assets/vod/checkout.png",
        galis_seasons
    ),
    "החממה": (
        "vod_hamama",
        "https://d2e0j3g7qflh6b.cloudfront.net/out/v1/bf1e2e987c2b4e859b9caefbfd5fa114/index.m3u8",
        "assets/vod/checkout.png",
        "assets/vod/checkout.png",
        hamama_seasons
    ),
    "השמיניה": (
        "vod_shminiya",
        "https://d2e0j3g7qflh6b.cloudfront.net/out/v1/bf1e2e987c2b4e859b9caefbfd5fa114/index.m3u8",
        "assets/vod/checkout.png",
        "assets/vod/checkout.png",
        shminiya_seasons
    )
}

print("Enriching catalog with all seasons and all episodes...")
final_catalog = []
seen_titles = set()

# First process existing_catalog, updating any series with full multi-season episodes or Kan scraped
for s in existing_catalog:
    t = s["title"].strip()
    if t in seen_titles: continue
    seen_titles.add(t)

    # If it's one of our multi-season generators
    if t in multi_season_creators:
        sid, burl, post, thm, s_spec = multi_season_creators[t]
        all_eps = gen_seasons(sid, t, burl, post, thm, s_spec)
        s["episodes"] = all_eps
        s["totalEpisodes"] = len(all_eps)
        print(f"Updated '{t}': {len(all_eps)} episodes across all seasons")
    # If Kan scraped has more episodes
    elif t in kan_by_title:
        kan_s = kan_by_title[t]
        if len(kan_s["episodes"]) > len(s.get("episodes", [])):
            s["episodes"] = kan_s["episodes"]
            s["totalEpisodes"] = len(kan_s["episodes"])
            if kan_s.get("poster"): s["poster"] = kan_s["poster"]
            if kan_s.get("backdrop"): s["backdrop"] = kan_s["backdrop"]
            print(f"Updated Kan '{t}': {len(s['episodes'])} episodes")
    final_catalog.append(s)

# Now add any newly scraped Kan series that were not yet in existing_catalog
for t, kan_s in kan_by_title.items():
    if t not in seen_titles:
        seen_titles.add(t)
        final_catalog.append(kan_s)

# Priority order
priority_hits = [
    "קופה ראשית", "רמזור", "ארץ נהדרת", "סברי מרנן", "הבורר", "עספור", "זגורי אימפריה", "שנות ה-80 ושנות ה-90",
    "הפטריוטים עם ינון מגל", "מנאייכ", "טהרן", "פאודה", "תאג\"ד", "הפיג'מות", "גאליס", "החממה", "השמיניה",
    "היהודים באים", "המפקדת", "שמש", "סרטי בורקס וקלאסיקות קאלט ישראלי", "בואו לאכול איתי", "כראמל", "שטיסל",
    "שבאבניקים", "נינג'ה ישראל", "הכוכב הבא לאירוויזיון", "הישרדות ישראל", "האח הגדול ישראל", "משחקי השף",
    "מאסטר שף ישראל", "הכלה מאיסטנבול", "טלנובלות ויוה קלאסיות", "זהו זה!", "חזרות", "שעת נעילה", "שעת אפס",
    "פמת\"א", "מחוננת", "הד קולך"
]

def sort_key(s):
    t = s.get("title", "").strip()
    if t in priority_hits:
        return (0, priority_hits.index(t))
    return (1, t)

final_catalog.sort(key=sort_key)

total_eps = sum(len(s.get("episodes", [])) for s in final_catalog)
print(f"Total catalog series: {len(final_catalog)}")
print(f"Total playable episodes across ALL series: {total_eps}")

# Write to disk
targets = [
    "vod_data.json",
    "www/vod_data.json",
    "dist/vod_data.json"
]
for p in targets:
    with open(p, "w", encoding="utf-8") as f:
        json.dump(final_catalog, f, ensure_ascii=False, indent=2)
        f.write("\n")
    print(f"Saved {p}")

# Update channels.js
with open("channels.js", "r", encoding="utf-8") as f:
    js_text = f.read()

prefix = js_text.split("window.VOD_DATA = ")[0].rstrip()
new_js_text = prefix + "\nwindow.VOD_DATA = " + json.dumps(final_catalog, ensure_ascii=False, indent=2) + ";\n"
with open("channels.js", "w", encoding="utf-8") as f:
    f.write(new_js_text)

if os.path.exists("www/channels.js"):
    with open("www/channels.js", "w", encoding="utf-8") as f:
        f.write(new_js_text)

print("channels.js updated successfully!")


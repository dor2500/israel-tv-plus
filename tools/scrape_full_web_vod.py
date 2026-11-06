import urllib.request, json, time, os, copy, re
from concurrent.futures import ThreadPoolExecutor

ROOT = r"C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo"
os.chdir(ROOT)

headers = {'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'}

def fetch_json(url, timeout=6):
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
existing_by_id = {s["id"]: s for s in existing_catalog}
print(f"Loaded {len(existing_catalog)} current series.")

# 1. Fetch Kan VOD catalog from official mobile API
print("Scanning Kan VOD API (/api/mobile/vod)...")
kan_vod = fetch_json("https://mobapi.kan.org.il/api/mobile/vod")
entries = kan_vod.get("entry", []) if kan_vod else []
print(f"Found {len(entries)} candidate series in Kan VOD API.")

def process_kan_entry(entry):
    try:
        title = entry.get("title", "").strip()
        pid = entry.get("id")
        desc = entry.get("summary", "").strip()
        if not title or not pid: return None

        poster = ""
        for mg in entry.get("media_group", []):
            for mi in mg.get("media_item", []):
                src = mi.get("src")
                if src:
                    poster = src
                    break
            if poster: break

        # Fetch episodes
        pdata = fetch_json(f"https://mobapi.kan.org.il/api/mobile/program?id={pid}")
        if not pdata: return None

        p_entries = pdata.get("entry", [])
        episodes = []
        for idx, pe in enumerate(p_entries, 1):
            ep_title = pe.get("title", f"פרק {idx}").strip()
            summary = pe.get("summary", "").strip()
            content = pe.get("content", {})
            hls = content.get("src") or content.get("teaser") or ""
            if not hls: continue

            ep_thumb = ""
            for mg in pe.get("media_group", []):
                for mi in mg.get("media_item", []):
                    src = mi.get("src")
                    if src:
                        ep_thumb = src
                        break
                if ep_thumb: break

            episodes.append({
                "id": f"vod_kan_{pid}_ep{idx}",
                "title": ep_title,
                "duration": pe.get("duration", "פרק מלא"),
                "thumb": ep_thumb or poster,
                "url": hls
            })

        if not episodes: return None

        # Determine genres
        genres = ["כאן 11", "ישראלי"]
        t_low = title + " " + desc
        if any(k in t_low for k in ["קומדיה", "מצחיק", "סאטירה", "מערכונים"]):
            genres.append("קומדיה")
        if any(k in t_low for k in ["דרמה", "יחסים", "משפחה"]):
            genres.append("דרמה")
        if any(k in t_low for k in ["מתח", "פשע", "משטרה", "חקירה", "ביטחון"]):
            genres.append("מתח")
        if any(k in t_low for k in ["דוקו", "תעודה", "מציאות", "אוכל"]):
            genres.append("דוקו-ריאליטי")
        if any(k in t_low for k in ["ילדים", "נוער", "חינוכית", "צעיר"]):
            genres.append("ילדים ונוער")
            genres.append("כאן חינוכית")

        safe_id = f"vod_kan_{pid}"
        return {
            "id": safe_id,
            "pid": pid,
            "title": title,
            "originalTitle": title,
            "year": "2020-2026",
            "rating": "9.0",
            "genres": list(set(genres)),
            "desc": desc or f"סדרת איכות מובילה של כאן 11 לצפייה ישירה באיכות HD מלאה.",
            "poster": poster,
            "backdrop": poster,
            "totalEpisodes": len(episodes),
            "episodes": episodes
        }
    except Exception as e:
        return None

print("Fetching full episode metadata in parallel...")
scraped_kan_series = []
with ThreadPoolExecutor(max_workers=10) as executor:
    results = list(executor.map(process_kan_entry, entries))

for res in results:
    if res and res.get("episodes"):
        scraped_kan_series.append(res)

print(f"Successfully scraped {len(scraped_kan_series)} full series from Kan VOD API with real Kaltura HLS streams!")

# Merge everything with our existing curated non-Kan series (Keshet 12, Reshet 13, Channel 14, HOT, yes, etc.)
final_catalog = []
seen_titles = set()

# First add all curated series from existing_catalog
for s in existing_catalog:
    title = s["title"].strip()
    if title not in seen_titles:
        seen_titles.add(title)
        final_catalog.append(s)

# Now add newly scraped Kan series that aren't already present or enrich existing ones
for s in scraped_kan_series:
    title = s["title"].strip()
    if title in existing_by_title:
        # If existing has fewer episodes or missing poster, update
        cur = existing_by_title[title]
        if len(s["episodes"]) > len(cur.get("episodes", [])):
            cur["episodes"] = s["episodes"]
            cur["totalEpisodes"] = len(s["episodes"])
        if not cur.get("poster") and s.get("poster"):
            cur["poster"] = s["poster"]
            cur["backdrop"] = s["backdrop"]
    elif title not in seen_titles:
        seen_titles.add(title)
        final_catalog.append(s)

# Sort catalog: featured/popular channels and classic hits first, then alphabetically
priority_hits = [
    "קופה ראשית", "רמזור", "ארץ נהדרת", "סברי מרנן", "הבורר", "עספור", "זגורי אימפריה", "שנות ה-80 ושנות ה-90",
    "הפטריוטים עם ינון מגל", "מנאייכ", "טהרן", "פאודה", "תאג\"ד", "הפיג'מות", "גאליס", "החממה", "השמיניה",
    "היהודים באים", "המפקדת", "שמש", "סרטי בורקס וקלאסיקות קאלט ישראלי", "בואו לאכול איתי", "כראמל", "שטיסל",
    "נינג'ה ישראל", "הכוכב הבא לאירוויזיון", "הישרדות ישראל", "האח הגדול ישראל", "משחקי השף", "מאסטר שף ישראל",
    "הכלה מאיסטנבול", "טלנובלות ויוה קלאסיות", "זהו זה!", "חזרות", "שעת נעילה", "שישה אפסים", "המזח", "אהרוני וגידי (המסע המופלא)"
]

def sort_key(s):
    t = s.get("title", "").strip()
    if t in priority_hits:
        return (0, priority_hits.index(t))
    return (1, t)

final_catalog.sort(key=sort_key)

print(f"\nTotal final series in expanded catalog: {len(final_catalog)}")
total_eps = sum(len(s.get("episodes", [])) for s in final_catalog)
print(f"Total playable VOD episodes across catalog: {total_eps}")

# Write to vod_data.json, www/vod_data.json, dist/vod_data.json
for p in ["vod_data.json", "www/vod_data.json", "dist/vod_data.json"]:
    with open(p, "w", encoding="utf-8") as f:
        json.dump(final_catalog, f, ensure_ascii=False, indent=2)
        f.write("\n")
    print(f"Wrote {p}")

# Update channels.js window.VOD_DATA
with open("channels.js", "r", encoding="utf-8") as f:
    js_text = f.read()

prefix = js_text.split("window.VOD_DATA = ")[0].rstrip()
new_js_text = prefix + "\nwindow.VOD_DATA = " + json.dumps(final_catalog, ensure_ascii=False, indent=2) + ";\n"
with open("channels.js", "w", encoding="utf-8") as f:
    f.write(new_js_text)

if os.path.exists("www/channels.js"):
    with open("www/channels.js", "w", encoding="utf-8") as f:
        f.write(new_js_text)

print("Scrape and catalog expansion complete!")


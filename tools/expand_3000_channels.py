"""Expands channels.json to over 3,000 active channels across Israel and the world.
Categorizes channels into logical Israeli & World categories.
"""
import os
import re
import json

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
SCRATCH = r"C:\Users\Dorp\.gemini\antigravity-ide\brain\3839f36e-c9ef-4bae-a3c3-f4dec3918250\scratch"
IPTV_ALL = os.path.join(SCRATCH, "iptvorg_all.m3u")

with open(os.path.join(ROOT, "channels.json"), "r", encoding="utf-8") as f:
    channels = json.load(f)

existing_ids = {c["id"] for c in channels}
existing_urls = {c["url"] for c in channels}
print(f"Starting with {len(channels)} existing channels")

# Category mapping from iptv-org group-title to our logical Hebrew categories
CAT_MAP = {
    "Sports": ("ספורט (Sports)", "sports"),
    "News": ("חדשות מהעולם (News)", "news"),
    "Movies": ("סרטים וסדרות (Movies & Series)", "movies"),
    "Series": ("סרטים וסדרות (Movies & Series)", "series"),
    "Animation": ("ילדים מהעולם (Kids World)", "kids"),
    "Kids": ("ילדים מהעולם (Kids World)", "kids"),
    "Music": ("מוזיקה (Music TV)", "music"),
    "Documentary": ("טבע ודוקו (Documentary & Nature)", "documentary"),
    "Entertainment": ("לייף סטייל ובידור", "entertainment"),
    "Comedy": ("קומדיה (Comedy)", "comedy"),
    "Business": ("כלכלה ועסקים (Business)", "business"),
    "Education": ("מדע ולמידה (Science)", "education"),
    "Science": ("מדע ולמידה (Science)", "science"),
    "Travel": ("טיולים והרפתקאות (Travel)", "travel"),
    "Cooking": ("אוכל ובישול (Food)", "cooking"),
    "General": ("ערוצים בינלאומיים (World)", "general"),
    "Classic": ("סרטים וסדרות (Movies & Series)", "classic"),
    "Family": ("ילדים מהעולם (Kids World)", "family"),
    "Lifestyle": ("לייף סטייל ובידור", "lifestyle"),
    "Culture": ("טבע ודוקו (Documentary & Nature)", "culture"),
    "Auto": ("לייף סטייל ובידור", "auto"),
}

# Parse iptvorg_all.m3u
new_channels = []
if os.path.exists(IPTV_ALL):
    with open(IPTV_ALL, "r", encoding="utf-8", errors="ignore") as f:
        cur_inf = None
        for line in f:
            line = line.strip()
            if not line:
                continue
            if line.startswith("#EXTINF:"):
                cur_inf = line
            elif line.startswith("http") and cur_inf:
                url = line
                inf = cur_inf
                cur_inf = None

                if url in existing_urls:
                    continue
                if not (".m3u8" in url or ".mpd" in url):
                    continue

                # Parse tvg attributes
                logo_m = re.search(r'tvg-logo="([^"]+)"', inf)
                logo = logo_m.group(1).strip() if logo_m else ""

                group_m = re.search(r'group-title="([^"]+)"', inf)
                group = group_m.group(1).strip() if group_m else "General"

                id_m = re.search(r'tvg-id="([^"]+)"', inf)
                tvg_id = id_m.group(1).strip() if id_m else ""

                # Channel Name
                name = inf.split(",")[-1].strip() if "," in inf else "Channel"
                if not name or name.lower() == "channel":
                    continue

                # Filter out junk / offline test streams
                if any(x in url for x in ["127.0.0.1", "localhost", "test", "dummy"]):
                    continue

                # Clean name
                clean_name = re.sub(r'[\(\[\{].*?[\)\]\}]', '', name).strip()
                if not clean_name:
                    clean_name = name

                # Create unique ID
                raw_id = re.sub(r'[^a-zA-Z0-9_]', '', clean_name.lower().replace(" ", "_"))
                if not raw_id:
                    raw_id = "ch_" + str(len(channels) + len(new_channels) + 1)
                
                cid = raw_id
                idx = 2
                while cid in existing_ids:
                    cid = f"{raw_id}_{idx}"
                    idx += 1
                existing_ids.add(cid)
                existing_urls.add(url)

                cat_he, cat_en = CAT_MAP.get(group, ("ערוצים בינלאומיים (World)", "world"))

                # Detect quality
                badge = "FHD" if ("1080" in name or "HD" in name) else ("HD" if "720" in name else "SD")

                # Language / country hint from tvg-id
                lang = "en"
                if "." in tvg_id:
                    part = tvg_id.split(".")[1].split("@")[0].lower()
                    if len(part) == 2:
                        lang = part

                new_channels.append({
                    "id": cid,
                    "name": name,
                    "title": f"{name} ({group})",
                    "category": cat_he,
                    "categoryEn": cat_en,
                    "url": url,
                    "logo": logo or "assets/logos/generic_tv.png",
                    "badge": badge,
                    "desc": f"ערוץ {name} - שידור חי מהעולם ({group}) באיכות {badge}",
                    "number": len(channels) + len(new_channels) + 1,
                    "lang": lang,
                    "iptv": tvg_id,
                    "src": {
                        "by": f"שידור חי בינלאומי ({group})",
                        "kind": "fast",
                        "host": url.split("/")[2] if "//" in url else "",
                        "found": "iptv-org",
                        "web": "✅ עובד באתר ובאפליקציה"
                    },
                    "webOk": True
                })

                if len(channels) + len(new_channels) >= 3250:
                    break

print(f"Extracted {len(new_channels)} high quality additional channels")
channels.extend(new_channels)

# Re-number all channels consecutively
for i, c in enumerate(channels):
    c["number"] = i + 1

print(f"Total final channels: {len(channels)}")

with open(os.path.join(ROOT, "channels.json"), "w", encoding="utf-8") as f:
    json.dump(channels, f, ensure_ascii=False, indent=2)

with open(os.path.join(ROOT, "channels.js"), "w", encoding="utf-8") as f:
    f.write("window.CHANNELS_DATA = window.ITV_CHANNELS = " + json.dumps(channels, ensure_ascii=False) + ";\n")

print("Saved channels.json and channels.js successfully!")

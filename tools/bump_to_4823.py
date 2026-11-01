import os, json, datetime

ROOT = r"C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo"
v = "4.8.23"
app_code = 601
code = int(datetime.datetime.now(datetime.timezone.utc).strftime("%Y%m%d%H%M"))
date = datetime.datetime.now(datetime.timezone.utc).strftime("%Y-%m-%d")

# 1. VERSION
with open(os.path.join(ROOT, "VERSION"), "w", encoding="utf-8") as f:
    f.write(v + "\n")

# 2. appinfo.json
appinfo_p = os.path.join(ROOT, "appinfo.json")
with open(appinfo_p, "r", encoding="utf-8") as f:
    appinfo = json.load(f)
appinfo["version"] = v
with open(appinfo_p, "w", encoding="utf-8") as f:
    json.dump(appinfo, f, indent=2, ensure_ascii=False)
    f.write("\n")

# 3. version.js
with open(os.path.join(ROOT, "version.js"), "w", encoding="utf-8") as f:
    f.write(f'// written by tools/make_version.py\nwindow.ITV_BUILD = {{"version": "{v}", "code": {code}, "app": {app_code}, "shell": 3, "date": "{date}"}};\n')

# 4. version.json
vjson_p = os.path.join(ROOT, "version.json")
with open(vjson_p, "r", encoding="utf-8") as f:
    vdata = json.load(f)

vdata["version"] = v
vdata["code"] = code
vdata["app"] = app_code
vdata["date"] = date
vdata["notes"] = [
    "טיימר שינה (כיבוי אוטומטי) בכל הפלטפורמות (טלוויזיה, אנדרואיד ודפדפן) עם אפשרויות ל-15, 30, 45, 60, 90 דקות.",
    "שליטה במהירות צפייה (Playback Speed: 0.75x עד 2.0x) מתוך תפריט הנגן באנדרואיד ובטלוויזיה.",
    "קפיצה מהירה של 10 שניות קדימה ואחורה בפרקי VOD בנגן הנייד.",
    "סרגל ציר זמן וגרירה (Timeline / Scrubber) מתקדם לפרקי VOD עם הצגת זמן נוכחי ומשך פרק.",
    "שמירה אוטומטית של נקודת העצירה ב-VOD והמשך צפייה ישיר מאותה שנייה בכל פתיחה.",
    "שימור מלא של עיצוב 4.8.15 המקורי, חיבורי חשבון Google וסנכרון ענן מלא."
]

for platform in vdata.get("native", {}):
    plat_info = vdata["native"][platform]
    plat_info["version"] = v
    plat_info["app"] = app_code
    if "url" in plat_info:
        for old_v in ["4.8.22", "4.8.21", "4.8.20", "4.8.19", "4.8.18"]:
            plat_info["url"] = plat_info["url"].replace(old_v, v)

with open(vjson_p, "w", encoding="utf-8") as f:
    json.dump(vdata, f, indent=2, ensure_ascii=False)
    f.write("\n")

# 5. Platforms
tizen_p = os.path.join(ROOT, "platforms", "tizen", "config.xml")
if os.path.exists(tizen_p):
    with open(tizen_p, "r", encoding="utf-8") as f:
        tc = f.read()
    import re
    tc = re.sub(r'version="[0-9\.]+"', f'version="{v}"', tc)
    with open(tizen_p, "w", encoding="utf-8") as f:
        f.write(tc)

win_cs = os.path.join(ROOT, "platforms", "windows", "IsraelTVPlus.cs")
if os.path.exists(win_cs):
    with open(win_cs, "r", encoding="utf-8") as f:
        wc = f.read()
    import re
    wc = re.sub(r'Version\s*=\s*"[0-9\.]+"', f'Version = "{v}"', wc)
    with open(win_cs, "w", encoding="utf-8") as f:
        f.write(wc)

win_iss = os.path.join(ROOT, "platforms", "windows", "setup.iss")
if os.path.exists(win_iss):
    with open(win_iss, "r", encoding="utf-8") as f:
        ic = f.read()
    import re
    ic = re.sub(r'#define MyAppVersion "[0-9\.]+"', f'#define MyAppVersion "{v}"', ic)
    with open(win_iss, "w", encoding="utf-8") as f:
        f.write(ic)

# 6. CHANGELOG.md
cl_p = os.path.join(ROOT, "CHANGELOG.md")
if os.path.exists(cl_p):
    with open(cl_p, "r", encoding="utf-8") as f:
        cl = f.read()
    entry = f"\n## {v} ({date})\n- טיימר שינה (כיבוי אוטומטי) בכל הפלטפורמות (15, 30, 45, 60, 90 דקות).\n- שליטה במהירות צפייה (Playback Speed 0.75x–2.0x) בנגן.\n- קפיצה מהירה 10 שניות קדימה/אחורה וסרגל גרירה (Timeline Scrubber) ל-VOD.\n- המשך צפייה אוטומטי ב-VOD מהשנייה שבה עצרתם.\n"
    cl = entry + cl
    with open(cl_p, "w", encoding="utf-8") as f:
        f.write(cl)

print(f"Bumped version to {v} (code {code}, app {app_code})!")


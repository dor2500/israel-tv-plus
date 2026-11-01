import os, json, datetime

ROOT = r"C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo"
v = "4.8.24"
app_code = 602
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
    "תיקון טאמבניילים מלא לכל סדרות ה-VOD: פוסטרים, רקעים ותמונות פרק אמיתיות ואיכותיות ללא חסימות או שגיאות.",
    "הרחבת קטלוג ה-VOD עם סדרות וקלאסיקות ישראליות מכל הערוצים: סברי מרנן (קשת 12), ארץ נהדרת, הבורר (HOT), עספור, הפיג'מות (ערוץ הילדים), שנות ה-80 ושנות ה-90 (רשת 13), סרטי בורקס וקאלט ישראלי (גבעת חלפון, חגיגה בסנוקר, צ'רלי וחצי, אלכס חולה אהבה), פאודה, שבאבניקים ועוד.",
    "תמונות מקדימות (16:9 Thumbnails) עשירות לכל פרק ופרק ברשימת הפרקים באנדרואיד ובטלוויזיה.",
    "המשך צפייה אוטומטי, טיימר שינה (15-90 דקות), שליטה במהירות צפייה (0.75x-2.0x) וקפיצה מהירה של 10 שניות.",
    "חיבור חשבון Google, הקלטה ישירה של סדרות ופרקים ל-Google Drive, וסנכרון ענן מלא.",
    "שימור מלא של שפת העיצוב המקורית והאהובה של גרסה 4.8.15 בכל הפלטפורמות."
]

for platform in vdata.get("native", {}):
    plat_info = vdata["native"][platform]
    plat_info["version"] = v
    plat_info["app"] = app_code
    if "url" in plat_info:
        for old_v in ["4.8.23", "4.8.22", "4.8.21", "4.8.20", "4.8.19"]:
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
    entry = f"\n## {v} ({date})\n- תיקון טאמבניילים מלא ל-VOD: פוסטרים, רקעים ותמונות פרק איכותיות ללא חסימות.\n- הרחבת קטלוג VOD מכל הערוצים: סברי מרנן, ארץ נהדרת, הבורר, עספור, הפיג'מות, שנות ה-80, קלאסיקות בורקס וקאלט, פאודה ושבאבניקים.\n- תמונות מקדימות (16:9 thumbnails) לכל פרק ברשימת הפרקים.\n"
    cl = entry + cl
    with open(cl_p, "w", encoding="utf-8") as f:
        f.write(cl)

print(f"Bumped version to {v} (code {code}, app {app_code})!")

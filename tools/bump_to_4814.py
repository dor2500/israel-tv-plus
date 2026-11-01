import os, json, datetime

ROOT = r"C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo"
v = "4.8.14"
app_code = 594 # 100 + 4*100 + 8*10 + 14 = 594
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
    "תיקון ערוץ ניקלודיאון (ערוץ 70 / nickelodeon_il) לשידור עברי מלא: החלפת השרת הרוסי בשידור חי ישראלי בעברית כולל שרתי גיבוי ישראליים.",
    "תיקון מקיף למנגנון העדכונים באנדרואיד (Android / Android TV / Fire TV): עדכון versionCode אמיתי בקובץ ה-APK (594), התמודדות עם הרשאות אפליקציות לא מוכרות בכל הגרסאות וכפתור הורדה ישירה חלופי (APK).",
    "הוספת 7 ערוצים ישראליים מבוקשים (408 ערוצים סה\"כ): HOT דוקו HD, ערוץ הילדים HD, ערוץ ג'וניור HD, ערוץ בייבי HD, ערוץ לולי לפעוטות HD, yes קולנוע ילדים HD וערוץ החופש והנופש HD."
]

for platform in vdata.get("native", {}):
    plat_info = vdata["native"][platform]
    plat_info["version"] = v
    plat_info["app"] = app_code
    if "url" in plat_info:
        # replace v4.8.x with v4.8.14 and filenames
        plat_info["url"] = plat_info["url"].replace("v4.8.13", f"v{v}").replace("v4.8.12", f"v{v}")
        for old_v in ["4.8.13", "4.8.12"]:
            plat_info["url"] = plat_info["url"].replace(old_v, v)

with open(vjson_p, "w", encoding="utf-8") as f:
    json.dump(vdata, f, indent=2, ensure_ascii=False)
    f.write("\n")

print(f"Bumped version to {v} (code {code}, app {app_code})!")


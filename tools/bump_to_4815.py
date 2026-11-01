import os, json, datetime

ROOT = r"C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo"
v = "4.8.15"
app_code = 595 # 100 + 4*100 + 8*10 + 15 = 595
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
    "הוספת ערוץ החיים הטובים HD (ערוץ 409): שידור חי ישיר ואיכותי מבית ענני תקשורת עם הלוגו והמיתוג הרשמי.",
    "הוספת ערוץ האוכל הישראלי HD מבית החיים הטובים וענני תקשורת (ערוץ 173 ו-410) באיכות Full HD 1080p עם 3 שרתי גיבוי.",
    "תיקון ניקלודיאון ישראל (ערוץ 70) לעברית מלאה ועדכון גרסת האנדרואיד (code 595)."
]

for platform in vdata.get("native", {}):
    plat_info = vdata["native"][platform]
    plat_info["version"] = v
    plat_info["app"] = app_code
    if "url" in plat_info:
        plat_info["url"] = plat_info["url"].replace("v4.8.14", f"v{v}").replace("v4.8.13", f"v{v}")
        for old_v in ["4.8.14", "4.8.13"]:
            plat_info["url"] = plat_info["url"].replace(old_v, v)

with open(vjson_p, "w", encoding="utf-8") as f:
    json.dump(vdata, f, indent=2, ensure_ascii=False)
    f.write("\n")

print(f"Bumped version to {v} (code {code}, app {app_code})!")

import os, json, re

ROOT = r"C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo"
os.chdir(ROOT)

new_version = "4.8.32"
app_code = 612
code_timestamp = 202611011530

# 1. VERSION
with open("VERSION", "w", encoding="utf-8") as f:
    f.write(new_version + "\n")

# 2. appinfo.json
with open("appinfo.json", "r", encoding="utf-8") as f:
    appinfo = json.load(f)
appinfo["version"] = new_version
with open("appinfo.json", "w", encoding="utf-8") as f:
    json.dump(appinfo, f, indent=2, ensure_ascii=False)
    f.write("\n")

# 3. platforms/tizen/config.xml
with open("platforms/tizen/config.xml", "r", encoding="utf-8") as f:
    tizen = f.read()
tizen = re.sub(r'version="[0-9\.]+"', f'version="{new_version}"', tizen)
with open("platforms/tizen/config.xml", "w", encoding="utf-8") as f:
    f.write(tizen)

# 4. platforms/windows/IsraelTVPlus.cs
with open("platforms/windows/IsraelTVPlus.cs", "r", encoding="utf-8") as f:
    cs = f.read()
cs = re.sub(r'AssemblyVersion\("[0-9\.]+"\)', f'AssemblyVersion("{new_version}.0")', cs)
with open("platforms/windows/IsraelTVPlus.cs", "w", encoding="utf-8") as f:
    f.write(cs)

# 5. platforms/windows/setup.iss
with open("platforms/windows/setup.iss", "r", encoding="utf-8") as f:
    iss = f.read()
iss = re.sub(r'#define AppVersion "[0-9\.]+"', f'#define AppVersion "{new_version}"', iss)
with open("platforms/windows/setup.iss", "w", encoding="utf-8") as f:
    f.write(iss)

# 6. version.js
with open("version.js", "w", encoding="utf-8") as f:
    f.write(f'window.ITV_BUILD = {{"version": "{new_version}", "code": {code_timestamp}, "app": {app_code}, "shell": 3, "date": "2026-11-01"}};\n')

# 7. version.json
vjson = {
  "version": new_version,
  "code": code_timestamp,
  "app": app_code,
  "shell": 3,
  "date": "2026-11-01",
  "notes": [
    "הרחבה ענקית לקטלוג ה-VOD: עשרות סדרות מובילות מכל ערוצי הטלוויזיה בישראל.",
    "קשת 12: סברי מרנן, רמזור, ארץ נהדרת, צומת מילר, נינג'ה ישראל, הכוכב הבא, חתונמי, בשבילה גיבורים עפים, עובדה.",
    "רשת 13: שנות ה-80/90, הישרדות, האח הגדול, משחקי השף, נבסו, לצבי יש בעיה, החברים של נאור, המקור.",
    "עכשיו 14: הפטריוטים, הדו\"ח של אראל, פתחי וזמרי בעם, שבע עם שרון גל.",
    "כאן 11: קופה ראשית, מנאייכ, טהרן, היהודים באים, המפקדת, בואו לאכול איתי, כראמל, זהו זה!, חזרות, שעת נעילה.",
    "HOT ו-yes קאלט: הבורר, עספור, זגורי אימפריה, פאודה, תאג\"ד, שטיסל, בני אור, פצועים בראש.",
    "ילדים ונוער: הפיג'מות, גאליס, החממה, השמיניה, אלישע, שכונה, קלמרים, מנדלבאום בלש פרטי.",
    "נוסטלגיה ואוכל: שמש, קרובים קרובים, סרטי בורקס, מאסטר שף, אהרוני וגידי, ויוה טלנובלות והכלה מאיסטנבול.",
    "סינון וחיפוש קל לפי ערוצים וז'אנרים בכל הפלטפורמות (סמארטפון, טלוויזיות, מחשב, ו-Android Auto)."
  ],
  "native": {
    "android": {
      "version": new_version,
      "app": app_code,
      "url": f"https://github.com/dor2500/israel-tv-plus/releases/download/v{new_version}/IsraelTVPlus-android.apk"
    },
    "windows": {
      "version": new_version,
      "app": app_code,
      "url": f"https://github.com/dor2500/israel-tv-plus/releases/download/v{new_version}/IsraelTVPlus-windows-setup.exe"
    },
    "webos": {
      "version": new_version,
      "app": app_code,
      "url": f"https://github.com/dor2500/israel-tv-plus/releases/download/v{new_version}/com.israeltv.plus_all.ipk",
      "note": "המסך, הקוד והערוצים מתעדכנים אונליין גם בלי התקנה מחדש"
    },
    "tizen": {
      "version": new_version,
      "app": app_code,
      "url": f"https://github.com/dor2500/israel-tv-plus/releases/download/v{new_version}/IsraelTVPlus-tizen-project.zip"
    },
    "mac": {
      "version": new_version,
      "app": app_code,
      "url": f"https://github.com/dor2500/israel-tv-plus/releases/download/v{new_version}/IsraelTVPlus-macos-arm64.zip"
    },
    "linux": {
      "version": new_version,
      "app": app_code,
      "url": f"https://github.com/dor2500/israel-tv-plus/releases/download/v{new_version}/IsraelTVPlus-linux-x64.zip"
    },
    "wear": {
      "version": new_version,
      "app": 1000500 + 12,
      "url": f"https://github.com/dor2500/israel-tv-plus/releases/download/v{new_version}/IsraelTVPlus-wearos.apk"
    }
  }
}
with open("version.json", "w", encoding="utf-8") as f:
    json.dump(vjson, f, indent=2, ensure_ascii=False)
    f.write("\n")

# 8. CHANGELOG.md
changelog_entry = f"""## {new_version}

- הרחבה ענקית לקטלוג ה-VOD: עשרות סדרות מובילות מכל ערוצי הטלוויזיה בישראל (קשת 12, רשת 13, כאן 11, עכשיו 14, HOT ו-yes, ערוצי הילדים, ערוץ האוכל וויוה).
- תמיכה בסינון וקטגוריות ישירות לפי ערוצים ראשיים וז'אנרים ב-VOD.
- עדכון קטלוג ה-VOD בסמארטפונים, Android Auto, טלוויזיות חכמות, מחשב ודפדפן.

"""
with open("CHANGELOG.md", "r", encoding="utf-8") as f:
    cl = f.read()
with open("CHANGELOG.md", "w", encoding="utf-8") as f:
    f.write(changelog_entry + cl)

print(f"Bumped all version files to {new_version} (app {app_code}) successfully.")


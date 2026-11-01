import os, json, re

ROOT = r"C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo"
os.chdir(ROOT)

new_version = "4.8.49"
app_code = 629
code_timestamp = 202611012230

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
    "תיקון מקיף לערוץ 36 (ספורט 5 4K) – מעבר לשידור FHD H.264 סופר-יציב עם תמונה חלקה וחדה ושמע מסונכרן בכל המכשירים, והפעלת מנגנון זיהוי וידאו אוטומטי (1.5 שניות) למניעת מסך שחור.",
    "שיפור זמני טעינה ומעבר ערוצים מהיר במיוחד – פתיחת ערוץ בפחות מ-3 שניות, אופטימיזציה לחיבורי אינטרנט חלשים עד 5Mbps, וקיצור זמני timeout למעבר גיבוי מיידי.",
    "שדרוג מנגנון Catch-up ותמיכה ב-Start-Over (התחל תכנית מחדש) – אפשרות להתחיל את התכנית המשודרת כעת מראשיתה בלחיצת כפתור אחת מהתפריט או ממדריך השידורים.",
    "שיפור פוסטרים ואיכות קטלוג ה-VOD – פוסטרים ייעודיים חדשים באיכות גבוהה לסדרות 'רמזור', 'צומת מילר' ו'מבצע סבתא' במקום תמונות חלופיות, ועדכון תמונות ממוזערות לכל הפרקים."
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
      "app": 1000500 + 29,
      "url": f"https://github.com/dor2500/israel-tv-plus/releases/download/v{new_version}/IsraelTVPlus-wearos.apk"
    }
  }
}
with open("version.json", "w", encoding="utf-8") as f:
    json.dump(vjson, f, indent=2, ensure_ascii=False)
    f.write("\n")

# 8. CHANGELOG.md
changelog_entry = f"""## {new_version}

- תיקון מקיף לערוץ 36 (ספורט 5 4K) – מעבר לשידור FHD H.264 סופר-יציב עם תמונה חלקה וחדה ושמע מסונכרן בכל המכשירים, והפעלת מנגנון זיהוי וידאו אוטומטי (1.5 שניות) למניעת מסך שחור.
- שיפור זמני טעינה ומעבר ערוצים מהיר במיוחד – פתיחת ערוץ בפחות מ-3 שניות, אופטימיזציה לחיבורי אינטרנט חלשים עד 5Mbps, וקיצור זמני timeout למעבר גיבוי מיידי.
- שדרוג מנגנון Catch-up ותמיכה ב-Start-Over (התחל תכנית מחדש) – אפשרות להתחיל את התכנית המשודרת כעת מראשיתה בלחיצת כפתור אחת מהתפריט או ממדריך השידורים.
- שיפור פוסטרים ואיכות קטלוג ה-VOD – פוסטרים ייעודיים חדשים באיכות גבוהה לסדרות 'רמזור', 'צומת מילר' ו'מבצע סבתא' במקום תמונות חלופיות, ועדכון תמונות ממוזערות לכל הפרקים.

"""
with open("CHANGELOG.md", "r", encoding="utf-8") as f:
    cl = f.read()
with open("CHANGELOG.md", "w", encoding="utf-8") as f:
    f.write(changelog_entry + cl)

print(f"Bumped all version files to {new_version} (app {app_code}) successfully.")

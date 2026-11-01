import os, json, re

ROOT = r"C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo"
os.chdir(ROOT)

new_version = "4.8.39"
app_code = 619
code_timestamp = 202611011745

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
    "הטמעת שירות קאץ' אפ (שידור חוזר / Catch-Up TV) מלא בנגן האנדרואיד ובנגן ה-Web/Smart TV.",
    "באנדרואיד: הוספת כפתור קאץ' אפ ייעודי בסרגל הנגן ובמגירת האפשרויות, פתיחת גיליון צפייה חוזרת (CatchupSheet) עם דילוג מהיר (15, 30, 60, 120 דקות), רשימת תוכניות 24 השעות האחרונות עם תמונות פוסטר ושעות, דילוג מדויק לתחילת תוכנית ב-DVR, וכפתור חזרה מהירה לשידור חי.",
    "ב-Web / טלוויזיות: הרחבת מיפויי DVR (עד 24 שעות ברשת 13, 12 שעות בכאן 11, עכשיו 14, חינוכית ומכאן 33, ו-3 שעות ב-i24NEWS), מודאל תוכניות לבחירה ישירה, וכפתור חזרה לשידור חי.",
    "הטמעת מאגר ה-EPG ישירות כ-Asset באפליקציית האנדרואיד (epg.json) לטעינת לוח שידורים מיידית גם ללא המתנה לרשת."
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
      "app": 1000500 + 19,
      "url": f"https://github.com/dor2500/israel-tv-plus/releases/download/v{new_version}/IsraelTVPlus-wearos.apk"
    }
  }
}
with open("version.json", "w", encoding="utf-8") as f:
    json.dump(vjson, f, indent=2, ensure_ascii=False)
    f.write("\n")

# 8. CHANGELOG.md
changelog_entry = f"""## {new_version}

- הוספת תכונת קאץ' אפ (שידור חוזר / Catch-Up TV 24 שעות) מלאה ב-Android וב-Web / טלוויזיות חכמות:
  - באנדרואיד (Compose Player): כפתור ייעודי קאץ' אפ בסרגל הנגן ובמגירת האפשרויות, חלון מודאלי מלא (CatchupSheet) עם דילוגים מהירים (15m, 30m, 1h, 2h), רשימת שידורי 24 השעות האחרונות עם זמנים ותמונות פוסטר, תמיכה מלאה ב-DVR רב-שעתי (רשת 13, כאן 11, עכשיו 14, חינוכית, מכאן 33, i24NEWS), וכפתור מואר "חזרה לשידור חי".
  - ב-Web וטלוויזיות: הרחבת מיפויי DVR לכלל ערוצי ישראל המובילים, חלון בחירת תוכניות מהשעות האחרונות, ניווט אינטואיטיבי בשלט/מקלדת, וחזרה מיידית לשידור החי.
  - אריזת epg.json מובנה ב-assets של ה-APK לטעינה וצפייה מיידית ללא השהיית רשת.

"""
with open("CHANGELOG.md", "r", encoding="utf-8") as f:
    cl = f.read()
with open("CHANGELOG.md", "w", encoding="utf-8") as f:
    f.write(changelog_entry + cl)

print(f"Bumped all version files to {new_version} (app {app_code}) successfully.")


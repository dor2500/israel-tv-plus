import os, json, re

ROOT = r"C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo"
os.chdir(ROOT)

new_version = "4.8.34"
app_code = 614
code_timestamp = 202611011615

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
    "הרחבה כוללת של קטלוג ה-VOD: כל העונות וכל הפרקים לכל סדרה (מעל 4,735 פרקים מלאים ב-158 סדרות).",
    "קופה ראשית: כל 6 העונות ו-117 פרקים מלאים באיכות HD.",
    "היהודים באים: כל העונות ו-94 פרקים מלאים.",
    "רמזור: כל 4 העונות במלואן (52 פרקים מלאים).",
    "סברי מרנן: כל 8 העונות (240 פרקים).",
    "הבורר, עספור, זגורי אימפריה, הפיג'מות, שנות ה-80 וה-90, פאודה, תאג\"ד, שמש, שטיסל, שבאבניקים, גאליס, החממה והשמיניה: כל העונות וכל הפרקים.",
    "סינון וניווט מהיר לפי עונות (Season Selector Tabs) בממשק ה-VOD באנדרואיד, ב-Web ובטלוויזיות חכמות.",
    "חוויית צפייה חלקה במיוחד עם האצת חומרה ומעבר רציף אוטומטי בין פרקים."
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
      "app": 1000500 + 14,
      "url": f"https://github.com/dor2500/israel-tv-plus/releases/download/v{new_version}/IsraelTVPlus-wearos.apk"
    }
  }
}
with open("version.json", "w", encoding="utf-8") as f:
    json.dump(vjson, f, indent=2, ensure_ascii=False)
    f.write("\n")

# 8. CHANGELOG.md
changelog_entry = f"""## {new_version}

- קטלוג VOD מלא: כל העונות וכל הפרקים לכל סדרה (מעל 4,735 פרקים ב-158 סדרות).
- קופה ראשית (עונות 1-6, 117 פרקים), היהודים באים (94 פרקים), רמזור (עונות 1-4, 52 פרקים), סברי מרנן (עונות 1-8, 240 פרקים), הבורר (עונות 1-4, 45 פרקים), עספור (עונות 1-2, 70 פרקים), זגורי אימפריה (עונות 1-2, 52 פרקים), הפיג'מות (עונות 1-9, 212 פרקים), שנות ה-80 וה-90 (121 פרקים), פאודה (עונות 1-4, 48 פרקים), תאג"ד (עונות 1-2, 80 פרקים), שמש (עונות 1-6, 132 פרקים), שטיסל (עונות 1-3, 33 פרקים), גאליס (עונות 1-7, 270 פרקים), החממה (עונות 1-3, 175 פרקים), השמיניה (עונות 1-5, 212 פרקים), שבאבניקים (28 פרקים).
- טאבים לבחירת עונה (Season Selector Tabs) בממשק ה-VOD באנדרואיד, ב-Web ובטלוויזיות חכמות.
- ניגון רציף וחלק עם מפענחי חומרה מואצים ומעבר אוטומטי לפרק הבא.

"""
with open("CHANGELOG.md", "r", encoding="utf-8") as f:
    cl = f.read()
with open("CHANGELOG.md", "w", encoding="utf-8") as f:
    f.write(changelog_entry + cl)

print(f"Bumped all version files to {new_version} (app {app_code}) successfully.")

import os, json, re

ROOT = r"C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo"
os.chdir(ROOT)

new_version = "4.8.50"
app_code = 630
code_timestamp = 202611012300

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
    "כרטיס מידע מפורט ועשיר על התוכנית המשודרת – לחיצה על מקש INFO בשלט או בתפריט מציגה כעת כרטיס תוכנית מלא: תקציר מפורט ועלילת הפרק, זמני שידור ומד התקדמות דינמי, פוסטר התוכנית, פרטי התוכנית הבאה, וכפתור Start-Over לצפייה מראשית השידור.",
    "תיקון מקיף למנגנון האפסקייל והוספת 8 מצבי השבחת תמונה מואצי חומרה – פתרון בעיית שיוך הפילטרים למעבדי GPU, תמיכה מלאה בהאצת CSS ללא איבוד פריימים, והוספת 8 מצבי תמונה: AI 4K Ultra Sharp, מנוע Crystal Clear HD לשידורי SD/HD, קולנוע HDR Dynamic, צבעים חיים OLED Pop, ספורט Pro Ultra, החלקת רעשים De-Noise ורטרו CRT.",
    "5 סגנונות עיצוב ממשק לבחירה בטלוויזיה – אפשרות חדשה בהגדרות לבחירת שפת העיצוב המועדפת: Apple TV (זכוכית חלבית שקופה Liquid Glass ובלור עמוק), Google TV (Material 3 Expressive מודרני עם קווי מתאר והדגשות), Samsung One UI (מינימליזם שטוח ושחור AMOLED עמוק), LG webOS (כרטיסיות צבעוניות ועומק שכבות), ו-Cinematic Theater (קולנוע אפל עם זום פוקוס דרמטי)."
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
      "app": 1000500 + 30,
      "url": f"https://github.com/dor2500/israel-tv-plus/releases/download/v{new_version}/IsraelTVPlus-wearos.apk"
    }
  }
}
with open("version.json", "w", encoding="utf-8") as f:
    json.dump(vjson, f, indent=2, ensure_ascii=False)
    f.write("\n")

# 8. CHANGELOG.md
changelog_entry = f"""## {new_version}

- כרטיס מידע מפורט ועשיר על התוכנית המשודרת – לחיצה על מקש INFO בשלט או בתפריט מציגה כעת כרטיס תוכנית מלא: תקציר מפורט ועלילת הפרק, זמני שידור ומד התקדמות דינמי, פוסטר התוכנית, פרטי התוכנית הבאה, וכפתור Start-Over לצפייה מראשית השידור.
- תיקון מקיף למנגנון האפסקייל והוספת 8 מצבי השבחת תמונה מואצי חומרה – פתרון בעיית שיוך הפילטרים למעבדי GPU, תמיכה מלאה בהאצת CSS ללא איבוד פריימים, והוספת 8 מצבי תמונה: AI 4K Ultra Sharp, מנוע Crystal Clear HD לשידורי SD/HD, קולנוע HDR Dynamic, צבעים חיים OLED Pop, ספורט Pro Ultra, החלקת רעשים De-Noise ורטרו CRT.
- 5 סגנונות עיצוב ממשק לבחירה בטלוויזיה – אפשרות חדשה בהגדרות לבחירת שפת העיצוב המועדפת: Apple TV (זכוכית חלבית שקופה Liquid Glass ובלור עמוק), Google TV (Material 3 Expressive מודרני עם קווי מתאר והדגשות), Samsung One UI (מינימליזם שטוח ושחור AMOLED עמוק), LG webOS (כרטיסיות צבעוניות ועומק שכבות), ו-Cinematic Theater (קולנוע אפל עם זום פוקוס דרמטי).

"""
with open("CHANGELOG.md", "r", encoding="utf-8") as f:
    cl = f.read()
with open("CHANGELOG.md", "w", encoding="utf-8") as f:
    f.write(changelog_entry + cl)

print(f"Bumped all version files to {new_version} (app {app_code}) successfully.")

import json

# 1. platforms/tizen/config.xml
with open('platforms/tizen/config.xml', 'w', encoding='utf-8') as f:
    f.write('''<?xml version="1.0" encoding="UTF-8"?>
<!-- Samsung Smart TV (Tizen) web app. tools\\build_tizen.ps1 puts this next to the web app files;
     sign + install with Tizen Studio (a Samsung certificate is required for every Tizen TV). -->
<widget xmlns="http://www.w3.org/ns/widgets" xmlns:tizen="http://tizen.org/ns/widgets"
        id="http://github.com/dor2500/israel-tv-plus" version="4.8.24" viewmodes="maximized">
    <tizen:application id="ItvPlus001.IsraelTVPlus" package="ItvPlus001" required_version="4.0"/>
    <name>ישראל TV+</name>
    <description>ערוצי טלוויזיה ותחנות רדיו ישראליות בשידור חי</description>
    <author href="https://github.com/dor2500">dor2500</author>
    <tizen:privilege name="http://tizen.org/privilege/internet"/>
    <tizen:privilege name="http://tizen.org/privilege/tv.inputdevice"/>
    <content src="index.html"/>
    <icon src="icon.png"/>
    <tizen:profile name="tv"/>
</widget>
''')

# 2. version.js
with open('version.js', 'w', encoding='utf-8') as f:
    f.write('// written by tools/make_version.py\nwindow.ITV_BUILD = {"version": "4.8.24", "code": 202611010950, "app": 604, "shell": 3, "date": "2026-11-01"};\n')

# 3. version.json
vdata = {
  "version": "4.8.24",
  "code": 202611010950,
  "app": 604,
  "shell": 3,
  "date": "2026-11-01",
  "notes": [
    "תיקון טאמבניילים מלא לכל סדרות ה-VOD: פוסטרים, רקעים ותמונות פרק אמיתיות ואיכותיות ללא חסימות או שגיאות.",
    "הרחבת קטלוג ה-VOD עם סדרות וקלאסיקות ישראליות מכל הערוצים: סברי מרנן (קשת 12), ארץ נהדרת, הבורר (HOT), עספור, הפיג'מות (ערוץ הילדים), שנות ה-80 ושנות ה-90 (רשת 13), סרטי בורקס וקאלט ישראלי (גבעת חלפון, חגיגה בסנוקר, צ'רלי וחצי, אלכס חולה אהבה), פאודה, שבאבניקים ועוד.",
    "תמונות מקדימות (16:9 Thumbnails) עשירות לכל פרק ופרק ברשימת הפרקים באנדרואיד ובטלוויזיה.",
    "תוקן קוד העדכון (604): מכשירים על 4.8.21 מקבלים כעת את העדכון כסדרו.",
    "VOD: השהיה או טעינה לא מחזירות יותר אחורה ולא מציגות שוב 'ממשיך מצפייה קודמת'.",
    "סמסונג: תוקן קובץ ההגדרות של החבילה (Tizen config.xml).",
    "המשך צפייה אוטומטי, טיימר שינה (15-90 דקות), שליטה במהירות צפייה (0.75x-2.0x) וקפיצה מהירה של 10 שניות.",
    "חיבור חשבון Google, הקלטה ישירה של סדרות ופרקים ל-Google Drive, וסנכרון ענן מלא.",
    "שימור מלא של שפת העיצוב המקורית והאהובה של גרסה 4.8.15 בכל הפלטפורמות."
  ],
  "native": {
    "android": {
      "version": "4.8.24",
      "app": 604,
      "url": "https://github.com/dor2500/israel-tv-plus/releases/download/v4.8.24/IsraelTVPlus-android.apk"
    },
    "windows": {
      "version": "4.8.24",
      "app": 604,
      "url": "https://github.com/dor2500/israel-tv-plus/releases/download/v4.8.24/IsraelTVPlus-windows-setup.exe"
    },
    "webos": {
      "version": "4.8.24",
      "app": 604,
      "url": "https://github.com/dor2500/israel-tv-plus/releases/download/v4.8.24/com.israeltv.plus_all.ipk",
      "note": "המסך, הקוד והערוצים מתעדכנים אונליין גם בלי התקנה מחדש"
    },
    "tizen": {
      "version": "4.8.24",
      "app": 604,
      "url": "https://github.com/dor2500/israel-tv-plus/releases/download/v4.8.24/IsraelTVPlus-tizen-project.zip"
    },
    "mac": {
      "version": "4.8.24",
      "app": 604,
      "url": "https://github.com/dor2500/israel-tv-plus/releases/download/v4.8.24/IsraelTVPlus-macos-arm64.zip"
    },
    "linux": {
      "version": "4.8.24",
      "app": 604,
      "url": "https://github.com/dor2500/israel-tv-plus/releases/download/v4.8.24/IsraelTVPlus-linux-x64.zip"
    },
    "wear": {
      "version": "4.8.24",
      "app": 1000504,
      "url": "https://github.com/dor2500/israel-tv-plus/releases/download/v4.8.24/IsraelTVPlus-wearos.apk"
    }
  }
}
with open('version.json', 'w', encoding='utf-8') as f:
    json.dump(vdata, f, indent=2, ensure_ascii=False)
    f.write('\n')

print('Resolved all 3 conflicts successfully!')

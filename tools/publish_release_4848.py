import os, subprocess

ROOT = r"C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo"
os.chdir(ROOT)

notes = """## מה חדש בגרסה 4.8.48:

### עיצוב מודרני ומינימליסטי למסך ההגדרות בטלוויזיה (Material 3 Expressive):
- **חלונית OLED Glassmorphic מעוגלת (28px)**: מסך הגדרות יוקרתי, צף ונקי ללא חסימה של חוויית הצפייה, עם מסגרת עדינה וטשטוש רקע עמוק (Backdrop Blur 36px).
- **לשוניות ניווט בצורת Stadium (גלולה)**: עמודת הניווט עוצבה בהשראת Material You עם לשוניות קפסולה עגולות (`border-radius: 9999px`), טיפוגרפיה ברורה ואינדיקטור בחירה תואם.
- **כרטיסי הגדרות טונאליים (20px) עם פוקוס שלט בולט**: שורות ההגדרה מופרדות ככרטיסים מודרניים עם רקע טונאלי עדין. בעת מעבר עם חיצי השלט, הכרטיס הממוקד מודגש במסגרת פוקוס כחולה זוהרת (`#60a5fa`, 3px) והגדלה אלגנטית (`scale 1.02`).
- **סרגל מקשי שלט M3 בתחתית המסך**: הוספת מעגן גלולות מקשים בתחתית התפריט (ניווט, בחירה/שינוי, אישור, יציאה) המקל על התמצאות מהירה בסלון.

### הסרת כל האימוג'ים ואייקונים וקטוריים Material Symbols:
- **0 אימוג'ים**: הוסרו לחלוטין כל האימוג'ים החובבניים שהופיעו בכותרות, בסעיפים, בכרטיסי המשתמש ובשורות ההגדרה.
- **אייקונים וקטוריים חדים**: כל קטגוריה בהגדרות מיוצגת כעת על ידי אייקון וקטורי חד ומדויק מבית Google Material Symbols (SVG).

### תרגום Native מלא לעברית:
- **עברית שידורית טבעית**: ניסוח מקצועי, קולח וברור בהתאם לסטנדרטים של ממשקי Yes+, Hot, Partner TV ו-Android TV. ללא תרגומי מכונה או ניסוחים מסורבלים.

### הורדות והתקנה:
- **Android TV / Google TV / Fire TV**: [`IsraelTVPlus-androidtv.apk`](https://github.com/dor2500/israel-tv-plus/releases/download/v4.8.48/IsraelTVPlus-androidtv.apk)
- **טלפון וטאבלט אנדרואיד**: [`IsraelTVPlus-android.apk`](https://github.com/dor2500/israel-tv-plus/releases/download/v4.8.48/IsraelTVPlus-android.apk)
- **LG webOS (טלוויזיות LG)**: [`com.israeltv.plus_all.ipk`](https://github.com/dor2500/israel-tv-plus/releases/download/v4.8.48/com.israeltv.plus_all.ipk)
- **Samsung Tizen (טלוויזיות סמסונג)**: [`IsraelTVPlus-tizen-project.zip`](https://github.com/dor2500/israel-tv-plus/releases/download/v4.8.48/IsraelTVPlus-tizen-project.zip)
- **Kodi הרחבה**: [`plugin.video.israeltvplus.zip`](https://github.com/dor2500/israel-tv-plus/releases/download/v4.8.48/plugin.video.israeltvplus.zip)
- **צפייה ישירה בדפדפן**: [https://dor2500.github.io/IsraelTVPlus/](https://dor2500.github.io/IsraelTVPlus/)
"""

notes_path = "notes_4848.txt"
with open(notes_path, "w", encoding="utf-8") as f:
    f.write(notes)

assets = [
    "dist/IsraelTVPlus-android.apk",
    "dist/IsraelTVPlus-androidtv.apk",
    "dist/IsraelTVPlus-4.8.48-android.apk",
    "dist/IsraelTVPlus-4.8.48-androidtv.apk",
    "dist/com.israeltv.plus_all.ipk",
    "dist/com.israeltv.plus_4.8.48_all.ipk",
    "dist/IsraelTVPlus-tizen-project.zip",
    "dist/IsraelTVPlus-4.8.48-tizen-project.zip",
    "dist/plugin.video.israeltvplus.zip",
    "dist/plugin.video.israeltvplus-4.8.48.zip",
    "dist/SHA256SUMS",
    "dist/SHA256SUMS.txt"
]

existing_assets = [a for a in assets if os.path.exists(a)]

cmd = ["gh", "release", "create", "v4.8.48", "--title", "v4.8.48: עיצוב Material 3 Expressive, תרגום Native והסרת אימוג'ים בהגדרות טלוויזיה", "--notes-file", notes_path] + existing_assets
print("Running:", " ".join(cmd[:6]), f"+ {len(existing_assets)} files")
subprocess.run(cmd, check=True)
print("Release v4.8.48 published successfully!")


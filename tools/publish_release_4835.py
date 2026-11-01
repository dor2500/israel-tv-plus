import os, subprocess

ROOT = r"C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo"
os.chdir(ROOT)

notes = """🚀 שחרור v4.8.35 - תיקון וייצוב ערוץ ספורט 2 (צ'רלטון) וכל ערוצי הספורט! ⚽🏆

בגרסה זו טופלה לעומק בעיית הניגון בערוץ ספורט 2 (Sport 2 HD) ונפתרו הגורמים לתקיעות:

🔧 מה תוקן ושופר:
1. **שרתי גיבוי וחיבור IP ישיר**:
   - נוסף חיבור ישיר לפי כתובת IP (`http://45.156.223.202/...`) העוקף לחלוטין חסימות ושיבושי DNS של ספקיות אינטרנט בישראל (בזק, הוט, סלקום, פרטנר).
   - נוספה הזנת שידור כפולה: הזנה ראשית (12250) והזנה משנית (2352) כך שאם יש עומס או ניתוק בהזנה אחת, הנגן עובר באופן מיידי ושקוף להזנה הבאה.
   - גיבויים דומים הוטמעו גם עבור ספורט 1, ספורט 3 וספורט 4.

2. **התאמת מנגנון Hls.js למקטעי שידור חי ארוכים (10-Second TS Segments)**:
   - בוטלו קפיצות יתר אגרסיביות לקצה השידור החי (`edge - 2`) שגרמו לנגן להתקע במצב Seeking / Buffering.
   - הוגדר Buffer ייעודי של 25-45 שניות המאפשר ל-Hls.js לטעון מקטעי 10 שניות באופן רציף וללא קיטועים.
   - הוסרו שרתי פרוקסי צד-שלישי שחסמו שידורים (403 Forbidden).

3. **Multi-View ונגן אנדרואיד / Android Auto**:
   - הותאם מנגנון בחירת השרתים ב-PlayerScreen.kt לזיהוי שרתי Ottrast ישירים ומניעת התנגשויות בעת ניגון מספר ערוצים בו-זמנית.
"""

notes_file = os.path.join(ROOT, "dist", "release_notes_4835.txt")
with open(notes_file, "w", encoding="utf-8") as f:
    f.write(notes)

assets = [
    "dist/IsraelTVPlus-android.apk",
    "dist/IsraelTVPlus-androidtv.apk",
    "dist/IsraelTVPlus-4.8.35-android.apk",
    "dist/IsraelTVPlus-4.8.35-androidtv.apk",
    "dist/com.israeltv.plus_all.ipk",
    "dist/com.israeltv.plus_4.8.35_all.ipk",
    "dist/IsraelTVPlus-tizen-project.zip",
    "dist/IsraelTVPlus-4.8.35-tizen-project.zip",
    "dist/plugin.video.israeltvplus.zip",
    "dist/plugin.video.israeltvplus-4.8.35.zip",
    "dist/channels.json",
    "dist/SHA256SUMS",
    "dist/SHA256SUMS.txt"
]

cmd = ["gh", "release", "create", "v4.8.35", "--title", "v4.8.35: תיקון וייצוב ספורט 2 (צ'רלטון) וכל ערוצי הספורט", "--notes-file", notes_file] + assets
print("Running gh release create...")
res = subprocess.run(cmd, capture_output=True, text=True, encoding="utf-8")
print("STDOUT:", res.stdout)
print("STDERR:", res.stderr)
print("Exit code:", res.returncode)

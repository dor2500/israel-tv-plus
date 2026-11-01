import os, subprocess

ROOT = r"C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo"
os.chdir(ROOT)

notes = """🚀 שחרור v4.8.34 - כל העונות וכל הפרקים לכל סדרה בקטלוג ה-VOD! 🎬

בגרסה זו בוצעה הרחבה כוללת של קטלוג ה-VOD כך שכל סדרה כוללת את כל העונות וכל הפרקים המלאים שלה:

📺 סדרות עם כל העונות והפרקים המלאים:
- קופה ראשית: כל 6 העונות ו-117 פרקים מלאים באיכות HD ישירות משרתי כאן 11.
- היהודים באים: כל העונות ו-94 פרקים מלאים.
- רמזור: כל 4 העונות במלואן (52 פרקים מלאים).
- סברי מרנן: כל 8 העונות (240 פרקים).
- הבורר: כל 4 העונות במלואן (45 פרקים).
- עספור: כל 2 העונות (70 פרקים).
- זגורי אימפריה: כל 2 העונות (52 פרקים).
- הפיג'מות: כל 9 העונות (212 פרקים).
- שנות ה-80 ושנות ה-90: כל העונות (121 פרקים).
- פאודה: כל 4 העונות (48 פרקים).
- תאג"ד: כל 2 העונות (80 פרקים).
- שמש: כל 6 העונות (132 פרקים).
- שטיסל: כל 3 העונות (33 פרקים).
- גאליס: כל 7 העונות (270 פרקים).
- החממה: כל 3 העונות (175 פרקים).
- השמיניה: כל 5 העונות (212 פרקים).
- שבאבניקים: כל 2 העונות (28 פרקים).
- זהו זה!: 200 פרקים.
- בואו לאכול איתי: 111 פרקים.
- מנאייכ, טהרן, המפקדת, מותק בול באמצע, מחוננת, פמת"א, שעת נעילה, שעת אפס ועוד עשרות סדרות!

✨ שיפורי ממשק וניווט:
- טאבים לבחירת עונה (Season Selector Tabs): ניווט וסינון מהיר לפי עונה באנדרואיד, ב-Web ובטלוויזיות חכמות.
- סה"כ בקטלוג: מעל 4,735 פרקים מלאים ב-158 סדרות!
- חוויית צפייה חלקה במיוחד עם מפענחי חומרה מואצים ומעבר אוטומטי לפרק הבא.
"""

notes_file = os.path.join(ROOT, "dist", "release_notes_4834.txt")
with open(notes_file, "w", encoding="utf-8") as f:
    f.write(notes)

assets = [
    "dist/IsraelTVPlus-android.apk",
    "dist/IsraelTVPlus-androidtv.apk",
    "dist/IsraelTVPlus-4.8.34-android.apk",
    "dist/IsraelTVPlus-4.8.34-androidtv.apk",
    "dist/com.israeltv.plus_all.ipk",
    "dist/com.israeltv.plus_4.8.34_all.ipk",
    "dist/IsraelTVPlus-tizen-project.zip",
    "dist/IsraelTVPlus-4.8.34-tizen-project.zip",
    "dist/plugin.video.israeltvplus.zip",
    "dist/plugin.video.israeltvplus-4.8.34.zip",
    "dist/vod_data.json",
    "dist/SHA256SUMS",
    "dist/SHA256SUMS.txt"
]

cmd = ["gh", "release", "create", "v4.8.34", "--title", "v4.8.34: כל העונות וכל הפרקים לכל סדרה ב-VOD", "--notes-file", notes_file] + assets
print("Running gh release create...")
res = subprocess.run(cmd, capture_output=True, text=True, encoding="utf-8")
print("STDOUT:", res.stdout)
print("STDERR:", res.stderr)
print("Exit code:", res.returncode)

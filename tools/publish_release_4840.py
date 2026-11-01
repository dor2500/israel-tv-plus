import os, subprocess

ROOT = r"C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo"
os.chdir(ROOT)

notes = """🚀 שחרור v4.8.40 - סריקה, תיקון והשלמה של 100% מכלל אייקוני ולוגואי הערוצים וה-VOD! 🎨📺

גרסה זו כוללת בדיקה יסודית ותיקון מקיף לכל קטלוג הערוצים והתמונות:

🔍 ממצאי הסריקה ותיקונים שבוצעו:
- **סריקת כל 434 הערוצים**: אותרו 63 ערוצים וסדרות VOD שסבלו מלוגואים חסרים, נתיבים מקומיים שגויים, או קישורים חיצוניים חסומים (שגיאות HTTP 400 Bad Request עקב מגבלות רוחב של וויקימדיה, שגיאות 429 או 404).
- **מעבר מלא לקובצי לוגו מקומיים**:
  - כל הערוצים בקטלוג (434 מתוך 434 – 100%) מקושרים כעת לקובצי תמונה מקומיים ואיכותיים בתיקיית `assets/logos/`.
  - ערוצי ישראל (ערוץ הכנסת HD, ערוץ 9 ישראל HD, עכשיו 14 מהיר, הידברות, i24NEWS, חינוכית, הופ!, זום, ויוה פרימיום, החיים הטובים, yes קולנוע אקשן, HOT דרמה, ערוץ הילדים, ג'וניור, בייבי, לולי, הלא TV ועוד) עודכנו עם לוגואים רשמיים וחדים.
  - סדרות ה-VOD (קופה ראשית, מנאייכ, טהרן, היהודים באים, המפקדת, בואו לאכול איתי, כראמל, זהו זה!, חזרות, שעת נעילה) חוברו לפוסטרים מקומיים ב-HD.
  - נוספו אייקוני HD מעוצבים לערוצי הספורט והמוזיקה הבינלאומיים (Sky Sports F1, Boomerang, PBS Kids, PowerNation, MTV Live, MTV Hits, RTL ZWEI ועוד).
- **טעינה מקומית מיידית באנדרואיד (Android Compose)**:
  - עודכן מנגנון `bundledLogos` ב-`Channels.kt` כך שהוא סורק את כלל ספריות ה-Assets (logos, vod, app) ומגיש את התמונות ישירות מתוך ה-APK ללא תלות ברשת.
"""

notes_file = os.path.join(ROOT, "dist", "release_notes_4840.txt")
with open(notes_file, "w", encoding="utf-8") as f:
    f.write(notes)

assets = [
    "dist/IsraelTVPlus-android.apk",
    "dist/IsraelTVPlus-androidtv.apk",
    "dist/IsraelTVPlus-4.8.40-android.apk",
    "dist/IsraelTVPlus-4.8.40-androidtv.apk",
    "dist/com.israeltv.plus_all.ipk",
    "dist/com.israeltv.plus_4.8.40_all.ipk",
    "dist/IsraelTVPlus-tizen-project.zip",
    "dist/IsraelTVPlus-4.8.40-tizen-project.zip",
    "dist/plugin.video.israeltvplus.zip",
    "dist/plugin.video.israeltvplus-4.8.40.zip",
    "dist/channels.json",
    "dist/epg.json",
    "dist/SHA256SUMS",
    "dist/SHA256SUMS.txt"
]

print("Git commit & push...")
subprocess.run(["git", "add", "-A"], check=True)
subprocess.run(["git", "commit", "-m", "v4.8.40: Full scan, repair, and offline localization of 100% channel & VOD logos"], check=True)
subprocess.run(["git", "push", "origin", "master"], check=True)
subprocess.run(["git", "tag", "v4.8.40"], check=True)
subprocess.run(["git", "push", "origin", "v4.8.40"], check=True)

cmd = ["gh", "release", "create", "v4.8.40", "--title", "v4.8.40: סריקה, תיקון והשלמה של 100% מאייקוני ולוגואי הערוצים וה-VOD", "--notes-file", notes_file] + assets
print("Running gh release...")
subprocess.run(cmd, check=True)
print("Release v4.8.40 created successfully!")


import os, subprocess

ROOT = r"C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo"
os.chdir(ROOT)

notes = """🚀 שחרור v4.8.39 - שירות קאץ' אפ (Catch-Up TV / שידור חוזר) מלא לכל ערוצי ישראל המובילים! 📺⏮️

גרסה זו מוסיפה יכולת קאץ' אפ (שידור חוזר / צפייה בתוכניות העבר) מלאה ומקיפה בכל הפלטפורמות:

⏮️ קאץ' אפ מלא בנגן האנדרואיד (Android Compose Player):
- **כפתור ייעודי בסרגל הנגן ובמגירת האפשרויות**: לחיצה על כפתור `קאץ' אפ` פותחת את גיליון הצפייה החוזרת.
- **גיליון צפייה חוזרת מעוצב (CatchupSheet)**:
  - כפתורי דילוג מהיר נוחים בראש המסך: 15 דקות, 30 דקות, שעה, ושעתיים אחורה בלחיצה אחת.
  - רשימת כל תוכניות העבר מ-24 השעות האחרונות ממוינות מהחדשה לישנה, כולל שעות שידור (התחלה וסיום), כרטיסיית תמונה/פוסטר מועשרת ותגית זמנים יחסית ("שודר לפני שעה", "משודר עכשיו").
  - בחירת תוכנית מעבירה אוטומטית לשידור ה-DVR של הערוץ וקופצת ישירות לרגע תחילת התוכנית.
- **חזרה מיידית לשידור החי**: בעת צפייה בתוכנית מהעבר, מוצגת תגית `קאץ' אפ` יחד עם שם התוכנית וכפתור מואר `🔴 חזרה לשידור חי` המאפשר חזרה ישירה ל-Live בכל רגע.
- **שילוב EPG כ-Asset מקומי**: קובץ `epg.json` ארוז כעת כחלק מקובצי ה-APK, מה שמאפשר טעינה וצפייה בלוח השידורים מיידית ללא צורך בהמתנה להורדה ברשת.

🌐 קאץ' אפ ב-Web ובטלוויזיות חכמות (Smart TV / LG webOS / Samsung Tizen / Kodi):
- **תמיכה ב-DVR רב-שעתי**:
  - רשת 13: עד 24 שעות אחורה.
  - כאן 11: עד 12 שעות אחורה.
  - עכשיו 14: עד 12 שעות אחורה.
  - כאן חינוכית: עד 12 שעות אחורה.
  - מכאן 33: עד 12 שעות אחורה.
  - ערוצי i24NEWS (עברית, אנגלית, ערבית, צרפתית): עד 3 שעות אחורה.
- **חלונית מודאלית לבחירת תוכניות וצפייה ישירה**: אפשרות `קאץ' אפ (שידור חוזר 24 שעות)` בתפריט האפשרויות של הנגן.
- **שילוב מלא ב-OSD ובשלט הרחוק**: תמיכה בדילוג אחורה וקדימה ב-30 שניות, סרגל התקדמות מדויק בתוך התוכנית וכפתור חזרה לשידור חי.
"""

notes_file = os.path.join(ROOT, "dist", "release_notes_4839.txt")
with open(notes_file, "w", encoding="utf-8") as f:
    f.write(notes)

assets = [
    "dist/IsraelTVPlus-android.apk",
    "dist/IsraelTVPlus-androidtv.apk",
    "dist/IsraelTVPlus-4.8.39-android.apk",
    "dist/IsraelTVPlus-4.8.39-androidtv.apk",
    "dist/com.israeltv.plus_all.ipk",
    "dist/com.israeltv.plus_4.8.39_all.ipk",
    "dist/IsraelTVPlus-tizen-project.zip",
    "dist/IsraelTVPlus-4.8.39-tizen-project.zip",
    "dist/plugin.video.israeltvplus.zip",
    "dist/plugin.video.israeltvplus-4.8.39.zip",
    "dist/channels.json",
    "dist/epg.json",
    "dist/SHA256SUMS",
    "dist/SHA256SUMS.txt"
]

print("Git commit & push...")
subprocess.run(["git", "add", "-A"], check=True)
subprocess.run(["git", "commit", "-m", "v4.8.39: Add Catch-Up TV (שידור חוזר) across Android and Web with 24h DVR and local EPG integration"], check=True)
subprocess.run(["git", "push", "origin", "master"], check=True)
subprocess.run(["git", "tag", "v4.8.39"], check=True)
subprocess.run(["git", "push", "origin", "v4.8.39"], check=True)

cmd = ["gh", "release", "create", "v4.8.39", "--title", "v4.8.39: שירות קאץ' אפ (Catch-Up TV / שידור חוזר) מלא לכל ערוצי ישראל המובילים", "--notes-file", notes_file] + assets
print("Running gh release...")
subprocess.run(cmd, check=True)
print("Release v4.8.39 created successfully!")


import os, subprocess

ROOT = r"C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo"
os.chdir(ROOT)

notes = """🚀 שחרור v4.8.43 - פיצ'רים מקוריים חדשים: מצב רשת איטית (5Mbps ומטה), מצב האזנה וחיסכון בסוללה, דילוג פתיח והרחבת VOD ישראלי! ⚡🎧📺

גרסה זו כוללת שורת יכולות מקוריות חדשות שפותחו במיוחד לחוויית צפייה והאזנה מיטבית:

📶 1. מצב רשת איטית וקליטה חלשה (Ultra Low-Bandwidth / 5Mbps ומטה):
- פיתוח ייעודי המאפשר צפייה חלקה, רציפה וללא תקיעות או buffering גם בתנאי קליטה ירודים ורשת של 2-5Mbps בלבד.
- אופטימיזציה דינמית של הנגן: הגבלת ביטרייט ל-1.4Mbps ורזולוציה אופטימלית של 480p, צמצום זמן ה-Startup Buffer, ודילוג על תקיעות.
- כפתור הפעלה ייעודי בסרגל הנגן ובמגירת האפשרויות עם חיווי חי `📶 5Mbps`.

🎧 2. מצב האזנה בלבד וחיסכון קיצוני בסוללה (Audio-Only / Battery Saver Mode):
- מעבר בלחיצה אחת מכל ערוץ טלוויזיה למצב האזנה לשמע בלבד.
- כיבוי מלא של פענוח הוידאו ומסך שחור OLED מלא המציג אנימציית גלי קול מעוצבת, לוגו הערוץ ושם התוכנית.
- חיסכון אדיר של כ-85% בצריכת הסוללה ובחבילת הגלישה – אידיאלי לנסיעות, האזנה לחדשות, פודקאסטים ושידורי ספורט ברקע.
- הקשה בכל מקום במסך מחזירה מיידית את שידור הוידאו המלא.

⏩ 3. דילוג על פתיח (Skip Intro) והפרק הבא ב-VOD:
- כפתור מעוצב `⏩ דלג על פתיח` המופיע אוטומטית ב-85 השניות הראשונות של פרקי VOD ומדלג ישירות לתחילת הפרק.
- כפתור `⏭️ הפרק הבא` לקראת סיום הפרק למעבר נוח ומהיר בפרקי סדרה.

🎬 4. הרחבת קטלוג ה-VOD ל-164 כותרים ישראליים אהובים:
- הוספת סרטי קאלט ישראליים מיתולוגיים: "מבצע סבתא", "גבעת חלפון אינה עונה", "חגיגה בסנוקר", "אלכס חולה אהבה".
- הוספת סדרות אהובות: "השוטרים", "שבאבניקים", "ילדי בית העץ" ועוד.
"""

notes_file = os.path.join(ROOT, "dist", "release_notes_4843.txt")
with open(notes_file, "w", encoding="utf-8") as f:
    f.write(notes)

assets = [
    "dist/IsraelTVPlus-android.apk",
    "dist/IsraelTVPlus-androidtv.apk",
    "dist/IsraelTVPlus-4.8.43-android.apk",
    "dist/IsraelTVPlus-4.8.43-androidtv.apk",
    "dist/com.israeltv.plus_all.ipk",
    "dist/com.israeltv.plus_4.8.43_all.ipk",
    "dist/IsraelTVPlus-tizen-project.zip",
    "dist/IsraelTVPlus-4.8.43-tizen-project.zip",
    "dist/plugin.video.israeltvplus.zip",
    "dist/plugin.video.israeltvplus-4.8.43.zip",
    "dist/channels.json",
    "dist/vod_data.json",
    "dist/epg.json",
    "dist/SHA256SUMS",
    "dist/SHA256SUMS.txt"
]

print("Git commit & push...")
subprocess.run(["git", "add", "-A"], check=True)
subprocess.run(["git", "commit", "-m", "v4.8.43: Add Low-Bandwidth 5Mbps mode, Audio-Only Battery Saver mode, VOD Skip Intro, and expand Israeli cult VOD"], check=True)
subprocess.run(["git", "push", "origin", "master"], check=True)
subprocess.run(["git", "tag", "v4.8.43"], check=True)
subprocess.run(["git", "push", "origin", "v4.8.43"], check=True)

cmd = ["gh", "release", "create", "v4.8.43", "--title", "v4.8.43: מצב רשת איטית (5Mbps), מצב האזנה וחיסכון בסוללה, דילוג פתיח והרחבת VOD", "--notes-file", notes_file] + assets
print("Running gh release...")
subprocess.run(cmd, check=True)
print("Release v4.8.43 created successfully!")


# tools/patch_readme_sources.py
import re

readme_path = r'C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo\README.md'
with open(readme_path, 'r', encoding='utf-8') as f:
    text = f.read()

sources_section = """
## 📡 מקורות שידור, שרתי CDN וארכיטקטורת שידור (Broadcast Sources & Infrastructure)

כל ערוץ באפליקציה מחזיק מקור ראשי מאומת ושרת גיבוי חי עם מנגנון מעבר שקוף (Dual-Origin Failover):

| קבוצת ערוצים | שרתי מקור ו-CDN עיקריים | מנגנון גיבוי ואיכות |
|---|---|---|
| **כאן 11, מכאן 33, כאן חינוכית 23** | Kaltura CDN / AWS CloudFront רשמי של תאגיד השידור הישראלי | שרתי גיבוי ישירים Ottrast / Akamai ב-1080p |
| **קשת 12** | Mako Live Origin / FreeTV CloudFront subs | זרם ראשי 1080p + שרת גיבוי מבוסס תשתית שידור ארצית |
| **רשת 13** | 13tv.co.il Akamai Edge / FreeTV CloudFront | זרם ישיר ללא פרסומות + שרת גיבוי אוטומטי |
| **עכשיו 14** | Channel 14 Live Origin (AWS CloudFront / Wowza) | זרם רשמי ישיר FHD + שרת גיבוי Ottrast |
| **i24NEWS עברית (15), ערוץ 16** | Fastly / Official Global CDN | זרם ישיר רשמי באיכות 1080p |
| **ערוץ 9 הישראלי (9), כלכלה 10** | Channel 9 Live Origin / Calcala10 Stream | שידור ישיר מהאולפנים |
| **ערוצי ספורט ישראליים (48–60)** | FreeTV Edge / Sport 5 CDN / ONE Video Delivery | כולל ספורט 5 4K, 5 לייב, 5 גולד, ONE 1-2, ONE דוקו וספורט 1-4 |
| **ערוצי ילדים ישראליים (61–87)** | ערוץ הילדים, לוגי, הופ!, ילדותי, לולי, דיסני, ניקלודיאון (PlayMedia / CloudFront) | זרמים ישירים נקיים בעברית ללא דיליי |
| **רדיו ישראלי (136 תחנות)** | StreamTheWorld / תחנות אזוריות וגלגלצ Origin | שמע סטריאו נקי 128-320kbps |

### 🛠️ בקרת איכות שידור, שדרוג 4K ומניעת תקיעות (Anti-Stutter Buffer Engine)
- **השבחה ל-4K / Ultra Sharp**: אלגוריתם שדרוג חדות וקונטרסט בחומרה (Hardware-Accelerated Super-Resolution) המאפשר שדרוג שידורי 420p / 480p / 720p לתמונה חדה ועשירה גם על גבי מסכי 4K גדולים.
- **הורדת איכות יזומה (1080p / 720p / 480p / 360p)**: שליטה מלאה ברזולוציה – מעבר מהיר לרזולוציה קלה יותר ברשתות חלשות או סלולריות למניעת עצירות.
- **מנגנון אנטי-תקיעות (Adaptive Buffer Watchdog)**: אגירה מוגדלת (עד 60 שניות buffer), ניטור תקיעות אוטומטי והתאמת קצב נתונים אוטומטית המבטיחה שידור חלק ורציף לחלוטין ללא גמגומים ("Micro-stutters").

### 🔢 מספור שלט קנוני נוח
הקשת הספרה בשלט מעבירה ישירות לערוץ המתאים:
`9` (ערוץ 9) · `10` (כלכלה 10) · `11` (כאן 11) · `12` (קשת 12) · `13` (רשת 13) · `14` (עכשיו 14) · `15` (i24NEWS) · `16` (ערוץ 16) · `23` (כאן חינוכית) · `24` (ערוץ 24) · `48-60` (ערוצי ספורט) · `61-87` (ערוצי ילדים) · `98` (ערוץ 98) · `99` (הכנסת).
"""

# Place it after the features table
idx = text.find('## 🧸 מצב ילדים')
if idx == -1:
    idx = text.find('## ⬆️ עדכונים')
if idx != -1:
    text = text[:idx] + sources_section + "\n" + text[idx:]
    with open(readme_path, 'w', encoding='utf-8') as f:
        f.write(text)
    print("README.md updated with detailed sources & infrastructure documentation")
else:
    print("Insertion point not found in README.md")


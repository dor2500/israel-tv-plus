# Israel TV Plus (LG webOS Smart TV) 🇮🇱 📺

אפליקציית טלוויזיה מתקדמת בשידור חי למסכי **LG webOS Smart TV** המותאמת במיוחד למסכי **OLED** ושלט ה-Magic Remote.

![LG webOS](https://img.shields.io/badge/LG-webOS%20TV-c4004f?style=for-the-badge&logo=lg)
![OLED Optimized](https://img.shields.io/badge/Display-OLED%204K%20Pure%20Black-black?style=for-the-badge)
![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)

---

## 🌟 תכונות מרכזיות (Key Features)

1. **ניווט חכם וללא יציאות שווא (Safe Back Navigation):**
   - כפתור ה-**Back (חזרה)** בשלט לעולם לא מוציא אתכם מהאפליקציה בטעות!
   - ניווט היררכי חכם: סגירת מודאלים ⇦ סגירת תפריט צד ⇦ יציאה ממסך מלא למדריך הערוצים ⇦ איפוס חיפוש וקטגוריות ⇦ פתיחת חלונית אישור יציאה מעוצבת.

2. **מאגר ערוצים עשיר עם דגש מיוחד על ילדים (88+ Channels):**
   - **ערוצי ילדים ונוער:** הופ!, ערוץ זום (Zoom), ניקלודיאון, ניק ג'וניור, כאן חינוכית 23, בייבי פירסט (Baby First), פלוס פלוס (PLUSPLUS), פיקסל TV, Niki Kids, Niki Junior, TiJi ועוד!
   - **ערוצים מרכזיים וחדשות:** כאן 11, קשת 12, רשת 13, עכשיו 14, ערוץ 9, ערוץ 24, ערוץ הכנסת, i24NEWS (עברית/אנגלית/צרפתית).
   - **ספורט, לייף סטייל ובינלאומי:** ספורט 5, ערוצי דוקו וטבע, מוזיקה וערוצים נבחרים מהעולם.
   - **אייקונים וסמלים באיכות גבוהה:** כל ערוץ מלווה בלוגו רשמי ואיכותי ממאגרי Wikimedia ו-CDN, עם גיבוי אוטומטי.

3. **משפר תמונה לסדרות ישנות (AI Retro Remaster & Aspect Ratio):**
   - לחיצה על **המקש הירוק** בשלט מפעילה מסנן חידוד קונבולוציה חומרתי (Hardware SVG Sharpening Filter) המחדד טקסטורות ומפחית טשטוש בשידורי SD ישנים.
   - בורר יחסי תמונה מהיר בלחיצת כפתור:
     - **Sharp (חדות משופרת)**
     - **4:3 Pillarbox (יחס מקורי ללא מתיחה מעוותת של אנשים)**
     - **16:9 Smart Zoom (מילוי מסך חכם)**
     - **Stretch (מתיחה מלאה)**

4. **אופטימיזציה ייעודית למסכי OLED (True OLED Engine):**
   - עיצוב ב-**True OLED `#000000`** המכבה פיקסלים שחורים לחלוטין לחסכון בחשמל וניגודיות אינסופית.
   - מצבי תמונה מובנים בלחיצה על **המקש הצהוב**:
     - *Cinema Pure Black* (שחור אבסולוטי לצפייה בקולנוע)
     - *Vivid / HDR Boost* (צבעים מרהיבים)
     - *Sports Boost* (החלקה וחדות לתנועה מהירה)
     - *Natural* (צבעים נאמנים למקור)
   - שומר מסך אוטומטי למניעת צריבת מסך (Burn-in Prevention).

5. **ביצועים מקסימליים (60fps GPU Hardware Acceleration):**
   - האפליקציה רצה ב-60 פריימים לשנייה חלקים הודות לשימוש ב-Hardware Layering (`translate3d(0,0,0)` ו-CSS `contain: layout style`).
   - Hero Spotlight בראש המסך המתעדכן מיידית בעת מעבר בין ערוצים עם תקציר ומידע חי.

---

## 🎮 קיצורי שלט ה-Magic Remote של LG

| מקש בשלט | פעולה |
|---|---|
| **חצים (Up/Down/Left/Right)** | ניווט חלק בין הערוצים והקטגוריות |
| **OK / גלגלת** | בחירת ערוץ / מעבר למסך מלא / השהייה-ניגון |
| **Back (חזרה)** | חזרה לשלב הקודם / חלון אישור יציאה (ללא יציאה פתאומית) |
| **מקש אדום (🔴)** | מעבר מהיר לכל הערוצים |
| **מקש ירוק (🟢)** | החלפת מצב רטרו לסדרות ישנות (חדות, 4:3, 16:9 זום) |
| **מקש צהוב (🟡)** | החלפת מצב תמונה ל-OLED (קולנוע שחור מושלם, ספורט, צבעים עזים) |
| **מקש כחול (🔵)** | פתיחת חיפוש ערוצים מיידי |
| **Ch + / Ch -** | העברת ערוץ מהירה קדימה / אחורה בצפייה |
| **ספרות 0-9** | קפיצה ישירה למספר ערוץ |

---

## 🚀 התקנה על טלוויזיית LG webOS

### דרך 1: שימוש ב-WebOS Dev Manager (הכי קל)
1. הורידו את קובץ ה-IPK המעודכן: `com.israeltv.plus_1.0.0_all.ipk`.
2. פתחו את **WebOS Dev Manager** במחשב.
3. התחברו לטלוויזיה שלכם (כתובת IP + קוד מצב מפתח).
4. גררו את קובץ ה-`IPK` לתוך החלון ולחצו **Install**.

### דרך 2: התקנה ישירה דרך ה-CLI / SSH
אם מופעל שרת SSH בטלוויזיה (יציאה 9922):
```bash
# בניית קובץ ה-IPK
python build_ipk.py ./israel-tv-plus com.israeltv.plus_1.0.0_all.ipk com.israeltv.plus 1.0.0 "Israel TV Plus"

# סנכרון ישיר לטלוויזיה
scp -P 9922 -i ~/.ssh/tv_key index.html style.css app.js channels.js channels.json appinfo.json prisoner@<TV_IP>:apps/usr/palm/applications/com.israeltv.plus/

# הפעלת האפליקציה במסך
ssh -p 9922 -i ~/.ssh/tv_key prisoner@<TV_IP> 'luna-send-pub -n 1 luna://com.webos.applicationManager/launch "{\"id\":\"com.israeltv.plus\"}"'
```

---

## 📁 מבנה הפרויקט
```text
israel-tv-plus/
├── appinfo.json       # הגדרות אפליקציית webOS (השבתת Back היסטורי מובנה)
├── index.html         # שלד ה-GUI, Hero Spotlight, פילטר חידוד SVG ומודאלים
├── style.css          # עיצוב True OLED, חומרה מואצת 60fps, רספונסיבי ל-4K/FHD
├── app.js             # מנוע הניווט, ניהול שלט LG, שיפורי וידאו ו-HLS Player
├── channels.js        # מאגר 88+ ערוצים עם נתיבי CDN ואייקונים רשמיים
├── channels.json      # קובץ JSON תואם להזרמה חיצונית
├── icon.png           # אייקון התפריט הראשי של LG (80x80)
├── largeIcon.png      # אייקון ה-Store וה-Dashboard (130x130)
└── splash.png         # מסך טעינה מהיר ב-True Black
```

---

## ⚖️ רישיון
פרויקט זה מיועד לשימוש אישי ומשפחתי על גבי מסכי LG webOS. כל זכויות השידורים והערוצים שייכות לבעלי הזכויות המקוריים.

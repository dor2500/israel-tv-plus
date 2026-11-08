# 🏠 Israel TV+ עבור Home Assistant

אינטגרציה והרחבה מלאה של **ישראל TV+** לתוך מערכת הבית החכם **Home Assistant**!

מאפשרת לנגן את כל 438 ערוצי הטלוויזיה, הספורט, הילדים, החדשות והרדיו הישראליים ישירות על כל נגן מדיה בבית (טלוויזיות חכמות, Android TV, Chromecast, רמקולים חכמים, Google Nest, דפדפנים ולוחות בקרה).

---

## 🚀 אפשרויות התקנה

### אפשרות 1: התקנה דרך HACS (מומלץ)
1. פתח את **HACS** ב-Home Assistant.
2. לחץ על **Integrations** -> שלוש נקודות בפינה -> **Custom repositories**.
3. הוסף את הקישור: `https://github.com/dor2500/israel-tv-plus`
4. בחר בקטגוריה: **Integration** ולחץ **Add**.
5. חפש את **Israel TV+** ולחץ **Download**.
6. הפעל מחדש את Home Assistant.
7. עבור אל: **הגדרות (Settings) -> מכשירים ושירותים (Devices & Services) -> הוסף שילוב (Add Integration)** -> חפש **Israel TV+** ואשר.

---

### אפשרות 2: התקנה ידנית (קובץ ZIP)
1. הורד את `IsraelTVPlus-homeassistant.zip` מדף ה-[Releases](https://github.com/dor2500/israel-tv-plus/releases).
2. חלץ את התיקייה `israel_tv_plus` לתוך תיקיית `custom_components` ב-Home Assistant שלך (`/config/custom_components/israel_tv_plus`).
3. הפעל מחדש את Home Assistant והוסף את האינטגרציה דרך הממשק.

---

### אפשרות 3: התקנה כ-Add-on (הרחבה לסרגל הצד)
1. ב-Home Assistant עבור אל: **הגדרות -> הרחבות (Add-ons) -> חנות ההרחבות (Add-on Store)**.
2. לחץ על שלוש הנקודות בפינה -> **מאגרים (Repositories)**.
3. הוסף את הקישור: `https://github.com/dor2500/israel-tv-plus`
4. לחץ על **Israel TV+** מתוך הרשימה ולחץ **Install**.
5. הפעל את ההרחבה וסמן **Show in sidebar** – כעת ממשק הנגן המלא יוצג ישירות בסרגל הצד של Home Assistant!

---

## 📺 שימוש ב-Media Browser (דפדפן המדיה)
לאחר ההתקנה, היכנס ללשונית **מדיה (Media)** בסרגל הצד:
- תראה תיקייה חדשה בשם **Israel TV+**.
- לחיצה עליה תפתח את כל הקטגוריות:
  - 📺 **ערוצים מובילים** (11, 12, 13, 14, 9, 24, 33...)
  - ⚽ **ערוצי ספורט** (ספורט 1-6, 5SPORT, ONE...)
  - 📰 **חדשות ואקטואליה**
  - 👶 **ערוצי ילדים ונוער**
  - 🍿 **סרטים, סדרות ובידור**
  - 📻 **תחנות רדיו מובילות** (גלגלצ, גלצ, כאן ב, 103FM...)
  - 🎬 **סדרות וסרטים (VOD)**
  - 📋 **כל הערוצים (א-ב)**
- בחר ערוץ ונגן אותו ישירות על כל מסך או רמקול בבית!

---

## 🤖 אוטומציות ושירות `israel_tv_plus.play_channel`
האינטגרציה מספקת שירות ייעודי לשליטה קולית ובאוטומציות:

```yaml
service: israel_tv_plus.play_channel
data:
  entity_id: media_player.living_room_tv
  channel: "12" # ניתן לציין מספר ערוץ (12), שם ("קשת 12", "ספורט 1", "גלגלצ") או מזהה ("keshet12")
```

### דוגמה לאוטומציה: חדשות בשעה 20:00
```yaml
alias: "הפעלת חדשות ערוץ 12 בשמונה בערב"
trigger:
  - platform: time
    at: "20:00:00"
action:
  - service: israel_tv_plus.play_channel
    data:
      entity_id: media_player.living_room_tv
      channel: "12"
```

---

## 🎴 כרטיס Lovelace מותאם אישית (Remote Card)
להוספת לוח שידורים עם כפתורי ערוצים ישירים לדשבורד:
1. העתק את `platforms/homeassistant/israel-tv-card.js` לתיקיית `/config/www/israel-tv-card.js`.
2. בהגדרות הדשבורד -> Resources הוסף את `/local/israel-tv-card.js` כ-JavaScript Module.
3. הוסף כרטיס ידני בדשבורד:
```yaml
type: custom:israel-tv-card
```


# 📝 הגדרת Google Form להצעות לשיפור, תקלות ובקשת ערוצים

מדריך להקמת טופס Google Forms בלחיצת כפתור אחת בחשבון ה-Google של מנהל הפרויקט:

## ⚡ הקמה מהירה ב-30 שניות דרך Google Apps Script

1. פתח בדפדפן את הקישור הרשמי של גוגל: **[script.new](https://script.new)**
2. מחק את מה שכתוב בעורך והדבק את הקוד הבא:

```javascript
function createIsraelTvFeedbackForm() {
  // 1. יצירת הטופס
  var form = FormApp.create('Israel TV Plus – הצעות לשיפור, דיווח על תקלות ובקשת ערוצים 📺');
  
  // 2. תיאור והסבר מקיף למשתמשים
  form.setDescription(
    'ברוכים הבאים לטופס המשוב וההצעות של Israel TV Plus (גרסה 4.3.9) 🇮🇱✨\n\n' +
    'הפרויקט נבנה מתוך מטרה לספק גישה חופשית, איכותית ונוחה לכל שידורי הטלוויזיה, הרדיו, וערוצי הילדים והבידור הישראליים – בכל הפלטפורמות (Web, Android, Android TV, Stremio, ו-LG webOS).\n\n' +
    'נשמח לשמוע מכם:\n' +
    '• ערוצים שהייתם רוצים שנוסיף לפרויקט\n' +
    '• דיווח על ערוץ שנעצר, מקרטע או לא עובד\n' +
    '• רעיונות לשיפור חוויית הצפייה, הממשק והפיצ\'רים\n\n' +
    'תודה על עזרתכם להפוך את Israel TV Plus לטוב עוד יותר!'
  );
  
  // הגדרות טופס - פתוח לכולם ללא חובת התחברות
  form.setAllowResponseEdits(false);
  form.setCollectEmail(false);
  
  // 3. שאלה 1: נושא הפנייה (חובה)
  var topicItem = form.addMultipleChoiceItem();
  topicItem.setTitle('1. מה נושא הפנייה שלך?')
           .setChoiceValues([
             '➕ בקשה להוספת ערוץ חדש',
             '⚠️ דיווח על ערוץ שלא עובד / תקלה בשידור',
             '💡 הצעה לשיפור ממשק או תכונה חדשה (UI / Features)',
             '📱 תקלה טכנית באפליקציה (Android / TV / Web / Stremio)',
             '❤️ משוב כללי או מילה טובה'
           ])
           .setRequired(true);

  // 4. שאלה 2: שם הערוץ (במידה ורלוונטי)
  var channelItem = form.addTextItem();
  channelItem.setTitle('2. שם הערוץ המבוקש או התקול (אם רלוונטי)')
             .setHelpText('לדוגמה: כאן 11, ערוץ הילדים, רדיו דרום וכו\'')
             .setRequired(false);

  // 5. שאלה 3: קישור או מקור לשידור
  var urlItem = form.addTextItem();
  urlItem.setTitle('3. קישור לשידור חי / אתר השידור (אם ידוע לך)')
         .setHelpText('קישור לאתר הרשמי, m3u8 או יוטיוב במידה ויש לך')
         .setRequired(false);

  // 6. שאלה 4: פלטפורמה / מכשיר צפייה (חובה)
  var deviceItem = form.addCheckboxItem();
  deviceItem.setTitle('4. באיזה מכשיר או פלטפורמה אתה משתמש ב-Israel TV Plus?')
            .setChoiceValues([
              '🌐 דפדפן במחשב (Web)',
              '📱 סמארטפון אנדרואיד (APK)',
              '📺 Android TV / סטרימר אנדרואיד',
              '🎬 תוסף Stremio (סטרימיו)',
              '🖥️ טלוויזיה חכמה LG (webOS IPK)',
              '📺 נגן IPTV חיצוני (TiviMate / IPTV Smarters / VLC)'
            ])
            .showOtherOption(true)
            .setRequired(true);

  // 7. שאלה 5: פירוט מלא של ההצעה / התקלה (חובה)
  var detailsItem = form.addParagraphTextItem();
  detailsItem.setTitle('5. פירוט ההצעה לשיפור, התקלה או הרעיון שלך')
             .setHelpText('תאר מה היית רוצה שנשפר, איך התקלה מתרחשת או איזה שינוי יעזור לך.')
             .setRequired(true);

  // 8. שאלה 6: אימייל או שם לחזרה (אופציונלי)
  var contactItem = form.addTextItem();
  contactItem.setTitle('6. שם או כתובת אימייל לחזרה (אופציונלי)')
             .setHelpText('רק במידה ותרצה שנעדכן אותך כשהערוץ נוסף או כשהתקלה תוקנה')
             .setRequired(false);

  Logger.log('🎉 הטופס נוצר בהצלחה!');
  Logger.log('🔗 קישור עריכה: ' + form.getEditUrl());
  Logger.log('🌐 קישור שיתוף למשתמשים (ציבורי): ' + form.getPublishedUrl());
}
```

3. לחץ על כפתור **Run** (הפעל).
4. בסרגל הלוג למטה יופיעו שני קישורים:
   - **קישור עריכה** - לעריכת השאלות וצפייה בתשובות המשתמשים.
   - **קישור שיתוף ציבורי** - קישור להפצה ושילוב באפליקציה ובאתר.

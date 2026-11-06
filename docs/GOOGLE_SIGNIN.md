# התחברות Google – הגדרה חד-פעמית

הקוד ב-`gdrive.js` צריך שני לקוחות OAuth של פרויקט Google Cloud. כל עוד הם ריקים, האפליקציה מסבירה שההתחברות
לא זמינה במקום לשלוח ל-`Error 401: invalid_client`.

1. [console.cloud.google.com](https://console.cloud.google.com) ← פרויקט חדש `Israel TV Plus`.
2. **APIs & Services → Library** ← להפעיל את **Google Drive API**.
3. **Google Auth Platform → Branding**: שם אפליקציה, מייל תמיכה ומייל מפתח. בלי לוגו, כדי לא להיכנס לאימות מותג.
4. **Audience**: `External`, ואז **Publish app**. ב-`Testing` רק משתמשי בדיקה יכולים להיכנס, וההתחברות פגה אחרי 7 ימים.
5. **Data Access** ← להוסיף `openid`, `email`, `profile`, `.../auth/drive.file`. אלה הרשאות לא רגישות, ולא נדרש אימות של Google.
6. **Clients → Create client**, שלוש פעמים:
   * **Web application**. Authorized JavaScript origins: `https://dor2500.github.io`. בלי Redirect URI.
   * **TVs and Limited Input devices**. שומרים גם את ה-Client ID וגם את ה-Client secret.
   * **Android** (לאפליקציה בטלפון). Package name: `com.israeltv.plus`, SHA-1: `EE:4A:93:03:30:4C:22:D3:70:0E:0A:D8:C6:3B:D8:08:A5:74:D9:21`.
7. ממלאים ב-`gdrive.js` את `WEB_CLIENT_ID`, `TV_CLIENT_ID`, `TV_CLIENT_SECRET`. לקוח Android לא נכנס לקוד: Google מזהה את האפליקציה לפי החבילה והחתימה, ולכן הטלפון מתחיל לעבוד מיד כשהלקוח נוצר.

הסוד של לקוח TV אינו סודי לפי Google, כי הוא נשלח בתוך האפליקציה. GitHub עלול לחסום push שמכיל אותו (push protection),
ואז צריך לאשר את הקובץ הזה בקישור שמופיע בהודעת השגיאה.

בטלפון ההתחברות מתבצעת בבורר החשבונות של המערכת, בלי דפדפן. בטלוויזיה מוצג בתוך האפליקציה קוד ו-QR שמאשרים בטלפון, כי Google לא מאפשרת להקליד סיסמה במסך טלוויזיה בזרימה הזו.

בדיקה: באתר ובטלוויזיה, הגדרות ← חשבון Google ← התחברות. אחר כך "סנכרן עכשיו" ו"ההקלטות שלי".

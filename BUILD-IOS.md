# בניית Israel TV+ ל־iPhone (IPA)

הפרויקט כולל מעטפת iOS מבוססת Capacitor. היא טוענת את האתר הרשמי `https://dor2500.github.io/israel-tv-plus/`; לכן היא זקוקה לחיבור אינטרנט, והעדכונים נשארים באתר עצמו.

## דרישות

- מחשב Mac עם Xcode ו־Xcode Command Line Tools. בניית iOS דורשת macOS.
- חשבון Apple כדי לחתום את האפליקציה. להתקנה על מכשיר מסוים משתמשים בחתימת Development/Ad Hoc ובפרופיל מתאים. הפצה ב־TestFlight או ב־App Store דורשת חברות בתוכנית Apple Developer.

## בנייה

1. פתחו Terminal בתיקיית הפרויקט.
2. הריצו `npm ci`.
3. הריצו `npx cap sync ios`.
4. הריצו `npx cap open ios`.
5. ב־Xcode פתחו את ה־target בשם **App**, עברו ל־**Signing & Capabilities**, בחרו את צוות Apple שלכם, ובחרו מכשיר יעד.
6. בחרו **Product → Archive**, ואז **Distribute App** ובחרו שיטת הפצה מתאימה. Xcode ייצא קובץ `.ipa` חתום.

אם ה־Bundle ID `com.israeltv.plus.ios` כבר תפוס בחשבון Apple שלכם, שנו אותו ב־Xcode ל־Bundle ID ייחודי לפני החתימה.

## מגבלות

- ה־IPA מציג את גרסת האתר העדכנית, ולכן אינו כולל את כל קובצי האתר בתוכו ואינו עובד ללא אינטרנט.
- לא נוצר IPA בתיקיית הפלט: המחשב הנוכחי הוא Windows, ו־Xcode זמין רק ב־macOS. גם אין בסביבה תעודת חתימה או פרופיל התקנה של Apple.
- אם המטרה היא רק סמל במסך הבית באייפון, אפשר לעשות זאת כבר עכשיו ב־Safari: לפתוח את האתר, לבחור **שיתוף → הוספה למסך הבית**.

# RELEASE – איך מוציאים גרסה של ישראל TV+

המסמך הזה הוא הנוהל הקבוע. הוא נכתב אחרי הבלאגן של 4.4.5–4.4.7: תגיות שנוצרו בלי ריליס,
ו-`version.json` שהצביע לקבצים שלא הועלו (כל כפתור "עדכון" החזיר 404).

## הכלל היחיד שחייבים לשמור
**תגית = ריליס מלא.** אין לדחוף תגית `vX.Y.Z` לפני שכל הקבצים שלה עלו לריליס.
האפליקציה מורידה לפי `version.json` → `native.<platform>.url`, כלומר
`https://github.com/dor2500/israel-tv-plus/releases/download/v<version>/<asset>`.
אם הריליס לא קיים – כל העדכונים נשברים בשקט.

## מה כל ריליס חייב לכלול
| קובץ | פלטפורמה | נבנה על ידי |
| --- | --- | --- |
| `IsraelTVPlus-android.apk` | אנדרואיד / טאבלטים | `android/gradlew :app:assembleRelease` |
| `IsraelTVPlus-androidtv.apk` | Android TV / Google TV / Fire TV (אותו APK) | העתקה של הקובץ הקודם |
| `IsraelTVPlus-wearos.apk` | Wear OS – **חובה**: אפליקציית הטלפון מורידה אותו מהריליס | `wear/gradlew :app:assembleRelease` |
| `com.israeltv.plus_all.ipk` | LG webOS | `python -I tools/build_ipk.py dist` |
| `IsraelTVPlus-tizen-project.zip` | סמסונג Tizen (פרויקט לפתיחה ב-Tizen Studio) | `python -I tools/build_tizen_zip.py dist` |
| `IsraelTVPlus-windows-setup.exe` + `-portable.zip` | Windows | `tools/build_windows.ps1` |
| `IsraelTVPlus-linux-x64.zip` / `-linux-arm64.zip` | לינוקס | `python -I tools/build_desktop.py` |
| `IsraelTVPlus-macos-arm64.zip` / `-macos-x64.zip` | macOS | `python -I tools/build_desktop.py` |
| `version.json`, `channels.json`, הפלייליסטים | בדיקה / נגני IPTV | `python -I tools/make_version.py` |
| `SHA256SUMS.txt` | בדיקת תקינות | `sha256sum *` |

## סדר הפעולות לגרסה חדשה
1. **עדכנו את `CHANGELOG.md`** – פרק `## X.Y.Z` עם שורות `- `. ממנו נגזרים גם
   `version.json.notes` (חלון העדכון באפליקציה) וגם גוף הריליס ב-GitHub.
2. **עדכנו `VERSION`** (למשל `4.5.0`).
3. `python -I tools/make_version.py` – חותם את הגרסה ב-`version.js`, `version.json`,
   `appinfo.json` (LG), `platforms/tizen/config.xml`, `platforms/windows/*`.
4. אם השתנו ערוצים: `python -I tools/make_playlists.py` ואחר כך `python -I tools/make_stremio.py`.
5. **בנו את כל החבילות** (טבלה למעלה). חובה לבדוק שהן נבנו, לא להניח.
6. בדקו התקנה בפועל: `adb install -r dist/IsraelTVPlus-android.apk` ואז פתחו את האפליקציה.
7. `git add -A && git commit -m "release: Israel TV+ X.Y.Z"` ו-`git push origin master`.
8. `git tag vX.Y.Z && git push origin vX.Y.Z` – מכאן ה-workflow (`.github/workflows/release.yml`)
   בונה ומעלה את הקבצים לריליס לבד.
9. בדקו שהריליס אכן מכיל את כולם:
   `gh release view vX.Y.Z --json assets --jq '.assets[].name'`
10. רק עכשיו האתר מתעדכן (GitHub Pages) וכל האפליקציות רואות את הגרסה החדשה.

## מפתח החתימה – הדבר היחיד שאי אפשר לשחזר
העדכון באנדרואיד מתקין מעל הגרסה הקודמת רק אם שני ה-APK חתומים באותו מפתח.

* הקובץ: `%USERPROFILE%\.android\israel-tv-plus.jks` והסיסמה ב-`israel-tv-plus.pass` שלידו
  (אותו מפתח גם ל-Wear). **גבו אותם** – אם הם הולכים לאיבוד, כל המשתמשים הקיימים יצטרכו
  להסיר את האפליקציה לפני התקנה של גרסה חדשה.
* ב-CI: סודות הריפו `ANDROID_KEYSTORE_BASE64` (base64 של ה-jks) ו-`ANDROID_KEYSTORE_PASSWORD`.
  בלי הסודות ה-workflow עדיין בונה APK, אבל חתום במפתח זמני – טוב לבדיקות בלבד.

## סדר גרסאות (versionCode)
`4.3.x → 500+patch`, `4.4.x → 540+patch`, אחרת `max(550, major*100+minor*10+patch)`.
`4.5.0 → 550`. אסור לרדת – אנדרואיד מסרב להתקין גרסה עם קוד נמוך מהמותקנת
(`INSTALL_FAILED_VERSION_DOWNGRADE`). היסטורית המספרים עלתה ל-4.6.0 וחזרה ל-4.3.0 –
הקוד עצמו נשאר עולה, וזה מה שקובע.

## תגיות היסטוריות
`v4.4.4` לא קיימת (הגרסה מופיעה ב-CHANGELOG אבל לא נבנתה), ול-`v4.4.5`, `v4.4.6`, `v4.4.7`
לא היה ריליס. החל מ-4.5.0 הריליס כולל גם את ה-APK של אותן גרסאות (למי שרוצה לחזור אחורה),
ורשימת התגיות ב-`docs/release-audit-4.5.0.md`.

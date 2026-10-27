# מקורות – מאיפה מגיע כל ערוץ

נוצר אוטומטית מ-`channels.json` על ידי `tools/make_sources.py`. סה"כ **339** פריטים: 226 ערוצי טלוויזיה, 113 תחנות רדיו, 0 אפליקציות/שירותים.

## איך הערוצים נאספו ונבדקו

| מקור מידע | מה נלקח ממנו |
|---|---|
| [iptv-org](https://github.com/iptv-org/iptv) ([API](https://iptv-org.github.io/api/)) | מאגר קהילתי פתוח של כתובות שידור ציבוריות. ממנו רוב ערוצי הטלוויזיה, הלוגואים, השפה ומזהה הערוץ (`iptv`) |
| [radio-browser.info](https://www.radio-browser.info/) | מאגר פתוח של תחנות רדיו. ממנו תחנות הרדיו הישראליות (`/stations/bycountrycodeexact/IL`) |
| [Free-TV/IPTV](https://github.com/Free-TV/IPTV) | רשימת ערוצים חינמיים רשמיים – שימשה להצלבה של ערוצי ישראל |
| CDN של כאן + StreamTheWorld | בדקתי ידנית את שמות הזרמים של תחנות כאן (`KAN_BET`, `KAN_GIMMEL` וכו'). רשימות ה-HLS של כאן לרדיו חוזרות ריקות, לכן הרדיו מגיע מ-StreamTheWorld – המפיץ הרשמי שלהם |
| Google Play | מזהי האפליקציות הרשמיות (פרטנר, סלקום, STING, yes, HOT, כאן, 12, 13, 14, ספורט 5) |
| גרסאות קודמות של הפרויקט | ערוצים שנוספו ידנית לפני v2 (מסומנים "רשימה ידנית") |

**בדיקות:** כל שידור נבדק לפני שנכנס (פלייליסט ← תת-פלייליסט ← קטע וידאו/שמע אמיתי, `tools/add_israeli.py`); מכל ערוץ וידאו צולמה תמונה עם ffmpeg והושוותה לשם הערוץ (`tools/check_channels.py`); תחנות הרדיו ומקורות האתר נוגנו בדפדפן Chromium אמיתי.

## ⚖️ מצב הזכויות – חשוב לדעת

- **184 שידורים רשמיים** – מהשרתים של הגוף המשדר עצמו או של המפיץ שלו (כאן, 13, 14, i24NEWS, גלי צה"ל, תחנות הרדיו, DW, אל-ג'זירה...).
- **6 ערוצי FAST** – ערוצים חינמיים עם פרסומות שהבעלים מפיצים בעצמם (Pluto TV, BabyFirst, LEGO, Baby Shark...).
- **0 ערוצים מהעולם** – שידורים פתוחים מהמאגר הציבורי iptv-org (ייבוא אוטומטי, `tools/import_iptv.py`): כל אחד נבדק שהוא משדר ושיש בו תמונה, ללא ערוצים שדורשים מנוי/כתובת מיוחדת. רובם מהשרתים של הערוצים עצמם, אבל הבעלים לא נבדקו אחד-אחד.
- **149 ממסרי צד שלישי** – בעיקר `stream.mcquack.net` (35 ערוצים). אלה **אינם** השרתים של הערוצים: אתר חיצוני קולט את השידור ומפיץ אותו מחדש. בהם נמצאים גם ערוצים בתשלום (HOT, yes, viju/TV1000, ערוצי ספורט) – סביר מאוד שההפצה הזו **ללא רישיון** מבעלי הזכויות, והם עלולים להיעלם בכל רגע. ההחלטה אם להשאיר אותם היא שלך; הסקריפטים לא מוסיפים ממסרים חדשים של ערוצים בתשלום.
- **שירותי המנויים** (פרטנר, סלקום, STING, yes, HOT) – אין בפרויקט שום שידור שלהם; הכרטיס רק פותח את האפליקציה/האתר הרשמי, וצפייה דורשת מנוי.

> הסיווג רשמי / FAST / ממסר נקבע לפי שם השרת שממנו מגיע השידור (למשל CDN של הגוף המשדר), ולא אושר מול כל גוף משדר בנפרד.

## לפי שרת

| שרת | מי זה | סוג | מספר |
|---|---|---|---|
| `b84203c4.ottrast.com` | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | 79 |
| `d1zqtf09wb8nt5.cloudfront.net` | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | 59 |
| `stream.mcquack.net` | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | 35 |
| `gb25.streamgates.net` | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | 34 |
| `cdn.cybercdn.live` | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | 16 |
| `playerservices.streamtheworld.com` | Triton StreamTheWorld – מפיץ הרדיו של כאן | ✅ רשמי | 7 |
| `glzwizzlv.bynetcdn.com` | גלי צה"ל / גלגלצ – CDN רשמי (Bynet) | ✅ רשמי | 6 |
| `cast.breslevforyou.co.il` | רדיו ברסלב – שרת התחנה | ✅ רשמי | 5 |
| `icecast.kab.tv` | icecast.kab.tv | ⚠️ ממסר צד שלישי | 5 |
| `mako-streaming.akamaized.net` | קשת – ה-CDN של mako (עם הרשאת צפייה של mako) | ✅ רשמי | 4 |
| `cdna.streamgates.net` | StreamGates – ה-CDN של התחנה | ✅ רשמי | 4 |
| `kancdn.medonecdn.net` | כאן – תאגיד השידור (CDN רשמי) | ✅ רשמי | 3 |
| `r.il.cdn-redge.media` | Redge – ה-CDN הרשמי של כאן, ערוץ 14 וכלכלה 10 | ✅ רשמי | 3 |
| `skynewsau-live.akamaized.net` | skynewsau-live.akamaized.net | ⚠️ ממסר צד שלישי | 3 |
| `jointil.com` | jointil.com | ⚠️ ממסר צד שלישי | 3 |
| `kneset.gostreaming.tv` | ערוץ הכנסת – השידור של הכנסת (GoStreaming) | ✅ רשמי | 2 |
| `hlspackager.akamaized.net` | i24NEWS – CDN Akamai של השידור הרשמי | ✅ רשמי | 2 |
| `ch16israel-cdn.encoders.immergo.tv` | ערוץ 16 – שרת רשמי | ✅ רשמי | 2 |
| `rgelive.akamaized.net` | ספורט 5 – השידור הרשמי של RGE (Akamai) | ✅ רשמי | 2 |
| `dash3.antik.sk` | Antik Telecom (סלובקיה), נתיבי test | ⚠️ ממסר צד שלישי | 2 |
| `ncdn-live-bfm.pfd.sfr.net` | BFM – SFR (רשמי) | ✅ רשמי | 2 |
| `radio.streamgates.net` | StreamGates – ה-CDN של התחנה | ✅ רשמי | 2 |
| `media2.93fm.co.il` | קול חי – שרת התחנה | ✅ רשמי | 2 |
| `icecast.live` | icecast.live | ⚠️ ממסר צד שלישי | 2 |
| `live.ecast.co.il` | eCast – שרת התחנה | ✅ רשמי | 2 |
| `d18b0e6mopany4.cloudfront.net` | רשת 13 – AWS CloudFront | ✅ רשמי | 1 |
| `ynet-live-01.ynet-pic1.yit.co.il` | ynet – השרת של ידיעות אחרונות | ✅ רשמי | 1 |
| `live.wcdn.co.il` | live.wcdn.co.il | ✅ רשמי | 1 |
| `stream.panet.com` | פאנט – הלא TV (רשמי) | ✅ רשמי | 1 |
| `d1yd8hohnldm33.cloudfront.net` | ערוץ FAST ישראלי – AWS CloudFront | ✅ FAST חינמי | 1 |
| `1247634592.rsc.cdn77.org` | שלנו TV – CDN77 | ✅ רשמי | 1 |
| `live.creacast.com` | ILTV – CreaCast (רשמי) | ✅ רשמי | 1 |
| `jlt-jltv-xumo.otteravision.com` | JLTV – Ottera / Xumo FAST | ✅ FAST חינמי | 1 |
| `janya-digimix.akamaized.net` | Angel TV Hebrew – Akamai (רשמי) | ✅ רשמי | 1 |
| `reshet.g-mana.live` | רשת 13 – השרת הרשמי (G-Mana) | ✅ רשמי | 1 |
| `reshetbetvideo.media.kan.org.il` | reshetbetvideo.media.kan.org.il | ⚠️ ממסר צד שלישי | 1 |
| `cdn1.cast-tv.com` | cdn1.cast-tv.com | ⚠️ ממסר צד שלישי | 1 |
| `636ffd31f0e12.streamlock.net` | 636ffd31f0e12.streamlock.net | ⚠️ ממסר צד שלישי | 1 |
| `svs.itworkscdn.net` | Asharq Discovery – ItWorks (רשמי) | ✅ רשמי | 1 |
| `edge3.uk.kab.tv` | קבלה לעם – שרת רשמי | ✅ רשמי | 1 |
| `edge2.il.kab.tv` | קבלה לעם – השרת של הערוץ | ✅ רשמי | 1 |
| `linear901-oo-hls0-prd-gtm.delivery.skycdp.com` | linear901-oo-hls0-prd-gtm.delivery.skycdp.com | ⚠️ ממסר צד שלישי | 1 |
| `live-stream.skynewsarabia.com` | live-stream.skynewsarabia.com | ⚠️ ממסר צד שלישי | 1 |
| `stream.skynewsarabia.com` | stream.skynewsarabia.com | ⚠️ ממסר צד שלישי | 1 |
| `newidco-babysharktv-1-us.roku.wurl.tv` | Baby Shark TV – Wurl (Roku FAST) | ✅ FAST חינמי | 1 |
| `cdn.fast.jwp.services` | BabyFirst – JW Player FAST | ✅ FAST חינמי | 1 |
| `zoomoo-samsungau.amagi.tv` | amagi.tv | ✅ FAST חינמי | 1 |
| `dltiqboxjw21d.cloudfront.net` | LEGO Channel – AWS CloudFront FAST | ✅ FAST חינמי | 1 |
| `skyfolk.mk` | Sky Folk Live | ✅ רשמי | 1 |
| `stream.4fun.tv` | 4FUN TV – שרת רשמי | ✅ רשמי | 1 |
| `176.118.197.101` | שרת IP ללא שם (1HD) | ⚠️ ממסר צד שלישי | 1 |
| `dwamdstream102.akamaized.net` | DW – Akamai (רשמי) | ✅ רשמי | 1 |
| `eco-live.mediacast.co.il` | eco99fm – Mediacast (רשמי) | ✅ רשמי | 1 |
| `102.livecdn.biz` | 102FM – LiveCDN (רשמי) | ✅ רשמי | 1 |
| `1075.livecdn.biz` | רדיו חיפה – LiveCDN (רשמי) | ✅ רשמי | 1 |
| `rs.mizrahit.fm` | rs.mizrahit.fm | ⚠️ ממסר צד שלישי | 1 |
| `www.rs.mizrahit.fm` | www.rs.mizrahit.fm | ⚠️ ממסר צד שלישי | 1 |
| `streaming.radio.co` | streaming.radio.co | ⚠️ ממסר צד שלישי | 1 |
| `kzradio.mediacast.co.il` | רדיו הקצה – Mediacast (רשמי) | ✅ רשמי | 1 |
| `995.livecdn.biz` | 99.5FM – LiveCDN (רשמי) | ✅ רשמי | 1 |
| `176.9.7.145` | 176.9.7.145 | ⚠️ ממסר צד שלישי | 1 |
| `stream10.vestaradio.com` | stream10.vestaradio.com | ⚠️ ממסר צד שלישי | 1 |
| `radio.betteradio.live` | radio.betteradio.live | ⚠️ ממסר צד שלישי | 1 |
| `ystream.live` | ystream.live | ⚠️ ממסר צד שלישי | 1 |
| `online.star33radio.com` | online.star33radio.com | ⚠️ ממסר צד שלישי | 1 |
| `n0a.radiojar.com` | n0a.radiojar.com | ⚠️ ממסר צד שלישי | 1 |
| `liveradio.co.il` | liveradio.co.il | ✅ רשמי | 1 |
| `listen6.myradio24.com` | listen6.myradio24.com | ⚠️ ממסר צד שלישי | 1 |
| `cast.radionahariya.com` | רדיו נהריה – שרת התחנה | ✅ רשמי | 1 |
| `radio.diki-mizrahit.co.il` | רדיו שירי דיכאון – השרת של התחנה | ✅ רשמי | 1 |
| `play.radioking.io` | play.radioking.io | ⚠️ ממסר צד שלישי | 1 |
| `1062onair.runi.ac.il` | runi.ac.il | ✅ רשמי | 1 |
| `radioangelika.com` | radioangelika.com | ✅ רשמי | 1 |
| `www.kavkazportal.com` | www.kavkazportal.com | ⚠️ ממסר צד שלישי | 1 |
| `listen7.myradio24.com` | orenu.co.il | ✅ רשמי | 1 |
| `listen.radioking.com` | RadioKing – נגן השידור של Studio Qualita | ✅ רשמי | 1 |
| `s3.yesstreaming.net` | YesStreaming – זרם הנגן באתר Radio Plus | ✅ רשמי | 1 |
| `stream.jewishmusicstream.com` | stream.jewishmusicstream.com | ⚠️ ממסר צד שלישי | 1 |

## 📺 ערוצי ישראל - שידור חי

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 1 | כאן 11 FHD | כאן – תאגיד השידור (CDN רשמי) | ✅ רשמי | iptv-org (Kan11.il) | ✅ |
| 2 | קשת 12 FHD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | iptv-org (Keshet12.il) | ✅ |
| 3 | רשת 13 FHD | רשת 13 – AWS CloudFront | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 4 | עכשיו 14 FHD | Redge – ה-CDN הרשמי של כאן, ערוץ 14 וכלכלה 10 | ✅ רשמי | iptv-org (Now14.il) | ✅ |
| 5 | i24NEWS עברית HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | iptv-org (i24NEWSHebrew.il) | ✅ |
| 6 | ערוץ 9 הישראלי FHD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | iptv-org (Channel9.il) | ✅ |
| 7 | מכאן 33 HD | כאן – תאגיד השידור (CDN רשמי) | ✅ רשמי | iptv-org (Makan33.il) | ✅ |
| 8 | ערוץ 24 HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | iptv-org (Channel24.il) | ✅ |
| 9 | ערוץ הכנסת 99 HD | ערוץ הכנסת – השידור של הכנסת (GoStreaming) | ✅ רשמי | iptv-org (KnessetChannel.il) | ✅ |
| 10 | ערוץ כלכלה 10 HD | Redge – ה-CDN הרשמי של כאן, ערוץ 14 וכלכלה 10 | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 11 | ynet Live | ynet – השרת של ידיעות אחרונות | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 12 | וואלה!+ חדשות – שידור חי HD | live.wcdn.co.il | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 13 | HOT בידור ישראלי HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 14 | HOT ריאל HD (HOT Real) | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 15 | yes דוקו HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 16 | HOT 8 מדע ודוקו HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 17 | HOT מיינד דוקו והעשרה HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 18 | ערוץ החיים הטובים HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 19 | ערוץ בית+ Home Plus HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 20 | ערוץ העיצוב HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 21 | ערוץ הבריאות HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 22 | ערוץ הטיולים HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 23 | ערוץ הנופש והחופש HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 24 | ערוץ האוכל Food Network HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 25 | ערוץ פודי Foody HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 26 | ערוץ האוכל והבישול – קשת / FreeTV | קשת – ה-CDN של mako (עם הרשאת צפייה של mako) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 27 | ערוץ הבידור E! ישראל HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 28 | ערוץ אגו Ego HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 29 | ערוץ הריאליטי HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 30 | ערוץ השעשועונים HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 31 | ערוץ ההומור והצחוק HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 32 | ערוץ Beautyz אופנה ויופי HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 33 | ערוץ מוזיקה il HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 34 | ערוץ המוזיקה – קשת / FreeTV | קשת – ה-CDN של mako (עם הרשאת צפייה של mako) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 35 | ערוץ רוקדים עם כוכבים 24/7 HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 36 | ערוץ 98 – האזרח הוותיק HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 37 | Hala TV – ערוץ הלא HD | פאנט – הלא TV (רשמי) | ✅ רשמי | סריקת ערוצים מאומתת | ✅ |
| 38 | HOT ערוץ החופש HD | ערוץ FAST ישראלי – AWS CloudFront | ✅ FAST חינמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 39 | Shelanu.TV – ערוץ שלנו HD | שלנו TV – CDN77 | ✅ רשמי | סריקת ערוצים מאומתת | ✅ |
| 40 | ILTV ישראל בצרפתית | ILTV – CreaCast (רשמי) | ✅ רשמי | iptv-org (ILTV.fr) | ✅ |
| 41 | JLTV טלוויזיה יהודית HD | JLTV – Ottera / Xumo FAST | ✅ FAST חינמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 42 | Angel TV Hebrew HD | Angel TV Hebrew – Akamai (רשמי) | ✅ רשמי | סריקת ערוצים מאומתת | ✅ |
| 43 | כאן 11 – כתוביות | Redge – ה-CDN הרשמי של כאן, ערוץ 14 וכלכלה 10 | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 44 | רשת 13 – נגיש | רשת 13 – השרת הרשמי (G-Mana) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 45 | ערוץ הכנסת – נגיש | ערוץ הכנסת – השידור של הכנסת (GoStreaming) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 46 | i24NEWS English | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 47 | i24NEWS צרפתית HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | iptv-org (i24NEWSFrench.il) | ✅ |
| 48 | i24NEWS العربية HD | i24NEWS – CDN Akamai של השידור הרשמי | ✅ רשמי | זרם i24NEWS Arabic ב-iptv-org; קיום הערוץ אושר באתר i24NEWS | ✅ |
| 49 | i24NEWS English World HD | i24NEWS – CDN Akamai של השידור הרשמי | ✅ רשמי | סריקת ערוצים מאומתת | ✅ |
| 50 | FreeTV לייף סטייל HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 51 | FreeTV דוקו ישראלי HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 52 | FreeTV אוכל ובישול HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 53 | ערוץ הקריוקי הישראלי HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 54 | ערוץ המוזיקה הישראלי 24/7 HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 55 | כאן רשת ב – אולפן וידאו חי HD | reshetbetvideo.media.kan.org.il | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 56 | הכותל המערבי – שידור חי 24/7 | cdn1.cast-tv.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 57 | קשת 12 – כתוביות ונגישות HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 58 | עכשיו 14 – כתוביות ונגישות HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |

## 📺 ילדים ונוער (בעברית)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 59 | ערוץ הילדים HD (המקום האמיתי) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 60 | ערוץ לוגי HD (Logi) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 61 | כאן חינוכית 23 FHD | כאן – תאגיד השידור (CDN רשמי) | ✅ רשמי | iptv-org (KanEducational.il) | ✅ |
| 62 | ערוץ הופ! ילדים בעברית HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 63 | ערוץ הופ! ילדות ישראלית HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 64 | ערוץ הופ! פלא HD (Hop Pele) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 65 | ערוץ לולי לפעוטות בעברית HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | iptv-org (`Luli.il`) | ❌ רק באפליקציה (HTTP) |
| 66 | ערוץ דיסני ישראל HD (Disney Channel) | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | iptv-org (`DisneyChannel.il`) | ❌ רק באפליקציה (HTTP) |
| 67 | ערוץ דיסני ג'וניור ישראל HD (Disney Jr.) | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | iptv-org (`DisneyJunior.il`) | ❌ רק באפליקציה (HTTP) |
| 68 | ערוץ ניקלודיאון ישראל HD (Nickelodeon) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 69 | ערוץ ניק ג'וניור ישראל HD (Nick Jr.) | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | iptv-org (`NickJr.il`) | ❌ רק באפליקציה (HTTP) |
| 70 | ערוץ טין ניק ישראל HD (TeenNick) | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | iptv-org (`TeenNick.il`) | ❌ רק באפליקציה (HTTP) |
| 71 | ערוץ זום ZOOM ילדים ונוער HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | iptv-org (Zoom.il) | ❌ רק באפליקציה (HTTP) |
| 72 | ערוץ ג'וניור HD (Junior) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 73 | ערוץ יויו HD (YoYo) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 74 | ערוץ בייבי ישראל HD (BabyTV) | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 75 | yes קולנוע ילדים FHD (yes Movies Kids) | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 76 | HOT סינמה פמילי HD (סרטי ילדים ומשפחה) | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 77 | ערוץ WIZ! לילדים ונוער HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 78 | ערוץ HOT באזז לנוער HD (HOT Buzz) | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 79 | ערוץ FOMO לנוער ורשת HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 80 | ערוץ ילדותי HD (Yalduti) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 81 | ערוץ שירי ילדותי HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 82 | ערוץ A+ ילדים HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 83 | ערוץ הכוכבים לילדים ונוער HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 84 | ערוץ 16 ילדים ונוער HD | ערוץ 16 – שרת רשמי | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |

## 📺 ספורט (Sports)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 88 | ספורט 1 HD (Sport 1) | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 89 | ספורט 2 HD (Sport 2) | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 90 | ספורט 3 HD (Sport 3) | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 91 | ספורט 4 FHD (Sport 4) | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 92 | ספורט 6 HD (Sport 6) | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 93 | ספורט 5 FHD (5SPORT) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 94 | ספורט 5 פלוס FHD (5PLUS) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 95 | ספורט 5 לייב FHD (5LIVE) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 96 | ספורט 5 גולד HD (5GOLD) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 97 | ספורט 5 סטארס FHD (5STARS) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 98 | ספורט 5 מקס FHD (5MAX) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 99 | ספורט 5 4K UHD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 100 | ערוץ ONE 1 FHD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 101 | ערוץ ONE 2 FHD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 102 | ערוץ ONE דוקו HD (ONE DOCO) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 103 | ערוץ ONE Edge HD (אדג' ספורט) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 104 | יורוספורט 2 HD (Eurosport 2) | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 105 | Sky Racing 1 HD (סקיי רייסינג ספורט) | 636ffd31f0e12.streamlock.net | ⚠️ ממסר צד שלישי | שידור רשמי Sky | ✅ |
| 106 | ספורט פלוס HD (viju+ Sport) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (vijuPlusSport.ru) | ✅ דרך מקור חלופי: Cinerama.uz – ספק IPTV באוזבקיסטן |
| 107 | ספורט סטאנטה Setanta Sports 2 HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (SetantaSports2Eurasia.ie) | ❌ רק באפליקציה (HTTP) |
| 108 | ערוץ אקסטרים Extreme Sports HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 109 | ספורט 5 – שידור חי | ספורט 5 – השידור הרשמי של RGE (Akamai) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |

## 📺 סרטים וסדרות (Movies & Series)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 110 | HOT 3 ערוץ הדגל FHD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | iptv-org (`Hot3.il`) | ❌ רק באפליקציה (HTTP) |
| 111 | HOT HBO סדרות פרימיום HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | iptv-org (`HotHBO.il`) | ❌ רק באפליקציה (HTTP) |
| 112 | HOT סינמה 1 FHD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 113 | HOT סינמה 2 HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 114 | HOT סינמה 3 FHD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 115 | HOT סינמה 4 HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | iptv-org (`HotCinema4.il`) | ❌ רק באפליקציה (HTTP) |
| 116 | HOT סינמה 5 HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 117 | HOT קולנוע ישראלי HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 118 | HOT קולנוע רומנטי HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 119 | HOT דרמה HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 120 | HOT זון HD (HOT Zone) | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 121 | HOT קומדי HD (HOT Comedy) | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 122 | yes קולנוע ישראלי HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 123 | yes קולנוע אקשן FHD (yes Movies Action) | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 124 | yes קולנוע דרמה FHD (yes Movies Drama) | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 125 | yes קולנוע קומדיה FHD (yes Movies Comedy) | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 126 | yes TV אקשן HD (yes TV Action) | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 127 | yes TV דרמה HD (yes TV Drama) | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 128 | yes TV קומדיה HD (yes TV Comedy) | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | iptv-org (`YesTvComedy.il`) | ❌ רק באפליקציה (HTTP) |
| 129 | ערוץ ויוה טלנובלות HD (Viva) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 130 | ערוץ ויוה איסטנבול HD (Viva Istanbul) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 131 | ערוץ ויוה פרימיום HD (Viva Premium) | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 132 | הערוץ הים תיכוני HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 133 | הערוץ הים תיכוני+ HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 134 | ערוץ הדרמות הטורקיות+ FHD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 135 | ערוץ הדרמות הטורקיות 2 FHD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 136 | ערוץ הדרמות הטורקיות 3 FHD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 137 | ערוץ הסרטים הים תיכוני 1 HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 138 | ערוץ הסרטים הים תיכוני 2 HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 139 | ערוץ האקשן העולמי 1 HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 140 | ערוץ האקשן העולמי 2 HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 141 | ערוץ הדרמות הספרדיות 1 HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 142 | ערוץ הדרמות הספרדיות 2 HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 143 | ערוץ הדרמות ההודיות 1 HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 144 | ערוץ הדרמות ההודיות 2 HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 145 | ערוץ בוליווד סרטים הודיים HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 146 | ערוץ בוליווד קלאסי HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 147 | ערוץ בולישואו Bollyshow HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 148 | ערוץ הקומדיות הקלאסיות HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 149 | ערוץ האהבה והרומנטיקה HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 150 | ערוץ ארץ נהדרת 24/7 | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 151 | סברי מרנן 24/7 – קשת / FreeTV | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 152 | ערוץ הדרמה – קשת / FreeTV | קשת – ה-CDN של mako (עם הרשאת צפייה של mako) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 153 | ערוץ הקומדיה – קשת / FreeTV | קשת – ה-CDN של mako (עם הרשאת צפייה של mako) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 154 | FreeTV סרטים ישראלי HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 155 | FreeTV סרטים פעולה HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 156 | FreeTV סרטים קומדיה HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 157 | FreeTV סרטים דרמה HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 158 | FreeTV סרטים מתח HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 159 | FreeTV סרטים אימה HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 160 | FreeTV סרטים רומנטי HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 161 | FreeTV סרטים פיל גוד HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 162 | FreeTV סרטים משפחה HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 163 | FreeTV 10 Stars סרטים HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 164 | FreeTV סדרות דרמה HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 165 | FreeTV סדרות קומדיה HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 166 | FreeTV סדרות קומדיה 2 HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 167 | ערוץ הדרמות היומיות HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 168 | ערוץ וואמוס HD (Vamos) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 169 | FreeTV גלובל סדרות HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 170 | ערוץ סרטי אימה HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (Kinouzhas.ru) | ❌ רק באפליקציה (HTTP) |
| 171 | קולנוע להיטים KinoHit HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (KinoHit.ru) | ✅ דרך מקור חלופי: Cinerama.uz – ספק IPTV באוזבקיסטן |
| 172 | משפחת סימפסון 24/7 HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 173 | קולנוע רטרו VHS Cinema HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |

## 📺 טבע ודוקו (Documentary & Nature)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 174 | נשיונל ג'יאוגרפיק ישראל HD (Nat Geo) | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | iptv-org (`NationalGeographic.il`) | ❌ רק באפליקציה (HTTP) |
| 175 | נשיונל ג'יאוגרפיק וויילד FHD (Nat Geo Wild) | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | iptv-org (`NationalGeographicWild.il`) | ❌ רק באפליקציה (HTTP) |
| 176 | ערוץ דיסקברי ישראל FHD (Discovery) | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | iptv-org (`DiscoveryChannel.il`) | ❌ רק באפליקציה (HTTP) |
| 177 | ערוץ ההיסטוריה ישראל FHD (History) | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | iptv-org (`History.il`) | ❌ רק באפליקציה (HTTP) |
| 178 | ערוץ הטבע הישראלי HD (Teva) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 179 | Animal Planet World HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (AnimalPlanet.ru) | ❌ רק באפליקציה (HTTP) |
| 180 | Discovery חקירות (ID) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (InvestigationDiscovery.ru) | ❌ רק באפליקציה (HTTP) |
| 181 | עולם וטבע HD (viju+ Planet) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (vijuPlusPlanet.ru) | ❌ רק באפליקציה (HTTP) |
| 182 | Asharq Discovery HD | Asharq Discovery – ItWorks (רשמי) | ✅ רשמי | iptv-org (AsharqDiscovery.sa) | ✅ |

## 📺 מסורת וקהילה

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 183 | ערוץ הידברות 97 HD | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | iptv-org (Hidabroot.il) | ✅ |
| 184 | ערוץ קבלה לעם 66 HD | קבלה לעם – שרת רשמי | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 189 | קבלה לעם – רוסית (ערוץ 66) | קבלה לעם – השרת של הערוץ | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 190 | ערוץ 16 ישראל | ערוץ 16 – שרת רשמי | ✅ רשמי | iptv-org (Channel16.il) | ✅ |

## 📺 חדשות מהעולם (News)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 297 | Sky News UK FHD | linear901-oo-hls0-prd-gtm.delivery.skycdp.com | ⚠️ ממסר צד שלישי | שידור רשמי Sky | ✅ |
| 298 | Sky News Arabia HD (סקיי ניוז ערבית) | live-stream.skynewsarabia.com | ⚠️ ממסר צד שלישי | שידור רשמי Sky | ✅ |
| 299 | Sky News Arabia ישיר HD (מקור 2) | stream.skynewsarabia.com | ⚠️ ממסר צד שלישי | שידור רשמי Sky | ✅ |
| 300 | Sky News Australia Extra 1 HD | skynewsau-live.akamaized.net | ⚠️ ממסר צד שלישי | שידור רשמי Sky | ✅ |
| 301 | Sky News Australia Extra 2 HD | skynewsau-live.akamaized.net | ⚠️ ממסר צד שלישי | שידור רשמי Sky | ✅ |
| 302 | Sky News Australia Extra 3 HD | skynewsau-live.akamaized.net | ⚠️ ממסר צד שלישי | שידור רשמי Sky | ✅ |

## 📺 ילדים מהעולם (Kids World)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 303 | Baby Shark TV (בייבי שארק לילדים) | Baby Shark TV – Wurl (Roku FAST) | ✅ FAST חינמי | סריקת ערוצי ילדים | ✅ |
| 304 | ערוץ בייבי BabyTV Global HD | BabyFirst – JW Player FAST | ✅ FAST חינמי | סריקת ערוצי ילדים | ✅ |
| 305 | ערוץ זו-מו לילדים (ZooMoo HD) | amagi.tv | ✅ FAST חינמי | סריקת ערוצי ילדים | ✅ |
| 306 | ערוץ אני אנימציה HD (Ani Kids) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | סריקת ערוצי ילדים | ❌ רק באפליקציה (HTTP) |
| 307 | ערוץ האנימציה PLUSPLUS HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | סריקת ערוצי ילדים | ❌ רק באפליקציה (HTTP) |
| 308 | ערוץ ניקי ג'וניור HD (Niki Junior) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | סריקת ערוצי ילדים | ❌ רק באפליקציה (HTTP) |
| 309 | ערוץ סרטוני אנימציה קצרים (Cartoons Short) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | סריקת ערוצי ילדים | ❌ רק באפליקציה (HTTP) |
| 310 | ערוץ סרטי אנימציה לילדים (Cartoons Cinema) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | סריקת ערוצי ילדים | ❌ רק באפליקציה (HTTP) |
| 311 | פרש קידס (Fresh Kids TV HD) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | סריקת ערוצי ילדים | ❌ רק באפליקציה (HTTP) |
| 312 | ניק ג'וניור (ברוסית) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (NickJr.ru) | ✅ דרך מקור חלופי: Cinerama.uz – ספק IPTV באוזבקיסטן |
| 313 | גולי גירל Gulli Girl (ברוסית) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (GulliGirl.ru) | ✅ דרך מקור חלופי: Cinerama.uz – ספק IPTV באוזבקיסטן |
| 314 | דאק טי וי פלוס ducktv PLUS HD | Antik Telecom (סלובקיה), נתיבי test | ⚠️ ממסר צד שלישי | iptv-org (ducktvPLUS.sk) | ✅ |
| 315 | ערוץ לגו Lego Channel HD | LEGO Channel – AWS CloudFront FAST | ✅ FAST חינמי | iptv-org (LegoChannel.us) | ✅ |
| 316 | ערוץ טיז'י TiJi לילדים HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (TiJi.ru) | ✅ דרך מקור חלופי: Cinerama.uz – ספק IPTV באוזבקיסטן |
| 317 | ג'ים ג'אם JimJam לילדים HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (JimJamEurope.uk) | ❌ רק באפליקציה (HTTP) |
| 318 | קלאסיקות מצוירות 90s HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 319 | קולנוע אגדות וילדים HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (KinoKazka.es) | ❌ רק באפליקציה (HTTP) |
| 320 | פיקסל TV ילדים HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (PixelTV.ua) | ✅ דרך מקור חלופי: live-tv.cloud – ספק IPTV אוקראיני |
| 321 | ניקי קידס Niki Kids HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (NikiKids.ua) | ❌ רק באפליקציה (HTTP) |
| 322 | קפטן פנטסטיקה HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (KapitanFantastika.ru) | ✅ דרך מקור חלופי: Cinerama.uz – ספק IPTV באוזבקיסטן |
| 323 | ערוץ O! ילדים וטבע | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (O.ru) | ❌ רק באפליקציה (HTTP) |
| 324 | דאק טי וי ducktv HD | Antik Telecom (סלובקיה), נתיבי test | ⚠️ ממסר צד שלישי | iptv-org (ducktv.sk) | ✅ |
| 325 | ערוץ השמש לילדים HD (Solntse Kids) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (Solnce.ru) | ✅ דרך מקור חלופי: ערוץ השמש (Солнце) – CDN |
| 326 | ערוץ מולט ילדים HD (Mult Kids) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (Mult.ru) | ✅ דרך מקור חלופי: Cinerama.uz – ספק IPTV באוזבקיסטן |
| 327 | מולט ומוזיקה לילדים HD (Mult & Music) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (Multimuzyka.ru) | ❌ רק באפליקציה (HTTP) |
| 328 | קרוסלה ערוץ הילדים HD (Carousel Kids) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (Carousel.ru) | ✅ דרך מקור חלופי: Smotrim (VGTRK) – הבעלים של קרוסלה |
| 329 | סי טי סי קידס HD (CTC Kids) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (STSkids.ru) | ❌ רק באפליקציה (HTTP) |
| 330 | ערוץ אגדות וסיפורים HD (Fairy Tales Kids) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (Vgostyakhuskazki.ru) | ✅ דרך מקור חלופי: Cinerama.uz – ספק IPTV באוזבקיסטן |

## 📺 מוזיקה (Music TV)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 331 | רדיוס 100FM – אולפן וידאו HD | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 332 | Sky Folk TV HD (סקיי פולק) | Sky Folk Live | ✅ רשמי | שידור רשמי Sky | ✅ |
| 333 | 4ever Music Hits HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (4everMusic.ua) | ❌ רק באפליקציה (HTTP) |
| 334 | 4Fun TV Pop Hits HD | 4FUN TV – שרת רשמי | ✅ רשמי | iptv-org (4FunTV.pl) | ✅ |
| 335 | ¡Viva Latino! Dance HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (VivaLatino.ua) | ❌ רק באפליקציה (HTTP) |
| 336 | 1HD Music Television | שרת IP ללא שם (1HD) | ⚠️ ממסר צד שלישי | iptv-org (1HDMusicTelevision.ru) | ❌ רק באפליקציה (HTTP) |

## 📺 ערוצים בינלאומיים (World)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 337 | DW News English HD | DW – Akamai (רשמי) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 338 | BFM TV HD (France) | BFM – SFR (רשמי) | ✅ רשמי | iptv-org (BFMTV.fr) | ✅ |
| 339 | BFM Tech & Co HD | BFM – SFR (רשמי) | ✅ רשמי | iptv-org (BFMTechCo.fr) | ✅ |

## 📻 רדיו

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 85 | רדיוס 100% שירי דיסני | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 86 | רדיוס 100% ילדים | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 87 | רדיוס 100% ערוץ הכוכבים | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 185 | ברסלב: הלכות ודף יומי | רדיו ברסלב – שרת התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 186 | ברסלב: ספרי ברסלב | רדיו ברסלב – שרת התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 187 | ברסלב: קצר וקולע | רדיו ברסלב – שרת התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 188 | ברסלב: תורה וזימרה | רדיו ברסלב – שרת התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 191 | כאן ב - רשת ב | Triton StreamTheWorld – מפיץ הרדיו של כאן | ✅ רשמי | radio-browser.info + בדיקת שמות הזרמים של כאן | ✅ |
| 192 | כאן גימל | Triton StreamTheWorld – מפיץ הרדיו של כאן | ✅ רשמי | radio-browser.info + בדיקת שמות הזרמים של כאן | ✅ |
| 193 | כאן 88 | Triton StreamTheWorld – מפיץ הרדיו של כאן | ✅ רשמי | radio-browser.info + בדיקת שמות הזרמים של כאן | ✅ |
| 194 | כאן תרבות | Triton StreamTheWorld – מפיץ הרדיו של כאן | ✅ רשמי | radio-browser.info + בדיקת שמות הזרמים של כאן | ✅ |
| 195 | כאן מורשת | Triton StreamTheWorld – מפיץ הרדיו של כאן | ✅ רשמי | radio-browser.info + בדיקת שמות הזרמים של כאן | ✅ |
| 196 | כאן רקע | Triton StreamTheWorld – מפיץ הרדיו של כאן | ✅ רשמי | radio-browser.info + בדיקת שמות הזרמים של כאן | ✅ |
| 197 | כאן קול המוזיקה | Triton StreamTheWorld – מפיץ הרדיו של כאן | ✅ רשמי | radio-browser.info + בדיקת שמות הזרמים של כאן | ✅ |
| 198 | גלגלצ | גלי צה"ל / גלגלצ – CDN רשמי (Bynet) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 199 | גלי צה"ל | גלי צה"ל / גלגלצ – CDN רשמי (Bynet) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 200 | גלגלצ להיטים | גלי צה"ל / גלגלצ – CDN רשמי (Bynet) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 201 | eco99fm | eco99fm – Mediacast (רשמי) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 202 | 103FM | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 203 | רדיוס 100FM | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 204 | לב המדינה 91FM | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 205 | 102FM רדיו תל אביב | 102FM – LiveCDN (רשמי) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 206 | רדיו חיפה 107.5FM | רדיו חיפה – LiveCDN (רשמי) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 207 | 101FM רדיו ירושלים | StreamGates – ה-CDN של התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 208 | 90FM אמצע הדרך | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 209 | רדיו דרום 101.5FM | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 210 | רדיו דרום 97FM | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 211 | 104.5FM רדיו צפון | StreamGates – ה-CDN של התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 212 | קול רגע 96FM | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 213 | 102FM קול הים האדום | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 214 | גלי ישראל 94FM | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 215 | קול חי 93FM | קול חי – שרת התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 216 | קול חי מיוזיק | קול חי – שרת התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 217 | קול ברמה | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 218 | קול ברמה מוזיקה (קול פליי) | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 219 | רדיו 2000 | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 220 | רדיו ברסלב | רדיו ברסלב – שרת התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 221 | מזרחית FM | rs.mizrahit.fm | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 222 | נושמים מזרחית | www.rs.mizrahit.fm | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 223 | רדיו עברי 6 (מוזיקה ישראלית) | streaming.radio.co | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 224 | רדיו הקצה KZ | רדיו הקצה – Mediacast (רשמי) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 225 | רדיו ספורט 5 | ספורט 5 – השידור הרשמי של RGE (Akamai) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 226 | 99.5FM רדיו | 99.5FM – LiveCDN (רשמי) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 227 | AvivMedia טראנס | 176.9.7.145 | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 228 | Israeli Voice Radio | icecast.live | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 229 | Radio France Israël | stream10.vestaradio.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 230 | TIMBR רדיו | radio.betteradio.live | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 231 | Up2Dance רדיו | icecast.live | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 232 | yStream | ystream.live | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 233 | א-שמס A-Shams | StreamGates – ה-CDN של התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 234 | ג'וינט רדיו בלוז רוק | jointil.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 235 | ג'וינט רדיו טראנס | jointil.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 236 | ג'וינט רדיו רגאיי | jointil.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 237 | גלגלצ עשורים | גלי צה"ל / גלגלצ – CDN רשמי (Bynet) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 238 | גלגלצ רגוע | גלי צה"ל / גלגלצ – CDN רשמי (Bynet) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 239 | גלגלצ רוק | גלי צה"ל / גלגלצ – CDN רשמי (Bynet) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 240 | הרדיו הטוב 106.4FM (רוסית) | StreamGates – ה-CDN של התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 241 | הרדיו הראשון 89.1FM (רוסית) | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 242 | סטאר 33 רדיו | online.star33radio.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 243 | קבלה לעם רדיו | icecast.kab.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 244 | קבלה לעם רדיו – אנגלית | icecast.kab.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 245 | קבלה לעם רדיו – ספרדית | icecast.kab.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 246 | קבלה לעם רדיו – צרפתית | icecast.kab.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 247 | קבלה לעם רדיו – רוסית | icecast.kab.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 248 | קול הצפון | eCast – שרת התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 249 | רדיו CS קלאסי | n0a.radiojar.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 250 | רדיו אהבה – רומנטי | liveradio.co.il | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 251 | רדיו אלי ניסמן | listen6.myradio24.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 252 | רדיו התחנה 101.5 | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 253 | רדיו נאס Radio Nas | StreamGates – ה-CDN של התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 254 | רדיו נהריה | רדיו נהריה – שרת התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 255 | רדיו סהר | eCast – שרת התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 256 | רדיו שירי דיכאון | רדיו שירי דיכאון – השרת של התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 257 | רדיוס 100% K-Pop | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 258 | רדיוס 100% אגדות | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 259 | רדיוס 100% אהבה | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 260 | רדיוס 100% אולדיז | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 261 | רדיוס 100% אימון | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 262 | רדיוס 100% אירוויזיון | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 263 | רדיוס 100% בלוז | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 264 | רדיוס 100% בריטפופ | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 265 | רדיוס 100% ג'אז | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 266 | רדיוס 100% גיימר | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 267 | רדיוס 100% טופ 40 | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 268 | רדיוס 100% טיקטוק | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 269 | רדיוס 100% יהודי | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 270 | רדיוס 100% להיטים | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 271 | רדיוס 100% לטינו | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 272 | רדיוס 100% מוזיקת עולם | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 273 | רדיוס 100% מזרחית | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 274 | רדיוס 100% סטנדאפ | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 275 | רדיוס 100% סלפי | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 276 | רדיוס 100% סרטים | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 277 | רדיוס 100% צ'ילאאוט | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 278 | רדיוס 100% קאברים | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 279 | רדיוס 100% קאנטרי | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 280 | רדיוס 100% קלאב | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 281 | רדיוס 100% קלאסית | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 282 | רדיוס 100% רגוע | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 283 | רדיוס 100% רוק | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 284 | רדיוס 100% רוק קלאסי | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 285 | רדיוס 100% רטרו | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 286 | רדיוס 100% שנות ה-80 | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 287 | רדיוס 100% שנות ה-90 | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 288 | רדיוס נוסטלגי 96.3 | StreamGates – ה-CDN של התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 289 | רק מוזיקה (Rak Musica) | play.radioking.io | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 290 | AudioVersity רדיו אוניברסיטת רייכמן | runi.ac.il | ✅ רשמי | סריקת ערוצים מאומתת | ✅ |
| 291 | Radio Angelika תל אביב | radioangelika.com | ✅ רשמי | סריקת ערוצים מאומתת | ✅ |
| 292 | Душа Кавказа – רדיו קהילת קווקז בישראל | www.kavkazportal.com | ⚠️ ממסר צד שלישי | סריקת ערוצים מאומתת | ✅ |
| 293 | רדיו אורנו (Орэну) | orenu.co.il | ✅ רשמי | סריקת ערוצים מאומתת | ✅ |
| 294 | רדיו קווליטה (Radio Qualita) | RadioKing – נגן השידור של Studio Qualita | ✅ רשמי | זרם Radio Qualita; זהות התחנה אומתה באתר qualita.org.il | ✅ |
| 295 | Radio Plus ישראל | YesStreaming – זרם הנגן באתר Radio Plus | ✅ רשמי | נגן Radio Plus באתר radioplus.co.il | ✅ |
| 296 | Jewish Music Stream (מוזיקה יהודית) | stream.jewishmusicstream.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |

## 📲 מנויים ואפליקציות

| # | שירות | אפליקציה בטלפון | אפליקציה ב-Android TV | אתר |
|---|---|---|---|---|

## רכיבים וספריות

| רכיב | מקור | רישיון |
|---|---|---|
| hls.js (`assets/hls.min.js`) | [video-dev/hls.js](https://github.com/video-dev/hls.js) | Apache-2.0 |
| גופן Heebo | Google Fonts | OFL |
| Electron (אפליקציות Linux / macOS) | [electron/electron](https://github.com/electron/electron) – קבצי ה-release הרשמיים, נבדקו מול SHASUMS256 | MIT |
| Microsoft WebView2 (אפליקציית Windows) | חבילת NuGet `Microsoft.Web.WebView2` | רישיון Microsoft (מותר להפצה) |
| Whisper בדפדפן (דיבוב מקומי) | transformers.js דרך jsDelivr | Apache-2.0 / MIT |
| תרגום וקול לדיבוב | Google Gemini / OpenAI (מפתח של המשתמש), Google Translate | לפי תנאי השירות |
| לוגואים של ערוצים | iptv-org / ויקימדיה; לוגואי הרדיו והאפליקציות נוצרו ב-`tools/add_israeli.py` | הלוגואים הם סימני מסחר של בעליהם |

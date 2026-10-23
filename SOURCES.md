# מקורות – מאיפה מגיע כל ערוץ

נוצר אוטומטית מ-`channels.json` על ידי `tools/make_sources.py`. סה"כ **154** פריטים: 96 ערוצי טלוויזיה, 47 תחנות רדיו, 11 אפליקציות/שירותים.

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

- **69 שידורים רשמיים** – מהשרתים של הגוף המשדר עצמו או של המפיץ שלו (כאן, 13, 14, i24NEWS, גלי צה"ל, תחנות הרדיו, DW, אל-ג'זירה...).
- **9 ערוצי FAST** – ערוצים חינמיים עם פרסומות שהבעלים מפיצים בעצמם (Pluto TV, BabyFirst, LEGO, Baby Shark...).
- **65 ממסרי צד שלישי** – בעיקר `stream.mcquack.net` (60 ערוצים). אלה **אינם** השרתים של הערוצים: אתר חיצוני קולט את השידור ומפיץ אותו מחדש. בהם נמצאים גם ערוצים בתשלום (HOT, yes, viju/TV1000, ערוצי ספורט) – סביר מאוד שההפצה הזו **ללא רישיון** מבעלי הזכויות, והם עלולים להיעלם בכל רגע. ההחלטה אם להשאיר אותם היא שלך; הסקריפטים לא מוסיפים ממסרים חדשים של ערוצים בתשלום.
- **שירותי המנויים** (פרטנר, סלקום, STING, yes, HOT) – אין בפרויקט שום שידור שלהם; הכרטיס רק פותח את האפליקציה/האתר הרשמי, וצפייה דורשת מנוי.

> הסיווג רשמי / FAST / ממסר נקבע לפי שם השרת שממנו מגיע השידור (למשל CDN של הגוף המשדר), ולא אושר מול כל גוף משדר בנפרד.

## לפי שרת

| שרת | מי זה | סוג | מספר |
|---|---|---|---|
| `stream.mcquack.net` | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | 60 |
| `cdn.cybercdn.live` | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | 14 |
| `playerservices.streamtheworld.com` | Triton StreamTheWorld – מפיץ הרדיו של כאן | ✅ רשמי | 7 |
| `gb25.streamgates.net` | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | 7 |
| `glzwizzlv.bynetcdn.com` | גלי צה"ל / גלגלצ – CDN רשמי (Bynet) | ✅ רשמי | 6 |
| `jmp2.uk` | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | 3 |
| `cdna.streamgates.net` | StreamGates – ה-CDN של התחנה | ✅ רשמי | 3 |
| `kancdn.medonecdn.net` | כאן – תאגיד השידור (CDN רשמי) | ✅ רשמי | 2 |
| `88.212.15.17` | שרת של Antik (סלובקיה), נתיבי test_ | ⚠️ ממסר צד שלישי | 2 |
| `dash3.antik.sk` | Antik Telecom (סלובקיה), נתיבי test | ⚠️ ממסר צד שלישי | 2 |
| `ncdn-live-bfm.pfd.sfr.net` | BFM – SFR (רשמי) | ✅ רשמי | 2 |
| `radio.streamgates.net` | StreamGates – ה-CDN של התחנה | ✅ רשמי | 2 |
| `media2.93fm.co.il` | קול חי – שרת התחנה | ✅ רשמי | 2 |
| `d18b0e6mopany4.cloudfront.net` | רשת 13 – AWS CloudFront | ✅ רשמי | 1 |
| `r.il.cdn-redge.media` | ערוץ 14 – CDN רשמי (Redge) | ✅ רשמי | 1 |
| `i24newshebrew-cdn.encoders.immergo.tv` | i24NEWS – שרת רשמי | ✅ רשמי | 1 |
| `contact.gostreaming.tv` | GoStreaming – ספק השידור של ערוץ 9 | ✅ רשמי | 1 |
| `i24newsfrench-cdn.encoders.immergo.tv` | i24NEWS – שרת רשמי | ✅ רשמי | 1 |
| `i24newsarabic-cdn.encoders.immergo.tv` | i24NEWS – שרת רשמי | ✅ רשמי | 1 |
| `i24newsenglish-cdn.encoders.immergo.tv` | i24NEWS – שרת רשמי | ✅ רשמי | 1 |
| `d2dffl3588mvfk.cloudfront.net` | ערוץ FAST ישראלי – AWS CloudFront | ✅ FAST חינמי | 1 |
| `d15ds134q59udk.cloudfront.net` | ערוץ FAST ישראלי – AWS CloudFront | ✅ FAST חינמי | 1 |
| `d1yd8hohnldm33.cloudfront.net` | ערוץ FAST ישראלי – AWS CloudFront | ✅ FAST חינמי | 1 |
| `rbmn-live.akamaized.net` | Red Bull TV – Akamai (רשמי) | ✅ רשמי | 1 |
| `cdn.fast.jwp.services` | BabyFirst – JW Player FAST | ✅ FAST חינמי | 1 |
| `dltiqboxjw21d.cloudfront.net` | LEGO Channel – AWS CloudFront FAST | ✅ FAST חינמי | 1 |
| `newidco-babysharktv-1-us.roku.wurl.tv` | Baby Shark TV – Wurl (Roku FAST) | ✅ FAST חינמי | 1 |
| `stream.4fun.tv` | 4FUN TV – שרת רשמי | ✅ רשמי | 1 |
| `176.118.197.101` | שרת IP ללא שם (1HD) | ⚠️ ממסר צד שלישי | 1 |
| `585b674743bbb.streamlock.net` | 70-80 TV – Wowza Streamlock | ✅ רשמי | 1 |
| `svs.itworkscdn.net` | Asharq Discovery – ItWorks (רשמי) | ✅ רשמי | 1 |
| `stream.panet.com` | פאנט – הלא TV (רשמי) | ✅ רשמי | 1 |
| `edge3.uk.kab.tv` | קבלה לעם – שרת רשמי | ✅ רשמי | 1 |
| `1247634592.rsc.cdn77.org` | שלנו TV – CDN77 | ✅ רשמי | 1 |
| `ch16israel-cdn.encoders.immergo.tv` | ערוץ 16 – שרת רשמי | ✅ רשמי | 1 |
| `dwamdstream102.akamaized.net` | DW – Akamai (רשמי) | ✅ רשמי | 1 |
| `live-hls-apps-aje-fa.getaj.net` | אל-ג'זירה – שרת רשמי | ✅ רשמי | 1 |
| `eco-live.mediacast.co.il` | eco99fm – Mediacast (רשמי) | ✅ רשמי | 1 |
| `102.livecdn.biz` | 102FM – LiveCDN (רשמי) | ✅ רשמי | 1 |
| `kzradio.mediacast.co.il` | רדיו הקצה – Mediacast (רשמי) | ✅ רשמי | 1 |
| `1075.livecdn.biz` | רדיו חיפה – LiveCDN (רשמי) | ✅ רשמי | 1 |
| `live.ecast.co.il` | eCast – שרת התחנה | ✅ רשמי | 1 |
| `cast.breslevforyou.co.il` | רדיו ברסלב – שרת התחנה | ✅ רשמי | 1 |
| `cast.radionahariya.com` | רדיו נהריה – שרת התחנה | ✅ רשמי | 1 |

## 📺 ערוצי ישראל - שידור חי

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 1 | קשת 12 HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Keshet12.il`) | ❌ רק באפליקציה (HTTP) |
| 2 | רשת 13 HD | רשת 13 – AWS CloudFront | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 3 | כאן 11 HD | כאן – תאגיד השידור (CDN רשמי) | ✅ רשמי | iptv-org (`Kan11.il`) | ✅ |
| 4 | עכשיו 14 HD | ערוץ 14 – CDN רשמי (Redge) | ✅ רשמי | iptv-org (`Now14.il`) | ✅ |
| 5 | i24NEWS עברית HD | i24NEWS – שרת רשמי | ✅ רשמי | iptv-org (`i24NEWSHebrew.il`) | ✅ |
| 6 | ערוץ 9 ישראל HD | GoStreaming – ספק השידור של ערוץ 9 | ✅ רשמי | iptv-org (`Channel9.il`) | ✅ |
| 7 | ערוץ 24 HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Channel24.il`) | ❌ רק באפליקציה (HTTP) |
| 8 | מכאן 33 HD | כאן – תאגיד השידור (CDN רשמי) | ✅ רשמי | iptv-org (`Makan33.il`) | ✅ |
| 9 | ערוץ הכנסת 99 | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`KnessetChannel.il`) | ❌ רק באפליקציה (HTTP) |
| 10 | i24NEWS צרפתית HD | i24NEWS – שרת רשמי | ✅ רשמי | iptv-org (`i24NEWSFrench.il`) | ✅ |
| 11 | i24NEWS ערבית HD | i24NEWS – שרת רשמי | ✅ רשמי | iptv-org (`i24NEWSArabic.il`) | ✅ |
| 12 | i24NEWS English | i24NEWS – שרת רשמי | ✅ רשמי | iptv-org | ✅ |

## 📺 ילדים ונוער (בעברית)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 13 | כאן חינוכית 23 HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`KanEducational.il`) | ❌ רק באפליקציה (HTTP) |
| 14 | ערוץ הופ! ילדים בעברית HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Hop.il`) | ❌ רק באפליקציה (HTTP) |

## 📺 סרטים וסדרות (Movies & Series)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 73 | ערוץ הים התיכון HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`YamTichoni.il`) | ❌ רק באפליקציה (HTTP) |
| 74 | yes סרטים אקשן HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`YesMoviesAction.il`) | ❌ רק באפליקציה (HTTP) |
| 75 | HOT דרמה HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`HOTDrama.il`) | ❌ רק באפליקציה (HTTP) |
| 76 | HOT זון HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`HOTZone.il`) | ❌ רק באפליקציה (HTTP) |
| 77 | yes ריאליטי HD | ערוץ FAST ישראלי – AWS CloudFront | ✅ FAST חינמי | iptv-org (`RealityChannel.il`) | ✅ |
| 78 | ערוץ הקומדיה HD | ערוץ FAST ישראלי – AWS CloudFront | ✅ FAST חינמי | iptv-org (`ComedyChannel.il`) | ✅ |
| 79 | ויוה פרימיום HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`VivaPremium.il`) | ❌ רק באפליקציה (HTTP) |
| 80 | סינמה פרימיום HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Premialnoe.ru`) | ❌ רק באפליקציה (HTTP) |
| 81 | סינמה מתח ואקשן HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Ostrosyuzhetnoye.ru`) | ❌ רק באפליקציה (HTTP) |
| 82 | ערוץ סרטי אימה HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Kinouzhas.ru`) | ❌ רק באפליקציה (HTTP) |
| 83 | קולנוע להיטים KinoHit HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`KinoHit.ru`) | ✅ דרך מקור חלופי: Cinerama.uz – ספק IPTV באוזבקיסטן |
| 84 | קולנוע משפחתי Kinosemja HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Kinosemja.ru`) | ❌ רק באפליקציה (HTTP) |
| 85 | סינמה מגה-להיטים HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`vijuPlusMegahit.ru`) | ❌ רק באפליקציה (HTTP) |
| 86 | סינמה בכורה Premiere HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`vijuPlusPremiere.ru`) | ❌ רק באפליקציה (HTTP) |
| 87 | סינמה קומדיה HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`vijuPlusComedy.ru`) | ❌ רק באפליקציה (HTTP) |
| 88 | קולנוע TV1000 אקשן HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`vijuTV1000action.ru`) | ✅ דרך מקור חלופי: Cinerama.uz – ספק IPTV באוזבקיסטן |
| 89 | קולנוע TV1000 קלאסי | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`vijuTV1000.ru`) | ❌ רק באפליקציה (HTTP) |
| 90 | ערוץ הסדרות Serial HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`vijuPlusSerial.ru`) | ❌ רק באפליקציה (HTTP) |
| 91 | ערוץ החופשות והנופש HD | ערוץ FAST ישראלי – AWS CloudFront | ✅ FAST חינמי | iptv-org (`VacationChannel.il`) | ✅ |
| 92 | סרטי שנות ה-80 Rewind HD | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`80sRewind.us`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 93 | סרטי שנות ה-70 Cinema HD | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`70sCinema.us`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 94 | סרטי שנות ה-2000 Replay HD | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`00sReplay.us`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 95 | משפחת סימפסון 24/7 HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 96 | קולנוע קומדיה CineMan Comedy HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 97 | קולנוע רטרו VHS Cinema HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |

## 📺 ספורט (Sports)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 98 | Sport 5 אוקראינה | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Sport5.ua`) | ❌ רק באפליקציה (HTTP) |
| 99 | Sport 2 אוקראינה | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Sport2.ua`) | ❌ רק באפליקציה (HTTP) |
| 100 | Sport 3 אוקראינה | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Sport3.ua`) | ❌ רק באפליקציה (HTTP) |
| 101 | Sport 4 אוקראינה | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Sport4.ua`) | ❌ רק באפליקציה (HTTP) |
| 102 | ספורט פלוס HD (viju+ Sport) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`vijuPlusSport.ru`) | ✅ דרך מקור חלופי: Cinerama.uz – ספק IPTV באוזבקיסטן |
| 103 | ספורט סטאנטה Setanta Sports 2 HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`SetantaSports2Eurasia.ie`) | ❌ רק באפליקציה (HTTP) |
| 104 | Red Bull TV HD | Red Bull TV – Akamai (רשמי) | ✅ רשמי | iptv-org (`RedBullTV.at`) | ✅ |
| 105 | Arena4 Sports Live | שרת של Antik (סלובקיה), נתיבי test_ | ⚠️ ממסר צד שלישי | iptv-org (`Arena4.hu`) | ❌ רק באפליקציה (HTTP) |
| 106 | ערוץ אקסטרים Extreme Sports HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |

## 📺 ילדים מהעולם (Kids World)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 107 | ניקלודיאון (ברוסית) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Nickelodeon.ru`) | ❌ רק באפליקציה (HTTP) |
| 108 | ניק ג'וניור (ברוסית) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`NickJr.ru`) | ✅ דרך מקור חלופי: Cinerama.uz – ספק IPTV באוזבקיסטן |
| 109 | גולי גירל Gulli Girl (ברוסית) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`GulliGirl.ru`) | ✅ דרך מקור חלופי: Cinerama.uz – ספק IPTV באוזבקיסטן |
| 110 | דאק טי וי פלוס ducktv PLUS HD | Antik Telecom (סלובקיה), נתיבי test | ⚠️ ממסר צד שלישי | iptv-org (`ducktvPLUS.sk`) | ✅ |
| 111 | בייבי פירסט BabyFirst HD | BabyFirst – JW Player FAST | ✅ FAST חינמי | iptv-org (`BabyFirst.us`) | ✅ |
| 112 | אנימציה קצרצרה Cartoons Short HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 113 | ערוץ לגו Lego Channel HD | LEGO Channel – AWS CloudFront FAST | ✅ FAST חינמי | iptv-org (`LegoChannel.us`) | ✅ |
| 114 | ערוץ טיז'י TiJi לילדים HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`TiJi.ru`) | ✅ דרך מקור חלופי: Cinerama.uz – ספק IPTV באוזבקיסטן |
| 115 | ג'ים ג'אם JimJam לילדים HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`JimJamEurope.uk`) | ❌ רק באפליקציה (HTTP) |
| 116 | אנימציה וילדים Fresh Kids HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 117 | קלאסיקות מצוירות 90s HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 118 | סרטי אנימציה Cartoons Big HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 119 | קולנוע אגדות וילדים HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`KinoKazka.es`) | ❌ רק באפליקציה (HTTP) |
| 120 | פיקסל TV ילדים HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`PixelTV.ua`) | ✅ דרך מקור חלופי: live-tv.cloud – ספק IPTV אוקראיני |
| 121 | ניקי קידס Niki Kids HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`NikiKids.ua`) | ❌ רק באפליקציה (HTTP) |
| 122 | קפטן פנטסטיקה HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`KapitanFantastika.ru`) | ✅ דרך מקור חלופי: Cinerama.uz – ספק IPTV באוזבקיסטן |
| 123 | ערוץ O! ילדים וטבע | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`O.ru`) | ❌ רק באפליקציה (HTTP) |
| 124 | דאק טי וי ducktv HD | Antik Telecom (סלובקיה), נתיבי test | ⚠️ ממסר צד שלישי | iptv-org (`ducktv.sk`) | ✅ |
| 125 | בייבי שארק Baby Shark TV HD | Baby Shark TV – Wurl (Roku FAST) | ✅ FAST חינמי | iptv-org (`BabySharkTV.us`) | ✅ |
| 126 | ערוץ השמש לילדים HD (Solntse Kids) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Solnce.ru`) | ✅ דרך מקור חלופי: ערוץ השמש (Солнце) – CDN |
| 127 | ערוץ מולט ילדים HD (Mult Kids) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Mult.ru`) | ✅ דרך מקור חלופי: Cinerama.uz – ספק IPTV באוזבקיסטן |
| 128 | מולט ומוזיקה לילדים HD (Mult & Music) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Multimuzyka.ru`) | ❌ רק באפליקציה (HTTP) |
| 129 | קרוסלה ערוץ הילדים HD (Carousel Kids) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Carousel.ru`) | ✅ דרך מקור חלופי: Smotrim (VGTRK) – הבעלים של קרוסלה |
| 130 | סי טי סי קידס HD (CTC Kids) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`STSkids.ru`) | ❌ רק באפליקציה (HTTP) |
| 131 | ערוץ אגדות וסיפורים HD (Fairy Tales Kids) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Vgostyakhuskazki.ru`) | ✅ דרך מקור חלופי: Cinerama.uz – ספק IPTV באוזבקיסטן |

## 📺 מוזיקה (Music TV)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 132 | 4ever Music Hits HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`4everMusic.ua`) | ❌ רק באפליקציה (HTTP) |
| 133 | 4Fun TV Pop Hits HD | 4FUN TV – שרת רשמי | ✅ רשמי | iptv-org (`4FunTV.pl`) | ✅ |
| 134 | ¡Viva Latino! Dance HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`VivaLatino.ua`) | ❌ רק באפליקציה (HTTP) |
| 135 | 1HD Music Television | שרת IP ללא שם (1HD) | ⚠️ ממסר צד שלישי | iptv-org (`1HDMusicTelevision.ru`) | ❌ רק באפליקציה (HTTP) |
| 136 | 70-80 TV Retro Hits | 70-80 TV – Wowza Streamlock | ✅ רשמי | iptv-org (`7080TV.it`) | ✅ |

## 📺 טבע ודוקו (Documentary & Nature)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 137 | Animal Planet World HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`AnimalPlanet.ru`) | ❌ רק באפליקציה (HTTP) |
| 138 | Discovery חקירות (ID) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`InvestigationDiscovery.ru`) | ❌ רק באפליקציה (HTTP) |
| 139 | ערוץ הטיולים Travel Channel | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`TravelChannel.ru`) | ❌ רק באפליקציה (HTTP) |
| 140 | ערוץ החקר והמדע (viju Explore) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`vijuExplore.ru`) | ❌ רק באפליקציה (HTTP) |
| 141 | עולם וטבע HD (viju+ Planet) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`vijuPlusPlanet.ru`) | ❌ רק באפליקציה (HTTP) |
| 142 | BBC Earth Live HD | שרת של Antik (סלובקיה), נתיבי test_ | ⚠️ ממסר צד שלישי | iptv-org (`BBCEarth.uk`) | ✅ דרך מקור חלופי: BBC Earth – Amagi / Xumo FAST |
| 143 | Asharq Discovery HD | Asharq Discovery – ItWorks (רשמי) | ✅ רשמי | iptv-org (`AsharqDiscovery.sa`) | ✅ |
| 144 | 365 ימי היסטוריה (History TV) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`365daysTV.ru`) | ✅ דרך מקור חלופי: Cinerama.uz – ספק IPTV באוזבקיסטן |

## 📺 לייף סטייל ובידור

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 145 | ערוץ החיים הטובים HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`GoodLife.il`) | ❌ רק באפליקציה (HTTP) |

## 📺 מסורת וקהילה

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 146 | ערוץ הידברות HD | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | iptv-org (`Hidabroot.il`) | ✅ |
| 147 | הלא TV HD (Hala TV) | פאנט – הלא TV (רשמי) | ✅ רשמי | iptv-org (`HalaTV.il`) | ✅ |
| 148 | ערוץ קבלה לעם | קבלה לעם – שרת רשמי | ✅ רשמי | iptv-org (`KabbalahforthePeopleIsrael.il`) | ✅ |
| 149 | שלנו TV HD (Shelanu TV) | שלנו TV – CDN77 | ✅ רשמי | iptv-org (`ShelanuTV.il`) | ✅ |
| 150 | ערוץ 16 ישראל | ערוץ 16 – שרת רשמי | ✅ רשמי | iptv-org (`Channel16.il`) | ✅ |

## 📺 ערוצים בינלאומיים (World)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 151 | DW News English HD | DW – Akamai (רשמי) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 152 | Al Jazeera English HD | אל-ג'זירה – שרת רשמי | ✅ רשמי | iptv-org (`AlJazeera.qa`) | ✅ |
| 153 | BFM TV HD (France) | BFM – SFR (רשמי) | ✅ רשמי | iptv-org (`BFMTV.fr`) | ✅ |
| 154 | BFM Tech & Co HD | BFM – SFR (רשמי) | ✅ רשמי | iptv-org (`BFMTechCo.fr`) | ✅ |

## 📻 רדיו

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 15 | כאן ב - רשת ב | Triton StreamTheWorld – מפיץ הרדיו של כאן | ✅ רשמי | radio-browser.info + בדיקת שמות הזרמים של כאן | ✅ |
| 16 | כאן גימל | Triton StreamTheWorld – מפיץ הרדיו של כאן | ✅ רשמי | radio-browser.info + בדיקת שמות הזרמים של כאן | ✅ |
| 17 | כאן 88 | Triton StreamTheWorld – מפיץ הרדיו של כאן | ✅ רשמי | radio-browser.info + בדיקת שמות הזרמים של כאן | ✅ |
| 18 | כאן תרבות | Triton StreamTheWorld – מפיץ הרדיו של כאן | ✅ רשמי | radio-browser.info + בדיקת שמות הזרמים של כאן | ✅ |
| 19 | כאן מורשת | Triton StreamTheWorld – מפיץ הרדיו של כאן | ✅ רשמי | radio-browser.info + בדיקת שמות הזרמים של כאן | ✅ |
| 20 | כאן רקע | Triton StreamTheWorld – מפיץ הרדיו של כאן | ✅ רשמי | radio-browser.info + בדיקת שמות הזרמים של כאן | ✅ |
| 21 | כאן קול המוזיקה | Triton StreamTheWorld – מפיץ הרדיו של כאן | ✅ רשמי | radio-browser.info + בדיקת שמות הזרמים של כאן | ✅ |
| 22 | גלגלצ | גלי צה"ל / גלגלצ – CDN רשמי (Bynet) | ✅ רשמי | radio-browser.info | ✅ |
| 23 | גלי צה"ל | גלי צה"ל / גלגלצ – CDN רשמי (Bynet) | ✅ רשמי | radio-browser.info | ✅ |
| 24 | גלגלצ להיטים | גלי צה"ל / גלגלצ – CDN רשמי (Bynet) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 25 | eco99fm | eco99fm – Mediacast (רשמי) | ✅ רשמי | radio-browser.info | ✅ |
| 26 | 103FM | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | radio-browser.info | ✅ |
| 27 | רדיוס 100FM | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | radio-browser.info | ✅ |
| 28 | 102FM רדיו תל אביב | 102FM – LiveCDN (רשמי) | ✅ רשמי | radio-browser.info | ✅ |
| 29 | רדיו הקצה KZ | רדיו הקצה – Mediacast (רשמי) | ✅ רשמי | radio-browser.info | ✅ |
| 30 | קול ברמה | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 31 | רדיו דרום 101.5FM | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | radio-browser.info | ✅ |
| 32 | רדיו 2000 | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 33 | קול ברמה מוזיקה (קול פליי) | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | radio-browser.info | ✅ |
| 34 | 104.5FM רדיו צפון | StreamGates – ה-CDN של התחנה | ✅ רשמי | radio-browser.info | ✅ |
| 35 | 101FM רדיו ירושלים | StreamGates – ה-CDN של התחנה | ✅ רשמי | radio-browser.info | ✅ |
| 36 | רדיו חיפה 107.5FM | רדיו חיפה – LiveCDN (רשמי) | ✅ רשמי | radio-browser.info | ✅ |
| 37 | 90FM אמצע הדרך | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | radio-browser.info | ✅ |
| 38 | רדיו דרום 97FM | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | radio-browser.info | ✅ |
| 39 | רדיו התחנה 101.5 | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | radio-browser.info | ✅ |
| 40 | גלי ישראל 94FM | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | radio-browser.info | ✅ |
| 41 | קול חי 93FM | קול חי – שרת התחנה | ✅ רשמי | radio-browser.info | ✅ |
| 42 | קול חי מיוזיק | קול חי – שרת התחנה | ✅ רשמי | radio-browser.info | ✅ |
| 43 | קול רגע 96FM | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | radio-browser.info | ✅ |
| 44 | לב המדינה 91FM | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | radio-browser.info | ✅ |
| 45 | רדיוס נוסטלגי 96.3 | StreamGates – ה-CDN של התחנה | ✅ רשמי | radio-browser.info | ✅ |
| 46 | קול הצפון | eCast – שרת התחנה | ✅ רשמי | radio-browser.info | ✅ |
| 47 | גלגלצ רוק | גלי צה"ל / גלגלצ – CDN רשמי (Bynet) | ✅ רשמי | radio-browser.info | ✅ |
| 48 | גלגלצ עשורים | גלי צה"ל / גלגלצ – CDN רשמי (Bynet) | ✅ רשמי | radio-browser.info | ✅ |
| 49 | גלגלצ רגוע | גלי צה"ל / גלגלצ – CDN רשמי (Bynet) | ✅ רשמי | radio-browser.info | ✅ |
| 50 | רדיוס 100% מזרחית | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | radio-browser.info | ✅ |
| 51 | רדיוס 100% יהודי | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | radio-browser.info | ✅ |
| 52 | רדיוס 100% להיטים | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | radio-browser.info | ✅ |
| 53 | רדיוס 100% שנות ה-80 | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | radio-browser.info | ✅ |
| 54 | רדיוס 100% שנות ה-90 | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | radio-browser.info | ✅ |
| 55 | רדיוס 100% ילדים | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | radio-browser.info | ✅ |
| 56 | רדיוס 100% רגוע | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | radio-browser.info | ✅ |
| 57 | א-שמס A-Shams | StreamGates – ה-CDN של התחנה | ✅ רשמי | radio-browser.info | ✅ |
| 58 | רדיו נאס Radio Nas | StreamGates – ה-CDN של התחנה | ✅ רשמי | radio-browser.info | ✅ |
| 59 | הרדיו הראשון 89.1FM (רוסית) | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | radio-browser.info | ✅ |
| 60 | רדיו ברסלב | רדיו ברסלב – שרת התחנה | ✅ רשמי | radio-browser.info | ✅ |
| 61 | רדיו נהריה | רדיו נהריה – שרת התחנה | ✅ רשמי | radio-browser.info | ✅ |

## 📲 מנויים ואפליקציות

| # | שירות | אפליקציה בטלפון | אפליקציה ב-Android TV | אתר |
|---|---|---|---|---|
| 62 | Partner tv+ | `il.co.partnertv` | `il.co.partnertv.atv` | https://www.partner.co.il/tv/ |
| 63 | סלקום tv+ | `com.cellcom.cellcomtv` | `com.cellcom.cellcom_tv` | https://cellcom.co.il/tv/ |
| 64 | STING+ | `il.co.stingtv` | `il.co.stingtv.atv` | https://www.stingtv.co.il/ |
| 65 | yes+ | `il.co.yes.yesgo` | `il.co.yes.yesgo` | https://www.yes.co.il/ |
| 66 | HOT NEXT TV | `il.net.hot.nexttv` | `il.net.hot.nexttv` | https://www.hot.net.il/ |
| 67 | כאן - אפליקציה | `com.applicaster.il.ch1` | `com.applicaster.il.ch1` | https://www.kan.org.il/ |
| 68 | כאן ילדים | `com.channel23` | `com.channel23` | https://www.kankids.org.il/ |
| 69 | 12+ mako | `com.goldtouch.mako` | `com.keshet.mako.VODAndroidTV` | https://www.mako.co.il/mako-vod-live-tv |
| 70 | רשת 13+ | `com.applicaster.iReshet` | `com.applicaster.iReshet` | https://13tv.co.il/live/ |
| 71 | 14 LIVE | `com.channel.fourteen.univ` | `com.channel.fourteen.univ` | https://www.c14.co.il/live |
| 72 | ספורט 5 | `com.rge.sport5` | `com.rge.sport5` | https://www.sport5.co.il/ |

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

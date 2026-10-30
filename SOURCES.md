# מקורות – מאיפה מגיע כל ערוץ

נוצר אוטומטית מ-`channels.json` על ידי `tools/make_sources.py`. סה"כ **440** פריטים: 297 ערוצי טלוויזיה, 143 תחנות רדיו, 0 אפליקציות/שירותים.

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

- **210 שידורים רשמיים** – מהשרתים של הגוף המשדר עצמו או של המפיץ שלו (כאן, 13, 14, i24NEWS, גלי צה"ל, תחנות הרדיו, DW, אל-ג'זירה...).
- **38 ערוצי FAST** – ערוצים חינמיים עם פרסומות שהבעלים מפיצים בעצמם (Pluto TV, BabyFirst, LEGO, Baby Shark...).
- **0 ערוצים מהעולם** – שידורים פתוחים מהמאגר הציבורי iptv-org (ייבוא אוטומטי, `tools/import_iptv.py`): כל אחד נבדק שהוא משדר ושיש בו תמונה, ללא ערוצים שדורשים מנוי/כתובת מיוחדת. רובם מהשרתים של הערוצים עצמם, אבל הבעלים לא נבדקו אחד-אחד.
- **192 ממסרי צד שלישי** – בעיקר `stream.mcquack.net` (35 ערוצים). אלה **אינם** השרתים של הערוצים: אתר חיצוני קולט את השידור ומפיץ אותו מחדש. בהם נמצאים גם ערוצים בתשלום (HOT, yes, viju/TV1000, ערוצי ספורט) – סביר מאוד שההפצה הזו **ללא רישיון** מבעלי הזכויות, והם עלולים להיעלם בכל רגע. ההחלטה אם להשאיר אותם היא שלך; הסקריפטים לא מוסיפים ממסרים חדשים של ערוצים בתשלום.
- **שירותי המנויים** (פרטנר, סלקום, STING, yes, HOT) – אין בפרויקט שום שידור שלהם; הכרטיס רק פותח את האפליקציה/האתר הרשמי, וצפייה דורשת מנוי.

> הסיווג רשמי / FAST / ממסר נקבע לפי שם השרת שממנו מגיע השידור (למשל CDN של הגוף המשדר), ולא אושר מול כל גוף משדר בנפרד.

## לפי שרת

| שרת | מי זה | סוג | מספר |
|---|---|---|---|
| `b84203c4.ottrast.com` | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | 88 |
| `d1zqtf09wb8nt5.cloudfront.net` | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | 66 |
| `gb25.streamgates.net` | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | 60 |
| `stream.mcquack.net` | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | 35 |
| `jmp2.uk` | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | 22 |
| `cdn.cybercdn.live` | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | 18 |
| `service-stitcher.clusters.pluto.tv` | CDN רשמי (HTTPS) | ✅ FAST חינמי | 11 |
| `playerservices.streamtheworld.com` | Triton StreamTheWorld – מפיץ הרדיו של כאן | ✅ רשמי | 7 |
| `edge1.il.kab.tv` | edge1.il.kab.tv | ⚠️ ממסר צד שלישי | 6 |
| `glzwizzlv.bynetcdn.com` | גלי צה"ל / גלגלצ – CDN רשמי (Bynet) | ✅ רשמי | 6 |
| `cast.breslevforyou.co.il` | רדיו ברסלב – שרת התחנה | ✅ רשמי | 5 |
| `icecast.kab.tv` | icecast.kab.tv | ⚠️ ממסר צד שלישי | 5 |
| `kancdn.medonecdn.net` | כאן – תאגיד השידור (CDN רשמי) | ✅ רשמי | 3 |
| `r.il.cdn-redge.media` | Redge – ה-CDN הרשמי של כאן, ערוץ 14 וכלכלה 10 | ✅ רשמי | 3 |
| `skynewsau-live.akamaized.net` | skynewsau-live.akamaized.net | ⚠️ ממסר צד שלישי | 3 |
| `jointil.com` | jointil.com | ⚠️ ממסר צד שלישי | 3 |
| `live.wcdn.co.il` | live.wcdn.co.il | ✅ רשמי | 2 |
| `d1yd8hohnldm33.cloudfront.net` | ערוץ FAST ישראלי – AWS CloudFront | ✅ FAST חינמי | 2 |
| `kneset.gostreaming.tv` | ערוץ הכנסת – השידור של הכנסת (GoStreaming) | ✅ רשמי | 2 |
| `d2dffl3588mvfk.cloudfront.net` | ערוץ FAST ישראלי – AWS CloudFront | ✅ FAST חינמי | 2 |
| `rgelive.akamaized.net` | ספורט 5 – השידור הרשמי של RGE (Akamai) | ✅ רשמי | 2 |
| `aegis-cloudfront-1.tubi.video` | aegis-cloudfront-1.tubi.video | ⚠️ ממסר צד שלישי | 2 |
| `ncdn-live-bfm.pfd.sfr.net` | BFM – SFR (רשמי) | ✅ רשמי | 2 |
| `dash3.antik.sk` | Antik Telecom (סלובקיה), נתיבי test | ⚠️ ממסר צד שלישי | 2 |
| `radio.streamgates.net` | StreamGates – ה-CDN של התחנה | ✅ רשמי | 2 |
| `media2.93fm.co.il` | קול חי – שרת התחנה | ✅ רשמי | 2 |
| `icecast.live` | icecast.live | ⚠️ ממסר צד שלישי | 2 |
| `cdna.streamgates.net` | StreamGates – ה-CDN של התחנה | ✅ רשמי | 2 |
| `live.ecast.co.il` | eCast – שרת התחנה | ✅ רשמי | 2 |
| `d18b0e6mopany4.cloudfront.net` | רשת 13 – AWS CloudFront | ✅ רשמי | 1 |
| `ynet-live-01.ynet-pic1.yit.co.il` | ynet – השרת של ידיעות אחרונות | ✅ רשמי | 1 |
| `d15ds134q59udk.cloudfront.net` | ערוץ FAST ישראלי – AWS CloudFront | ✅ FAST חינמי | 1 |
| `1247634592.rsc.cdn77.org` | שלנו TV – CDN77 | ✅ רשמי | 1 |
| `live.creacast.com` | ILTV – CreaCast (רשמי) | ✅ רשמי | 1 |
| `jlt-jltv-xumo.otteravision.com` | JLTV – Ottera / Xumo FAST | ✅ FAST חינמי | 1 |
| `janya-digimix.akamaized.net` | Angel TV Hebrew – Akamai (רשמי) | ✅ רשמי | 1 |
| `reshet.g-mana.live` | רשת 13 – השרת הרשמי (G-Mana) | ✅ רשמי | 1 |
| `cdn1.cast-tv.com` | cdn1.cast-tv.com | ⚠️ ממסר צד שלישי | 1 |
| `wl.tvrain.tv` | wl.tvrain.tv | ⚠️ ממסר צד שלישי | 1 |
| `ynet-live-02.ynet-pic1.yit.co.il` | ynet-live-02.ynet-pic1.yit.co.il | ✅ רשמי | 1 |
| `i24newsfrench-cdn.encoders.immergo.tv` | i24NEWS – שרת רשמי | ✅ רשמי | 1 |
| `636ffd31f0e12.streamlock.net` | 636ffd31f0e12.streamlock.net | ⚠️ ממסר צד שלישי | 1 |
| `rbmn-live.akamaized.net` | Red Bull TV – Akamai (רשמי) | ✅ רשמי | 1 |
| `bonappetit-samsung.amagi.tv` | bonappetit-samsung.amagi.tv | ⚠️ ממסר צד שלישי | 1 |
| `e75fc053c0d2406fb74cdd71d77e1a7f.mediatailor.us-east-1.amazonaws.com` | e75fc053c0d2406fb74cdd71d77e1a7f.mediatailor.us-east-1.amazonaws.com | ⚠️ ממסר צד שלישי | 1 |
| `streaming.aurora.enhanced.live` | streaming.aurora.enhanced.live | ⚠️ ממסר צד שלישי | 1 |
| `tvf-tv5ch.otteravision.com` | tvf-tv5ch.otteravision.com | ⚠️ ממסר צד שלישי | 1 |
| `stream-us-east-1.getpublica.com` | stream-us-east-1.getpublica.com | ⚠️ ממסר צד שלישי | 1 |
| `hlspackager.akamaized.net` | i24NEWS – CDN Akamai של השידור הרשמי | ✅ רשמי | 1 |
| `linear901-oo-hls0-prd-gtm.delivery.skycdp.com` | linear901-oo-hls0-prd-gtm.delivery.skycdp.com | ⚠️ ממסר צד שלישי | 1 |
| `dwamdstream102.akamaized.net` | DW – Akamai (רשמי) | ✅ רשמי | 1 |
| `vs-hls-push-ww-live.akamaized.net` | vs-hls-push-ww-live.akamaized.net | ⚠️ ממסר צד שלישי | 1 |
| `turnerlive.warnermediacdn.com` | turnerlive.warnermediacdn.com | ⚠️ ממסר צד שלישי | 1 |
| `fox-foxnewsnow-samsungus.amagi.tv` | fox-foxnewsnow-samsungus.amagi.tv | ⚠️ ממסר צד שלישי | 1 |
| `cbsn-us.cbsnstream.cbsnews.com` | cbsn-us.cbsnstream.cbsnews.com | ⚠️ ממסר צד שלישי | 1 |
| `www.bloomberg.com` | www.bloomberg.com | ⚠️ ממסר צד שלישי | 1 |
| `amg00217-newsmax-newsmax-zeasn-022k9.amagi.tv` | amg00217-newsmax-newsmax-zeasn-022k9.amagi.tv | ⚠️ ממסר צד שלישי | 1 |
| `tv-trtworld.medya.trt.com.tr` | tv-trtworld.medya.trt.com.tr | ⚠️ ממסר צד שלישי | 1 |
| `english-livetx.cgtn.com` | english-livetx.cgtn.com | ⚠️ ממסר צד שלישי | 1 |
| `skyfolk.mk` | Sky Folk Live | ✅ רשמי | 1 |
| `stream.4fun.tv` | 4FUN TV – שרת רשמי | ✅ רשמי | 1 |
| `176.118.197.101` | שרת IP ללא שם (1HD) | ⚠️ ממסר צד שלישי | 1 |
| `vivo.solumedia.com` | vivo.solumedia.com | ⚠️ ממסר צד שלישי | 1 |
| `newidco-babysharktv-1-us.roku.wurl.tv` | Baby Shark TV – Wurl (Roku FAST) | ✅ FAST חינמי | 1 |
| `cdn.fast.jwp.services` | BabyFirst – JW Player FAST | ✅ FAST חינמי | 1 |
| `zoomoo-samsungau.amagi.tv` | amagi.tv | ✅ FAST חינמי | 1 |
| `dltiqboxjw21d.cloudfront.net` | LEGO Channel – AWS CloudFront FAST | ✅ FAST חינמי | 1 |
| `bein-xtra-bein.amagi.tv` | bein-xtra-bein.amagi.tv | ⚠️ ממסר צד שלישי | 1 |
| `d11k1mnrgfposz.cloudfront.net` | d11k1mnrgfposz.cloudfront.net | ⚠️ ממסר צד שלישי | 1 |
| `amg00056-amg00056c6-rakuten-uk-3235.playouts.now.amagi.tv` | amg00056-amg00056c6-rakuten-uk-3235.playouts.now.amagi.tv | ⚠️ ממסר צד שלישי | 1 |
| `dai.google.com` | dai.google.com | ⚠️ ממסר צד שלישי | 1 |
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
| `ff_engine.streamgates.net` | ff_engine.streamgates.net | ⚠️ ממסר צד שלישי | 1 |
| `stream.radio.co` | stream.radio.co | ⚠️ ממסר צד שלישי | 1 |
| `streams.radio.co` | streams.radio.co | ⚠️ ממסר צד שלישי | 1 |
| `uksoutha.streaming.broadcast.radio` | uksoutha.streaming.broadcast.radio | ⚠️ ממסר צד שלישי | 1 |
| `c44.radioboss.fm` | c44.radioboss.fm | ⚠️ ממסר צד שלישי | 1 |

## 📺 ערוצי ישראל - שידור חי

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 1 | כאן 11 FHD | כאן – תאגיד השידור (CDN רשמי) | ✅ רשמי | iptv-org (Kan11.il) | ✅ |
| 2 | קשת 12 FHD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | iptv-org (Keshet12.il) | ✅ |
| 3 | רשת 13 FHD | רשת 13 – AWS CloudFront | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 4 | עכשיו 14 FHD | Redge – ה-CDN הרשמי של כאן, ערוץ 14 וכלכלה 10 | ✅ רשמי | iptv-org (Now14.il) | ✅ |
| 5 | i24NEWS עברית HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | iptv-org (i24NEWSHebrew.il) | ✅ |
| 6 | ערוץ חדשות 16 HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | iptv-org (Channel16.il) | ✅ |
| 7 | ערוץ 9 הישראלי FHD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | iptv-org (Channel9.il) | ✅ |
| 8 | ערוץ 24 HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | iptv-org (Channel24.il) | ✅ |
| 9 | ערוץ הכנסת 99 HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | iptv-org (KnessetChannel.il) | ✅ |
| 10 | ערוץ כלכלה 10 HD | Redge – ה-CDN הרשמי של כאן, ערוץ 14 וכלכלה 10 | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 11 | ynet Live | ynet – השרת של ידיעות אחרונות | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 12 | וואלה!+ חדשות – שידור חי HD | live.wcdn.co.il | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 13 | וואלה!+ yes תרבות ובידור HD | live.wcdn.co.il | ✅ רשמי | iptv-org (WallaYes.il) | ✅ |
| 14 | HOT בידור ישראלי HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 15 | HOT ריאל HD (HOT Real) | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 16 | רשת 13 קומדיה ובידור HD | ערוץ FAST ישראלי – AWS CloudFront | ✅ FAST חינמי | iptv-org (Reshet13Comedy.il) | ✅ |
| 17 | yes דוקו HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 18 | HOT 8 מדע ודוקו HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 19 | HOT מיינד דוקו והעשרה HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 20 | ערוץ החיים הטובים HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 21 | ערוץ בית+ Home Plus HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 22 | ערוץ העיצוב HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 23 | ערוץ הבריאות HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 24 | ערוץ הטיולים HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 25 | ערוץ הנופש והחופש HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 26 | ערוץ הבידור E! ישראל HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 27 | ערוץ אגו Ego HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 28 | ערוץ הריאליטי HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 29 | ערוץ השעשועונים HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 30 | ערוץ ההומור והצחוק HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 31 | ערוץ Beautyz אופנה ויופי HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 32 | ערוץ מוזיקה il HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 33 | ערוץ המוזיקה – קשת / FreeTV | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 34 | ערוץ רוקדים עם כוכבים 24/7 HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 35 | ערוץ 98 – האזרח הוותיק HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 36 | כאן 11 – כתוביות ונגישות HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | iptv-org (Kan11Subs.il) | ✅ |
| 37 | קשת 12 – כתוביות ונגישות HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 38 | רשת 13 – כתוביות ונגישות HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | iptv-org (Reshet13Subs.il) | ✅ |
| 39 | עכשיו 14 – כתוביות ונגישות HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 40 | HOT ערוץ החופש HD | ערוץ FAST ישראלי – AWS CloudFront | ✅ FAST חינמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 41 | Shelanu.TV – ערוץ שלנו HD | שלנו TV – CDN77 | ✅ רשמי | סריקת ערוצים מאומתת | ✅ |
| 42 | ILTV ישראל בצרפתית | ILTV – CreaCast (רשמי) | ✅ רשמי | iptv-org (ILTV.fr) | ✅ |
| 43 | JLTV טלוויזיה יהודית HD | JLTV – Ottera / Xumo FAST | ✅ FAST חינמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 44 | Angel TV Hebrew HD | Angel TV Hebrew – Akamai (רשמי) | ✅ רשמי | סריקת ערוצים מאומתת | ✅ |
| 45 | כאן 11 – כתוביות | Redge – ה-CDN הרשמי של כאן, ערוץ 14 וכלכלה 10 | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 46 | רשת 13 – נגיש | רשת 13 – השרת הרשמי (G-Mana) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 47 | ערוץ הכנסת – נגיש | ערוץ הכנסת – השידור של הכנסת (GoStreaming) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 48 | i24NEWS English | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 49 | i24NEWS צרפתית HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | iptv-org (i24NEWSFrench.il) | ✅ |
| 50 | FreeTV לייף סטייל HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 51 | FreeTV דוקו ישראלי HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 52 | ערוץ הקריוקי הישראלי HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 53 | הכותל המערבי – שידור חי 24/7 | cdn1.cast-tv.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 54 | ערוץ דוז'ד ישראל HD (TV Rain) | wl.tvrain.tv | ⚠️ ממסר צד שלישי | iptv-org (TVRain.il) | ✅ |
| 55 | ערוץ הסטייל והאופנה HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 56 | רשת 13 ערוץ הנופש HD | ערוץ FAST ישראלי – AWS CloudFront | ✅ FAST חינמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 57 | רשת 13 ערוץ הריאליטי HD | ערוץ FAST ישראלי – AWS CloudFront | ✅ FAST חינמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 58 | ערוץ הכנסת – שפת הסימנים ונגישות | ערוץ הכנסת – השידור של הכנסת (GoStreaming) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 59 | Ynet Live 2 – שידורי חדשות | ynet-live-02.ynet-pic1.yit.co.il | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 60 | הרדיו הראשון 89.1FM – וידאו חי | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 61 | רדיו לב המדינה 91FM – וידאו חי | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 62 | Daystar TV (FreeTV ישראל) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 63 | ערוץ ההומור – סלקום ופרטנר TV HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 64 | ערוץ בית ועיצוב – סלקום ופרטנר TV HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 65 | ערוץ הריאליטי הישראלי – פרטנר וסלקום TV HD | ערוץ FAST ישראלי – AWS CloudFront | ✅ FAST חינמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 66 | i24NEWS צרפתית ישראל FHD | i24NEWS – שרת רשמי | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |

## 📺 ילדים ונוער (בעברית)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 67 | ערוץ הילדים HD (המקום האמיתי) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 68 | ערוץ לוגי HD (Logi) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 69 | כאן חינוכית 23 FHD | כאן – תאגיד השידור (CDN רשמי) | ✅ רשמי | iptv-org (KanEducational.il) | ✅ |
| 70 | ערוץ הופ! ילדים בעברית HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 71 | ערוץ הופ! ילדות ישראלית HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 72 | ערוץ הופ! פלא HD (Hop Pele) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 73 | ערוץ לולי לפעוטות בעברית HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | iptv-org (Luli.il) | ❌ רק באפליקציה (HTTP) |
| 74 | ערוץ דיסני ישראל HD (Disney Channel) | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | iptv-org (DisneyChannel.il) | ❌ רק באפליקציה (HTTP) |
| 75 | ערוץ דיסני ג'וניור ישראל HD (Disney Jr.) | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | iptv-org (DisneyJunior.il) | ❌ רק באפליקציה (HTTP) |
| 76 | ערוץ ניקלודיאון ישראל HD (Nickelodeon) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 77 | ערוץ ניק ג'וניור ישראל HD (Nick Jr.) | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | iptv-org (NickJr.il) | ❌ רק באפליקציה (HTTP) |
| 78 | ערוץ טין ניק ישראל HD (TeenNick) | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | iptv-org (TeenNick.il) | ❌ רק באפליקציה (HTTP) |
| 79 | ערוץ זום ZOOM ילדים ונוער HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | iptv-org (Zoom.il) | ❌ רק באפליקציה (HTTP) |
| 80 | ערוץ ג'וניור HD (Junior) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 81 | ערוץ יויו HD (YoYo) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 82 | ערוץ בייבי ישראל HD (BabyTV) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 83 | yes קולנוע ילדים FHD (yes Movies Kids) | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 84 | HOT סינמה פמילי HD (סרטי ילדים ומשפחה) | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 85 | ערוץ WIZ! לילדים ונוער HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 86 | ערוץ HOT באזז לנוער HD (HOT Buzz) | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 87 | ערוץ FOMO לנוער ורשת HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 88 | ערוץ ילדותי HD (Yalduti) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 89 | ערוץ שירי ילדותי HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 90 | ערוץ A+ ילדים HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 91 | ערוץ הכוכבים לילדים ונוער HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 95 | ערוץ ניק ג'וניור פלוס בעברית HD (Nick Jr.+) | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |

## 📺 ספורט (Sports)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 96 | ספורט 1 HD (Sport 1) | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 97 | ספורט 2 HD (Sport 2) | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 98 | ספורט 3 HD (Sport 3) | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 99 | ספורט 4 FHD (Sport 4) | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 100 | ספורט 6 HD (Sport 6) | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 101 | ספורט 5 FHD (5SPORT) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 102 | ספורט 5 פלוס FHD (5PLUS) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 103 | ספורט 5 לייב FHD (5LIVE) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 104 | ספורט 5 גולד HD (5GOLD) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 105 | ספורט 5 סטארס FHD (5STARS) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 106 | ספורט 5 מקס FHD (5MAX) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 107 | ספורט 5 4K UHD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 108 | ערוץ ONE 1 FHD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 109 | ערוץ ONE 2 FHD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 110 | ערוץ ONE דוקו HD (ONE DOCO) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 111 | ערוץ ONE Edge HD (אדג' ספורט) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 112 | יורוספורט 2 HD (Eurosport 2) | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 113 | Sky Racing 1 HD (סקיי רייסינג ספורט) | 636ffd31f0e12.streamlock.net | ⚠️ ממסר צד שלישי | שידור רשמי Sky | ✅ |
| 114 | ספורט פלוס HD (viju+ Sport) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (vijuPlusSport.ru) | ✅ דרך מקור חלופי: Cinerama.uz – ספק IPTV באוזבקיסטן |
| 115 | ספורט סטאנטה Setanta Sports 2 HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (SetantaSports2Eurasia.ie) | ❌ רק באפליקציה (HTTP) |
| 116 | ערוץ אקסטרים Extreme Sports HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 117 | ספורט 5 – שידור חי | ספורט 5 – השידור הרשמי של RGE (Akamai) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 118 | יורוספורט 1 ישראל HD (Eurosport 1) | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 119 | Red Bull TV Extreme Sports FHD | Red Bull TV – Akamai (רשמי) | ✅ רשמי | סריקת רשת v4.6.0 | ✅ |
| 422 | MLB Baseball Live FHD | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 423 | FOX Sports Live FHD | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 425 | beIN SPORTS XTRA FHD | bein-xtra-bein.amagi.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 427 | PGA Tour Golf Live FHD | d11k1mnrgfposz.cloudfront.net | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 438 | CBS Sports HQ FHD | dai.google.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |

## 📺 סרטים וסדרות (Movies & Series)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 120 | HOT 3 ערוץ הדגל FHD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | iptv-org (Hot3.il) | ❌ רק באפליקציה (HTTP) |
| 121 | HOT HBO סדרות פרימיום HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | iptv-org (HotHBO.il) | ❌ רק באפליקציה (HTTP) |
| 122 | HOT סינמה 1 FHD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 123 | HOT סינמה 2 HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 124 | HOT סינמה 3 FHD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 125 | HOT סינמה 4 HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | iptv-org (HotCinema4.il) | ❌ רק באפליקציה (HTTP) |
| 126 | HOT סינמה 5 HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 127 | HOT קולנוע ישראלי HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 128 | HOT קולנוע רומנטי HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 129 | HOT דרמה HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 130 | HOT זון HD (HOT Zone) | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 131 | HOT קומדי HD (HOT Comedy) | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 132 | yes קולנוע ישראלי HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 133 | yes קולנוע אקשן FHD (yes Movies Action) | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 134 | yes קולנוע דרמה FHD (yes Movies Drama) | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 135 | yes קולנוע קומדיה FHD (yes Movies Comedy) | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 136 | yes TV אקשן HD (yes TV Action) | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 137 | yes TV דרמה HD (yes TV Drama) | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 138 | yes TV קומדיה HD (yes TV Comedy) | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | iptv-org (YesTvComedy.il) | ❌ רק באפליקציה (HTTP) |
| 139 | ערוץ ויוה טלנובלות HD (Viva) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 140 | ערוץ ויוה איסטנבול HD (Viva Istanbul) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 141 | ערוץ ויוה פרימיום HD (Viva Premium) | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 142 | הערוץ הים תיכוני HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 143 | הערוץ הים תיכוני+ HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 144 | ערוץ הדרמות הטורקיות+ FHD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 145 | ערוץ הדרמות הטורקיות 2 FHD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 146 | ערוץ הדרמות הטורקיות 3 FHD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 147 | ערוץ הסרטים הים תיכוני 1 HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 148 | ערוץ הסרטים הים תיכוני 2 HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 149 | ערוץ האקשן העולמי 1 HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 150 | ערוץ האקשן העולמי 2 HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 151 | ערוץ הדרמות הספרדיות 1 HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 152 | ערוץ הדרמות הספרדיות 2 HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 153 | ערוץ הדרמות ההודיות 1 HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 154 | ערוץ הדרמות ההודיות 2 HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 155 | ערוץ בוליווד סרטים הודיים HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 156 | ערוץ בוליווד קלאסי HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 157 | ערוץ בולישואו Bollyshow HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 158 | ערוץ ZEE TV ישראל HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | iptv-org (ZeeTV.il) | ❌ רק באפליקציה (HTTP) |
| 159 | ערוץ GTV דרמות וקולנוע HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | iptv-org (GTV.il) | ❌ רק באפליקציה (HTTP) |
| 160 | ערוץ הקומדיות הקלאסיות HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 161 | ערוץ האהבה והרומנטיקה HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 162 | ערוץ ארץ נהדרת 24/7 | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 163 | סברי מרנן 24/7 – קשת / FreeTV | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 164 | ערוץ הדרמה – קשת / FreeTV | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 165 | ערוץ הקומדיה – קשת / FreeTV | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 166 | FreeTV סרטים ישראלי HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 167 | FreeTV סרטים פעולה HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 168 | FreeTV סרטים קומדיה HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 169 | FreeTV סרטים דרמה HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 170 | FreeTV סרטים מתח HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 171 | FreeTV סרטים אימה HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 172 | FreeTV סרטים רומנטי HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 173 | FreeTV סרטים פיל גוד HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 174 | FreeTV סרטים משפחה HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 175 | FreeTV 10 Stars סרטים HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 176 | FreeTV סדרות קומדיה 2 HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 177 | ערוץ הדרמות היומיות HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 178 | ערוץ וואמוס HD (Vamos) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 179 | FreeTV גלובל סדרות HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 180 | ערוץ סרטי אימה HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (Kinouzhas.ru) | ❌ רק באפליקציה (HTTP) |
| 181 | קולנוע להיטים KinoHit HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (KinoHit.ru) | ✅ דרך מקור חלופי: Cinerama.uz – ספק IPTV באוזבקיסטן |
| 182 | משפחת סימפסון 24/7 HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 183 | קולנוע רטרו VHS Cinema HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 184 | ערוץ הדרמות הטורקיות – סלקום ופרטנר TV HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 185 | Pluto TV Action Movies FHD | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | סריקת רשת v4.6.0 | ✅ |
| 186 | Pluto TV Sci-Fi FHD | CDN רשמי (HTTPS) | ✅ FAST חינמי | סריקת רשת v4.6.0 | ✅ |
| 187 | Pluto TV Thrillers FHD | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | סריקת רשת v4.6.0 | ✅ |
| 188 | Pluto TV Comedy Movies FHD | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | סריקת רשת v4.6.0 | ✅ |
| 418 | Pluto TV Spotlight Movies FHD | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 419 | Pluto TV True Crime & Drama FHD | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 420 | Pluto TV Drama Movies FHD | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 421 | Pluto TV Horror Movies FHD | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 424 | Pluto TV Classic Movies FHD | service-stitcher.clusters.pluto.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 426 | Pluto TV Romance Movies FHD | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 428 | Star Trek 24/7 Channel FHD | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 440 | CSI Crime Scene Investigation FHD | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | רשימה ידנית (גרסה קודמת) | ✅ |

## 📺 אוכל ובישול (Food)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 189 | ערוץ האוכל Food Network HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 190 | ערוץ פודי Foody HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 191 | ערוץ האוכל והבישול – קשת / FreeTV | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 192 | Bon Appétit HD – ערוץ הבישול בון אפטיט | bonappetit-samsung.amagi.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 193 | Gusto TV FHD – ערוץ האוכל העולמי גוסטו | e75fc053c0d2406fb74cdd71d77e1a7f.mediatailor.us-east-1.amazonaws.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 194 | Gordon Ramsay's Hell's Kitchen 24/7 HD | aegis-cloudfront-1.tubi.video | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 195 | Food Network International HD | streaming.aurora.enhanced.live | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 196 | Tastemade Food & Travel HD | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 197 | TV5MONDE Chefs HD – שפים וגסטרונומיה | tvf-tv5ch.otteravision.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 198 | Cooking Panda HD – מתכונים ובישול | stream-us-east-1.getpublica.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 199 | Pluto TV Food & Cooking HD | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 200 | Pluto TV Cuisine HD – קולינריה עולמית | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 201 | Pluto TV Cucina Italiana HD – המטבח האיטלקי | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 202 | Pluto TV K-Food HD – אוכל אסייתי וקוריאני | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 203 | Kus-Kus Culinary TV HD – ערוץ קוסקוס אוכל ומתכונים | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 204 | America's Test Kitchen FHD | CDN רשמי (HTTPS) | ✅ FAST חינמי | סריקת רשת v4.6.0 | ✅ |
| 205 | Jamie Oliver Channel FHD | CDN רשמי (HTTPS) | ✅ FAST חינמי | סריקת רשת v4.6.0 | ✅ |
| 432 | מטבחי הגיהנום 24/7 — Hell's Kitchen FHD | service-stitcher.clusters.pluto.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |

## 📺 חדשות מהעולם (News)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 206 | i24NEWS English World HD | i24NEWS – CDN Akamai של השידור הרשמי | ✅ רשמי | סריקת ערוצים מאומתת | ✅ |
| 207 | Sky News UK FHD | linear901-oo-hls0-prd-gtm.delivery.skycdp.com | ⚠️ ממסר צד שלישי | שידור רשמי Sky | ✅ |
| 208 | Sky News Australia Extra 1 HD | skynewsau-live.akamaized.net | ⚠️ ממסר צד שלישי | שידור רשמי Sky | ✅ |
| 209 | Sky News Australia Extra 2 HD | skynewsau-live.akamaized.net | ⚠️ ממסר צד שלישי | שידור רשמי Sky | ✅ |
| 210 | Sky News Australia Extra 3 HD | skynewsau-live.akamaized.net | ⚠️ ממסר צד שלישי | שידור רשמי Sky | ✅ |
| 211 | DW News English HD | DW – Akamai (רשמי) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 212 | BFM TV HD (France) | BFM – SFR (רשמי) | ✅ רשמי | iptv-org (BFMTV.fr) | ✅ |
| 213 | BFM Tech & Co HD | BFM – SFR (רשמי) | ✅ רשמי | iptv-org (BFMTechCo.fr) | ✅ |
| 214 | France 24 (FreeTV ישראל) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 215 | BBC News World FHD | vs-hls-push-ww-live.akamaized.net | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 216 | CNN International HD | turnerlive.warnermediacdn.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 217 | Fox News Channel HD | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 218 | FOX LiveNOW News HD | fox-foxnewsnow-samsungus.amagi.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 219 | CBS News 24/7 FHD | cbsn-us.cbsnstream.cbsnews.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 220 | Bloomberg TV+ Business FHD | www.bloomberg.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 221 | Scripps News USA FHD | aegis-cloudfront-1.tubi.video | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 222 | Newsmax TV HD | amg00217-newsmax-newsmax-zeasn-022k9.amagi.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 223 | TRT World News FHD | tv-trtworld.medya.trt.com.tr | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 224 | CGTN News English FHD | english-livetx.cgtn.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 225 | CNBC Business Live HD | CDN רשמי (HTTPS) | ✅ FAST חינמי | סריקת רשת v4.6.0 | ✅ |

## 📺 טבע ודוקו (Documentary & Nature)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 226 | נשיונל ג'יאוגרפיק ישראל HD (Nat Geo) | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | iptv-org (NationalGeographic.il) | ❌ רק באפליקציה (HTTP) |
| 227 | נשיונל ג'יאוגרפיק וויילד FHD (Nat Geo Wild) | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | iptv-org (NationalGeographicWild.il) | ❌ רק באפליקציה (HTTP) |
| 228 | ערוץ דיסקברי ישראל FHD (Discovery) | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | iptv-org (DiscoveryChannel.il) | ❌ רק באפליקציה (HTTP) |
| 229 | ערוץ ההיסטוריה ישראל FHD (History) | b84203c4.ottrast.com | ⚠️ ממסר צד שלישי | iptv-org (History.il) | ❌ רק באפליקציה (HTTP) |
| 230 | ערוץ הטבע הישראלי HD (Teva) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 231 | Animal Planet World HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (AnimalPlanet.ru) | ❌ רק באפליקציה (HTTP) |
| 232 | Discovery חקירות (ID) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (InvestigationDiscovery.ru) | ❌ רק באפליקציה (HTTP) |
| 233 | עולם וטבע HD (viju+ Planet) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (vijuPlusPlanet.ru) | ❌ רק באפליקציה (HTTP) |
| 234 | Pluto TV History FHD | CDN רשמי (HTTPS) | ✅ FAST חינמי | סריקת רשת v4.6.0 | ✅ |
| 235 | Pluto TV Animals & Nature FHD | CDN רשמי (HTTPS) | ✅ FAST חינמי | סריקת רשת v4.6.0 | ✅ |
| 433 | Smithsonian Channel Selects FHD | service-stitcher.clusters.pluto.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 436 | Pluto TV Science & Space FHD | service-stitcher.clusters.pluto.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |

## 📺 מוזיקה (Music TV)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 236 | רדיוס 100FM – אולפן וידאו HD | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 237 | Sky Folk TV HD (סקיי פולק) | Sky Folk Live | ✅ רשמי | שידור רשמי Sky | ✅ |
| 238 | 4ever Music Hits HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (4everMusic.ua) | ❌ רק באפליקציה (HTTP) |
| 239 | 4Fun TV Pop Hits HD | 4FUN TV – שרת רשמי | ✅ רשמי | iptv-org (4FunTV.pl) | ✅ |
| 240 | ¡Viva Latino! Dance HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (VivaLatino.ua) | ❌ רק באפליקציה (HTTP) |
| 241 | 1HD Music Television | שרת IP ללא שם (1HD) | ⚠️ ממסר צד שלישי | iptv-org (1HDMusicTelevision.ru) | ❌ רק באפליקציה (HTTP) |
| 434 | Vevo 90s Music Classics FHD | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 437 | Vevo Pop Music Hits FHD | amg00056-amg00056c6-rakuten-uk-3235.playouts.now.amagi.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |

## 📺 מסורת וקהילה

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 242 | ערוץ הידברות 97 HD | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | iptv-org (Hidabroot.il) | ✅ |
| 243 | ערוץ קבלה לעם 66 HD | edge1.il.kab.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 244 | קבלה לעם – רוסית (ערוץ 66) | edge1.il.kab.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 245 | קבלה לעם – רוסית HD | edge1.il.kab.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 246 | קבלה לעם – צרפתית HD | edge1.il.kab.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 247 | קבלה לעם – גרמנית HD | edge1.il.kab.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 248 | קבלה לעם – איטלקית HD | edge1.il.kab.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 249 | Jai TV – טלוויזיה יהודית וישראלית | vivo.solumedia.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |

## 📺 ילדים מהעולם (Kids World)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 390 | Baby Shark TV (בייבי שארק לילדים) | Baby Shark TV – Wurl (Roku FAST) | ✅ FAST חינמי | סריקת ערוצי ילדים | ✅ |
| 391 | ערוץ בייבי BabyTV Global HD | BabyFirst – JW Player FAST | ✅ FAST חינמי | סריקת ערוצי ילדים | ✅ |
| 392 | ערוץ זו-מו לילדים (ZooMoo HD) | amagi.tv | ✅ FAST חינמי | סריקת ערוצי ילדים | ✅ |
| 393 | ערוץ אני אנימציה HD (Ani Kids) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | סריקת ערוצי ילדים | ❌ רק באפליקציה (HTTP) |
| 394 | ערוץ האנימציה PLUSPLUS HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | סריקת ערוצי ילדים | ❌ רק באפליקציה (HTTP) |
| 395 | ערוץ ניקי ג'וניור HD (Niki Junior) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | סריקת ערוצי ילדים | ❌ רק באפליקציה (HTTP) |
| 396 | ערוץ סרטוני אנימציה קצרים (Cartoons Short) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | סריקת ערוצי ילדים | ❌ רק באפליקציה (HTTP) |
| 397 | ערוץ סרטי אנימציה לילדים (Cartoons Cinema) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | סריקת ערוצי ילדים | ❌ רק באפליקציה (HTTP) |
| 398 | פרש קידס (Fresh Kids TV HD) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | סריקת ערוצי ילדים | ❌ רק באפליקציה (HTTP) |
| 399 | ניק ג'וניור (ברוסית) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (NickJr.ru) | ✅ דרך מקור חלופי: Cinerama.uz – ספק IPTV באוזבקיסטן |
| 400 | גולי גירל Gulli Girl (ברוסית) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (GulliGirl.ru) | ✅ דרך מקור חלופי: Cinerama.uz – ספק IPTV באוזבקיסטן |
| 401 | דאק טי וי פלוס ducktv PLUS HD | Antik Telecom (סלובקיה), נתיבי test | ⚠️ ממסר צד שלישי | iptv-org (ducktvPLUS.sk) | ✅ |
| 402 | ערוץ לגו Lego Channel HD | LEGO Channel – AWS CloudFront FAST | ✅ FAST חינמי | iptv-org (LegoChannel.us) | ✅ |
| 403 | ערוץ טיז'י TiJi לילדים HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (TiJi.ru) | ✅ דרך מקור חלופי: Cinerama.uz – ספק IPTV באוזבקיסטן |
| 404 | ג'ים ג'אם JimJam לילדים HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (JimJamEurope.uk) | ❌ רק באפליקציה (HTTP) |
| 405 | קלאסיקות מצוירות 90s HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 406 | קולנוע אגדות וילדים HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (KinoKazka.es) | ❌ רק באפליקציה (HTTP) |
| 407 | פיקסל TV ילדים HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (PixelTV.ua) | ✅ דרך מקור חלופי: live-tv.cloud – ספק IPTV אוקראיני |
| 408 | ניקי קידס Niki Kids HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (NikiKids.ua) | ❌ רק באפליקציה (HTTP) |
| 409 | קפטן פנטסטיקה HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (KapitanFantastika.ru) | ✅ דרך מקור חלופי: Cinerama.uz – ספק IPTV באוזבקיסטן |
| 410 | ערוץ O! ילדים וטבע | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (O.ru) | ❌ רק באפליקציה (HTTP) |
| 411 | דאק טי וי ducktv HD | Antik Telecom (סלובקיה), נתיבי test | ⚠️ ממסר צד שלישי | iptv-org (ducktv.sk) | ✅ |
| 412 | ערוץ השמש לילדים HD (Solntse Kids) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (Solnce.ru) | ✅ דרך מקור חלופי: ערוץ השמש (Солнце) – CDN |
| 413 | ערוץ מולט ילדים HD (Mult Kids) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (Mult.ru) | ✅ דרך מקור חלופי: Cinerama.uz – ספק IPTV באוזבקיסטן |
| 414 | מולט ומוזיקה לילדים HD (Mult & Music) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (Multimuzyka.ru) | ❌ רק באפליקציה (HTTP) |
| 415 | קרוסלה ערוץ הילדים HD (Carousel Kids) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (Carousel.ru) | ✅ דרך מקור חלופי: Smotrim (VGTRK) – הבעלים של קרוסלה |
| 416 | סי טי סי קידס HD (CTC Kids) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (STSkids.ru) | ❌ רק באפליקציה (HTTP) |
| 417 | ערוץ אגדות וסיפורים HD (Fairy Tales Kids) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (Vgostyakhuskazki.ru) | ✅ דרך מקור חלופי: Cinerama.uz – ספק IPTV באוזבקיסטן |
| 429 | צבי הנינג׳ה 24/7 — Totally Turtles FHD | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 430 | רובוטריקים 24/7 — Transformers TV FHD | service-stitcher.clusters.pluto.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 431 | Nick Pluto TV Kids FHD | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 435 | גארפילד וחברים 24/7 FHD | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 439 | Nick Jr. Pluto TV FHD | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | רשימה ידנית (גרסה קודמת) | ✅ |

## 📻 רדיו

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 92 | רדיוס 100% שירי דיסני | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 93 | רדיוס 100% ילדים | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 94 | רדיוס 100% ערוץ הכוכבים | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 250 | ברסלב: הלכות ודף יומי | רדיו ברסלב – שרת התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 251 | ברסלב: ספרי ברסלב | רדיו ברסלב – שרת התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 252 | ברסלב: קצר וקולע | רדיו ברסלב – שרת התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 253 | ברסלב: תורה וזימרה | רדיו ברסלב – שרת התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 254 | כאן ב - רשת ב | Triton StreamTheWorld – מפיץ הרדיו של כאן | ✅ רשמי | radio-browser.info + בדיקת שמות הזרמים של כאן | ✅ |
| 255 | כאן גימל | Triton StreamTheWorld – מפיץ הרדיו של כאן | ✅ רשמי | radio-browser.info + בדיקת שמות הזרמים של כאן | ✅ |
| 256 | כאן 88 | Triton StreamTheWorld – מפיץ הרדיו של כאן | ✅ רשמי | radio-browser.info + בדיקת שמות הזרמים של כאן | ✅ |
| 257 | כאן תרבות | Triton StreamTheWorld – מפיץ הרדיו של כאן | ✅ רשמי | radio-browser.info + בדיקת שמות הזרמים של כאן | ✅ |
| 258 | כאן מורשת | Triton StreamTheWorld – מפיץ הרדיו של כאן | ✅ רשמי | radio-browser.info + בדיקת שמות הזרמים של כאן | ✅ |
| 259 | כאן רקע | Triton StreamTheWorld – מפיץ הרדיו של כאן | ✅ רשמי | radio-browser.info + בדיקת שמות הזרמים של כאן | ✅ |
| 260 | כאן קול המוזיקה | Triton StreamTheWorld – מפיץ הרדיו של כאן | ✅ רשמי | radio-browser.info + בדיקת שמות הזרמים של כאן | ✅ |
| 261 | גלגלצ | גלי צה"ל / גלגלצ – CDN רשמי (Bynet) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 262 | גלי צה"ל | גלי צה"ל / גלגלצ – CDN רשמי (Bynet) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 263 | גלגלצ להיטים | גלי צה"ל / גלגלצ – CDN רשמי (Bynet) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 264 | eco99fm | eco99fm – Mediacast (רשמי) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 265 | 103FM | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 266 | רדיוס 100FM | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 267 | לב המדינה 91FM | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 268 | 102FM רדיו תל אביב | 102FM – LiveCDN (רשמי) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 269 | רדיו חיפה 107.5FM | רדיו חיפה – LiveCDN (רשמי) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 270 | 101FM רדיו ירושלים | StreamGates – ה-CDN של התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 271 | 90FM אמצע הדרך | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 272 | רדיו דרום 101.5FM | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 273 | רדיו דרום 97FM | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 274 | 104.5FM רדיו צפון | StreamGates – ה-CDN של התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 275 | קול רגע 96FM | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 276 | 102FM קול הים האדום | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 277 | גלי ישראל 94FM | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 278 | קול חי 93FM | קול חי – שרת התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 279 | קול חי מיוזיק | קול חי – שרת התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 280 | קול ברמה | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 281 | קול ברמה מוזיקה (קול פליי) | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 282 | רדיו 2000 | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 283 | רדיו ברסלב | רדיו ברסלב – שרת התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 284 | מזרחית FM | rs.mizrahit.fm | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 285 | נושמים מזרחית | www.rs.mizrahit.fm | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 286 | רדיו עברי 6 (מוזיקה ישראלית) | streaming.radio.co | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 287 | רדיו הקצה KZ | רדיו הקצה – Mediacast (רשמי) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 288 | רדיו ספורט 5 | ספורט 5 – השידור הרשמי של RGE (Akamai) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 289 | 99.5FM רדיו | 99.5FM – LiveCDN (רשמי) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 290 | AvivMedia טראנס | 176.9.7.145 | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 291 | Israeli Voice Radio | icecast.live | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 292 | Radio France Israël | stream10.vestaradio.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 293 | TIMBR רדיו | radio.betteradio.live | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 294 | Up2Dance רדיו | icecast.live | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 295 | yStream | ystream.live | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 296 | ג'וינט רדיו בלוז רוק | jointil.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 297 | ג'וינט רדיו טראנס | jointil.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 298 | ג'וינט רדיו רגאיי | jointil.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 299 | גלגלצ עשורים | גלי צה"ל / גלגלצ – CDN רשמי (Bynet) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 300 | גלגלצ רגוע | גלי צה"ל / גלגלצ – CDN רשמי (Bynet) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 301 | גלגלצ רוק | גלי צה"ל / גלגלצ – CDN רשמי (Bynet) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 302 | הרדיו הטוב 106.4FM (רוסית) | StreamGates – ה-CDN של התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 303 | הרדיו הראשון 89.1FM (רוסית) | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 304 | סטאר 33 רדיו | online.star33radio.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 305 | קבלה לעם רדיו | icecast.kab.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 306 | קבלה לעם רדיו – אנגלית | icecast.kab.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 307 | קבלה לעם רדיו – ספרדית | icecast.kab.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 308 | קבלה לעם רדיו – צרפתית | icecast.kab.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 309 | קבלה לעם רדיו – רוסית | icecast.kab.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 310 | קול הצפון | eCast – שרת התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 311 | רדיו CS קלאסי | n0a.radiojar.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 312 | רדיו אהבה – רומנטי | liveradio.co.il | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 313 | רדיו אלי ניסמן | listen6.myradio24.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 314 | רדיו התחנה 101.5 | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 315 | רדיו נהריה | רדיו נהריה – שרת התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 316 | רדיו סהר | eCast – שרת התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 317 | רדיו שירי דיכאון | רדיו שירי דיכאון – השרת של התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 318 | רדיוס 100% K-Pop | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 319 | רדיוס 100% אגדות | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 320 | רדיוס 100% אהבה | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 321 | רדיוס 100% אולדיז | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 322 | רדיוס 100% אימון | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 323 | רדיוס 100% אירוויזיון | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 324 | רדיוס 100% בלוז | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 325 | רדיוס 100% בריטפופ | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 326 | רדיוס 100% ג'אז | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 327 | רדיוס 100% גיימר | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 328 | רדיוס 100% טופ 40 | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 329 | רדיוס 100% טיקטוק | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 330 | רדיוס 100% יהודי | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 331 | רדיוס 100% להיטים | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 332 | רדיוס 100% לטינו | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 333 | רדיוס 100% מוזיקת עולם | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 334 | רדיוס 100% מזרחית | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 335 | רדיוס 100% סטנדאפ | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 336 | רדיוס 100% סלפי | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 337 | רדיוס 100% סרטים | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 338 | רדיוס 100% צ'ילאאוט | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 339 | רדיוס 100% קאברים | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 340 | רדיוס 100% קאנטרי | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 341 | רדיוס 100% קלאב | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 342 | רדיוס 100% קלאסית | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 343 | רדיוס 100% רגוע | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 344 | רדיוס 100% רוק | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 345 | רדיוס 100% רוק קלאסי | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 346 | רדיוס 100% רטרו | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 347 | רדיוס 100% שנות ה-80 | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 348 | רדיוס 100% שנות ה-90 | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 349 | רדיוס נוסטלגי 96.3 | StreamGates – ה-CDN של התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 350 | רק מוזיקה (Rak Musica) | play.radioking.io | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 351 | AudioVersity רדיו אוניברסיטת רייכמן | runi.ac.il | ✅ רשמי | סריקת ערוצים מאומתת | ✅ |
| 352 | Radio Angelika תל אביב | radioangelika.com | ✅ רשמי | סריקת ערוצים מאומתת | ✅ |
| 353 | Душа Кавказа – רדיו קהילת קווקז בישראל | www.kavkazportal.com | ⚠️ ממסר צד שלישי | סריקת ערוצים מאומתת | ✅ |
| 354 | רדיו אורנו (Орэну) | orenu.co.il | ✅ רשמי | סריקת ערוצים מאומתת | ✅ |
| 355 | רדיו קווליטה (Radio Qualita) | RadioKing – נגן השידור של Studio Qualita | ✅ רשמי | זרם Radio Qualita; זהות התחנה אומתה באתר qualita.org.il | ✅ |
| 356 | Radio Plus ישראל | YesStreaming – זרם הנגן באתר Radio Plus | ✅ רשמי | נגן Radio Plus באתר radioplus.co.il | ✅ |
| 357 | Jewish Music Stream (מוזיקה יהודית) | stream.jewishmusicstream.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 358 | רדיוס 100% שלמה ארצי | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 359 | רדיוס 100% ישראלי | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 360 | רדיוס 100% חפלה | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 361 | רדיוס 100% ים תיכוני לוהט | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 362 | רדיוס 100% נוסטלגיה | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 363 | רדיוס 100FM – עושים היסטוריה | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 364 | רדיוס 100% שנות ה-70 | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 365 | רדיוס 100% שנות ה-2000 | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 366 | רדיוס 100% טראנס | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 367 | רדיוס 100% דאנס | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 368 | רדיוס 100% היפ הופ | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 369 | רדיוס 100% דיפ (Deep) | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 370 | רדיוס 100% סט די-ג'יי | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 371 | רדיוס 100% קריוקי | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 372 | רדיוס 100% קפה (Café) | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 373 | רדיוס 100% דרייב טיים | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 374 | רדיוס 100% מקום ראשון (No.1) | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 375 | רדיוס 100% הופעות חיות | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 376 | רדיוס 100% יוונית | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 377 | רדיוס 100% איטלקית | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 378 | רדיוס 100% צרפתית | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 379 | רדיוס 100% רוסית (89.1) | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 380 | רדיוס 100FM – תומר מייזנר | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 381 | רדיוס 100FM – VIP Digital | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 382 | רדיוס 100FM – בית לוינשטיין | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 383 | רדיוס 100FM – התקליטייה | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 384 | רדיוס 100FM – מבזק חדשות 12 | ff_engine.streamgates.net | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 385 | כאן חינוכית – רדיו ילדים | כאן – תאגיד השידור (CDN רשמי) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 386 | רדיו חב"ד – מוזיקה יהודית | stream.radio.co | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 387 | רדיו חב"ד – שיעורי תורה | streams.radio.co | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 388 | קול רדיו (Kol Radio) | uksoutha.streaming.broadcast.radio | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 389 | Imsostablee Radio ישראל | c44.radioboss.fm | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |

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

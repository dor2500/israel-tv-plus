# מקורות – מאיפה מגיע כל ערוץ

נוצר אוטומטית מ-`channels.json` על ידי `tools/make_sources.py`. סה"כ **402** פריטים: 266 ערוצי טלוויזיה, 136 תחנות רדיו, 0 אפליקציות/שירותים.

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

- **302 שידורים רשמיים** – מהשרתים של הגוף המשדר עצמו או של המפיץ שלו (כאן, 13, 14, i24NEWS, גלי צה"ל, תחנות הרדיו, DW, אל-ג'זירה...).
- **19 ערוצי FAST** – ערוצים חינמיים עם פרסומות שהבעלים מפיצים בעצמם (Pluto TV, BabyFirst, LEGO, Baby Shark...).
- **0 ערוצים מהעולם** – שידורים פתוחים מהמאגר הציבורי iptv-org (ייבוא אוטומטי, `tools/import_iptv.py`): כל אחד נבדק שהוא משדר ושיש בו תמונה, ללא ערוצים שדורשים מנוי/כתובת מיוחדת. רובם מהשרתים של הערוצים עצמם, אבל הבעלים לא נבדקו אחד-אחד.
- **77 ממסרי צד שלישי** – בעיקר `stream.mcquack.net` (9 ערוצים). אלה **אינם** השרתים של הערוצים: אתר חיצוני קולט את השידור ומפיץ אותו מחדש. בהם נמצאים גם ערוצים בתשלום (HOT, yes, viju/TV1000, ערוצי ספורט) – סביר מאוד שההפצה הזו **ללא רישיון** מבעלי הזכויות, והם עלולים להיעלם בכל רגע. ההחלטה אם להשאיר אותם היא שלך; הסקריפטים לא מוסיפים ממסרים חדשים של ערוצים בתשלום.
- **שירותי המנויים** (פרטנר, סלקום, STING, yes, HOT) – אין בפרויקט שום שידור שלהם; הכרטיס רק פותח את האפליקציה/האתר הרשמי, וצפייה דורשת מנוי.

> הסיווג רשמי / FAST / ממסר נקבע לפי שם השרת שממנו מגיע השידור (למשל CDN של הגוף המשדר), ולא אושר מול כל גוף משדר בנפרד.

## לפי שרת

| שרת | מי זה | סוג | מספר |
|---|---|---|---|
| `b84203c4.ottrast.com` | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | 93 |
| `d1zqtf09wb8nt5.cloudfront.net` | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | 66 |
| `gb25.streamgates.net` | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | 59 |
| `cdn.cybercdn.live` | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | 17 |
| `service-stitcher.clusters.pluto.tv` | CDN רשמי (HTTPS) | ✅ FAST חינמי | 11 |
| `stream.mcquack.net` | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | 9 |
| `playerservices.streamtheworld.com` | Triton StreamTheWorld – מפיץ הרדיו של כאן | ✅ רשמי | 7 |
| `glzwizzlv.bynetcdn.com` | גלי צה"ל / גלגלצ – CDN רשמי (Bynet) | ✅ רשמי | 6 |
| `cast.breslevforyou.co.il` | רדיו ברסלב – שרת התחנה | ✅ רשמי | 5 |
| `edge1.il.kab.tv` | edge1.il.kab.tv | ⚠️ ממסר צד שלישי | 4 |
| `icecast.kab.tv` | icecast.kab.tv | ⚠️ ממסר צד שלישי | 4 |
| `r.il.cdn-redge.media` | Redge – ה-CDN הרשמי של כאן, ערוץ 14 וכלכלה 10 | ✅ רשמי | 3 |
| `kancdn.medonecdn.net` | כאן – תאגיד השידור (CDN רשמי) | ✅ רשמי | 3 |
| `skynewsau-live.akamaized.net` | skynewsau-live.akamaized.net | ⚠️ ממסר צד שלישי | 3 |
| `jointil.com` | jointil.com | ⚠️ ממסר צד שלישי | 3 |
| `d2dffl3588mvfk.cloudfront.net` | ערוץ FAST ישראלי – AWS CloudFront | ✅ FAST חינמי | 2 |
| `live.wcdn.co.il` | live.wcdn.co.il | ✅ רשמי | 2 |
| `d15ds134q59udk.cloudfront.net` | ערוץ FAST ישראלי – AWS CloudFront | ✅ FAST חינמי | 2 |
| `d1yd8hohnldm33.cloudfront.net` | ערוץ FAST ישראלי – AWS CloudFront | ✅ FAST חינמי | 2 |
| `kneset.gostreaming.tv` | ערוץ הכנסת – השידור של הכנסת (GoStreaming) | ✅ רשמי | 2 |
| `636ffd31f0e12.streamlock.net` | 636ffd31f0e12.streamlock.net | ⚠️ ממסר צד שלישי | 2 |
| `rgelive.akamaized.net` | ספורט 5 – השידור הרשמי של RGE (Akamai) | ✅ רשמי | 2 |
| `aegis-cloudfront-1.tubi.video` | aegis-cloudfront-1.tubi.video | ⚠️ ממסר צד שלישי | 2 |
| `ncdn-live-bfm.pfd.sfr.net` | BFM – SFR (רשמי) | ✅ רשמי | 2 |
| `dash3.antik.sk` | Antik Telecom (סלובקיה), נתיבי test | ⚠️ ממסר צד שלישי | 2 |
| `jmp2.uk` | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | 2 |
| `radio.streamgates.net` | StreamGates – ה-CDN של התחנה | ✅ רשמי | 2 |
| `media2.93fm.co.il` | קול חי – שרת התחנה | ✅ רשמי | 2 |
| `icecast.live` | icecast.live | ⚠️ ממסר צד שלישי | 2 |
| `live.ecast.co.il` | eCast – שרת התחנה | ✅ רשמי | 2 |
| `d18b0e6mopany4.cloudfront.net` | רשת 13 – AWS CloudFront | ✅ רשמי | 1 |
| `ynet-live-01.ynet-pic1.yit.co.il` | ynet – השרת של ידיעות אחרונות | ✅ רשמי | 1 |
| `1247634592.rsc.cdn77.org` | שלנו TV – CDN77 | ✅ רשמי | 1 |
| `live.creacast.com` | ILTV – CreaCast (רשמי) | ✅ רשמי | 1 |
| `jlt-jltv-xumo.otteravision.com` | JLTV – Ottera / Xumo FAST | ✅ FAST חינמי | 1 |
| `janya-digimix.akamaized.net` | Angel TV Hebrew – Akamai (רשמי) | ✅ רשמי | 1 |
| `reshet.g-mana.live` | רשת 13 – השרת הרשמי (G-Mana) | ✅ רשמי | 1 |
| `cdn1.cast-tv.com` | cdn1.cast-tv.com | ⚠️ ממסר צד שלישי | 1 |
| `ynet-live-02.ynet-pic1.yit.co.il` | ynet-live-02.ynet-pic1.yit.co.il | ✅ רשמי | 1 |
| `i24newsfrench-cdn.encoders.immergo.tv` | i24NEWS – שרת רשמי | ✅ רשמי | 1 |
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
| `cdn-uw2-prod.tsv2.amagi.tv` | cdn-uw2-prod.tsv2.amagi.tv | ⚠️ ממסר צד שלישי | 1 |
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
| `vodzong.mjunoon.tv` | Cartoon Network Broadcast Stream | ✅ שידור חי | 1 |
| `23.237.104.106` | Boomerang Broadcast | ✅ שידור חי | 1 |
| `s6.hopslan.com` | Disney Channel Stream | ✅ שידור חי | 1 |
| `edge3.uk.kab.tv` | קבלה לעם – שרת רשמי | ✅ רשמי | 1 |
| `cdn.stmify.com` | Sky Sports F1 Playout | ✅ שידור חי | 1 |
| `mvg-mv-xumo.otteravision.com` | mvg-mv-xumo.otteravision.com | ⚠️ ממסר צד שלישי | 1 |
| `a-cdn.klowdtv.com` | a-cdn.klowdtv.com | ⚠️ ממסר צד שלישי | 1 |
| `amg00378-mavtv-amg00378c2-rakuten-us-1048.playouts.now.amagi.tv` | amg00378-mavtv-amg00378c2-rakuten-us-1048.playouts.now.amagi.tv | ⚠️ ממסר צד שלישי | 1 |
| `amg01258-raycomsports-powernationau-samsungau-18bwc.amagi.tv` | amg01258-raycomsports-powernationau-samsungau-18bwc.amagi.tv | ⚠️ ממסר צד שלישי | 1 |
| `ch16israel-cdn.encoders.immergo.tv` | ערוץ 16 – שרת רשמי | ✅ רשמי | 1 |
| `i24newshebrew-cdn.encoders.immergo.tv` | i24NEWS – שרת רשמי | ✅ רשמי | 1 |
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
| `cast.radionahariya.com` | רדיו נהריה – שרת התחנה | ✅ רשמי | 1 |
| `radio.diki-mizrahit.co.il` | רדיו שירי דיכאון – השרת של התחנה | ✅ רשמי | 1 |
| `cdna.streamgates.net` | StreamGates – ה-CDN של התחנה | ✅ רשמי | 1 |
| `play.radioking.io` | play.radioking.io | ⚠️ ממסר צד שלישי | 1 |
| `1062onair.runi.ac.il` | runi.ac.il | ✅ רשמי | 1 |
| `radioangelika.com` | radioangelika.com | ✅ רשמי | 1 |
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
| 9 | ערוץ 9 הישראלי FHD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | iptv-org (Channel9.il) | ✅ |
| 10 | ערוץ כלכלה 10 HD | Redge – ה-CDN הרשמי של כאן, ערוץ 14 וכלכלה 10 | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 11 | כאן 11 FHD | כאן – תאגיד השידור (CDN רשמי) | ✅ רשמי | iptv-org (Kan11.il) | ✅ |
| 12 | קשת 12 FHD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | iptv-org (Keshet12.il) | ✅ |
| 13 | רשת 13 FHD | רשת 13 – AWS CloudFront | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 14 | עכשיו 14 FHD | Redge – ה-CDN הרשמי של כאן, ערוץ 14 וכלכלה 10 | ✅ רשמי | iptv-org (Now14.il) | ✅ |
| 15 | i24NEWS עברית HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | iptv-org (i24NEWSHebrew.il) | ✅ |
| 16 | ערוץ חדשות 16 HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | iptv-org (Channel16.il) | ✅ |
| 24 | ערוץ 24 HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | iptv-org (Channel24.il) | ✅ |
| 98 | ערוץ 98 – האזרח הוותיק HD | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 99 | ערוץ הכנסת 99 HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | iptv-org (KnessetChannel.il) | ✅ |
| 112 | ynet Live | ynet – השרת של ידיעות אחרונות | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 113 | וואלה!+ חדשות – שידור חי HD | live.wcdn.co.il | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 114 | וואלה!+ yes תרבות ובידור HD | live.wcdn.co.il | ✅ רשמי | iptv-org (WallaYes.il) | ✅ |
| 115 | HOT בידור ישראלי HD | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 116 | HOT ריאל HD (HOT Real) | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 117 | רשת 13 קומדיה ובידור HD | ערוץ FAST ישראלי – AWS CloudFront | ✅ FAST חינמי | iptv-org (Reshet13Comedy.il) | ✅ |
| 118 | yes דוקו HD | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 119 | HOT 8 מדע ודוקו HD | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 120 | HOT מיינד דוקו והעשרה HD | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 121 | ערוץ בית+ Home Plus HD | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 122 | ערוץ הבריאות HD | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 123 | ערוץ הטיולים HD | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 124 | ערוץ הבידור E! ישראל HD | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 125 | ערוץ אגו Ego HD | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 126 | ערוץ הריאליטי HD | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 127 | ערוץ ההומור והצחוק HD | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 128 | ערוץ Beautyz אופנה ויופי HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 129 | ערוץ מוזיקה il HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 130 | ערוץ המוזיקה – קשת / FreeTV | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 131 | ערוץ רוקדים עם כוכבים 24/7 HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 132 | כאן 11 – כתוביות ונגישות HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | iptv-org (Kan11Subs.il) | ✅ |
| 133 | קשת 12 – כתוביות ונגישות HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 134 | רשת 13 – כתוביות ונגישות HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | iptv-org (Reshet13Subs.il) | ✅ |
| 135 | עכשיו 14 – כתוביות ונגישות HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 136 | HOT ערוץ החופש HD | ערוץ FAST ישראלי – AWS CloudFront | ✅ FAST חינמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 137 | Shelanu.TV – ערוץ שלנו HD | שלנו TV – CDN77 | ✅ רשמי | סריקת ערוצים מאומתת | ✅ |
| 138 | ILTV ישראל בצרפתית | ILTV – CreaCast (רשמי) | ✅ רשמי | iptv-org (ILTV.fr) | ✅ |
| 139 | JLTV טלוויזיה יהודית HD | JLTV – Ottera / Xumo FAST | ✅ FAST חינמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 140 | Angel TV Hebrew HD | Angel TV Hebrew – Akamai (רשמי) | ✅ רשמי | סריקת ערוצים מאומתת | ✅ |
| 141 | כאן 11 – כתוביות | Redge – ה-CDN הרשמי של כאן, ערוץ 14 וכלכלה 10 | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 142 | רשת 13 – נגיש | רשת 13 – השרת הרשמי (G-Mana) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 143 | ערוץ הכנסת – נגיש | ערוץ הכנסת – השידור של הכנסת (GoStreaming) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 144 | i24NEWS English | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 145 | i24NEWS צרפתית HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | iptv-org (i24NEWSFrench.il) | ✅ |
| 146 | FreeTV לייף סטייל HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 147 | FreeTV דוקו ישראלי HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 148 | ערוץ הקריוקי הישראלי HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 149 | הכותל המערבי – שידור חי 24/7 | cdn1.cast-tv.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 150 | ערוץ הסטייל והאופנה HD | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 151 | רשת 13 ערוץ הנופש HD | ערוץ FAST ישראלי – AWS CloudFront | ✅ FAST חינמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 152 | רשת 13 ערוץ הריאליטי HD | ערוץ FAST ישראלי – AWS CloudFront | ✅ FAST חינמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 153 | ערוץ הכנסת – שפת הסימנים ונגישות | ערוץ הכנסת – השידור של הכנסת (GoStreaming) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 154 | Ynet Live 2 – שידורי חדשות | ynet-live-02.ynet-pic1.yit.co.il | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 155 | הרדיו הראשון 89.1FM – וידאו חי | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 156 | רדיו לב המדינה 91FM – וידאו חי | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 157 | Daystar TV (FreeTV ישראל) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 158 | i24NEWS צרפתית ישראל FHD | i24NEWS – שרת רשמי | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 479 | ערוץ הקומדיה הישראלי HD | ערוץ FAST ישראלי – AWS CloudFront | ✅ FAST חינמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 480 | ערוץ קבלה לעם שידור חי HD | קבלה לעם – שרת רשמי | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 487 | ערוץ 16 ישראל HD | ערוץ 16 – שרת רשמי | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 488 | ערוץ i24NEWS עברית HD | i24NEWS – שרת רשמי | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 489 | האח הגדול שידור חי 24/7 HD | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |

## 📺 ילדים ונוער (בעברית)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 23 | כאן חינוכית 23 FHD | כאן – תאגיד השידור (CDN רשמי) | ✅ רשמי | iptv-org (KanEducational.il) | ✅ |
| 159 | ערוץ הילדים HD (המקום האמיתי) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 160 | ערוץ לוגי HD (Logi) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 161 | ערוץ הופ! ילדים בעברית HD | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 162 | ערוץ הופ! ילדות ישראלית HD | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 163 | ערוץ הופ! פלא HD (Hop Pele) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 164 | ערוץ לולי לפעוטות בעברית HD | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | iptv-org (Luli.il) | ❌ רק באפליקציה (HTTP) |
| 165 | ערוץ דיסני ישראל HD (Disney Channel) | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | iptv-org (DisneyChannel.il) | ❌ רק באפליקציה (HTTP) |
| 166 | ערוץ דיסני ג'וניור ישראל HD (Disney Jr.) | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | iptv-org (DisneyJunior.il) | ❌ רק באפליקציה (HTTP) |
| 167 | ערוץ ניקלודיאון ישראל HD (Nickelodeon) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 168 | ערוץ ניק ג'וניור ישראל HD (Nick Jr.) | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | iptv-org (NickJr.il) | ❌ רק באפליקציה (HTTP) |
| 169 | ערוץ טין ניק ישראל HD (TeenNick) | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | iptv-org (TeenNick.il) | ❌ רק באפליקציה (HTTP) |
| 170 | ערוץ זום ZOOM ילדים ונוער HD | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | iptv-org (Zoom.il) | ❌ רק באפליקציה (HTTP) |
| 171 | ערוץ ג'וניור HD (Junior) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 172 | ערוץ יויו HD (YoYo) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 173 | ערוץ בייבי ישראל HD (BabyTV) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 174 | yes קולנוע ילדים FHD (yes Movies Kids) | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 175 | HOT סינמה פמילי HD (סרטי ילדים ומשפחה) | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 176 | ערוץ WIZ! לילדים ונוער HD | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 177 | ערוץ HOT באזז לנוער HD (HOT Buzz) | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 178 | ערוץ FOMO לנוער ורשת HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 179 | ערוץ ילדותי HD (Yalduti) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 180 | ערוץ שירי ילדותי HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 181 | ערוץ A+ ילדים HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 182 | ערוץ הכוכבים לילדים ונוער HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 186 | ערוץ ניק ג'וניור פלוס בעברית HD (Nick Jr.+) | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |

## 📺 ערוצי פרטנר TV (Partner TV)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 100 | ערוץ פרטנר TV – הומור וסטנדאפ HD | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 101 | ערוץ פרטנר TV – בית ועיצוב HD | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 102 | ערוץ פרטנר TV – ריאליטי ישראלי HD | ערוץ FAST ישראלי – AWS CloudFront | ✅ FAST חינמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 103 | ערוץ פרטנר TV – הדרמות הטורקיות HD | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 104 | ערוץ פרטנר TV – החיים הטובים HD | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 105 | ערוץ פרטנר TV – עולם הילדים HD | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 106 | ערוץ פרטנר TV – לולי לגיל הרך HD | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 107 | ערוץ פרטנר TV – השעשועונים והמשחקים HD | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 108 | ערוץ פרטנר TV – הנופש והחופש HD | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 109 | ערוץ פרטנר TV – ויוה טלנובלות HD | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 110 | ערוץ פרטנר TV – ג'וניור פלוס HD | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 111 | ערוץ פרטנר TV – עיצוב ולייף סטייל HD | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |

## 📺 ספורט (Sports)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 187 | ספורט 1 HD (Sport 1) | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 188 | ספורט 2 HD (Sport 2) | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 189 | ספורט 3 HD (Sport 3) | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 190 | ספורט 4 FHD (Sport 4) | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 191 | ספורט 6 HD (Sport 6) | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 192 | ספורט 5 FHD (5SPORT) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 193 | ספורט 5 פלוס FHD (5PLUS) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 194 | ספורט 5 לייב FHD (5LIVE) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 195 | ספורט 5 גולד HD (5GOLD) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 196 | ספורט 5 סטארס FHD (5STARS) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 197 | ספורט 5 מקס FHD (5MAX) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 198 | ספורט 5 4K UHD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 199 | ערוץ ONE 1 FHD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 200 | ערוץ ONE 2 FHD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 201 | ערוץ ONE דוקו HD (ONE DOCO) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 202 | ערוץ ONE Edge HD (אדג' ספורט) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 203 | יורוספורט 2 HD (Eurosport 2) | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 204 | Sky Racing 1 HD (סקיי רייסינג ספורט) | 636ffd31f0e12.streamlock.net | ⚠️ ממסר צד שלישי | שידור רשמי Sky | ✅ |
| 205 | ספורט 5 – שידור חי | ספורט 5 – השידור הרשמי של RGE (Akamai) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 206 | יורוספורט 1 ישראל HD (Eurosport 1) | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 207 | Red Bull TV Extreme Sports FHD | Red Bull TV – Akamai (רשמי) | ✅ רשמי | סריקת רשת v4.6.0 | ✅ |
| 466 | beIN SPORTS XTRA FHD | bein-xtra-bein.amagi.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 467 | PGA Tour Golf Live FHD | d11k1mnrgfposz.cloudfront.net | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 473 | CBS Sports HQ FHD | dai.google.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 481 | Sky Sports F1 HD (פורמולה 1 שידור חי) | Sky Sports F1 Playout | ✅ שידור חי | רשימה ידנית (גרסה קודמת) | ✅ |
| 482 | מוטורוויז'ן Motorvision HD | mvg-mv-xumo.otteravision.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 483 | ערוץ הרכב Cars.TV HD | a-cdn.klowdtv.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 484 | ערוץ מרוצים RACER International HD | amg00378-mavtv-amg00378c2-rakuten-us-1048.playouts.now.amagi.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 485 | ערוץ PowerNation TV מנועים ומוטורספורט | amg01258-raycomsports-powernationau-samsungau-18bwc.amagi.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 486 | ערוץ מרוצים Sky Racing 1 HD | 636ffd31f0e12.streamlock.net | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |

## 📺 סרטים וסדרות (Movies & Series)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 208 | HOT 3 ערוץ הדגל FHD | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | iptv-org (Hot3.il) | ❌ רק באפליקציה (HTTP) |
| 209 | HOT HBO סדרות פרימיום HD | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | iptv-org (HotHBO.il) | ❌ רק באפליקציה (HTTP) |
| 210 | HOT סינמה 1 FHD | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 211 | HOT סינמה 2 HD | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 212 | HOT סינמה 3 FHD | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 213 | HOT סינמה 4 HD | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | iptv-org (HotCinema4.il) | ❌ רק באפליקציה (HTTP) |
| 214 | HOT סינמה 5 HD | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 215 | HOT קולנוע ישראלי HD | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 216 | HOT קולנוע רומנטי HD | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 217 | HOT דרמה HD | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 218 | HOT זון HD (HOT Zone) | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 219 | HOT קומדי HD (HOT Comedy) | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 220 | yes קולנוע ישראלי HD | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 221 | yes קולנוע אקשן FHD (yes Movies Action) | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 222 | yes קולנוע דרמה FHD (yes Movies Drama) | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 223 | yes קולנוע קומדיה FHD (yes Movies Comedy) | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 224 | yes TV אקשן HD (yes TV Action) | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 225 | yes TV דרמה HD (yes TV Drama) | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 226 | yes TV קומדיה HD (yes TV Comedy) | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | iptv-org (YesTvComedy.il) | ❌ רק באפליקציה (HTTP) |
| 227 | ערוץ ויוה טלנובלות HD (Viva) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 228 | ערוץ ויוה איסטנבול HD (Viva Istanbul) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 229 | ערוץ ויוה פרימיום HD (Viva Premium) | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 230 | הערוץ הים תיכוני HD | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 231 | הערוץ הים תיכוני+ HD | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 232 | ערוץ הדרמות הטורקיות+ FHD | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 233 | ערוץ הדרמות הטורקיות 2 FHD | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 234 | ערוץ הדרמות הטורקיות 3 FHD | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 235 | ערוץ הסרטים הים תיכוני 1 HD | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 236 | ערוץ הסרטים הים תיכוני 2 HD | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 237 | ערוץ האקשן העולמי 1 HD | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 238 | ערוץ האקשן העולמי 2 HD | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 239 | ערוץ הדרמות הספרדיות 1 HD | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 240 | ערוץ הדרמות הספרדיות 2 HD | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 241 | ערוץ הדרמות ההודיות 1 HD | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 242 | ערוץ הדרמות ההודיות 2 HD | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 243 | ערוץ בוליווד סרטים הודיים HD | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 244 | ערוץ בוליווד קלאסי HD | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 245 | ערוץ בולישואו Bollyshow HD | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 246 | ערוץ ZEE TV ישראל HD | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | iptv-org (ZeeTV.il) | ❌ רק באפליקציה (HTTP) |
| 247 | ערוץ GTV דרמות וקולנוע HD | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | iptv-org (GTV.il) | ❌ רק באפליקציה (HTTP) |
| 248 | ערוץ הקומדיות הקלאסיות HD | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 249 | ערוץ האהבה והרומנטיקה HD | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 250 | ערוץ ארץ נהדרת 24/7 | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 251 | סברי מרנן 24/7 – קשת / FreeTV | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 252 | ערוץ הדרמה – קשת / FreeTV | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 253 | ערוץ הקומדיה – קשת / FreeTV | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 254 | FreeTV סרטים ישראלי HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 255 | FreeTV סרטים פעולה HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 256 | FreeTV סרטים קומדיה HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 257 | FreeTV סרטים דרמה HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 258 | FreeTV סרטים מתח HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 259 | FreeTV סרטים אימה HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 260 | FreeTV סרטים רומנטי HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 261 | FreeTV סרטים פיל גוד HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 262 | FreeTV סרטים משפחה HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 263 | FreeTV 10 Stars סרטים HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 264 | FreeTV סדרות קומדיה 2 HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 265 | ערוץ הדרמות היומיות HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 266 | ערוץ וואמוס HD (Vamos) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 267 | FreeTV גלובל סדרות HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 268 | Pluto TV Sci-Fi FHD | CDN רשמי (HTTPS) | ✅ FAST חינמי | סריקת רשת v4.6.0 | ✅ |
| 465 | Pluto TV Classic Movies FHD | service-stitcher.clusters.pluto.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |

## 📺 אוכל ובישול (Food)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 269 | ערוץ האוכל Food Network HD | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 270 | ערוץ פודי Foody HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 271 | ערוץ האוכל והבישול – קשת / FreeTV | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 272 | Bon Appétit HD – ערוץ הבישול בון אפטיט | bonappetit-samsung.amagi.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 273 | Gusto TV FHD – ערוץ האוכל העולמי גוסטו | e75fc053c0d2406fb74cdd71d77e1a7f.mediatailor.us-east-1.amazonaws.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 274 | Gordon Ramsay's Hell's Kitchen 24/7 HD | aegis-cloudfront-1.tubi.video | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 275 | Food Network International HD | streaming.aurora.enhanced.live | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 276 | TV5MONDE Chefs HD – שפים וגסטרונומיה | tvf-tv5ch.otteravision.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 277 | Cooking Panda HD – מתכונים ובישול | stream-us-east-1.getpublica.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 278 | Kus-Kus Culinary TV HD – ערוץ קוסקוס אוכל ומתכונים | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 279 | America's Test Kitchen FHD | CDN רשמי (HTTPS) | ✅ FAST חינמי | סריקת רשת v4.6.0 | ✅ |
| 280 | Jamie Oliver Channel FHD | CDN רשמי (HTTPS) | ✅ FAST חינמי | סריקת רשת v4.6.0 | ✅ |
| 469 | מטבחי הגיהנום 24/7 — Hell's Kitchen FHD | service-stitcher.clusters.pluto.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |

## 📺 חדשות מהעולם (News)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 281 | i24NEWS English World HD | i24NEWS – CDN Akamai של השידור הרשמי | ✅ רשמי | סריקת ערוצים מאומתת | ✅ |
| 282 | Sky News UK FHD | linear901-oo-hls0-prd-gtm.delivery.skycdp.com | ⚠️ ממסר צד שלישי | שידור רשמי Sky | ✅ |
| 283 | Sky News Australia Extra 1 HD | skynewsau-live.akamaized.net | ⚠️ ממסר צד שלישי | שידור רשמי Sky | ✅ |
| 284 | Sky News Australia Extra 2 HD | skynewsau-live.akamaized.net | ⚠️ ממסר צד שלישי | שידור רשמי Sky | ✅ |
| 285 | Sky News Australia Extra 3 HD | skynewsau-live.akamaized.net | ⚠️ ממסר צד שלישי | שידור רשמי Sky | ✅ |
| 286 | DW News English HD | DW – Akamai (רשמי) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 287 | BFM TV HD (France) | BFM – SFR (רשמי) | ✅ רשמי | iptv-org (BFMTV.fr) | ✅ |
| 288 | BFM Tech & Co HD | BFM – SFR (רשמי) | ✅ רשמי | iptv-org (BFMTechCo.fr) | ✅ |
| 289 | France 24 (FreeTV ישראל) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 290 | BBC News World FHD | vs-hls-push-ww-live.akamaized.net | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 291 | CNN International HD | turnerlive.warnermediacdn.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 292 | Fox News Channel HD | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 293 | FOX LiveNOW News HD | cdn-uw2-prod.tsv2.amagi.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 294 | CBS News 24/7 FHD | cbsn-us.cbsnstream.cbsnews.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 295 | Bloomberg TV+ Business FHD | www.bloomberg.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 296 | Scripps News USA FHD | aegis-cloudfront-1.tubi.video | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 297 | Newsmax TV HD | amg00217-newsmax-newsmax-zeasn-022k9.amagi.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 298 | TRT World News FHD | tv-trtworld.medya.trt.com.tr | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 299 | CGTN News English FHD | english-livetx.cgtn.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 300 | CNBC Business Live HD | CDN רשמי (HTTPS) | ✅ FAST חינמי | סריקת רשת v4.6.0 | ✅ |

## 📺 טבע ודוקו (Documentary & Nature)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 301 | נשיונל ג'יאוגרפיק ישראל HD (Nat Geo) | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | iptv-org (NationalGeographic.il) | ❌ רק באפליקציה (HTTP) |
| 302 | נשיונל ג'יאוגרפיק וויילד FHD (Nat Geo Wild) | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | iptv-org (NationalGeographicWild.il) | ❌ רק באפליקציה (HTTP) |
| 303 | ערוץ דיסקברי ישראל FHD (Discovery) | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | iptv-org (DiscoveryChannel.il) | ❌ רק באפליקציה (HTTP) |
| 304 | ערוץ ההיסטוריה ישראל FHD (History) | פרטנר TV וסלקום – שרתי שידור ישראליים | ✅ רשמי | iptv-org (History.il) | ❌ רק באפליקציה (HTTP) |
| 305 | ערוץ הטבע הישראלי HD (Teva) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 306 | Pluto TV History FHD | CDN רשמי (HTTPS) | ✅ FAST חינמי | סריקת רשת v4.6.0 | ✅ |
| 307 | Pluto TV Animals & Nature FHD | CDN רשמי (HTTPS) | ✅ FAST חינמי | סריקת רשת v4.6.0 | ✅ |
| 470 | Smithsonian Channel Selects FHD | service-stitcher.clusters.pluto.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 471 | Pluto TV Science & Space FHD | service-stitcher.clusters.pluto.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |

## 📺 מוזיקה (Music TV)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 308 | רדיוס 100FM – אולפן וידאו HD | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 309 | Sky Folk TV HD (סקיי פולק) | Sky Folk Live | ✅ רשמי | שידור רשמי Sky | ✅ |
| 310 | 4ever Music Hits HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (4everMusic.ua) | ❌ רק באפליקציה (HTTP) |
| 311 | 4Fun TV Pop Hits HD | 4FUN TV – שרת רשמי | ✅ רשמי | iptv-org (4FunTV.pl) | ✅ |
| 312 | ¡Viva Latino! Dance HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (VivaLatino.ua) | ❌ רק באפליקציה (HTTP) |
| 313 | 1HD Music Television | שרת IP ללא שם (1HD) | ⚠️ ממסר צד שלישי | iptv-org (1HDMusicTelevision.ru) | ❌ רק באפליקציה (HTTP) |
| 472 | Vevo Pop Music Hits FHD | amg00056-amg00056c6-rakuten-uk-3235.playouts.now.amagi.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |

## 📺 מסורת וקהילה

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 314 | ערוץ הידברות 97 HD | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | iptv-org (Hidabroot.il) | ✅ |
| 315 | ערוץ קבלה לעם 66 HD | edge1.il.kab.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 316 | קבלה לעם – צרפתית HD | edge1.il.kab.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 317 | קבלה לעם – גרמנית HD | edge1.il.kab.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 318 | קבלה לעם – איטלקית HD | edge1.il.kab.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 319 | Jai TV – טלוויזיה יהודית וישראלית | vivo.solumedia.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |

## 📺 ילדים מהעולם (Kids World)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 453 | Baby Shark TV (בייבי שארק לילדים) | Baby Shark TV – Wurl (Roku FAST) | ✅ FAST חינמי | סריקת ערוצי ילדים | ✅ |
| 454 | ערוץ בייבי BabyTV Global HD | BabyFirst – JW Player FAST | ✅ FAST חינמי | סריקת ערוצי ילדים | ✅ |
| 455 | ערוץ זו-מו לילדים (ZooMoo HD) | amagi.tv | ✅ FAST חינמי | סריקת ערוצי ילדים | ✅ |
| 456 | ערוץ אני אנימציה HD (Ani Kids) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | סריקת ערוצי ילדים | ❌ רק באפליקציה (HTTP) |
| 457 | ערוץ האנימציה PLUSPLUS HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | סריקת ערוצי ילדים | ❌ רק באפליקציה (HTTP) |
| 458 | ערוץ ניקי ג'וניור HD (Niki Junior) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | סריקת ערוצי ילדים | ❌ רק באפליקציה (HTTP) |
| 459 | ערוץ סרטוני אנימציה קצרים (Cartoons Short) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | סריקת ערוצי ילדים | ❌ רק באפליקציה (HTTP) |
| 460 | ערוץ סרטי אנימציה לילדים (Cartoons Cinema) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | סריקת ערוצי ילדים | ❌ רק באפליקציה (HTTP) |
| 461 | פרש קידס (Fresh Kids TV HD) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | סריקת ערוצי ילדים | ❌ רק באפליקציה (HTTP) |
| 462 | דאק טי וי פלוס ducktv PLUS HD | Antik Telecom (סלובקיה), נתיבי test | ⚠️ ממסר צד שלישי | iptv-org (ducktvPLUS.sk) | ✅ |
| 463 | ערוץ לגו Lego Channel HD | LEGO Channel – AWS CloudFront FAST | ✅ FAST חינמי | iptv-org (LegoChannel.us) | ✅ |
| 464 | דאק טי וי ducktv HD | Antik Telecom (סלובקיה), נתיבי test | ⚠️ ממסר צד שלישי | iptv-org (ducktv.sk) | ✅ |
| 468 | רובוטריקים 24/7 — Transformers TV FHD | service-stitcher.clusters.pluto.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 474 | Cartoon Network HD (קארטון נטוורק) | Cartoon Network Broadcast Stream | ✅ שידור חי | רשימה ידנית (גרסה קודמת) | ✅ |
| 475 | Boomerang HD (בומרנג) | Boomerang Broadcast | ✅ שידור חי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 476 | Disney Channel Global HD | Disney Channel Stream | ✅ שידור חי | רשימה ידנית (גרסה קודמת) | ✅ |
| 477 | Crunchyroll Anime HD | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 478 | PBS Kids HD | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | רשימה ידנית (גרסה קודמת) | ✅ |

## 📻 רדיו

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 183 | רדיוס 100% שירי דיסני | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 184 | רדיוס 100% ילדים | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 185 | רדיוס 100% ערוץ הכוכבים | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 320 | ברסלב: הלכות ודף יומי | רדיו ברסלב – שרת התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 321 | ברסלב: ספרי ברסלב | רדיו ברסלב – שרת התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 322 | ברסלב: קצר וקולע | רדיו ברסלב – שרת התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 323 | ברסלב: תורה וזימרה | רדיו ברסלב – שרת התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 324 | כאן ב - רשת ב | Triton StreamTheWorld – מפיץ הרדיו של כאן | ✅ רשמי | radio-browser.info + בדיקת שמות הזרמים של כאן | ✅ |
| 325 | כאן גימל | Triton StreamTheWorld – מפיץ הרדיו של כאן | ✅ רשמי | radio-browser.info + בדיקת שמות הזרמים של כאן | ✅ |
| 326 | כאן 88 | Triton StreamTheWorld – מפיץ הרדיו של כאן | ✅ רשמי | radio-browser.info + בדיקת שמות הזרמים של כאן | ✅ |
| 327 | כאן תרבות | Triton StreamTheWorld – מפיץ הרדיו של כאן | ✅ רשמי | radio-browser.info + בדיקת שמות הזרמים של כאן | ✅ |
| 328 | כאן מורשת | Triton StreamTheWorld – מפיץ הרדיו של כאן | ✅ רשמי | radio-browser.info + בדיקת שמות הזרמים של כאן | ✅ |
| 329 | כאן רקע | Triton StreamTheWorld – מפיץ הרדיו של כאן | ✅ רשמי | radio-browser.info + בדיקת שמות הזרמים של כאן | ✅ |
| 330 | כאן קול המוזיקה | Triton StreamTheWorld – מפיץ הרדיו של כאן | ✅ רשמי | radio-browser.info + בדיקת שמות הזרמים של כאן | ✅ |
| 331 | גלגלצ | גלי צה"ל / גלגלצ – CDN רשמי (Bynet) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 332 | גלי צה"ל | גלי צה"ל / גלגלצ – CDN רשמי (Bynet) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 333 | גלגלצ להיטים | גלי צה"ל / גלגלצ – CDN רשמי (Bynet) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 334 | eco99fm | eco99fm – Mediacast (רשמי) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 335 | 103FM | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 336 | רדיוס 100FM | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 337 | לב המדינה 91FM | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 338 | 102FM רדיו תל אביב | 102FM – LiveCDN (רשמי) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 339 | רדיו חיפה 107.5FM | רדיו חיפה – LiveCDN (רשמי) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 340 | 101FM רדיו ירושלים | StreamGates – ה-CDN של התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 341 | 90FM אמצע הדרך | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 342 | רדיו דרום 101.5FM | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 343 | רדיו דרום 97FM | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 344 | 104.5FM רדיו צפון | StreamGates – ה-CDN של התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 345 | קול רגע 96FM | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 346 | 102FM קול הים האדום | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 347 | גלי ישראל 94FM | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 348 | קול חי 93FM | קול חי – שרת התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 349 | קול חי מיוזיק | קול חי – שרת התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 350 | קול ברמה | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 351 | קול ברמה מוזיקה (קול פליי) | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 352 | רדיו 2000 | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 353 | רדיו ברסלב | רדיו ברסלב – שרת התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 354 | מזרחית FM | rs.mizrahit.fm | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 355 | נושמים מזרחית | www.rs.mizrahit.fm | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 356 | רדיו עברי 6 (מוזיקה ישראלית) | streaming.radio.co | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 357 | רדיו הקצה KZ | רדיו הקצה – Mediacast (רשמי) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 358 | רדיו ספורט 5 | ספורט 5 – השידור הרשמי של RGE (Akamai) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 359 | 99.5FM רדיו | 99.5FM – LiveCDN (רשמי) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 360 | AvivMedia טראנס | 176.9.7.145 | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 361 | Israeli Voice Radio | icecast.live | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 362 | Radio France Israël | stream10.vestaradio.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 363 | TIMBR רדיו | radio.betteradio.live | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 364 | Up2Dance רדיו | icecast.live | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 365 | yStream | ystream.live | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 366 | ג'וינט רדיו בלוז רוק | jointil.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 367 | ג'וינט רדיו טראנס | jointil.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 368 | ג'וינט רדיו רגאיי | jointil.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 369 | גלגלצ עשורים | גלי צה"ל / גלגלצ – CDN רשמי (Bynet) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 370 | גלגלצ רגוע | גלי צה"ל / גלגלצ – CDN רשמי (Bynet) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 371 | גלגלצ רוק | גלי צה"ל / גלגלצ – CDN רשמי (Bynet) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 372 | סטאר 33 רדיו | online.star33radio.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 373 | קבלה לעם רדיו | icecast.kab.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 374 | קבלה לעם רדיו – אנגלית | icecast.kab.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 375 | קבלה לעם רדיו – ספרדית | icecast.kab.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 376 | קבלה לעם רדיו – צרפתית | icecast.kab.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 377 | קול הצפון | eCast – שרת התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 378 | רדיו CS קלאסי | n0a.radiojar.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 379 | רדיו אהבה – רומנטי | liveradio.co.il | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 380 | רדיו התחנה 101.5 | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 381 | רדיו נהריה | רדיו נהריה – שרת התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 382 | רדיו סהר | eCast – שרת התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 383 | רדיו שירי דיכאון | רדיו שירי דיכאון – השרת של התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 384 | רדיוס 100% K-Pop | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 385 | רדיוס 100% אגדות | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 386 | רדיוס 100% אהבה | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 387 | רדיוס 100% אולדיז | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 388 | רדיוס 100% אימון | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 389 | רדיוס 100% אירוויזיון | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 390 | רדיוס 100% בלוז | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 391 | רדיוס 100% בריטפופ | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 392 | רדיוס 100% ג'אז | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 393 | רדיוס 100% גיימר | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 394 | רדיוס 100% טופ 40 | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 395 | רדיוס 100% טיקטוק | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 396 | רדיוס 100% יהודי | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 397 | רדיוס 100% להיטים | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 398 | רדיוס 100% לטינו | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 399 | רדיוס 100% מוזיקת עולם | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 400 | רדיוס 100% מזרחית | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 401 | רדיוס 100% סטנדאפ | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 402 | רדיוס 100% סלפי | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 403 | רדיוס 100% סרטים | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 404 | רדיוס 100% צ'ילאאוט | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 405 | רדיוס 100% קאברים | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 406 | רדיוס 100% קאנטרי | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 407 | רדיוס 100% קלאב | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 408 | רדיוס 100% קלאסית | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 409 | רדיוס 100% רגוע | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 410 | רדיוס 100% רוק | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 411 | רדיוס 100% רוק קלאסי | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 412 | רדיוס 100% רטרו | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 413 | רדיוס 100% שנות ה-80 | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 414 | רדיוס 100% שנות ה-90 | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 415 | רדיוס נוסטלגי 96.3 | StreamGates – ה-CDN של התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 416 | רק מוזיקה (Rak Musica) | play.radioking.io | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 417 | AudioVersity רדיו אוניברסיטת רייכמן | runi.ac.il | ✅ רשמי | סריקת ערוצים מאומתת | ✅ |
| 418 | Radio Angelika תל אביב | radioangelika.com | ✅ רשמי | סריקת ערוצים מאומתת | ✅ |
| 419 | רדיו קווליטה (Radio Qualita) | RadioKing – נגן השידור של Studio Qualita | ✅ רשמי | זרם Radio Qualita; זהות התחנה אומתה באתר qualita.org.il | ✅ |
| 420 | Radio Plus ישראל | YesStreaming – זרם הנגן באתר Radio Plus | ✅ רשמי | נגן Radio Plus באתר radioplus.co.il | ✅ |
| 421 | Jewish Music Stream (מוזיקה יהודית) | stream.jewishmusicstream.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 422 | רדיוס 100% שלמה ארצי | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 423 | רדיוס 100% ישראלי | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 424 | רדיוס 100% חפלה | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 425 | רדיוס 100% ים תיכוני לוהט | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 426 | רדיוס 100% נוסטלגיה | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 427 | רדיוס 100FM – עושים היסטוריה | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 428 | רדיוס 100% שנות ה-70 | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 429 | רדיוס 100% שנות ה-2000 | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 430 | רדיוס 100% טראנס | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 431 | רדיוס 100% דאנס | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 432 | רדיוס 100% היפ הופ | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 433 | רדיוס 100% דיפ (Deep) | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 434 | רדיוס 100% סט די-ג'יי | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 435 | רדיוס 100% קריוקי | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 436 | רדיוס 100% קפה (Café) | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 437 | רדיוס 100% דרייב טיים | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 438 | רדיוס 100% מקום ראשון (No.1) | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 439 | רדיוס 100% הופעות חיות | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 440 | רדיוס 100% יוונית | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 441 | רדיוס 100% איטלקית | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 442 | רדיוס 100% צרפתית | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 443 | רדיוס 100FM – תומר מייזנר | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 444 | רדיוס 100FM – VIP Digital | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 445 | רדיוס 100FM – בית לוינשטיין | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 446 | רדיוס 100FM – התקליטייה | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 447 | רדיוס 100FM – מבזק חדשות 12 | ff_engine.streamgates.net | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 448 | כאן חינוכית – רדיו ילדים | כאן – תאגיד השידור (CDN רשמי) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 449 | רדיו חב"ד – מוזיקה יהודית | stream.radio.co | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 450 | רדיו חב"ד – שיעורי תורה | streams.radio.co | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 451 | קול רדיו (Kol Radio) | uksoutha.streaming.broadcast.radio | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 452 | Imsostablee Radio ישראל | c44.radioboss.fm | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |

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

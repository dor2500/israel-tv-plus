# מקורות – מאיפה מגיע כל ערוץ

נוצר אוטומטית מ-`channels.json` על ידי `tools/make_sources.py`. סה"כ **489** פריטים: 353 ערוצי טלוויזיה, 136 תחנות רדיו, 0 אפליקציות/שירותים.

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

- **233 שידורים רשמיים** – מהשרתים של הגוף המשדר עצמו או של המפיץ שלו (כאן, 13, 14, i24NEWS, גלי צה"ל, תחנות הרדיו, DW, אל-ג'זירה...).
- **104 ערוצי FAST** – ערוצים חינמיים עם פרסומות שהבעלים מפיצים בעצמם (Pluto TV, BabyFirst, LEGO, Baby Shark...).
- **0 ערוצים מהעולם** – שידורים פתוחים מהמאגר הציבורי iptv-org (ייבוא אוטומטי, `tools/import_iptv.py`): כל אחד נבדק שהוא משדר ושיש בו תמונה, ללא ערוצים שדורשים מנוי/כתובת מיוחדת. רובם מהשרתים של הערוצים עצמם, אבל הבעלים לא נבדקו אחד-אחד.
- **150 ממסרי צד שלישי** – בעיקר `stream.mcquack.net` (0 ערוצים). אלה **אינם** השרתים של הערוצים: אתר חיצוני קולט את השידור ומפיץ אותו מחדש. בהם נמצאים גם ערוצים בתשלום (HOT, yes, viju/TV1000, ערוצי ספורט) – סביר מאוד שההפצה הזו **ללא רישיון** מבעלי הזכויות, והם עלולים להיעלם בכל רגע. ההחלטה אם להשאיר אותם היא שלך; הסקריפטים לא מוסיפים ממסרים חדשים של ערוצים בתשלום.
- **שירותי המנויים** (פרטנר, סלקום, STING, yes, HOT) – אין בפרויקט שום שידור שלהם; הכרטיס רק פותח את האפליקציה/האתר הרשמי, וצפייה דורשת מנוי.

> הסיווג רשמי / FAST / ממסר נקבע לפי שם השרת שממנו מגיע השידור (למשל CDN של הגוף המשדר), ולא אושר מול כל גוף משדר בנפרד.

## לפי שרת

| שרת | מי זה | סוג | מספר |
|---|---|---|---|
| `osoyz0xv.ottclub.xyz` | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | 87 |
| `d1zqtf09wb8nt5.cloudfront.net` | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | 69 |
| `gb25.streamgates.net` | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | 60 |
| `jmp2.uk` | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | 39 |
| `cdn.cybercdn.live` | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | 18 |
| `r.il.cdn-redge.media` | Redge – ה-CDN הרשמי של כאן, ערוץ 14 וכלכלה 10 | ✅ רשמי | 16 |
| `playerservices.streamtheworld.com` | Triton StreamTheWorld – מפיץ הרדיו של כאן | ✅ רשמי | 7 |
| `glzwizzlv.bynetcdn.com` | גלי צה"ל / גלגלצ – CDN רשמי (Bynet) | ✅ רשמי | 6 |
| `edge1.il.kab.tv` | edge1.il.kab.tv | ⚠️ ממסר צד שלישי | 5 |
| `cast.breslevforyou.co.il` | רדיו ברסלב – שרת התחנה | ✅ רשמי | 5 |
| `cdna.streamgates.net` | StreamGates – ה-CDN של התחנה | ✅ רשמי | 4 |
| `d1yd8hohnldm33.cloudfront.net` | ערוץ FAST ישראלי – AWS CloudFront | ✅ FAST חינמי | 3 |
| `skynewsau-live.akamaized.net` | skynewsau-live.akamaized.net | ⚠️ ממסר צד שלישי | 3 |
| `jointil.com` | jointil.com | ⚠️ ממסר צד שלישי | 3 |
| `live.wcdn.co.il` | live.wcdn.co.il | ✅ רשמי | 2 |
| `d15ds134q59udk.cloudfront.net` | ערוץ FAST ישראלי – AWS CloudFront | ✅ FAST חינמי | 2 |
| `kancdn.medonecdn.net` | כאן – תאגיד השידור (CDN רשמי) | ✅ רשמי | 2 |
| `kneset.gostreaming.tv` | ערוץ הכנסת – השידור של הכנסת (GoStreaming) | ✅ רשמי | 2 |
| `d2dffl3588mvfk.cloudfront.net` | ערוץ FAST ישראלי – AWS CloudFront | ✅ FAST חינמי | 2 |
| `636ffd31f0e12.streamlock.net` | 636ffd31f0e12.streamlock.net | ⚠️ ממסר צד שלישי | 2 |
| `rgelive.akamaized.net` | ספורט 5 – השידור הרשמי של RGE (Akamai) | ✅ רשמי | 2 |
| `stream-us-east-1.getpublica.com` | stream-us-east-1.getpublica.com | ⚠️ ממסר צד שלישי | 2 |
| `dash3.antik.sk` | Antik Telecom (סלובקיה), נתיבי test | ⚠️ ממסר צד שלישי | 2 |
| `edge3.uk.kab.tv` | קבלה לעם – שרת רשמי | ✅ רשמי | 2 |
| `mvg-mv-xumo.otteravision.com` | mvg-mv-xumo.otteravision.com | ⚠️ ממסר צד שלישי | 2 |
| `lotus.stingray.com` | FAST Public CDN | ✅ FAST חינמי | 2 |
| `30a-tv.com` | FAST Public CDN | ✅ FAST חינמי | 2 |
| `a-cdn.klowdtv.com` | FAST Public CDN | ✅ FAST חינמי | 2 |
| `bloomberg.com` | FAST Public CDN | ✅ FAST חינמי | 2 |
| `radio.streamgates.net` | StreamGates – ה-CDN של התחנה | ✅ רשמי | 2 |
| `media2.93fm.co.il` | קול חי – שרת התחנה | ✅ רשמי | 2 |
| `icecast.live` | icecast.live | ⚠️ ממסר צד שלישי | 2 |
| `live.ecast.co.il` | eCast – שרת התחנה | ✅ רשמי | 2 |
| `radioangelika.com` | radioangelika.com | ✅ רשמי | 2 |
| `ynet-live-01.ynet-pic1.yit.co.il` | ynet – השרת של ידיעות אחרונות | ✅ רשמי | 1 |
| `88.212.15.50` | 88.212.15.50 | ⚠️ ממסר צד שלישי | 1 |
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
| `i24newsenglish-cdn.encoders.immergo.tv` | i24NEWS – שרת רשמי | ✅ רשמי | 1 |
| `linear901-oo-hls0-prd-gtm.delivery.skycdp.com` | linear901-oo-hls0-prd-gtm.delivery.skycdp.com | ⚠️ ממסר צד שלישי | 1 |
| `dwamdstream102.akamaized.net` | DW – Akamai (רשמי) | ✅ רשמי | 1 |
| `live-cdn-stream-euw1.bfmtv.bct.nextradiotv.com` | live-cdn-stream-euw1.bfmtv.bct.nextradiotv.com | ⚠️ ממסר צד שלישי | 1 |
| `ncdn-live-bfm.pfd.sfr.net` | BFM – SFR (רשמי) | ✅ רשמי | 1 |
| `vs-hls-push-ww-live.akamaized.net` | vs-hls-push-ww-live.akamaized.net | ⚠️ ממסר צד שלישי | 1 |
| `cdn-uw2-prod.tsv2.amagi.tv` | cdn-uw2-prod.tsv2.amagi.tv | ⚠️ ממסר צד שלישי | 1 |
| `cbsn-us.cbsnstream.cbsnews.com` | cbsn-us.cbsnstream.cbsnews.com | ⚠️ ממסר צד שלישי | 1 |
| `www.bloomberg.com` | www.bloomberg.com | ⚠️ ממסר צד שלישי | 1 |
| `amg00217-newsmax-newsmax-zeasn-022k9.amagi.tv` | amg00217-newsmax-newsmax-zeasn-022k9.amagi.tv | ⚠️ ממסר צד שלישי | 1 |
| `tv-trtworld.medya.trt.com.tr` | tv-trtworld.medya.trt.com.tr | ⚠️ ממסר צד שלישי | 1 |
| `english-livetx.cgtn.com` | english-livetx.cgtn.com | ⚠️ ממסר צד שלישי | 1 |
| `skyfolk.mk` | Sky Folk Live | ✅ רשמי | 1 |
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
| `s6.hopslan.com` | Disney Channel Stream | ✅ שידור חי | 1 |
| `amg00378-mavtv-amg00378c2-rakuten-us-1048.playouts.now.amagi.tv` | amg00378-mavtv-amg00378c2-rakuten-us-1048.playouts.now.amagi.tv | ⚠️ ממסר צד שלישי | 1 |
| `amg01258-raycomsports-powernationau-samsungau-18bwc.amagi.tv` | amg01258-raycomsports-powernationau-samsungau-18bwc.amagi.tv | ⚠️ ממסר צד שלישי | 1 |
| `ch16israel-cdn.encoders.immergo.tv` | ערוץ 16 – שרת רשמי | ✅ רשמי | 1 |
| `i24newshebrew-cdn.encoders.immergo.tv` | i24NEWS – שרת רשמי | ✅ רשמי | 1 |
| `ma.anixa.tv` | ma.anixa.tv | ⚠️ ממסר צד שלישי | 1 |
| `stream.panet.com` | פאנט – הלא TV (רשמי) | ✅ רשמי | 1 |
| `contact.gostreaming.tv` | GoStreaming – ספק השידור של ערוץ 9 | ✅ רשמי | 1 |
| `i24newsarabic-cdn.encoders.immergo.tv` | i24NEWS – שרת רשמי | ✅ רשמי | 1 |
| `r.eu.cdn-redge.media` | r.eu.cdn-redge.media | ⚠️ ממסר צד שלישי | 1 |
| `moonbug-rokuus.amagi.tv` | moonbug-rokuus.amagi.tv | ⚠️ ממסר צד שלישי | 1 |
| `stream.ads.ottera.tv` | stream.ads.ottera.tv | ⚠️ ממסר צד שלישי | 1 |
| `amg00793-amg00793c6-vizio-us-2670.playouts.now.amagi.tv` | FAST Official | ✅ FAST חינמי | 1 |
| `lds-timeline-rakuten.amagi.tv` | FAST Official | ✅ FAST חינמי | 1 |
| `bamusa-naturetime-emea-eng-rakuten.amagi.tv` | FAST Official | ✅ FAST חינמי | 1 |
| `amg01821-lovetvchannels-lovetheplanetuksamsung-samsunguk-apopw.amagi.tv` | FAST Official | ✅ FAST חינמי | 1 |
| `amg01576-blueskyeenterta-lovepetsemea-samsungse-ctamh.amagi.tv` | FAST Official | ✅ FAST חינמי | 1 |
| `amg00861-terninternation-inwild-samsunguk-w5wic.amagi.tv` | FAST Official | ✅ FAST חינמי | 1 |
| `amc-rushbyamc-1-us.vizio.wurl.tv` | FAST Official | ✅ FAST חינמי | 1 |
| `amogonetworx-artflix-1-nl.samsung.wurl.tv` | FAST Official | ✅ FAST חינמי | 1 |
| `linear-1224.frequency.stream` | FAST Public CDN | ✅ FAST חינמי | 1 |
| `dqga3jatxofgx.cloudfront.net` | FAST Public CDN | ✅ FAST חינמי | 1 |
| `aegis-cloudfront-1.tubi.video` | FAST Public CDN | ✅ FAST חינמי | 1 |
| `tgn.bozztv.com` | FAST Public CDN | ✅ FAST חינמי | 1 |
| `cc-mgr91yrk4pehy.akamaized.net` | FAST Public CDN | ✅ FAST חינמי | 1 |
| `ef79b15c8c7c46c7a9de9d33001dbd07.mediatailor.us-west-2.amazonaws.com` | FAST Public CDN | ✅ FAST חינמי | 1 |
| `amg00861-amg00861c10-rakuten-uk-3152.playouts.now.amagi.tv` | FAST Public CDN | ✅ FAST חינמי | 1 |
| `95cedebd.wurl.com` | FAST Public CDN | ✅ FAST חינמי | 1 |
| `amg01074-fueltv-fueltvemeaen-rakuten-b6j62.amagi.tv` | FAST Public CDN | ✅ FAST חינמי | 1 |
| `nosignal1.antenaplay.ro` | FAST Public CDN | ✅ FAST חינמי | 1 |
| `amg02278-amg02278c1-flosports-worldwide-7592.playouts.now.amagi.tv` | FAST Public CDN | ✅ FAST חינמי | 1 |
| `drybar-drybarcomedy-1-au.samsung.wurl.tv` | FAST Public CDN | ✅ FAST חינמי | 1 |
| `lightning-now90s-samsungnz.amagi.tv` | FAST Public CDN | ✅ FAST חינמי | 1 |
| `live-hls-apps-aje-fa.getaj.net` | אל-ג'זירה – שרת רשמי | ✅ רשמי | 1 |
| `media-tyo.hls.nhkworld.jp` | FAST Public CDN | ✅ FAST חינמי | 1 |
| `d8975dc3.wurl.com` | FAST Public CDN | ✅ FAST חינמי | 1 |
| `fastlive.cctvplus.com` | FAST Public CDN | ✅ FAST חינמי | 1 |
| `boing.akamaized.net` | FAST Public CDN | ✅ FAST חינמי | 1 |
| `lightning-fnf-samsungaus.amagi.tv` | FAST Public CDN | ✅ FAST חינמי | 1 |
| `sr-live-weigel1.akamaized.net` | FAST Public CDN | ✅ FAST חינמי | 1 |
| `d21vn5eki5qsdi.cloudfront.net` | FAST Public CDN | ✅ FAST חינמי | 1 |
| `linear-899.frequency.stream` | FAST Public CDN | ✅ FAST חינמי | 1 |
| `bbc-classicdrwho-1-us.roku.wurl.tv` | FAST Public CDN | ✅ FAST חינמי | 1 |
| `linear-938.frequency.stream` | FAST Public CDN | ✅ FAST חינמי | 1 |
| `vizioaaaa-carol-burnett-vizio-xam92.amagi.tv` | FAST Public CDN | ✅ FAST חינמי | 1 |
| `d1e9r0b71zfwk7.cloudfront.net` | FAST Public CDN | ✅ FAST חינמי | 1 |
| `d3i68hpvh19xjd.cloudfront.net` | FAST Public CDN | ✅ FAST חינמי | 1 |
| `rakutenaa-tm-intl-aus-rakuten-eu-n1gtg.amagi.tv` | FAST Public CDN | ✅ FAST חינמי | 1 |
| `amg00793-amg00793c5-vizio-us-2666.playouts.now.amagi.tv` | FAST Public CDN | ✅ FAST חינמי | 1 |
| `mvg-mv.otteravision.com` | FAST Public CDN | ✅ FAST חינמי | 1 |
| `amg00627-banijaygroup-dealornodeal-samsungau-si7xg.amagi.tv` | FAST Public CDN | ✅ FAST חינמי | 1 |
| `amg00627-banijaygroup-fearfactor-samsungau-9vdod.amagi.tv` | FAST Public CDN | ✅ FAST חינמי | 1 |
| `amg00627-banijaygroup-wipeoutxtraau-samsungau-ashbl.amagi.tv` | FAST Public CDN | ✅ FAST חינמי | 1 |
| `d11r33s5i066xh.cloudfront.net` | FAST Public CDN | ✅ FAST חינמי | 1 |
| `edge-fast3.evrideo.tv` | FAST Public CDN | ✅ FAST חינמי | 1 |
| `amg00441-amg00441c1-rakuten-us-6050.playouts.now.amagi.tv` | FAST Public CDN | ✅ FAST חינמי | 1 |
| `d6ef3usc6d9cl.cloudfront.net` | FAST Public CDN | ✅ FAST חינמי | 1 |
| `eco-live.mediacast.co.il` | eco99fm – Mediacast (רשמי) | ✅ רשמי | 1 |
| `102.livecdn.biz` | 102FM – LiveCDN (רשמי) | ✅ רשמי | 1 |
| `1075.livecdn.biz` | רדיו חיפה – LiveCDN (רשמי) | ✅ רשמי | 1 |
| `rs.mizrahit.fm` | rs.mizrahit.fm | ⚠️ ממסר צד שלישי | 1 |
| `www.rs.mizrahit.fm` | www.rs.mizrahit.fm | ⚠️ ממסר צד שלישי | 1 |
| `streaming.radio.co` | streaming.radio.co | ⚠️ ממסר צד שלישי | 1 |
| `kzradio.mediacast.co.il` | רדיו הקצה – Mediacast (רשמי) | ✅ רשמי | 1 |
| `995.livecdn.biz` | 99.5FM – LiveCDN (רשמי) | ✅ רשמי | 1 |
| `176.9.7.145` | 176.9.7.145 | ⚠️ ממסר צד שלישי | 1 |
| `radio.betteradio.live` | radio.betteradio.live | ⚠️ ממסר צד שלישי | 1 |
| `ystream.live` | ystream.live | ⚠️ ממסר צד שלישי | 1 |
| `online.star33radio.com` | online.star33radio.com | ⚠️ ממסר צד שלישי | 1 |
| `n0a.radiojar.com` | n0a.radiojar.com | ⚠️ ממסר צד שלישי | 1 |
| `liveradio.co.il` | liveradio.co.il | ✅ רשמי | 1 |
| `cast.radionahariya.com` | רדיו נהריה – שרת התחנה | ✅ רשמי | 1 |
| `radio.diki-mizrahit.co.il` | רדיו שירי דיכאון – השרת של התחנה | ✅ רשמי | 1 |
| `play.radioking.io` | play.radioking.io | ⚠️ ממסר צד שלישי | 1 |
| `1062onair.runi.ac.il` | runi.ac.il | ✅ רשמי | 1 |
| `listen.radioking.com` | RadioKing – נגן השידור של Studio Qualita | ✅ רשמי | 1 |
| `s3.yesstreaming.net` | YesStreaming – זרם הנגן באתר Radio Plus | ✅ רשמי | 1 |
| `stream.jewishmusicstream.com` | stream.jewishmusicstream.com | ⚠️ ממסר צד שלישי | 1 |
| `ff_engine.streamgates.net` | ff_engine.streamgates.net | ⚠️ ממסר צד שלישי | 1 |
| `stream.radio.co` | stream.radio.co | ⚠️ ממסר צד שלישי | 1 |
| `streams.radio.co` | streams.radio.co | ⚠️ ממסר צד שלישי | 1 |
| `uksoutha.streaming.broadcast.radio` | uksoutha.streaming.broadcast.radio | ⚠️ ממסר צד שלישי | 1 |
| `c44.radioboss.fm` | c44.radioboss.fm | ⚠️ ממסר צד שלישי | 1 |
| `rocky.kupernet.com` | rocky.kupernet.com | ⚠️ ממסר צד שלישי | 1 |
| `listen6.myradio24.com` | listen6.myradio24.com | ⚠️ ממסר צד שלישי | 1 |

## 📺 ערוצי ישראל - שידור חי

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 1 | וואלה!+ חדשות – שידור חי HD | live.wcdn.co.il | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 2 | ynet Live | ynet – השרת של ידיעות אחרונות | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 3 | וואלה!+ yes תרבות ובידור HD | live.wcdn.co.il | ✅ רשמי | iptv-org (WallaYes.il) | ✅ |
| 6 | רשת 13 קומדיה ובידור HD | ערוץ FAST ישראלי – AWS CloudFront | ✅ FAST חינמי | iptv-org (Reshet13Comedy.il) | ✅ |
| 7 | ערוץ בית ועיצוב ישראלי HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 9 | ערוץ 9 הישראלי FHD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | iptv-org (Channel9.il) | ✅ |
| 10 | ערוץ כלכלה 10 HD | Redge – ה-CDN הרשמי של כאן, ערוץ 14 וכלכלה 10 | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 11 | כאן 11 FHD | Redge – ה-CDN הרשמי של כאן, ערוץ 14 וכלכלה 10 | ✅ רשמי | שידור רשמי ויציב ללא תקיעות | ✅ |
| 12 | קשת 12 FHD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | iptv-org (Keshet12.il) | ✅ |
| 13 | רשת 13 FHD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | iptv-org (Reshet13.il) | ✅ |
| 14 | עכשיו 14 FHD | Redge – ה-CDN הרשמי של כאן, ערוץ 14 וכלכלה 10 | ✅ רשמי | iptv-org (Now14.il) | ✅ |
| 15 | i24NEWS עברית HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | iptv-org (i24NEWSHebrew.il) | ✅ |
| 16 | ערוץ חדשות 16 HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | iptv-org (Channel16.il) | ✅ |
| 17 | ערוץ ארץ נהדרת 24/7 HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 22 | ערוץ הטיולים HD | ערוץ FAST ישראלי – AWS CloudFront | ✅ FAST חינמי | שידור רשמי ערוץ הנופש והתיירות HD | ✅ |
| 24 | ערוץ 24 HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | iptv-org (Channel24.il) | ✅ |
| 25 | ערוץ סברי מרנן 24/7 HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 33 | ערוץ מכאן 33 FHD | כאן – תאגיד השידור (CDN רשמי) | ✅ רשמי | סריקת ערוצים רשמית | ✅ |
| 70 | ערוץ הכנסת 99 HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | iptv-org (KnessetChannel.il) | ✅ |
| 71 | ערוץ Beautyz אופנה ויופי HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 72 | ערוץ מוזיקה il HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 73 | ערוץ המוזיקה – קשת / FreeTV | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 74 | ערוץ רוקדים עם כוכבים 24/7 HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 75 | כאן 11 – כתוביות ונגישות HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | iptv-org (Kan11Subs.il) | ✅ |
| 76 | קשת 12 – כתוביות ונגישות HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 77 | רשת 13 – כתוביות ונגישות HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | iptv-org (Reshet13Subs.il) | ✅ |
| 78 | עכשיו 14 – כתוביות ונגישות HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 79 | HOT ערוץ החופש HD | ערוץ FAST ישראלי – AWS CloudFront | ✅ FAST חינמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 80 | Shelanu.TV – ערוץ שלנו HD | שלנו TV – CDN77 | ✅ רשמי | סריקת ערוצים מאומתת | ✅ |
| 81 | ILTV ישראל בצרפתית | ILTV – CreaCast (רשמי) | ✅ רשמי | iptv-org (ILTV.fr) | ✅ |
| 82 | JLTV טלוויזיה יהודית HD | JLTV – Ottera / Xumo FAST | ✅ FAST חינמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 83 | Angel TV Hebrew HD | Angel TV Hebrew – Akamai (רשמי) | ✅ רשמי | סריקת ערוצים מאומתת | ✅ |
| 84 | כאן 11 – כתוביות | Redge – ה-CDN הרשמי של כאן, ערוץ 14 וכלכלה 10 | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 85 | רשת 13 – נגיש | רשת 13 – השרת הרשמי (G-Mana) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 86 | ערוץ הכנסת – נגיש | ערוץ הכנסת – השידור של הכנסת (GoStreaming) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 87 | i24NEWS English | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 88 | i24NEWS צרפתית HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | iptv-org (i24NEWSFrench.il) | ✅ |
| 89 | FreeTV דוקו ישראלי HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 91 | רשת 13 ערוץ הנופש HD | ערוץ FAST ישראלי – AWS CloudFront | ✅ FAST חינמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 92 | רשת 13 ערוץ הריאליטי HD | ערוץ FAST ישראלי – AWS CloudFront | ✅ FAST חינמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 93 | ערוץ הכנסת – שפת הסימנים ונגישות | ערוץ הכנסת – השידור של הכנסת (GoStreaming) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 94 | Ynet Live 2 – שידורי חדשות | ynet-live-02.ynet-pic1.yit.co.il | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 95 | הרדיו הראשון 89.1FM – וידאו חי | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 96 | רדיו לב המדינה 91FM – וידאו חי | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 97 | Daystar TV (FreeTV ישראל) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 98 | i24NEWS צרפתית ישראל FHD | i24NEWS – שרת רשמי | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 373 | ערוץ הקומדיה הישראלי HD | ערוץ FAST ישראלי – AWS CloudFront | ✅ FAST חינמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 374 | ערוץ קבלה לעם שידור חי HD | קבלה לעם – שרת רשמי | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 381 | ערוץ 16 ישראל HD | ערוץ 16 – שרת רשמי | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 382 | ערוץ i24NEWS עברית HD | i24NEWS – שרת רשמי | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 411 | ערוץ הלא TV HD (Hala TV) | פאנט – הלא TV (רשמי) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 513 | ערוץ 9 ישראל HD | GoStreaming – ספק השידור של ערוץ 9 | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 528 | ערוץ הקריוקי הישראלי HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 536 | הרשת הישראלית – The Israeli Network HD | r.eu.cdn-redge.media | ⚠️ ממסר צד שלישי | שידור רשמי The Israeli Network | ✅ |
| 4 | HOT בידור ישראלי HD | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 5 | HOT ריאל HD (HOT Real) | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 8 | ערוץ השעשועונים והמשחקים HD | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 18 | yes דוקו HD | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 19 | HOT 8 מדע ודוקו HD | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 20 | ערוץ בית+ Home Plus HD | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 21 | ערוץ הבריאות HD | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 26 | ערוץ הבידור E! ישראל HD | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 28 | ערוץ הריאליטי HD | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 29 | ערוץ ההומור והצחוק HD | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 35 | HOT מיינד דוקו והעשרה HD | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 69 | ערוץ 98 – האזרח הוותיק HD | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 153 | ערוץ הקומדיות הקלאסיות HD | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 390 | ערוץ ההומור 2 HD | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 399 | National Geographic WILD FHD | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 401 | ערוץ Beautyz אופנה ולייפסטייל HD | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 409 | ערוץ החיים הטובים HD (Good Life) | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 27 | ערוץ אגו Ego HD | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 111 | ערוץ פרטנר TV – עיצוב ולייף סטייל HD | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |

## 📺 ילדים ונוער (בעברית)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 23 | כאן חינוכית 23 FHD | Redge – ה-CDN הרשמי של כאן, ערוץ 14 וכלכלה 10 | ✅ רשמי | שידור רשמי ויציב ללא תקיעות | ✅ |
| 42 | ערוץ הילדים HD (המקום האמיתי) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 43 | ערוץ לוגי HD (Logi) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 45 | ערוץ ילדותי ישראלי HD (FreeTV Yalduti) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 46 | ערוץ הופ! פלא HD (Hop Pele) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 49 | ערוץ דיסני ג'וניור ישראל HD (Disney Jr.) | 88.212.15.50 | ⚠️ ממסר צד שלישי | iptv-org (DisneyJunior.il) | ❌ רק באפליקציה (HTTP) |
| 54 | ערוץ ג'וניור HD (Junior) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 55 | ערוץ יויו HD (YoYo) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 56 | ערוץ בייבי ישראל HD (BabyTV) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 60 | ערוץ סרטי ילדים ומשפחה HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 61 | ערוץ FOMO לנוער ורשת HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 62 | ערוץ שירי ילדותי HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 63 | ערוץ A+ ילדים HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 64 | ערוץ הכוכבים לילדים ונוער HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 44 | ערוץ הופ! ילדים בעברית HD | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 47 | ערוץ לולי לפעוטות בעברית HD | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 48 | ערוץ דיסני ישראל HD (Disney Channel) | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 50 | ערוץ ניקלודיאון ישראל HD (Nickelodeon) | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 51 | ערוץ ניק ג'וניור ישראל HD (Nick Jr.) | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 52 | ערוץ טין ניק ישראל HD (TeenNick) | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 53 | ערוץ זום ZOOM ילדים ונוער HD | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 57 | yes קולנוע ילדים FHD (yes Movies Kids) | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 58 | HOT סינמה פמילי HD (סרטי ילדים ומשפחה) | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 59 | ערוץ WIZ! לילדים ונוער HD | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 68 | HOT באזז לילדים ונוער HD (HOT Buzz) | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 384 | ערוץ הופ! פלוס HD | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 404 | ערוץ ג'וניור HD (Junior) | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 405 | ערוץ בייבי HD (Baby Channel) | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 407 | yes קולנוע ילדים HD (yes Movies Kids) | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |

## 📺 ערוצי אוכל (Food Channels)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 30 | ערוץ פודי Foody בישול ישראלי HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 175 | ערוץ האוכל והבישול – קשת / FreeTV | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 176 | Bon Appétit HD – ערוץ הבישול בון אפטיט | bonappetit-samsung.amagi.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 177 | Gusto TV FHD – ערוץ האוכל העולמי גוסטו | e75fc053c0d2406fb74cdd71d77e1a7f.mediatailor.us-east-1.amazonaws.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 179 | Food Network International HD | streaming.aurora.enhanced.live | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 180 | TV5MONDE Chefs HD – שפים וגסטרונומיה | tvf-tv5ch.otteravision.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 181 | Cooking Panda HD – מתכונים ובישול | stream-us-east-1.getpublica.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 618 | ערוץ הבישול הבריטי (BBC Food) | FAST Public CDN | ✅ FAST חינמי | iptv-org verified | ✅ |
| 619 | השף ג׳יימי אוליבר (Jamie Oliver) | FAST Public CDN | ✅ FAST חינמי | iptv-org verified | ✅ |
| 620 | אוכל וקולינריה (Tastemade) | FAST Public CDN | ✅ FAST חינמי | iptv-org verified | ✅ |
| 621 | מטבח המבחן (Test Kitchen) | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org verified | ✅ |
| 622 | אוכל קוריאני (K-FOOD) | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org verified | ✅ |
| 173 | ערוץ האוכל HD (Food Network / החיים הטובים) | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 174 | ערוץ פודי Foody HD | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |

## 📺 סרטים וסדרות (Movies & Series)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 31 | ערוץ ויוה איסטנבול HD (Viva Istanbul) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 32 | ערוץ ויוה טלנובלות HD (Viva) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 156 | סברי מרנן 24/7 – קשת / FreeTV | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 157 | ערוץ הדרמה – קשת / FreeTV | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 158 | ערוץ הקומדיה – קשת / FreeTV | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 159 | FreeTV סרטים ישראלי HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 160 | FreeTV סרטים פעולה HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 161 | FreeTV סרטים קומדיה HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 162 | FreeTV סרטים דרמה HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 163 | FreeTV סרטים מתח HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 164 | FreeTV סרטים אימה HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 165 | FreeTV סרטים רומנטי HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 166 | FreeTV סרטים פיל גוד HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 167 | FreeTV סרטים משפחה HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 168 | FreeTV 10 Stars סרטים HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 169 | FreeTV סדרות קומדיה 2 HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 170 | ערוץ הדרמות היומיות HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 171 | ערוץ וואמוס HD (Vamos) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 172 | FreeTV גלובל סדרות HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 531 | ערוץ הריאליטי הישראלי HD | ערוץ FAST ישראלי – AWS CloudFront | ✅ FAST חינמי | שידור פרטנר TV רשמי | ✅ |
| 551 | AMC Thrillers HD (סרטי מתח) | FAST Official | ✅ FAST חינמי | שידור FAST רשמי | ✅ |
| 552 | ARTFLIX Movie Classics HD (קלאסיקות) | FAST Official | ✅ FAST חינמי | שידור FAST רשמי | ✅ |
| 553 | 70s Cinema (קולנוע שנות ה-70) | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | שידור FAST רשמי | ✅ |
| 554 | 80s Rewind (קולנוע שנות ה-80) | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | שידור FAST רשמי | ✅ |
| 555 | 90s Throwback (קולנוע שנות ה-90) | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | שידור FAST רשמי | ✅ |
| 556 | 00s Replay (קולנוע שנות ה-2000) | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | שידור FAST רשמי | ✅ |
| 578 | סאות׳ פארק 24/7 (South Park) | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | verified | ✅ |
| 579 | קומדי סנטרל רוסט (CC Roast) | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | verified | ✅ |
| 580 | סטנדאפ נקי (Dry Bar Comedy) | FAST Public CDN | ✅ FAST חינמי | verified | ✅ |
| 581 | קומדיה בריטית (British Comedy) | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | verified | ✅ |
| 582 | סרטים קלאסיים (Classic Movies) | FAST Public CDN | ✅ FAST חינמי | verified | ✅ |
| 583 | סרטי אקשן (50 Cent Action) | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | verified | ✅ |
| 609 | זירת הפשע 24/7 (CSI) | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org verified | ✅ |
| 610 | זירת הפשע מיאמי (CSI: Miami) | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org verified | ✅ |
| 611 | זירת הפשע ניו יורק (CSI: NY) | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org verified | ✅ |
| 612 | דוקטור הו קלאסי (Doctor Who) | FAST Public CDN | ✅ FAST חינמי | iptv-org verified | ✅ |
| 613 | שרלוק הולמס 24/7 (Sherlock) | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org verified | ✅ |
| 614 | דרמות בריטיות (BBC Drama) | FAST Public CDN | ✅ FAST חינמי | iptv-org verified | ✅ |
| 115 | HOT 3 ערוץ הדגל FHD | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 116 | HOT HBO סדרות פרימיום HD | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 117 | HOT סינמה 1 FHD | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 118 | HOT סינמה 2 HD | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 119 | HOT סינמה 3 FHD | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 120 | HOT סינמה 4 HD | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 121 | HOT סינמה 5 HD | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 122 | HOT קולנוע ישראלי HD | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 123 | HOT קולנוע רומנטי HD | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 124 | HOT דרמה HD | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 125 | HOT זון HD (HOT Zone) | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 126 | HOT קומדי HD (HOT Comedy) | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 127 | yes קולנוע ישראלי HD | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 128 | yes קולנוע אקשן FHD (yes Movies Action) | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 129 | yes קולנוע דרמה FHD (yes Movies Drama) | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 130 | yes קולנוע קומדיה FHD (yes Movies Comedy) | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 131 | yes TV אקשן HD (yes TV Action) | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 132 | yes TV דרמה HD (yes TV Drama) | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 133 | yes TV קומדיה HD (yes TV Comedy) | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 134 | ערוץ ויוה פרימיום HD (Viva Premium) | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 135 | הערוץ הים תיכוני HD | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 136 | הערוץ הים תיכוני+ HD | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 137 | ערוץ הדרמות הטורקיות+ FHD | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 138 | ערוץ הדרמות הטורקיות 2 FHD | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 139 | ערוץ הדרמות הטורקיות 3 FHD | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 141 | ערוץ הסרטים הים תיכוני 2 HD | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 142 | ערוץ האקשן העולמי 1 HD | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 143 | ערוץ האקשן העולמי 2 HD | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 144 | ערוץ הדרמות הספרדיות 1 HD | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 145 | ערוץ הדרמות הספרדיות 2 HD | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 146 | ערוץ הדרמות ההודיות 1 HD | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 147 | ערוץ הדרמות ההודיות 2 HD | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 148 | ערוץ בוליווד סרטים הודיים HD | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 149 | ערוץ בוליווד קלאסי HD | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 150 | ערוץ בולישואו Bollyshow HD | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 151 | ערוץ ZEE TV ישראל HD | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 152 | ערוץ GTV דרמות וקולנוע HD | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 154 | ערוץ האהבה והרומנטיקה HD | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 512 | ערוץ הסרטים הים תיכוני 1 HD | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 537 | ערוץ הדרמות הטורקיות פלוס HD | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |

## 📺 ספורט (Sports)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 36 | ערוץ ספורט 5 4K UHD | Redge – ה-CDN הרשמי של כאן, ערוץ 14 וכלכלה 10 | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 37 | ערוץ ONE דוקו ספורט HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 38 | ספורט 1 HD (Sport 1) | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי מקורי צ'רלטון FHD | ❌ רק באפליקציה (הפניה בלי CORS) |
| 39 | ספורט 2 HD (Sport 2) | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי מקורי צ'רלטון FHD | ❌ רק באפליקציה (הפניה בלי CORS) |
| 40 | ספורט 3 HD (Sport 3) | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי מקורי צ'רלטון FHD | ❌ רק באפליקציה (הפניה בלי CORS) |
| 41 | ספורט 4 FHD (Sport 4) | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי מקורי צ'רלטון FHD | ❌ רק באפליקציה (הפניה בלי CORS) |
| 100 | ספורט 5 FHD (5SPORT) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 101 | ספורט 5 פלוס FHD (5PLUS) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 102 | ספורט 5 לייב FHD (5LIVE) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 103 | ספורט 5 גולד HD (5GOLD) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 104 | ספורט 5 סטארס FHD (5STARS) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 105 | ספורט 5 מקס FHD (5MAX) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 107 | ערוץ ONE 1 FHD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 108 | ערוץ ONE 2 FHD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 110 | ערוץ ONE Edge HD (אדג' ספורט) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 112 | Sky Racing 1 HD (סקיי רייסינג ספורט) | 636ffd31f0e12.streamlock.net | ⚠️ ממסר צד שלישי | שידור רשמי Sky | ✅ |
| 113 | ספורט 5 – שידור חי | ספורט 5 – השידור הרשמי של RGE (Akamai) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 114 | Red Bull TV Extreme Sports FHD | Red Bull TV – Akamai (רשמי) | ✅ רשמי | סריקת רשת v4.6.0 | ✅ |
| 364 | beIN SPORTS XTRA FHD | bein-xtra-bein.amagi.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 365 | PGA Tour Golf Live FHD | d11k1mnrgfposz.cloudfront.net | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 367 | CBS Sports HQ FHD | dai.google.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 375 | Sky Sports F1 HD (פורמולה 1 שידור חי) | mvg-mv-xumo.otteravision.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 376 | מוטורוויז'ן Motorvision HD | mvg-mv-xumo.otteravision.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 378 | ערוץ מרוצים RACER International HD | amg00378-mavtv-amg00378c2-rakuten-us-1048.playouts.now.amagi.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 379 | ערוץ PowerNation TV מנועים ומוטורספורט | amg01258-raycomsports-powernationau-samsungau-18bwc.amagi.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 380 | ערוץ מרוצים Sky Racing 1 HD | 636ffd31f0e12.streamlock.net | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 571 | פיפ״א+ כדורגל (FIFA+) | FAST Public CDN | ✅ FAST חינמי | verified | ✅ |
| 572 | ספורט אקסטרים (FUEL TV) | FAST Public CDN | ✅ FAST חינמי | verified | ✅ |
| 573 | אומנויות לחימה (FightBox HD) | FAST Public CDN | ✅ FAST חינמי | verified | ✅ |
| 574 | בלטור MMA (Bellator) | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | verified | ✅ |
| 575 | מרוצי מוטורספורט (FloRacing) | FAST Public CDN | ✅ FAST חינמי | verified | ✅ |
| 576 | DAZN חצים (DAZN Darts) | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | verified | ✅ |
| 577 | גולף עולמי (Golf Kingdom) | FAST Public CDN | ✅ FAST חינמי | verified | ✅ |
| 511 | ספורט 6 HD (Sport 6) | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 106 | ספורט 5 4K UHD | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 109 | ערוץ ONE דוקו HD (ONE DOCO) | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |

## 📺 מסורת וקהילה

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 90 | הכותל המערבי – שידור חי 24/7 HD | cdn1.cast-tv.com | ⚠️ ממסר צד שלישי | שידור רשמי הקרן למורשת הכותל | ✅ |
| 213 | ערוץ הידברות 97 HD | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | iptv-org (Hidabroot.il) | ✅ |
| 214 | ערוץ קבלה לעם 66 HD | edge1.il.kab.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 215 | קבלה לעם – צרפתית HD | edge1.il.kab.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 216 | קבלה לעם – גרמנית HD | edge1.il.kab.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 217 | קבלה לעם – איטלקית HD | edge1.il.kab.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 218 | Jai TV – טלוויזיה יהודית וישראלית | vivo.solumedia.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 527 | ערוץ קבלה לעם ברוסית HD | קבלה לעם – שרת רשמי | ✅ רשמי | סריקת ערוצים מאומתת | ✅ |
| 635 | קבלה לעם (ערוץ 66) HD | Official Israeli CDN | ✅ רשמי | שידור רשמי ישיר | ✅ |

## 📺 חדשות מהעולם (News)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 183 | i24NEWS English World HD | i24NEWS – שרת רשמי | ✅ רשמי | סריקת ערוצים מאומתת | ✅ |
| 184 | Sky News UK FHD | linear901-oo-hls0-prd-gtm.delivery.skycdp.com | ⚠️ ממסר צד שלישי | שידור רשמי Sky | ✅ |
| 185 | Sky News Australia Extra 1 HD | skynewsau-live.akamaized.net | ⚠️ ממסר צד שלישי | שידור רשמי Sky | ✅ |
| 186 | Sky News Australia Extra 2 HD | skynewsau-live.akamaized.net | ⚠️ ממסר צד שלישי | שידור רשמי Sky | ✅ |
| 187 | Sky News Australia Extra 3 HD | skynewsau-live.akamaized.net | ⚠️ ממסר צד שלישי | שידור רשמי Sky | ✅ |
| 188 | DW News English HD | DW – Akamai (רשמי) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 189 | BFM TV HD (France) | live-cdn-stream-euw1.bfmtv.bct.nextradiotv.com | ⚠️ ממסר צד שלישי | iptv-org (BFMTV.fr) | ✅ |
| 190 | BFM Tech & Co HD | BFM – SFR (רשמי) | ✅ רשמי | iptv-org (BFMTechCo.fr) | ✅ |
| 191 | France 24 (FreeTV ישראל) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 192 | BBC News World FHD | vs-hls-push-ww-live.akamaized.net | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 193 | CNN International HD | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 195 | FOX LiveNOW News HD | cdn-uw2-prod.tsv2.amagi.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 196 | CBS News 24/7 FHD | cbsn-us.cbsnstream.cbsnews.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 197 | Bloomberg TV+ Business FHD | www.bloomberg.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 199 | Newsmax TV HD | amg00217-newsmax-newsmax-zeasn-022k9.amagi.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 200 | TRT World News FHD | tv-trtworld.medya.trt.com.tr | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 201 | CGTN News English FHD | english-livetx.cgtn.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 515 | ערוץ i24NEWS ערבית HD | i24NEWS – שרת רשמי | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 590 | ABC News Live HD | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org verified | ✅ |
| 591 | CBS News 24/7 HD | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org verified | ✅ |
| 592 | Al Jazeera English HD | אל-ג'זירה – שרת רשמי | ✅ רשמי | iptv-org verified | ✅ |
| 593 | NHK World יפן HD | FAST Public CDN | ✅ FAST חינמי | iptv-org verified | ✅ |

## 📺 טבע ודוקו (Documentary & Nature)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 206 | ערוץ הטבע הישראלי HD (Teva) | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 545 | BBC Earth HD (טבע ומדע) | FAST Official | ✅ FAST חינמי | שידור FAST רשמי | ✅ |
| 546 | History Hit HD (היסטוריה) | FAST Official | ✅ FAST חינמי | שידור FAST רשמי | ✅ |
| 547 | NatureTime HD (עולם הטבע) | FAST Official | ✅ FAST חינמי | שידור FAST רשמי | ✅ |
| 548 | Love The Planet HD (כדור הארץ) | FAST Official | ✅ FAST חינמי | שידור FAST רשמי | ✅ |
| 549 | Love Pets HD (חיות מחמד וטבע) | FAST Official | ✅ FAST חינמי | שידור FAST רשמי | ✅ |
| 550 | INWILD HD (חיות בר) | FAST Official | ✅ FAST חינמי | שידור FAST רשמי | ✅ |
| 561 | חלל בשידור חי (Space Live 4K) | FAST Public CDN | ✅ FAST חינמי | verified | ✅ |
| 562 | ספארי חי באפריקה (WildEarth) | FAST Public CDN | ✅ FAST חינמי | verified | ✅ |
| 563 | אווירה ורגיעה (ZenLIFE) | FAST Public CDN | ✅ FAST חינמי | verified | ✅ |
| 564 | טבע פראי (Love Nature) | FAST Public CDN | ✅ FAST חינמי | verified | ✅ |
| 565 | טבע ונופים (Better Life) | FAST Public CDN | ✅ FAST חינמי | verified | ✅ |
| 566 | סמית׳סוניאן דוקו (Smithsonian) | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | verified | ✅ |
| 567 | ערוץ המדע (Pluto Science) | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | verified | ✅ |
| 568 | דוקו עולמי (DocuBay) | FAST Public CDN | ✅ FAST חינמי | verified | ✅ |
| 569 | דוקומנטרי+ (Documentary+) | FAST Public CDN | ✅ FAST חינמי | verified | ✅ |
| 570 | טיולים בעולם (INTRAVEL) | FAST Public CDN | ✅ FAST חינמי | verified | ✅ |
| 202 | נשיונל ג'יאוגרפיק ישראל HD (Nat Geo) | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 203 | נשיונל ג'יאוגרפיק וויילד FHD (Nat Geo Wild) | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 204 | ערוץ דיסקברי ישראל FHD (Discovery) | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |
| 205 | ערוץ ההיסטוריה ישראל FHD (History) | osoyz0xv.ottclub.xyz | ⚠️ ממסר צד שלישי | שידור ישראלי חי מאומת | ❌ רק באפליקציה (הפניה בלי CORS) |

## 📺 מוזיקה (Music TV)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 207 | רדיוס 100FM – אולפן וידאו HD | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 208 | Sky Folk TV HD (סקיי פולק) | Sky Folk Live | ✅ רשמי | שידור רשמי Sky | ✅ |
| 212 | 1HD Music Television | שרת IP ללא שם (1HD) | ⚠️ ממסר צד שלישי | iptv-org (1HDMusicTelevision.ru) | ❌ רק באפליקציה (HTTP) |
| 366 | Vevo Pop Music Hits FHD | amg00056-amg00056c6-rakuten-uk-3235.playouts.now.amagi.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 387 | ערוץ מוזיקה ישראלית 24/7 HD | FreeTV – השידור הרשמי (AWS CloudFront) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 397 | MTV Hits & Clips HD | ma.anixa.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 584 | MTV להיטי פופ (MTV Pop) | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | verified | ✅ |
| 585 | MTV שירים חדשים (MTV New) | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | verified | ✅ |
| 586 | ערוץ הרוק (NOW Rock) | FAST Public CDN | ✅ FAST חינמי | verified | ✅ |
| 587 | מוזיקה אלקטרונית (Clubbing TV) | FAST Public CDN | ✅ FAST חינמי | verified | ✅ |
| 588 | פסקולים מסרטים (Movie Music) | FAST Public CDN | ✅ FAST חינמי | verified | ✅ |
| 589 | מוזיקה קלאסית (Classica) | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | verified | ✅ |

## 📺 ילדים מהעולם (Kids World)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 352 | Baby Shark TV (בייבי שארק לילדים) | Baby Shark TV – Wurl (Roku FAST) | ✅ FAST חינמי | סריקת ערוצי ילדים | ✅ |
| 353 | ערוץ בייבי BabyTV Global HD | BabyFirst – JW Player FAST | ✅ FAST חינמי | סריקת ערוצי ילדים | ✅ |
| 354 | ערוץ זו-מו לילדים (ZooMoo HD) | amagi.tv | ✅ FAST חינמי | סריקת ערוצי ילדים | ✅ |
| 361 | דאק טי וי פלוס ducktv PLUS HD | Antik Telecom (סלובקיה), נתיבי test | ⚠️ ממסר צד שלישי | iptv-org (ducktvPLUS.sk) | ✅ |
| 362 | ערוץ לגו Lego Channel HD | LEGO Channel – AWS CloudFront FAST | ✅ FAST חינמי | iptv-org (LegoChannel.us) | ✅ |
| 363 | דאק טי וי ducktv HD | Antik Telecom (סלובקיה), נתיבי test | ⚠️ ממסר צד שלישי | iptv-org (ducktv.sk) | ✅ |
| 368 | Cartoon Network HD (קארטון נטוורק) | Cartoon Network Broadcast Stream | ✅ שידור חי | רשימה ידנית (גרסה קודמת) | ✅ |
| 370 | Disney Channel Global HD | Disney Channel Stream | ✅ שידור חי | רשימה ידנית (גרסה קודמת) | ✅ |
| 371 | Crunchyroll Anime HD | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 372 | PBS Kids HD | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 539 | Moonbug Kids HD (קוקומלון ומונבאג קידס) | moonbug-rokuus.amagi.tv | ⚠️ ממסר צד שלישי | שידור FAST רשמי Moonbug Kids | ✅ |
| 540 | Peppa Pig TV (פפה פיג 24/7) | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | שידור FAST רשמי Pluto TV | ✅ |
| 541 | Smurf TV (הדרדסים 24/7) | stream.ads.ottera.tv | ⚠️ ממסר צד שלישי | שידור FAST רשמי Smurf TV | ✅ |
| 542 | Nick Jr. Pluto TV HD (ניק ג'וניור עולמי) | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | שידור FAST רשמי Pluto TV | ✅ |
| 543 | Nickelodeon Pluto TV HD (ניקלודיאון עולמי) | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | שידור FAST רשמי Pluto TV | ✅ |
| 544 | NickToons Pluto TV HD (ניקטונס) | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | שידור FAST רשמי Pluto TV | ✅ |
| 598 | ערוץ לגו לילדים (LEGO Kids TV) | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org verified | ✅ |
| 599 | גארפילד וחברים 24/7 (Garfield) | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org verified | ✅ |
| 600 | קרטוניטו לילדים (Cartoonito HD) | FAST Public CDN | ✅ FAST חינמי | iptv-org verified | ✅ |
| 601 | ערוץ המצוירים (Kartoon Channel!) | FAST Public CDN | ✅ FAST חינמי | iptv-org verified | ✅ |
| 602 | מצוירים קלאסיים (MeTV Toons) | FAST Public CDN | ✅ FAST חינמי | iptv-org verified | ✅ |
| 603 | סוניק הקיפוד 24/7 (Sonic TV) | FAST Public CDN | ✅ FAST חינמי | iptv-org verified | ✅ |
| 604 | נארוטו 24/7 (Naruto) | FAST Public CDN | ✅ FAST חינמי | iptv-org verified | ✅ |
| 605 | נארוטו שיפודן 24/7 (Shippuden) | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org verified | ✅ |
| 606 | ערוץ אנימה (Anime x HIDIVE) | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org verified | ✅ |
| 607 | אנימה רטרו קלאסית (RetroCrush) | FAST Public CDN | ✅ FAST חינמי | iptv-org verified | ✅ |
| 608 | סרטי אנימה (Pluto Anime Movies) | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org verified | ✅ |

## 📺 סדרות לצפייה ישירה (VOD)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 501 | קופה ראשית FHD (VOD) | Redge – ה-CDN הרשמי של כאן, ערוץ 14 וכלכלה 10 | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 502 | מנאייכ FHD (VOD) | Redge – ה-CDN הרשמי של כאן, ערוץ 14 וכלכלה 10 | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 503 | טהרן FHD (VOD) | Redge – ה-CDN הרשמי של כאן, ערוץ 14 וכלכלה 10 | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 504 | היהודים באים FHD (VOD) | Redge – ה-CDN הרשמי של כאן, ערוץ 14 וכלכלה 10 | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 505 | המפקדת FHD (VOD) | Redge – ה-CDN הרשמי של כאן, ערוץ 14 וכלכלה 10 | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 506 | בואו לאכול איתי FHD (VOD) | Redge – ה-CDN הרשמי של כאן, ערוץ 14 וכלכלה 10 | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 507 | כראמל FHD (VOD) | Redge – ה-CDN הרשמי של כאן, ערוץ 14 וכלכלה 10 | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 508 | זהו זה! FHD (VOD) | Redge – ה-CDN הרשמי של כאן, ערוץ 14 וכלכלה 10 | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 509 | חזרות FHD (VOD) | Redge – ה-CDN הרשמי של כאן, ערוץ 14 וכלכלה 10 | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 510 | שעת נעילה FHD (VOD) | Redge – ה-CDN הרשמי של כאן, ערוץ 14 וכלכלה 10 | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |

## 📺 רדיו ישראלי (Israeli Radio)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 533 | הרדיו הטוב 106.4FM (The Best Radio) | StreamGates – ה-CDN של התחנה | ✅ רשמי | שידור רשמי הרדיו הטוב | ✅ |
| 534 | הרדיו הראשון 89.1FM (Первое радио) | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | שידור רשמי הרדיו הראשון | ✅ |
| 535 | רדיוס 100% רוסית 89.1 (Radios Russian) | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | שידור רדיוס 100FM רשמי | ✅ |

## 📺 כלכלה ועסקים (Business)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 594 | Bloomberg TV+ כלכלה ועסקים | FAST Public CDN | ✅ FAST חינמי | iptv-org verified | ✅ |
| 595 | Bloomberg Originals דוקו ועסקים | FAST Public CDN | ✅ FAST חינמי | iptv-org verified | ✅ |
| 596 | Bloomberg Europe שוק ההון | FAST Public CDN | ✅ FAST חינמי | iptv-org verified | ✅ |
| 597 | CGTN Global Business | FAST Public CDN | ✅ FAST חינמי | iptv-org verified | ✅ |

## 📺 קומדיה (Comedy)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 615 | סאות׳ פארק: קרטמן (Cartman 24/7) | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org verified | ✅ |
| 616 | מופע קומדיה קלאסי (Carol Burnett) | FAST Public CDN | ✅ FAST חינמי | iptv-org verified | ✅ |
| 617 | איי קארלי 24/7 (iCarly) | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org verified | ✅ |

## 📺 לייף סטייל ובידור

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 623 | טופ גיר רכב 24/7 (Top Gear) | FAST Public CDN | ✅ FAST חינמי | iptv-org verified | ✅ |
| 624 | עולם הרכב והמרוצים (Motorvision FHD) | FAST Public CDN | ✅ FAST חינמי | iptv-org verified | ✅ |
| 625 | מכוניות על (Cars.TV) | FAST Public CDN | ✅ FAST חינמי | iptv-org verified | ✅ |
| 626 | מרדפי מכוניות בשידור חי (Car Chase) | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org verified | ✅ |
| 627 | דיל או לא דיל 24/7 (Deal or No Deal) | FAST Public CDN | ✅ FAST חינמי | iptv-org verified | ✅ |
| 628 | אפקט הפחד 24/7 (Fear Factor) | FAST Public CDN | ✅ FAST חינמי | iptv-org verified | ✅ |
| 629 | ווייפאאוט אקסטרים (Wipeout) | FAST Public CDN | ✅ FAST חינמי | iptv-org verified | ✅ |
| 630 | עיצוב הבית והגינה (BBC Home) | FAST Public CDN | ✅ FAST חינמי | iptv-org verified | ✅ |
| 631 | ערוץ האופנה פריז (FashionTV) | FAST Public CDN | ✅ FAST חינמי | iptv-org verified | ✅ |
| 632 | עיצוב ואדריכלות (Design Network) | FAST Public CDN | ✅ FAST חינמי | iptv-org verified | ✅ |

## 📺 טיולים והרפתקאות (Travel)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 633 | מסעות בעולם (BBC Travel) | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org verified | ✅ |
| 634 | קולינריה וטיולים (Tastemade Travel) | FAST Public CDN | ✅ FAST חינמי | iptv-org verified | ✅ |

## 📻 רדיו

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 65 | רדיוס 100% שירי דיסני | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 66 | רדיוס 100% ילדים | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 67 | רדיוס 100% ערוץ הכוכבים | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 219 | ברסלב: הלכות ודף יומי | רדיו ברסלב – שרת התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 220 | ברסלב: ספרי ברסלב | רדיו ברסלב – שרת התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 221 | ברסלב: קצר וקולע | רדיו ברסלב – שרת התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 222 | ברסלב: תורה וזימרה | רדיו ברסלב – שרת התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 223 | כאן ב - רשת ב | Triton StreamTheWorld – מפיץ הרדיו של כאן | ✅ רשמי | radio-browser.info + בדיקת שמות הזרמים של כאן | ✅ |
| 224 | כאן גימל | Triton StreamTheWorld – מפיץ הרדיו של כאן | ✅ רשמי | radio-browser.info + בדיקת שמות הזרמים של כאן | ✅ |
| 225 | כאן 88 | Triton StreamTheWorld – מפיץ הרדיו של כאן | ✅ רשמי | radio-browser.info + בדיקת שמות הזרמים של כאן | ✅ |
| 226 | כאן תרבות | Triton StreamTheWorld – מפיץ הרדיו של כאן | ✅ רשמי | radio-browser.info + בדיקת שמות הזרמים של כאן | ✅ |
| 227 | כאן מורשת | Triton StreamTheWorld – מפיץ הרדיו של כאן | ✅ רשמי | radio-browser.info + בדיקת שמות הזרמים של כאן | ✅ |
| 228 | כאן רקע | Triton StreamTheWorld – מפיץ הרדיו של כאן | ✅ רשמי | radio-browser.info + בדיקת שמות הזרמים של כאן | ✅ |
| 229 | כאן קול המוזיקה | Triton StreamTheWorld – מפיץ הרדיו של כאן | ✅ רשמי | radio-browser.info + בדיקת שמות הזרמים של כאן | ✅ |
| 230 | גלגלצ | גלי צה"ל / גלגלצ – CDN רשמי (Bynet) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 231 | גלי צה"ל | גלי צה"ל / גלגלצ – CDN רשמי (Bynet) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 232 | גלגלצ להיטים | גלי צה"ל / גלגלצ – CDN רשמי (Bynet) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 233 | eco99fm | eco99fm – Mediacast (רשמי) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 234 | 103FM | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 235 | רדיוס 100FM | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 236 | לב המדינה 91FM | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 237 | 102FM רדיו תל אביב | 102FM – LiveCDN (רשמי) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 238 | רדיו חיפה 107.5FM | רדיו חיפה – LiveCDN (רשמי) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 239 | 101FM רדיו ירושלים | StreamGates – ה-CDN של התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 240 | 90FM אמצע הדרך | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 241 | רדיו דרום 101.5FM | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 242 | רדיו דרום 97FM | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 243 | 104.5FM רדיו צפון | StreamGates – ה-CDN של התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 244 | קול רגע 96FM | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 245 | 102FM קול הים האדום | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 246 | גלי ישראל 94FM | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 247 | קול חי 93FM | קול חי – שרת התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 248 | קול חי מיוזיק | קול חי – שרת התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 249 | קול ברמה | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 250 | קול ברמה מוזיקה (קול פליי) | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 251 | רדיו 2000 | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 252 | רדיו ברסלב | רדיו ברסלב – שרת התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 253 | מזרחית FM | rs.mizrahit.fm | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 254 | נושמים מזרחית | www.rs.mizrahit.fm | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 255 | רדיו עברי 6 (מוזיקה ישראלית) | streaming.radio.co | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 256 | רדיו הקצה KZ | רדיו הקצה – Mediacast (רשמי) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 257 | רדיו ספורט 5 | ספורט 5 – השידור הרשמי של RGE (Akamai) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 258 | 99.5FM רדיו | 99.5FM – LiveCDN (רשמי) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 259 | AvivMedia טראנס | 176.9.7.145 | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 260 | Israeli Voice Radio | icecast.live | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 262 | TIMBR רדיו | radio.betteradio.live | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 263 | Up2Dance רדיו | icecast.live | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 264 | yStream | ystream.live | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 265 | ג'וינט רדיו בלוז רוק | jointil.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 266 | ג'וינט רדיו טראנס | jointil.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 267 | ג'וינט רדיו רגאיי | jointil.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 268 | גלגלצ עשורים | גלי צה"ל / גלגלצ – CDN רשמי (Bynet) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 269 | גלגלצ רגוע | גלי צה"ל / גלגלצ – CDN רשמי (Bynet) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 270 | גלגלצ רוק | גלי צה"ל / גלגלצ – CDN רשמי (Bynet) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 271 | סטאר 33 רדיו | online.star33radio.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 276 | קול הצפון | eCast – שרת התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 277 | רדיו CS קלאסי | n0a.radiojar.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 278 | רדיו אהבה – רומנטי | liveradio.co.il | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 279 | רדיו התחנה 101.5 | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 280 | רדיו נהריה | רדיו נהריה – שרת התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 281 | רדיו סהר | eCast – שרת התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 282 | רדיו שירי דיכאון | רדיו שירי דיכאון – השרת של התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 283 | רדיוס 100% K-Pop | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 284 | רדיוס 100% אגדות | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 285 | רדיוס 100% אהבה | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 286 | רדיוס 100% אולדיז | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 287 | רדיוס 100% אימון | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 288 | רדיוס 100% אירוויזיון | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 289 | רדיוס 100% בלוז | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 290 | רדיוס 100% בריטפופ | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 291 | רדיוס 100% ג'אז | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 292 | רדיוס 100% גיימר | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 293 | רדיוס 100% טופ 40 | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 294 | רדיוס 100% טיקטוק | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 295 | רדיוס 100% יהודי | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 296 | רדיוס 100% להיטים | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 297 | רדיוס 100% לטינו | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 298 | רדיוס 100% מוזיקת עולם | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 299 | רדיוס 100% מזרחית | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 300 | רדיוס 100% סטנדאפ | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 301 | רדיוס 100% סלפי | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 302 | רדיוס 100% סרטים | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 303 | רדיוס 100% צ'ילאאוט | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 304 | רדיוס 100% קאברים | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 305 | רדיוס 100% קאנטרי | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 306 | רדיוס 100% קלאב | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 307 | רדיוס 100% קלאסית | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 308 | רדיוס 100% רגוע | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 309 | רדיוס 100% רוק | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 310 | רדיוס 100% רוק קלאסי | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 311 | רדיוס 100% רטרו | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 312 | רדיוס 100% שנות ה-80 | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 313 | רדיוס 100% שנות ה-90 | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 314 | רדיוס נוסטלגי 96.3 | StreamGates – ה-CDN של התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 315 | רק מוזיקה (Rak Musica) | play.radioking.io | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 316 | AudioVersity רדיו אוניברסיטת רייכמן | runi.ac.il | ✅ רשמי | סריקת ערוצים מאומתת | ✅ |
| 317 | Radio Angelika תל אביב | radioangelika.com | ✅ רשמי | סריקת ערוצים מאומתת | ✅ |
| 318 | רדיו קווליטה (Radio Qualita) | RadioKing – נגן השידור של Studio Qualita | ✅ רשמי | זרם Radio Qualita; זהות התחנה אומתה באתר qualita.org.il | ✅ |
| 319 | Radio Plus ישראל | YesStreaming – זרם הנגן באתר Radio Plus | ✅ רשמי | נגן Radio Plus באתר radioplus.co.il | ✅ |
| 320 | Jewish Music Stream (מוזיקה יהודית) | stream.jewishmusicstream.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 321 | רדיוס 100% שלמה ארצי | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 322 | רדיוס 100% ישראלי | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 323 | רדיוס 100% חפלה | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 324 | רדיוס 100% ים תיכוני לוהט | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 325 | רדיוס 100% נוסטלגיה | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 326 | רדיוס 100FM – עושים היסטוריה | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 327 | רדיוס 100% שנות ה-70 | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 328 | רדיוס 100% שנות ה-2000 | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 329 | רדיוס 100% טראנס | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 330 | רדיוס 100% דאנס | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 331 | רדיוס 100% היפ הופ | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 332 | רדיוס 100% דיפ (Deep) | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 333 | רדיוס 100% סט די-ג'יי | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 334 | רדיוס 100% קריוקי | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 335 | רדיוס 100% קפה (Café) | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 336 | רדיוס 100% דרייב טיים | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 337 | רדיוס 100% מקום ראשון (No.1) | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 338 | רדיוס 100% הופעות חיות | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 339 | רדיוס 100% יוונית | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 340 | רדיוס 100% איטלקית | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 341 | רדיוס 100% צרפתית | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 342 | רדיוס 100FM – תומר מייזנר | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 343 | רדיוס 100FM – VIP Digital | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 344 | רדיוס 100FM – בית לוינשטיין | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 345 | רדיוס 100FM – התקליטייה | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 346 | רדיוס 100FM – מבזק חדשות 12 | ff_engine.streamgates.net | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 347 | כאן חינוכית – רדיו ילדים | כאן – תאגיד השידור (CDN רשמי) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 348 | רדיו חב"ד – מוזיקה יהודית | stream.radio.co | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 349 | רדיו חב"ד – שיעורי תורה | streams.radio.co | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 350 | קול רדיו (Kol Radio) | uksoutha.streaming.broadcast.radio | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 351 | Imsostablee Radio ישראל | c44.radioboss.fm | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 538 | רדיו זה רוק (Ze Rock Radio) | rocky.kupernet.com | ⚠️ ממסר צד שלישי | שידור רשמי Ze Rock Radio | ✅ |
| 557 | רדיו א-שמס 101FM (A-Shams) | StreamGates – ה-CDN של התחנה | ✅ רשמי | שידור רשמי רדיו א-שמס | ✅ |
| 558 | רדיו נאס ישראל (Radio Nas) | StreamGates – ה-CDN של התחנה | ✅ רשמי | שידור רשמי רדיו נאס | ✅ |
| 559 | רדיו אנג'ליקה ישראל (Radio Angelika) | radioangelika.com | ✅ רשמי | שידור רשמי Radio Angelika | ✅ |
| 560 | רדיו אלי ניסמן ישראל (Eli Nisman) | listen6.myradio24.com | ⚠️ ממסר צד שלישי | שידור רשמי Eli Nisman | ✅ |

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

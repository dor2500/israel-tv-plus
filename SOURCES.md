# מקורות – מאיפה מגיע כל ערוץ

נוצר אוטומטית מ-`channels.json` על ידי `tools/make_sources.py`. סה"כ **1693** פריטים: 1593 ערוצי טלוויזיה, 100 תחנות רדיו, 0 אפליקציות/שירותים.

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

- **134 שידורים רשמיים** – מהשרתים של הגוף המשדר עצמו או של המפיץ שלו (כאן, 13, 14, i24NEWS, גלי צה"ל, תחנות הרדיו, DW, אל-ג'זירה...).
- **176 ערוצי FAST** – ערוצים חינמיים עם פרסומות שהבעלים מפיצים בעצמם (Pluto TV, BabyFirst, LEGO, Baby Shark...).
- **1173 ערוצים מהעולם** – שידורים פתוחים מהמאגר הציבורי iptv-org (ייבוא אוטומטי, `tools/import_iptv.py`): כל אחד נבדק שהוא משדר ושיש בו תמונה, ללא ערוצים שדורשים מנוי/כתובת מיוחדת. רובם מהשרתים של הערוצים עצמם, אבל הבעלים לא נבדקו אחד-אחד.
- **210 ממסרי צד שלישי** – בעיקר `stream.mcquack.net` (87 ערוצים). אלה **אינם** השרתים של הערוצים: אתר חיצוני קולט את השידור ומפיץ אותו מחדש. בהם נמצאים גם ערוצים בתשלום (HOT, yes, viju/TV1000, ערוצי ספורט) – סביר מאוד שההפצה הזו **ללא רישיון** מבעלי הזכויות, והם עלולים להיעלם בכל רגע. ההחלטה אם להשאיר אותם היא שלך; הסקריפטים לא מוסיפים ממסרים חדשים של ערוצים בתשלום.
- **שירותי המנויים** (פרטנר, סלקום, STING, yes, HOT) – אין בפרויקט שום שידור שלהם; הכרטיס רק פותח את האפליקציה/האתר הרשמי, וצפייה דורשת מנוי.

> הסיווג רשמי / FAST / ממסר נקבע לפי שם השרת שממנו מגיע השידור (למשל CDN של הגוף המשדר), ולא אושר מול כל גוף משדר בנפרד.

## לפי שרת

| שרת | מי זה | סוג | מספר |
|---|---|---|---|
| `jmp2.uk` | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | 169 |
| `stream.mcquack.net` | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | 87 |
| `gb25.streamgates.net` | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | 34 |
| `92.114.85.87` | 92.114.85.87 | 🌐 ציבורי (iptv-org) | 24 |
| `cdn.cybercdn.live` | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | 16 |
| `freem3u.xyz` | freem3u.xyz | 🌐 ציבורי (iptv-org) | 15 |
| `rpn.bozztv.com` | rpn.bozztv.com | 🌐 ציבורי (iptv-org) | 13 |
| `38.75.136.137` | 38.75.136.137 | 🌐 ציבורי (iptv-org) | 10 |
| `cityeden.catcast.tv` | cityeden.catcast.tv | 🌐 ציבורי (iptv-org) | 10 |
| `barinaslive1.boldmss.com` | barinaslive1.boldmss.com | 🌐 ציבורי (iptv-org) | 10 |
| `dash2.antik.sk` | dash2.antik.sk | 🌐 ציבורי (iptv-org) | 9 |
| `live.kwikmotion.com` | live.kwikmotion.com | 🌐 ציבורי (iptv-org) | 9 |
| `mako-streaming.akamaized.net` | קשת – ה-CDN של mako (עם הרשאת צפייה של mako) | ✅ רשמי | 7 |
| `dash3.antik.sk` | Antik Telecom (סלובקיה), נתיבי test | ⚠️ ממסר צד שלישי | 7 |
| `a-cdn.klowdtv.com` | a-cdn.klowdtv.com | 🌐 ציבורי (iptv-org) | 7 |
| `stream-us-east-1.getpublica.com` | stream-us-east-1.getpublica.com | 🌐 ציבורי (iptv-org) | 7 |
| `cdn.pishow.tv` | cdn.pishow.tv | 🌐 ציבורי (iptv-org) | 7 |
| `playerservices.streamtheworld.com` | Triton StreamTheWorld – מפיץ הרדיו של כאן | ✅ רשמי | 7 |
| `ngtrk.dc.beltelecom.by` | ngtrk.dc.beltelecom.by | 🌐 ציבורי (iptv-org) | 6 |
| `n18syndication.akamaized.net` | n18syndication.akamaized.net | 🌐 ציבורי (iptv-org) | 6 |
| `redirector.rudo.video` | redirector.rudo.video | 🌐 ציבורי (iptv-org) | 6 |
| `74.91.26.218` | 74.91.26.218 | 🌐 ציבורי (iptv-org) | 6 |
| `hls02ns.antenaplay.ro` | hls02ns.antenaplay.ro | 🌐 ציבורי (iptv-org) | 6 |
| `stream.smotrim.ru` | Smotrim (VGTRK) – הבעלים של קרוסלה | ✅ רשמי | 6 |
| `live20.bozztv.com` | live20.bozztv.com | 🌐 ציבורי (iptv-org) | 6 |
| `glzwizzlv.bynetcdn.com` | גלי צה"ל / גלגלצ – CDN רשמי (Bynet) | ✅ רשמי | 6 |
| `alpha.tv.online.tm` | alpha.tv.online.tm | 🌐 ציבורי (iptv-org) | 5 |
| `nosignal1.antenaplay.ro` | nosignal1.antenaplay.ro | 🌐 ציבורי (iptv-org) | 5 |
| `168.196.127.137` | 168.196.127.137 | 🌐 ציבורי (iptv-org) | 5 |
| `hlspackager.akamaized.net` | hlspackager.akamaized.net | 🌐 ציבורי (iptv-org) | 5 |
| `video1.getstreamhosting.com` | video1.getstreamhosting.com | 🌐 ציבורי (iptv-org) | 5 |
| `mdstrm.com` | mdstrm.com | 🌐 ציבורי (iptv-org) | 5 |
| `livefta.malimarcdn.com` | livefta.malimarcdn.com | 🌐 ציבורי (iptv-org) | 5 |
| `mn-nl.mncdn.com` | mn-nl.mncdn.com | 🌐 ציבורי (iptv-org) | 5 |
| `livestream.pbskids.org` | livestream.pbskids.org | 🌐 ציבורי (iptv-org) | 5 |
| `drk6xq0vhn.gpcdn.net` | drk6xq0vhn.gpcdn.net | 🌐 ציבורי (iptv-org) | 5 |
| `cdn-live.dltv.ac.th` | cdn-live.dltv.ac.th | 🌐 ציבורי (iptv-org) | 5 |
| `cast.breslevforyou.co.il` | רדיו ברסלב – שרת התחנה | ✅ רשמי | 5 |
| `streams2.sofast.tv` | streams2.sofast.tv | 🌐 ציבורי (iptv-org) | 4 |
| `lbgo.bozztv.com` | lbgo.bozztv.com | 🌐 ציבורי (iptv-org) | 4 |
| `cdn3.wowza.com` | cdn3.wowza.com | 🌐 ציבורי (iptv-org) | 4 |
| `dash4.antik.sk` | dash4.antik.sk | 🌐 ציבורי (iptv-org) | 4 |
| `hls.newworldtv.com` | hls.newworldtv.com | 🌐 ציבורי (iptv-org) | 4 |
| `tgn.bozztv.com` | tgn.bozztv.com | 🌐 ציבורי (iptv-org) | 4 |
| `svs.itworkscdn.net` | Asharq Discovery – ItWorks (רשמי) | ✅ רשמי | 4 |
| `edge.enhdtv.com` | edge.enhdtv.com | 🌐 ציבורי (iptv-org) | 4 |
| `w2.manasat.com` | w2.manasat.com | 🌐 ציבורי (iptv-org) | 4 |
| `live-fi.tvkaista.net` | live-fi.tvkaista.net | 🌐 ציבורי (iptv-org) | 4 |
| `fl.biztv.media` | fl.biztv.media | 🌐 ציבורי (iptv-org) | 4 |
| `playlist.fasttvcdn.com` | playlist.fasttvcdn.com | 🌐 ציבורי (iptv-org) | 4 |
| `ebsonair.ebs.co.kr` | ebsonair.ebs.co.kr | 🌐 ציבורי (iptv-org) | 4 |
| `edge12.vedge.infomaniak.com` | edge12.vedge.infomaniak.com | 🌐 ציבורי (iptv-org) | 4 |
| `cdn4.mjunoon.tv` | cdn4.mjunoon.tv | 🌐 ציבורי (iptv-org) | 4 |
| `85.237.89.160` | 85.237.89.160 | ⚠️ ממסר צד שלישי | 4 |
| `cdna.streamgates.net` | StreamGates – ה-CDN של התחנה | ✅ רשמי | 4 |
| `icecast.kab.tv` | icecast.kab.tv | ⚠️ ממסר צד שלישי | 4 |
| `kancdn.medonecdn.net` | כאן – תאגיד השידור (CDN רשמי) | ✅ רשמי | 3 |
| `r.il.cdn-redge.media` | Redge – ה-CDN הרשמי של כאן, ערוץ 14 וכלכלה 10 | ✅ רשמי | 3 |
| `rnttwmjcin.turknet.ercdn.net` | rnttwmjcin.turknet.ercdn.net | 🌐 ציבורי (iptv-org) | 3 |
| `158.172.155.130` | 158.172.155.130 | 🌐 ציבורי (iptv-org) | 3 |
| `5c7b683162943.streamlock.net` | 5c7b683162943.streamlock.net | 🌐 ציבורי (iptv-org) | 3 |
| `streaming.onnethn.com` | streaming.onnethn.com | 🌐 ציבורי (iptv-org) | 3 |
| `master.tucableip.com` | master.tucableip.com | 🌐 ציבורי (iptv-org) | 3 |
| `d4whmvwm0rdvi.cloudfront.net` | d4whmvwm0rdvi.cloudfront.net | 🌐 ציבורי (iptv-org) | 3 |
| `fastly.live.brightcove.com` | fastly.live.brightcove.com | 🌐 ציבורי (iptv-org) | 3 |
| `team-tv.co` | team-tv.co | 🌐 ציבורי (iptv-org) | 3 |
| `edge20.vedge.infomaniak.com` | edge20.vedge.infomaniak.com | 🌐 ציבורי (iptv-org) | 3 |
| `live.alarabiya.net` | live.alarabiya.net | 🌐 ציבורי (iptv-org) | 3 |
| `5fc584f3f19c9.streamlock.net` | 5fc584f3f19c9.streamlock.net | 🌐 ציבורי (iptv-org) | 3 |
| `vodzong.mjunoon.tv` | vodzong.mjunoon.tv | 🌐 ציבורי (iptv-org) | 3 |
| `cdn.live.easybroadcast.io` | cdn.live.easybroadcast.io | 🌐 ציבורי (iptv-org) | 3 |
| `viewmedia7219.bozztv.com` | viewmedia7219.bozztv.com | 🌐 ציבורי (iptv-org) | 3 |
| `eu1.servers10.com` | eu1.servers10.com | 🌐 ציבורי (iptv-org) | 3 |
| `stream.berosat.live` | stream.berosat.live | 🌐 ציבורי (iptv-org) | 3 |
| `directes-tv-int.3catdirectes.cat` | directes-tv-int.3catdirectes.cat | 🌐 ציבורי (iptv-org) | 3 |
| `cdn15.live-tv.cloud` | live-tv.cloud – ספק IPTV אוקראיני | ⚠️ ממסר צד שלישי | 3 |
| `fastlive.cctvplus.com` | fastlive.cctvplus.com | 🌐 ציבורי (iptv-org) | 3 |
| `103.151.60.162` | 103.151.60.162 | 🌐 ציבורי (iptv-org) | 3 |
| `103.172.187.30` | 103.172.187.30 | 🌐 ציבורי (iptv-org) | 3 |
| `203.81.84.130` | 203.81.84.130 | 🌐 ציבורי (iptv-org) | 3 |
| `raw.githubusercontent.com` | raw.githubusercontent.com | 🌐 ציבורי (iptv-org) | 3 |
| `stream1.antenaplay.ro` | stream1.antenaplay.ro | 🌐 ציבורי (iptv-org) | 3 |
| `190.61.47.54` | 190.61.47.54 | 🌐 ציבורי (iptv-org) | 3 |
| `stmv1.srvif.com` | stmv1.srvif.com | 🌐 ציבורי (iptv-org) | 3 |
| `200.115.120.1` | 200.115.120.1 | 🌐 ציבורי (iptv-org) | 3 |
| `fs.uplink.kz` | fs.uplink.kz | 🌐 ציבורי (iptv-org) | 3 |
| `da86m1sqpm3o0.cloudfront.net` | da86m1sqpm3o0.cloudfront.net | 🌐 ציבורי (iptv-org) | 3 |
| `138.121.15.230` | 138.121.15.230 | 🌐 ציבורי (iptv-org) | 3 |
| `30a-tv.com` | 30a-tv.com | 🌐 ציבורי (iptv-org) | 3 |
| `cdnlive.klicgo.net` | cdnlive.klicgo.net | 🌐 ציבורי (iptv-org) | 3 |
| `cloudvideo.servers10.com` | cloudvideo.servers10.com | 🌐 ציבורי (iptv-org) | 3 |
| `sb.err.ee` | sb.err.ee | 🌐 ציבורי (iptv-org) | 3 |
| `185.227.34.179` | 185.227.34.179 | 🌐 ציבורי (iptv-org) | 3 |
| `lotus.stingray.com` | lotus.stingray.com | 🌐 ציבורי (iptv-org) | 3 |
| `livecdn.live247stream.com` | livecdn.live247stream.com | 🌐 ציבורי (iptv-org) | 3 |
| `live2.tensila.com` | live2.tensila.com | 🌐 ציבורי (iptv-org) | 3 |
| `stream.nasatv.com.mk` | stream.nasatv.com.mk | 🌐 ציבורי (iptv-org) | 3 |
| `stream.sen-gt.com` | stream.sen-gt.com | 🌐 ציבורי (iptv-org) | 3 |
| `tls-cl.cdnz.cl` | tls-cl.cdnz.cl | 🌐 ציבורי (iptv-org) | 3 |
| `movil.ejeserver.com` | movil.ejeserver.com | 🌐 ציבורי (iptv-org) | 3 |
| `aegis-cloudfront-1.tubi.video` | aegis-cloudfront-1.tubi.video | 🌐 ציבורי (iptv-org) | 3 |
| `stream.swamiji.tv` | stream.swamiji.tv | 🌐 ציבורי (iptv-org) | 3 |
| `cdn01.setar.aw` | cdn01.setar.aw | 🌐 ציבורי (iptv-org) | 3 |
| `178.134.1.158` | 178.134.1.158 | 🌐 ציבורי (iptv-org) | 3 |
| `videostream.shockmedia.com.ar` | videostream.shockmedia.com.ar | 🌐 ציבורי (iptv-org) | 3 |
| `edge13.vedge.infomaniak.com` | edge13.vedge.infomaniak.com | 🌐 ציבורי (iptv-org) | 3 |
| `cdn.freevisiontv.co.za` | cdn.freevisiontv.co.za | 🌐 ציבורי (iptv-org) | 3 |
| `jointil.com` | jointil.com | ⚠️ ממסר צד שלישי | 3 |
| `kneset.gostreaming.tv` | ערוץ הכנסת – השידור של הכנסת (GoStreaming) | ✅ רשמי | 2 |
| `rgelive.akamaized.net` | ספורט 5 – השידור הרשמי של RGE (Akamai) | ✅ רשמי | 2 |
| `d3qs3d2rkhfqrt.cloudfront.net` | d3qs3d2rkhfqrt.cloudfront.net | 🌐 ציבורי (iptv-org) | 2 |
| `live4dai.antenaplay.ro` | live4dai.antenaplay.ro | 🌐 ציבורי (iptv-org) | 2 |
| `partneta.cdn.mgmlcdn.com` | partneta.cdn.mgmlcdn.com | 🌐 ציבורי (iptv-org) | 2 |
| `stream3.cinerama.uz` | stream3.cinerama.uz | 🌐 ציבורי (iptv-org) | 2 |
| `live.mnb.mn` | live.mnb.mn | 🌐 ציבורי (iptv-org) | 2 |
| `d3k8wzt41aflvx.cloudfront.net` | d3k8wzt41aflvx.cloudfront.net | 🌐 ציבורי (iptv-org) | 2 |
| `samitaorigin.tangotv.in` | samitaorigin.tangotv.in | 🌐 ציבורי (iptv-org) | 2 |
| `unlimited6-cl.dps.live` | unlimited6-cl.dps.live | 🌐 ציבורי (iptv-org) | 2 |
| `mumbai-edge.smartplaytv.in` | mumbai-edge.smartplaytv.in | 🌐 ציבורי (iptv-org) | 2 |
| `tvsen5.aynaott.com` | tvsen5.aynaott.com | 🌐 ציבורי (iptv-org) | 2 |
| `skynewsau-live-premium.akamaized.net` | skynewsau-live-premium.akamaized.net | 🌐 ציבורי (iptv-org) | 2 |
| `streams.prd.commavservices.eu` | streams.prd.commavservices.eu | 🌐 ציבורי (iptv-org) | 2 |
| `ss2.tvrdomi.com` | ss2.tvrdomi.com | 🌐 ציבורי (iptv-org) | 2 |
| `jmc-live.ercdn.net` | jmc-live.ercdn.net | 🌐 ציבורי (iptv-org) | 2 |
| `ptvlive.kordia.net.nz` | ptvlive.kordia.net.nz | 🌐 ציבורי (iptv-org) | 2 |
| `partwota.cdn.mgmlcdn.com` | partwota.cdn.mgmlcdn.com | 🌐 ציבורי (iptv-org) | 2 |
| `gjirafa-video-live.gjirafa.net` | gjirafa-video-live.gjirafa.net | 🌐 ציבורי (iptv-org) | 2 |
| `africa24.vedge.infomaniak.com` | africa24.vedge.infomaniak.com | 🌐 ציבורי (iptv-org) | 2 |
| `live-vgtrksmotrim.cdnvideo.ru` | live-vgtrksmotrim.cdnvideo.ru | 🌐 ציבורי (iptv-org) | 2 |
| `citynewsregional.akamaized.net` | citynewsregional.akamaized.net | 🌐 ציבורי (iptv-org) | 2 |
| `cdn.live.br1.jmvstream.com` | cdn.live.br1.jmvstream.com | 🌐 ציבורי (iptv-org) | 2 |
| `cdn0-03837-liveedge0.dna.ip-only.net` | cdn0-03837-liveedge0.dna.ip-only.net | 🌐 ציבורי (iptv-org) | 2 |
| `live.obslivestream.com` | live.obslivestream.com | 🌐 ציבורי (iptv-org) | 2 |
| `edge.essastream.com` | edge.essastream.com | 🌐 ציבורי (iptv-org) | 2 |
| `live1.acangroup.org` | live1.acangroup.org | 🌐 ציבורי (iptv-org) | 2 |
| `fox.hostlagarto.com` | fox.hostlagarto.com | 🌐 ציבורי (iptv-org) | 2 |
| `d1hz1p2gea2zy4.cloudfront.net` | d1hz1p2gea2zy4.cloudfront.net | 🌐 ציבורי (iptv-org) | 2 |
| `2-fss-2.streamhoster.com` | 2-fss-2.streamhoster.com | 🌐 ציבורי (iptv-org) | 2 |
| `tvsen6.aynaott.com` | tvsen6.aynaott.com | 🌐 ציבורי (iptv-org) | 2 |
| `dai.google.com` | dai.google.com | 🌐 ציבורי (iptv-org) | 2 |
| `cdn.streamhispanatv.net` | cdn.streamhispanatv.net | 🌐 ציבורי (iptv-org) | 2 |
| `cnn.hostlagarto.com` | cnn.hostlagarto.com | 🌐 ציבורי (iptv-org) | 2 |
| `stream.qazcdn.net` | stream.qazcdn.net | 🌐 ציבורי (iptv-org) | 2 |
| `15.204.196.147` | 15.204.196.147 | 🌐 ציבורי (iptv-org) | 2 |
| `canal.mediaserver.com.co` | canal.mediaserver.com.co | 🌐 ציבורי (iptv-org) | 2 |
| `fast-channels.sinclairstoryline.com` | fast-channels.sinclairstoryline.com | 🌐 ציבורי (iptv-org) | 2 |
| `45.162.64.114` | 45.162.64.114 | 🌐 ציבורי (iptv-org) | 2 |
| `hls.botflix.tv` | hls.botflix.tv | 🌐 ציבורי (iptv-org) | 2 |
| `streamer1.streamhost.org` | streamer1.streamhost.org | 🌐 ציבורי (iptv-org) | 2 |
| `138.59.227.20` | 138.59.227.20 | 🌐 ציבורי (iptv-org) | 2 |
| `stream.4fun.tv` | 4FUN TV – שרת רשמי | ✅ רשמי | 2 |
| `hls01ns.antenaplay.ro` | hls01ns.antenaplay.ro | 🌐 ציבורי (iptv-org) | 2 |
| `s.emisoras.tv` | s.emisoras.tv | 🌐 ציבורי (iptv-org) | 2 |
| `c.mjh.nz` | c.mjh.nz | 🌐 ציבורי (iptv-org) | 2 |
| `170.83.49.66` | 170.83.49.66 | 🌐 ציבורי (iptv-org) | 2 |
| `tv.wracanal10.com` | tv.wracanal10.com | 🌐 ציבורי (iptv-org) | 2 |
| `585b674743bbb.streamlock.net` | 70-80 TV – Wowza Streamlock | ✅ רשמי | 2 |
| `channels.trace.plus` | channels.trace.plus | 🌐 ציבורי (iptv-org) | 2 |
| `tv1.intv.mk` | tv1.intv.mk | 🌐 ציבורי (iptv-org) | 2 |
| `cdn.streamlane.tv` | cdn.streamlane.tv | 🌐 ציבורי (iptv-org) | 2 |
| `livetv.powerapp.com.tr` | livetv.powerapp.com.tr | 🌐 ציבורי (iptv-org) | 2 |
| `live.vrtcdn.be` | live.vrtcdn.be | 🌐 ציבורי (iptv-org) | 2 |
| `60efd7a2b4d02.streamlock.net` | 60efd7a2b4d02.streamlock.net | 🌐 ציבורי (iptv-org) | 2 |
| `m1b2.worldcast.tv` | m1b2.worldcast.tv | 🌐 ציבורי (iptv-org) | 2 |
| `bss1.neterra.tv` | bss1.neterra.tv | 🌐 ציבורי (iptv-org) | 2 |
| `peer2.tdiradio.com` | peer2.tdiradio.com | 🌐 ציבורי (iptv-org) | 2 |
| `stream.rtl.lu` | stream.rtl.lu | 🌐 ציבורי (iptv-org) | 2 |
| `rp.tactivemedia.com` | rp.tactivemedia.com | 🌐 ציבורי (iptv-org) | 2 |
| `rds3.desdeparaguay.net` | rds3.desdeparaguay.net | 🌐 ציבורי (iptv-org) | 2 |
| `vdo2.pro-fhi.net` | vdo2.pro-fhi.net | 🌐 ציבורי (iptv-org) | 2 |
| `video0.rogohosting.com` | video0.rogohosting.com | 🌐 ציבורי (iptv-org) | 2 |
| `5ed325193d4e1.streamlock.net` | 5ed325193d4e1.streamlock.net | 🌐 ציבורי (iptv-org) | 2 |
| `live.artidijitalmedya.com` | live.artidijitalmedya.com | 🌐 ציבורי (iptv-org) | 2 |
| `streamingcws30.com` | streamingcws30.com | 🌐 ציבורי (iptv-org) | 2 |
| `video.hostingcaaguazu.com` | video.hostingcaaguazu.com | 🌐 ציבורי (iptv-org) | 2 |
| `live.koryocdn.org` | live.koryocdn.org | 🌐 ציבורי (iptv-org) | 2 |
| `5ca9af4645e15.streamlock.net` | 5ca9af4645e15.streamlock.net | 🌐 ציבורי (iptv-org) | 2 |
| `stream8.cinerama.uz` | Cinerama.uz – ספק IPTV באוזבקיסטן | ⚠️ ממסר צד שלישי | 2 |
| `videostream.chileservidores.com` | videostream.chileservidores.com | 🌐 ציבורי (iptv-org) | 2 |
| `livestream.telvue.com` | livestream.telvue.com | 🌐 ציבורי (iptv-org) | 2 |
| `60417ddeaf0d9.streamlock.net` | 60417ddeaf0d9.streamlock.net | 🌐 ציבורי (iptv-org) | 2 |
| `cdn2.invivo.bg` | cdn2.invivo.bg | 🌐 ציבורי (iptv-org) | 2 |
| `5c46fa289c89f.streamlock.net` | 5c46fa289c89f.streamlock.net | 🌐 ציבורי (iptv-org) | 2 |
| `streaming.aurora.enhanced.live` | streaming.aurora.enhanced.live | 🌐 ציבורי (iptv-org) | 2 |
| `ncdn-live-bfm.pfd.sfr.net` | BFM – SFR (רשמי) | ✅ רשמי | 2 |
| `strhlslb01.streamakaci.tv` | strhlslb01.streamakaci.tv | 🌐 ציבורי (iptv-org) | 2 |
| `2-fss-1.streamhoster.com` | 2-fss-1.streamhoster.com | 🌐 ציבורי (iptv-org) | 2 |
| `mumt03.tangotv.in` | mumt03.tangotv.in | 🌐 ציבורי (iptv-org) | 2 |
| `23.237.104.106` | 23.237.104.106 | 🌐 ציבורי (iptv-org) | 2 |
| `3abn.bozztv.com` | 3abn.bozztv.com | 🌐 ציבורי (iptv-org) | 2 |
| `23.239.31.26` | 23.239.31.26 | ⚠️ ממסר צד שלישי | 2 |
| `181.78.197.59` | 181.78.197.59 | ⚠️ ממסר צד שלישי | 2 |
| `atomic.streamnet.ro` | atomic.streamnet.ro | ⚠️ ממסר צד שלישי | 2 |
| `rtmp.streams.ovh` | rtmp.streams.ovh | ⚠️ ממסר צד שלישי | 2 |
| `radio.streamgates.net` | StreamGates – ה-CDN של התחנה | ✅ רשמי | 2 |
| `media2.93fm.co.il` | קול חי – שרת התחנה | ✅ רשמי | 2 |
| `live.ecast.co.il` | eCast – שרת התחנה | ✅ רשמי | 2 |
| `d18b0e6mopany4.cloudfront.net` | רשת 13 – AWS CloudFront | ✅ רשמי | 1 |
| `i24newshebrew-cdn.encoders.immergo.tv` | i24NEWS – שרת רשמי | ✅ רשמי | 1 |
| `contact.gostreaming.tv` | GoStreaming – ספק השידור של ערוץ 9 | ✅ רשמי | 1 |
| `i24newsfrench-cdn.encoders.immergo.tv` | i24NEWS – שרת רשמי | ✅ רשמי | 1 |
| `i24newsarabic-cdn.encoders.immergo.tv` | i24NEWS – שרת רשמי | ✅ רשמי | 1 |
| `i24newsenglish-cdn.encoders.immergo.tv` | i24NEWS – שרת רשמי | ✅ רשמי | 1 |
| `live.creacast.com` | live.creacast.com | ⚠️ ממסר צד שלישי | 1 |
| `ynet-live-01.ynet-pic1.yit.co.il` | ynet – השרת של ידיעות אחרונות | ✅ רשמי | 1 |
| `reshet.g-mana.live` | רשת 13 – השרת הרשמי (G-Mana) | ✅ רשמי | 1 |
| `rbmn-live.akamaized.net` | Red Bull TV – Akamai (רשמי) | ✅ רשמי | 1 |
| `raycom-accdn-firetv.amagi.tv` | raycom-accdn-firetv.amagi.tv | 🌐 ציבורי (iptv-org) | 1 |
| `af7a8b4e.wurl.com` | af7a8b4e.wurl.com | 🌐 ציבורי (iptv-org) | 1 |
| `mts1.iptv-playoutcenter.de` | mts1.iptv-playoutcenter.de | 🌐 ציבורי (iptv-org) | 1 |
| `webstream.multistream.it` | webstream.multistream.it | 🌐 ציבורי (iptv-org) | 1 |
| `d3pnbvng3bx2nj.cloudfront.net` | d3pnbvng3bx2nj.cloudfront.net | 🌐 ציבורי (iptv-org) | 1 |
| `138.204.86.4` | 138.204.86.4 | 🌐 ציבורי (iptv-org) | 1 |
| `live-manifest.production-public.tubi.io` | live-manifest.production-public.tubi.io | 🌐 ציבורי (iptv-org) | 1 |
| `stmv2.voxtvhd.com.br` | stmv2.voxtvhd.com.br | 🌐 ציבורי (iptv-org) | 1 |
| `h.jcp.live.opencaster.com` | h.jcp.live.opencaster.com | 🌐 ציבורי (iptv-org) | 1 |
| `live-us1.thaimomo.com` | live-us1.thaimomo.com | 🌐 ציבורי (iptv-org) | 1 |
| `cdn.rabta.stream` | cdn.rabta.stream | 🌐 ציבורי (iptv-org) | 1 |
| `149.100.11.252` | 149.100.11.252 | 🌐 ציבורי (iptv-org) | 1 |
| `kwtspta.cdn.mangomolo.com` | kwtspta.cdn.mangomolo.com | 🌐 ציבורי (iptv-org) | 1 |
| `cast.talenttv.lk` | cast.talenttv.lk | 🌐 ציבורי (iptv-org) | 1 |
| `imn-live.esite-lab.com` | imn-live.esite-lab.com | 🌐 ציבורי (iptv-org) | 1 |
| `live.elheddaftv.com` | live.elheddaftv.com | 🌐 ציבורי (iptv-org) | 1 |
| `jrtv-live.ercdn.net` | jrtv-live.ercdn.net | 🌐 ציבורי (iptv-org) | 1 |
| `cdn.bestream.io` | cdn.bestream.io | 🌐 ציבורי (iptv-org) | 1 |
| `streamtv.as3sport.online` | streamtv.as3sport.online | 🌐 ציבורי (iptv-org) | 1 |
| `190.61.90.17` | 190.61.90.17 | 🌐 ציבורי (iptv-org) | 1 |
| `lightning-tracesport-samsungau.amagi.tv` | lightning-tracesport-samsungau.amagi.tv | 🌐 ציבורי (iptv-org) | 1 |
| `mainstreammedia-worldoffreesportsintl-rakuten.amagi.tv` | mainstreammedia-worldoffreesportsintl-rakuten.amagi.tv | 🌐 ציבורי (iptv-org) | 1 |
| `streaming.astrakhan.ru` | streaming.astrakhan.ru | 🌐 ציבורי (iptv-org) | 1 |
| `tjktv-live.tjk.org` | tjktv-live.tjk.org | 🌐 ציבורי (iptv-org) | 1 |
| `thm-it-roku.otteravision.com` | thm-it-roku.otteravision.com | 🌐 ציבורי (iptv-org) | 1 |
| `kanal75xto-llhls.akamaized.net` | kanal75xto-llhls.akamaized.net | 🌐 ציבורי (iptv-org) | 1 |
| `cdn2.ujjina.com` | cdn2.ujjina.com | 🌐 ציבורי (iptv-org) | 1 |
| `kwtsplta.cdn.mangomolo.com` | kwtsplta.cdn.mangomolo.com | 🌐 ציבורי (iptv-org) | 1 |
| `media1.skifnet.by` | media1.skifnet.by | 🌐 ציבורי (iptv-org) | 1 |
| `cnn.livestreaminggroup.info` | cnn.livestreaminggroup.info | 🌐 ציבורי (iptv-org) | 1 |
| `na.linear.zype.com` | na.linear.zype.com | 🌐 ציבורי (iptv-org) | 1 |
| `cffda8ff.wurl.com` | cffda8ff.wurl.com | 🌐 ציבורי (iptv-org) | 1 |
| `fighthls.persiana.live` | fighthls.persiana.live | 🌐 ציבורי (iptv-org) | 1 |
| `srvx1.selftv.video` | srvx1.selftv.video | 🌐 ציבורי (iptv-org) | 1 |
| `46.53.200.153` | 46.53.200.153 | 🌐 ציבורי (iptv-org) | 1 |
| `ciner.daioncdn.net` | ciner.daioncdn.net | 🌐 ציבורי (iptv-org) | 1 |
| `tvr-sport.lg.mncdn.com` | tvr-sport.lg.mncdn.com | 🌐 ציבורי (iptv-org) | 1 |
| `tvturf4.janus.cl` | tvturf4.janus.cl | 🌐 ציבורי (iptv-org) | 1 |
| `d3b6q2ou5kp8ke.cloudfront.net` | d3b6q2ou5kp8ke.cloudfront.net | 🌐 ציבורי (iptv-org) | 1 |
| `edge-001.streamup.eu` | edge-001.streamup.eu | 🌐 ציבורי (iptv-org) | 1 |
| `5.188.159.128` | 5.188.159.128 | 🌐 ציבורי (iptv-org) | 1 |
| `1hskrdto.rocketcdn.com` | 1hskrdto.rocketcdn.com | 🌐 ציבורי (iptv-org) | 1 |
| `amg02278-amg02278c2-flosports-worldwide-9916.playouts.now.amagi.tv` | amg02278-amg02278c2-flosports-worldwide-9916.playouts.now.amagi.tv | 🌐 ציבורי (iptv-org) | 1 |
| `rightsboosterltd-scl-1-be.samsung.wurl.tv` | rightsboosterltd-scl-1-be.samsung.wurl.tv | 🌐 ציבורי (iptv-org) | 1 |
| `amg02278-amg02278c1-flosports-worldwide-7592.playouts.now.amagi.tv` | amg02278-amg02278c1-flosports-worldwide-7592.playouts.now.amagi.tv | 🌐 ציבורי (iptv-org) | 1 |
| `livetr.teleromagna.it` | livetr.teleromagna.it | 🌐 ציבורי (iptv-org) | 1 |
| `d11k1mnrgfposz.cloudfront.net` | d11k1mnrgfposz.cloudfront.net | 🌐 ציבורי (iptv-org) | 1 |
| `amg00378-amg00378c4-freelivesports-emea-5637.playouts.now.amagi.tv` | amg00378-amg00378c4-freelivesports-emea-5637.playouts.now.amagi.tv | 🌐 ציבורי (iptv-org) | 1 |
| `rally-tv-live.akamaized.net` | rally-tv-live.akamaized.net | 🌐 ציבורי (iptv-org) | 1 |
| `sportsgrid-tribal.amagi.tv` | sportsgrid-tribal.amagi.tv | 🌐 ציבורי (iptv-org) | 1 |
| `linear-253.frequency.stream` | linear-253.frequency.stream | 🌐 ציבורי (iptv-org) | 1 |
| `dc1644a9jazgj.cloudfront.net` | dc1644a9jazgj.cloudfront.net | 🌐 ציבורי (iptv-org) | 1 |
| `6f972d29.wurl.com` | 6f972d29.wurl.com | 🌐 ציבורי (iptv-org) | 1 |
| `wurl120sports.global.transmit.live` | wurl120sports.global.transmit.live | 🌐 ציבורי (iptv-org) | 1 |
| `rightsboosterltd-scl-2-eu.rakuten.wurl.tv` | rightsboosterltd-scl-2-eu.rakuten.wurl.tv | 🌐 ציבורי (iptv-org) | 1 |
| `blacknewschannel-xumo-us.amagi.tv` | blacknewschannel-xumo-us.amagi.tv | 🌐 ציבורי (iptv-org) | 1 |
| `dwamdstream103.akamaized.net` | dwamdstream103.akamaized.net | 🌐 ציבורי (iptv-org) | 1 |
| `limited2.todostreaming.es` | limited2.todostreaming.es | 🌐 ציבורי (iptv-org) | 1 |
| `rt-glb.rttv.com` | rt-glb.rttv.com | 🌐 ציבורי (iptv-org) | 1 |
| `tv.hamdardtv.com` | tv.hamdardtv.com | 🌐 ציבורי (iptv-org) | 1 |
| `av.alarabiya.net` | av.alarabiya.net | 🌐 ציבורי (iptv-org) | 1 |
| `eazyvwqssi.erbvr.com` | eazyvwqssi.erbvr.com | 🌐 ציבורי (iptv-org) | 1 |
| `tv-trtworld.medya.trt.com.tr` | tv-trtworld.medya.trt.com.tr | 🌐 ציבורי (iptv-org) | 1 |
| `masterpl.hls.nhkworld.jp` | masterpl.hls.nhkworld.jp | 🌐 ציבורי (iptv-org) | 1 |
| `amg00405-rakutentv-cgtn-rakuten-i9tar.amagi.tv` | amg00405-rakutentv-cgtn-rakuten-i9tar.amagi.tv | 🌐 ציבורי (iptv-org) | 1 |
| `d25usgadhphvwi.cloudfront.net` | d25usgadhphvwi.cloudfront.net | 🌐 ציבורי (iptv-org) | 1 |
| `stream-gtlc.telecentro.net.ar` | stream-gtlc.telecentro.net.ar | 🌐 ציבורי (iptv-org) | 1 |
| `playout172.livextend.cloud` | playout172.livextend.cloud | 🌐 ציבורי (iptv-org) | 1 |
| `streaming.meteonews.net` | streaming.meteonews.net | 🌐 ציבורי (iptv-org) | 1 |
| `cdn-euronews.akamaized.net` | cdn-euronews.akamaized.net | 🌐 ציבורי (iptv-org) | 1 |
| `server.gointernet.gr` | server.gointernet.gr | 🌐 ציבורי (iptv-org) | 1 |
| `live.miracleonline.org` | live.miracleonline.org | 🌐 ציבורי (iptv-org) | 1 |
| `cdnapi.kaltura.com` | cdnapi.kaltura.com | 🌐 ציבורי (iptv-org) | 1 |
| `virta2.nopolanews.fi` | virta2.nopolanews.fi | 🌐 ציבורי (iptv-org) | 1 |
| `rfe-ingest.akamaized.net` | rfe-ingest.akamaized.net | 🌐 ציבורי (iptv-org) | 1 |
| `live.dottotv.ro` | live.dottotv.ro | 🌐 ציבורי (iptv-org) | 1 |
| `pantera1-100gb-cl-movistar.dps.live` | pantera1-100gb-cl-movistar.dps.live | 🌐 ציבורי (iptv-org) | 1 |
| `play.cdn.enetres.net` | play.cdn.enetres.net | 🌐 ציבורי (iptv-org) | 1 |
| `lnd0t3b922.tenbytecdn.com` | lnd0t3b922.tenbytecdn.com | 🌐 ציבורי (iptv-org) | 1 |
| `live.x2.co.th` | live.x2.co.th | 🌐 ציבורי (iptv-org) | 1 |
| `iptv.mihantv.com` | iptv.mihantv.com | 🌐 ציבורי (iptv-org) | 1 |
| `cdnstack.internetmultimediaonline.org` | cdnstack.internetmultimediaonline.org | 🌐 ציבורי (iptv-org) | 1 |
| `indexforce.com` | indexforce.com | 🌐 ציבורי (iptv-org) | 1 |
| `live.streamhch.com` | live.streamhch.com | 🌐 ציבורי (iptv-org) | 1 |
| `live1.mediadesk.al` | live1.mediadesk.al | 🌐 ציבורי (iptv-org) | 1 |
| `halabtoday-live.lg.mncdn.com` | halabtoday-live.lg.mncdn.com | 🌐 ציבורי (iptv-org) | 1 |
| `streaming.canal-i.com` | streaming.canal-i.com | 🌐 ציבורי (iptv-org) | 1 |
| `rtmp.baku.tv` | rtmp.baku.tv | 🌐 ציבורי (iptv-org) | 1 |
| `kwtktv2ta.cdn.mangomolo.com` | kwtktv2ta.cdn.mangomolo.com | 🌐 ציבורי (iptv-org) | 1 |
| `live.cdnbridge.tv` | live.cdnbridge.tv | 🌐 ציבורי (iptv-org) | 1 |
| `streaming.najah.edu` | streaming.najah.edu | 🌐 ציבורי (iptv-org) | 1 |
| `streamingav.asambleanacional.gob.ec` | streamingav.asambleanacional.gob.ec | 🌐 ציבורי (iptv-org) | 1 |
| `ekusheyserver.com` | ekusheyserver.com | 🌐 ציבורי (iptv-org) | 1 |
| `c4635.cdn.xsg.ge` | c4635.cdn.xsg.ge | 🌐 ציבורי (iptv-org) | 1 |
| `cachestar.privesc.eu` | cachestar.privesc.eu | 🌐 ציבורי (iptv-org) | 1 |
| `d2e1asnsl7br7b.cloudfront.net` | d2e1asnsl7br7b.cloudfront.net | 🌐 ציבורי (iptv-org) | 1 |
| `cdn-edge1.streamcomedia.com` | cdn-edge1.streamcomedia.com | 🌐 ציבורי (iptv-org) | 1 |
| `starmenajo.com` | starmenajo.com | 🌐 ציבורי (iptv-org) | 1 |
| `hls.dabangasudan.org` | hls.dabangasudan.org | 🌐 ציבורי (iptv-org) | 1 |
| `live.saharamedias.net` | live.saharamedias.net | 🌐 ציבורי (iptv-org) | 1 |
| `cdn.haititivi.com` | cdn.haititivi.com | 🌐 ציבורי (iptv-org) | 1 |
| `webtv.monacoinfo.com` | webtv.monacoinfo.com | 🌐 ציבורי (iptv-org) | 1 |
| `videohd.live` | videohd.live | 🌐 ציבורי (iptv-org) | 1 |
| `www.streaming507.net` | www.streaming507.net | 🌐 ציבורי (iptv-org) | 1 |
| `live-stream.dvb.no` | live-stream.dvb.no | 🌐 ציבורי (iptv-org) | 1 |
| `amtvusdvr.tulix.tv` | amtvusdvr.tulix.tv | 🌐 ציבורי (iptv-org) | 1 |
| `newsmaxadria.mts-si.tv` | newsmaxadria.mts-si.tv | 🌐 ציבורי (iptv-org) | 1 |
| `tvjadran.stream.agatin.hr` | tvjadran.stream.agatin.hr | 🌐 ציבורי (iptv-org) | 1 |
| `live.visir.is` | live.visir.is | 🌐 ציבורי (iptv-org) | 1 |
| `reflect-stream-bronxnet.cablecast.tv` | reflect-stream-bronxnet.cablecast.tv | 🌐 ציבורי (iptv-org) | 1 |
| `ukintl.dunyanews.tv` | ukintl.dunyanews.tv | 🌐 ציבורי (iptv-org) | 1 |
| `senato-live.morescreens.com` | senato-live.morescreens.com | 🌐 ציבורי (iptv-org) | 1 |
| `streamvideol1.luxnet.ua` | streamvideol1.luxnet.ua | 🌐 ציבורי (iptv-org) | 1 |
| `live-hls-7agy.livepush.io` | live-hls-7agy.livepush.io | 🌐 ציבורי (iptv-org) | 1 |
| `rch01e-alive-hls.akamaized.net` | rch01e-alive-hls.akamaized.net | 🌐 ציבורי (iptv-org) | 1 |
| `amdlive-ch02-ctnd-com.akamaized.net` | amdlive-ch02-ctnd-com.akamaized.net | 🌐 ציבורי (iptv-org) | 1 |
| `5e50264bd6766.streamlock.net` | 5e50264bd6766.streamlock.net | 🌐 ציבורי (iptv-org) | 1 |
| `vivo.solumedia.com` | vivo.solumedia.com | 🌐 ציבורי (iptv-org) | 1 |
| `github.com` | github.com | 🌐 ציבורי (iptv-org) | 1 |
| `stream.tvkstella.pl` | stream.tvkstella.pl | 🌐 ציבורי (iptv-org) | 1 |
| `ert-ucdn.broadpeak-aas.com` | ert-ucdn.broadpeak-aas.com | 🌐 ציבורי (iptv-org) | 1 |
| `stream-aleph.m.ro` | stream-aleph.m.ro | 🌐 ציבורי (iptv-org) | 1 |
| `redirector.dps.live` | redirector.dps.live | 🌐 ציבורי (iptv-org) | 1 |
| `stream1.bitred.cl` | stream1.bitred.cl | 🌐 ציבורי (iptv-org) | 1 |
| `lingkartv.my.id` | lingkartv.my.id | 🌐 ציבורי (iptv-org) | 1 |
| `tv-live.tpchannel.org` | tv-live.tpchannel.org | 🌐 ציבורי (iptv-org) | 1 |
| `imob.dunyanews.tv` | imob.dunyanews.tv | 🌐 ציבורי (iptv-org) | 1 |
| `sabconetanw.cdn.mangomolo.com` | sabconetanw.cdn.mangomolo.com | 🌐 ציבורי (iptv-org) | 1 |
| `wf.newscentral.ng` | wf.newscentral.ng | 🌐 ציבורי (iptv-org) | 1 |
| `sd343444.vodu.store` | sd343444.vodu.store | 🌐 ציבורי (iptv-org) | 1 |
| `www.desde-paraguay.com` | www.desde-paraguay.com | 🌐 ציבורי (iptv-org) | 1 |
| `lencatelevision.com` | lencatelevision.com | 🌐 ציבורי (iptv-org) | 1 |
| `protokolldns.xyz` | protokolldns.xyz | 🌐 ציבורי (iptv-org) | 1 |
| `mblenmain01.telesur.ultrabase.net` | mblenmain01.telesur.ultrabase.net | 🌐 ציבורי (iptv-org) | 1 |
| `n11.stv.livebox.sk` | n11.stv.livebox.sk | 🌐 ציבורי (iptv-org) | 1 |
| `stream.apa.tv` | stream.apa.tv | 🌐 ציבורי (iptv-org) | 1 |
| `streaming-android.freshnewsasia.tv` | streaming-android.freshnewsasia.tv | 🌐 ציבורי (iptv-org) | 1 |
| `kwtkbta.cdn.mangomolo.com` | kwtkbta.cdn.mangomolo.com | 🌐 ציבורי (iptv-org) | 1 |
| `live2.cdnbridge.tv` | live2.cdnbridge.tv | 🌐 ציבורי (iptv-org) | 1 |
| `htvmada.mada.ps` | htvmada.mada.ps | 🌐 ציבורי (iptv-org) | 1 |
| `ghaasiflu.online` | ghaasiflu.online | 🌐 ציבורי (iptv-org) | 1 |
| `ecoteltv.streamseguro.com` | ecoteltv.streamseguro.com | 🌐 ציבורי (iptv-org) | 1 |
| `103.190.133.68` | 103.190.133.68 | 🌐 ציבורי (iptv-org) | 1 |
| `live.palitranews.ge` | live.palitranews.ge | 🌐 ציבורי (iptv-org) | 1 |
| `realitatealive.md` | realitatealive.md | 🌐 ציבורי (iptv-org) | 1 |
| `hls.livecdn.io` | hls.livecdn.io | 🌐 ציבורי (iptv-org) | 1 |
| `hls.afintl.com` | hls.afintl.com | 🌐 ציבורי (iptv-org) | 1 |
| `5db313b643fd8.streamlock.net` | 5db313b643fd8.streamlock.net | 🌐 ציבורי (iptv-org) | 1 |
| `rt-arb.rttv.com` | rt-arb.rttv.com | 🌐 ציבורי (iptv-org) | 1 |
| `abc-news-dmd-streams-1.akamaized.net` | abc-news-dmd-streams-1.akamaized.net | 🌐 ציבורי (iptv-org) | 1 |
| `live-hls-web-aja2-gcp.thehlive.com` | live-hls-web-aja2-gcp.thehlive.com | 🌐 ציבורי (iptv-org) | 1 |
| `live.paltodaytv.com` | live.paltodaytv.com | 🌐 ציבורי (iptv-org) | 1 |
| `tv-trtarabi.medya.trt.com.tr` | tv-trtarabi.medya.trt.com.tr | 🌐 ציבורי (iptv-org) | 1 |
| `hlive.ktv.go.kr` | hlive.ktv.go.kr | 🌐 ציבורי (iptv-org) | 1 |
| `stream.sun0769.com` | stream.sun0769.com | 🌐 ציבורי (iptv-org) | 1 |
| `stream3.camara.gov.br` | stream3.camara.gov.br | 🌐 ציבורי (iptv-org) | 1 |
| `5f2c1b0d880e5.streamlock.net` | 5f2c1b0d880e5.streamlock.net | 🌐 ציבורי (iptv-org) | 1 |
| `stream.arcast.com.ar` | stream.arcast.com.ar | 🌐 ציבורי (iptv-org) | 1 |
| `telmaco.ascdn.broadpeak.io` | telmaco.ascdn.broadpeak.io | 🌐 ציבורי (iptv-org) | 1 |
| `edge-ar.rcs-rds.ro` | edge-ar.rcs-rds.ro | 🌐 ציבורי (iptv-org) | 1 |
| `inforedvos.lcdn.claro.net.co` | inforedvos.lcdn.claro.net.co | 🌐 ציבורי (iptv-org) | 1 |
| `luna-4-video.mediaserver.digital` | luna-4-video.mediaserver.digital | 🌐 ציבורי (iptv-org) | 1 |
| `edge.medcom.id` | edge.medcom.id | 🌐 ציבורי (iptv-org) | 1 |
| `live.topnews.co.th` | live.topnews.co.th | 🌐 ציבורי (iptv-org) | 1 |
| `vcdn.dunyanews.tv` | vcdn.dunyanews.tv | 🌐 ציבורי (iptv-org) | 1 |
| `sabctretalh.cdn.mangomolo.com` | sabctretalh.cdn.mangomolo.com | 🌐 ציבורי (iptv-org) | 1 |
| `nanduti.b-cdn.net` | nanduti.b-cdn.net | 🌐 ציבורי (iptv-org) | 1 |
| `tdameritrade-vizio.amagi.tv` | tdameritrade-vizio.amagi.tv | 🌐 ציבורי (iptv-org) | 1 |
| `live-cdn-stream-euw1.bfmb.bct.nextradiotv.com` | live-cdn-stream-euw1.bfmb.bct.nextradiotv.com | 🌐 ציבורי (iptv-org) | 1 |
| `streaming013.gestec-video.com` | streaming013.gestec-video.com | 🌐 ציבורי (iptv-org) | 1 |
| `tv-streaming.bsh.ru` | tv-streaming.bsh.ru | 🌐 ציבורי (iptv-org) | 1 |
| `d9quh89lh7dtw.cloudfront.net` | d9quh89lh7dtw.cloudfront.net | 🌐 ציבורי (iptv-org) | 1 |
| `live-news.asharq.com` | live-news.asharq.com | 🌐 ציבורי (iptv-org) | 1 |
| `tv.ensonhaber.com` | tv.ensonhaber.com | 🌐 ציבורי (iptv-org) | 1 |
| `s-pl-01.mediatool.tv` | s-pl-01.mediatool.tv | 🌐 ציבורי (iptv-org) | 1 |
| `streamw.m.ro` | streamw.m.ro | 🌐 ציבורי (iptv-org) | 1 |
| `amg01529-defiancetv-defiancemedia-glewed-tre8x.amagi.tv` | amg01529-defiancetv-defiancemedia-glewed-tre8x.amagi.tv | 🌐 ציבורי (iptv-org) | 1 |
| `tv3.bitstreaming.net` | tv3.bitstreaming.net | 🌐 ציבורי (iptv-org) | 1 |
| `www.khuwemultimedia.com` | www.khuwemultimedia.com | 🌐 ציבורי (iptv-org) | 1 |
| `bloomberg.com` | bloomberg.com | 🌐 ציבורי (iptv-org) | 1 |
| `cnbc-live.akamaized.net` | cnbc-live.akamaized.net | 🌐 ציבורי (iptv-org) | 1 |
| `hnpsechtsc.turknet.ercdn.net` | hnpsechtsc.turknet.ercdn.net | 🌐 ציבורי (iptv-org) | 1 |
| `jobtvserver.eu` | jobtvserver.eu | 🌐 ציבורי (iptv-org) | 1 |
| `d8975dc3.wurl.com` | d8975dc3.wurl.com | 🌐 ציבורי (iptv-org) | 1 |
| `ndtvprofit.akamaized.net` | ndtvprofit.akamaized.net | 🌐 ציבורי (iptv-org) | 1 |
| `d1ewctnvcwvvvu.cloudfront.net` | d1ewctnvcwvvvu.cloudfront.net | 🌐 ציבורי (iptv-org) | 1 |
| `newscapital.com` | newscapital.com | 🌐 ציבורי (iptv-org) | 1 |
| `d2dffl3588mvfk.cloudfront.net` | ערוץ FAST ישראלי – AWS CloudFront | ✅ FAST חינמי | 1 |
| `b6cca454.wurl.com` | b6cca454.wurl.com | 🌐 ציבורי (iptv-org) | 1 |
| `zylo-cinenanar-rakuten.amagi.tv` | zylo-cinenanar-rakuten.amagi.tv | 🌐 ציבורי (iptv-org) | 1 |
| `amogonetworx-grjngo-3-dk.samsung.wurl.tv` | amogonetworx-grjngo-3-dk.samsung.wurl.tv | 🌐 ציבורי (iptv-org) | 1 |
| `d2mr4fu91mjx9m.cloudfront.net` | d2mr4fu91mjx9m.cloudfront.net | 🌐 ציבורי (iptv-org) | 1 |
| `59d7d6f47d7fc.streamlock.net` | 59d7d6f47d7fc.streamlock.net | 🌐 ציבורי (iptv-org) | 1 |
| `ml-pull-hwc.myco.io` | ml-pull-hwc.myco.io | 🌐 ציבורי (iptv-org) | 1 |
| `gcinemahls.wns.live` | gcinemahls.wns.live | 🌐 ציבורי (iptv-org) | 1 |
| `cdnb4u.wiseplayout.com` | cdnb4u.wiseplayout.com | 🌐 ציבורי (iptv-org) | 1 |
| `stream.hrbtv.net` | stream.hrbtv.net | 🌐 ציבורי (iptv-org) | 1 |
| `cc-hqw8u5r1nshjc.akamaized.net` | cc-hqw8u5r1nshjc.akamaized.net | 🌐 ציבורי (iptv-org) | 1 |
| `oly-ftvpc.otteravision.com` | oly-ftvpc.otteravision.com | 🌐 ציבורי (iptv-org) | 1 |
| `iptv.bitred.cl` | iptv.bitred.cl | 🌐 ציבורי (iptv-org) | 1 |
| `actionlive.siliconweb.com` | actionlive.siliconweb.com | 🌐 ציבורי (iptv-org) | 1 |
| `141.94.193.53` | 141.94.193.53 | 🌐 ציבורי (iptv-org) | 1 |
| `video.ejeserver.com` | video.ejeserver.com | 🌐 ציבורי (iptv-org) | 1 |
| `avaserieshls.wns.live` | avaserieshls.wns.live | 🌐 ציבורי (iptv-org) | 1 |
| `d1yl6bf4hrzbr0.cloudfront.net` | d1yl6bf4hrzbr0.cloudfront.net | 🌐 ציבורי (iptv-org) | 1 |
| `hls.maahtv.live` | hls.maahtv.live | 🌐 ציבורי (iptv-org) | 1 |
| `alvetv.com` | alvetv.com | 🌐 ציבורי (iptv-org) | 1 |
| `bbc-antiquesroadshowuk-1-us.roku.wurl.tv` | bbc-antiquesroadshowuk-1-us.roku.wurl.tv | 🌐 ציבורי (iptv-org) | 1 |
| `amg00793-amg00793c40-rakuten-es-5444.playouts.now.amagi.tv` | amg00793-amg00793c40-rakuten-es-5444.playouts.now.amagi.tv | 🌐 ציבורי (iptv-org) | 1 |
| `06cb85ad6ccb4a6b97f561e62d16ad3f.mediatailor.us-west-2.amazonaws.com` | 06cb85ad6ccb4a6b97f561e62d16ad3f.mediatailor.us-west-2.amazonaws.com | 🌐 ציבורי (iptv-org) | 1 |
| `appletree-mytimespain-rakuten.amagi.tv` | appletree-mytimespain-rakuten.amagi.tv | 🌐 ציבורי (iptv-org) | 1 |
| `cc-8hy4a26pz2uos.akamaized.net` | cc-8hy4a26pz2uos.akamaized.net | 🌐 ציבורי (iptv-org) | 1 |
| `appletree-mytime-samsungbrazil.amagi.tv` | appletree-mytime-samsungbrazil.amagi.tv | 🌐 ציבורי (iptv-org) | 1 |
| `web.onair-radio.eu` | web.onair-radio.eu | 🌐 ציבורי (iptv-org) | 1 |
| `origin.dpsgo.com` | origin.dpsgo.com | 🌐 ציבורי (iptv-org) | 1 |
| `slonehls.wns.live` | slonehls.wns.live | 🌐 ציבורי (iptv-org) | 1 |
| `appletree-mytimeuk-rakuten.amagi.tv` | appletree-mytimeuk-rakuten.amagi.tv | 🌐 ציבורי (iptv-org) | 1 |
| `49d735318d6b4c30a24a7997ea594e1b.mediatailor.us-west-2.amazonaws.com` | 49d735318d6b4c30a24a7997ea594e1b.mediatailor.us-west-2.amazonaws.com | 🌐 ציבורי (iptv-org) | 1 |
| `a9c57ec7ec5e4b7daeacc6316a0bb404.mediatailor.eu-west-1.amazonaws.com` | a9c57ec7ec5e4b7daeacc6316a0bb404.mediatailor.eu-west-1.amazonaws.com | 🌐 ציבורי (iptv-org) | 1 |
| `live-rian.cdnvideo.ru` | live-rian.cdnvideo.ru | 🌐 ציבורי (iptv-org) | 1 |
| `d3arbp6l7f096k.cloudfront.net` | d3arbp6l7f096k.cloudfront.net | 🌐 ציבורי (iptv-org) | 1 |
| `stmv1.video.brstream.com.br` | stmv1.video.brstream.com.br | 🌐 ציבורי (iptv-org) | 1 |
| `158.172.217.18` | 158.172.217.18 | 🌐 ציבורי (iptv-org) | 1 |
| `sltwohls.wns.live` | sltwohls.wns.live | 🌐 ציבורי (iptv-org) | 1 |
| `bbc-classicdrwho-1-us.roku.wurl.tv` | bbc-classicdrwho-1-us.roku.wurl.tv | 🌐 ציבורי (iptv-org) | 1 |
| `5098a8b860504a3690fd2e7c0a18d68f.mediatailor.us-west-2.amazonaws.com` | 5098a8b860504a3690fd2e7c0a18d68f.mediatailor.us-west-2.amazonaws.com | 🌐 ציבורי (iptv-org) | 1 |
| `71db867f03ce4d71a29e92155f07ab87.mediatailor.eu-west-1.amazonaws.com` | 71db867f03ce4d71a29e92155f07ab87.mediatailor.eu-west-1.amazonaws.com | 🌐 ציבורי (iptv-org) | 1 |
| `vglivessai.akamaized.net` | vglivessai.akamaized.net | 🌐 ציבורי (iptv-org) | 1 |
| `38.134.250.110` | 38.134.250.110 | 🌐 ציבורי (iptv-org) | 1 |
| `afrafhls.wns.live` | afrafhls.wns.live | 🌐 ציבורי (iptv-org) | 1 |
| `amg00276-amg00276c2-rakuten-uk-3449.playouts.now.amagi.tv` | amg00276-amg00276c2-rakuten-uk-3449.playouts.now.amagi.tv | 🌐 ציבורי (iptv-org) | 1 |
| `7aa9671895264ec9a384dfed1b992171.mediatailor.us-west-2.amazonaws.com` | 7aa9671895264ec9a384dfed1b992171.mediatailor.us-west-2.amazonaws.com | 🌐 ציבורי (iptv-org) | 1 |
| `a7089c89d85f453d850c4a1518b43076.mediatailor.eu-west-1.amazonaws.com` | a7089c89d85f453d850c4a1518b43076.mediatailor.eu-west-1.amazonaws.com | 🌐 ציבורי (iptv-org) | 1 |
| `livestream.rajtv.tv` | livestream.rajtv.tv | 🌐 ציבורי (iptv-org) | 1 |
| `cafefhls.wns.live` | cafefhls.wns.live | 🌐 ציבורי (iptv-org) | 1 |
| `d6dg3ebeih71x.cloudfront.net` | d6dg3ebeih71x.cloudfront.net | 🌐 ציבורי (iptv-org) | 1 |
| `cinehls.persiana.live` | cinehls.persiana.live | 🌐 ציבורי (iptv-org) | 1 |
| `b0d59c8c98974e708e5ccb9a27cdfdfc.mediatailor.eu-west-1.amazonaws.com` | b0d59c8c98974e708e5ccb9a27cdfdfc.mediatailor.eu-west-1.amazonaws.com | 🌐 ציבורי (iptv-org) | 1 |
| `cc-yw7ztecy8do3q.akamaized.net` | cc-yw7ztecy8do3q.akamaized.net | 🌐 ציבורי (iptv-org) | 1 |
| `170.83.16.50` | 170.83.16.50 | 🌐 ציבורי (iptv-org) | 1 |
| `clshls.wns.live` | clshls.wns.live | 🌐 ציבורי (iptv-org) | 1 |
| `bbc-impossible-1-us.roku.wurl.tv` | bbc-impossible-1-us.roku.wurl.tv | 🌐 ציבורי (iptv-org) | 1 |
| `familyhls.persiana.live` | familyhls.persiana.live | 🌐 ציבורי (iptv-org) | 1 |
| `ff335120300e4742a2b135ee9a9e7df8.mediatailor.eu-west-1.amazonaws.com` | ff335120300e4742a2b135ee9a9e7df8.mediatailor.eu-west-1.amazonaws.com | 🌐 ציבורי (iptv-org) | 1 |
| `167.249.23.102` | 167.249.23.102 | 🌐 ציבורי (iptv-org) | 1 |
| `moviethls.wns.live` | moviethls.wns.live | 🌐 ציבורי (iptv-org) | 1 |
| `maverick-maverick-black-cinema-3-us.roku.wurl.tv` | maverick-maverick-black-cinema-3-us.roku.wurl.tv | 🌐 ציבורי (iptv-org) | 1 |
| `irhls.persiana.live` | irhls.persiana.live | 🌐 ציבורי (iptv-org) | 1 |
| `fast-tailor.3catdirectes.cat` | fast-tailor.3catdirectes.cat | 🌐 ציבורי (iptv-org) | 1 |
| `sis-global.prod.samsungtv.plus` | sis-global.prod.samsungtv.plus | 🌐 ציבורי (iptv-org) | 1 |
| `metafilmhls.wns.live` | metafilmhls.wns.live | 🌐 ציבורי (iptv-org) | 1 |
| `all3media-midsomer-1-us.roku.wurl.tv` | all3media-midsomer-1-us.roku.wurl.tv | 🌐 ציבורי (iptv-org) | 1 |
| `korhls.persiana.live` | korhls.persiana.live | 🌐 ציבורי (iptv-org) | 1 |
| `janus.xpbroadcasting.com` | janus.xpbroadcasting.com | 🌐 ציבורי (iptv-org) | 1 |
| `vod.tuva.ru` | vod.tuva.ru | 🌐 ציבורי (iptv-org) | 1 |
| `appletree-mytime-samsungmexico.amagi.tv` | appletree-mytime-samsungmexico.amagi.tv | 🌐 ציבורי (iptv-org) | 1 |
| `ads.its-newid.net` | ads.its-newid.net | 🌐 ציבורי (iptv-org) | 1 |
| `euhls.persiana.live` | euhls.persiana.live | 🌐 ציבורי (iptv-org) | 1 |
| `livetv.tarangplus.in` | livetv.tarangplus.in | 🌐 ציבורי (iptv-org) | 1 |
| `d3o593mz1glx8d.cloudfront.net` | d3o593mz1glx8d.cloudfront.net | 🌐 ציבורי (iptv-org) | 1 |
| `onehls.persiana.live` | onehls.persiana.live | 🌐 ציבורי (iptv-org) | 1 |
| `live-bhojpuri.akamaized.net` | live-bhojpuri.akamaized.net | 🌐 ציבורי (iptv-org) | 1 |
| `thisoldhouse-2-us.roku.wurl.tv` | thisoldhouse-2-us.roku.wurl.tv | 🌐 ציבורי (iptv-org) | 1 |
| `92.63.196.180` | 92.63.196.180 | 🌐 ציבורי (iptv-org) | 1 |
| `xumo-xumoent-vc-107-xmuvk.fast.nbcuni.com` | xumo-xumoent-vc-107-xmuvk.fast.nbcuni.com | 🌐 ציבורי (iptv-org) | 1 |
| `vg-pitaaratvlive.akamaized.net` | vg-pitaaratvlive.akamaized.net | 🌐 ציבורי (iptv-org) | 1 |
| `d393sxaxig6bax.cloudfront.net` | d393sxaxig6bax.cloudfront.net | 🌐 ציבורי (iptv-org) | 1 |
| `mediaserver.abnvideos.com` | mediaserver.abnvideos.com | 🌐 ציבורי (iptv-org) | 1 |
| `d15ds134q59udk.cloudfront.net` | ערוץ FAST ישראלי – AWS CloudFront | ✅ FAST חינמי | 1 |
| `xumo-xumoent-vc-105-z0vpm.fast.nbcuni.com` | xumo-xumoent-vc-105-z0vpm.fast.nbcuni.com | 🌐 ציבורי (iptv-org) | 1 |
| `comedyhls.persiana.live` | comedyhls.persiana.live | 🌐 ציבורי (iptv-org) | 1 |
| `cc-wie8j8y69d2uy.akamaized.net` | cc-wie8j8y69d2uy.akamaized.net | 🌐 ציבורי (iptv-org) | 1 |
| `segment.yuppcdn.net` | segment.yuppcdn.net | 🌐 ציבורי (iptv-org) | 1 |
| `tvoetv.space` | tvoetv.space | 🌐 ציבורי (iptv-org) | 1 |
| `linear-12.frequency.stream` | linear-12.frequency.stream | 🌐 ציבורי (iptv-org) | 1 |
| `drybar-drybarcomedy-1-au.samsung.wurl.tv` | drybar-drybarcomedy-1-au.samsung.wurl.tv | 🌐 ציבורי (iptv-org) | 1 |
| `failarmy-international-au.samsung.wurl.tv` | failarmy-international-au.samsung.wurl.tv | 🌐 ציבורי (iptv-org) | 1 |
| `wurlrifftrax.global.transmit.live` | wurlrifftrax.global.transmit.live | 🌐 ציבורי (iptv-org) | 1 |
| `cdn.fast.jwp.services` | BabyFirst – JW Player FAST | ✅ FAST חינמי | 1 |
| `dltiqboxjw21d.cloudfront.net` | LEGO Channel – AWS CloudFront FAST | ✅ FAST חינמי | 1 |
| `newidco-babysharktv-1-us.roku.wurl.tv` | Baby Shark TV – Wurl (Roku FAST) | ✅ FAST חינמי | 1 |
| `moonbug-rokuus.amagi.tv` | moonbug-rokuus.amagi.tv | 🌐 ציבורי (iptv-org) | 1 |
| `d3b73b34o7cvkq.cloudfront.net` | d3b73b34o7cvkq.cloudfront.net | 🌐 ציבורי (iptv-org) | 1 |
| `kikageohls.akamaized.net` | kikageohls.akamaized.net | 🌐 ציבורי (iptv-org) | 1 |
| `stream.paroledivita.org` | stream.paroledivita.org | 🌐 ציבורי (iptv-org) | 1 |
| `5d23269b3ec0c.streamlock.net` | 5d23269b3ec0c.streamlock.net | 🌐 ציבורי (iptv-org) | 1 |
| `d1wal6k3d7ssin.cloudfront.net` | d1wal6k3d7ssin.cloudfront.net | 🌐 ציבורי (iptv-org) | 1 |
| `5aafcc5de91f1.streamlock.net` | 5aafcc5de91f1.streamlock.net | 🌐 ציבורי (iptv-org) | 1 |
| `5d658d7e9f562.streamlock.net` | 5d658d7e9f562.streamlock.net | 🌐 ציבורי (iptv-org) | 1 |
| `stream.starmenajo.com` | stream.starmenajo.com | 🌐 ציבורי (iptv-org) | 1 |
| `tv-trtcocuk.medya.trt.com.tr` | tv-trtcocuk.medya.trt.com.tr | 🌐 ציבורי (iptv-org) | 1 |
| `cc-t8lqe1o99pszu.akamaized.net` | cc-t8lqe1o99pszu.akamaized.net | 🌐 ציבורי (iptv-org) | 1 |
| `foxkidstv.be` | foxkidstv.be | 🌐 ציבורי (iptv-org) | 1 |
| `45.70.201.81` | 45.70.201.81 | 🌐 ציבורי (iptv-org) | 1 |
| `gist.githubusercontent.com` | gist.githubusercontent.com | 🌐 ציבורי (iptv-org) | 1 |
| `m6.livecdn.io` | m6.livecdn.io | 🌐 ציבורי (iptv-org) | 1 |
| `zoomoo-samsungau.amagi.tv` | zoomoo-samsungau.amagi.tv | 🌐 ציבורי (iptv-org) | 1 |
| `mkstream.servehttp.com` | mkstream.servehttp.com | 🌐 ציבורי (iptv-org) | 1 |
| `627bb251f23c7.streamlock.net` | 627bb251f23c7.streamlock.net | 🌐 ציבורי (iptv-org) | 1 |
| `balapantv-stream.qazcdn.com` | balapantv-stream.qazcdn.com | 🌐 ציבורי (iptv-org) | 1 |
| `dil9xdvretp0f.cloudfront.net` | dil9xdvretp0f.cloudfront.net | 🌐 ציבורי (iptv-org) | 1 |
| `junhls.persiana.live` | junhls.persiana.live | 🌐 ציבורי (iptv-org) | 1 |
| `rictv.iptv-playoutcenter.de` | rictv.iptv-playoutcenter.de | 🌐 ציבורי (iptv-org) | 1 |
| `boing.akamaized.net` | boing.akamaized.net | 🌐 ציבורי (iptv-org) | 1 |
| `cc-4bhi5osabejc9.akamaized.net` | cc-4bhi5osabejc9.akamaized.net | 🌐 ציבורי (iptv-org) | 1 |
| `pistream.ddns.net` | pistream.ddns.net | 🌐 ציבורי (iptv-org) | 1 |
| `online.godstands.tv` | online.godstands.tv | 🌐 ציבורי (iptv-org) | 1 |
| `65f16f0fdfc51.streamlock.net` | 65f16f0fdfc51.streamlock.net | 🌐 ציבורי (iptv-org) | 1 |
| `495c5a85d9074f29acffeaea9e0215eb.msvdn.net` | 495c5a85d9074f29acffeaea9e0215eb.msvdn.net | 🌐 ציבורי (iptv-org) | 1 |
| `radm.mediacdn.ru` | radm.mediacdn.ru | 🌐 ציבורי (iptv-org) | 1 |
| `server.zillarbarta.com` | server.zillarbarta.com | 🌐 ציבורי (iptv-org) | 1 |
| `bsttv.bonus-tv.ru` | bsttv.bonus-tv.ru | 🌐 ציבורי (iptv-org) | 1 |
| `tv-trtdiyanetcocuk.medya.trt.com.tr` | tv-trtdiyanetcocuk.medya.trt.com.tr | 🌐 ציבורי (iptv-org) | 1 |
| `live-saha.cdnvideo.ru` | live-saha.cdnvideo.ru | 🌐 ציבורי (iptv-org) | 1 |
| `yuppparoriglin.akamaized.net` | yuppparoriglin.akamaized.net | 🌐 ציבורי (iptv-org) | 1 |
| `toy.vcdn.fyi` | toy.vcdn.fyi | 🌐 ציבורי (iptv-org) | 1 |
| `mumt05.tangotv.in` | mumt05.tangotv.in | 🌐 ציבורי (iptv-org) | 1 |
| `linear-899.frequency.stream` | linear-899.frequency.stream | 🌐 ציבורי (iptv-org) | 1 |
| `janson-supersimplesongs-1-us.roku.wurl.tv` | janson-supersimplesongs-1-us.roku.wurl.tv | 🌐 ציבורי (iptv-org) | 1 |
| `d3868b4ny0rgdf.cloudfront.net` | d3868b4ny0rgdf.cloudfront.net | 🌐 ציבורי (iptv-org) | 1 |
| `d3i6upqaqzosi1.cloudfront.net` | d3i6upqaqzosi1.cloudfront.net | 🌐 ציבורי (iptv-org) | 1 |
| `stream.ads.ottera.tv` | stream.ads.ottera.tv | 🌐 ציבורי (iptv-org) | 1 |
| `vodcdn.bamboo-cloud.com` | vodcdn.bamboo-cloud.com | 🌐 ציבורי (iptv-org) | 1 |
| `176.118.197.101` | שרת IP ללא שם (1HD) | ⚠️ ממסר צד שלישי | 1 |
| `stream.ecable.tv` | stream.ecable.tv | 🌐 ציבורי (iptv-org) | 1 |
| `d-m-f.iptv-playoutcenter.de` | d-m-f.iptv-playoutcenter.de | 🌐 ציבורי (iptv-org) | 1 |
| `stream2.xdevel.com` | stream2.xdevel.com | 🌐 ציבורי (iptv-org) | 1 |
| `live-maidantv.cdnvideo.ru` | live-maidantv.cdnvideo.ru | 🌐 ציבורי (iptv-org) | 1 |
| `tv.mediacp.eu` | tv.mediacp.eu | 🌐 ציבורי (iptv-org) | 1 |
| `dbbv9umqcd7cs.cloudfront.net` | dbbv9umqcd7cs.cloudfront.net | 🌐 ציבורי (iptv-org) | 1 |
| `nogoumtv.nrpstream.com` | nogoumtv.nrpstream.com | 🌐 ציבורי (iptv-org) | 1 |
| `wiselp.wiseplayout.com` | wiselp.wiseplayout.com | 🌐 ציבורי (iptv-org) | 1 |
| `mgugaklive.nowcdn.co.kr` | mgugaklive.nowcdn.co.kr | 🌐 ציבורי (iptv-org) | 1 |
| `d1bl6tskrpq9ze.cloudfront.net` | d1bl6tskrpq9ze.cloudfront.net | 🌐 ציבורי (iptv-org) | 1 |
| `5ce9406b73c33.streamlock.net` | 5ce9406b73c33.streamlock.net | 🌐 ציבורי (iptv-org) | 1 |
| `1888165924.rsc.cdn77.org` | 1888165924.rsc.cdn77.org | 🌐 ציבורי (iptv-org) | 1 |
| `eco.streams.ovh` | eco.streams.ovh | 🌐 ציבורי (iptv-org) | 1 |
| `streamer.radiotaajuus.fi` | streamer.radiotaajuus.fi | 🌐 ציבורי (iptv-org) | 1 |
| `ocko-live.ssl.cdn.cra.cz` | ocko-live.ssl.cdn.cra.cz | 🌐 ציבורי (iptv-org) | 1 |
| `oxygenmusic.hu` | oxygenmusic.hu | 🌐 ציבורי (iptv-org) | 1 |
| `live.ondadigitaltv.com` | live.ondadigitaltv.com | 🌐 ציבורי (iptv-org) | 1 |
| `bozztv.com` | bozztv.com | 🌐 ציבורי (iptv-org) | 1 |
| `customer-yzxu6n447dx2x45h.cloudflarestream.com` | customer-yzxu6n447dx2x45h.cloudflarestream.com | 🌐 ציבורי (iptv-org) | 1 |
| `tigocloud.desdeparaguay.net` | tigocloud.desdeparaguay.net | 🌐 ציבורי (iptv-org) | 1 |
| `streamlov.alsolnet.com` | streamlov.alsolnet.com | 🌐 ציבורי (iptv-org) | 1 |
| `streaming.imagenfm105-1.com` | streaming.imagenfm105-1.com | 🌐 ציבורי (iptv-org) | 1 |
| `cloud.fastchannel.es` | cloud.fastchannel.es | 🌐 ציבורי (iptv-org) | 1 |
| `583ed2f226ac2.streamlock.net` | 583ed2f226ac2.streamlock.net | 🌐 ציבורי (iptv-org) | 1 |
| `tezaurtv.md` | tezaurtv.md | 🌐 ציבורי (iptv-org) | 1 |
| `stream.cmctv.hr` | stream.cmctv.hr | 🌐 ציבורי (iptv-org) | 1 |
| `w6oy7x7jlxrg-hls-live.5centscdn.com` | w6oy7x7jlxrg-hls-live.5centscdn.com | 🌐 ציבורי (iptv-org) | 1 |
| `hls-1zdhtxpm-livepush.akamaized.net` | hls-1zdhtxpm-livepush.akamaized.net | 🌐 ציבורי (iptv-org) | 1 |
| `webcam.mosaiquefm.net` | webcam.mosaiquefm.net | 🌐 ציבורי (iptv-org) | 1 |
| `5cf4a2c2512a2.streamlock.net` | 5cf4a2c2512a2.streamlock.net | 🌐 ציבורי (iptv-org) | 1 |
| `albportal.net` | albportal.net | 🌐 ציבורי (iptv-org) | 1 |
| `80.241.215.175` | 80.241.215.175 | 🌐 ציבורי (iptv-org) | 1 |
| `stream.ishtartv.com` | stream.ishtartv.com | 🌐 ציבורי (iptv-org) | 1 |
| `k100streymi.mbl.is` | k100streymi.mbl.is | 🌐 ציבורי (iptv-org) | 1 |
| `playout395.livestreamingcdn.com` | playout395.livestreamingcdn.com | 🌐 ציבורי (iptv-org) | 1 |
| `cdn4.yayin.com.tr` | cdn4.yayin.com.tr | 🌐 ציבורי (iptv-org) | 1 |
| `m-1.data.lt` | m-1.data.lt | 🌐 ציבורי (iptv-org) | 1 |
| `00ff00.latnet.media` | 00ff00.latnet.media | 🌐 ציבורי (iptv-org) | 1 |
| `lightning-now90s-samsungnz.amagi.tv` | lightning-now90s-samsungnz.amagi.tv | 🌐 ציבורי (iptv-org) | 1 |
| `h056.video-stream-hosting.de` | h056.video-stream-hosting.de | 🌐 ציבורי (iptv-org) | 1 |
| `58f12ffd2447a.streamlock.net` | 58f12ffd2447a.streamlock.net | 🌐 ציבורי (iptv-org) | 1 |
| `hls-tvsoyuz.cdnvideo.ru` | hls-tvsoyuz.cdnvideo.ru | 🌐 ציבורי (iptv-org) | 1 |
| `pmchls.wns.live` | pmchls.wns.live | 🌐 ציבורי (iptv-org) | 1 |
| `9090video.mobtada.com` | 9090video.mobtada.com | 🌐 ציבורי (iptv-org) | 1 |
| `hms.pfs.gdn` | hms.pfs.gdn | 🌐 ציבורי (iptv-org) | 1 |
| `stream.ottlive.co.in` | stream.ottlive.co.in | 🌐 ציבורי (iptv-org) | 1 |
| `stream.ichibansys.com` | stream.ichibansys.com | 🌐 ציבורי (iptv-org) | 1 |
| `stream8.mexiserver.com` | stream8.mexiserver.com | 🌐 ציבורי (iptv-org) | 1 |
| `tuvideoonline.com.ar` | tuvideoonline.com.ar | 🌐 ציבורי (iptv-org) | 1 |
| `video03.logicahost.com.br` | video03.logicahost.com.br | 🌐 ציבורי (iptv-org) | 1 |
| `d726x48n2pd5h.cloudfront.net` | d726x48n2pd5h.cloudfront.net | 🌐 ציבורי (iptv-org) | 1 |
| `livetv.streams.ovh` | livetv.streams.ovh | 🌐 ציבורי (iptv-org) | 1 |
| `hls.navahang.live` | hls.navahang.live | 🌐 ציבורי (iptv-org) | 1 |
| `stream.mediawork.cz` | stream.mediawork.cz | 🌐 ציבורי (iptv-org) | 1 |
| `online.tvimpact.live` | online.tvimpact.live | 🌐 ציבורי (iptv-org) | 1 |
| `t2hls.wns.live` | t2hls.wns.live | 🌐 ציבורי (iptv-org) | 1 |
| `video2.makrodigital.com` | video2.makrodigital.com | 🌐 ציבורי (iptv-org) | 1 |
| `vcp11.myplaytv.com` | vcp11.myplaytv.com | 🌐 ציבורי (iptv-org) | 1 |
| `vcp8.myplaytv.com` | vcp8.myplaytv.com | 🌐 ציבורי (iptv-org) | 1 |
| `59d39900ebfb8.streamlock.net` | 59d39900ebfb8.streamlock.net | 🌐 ציבורי (iptv-org) | 1 |
| `cdnbarinas.tvzamora.com` | cdnbarinas.tvzamora.com | 🌐 ציבורי (iptv-org) | 1 |
| `stream.eleden.com` | stream.eleden.com | 🌐 ציבורי (iptv-org) | 1 |
| `hitfm.nfthost.net` | hitfm.nfthost.net | 🌐 ציבורי (iptv-org) | 1 |
| `amg00793-amg00793c6-xumo-us-2669.playouts.now.amagi.tv` | BBC Earth – Amagi / Xumo FAST | ✅ FAST חינמי | 1 |
| `d1si3n1st4nkgb.cloudfront.net` | d1si3n1st4nkgb.cloudfront.net | 🌐 ציבורי (iptv-org) | 1 |
| `lds-timeline-rakuten.amagi.tv` | lds-timeline-rakuten.amagi.tv | 🌐 ציבורי (iptv-org) | 1 |
| `5421175365ea3.streamlock.net` | 5421175365ea3.streamlock.net | 🌐 ציבורי (iptv-org) | 1 |
| `a57e9c69976649b582a8d7604c00e69a.mediatailor.us-east-1.amazonaws.com` | a57e9c69976649b582a8d7604c00e69a.mediatailor.us-east-1.amazonaws.com | 🌐 ציבורי (iptv-org) | 1 |
| `cc-mgr91yrk4pehy.akamaized.net` | cc-mgr91yrk4pehy.akamaized.net | 🌐 ציבורי (iptv-org) | 1 |
| `amg00405-rakutentv-cgtndocumentary-rakuten-0ql8j.amagi.tv` | amg00405-rakutentv-cgtndocumentary-rakuten-0ql8j.amagi.tv | 🌐 ציבורי (iptv-org) | 1 |
| `amg01082-cna-amg01082c1-rlaxx-us-11304.playouts.now.amagi.tv` | amg01082-cna-amg01082c1-rlaxx-us-11304.playouts.now.amagi.tv | 🌐 ציבורי (iptv-org) | 1 |
| `ktvlive.online` | ktvlive.online | 🌐 ציבורי (iptv-org) | 1 |
| `streaming.grupomediosdelnorte.com` | streaming.grupomediosdelnorte.com | 🌐 ציבורי (iptv-org) | 1 |
| `d1mgh147xpmgs8.cloudfront.net` | d1mgh147xpmgs8.cloudfront.net | 🌐 ציבורי (iptv-org) | 1 |
| `amg00711-zylo-amg00711c10-rakuten-fr-6731.playouts.now.amagi.tv` | amg00711-zylo-amg00711c10-rakuten-fr-6731.playouts.now.amagi.tv | 🌐 ציבורי (iptv-org) | 1 |
| `9e754fa707344ccca6d84955c8fcaf36.mediatailor.us-east-1.amazonaws.com` | 9e754fa707344ccca6d84955c8fcaf36.mediatailor.us-east-1.amazonaws.com | 🌐 ציבורי (iptv-org) | 1 |
| `bamusa-naturetime-emea-eng-rakuten.amagi.tv` | bamusa-naturetime-emea-eng-rakuten.amagi.tv | 🌐 ציבורי (iptv-org) | 1 |
| `cc-wsuyg2uxeak04.akamaized.net` | cc-wsuyg2uxeak04.akamaized.net | 🌐 ציבורי (iptv-org) | 1 |
| `ef79b15c8c7c46c7a9de9d33001dbd07.mediatailor.us-west-2.amazonaws.com` | ef79b15c8c7c46c7a9de9d33001dbd07.mediatailor.us-west-2.amazonaws.com | 🌐 ציבורי (iptv-org) | 1 |
| `scihls.persiana.live` | scihls.persiana.live | 🌐 ציבורי (iptv-org) | 1 |
| `muc3.iptv-playoutcenter.de` | muc3.iptv-playoutcenter.de | 🌐 ציבורי (iptv-org) | 1 |
| `185.23.80.23` | 185.23.80.23 | 🌐 ציבורי (iptv-org) | 1 |
| `xykt-fix.github.io` | xykt-fix.github.io | 🌐 ציבורי (iptv-org) | 1 |
| `ptravelhls.persiana.live` | ptravelhls.persiana.live | 🌐 ציבורי (iptv-org) | 1 |
| `53ff3982.wurl.com` | 53ff3982.wurl.com | 🌐 ציבורי (iptv-org) | 1 |
| `mumt04.tangotv.in` | mumt04.tangotv.in | 🌐 ציבורי (iptv-org) | 1 |
| `d1nhni5l2n8hjt.cloudfront.net` | d1nhni5l2n8hjt.cloudfront.net | 🌐 ציבורי (iptv-org) | 1 |
| `s6.hopslan.com` | s6.hopslan.com | 🌐 ציבורי (iptv-org) | 1 |
| `cdn.whiplash.cc` | cdn.whiplash.cc | 🌐 ציבורי (iptv-org) | 1 |
| `d1yd8hohnldm33.cloudfront.net` | ערוץ FAST ישראלי – AWS CloudFront | ✅ FAST חינמי | 1 |
| `broadcast.blivenyc.com` | broadcast.blivenyc.com | 🌐 ציבורי (iptv-org) | 1 |
| `hnc-free-viewlift.amagi.tv` | hnc-free-viewlift.amagi.tv | 🌐 ציבורי (iptv-org) | 1 |
| `iptv.peer.biz` | iptv.peer.biz | 🌐 ציבורי (iptv-org) | 1 |
| `9ada494d.wurl.com` | 9ada494d.wurl.com | 🌐 ציבורי (iptv-org) | 1 |
| `amg00861-amg00861c10-rakuten-uk-3152.playouts.now.amagi.tv` | amg00861-amg00861c10-rakuten-uk-3152.playouts.now.amagi.tv | 🌐 ציבורי (iptv-org) | 1 |
| `agrotv.streamnet.ro` | agrotv.streamnet.ro | 🌐 ציבורי (iptv-org) | 1 |
| `dqga3jatxofgx.cloudfront.net` | dqga3jatxofgx.cloudfront.net | 🌐 ציבורי (iptv-org) | 1 |
| `streamer103.neterra.tv` | streamer103.neterra.tv | 🌐 ציבורי (iptv-org) | 1 |
| `copacoradios.desdeparaguay.net` | copacoradios.desdeparaguay.net | 🌐 ציבורי (iptv-org) | 1 |
| `ardbtv.ardb.com.kh` | ardbtv.ardb.com.kh | 🌐 ציבורי (iptv-org) | 1 |
| `airvuz-dronetv-1-us.ohlscdn.wurl.tv` | airvuz-dronetv-1-us.ohlscdn.wurl.tv | 🌐 ציבורי (iptv-org) | 1 |
| `linear-1224.frequency.stream` | linear-1224.frequency.stream | 🌐 ציבורי (iptv-org) | 1 |
| `aovivo.equipea.com.br` | aovivo.equipea.com.br | 🌐 ציבורי (iptv-org) | 1 |
| `streaming5.globalhostla.com` | streaming5.globalhostla.com | 🌐 ציבורי (iptv-org) | 1 |
| `restr2.bgtv.bg` | restr2.bgtv.bg | 🌐 ציבורי (iptv-org) | 1 |
| `main.duckhunting.playout.vju.tv` | main.duckhunting.playout.vju.tv | 🌐 ציבורי (iptv-org) | 1 |
| `111.59.24.227` | 111.59.24.227 | 🌐 ציבורי (iptv-org) | 1 |
| `d1e354daam8g5r.cloudfront.net` | d1e354daam8g5r.cloudfront.net | 🌐 ציבורי (iptv-org) | 1 |
| `200.189.113.201` | 200.189.113.201 | 🌐 ציבורי (iptv-org) | 1 |
| `m317.video-stream-hosting.de` | m317.video-stream-hosting.de | 🌐 ציבורי (iptv-org) | 1 |
| `dfoiz3dv1euv7.cloudfront.net` | dfoiz3dv1euv7.cloudfront.net | 🌐 ציבורי (iptv-org) | 1 |
| `d1p1siomv3cm7b.cloudfront.net` | d1p1siomv3cm7b.cloudfront.net | 🌐 ציבורי (iptv-org) | 1 |
| `cdnapisec.kaltura.com` | cdnapisec.kaltura.com | 🌐 ציבורי (iptv-org) | 1 |
| `phd2hls.persiana.live` | phd2hls.persiana.live | 🌐 ציבורי (iptv-org) | 1 |
| `fms.nrwision.de` | fms.nrwision.de | 🌐 ציבורי (iptv-org) | 1 |
| `932y4273djv8-hls-live.mariatvcdn.it` | 932y4273djv8-hls-live.mariatvcdn.it | 🌐 ציבורי (iptv-org) | 1 |
| `live.knou.ac.kr` | live.knou.ac.kr | 🌐 ציבורי (iptv-org) | 1 |
| `app.tvufop.com.br` | app.tvufop.com.br | 🌐 ציבורי (iptv-org) | 1 |
| `tvenvivo.buap.mx` | tvenvivo.buap.mx | 🌐 ציבורי (iptv-org) | 1 |
| `unlimited1-us.dps.live` | unlimited1-us.dps.live | 🌐 ציבורי (iptv-org) | 1 |
| `stmv3.voxtvhd.com.br` | stmv3.voxtvhd.com.br | 🌐 ציבורי (iptv-org) | 1 |
| `restreamer.indonusa.id` | restreamer.indonusa.id | 🌐 ציבורי (iptv-org) | 1 |
| `live.canthotv.vn` | live.canthotv.vn | 🌐 ציבורי (iptv-org) | 1 |
| `live.kh.malimarcdn.com` | live.kh.malimarcdn.com | 🌐 ציבורי (iptv-org) | 1 |
| `avm.ecuamedia.net` | avm.ecuamedia.net | 🌐 ציבורי (iptv-org) | 1 |
| `create.lls.pbs.org` | create.lls.pbs.org | 🌐 ציבורי (iptv-org) | 1 |
| `tv-e-okul01.medya.trt.com.tr` | tv-e-okul01.medya.trt.com.tr | 🌐 ציבורי (iptv-org) | 1 |
| `media.joycorp.co.kr` | media.joycorp.co.kr | 🌐 ציבורי (iptv-org) | 1 |
| `streaming.procergs.com.br` | streaming.procergs.com.br | 🌐 ציבורי (iptv-org) | 1 |
| `5ca3e84a76d30.streamlock.net` | 5ca3e84a76d30.streamlock.net | 🌐 ציבורי (iptv-org) | 1 |
| `193.225.32.62` | 193.225.32.62 | 🌐 ציבורי (iptv-org) | 1 |
| `stv4.janus.cl` | stv4.janus.cl | 🌐 ציבורי (iptv-org) | 1 |
| `rinjanitv.cloud` | rinjanitv.cloud | 🌐 ציבורי (iptv-org) | 1 |
| `stream.wikitv.asia` | stream.wikitv.asia | 🌐 ציבורי (iptv-org) | 1 |
| `5e85d90130e77.streamlock.net` | 5e85d90130e77.streamlock.net | 🌐 ציבורי (iptv-org) | 1 |
| `190.128.214.97` | 190.128.214.97 | 🌐 ציבורי (iptv-org) | 1 |
| `video.compuwebecuador.com` | video.compuwebecuador.com | 🌐 ציבורי (iptv-org) | 1 |
| `video.oct.dc.gov` | video.oct.dc.gov | 🌐 ציבורי (iptv-org) | 1 |
| `lds-wonder-rakuten.amagi.tv` | lds-wonder-rakuten.amagi.tv | 🌐 ציבורי (iptv-org) | 1 |
| `relay2.cdn.fem-net.de` | relay2.cdn.fem-net.de | 🌐 ציבורי (iptv-org) | 1 |
| `932y4x26ljv8-hls-live.5centscdn.com` | 932y4x26ljv8-hls-live.5centscdn.com | 🌐 ציבורי (iptv-org) | 1 |
| `canaleducacao-stream.ebc.com.br` | canaleducacao-stream.ebc.com.br | 🌐 ציבורי (iptv-org) | 1 |
| `5fe2654d6127d.streamlock.net` | 5fe2654d6127d.streamlock.net | 🌐 ציבורי (iptv-org) | 1 |
| `49.156.33.114` | 49.156.33.114 | 🌐 ציבורי (iptv-org) | 1 |
| `reflect-channel36-la.cablecast.tv` | reflect-channel36-la.cablecast.tv | 🌐 ציבורי (iptv-org) | 1 |
| `playhls.media.nic.in` | playhls.media.nic.in | 🌐 ציבורי (iptv-org) | 1 |
| `stmv1.srvstm.com` | stmv1.srvstm.com | 🌐 ציבורי (iptv-org) | 1 |
| `live-ilce.ovp-vivaro.digital` | live-ilce.ovp-vivaro.digital | 🌐 ציבורי (iptv-org) | 1 |
| `sitetv.brasilstream.com.br` | sitetv.brasilstream.com.br | 🌐 ציבורי (iptv-org) | 1 |
| `alt-al.otteravision.com` | alt-al.otteravision.com | 🌐 ציבורי (iptv-org) | 1 |
| `amg00654-itv-amg00654c35-rakuten-gb-7598.playouts.now.amagi.tv` | amg00654-itv-amg00654c35-rakuten-gb-7598.playouts.now.amagi.tv | 🌐 ציבורי (iptv-org) | 1 |
| `fl718492-exthls-p1.flovp.com` | fl718492-exthls-p1.flovp.com | 🌐 ציבורי (iptv-org) | 1 |
| `cb0c87cc605942ff9766a4e6744bbadc.mediatailor.us-east-1.amazonaws.com` | cb0c87cc605942ff9766a4e6744bbadc.mediatailor.us-east-1.amazonaws.com | 🌐 ציבורי (iptv-org) | 1 |
| `streamcdnr6-dddfac816632463dac50b21109dc1bb3.msvdn.net` | streamcdnr6-dddfac816632463dac50b21109dc1bb3.msvdn.net | 🌐 ציבורי (iptv-org) | 1 |
| `live-minidey.cdnvideo.ru` | live-minidey.cdnvideo.ru | 🌐 ציבורי (iptv-org) | 1 |
| `eu.streamjo.com` | eu.streamjo.com | 🌐 ציבורי (iptv-org) | 1 |
| `dce3793146fef017.mediapackage.us-west-2.amazonaws.com` | dce3793146fef017.mediapackage.us-west-2.amazonaws.com | 🌐 ציבורי (iptv-org) | 1 |
| `canli.edessatv.com` | canli.edessatv.com | 🌐 ציבורי (iptv-org) | 1 |
| `media-tyo.hls.nhkworld.jp` | media-tyo.hls.nhkworld.jp | 🌐 ציבורי (iptv-org) | 1 |
| `nonghyup.flive.skcdn.com` | nonghyup.flive.skcdn.com | 🌐 ציבורי (iptv-org) | 1 |
| `liveplus.lzr.com.cn` | liveplus.lzr.com.cn | 🌐 ציבורי (iptv-org) | 1 |
| `isaocorp.cloudecast.com` | isaocorp.cloudecast.com | 🌐 ציבורי (iptv-org) | 1 |
| `azt-mun.otteravision.com` | azt-mun.otteravision.com | 🌐 ציבורי (iptv-org) | 1 |
| `streamtv12.ddns.net` | streamtv12.ddns.net | 🌐 ציבורי (iptv-org) | 1 |
| `live.salto.nl` | live.salto.nl | 🌐 ציבורי (iptv-org) | 1 |
| `bsmbialogard.pl` | bsmbialogard.pl | 🌐 ציבורי (iptv-org) | 1 |
| `es1.crystalweb.net` | es1.crystalweb.net | 🌐 ציבורי (iptv-org) | 1 |
| `digicom.hls.iptvdc.com` | digicom.hls.iptvdc.com | 🌐 ציבורי (iptv-org) | 1 |
| `trun.suyaitv.cl` | trun.suyaitv.cl | 🌐 ציבורי (iptv-org) | 1 |
| `rcntv-rcnmas-1-us.roku.wurl.tv` | rcntv-rcnmas-1-us.roku.wurl.tv | 🌐 ציבורי (iptv-org) | 1 |
| `video2.lhdserver.es` | video2.lhdserver.es | 🌐 ציבורי (iptv-org) | 1 |
| `live.salira.tv` | live.salira.tv | 🌐 ציבורי (iptv-org) | 1 |
| `stream1.ai-net.net` | stream1.ai-net.net | 🌐 ציבורי (iptv-org) | 1 |
| `g4wlkwx8l23a-hls-live.5centscdn.com` | g4wlkwx8l23a-hls-live.5centscdn.com | 🌐 ציבורי (iptv-org) | 1 |
| `hls.247box.live` | hls.247box.live | 🌐 ציבורי (iptv-org) | 1 |
| `136044159.r.cdnsun.net` | 136044159.r.cdnsun.net | 🌐 ציבורי (iptv-org) | 1 |
| `acasmedia3.acangroup.org` | acasmedia3.acangroup.org | 🌐 ציבורי (iptv-org) | 1 |
| `stream.aguacatetv.com` | stream.aguacatetv.com | 🌐 ציבורי (iptv-org) | 1 |
| `sv72.ecuaradiotv.net` | sv72.ecuaradiotv.net | 🌐 ציבורי (iptv-org) | 1 |
| `streamfi-alphadgtl1.zettawiseroutes.com` | streamfi-alphadgtl1.zettawiseroutes.com | 🌐 ציבורי (iptv-org) | 1 |
| `hls.shift.tv` | hls.shift.tv | 🌐 ציבורי (iptv-org) | 1 |
| `med-tv-it.fast.rakuten.tv` | med-tv-it.fast.rakuten.tv | 🌐 ציבורי (iptv-org) | 1 |
| `5dcabf026b188.streamlock.net` | 5dcabf026b188.streamlock.net | 🌐 ציבורי (iptv-org) | 1 |
| `tv2-stream.rtnb.bi` | tv2-stream.rtnb.bi | 🌐 ציבורי (iptv-org) | 1 |
| `sy.wns.live` | sy.wns.live | 🌐 ציבורי (iptv-org) | 1 |
| `live.suricloud.com` | live.suricloud.com | 🌐 ציבורי (iptv-org) | 1 |
| `kwtktvata.cdn.mangomolo.com` | kwtktvata.cdn.mangomolo.com | 🌐 ציבורי (iptv-org) | 1 |
| `streamtv.cmediahosthosting.net` | streamtv.cmediahosthosting.net | 🌐 ציבורי (iptv-org) | 1 |
| `mistserver.evi-tv.com` | mistserver.evi-tv.com | 🌐 ציבורי (iptv-org) | 1 |
| `connectiktv.ddns.net` | connectiktv.ddns.net | 🌐 ציבורי (iptv-org) | 1 |
| `live.gideo.video` | live.gideo.video | 🌐 ציבורי (iptv-org) | 1 |
| `cdn.mycloudstream.io` | cdn.mycloudstream.io | 🌐 ציבורי (iptv-org) | 1 |
| `livedelta.cdn.antel.net.uy` | livedelta.cdn.antel.net.uy | 🌐 ציבורי (iptv-org) | 1 |
| `abaitv-stream.qazcdn.com` | abaitv-stream.qazcdn.com | 🌐 ציבורי (iptv-org) | 1 |
| `l2.cloudskep.com` | l2.cloudskep.com | 🌐 ציבורי (iptv-org) | 1 |
| `media.streambrothers.com` | media.streambrothers.com | 🌐 ציבורי (iptv-org) | 1 |
| `seswa.bonce.tv` | seswa.bonce.tv | 🌐 ציבורי (iptv-org) | 1 |
| `stream.eldo.lu` | stream.eldo.lu | 🌐 ציבורי (iptv-org) | 1 |
| `live.lietuvosryto.tv` | live.lietuvosryto.tv | 🌐 ציבורי (iptv-org) | 1 |
| `streaming.tvnepal.com` | streaming.tvnepal.com | 🌐 ציבורי (iptv-org) | 1 |
| `stream-134630.castr.net` | stream-134630.castr.net | 🌐 ציבורי (iptv-org) | 1 |
| `38.64.72.148` | 38.64.72.148 | 🌐 ציבורי (iptv-org) | 1 |
| `lo3.gemporia.com` | lo3.gemporia.com | 🌐 ציבורי (iptv-org) | 1 |
| `edge-fast3.evrideo.tv` | edge-fast3.evrideo.tv | 🌐 ציבורי (iptv-org) | 1 |
| `mvg-mv-xumo.otteravision.com` | mvg-mv-xumo.otteravision.com | 🌐 ציבורי (iptv-org) | 1 |
| `d1k3vzh2ivy22k.cloudfront.net` | d1k3vzh2ivy22k.cloudfront.net | 🌐 ציבורי (iptv-org) | 1 |
| `edge.taksimbilisim.com` | edge.taksimbilisim.com | 🌐 ציבורי (iptv-org) | 1 |
| `adylnet.streampool.net` | adylnet.streampool.net | 🌐 ציבורי (iptv-org) | 1 |
| `video.cdmx.gob.mx` | video.cdmx.gob.mx | 🌐 ציבורי (iptv-org) | 1 |
| `sbs6classic.prd1.talpatvcdn.nl` | sbs6classic.prd1.talpatvcdn.nl | 🌐 ציבורי (iptv-org) | 1 |
| `onefmhd.vedge.infomaniak.com` | onefmhd.vedge.infomaniak.com | 🌐 ציבורי (iptv-org) | 1 |
| `stream14.polskieradio.pl` | stream14.polskieradio.pl | 🌐 ציבורי (iptv-org) | 1 |
| `livestar.siliconweb.com` | livestar.siliconweb.com | 🌐 ציבורי (iptv-org) | 1 |
| `tvr-cultural.lg.mncdn.com` | tvr-cultural.lg.mncdn.com | 🌐 ציבורי (iptv-org) | 1 |
| `hls.oxir.live` | hls.oxir.live | 🌐 ציבורי (iptv-org) | 1 |
| `cdn.protvradiostream.com` | cdn.protvradiostream.com | 🌐 ציבורי (iptv-org) | 1 |
| `streamtv.intervenhosting.net` | streamtv.intervenhosting.net | 🌐 ציבורי (iptv-org) | 1 |
| `cloud37.ecuatel.com` | cloud37.ecuatel.com | 🌐 ציבורי (iptv-org) | 1 |
| `acceso.mediosdecostarica.com` | acceso.mediosdecostarica.com | 🌐 ציבורי (iptv-org) | 1 |
| `avr.host247.net` | avr.host247.net | 🌐 ציבורי (iptv-org) | 1 |
| `fort.co-works.org` | fort.co-works.org | 🌐 ציבורי (iptv-org) | 1 |
| `tv.hiruhost.com` | tv.hiruhost.com | 🌐 ציבורי (iptv-org) | 1 |
| `stream.agatin.hr` | stream.agatin.hr | 🌐 ציבורי (iptv-org) | 1 |
| `linear-900.frequency.stream` | linear-900.frequency.stream | 🌐 ציבורי (iptv-org) | 1 |
| `stream.panet.com` | פאנט – הלא TV (רשמי) | ✅ רשמי | 1 |
| `edge3.uk.kab.tv` | קבלה לעם – שרת רשמי | ✅ רשמי | 1 |
| `1247634592.rsc.cdn77.org` | שלנו TV – CDN77 | ✅ רשמי | 1 |
| `ch16israel-cdn.encoders.immergo.tv` | ערוץ 16 – שרת רשמי | ✅ רשמי | 1 |
| `jlt-jltv-xumo.otteravision.com` | jlt-jltv-xumo.otteravision.com | ⚠️ ממסר צד שלישי | 1 |
| `edge2.il.kab.tv` | קבלה לעם – השרת של הערוץ | ✅ רשמי | 1 |
| `dwamdstream102.akamaized.net` | DW – Akamai (רשמי) | ✅ רשמי | 1 |
| `live-hls-apps-aje-fa.getaj.net` | אל-ג'זירה – שרת רשמי | ✅ רשמי | 1 |
| `amg01942-amg01942c6-stirr-us-10178.playouts.now.amagi.tv` | amg01942-amg01942c6-stirr-us-10178.playouts.now.amagi.tv | 🌐 ציבורי (iptv-org) | 1 |
| `hls.autopo.st` | hls.autopo.st | 🌐 ציבורי (iptv-org) | 1 |
| `live-20minutestv.digiteka.com` | live-20minutestv.digiteka.com | 🌐 ציבורי (iptv-org) | 1 |
| `alex-stream.rosebud-media.de` | alex-stream.rosebud-media.de | 🌐 ציבורי (iptv-org) | 1 |
| `streamtv2.elitecomunicacion.cloud` | streamtv2.elitecomunicacion.cloud | 🌐 ציבורי (iptv-org) | 1 |
| `dfhsahpa45kk2.cloudfront.net` | dfhsahpa45kk2.cloudfront.net | 🌐 ציבורי (iptv-org) | 1 |
| `cdn1.logichost.in` | cdn1.logichost.in | 🌐 ציבורי (iptv-org) | 1 |
| `alrayyancdn.vidgyor.com` | alrayyancdn.vidgyor.com | 🌐 ציבורי (iptv-org) | 1 |
| `5c3639aa99149.streamlock.net` | 5c3639aa99149.streamlock.net | 🌐 ציבורי (iptv-org) | 1 |
| `event.pull.hebtv.com` | event.pull.hebtv.com | 🌐 ציבורי (iptv-org) | 1 |
| `amazonsat.brasilstream.com.br` | amazonsat.brasilstream.com.br | 🌐 ציבורי (iptv-org) | 1 |
| `nd106.republicaservers.com` | nd106.republicaservers.com | 🌐 ציבורי (iptv-org) | 1 |
| `production-fast-sic.content.okast.tv` | production-fast-sic.content.okast.tv | 🌐 ציבורי (iptv-org) | 1 |
| `wl.tvrain.tv` | wl.tvrain.tv | 🌐 ציבורי (iptv-org) | 1 |
| `live.matele.be` | live.matele.be | 🌐 ציבורי (iptv-org) | 1 |
| `cdn.katapy.io` | cdn.katapy.io | 🌐 ציבורי (iptv-org) | 1 |
| `streaming13.huberwebmedia.at` | streaming13.huberwebmedia.at | 🌐 ציבורי (iptv-org) | 1 |
| `1409502578.rsc.cdn77.org` | 1409502578.rsc.cdn77.org | 🌐 ציבורי (iptv-org) | 1 |
| `alphatvlive2.siliconweb.com` | alphatvlive2.siliconweb.com | 🌐 ציבורי (iptv-org) | 1 |
| `frikanalen.no` | frikanalen.no | 🌐 ציבורי (iptv-org) | 1 |
| `drlivedrtvahls.akamaized.net` | drlivedrtvahls.akamaized.net | 🌐 ציבורי (iptv-org) | 1 |
| `a7tvlive.ro` | a7tvlive.ro | 🌐 ציבורי (iptv-org) | 1 |
| `cloudfront44.lexanetwork.com` | cloudfront44.lexanetwork.com | 🌐 ציבורי (iptv-org) | 1 |
| `canal.aupurtv.co` | canal.aupurtv.co | 🌐 ציבורי (iptv-org) | 1 |
| `ott-balancer.tvri.go.id` | ott-balancer.tvri.go.id | 🌐 ציבורי (iptv-org) | 1 |
| `cdn-edge.iiptvcdn.com` | cdn-edge.iiptvcdn.com | 🌐 ציבורי (iptv-org) | 1 |
| `amagi-live.ondemandkorea.com` | amagi-live.ondemandkorea.com | 🌐 ציבורי (iptv-org) | 1 |
| `cdn.live247stream.com` | cdn.live247stream.com | 🌐 ציבורי (iptv-org) | 1 |
| `live.asil.tv` | live.asil.tv | 🌐 ציבורי (iptv-org) | 1 |
| `d2qh3gh0k5vp3v.cloudfront.net` | d2qh3gh0k5vp3v.cloudfront.net | 🌐 ציבורי (iptv-org) | 1 |
| `tv-west.lg.mncdn.com` | tv-west.lg.mncdn.com | 🌐 ציבורי (iptv-org) | 1 |
| `183.bozztv.com` | 183.bozztv.com | 🌐 ציבורי (iptv-org) | 1 |
| `stream.paraguaytv.gov.py` | stream.paraguaytv.gov.py | 🌐 ציבורי (iptv-org) | 1 |
| `cdnsk003.panaccess.com` | cdnsk003.panaccess.com | 🌐 ציבורי (iptv-org) | 1 |
| `streamtv.latinamedios.com` | streamtv.latinamedios.com | 🌐 ציבורי (iptv-org) | 1 |
| `video.makrodigital.com` | video.makrodigital.com | 🌐 ציבורי (iptv-org) | 1 |
| `k20.usastreams.com` | k20.usastreams.com | 🌐 ציבורי (iptv-org) | 1 |
| `media1.livaat.com` | media1.livaat.com | 🌐 ציבורי (iptv-org) | 1 |
| `tsv.md` | tsv.md | 🌐 ציבורי (iptv-org) | 1 |
| `live.11-cdn.com` | live.11-cdn.com | 🌐 ציבורי (iptv-org) | 1 |
| `stream.shariarsuvo.com` | stream.shariarsuvo.com | 🌐 ציבורי (iptv-org) | 1 |
| `stream.nmih.hu` | stream.nmih.hu | 🌐 ציבורי (iptv-org) | 1 |
| `tv.cdn.xsg.ge` | tv.cdn.xsg.ge | 🌐 ציבורי (iptv-org) | 1 |
| `stream.it-innov.com` | stream.it-innov.com | 🌐 ציבורי (iptv-org) | 1 |
| `live-edge.rtl.lu` | live-edge.rtl.lu | 🌐 ציבורי (iptv-org) | 1 |
| `video-eu1.streamerr.co` | video-eu1.streamerr.co | 🌐 ציבורי (iptv-org) | 1 |
| `tringliveviz.akamaized.net` | tringliveviz.akamaized.net | 🌐 ציבורי (iptv-org) | 1 |
| `live.kingdom-tv.com` | live.kingdom-tv.com | 🌐 ציבורי (iptv-org) | 1 |
| `janya-ayaztv.vgcdn.net` | janya-ayaztv.vgcdn.net | 🌐 ציבורי (iptv-org) | 1 |
| `amtv.tulixcdn.com` | amtv.tulixcdn.com | 🌐 ציבורי (iptv-org) | 1 |
| `seatv.netlinkbroadcaster.com` | seatv.netlinkbroadcaster.com | 🌐 ציבורי (iptv-org) | 1 |
| `hls.tamadon.live` | hls.tamadon.live | 🌐 ציבורי (iptv-org) | 1 |
| `lsm.eu.cdns-redge.media` | lsm.eu.cdns-redge.media | 🌐 ציבורי (iptv-org) | 1 |
| `pool.alter-media.hr` | pool.alter-media.hr | 🌐 ציבורי (iptv-org) | 1 |
| `stream.rtvze.ba` | stream.rtvze.ba | 🌐 ציבורי (iptv-org) | 1 |
| `rtcg-live-open.morescreens.com` | rtcg-live-open.morescreens.com | 🌐 ציבורי (iptv-org) | 1 |
| `lbs-us1.suprememastertv.com` | lbs-us1.suprememastertv.com | 🌐 ציבורי (iptv-org) | 1 |
| `5dcab9aed5331.streamlock.net` | 5dcab9aed5331.streamlock.net | 🌐 ציבורי (iptv-org) | 1 |
| `kilolink.pulsarstarsolutions.com` | kilolink.pulsarstarsolutions.com | 🌐 ציבורי (iptv-org) | 1 |
| `ruv-web-live.akamaized.net` | ruv-web-live.akamaized.net | 🌐 ציבורי (iptv-org) | 1 |
| `tv.tvsb.bg` | tv.tvsb.bg | 🌐 ציבורי (iptv-org) | 1 |
| `5790d294af2dc.streamlock.net` | 5790d294af2dc.streamlock.net | 🌐 ציבורי (iptv-org) | 1 |
| `live-onesignal.royalmedia.co.ke` | live-onesignal.royalmedia.co.ke | 🌐 ציבורי (iptv-org) | 1 |
| `tv.pointville.ag` | tv.pointville.ag | 🌐 ציבורי (iptv-org) | 1 |
| `raajjetv.yarr-myn.workers.dev` | raajjetv.yarr-myn.workers.dev | 🌐 ציבורי (iptv-org) | 1 |
| `vod2live.univtec.com` | vod2live.univtec.com | 🌐 ציבורי (iptv-org) | 1 |
| `zns-edge1.streamcomedia.cloud` | zns-edge1.streamcomedia.cloud | 🌐 ציבורי (iptv-org) | 1 |
| `sw1.tanitweb.net` | sw1.tanitweb.net | 🌐 ציבורי (iptv-org) | 1 |
| `kwtktv1ta.cdn.mangomolo.com` | kwtktv1ta.cdn.mangomolo.com | 🌐 ציבורי (iptv-org) | 1 |
| `cloud.streamingconnect.tv` | cloud.streamingconnect.tv | 🌐 ציבורי (iptv-org) | 1 |
| `cdn-globecast.akamaized.net` | cdn-globecast.akamaized.net | 🌐 ציבורי (iptv-org) | 1 |
| `185.9.2.18` | 185.9.2.18 | 🌐 ציבורי (iptv-org) | 1 |
| `video.yementdy.tv` | video.yementdy.tv | 🌐 ציבורי (iptv-org) | 1 |
| `tv1-stream.rtnb.bi` | tv1-stream.rtnb.bi | 🌐 ציבורי (iptv-org) | 1 |
| `cdn140m.panaccess.com` | cdn140m.panaccess.com | 🌐 ציבורי (iptv-org) | 1 |
| `production-fast-mcrtv.content.okast.tv` | production-fast-mcrtv.content.okast.tv | 🌐 ציבורי (iptv-org) | 1 |
| `live.zitata.tv` | live.zitata.tv | 🌐 ציבורי (iptv-org) | 1 |
| `stream.tvm.co.mz` | stream.tvm.co.mz | 🌐 ציבורי (iptv-org) | 1 |
| `livesg1.rtva.hiway.media` | livesg1.rtva.hiway.media | 🌐 ציבורי (iptv-org) | 1 |
| `goliveafrica.media` | goliveafrica.media | 🌐 ציבורי (iptv-org) | 1 |
| `stream2.nostvbonaire.com` | stream2.nostvbonaire.com | 🌐 ציבורי (iptv-org) | 1 |
| `oqgdrog4d4rm-hls-live.5centscdn.com` | oqgdrog4d4rm-hls-live.5centscdn.com | 🌐 ציבורי (iptv-org) | 1 |
| `radio.serv.si` | radio.serv.si | 🌐 ציבורי (iptv-org) | 1 |
| `candlelightmedia-sparklightlove-4-us.roku.wurl.tv` | candlelightmedia-sparklightlove-4-us.roku.wurl.tv | 🌐 ציבורי (iptv-org) | 1 |
| `stream01.welocal.stream` | stream01.welocal.stream | 🌐 ציבורי (iptv-org) | 1 |
| `cdnlivevlc.codev8.net` | cdnlivevlc.codev8.net | 🌐 ציבורי (iptv-org) | 1 |
| `bonappetit-samsung.amagi.tv` | bonappetit-samsung.amagi.tv | 🌐 ציבורי (iptv-org) | 1 |
| `platform-iuoamc.uk` | platform-iuoamc.uk | 🌐 ציבורי (iptv-org) | 1 |
| `tvf-tv5ch.otteravision.com` | tvf-tv5ch.otteravision.com | 🌐 ציבורי (iptv-org) | 1 |
| `api.alpaca.t62a.com` | api.alpaca.t62a.com | 🌐 ציבורי (iptv-org) | 1 |
| `e75fc053c0d2406fb74cdd71d77e1a7f.mediatailor.us-east-1.amazonaws.com` | e75fc053c0d2406fb74cdd71d77e1a7f.mediatailor.us-east-1.amazonaws.com | 🌐 ציבורי (iptv-org) | 1 |
| `op-flashcon-digdayahd-1.dens.tv` | op-flashcon-digdayahd-1.dens.tv | 🌐 ציבורי (iptv-org) | 1 |
| `faithworldtv.abs.tv` | faithworldtv.abs.tv | 🌐 ציבורי (iptv-org) | 1 |
| `evandis.streamakaci.tv` | evandis.streamakaci.tv | 🌐 ציבורי (iptv-org) | 1 |
| `bild-und-ton.stream` | bild-und-ton.stream | 🌐 ציבורי (iptv-org) | 1 |
| `600f07e114306.streamlock.net` | 600f07e114306.streamlock.net | 🌐 ציבורי (iptv-org) | 1 |
| `hls-mycdn08724960135.cdnvideo.ru` | hls-mycdn08724960135.cdnvideo.ru | 🌐 ציבורי (iptv-org) | 1 |
| `dzkyvlfyge.erbvr.com` | dzkyvlfyge.erbvr.com | 🌐 ציבורי (iptv-org) | 1 |
| `qatartv.akamaized.net` | qatartv.akamaized.net | 🌐 ציבורי (iptv-org) | 1 |
| `5b622f07944df.streamlock.net` | 5b622f07944df.streamlock.net | 🌐 ציבורי (iptv-org) | 1 |
| `eustr73.mediatriple.net` | eustr73.mediatriple.net | 🌐 ציבורי (iptv-org) | 1 |
| `ktismaservers.in` | ktismaservers.in | 🌐 ציבורי (iptv-org) | 1 |
| `btn.nowcdn.co.kr` | btn.nowcdn.co.kr | 🌐 ציבורי (iptv-org) | 1 |
| `play.kankanlive.com` | play.kankanlive.com | 🌐 ציבורי (iptv-org) | 1 |
| `g5nl63z8lpq6-hls-live.5centscdn.com` | g5nl63z8lpq6-hls-live.5centscdn.com | 🌐 ציבורי (iptv-org) | 1 |
| `everest.pa-hosting.de` | everest.pa-hosting.de | 🌐 ציבורי (iptv-org) | 1 |
| `vod.tv7.fi` | vod.tv7.fi | 🌐 ציבורי (iptv-org) | 1 |
| `aom-live.maghost.ro` | aom-live.maghost.ro | 🌐 ציבורי (iptv-org) | 1 |
| `live.apostoltv.hu` | live.apostoltv.hu | 🌐 ציבורי (iptv-org) | 1 |
| `multimedia.fortalezatv.cl` | multimedia.fortalezatv.cl | 🌐 ציבורי (iptv-org) | 1 |
| `strhls.streamakaci.tv` | strhls.streamakaci.tv | ⚠️ ממסר צד שלישי | 1 |
| `bein-xtra-bein.amagi.tv` | bein-xtra-bein.amagi.tv | ⚠️ ממסר צד שלישי | 1 |
| `178.124.179.122` | 178.124.179.122 | ⚠️ ממסר צד שלישי | 1 |
| `edge59.dc.beltelecom.by` | edge59.dc.beltelecom.by | ⚠️ ממסר צד שלישי | 1 |
| `livestream.lumpentv.com.ar` | livestream.lumpentv.com.ar | ⚠️ ממסר צד שלישי | 1 |
| `hls.tvabierta.net` | hls.tvabierta.net | ⚠️ ממסר צד שלישי | 1 |
| `37b4c228.wurl.com` | 37b4c228.wurl.com | ⚠️ ממסר צד שלישי | 1 |
| `4397879b.wurl.com` | 4397879b.wurl.com | ⚠️ ממסר צד שלישי | 1 |
| `6c849fb3.wurl.com` | 6c849fb3.wurl.com | ⚠️ ממסר צד שלישי | 1 |
| `5d95f7d7.wurl.com` | 5d95f7d7.wurl.com | ⚠️ ממסר צד שלישי | 1 |
| `e3be9ac5.wurl.com` | e3be9ac5.wurl.com | ⚠️ ממסר צד שלישי | 1 |
| `d63fabad.wurl.com` | d63fabad.wurl.com | ⚠️ ממסר צד שלישי | 1 |
| `d2w9q46ikgrcwx.cloudfront.net` | d2w9q46ikgrcwx.cloudfront.net | ⚠️ ממסר צד שלישי | 1 |
| `tvsen7.aynascope.net` | tvsen7.aynascope.net | ⚠️ ממסר צד שלישי | 1 |
| `1593604785.rsc.cdn77.org` | 1593604785.rsc.cdn77.org | ⚠️ ממסר צד שלישי | 1 |
| `dnf08l6u6uxnz.cloudfront.net` | dnf08l6u6uxnz.cloudfront.net | ⚠️ ממסר צד שלישי | 1 |
| `amg00477-samsungelectron-worldpokertour-samsunguk-81igb.amagi.tv` | amg00477-samsungelectron-worldpokertour-samsunguk-81igb.amagi.tv | ⚠️ ממסר צד שלישי | 1 |
| `4kuhls.persiana.live` | 4kuhls.persiana.live | ⚠️ ממסר צד שלישי | 1 |
| `musichls.persiana.live` | musichls.persiana.live | ⚠️ ממסר צד שלישי | 1 |
| `cdn2.in` | cdn2.in | ⚠️ ממסר צד שלישי | 1 |
| `wms.shared.streamshow.it` | wms.shared.streamshow.it | ⚠️ ממסר צד שלישי | 1 |
| `k3.usastreams.com` | k3.usastreams.com | ⚠️ ממסר צד שלישי | 1 |
| `d2hng5r56zpsbw.cloudfront.net` | d2hng5r56zpsbw.cloudfront.net | ⚠️ ממסר צד שלישי | 1 |
| `streamtv.mediasector.es` | streamtv.mediasector.es | ⚠️ ממסר צד שלישי | 1 |
| `video.wilohosting.com` | video.wilohosting.com | ⚠️ ממסר צד שלישי | 1 |
| `tv.alterna.ar` | tv.alterna.ar | ⚠️ ממסר צד שלישי | 1 |
| `istream.binarywaves.com` | istream.binarywaves.com | ⚠️ ממסר צד שלישי | 1 |
| `hls.avang.live` | hls.avang.live | ⚠️ ממסר צד שלישי | 1 |
| `streamyes.alsolnet.com` | streamyes.alsolnet.com | ⚠️ ממסר צד שלישי | 1 |
| `amg01408-amg01408c5-amgplt0747.playout.now3.amagi.tv` | amg01408-amg01408c5-amgplt0747.playout.now3.amagi.tv | ⚠️ ממסר צד שלישי | 1 |
| `streams.tangotv.in` | streams.tangotv.in | ⚠️ ממסר צד שלישי | 1 |
| `mcncdndigital.com` | mcncdndigital.com | ⚠️ ממסר צד שלישי | 1 |
| `181.78.106.127` | 181.78.106.127 | ⚠️ ממסר צד שלישי | 1 |
| `tv.beatfm.nl` | tv.beatfm.nl | ⚠️ ממסר צד שלישי | 1 |
| `bibeltv03.iptv-playoutcenter.de` | bibeltv03.iptv-playoutcenter.de | ⚠️ ממסר צד שלישי | 1 |
| `livestream2.bokradio.co.za` | livestream2.bokradio.co.za | ⚠️ ממסר צד שלישי | 1 |
| `yuppnimrestreammum.akamaized.net` | yuppnimrestreammum.akamaized.net | ⚠️ ממסר צד שלישי | 1 |
| `tvboom.ro` | tvboom.ro | ⚠️ ממסר צד שלישי | 1 |
| `cmc-cmctv-cineverse.amagi.tv` | cmc-cmctv-cineverse.amagi.tv | ⚠️ ממסר צד שלישי | 1 |
| `eco-live.mediacast.co.il` | eco99fm – Mediacast (רשמי) | ✅ רשמי | 1 |
| `102.livecdn.biz` | 102FM – LiveCDN (רשמי) | ✅ רשמי | 1 |
| `kzradio.mediacast.co.il` | רדיו הקצה – Mediacast (רשמי) | ✅ רשמי | 1 |
| `1075.livecdn.biz` | רדיו חיפה – LiveCDN (רשמי) | ✅ רשמי | 1 |
| `cast.radionahariya.com` | רדיו נהריה – שרת התחנה | ✅ רשמי | 1 |
| `www.rs.mizrahit.fm` | www.rs.mizrahit.fm | ⚠️ ממסר צד שלישי | 1 |
| `icecast.live` | icecast.live | ⚠️ ממסר צד שלישי | 1 |
| `stream10.vestaradio.com` | stream10.vestaradio.com | ⚠️ ממסר צד שלישי | 1 |
| `995.livecdn.biz` | 99.5FM – LiveCDN (רשמי) | ✅ רשמי | 1 |
| `radio.diki-mizrahit.co.il` | רדיו שירי דיכאון – השרת של התחנה | ✅ רשמי | 1 |
| `listen6.myradio24.com` | listen6.myradio24.com | ⚠️ ממסר צד שלישי | 1 |
| `streaming.radio.co` | streaming.radio.co | ⚠️ ממסר צד שלישי | 1 |
| `rs.mizrahit.fm` | rs.mizrahit.fm | ⚠️ ממסר צד שלישי | 1 |
| `play.radioking.io` | play.radioking.io | ⚠️ ממסר צד שלישי | 1 |
| `online.star33radio.com` | online.star33radio.com | ⚠️ ממסר צד שלישי | 1 |
| `176.9.7.145` | 176.9.7.145 | ⚠️ ממסר צד שלישי | 1 |

## 📺 ערוצי ישראל - שידור חי

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 1 | קשת 12 HD | קשת – ה-CDN של mako (עם הרשאת צפייה של mako) | ✅ רשמי | iptv-org (`Keshet12.il`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 2 | רשת 13 HD | רשת 13 – AWS CloudFront | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 3 | כאן 11 HD | כאן – תאגיד השידור (CDN רשמי) | ✅ רשמי | iptv-org (`Kan11.il`) | ✅ |
| 4 | עכשיו 14 HD | Redge – ה-CDN הרשמי של כאן, ערוץ 14 וכלכלה 10 | ✅ רשמי | iptv-org (`Now14.il`) | ✅ |
| 5 | i24NEWS עברית HD | i24NEWS – שרת רשמי | ✅ רשמי | iptv-org (`i24NEWSHebrew.il`) | ✅ |
| 6 | ערוץ 9 ישראל HD | GoStreaming – ספק השידור של ערוץ 9 | ✅ רשמי | iptv-org (`Channel9.il`) | ✅ |
| 7 | ערוץ 24 HD | קשת – ה-CDN של mako (עם הרשאת צפייה של mako) | ✅ רשמי | iptv-org (`Channel24.il`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 8 | מכאן 33 HD | כאן – תאגיד השידור (CDN רשמי) | ✅ רשמי | iptv-org (`Makan33.il`) | ✅ |
| 9 | ערוץ הכנסת 99 | ערוץ הכנסת – השידור של הכנסת (GoStreaming) | ✅ רשמי | iptv-org (`KnessetChannel.il`) | ✅ |
| 10 | i24NEWS צרפתית HD | i24NEWS – שרת רשמי | ✅ רשמי | iptv-org (`i24NEWSFrench.il`) | ✅ |
| 11 | i24NEWS ערבית HD | i24NEWS – שרת רשמי | ✅ רשמי | iptv-org (`i24NEWSArabic.il`) | ✅ |
| 12 | i24NEWS English | i24NEWS – שרת רשמי | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 13 | ILTV ישראל בצרפתית | live.creacast.com | ⚠️ ממסר צד שלישי | iptv-org (`ILTV.fr`) | ✅ |
| 14 | ynet Live | ynet – השרת של ידיעות אחרונות | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 15 | כאן 11 – כתוביות | Redge – ה-CDN הרשמי של כאן, ערוץ 14 וכלכלה 10 | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 16 | רשת 13 – נגיש | רשת 13 – השרת הרשמי (G-Mana) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 17 | ערוץ הכנסת – נגיש | ערוץ הכנסת – השידור של הכנסת (GoStreaming) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |

## 📺 ילדים ונוער (בעברית)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 18 | כאן חינוכית 23 HD | כאן – תאגיד השידור (CDN רשמי) | ✅ רשמי | iptv-org (`KanEducational.il`) | ✅ |
| 19 | ערוץ הופ! ילדים בעברית HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Hop.il`) | ❌ רק באפליקציה (HTTP) |
| 20 | ZOOM ילדים ונוער | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Zoom.il`) | ❌ רק באפליקציה (HTTP) |

## 📺 ספורט (Sports)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 121 | Sport 5 אוקראינה | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Sport5.ua`) | ❌ רק באפליקציה (HTTP) |
| 122 | Sport 2 אוקראינה | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Sport2.ua`) | ❌ רק באפליקציה (HTTP) |
| 123 | Sport 3 אוקראינה | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Sport3.ua`) | ❌ רק באפליקציה (HTTP) |
| 124 | Sport 4 אוקראינה | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Sport4.ua`) | ❌ רק באפליקציה (HTTP) |
| 125 | ספורט פלוס HD (viju+ Sport) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`vijuPlusSport.ru`) | ✅ דרך מקור חלופי: Cinerama.uz – ספק IPTV באוזבקיסטן |
| 126 | ספורט סטאנטה Setanta Sports 2 HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`SetantaSports2Eurasia.ie`) | ❌ רק באפליקציה (HTTP) |
| 127 | Red Bull TV HD | Red Bull TV – Akamai (רשמי) | ✅ רשמי | iptv-org (`RedBullTV.at`) | ✅ |
| 128 | ערוץ אקסטרים Extreme Sports HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 129 | Sport 1 אוקראינה | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Sport1.ua`) | ❌ רק באפליקציה (HTTP) |
| 130 | ספורט 5 – שידור חי | ספורט 5 – השידור הרשמי של RGE (Akamai) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 131 | ACC Digital Network | raycom-accdn-firetv.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`ACCDigitalNetwork.us`) | ✅ |
| 132 | talkSPORT | af7a8b4e.wurl.com | 🌐 ציבורי (iptv-org) | iptv-org (`talkSPORT.uk`) | ✅ |
| 133 | Kozoom TV | streams2.sofast.tv | 🌐 ציבורי (iptv-org) | iptv-org (`KozoomTV.fr`) | ✅ |
| 134 | More Than Sports TV | mts1.iptv-playoutcenter.de | 🌐 ציבורי (iptv-org) | iptv-org (`MoreThanSportsTV.de`) | ✅ |
| 135 | ACI Sport TV | webstream.multistream.it | 🌐 ציבורי (iptv-org) | iptv-org (`ACISportTV.it`) | ✅ |
| 136 | MMA-TV.com | streams2.sofast.tv | 🌐 ציבורי (iptv-org) | iptv-org (`MMATVcom.ru`) | ✅ |
| 137 | DiviSport | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`DiviSport.ua`) | ❌ רק באפליקציה (HTTP) |
| 138 | TSN The Ocho | d3pnbvng3bx2nj.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`TSNTheOcho.ca`) | ✅ |
| 139 | A Spor | rnttwmjcin.turknet.ercdn.net | 🌐 ציבורי (iptv-org) | iptv-org (`ASpor.tr`) | ✅ |
| 140 | DD Sports | d3qs3d2rkhfqrt.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`DDSports.in`) | ✅ |
| 141 | CCTV-Billiards | 38.75.136.137 | 🌐 ציבורי (iptv-org) | iptv-org (`CCTVBilliards.cn`) | ❌ רק באפליקציה (HTTP) |
| 142 | CazeTV | 138.204.86.4 | 🌐 ציבורי (iptv-org) | iptv-org (`CazeTV.br`) | ❌ רק באפליקציה (HTTP) |
| 143 | Azteca Deportes Network | live-manifest.production-public.tubi.io | 🌐 ציבורי (iptv-org) | iptv-org (`AztecaDeportesNetwork.mx`) | ✅ |
| 144 | DeporTV | 158.172.155.130 | 🌐 ציבורי (iptv-org) | iptv-org (`DeporTV.ar`) | ❌ רק באפליקציה (HTTP) |
| 145 | Fast&FunBox | Antik Telecom (סלובקיה), נתיבי test | ⚠️ ממסר צד שלישי | iptv-org (`FastFunBox.nl`) | ✅ |
| 146 | Pluto TV Snooker 900 | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVSnooker900.se`) | ✅ |
| 147 | Sporty TV | dash2.antik.sk | 🌐 ציבורי (iptv-org) | iptv-org (`SportyTV.cz`) | ✅ |
| 148 | Formula 1 AntenaPlay | live4dai.antenaplay.ro | 🌐 ציבורי (iptv-org) | iptv-org (`Formula1AntenaPlay.ro`) | ✅ |
| 149 | CRTV | stmv2.voxtvhd.com.br | 🌐 ציבורי (iptv-org) | iptv-org (`CRTV.cl`) | ✅ |
| 150 | Monterrico TV | h.jcp.live.opencaster.com | 🌐 ציבורי (iptv-org) | iptv-org (`MonterricoTV.pe`) | ✅ |
| 151 | T Sports 7 | live-us1.thaimomo.com | 🌐 ציבורי (iptv-org) | iptv-org (`TSports7.th`) | ✅ |
| 152 | Dong Nai TV 2 | freem3u.xyz | 🌐 ציבורי (iptv-org) | iptv-org (`DongNaiTV2.vn`) | ✅ |
| 153 | M Sports | cdn.rabta.stream | 🌐 ציבורי (iptv-org) | iptv-org (`MSports.pk`) | ✅ |
| 154 | Arryadia | 149.100.11.252 | 🌐 ציבורי (iptv-org) | iptv-org (`Arryadia.ma`) | ❌ רק באפליקציה (HTTP) |
| 155 | KTV Sport | kwtspta.cdn.mangomolo.com | 🌐 ציבורי (iptv-org) | iptv-org (`KTVSport.kw`) | ✅ |
| 156 | Belarus-5 | ngtrk.dc.beltelecom.by | 🌐 ציבורי (iptv-org) | iptv-org (`Belarus5.by`) | ✅ |
| 157 | Bahrain Sports 1 | 5c7b683162943.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`BahrainSports1.bh`) | ✅ |
| 158 | Oman Sports TV | partneta.cdn.mgmlcdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`OmanSportsTV.om`) | ✅ |
| 159 | Talent TV | cast.talenttv.lk | 🌐 ציבורי (iptv-org) | iptv-org (`TalentTV.lk`) | ✅ |
| 160 | Al Iraqia Sport | imn-live.esite-lab.com | 🌐 ציבורי (iptv-org) | iptv-org (`AlIraqiaSport.iq`) | ✅ |
| 161 | El-Heddaf TV | live.elheddaftv.com | 🌐 ציבורי (iptv-org) | iptv-org (`ElHeddafTV.dz`) | ✅ |
| 162 | Jordan Sport | jrtv-live.ercdn.net | 🌐 ציבורי (iptv-org) | iptv-org (`JordanSport.jo`) | ✅ |
| 163 | Prime | cdn.bestream.io | 🌐 ציבורי (iptv-org) | iptv-org (`Prime.sy`) | ✅ |
| 164 | AS3 Sport TV | streamtv.as3sport.online | 🌐 ציבורי (iptv-org) | iptv-org (`AS3SportTV.ve`) | ✅ |
| 165 | Deportes TVC | streaming.onnethn.com | 🌐 ציבורי (iptv-org) | iptv-org (`DeportesTVC.hn`) | ✅ |
| 166 | FTV | master.tucableip.com | 🌐 ציבורי (iptv-org) | iptv-org (`FTV.bo`) | ✅ |
| 167 | FUTV | 190.61.90.17 | 🌐 ציבורי (iptv-org) | iptv-org (`FUTV.cr`) | ❌ רק באפליקציה (HTTP) |
| 168 | Futbol TV | stream3.cinerama.uz | 🌐 ציבורי (iptv-org) | iptv-org (`FutbolTV.uz`) | ❌ רק באפליקציה (HTTP) |
| 169 | MNB Sport | live.mnb.mn | 🌐 ציבורי (iptv-org) | iptv-org (`MNBSport.mn`) | ✅ |
| 170 | Turkmenistan Sport | alpha.tv.online.tm | 🌐 ציבורי (iptv-org) | iptv-org (`TurkmenistanSport.tm`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 171 | American Ninja Warrior | d4whmvwm0rdvi.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`AmericanNinjaWarrior.us`) | ✅ |
| 172 | FIFA+ | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`FIFAPlus.uk`) | ✅ |
| 173 | Trace Sport Stars | lightning-tracesport-samsungau.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`TraceSportStars.fr`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 174 | World of Freesports | mainstreammedia-worldoffreesportsintl-rakuten.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`WorldofFreesports.de`) | ✅ |
| 175 | Inter TV | d3k8wzt41aflvx.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`InterTV.it`) | ✅ |
| 176 | Astrahan.Ru Sport | streaming.astrakhan.ru | 🌐 ציבורי (iptv-org) | iptv-org (`AstrahanRuSport.ru`) | ✅ |
| 177 | Maincast Cybersport | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`MaincastCybersport.ua`) | ❌ רק באפליקציה (HTTP) |
| 178 | Game+ | a-cdn.klowdtv.com | 🌐 ציבורי (iptv-org) | iptv-org (`GamePlus.ca`) | ✅ |
| 179 | TJK TV | tjktv-live.tjk.org | 🌐 ציבורי (iptv-org) | iptv-org (`TJKTV.tr`) | ✅ |
| 180 | Kabaddi 24x7 | samitaorigin.tangotv.in | 🌐 ציבורי (iptv-org) | iptv-org (`Kabaddi24x7.in`) | ✅ |
| 181 | CCTV-Golf & Tennis | 38.75.136.137 | 🌐 ציבורי (iptv-org) | iptv-org (`CCTVGolfTennis.cn`) | ❌ רק באפליקציה (HTTP) |
| 182 | ITV Deportes | thm-it-roku.otteravision.com | 🌐 ציבורי (iptv-org) | iptv-org (`ITVDeportes.mx`) | ✅ |
| 183 | FightBox | nosignal1.antenaplay.ro | 🌐 ציבורי (iptv-org) | iptv-org (`FightBox.nl`) | ✅ |
| 184 | ATG Live | kanal75xto-llhls.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`ATGLive.se`) | ✅ |
| 185 | NHL AntenaPlay | live4dai.antenaplay.ro | 🌐 ציבורי (iptv-org) | iptv-org (`NHLAntenaPlay.ro`) | ✅ |
| 186 | Teletrak TV | unlimited6-cl.dps.live | 🌐 ציבורי (iptv-org) | iptv-org (`TeletrakTV.cl`) | ✅ |
| 187 | Ovacion TV | cdn2.ujjina.com | 🌐 ציבורי (iptv-org) | iptv-org (`OvacionTV.pe`) | ❌ רק באפליקציה (HTTP) |
| 188 | PK Sports | lbgo.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`PKSports.pk`) | ✅ |
| 189 | KTV Sport Plus | kwtsplta.cdn.mangomolo.com | 🌐 ציבורי (iptv-org) | iptv-org (`KTVSportPlus.kw`) | ✅ |
| 190 | Sport TV | media1.skifnet.by | 🌐 ציבורי (iptv-org) | iptv-org (`SportTV.by`) | ❌ רק באפליקציה (HTTP) |
| 191 | Bahrain Sports 2 | 5c7b683162943.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`BahrainSports2.bh`) | ✅ |
| 192 | Colimdo TV | cnn.livestreaminggroup.info | 🌐 ציבורי (iptv-org) | iptv-org (`ColimdoTV.do`) | ✅ |
| 193 | DraftKings Network | na.linear.zype.com | 🌐 ציבורי (iptv-org) | iptv-org (`DraftKingsNetwork.us`) | ✅ |
| 194 | FIFA+ Women | cffda8ff.wurl.com | 🌐 ציבורי (iptv-org) | iptv-org (`FIFAPlusWomen.uk`) | ✅ |
| 195 | Persiana Fight | fighthls.persiana.live | 🌐 ציבורי (iptv-org) | iptv-org (`PersianaFight.fr`) | ✅ |
| 196 | DAZN Darts x Pluto TV | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`DAZNDartsxPlutoTV.de`) | ✅ |
| 197 | MadeinBO TV | srvx1.selftv.video | 🌐 ציבורי (iptv-org) | iptv-org (`MadeinBOTV.it`) | ✅ |
| 198 | Russian Extreme Ultra | 46.53.200.153 | 🌐 ציבורי (iptv-org) | iptv-org (`RussianExtremeUltra.ru`) | ❌ רק באפליקציה (HTTP) |
| 199 | Maincast Sport | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`MaincastSport.ua`) | ❌ רק באפליקציה (HTTP) |
| 200 | HTSpor TV | ciner.daioncdn.net | 🌐 ציבורי (iptv-org) | iptv-org (`HTSporTV.tr`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 201 | CCTV-Storm Football | 38.75.136.137 | 🌐 ציבורי (iptv-org) | iptv-org (`CCTVStormFootball.cn`) | ❌ רק באפליקציה (HTTP) |
| 202 | AyM Sports | 168.196.127.137 | 🌐 ציבורי (iptv-org) | iptv-org (`AyMSports.mx`) | ❌ רק באפליקציה (HTTP) |
| 203 | Brondby TV | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`BrondbyTV.se`) | ✅ |
| 204 | TVR Sport | tvr-sport.lg.mncdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`TVRSport.ro`) | ✅ |
| 205 | Turf Movil | tvturf4.janus.cl | 🌐 ציבורי (iptv-org) | iptv-org (`TurfMovil.cl`) | ✅ |
| 206 | ESPN8: The Ocho | d3b6q2ou5kp8ke.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`ESPN8TheOcho.us`) | ✅ |
| 207 | MUTV | fastly.live.brightcove.com | 🌐 ציבורי (iptv-org) | iptv-org (`MUTV.uk`) | ✅ |
| 208 | DAZN Heldinnen x Pluto TV | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`DAZNHeldinnenxPlutoTV.de`) | ✅ |
| 209 | Sportitalia | edge-001.streamup.eu | 🌐 ציבורי (iptv-org) | iptv-org (`Sportitalia.it`) | ✅ |
| 210 | UDAR | 5.188.159.128 | 🌐 ציבורי (iptv-org) | iptv-org (`UDAR.ru`) | ❌ רק באפליקציה (HTTP) |
| 211 | FB TV | 1hskrdto.rocketcdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`FBTV.tr`) | ❌ רק באפליקציה (HTTP) |
| 212 | Claro Sports | 168.196.127.137 | 🌐 ציבורי (iptv-org) | iptv-org (`ClaroSports.mx`) | ❌ רק באפליקציה (HTTP) |
| 213 | FCK Lovinderne | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`FCKLovinderne.se`) | ✅ |
| 214 | FloHockey | amg02278-amg02278c2-flosports-worldwide-9916.playouts.now.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`FloHockey.us`) | ✅ |
| 215 | Strongman Champions League | rightsboosterltd-scl-1-be.samsung.wurl.tv | 🌐 ציבורי (iptv-org) | iptv-org (`StrongmanChampionsLeague.uk`) | ✅ |
| 216 | Pluto TV Sport | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVSport.de`) | ✅ |
| 217 | Nautical Channel | a-cdn.klowdtv.com | 🌐 ציבורי (iptv-org) | iptv-org (`NauticalChannel.it`) | ✅ |
| 218 | Pluto TV Handboll Highlights | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVHandbollHighlights.se`) | ✅ |
| 219 | FloRacing | amg02278-amg02278c1-flosports-worldwide-7592.playouts.now.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`FloRacing.us`) | ✅ |
| 220 | DAZN Combat | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`DAZNCombat.uk`) | ✅ |
| 221 | Horse TV | a-cdn.klowdtv.com | 🌐 ציבורי (iptv-org) | iptv-org (`HorseTV.it`) | ✅ |
| 222 | Pluto TV Handboll Live | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVHandbollLive.se`) | ✅ |
| 223 | NBC Sports NOW | d4whmvwm0rdvi.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`NBCSportsNOW.us`) | ✅ |
| 224 | TR Sport | livetr.teleromagna.it | 🌐 ציבורי (iptv-org) | iptv-org (`TRSport.it`) | ✅ |
| 225 | Pluto TV Sport | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVSport.se`) | ✅ |
| 226 | PGA Tour | d11k1mnrgfposz.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`PGATour.us`) | ✅ |
| 227 | Milan TV | team-tv.co | 🌐 ציבורי (iptv-org) | iptv-org (`MilanTV.it`) | ❌ רק באפליקציה (HTTP) |
| 228 | Pluto TV Fishing | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVFishing.se`) | ✅ |
| 229 | Racer Select | amg00378-amg00378c4-freelivesports-emea-5637.playouts.now.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`RacerSelect.us`) | ✅ |
| 230 | Pluto TV Handbold Highlights | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVHandboldHighlights.se`) | ✅ |
| 231 | Rally TV | rally-tv-live.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`RallyTV.us`) | ✅ |
| 232 | Pluto TV Handbold Live | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVHandboldLive.se`) | ✅ |
| 233 | SportsGrid | sportsgrid-tribal.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`SportsGrid.us`) | ✅ |
| 234 | Swerve Combat | linear-253.frequency.stream | 🌐 ציבורי (iptv-org) | iptv-org (`SwerveCombat.us`) | ✅ |
| 235 | Tennis Channel 2 | mumbai-edge.smartplaytv.in | 🌐 ציבורי (iptv-org) | iptv-org (`TennisChannel2.us`) | ✅ |
| 236 | beIN SPORTS XTRA en Espanol | dc1644a9jazgj.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`beINSPORTSXTRAenEspanol.us`) | ✅ |
| 237 | BEK Sports | cdn3.wowza.com | 🌐 ציבורי (iptv-org) | iptv-org (`BEKSports.us`) | ✅ |
| 238 | Glory Kickboxing | 6f972d29.wurl.com | 🌐 ציבורי (iptv-org) | iptv-org (`GloryKickboxing.us`) | ✅ |
| 239 | Golazo Network | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`GolazoNetwork.us`) | ✅ |
| 240 | Lacrosse TV | a-cdn.klowdtv.com | 🌐 ציבורי (iptv-org) | iptv-org (`LacrosseTV.us`) | ✅ |
| 241 | Monster Jam | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`MonsterJam.us`) | ✅ |
| 242 | PBR RidePass | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PBRRidePass.us`) | ✅ |
| 243 | Right Now TV | a-cdn.klowdtv.com | 🌐 ציבורי (iptv-org) | iptv-org (`RightNowTV.us`) | ✅ |
| 244 | Slopes TV | stream-us-east-1.getpublica.com | 🌐 ציבורי (iptv-org) | iptv-org (`SlopesTV.us`) | ✅ |
| 245 | Stadium | wurl120sports.global.transmit.live | 🌐 ציבורי (iptv-org) | iptv-org (`Stadium.us`) | ✅ |
| 246 | Swerve Sports | a-cdn.klowdtv.com | 🌐 ציבורי (iptv-org) | iptv-org (`SwerveSports.us`) | ✅ |
| 247 | TVS Bowling Network | rpn.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`TVSBowlingNetwork.us`) | ✅ |
| 248 | TVS Sports | rpn.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`TVSSports.us`) | ✅ |
| 249 | TVS Women Sports | rpn.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`TVSWomenSports.us`) | ✅ |
| 250 | Willow | tvsen5.aynaott.com | 🌐 ציבורי (iptv-org) | iptv-org (`Willow.us`) | ✅ |
| 251 | XFC TV | stream-us-east-1.getpublica.com | 🌐 ציבורי (iptv-org) | iptv-org (`XFCTV.us`) | ✅ |
| 252 | Strongman | rightsboosterltd-scl-2-eu.rakuten.wurl.tv | 🌐 ציבורי (iptv-org) | iptv-org (`Strongman.us`) | ✅ |
| 253 | TVS Boxing | rpn.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`TVSBoxing.us`) | ✅ |
| 254 | TVS Classic Sports | rpn.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`TVSClassicSports.us`) | ✅ |
| 255 | TVS Turbo | rpn.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`TVSTurbo.us`) | ✅ |
| 256 | Bellator MMA | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`BellatorMMA.us`) | ✅ |
| 257 | Pluto TV Competition | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVCompetition.us`) | ✅ |
| 258 | PokerGo | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PokerGo.us`) | ✅ |
| 259 | Sport 1 בלטיק HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 1594 | 30A Golf Kingdom HD | 30a-tv.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 1595 | ACC Network HD | 23.239.31.26 | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 1596 | ADO TV HD | strhls.streamakaci.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 1597 | beIN SPORTS XTRA HD | bein-xtra-bein.amagi.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 1598 | Belarus-5 HD HD | 178.124.179.122 | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 1599 | Belarus-5 Internet HD | edge59.dc.beltelecom.by | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 1600 | Canal Showsport HD | livestream.lumpentv.com.ar | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 1601 | CBS Sports Golazo Network HD | dai.google.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 1602 | CBS Sports HQ HD | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 1603 | CDN Deportes HD | hls.tvabierta.net | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 1604 | CRTV Sports and Entertainment SD HD | live1.acangroup.org | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 1605 | DD Sports HD HD | cdn.pishow.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 1606 | Dynamo Kyiv TV HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 1607 | Eleven Sports 1 HD | 92.114.85.87 | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 1608 | Eleven Sports 2 HD | 92.114.85.87 | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 1609 | Eleven Sports 3 HD | 92.114.85.87 | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 1610 | Eleven Sports 4 HD | 92.114.85.87 | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 1611 | Equalympic HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 1612 | Equidia HD | raw.githubusercontent.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 1613 | ESPN 2  HD HD | 85.237.89.160 | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 1614 | ESPN 2 Latin America HD | barinaslive1.boldmss.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 1615 | ESPN 2 Latin America Colombia HD | 190.61.47.54 | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 1616 | ESPN 3 Latin America HD | barinaslive1.boldmss.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 1617 | ESPN 4 HD | 181.78.197.59 | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 1618 | ESPN 4 Latin America HD | barinaslive1.boldmss.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 1619 | ESPN 5 Latin America HD | barinaslive1.boldmss.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 1620 | ESPN 6 Latin America HD | barinaslive1.boldmss.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 1621 | ESPN 7 Latin America HD | barinaslive1.boldmss.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 1622 | ESPN HD | 181.78.197.59 | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 1623 | ESPN  HD HD | 85.237.89.160 | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 1624 | ESPN Latin America HD | barinaslive1.boldmss.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 1625 | ESPN Latin America Colombia HD | 190.61.47.54 | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 1626 | ESPN Premium  HD HD | 158.172.155.130 | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 1627 | ESPNU HD HD | 85.237.89.160 | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 1628 | Esport3 Originals HD | directes-tv-int.3catdirectes.cat | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 1629 | Eurosport HD | drk6xq0vhn.gpcdn.net | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 1630 | FIFA+ French HD | 37b4c228.wurl.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 1631 | FIFA+ German HD | 4397879b.wurl.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 1632 | FIFA+ Hispanic America HD | 6c849fb3.wurl.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 1633 | FIFA+ Italy HD | 5d95f7d7.wurl.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 1634 | FIFA+ Portuguese HD | e3be9ac5.wurl.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 1635 | FIFA+ Spain HD | d63fabad.wurl.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 1636 | FIFA+ United States HD | d2w9q46ikgrcwx.cloudfront.net | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 1637 | FightBox Poland HD | 92.114.85.87 | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 1638 | Fox Sports 1 HD | 85.237.89.160 | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 1639 | Fox Sports 2 HD | tvsen7.aynascope.net | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 1640 | Fox Sports HD | 138.59.227.20 | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 1641 | Fox Sports News HD | skynewsau-live-premium.akamaized.net | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 1642 | FTF Sports HD | 1593604785.rsc.cdn77.org | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 1643 | Fubo Sports Network HD | dnf08l6u6uxnz.cloudfront.net | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 1644 | World Poker Tour UK HD | amg00477-samsungelectron-worldpokertour-samsunguk-81igb.amagi.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 1645 | WWE Network HD | 103.151.60.162 | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |

## 📺 חדשות מהעולם (News)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 260 | Black News Channel | blacknewschannel-xumo-us.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`BlackNewsChannel.us`) | ✅ |
| 261 | Iran International | hlspackager.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`IranInternational.uk`) | ✅ |
| 262 | Africa 24 English | edge20.vedge.infomaniak.com | 🌐 ציבורי (iptv-org) | iptv-org (`Africa24English.fr`) | ✅ |
| 263 | DW | dwamdstream103.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`DW.de`) | ✅ |
| 264 | M95 Television Marbella | limited2.todostreaming.es | 🌐 ציבורי (iptv-org) | iptv-org (`M95TelevisionMarbella.es`) | ✅ |
| 265 | Rai News 24 | d3k8wzt41aflvx.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`RaiNews24.it`) | ✅ |
| 266 | RT | rt-glb.rttv.com | 🌐 ציבורי (iptv-org) | iptv-org (`RT.ru`) | ✅ |
| 267 | FREEDOM | dash4.antik.sk | 🌐 ציבורי (iptv-org) | iptv-org (`FREEDOM.ua`) | ✅ |
| 268 | Hamdard TV | tv.hamdardtv.com | 🌐 ציבורי (iptv-org) | iptv-org (`HamdardTV.ca`) | ✅ |
| 269 | News24 | skynewsau-live-premium.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`News24.au`) | ✅ |
| 270 | Al Arabiya Programs | live.alarabiya.net | 🌐 ציבורי (iptv-org) | iptv-org (`AlArabiyaPrograms.ae`) | ✅ |
| 271 | Al Araby TV | live.kwikmotion.com | 🌐 ציבורי (iptv-org) | iptv-org (`AlArabyTV.qa`) | ✅ |
| 272 | Al Hadath | av.alarabiya.net | 🌐 ציבורי (iptv-org) | iptv-org (`AlHadath.sa`) | ✅ |
| 273 | Al Ghad TV | eazyvwqssi.erbvr.com | 🌐 ציבורי (iptv-org) | iptv-org (`AlGhadTV.eg`) | ✅ |
| 274 | Red TV Lebanon | live.kwikmotion.com | 🌐 ציבורי (iptv-org) | iptv-org (`RedTVLebanon.lb`) | ✅ |
| 275 | TRT World | tv-trtworld.medya.trt.com.tr | 🌐 ציבורי (iptv-org) | iptv-org (`TRTWorld.tr`) | ✅ |
| 276 | CNN News 18 | n18syndication.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`CNNNews18.in`) | ✅ |
| 277 | NHK World-Japan | masterpl.hls.nhkworld.jp | 🌐 ציבורי (iptv-org) | iptv-org (`NHKWorldJapan.jp`) | ✅ |
| 278 | Arirang TV | freem3u.xyz | 🌐 ציבורי (iptv-org) | iptv-org (`ArirangTV.kr`) | ✅ |
| 279 | CGTN | amg00405-rakutentv-cgtn-rakuten-i9tar.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`CGTN.cn`) | ✅ |
| 280 | CNN Brasil | d25usgadhphvwi.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`CNNBrasil.br`) | ✅ |
| 281 | Icrtv Colima | 5fc584f3f19c9.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`IcrtvColima.mx`) | ✅ |
| 282 | Canal 26 | stream-gtlc.telecentro.net.ar | 🌐 ציבורי (iptv-org) | iptv-org (`Canal26.ar`) | ✅ |
| 283 | ARTV | playout172.livextend.cloud | 🌐 ציבורי (iptv-org) | iptv-org (`ARTV.pt`) | ✅ |
| 284 | EbS | streams.prd.commavservices.eu | 🌐 ציבורי (iptv-org) | iptv-org (`EbS.be`) | ✅ |
| 285 | Meteonews | streaming.meteonews.net | 🌐 ציבורי (iptv-org) | iptv-org (`Meteonews.ch`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 286 | Euronews | cdn-euronews.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`Euronews.pl`) | ✅ |
| 287 | Astra TV | server.gointernet.gr | 🌐 ציבורי (iptv-org) | iptv-org (`AstraTV.gr`) | ✅ |
| 288 | Miracle Channel | live.miracleonline.org | 🌐 ציבורי (iptv-org) | iptv-org (`MiracleChannel.se`) | ✅ |
| 289 | TV from the Danish Parliament | cdnapi.kaltura.com | 🌐 ציבורי (iptv-org) | iptv-org (`TVfromtheDanishParliament.dk`) | ✅ |
| 290 | Nopola News | virta2.nopolanews.fi | 🌐 ציבורי (iptv-org) | iptv-org (`NopolaNews.fi`) | ✅ |
| 291 | Current Time TV | rfe-ingest.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`CurrentTimeTV.cz`) | ✅ |
| 292 | Dotto TV | live.dottotv.ro | 🌐 ציבורי (iptv-org) | iptv-org (`DottoTV.ro`) | ✅ |
| 293 | NCTV | pantera1-100gb-cl-movistar.dps.live | 🌐 ציבורי (iptv-org) | iptv-org (`NCTV.cl`) | ✅ |
| 294 | Noticiero 90 Minutos | play.cdn.enetres.net | 🌐 ציבורי (iptv-org) | iptv-org (`Noticiero90Minutos.co`) | ✅ |
| 295 | RPP TV | redirector.rudo.video | 🌐 ציבורי (iptv-org) | iptv-org (`RPPTV.pe`) | ✅ |
| 296 | BTV | lnd0t3b922.tenbytecdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`BTV.id`) | ✅ |
| 297 | 13 Siam Thai | live.x2.co.th | 🌐 ציבורי (iptv-org) | iptv-org (`13SiamThai.th`) | ✅ |
| 298 | Dong Nai TV 1 | freem3u.xyz | 🌐 ציבורי (iptv-org) | iptv-org (`DongNaiTV1.vn`) | ✅ |
| 299 | Neo News | vodzong.mjunoon.tv | 🌐 ציבורי (iptv-org) | iptv-org (`NeoNews.pk`) | ✅ |
| 300 | Mihan TV | iptv.mihantv.com | 🌐 ציבורי (iptv-org) | iptv-org (`MihanTV.ir`) | ✅ |
| 301 | Medi1TV Arabic | cdn.live.easybroadcast.io | 🌐 ציבורי (iptv-org) | iptv-org (`Medi1TVArabic.ma`) | ✅ |
| 302 | LN24SA | cdnstack.internetmultimediaonline.org | 🌐 ציבורי (iptv-org) | iptv-org (`LN24SA.za`) | ✅ |
| 303 | Silverbird News 24 | viewmedia7219.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`SilverbirdNews24.ng`) | ✅ |
| 304 | Al Sharqiya News | indexforce.com | 🌐 ציבורי (iptv-org) | iptv-org (`AlSharqiyaNews.iq`) | ✅ |
| 305 | Carivision | ss2.tvrdomi.com | 🌐 ציבורי (iptv-org) | iptv-org (`Carivision.do`) | ✅ |
| 306 | HCH | live.streamhch.com | 🌐 ציבורי (iptv-org) | iptv-org (`HCH.hn`) | ✅ |
| 307 | CNA | live1.mediadesk.al | 🌐 ציבורי (iptv-org) | iptv-org (`CNA.al`) | ✅ |
| 308 | Halab Today TV | halabtoday-live.lg.mncdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`HalabTodayTV.sy`) | ✅ |
| 309 | Canal I | streaming.canal-i.com | 🌐 ציבורי (iptv-org) | iptv-org (`CanalI.ve`) | ✅ |
| 310 | CEACOM TV | eu1.servers10.com | 🌐 ציבורי (iptv-org) | iptv-org (`CEACOMTV.bo`) | ✅ |
| 311 | Antik Info TV | dash4.antik.sk | 🌐 ציבורי (iptv-org) | iptv-org (`AntikInfoTV.sk`) | ✅ |
| 312 | Baku.TV | rtmp.baku.tv | 🌐 ציבורי (iptv-org) | iptv-org (`BakuTV.az`) | ✅ |
| 313 | KTV 2 | kwtktv2ta.cdn.mangomolo.com | 🌐 ציבורי (iptv-org) | iptv-org (`KTV2.kw`) | ✅ |
| 314 | NW Info 2 | hls.newworldtv.com | 🌐 ציבורי (iptv-org) | iptv-org (`NWInfo2.tg`) | ✅ |
| 315 | Belarus-24 | ngtrk.dc.beltelecom.by | 🌐 ציבורי (iptv-org) | iptv-org (`Belarus24.by`) | ✅ |
| 316 | Al Masirah | live.cdnbridge.tv | 🌐 ציבורי (iptv-org) | iptv-org (`AlMasirah.ye`) | ✅ |
| 317 | Al Najah News | streaming.najah.edu | 🌐 ציבורי (iptv-org) | iptv-org (`AlNajahNews.ps`) | ✅ |
| 318 | Altaghier TV | jmc-live.ercdn.net | 🌐 ציבורי (iptv-org) | iptv-org (`AltaghierTV.jo`) | ✅ |
| 319 | TV Legislativa | streamingav.asambleanacional.gob.ec | 🌐 ציבורי (iptv-org) | iptv-org (`TVLegislativa.ec`) | ✅ |
| 320 | Ekushey TV | ekusheyserver.com | 🌐 ציבורי (iptv-org) | iptv-org (`EkusheyTV.bd`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 321 | Formula | c4635.cdn.xsg.ge | 🌐 ציבורי (iptv-org) | iptv-org (`Formula.ge`) | ✅ |
| 322 | Privesc.Eu TV | cachestar.privesc.eu | 🌐 ציבורי (iptv-org) | iptv-org (`PrivescEuTV.md`) | ✅ |
| 323 | CNA | d2e1asnsl7br7b.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`CNA.sg`) | ✅ |
| 324 | Guardian Talk Radio | cdn-edge1.streamcomedia.com | 🌐 ציבורי (iptv-org) | iptv-org (`GuardianTalkRadio.bs`) | ✅ |
| 325 | Parliament TV | ptvlive.kordia.net.nz | 🌐 ציבורי (iptv-org) | iptv-org (`ParliamentTV.nz`) | ✅ |
| 326 | Al Masar TV | starmenajo.com | 🌐 ציבורי (iptv-org) | iptv-org (`AlMasarTV.ly`) | ✅ |
| 327 | AL24 News | cdn.live.easybroadcast.io | 🌐 ציבורי (iptv-org) | iptv-org (`AL24News.dz`) | ✅ |
| 328 | Dabanga TV | hls.dabangasudan.org | 🌐 ציבורי (iptv-org) | iptv-org (`DabangaTV.sd`) | ✅ |
| 329 | Oman TV Mubashir | partwota.cdn.mgmlcdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`OmanTVMubashir.om`) | ✅ |
| 330 | Sahara 24 | live.saharamedias.net | 🌐 ציבורי (iptv-org) | iptv-org (`Sahara24.mr`) | ✅ |
| 331 | 7 Info | video1.getstreamhosting.com | 🌐 ציבורי (iptv-org) | iptv-org (`7Info.ci`) | ✅ |
| 332 | Baraka Television | stream.berosat.live | 🌐 ציבורי (iptv-org) | iptv-org (`BarakaTelevision.cd`) | ✅ |
| 333 | Haiti News Channel | cdn.haititivi.com | 🌐 ציבורי (iptv-org) | iptv-org (`HaitiNewsChannel.ht`) | ✅ |
| 334 | Monaco Info | webtv.monacoinfo.com | 🌐 ציבורי (iptv-org) | iptv-org (`MonacoInfo.mc`) | ✅ |
| 335 | Guatevision | mdstrm.com | 🌐 ציבורי (iptv-org) | iptv-org (`Guatevision.gt`) | ✅ |
| 336 | Norte Informativo TV | videohd.live | 🌐 ציבורי (iptv-org) | iptv-org (`NorteInformativoTV.cr`) | ✅ |
| 337 | Prensa Latina TV | eu1.servers10.com | 🌐 ציבורי (iptv-org) | iptv-org (`PrensaLatinaTV.cu`) | ✅ |
| 338 | Radio Ancon | www.streaming507.net | 🌐 ציבורי (iptv-org) | iptv-org (`RadioAncon.pa`) | ✅ |
| 339 | Brian TV | livefta.malimarcdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`BrianTV.la`) | ✅ |
| 340 | DVB TV | live-stream.dvb.no | 🌐 ציבורי (iptv-org) | iptv-org (`DVBTV.mm`) | ✅ |
| 341 | First Channel News | amtvusdvr.tulix.tv | 🌐 ציבורי (iptv-org) | iptv-org (`FirstChannelNews.am`) | ✅ |
| 342 | Newsmax Balkans | newsmaxadria.mts-si.tv | 🌐 ציבורי (iptv-org) | iptv-org (`NewsmaxBalkans.ba`) | ✅ |
| 343 | TOLOnews | tgn.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`TOLOnews.af`) | ✅ |
| 344 | TV Arta | gjirafa-video-live.gjirafa.net | 🌐 ציבורי (iptv-org) | iptv-org (`TVArta.xk`) | ✅ |
| 345 | TV Jadran | tvjadran.stream.agatin.hr | 🌐 ציבורי (iptv-org) | iptv-org (`TVJadran.hr`) | ✅ |
| 346 | Visir | live.visir.is | 🌐 ציבורי (iptv-org) | iptv-org (`Visir.is`) | ✅ |
| 347 | BX Inform | reflect-stream-bronxnet.cablecast.tv | 🌐 ציבורי (iptv-org) | iptv-org (`BXInform.us`) | ✅ |
| 348 | Dunya News UK | ukintl.dunyanews.tv | 🌐 ציבורי (iptv-org) | iptv-org (`DunyaNewsUK.uk`) | ✅ |
| 349 | Africa 24 | africa24.vedge.infomaniak.com | 🌐 ציבורי (iptv-org) | iptv-org (`Africa24.fr`) | ✅ |
| 350 | 3Cat Cameres del temps | directes-tv-int.3catdirectes.cat | 🌐 ציבורי (iptv-org) | iptv-org (`3CatCameresdeltemps.es`) | ✅ |
| 351 | Senato TV | senato-live.morescreens.com | 🌐 ציבורי (iptv-org) | iptv-org (`SenatoTV.it`) | ✅ |
| 352 | 360° News | live-vgtrksmotrim.cdnvideo.ru | 🌐 ציבורי (iptv-org) | iptv-org (`360News.ru`) | ✅ |
| 353 | Channel 24 | streamvideol1.luxnet.ua | 🌐 ציבורי (iptv-org) | iptv-org (`Channel24.ua`) | ✅ |
| 354 | CityNews Calgary | citynewsregional.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`CityNewsCalgary.ca`) | ✅ |
| 355 | ticker News | live-hls-7agy.livepush.io | 🌐 ציבורי (iptv-org) | iptv-org (`tickerNews.au`) | ✅ |
| 356 | Al Yaum TV | hlspackager.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`AlYaumTV.ae`) | ✅ |
| 357 | Al Araby TV 2 | live.kwikmotion.com | 🌐 ציבורי (iptv-org) | iptv-org (`AlArabyTV2.qa`) | ✅ |
| 358 | Al Alam Al Yawm | mn-nl.mncdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`AlAlamAlYawm.sa`) | ✅ |
| 359 | Voice of Lebanon | Asharq Discovery – ItWorks (רשמי) | ✅ רשמי | iptv-org (`VoiceofLebanon.lb`) | ✅ |
| 360 | Mekameleen TV | mn-nl.mncdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`MekameleenTV.tr`) | ✅ |
| 361 | News18 Assam North-East | n18syndication.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`News18AssamNorthEast.in`) | ✅ |
| 362 | Weathernews | rch01e-alive-hls.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`Weathernews.jp`) | ✅ |
| 363 | Arirang UN | amdlive-ch02-ctnd-com.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`ArirangUN.kr`) | ✅ |
| 364 | STZ TV | cdn.live.br1.jmvstream.com | 🌐 ציבורי (iptv-org) | iptv-org (`STZTV.br`) | ✅ |
| 365 | AMX Noticias | 5e50264bd6766.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`AMXNoticias.mx`) | ✅ |
| 366 | VIVE TV | vivo.solumedia.com | 🌐 ציבורי (iptv-org) | iptv-org (`VIVETV.ar`) | ✅ |
| 367 | CNN Portugal | github.com | 🌐 ציבורי (iptv-org) | iptv-org (`CNNPortugal.pt`) | ✅ |
| 368 | EbS+ | streams.prd.commavservices.eu | 🌐 ציבורי (iptv-org) | iptv-org (`EbSPlus.be`) | ✅ |
| 369 | TVK Stella | stream.tvkstella.pl | 🌐 ציבורי (iptv-org) | iptv-org (`TVKStella.pl`) | ✅ |
| 370 | Hellenic Parliament TV | ert-ucdn.broadpeak-aas.com | 🌐 ציבורי (iptv-org) | iptv-org (`HellenicParliamentTV.gr`) | ✅ |
| 371 | Di TV | cdn0-03837-liveedge0.dna.ip-only.net | 🌐 ציבורי (iptv-org) | iptv-org (`DiTV.se`) | ✅ |
| 372 | CT24 | dash2.antik.sk | 🌐 ציבורי (iptv-org) | iptv-org (`CT24.cz`) | ✅ |
| 373 | Aleph News | stream-aleph.m.ro | 🌐 ציבורי (iptv-org) | iptv-org (`AlephNews.ro`) | ✅ |
| 374 | T13 En Vivo | redirector.dps.live | 🌐 ציבורי (iptv-org) | iptv-org (`T13EnVivo.cl`) | ✅ |
| 375 | NTN24 | stream1.bitred.cl | 🌐 ציבורי (iptv-org) | iptv-org (`NTN24.co`) | ✅ |
| 376 | Conecta2TV | live.obslivestream.com | 🌐 ציבורי (iptv-org) | iptv-org (`Conecta2TV.pe`) | ✅ |
| 377 | Lingkar TV | lingkartv.my.id | 🌐 ציבורי (iptv-org) | iptv-org (`LingkarTV.id`) | ✅ |
| 378 | Thai Parliament Television | tv-live.tpchannel.org | 🌐 ציבורי (iptv-org) | iptv-org (`ThaiParliamentTelevision.th`) | ✅ |
| 379 | Tra Vinh TV | freem3u.xyz | 🌐 ציבורי (iptv-org) | iptv-org (`TraVinhTV.vn`) | ✅ |
| 380 | Dunya News | imob.dunyanews.tv | 🌐 ציבורי (iptv-org) | iptv-org (`DunyaNews.pk`) | ✅ |
| 381 | Medi1TV Afrique | cdn.live.easybroadcast.io | 🌐 ציבורי (iptv-org) | iptv-org (`Medi1TVAfrique.ma`) | ✅ |
| 382 | SABC News | sabconetanw.cdn.mangomolo.com | 🌐 ציבורי (iptv-org) | iptv-org (`SABCNews.za`) | ✅ |
| 383 | News Central | wf.newscentral.ng | 🌐 ציבורי (iptv-org) | iptv-org (`NewsCentral.ng`) | ✅ |
| 384 | Alghadeer TV | sd343444.vodu.store | 🌐 ציבורי (iptv-org) | iptv-org (`AlghadeerTV.iq`) | ✅ |
| 385 | RDN | edge.essastream.com | 🌐 ציבורי (iptv-org) | iptv-org (`RDN.do`) | ✅ |
| 386 | Mega TV | www.desde-paraguay.com | 🌐 ציבורי (iptv-org) | iptv-org (`MegaTV.py`) | ✅ |
| 387 | Lenca TV | lencatelevision.com | 🌐 ציבורי (iptv-org) | iptv-org (`LencaTV.hn`) | ✅ |
| 388 | ntv | protokolldns.xyz | 🌐 ציבורי (iptv-org) | iptv-org (`ntv.al`) | ✅ |
| 389 | Syria TV | live.kwikmotion.com | 🌐 ציבורי (iptv-org) | iptv-org (`SyriaTV.sy`) | ✅ |
| 390 | CRTV News | live1.acangroup.org | 🌐 ציבורי (iptv-org) | iptv-org (`CRTVNews.cm`) | ✅ |
| 391 | Telesur | mblenmain01.telesur.ultrabase.net | 🌐 ציבורי (iptv-org) | iptv-org (`Telesur.ve`) | ✅ |
| 392 | UMSA TVU LP | edge.enhdtv.com | 🌐 ציבורי (iptv-org) | iptv-org (`UMSATVULP.bo`) | ✅ |
| 393 | TV NRSR | n11.stv.livebox.sk | 🌐 ציבורי (iptv-org) | iptv-org (`TVNRSR.sk`) | ✅ |
| 394 | APA Tv | stream.apa.tv | 🌐 ציבורי (iptv-org) | iptv-org (`APATv.az`) | ❌ רק באפליקציה (HTTP) |
| 395 | Fresh News | streaming-android.freshnewsasia.tv | 🌐 ציבורי (iptv-org) | iptv-org (`FreshNews.kh`) | ❌ רק באפליקציה (HTTP) |
| 396 | KTV News | kwtkbta.cdn.mangomolo.com | 🌐 ציבורי (iptv-org) | iptv-org (`KTVNews.kw`) | ✅ |
| 397 | NW Info | hls.newworldtv.com | 🌐 ציבורי (iptv-org) | iptv-org (`NWInfo.tg`) | ✅ |
| 398 | Perviy Informationniy | ngtrk.dc.beltelecom.by | 🌐 ציבורי (iptv-org) | iptv-org (`PerviyInformationniy.by`) | ✅ |
| 399 | Al Masirah Mubacher | live2.cdnbridge.tv | 🌐 ציבורי (iptv-org) | iptv-org (`AlMasirahMubacher.ye`) | ✅ |
| 400 | Ma'an TV | htvmada.mada.ps | 🌐 ציבורי (iptv-org) | iptv-org (`MaanTV.ps`) | ✅ |
| 401 | Alhaqeqa Aldawlia | ghaasiflu.online | 🌐 ציבורי (iptv-org) | iptv-org (`AlhaqeqaAldawlia.jo`) | ✅ |
| 402 | Ecotel TV | ecoteltv.streamseguro.com | 🌐 ציבורי (iptv-org) | iptv-org (`EcotelTV.ec`) | ✅ |
| 403 | News 21 Bangla TV | 103.190.133.68 | 🌐 ציבורי (iptv-org) | iptv-org (`News21BanglaTV.bd`) | ❌ רק באפליקציה (HTTP) |
| 404 | Palitra News | live.palitranews.ge | 🌐 ציבורי (iptv-org) | iptv-org (`PalitraNews.ge`) | ✅ |
| 405 | Realitatea TV | realitatealive.md | 🌐 ציבורי (iptv-org) | iptv-org (`RealitateaTV.md`) | ✅ |
| 406 | Cheddar News | hls.livecdn.io | 🌐 ציבורי (iptv-org) | iptv-org (`CheddarNews.us`) | ✅ |
| 407 | Afghanistan International | hls.afintl.com | 🌐 ציבורי (iptv-org) | iptv-org (`AfghanistanInternational.uk`) | ✅ |
| 408 | Africa 24 Sport | africa24.vedge.infomaniak.com | 🌐 ציבורי (iptv-org) | iptv-org (`Africa24Sport.fr`) | ✅ |
| 409 | WELT | dash4.antik.sk | 🌐 ציבורי (iptv-org) | iptv-org (`WELT.de`) | ✅ |
| 410 | 3CatInfo | directes-tv-int.3catdirectes.cat | 🌐 ציבורי (iptv-org) | iptv-org (`3CatInfo.es`) | ✅ |
| 411 | Etna Espresso Channel | 5db313b643fd8.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`EtnaEspressoChannel.it`) | ✅ |
| 412 | RT Arabic | rt-arb.rttv.com | 🌐 ציבורי (iptv-org) | iptv-org (`RTArabic.ru`) | ✅ |
| 413 | Rada TV | live-tv.cloud – ספק IPTV אוקראיני | ⚠️ ממסר צד שלישי | iptv-org (`RadaTV.ua`) | ✅ |
| 414 | CityNews Toronto | citynewsregional.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`CityNewsToronto.ca`) | ✅ |
| 415 | ABC News | abc-news-dmd-streams-1.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`ABCNews.au`) | ✅ |
| 416 | Alarabiya | live.alarabiya.net | 🌐 ציבורי (iptv-org) | iptv-org (`Alarabiya.ae`) | ✅ |
| 417 | Al Jazeera 2 | live-hls-web-aja2-gcp.thehlive.com | 🌐 ציבורי (iptv-org) | iptv-org (`AlJazeera2.qa`) | ✅ |
| 418 | Palestine Al Yawm | live.paltodaytv.com | 🌐 ציבורי (iptv-org) | iptv-org (`PalestineAlYawm.lb`) | ✅ |
| 419 | TRT Arabi | tv-trtarabi.medya.trt.com.tr | 🌐 ציבורי (iptv-org) | iptv-org (`TRTArabi.tr`) | ✅ |
| 420 | Republic TV | d3qs3d2rkhfqrt.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`RepublicTV.in`) | ✅ |
| 421 | KTV | hlive.ktv.go.kr | 🌐 ציבורי (iptv-org) | iptv-org (`KTV.kr`) | ✅ |
| 422 | Dongguan Comprehensive News Channel | stream.sun0769.com | 🌐 ציבורי (iptv-org) | iptv-org (`DongguanComprehensiveNewsChannel.cn`) | ✅ |
| 423 | TV Camara | stream3.camara.gov.br | 🌐 ציבורי (iptv-org) | iptv-org (`TVCamara.br`) | ✅ |
| 424 | Canal 33 Tijuana | 5f2c1b0d880e5.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`Canal33Tijuana.mx`) | ✅ |
| 425 | Catamarca TV | stream.arcast.com.ar | 🌐 ציבורי (iptv-org) | iptv-org (`CatamarcaTV.ar`) | ✅ |
| 426 | Sterk TV | hlspackager.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`SterkTV.be`) | ✅ |
| 427 | TVP World | dash2.antik.sk | 🌐 ציבורי (iptv-org) | iptv-org (`TVPWorld.pl`) | ✅ |
| 428 | Naftemporiki TV | telmaco.ascdn.broadpeak.io | 🌐 ציבורי (iptv-org) | iptv-org (`NaftemporikiTV.gr`) | ✅ |
| 429 | Expressen TV | cdn0-03837-liveedge0.dna.ip-only.net | 🌐 ציבורי (iptv-org) | iptv-org (`ExpressenTV.se`) | ✅ |
| 430 | Digi 24 | edge-ar.rcs-rds.ro | 🌐 ציבורי (iptv-org) | iptv-org (`Digi24.ro`) | ✅ |
| 431 | ADN TV | redirector.rudo.video | 🌐 ציבורי (iptv-org) | iptv-org (`ADNTV.cl`) | ✅ |
| 432 | Red+ | inforedvos.lcdn.claro.net.co | 🌐 ציבורי (iptv-org) | iptv-org (`RedPlus.co`) | ✅ |
| 433 | Exitosa TV | luna-4-video.mediaserver.digital | 🌐 ציבורי (iptv-org) | iptv-org (`ExitosaTV.pe`) | ✅ |
| 434 | Metro TV | edge.medcom.id | 🌐 ציבורי (iptv-org) | iptv-org (`MetroTV.id`) | ✅ |
| 435 | Top News | live.topnews.co.th | 🌐 ציבורי (iptv-org) | iptv-org (`TopNews.th`) | ✅ |
| 436 | Lahore News | vcdn.dunyanews.tv | 🌐 ציבורי (iptv-org) | iptv-org (`LahoreNews.pk`) | ✅ |
| 437 | SABC Lehae | sabctretalh.cdn.mangomolo.com | 🌐 ציבורי (iptv-org) | iptv-org (`SABCLehae.za`) | ✅ |
| 438 | Hermanas Mirabal TV | fox.hostlagarto.com | 🌐 ציבורי (iptv-org) | iptv-org (`HermanasMirabalTV.do`) | ✅ |
| 439 | Nanduti | nanduti.b-cdn.net | 🌐 ציבורי (iptv-org) | iptv-org (`Nanduti.py`) | ✅ |

## 📺 כלכלה ועסקים (Business)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 440 | כלכלה 10 | Redge – ה-CDN הרשמי של כאן, ערוץ 14 וכלכלה 10 | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 441 | Ameritrade | tdameritrade-vizio.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`Ameritrade.us`) | ✅ |
| 442 | BFM Business | live-cdn-stream-euw1.bfmb.bct.nextradiotv.com | 🌐 ציבורי (iptv-org) | iptv-org (`BFMBusiness.fr`) | ✅ |
| 443 | Negocios TV | streaming013.gestec-video.com | 🌐 ציבורי (iptv-org) | iptv-org (`NegociosTV.es`) | ✅ |
| 444 | Pro Business | tv-streaming.bsh.ru | 🌐 ציבורי (iptv-org) | iptv-org (`ProBusiness.ru`) | ✅ |
| 445 | Ausbiz TV | d9quh89lh7dtw.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`AusbizTV.au`) | ✅ |
| 446 | Al Arabiya Business | live.alarabiya.net | 🌐 ציבורי (iptv-org) | iptv-org (`AlArabiyaBusiness.ae`) | ✅ |
| 447 | Asharq News | live-news.asharq.com | 🌐 ציבורי (iptv-org) | iptv-org (`AsharqNews.sa`) | ✅ |
| 448 | Bloomberg HT | tv.ensonhaber.com | 🌐 ציבורי (iptv-org) | iptv-org (`BloombergHT.tr`) | ✅ |
| 449 | CNBC TV18 | n18syndication.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`CNBCTV18.in`) | ✅ |
| 450 | CGTN Global Biz | fastlive.cctvplus.com | 🌐 ציבורי (iptv-org) | iptv-org (`CGTNGlobalBiz.cn`) | ✅ |
| 451 | Canal Z | d1hz1p2gea2zy4.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`CanalZ.be`) | ✅ |
| 452 | Telewizja Biznesowa | s-pl-01.mediatool.tv | 🌐 ציבורי (iptv-org) | iptv-org (`TelewizjaBiznesowa.pl`) | ✅ |
| 453 | Aleph Business | streamw.m.ro | 🌐 ציבורי (iptv-org) | iptv-org (`AlephBusiness.ro`) | ✅ |
| 454 | CNBC Asia | freem3u.xyz | 🌐 ציבורי (iptv-org) | iptv-org (`CNBCAsia.sg`) | ✅ |
| 455 | DeFiance Media | amg01529-defiancetv-defiancemedia-glewed-tre8x.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`DeFianceMedia.pr`) | ✅ |
| 456 | Champion TV | stream.berosat.live | 🌐 ציבורי (iptv-org) | iptv-org (`ChampionTV.ci`) | ✅ |
| 457 | NW Economie | hls.newworldtv.com | 🌐 ציבורי (iptv-org) | iptv-org (`NWEconomie.tg`) | ✅ |
| 458 | Zuraca TV | tv3.bitstreaming.net | 🌐 ציבורי (iptv-org) | iptv-org (`ZuracaTV.bo`) | ✅ |
| 459 | MNTV | www.khuwemultimedia.com | 🌐 ציבורי (iptv-org) | iptv-org (`MNTV.mz`) | ✅ |
| 460 | apart TV | 2-fss-2.streamhoster.com | 🌐 ציבורי (iptv-org) | iptv-org (`apartTV.lu`) | ✅ |
| 461 | Bloomberg Originals | bloomberg.com | 🌐 ציבורי (iptv-org) | iptv-org (`BloombergOriginals.us`) | ✅ |
| 462 | CNBC Arabiya | cnbc-live.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`CNBCArabiya.ae`) | ✅ |
| 463 | Asharq Radio | Asharq Discovery – ItWorks (רשמי) | ✅ רשמי | iptv-org (`AsharqRadio.sa`) | ✅ |
| 464 | CNBC-e | hnpsechtsc.turknet.ercdn.net | 🌐 ציבורי (iptv-org) | iptv-org (`CNBCe.tr`) | ✅ |
| 465 | CNBC TV18 Prime HD | n18syndication.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`CNBCTV18PrimeHD.in`) | ✅ |
| 466 | CCTV-2 | 74.91.26.218 | 🌐 ציבורי (iptv-org) | iptv-org (`CCTV2.cn`) | ❌ רק באפליקציה (HTTP) |
| 467 | Kanaal Z | d1hz1p2gea2zy4.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`KanaalZ.be`) | ✅ |
| 468 | JOB TV | jobtvserver.eu | 🌐 ציבורי (iptv-org) | iptv-org (`JOBTV.ro`) | ✅ |
| 469 | Bloomberg TV | d8975dc3.wurl.com | 🌐 ציבורי (iptv-org) | iptv-org (`BloombergTV.us`) | ✅ |
| 470 | NDTV Profit | ndtvprofit.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`NDTVProfit.in`) | ✅ |
| 471 | Yahoo! Finance | d1ewctnvcwvvvu.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`YahooFinance.us`) | ✅ |
| 472 | CNBC Awaaz | n18syndication.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`CNBCAwaaz.in`) | ✅ |
| 473 | EntrepreneurTV | stream-us-east-1.getpublica.com | 🌐 ציבורי (iptv-org) | iptv-org (`EntrepreneurTV.us`) | ✅ |
| 474 | CNBC Bajar | n18syndication.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`CNBCBajar.in`) | ✅ |
| 475 | Fox Business Network | tvsen6.aynaott.com | 🌐 ציבורי (iptv-org) | iptv-org (`FoxBusinessNetwork.us`) | ✅ |
| 476 | News Capital Market | newscapital.com | 🌐 ציבורי (iptv-org) | iptv-org (`NewsCapitalMarket.in`) | ✅ |

## 📺 סרטים וסדרות (Movies & Series)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 477 | ערוץ הים התיכון HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`YamTichoni.il`) | ❌ רק באפליקציה (HTTP) |
| 478 | yes סרטים אקשן HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`YesMoviesAction.il`) | ❌ רק באפליקציה (HTTP) |
| 479 | HOT דרמה HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`HOTDrama.il`) | ❌ רק באפליקציה (HTTP) |
| 480 | HOT זון HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`HOTZone.il`) | ❌ רק באפליקציה (HTTP) |
| 481 | yes ריאליטי HD | ערוץ FAST ישראלי – AWS CloudFront | ✅ FAST חינמי | iptv-org (`RealityChannel.il`) | ✅ |
| 482 | ויוה פרימיום HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`VivaPremium.il`) | ❌ רק באפליקציה (HTTP) |
| 483 | סינמה פרימיום HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Premialnoe.ru`) | ❌ רק באפליקציה (HTTP) |
| 484 | סינמה מתח ואקשן HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Ostrosyuzhetnoye.ru`) | ❌ רק באפליקציה (HTTP) |
| 485 | ערוץ סרטי אימה HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Kinouzhas.ru`) | ❌ רק באפליקציה (HTTP) |
| 486 | קולנוע להיטים KinoHit HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`KinoHit.ru`) | ✅ דרך מקור חלופי: Cinerama.uz – ספק IPTV באוזבקיסטן |
| 487 | קולנוע משפחתי Kinosemja HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Kinosemja.ru`) | ❌ רק באפליקציה (HTTP) |
| 488 | סינמה מגה-להיטים HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`vijuPlusMegahit.ru`) | ❌ רק באפליקציה (HTTP) |
| 489 | סינמה בכורה Premiere HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`vijuPlusPremiere.ru`) | ❌ רק באפליקציה (HTTP) |
| 490 | סינמה קומדיה HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`vijuPlusComedy.ru`) | ❌ רק באפליקציה (HTTP) |
| 491 | קולנוע TV1000 אקשן HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`vijuTV1000action.ru`) | ✅ דרך מקור חלופי: Cinerama.uz – ספק IPTV באוזבקיסטן |
| 492 | קולנוע TV1000 קלאסי | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`vijuTV1000.ru`) | ❌ רק באפליקציה (HTTP) |
| 493 | ערוץ הסדרות Serial HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`vijuPlusSerial.ru`) | ❌ רק באפליקציה (HTTP) |
| 494 | סרטי שנות ה-80 Rewind HD | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`80sRewind.us`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 495 | סרטי שנות ה-70 Cinema HD | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`70sCinema.us`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 496 | סרטי שנות ה-2000 Replay HD | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`00sReplay.us`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 497 | משפחת סימפסון 24/7 HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 498 | קולנוע קומדיה CineMan Comedy HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 499 | קולנוע רטרו VHS Cinema HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 500 | ערוץ הדרמה של קשת | קשת – ה-CDN של mako (עם הרשאת צפייה של mako) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 501 | 48 Hours | dai.google.com | 🌐 ציבורי (iptv-org) | iptv-org (`48Hours.us`) | ✅ |
| 502 | True Crime | b6cca454.wurl.com | 🌐 ציבורי (iptv-org) | iptv-org (`TrueCrime.uk`) | ✅ |
| 503 | Cine Nanar | zylo-cinenanar-rakuten.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`CineNanar.fr`) | ✅ |
| 504 | Grjngo Western Movies | amogonetworx-grjngo-3-dk.samsung.wurl.tv | 🌐 ציבורי (iptv-org) | iptv-org (`GrjngoWesternMovies.de`) | ✅ |
| 505 | Cine Friki | d2mr4fu91mjx9m.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`CineFriki.es`) | ✅ |
| 506 | Super TV | 59d7d6f47d7fc.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`SuperTV.it`) | ✅ |
| 507 | Smotrim Chestnyy Detektiv | live-vgtrksmotrim.cdnvideo.ru | 🌐 ציבורי (iptv-org) | iptv-org (`SmotrimChestnyyDetektiv.ru`) | ✅ |
| 508 | FilmUADrama | dash2.antik.sk | 🌐 ציבורי (iptv-org) | iptv-org (`FilmUADrama.ua`) | ✅ |
| 509 | Mix Hollywood | ml-pull-hwc.myco.io | 🌐 ציבורי (iptv-org) | iptv-org (`MixHollywood.eg`) | ✅ |
| 510 | Grand Cinema | gcinemahls.wns.live | 🌐 ציבורי (iptv-org) | iptv-org (`GrandCinema.tr`) | ✅ |
| 511 | B4U Bhojpuri | cdnb4u.wiseplayout.com | 🌐 ציבורי (iptv-org) | iptv-org (`B4UBhojpuri.in`) | ✅ |
| 512 | Harbin Movie Channel | stream.hrbtv.net | 🌐 ציבורי (iptv-org) | iptv-org (`HarbinMovieChannel.cn`) | ✅ |
| 513 | CINDIE TV | cc-hqw8u5r1nshjc.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`CINDIETV.br`) | ✅ |
| 514 | Popcorn Central | oly-ftvpc.otteravision.com | 🌐 ציבורי (iptv-org) | iptv-org (`PopcornCentral.mx`) | ✅ |
| 515 | Europa Europa | iptv.bitred.cl | 🌐 ציבורי (iptv-org) | iptv-org (`EuropaEuropa.ar`) | ✅ |
| 516 | Kuriakos Cine | w2.manasat.com | 🌐 ציבורי (iptv-org) | iptv-org (`KuriakosCine.pt`) | ✅ |
| 517 | FILMBOX+ Festival | nosignal1.antenaplay.ro | 🌐 ציבורי (iptv-org) | iptv-org (`FILMBOXPlusFestival.pl`) | ✅ |
| 518 | Action 24 | actionlive.siliconweb.com | 🌐 ציבורי (iptv-org) | iptv-org (`Action24.gr`) | ❌ רק באפליקציה (HTTP) |
| 519 | Bondi Rescue | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`BondiRescue.se`) | ✅ |
| 520 | Hero | live-fi.tvkaista.net | 🌐 ציבורי (iptv-org) | iptv-org (`Hero.fi`) | ✅ |
| 521 | Cinemaraton Plus | hls02ns.antenaplay.ro | 🌐 ציבורי (iptv-org) | iptv-org (`CinemaratonPlus.ro`) | ✅ |
| 522 | MAX4 | 141.94.193.53 | 🌐 ציבורי (iptv-org) | iptv-org (`MAX4.hu`) | ❌ רק באפליקציה (HTTP) |
| 523 | TVN3 | mdstrm.com | 🌐 ציבורי (iptv-org) | iptv-org (`TVN3.cl`) | ✅ |
| 524 | MCI Television | video.ejeserver.com | 🌐 ציבורי (iptv-org) | iptv-org (`MCITelevision.co`) | ✅ |
| 525 | Latina Clasicos | redirector.rudo.video | 🌐 ציבורי (iptv-org) | iptv-org (`LatinaClasicos.pe`) | ✅ |
| 526 | ON Movies | freem3u.xyz | 🌐 ציבורי (iptv-org) | iptv-org (`ONMovies.vn`) | ✅ |
| 527 | AVA Series | avaserieshls.wns.live | 🌐 ציבורי (iptv-org) | iptv-org (`AVASeries.ir`) | ✅ |
| 528 | Itage TV | viewmedia7219.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`ItageTV.ng`) | ✅ |
| 529 | Aurora Media Films | cdn.streamhispanatv.net | 🌐 ציבורי (iptv-org) | iptv-org (`AuroraMediaFilms.gt`) | ✅ |
| 530 | Mega Cine TV | cnn.hostlagarto.com | 🌐 ציבורי (iptv-org) | iptv-org (`MegaCineTV.do`) | ✅ |
| 531 | Allo Cine | d1yl6bf4hrzbr0.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`AlloCine.cm`) | ✅ |
| 532 | HITS Movies | 103.151.60.162 | 🌐 ציבורי (iptv-org) | iptv-org (`HITSMovies.sg`) | ❌ רק באפליקציה (HTTP) |
| 533 | Silk Way Cinema | stream.qazcdn.net | 🌐 ציבורי (iptv-org) | iptv-org (`SilkWayCinema.kz`) | ✅ |
| 534 | Lollywood HD TV | stream.berosat.live | 🌐 ציבורי (iptv-org) | iptv-org (`LollywoodHDTV.cd`) | ✅ |
| 535 | Serie Max | video1.getstreamhosting.com | 🌐 ציבורי (iptv-org) | iptv-org (`SerieMax.ci`) | ✅ |
| 536 | BIZ Cinema | fl.biztv.media | 🌐 ציבורי (iptv-org) | iptv-org (`BIZCinema.uz`) | ✅ |
| 537 | Maah TV | hls.maahtv.live | 🌐 ציבורי (iptv-org) | iptv-org (`MaahTV.my`) | ✅ |
| 538 | Mei Ah Movie Channel | 103.172.187.30 | 🌐 ציבורי (iptv-org) | iptv-org (`MeiAhMovieChannel.hk`) | ❌ רק באפליקציה (HTTP) |
| 539 | Movie Bangla | alvetv.com | 🌐 ציבורי (iptv-org) | iptv-org (`MovieBangla.bd`) | ❌ רק באפליקציה (HTTP) |
| 540 | MWD Movies | 203.81.84.130 | 🌐 ציבורי (iptv-org) | iptv-org (`MWDMovies.mm`) | ❌ רק באפליקציה (HTTP) |
| 541 | TV1000 | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`TV1000.bg`) | ❌ רק באפליקציה (HTTP) |
| 542 | Antiques Roadshow UK | bbc-antiquesroadshowuk-1-us.roku.wurl.tv | 🌐 ציבורי (iptv-org) | iptv-org (`AntiquesRoadshowUK.us`) | ✅ |
| 543 | BBC Drama | amg00793-amg00793c40-rakuten-es-5444.playouts.now.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`BBCDrama.uk`) | ✅ |
| 544 | Sony One Blacklist | 06cb85ad6ccb4a6b97f561e62d16ad3f.mediatailor.us-west-2.amazonaws.com | 🌐 ציבורי (iptv-org) | iptv-org (`SonyOneBlacklist.fr`) | ✅ |
| 545 | Pluto TV Dating | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVDating.de`) | ✅ |
| 546 | MyTime Movie Network | appletree-mytimespain-rakuten.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`MyTimeMovieNetwork.es`) | ✅ |
| 547 | Rai Movie | team-tv.co | 🌐 ציבורי (iptv-org) | iptv-org (`RaiMovie.it`) | ❌ רק באפליקציה (HTTP) |
| 548 | Star Family | dash2.antik.sk | 🌐 ציבורי (iptv-org) | iptv-org (`StarFamily.ru`) | ✅ |
| 549 | FilmUA Live | dash2.antik.sk | 🌐 ציבורי (iptv-org) | iptv-org (`FilmUALive.ua`) | ✅ |
| 550 | Epic Bhojpuri | cc-8hy4a26pz2uos.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`EpicBhojpuri.in`) | ✅ |
| 551 | CCTV-Nostalgia Theater | 38.75.136.137 | 🌐 ציבורי (iptv-org) | iptv-org (`CCTVNostalgiaTheater.cn`) | ❌ רק באפליקציה (HTTP) |
| 552 | MyTime Movie Network | appletree-mytime-samsungbrazil.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`MyTimeMovieNetwork.br`) | ✅ |
| 553 | Az Cinema | 15.204.196.147 | 🌐 ציבורי (iptv-org) | iptv-org (`AzCinema.mx`) | ❌ רק באפליקציה (HTTP) |
| 554 | TVI Ficcao | raw.githubusercontent.com | 🌐 ציבורי (iptv-org) | iptv-org (`TVIFiccao.pt`) | ✅ |
| 555 | FILMBOX+ Hits | nosignal1.antenaplay.ro | 🌐 ציבורי (iptv-org) | iptv-org (`FILMBOXPlusHits.pl`) | ✅ |
| 556 | Groovy TV | web.onair-radio.eu | 🌐 ציבורי (iptv-org) | iptv-org (`GroovyTV.gr`) | ❌ רק באפליקציה (HTTP) |
| 557 | Car Chase | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`CarChase.se`) | ✅ |
| 558 | Nostalgia TV | hls02ns.antenaplay.ro | 🌐 ציבורי (iptv-org) | iptv-org (`NostalgiaTV.ro`) | ✅ |
| 559 | 13T | origin.dpsgo.com | 🌐 ציבורי (iptv-org) | iptv-org (`13T.cl`) | ✅ |
| 560 | Canal Cine Dios | canal.mediaserver.com.co | 🌐 ציבורי (iptv-org) | iptv-org (`CanalCineDios.co`) | ✅ |
| 561 | ON Cine | freem3u.xyz | 🌐 ציבורי (iptv-org) | iptv-org (`ONCine.vn`) | ✅ |
| 562 | SL 1 | slonehls.wns.live | 🌐 ציבורי (iptv-org) | iptv-org (`SL1.ir`) | ✅ |
| 563 | TVS Retro | cdn.streamhispanatv.net | 🌐 ציבורי (iptv-org) | iptv-org (`TVSRetro.gt`) | ✅ |
| 564 | Boreal | edge.essastream.com | 🌐 ציבורי (iptv-org) | iptv-org (`Boreal.do`) | ✅ |
| 565 | Charge! | fast-channels.sinclairstoryline.com | 🌐 ציבורי (iptv-org) | iptv-org (`Charge.us`) | ✅ |
| 566 | MyTime Movie Network | appletree-mytimeuk-rakuten.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`MyTimeMovieNetwork.uk`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 567 | Sony One Favoris | 49d735318d6b4c30a24a7997ea594e1b.mediatailor.us-west-2.amazonaws.com | 🌐 ציבורי (iptv-org) | iptv-org (`SonyOneFavoris.fr`) | ✅ |
| 568 | Cagney & Lacey | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`CagneyLacey.de`) | ✅ |
| 569 | Rakuten TV Action Movies | a9c57ec7ec5e4b7daeacc6316a0bb404.mediatailor.eu-west-1.amazonaws.com | 🌐 ציבורי (iptv-org) | iptv-org (`RakutenTVActionMovies.es`) | ✅ |
| 570 | Horoshee Kino | live-rian.cdnvideo.ru | 🌐 ציבורי (iptv-org) | iptv-org (`HorosheeKino.ru`) | ✅ |
| 571 | 4ever Cinema | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`4everCinema.ua`) | ❌ רק באפליקציה (HTTP) |
| 572 | NH Tamil Gold | d3arbp6l7f096k.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`NHTamilGold.in`) | ✅ |
| 573 | CCTV-Storm Theater | 38.75.136.137 | 🌐 ציבורי (iptv-org) | iptv-org (`CCTVStormTheater.cn`) | ❌ רק באפליקציה (HTTP) |
| 574 | Retro Cartoon | stmv1.video.brstream.com.br | 🌐 ציבורי (iptv-org) | iptv-org (`RetroCartoon.br`) | ✅ |
| 575 | Claro Cinema | 158.172.217.18 | 🌐 ציבורי (iptv-org) | iptv-org (`ClaroCinema.mx`) | ❌ רק באפליקציה (HTTP) |
| 576 | FILMBOX+ Comedy | nosignal1.antenaplay.ro | 🌐 ציבורי (iptv-org) | iptv-org (`FILMBOXPlusComedy.pl`) | ✅ |
| 577 | 2900 Happiness | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`2900Happiness.se`) | ✅ |
| 578 | Mireasa | stream1.antenaplay.ro | 🌐 ציבורי (iptv-org) | iptv-org (`Mireasa.ro`) | ✅ |
| 579 | Mega Ficcion | 15.204.196.147 | 🌐 ציבורי (iptv-org) | iptv-org (`MegaFiccion.cl`) | ❌ רק באפליקציה (HTTP) |
| 580 | Star Channel | 190.61.47.54 | 🌐 ציבורי (iptv-org) | iptv-org (`StarChannel.co`) | ❌ רק באפליקציה (HTTP) |
| 581 | SL 2 | sltwohls.wns.live | 🌐 ציבורי (iptv-org) | iptv-org (`SL2.ir`) | ✅ |
| 582 | Doctor Who Classic | bbc-classicdrwho-1-us.roku.wurl.tv | 🌐 ציבורי (iptv-org) | iptv-org (`DoctorWhoClassic.us`) | ✅ |
| 583 | Viasat Epic Drama | 92.114.85.87 | 🌐 ציבורי (iptv-org) | iptv-org (`ViasatEpicDrama.uk`) | ❌ רק באפליקציה (HTTP) |
| 584 | Sony One Hits Action | 5098a8b860504a3690fd2e7c0a18d68f.mediatailor.us-west-2.amazonaws.com | 🌐 ציבורי (iptv-org) | iptv-org (`SonyOneHitsAction.fr`) | ✅ |
| 585 | CSI: Crime Scene Investigation | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`CSICrimeSceneInvestigation.de`) | ✅ |
| 586 | Rakuten TV Comedy Movies | 71db867f03ce4d71a29e92155f07ab87.mediatailor.eu-west-1.amazonaws.com | 🌐 ציבורי (iptv-org) | iptv-org (`RakutenTVComedyMovies.es`) | ✅ |
| 587 | Smotrim 100% Klassika | Smotrim (VGTRK) – הבעלים של קרוסלה | ✅ רשמי | iptv-org (`Smotrim100Klassika.ru`) | ✅ |
| 588 | 4ever Drama | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`4everDrama.ua`) | ❌ רק באפליקציה (HTTP) |
| 589 | Punjabi Shorts | vglivessai.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`PunjabiShorts.in`) | ✅ |
| 590 | CCTV-The First Theater | 38.75.136.137 | 🌐 ציבורי (iptv-org) | iptv-org (`CCTVTheFirstTheater.cn`) | ❌ רק באפליקציה (HTTP) |
| 591 | TV Series | stmv1.srvif.com | 🌐 ציבורי (iptv-org) | iptv-org (`TVSeries.br`) | ✅ |
| 592 | FILMBOX+ Love & Crime | nosignal1.antenaplay.ro | 🌐 ציבורי (iptv-org) | iptv-org (`FILMBOXPlusLoveCrime.pl`) | ✅ |
| 593 | 7th Heaven | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`7thHeaven.se`) | ✅ |
| 594 | Bollywood Classic | hls02ns.antenaplay.ro | 🌐 ציבורי (iptv-org) | iptv-org (`BollywoodClassic.ro`) | ✅ |
| 595 | TeleNostalgia | 38.134.250.110 | 🌐 ציבורי (iptv-org) | iptv-org (`TeleNostalgia.co`) | ❌ רק באפליקציה (HTTP) |
| 596 | Afra Film | afrafhls.wns.live | 🌐 ציבורי (iptv-org) | iptv-org (`AfraFilm.ir`) | ✅ |
| 597 | Duck Dynasty | amg00276-amg00276c2-rakuten-uk-3449.playouts.now.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`DuckDynasty.us`) | ✅ |
| 598 | Sony One Hits Comedie | 7aa9671895264ec9a384dfed1b992171.mediatailor.us-west-2.amazonaws.com | 🌐 ציבורי (iptv-org) | iptv-org (`SonyOneHitsComedie.fr`) | ✅ |
| 599 | More TV Crime | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`MoreTVCrime.de`) | ✅ |
| 600 | Rakuten TV Drama Movies | a7089c89d85f453d850c4a1518b43076.mediatailor.eu-west-1.amazonaws.com | 🌐 ציבורי (iptv-org) | iptv-org (`RakutenTVDramaMovies.es`) | ✅ |
| 601 | Smotrim 100% Lyubov | Smotrim (VGTRK) – הבעלים של קרוסלה | ✅ רשמי | iptv-org (`Smotrim100Lyubov.ru`) | ✅ |
| 602 | Raj Digital Plus | livestream.rajtv.tv | 🌐 ציבורי (iptv-org) | iptv-org (`RajDigitalPlus.in`) | ✅ |
| 603 | Classique TV | stmv1.srvif.com | 🌐 ציבורי (iptv-org) | iptv-org (`ClassiqueTV.br`) | ✅ |
| 604 | De Pelicula Plus | 200.115.120.1 | 🌐 ציבורי (iptv-org) | iptv-org (`DePeliculaPlus.mx`) | ❌ רק באפליקציה (HTTP) |
| 605 | AXN Poland | 92.114.85.87 | 🌐 ציבורי (iptv-org) | iptv-org (`AXNPoland.pl`) | ❌ רק באפליקציה (HTTP) |
| 606 | Auction Hunters | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`AuctionHunters.se`) | ✅ |
| 607 | CineWow | hls02ns.antenaplay.ro | 🌐 ציבורי (iptv-org) | iptv-org (`CineWow.ro`) | ✅ |
| 608 | Cafe Film | cafefhls.wns.live | 🌐 ציבורי (iptv-org) | iptv-org (`CafeFilm.ir`) | ✅ |
| 609 | Gravitas Movies | d6dg3ebeih71x.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`GravitasMovies.us`) | ✅ |
| 610 | Persiana Cinema | cinehls.persiana.live | 🌐 ציבורי (iptv-org) | iptv-org (`PersianaCinema.fr`) | ✅ |
| 611 | Pluto TV American True Crime | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVAmericanTrueCrime.de`) | ✅ |
| 612 | Rakuten TV Family Movies | b0d59c8c98974e708e5ccb9a27cdfdfc.mediatailor.eu-west-1.amazonaws.com | 🌐 ציבורי (iptv-org) | iptv-org (`RakutenTVFamilyMovies.es`) | ✅ |
| 613 | Smotrim 100% Muzhskoe | Smotrim (VGTRK) – הבעלים של קרוסלה | ✅ רשמי | iptv-org (`Smotrim100Muzhskoe.ru`) | ✅ |
| 614 | South Station | cc-yw7ztecy8do3q.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`SouthStation.in`) | ✅ |
| 615 | AXN | 170.83.16.50 | 🌐 ציבורי (iptv-org) | iptv-org (`AXN.br`) | ❌ רק באפליקציה (HTTP) |
| 616 | MultiPremier | 168.196.127.137 | 🌐 ציבורי (iptv-org) | iptv-org (`MultiPremier.mx`) | ❌ רק באפליקציה (HTTP) |
| 617 | TVP Seriale | 92.114.85.87 | 🌐 ציבורי (iptv-org) | iptv-org (`TVPSeriale.pl`) | ❌ רק באפליקציה (HTTP) |
| 618 | Becker | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`Becker.se`) | ✅ |
| 619 | Classic TV | clshls.wns.live | 🌐 ציבורי (iptv-org) | iptv-org (`ClassicTV.ir`) | ✅ |
| 620 | Impossible Quiz Show | bbc-impossible-1-us.roku.wurl.tv | 🌐 ציבורי (iptv-org) | iptv-org (`ImpossibleQuizShow.us`) | ✅ |
| 621 | Persiana Family | familyhls.persiana.live | 🌐 ציבורי (iptv-org) | iptv-org (`PersianaFamily.fr`) | ✅ |
| 622 | Pluto TV Classic TV | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVClassicTV.de`) | ✅ |
| 623 | Rakuten TV Top Movies | ff335120300e4742a2b135ee9a9e7df8.mediatailor.eu-west-1.amazonaws.com | 🌐 ציבורי (iptv-org) | iptv-org (`RakutenTVTopMovies.es`) | ✅ |
| 624 | Dorama | fs.uplink.kz | 🌐 ציבורי (iptv-org) | iptv-org (`Dorama.ru`) | ✅ |
| 625 | Thalaa TV | streams2.sofast.tv | 🌐 ציבורי (iptv-org) | iptv-org (`ThalaaTV.in`) | ✅ |
| 626 | Sony Movies | 45.162.64.114 | 🌐 ציבורי (iptv-org) | iptv-org (`SonyMovies.br`) | ❌ רק באפליקציה (HTTP) |
| 627 | Tlnovelas Mexico | 167.249.23.102 | 🌐 ציבורי (iptv-org) | iptv-org (`TlnovelasMexico.mx`) | ❌ רק באפליקציה (HTTP) |
| 628 | FILMBOX+ Action | 92.114.85.87 | 🌐 ציבורי (iptv-org) | iptv-org (`FILMBOXPlusAction.pl`) | ❌ רק באפליקציה (HTTP) |
| 629 | BET | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`BET.se`) | ✅ |
| 630 | Gold Star | moviethls.wns.live | 🌐 ציבורי (iptv-org) | iptv-org (`GoldStar.ir`) | ✅ |
| 631 | Maverick Black Cinema | maverick-maverick-black-cinema-3-us.roku.wurl.tv | 🌐 ציבורי (iptv-org) | iptv-org (`MaverickBlackCinema.us`) | ✅ |
| 632 | Persiana Iranian | irhls.persiana.live | 🌐 ציבורי (iptv-org) | iptv-org (`PersianaIranian.fr`) | ✅ |
| 633 | Pluto TV Cult Films | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVCultFilms.de`) | ✅ |
| 634 | 3Cat Plats bruts | fast-tailor.3catdirectes.cat | 🌐 ציבורי (iptv-org) | iptv-org (`3CatPlatsbruts.es`) | ✅ |
| 635 | Kinoseriya | fs.uplink.kz | 🌐 ציבורי (iptv-org) | iptv-org (`Kinoseriya.ru`) | ✅ |
| 636 | The Movie Club | sis-global.prod.samsungtv.plus | 🌐 ציבורי (iptv-org) | iptv-org (`TheMovieClub.in`) | ✅ |
| 637 | Az Corazon | 200.115.120.1 | 🌐 ציבורי (iptv-org) | iptv-org (`AzCorazon.mx`) | ❌ רק באפליקציה (HTTP) |
| 638 | FILMBOX+ Emotion | 92.114.85.87 | 🌐 ציבורי (iptv-org) | iptv-org (`FILMBOXPlusEmotion.pl`) | ❌ רק באפליקציה (HTTP) |
| 639 | Britain's Next Top Model | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`BritainsNextTopModel.se`) | ✅ |
| 640 | Meta Film TV | metafilmhls.wns.live | 🌐 ציבורי (iptv-org) | iptv-org (`MetaFilmTV.ir`) | ✅ |
| 641 | Midsomer Murders | all3media-midsomer-1-us.roku.wurl.tv | 🌐 ציבורי (iptv-org) | iptv-org (`MidsomerMurders.us`) | ✅ |
| 642 | Persiana Korea | korhls.persiana.live | 🌐 ציבורי (iptv-org) | iptv-org (`PersianaKorea.fr`) | ✅ |
| 643 | Pluto TV Family Movie Club | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVFamilyMovieClub.de`) | ✅ |
| 644 | XPTV US | janus.xpbroadcasting.com | 🌐 ציבורי (iptv-org) | iptv-org (`XPTVUS.es`) | ✅ |
| 645 | Russkiy roman | vod.tuva.ru | 🌐 ציבורי (iptv-org) | iptv-org (`Russkiyroman.ru`) | ✅ |
| 646 | Tolly TV | streams2.sofast.tv | 🌐 ציבורי (iptv-org) | iptv-org (`TollyTV.in`) | ✅ |
| 647 | MyTime Movie Network | appletree-mytime-samsungmexico.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`MyTimeMovieNetwork.mx`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 648 | AXN Black | 92.114.85.87 | 🌐 ציבורי (iptv-org) | iptv-org (`AXNBlack.pl`) | ❌ רק באפליקציה (HTTP) |
| 649 | Bull | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`Bull.se`) | ✅ |
| 650 | NEW K.MOVIES | ads.its-newid.net | 🌐 ציבורי (iptv-org) | iptv-org (`NEWKMOVIES.us`) | ✅ |
| 651 | Persiana Plus | euhls.persiana.live | 🌐 ציבורי (iptv-org) | iptv-org (`PersianaPlus.fr`) | ✅ |
| 652 | Pluto TV Fantasy & Horror | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVFantasyHorror.de`) | ✅ |
| 653 | City Eden KinoAction | cityeden.catcast.tv | 🌐 ציבורי (iptv-org) | iptv-org (`CityEdenKinoAction.ru`) | ✅ |
| 654 | Alankar TV | livetv.tarangplus.in | 🌐 ציבורי (iptv-org) | iptv-org (`AlankarTV.in`) | ✅ |
| 655 | AXN White | 92.114.85.87 | 🌐 ציבורי (iptv-org) | iptv-org (`AXNWhite.pl`) | ❌ רק באפליקציה (HTTP) |
| 656 | CSI | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`CSI.se`) | ✅ |
| 657 | OuterSphere | d3o593mz1glx8d.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`OuterSphere.us`) | ✅ |
| 658 | Persiana Series | onehls.persiana.live | 🌐 ציבורי (iptv-org) | iptv-org (`PersianaSeries.fr`) | ✅ |
| 659 | Pluto TV Investigation | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVInvestigation.de`) | ✅ |
| 660 | City Eden KinoArt | cityeden.catcast.tv | 🌐 ציבורי (iptv-org) | iptv-org (`CityEdenKinoArt.ru`) | ✅ |
| 661 | Bhojpuri Cinema | live-bhojpuri.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`BhojpuriCinema.in`) | ✅ |
| 662 | Sundance TV | 92.114.85.87 | 🌐 ציבורי (iptv-org) | iptv-org (`SundanceTV.pl`) | ❌ רק באפליקציה (HTTP) |
| 663 | Dallas Cowboys Cheerleaders | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`DallasCowboysCheerleaders.se`) | ✅ |
| 664 | The Loop | tgn.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`TheLoop.us`) | ✅ |
| 665 | Pluto TV Sci-fi Series | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVScifiSeries.de`) | ✅ |
| 666 | City Eden KinoAsia | cityeden.catcast.tv | 🌐 ציבורי (iptv-org) | iptv-org (`CityEdenKinoAsia.ru`) | ✅ |
| 667 | Manoranjan Grand | cdn.pishow.tv | 🌐 ציבורי (iptv-org) | iptv-org (`ManoranjanGrand.in`) | ✅ |
| 668 | AXN Spin | 92.114.85.87 | 🌐 ציבורי (iptv-org) | iptv-org (`AXNSpin.pl`) | ❌ רק באפליקציה (HTTP) |
| 669 | Dating Naked UK | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`DatingNakedUK.se`) | ✅ |
| 670 | This Old House | thisoldhouse-2-us.roku.wurl.tv | 🌐 ציבורי (iptv-org) | iptv-org (`ThisOldHouse.us`) | ✅ |
| 671 | Pluto TV Sherlock | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVSherlock.de`) | ✅ |
| 672 | City Eden KinoDetektiv | cityeden.catcast.tv | 🌐 ציבורי (iptv-org) | iptv-org (`CityEdenKinoDetektiv.ru`) | ✅ |
| 673 | Movie Punjab | samitaorigin.tangotv.in | 🌐 ציבורי (iptv-org) | iptv-org (`MoviePunjab.in`) | ✅ |
| 674 | Polsat Film | 92.63.196.180 | 🌐 ציבורי (iptv-org) | iptv-org (`PolsatFilm.pl`) | ❌ רק באפליקציה (HTTP) |
| 675 | Diagnosis Murder | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`DiagnosisMurder.se`) | ✅ |
| 676 | Universal Crime | xumo-xumoent-vc-107-xmuvk.fast.nbcuni.com | 🌐 ציבורי (iptv-org) | iptv-org (`UniversalCrime.us`) | ✅ |
| 677 | Pluto TV Tough Jobs | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVToughJobs.de`) | ✅ |
| 678 | City Eden KinoDok | cityeden.catcast.tv | 🌐 ציבורי (iptv-org) | iptv-org (`CityEdenKinoDok.ru`) | ✅ |
| 679 | Oscar Movies Bhojpuri | cdn.pishow.tv | 🌐 ציבורי (iptv-org) | iptv-org (`OscarMoviesBhojpuri.in`) | ✅ |
| 680 | FBoy Island | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`FBoyIsland.se`) | ✅ |
| 681 | AXN Latin America | streaming.onnethn.com | 🌐 ציבורי (iptv-org) | iptv-org (`AXNLatinAmerica.us`) | ✅ |
| 682 | Pluto TV Westerns | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVWesterns.de`) | ✅ |
| 683 | City Eden KinoDrama | cityeden.catcast.tv | 🌐 ציבורי (iptv-org) | iptv-org (`CityEdenKinoDrama.ru`) | ✅ |
| 684 | Pitaara | vg-pitaaratvlive.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`Pitaara.in`) | ✅ |
| 685 | Happy Days | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`HappyDays.se`) | ✅ |
| 686 | Cinecanal | barinaslive1.boldmss.com | 🌐 ציבורי (iptv-org) | iptv-org (`Cinecanal.us`) | ✅ |
| 687 | The New Detectives | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`TheNewDetectives.de`) | ✅ |
| 688 | City Eden KinoFantastika | cityeden.catcast.tv | 🌐 ציבורי (iptv-org) | iptv-org (`CityEdenKinoFantastika.ru`) | ✅ |
| 689 | Shubh Cinema TV | d393sxaxig6bax.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`ShubhCinemaTV.in`) | ✅ |
| 690 | House of Lies | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`HouseofLies.se`) | ✅ |
| 691 | 50 Cent Action | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`50CentAction.us`) | ✅ |
| 692 | Cine 666 by Pluto TV | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`Cine666byPlutoTV.de`) | ✅ |
| 693 | City Eden KinoKomediya | cityeden.catcast.tv | 🌐 ציבורי (iptv-org) | iptv-org (`CityEdenKinoKomediya.ru`) | ✅ |
| 694 | Star Maa Movies | da86m1sqpm3o0.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`StarMaaMovies.in`) | ✅ |
| 695 | JAG | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`JAG.se`) | ✅ |
| 696 | ABN Bible Movies Channel | mediaserver.abnvideos.com | 🌐 ציבורי (iptv-org) | iptv-org (`ABNBibleMoviesChannel.us`) | ✅ |
| 697 | Cine Action by Pluto TV | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`CineActionbyPlutoTV.de`) | ✅ |
| 698 | City Eden KinoMistika | cityeden.catcast.tv | 🌐 ציבורי (iptv-org) | iptv-org (`CityEdenKinoMistika.ru`) | ✅ |
| 699 | Studio One + | cdn.pishow.tv | 🌐 ציבורי (iptv-org) | iptv-org (`StudioOnePlus.in`) | ✅ |
| 700 | Judge Judy | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`JudgeJudy.se`) | ✅ |

## 📺 קומדיה (Comedy)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 701 | ערוץ הקומדיה HD | ערוץ FAST ישראלי – AWS CloudFront | ✅ FAST חינמי | iptv-org (`ComedyChannel.il`) | ✅ |
| 702 | ערוץ הקומדיה של קשת | קשת – ה-CDN של mako (עם הרשאת צפייה של mako) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 703 | ארץ נהדרת 24/7 | קשת – ה-CDN של mako (עם הרשאת צפייה של mako) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 704 | סברי מרנן 24/7 | קשת – ה-CDN של mako (עם הרשאת צפייה של mako) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 705 | NBC Comedy Vault | xumo-xumoent-vc-105-z0vpm.fast.nbcuni.com | 🌐 ציבורי (iptv-org) | iptv-org (`NBCComedyVault.us`) | ✅ |
| 706 | Persiana Comedy | comedyhls.persiana.live | 🌐 ציבורי (iptv-org) | iptv-org (`PersianaComedy.fr`) | ✅ |
| 707 | Ridiculousness | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`Ridiculousness.de`) | ✅ |
| 708 | Comedy Central | team-tv.co | 🌐 ציבורי (iptv-org) | iptv-org (`ComedyCentral.it`) | ❌ רק באפליקציה (HTTP) |
| 709 | KVN TV | fs.uplink.kz | 🌐 ציבורי (iptv-org) | iptv-org (`KVNTV.ru`) | ✅ |
| 710 | ETV Comedy | cc-wie8j8y69d2uy.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`ETVComedy.in`) | ✅ |
| 711 | Distrito Comedia | 138.121.15.230 | 🌐 ציבורי (iptv-org) | iptv-org (`DistritoComedia.mx`) | ❌ רק באפליקציה (HTTP) |
| 712 | Awkward | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`Awkward.se`) | ✅ |
| 713 | Comedy Play | stream1.antenaplay.ro | 🌐 ציבורי (iptv-org) | iptv-org (`ComedyPlay.ro`) | ✅ |
| 714 | Jordan Comedy | playlist.fasttvcdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`JordanComedy.jo`) | ✅ |
| 715 | HAHA TV | Antik Telecom (סלובקיה), נתיבי test | ⚠️ ממסר צד שלישי | iptv-org (`HAHATV.sk`) | ✅ |
| 716 | SNL Vault | d4whmvwm0rdvi.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`SNLVault.us`) | ✅ |
| 717 | Comedia a lo bestia de Pluto TV | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`ComediaalobestiadePlutoTV.de`) | ✅ |
| 718 | Komediynoe | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Komediynoe.ru`) | ❌ רק באפליקציה (HTTP) |
| 719 | Sirippoli TV | segment.yuppcdn.net | 🌐 ציבורי (iptv-org) | iptv-org (`SirippoliTV.in`) | ✅ |
| 720 | Beavis and Butt-Head | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`BeavisandButtHead.se`) | ✅ |
| 721 | Comedy Central Latin America | barinaslive1.boldmss.com | 🌐 ציבורי (iptv-org) | iptv-org (`ComedyCentralLatinAmerica.us`) | ✅ |
| 722 | Pluto TV Comedia made in Spain | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVComediamadeinSpain.de`) | ✅ |
| 723 | Tvoye TV Yumor | tvoetv.space | 🌐 ציבורי (iptv-org) | iptv-org (`TvoyeTVYumor.ru`) | ❌ רק באפליקציה (HTTP) |
| 724 | Broad City | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`BroadCity.se`) | ✅ |
| 725 | 30A Ridiculous TV | 30a-tv.com | 🌐 ציבורי (iptv-org) | iptv-org (`30ARidiculousTV.us`) | ✅ |
| 726 | Alle hassen Chris | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`AllehassenChris.de`) | ✅ |
| 727 | Komedia | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Komedia.ru`) | ❌ רק באפליקציה (HTTP) |
| 728 | Caroline in the City | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`CarolineintheCity.se`) | ✅ |
| 729 | AFV | linear-12.frequency.stream | 🌐 ציבורי (iptv-org) | iptv-org (`AFV.us`) | ✅ |
| 730 | Becker | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`Becker.de`) | ✅ |
| 731 | Chappelle's Show | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`ChappellesShow.se`) | ✅ |
| 732 | Dry Bar Comedy+ | drybar-drybarcomedy-1-au.samsung.wurl.tv | 🌐 ציבורי (iptv-org) | iptv-org (`DryBarComedyPlus.us`) | ✅ |
| 733 | CC Pluto TV | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`CCPlutoTV.de`) | ✅ |
| 734 | Cheers | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`Cheers.se`) | ✅ |
| 735 | FailArmy | failarmy-international-au.samsung.wurl.tv | 🌐 ציבורי (iptv-org) | iptv-org (`FailArmy.us`) | ✅ |
| 736 | Pluto TV Deutsche Comedy | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVDeutscheComedy.de`) | ✅ |
| 737 | Comedy Central Roast | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`ComedyCentralRoast.se`) | ✅ |
| 738 | RiffTrax | wurlrifftrax.global.transmit.live | 🌐 ציבורי (iptv-org) | iptv-org (`RiffTrax.us`) | ✅ |
| 739 | Sabrina: Total verhext! | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`SabrinaTotalverhext.de`) | ✅ |
| 740 | Crazy Ex-Girlfriend | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`CrazyExGirlfriend.se`) | ✅ |
| 741 | TVS Comedy Network | rpn.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`TVSComedyNetwork.us`) | ✅ |
| 742 | Pluto TV Scherzi e risate | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVScherzierisate.de`) | ✅ |
| 743 | Everybody Hates Chris | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`EverybodyHatesChris.se`) | ✅ |
| 744 | Camera Smile TV | rpn.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`CameraSmileTV.us`) | ✅ |
| 745 | Family Ties | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`FamilyTies.se`) | ✅ |

## 📺 ילדים מהעולם (Kids World)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 746 | ניקלודיאון (ברוסית) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Nickelodeon.ru`) | ❌ רק באפליקציה (HTTP) |
| 747 | ניק ג'וניור (ברוסית) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`NickJr.ru`) | ✅ דרך מקור חלופי: Cinerama.uz – ספק IPTV באוזבקיסטן |
| 748 | גולי גירל Gulli Girl (ברוסית) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`GulliGirl.ru`) | ✅ דרך מקור חלופי: Cinerama.uz – ספק IPTV באוזבקיסטן |
| 749 | דאק טי וי פלוס ducktv PLUS HD | Antik Telecom (סלובקיה), נתיבי test | ⚠️ ממסר צד שלישי | iptv-org (`ducktvPLUS.sk`) | ✅ |
| 750 | בייבי פירסט BabyFirst HD | BabyFirst – JW Player FAST | ✅ FAST חינמי | iptv-org (`BabyFirst.us`) | ✅ |
| 751 | אנימציה קצרצרה Cartoons Short HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 752 | ערוץ לגו Lego Channel HD | LEGO Channel – AWS CloudFront FAST | ✅ FAST חינמי | iptv-org (`LegoChannel.us`) | ✅ |
| 753 | ערוץ טיז'י TiJi לילדים HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`TiJi.ru`) | ✅ דרך מקור חלופי: Cinerama.uz – ספק IPTV באוזבקיסטן |
| 754 | ג'ים ג'אם JimJam לילדים HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`JimJamEurope.uk`) | ❌ רק באפליקציה (HTTP) |
| 755 | אנימציה וילדים Fresh Kids HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 756 | קלאסיקות מצוירות 90s HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 757 | סרטי אנימציה Cartoons Big HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 758 | קולנוע אגדות וילדים HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`KinoKazka.es`) | ❌ רק באפליקציה (HTTP) |
| 759 | פיקסל TV ילדים HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`PixelTV.ua`) | ✅ דרך מקור חלופי: live-tv.cloud – ספק IPTV אוקראיני |
| 760 | ניקי קידס Niki Kids HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`NikiKids.ua`) | ❌ רק באפליקציה (HTTP) |
| 761 | קפטן פנטסטיקה HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`KapitanFantastika.ru`) | ✅ דרך מקור חלופי: Cinerama.uz – ספק IPTV באוזבקיסטן |
| 762 | ערוץ O! ילדים וטבע | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`O.ru`) | ❌ רק באפליקציה (HTTP) |
| 763 | דאק טי וי ducktv HD | Antik Telecom (סלובקיה), נתיבי test | ⚠️ ממסר צד שלישי | iptv-org (`ducktv.sk`) | ✅ |
| 764 | בייבי שארק Baby Shark TV HD | Baby Shark TV – Wurl (Roku FAST) | ✅ FAST חינמי | iptv-org (`BabySharkTV.us`) | ✅ |
| 765 | ערוץ השמש לילדים HD (Solntse Kids) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Solnce.ru`) | ✅ דרך מקור חלופי: ערוץ השמש (Солнце) – CDN |
| 766 | ערוץ מולט ילדים HD (Mult Kids) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Mult.ru`) | ✅ דרך מקור חלופי: Cinerama.uz – ספק IPTV באוזבקיסטן |
| 767 | מולט ומוזיקה לילדים HD (Mult & Music) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Multimuzyka.ru`) | ❌ רק באפליקציה (HTTP) |
| 768 | קרוסלה ערוץ הילדים HD (Carousel Kids) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Carousel.ru`) | ✅ דרך מקור חלופי: Smotrim (VGTRK) – הבעלים של קרוסלה |
| 769 | סי טי סי קידס HD (CTC Kids) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`STSkids.ru`) | ❌ רק באפליקציה (HTTP) |
| 770 | ערוץ אגדות וסיפורים HD (Fairy Tales Kids) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Vgostyakhuskazki.ru`) | ✅ דרך מקור חלופי: Cinerama.uz – ספק IPTV באוזבקיסטן |
| 771 | ערוץ אני אנימציה HD (Ani Kids) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Ani.ru`) | ❌ רק באפליקציה (HTTP) |
| 772 | ערוץ האנימציה PLUSPLUS HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`PLUSPLUS.ua`) | ❌ רק באפליקציה (HTTP) |
| 773 | Botflix TV | hls.botflix.tv | 🌐 ציבורי (iptv-org) | iptv-org (`BotflixTV.us`) | ✅ |
| 774 | Moonbug Kids | moonbug-rokuus.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`MoonbugKids.uk`) | ✅ |
| 775 | ADN TV+ | d3b73b34o7cvkq.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`ADNTVPlus.fr`) | ✅ |
| 776 | KiKA | kikageohls.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`KiKA.de`) | ✅ |
| 777 | Logos TV Kids | streamer1.streamhost.org | 🌐 ציבורי (iptv-org) | iptv-org (`LogosTVKids.es`) | ✅ |
| 778 | BeJoy.kids | stream.paroledivita.org | 🌐 ציבורי (iptv-org) | iptv-org (`BeJoykids.it`) | ✅ |
| 779 | Smile TV | 5d23269b3ec0c.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`SmileTV.ru`) | ✅ |
| 780 | Niki Junior | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`NikiJunior.ua`) | ❌ רק באפליקציה (HTTP) |
| 781 | Knowledge Network | d1wal6k3d7ssin.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`KnowledgeNetwork.ca`) | ✅ |
| 782 | Atfal & Mawaheb TV | 5aafcc5de91f1.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`AtfalMawahebTV.sa`) | ✅ |
| 783 | Koogi TV | 5d658d7e9f562.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`KoogiTV.eg`) | ✅ |
| 784 | Taha TV | stream.starmenajo.com | 🌐 ציבורי (iptv-org) | iptv-org (`TahaTV.lb`) | ✅ |
| 785 | TRT Cocuk | tv-trtcocuk.medya.trt.com.tr | 🌐 ציבורי (iptv-org) | iptv-org (`TRTCocuk.tr`) | ✅ |
| 786 | Epic Kids | cc-t8lqe1o99pszu.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`EpicKids.in`) | ✅ |
| 787 | EBS Kids | ebsonair.ebs.co.kr | 🌐 ציבורי (iptv-org) | iptv-org (`EBSKids.kr`) | ✅ |
| 788 | CCTV-14 | 74.91.26.218 | 🌐 ציבורי (iptv-org) | iptv-org (`CCTV14.cn`) | ❌ רק באפליקציה (HTTP) |
| 789 | El Chavo TV | live20.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`ElChavoTV.mx`) | ✅ |
| 790 | Pakapaka | 138.59.227.20 | 🌐 ציבורי (iptv-org) | iptv-org (`Pakapaka.ar`) | ❌ רק באפליקציה (HTTP) |
| 791 | Kuriakos Kids | w2.manasat.com | 🌐 ציבורי (iptv-org) | iptv-org (`KuriakosKids.pt`) | ✅ |
| 792 | JapanimTV | foxkidstv.be | 🌐 ציבורי (iptv-org) | iptv-org (`JapanimTV.be`) | ✅ |
| 793 | 4 Fun Kids | 4FUN TV – שרת רשמי | ✅ רשמי | iptv-org (`4FunKids.pl`) | ✅ |
| 794 | Pluto TV Juniorklubben | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVJuniorklubben.se`) | ✅ |
| 795 | TraLaLa DSN | hls01ns.antenaplay.ro | 🌐 ציבורי (iptv-org) | iptv-org (`TraLaLaDSN.ro`) | ✅ |
| 796 | Chikitoonz | 45.70.201.81 | 🌐 ציבורי (iptv-org) | iptv-org (`Chikitoonz.pe`) | ❌ רק באפליקציה (HTTP) |
| 797 | Groovia Kids | gist.githubusercontent.com | 🌐 ציבורי (iptv-org) | iptv-org (`GrooviaKids.id`) | ✅ |
| 798 | ON Kids | freem3u.xyz | 🌐 ציבורי (iptv-org) | iptv-org (`ONKids.vn`) | ✅ |
| 799 | Minimax | vodzong.mjunoon.tv | 🌐 ציבורי (iptv-org) | iptv-org (`Minimax.pk`) | ✅ |
| 800 | Pooya TV | tgn.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`PooyaTV.ir`) | ✅ |
| 801 | Tom And Jerry | live20.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`TomAndJerry.do`) | ✅ |
| 802 | MAX Anime | cdnlive.klicgo.net | 🌐 ציבורי (iptv-org) | iptv-org (`MAXAnime.ve`) | ✅ |
| 803 | Akili Kids! | m6.livecdn.io | 🌐 ציבורי (iptv-org) | iptv-org (`AkiliKids.ke`) | ✅ |
| 804 | ZooMoo | zoomoo-samsungau.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`ZooMoo.sg`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 805 | Amou Yazid TV | mkstream.servehttp.com | 🌐 ציבורי (iptv-org) | iptv-org (`AmouYazidTV.dz`) | ❌ רק באפליקציה (HTTP) |
| 806 | Dios Te Ve Kids | s.emisoras.tv | 🌐 ציבורי (iptv-org) | iptv-org (`DiosTeVeKids.hn`) | ✅ |
| 807 | Dreiko TV | cloudvideo.servers10.com | 🌐 ציבורי (iptv-org) | iptv-org (`DreikoTV.pa`) | ✅ |
| 808 | Extrema Kids TV | 627bb251f23c7.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`ExtremaKidsTV.cr`) | ✅ |
| 809 | Balapan | balapantv-stream.qazcdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`Balapan.kz`) | ✅ |
| 810 | ETV 2 | sb.err.ee | 🌐 ציבורי (iptv-org) | iptv-org (`ETV2.ee`) | ✅ |
| 811 | HappyKids | dil9xdvretp0f.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`HappyKids.us`) | ✅ |
| 812 | Persiana Junior | junhls.persiana.live | 🌐 ציבורי (iptv-org) | iptv-org (`PersianaJunior.fr`) | ✅ |
| 813 | RiC | rictv.iptv-playoutcenter.de | 🌐 ציבורי (iptv-org) | iptv-org (`RiC.de`) | ✅ |
| 814 | Boing | boing.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`Boing.it`) | ✅ |
| 815 | Smotrim 100% Detskoe | Smotrim (VGTRK) – הבעלים של קרוסלה | ✅ רשמי | iptv-org (`Smotrim100Detskoe.ru`) | ✅ |
| 816 | TVOKids | fastly.live.brightcove.com | 🌐 ציבורי (iptv-org) | iptv-org (`TVOKids.ca`) | ✅ |
| 817 | ABC Kids | c.mjh.nz | 🌐 ציבורי (iptv-org) | iptv-org (`ABCKids.au`) | ✅ |
| 818 | Minika Cocuk | rnttwmjcin.turknet.ercdn.net | 🌐 ציבורי (iptv-org) | iptv-org (`MinikaCocuk.tr`) | ✅ |
| 819 | The Jungle Book | cc-4bhi5osabejc9.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`TheJungleBook.in`) | ✅ |
| 820 | Box Kids TV | 170.83.49.66 | 🌐 ציבורי (iptv-org) | iptv-org (`BoxKidsTV.br`) | ❌ רק באפליקציה (HTTP) |
| 821 | GikTVMX | pistream.ddns.net | 🌐 ציבורי (iptv-org) | iptv-org (`GikTVMX.mx`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 822 | Plim Plim | 200.115.120.1 | 🌐 ציבורי (iptv-org) | iptv-org (`PlimPlim.ar`) | ❌ רק באפליקציה (HTTP) |
| 823 | Teletoon+ | 92.114.85.87 | 🌐 ציבורי (iptv-org) | iptv-org (`TeletoonPlus.pl`) | ❌ רק באפליקציה (HTTP) |
| 824 | Pluto TV Tecknade klassiker | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVTecknadeklassiker.se`) | ✅ |
| 825 | TraLaLa TV | hls01ns.antenaplay.ro | 🌐 ציבורי (iptv-org) | iptv-org (`TraLaLaTV.ro`) | ✅ |
| 826 | God Stands Kids Club TV | online.godstands.tv | 🌐 ציבורי (iptv-org) | iptv-org (`GodStandsKidsClubTV.pk`) | ✅ |
| 827 | Tiguerito TV | ss2.tvrdomi.com | 🌐 ציבורי (iptv-org) | iptv-org (`TigueritoTV.do`) | ✅ |
| 828 | eSports Max TV | cdnlive.klicgo.net | 🌐 ציבורי (iptv-org) | iptv-org (`eSportsMaxTV.ve`) | ✅ |
| 829 | Afarin TV | 65f16f0fdfc51.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`AfarinTV.iq`) | ✅ |
| 830 | KidsFlix | stream-us-east-1.getpublica.com | 🌐 ציבורי (iptv-org) | iptv-org (`KidsFlix.us`) | ✅ |
| 831 | Tortues Ninja TV | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`TortuesNinjaTV.de`) | ✅ |
| 832 | Super! | 495c5a85d9074f29acffeaea9e0215eb.msvdn.net | 🌐 ציבורי (iptv-org) | iptv-org (`Super.it`) | ✅ |
| 833 | Radost Moya | radm.mediacdn.ru | 🌐 ציבורי (iptv-org) | iptv-org (`RadostMoya.ru`) | ✅ |
| 834 | Minika Go | rnttwmjcin.turknet.ercdn.net | 🌐 ציבורי (iptv-org) | iptv-org (`MinikaGo.tr`) | ✅ |
| 835 | ZB Cartoon | server.zillarbarta.com | 🌐 ציבורי (iptv-org) | iptv-org (`ZBCartoon.in`) | ✅ |
| 836 | DumDum | 45.162.64.114 | 🌐 ציבורי (iptv-org) | iptv-org (`DumDum.br`) | ❌ רק באפליקציה (HTTP) |
| 837 | TeenNick | 92.114.85.87 | 🌐 ציבורי (iptv-org) | iptv-org (`TeenNick.pl`) | ❌ רק באפליקציה (HTTP) |
| 838 | Pluto TV Tecknat | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVTecknat.se`) | ✅ |
| 839 | Chinola TV | tv.wracanal10.com | 🌐 ציבורי (iptv-org) | iptv-org (`ChinolaTV.do`) | ✅ |
| 840 | PBS Kids Alaska | livestream.pbskids.org | 🌐 ציבורי (iptv-org) | iptv-org (`PBSKidsAlaska.us`) | ✅ |
| 841 | Bob Esponja | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`BobEsponja.de`) | ✅ |
| 842 | Tamyr | bsttv.bonus-tv.ru | 🌐 ציבורי (iptv-org) | iptv-org (`Tamyr.ru`) | ✅ |
| 843 | TRT Diyanet Cocuk | tv-trtdiyanetcocuk.medya.trt.com.tr | 🌐 ציבורי (iptv-org) | iptv-org (`TRTDiyanetCocuk.tr`) | ✅ |
| 844 | Discovery Kids | drk6xq0vhn.gpcdn.net | 🌐 ציבורי (iptv-org) | iptv-org (`DiscoveryKids.in`) | ✅ |
| 845 | Polsat JimJam | 185.227.34.179 | 🌐 ציבורי (iptv-org) | iptv-org (`PolsatJimJam.pl`) | ❌ רק באפליקציה (HTTP) |
| 846 | Pluto TV Ungdomsserier | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVUngdomsserier.se`) | ✅ |
| 847 | PBS Kids Mountain | livestream.pbskids.org | 🌐 ציבורי (iptv-org) | iptv-org (`PBSKidsMountain.us`) | ✅ |
| 848 | Pluto TV Kids Classics | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVKidsClassics.de`) | ✅ |
| 849 | Tooku | live-saha.cdnvideo.ru | 🌐 ציבורי (iptv-org) | iptv-org (`Tooku.ru`) | ❌ רק באפליקציה (HTTP) |
| 850 | WOW Kidz | yuppparoriglin.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`WOWKidz.in`) | ✅ |
| 851 | TVP ABC | 92.114.85.87 | 🌐 ציבורי (iptv-org) | iptv-org (`TVPABC.pl`) | ❌ רק באפליקציה (HTTP) |
| 852 | SvampBob Fyrkant | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`SvampBobFyrkant.se`) | ✅ |
| 853 | PBS Kids Pacific | livestream.pbskids.org | 🌐 ציבורי (iptv-org) | iptv-org (`PBSKidsPacific.us`) | ✅ |
| 854 | Pluto TV Teen | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVTeen.de`) | ✅ |
| 855 | Chithiram | cdn.pishow.tv | 🌐 ציבורי (iptv-org) | iptv-org (`Chithiram.in`) | ✅ |
| 856 | SvampeBob Firkant | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`SvampeBobFirkant.se`) | ✅ |
| 857 | Placid | toy.vcdn.fyi | 🌐 ציבורי (iptv-org) | iptv-org (`Placid.us`) | ✅ |
| 858 | Pluto TV Toons | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVToons.de`) | ✅ |
| 859 | Unique TV | mumt05.tangotv.in | 🌐 ציבורי (iptv-org) | iptv-org (`UniqueTV.in`) | ✅ |
| 860 | Totally Turtles | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`TotallyTurtles.se`) | ✅ |
| 861 | RetroCrush | linear-899.frequency.stream | 🌐 ציבורי (iptv-org) | iptv-org (`RetroCrush.us`) | ✅ |
| 862 | SpongeBob Schwammkopf | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`SpongeBobSchwammkopf.de`) | ✅ |
| 863 | Pogo | 103.151.60.162 | 🌐 ציבורי (iptv-org) | iptv-org (`Pogo.in`) | ❌ רק באפליקציה (HTTP) |
| 864 | Pluto TV Tegnefilm | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVTegnefilm.se`) | ✅ |
| 865 | Skwad Play | stream-us-east-1.getpublica.com | 🌐 ציבורי (iptv-org) | iptv-org (`SkwadPlay.us`) | ✅ |
| 866 | Totally Turtles | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`TotallyTurtles.de`) | ✅ |
| 867 | Avatar | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`Avatar.se`) | ✅ |
| 868 | Super Simple Songs | janson-supersimplesongs-1-us.roku.wurl.tv | 🌐 ציבורי (iptv-org) | iptv-org (`SuperSimpleSongs.us`) | ✅ |
| 869 | Teenage Mutant Ninja Turtles | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`TeenageMutantNinjaTurtles.de`) | ✅ |
| 870 | iCarly | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`iCarly.se`) | ✅ |
| 871 | Crunchyroll | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`Crunchyroll.us`) | ✅ |
| 872 | Avatar | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`Avatar.de`) | ✅ |
| 873 | South Park | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`SouthPark.se`) | ✅ |
| 874 | Ninja Kidz TV | d3868b4ny0rgdf.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`NinjaKidzTV.us`) | ✅ |
| 875 | Pluto TV Animation | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVAnimation.de`) | ✅ |
| 876 | South Park Armageddon | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`SouthParkArmageddon.se`) | ✅ |
| 877 | PBS Kids | livestream.pbskids.org | 🌐 ציבורי (iptv-org) | iptv-org (`PBSKids.us`) | ✅ |
| 878 | Pluto TV Retro Toons | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVRetroToons.de`) | ✅ |
| 879 | South Park Rockin' Out | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`SouthParkRockinOut.se`) | ✅ |
| 880 | PBS Kids Hawaii | livestream.pbskids.org | 🌐 ציבורי (iptv-org) | iptv-org (`PBSKidsHawaii.us`) | ✅ |
| 881 | Pluto TV Super Kids | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVSuperKids.de`) | ✅ |
| 882 | South Park: Butters Collection | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`SouthParkButtersCollection.se`) | ✅ |
| 883 | Sonic TV | stream-us-east-1.getpublica.com | 🌐 ציבורי (iptv-org) | iptv-org (`SonicTV.us`) | ✅ |
| 884 | Rugrats | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`Rugrats.de`) | ✅ |
| 885 | South Park: Cartman Collection | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`SouthParkCartmanCollection.se`) | ✅ |
| 886 | SpongeBob SquarePants | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`SpongeBobSquarePants.us`) | ✅ |
| 887 | South Park | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`SouthPark.de`) | ✅ |
| 888 | South Park: Into the Stars | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`SouthParkIntotheStars.se`) | ✅ |
| 889 | TG Junior | d3i6upqaqzosi1.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`TGJunior.us`) | ✅ |
| 890 | South Park Version Original | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`SouthParkVersionOriginal.de`) | ✅ |
| 891 | South Park: Kenny Collection | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`SouthParkKennyCollection.se`) | ✅ |
| 892 | Tortues Ninja TV | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`TortuesNinjaTV.us`) | ✅ |
| 893 | Drake & Josh | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`DrakeJosh.de`) | ✅ |
| 894 | South Park: Kyle Collection | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`SouthParkKyleCollection.se`) | ✅ |
| 895 | TVS Hi Tops | rpn.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`TVSHiTops.us`) | ✅ |
| 896 | Pluto TV Adult Animation | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVAdultAnimation.de`) | ✅ |
| 897 | South Park: Stan Collection | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`SouthParkStanCollection.se`) | ✅ |
| 898 | Bob l'eponge | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`Bobleponge.us`) | ✅ |
| 899 | Pluto TV Star Trek: Animation | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVStarTrekAnimation.de`) | ✅ |
| 900 | Detective Conan | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`DetectiveConan.us`) | ✅ |
| 901 | South Park: Cartman Collection | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`SouthParkCartmanCollection.de`) | ✅ |
| 902 | Las Tortugas Ninja | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`LasTortugasNinja.us`) | ✅ |
| 903 | South Park: Kenny Collection | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`SouthParkKennyCollection.de`) | ✅ |
| 904 | Super! SpongeBob | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`SuperSpongeBob.us`) | ✅ |
| 905 | South Park: Kyle Collection | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`SouthParkKyleCollection.de`) | ✅ |
| 906 | Bob Esponja Calca Quadrada | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`BobEsponjaCalcaQuadrada.us`) | ✅ |
| 907 | South Park: Original Version | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`SouthParkOriginalVersion.de`) | ✅ |
| 908 | Camp Spoopy | stream.ads.ottera.tv | 🌐 ציבורי (iptv-org) | iptv-org (`CampSpoopy.us`) | ✅ |
| 909 | South Park: Stan Collection | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`SouthParkStanCollection.de`) | ✅ |
| 910 | Giggle Mug | tvsen6.aynaott.com | 🌐 ציבורי (iptv-org) | iptv-org (`GiggleMug.us`) | ✅ |
| 911 | S Corby TV | vodcdn.bamboo-cloud.com | 🌐 ציבורי (iptv-org) | iptv-org (`SCorbyTV.us`) | ✅ |

## 📺 מוזיקה (Music TV)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 912 | 4ever Music Hits HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`4everMusic.ua`) | ❌ רק באפליקציה (HTTP) |
| 913 | 4Fun TV Pop Hits HD | 4FUN TV – שרת רשמי | ✅ רשמי | iptv-org (`4FunTV.pl`) | ✅ |
| 914 | ¡Viva Latino! Dance HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`VivaLatino.ua`) | ❌ רק באפליקציה (HTTP) |
| 915 | 1HD Music Television | שרת IP ללא שם (1HD) | ⚠️ ממסר צד שלישי | iptv-org (`1HDMusicTelevision.ru`) | ❌ רק באפליקציה (HTTP) |
| 916 | 70-80 TV Retro Hits | 70-80 TV – Wowza Streamlock | ✅ רשמי | iptv-org (`7080TV.it`) | ✅ |
| 917 | ערוץ המוזיקה של קשת | קשת – ה-CDN של mako (עם הרשאת צפייה של mako) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 918 | רדיוס 100FM – וידאו | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 919 | Botflix Radioo | hls.botflix.tv | 🌐 ציבורי (iptv-org) | iptv-org (`BotflixRadioo.us`) | ✅ |
| 920 | Afrobeat TV Entertainment | stream.ecable.tv | 🌐 ציבורי (iptv-org) | iptv-org (`AfrobeatTVEntertainment.uk`) | ✅ |
| 921 | Trace Africa | channels.trace.plus | 🌐 ציבורי (iptv-org) | iptv-org (`TraceAfrica.fr`) | ✅ |
| 922 | DMF | d-m-f.iptv-playoutcenter.de | 🌐 ציבורי (iptv-org) | iptv-org (`DMF.de`) | ✅ |
| 923 | Music TV Granada | cloudvideo.servers10.com | 🌐 ציבורי (iptv-org) | iptv-org (`MusicTVGranada.es`) | ✅ |
| 924 | FM ITALIA | stream2.xdevel.com | 🌐 ציבורי (iptv-org) | iptv-org (`FMITALIA.it`) | ✅ |
| 925 | Maidan | live-maidantv.cdnvideo.ru | 🌐 ציבורי (iptv-org) | iptv-org (`Maidan.ru`) | ✅ |
| 926 | Happy Radio TV | tv.mediacp.eu | 🌐 ציבורי (iptv-org) | iptv-org (`HappyRadioTV.ua`) | ✅ |
| 927 | Movie Music by Stingray | lotus.stingray.com | 🌐 ציבורי (iptv-org) | iptv-org (`MovieMusicbyStingray.ca`) | ✅ |
| 928 | Folk Klub TV | tv1.intv.mk | 🌐 ציבורי (iptv-org) | iptv-org (`FolkKlubTV.au`) | ❌ רק באפליקציה (HTTP) |
| 929 | MBC FM | dbbv9umqcd7cs.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`MBCFM.ae`) | ✅ |
| 930 | Jeddah Radio | live.kwikmotion.com | 🌐 ציבורי (iptv-org) | iptv-org (`JeddahRadio.sa`) | ✅ |
| 931 | NogoumFMTV | nogoumtv.nrpstream.com | 🌐 ציבורי (iptv-org) | iptv-org (`NogoumFMTV.eg`) | ✅ |
| 932 | Aghani Aghani TV | cdn.streamlane.tv | 🌐 ציבורי (iptv-org) | iptv-org (`AghaniAghaniTV.lb`) | ✅ |
| 933 | Power Dance | livetv.powerapp.com.tr | 🌐 ציבורי (iptv-org) | iptv-org (`PowerDance.tr`) | ✅ |
| 934 | 9X Jalwa | wiselp.wiseplayout.com | 🌐 ציבורי (iptv-org) | iptv-org (`9XJalwa.in`) | ✅ |
| 935 | Gugak TV | mgugaklive.nowcdn.co.kr | 🌐 ציבורי (iptv-org) | iptv-org (`GugakTV.kr`) | ✅ |
| 936 | CCTV-Storm Music | 38.75.136.137 | 🌐 ציבורי (iptv-org) | iptv-org (`CCTVStormMusic.cn`) | ❌ רק באפליקציה (HTTP) |
| 937 | TikTok Radio Brasil | d1bl6tskrpq9ze.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`TikTokRadioBrasil.br`) | ✅ |
| 938 | Bandamax | streaming.onnethn.com | 🌐 ציבורי (iptv-org) | iptv-org (`Bandamax.mx`) | ✅ |
| 939 | La 100 | mdstrm.com | 🌐 ציבורי (iptv-org) | iptv-org (`La100.ar`) | ✅ |
| 940 | ON FM | 5ce9406b73c33.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`ONFM.pt`) | ✅ |
| 941 | 360TuneBox | Antik Telecom (סלובקיה), נתיבי test | ⚠️ ממסר צד שלישי | iptv-org (`360TuneBox.nl`) | ✅ |
| 942 | StuBru | live.vrtcdn.be | 🌐 ציבורי (iptv-org) | iptv-org (`StuBru.be`) | ✅ |
| 943 | RADIO DISCOunt TV | 70-80 TV – Wowza Streamlock | ✅ רשמי | iptv-org (`RADIODISCOuntTV.ch`) | ✅ |
| 944 | Antenne Kaernten | 60efd7a2b4d02.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`AntenneKaernten.at`) | ✅ |
| 945 | Radio RAM | 1888165924.rsc.cdn77.org | 🌐 ציבורי (iptv-org) | iptv-org (`RadioRAM.pl`) | ✅ |
| 946 | Baraza TV | eco.streams.ovh | 🌐 ציבורי (iptv-org) | iptv-org (`BarazaTV.gr`) | ✅ |
| 947 | JRTV Jarviradio | streamer.radiotaajuus.fi | 🌐 ציבורי (iptv-org) | iptv-org (`JRTVJarviradio.fi`) | ✅ |
| 948 | Ocko Expres | ocko-live.ssl.cdn.cra.cz | 🌐 ציבורי (iptv-org) | iptv-org (`OckoExpres.cz`) | ✅ |
| 949 | Party Mix | hls02ns.antenaplay.ro | 🌐 ציבורי (iptv-org) | iptv-org (`PartyMix.ro`) | ✅ |
| 950 | Oxygen Music | oxygenmusic.hu | 🌐 ציבורי (iptv-org) | iptv-org (`OxygenMusic.hu`) | ✅ |
| 951 | Pauta | redirector.rudo.video | 🌐 ציבורי (iptv-org) | iptv-org (`Pauta.cl`) | ✅ |
| 952 | La Kalle | mdstrm.com | 🌐 ציבורי (iptv-org) | iptv-org (`LaKalle.co`) | ✅ |
| 953 | Radio Onda Digital | live.ondadigitaltv.com | 🌐 ציבורי (iptv-org) | iptv-org (`RadioOndaDigital.pe`) | ✅ |
| 954 | ON Music | freem3u.xyz | 🌐 ציבורי (iptv-org) | iptv-org (`ONMusic.vn`) | ✅ |
| 955 | JooMusic | livecdn.live247stream.com | 🌐 ציבורי (iptv-org) | iptv-org (`JooMusic.pk`) | ✅ |
| 956 | AFN TV | bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`AFNTV.ir`) | ✅ |
| 957 | HMTV | customer-yzxu6n447dx2x45h.cloudflarestream.com | 🌐 ציבורי (iptv-org) | iptv-org (`HMTV.ng`) | ✅ |
| 958 | Gex TV | live20.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`GexTV.cr`) | ✅ |
| 959 | Venus Media | tigocloud.desdeparaguay.net | 🌐 ציבורי (iptv-org) | iptv-org (`VenusMedia.py`) | ✅ |
| 960 | El Sol Network TV | tv.wracanal10.com | 🌐 ציבורי (iptv-org) | iptv-org (`ElSolNetworkTV.ec`) | ✅ |
| 961 | Best of Dance TV | m1b2.worldcast.tv | 🌐 ציבורי (iptv-org) | iptv-org (`BestofDanceTV.ee`) | ✅ |
| 962 | Makao TV | live.obslivestream.com | 🌐 ציבורי (iptv-org) | iptv-org (`MakaoTV.do`) | ✅ |
| 963 | Magic TV | bss1.neterra.tv | 🌐 ציבורי (iptv-org) | iptv-org (`MagicTV.bg`) | ✅ |
| 964 | Cool FM 98.9 | live2.tensila.com | 🌐 ציבורי (iptv-org) | iptv-org (`CoolFM989.aw`) | ✅ |
| 965 | Planet 100.9 FM | streamlov.alsolnet.com | 🌐 ציבורי (iptv-org) | iptv-org (`Planet1009FM.pa`) | ✅ |
| 966 | Radio Hit FM TV | peer2.tdiradio.com | 🌐 ציבורי (iptv-org) | iptv-org (`RadioHitFMTV.rs`) | ✅ |
| 967 | Imagen FM 105.1 | streaming.imagenfm105-1.com | 🌐 ציבורי (iptv-org) | iptv-org (`ImagenFM1051.hn`) | ✅ |
| 968 | FUSION TV | edge20.vedge.infomaniak.com | 🌐 ציבורי (iptv-org) | iptv-org (`FUSIONTV.mq`) | ✅ |
| 969 | Madras FM TV | edge12.vedge.infomaniak.com | 🌐 ציבורי (iptv-org) | iptv-org (`MadrasFMTV.gp`) | ✅ |
| 970 | BTA TV | cloud.fastchannel.es | 🌐 ציבורי (iptv-org) | iptv-org (`BTATV.ve`) | ✅ |
| 971 | The Retro Channel | 583ed2f226ac2.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`TheRetroChannel.pr`) | ✅ |
| 972 | Folk TV | stream.nasatv.com.mk | 🌐 ציבורי (iptv-org) | iptv-org (`FolkTV.mk`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 973 | Tezaur TV | tezaurtv.md | 🌐 ציבורי (iptv-org) | iptv-org (`TezaurTV.md`) | ✅ |
| 974 | RTL Gold | stream.rtl.lu | 🌐 ציבורי (iptv-org) | iptv-org (`RTLGold.lu`) | ✅ |
| 975 | Audiokiss | master.tucableip.com | 🌐 ציבורי (iptv-org) | iptv-org (`Audiokiss.bo`) | ✅ |
| 976 | CMC TV | stream.cmctv.hr | 🌐 ציבורי (iptv-org) | iptv-org (`CMCTV.hr`) | ✅ |
| 977 | 98.1 Pearl FM | live2.tensila.com | 🌐 ציבורי (iptv-org) | iptv-org (`981PearlFM.sx`) | ✅ |
| 978 | Scorch TV | w6oy7x7jlxrg-hls-live.5centscdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`ScorchTV.tt`) | ✅ |
| 979 | Star 101 FM | live2.tensila.com | 🌐 ציבורי (iptv-org) | iptv-org (`Star101FM.gu`) | ✅ |
| 980 | Tjil TV | hls-1zdhtxpm-livepush.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`TjilTV.na`) | ✅ |
| 981 | Falastini TV | rp.tactivemedia.com | 🌐 ציבורי (iptv-org) | iptv-org (`FalastiniTV.ps`) | ✅ |
| 982 | Jordan Songs | playlist.fasttvcdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`JordanSongs.jo`) | ✅ |
| 983 | Mosaique FM | webcam.mosaiquefm.net | 🌐 ציבורי (iptv-org) | iptv-org (`MosaiqueFM.tn`) | ✅ |
| 984 | A2i Music | stream.sen-gt.com | 🌐 ציבורי (iptv-org) | iptv-org (`A2iMusic.sn`) | ✅ |
| 985 | Ivoire Channel | video1.getstreamhosting.com | 🌐 ציבורי (iptv-org) | iptv-org (`IvoireChannel.ci`) | ✅ |
| 986 | Muzangala TV | 5cf4a2c2512a2.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`MuzangalaTV.ao`) | ✅ |
| 987 | TV Cidade | stmv1.srvif.com | 🌐 ציבורי (iptv-org) | iptv-org (`TVCidade.cv`) | ✅ |
| 988 | AlbKanale Music TV | albportal.net | 🌐 ציבורי (iptv-org) | iptv-org (`AlbKanaleMusicTV.al`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 989 | Isango TV | 80.241.215.175 | 🌐 ציבורי (iptv-org) | iptv-org (`IsangoTV.rw`) | ❌ רק באפליקציה (HTTP) |
| 990 | Ishtar TV | stream.ishtartv.com | 🌐 ציבורי (iptv-org) | iptv-org (`IshtarTV.iq`) | ✅ |
| 991 | K100 | k100streymi.mbl.is | 🌐 ציבורי (iptv-org) | iptv-org (`K100.is`) | ✅ |
| 992 | Kayhan TV | playout395.livestreamingcdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`KayhanTV.af`) | ✅ |
| 993 | KN Music TV | cdn4.yayin.com.tr | 🌐 ציבורי (iptv-org) | iptv-org (`KNMusicTV.az`) | ✅ |
| 994 | Look Thoong TV | livefta.malimarcdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`LookThoongTV.la`) | ✅ |
| 995 | M-1 | m-1.data.lt | 🌐 ציבורי (iptv-org) | iptv-org (`M1.lt`) | ✅ |
| 996 | Radio SWH TV | 00ff00.latnet.media | 🌐 ציבורי (iptv-org) | iptv-org (`RadioSWHTV.lv`) | ✅ |
| 997 | Turkmen Owazy | alpha.tv.online.tm | 🌐 ציבורי (iptv-org) | iptv-org (`TurkmenOwazy.tm`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 998 | Harmony Gospel Music TV | tgn.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`HarmonyGospelMusicTV.us`) | ✅ |
| 999 | NOW Rock | lightning-now90s-samsungnz.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`NOWRock.uk`) | ✅ |
| 1000 | Trace Jama | channels.trace.plus | 🌐 ציבורי (iptv-org) | iptv-org (`TraceJama.fr`) | ✅ |
| 1001 | Lausitzwelle | h056.video-stream-hosting.de | 🌐 ציבורי (iptv-org) | iptv-org (`Lausitzwelle.de`) | ✅ |
| 1002 | Kiss Kiss TV | 58f12ffd2447a.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`KissKissTV.it`) | ✅ |
| 1003 | Muz Soyuz | hls-tvsoyuz.cdnvideo.ru | 🌐 ציבורי (iptv-org) | iptv-org (`MuzSoyuz.ru`) | ✅ |
| 1004 | Muzvar | live-tv.cloud – ספק IPTV אוקראיני | ⚠️ ממסר צד שלישי | iptv-org (`Muzvar.ua`) | ✅ |
| 1005 | Qello Concerts by Stingray | lotus.stingray.com | 🌐 ציבורי (iptv-org) | iptv-org (`QelloConcertsbyStingray.ca`) | ✅ |
| 1006 | INTV Australia | tv1.intv.mk | 🌐 ציבורי (iptv-org) | iptv-org (`INTVAustralia.au`) | ❌ רק באפליקציה (HTTP) |
| 1007 | PMC | pmchls.wns.live | 🌐 ציבורי (iptv-org) | iptv-org (`PMC.ae`) | ✅ |
| 1008 | Nidae AlIslam Radio | live.kwikmotion.com | 🌐 ציבורי (iptv-org) | iptv-org (`NidaeAlIslamRadio.sa`) | ✅ |
| 1009 | El Radio 9090 FM | 9090video.mobtada.com | 🌐 ציבורי (iptv-org) | iptv-org (`ElRadio9090FM.eg`) | ✅ |
| 1010 | mfm | hms.pfs.gdn | 🌐 ציבורי (iptv-org) | iptv-org (`mfm.lb`) | ✅ |
| 1011 | Power Love | livetv.powerapp.com.tr | 🌐 ציבורי (iptv-org) | iptv-org (`PowerLove.tr`) | ✅ |
| 1012 | Aaryaa TV | stream.ottlive.co.in | 🌐 ציבורי (iptv-org) | iptv-org (`AaryaaTV.in`) | ✅ |
| 1013 | CCTV-11 | 74.91.26.218 | 🌐 ציבורי (iptv-org) | iptv-org (`CCTV11.cn`) | ❌ רק באפליקציה (HTTP) |
| 1014 | +MaisUm | stream.ichibansys.com | 🌐 ציבורי (iptv-org) | iptv-org (`PlusMaisUm.br`) | ✅ |
| 1015 | Conecta TV | stream8.mexiserver.com | 🌐 ציבורי (iptv-org) | iptv-org (`ConectaTV.mx`) | ✅ |
| 1016 | X Level Media | tuvideoonline.com.ar | 🌐 ציבורי (iptv-org) | iptv-org (`XLevelMedia.ar`) | ✅ |
| 1017 | RFPtv | video03.logicahost.com.br | 🌐 ציבורי (iptv-org) | iptv-org (`RFPtv.pt`) | ✅ |
| 1018 | XITE Hits | d726x48n2pd5h.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`XITEHits.nl`) | ✅ |
| 1019 | Klara | live.vrtcdn.be | 🌐 ציבורי (iptv-org) | iptv-org (`Klara.be`) | ✅ |
| 1020 | Antenne Steiermark | 60efd7a2b4d02.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`AntenneSteiermark.at`) | ✅ |
| 1021 | Disco Polo Music | 185.227.34.179 | 🌐 ציבורי (iptv-org) | iptv-org (`DiscoPoloMusic.pl`) | ❌ רק באפליקציה (HTTP) |
| 1022 | Panik TV | livetv.streams.ovh | 🌐 ציבורי (iptv-org) | iptv-org (`PanikTV.gr`) | ✅ |
| 1023 | Navahang TV | hls.navahang.live | 🌐 ציבורי (iptv-org) | iptv-org (`NavahangTV.fi`) | ✅ |
| 1024 | Retro Music TV | stream.mediawork.cz | 🌐 ציבורי (iptv-org) | iptv-org (`RetroMusicTV.cz`) | ✅ |
| 1025 | Impact TV 2 | online.tvimpact.live | 🌐 ציבורי (iptv-org) | iptv-org (`ImpactTV2.ro`) | ✅ |
| 1026 | Rewind TV | tls-cl.cdnz.cl | 🌐 ציבורי (iptv-org) | iptv-org (`RewindTV.cl`) | ✅ |
| 1027 | La Morada Radio TV | movil.ejeserver.com | 🌐 ציבורי (iptv-org) | iptv-org (`LaMoradaRadioTV.co`) | ✅ |
| 1028 | La Fabulosa TV | eu1.servers10.com | 🌐 ציבורי (iptv-org) | iptv-org (`LaFabulosaTV.pe`) | ✅ |
| 1029 | Jalwa TV | vodzong.mjunoon.tv | 🌐 ציבורי (iptv-org) | iptv-org (`JalwaTV.pk`) | ✅ |
| 1030 | T2 TV | t2hls.wns.live | 🌐 ציבורי (iptv-org) | iptv-org (`T2TV.ir`) | ✅ |
| 1031 | AMusic Channel | viewmedia7219.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`AMusicChannel.ng`) | ✅ |
| 1032 | Canal 3 KMK TV | live20.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`Canal3KMKTV.cr`) | ✅ |
| 1033 | Dismar Radio TV | rds3.desdeparaguay.net | 🌐 ציבורי (iptv-org) | iptv-org (`DismarRadioTV.py`) | ✅ |
| 1034 | FM Mundo | video2.makrodigital.com | 🌐 ציבורי (iptv-org) | iptv-org (`FMMundo.ec`) | ✅ |
| 1035 | DanceTV Deep House District | m1b2.worldcast.tv | 🌐 ציבורי (iptv-org) | iptv-org (`DanceTVDeepHouseDistrict.ee`) | ✅ |
| 1036 | Montones TV | lbgo.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`MontonesTV.do`) | ✅ |
| 1037 | The Voice | bss1.neterra.tv | 🌐 ציבורי (iptv-org) | iptv-org (`TheVoice.bg`) | ✅ |
| 1038 | Power 101.7 FM | vcp11.myplaytv.com | 🌐 ציבורי (iptv-org) | iptv-org (`Power1017FM.aw`) | ✅ |
| 1039 | Super Q Panama | vcp8.myplaytv.com | 🌐 ציבורי (iptv-org) | iptv-org (`SuperQPanama.pa`) | ✅ |
| 1040 | Radio Karolina TV | peer2.tdiradio.com | 🌐 ציבורי (iptv-org) | iptv-org (`RadioKarolinaTV.rs`) | ✅ |
| 1041 | La Top 102.9 | 59d39900ebfb8.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`LaTop1029.hn`) | ✅ |
| 1042 | Identite Tele Caraibes | vdo2.pro-fhi.net | 🌐 ציבורי (iptv-org) | iptv-org (`IdentiteTeleCaraibes.mq`) | ✅ |
| 1043 | RHT Guadeloupe | edge12.vedge.infomaniak.com | 🌐 ציבורי (iptv-org) | iptv-org (`RHTGuadeloupe.gp`) | ✅ |
| 1044 | ShowVen TV | cdnbarinas.tvzamora.com | 🌐 ציבורי (iptv-org) | iptv-org (`ShowVenTV.ve`) | ✅ |
| 1045 | La-X | stream.eleden.com | 🌐 ציבורי (iptv-org) | iptv-org (`LaX.pr`) | ✅ |
| 1046 | Love TV | stream.nasatv.com.mk | 🌐 ציבורי (iptv-org) | iptv-org (`LoveTV.mk`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 1047 | Hit FM | hitfm.nfthost.net | 🌐 ציבורי (iptv-org) | iptv-org (`HitFM.md`) | ✅ |
| 1048 | Today Radio | stream.rtl.lu | 🌐 ציבורי (iptv-org) | iptv-org (`TodayRadio.lu`) | ✅ |
| 1646 | 3ABN Praise Him Music Network HD | 3abn.bozztv.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 1647 | 4 Fun Dance HD | 92.114.85.87 | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 1648 | 4Kurd HD | 4kuhls.persiana.live | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 1649 | 4 Music HD | musichls.persiana.live | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 1650 | 7S Music HD | cdn.pishow.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 1651 | 7X Music HD | cdn2.in | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 1652 | 8XM HD | cdn4.mjunoon.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 1653 | 30A Music HD | 30a-tv.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 1654 | 51 Radio TV HD | wms.shared.streamshow.it | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 1655 | 88 Stereo HD | k3.usastreams.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 1656 | Abdulmajeed Abdullah HD | d2hng5r56zpsbw.cloudfront.net | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 1657 | ACE Country Radio KPVM-LD HD | 2-fss-1.streamhoster.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 1658 | Activa TV HD | streamtv.mediasector.es | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 1659 | AE Radio TV HD | tls-cl.cdnz.cl | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 1660 | Alcance FM PLAY TV HD | video.wilohosting.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 1661 | Allegro HD HD | 158.172.155.130 | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 1662 | AlternaTV HD | tv.alterna.ar | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 1663 | Andy Haryana HD | mumt03.tangotv.in | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 1664 | Arabica TV HD | istream.binarywaves.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 1665 | Atomic Academy TV HD | atomic.streamnet.ro | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 1666 | Atomic TV HD | atomic.streamnet.ro | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 1667 | Avang TV HD | hls.avang.live | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 1668 | AXS TV HD | 23.239.31.26 | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 1669 | Azahares Radiovisual Multimedia HD | streamyes.alsolnet.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 1670 | B4U Music HD | amg01408-amg01408c5-amgplt0747.playout.now3.amagi.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 1671 | Baby Time HD | 178.134.1.158 | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 1672 | Balle Balle HD | streams.tangotv.in | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 1673 | Balle Balle HD HD | mcncdndigital.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 1674 | Bandamax HD | 181.78.106.127 | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 1675 | Baraza TV Deep House HD | rtmp.streams.ovh | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 1676 | Baraza TV Relaxing HD | rtmp.streams.ovh | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 1677 | Beats Radio HD | videostream.shockmedia.com.ar | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 1678 | BEATTV HD | tv.beatfm.nl | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 1679 | Bibel TV Musik HD | bibeltv03.iptv-playoutcenter.de | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 1680 | BIZ Music HD | fl.biztv.media | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 1681 | Black HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 1682 | BOKTV HD | livestream2.bokradio.co.za | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 1683 | Boogle Bollywood HD | yuppnimrestreammum.akamaized.net | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 1684 | Boom TV HD | tvboom.ro | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 1685 | BPX TV Radio HD | video1.getstreamhosting.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 1686 | BRIDGE HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 1687 | BRIDGE Classic HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 1688 | BRIDGE Deluxe HD HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 1689 | BRIDGE Hits HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 1690 | BRIDGE Rock HD | 178.134.1.158 | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 1691 | Buenisima Radio TV HD | canal.mediaserver.com.co | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 1692 | Cadena Elite HD | cloudvideo.servers10.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 1693 | California Music Channel HD | cmc-cmctv-cineverse.amagi.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |

## 📺 טבע ודוקו (Documentary & Nature)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 1049 | Animal Planet World HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`AnimalPlanet.ru`) | ❌ רק באפליקציה (HTTP) |
| 1050 | Discovery חקירות (ID) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`InvestigationDiscovery.ru`) | ❌ רק באפליקציה (HTTP) |
| 1051 | עולם וטבע HD (viju+ Planet) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`vijuPlusPlanet.ru`) | ❌ רק באפליקציה (HTTP) |
| 1052 | BBC Earth Live HD | BBC Earth – Amagi / Xumo FAST | ✅ FAST חינמי | iptv-org (`BBCEarth.uk`) | ✅ |
| 1053 | Asharq Discovery HD | Asharq Discovery – ItWorks (רשמי) | ✅ רשמי | iptv-org (`AsharqDiscovery.sa`) | ✅ |
| 1054 | 365 ימי היסטוריה (History TV) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`365daysTV.ru`) | ✅ דרך מקור חלופי: Cinerama.uz – ספק IPTV באוזבקיסטן |
| 1055 | Dateline 24/7 | d1si3n1st4nkgb.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`Dateline247.us`) | ✅ |
| 1056 | History Hit | lds-timeline-rakuten.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`HistoryHit.uk`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 1057 | Francophonie24 | 5421175365ea3.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`Francophonie24.fr`) | ✅ |
| 1058 | Adventure Earth | a57e9c69976649b582a8d7604c00e69a.mediatailor.us-east-1.amazonaws.com | 🌐 ציבורי (iptv-org) | iptv-org (`AdventureEarth.de`) | ✅ |
| 1059 | Crimen & Historia | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`CrimenHistoria.es`) | ✅ |
| 1060 | Total Crime | aegis-cloudfront-1.tubi.video | 🌐 ציבורי (iptv-org) | iptv-org (`TotalCrime.ca`) | ✅ |
| 1061 | DocuBay TV | cc-mgr91yrk4pehy.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`DocuBayTV.in`) | ✅ |
| 1062 | CGTN Documentary | amg00405-rakutentv-cgtndocumentary-rakuten-0ql8j.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`CGTNDocumentary.cn`) | ✅ |
| 1063 | National Geographic | 138.121.15.230 | 🌐 ציבורי (iptv-org) | iptv-org (`NationalGeographic.mx`) | ❌ רק באפליקציה (HTTP) |
| 1064 | DocuBox | Antik Telecom (סלובקיה), נתיבי test | ⚠️ ממסר צד שלישי | iptv-org (`DocuBox.nl`) | ✅ |
| 1065 | Polsat Viasat History | 185.227.34.179 | 🌐 ציבורי (iptv-org) | iptv-org (`PolsatViasatHistory.pl`) | ❌ רק באפליקציה (HTTP) |
| 1066 | Pluto TV Dokumentar | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVDokumentar.se`) | ✅ |
| 1067 | CNA Originals | amg01082-cna-amg01082c1-rlaxx-us-11304.playouts.now.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`CNAOriginals.sg`) | ✅ |
| 1068 | Karbala Documentary | ktvlive.online | 🌐 ציבורי (iptv-org) | iptv-org (`KarbalaDocumentary.iq`) | ✅ |
| 1069 | Ecovision | streaming.grupomediosdelnorte.com | 🌐 ציבורי (iptv-org) | iptv-org (`Ecovision.do`) | ✅ |
| 1070 | UMSA TVU Internacional | edge.enhdtv.com | 🌐 ציבורי (iptv-org) | iptv-org (`UMSATVUInternacional.bo`) | ✅ |
| 1071 | MWD Documentary | 203.81.84.130 | 🌐 ציבורי (iptv-org) | iptv-org (`MWDDocumentary.mm`) | ❌ רק באפליקציה (HTTP) |
| 1072 | Deadly Women | d1mgh147xpmgs8.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`DeadlyWomen.us`) | ✅ |
| 1073 | Into Crime | amg00711-zylo-amg00711c10-rakuten-fr-6731.playouts.now.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`IntoCrime.fr`) | ✅ |
| 1074 | Autentic History | 9e754fa707344ccca6d84955c8fcaf36.mediatailor.us-east-1.amazonaws.com | 🌐 ציבורי (iptv-org) | iptv-org (`AutenticHistory.de`) | ✅ |
| 1075 | Historia Y Vida | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`HistoriaYVida.es`) | ✅ |
| 1076 | Smotrim 100% Fakty | Smotrim (VGTRK) – הבעלים של קרוסלה | ✅ רשמי | iptv-org (`Smotrim100Fakty.ru`) | ✅ |
| 1077 | NatureTime | bamusa-naturetime-emea-eng-rakuten.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`NatureTime.ca`) | ✅ |
| 1078 | Epic Crimes | cc-wsuyg2uxeak04.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`EpicCrimes.in`) | ✅ |
| 1079 | Discovering China | fastlive.cctvplus.com | 🌐 ציבורי (iptv-org) | iptv-org (`DiscoveringChina.cn`) | ✅ |
| 1080 | History | 92.114.85.87 | 🌐 ציבורי (iptv-org) | iptv-org (`History.pl`) | ❌ רק באפליקציה (HTTP) |
| 1081 | Pluto TV Varldskrigen | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVVarldskrigen.se`) | ✅ |
| 1082 | Documentary+ | ef79b15c8c7c46c7a9de9d33001dbd07.mediatailor.us-west-2.amazonaws.com | 🌐 ציבורי (iptv-org) | iptv-org (`DocumentaryPlus.us`) | ✅ |
| 1083 | Persiana Docs | scihls.persiana.live | 🌐 ציבורי (iptv-org) | iptv-org (`PersianaDocs.fr`) | ✅ |
| 1084 | Dokusat | muc3.iptv-playoutcenter.de | 🌐 ציבורי (iptv-org) | iptv-org (`Dokusat.de`) | ✅ |
| 1085 | Storie Criminali | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`StorieCriminali.es`) | ✅ |
| 1086 | Nano | 185.23.80.23 | 🌐 ציבורי (iptv-org) | iptv-org (`Nano.ru`) | ❌ רק באפליקציה (HTTP) |
| 1087 | Animal Planet | drk6xq0vhn.gpcdn.net | 🌐 ציבורי (iptv-org) | iptv-org (`AnimalPlanet.in`) | ✅ |
| 1088 | CCTV-9 | xykt-fix.github.io | 🌐 ציבורי (iptv-org) | iptv-org (`CCTV9.cn`) | ✅ |
| 1089 | National Geographic | 92.114.85.87 | 🌐 ציבורי (iptv-org) | iptv-org (`NationalGeographic.pl`) | ❌ רק באפליקציה (HTTP) |
| 1090 | Smithsonian Channel Selects | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`SmithsonianChannelSelects.se`) | ✅ |
| 1091 | The Nest | fast-channels.sinclairstoryline.com | 🌐 ציבורי (iptv-org) | iptv-org (`TheNest.us`) | ✅ |
| 1092 | Persiana Travel | ptravelhls.persiana.live | 🌐 ציבורי (iptv-org) | iptv-org (`PersianaTravel.fr`) | ✅ |
| 1093 | Terra Mater WILD | 53ff3982.wurl.com | 🌐 ציבורי (iptv-org) | iptv-org (`TerraMaterWILD.de`) | ✅ |
| 1094 | Investigation Discovery | drk6xq0vhn.gpcdn.net | 🌐 ציבורי (iptv-org) | iptv-org (`InvestigationDiscovery.in`) | ✅ |
| 1095 | CCTV-Weapon & Technology | 38.75.136.137 | 🌐 ציבורי (iptv-org) | iptv-org (`CCTVWeaponTechnology.cn`) | ❌ רק באפליקציה (HTTP) |
| 1096 | National Geographic Wild | 92.114.85.87 | 🌐 ציבורי (iptv-org) | iptv-org (`NationalGeographicWild.pl`) | ❌ רק באפליקציה (HTTP) |
| 1097 | Pluto TV Alien Invasion | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVAlienInvasion.se`) | ✅ |
| 1098 | National Geographic Latin America | barinaslive1.boldmss.com | 🌐 ציבורי (iptv-org) | iptv-org (`NationalGeographicLatinAmerica.us`) | ✅ |
| 1099 | Pluto TV Histoire | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVHistoire.de`) | ✅ |
| 1100 | Safari TV | mumt04.tangotv.in | 🌐 ציבורי (iptv-org) | iptv-org (`SafariTV.in`) | ✅ |
| 1101 | Polsat Viasat Explore | 92.114.85.87 | 🌐 ציבורי (iptv-org) | iptv-org (`PolsatViasatExplore.pl`) | ❌ רק באפליקציה (HTTP) |
| 1102 | Pluto TV Space | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVSpace.se`) | ✅ |
| 1103 | Crime 360 | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`Crime360.us`) | ✅ |
| 1104 | Pluto TV Historia | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVHistoria.de`) | ✅ |
| 1105 | Polsat Viasat Nature | 92.114.85.87 | 🌐 ציבורי (iptv-org) | iptv-org (`PolsatViasatNature.pl`) | ❌ רק באפליקציה (HTTP) |
| 1106 | Pluto TV Verdenskrigene | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVVerdenskrigene.se`) | ✅ |
| 1107 | Get.factual | d1nhni5l2n8hjt.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`Getfactual.us`) | ✅ |
| 1108 | kabel eins Doku | s6.hopslan.com | 🌐 ציבורי (iptv-org) | iptv-org (`kabeleinsDoku.de`) | ✅ |
| 1109 | History Asia | freem3u.xyz | 🌐 ציבורי (iptv-org) | iptv-org (`HistoryAsia.us`) | ✅ |
| 1110 | Pluto TV Alien Invasion | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVAlienInvasion.de`) | ✅ |
| 1111 | Law & Crime | a-cdn.klowdtv.com | 🌐 ציבורי (iptv-org) | iptv-org (`LawCrime.us`) | ✅ |
| 1112 | Pluto TV Britain at War | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVBritainatWar.de`) | ✅ |
| 1113 | Atlas | cdn.whiplash.cc | 🌐 ציבורי (iptv-org) | iptv-org (`Atlas.us`) | ✅ |
| 1114 | Pluto TV Conspiracy | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVConspiracy.de`) | ✅ |
| 1115 | FBI Files | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`FBIFiles.us`) | ✅ |
| 1116 | Pluto TV Animaux | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVAnimaux.de`) | ✅ |
| 1117 | Ink Master | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`InkMaster.us`) | ✅ |
| 1118 | Pluto TV Aventure | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVAventure.de`) | ✅ |
| 1119 | National Geographic HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 1120 | Discovery Channel HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 1121 | Nat Geo Wild HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |

## 📺 טיולים והרפתקאות (Travel)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 1122 | ערוץ החופשות והנופש HD | ערוץ FAST ישראלי – AWS CloudFront | ✅ FAST חינמי | iptv-org (`VacationChannel.il`) | ✅ |
| 1123 | ערוץ הטיולים Travel Channel | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`TravelChannel.ru`) | ❌ רק באפליקציה (HTTP) |
| 1124 | AKC TV | broadcast.blivenyc.com | 🌐 ציבורי (iptv-org) | iptv-org (`AKCTV.us`) | ✅ |
| 1125 | Horse & Country TV | hnc-free-viewlift.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`HorseCountryTV.uk`) | ✅ |
| 1126 | Voyages & Saveurs | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`VoyagesSaveurs.es`) | ✅ |
| 1127 | Peer TV South Tyrol | iptv.peer.biz | 🌐 ציבורי (iptv-org) | iptv-org (`PeerTVSouthTyrol.it`) | ✅ |
| 1128 | Dikij | stream3.cinerama.uz | 🌐 ציבורי (iptv-org) | iptv-org (`Dikij.ru`) | ✅ |
| 1129 | Rybalka | dash2.antik.sk | 🌐 ציבורי (iptv-org) | iptv-org (`Rybalka.ua`) | ✅ |
| 1130 | Love Nature | aegis-cloudfront-1.tubi.video | 🌐 ציבורי (iptv-org) | iptv-org (`LoveNature.ca`) | ✅ |
| 1131 | China Travel | fastlive.cctvplus.com | 🌐 ציבורי (iptv-org) | iptv-org (`ChinaTravel.cn`) | ✅ |
| 1132 | Terra Viva | 9ada494d.wurl.com | 🌐 ציבורי (iptv-org) | iptv-org (`TerraViva.br`) | ✅ |
| 1133 | Ciudad Magica | lbgo.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`CiudadMagica.ar`) | ✅ |
| 1134 | INTRAVEL | amg00861-amg00861c10-rakuten-uk-3152.playouts.now.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`INTRAVEL.nl`) | ✅ |
| 1135 | Swamiji TV American | stream.swamiji.tv | 🌐 ציבורי (iptv-org) | iptv-org (`SwamijiTVAmerican.at`) | ✅ |
| 1136 | Travel Channel | 92.114.85.87 | 🌐 ציבורי (iptv-org) | iptv-org (`TravelChannel.pl`) | ❌ רק באפליקציה (HTTP) |
| 1137 | Agro TV | agrotv.streamnet.ro | 🌐 ציבורי (iptv-org) | iptv-org (`AgroTV.ro`) | ✅ |
| 1138 | El Mundo del Campo | movil.ejeserver.com | 🌐 ציבורי (iptv-org) | iptv-org (`ElMundodelCampo.co`) | ✅ |
| 1139 | Discover Pakistan | livecdn.live247stream.com | 🌐 ציבורי (iptv-org) | iptv-org (`DiscoverPakistan.pk`) | ✅ |
| 1140 | WildEarth | dqga3jatxofgx.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`WildEarth.za`) | ✅ |
| 1141 | Agrotendencia TV | 5fc584f3f19c9.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`AgrotendenciaTV.cr`) | ✅ |
| 1142 | Travel TV | streamer103.neterra.tv | 🌐 ציבורי (iptv-org) | iptv-org (`TravelTV.bg`) | ✅ |
| 1143 | Aruba.TV | cdn01.setar.aw | 🌐 ציבורי (iptv-org) | iptv-org (`ArubaTV.aw`) | ✅ |
| 1144 | OEPM TV | cnn.hostlagarto.com | 🌐 ציבורי (iptv-org) | iptv-org (`OEPMTV.do`) | ✅ |
| 1145 | Productiva TV | copacoradios.desdeparaguay.net | 🌐 ציבורי (iptv-org) | iptv-org (`ProductivaTV.py`) | ✅ |
| 1146 | Sun Channel | 168.196.127.137 | 🌐 ציבורי (iptv-org) | iptv-org (`SunChannel.ve`) | ❌ רק באפליקציה (HTTP) |
| 1147 | TV OFF | edge.enhdtv.com | 🌐 ציבורי (iptv-org) | iptv-org (`TVOFF.bo`) | ✅ |
| 1148 | ARDB TV | ardbtv.ardb.com.kh | 🌐 ציבורי (iptv-org) | iptv-org (`ARDBTV.kh`) | ✅ |
| 1149 | DroneTV | airvuz-dronetv-1-us.ohlscdn.wurl.tv | 🌐 ציבורי (iptv-org) | iptv-org (`DroneTV.us`) | ✅ |
| 1150 | Space Live powered by sen | linear-1224.frequency.stream | 🌐 ציבורי (iptv-org) | iptv-org (`SpaceLivepoweredbysen.uk`) | ✅ |
| 1151 | Dikaya okhota | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Dikayaokhota.ru`) | ❌ רק באפליקציה (HTTP) |
| 1152 | CCTV-17 | 74.91.26.218 | 🌐 ציבורי (iptv-org) | iptv-org (`CCTV17.cn`) | ❌ רק באפליקציה (HTTP) |
| 1153 | AgroCanal | aovivo.equipea.com.br | 🌐 ציבורי (iptv-org) | iptv-org (`AgroCanal.br`) | ✅ |
| 1154 | Swamiji TV Australian | stream.swamiji.tv | 🌐 ציבורי (iptv-org) | iptv-org (`SwamijiTVAustralian.at`) | ✅ |
| 1155 | Travel Mix | hls02ns.antenaplay.ro | 🌐 ציבורי (iptv-org) | iptv-org (`TravelMix.ro`) | ✅ |
| 1156 | UMC TV | streaming5.globalhostla.com | 🌐 ציבורי (iptv-org) | iptv-org (`UMCTV.co`) | ✅ |
| 1157 | Costa Rica Channel | video0.rogohosting.com | 🌐 ציבורי (iptv-org) | iptv-org (`CostaRicaChannel.cr`) | ✅ |
| 1158 | Agro TV | restr2.bgtv.bg | 🌐 ציבורי (iptv-org) | iptv-org (`AgroTV.bg`) | ✅ |
| 1159 | Duck Hunting TV | main.duckhunting.playout.vju.tv | 🌐 ציבורי (iptv-org) | iptv-org (`DuckHuntingTV.us`) | ✅ |
| 1160 | Dikaya rybalka | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Dikayarybalka.ru`) | ❌ רק באפליקציה (HTTP) |
| 1161 | Guangxi Variety & Travel Channel | 111.59.24.227 | 🌐 ציבורי (iptv-org) | iptv-org (`GuangxiVarietyTravelChannel.cn`) | ❌ רק באפליקציה (HTTP) |
| 1162 | Canal Rural | 170.83.49.66 | 🌐 ציבורי (iptv-org) | iptv-org (`CanalRural.br`) | ❌ רק באפליקציה (HTTP) |
| 1163 | Swamiji TV European | stream.swamiji.tv | 🌐 ציבורי (iptv-org) | iptv-org (`SwamijiTVEuropean.at`) | ✅ |
| 1164 | Outdoor America | d1e354daam8g5r.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`OutdoorAmerica.us`) | ✅ |
| 1165 | Moya stikhiya | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Moyastikhiya.ru`) | ❌ רק באפליקציה (HTTP) |
| 1166 | TV Parana Turismo | 200.189.113.201 | 🌐 ציבורי (iptv-org) | iptv-org (`TVParanaTurismo.br`) | ❌ רק באפליקציה (HTTP) |
| 1167 | Schladming-Dachstein TV | m317.video-stream-hosting.de | 🌐 ציבורי (iptv-org) | iptv-org (`SchladmingDachsteinTV.at`) | ✅ |
| 1168 | Pocono Television Network | dfoiz3dv1euv7.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`PoconoTelevisionNetwork.us`) | ✅ |
| 1169 | RTG TV | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`RTGTV.ru`) | ❌ רק באפליקציה (HTTP) |
| 1170 | PursuitUP | d1p1siomv3cm7b.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`PursuitUP.us`) | ✅ |
| 1171 | Rybolov | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Rybolov.ru`) | ❌ רק באפליקציה (HTTP) |
| 1172 | Beach TV Florida & Alabama | 5ed325193d4e1.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`BeachTVFloridaAlabama.us`) | ✅ |
| 1173 | Vkus | 178.134.1.158 | 🌐 ציבורי (iptv-org) | iptv-org (`Vkus.ru`) | ❌ רק באפליקציה (HTTP) |
| 1174 | Beach TV Key West & Florida Keys | 5ed325193d4e1.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`BeachTVKeyWestFloridaKeys.us`) | ✅ |

## 📺 מדע ולמידה (Science)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 1175 | ערוץ החקר והמדע (viju Explore) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`vijuExplore.ru`) | ❌ רק באפליקציה (HTTP) |
| 1176 | BUTV10 | cdnapisec.kaltura.com | 🌐 ציבורי (iptv-org) | iptv-org (`BUTV10.us`) | ✅ |
| 1177 | Al Hiwar TV | mn-nl.mncdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`AlHiwarTV.uk`) | ✅ |
| 1178 | Persiana Medical | phd2hls.persiana.live | 🌐 ציבורי (iptv-org) | iptv-org (`PersianaMedical.fr`) | ✅ |
| 1179 | NRWision | fms.nrwision.de | 🌐 ציבורי (iptv-org) | iptv-org (`NRWision.de`) | ✅ |
| 1180 | Platzi TV | mdstrm.com | 🌐 ציבורי (iptv-org) | iptv-org (`PlatziTV.es`) | ✅ |
| 1181 | Telepace 3 | 932y4273djv8-hls-live.mariatvcdn.it | 🌐 ציבורי (iptv-org) | iptv-org (`Telepace3.it`) | ✅ |
| 1182 | .sci-fi | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`scifi.ru`) | ❌ רק באפליקציה (HTTP) |
| 1183 | Ciftci TV | live.artidijitalmedya.com | 🌐 ציבורי (iptv-org) | iptv-org (`CiftciTV.tr`) | ✅ |
| 1184 | Discovery Science | drk6xq0vhn.gpcdn.net | 🌐 ציבורי (iptv-org) | iptv-org (`DiscoveryScience.in`) | ✅ |
| 1185 | OUN | live.knou.ac.kr | 🌐 ציבורי (iptv-org) | iptv-org (`OUN.kr`) | ✅ |
| 1186 | CCTV-World Geography | 38.75.136.137 | 🌐 ציבורי (iptv-org) | iptv-org (`CCTVWorldGeography.cn`) | ❌ רק באפליקציה (HTTP) |
| 1187 | TV UFOP | app.tvufop.com.br | 🌐 ציבורי (iptv-org) | iptv-org (`TVUFOP.br`) | ✅ |
| 1188 | TV BUAP | tvenvivo.buap.mx | 🌐 ציבורי (iptv-org) | iptv-org (`TVBUAP.mx`) | ✅ |
| 1189 | Canal Universidad | videostream.shockmedia.com.ar | 🌐 ציבורי (iptv-org) | iptv-org (`CanalUniversidad.ar`) | ✅ |
| 1190 | UTalca TV | unlimited1-us.dps.live | 🌐 ציבורי (iptv-org) | iptv-org (`UTalcaTV.cl`) | ✅ |
| 1191 | Eduvision | stmv3.voxtvhd.com.br | 🌐 ציבורי (iptv-org) | iptv-org (`Eduvision.co`) | ✅ |
| 1192 | The Indonesia Channel | restreamer.indonusa.id | 🌐 ציבורי (iptv-org) | iptv-org (`TheIndonesiaChannel.id`) | ✅ |
| 1193 | DLTV 1 | cdn-live.dltv.ac.th | 🌐 ציבורי (iptv-org) | iptv-org (`DLTV1.th`) | ✅ |
| 1194 | Can Tho TV 2 | live.canthotv.vn | 🌐 ציבורי (iptv-org) | iptv-org (`CanThoTV2.vn`) | ✅ |
| 1195 | Paighan TV | cdn4.mjunoon.tv | 🌐 ציבורי (iptv-org) | iptv-org (`PaighanTV.pk`) | ✅ |
| 1196 | TVK 2 | live.kh.malimarcdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`TVK2.kh`) | ✅ |
| 1197 | STVE Telebasica | streamingcws30.com | 🌐 ציבורי (iptv-org) | iptv-org (`STVETelebasica.hn`) | ✅ |
| 1198 | Cosmos TV | video.hostingcaaguazu.com | 🌐 ציבורי (iptv-org) | iptv-org (`CosmosTV.py`) | ✅ |
| 1199 | Educa TV | avm.ecuamedia.net | 🌐 ציבורי (iptv-org) | iptv-org (`EducaTV.ec`) | ✅ |
| 1200 | Radio Universitaria San Andres | edge.enhdtv.com | 🌐 ציבורי (iptv-org) | iptv-org (`RadioUniversitariaSanAndres.bo`) | ✅ |
| 1201 | Ryongnamsan Television | live.koryocdn.org | 🌐 ציבורי (iptv-org) | iptv-org (`RyongnamsanTelevision.kp`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 1202 | TV Maya | 5ca9af4645e15.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`TVMaya.gt`) | ✅ |
| 1203 | Yaslyk | alpha.tv.online.tm | 🌐 ציבורי (iptv-org) | iptv-org (`Yaslyk.tm`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 1204 | Create | create.lls.pbs.org | 🌐 ציבורי (iptv-org) | iptv-org (`Create.us`) | ✅ |
| 1205 | English Club TV | Cinerama.uz – ספק IPTV באוזבקיסטן | ⚠️ ממסר צד שלישי | iptv-org (`EnglishClubTV.uk`) | ✅ |
| 1206 | Curiosity Channel | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`CuriosityChannel.de`) | ✅ |
| 1207 | TRT EBA | tv-e-okul01.medya.trt.com.tr | 🌐 ציבורי (iptv-org) | iptv-org (`TRTEBA.tr`) | ✅ |
| 1208 | Gyandarshan | cdn.pishow.tv | 🌐 ציבורי (iptv-org) | iptv-org (`Gyandarshan.in`) | ✅ |
| 1209 | NBS | media.joycorp.co.kr | 🌐 ציבורי (iptv-org) | iptv-org (`NBS.kr`) | ✅ |
| 1210 | CCTV-10 | 74.91.26.218 | 🌐 ציבורי (iptv-org) | iptv-org (`CCTV10.cn`) | ❌ רק באפליקציה (HTTP) |
| 1211 | TVE RS | streaming.procergs.com.br | 🌐 ציבורי (iptv-org) | iptv-org (`TVERS.br`) | ✅ |
| 1212 | TV UG | 5ca3e84a76d30.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`TVUG.mx`) | ✅ |
| 1213 | Liceum TV | 193.225.32.62 | 🌐 ציבורי (iptv-org) | iptv-org (`LiceumTV.hu`) | ❌ רק באפליקציה (HTTP) |
| 1214 | TV Usach | stv4.janus.cl | 🌐 ציבורי (iptv-org) | iptv-org (`TVUsach.cl`) | ✅ |
| 1215 | Zoom | 138.121.15.230 | 🌐 ציבורי (iptv-org) | iptv-org (`Zoom.co`) | ❌ רק באפליקציה (HTTP) |
| 1216 | Rinjani TV | rinjanitv.cloud | 🌐 ציבורי (iptv-org) | iptv-org (`RinjaniTV.id`) | ✅ |
| 1217 | DLTV 10 | cdn-live.dltv.ac.th | 🌐 ציבורי (iptv-org) | iptv-org (`DLTV10.th`) | ✅ |
| 1218 | HTV4 | freem3u.xyz | 🌐 ציבורי (iptv-org) | iptv-org (`HTV4.vn`) | ✅ |
| 1219 | Wiki TV | stream.wikitv.asia | 🌐 ציבורי (iptv-org) | iptv-org (`WikiTV.kh`) | ✅ |
| 1220 | Campus TV | 5e85d90130e77.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`CampusTV.hn`) | ✅ |
| 1221 | Educanal | 190.128.214.97 | 🌐 ציבורי (iptv-org) | iptv-org (`Educanal.py`) | ❌ רק באפליקציה (HTTP) |
| 1222 | UNIANDES TV | video.compuwebecuador.com | 🌐 ציבורי (iptv-org) | iptv-org (`UNIANDESTV.ec`) | ✅ |
| 1223 | DKN | video.oct.dc.gov | 🌐 ציבורי (iptv-org) | iptv-org (`DKN.us`) | ✅ |
| 1224 | Wonder | lds-wonder-rakuten.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`Wonder.uk`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 1225 | iSTUFF | relay2.cdn.fem-net.de | 🌐 ציבורי (iptv-org) | iptv-org (`iSTUFF.de`) | ✅ |
| 1226 | Kite Victers | 932y4x26ljv8-hls-live.5centscdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`KiteVicters.in`) | ✅ |
| 1227 | EBS English | ebsonair.ebs.co.kr | 🌐 ציבורי (iptv-org) | iptv-org (`EBSEnglish.kr`) | ✅ |
| 1228 | CCTV-7 | 74.91.26.218 | 🌐 ציבורי (iptv-org) | iptv-org (`CCTV7.cn`) | ❌ רק באפליקציה (HTTP) |
| 1229 | Canal Educacao | canaleducacao-stream.ebc.com.br | 🌐 ציבורי (iptv-org) | iptv-org (`CanalEducacao.br`) | ✅ |
| 1230 | UAA TV | 5fe2654d6127d.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`UAATV.mx`) | ✅ |
| 1231 | GVision | videostream.chileservidores.com | 🌐 ציבורי (iptv-org) | iptv-org (`GVision.cl`) | ✅ |
| 1232 | DLTV 11 | cdn-live.dltv.ac.th | 🌐 ציבורי (iptv-org) | iptv-org (`DLTV11.th`) | ✅ |
| 1233 | One TV E-School Cambodia | 49.156.33.114 | 🌐 ציבורי (iptv-org) | iptv-org (`OneTVESchoolCambodia.kh`) | ❌ רק באפליקציה (HTTP) |
| 1234 | LA36 | reflect-channel36-la.cablecast.tv | 🌐 ציבורי (iptv-org) | iptv-org (`LA36.us`) | ✅ |
| 1235 | Vyas Channel | playhls.media.nic.in | 🌐 ציבורי (iptv-org) | iptv-org (`VyasChannel.in`) | ✅ |
| 1236 | EBS Plus 2 | ebsonair.ebs.co.kr | 🌐 ציבורי (iptv-org) | iptv-org (`EBSPlus2.kr`) | ✅ |
| 1237 | ISTV | stmv1.srvstm.com | 🌐 ציבורי (iptv-org) | iptv-org (`ISTV.br`) | ✅ |
| 1238 | ILCE Canal 15 Summa Saberes | live-ilce.ovp-vivaro.digital | 🌐 ציבורי (iptv-org) | iptv-org (`ILCECanal15SummaSaberes.mx`) | ✅ |
| 1239 | UMAGTV | tls-cl.cdnz.cl | 🌐 ציבורי (iptv-org) | iptv-org (`UMAGTV.cl`) | ✅ |
| 1240 | DLTV 12 | cdn-live.dltv.ac.th | 🌐 ציבורי (iptv-org) | iptv-org (`DLTV12.th`) | ✅ |
| 1241 | TUTV | livestream.telvue.com | 🌐 ציבורי (iptv-org) | iptv-org (`TUTV.us`) | ✅ |
| 1242 | EBS 1TV | ebsonair.ebs.co.kr | 🌐 ציבורי (iptv-org) | iptv-org (`EBS1TV.kr`) | ✅ |
| 1243 | Unisul TV | sitetv.brasilstream.com.br | 🌐 ציבורי (iptv-org) | iptv-org (`UnisulTV.br`) | ✅ |
| 1244 | Jalisco TV | video0.rogohosting.com | 🌐 ציבורי (iptv-org) | iptv-org (`JaliscoTV.mx`) | ✅ |
| 1245 | DLTV 13 | cdn-live.dltv.ac.th | 🌐 ציבורי (iptv-org) | iptv-org (`DLTV13.th`) | ✅ |

## 📺 לייף סטייל ובידור

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 1246 | ערוץ החיים הטובים HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`GoodLife.il`) | ❌ רק באפליקציה (HTTP) |
| 1247 | AfroLandTV | alt-al.otteravision.com | 🌐 ציבורי (iptv-org) | iptv-org (`AfroLandTV.us`) | ✅ |
| 1248 | The Graham Norton Show | amg00654-itv-amg00654c35-rakuten-gb-7598.playouts.now.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`TheGrahamNortonShow.uk`) | ✅ |
| 1249 | Monte Carlo Digital Television | fl718492-exthls-p1.flovp.com | 🌐 ציבורי (iptv-org) | iptv-org (`MonteCarloDigitalTelevision.fr`) | ✅ |
| 1250 | Autentic Travel | cb0c87cc605942ff9766a4e6744bbadc.mediatailor.us-east-1.amazonaws.com | 🌐 ציבורי (iptv-org) | iptv-org (`AutenticTravel.de`) | ✅ |
| 1251 | Hamaika Telebista | cdn3.wowza.com | 🌐 ציבורי (iptv-org) | iptv-org (`HamaikaTelebista.es`) | ✅ |
| 1252 | 7 Gold | streamcdnr6-dddfac816632463dac50b21109dc1bb3.msvdn.net | 🌐 ציבורי (iptv-org) | iptv-org (`7Gold.it`) | ✅ |
| 1253 | Ministerstvo idey | live-minidey.cdnvideo.ru | 🌐 ציבורי (iptv-org) | iptv-org (`Ministerstvoidey.ru`) | ✅ |
| 1254 | K2 | live-tv.cloud – ספק IPTV אוקראיני | ⚠️ ממסר צד שלישי | iptv-org (`K2.ua`) | ✅ |
| 1255 | EET TV | eu.streamjo.com | 🌐 ציבורי (iptv-org) | iptv-org (`EETTV.ca`) | ✅ |
| 1256 | 10 Bold | dce3793146fef017.mediapackage.us-west-2.amazonaws.com | 🌐 ציבורי (iptv-org) | iptv-org (`10Bold.au`) | ✅ |
| 1257 | Edessa TV | canli.edessatv.com | 🌐 ציבורי (iptv-org) | iptv-org (`EdessaTV.tr`) | ✅ |
| 1258 | Asianet | da86m1sqpm3o0.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`Asianet.in`) | ✅ |
| 1259 | NHK World Premium | media-tyo.hls.nhkworld.jp | 🌐 ציבורי (iptv-org) | iptv-org (`NHKWorldPremium.jp`) | ✅ |
| 1260 | NHTV | nonghyup.flive.skcdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`NHTV.kr`) | ❌ רק באפליקציה (HTTP) |
| 1261 | Lanzhou Culture & Tourism Channel | liveplus.lzr.com.cn | 🌐 ציבורי (iptv-org) | iptv-org (`LanzhouCultureTourismChannel.cn`) | ✅ |
| 1262 | Rede NGT | isaocorp.cloudecast.com | 🌐 ציבורי (iptv-org) | iptv-org (`RedeNGT.br`) | ✅ |
| 1263 | Azteca Internacional | azt-mun.otteravision.com | 🌐 ציבורי (iptv-org) | iptv-org (`AztecaInternacional.mx`) | ✅ |
| 1264 | Metro TV | streamtv12.ddns.net | 🌐 ציבורי (iptv-org) | iptv-org (`MetroTV.ar`) | ✅ |
| 1265 | TVI Reality | raw.githubusercontent.com | 🌐 ציבורי (iptv-org) | iptv-org (`TVIReality.pt`) | ✅ |
| 1266 | MaLive | live.salto.nl | 🌐 ציבורי (iptv-org) | iptv-org (`MaLive.nl`) | ✅ |
| 1267 | Carac 1 | edge13.vedge.infomaniak.com | 🌐 ציבורי (iptv-org) | iptv-org (`Carac1.ch`) | ✅ |
| 1268 | Bialogardzka Telewizja Kablowa | bsmbialogard.pl | 🌐 ציבורי (iptv-org) | iptv-org (`BialogardzkaTelewizjaKablowa.pl`) | ✅ |
| 1269 | Netmax TV | es1.crystalweb.net | 🌐 ציבורי (iptv-org) | iptv-org (`NetmaxTV.gr`) | ✅ |
| 1270 | MTV Catfish | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`MTVCatfish.se`) | ✅ |
| 1271 | Canal Motor | digicom.hls.iptvdc.com | 🌐 ציבורי (iptv-org) | iptv-org (`CanalMotor.no`) | ✅ |
| 1272 | Jim | live-fi.tvkaista.net | 🌐 ציבורי (iptv-org) | iptv-org (`Jim.fi`) | ✅ |
| 1273 | Antena Monden | stream1.antenaplay.ro | 🌐 ציבורי (iptv-org) | iptv-org (`AntenaMonden.ro`) | ✅ |
| 1274 | Suyai TV | trun.suyaitv.cl | 🌐 ציבורי (iptv-org) | iptv-org (`SuyaiTV.cl`) | ✅ |
| 1275 | RCN Mas | rcntv-rcnmas-1-us.roku.wurl.tv | 🌐 ציבורי (iptv-org) | iptv-org (`RCNMas.co`) | ✅ |
| 1276 | AsiriTV | video2.lhdserver.es | 🌐 ציבורי (iptv-org) | iptv-org (`AsiriTV.pe`) | ✅ |
| 1277 | Salira TV | live.salira.tv | 🌐 ציבורי (iptv-org) | iptv-org (`SaliraTV.id`) | ✅ |
| 1278 | Good Idea TV | stream1.ai-net.net | 🌐 ציבורי (iptv-org) | iptv-org (`GoodIdeaTV.th`) | ❌ רק באפליקציה (HTTP) |
| 1279 | ON Vie Giai Tri | freem3u.xyz | 🌐 ציבורי (iptv-org) | iptv-org (`ONVieGiaiTri.vn`) | ✅ |
| 1280 | Hum TV | g4wlkwx8l23a-hls-live.5centscdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`HumTV.pk`) | ✅ |
| 1281 | 247 Box TV | hls.247box.live | 🌐 ציבורי (iptv-org) | iptv-org (`247BoxTV.ir`) | ✅ |
| 1282 | StoryChannel TV | 136044159.r.cdnsun.net | 🌐 ציבורי (iptv-org) | iptv-org (`StoryChannelTV.ma`) | ✅ |
| 1283 | 1KZN TV | cdn.freevisiontv.co.za | 🌐 ציבורי (iptv-org) | iptv-org (`1KZNTV.za`) | ✅ |
| 1284 | Qausain TV | acasmedia3.acangroup.org | 🌐 ציבורי (iptv-org) | iptv-org (`QausainTV.ng`) | ✅ |
| 1285 | Bellavision | fox.hostlagarto.com | 🌐 ציבורי (iptv-org) | iptv-org (`Bellavision.do`) | ✅ |
| 1286 | Aguacate TV | stream.aguacatetv.com | 🌐 ציבורי (iptv-org) | iptv-org (`AguacateTV.ve`) | ✅ |
| 1287 | La Nueva Radio TV 97.7 | sv72.ecuaradiotv.net | 🌐 ציבורי (iptv-org) | iptv-org (`LaNuevaRadioTV977.ec`) | ✅ |
| 1288 | KIX | freem3u.xyz | 🌐 ציבורי (iptv-org) | iptv-org (`KIX.hk`) | ✅ |
| 1289 | ¡OPA! | 5fc584f3f19c9.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`OPA.cr`) | ✅ |
| 1290 | Kurdsat | hlspackager.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`Kurdsat.iq`) | ✅ |
| 1291 | MiTV | rds3.desdeparaguay.net | 🌐 ציבורי (iptv-org) | iptv-org (`MiTV.py`) | ✅ |
| 1292 | EDN TV | 60417ddeaf0d9.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`EDNTV.hn`) | ✅ |
| 1293 | Code Fashion TV | cdn2.invivo.bg | 🌐 ציבורי (iptv-org) | iptv-org (`CodeFashionTV.bg`) | ✅ |
| 1294 | BTV | streamfi-alphadgtl1.zettawiseroutes.com | 🌐 ציבורי (iptv-org) | iptv-org (`BTV.ug`) | ✅ |
| 1295 | Belarus-2 | ngtrk.dc.beltelecom.by | 🌐 ציבורי (iptv-org) | iptv-org (`Belarus2.by`) | ✅ |
| 1296 | ETV+ | sb.err.ee | 🌐 ציבורי (iptv-org) | iptv-org (`ETVPlus.ee`) | ✅ |
| 1297 | BIZ TV | fl.biztv.media | 🌐 ציבורי (iptv-org) | iptv-org (`BIZTV.uz`) | ✅ |
| 1298 | Imai TV | live20.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`ImaiTV.lk`) | ✅ |
| 1299 | Ning TV | livefta.malimarcdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`NingTV.la`) | ✅ |
| 1300 | Shift TV | hls.shift.tv | 🌐 ציבורי (iptv-org) | iptv-org (`ShiftTV.hr`) | ✅ |
| 1301 | KC2 | 5c46fa289c89f.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`KC2.rw`) | ✅ |
| 1302 | Med.TV | med-tv-it.fast.rakuten.tv | 🌐 ציבורי (iptv-org) | iptv-org (`MedTV.mt`) | ✅ |
| 1303 | OTV | 5dcabf026b188.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`OTV.gh`) | ✅ |
| 1304 | RTNB Divertissement | tv2-stream.rtnb.bi | 🌐 ציבורי (iptv-org) | iptv-org (`RTNBDivertissement.bi`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 1305 | Superyacht TV | sy.wns.live | 🌐 ציבורי (iptv-org) | iptv-org (`SuperyachtTV.mc`) | ✅ |
| 1306 | Trishul Broadcasting Network | live.suricloud.com | 🌐 ציבורי (iptv-org) | iptv-org (`TrishulBroadcastingNetwork.sr`) | ✅ |
| 1307 | KTV Arabe | kwtktvata.cdn.mangomolo.com | 🌐 ציבורי (iptv-org) | iptv-org (`KTVArabe.kw`) | ✅ |
| 1308 | Oman TV Cultural | partwota.cdn.mgmlcdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`OmanTVCultural.om`) | ✅ |
| 1309 | Afroculture TV | streamtv.cmediahosthosting.net | 🌐 ציבורי (iptv-org) | iptv-org (`AfrocultureTV.ci`) | ✅ |
| 1310 | D3 TV | lbgo.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`D3TV.ml`) | ✅ |
| 1311 | EVI TV | mistserver.evi-tv.com | 🌐 ציבורי (iptv-org) | iptv-org (`EVITV.cd`) | ✅ |
| 1312 | PLEX TV | connectiktv.ddns.net | 🌐 ציבורי (iptv-org) | iptv-org (`PLEXTV.cm`) | ❌ רק באפליקציה (HTTP) |
| 1313 | America TeVe | live.gideo.video | 🌐 ציבורי (iptv-org) | iptv-org (`AmericaTeVe.pr`) | ✅ |
| 1314 | TVCARiB Latino | cdn.mycloudstream.io | 🌐 ציבורי (iptv-org) | iptv-org (`TVCARiBLatino.sx`) | ✅ |
| 1315 | UCL | livedelta.cdn.antel.net.uy | 🌐 ציבורי (iptv-org) | iptv-org (`UCL.uy`) | ✅ |
| 1316 | Abai TV | abaitv-stream.qazcdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`AbaiTV.kz`) | ✅ |
| 1317 | ANT1 Cyprus | l2.cloudskep.com | 🌐 ציבורי (iptv-org) | iptv-org (`ANT1Cyprus.cy`) | ❌ רק באפליקציה (HTTP) |
| 1318 | Bonaire.tv | media.streambrothers.com | 🌐 ציבורי (iptv-org) | iptv-org (`Bonairetv.bq`) | ✅ |
| 1319 | Bonce TV | seswa.bonce.tv | 🌐 ציבורי (iptv-org) | iptv-org (`BonceTV.cw`) | ✅ |
| 1320 | eldo.TV | stream.eldo.lu | 🌐 ציבורי (iptv-org) | iptv-org (`eldoTV.lu`) | ✅ |
| 1321 | Lietuvos Rytas TV | live.lietuvosryto.tv | 🌐 ציבורי (iptv-org) | iptv-org (`LietuvosRytasTV.lt`) | ✅ |
| 1322 | Maasranga TV | tvsen5.aynaott.com | 🌐 ציבורי (iptv-org) | iptv-org (`MaasrangaTV.bd`) | ✅ |
| 1323 | Miras | alpha.tv.online.tm | 🌐 ציבורי (iptv-org) | iptv-org (`Miras.tm`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 1324 | Nos Isla TV | cdn01.setar.aw | 🌐 ציבורי (iptv-org) | iptv-org (`NosIslaTV.aw`) | ✅ |
| 1325 | Ray TV HD | streaming.tvnepal.com | 🌐 ציבורי (iptv-org) | iptv-org (`RayTVHD.np`) | ✅ |
| 1326 | RTB 3 | edge13.vedge.infomaniak.com | 🌐 ציבורי (iptv-org) | iptv-org (`RTB3.bf`) | ✅ |
| 1327 | TBC2 | stream-134630.castr.net | 🌐 ציבורי (iptv-org) | iptv-org (`TBC2.tz`) | ✅ |
| 1328 | TV Vita | Antik Telecom (סלובקיה), נתיבי test | ⚠️ ממסר צד שלישי | iptv-org (`TVVita.sk`) | ✅ |
| 1329 | TVBS-Asia | 38.64.72.148 | 🌐 ציבורי (iptv-org) | iptv-org (`TVBSAsia.tw`) | ❌ רק באפליקציה (HTTP) |
| 1330 | AMG TV | 2-fss-2.streamhoster.com | 🌐 ציבורי (iptv-org) | iptv-org (`AMGTV.us`) | ✅ |
| 1331 | Hobby Maker | lo3.gemporia.com | 🌐 ציבורי (iptv-org) | iptv-org (`HobbyMaker.uk`) | ✅ |
| 1332 | FashionTV Paris L'Original | edge-fast3.evrideo.tv | 🌐 ציבורי (iptv-org) | iptv-org (`FashionTVParisLOriginal.fr`) | ✅ |
| 1333 | Motorvision | mvg-mv-xumo.otteravision.com | 🌐 ציבורי (iptv-org) | iptv-org (`Motorvision.de`) | ✅ |
| 1334 | Historia | d1k3vzh2ivy22k.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`Historia.es`) | ✅ |
| 1335 | Discovery Channel | streaming.aurora.enhanced.live | 🌐 ציבורי (iptv-org) | iptv-org (`DiscoveryChannel.it`) | ✅ |
| 1336 | Russia-K | Smotrim (VGTRK) – הבעלים של קרוסלה | ✅ רשמי | iptv-org (`RussiaK.ru`) | ✅ |
| 1337 | 36.6 TV | dash2.antik.sk | 🌐 ציבורי (iptv-org) | iptv-org (`366TV.ua`) | ✅ |
| 1338 | ZenLIFE by Stingray | lotus.stingray.com | 🌐 ציבורי (iptv-org) | iptv-org (`ZenLIFEbyStingray.ca`) | ✅ |
| 1339 | Fortuna TV | edge.taksimbilisim.com | 🌐 ציבורי (iptv-org) | iptv-org (`FortunaTV.tr`) | ✅ |
| 1340 | Colors Kannada | da86m1sqpm3o0.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`ColorsKannada.in`) | ✅ |
| 1341 | CCTV-Health | 38.75.136.137 | 🌐 ציבורי (iptv-org) | iptv-org (`CCTVHealth.cn`) | ❌ רק באפליקציה (HTTP) |
| 1342 | Conecta Mais TV | adylnet.streampool.net | 🌐 ציבורי (iptv-org) | iptv-org (`ConectaMaisTV.br`) | ✅ |
| 1343 | Capital 21 | video.cdmx.gob.mx | 🌐 ציבורי (iptv-org) | iptv-org (`Capital21.mx`) | ✅ |
| 1344 | Power Max Radio TV | videostream.shockmedia.com.ar | 🌐 ציבורי (iptv-org) | iptv-org (`PowerMaxRadioTV.ar`) | ✅ |
| 1345 | SBS6 Classics | sbs6classic.prd1.talpatvcdn.nl | 🌐 ציבורי (iptv-org) | iptv-org (`SBS6Classics.nl`) | ✅ |
| 1346 | Carac 2 | onefmhd.vedge.infomaniak.com | 🌐 ציבורי (iptv-org) | iptv-org (`Carac2.ch`) | ✅ |
| 1347 | Radiowa Czworka | stream14.polskieradio.pl | 🌐 ציבורי (iptv-org) | iptv-org (`RadiowaCzworka.pl`) | ✅ |
| 1348 | Star Channel International | livestar.siliconweb.com | 🌐 ציבורי (iptv-org) | iptv-org (`StarChannelInternational.gr`) | ✅ |
| 1349 | MTV Dating | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`MTVDating.se`) | ✅ |
| 1350 | Liv | live-fi.tvkaista.net | 🌐 ציבורי (iptv-org) | iptv-org (`Liv.fi`) | ✅ |
| 1351 | TVR Cultural | tvr-cultural.lg.mncdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`TVRCultural.ro`) | ✅ |
| 1352 | Canal Chilote | videostream.chileservidores.com | 🌐 ציבורי (iptv-org) | iptv-org (`CanalChilote.cl`) | ✅ |
| 1353 | RCN Novelas | cdnlive.klicgo.net | 🌐 ציבורי (iptv-org) | iptv-org (`RCNNovelas.co`) | ✅ |
| 1354 | ON Life | freem3u.xyz | 🌐 ציבורי (iptv-org) | iptv-org (`ONLife.vn`) | ✅ |
| 1355 | A-Plus TV | cdn4.mjunoon.tv | 🌐 ציבורי (iptv-org) | iptv-org (`APlusTV.pk`) | ✅ |
| 1356 | OXIR TV | hls.oxir.live | 🌐 ציבורי (iptv-org) | iptv-org (`OXIRTV.ir`) | ✅ |
| 1357 | Soweto TV | cdn.freevisiontv.co.za | 🌐 ציבורי (iptv-org) | iptv-org (`SowetoTV.za`) | ✅ |
| 1358 | Superscreen TV | video1.getstreamhosting.com | 🌐 ציבורי (iptv-org) | iptv-org (`SuperscreenTV.ng`) | ✅ |
| 1359 | Canal 17 RTVD | cdn.protvradiostream.com | 🌐 ציבורי (iptv-org) | iptv-org (`Canal17RTVD.do`) | ✅ |
| 1360 | Inter TV | streamtv.intervenhosting.net | 🌐 ציבורי (iptv-org) | iptv-org (`InterTV.ve`) | ✅ |
| 1361 | Ciracu TV | cloud37.ecuatel.com | 🌐 ציבורי (iptv-org) | iptv-org (`CiracuTV.ec`) | ✅ |
| 1362 | Asian Drama | 103.172.187.30 | 🌐 ציבורי (iptv-org) | iptv-org (`AsianDrama.hk`) | ❌ רק באפליקציה (HTTP) |
| 1363 | TeleMas | acceso.mediosdecostarica.com | 🌐 ציבורי (iptv-org) | iptv-org (`TeleMas.cr`) | ✅ |
| 1364 | EmanTv | avr.host247.net | 🌐 ציבורי (iptv-org) | iptv-org (`EmanTv.iq`) | ✅ |
| 1365 | Oasis TV | video.hostingcaaguazu.com | 🌐 ציבורי (iptv-org) | iptv-org (`OasisTV.py`) | ✅ |
| 1366 | Girasol TV | s.emisoras.tv | 🌐 ציבורי (iptv-org) | iptv-org (`GirasolTV.hn`) | ✅ |
| 1367 | Code Health TV | cdn2.invivo.bg | 🌐 ציבורי (iptv-org) | iptv-org (`CodeHealthTV.bg`) | ✅ |
| 1368 | FORT TV | fort.co-works.org | 🌐 ציבורי (iptv-org) | iptv-org (`FORTTV.ug`) | ✅ |
| 1369 | Belarus-3 | ngtrk.dc.beltelecom.by | 🌐 ציבורי (iptv-org) | iptv-org (`Belarus3.by`) | ✅ |
| 1370 | NW Magazine | hls.newworldtv.com | 🌐 ציבורי (iptv-org) | iptv-org (`NWMagazine.tg`) | ✅ |
| 1371 | Cheksiz TV | fl.biztv.media | 🌐 ציבורי (iptv-org) | iptv-org (`CheksizTV.uz`) | ✅ |
| 1372 | Hiru TV | tv.hiruhost.com | 🌐 ציבורי (iptv-org) | iptv-org (`HiruTV.lk`) | ✅ |
| 1373 | Vati Lao | livefta.malimarcdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`VatiLao.la`) | ✅ |
| 1374 | TV Nova | stream.agatin.hr | 🌐 ציבורי (iptv-org) | iptv-org (`TVNova.hr`) | ✅ |
| 1375 | AsianCrush | linear-900.frequency.stream | 🌐 ציבורי (iptv-org) | iptv-org (`AsianCrush.us`) | ✅ |
| 1376 | Top Gear | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`TopGear.uk`) | ✅ |

## 📺 מסורת וקהילה

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 1377 | ערוץ הידברות HD | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | iptv-org (`Hidabroot.il`) | ✅ |
| 1378 | הלא TV HD (Hala TV) | פאנט – הלא TV (רשמי) | ✅ רשמי | iptv-org (`HalaTV.il`) | ✅ |
| 1379 | ערוץ קבלה לעם | קבלה לעם – שרת רשמי | ✅ רשמי | iptv-org (`KabbalahforthePeopleIsrael.il`) | ✅ |
| 1380 | שלנו TV HD (Shelanu TV) | שלנו TV – CDN77 | ✅ רשמי | iptv-org (`ShelanuTV.il`) | ✅ |
| 1381 | ערוץ 16 ישראל | ערוץ 16 – שרת רשמי | ✅ רשמי | iptv-org (`Channel16.il`) | ✅ |
| 1382 | JLTV טלוויזיה יהודית | jlt-jltv-xumo.otteravision.com | ⚠️ ממסר צד שלישי | iptv-org (`JewishLifeTelevision.us`) | ✅ |
| 1383 | קבלה לעם – רוסית | קבלה לעם – השרת של הערוץ | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |

## 📺 ערוצים בינלאומיים (World)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 1384 | DW News English HD | DW – Akamai (רשמי) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 1385 | Al Jazeera English HD | אל-ג'זירה – שרת רשמי | ✅ רשמי | iptv-org (`AlJazeera.qa`) | ✅ |
| 1386 | BFM TV HD (France) | BFM – SFR (רשמי) | ✅ רשמי | iptv-org (`BFMTV.fr`) | ✅ |
| 1387 | BFM Tech & Co HD | BFM – SFR (רשמי) | ✅ רשמי | iptv-org (`BFMTechCo.fr`) | ✅ |
| 1388 | ABC | amg01942-amg01942c6-stirr-us-10178.playouts.now.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`ABC.us`) | ✅ |
| 1389 | Brit Asia TV | hls.autopo.st | 🌐 ציבורי (iptv-org) | iptv-org (`BritAsiaTV.uk`) | ✅ |
| 1390 | 20 Minutes TV | live-20minutestv.digiteka.com | 🌐 ציבורי (iptv-org) | iptv-org (`20MinutesTV.fr`) | ✅ |
| 1391 | Alex Berlin | alex-stream.rosebud-media.de | 🌐 ציבורי (iptv-org) | iptv-org (`AlexBerlin.de`) | ✅ |
| 1392 | Arabi TV | streamtv2.elitecomunicacion.cloud | 🌐 ציבורי (iptv-org) | iptv-org (`ArabiTV.es`) | ✅ |
| 1393 | A2i TV | stream.sen-gt.com | 🌐 ציבורי (iptv-org) | iptv-org (`A2iTV.it`) | ✅ |
| 1394 | Insayt TV | Cinerama.uz – ספק IPTV באוזבקיסטן | ⚠️ ממסר צד שלישי | iptv-org (`InsaytTV.ru`) | ✅ |
| 1395 | 1+1 Marafon | dash2.antik.sk | 🌐 ציבורי (iptv-org) | iptv-org (`1Plus1Marafon.ua`) | ✅ |
| 1396 | Wild TV | dfhsahpa45kk2.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`WildTV.ca`) | ✅ |
| 1397 | ABC TV | c.mjh.nz | 🌐 ציבורי (iptv-org) | iptv-org (`ABCTV.au`) | ✅ |
| 1398 | Ajman TV | cdn1.logichost.in | 🌐 ציבורי (iptv-org) | iptv-org (`AjmanTV.ae`) | ✅ |
| 1399 | Al Rayyan Old TV | alrayyancdn.vidgyor.com | 🌐 ציבורי (iptv-org) | iptv-org (`AlRayyanOldTV.qa`) | ✅ |
| 1400 | Al Riyadh Radio | live.kwikmotion.com | 🌐 ציבורי (iptv-org) | iptv-org (`AlRiyadhRadio.sa`) | ✅ |
| 1401 | Watan TV | rp.tactivemedia.com | 🌐 ציבורי (iptv-org) | iptv-org (`WatanTV.eg`) | ✅ |
| 1402 | Lana TV | cdn.streamlane.tv | 🌐 ציבורי (iptv-org) | iptv-org (`LanaTV.lb`) | ✅ |
| 1403 | Elsharq TV | mn-nl.mncdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`ElsharqTV.tr`) | ✅ |
| 1404 | DD India | mumbai-edge.smartplaytv.in | 🌐 ציבורי (iptv-org) | iptv-org (`DDIndia.in`) | ✅ |
| 1405 | HLAT-DTV | 5c3639aa99149.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`HLATDTV.kr`) | ✅ |
| 1406 | Hebei TV | event.pull.hebtv.com | 🌐 ציבורי (iptv-org) | iptv-org (`HebeiTV.cn`) | ✅ |
| 1407 | Amazon Sat | amazonsat.brasilstream.com.br | 🌐 ציבורי (iptv-org) | iptv-org (`AmazonSat.br`) | ✅ |
| 1408 | 8NTV | 60417ddeaf0d9.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`8NTV.mx`) | ✅ |
| 1409 | Canal 12 Venado Tuerto | nd106.republicaservers.com | 🌐 ציבורי (iptv-org) | iptv-org (`Canal12VenadoTuerto.ar`) | ✅ |
| 1410 | SIC Alta Definicao | production-fast-sic.content.okast.tv | 🌐 ציבורי (iptv-org) | iptv-org (`SICAltaDefinicao.pt`) | ✅ |
| 1411 | TV Rain | wl.tvrain.tv | 🌐 ציבורי (iptv-org) | iptv-org (`TVRain.nl`) | ✅ |
| 1412 | Matele | live.matele.be | 🌐 ציבורי (iptv-org) | iptv-org (`Matele.be`) | ✅ |
| 1413 | Max TV | cdn.katapy.io | 🌐 ציבורי (iptv-org) | iptv-org (`MaxTV.ch`) | ✅ |
| 1414 | Landle TV | streaming13.huberwebmedia.at | 🌐 ציבורי (iptv-org) | iptv-org (`LandleTV.at`) | ✅ |
| 1415 | NewTV | 1409502578.rsc.cdn77.org | 🌐 ציבורי (iptv-org) | iptv-org (`NewTV.pl`) | ✅ |
| 1416 | Alpha TV | alphatvlive2.siliconweb.com | 🌐 ציבורי (iptv-org) | iptv-org (`AlphaTV.gr`) | ✅ |
| 1417 | MTV Reality | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`MTVReality.se`) | ✅ |
| 1418 | Frikanalen | frikanalen.no | 🌐 ציבורי (iptv-org) | iptv-org (`Frikanalen.no`) | ✅ |
| 1419 | DRTV | drlivedrtvahls.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`DRTV.dk`) | ✅ |
| 1420 | MTV Sub | live-fi.tvkaista.net | 🌐 ציבורי (iptv-org) | iptv-org (`MTVSub.fi`) | ✅ |
| 1421 | TG4 | fastly.live.brightcove.com | 🌐 ציבורי (iptv-org) | iptv-org (`TG4.ie`) | ✅ |
| 1422 | Barrandov Krimi | dash4.antik.sk | 🌐 ציבורי (iptv-org) | iptv-org (`BarrandovKrimi.cz`) | ✅ |
| 1423 | A7TV | a7tvlive.ro | 🌐 ציבורי (iptv-org) | iptv-org (`A7TV.ro`) | ✅ |
| 1424 | Hevizi TV | cloudfront44.lexanetwork.com | 🌐 ציבורי (iptv-org) | iptv-org (`HeviziTV.hu`) | ✅ |
| 1425 | Antofagasta TV | unlimited6-cl.dps.live | 🌐 ציבורי (iptv-org) | iptv-org (`AntofagastaTV.cl`) | ✅ |
| 1426 | Aupur Television | canal.aupurtv.co | 🌐 ציבורי (iptv-org) | iptv-org (`AupurTelevision.co`) | ✅ |
| 1427 | Latina | redirector.rudo.video | 🌐 ציבורי (iptv-org) | iptv-org (`Latina.pe`) | ✅ |
| 1428 | TVRI World | ott-balancer.tvri.go.id | 🌐 ציבורי (iptv-org) | iptv-org (`TVRIWorld.id`) | ✅ |
| 1429 | NBT World | cdn-edge.iiptvcdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`NBTWorld.th`) | ✅ |
| 1430 | Vietnam Today | amagi-live.ondemandkorea.com | 🌐 ציבורי (iptv-org) | iptv-org (`VietnamToday.vn`) | ✅ |
| 1431 | PMI TV | cdn.live247stream.com | 🌐 ציבורי (iptv-org) | iptv-org (`PMITV.pk`) | ✅ |
| 1432 | Asil TV | live.asil.tv | 🌐 ציבורי (iptv-org) | iptv-org (`AsilTV.ir`) | ✅ |
| 1433 | 2M Monde | d2qh3gh0k5vp3v.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`2MMonde.ma`) | ✅ |
| 1434 | TV BRICS Africa | cdn.freevisiontv.co.za | 🌐 ציבורי (iptv-org) | iptv-org (`TVBRICSAfrica.za`) | ✅ |
| 1435 | TV-WEST | tv-west.lg.mncdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`TVWEST.ng`) | ✅ |
| 1436 | A7 TV | 183.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`A7TV.do`) | ✅ |
| 1437 | Dmoda Channel | master.tucableip.com | 🌐 ציבורי (iptv-org) | iptv-org (`DmodaChannel.bo`) | ✅ |
| 1438 | Paraguay TV | stream.paraguaytv.gov.py | 🌐 ציבורי (iptv-org) | iptv-org (`ParaguayTV.py`) | ✅ |
| 1439 | MTR | cdnsk003.panaccess.com | 🌐 ציבורי (iptv-org) | iptv-org (`MTR.sk`) | ✅ |
| 1440 | Latina TV | streamtv.latinamedios.com | 🌐 ציבורי (iptv-org) | iptv-org (`LatinaTV.ve`) | ✅ |
| 1441 | America Estereo Quito | video.makrodigital.com | 🌐 ציבורי (iptv-org) | iptv-org (`AmericaEstereoQuito.ec`) | ✅ |
| 1442 | TV Sur Canal 14 | k20.usastreams.com | 🌐 ציבורי (iptv-org) | iptv-org (`TVSurCanal14.cr`) | ✅ |
| 1443 | Canal 100 Chinique | live20.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`Canal100Chinique.gt`) | ✅ |
| 1444 | Al Rasheed TV | media1.livaat.com | 🌐 ציבורי (iptv-org) | iptv-org (`AlRasheedTV.iq`) | ✅ |
| 1445 | Canal 11 | redirector.rudo.video | 🌐 ציבורי (iptv-org) | iptv-org (`Canal11.hn`) | ✅ |
| 1446 | Jordan Archive | playlist.fasttvcdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`JordanArchive.jo`) | ✅ |
| 1447 | Belarus-1 | ngtrk.dc.beltelecom.by | 🌐 ציבורי (iptv-org) | iptv-org (`Belarus1.by`) | ✅ |
| 1448 | Rajeen TV | mn-nl.mncdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`RajeenTV.ps`) | ✅ |
| 1449 | TSV | tsv.md | 🌐 ציבורי (iptv-org) | iptv-org (`TSV.md`) | ✅ |
| 1450 | Jibek Joly | stream.qazcdn.net | 🌐 ציבורי (iptv-org) | iptv-org (`JibekJoly.kz`) | ✅ |
| 1451 | Louga TV | stream.sen-gt.com | 🌐 ציבורי (iptv-org) | iptv-org (`LougaTV.sn`) | ✅ |
| 1452 | TeleOnce | live.11-cdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`TeleOnce.pr`) | ✅ |
| 1453 | Sat 7 Turk | live.artidijitalmedya.com | 🌐 ציבורי (iptv-org) | iptv-org (`Sat7Turk.cy`) | ✅ |
| 1454 | Rajdhani TV | stream.shariarsuvo.com | 🌐 ציבורי (iptv-org) | iptv-org (`RajdhaniTV.bd`) | ✅ |
| 1455 | Nasa TV | stream.nasatv.com.mk | 🌐 ציבורי (iptv-org) | iptv-org (`NasaTV.mk`) | ✅ |
| 1456 | Pannon TV | stream.nmih.hu | 🌐 ציבורי (iptv-org) | iptv-org (`PannonTV.rs`) | ✅ |
| 1457 | RTK 3 | gjirafa-video-live.gjirafa.net | 🌐 ציבורי (iptv-org) | iptv-org (`RTK3.xk`) | ✅ |
| 1458 | TV10 | 5c46fa289c89f.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`TV10.rw`) | ✅ |
| 1459 | Imedi TV | tv.cdn.xsg.ge | 🌐 ציבורי (iptv-org) | iptv-org (`ImediTV.ge`) | ✅ |
| 1460 | Turkmenistan | alpha.tv.online.tm | 🌐 ציבורי (iptv-org) | iptv-org (`Turkmenistan.tm`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 1461 | CAM 10 TV | stream.it-innov.com | 🌐 ציבורי (iptv-org) | iptv-org (`CAM10TV.cm`) | ✅ |
| 1462 | RTL Zwee | live-edge.rtl.lu | 🌐 ציבורי (iptv-org) | iptv-org (`RTLZwee.lu`) | ✅ |
| 1463 | Verbum TV | video-eu1.streamerr.co | 🌐 ציבורי (iptv-org) | iptv-org (`VerbumTV.lk`) | ✅ |
| 1464 | Vizion Plus | tringliveviz.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`VizionPlus.al`) | ✅ |
| 1465 | Kordia TV | ptvlive.kordia.net.nz | 🌐 ציבורי (iptv-org) | iptv-org (`KordiaTV.nz`) | ✅ |
| 1466 | MNB World | live.mnb.mn | 🌐 ציבורי (iptv-org) | iptv-org (`MNBWorld.mn`) | ✅ |
| 1467 | Television Caribbean | live.kingdom-tv.com | 🌐 ציבורי (iptv-org) | iptv-org (`TelevisionCaribbean.kn`) | ✅ |
| 1468 | Althania | live.kwikmotion.com | 🌐 ציבורי (iptv-org) | iptv-org (`Althania.sy`) | ✅ |
| 1469 | Ayaz TV | janya-ayaztv.vgcdn.net | 🌐 ציבורי (iptv-org) | iptv-org (`AyazTV.az`) | ✅ |
| 1470 | Armenia 2 | amtv.tulixcdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`Armenia2.am`) | ✅ |
| 1471 | SEA TV | seatv.netlinkbroadcaster.com | 🌐 ציבורי (iptv-org) | iptv-org (`SEATV.kh`) | ✅ |
| 1472 | Tamadon TV | hls.tamadon.live | 🌐 ציבורי (iptv-org) | iptv-org (`TamadonTV.af`) | ✅ |
| 1473 | Lao Net TV | livefta.malimarcdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`LaoNetTV.la`) | ✅ |
| 1474 | LTV1 | lsm.eu.cdns-redge.media | 🌐 ציבורי (iptv-org) | iptv-org (`LTV1.lv`) | ✅ |
| 1475 | Radio Televizija Banovina | pool.alter-media.hr | 🌐 ציבורי (iptv-org) | iptv-org (`RadioTelevizijaBanovina.hr`) | ✅ |
| 1476 | RTV Zenica | stream.rtvze.ba | 🌐 ציבורי (iptv-org) | iptv-org (`RTVZenica.ba`) | ✅ |
| 1477 | TVCG MNE | rtcg-live-open.morescreens.com | 🌐 ציבורי (iptv-org) | iptv-org (`TVCGMNE.me`) | ✅ |
| 1478 | Bahrain International | 5c7b683162943.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`BahrainInternational.bh`) | ✅ |
| 1479 | Ignite Television | rpn.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`IgniteTelevision.gy`) | ✅ |
| 1480 | Supreme Master TV | lbs-us1.suprememastertv.com | 🌐 ציבורי (iptv-org) | iptv-org (`SupremeMasterTV.tw`) | ✅ |
| 1481 | Radio Tele Planet Compas | 5dcab9aed5331.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`RadioTelePlanetCompas.ht`) | ✅ |
| 1482 | Tanasuh TV | kilolink.pulsarstarsolutions.com | 🌐 ציבורי (iptv-org) | iptv-org (`TanasuhTV.ly`) | ✅ |
| 1483 | ETV | edge12.vedge.infomaniak.com | 🌐 ציבורי (iptv-org) | iptv-org (`ETV.gp`) | ✅ |
| 1484 | NTV | strhlslb01.streamakaci.tv | 🌐 ציבורי (iptv-org) | iptv-org (`NTV.ci`) | ✅ |
| 1485 | TN8 | streamingcws30.com | 🌐 ציבורי (iptv-org) | iptv-org (`TN8.ni`) | ✅ |
| 1486 | Nex TV Canal 21 | movil.ejeserver.com | 🌐 ציבורי (iptv-org) | iptv-org (`NexTVCanal21.pa`) | ✅ |
| 1487 | RUV | ruv-web-live.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`RUV.is`) | ✅ |
| 1488 | TV Spravedliva Bulgariya | tv.tvsb.bg | 🌐 ציבורי (iptv-org) | iptv-org (`TVSpravedlivaBulgariya.bg`) | ✅ |
| 1489 | CBN TV | 5790d294af2dc.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`CBNTV.vg`) | ✅ |
| 1490 | Inooro TV | live-onesignal.royalmedia.co.ke | 🌐 ציבורי (iptv-org) | iptv-org (`InooroTV.ke`) | ✅ |
| 1491 | Pointe TV | tv.pointville.ag | 🌐 ציבורי (iptv-org) | iptv-org (`PointeTV.ag`) | ✅ |
| 1492 | Raajje TV | raajjetv.yarr-myn.workers.dev | 🌐 ציבורי (iptv-org) | iptv-org (`RaajjeTV.mv`) | ✅ |
| 1493 | TVJ | vod2live.univtec.com | 🌐 ציבורי (iptv-org) | iptv-org (`TVJ.jm`) | ✅ |
| 1494 | ZNS-TV | zns-edge1.streamcomedia.cloud | 🌐 ציבורי (iptv-org) | iptv-org (`ZNSTV.bs`) | ✅ |
| 1495 | El Watania 1 | sw1.tanitweb.net | 🌐 ציבורי (iptv-org) | iptv-org (`ElWatania1.tn`) | ✅ |
| 1496 | ERi-TV 1 | jmc-live.ercdn.net | 🌐 ציבורי (iptv-org) | iptv-org (`ERiTV1.er`) | ✅ |
| 1497 | KTV 1 | kwtktv1ta.cdn.mangomolo.com | 🌐 ציבורי (iptv-org) | iptv-org (`KTV1.kw`) | ✅ |
| 1498 | Oman TV | partneta.cdn.mgmlcdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`OmanTV.om`) | ✅ |
| 1499 | RASD TV | cloud.streamingconnect.tv | 🌐 ציבורי (iptv-org) | iptv-org (`RASDTV.eh`) | ✅ |
| 1500 | Sudan TV | cdn-globecast.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`SudanTV.sd`) | ✅ |
| 1501 | TV2 | 185.9.2.18 | 🌐 ציבורי (iptv-org) | iptv-org (`TV2.dz`) | ❌ רק באפליקציה (HTTP) |
| 1502 | Yemen Today TV | video.yementdy.tv | 🌐 ציבורי (iptv-org) | iptv-org (`YemenTodayTV.ye`) | ✅ |
| 1503 | Kalac TV | edge13.vedge.infomaniak.com | 🌐 ציבורי (iptv-org) | iptv-org (`KalacTV.gn`) | ✅ |
| 1504 | RTB | edge12.vedge.infomaniak.com | 🌐 ציבורי (iptv-org) | iptv-org (`RTB.bf`) | ✅ |
| 1505 | RTNB TV | tv1-stream.rtnb.bi | 🌐 ציבורי (iptv-org) | iptv-org (`RTNBTV.bi`) | ✅ |
| 1506 | Tele Peyi | edge20.vedge.infomaniak.com | 🌐 ציבורי (iptv-org) | iptv-org (`TelePeyi.gf`) | ✅ |
| 1507 | Tele Tchad | strhlslb01.streamakaci.tv | 🌐 ציבורי (iptv-org) | iptv-org (`TeleTchad.td`) | ✅ |
| 1508 | Television Espoir 47 | cdn140m.panaccess.com | 🌐 ציבורי (iptv-org) | iptv-org (`TelevisionEspoir47.tg`) | ✅ |
| 1509 | TV Monaco | production-fast-mcrtv.content.okast.tv | 🌐 ציבורי (iptv-org) | iptv-org (`TVMonaco.mc`) | ✅ |
| 1510 | Zitata TV | live.zitata.tv | 🌐 ציבורי (iptv-org) | iptv-org (`ZitataTV.mq`) | ✅ |
| 1511 | TVM | stream.tvm.co.mz | 🌐 ציבורי (iptv-org) | iptv-org (`TVM.mz`) | ✅ |
| 1512 | Andorra TV | livesg1.rtva.hiway.media | 🌐 ציבורי (iptv-org) | iptv-org (`AndorraTV.ad`) | ✅ |
| 1513 | Dodoma TV | goliveafrica.media | 🌐 ציבורי (iptv-org) | iptv-org (`DodomaTV.tz`) | ✅ |
| 1514 | ETV | sb.err.ee | 🌐 ציבורי (iptv-org) | iptv-org (`ETV.ee`) | ✅ |
| 1515 | Golden Jade | 103.172.187.30 | 🌐 ציבורי (iptv-org) | iptv-org (`GoldenJade.hk`) | ❌ רק באפליקציה (HTTP) |
| 1516 | Korean Central Television | live.koryocdn.org | 🌐 ציבורי (iptv-org) | iptv-org (`KoreanCentralTelevision.kp`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 1517 | MWD | 203.81.84.130 | 🌐 ציבורי (iptv-org) | iptv-org (`MWD.mm`) | ❌ רק באפליקציה (HTTP) |
| 1518 | NosTV Bonaire 2 | stream2.nostvbonaire.com | 🌐 ציבורי (iptv-org) | iptv-org (`NosTVBonaire2.bq`) | ✅ |
| 1519 | One | 2-fss-1.streamhoster.com | 🌐 ציבורי (iptv-org) | iptv-org (`One.mt`) | ✅ |
| 1520 | Telearuba | cdn01.setar.aw | 🌐 ציבורי (iptv-org) | iptv-org (`Telearuba.aw`) | ✅ |
| 1521 | TV Direct 13 | oqgdrog4d4rm-hls-live.5centscdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`TVDirect13.cw`) | ✅ |
| 1522 | TV Veseljak Golica HD | radio.serv.si | 🌐 ציבורי (iptv-org) | iptv-org (`TVVeseljakGolicaHD.si`) | ✅ |
| 1523 | AFTV | livestream.telvue.com | 🌐 ציבורי (iptv-org) | iptv-org (`AFTV.us`) | ✅ |
| 1524 | Spark TV | candlelightmedia-sparklightlove-4-us.roku.wurl.tv | 🌐 ציבורי (iptv-org) | iptv-org (`SparkTV.uk`) | ✅ |
| 1525 | Cannes Lerins TV | vdo2.pro-fhi.net | 🌐 ציבורי (iptv-org) | iptv-org (`CannesLerinsTV.fr`) | ✅ |
| 1526 | Allgau TV | stream01.welocal.stream | 🌐 ציבורי (iptv-org) | iptv-org (`AllgauTV.de`) | ✅ |
| 1527 | Canal San Roque | cdnlivevlc.codev8.net | 🌐 ציבורי (iptv-org) | iptv-org (`CanalSanRoque.es`) | ✅ |

## 📺 אוכל ובישול (Food)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 1528 | bon appetit | bonappetit-samsung.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`bonappetit.us`) | ✅ |
| 1529 | Master Chefs Academy TV | platform-iuoamc.uk | 🌐 ציבורי (iptv-org) | iptv-org (`MasterChefsAcademyTV.uk`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 1530 | TV5MONDE Chefs | tvf-tv5ch.otteravision.com | 🌐 ציבורי (iptv-org) | iptv-org (`TV5MONDEChefs.fr`) | ✅ |
| 1531 | Pluto TV Cuisine | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVCuisine.de`) | ✅ |
| 1532 | Food Network | streaming.aurora.enhanced.live | 🌐 ציבורי (iptv-org) | iptv-org (`FoodNetwork.it`) | ✅ |
| 1533 | Kulinar TV | api.alpaca.t62a.com | 🌐 ציבורי (iptv-org) | iptv-org (`KulinarTV.ru`) | ✅ |
| 1534 | Gusto TV | e75fc053c0d2406fb74cdd71d77e1a7f.mediatailor.us-east-1.amazonaws.com | 🌐 ציבורי (iptv-org) | iptv-org (`GustoTV.ca`) | ✅ |
| 1535 | Food Food | mumt03.tangotv.in | 🌐 ציבורי (iptv-org) | iptv-org (`FoodFood.in`) | ✅ |
| 1536 | Boligkjop i blinde | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`Boligkjopiblinde.se`) | ✅ |
| 1537 | Dens Food Channel | op-flashcon-digdayahd-1.dens.tv | 🌐 ציבורי (iptv-org) | iptv-org (`DensFoodChannel.id`) | ✅ |
| 1538 | Masala TV | cdn4.mjunoon.tv | 🌐 ציבורי (iptv-org) | iptv-org (`MasalaTV.pk`) | ✅ |
| 1539 | Asian Food Network | freem3u.xyz | 🌐 ציבורי (iptv-org) | iptv-org (`AsianFoodNetwork.sg`) | ✅ |
| 1540 | Jordan Kitchen | playlist.fasttvcdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`JordanKitchen.jo`) | ✅ |
| 1541 | Chef Champion | rpn.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`ChefChampion.us`) | ✅ |
| 1542 | Pluto TV Cocina | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVCocina.de`) | ✅ |
| 1543 | City Eden Recepty Gurmana | cityeden.catcast.tv | 🌐 ציבורי (iptv-org) | iptv-org (`CityEdenReceptyGurmana.ru`) | ✅ |
| 1544 | Boligkob i blinde | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`Boligkobiblinde.se`) | ✅ |
| 1545 | Chef Roc Show | rpn.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`ChefRocShow.us`) | ✅ |
| 1546 | Pluto TV Food | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVFood.de`) | ✅ |
| 1547 | FoodTime | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`FoodTime.ru`) | ❌ רק באפליקציה (HTTP) |
| 1548 | Buying Blind Denmark | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`BuyingBlindDenmark.se`) | ✅ |
| 1549 | Gordon Ramsay's Hell's Kitchen | aegis-cloudfront-1.tubi.video | 🌐 ציבורי (iptv-org) | iptv-org (`GordonRamsaysHellsKitchen.us`) | ✅ |
| 1550 | Pluto TV Cucina | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVCucina.de`) | ✅ |
| 1551 | Koch'en pa toppen | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`Kochenpatoppen.se`) | ✅ |
| 1552 | Tastemade Brasil | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`TastemadeBrasil.us`) | ✅ |
| 1553 | MasterChef Danmarks storste madtalenter | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`MasterChefDanmarksstorstemadtalenter.se`) | ✅ |
| 1554 | Cooking Panda | stream-us-east-1.getpublica.com | 🌐 ציבורי (iptv-org) | iptv-org (`CookingPanda.us`) | ✅ |
| 1555 | Med kniven for struben | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`Medknivenforstruben.se`) | ✅ |
| 1556 | Mad Dog and Merrill | rpn.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`MadDogandMerrill.us`) | ✅ |
| 1557 | Skonne ombygninger | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`Skonneombygninger.se`) | ✅ |
| 1558 | Pluto TV KFOOD | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVKFOOD.us`) | ✅ |
| 1559 | Stugfixarna Norge | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`StugfixarnaNorge.se`) | ✅ |
| 1560 | Food Network Latin America | 168.196.127.137 | 🌐 ציבורי (iptv-org) | iptv-org (`FoodNetworkLatinAmerica.us`) | ❌ רק באפליקציה (HTTP) |
| 1561 | Til Middag Hos | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`TilMiddagHos.se`) | ✅ |
| 1562 | Cooking Channel | 23.237.104.106 | 🌐 ציבורי (iptv-org) | iptv-org (`CookingChannel.us`) | ❌ רק באפליקציה (HTTP) |
| 1563 | Food Network | 23.237.104.106 | 🌐 ציבורי (iptv-org) | iptv-org (`FoodNetwork.us`) | ❌ רק באפליקציה (HTTP) |

## 📺 דת ואמונה (Religion)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 1564 | Amazing Facts TV | hlspackager.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`AmazingFactsTV.us`) | ✅ |
| 1565 | Faith World TV | faithworldtv.abs.tv | 🌐 ציבורי (iptv-org) | iptv-org (`FaithWorldTV.uk`) | ✅ |
| 1566 | Evandis Gospel TV | evandis.streamakaci.tv | 🌐 ציבורי (iptv-org) | iptv-org (`EvandisGospelTV.fr`) | ✅ |
| 1567 | Sophia TV Francais | bild-und-ton.stream | 🌐 ציבורי (iptv-org) | iptv-org (`SophiaTVFrancais.de`) | ✅ |
| 1568 | Logos TV | streamer1.streamhost.org | 🌐 ציבורי (iptv-org) | iptv-org (`LogosTV.es`) | ✅ |
| 1569 | Padre Pio TV | 600f07e114306.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`PadrePioTV.it`) | ✅ |
| 1570 | Huzur TV | hls-mycdn08724960135.cdnvideo.ru | 🌐 ציבורי (iptv-org) | iptv-org (`HuzurTV.ru`) | ✅ |
| 1571 | EWTN Ukraine | cdn3.wowza.com | 🌐 ציבורי (iptv-org) | iptv-org (`EWTNUkraine.ua`) | ✅ |
| 1572 | 3ABN Canada | 3abn.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`3ABNCanada.ca`) | ✅ |
| 1573 | Mission Asia | livecdn.live247stream.com | 🌐 ציבורי (iptv-org) | iptv-org (`MissionAsia.au`) | ✅ |
| 1574 | Peace TV English | dzkyvlfyge.erbvr.com | 🌐 ציבורי (iptv-org) | iptv-org (`PeaceTVEnglish.ae`) | ✅ |
| 1575 | Qatar TV The Holy Quran | qatartv.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`QatarTVTheHolyQuran.qa`) | ✅ |
| 1576 | Holy Quran Radio | live.kwikmotion.com | 🌐 ציבורי (iptv-org) | iptv-org (`HolyQuranRadio.sa`) | ✅ |
| 1577 | Aghapy TV | 5b622f07944df.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`AghapyTV.eg`) | ✅ |
| 1578 | Noursat English | Asharq Discovery – ItWorks (רשמי) | ✅ רשמי | iptv-org (`NoursatEnglish.lb`) | ✅ |
| 1579 | Diyanet TV | eustr73.mediatriple.net | 🌐 ציבורי (iptv-org) | iptv-org (`DiyanetTV.tr`) | ✅ |
| 1580 | Salvation TV | ktismaservers.in | 🌐 ציבורי (iptv-org) | iptv-org (`SalvationTV.in`) | ✅ |
| 1581 | BTN TV | btn.nowcdn.co.kr | 🌐 ציבורי (iptv-org) | iptv-org (`BTNTV.kr`) | ✅ |
| 1582 | Ando TV | play.kankanlive.com | 🌐 ציבורי (iptv-org) | iptv-org (`AndoTV.cn`) | ❌ רק באפליקציה (HTTP) |
| 1583 | Rede Gospel | cdn.live.br1.jmvstream.com | 🌐 ציבורי (iptv-org) | iptv-org (`RedeGospel.br`) | ✅ |
| 1584 | Monte Maria | 5ca9af4645e15.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`MonteMaria.mx`) | ✅ |
| 1585 | TV Mana Argentina | w2.manasat.com | 🌐 ציבורי (iptv-org) | iptv-org (`TVManaArgentina.ar`) | ✅ |
| 1586 | Mana Church Online | w2.manasat.com | 🌐 ציבורי (iptv-org) | iptv-org (`ManaChurchOnline.pt`) | ✅ |
| 1587 | Immaculata TV | g5nl63z8lpq6-hls-live.5centscdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`ImmaculataTV.ch`) | ✅ |
| 1588 | KIT-TV | everest.pa-hosting.de | 🌐 ציבורי (iptv-org) | iptv-org (`KITTV.at`) | ✅ |
| 1589 | EWTN Poland | cdn3.wowza.com | 🌐 ציבורי (iptv-org) | iptv-org (`EWTNPoland.pl`) | ✅ |
| 1590 | Himlen TV7 | vod.tv7.fi | 🌐 ציבורי (iptv-org) | iptv-org (`HimlenTV7.fi`) | ✅ |
| 1591 | Alfa Omega TV | aom-live.maghost.ro | 🌐 ציבורי (iptv-org) | iptv-org (`AlfaOmegaTV.ro`) | ✅ |
| 1592 | Apostol TV | live.apostoltv.hu | 🌐 ציבורי (iptv-org) | iptv-org (`ApostolTV.hu`) | ✅ |
| 1593 | Fortaleza TV | multimedia.fortalezatv.cl | 🌐 ציבורי (iptv-org) | iptv-org (`FortalezaTV.cl`) | ✅ |

## 📻 רדיו

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 21 | כאן ב - רשת ב | Triton StreamTheWorld – מפיץ הרדיו של כאן | ✅ רשמי | radio-browser.info + בדיקת שמות הזרמים של כאן | ✅ |
| 22 | כאן גימל | Triton StreamTheWorld – מפיץ הרדיו של כאן | ✅ רשמי | radio-browser.info + בדיקת שמות הזרמים של כאן | ✅ |
| 23 | כאן 88 | Triton StreamTheWorld – מפיץ הרדיו של כאן | ✅ רשמי | radio-browser.info + בדיקת שמות הזרמים של כאן | ✅ |
| 24 | כאן תרבות | Triton StreamTheWorld – מפיץ הרדיו של כאן | ✅ רשמי | radio-browser.info + בדיקת שמות הזרמים של כאן | ✅ |
| 25 | כאן מורשת | Triton StreamTheWorld – מפיץ הרדיו של כאן | ✅ רשמי | radio-browser.info + בדיקת שמות הזרמים של כאן | ✅ |
| 26 | כאן רקע | Triton StreamTheWorld – מפיץ הרדיו של כאן | ✅ רשמי | radio-browser.info + בדיקת שמות הזרמים של כאן | ✅ |
| 27 | כאן קול המוזיקה | Triton StreamTheWorld – מפיץ הרדיו של כאן | ✅ רשמי | radio-browser.info + בדיקת שמות הזרמים של כאן | ✅ |
| 28 | גלגלצ | גלי צה"ל / גלגלצ – CDN רשמי (Bynet) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 29 | גלי צה"ל | גלי צה"ל / גלגלצ – CDN רשמי (Bynet) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 30 | גלגלצ להיטים | גלי צה"ל / גלגלצ – CDN רשמי (Bynet) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 31 | eco99fm | eco99fm – Mediacast (רשמי) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 32 | 103FM | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 33 | רדיוס 100FM | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 34 | 102FM רדיו תל אביב | 102FM – LiveCDN (רשמי) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 35 | רדיו הקצה KZ | רדיו הקצה – Mediacast (רשמי) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 36 | קול ברמה | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 37 | רדיו דרום 101.5FM | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 38 | רדיו 2000 | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 39 | קול ברמה מוזיקה (קול פליי) | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 40 | 104.5FM רדיו צפון | StreamGates – ה-CDN של התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 41 | 101FM רדיו ירושלים | StreamGates – ה-CDN של התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 42 | רדיו חיפה 107.5FM | רדיו חיפה – LiveCDN (רשמי) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 43 | 90FM אמצע הדרך | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 44 | רדיו דרום 97FM | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 45 | רדיו התחנה 101.5 | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 46 | גלי ישראל 94FM | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 47 | קול חי 93FM | קול חי – שרת התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 48 | קול חי מיוזיק | קול חי – שרת התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 49 | קול רגע 96FM | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 50 | לב המדינה 91FM | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 51 | רדיוס נוסטלגי 96.3 | StreamGates – ה-CDN של התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 52 | קול הצפון | eCast – שרת התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 53 | גלגלצ רוק | גלי צה"ל / גלגלצ – CDN רשמי (Bynet) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 54 | גלגלצ עשורים | גלי צה"ל / גלגלצ – CDN רשמי (Bynet) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 55 | גלגלצ רגוע | גלי צה"ל / גלגלצ – CDN רשמי (Bynet) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 56 | רדיוס 100% מזרחית | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 57 | רדיוס 100% יהודי | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 58 | רדיוס 100% להיטים | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 59 | רדיוס 100% שנות ה-80 | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 60 | רדיוס 100% שנות ה-90 | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 61 | רדיוס 100% ילדים | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 62 | רדיוס 100% רגוע | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 63 | א-שמס A-Shams | StreamGates – ה-CDN של התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 64 | רדיו נאס Radio Nas | StreamGates – ה-CDN של התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 65 | הרדיו הראשון 89.1FM (רוסית) | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 66 | רדיו ברסלב | רדיו ברסלב – שרת התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 67 | רדיו נהריה | רדיו נהריה – שרת התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 68 | רדיוס 100% אימון | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 69 | רדיוס 100% טופ 40 | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 70 | רדיוס 100% סרטים | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 71 | רדיוס 100% אולדיז | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 72 | רדיוס 100% רטרו | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 73 | רדיוס 100% צ'ילאאוט | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 74 | רדיוס 100% קאנטרי | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 75 | רדיוס 100% K-Pop | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 76 | רדיוס 100% גיימר | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 77 | נושמים מזרחית | www.rs.mizrahit.fm | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 78 | רדיו סהר | eCast – שרת התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 79 | Israeli Voice Radio | icecast.live | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 80 | ג'וינט רדיו בלוז רוק | jointil.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 81 | ג'וינט רדיו רגאיי | jointil.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 82 | ג'וינט רדיו טראנס | jointil.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 83 | קבלה לעם רדיו | icecast.kab.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 84 | ברסלב: תורה וזימרה | רדיו ברסלב – שרת התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 85 | Radio France Israël | stream10.vestaradio.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 86 | רדיו ספורט 5 | ספורט 5 – השידור הרשמי של RGE (Akamai) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 87 | 102FM קול הים האדום | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 88 | 99.5FM רדיו | 99.5FM – LiveCDN (רשמי) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 89 | הרדיו הטוב 106.4FM (רוסית) | StreamGates – ה-CDN של התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 90 | רדיו שירי דיכאון | רדיו שירי דיכאון – השרת של התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 91 | Radios 100FM - 100% Classic Rock | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 92 | Radios 100FM - 100% World Music | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 93 | Radios 100FM - 100% Tiktok | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 94 | Kabbalah Fr | icecast.kab.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 95 | Eli Nisman Radio | listen6.myradio24.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 96 | Ivri 6 | streaming.radio.co | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 97 | Radios 100FM - 100% Covers | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 98 | Radios 100FM - 100% Classic | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 99 | Radios 100FM - 100% Love | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 100 | Mizrahit FM Best | rs.mizrahit.fm | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 101 | Radios 100FM - 100% Britpop | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 102 | Rak Musica | play.radioking.io | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 103 | Radios 100FM - 100% Jazz | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 104 | Star 33 Radiostation | online.star33radio.com | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ✅ |
| 105 | ברסלב פוריו: הלכות ודף יומי | רדיו ברסלב – שרת התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 106 | ברסלב פוריו: קצר וקולע | רדיו ברסלב – שרת התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 107 | ברסלב פוריו: ספרי ברסלב | רדיו ברסלב – שרת התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 108 | קבלה לעם צרפתית - Kabbalah fre | icecast.kab.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 109 | Radios 100FM - 100% Selfie | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 110 | Radios 100FM - 100% Eurovision | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 111 | Radios 100FM - 100% Rock | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 112 | Radios 100FM - 100% Blues | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 113 | Radios 100FM - 100% Legends | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 114 | Radios 100FM - 100% Latin | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 115 | Radios 100FM - 100% StandUp | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 116 | AVIVmedia | 176.9.7.145 | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 117 | Radios 100FM - 100% Club | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 118 | Radios 100FM - 100% Disney | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 119 | Radios 100FM - 100% Stars (ערוץ הכוכבים) | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 120 | קבלה לעם אנגלית - Kabbalah eng | icecast.kab.tv | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |

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

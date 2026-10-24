# מקורות – מאיפה מגיע כל ערוץ

נוצר אוטומטית מ-`channels.json` על ידי `tools/make_sources.py`. סה"כ **1597** פריטים: 1489 ערוצי טלוויזיה, 108 תחנות רדיו, 0 אפליקציות/שירותים.

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

- **135 שידורים רשמיים** – מהשרתים של הגוף המשדר עצמו או של המפיץ שלו (כאן, 13, 14, i24NEWS, גלי צה"ל, תחנות הרדיו, DW, אל-ג'זירה...).
- **175 ערוצי FAST** – ערוצים חינמיים עם פרסומות שהבעלים מפיצים בעצמם (Pluto TV, BabyFirst, LEGO, Baby Shark...).
- **1173 ערוצים מהעולם** – שידורים פתוחים מהמאגר הציבורי iptv-org (ייבוא אוטומטי, `tools/import_iptv.py`): כל אחד נבדק שהוא משדר ושיש בו תמונה, ללא ערוצים שדורשים מנוי/כתובת מיוחדת. רובם מהשרתים של הערוצים עצמם, אבל הבעלים לא נבדקו אחד-אחד.
- **114 ממסרי צד שלישי** – בעיקר `stream.mcquack.net` (76 ערוצים). אלה **אינם** השרתים של הערוצים: אתר חיצוני קולט את השידור ומפיץ אותו מחדש. בהם נמצאים גם ערוצים בתשלום (HOT, yes, viju/TV1000, ערוצי ספורט) – סביר מאוד שההפצה הזו **ללא רישיון** מבעלי הזכויות, והם עלולים להיעלם בכל רגע. ההחלטה אם להשאיר אותם היא שלך; הסקריפטים לא מוסיפים ממסרים חדשים של ערוצים בתשלום.
- **שירותי המנויים** (פרטנר, סלקום, STING, yes, HOT) – אין בפרויקט שום שידור שלהם; הכרטיס רק פותח את האפליקציה/האתר הרשמי, וצפייה דורשת מנוי.

> הסיווג רשמי / FAST / ממסר נקבע לפי שם השרת שממנו מגיע השידור (למשל CDN של הגוף המשדר), ולא אושר מול כל גוף משדר בנפרד.

## לפי שרת

| שרת | מי זה | סוג | מספר |
|---|---|---|---|
| `jmp2.uk` | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | 168 |
| `stream.mcquack.net` | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | 76 |
| `gb25.streamgates.net` | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | 34 |
| `92.114.85.87` | 92.114.85.87 | 🌐 ציבורי (iptv-org) | 18 |
| `cdn.cybercdn.live` | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | 16 |
| `freem3u.xyz` | freem3u.xyz | 🌐 ציבורי (iptv-org) | 15 |
| `rpn.bozztv.com` | rpn.bozztv.com | 🌐 ציבורי (iptv-org) | 13 |
| `38.75.136.137` | 38.75.136.137 | 🌐 ציבורי (iptv-org) | 10 |
| `cityeden.catcast.tv` | cityeden.catcast.tv | 🌐 ציבורי (iptv-org) | 10 |
| `dash2.antik.sk` | dash2.antik.sk | 🌐 ציבורי (iptv-org) | 9 |
| `live.kwikmotion.com` | live.kwikmotion.com | 🌐 ציבורי (iptv-org) | 9 |
| `mako-streaming.akamaized.net` | קשת – ה-CDN של mako (עם הרשאת צפייה של mako) | ✅ רשמי | 7 |
| `dash3.antik.sk` | Antik Telecom (סלובקיה), נתיבי test | ⚠️ ממסר צד שלישי | 7 |
| `a-cdn.klowdtv.com` | a-cdn.klowdtv.com | 🌐 ציבורי (iptv-org) | 7 |
| `stream-us-east-1.getpublica.com` | stream-us-east-1.getpublica.com | 🌐 ציבורי (iptv-org) | 7 |
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
| `mdstrm.com` | mdstrm.com | 🌐 ציבורי (iptv-org) | 5 |
| `livefta.malimarcdn.com` | livefta.malimarcdn.com | 🌐 ציבורי (iptv-org) | 5 |
| `mn-nl.mncdn.com` | mn-nl.mncdn.com | 🌐 ציבורי (iptv-org) | 5 |
| `cdn.pishow.tv` | cdn.pishow.tv | 🌐 ציבורי (iptv-org) | 5 |
| `livestream.pbskids.org` | livestream.pbskids.org | 🌐 ציבורי (iptv-org) | 5 |
| `cdn-live.dltv.ac.th` | cdn-live.dltv.ac.th | 🌐 ציבורי (iptv-org) | 5 |
| `cast.breslevforyou.co.il` | רדיו ברסלב – שרת התחנה | ✅ רשמי | 5 |
| `icecast.kab.tv` | icecast.kab.tv | ⚠️ ממסר צד שלישי | 5 |
| `svs.itworkscdn.net` | Asharq Discovery – ItWorks (רשמי) | ✅ רשמי | 4 |
| `streams2.sofast.tv` | streams2.sofast.tv | 🌐 ציבורי (iptv-org) | 4 |
| `lbgo.bozztv.com` | lbgo.bozztv.com | 🌐 ציבורי (iptv-org) | 4 |
| `cdn3.wowza.com` | cdn3.wowza.com | 🌐 ציבורי (iptv-org) | 4 |
| `dash4.antik.sk` | dash4.antik.sk | 🌐 ציבורי (iptv-org) | 4 |
| `hls.newworldtv.com` | hls.newworldtv.com | 🌐 ציבורי (iptv-org) | 4 |
| `video1.getstreamhosting.com` | video1.getstreamhosting.com | 🌐 ציבורי (iptv-org) | 4 |
| `tgn.bozztv.com` | tgn.bozztv.com | 🌐 ציבורי (iptv-org) | 4 |
| `edge.enhdtv.com` | edge.enhdtv.com | 🌐 ציבורי (iptv-org) | 4 |
| `w2.manasat.com` | w2.manasat.com | 🌐 ציבורי (iptv-org) | 4 |
| `live-fi.tvkaista.net` | live-fi.tvkaista.net | 🌐 ציבורי (iptv-org) | 4 |
| `playlist.fasttvcdn.com` | playlist.fasttvcdn.com | 🌐 ציבורי (iptv-org) | 4 |
| `ebsonair.ebs.co.kr` | ebsonair.ebs.co.kr | 🌐 ציבורי (iptv-org) | 4 |
| `drk6xq0vhn.gpcdn.net` | drk6xq0vhn.gpcdn.net | 🌐 ציבורי (iptv-org) | 4 |
| `edge12.vedge.infomaniak.com` | edge12.vedge.infomaniak.com | 🌐 ציבורי (iptv-org) | 4 |
| `cdna.streamgates.net` | StreamGates – ה-CDN של התחנה | ✅ רשמי | 4 |
| `kancdn.medonecdn.net` | כאן – תאגיד השידור (CDN רשמי) | ✅ רשמי | 3 |
| `r.il.cdn-redge.media` | Redge – ה-CDN הרשמי של כאן, ערוץ 14 וכלכלה 10 | ✅ רשמי | 3 |
| `rnttwmjcin.turknet.ercdn.net` | rnttwmjcin.turknet.ercdn.net | 🌐 ציבורי (iptv-org) | 3 |
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
| `cdn15.live-tv.cloud` | live-tv.cloud – ספק IPTV אוקראיני | ⚠️ ממסר צד שלישי | 3 |
| `fastlive.cctvplus.com` | fastlive.cctvplus.com | 🌐 ציבורי (iptv-org) | 3 |
| `fl.biztv.media` | fl.biztv.media | 🌐 ציבורי (iptv-org) | 3 |
| `103.172.187.30` | 103.172.187.30 | 🌐 ציבורי (iptv-org) | 3 |
| `203.81.84.130` | 203.81.84.130 | 🌐 ציבורי (iptv-org) | 3 |
| `stream1.antenaplay.ro` | stream1.antenaplay.ro | 🌐 ציבורי (iptv-org) | 3 |
| `stmv1.srvif.com` | stmv1.srvif.com | 🌐 ציבורי (iptv-org) | 3 |
| `200.115.120.1` | 200.115.120.1 | 🌐 ציבורי (iptv-org) | 3 |
| `fs.uplink.kz` | fs.uplink.kz | 🌐 ציבורי (iptv-org) | 3 |
| `barinaslive1.boldmss.com` | barinaslive1.boldmss.com | 🌐 ציבורי (iptv-org) | 3 |
| `da86m1sqpm3o0.cloudfront.net` | da86m1sqpm3o0.cloudfront.net | 🌐 ציבורי (iptv-org) | 3 |
| `138.121.15.230` | 138.121.15.230 | 🌐 ציבורי (iptv-org) | 3 |
| `cdn4.mjunoon.tv` | cdn4.mjunoon.tv | 🌐 ציבורי (iptv-org) | 3 |
| `aegis-cloudfront-1.tubi.video` | aegis-cloudfront-1.tubi.video | 🌐 ציבורי (iptv-org) | 3 |
| `cdnlive.klicgo.net` | cdnlive.klicgo.net | 🌐 ציבורי (iptv-org) | 3 |
| `sb.err.ee` | sb.err.ee | 🌐 ציבורי (iptv-org) | 3 |
| `185.227.34.179` | 185.227.34.179 | 🌐 ציבורי (iptv-org) | 3 |
| `lotus.stingray.com` | lotus.stingray.com | 🌐 ציבורי (iptv-org) | 3 |
| `livecdn.live247stream.com` | livecdn.live247stream.com | 🌐 ציבורי (iptv-org) | 3 |
| `live2.tensila.com` | live2.tensila.com | 🌐 ציבורי (iptv-org) | 3 |
| `stream.nasatv.com.mk` | stream.nasatv.com.mk | 🌐 ציבורי (iptv-org) | 3 |
| `stream.sen-gt.com` | stream.sen-gt.com | 🌐 ציבורי (iptv-org) | 3 |
| `movil.ejeserver.com` | movil.ejeserver.com | 🌐 ציבורי (iptv-org) | 3 |
| `stream.swamiji.tv` | stream.swamiji.tv | 🌐 ציבורי (iptv-org) | 3 |
| `cdn01.setar.aw` | cdn01.setar.aw | 🌐 ציבורי (iptv-org) | 3 |
| `edge13.vedge.infomaniak.com` | edge13.vedge.infomaniak.com | 🌐 ציבורי (iptv-org) | 3 |
| `cdn.freevisiontv.co.za` | cdn.freevisiontv.co.za | 🌐 ציבורי (iptv-org) | 3 |
| `jointil.com` | jointil.com | ⚠️ ממסר צד שלישי | 3 |
| `kneset.gostreaming.tv` | ערוץ הכנסת – השידור של הכנסת (GoStreaming) | ✅ רשמי | 2 |
| `rgelive.akamaized.net` | ספורט 5 – השידור הרשמי של RGE (Akamai) | ✅ רשמי | 2 |
| `stream.4fun.tv` | 4FUN TV – שרת רשמי | ✅ רשמי | 2 |
| `585b674743bbb.streamlock.net` | 70-80 TV – Wowza Streamlock | ✅ רשמי | 2 |
| `ncdn-live-bfm.pfd.sfr.net` | BFM – SFR (רשמי) | ✅ רשמי | 2 |
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
| `streams.prd.commavservices.eu` | streams.prd.commavservices.eu | 🌐 ציבורי (iptv-org) | 2 |
| `ss2.tvrdomi.com` | ss2.tvrdomi.com | 🌐 ציבורי (iptv-org) | 2 |
| `jmc-live.ercdn.net` | jmc-live.ercdn.net | 🌐 ציבורי (iptv-org) | 2 |
| `ptvlive.kordia.net.nz` | ptvlive.kordia.net.nz | 🌐 ציבורי (iptv-org) | 2 |
| `partwota.cdn.mgmlcdn.com` | partwota.cdn.mgmlcdn.com | 🌐 ציבורי (iptv-org) | 2 |
| `gjirafa-video-live.gjirafa.net` | gjirafa-video-live.gjirafa.net | 🌐 ציבורי (iptv-org) | 2 |
| `africa24.vedge.infomaniak.com` | africa24.vedge.infomaniak.com | 🌐 ציבורי (iptv-org) | 2 |
| `directes-tv-int.3catdirectes.cat` | directes-tv-int.3catdirectes.cat | 🌐 ציבורי (iptv-org) | 2 |
| `live-vgtrksmotrim.cdnvideo.ru` | live-vgtrksmotrim.cdnvideo.ru | 🌐 ציבורי (iptv-org) | 2 |
| `citynewsregional.akamaized.net` | citynewsregional.akamaized.net | 🌐 ציבורי (iptv-org) | 2 |
| `cdn.live.br1.jmvstream.com` | cdn.live.br1.jmvstream.com | 🌐 ציבורי (iptv-org) | 2 |
| `cdn0-03837-liveedge0.dna.ip-only.net` | cdn0-03837-liveedge0.dna.ip-only.net | 🌐 ציבורי (iptv-org) | 2 |
| `live.obslivestream.com` | live.obslivestream.com | 🌐 ציבורי (iptv-org) | 2 |
| `edge.essastream.com` | edge.essastream.com | 🌐 ציבורי (iptv-org) | 2 |
| `fox.hostlagarto.com` | fox.hostlagarto.com | 🌐 ציבורי (iptv-org) | 2 |
| `d1hz1p2gea2zy4.cloudfront.net` | d1hz1p2gea2zy4.cloudfront.net | 🌐 ציבורי (iptv-org) | 2 |
| `2-fss-2.streamhoster.com` | 2-fss-2.streamhoster.com | 🌐 ציבורי (iptv-org) | 2 |
| `tvsen6.aynaott.com` | tvsen6.aynaott.com | 🌐 ציבורי (iptv-org) | 2 |
| `cdn.streamhispanatv.net` | cdn.streamhispanatv.net | 🌐 ציבורי (iptv-org) | 2 |
| `cnn.hostlagarto.com` | cnn.hostlagarto.com | 🌐 ציבורי (iptv-org) | 2 |
| `103.151.60.162` | 103.151.60.162 | 🌐 ציבורי (iptv-org) | 2 |
| `stream.qazcdn.net` | stream.qazcdn.net | 🌐 ציבורי (iptv-org) | 2 |
| `15.204.196.147` | 15.204.196.147 | 🌐 ציבורי (iptv-org) | 2 |
| `raw.githubusercontent.com` | raw.githubusercontent.com | 🌐 ציבורי (iptv-org) | 2 |
| `fast-channels.sinclairstoryline.com` | fast-channels.sinclairstoryline.com | 🌐 ציבורי (iptv-org) | 2 |
| `45.162.64.114` | 45.162.64.114 | 🌐 ציבורי (iptv-org) | 2 |
| `streaming.aurora.enhanced.live` | streaming.aurora.enhanced.live | 🌐 ציבורי (iptv-org) | 2 |
| `23.237.104.106` | 23.237.104.106 | 🌐 ציבורי (iptv-org) | 2 |
| `hls.botflix.tv` | hls.botflix.tv | 🌐 ציבורי (iptv-org) | 2 |
| `streamer1.streamhost.org` | streamer1.streamhost.org | 🌐 ציבורי (iptv-org) | 2 |
| `hls01ns.antenaplay.ro` | hls01ns.antenaplay.ro | 🌐 ציבורי (iptv-org) | 2 |
| `s.emisoras.tv` | s.emisoras.tv | 🌐 ציבורי (iptv-org) | 2 |
| `cloudvideo.servers10.com` | cloudvideo.servers10.com | 🌐 ציבורי (iptv-org) | 2 |
| `c.mjh.nz` | c.mjh.nz | 🌐 ציבורי (iptv-org) | 2 |
| `170.83.49.66` | 170.83.49.66 | 🌐 ציבורי (iptv-org) | 2 |
| `tv.wracanal10.com` | tv.wracanal10.com | 🌐 ציבורי (iptv-org) | 2 |
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
| `tls-cl.cdnz.cl` | tls-cl.cdnz.cl | 🌐 ציבורי (iptv-org) | 2 |
| `rds3.desdeparaguay.net` | rds3.desdeparaguay.net | 🌐 ציבורי (iptv-org) | 2 |
| `vdo2.pro-fhi.net` | vdo2.pro-fhi.net | 🌐 ציבורי (iptv-org) | 2 |
| `video0.rogohosting.com` | video0.rogohosting.com | 🌐 ציבורי (iptv-org) | 2 |
| `5ed325193d4e1.streamlock.net` | 5ed325193d4e1.streamlock.net | 🌐 ציבורי (iptv-org) | 2 |
| `live.artidijitalmedya.com` | live.artidijitalmedya.com | 🌐 ציבורי (iptv-org) | 2 |
| `videostream.shockmedia.com.ar` | videostream.shockmedia.com.ar | 🌐 ציבורי (iptv-org) | 2 |
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
| `strhlslb01.streamakaci.tv` | strhlslb01.streamakaci.tv | 🌐 ציבורי (iptv-org) | 2 |
| `radio.streamgates.net` | StreamGates – ה-CDN של התחנה | ✅ רשמי | 2 |
| `media2.93fm.co.il` | קול חי – שרת התחנה | ✅ רשמי | 2 |
| `live.ecast.co.il` | eCast – שרת התחנה | ✅ רשמי | 2 |
| `icecast.live` | icecast.live | ⚠️ ממסר צד שלישי | 2 |
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
| `d2dffl3588mvfk.cloudfront.net` | ערוץ FAST ישראלי – AWS CloudFront | ✅ FAST חינמי | 1 |
| `d15ds134q59udk.cloudfront.net` | ערוץ FAST ישראלי – AWS CloudFront | ✅ FAST חינמי | 1 |
| `cdn.fast.jwp.services` | BabyFirst – JW Player FAST | ✅ FAST חינמי | 1 |
| `dltiqboxjw21d.cloudfront.net` | LEGO Channel – AWS CloudFront FAST | ✅ FAST חינמי | 1 |
| `newidco-babysharktv-1-us.roku.wurl.tv` | Baby Shark TV – Wurl (Roku FAST) | ✅ FAST חינמי | 1 |
| `176.118.197.101` | שרת IP ללא שם (1HD) | ⚠️ ממסר צד שלישי | 1 |
| `amg00793-amg00793c6-xumo-us-2669.playouts.now.amagi.tv` | BBC Earth – Amagi / Xumo FAST | ✅ FAST חינמי | 1 |
| `d1yd8hohnldm33.cloudfront.net` | ערוץ FAST ישראלי – AWS CloudFront | ✅ FAST חינמי | 1 |
| `stream.panet.com` | פאנט – הלא TV (רשמי) | ✅ רשמי | 1 |
| `edge3.uk.kab.tv` | קבלה לעם – שרת רשמי | ✅ רשמי | 1 |
| `1247634592.rsc.cdn77.org` | שלנו TV – CDN77 | ✅ רשמי | 1 |
| `ch16israel-cdn.encoders.immergo.tv` | ערוץ 16 – שרת רשמי | ✅ רשמי | 1 |
| `jlt-jltv-xumo.otteravision.com` | jlt-jltv-xumo.otteravision.com | ⚠️ ממסר צד שלישי | 1 |
| `edge2.il.kab.tv` | קבלה לעם – השרת של הערוץ | ✅ רשמי | 1 |
| `dwamdstream102.akamaized.net` | DW – Akamai (רשמי) | ✅ רשמי | 1 |
| `live-hls-apps-aje-fa.getaj.net` | אל-ג'זירה – שרת רשמי | ✅ רשמי | 1 |
| `raycom-accdn-firetv.amagi.tv` | raycom-accdn-firetv.amagi.tv | 🌐 ציבורי (iptv-org) | 1 |
| `af7a8b4e.wurl.com` | af7a8b4e.wurl.com | 🌐 ציבורי (iptv-org) | 1 |
| `mts1.iptv-playoutcenter.de` | mts1.iptv-playoutcenter.de | 🌐 ציבורי (iptv-org) | 1 |
| `webstream.multistream.it` | webstream.multistream.it | 🌐 ציבורי (iptv-org) | 1 |
| `d3pnbvng3bx2nj.cloudfront.net` | d3pnbvng3bx2nj.cloudfront.net | 🌐 ציבורי (iptv-org) | 1 |
| `138.204.86.4` | 138.204.86.4 | 🌐 ציבורי (iptv-org) | 1 |
| `live-manifest.production-public.tubi.io` | live-manifest.production-public.tubi.io | 🌐 ציבורי (iptv-org) | 1 |
| `158.172.155.130` | 158.172.155.130 | 🌐 ציבורי (iptv-org) | 1 |
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
| `skynewsau-live-premium.akamaized.net` | skynewsau-live-premium.akamaized.net | 🌐 ציבורי (iptv-org) | 1 |
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
| `live1.acangroup.org` | live1.acangroup.org | 🌐 ציבורי (iptv-org) | 1 |
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
| `dai.google.com` | dai.google.com | 🌐 ציבורי (iptv-org) | 1 |
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
| `canal.mediaserver.com.co` | canal.mediaserver.com.co | 🌐 ציבורי (iptv-org) | 1 |
| `slonehls.wns.live` | slonehls.wns.live | 🌐 ציבורי (iptv-org) | 1 |
| `appletree-mytimeuk-rakuten.amagi.tv` | appletree-mytimeuk-rakuten.amagi.tv | 🌐 ציבורי (iptv-org) | 1 |
| `49d735318d6b4c30a24a7997ea594e1b.mediatailor.us-west-2.amazonaws.com` | 49d735318d6b4c30a24a7997ea594e1b.mediatailor.us-west-2.amazonaws.com | 🌐 ציבורי (iptv-org) | 1 |
| `a9c57ec7ec5e4b7daeacc6316a0bb404.mediatailor.eu-west-1.amazonaws.com` | a9c57ec7ec5e4b7daeacc6316a0bb404.mediatailor.eu-west-1.amazonaws.com | 🌐 ציבורי (iptv-org) | 1 |
| `live-rian.cdnvideo.ru` | live-rian.cdnvideo.ru | 🌐 ציבורי (iptv-org) | 1 |
| `d3arbp6l7f096k.cloudfront.net` | d3arbp6l7f096k.cloudfront.net | 🌐 ציבורי (iptv-org) | 1 |
| `stmv1.video.brstream.com.br` | stmv1.video.brstream.com.br | 🌐 ציבורי (iptv-org) | 1 |
| `158.172.217.18` | 158.172.217.18 | 🌐 ציבורי (iptv-org) | 1 |
| `190.61.47.54` | 190.61.47.54 | 🌐 ציבורי (iptv-org) | 1 |
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
| `xumo-xumoent-vc-105-z0vpm.fast.nbcuni.com` | xumo-xumoent-vc-105-z0vpm.fast.nbcuni.com | 🌐 ציבורי (iptv-org) | 1 |
| `comedyhls.persiana.live` | comedyhls.persiana.live | 🌐 ציבורי (iptv-org) | 1 |
| `cc-wie8j8y69d2uy.akamaized.net` | cc-wie8j8y69d2uy.akamaized.net | 🌐 ציבורי (iptv-org) | 1 |
| `segment.yuppcdn.net` | segment.yuppcdn.net | 🌐 ציבורי (iptv-org) | 1 |
| `tvoetv.space` | tvoetv.space | 🌐 ציבורי (iptv-org) | 1 |
| `30a-tv.com` | 30a-tv.com | 🌐 ציבורי (iptv-org) | 1 |
| `linear-12.frequency.stream` | linear-12.frequency.stream | 🌐 ציבורי (iptv-org) | 1 |
| `drybar-drybarcomedy-1-au.samsung.wurl.tv` | drybar-drybarcomedy-1-au.samsung.wurl.tv | 🌐 ציבורי (iptv-org) | 1 |
| `failarmy-international-au.samsung.wurl.tv` | failarmy-international-au.samsung.wurl.tv | 🌐 ציבורי (iptv-org) | 1 |
| `wurlrifftrax.global.transmit.live` | wurlrifftrax.global.transmit.live | 🌐 ציבורי (iptv-org) | 1 |
| `bonappetit-samsung.amagi.tv` | bonappetit-samsung.amagi.tv | 🌐 ציבורי (iptv-org) | 1 |
| `platform-iuoamc.uk` | platform-iuoamc.uk | 🌐 ציבורי (iptv-org) | 1 |
| `tvf-tv5ch.otteravision.com` | tvf-tv5ch.otteravision.com | 🌐 ציבורי (iptv-org) | 1 |
| `api.alpaca.t62a.com` | api.alpaca.t62a.com | 🌐 ציבורי (iptv-org) | 1 |
| `e75fc053c0d2406fb74cdd71d77e1a7f.mediatailor.us-east-1.amazonaws.com` | e75fc053c0d2406fb74cdd71d77e1a7f.mediatailor.us-east-1.amazonaws.com | 🌐 ציבורי (iptv-org) | 1 |
| `mumt03.tangotv.in` | mumt03.tangotv.in | 🌐 ציבורי (iptv-org) | 1 |
| `op-flashcon-digdayahd-1.dens.tv` | op-flashcon-digdayahd-1.dens.tv | 🌐 ציבורי (iptv-org) | 1 |
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
| `138.59.227.20` | 138.59.227.20 | 🌐 ציבורי (iptv-org) | 1 |
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
| `178.134.1.158` | 178.134.1.158 | 🌐 ציבורי (iptv-org) | 1 |
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
| `faithworldtv.abs.tv` | faithworldtv.abs.tv | 🌐 ציבורי (iptv-org) | 1 |
| `evandis.streamakaci.tv` | evandis.streamakaci.tv | 🌐 ציבורי (iptv-org) | 1 |
| `bild-und-ton.stream` | bild-und-ton.stream | 🌐 ציבורי (iptv-org) | 1 |
| `600f07e114306.streamlock.net` | 600f07e114306.streamlock.net | 🌐 ציבורי (iptv-org) | 1 |
| `hls-mycdn08724960135.cdnvideo.ru` | hls-mycdn08724960135.cdnvideo.ru | 🌐 ציבורי (iptv-org) | 1 |
| `3abn.bozztv.com` | 3abn.bozztv.com | 🌐 ציבורי (iptv-org) | 1 |
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
| `2-fss-1.streamhoster.com` | 2-fss-1.streamhoster.com | 🌐 ציבורי (iptv-org) | 1 |
| `oqgdrog4d4rm-hls-live.5centscdn.com` | oqgdrog4d4rm-hls-live.5centscdn.com | 🌐 ציבורי (iptv-org) | 1 |
| `radio.serv.si` | radio.serv.si | 🌐 ציבורי (iptv-org) | 1 |
| `candlelightmedia-sparklightlove-4-us.roku.wurl.tv` | candlelightmedia-sparklightlove-4-us.roku.wurl.tv | 🌐 ציבורי (iptv-org) | 1 |
| `stream01.welocal.stream` | stream01.welocal.stream | 🌐 ציבורי (iptv-org) | 1 |
| `cdnlivevlc.codev8.net` | cdnlivevlc.codev8.net | 🌐 ציבורי (iptv-org) | 1 |
| `eco-live.mediacast.co.il` | eco99fm – Mediacast (רשמי) | ✅ רשמי | 1 |
| `102.livecdn.biz` | 102FM – LiveCDN (רשמי) | ✅ רשמי | 1 |
| `kzradio.mediacast.co.il` | רדיו הקצה – Mediacast (רשמי) | ✅ רשמי | 1 |
| `1075.livecdn.biz` | רדיו חיפה – LiveCDN (רשמי) | ✅ רשמי | 1 |
| `cast.radionahariya.com` | רדיו נהריה – שרת התחנה | ✅ רשמי | 1 |
| `www.rs.mizrahit.fm` | www.rs.mizrahit.fm | ⚠️ ממסר צד שלישי | 1 |
| `stream10.vestaradio.com` | stream10.vestaradio.com | ⚠️ ממסר צד שלישי | 1 |
| `995.livecdn.biz` | 99.5FM – LiveCDN (רשמי) | ✅ רשמי | 1 |
| `radio.diki-mizrahit.co.il` | רדיו שירי דיכאון – השרת של התחנה | ✅ רשמי | 1 |
| `rs.mizrahit.fm` | rs.mizrahit.fm | ⚠️ ממסר צד שלישי | 1 |
| `online.star33radio.com` | online.star33radio.com | ⚠️ ממסר צד שלישי | 1 |
| `play.radioking.io` | play.radioking.io | ⚠️ ממסר צד שלישי | 1 |
| `streaming.radio.co` | streaming.radio.co | ⚠️ ממסר צד שלישי | 1 |
| `ystream.live` | ystream.live | ⚠️ ממסר צד שלישי | 1 |
| `liveradio.co.il` | liveradio.co.il | ✅ רשמי | 1 |
| `radio.betteradio.live` | radio.betteradio.live | ⚠️ ממסר צד שלישי | 1 |
| `listen6.myradio24.com` | listen6.myradio24.com | ⚠️ ממסר צד שלישי | 1 |
| `www.radioking.com` | www.radioking.com | ⚠️ ממסר צד שלישי | 1 |
| `s3.yesstreaming.net` | s3.yesstreaming.net | ⚠️ ממסר צד שלישי | 1 |
| `176.9.7.145` | 176.9.7.145 | ⚠️ ממסר צד שלישי | 1 |
| `n0a.radiojar.com` | n0a.radiojar.com | ⚠️ ממסר צד שלישי | 1 |

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
| 12 | i24NEWS English | i24NEWS – שרת רשמי | ✅ רשמי | iptv-org | ✅ |
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
| 129 | Sport 5 אוקראינה | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Sport5.ua`) | ❌ רק באפליקציה (HTTP) |
| 130 | Sport 2 אוקראינה | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Sport2.ua`) | ❌ רק באפליקציה (HTTP) |
| 131 | Sport 3 אוקראינה | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Sport3.ua`) | ❌ רק באפליקציה (HTTP) |
| 132 | Sport 4 אוקראינה | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Sport4.ua`) | ❌ רק באפליקציה (HTTP) |
| 133 | ספורט פלוס HD (viju+ Sport) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`vijuPlusSport.ru`) | ✅ דרך מקור חלופי: Cinerama.uz – ספק IPTV באוזבקיסטן |
| 134 | ספורט סטאנטה Setanta Sports 2 HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`SetantaSports2Eurasia.ie`) | ❌ רק באפליקציה (HTTP) |
| 135 | Red Bull TV HD | Red Bull TV – Akamai (רשמי) | ✅ רשמי | iptv-org (`RedBullTV.at`) | ✅ |
| 136 | ערוץ אקסטרים Extreme Sports HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 137 | Sport 1 אוקראינה | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Sport1.ua`) | ❌ רק באפליקציה (HTTP) |
| 138 | ספורט 5 – שידור חי | ספורט 5 – השידור הרשמי של RGE (Akamai) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 201 | ACC Digital Network | raycom-accdn-firetv.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`ACCDigitalNetwork.us`) | ✅ |
| 202 | talkSPORT | af7a8b4e.wurl.com | 🌐 ציבורי (iptv-org) | iptv-org (`talkSPORT.uk`) | ✅ |
| 203 | Kozoom TV | streams2.sofast.tv | 🌐 ציבורי (iptv-org) | iptv-org (`KozoomTV.fr`) | ✅ |
| 204 | More Than Sports TV | mts1.iptv-playoutcenter.de | 🌐 ציבורי (iptv-org) | iptv-org (`MoreThanSportsTV.de`) | ✅ |
| 205 | ACI Sport TV | webstream.multistream.it | 🌐 ציבורי (iptv-org) | iptv-org (`ACISportTV.it`) | ✅ |
| 206 | MMA-TV.com | streams2.sofast.tv | 🌐 ציבורי (iptv-org) | iptv-org (`MMATVcom.ru`) | ✅ |
| 207 | DiviSport | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`DiviSport.ua`) | ❌ רק באפליקציה (HTTP) |
| 208 | TSN The Ocho | d3pnbvng3bx2nj.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`TSNTheOcho.ca`) | ✅ |
| 209 | A Spor | rnttwmjcin.turknet.ercdn.net | 🌐 ציבורי (iptv-org) | iptv-org (`ASpor.tr`) | ✅ |
| 210 | DD Sports | d3qs3d2rkhfqrt.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`DDSports.in`) | ✅ |
| 211 | CCTV-Billiards | 38.75.136.137 | 🌐 ציבורי (iptv-org) | iptv-org (`CCTVBilliards.cn`) | ❌ רק באפליקציה (HTTP) |
| 212 | CazeTV | 138.204.86.4 | 🌐 ציבורי (iptv-org) | iptv-org (`CazeTV.br`) | ❌ רק באפליקציה (HTTP) |
| 213 | Azteca Deportes Network | live-manifest.production-public.tubi.io | 🌐 ציבורי (iptv-org) | iptv-org (`AztecaDeportesNetwork.mx`) | ✅ |
| 214 | DeporTV | 158.172.155.130 | 🌐 ציבורי (iptv-org) | iptv-org (`DeporTV.ar`) | ❌ רק באפליקציה (HTTP) |
| 215 | Fast&FunBox | Antik Telecom (סלובקיה), נתיבי test | ⚠️ ממסר צד שלישי | iptv-org (`FastFunBox.nl`) | ✅ |
| 216 | Pluto TV Snooker 900 | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVSnooker900.se`) | ✅ |
| 217 | Sporty TV | dash2.antik.sk | 🌐 ציבורי (iptv-org) | iptv-org (`SportyTV.cz`) | ✅ |
| 218 | Formula 1 AntenaPlay | live4dai.antenaplay.ro | 🌐 ציבורי (iptv-org) | iptv-org (`Formula1AntenaPlay.ro`) | ✅ |
| 219 | CRTV | stmv2.voxtvhd.com.br | 🌐 ציבורי (iptv-org) | iptv-org (`CRTV.cl`) | ✅ |
| 220 | Monterrico TV | h.jcp.live.opencaster.com | 🌐 ציבורי (iptv-org) | iptv-org (`MonterricoTV.pe`) | ✅ |
| 221 | T Sports 7 | live-us1.thaimomo.com | 🌐 ציבורי (iptv-org) | iptv-org (`TSports7.th`) | ✅ |
| 222 | Dong Nai TV 2 | freem3u.xyz | 🌐 ציבורי (iptv-org) | iptv-org (`DongNaiTV2.vn`) | ✅ |
| 223 | M Sports | cdn.rabta.stream | 🌐 ציבורי (iptv-org) | iptv-org (`MSports.pk`) | ✅ |
| 224 | Arryadia | 149.100.11.252 | 🌐 ציבורי (iptv-org) | iptv-org (`Arryadia.ma`) | ❌ רק באפליקציה (HTTP) |
| 225 | KTV Sport | kwtspta.cdn.mangomolo.com | 🌐 ציבורי (iptv-org) | iptv-org (`KTVSport.kw`) | ✅ |
| 226 | Belarus-5 | ngtrk.dc.beltelecom.by | 🌐 ציבורי (iptv-org) | iptv-org (`Belarus5.by`) | ✅ |
| 227 | Bahrain Sports 1 | 5c7b683162943.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`BahrainSports1.bh`) | ✅ |
| 228 | Oman Sports TV | partneta.cdn.mgmlcdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`OmanSportsTV.om`) | ✅ |
| 229 | Talent TV | cast.talenttv.lk | 🌐 ציבורי (iptv-org) | iptv-org (`TalentTV.lk`) | ✅ |
| 230 | Al Iraqia Sport | imn-live.esite-lab.com | 🌐 ציבורי (iptv-org) | iptv-org (`AlIraqiaSport.iq`) | ✅ |
| 231 | El-Heddaf TV | live.elheddaftv.com | 🌐 ציבורי (iptv-org) | iptv-org (`ElHeddafTV.dz`) | ✅ |
| 232 | Jordan Sport | jrtv-live.ercdn.net | 🌐 ציבורי (iptv-org) | iptv-org (`JordanSport.jo`) | ✅ |
| 233 | Prime | cdn.bestream.io | 🌐 ציבורי (iptv-org) | iptv-org (`Prime.sy`) | ✅ |
| 234 | AS3 Sport TV | streamtv.as3sport.online | 🌐 ציבורי (iptv-org) | iptv-org (`AS3SportTV.ve`) | ✅ |
| 235 | Deportes TVC | streaming.onnethn.com | 🌐 ציבורי (iptv-org) | iptv-org (`DeportesTVC.hn`) | ✅ |
| 236 | FTV | master.tucableip.com | 🌐 ציבורי (iptv-org) | iptv-org (`FTV.bo`) | ✅ |
| 237 | FUTV | 190.61.90.17 | 🌐 ציבורי (iptv-org) | iptv-org (`FUTV.cr`) | ❌ רק באפליקציה (HTTP) |
| 238 | Futbol TV | stream3.cinerama.uz | 🌐 ציבורי (iptv-org) | iptv-org (`FutbolTV.uz`) | ❌ רק באפליקציה (HTTP) |
| 239 | MNB Sport | live.mnb.mn | 🌐 ציבורי (iptv-org) | iptv-org (`MNBSport.mn`) | ✅ |
| 240 | Turkmenistan Sport | alpha.tv.online.tm | 🌐 ציבורי (iptv-org) | iptv-org (`TurkmenistanSport.tm`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 241 | American Ninja Warrior | d4whmvwm0rdvi.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`AmericanNinjaWarrior.us`) | ✅ |
| 242 | FIFA+ | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`FIFAPlus.uk`) | ✅ |
| 243 | Trace Sport Stars | lightning-tracesport-samsungau.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`TraceSportStars.fr`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 244 | World of Freesports | mainstreammedia-worldoffreesportsintl-rakuten.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`WorldofFreesports.de`) | ✅ |
| 245 | Inter TV | d3k8wzt41aflvx.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`InterTV.it`) | ✅ |
| 246 | Astrahan.Ru Sport | streaming.astrakhan.ru | 🌐 ציבורי (iptv-org) | iptv-org (`AstrahanRuSport.ru`) | ✅ |
| 247 | Maincast Cybersport | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`MaincastCybersport.ua`) | ❌ רק באפליקציה (HTTP) |
| 248 | Game+ | a-cdn.klowdtv.com | 🌐 ציבורי (iptv-org) | iptv-org (`GamePlus.ca`) | ✅ |
| 249 | TJK TV | tjktv-live.tjk.org | 🌐 ציבורי (iptv-org) | iptv-org (`TJKTV.tr`) | ✅ |
| 250 | Kabaddi 24x7 | samitaorigin.tangotv.in | 🌐 ציבורי (iptv-org) | iptv-org (`Kabaddi24x7.in`) | ✅ |
| 251 | CCTV-Golf & Tennis | 38.75.136.137 | 🌐 ציבורי (iptv-org) | iptv-org (`CCTVGolfTennis.cn`) | ❌ רק באפליקציה (HTTP) |
| 252 | ITV Deportes | thm-it-roku.otteravision.com | 🌐 ציבורי (iptv-org) | iptv-org (`ITVDeportes.mx`) | ✅ |
| 253 | FightBox | nosignal1.antenaplay.ro | 🌐 ציבורי (iptv-org) | iptv-org (`FightBox.nl`) | ✅ |
| 254 | ATG Live | kanal75xto-llhls.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`ATGLive.se`) | ✅ |
| 255 | NHL AntenaPlay | live4dai.antenaplay.ro | 🌐 ציבורי (iptv-org) | iptv-org (`NHLAntenaPlay.ro`) | ✅ |
| 256 | Teletrak TV | unlimited6-cl.dps.live | 🌐 ציבורי (iptv-org) | iptv-org (`TeletrakTV.cl`) | ✅ |
| 257 | Ovacion TV | cdn2.ujjina.com | 🌐 ציבורי (iptv-org) | iptv-org (`OvacionTV.pe`) | ❌ רק באפליקציה (HTTP) |
| 258 | PK Sports | lbgo.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`PKSports.pk`) | ✅ |
| 259 | KTV Sport Plus | kwtsplta.cdn.mangomolo.com | 🌐 ציבורי (iptv-org) | iptv-org (`KTVSportPlus.kw`) | ✅ |
| 260 | Sport TV | media1.skifnet.by | 🌐 ציבורי (iptv-org) | iptv-org (`SportTV.by`) | ❌ רק באפליקציה (HTTP) |
| 261 | Bahrain Sports 2 | 5c7b683162943.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`BahrainSports2.bh`) | ✅ |
| 262 | Colimdo TV | cnn.livestreaminggroup.info | 🌐 ציבורי (iptv-org) | iptv-org (`ColimdoTV.do`) | ✅ |
| 263 | DraftKings Network | na.linear.zype.com | 🌐 ציבורי (iptv-org) | iptv-org (`DraftKingsNetwork.us`) | ✅ |
| 264 | FIFA+ Women | cffda8ff.wurl.com | 🌐 ציבורי (iptv-org) | iptv-org (`FIFAPlusWomen.uk`) | ✅ |
| 265 | Persiana Fight | fighthls.persiana.live | 🌐 ציבורי (iptv-org) | iptv-org (`PersianaFight.fr`) | ✅ |
| 266 | DAZN Darts x Pluto TV | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`DAZNDartsxPlutoTV.de`) | ✅ |
| 267 | MadeinBO TV | srvx1.selftv.video | 🌐 ציבורי (iptv-org) | iptv-org (`MadeinBOTV.it`) | ✅ |
| 268 | Russian Extreme Ultra | 46.53.200.153 | 🌐 ציבורי (iptv-org) | iptv-org (`RussianExtremeUltra.ru`) | ❌ רק באפליקציה (HTTP) |
| 269 | Maincast Sport | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`MaincastSport.ua`) | ❌ רק באפליקציה (HTTP) |
| 270 | HTSpor TV | ciner.daioncdn.net | 🌐 ציבורי (iptv-org) | iptv-org (`HTSporTV.tr`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 271 | CCTV-Storm Football | 38.75.136.137 | 🌐 ציבורי (iptv-org) | iptv-org (`CCTVStormFootball.cn`) | ❌ רק באפליקציה (HTTP) |
| 272 | AyM Sports | 168.196.127.137 | 🌐 ציבורי (iptv-org) | iptv-org (`AyMSports.mx`) | ❌ רק באפליקציה (HTTP) |
| 273 | Brondby TV | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`BrondbyTV.se`) | ✅ |
| 274 | TVR Sport | tvr-sport.lg.mncdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`TVRSport.ro`) | ✅ |
| 275 | Turf Movil | tvturf4.janus.cl | 🌐 ציבורי (iptv-org) | iptv-org (`TurfMovil.cl`) | ✅ |
| 276 | ESPN8: The Ocho | d3b6q2ou5kp8ke.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`ESPN8TheOcho.us`) | ✅ |
| 277 | MUTV | fastly.live.brightcove.com | 🌐 ציבורי (iptv-org) | iptv-org (`MUTV.uk`) | ✅ |
| 278 | DAZN Heldinnen x Pluto TV | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`DAZNHeldinnenxPlutoTV.de`) | ✅ |
| 279 | Sportitalia | edge-001.streamup.eu | 🌐 ציבורי (iptv-org) | iptv-org (`Sportitalia.it`) | ✅ |
| 280 | UDAR | 5.188.159.128 | 🌐 ציבורי (iptv-org) | iptv-org (`UDAR.ru`) | ❌ רק באפליקציה (HTTP) |
| 281 | FB TV | 1hskrdto.rocketcdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`FBTV.tr`) | ❌ רק באפליקציה (HTTP) |
| 282 | Claro Sports | 168.196.127.137 | 🌐 ציבורי (iptv-org) | iptv-org (`ClaroSports.mx`) | ❌ רק באפליקציה (HTTP) |
| 283 | FCK Lovinderne | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`FCKLovinderne.se`) | ✅ |
| 284 | FloHockey | amg02278-amg02278c2-flosports-worldwide-9916.playouts.now.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`FloHockey.us`) | ✅ |
| 285 | Strongman Champions League | rightsboosterltd-scl-1-be.samsung.wurl.tv | 🌐 ציבורי (iptv-org) | iptv-org (`StrongmanChampionsLeague.uk`) | ✅ |
| 286 | Pluto TV Sport | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVSport.de`) | ✅ |
| 287 | Nautical Channel | a-cdn.klowdtv.com | 🌐 ציבורי (iptv-org) | iptv-org (`NauticalChannel.it`) | ✅ |
| 288 | Pluto TV Handboll Highlights | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVHandbollHighlights.se`) | ✅ |
| 289 | FloRacing | amg02278-amg02278c1-flosports-worldwide-7592.playouts.now.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`FloRacing.us`) | ✅ |
| 290 | DAZN Combat | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`DAZNCombat.uk`) | ✅ |
| 291 | Horse TV | a-cdn.klowdtv.com | 🌐 ציבורי (iptv-org) | iptv-org (`HorseTV.it`) | ✅ |
| 292 | Pluto TV Handboll Live | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVHandbollLive.se`) | ✅ |
| 293 | NBC Sports NOW | d4whmvwm0rdvi.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`NBCSportsNOW.us`) | ✅ |
| 294 | TR Sport | livetr.teleromagna.it | 🌐 ציבורי (iptv-org) | iptv-org (`TRSport.it`) | ✅ |
| 295 | Pluto TV Sport | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVSport.se`) | ✅ |
| 296 | PGA Tour | d11k1mnrgfposz.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`PGATour.us`) | ✅ |
| 297 | Milan TV | team-tv.co | 🌐 ציבורי (iptv-org) | iptv-org (`MilanTV.it`) | ❌ רק באפליקציה (HTTP) |
| 298 | Pluto TV Fishing | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVFishing.se`) | ✅ |
| 299 | Racer Select | amg00378-amg00378c4-freelivesports-emea-5637.playouts.now.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`RacerSelect.us`) | ✅ |
| 300 | Pluto TV Handbold Highlights | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVHandboldHighlights.se`) | ✅ |
| 301 | Rally TV | rally-tv-live.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`RallyTV.us`) | ✅ |
| 302 | Pluto TV Handbold Live | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVHandboldLive.se`) | ✅ |
| 303 | SportsGrid | sportsgrid-tribal.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`SportsGrid.us`) | ✅ |
| 304 | Swerve Combat | linear-253.frequency.stream | 🌐 ציבורי (iptv-org) | iptv-org (`SwerveCombat.us`) | ✅ |
| 305 | Tennis Channel 2 | mumbai-edge.smartplaytv.in | 🌐 ציבורי (iptv-org) | iptv-org (`TennisChannel2.us`) | ✅ |
| 306 | beIN SPORTS XTRA en Espanol | dc1644a9jazgj.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`beINSPORTSXTRAenEspanol.us`) | ✅ |
| 307 | BEK Sports | cdn3.wowza.com | 🌐 ציבורי (iptv-org) | iptv-org (`BEKSports.us`) | ✅ |
| 308 | Glory Kickboxing | 6f972d29.wurl.com | 🌐 ציבורי (iptv-org) | iptv-org (`GloryKickboxing.us`) | ✅ |
| 309 | Golazo Network | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`GolazoNetwork.us`) | ✅ |
| 310 | Lacrosse TV | a-cdn.klowdtv.com | 🌐 ציבורי (iptv-org) | iptv-org (`LacrosseTV.us`) | ✅ |
| 311 | Monster Jam | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`MonsterJam.us`) | ✅ |
| 312 | PBR RidePass | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PBRRidePass.us`) | ✅ |
| 313 | Right Now TV | a-cdn.klowdtv.com | 🌐 ציבורי (iptv-org) | iptv-org (`RightNowTV.us`) | ✅ |
| 314 | Slopes TV | stream-us-east-1.getpublica.com | 🌐 ציבורי (iptv-org) | iptv-org (`SlopesTV.us`) | ✅ |
| 315 | Stadium | wurl120sports.global.transmit.live | 🌐 ציבורי (iptv-org) | iptv-org (`Stadium.us`) | ✅ |
| 316 | Swerve Sports | a-cdn.klowdtv.com | 🌐 ציבורי (iptv-org) | iptv-org (`SwerveSports.us`) | ✅ |
| 317 | TVS Bowling Network | rpn.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`TVSBowlingNetwork.us`) | ✅ |
| 318 | TVS Sports | rpn.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`TVSSports.us`) | ✅ |
| 319 | TVS Women Sports | rpn.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`TVSWomenSports.us`) | ✅ |
| 320 | Willow | tvsen5.aynaott.com | 🌐 ציבורי (iptv-org) | iptv-org (`Willow.us`) | ✅ |
| 321 | XFC TV | stream-us-east-1.getpublica.com | 🌐 ציבורי (iptv-org) | iptv-org (`XFCTV.us`) | ✅ |
| 322 | Strongman | rightsboosterltd-scl-2-eu.rakuten.wurl.tv | 🌐 ציבורי (iptv-org) | iptv-org (`Strongman.us`) | ✅ |
| 323 | TVS Boxing | rpn.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`TVSBoxing.us`) | ✅ |
| 324 | TVS Classic Sports | rpn.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`TVSClassicSports.us`) | ✅ |
| 325 | TVS Turbo | rpn.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`TVSTurbo.us`) | ✅ |
| 326 | Bellator MMA | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`BellatorMMA.us`) | ✅ |
| 327 | Pluto TV Competition | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVCompetition.us`) | ✅ |
| 328 | PokerGo | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PokerGo.us`) | ✅ |

## 📺 כלכלה ועסקים (Business)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 139 | כלכלה 10 | Redge – ה-CDN הרשמי של כאן, ערוץ 14 וכלכלה 10 | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 509 | Ameritrade | tdameritrade-vizio.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`Ameritrade.us`) | ✅ |
| 510 | BFM Business | live-cdn-stream-euw1.bfmb.bct.nextradiotv.com | 🌐 ציבורי (iptv-org) | iptv-org (`BFMBusiness.fr`) | ✅ |
| 511 | Negocios TV | streaming013.gestec-video.com | 🌐 ציבורי (iptv-org) | iptv-org (`NegociosTV.es`) | ✅ |
| 512 | Pro Business | tv-streaming.bsh.ru | 🌐 ציבורי (iptv-org) | iptv-org (`ProBusiness.ru`) | ✅ |
| 513 | Ausbiz TV | d9quh89lh7dtw.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`AusbizTV.au`) | ✅ |
| 514 | Al Arabiya Business | live.alarabiya.net | 🌐 ציבורי (iptv-org) | iptv-org (`AlArabiyaBusiness.ae`) | ✅ |
| 515 | Asharq News | live-news.asharq.com | 🌐 ציבורי (iptv-org) | iptv-org (`AsharqNews.sa`) | ✅ |
| 516 | Bloomberg HT | tv.ensonhaber.com | 🌐 ציבורי (iptv-org) | iptv-org (`BloombergHT.tr`) | ✅ |
| 517 | CNBC TV18 | n18syndication.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`CNBCTV18.in`) | ✅ |
| 518 | CGTN Global Biz | fastlive.cctvplus.com | 🌐 ציבורי (iptv-org) | iptv-org (`CGTNGlobalBiz.cn`) | ✅ |
| 519 | Canal Z | d1hz1p2gea2zy4.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`CanalZ.be`) | ✅ |
| 520 | Telewizja Biznesowa | s-pl-01.mediatool.tv | 🌐 ציבורי (iptv-org) | iptv-org (`TelewizjaBiznesowa.pl`) | ✅ |
| 521 | Aleph Business | streamw.m.ro | 🌐 ציבורי (iptv-org) | iptv-org (`AlephBusiness.ro`) | ✅ |
| 522 | CNBC Asia | freem3u.xyz | 🌐 ציבורי (iptv-org) | iptv-org (`CNBCAsia.sg`) | ✅ |
| 523 | DeFiance Media | amg01529-defiancetv-defiancemedia-glewed-tre8x.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`DeFianceMedia.pr`) | ✅ |
| 524 | Champion TV | stream.berosat.live | 🌐 ציבורי (iptv-org) | iptv-org (`ChampionTV.ci`) | ✅ |
| 525 | NW Economie | hls.newworldtv.com | 🌐 ציבורי (iptv-org) | iptv-org (`NWEconomie.tg`) | ✅ |
| 526 | Zuraca TV | tv3.bitstreaming.net | 🌐 ציבורי (iptv-org) | iptv-org (`ZuracaTV.bo`) | ✅ |
| 527 | MNTV | www.khuwemultimedia.com | 🌐 ציבורי (iptv-org) | iptv-org (`MNTV.mz`) | ✅ |
| 528 | apart TV | 2-fss-2.streamhoster.com | 🌐 ציבורי (iptv-org) | iptv-org (`apartTV.lu`) | ✅ |
| 529 | Bloomberg Originals | bloomberg.com | 🌐 ציבורי (iptv-org) | iptv-org (`BloombergOriginals.us`) | ✅ |
| 530 | CNBC Arabiya | cnbc-live.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`CNBCArabiya.ae`) | ✅ |
| 531 | Asharq Radio | Asharq Discovery – ItWorks (רשמי) | ✅ רשמי | iptv-org (`AsharqRadio.sa`) | ✅ |
| 532 | CNBC-e | hnpsechtsc.turknet.ercdn.net | 🌐 ציבורי (iptv-org) | iptv-org (`CNBCe.tr`) | ✅ |
| 533 | CNBC TV18 Prime HD | n18syndication.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`CNBCTV18PrimeHD.in`) | ✅ |
| 534 | CCTV-2 | 74.91.26.218 | 🌐 ציבורי (iptv-org) | iptv-org (`CCTV2.cn`) | ❌ רק באפליקציה (HTTP) |
| 535 | Kanaal Z | d1hz1p2gea2zy4.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`KanaalZ.be`) | ✅ |
| 536 | JOB TV | jobtvserver.eu | 🌐 ציבורי (iptv-org) | iptv-org (`JOBTV.ro`) | ✅ |
| 537 | Bloomberg TV | d8975dc3.wurl.com | 🌐 ציבורי (iptv-org) | iptv-org (`BloombergTV.us`) | ✅ |
| 538 | NDTV Profit | ndtvprofit.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`NDTVProfit.in`) | ✅ |
| 539 | Yahoo! Finance | d1ewctnvcwvvvu.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`YahooFinance.us`) | ✅ |
| 540 | CNBC Awaaz | n18syndication.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`CNBCAwaaz.in`) | ✅ |
| 541 | EntrepreneurTV | stream-us-east-1.getpublica.com | 🌐 ציבורי (iptv-org) | iptv-org (`EntrepreneurTV.us`) | ✅ |
| 542 | CNBC Bajar | n18syndication.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`CNBCBajar.in`) | ✅ |
| 543 | Fox Business Network | tvsen6.aynaott.com | 🌐 ציבורי (iptv-org) | iptv-org (`FoxBusinessNetwork.us`) | ✅ |
| 544 | News Capital Market | newscapital.com | 🌐 ציבורי (iptv-org) | iptv-org (`NewsCapitalMarket.in`) | ✅ |

## 📺 סרטים וסדרות (Movies & Series)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 140 | ערוץ הים התיכון HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`YamTichoni.il`) | ❌ רק באפליקציה (HTTP) |
| 141 | yes סרטים אקשן HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`YesMoviesAction.il`) | ❌ רק באפליקציה (HTTP) |
| 142 | HOT דרמה HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`HOTDrama.il`) | ❌ רק באפליקציה (HTTP) |
| 143 | HOT זון HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`HOTZone.il`) | ❌ רק באפליקציה (HTTP) |
| 144 | yes ריאליטי HD | ערוץ FAST ישראלי – AWS CloudFront | ✅ FAST חינמי | iptv-org (`RealityChannel.il`) | ✅ |
| 145 | ויוה פרימיום HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`VivaPremium.il`) | ❌ רק באפליקציה (HTTP) |
| 146 | סינמה פרימיום HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Premialnoe.ru`) | ❌ רק באפליקציה (HTTP) |
| 147 | סינמה מתח ואקשן HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Ostrosyuzhetnoye.ru`) | ❌ רק באפליקציה (HTTP) |
| 148 | ערוץ סרטי אימה HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Kinouzhas.ru`) | ❌ רק באפליקציה (HTTP) |
| 149 | קולנוע להיטים KinoHit HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`KinoHit.ru`) | ✅ דרך מקור חלופי: Cinerama.uz – ספק IPTV באוזבקיסטן |
| 150 | קולנוע משפחתי Kinosemja HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Kinosemja.ru`) | ❌ רק באפליקציה (HTTP) |
| 151 | סינמה מגה-להיטים HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`vijuPlusMegahit.ru`) | ❌ רק באפליקציה (HTTP) |
| 152 | סינמה בכורה Premiere HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`vijuPlusPremiere.ru`) | ❌ רק באפליקציה (HTTP) |
| 153 | סינמה קומדיה HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`vijuPlusComedy.ru`) | ❌ רק באפליקציה (HTTP) |
| 154 | קולנוע TV1000 אקשן HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`vijuTV1000action.ru`) | ✅ דרך מקור חלופי: Cinerama.uz – ספק IPTV באוזבקיסטן |
| 155 | קולנוע TV1000 קלאסי | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`vijuTV1000.ru`) | ❌ רק באפליקציה (HTTP) |
| 156 | ערוץ הסדרות Serial HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`vijuPlusSerial.ru`) | ❌ רק באפליקציה (HTTP) |
| 157 | סרטי שנות ה-80 Rewind HD | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`80sRewind.us`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 158 | סרטי שנות ה-70 Cinema HD | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`70sCinema.us`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 159 | סרטי שנות ה-2000 Replay HD | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`00sReplay.us`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 160 | משפחת סימפסון 24/7 HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 161 | קולנוע קומדיה CineMan Comedy HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 162 | קולנוע רטרו VHS Cinema HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 163 | ערוץ הדרמה של קשת | קשת – ה-CDN של mako (עם הרשאת צפייה של mako) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 545 | 48 Hours | dai.google.com | 🌐 ציבורי (iptv-org) | iptv-org (`48Hours.us`) | ✅ |
| 546 | True Crime | b6cca454.wurl.com | 🌐 ציבורי (iptv-org) | iptv-org (`TrueCrime.uk`) | ✅ |
| 547 | Cine Nanar | zylo-cinenanar-rakuten.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`CineNanar.fr`) | ✅ |
| 548 | Grjngo Western Movies | amogonetworx-grjngo-3-dk.samsung.wurl.tv | 🌐 ציבורי (iptv-org) | iptv-org (`GrjngoWesternMovies.de`) | ✅ |
| 549 | Cine Friki | d2mr4fu91mjx9m.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`CineFriki.es`) | ✅ |
| 550 | Super TV | 59d7d6f47d7fc.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`SuperTV.it`) | ✅ |
| 551 | Smotrim Chestnyy Detektiv | live-vgtrksmotrim.cdnvideo.ru | 🌐 ציבורי (iptv-org) | iptv-org (`SmotrimChestnyyDetektiv.ru`) | ✅ |
| 552 | FilmUADrama | dash2.antik.sk | 🌐 ציבורי (iptv-org) | iptv-org (`FilmUADrama.ua`) | ✅ |
| 553 | Mix Hollywood | ml-pull-hwc.myco.io | 🌐 ציבורי (iptv-org) | iptv-org (`MixHollywood.eg`) | ✅ |
| 554 | Grand Cinema | gcinemahls.wns.live | 🌐 ציבורי (iptv-org) | iptv-org (`GrandCinema.tr`) | ✅ |
| 555 | B4U Bhojpuri | cdnb4u.wiseplayout.com | 🌐 ציבורי (iptv-org) | iptv-org (`B4UBhojpuri.in`) | ✅ |
| 556 | Harbin Movie Channel | stream.hrbtv.net | 🌐 ציבורי (iptv-org) | iptv-org (`HarbinMovieChannel.cn`) | ✅ |
| 557 | CINDIE TV | cc-hqw8u5r1nshjc.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`CINDIETV.br`) | ✅ |
| 558 | Popcorn Central | oly-ftvpc.otteravision.com | 🌐 ציבורי (iptv-org) | iptv-org (`PopcornCentral.mx`) | ✅ |
| 559 | Europa Europa | iptv.bitred.cl | 🌐 ציבורי (iptv-org) | iptv-org (`EuropaEuropa.ar`) | ✅ |
| 560 | Kuriakos Cine | w2.manasat.com | 🌐 ציבורי (iptv-org) | iptv-org (`KuriakosCine.pt`) | ✅ |
| 561 | FILMBOX+ Festival | nosignal1.antenaplay.ro | 🌐 ציבורי (iptv-org) | iptv-org (`FILMBOXPlusFestival.pl`) | ✅ |
| 562 | Action 24 | actionlive.siliconweb.com | 🌐 ציבורי (iptv-org) | iptv-org (`Action24.gr`) | ❌ רק באפליקציה (HTTP) |
| 563 | Bondi Rescue | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`BondiRescue.se`) | ✅ |
| 564 | Hero | live-fi.tvkaista.net | 🌐 ציבורי (iptv-org) | iptv-org (`Hero.fi`) | ✅ |
| 565 | Cinemaraton Plus | hls02ns.antenaplay.ro | 🌐 ציבורי (iptv-org) | iptv-org (`CinemaratonPlus.ro`) | ✅ |
| 566 | MAX4 | 141.94.193.53 | 🌐 ציבורי (iptv-org) | iptv-org (`MAX4.hu`) | ❌ רק באפליקציה (HTTP) |
| 567 | TVN3 | mdstrm.com | 🌐 ציבורי (iptv-org) | iptv-org (`TVN3.cl`) | ✅ |
| 568 | MCI Television | video.ejeserver.com | 🌐 ציבורי (iptv-org) | iptv-org (`MCITelevision.co`) | ✅ |
| 569 | Latina Clasicos | redirector.rudo.video | 🌐 ציבורי (iptv-org) | iptv-org (`LatinaClasicos.pe`) | ✅ |
| 570 | ON Movies | freem3u.xyz | 🌐 ציבורי (iptv-org) | iptv-org (`ONMovies.vn`) | ✅ |
| 571 | AVA Series | avaserieshls.wns.live | 🌐 ציבורי (iptv-org) | iptv-org (`AVASeries.ir`) | ✅ |
| 572 | Itage TV | viewmedia7219.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`ItageTV.ng`) | ✅ |
| 573 | Aurora Media Films | cdn.streamhispanatv.net | 🌐 ציבורי (iptv-org) | iptv-org (`AuroraMediaFilms.gt`) | ✅ |
| 574 | Mega Cine TV | cnn.hostlagarto.com | 🌐 ציבורי (iptv-org) | iptv-org (`MegaCineTV.do`) | ✅ |
| 575 | Allo Cine | d1yl6bf4hrzbr0.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`AlloCine.cm`) | ✅ |
| 576 | HITS Movies | 103.151.60.162 | 🌐 ציבורי (iptv-org) | iptv-org (`HITSMovies.sg`) | ❌ רק באפליקציה (HTTP) |
| 577 | Silk Way Cinema | stream.qazcdn.net | 🌐 ציבורי (iptv-org) | iptv-org (`SilkWayCinema.kz`) | ✅ |
| 578 | Lollywood HD TV | stream.berosat.live | 🌐 ציבורי (iptv-org) | iptv-org (`LollywoodHDTV.cd`) | ✅ |
| 579 | Serie Max | video1.getstreamhosting.com | 🌐 ציבורי (iptv-org) | iptv-org (`SerieMax.ci`) | ✅ |
| 580 | BIZ Cinema | fl.biztv.media | 🌐 ציבורי (iptv-org) | iptv-org (`BIZCinema.uz`) | ✅ |
| 581 | Maah TV | hls.maahtv.live | 🌐 ציבורי (iptv-org) | iptv-org (`MaahTV.my`) | ✅ |
| 582 | Mei Ah Movie Channel | 103.172.187.30 | 🌐 ציבורי (iptv-org) | iptv-org (`MeiAhMovieChannel.hk`) | ❌ רק באפליקציה (HTTP) |
| 583 | Movie Bangla | alvetv.com | 🌐 ציבורי (iptv-org) | iptv-org (`MovieBangla.bd`) | ❌ רק באפליקציה (HTTP) |
| 584 | MWD Movies | 203.81.84.130 | 🌐 ציבורי (iptv-org) | iptv-org (`MWDMovies.mm`) | ❌ רק באפליקציה (HTTP) |
| 585 | TV1000 | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`TV1000.bg`) | ❌ רק באפליקציה (HTTP) |
| 586 | Antiques Roadshow UK | bbc-antiquesroadshowuk-1-us.roku.wurl.tv | 🌐 ציבורי (iptv-org) | iptv-org (`AntiquesRoadshowUK.us`) | ✅ |
| 587 | BBC Drama | amg00793-amg00793c40-rakuten-es-5444.playouts.now.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`BBCDrama.uk`) | ✅ |
| 588 | Sony One Blacklist | 06cb85ad6ccb4a6b97f561e62d16ad3f.mediatailor.us-west-2.amazonaws.com | 🌐 ציבורי (iptv-org) | iptv-org (`SonyOneBlacklist.fr`) | ✅ |
| 589 | Pluto TV Dating | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVDating.de`) | ✅ |
| 590 | MyTime Movie Network | appletree-mytimespain-rakuten.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`MyTimeMovieNetwork.es`) | ✅ |
| 591 | Rai Movie | team-tv.co | 🌐 ציבורי (iptv-org) | iptv-org (`RaiMovie.it`) | ❌ רק באפליקציה (HTTP) |
| 592 | Star Family | dash2.antik.sk | 🌐 ציבורי (iptv-org) | iptv-org (`StarFamily.ru`) | ✅ |
| 593 | FilmUA Live | dash2.antik.sk | 🌐 ציבורי (iptv-org) | iptv-org (`FilmUALive.ua`) | ✅ |
| 594 | Epic Bhojpuri | cc-8hy4a26pz2uos.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`EpicBhojpuri.in`) | ✅ |
| 595 | CCTV-Nostalgia Theater | 38.75.136.137 | 🌐 ציבורי (iptv-org) | iptv-org (`CCTVNostalgiaTheater.cn`) | ❌ רק באפליקציה (HTTP) |
| 596 | MyTime Movie Network | appletree-mytime-samsungbrazil.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`MyTimeMovieNetwork.br`) | ✅ |
| 597 | Az Cinema | 15.204.196.147 | 🌐 ציבורי (iptv-org) | iptv-org (`AzCinema.mx`) | ❌ רק באפליקציה (HTTP) |
| 598 | TVI Ficcao | raw.githubusercontent.com | 🌐 ציבורי (iptv-org) | iptv-org (`TVIFiccao.pt`) | ✅ |
| 599 | FILMBOX+ Hits | nosignal1.antenaplay.ro | 🌐 ציבורי (iptv-org) | iptv-org (`FILMBOXPlusHits.pl`) | ✅ |
| 600 | Groovy TV | web.onair-radio.eu | 🌐 ציבורי (iptv-org) | iptv-org (`GroovyTV.gr`) | ❌ רק באפליקציה (HTTP) |
| 601 | Car Chase | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`CarChase.se`) | ✅ |
| 602 | Nostalgia TV | hls02ns.antenaplay.ro | 🌐 ציבורי (iptv-org) | iptv-org (`NostalgiaTV.ro`) | ✅ |
| 603 | 13T | origin.dpsgo.com | 🌐 ציבורי (iptv-org) | iptv-org (`13T.cl`) | ✅ |
| 604 | Canal Cine Dios | canal.mediaserver.com.co | 🌐 ציבורי (iptv-org) | iptv-org (`CanalCineDios.co`) | ✅ |
| 605 | ON Cine | freem3u.xyz | 🌐 ציבורי (iptv-org) | iptv-org (`ONCine.vn`) | ✅ |
| 606 | SL 1 | slonehls.wns.live | 🌐 ציבורי (iptv-org) | iptv-org (`SL1.ir`) | ✅ |
| 607 | TVS Retro | cdn.streamhispanatv.net | 🌐 ציבורי (iptv-org) | iptv-org (`TVSRetro.gt`) | ✅ |
| 608 | Boreal | edge.essastream.com | 🌐 ציבורי (iptv-org) | iptv-org (`Boreal.do`) | ✅ |
| 609 | Charge! | fast-channels.sinclairstoryline.com | 🌐 ציבורי (iptv-org) | iptv-org (`Charge.us`) | ✅ |
| 610 | MyTime Movie Network | appletree-mytimeuk-rakuten.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`MyTimeMovieNetwork.uk`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 611 | Sony One Favoris | 49d735318d6b4c30a24a7997ea594e1b.mediatailor.us-west-2.amazonaws.com | 🌐 ציבורי (iptv-org) | iptv-org (`SonyOneFavoris.fr`) | ✅ |
| 612 | Cagney & Lacey | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`CagneyLacey.de`) | ✅ |
| 613 | Rakuten TV Action Movies | a9c57ec7ec5e4b7daeacc6316a0bb404.mediatailor.eu-west-1.amazonaws.com | 🌐 ציבורי (iptv-org) | iptv-org (`RakutenTVActionMovies.es`) | ✅ |
| 614 | Horoshee Kino | live-rian.cdnvideo.ru | 🌐 ציבורי (iptv-org) | iptv-org (`HorosheeKino.ru`) | ✅ |
| 615 | 4ever Cinema | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`4everCinema.ua`) | ❌ רק באפליקציה (HTTP) |
| 616 | NH Tamil Gold | d3arbp6l7f096k.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`NHTamilGold.in`) | ✅ |
| 617 | CCTV-Storm Theater | 38.75.136.137 | 🌐 ציבורי (iptv-org) | iptv-org (`CCTVStormTheater.cn`) | ❌ רק באפליקציה (HTTP) |
| 618 | Retro Cartoon | stmv1.video.brstream.com.br | 🌐 ציבורי (iptv-org) | iptv-org (`RetroCartoon.br`) | ✅ |
| 619 | Claro Cinema | 158.172.217.18 | 🌐 ציבורי (iptv-org) | iptv-org (`ClaroCinema.mx`) | ❌ רק באפליקציה (HTTP) |
| 620 | FILMBOX+ Comedy | nosignal1.antenaplay.ro | 🌐 ציבורי (iptv-org) | iptv-org (`FILMBOXPlusComedy.pl`) | ✅ |
| 621 | 2900 Happiness | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`2900Happiness.se`) | ✅ |
| 622 | Mireasa | stream1.antenaplay.ro | 🌐 ציבורי (iptv-org) | iptv-org (`Mireasa.ro`) | ✅ |
| 623 | Mega Ficcion | 15.204.196.147 | 🌐 ציבורי (iptv-org) | iptv-org (`MegaFiccion.cl`) | ❌ רק באפליקציה (HTTP) |
| 624 | Star Channel | 190.61.47.54 | 🌐 ציבורי (iptv-org) | iptv-org (`StarChannel.co`) | ❌ רק באפליקציה (HTTP) |
| 625 | SL 2 | sltwohls.wns.live | 🌐 ציבורי (iptv-org) | iptv-org (`SL2.ir`) | ✅ |
| 626 | Doctor Who Classic | bbc-classicdrwho-1-us.roku.wurl.tv | 🌐 ציבורי (iptv-org) | iptv-org (`DoctorWhoClassic.us`) | ✅ |
| 627 | Viasat Epic Drama | 92.114.85.87 | 🌐 ציבורי (iptv-org) | iptv-org (`ViasatEpicDrama.uk`) | ❌ רק באפליקציה (HTTP) |
| 628 | Sony One Hits Action | 5098a8b860504a3690fd2e7c0a18d68f.mediatailor.us-west-2.amazonaws.com | 🌐 ציבורי (iptv-org) | iptv-org (`SonyOneHitsAction.fr`) | ✅ |
| 629 | CSI: Crime Scene Investigation | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`CSICrimeSceneInvestigation.de`) | ✅ |
| 630 | Rakuten TV Comedy Movies | 71db867f03ce4d71a29e92155f07ab87.mediatailor.eu-west-1.amazonaws.com | 🌐 ציבורי (iptv-org) | iptv-org (`RakutenTVComedyMovies.es`) | ✅ |
| 631 | Smotrim 100% Klassika | Smotrim (VGTRK) – הבעלים של קרוסלה | ✅ רשמי | iptv-org (`Smotrim100Klassika.ru`) | ✅ |
| 632 | 4ever Drama | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`4everDrama.ua`) | ❌ רק באפליקציה (HTTP) |
| 633 | Punjabi Shorts | vglivessai.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`PunjabiShorts.in`) | ✅ |
| 634 | CCTV-The First Theater | 38.75.136.137 | 🌐 ציבורי (iptv-org) | iptv-org (`CCTVTheFirstTheater.cn`) | ❌ רק באפליקציה (HTTP) |
| 635 | TV Series | stmv1.srvif.com | 🌐 ציבורי (iptv-org) | iptv-org (`TVSeries.br`) | ✅ |
| 636 | FILMBOX+ Love & Crime | nosignal1.antenaplay.ro | 🌐 ציבורי (iptv-org) | iptv-org (`FILMBOXPlusLoveCrime.pl`) | ✅ |
| 637 | 7th Heaven | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`7thHeaven.se`) | ✅ |
| 638 | Bollywood Classic | hls02ns.antenaplay.ro | 🌐 ציבורי (iptv-org) | iptv-org (`BollywoodClassic.ro`) | ✅ |
| 639 | TeleNostalgia | 38.134.250.110 | 🌐 ציבורי (iptv-org) | iptv-org (`TeleNostalgia.co`) | ❌ רק באפליקציה (HTTP) |
| 640 | Afra Film | afrafhls.wns.live | 🌐 ציבורי (iptv-org) | iptv-org (`AfraFilm.ir`) | ✅ |
| 641 | Duck Dynasty | amg00276-amg00276c2-rakuten-uk-3449.playouts.now.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`DuckDynasty.us`) | ✅ |
| 642 | Sony One Hits Comedie | 7aa9671895264ec9a384dfed1b992171.mediatailor.us-west-2.amazonaws.com | 🌐 ציבורי (iptv-org) | iptv-org (`SonyOneHitsComedie.fr`) | ✅ |
| 643 | More TV Crime | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`MoreTVCrime.de`) | ✅ |
| 644 | Rakuten TV Drama Movies | a7089c89d85f453d850c4a1518b43076.mediatailor.eu-west-1.amazonaws.com | 🌐 ציבורי (iptv-org) | iptv-org (`RakutenTVDramaMovies.es`) | ✅ |
| 645 | Smotrim 100% Lyubov | Smotrim (VGTRK) – הבעלים של קרוסלה | ✅ רשמי | iptv-org (`Smotrim100Lyubov.ru`) | ✅ |
| 646 | Raj Digital Plus | livestream.rajtv.tv | 🌐 ציבורי (iptv-org) | iptv-org (`RajDigitalPlus.in`) | ✅ |
| 647 | Classique TV | stmv1.srvif.com | 🌐 ציבורי (iptv-org) | iptv-org (`ClassiqueTV.br`) | ✅ |
| 648 | De Pelicula Plus | 200.115.120.1 | 🌐 ציבורי (iptv-org) | iptv-org (`DePeliculaPlus.mx`) | ❌ רק באפליקציה (HTTP) |
| 649 | AXN Poland | 92.114.85.87 | 🌐 ציבורי (iptv-org) | iptv-org (`AXNPoland.pl`) | ❌ רק באפליקציה (HTTP) |
| 650 | Auction Hunters | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`AuctionHunters.se`) | ✅ |
| 651 | CineWow | hls02ns.antenaplay.ro | 🌐 ציבורי (iptv-org) | iptv-org (`CineWow.ro`) | ✅ |
| 652 | Cafe Film | cafefhls.wns.live | 🌐 ציבורי (iptv-org) | iptv-org (`CafeFilm.ir`) | ✅ |
| 653 | Gravitas Movies | d6dg3ebeih71x.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`GravitasMovies.us`) | ✅ |
| 654 | Persiana Cinema | cinehls.persiana.live | 🌐 ציבורי (iptv-org) | iptv-org (`PersianaCinema.fr`) | ✅ |
| 655 | Pluto TV American True Crime | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVAmericanTrueCrime.de`) | ✅ |
| 656 | Rakuten TV Family Movies | b0d59c8c98974e708e5ccb9a27cdfdfc.mediatailor.eu-west-1.amazonaws.com | 🌐 ציבורי (iptv-org) | iptv-org (`RakutenTVFamilyMovies.es`) | ✅ |
| 657 | Smotrim 100% Muzhskoe | Smotrim (VGTRK) – הבעלים של קרוסלה | ✅ רשמי | iptv-org (`Smotrim100Muzhskoe.ru`) | ✅ |
| 658 | South Station | cc-yw7ztecy8do3q.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`SouthStation.in`) | ✅ |
| 659 | AXN | 170.83.16.50 | 🌐 ציבורי (iptv-org) | iptv-org (`AXN.br`) | ❌ רק באפליקציה (HTTP) |
| 660 | MultiPremier | 168.196.127.137 | 🌐 ציבורי (iptv-org) | iptv-org (`MultiPremier.mx`) | ❌ רק באפליקציה (HTTP) |
| 661 | TVP Seriale | 92.114.85.87 | 🌐 ציבורי (iptv-org) | iptv-org (`TVPSeriale.pl`) | ❌ רק באפליקציה (HTTP) |
| 662 | Becker | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`Becker.se`) | ✅ |
| 663 | Classic TV | clshls.wns.live | 🌐 ציבורי (iptv-org) | iptv-org (`ClassicTV.ir`) | ✅ |
| 664 | Impossible Quiz Show | bbc-impossible-1-us.roku.wurl.tv | 🌐 ציבורי (iptv-org) | iptv-org (`ImpossibleQuizShow.us`) | ✅ |
| 665 | Persiana Family | familyhls.persiana.live | 🌐 ציבורי (iptv-org) | iptv-org (`PersianaFamily.fr`) | ✅ |
| 666 | Pluto TV Classic TV | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVClassicTV.de`) | ✅ |
| 667 | Rakuten TV Top Movies | ff335120300e4742a2b135ee9a9e7df8.mediatailor.eu-west-1.amazonaws.com | 🌐 ציבורי (iptv-org) | iptv-org (`RakutenTVTopMovies.es`) | ✅ |
| 668 | Dorama | fs.uplink.kz | 🌐 ציבורי (iptv-org) | iptv-org (`Dorama.ru`) | ✅ |
| 669 | Thalaa TV | streams2.sofast.tv | 🌐 ציבורי (iptv-org) | iptv-org (`ThalaaTV.in`) | ✅ |
| 670 | Sony Movies | 45.162.64.114 | 🌐 ציבורי (iptv-org) | iptv-org (`SonyMovies.br`) | ❌ רק באפליקציה (HTTP) |
| 671 | Tlnovelas Mexico | 167.249.23.102 | 🌐 ציבורי (iptv-org) | iptv-org (`TlnovelasMexico.mx`) | ❌ רק באפליקציה (HTTP) |
| 672 | FILMBOX+ Action | 92.114.85.87 | 🌐 ציבורי (iptv-org) | iptv-org (`FILMBOXPlusAction.pl`) | ❌ רק באפליקציה (HTTP) |
| 673 | BET | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`BET.se`) | ✅ |
| 674 | Gold Star | moviethls.wns.live | 🌐 ציבורי (iptv-org) | iptv-org (`GoldStar.ir`) | ✅ |
| 675 | Maverick Black Cinema | maverick-maverick-black-cinema-3-us.roku.wurl.tv | 🌐 ציבורי (iptv-org) | iptv-org (`MaverickBlackCinema.us`) | ✅ |
| 676 | Persiana Iranian | irhls.persiana.live | 🌐 ציבורי (iptv-org) | iptv-org (`PersianaIranian.fr`) | ✅ |
| 677 | Pluto TV Cult Films | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVCultFilms.de`) | ✅ |
| 678 | 3Cat Plats bruts | fast-tailor.3catdirectes.cat | 🌐 ציבורי (iptv-org) | iptv-org (`3CatPlatsbruts.es`) | ✅ |
| 679 | Kinoseriya | fs.uplink.kz | 🌐 ציבורי (iptv-org) | iptv-org (`Kinoseriya.ru`) | ✅ |
| 680 | The Movie Club | sis-global.prod.samsungtv.plus | 🌐 ציבורי (iptv-org) | iptv-org (`TheMovieClub.in`) | ✅ |
| 681 | Az Corazon | 200.115.120.1 | 🌐 ציבורי (iptv-org) | iptv-org (`AzCorazon.mx`) | ❌ רק באפליקציה (HTTP) |
| 682 | FILMBOX+ Emotion | 92.114.85.87 | 🌐 ציבורי (iptv-org) | iptv-org (`FILMBOXPlusEmotion.pl`) | ❌ רק באפליקציה (HTTP) |
| 683 | Britain's Next Top Model | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`BritainsNextTopModel.se`) | ✅ |
| 684 | Meta Film TV | metafilmhls.wns.live | 🌐 ציבורי (iptv-org) | iptv-org (`MetaFilmTV.ir`) | ✅ |
| 685 | Midsomer Murders | all3media-midsomer-1-us.roku.wurl.tv | 🌐 ציבורי (iptv-org) | iptv-org (`MidsomerMurders.us`) | ✅ |
| 686 | Persiana Korea | korhls.persiana.live | 🌐 ציבורי (iptv-org) | iptv-org (`PersianaKorea.fr`) | ✅ |
| 687 | Pluto TV Family Movie Club | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVFamilyMovieClub.de`) | ✅ |
| 688 | XPTV US | janus.xpbroadcasting.com | 🌐 ציבורי (iptv-org) | iptv-org (`XPTVUS.es`) | ✅ |
| 689 | Russkiy roman | vod.tuva.ru | 🌐 ציבורי (iptv-org) | iptv-org (`Russkiyroman.ru`) | ✅ |
| 690 | Tolly TV | streams2.sofast.tv | 🌐 ציבורי (iptv-org) | iptv-org (`TollyTV.in`) | ✅ |
| 691 | MyTime Movie Network | appletree-mytime-samsungmexico.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`MyTimeMovieNetwork.mx`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 692 | AXN Black | 92.114.85.87 | 🌐 ציבורי (iptv-org) | iptv-org (`AXNBlack.pl`) | ❌ רק באפליקציה (HTTP) |
| 693 | Bull | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`Bull.se`) | ✅ |
| 694 | NEW K.MOVIES | ads.its-newid.net | 🌐 ציבורי (iptv-org) | iptv-org (`NEWKMOVIES.us`) | ✅ |
| 695 | Persiana Plus | euhls.persiana.live | 🌐 ציבורי (iptv-org) | iptv-org (`PersianaPlus.fr`) | ✅ |
| 696 | Pluto TV Fantasy & Horror | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVFantasyHorror.de`) | ✅ |
| 697 | City Eden KinoAction | cityeden.catcast.tv | 🌐 ציבורי (iptv-org) | iptv-org (`CityEdenKinoAction.ru`) | ✅ |
| 698 | Alankar TV | livetv.tarangplus.in | 🌐 ציבורי (iptv-org) | iptv-org (`AlankarTV.in`) | ✅ |
| 699 | AXN White | 92.114.85.87 | 🌐 ציבורי (iptv-org) | iptv-org (`AXNWhite.pl`) | ❌ רק באפליקציה (HTTP) |
| 700 | CSI | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`CSI.se`) | ✅ |
| 701 | OuterSphere | d3o593mz1glx8d.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`OuterSphere.us`) | ✅ |
| 702 | Persiana Series | onehls.persiana.live | 🌐 ציבורי (iptv-org) | iptv-org (`PersianaSeries.fr`) | ✅ |
| 703 | Pluto TV Investigation | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVInvestigation.de`) | ✅ |
| 704 | City Eden KinoArt | cityeden.catcast.tv | 🌐 ציבורי (iptv-org) | iptv-org (`CityEdenKinoArt.ru`) | ✅ |
| 705 | Bhojpuri Cinema | live-bhojpuri.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`BhojpuriCinema.in`) | ✅ |
| 706 | Sundance TV | 92.114.85.87 | 🌐 ציבורי (iptv-org) | iptv-org (`SundanceTV.pl`) | ❌ רק באפליקציה (HTTP) |
| 707 | Dallas Cowboys Cheerleaders | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`DallasCowboysCheerleaders.se`) | ✅ |
| 708 | The Loop | tgn.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`TheLoop.us`) | ✅ |
| 709 | Pluto TV Sci-fi Series | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVScifiSeries.de`) | ✅ |
| 710 | City Eden KinoAsia | cityeden.catcast.tv | 🌐 ציבורי (iptv-org) | iptv-org (`CityEdenKinoAsia.ru`) | ✅ |
| 711 | Manoranjan Grand | cdn.pishow.tv | 🌐 ציבורי (iptv-org) | iptv-org (`ManoranjanGrand.in`) | ✅ |
| 712 | AXN Spin | 92.114.85.87 | 🌐 ציבורי (iptv-org) | iptv-org (`AXNSpin.pl`) | ❌ רק באפליקציה (HTTP) |
| 713 | Dating Naked UK | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`DatingNakedUK.se`) | ✅ |
| 714 | This Old House | thisoldhouse-2-us.roku.wurl.tv | 🌐 ציבורי (iptv-org) | iptv-org (`ThisOldHouse.us`) | ✅ |
| 715 | Pluto TV Sherlock | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVSherlock.de`) | ✅ |
| 716 | City Eden KinoDetektiv | cityeden.catcast.tv | 🌐 ציבורי (iptv-org) | iptv-org (`CityEdenKinoDetektiv.ru`) | ✅ |
| 717 | Movie Punjab | samitaorigin.tangotv.in | 🌐 ציבורי (iptv-org) | iptv-org (`MoviePunjab.in`) | ✅ |
| 718 | Polsat Film | 92.63.196.180 | 🌐 ציבורי (iptv-org) | iptv-org (`PolsatFilm.pl`) | ❌ רק באפליקציה (HTTP) |
| 719 | Diagnosis Murder | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`DiagnosisMurder.se`) | ✅ |
| 720 | Universal Crime | xumo-xumoent-vc-107-xmuvk.fast.nbcuni.com | 🌐 ציבורי (iptv-org) | iptv-org (`UniversalCrime.us`) | ✅ |
| 721 | Pluto TV Tough Jobs | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVToughJobs.de`) | ✅ |
| 722 | City Eden KinoDok | cityeden.catcast.tv | 🌐 ציבורי (iptv-org) | iptv-org (`CityEdenKinoDok.ru`) | ✅ |
| 723 | Oscar Movies Bhojpuri | cdn.pishow.tv | 🌐 ציבורי (iptv-org) | iptv-org (`OscarMoviesBhojpuri.in`) | ✅ |
| 724 | FBoy Island | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`FBoyIsland.se`) | ✅ |
| 725 | AXN Latin America | streaming.onnethn.com | 🌐 ציבורי (iptv-org) | iptv-org (`AXNLatinAmerica.us`) | ✅ |
| 726 | Pluto TV Westerns | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVWesterns.de`) | ✅ |
| 727 | City Eden KinoDrama | cityeden.catcast.tv | 🌐 ציבורי (iptv-org) | iptv-org (`CityEdenKinoDrama.ru`) | ✅ |
| 728 | Pitaara | vg-pitaaratvlive.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`Pitaara.in`) | ✅ |
| 729 | Happy Days | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`HappyDays.se`) | ✅ |
| 730 | Cinecanal | barinaslive1.boldmss.com | 🌐 ציבורי (iptv-org) | iptv-org (`Cinecanal.us`) | ✅ |
| 731 | The New Detectives | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`TheNewDetectives.de`) | ✅ |
| 732 | City Eden KinoFantastika | cityeden.catcast.tv | 🌐 ציבורי (iptv-org) | iptv-org (`CityEdenKinoFantastika.ru`) | ✅ |
| 733 | Shubh Cinema TV | d393sxaxig6bax.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`ShubhCinemaTV.in`) | ✅ |
| 734 | House of Lies | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`HouseofLies.se`) | ✅ |
| 735 | 50 Cent Action | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`50CentAction.us`) | ✅ |
| 736 | Cine 666 by Pluto TV | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`Cine666byPlutoTV.de`) | ✅ |
| 737 | City Eden KinoKomediya | cityeden.catcast.tv | 🌐 ציבורי (iptv-org) | iptv-org (`CityEdenKinoKomediya.ru`) | ✅ |
| 738 | Star Maa Movies | da86m1sqpm3o0.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`StarMaaMovies.in`) | ✅ |
| 739 | JAG | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`JAG.se`) | ✅ |
| 740 | ABN Bible Movies Channel | mediaserver.abnvideos.com | 🌐 ציבורי (iptv-org) | iptv-org (`ABNBibleMoviesChannel.us`) | ✅ |
| 741 | Cine Action by Pluto TV | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`CineActionbyPlutoTV.de`) | ✅ |
| 742 | City Eden KinoMistika | cityeden.catcast.tv | 🌐 ציבורי (iptv-org) | iptv-org (`CityEdenKinoMistika.ru`) | ✅ |
| 743 | Studio One + | cdn.pishow.tv | 🌐 ציבורי (iptv-org) | iptv-org (`StudioOnePlus.in`) | ✅ |
| 744 | Judge Judy | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`JudgeJudy.se`) | ✅ |

## 📺 קומדיה (Comedy)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 164 | ערוץ הקומדיה HD | ערוץ FAST ישראלי – AWS CloudFront | ✅ FAST חינמי | iptv-org (`ComedyChannel.il`) | ✅ |
| 165 | ערוץ הקומדיה של קשת | קשת – ה-CDN של mako (עם הרשאת צפייה של mako) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 166 | ארץ נהדרת 24/7 | קשת – ה-CDN של mako (עם הרשאת צפייה של mako) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 167 | סברי מרנן 24/7 | קשת – ה-CDN של mako (עם הרשאת צפייה של mako) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 745 | NBC Comedy Vault | xumo-xumoent-vc-105-z0vpm.fast.nbcuni.com | 🌐 ציבורי (iptv-org) | iptv-org (`NBCComedyVault.us`) | ✅ |
| 746 | Persiana Comedy | comedyhls.persiana.live | 🌐 ציבורי (iptv-org) | iptv-org (`PersianaComedy.fr`) | ✅ |
| 747 | Ridiculousness | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`Ridiculousness.de`) | ✅ |
| 748 | Comedy Central | team-tv.co | 🌐 ציבורי (iptv-org) | iptv-org (`ComedyCentral.it`) | ❌ רק באפליקציה (HTTP) |
| 749 | KVN TV | fs.uplink.kz | 🌐 ציבורי (iptv-org) | iptv-org (`KVNTV.ru`) | ✅ |
| 750 | ETV Comedy | cc-wie8j8y69d2uy.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`ETVComedy.in`) | ✅ |
| 751 | Distrito Comedia | 138.121.15.230 | 🌐 ציבורי (iptv-org) | iptv-org (`DistritoComedia.mx`) | ❌ רק באפליקציה (HTTP) |
| 752 | Awkward | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`Awkward.se`) | ✅ |
| 753 | Comedy Play | stream1.antenaplay.ro | 🌐 ציבורי (iptv-org) | iptv-org (`ComedyPlay.ro`) | ✅ |
| 754 | Jordan Comedy | playlist.fasttvcdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`JordanComedy.jo`) | ✅ |
| 755 | HAHA TV | Antik Telecom (סלובקיה), נתיבי test | ⚠️ ממסר צד שלישי | iptv-org (`HAHATV.sk`) | ✅ |
| 756 | SNL Vault | d4whmvwm0rdvi.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`SNLVault.us`) | ✅ |
| 757 | Comedia a lo bestia de Pluto TV | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`ComediaalobestiadePlutoTV.de`) | ✅ |
| 758 | Komediynoe | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Komediynoe.ru`) | ❌ רק באפליקציה (HTTP) |
| 759 | Sirippoli TV | segment.yuppcdn.net | 🌐 ציבורי (iptv-org) | iptv-org (`SirippoliTV.in`) | ✅ |
| 760 | Beavis and Butt-Head | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`BeavisandButtHead.se`) | ✅ |
| 761 | Comedy Central Latin America | barinaslive1.boldmss.com | 🌐 ציבורי (iptv-org) | iptv-org (`ComedyCentralLatinAmerica.us`) | ✅ |
| 762 | Pluto TV Comedia made in Spain | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVComediamadeinSpain.de`) | ✅ |
| 763 | Tvoye TV Yumor | tvoetv.space | 🌐 ציבורי (iptv-org) | iptv-org (`TvoyeTVYumor.ru`) | ❌ רק באפליקציה (HTTP) |
| 764 | Broad City | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`BroadCity.se`) | ✅ |
| 765 | 30A Ridiculous TV | 30a-tv.com | 🌐 ציבורי (iptv-org) | iptv-org (`30ARidiculousTV.us`) | ✅ |
| 766 | Alle hassen Chris | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`AllehassenChris.de`) | ✅ |
| 767 | Komedia | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Komedia.ru`) | ❌ רק באפליקציה (HTTP) |
| 768 | Caroline in the City | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`CarolineintheCity.se`) | ✅ |
| 769 | AFV | linear-12.frequency.stream | 🌐 ציבורי (iptv-org) | iptv-org (`AFV.us`) | ✅ |
| 770 | Becker | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`Becker.de`) | ✅ |
| 771 | Chappelle's Show | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`ChappellesShow.se`) | ✅ |
| 772 | Dry Bar Comedy+ | drybar-drybarcomedy-1-au.samsung.wurl.tv | 🌐 ציבורי (iptv-org) | iptv-org (`DryBarComedyPlus.us`) | ✅ |
| 773 | CC Pluto TV | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`CCPlutoTV.de`) | ✅ |
| 774 | Cheers | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`Cheers.se`) | ✅ |
| 775 | FailArmy | failarmy-international-au.samsung.wurl.tv | 🌐 ציבורי (iptv-org) | iptv-org (`FailArmy.us`) | ✅ |
| 776 | Pluto TV Deutsche Comedy | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVDeutscheComedy.de`) | ✅ |
| 777 | Comedy Central Roast | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`ComedyCentralRoast.se`) | ✅ |
| 778 | RiffTrax | wurlrifftrax.global.transmit.live | 🌐 ציבורי (iptv-org) | iptv-org (`RiffTrax.us`) | ✅ |
| 779 | Sabrina: Total verhext! | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`SabrinaTotalverhext.de`) | ✅ |
| 780 | Crazy Ex-Girlfriend | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`CrazyExGirlfriend.se`) | ✅ |
| 781 | TVS Comedy Network | rpn.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`TVSComedyNetwork.us`) | ✅ |
| 782 | Pluto TV Scherzi e risate | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVScherzierisate.de`) | ✅ |
| 783 | Everybody Hates Chris | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`EverybodyHatesChris.se`) | ✅ |
| 784 | Camera Smile TV | rpn.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`CameraSmileTV.us`) | ✅ |
| 785 | Family Ties | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`FamilyTies.se`) | ✅ |

## 📺 ילדים מהעולם (Kids World)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 168 | ניקלודיאון (ברוסית) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Nickelodeon.ru`) | ❌ רק באפליקציה (HTTP) |
| 169 | ניק ג'וניור (ברוסית) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`NickJr.ru`) | ✅ דרך מקור חלופי: Cinerama.uz – ספק IPTV באוזבקיסטן |
| 170 | גולי גירל Gulli Girl (ברוסית) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`GulliGirl.ru`) | ✅ דרך מקור חלופי: Cinerama.uz – ספק IPTV באוזבקיסטן |
| 171 | דאק טי וי פלוס ducktv PLUS HD | Antik Telecom (סלובקיה), נתיבי test | ⚠️ ממסר צד שלישי | iptv-org (`ducktvPLUS.sk`) | ✅ |
| 172 | בייבי פירסט BabyFirst HD | BabyFirst – JW Player FAST | ✅ FAST חינמי | iptv-org (`BabyFirst.us`) | ✅ |
| 173 | אנימציה קצרצרה Cartoons Short HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 174 | ערוץ לגו Lego Channel HD | LEGO Channel – AWS CloudFront FAST | ✅ FAST חינמי | iptv-org (`LegoChannel.us`) | ✅ |
| 175 | ערוץ טיז'י TiJi לילדים HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`TiJi.ru`) | ✅ דרך מקור חלופי: Cinerama.uz – ספק IPTV באוזבקיסטן |
| 176 | ג'ים ג'אם JimJam לילדים HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`JimJamEurope.uk`) | ❌ רק באפליקציה (HTTP) |
| 177 | אנימציה וילדים Fresh Kids HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 178 | קלאסיקות מצוירות 90s HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 179 | סרטי אנימציה Cartoons Big HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 180 | קולנוע אגדות וילדים HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`KinoKazka.es`) | ❌ רק באפליקציה (HTTP) |
| 181 | פיקסל TV ילדים HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`PixelTV.ua`) | ✅ דרך מקור חלופי: live-tv.cloud – ספק IPTV אוקראיני |
| 182 | ניקי קידס Niki Kids HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`NikiKids.ua`) | ❌ רק באפליקציה (HTTP) |
| 183 | קפטן פנטסטיקה HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`KapitanFantastika.ru`) | ✅ דרך מקור חלופי: Cinerama.uz – ספק IPTV באוזבקיסטן |
| 184 | ערוץ O! ילדים וטבע | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`O.ru`) | ❌ רק באפליקציה (HTTP) |
| 185 | דאק טי וי ducktv HD | Antik Telecom (סלובקיה), נתיבי test | ⚠️ ממסר צד שלישי | iptv-org (`ducktv.sk`) | ✅ |
| 186 | בייבי שארק Baby Shark TV HD | Baby Shark TV – Wurl (Roku FAST) | ✅ FAST חינמי | iptv-org (`BabySharkTV.us`) | ✅ |
| 187 | ערוץ השמש לילדים HD (Solntse Kids) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Solnce.ru`) | ✅ דרך מקור חלופי: ערוץ השמש (Солнце) – CDN |
| 188 | ערוץ מולט ילדים HD (Mult Kids) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Mult.ru`) | ✅ דרך מקור חלופי: Cinerama.uz – ספק IPTV באוזבקיסטן |
| 189 | מולט ומוזיקה לילדים HD (Mult & Music) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Multimuzyka.ru`) | ❌ רק באפליקציה (HTTP) |
| 190 | קרוסלה ערוץ הילדים HD (Carousel Kids) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Carousel.ru`) | ✅ דרך מקור חלופי: Smotrim (VGTRK) – הבעלים של קרוסלה |
| 191 | סי טי סי קידס HD (CTC Kids) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`STSkids.ru`) | ❌ רק באפליקציה (HTTP) |
| 192 | ערוץ אגדות וסיפורים HD (Fairy Tales Kids) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Vgostyakhuskazki.ru`) | ✅ דרך מקור חלופי: Cinerama.uz – ספק IPTV באוזבקיסטן |
| 193 | ערוץ אני אנימציה HD (Ani Kids) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Ani.ru`) | ❌ רק באפליקציה (HTTP) |
| 194 | ערוץ האנימציה PLUSPLUS HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`PLUSPLUS.ua`) | ❌ רק באפליקציה (HTTP) |
| 822 | Botflix TV | hls.botflix.tv | 🌐 ציבורי (iptv-org) | iptv-org (`BotflixTV.us`) | ✅ |
| 823 | Moonbug Kids | moonbug-rokuus.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`MoonbugKids.uk`) | ✅ |
| 824 | ADN TV+ | d3b73b34o7cvkq.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`ADNTVPlus.fr`) | ✅ |
| 825 | KiKA | kikageohls.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`KiKA.de`) | ✅ |
| 826 | Logos TV Kids | streamer1.streamhost.org | 🌐 ציבורי (iptv-org) | iptv-org (`LogosTVKids.es`) | ✅ |
| 827 | BeJoy.kids | stream.paroledivita.org | 🌐 ציבורי (iptv-org) | iptv-org (`BeJoykids.it`) | ✅ |
| 828 | Smile TV | 5d23269b3ec0c.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`SmileTV.ru`) | ✅ |
| 829 | Niki Junior | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`NikiJunior.ua`) | ❌ רק באפליקציה (HTTP) |
| 830 | Knowledge Network | d1wal6k3d7ssin.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`KnowledgeNetwork.ca`) | ✅ |
| 831 | Atfal & Mawaheb TV | 5aafcc5de91f1.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`AtfalMawahebTV.sa`) | ✅ |
| 832 | Koogi TV | 5d658d7e9f562.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`KoogiTV.eg`) | ✅ |
| 833 | Taha TV | stream.starmenajo.com | 🌐 ציבורי (iptv-org) | iptv-org (`TahaTV.lb`) | ✅ |
| 834 | TRT Cocuk | tv-trtcocuk.medya.trt.com.tr | 🌐 ציבורי (iptv-org) | iptv-org (`TRTCocuk.tr`) | ✅ |
| 835 | Epic Kids | cc-t8lqe1o99pszu.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`EpicKids.in`) | ✅ |
| 836 | EBS Kids | ebsonair.ebs.co.kr | 🌐 ציבורי (iptv-org) | iptv-org (`EBSKids.kr`) | ✅ |
| 837 | CCTV-14 | 74.91.26.218 | 🌐 ציבורי (iptv-org) | iptv-org (`CCTV14.cn`) | ❌ רק באפליקציה (HTTP) |
| 838 | El Chavo TV | live20.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`ElChavoTV.mx`) | ✅ |
| 839 | Pakapaka | 138.59.227.20 | 🌐 ציבורי (iptv-org) | iptv-org (`Pakapaka.ar`) | ❌ רק באפליקציה (HTTP) |
| 840 | Kuriakos Kids | w2.manasat.com | 🌐 ציבורי (iptv-org) | iptv-org (`KuriakosKids.pt`) | ✅ |
| 841 | JapanimTV | foxkidstv.be | 🌐 ציבורי (iptv-org) | iptv-org (`JapanimTV.be`) | ✅ |
| 842 | 4 Fun Kids | 4FUN TV – שרת רשמי | ✅ רשמי | iptv-org (`4FunKids.pl`) | ✅ |
| 843 | Pluto TV Juniorklubben | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVJuniorklubben.se`) | ✅ |
| 844 | TraLaLa DSN | hls01ns.antenaplay.ro | 🌐 ציבורי (iptv-org) | iptv-org (`TraLaLaDSN.ro`) | ✅ |
| 845 | Chikitoonz | 45.70.201.81 | 🌐 ציבורי (iptv-org) | iptv-org (`Chikitoonz.pe`) | ❌ רק באפליקציה (HTTP) |
| 846 | Groovia Kids | gist.githubusercontent.com | 🌐 ציבורי (iptv-org) | iptv-org (`GrooviaKids.id`) | ✅ |
| 847 | ON Kids | freem3u.xyz | 🌐 ציבורי (iptv-org) | iptv-org (`ONKids.vn`) | ✅ |
| 848 | Minimax | vodzong.mjunoon.tv | 🌐 ציבורי (iptv-org) | iptv-org (`Minimax.pk`) | ✅ |
| 849 | Pooya TV | tgn.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`PooyaTV.ir`) | ✅ |
| 850 | Tom And Jerry | live20.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`TomAndJerry.do`) | ✅ |
| 851 | MAX Anime | cdnlive.klicgo.net | 🌐 ציבורי (iptv-org) | iptv-org (`MAXAnime.ve`) | ✅ |
| 852 | Akili Kids! | m6.livecdn.io | 🌐 ציבורי (iptv-org) | iptv-org (`AkiliKids.ke`) | ✅ |
| 853 | ZooMoo | zoomoo-samsungau.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`ZooMoo.sg`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 854 | Amou Yazid TV | mkstream.servehttp.com | 🌐 ציבורי (iptv-org) | iptv-org (`AmouYazidTV.dz`) | ❌ רק באפליקציה (HTTP) |
| 855 | Dios Te Ve Kids | s.emisoras.tv | 🌐 ציבורי (iptv-org) | iptv-org (`DiosTeVeKids.hn`) | ✅ |
| 856 | Dreiko TV | cloudvideo.servers10.com | 🌐 ציבורי (iptv-org) | iptv-org (`DreikoTV.pa`) | ✅ |
| 857 | Extrema Kids TV | 627bb251f23c7.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`ExtremaKidsTV.cr`) | ✅ |
| 858 | Balapan | balapantv-stream.qazcdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`Balapan.kz`) | ✅ |
| 859 | ETV 2 | sb.err.ee | 🌐 ציבורי (iptv-org) | iptv-org (`ETV2.ee`) | ✅ |
| 860 | HappyKids | dil9xdvretp0f.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`HappyKids.us`) | ✅ |
| 861 | Persiana Junior | junhls.persiana.live | 🌐 ציבורי (iptv-org) | iptv-org (`PersianaJunior.fr`) | ✅ |
| 862 | RiC | rictv.iptv-playoutcenter.de | 🌐 ציבורי (iptv-org) | iptv-org (`RiC.de`) | ✅ |
| 863 | Boing | boing.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`Boing.it`) | ✅ |
| 864 | Smotrim 100% Detskoe | Smotrim (VGTRK) – הבעלים של קרוסלה | ✅ רשמי | iptv-org (`Smotrim100Detskoe.ru`) | ✅ |
| 865 | TVOKids | fastly.live.brightcove.com | 🌐 ציבורי (iptv-org) | iptv-org (`TVOKids.ca`) | ✅ |
| 866 | ABC Kids | c.mjh.nz | 🌐 ציבורי (iptv-org) | iptv-org (`ABCKids.au`) | ✅ |
| 867 | Minika Cocuk | rnttwmjcin.turknet.ercdn.net | 🌐 ציבורי (iptv-org) | iptv-org (`MinikaCocuk.tr`) | ✅ |
| 868 | The Jungle Book | cc-4bhi5osabejc9.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`TheJungleBook.in`) | ✅ |
| 869 | Box Kids TV | 170.83.49.66 | 🌐 ציבורי (iptv-org) | iptv-org (`BoxKidsTV.br`) | ❌ רק באפליקציה (HTTP) |
| 870 | GikTVMX | pistream.ddns.net | 🌐 ציבורי (iptv-org) | iptv-org (`GikTVMX.mx`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 871 | Plim Plim | 200.115.120.1 | 🌐 ציבורי (iptv-org) | iptv-org (`PlimPlim.ar`) | ❌ רק באפליקציה (HTTP) |
| 872 | Teletoon+ | 92.114.85.87 | 🌐 ציבורי (iptv-org) | iptv-org (`TeletoonPlus.pl`) | ❌ רק באפליקציה (HTTP) |
| 873 | Pluto TV Tecknade klassiker | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVTecknadeklassiker.se`) | ✅ |
| 874 | TraLaLa TV | hls01ns.antenaplay.ro | 🌐 ציבורי (iptv-org) | iptv-org (`TraLaLaTV.ro`) | ✅ |
| 875 | God Stands Kids Club TV | online.godstands.tv | 🌐 ציבורי (iptv-org) | iptv-org (`GodStandsKidsClubTV.pk`) | ✅ |
| 876 | Tiguerito TV | ss2.tvrdomi.com | 🌐 ציבורי (iptv-org) | iptv-org (`TigueritoTV.do`) | ✅ |
| 877 | eSports Max TV | cdnlive.klicgo.net | 🌐 ציבורי (iptv-org) | iptv-org (`eSportsMaxTV.ve`) | ✅ |
| 878 | Afarin TV | 65f16f0fdfc51.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`AfarinTV.iq`) | ✅ |
| 879 | KidsFlix | stream-us-east-1.getpublica.com | 🌐 ציבורי (iptv-org) | iptv-org (`KidsFlix.us`) | ✅ |
| 880 | Tortues Ninja TV | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`TortuesNinjaTV.de`) | ✅ |
| 881 | Super! | 495c5a85d9074f29acffeaea9e0215eb.msvdn.net | 🌐 ציבורי (iptv-org) | iptv-org (`Super.it`) | ✅ |
| 882 | Radost Moya | radm.mediacdn.ru | 🌐 ציבורי (iptv-org) | iptv-org (`RadostMoya.ru`) | ✅ |
| 883 | Minika Go | rnttwmjcin.turknet.ercdn.net | 🌐 ציבורי (iptv-org) | iptv-org (`MinikaGo.tr`) | ✅ |
| 884 | ZB Cartoon | server.zillarbarta.com | 🌐 ציבורי (iptv-org) | iptv-org (`ZBCartoon.in`) | ✅ |
| 885 | DumDum | 45.162.64.114 | 🌐 ציבורי (iptv-org) | iptv-org (`DumDum.br`) | ❌ רק באפליקציה (HTTP) |
| 886 | TeenNick | 92.114.85.87 | 🌐 ציבורי (iptv-org) | iptv-org (`TeenNick.pl`) | ❌ רק באפליקציה (HTTP) |
| 887 | Pluto TV Tecknat | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVTecknat.se`) | ✅ |
| 888 | Chinola TV | tv.wracanal10.com | 🌐 ציבורי (iptv-org) | iptv-org (`ChinolaTV.do`) | ✅ |
| 889 | PBS Kids Alaska | livestream.pbskids.org | 🌐 ציבורי (iptv-org) | iptv-org (`PBSKidsAlaska.us`) | ✅ |
| 890 | Bob Esponja | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`BobEsponja.de`) | ✅ |
| 891 | Tamyr | bsttv.bonus-tv.ru | 🌐 ציבורי (iptv-org) | iptv-org (`Tamyr.ru`) | ✅ |
| 892 | TRT Diyanet Cocuk | tv-trtdiyanetcocuk.medya.trt.com.tr | 🌐 ציבורי (iptv-org) | iptv-org (`TRTDiyanetCocuk.tr`) | ✅ |
| 893 | Discovery Kids | drk6xq0vhn.gpcdn.net | 🌐 ציבורי (iptv-org) | iptv-org (`DiscoveryKids.in`) | ✅ |
| 894 | Polsat JimJam | 185.227.34.179 | 🌐 ציבורי (iptv-org) | iptv-org (`PolsatJimJam.pl`) | ❌ רק באפליקציה (HTTP) |
| 895 | Pluto TV Ungdomsserier | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVUngdomsserier.se`) | ✅ |
| 896 | PBS Kids Mountain | livestream.pbskids.org | 🌐 ציבורי (iptv-org) | iptv-org (`PBSKidsMountain.us`) | ✅ |
| 897 | Pluto TV Kids Classics | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVKidsClassics.de`) | ✅ |
| 898 | Tooku | live-saha.cdnvideo.ru | 🌐 ציבורי (iptv-org) | iptv-org (`Tooku.ru`) | ❌ רק באפליקציה (HTTP) |
| 899 | WOW Kidz | yuppparoriglin.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`WOWKidz.in`) | ✅ |
| 900 | TVP ABC | 92.114.85.87 | 🌐 ציבורי (iptv-org) | iptv-org (`TVPABC.pl`) | ❌ רק באפליקציה (HTTP) |
| 901 | SvampBob Fyrkant | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`SvampBobFyrkant.se`) | ✅ |
| 902 | PBS Kids Pacific | livestream.pbskids.org | 🌐 ציבורי (iptv-org) | iptv-org (`PBSKidsPacific.us`) | ✅ |
| 903 | Pluto TV Teen | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVTeen.de`) | ✅ |
| 904 | Chithiram | cdn.pishow.tv | 🌐 ציבורי (iptv-org) | iptv-org (`Chithiram.in`) | ✅ |
| 905 | SvampeBob Firkant | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`SvampeBobFirkant.se`) | ✅ |
| 906 | Placid | toy.vcdn.fyi | 🌐 ציבורי (iptv-org) | iptv-org (`Placid.us`) | ✅ |
| 907 | Pluto TV Toons | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVToons.de`) | ✅ |
| 908 | Unique TV | mumt05.tangotv.in | 🌐 ציבורי (iptv-org) | iptv-org (`UniqueTV.in`) | ✅ |
| 909 | Totally Turtles | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`TotallyTurtles.se`) | ✅ |
| 910 | RetroCrush | linear-899.frequency.stream | 🌐 ציבורי (iptv-org) | iptv-org (`RetroCrush.us`) | ✅ |
| 911 | SpongeBob Schwammkopf | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`SpongeBobSchwammkopf.de`) | ✅ |
| 912 | Pogo | 103.151.60.162 | 🌐 ציבורי (iptv-org) | iptv-org (`Pogo.in`) | ❌ רק באפליקציה (HTTP) |
| 913 | Pluto TV Tegnefilm | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVTegnefilm.se`) | ✅ |
| 914 | Skwad Play | stream-us-east-1.getpublica.com | 🌐 ציבורי (iptv-org) | iptv-org (`SkwadPlay.us`) | ✅ |
| 915 | Totally Turtles | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`TotallyTurtles.de`) | ✅ |
| 916 | Avatar | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`Avatar.se`) | ✅ |
| 917 | Super Simple Songs | janson-supersimplesongs-1-us.roku.wurl.tv | 🌐 ציבורי (iptv-org) | iptv-org (`SuperSimpleSongs.us`) | ✅ |
| 918 | Teenage Mutant Ninja Turtles | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`TeenageMutantNinjaTurtles.de`) | ✅ |
| 919 | iCarly | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`iCarly.se`) | ✅ |
| 920 | Crunchyroll | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`Crunchyroll.us`) | ✅ |
| 921 | Avatar | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`Avatar.de`) | ✅ |
| 922 | South Park | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`SouthPark.se`) | ✅ |
| 923 | Ninja Kidz TV | d3868b4ny0rgdf.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`NinjaKidzTV.us`) | ✅ |
| 924 | Pluto TV Animation | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVAnimation.de`) | ✅ |
| 925 | South Park Armageddon | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`SouthParkArmageddon.se`) | ✅ |
| 926 | PBS Kids | livestream.pbskids.org | 🌐 ציבורי (iptv-org) | iptv-org (`PBSKids.us`) | ✅ |
| 927 | Pluto TV Retro Toons | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVRetroToons.de`) | ✅ |
| 928 | South Park Rockin' Out | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`SouthParkRockinOut.se`) | ✅ |
| 929 | PBS Kids Hawaii | livestream.pbskids.org | 🌐 ציבורי (iptv-org) | iptv-org (`PBSKidsHawaii.us`) | ✅ |
| 930 | Pluto TV Super Kids | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVSuperKids.de`) | ✅ |
| 931 | South Park: Butters Collection | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`SouthParkButtersCollection.se`) | ✅ |
| 932 | Sonic TV | stream-us-east-1.getpublica.com | 🌐 ציבורי (iptv-org) | iptv-org (`SonicTV.us`) | ✅ |
| 933 | Rugrats | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`Rugrats.de`) | ✅ |
| 934 | South Park: Cartman Collection | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`SouthParkCartmanCollection.se`) | ✅ |
| 935 | SpongeBob SquarePants | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`SpongeBobSquarePants.us`) | ✅ |
| 936 | South Park | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`SouthPark.de`) | ✅ |
| 937 | South Park: Into the Stars | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`SouthParkIntotheStars.se`) | ✅ |
| 938 | TG Junior | d3i6upqaqzosi1.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`TGJunior.us`) | ✅ |
| 939 | South Park Version Original | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`SouthParkVersionOriginal.de`) | ✅ |
| 940 | South Park: Kenny Collection | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`SouthParkKennyCollection.se`) | ✅ |
| 941 | Tortues Ninja TV | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`TortuesNinjaTV.us`) | ✅ |
| 942 | Drake & Josh | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`DrakeJosh.de`) | ✅ |
| 943 | South Park: Kyle Collection | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`SouthParkKyleCollection.se`) | ✅ |
| 944 | TVS Hi Tops | rpn.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`TVSHiTops.us`) | ✅ |
| 945 | Pluto TV Adult Animation | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVAdultAnimation.de`) | ✅ |
| 946 | South Park: Stan Collection | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`SouthParkStanCollection.se`) | ✅ |
| 947 | Bob l'eponge | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`Bobleponge.us`) | ✅ |
| 948 | Pluto TV Star Trek: Animation | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVStarTrekAnimation.de`) | ✅ |
| 949 | Detective Conan | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`DetectiveConan.us`) | ✅ |
| 950 | South Park: Cartman Collection | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`SouthParkCartmanCollection.de`) | ✅ |
| 951 | Las Tortugas Ninja | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`LasTortugasNinja.us`) | ✅ |
| 952 | South Park: Kenny Collection | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`SouthParkKennyCollection.de`) | ✅ |
| 953 | Super! SpongeBob | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`SuperSpongeBob.us`) | ✅ |
| 954 | South Park: Kyle Collection | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`SouthParkKyleCollection.de`) | ✅ |
| 955 | Bob Esponja Calca Quadrada | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`BobEsponjaCalcaQuadrada.us`) | ✅ |
| 956 | South Park: Original Version | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`SouthParkOriginalVersion.de`) | ✅ |
| 957 | Camp Spoopy | stream.ads.ottera.tv | 🌐 ציבורי (iptv-org) | iptv-org (`CampSpoopy.us`) | ✅ |
| 958 | South Park: Stan Collection | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`SouthParkStanCollection.de`) | ✅ |
| 959 | Giggle Mug | tvsen6.aynaott.com | 🌐 ציבורי (iptv-org) | iptv-org (`GiggleMug.us`) | ✅ |
| 960 | S Corby TV | vodcdn.bamboo-cloud.com | 🌐 ציבורי (iptv-org) | iptv-org (`SCorbyTV.us`) | ✅ |

## 📺 מוזיקה (Music TV)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 195 | 4ever Music Hits HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`4everMusic.ua`) | ❌ רק באפליקציה (HTTP) |
| 196 | 4Fun TV Pop Hits HD | 4FUN TV – שרת רשמי | ✅ רשמי | iptv-org (`4FunTV.pl`) | ✅ |
| 197 | ¡Viva Latino! Dance HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`VivaLatino.ua`) | ❌ רק באפליקציה (HTTP) |
| 198 | 1HD Music Television | שרת IP ללא שם (1HD) | ⚠️ ממסר צד שלישי | iptv-org (`1HDMusicTelevision.ru`) | ❌ רק באפליקציה (HTTP) |
| 199 | 70-80 TV Retro Hits | 70-80 TV – Wowza Streamlock | ✅ רשמי | iptv-org (`7080TV.it`) | ✅ |
| 200 | ערוץ המוזיקה של קשת | קשת – ה-CDN של mako (עם הרשאת צפייה של mako) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 201 | רדיוס 100FM – וידאו | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 961 | Botflix Radioo | hls.botflix.tv | 🌐 ציבורי (iptv-org) | iptv-org (`BotflixRadioo.us`) | ✅ |
| 962 | Afrobeat TV Entertainment | stream.ecable.tv | 🌐 ציבורי (iptv-org) | iptv-org (`AfrobeatTVEntertainment.uk`) | ✅ |
| 963 | Trace Africa | channels.trace.plus | 🌐 ציבורי (iptv-org) | iptv-org (`TraceAfrica.fr`) | ✅ |
| 964 | DMF | d-m-f.iptv-playoutcenter.de | 🌐 ציבורי (iptv-org) | iptv-org (`DMF.de`) | ✅ |
| 965 | Music TV Granada | cloudvideo.servers10.com | 🌐 ציבורי (iptv-org) | iptv-org (`MusicTVGranada.es`) | ✅ |
| 966 | FM ITALIA | stream2.xdevel.com | 🌐 ציבורי (iptv-org) | iptv-org (`FMITALIA.it`) | ✅ |
| 967 | Maidan | live-maidantv.cdnvideo.ru | 🌐 ציבורי (iptv-org) | iptv-org (`Maidan.ru`) | ✅ |
| 968 | Happy Radio TV | tv.mediacp.eu | 🌐 ציבורי (iptv-org) | iptv-org (`HappyRadioTV.ua`) | ✅ |
| 969 | Movie Music by Stingray | lotus.stingray.com | 🌐 ציבורי (iptv-org) | iptv-org (`MovieMusicbyStingray.ca`) | ✅ |
| 970 | Folk Klub TV | tv1.intv.mk | 🌐 ציבורי (iptv-org) | iptv-org (`FolkKlubTV.au`) | ❌ רק באפליקציה (HTTP) |
| 971 | MBC FM | dbbv9umqcd7cs.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`MBCFM.ae`) | ✅ |
| 972 | Jeddah Radio | live.kwikmotion.com | 🌐 ציבורי (iptv-org) | iptv-org (`JeddahRadio.sa`) | ✅ |
| 973 | NogoumFMTV | nogoumtv.nrpstream.com | 🌐 ציבורי (iptv-org) | iptv-org (`NogoumFMTV.eg`) | ✅ |
| 974 | Aghani Aghani TV | cdn.streamlane.tv | 🌐 ציבורי (iptv-org) | iptv-org (`AghaniAghaniTV.lb`) | ✅ |
| 975 | Power Dance | livetv.powerapp.com.tr | 🌐 ציבורי (iptv-org) | iptv-org (`PowerDance.tr`) | ✅ |
| 976 | 9X Jalwa | wiselp.wiseplayout.com | 🌐 ציבורי (iptv-org) | iptv-org (`9XJalwa.in`) | ✅ |
| 977 | Gugak TV | mgugaklive.nowcdn.co.kr | 🌐 ציבורי (iptv-org) | iptv-org (`GugakTV.kr`) | ✅ |
| 978 | CCTV-Storm Music | 38.75.136.137 | 🌐 ציבורי (iptv-org) | iptv-org (`CCTVStormMusic.cn`) | ❌ רק באפליקציה (HTTP) |
| 979 | TikTok Radio Brasil | d1bl6tskrpq9ze.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`TikTokRadioBrasil.br`) | ✅ |
| 980 | Bandamax | streaming.onnethn.com | 🌐 ציבורי (iptv-org) | iptv-org (`Bandamax.mx`) | ✅ |
| 981 | La 100 | mdstrm.com | 🌐 ציבורי (iptv-org) | iptv-org (`La100.ar`) | ✅ |
| 982 | ON FM | 5ce9406b73c33.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`ONFM.pt`) | ✅ |
| 983 | 360TuneBox | Antik Telecom (סלובקיה), נתיבי test | ⚠️ ממסר צד שלישי | iptv-org (`360TuneBox.nl`) | ✅ |
| 984 | StuBru | live.vrtcdn.be | 🌐 ציבורי (iptv-org) | iptv-org (`StuBru.be`) | ✅ |
| 985 | RADIO DISCOunt TV | 70-80 TV – Wowza Streamlock | ✅ רשמי | iptv-org (`RADIODISCOuntTV.ch`) | ✅ |
| 986 | Antenne Kaernten | 60efd7a2b4d02.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`AntenneKaernten.at`) | ✅ |
| 987 | Radio RAM | 1888165924.rsc.cdn77.org | 🌐 ציבורי (iptv-org) | iptv-org (`RadioRAM.pl`) | ✅ |
| 988 | Baraza TV | eco.streams.ovh | 🌐 ציבורי (iptv-org) | iptv-org (`BarazaTV.gr`) | ✅ |
| 989 | JRTV Jarviradio | streamer.radiotaajuus.fi | 🌐 ציבורי (iptv-org) | iptv-org (`JRTVJarviradio.fi`) | ✅ |
| 990 | Ocko Expres | ocko-live.ssl.cdn.cra.cz | 🌐 ציבורי (iptv-org) | iptv-org (`OckoExpres.cz`) | ✅ |
| 991 | Party Mix | hls02ns.antenaplay.ro | 🌐 ציבורי (iptv-org) | iptv-org (`PartyMix.ro`) | ✅ |
| 992 | Oxygen Music | oxygenmusic.hu | 🌐 ציבורי (iptv-org) | iptv-org (`OxygenMusic.hu`) | ✅ |
| 993 | Pauta | redirector.rudo.video | 🌐 ציבורי (iptv-org) | iptv-org (`Pauta.cl`) | ✅ |
| 994 | La Kalle | mdstrm.com | 🌐 ציבורי (iptv-org) | iptv-org (`LaKalle.co`) | ✅ |
| 995 | Radio Onda Digital | live.ondadigitaltv.com | 🌐 ציבורי (iptv-org) | iptv-org (`RadioOndaDigital.pe`) | ✅ |
| 996 | ON Music | freem3u.xyz | 🌐 ציבורי (iptv-org) | iptv-org (`ONMusic.vn`) | ✅ |
| 997 | JooMusic | livecdn.live247stream.com | 🌐 ציבורי (iptv-org) | iptv-org (`JooMusic.pk`) | ✅ |
| 998 | AFN TV | bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`AFNTV.ir`) | ✅ |
| 999 | HMTV | customer-yzxu6n447dx2x45h.cloudflarestream.com | 🌐 ציבורי (iptv-org) | iptv-org (`HMTV.ng`) | ✅ |
| 1000 | Gex TV | live20.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`GexTV.cr`) | ✅ |
| 1001 | Venus Media | tigocloud.desdeparaguay.net | 🌐 ציבורי (iptv-org) | iptv-org (`VenusMedia.py`) | ✅ |
| 1002 | El Sol Network TV | tv.wracanal10.com | 🌐 ציבורי (iptv-org) | iptv-org (`ElSolNetworkTV.ec`) | ✅ |
| 1003 | Best of Dance TV | m1b2.worldcast.tv | 🌐 ציבורי (iptv-org) | iptv-org (`BestofDanceTV.ee`) | ✅ |
| 1004 | Makao TV | live.obslivestream.com | 🌐 ציבורי (iptv-org) | iptv-org (`MakaoTV.do`) | ✅ |
| 1005 | Magic TV | bss1.neterra.tv | 🌐 ציבורי (iptv-org) | iptv-org (`MagicTV.bg`) | ✅ |
| 1006 | Cool FM 98.9 | live2.tensila.com | 🌐 ציבורי (iptv-org) | iptv-org (`CoolFM989.aw`) | ✅ |
| 1007 | Planet 100.9 FM | streamlov.alsolnet.com | 🌐 ציבורי (iptv-org) | iptv-org (`Planet1009FM.pa`) | ✅ |
| 1008 | Radio Hit FM TV | peer2.tdiradio.com | 🌐 ציבורי (iptv-org) | iptv-org (`RadioHitFMTV.rs`) | ✅ |
| 1009 | Imagen FM 105.1 | streaming.imagenfm105-1.com | 🌐 ציבורי (iptv-org) | iptv-org (`ImagenFM1051.hn`) | ✅ |
| 1010 | FUSION TV | edge20.vedge.infomaniak.com | 🌐 ציבורי (iptv-org) | iptv-org (`FUSIONTV.mq`) | ✅ |
| 1011 | Madras FM TV | edge12.vedge.infomaniak.com | 🌐 ציבורי (iptv-org) | iptv-org (`MadrasFMTV.gp`) | ✅ |
| 1012 | BTA TV | cloud.fastchannel.es | 🌐 ציבורי (iptv-org) | iptv-org (`BTATV.ve`) | ✅ |
| 1013 | The Retro Channel | 583ed2f226ac2.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`TheRetroChannel.pr`) | ✅ |
| 1014 | Folk TV | stream.nasatv.com.mk | 🌐 ציבורי (iptv-org) | iptv-org (`FolkTV.mk`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 1015 | Tezaur TV | tezaurtv.md | 🌐 ציבורי (iptv-org) | iptv-org (`TezaurTV.md`) | ✅ |
| 1016 | RTL Gold | stream.rtl.lu | 🌐 ציבורי (iptv-org) | iptv-org (`RTLGold.lu`) | ✅ |
| 1017 | Audiokiss | master.tucableip.com | 🌐 ציבורי (iptv-org) | iptv-org (`Audiokiss.bo`) | ✅ |
| 1018 | CMC TV | stream.cmctv.hr | 🌐 ציבורי (iptv-org) | iptv-org (`CMCTV.hr`) | ✅ |
| 1019 | 98.1 Pearl FM | live2.tensila.com | 🌐 ציבורי (iptv-org) | iptv-org (`981PearlFM.sx`) | ✅ |
| 1020 | Scorch TV | w6oy7x7jlxrg-hls-live.5centscdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`ScorchTV.tt`) | ✅ |
| 1021 | Star 101 FM | live2.tensila.com | 🌐 ציבורי (iptv-org) | iptv-org (`Star101FM.gu`) | ✅ |
| 1022 | Tjil TV | hls-1zdhtxpm-livepush.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`TjilTV.na`) | ✅ |
| 1023 | Falastini TV | rp.tactivemedia.com | 🌐 ציבורי (iptv-org) | iptv-org (`FalastiniTV.ps`) | ✅ |
| 1024 | Jordan Songs | playlist.fasttvcdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`JordanSongs.jo`) | ✅ |
| 1025 | Mosaique FM | webcam.mosaiquefm.net | 🌐 ציבורי (iptv-org) | iptv-org (`MosaiqueFM.tn`) | ✅ |
| 1026 | A2i Music | stream.sen-gt.com | 🌐 ציבורי (iptv-org) | iptv-org (`A2iMusic.sn`) | ✅ |
| 1027 | Ivoire Channel | video1.getstreamhosting.com | 🌐 ציבורי (iptv-org) | iptv-org (`IvoireChannel.ci`) | ✅ |
| 1028 | Muzangala TV | 5cf4a2c2512a2.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`MuzangalaTV.ao`) | ✅ |
| 1029 | TV Cidade | stmv1.srvif.com | 🌐 ציבורי (iptv-org) | iptv-org (`TVCidade.cv`) | ✅ |
| 1030 | AlbKanale Music TV | albportal.net | 🌐 ציבורי (iptv-org) | iptv-org (`AlbKanaleMusicTV.al`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 1031 | Isango TV | 80.241.215.175 | 🌐 ציבורי (iptv-org) | iptv-org (`IsangoTV.rw`) | ❌ רק באפליקציה (HTTP) |
| 1032 | Ishtar TV | stream.ishtartv.com | 🌐 ציבורי (iptv-org) | iptv-org (`IshtarTV.iq`) | ✅ |
| 1033 | K100 | k100streymi.mbl.is | 🌐 ציבורי (iptv-org) | iptv-org (`K100.is`) | ✅ |
| 1034 | Kayhan TV | playout395.livestreamingcdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`KayhanTV.af`) | ✅ |
| 1035 | KN Music TV | cdn4.yayin.com.tr | 🌐 ציבורי (iptv-org) | iptv-org (`KNMusicTV.az`) | ✅ |
| 1036 | Look Thoong TV | livefta.malimarcdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`LookThoongTV.la`) | ✅ |
| 1037 | M-1 | m-1.data.lt | 🌐 ציבורי (iptv-org) | iptv-org (`M1.lt`) | ✅ |
| 1038 | Radio SWH TV | 00ff00.latnet.media | 🌐 ציבורי (iptv-org) | iptv-org (`RadioSWHTV.lv`) | ✅ |
| 1039 | Turkmen Owazy | alpha.tv.online.tm | 🌐 ציבורי (iptv-org) | iptv-org (`TurkmenOwazy.tm`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 1040 | Harmony Gospel Music TV | tgn.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`HarmonyGospelMusicTV.us`) | ✅ |
| 1041 | NOW Rock | lightning-now90s-samsungnz.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`NOWRock.uk`) | ✅ |
| 1042 | Trace Jama | channels.trace.plus | 🌐 ציבורי (iptv-org) | iptv-org (`TraceJama.fr`) | ✅ |
| 1043 | Lausitzwelle | h056.video-stream-hosting.de | 🌐 ציבורי (iptv-org) | iptv-org (`Lausitzwelle.de`) | ✅ |
| 1044 | Kiss Kiss TV | 58f12ffd2447a.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`KissKissTV.it`) | ✅ |
| 1045 | Muz Soyuz | hls-tvsoyuz.cdnvideo.ru | 🌐 ציבורי (iptv-org) | iptv-org (`MuzSoyuz.ru`) | ✅ |
| 1046 | Muzvar | live-tv.cloud – ספק IPTV אוקראיני | ⚠️ ממסר צד שלישי | iptv-org (`Muzvar.ua`) | ✅ |
| 1047 | Qello Concerts by Stingray | lotus.stingray.com | 🌐 ציבורי (iptv-org) | iptv-org (`QelloConcertsbyStingray.ca`) | ✅ |
| 1048 | INTV Australia | tv1.intv.mk | 🌐 ציבורי (iptv-org) | iptv-org (`INTVAustralia.au`) | ❌ רק באפליקציה (HTTP) |
| 1049 | PMC | pmchls.wns.live | 🌐 ציבורי (iptv-org) | iptv-org (`PMC.ae`) | ✅ |
| 1050 | Nidae AlIslam Radio | live.kwikmotion.com | 🌐 ציבורי (iptv-org) | iptv-org (`NidaeAlIslamRadio.sa`) | ✅ |
| 1051 | El Radio 9090 FM | 9090video.mobtada.com | 🌐 ציבורי (iptv-org) | iptv-org (`ElRadio9090FM.eg`) | ✅ |
| 1052 | mfm | hms.pfs.gdn | 🌐 ציבורי (iptv-org) | iptv-org (`mfm.lb`) | ✅ |
| 1053 | Power Love | livetv.powerapp.com.tr | 🌐 ציבורי (iptv-org) | iptv-org (`PowerLove.tr`) | ✅ |
| 1054 | Aaryaa TV | stream.ottlive.co.in | 🌐 ציבורי (iptv-org) | iptv-org (`AaryaaTV.in`) | ✅ |
| 1055 | CCTV-11 | 74.91.26.218 | 🌐 ציבורי (iptv-org) | iptv-org (`CCTV11.cn`) | ❌ רק באפליקציה (HTTP) |
| 1056 | +MaisUm | stream.ichibansys.com | 🌐 ציבורי (iptv-org) | iptv-org (`PlusMaisUm.br`) | ✅ |
| 1057 | Conecta TV | stream8.mexiserver.com | 🌐 ציבורי (iptv-org) | iptv-org (`ConectaTV.mx`) | ✅ |
| 1058 | X Level Media | tuvideoonline.com.ar | 🌐 ציבורי (iptv-org) | iptv-org (`XLevelMedia.ar`) | ✅ |
| 1059 | RFPtv | video03.logicahost.com.br | 🌐 ציבורי (iptv-org) | iptv-org (`RFPtv.pt`) | ✅ |
| 1060 | XITE Hits | d726x48n2pd5h.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`XITEHits.nl`) | ✅ |
| 1061 | Klara | live.vrtcdn.be | 🌐 ציבורי (iptv-org) | iptv-org (`Klara.be`) | ✅ |
| 1062 | Antenne Steiermark | 60efd7a2b4d02.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`AntenneSteiermark.at`) | ✅ |
| 1063 | Disco Polo Music | 185.227.34.179 | 🌐 ציבורי (iptv-org) | iptv-org (`DiscoPoloMusic.pl`) | ❌ רק באפליקציה (HTTP) |
| 1064 | Panik TV | livetv.streams.ovh | 🌐 ציבורי (iptv-org) | iptv-org (`PanikTV.gr`) | ✅ |
| 1065 | Navahang TV | hls.navahang.live | 🌐 ציבורי (iptv-org) | iptv-org (`NavahangTV.fi`) | ✅ |
| 1066 | Retro Music TV | stream.mediawork.cz | 🌐 ציבורי (iptv-org) | iptv-org (`RetroMusicTV.cz`) | ✅ |
| 1067 | Impact TV 2 | online.tvimpact.live | 🌐 ציבורי (iptv-org) | iptv-org (`ImpactTV2.ro`) | ✅ |
| 1068 | Rewind TV | tls-cl.cdnz.cl | 🌐 ציבורי (iptv-org) | iptv-org (`RewindTV.cl`) | ✅ |
| 1069 | La Morada Radio TV | movil.ejeserver.com | 🌐 ציבורי (iptv-org) | iptv-org (`LaMoradaRadioTV.co`) | ✅ |
| 1070 | La Fabulosa TV | eu1.servers10.com | 🌐 ציבורי (iptv-org) | iptv-org (`LaFabulosaTV.pe`) | ✅ |
| 1071 | Jalwa TV | vodzong.mjunoon.tv | 🌐 ציבורי (iptv-org) | iptv-org (`JalwaTV.pk`) | ✅ |
| 1072 | T2 TV | t2hls.wns.live | 🌐 ציבורי (iptv-org) | iptv-org (`T2TV.ir`) | ✅ |
| 1073 | AMusic Channel | viewmedia7219.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`AMusicChannel.ng`) | ✅ |
| 1074 | Canal 3 KMK TV | live20.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`Canal3KMKTV.cr`) | ✅ |
| 1075 | Dismar Radio TV | rds3.desdeparaguay.net | 🌐 ציבורי (iptv-org) | iptv-org (`DismarRadioTV.py`) | ✅ |
| 1076 | FM Mundo | video2.makrodigital.com | 🌐 ציבורי (iptv-org) | iptv-org (`FMMundo.ec`) | ✅ |
| 1077 | DanceTV Deep House District | m1b2.worldcast.tv | 🌐 ציבורי (iptv-org) | iptv-org (`DanceTVDeepHouseDistrict.ee`) | ✅ |
| 1078 | Montones TV | lbgo.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`MontonesTV.do`) | ✅ |
| 1079 | The Voice | bss1.neterra.tv | 🌐 ציבורי (iptv-org) | iptv-org (`TheVoice.bg`) | ✅ |
| 1080 | Power 101.7 FM | vcp11.myplaytv.com | 🌐 ציבורי (iptv-org) | iptv-org (`Power1017FM.aw`) | ✅ |
| 1081 | Super Q Panama | vcp8.myplaytv.com | 🌐 ציבורי (iptv-org) | iptv-org (`SuperQPanama.pa`) | ✅ |
| 1082 | Radio Karolina TV | peer2.tdiradio.com | 🌐 ציבורי (iptv-org) | iptv-org (`RadioKarolinaTV.rs`) | ✅ |
| 1083 | La Top 102.9 | 59d39900ebfb8.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`LaTop1029.hn`) | ✅ |
| 1084 | Identite Tele Caraibes | vdo2.pro-fhi.net | 🌐 ציבורי (iptv-org) | iptv-org (`IdentiteTeleCaraibes.mq`) | ✅ |
| 1085 | RHT Guadeloupe | edge12.vedge.infomaniak.com | 🌐 ציבורי (iptv-org) | iptv-org (`RHTGuadeloupe.gp`) | ✅ |
| 1086 | ShowVen TV | cdnbarinas.tvzamora.com | 🌐 ציבורי (iptv-org) | iptv-org (`ShowVenTV.ve`) | ✅ |
| 1087 | La-X | stream.eleden.com | 🌐 ציבורי (iptv-org) | iptv-org (`LaX.pr`) | ✅ |
| 1088 | Love TV | stream.nasatv.com.mk | 🌐 ציבורי (iptv-org) | iptv-org (`LoveTV.mk`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 1089 | Hit FM | hitfm.nfthost.net | 🌐 ציבורי (iptv-org) | iptv-org (`HitFM.md`) | ✅ |
| 1090 | Today Radio | stream.rtl.lu | 🌐 ציבורי (iptv-org) | iptv-org (`TodayRadio.lu`) | ✅ |

## 📺 טבע ודוקו (Documentary & Nature)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 202 | Animal Planet World HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`AnimalPlanet.ru`) | ❌ רק באפליקציה (HTTP) |
| 203 | Discovery חקירות (ID) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`InvestigationDiscovery.ru`) | ❌ רק באפליקציה (HTTP) |
| 204 | עולם וטבע HD (viju+ Planet) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`vijuPlusPlanet.ru`) | ❌ רק באפליקציה (HTTP) |
| 205 | BBC Earth Live HD | BBC Earth – Amagi / Xumo FAST | ✅ FAST חינמי | iptv-org (`BBCEarth.uk`) | ✅ |
| 206 | Asharq Discovery HD | Asharq Discovery – ItWorks (רשמי) | ✅ רשמי | iptv-org (`AsharqDiscovery.sa`) | ✅ |
| 207 | 365 ימי היסטוריה (History TV) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`365daysTV.ru`) | ✅ דרך מקור חלופי: Cinerama.uz – ספק IPTV באוזבקיסטן |
| 1091 | Dateline 24/7 | d1si3n1st4nkgb.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`Dateline247.us`) | ✅ |
| 1092 | History Hit | lds-timeline-rakuten.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`HistoryHit.uk`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 1093 | Francophonie24 | 5421175365ea3.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`Francophonie24.fr`) | ✅ |
| 1094 | Adventure Earth | a57e9c69976649b582a8d7604c00e69a.mediatailor.us-east-1.amazonaws.com | 🌐 ציבורי (iptv-org) | iptv-org (`AdventureEarth.de`) | ✅ |
| 1095 | Crimen & Historia | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`CrimenHistoria.es`) | ✅ |
| 1096 | Total Crime | aegis-cloudfront-1.tubi.video | 🌐 ציבורי (iptv-org) | iptv-org (`TotalCrime.ca`) | ✅ |
| 1097 | DocuBay TV | cc-mgr91yrk4pehy.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`DocuBayTV.in`) | ✅ |
| 1098 | CGTN Documentary | amg00405-rakutentv-cgtndocumentary-rakuten-0ql8j.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`CGTNDocumentary.cn`) | ✅ |
| 1099 | National Geographic | 138.121.15.230 | 🌐 ציבורי (iptv-org) | iptv-org (`NationalGeographic.mx`) | ❌ רק באפליקציה (HTTP) |
| 1100 | DocuBox | Antik Telecom (סלובקיה), נתיבי test | ⚠️ ממסר צד שלישי | iptv-org (`DocuBox.nl`) | ✅ |
| 1101 | Polsat Viasat History | 185.227.34.179 | 🌐 ציבורי (iptv-org) | iptv-org (`PolsatViasatHistory.pl`) | ❌ רק באפליקציה (HTTP) |
| 1102 | Pluto TV Dokumentar | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVDokumentar.se`) | ✅ |
| 1103 | CNA Originals | amg01082-cna-amg01082c1-rlaxx-us-11304.playouts.now.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`CNAOriginals.sg`) | ✅ |
| 1104 | Karbala Documentary | ktvlive.online | 🌐 ציבורי (iptv-org) | iptv-org (`KarbalaDocumentary.iq`) | ✅ |
| 1105 | Ecovision | streaming.grupomediosdelnorte.com | 🌐 ציבורי (iptv-org) | iptv-org (`Ecovision.do`) | ✅ |
| 1106 | UMSA TVU Internacional | edge.enhdtv.com | 🌐 ציבורי (iptv-org) | iptv-org (`UMSATVUInternacional.bo`) | ✅ |
| 1107 | MWD Documentary | 203.81.84.130 | 🌐 ציבורי (iptv-org) | iptv-org (`MWDDocumentary.mm`) | ❌ רק באפליקציה (HTTP) |
| 1108 | Deadly Women | d1mgh147xpmgs8.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`DeadlyWomen.us`) | ✅ |
| 1109 | Into Crime | amg00711-zylo-amg00711c10-rakuten-fr-6731.playouts.now.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`IntoCrime.fr`) | ✅ |
| 1110 | Autentic History | 9e754fa707344ccca6d84955c8fcaf36.mediatailor.us-east-1.amazonaws.com | 🌐 ציבורי (iptv-org) | iptv-org (`AutenticHistory.de`) | ✅ |
| 1111 | Historia Y Vida | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`HistoriaYVida.es`) | ✅ |
| 1112 | Smotrim 100% Fakty | Smotrim (VGTRK) – הבעלים של קרוסלה | ✅ רשמי | iptv-org (`Smotrim100Fakty.ru`) | ✅ |
| 1113 | NatureTime | bamusa-naturetime-emea-eng-rakuten.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`NatureTime.ca`) | ✅ |
| 1114 | Epic Crimes | cc-wsuyg2uxeak04.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`EpicCrimes.in`) | ✅ |
| 1115 | Discovering China | fastlive.cctvplus.com | 🌐 ציבורי (iptv-org) | iptv-org (`DiscoveringChina.cn`) | ✅ |
| 1116 | History | 92.114.85.87 | 🌐 ציבורי (iptv-org) | iptv-org (`History.pl`) | ❌ רק באפליקציה (HTTP) |
| 1117 | Pluto TV Varldskrigen | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVVarldskrigen.se`) | ✅ |
| 1118 | Documentary+ | ef79b15c8c7c46c7a9de9d33001dbd07.mediatailor.us-west-2.amazonaws.com | 🌐 ציבורי (iptv-org) | iptv-org (`DocumentaryPlus.us`) | ✅ |
| 1119 | Persiana Docs | scihls.persiana.live | 🌐 ציבורי (iptv-org) | iptv-org (`PersianaDocs.fr`) | ✅ |
| 1120 | Dokusat | muc3.iptv-playoutcenter.de | 🌐 ציבורי (iptv-org) | iptv-org (`Dokusat.de`) | ✅ |
| 1121 | Storie Criminali | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`StorieCriminali.es`) | ✅ |
| 1122 | Nano | 185.23.80.23 | 🌐 ציבורי (iptv-org) | iptv-org (`Nano.ru`) | ❌ רק באפליקציה (HTTP) |
| 1123 | Animal Planet | drk6xq0vhn.gpcdn.net | 🌐 ציבורי (iptv-org) | iptv-org (`AnimalPlanet.in`) | ✅ |
| 1124 | CCTV-9 | xykt-fix.github.io | 🌐 ציבורי (iptv-org) | iptv-org (`CCTV9.cn`) | ✅ |
| 1125 | National Geographic | 92.114.85.87 | 🌐 ציבורי (iptv-org) | iptv-org (`NationalGeographic.pl`) | ❌ רק באפליקציה (HTTP) |
| 1126 | Smithsonian Channel Selects | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`SmithsonianChannelSelects.se`) | ✅ |
| 1127 | The Nest | fast-channels.sinclairstoryline.com | 🌐 ציבורי (iptv-org) | iptv-org (`TheNest.us`) | ✅ |
| 1128 | Persiana Travel | ptravelhls.persiana.live | 🌐 ציבורי (iptv-org) | iptv-org (`PersianaTravel.fr`) | ✅ |
| 1129 | Terra Mater WILD | 53ff3982.wurl.com | 🌐 ציבורי (iptv-org) | iptv-org (`TerraMaterWILD.de`) | ✅ |
| 1130 | Investigation Discovery | drk6xq0vhn.gpcdn.net | 🌐 ציבורי (iptv-org) | iptv-org (`InvestigationDiscovery.in`) | ✅ |
| 1131 | CCTV-Weapon & Technology | 38.75.136.137 | 🌐 ציבורי (iptv-org) | iptv-org (`CCTVWeaponTechnology.cn`) | ❌ רק באפליקציה (HTTP) |
| 1132 | National Geographic Wild | 92.114.85.87 | 🌐 ציבורי (iptv-org) | iptv-org (`NationalGeographicWild.pl`) | ❌ רק באפליקציה (HTTP) |
| 1133 | Pluto TV Alien Invasion | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVAlienInvasion.se`) | ✅ |
| 1134 | National Geographic Latin America | barinaslive1.boldmss.com | 🌐 ציבורי (iptv-org) | iptv-org (`NationalGeographicLatinAmerica.us`) | ✅ |
| 1135 | Pluto TV Histoire | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVHistoire.de`) | ✅ |
| 1136 | Safari TV | mumt04.tangotv.in | 🌐 ציבורי (iptv-org) | iptv-org (`SafariTV.in`) | ✅ |
| 1137 | Polsat Viasat Explore | 92.114.85.87 | 🌐 ציבורי (iptv-org) | iptv-org (`PolsatViasatExplore.pl`) | ❌ רק באפליקציה (HTTP) |
| 1138 | Pluto TV Space | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVSpace.se`) | ✅ |
| 1139 | Crime 360 | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`Crime360.us`) | ✅ |
| 1140 | Pluto TV Historia | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVHistoria.de`) | ✅ |
| 1141 | Polsat Viasat Nature | 92.114.85.87 | 🌐 ציבורי (iptv-org) | iptv-org (`PolsatViasatNature.pl`) | ❌ רק באפליקציה (HTTP) |
| 1142 | Pluto TV Verdenskrigene | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVVerdenskrigene.se`) | ✅ |
| 1143 | Get.factual | d1nhni5l2n8hjt.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`Getfactual.us`) | ✅ |
| 1144 | kabel eins Doku | s6.hopslan.com | 🌐 ציבורי (iptv-org) | iptv-org (`kabeleinsDoku.de`) | ✅ |
| 1145 | History Asia | freem3u.xyz | 🌐 ציבורי (iptv-org) | iptv-org (`HistoryAsia.us`) | ✅ |
| 1146 | Pluto TV Alien Invasion | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVAlienInvasion.de`) | ✅ |
| 1147 | Law & Crime | a-cdn.klowdtv.com | 🌐 ציבורי (iptv-org) | iptv-org (`LawCrime.us`) | ✅ |
| 1148 | Pluto TV Britain at War | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVBritainatWar.de`) | ✅ |
| 1149 | Atlas | cdn.whiplash.cc | 🌐 ציבורי (iptv-org) | iptv-org (`Atlas.us`) | ✅ |
| 1150 | Pluto TV Conspiracy | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVConspiracy.de`) | ✅ |
| 1151 | FBI Files | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`FBIFiles.us`) | ✅ |
| 1152 | Pluto TV Animaux | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVAnimaux.de`) | ✅ |
| 1153 | Ink Master | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`InkMaster.us`) | ✅ |
| 1154 | Pluto TV Aventure | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVAventure.de`) | ✅ |

## 📺 טיולים והרפתקאות (Travel)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 208 | ערוץ החופשות והנופש HD | ערוץ FAST ישראלי – AWS CloudFront | ✅ FAST חינמי | iptv-org (`VacationChannel.il`) | ✅ |
| 209 | ערוץ הטיולים Travel Channel | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`TravelChannel.ru`) | ❌ רק באפליקציה (HTTP) |
| 1155 | AKC TV | broadcast.blivenyc.com | 🌐 ציבורי (iptv-org) | iptv-org (`AKCTV.us`) | ✅ |
| 1156 | Horse & Country TV | hnc-free-viewlift.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`HorseCountryTV.uk`) | ✅ |
| 1157 | Voyages & Saveurs | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`VoyagesSaveurs.es`) | ✅ |
| 1158 | Peer TV South Tyrol | iptv.peer.biz | 🌐 ציבורי (iptv-org) | iptv-org (`PeerTVSouthTyrol.it`) | ✅ |
| 1159 | Dikij | stream3.cinerama.uz | 🌐 ציבורי (iptv-org) | iptv-org (`Dikij.ru`) | ✅ |
| 1160 | Rybalka | dash2.antik.sk | 🌐 ציבורי (iptv-org) | iptv-org (`Rybalka.ua`) | ✅ |
| 1161 | Love Nature | aegis-cloudfront-1.tubi.video | 🌐 ציבורי (iptv-org) | iptv-org (`LoveNature.ca`) | ✅ |
| 1162 | China Travel | fastlive.cctvplus.com | 🌐 ציבורי (iptv-org) | iptv-org (`ChinaTravel.cn`) | ✅ |
| 1163 | Terra Viva | 9ada494d.wurl.com | 🌐 ציבורי (iptv-org) | iptv-org (`TerraViva.br`) | ✅ |
| 1164 | Ciudad Magica | lbgo.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`CiudadMagica.ar`) | ✅ |
| 1165 | INTRAVEL | amg00861-amg00861c10-rakuten-uk-3152.playouts.now.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`INTRAVEL.nl`) | ✅ |
| 1166 | Swamiji TV American | stream.swamiji.tv | 🌐 ציבורי (iptv-org) | iptv-org (`SwamijiTVAmerican.at`) | ✅ |
| 1167 | Travel Channel | 92.114.85.87 | 🌐 ציבורי (iptv-org) | iptv-org (`TravelChannel.pl`) | ❌ רק באפליקציה (HTTP) |
| 1168 | Agro TV | agrotv.streamnet.ro | 🌐 ציבורי (iptv-org) | iptv-org (`AgroTV.ro`) | ✅ |
| 1169 | El Mundo del Campo | movil.ejeserver.com | 🌐 ציבורי (iptv-org) | iptv-org (`ElMundodelCampo.co`) | ✅ |
| 1170 | Discover Pakistan | livecdn.live247stream.com | 🌐 ציבורי (iptv-org) | iptv-org (`DiscoverPakistan.pk`) | ✅ |
| 1171 | WildEarth | dqga3jatxofgx.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`WildEarth.za`) | ✅ |
| 1172 | Agrotendencia TV | 5fc584f3f19c9.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`AgrotendenciaTV.cr`) | ✅ |
| 1173 | Travel TV | streamer103.neterra.tv | 🌐 ציבורי (iptv-org) | iptv-org (`TravelTV.bg`) | ✅ |
| 1174 | Aruba.TV | cdn01.setar.aw | 🌐 ציבורי (iptv-org) | iptv-org (`ArubaTV.aw`) | ✅ |
| 1175 | OEPM TV | cnn.hostlagarto.com | 🌐 ציבורי (iptv-org) | iptv-org (`OEPMTV.do`) | ✅ |
| 1176 | Productiva TV | copacoradios.desdeparaguay.net | 🌐 ציבורי (iptv-org) | iptv-org (`ProductivaTV.py`) | ✅ |
| 1177 | Sun Channel | 168.196.127.137 | 🌐 ציבורי (iptv-org) | iptv-org (`SunChannel.ve`) | ❌ רק באפליקציה (HTTP) |
| 1178 | TV OFF | edge.enhdtv.com | 🌐 ציבורי (iptv-org) | iptv-org (`TVOFF.bo`) | ✅ |
| 1179 | ARDB TV | ardbtv.ardb.com.kh | 🌐 ציבורי (iptv-org) | iptv-org (`ARDBTV.kh`) | ✅ |
| 1180 | DroneTV | airvuz-dronetv-1-us.ohlscdn.wurl.tv | 🌐 ציבורי (iptv-org) | iptv-org (`DroneTV.us`) | ✅ |
| 1181 | Space Live powered by sen | linear-1224.frequency.stream | 🌐 ציבורי (iptv-org) | iptv-org (`SpaceLivepoweredbysen.uk`) | ✅ |
| 1182 | Dikaya okhota | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Dikayaokhota.ru`) | ❌ רק באפליקציה (HTTP) |
| 1183 | CCTV-17 | 74.91.26.218 | 🌐 ציבורי (iptv-org) | iptv-org (`CCTV17.cn`) | ❌ רק באפליקציה (HTTP) |
| 1184 | AgroCanal | aovivo.equipea.com.br | 🌐 ציבורי (iptv-org) | iptv-org (`AgroCanal.br`) | ✅ |
| 1185 | Swamiji TV Australian | stream.swamiji.tv | 🌐 ציבורי (iptv-org) | iptv-org (`SwamijiTVAustralian.at`) | ✅ |
| 1186 | Travel Mix | hls02ns.antenaplay.ro | 🌐 ציבורי (iptv-org) | iptv-org (`TravelMix.ro`) | ✅ |
| 1187 | UMC TV | streaming5.globalhostla.com | 🌐 ציבורי (iptv-org) | iptv-org (`UMCTV.co`) | ✅ |
| 1188 | Costa Rica Channel | video0.rogohosting.com | 🌐 ציבורי (iptv-org) | iptv-org (`CostaRicaChannel.cr`) | ✅ |
| 1189 | Agro TV | restr2.bgtv.bg | 🌐 ציבורי (iptv-org) | iptv-org (`AgroTV.bg`) | ✅ |
| 1190 | Duck Hunting TV | main.duckhunting.playout.vju.tv | 🌐 ציבורי (iptv-org) | iptv-org (`DuckHuntingTV.us`) | ✅ |
| 1191 | Dikaya rybalka | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Dikayarybalka.ru`) | ❌ רק באפליקציה (HTTP) |
| 1192 | Guangxi Variety & Travel Channel | 111.59.24.227 | 🌐 ציבורי (iptv-org) | iptv-org (`GuangxiVarietyTravelChannel.cn`) | ❌ רק באפליקציה (HTTP) |
| 1193 | Canal Rural | 170.83.49.66 | 🌐 ציבורי (iptv-org) | iptv-org (`CanalRural.br`) | ❌ רק באפליקציה (HTTP) |
| 1194 | Swamiji TV European | stream.swamiji.tv | 🌐 ציבורי (iptv-org) | iptv-org (`SwamijiTVEuropean.at`) | ✅ |
| 1195 | Outdoor America | d1e354daam8g5r.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`OutdoorAmerica.us`) | ✅ |
| 1196 | Moya stikhiya | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Moyastikhiya.ru`) | ❌ רק באפליקציה (HTTP) |
| 1197 | TV Parana Turismo | 200.189.113.201 | 🌐 ציבורי (iptv-org) | iptv-org (`TVParanaTurismo.br`) | ❌ רק באפליקציה (HTTP) |
| 1198 | Schladming-Dachstein TV | m317.video-stream-hosting.de | 🌐 ציבורי (iptv-org) | iptv-org (`SchladmingDachsteinTV.at`) | ✅ |
| 1199 | Pocono Television Network | dfoiz3dv1euv7.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`PoconoTelevisionNetwork.us`) | ✅ |
| 1200 | RTG TV | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`RTGTV.ru`) | ❌ רק באפליקציה (HTTP) |
| 1201 | PursuitUP | d1p1siomv3cm7b.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`PursuitUP.us`) | ✅ |
| 1202 | Rybolov | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Rybolov.ru`) | ❌ רק באפליקציה (HTTP) |
| 1203 | Beach TV Florida & Alabama | 5ed325193d4e1.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`BeachTVFloridaAlabama.us`) | ✅ |
| 1204 | Vkus | 178.134.1.158 | 🌐 ציבורי (iptv-org) | iptv-org (`Vkus.ru`) | ❌ רק באפליקציה (HTTP) |
| 1205 | Beach TV Key West & Florida Keys | 5ed325193d4e1.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`BeachTVKeyWestFloridaKeys.us`) | ✅ |

## 📺 מדע ולמידה (Science)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 210 | ערוץ החקר והמדע (viju Explore) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`vijuExplore.ru`) | ❌ רק באפליקציה (HTTP) |
| 1206 | BUTV10 | cdnapisec.kaltura.com | 🌐 ציבורי (iptv-org) | iptv-org (`BUTV10.us`) | ✅ |
| 1207 | Al Hiwar TV | mn-nl.mncdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`AlHiwarTV.uk`) | ✅ |
| 1208 | Persiana Medical | phd2hls.persiana.live | 🌐 ציבורי (iptv-org) | iptv-org (`PersianaMedical.fr`) | ✅ |
| 1209 | NRWision | fms.nrwision.de | 🌐 ציבורי (iptv-org) | iptv-org (`NRWision.de`) | ✅ |
| 1210 | Platzi TV | mdstrm.com | 🌐 ציבורי (iptv-org) | iptv-org (`PlatziTV.es`) | ✅ |
| 1211 | Telepace 3 | 932y4273djv8-hls-live.mariatvcdn.it | 🌐 ציבורי (iptv-org) | iptv-org (`Telepace3.it`) | ✅ |
| 1212 | .sci-fi | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`scifi.ru`) | ❌ רק באפליקציה (HTTP) |
| 1213 | Ciftci TV | live.artidijitalmedya.com | 🌐 ציבורי (iptv-org) | iptv-org (`CiftciTV.tr`) | ✅ |
| 1214 | Discovery Science | drk6xq0vhn.gpcdn.net | 🌐 ציבורי (iptv-org) | iptv-org (`DiscoveryScience.in`) | ✅ |
| 1215 | OUN | live.knou.ac.kr | 🌐 ציבורי (iptv-org) | iptv-org (`OUN.kr`) | ✅ |
| 1216 | CCTV-World Geography | 38.75.136.137 | 🌐 ציבורי (iptv-org) | iptv-org (`CCTVWorldGeography.cn`) | ❌ רק באפליקציה (HTTP) |
| 1217 | TV UFOP | app.tvufop.com.br | 🌐 ציבורי (iptv-org) | iptv-org (`TVUFOP.br`) | ✅ |
| 1218 | TV BUAP | tvenvivo.buap.mx | 🌐 ציבורי (iptv-org) | iptv-org (`TVBUAP.mx`) | ✅ |
| 1219 | Canal Universidad | videostream.shockmedia.com.ar | 🌐 ציבורי (iptv-org) | iptv-org (`CanalUniversidad.ar`) | ✅ |
| 1220 | UTalca TV | unlimited1-us.dps.live | 🌐 ציבורי (iptv-org) | iptv-org (`UTalcaTV.cl`) | ✅ |
| 1221 | Eduvision | stmv3.voxtvhd.com.br | 🌐 ציבורי (iptv-org) | iptv-org (`Eduvision.co`) | ✅ |
| 1222 | The Indonesia Channel | restreamer.indonusa.id | 🌐 ציבורי (iptv-org) | iptv-org (`TheIndonesiaChannel.id`) | ✅ |
| 1223 | DLTV 1 | cdn-live.dltv.ac.th | 🌐 ציבורי (iptv-org) | iptv-org (`DLTV1.th`) | ✅ |
| 1224 | Can Tho TV 2 | live.canthotv.vn | 🌐 ציבורי (iptv-org) | iptv-org (`CanThoTV2.vn`) | ✅ |
| 1225 | Paighan TV | cdn4.mjunoon.tv | 🌐 ציבורי (iptv-org) | iptv-org (`PaighanTV.pk`) | ✅ |
| 1226 | TVK 2 | live.kh.malimarcdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`TVK2.kh`) | ✅ |
| 1227 | STVE Telebasica | streamingcws30.com | 🌐 ציבורי (iptv-org) | iptv-org (`STVETelebasica.hn`) | ✅ |
| 1228 | Cosmos TV | video.hostingcaaguazu.com | 🌐 ציבורי (iptv-org) | iptv-org (`CosmosTV.py`) | ✅ |
| 1229 | Educa TV | avm.ecuamedia.net | 🌐 ציבורי (iptv-org) | iptv-org (`EducaTV.ec`) | ✅ |
| 1230 | Radio Universitaria San Andres | edge.enhdtv.com | 🌐 ציבורי (iptv-org) | iptv-org (`RadioUniversitariaSanAndres.bo`) | ✅ |
| 1231 | Ryongnamsan Television | live.koryocdn.org | 🌐 ציבורי (iptv-org) | iptv-org (`RyongnamsanTelevision.kp`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 1232 | TV Maya | 5ca9af4645e15.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`TVMaya.gt`) | ✅ |
| 1233 | Yaslyk | alpha.tv.online.tm | 🌐 ציבורי (iptv-org) | iptv-org (`Yaslyk.tm`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 1234 | Create | create.lls.pbs.org | 🌐 ציבורי (iptv-org) | iptv-org (`Create.us`) | ✅ |
| 1235 | English Club TV | Cinerama.uz – ספק IPTV באוזבקיסטן | ⚠️ ממסר צד שלישי | iptv-org (`EnglishClubTV.uk`) | ✅ |
| 1236 | Curiosity Channel | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`CuriosityChannel.de`) | ✅ |
| 1237 | TRT EBA | tv-e-okul01.medya.trt.com.tr | 🌐 ציבורי (iptv-org) | iptv-org (`TRTEBA.tr`) | ✅ |
| 1238 | Gyandarshan | cdn.pishow.tv | 🌐 ציבורי (iptv-org) | iptv-org (`Gyandarshan.in`) | ✅ |
| 1239 | NBS | media.joycorp.co.kr | 🌐 ציבורי (iptv-org) | iptv-org (`NBS.kr`) | ✅ |
| 1240 | CCTV-10 | 74.91.26.218 | 🌐 ציבורי (iptv-org) | iptv-org (`CCTV10.cn`) | ❌ רק באפליקציה (HTTP) |
| 1241 | TVE RS | streaming.procergs.com.br | 🌐 ציבורי (iptv-org) | iptv-org (`TVERS.br`) | ✅ |
| 1242 | TV UG | 5ca3e84a76d30.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`TVUG.mx`) | ✅ |
| 1243 | Liceum TV | 193.225.32.62 | 🌐 ציבורי (iptv-org) | iptv-org (`LiceumTV.hu`) | ❌ רק באפליקציה (HTTP) |
| 1244 | TV Usach | stv4.janus.cl | 🌐 ציבורי (iptv-org) | iptv-org (`TVUsach.cl`) | ✅ |
| 1245 | Zoom | 138.121.15.230 | 🌐 ציבורי (iptv-org) | iptv-org (`Zoom.co`) | ❌ רק באפליקציה (HTTP) |
| 1246 | Rinjani TV | rinjanitv.cloud | 🌐 ציבורי (iptv-org) | iptv-org (`RinjaniTV.id`) | ✅ |
| 1247 | DLTV 10 | cdn-live.dltv.ac.th | 🌐 ציבורי (iptv-org) | iptv-org (`DLTV10.th`) | ✅ |
| 1248 | HTV4 | freem3u.xyz | 🌐 ציבורי (iptv-org) | iptv-org (`HTV4.vn`) | ✅ |
| 1249 | Wiki TV | stream.wikitv.asia | 🌐 ציבורי (iptv-org) | iptv-org (`WikiTV.kh`) | ✅ |
| 1250 | Campus TV | 5e85d90130e77.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`CampusTV.hn`) | ✅ |
| 1251 | Educanal | 190.128.214.97 | 🌐 ציבורי (iptv-org) | iptv-org (`Educanal.py`) | ❌ רק באפליקציה (HTTP) |
| 1252 | UNIANDES TV | video.compuwebecuador.com | 🌐 ציבורי (iptv-org) | iptv-org (`UNIANDESTV.ec`) | ✅ |
| 1253 | DKN | video.oct.dc.gov | 🌐 ציבורי (iptv-org) | iptv-org (`DKN.us`) | ✅ |
| 1254 | Wonder | lds-wonder-rakuten.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`Wonder.uk`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 1255 | iSTUFF | relay2.cdn.fem-net.de | 🌐 ציבורי (iptv-org) | iptv-org (`iSTUFF.de`) | ✅ |
| 1256 | Kite Victers | 932y4x26ljv8-hls-live.5centscdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`KiteVicters.in`) | ✅ |
| 1257 | EBS English | ebsonair.ebs.co.kr | 🌐 ציבורי (iptv-org) | iptv-org (`EBSEnglish.kr`) | ✅ |
| 1258 | CCTV-7 | 74.91.26.218 | 🌐 ציבורי (iptv-org) | iptv-org (`CCTV7.cn`) | ❌ רק באפליקציה (HTTP) |
| 1259 | Canal Educacao | canaleducacao-stream.ebc.com.br | 🌐 ציבורי (iptv-org) | iptv-org (`CanalEducacao.br`) | ✅ |
| 1260 | UAA TV | 5fe2654d6127d.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`UAATV.mx`) | ✅ |
| 1261 | GVision | videostream.chileservidores.com | 🌐 ציבורי (iptv-org) | iptv-org (`GVision.cl`) | ✅ |
| 1262 | DLTV 11 | cdn-live.dltv.ac.th | 🌐 ציבורי (iptv-org) | iptv-org (`DLTV11.th`) | ✅ |
| 1263 | One TV E-School Cambodia | 49.156.33.114 | 🌐 ציבורי (iptv-org) | iptv-org (`OneTVESchoolCambodia.kh`) | ❌ רק באפליקציה (HTTP) |
| 1264 | LA36 | reflect-channel36-la.cablecast.tv | 🌐 ציבורי (iptv-org) | iptv-org (`LA36.us`) | ✅ |
| 1265 | Vyas Channel | playhls.media.nic.in | 🌐 ציבורי (iptv-org) | iptv-org (`VyasChannel.in`) | ✅ |
| 1266 | EBS Plus 2 | ebsonair.ebs.co.kr | 🌐 ציבורי (iptv-org) | iptv-org (`EBSPlus2.kr`) | ✅ |
| 1267 | ISTV | stmv1.srvstm.com | 🌐 ציבורי (iptv-org) | iptv-org (`ISTV.br`) | ✅ |
| 1268 | ILCE Canal 15 Summa Saberes | live-ilce.ovp-vivaro.digital | 🌐 ציבורי (iptv-org) | iptv-org (`ILCECanal15SummaSaberes.mx`) | ✅ |
| 1269 | UMAGTV | tls-cl.cdnz.cl | 🌐 ציבורי (iptv-org) | iptv-org (`UMAGTV.cl`) | ✅ |
| 1270 | DLTV 12 | cdn-live.dltv.ac.th | 🌐 ציבורי (iptv-org) | iptv-org (`DLTV12.th`) | ✅ |
| 1271 | TUTV | livestream.telvue.com | 🌐 ציבורי (iptv-org) | iptv-org (`TUTV.us`) | ✅ |
| 1272 | EBS 1TV | ebsonair.ebs.co.kr | 🌐 ציבורי (iptv-org) | iptv-org (`EBS1TV.kr`) | ✅ |
| 1273 | Unisul TV | sitetv.brasilstream.com.br | 🌐 ציבורי (iptv-org) | iptv-org (`UnisulTV.br`) | ✅ |
| 1274 | Jalisco TV | video0.rogohosting.com | 🌐 ציבורי (iptv-org) | iptv-org (`JaliscoTV.mx`) | ✅ |
| 1275 | DLTV 13 | cdn-live.dltv.ac.th | 🌐 ציבורי (iptv-org) | iptv-org (`DLTV13.th`) | ✅ |

## 📺 לייף סטייל ובידור

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 211 | ערוץ החיים הטובים HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`GoodLife.il`) | ❌ רק באפליקציה (HTTP) |
| 1276 | AfroLandTV | alt-al.otteravision.com | 🌐 ציבורי (iptv-org) | iptv-org (`AfroLandTV.us`) | ✅ |
| 1277 | The Graham Norton Show | amg00654-itv-amg00654c35-rakuten-gb-7598.playouts.now.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`TheGrahamNortonShow.uk`) | ✅ |
| 1278 | Monte Carlo Digital Television | fl718492-exthls-p1.flovp.com | 🌐 ציבורי (iptv-org) | iptv-org (`MonteCarloDigitalTelevision.fr`) | ✅ |
| 1279 | Autentic Travel | cb0c87cc605942ff9766a4e6744bbadc.mediatailor.us-east-1.amazonaws.com | 🌐 ציבורי (iptv-org) | iptv-org (`AutenticTravel.de`) | ✅ |
| 1280 | Hamaika Telebista | cdn3.wowza.com | 🌐 ציבורי (iptv-org) | iptv-org (`HamaikaTelebista.es`) | ✅ |
| 1281 | 7 Gold | streamcdnr6-dddfac816632463dac50b21109dc1bb3.msvdn.net | 🌐 ציבורי (iptv-org) | iptv-org (`7Gold.it`) | ✅ |
| 1282 | Ministerstvo idey | live-minidey.cdnvideo.ru | 🌐 ציבורי (iptv-org) | iptv-org (`Ministerstvoidey.ru`) | ✅ |
| 1283 | K2 | live-tv.cloud – ספק IPTV אוקראיני | ⚠️ ממסר צד שלישי | iptv-org (`K2.ua`) | ✅ |
| 1284 | EET TV | eu.streamjo.com | 🌐 ציבורי (iptv-org) | iptv-org (`EETTV.ca`) | ✅ |
| 1285 | 10 Bold | dce3793146fef017.mediapackage.us-west-2.amazonaws.com | 🌐 ציבורי (iptv-org) | iptv-org (`10Bold.au`) | ✅ |
| 1286 | Edessa TV | canli.edessatv.com | 🌐 ציבורי (iptv-org) | iptv-org (`EdessaTV.tr`) | ✅ |
| 1287 | Asianet | da86m1sqpm3o0.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`Asianet.in`) | ✅ |
| 1288 | NHK World Premium | media-tyo.hls.nhkworld.jp | 🌐 ציבורי (iptv-org) | iptv-org (`NHKWorldPremium.jp`) | ✅ |
| 1289 | NHTV | nonghyup.flive.skcdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`NHTV.kr`) | ❌ רק באפליקציה (HTTP) |
| 1290 | Lanzhou Culture & Tourism Channel | liveplus.lzr.com.cn | 🌐 ציבורי (iptv-org) | iptv-org (`LanzhouCultureTourismChannel.cn`) | ✅ |
| 1291 | Rede NGT | isaocorp.cloudecast.com | 🌐 ציבורי (iptv-org) | iptv-org (`RedeNGT.br`) | ✅ |
| 1292 | Azteca Internacional | azt-mun.otteravision.com | 🌐 ציבורי (iptv-org) | iptv-org (`AztecaInternacional.mx`) | ✅ |
| 1293 | Metro TV | streamtv12.ddns.net | 🌐 ציבורי (iptv-org) | iptv-org (`MetroTV.ar`) | ✅ |
| 1294 | TVI Reality | raw.githubusercontent.com | 🌐 ציבורי (iptv-org) | iptv-org (`TVIReality.pt`) | ✅ |
| 1295 | MaLive | live.salto.nl | 🌐 ציבורי (iptv-org) | iptv-org (`MaLive.nl`) | ✅ |
| 1296 | Carac 1 | edge13.vedge.infomaniak.com | 🌐 ציבורי (iptv-org) | iptv-org (`Carac1.ch`) | ✅ |
| 1297 | Bialogardzka Telewizja Kablowa | bsmbialogard.pl | 🌐 ציבורי (iptv-org) | iptv-org (`BialogardzkaTelewizjaKablowa.pl`) | ✅ |
| 1298 | Netmax TV | es1.crystalweb.net | 🌐 ציבורי (iptv-org) | iptv-org (`NetmaxTV.gr`) | ✅ |
| 1299 | MTV Catfish | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`MTVCatfish.se`) | ✅ |
| 1300 | Canal Motor | digicom.hls.iptvdc.com | 🌐 ציבורי (iptv-org) | iptv-org (`CanalMotor.no`) | ✅ |
| 1301 | Jim | live-fi.tvkaista.net | 🌐 ציבורי (iptv-org) | iptv-org (`Jim.fi`) | ✅ |
| 1302 | Antena Monden | stream1.antenaplay.ro | 🌐 ציבורי (iptv-org) | iptv-org (`AntenaMonden.ro`) | ✅ |
| 1303 | Suyai TV | trun.suyaitv.cl | 🌐 ציבורי (iptv-org) | iptv-org (`SuyaiTV.cl`) | ✅ |
| 1304 | RCN Mas | rcntv-rcnmas-1-us.roku.wurl.tv | 🌐 ציבורי (iptv-org) | iptv-org (`RCNMas.co`) | ✅ |
| 1305 | AsiriTV | video2.lhdserver.es | 🌐 ציבורי (iptv-org) | iptv-org (`AsiriTV.pe`) | ✅ |
| 1306 | Salira TV | live.salira.tv | 🌐 ציבורי (iptv-org) | iptv-org (`SaliraTV.id`) | ✅ |
| 1307 | Good Idea TV | stream1.ai-net.net | 🌐 ציבורי (iptv-org) | iptv-org (`GoodIdeaTV.th`) | ❌ רק באפליקציה (HTTP) |
| 1308 | ON Vie Giai Tri | freem3u.xyz | 🌐 ציבורי (iptv-org) | iptv-org (`ONVieGiaiTri.vn`) | ✅ |
| 1309 | Hum TV | g4wlkwx8l23a-hls-live.5centscdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`HumTV.pk`) | ✅ |
| 1310 | 247 Box TV | hls.247box.live | 🌐 ציבורי (iptv-org) | iptv-org (`247BoxTV.ir`) | ✅ |
| 1311 | StoryChannel TV | 136044159.r.cdnsun.net | 🌐 ציבורי (iptv-org) | iptv-org (`StoryChannelTV.ma`) | ✅ |
| 1312 | 1KZN TV | cdn.freevisiontv.co.za | 🌐 ציבורי (iptv-org) | iptv-org (`1KZNTV.za`) | ✅ |
| 1313 | Qausain TV | acasmedia3.acangroup.org | 🌐 ציבורי (iptv-org) | iptv-org (`QausainTV.ng`) | ✅ |
| 1314 | Bellavision | fox.hostlagarto.com | 🌐 ציבורי (iptv-org) | iptv-org (`Bellavision.do`) | ✅ |
| 1315 | Aguacate TV | stream.aguacatetv.com | 🌐 ציבורי (iptv-org) | iptv-org (`AguacateTV.ve`) | ✅ |
| 1316 | La Nueva Radio TV 97.7 | sv72.ecuaradiotv.net | 🌐 ציבורי (iptv-org) | iptv-org (`LaNuevaRadioTV977.ec`) | ✅ |
| 1317 | KIX | freem3u.xyz | 🌐 ציבורי (iptv-org) | iptv-org (`KIX.hk`) | ✅ |
| 1318 | ¡OPA! | 5fc584f3f19c9.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`OPA.cr`) | ✅ |
| 1319 | Kurdsat | hlspackager.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`Kurdsat.iq`) | ✅ |
| 1320 | MiTV | rds3.desdeparaguay.net | 🌐 ציבורי (iptv-org) | iptv-org (`MiTV.py`) | ✅ |
| 1321 | EDN TV | 60417ddeaf0d9.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`EDNTV.hn`) | ✅ |
| 1322 | Code Fashion TV | cdn2.invivo.bg | 🌐 ציבורי (iptv-org) | iptv-org (`CodeFashionTV.bg`) | ✅ |
| 1323 | BTV | streamfi-alphadgtl1.zettawiseroutes.com | 🌐 ציבורי (iptv-org) | iptv-org (`BTV.ug`) | ✅ |
| 1324 | Belarus-2 | ngtrk.dc.beltelecom.by | 🌐 ציבורי (iptv-org) | iptv-org (`Belarus2.by`) | ✅ |
| 1325 | ETV+ | sb.err.ee | 🌐 ציבורי (iptv-org) | iptv-org (`ETVPlus.ee`) | ✅ |
| 1326 | BIZ TV | fl.biztv.media | 🌐 ציבורי (iptv-org) | iptv-org (`BIZTV.uz`) | ✅ |
| 1327 | Imai TV | live20.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`ImaiTV.lk`) | ✅ |
| 1328 | Ning TV | livefta.malimarcdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`NingTV.la`) | ✅ |
| 1329 | Shift TV | hls.shift.tv | 🌐 ציבורי (iptv-org) | iptv-org (`ShiftTV.hr`) | ✅ |
| 1330 | KC2 | 5c46fa289c89f.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`KC2.rw`) | ✅ |
| 1331 | Med.TV | med-tv-it.fast.rakuten.tv | 🌐 ציבורי (iptv-org) | iptv-org (`MedTV.mt`) | ✅ |
| 1332 | OTV | 5dcabf026b188.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`OTV.gh`) | ✅ |
| 1333 | RTNB Divertissement | tv2-stream.rtnb.bi | 🌐 ציבורי (iptv-org) | iptv-org (`RTNBDivertissement.bi`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 1334 | Superyacht TV | sy.wns.live | 🌐 ציבורי (iptv-org) | iptv-org (`SuperyachtTV.mc`) | ✅ |
| 1335 | Trishul Broadcasting Network | live.suricloud.com | 🌐 ציבורי (iptv-org) | iptv-org (`TrishulBroadcastingNetwork.sr`) | ✅ |
| 1336 | KTV Arabe | kwtktvata.cdn.mangomolo.com | 🌐 ציבורי (iptv-org) | iptv-org (`KTVArabe.kw`) | ✅ |
| 1337 | Oman TV Cultural | partwota.cdn.mgmlcdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`OmanTVCultural.om`) | ✅ |
| 1338 | Afroculture TV | streamtv.cmediahosthosting.net | 🌐 ציבורי (iptv-org) | iptv-org (`AfrocultureTV.ci`) | ✅ |
| 1339 | D3 TV | lbgo.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`D3TV.ml`) | ✅ |
| 1340 | EVI TV | mistserver.evi-tv.com | 🌐 ציבורי (iptv-org) | iptv-org (`EVITV.cd`) | ✅ |
| 1341 | PLEX TV | connectiktv.ddns.net | 🌐 ציבורי (iptv-org) | iptv-org (`PLEXTV.cm`) | ❌ רק באפליקציה (HTTP) |
| 1342 | America TeVe | live.gideo.video | 🌐 ציבורי (iptv-org) | iptv-org (`AmericaTeVe.pr`) | ✅ |
| 1343 | TVCARiB Latino | cdn.mycloudstream.io | 🌐 ציבורי (iptv-org) | iptv-org (`TVCARiBLatino.sx`) | ✅ |
| 1344 | UCL | livedelta.cdn.antel.net.uy | 🌐 ציבורי (iptv-org) | iptv-org (`UCL.uy`) | ✅ |
| 1345 | Abai TV | abaitv-stream.qazcdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`AbaiTV.kz`) | ✅ |
| 1346 | ANT1 Cyprus | l2.cloudskep.com | 🌐 ציבורי (iptv-org) | iptv-org (`ANT1Cyprus.cy`) | ❌ רק באפליקציה (HTTP) |
| 1347 | Bonaire.tv | media.streambrothers.com | 🌐 ציבורי (iptv-org) | iptv-org (`Bonairetv.bq`) | ✅ |
| 1348 | Bonce TV | seswa.bonce.tv | 🌐 ציבורי (iptv-org) | iptv-org (`BonceTV.cw`) | ✅ |
| 1349 | eldo.TV | stream.eldo.lu | 🌐 ציבורי (iptv-org) | iptv-org (`eldoTV.lu`) | ✅ |
| 1350 | Lietuvos Rytas TV | live.lietuvosryto.tv | 🌐 ציבורי (iptv-org) | iptv-org (`LietuvosRytasTV.lt`) | ✅ |
| 1351 | Maasranga TV | tvsen5.aynaott.com | 🌐 ציבורי (iptv-org) | iptv-org (`MaasrangaTV.bd`) | ✅ |
| 1352 | Miras | alpha.tv.online.tm | 🌐 ציבורי (iptv-org) | iptv-org (`Miras.tm`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 1353 | Nos Isla TV | cdn01.setar.aw | 🌐 ציבורי (iptv-org) | iptv-org (`NosIslaTV.aw`) | ✅ |
| 1354 | Ray TV HD | streaming.tvnepal.com | 🌐 ציבורי (iptv-org) | iptv-org (`RayTVHD.np`) | ✅ |
| 1355 | RTB 3 | edge13.vedge.infomaniak.com | 🌐 ציבורי (iptv-org) | iptv-org (`RTB3.bf`) | ✅ |
| 1356 | TBC2 | stream-134630.castr.net | 🌐 ציבורי (iptv-org) | iptv-org (`TBC2.tz`) | ✅ |
| 1357 | TV Vita | Antik Telecom (סלובקיה), נתיבי test | ⚠️ ממסר צד שלישי | iptv-org (`TVVita.sk`) | ✅ |
| 1358 | TVBS-Asia | 38.64.72.148 | 🌐 ציבורי (iptv-org) | iptv-org (`TVBSAsia.tw`) | ❌ רק באפליקציה (HTTP) |
| 1359 | AMG TV | 2-fss-2.streamhoster.com | 🌐 ציבורי (iptv-org) | iptv-org (`AMGTV.us`) | ✅ |
| 1360 | Hobby Maker | lo3.gemporia.com | 🌐 ציבורי (iptv-org) | iptv-org (`HobbyMaker.uk`) | ✅ |
| 1361 | FashionTV Paris L'Original | edge-fast3.evrideo.tv | 🌐 ציבורי (iptv-org) | iptv-org (`FashionTVParisLOriginal.fr`) | ✅ |
| 1362 | Motorvision | mvg-mv-xumo.otteravision.com | 🌐 ציבורי (iptv-org) | iptv-org (`Motorvision.de`) | ✅ |
| 1363 | Historia | d1k3vzh2ivy22k.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`Historia.es`) | ✅ |
| 1364 | Discovery Channel | streaming.aurora.enhanced.live | 🌐 ציבורי (iptv-org) | iptv-org (`DiscoveryChannel.it`) | ✅ |
| 1365 | Russia-K | Smotrim (VGTRK) – הבעלים של קרוסלה | ✅ רשמי | iptv-org (`RussiaK.ru`) | ✅ |
| 1366 | 36.6 TV | dash2.antik.sk | 🌐 ציבורי (iptv-org) | iptv-org (`366TV.ua`) | ✅ |
| 1367 | ZenLIFE by Stingray | lotus.stingray.com | 🌐 ציבורי (iptv-org) | iptv-org (`ZenLIFEbyStingray.ca`) | ✅ |
| 1368 | Fortuna TV | edge.taksimbilisim.com | 🌐 ציבורי (iptv-org) | iptv-org (`FortunaTV.tr`) | ✅ |
| 1369 | Colors Kannada | da86m1sqpm3o0.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`ColorsKannada.in`) | ✅ |
| 1370 | CCTV-Health | 38.75.136.137 | 🌐 ציבורי (iptv-org) | iptv-org (`CCTVHealth.cn`) | ❌ רק באפליקציה (HTTP) |
| 1371 | Conecta Mais TV | adylnet.streampool.net | 🌐 ציבורי (iptv-org) | iptv-org (`ConectaMaisTV.br`) | ✅ |
| 1372 | Capital 21 | video.cdmx.gob.mx | 🌐 ציבורי (iptv-org) | iptv-org (`Capital21.mx`) | ✅ |
| 1373 | Power Max Radio TV | videostream.shockmedia.com.ar | 🌐 ציבורי (iptv-org) | iptv-org (`PowerMaxRadioTV.ar`) | ✅ |
| 1374 | SBS6 Classics | sbs6classic.prd1.talpatvcdn.nl | 🌐 ציבורי (iptv-org) | iptv-org (`SBS6Classics.nl`) | ✅ |
| 1375 | Carac 2 | onefmhd.vedge.infomaniak.com | 🌐 ציבורי (iptv-org) | iptv-org (`Carac2.ch`) | ✅ |
| 1376 | Radiowa Czworka | stream14.polskieradio.pl | 🌐 ציבורי (iptv-org) | iptv-org (`RadiowaCzworka.pl`) | ✅ |
| 1377 | Star Channel International | livestar.siliconweb.com | 🌐 ציבורי (iptv-org) | iptv-org (`StarChannelInternational.gr`) | ✅ |
| 1378 | MTV Dating | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`MTVDating.se`) | ✅ |
| 1379 | Liv | live-fi.tvkaista.net | 🌐 ציבורי (iptv-org) | iptv-org (`Liv.fi`) | ✅ |
| 1380 | TVR Cultural | tvr-cultural.lg.mncdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`TVRCultural.ro`) | ✅ |
| 1381 | Canal Chilote | videostream.chileservidores.com | 🌐 ציבורי (iptv-org) | iptv-org (`CanalChilote.cl`) | ✅ |
| 1382 | RCN Novelas | cdnlive.klicgo.net | 🌐 ציבורי (iptv-org) | iptv-org (`RCNNovelas.co`) | ✅ |
| 1383 | ON Life | freem3u.xyz | 🌐 ציבורי (iptv-org) | iptv-org (`ONLife.vn`) | ✅ |
| 1384 | A-Plus TV | cdn4.mjunoon.tv | 🌐 ציבורי (iptv-org) | iptv-org (`APlusTV.pk`) | ✅ |
| 1385 | OXIR TV | hls.oxir.live | 🌐 ציבורי (iptv-org) | iptv-org (`OXIRTV.ir`) | ✅ |
| 1386 | Soweto TV | cdn.freevisiontv.co.za | 🌐 ציבורי (iptv-org) | iptv-org (`SowetoTV.za`) | ✅ |
| 1387 | Superscreen TV | video1.getstreamhosting.com | 🌐 ציבורי (iptv-org) | iptv-org (`SuperscreenTV.ng`) | ✅ |
| 1388 | Canal 17 RTVD | cdn.protvradiostream.com | 🌐 ציבורי (iptv-org) | iptv-org (`Canal17RTVD.do`) | ✅ |
| 1389 | Inter TV | streamtv.intervenhosting.net | 🌐 ציבורי (iptv-org) | iptv-org (`InterTV.ve`) | ✅ |
| 1390 | Ciracu TV | cloud37.ecuatel.com | 🌐 ציבורי (iptv-org) | iptv-org (`CiracuTV.ec`) | ✅ |
| 1391 | Asian Drama | 103.172.187.30 | 🌐 ציבורי (iptv-org) | iptv-org (`AsianDrama.hk`) | ❌ רק באפליקציה (HTTP) |
| 1392 | TeleMas | acceso.mediosdecostarica.com | 🌐 ציבורי (iptv-org) | iptv-org (`TeleMas.cr`) | ✅ |
| 1393 | EmanTv | avr.host247.net | 🌐 ציבורי (iptv-org) | iptv-org (`EmanTv.iq`) | ✅ |
| 1394 | Oasis TV | video.hostingcaaguazu.com | 🌐 ציבורי (iptv-org) | iptv-org (`OasisTV.py`) | ✅ |
| 1395 | Girasol TV | s.emisoras.tv | 🌐 ציבורי (iptv-org) | iptv-org (`GirasolTV.hn`) | ✅ |
| 1396 | Code Health TV | cdn2.invivo.bg | 🌐 ציבורי (iptv-org) | iptv-org (`CodeHealthTV.bg`) | ✅ |
| 1397 | FORT TV | fort.co-works.org | 🌐 ציבורי (iptv-org) | iptv-org (`FORTTV.ug`) | ✅ |
| 1398 | Belarus-3 | ngtrk.dc.beltelecom.by | 🌐 ציבורי (iptv-org) | iptv-org (`Belarus3.by`) | ✅ |
| 1399 | NW Magazine | hls.newworldtv.com | 🌐 ציבורי (iptv-org) | iptv-org (`NWMagazine.tg`) | ✅ |
| 1400 | Cheksiz TV | fl.biztv.media | 🌐 ציבורי (iptv-org) | iptv-org (`CheksizTV.uz`) | ✅ |
| 1401 | Hiru TV | tv.hiruhost.com | 🌐 ציבורי (iptv-org) | iptv-org (`HiruTV.lk`) | ✅ |
| 1402 | Vati Lao | livefta.malimarcdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`VatiLao.la`) | ✅ |
| 1403 | TV Nova | stream.agatin.hr | 🌐 ציבורי (iptv-org) | iptv-org (`TVNova.hr`) | ✅ |
| 1404 | AsianCrush | linear-900.frequency.stream | 🌐 ציבורי (iptv-org) | iptv-org (`AsianCrush.us`) | ✅ |
| 1405 | Top Gear | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`TopGear.uk`) | ✅ |

## 📺 מסורת וקהילה

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 212 | ערוץ הידברות HD | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | iptv-org (`Hidabroot.il`) | ✅ |
| 213 | הלא TV HD (Hala TV) | פאנט – הלא TV (רשמי) | ✅ רשמי | iptv-org (`HalaTV.il`) | ✅ |
| 214 | ערוץ קבלה לעם | קבלה לעם – שרת רשמי | ✅ רשמי | iptv-org (`KabbalahforthePeopleIsrael.il`) | ✅ |
| 215 | שלנו TV HD (Shelanu TV) | שלנו TV – CDN77 | ✅ רשמי | iptv-org (`ShelanuTV.il`) | ✅ |
| 216 | ערוץ 16 ישראל | ערוץ 16 – שרת רשמי | ✅ רשמי | iptv-org (`Channel16.il`) | ✅ |
| 217 | JLTV טלוויזיה יהודית | jlt-jltv-xumo.otteravision.com | ⚠️ ממסר צד שלישי | iptv-org (`JewishLifeTelevision.us`) | ✅ |
| 218 | קבלה לעם – רוסית | קבלה לעם – השרת של הערוץ | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |

## 📺 ערוצים בינלאומיים (World)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 219 | DW News English HD | DW – Akamai (רשמי) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 220 | Al Jazeera English HD | אל-ג'זירה – שרת רשמי | ✅ רשמי | iptv-org (`AlJazeera.qa`) | ✅ |
| 221 | BFM TV HD (France) | BFM – SFR (רשמי) | ✅ רשמי | iptv-org (`BFMTV.fr`) | ✅ |
| 222 | BFM Tech & Co HD | BFM – SFR (רשמי) | ✅ רשמי | iptv-org (`BFMTechCo.fr`) | ✅ |
| 1436 | ABC | amg01942-amg01942c6-stirr-us-10178.playouts.now.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`ABC.us`) | ✅ |
| 1437 | Brit Asia TV | hls.autopo.st | 🌐 ציבורי (iptv-org) | iptv-org (`BritAsiaTV.uk`) | ✅ |
| 1438 | 20 Minutes TV | live-20minutestv.digiteka.com | 🌐 ציבורי (iptv-org) | iptv-org (`20MinutesTV.fr`) | ✅ |
| 1439 | Alex Berlin | alex-stream.rosebud-media.de | 🌐 ציבורי (iptv-org) | iptv-org (`AlexBerlin.de`) | ✅ |
| 1440 | Arabi TV | streamtv2.elitecomunicacion.cloud | 🌐 ציבורי (iptv-org) | iptv-org (`ArabiTV.es`) | ✅ |
| 1441 | A2i TV | stream.sen-gt.com | 🌐 ציבורי (iptv-org) | iptv-org (`A2iTV.it`) | ✅ |
| 1442 | Insayt TV | Cinerama.uz – ספק IPTV באוזבקיסטן | ⚠️ ממסר צד שלישי | iptv-org (`InsaytTV.ru`) | ✅ |
| 1443 | 1+1 Marafon | dash2.antik.sk | 🌐 ציבורי (iptv-org) | iptv-org (`1Plus1Marafon.ua`) | ✅ |
| 1444 | Wild TV | dfhsahpa45kk2.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`WildTV.ca`) | ✅ |
| 1445 | ABC TV | c.mjh.nz | 🌐 ציבורי (iptv-org) | iptv-org (`ABCTV.au`) | ✅ |
| 1446 | Ajman TV | cdn1.logichost.in | 🌐 ציבורי (iptv-org) | iptv-org (`AjmanTV.ae`) | ✅ |
| 1447 | Al Rayyan Old TV | alrayyancdn.vidgyor.com | 🌐 ציבורי (iptv-org) | iptv-org (`AlRayyanOldTV.qa`) | ✅ |
| 1448 | Al Riyadh Radio | live.kwikmotion.com | 🌐 ציבורי (iptv-org) | iptv-org (`AlRiyadhRadio.sa`) | ✅ |
| 1449 | Watan TV | rp.tactivemedia.com | 🌐 ציבורי (iptv-org) | iptv-org (`WatanTV.eg`) | ✅ |
| 1450 | Lana TV | cdn.streamlane.tv | 🌐 ציבורי (iptv-org) | iptv-org (`LanaTV.lb`) | ✅ |
| 1451 | Elsharq TV | mn-nl.mncdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`ElsharqTV.tr`) | ✅ |
| 1452 | DD India | mumbai-edge.smartplaytv.in | 🌐 ציבורי (iptv-org) | iptv-org (`DDIndia.in`) | ✅ |
| 1453 | HLAT-DTV | 5c3639aa99149.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`HLATDTV.kr`) | ✅ |
| 1454 | Hebei TV | event.pull.hebtv.com | 🌐 ציבורי (iptv-org) | iptv-org (`HebeiTV.cn`) | ✅ |
| 1455 | Amazon Sat | amazonsat.brasilstream.com.br | 🌐 ציבורי (iptv-org) | iptv-org (`AmazonSat.br`) | ✅ |
| 1456 | 8NTV | 60417ddeaf0d9.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`8NTV.mx`) | ✅ |
| 1457 | Canal 12 Venado Tuerto | nd106.republicaservers.com | 🌐 ציבורי (iptv-org) | iptv-org (`Canal12VenadoTuerto.ar`) | ✅ |
| 1458 | SIC Alta Definicao | production-fast-sic.content.okast.tv | 🌐 ציבורי (iptv-org) | iptv-org (`SICAltaDefinicao.pt`) | ✅ |
| 1459 | TV Rain | wl.tvrain.tv | 🌐 ציבורי (iptv-org) | iptv-org (`TVRain.nl`) | ✅ |
| 1460 | Matele | live.matele.be | 🌐 ציבורי (iptv-org) | iptv-org (`Matele.be`) | ✅ |
| 1461 | Max TV | cdn.katapy.io | 🌐 ציבורי (iptv-org) | iptv-org (`MaxTV.ch`) | ✅ |
| 1462 | Landle TV | streaming13.huberwebmedia.at | 🌐 ציבורי (iptv-org) | iptv-org (`LandleTV.at`) | ✅ |
| 1463 | NewTV | 1409502578.rsc.cdn77.org | 🌐 ציבורי (iptv-org) | iptv-org (`NewTV.pl`) | ✅ |
| 1464 | Alpha TV | alphatvlive2.siliconweb.com | 🌐 ציבורי (iptv-org) | iptv-org (`AlphaTV.gr`) | ✅ |
| 1465 | MTV Reality | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`MTVReality.se`) | ✅ |
| 1466 | Frikanalen | frikanalen.no | 🌐 ציבורי (iptv-org) | iptv-org (`Frikanalen.no`) | ✅ |
| 1467 | DRTV | drlivedrtvahls.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`DRTV.dk`) | ✅ |
| 1468 | MTV Sub | live-fi.tvkaista.net | 🌐 ציבורי (iptv-org) | iptv-org (`MTVSub.fi`) | ✅ |
| 1469 | TG4 | fastly.live.brightcove.com | 🌐 ציבורי (iptv-org) | iptv-org (`TG4.ie`) | ✅ |
| 1470 | Barrandov Krimi | dash4.antik.sk | 🌐 ציבורי (iptv-org) | iptv-org (`BarrandovKrimi.cz`) | ✅ |
| 1471 | A7TV | a7tvlive.ro | 🌐 ציבורי (iptv-org) | iptv-org (`A7TV.ro`) | ✅ |
| 1472 | Hevizi TV | cloudfront44.lexanetwork.com | 🌐 ציבורי (iptv-org) | iptv-org (`HeviziTV.hu`) | ✅ |
| 1473 | Antofagasta TV | unlimited6-cl.dps.live | 🌐 ציבורי (iptv-org) | iptv-org (`AntofagastaTV.cl`) | ✅ |
| 1474 | Aupur Television | canal.aupurtv.co | 🌐 ציבורי (iptv-org) | iptv-org (`AupurTelevision.co`) | ✅ |
| 1475 | Latina | redirector.rudo.video | 🌐 ציבורי (iptv-org) | iptv-org (`Latina.pe`) | ✅ |
| 1476 | TVRI World | ott-balancer.tvri.go.id | 🌐 ציבורי (iptv-org) | iptv-org (`TVRIWorld.id`) | ✅ |
| 1477 | NBT World | cdn-edge.iiptvcdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`NBTWorld.th`) | ✅ |
| 1478 | Vietnam Today | amagi-live.ondemandkorea.com | 🌐 ציבורי (iptv-org) | iptv-org (`VietnamToday.vn`) | ✅ |
| 1479 | PMI TV | cdn.live247stream.com | 🌐 ציבורי (iptv-org) | iptv-org (`PMITV.pk`) | ✅ |
| 1480 | Asil TV | live.asil.tv | 🌐 ציבורי (iptv-org) | iptv-org (`AsilTV.ir`) | ✅ |
| 1481 | 2M Monde | d2qh3gh0k5vp3v.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`2MMonde.ma`) | ✅ |
| 1482 | TV BRICS Africa | cdn.freevisiontv.co.za | 🌐 ציבורי (iptv-org) | iptv-org (`TVBRICSAfrica.za`) | ✅ |
| 1483 | TV-WEST | tv-west.lg.mncdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`TVWEST.ng`) | ✅ |
| 1484 | A7 TV | 183.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`A7TV.do`) | ✅ |
| 1485 | Dmoda Channel | master.tucableip.com | 🌐 ציבורי (iptv-org) | iptv-org (`DmodaChannel.bo`) | ✅ |
| 1486 | Paraguay TV | stream.paraguaytv.gov.py | 🌐 ציבורי (iptv-org) | iptv-org (`ParaguayTV.py`) | ✅ |
| 1487 | MTR | cdnsk003.panaccess.com | 🌐 ציבורי (iptv-org) | iptv-org (`MTR.sk`) | ✅ |
| 1488 | Latina TV | streamtv.latinamedios.com | 🌐 ציבורי (iptv-org) | iptv-org (`LatinaTV.ve`) | ✅ |
| 1489 | America Estereo Quito | video.makrodigital.com | 🌐 ציבורי (iptv-org) | iptv-org (`AmericaEstereoQuito.ec`) | ✅ |
| 1490 | TV Sur Canal 14 | k20.usastreams.com | 🌐 ציבורי (iptv-org) | iptv-org (`TVSurCanal14.cr`) | ✅ |
| 1491 | Canal 100 Chinique | live20.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`Canal100Chinique.gt`) | ✅ |
| 1492 | Al Rasheed TV | media1.livaat.com | 🌐 ציבורי (iptv-org) | iptv-org (`AlRasheedTV.iq`) | ✅ |
| 1493 | Canal 11 | redirector.rudo.video | 🌐 ציבורי (iptv-org) | iptv-org (`Canal11.hn`) | ✅ |
| 1494 | Jordan Archive | playlist.fasttvcdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`JordanArchive.jo`) | ✅ |
| 1495 | Belarus-1 | ngtrk.dc.beltelecom.by | 🌐 ציבורי (iptv-org) | iptv-org (`Belarus1.by`) | ✅ |
| 1496 | Rajeen TV | mn-nl.mncdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`RajeenTV.ps`) | ✅ |
| 1497 | TSV | tsv.md | 🌐 ציבורי (iptv-org) | iptv-org (`TSV.md`) | ✅ |
| 1498 | Jibek Joly | stream.qazcdn.net | 🌐 ציבורי (iptv-org) | iptv-org (`JibekJoly.kz`) | ✅ |
| 1499 | Louga TV | stream.sen-gt.com | 🌐 ציבורי (iptv-org) | iptv-org (`LougaTV.sn`) | ✅ |
| 1500 | TeleOnce | live.11-cdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`TeleOnce.pr`) | ✅ |
| 1501 | Sat 7 Turk | live.artidijitalmedya.com | 🌐 ציבורי (iptv-org) | iptv-org (`Sat7Turk.cy`) | ✅ |
| 1502 | Rajdhani TV | stream.shariarsuvo.com | 🌐 ציבורי (iptv-org) | iptv-org (`RajdhaniTV.bd`) | ✅ |
| 1503 | Nasa TV | stream.nasatv.com.mk | 🌐 ציבורי (iptv-org) | iptv-org (`NasaTV.mk`) | ✅ |
| 1504 | Pannon TV | stream.nmih.hu | 🌐 ציבורי (iptv-org) | iptv-org (`PannonTV.rs`) | ✅ |
| 1505 | RTK 3 | gjirafa-video-live.gjirafa.net | 🌐 ציבורי (iptv-org) | iptv-org (`RTK3.xk`) | ✅ |
| 1506 | TV10 | 5c46fa289c89f.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`TV10.rw`) | ✅ |
| 1507 | Imedi TV | tv.cdn.xsg.ge | 🌐 ציבורי (iptv-org) | iptv-org (`ImediTV.ge`) | ✅ |
| 1508 | Turkmenistan | alpha.tv.online.tm | 🌐 ציבורי (iptv-org) | iptv-org (`Turkmenistan.tm`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 1509 | CAM 10 TV | stream.it-innov.com | 🌐 ציבורי (iptv-org) | iptv-org (`CAM10TV.cm`) | ✅ |
| 1510 | RTL Zwee | live-edge.rtl.lu | 🌐 ציבורי (iptv-org) | iptv-org (`RTLZwee.lu`) | ✅ |
| 1511 | Verbum TV | video-eu1.streamerr.co | 🌐 ציבורי (iptv-org) | iptv-org (`VerbumTV.lk`) | ✅ |
| 1512 | Vizion Plus | tringliveviz.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`VizionPlus.al`) | ✅ |
| 1513 | Kordia TV | ptvlive.kordia.net.nz | 🌐 ציבורי (iptv-org) | iptv-org (`KordiaTV.nz`) | ✅ |
| 1514 | MNB World | live.mnb.mn | 🌐 ציבורי (iptv-org) | iptv-org (`MNBWorld.mn`) | ✅ |
| 1515 | Television Caribbean | live.kingdom-tv.com | 🌐 ציבורי (iptv-org) | iptv-org (`TelevisionCaribbean.kn`) | ✅ |
| 1516 | Althania | live.kwikmotion.com | 🌐 ציבורי (iptv-org) | iptv-org (`Althania.sy`) | ✅ |
| 1517 | Ayaz TV | janya-ayaztv.vgcdn.net | 🌐 ציבורי (iptv-org) | iptv-org (`AyazTV.az`) | ✅ |
| 1518 | Armenia 2 | amtv.tulixcdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`Armenia2.am`) | ✅ |
| 1519 | SEA TV | seatv.netlinkbroadcaster.com | 🌐 ציבורי (iptv-org) | iptv-org (`SEATV.kh`) | ✅ |
| 1520 | Tamadon TV | hls.tamadon.live | 🌐 ציבורי (iptv-org) | iptv-org (`TamadonTV.af`) | ✅ |
| 1521 | Lao Net TV | livefta.malimarcdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`LaoNetTV.la`) | ✅ |
| 1522 | LTV1 | lsm.eu.cdns-redge.media | 🌐 ציבורי (iptv-org) | iptv-org (`LTV1.lv`) | ✅ |
| 1523 | Radio Televizija Banovina | pool.alter-media.hr | 🌐 ציבורי (iptv-org) | iptv-org (`RadioTelevizijaBanovina.hr`) | ✅ |
| 1524 | RTV Zenica | stream.rtvze.ba | 🌐 ציבורי (iptv-org) | iptv-org (`RTVZenica.ba`) | ✅ |
| 1525 | TVCG MNE | rtcg-live-open.morescreens.com | 🌐 ציבורי (iptv-org) | iptv-org (`TVCGMNE.me`) | ✅ |
| 1526 | Bahrain International | 5c7b683162943.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`BahrainInternational.bh`) | ✅ |
| 1527 | Ignite Television | rpn.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`IgniteTelevision.gy`) | ✅ |
| 1528 | Supreme Master TV | lbs-us1.suprememastertv.com | 🌐 ציבורי (iptv-org) | iptv-org (`SupremeMasterTV.tw`) | ✅ |
| 1529 | Radio Tele Planet Compas | 5dcab9aed5331.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`RadioTelePlanetCompas.ht`) | ✅ |
| 1530 | Tanasuh TV | kilolink.pulsarstarsolutions.com | 🌐 ציבורי (iptv-org) | iptv-org (`TanasuhTV.ly`) | ✅ |
| 1531 | ETV | edge12.vedge.infomaniak.com | 🌐 ציבורי (iptv-org) | iptv-org (`ETV.gp`) | ✅ |
| 1532 | NTV | strhlslb01.streamakaci.tv | 🌐 ציבורי (iptv-org) | iptv-org (`NTV.ci`) | ✅ |
| 1533 | TN8 | streamingcws30.com | 🌐 ציבורי (iptv-org) | iptv-org (`TN8.ni`) | ✅ |
| 1534 | Nex TV Canal 21 | movil.ejeserver.com | 🌐 ציבורי (iptv-org) | iptv-org (`NexTVCanal21.pa`) | ✅ |
| 1535 | RUV | ruv-web-live.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`RUV.is`) | ✅ |
| 1536 | TV Spravedliva Bulgariya | tv.tvsb.bg | 🌐 ציבורי (iptv-org) | iptv-org (`TVSpravedlivaBulgariya.bg`) | ✅ |
| 1537 | CBN TV | 5790d294af2dc.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`CBNTV.vg`) | ✅ |
| 1538 | Inooro TV | live-onesignal.royalmedia.co.ke | 🌐 ציבורי (iptv-org) | iptv-org (`InooroTV.ke`) | ✅ |
| 1539 | Pointe TV | tv.pointville.ag | 🌐 ציבורי (iptv-org) | iptv-org (`PointeTV.ag`) | ✅ |
| 1540 | Raajje TV | raajjetv.yarr-myn.workers.dev | 🌐 ציבורי (iptv-org) | iptv-org (`RaajjeTV.mv`) | ✅ |
| 1541 | TVJ | vod2live.univtec.com | 🌐 ציבורי (iptv-org) | iptv-org (`TVJ.jm`) | ✅ |
| 1542 | ZNS-TV | zns-edge1.streamcomedia.cloud | 🌐 ציבורי (iptv-org) | iptv-org (`ZNSTV.bs`) | ✅ |
| 1543 | El Watania 1 | sw1.tanitweb.net | 🌐 ציבורי (iptv-org) | iptv-org (`ElWatania1.tn`) | ✅ |
| 1544 | ERi-TV 1 | jmc-live.ercdn.net | 🌐 ציבורי (iptv-org) | iptv-org (`ERiTV1.er`) | ✅ |
| 1545 | KTV 1 | kwtktv1ta.cdn.mangomolo.com | 🌐 ציבורי (iptv-org) | iptv-org (`KTV1.kw`) | ✅ |
| 1546 | Oman TV | partneta.cdn.mgmlcdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`OmanTV.om`) | ✅ |
| 1547 | RASD TV | cloud.streamingconnect.tv | 🌐 ציבורי (iptv-org) | iptv-org (`RASDTV.eh`) | ✅ |
| 1548 | Sudan TV | cdn-globecast.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`SudanTV.sd`) | ✅ |
| 1549 | TV2 | 185.9.2.18 | 🌐 ציבורי (iptv-org) | iptv-org (`TV2.dz`) | ❌ רק באפליקציה (HTTP) |
| 1550 | Yemen Today TV | video.yementdy.tv | 🌐 ציבורי (iptv-org) | iptv-org (`YemenTodayTV.ye`) | ✅ |
| 1551 | Kalac TV | edge13.vedge.infomaniak.com | 🌐 ציבורי (iptv-org) | iptv-org (`KalacTV.gn`) | ✅ |
| 1552 | RTB | edge12.vedge.infomaniak.com | 🌐 ציבורי (iptv-org) | iptv-org (`RTB.bf`) | ✅ |
| 1553 | RTNB TV | tv1-stream.rtnb.bi | 🌐 ציבורי (iptv-org) | iptv-org (`RTNBTV.bi`) | ✅ |
| 1554 | Tele Peyi | edge20.vedge.infomaniak.com | 🌐 ציבורי (iptv-org) | iptv-org (`TelePeyi.gf`) | ✅ |
| 1555 | Tele Tchad | strhlslb01.streamakaci.tv | 🌐 ציבורי (iptv-org) | iptv-org (`TeleTchad.td`) | ✅ |
| 1556 | Television Espoir 47 | cdn140m.panaccess.com | 🌐 ציבורי (iptv-org) | iptv-org (`TelevisionEspoir47.tg`) | ✅ |
| 1557 | TV Monaco | production-fast-mcrtv.content.okast.tv | 🌐 ציבורי (iptv-org) | iptv-org (`TVMonaco.mc`) | ✅ |
| 1558 | Zitata TV | live.zitata.tv | 🌐 ציבורי (iptv-org) | iptv-org (`ZitataTV.mq`) | ✅ |
| 1559 | TVM | stream.tvm.co.mz | 🌐 ציבורי (iptv-org) | iptv-org (`TVM.mz`) | ✅ |
| 1560 | Andorra TV | livesg1.rtva.hiway.media | 🌐 ציבורי (iptv-org) | iptv-org (`AndorraTV.ad`) | ✅ |
| 1561 | Dodoma TV | goliveafrica.media | 🌐 ציבורי (iptv-org) | iptv-org (`DodomaTV.tz`) | ✅ |
| 1562 | ETV | sb.err.ee | 🌐 ציבורי (iptv-org) | iptv-org (`ETV.ee`) | ✅ |
| 1563 | Golden Jade | 103.172.187.30 | 🌐 ציבורי (iptv-org) | iptv-org (`GoldenJade.hk`) | ❌ רק באפליקציה (HTTP) |
| 1564 | Korean Central Television | live.koryocdn.org | 🌐 ציבורי (iptv-org) | iptv-org (`KoreanCentralTelevision.kp`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 1565 | MWD | 203.81.84.130 | 🌐 ציבורי (iptv-org) | iptv-org (`MWD.mm`) | ❌ רק באפליקציה (HTTP) |
| 1566 | NosTV Bonaire 2 | stream2.nostvbonaire.com | 🌐 ציבורי (iptv-org) | iptv-org (`NosTVBonaire2.bq`) | ✅ |
| 1567 | One | 2-fss-1.streamhoster.com | 🌐 ציבורי (iptv-org) | iptv-org (`One.mt`) | ✅ |
| 1568 | Telearuba | cdn01.setar.aw | 🌐 ציבורי (iptv-org) | iptv-org (`Telearuba.aw`) | ✅ |
| 1569 | TV Direct 13 | oqgdrog4d4rm-hls-live.5centscdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`TVDirect13.cw`) | ✅ |
| 1570 | TV Veseljak Golica HD | radio.serv.si | 🌐 ציבורי (iptv-org) | iptv-org (`TVVeseljakGolicaHD.si`) | ✅ |
| 1571 | AFTV | livestream.telvue.com | 🌐 ציבורי (iptv-org) | iptv-org (`AFTV.us`) | ✅ |
| 1572 | Spark TV | candlelightmedia-sparklightlove-4-us.roku.wurl.tv | 🌐 ציבורי (iptv-org) | iptv-org (`SparkTV.uk`) | ✅ |
| 1573 | Cannes Lerins TV | vdo2.pro-fhi.net | 🌐 ציבורי (iptv-org) | iptv-org (`CannesLerinsTV.fr`) | ✅ |
| 1574 | Allgau TV | stream01.welocal.stream | 🌐 ציבורי (iptv-org) | iptv-org (`AllgauTV.de`) | ✅ |
| 1575 | Canal San Roque | cdnlivevlc.codev8.net | 🌐 ציבורי (iptv-org) | iptv-org (`CanalSanRoque.es`) | ✅ |

## 📺 חדשות מהעולם (News)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 329 | Black News Channel | blacknewschannel-xumo-us.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`BlackNewsChannel.us`) | ✅ |
| 330 | Iran International | hlspackager.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`IranInternational.uk`) | ✅ |
| 331 | Africa 24 English | edge20.vedge.infomaniak.com | 🌐 ציבורי (iptv-org) | iptv-org (`Africa24English.fr`) | ✅ |
| 332 | DW | dwamdstream103.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`DW.de`) | ✅ |
| 333 | M95 Television Marbella | limited2.todostreaming.es | 🌐 ציבורי (iptv-org) | iptv-org (`M95TelevisionMarbella.es`) | ✅ |
| 334 | Rai News 24 | d3k8wzt41aflvx.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`RaiNews24.it`) | ✅ |
| 335 | RT | rt-glb.rttv.com | 🌐 ציבורי (iptv-org) | iptv-org (`RT.ru`) | ✅ |
| 336 | FREEDOM | dash4.antik.sk | 🌐 ציבורי (iptv-org) | iptv-org (`FREEDOM.ua`) | ✅ |
| 337 | Hamdard TV | tv.hamdardtv.com | 🌐 ציבורי (iptv-org) | iptv-org (`HamdardTV.ca`) | ✅ |
| 338 | News24 | skynewsau-live-premium.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`News24.au`) | ✅ |
| 339 | Al Arabiya Programs | live.alarabiya.net | 🌐 ציבורי (iptv-org) | iptv-org (`AlArabiyaPrograms.ae`) | ✅ |
| 340 | Al Araby TV | live.kwikmotion.com | 🌐 ציבורי (iptv-org) | iptv-org (`AlArabyTV.qa`) | ✅ |
| 341 | Al Hadath | av.alarabiya.net | 🌐 ציבורי (iptv-org) | iptv-org (`AlHadath.sa`) | ✅ |
| 342 | Al Ghad TV | eazyvwqssi.erbvr.com | 🌐 ציבורי (iptv-org) | iptv-org (`AlGhadTV.eg`) | ✅ |
| 343 | Red TV Lebanon | live.kwikmotion.com | 🌐 ציבורי (iptv-org) | iptv-org (`RedTVLebanon.lb`) | ✅ |
| 344 | TRT World | tv-trtworld.medya.trt.com.tr | 🌐 ציבורי (iptv-org) | iptv-org (`TRTWorld.tr`) | ✅ |
| 345 | CNN News 18 | n18syndication.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`CNNNews18.in`) | ✅ |
| 346 | NHK World-Japan | masterpl.hls.nhkworld.jp | 🌐 ציבורי (iptv-org) | iptv-org (`NHKWorldJapan.jp`) | ✅ |
| 347 | Arirang TV | freem3u.xyz | 🌐 ציבורי (iptv-org) | iptv-org (`ArirangTV.kr`) | ✅ |
| 348 | CGTN | amg00405-rakutentv-cgtn-rakuten-i9tar.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`CGTN.cn`) | ✅ |
| 349 | CNN Brasil | d25usgadhphvwi.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`CNNBrasil.br`) | ✅ |
| 350 | Icrtv Colima | 5fc584f3f19c9.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`IcrtvColima.mx`) | ✅ |
| 351 | Canal 26 | stream-gtlc.telecentro.net.ar | 🌐 ציבורי (iptv-org) | iptv-org (`Canal26.ar`) | ✅ |
| 352 | ARTV | playout172.livextend.cloud | 🌐 ציבורי (iptv-org) | iptv-org (`ARTV.pt`) | ✅ |
| 353 | EbS | streams.prd.commavservices.eu | 🌐 ציבורי (iptv-org) | iptv-org (`EbS.be`) | ✅ |
| 354 | Meteonews | streaming.meteonews.net | 🌐 ציבורי (iptv-org) | iptv-org (`Meteonews.ch`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 355 | Euronews | cdn-euronews.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`Euronews.pl`) | ✅ |
| 356 | Astra TV | server.gointernet.gr | 🌐 ציבורי (iptv-org) | iptv-org (`AstraTV.gr`) | ✅ |
| 357 | Miracle Channel | live.miracleonline.org | 🌐 ציבורי (iptv-org) | iptv-org (`MiracleChannel.se`) | ✅ |
| 358 | TV from the Danish Parliament | cdnapi.kaltura.com | 🌐 ציבורי (iptv-org) | iptv-org (`TVfromtheDanishParliament.dk`) | ✅ |
| 359 | Nopola News | virta2.nopolanews.fi | 🌐 ציבורי (iptv-org) | iptv-org (`NopolaNews.fi`) | ✅ |
| 360 | Current Time TV | rfe-ingest.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`CurrentTimeTV.cz`) | ✅ |
| 361 | Dotto TV | live.dottotv.ro | 🌐 ציבורי (iptv-org) | iptv-org (`DottoTV.ro`) | ✅ |
| 362 | NCTV | pantera1-100gb-cl-movistar.dps.live | 🌐 ציבורי (iptv-org) | iptv-org (`NCTV.cl`) | ✅ |
| 363 | Noticiero 90 Minutos | play.cdn.enetres.net | 🌐 ציבורי (iptv-org) | iptv-org (`Noticiero90Minutos.co`) | ✅ |
| 364 | RPP TV | redirector.rudo.video | 🌐 ציבורי (iptv-org) | iptv-org (`RPPTV.pe`) | ✅ |
| 365 | BTV | lnd0t3b922.tenbytecdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`BTV.id`) | ✅ |
| 366 | 13 Siam Thai | live.x2.co.th | 🌐 ציבורי (iptv-org) | iptv-org (`13SiamThai.th`) | ✅ |
| 367 | Dong Nai TV 1 | freem3u.xyz | 🌐 ציבורי (iptv-org) | iptv-org (`DongNaiTV1.vn`) | ✅ |
| 368 | Neo News | vodzong.mjunoon.tv | 🌐 ציבורי (iptv-org) | iptv-org (`NeoNews.pk`) | ✅ |
| 369 | Mihan TV | iptv.mihantv.com | 🌐 ציבורי (iptv-org) | iptv-org (`MihanTV.ir`) | ✅ |
| 370 | Medi1TV Arabic | cdn.live.easybroadcast.io | 🌐 ציבורי (iptv-org) | iptv-org (`Medi1TVArabic.ma`) | ✅ |
| 371 | LN24SA | cdnstack.internetmultimediaonline.org | 🌐 ציבורי (iptv-org) | iptv-org (`LN24SA.za`) | ✅ |
| 372 | Silverbird News 24 | viewmedia7219.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`SilverbirdNews24.ng`) | ✅ |
| 373 | Al Sharqiya News | indexforce.com | 🌐 ציבורי (iptv-org) | iptv-org (`AlSharqiyaNews.iq`) | ✅ |
| 374 | Carivision | ss2.tvrdomi.com | 🌐 ציבורי (iptv-org) | iptv-org (`Carivision.do`) | ✅ |
| 375 | HCH | live.streamhch.com | 🌐 ציבורי (iptv-org) | iptv-org (`HCH.hn`) | ✅ |
| 376 | CNA | live1.mediadesk.al | 🌐 ציבורי (iptv-org) | iptv-org (`CNA.al`) | ✅ |
| 377 | Halab Today TV | halabtoday-live.lg.mncdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`HalabTodayTV.sy`) | ✅ |
| 378 | Canal I | streaming.canal-i.com | 🌐 ציבורי (iptv-org) | iptv-org (`CanalI.ve`) | ✅ |
| 379 | CEACOM TV | eu1.servers10.com | 🌐 ציבורי (iptv-org) | iptv-org (`CEACOMTV.bo`) | ✅ |
| 380 | Antik Info TV | dash4.antik.sk | 🌐 ציבורי (iptv-org) | iptv-org (`AntikInfoTV.sk`) | ✅ |
| 381 | Baku.TV | rtmp.baku.tv | 🌐 ציבורי (iptv-org) | iptv-org (`BakuTV.az`) | ✅ |
| 382 | KTV 2 | kwtktv2ta.cdn.mangomolo.com | 🌐 ציבורי (iptv-org) | iptv-org (`KTV2.kw`) | ✅ |
| 383 | NW Info 2 | hls.newworldtv.com | 🌐 ציבורי (iptv-org) | iptv-org (`NWInfo2.tg`) | ✅ |
| 384 | Belarus-24 | ngtrk.dc.beltelecom.by | 🌐 ציבורי (iptv-org) | iptv-org (`Belarus24.by`) | ✅ |
| 385 | Al Masirah | live.cdnbridge.tv | 🌐 ציבורי (iptv-org) | iptv-org (`AlMasirah.ye`) | ✅ |
| 386 | Al Najah News | streaming.najah.edu | 🌐 ציבורי (iptv-org) | iptv-org (`AlNajahNews.ps`) | ✅ |
| 387 | Altaghier TV | jmc-live.ercdn.net | 🌐 ציבורי (iptv-org) | iptv-org (`AltaghierTV.jo`) | ✅ |
| 388 | TV Legislativa | streamingav.asambleanacional.gob.ec | 🌐 ציבורי (iptv-org) | iptv-org (`TVLegislativa.ec`) | ✅ |
| 389 | Ekushey TV | ekusheyserver.com | 🌐 ציבורי (iptv-org) | iptv-org (`EkusheyTV.bd`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 390 | Formula | c4635.cdn.xsg.ge | 🌐 ציבורי (iptv-org) | iptv-org (`Formula.ge`) | ✅ |
| 391 | Privesc.Eu TV | cachestar.privesc.eu | 🌐 ציבורי (iptv-org) | iptv-org (`PrivescEuTV.md`) | ✅ |
| 392 | CNA | d2e1asnsl7br7b.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`CNA.sg`) | ✅ |
| 393 | Guardian Talk Radio | cdn-edge1.streamcomedia.com | 🌐 ציבורי (iptv-org) | iptv-org (`GuardianTalkRadio.bs`) | ✅ |
| 394 | Parliament TV | ptvlive.kordia.net.nz | 🌐 ציבורי (iptv-org) | iptv-org (`ParliamentTV.nz`) | ✅ |
| 395 | Al Masar TV | starmenajo.com | 🌐 ציבורי (iptv-org) | iptv-org (`AlMasarTV.ly`) | ✅ |
| 396 | AL24 News | cdn.live.easybroadcast.io | 🌐 ציבורי (iptv-org) | iptv-org (`AL24News.dz`) | ✅ |
| 397 | Dabanga TV | hls.dabangasudan.org | 🌐 ציבורי (iptv-org) | iptv-org (`DabangaTV.sd`) | ✅ |
| 398 | Oman TV Mubashir | partwota.cdn.mgmlcdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`OmanTVMubashir.om`) | ✅ |
| 399 | Sahara 24 | live.saharamedias.net | 🌐 ציבורי (iptv-org) | iptv-org (`Sahara24.mr`) | ✅ |
| 400 | 7 Info | video1.getstreamhosting.com | 🌐 ציבורי (iptv-org) | iptv-org (`7Info.ci`) | ✅ |
| 401 | Baraka Television | stream.berosat.live | 🌐 ציבורי (iptv-org) | iptv-org (`BarakaTelevision.cd`) | ✅ |
| 402 | Haiti News Channel | cdn.haititivi.com | 🌐 ציבורי (iptv-org) | iptv-org (`HaitiNewsChannel.ht`) | ✅ |
| 403 | Monaco Info | webtv.monacoinfo.com | 🌐 ציבורי (iptv-org) | iptv-org (`MonacoInfo.mc`) | ✅ |
| 404 | Guatevision | mdstrm.com | 🌐 ציבורי (iptv-org) | iptv-org (`Guatevision.gt`) | ✅ |
| 405 | Norte Informativo TV | videohd.live | 🌐 ציבורי (iptv-org) | iptv-org (`NorteInformativoTV.cr`) | ✅ |
| 406 | Prensa Latina TV | eu1.servers10.com | 🌐 ציבורי (iptv-org) | iptv-org (`PrensaLatinaTV.cu`) | ✅ |
| 407 | Radio Ancon | www.streaming507.net | 🌐 ציבורי (iptv-org) | iptv-org (`RadioAncon.pa`) | ✅ |
| 408 | Brian TV | livefta.malimarcdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`BrianTV.la`) | ✅ |
| 409 | DVB TV | live-stream.dvb.no | 🌐 ציבורי (iptv-org) | iptv-org (`DVBTV.mm`) | ✅ |
| 410 | First Channel News | amtvusdvr.tulix.tv | 🌐 ציבורי (iptv-org) | iptv-org (`FirstChannelNews.am`) | ✅ |
| 411 | Newsmax Balkans | newsmaxadria.mts-si.tv | 🌐 ציבורי (iptv-org) | iptv-org (`NewsmaxBalkans.ba`) | ✅ |
| 412 | TOLOnews | tgn.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`TOLOnews.af`) | ✅ |
| 413 | TV Arta | gjirafa-video-live.gjirafa.net | 🌐 ציבורי (iptv-org) | iptv-org (`TVArta.xk`) | ✅ |
| 414 | TV Jadran | tvjadran.stream.agatin.hr | 🌐 ציבורי (iptv-org) | iptv-org (`TVJadran.hr`) | ✅ |
| 415 | Visir | live.visir.is | 🌐 ציבורי (iptv-org) | iptv-org (`Visir.is`) | ✅ |
| 416 | BX Inform | reflect-stream-bronxnet.cablecast.tv | 🌐 ציבורי (iptv-org) | iptv-org (`BXInform.us`) | ✅ |
| 417 | Dunya News UK | ukintl.dunyanews.tv | 🌐 ציבורי (iptv-org) | iptv-org (`DunyaNewsUK.uk`) | ✅ |
| 418 | Africa 24 | africa24.vedge.infomaniak.com | 🌐 ציבורי (iptv-org) | iptv-org (`Africa24.fr`) | ✅ |
| 419 | 3Cat Cameres del temps | directes-tv-int.3catdirectes.cat | 🌐 ציבורי (iptv-org) | iptv-org (`3CatCameresdeltemps.es`) | ✅ |
| 420 | Senato TV | senato-live.morescreens.com | 🌐 ציבורי (iptv-org) | iptv-org (`SenatoTV.it`) | ✅ |
| 421 | 360° News | live-vgtrksmotrim.cdnvideo.ru | 🌐 ציבורי (iptv-org) | iptv-org (`360News.ru`) | ✅ |
| 422 | Channel 24 | streamvideol1.luxnet.ua | 🌐 ציבורי (iptv-org) | iptv-org (`Channel24.ua`) | ✅ |
| 423 | CityNews Calgary | citynewsregional.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`CityNewsCalgary.ca`) | ✅ |
| 424 | ticker News | live-hls-7agy.livepush.io | 🌐 ציבורי (iptv-org) | iptv-org (`tickerNews.au`) | ✅ |
| 425 | Al Yaum TV | hlspackager.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`AlYaumTV.ae`) | ✅ |
| 426 | Al Araby TV 2 | live.kwikmotion.com | 🌐 ציבורי (iptv-org) | iptv-org (`AlArabyTV2.qa`) | ✅ |
| 427 | Al Alam Al Yawm | mn-nl.mncdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`AlAlamAlYawm.sa`) | ✅ |
| 428 | Voice of Lebanon | Asharq Discovery – ItWorks (רשמי) | ✅ רשמי | iptv-org (`VoiceofLebanon.lb`) | ✅ |
| 429 | Mekameleen TV | mn-nl.mncdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`MekameleenTV.tr`) | ✅ |
| 430 | News18 Assam North-East | n18syndication.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`News18AssamNorthEast.in`) | ✅ |
| 431 | Weathernews | rch01e-alive-hls.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`Weathernews.jp`) | ✅ |
| 432 | Arirang UN | amdlive-ch02-ctnd-com.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`ArirangUN.kr`) | ✅ |
| 433 | STZ TV | cdn.live.br1.jmvstream.com | 🌐 ציבורי (iptv-org) | iptv-org (`STZTV.br`) | ✅ |
| 434 | AMX Noticias | 5e50264bd6766.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`AMXNoticias.mx`) | ✅ |
| 435 | VIVE TV | vivo.solumedia.com | 🌐 ציבורי (iptv-org) | iptv-org (`VIVETV.ar`) | ✅ |
| 436 | CNN Portugal | github.com | 🌐 ציבורי (iptv-org) | iptv-org (`CNNPortugal.pt`) | ✅ |
| 437 | EbS+ | streams.prd.commavservices.eu | 🌐 ציבורי (iptv-org) | iptv-org (`EbSPlus.be`) | ✅ |
| 438 | TVK Stella | stream.tvkstella.pl | 🌐 ציבורי (iptv-org) | iptv-org (`TVKStella.pl`) | ✅ |
| 439 | Hellenic Parliament TV | ert-ucdn.broadpeak-aas.com | 🌐 ציבורי (iptv-org) | iptv-org (`HellenicParliamentTV.gr`) | ✅ |
| 440 | Di TV | cdn0-03837-liveedge0.dna.ip-only.net | 🌐 ציבורי (iptv-org) | iptv-org (`DiTV.se`) | ✅ |
| 441 | CT24 | dash2.antik.sk | 🌐 ציבורי (iptv-org) | iptv-org (`CT24.cz`) | ✅ |
| 442 | Aleph News | stream-aleph.m.ro | 🌐 ציבורי (iptv-org) | iptv-org (`AlephNews.ro`) | ✅ |
| 443 | T13 En Vivo | redirector.dps.live | 🌐 ציבורי (iptv-org) | iptv-org (`T13EnVivo.cl`) | ✅ |
| 444 | NTN24 | stream1.bitred.cl | 🌐 ציבורי (iptv-org) | iptv-org (`NTN24.co`) | ✅ |
| 445 | Conecta2TV | live.obslivestream.com | 🌐 ציבורי (iptv-org) | iptv-org (`Conecta2TV.pe`) | ✅ |
| 446 | Lingkar TV | lingkartv.my.id | 🌐 ציבורי (iptv-org) | iptv-org (`LingkarTV.id`) | ✅ |
| 447 | Thai Parliament Television | tv-live.tpchannel.org | 🌐 ציבורי (iptv-org) | iptv-org (`ThaiParliamentTelevision.th`) | ✅ |
| 448 | Tra Vinh TV | freem3u.xyz | 🌐 ציבורי (iptv-org) | iptv-org (`TraVinhTV.vn`) | ✅ |
| 449 | Dunya News | imob.dunyanews.tv | 🌐 ציבורי (iptv-org) | iptv-org (`DunyaNews.pk`) | ✅ |
| 450 | Medi1TV Afrique | cdn.live.easybroadcast.io | 🌐 ציבורי (iptv-org) | iptv-org (`Medi1TVAfrique.ma`) | ✅ |
| 451 | SABC News | sabconetanw.cdn.mangomolo.com | 🌐 ציבורי (iptv-org) | iptv-org (`SABCNews.za`) | ✅ |
| 452 | News Central | wf.newscentral.ng | 🌐 ציבורי (iptv-org) | iptv-org (`NewsCentral.ng`) | ✅ |
| 453 | Alghadeer TV | sd343444.vodu.store | 🌐 ציבורי (iptv-org) | iptv-org (`AlghadeerTV.iq`) | ✅ |
| 454 | RDN | edge.essastream.com | 🌐 ציבורי (iptv-org) | iptv-org (`RDN.do`) | ✅ |
| 455 | Mega TV | www.desde-paraguay.com | 🌐 ציבורי (iptv-org) | iptv-org (`MegaTV.py`) | ✅ |
| 456 | Lenca TV | lencatelevision.com | 🌐 ציבורי (iptv-org) | iptv-org (`LencaTV.hn`) | ✅ |
| 457 | ntv | protokolldns.xyz | 🌐 ציבורי (iptv-org) | iptv-org (`ntv.al`) | ✅ |
| 458 | Syria TV | live.kwikmotion.com | 🌐 ציבורי (iptv-org) | iptv-org (`SyriaTV.sy`) | ✅ |
| 459 | CRTV News | live1.acangroup.org | 🌐 ציבורי (iptv-org) | iptv-org (`CRTVNews.cm`) | ✅ |
| 460 | Telesur | mblenmain01.telesur.ultrabase.net | 🌐 ציבורי (iptv-org) | iptv-org (`Telesur.ve`) | ✅ |
| 461 | UMSA TVU LP | edge.enhdtv.com | 🌐 ציבורי (iptv-org) | iptv-org (`UMSATVULP.bo`) | ✅ |
| 462 | TV NRSR | n11.stv.livebox.sk | 🌐 ציבורי (iptv-org) | iptv-org (`TVNRSR.sk`) | ✅ |
| 463 | APA Tv | stream.apa.tv | 🌐 ציבורי (iptv-org) | iptv-org (`APATv.az`) | ❌ רק באפליקציה (HTTP) |
| 464 | Fresh News | streaming-android.freshnewsasia.tv | 🌐 ציבורי (iptv-org) | iptv-org (`FreshNews.kh`) | ❌ רק באפליקציה (HTTP) |
| 465 | KTV News | kwtkbta.cdn.mangomolo.com | 🌐 ציבורי (iptv-org) | iptv-org (`KTVNews.kw`) | ✅ |
| 466 | NW Info | hls.newworldtv.com | 🌐 ציבורי (iptv-org) | iptv-org (`NWInfo.tg`) | ✅ |
| 467 | Perviy Informationniy | ngtrk.dc.beltelecom.by | 🌐 ציבורי (iptv-org) | iptv-org (`PerviyInformationniy.by`) | ✅ |
| 468 | Al Masirah Mubacher | live2.cdnbridge.tv | 🌐 ציבורי (iptv-org) | iptv-org (`AlMasirahMubacher.ye`) | ✅ |
| 469 | Ma'an TV | htvmada.mada.ps | 🌐 ציבורי (iptv-org) | iptv-org (`MaanTV.ps`) | ✅ |
| 470 | Alhaqeqa Aldawlia | ghaasiflu.online | 🌐 ציבורי (iptv-org) | iptv-org (`AlhaqeqaAldawlia.jo`) | ✅ |
| 471 | Ecotel TV | ecoteltv.streamseguro.com | 🌐 ציבורי (iptv-org) | iptv-org (`EcotelTV.ec`) | ✅ |
| 472 | News 21 Bangla TV | 103.190.133.68 | 🌐 ציבורי (iptv-org) | iptv-org (`News21BanglaTV.bd`) | ❌ רק באפליקציה (HTTP) |
| 473 | Palitra News | live.palitranews.ge | 🌐 ציבורי (iptv-org) | iptv-org (`PalitraNews.ge`) | ✅ |
| 474 | Realitatea TV | realitatealive.md | 🌐 ציבורי (iptv-org) | iptv-org (`RealitateaTV.md`) | ✅ |
| 475 | Cheddar News | hls.livecdn.io | 🌐 ציבורי (iptv-org) | iptv-org (`CheddarNews.us`) | ✅ |
| 476 | Afghanistan International | hls.afintl.com | 🌐 ציבורי (iptv-org) | iptv-org (`AfghanistanInternational.uk`) | ✅ |
| 477 | Africa 24 Sport | africa24.vedge.infomaniak.com | 🌐 ציבורי (iptv-org) | iptv-org (`Africa24Sport.fr`) | ✅ |
| 478 | WELT | dash4.antik.sk | 🌐 ציבורי (iptv-org) | iptv-org (`WELT.de`) | ✅ |
| 479 | 3CatInfo | directes-tv-int.3catdirectes.cat | 🌐 ציבורי (iptv-org) | iptv-org (`3CatInfo.es`) | ✅ |
| 480 | Etna Espresso Channel | 5db313b643fd8.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`EtnaEspressoChannel.it`) | ✅ |
| 481 | RT Arabic | rt-arb.rttv.com | 🌐 ציבורי (iptv-org) | iptv-org (`RTArabic.ru`) | ✅ |
| 482 | Rada TV | live-tv.cloud – ספק IPTV אוקראיני | ⚠️ ממסר צד שלישי | iptv-org (`RadaTV.ua`) | ✅ |
| 483 | CityNews Toronto | citynewsregional.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`CityNewsToronto.ca`) | ✅ |
| 484 | ABC News | abc-news-dmd-streams-1.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`ABCNews.au`) | ✅ |
| 485 | Alarabiya | live.alarabiya.net | 🌐 ציבורי (iptv-org) | iptv-org (`Alarabiya.ae`) | ✅ |
| 486 | Al Jazeera 2 | live-hls-web-aja2-gcp.thehlive.com | 🌐 ציבורי (iptv-org) | iptv-org (`AlJazeera2.qa`) | ✅ |
| 487 | Palestine Al Yawm | live.paltodaytv.com | 🌐 ציבורי (iptv-org) | iptv-org (`PalestineAlYawm.lb`) | ✅ |
| 488 | TRT Arabi | tv-trtarabi.medya.trt.com.tr | 🌐 ציבורי (iptv-org) | iptv-org (`TRTArabi.tr`) | ✅ |
| 489 | Republic TV | d3qs3d2rkhfqrt.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`RepublicTV.in`) | ✅ |
| 490 | KTV | hlive.ktv.go.kr | 🌐 ציבורי (iptv-org) | iptv-org (`KTV.kr`) | ✅ |
| 491 | Dongguan Comprehensive News Channel | stream.sun0769.com | 🌐 ציבורי (iptv-org) | iptv-org (`DongguanComprehensiveNewsChannel.cn`) | ✅ |
| 492 | TV Camara | stream3.camara.gov.br | 🌐 ציבורי (iptv-org) | iptv-org (`TVCamara.br`) | ✅ |
| 493 | Canal 33 Tijuana | 5f2c1b0d880e5.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`Canal33Tijuana.mx`) | ✅ |
| 494 | Catamarca TV | stream.arcast.com.ar | 🌐 ציבורי (iptv-org) | iptv-org (`CatamarcaTV.ar`) | ✅ |
| 495 | Sterk TV | hlspackager.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`SterkTV.be`) | ✅ |
| 496 | TVP World | dash2.antik.sk | 🌐 ציבורי (iptv-org) | iptv-org (`TVPWorld.pl`) | ✅ |
| 497 | Naftemporiki TV | telmaco.ascdn.broadpeak.io | 🌐 ציבורי (iptv-org) | iptv-org (`NaftemporikiTV.gr`) | ✅ |
| 498 | Expressen TV | cdn0-03837-liveedge0.dna.ip-only.net | 🌐 ציבורי (iptv-org) | iptv-org (`ExpressenTV.se`) | ✅ |
| 499 | Digi 24 | edge-ar.rcs-rds.ro | 🌐 ציבורי (iptv-org) | iptv-org (`Digi24.ro`) | ✅ |
| 500 | ADN TV | redirector.rudo.video | 🌐 ציבורי (iptv-org) | iptv-org (`ADNTV.cl`) | ✅ |
| 501 | Red+ | inforedvos.lcdn.claro.net.co | 🌐 ציבורי (iptv-org) | iptv-org (`RedPlus.co`) | ✅ |
| 502 | Exitosa TV | luna-4-video.mediaserver.digital | 🌐 ציבורי (iptv-org) | iptv-org (`ExitosaTV.pe`) | ✅ |
| 503 | Metro TV | edge.medcom.id | 🌐 ציבורי (iptv-org) | iptv-org (`MetroTV.id`) | ✅ |
| 504 | Top News | live.topnews.co.th | 🌐 ציבורי (iptv-org) | iptv-org (`TopNews.th`) | ✅ |
| 505 | Lahore News | vcdn.dunyanews.tv | 🌐 ציבורי (iptv-org) | iptv-org (`LahoreNews.pk`) | ✅ |
| 506 | SABC Lehae | sabctretalh.cdn.mangomolo.com | 🌐 ציבורי (iptv-org) | iptv-org (`SABCLehae.za`) | ✅ |
| 507 | Hermanas Mirabal TV | fox.hostlagarto.com | 🌐 ציבורי (iptv-org) | iptv-org (`HermanasMirabalTV.do`) | ✅ |
| 508 | Nanduti | nanduti.b-cdn.net | 🌐 ציבורי (iptv-org) | iptv-org (`Nanduti.py`) | ✅ |

## 📺 אוכל ובישול (Food)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 786 | bon appetit | bonappetit-samsung.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`bonappetit.us`) | ✅ |
| 787 | Master Chefs Academy TV | platform-iuoamc.uk | 🌐 ציבורי (iptv-org) | iptv-org (`MasterChefsAcademyTV.uk`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 788 | TV5MONDE Chefs | tvf-tv5ch.otteravision.com | 🌐 ציבורי (iptv-org) | iptv-org (`TV5MONDEChefs.fr`) | ✅ |
| 789 | Pluto TV Cuisine | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVCuisine.de`) | ✅ |
| 790 | Food Network | streaming.aurora.enhanced.live | 🌐 ציבורי (iptv-org) | iptv-org (`FoodNetwork.it`) | ✅ |
| 791 | Kulinar TV | api.alpaca.t62a.com | 🌐 ציבורי (iptv-org) | iptv-org (`KulinarTV.ru`) | ✅ |
| 792 | Gusto TV | e75fc053c0d2406fb74cdd71d77e1a7f.mediatailor.us-east-1.amazonaws.com | 🌐 ציבורי (iptv-org) | iptv-org (`GustoTV.ca`) | ✅ |
| 793 | Food Food | mumt03.tangotv.in | 🌐 ציבורי (iptv-org) | iptv-org (`FoodFood.in`) | ✅ |
| 794 | Boligkjop i blinde | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`Boligkjopiblinde.se`) | ✅ |
| 795 | Dens Food Channel | op-flashcon-digdayahd-1.dens.tv | 🌐 ציבורי (iptv-org) | iptv-org (`DensFoodChannel.id`) | ✅ |
| 796 | Masala TV | cdn4.mjunoon.tv | 🌐 ציבורי (iptv-org) | iptv-org (`MasalaTV.pk`) | ✅ |
| 797 | Asian Food Network | freem3u.xyz | 🌐 ציבורי (iptv-org) | iptv-org (`AsianFoodNetwork.sg`) | ✅ |
| 798 | Jordan Kitchen | playlist.fasttvcdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`JordanKitchen.jo`) | ✅ |
| 799 | Chef Champion | rpn.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`ChefChampion.us`) | ✅ |
| 800 | Pluto TV Cocina | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVCocina.de`) | ✅ |
| 801 | City Eden Recepty Gurmana | cityeden.catcast.tv | 🌐 ציבורי (iptv-org) | iptv-org (`CityEdenReceptyGurmana.ru`) | ✅ |
| 802 | Boligkob i blinde | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`Boligkobiblinde.se`) | ✅ |
| 803 | Chef Roc Show | rpn.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`ChefRocShow.us`) | ✅ |
| 804 | Pluto TV Food | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVFood.de`) | ✅ |
| 805 | FoodTime | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`FoodTime.ru`) | ❌ רק באפליקציה (HTTP) |
| 806 | Buying Blind Denmark | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`BuyingBlindDenmark.se`) | ✅ |
| 807 | Gordon Ramsay's Hell's Kitchen | aegis-cloudfront-1.tubi.video | 🌐 ציבורי (iptv-org) | iptv-org (`GordonRamsaysHellsKitchen.us`) | ✅ |
| 808 | Pluto TV Cucina | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVCucina.de`) | ✅ |
| 809 | Koch'en pa toppen | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`Kochenpatoppen.se`) | ✅ |
| 810 | Tastemade Brasil | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`TastemadeBrasil.us`) | ✅ |
| 811 | MasterChef Danmarks storste madtalenter | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`MasterChefDanmarksstorstemadtalenter.se`) | ✅ |
| 812 | Cooking Panda | stream-us-east-1.getpublica.com | 🌐 ציבורי (iptv-org) | iptv-org (`CookingPanda.us`) | ✅ |
| 813 | Med kniven for struben | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`Medknivenforstruben.se`) | ✅ |
| 814 | Mad Dog and Merrill | rpn.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`MadDogandMerrill.us`) | ✅ |
| 815 | Skonne ombygninger | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`Skonneombygninger.se`) | ✅ |
| 816 | Pluto TV KFOOD | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVKFOOD.us`) | ✅ |
| 817 | Stugfixarna Norge | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`StugfixarnaNorge.se`) | ✅ |
| 818 | Food Network Latin America | 168.196.127.137 | 🌐 ציבורי (iptv-org) | iptv-org (`FoodNetworkLatinAmerica.us`) | ❌ רק באפליקציה (HTTP) |
| 819 | Til Middag Hos | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`TilMiddagHos.se`) | ✅ |
| 820 | Cooking Channel | 23.237.104.106 | 🌐 ציבורי (iptv-org) | iptv-org (`CookingChannel.us`) | ❌ רק באפליקציה (HTTP) |
| 821 | Food Network | 23.237.104.106 | 🌐 ציבורי (iptv-org) | iptv-org (`FoodNetwork.us`) | ❌ רק באפליקציה (HTTP) |

## 📺 דת ואמונה (Religion)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 1406 | Amazing Facts TV | hlspackager.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`AmazingFactsTV.us`) | ✅ |
| 1407 | Faith World TV | faithworldtv.abs.tv | 🌐 ציבורי (iptv-org) | iptv-org (`FaithWorldTV.uk`) | ✅ |
| 1408 | Evandis Gospel TV | evandis.streamakaci.tv | 🌐 ציבורי (iptv-org) | iptv-org (`EvandisGospelTV.fr`) | ✅ |
| 1409 | Sophia TV Francais | bild-und-ton.stream | 🌐 ציבורי (iptv-org) | iptv-org (`SophiaTVFrancais.de`) | ✅ |
| 1410 | Logos TV | streamer1.streamhost.org | 🌐 ציבורי (iptv-org) | iptv-org (`LogosTV.es`) | ✅ |
| 1411 | Padre Pio TV | 600f07e114306.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`PadrePioTV.it`) | ✅ |
| 1412 | Huzur TV | hls-mycdn08724960135.cdnvideo.ru | 🌐 ציבורי (iptv-org) | iptv-org (`HuzurTV.ru`) | ✅ |
| 1413 | EWTN Ukraine | cdn3.wowza.com | 🌐 ציבורי (iptv-org) | iptv-org (`EWTNUkraine.ua`) | ✅ |
| 1414 | 3ABN Canada | 3abn.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`3ABNCanada.ca`) | ✅ |
| 1415 | Mission Asia | livecdn.live247stream.com | 🌐 ציבורי (iptv-org) | iptv-org (`MissionAsia.au`) | ✅ |
| 1416 | Peace TV English | dzkyvlfyge.erbvr.com | 🌐 ציבורי (iptv-org) | iptv-org (`PeaceTVEnglish.ae`) | ✅ |
| 1417 | Qatar TV The Holy Quran | qatartv.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`QatarTVTheHolyQuran.qa`) | ✅ |
| 1418 | Holy Quran Radio | live.kwikmotion.com | 🌐 ציבורי (iptv-org) | iptv-org (`HolyQuranRadio.sa`) | ✅ |
| 1419 | Aghapy TV | 5b622f07944df.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`AghapyTV.eg`) | ✅ |
| 1420 | Noursat English | Asharq Discovery – ItWorks (רשמי) | ✅ רשמי | iptv-org (`NoursatEnglish.lb`) | ✅ |
| 1421 | Diyanet TV | eustr73.mediatriple.net | 🌐 ציבורי (iptv-org) | iptv-org (`DiyanetTV.tr`) | ✅ |
| 1422 | Salvation TV | ktismaservers.in | 🌐 ציבורי (iptv-org) | iptv-org (`SalvationTV.in`) | ✅ |
| 1423 | BTN TV | btn.nowcdn.co.kr | 🌐 ציבורי (iptv-org) | iptv-org (`BTNTV.kr`) | ✅ |
| 1424 | Ando TV | play.kankanlive.com | 🌐 ציבורי (iptv-org) | iptv-org (`AndoTV.cn`) | ❌ רק באפליקציה (HTTP) |
| 1425 | Rede Gospel | cdn.live.br1.jmvstream.com | 🌐 ציבורי (iptv-org) | iptv-org (`RedeGospel.br`) | ✅ |
| 1426 | Monte Maria | 5ca9af4645e15.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`MonteMaria.mx`) | ✅ |
| 1427 | TV Mana Argentina | w2.manasat.com | 🌐 ציבורי (iptv-org) | iptv-org (`TVManaArgentina.ar`) | ✅ |
| 1428 | Mana Church Online | w2.manasat.com | 🌐 ציבורי (iptv-org) | iptv-org (`ManaChurchOnline.pt`) | ✅ |
| 1429 | Immaculata TV | g5nl63z8lpq6-hls-live.5centscdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`ImmaculataTV.ch`) | ✅ |
| 1430 | KIT-TV | everest.pa-hosting.de | 🌐 ציבורי (iptv-org) | iptv-org (`KITTV.at`) | ✅ |
| 1431 | EWTN Poland | cdn3.wowza.com | 🌐 ציבורי (iptv-org) | iptv-org (`EWTNPoland.pl`) | ✅ |
| 1432 | Himlen TV7 | vod.tv7.fi | 🌐 ציבורי (iptv-org) | iptv-org (`HimlenTV7.fi`) | ✅ |
| 1433 | Alfa Omega TV | aom-live.maghost.ro | 🌐 ציבורי (iptv-org) | iptv-org (`AlfaOmegaTV.ro`) | ✅ |
| 1434 | Apostol TV | live.apostoltv.hu | 🌐 ציבורי (iptv-org) | iptv-org (`ApostolTV.hu`) | ✅ |
| 1435 | Fortaleza TV | multimedia.fortalezatv.cl | 🌐 ציבורי (iptv-org) | iptv-org (`FortalezaTV.cl`) | ✅ |

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
| 28 | גלגלצ | גלי צה"ל / גלגלצ – CDN רשמי (Bynet) | ✅ רשמי | radio-browser.info | ✅ |
| 29 | גלי צה"ל | גלי צה"ל / גלגלצ – CDN רשמי (Bynet) | ✅ רשמי | radio-browser.info | ✅ |
| 30 | גלגלצ להיטים | גלי צה"ל / גלגלצ – CDN רשמי (Bynet) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 31 | eco99fm | eco99fm – Mediacast (רשמי) | ✅ רשמי | radio-browser.info | ✅ |
| 32 | 103FM | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | radio-browser.info | ✅ |
| 33 | רדיוס 100FM | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | radio-browser.info | ✅ |
| 34 | 102FM רדיו תל אביב | 102FM – LiveCDN (רשמי) | ✅ רשמי | radio-browser.info | ✅ |
| 35 | רדיו הקצה KZ | רדיו הקצה – Mediacast (רשמי) | ✅ רשמי | radio-browser.info | ✅ |
| 36 | קול ברמה | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 37 | רדיו דרום 101.5FM | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | radio-browser.info | ✅ |
| 38 | רדיו 2000 | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 39 | קול ברמה מוזיקה (קול פליי) | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | radio-browser.info | ✅ |
| 40 | 104.5FM רדיו צפון | StreamGates – ה-CDN של התחנה | ✅ רשמי | radio-browser.info | ✅ |
| 41 | 101FM רדיו ירושלים | StreamGates – ה-CDN של התחנה | ✅ רשמי | radio-browser.info | ✅ |
| 42 | רדיו חיפה 107.5FM | רדיו חיפה – LiveCDN (רשמי) | ✅ רשמי | radio-browser.info | ✅ |
| 43 | 90FM אמצע הדרך | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | radio-browser.info | ✅ |
| 44 | רדיו דרום 97FM | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | radio-browser.info | ✅ |
| 45 | רדיו התחנה 101.5 | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | radio-browser.info | ✅ |
| 46 | גלי ישראל 94FM | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | radio-browser.info | ✅ |
| 47 | קול חי 93FM | קול חי – שרת התחנה | ✅ רשמי | radio-browser.info | ✅ |
| 48 | קול חי מיוזיק | קול חי – שרת התחנה | ✅ רשמי | radio-browser.info | ✅ |
| 49 | קול רגע 96FM | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | radio-browser.info | ✅ |
| 50 | לב המדינה 91FM | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | radio-browser.info | ✅ |
| 51 | רדיוס נוסטלגי 96.3 | StreamGates – ה-CDN של התחנה | ✅ רשמי | radio-browser.info | ✅ |
| 52 | קול הצפון | eCast – שרת התחנה | ✅ רשמי | radio-browser.info | ✅ |
| 53 | גלגלצ רוק | גלי צה"ל / גלגלצ – CDN רשמי (Bynet) | ✅ רשמי | radio-browser.info | ✅ |
| 54 | גלגלצ עשורים | גלי צה"ל / גלגלצ – CDN רשמי (Bynet) | ✅ רשמי | radio-browser.info | ✅ |
| 55 | גלגלצ רגוע | גלי צה"ל / גלגלצ – CDN רשמי (Bynet) | ✅ רשמי | radio-browser.info | ✅ |
| 56 | רדיוס 100% מזרחית | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | radio-browser.info | ✅ |
| 57 | רדיוס 100% יהודי | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | radio-browser.info | ✅ |
| 58 | רדיוס 100% להיטים | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | radio-browser.info | ✅ |
| 59 | רדיוס 100% שנות ה-80 | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | radio-browser.info | ✅ |
| 60 | רדיוס 100% שנות ה-90 | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | radio-browser.info | ✅ |
| 61 | רדיוס 100% ילדים | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | radio-browser.info | ✅ |
| 62 | רדיוס 100% רגוע | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | radio-browser.info | ✅ |
| 63 | א-שמס A-Shams | StreamGates – ה-CDN של התחנה | ✅ רשמי | radio-browser.info | ✅ |
| 64 | רדיו נאס Radio Nas | StreamGates – ה-CDN של התחנה | ✅ רשמי | radio-browser.info | ✅ |
| 65 | הרדיו הראשון 89.1FM (רוסית) | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | radio-browser.info | ✅ |
| 66 | רדיו ברסלב | רדיו ברסלב – שרת התחנה | ✅ רשמי | radio-browser.info | ✅ |
| 67 | רדיו נהריה | רדיו נהריה – שרת התחנה | ✅ רשמי | radio-browser.info | ✅ |
| 68 | רדיוס 100% אימון | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | radio-browser.info | ✅ |
| 69 | רדיוס 100% טופ 40 | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | radio-browser.info | ✅ |
| 70 | רדיוס 100% סרטים | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | radio-browser.info | ✅ |
| 71 | רדיוס 100% אולדיז | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | radio-browser.info | ✅ |
| 72 | רדיוס 100% רטרו | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | radio-browser.info | ✅ |
| 73 | רדיוס 100% צ'ילאאוט | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | radio-browser.info | ✅ |
| 74 | רדיוס 100% קאנטרי | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | radio-browser.info | ✅ |
| 75 | רדיוס 100% K-Pop | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | radio-browser.info | ✅ |
| 76 | רדיוס 100% גיימר | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | radio-browser.info | ✅ |
| 77 | נושמים מזרחית | www.rs.mizrahit.fm | ⚠️ ממסר צד שלישי | radio-browser.info | ❌ רק באפליקציה (HTTP) |
| 78 | רדיו סהר | eCast – שרת התחנה | ✅ רשמי | radio-browser.info | ✅ |
| 79 | Israeli Voice Radio | icecast.live | ⚠️ ממסר צד שלישי | radio-browser.info | ✅ |
| 80 | ג'וינט רדיו בלוז רוק | jointil.com | ⚠️ ממסר צד שלישי | radio-browser.info | ✅ |
| 81 | ג'וינט רדיו רגאיי | jointil.com | ⚠️ ממסר צד שלישי | radio-browser.info | ✅ |
| 82 | ג'וינט רדיו טראנס | jointil.com | ⚠️ ממסר צד שלישי | radio-browser.info | ✅ |
| 83 | קבלה לעם רדיו | icecast.kab.tv | ⚠️ ממסר צד שלישי | radio-browser.info | ❌ רק באפליקציה (HTTP) |
| 84 | ברסלב: תורה וזימרה | רדיו ברסלב – שרת התחנה | ✅ רשמי | radio-browser.info | ✅ |
| 85 | Radio France Israël | stream10.vestaradio.com | ⚠️ ממסר צד שלישי | radio-browser.info | ✅ |
| 86 | רדיו ספורט 5 | ספורט 5 – השידור הרשמי של RGE (Akamai) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 87 | 102FM קול הים האדום | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 88 | 99.5FM רדיו | 99.5FM – LiveCDN (רשמי) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 89 | הרדיו הטוב 106.4FM (רוסית) | StreamGates – ה-CDN של התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 90 | רדיו שירי דיכאון | רדיו שירי דיכאון – השרת של התחנה | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 91 | רדיוס 100% אהבה | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | radio-browser.info | ✅ |
| 92 | רדיוס 100% רוק קלאסי | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | radio-browser.info | ✅ |
| 93 | רדיוס 100% רוק | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | radio-browser.info | ✅ |
| 94 | רדיוס 100% בלוז | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | radio-browser.info | ✅ |
| 95 | רדיוס 100% ג'אז | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | radio-browser.info | ✅ |
| 96 | רדיוס 100% מוזיקת עולם | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | radio-browser.info | ✅ |
| 97 | רדיוס 100% קלאב | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | radio-browser.info | ✅ |
| 98 | רדיוס 100% לטינו | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | radio-browser.info | ✅ |
| 99 | רדיוס 100% בריטפופ | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | radio-browser.info | ✅ |
| 100 | רדיוס 100% אגדות | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | radio-browser.info | ✅ |
| 101 | רדיוס 100% קאברים | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | radio-browser.info | ✅ |
| 102 | רדיוס 100% טיקטוק | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | radio-browser.info | ✅ |
| 103 | רדיוס 100% קלאסית | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | radio-browser.info | ✅ |
| 104 | רדיוס 100% אירוויזיון | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | radio-browser.info | ✅ |
| 105 | רדיוס 100% שירי דיסני | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | radio-browser.info | ✅ |
| 106 | רדיוס 100% ערוץ הכוכבים | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | radio-browser.info | ✅ |
| 107 | רדיוס 100% סטנדאפ | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | radio-browser.info | ✅ |
| 108 | רדיוס 100% סלפי | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | radio-browser.info | ✅ |
| 109 | קבלה לעם רדיו – רוסית | icecast.kab.tv | ⚠️ ממסר צד שלישי | radio-browser.info | ❌ רק באפליקציה (HTTP) |
| 110 | קבלה לעם רדיו – אנגלית | icecast.kab.tv | ⚠️ ממסר צד שלישי | radio-browser.info | ❌ רק באפליקציה (HTTP) |
| 111 | קבלה לעם רדיו – צרפתית | icecast.kab.tv | ⚠️ ממסר צד שלישי | radio-browser.info | ❌ רק באפליקציה (HTTP) |
| 112 | קבלה לעם רדיו – ספרדית | icecast.kab.tv | ⚠️ ממסר צד שלישי | radio-browser.info | ❌ רק באפליקציה (HTTP) |
| 113 | מזרחית FM | rs.mizrahit.fm | ⚠️ ממסר צד שלישי | radio-browser.info | ❌ רק באפליקציה (HTTP) |
| 114 | סטאר 33 רדיו | online.star33radio.com | ⚠️ ממסר צד שלישי | radio-browser.info | ✅ |
| 115 | רק מוזיקה (Rak Musica) | play.radioking.io | ⚠️ ממסר צד שלישי | radio-browser.info | ✅ |
| 116 | עברי 6 | streaming.radio.co | ⚠️ ממסר צד שלישי | radio-browser.info | ✅ |
| 117 | yStream | ystream.live | ⚠️ ממסר צד שלישי | radio-browser.info | ✅ |
| 118 | ברסלב: הלכות ודף יומי | רדיו ברסלב – שרת התחנה | ✅ רשמי | radio-browser.info | ✅ |
| 119 | ברסלב: קצר וקולע | רדיו ברסלב – שרת התחנה | ✅ רשמי | radio-browser.info | ✅ |
| 120 | ברסלב: ספרי ברסלב | רדיו ברסלב – שרת התחנה | ✅ רשמי | radio-browser.info | ✅ |
| 121 | Up2Dance רדיו | icecast.live | ⚠️ ממסר צד שלישי | radio-browser.info | ✅ |
| 122 | רדיו אהבה – רומנטי | liveradio.co.il | ✅ רשמי | radio-browser.info | ✅ |
| 123 | TIMBR רדיו | radio.betteradio.live | ⚠️ ממסר צד שלישי | radio-browser.info | ✅ |
| 124 | רדיו אלי ניסמן | listen6.myradio24.com | ⚠️ ממסר צד שלישי | radio-browser.info | ✅ |
| 125 | רדיו קוואליטה | www.radioking.com | ⚠️ ממסר צד שלישי | radio-browser.info | ✅ |
| 126 | RadioPlus | s3.yesstreaming.net | ⚠️ ממסר צד שלישי | radio-browser.info | ❌ רק באפליקציה (HTTP) |
| 127 | AvivMedia טראנס | 176.9.7.145 | ⚠️ ממסר צד שלישי | radio-browser.info | ❌ רק באפליקציה (HTTP) |
| 128 | רדיו CS קלאסי | n0a.radiojar.com | ⚠️ ממסר צד שלישי | radio-browser.info | ✅ |

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

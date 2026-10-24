# מקורות – מאיפה מגיע כל ערוץ

נוצר אוטומטית מ-`channels.json` על ידי `tools/make_sources.py`. סה"כ **1238** פריטים: 1173 ערוצי טלוויזיה, 65 תחנות רדיו, 0 אפליקציות/שירותים.

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

- **94 שידורים רשמיים** – מהשרתים של הגוף המשדר עצמו או של המפיץ שלו (כאן, 13, 14, i24NEWS, גלי צה"ל, תחנות הרדיו, DW, אל-ג'זירה...).
- **85 ערוצי FAST** – ערוצים חינמיים עם פרסומות שהבעלים מפיצים בעצמם (Pluto TV, BabyFirst, LEGO, Baby Shark...).
- **958 ערוצים מהעולם** – שידורים פתוחים מהמאגר הציבורי iptv-org (ייבוא אוטומטי, `tools/import_iptv.py`): כל אחד נבדק שהוא משדר ושיש בו תמונה, ללא ערוצים שדורשים מנוי/כתובת מיוחדת. רובם מהשרתים של הערוצים עצמם, אבל הבעלים לא נבדקו אחד-אחד.
- **101 ממסרי צד שלישי** – בעיקר `stream.mcquack.net` (79 ערוצים). אלה **אינם** השרתים של הערוצים: אתר חיצוני קולט את השידור ומפיץ אותו מחדש. בהם נמצאים גם ערוצים בתשלום (HOT, yes, viju/TV1000, ערוצי ספורט) – סביר מאוד שההפצה הזו **ללא רישיון** מבעלי הזכויות, והם עלולים להיעלם בכל רגע. ההחלטה אם להשאיר אותם היא שלך; הסקריפטים לא מוסיפים ממסרים חדשים של ערוצים בתשלום.
- **שירותי המנויים** (פרטנר, סלקום, STING, yes, HOT) – אין בפרויקט שום שידור שלהם; הכרטיס רק פותח את האפליקציה/האתר הרשמי, וצפייה דורשת מנוי.

> הסיווג רשמי / FAST / ממסר נקבע לפי שם השרת שממנו מגיע השידור (למשל CDN של הגוף המשדר), ולא אושר מול כל גוף משדר בנפרד.

## לפי שרת

| שרת | מי זה | סוג | מספר |
|---|---|---|---|
| `stream.mcquack.net` | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | 79 |
| `jmp2.uk` | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | 78 |
| `gb25.streamgates.net` | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | 16 |
| `freem3u.xyz` | freem3u.xyz | 🌐 ציבורי (iptv-org) | 14 |
| `cdn.cybercdn.live` | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | 14 |
| `92.114.85.87` | 92.114.85.87 | 🌐 ציבורי (iptv-org) | 13 |
| `38.75.136.137` | 38.75.136.137 | 🌐 ציבורי (iptv-org) | 10 |
| `live.kwikmotion.com` | live.kwikmotion.com | 🌐 ציבורי (iptv-org) | 9 |
| `dash2.antik.sk` | dash2.antik.sk | 🌐 ציבורי (iptv-org) | 8 |
| `dash3.antik.sk` | Antik Telecom (סלובקיה), נתיבי test | ⚠️ ממסר צד שלישי | 7 |
| `playerservices.streamtheworld.com` | Triton StreamTheWorld – מפיץ הרדיו של כאן | ✅ רשמי | 7 |
| `n18syndication.akamaized.net` | n18syndication.akamaized.net | 🌐 ציבורי (iptv-org) | 6 |
| `svs.itworkscdn.net` | Asharq Discovery – ItWorks (רשמי) | ✅ רשמי | 6 |
| `hls02ns.antenaplay.ro` | hls02ns.antenaplay.ro | 🌐 ציבורי (iptv-org) | 6 |
| `stream.smotrim.ru` | Smotrim (VGTRK) – הבעלים של קרוסלה | ✅ רשמי | 6 |
| `glzwizzlv.bynetcdn.com` | גלי צה"ל / גלגלצ – CDN רשמי (Bynet) | ✅ רשמי | 6 |
| `ngtrk.dc.beltelecom.by` | ngtrk.dc.beltelecom.by | 🌐 ציבורי (iptv-org) | 5 |
| `alpha.tv.online.tm` | alpha.tv.online.tm | 🌐 ציבורי (iptv-org) | 5 |
| `nosignal1.antenaplay.ro` | nosignal1.antenaplay.ro | 🌐 ציבורי (iptv-org) | 5 |
| `redirector.rudo.video` | redirector.rudo.video | 🌐 ציבורי (iptv-org) | 5 |
| `mdstrm.com` | mdstrm.com | 🌐 ציבורי (iptv-org) | 5 |
| `mn-nl.mncdn.com` | mn-nl.mncdn.com | 🌐 ציבורי (iptv-org) | 5 |
| `138.121.15.230` | 138.121.15.230 | 🌐 ציבורי (iptv-org) | 5 |
| `live20.bozztv.com` | live20.bozztv.com | 🌐 ציבורי (iptv-org) | 5 |
| `168.196.127.137` | 168.196.127.137 | 🌐 ציבורי (iptv-org) | 4 |
| `hlspackager.akamaized.net` | hlspackager.akamaized.net | 🌐 ציבורי (iptv-org) | 4 |
| `dash4.antik.sk` | dash4.antik.sk | 🌐 ציבורי (iptv-org) | 4 |
| `livefta.malimarcdn.com` | livefta.malimarcdn.com | 🌐 ציבורי (iptv-org) | 4 |
| `edge.enhdtv.com` | edge.enhdtv.com | 🌐 ציבורי (iptv-org) | 4 |
| `w2.manasat.com` | w2.manasat.com | 🌐 ציבורי (iptv-org) | 4 |
| `playlist.fasttvcdn.com` | playlist.fasttvcdn.com | 🌐 ציבורי (iptv-org) | 4 |
| `c.mjh.nz` | c.mjh.nz | 🌐 ציבורי (iptv-org) | 4 |
| `74.91.26.218` | 74.91.26.218 | 🌐 ציבורי (iptv-org) | 4 |
| `drk6xq0vhn.gpcdn.net` | drk6xq0vhn.gpcdn.net | 🌐 ציבורי (iptv-org) | 4 |
| `rpn.bozztv.com` | rpn.bozztv.com | 🌐 ציבורי (iptv-org) | 4 |
| `kancdn.medonecdn.net` | כאן – תאגיד השידור (CDN רשמי) | ✅ רשמי | 3 |
| `streams2.sofast.tv` | streams2.sofast.tv | 🌐 ציבורי (iptv-org) | 3 |
| `rnttwmjcin.turknet.ercdn.net` | rnttwmjcin.turknet.ercdn.net | 🌐 ציבורי (iptv-org) | 3 |
| `5c7b683162943.streamlock.net` | 5c7b683162943.streamlock.net | 🌐 ציבורי (iptv-org) | 3 |
| `master.tucableip.com` | master.tucableip.com | 🌐 ציבורי (iptv-org) | 3 |
| `d4whmvwm0rdvi.cloudfront.net` | d4whmvwm0rdvi.cloudfront.net | 🌐 ציבורי (iptv-org) | 3 |
| `a-cdn.klowdtv.com` | a-cdn.klowdtv.com | 🌐 ציבורי (iptv-org) | 3 |
| `lbgo.bozztv.com` | lbgo.bozztv.com | 🌐 ציבורי (iptv-org) | 3 |
| `fastly.live.brightcove.com` | fastly.live.brightcove.com | 🌐 ציבורי (iptv-org) | 3 |
| `edge20.vedge.infomaniak.com` | edge20.vedge.infomaniak.com | 🌐 ציבורי (iptv-org) | 3 |
| `live.alarabiya.net` | live.alarabiya.net | 🌐 ציבורי (iptv-org) | 3 |
| `5fc584f3f19c9.streamlock.net` | 5fc584f3f19c9.streamlock.net | 🌐 ציבורי (iptv-org) | 3 |
| `vodzong.mjunoon.tv` | vodzong.mjunoon.tv | 🌐 ציבורי (iptv-org) | 3 |
| `cdn.live.easybroadcast.io` | cdn.live.easybroadcast.io | 🌐 ציבורי (iptv-org) | 3 |
| `hls.newworldtv.com` | hls.newworldtv.com | 🌐 ציבורי (iptv-org) | 3 |
| `stream.berosat.live` | stream.berosat.live | 🌐 ציבורי (iptv-org) | 3 |
| `tgn.bozztv.com` | tgn.bozztv.com | 🌐 ציבורי (iptv-org) | 3 |
| `cdn15.live-tv.cloud` | live-tv.cloud – ספק IPTV אוקראיני | ⚠️ ממסר צד שלישי | 3 |
| `live-fi.tvkaista.net` | live-fi.tvkaista.net | 🌐 ציבורי (iptv-org) | 3 |
| `fl.biztv.media` | fl.biztv.media | 🌐 ציבורי (iptv-org) | 3 |
| `stream1.antenaplay.ro` | stream1.antenaplay.ro | 🌐 ציבורי (iptv-org) | 3 |
| `stmv1.srvif.com` | stmv1.srvif.com | 🌐 ציבורי (iptv-org) | 3 |
| `200.115.120.1` | 200.115.120.1 | 🌐 ציבורי (iptv-org) | 3 |
| `fs.uplink.kz` | fs.uplink.kz | 🌐 ציבורי (iptv-org) | 3 |
| `stream-us-east-1.getpublica.com` | stream-us-east-1.getpublica.com | 🌐 ציבורי (iptv-org) | 3 |
| `lotus.stingray.com` | lotus.stingray.com | 🌐 ציבורי (iptv-org) | 3 |
| `livecdn.live247stream.com` | livecdn.live247stream.com | 🌐 ציבורי (iptv-org) | 3 |
| `live2.tensila.com` | live2.tensila.com | 🌐 ציבורי (iptv-org) | 3 |
| `edge12.vedge.infomaniak.com` | edge12.vedge.infomaniak.com | 🌐 ציבורי (iptv-org) | 3 |
| `stream.sen-gt.com` | stream.sen-gt.com | 🌐 ציבורי (iptv-org) | 3 |
| `aegis-cloudfront-1.tubi.video` | aegis-cloudfront-1.tubi.video | 🌐 ציבורי (iptv-org) | 3 |
| `fastlive.cctvplus.com` | fastlive.cctvplus.com | 🌐 ציבורי (iptv-org) | 3 |
| `stream.swamiji.tv` | stream.swamiji.tv | 🌐 ציבורי (iptv-org) | 3 |
| `cdn3.wowza.com` | cdn3.wowza.com | 🌐 ציבורי (iptv-org) | 3 |
| `edge13.vedge.infomaniak.com` | edge13.vedge.infomaniak.com | 🌐 ציבורי (iptv-org) | 3 |
| `cdna.streamgates.net` | StreamGates – ה-CDN של התחנה | ✅ רשמי | 3 |
| `jointil.com` | jointil.com | ⚠️ ממסר צד שלישי | 3 |
| `live4dai.antenaplay.ro` | live4dai.antenaplay.ro | 🌐 ציבורי (iptv-org) | 2 |
| `streaming.onnethn.com` | streaming.onnethn.com | 🌐 ציבורי (iptv-org) | 2 |
| `partneta.cdn.mgmlcdn.com` | partneta.cdn.mgmlcdn.com | 🌐 ציבורי (iptv-org) | 2 |
| `stream3.cinerama.uz` | stream3.cinerama.uz | 🌐 ציבורי (iptv-org) | 2 |
| `live.mnb.mn` | live.mnb.mn | 🌐 ציבורי (iptv-org) | 2 |
| `d3k8wzt41aflvx.cloudfront.net` | d3k8wzt41aflvx.cloudfront.net | 🌐 ציבורי (iptv-org) | 2 |
| `unlimited6-cl.dps.live` | unlimited6-cl.dps.live | 🌐 ציבורי (iptv-org) | 2 |
| `mumbai-edge.smartplaytv.in` | mumbai-edge.smartplaytv.in | 🌐 ציבורי (iptv-org) | 2 |
| `streams.prd.commavservices.eu` | streams.prd.commavservices.eu | 🌐 ציבורי (iptv-org) | 2 |
| `viewmedia7219.bozztv.com` | viewmedia7219.bozztv.com | 🌐 ציבורי (iptv-org) | 2 |
| `ss2.tvrdomi.com` | ss2.tvrdomi.com | 🌐 ציבורי (iptv-org) | 2 |
| `jmc-live.ercdn.net` | jmc-live.ercdn.net | 🌐 ציבורי (iptv-org) | 2 |
| `ptvlive.kordia.net.nz` | ptvlive.kordia.net.nz | 🌐 ציבורי (iptv-org) | 2 |
| `partwota.cdn.mgmlcdn.com` | partwota.cdn.mgmlcdn.com | 🌐 ציבורי (iptv-org) | 2 |
| `kali.vdopanel.com` | kali.vdopanel.com | 🌐 ציבורי (iptv-org) | 2 |
| `gjirafa-video-live.gjirafa.net` | gjirafa-video-live.gjirafa.net | 🌐 ציבורי (iptv-org) | 2 |
| `africa24.vedge.infomaniak.com` | africa24.vedge.infomaniak.com | 🌐 ציבורי (iptv-org) | 2 |
| `directes-tv-int.3catdirectes.cat` | directes-tv-int.3catdirectes.cat | 🌐 ציבורי (iptv-org) | 2 |
| `live-vgtrksmotrim.cdnvideo.ru` | live-vgtrksmotrim.cdnvideo.ru | 🌐 ציבורי (iptv-org) | 2 |
| `citynewsregional.akamaized.net` | citynewsregional.akamaized.net | 🌐 ציבורי (iptv-org) | 2 |
| `cdn.live.br1.jmvstream.com` | cdn.live.br1.jmvstream.com | 🌐 ציבורי (iptv-org) | 2 |
| `edge.essastream.com` | edge.essastream.com | 🌐 ציבורי (iptv-org) | 2 |
| `iptv.bitred.cl` | iptv.bitred.cl | 🌐 ציבורי (iptv-org) | 2 |
| `cdn.streamhispanatv.net` | cdn.streamhispanatv.net | 🌐 ציבורי (iptv-org) | 2 |
| `cnn.hostlagarto.com` | cnn.hostlagarto.com | 🌐 ציבורי (iptv-org) | 2 |
| `103.151.60.162` | 103.151.60.162 | 🌐 ציבורי (iptv-org) | 2 |
| `stream.qazcdn.net` | stream.qazcdn.net | 🌐 ציבורי (iptv-org) | 2 |
| `video1.getstreamhosting.com` | video1.getstreamhosting.com | 🌐 ציבורי (iptv-org) | 2 |
| `203.81.84.130` | 203.81.84.130 | 🌐 ציבורי (iptv-org) | 2 |
| `15.204.196.147` | 15.204.196.147 | 🌐 ציבורי (iptv-org) | 2 |
| `raw.githubusercontent.com` | raw.githubusercontent.com | 🌐 ציבורי (iptv-org) | 2 |
| `fast-channels.sinclairstoryline.com` | fast-channels.sinclairstoryline.com | 🌐 ציבורי (iptv-org) | 2 |
| `45.162.64.114` | 45.162.64.114 | 🌐 ציבורי (iptv-org) | 2 |
| `hls.botflix.tv` | hls.botflix.tv | 🌐 ציבורי (iptv-org) | 2 |
| `streamer1.streamhost.org` | streamer1.streamhost.org | 🌐 ציבורי (iptv-org) | 2 |
| `stream.4fun.tv` | 4FUN TV – שרת רשמי | ✅ רשמי | 2 |
| `hls01ns.antenaplay.ro` | hls01ns.antenaplay.ro | 🌐 ציבורי (iptv-org) | 2 |
| `cdnlive.klicgo.net` | cdnlive.klicgo.net | 🌐 ציבורי (iptv-org) | 2 |
| `5dcabf026b188.streamlock.net` | 5dcabf026b188.streamlock.net | 🌐 ציבורי (iptv-org) | 2 |
| `cloudvideo.servers10.com` | cloudvideo.servers10.com | 🌐 ציבורי (iptv-org) | 2 |
| `sb.err.ee` | sb.err.ee | 🌐 ציבורי (iptv-org) | 2 |
| `170.83.49.66` | 170.83.49.66 | 🌐 ציבורי (iptv-org) | 2 |
| `tv.wracanal10.com` | tv.wracanal10.com | 🌐 ציבורי (iptv-org) | 2 |
| `585b674743bbb.streamlock.net` | 70-80 TV – Wowza Streamlock | ✅ רשמי | 2 |
| `channels.trace.plus` | channels.trace.plus | 🌐 ציבורי (iptv-org) | 2 |
| `tv1.intv.mk` | tv1.intv.mk | 🌐 ציבורי (iptv-org) | 2 |
| `cdn.streamlane.tv` | cdn.streamlane.tv | 🌐 ציבורי (iptv-org) | 2 |
| `rp.tactivemedia.com` | rp.tactivemedia.com | 🌐 ציבורי (iptv-org) | 2 |
| `media.streambrothers.com` | media.streambrothers.com | 🌐 ציבורי (iptv-org) | 2 |
| `stmv3.voxtvhd.com.br` | stmv3.voxtvhd.com.br | 🌐 ציבורי (iptv-org) | 2 |
| `movil.ejeserver.com` | movil.ejeserver.com | 🌐 ציבורי (iptv-org) | 2 |
| `cdn01.setar.aw` | cdn01.setar.aw | 🌐 ציבורי (iptv-org) | 2 |
| `live.artidijitalmedya.com` | live.artidijitalmedya.com | 🌐 ציבורי (iptv-org) | 2 |
| `videostream.shockmedia.com.ar` | videostream.shockmedia.com.ar | 🌐 ציבורי (iptv-org) | 2 |
| `cdn-live.dltv.ac.th` | cdn-live.dltv.ac.th | 🌐 ציבורי (iptv-org) | 2 |
| `cdn4.mjunoon.tv` | cdn4.mjunoon.tv | 🌐 ציבורי (iptv-org) | 2 |
| `streamingcws30.com` | streamingcws30.com | 🌐 ציבורי (iptv-org) | 2 |
| `5ca9af4645e15.streamlock.net` | 5ca9af4645e15.streamlock.net | 🌐 ציבורי (iptv-org) | 2 |
| `stream8.cinerama.uz` | Cinerama.uz – ספק IPTV באוזבקיסטן | ⚠️ ממסר צד שלישי | 2 |
| `da86m1sqpm3o0.cloudfront.net` | da86m1sqpm3o0.cloudfront.net | 🌐 ציבורי (iptv-org) | 2 |
| `cdn.freevisiontv.co.za` | cdn.freevisiontv.co.za | 🌐 ציבורי (iptv-org) | 2 |
| `60417ddeaf0d9.streamlock.net` | 60417ddeaf0d9.streamlock.net | 🌐 ציבורי (iptv-org) | 2 |
| `5c46fa289c89f.streamlock.net` | 5c46fa289c89f.streamlock.net | 🌐 ציבורי (iptv-org) | 2 |
| `2-fss-2.streamhoster.com` | 2-fss-2.streamhoster.com | 🌐 ציבורי (iptv-org) | 2 |
| `streaming.aurora.enhanced.live` | streaming.aurora.enhanced.live | 🌐 ציבורי (iptv-org) | 2 |
| `ncdn-live-bfm.pfd.sfr.net` | BFM – SFR (רשמי) | ✅ רשמי | 2 |
| `strhlslb01.streamakaci.tv` | strhlslb01.streamakaci.tv | 🌐 ציבורי (iptv-org) | 2 |
| `d1hz1p2gea2zy4.cloudfront.net` | d1hz1p2gea2zy4.cloudfront.net | 🌐 ציבורי (iptv-org) | 2 |
| `radio.streamgates.net` | StreamGates – ה-CDN של התחנה | ✅ רשמי | 2 |
| `media2.93fm.co.il` | קול חי – שרת התחנה | ✅ רשמי | 2 |
| `live.ecast.co.il` | eCast – שרת התחנה | ✅ רשמי | 2 |
| `cast.breslevforyou.co.il` | רדיו ברסלב – שרת התחנה | ✅ רשמי | 2 |
| `d18b0e6mopany4.cloudfront.net` | רשת 13 – AWS CloudFront | ✅ רשמי | 1 |
| `r.il.cdn-redge.media` | ערוץ 14 – CDN רשמי (Redge) | ✅ רשמי | 1 |
| `i24newshebrew-cdn.encoders.immergo.tv` | i24NEWS – שרת רשמי | ✅ רשמי | 1 |
| `contact.gostreaming.tv` | GoStreaming – ספק השידור של ערוץ 9 | ✅ רשמי | 1 |
| `i24newsfrench-cdn.encoders.immergo.tv` | i24NEWS – שרת רשמי | ✅ רשמי | 1 |
| `i24newsarabic-cdn.encoders.immergo.tv` | i24NEWS – שרת רשמי | ✅ רשמי | 1 |
| `i24newsenglish-cdn.encoders.immergo.tv` | i24NEWS – שרת רשמי | ✅ רשמי | 1 |
| `live.creacast.com` | live.creacast.com | ⚠️ ממסר צד שלישי | 1 |
| `rbmn-live.akamaized.net` | Red Bull TV – Akamai (רשמי) | ✅ רשמי | 1 |
| `raycom-accdn-firetv.amagi.tv` | raycom-accdn-firetv.amagi.tv | 🌐 ציבורי (iptv-org) | 1 |
| `af7a8b4e.wurl.com` | af7a8b4e.wurl.com | 🌐 ציבורי (iptv-org) | 1 |
| `mts1.iptv-playoutcenter.de` | mts1.iptv-playoutcenter.de | 🌐 ציבורי (iptv-org) | 1 |
| `webstream.multistream.it` | webstream.multistream.it | 🌐 ציבורי (iptv-org) | 1 |
| `d3pnbvng3bx2nj.cloudfront.net` | d3pnbvng3bx2nj.cloudfront.net | 🌐 ציבורי (iptv-org) | 1 |
| `d3qs3d2rkhfqrt.cloudfront.net` | d3qs3d2rkhfqrt.cloudfront.net | 🌐 ציבורי (iptv-org) | 1 |
| `live-manifest.production-public.tubi.io` | live-manifest.production-public.tubi.io | 🌐 ציבורי (iptv-org) | 1 |
| `158.172.155.130` | 158.172.155.130 | 🌐 ציבורי (iptv-org) | 1 |
| `stmv2.voxtvhd.com.br` | stmv2.voxtvhd.com.br | 🌐 ציבורי (iptv-org) | 1 |
| `h.jcp.live.opencaster.com` | h.jcp.live.opencaster.com | 🌐 ציבורי (iptv-org) | 1 |
| `live-us1.thaimomo.com` | live-us1.thaimomo.com | 🌐 ציבורי (iptv-org) | 1 |
| `cdn.rabta.stream` | cdn.rabta.stream | 🌐 ציבורי (iptv-org) | 1 |
| `149.100.11.252` | 149.100.11.252 | 🌐 ציבורי (iptv-org) | 1 |
| `kwtspta.cdn.mangomolo.com` | kwtspta.cdn.mangomolo.com | 🌐 ציבורי (iptv-org) | 1 |
| `hls.tvabierta.net` | hls.tvabierta.net | 🌐 ציבורי (iptv-org) | 1 |
| `cast.talenttv.lk` | cast.talenttv.lk | 🌐 ציבורי (iptv-org) | 1 |
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
| `samitaorigin.tangotv.in` | samitaorigin.tangotv.in | 🌐 ציבורי (iptv-org) | 1 |
| `138.204.86.4` | 138.204.86.4 | 🌐 ציבורי (iptv-org) | 1 |
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
| `takeoff.jetstre.am` | takeoff.jetstre.am | 🌐 ציבורי (iptv-org) | 1 |
| `streaming.meteonews.net` | streaming.meteonews.net | 🌐 ציבורי (iptv-org) | 1 |
| `cdn-euronews.akamaized.net` | cdn-euronews.akamaized.net | 🌐 ציבורי (iptv-org) | 1 |
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
| `eu1.servers10.com` | eu1.servers10.com | 🌐 ציבורי (iptv-org) | 1 |
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
| `rt-ger.rttv.com` | rt-ger.rttv.com | 🌐 ציבורי (iptv-org) | 1 |
| `senato-live.morescreens.com` | senato-live.morescreens.com | 🌐 ציבורי (iptv-org) | 1 |
| `streamvideol1.luxnet.ua` | streamvideol1.luxnet.ua | 🌐 ציבורי (iptv-org) | 1 |
| `skynewsau-live-premium.akamaized.net` | skynewsau-live-premium.akamaized.net | 🌐 ציבורי (iptv-org) | 1 |
| `rch01e-alive-hls.akamaized.net` | rch01e-alive-hls.akamaized.net | 🌐 ציבורי (iptv-org) | 1 |
| `amdlive-ch02-ctnd-com.akamaized.net` | amdlive-ch02-ctnd-com.akamaized.net | 🌐 ציבורי (iptv-org) | 1 |
| `5e50264bd6766.streamlock.net` | 5e50264bd6766.streamlock.net | 🌐 ציבורי (iptv-org) | 1 |
| `vivo.solumedia.com` | vivo.solumedia.com | 🌐 ציבורי (iptv-org) | 1 |
| `github.com` | github.com | 🌐 ציבורי (iptv-org) | 1 |
| `stream.tvkstella.pl` | stream.tvkstella.pl | 🌐 ציבורי (iptv-org) | 1 |
| `ert-ucdn.broadpeak-aas.com` | ert-ucdn.broadpeak-aas.com | 🌐 ציבורי (iptv-org) | 1 |
| `cdn0-03837-liveedge0.dna.ip-only.net` | cdn0-03837-liveedge0.dna.ip-only.net | 🌐 ציבורי (iptv-org) | 1 |
| `stream-aleph.m.ro` | stream-aleph.m.ro | 🌐 ציבורי (iptv-org) | 1 |
| `redirector.dps.live` | redirector.dps.live | 🌐 ציבורי (iptv-org) | 1 |
| `stream1.bitred.cl` | stream1.bitred.cl | 🌐 ציבורי (iptv-org) | 1 |
| `live.obslivestream.com` | live.obslivestream.com | 🌐 ציבורי (iptv-org) | 1 |
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
| `5db313b643fd8.streamlock.net` | 5db313b643fd8.streamlock.net | 🌐 ציבורי (iptv-org) | 1 |
| `rt-arb.rttv.com` | rt-arb.rttv.com | 🌐 ציבורי (iptv-org) | 1 |
| `live-hls-7agy.livepush.io` | live-hls-7agy.livepush.io | 🌐 ציבורי (iptv-org) | 1 |
| `live-hls-web-aja2-gcp.thehlive.com` | live-hls-web-aja2-gcp.thehlive.com | 🌐 ציבורי (iptv-org) | 1 |
| `live.paltodaytv.com` | live.paltodaytv.com | 🌐 ציבורי (iptv-org) | 1 |
| `tv-trtarabi.medya.trt.com.tr` | tv-trtarabi.medya.trt.com.tr | 🌐 ציבורי (iptv-org) | 1 |
| `d2dffl3588mvfk.cloudfront.net` | ערוץ FAST ישראלי – AWS CloudFront | ✅ FAST חינמי | 1 |
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
| `actionlive.siliconweb.com` | actionlive.siliconweb.com | 🌐 ציבורי (iptv-org) | 1 |
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
| `38.19.41.46` | 38.19.41.46 | 🌐 ציבורי (iptv-org) | 1 |
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
| `d15ds134q59udk.cloudfront.net` | ערוץ FAST ישראלי – AWS CloudFront | ✅ FAST חינמי | 1 |
| `xumo-xumoent-vc-105-z0vpm.fast.nbcuni.com` | xumo-xumoent-vc-105-z0vpm.fast.nbcuni.com | 🌐 ציבורי (iptv-org) | 1 |
| `comedyhls.persiana.live` | comedyhls.persiana.live | 🌐 ציבורי (iptv-org) | 1 |
| `team-tv.co` | team-tv.co | 🌐 ציבורי (iptv-org) | 1 |
| `cc-wie8j8y69d2uy.akamaized.net` | cc-wie8j8y69d2uy.akamaized.net | 🌐 ציבורי (iptv-org) | 1 |
| `stmv2.srvif.com` | stmv2.srvif.com | 🌐 ציבורי (iptv-org) | 1 |
| `segment.yuppcdn.net` | segment.yuppcdn.net | 🌐 ציבורי (iptv-org) | 1 |
| `tvoetv.space` | tvoetv.space | 🌐 ציבורי (iptv-org) | 1 |
| `30a-tv.com` | 30a-tv.com | 🌐 ציבורי (iptv-org) | 1 |
| `linear-12.frequency.stream` | linear-12.frequency.stream | 🌐 ציבורי (iptv-org) | 1 |
| `drybar-drybarcomedy-1-au.samsung.wurl.tv` | drybar-drybarcomedy-1-au.samsung.wurl.tv | 🌐 ציבורי (iptv-org) | 1 |
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
| `cc-t8lqe1o99pszu.akamaized.net` | cc-t8lqe1o99pszu.akamaized.net | 🌐 ציבורי (iptv-org) | 1 |
| `ebsonair.ebs.co.kr` | ebsonair.ebs.co.kr | 🌐 ציבורי (iptv-org) | 1 |
| `x1colegal.com` | x1colegal.com | 🌐 ציבורי (iptv-org) | 1 |
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
| `190.14.10.19` | 190.14.10.19 | 🌐 ציבורי (iptv-org) | 1 |
| `185.227.34.179` | 185.227.34.179 | 🌐 ציבורי (iptv-org) | 1 |
| `65f16f0fdfc51.streamlock.net` | 65f16f0fdfc51.streamlock.net | 🌐 ציבורי (iptv-org) | 1 |
| `tvsen7.aynascope.net` | tvsen7.aynascope.net | 🌐 ציבורי (iptv-org) | 1 |
| `495c5a85d9074f29acffeaea9e0215eb.msvdn.net` | 495c5a85d9074f29acffeaea9e0215eb.msvdn.net | 🌐 ציבורי (iptv-org) | 1 |
| `radm.mediacdn.ru` | radm.mediacdn.ru | 🌐 ציבורי (iptv-org) | 1 |
| `server.zillarbarta.com` | server.zillarbarta.com | 🌐 ציבורי (iptv-org) | 1 |
| `138.59.227.20` | 138.59.227.20 | 🌐 ציבורי (iptv-org) | 1 |
| `online.godstands.tv` | online.godstands.tv | 🌐 ציבורי (iptv-org) | 1 |
| `livestream.pbskids.org` | livestream.pbskids.org | 🌐 ציבורי (iptv-org) | 1 |
| `tv-trtdiyanetcocuk.medya.trt.com.tr` | tv-trtdiyanetcocuk.medya.trt.com.tr | 🌐 ציבורי (iptv-org) | 1 |
| `176.118.197.101` | שרת IP ללא שם (1HD) | ⚠️ ממסר צד שלישי | 1 |
| `stream.ecable.tv` | stream.ecable.tv | 🌐 ציבורי (iptv-org) | 1 |
| `d-m-f.iptv-playoutcenter.de` | d-m-f.iptv-playoutcenter.de | 🌐 ציבורי (iptv-org) | 1 |
| `stream2.xdevel.com` | stream2.xdevel.com | 🌐 ציבורי (iptv-org) | 1 |
| `live-maidantv.cdnvideo.ru` | live-maidantv.cdnvideo.ru | 🌐 ציבורי (iptv-org) | 1 |
| `tv.mediacp.eu` | tv.mediacp.eu | 🌐 ציבורי (iptv-org) | 1 |
| `dbbv9umqcd7cs.cloudfront.net` | dbbv9umqcd7cs.cloudfront.net | 🌐 ציבורי (iptv-org) | 1 |
| `nogoumtv.nrpstream.com` | nogoumtv.nrpstream.com | 🌐 ציבורי (iptv-org) | 1 |
| `livetv.powerapp.com.tr` | livetv.powerapp.com.tr | 🌐 ציבורי (iptv-org) | 1 |
| `wiselp.wiseplayout.com` | wiselp.wiseplayout.com | 🌐 ציבורי (iptv-org) | 1 |
| `mgugaklive.nowcdn.co.kr` | mgugaklive.nowcdn.co.kr | 🌐 ציבורי (iptv-org) | 1 |
| `d1bl6tskrpq9ze.cloudfront.net` | d1bl6tskrpq9ze.cloudfront.net | 🌐 ציבורי (iptv-org) | 1 |
| `5ce9406b73c33.streamlock.net` | 5ce9406b73c33.streamlock.net | 🌐 ציבורי (iptv-org) | 1 |
| `live.vrtcdn.be` | live.vrtcdn.be | 🌐 ציבורי (iptv-org) | 1 |
| `60efd7a2b4d02.streamlock.net` | 60efd7a2b4d02.streamlock.net | 🌐 ציבורי (iptv-org) | 1 |
| `1888165924.rsc.cdn77.org` | 1888165924.rsc.cdn77.org | 🌐 ציבורי (iptv-org) | 1 |
| `eco.streams.ovh` | eco.streams.ovh | 🌐 ציבורי (iptv-org) | 1 |
| `ocko-live.ssl.cdn.cra.cz` | ocko-live.ssl.cdn.cra.cz | 🌐 ציבורי (iptv-org) | 1 |
| `oxygenmusic.hu` | oxygenmusic.hu | 🌐 ציבורי (iptv-org) | 1 |
| `live.ondadigitaltv.com` | live.ondadigitaltv.com | 🌐 ציבורי (iptv-org) | 1 |
| `bozztv.com` | bozztv.com | 🌐 ציבורי (iptv-org) | 1 |
| `customer-yzxu6n447dx2x45h.cloudflarestream.com` | customer-yzxu6n447dx2x45h.cloudflarestream.com | 🌐 ציבורי (iptv-org) | 1 |
| `tigocloud.desdeparaguay.net` | tigocloud.desdeparaguay.net | 🌐 ציבורי (iptv-org) | 1 |
| `m1b2.worldcast.tv` | m1b2.worldcast.tv | 🌐 ציבורי (iptv-org) | 1 |
| `bss1.neterra.tv` | bss1.neterra.tv | 🌐 ציבורי (iptv-org) | 1 |
| `streamlov.alsolnet.com` | streamlov.alsolnet.com | 🌐 ציבורי (iptv-org) | 1 |
| `peer2.tdiradio.com` | peer2.tdiradio.com | 🌐 ציבורי (iptv-org) | 1 |
| `streaming.imagenfm105-1.com` | streaming.imagenfm105-1.com | 🌐 ציבורי (iptv-org) | 1 |
| `cloud.fastchannel.es` | cloud.fastchannel.es | 🌐 ציבורי (iptv-org) | 1 |
| `583ed2f226ac2.streamlock.net` | 583ed2f226ac2.streamlock.net | 🌐 ציבורי (iptv-org) | 1 |
| `tezaurtv.md` | tezaurtv.md | 🌐 ציבורי (iptv-org) | 1 |
| `stream.rtl.lu` | stream.rtl.lu | 🌐 ציבורי (iptv-org) | 1 |
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
| `58f12ffd2447a.streamlock.net` | 58f12ffd2447a.streamlock.net | 🌐 ציבורי (iptv-org) | 1 |
| `hls-tvsoyuz.cdnvideo.ru` | hls-tvsoyuz.cdnvideo.ru | 🌐 ציבורי (iptv-org) | 1 |
| `pmchls.wns.live` | pmchls.wns.live | 🌐 ציבורי (iptv-org) | 1 |
| `9090video.mobtada.com` | 9090video.mobtada.com | 🌐 ציבורי (iptv-org) | 1 |
| `hms.pfs.gdn` | hms.pfs.gdn | 🌐 ציבורי (iptv-org) | 1 |
| `amg00793-amg00793c6-xumo-us-2669.playouts.now.amagi.tv` | BBC Earth – Amagi / Xumo FAST | ✅ FAST חינמי | 1 |
| `d1si3n1st4nkgb.cloudfront.net` | d1si3n1st4nkgb.cloudfront.net | 🌐 ציבורי (iptv-org) | 1 |
| `lds-timeline-rakuten.amagi.tv` | lds-timeline-rakuten.amagi.tv | 🌐 ציבורי (iptv-org) | 1 |
| `5421175365ea3.streamlock.net` | 5421175365ea3.streamlock.net | 🌐 ציבורי (iptv-org) | 1 |
| `a57e9c69976649b582a8d7604c00e69a.mediatailor.us-east-1.amazonaws.com` | a57e9c69976649b582a8d7604c00e69a.mediatailor.us-east-1.amazonaws.com | 🌐 ציבורי (iptv-org) | 1 |
| `rt-rtd.rttv.com` | rt-rtd.rttv.com | 🌐 ציבורי (iptv-org) | 1 |
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
| `restreamer.indonusa.id` | restreamer.indonusa.id | 🌐 ציבורי (iptv-org) | 1 |
| `live.canthotv.vn` | live.canthotv.vn | 🌐 ציבורי (iptv-org) | 1 |
| `live.kh.malimarcdn.com` | live.kh.malimarcdn.com | 🌐 ציבורי (iptv-org) | 1 |
| `video.hostingcaaguazu.com` | video.hostingcaaguazu.com | 🌐 ציבורי (iptv-org) | 1 |
| `avm.ecuamedia.net` | avm.ecuamedia.net | 🌐 ציבורי (iptv-org) | 1 |
| `live.koryocdn.org` | live.koryocdn.org | 🌐 ציבורי (iptv-org) | 1 |
| `create.lls.pbs.org` | create.lls.pbs.org | 🌐 ציבורי (iptv-org) | 1 |
| `tv-e-okul01.medya.trt.com.tr` | tv-e-okul01.medya.trt.com.tr | 🌐 ציבורי (iptv-org) | 1 |
| `cdn.pishow.tv` | cdn.pishow.tv | 🌐 ציבורי (iptv-org) | 1 |
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
| `fox.hostlagarto.com` | fox.hostlagarto.com | 🌐 ציבורי (iptv-org) | 1 |
| `sv72.ecuaradiotv.net` | sv72.ecuaradiotv.net | 🌐 ציבורי (iptv-org) | 1 |
| `rds3.desdeparaguay.net` | rds3.desdeparaguay.net | 🌐 ציבורי (iptv-org) | 1 |
| `cdn2.invivo.bg` | cdn2.invivo.bg | 🌐 ציבורי (iptv-org) | 1 |
| `streamfi-alphadgtl1.zettawiseroutes.com` | streamfi-alphadgtl1.zettawiseroutes.com | 🌐 ציבורי (iptv-org) | 1 |
| `hls.shift.tv` | hls.shift.tv | 🌐 ציבורי (iptv-org) | 1 |
| `med-tv-it.fast.rakuten.tv` | med-tv-it.fast.rakuten.tv | 🌐 ציבורי (iptv-org) | 1 |
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
| `seswa.bonce.tv` | seswa.bonce.tv | 🌐 ציבורי (iptv-org) | 1 |
| `stream.eldo.lu` | stream.eldo.lu | 🌐 ציבורי (iptv-org) | 1 |
| `103.215.194.93` | 103.215.194.93 | 🌐 ציבורי (iptv-org) | 1 |
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
| `stream.panet.com` | פאנט – הלא TV (רשמי) | ✅ רשמי | 1 |
| `edge3.uk.kab.tv` | קבלה לעם – שרת רשמי | ✅ רשמי | 1 |
| `1247634592.rsc.cdn77.org` | שלנו TV – CDN77 | ✅ רשמי | 1 |
| `ch16israel-cdn.encoders.immergo.tv` | ערוץ 16 – שרת רשמי | ✅ רשמי | 1 |
| `jlt-jltv-xumo.otteravision.com` | jlt-jltv-xumo.otteravision.com | ⚠️ ממסר צד שלישי | 1 |
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
| `stream.nasatv.com.mk` | stream.nasatv.com.mk | 🌐 ציבורי (iptv-org) | 1 |
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
| `ktvhdsg.ekantipur.com` | ktvhdsg.ekantipur.com | 🌐 ציבורי (iptv-org) | 1 |
| `lbs-us1.suprememastertv.com` | lbs-us1.suprememastertv.com | 🌐 ציבורי (iptv-org) | 1 |
| `5dcab9aed5331.streamlock.net` | 5dcab9aed5331.streamlock.net | 🌐 ציבורי (iptv-org) | 1 |
| `kilolink.pulsarstarsolutions.com` | kilolink.pulsarstarsolutions.com | 🌐 ציבורי (iptv-org) | 1 |
| `ruv-web-live.akamaized.net` | ruv-web-live.akamaized.net | 🌐 ציבורי (iptv-org) | 1 |
| `tv.tvsb.bg` | tv.tvsb.bg | 🌐 ציבורי (iptv-org) | 1 |
| `5790d294af2dc.streamlock.net` | 5790d294af2dc.streamlock.net | 🌐 ציבורי (iptv-org) | 1 |
| `live-onesignal.royalmedia.co.ke` | live-onesignal.royalmedia.co.ke | 🌐 ציבורי (iptv-org) | 1 |
| `tv.pointville.ag` | tv.pointville.ag | 🌐 ציבורי (iptv-org) | 1 |
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
| `tdameritrade-vizio.amagi.tv` | tdameritrade-vizio.amagi.tv | 🌐 ציבורי (iptv-org) | 1 |
| `live-cdn-stream-euw1.bfmb.bct.nextradiotv.com` | live-cdn-stream-euw1.bfmb.bct.nextradiotv.com | 🌐 ציבורי (iptv-org) | 1 |
| `streaming013.gestec-video.com` | streaming013.gestec-video.com | 🌐 ציבורי (iptv-org) | 1 |
| `tv-streaming.bsh.ru` | tv-streaming.bsh.ru | 🌐 ציבורי (iptv-org) | 1 |
| `d9quh89lh7dtw.cloudfront.net` | d9quh89lh7dtw.cloudfront.net | 🌐 ציבורי (iptv-org) | 1 |
| `live-news.asharq.com` | live-news.asharq.com | 🌐 ציבורי (iptv-org) | 1 |
| `tv.ensonhaber.com` | tv.ensonhaber.com | 🌐 ציבורי (iptv-org) | 1 |
| `s-pl-01.mediatool.tv` | s-pl-01.mediatool.tv | 🌐 ציבורי (iptv-org) | 1 |
| `streamw.m.ro` | streamw.m.ro | 🌐 ציבורי (iptv-org) | 1 |
| `v2.tustreaming.cl` | v2.tustreaming.cl | 🌐 ציבורי (iptv-org) | 1 |
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
| `bonappetit-samsung.amagi.tv` | bonappetit-samsung.amagi.tv | 🌐 ציבורי (iptv-org) | 1 |
| `platform-iuoamc.uk` | platform-iuoamc.uk | 🌐 ציבורי (iptv-org) | 1 |
| `tvf-tv5ch.otteravision.com` | tvf-tv5ch.otteravision.com | 🌐 ציבורי (iptv-org) | 1 |
| `api.alpaca.t62a.com` | api.alpaca.t62a.com | 🌐 ציבורי (iptv-org) | 1 |
| `e75fc053c0d2406fb74cdd71d77e1a7f.mediatailor.us-east-1.amazonaws.com` | e75fc053c0d2406fb74cdd71d77e1a7f.mediatailor.us-east-1.amazonaws.com | 🌐 ציבורי (iptv-org) | 1 |
| `mumt03.tangotv.in` | mumt03.tangotv.in | 🌐 ציבורי (iptv-org) | 1 |
| `op-flashcon-digdayahd-1.dens.tv` | op-flashcon-digdayahd-1.dens.tv | 🌐 ציבורי (iptv-org) | 1 |
| `cityeden.catcast.tv` | cityeden.catcast.tv | 🌐 ציבורי (iptv-org) | 1 |
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
| `eco-live.mediacast.co.il` | eco99fm – Mediacast (רשמי) | ✅ רשמי | 1 |
| `102.livecdn.biz` | 102FM – LiveCDN (רשמי) | ✅ רשמי | 1 |
| `kzradio.mediacast.co.il` | רדיו הקצה – Mediacast (רשמי) | ✅ רשמי | 1 |
| `1075.livecdn.biz` | רדיו חיפה – LiveCDN (רשמי) | ✅ רשמי | 1 |
| `cast.radionahariya.com` | רדיו נהריה – שרת התחנה | ✅ רשמי | 1 |
| `www.rs.mizrahit.fm` | www.rs.mizrahit.fm | ⚠️ ממסר צד שלישי | 1 |
| `icecast.live` | icecast.live | ⚠️ ממסר צד שלישי | 1 |
| `icecast.kab.tv` | icecast.kab.tv | ⚠️ ממסר צד שלישי | 1 |
| `stream10.vestaradio.com` | stream10.vestaradio.com | ⚠️ ממסר צד שלישי | 1 |

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
| 13 | ILTV ישראל בצרפתית | live.creacast.com | ⚠️ ממסר צד שלישי | iptv-org (`ILTV.fr`) | ✅ |

## 📺 ילדים ונוער (בעברית)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 14 | כאן חינוכית 23 HD | כאן – תאגיד השידור (CDN רשמי) | ✅ רשמי | iptv-org (`KanEducational.il`) | ✅ |
| 15 | ערוץ הופ! ילדים בעברית HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Hop.il`) | ❌ רק באפליקציה (HTTP) |
| 16 | ZOOM ילדים ונוער | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Zoom.il`) | ❌ רק באפליקציה (HTTP) |

## 📺 ספורט (Sports)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 82 | Sport 5 אוקראינה | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Sport5.ua`) | ❌ רק באפליקציה (HTTP) |
| 83 | Sport 2 אוקראינה | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Sport2.ua`) | ❌ רק באפליקציה (HTTP) |
| 84 | Sport 3 אוקראינה | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Sport3.ua`) | ❌ רק באפליקציה (HTTP) |
| 85 | Sport 4 אוקראינה | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Sport4.ua`) | ❌ רק באפליקציה (HTTP) |
| 86 | ספורט פלוס HD (viju+ Sport) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`vijuPlusSport.ru`) | ✅ דרך מקור חלופי: Cinerama.uz – ספק IPTV באוזבקיסטן |
| 87 | ספורט סטאנטה Setanta Sports 2 HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`SetantaSports2Eurasia.ie`) | ❌ רק באפליקציה (HTTP) |
| 88 | Red Bull TV HD | Red Bull TV – Akamai (רשמי) | ✅ רשמי | iptv-org (`RedBullTV.at`) | ✅ |
| 89 | ערוץ אקסטרים Extreme Sports HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 90 | Sport 1 אוקראינה | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Sport1.ua`) | ❌ רק באפליקציה (HTTP) |
| 91 | ACC Digital Network | raycom-accdn-firetv.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`ACCDigitalNetwork.us`) | ✅ |
| 92 | talkSPORT | af7a8b4e.wurl.com | 🌐 ציבורי (iptv-org) | iptv-org (`talkSPORT.uk`) | ✅ |
| 93 | Kozoom TV | streams2.sofast.tv | 🌐 ציבורי (iptv-org) | iptv-org (`KozoomTV.fr`) | ✅ |
| 94 | More Than Sports TV | mts1.iptv-playoutcenter.de | 🌐 ציבורי (iptv-org) | iptv-org (`MoreThanSportsTV.de`) | ✅ |
| 95 | ACI Sport TV | webstream.multistream.it | 🌐 ציבורי (iptv-org) | iptv-org (`ACISportTV.it`) | ✅ |
| 96 | MMA-TV.com | streams2.sofast.tv | 🌐 ציבורי (iptv-org) | iptv-org (`MMATVcom.ru`) | ✅ |
| 97 | DiviSport | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`DiviSport.ua`) | ❌ רק באפליקציה (HTTP) |
| 98 | TSN The Ocho | d3pnbvng3bx2nj.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`TSNTheOcho.ca`) | ✅ |
| 99 | A Spor | rnttwmjcin.turknet.ercdn.net | 🌐 ציבורי (iptv-org) | iptv-org (`ASpor.tr`) | ✅ |
| 100 | DD Sports | d3qs3d2rkhfqrt.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`DDSports.in`) | ✅ |
| 101 | CCTV-Billiards | 38.75.136.137 | 🌐 ציבורי (iptv-org) | iptv-org (`CCTVBilliards.cn`) | ❌ רק באפליקציה (HTTP) |
| 102 | Azteca Deportes Network | live-manifest.production-public.tubi.io | 🌐 ציבורי (iptv-org) | iptv-org (`AztecaDeportesNetwork.mx`) | ✅ |
| 103 | DeporTV | 158.172.155.130 | 🌐 ציבורי (iptv-org) | iptv-org (`DeporTV.ar`) | ❌ רק באפליקציה (HTTP) |
| 104 | Fast&FunBox | Antik Telecom (סלובקיה), נתיבי test | ⚠️ ממסר צד שלישי | iptv-org (`FastFunBox.nl`) | ✅ |
| 105 | Pluto TV Snooker 900 | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVSnooker900.se`) | ✅ |
| 106 | Formula 1 AntenaPlay | live4dai.antenaplay.ro | 🌐 ציבורי (iptv-org) | iptv-org (`Formula1AntenaPlay.ro`) | ✅ |
| 107 | CRTV | stmv2.voxtvhd.com.br | 🌐 ציבורי (iptv-org) | iptv-org (`CRTV.cl`) | ✅ |
| 108 | Monterrico TV | h.jcp.live.opencaster.com | 🌐 ציבורי (iptv-org) | iptv-org (`MonterricoTV.pe`) | ✅ |
| 109 | T Sports 7 | live-us1.thaimomo.com | 🌐 ציבורי (iptv-org) | iptv-org (`TSports7.th`) | ✅ |
| 110 | Dong Nai TV 2 | freem3u.xyz | 🌐 ציבורי (iptv-org) | iptv-org (`DongNaiTV2.vn`) | ✅ |
| 111 | M Sports | cdn.rabta.stream | 🌐 ציבורי (iptv-org) | iptv-org (`MSports.pk`) | ✅ |
| 112 | Arryadia | 149.100.11.252 | 🌐 ציבורי (iptv-org) | iptv-org (`Arryadia.ma`) | ❌ רק באפליקציה (HTTP) |
| 113 | KTV Sport | kwtspta.cdn.mangomolo.com | 🌐 ציבורי (iptv-org) | iptv-org (`KTVSport.kw`) | ✅ |
| 114 | Belarus-5 | ngtrk.dc.beltelecom.by | 🌐 ציבורי (iptv-org) | iptv-org (`Belarus5.by`) | ✅ |
| 115 | Bahrain Sports 1 | 5c7b683162943.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`BahrainSports1.bh`) | ✅ |
| 116 | CDN Deportes | hls.tvabierta.net | 🌐 ציבורי (iptv-org) | iptv-org (`CDNDeportes.do`) | ✅ |
| 117 | Deportes TVC | streaming.onnethn.com | 🌐 ציבורי (iptv-org) | iptv-org (`DeportesTVC.hn`) | ✅ |
| 118 | Oman Sports TV | partneta.cdn.mgmlcdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`OmanSportsTV.om`) | ✅ |
| 119 | Talent TV | cast.talenttv.lk | 🌐 ציבורי (iptv-org) | iptv-org (`TalentTV.lk`) | ✅ |
| 120 | El-Heddaf TV | live.elheddaftv.com | 🌐 ציבורי (iptv-org) | iptv-org (`ElHeddafTV.dz`) | ✅ |
| 121 | Jordan Sport | jrtv-live.ercdn.net | 🌐 ציבורי (iptv-org) | iptv-org (`JordanSport.jo`) | ✅ |
| 122 | Prime | cdn.bestream.io | 🌐 ציבורי (iptv-org) | iptv-org (`Prime.sy`) | ✅ |
| 123 | AS3 Sport TV | streamtv.as3sport.online | 🌐 ציבורי (iptv-org) | iptv-org (`AS3SportTV.ve`) | ✅ |
| 124 | FTV | master.tucableip.com | 🌐 ציבורי (iptv-org) | iptv-org (`FTV.bo`) | ✅ |
| 125 | FUTV | 190.61.90.17 | 🌐 ציבורי (iptv-org) | iptv-org (`FUTV.cr`) | ❌ רק באפליקציה (HTTP) |
| 126 | Futbol TV | stream3.cinerama.uz | 🌐 ציבורי (iptv-org) | iptv-org (`FutbolTV.uz`) | ❌ רק באפליקציה (HTTP) |
| 127 | MNB Sport | live.mnb.mn | 🌐 ציבורי (iptv-org) | iptv-org (`MNBSport.mn`) | ✅ |
| 128 | Turkmenistan Sport | alpha.tv.online.tm | 🌐 ציבורי (iptv-org) | iptv-org (`TurkmenistanSport.tm`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 129 | American Ninja Warrior | d4whmvwm0rdvi.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`AmericanNinjaWarrior.us`) | ✅ |
| 130 | FIFA+ | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`FIFAPlus.uk`) | ✅ |
| 131 | Trace Sport Stars | lightning-tracesport-samsungau.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`TraceSportStars.fr`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 132 | World of Freesports | mainstreammedia-worldoffreesportsintl-rakuten.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`WorldofFreesports.de`) | ✅ |
| 133 | Inter TV | d3k8wzt41aflvx.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`InterTV.it`) | ✅ |
| 134 | Astrahan.Ru Sport | streaming.astrakhan.ru | 🌐 ציבורי (iptv-org) | iptv-org (`AstrahanRuSport.ru`) | ✅ |
| 135 | Maincast Cybersport | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`MaincastCybersport.ua`) | ❌ רק באפליקציה (HTTP) |
| 136 | Game+ | a-cdn.klowdtv.com | 🌐 ציבורי (iptv-org) | iptv-org (`GamePlus.ca`) | ✅ |
| 137 | TJK TV | tjktv-live.tjk.org | 🌐 ציבורי (iptv-org) | iptv-org (`TJKTV.tr`) | ✅ |
| 138 | CCTV-Golf & Tennis | 38.75.136.137 | 🌐 ציבורי (iptv-org) | iptv-org (`CCTVGolfTennis.cn`) | ❌ רק באפליקציה (HTTP) |
| 139 | ITV Deportes | thm-it-roku.otteravision.com | 🌐 ציבורי (iptv-org) | iptv-org (`ITVDeportes.mx`) | ✅ |
| 140 | FightBox | nosignal1.antenaplay.ro | 🌐 ציבורי (iptv-org) | iptv-org (`FightBox.nl`) | ✅ |
| 141 | ATG Live | kanal75xto-llhls.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`ATGLive.se`) | ✅ |
| 142 | Sporty TV | dash2.antik.sk | 🌐 ציבורי (iptv-org) | iptv-org (`SportyTV.cz`) | ✅ |
| 143 | NHL AntenaPlay | live4dai.antenaplay.ro | 🌐 ציבורי (iptv-org) | iptv-org (`NHLAntenaPlay.ro`) | ✅ |
| 144 | Teletrak TV | unlimited6-cl.dps.live | 🌐 ציבורי (iptv-org) | iptv-org (`TeletrakTV.cl`) | ✅ |
| 145 | Ovacion TV | cdn2.ujjina.com | 🌐 ציבורי (iptv-org) | iptv-org (`OvacionTV.pe`) | ❌ רק באפליקציה (HTTP) |
| 146 | PK Sports | lbgo.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`PKSports.pk`) | ✅ |
| 147 | KTV Sport Plus | kwtsplta.cdn.mangomolo.com | 🌐 ציבורי (iptv-org) | iptv-org (`KTVSportPlus.kw`) | ✅ |
| 148 | Sport TV | media1.skifnet.by | 🌐 ציבורי (iptv-org) | iptv-org (`SportTV.by`) | ❌ רק באפליקציה (HTTP) |
| 149 | Bahrain Sports 2 | 5c7b683162943.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`BahrainSports2.bh`) | ✅ |
| 150 | Colimdo TV | cnn.livestreaminggroup.info | 🌐 ציבורי (iptv-org) | iptv-org (`ColimdoTV.do`) | ✅ |
| 151 | DraftKings Network | na.linear.zype.com | 🌐 ציבורי (iptv-org) | iptv-org (`DraftKingsNetwork.us`) | ✅ |
| 152 | FIFA+ Women | cffda8ff.wurl.com | 🌐 ציבורי (iptv-org) | iptv-org (`FIFAPlusWomen.uk`) | ✅ |
| 153 | Persiana Fight | fighthls.persiana.live | 🌐 ציבורי (iptv-org) | iptv-org (`PersianaFight.fr`) | ✅ |
| 154 | DAZN Darts x Pluto TV | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`DAZNDartsxPlutoTV.de`) | ✅ |
| 155 | MadeinBO TV | srvx1.selftv.video | 🌐 ציבורי (iptv-org) | iptv-org (`MadeinBOTV.it`) | ✅ |
| 156 | Russian Extreme Ultra | 46.53.200.153 | 🌐 ציבורי (iptv-org) | iptv-org (`RussianExtremeUltra.ru`) | ❌ רק באפליקציה (HTTP) |
| 157 | Maincast Sport | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`MaincastSport.ua`) | ❌ רק באפליקציה (HTTP) |
| 158 | HTSpor TV | ciner.daioncdn.net | 🌐 ציבורי (iptv-org) | iptv-org (`HTSporTV.tr`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 159 | Kabaddi 24x7 | samitaorigin.tangotv.in | 🌐 ציבורי (iptv-org) | iptv-org (`Kabaddi24x7.in`) | ✅ |
| 160 | CCTV-Storm Football | 38.75.136.137 | 🌐 ציבורי (iptv-org) | iptv-org (`CCTVStormFootball.cn`) | ❌ רק באפליקציה (HTTP) |
| 161 | CazeTV | 138.204.86.4 | 🌐 ציבורי (iptv-org) | iptv-org (`CazeTV.br`) | ❌ רק באפליקציה (HTTP) |
| 162 | AyM Sports | 168.196.127.137 | 🌐 ציבורי (iptv-org) | iptv-org (`AyMSports.mx`) | ❌ רק באפליקציה (HTTP) |
| 163 | Brondby TV | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`BrondbyTV.se`) | ✅ |
| 164 | TVR Sport | tvr-sport.lg.mncdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`TVRSport.ro`) | ✅ |
| 165 | Turf Movil | tvturf4.janus.cl | 🌐 ציבורי (iptv-org) | iptv-org (`TurfMovil.cl`) | ✅ |
| 166 | ESPN8: The Ocho | d3b6q2ou5kp8ke.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`ESPN8TheOcho.us`) | ✅ |
| 167 | MUTV | fastly.live.brightcove.com | 🌐 ציבורי (iptv-org) | iptv-org (`MUTV.uk`) | ✅ |
| 168 | DAZN Heldinnen x Pluto TV | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`DAZNHeldinnenxPlutoTV.de`) | ✅ |
| 169 | Sportitalia | edge-001.streamup.eu | 🌐 ציבורי (iptv-org) | iptv-org (`Sportitalia.it`) | ✅ |
| 170 | UDAR | 5.188.159.128 | 🌐 ציבורי (iptv-org) | iptv-org (`UDAR.ru`) | ❌ רק באפליקציה (HTTP) |
| 171 | FB TV | 1hskrdto.rocketcdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`FBTV.tr`) | ❌ רק באפליקציה (HTTP) |
| 172 | Claro Sports | 168.196.127.137 | 🌐 ציבורי (iptv-org) | iptv-org (`ClaroSports.mx`) | ❌ רק באפליקציה (HTTP) |
| 173 | FCK Lovinderne | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`FCKLovinderne.se`) | ✅ |
| 174 | FloHockey | amg02278-amg02278c2-flosports-worldwide-9916.playouts.now.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`FloHockey.us`) | ✅ |
| 175 | Strongman Champions League | rightsboosterltd-scl-1-be.samsung.wurl.tv | 🌐 ציבורי (iptv-org) | iptv-org (`StrongmanChampionsLeague.uk`) | ✅ |
| 176 | Pluto TV Sport | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVSport.de`) | ✅ |
| 177 | Nautical Channel | a-cdn.klowdtv.com | 🌐 ציבורי (iptv-org) | iptv-org (`NauticalChannel.it`) | ✅ |
| 178 | Pluto TV Handboll Highlights | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVHandbollHighlights.se`) | ✅ |
| 179 | FloRacing | amg02278-amg02278c1-flosports-worldwide-7592.playouts.now.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`FloRacing.us`) | ✅ |
| 180 | DAZN Combat | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`DAZNCombat.uk`) | ✅ |
| 181 | Horse TV | a-cdn.klowdtv.com | 🌐 ציבורי (iptv-org) | iptv-org (`HorseTV.it`) | ✅ |
| 182 | Pluto TV Handboll Live | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVHandbollLive.se`) | ✅ |
| 183 | NBC Sports NOW | d4whmvwm0rdvi.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`NBCSportsNOW.us`) | ✅ |
| 184 | TR Sport | livetr.teleromagna.it | 🌐 ציבורי (iptv-org) | iptv-org (`TRSport.it`) | ✅ |
| 185 | Pluto TV Sport | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVSport.se`) | ✅ |
| 186 | PGA Tour | d11k1mnrgfposz.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`PGATour.us`) | ✅ |
| 187 | Pluto TV Fishing | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVFishing.se`) | ✅ |
| 188 | Racer Select | amg00378-amg00378c4-freelivesports-emea-5637.playouts.now.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`RacerSelect.us`) | ✅ |
| 189 | Pluto TV Handbold Highlights | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVHandboldHighlights.se`) | ✅ |
| 190 | Rally TV | rally-tv-live.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`RallyTV.us`) | ✅ |
| 191 | Pluto TV Handbold Live | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVHandboldLive.se`) | ✅ |
| 192 | SportsGrid | sportsgrid-tribal.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`SportsGrid.us`) | ✅ |
| 193 | Swerve Combat | linear-253.frequency.stream | 🌐 ציבורי (iptv-org) | iptv-org (`SwerveCombat.us`) | ✅ |
| 194 | Tennis Channel 2 | mumbai-edge.smartplaytv.in | 🌐 ציבורי (iptv-org) | iptv-org (`TennisChannel2.us`) | ✅ |
| 195 | beIN SPORTS XTRA en Espanol | dc1644a9jazgj.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`beINSPORTSXTRAenEspanol.us`) | ✅ |

## 📺 חדשות מהעולם (News)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 196 | Black News Channel | blacknewschannel-xumo-us.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`BlackNewsChannel.us`) | ✅ |
| 197 | Iran International | hlspackager.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`IranInternational.uk`) | ✅ |
| 198 | Africa 24 English | edge20.vedge.infomaniak.com | 🌐 ציבורי (iptv-org) | iptv-org (`Africa24English.fr`) | ✅ |
| 199 | DW | dwamdstream103.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`DW.de`) | ✅ |
| 200 | M95 Television Marbella | limited2.todostreaming.es | 🌐 ציבורי (iptv-org) | iptv-org (`M95TelevisionMarbella.es`) | ✅ |
| 201 | Rai News 24 | d3k8wzt41aflvx.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`RaiNews24.it`) | ✅ |
| 202 | RT | rt-glb.rttv.com | 🌐 ציבורי (iptv-org) | iptv-org (`RT.ru`) | ✅ |
| 203 | FREEDOM | dash4.antik.sk | 🌐 ציבורי (iptv-org) | iptv-org (`FREEDOM.ua`) | ✅ |
| 204 | Hamdard TV | tv.hamdardtv.com | 🌐 ציבורי (iptv-org) | iptv-org (`HamdardTV.ca`) | ✅ |
| 205 | Al Arabiya Programs | live.alarabiya.net | 🌐 ציבורי (iptv-org) | iptv-org (`AlArabiyaPrograms.ae`) | ✅ |
| 206 | Al Araby TV | live.kwikmotion.com | 🌐 ציבורי (iptv-org) | iptv-org (`AlArabyTV.qa`) | ✅ |
| 207 | Al Hadath | av.alarabiya.net | 🌐 ציבורי (iptv-org) | iptv-org (`AlHadath.sa`) | ✅ |
| 208 | Al Ghad TV | eazyvwqssi.erbvr.com | 🌐 ציבורי (iptv-org) | iptv-org (`AlGhadTV.eg`) | ✅ |
| 209 | Red TV Lebanon | live.kwikmotion.com | 🌐 ציבורי (iptv-org) | iptv-org (`RedTVLebanon.lb`) | ✅ |
| 210 | TRT World | tv-trtworld.medya.trt.com.tr | 🌐 ציבורי (iptv-org) | iptv-org (`TRTWorld.tr`) | ✅ |
| 211 | CNN News 18 | n18syndication.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`CNNNews18.in`) | ✅ |
| 212 | NHK World-Japan | masterpl.hls.nhkworld.jp | 🌐 ציבורי (iptv-org) | iptv-org (`NHKWorldJapan.jp`) | ✅ |
| 213 | Arirang TV | freem3u.xyz | 🌐 ציבורי (iptv-org) | iptv-org (`ArirangTV.kr`) | ✅ |
| 214 | CGTN | amg00405-rakutentv-cgtn-rakuten-i9tar.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`CGTN.cn`) | ✅ |
| 215 | CNN Brasil | d25usgadhphvwi.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`CNNBrasil.br`) | ✅ |
| 216 | Icrtv Colima | 5fc584f3f19c9.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`IcrtvColima.mx`) | ✅ |
| 217 | Canal 26 | stream-gtlc.telecentro.net.ar | 🌐 ציבורי (iptv-org) | iptv-org (`Canal26.ar`) | ✅ |
| 218 | ARTV | playout172.livextend.cloud | 🌐 ציבורי (iptv-org) | iptv-org (`ARTV.pt`) | ✅ |
| 219 | NH | takeoff.jetstre.am | 🌐 ציבורי (iptv-org) | iptv-org (`NH.nl`) | ✅ |
| 220 | EbS | streams.prd.commavservices.eu | 🌐 ציבורי (iptv-org) | iptv-org (`EbS.be`) | ✅ |
| 221 | Meteonews | streaming.meteonews.net | 🌐 ציבורי (iptv-org) | iptv-org (`Meteonews.ch`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 222 | Euronews | cdn-euronews.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`Euronews.pl`) | ✅ |
| 223 | Miracle Channel | live.miracleonline.org | 🌐 ציבורי (iptv-org) | iptv-org (`MiracleChannel.se`) | ✅ |
| 224 | TV from the Danish Parliament | cdnapi.kaltura.com | 🌐 ציבורי (iptv-org) | iptv-org (`TVfromtheDanishParliament.dk`) | ✅ |
| 225 | Nopola News | virta2.nopolanews.fi | 🌐 ציבורי (iptv-org) | iptv-org (`NopolaNews.fi`) | ✅ |
| 226 | Current Time TV | rfe-ingest.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`CurrentTimeTV.cz`) | ✅ |
| 227 | Dotto TV | live.dottotv.ro | 🌐 ציבורי (iptv-org) | iptv-org (`DottoTV.ro`) | ✅ |
| 228 | NCTV | pantera1-100gb-cl-movistar.dps.live | 🌐 ציבורי (iptv-org) | iptv-org (`NCTV.cl`) | ✅ |
| 229 | Noticiero 90 Minutos | play.cdn.enetres.net | 🌐 ציבורי (iptv-org) | iptv-org (`Noticiero90Minutos.co`) | ✅ |
| 230 | RPP TV | redirector.rudo.video | 🌐 ציבורי (iptv-org) | iptv-org (`RPPTV.pe`) | ✅ |
| 231 | BTV | lnd0t3b922.tenbytecdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`BTV.id`) | ✅ |
| 232 | 13 Siam Thai | live.x2.co.th | 🌐 ציבורי (iptv-org) | iptv-org (`13SiamThai.th`) | ✅ |
| 233 | Dong Nai TV 1 | freem3u.xyz | 🌐 ציבורי (iptv-org) | iptv-org (`DongNaiTV1.vn`) | ✅ |
| 234 | Neo News | vodzong.mjunoon.tv | 🌐 ציבורי (iptv-org) | iptv-org (`NeoNews.pk`) | ✅ |
| 235 | Mihan TV | iptv.mihantv.com | 🌐 ציבורי (iptv-org) | iptv-org (`MihanTV.ir`) | ✅ |
| 236 | Medi1TV Arabic | cdn.live.easybroadcast.io | 🌐 ציבורי (iptv-org) | iptv-org (`Medi1TVArabic.ma`) | ✅ |
| 237 | LN24SA | cdnstack.internetmultimediaonline.org | 🌐 ציבורי (iptv-org) | iptv-org (`LN24SA.za`) | ✅ |
| 238 | Silverbird News 24 | viewmedia7219.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`SilverbirdNews24.ng`) | ✅ |
| 239 | Al Sharqiya News | indexforce.com | 🌐 ציבורי (iptv-org) | iptv-org (`AlSharqiyaNews.iq`) | ✅ |
| 240 | Carivision | ss2.tvrdomi.com | 🌐 ציבורי (iptv-org) | iptv-org (`Carivision.do`) | ✅ |
| 241 | HCH | live.streamhch.com | 🌐 ציבורי (iptv-org) | iptv-org (`HCH.hn`) | ✅ |
| 242 | CNA | live1.mediadesk.al | 🌐 ציבורי (iptv-org) | iptv-org (`CNA.al`) | ✅ |
| 243 | Halab Today TV | halabtoday-live.lg.mncdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`HalabTodayTV.sy`) | ✅ |
| 244 | Canal I | streaming.canal-i.com | 🌐 ציבורי (iptv-org) | iptv-org (`CanalI.ve`) | ✅ |
| 245 | CEACOM TV | eu1.servers10.com | 🌐 ציבורי (iptv-org) | iptv-org (`CEACOMTV.bo`) | ✅ |
| 246 | Antik Info TV | dash4.antik.sk | 🌐 ציבורי (iptv-org) | iptv-org (`AntikInfoTV.sk`) | ✅ |
| 247 | Baku.TV | rtmp.baku.tv | 🌐 ציבורי (iptv-org) | iptv-org (`BakuTV.az`) | ✅ |
| 248 | KTV 2 | kwtktv2ta.cdn.mangomolo.com | 🌐 ציבורי (iptv-org) | iptv-org (`KTV2.kw`) | ✅ |
| 249 | NW Info 2 | hls.newworldtv.com | 🌐 ציבורי (iptv-org) | iptv-org (`NWInfo2.tg`) | ✅ |
| 250 | Belarus-24 | ngtrk.dc.beltelecom.by | 🌐 ציבורי (iptv-org) | iptv-org (`Belarus24.by`) | ✅ |
| 251 | Al Masirah | live.cdnbridge.tv | 🌐 ציבורי (iptv-org) | iptv-org (`AlMasirah.ye`) | ✅ |
| 252 | Al Najah News | streaming.najah.edu | 🌐 ציבורי (iptv-org) | iptv-org (`AlNajahNews.ps`) | ✅ |
| 253 | Altaghier TV | jmc-live.ercdn.net | 🌐 ציבורי (iptv-org) | iptv-org (`AltaghierTV.jo`) | ✅ |
| 254 | TV Legislativa | streamingav.asambleanacional.gob.ec | 🌐 ציבורי (iptv-org) | iptv-org (`TVLegislativa.ec`) | ✅ |
| 255 | Ekushey TV | ekusheyserver.com | 🌐 ציבורי (iptv-org) | iptv-org (`EkusheyTV.bd`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 256 | Formula | c4635.cdn.xsg.ge | 🌐 ציבורי (iptv-org) | iptv-org (`Formula.ge`) | ✅ |
| 257 | Privesc.Eu TV | cachestar.privesc.eu | 🌐 ציבורי (iptv-org) | iptv-org (`PrivescEuTV.md`) | ✅ |
| 258 | CNA | d2e1asnsl7br7b.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`CNA.sg`) | ✅ |
| 259 | Guardian Talk Radio | cdn-edge1.streamcomedia.com | 🌐 ציבורי (iptv-org) | iptv-org (`GuardianTalkRadio.bs`) | ✅ |
| 260 | Parliament TV | ptvlive.kordia.net.nz | 🌐 ציבורי (iptv-org) | iptv-org (`ParliamentTV.nz`) | ✅ |
| 261 | Al Masar TV | starmenajo.com | 🌐 ציבורי (iptv-org) | iptv-org (`AlMasarTV.ly`) | ✅ |
| 262 | AL24 News | cdn.live.easybroadcast.io | 🌐 ציבורי (iptv-org) | iptv-org (`AL24News.dz`) | ✅ |
| 263 | Dabanga TV | hls.dabangasudan.org | 🌐 ציבורי (iptv-org) | iptv-org (`DabangaTV.sd`) | ✅ |
| 264 | Oman TV Mubashir | partwota.cdn.mgmlcdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`OmanTVMubashir.om`) | ✅ |
| 265 | Sahara 24 | live.saharamedias.net | 🌐 ציבורי (iptv-org) | iptv-org (`Sahara24.mr`) | ✅ |
| 266 | Afrik Info TV | kali.vdopanel.com | 🌐 ציבורי (iptv-org) | iptv-org (`AfrikInfoTV.gn`) | ✅ |
| 267 | Baraka Television | stream.berosat.live | 🌐 ציבורי (iptv-org) | iptv-org (`BarakaTelevision.cd`) | ✅ |
| 268 | Haiti News Channel | cdn.haititivi.com | 🌐 ציבורי (iptv-org) | iptv-org (`HaitiNewsChannel.ht`) | ✅ |
| 269 | Monaco Info | webtv.monacoinfo.com | 🌐 ציבורי (iptv-org) | iptv-org (`MonacoInfo.mc`) | ✅ |
| 270 | Guatevision | mdstrm.com | 🌐 ציבורי (iptv-org) | iptv-org (`Guatevision.gt`) | ✅ |
| 271 | Norte Informativo TV | videohd.live | 🌐 ציבורי (iptv-org) | iptv-org (`NorteInformativoTV.cr`) | ✅ |
| 272 | Radio Ancon | www.streaming507.net | 🌐 ציבורי (iptv-org) | iptv-org (`RadioAncon.pa`) | ✅ |
| 273 | Brian TV | livefta.malimarcdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`BrianTV.la`) | ✅ |
| 274 | DVB TV | live-stream.dvb.no | 🌐 ציבורי (iptv-org) | iptv-org (`DVBTV.mm`) | ✅ |
| 275 | First Channel News | amtvusdvr.tulix.tv | 🌐 ציבורי (iptv-org) | iptv-org (`FirstChannelNews.am`) | ✅ |
| 276 | Newsmax Balkans | newsmaxadria.mts-si.tv | 🌐 ציבורי (iptv-org) | iptv-org (`NewsmaxBalkans.ba`) | ✅ |
| 277 | TOLOnews | tgn.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`TOLOnews.af`) | ✅ |
| 278 | TV Arta | gjirafa-video-live.gjirafa.net | 🌐 ציבורי (iptv-org) | iptv-org (`TVArta.xk`) | ✅ |
| 279 | TV Jadran | tvjadran.stream.agatin.hr | 🌐 ציבורי (iptv-org) | iptv-org (`TVJadran.hr`) | ✅ |
| 280 | Visir | live.visir.is | 🌐 ציבורי (iptv-org) | iptv-org (`Visir.is`) | ✅ |
| 281 | BX Inform | reflect-stream-bronxnet.cablecast.tv | 🌐 ציבורי (iptv-org) | iptv-org (`BXInform.us`) | ✅ |
| 282 | Dunya News UK | ukintl.dunyanews.tv | 🌐 ציבורי (iptv-org) | iptv-org (`DunyaNewsUK.uk`) | ✅ |
| 283 | Africa 24 | africa24.vedge.infomaniak.com | 🌐 ציבורי (iptv-org) | iptv-org (`Africa24.fr`) | ✅ |
| 284 | RT DE | rt-ger.rttv.com | 🌐 ציבורי (iptv-org) | iptv-org (`RTDE.de`) | ✅ |
| 285 | 3Cat Cameres del temps | directes-tv-int.3catdirectes.cat | 🌐 ציבורי (iptv-org) | iptv-org (`3CatCameresdeltemps.es`) | ✅ |
| 286 | Senato TV | senato-live.morescreens.com | 🌐 ציבורי (iptv-org) | iptv-org (`SenatoTV.it`) | ✅ |
| 287 | 360° News | live-vgtrksmotrim.cdnvideo.ru | 🌐 ציבורי (iptv-org) | iptv-org (`360News.ru`) | ✅ |
| 288 | Channel 24 | streamvideol1.luxnet.ua | 🌐 ציבורי (iptv-org) | iptv-org (`Channel24.ua`) | ✅ |
| 289 | CityNews Calgary | citynewsregional.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`CityNewsCalgary.ca`) | ✅ |
| 290 | News24 | skynewsau-live-premium.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`News24.au`) | ✅ |
| 291 | Al Yaum TV | hlspackager.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`AlYaumTV.ae`) | ✅ |
| 292 | Al Araby TV 2 | live.kwikmotion.com | 🌐 ציבורי (iptv-org) | iptv-org (`AlArabyTV2.qa`) | ✅ |
| 293 | Al Alam Al Yawm | mn-nl.mncdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`AlAlamAlYawm.sa`) | ✅ |
| 294 | Voice of Lebanon | Asharq Discovery – ItWorks (רשמי) | ✅ רשמי | iptv-org (`VoiceofLebanon.lb`) | ✅ |
| 295 | Mekameleen TV | mn-nl.mncdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`MekameleenTV.tr`) | ✅ |
| 296 | News18 Assam North-East | n18syndication.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`News18AssamNorthEast.in`) | ✅ |
| 297 | Weathernews | rch01e-alive-hls.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`Weathernews.jp`) | ✅ |
| 298 | Arirang UN | amdlive-ch02-ctnd-com.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`ArirangUN.kr`) | ✅ |
| 299 | STZ TV | cdn.live.br1.jmvstream.com | 🌐 ציבורי (iptv-org) | iptv-org (`STZTV.br`) | ✅ |
| 300 | AMX Noticias | 5e50264bd6766.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`AMXNoticias.mx`) | ✅ |
| 301 | VIVE TV | vivo.solumedia.com | 🌐 ציבורי (iptv-org) | iptv-org (`VIVETV.ar`) | ✅ |
| 302 | CNN Portugal | github.com | 🌐 ציבורי (iptv-org) | iptv-org (`CNNPortugal.pt`) | ✅ |
| 303 | EbS+ | streams.prd.commavservices.eu | 🌐 ציבורי (iptv-org) | iptv-org (`EbSPlus.be`) | ✅ |
| 304 | TVK Stella | stream.tvkstella.pl | 🌐 ציבורי (iptv-org) | iptv-org (`TVKStella.pl`) | ✅ |
| 305 | Hellenic Parliament TV | ert-ucdn.broadpeak-aas.com | 🌐 ציבורי (iptv-org) | iptv-org (`HellenicParliamentTV.gr`) | ✅ |
| 306 | Di TV | cdn0-03837-liveedge0.dna.ip-only.net | 🌐 ציבורי (iptv-org) | iptv-org (`DiTV.se`) | ✅ |
| 307 | CT24 | dash2.antik.sk | 🌐 ציבורי (iptv-org) | iptv-org (`CT24.cz`) | ✅ |
| 308 | Aleph News | stream-aleph.m.ro | 🌐 ציבורי (iptv-org) | iptv-org (`AlephNews.ro`) | ✅ |
| 309 | T13 En Vivo | redirector.dps.live | 🌐 ציבורי (iptv-org) | iptv-org (`T13EnVivo.cl`) | ✅ |
| 310 | NTN24 | stream1.bitred.cl | 🌐 ציבורי (iptv-org) | iptv-org (`NTN24.co`) | ✅ |
| 311 | Conecta2TV | live.obslivestream.com | 🌐 ציבורי (iptv-org) | iptv-org (`Conecta2TV.pe`) | ✅ |
| 312 | Lingkar TV | lingkartv.my.id | 🌐 ציבורי (iptv-org) | iptv-org (`LingkarTV.id`) | ✅ |
| 313 | Thai Parliament Television | tv-live.tpchannel.org | 🌐 ציבורי (iptv-org) | iptv-org (`ThaiParliamentTelevision.th`) | ✅ |
| 314 | Tra Vinh TV | freem3u.xyz | 🌐 ציבורי (iptv-org) | iptv-org (`TraVinhTV.vn`) | ✅ |
| 315 | Dunya News | imob.dunyanews.tv | 🌐 ציבורי (iptv-org) | iptv-org (`DunyaNews.pk`) | ✅ |
| 316 | Medi1TV Afrique | cdn.live.easybroadcast.io | 🌐 ציבורי (iptv-org) | iptv-org (`Medi1TVAfrique.ma`) | ✅ |
| 317 | SABC News | sabconetanw.cdn.mangomolo.com | 🌐 ציבורי (iptv-org) | iptv-org (`SABCNews.za`) | ✅ |
| 318 | News Central | wf.newscentral.ng | 🌐 ציבורי (iptv-org) | iptv-org (`NewsCentral.ng`) | ✅ |
| 319 | Alghadeer TV | sd343444.vodu.store | 🌐 ציבורי (iptv-org) | iptv-org (`AlghadeerTV.iq`) | ✅ |
| 320 | RDN | edge.essastream.com | 🌐 ציבורי (iptv-org) | iptv-org (`RDN.do`) | ✅ |
| 321 | Mega TV | www.desde-paraguay.com | 🌐 ציבורי (iptv-org) | iptv-org (`MegaTV.py`) | ✅ |
| 322 | Lenca TV | lencatelevision.com | 🌐 ציבורי (iptv-org) | iptv-org (`LencaTV.hn`) | ✅ |
| 323 | ntv | protokolldns.xyz | 🌐 ציבורי (iptv-org) | iptv-org (`ntv.al`) | ✅ |
| 324 | Syria TV | live.kwikmotion.com | 🌐 ציבורי (iptv-org) | iptv-org (`SyriaTV.sy`) | ✅ |
| 325 | CRTV News | live1.acangroup.org | 🌐 ציבורי (iptv-org) | iptv-org (`CRTVNews.cm`) | ✅ |
| 326 | Telesur | mblenmain01.telesur.ultrabase.net | 🌐 ציבורי (iptv-org) | iptv-org (`Telesur.ve`) | ✅ |
| 327 | UMSA TVU LP | edge.enhdtv.com | 🌐 ציבורי (iptv-org) | iptv-org (`UMSATVULP.bo`) | ✅ |
| 328 | TV NRSR | n11.stv.livebox.sk | 🌐 ציבורי (iptv-org) | iptv-org (`TVNRSR.sk`) | ✅ |
| 329 | APA Tv | stream.apa.tv | 🌐 ציבורי (iptv-org) | iptv-org (`APATv.az`) | ❌ רק באפליקציה (HTTP) |
| 330 | Fresh News | streaming-android.freshnewsasia.tv | 🌐 ציבורי (iptv-org) | iptv-org (`FreshNews.kh`) | ❌ רק באפליקציה (HTTP) |
| 331 | KTV News | kwtkbta.cdn.mangomolo.com | 🌐 ציבורי (iptv-org) | iptv-org (`KTVNews.kw`) | ✅ |
| 332 | NW Info | hls.newworldtv.com | 🌐 ציבורי (iptv-org) | iptv-org (`NWInfo.tg`) | ✅ |
| 333 | Perviy Informationniy | ngtrk.dc.beltelecom.by | 🌐 ציבורי (iptv-org) | iptv-org (`PerviyInformationniy.by`) | ✅ |
| 334 | Al Masirah Mubacher | live2.cdnbridge.tv | 🌐 ציבורי (iptv-org) | iptv-org (`AlMasirahMubacher.ye`) | ✅ |
| 335 | Ma'an TV | htvmada.mada.ps | 🌐 ציבורי (iptv-org) | iptv-org (`MaanTV.ps`) | ✅ |
| 336 | Alhaqeqa Aldawlia | ghaasiflu.online | 🌐 ציבורי (iptv-org) | iptv-org (`AlhaqeqaAldawlia.jo`) | ✅ |
| 337 | Ecotel TV | ecoteltv.streamseguro.com | 🌐 ציבורי (iptv-org) | iptv-org (`EcotelTV.ec`) | ✅ |
| 338 | News 21 Bangla TV | 103.190.133.68 | 🌐 ציבורי (iptv-org) | iptv-org (`News21BanglaTV.bd`) | ❌ רק באפליקציה (HTTP) |
| 339 | Palitra News | live.palitranews.ge | 🌐 ציבורי (iptv-org) | iptv-org (`PalitraNews.ge`) | ✅ |
| 340 | Realitatea TV | realitatealive.md | 🌐 ציבורי (iptv-org) | iptv-org (`RealitateaTV.md`) | ✅ |
| 341 | Cheddar News | hls.livecdn.io | 🌐 ציבורי (iptv-org) | iptv-org (`CheddarNews.us`) | ✅ |
| 342 | Africa 24 Sport | africa24.vedge.infomaniak.com | 🌐 ציבורי (iptv-org) | iptv-org (`Africa24Sport.fr`) | ✅ |
| 343 | WELT | dash4.antik.sk | 🌐 ציבורי (iptv-org) | iptv-org (`WELT.de`) | ✅ |
| 344 | 3CatInfo | directes-tv-int.3catdirectes.cat | 🌐 ציבורי (iptv-org) | iptv-org (`3CatInfo.es`) | ✅ |
| 345 | Etna Espresso Channel | 5db313b643fd8.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`EtnaEspressoChannel.it`) | ✅ |
| 346 | RT Arabic | rt-arb.rttv.com | 🌐 ציבורי (iptv-org) | iptv-org (`RTArabic.ru`) | ✅ |
| 347 | Rada TV | live-tv.cloud – ספק IPTV אוקראיני | ⚠️ ממסר צד שלישי | iptv-org (`RadaTV.ua`) | ✅ |
| 348 | CityNews Toronto | citynewsregional.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`CityNewsToronto.ca`) | ✅ |
| 349 | ticker News | live-hls-7agy.livepush.io | 🌐 ציבורי (iptv-org) | iptv-org (`tickerNews.au`) | ✅ |
| 350 | Alarabiya | live.alarabiya.net | 🌐 ציבורי (iptv-org) | iptv-org (`Alarabiya.ae`) | ✅ |
| 351 | Al Jazeera 2 | live-hls-web-aja2-gcp.thehlive.com | 🌐 ציבורי (iptv-org) | iptv-org (`AlJazeera2.qa`) | ✅ |
| 352 | Palestine Al Yawm | live.paltodaytv.com | 🌐 ציבורי (iptv-org) | iptv-org (`PalestineAlYawm.lb`) | ✅ |
| 353 | TRT Arabi | tv-trtarabi.medya.trt.com.tr | 🌐 ציבורי (iptv-org) | iptv-org (`TRTArabi.tr`) | ✅ |

## 📺 סרטים וסדרות (Movies & Series)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 354 | ערוץ הים התיכון HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`YamTichoni.il`) | ❌ רק באפליקציה (HTTP) |
| 355 | yes סרטים אקשן HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`YesMoviesAction.il`) | ❌ רק באפליקציה (HTTP) |
| 356 | HOT דרמה HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`HOTDrama.il`) | ❌ רק באפליקציה (HTTP) |
| 357 | HOT זון HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`HOTZone.il`) | ❌ רק באפליקציה (HTTP) |
| 358 | yes ריאליטי HD | ערוץ FAST ישראלי – AWS CloudFront | ✅ FAST חינמי | iptv-org (`RealityChannel.il`) | ✅ |
| 359 | ויוה פרימיום HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`VivaPremium.il`) | ❌ רק באפליקציה (HTTP) |
| 360 | סינמה פרימיום HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Premialnoe.ru`) | ❌ רק באפליקציה (HTTP) |
| 361 | סינמה מתח ואקשן HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Ostrosyuzhetnoye.ru`) | ❌ רק באפליקציה (HTTP) |
| 362 | ערוץ סרטי אימה HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Kinouzhas.ru`) | ❌ רק באפליקציה (HTTP) |
| 363 | קולנוע להיטים KinoHit HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`KinoHit.ru`) | ✅ דרך מקור חלופי: Cinerama.uz – ספק IPTV באוזבקיסטן |
| 364 | קולנוע משפחתי Kinosemja HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Kinosemja.ru`) | ❌ רק באפליקציה (HTTP) |
| 365 | סינמה מגה-להיטים HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`vijuPlusMegahit.ru`) | ❌ רק באפליקציה (HTTP) |
| 366 | סינמה בכורה Premiere HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`vijuPlusPremiere.ru`) | ❌ רק באפליקציה (HTTP) |
| 367 | סינמה קומדיה HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`vijuPlusComedy.ru`) | ❌ רק באפליקציה (HTTP) |
| 368 | קולנוע TV1000 אקשן HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`vijuTV1000action.ru`) | ✅ דרך מקור חלופי: Cinerama.uz – ספק IPTV באוזבקיסטן |
| 369 | קולנוע TV1000 קלאסי | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`vijuTV1000.ru`) | ❌ רק באפליקציה (HTTP) |
| 370 | ערוץ הסדרות Serial HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`vijuPlusSerial.ru`) | ❌ רק באפליקציה (HTTP) |
| 371 | סרטי שנות ה-80 Rewind HD | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`80sRewind.us`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 372 | סרטי שנות ה-70 Cinema HD | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`70sCinema.us`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 373 | סרטי שנות ה-2000 Replay HD | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`00sReplay.us`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 374 | משפחת סימפסון 24/7 HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 375 | קולנוע קומדיה CineMan Comedy HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 376 | קולנוע רטרו VHS Cinema HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 377 | 48 Hours | dai.google.com | 🌐 ציבורי (iptv-org) | iptv-org (`48Hours.us`) | ✅ |
| 378 | True Crime | b6cca454.wurl.com | 🌐 ציבורי (iptv-org) | iptv-org (`TrueCrime.uk`) | ✅ |
| 379 | Cine Nanar | zylo-cinenanar-rakuten.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`CineNanar.fr`) | ✅ |
| 380 | Grjngo Western Movies | amogonetworx-grjngo-3-dk.samsung.wurl.tv | 🌐 ציבורי (iptv-org) | iptv-org (`GrjngoWesternMovies.de`) | ✅ |
| 381 | Cine Friki | d2mr4fu91mjx9m.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`CineFriki.es`) | ✅ |
| 382 | Super TV | 59d7d6f47d7fc.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`SuperTV.it`) | ✅ |
| 383 | Smotrim Chestnyy Detektiv | live-vgtrksmotrim.cdnvideo.ru | 🌐 ציבורי (iptv-org) | iptv-org (`SmotrimChestnyyDetektiv.ru`) | ✅ |
| 384 | FilmUADrama | dash2.antik.sk | 🌐 ציבורי (iptv-org) | iptv-org (`FilmUADrama.ua`) | ✅ |
| 385 | Mix Hollywood | ml-pull-hwc.myco.io | 🌐 ציבורי (iptv-org) | iptv-org (`MixHollywood.eg`) | ✅ |
| 386 | Grand Cinema | gcinemahls.wns.live | 🌐 ציבורי (iptv-org) | iptv-org (`GrandCinema.tr`) | ✅ |
| 387 | B4U Bhojpuri | cdnb4u.wiseplayout.com | 🌐 ציבורי (iptv-org) | iptv-org (`B4UBhojpuri.in`) | ✅ |
| 388 | Harbin Movie Channel | stream.hrbtv.net | 🌐 ציבורי (iptv-org) | iptv-org (`HarbinMovieChannel.cn`) | ✅ |
| 389 | CINDIE TV | cc-hqw8u5r1nshjc.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`CINDIETV.br`) | ✅ |
| 390 | Popcorn Central | oly-ftvpc.otteravision.com | 🌐 ציבורי (iptv-org) | iptv-org (`PopcornCentral.mx`) | ✅ |
| 391 | Europa Europa | iptv.bitred.cl | 🌐 ציבורי (iptv-org) | iptv-org (`EuropaEuropa.ar`) | ✅ |
| 392 | Kuriakos Cine | w2.manasat.com | 🌐 ציבורי (iptv-org) | iptv-org (`KuriakosCine.pt`) | ✅ |
| 393 | FILMBOX+ Festival | nosignal1.antenaplay.ro | 🌐 ציבורי (iptv-org) | iptv-org (`FILMBOXPlusFestival.pl`) | ✅ |
| 394 | Action 24 | actionlive.siliconweb.com | 🌐 ציבורי (iptv-org) | iptv-org (`Action24.gr`) | ❌ רק באפליקציה (HTTP) |
| 395 | Bondi Rescue | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`BondiRescue.se`) | ✅ |
| 396 | Hero | live-fi.tvkaista.net | 🌐 ציבורי (iptv-org) | iptv-org (`Hero.fi`) | ✅ |
| 397 | Cinemaraton Plus | hls02ns.antenaplay.ro | 🌐 ציבורי (iptv-org) | iptv-org (`CinemaratonPlus.ro`) | ✅ |
| 398 | TVN3 | mdstrm.com | 🌐 ציבורי (iptv-org) | iptv-org (`TVN3.cl`) | ✅ |
| 399 | MCI Television | video.ejeserver.com | 🌐 ציבורי (iptv-org) | iptv-org (`MCITelevision.co`) | ✅ |
| 400 | Latina Clasicos | redirector.rudo.video | 🌐 ציבורי (iptv-org) | iptv-org (`LatinaClasicos.pe`) | ✅ |
| 401 | ON Movies | freem3u.xyz | 🌐 ציבורי (iptv-org) | iptv-org (`ONMovies.vn`) | ✅ |
| 402 | AVA Series | avaserieshls.wns.live | 🌐 ציבורי (iptv-org) | iptv-org (`AVASeries.ir`) | ✅ |
| 403 | Itage TV | viewmedia7219.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`ItageTV.ng`) | ✅ |
| 404 | Aurora Media Films | cdn.streamhispanatv.net | 🌐 ציבורי (iptv-org) | iptv-org (`AuroraMediaFilms.gt`) | ✅ |
| 405 | Mega Cine TV | cnn.hostlagarto.com | 🌐 ציבורי (iptv-org) | iptv-org (`MegaCineTV.do`) | ✅ |
| 406 | Allo Cine | d1yl6bf4hrzbr0.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`AlloCine.cm`) | ✅ |
| 407 | HITS Movies | 103.151.60.162 | 🌐 ציבורי (iptv-org) | iptv-org (`HITSMovies.sg`) | ❌ רק באפליקציה (HTTP) |
| 408 | Silk Way Cinema | stream.qazcdn.net | 🌐 ציבורי (iptv-org) | iptv-org (`SilkWayCinema.kz`) | ✅ |
| 409 | Lollywood HD TV | stream.berosat.live | 🌐 ציבורי (iptv-org) | iptv-org (`LollywoodHDTV.cd`) | ✅ |
| 410 | Serie Max | video1.getstreamhosting.com | 🌐 ציבורי (iptv-org) | iptv-org (`SerieMax.ci`) | ✅ |
| 411 | BIZ Cinema | fl.biztv.media | 🌐 ציבורי (iptv-org) | iptv-org (`BIZCinema.uz`) | ✅ |
| 412 | Maah TV | hls.maahtv.live | 🌐 ציבורי (iptv-org) | iptv-org (`MaahTV.my`) | ✅ |
| 413 | Movie Bangla | alvetv.com | 🌐 ציבורי (iptv-org) | iptv-org (`MovieBangla.bd`) | ❌ רק באפליקציה (HTTP) |
| 414 | MWD Movies | 203.81.84.130 | 🌐 ציבורי (iptv-org) | iptv-org (`MWDMovies.mm`) | ❌ רק באפליקציה (HTTP) |
| 415 | TV1000 | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`TV1000.bg`) | ❌ רק באפליקציה (HTTP) |
| 416 | Antiques Roadshow UK | bbc-antiquesroadshowuk-1-us.roku.wurl.tv | 🌐 ציבורי (iptv-org) | iptv-org (`AntiquesRoadshowUK.us`) | ✅ |
| 417 | BBC Drama | amg00793-amg00793c40-rakuten-es-5444.playouts.now.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`BBCDrama.uk`) | ✅ |
| 418 | Sony One Blacklist | 06cb85ad6ccb4a6b97f561e62d16ad3f.mediatailor.us-west-2.amazonaws.com | 🌐 ציבורי (iptv-org) | iptv-org (`SonyOneBlacklist.fr`) | ✅ |
| 419 | Pluto TV Dating | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVDating.de`) | ✅ |
| 420 | MyTime Movie Network | appletree-mytimespain-rakuten.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`MyTimeMovieNetwork.es`) | ✅ |
| 421 | Star Family | dash2.antik.sk | 🌐 ציבורי (iptv-org) | iptv-org (`StarFamily.ru`) | ✅ |
| 422 | FilmUA Live | dash2.antik.sk | 🌐 ציבורי (iptv-org) | iptv-org (`FilmUALive.ua`) | ✅ |
| 423 | Epic Bhojpuri | cc-8hy4a26pz2uos.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`EpicBhojpuri.in`) | ✅ |
| 424 | CCTV-Nostalgia Theater | 38.75.136.137 | 🌐 ציבורי (iptv-org) | iptv-org (`CCTVNostalgiaTheater.cn`) | ❌ רק באפליקציה (HTTP) |
| 425 | MyTime Movie Network | appletree-mytime-samsungbrazil.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`MyTimeMovieNetwork.br`) | ✅ |
| 426 | Az Cinema | 15.204.196.147 | 🌐 ציבורי (iptv-org) | iptv-org (`AzCinema.mx`) | ❌ רק באפליקציה (HTTP) |
| 427 | TVI Ficcao | raw.githubusercontent.com | 🌐 ציבורי (iptv-org) | iptv-org (`TVIFiccao.pt`) | ✅ |
| 428 | FILMBOX+ Hits | nosignal1.antenaplay.ro | 🌐 ציבורי (iptv-org) | iptv-org (`FILMBOXPlusHits.pl`) | ✅ |
| 429 | Groovy TV | web.onair-radio.eu | 🌐 ציבורי (iptv-org) | iptv-org (`GroovyTV.gr`) | ❌ רק באפליקציה (HTTP) |
| 430 | Car Chase | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`CarChase.se`) | ✅ |
| 431 | Nostalgia TV | hls02ns.antenaplay.ro | 🌐 ציבורי (iptv-org) | iptv-org (`NostalgiaTV.ro`) | ✅ |
| 432 | 13T | origin.dpsgo.com | 🌐 ציבורי (iptv-org) | iptv-org (`13T.cl`) | ✅ |
| 433 | Canal Cine Dios | canal.mediaserver.com.co | 🌐 ציבורי (iptv-org) | iptv-org (`CanalCineDios.co`) | ✅ |
| 434 | ON Cine | freem3u.xyz | 🌐 ציבורי (iptv-org) | iptv-org (`ONCine.vn`) | ✅ |
| 435 | SL 1 | slonehls.wns.live | 🌐 ציבורי (iptv-org) | iptv-org (`SL1.ir`) | ✅ |
| 436 | TVS Retro | cdn.streamhispanatv.net | 🌐 ציבורי (iptv-org) | iptv-org (`TVSRetro.gt`) | ✅ |
| 437 | Boreal | edge.essastream.com | 🌐 ציבורי (iptv-org) | iptv-org (`Boreal.do`) | ✅ |
| 438 | Charge! | fast-channels.sinclairstoryline.com | 🌐 ציבורי (iptv-org) | iptv-org (`Charge.us`) | ✅ |
| 439 | MyTime Movie Network | appletree-mytimeuk-rakuten.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`MyTimeMovieNetwork.uk`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 440 | Sony One Favoris | 49d735318d6b4c30a24a7997ea594e1b.mediatailor.us-west-2.amazonaws.com | 🌐 ציבורי (iptv-org) | iptv-org (`SonyOneFavoris.fr`) | ✅ |
| 441 | Cagney & Lacey | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`CagneyLacey.de`) | ✅ |
| 442 | Rakuten TV Action Movies | a9c57ec7ec5e4b7daeacc6316a0bb404.mediatailor.eu-west-1.amazonaws.com | 🌐 ציבורי (iptv-org) | iptv-org (`RakutenTVActionMovies.es`) | ✅ |
| 443 | Horoshee Kino | live-rian.cdnvideo.ru | 🌐 ציבורי (iptv-org) | iptv-org (`HorosheeKino.ru`) | ✅ |
| 444 | 4ever Cinema | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`4everCinema.ua`) | ❌ רק באפליקציה (HTTP) |
| 445 | NH Tamil Gold | d3arbp6l7f096k.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`NHTamilGold.in`) | ✅ |
| 446 | CCTV-Storm Theater | 38.75.136.137 | 🌐 ציבורי (iptv-org) | iptv-org (`CCTVStormTheater.cn`) | ❌ רק באפליקציה (HTTP) |
| 447 | Retro Cartoon | stmv1.video.brstream.com.br | 🌐 ציבורי (iptv-org) | iptv-org (`RetroCartoon.br`) | ✅ |
| 448 | Claro Cinema | 158.172.217.18 | 🌐 ציבורי (iptv-org) | iptv-org (`ClaroCinema.mx`) | ❌ רק באפליקציה (HTTP) |
| 449 | FILMBOX+ Comedy | nosignal1.antenaplay.ro | 🌐 ציבורי (iptv-org) | iptv-org (`FILMBOXPlusComedy.pl`) | ✅ |
| 450 | 2900 Happiness | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`2900Happiness.se`) | ✅ |
| 451 | Mireasa | stream1.antenaplay.ro | 🌐 ציבורי (iptv-org) | iptv-org (`Mireasa.ro`) | ✅ |
| 452 | Mega Ficcion | 15.204.196.147 | 🌐 ציבורי (iptv-org) | iptv-org (`MegaFiccion.cl`) | ❌ רק באפליקציה (HTTP) |
| 453 | Star Channel | 190.61.47.54 | 🌐 ציבורי (iptv-org) | iptv-org (`StarChannel.co`) | ❌ רק באפליקציה (HTTP) |
| 454 | SL 2 | sltwohls.wns.live | 🌐 ציבורי (iptv-org) | iptv-org (`SL2.ir`) | ✅ |
| 455 | Doctor Who Classic | bbc-classicdrwho-1-us.roku.wurl.tv | 🌐 ציבורי (iptv-org) | iptv-org (`DoctorWhoClassic.us`) | ✅ |
| 456 | Viasat Epic Drama | 92.114.85.87 | 🌐 ציבורי (iptv-org) | iptv-org (`ViasatEpicDrama.uk`) | ❌ רק באפליקציה (HTTP) |
| 457 | Sony One Hits Action | 5098a8b860504a3690fd2e7c0a18d68f.mediatailor.us-west-2.amazonaws.com | 🌐 ציבורי (iptv-org) | iptv-org (`SonyOneHitsAction.fr`) | ✅ |
| 458 | CSI: Crime Scene Investigation | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`CSICrimeSceneInvestigation.de`) | ✅ |
| 459 | Rakuten TV Comedy Movies | 71db867f03ce4d71a29e92155f07ab87.mediatailor.eu-west-1.amazonaws.com | 🌐 ציבורי (iptv-org) | iptv-org (`RakutenTVComedyMovies.es`) | ✅ |
| 460 | Smotrim 100% Klassika | Smotrim (VGTRK) – הבעלים של קרוסלה | ✅ רשמי | iptv-org (`Smotrim100Klassika.ru`) | ✅ |
| 461 | 4ever Drama | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`4everDrama.ua`) | ❌ רק באפליקציה (HTTP) |
| 462 | Punjabi Shorts | vglivessai.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`PunjabiShorts.in`) | ✅ |
| 463 | CCTV-The First Theater | 38.75.136.137 | 🌐 ציבורי (iptv-org) | iptv-org (`CCTVTheFirstTheater.cn`) | ❌ רק באפליקציה (HTTP) |
| 464 | TV Series | stmv1.srvif.com | 🌐 ציבורי (iptv-org) | iptv-org (`TVSeries.br`) | ✅ |
| 465 | De Pelicula Latin America | 38.19.41.46 | 🌐 ציבורי (iptv-org) | iptv-org (`DePeliculaLatinAmerica.mx`) | ❌ רק באפליקציה (HTTP) |
| 466 | FILMBOX+ Love & Crime | nosignal1.antenaplay.ro | 🌐 ציבורי (iptv-org) | iptv-org (`FILMBOXPlusLoveCrime.pl`) | ✅ |
| 467 | 7th Heaven | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`7thHeaven.se`) | ✅ |
| 468 | Bollywood Classic | hls02ns.antenaplay.ro | 🌐 ציבורי (iptv-org) | iptv-org (`BollywoodClassic.ro`) | ✅ |
| 469 | Star Channel | 138.121.15.230 | 🌐 ציבורי (iptv-org) | iptv-org (`StarChannel.cl`) | ❌ רק באפליקציה (HTTP) |
| 470 | TeleNostalgia | 38.134.250.110 | 🌐 ציבורי (iptv-org) | iptv-org (`TeleNostalgia.co`) | ❌ רק באפליקציה (HTTP) |
| 471 | Afra Film | afrafhls.wns.live | 🌐 ציבורי (iptv-org) | iptv-org (`AfraFilm.ir`) | ✅ |
| 472 | Duck Dynasty | amg00276-amg00276c2-rakuten-uk-3449.playouts.now.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`DuckDynasty.us`) | ✅ |
| 473 | Sony One Hits Comedie | 7aa9671895264ec9a384dfed1b992171.mediatailor.us-west-2.amazonaws.com | 🌐 ציבורי (iptv-org) | iptv-org (`SonyOneHitsComedie.fr`) | ✅ |
| 474 | More TV Crime | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`MoreTVCrime.de`) | ✅ |
| 475 | Rakuten TV Drama Movies | a7089c89d85f453d850c4a1518b43076.mediatailor.eu-west-1.amazonaws.com | 🌐 ציבורי (iptv-org) | iptv-org (`RakutenTVDramaMovies.es`) | ✅ |
| 476 | Smotrim 100% Lyubov | Smotrim (VGTRK) – הבעלים של קרוסלה | ✅ רשמי | iptv-org (`Smotrim100Lyubov.ru`) | ✅ |
| 477 | Raj Digital Plus | livestream.rajtv.tv | 🌐 ציבורי (iptv-org) | iptv-org (`RajDigitalPlus.in`) | ✅ |
| 478 | Classique TV | stmv1.srvif.com | 🌐 ציבורי (iptv-org) | iptv-org (`ClassiqueTV.br`) | ✅ |
| 479 | De Pelicula Plus | 200.115.120.1 | 🌐 ציבורי (iptv-org) | iptv-org (`DePeliculaPlus.mx`) | ❌ רק באפליקציה (HTTP) |
| 480 | AXN Poland | 92.114.85.87 | 🌐 ציבורי (iptv-org) | iptv-org (`AXNPoland.pl`) | ❌ רק באפליקציה (HTTP) |
| 481 | Auction Hunters | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`AuctionHunters.se`) | ✅ |
| 482 | CineWow | hls02ns.antenaplay.ro | 🌐 ציבורי (iptv-org) | iptv-org (`CineWow.ro`) | ✅ |
| 483 | Cafe Film | cafefhls.wns.live | 🌐 ציבורי (iptv-org) | iptv-org (`CafeFilm.ir`) | ✅ |
| 484 | Gravitas Movies | d6dg3ebeih71x.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`GravitasMovies.us`) | ✅ |
| 485 | Persiana Cinema | cinehls.persiana.live | 🌐 ציבורי (iptv-org) | iptv-org (`PersianaCinema.fr`) | ✅ |
| 486 | Pluto TV American True Crime | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVAmericanTrueCrime.de`) | ✅ |
| 487 | Rakuten TV Family Movies | b0d59c8c98974e708e5ccb9a27cdfdfc.mediatailor.eu-west-1.amazonaws.com | 🌐 ציבורי (iptv-org) | iptv-org (`RakutenTVFamilyMovies.es`) | ✅ |
| 488 | Smotrim 100% Muzhskoe | Smotrim (VGTRK) – הבעלים של קרוסלה | ✅ רשמי | iptv-org (`Smotrim100Muzhskoe.ru`) | ✅ |
| 489 | South Station | cc-yw7ztecy8do3q.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`SouthStation.in`) | ✅ |
| 490 | AXN | 170.83.16.50 | 🌐 ציבורי (iptv-org) | iptv-org (`AXN.br`) | ❌ רק באפליקציה (HTTP) |
| 491 | MultiPremier | 168.196.127.137 | 🌐 ציבורי (iptv-org) | iptv-org (`MultiPremier.mx`) | ❌ רק באפליקציה (HTTP) |
| 492 | TVP Seriale | 92.114.85.87 | 🌐 ציבורי (iptv-org) | iptv-org (`TVPSeriale.pl`) | ❌ רק באפליקציה (HTTP) |
| 493 | Becker | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`Becker.se`) | ✅ |
| 494 | Classic TV | clshls.wns.live | 🌐 ציבורי (iptv-org) | iptv-org (`ClassicTV.ir`) | ✅ |
| 495 | Impossible Quiz Show | bbc-impossible-1-us.roku.wurl.tv | 🌐 ציבורי (iptv-org) | iptv-org (`ImpossibleQuizShow.us`) | ✅ |
| 496 | Persiana Family | familyhls.persiana.live | 🌐 ציבורי (iptv-org) | iptv-org (`PersianaFamily.fr`) | ✅ |
| 497 | Pluto TV Classic TV | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVClassicTV.de`) | ✅ |
| 498 | Rakuten TV Top Movies | ff335120300e4742a2b135ee9a9e7df8.mediatailor.eu-west-1.amazonaws.com | 🌐 ציבורי (iptv-org) | iptv-org (`RakutenTVTopMovies.es`) | ✅ |
| 499 | Dorama | fs.uplink.kz | 🌐 ציבורי (iptv-org) | iptv-org (`Dorama.ru`) | ✅ |
| 500 | Thalaa TV | streams2.sofast.tv | 🌐 ציבורי (iptv-org) | iptv-org (`ThalaaTV.in`) | ✅ |
| 501 | Sony Movies | 45.162.64.114 | 🌐 ציבורי (iptv-org) | iptv-org (`SonyMovies.br`) | ❌ רק באפליקציה (HTTP) |
| 502 | Tlnovelas Mexico | 167.249.23.102 | 🌐 ציבורי (iptv-org) | iptv-org (`TlnovelasMexico.mx`) | ❌ רק באפליקציה (HTTP) |
| 503 | FILMBOX+ Action | 92.114.85.87 | 🌐 ציבורי (iptv-org) | iptv-org (`FILMBOXPlusAction.pl`) | ❌ רק באפליקציה (HTTP) |
| 504 | BET | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`BET.se`) | ✅ |
| 505 | Gold Star | moviethls.wns.live | 🌐 ציבורי (iptv-org) | iptv-org (`GoldStar.ir`) | ✅ |
| 506 | Maverick Black Cinema | maverick-maverick-black-cinema-3-us.roku.wurl.tv | 🌐 ציבורי (iptv-org) | iptv-org (`MaverickBlackCinema.us`) | ✅ |
| 507 | Persiana Iranian | irhls.persiana.live | 🌐 ציבורי (iptv-org) | iptv-org (`PersianaIranian.fr`) | ✅ |
| 508 | Pluto TV Cult Films | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVCultFilms.de`) | ✅ |
| 509 | 3Cat Plats bruts | fast-tailor.3catdirectes.cat | 🌐 ציבורי (iptv-org) | iptv-org (`3CatPlatsbruts.es`) | ✅ |
| 510 | Kinoseriya | fs.uplink.kz | 🌐 ציבורי (iptv-org) | iptv-org (`Kinoseriya.ru`) | ✅ |
| 511 | The Movie Club | sis-global.prod.samsungtv.plus | 🌐 ציבורי (iptv-org) | iptv-org (`TheMovieClub.in`) | ✅ |
| 512 | Az Corazon | 200.115.120.1 | 🌐 ציבורי (iptv-org) | iptv-org (`AzCorazon.mx`) | ❌ רק באפליקציה (HTTP) |
| 513 | FILMBOX+ Emotion | 92.114.85.87 | 🌐 ציבורי (iptv-org) | iptv-org (`FILMBOXPlusEmotion.pl`) | ❌ רק באפליקציה (HTTP) |
| 514 | Britain's Next Top Model | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`BritainsNextTopModel.se`) | ✅ |
| 515 | Meta Film TV | metafilmhls.wns.live | 🌐 ציבורי (iptv-org) | iptv-org (`MetaFilmTV.ir`) | ✅ |
| 516 | Midsomer Murders | all3media-midsomer-1-us.roku.wurl.tv | 🌐 ציבורי (iptv-org) | iptv-org (`MidsomerMurders.us`) | ✅ |

## 📺 קומדיה (Comedy)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 517 | ערוץ הקומדיה HD | ערוץ FAST ישראלי – AWS CloudFront | ✅ FAST חינמי | iptv-org (`ComedyChannel.il`) | ✅ |
| 518 | NBC Comedy Vault | xumo-xumoent-vc-105-z0vpm.fast.nbcuni.com | 🌐 ציבורי (iptv-org) | iptv-org (`NBCComedyVault.us`) | ✅ |
| 519 | Persiana Comedy | comedyhls.persiana.live | 🌐 ציבורי (iptv-org) | iptv-org (`PersianaComedy.fr`) | ✅ |
| 520 | Ridiculousness | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`Ridiculousness.de`) | ✅ |
| 521 | Comedy Central | team-tv.co | 🌐 ציבורי (iptv-org) | iptv-org (`ComedyCentral.it`) | ❌ רק באפליקציה (HTTP) |
| 522 | KVN TV | fs.uplink.kz | 🌐 ציבורי (iptv-org) | iptv-org (`KVNTV.ru`) | ✅ |
| 523 | ETV Comedy | cc-wie8j8y69d2uy.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`ETVComedy.in`) | ✅ |
| 524 | Comedy Central Brasil | stmv2.srvif.com | 🌐 ציבורי (iptv-org) | iptv-org (`ComedyCentralBrasil.br`) | ✅ |
| 525 | Distrito Comedia | 138.121.15.230 | 🌐 ציבורי (iptv-org) | iptv-org (`DistritoComedia.mx`) | ❌ רק באפליקציה (HTTP) |
| 526 | Awkward | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`Awkward.se`) | ✅ |
| 527 | Comedy Play | stream1.antenaplay.ro | 🌐 ציבורי (iptv-org) | iptv-org (`ComedyPlay.ro`) | ✅ |
| 528 | Jordan Comedy | playlist.fasttvcdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`JordanComedy.jo`) | ✅ |
| 529 | HAHA TV | Antik Telecom (סלובקיה), נתיבי test | ⚠️ ממסר צד שלישי | iptv-org (`HAHATV.sk`) | ✅ |
| 530 | SNL Vault | d4whmvwm0rdvi.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`SNLVault.us`) | ✅ |
| 531 | Comedia a lo bestia de Pluto TV | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`ComediaalobestiadePlutoTV.de`) | ✅ |
| 532 | Komediynoe | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Komediynoe.ru`) | ❌ רק באפליקציה (HTTP) |
| 533 | Sirippoli TV | segment.yuppcdn.net | 🌐 ציבורי (iptv-org) | iptv-org (`SirippoliTV.in`) | ✅ |
| 534 | Beavis and Butt-Head | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`BeavisandButtHead.se`) | ✅ |
| 535 | Pluto TV Comedia made in Spain | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVComediamadeinSpain.de`) | ✅ |
| 536 | Tvoye TV Yumor | tvoetv.space | 🌐 ציבורי (iptv-org) | iptv-org (`TvoyeTVYumor.ru`) | ❌ רק באפליקציה (HTTP) |
| 537 | Broad City | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`BroadCity.se`) | ✅ |
| 538 | 30A Ridiculous TV | 30a-tv.com | 🌐 ציבורי (iptv-org) | iptv-org (`30ARidiculousTV.us`) | ✅ |
| 539 | Alle hassen Chris | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`AllehassenChris.de`) | ✅ |
| 540 | Komedia | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Komedia.ru`) | ❌ רק באפליקציה (HTTP) |
| 541 | Caroline in the City | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`CarolineintheCity.se`) | ✅ |
| 542 | AFV | linear-12.frequency.stream | 🌐 ציבורי (iptv-org) | iptv-org (`AFV.us`) | ✅ |
| 543 | Becker | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`Becker.de`) | ✅ |
| 544 | Chappelle's Show | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`ChappellesShow.se`) | ✅ |
| 545 | Dry Bar Comedy+ | drybar-drybarcomedy-1-au.samsung.wurl.tv | 🌐 ציבורי (iptv-org) | iptv-org (`DryBarComedyPlus.us`) | ✅ |
| 546 | CC Pluto TV | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`CCPlutoTV.de`) | ✅ |
| 547 | Cheers | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`Cheers.se`) | ✅ |

## 📺 ילדים מהעולם (Kids World)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 548 | ניקלודיאון (ברוסית) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Nickelodeon.ru`) | ❌ רק באפליקציה (HTTP) |
| 549 | ניק ג'וניור (ברוסית) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`NickJr.ru`) | ✅ דרך מקור חלופי: Cinerama.uz – ספק IPTV באוזבקיסטן |
| 550 | גולי גירל Gulli Girl (ברוסית) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`GulliGirl.ru`) | ✅ דרך מקור חלופי: Cinerama.uz – ספק IPTV באוזבקיסטן |
| 551 | דאק טי וי פלוס ducktv PLUS HD | Antik Telecom (סלובקיה), נתיבי test | ⚠️ ממסר צד שלישי | iptv-org (`ducktvPLUS.sk`) | ✅ |
| 552 | בייבי פירסט BabyFirst HD | BabyFirst – JW Player FAST | ✅ FAST חינמי | iptv-org (`BabyFirst.us`) | ✅ |
| 553 | אנימציה קצרצרה Cartoons Short HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 554 | ערוץ לגו Lego Channel HD | LEGO Channel – AWS CloudFront FAST | ✅ FAST חינמי | iptv-org (`LegoChannel.us`) | ✅ |
| 555 | ערוץ טיז'י TiJi לילדים HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`TiJi.ru`) | ✅ דרך מקור חלופי: Cinerama.uz – ספק IPTV באוזבקיסטן |
| 556 | ג'ים ג'אם JimJam לילדים HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`JimJamEurope.uk`) | ❌ רק באפליקציה (HTTP) |
| 557 | אנימציה וילדים Fresh Kids HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 558 | קלאסיקות מצוירות 90s HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 559 | סרטי אנימציה Cartoons Big HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | רשימה ידנית (גרסה קודמת) | ❌ רק באפליקציה (HTTP) |
| 560 | קולנוע אגדות וילדים HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`KinoKazka.es`) | ❌ רק באפליקציה (HTTP) |
| 561 | פיקסל TV ילדים HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`PixelTV.ua`) | ✅ דרך מקור חלופי: live-tv.cloud – ספק IPTV אוקראיני |
| 562 | ניקי קידס Niki Kids HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`NikiKids.ua`) | ❌ רק באפליקציה (HTTP) |
| 563 | קפטן פנטסטיקה HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`KapitanFantastika.ru`) | ✅ דרך מקור חלופי: Cinerama.uz – ספק IPTV באוזבקיסטן |
| 564 | ערוץ O! ילדים וטבע | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`O.ru`) | ❌ רק באפליקציה (HTTP) |
| 565 | דאק טי וי ducktv HD | Antik Telecom (סלובקיה), נתיבי test | ⚠️ ממסר צד שלישי | iptv-org (`ducktv.sk`) | ✅ |
| 566 | בייבי שארק Baby Shark TV HD | Baby Shark TV – Wurl (Roku FAST) | ✅ FAST חינמי | iptv-org (`BabySharkTV.us`) | ✅ |
| 567 | ערוץ השמש לילדים HD (Solntse Kids) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Solnce.ru`) | ✅ דרך מקור חלופי: ערוץ השמש (Солнце) – CDN |
| 568 | ערוץ מולט ילדים HD (Mult Kids) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Mult.ru`) | ✅ דרך מקור חלופי: Cinerama.uz – ספק IPTV באוזבקיסטן |
| 569 | מולט ומוזיקה לילדים HD (Mult & Music) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Multimuzyka.ru`) | ❌ רק באפליקציה (HTTP) |
| 570 | קרוסלה ערוץ הילדים HD (Carousel Kids) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Carousel.ru`) | ✅ דרך מקור חלופי: Smotrim (VGTRK) – הבעלים של קרוסלה |
| 571 | סי טי סי קידס HD (CTC Kids) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`STSkids.ru`) | ❌ רק באפליקציה (HTTP) |
| 572 | ערוץ אגדות וסיפורים HD (Fairy Tales Kids) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Vgostyakhuskazki.ru`) | ✅ דרך מקור חלופי: Cinerama.uz – ספק IPTV באוזבקיסטן |
| 573 | ערוץ אני אנימציה HD (Ani Kids) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Ani.ru`) | ❌ רק באפליקציה (HTTP) |
| 574 | ערוץ האנימציה PLUSPLUS HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`PLUSPLUS.ua`) | ❌ רק באפליקציה (HTTP) |
| 575 | Botflix TV | hls.botflix.tv | 🌐 ציבורי (iptv-org) | iptv-org (`BotflixTV.us`) | ✅ |
| 576 | Moonbug Kids | moonbug-rokuus.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`MoonbugKids.uk`) | ✅ |
| 577 | ADN TV+ | d3b73b34o7cvkq.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`ADNTVPlus.fr`) | ✅ |
| 578 | KiKA | kikageohls.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`KiKA.de`) | ✅ |
| 579 | Logos TV Kids | streamer1.streamhost.org | 🌐 ציבורי (iptv-org) | iptv-org (`LogosTVKids.es`) | ✅ |
| 580 | BeJoy.kids | stream.paroledivita.org | 🌐 ציבורי (iptv-org) | iptv-org (`BeJoykids.it`) | ✅ |
| 581 | Smile TV | 5d23269b3ec0c.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`SmileTV.ru`) | ✅ |
| 582 | Niki Junior | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`NikiJunior.ua`) | ❌ רק באפליקציה (HTTP) |
| 583 | Knowledge Network | d1wal6k3d7ssin.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`KnowledgeNetwork.ca`) | ✅ |
| 584 | ABC Entertains | c.mjh.nz | 🌐 ציבורי (iptv-org) | iptv-org (`ABCEntertains.au`) | ✅ |
| 585 | Atfal & Mawaheb TV | 5aafcc5de91f1.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`AtfalMawahebTV.sa`) | ✅ |
| 586 | Koogi TV | 5d658d7e9f562.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`KoogiTV.eg`) | ✅ |
| 587 | Taha TV | stream.starmenajo.com | 🌐 ציבורי (iptv-org) | iptv-org (`TahaTV.lb`) | ✅ |
| 588 | Epic Kids | cc-t8lqe1o99pszu.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`EpicKids.in`) | ✅ |
| 589 | EBS Kids | ebsonair.ebs.co.kr | 🌐 ציבורי (iptv-org) | iptv-org (`EBSKids.kr`) | ✅ |
| 590 | CCTV-14 | 74.91.26.218 | 🌐 ציבורי (iptv-org) | iptv-org (`CCTV14.cn`) | ❌ רק באפליקציה (HTTP) |
| 591 | NickOnline | x1colegal.com | 🌐 ציבורי (iptv-org) | iptv-org (`NickOnline.br`) | ✅ |
| 592 | El Chavo TV | live20.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`ElChavoTV.mx`) | ✅ |
| 593 | Kuriakos Kids | w2.manasat.com | 🌐 ציבורי (iptv-org) | iptv-org (`KuriakosKids.pt`) | ✅ |
| 594 | JapanimTV | foxkidstv.be | 🌐 ציבורי (iptv-org) | iptv-org (`JapanimTV.be`) | ✅ |
| 595 | 4 Fun Kids | 4FUN TV – שרת רשמי | ✅ רשמי | iptv-org (`4FunKids.pl`) | ✅ |
| 596 | Pluto TV Juniorklubben | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVJuniorklubben.se`) | ✅ |
| 597 | TraLaLa DSN | hls01ns.antenaplay.ro | 🌐 ציבורי (iptv-org) | iptv-org (`TraLaLaDSN.ro`) | ✅ |
| 598 | Chikitoonz | 45.70.201.81 | 🌐 ציבורי (iptv-org) | iptv-org (`Chikitoonz.pe`) | ❌ רק באפליקציה (HTTP) |
| 599 | Groovia Kids | gist.githubusercontent.com | 🌐 ציבורי (iptv-org) | iptv-org (`GrooviaKids.id`) | ✅ |
| 600 | ON Kids | freem3u.xyz | 🌐 ציבורי (iptv-org) | iptv-org (`ONKids.vn`) | ✅ |
| 601 | Minimax | vodzong.mjunoon.tv | 🌐 ציבורי (iptv-org) | iptv-org (`Minimax.pk`) | ✅ |
| 602 | Pooya TV | tgn.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`PooyaTV.ir`) | ✅ |
| 603 | Tom And Jerry | live20.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`TomAndJerry.do`) | ✅ |
| 604 | MAX Anime | cdnlive.klicgo.net | 🌐 ציבורי (iptv-org) | iptv-org (`MAXAnime.ve`) | ✅ |
| 605 | Afarin Baxcha | 5dcabf026b188.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`AfarinBaxcha.iq`) | ✅ |
| 606 | Akili Kids! | m6.livecdn.io | 🌐 ציבורי (iptv-org) | iptv-org (`AkiliKids.ke`) | ✅ |
| 607 | ZooMoo | zoomoo-samsungau.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`ZooMoo.sg`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 608 | Amou Yazid TV | mkstream.servehttp.com | 🌐 ציבורי (iptv-org) | iptv-org (`AmouYazidTV.dz`) | ❌ רק באפליקציה (HTTP) |
| 609 | Sat 7 Kids | Asharq Discovery – ItWorks (רשמי) | ✅ רשמי | iptv-org (`Sat7Kids.cy`) | ✅ |
| 610 | Dreiko TV | cloudvideo.servers10.com | 🌐 ציבורי (iptv-org) | iptv-org (`DreikoTV.pa`) | ✅ |
| 611 | Extrema Kids TV | 627bb251f23c7.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`ExtremaKidsTV.cr`) | ✅ |
| 612 | Balapan | balapantv-stream.qazcdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`Balapan.kz`) | ✅ |
| 613 | ETV 2 | sb.err.ee | 🌐 ציבורי (iptv-org) | iptv-org (`ETV2.ee`) | ✅ |
| 614 | MiMi TV | fl.biztv.media | 🌐 ציבורי (iptv-org) | iptv-org (`MiMiTV.uz`) | ✅ |
| 615 | HappyKids | dil9xdvretp0f.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`HappyKids.us`) | ✅ |
| 616 | BabyTV | 138.121.15.230 | 🌐 ציבורי (iptv-org) | iptv-org (`BabyTV.uk`) | ❌ רק באפליקציה (HTTP) |
| 617 | Persiana Junior | junhls.persiana.live | 🌐 ציבורי (iptv-org) | iptv-org (`PersianaJunior.fr`) | ✅ |
| 618 | RiC | rictv.iptv-playoutcenter.de | 🌐 ציבורי (iptv-org) | iptv-org (`RiC.de`) | ✅ |
| 619 | Boing | boing.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`Boing.it`) | ✅ |
| 620 | Smotrim 100% Detskoe | Smotrim (VGTRK) – הבעלים של קרוסלה | ✅ רשמי | iptv-org (`Smotrim100Detskoe.ru`) | ✅ |
| 621 | TVOKids | fastly.live.brightcove.com | 🌐 ציבורי (iptv-org) | iptv-org (`TVOKids.ca`) | ✅ |
| 622 | ABC Kids | c.mjh.nz | 🌐 ציבורי (iptv-org) | iptv-org (`ABCKids.au`) | ✅ |
| 623 | Minika Cocuk | rnttwmjcin.turknet.ercdn.net | 🌐 ציבורי (iptv-org) | iptv-org (`MinikaCocuk.tr`) | ✅ |
| 624 | The Jungle Book | cc-4bhi5osabejc9.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`TheJungleBook.in`) | ✅ |
| 625 | Box Kids TV | 170.83.49.66 | 🌐 ציבורי (iptv-org) | iptv-org (`BoxKidsTV.br`) | ❌ רק באפליקציה (HTTP) |
| 626 | GikTVMX | pistream.ddns.net | 🌐 ציבורי (iptv-org) | iptv-org (`GikTVMX.mx`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 627 | Disney Jr. Latin America | 190.14.10.19 | 🌐 ציבורי (iptv-org) | iptv-org (`DisneyJrLatinAmerica.ar`) | ❌ רק באפליקציה (HTTP) |
| 628 | Disney Channel | 185.227.34.179 | 🌐 ציבורי (iptv-org) | iptv-org (`DisneyChannel.pl`) | ❌ רק באפליקציה (HTTP) |
| 629 | Pluto TV Tecknade klassiker | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVTecknadeklassiker.se`) | ✅ |
| 630 | TraLaLa TV | hls01ns.antenaplay.ro | 🌐 ציבורי (iptv-org) | iptv-org (`TraLaLaTV.ro`) | ✅ |
| 631 | Cartoon Network | vodzong.mjunoon.tv | 🌐 ציבורי (iptv-org) | iptv-org (`CartoonNetwork.pk`) | ✅ |
| 632 | Tiguerito TV | ss2.tvrdomi.com | 🌐 ציבורי (iptv-org) | iptv-org (`TigueritoTV.do`) | ✅ |
| 633 | eSports Max TV | cdnlive.klicgo.net | 🌐 ציבורי (iptv-org) | iptv-org (`eSportsMaxTV.ve`) | ✅ |
| 634 | Afarin TV | 65f16f0fdfc51.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`AfarinTV.iq`) | ✅ |
| 635 | KidsFlix | stream-us-east-1.getpublica.com | 🌐 ציבורי (iptv-org) | iptv-org (`KidsFlix.us`) | ✅ |
| 636 | Nickelodeon | tvsen7.aynascope.net | 🌐 ציבורי (iptv-org) | iptv-org (`Nickelodeon.uk`) | ❌ רק באפליקציה (HTTP) |
| 637 | Tortues Ninja TV | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`TortuesNinjaTV.de`) | ✅ |
| 638 | Super! | 495c5a85d9074f29acffeaea9e0215eb.msvdn.net | 🌐 ציבורי (iptv-org) | iptv-org (`Super.it`) | ✅ |
| 639 | Radost Moya | radm.mediacdn.ru | 🌐 ציבורי (iptv-org) | iptv-org (`RadostMoya.ru`) | ✅ |
| 640 | Minika Go | rnttwmjcin.turknet.ercdn.net | 🌐 ציבורי (iptv-org) | iptv-org (`MinikaGo.tr`) | ✅ |
| 641 | ZB Cartoon | server.zillarbarta.com | 🌐 ציבורי (iptv-org) | iptv-org (`ZBCartoon.in`) | ✅ |
| 642 | DumDum | 45.162.64.114 | 🌐 ציבורי (iptv-org) | iptv-org (`DumDum.br`) | ❌ רק באפליקציה (HTTP) |
| 643 | Pakapaka | 138.59.227.20 | 🌐 ציבורי (iptv-org) | iptv-org (`Pakapaka.ar`) | ❌ רק באפליקציה (HTTP) |
| 644 | Teletoon+ | 92.114.85.87 | 🌐 ציבורי (iptv-org) | iptv-org (`TeletoonPlus.pl`) | ❌ רק באפליקציה (HTTP) |
| 645 | Pluto TV Tecknat | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVTecknat.se`) | ✅ |
| 646 | God Stands Kids Club TV | online.godstands.tv | 🌐 ציבורי (iptv-org) | iptv-org (`GodStandsKidsClubTV.pk`) | ✅ |
| 647 | Chinola TV | tv.wracanal10.com | 🌐 ציבורי (iptv-org) | iptv-org (`ChinolaTV.do`) | ✅ |
| 648 | PBS Kids Alaska | livestream.pbskids.org | 🌐 ציבורי (iptv-org) | iptv-org (`PBSKidsAlaska.us`) | ✅ |
| 649 | Nickelodeon Global | 103.151.60.162 | 🌐 ציבורי (iptv-org) | iptv-org (`NickelodeonGlobal.uk`) | ❌ רק באפליקציה (HTTP) |
| 650 | Bob Esponja | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`BobEsponja.de`) | ✅ |
| 651 | TRT Diyanet Cocuk | tv-trtdiyanetcocuk.medya.trt.com.tr | 🌐 ציבורי (iptv-org) | iptv-org (`TRTDiyanetCocuk.tr`) | ✅ |
| 652 | Cartoon Network HD+ | drk6xq0vhn.gpcdn.net | 🌐 ציבורי (iptv-org) | iptv-org (`CartoonNetworkHDPlus.in`) | ✅ |
| 653 | Plim Plim | 200.115.120.1 | 🌐 ציבורי (iptv-org) | iptv-org (`PlimPlim.ar`) | ❌ רק באפליקציה (HTTP) |
| 654 | Nicktoons | 92.114.85.87 | 🌐 ציבורי (iptv-org) | iptv-org (`Nicktoons.pl`) | ❌ רק באפליקציה (HTTP) |

## 📺 מוזיקה (Music TV)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 655 | 4ever Music Hits HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`4everMusic.ua`) | ❌ רק באפליקציה (HTTP) |
| 656 | 4Fun TV Pop Hits HD | 4FUN TV – שרת רשמי | ✅ רשמי | iptv-org (`4FunTV.pl`) | ✅ |
| 657 | ¡Viva Latino! Dance HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`VivaLatino.ua`) | ❌ רק באפליקציה (HTTP) |
| 658 | 1HD Music Television | שרת IP ללא שם (1HD) | ⚠️ ממסר צד שלישי | iptv-org (`1HDMusicTelevision.ru`) | ❌ רק באפליקציה (HTTP) |
| 659 | 70-80 TV Retro Hits | 70-80 TV – Wowza Streamlock | ✅ רשמי | iptv-org (`7080TV.it`) | ✅ |
| 660 | Botflix Radioo | hls.botflix.tv | 🌐 ציבורי (iptv-org) | iptv-org (`BotflixRadioo.us`) | ✅ |
| 661 | Afrobeat TV Entertainment | stream.ecable.tv | 🌐 ציבורי (iptv-org) | iptv-org (`AfrobeatTVEntertainment.uk`) | ✅ |
| 662 | Trace Africa | channels.trace.plus | 🌐 ציבורי (iptv-org) | iptv-org (`TraceAfrica.fr`) | ✅ |
| 663 | DMF | d-m-f.iptv-playoutcenter.de | 🌐 ציבורי (iptv-org) | iptv-org (`DMF.de`) | ✅ |
| 664 | Music TV Granada | cloudvideo.servers10.com | 🌐 ציבורי (iptv-org) | iptv-org (`MusicTVGranada.es`) | ✅ |
| 665 | FM ITALIA | stream2.xdevel.com | 🌐 ציבורי (iptv-org) | iptv-org (`FMITALIA.it`) | ✅ |
| 666 | Maidan | live-maidantv.cdnvideo.ru | 🌐 ציבורי (iptv-org) | iptv-org (`Maidan.ru`) | ✅ |
| 667 | Happy Radio TV | tv.mediacp.eu | 🌐 ציבורי (iptv-org) | iptv-org (`HappyRadioTV.ua`) | ✅ |
| 668 | Movie Music by Stingray | lotus.stingray.com | 🌐 ציבורי (iptv-org) | iptv-org (`MovieMusicbyStingray.ca`) | ✅ |
| 669 | Folk Klub TV | tv1.intv.mk | 🌐 ציבורי (iptv-org) | iptv-org (`FolkKlubTV.au`) | ❌ רק באפליקציה (HTTP) |
| 670 | MBC FM | dbbv9umqcd7cs.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`MBCFM.ae`) | ✅ |
| 671 | Jeddah Radio | live.kwikmotion.com | 🌐 ציבורי (iptv-org) | iptv-org (`JeddahRadio.sa`) | ✅ |
| 672 | NogoumFMTV | nogoumtv.nrpstream.com | 🌐 ציבורי (iptv-org) | iptv-org (`NogoumFMTV.eg`) | ✅ |
| 673 | Aghani Aghani TV | cdn.streamlane.tv | 🌐 ציבורי (iptv-org) | iptv-org (`AghaniAghaniTV.lb`) | ✅ |
| 674 | Power Dance | livetv.powerapp.com.tr | 🌐 ציבורי (iptv-org) | iptv-org (`PowerDance.tr`) | ✅ |
| 675 | 9X Jalwa | wiselp.wiseplayout.com | 🌐 ציבורי (iptv-org) | iptv-org (`9XJalwa.in`) | ✅ |
| 676 | Gugak TV | mgugaklive.nowcdn.co.kr | 🌐 ציבורי (iptv-org) | iptv-org (`GugakTV.kr`) | ✅ |
| 677 | CCTV-Storm Music | 38.75.136.137 | 🌐 ציבורי (iptv-org) | iptv-org (`CCTVStormMusic.cn`) | ❌ רק באפליקציה (HTTP) |
| 678 | TikTok Radio Brasil | d1bl6tskrpq9ze.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`TikTokRadioBrasil.br`) | ✅ |
| 679 | Bandamax | streaming.onnethn.com | 🌐 ציבורי (iptv-org) | iptv-org (`Bandamax.mx`) | ✅ |
| 680 | La 100 | mdstrm.com | 🌐 ציבורי (iptv-org) | iptv-org (`La100.ar`) | ✅ |
| 681 | ON FM | 5ce9406b73c33.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`ONFM.pt`) | ✅ |
| 682 | 360TuneBox | Antik Telecom (סלובקיה), נתיבי test | ⚠️ ממסר צד שלישי | iptv-org (`360TuneBox.nl`) | ✅ |
| 683 | StuBru | live.vrtcdn.be | 🌐 ציבורי (iptv-org) | iptv-org (`StuBru.be`) | ✅ |
| 684 | RADIO DISCOunt TV | 70-80 TV – Wowza Streamlock | ✅ רשמי | iptv-org (`RADIODISCOuntTV.ch`) | ✅ |
| 685 | Antenne Kaernten | 60efd7a2b4d02.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`AntenneKaernten.at`) | ✅ |
| 686 | Radio RAM | 1888165924.rsc.cdn77.org | 🌐 ציבורי (iptv-org) | iptv-org (`RadioRAM.pl`) | ✅ |
| 687 | Baraza TV | eco.streams.ovh | 🌐 ציבורי (iptv-org) | iptv-org (`BarazaTV.gr`) | ✅ |
| 688 | Ocko Expres | ocko-live.ssl.cdn.cra.cz | 🌐 ציבורי (iptv-org) | iptv-org (`OckoExpres.cz`) | ✅ |
| 689 | Party Mix | hls02ns.antenaplay.ro | 🌐 ציבורי (iptv-org) | iptv-org (`PartyMix.ro`) | ✅ |
| 690 | Oxygen Music | oxygenmusic.hu | 🌐 ציבורי (iptv-org) | iptv-org (`OxygenMusic.hu`) | ✅ |
| 691 | Pauta | redirector.rudo.video | 🌐 ציבורי (iptv-org) | iptv-org (`Pauta.cl`) | ✅ |
| 692 | La Kalle | mdstrm.com | 🌐 ציבורי (iptv-org) | iptv-org (`LaKalle.co`) | ✅ |
| 693 | Radio Onda Digital | live.ondadigitaltv.com | 🌐 ציבורי (iptv-org) | iptv-org (`RadioOndaDigital.pe`) | ✅ |
| 694 | ON Music | freem3u.xyz | 🌐 ציבורי (iptv-org) | iptv-org (`ONMusic.vn`) | ✅ |
| 695 | JooMusic | livecdn.live247stream.com | 🌐 ציבורי (iptv-org) | iptv-org (`JooMusic.pk`) | ✅ |
| 696 | AFN TV | bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`AFNTV.ir`) | ✅ |
| 697 | HMTV | customer-yzxu6n447dx2x45h.cloudflarestream.com | 🌐 ציבורי (iptv-org) | iptv-org (`HMTV.ng`) | ✅ |
| 698 | Gex TV | live20.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`GexTV.cr`) | ✅ |
| 699 | Venus Media | tigocloud.desdeparaguay.net | 🌐 ציבורי (iptv-org) | iptv-org (`VenusMedia.py`) | ✅ |
| 700 | El Sol Network TV | tv.wracanal10.com | 🌐 ציבורי (iptv-org) | iptv-org (`ElSolNetworkTV.ec`) | ✅ |
| 701 | Best of Dance TV | m1b2.worldcast.tv | 🌐 ציבורי (iptv-org) | iptv-org (`BestofDanceTV.ee`) | ✅ |
| 702 | Magic TV | bss1.neterra.tv | 🌐 ציבורי (iptv-org) | iptv-org (`MagicTV.bg`) | ✅ |
| 703 | Cool FM 98.9 | live2.tensila.com | 🌐 ציבורי (iptv-org) | iptv-org (`CoolFM989.aw`) | ✅ |
| 704 | Planet 100.9 FM | streamlov.alsolnet.com | 🌐 ציבורי (iptv-org) | iptv-org (`Planet1009FM.pa`) | ✅ |
| 705 | Radio Hit FM TV | peer2.tdiradio.com | 🌐 ציבורי (iptv-org) | iptv-org (`RadioHitFMTV.rs`) | ✅ |
| 706 | Imagen FM 105.1 | streaming.imagenfm105-1.com | 🌐 ציבורי (iptv-org) | iptv-org (`ImagenFM1051.hn`) | ✅ |
| 707 | FUSION TV | edge20.vedge.infomaniak.com | 🌐 ציבורי (iptv-org) | iptv-org (`FUSIONTV.mq`) | ✅ |
| 708 | Madras FM TV | edge12.vedge.infomaniak.com | 🌐 ציבורי (iptv-org) | iptv-org (`MadrasFMTV.gp`) | ✅ |
| 709 | BTA TV | cloud.fastchannel.es | 🌐 ציבורי (iptv-org) | iptv-org (`BTATV.ve`) | ✅ |
| 710 | The Retro Channel | 583ed2f226ac2.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`TheRetroChannel.pr`) | ✅ |
| 711 | Tezaur TV | tezaurtv.md | 🌐 ציבורי (iptv-org) | iptv-org (`TezaurTV.md`) | ✅ |
| 712 | RTL Gold | stream.rtl.lu | 🌐 ציבורי (iptv-org) | iptv-org (`RTLGold.lu`) | ✅ |
| 713 | Audiokiss | master.tucableip.com | 🌐 ציבורי (iptv-org) | iptv-org (`Audiokiss.bo`) | ✅ |
| 714 | CMC TV | stream.cmctv.hr | 🌐 ציבורי (iptv-org) | iptv-org (`CMCTV.hr`) | ✅ |
| 715 | 98.1 Pearl FM | live2.tensila.com | 🌐 ציבורי (iptv-org) | iptv-org (`981PearlFM.sx`) | ✅ |
| 716 | Scorch TV | w6oy7x7jlxrg-hls-live.5centscdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`ScorchTV.tt`) | ✅ |
| 717 | Star 101 FM | live2.tensila.com | 🌐 ציבורי (iptv-org) | iptv-org (`Star101FM.gu`) | ✅ |
| 718 | Tjil TV | hls-1zdhtxpm-livepush.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`TjilTV.na`) | ✅ |
| 719 | Falastini TV | rp.tactivemedia.com | 🌐 ציבורי (iptv-org) | iptv-org (`FalastiniTV.ps`) | ✅ |
| 720 | Jordan Songs | playlist.fasttvcdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`JordanSongs.jo`) | ✅ |
| 721 | Mosaique FM | webcam.mosaiquefm.net | 🌐 ציבורי (iptv-org) | iptv-org (`MosaiqueFM.tn`) | ✅ |
| 722 | A2i Music | stream.sen-gt.com | 🌐 ציבורי (iptv-org) | iptv-org (`A2iMusic.sn`) | ✅ |
| 723 | Ivoire Channel | video1.getstreamhosting.com | 🌐 ציבורי (iptv-org) | iptv-org (`IvoireChannel.ci`) | ✅ |
| 724 | Muzangala TV | 5cf4a2c2512a2.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`MuzangalaTV.ao`) | ✅ |
| 725 | TV Cidade | stmv1.srvif.com | 🌐 ציבורי (iptv-org) | iptv-org (`TVCidade.cv`) | ✅ |
| 726 | AlbKanale Music TV | albportal.net | 🌐 ציבורי (iptv-org) | iptv-org (`AlbKanaleMusicTV.al`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 727 | Isango TV | 80.241.215.175 | 🌐 ציבורי (iptv-org) | iptv-org (`IsangoTV.rw`) | ❌ רק באפליקציה (HTTP) |
| 728 | Ishtar TV | stream.ishtartv.com | 🌐 ציבורי (iptv-org) | iptv-org (`IshtarTV.iq`) | ✅ |
| 729 | K100 | k100streymi.mbl.is | 🌐 ציבורי (iptv-org) | iptv-org (`K100.is`) | ✅ |
| 730 | Kayhan TV | playout395.livestreamingcdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`KayhanTV.af`) | ✅ |
| 731 | KN Music TV | cdn4.yayin.com.tr | 🌐 ציבורי (iptv-org) | iptv-org (`KNMusicTV.az`) | ✅ |
| 732 | Live99FM | media.streambrothers.com | 🌐 ציבורי (iptv-org) | iptv-org (`Live99FM.bq`) | ✅ |
| 733 | Look Thoong TV | livefta.malimarcdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`LookThoongTV.la`) | ✅ |
| 734 | M-1 | m-1.data.lt | 🌐 ציבורי (iptv-org) | iptv-org (`M1.lt`) | ✅ |
| 735 | Radio SWH TV | 00ff00.latnet.media | 🌐 ציבורי (iptv-org) | iptv-org (`RadioSWHTV.lv`) | ✅ |
| 736 | Turkmen Owazy | alpha.tv.online.tm | 🌐 ציבורי (iptv-org) | iptv-org (`TurkmenOwazy.tm`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 737 | Harmony Gospel Music TV | tgn.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`HarmonyGospelMusicTV.us`) | ✅ |
| 738 | NOW Rock | lightning-now90s-samsungnz.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`NOWRock.uk`) | ✅ |
| 739 | Trace Jama | channels.trace.plus | 🌐 ציבורי (iptv-org) | iptv-org (`TraceJama.fr`) | ✅ |
| 740 | Kiss Kiss TV | 58f12ffd2447a.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`KissKissTV.it`) | ✅ |
| 741 | Muz Soyuz | hls-tvsoyuz.cdnvideo.ru | 🌐 ציבורי (iptv-org) | iptv-org (`MuzSoyuz.ru`) | ✅ |
| 742 | Muzvar | live-tv.cloud – ספק IPTV אוקראיני | ⚠️ ממסר צד שלישי | iptv-org (`Muzvar.ua`) | ✅ |
| 743 | Qello Concerts by Stingray | lotus.stingray.com | 🌐 ציבורי (iptv-org) | iptv-org (`QelloConcertsbyStingray.ca`) | ✅ |
| 744 | INTV Australia | tv1.intv.mk | 🌐 ציבורי (iptv-org) | iptv-org (`INTVAustralia.au`) | ❌ רק באפליקציה (HTTP) |
| 745 | PMC | pmchls.wns.live | 🌐 ציבורי (iptv-org) | iptv-org (`PMC.ae`) | ✅ |
| 746 | Nidae AlIslam Radio | live.kwikmotion.com | 🌐 ציבורי (iptv-org) | iptv-org (`NidaeAlIslamRadio.sa`) | ✅ |
| 747 | El Radio 9090 FM | 9090video.mobtada.com | 🌐 ציבורי (iptv-org) | iptv-org (`ElRadio9090FM.eg`) | ✅ |
| 748 | mfm | hms.pfs.gdn | 🌐 ציבורי (iptv-org) | iptv-org (`mfm.lb`) | ✅ |

## 📺 טבע ודוקו (Documentary & Nature)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 749 | Animal Planet World HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`AnimalPlanet.ru`) | ❌ רק באפליקציה (HTTP) |
| 750 | Discovery חקירות (ID) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`InvestigationDiscovery.ru`) | ❌ רק באפליקציה (HTTP) |
| 751 | עולם וטבע HD (viju+ Planet) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`vijuPlusPlanet.ru`) | ❌ רק באפליקציה (HTTP) |
| 752 | BBC Earth Live HD | BBC Earth – Amagi / Xumo FAST | ✅ FAST חינמי | iptv-org (`BBCEarth.uk`) | ✅ |
| 753 | Asharq Discovery HD | Asharq Discovery – ItWorks (רשמי) | ✅ רשמי | iptv-org (`AsharqDiscovery.sa`) | ✅ |
| 754 | 365 ימי היסטוריה (History TV) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`365daysTV.ru`) | ✅ דרך מקור חלופי: Cinerama.uz – ספק IPTV באוזבקיסטן |
| 755 | Dateline 24/7 | d1si3n1st4nkgb.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`Dateline247.us`) | ✅ |
| 756 | History Hit | lds-timeline-rakuten.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`HistoryHit.uk`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 757 | Francophonie24 | 5421175365ea3.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`Francophonie24.fr`) | ✅ |
| 758 | Adventure Earth | a57e9c69976649b582a8d7604c00e69a.mediatailor.us-east-1.amazonaws.com | 🌐 ציבורי (iptv-org) | iptv-org (`AdventureEarth.de`) | ✅ |
| 759 | Crimen & Historia | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`CrimenHistoria.es`) | ✅ |
| 760 | RT Documentary | rt-rtd.rttv.com | 🌐 ציבורי (iptv-org) | iptv-org (`RTDocumentary.ru`) | ✅ |
| 761 | Total Crime | aegis-cloudfront-1.tubi.video | 🌐 ציבורי (iptv-org) | iptv-org (`TotalCrime.ca`) | ✅ |
| 762 | Asharq Documentary | Asharq Discovery – ItWorks (רשמי) | ✅ רשמי | iptv-org (`AsharqDocumentary.sa`) | ✅ |
| 763 | DocuBay TV | cc-mgr91yrk4pehy.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`DocuBayTV.in`) | ✅ |
| 764 | CGTN Documentary | amg00405-rakutentv-cgtndocumentary-rakuten-0ql8j.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`CGTNDocumentary.cn`) | ✅ |
| 765 | National Geographic | 138.121.15.230 | 🌐 ציבורי (iptv-org) | iptv-org (`NationalGeographic.mx`) | ❌ רק באפליקציה (HTTP) |
| 766 | DocuBox | Antik Telecom (סלובקיה), נתיבי test | ⚠️ ממסר צד שלישי | iptv-org (`DocuBox.nl`) | ✅ |
| 767 | Pluto TV Dokumentar | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVDokumentar.se`) | ✅ |
| 768 | CNA Originals | amg01082-cna-amg01082c1-rlaxx-us-11304.playouts.now.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`CNAOriginals.sg`) | ✅ |
| 769 | Karbala Documentary | ktvlive.online | 🌐 ציבורי (iptv-org) | iptv-org (`KarbalaDocumentary.iq`) | ✅ |
| 770 | Ecovision | streaming.grupomediosdelnorte.com | 🌐 ציבורי (iptv-org) | iptv-org (`Ecovision.do`) | ✅ |
| 771 | UMSA TVU Internacional | edge.enhdtv.com | 🌐 ציבורי (iptv-org) | iptv-org (`UMSATVUInternacional.bo`) | ✅ |
| 772 | MWD Documentary | 203.81.84.130 | 🌐 ציבורי (iptv-org) | iptv-org (`MWDDocumentary.mm`) | ❌ רק באפליקציה (HTTP) |
| 773 | Deadly Women | d1mgh147xpmgs8.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`DeadlyWomen.us`) | ✅ |
| 774 | Into Crime | amg00711-zylo-amg00711c10-rakuten-fr-6731.playouts.now.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`IntoCrime.fr`) | ✅ |
| 775 | Autentic History | 9e754fa707344ccca6d84955c8fcaf36.mediatailor.us-east-1.amazonaws.com | 🌐 ציבורי (iptv-org) | iptv-org (`AutenticHistory.de`) | ✅ |
| 776 | Historia Y Vida | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`HistoriaYVida.es`) | ✅ |
| 777 | Smotrim 100% Fakty | Smotrim (VGTRK) – הבעלים של קרוסלה | ✅ רשמי | iptv-org (`Smotrim100Fakty.ru`) | ✅ |
| 778 | NatureTime | bamusa-naturetime-emea-eng-rakuten.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`NatureTime.ca`) | ✅ |
| 779 | Epic Crimes | cc-wsuyg2uxeak04.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`EpicCrimes.in`) | ✅ |
| 780 | Discovering China | fastlive.cctvplus.com | 🌐 ציבורי (iptv-org) | iptv-org (`DiscoveringChina.cn`) | ✅ |
| 781 | History | 92.114.85.87 | 🌐 ציבורי (iptv-org) | iptv-org (`History.pl`) | ❌ רק באפליקציה (HTTP) |
| 782 | Pluto TV Varldskrigen | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVVarldskrigen.se`) | ✅ |
| 783 | Documentary+ | ef79b15c8c7c46c7a9de9d33001dbd07.mediatailor.us-west-2.amazonaws.com | 🌐 ציבורי (iptv-org) | iptv-org (`DocumentaryPlus.us`) | ✅ |
| 784 | Persiana Docs | scihls.persiana.live | 🌐 ציבורי (iptv-org) | iptv-org (`PersianaDocs.fr`) | ✅ |
| 785 | Dokusat | muc3.iptv-playoutcenter.de | 🌐 ציבורי (iptv-org) | iptv-org (`Dokusat.de`) | ✅ |
| 786 | Storie Criminali | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`StorieCriminali.es`) | ✅ |
| 787 | Nano | 185.23.80.23 | 🌐 ציבורי (iptv-org) | iptv-org (`Nano.ru`) | ❌ רק באפליקציה (HTTP) |
| 788 | Animal Planet | drk6xq0vhn.gpcdn.net | 🌐 ציבורי (iptv-org) | iptv-org (`AnimalPlanet.in`) | ✅ |
| 789 | CCTV-9 | xykt-fix.github.io | 🌐 ציבורי (iptv-org) | iptv-org (`CCTV9.cn`) | ✅ |
| 790 | National Geographic | 92.114.85.87 | 🌐 ציבורי (iptv-org) | iptv-org (`NationalGeographic.pl`) | ❌ רק באפליקציה (HTTP) |
| 791 | Smithsonian Channel Selects | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`SmithsonianChannelSelects.se`) | ✅ |
| 792 | The Nest | fast-channels.sinclairstoryline.com | 🌐 ציבורי (iptv-org) | iptv-org (`TheNest.us`) | ✅ |
| 793 | Persiana Travel | ptravelhls.persiana.live | 🌐 ציבורי (iptv-org) | iptv-org (`PersianaTravel.fr`) | ✅ |
| 794 | Terra Mater WILD | 53ff3982.wurl.com | 🌐 ציבורי (iptv-org) | iptv-org (`TerraMaterWILD.de`) | ✅ |
| 795 | Investigation Discovery | drk6xq0vhn.gpcdn.net | 🌐 ציבורי (iptv-org) | iptv-org (`InvestigationDiscovery.in`) | ✅ |
| 796 | CCTV-Weapon & Technology | 38.75.136.137 | 🌐 ציבורי (iptv-org) | iptv-org (`CCTVWeaponTechnology.cn`) | ❌ רק באפליקציה (HTTP) |
| 797 | National Geographic Wild | 92.114.85.87 | 🌐 ציבורי (iptv-org) | iptv-org (`NationalGeographicWild.pl`) | ❌ רק באפליקציה (HTTP) |
| 798 | Pluto TV Alien Invasion | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVAlienInvasion.se`) | ✅ |
| 799 | Pluto TV Histoire | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVHistoire.de`) | ✅ |
| 800 | Safari TV | mumt04.tangotv.in | 🌐 ציבורי (iptv-org) | iptv-org (`SafariTV.in`) | ✅ |
| 801 | Polsat Viasat Explore | 92.114.85.87 | 🌐 ציבורי (iptv-org) | iptv-org (`PolsatViasatExplore.pl`) | ❌ רק באפליקציה (HTTP) |
| 802 | Pluto TV Space | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVSpace.se`) | ✅ |
| 803 | Crime 360 | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`Crime360.us`) | ✅ |
| 804 | Pluto TV Historia | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVHistoria.de`) | ✅ |
| 805 | Polsat Viasat Nature | 92.114.85.87 | 🌐 ציבורי (iptv-org) | iptv-org (`PolsatViasatNature.pl`) | ❌ רק באפליקציה (HTTP) |
| 806 | Pluto TV Verdenskrigene | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVVerdenskrigene.se`) | ✅ |
| 807 | Get.factual | d1nhni5l2n8hjt.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`Getfactual.us`) | ✅ |
| 808 | kabel eins Doku | s6.hopslan.com | 🌐 ציבורי (iptv-org) | iptv-org (`kabeleinsDoku.de`) | ✅ |
| 809 | History Asia | freem3u.xyz | 🌐 ציבורי (iptv-org) | iptv-org (`HistoryAsia.us`) | ✅ |
| 810 | Pluto TV Alien Invasion | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVAlienInvasion.de`) | ✅ |

## 📺 טיולים והרפתקאות (Travel)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 811 | ערוץ החופשות והנופש HD | ערוץ FAST ישראלי – AWS CloudFront | ✅ FAST חינמי | iptv-org (`VacationChannel.il`) | ✅ |
| 812 | ערוץ הטיולים Travel Channel | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`TravelChannel.ru`) | ❌ רק באפליקציה (HTTP) |
| 813 | AKC TV | broadcast.blivenyc.com | 🌐 ציבורי (iptv-org) | iptv-org (`AKCTV.us`) | ✅ |
| 814 | Horse & Country TV | hnc-free-viewlift.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`HorseCountryTV.uk`) | ✅ |
| 815 | Voyages & Saveurs | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`VoyagesSaveurs.es`) | ✅ |
| 816 | Peer TV South Tyrol | iptv.peer.biz | 🌐 ציבורי (iptv-org) | iptv-org (`PeerTVSouthTyrol.it`) | ✅ |
| 817 | Dikij | stream3.cinerama.uz | 🌐 ציבורי (iptv-org) | iptv-org (`Dikij.ru`) | ✅ |
| 818 | Rybalka | dash2.antik.sk | 🌐 ציבורי (iptv-org) | iptv-org (`Rybalka.ua`) | ✅ |
| 819 | Love Nature | aegis-cloudfront-1.tubi.video | 🌐 ציבורי (iptv-org) | iptv-org (`LoveNature.ca`) | ✅ |
| 820 | China Travel | fastlive.cctvplus.com | 🌐 ציבורי (iptv-org) | iptv-org (`ChinaTravel.cn`) | ✅ |
| 821 | Terra Viva | 9ada494d.wurl.com | 🌐 ציבורי (iptv-org) | iptv-org (`TerraViva.br`) | ✅ |
| 822 | Ciudad Magica | lbgo.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`CiudadMagica.ar`) | ✅ |
| 823 | INTRAVEL | amg00861-amg00861c10-rakuten-uk-3152.playouts.now.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`INTRAVEL.nl`) | ✅ |
| 824 | Swamiji TV American | stream.swamiji.tv | 🌐 ציבורי (iptv-org) | iptv-org (`SwamijiTVAmerican.at`) | ✅ |
| 825 | Travel Channel | 92.114.85.87 | 🌐 ציבורי (iptv-org) | iptv-org (`TravelChannel.pl`) | ❌ רק באפליקציה (HTTP) |
| 826 | Agro TV | agrotv.streamnet.ro | 🌐 ציבורי (iptv-org) | iptv-org (`AgroTV.ro`) | ✅ |
| 827 | Turismo Travel TV | stmv3.voxtvhd.com.br | 🌐 ציבורי (iptv-org) | iptv-org (`TurismoTravelTV.cl`) | ✅ |
| 828 | El Mundo del Campo | movil.ejeserver.com | 🌐 ציבורי (iptv-org) | iptv-org (`ElMundodelCampo.co`) | ✅ |
| 829 | Discover Pakistan | livecdn.live247stream.com | 🌐 ציבורי (iptv-org) | iptv-org (`DiscoverPakistan.pk`) | ✅ |
| 830 | WildEarth | dqga3jatxofgx.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`WildEarth.za`) | ✅ |
| 831 | Agrotendencia TV | 5fc584f3f19c9.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`AgrotendenciaTV.cr`) | ✅ |
| 832 | Travel TV | streamer103.neterra.tv | 🌐 ציבורי (iptv-org) | iptv-org (`TravelTV.bg`) | ✅ |
| 833 | Aruba.TV | cdn01.setar.aw | 🌐 ציבורי (iptv-org) | iptv-org (`ArubaTV.aw`) | ✅ |
| 834 | OEPM TV | cnn.hostlagarto.com | 🌐 ציבורי (iptv-org) | iptv-org (`OEPMTV.do`) | ✅ |
| 835 | Productiva TV | copacoradios.desdeparaguay.net | 🌐 ציבורי (iptv-org) | iptv-org (`ProductivaTV.py`) | ✅ |
| 836 | Sun Channel | 168.196.127.137 | 🌐 ציבורי (iptv-org) | iptv-org (`SunChannel.ve`) | ❌ רק באפליקציה (HTTP) |
| 837 | TV OFF | edge.enhdtv.com | 🌐 ציבורי (iptv-org) | iptv-org (`TVOFF.bo`) | ✅ |
| 838 | ARDB TV | ardbtv.ardb.com.kh | 🌐 ציבורי (iptv-org) | iptv-org (`ARDBTV.kh`) | ✅ |
| 839 | DroneTV | airvuz-dronetv-1-us.ohlscdn.wurl.tv | 🌐 ציבורי (iptv-org) | iptv-org (`DroneTV.us`) | ✅ |
| 840 | Space Live powered by sen | linear-1224.frequency.stream | 🌐 ציבורי (iptv-org) | iptv-org (`SpaceLivepoweredbysen.uk`) | ✅ |
| 841 | Dikaya okhota | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Dikayaokhota.ru`) | ❌ רק באפליקציה (HTTP) |
| 842 | CCTV-17 | 74.91.26.218 | 🌐 ציבורי (iptv-org) | iptv-org (`CCTV17.cn`) | ❌ רק באפליקציה (HTTP) |
| 843 | AgroCanal | aovivo.equipea.com.br | 🌐 ציבורי (iptv-org) | iptv-org (`AgroCanal.br`) | ✅ |
| 844 | Swamiji TV Australian | stream.swamiji.tv | 🌐 ציבורי (iptv-org) | iptv-org (`SwamijiTVAustralian.at`) | ✅ |
| 845 | Travel Mix | hls02ns.antenaplay.ro | 🌐 ציבורי (iptv-org) | iptv-org (`TravelMix.ro`) | ✅ |
| 846 | UMC TV | streaming5.globalhostla.com | 🌐 ציבורי (iptv-org) | iptv-org (`UMCTV.co`) | ✅ |
| 847 | Agro TV | restr2.bgtv.bg | 🌐 ציבורי (iptv-org) | iptv-org (`AgroTV.bg`) | ✅ |
| 848 | Duck Hunting TV | main.duckhunting.playout.vju.tv | 🌐 ציבורי (iptv-org) | iptv-org (`DuckHuntingTV.us`) | ✅ |
| 849 | Dikaya rybalka | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Dikayarybalka.ru`) | ❌ רק באפליקציה (HTTP) |
| 850 | Guangxi Variety & Travel Channel | 111.59.24.227 | 🌐 ציבורי (iptv-org) | iptv-org (`GuangxiVarietyTravelChannel.cn`) | ❌ רק באפליקציה (HTTP) |
| 851 | Canal Rural | 170.83.49.66 | 🌐 ציבורי (iptv-org) | iptv-org (`CanalRural.br`) | ❌ רק באפליקציה (HTTP) |
| 852 | Swamiji TV European | stream.swamiji.tv | 🌐 ציבורי (iptv-org) | iptv-org (`SwamijiTVEuropean.at`) | ✅ |
| 853 | Outdoor America | d1e354daam8g5r.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`OutdoorAmerica.us`) | ✅ |
| 854 | Moya stikhiya | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Moyastikhiya.ru`) | ❌ רק באפליקציה (HTTP) |
| 855 | TV Parana Turismo | 200.189.113.201 | 🌐 ציבורי (iptv-org) | iptv-org (`TVParanaTurismo.br`) | ❌ רק באפליקציה (HTTP) |
| 856 | Schladming-Dachstein TV | m317.video-stream-hosting.de | 🌐 ציבורי (iptv-org) | iptv-org (`SchladmingDachsteinTV.at`) | ✅ |
| 857 | Pocono Television Network | dfoiz3dv1euv7.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`PoconoTelevisionNetwork.us`) | ✅ |
| 858 | RTG TV | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`RTGTV.ru`) | ❌ רק באפליקציה (HTTP) |
| 859 | PursuitUP | d1p1siomv3cm7b.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`PursuitUP.us`) | ✅ |
| 860 | Rybolov | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`Rybolov.ru`) | ❌ רק באפליקציה (HTTP) |

## 📺 מדע ולמידה (Science)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 861 | ערוץ החקר והמדע (viju Explore) | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`vijuExplore.ru`) | ❌ רק באפליקציה (HTTP) |
| 862 | BUTV10 | cdnapisec.kaltura.com | 🌐 ציבורי (iptv-org) | iptv-org (`BUTV10.us`) | ✅ |
| 863 | Al Hiwar TV | mn-nl.mncdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`AlHiwarTV.uk`) | ✅ |
| 864 | Persiana Medical | phd2hls.persiana.live | 🌐 ציבורי (iptv-org) | iptv-org (`PersianaMedical.fr`) | ✅ |
| 865 | NRWision | fms.nrwision.de | 🌐 ציבורי (iptv-org) | iptv-org (`NRWision.de`) | ✅ |
| 866 | Platzi TV | mdstrm.com | 🌐 ציבורי (iptv-org) | iptv-org (`PlatziTV.es`) | ✅ |
| 867 | Telepace 3 | 932y4273djv8-hls-live.mariatvcdn.it | 🌐 ציבורי (iptv-org) | iptv-org (`Telepace3.it`) | ✅ |
| 868 | .sci-fi | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`scifi.ru`) | ❌ רק באפליקציה (HTTP) |
| 869 | Ciftci TV | live.artidijitalmedya.com | 🌐 ציבורי (iptv-org) | iptv-org (`CiftciTV.tr`) | ✅ |
| 870 | Discovery Science | drk6xq0vhn.gpcdn.net | 🌐 ציבורי (iptv-org) | iptv-org (`DiscoveryScience.in`) | ✅ |
| 871 | OUN | live.knou.ac.kr | 🌐 ציבורי (iptv-org) | iptv-org (`OUN.kr`) | ✅ |
| 872 | CCTV-World Geography | 38.75.136.137 | 🌐 ציבורי (iptv-org) | iptv-org (`CCTVWorldGeography.cn`) | ❌ רק באפליקציה (HTTP) |
| 873 | TV UFOP | app.tvufop.com.br | 🌐 ציבורי (iptv-org) | iptv-org (`TVUFOP.br`) | ✅ |
| 874 | TV BUAP | tvenvivo.buap.mx | 🌐 ציבורי (iptv-org) | iptv-org (`TVBUAP.mx`) | ✅ |
| 875 | Canal Universidad | videostream.shockmedia.com.ar | 🌐 ציבורי (iptv-org) | iptv-org (`CanalUniversidad.ar`) | ✅ |
| 876 | UTalca TV | unlimited1-us.dps.live | 🌐 ציבורי (iptv-org) | iptv-org (`UTalcaTV.cl`) | ✅ |
| 877 | Eduvision | stmv3.voxtvhd.com.br | 🌐 ציבורי (iptv-org) | iptv-org (`Eduvision.co`) | ✅ |
| 878 | The Indonesia Channel | restreamer.indonusa.id | 🌐 ציבורי (iptv-org) | iptv-org (`TheIndonesiaChannel.id`) | ✅ |
| 879 | DLTV 1 | cdn-live.dltv.ac.th | 🌐 ציבורי (iptv-org) | iptv-org (`DLTV1.th`) | ✅ |
| 880 | Can Tho TV 2 | live.canthotv.vn | 🌐 ציבורי (iptv-org) | iptv-org (`CanThoTV2.vn`) | ✅ |
| 881 | Paighan TV | cdn4.mjunoon.tv | 🌐 ציבורי (iptv-org) | iptv-org (`PaighanTV.pk`) | ✅ |
| 882 | TVK 2 | live.kh.malimarcdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`TVK2.kh`) | ✅ |
| 883 | STVE Telebasica | streamingcws30.com | 🌐 ציבורי (iptv-org) | iptv-org (`STVETelebasica.hn`) | ✅ |
| 884 | Cosmos TV | video.hostingcaaguazu.com | 🌐 ציבורי (iptv-org) | iptv-org (`CosmosTV.py`) | ✅ |
| 885 | Educa TV | avm.ecuamedia.net | 🌐 ציבורי (iptv-org) | iptv-org (`EducaTV.ec`) | ✅ |
| 886 | Radio Universitaria San Andres | edge.enhdtv.com | 🌐 ציבורי (iptv-org) | iptv-org (`RadioUniversitariaSanAndres.bo`) | ✅ |
| 887 | Ryongnamsan Television | live.koryocdn.org | 🌐 ציבורי (iptv-org) | iptv-org (`RyongnamsanTelevision.kp`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 888 | TV Maya | 5ca9af4645e15.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`TVMaya.gt`) | ✅ |
| 889 | Yaslyk | alpha.tv.online.tm | 🌐 ציבורי (iptv-org) | iptv-org (`Yaslyk.tm`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 890 | Create | create.lls.pbs.org | 🌐 ציבורי (iptv-org) | iptv-org (`Create.us`) | ✅ |
| 891 | English Club TV | Cinerama.uz – ספק IPTV באוזבקיסטן | ⚠️ ממסר צד שלישי | iptv-org (`EnglishClubTV.uk`) | ✅ |
| 892 | Curiosity Channel | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`CuriosityChannel.de`) | ✅ |
| 893 | TRT EBA | tv-e-okul01.medya.trt.com.tr | 🌐 ציבורי (iptv-org) | iptv-org (`TRTEBA.tr`) | ✅ |
| 894 | Gyandarshan | cdn.pishow.tv | 🌐 ציבורי (iptv-org) | iptv-org (`Gyandarshan.in`) | ✅ |
| 895 | NBS | media.joycorp.co.kr | 🌐 ציבורי (iptv-org) | iptv-org (`NBS.kr`) | ✅ |
| 896 | CCTV-10 | 74.91.26.218 | 🌐 ציבורי (iptv-org) | iptv-org (`CCTV10.cn`) | ❌ רק באפליקציה (HTTP) |
| 897 | TVE RS | streaming.procergs.com.br | 🌐 ציבורי (iptv-org) | iptv-org (`TVERS.br`) | ✅ |
| 898 | TV UG | 5ca3e84a76d30.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`TVUG.mx`) | ✅ |
| 899 | Liceum TV | 193.225.32.62 | 🌐 ציבורי (iptv-org) | iptv-org (`LiceumTV.hu`) | ❌ רק באפליקציה (HTTP) |
| 900 | TV Usach | stv4.janus.cl | 🌐 ציבורי (iptv-org) | iptv-org (`TVUsach.cl`) | ✅ |
| 901 | Zoom | 138.121.15.230 | 🌐 ציבורי (iptv-org) | iptv-org (`Zoom.co`) | ❌ רק באפליקציה (HTTP) |
| 902 | Rinjani TV | rinjanitv.cloud | 🌐 ציבורי (iptv-org) | iptv-org (`RinjaniTV.id`) | ✅ |
| 903 | DLTV 10 | cdn-live.dltv.ac.th | 🌐 ציבורי (iptv-org) | iptv-org (`DLTV10.th`) | ✅ |
| 904 | HTV4 | freem3u.xyz | 🌐 ציבורי (iptv-org) | iptv-org (`HTV4.vn`) | ✅ |
| 905 | Wiki TV | stream.wikitv.asia | 🌐 ציבורי (iptv-org) | iptv-org (`WikiTV.kh`) | ✅ |
| 906 | Campus TV | 5e85d90130e77.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`CampusTV.hn`) | ✅ |
| 907 | Educanal | 190.128.214.97 | 🌐 ציבורי (iptv-org) | iptv-org (`Educanal.py`) | ❌ רק באפליקציה (HTTP) |
| 908 | UNIANDES TV | video.compuwebecuador.com | 🌐 ציבורי (iptv-org) | iptv-org (`UNIANDESTV.ec`) | ✅ |
| 909 | DKN | video.oct.dc.gov | 🌐 ציבורי (iptv-org) | iptv-org (`DKN.us`) | ✅ |
| 910 | Wonder | lds-wonder-rakuten.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`Wonder.uk`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 911 | iSTUFF | relay2.cdn.fem-net.de | 🌐 ציבורי (iptv-org) | iptv-org (`iSTUFF.de`) | ✅ |

## 📺 לייף סטייל ובידור

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 912 | ערוץ החיים הטובים HD | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`GoodLife.il`) | ❌ רק באפליקציה (HTTP) |
| 913 | AfroLandTV | alt-al.otteravision.com | 🌐 ציבורי (iptv-org) | iptv-org (`AfroLandTV.us`) | ✅ |
| 914 | The Graham Norton Show | amg00654-itv-amg00654c35-rakuten-gb-7598.playouts.now.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`TheGrahamNortonShow.uk`) | ✅ |
| 915 | Monte Carlo Digital Television | fl718492-exthls-p1.flovp.com | 🌐 ציבורי (iptv-org) | iptv-org (`MonteCarloDigitalTelevision.fr`) | ✅ |
| 916 | Autentic Travel | cb0c87cc605942ff9766a4e6744bbadc.mediatailor.us-east-1.amazonaws.com | 🌐 ציבורי (iptv-org) | iptv-org (`AutenticTravel.de`) | ✅ |
| 917 | Hamaika Telebista | cdn3.wowza.com | 🌐 ציבורי (iptv-org) | iptv-org (`HamaikaTelebista.es`) | ✅ |
| 918 | 7 Gold | streamcdnr6-dddfac816632463dac50b21109dc1bb3.msvdn.net | 🌐 ציבורי (iptv-org) | iptv-org (`7Gold.it`) | ✅ |
| 919 | Ministerstvo idey | live-minidey.cdnvideo.ru | 🌐 ציבורי (iptv-org) | iptv-org (`Ministerstvoidey.ru`) | ✅ |
| 920 | K2 | live-tv.cloud – ספק IPTV אוקראיני | ⚠️ ממסר צד שלישי | iptv-org (`K2.ua`) | ✅ |
| 921 | EET TV | eu.streamjo.com | 🌐 ציבורי (iptv-org) | iptv-org (`EETTV.ca`) | ✅ |
| 922 | 10 Bold | dce3793146fef017.mediapackage.us-west-2.amazonaws.com | 🌐 ציבורי (iptv-org) | iptv-org (`10Bold.au`) | ✅ |
| 923 | Edessa TV | canli.edessatv.com | 🌐 ציבורי (iptv-org) | iptv-org (`EdessaTV.tr`) | ✅ |
| 924 | Asianet | da86m1sqpm3o0.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`Asianet.in`) | ✅ |
| 925 | NHK World Premium | media-tyo.hls.nhkworld.jp | 🌐 ציבורי (iptv-org) | iptv-org (`NHKWorldPremium.jp`) | ✅ |
| 926 | NHTV | nonghyup.flive.skcdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`NHTV.kr`) | ❌ רק באפליקציה (HTTP) |
| 927 | Rede NGT | isaocorp.cloudecast.com | 🌐 ציבורי (iptv-org) | iptv-org (`RedeNGT.br`) | ✅ |
| 928 | Azteca Internacional | azt-mun.otteravision.com | 🌐 ציבורי (iptv-org) | iptv-org (`AztecaInternacional.mx`) | ✅ |
| 929 | Metro TV | streamtv12.ddns.net | 🌐 ציבורי (iptv-org) | iptv-org (`MetroTV.ar`) | ✅ |
| 930 | TVI Reality | raw.githubusercontent.com | 🌐 ציבורי (iptv-org) | iptv-org (`TVIReality.pt`) | ✅ |
| 931 | MaLive | live.salto.nl | 🌐 ציבורי (iptv-org) | iptv-org (`MaLive.nl`) | ✅ |
| 932 | Carac 1 | edge13.vedge.infomaniak.com | 🌐 ציבורי (iptv-org) | iptv-org (`Carac1.ch`) | ✅ |
| 933 | Bialogardzka Telewizja Kablowa | bsmbialogard.pl | 🌐 ציבורי (iptv-org) | iptv-org (`BialogardzkaTelewizjaKablowa.pl`) | ✅ |
| 934 | Netmax TV | es1.crystalweb.net | 🌐 ציבורי (iptv-org) | iptv-org (`NetmaxTV.gr`) | ✅ |
| 935 | MTV Catfish | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`MTVCatfish.se`) | ✅ |
| 936 | Canal Motor | digicom.hls.iptvdc.com | 🌐 ציבורי (iptv-org) | iptv-org (`CanalMotor.no`) | ✅ |
| 937 | Jim | live-fi.tvkaista.net | 🌐 ציבורי (iptv-org) | iptv-org (`Jim.fi`) | ✅ |
| 938 | Antena Monden | stream1.antenaplay.ro | 🌐 ציבורי (iptv-org) | iptv-org (`AntenaMonden.ro`) | ✅ |
| 939 | Suyai TV | trun.suyaitv.cl | 🌐 ציבורי (iptv-org) | iptv-org (`SuyaiTV.cl`) | ✅ |
| 940 | RCN Mas | rcntv-rcnmas-1-us.roku.wurl.tv | 🌐 ציבורי (iptv-org) | iptv-org (`RCNMas.co`) | ✅ |
| 941 | AsiriTV | video2.lhdserver.es | 🌐 ציבורי (iptv-org) | iptv-org (`AsiriTV.pe`) | ✅ |
| 942 | Salira TV | live.salira.tv | 🌐 ציבורי (iptv-org) | iptv-org (`SaliraTV.id`) | ✅ |
| 943 | Good Idea TV | stream1.ai-net.net | 🌐 ציבורי (iptv-org) | iptv-org (`GoodIdeaTV.th`) | ❌ רק באפליקציה (HTTP) |
| 944 | ON Vie Giai Tri | freem3u.xyz | 🌐 ציבורי (iptv-org) | iptv-org (`ONVieGiaiTri.vn`) | ✅ |
| 945 | Hum TV | g4wlkwx8l23a-hls-live.5centscdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`HumTV.pk`) | ✅ |
| 946 | 247 Box TV | hls.247box.live | 🌐 ציבורי (iptv-org) | iptv-org (`247BoxTV.ir`) | ✅ |
| 947 | StoryChannel TV | 136044159.r.cdnsun.net | 🌐 ציבורי (iptv-org) | iptv-org (`StoryChannelTV.ma`) | ✅ |
| 948 | 1KZN TV | cdn.freevisiontv.co.za | 🌐 ציבורי (iptv-org) | iptv-org (`1KZNTV.za`) | ✅ |
| 949 | Qausain TV | acasmedia3.acangroup.org | 🌐 ציבורי (iptv-org) | iptv-org (`QausainTV.ng`) | ✅ |
| 950 | Bellavision | fox.hostlagarto.com | 🌐 ציבורי (iptv-org) | iptv-org (`Bellavision.do`) | ✅ |
| 951 | La Nueva Radio TV 97.7 | sv72.ecuaradiotv.net | 🌐 ציבורי (iptv-org) | iptv-org (`LaNuevaRadioTV977.ec`) | ✅ |
| 952 | KIX | freem3u.xyz | 🌐 ציבורי (iptv-org) | iptv-org (`KIX.hk`) | ✅ |
| 953 | ¡OPA! | 5fc584f3f19c9.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`OPA.cr`) | ✅ |
| 954 | Kurdsat | hlspackager.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`Kurdsat.iq`) | ✅ |
| 955 | MiTV | rds3.desdeparaguay.net | 🌐 ציבורי (iptv-org) | iptv-org (`MiTV.py`) | ✅ |
| 956 | EDN TV | 60417ddeaf0d9.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`EDNTV.hn`) | ✅ |
| 957 | Code Fashion TV | cdn2.invivo.bg | 🌐 ציבורי (iptv-org) | iptv-org (`CodeFashionTV.bg`) | ✅ |
| 958 | BTV | streamfi-alphadgtl1.zettawiseroutes.com | 🌐 ציבורי (iptv-org) | iptv-org (`BTV.ug`) | ✅ |
| 959 | Belarus-2 | ngtrk.dc.beltelecom.by | 🌐 ציבורי (iptv-org) | iptv-org (`Belarus2.by`) | ✅ |
| 960 | ETV+ | sb.err.ee | 🌐 ציבורי (iptv-org) | iptv-org (`ETVPlus.ee`) | ✅ |
| 961 | Chandel TV | kali.vdopanel.com | 🌐 ציבורי (iptv-org) | iptv-org (`ChandelTV.tg`) | ✅ |
| 962 | BIZ TV | fl.biztv.media | 🌐 ציבורי (iptv-org) | iptv-org (`BIZTV.uz`) | ✅ |
| 963 | Imai TV | live20.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`ImaiTV.lk`) | ✅ |
| 964 | Ning TV | livefta.malimarcdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`NingTV.la`) | ✅ |
| 965 | Shift TV | hls.shift.tv | 🌐 ציבורי (iptv-org) | iptv-org (`ShiftTV.hr`) | ✅ |
| 966 | KC2 | 5c46fa289c89f.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`KC2.rw`) | ✅ |
| 967 | Med.TV | med-tv-it.fast.rakuten.tv | 🌐 ציבורי (iptv-org) | iptv-org (`MedTV.mt`) | ✅ |
| 968 | OTV | 5dcabf026b188.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`OTV.gh`) | ✅ |
| 969 | RTNB Divertissement | tv2-stream.rtnb.bi | 🌐 ציבורי (iptv-org) | iptv-org (`RTNBDivertissement.bi`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 970 | Superyacht TV | sy.wns.live | 🌐 ציבורי (iptv-org) | iptv-org (`SuperyachtTV.mc`) | ✅ |
| 971 | Trishul Broadcasting Network | live.suricloud.com | 🌐 ציבורי (iptv-org) | iptv-org (`TrishulBroadcastingNetwork.sr`) | ✅ |
| 972 | KTV Arabe | kwtktvata.cdn.mangomolo.com | 🌐 ציבורי (iptv-org) | iptv-org (`KTVArabe.kw`) | ✅ |
| 973 | Oman TV Cultural | partwota.cdn.mgmlcdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`OmanTVCultural.om`) | ✅ |
| 974 | Afroculture TV | streamtv.cmediahosthosting.net | 🌐 ציבורי (iptv-org) | iptv-org (`AfrocultureTV.ci`) | ✅ |
| 975 | D3 TV | lbgo.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`D3TV.ml`) | ✅ |
| 976 | EVI TV | mistserver.evi-tv.com | 🌐 ציבורי (iptv-org) | iptv-org (`EVITV.cd`) | ✅ |
| 977 | PLEX TV | connectiktv.ddns.net | 🌐 ציבורי (iptv-org) | iptv-org (`PLEXTV.cm`) | ❌ רק באפליקציה (HTTP) |
| 978 | America TeVe | live.gideo.video | 🌐 ציבורי (iptv-org) | iptv-org (`AmericaTeVe.pr`) | ✅ |
| 979 | TVCARiB Latino | cdn.mycloudstream.io | 🌐 ציבורי (iptv-org) | iptv-org (`TVCARiBLatino.sx`) | ✅ |
| 980 | UCL | livedelta.cdn.antel.net.uy | 🌐 ציבורי (iptv-org) | iptv-org (`UCL.uy`) | ✅ |
| 981 | Abai TV | abaitv-stream.qazcdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`AbaiTV.kz`) | ✅ |
| 982 | ANT1 Cyprus | l2.cloudskep.com | 🌐 ציבורי (iptv-org) | iptv-org (`ANT1Cyprus.cy`) | ❌ רק באפליקציה (HTTP) |
| 983 | Bonaire.tv | media.streambrothers.com | 🌐 ציבורי (iptv-org) | iptv-org (`Bonairetv.bq`) | ✅ |
| 984 | Bonce TV | seswa.bonce.tv | 🌐 ציבורי (iptv-org) | iptv-org (`BonceTV.cw`) | ✅ |
| 985 | eldo.TV | stream.eldo.lu | 🌐 ציבורי (iptv-org) | iptv-org (`eldoTV.lu`) | ✅ |
| 986 | Fortune TV | 103.215.194.93 | 🌐 ציבורי (iptv-org) | iptv-org (`FortuneTV.mm`) | ❌ רק באפליקציה (HTTP) |
| 987 | Lietuvos Rytas TV | live.lietuvosryto.tv | 🌐 ציבורי (iptv-org) | iptv-org (`LietuvosRytasTV.lt`) | ✅ |
| 988 | Miras | alpha.tv.online.tm | 🌐 ציבורי (iptv-org) | iptv-org (`Miras.tm`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 989 | Nos Isla TV | cdn01.setar.aw | 🌐 ציבורי (iptv-org) | iptv-org (`NosIslaTV.aw`) | ✅ |
| 990 | Ray TV HD | streaming.tvnepal.com | 🌐 ציבורי (iptv-org) | iptv-org (`RayTVHD.np`) | ✅ |
| 991 | RTB 3 | edge13.vedge.infomaniak.com | 🌐 ציבורי (iptv-org) | iptv-org (`RTB3.bf`) | ✅ |
| 992 | TBC2 | stream-134630.castr.net | 🌐 ציבורי (iptv-org) | iptv-org (`TBC2.tz`) | ✅ |
| 993 | TV Vita | Antik Telecom (סלובקיה), נתיבי test | ⚠️ ממסר צד שלישי | iptv-org (`TVVita.sk`) | ✅ |
| 994 | TVBS-Asia | 38.64.72.148 | 🌐 ציבורי (iptv-org) | iptv-org (`TVBSAsia.tw`) | ❌ רק באפליקציה (HTTP) |
| 995 | AMG TV | 2-fss-2.streamhoster.com | 🌐 ציבורי (iptv-org) | iptv-org (`AMGTV.us`) | ✅ |
| 996 | Hobby Maker | lo3.gemporia.com | 🌐 ציבורי (iptv-org) | iptv-org (`HobbyMaker.uk`) | ✅ |
| 997 | FashionTV Paris L'Original | edge-fast3.evrideo.tv | 🌐 ציבורי (iptv-org) | iptv-org (`FashionTVParisLOriginal.fr`) | ✅ |
| 998 | Motorvision | mvg-mv-xumo.otteravision.com | 🌐 ציבורי (iptv-org) | iptv-org (`Motorvision.de`) | ✅ |
| 999 | Historia | d1k3vzh2ivy22k.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`Historia.es`) | ✅ |
| 1000 | Discovery Channel | streaming.aurora.enhanced.live | 🌐 ציבורי (iptv-org) | iptv-org (`DiscoveryChannel.it`) | ✅ |
| 1001 | Russia-K | Smotrim (VGTRK) – הבעלים של קרוסלה | ✅ רשמי | iptv-org (`RussiaK.ru`) | ✅ |
| 1002 | 36.6 TV | dash2.antik.sk | 🌐 ציבורי (iptv-org) | iptv-org (`366TV.ua`) | ✅ |
| 1003 | ZenLIFE by Stingray | lotus.stingray.com | 🌐 ציבורי (iptv-org) | iptv-org (`ZenLIFEbyStingray.ca`) | ✅ |
| 1004 | ABC TV Plus | c.mjh.nz | 🌐 ציבורי (iptv-org) | iptv-org (`ABCTVPlus.au`) | ✅ |
| 1005 | Fortuna TV | edge.taksimbilisim.com | 🌐 ציבורי (iptv-org) | iptv-org (`FortunaTV.tr`) | ✅ |
| 1006 | Colors Kannada | da86m1sqpm3o0.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`ColorsKannada.in`) | ✅ |
| 1007 | CCTV-Health | 38.75.136.137 | 🌐 ציבורי (iptv-org) | iptv-org (`CCTVHealth.cn`) | ❌ רק באפליקציה (HTTP) |
| 1008 | Conecta Mais TV | adylnet.streampool.net | 🌐 ציבורי (iptv-org) | iptv-org (`ConectaMaisTV.br`) | ✅ |
| 1009 | Capital 21 | video.cdmx.gob.mx | 🌐 ציבורי (iptv-org) | iptv-org (`Capital21.mx`) | ✅ |
| 1010 | Power Max Radio TV | videostream.shockmedia.com.ar | 🌐 ציבורי (iptv-org) | iptv-org (`PowerMaxRadioTV.ar`) | ✅ |
| 1011 | SBS6 Classics | sbs6classic.prd1.talpatvcdn.nl | 🌐 ציבורי (iptv-org) | iptv-org (`SBS6Classics.nl`) | ✅ |
| 1012 | Carac 2 | onefmhd.vedge.infomaniak.com | 🌐 ציבורי (iptv-org) | iptv-org (`Carac2.ch`) | ✅ |

## 📺 מסורת וקהילה

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 1013 | ערוץ הידברות HD | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | iptv-org (`Hidabroot.il`) | ✅ |
| 1014 | הלא TV HD (Hala TV) | פאנט – הלא TV (רשמי) | ✅ רשמי | iptv-org (`HalaTV.il`) | ✅ |
| 1015 | ערוץ קבלה לעם | קבלה לעם – שרת רשמי | ✅ רשמי | iptv-org (`KabbalahforthePeopleIsrael.il`) | ✅ |
| 1016 | שלנו TV HD (Shelanu TV) | שלנו TV – CDN77 | ✅ רשמי | iptv-org (`ShelanuTV.il`) | ✅ |
| 1017 | ערוץ 16 ישראל | ערוץ 16 – שרת רשמי | ✅ רשמי | iptv-org (`Channel16.il`) | ✅ |
| 1018 | JLTV טלוויזיה יהודית | jlt-jltv-xumo.otteravision.com | ⚠️ ממסר צד שלישי | iptv-org (`JewishLifeTelevision.us`) | ✅ |

## 📺 ערוצים בינלאומיים (World)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 1019 | DW News English HD | DW – Akamai (רשמי) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 1020 | Al Jazeera English HD | אל-ג'זירה – שרת רשמי | ✅ רשמי | iptv-org (`AlJazeera.qa`) | ✅ |
| 1021 | BFM TV HD (France) | BFM – SFR (רשמי) | ✅ רשמי | iptv-org (`BFMTV.fr`) | ✅ |
| 1022 | BFM Tech & Co HD | BFM – SFR (רשמי) | ✅ רשמי | iptv-org (`BFMTechCo.fr`) | ✅ |
| 1023 | ABC | amg01942-amg01942c6-stirr-us-10178.playouts.now.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`ABC.us`) | ✅ |
| 1024 | Brit Asia TV | hls.autopo.st | 🌐 ציבורי (iptv-org) | iptv-org (`BritAsiaTV.uk`) | ✅ |
| 1025 | 20 Minutes TV | live-20minutestv.digiteka.com | 🌐 ציבורי (iptv-org) | iptv-org (`20MinutesTV.fr`) | ✅ |
| 1026 | Alex Berlin | alex-stream.rosebud-media.de | 🌐 ציבורי (iptv-org) | iptv-org (`AlexBerlin.de`) | ✅ |
| 1027 | Arabi TV | streamtv2.elitecomunicacion.cloud | 🌐 ציבורי (iptv-org) | iptv-org (`ArabiTV.es`) | ✅ |
| 1028 | A2i TV | stream.sen-gt.com | 🌐 ציבורי (iptv-org) | iptv-org (`A2iTV.it`) | ✅ |
| 1029 | Insayt TV | Cinerama.uz – ספק IPTV באוזבקיסטן | ⚠️ ממסר צד שלישי | iptv-org (`InsaytTV.ru`) | ✅ |
| 1030 | 1+1 Marafon | dash2.antik.sk | 🌐 ציבורי (iptv-org) | iptv-org (`1Plus1Marafon.ua`) | ✅ |
| 1031 | Wild TV | dfhsahpa45kk2.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`WildTV.ca`) | ✅ |
| 1032 | ABC TV | c.mjh.nz | 🌐 ציבורי (iptv-org) | iptv-org (`ABCTV.au`) | ✅ |
| 1033 | Ajman TV | cdn1.logichost.in | 🌐 ציבורי (iptv-org) | iptv-org (`AjmanTV.ae`) | ✅ |
| 1034 | Al Rayyan Old TV | alrayyancdn.vidgyor.com | 🌐 ציבורי (iptv-org) | iptv-org (`AlRayyanOldTV.qa`) | ✅ |
| 1035 | Al Riyadh Radio | live.kwikmotion.com | 🌐 ציבורי (iptv-org) | iptv-org (`AlRiyadhRadio.sa`) | ✅ |
| 1036 | Watan TV | rp.tactivemedia.com | 🌐 ציבורי (iptv-org) | iptv-org (`WatanTV.eg`) | ✅ |
| 1037 | Lana TV | cdn.streamlane.tv | 🌐 ציבורי (iptv-org) | iptv-org (`LanaTV.lb`) | ✅ |
| 1038 | Elsharq TV | mn-nl.mncdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`ElsharqTV.tr`) | ✅ |
| 1039 | DD India | mumbai-edge.smartplaytv.in | 🌐 ציבורי (iptv-org) | iptv-org (`DDIndia.in`) | ✅ |
| 1040 | HLAT-DTV | 5c3639aa99149.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`HLATDTV.kr`) | ✅ |
| 1041 | Hebei TV | event.pull.hebtv.com | 🌐 ציבורי (iptv-org) | iptv-org (`HebeiTV.cn`) | ✅ |
| 1042 | 8NTV | 60417ddeaf0d9.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`8NTV.mx`) | ✅ |
| 1043 | Canal 12 Venado Tuerto | nd106.republicaservers.com | 🌐 ציבורי (iptv-org) | iptv-org (`Canal12VenadoTuerto.ar`) | ✅ |
| 1044 | SIC Alta Definicao | production-fast-sic.content.okast.tv | 🌐 ציבורי (iptv-org) | iptv-org (`SICAltaDefinicao.pt`) | ✅ |
| 1045 | TV Rain | wl.tvrain.tv | 🌐 ציבורי (iptv-org) | iptv-org (`TVRain.nl`) | ✅ |
| 1046 | Matele | live.matele.be | 🌐 ציבורי (iptv-org) | iptv-org (`Matele.be`) | ✅ |
| 1047 | Max TV | cdn.katapy.io | 🌐 ציבורי (iptv-org) | iptv-org (`MaxTV.ch`) | ✅ |
| 1048 | Landle TV | streaming13.huberwebmedia.at | 🌐 ציבורי (iptv-org) | iptv-org (`LandleTV.at`) | ✅ |
| 1049 | NewTV | 1409502578.rsc.cdn77.org | 🌐 ציבורי (iptv-org) | iptv-org (`NewTV.pl`) | ✅ |
| 1050 | Alpha TV | alphatvlive2.siliconweb.com | 🌐 ציבורי (iptv-org) | iptv-org (`AlphaTV.gr`) | ✅ |
| 1051 | MTV Reality | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`MTVReality.se`) | ✅ |
| 1052 | Frikanalen | frikanalen.no | 🌐 ציבורי (iptv-org) | iptv-org (`Frikanalen.no`) | ✅ |
| 1053 | DRTV | drlivedrtvahls.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`DRTV.dk`) | ✅ |
| 1054 | MTV Sub | live-fi.tvkaista.net | 🌐 ציבורי (iptv-org) | iptv-org (`MTVSub.fi`) | ✅ |
| 1055 | TG4 | fastly.live.brightcove.com | 🌐 ציבורי (iptv-org) | iptv-org (`TG4.ie`) | ✅ |
| 1056 | Barrandov Krimi | dash4.antik.sk | 🌐 ציבורי (iptv-org) | iptv-org (`BarrandovKrimi.cz`) | ✅ |
| 1057 | A7TV | a7tvlive.ro | 🌐 ציבורי (iptv-org) | iptv-org (`A7TV.ro`) | ✅ |
| 1058 | Hevizi TV | cloudfront44.lexanetwork.com | 🌐 ציבורי (iptv-org) | iptv-org (`HeviziTV.hu`) | ✅ |
| 1059 | Antofagasta TV | unlimited6-cl.dps.live | 🌐 ציבורי (iptv-org) | iptv-org (`AntofagastaTV.cl`) | ✅ |
| 1060 | Aupur Television | canal.aupurtv.co | 🌐 ציבורי (iptv-org) | iptv-org (`AupurTelevision.co`) | ✅ |
| 1061 | Latina | redirector.rudo.video | 🌐 ציבורי (iptv-org) | iptv-org (`Latina.pe`) | ✅ |
| 1062 | TVRI World | ott-balancer.tvri.go.id | 🌐 ציבורי (iptv-org) | iptv-org (`TVRIWorld.id`) | ✅ |
| 1063 | NBT World | cdn-edge.iiptvcdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`NBTWorld.th`) | ✅ |
| 1064 | Vietnam Today | amagi-live.ondemandkorea.com | 🌐 ציבורי (iptv-org) | iptv-org (`VietnamToday.vn`) | ✅ |
| 1065 | PMI TV | cdn.live247stream.com | 🌐 ציבורי (iptv-org) | iptv-org (`PMITV.pk`) | ✅ |
| 1066 | Asil TV | live.asil.tv | 🌐 ציבורי (iptv-org) | iptv-org (`AsilTV.ir`) | ✅ |
| 1067 | 2M Monde | d2qh3gh0k5vp3v.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`2MMonde.ma`) | ✅ |
| 1068 | TV BRICS Africa | cdn.freevisiontv.co.za | 🌐 ציבורי (iptv-org) | iptv-org (`TVBRICSAfrica.za`) | ✅ |
| 1069 | TV-WEST | tv-west.lg.mncdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`TVWEST.ng`) | ✅ |
| 1070 | A7 TV | 183.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`A7TV.do`) | ✅ |
| 1071 | Dmoda Channel | master.tucableip.com | 🌐 ציבורי (iptv-org) | iptv-org (`DmodaChannel.bo`) | ✅ |
| 1072 | Paraguay TV | stream.paraguaytv.gov.py | 🌐 ציבורי (iptv-org) | iptv-org (`ParaguayTV.py`) | ✅ |
| 1073 | MTR | cdnsk003.panaccess.com | 🌐 ציבורי (iptv-org) | iptv-org (`MTR.sk`) | ✅ |
| 1074 | Latina TV | streamtv.latinamedios.com | 🌐 ציבורי (iptv-org) | iptv-org (`LatinaTV.ve`) | ✅ |
| 1075 | America Estereo Quito | video.makrodigital.com | 🌐 ציבורי (iptv-org) | iptv-org (`AmericaEstereoQuito.ec`) | ✅ |
| 1076 | TV Sur Canal 14 | k20.usastreams.com | 🌐 ציבורי (iptv-org) | iptv-org (`TVSurCanal14.cr`) | ✅ |
| 1077 | Canal 100 Chinique | live20.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`Canal100Chinique.gt`) | ✅ |
| 1078 | Al Rasheed TV | media1.livaat.com | 🌐 ציבורי (iptv-org) | iptv-org (`AlRasheedTV.iq`) | ✅ |
| 1079 | Canal 11 | redirector.rudo.video | 🌐 ציבורי (iptv-org) | iptv-org (`Canal11.hn`) | ✅ |
| 1080 | Jordan Archive | playlist.fasttvcdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`JordanArchive.jo`) | ✅ |
| 1081 | Belarus-1 | ngtrk.dc.beltelecom.by | 🌐 ציבורי (iptv-org) | iptv-org (`Belarus1.by`) | ✅ |
| 1082 | Rajeen TV | mn-nl.mncdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`RajeenTV.ps`) | ✅ |
| 1083 | TSV | tsv.md | 🌐 ציבורי (iptv-org) | iptv-org (`TSV.md`) | ✅ |
| 1084 | Jibek Joly | stream.qazcdn.net | 🌐 ציבורי (iptv-org) | iptv-org (`JibekJoly.kz`) | ✅ |
| 1085 | Louga TV | stream.sen-gt.com | 🌐 ציבורי (iptv-org) | iptv-org (`LougaTV.sn`) | ✅ |
| 1086 | TeleOnce | live.11-cdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`TeleOnce.pr`) | ✅ |
| 1087 | Sat 7 Turk | live.artidijitalmedya.com | 🌐 ציבורי (iptv-org) | iptv-org (`Sat7Turk.cy`) | ✅ |
| 1088 | Rajdhani TV | stream.shariarsuvo.com | 🌐 ציבורי (iptv-org) | iptv-org (`RajdhaniTV.bd`) | ✅ |
| 1089 | Nasa TV | stream.nasatv.com.mk | 🌐 ציבורי (iptv-org) | iptv-org (`NasaTV.mk`) | ✅ |
| 1090 | RTK 3 | gjirafa-video-live.gjirafa.net | 🌐 ציבורי (iptv-org) | iptv-org (`RTK3.xk`) | ✅ |
| 1091 | TV10 | 5c46fa289c89f.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`TV10.rw`) | ✅ |
| 1092 | Imedi TV | tv.cdn.xsg.ge | 🌐 ציבורי (iptv-org) | iptv-org (`ImediTV.ge`) | ✅ |
| 1093 | Turkmenistan | alpha.tv.online.tm | 🌐 ציבורי (iptv-org) | iptv-org (`Turkmenistan.tm`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 1094 | CAM 10 TV | stream.it-innov.com | 🌐 ציבורי (iptv-org) | iptv-org (`CAM10TV.cm`) | ✅ |
| 1095 | RTL Zwee | live-edge.rtl.lu | 🌐 ציבורי (iptv-org) | iptv-org (`RTLZwee.lu`) | ✅ |
| 1096 | Verbum TV | video-eu1.streamerr.co | 🌐 ציבורי (iptv-org) | iptv-org (`VerbumTV.lk`) | ✅ |
| 1097 | Vizion Plus | tringliveviz.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`VizionPlus.al`) | ✅ |
| 1098 | Kordia TV | ptvlive.kordia.net.nz | 🌐 ציבורי (iptv-org) | iptv-org (`KordiaTV.nz`) | ✅ |
| 1099 | MNB World | live.mnb.mn | 🌐 ציבורי (iptv-org) | iptv-org (`MNBWorld.mn`) | ✅ |
| 1100 | Television Caribbean | live.kingdom-tv.com | 🌐 ציבורי (iptv-org) | iptv-org (`TelevisionCaribbean.kn`) | ✅ |
| 1101 | Althania | live.kwikmotion.com | 🌐 ציבורי (iptv-org) | iptv-org (`Althania.sy`) | ✅ |
| 1102 | Ayaz TV | janya-ayaztv.vgcdn.net | 🌐 ציבורי (iptv-org) | iptv-org (`AyazTV.az`) | ✅ |
| 1103 | Armenia 2 | amtv.tulixcdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`Armenia2.am`) | ✅ |
| 1104 | SEA TV | seatv.netlinkbroadcaster.com | 🌐 ציבורי (iptv-org) | iptv-org (`SEATV.kh`) | ✅ |
| 1105 | Tamadon TV | hls.tamadon.live | 🌐 ציבורי (iptv-org) | iptv-org (`TamadonTV.af`) | ✅ |
| 1106 | Lao Net TV | livefta.malimarcdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`LaoNetTV.la`) | ✅ |
| 1107 | LTV1 | lsm.eu.cdns-redge.media | 🌐 ציבורי (iptv-org) | iptv-org (`LTV1.lv`) | ✅ |
| 1108 | Radio Televizija Banovina | pool.alter-media.hr | 🌐 ציבורי (iptv-org) | iptv-org (`RadioTelevizijaBanovina.hr`) | ✅ |
| 1109 | RTV Zenica | stream.rtvze.ba | 🌐 ציבורי (iptv-org) | iptv-org (`RTVZenica.ba`) | ✅ |
| 1110 | TVCG MNE | rtcg-live-open.morescreens.com | 🌐 ציבורי (iptv-org) | iptv-org (`TVCGMNE.me`) | ✅ |
| 1111 | Bahrain International | 5c7b683162943.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`BahrainInternational.bh`) | ✅ |
| 1112 | Ignite Television | rpn.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`IgniteTelevision.gy`) | ✅ |
| 1113 | Kantipur TV | ktvhdsg.ekantipur.com | 🌐 ציבורי (iptv-org) | iptv-org (`KantipurTV.np`) | ✅ |
| 1114 | Supreme Master TV | lbs-us1.suprememastertv.com | 🌐 ציבורי (iptv-org) | iptv-org (`SupremeMasterTV.tw`) | ✅ |
| 1115 | Radio Tele Planet Compas | 5dcab9aed5331.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`RadioTelePlanetCompas.ht`) | ✅ |
| 1116 | Tanasuh TV | kilolink.pulsarstarsolutions.com | 🌐 ציבורי (iptv-org) | iptv-org (`TanasuhTV.ly`) | ✅ |
| 1117 | ETV | edge12.vedge.infomaniak.com | 🌐 ציבורי (iptv-org) | iptv-org (`ETV.gp`) | ✅ |
| 1118 | NTV | strhlslb01.streamakaci.tv | 🌐 ציבורי (iptv-org) | iptv-org (`NTV.ci`) | ✅ |
| 1119 | TN8 | streamingcws30.com | 🌐 ציבורי (iptv-org) | iptv-org (`TN8.ni`) | ✅ |
| 1120 | Nex TV Canal 21 | movil.ejeserver.com | 🌐 ציבורי (iptv-org) | iptv-org (`NexTVCanal21.pa`) | ✅ |
| 1121 | RUV | ruv-web-live.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`RUV.is`) | ✅ |
| 1122 | TV Spravedliva Bulgariya | tv.tvsb.bg | 🌐 ציבורי (iptv-org) | iptv-org (`TVSpravedlivaBulgariya.bg`) | ✅ |
| 1123 | CBN TV | 5790d294af2dc.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`CBNTV.vg`) | ✅ |
| 1124 | Inooro TV | live-onesignal.royalmedia.co.ke | 🌐 ציבורי (iptv-org) | iptv-org (`InooroTV.ke`) | ✅ |
| 1125 | Pointe TV | tv.pointville.ag | 🌐 ציבורי (iptv-org) | iptv-org (`PointeTV.ag`) | ✅ |
| 1126 | TVJ | vod2live.univtec.com | 🌐 ציבורי (iptv-org) | iptv-org (`TVJ.jm`) | ✅ |
| 1127 | ZNS-TV | zns-edge1.streamcomedia.cloud | 🌐 ציבורי (iptv-org) | iptv-org (`ZNSTV.bs`) | ✅ |
| 1128 | El Watania 1 | sw1.tanitweb.net | 🌐 ציבורי (iptv-org) | iptv-org (`ElWatania1.tn`) | ✅ |
| 1129 | ERi-TV 1 | jmc-live.ercdn.net | 🌐 ציבורי (iptv-org) | iptv-org (`ERiTV1.er`) | ✅ |
| 1130 | KTV 1 | kwtktv1ta.cdn.mangomolo.com | 🌐 ציבורי (iptv-org) | iptv-org (`KTV1.kw`) | ✅ |
| 1131 | Oman TV | partneta.cdn.mgmlcdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`OmanTV.om`) | ✅ |
| 1132 | RASD TV | cloud.streamingconnect.tv | 🌐 ציבורי (iptv-org) | iptv-org (`RASDTV.eh`) | ✅ |
| 1133 | Sudan TV | cdn-globecast.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`SudanTV.sd`) | ✅ |
| 1134 | TV2 | 185.9.2.18 | 🌐 ציבורי (iptv-org) | iptv-org (`TV2.dz`) | ❌ רק באפליקציה (HTTP) |
| 1135 | Yemen Today TV | video.yementdy.tv | 🌐 ציבורי (iptv-org) | iptv-org (`YemenTodayTV.ye`) | ✅ |
| 1136 | Kalac TV | edge13.vedge.infomaniak.com | 🌐 ציבורי (iptv-org) | iptv-org (`KalacTV.gn`) | ✅ |
| 1137 | RTB | edge12.vedge.infomaniak.com | 🌐 ציבורי (iptv-org) | iptv-org (`RTB.bf`) | ✅ |
| 1138 | RTNB TV | tv1-stream.rtnb.bi | 🌐 ציבורי (iptv-org) | iptv-org (`RTNBTV.bi`) | ✅ |
| 1139 | Tele Peyi | edge20.vedge.infomaniak.com | 🌐 ציבורי (iptv-org) | iptv-org (`TelePeyi.gf`) | ✅ |
| 1140 | Tele Tchad | strhlslb01.streamakaci.tv | 🌐 ציבורי (iptv-org) | iptv-org (`TeleTchad.td`) | ✅ |
| 1141 | Television Espoir 47 | cdn140m.panaccess.com | 🌐 ציבורי (iptv-org) | iptv-org (`TelevisionEspoir47.tg`) | ✅ |
| 1142 | TV Monaco | production-fast-mcrtv.content.okast.tv | 🌐 ציבורי (iptv-org) | iptv-org (`TVMonaco.mc`) | ✅ |

## 📺 כלכלה ועסקים (Business)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 1143 | Ameritrade | tdameritrade-vizio.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`Ameritrade.us`) | ✅ |
| 1144 | BFM Business | live-cdn-stream-euw1.bfmb.bct.nextradiotv.com | 🌐 ציבורי (iptv-org) | iptv-org (`BFMBusiness.fr`) | ✅ |
| 1145 | Negocios TV | streaming013.gestec-video.com | 🌐 ציבורי (iptv-org) | iptv-org (`NegociosTV.es`) | ✅ |
| 1146 | Pro Business | tv-streaming.bsh.ru | 🌐 ציבורי (iptv-org) | iptv-org (`ProBusiness.ru`) | ✅ |
| 1147 | Ausbiz TV | d9quh89lh7dtw.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`AusbizTV.au`) | ✅ |
| 1148 | Al Arabiya Business | live.alarabiya.net | 🌐 ציבורי (iptv-org) | iptv-org (`AlArabiyaBusiness.ae`) | ✅ |
| 1149 | Asharq News | live-news.asharq.com | 🌐 ציבורי (iptv-org) | iptv-org (`AsharqNews.sa`) | ✅ |
| 1150 | Bloomberg HT | tv.ensonhaber.com | 🌐 ציבורי (iptv-org) | iptv-org (`BloombergHT.tr`) | ✅ |
| 1151 | CNBC TV18 | n18syndication.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`CNBCTV18.in`) | ✅ |
| 1152 | CGTN Global Biz | fastlive.cctvplus.com | 🌐 ציבורי (iptv-org) | iptv-org (`CGTNGlobalBiz.cn`) | ✅ |
| 1153 | Canal Z | d1hz1p2gea2zy4.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`CanalZ.be`) | ✅ |
| 1154 | Telewizja Biznesowa | s-pl-01.mediatool.tv | 🌐 ציבורי (iptv-org) | iptv-org (`TelewizjaBiznesowa.pl`) | ✅ |
| 1155 | Aleph Business | streamw.m.ro | 🌐 ציבורי (iptv-org) | iptv-org (`AlephBusiness.ro`) | ✅ |
| 1156 | Tevex | v2.tustreaming.cl | 🌐 ציבורי (iptv-org) | iptv-org (`Tevex.cl`) | ✅ |
| 1157 | CNBC Asia | freem3u.xyz | 🌐 ציבורי (iptv-org) | iptv-org (`CNBCAsia.sg`) | ✅ |
| 1158 | DeFiance Media | amg01529-defiancetv-defiancemedia-glewed-tre8x.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`DeFianceMedia.pr`) | ✅ |
| 1159 | Champion TV | stream.berosat.live | 🌐 ציבורי (iptv-org) | iptv-org (`ChampionTV.ci`) | ✅ |
| 1160 | NW Economie | hls.newworldtv.com | 🌐 ציבורי (iptv-org) | iptv-org (`NWEconomie.tg`) | ✅ |
| 1161 | Zuraca TV | tv3.bitstreaming.net | 🌐 ציבורי (iptv-org) | iptv-org (`ZuracaTV.bo`) | ✅ |
| 1162 | MNTV | www.khuwemultimedia.com | 🌐 ציבורי (iptv-org) | iptv-org (`MNTV.mz`) | ✅ |
| 1163 | apart TV | 2-fss-2.streamhoster.com | 🌐 ציבורי (iptv-org) | iptv-org (`apartTV.lu`) | ✅ |
| 1164 | Bloomberg Originals | bloomberg.com | 🌐 ציבורי (iptv-org) | iptv-org (`BloombergOriginals.us`) | ✅ |
| 1165 | CNBC Arabiya | cnbc-live.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`CNBCArabiya.ae`) | ✅ |
| 1166 | Asharq Radio | Asharq Discovery – ItWorks (רשמי) | ✅ רשמי | iptv-org (`AsharqRadio.sa`) | ✅ |
| 1167 | CNBC-e | hnpsechtsc.turknet.ercdn.net | 🌐 ציבורי (iptv-org) | iptv-org (`CNBCe.tr`) | ✅ |
| 1168 | CNBC TV18 Prime HD | n18syndication.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`CNBCTV18PrimeHD.in`) | ✅ |
| 1169 | CCTV-2 | 74.91.26.218 | 🌐 ציבורי (iptv-org) | iptv-org (`CCTV2.cn`) | ❌ רק באפליקציה (HTTP) |
| 1170 | Kanaal Z | d1hz1p2gea2zy4.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`KanaalZ.be`) | ✅ |
| 1171 | JOB TV | jobtvserver.eu | 🌐 ציבורי (iptv-org) | iptv-org (`JOBTV.ro`) | ✅ |
| 1172 | Bloomberg TV | d8975dc3.wurl.com | 🌐 ציבורי (iptv-org) | iptv-org (`BloombergTV.us`) | ✅ |
| 1173 | NDTV Profit | ndtvprofit.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`NDTVProfit.in`) | ✅ |
| 1174 | Yahoo! Finance | d1ewctnvcwvvvu.cloudfront.net | 🌐 ציבורי (iptv-org) | iptv-org (`YahooFinance.us`) | ✅ |
| 1175 | CNBC Awaaz | n18syndication.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`CNBCAwaaz.in`) | ✅ |
| 1176 | EntrepreneurTV | stream-us-east-1.getpublica.com | 🌐 ציבורי (iptv-org) | iptv-org (`EntrepreneurTV.us`) | ✅ |
| 1177 | CNBC Bajar | n18syndication.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`CNBCBajar.in`) | ✅ |

## 📺 אוכל ובישול (Food)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 1178 | bon appetit | bonappetit-samsung.amagi.tv | 🌐 ציבורי (iptv-org) | iptv-org (`bonappetit.us`) | ✅ |
| 1179 | Master Chefs Academy TV | platform-iuoamc.uk | 🌐 ציבורי (iptv-org) | iptv-org (`MasterChefsAcademyTV.uk`) | ❌ רק באפליקציה (הפניה בלי CORS) |
| 1180 | TV5MONDE Chefs | tvf-tv5ch.otteravision.com | 🌐 ציבורי (iptv-org) | iptv-org (`TV5MONDEChefs.fr`) | ✅ |
| 1181 | Pluto TV Cuisine | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVCuisine.de`) | ✅ |
| 1182 | Food Network | streaming.aurora.enhanced.live | 🌐 ציבורי (iptv-org) | iptv-org (`FoodNetwork.it`) | ✅ |
| 1183 | Kulinar TV | api.alpaca.t62a.com | 🌐 ציבורי (iptv-org) | iptv-org (`KulinarTV.ru`) | ✅ |
| 1184 | Gusto TV | e75fc053c0d2406fb74cdd71d77e1a7f.mediatailor.us-east-1.amazonaws.com | 🌐 ציבורי (iptv-org) | iptv-org (`GustoTV.ca`) | ✅ |
| 1185 | Food Food | mumt03.tangotv.in | 🌐 ציבורי (iptv-org) | iptv-org (`FoodFood.in`) | ✅ |
| 1186 | El Gourmet | iptv.bitred.cl | 🌐 ציבורי (iptv-org) | iptv-org (`ElGourmet.ar`) | ✅ |
| 1187 | Boligkjop i blinde | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`Boligkjopiblinde.se`) | ✅ |
| 1188 | Dens Food Channel | op-flashcon-digdayahd-1.dens.tv | 🌐 ציבורי (iptv-org) | iptv-org (`DensFoodChannel.id`) | ✅ |
| 1189 | Masala TV | cdn4.mjunoon.tv | 🌐 ציבורי (iptv-org) | iptv-org (`MasalaTV.pk`) | ✅ |
| 1190 | Asian Food Network | freem3u.xyz | 🌐 ציבורי (iptv-org) | iptv-org (`AsianFoodNetwork.sg`) | ✅ |
| 1191 | Jordan Kitchen | playlist.fasttvcdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`JordanKitchen.jo`) | ✅ |
| 1192 | Chef Champion | rpn.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`ChefChampion.us`) | ✅ |
| 1193 | Pluto TV Cocina | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVCocina.de`) | ✅ |
| 1194 | City Eden Recepty Gurmana | cityeden.catcast.tv | 🌐 ציבורי (iptv-org) | iptv-org (`CityEdenReceptyGurmana.ru`) | ✅ |
| 1195 | Boligkob i blinde | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`Boligkobiblinde.se`) | ✅ |
| 1196 | Chef Roc Show | rpn.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`ChefRocShow.us`) | ✅ |
| 1197 | Pluto TV Food | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVFood.de`) | ✅ |
| 1198 | FoodTime | mcquack.net – אתר ממסר (restream) של צד שלישי | ⚠️ ממסר צד שלישי | iptv-org (`FoodTime.ru`) | ❌ רק באפליקציה (HTTP) |
| 1199 | Buying Blind Denmark | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`BuyingBlindDenmark.se`) | ✅ |
| 1200 | Gordon Ramsay's Hell's Kitchen | aegis-cloudfront-1.tubi.video | 🌐 ציבורי (iptv-org) | iptv-org (`GordonRamsaysHellsKitchen.us`) | ✅ |
| 1201 | Pluto TV Cucina | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`PlutoTVCucina.de`) | ✅ |
| 1202 | Koch'en pa toppen | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`Kochenpatoppen.se`) | ✅ |
| 1203 | Tastemade Brasil | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`TastemadeBrasil.us`) | ✅ |
| 1204 | MasterChef Danmarks storste madtalenter | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`MasterChefDanmarksstorstemadtalenter.se`) | ✅ |
| 1205 | Cooking Panda | stream-us-east-1.getpublica.com | 🌐 ציבורי (iptv-org) | iptv-org (`CookingPanda.us`) | ✅ |
| 1206 | Med kniven for struben | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`Medknivenforstruben.se`) | ✅ |
| 1207 | Mad Dog and Merrill | rpn.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`MadDogandMerrill.us`) | ✅ |
| 1208 | Skonne ombygninger | Pluto TV (חינמי) דרך שירות ההפניה jmp2.uk | ✅ FAST חינמי | iptv-org (`Skonneombygninger.se`) | ✅ |

## 📺 דת ואמונה (Religion)

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 1209 | Amazing Facts TV | hlspackager.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`AmazingFactsTV.us`) | ✅ |
| 1210 | Faith World TV | faithworldtv.abs.tv | 🌐 ציבורי (iptv-org) | iptv-org (`FaithWorldTV.uk`) | ✅ |
| 1211 | Evandis Gospel TV | evandis.streamakaci.tv | 🌐 ציבורי (iptv-org) | iptv-org (`EvandisGospelTV.fr`) | ✅ |
| 1212 | Sophia TV Francais | bild-und-ton.stream | 🌐 ציבורי (iptv-org) | iptv-org (`SophiaTVFrancais.de`) | ✅ |
| 1213 | Logos TV | streamer1.streamhost.org | 🌐 ציבורי (iptv-org) | iptv-org (`LogosTV.es`) | ✅ |
| 1214 | Padre Pio TV | 600f07e114306.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`PadrePioTV.it`) | ✅ |
| 1215 | Huzur TV | hls-mycdn08724960135.cdnvideo.ru | 🌐 ציבורי (iptv-org) | iptv-org (`HuzurTV.ru`) | ✅ |
| 1216 | EWTN Ukraine | cdn3.wowza.com | 🌐 ציבורי (iptv-org) | iptv-org (`EWTNUkraine.ua`) | ✅ |
| 1217 | 3ABN Canada | 3abn.bozztv.com | 🌐 ציבורי (iptv-org) | iptv-org (`3ABNCanada.ca`) | ✅ |
| 1218 | Mission Asia | livecdn.live247stream.com | 🌐 ציבורי (iptv-org) | iptv-org (`MissionAsia.au`) | ✅ |
| 1219 | Peace TV English | dzkyvlfyge.erbvr.com | 🌐 ציבורי (iptv-org) | iptv-org (`PeaceTVEnglish.ae`) | ✅ |
| 1220 | Qatar TV The Holy Quran | qatartv.akamaized.net | 🌐 ציבורי (iptv-org) | iptv-org (`QatarTVTheHolyQuran.qa`) | ✅ |
| 1221 | Holy Quran Radio | live.kwikmotion.com | 🌐 ציבורי (iptv-org) | iptv-org (`HolyQuranRadio.sa`) | ✅ |
| 1222 | Aghapy TV | 5b622f07944df.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`AghapyTV.eg`) | ✅ |
| 1223 | Noursat English | Asharq Discovery – ItWorks (רשמי) | ✅ רשמי | iptv-org (`NoursatEnglish.lb`) | ✅ |
| 1224 | Diyanet TV | eustr73.mediatriple.net | 🌐 ציבורי (iptv-org) | iptv-org (`DiyanetTV.tr`) | ✅ |
| 1225 | Salvation TV | ktismaservers.in | 🌐 ציבורי (iptv-org) | iptv-org (`SalvationTV.in`) | ✅ |
| 1226 | BTN TV | btn.nowcdn.co.kr | 🌐 ציבורי (iptv-org) | iptv-org (`BTNTV.kr`) | ✅ |
| 1227 | Ando TV | play.kankanlive.com | 🌐 ציבורי (iptv-org) | iptv-org (`AndoTV.cn`) | ❌ רק באפליקציה (HTTP) |
| 1228 | Rede Gospel | cdn.live.br1.jmvstream.com | 🌐 ציבורי (iptv-org) | iptv-org (`RedeGospel.br`) | ✅ |
| 1229 | Monte Maria | 5ca9af4645e15.streamlock.net | 🌐 ציבורי (iptv-org) | iptv-org (`MonteMaria.mx`) | ✅ |
| 1230 | TV Mana Argentina | w2.manasat.com | 🌐 ציבורי (iptv-org) | iptv-org (`TVManaArgentina.ar`) | ✅ |
| 1231 | Mana Church Online | w2.manasat.com | 🌐 ציבורי (iptv-org) | iptv-org (`ManaChurchOnline.pt`) | ✅ |
| 1232 | Immaculata TV | g5nl63z8lpq6-hls-live.5centscdn.com | 🌐 ציבורי (iptv-org) | iptv-org (`ImmaculataTV.ch`) | ✅ |
| 1233 | KIT-TV | everest.pa-hosting.de | 🌐 ציבורי (iptv-org) | iptv-org (`KITTV.at`) | ✅ |
| 1234 | EWTN Poland | cdn3.wowza.com | 🌐 ציבורי (iptv-org) | iptv-org (`EWTNPoland.pl`) | ✅ |
| 1235 | Himlen TV7 | vod.tv7.fi | 🌐 ציבורי (iptv-org) | iptv-org (`HimlenTV7.fi`) | ✅ |
| 1236 | Alfa Omega TV | aom-live.maghost.ro | 🌐 ציבורי (iptv-org) | iptv-org (`AlfaOmegaTV.ro`) | ✅ |
| 1237 | Apostol TV | live.apostoltv.hu | 🌐 ציבורי (iptv-org) | iptv-org (`ApostolTV.hu`) | ✅ |
| 1238 | Fortaleza TV | multimedia.fortalezatv.cl | 🌐 ציבורי (iptv-org) | iptv-org (`FortalezaTV.cl`) | ✅ |

## 📻 רדיו

| # | ערוץ | שרת השידור | סוג | נמצא ב | באתר (דפדפן) |
|---|---|---|---|---|---|
| 17 | כאן ב - רשת ב | Triton StreamTheWorld – מפיץ הרדיו של כאן | ✅ רשמי | radio-browser.info + בדיקת שמות הזרמים של כאן | ✅ |
| 18 | כאן גימל | Triton StreamTheWorld – מפיץ הרדיו של כאן | ✅ רשמי | radio-browser.info + בדיקת שמות הזרמים של כאן | ✅ |
| 19 | כאן 88 | Triton StreamTheWorld – מפיץ הרדיו של כאן | ✅ רשמי | radio-browser.info + בדיקת שמות הזרמים של כאן | ✅ |
| 20 | כאן תרבות | Triton StreamTheWorld – מפיץ הרדיו של כאן | ✅ רשמי | radio-browser.info + בדיקת שמות הזרמים של כאן | ✅ |
| 21 | כאן מורשת | Triton StreamTheWorld – מפיץ הרדיו של כאן | ✅ רשמי | radio-browser.info + בדיקת שמות הזרמים של כאן | ✅ |
| 22 | כאן רקע | Triton StreamTheWorld – מפיץ הרדיו של כאן | ✅ רשמי | radio-browser.info + בדיקת שמות הזרמים של כאן | ✅ |
| 23 | כאן קול המוזיקה | Triton StreamTheWorld – מפיץ הרדיו של כאן | ✅ רשמי | radio-browser.info + בדיקת שמות הזרמים של כאן | ✅ |
| 24 | גלגלצ | גלי צה"ל / גלגלצ – CDN רשמי (Bynet) | ✅ רשמי | radio-browser.info | ✅ |
| 25 | גלי צה"ל | גלי צה"ל / גלגלצ – CDN רשמי (Bynet) | ✅ רשמי | radio-browser.info | ✅ |
| 26 | גלגלצ להיטים | גלי צה"ל / גלגלצ – CDN רשמי (Bynet) | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 27 | eco99fm | eco99fm – Mediacast (רשמי) | ✅ רשמי | radio-browser.info | ✅ |
| 28 | 103FM | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | radio-browser.info | ✅ |
| 29 | רדיוס 100FM | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | radio-browser.info | ✅ |
| 30 | 102FM רדיו תל אביב | 102FM – LiveCDN (רשמי) | ✅ רשמי | radio-browser.info | ✅ |
| 31 | רדיו הקצה KZ | רדיו הקצה – Mediacast (רשמי) | ✅ רשמי | radio-browser.info | ✅ |
| 32 | קול ברמה | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 33 | רדיו דרום 101.5FM | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | radio-browser.info | ✅ |
| 34 | רדיו 2000 | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | רשימה ידנית (גרסה קודמת) | ✅ |
| 35 | קול ברמה מוזיקה (קול פליי) | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | radio-browser.info | ✅ |
| 36 | 104.5FM רדיו צפון | StreamGates – ה-CDN של התחנה | ✅ רשמי | radio-browser.info | ✅ |
| 37 | 101FM רדיו ירושלים | StreamGates – ה-CDN של התחנה | ✅ רשמי | radio-browser.info | ✅ |
| 38 | רדיו חיפה 107.5FM | רדיו חיפה – LiveCDN (רשמי) | ✅ רשמי | radio-browser.info | ✅ |
| 39 | 90FM אמצע הדרך | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | radio-browser.info | ✅ |
| 40 | רדיו דרום 97FM | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | radio-browser.info | ✅ |
| 41 | רדיו התחנה 101.5 | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | radio-browser.info | ✅ |
| 42 | גלי ישראל 94FM | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | radio-browser.info | ✅ |
| 43 | קול חי 93FM | קול חי – שרת התחנה | ✅ רשמי | radio-browser.info | ✅ |
| 44 | קול חי מיוזיק | קול חי – שרת התחנה | ✅ רשמי | radio-browser.info | ✅ |
| 45 | קול רגע 96FM | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | radio-browser.info | ✅ |
| 46 | לב המדינה 91FM | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | radio-browser.info | ✅ |
| 47 | רדיוס נוסטלגי 96.3 | StreamGates – ה-CDN של התחנה | ✅ רשמי | radio-browser.info | ✅ |
| 48 | קול הצפון | eCast – שרת התחנה | ✅ רשמי | radio-browser.info | ✅ |
| 49 | גלגלצ רוק | גלי צה"ל / גלגלצ – CDN רשמי (Bynet) | ✅ רשמי | radio-browser.info | ✅ |
| 50 | גלגלצ עשורים | גלי צה"ל / גלגלצ – CDN רשמי (Bynet) | ✅ רשמי | radio-browser.info | ✅ |
| 51 | גלגלצ רגוע | גלי צה"ל / גלגלצ – CDN רשמי (Bynet) | ✅ רשמי | radio-browser.info | ✅ |
| 52 | רדיוס 100% מזרחית | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | radio-browser.info | ✅ |
| 53 | רדיוס 100% יהודי | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | radio-browser.info | ✅ |
| 54 | רדיוס 100% להיטים | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | radio-browser.info | ✅ |
| 55 | רדיוס 100% שנות ה-80 | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | radio-browser.info | ✅ |
| 56 | רדיוס 100% שנות ה-90 | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | radio-browser.info | ✅ |
| 57 | רדיוס 100% ילדים | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | radio-browser.info | ✅ |
| 58 | רדיוס 100% רגוע | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | radio-browser.info | ✅ |
| 59 | א-שמס A-Shams | StreamGates – ה-CDN של התחנה | ✅ רשמי | radio-browser.info | ✅ |
| 60 | רדיו נאס Radio Nas | StreamGates – ה-CDN של התחנה | ✅ רשמי | radio-browser.info | ✅ |
| 61 | הרדיו הראשון 89.1FM (רוסית) | CyberCDN – ה-CDN של תחנות הרדיו עצמן | ✅ רשמי | radio-browser.info | ✅ |
| 62 | רדיו ברסלב | רדיו ברסלב – שרת התחנה | ✅ רשמי | radio-browser.info | ✅ |
| 63 | רדיו נהריה | רדיו נהריה – שרת התחנה | ✅ רשמי | radio-browser.info | ✅ |
| 64 | רדיוס 100% אימון | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | radio-browser.info | ✅ |
| 65 | רדיוס 100% טופ 40 | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | radio-browser.info | ✅ |
| 66 | רדיוס 100% סרטים | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | radio-browser.info | ✅ |
| 67 | רדיוס 100% אולדיז | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | radio-browser.info | ✅ |
| 68 | רדיוס 100% רטרו | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | radio-browser.info | ✅ |
| 69 | רדיוס 100% צ'ילאאוט | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | radio-browser.info | ✅ |
| 70 | רדיוס 100% קאנטרי | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | radio-browser.info | ✅ |
| 71 | רדיוס 100% K-Pop | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | radio-browser.info | ✅ |
| 72 | רדיוס 100% גיימר | StreamGates – ערוצי רדיוס 100FM | ✅ רשמי | radio-browser.info | ✅ |
| 73 | נושמים מזרחית | www.rs.mizrahit.fm | ⚠️ ממסר צד שלישי | radio-browser.info | ❌ רק באפליקציה (HTTP) |
| 74 | רדיו סהר | eCast – שרת התחנה | ✅ רשמי | radio-browser.info | ✅ |
| 75 | Israeli Voice Radio | icecast.live | ⚠️ ממסר צד שלישי | radio-browser.info | ✅ |
| 76 | ג'וינט רדיו בלוז רוק | jointil.com | ⚠️ ממסר צד שלישי | radio-browser.info | ✅ |
| 77 | ג'וינט רדיו רגאיי | jointil.com | ⚠️ ממסר צד שלישי | radio-browser.info | ✅ |
| 78 | ג'וינט רדיו טראנס | jointil.com | ⚠️ ממסר צד שלישי | radio-browser.info | ✅ |
| 79 | קבלה לעם רדיו | icecast.kab.tv | ⚠️ ממסר צד שלישי | radio-browser.info | ❌ רק באפליקציה (HTTP) |
| 80 | ברסלב: תורה וזימרה | רדיו ברסלב – שרת התחנה | ✅ רשמי | radio-browser.info | ✅ |
| 81 | Radio France Israël | stream10.vestaradio.com | ⚠️ ממסר צד שלישי | radio-browser.info | ✅ |

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

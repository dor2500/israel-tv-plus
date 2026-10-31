import json
import os
import channel_numbers

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
path = os.path.join(ROOT, "channels.json")

with open(path, encoding="utf-8") as f:
    chs = json.load(f)

existing_ids = {c["id"] for c in chs}

new_channels = [
    {
        "id": "formula1_hd",
        "name": "Sky Sports F1 HD (פורמולה 1 שידור חי)",
        "title": "ערוץ פורמולה 1 שידור חי (Sky Sports F1 HD)",
        "category": "ספורט (Sports)",
        "categoryEn": "sports",
        "url": "https://cdn.stmify.com/out/v1/SKY_SPORTS_F1/index.m3u8",
        "backups": ["https://mvg-mv-xumo.otteravision.com/mvg/mv/mv.m3u8"],
        "fallbackUrls": ["https://mvg-mv-xumo.otteravision.com/mvg/mv/mv.m3u8"],
        "badge": "HD",
        "desc": "ערוץ הספורט המוביל בעולם לשידורי פורמולה 1 Formula 1 בשידור חי ובאיכות HD – מרוצים, מקצי דירוג, אימונים ופרשנות עולמית",
        "lang": "en",
        "logo": "https://upload.wikimedia.org/wikipedia/commons/3/33/F1.svg",
        "country": "UK",
        "countryHe": "בריטניה",
        "http_referrer": "https://stmify.com/",
        "src": {
            "by": "Sky Sports F1 Playout",
            "kind": "live",
            "host": "cdn.stmify.com",
            "web": "✅"
        }
    },
    {
        "id": "motorvision_hd",
        "name": "מוטורוויז'ן Motorvision HD",
        "title": "מוטורוויז'ן Motorvision HD",
        "category": "ספורט (Sports)",
        "categoryEn": "sports",
        "url": "https://mvg-mv-xumo.otteravision.com/mvg/mv/mv.m3u8",
        "backups": [],
        "fallbackUrls": [],
        "badge": "HD",
        "desc": "ערוץ הספורט המוטורי ועולם הרכב המוביל Motorvision TV – סופרקארס, מרוצי מסלול ומגזיני רכב",
        "lang": "en",
        "logo": "https://i.imgur.com/v4kj8pI.png",
        "country": "DE",
        "countryHe": "גרמניה",
        "src": {
            "by": "Motorvision FAST",
            "kind": "live",
            "host": "otteravision.com",
            "web": "✅"
        }
    },
    {
        "id": "cars_tv_hd",
        "name": "ערוץ הרכב Cars.TV HD",
        "title": "ערוץ הרכב Cars.TV HD",
        "category": "ספורט (Sports)",
        "categoryEn": "sports",
        "url": "https://a-cdn.klowdtv.com/live2/carstv_720p/playlist.m3u8",
        "backups": [],
        "fallbackUrls": [],
        "badge": "HD",
        "desc": "ערוץ רכבי יוקרה, אספנות וביצועים מבית Cars.TV",
        "lang": "en",
        "logo": "https://i.imgur.com/JLSHiTp.png",
        "country": "US",
        "countryHe": "ארה״ב",
        "src": {
            "by": "Cars.TV KlowdTV",
            "kind": "live",
            "host": "klowdtv.com",
            "web": "✅"
        }
    },
    {
        "id": "racer_international_hd",
        "name": "ערוץ מרוצים RACER International HD",
        "title": "ערוץ מרוצים RACER International HD",
        "category": "ספורט (Sports)",
        "categoryEn": "sports",
        "url": "https://amg00378-mavtv-amg00378c2-rakuten-us-1048.playouts.now.amagi.tv/playlist/amg00378-mavtvfast-motorsportsnetwork-rakutenus/playlist.m3u8",
        "backups": ["https://amg00378-amg00378c4-freelivesports-emea-5637.playouts.now.amagi.tv/playlist.m3u8"],
        "fallbackUrls": ["https://amg00378-amg00378c4-freelivesports-emea-5637.playouts.now.amagi.tv/playlist.m3u8"],
        "badge": "HD",
        "desc": "ערוץ המרוצים הבינלאומי MAVTV / RACER International – כיסוי עולמי של מרוצי מכוניות ואופנועים",
        "lang": "en",
        "logo": "https://provider-static.plex.tv/epg/cms/production/dd4e5871-4347-49e3-9ea1-a8770ea9c518/RACER_Intl_logo_1500x1000.png",
        "country": "US",
        "countryHe": "ארה״ב",
        "src": {
            "by": "RACER International Playout",
            "kind": "live",
            "host": "amagi.tv",
            "web": "✅"
        }
    },
    {
        "id": "powernation_hd",
        "name": "ערוץ PowerNation TV מנועים ומוטורספורט",
        "title": "ערוץ PowerNation TV מנועים ומוטורספורט",
        "category": "ספורט (Sports)",
        "categoryEn": "sports",
        "url": "https://amg01258-raycomsports-powernationau-samsungau-18bwc.amagi.tv/playlist.m3u8",
        "backups": [],
        "fallbackUrls": [],
        "badge": "HD",
        "desc": "ערוץ מוטורי אמריקאי לבניית מכוניות מרוץ, שיפורי מנועים וספורט מוטורי PowerNation TV",
        "lang": "en",
        "logo": "https://images.fubo.tv/station-logos/on-dark/powernation.png",
        "country": "US",
        "countryHe": "ארה״ב",
        "src": {
            "by": "PowerNation Playout",
            "kind": "live",
            "host": "amagi.tv",
            "web": "✅"
        }
    },
    {
        "id": "redbull_tv_extreme",
        "name": "רד בול טי וי אקסטרים Red Bull TV FHD",
        "title": "רד בול טי וי אקסטרים Red Bull TV FHD",
        "category": "ספורט (Sports)",
        "categoryEn": "sports",
        "url": "https://rbmn-live.akamaized.net/hls/live/590964/BoRB-AT/master.m3u8",
        "backups": [],
        "fallbackUrls": [],
        "badge": "FHD",
        "desc": "ערוץ האקסטרים והמוטורספורט הרשמי של רד בול Red Bull TV – מרוצי פורמולה, מוטוקרוס וספורט אתגרי",
        "lang": "en",
        "logo": "https://upload.wikimedia.org/wikipedia/en/thumb/e/e4/Red_Bull_TV_logo.svg/512px-Red_Bull_TV_logo.svg.png",
        "country": "AT",
        "countryHe": "אוסטריה",
        "src": {
            "by": "Red Bull Media House",
            "kind": "live",
            "host": "akamaized.net",
            "web": "✅"
        }
    },
    {
        "id": "sky_racing_hd",
        "name": "ערוץ מרוצים Sky Racing 1 HD",
        "title": "ערוץ מרוצים Sky Racing 1 HD",
        "category": "ספורט (Sports)",
        "categoryEn": "sports",
        "url": "https://636ffd31f0e12.streamlock.net/RacingStream1/RacingStream1/chunklist_w1848520265.m3u8",
        "backups": ["https://636ffd31f0e12.streamlock.net/RacingStream1/RacingStream1/playlist.m3u8"],
        "fallbackUrls": ["https://636ffd31f0e12.streamlock.net/RacingStream1/RacingStream1/playlist.m3u8"],
        "badge": "HD",
        "desc": "ערוץ המרוצים המקצועי Sky Racing לשידורי מרוצים חיים",
        "lang": "en",
        "logo": "https://i.imgur.com/Hf0EiaW.png",
        "country": "AU",
        "countryHe": "אוסטרליה",
        "src": {
            "by": "Sky Racing Playout",
            "kind": "live",
            "host": "streamlock.net",
            "web": "✅"
        }
    },
    {
        "id": "walla_yes_hd",
        "name": "וואלה!+ yes תרבות ובידור HD",
        "title": "וואלה!+ yes תרבות ובידור HD",
        "category": "ערוצי ישראל - שידור חי",
        "categoryEn": "israel",
        "url": "https://live.wcdn.co.il/yes/prog_index.m3u8",
        "backups": [],
        "fallbackUrls": [],
        "badge": "HD",
        "desc": "ערוץ התרבות, הבידור והסדרות של וואלה! בשיתוף yes",
        "lang": "he",
        "isIsraeli": True,
        "logo": "https://upload.wikimedia.org/wikipedia/he/thumb/f/f6/Walla%21_logo_2020.svg/500px-Walla%21_logo_2020.svg.png",
        "country": "IL",
        "countryHe": "ישראל",
        "src": {
            "by": "Walla yes Stream",
            "kind": "live",
            "host": "wcdn.co.il",
            "web": "✅"
        }
    },
    {
        "id": "reality_channel_il",
        "name": "ערוץ הריאליטי Reality Channel HD",
        "title": "ערוץ הריאליטי Reality Channel HD",
        "category": "סרטים וסדרות (Movies & Series)",
        "categoryEn": "movies",
        "url": "https://d2dffl3588mvfk.cloudfront.net/out/v1/d8e15050ca4148aab0ee387a5e2eb46b/index_1.m3u8",
        "backups": [],
        "fallbackUrls": [],
        "badge": "HD",
        "desc": "ערוץ תוכניות המציאות והריאליטי המובילות בישראל ובעולם",
        "lang": "he",
        "isIsraeli": True,
        "logo": "https://static.g-cdn.app/streams/yes-reality-channel-il-hd.png",
        "country": "IL",
        "countryHe": "ישראל",
        "src": {
            "by": "CloudFront CDN",
            "kind": "live",
            "host": "cloudfront.net",
            "web": "✅"
        }
    },
    {
        "id": "vacation_channel_il_cloud",
        "name": "ערוץ הנופש והחופש Vacation HD",
        "title": "ערוץ הנופש והחופש Vacation HD",
        "category": "ערוצי ישראל - שידור חי",
        "categoryEn": "israel",
        "url": "https://d1yd8hohnldm33.cloudfront.net/out/v1/19dee23c2cc24f689bd4e1288661ee0c/index_2.m3u8",
        "backups": ["http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/32160/index.m3u8"],
        "fallbackUrls": ["http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/32160/index.m3u8"],
        "badge": "HD",
        "desc": "ערוץ התיירות, הנופש והמלונות המובילים בארץ ובעולם",
        "lang": "he",
        "isIsraeli": True,
        "logo": "https://static.g-cdn.app/streams/vacation-channel-il-hd.png",
        "country": "IL",
        "countryHe": "ישראל",
        "src": {
            "by": "CloudFront CDN",
            "kind": "live",
            "host": "cloudfront.net",
            "web": "✅"
        }
    },
    {
        "id": "comedy_channel_il_cloud",
        "name": "ערוץ הקומדיה Comedy Channel HD",
        "title": "ערוץ הקומדיה Comedy Channel HD",
        "category": "סרטים וסדרות (Movies & Series)",
        "categoryEn": "movies",
        "url": "https://d15ds134q59udk.cloudfront.net/out/v1/fbba879221d045598540ee783b140fe2/index.m3u8",
        "backups": ["http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/32136/index.m3u8"],
        "fallbackUrls": ["http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/32136/index.m3u8"],
        "badge": "HD",
        "desc": "ערוץ הצחוק, הסטנדאפ והקומדיות הישראליות המובילות",
        "lang": "he",
        "isIsraeli": True,
        "logo": "https://images.frp1.ott.kaltura.com/Service.svc/GetImage/p/5031/entry_id/fc0d8109a18a4db3b585f154d32bd5b1/version/93/width/288/height/153/",
        "country": "IL",
        "countryHe": "ישראל",
        "src": {
            "by": "CloudFront CDN",
            "kind": "live",
            "host": "cloudfront.net",
            "web": "✅"
        }
    },
    {
        "id": "ch16_immergo_il",
        "name": "ערוץ 16 ישראל HD",
        "title": "ערוץ 16 ישראל HD",
        "category": "ערוצי ישראל - שידור חי",
        "categoryEn": "israel",
        "url": "https://ch16israel-cdn.encoders.immergo.tv/2/streamPlaylist.m3u8",
        "backups": [],
        "fallbackUrls": [],
        "badge": "HD",
        "desc": "ערוץ 16 לשידורים ישירים ותרבות ישראלית",
        "lang": "he",
        "isIsraeli": True,
        "logo": "https://ch16israel.vod.immergo.tv/ch16israel/uploaded/bfbd4781-67a7-437e-8f7e-898a5cbc4ab4.png",
        "country": "IL",
        "countryHe": "ישראל",
        "src": {
            "by": "Immergo Encoders",
            "kind": "live",
            "host": "immergo.tv",
            "web": "✅"
        }
    },
    {
        "id": "i24news_hebrew_hd",
        "name": "ערוץ i24NEWS עברית HD",
        "title": "ערוץ i24NEWS חדשות ישראל בעברית HD",
        "category": "ערוצי ישראל - שידור חי",
        "categoryEn": "israel",
        "url": "https://i24newshebrew-cdn.encoders.immergo.tv/master.m3u8",
        "backups": ["http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/2386/index.m3u8"],
        "fallbackUrls": ["http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/2386/index.m3u8"],
        "badge": "HD",
        "desc": "ערוץ החדשות והאקטואליה הבינלאומי i24NEWS בשידור חי בעברית 24/7",
        "lang": "he",
        "isIsraeli": True,
        "logo": "https://www.i24news.tv/images/favicon.png",
        "country": "IL",
        "countryHe": "ישראל",
        "src": {
            "by": "i24NEWS Encoders",
            "kind": "live",
            "host": "immergo.tv",
            "web": "✅"
        }
    },
    {
        "id": "big_brother_live_hd",
        "name": "האח הגדול שידור חי 24/7 HD",
        "title": "האח הגדול ישראל שידור חי 24/7 HD",
        "category": "ערוצי ישראל - שידור חי",
        "categoryEn": "israel",
        "url": "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/32128/index.m3u8",
        "backups": [],
        "fallbackUrls": [],
        "badge": "HD",
        "desc": "ערוץ השידור החי הרציף 24/7 של בית האח הגדול ישראל",
        "lang": "he",
        "isIsraeli": True,
        "logo": "https://upload.wikimedia.org/wikipedia/he/thumb/8/87/HaAh_HaGadol_logo.png/500px-HaAh_HaGadol_logo.png",
        "country": "IL",
        "countryHe": "ישראל",
        "src": {
            "by": "Ottrast Live Feed",
            "kind": "live",
            "host": "ottrast.com",
            "web": "✅"
        }
    },
    {
        "id": "sport5_live_akamai",
        "name": "ספורט 5 לייב פרימיום HD",
        "title": "ספורט 5 לייב פרימיום HD",
        "category": "ספורט (Sports)",
        "categoryEn": "sports",
        "url": "https://rgelive.akamaized.net/hls/live/2043095/live3/playlist.m3u8",
        "backups": ["http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/12252/index.m3u8"],
        "fallbackUrls": ["http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/12252/index.m3u8"],
        "badge": "HD",
        "desc": "ערוץ ספורט 5 לייב פרימיום בשידורי ספורט ישירים, ליגת האלופות, פורמולה 1 וכדורסל",
        "lang": "he",
        "isIsraeli": True,
        "logo": "https://i.imgur.com/L3RA9Lv.png",
        "country": "IL",
        "countryHe": "ישראל",
        "src": {
            "by": "Akamai Live Edge",
            "kind": "live",
            "host": "akamaized.net",
            "web": "✅"
        }
    }
]

added = 0
for nc in new_channels:
    if nc["id"] not in existing_ids:
        chs.append(nc)
        existing_ids.add(nc["id"])
        added += 1

print(f"Added {added} channels. Total now: {len(chs)}")

# Assign channel numbers
chs = channel_numbers.assign(chs)

with open(path, "w", encoding="utf-8", newline="\n") as f:
    json.dump(chs, f, ensure_ascii=False, indent=2)

with open(os.path.join(ROOT, "channels.js"), "w", encoding="utf-8", newline="\n") as f:
    f.write("window.CHANNELS_DATA = window.ITV_CHANNELS = " + json.dumps(chs, ensure_ascii=False, indent=1) + ";\n")

print(f"Saved {len(chs)} channels to channels.json and channels.js")

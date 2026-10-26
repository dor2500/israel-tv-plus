import json
import os

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
CHANNELS_FILE = os.path.join(ROOT, "channels.json")

NEW_SKY_CHANNELS = [
    {
        "id": "sky_news_uk",
        "name": "Sky News UK FHD",
        "title": "Sky News UK FHD",
        "category": "חדשות מהעולם (News)",
        "categoryEn": "news",
        "url": "https://linear901-oo-hls0-prd-gtm.delivery.skycdp.com/17501/sde-fast-skynews/master.m3u8",
        "logo": "assets/logos/sky_news.png",
        "badge": "FHD",
        "desc": "ערוץ החדשות הבינלאומי המוביל של רשת Sky מבריטניה בשידור חי 24/7 באיכות FHD",
        "lang": "en",
        "country": "GB",
        "countryHe": "בריטניה",
        "src": {
            "by": "Sky CDN (Official FAST)",
            "kind": "official",
            "host": "delivery.skycdp.com",
            "found": "שידור רשמי Sky",
            "web": "✅ מתנגן בכל הפלטפורמות"
        }
    },
    {
        "id": "sky_news_arabia",
        "name": "Sky News Arabia HD (סקיי ניוז ערבית)",
        "title": "Sky News Arabia HD",
        "category": "חדשות מהעולם (News)",
        "categoryEn": "news",
        "url": "https://live-stream.skynewsarabia.com/c-horizontal-channel/horizontal-stream/index.m3u8",
        "logo": "assets/logos/sky_news.png",
        "badge": "HD",
        "desc": "ערוץ החדשות סקיי ניוז בשפה הערבית של רשת Sky באיכות HD",
        "lang": "ar",
        "country": "AE",
        "countryHe": "איחוד האמירויות",
        "src": {
            "by": "Sky News Arabia CDN",
            "kind": "official",
            "host": "skynewsarabia.com",
            "found": "שידור רשמי Sky",
            "web": "✅ מתנגן בכל הפלטפורמות"
        }
    },
    {
        "id": "sky_news_arabia_live",
        "name": "Sky News Arabia ישיר HD (מקור 2)",
        "title": "Sky News Arabia Live HD",
        "category": "חדשות מהעולם (News)",
        "categoryEn": "news",
        "url": "https://stream.skynewsarabia.com/hls/sna.m3u8",
        "logo": "assets/logos/sky_news.png",
        "badge": "HD",
        "desc": "מקור שידור ישיר נוסף של ערוץ Sky News ערבית באיכות HD",
        "lang": "ar",
        "country": "AE",
        "countryHe": "איחוד האמירויות",
        "src": {
            "by": "Sky News Arabia Direct",
            "kind": "official",
            "host": "skynewsarabia.com",
            "found": "שידור רשמי Sky",
            "web": "✅ מתנגן בכל הפלטפורמות"
        }
    },
    {
        "id": "sky_racing_1",
        "name": "Sky Racing 1 HD (סקיי רייסינג ספורט)",
        "title": "Sky Racing 1 HD",
        "category": "ספורט (Sports)",
        "categoryEn": "sports",
        "url": "https://636ffd31f0e12.streamlock.net/RacingStream1/RacingStream1/chunklist_w127116790.m3u8",
        "logo": "assets/logos/sky_sports_racing.png",
        "badge": "HD",
        "desc": "ערוץ הספורט ומרוצי הסוסים והספורט המוטורי בשידור חי מבית Sky",
        "lang": "en",
        "country": "AU",
        "countryHe": "אוסטרליה",
        "src": {
            "by": "Sky Racing Streamlock",
            "kind": "official",
            "host": "streamlock.net",
            "found": "שידור רשמי Sky",
            "web": "✅ מתנגן בכל הפלטפורמות"
        }
    },
    {
        "id": "sky_news_au_extra1",
        "name": "Sky News Australia Extra 1 HD",
        "title": "Sky News Australia Extra 1 HD",
        "category": "חדשות מהעולם (News)",
        "categoryEn": "news",
        "url": "https://skynewsau-live.akamaized.net/hls/live/2002689/skynewsau-extra1/master.m3u8",
        "logo": "assets/logos/sky_news.png",
        "badge": "HD",
        "desc": "ערוץ שידורי החדשות והאירועים החיים Sky News Australia Extra 1 מבית סקיי",
        "lang": "en",
        "country": "AU",
        "countryHe": "אוסטרליה",
        "src": {
            "by": "Sky News Akamai",
            "kind": "official",
            "host": "akamaized.net",
            "found": "שידור רשמי Sky",
            "web": "✅ מתנגן בכל הפלטפורמות"
        }
    },
    {
        "id": "sky_news_au_extra2",
        "name": "Sky News Australia Extra 2 HD",
        "title": "Sky News Australia Extra 2 HD",
        "category": "חדשות מהעולם (News)",
        "categoryEn": "news",
        "url": "https://skynewsau-live.akamaized.net/hls/live/2002690/skynewsau-extra2/master.m3u8",
        "logo": "assets/logos/sky_news.png",
        "badge": "HD",
        "desc": "ערוץ שידורי החדשות והאירועים החיים Sky News Australia Extra 2 מבית סקיי",
        "lang": "en",
        "country": "AU",
        "countryHe": "אוסטרליה",
        "src": {
            "by": "Sky News Akamai",
            "kind": "official",
            "host": "akamaized.net",
            "found": "שידור רשמי Sky",
            "web": "✅ מתנגן בכל הפלטפורמות"
        }
    },
    {
        "id": "sky_news_au_extra3",
        "name": "Sky News Australia Extra 3 HD",
        "title": "Sky News Australia Extra 3 HD",
        "category": "חדשות מהעולם (News)",
        "categoryEn": "news",
        "url": "https://skynewsau-live.akamaized.net/hls/live/2002691/skynewsau-extra3/master.m3u8",
        "logo": "assets/logos/sky_news.png",
        "badge": "HD",
        "desc": "ערוץ שידורי החדשות והאירועים החיים Sky News Australia Extra 3 מבית סקיי",
        "lang": "en",
        "country": "AU",
        "countryHe": "אוסטרליה",
        "src": {
            "by": "Sky News Akamai",
            "kind": "official",
            "host": "akamaized.net",
            "found": "שידור רשמי Sky",
            "web": "✅ מתנגן בכל הפלטפורמות"
        }
    },
    {
        "id": "sky_folk_tv",
        "name": "Sky Folk TV HD (סקיי פולק)",
        "title": "Sky Folk TV HD",
        "category": "מוזיקה (Music TV)",
        "categoryEn": "music",
        "url": "https://skyfolk.mk/live.m3u8",
        "logo": "assets/logos/sky_news.png",
        "badge": "HD",
        "desc": "ערוץ שידורי המוזיקה והתרבות הבינלאומי של סקיי ב-HD",
        "lang": "en",
        "country": "MK",
        "countryHe": "מקדוניה",
        "src": {
            "by": "Sky Folk Live",
            "kind": "official",
            "host": "skyfolk.mk",
            "found": "שידור רשמי Sky",
            "web": "✅ מתנגן בכל הפלטפורמות"
        }
    }
]

def main():
    with open(CHANNELS_FILE, encoding="utf-8") as f:
        channels = json.load(f)

    existing_ids = {c["id"] for c in channels}
    added = 0
    for ch in NEW_SKY_CHANNELS:
        if ch["id"] not in existing_ids:
            channels.append(ch)
            existing_ids.add(ch["id"])
            added += 1
            print(f"Added {ch['name']}")

    # Sort channels by category or keep order and re-number sequentially
    for idx, ch in enumerate(channels, 1):
        ch["number"] = idx

    with open(CHANNELS_FILE, "w", encoding="utf-8") as f:
        json.dump(channels, f, ensure_ascii=False, indent=2)

    print(f"Total channels now: {len(channels)} (+{added} Sky channels added).")

if __name__ == "__main__":
    main()

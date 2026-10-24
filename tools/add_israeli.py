"""Israeli channels, radio stations and TV-service apps (Partner / Cellcom / STING / yes / HOT ...) for channels.json.

Every stream below is tested before it goes in: HLS master -> variant -> first segment, or the first audio bytes of
an Icecast / MP3 / AAC stream. Failing entries are reported and left out. Also cleans the curated list:
removes channels whose source only shows a promo loop, fixes categories, and gives every channel a clean category.

usage: python -I tools/add_israeli.py [--dry]
"""
import json
import os
import sys
import urllib.parse
from concurrent.futures import ThreadPoolExecutor

import requests

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
LOGOS = os.path.join(ROOT, "assets", "logos")
UA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0 Safari/537.36"
HEADERS = {"User-Agent": UA, "Origin": "https://dor2500.github.io", "Referer": "https://dor2500.github.io/"}

CAT_ISRAEL = "ערוצי ישראל - שידור חי"
CAT_APPS = "מנויים ואפליקציות (TV Apps)"
CAT_RADIO = "רדיו ישראלי (Israeli Radio)"
CAT_KIDS_HE = "ילדים ונוער (בעברית)"
CAT_KIDS_WORLD = "ילדים מהעולם (Kids World)"
CAT_MOVIES = "סרטים וסדרות (Movies & Series)"
CAT_MUSIC = "מוזיקה (Music TV)"
CAT_WORLD = "ערוצים בינלאומיים (World)"
CAT_NEWS = "חדשות מהעולם (News)"

REMOVE = {"arena4"}  # server gone (connection refused, 2026-10-23)
# The TV-service tiles (Partner / Cellcom / STING / yes / HOT ...) are no longer shown (user's choice, 2026-10-24):
# they only opened other apps. APPS below stays as a record; set INCLUDE_APPS to bring them back.
INCLUDE_APPS = False
# On 2026-10-23 these mcquack feeds showed an "OTT Club" promo loop and were dropped; on 2026-10-24 all three showed
# the real channel again (frames: football on Sport 1, cartoons on Ani and PLUSPLUS) – back in.
RESTORE = [
    {"id": "sport1", "name": "Sport 1 אוקראינה", "title": "Sport 1 (Ukraine)", "category": "ספורט (Sports)", "categoryEn": "sports",
     "url": "http://stream.mcquack.net/391/index.m3u8", "logo": "https://i.imgur.com/Zjb8awW.png", "badge": "HD",
     "desc": "שידורים ישירים של משחקי כדורגל, כדורסל וספורט עולמי", "lang": "uk", "iptv": "Sport1.ua"},
    {"id": "ani_kids", "name": "ערוץ אני אנימציה HD (Ani Kids)", "title": "ערוץ אני אנימציה HD", "category": "ילדים מהעולם (Kids World)",
     "categoryEn": "kids", "url": "http://stream.mcquack.net/306/index.m3u8", "logo": "assets/logos/ani_kids.svg", "badge": "HD",
     "desc": "ערוץ סדרות אנימציה לילדים (ברוסית)", "lang": "ru", "iptv": "Ani.ru"},
    {"id": "plusplus_kids", "name": "ערוץ האנימציה PLUSPLUS HD", "title": "ערוץ האנימציה PLUSPLUS HD", "category": "ילדים מהעולם (Kids World)",
     "categoryEn": "kids", "url": "http://stream.mcquack.net/339/index.m3u8", "logo": "assets/logos/plusplus.svg", "badge": "HD",
     "desc": "ערוץ הילדים והאנימציה - סדרות חינוכיות, הרפתקאות ומדע לילדים (באוקראינית)", "lang": "uk", "iptv": "PLUSPLUS.ua"},
]

ISRAELI_RADIO_IDS = {"galgalatz_live", "glz_live", "radio_103fm", "kol_barama_live", "radios_100fm", "radio_darom_live",
                     "kz_radio_live", "radio_2000_live", "kol_barama_music_live", "galgalatz_hits_live"}
KIDS_WORLD_IDS = {"kinokazka", "pixel_tv", "niki_kids", "kapitan_fantastika", "okids"}
FIX = {
    "yam_tichoni_hd": {"category": CAT_MOVIES, "categoryEn": "movies", "lang": "he",
                       "desc": "הערוץ הים תיכוני - סדרות ודרמות ישראליות וים תיכוניות"},
    "knesset": {"url": "http://stream.mcquack.net/48/index.m3u8"},  # the https gostreaming feed is gone (404)
    "channel9": {"desc": "ערוץ 9 - ערוץ ישראלי בשפה הרוסית: חדשות, אקטואליה ותוכניות"},
    "galgalatz_hits_live": {"url": "https://glzwizzlv.bynetcdn.com/glglz_hits_mp3"},  # was http (blocked on the site)
    # Kan's own feed (kankids.org.il/kids-live): https + CORS, so it plays on the site too
    "kan_hinuchit": {"url": "https://kancdn.medonecdn.net/livehls/oil/kancdn-live/live/kan_edu/live.livx/playlist.m3u8"},
    "bbc_earth": {"url": "https://amg00793-amg00793c6-xumo-us-2669.playouts.now.amagi.tv/BBCStudios-BBCEarthA-hls/playlist.m3u8"},
    # titles are searched: these named other channels than the one the stream shows
    "yam_tichoni_hd": {"title": "הערוץ הים תיכוני (Yam Tichoni)"},
    "extreme_sports_hd": {"title": "ערוץ אקסטרים (Extreme Sports)"},
    "ducktv_plus_hd": {"title": "דאק טי וי פלוס (ducktv PLUS)"},
    "babyfirst_hd": {"title": "בייבי פירסט (BabyFirst)"},
    "cartoons_short_hd": {"title": "אנימציה קצרצרה (Cartoons Short)"},
    "lego_channel_hd": {"title": "ערוץ לגו (Lego Channel)"},
    "tiji_kids_hd": {"title": "טיז'י (TiJi)"},
    "jimjam_kids_hd": {"title": "ג'ים ג'אם (JimJam)"},
    "fresh_kids_hd": {"title": "אנימציה וילדים (Fresh Kids)"},
    "cartoons_90s": {"title": "קלאסיקות מצוירות (Cartoons 90s)"},
    "cartoons_big_hd": {"title": "סרטי אנימציה (Cartoons Big)"},
    # the new categories (2026-10-24) open with the curated channels that belong in them
    "comedy_channel": {"category": "קומדיה (Comedy)", "categoryEn": "comedy"},
    "vacation_channel": {"category": "טיולים והרפתקאות (Travel)", "categoryEn": "travel"},
    "travel_channel": {"category": "טיולים והרפתקאות (Travel)", "categoryEn": "travel"},
    "viju_explore": {"category": "מדע ולמידה (Science)", "categoryEn": "science"},
}
# https copies for the web site of channels whose main source is http (browsers block http on an https page).
# Each played in Chromium and its picture was checked against the channel (2026-10-23); the apps keep `url`.
WEB_URLS = {
    "kinohit": "https://stream8.cinerama.uz/1055/tracks-v1a1/mono.m3u8",
    "action_tv1000": "https://stream8.cinerama.uz/1225/tracks-v1a1/mono.m3u8",
    "viju_sport": "https://stream8.cinerama.uz/1229/tracks-v1a1/mono.m3u8",
    "nick_jr_he": "https://stream8.cinerama.uz/1501/playlist.m3u8",
    "gulli_kids_hd": "https://stream8.cinerama.uz/1445/tracks-v1a1/mono.m3u8",
    "tiji_kids_hd": "https://stream8.cinerama.uz/1441/tracks-v1a1/mono.m3u8",
    "pixel_tv": "https://cdn15.live-tv.cloud/ua_infinitas_tv/pixel-abr/playlist.m3u8",
    "kapitan_fantastika": "https://stream8.cinerama.uz/1406/tracks-v1a1/mono.m3u8",
    "solntse_kids": "https://tv.mediacdn.ru/live/solntse/playlist.m3u8",
    "mult_kids": "https://stream8.cinerama.uz/1246/tracks-v1a1/mono.m3u8",
    "carousel_kids": "https://stream.smotrim.ru/hls2/karusel/playlist_3.m3u8",
    "fairy_tales_kids": "https://stream8.cinerama.uz/1270/tracks-v1a1/mono.m3u8",
    "history_365": "https://stream8.cinerama.uz/1242/tracks-v1a1/mono.m3u8",
}
# Pluto TV via jmp2.uk: the redirect has no CORS header, so browsers can't follow it (the apps can)
WEB_BLOCKED = {"rewind_80s", "cinema_70s", "replay_00s"}
# older radio entries: clean names + logos in the same style as the new ones
OLD_RADIO = {
    "galgalatz_live": ("גלגלצ", "גלגלצ", "91.8FM", "#780000", "#ef4444"),
    "galgalatz_hits_live": ("גלגלצ להיטים", "גלגלצ", "להיטים", "#780000", "#ef4444"),
    "glz_live": ("גלי צה\"ל", "גלצ", "גלי צה\"ל", "#1f2937", "#a3e635"),
    "radio_103fm": ("103FM", "103FM", "רדיו ללא הפסקה", "#0c4a6e", "#38bdf8"),
    "radios_100fm": ("רדיוס 100FM", "100FM", "רדיוס", "#14532d", "#4ade80"),
    "radio_darom_live": ("רדיו דרום 101.5FM", "דרום", "101.5FM", "#7c2d12", "#fb923c"),
    "kz_radio_live": ("רדיו הקצה KZ", "KZ", "רדיו הקצה", "#111111", "#e5e7eb"),
    "radio_2000_live": ("רדיו 2000", "2000", "רדיו", "#1e3a8a", "#fde047"),
    "kol_barama_live": ("קול ברמה", "קול ברמה", "104.3FM", "#3b0764", "#c084fc"),
    "kol_barama_music_live": ("קול ברמה מוזיקה (קול פליי)", "קול פליי", "קול ברמה מוזיקה", "#3b0764", "#f0abfc"),
}
RADIO_FIRST = ["kan_bet", "kan_gimel", "kan_88", "kan_tarbut", "kan_moreshet", "kan_reka", "kan_kol_hamusica",
               "galgalatz_live", "glz_live", "galgalatz_hits_live", "eco99fm", "radio_103fm", "radios_100fm",
               "radio_102fm", "kz_radio_live"]

# ----------------------------------------------------------------------------- new TV channels
TV = [
    dict(id="i24news_en", name="i24NEWS English", title="i24NEWS English (Israel)", category=CAT_ISRAEL, categoryEn="israel",
         url="https://i24newsenglish-cdn.encoders.immergo.tv/master.m3u8", lang="en", badge="FHD",
         desc="ערוץ החדשות הבינלאומי של i24NEWS מישראל - באנגלית", logo_style=("i24NEWS", "English", "#0f172a", "#38bdf8")),
    dict(id="zoom_kids", name="ZOOM ילדים ונוער", title="ערוץ זום (Zoom Israel)", category=CAT_KIDS_HE, categoryEn="kids",
         url="http://stream.mcquack.net/377/index.m3u8", lang="he", badge="SD", iptv="Zoom.il",
         desc="ערוץ הילדים והנוער הישראלי - סדרות נוער, דרמות וקומדיות בעברית",
         logo_style=("ZOOM", "ילדים ונוער", "#831843", "#f472b6")),
    # 2026-10-24: Israeli / Jewish channels with an open stream that iptv-org lists outside the IL country code
    dict(id="iltv_fr", name="ILTV ישראל בצרפתית", title="ILTV - Israël en français", category=CAT_ISRAEL, categoryEn="israel",
         url="https://live.creacast.com/iltv/smil:iltv.smil/playlist.m3u8", lang="fr", badge="HD", iptv="ILTV.fr",
         desc="ערוץ ישראלי בצרפתית - חדשות, אקטואליה, תרבות ואוכל מישראל",
         logo_style=("ILTV", "ישראל בצרפתית", "#1e3a8a", "#93c5fd")),
    dict(id="jltv", name="JLTV טלוויזיה יהודית", title="Jewish Life Television (JLTV)", category="מסורת וקהילה",
         categoryEn="community", url="https://jlt-jltv-xumo.otteravision.com/jlt/jltv/jltv.m3u8", lang="en", badge="HD",
         iptv="JewishLifeTelevision.us", desc="ערוץ התרבות היהודית מארה\"ב - ישראל, אוכל, היסטוריה, מוזיקה וראיונות (באנגלית)",
         logo_style=("JLTV", "Jewish Life", "#0c4a6e", "#7dd3fc")),
]

# ----------------------------------------------------------------------------- Israeli radio
# (id, name, url, desc, logo big text, logo small text, bg, accent)
KAN = "https://playerservices.streamtheworld.com/api/livestream-redirect/{}.mp3"  # Kan's own HLS radio playlists come back empty
RADIO = [
    ("kan_bet", "כאן ב - רשת ב", KAN.format("KAN_BET"), "רשת החדשות והאקטואליה של תאגיד השידור", "כאן ב", "חדשות ואקטואליה", "#003049", "#669bbc"),
    ("kan_gimel", "כאן גימל", KAN.format("KAN_GIMMEL"), "רק מוזיקה ישראלית - מכל התקופות", "כאן גימל", "מוזיקה ישראלית", "#003049", "#669bbc"),
    ("kan_88", "כאן 88", KAN.format("KAN_88"), "רוק, אינדי, ג'אז ומוזיקה איכותית מהעולם", "כאן 88", "מוזיקה איכותית", "#003049", "#669bbc"),
    ("kan_tarbut", "כאן תרבות", KAN.format("KAN_TARBUT"), "תרבות, ספרות, הגות ושיחות", "כאן תרבות", "תרבות ושיח", "#003049", "#669bbc"),
    ("kan_moreshet", "כאן מורשת", KAN.format("KAN_MORESHET"), "יהדות, מסורת, פיוט וחזנות", "כאן מורשת", "יהדות ומסורת", "#003049", "#669bbc"),
    ("kan_reka", "כאן רקע", KAN.format("KAN_REKA"), "רדיו העלייה - שידורים בשפות רבות", "כאן רקע", "רדיו העלייה", "#003049", "#669bbc"),
    ("kan_kol_hamusica", "כאן קול המוזיקה", KAN.format("KAN_KOL_HAMUSICA"),
     "מוזיקה קלאסית, אופרה וג'אז", "קול המוזיקה", "מוזיקה קלאסית", "#003049", "#669bbc"),
    ("eco99fm", "eco99fm", "https://eco-live.mediacast.co.il/99fm_aac", "התחנה המובילה בישראל - להיטים ותוכניות אישיות", "eco99", "fm 99", "#064e3b", "#34d399"),
    ("radio_102fm", "102FM רדיו תל אביב", "https://102.livecdn.biz/102fm_aac", "רדיו תל אביב - מוזיקה, שיח ואקטואליה", "102FM", "רדיו תל אביב", "#1e3a8a", "#60a5fa"),
    ("radio_1045fm", "104.5FM רדיו צפון", "https://radio.streamgates.net/stream/1045fm", "הרדיו האזורי של הצפון", "104.5FM", "רדיו צפון", "#14532d", "#4ade80"),
    ("radio_101fm", "101FM רדיו ירושלים", "https://radio.streamgates.net/stream/101fm", "הרדיו האזורי של ירושלים", "101FM", "רדיו ירושלים", "#713f12", "#facc15"),
    ("radio_haifa", "רדיו חיפה 107.5FM", "https://1075.livecdn.biz/radiohaifa", "הרדיו האזורי של חיפה והקריות", "רדיו חיפה", "107.5FM", "#0c4a6e", "#38bdf8"),
    ("radio_90fm", "90FM אמצע הדרך", "https://cdn.cybercdn.live/Emtza_Haderech/Live_Audio/icecast.audio", "רדיו אמצע הדרך - מוזיקה ישראלית", "90FM", "אמצע הדרך", "#4c1d95", "#a78bfa"),
    ("radio_darom_97", "רדיו דרום 97FM", "https://cdn.cybercdn.live/Darom_97FM/Live/icecast.audio", "רדיו דרום - הרדיו של הנגב", "דרום 97", "רדיו הנגב", "#7c2d12", "#fb923c"),
    ("radio_hatahana", "רדיו התחנה 101.5", "https://cdn.cybercdn.live/Hatahana_1015/Live_Audio/icecast.audio", "נותנים למוזיקה לדבר", "התחנה", "101.5FM", "#831843", "#f472b6"),
    ("galei_israel", "גלי ישראל 94FM", "https://cdn.cybercdn.live/Galei_Israel/Live/icecast.audio", "רדיו גלי ישראל - אקטואליה, שיח ומוזיקה", "גלי ישראל", "94FM", "#1e3a8a", "#93c5fd"),
    ("kol_chai", "קול חי 93FM", "https://media2.93fm.co.il/live-new", "רדיו קול חי - אקטואליה ותוכן יהודי", "קול חי", "93FM", "#7f1d1d", "#fca5a5"),
    ("kol_chai_music", "קול חי מיוזיק", "https://media2.93fm.co.il/livemusic", "מוזיקה יהודית וחסידית ברצף", "קול חי", "מיוזיק", "#7f1d1d", "#fcd34d"),
    ("kol_rega", "קול רגע 96FM", "https://cdn.cybercdn.live/Kol_Rega/Live_Audio/icecast.audio", "הרדיו של הצפון והעמקים", "קול רגע", "96FM", "#134e4a", "#5eead4"),
    ("lev_hamedina", "לב המדינה 91FM", "https://cdn.cybercdn.live/Lev_Hamedina/Audio/icecast.audio", "רדיו לב המדינה - השרון והמרכז", "לב המדינה", "91FM", "#9f1239", "#fda4af"),
    ("radios_nostalgia", "רדיוס נוסטלגי 96.3", "https://cdna.streamgates.net/radios-audio/Nostalgia_963fm/icecast.audio", "להיטי נוסטלגיה ישראליים ולועזיים", "נוסטלגי", "96.3FM", "#312e81", "#c4b5fd"),
    ("kol_hatzafon", "קול הצפון", "https://live.ecast.co.il/stream/khz", "רדיו קול הצפון", "קול הצפון", "רדיו", "#14532d", "#86efac"),
    ("glglz_rock", "גלגלצ רוק", "https://glzwizzlv.bynetcdn.com/glglz_rock_mp3", "ערוץ הרוק של גלגלצ", "גלגלצ", "רוק", "#780000", "#c1121f"),
    ("glglz_decades", "גלגלצ עשורים", "https://glzwizzlv.bynetcdn.com/glglz_alt_mp3", "להיטים מכל העשורים", "גלגלצ", "עשורים", "#780000", "#c1121f"),
    ("glglz_weekend", "גלגלצ רגוע", "https://glzwizzlv.bynetcdn.com/glglz_sofshavua_mp3", "מוזיקה רגועה לסוף השבוע", "גלגלצ", "רגוע", "#780000", "#c1121f"),
    ("radios_mizrahit", "רדיוס 100% מזרחית", "https://gb25.streamgates.net/radios-audio/100Mizrachit/icecast.audio", "מוזיקה מזרחית ים תיכונית ברצף", "100%", "מזרחית", "#7c2d12", "#fdba74"),
    ("radios_yehudi", "רדיוס 100% יהודי", "https://gb25.streamgates.net/radios-audio/100yehudi/playlist.m3u8", "מוזיקה יהודית וחסידית", "100%", "יהודי", "#1e3a8a", "#bfdbfe"),
    ("radios_hits", "רדיוס 100% להיטים", "https://gb25.streamgates.net/radios-audio/100Hits/playlist.m3u8", "הלהיטים הגדולים ברצף", "100%", "להיטים", "#581c87", "#e9d5ff"),
    ("radios_80s", "רדיוס 100% שנות ה-80", "https://gb25.streamgates.net/radios-audio/10080s/icecast.audio", "להיטי שנות ה-80", "100%", "שנות ה-80", "#831843", "#fbcfe8"),
    ("radios_90s", "רדיוס 100% שנות ה-90", "https://gb25.streamgates.net/radios-audio/10090s/icecast.audio", "להיטי שנות ה-90", "100%", "שנות ה-90", "#164e63", "#a5f3fc"),
    ("radios_kids", "רדיוס 100% ילדים", "https://gb25.streamgates.net/radios-audio/100Kids/icecast.audio", "שירי ילדים ברצף", "100%", "ילדים", "#365314", "#d9f99d"),
    ("radios_relax", "רדיוס 100% רגוע", "https://gb25.streamgates.net/radios-audio/100Relax/icecast.audio", "מוזיקה רגועה", "100%", "רגוע", "#134e4a", "#99f6e4"),
    ("a_shams", "א-שמס A-Shams", "https://cdna.streamgates.net/Ashams/Live/icecast.audio", "רדיו א-שמס - הרדיו הערבי בישראל", "A-Shams", "الشمس", "#78350f", "#fbbf24"),
    ("radio_nas", "רדיו נאס Radio Nas", "https://cdna.streamgates.net/RadioNas/Live-Audio/icecast.audio", "רדיו נאס - מוזיקה ושיח בערבית", "Radio Nas", "راديو الناس", "#7f1d1d", "#fecaca"),
    ("pervoe_radio", "הרדיו הראשון 89.1FM (רוסית)", "https://cdn.cybercdn.live/Pervoia/Audio/icecast.audio", "Первое радио - רדיו ישראלי בשפה הרוסית", "89.1FM", "Первое радио", "#1e293b", "#f87171"),
    ("radio_breslev", "רדיו ברסלב", "https://cast.breslevforyou.co.il/listen/radiobreslev/radio.mp3", "שיעורים, ניגונים ומוזיקה חסידית", "ברסלב", "רדיו", "#1e3a8a", "#fde68a"),
    ("radio_nahariya", "רדיו נהריה", "https://cast.radionahariya.com:8443/main.mp3", "רדיו מקומי מנהריה", "נהריה", "רדיו", "#0c4a6e", "#7dd3fc"),
    # 2026-10-24: more Israeli stations from radio-browser.info (each one tested below before it goes in)
    ("radios_workout", "רדיוס 100% אימון", "https://gb25.streamgates.net/radios-audio/100Workout/icecast.audio", "מוזיקה קצבית לאימון ולספורט", "100%", "אימון", "#7c2d12", "#fdba74"),
    ("radios_top40", "רדיוס 100% טופ 40", "https://gb25.streamgates.net/radios-audio/100Top40/icecast.audio", "40 הלהיטים הגדולים עכשיו", "100%", "טופ 40", "#831843", "#f9a8d4"),
    ("radios_movies", "רדיוס 100% סרטים", "https://gb25.streamgates.net/radios-audio/100Movies/icecast.audio", "פסקולים ומוזיקה מהסרטים", "100%", "סרטים", "#312e81", "#a5b4fc"),
    ("radios_oldies", "רדיוס 100% אולדיז", "https://gb25.streamgates.net/radios-audio/100Oldies/icecast.audio", "להיטי הזהב של שנות ה-50 וה-60", "100%", "אולדיז", "#78350f", "#fcd34d"),
    ("radios_retro", "רדיוס 100% רטרו", "https://gb25.streamgates.net/radios-audio/100Retro/icecast.audio", "להיטי רטרו", "100%", "רטרו", "#4c1d95", "#d8b4fe"),
    ("radios_chillout", "רדיוס 100% צ'ילאאוט", "https://gb25.streamgates.net/radios-audio/100Chillout/playlist.m3u8", "מוזיקה רגועה ואלקטרונית", "100%", "צ'יל", "#134e4a", "#99f6e4"),
    ("radios_country", "רדיוס 100% קאנטרי", "https://gb25.streamgates.net/radios-audio/100Country/playlist.m3u8", "קאנטרי אמריקאי", "100%", "קאנטרי", "#713f12", "#fde68a"),
    ("radios_kpop", "רדיוס 100% K-Pop", "https://gb25.streamgates.net/radios-audio/100kpop/icecast.audio", "קיי-פופ קוריאני", "100%", "K-Pop", "#9d174d", "#fbcfe8"),
    ("radios_gamer", "רדיוס 100% גיימר", "https://gb25.streamgates.net/radios-audio/100Gamer/icecast.audio", "מוזיקה למשחקים", "100%", "גיימר", "#1e1b4b", "#a5f3fc"),
    ("noshmim_mizrahit", "נושמים מזרחית", "http://www.rs.mizrahit.fm:7777/", "מוזיקה מזרחית ים תיכונית 24/7", "נושמים", "מזרחית", "#7c2d12", "#fed7aa"),
    ("radio_sahar", "רדיו סהר", "https://live.ecast.co.il/stream/sahar/stream", "רוק, אינדי והיפ הופ ישראלי", "סהר", "רדיו", "#1f2937", "#fca5a5"),
    ("israeli_voice", "Israeli Voice Radio", "https://icecast.live/ivr", "מוזיקה ישראלית ברצף", "IVR", "קול ישראלי", "#1e3a8a", "#93c5fd"),
    ("joint_blues", "ג'וינט רדיו בלוז רוק", "https://jointil.com/stream-blues", "בלוז ורוק קלאסי – תחנה ישראלית", "joint", "בלוז", "#0f172a", "#7dd3fc"),
    ("joint_reggae", "ג'וינט רדיו רגאיי", "https://jointil.com/stream-reggae", "רגאיי ודאב – תחנה ישראלית", "joint", "רגאיי", "#14532d", "#fde047"),
    ("joint_beat", "ג'וינט רדיו טראנס", "https://jointil.com/stream-beat", "טראנס ואלקטרוני – תחנה ישראלית", "joint", "טראנס", "#3b0764", "#e879f9"),
    ("kabbalah_radio", "קבלה לעם רדיו", "http://icecast.kab.tv/live1-heb-574bcfd5.mp3", "שיעורי קבלה בעברית", "קבלה", "לעם", "#1e3a8a", "#bfdbfe"),
    ("breslev_torah", "ברסלב: תורה וזימרה", "https://cast.breslevforyou.co.il/listen/music-toravezimra/radio.mp3", "שיעורים ונגינה חסידית", "ברסלב", "תורה וזימרה", "#1e3a8a", "#fde68a"),
    ("radio_france_israel", "Radio France Israël", "https://stream10.vestaradio.com/Radio-France-Israel", "הרדיו של יוצאי צרפת בישראל", "RFI", "France Israël", "#1e3a8a", "#fca5a5"),
]

# ----------------------------------------------------------------------------- TV services / official apps
# Android: opens the installed app (TV / phone package), otherwise its Play Store page. Elsewhere: the official site.
APPS = [
    ("app_partner_tv", "Partner tv+", "il.co.partnertv.atv", "il.co.partnertv", "https://www.partner.co.il/tv/",
     "שירות הטלוויזיה של פרטנר - כל הערוצים, VOD וספורט (למנויים)", "partner", "tv+", "#0b0b0b", "#00c4cc"),
    ("app_cellcom_tv", "סלקום \u2066tv+\u2069", "com.cellcom.cellcom_tv", "com.cellcom.cellcomtv", "https://cellcom.co.il/tv/",
     "שירות הטלוויזיה של סלקום - ערוצים, VOD ונטפליקס (למנויים)", "cellcom", "tv+", "#5b2d90", "#c4b5fd"),
    ("app_sting_tv", "STING+", "il.co.stingtv.atv", "il.co.stingtv", "https://www.stingtv.co.il/",
     "STING+ של yes - ערוצים, סדרות וסרטים בסטרימינג (למנויים)", "STING+", "yes", "#111111", "#ff3d6e"),
    ("app_yes_plus", "yes+", "il.co.yes.yesgo", "il.co.yes.yesgo", "https://www.yes.co.il/",
     "אפליקציית yes+ - ערוצי yes, VOD וספורט (למנויים)", "yes+", "yes", "#0a2540", "#38bdf8"),
    ("app_hot_next", "HOT NEXT TV", "il.net.hot.nexttv", "il.net.hot.nexttv", "https://www.hot.net.il/",
     "אפליקציית HOT - ערוצי HOT, VOD וספורט (למנויים)", "HOT", "NEXT TV", "#c8102e", "#ffffff"),
    ("app_kan", "כאן - אפליקציה", "com.applicaster.il.ch1", "com.applicaster.il.ch1", "https://www.kan.org.il/",
     "האפליקציה הרשמית של תאגיד השידור - שידור חי ו-VOD חינם", "כאן", "אפליקציה", "#003049", "#669bbc"),
    ("app_kan_kids", "כאן ילדים", "com.channel23", "com.channel23", "https://www.kankids.org.il/",
     "האפליקציה הרשמית של כאן חינוכית לילדים", "כאן", "ילדים", "#f97316", "#fff7ed"),
    ("app_mako", "12+ mako", "com.keshet.mako.VODAndroidTV", "com.goldtouch.mako", "https://www.mako.co.il/mako-vod-live-tv",
     "האפליקציה הרשמית של קשת 12 - שידור חי ו-VOD", "12+", "mako", "#111827", "#f43f5e"),
    ("app_13", "רשת \u206613+\u2069", "com.applicaster.iReshet", "com.applicaster.iReshet", "https://13tv.co.il/live/",
     "האפליקציה הרשמית של רשת 13 - שידור חי וכל התוכניות", "13+", "רשת", "#111827", "#ec4899"),
    ("app_14", "14 LIVE", "com.channel.fourteen.univ", "com.channel.fourteen.univ", "https://www.c14.co.il/live",
     "האפליקציה הרשמית של ערוץ 14", "14", "LIVE", "#0b3d91", "#ef4444"),
    ("app_sport5", "ספורט 5", "com.rge.sport5", "com.rge.sport5", "https://www.sport5.co.il/",
     "האפליקציה הרשמית של ספורט 5 - תקצירים, שידורים וחדשות ספורט", "ספורט 5", "sport5", "#0b0b0b", "#22c55e"),
]


def get(url, n):
    r = requests.get(url, headers=HEADERS, timeout=15, stream=True, allow_redirects=True)
    data = r.raw.read(n, decode_content=True)
    r.close()
    return r, data


def probe_hls(url):
    r, data = get(url, 400000)
    if r.status_code != 200 or not data.lstrip().startswith(b"#EXTM3U"):
        return f"master {r.status_code}"
    cur, text = r.url, data.decode("utf-8", "replace")
    for _ in range(3):
        lines = [l.strip() for l in text.splitlines() if l.strip() and not l.startswith("#")]
        if not lines:
            return "empty playlist"
        nxt = urllib.parse.urljoin(cur, lines[0])
        if "#EXT-X-STREAM-INF" in text:
            r, data = get(nxt, 400000)
            if r.status_code != 200:
                return f"variant {r.status_code}"
            cur, text = r.url, data.decode("utf-8", "replace")
            continue
        r, data = get(nxt, 4096)
        return None if r.status_code in (200, 206) and len(data) >= 188 else f"segment {r.status_code}"
    return "playlist loop"


def probe(url):
    """None when the stream really delivers media, else the reason."""
    try:
        if ".m3u8" in url:
            return probe_hls(url)
        r, data = get(url, 16384)
        ct = r.headers.get("Content-Type", "")
        if r.status_code != 200:
            return f"http {r.status_code}"
        if len(data) < 8000:
            return f"only {len(data)} bytes"
        if "html" in ct or data.lstrip()[:1] == b"<":
            return f"not audio ({ct})"
        return None
    except Exception as e:  # noqa: BLE001
        return type(e).__name__


def esc(s):
    return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")


def fit(text, room, biggest):
    """Font size that keeps `text` inside `room` px (bold Heebo / Arial is about 0.62 em per character)."""
    return int(min(biggest, room / (0.62 * max(len(text), 1))))


def logo_svg(big, small, bg, accent, icon):
    """300x120 tile, brand text centred; radio stations get a small antenna badge on the right (RTL start)."""
    font = "Segoe UI, Heebo, Arial, sans-serif"  # weight 900 makes Windows fall back to a serif Hebrew font
    if icon == "radio":
        cx, room = 128, 196
        badge = (f'<circle cx="256" cy="60" r="26" fill="{accent}" fill-opacity="0.2" stroke="{accent}" stroke-width="2"/>'
                 f'<g fill="none" stroke="{accent}" stroke-width="3.2" stroke-linecap="round">'
                 f'<path d="M246 52a14 14 0 0 1 20 0"/><path d="M240 46a22 22 0 0 1 32 0"/></g>'
                 f'<circle cx="256" cy="64" r="5" fill="{accent}"/><rect x="254" y="68" width="4" height="12" rx="2" fill="{accent}"/>')
    else:
        cx, room, badge = 150, 264, f'<rect x="110" y="104" width="80" height="5" rx="2.5" fill="{accent}"/>'
    return f"""<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 300 120" width="300" height="120">
  <defs><linearGradient id="g" x1="0" y1="0" x2="1" y2="1"><stop offset="0" stop-color="{bg}"/><stop offset="1" stop-color="#05070d"/></linearGradient></defs>
  <rect x="1" y="1" width="298" height="118" rx="22" fill="url(#g)" stroke="{accent}" stroke-opacity="0.7" stroke-width="2"/>
  {badge}
  <text x="{cx}" y="64" font-family="{font}" font-size="{fit(big, room, 46)}" font-weight="700" fill="#ffffff" text-anchor="middle">{esc(big)}</text>
  <text x="{cx}" y="93" font-family="{font}" font-size="{fit(small, room, 19)}" font-weight="700" fill="{accent}" text-anchor="middle">{esc(small)}</text>
</svg>
"""


def write_logo(cid, big, small, bg, accent, icon):
    path = os.path.join(LOGOS, cid + ".svg")
    with open(path, "w", encoding="utf-8") as f:
        f.write(logo_svg(big, small, bg, accent, icon))
    return "assets/logos/" + cid + ".svg"


def main():
    dry = "--dry" in sys.argv
    with open(os.path.join(ROOT, "channels.json"), encoding="utf-8") as f:
        chans = json.load(f)

    out = []
    for c in chans:
        if c["id"] in REMOVE or (c.get("kind") == "app" and not INCLUDE_APPS):
            continue
        c.update(FIX.get(c["id"], {}))
        if c["id"] in ISRAELI_RADIO_IDS:
            c["category"], c["categoryEn"], c["kind"] = CAT_RADIO, "radio", "radio"
        elif c["id"] in KIDS_WORLD_IDS:
            c["category"], c["categoryEn"] = CAT_KIDS_WORLD, "kids"
        elif c["category"].startswith("מוזיקה"):
            c["category"], c["categoryEn"] = CAT_MUSIC, "music"
        if c.get("lang") == "music" and ".m3u8" not in c["url"]:
            c["kind"] = "radio"
        if c["id"] in WEB_URLS:
            c["webUrl"] = WEB_URLS[c["id"]]
        elif c.get("webUrl") == c["url"]:  # main source became the https one
            del c["webUrl"]
        if c["id"] in WEB_BLOCKED:
            c["webOk"] = False
        if c["id"] in OLD_RADIO:
            name, big, small, bg, accent = OLD_RADIO[c["id"]]
            c["name"] = c["title"] = name
            c["badge"] = "LIVE"
            if not dry:
                c["logo"] = write_logo(c["id"], big, small, bg, accent, "radio")
        out.append(c)
    have = {c["id"] for c in out}
    have_urls = {c["url"] for c in out}
    if not dry:  # logos are (re)drawn every run, so design changes reach entries added earlier too
        for t in TV:
            write_logo(t["id"], *t["logo_style"], "tv")
        for cid, _, _, _, big, small, bg, accent in RADIO:
            write_logo(cid, big, small, bg, accent, "radio")
        for cid, *_, big, small, bg, accent in APPS:
            write_logo(cid, big, small, bg, accent, "app")

    todo = []
    for t in TV:
        if t["id"] not in have and t["url"] not in have_urls:
            todo.append(("tv", t))
    for r in RADIO:
        if r[0] not in have and r[2] not in have_urls:
            todo.append(("radio", r))
    with ThreadPoolExecutor(16) as ex:
        results = list(ex.map(lambda x: (x, probe(x[1]["url"] if x[0] == "tv" else x[1][2])), todo))

    added, failed = [], []
    for (kind, item), err in results:
        if err:
            failed.append((item["id"] if kind == "tv" else item[0], err))
            continue
        if kind == "tv":
            t = dict(item)
            big, small, bg, accent = t.pop("logo_style")
            t["logo"] = write_logo(t["id"], big, small, bg, accent, "tv") if not dry else ""
            added.append(t)
        else:
            cid, name, url, desc, big, small, bg, accent = item
            added.append({"id": cid, "name": name, "title": name, "category": CAT_RADIO, "categoryEn": "radio",
                          "kind": "radio", "url": url, "badge": "LIVE", "desc": desc, "lang": "music",
                          "logo": write_logo(cid, big, small, bg, accent, "radio") if not dry else ""})
    for r in RESTORE:
        if r["id"] not in have and r["url"] not in have_urls:
            added.append(dict(r))
    for cid, name, tv_pkg, pkg, web, desc, big, small, bg, accent in APPS:
        if cid in have or not INCLUDE_APPS:
            continue
        added.append({"id": cid, "name": name, "title": name, "category": CAT_APPS, "categoryEn": "apps", "kind": "app",
                      "url": web, "site": web, "android": pkg, "androidTv": tv_pkg, "badge": "APP", "desc": desc,
                      "lang": "he", "logo": write_logo(cid, big, small, bg, accent, "app") if not dry else ""})

    # numbering follows the category order (Israeli first); inside a category the existing order stays,
    # except the radio list, which starts with Kan and the big national stations
    cats = [CAT_ISRAEL, CAT_KIDS_HE, CAT_RADIO, CAT_APPS, "ספורט (Sports)", CAT_NEWS, CAT_MOVIES, "קומדיה (Comedy)",
            CAT_KIDS_WORLD, CAT_MUSIC, "טבע ודוקו (Documentary & Nature)", "טיולים והרפתקאות (Travel)",
            "מדע ולמידה (Science)", "לייף סטייל ובידור", "מסורת וקהילה", CAT_WORLD]
    allc = out + added
    pos = {c["id"]: i for i, c in enumerate(allc)}
    first = {cid: i for i, cid in enumerate(RADIO_FIRST)}
    allc.sort(key=lambda c: (cats.index(c["category"]) if c["category"] in cats else len(cats),
                             first.get(c["id"], len(first)), pos[c["id"]]))
    for i, c in enumerate(allc):
        c["number"] = i + 1
    print(f"{len(chans)} -> kept {len(out)}, added {len(added)} = {len(allc)}")
    for cid, err in failed:
        print("  FAILED", cid, err)
    if dry:
        return
    with open(os.path.join(ROOT, "channels.json"), "w", encoding="utf-8") as f:
        json.dump(allc, f, ensure_ascii=False, indent=2)
    with open(os.path.join(ROOT, "channels.js"), "w", encoding="utf-8") as f:
        f.write("window.CHANNELS_DATA = " + json.dumps(allc, ensure_ascii=False, indent=1) + ";\n")


if __name__ == "__main__":
    main()

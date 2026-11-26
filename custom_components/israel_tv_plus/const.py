"""Constants for the Israel TV+ Home Assistant integration."""

DOMAIN = "israel_tv_plus"

REMOTE_CHANNELS_URL = "https://dor2500.github.io/israel-tv-plus/channels.json"
REMOTE_VOD_URL = "https://dor2500.github.io/israel-tv-plus/vod_data.json"
REMOTE_BASE_URL = "https://dor2500.github.io/israel-tv-plus/"
MAKO_ENTITLEMENT_URL = "https://mass.mako.co.il/ClicksStatistics/entitlementsServicesV2.jsp"

DEFAULT_USER_AGENT = (
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
    "(KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36 IsraelTVPlus/HomeAssistant"
)

# Core Israeli channels prioritized in Home Assistant
TOP_TV_IDS = [
    "kan11", "keshet12", "reshet13", "now14", "i24news_he",
    "channel9", "channel24_israel", "ch33_makan", "knesset",
    "ynet_live", "walla_news_il", "sport5_israel_hd", "sport1_israel",
]

# Top Israeli radio stations
TOP_RADIO_IDS = [
    "galgalatz_live", "glz_live", "kan_bet", "kan_gimel", "kan_88",
    "radio_103fm", "eco99fm", "radios_100fm", "radio_102fm", "radio_91fm",
    "kol_chai", "kol_barama_live",
]

# Root categories
CATEGORIES = [
    {"id": "tv_main", "title": "📺 ערוצים מובילים (11, 12, 13, 14, 9, 24...)"},
    {"id": "tv_sports", "title": "⚽ ערוצי ספורט (ספורט 1-6, 5SPORT, ONE)"},
    {"id": "tv_news", "title": "📰 חדשות ואקטואליה"},
    {"id": "tv_kids", "title": "👶 ערוצי ילדים ונוער"},
    {"id": "tv_entertainment", "title": "🍿 סרטים, סדרות ובידור"},
    {"id": "radio_top", "title": "📻 תחנות רדיו מובילות (גלגלצ, גלצ, כאן ב, 103FM...)"},
    {"id": "tv_all", "title": "📋 כל ערוצי הטלוויזיה (א-ב)"},
    {"id": "radio_all", "title": "📋 כל תחנות הרדיו (א-ב)"},
    {"id": "vod", "title": "🎬 סדרות וסרטים (VOD)"},
]


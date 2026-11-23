package com.israeltv.plus.data

import android.content.Context
import androidx.compose.runtime.Immutable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File

/**
 * An episode in a VOD series.
 */
@Immutable
@Serializable
data class VodEpisode(
    val id: String,
    val title: String,
    val duration: String = "",
    val url: String,
    val thumb: String = "",
    val season: Int = 1,
    val episode: Int = 1,
) {
    fun toChannel(series: VodSeries): Channel = Channel(
        id = id,
        name = "${series.title} · $title",
        title = title,
        category = "סדרות VOD",
        categoryEn = "vod",
        url = url,
        logo = if (thumb.isNotBlank()) resolveVodImage(thumb) else resolveVodImage(series.poster),
        desc = series.desc,
        badge = "VOD",
        kind = "vod",
    )
}

/**
 * Resolves local assets or remote CDN URLs for VOD posters and thumbnails.
 */
fun resolveVodImage(path: String): String = when {
    path.isEmpty() -> ""
    path.startsWith("http://") || path.startsWith("https://") -> path
    path.startsWith("file://") -> path
    else -> "file:///android_asset/www/" + path.removePrefix("/")
}

/**
 * A VOD series containing episodes and rich Israeli TV metadata.
 */
@Immutable
@Serializable
data class VodSeries(
    val id: String,
    val pid: Int = 0,
    val title: String,
    val originalTitle: String = "",
    val year: String = "",
    val rating: String = "",
    val genres: List<String> = emptyList(),
    val desc: String = "",
    val poster: String = "",
    val backdrop: String = "",
    val episodes: List<VodEpisode> = emptyList(),
    val totalEpisodes: Int = 0,
)

@Immutable
data class VodPortal(
    val id: String,
    val name: String,
    val category: String,
    val badge: String,
    val desc: String,
    val url: String,
    val isFree: Boolean = false,
)

val VOD_PORTAL_CATEGORIES = listOf(
    "הכל",
    "חינם",
    "OTT ישראלי",
    "ילדים ונוער",
    "קולנוע ודוקו",
    "מורשת ומשפחה",
    "ספורט",
    "בינלאומי",
)

val ISRAELI_VOD_PORTALS: List<VodPortal> = listOf(
    // חינם / AVOD ישראלי
    VodPortal("vp_kanbox", "כאן BOX / כאן 11 VOD", "חינם", "חינם", "קופה ראשית, מנאייכ, טהרן, המפקדת, היהודים באים, זהו זה! ודוקו", "https://www.kan.org.il/lobby/kan-box/", true),
    VodPortal("vp_kankids", "כאן חינוכית VOD", "ילדים ונוער", "חינם", "כראמל, ילדי בית העץ, האחיין שלי בנץ, שקשוקה וקלאסיקות ילדים", "https://www.kankids.org.il", true),
    VodPortal("vp_makan", "מכאן 33 VOD", "חינם", "חינם", "סדרות, תרבות, חדשות ודוקו בערבית ובעברית", "https://www.makan.org.il", true),
    VodPortal("vp_12plus", "12+ (Mako VOD)", "חינם", "חינם · AVOD", "ארץ נהדרת, סברי מרנן, צומת מילר, חתונה ממבט ראשון ודרמות קשת", "https://www.mako.co.il/mako-vod", true),
    VodPortal("vp_13plus", "13+ (רשת 13 VOD)", "חינם", "חינם · AVOD", "האח הגדול, הישרדות, שנות ה-80/90, המקור ודרמות רשת", "https://13tv.co.il", true),
    VodPortal("vp_now14", "עכשיו 14 VOD", "חינם", "חינם", "הפטריוטים, מהדורות, תוכניות אולפן ודוקו", "https://www.now14.co.il", true),
    VodPortal("vp_i24", "i24NEWS VOD", "חינם", "חינם", "מהדורות ותחקירים בעברית, אנגלית, צרפתית וערבית", "https://www.i24news.tv", true),
    VodPortal("vp_walla", "וואלה VOD", "חינם", "חינם · AVOD", "סרטים, סדרות, בידור ותוכניות אולפן", "https://vod.walla.co.il", true),
    VodPortal("vp_knesset", "ערוץ הכנסת VOD", "חינם", "חינם", "דיונים, ראיונות ותוכניות אקטואליה", "https://www.knesset.tv", true),
    VodPortal("vp_relevant", "רלוונט (RELEVANT)", "חינם", "חינם", "אקטואליה, פודקאסטים ותוכניות שיח דיגיטליות", "https://relevant.co.il", true),
    // OTT ישראלי רב-ערוצי
    VodPortal("vp_yes", "yes+", "OTT ישראלי", "מנוי", "הפקות המקור של yes (פאודה, שטיסל, תאג\"ד, בלאדי מורי) ו-HBO", "https://www.yes.co.il", false),
    VodPortal("vp_sting", "STING+", "OTT ישראלי", "מנוי", "ספריית סדרות המקור והרכש של yes במסלול גמיש", "https://www.stingtv.co.il", false),
    VodPortal("vp_next", "HOT VOD / NEXT TV", "OTT ישראלי", "מנוי", "הפקות המקור של HOT (שבאבניקים, הבורר, זגורי אימפריה, אינדל)", "https://www.nexttv.co.il", false),
    VodPortal("vp_freetv", "FreeTV", "OTT ישראלי", "מנוי", "סדרות ישראליות, ערוצי קולנוע 24/7, ילדים וספורט", "https://www.freetv.tv", false),
    VodPortal("vp_cellcom", "Cellcom TV+", "OTT ישראלי", "מנוי", "ספריית VOD רחבה כולל שיתופי תוכן מקומיים ורכש", "https://www.cellcomtv.co.il", false),
    VodPortal("vp_partner", "Partner TV+", "OTT ישראלי", "מנוי", "סדרות, סרטים, ספורט ושירותי סטרימינג משולבים", "https://www.partner.co.il/tv", false),
    // ילדים ונוער
    VodPortal("vp_bigi", "BIGI (ביגי)", "ילדים ונוער", "מנוי", "הפיג'מות, השמינייה, גאליס, אליפים וספריית ערוץ הילדים וזום", "https://www.bigi.tv", false),
    VodPortal("vp_hop", "הופ! פלוס (Hop!+)", "ילדים ונוער", "מנוי", "סדרות, שירים ותכנים לגיל הרך של ערוץ הופ! ולולי", "https://www.hop.co.il", false),
    VodPortal("vp_youkid", "יוקיד (YouKid)", "ילדים ונוער", "מנוי / חינם", "קלטות ילדים ישראליות קלאסיות, הצגות וכוכבי ילדים", "https://www.youkid.co.il", false),
    VodPortal("vp_podi", "Podi.TV (התיאטרון שלנו)", "ילדים ונוער", "מנוי", "הצגות תיאטרון מלאות לילדים ותכנים ערכיים ללא פרסומות", "https://www.podi.tv", false),
    VodPortal("vp_kidstv", "ערוץ הילדים (KidsTV)", "ילדים ונוער", "חינם", "סדרות וקטעי וידאו לילדים ונוער", "https://www.kidstv.co.il", true),
    // קולנוע, תיאטרון, דוקו וארכיון
    VodPortal("vp_jfc", "הארכיון הישראלי לסרטים", "קולנוע ודוקו", "חינם", "מאות סרטים ישראליים קלאסיים, תיעודיים ויומני חדשות (סינמטק ירושלים)", "https://jfc.org.il", true),
    VodPortal("vp_tlvcin", "סינמטק תל אביב VOD", "קולנוע ודוקו", "השכרה / מנוי", "קולנוע ישראלי עצמאי, סרטים תיעודיים ובכורות", "https://www.cinema.co.il", false),
    VodPortal("vp_kanarch", "כאן ארכיון ודוקו", "קולנוע ודוקו", "חינם", "סרטי דוקו מקוריים וארכיון הטלוויזיה הישראלית מ-1968", "https://archive.kan.org.il", true),
    VodPortal("vp_ifcs", "Israel Film Center Stream", "קולנוע ודוקו", "מנוי / השכרה", "קטלוג ענק של קולנוע ישראלי עכשווי וזוכי פסטיבלים", "https://www.israelfilmcenterstream.org", false),
    VodPortal("vp_go2films", "Go2Films VOD", "קולנוע ודוקו", "השכרה", "קולנוע דוקומנטרי ישראלי ויצירה עצמאית", "https://www.go2films.com", false),
    VodPortal("vp_docaviv", "דוקאביב VOD", "קולנוע ודוקו", "פסטיבל", "סרטי הקולנוע התיעודי של פסטיבל דוקאביב", "https://www.docaviv.co.il", false),
    VodPortal("vp_habima", "הבימה פלוס", "קולנוע ודוקו", "מנוי / דיגיטל", "הצגות מוקלטות ומחזות זמר של התיאטרון הלאומי", "https://www.habima.co.il", false),
    VodPortal("vp_maale", "בית הספר לקולנוע מעלה", "קולנוע ודוקו", "פתוח", "סרטי קולנוע ישראליים קצרים ותיעודיים", "https://www.maale.co.il", true),
    VodPortal("vp_vimeo", "Vimeo On Demand", "קולנוע ודוקו", "השכרה / חינם", "סרטים ישראליים עצמאיים ישירות מהיוצרים", "https://vimeo.com/ondemand", false),
    // מורשת ומשפחה (מפוקח)
    VodPortal("vp_hidabroot", "הידברות VOD ועולם הילדים", "מורשת ומשפחה", "חינם / מנוי", "הרצאות, תוכניות אולפן וספריית ילדים מפוקחת", "https://www.hidabroot.org/vod", true),
    VodPortal("vp_tv2000", "ערוץ 2000 VOD (+ ילדים)", "מורשת ומשפחה", "חינם", "שיעורי תורה, חיזוק ורצועת ילדים ייעודית", "https://www.tv2000.co.il", true),
    VodPortal("vp_mainstream", "מיינסטרים (Mainstream)", "מורשת ומשפחה", "מנוי / השכרה", "סרטים באורך מלא ותוכן מפוקח למשפחות ולנשים", "https://mainstream.online", false),
    VodPortal("vp_torahbox", "תורה-בוקס (Torah-Box)", "מורשת ומשפחה", "חינם", "סדרות יהדות, שידורים ומוזיקה בעברית ובצרפתית", "https://www.torah-box.com", true),
    VodPortal("vp_orot", "ערוץ אורות (Orot TV)", "מורשת ומשפחה", "חינם", "סדרות העשרה, מחשבת ישראל, זוגיות וחינוך", "https://www.youtube.com/@orottv", true),
    // ספורט VOD
    VodPortal("vp_sport5", "ספורט 5 VOD", "ספורט", "חינם / מנוי", "תקצירים, מגזינים, ליגת האלופות וליגת העל בכדורסל", "https://www.sport5.co.il", true),
    VodPortal("vp_one", "ONE VOD", "ספורט", "חינם", "תקצירי ליגה ספרדית, איטלקית וכדורגל ישראלי", "https://www.one.co.il", true),
    VodPortal("vp_sport1", "ספורט 1 (צ'רלטון VOD)", "ספורט", "חינם / מנוי", "תקצירי פרמיירליג, בונדסליגה וליגת העל", "https://sport1.maariv.co.il", true),
    // בינלאומי + חו"ל + FAST חינמי
    VodPortal("vp_screenil", "Screen iL (לישראלים בחו\"ל)", "בינלאומי", "מנוי · חו\"ל", "שידורים חיים, Catch-up ל-7 ימים ו-VOD ישראלי מחוץ לישראל", "https://www.screenil.com", false),
    VodPortal("vp_izzy", "IZZY (Stream Israel)", "בינלאומי", "מנוי", "מאות סדרות, סרטים ודוקו ישראליים עם כתוביות", "https://www.streamisrael.tv", false),
    VodPortal("vp_chaiflicks", "ChaiFlicks", "בינלאומי", "מנוי", "קולנוע ישראלי, סדרות דרמה ודוקו עטורי פרסים", "https://www.chaiflicks.com", false),
    VodPortal("vp_max", "HBO Max (מקס)", "בינלאומי", "מנוי", "ספריית HBO, Warner Bros., DC וסדרות פרימיום עם עברית", "https://www.max.com", false),
    VodPortal("vp_netflix", "Netflix", "בינלאומי", "מנוי", "פאודה, שטיסל, קוגל, גוף שלישי וקטלוג עולמי בעברית", "https://www.netflix.com", false),
    VodPortal("vp_disney", "Disney+", "בינלאומי", "מנוי", "דיסני, פיקסאר, מארוול, Star Wars ו-Star עם עברית", "https://www.disneyplus.com", false),
    VodPortal("vp_appletv", "Apple TV+", "בינלאומי", "מנוי", "הפקות מקור ב-4K HDR (כולל טהרן) עם תרגום לעברית", "https://tv.apple.com", false),
    VodPortal("vp_prime", "Amazon Prime Video", "בינלאומי", "מנוי", "סדרות מקור, סרטים ותוכן ישראלי עם כתוביות בעברית", "https://www.primevideo.com", false),
    VodPortal("vp_plex", "Plex Free Movies & TV", "בינלאומי", "חינם · FAST", "עשרות אלפי סרטים, סדרות וערוצי FAST חינמיים", "https://watch.plex.tv", true),
    VodPortal("vp_tubi", "Tubi", "בינלאומי", "חינם · AVOD", "אלפי סרטים וסדרות חינמיים כולל קולנוע עצמאי", "https://tubitv.com", true),
    VodPortal("vp_pluto", "Pluto TV", "בינלאומי", "חינם · FAST", "ערוצי תוכן ליניאריים וספריית VOD חינמית", "https://pluto.tv", true),
    VodPortal("vp_rakuten", "Rakuten TV", "בינלאומי", "חינם / השכרה", "סרטים חינמיים עם פרסומות והשכרת שוברי קופות", "https://www.rakuten.tv", true),
    VodPortal("vp_kanopy", "Kanopy", "בינלאומי", "חינם · ספרייה", "קולנוע ישראלי ואמנותי מובחר עם כרטיס ספרייה", "https://www.kanopy.com", true),
)

/**
 * Repository for loading and caching VOD series from bundled assets and remote updates.
 */
object VodRepository {
    private val json = Json { ignoreUnknownKeys = true; coerceInputValues = true }

    suspend fun load(context: Context): List<VodSeries> = withContext(Dispatchers.IO) {
        // 1. Try version cache
        val cache = File(context.filesDir, "vod_data-${com.israeltv.plus.BuildConfig.VERSION_CODE}.json")
        val cachedText = cache.takeIf { it.isFile && it.length() > 200 }?.let { runCatching { it.readText() }.getOrNull() }
        if (!cachedText.isNullOrBlank()) {
            val parsed = parse(cachedText)
            if (parsed.isNotEmpty()) return@withContext parsed
        }

        // 2. Try bundled APK assets
        val assetNames = listOf("vod_data.json", "www/vod_data.json")
        for (name in assetNames) {
            val text = runCatching { context.assets.open(name).bufferedReader().use { it.readText() } }.getOrNull()
            if (!text.isNullOrBlank()) {
                val parsed = parse(text)
                if (parsed.isNotEmpty()) return@withContext parsed
            }
        }
        emptyList()
    }

    suspend fun refresh(context: Context, http: OkHttpClient): List<VodSeries>? = withContext(Dispatchers.IO) {
        runCatching {
            val req = Request.Builder().url(REMOTE + "vod_data.json?t=" + System.currentTimeMillis()).build()
            http.newCall(req).execute().use { r ->
                if (!r.isSuccessful) return@runCatching null
                val text = r.body.string()
                val parsed = parse(text)
                if (parsed.isNotEmpty()) {
                    val cache = File(context.filesDir, "vod_data-${com.israeltv.plus.BuildConfig.VERSION_CODE}.json")
                    runCatching { cache.writeText(text) }
                    parsed
                } else null
            }
        }.getOrNull()
    }

    fun parse(text: String): List<VodSeries> = runCatching {
        json.decodeFromString<List<VodSeries>>(text)
    }.getOrElse { emptyList() }
}


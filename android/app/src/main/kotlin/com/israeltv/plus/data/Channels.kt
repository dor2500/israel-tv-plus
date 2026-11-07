package com.israeltv.plus.data

import android.content.Context
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.util.concurrent.ConcurrentHashMap

/** The published site: channel list, logos, version.json (GitHub Pages). */
const val REMOTE = "https://dor2500.github.io/israel-tv-plus/"
const val USER_AGENT = "Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Mobile Safari/537.36 IsraelTVPlus/3 AndroidPhone"

private val KIDS_EXCLUDE_WORDS = listOf(
    "היפ הופ", "hiphop", "הופעות", "אירוויזיון", "יהודית", "חדשות", "ספורט", "פוליטיקה",
    "דת ", "כלכלה", "ויוה", "viva", "טלנובלה", "telenovela", "אימה", "horror", "news",
    "sport", "finance", "דרמה", "drama", "רכב", "מרוצים", "motor", "cars", "racing",
    "אוכל", "בישול", "cooking", "food", "טבע", "פרא", "docu", "דוקו", "lifestyle",
    "עיצוב", "שיפוץ", "האח הגדול", "big brother", "reality", "ריאליטי", "קומדיה", "comedy",
    "קולנוע ישראלי", "סרטים", "cinema", "movies", "hbo", "hot 3", "yes tv", "action",
    "אקשן", "פוד", "foody", "סדרות", "series", "ערוץ 9", "ערוץ 11", "ערוץ 12", "ערוץ 13",
    "ערוץ 14", "ערוץ 15", "ערוץ 16", "כאן 11", "קשת 12", "רשת 13", "עכשיו 14", "i24news",
    "כנסת", "knesset", "103fm", "100fm", "eco99", "גלגלצ", "גלצ", "קול ברמה", "קול חי", "מכאן"
)

private val KIDS_ALLOW_OVERRIDE = listOf(
    "ילדים", "פמילי", "kids", "family", "מצויר", "cartoon", "disney", "דיסני", "ניק", "nick",
    "ג'וניור", "junior", "הופ!", "לולי", "luli", "חינוכית", "hinuchit", "ילדותי", "yalduti",
    "שירי ילדות", "baby", "duck", "lego"
)

private val KIDS_RADIO_IDS = setOf("radios_disney", "radios_kids", "radios_stars", "kan_kids_radio")
private val KIDS_CATS_EN = setOf("kids", "kids_he", "kids_world", "cartoons")

private val KIDS_ALLOW_WORDS = listOf(
    "ילדים", "נוער", "פעוטות", "קטנטנים", "אנימציה", "מצוירים", "הופ!", "בייבי", "דיסני",
    "ניקלודיאון", "ניק ג'וניור", "ג'וניור", "קארטון נטוורק", "בומרנג", "שירי ילדותי", "ילדותי",
    "baby shark", "ducktv", "lego", "transformers", "cartoon network", "boomerang", "luli",
    "לולי", "זום", "zoom", "חינוכית", "hinuchit", "יויו", "yoyo", "וויז", "wiz", "באזז",
    "fomo", "stars_kids", "a_plus_kids"
)

/** One entry of channels.json (the same file the web app, the M3U playlists and Stremio are made from). */
@Immutable
@Serializable
data class Channel(
    val id: String,
    val name: String,
    val title: String = "",
    val category: String = "",
    val categoryEn: String = "",
    val url: String,
    val fallbackUrls: List<String> = emptyList(),
    val backups: List<String> = emptyList(),
    val logo: String = "",
    val badge: String = "",
    val desc: String = "",
    val number: Int = 0,
    val lang: String = "",
    val kind: String = "",
    val countryHe: String = "",
    /** "mako": Keshet's own CDN, the URL needs a viewing ticket from mako (see [playableUrl]). */
    val auth: String = "",
    val headers: Map<String, String> = emptyMap(),
) {
    val isRadio: Boolean get() = kind == "radio"

    @Transient
    val isKidsChannel: Boolean = computeIsKids()

    private fun computeIsKids(): Boolean {
        val cat = category.lowercase()
        val en = categoryEn.lowercase()
        val n = name.lowercase()
        val cid = id.lowercase()
        val full = "$cat $en $n $cid"
        if (KIDS_EXCLUDE_WORDS.any { it in full }) {
            if (KIDS_ALLOW_OVERRIDE.none { it in full }) return false
        }
        if (isRadio || "radio" in en) {
            return cid in KIDS_RADIO_IDS
        }
        if ("ילדים" in cat || en in KIDS_CATS_EN) return true
        return KIDS_ALLOW_WORDS.any { it in full }
    }

    /** Ordered list of all stream URLs (primary + fallbacks + backups) to try automatically on playback error. */
    fun allCandidateUrls(): List<String> =
        (listOf(url) + fallbackUrls + backups).map { it.trim() }.filter { it.isNotEmpty() }.distinct()
}

private const val MAKO_ENTITLEMENT = "https://mass.mako.co.il/ClicksStatistics/entitlementsServicesV2.jsp"
private const val MAKO_TICKET_TTL_MS = 10 * 60_000L
private val makoTicketCache = ConcurrentHashMap<String, Pair<String, Long>>()

/**
 * Resolves a single candidate stream URL. Automatically fetches a Mako entitlement ticket whenever the URL is hosted
 * on mako-streaming.akamaized.net (or when auth == "mako" and the URL is a Mako stream); every other stream plays as is.
 */
suspend fun playableUrl(rawUrl: String, auth: String = "", forceRefresh: Boolean = false): String = withContext(Dispatchers.IO) {
    val needsMakoTicket = rawUrl.contains("mako-streaming.akamaized.net", ignoreCase = true) ||
        (auth == "mako" && rawUrl.contains("mako", ignoreCase = true))
    if (!needsMakoTicket) return@withContext rawUrl

    val now = System.currentTimeMillis()
    if (!forceRefresh) {
        makoTicketCache[rawUrl]?.let { (signedUrl, ts) ->
            if (now - ts < MAKO_TICKET_TTL_MS) return@withContext signedUrl
        }
    }

    val path = java.net.URI(rawUrl).let { it.rawPath + (it.rawQuery?.let { q -> "?$q" } ?: "") }
    val req = java.net.URL(MAKO_ENTITLEMENT + "?et=ngt&rv=AKAMAI&lp=" + java.net.URLEncoder.encode(path, "UTF-8"))
    val text = (req.openConnection() as java.net.HttpURLConnection).run {
        connectTimeout = 8_000
        readTimeout = 8_000
        setRequestProperty("User-Agent", USER_AGENT)
        setRequestProperty("Referer", "https://www.mako.co.il/")
        setRequestProperty("Origin", "https://www.mako.co.il")
        inputStream.bufferedReader().use { it.readText() }
    }
    val ticket = Regex("\"ticket\"\\s*:\\s*\"([^\"]+)\"").find(text)?.groupValues?.get(1)
        ?: error("no mako ticket")
    val resolved = rawUrl + (if ('?' in rawUrl) "&" else "?") + java.net.URLDecoder.decode(ticket, "UTF-8")
    makoTicketCache[rawUrl] = resolved to now
    resolved
}

suspend fun playableUrl(ch: Channel): String = playableUrl(ch.url, ch.auth)

@Immutable
data class Category(val key: String, val name: String, val channels: List<Channel>)

@Immutable
data class ChannelData(
    val channels: List<Channel>,
    val categories: List<Category>,
    val byId: Map<String, Channel>,
    val featured: List<Channel>,
    val vod: List<VodSeries> = emptyList(),
)

/** "ספורט (Sports)" -> "ספורט", "ערוצי ישראל - שידור חי" -> "ערוצי ישראל" */
fun shortCategory(name: String): String =
    name.substringBefore(" (").substringBefore(" - ").trim()

/** "ערוץ" as a word, kept before a number or a definite ("ה") brand noun: "ערוץ 12", "ערוץ הילדים"; "ערוץ הופ!" -> "הופ!". */
private val ChannelWord = Regex("""(^|\s)ערוץ(?=\s|$)(?!\s+(\d|ה(?!ופ)))""")
private val NumberOnly = Regex("^[\\d\\s+]*$")

/**
 * The name shown on cards: the repetitive word "ערוץ" (channel) dropped, so the real name fits
 * ("ערוץ זום ZOOM ילדים ונוער HD" -> "זום ZOOM ילדים ונוער HD"). Kept when only a number would be left ("ערוץ 12").
 */
val Channel.displayName: String
    get() {
        val short = name.replace(ChannelWord, " ").replace(Regex("\\s+"), " ").trim().trimStart('-', '–', ' ')
        return if (short.isEmpty() || NumberOnly.matches(short)) name else short
    }

/** The line under a card: country for world channels, the category for Israeli ones. */
fun Channel.subtitle(): String = when {
    isRadio -> "רדיו"
    countryHe.isNotEmpty() -> countryHe
    else -> shortCategory(category)
}

/**
 * The channel list: the copy bundled in the APK first (instant start, works offline), then the published list from
 * GitHub, which is kept in filesDir for the next start. Logos come from the APK when it has the file, else the site.
 */
@Stable
class ChannelStore(private val context: Context, private val http: OkHttpClient) {
    private val json = Json { ignoreUnknownKeys = true; coerceInputValues = true }
    // per app version: after an update the list bundled with the new APK is used until the site's list is fetched
    private val cache = File(context.filesDir, "channels-${com.israeltv.plus.BuildConfig.VERSION_CODE}.json")
    private val bundledLogos: Set<String> by lazy {
        val s = mutableSetOf<String>()
        runCatching { context.assets.list("www/assets/logos")?.let { s.addAll(it) } }
        runCatching { context.assets.list("www/assets/vod")?.let { s.addAll(it) } }
        runCatching { context.assets.list("www/assets/app")?.let { s.addAll(it) } }
        s
    }
    private val _state = MutableStateFlow<ChannelData?>(null)
    val state: StateFlow<ChannelData?> = _state

    suspend fun load() = withContext(Dispatchers.IO) {
        // 1. Emit live channels immediately for <50ms startup
        val text = cache.takeIf { it.isFile && it.length() > 1000 }?.let { runCatching { it.readText() }.getOrNull() }
            ?: context.assets.open("channels.json").bufferedReader().use { it.readText() }
        val initial = parse(text)
            ?: parse(context.assets.open("channels.json").bufferedReader().use { it.readText() })
        if (initial != null) {
            _state.value = initial
        }
        // 2. Load 2MB VOD catalog right after without blocking live channels
        val vod = VodRepository.load(context)
        if (vod.isNotEmpty()) {
            _state.value = (_state.value ?: initial)?.copy(vod = vod)
        }
    }

    suspend fun refresh() = withContext(Dispatchers.IO) {
        val freshVod = VodRepository.refresh(context, http) ?: _state.value?.vod.orEmpty()
        runCatching {
            val req = Request.Builder().url(REMOTE + "channels.json?t=" + System.currentTimeMillis()).build()
            http.newCall(req).execute().use { r ->
                if (!r.isSuccessful) return@runCatching
                val text = r.body.string()
                val data = parse(text, freshVod) ?: return@runCatching
                cache.writeText(text)
                _state.value = data
            }
        }
    }

    fun logoUrl(ch: Channel): String = when {
        ch.logo.isEmpty() -> ""
        ch.logo.startsWith("http") -> ch.logo
        ch.logo.substringAfterLast('/') in bundledLogos -> "file:///android_asset/www/" + ch.logo.removePrefix("/")
        else -> REMOTE + ch.logo.removePrefix("/")
    }

    private fun parse(text: String, vod: List<VodSeries> = emptyList()): ChannelData? = runCatching {
        val all = json.decodeFromString<List<Channel>>(text)
            .filter { it.kind != "app" && it.url.isNotEmpty() }
            .sortedBy { it.number }
        if (all.isEmpty()) return null
        val cats = all.groupBy { it.category }.map { (k, v) -> Category(k, shortCategory(k), v) }
        val israel = cats.firstOrNull { it.channels.first().categoryEn == "israel" }?.channels.orEmpty()
        ChannelData(all, cats, all.associateBy { it.id }, israel.filter { !it.isRadio }.take(8), vod)
    }.getOrNull()
}

fun Channel.isKids(): Boolean = isKidsChannel

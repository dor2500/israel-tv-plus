package com.israeltv.plus.wear

import android.content.Context
import androidx.compose.runtime.Immutable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

/** The published site: the channel list and the logos (GitHub Pages). */
const val REMOTE = "https://dor2500.github.io/israel-tv-plus/"
const val USER_AGENT = "Mozilla/5.0 (Linux; Android 14; Wear OS) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0 Mobile Safari/537.36 IsraelTVPlus/4 Wear"

/** One entry of channels.json (the same file the web app, the phone app and the playlists are made from). */
@Immutable
@Serializable
data class Channel(
    val id: String,
    val name: String,
    val category: String = "",
    val categoryEn: String = "",
    val url: String,
    val logo: String = "",
    val badge: String = "",
    val desc: String = "",
    val number: Int = 0,
    val kind: String = "",
    val countryHe: String = "",
    val imported: Boolean = false,
    /** "mako": Keshet's own CDN, the URL needs a viewing ticket from mako (see [playableUrl]). */
    val auth: String = "",
) {
    val isRadio: Boolean get() = kind == "radio" || categoryEn == "radio"
    val logoUrl: String get() = when {
        logo.isEmpty() -> ""
        logo.startsWith("http") -> logo
        else -> REMOTE + logo
    }
    val subtitle: String get() = when {
        isRadio -> "רדיו"
        countryHe.isNotEmpty() -> countryHe
        else -> shortCategory(category)
    }
}

/** "ספורט (Sports)" -> "ספורט", "ערוצי ישראל - שידור חי" -> "ערוצי ישראל" */
fun shortCategory(name: String): String = name.substringBefore(" (").substringBefore(" - ").trim()

private const val MAKO_ENTITLEMENT = "https://mass.mako.co.il/ClicksStatistics/entitlementsServicesV2.jsp"

/**
 * The URL to hand to the player. Keshet 12 / 24 stream from mako's CDN (the broadcaster, not a re-stream) and need a
 * short-lived ticket from mako's entitlement service, fetched right before playing; every other channel plays as is.
 */
suspend fun playableUrl(ch: Channel): String = withContext(Dispatchers.IO) {
    if (ch.auth != "mako") return@withContext ch.url
    val path = java.net.URI(ch.url).let { it.rawPath + (it.rawQuery?.let { q -> "?$q" } ?: "") }
    val req = URL(MAKO_ENTITLEMENT + "?et=ngt&rv=AKAMAI&lp=" + java.net.URLEncoder.encode(path, "UTF-8"))
    val text = (req.openConnection() as HttpURLConnection).run {
        connectTimeout = 10_000
        readTimeout = 10_000
        setRequestProperty("User-Agent", USER_AGENT)
        inputStream.bufferedReader().use { it.readText() }
    }
    val ticket = Regex("\"ticket\"\\s*:\\s*\"([^\"]+)\"").find(text)?.groupValues?.get(1) ?: error("no mako ticket")
    ch.url + (if ('?' in ch.url) "&" else "?") + java.net.URLDecoder.decode(ticket, "UTF-8")
}

/** A list on the watch: one of the home screen's shelves, or a category. */
@Immutable
data class Shelf(val key: String, val icon: String, val name: String, val channels: List<Channel>)

@Immutable
data class ChannelData(val all: List<Channel>, val byId: Map<String, Channel>, val shelves: List<Shelf>, val categories: List<Shelf>) {
    fun shelf(key: String): Shelf? = shelves.firstOrNull { it.key == key } ?: categories.firstOrNull { it.key == key }
}

/**
 * The channel list: the copy bundled in the APK first (instant start, works offline), then the published list from
 * GitHub, kept in filesDir for the next start (new channels reach the watch without an app update).
 */
class ChannelStore(private val context: Context) {
    private val json = Json { ignoreUnknownKeys = true; coerceInputValues = true }
    private val cache = File(context.filesDir, "channels-${BuildConfig.VERSION_CODE}.json")
    private val _state = MutableStateFlow<ChannelData?>(null)
    val state: StateFlow<ChannelData?> = _state

    suspend fun load() = withContext(Dispatchers.IO) {
        val cached = cache.takeIf { it.isFile && it.length() > 1000 }?.let { runCatching { it.readText() }.getOrNull() }
        val data = cached?.let { parse(it) }
            ?: parse(context.assets.open("channels.json").bufferedReader().use { it.readText() })
        if (data != null) _state.value = data
    }

    suspend fun refresh() = withContext(Dispatchers.IO) {
        runCatching {
            val conn = URL(REMOTE + "channels.json?t=" + System.currentTimeMillis()).openConnection() as HttpURLConnection
            conn.connectTimeout = 15_000
            conn.readTimeout = 20_000
            conn.setRequestProperty("User-Agent", USER_AGENT)
            if (conn.responseCode != 200) return@runCatching
            val text = conn.inputStream.bufferedReader().use { it.readText() }
            val data = parse(text) ?: return@runCatching
            cache.writeText(text)
            _state.value = data
        }
    }

    private fun parse(text: String): ChannelData? = runCatching {
        val all = json.decodeFromString<List<Channel>>(text)
            .filter { it.kind != "app" && it.url.isNotEmpty() }
            .sortedBy { it.number }
        if (all.isEmpty()) return null
        // the broadcasters' own Israeli channels first, then the rest of the world
        fun israeliFirst(list: List<Channel>) = list.filter { !it.imported } + list.filter { it.imported }
        val shelves = listOf(
            Shelf("israel", "📺", "ערוצי ישראל", all.filter { it.category.startsWith("ערוצי ישראל") && !it.isRadio }),
            Shelf("radio", "📻", "רדיו ישראלי", all.filter { it.isRadio }),
            Shelf("sports", "⚽", "ספורט", israeliFirst(all.filter { it.categoryEn == "sports" && !it.isRadio })),
            Shelf("kids", "🧸", "ילדים", israeliFirst(all.filter { (it.categoryEn == "kids" || "ילדים" in it.category) && !it.isRadio })),
            Shelf("movies", "🎬", "סרטים וסדרות", israeliFirst(all.filter { it.categoryEn == "movies" || it.categoryEn == "world_movies" })),
        ).filter { it.channels.isNotEmpty() }
        val categories = all.groupBy { it.category }
            .map { (k, v) -> Shelf("cat:$k", "", shortCategory(k), israeliFirst(v)) }
            .sortedWith(compareBy<Shelf>({ s -> s.channels.all { it.imported } }, { -it.channels.size }))
        ChannelData(all, all.associateBy { it.id }, shelves, categories)
    }.getOrNull()
}

/** Favourites and recently played, kept on the watch. */
class Library(context: Context) {
    private val prefs = context.getSharedPreferences("library", Context.MODE_PRIVATE)
    private val _favorites = MutableStateFlow(read("favorites"))
    private val _recents = MutableStateFlow(read("recents"))
    val favorites: StateFlow<List<String>> = _favorites
    val recents: StateFlow<List<String>> = _recents

    private fun read(key: String) = prefs.getString(key, "").orEmpty().split(',').filter { it.isNotEmpty() }
    private fun write(key: String, list: List<String>) = prefs.edit().putString(key, list.joinToString(",")).apply()

    /** Returns true when the channel is now a favourite. */
    fun toggleFavorite(id: String): Boolean {
        val now = _favorites.value.toMutableList()
        val added = if (id in now) { now.remove(id); false } else { now.add(0, id); true }
        _favorites.value = now
        write("favorites", now)
        return added
    }

    fun addRecent(id: String) {
        val now = (listOf(id) + _recents.value.filter { it != id }).take(12)
        _recents.value = now
        write("recents", now)
    }
}

package com.israeltv.plus.data

import android.content.Context
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File

/** The published site: channel list, logos, version.json (GitHub Pages). */
const val REMOTE = "https://dor2500.github.io/israel-tv-plus/"
const val USER_AGENT = "Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0 Mobile Safari/537.36 IsraelTVPlus/3 AndroidPhone"

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
    val logo: String = "",
    val badge: String = "",
    val desc: String = "",
    val number: Int = 0,
    val lang: String = "",
    val kind: String = "",
    val countryHe: String = "",
) {
    val isRadio: Boolean get() = kind == "radio"
}

@Immutable
data class Category(val key: String, val name: String, val channels: List<Channel>)

@Immutable
data class ChannelData(
    val channels: List<Channel>,
    val categories: List<Category>,
    val byId: Map<String, Channel>,
    val featured: List<Channel>,
)

/** "ספורט (Sports)" -> "ספורט", "ערוצי ישראל - שידור חי" -> "ערוצי ישראל" */
fun shortCategory(name: String): String =
    name.substringBefore(" (").substringBefore(" - ").trim()

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
    private val cache = File(context.filesDir, "channels.json")
    private val bundledLogos: Set<String> by lazy {
        runCatching { context.assets.list("www/assets/logos")?.toSet() }.getOrNull().orEmpty()
    }
    private val _state = MutableStateFlow<ChannelData?>(null)
    val state: StateFlow<ChannelData?> = _state

    suspend fun load() = withContext(Dispatchers.IO) {
        val text = cache.takeIf { it.isFile && it.length() > 1000 }?.let { runCatching { it.readText() }.getOrNull() }
            ?: context.assets.open("channels.json").bufferedReader().use { it.readText() }
        parse(text)?.let { _state.value = it }
            ?: parse(context.assets.open("channels.json").bufferedReader().use { it.readText() })?.let { _state.value = it }
    }

    suspend fun refresh() = withContext(Dispatchers.IO) {
        runCatching {
            val req = Request.Builder().url(REMOTE + "channels.json?t=" + System.currentTimeMillis()).build()
            http.newCall(req).execute().use { r ->
                if (!r.isSuccessful) return@runCatching
                val text = r.body.string()
                val data = parse(text) ?: return@runCatching
                cache.writeText(text)
                _state.value = data
            }
        }
    }

    fun logoUrl(ch: Channel): String = when {
        ch.logo.isEmpty() -> ""
        ch.logo.startsWith("http") -> ch.logo
        ch.logo.substringAfterLast('/') in bundledLogos -> "file:///android_asset/www/" + ch.logo
        else -> REMOTE + ch.logo
    }

    private fun parse(text: String): ChannelData? = runCatching {
        val all = json.decodeFromString<List<Channel>>(text)
            .filter { it.kind != "app" && it.url.isNotEmpty() }
            .sortedBy { it.number }
        if (all.isEmpty()) return null
        val cats = all.groupBy { it.category }.map { (k, v) -> Category(k, shortCategory(k), v) }
        val israel = cats.firstOrNull { it.channels.first().categoryEn == "israel" }?.channels.orEmpty()
        ChannelData(all, cats, all.associateBy { it.id }, israel.filter { !it.isRadio }.take(8))
    }.getOrNull()
}

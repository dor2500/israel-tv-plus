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


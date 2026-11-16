package com.israeltv.plus.data

import android.content.Context
import androidx.compose.runtime.Immutable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Immutable
data class CatchupProgram(
    val startMin: Long,
    val durationMin: Int,
    val title: String,
    val description: String = "",
    val poster: String = "",
) {
    val startMs: Long get() = startMin * 60_000L
    val endMs: Long get() = (startMin + durationMin) * 60_000L

    val isLiveNow: Boolean get() {
        val now = System.currentTimeMillis()
        return now in startMs..endMs
    }

    val isPast: Boolean get() = System.currentTimeMillis() > endMs

    val progressFraction: Float get() {
        val total = (endMs - startMs).coerceAtLeast(1L)
        val elapsed = (System.currentTimeMillis() - startMs).coerceIn(0L, total)
        return (elapsed.toFloat() / total.toFloat()).coerceIn(0f, 1f)
    }

    val timeLabel: String get() {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return "${sdf.format(Date(startMs))} - ${sdf.format(Date(endMs))}"
    }

    val relativeLabel: String get() {
        val now = System.currentTimeMillis()
        return when {
            isLiveNow -> "משודר עכשיו"
            now < startMs -> "בקרוב"
            else -> {
                val diffMin = ((now - endMs) / 60_000L).coerceAtLeast(1L)
                if (diffMin < 60) "שודר לפני $diffMin דק'"
                else "שודר לפני ${diffMin / 60} שעות"
            }
        }
    }
}

object CatchupDvr {
    /** Mapping of channel IDs to multi-hour sliding DVR streams */
    val DVR_CHANNELS = mapOf(
        // Reshet 13 (24h CloudFront DVR window)
        "reshet13" to "https://d198ztbnlup2iq.cloudfront.net/out/v1/2d9050c90fb94df8b78d1d98306a1a65/index.m3u8",
        "reshet_13" to "https://d198ztbnlup2iq.cloudfront.net/out/v1/2d9050c90fb94df8b78d1d98306a1a65/index.m3u8",
        "channel_13" to "https://d198ztbnlup2iq.cloudfront.net/out/v1/2d9050c90fb94df8b78d1d98306a1a65/index.m3u8",
        // Kan 11 (12h Redge DVR window)
        "kan11" to "https://r.il.cdn-redge.media/livehls/oil/kancdn-live/live/kan11/live.livx/playlist.m3u8?dvr=43200000",
        "kan_11" to "https://r.il.cdn-redge.media/livehls/oil/kancdn-live/live/kan11/live.livx/playlist.m3u8?dvr=43200000",
        // Now 14 (12h Redge DVR window)
        "ch14" to "https://r.il.cdn-redge.media/livehls/oil/ch14/live/ch14/live.livx/playlist.m3u8?dvr=43200000",
        "now14" to "https://r.il.cdn-redge.media/livehls/oil/ch14/live/ch14/live.livx/playlist.m3u8?dvr=43200000",
        "ch14_now_fast" to "https://r.il.cdn-redge.media/livehls/oil/ch14/live/ch14/live.livx/playlist.m3u8?dvr=43200000",
        "channel_14" to "https://r.il.cdn-redge.media/livehls/oil/ch14/live/ch14/live.livx/playlist.m3u8?dvr=43200000",
        // Kan Hinuchit (12h Redge DVR window)
        "kan_hinuchit" to "https://r.il.cdn-redge.media/livehls/oil/kancdn-live/live/kan_edu/live.livx/playlist.m3u8?dvr=43200000",
        "kan_hinuchit_mc" to "https://r.il.cdn-redge.media/livehls/oil/kancdn-live/live/kan_edu/live.livx/playlist.m3u8?dvr=43200000",
        // Makan 33 (12h Redge DVR window)
        "makan33" to "https://r.il.cdn-redge.media/livehls/oil/kancdn-live/live/makan/live.livx/playlist.m3u8?dvr=43200000",
        "makan_33" to "https://r.il.cdn-redge.media/livehls/oil/kancdn-live/live/makan/live.livx/playlist.m3u8?dvr=43200000",
        // the same broadcasts under their other channel entries
        "i24_english_world_hd" to "https://i24newsenglish-cdn.encoders.immergo.tv/master.m3u8",
        "i24news_fr_il" to "https://i24newsfrench-cdn.encoders.immergo.tv/master.m3u8",
        "i24news_hebrew_hd" to "https://i24newshebrew-cdn.encoders.immergo.tv/master.m3u8",
        "kan11_subs" to "https://r.il.cdn-redge.media/livehls/oil/kancdn-live/live/kan11/live.livx/playlist.m3u8?dvr=43200000",
        "kan11_subs_il" to "https://r.il.cdn-redge.media/livehls/oil/kancdn-live/live/kan11/live.livx/playlist.m3u8?dvr=43200000",
        "now14_subs_il" to "https://r.il.cdn-redge.media/livehls/oil/ch14/live/ch14/live.livx/playlist.m3u8?dvr=43200000",
        "reshet13_access" to "https://d198ztbnlup2iq.cloudfront.net/out/v1/2d9050c90fb94df8b78d1d98306a1a65/index.m3u8",
        "reshet13_subs_il" to "https://d198ztbnlup2iq.cloudfront.net/out/v1/2d9050c90fb94df8b78d1d98306a1a65/index.m3u8",
        // i24NEWS
        "i24news_he" to "https://i24newshebrew-cdn.encoders.immergo.tv/master.m3u8",
        "i24news_en" to "https://i24newsenglish-cdn.encoders.immergo.tv/master.m3u8",
        "i24news_english_hd" to "https://i24newsenglish-cdn.encoders.immergo.tv/master.m3u8",
        "i24news_ar" to "https://i24newsarabic-cdn.encoders.immergo.tv/master.m3u8",
        "i24news_arabic_hd" to "https://i24newsarabic-cdn.encoders.immergo.tv/master.m3u8",
        "i24news_fr" to "https://i24newsfrench-cdn.encoders.immergo.tv/master.m3u8",
    )

    fun dvrUrlFor(channel: Channel): String? =
        DVR_CHANNELS[channel.id] ?: DVR_CHANNELS[channel.id.lowercase()]
}

object CatchupArchive {
    private val FLUSSONIC_RE = Regex("""/(index|video|mono)\.m3u8(\?.*)?$""", RegexOption.IGNORE_CASE)
    private val IPTV_ARCHIVE_HOST_RE = Regex("""/iptv/[^/]+/\d+/(index|video|mono)\.m3u8""", RegexOption.IGNORE_CASE)

    fun supportsFlussonic(url: String): Boolean = IPTV_ARCHIVE_HOST_RE.containsMatchIn(url)

    fun hasArchive(channel: Channel): Boolean =
        channel.catchupDays > 0 || channel.catchupSource.isNotEmpty() || channel.catchupType.isNotEmpty() || supportsFlussonic(channel.url)

    fun archiveUrlFor(channel: Channel, startMs: Long, durationSec: Int): String? {
        if (!hasArchive(channel)) return null
        val startSec = (startMs / 1000L).coerceAtLeast(1L)
        val nowSec = System.currentTimeMillis() / 1000L
        val dur = durationSec.coerceIn(60, 6 * 3600)
        val endSec = startSec + dur
        val offsetSec = (nowSec - startSec).coerceAtLeast(0L)
        val url = channel.url

        if (channel.catchupSource.isNotEmpty()) {
            val utcCal = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC")).apply { timeInMillis = startSec * 1000L }
            fun pad2(v: Int) = v.toString().padStart(2, '0')
            var tmpl = channel.catchupSource
                .replace("{utc}", startSec.toString())
                .replace("\${start}", startSec.toString())
                .replace("{end}", endSec.toString())
                .replace("\${end}", endSec.toString())
                .replace("{duration}", dur.toString())
                .replace("\${duration}", dur.toString())
                .replace("{offset}", offsetSec.toString())
                .replace("{lutc}", nowSec.toString())
                .replace("\${timestamp}", nowSec.toString())
                .replace("{Y}", utcCal.get(java.util.Calendar.YEAR).toString())
                .replace("{m}", pad2(utcCal.get(java.util.Calendar.MONTH) + 1))
                .replace("{d}", pad2(utcCal.get(java.util.Calendar.DAY_OF_MONTH)))
                .replace("{H}", pad2(utcCal.get(java.util.Calendar.HOUR_OF_DAY)))
                .replace("{M}", pad2(utcCal.get(java.util.Calendar.MINUTE)))
                .replace("{S}", pad2(utcCal.get(java.util.Calendar.SECOND)))
            if (tmpl.startsWith("?") || tmpl.startsWith("&")) {
                val sep = if ('?' in url) "&" else "?"
                return url + sep + tmpl.trimStart('?', '&')
            }
            if (tmpl.startsWith("http://", ignoreCase = true) || tmpl.startsWith("https://", ignoreCase = true)) {
                return tmpl
            }
            return runCatching { java.net.URI(url).resolve(tmpl).toString() }.getOrDefault(tmpl)
        }

        val type = channel.catchupType.lowercase()
        if (type == "append" || type == "shift") {
            val sep = if ('?' in url) "&" else "?"
            return "${url}${sep}utc=${startSec}&lutc=${nowSec}"
        }

        if (FLUSSONIC_RE.containsMatchIn(url)) {
            return FLUSSONIC_RE.replace(url) { m ->
                val q = m.groupValues[2]
                "/archive-${startSec}-${dur}.m3u8$q"
            }
        }
        val sep = if ('?' in url) "&" else "?"
        return "${url}${sep}utc=${startSec}"
    }
}

object CatchupRepository {
    private var cachedEpgJson: JSONObject? = null
    private var lastFetchTime = 0L

    private fun readBundledEpg(context: Context): String? {
        for (name in listOf("www/epg.json", "epg.json")) {
            val s = runCatching { context.assets.open(name).bufferedReader().use { it.readText() } }.getOrNull()
            if (!s.isNullOrBlank()) return s
        }
        return null
    }

    private suspend fun getEpgObject(context: Context): JSONObject? = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        if (cachedEpgJson != null && now - lastFetchTime < 30 * 60_000L) {
            return@withContext cachedEpgJson
        }

        // 1. Try reading fresh cached file (< 3 hours old)
        val cacheFile = File(context.filesDir, "epg_cache.json")
        var jsonStr: String? = null
        if (cacheFile.exists() && cacheFile.length() > 1000 && now - cacheFile.lastModified() < 3 * 3600_000L) {
            jsonStr = runCatching { cacheFile.readText() }.getOrNull()
        }

        // 2. Fetch fresh remote EPG from GitHub Pages (updated every 6h by epg.yml)
        if (jsonStr == null) {
            jsonStr = runCatching {
                val url = java.net.URL(REMOTE + "epg.json")
                val conn = url.openConnection() as java.net.HttpURLConnection
                conn.connectTimeout = 5000
                conn.readTimeout = 7000
                conn.setRequestProperty("User-Agent", USER_AGENT)
                conn.inputStream.bufferedReader().use { it.readText() }
            }.getOrNull()
            if (!jsonStr.isNullOrBlank()) {
                runCatching { cacheFile.writeText(jsonStr) }
            }
        }

        // 3. Offline fallback: stale cache or bundled asset
        if (jsonStr == null && cacheFile.exists() && cacheFile.length() > 1000) {
            jsonStr = runCatching { cacheFile.readText() }.getOrNull()
        }
        if (jsonStr == null) {
            jsonStr = readBundledEpg(context)
        }

        if (!jsonStr.isNullOrBlank()) {
            runCatching {
                val obj = JSONObject(jsonStr)
                cachedEpgJson = obj
                lastFetchTime = now
                return@withContext obj
            }
        }
        null
    }

    suspend fun getProgramsForChannel(context: Context, channel: Channel): List<CatchupProgram> = withContext(Dispatchers.IO) {
        val root = getEpgObject(context) ?: return@withContext emptyList()
        val channelsObj = root.optJSONObject("channels") ?: return@withContext emptyList()

        val sameGuide = mapOf(
            "ch14_now_fast" to "now14",
            "i24news_english_hd" to "i24news_en",
            "i24news_fr_il" to "i24news_fr",
            "i24news_hebrew_hd" to "i24news_he",
            "i24_english_world_hd" to "i24news_en",
            "kan11_subs" to "kan11",
            "kan11_subs_il" to "kan11",
            "now14_subs_il" to "now14",
            "reshet13_access" to "reshet13",
            "reshet13_subs_il" to "reshet13"
        )
        val nameLower = channel.name.lowercase()
        val nameAliases = buildList {
            if (channel.epgId.isNotEmpty()) add(channel.epgId)
            if ("כאן 11" in nameLower || "kan 11" in nameLower) add("kan11")
            if ("קשת 12" in nameLower || "ערוץ 12" in nameLower || "keshet" in nameLower) add("keshet12")
            if ("רשת 13" in nameLower || "ערוץ 13" in nameLower || "reshet" in nameLower) add("reshet13")
            if ("עכשיו 14" in nameLower || "ערוץ 14" in nameLower || "now 14" in nameLower) add("now14")
            if ("חינוכית" in nameLower || "כאן 23" in nameLower) add("kan_hinuchit")
            if ("מכאן 33" in nameLower || "makan" in nameLower) add("makan33")
            if ("ערוץ 9" in nameLower) add("channel9_israel_hd")
            if ("ספורט 1" in nameLower || "sport 1" in nameLower) add("sport1_israel")
            if ("ספורט 2" in nameLower || "sport 2" in nameLower) add("sport2_israel")
            if ("ספורט 3" in nameLower || "sport 3" in nameLower) add("sport3_israel")
            if ("ספורט 4" in nameLower || "sport 4" in nameLower) add("sport4_israel")
            if ("ספורט 5+" in nameLower || "5+" in nameLower || "5plus" in nameLower) add("sport5_plus")
            if ("5 לייב" in nameLower || "5live" in nameLower) add("sport5_live")
            if ("5 גולד" in nameLower || "5gold" in nameLower) add("sport5_gold")
            if ("ספורט 5" in nameLower || "sport 5" in nameLower) add("sport5")
            if ("one 2" in nameLower || "וואן 2" in nameLower) add("one_2")
            if ("one" in nameLower || "וואן" in nameLower) add("one_1")
        }
        val candidates = nameAliases + listOf(
            sameGuide[channel.id] ?: channel.id,
            channel.id,
            channel.id.lowercase(),
            channel.id.replace("_hd", ""),
            channel.id.replace("_fhd", ""),
            channel.id.replace("_il", ""),
            channel.id.replace("_fast", ""),
            channel.id.replace("channel_", "ch"),
            channel.id.replace("ch14", "now14"),
            channel.id.replace("now_14", "now14"),
            channel.id.replace("keshet_12", "keshet12"),
            channel.id.replace("reshet_13", "reshet13"),
            channel.id.replace("kan_11", "kan11"),
            channel.id.replace("channel9", "channel9_israel_hd"),
        )

        var arr: org.json.JSONArray? = null
        for (cand in candidates) {
            if (channelsObj.has(cand)) {
                arr = channelsObj.optJSONArray(cand)
                break
            }
        }

        if (arr == null) {
            if (!CatchupArchive.hasArchive(channel)) return@withContext emptyList()
            val days = channel.catchupDays.coerceIn(1, 3)
            val nowHourMin = (System.currentTimeMillis() / 3_600_000L) * 60L
            return@withContext (0 until days * 24).map { h ->
                val startMin = nowHourMin - h * 60L
                val sdf = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())
                val label = if (h == 0) "שידור נוכחי (${sdf.format(Date(startMin * 60_000L))})" else "ארכיון · ${sdf.format(Date(startMin * 60_000L))}"
                CatchupProgram(startMin, 60, label, "הקלטת ספק IPTV")
            }
        }

        val list = mutableListOf<CatchupProgram>()
        val nowMin = System.currentTimeMillis() / 60_000L
        val lookbackHours = maxOf(24, channel.catchupDays.coerceAtMost(7) * 24)
        val minLimit = nowMin - lookbackHours * 60L
        val maxLimit = nowMin + 6 * 60L  // Up to next 6 hours

        for (i in 0 until arr.length()) {
            val item = arr.optJSONArray(i) ?: continue
            val start = item.optLong(0, 0L)
            val dur = item.optInt(1, 0)
            val title = item.optString(2, "")
            val desc = item.optString(3, "")
            val poster = item.optString(4, "")

            if (start + dur >= minLimit && start <= maxLimit && title.isNotEmpty()) {
                list.add(CatchupProgram(start, dur, title, desc, poster))
            }
        }

        // Return sorted reverse chronological (most recent at top)
        list.sortedByDescending { it.startMin }
    }

    /** Returns the currently airing program and the next upcoming program for a channel. */
    suspend fun getCurrentAndNextProgram(context: Context, channel: Channel): Pair<CatchupProgram?, CatchupProgram?> = withContext(Dispatchers.IO) {
        val programs = getProgramsForChannel(context, channel).sortedBy { it.startMin }
        if (programs.isEmpty()) return@withContext null to null
        val now = System.currentTimeMillis()
        val currentIdx = programs.indexOfFirst { now in it.startMs..it.endMs }
        if (currentIdx != -1) {
            val current = programs[currentIdx]
            val next = programs.getOrNull(currentIdx + 1)
            return@withContext current to next
        }
        val upcoming = programs.firstOrNull { it.startMs > now }
        null to upcoming
    }

    fun hasCatchup(channel: Channel): Boolean {
        return CatchupDvr.dvrUrlFor(channel) != null || CatchupArchive.hasArchive(channel)
    }
}

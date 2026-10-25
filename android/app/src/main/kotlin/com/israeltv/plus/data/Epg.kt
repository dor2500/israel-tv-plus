package com.israeltv.plus.data

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.setValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.Calendar

@Immutable
data class Programme(val start: Long, val end: Long, val title: String, val desc: String) {
    /** 0..1 of the programme that has passed. */
    fun progress(nowMin: Long) = ((nowMin - start).toFloat() / (end - start).coerceAtLeast(1)).coerceIn(0f, 1f)
}

/**
 * The programme guide: epg.json (tools/make_epg.py, rebuilt every two hours on the repository's "epg" branch) –
 * { t, c: { channel id: [[start (unix minutes), minutes, title, description?], ...] } }, an hour back and ten ahead.
 */
object Epg {
    private const val URL = "https://raw.githubusercontent.com/dor2500/israel-tv-plus/epg/epg.json"
    private var guide by mutableStateOf<Map<String, JsonArray>>(emptyMap())

    suspend fun load(http: OkHttpClient) = withContext(Dispatchers.IO) {
        runCatching {
            val req = Request.Builder().url(URL + "?t=" + System.currentTimeMillis() / 600_000).build()
            http.newCall(req).execute().use { r ->
                if (!r.isSuccessful) return@runCatching
                val root = Json.parseToJsonElement(r.body.string()).jsonObject
                guide = root["c"]?.jsonObject?.mapValues { it.value.jsonArray }.orEmpty()
            }
        }
    }

    /** The channel's programmes from the one on now (empty without a guide for it, or when it has a gap now). */
    fun programmes(id: String, nowMin: Long = nowMinute(), max: Int = 40): List<Programme> {
        val rows = guide[id] ?: return emptyList()
        val out = ArrayList<Programme>()
        for (row in rows) {
            val r = row.jsonArray
            val start = r[0].jsonPrimitive.long
            val end = start + r[1].jsonPrimitive.long
            if (end <= nowMin) continue
            out += Programme(start, end, r[2].jsonPrimitive.content, if (r.size > 3) r[3].jsonPrimitive.content else "")
            if (out.size >= max) break
        }
        return if (out.firstOrNull()?.let { it.start <= nowMin } == true) out else emptyList()
    }

    fun nowMinute() = System.currentTimeMillis() / 60_000
}

/** "18:30" in the device's time zone. */
fun hhmm(minute: Long): String {
    val c = Calendar.getInstance().apply { timeInMillis = minute * 60_000 }
    return "%02d:%02d".format(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE))
}

/** The current minute, ticking, so guide lines on screen follow the clock. */
@Composable
fun rememberNowMinute(): Long {
    val now by produceState(Epg.nowMinute()) {
        while (true) {
            delay(20_000)
            value = Epg.nowMinute()
        }
    }
    return now
}

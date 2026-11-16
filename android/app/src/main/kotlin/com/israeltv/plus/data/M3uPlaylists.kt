package com.israeltv.plus.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.util.UUID

@Serializable
data class M3uEntry(
    val id: String,
    val name: String,
    val url: String,
    val logo: String = "",
    val group: String = "",
    val chno: Int = 0,
    val isRadio: Boolean = false,
)

@Serializable
data class M3uPlaylist(
    val id: String,
    val name: String,
    val sourceType: String = "file",
    val url: String = "",
    val enabled: Boolean = true,
    val updatedAt: Long = System.currentTimeMillis(),
    val channels: List<M3uEntry> = emptyList(),
)

object M3uParser {
    private val STREAM_URL_RE = Regex("^(https?|rtmps?|rtsp|udp|mms)://", RegexOption.IGNORE_CASE)
    private val HLS_MANIFEST_RE = Regex("#EXT-X-TARGETDURATION|#EXT-X-MEDIA-SEQUENCE|#EXT-X-STREAM-INF", RegexOption.IGNORE_CASE)
    private val RADIO_RE = Regex("radio|רדיו|\\.(mp3|aac)(\\?|$)", RegexOption.IGNORE_CASE)

    private fun parseAttr(line: String, attr: String): String {
        val re = Regex("""$attr\s*=\s*(?:"([^"]*)"|'([^']*)'|([^\s,]+))""", RegexOption.IGNORE_CASE)
        val m = re.find(line) ?: return ""
        return (m.groupValues[1].ifEmpty { m.groupValues[2].ifEmpty { m.groupValues[3] } }).trim()
    }

    private fun parseName(extinfLine: String): String {
        var inQ = false
        var qChar = '\u0000'
        var lastComma = -1
        for (i in extinfLine.indices) {
            val c = extinfLine[i]
            if ((c == '"' || c == '\'') && (!inQ || qChar == c)) {
                inQ = !inQ
                qChar = if (inQ) c else '\u0000'
            } else if (c == ',' && !inQ) {
                lastComma = i
            }
        }
        return if (lastComma != -1) extinfLine.substring(lastComma + 1).trim() else ""
    }

    fun isSingleHlsManifest(text: String): Boolean = HLS_MANIFEST_RE.containsMatchIn(text)

    fun parse(text: String, playlistId: String): List<M3uEntry> {
        val clean = text.removePrefix("\uFEFF")
        val lines = clean.lines()
        val out = mutableListOf<M3uEntry>()
        var curName = ""
        var curLogo = ""
        var curGroup = ""
        var curChno = 0
        var hasExtinf = false

        for (raw in lines) {
            val line = raw.trim()
            if (line.isEmpty()) continue
            when {
                line.startsWith("#EXTINF:", ignoreCase = true) -> {
                    hasExtinf = true
                    curName = parseName(line).ifEmpty { parseAttr(line, "tvg-name") }
                    curLogo = parseAttr(line, "tvg-logo").takeIf { it.startsWith("http", ignoreCase = true) } ?: ""
                    val grp = parseAttr(line, "group-title")
                    if (grp.isNotEmpty()) curGroup = grp
                    curChno = parseAttr(line, "tvg-chno").toIntOrNull() ?: 0
                }
                line.startsWith("#EXTGRP:", ignoreCase = true) -> {
                    curGroup = line.substring(8).trim()
                }
                line.startsWith("#") -> continue
                STREAM_URL_RE.containsMatchIn(line) -> {
                    val idx = out.size
                    val name = (if (hasExtinf && curName.isNotEmpty()) curName else "ערוץ M3U ${idx + 1}")
                    val isRadio = RADIO_RE.containsMatchIn(curGroup) || RADIO_RE.containsMatchIn(line)
                    out.add(
                        M3uEntry(
                            id = "m3u_${playlistId}_$idx",
                            name = name,
                            url = line,
                            logo = if (hasExtinf) curLogo else "",
                            group = if (hasExtinf) curGroup else "",
                            chno = if (hasExtinf) curChno else 0,
                            isRadio = isRadio,
                        ),
                    )
                    hasExtinf = false
                    curName = ""
                    curLogo = ""
                    curChno = 0
                }
            }
        }
        return out
    }
}

class M3uStore(private val context: Context, private val http: OkHttpClient) {
    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }
    private val file = File(context.filesDir, "m3u_playlists.json")
    private var cached: List<M3uPlaylist>? = null

    fun load(): List<M3uPlaylist> {
        cached?.let { return it }
        val loaded = if (file.isFile) {
            runCatching { json.decodeFromString<List<M3uPlaylist>>(file.readText()) }.getOrDefault(emptyList())
        } else {
            emptyList()
        }
        cached = loaded
        return loaded
    }

    private fun save(list: List<M3uPlaylist>) {
        cached = list
        runCatching { file.writeText(json.encodeToString(list)) }
    }

    fun addFromText(
        text: String,
        name: String,
        sourceType: String = "file",
        url: String = "",
        existingId: String? = null,
    ): Result<M3uPlaylist> {
        val pid = existingId ?: UUID.randomUUID().toString().take(8)
        val plName = name.trim().ifEmpty { "רשימת M3U" }
        val entries = if (M3uParser.isSingleHlsManifest(text)) {
            if (sourceType == "url" && url.isNotBlank()) {
                listOf(M3uEntry(id = "m3u_${pid}_0", name = plName, url = url))
            } else {
                return Result.failure(IllegalArgumentException("זהו קובץ זרם HLS בודד (.m3u8) ולא רשימת ערוצים"))
            }
        } else {
            M3uParser.parse(text, pid)
        }
        if (entries.isEmpty()) {
            return Result.failure(IllegalArgumentException("לא נמצאו ערוצים תקינים בקובץ ה-M3U"))
        }
        val entry = M3uPlaylist(
            id = pid,
            name = plName,
            sourceType = sourceType,
            url = url,
            enabled = true,
            updatedAt = System.currentTimeMillis(),
            channels = entries,
        )
        val current = load().toMutableList()
        val idx = current.indexOfFirst { it.id == pid }
        if (idx >= 0) current[idx] = entry else current.add(entry)
        save(current)
        return Result.success(entry)
    }

    suspend fun addFromUrl(url: String, name: String = "", existingId: String? = null): Result<M3uPlaylist> =
        withContext(Dispatchers.IO) {
            val cleanUrl = url.trim()
            if (!cleanUrl.startsWith("http://", ignoreCase = true) && !cleanUrl.startsWith("https://", ignoreCase = true)) {
                return@withContext Result.failure(IllegalArgumentException("כתובת לא תקינה"))
            }
            val derivedName = name.trim().ifEmpty {
                runCatching {
                    val seg = java.net.URI(cleanUrl).path.trimEnd('/').substringAfterLast('/')
                    seg.replace(Regex("\\.(m3u8?|txt)$", RegexOption.IGNORE_CASE), "").ifEmpty { "רשימת M3U" }
                }.getOrDefault("רשימת M3U")
            }
            runCatching {
                val req = Request.Builder().url(cleanUrl).header("User-Agent", USER_AGENT).build()
                http.newCall(req).execute().use { resp ->
                    if (!resp.isSuccessful) error("HTTP ${resp.code}")
                    val body = resp.body.string()
                    addFromText(body, derivedName, sourceType = "url", url = cleanUrl, existingId = existingId).getOrThrow()
                }
            }
        }

    fun toggle(id: String) {
        val next = load().map { if (it.id == id) it.copy(enabled = !it.enabled) else it }
        save(next)
    }

    fun delete(id: String) {
        save(load().filterNot { it.id == id })
    }

    fun clearAll() {
        save(emptyList())
    }

    fun toChannels(baseNumber: Int): List<Channel> {
        var num = maxOf(baseNumber, 999)
        val out = mutableListOf<Channel>()
        for (pl in load()) {
            if (!pl.enabled) continue
            for (e in pl.channels) {
                num++
                val cat = if (e.group.isNotEmpty()) "📋 ${e.group} (M3U)" else "📋 ${pl.name} (M3U)"
                out.add(
                    Channel(
                        id = e.id,
                        name = e.name,
                        category = cat,
                        categoryEn = if (e.isRadio) "radio" else "m3u",
                        url = e.url,
                        logo = e.logo,
                        badge = e.group.ifEmpty { "M3U" },
                        desc = if (e.group.isNotEmpty()) "${e.group} · ${pl.name}" else "רשימת M3U: ${pl.name}",
                        number = if (e.chno > 0) e.chno else num,
                        lang = "he",
                        kind = if (e.isRadio) "radio" else "tv",
                    ),
                )
            }
        }
        return out
    }
}

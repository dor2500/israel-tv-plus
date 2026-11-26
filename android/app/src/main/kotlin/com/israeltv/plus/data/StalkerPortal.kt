package com.israeltv.plus.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.net.URLEncoder
import java.util.UUID

@Serializable
data class StalkerChannelEntry(
    val id: String,
    val name: String,
    val cmd: String,
    val logo: String = "",
    val genre: String = "",
    val chno: Int = 0,
    val archiveDays: Int = 0,
)

@Serializable
data class StalkerPortalAccount(
    val id: String,
    val name: String,
    val portalUrl: String,
    val mac: String,
    val enabled: Boolean = true,
    val updatedAt: Long = System.currentTimeMillis(),
    val channels: List<StalkerChannelEntry> = emptyList(),
)

/**
 * Standard Stalker / Ministra Portal (MAG/MAC) client.
 * Stores user-entered portals locally on the device only; never embeds credentials.
 */
object StalkerApi {
    private const val STB_UA = "Mozilla/5.0 (QtEmbedded; U; Linux; C) AppleWebKit/533.3 (KHTML, like Gecko) MAG200 stbapp ver: 2 rev: 250 Safari/533.3"

    fun normalizePortalBase(raw: String): String {
        var u = raw.trim().trimEnd('/')
        if (!u.startsWith("http://", ignoreCase = true) && !u.startsWith("https://", ignoreCase = true)) {
            u = "http://$u"
        }
        return u.replace(Regex("/c/?$", RegexOption.IGNORE_CASE), "")
            .replace(Regex("/portal\\.php$", RegexOption.IGNORE_CASE), "")
    }

    fun normalizeMac(raw: String): String =
        raw.trim().uppercase().replace("-", ":")

    private fun apiCandidates(base: String): List<String> = listOf(
        "$base/portal.php",
        "$base/server/load.php",
        "$base/stalker_portal/server/load.php",
    )

    private fun execGet(http: OkHttpClient, url: String, mac: String, token: String = ""): String {
        val req = Request.Builder()
            .url(url)
            .header("User-Agent", STB_UA)
            .header("X-User-Agent", "Model: MAG250; Link: WiFi")
            .header("Cookie", "mac=${URLEncoder.encode(mac, "UTF-8")}; stb_lang=en; timezone=Asia%2FJerusalem")
            .apply { if (token.isNotEmpty()) header("Authorization", "Bearer $token") }
            .build()
        http.newCall(req).execute().use { resp ->
            if (!resp.isSuccessful) error("HTTP ${resp.code}")
            return resp.body.string()
        }
    }

    private fun handshake(http: OkHttpClient, base: String, mac: String): Pair<String, String> {
        var lastErr: Throwable? = null
        for (ep in apiCandidates(base)) {
            try {
                val body = execGet(http, "$ep?type=stb&action=handshake&token=&JsHttpRequest=1-xml", mac)
                val js = JSONObject(body).optJSONObject("js")
                val tok = js?.optString("token").orEmpty()
                if (tok.isNotEmpty()) return ep to tok
            } catch (t: Throwable) {
                lastErr = t
            }
        }
        throw lastErr ?: IllegalStateException("לא ניתן להתחבר לפורטל (handshake נכשל)")
    }

    fun fetchChannels(http: OkHttpClient, portalUrl: String, macRaw: String): List<StalkerChannelEntry> {
        val base = normalizePortalBase(portalUrl)
        val mac = normalizeMac(macRaw)
        val (ep, token) = handshake(http, base, mac)
        runCatching { execGet(http, "$ep?type=stb&action=get_profile&JsHttpRequest=1-xml", mac, token) }

        val genres = mutableMapOf<String, String>()
        runCatching {
            val gBody = execGet(http, "$ep?type=itv&action=get_genres&JsHttpRequest=1-xml", mac, token)
            val arr = JSONObject(gBody).optJSONArray("js") ?: JSONArray()
            for (i in 0 until arr.length()) {
                val o = arr.optJSONObject(i) ?: continue
                val gid = o.optString("id")
                val title = o.optString("title")
                if (gid.isNotEmpty() && title.isNotEmpty()) genres[gid] = title
            }
        }

        val chBody = execGet(http, "$ep?type=itv&action=get_all_channels&JsHttpRequest=1-xml", mac, token)
        val js = JSONObject(chBody).optJSONObject("js")
        val data = js?.optJSONArray("data") ?: JSONArray()
        val out = mutableListOf<StalkerChannelEntry>()
        for (i in 0 until data.length()) {
            val o = data.optJSONObject(i) ?: continue
            val cid = o.optString("id").ifEmpty { i.toString() }
            val name = o.optString("name").trim()
            val cmd = o.optString("cmd").trim()
            if (name.isEmpty() || cmd.isEmpty()) continue
            val logoRaw = o.optString("logo").trim()
            val logo = when {
                logoRaw.startsWith("http", ignoreCase = true) -> logoRaw
                logoRaw.isNotEmpty() -> "$base/stalker_portal/misc/logos/320/$logoRaw"
                else -> ""
            }
            val genre = genres[o.optString("tv_genre_id")] ?: "Stalker IPTV"
            val chno = o.optString("number").toIntOrNull() ?: 0
            val archiveHours = o.optInt("tv_archive_duration", 0)
            val archiveDays = if (o.optInt("tv_archive", 0) == 1) (archiveHours / 24).coerceAtLeast(1) else 0
            out.add(StalkerChannelEntry(cid, name, cmd, logo, genre, chno, archiveDays))
        }
        return out
    }

    /** Resolves a `stalker://<b64_portal>|<mac>|<b64_cmd>` synthetic URL into a playable direct stream URL. */
    fun resolveLink(http: OkHttpClient, syntheticUrl: String): String {
        val parts = syntheticUrl.removePrefix("stalker://").split("|")
        if (parts.size < 3) error("invalid stalker url")
        val base = String(android.util.Base64.decode(parts[0], android.util.Base64.URL_SAFE or android.util.Base64.NO_WRAP))
        val mac = parts[1]
        val cmd = String(android.util.Base64.decode(parts[2], android.util.Base64.URL_SAFE or android.util.Base64.NO_WRAP))

        // If cmd already contains a direct http(s) URL (non-ffmpeg placeholder), return it directly
        val directMatch = Regex("""https?://\S+""").find(cmd)?.value
        if (directMatch != null && !directMatch.contains("localhost", ignoreCase = true) && !cmd.contains("stream=", ignoreCase = true)) {
            return directMatch
        }

        val (ep, token) = handshake(http, base, mac)
        val encCmd = URLEncoder.encode(cmd, "UTF-8")
        val body = execGet(http, "$ep?type=itv&action=create_link&cmd=$encCmd&JsHttpRequest=1-xml", mac, token)
        val raw = JSONObject(body).optJSONObject("js")?.optString("cmd").orEmpty().trim()
        val resolved = Regex("""https?://\S+""").find(raw)?.value ?: directMatch
        return resolved ?: error("הפורטל לא החזיר קישור שידור")
    }

    fun encodeSyntheticUrl(portalUrl: String, mac: String, cmd: String): String {
        val b64Portal = android.util.Base64.encodeToString(
            normalizePortalBase(portalUrl).toByteArray(Charsets.UTF_8),
            android.util.Base64.URL_SAFE or android.util.Base64.NO_WRAP,
        )
        val b64Cmd = android.util.Base64.encodeToString(
            cmd.toByteArray(Charsets.UTF_8),
            android.util.Base64.URL_SAFE or android.util.Base64.NO_WRAP,
        )
        return "stalker://$b64Portal|${normalizeMac(mac)}|$b64Cmd"
    }
}

class StalkerStore(private val context: Context, private val http: OkHttpClient) {
    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }
    private val file = File(context.filesDir, "stalker_portals.json")

    fun load(): List<StalkerPortalAccount> {
        if (!file.isFile) return emptyList()
        return runCatching {
            json.decodeFromString<List<StalkerPortalAccount>>(file.readText())
        }.getOrDefault(emptyList())
    }

    fun save(list: List<StalkerPortalAccount>) {
        runCatching { file.writeText(json.encodeToString(list)) }
    }

    suspend fun addOrRefresh(
        portalUrl: String,
        mac: String,
        name: String = "",
        existingId: String? = null,
    ): Result<StalkerPortalAccount> = withContext(Dispatchers.IO) {
        runCatching {
            val cleanUrl = StalkerApi.normalizePortalBase(portalUrl)
            val cleanMac = StalkerApi.normalizeMac(mac)
            if (!Regex("""^([0-9A-F]{2}:){5}[0-9A-F]{2}$""").matches(cleanMac)) {
                error("כתובת MAC לא תקינה (לדוגמה 00:1A:79:XX:XX:XX)")
            }
            val chans = StalkerApi.fetchChannels(http, cleanUrl, cleanMac)
            if (chans.isEmpty()) error("לא נמצאו ערוצים בפורטל זה")
            val host = runCatching { java.net.URI(cleanUrl).host ?: "Stalker" }.getOrDefault("Stalker")
            val label = name.trim().ifEmpty { host }
            val id = existingId ?: ("st_" + UUID.randomUUID().toString().take(8))
            val acct = StalkerPortalAccount(id, label, cleanUrl, cleanMac, true, System.currentTimeMillis(), chans)
            val cur = load().toMutableList()
            val idx = cur.indexOfFirst { it.id == id }
            if (idx >= 0) cur[idx] = acct else cur.add(acct)
            save(cur)
            acct
        }
    }

    fun toggle(id: String) {
        save(load().map { if (it.id == id) it.copy(enabled = !it.enabled) else it })
    }

    fun delete(id: String) {
        save(load().filterNot { it.id == id })
    }

    fun toChannels(startNumber: Int): List<Channel> {
        var num = startNumber
        val out = mutableListOf<Channel>()
        for (acct in load()) {
            if (!acct.enabled) continue
            for (c in acct.channels) {
                num++
                val catName = "📡 ${c.genre.ifBlank { acct.name }} (Stalker)"
                out.add(
                    Channel(
                        id = "stalker_${acct.id}_${c.id}",
                        name = c.name,
                        category = catName,
                        categoryEn = "stalker",
                        url = StalkerApi.encodeSyntheticUrl(acct.portalUrl, acct.mac, c.cmd),
                        logo = c.logo,
                        badge = "IPTV",
                        desc = "פורטל Stalker: ${acct.name}",
                        number = if (c.chno > 0) c.chno else num,
                        kind = "tv",
                        catchupDays = c.archiveDays,
                    )
                )
            }
        }
        return out
    }
}

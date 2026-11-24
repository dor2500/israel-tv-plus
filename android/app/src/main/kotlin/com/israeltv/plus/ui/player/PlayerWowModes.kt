package com.israeltv.plus.ui.player

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import org.json.JSONObject
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.util.Calendar
import java.util.Locale
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

/**
 * 1. Retro 90s CRT TV Overlay: horizontal scanlines + phosphor vignette + glowing green PAL badge.
 */
@Composable
fun RetroCrtOverlay(channelLabel: String, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize()) {
        Canvas(Modifier.fillMaxSize()) {
            // Subtle warm tint + horizontal scanlines
            val step = 6f
            var y = 0f
            while (y < size.height) {
                drawLine(
                    color = Color.Black.copy(alpha = 0.24f),
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 2.2f,
                )
                y += step
            }
            // Radial phosphor vignette
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.62f)),
                    center = Offset(size.width / 2f, size.height / 2f),
                    radius = size.maxDimension * 0.68f,
                )
            )
        }
        Text(
            text = "$channelLabel • PAL",
            color = Color(0xFF39FF14),
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 18.sp,
            modifier = Modifier
                .align(Alignment.TopStart)
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 18.dp, vertical = 14.dp),
        )
    }
}

/**
 * 2. Live Israeli Breaking News Ticker (Ynet RSS) docked cleanly at the top edge.
 */
@Composable
fun LiveNewsTickerBar(modifier: Modifier = Modifier) {
    var tickerText by remember { mutableStateOf("טוען מבזקי חדשות בזמן אמת...") }
    LaunchedEffect(Unit) {
        while (isActive) {
            val fetched = withContext(Dispatchers.IO) { fetchYnetBreakingHeadlines() }
            if (fetched.isNotEmpty()) {
                tickerText = fetched.joinToString("   ✦   ")
            }
            delay(180_000)
        }
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .height(32.dp)
            .background(Color(0xE60B101C))
            .border(0.5.dp, Color(0xFFEF4444).copy(alpha = 0.7f)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .fillMaxHeight()
                .background(Color(0xFFDC2626))
                .padding(horizontal = 10.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text("🔴 מבזקים", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
        }
        Text(
            text = tickerText,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 10.dp)
                .basicMarquee(iterations = Int.MAX_VALUE, velocity = 48.dp),
        )
    }
}

private fun fetchYnetBreakingHeadlines(): List<String> = runCatching {
    val conn = (URL("https://www.ynet.co.il/Integration/StoryRss1854.xml").openConnection() as HttpURLConnection).apply {
        connectTimeout = 6_000
        readTimeout = 6_000
        setRequestProperty("User-Agent", "Mozilla/5.0")
    }
    val xml = conn.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
    val regex = Regex("<item>.*?<title>(?:<!\\[CDATA\\[)?(.*?)(?:\\]\\]>)?</title>", setOf(RegexOption.DOT_MATCHES_ALL))
    regex.findAll(xml)
        .map { it.groupValues[1].replace("&quot;", "\"").replace("&amp;", "&").replace("&#39;", "'").trim() }
        .filter { it.isNotBlank() }
        .take(12)
        .toList()
}.getOrDefault(emptyList())

/**
 * 3. Live Stream Telemetry HUD (Stats for Nerds).
 */
@Composable
fun TelemetryHudOverlay(player: ExoPlayer, channelName: String, modifier: Modifier = Modifier) {
    var resolution by remember { mutableStateOf("טוען...") }
    var bitrate by remember { mutableStateOf("—") }
    var fps by remember { mutableStateOf("—") }
    var bufferSec by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(player) {
        while (isActive) {
            val vf = player.videoFormat
            resolution = if (vf != null && vf.width > 0) "${vf.width}×${vf.height}" else "אודיו / טוען"
            bitrate = if (vf != null && vf.bitrate > 0) String.format(Locale.US, "%.2f Mbps", vf.bitrate / 1_000_000f) else "אוטומטי"
            fps = if (vf != null && vf.frameRate > 0f) String.format(Locale.US, "%.0f fps", vf.frameRate) else "—"
            bufferSec = ((player.bufferedPosition - player.currentPosition).coerceAtLeast(0L) / 1000f)
            delay(1_000)
        }
    }

    val bufFrac = (bufferSec / 30f).coerceIn(0f, 1f)
    Column(
        modifier = modifier
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(top = 54.dp, start = 14.dp, end = 14.dp)
            .width(210.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xCC060B16))
            .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.42f), RoundedCornerShape(14.dp))
            .padding(10.dp),
    ) {
        Text("⚡ TELEMETRY HUD", color = Color(0xFF38BDF8), fontFamily = FontFamily.Monospace, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
        Spacer(Modifier.height(5.dp))
        HudRow("ערוץ", channelName.take(18))
        HudRow("רזולוציה", resolution)
        HudRow("קצב נתונים", bitrate)
        HudRow("קצב פריימים", fps)
        HudRow("באפר חי", String.format(Locale.US, "%.1fs", bufferSec))
        Spacer(Modifier.height(6.dp))
        Box(
            Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color.White.copy(alpha = 0.14f)),
        ) {
            Box(
                Modifier
                    .fillMaxWidth(bufFrac)
                    .fillMaxHeight()
                    .background(Color(0xFF34D399)),
            )
        }
    }
}

@Composable
private fun HudRow(k: String, v: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 1.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(k, color = Color(0xFF94A3B8), fontSize = 10.sp)
        Text(v, color = Color.White, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 10.sp, maxLines = 1)
    }
}

/**
 * 4. Stealth / Boss Key Overlay: instant mute + minimal dim clock screen.
 */
@Composable
fun StealthClockOverlay(onDismiss: () -> Unit) {
    var timeStr by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        while (isActive) {
            val c = Calendar.getInstance()
            timeStr = String.format(Locale.US, "%02d:%02d", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE))
            delay(15_000)
        }
    }
    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0xFF03050A))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(timeStr, color = Color.White.copy(alpha = 0.78f), fontSize = 64.sp, fontWeight = FontWeight.Thin)
            Spacer(Modifier.height(10.dp))
            Text("מצב השהיה שקט — הקש בכל מקום לחזרה לשידור", color = Color.White.copy(alpha = 0.35f), fontSize = 12.sp)
        }
    }
}

/**
 * 5. Pure-Kotlin Procedural Ambient Soundscape Generator (Rain / Ocean / Deep Space).
 */
enum class AmbientSoundMode(val label: String) {
    OFF("כבוי"),
    RAIN("🌧️ גשם עדין"),
    OCEAN("🌊 גלי ים"),
    SPACE("🌌 חלל עמוק");

    fun next(): AmbientSoundMode = entries[(ordinal + 1) % entries.size]
}

@Composable
fun AmbientSoundEffect(mode: AmbientSoundMode) {
    LaunchedEffect(mode) {
        if (mode == AmbientSoundMode.OFF) return@LaunchedEffect
        withContext(Dispatchers.IO) {
            val sampleRate = 22050
            val minBuf = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
            ).coerceAtLeast(4096)
            val track = runCatching {
                AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(minBuf * 2)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()
            }.getOrNull() ?: return@withContext

            try {
                track.play()
                val pcm = ShortArray(2048)
                var last = 0f
                var phase = 0.0
                val alpha = when (mode) {
                    AmbientSoundMode.RAIN -> 0.22f
                    AmbientSoundMode.OCEAN -> 0.06f
                    AmbientSoundMode.SPACE -> 0.025f
                    AmbientSoundMode.OFF -> 0f
                }
                val gain = when (mode) {
                    AmbientSoundMode.RAIN -> 0.14f
                    AmbientSoundMode.OCEAN -> 0.20f
                    AmbientSoundMode.SPACE -> 0.24f
                    AmbientSoundMode.OFF -> 0f
                }
                while (isActive) {
                    for (i in pcm.indices) {
                        val white = Random.nextFloat() * 2f - 1f
                        last = last + alpha * (white - last)
                        val mod = if (mode == AmbientSoundMode.OCEAN) {
                            (0.55 + 0.45 * sin(phase)).toFloat().also { phase += 2.0 * PI * 0.12 / sampleRate }
                        } else 1f
                        val sample = (last * gain * mod * 32767f).toInt().coerceIn(-32767, 32767)
                        pcm[i] = sample.toShort()
                    }
                    track.write(pcm, 0, pcm.size)
                }
            } finally {
                runCatching { track.stop() }
                runCatching { track.release() }
            }
        }
    }
}

/**
 * 6. Live Sports Scoreboard HUD (Fetches real-time soccer match scores from ESPN open JSON API).
 */
@Composable
fun LiveSportsScoreboardHud(modifier: Modifier = Modifier) {
    var matches by remember { mutableStateOf<List<Triple<String, String, String>>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        while (isActive) {
            val fetched = withContext(Dispatchers.IO) { fetchLiveSoccerScores() }
            matches = fetched
            loading = false
            delay(60_000)
        }
    }

    Column(
        modifier = modifier
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(top = 54.dp, start = 14.dp, end = 14.dp)
            .width(235.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xD907101E))
            .border(1.dp, Color(0xFF22C55E).copy(alpha = 0.45f), RoundedCornerShape(14.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text("⚽ לוח תוצאות ספורט חי", color = Color(0xFF4ADE80), fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
        if (loading) {
            Text("טוען משחקים חיים...", color = Color(0xFF94A3B8), fontSize = 11.sp)
        } else if (matches.isEmpty()) {
            Text("אין משחקים משודרים כרגע", color = Color(0xFF94A3B8), fontSize = 11.sp)
        } else {
            matches.take(5).forEach { (teams, score, status) ->
                Row(
                    Modifier.fillMaxWidth().padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(teams, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, modifier = Modifier.weight(1f))
                    Spacer(Modifier.width(6.dp))
                    Text(score, color = Color(0xFFFACC15), fontFamily = FontFamily.Monospace, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                    Spacer(Modifier.width(5.dp))
                    Text(status, color = Color(0xFF94A3B8), fontSize = 9.sp)
                }
            }
        }
    }
}

private fun fetchLiveSoccerScores(): List<Triple<String, String, String>> = runCatching {
    val conn = (URL("https://site.api.espn.com/apis/site/v2/sports/soccer/all/scoreboard").openConnection() as HttpURLConnection).apply {
        connectTimeout = 6_000
        readTimeout = 6_000
        setRequestProperty("User-Agent", "Mozilla/5.0")
    }
    val raw = conn.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
    val root = JSONObject(raw)
    val events = root.optJSONArray("events") ?: return emptyList()
    val list = mutableListOf<Triple<String, String, String>>()
    for (i in 0 until minOf(events.length(), 8)) {
        val ev = events.optJSONObject(i) ?: continue
        val comp = ev.optJSONArray("competitions")?.optJSONObject(0) ?: continue
        val competitors = comp.optJSONArray("competitors") ?: continue
        if (competitors.length() < 2) continue
        val c0 = competitors.getJSONObject(0)
        val c1 = competitors.getJSONObject(1)
        val t0 = c0.optJSONObject("team")?.optString("abbreviation").orEmpty().ifBlank {
            c0.optJSONObject("team")?.optString("shortDisplayName").orEmpty()
        }
        val t1 = c1.optJSONObject("team")?.optString("abbreviation").orEmpty().ifBlank {
            c1.optJSONObject("team")?.optString("shortDisplayName").orEmpty()
        }
        val s0 = c0.optString("score", "0")
        val s1 = c1.optString("score", "0")
        val st = ev.optJSONObject("status")?.optJSONObject("type")?.optString("shortDetail").orEmpty()
        list.add(Triple("$t0 - $t1", "$s0:$s1", st.take(8)))
    }
    list
}.getOrDefault(emptyList())

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WowModesSheet(
    retroCrt: Boolean,
    newsTicker: Boolean,
    telemetryHud: Boolean,
    sportsBoard: Boolean,
    autoTour: Boolean,
    varZoom: Float,
    nightDim: Float,
    ambientMode: AmbientSoundMode,
    onToggleRetroCrt: () -> Unit,
    onToggleNewsTicker: () -> Unit,
    onToggleTelemetryHud: () -> Unit,
    onToggleSportsBoard: () -> Unit,
    onToggleAutoTour: () -> Unit,
    onCycleVarZoom: () -> Unit,
    onCycleNightDim: () -> Unit,
    onActivateTouchLock: () -> Unit,
    onCycleAmbient: () -> Unit,
    onActivateStealth: () -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                "✨ מצבי WOW ואופציות מיוחדות",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            WowRow("⚽ לוח תוצאות כדורגל וספורט חי", if (sportsBoard) "פועל" else "כבוי", sportsBoard, onToggleSportsBoard)
            WowRow("🔍 זום שידור חי (VAR Super-Zoom)", if (varZoom > 1.05f) "${varZoom}x" else "רגיל (1x)", varZoom > 1.05f, onCycleVarZoom)
            WowRow("⚡ סיור ערוצים אוטומטי (כל 12 שניות)", if (autoTour) "פועל" else "כבוי", autoTour, onToggleAutoTour)
            WowRow("🌑 עמעום לילה עמוק (Extra Dimmer)", if (nightDim > 0f) "${(nightDim * 100).toInt()}%" else "כבוי", nightDim > 0f, onCycleNightDim)
            WowRow("🔒 נעילת מסך למניעת נגיעות (ילדים/כיס)", "נעל מסך", false) {
                onDismiss()
                onActivateTouchLock()
            }
            WowRow("📺 מצב רטרו שנות ה-90 (CRT)", if (retroCrt) "פועל" else "כבוי", retroCrt, onToggleRetroCrt)
            WowRow("📰 פס מבזקי חדשות חי (על כל ערוץ)", if (newsTicker) "פועל" else "כבוי", newsTicker, onToggleNewsTicker)
            WowRow("🎧 מיקסר צלילי רקע ואווירה", ambientMode.label, ambientMode != AmbientSoundMode.OFF, onCycleAmbient)
            WowRow("📊 מד דופק שידור חי (Telemetry HUD)", if (telemetryHud) "פועל" else "כבוי", telemetryHud, onToggleTelemetryHud)
            WowRow("🤫 מצב בוס / השתקת מסך מיידית", "הפעל", false) {
                onDismiss()
                onActivateStealth()
            }
            Spacer(Modifier.height(10.dp))
        }
    }
}

@Composable
private fun WowRow(title: String, status: String, active: Boolean, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (active) MaterialTheme.colorScheme.primary.copy(alpha = 0.22f)
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
            .border(
                1.dp,
                if (active) MaterialTheme.colorScheme.primary.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.08f),
                RoundedCornerShape(14.dp),
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 13.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(title, color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        Text(
            status,
            color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

import os

p = r"C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo\android\app\src\main\kotlin\com\israeltv\plus\ui\player\PlayerControls.kt"
with open(p, "r", encoding="utf-8") as f:
    c = f.read()

c = c.replace("\r\n", "\n")

# 1. Imports
imports = """import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.Forward10
import androidx.compose.material.icons.rounded.Replay10
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults"""

if "import androidx.compose.material.icons.rounded.Bedtime" not in c:
    c = c.replace(
        "import androidx.compose.material.icons.rounded.Tv\n",
        "import androidx.compose.material.icons.rounded.Tv\n" + imports + "\n"
    )

# 2. Add time format helper
time_helper = """
internal fun formatTime(ms: Long): String {
    val totalSec = (ms / 1000).coerceAtLeast(0)
    val h = totalSec / 3600
    val m = (totalSec % 3600) / 60
    val s = totalSec % 60
    return if (h > 0) "%d:%02d:%02d".format(h, m, s) else "%02d:%02d".format(m, s)
}
"""

if "internal fun formatTime" not in c:
    c = c.replace("internal enum class VideoFit", time_helper + "\ninternal enum class VideoFit")

# 3. Add PlaybackSpeedDialog and SleepTimerDialog
dialogs = """
@Composable
internal fun PlaybackSpeedDialog(
    currentSpeed: Float,
    onSelectSpeed: (Float) -> Unit,
    onDismiss: () -> Unit,
) {
    val speeds = listOf(0.75f, 1.0f, 1.25f, 1.5f, 1.75f, 2.0f)
    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(28.dp), color = MaterialTheme.colorScheme.surfaceContainerHigh) {
            Column(Modifier.padding(vertical = 24.dp, horizontal = 8.dp)) {
                Text(
                    "מהירות צפייה",
                    Modifier.padding(horizontal = 16.dp).padding(bottom = 16.dp),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.headlineSmall,
                )
                speeds.forEach { sp ->
                    val label = if (sp == 1.0f) "1.0x (רגיל)" else "${sp}x"
                    QualityRow(label, null, currentSpeed == sp) {
                        onSelectSpeed(sp)
                        onDismiss()
                    }
                }
            }
        }
    }
}

@Composable
internal fun SleepTimerDialog(
    currentMinutes: Int,
    onSelectMinutes: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val options = listOf(
        0 to "כבוי",
        15 to "15 דקות",
        30 to "30 דקות",
        45 to "45 דקות",
        60 to "שעה (60 דקות)",
        90 to "שעה וחצי (90 דקות)",
        120 to "שעתיים (120 דקות)",
    )
    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(28.dp), color = MaterialTheme.colorScheme.surfaceContainerHigh) {
            Column(Modifier.padding(vertical = 24.dp, horizontal = 8.dp)) {
                Text(
                    "טיימר שינה (כיבוי אוטומטי)",
                    Modifier.padding(horizontal = 16.dp).padding(bottom = 16.dp),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.headlineSmall,
                )
                options.forEach { (mins, label) ->
                    QualityRow(label, null, currentMinutes == mins) {
                        onSelectMinutes(mins)
                        onDismiss()
                    }
                }
            }
        }
    }
}
"""

if "internal fun PlaybackSpeedDialog" not in c:
    c = c.replace("@Composable\ninternal fun ResolutionDialog", dialogs + "\n@Composable\ninternal fun ResolutionDialog")

# 4. Update PlayerOptionsDrawer signature and body
old_drawer_sig = """internal fun PlayerOptionsDrawer(
    visible: Boolean,
    qualityLabel: String,
    serverIndex: Int,
    serverCount: Int,
    onSelectServer: (Int) -> Unit,
    onQuality: () -> Unit,
    onAudio: () -> Unit,
    onPip: () -> Unit,
    onMultiView: () -> Unit,
    onChannels: () -> Unit,
    onRotate: () -> Unit,
    onDismiss: () -> Unit,
) {"""

new_drawer_sig = """internal fun PlayerOptionsDrawer(
    visible: Boolean,
    qualityLabel: String,
    playbackSpeed: Float = 1.0f,
    sleepTimerMinutes: Int = 0,
    serverIndex: Int,
    serverCount: Int,
    onSelectServer: (Int) -> Unit,
    onQuality: () -> Unit,
    onAudio: () -> Unit,
    onSpeed: () -> Unit = {},
    onSleepTimer: () -> Unit = {},
    onPip: () -> Unit,
    onMultiView: () -> Unit,
    onChannels: () -> Unit,
    onRotate: () -> Unit,
    onDismiss: () -> Unit,
) {"""

assert old_drawer_sig in c, "old_drawer_sig not found"
c = c.replace(old_drawer_sig, new_drawer_sig)

old_drawer_items = """                        DrawerItem(Icons.Rounded.Audiotrack, "שמע", onClick = onAudio)
                        DrawerItem(Icons.Rounded.PictureInPictureAlt, "חלון צף", onClick = onPip)
                        DrawerItem(Icons.Rounded.HighQuality, "איכות וידאו", qualityLabel, onQuality)
                        DrawerItem(Icons.Rounded.GridView, "פיצול מסך", onClick = onMultiView)"""

new_drawer_items = """                        DrawerItem(Icons.Rounded.Audiotrack, "שמע", onClick = onAudio)
                        DrawerItem(Icons.Rounded.Speed, "מהירות צפייה", "${playbackSpeed}x", onClick = onSpeed)
                        DrawerItem(Icons.Rounded.Bedtime, "טיימר שינה", if (sleepTimerMinutes > 0) "$sleepTimerMinutes דק'" else "כבוי", onClick = onSleepTimer)
                        DrawerItem(Icons.Rounded.PictureInPictureAlt, "חלון צף", onClick = onPip)
                        DrawerItem(Icons.Rounded.HighQuality, "איכות וידאו", qualityLabel, onQuality)
                        DrawerItem(Icons.Rounded.GridView, "פיצול מסך", onClick = onMultiView)"""

assert old_drawer_items in c, "old_drawer_items not found"
c = c.replace(old_drawer_items, new_drawer_items)

# 5. Update PlayerBottomBar signature and body for VOD timeline
old_bottom_sig = """internal fun PlayerBottomBar(
    ch: Channel,
    fit: VideoFit,
    onCycleFit: () -> Unit,
    onOptions: () -> Unit,
) {"""

new_bottom_sig = """internal fun PlayerBottomBar(
    ch: Channel,
    fit: VideoFit,
    positionMs: Long = 0L,
    durationMs: Long = 0L,
    onSeek: (Long) -> Unit = {},
    onCycleFit: () -> Unit,
    onOptions: () -> Unit,
) {"""

assert old_bottom_sig in c, "old_bottom_sig not found"
c = c.replace(old_bottom_sig, new_bottom_sig)

old_bottom_row = """    Row(
        Modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.82f))))
            .navigationBarsPadding()
            .windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal))
            .padding(start = d.playerEdge, end = d.playerEdge, top = 32.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {"""

new_bottom_row = """    val isVod = ch.kind == "vod" || ch.categoryEn == "vod"
    Column(
        Modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.88f))))
            .navigationBarsPadding()
            .windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal))
            .padding(start = d.playerEdge, end = d.playerEdge, top = 20.dp, bottom = 16.dp),
    ) {
        if (isVod && durationMs > 0L) {
            Row(
                Modifier.fillMaxWidth().padding(bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    formatTime(positionMs),
                    color = Color.White.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.labelSmall
                )
                Slider(
                    value = (positionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f),
                    onValueChange = { frac ->
                        onSeek((frac * durationMs).toLong())
                    },
                    modifier = Modifier.weight(1f).height(24.dp),
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = Color.White.copy(alpha = 0.28f)
                    )
                )
                Text(
                    formatTime(durationMs),
                    color = Color.White.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {"""

assert old_bottom_row in c, "old_bottom_row not found"
c = c.replace(old_bottom_row, new_bottom_row)

# add closing brace for the outer Column in PlayerBottomBar
old_bottom_end = """            MorphAction(Icons.Rounded.MoreHoriz, "אפשרויות", onClick = onOptions)
        }
    }
}"""

new_bottom_end = """            MorphAction(Icons.Rounded.MoreHoriz, "אפשרויות", onClick = onOptions)
        }
    }
}
}"""

assert old_bottom_end in c, "old_bottom_end not found"
c = c.replace(old_bottom_end, new_bottom_end)

with open(p, "w", encoding="utf-8") as f:
    f.write(c)

print("PlayerControls.kt updated with Speed, Sleep Timer & VOD Timeline successfully!")


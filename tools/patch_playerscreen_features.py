import os

p = r"C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo\android\app\src\main\kotlin\com\israeltv\plus\ui\player\PlayerScreen.kt"
with open(p, "r", encoding="utf-8") as f:
    c = f.read()

c = c.replace("\r\n", "\n")

# 1. Imports
imports = """import androidx.compose.material.icons.rounded.Forward10
import androidx.compose.material.icons.rounded.Replay10
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf"""

if "import androidx.compose.material.icons.rounded.Forward10" not in c:
    c = c.replace(
        "import androidx.compose.material.icons.rounded.PlayArrow\n",
        "import androidx.compose.material.icons.rounded.PlayArrow\n" + imports + "\n"
    )

# 2. State variables inside PlayerScreen
old_states = """    var fit by remember { mutableStateOf(VideoFit.Fit) }
    var statusToast by remember(current.id) { mutableStateOf<String?>(null) }"""

new_states = """    var fit by remember { mutableStateOf(VideoFit.Fit) }
    var statusToast by remember(current.id) { mutableStateOf<String?>(null) }
    var playbackSpeed by remember { mutableFloatStateOf(1.0f) }
    var sleepTimerEndMs by remember { mutableLongStateOf(0L) }
    var sleepTimerMinutes by remember { mutableIntStateOf(0) }
    var currentPositionMs by remember { mutableLongStateOf(0L) }
    var durationMs by remember { mutableLongStateOf(0L) }"""

assert old_states in c, "old_states not found"
c = c.replace(old_states, new_states)

# 3. Position tracking loop & VOD resume
loop_code = """
    // Position tracking, VOD resume, and sleep timer loop
    LaunchedEffect(playing, current) {
        val isVod = current.kind == "vod" || current.categoryEn == "vod"
        if (isVod) {
            val sp = context.getSharedPreferences("israel_tv_prefs", Context.MODE_PRIVATE)
            val savedPos = sp.getLong("itv_vod_pos_" + current.id, 0L)
            if (savedPos > 10_000L) {
                delay(800)
                if (player.duration > 0 && savedPos < player.duration - 20_000L) {
                    player.seekTo(savedPos)
                    statusToast = "ממשיך מצפייה קודמת"
                }
            }
        }

        while (true) {
            delay(1000)
            if (player.isPlaying) {
                currentPositionMs = player.currentPosition
                durationMs = if (player.duration > 0) player.duration else 0L

                if (isVod && currentPositionMs > 5_000L) {
                    val sp = context.getSharedPreferences("israel_tv_prefs", Context.MODE_PRIVATE)
                    sp.edit().putLong("itv_vod_pos_" + current.id, currentPositionMs).apply()
                }

                if (sleepTimerEndMs > 0L) {
                    val remainingMs = sleepTimerEndMs - System.currentTimeMillis()
                    if (remainingMs <= 0) {
                        player.pause()
                        sleepTimerEndMs = 0L
                        sleepTimerMinutes = 0
                        statusToast = "טיימר שינה: כיבוי אוטומטי 💤"
                    }
                }
            }
        }
    }
"""

if "// Position tracking, VOD resume" not in c:
    c = c.replace(
        "    // Cinematic Idle: fade out ALL UI after 5s",
        loop_code + "\n    // Cinematic Idle: fade out ALL UI after 5s"
    )

# 4. Flank Play/Pause with 10s buttons when isVod
old_center_buttons = """                    ExpressiveGlassButton(
                        Icons.Rounded.SkipNext, "ערוץ הבא", 48.dp, 56.dp,
                    ) { switchTo(index + 1) }
                    ExpressiveGlassButton(
                        icon = if (playing) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        label = "נגן / השהה",
                        width = 68.dp,
                        height = 68.dp,
                        primary = true,
                        drawableRes = if (playing) R.drawable.ic_player_pause else R.drawable.ic_player_play,
                        morphed = !playing,
                    ) {
                        touchTick++
                        if (player.isPlaying) player.pause() else { player.volume = 1.0f; player.play() }
                    }
                    ExpressiveGlassButton(
                        Icons.Rounded.SkipPrevious, "ערוץ קודם", 48.dp, 56.dp,
                    ) { switchTo(index - 1) }"""

new_center_buttons = """                    val isVod = current.kind == "vod" || current.categoryEn == "vod"
                    if (isVod) {
                        ExpressiveGlassButton(
                            Icons.Rounded.Replay10, "10 שניות אחורה", 52.dp, 56.dp,
                        ) {
                            touchTick++
                            player.seekTo((player.currentPosition - 10_000L).coerceAtLeast(0L))
                        }
                    } else {
                        ExpressiveGlassButton(
                            Icons.Rounded.SkipNext, "ערוץ הבא", 48.dp, 56.dp,
                        ) { switchTo(index + 1) }
                    }
                    ExpressiveGlassButton(
                        icon = if (playing) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        label = "נגן / השהה",
                        width = 68.dp,
                        height = 68.dp,
                        primary = true,
                        drawableRes = if (playing) R.drawable.ic_player_pause else R.drawable.ic_player_play,
                        morphed = !playing,
                    ) {
                        touchTick++
                        if (player.isPlaying) player.pause() else { player.volume = 1.0f; player.play() }
                    }
                    if (isVod) {
                        ExpressiveGlassButton(
                            Icons.Rounded.Forward10, "10 שניות קדימה", 52.dp, 56.dp,
                        ) {
                            touchTick++
                            val maxPos = if (player.duration > 0) player.duration else Long.MAX_VALUE
                            player.seekTo((player.currentPosition + 10_000L).coerceAtMost(maxPos))
                        }
                    } else {
                        ExpressiveGlassButton(
                            Icons.Rounded.SkipPrevious, "ערוץ קודם", 48.dp, 56.dp,
                        ) { switchTo(index - 1) }
                    }"""

assert old_center_buttons in c, "old_center_buttons not found"
c = c.replace(old_center_buttons, new_center_buttons)

# 5. Pass positionMs, durationMs, onSeek to PlayerBottomBar
old_call_bottom = """            PlayerBottomBar(
                ch = current,
                fit = fit,
                onCycleFit = {
                    touchTick++
                    fit = fit.next()
                },
                onOptions = {
                    touchTick++
                    sheet = "more"
                },
            )"""

new_call_bottom = """            PlayerBottomBar(
                ch = current,
                fit = fit,
                positionMs = currentPositionMs,
                durationMs = durationMs,
                onSeek = { pos ->
                    touchTick++
                    currentPositionMs = pos
                    player.seekTo(pos)
                },
                onCycleFit = {
                    touchTick++
                    fit = fit.next()
                },
                onOptions = {
                    touchTick++
                    sheet = "more"
                },
            )"""

assert old_call_bottom in c, "old_call_bottom not found"
c = c.replace(old_call_bottom, new_call_bottom)

# 6. Pass playbackSpeed, sleepTimer to PlayerOptionsDrawer
old_call_drawer = """        PlayerOptionsDrawer(
            visible = sheet == "more",
            qualityLabel = selectedVideoHeight(player, tracks)?.let { "${it}p" } ?: "אוטומטי",
            serverIndex = sourceIndex,
            serverCount = candidates.size,"""

new_call_drawer = """        PlayerOptionsDrawer(
            visible = sheet == "more",
            qualityLabel = selectedVideoHeight(player, tracks)?.let { "${it}p" } ?: "אוטומטי",
            playbackSpeed = playbackSpeed,
            sleepTimerMinutes = sleepTimerMinutes,
            serverIndex = sourceIndex,
            serverCount = candidates.size,
            onSpeed = { sheet = "speed" },
            onSleepTimer = { sheet = "sleep" },"""

assert old_call_drawer in c, "old_call_drawer not found"
c = c.replace(old_call_drawer, new_call_drawer)

# 7. Add speed and sleep sheets in when (sheet)
old_sheets = """        "quality" -> ResolutionDialog(player, tracks, onDismiss = { sheet = null })
        "cast" -> CastSheet("""

new_sheets = """        "quality" -> ResolutionDialog(player, tracks, onDismiss = { sheet = null })
        "speed" -> PlaybackSpeedDialog(
            currentSpeed = playbackSpeed,
            onSelectSpeed = { sp ->
                playbackSpeed = sp
                player.setPlaybackSpeed(sp)
                statusToast = "מהירות: ${sp}x"
                sheet = null
            },
            onDismiss = { sheet = null }
        )
        "sleep" -> SleepTimerDialog(
            currentMinutes = sleepTimerMinutes,
            onSelectMinutes = { mins ->
                sleepTimerMinutes = mins
                sleepTimerEndMs = if (mins > 0) System.currentTimeMillis() + mins * 60_000L else 0L
                statusToast = if (mins > 0) "טיימר שינה: יכבה בעוד $mins דקות" else "טיימר שינה בוטל"
                sheet = null
            },
            onDismiss = { sheet = null }
        )
        "cast" -> CastSheet("""

assert old_sheets in c, "old_sheets not found"
c = c.replace(old_sheets, new_sheets)

with open(p, "w", encoding="utf-8") as f:
    f.write(c)

print("PlayerScreen.kt updated successfully with VOD timeline, 10s skip, speed & sleep timer!")

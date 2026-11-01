import os

path = "android/app/src/main/kotlin/com/israeltv/plus/ui/player/PlayerScreen.kt"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

# 1. Add imports
import_target = "import com.israeltv.plus.data.Channel"
import_replacement = """import com.israeltv.plus.data.Channel
import com.israeltv.plus.data.CatchupDvr
import com.israeltv.plus.data.CatchupProgram"""
assert import_target in text
text = text.replace(import_target, import_replacement, 1)

# 2. Add catchup state variables around line 291
state_target = """    var currentPositionMs by remember { mutableLongStateOf(0L) }
    var durationMs by remember { mutableLongStateOf(0L) }"""
state_replacement = """    var currentPositionMs by remember { mutableLongStateOf(0L) }
    var durationMs by remember { mutableLongStateOf(0L) }
    var isCatchupMode by remember(current.id) { mutableStateOf(false) }
    var catchupProgram by remember(current.id) { mutableStateOf<CatchupProgram?>(null) }"""
assert state_target in text
text = text.replace(state_target, state_replacement, 1)

# 3. In switchTo, reset catchup
switch_target = """    fun switchTo(i: Int) {
        index = (i + list.size) % list.size
        touchTick++
    }"""
switch_replacement = """    fun switchTo(i: Int) {
        index = (i + list.size) % list.size
        isCatchupMode = false
        catchupProgram = null
        sourceIndex = 0
        touchTick++
    }"""
assert switch_target in text
text = text.replace(switch_target, switch_replacement, 1)

# 4. In PlayerBottomBar call
bottom_target = """            PlayerBottomBar(
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

bottom_replacement = """            PlayerBottomBar(
                ch = current,
                fit = fit,
                positionMs = currentPositionMs,
                durationMs = durationMs,
                isCatchupActive = isCatchupMode,
                catchupTitle = catchupProgram?.title,
                onSeek = { pos ->
                    touchTick++
                    currentPositionMs = pos
                    player.seekTo(pos)
                },
                onCycleFit = {
                    touchTick++
                    fit = fit.next()
                },
                onCatchup = {
                    touchTick++
                    sheet = "catchup"
                },
                onReturnLive = {
                    touchTick++
                    isCatchupMode = false
                    catchupProgram = null
                    sourceIndex = 0
                    statusToast = "🔴 חזרה לשידור חי"
                    if (player.isCurrentMediaItemLive) {
                        player.seekToDefaultPosition()
                    } else {
                        switchTo(index)
                    }
                },
                onOptions = {
                    touchTick++
                    sheet = "more"
                },
            )"""
assert bottom_target in text
text = text.replace(bottom_target, bottom_replacement, 1)

# 5. In PlayerOptionsDrawer call
drawer_target = """            onMultiView = {
                sheet = null
                multiViewActive = true
            },
            onChannels = { sheet = "list" },"""

drawer_replacement = """            onMultiView = {
                sheet = null
                multiViewActive = true
            },
            onCatchup = {
                sheet = "catchup"
            },
            onChannels = { sheet = "list" },"""
assert drawer_target in text
text = text.replace(drawer_target, drawer_replacement, 1)

# 6. In when (sheet) add "catchup"
sheet_target = """    when (sheet) {
        "list" -> ChannelListSheet(list, index, logo, onPick = { switchTo(it); sheet = null }, onDismiss = { sheet = null })"""

sheet_replacement = """    when (sheet) {
        "catchup" -> CatchupSheet(
            channel = current,
            logoUrl = logo(current),
            onSelectProgram = { prog ->
                isCatchupMode = true
                catchupProgram = prog
                val dvrUrl = CatchupDvr.dvrUrlFor(current)
                val now = System.currentTimeMillis()
                val offsetFromLiveMs = (now - prog.startMs).coerceAtLeast(0L)
                statusToast = "⏮️ שידור חוזר: ${prog.title}"
                sheet = null
                if (dvrUrl != null) {
                    player.setMediaItem(
                        androidx.media3.common.MediaItem.Builder()
                            .setUri(dvrUrl)
                            .setMimeType(androidx.media3.common.MimeTypes.APPLICATION_M3U8)
                            .build()
                    )
                    player.prepare()
                    player.playWhenReady = true
                    scope.launch {
                        kotlinx.coroutines.delay(1000)
                        val dur = player.duration
                        if (dur > 0L) {
                            val targetPos = (dur - offsetFromLiveMs).coerceAtLeast(0L)
                            player.seekTo(targetPos)
                        }
                    }
                } else {
                    val dur = player.duration
                    if (dur > 0L) {
                        val targetPos = (dur - offsetFromLiveMs).coerceAtLeast(0L)
                        player.seekTo(targetPos)
                    }
                }
            },
            onRewindMinutes = { mins ->
                isCatchupMode = true
                catchupProgram = CatchupProgram(
                    startMin = (System.currentTimeMillis() / 60_000L) - mins,
                    durationMin = mins,
                    title = "לפני $mins דקות"
                )
                val dvrUrl = CatchupDvr.dvrUrlFor(current)
                val rewindMs = mins * 60_000L
                statusToast = "⏮️ חזרה $mins דקות אחורה"
                sheet = null
                if (dvrUrl != null) {
                    player.setMediaItem(
                        androidx.media3.common.MediaItem.Builder()
                            .setUri(dvrUrl)
                            .setMimeType(androidx.media3.common.MimeTypes.APPLICATION_M3U8)
                            .build()
                    )
                    player.prepare()
                    player.playWhenReady = true
                    scope.launch {
                        kotlinx.coroutines.delay(1000)
                        val dur = player.duration
                        if (dur > 0L) {
                            val targetPos = (dur - rewindMs).coerceAtLeast(0L)
                            player.seekTo(targetPos)
                        }
                    }
                } else {
                    val cur = player.currentPosition
                    val targetPos = (cur - rewindMs).coerceAtLeast(0L)
                    player.seekTo(targetPos)
                }
            },
            onDismiss = { sheet = null },
        )
        "list" -> ChannelListSheet(list, index, logo, onPick = { switchTo(it); sheet = null }, onDismiss = { sheet = null })"""
assert sheet_target in text
text = text.replace(sheet_target, sheet_replacement, 1)

with open(path, "w", encoding="utf-8") as f:
    f.write(text)
print("Updated PlayerScreen.kt for Catch-Up support successfully!")


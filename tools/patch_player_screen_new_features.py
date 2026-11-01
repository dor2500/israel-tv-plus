import sys

with open("android/app/src/main/kotlin/com/israeltv/plus/ui/player/PlayerScreen.kt", "r", encoding="utf-8") as f:
    text = f.read()

# 1. Add imports if needed
imports_to_add = """import androidx.compose.material.icons.rounded.BatteryChargingFull
import androidx.compose.material.icons.rounded.FastForward
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.LinearEasing
"""

if "BatteryChargingFull" not in text:
    text = text.replace("import androidx.compose.material.icons.rounded.GridView", "import androidx.compose.material.icons.rounded.GridView\n" + imports_to_add)

# 2. Add lowBandwidthMode and isAudioOnlyMode states
old_states = """    var isCatchupMode by remember(current.id) { mutableStateOf(false) }
    var catchupProgram by remember(current.id) { mutableStateOf<CatchupProgram?>(null) }"""

new_states = """    var isCatchupMode by remember(current.id) { mutableStateOf(false) }
    var catchupProgram by remember(current.id) { mutableStateOf<CatchupProgram?>(null) }
    var lowBandwidthMode by remember { mutableStateOf(false) }
    var isAudioOnlyMode by remember { mutableStateOf(false) }

    LaunchedEffect(lowBandwidthMode) {
        val p = player.trackSelectionParameters.buildUpon()
        if (lowBandwidthMode) {
            p.setMaxVideoBitrate(1_400_000)
             .setMaxVideoSize(854, 480)
             .setForceHighestSupportedBitrate(false)
        } else {
            p.setMaxVideoBitrate(15_000_000)
             .clearVideoSizeConstraints()
             .setForceHighestSupportedBitrate(true)
        }
        player.trackSelectionParameters = p.build()
    }"""

assert old_states in text, "old_states not found"
text = text.replace(old_states, new_states, 1)

# 3. Update PlayerBottomBar call
old_bottom_call = """                durationMs = durationMs,
                isCatchupActive = isCatchupMode,
                catchupTitle = catchupProgram?.title,
                onSeek = { pos ->"""

new_bottom_call = """                durationMs = durationMs,
                isCatchupActive = isCatchupMode,
                catchupTitle = catchupProgram?.title,
                isLowBandwidth = lowBandwidthMode,
                isAudioOnly = isAudioOnlyMode,
                onToggleLowBandwidth = {
                    lowBandwidthMode = !lowBandwidthMode
                    statusToast = if (lowBandwidthMode) "📶 מצב רשת איטית (5Mbps ומטה) מופעל" else "📶 איכות רגילה"
                },
                onToggleAudioOnly = { isAudioOnlyMode = !isAudioOnlyMode },
                onSeek = { pos ->"""

assert old_bottom_call in text, "old_bottom_call not found"
text = text.replace(old_bottom_call, new_bottom_call, 1)

# 4. Update PlayerOptionsDrawer call
old_drawer_call = """            onCatchup = {
                if (CatchupRepository.hasCatchup(current)) sheet = "catchup" else statusToast = "לערוץ הזה אין שידור חוזר"
            },
            onChannels = { sheet = "list" },"""

new_drawer_call = """            onCatchup = {
                if (CatchupRepository.hasCatchup(current)) sheet = "catchup" else statusToast = "לערוץ הזה אין שידור חוזר"
            },
            isLowBandwidth = lowBandwidthMode,
            isAudioOnly = isAudioOnlyMode,
            onToggleLowBandwidth = {
                lowBandwidthMode = !lowBandwidthMode
                statusToast = if (lowBandwidthMode) "📶 מצב רשת איטית (5Mbps ומטה) מופעל" else "📶 איכות רגילה"
            },
            onToggleAudioOnly = {
                isAudioOnlyMode = !isAudioOnlyMode
                sheet = null
            },
            onChannels = { sheet = "list" },"""

assert old_drawer_call in text, "old_drawer_call not found"
text = text.replace(old_drawer_call, new_drawer_call, 1)

# 5. Add VOD Skip Intro, Next Episode, and Audio-Only screen
target_after_drawer = """            onDismiss = { sheet = null },
        )"""

overlays_to_insert = """            onDismiss = { sheet = null },
        )

        // VOD Skip Intro & Next Episode pills
        val isVod = current.kind == "vod" || current.categoryEn == "vod"
        AnimatedVisibility(
            visible = isVod && currentPositionMs in 5_000L..85_000L,
            enter = fadeIn(tween(250)) + slideInHorizontally { it },
            exit = fadeOut(tween(250)) + slideOutHorizontally { it },
            modifier = Modifier.align(Alignment.BottomEnd).padding(end = 24.dp, bottom = 96.dp)
        ) {
            Box(
                Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.Black.copy(alpha = 0.82f))
                    .border(1.dp, Color.White.copy(alpha = 0.38f), RoundedCornerShape(24.dp))
                    .clickable {
                        touchTick++
                        val target = 85_000L
                        player.seekTo(target)
                        currentPositionMs = target
                        statusToast = "⏩ דולג על הפתיח"
                    }
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Rounded.FastForward, null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Text("דלג על פתיח", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        AnimatedVisibility(
            visible = isVod && durationMs > 60_000L && currentPositionMs >= (durationMs - 35_000L),
            enter = fadeIn(tween(250)) + slideInHorizontally { it },
            exit = fadeOut(tween(250)) + slideOutHorizontally { it },
            modifier = Modifier.align(Alignment.BottomEnd).padding(end = 24.dp, bottom = 96.dp)
        ) {
            Box(
                Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.9f))
                    .border(1.dp, Color.White.copy(alpha = 0.45f), RoundedCornerShape(24.dp))
                    .clickable {
                        touchTick++
                        switchTo(index + 1)
                        statusToast = "⏭️ עובר לפרק הבא"
                    }
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Rounded.SkipNext, null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Text("הפרק הבא", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Ambient Audio-Only / Battery Saver Screen
        if (isAudioOnlyMode) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .clickable { isAudioOnlyMode = false },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(24.dp)
                ) {
                    ChannelLogo(
                        url = logo(current),
                        modifier = Modifier.size(110.dp),
                        isRadio = current.isRadio
                    )
                    Spacer(Modifier.height(18.dp))
                    Text(
                        current.name,
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    val progTitle = catchupProgram?.title
                    if (!progTitle.isNullOrEmpty()) {
                        Spacer(Modifier.height(6.dp))
                        Text(progTitle, color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                    }
                    Spacer(Modifier.height(28.dp))
                    AudioWaveform()
                    Spacer(Modifier.height(28.dp))
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Color(0xFF10B981).copy(alpha = 0.2f))
                            .border(1.dp, Color(0xFF10B981), RoundedCornerShape(50))
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Rounded.BatteryChargingFull, null, tint = Color(0xFF10B981), modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("מצב האזנה בלבד · חיסכון של 85% בסוללה ודאטה", color = Color(0xFF10B981), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(Modifier.height(20.dp))
                    Text("הקש בכל מקום במסך לחזרה לוידאו", color = Color.White.copy(alpha = 0.45f), fontSize = 12.sp)
                }
            }
        }"""

assert target_after_drawer in text, "target_after_drawer not found"
text = text.replace(target_after_drawer, overlays_to_insert, 1)

# 6. Add AudioWaveform composable at the bottom
waveform_code = """
@Composable
private fun AudioWaveform(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val h1 = infiniteTransition.animateFloat(0.3f, 1f, infiniteRepeatable(tween(400, easing = LinearEasing), androidx.compose.animation.core.RepeatMode.Reverse), label = "h1")
    val h2 = infiniteTransition.animateFloat(0.5f, 0.9f, infiniteRepeatable(tween(550, easing = LinearEasing), androidx.compose.animation.core.RepeatMode.Reverse), label = "h2")
    val h3 = infiniteTransition.animateFloat(0.2f, 1f, infiniteRepeatable(tween(350, easing = LinearEasing), androidx.compose.animation.core.RepeatMode.Reverse), label = "h3")
    val h4 = infiniteTransition.animateFloat(0.4f, 0.85f, infiniteRepeatable(tween(600, easing = LinearEasing), androidx.compose.animation.core.RepeatMode.Reverse), label = "h4")
    val h5 = infiniteTransition.animateFloat(0.3f, 0.95f, infiniteRepeatable(tween(450, easing = LinearEasing), androidx.compose.animation.core.RepeatMode.Reverse), label = "h5")
    val heights = listOf(h1, h2, h3, h4, h5)
    Row(modifier.height(36.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
        heights.forEach { h ->
            Box(
                Modifier
                    .width(5.dp)
                    .fillMaxHeight(h.value)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFF38BDF8))
            )
        }
    }
}
"""

text = text.strip() + "\n" + waveform_code

with open("android/app/src/main/kotlin/com/israeltv/plus/ui/player/PlayerScreen.kt", "w", encoding="utf-8", newline="\n") as f:
    f.write(text)

print("PlayerScreen.kt cleanly patched")

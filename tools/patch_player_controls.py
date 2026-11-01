import os

path = "android/app/src/main/kotlin/com/israeltv/plus/ui/player/PlayerControls.kt"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

# Add History icon import if not present
if "import androidx.compose.material.icons.rounded.History" not in text:
    text = text.replace(
        "import androidx.compose.material.icons.rounded.GridView",
        "import androidx.compose.material.icons.rounded.GridView\nimport androidx.compose.material.icons.rounded.History"
    )

# Replace PlayerBottomBar signature & implementation
old_bar = """internal fun PlayerBottomBar(
    ch: Channel,
    fit: VideoFit,
    positionMs: Long = 0L,
    durationMs: Long = 0L,
    onSeek: (Long) -> Unit = {},
    onCycleFit: () -> Unit,
    onOptions: () -> Unit,
) {
    val d = LocalDimens.current
    val badgeShape = RoundedCornerShape(6.dp)
    val isVod = ch.kind == "vod" || ch.categoryEn == "vod"
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
        ) {
        Row(Modifier.weight(1f, fill = false), verticalAlignment = Alignment.CenterVertically) {
            val isVod = ch.kind == "vod" || ch.categoryEn == "vod"
            Text(
                when {
                    ch.isRadio -> "רדיו"
                    isVod -> "VOD"
                    else -> "Live"
                },
                Modifier
                    .clip(badgeShape)
                    .border(1.dp, if (isVod) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.42f), badgeShape)
                    .padding(horizontal = 8.dp, vertical = 2.dp),
                color = if (isVod) MaterialTheme.colorScheme.primary else Color.White,
                style = MaterialTheme.typography.labelMedium,
            )
            Spacer(Modifier.width(12.dp))
            Text(ch.name, color = Color.White, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Spacer(Modifier.width(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MorphAction(fit.icon, fit.label, onClick = onCycleFit)
            MorphAction(Icons.Rounded.MoreHoriz, "אפשרויות", onClick = onOptions)
        }
    }
}
}"""

new_bar = """internal fun PlayerBottomBar(
    ch: Channel,
    fit: VideoFit,
    positionMs: Long = 0L,
    durationMs: Long = 0L,
    isCatchupActive: Boolean = false,
    catchupTitle: String? = null,
    onSeek: (Long) -> Unit = {},
    onCycleFit: () -> Unit,
    onCatchup: () -> Unit = {},
    onReturnLive: () -> Unit = {},
    onOptions: () -> Unit,
) {
    val d = LocalDimens.current
    val badgeShape = RoundedCornerShape(6.dp)
    val isVod = ch.kind == "vod" || ch.categoryEn == "vod"
    val showScrubber = (isVod || isCatchupActive) && durationMs > 0L
    Column(
        Modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.88f))))
            .navigationBarsPadding()
            .windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal))
            .padding(start = d.playerEdge, end = d.playerEdge, top = 20.dp, bottom = 16.dp),
    ) {
        if (showScrubber) {
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
                        thumbColor = if (isCatchupActive) Color(0xFFE5A93C) else MaterialTheme.colorScheme.primary,
                        activeTrackColor = if (isCatchupActive) Color(0xFFE5A93C) else MaterialTheme.colorScheme.primary,
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
        ) {
            Row(Modifier.weight(1f, fill = false), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    when {
                        isCatchupActive -> "קאץ' אפ"
                        ch.isRadio -> "רדיו"
                        isVod -> "VOD"
                        else -> "Live"
                    },
                    Modifier
                        .clip(badgeShape)
                        .border(
                            1.dp,
                            when {
                                isCatchupActive -> Color(0xFFE5A93C)
                                isVod -> MaterialTheme.colorScheme.primary
                                else -> Color.White.copy(alpha = 0.42f)
                            },
                            badgeShape
                        )
                        .background(if (isCatchupActive) Color(0xFFE5A93C).copy(alpha = 0.16f) else Color.Transparent)
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    color = when {
                        isCatchupActive -> Color(0xFFE5A93C)
                        isVod -> MaterialTheme.colorScheme.primary
                        else -> Color.White
                    },
                    style = MaterialTheme.typography.labelMedium,
                )
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(
                        if (isCatchupActive && !catchupTitle.isNullOrEmpty()) catchupTitle else ch.name,
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (isCatchupActive && !catchupTitle.isNullOrEmpty()) {
                        Text(
                            "${ch.name} · שידור חוזר",
                            color = Color.White.copy(alpha = 0.62f),
                            fontSize = 11.5.sp,
                            maxLines = 1,
                        )
                    }
                }
            }
            Spacer(Modifier.width(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                if (isCatchupActive) {
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Color(0xFFE53935))
                            .clickable(onClick = onReturnLive)
                            .padding(horizontal = 11.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(7.dp).clip(CircleShape).background(Color.White))
                            Spacer(Modifier.width(5.dp))
                            Text("חזרה לשידור חי", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                } else if (!ch.isRadio && !isVod) {
                    MorphAction(Icons.Rounded.History, "קאץ' אפ", onClick = onCatchup)
                }
                MorphAction(fit.icon, fit.label, onClick = onCycleFit)
                MorphAction(Icons.Rounded.MoreHoriz, "אפשרויות", onClick = onOptions)
            }
        }
    }
}"""

# Check CRLF
old_bar_crlf = old_bar.replace("\n", "\r\n")
if old_bar in text:
    text = text.replace(old_bar, new_bar)
elif old_bar_crlf in text:
    text = text.replace(old_bar_crlf, new_bar.replace("\n", "\r\n"))
else:
    print("Could not find old_bar exactly! Checking substring...")

# Update PlayerOptionsDrawer signature & items
old_drawer_sig = """    onPip: () -> Unit,
    onMultiView: () -> Unit,
    onChannels: () -> Unit,"""
new_drawer_sig = """    onPip: () -> Unit,
    onMultiView: () -> Unit,
    onCatchup: () -> Unit = {},
    onChannels: () -> Unit,"""
if old_drawer_sig in text:
    text = text.replace(old_drawer_sig, new_drawer_sig)
else:
    text = text.replace(old_drawer_sig.replace("\n", "\r\n"), new_drawer_sig.replace("\n", "\r\n"))

old_drawer_item = """DrawerItem(Icons.Rounded.GridView, "פיצול מסך", onClick = onMultiView)"""
new_drawer_item = """DrawerItem(Icons.Rounded.History, "קאץ' אפ (שידור חוזר)", "תוכניות קודמות", onClick = onCatchup)
                        DrawerItem(Icons.Rounded.GridView, "פיצול מסך", onClick = onMultiView)"""
if old_drawer_item in text:
    text = text.replace(old_drawer_item, new_drawer_item)
else:
    text = text.replace(old_drawer_item.replace("\n", "\r\n"), new_drawer_item.replace("\n", "\r\n"))

with open(path, "w", encoding="utf-8") as f:
    f.write(text)
print("Updated PlayerControls.kt successfully!")

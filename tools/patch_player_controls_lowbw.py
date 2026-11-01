import sys

with open("android/app/src/main/kotlin/com/israeltv/plus/ui/player/PlayerControls.kt", "r", encoding="utf-8") as f:
    text = f.read()

# 1. Update PlayerBottomBar parameters
old_bottom_params = """    isCatchupActive: Boolean = false,
    catchupTitle: String? = null,
    onSeek: (Long) -> Unit = {},
    onCycleFit: () -> Unit,
    onCatchup: () -> Unit = {},
    onReturnLive: () -> Unit = {},
    onOptions: () -> Unit,"""

new_bottom_params = """    isCatchupActive: Boolean = false,
    catchupTitle: String? = null,
    isLowBandwidth: Boolean = false,
    isAudioOnly: Boolean = false,
    onSeek: (Long) -> Unit = {},
    onCycleFit: () -> Unit,
    onCatchup: () -> Unit = {},
    onReturnLive: () -> Unit = {},
    onToggleLowBandwidth: () -> Unit = {},
    onToggleAudioOnly: () -> Unit = {},
    onOptions: () -> Unit,"""

assert old_bottom_params in text, "old_bottom_params not found"
text = text.replace(old_bottom_params, new_bottom_params, 1)

# 2. Add low bandwidth badge and headphone button to PlayerBottomBar
old_bar_actions = """                } else if (!ch.isRadio && !isVod) {
                    MorphAction(Icons.Rounded.History, "קאץ' אפ", onClick = onCatchup)
                }
                MorphAction(fit.icon, fit.label, onClick = onCycleFit)
                MorphAction(Icons.Rounded.MoreHoriz, "אפשרויות", onClick = onOptions)"""

new_bar_actions = """                } else if (!ch.isRadio && !isVod) {
                    MorphAction(Icons.Rounded.History, "קאץ' אפ", onClick = onCatchup)
                    MorphAction(Icons.Rounded.Audiotrack, "האזנה", onClick = onToggleAudioOnly)
                }
                if (isLowBandwidth) {
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Color(0xFF0284C7).copy(alpha = 0.25f))
                            .border(1.dp, Color(0xFF38BDF8), RoundedCornerShape(50))
                            .clickable(onClick = onToggleLowBandwidth)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Speed, null, tint = Color(0xFF38BDF8), modifier = Modifier.size(12.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("5Mbps", color = Color(0xFF38BDF8), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                MorphAction(fit.icon, fit.label, onClick = onCycleFit)
                MorphAction(Icons.Rounded.MoreHoriz, "אפשרויות", onClick = onOptions)"""

assert old_bar_actions in text, "old_bar_actions not found"
text = text.replace(old_bar_actions, new_bar_actions, 1)

# 3. Update PlayerOptionsDrawer parameters
old_drawer_params = """    onPip: () -> Unit,
    onMultiView: () -> Unit,
    onCatchup: () -> Unit = {},
    onChannels: () -> Unit,"""

new_drawer_params = """    onPip: () -> Unit,
    onMultiView: () -> Unit,
    onCatchup: () -> Unit = {},
    isLowBandwidth: Boolean = false,
    isAudioOnly: Boolean = false,
    onToggleLowBandwidth: () -> Unit = {},
    onToggleAudioOnly: () -> Unit = {},
    onChannels: () -> Unit,"""

assert old_drawer_params in text, "old_drawer_params not found"
text = text.replace(old_drawer_params, new_drawer_params, 1)

# 4. Add items in PlayerOptionsDrawer
old_drawer_items = """                        DrawerItem(Icons.Rounded.PictureInPictureAlt, "חלון צף", onClick = onPip)
                        DrawerItem(Icons.Rounded.HighQuality, "איכות וידאו", qualityLabel, onQuality)
                        DrawerItem(Icons.Rounded.History, "קאץ' אפ (שידור חוזר)", "תוכניות קודמות", onClick = onCatchup)"""

new_drawer_items = """                        DrawerItem(Icons.Rounded.PictureInPictureAlt, "חלון צף", onClick = onPip)
                        DrawerItem(Icons.Rounded.HighQuality, "איכות וידאו", qualityLabel, onQuality)
                        DrawerItem(Icons.Rounded.Speed, "מצב רשת איטית (5Mbps ומטה)", if (isLowBandwidth) "פעיל (480p)" else "איכות רגילה", onClick = onToggleLowBandwidth)
                        DrawerItem(Icons.Rounded.Audiotrack, "מצב האזנה (חיסכון בסוללה)", if (isAudioOnly) "פעיל" else "כבוי", onClick = onToggleAudioOnly)
                        DrawerItem(Icons.Rounded.History, "קאץ' אפ (שידור חוזר)", "תוכניות קודמות", onClick = onCatchup)"""

assert old_drawer_items in text, "old_drawer_items not found"
text = text.replace(old_drawer_items, new_drawer_items, 1)

with open("android/app/src/main/kotlin/com/israeltv/plus/ui/player/PlayerControls.kt", "w", encoding="utf-8", newline="\n") as f:
    f.write(text)

print("PlayerControls.kt updated successfully")

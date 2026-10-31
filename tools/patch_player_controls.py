import os

player_controls = r'C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo\android\app\src\main\kotlin\com\israeltv\plus\ui\player\PlayerControls.kt'

with open(player_controls, 'r', encoding='utf-8') as f:
    c = f.read()

# Add imports
old_imp = 'import androidx.compose.ui.platform.LocalHapticFeedback\n'
new_imp = '''import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import android.content.Intent
'''
if old_imp in c:
    c = c.replace(old_imp, new_imp, 1)

# In CastSheet: add showPlexDialog state and Plex option row + dialog
old_cast_sheet_start = '''internal fun CastSheet(
    channel: Channel,
    castController: CastController,
    onDisconnectAndResumePhone: () -> Unit,
    onDismiss: () -> Unit,
) {
    val scheme = MaterialTheme.colorScheme'''

new_cast_sheet_start = '''internal fun CastSheet(
    channel: Channel,
    castController: CastController,
    onDisconnectAndResumePhone: () -> Unit,
    onDismiss: () -> Unit,
) {
    val scheme = MaterialTheme.colorScheme
    var showPlexDialog by remember { mutableStateOf(false) }'''

if old_cast_sheet_start in c:
    c = c.replace(old_cast_sheet_start, new_cast_sheet_start, 1)

# After ScreenShare row in CastSheet
old_screen_share_end = '''                Column(Modifier.weight(1f)) {
                    Text("שיקוף מסך (LG Screen Share / Smart View)", color = scheme.onPrimaryContainer, style = MaterialTheme.typography.titleMedium)
                    Text("שיקוף ישיר של מסך הטלפון אל הטלוויזיה בלחיצה אחת", color = scheme.onPrimaryContainer.copy(alpha = 0.8f), style = MaterialTheme.typography.bodySmall)
                }
            }'''

new_screen_share_end = '''                Column(Modifier.weight(1f)) {
                    Text("שיקוף מסך (LG Screen Share / Smart View)", color = scheme.onPrimaryContainer, style = MaterialTheme.typography.titleMedium)
                    Text("שיקוף ישיר של מסך הטלפון אל הטלוויזיה בלחיצה אחת", color = scheme.onPrimaryContainer.copy(alpha = 0.8f), style = MaterialTheme.typography.bodySmall)
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(scheme.surfaceContainerHigh)
                    .clickable(role = Role.Button) {
                        showPlexDialog = true
                    }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Rounded.Tv, null, tint = scheme.primary, modifier = Modifier.size(26.dp))
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text("חיבור ל-Plex ול-DLNA (טלוויזיית LG / מחשב)", color = scheme.onSurface, style = MaterialTheme.typography.titleMedium)
                    Text("הוספת 269 הערוצים ולוח השידורים ל-Plex Live TV & DVR", color = scheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                }
            }'''

if old_screen_share_end in c:
    c = c.replace(old_screen_share_end, new_screen_share_end, 1)

# Add showPlexDialog check before ending CastSheet
old_cast_sheet_end = '''            CastQuietRow(Icons.AutoMirrored.Rounded.OpenInNew, "פתיחה באפליקציה חיצונית") {
                castController.openExternalCaster(channel)
                onDismiss()
            }
        }
    }
}'''

new_cast_sheet_end = '''            CastQuietRow(Icons.AutoMirrored.Rounded.OpenInNew, "פתיחה באפליקציה חיצונית") {
                castController.openExternalCaster(channel)
                onDismiss()
            }
        }
    }
    if (showPlexDialog) {
        PlexIntegrationDialog(onDismiss = { showPlexDialog = false })
    }
}'''

if old_cast_sheet_end in c:
    c = c.replace(old_cast_sheet_end, new_cast_sheet_end, 1)

# Add PlexIntegrationDialog composable at bottom of file
dialog_code = '''

@Composable
fun PlexIntegrationDialog(onDismiss: () -> Unit) {
    val scheme = MaterialTheme.colorScheme
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val haptic = LocalHapticFeedback.current
    val m3uUrl = "https://dor2500.github.io/israel-tv-plus/plex.m3u"
    val epgUrl = "https://dor2500.github.io/israel-tv-plus/epg.xml"

    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = scheme.surfaceContainerHigh,
            modifier = Modifier.fillMaxWidth().padding(16.dp),
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Tv, null, tint = scheme.primary, modifier = Modifier.size(32.dp))
                    Spacer(Modifier.width(16.dp))
                    Text("חיבור ל-Plex ול-DLNA", style = MaterialTheme.typography.headlineSmall, color = scheme.onSurface)
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    "באמצעות כתובות אלו ניתן לצפות בכל 269 הערוצים הישראליים בתוך אפליקציית Plex בטלוויזיית LG, סמסונג או סטרימר, עם לוח שידורים (EPG) ופוסטרים מלאים לכל תוכנית.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = scheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(16.dp))

                Text("רשימת ערוצים (M3U Tuner):", style = MaterialTheme.typography.labelLarge, color = scheme.primary)
                Spacer(Modifier.height(4.dp))
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(scheme.surfaceContainerLowest)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(m3uUrl, Modifier.weight(1f), style = MaterialTheme.typography.bodySmall, color = scheme.onSurface, maxLines = 2)
                    Spacer(Modifier.width(8.dp))
                    FilledTonalButton(onClick = {
                        clipboard.setText(AnnotatedString(m3uUrl))
                        haptic.performHapticFeedback(HapticFeedbackType.VirtualKey)
                        android.widget.Toast.makeText(context, "הועתק ללוח", android.widget.Toast.LENGTH_SHORT).show()
                    }) {
                        Text("העתק")
                    }
                }

                Spacer(Modifier.height(12.dp))

                Text("מדריך שידורים (XMLTV Guide):", style = MaterialTheme.typography.labelLarge, color = scheme.primary)
                Spacer(Modifier.height(4.dp))
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(scheme.surfaceContainerLowest)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(epgUrl, Modifier.weight(1f), style = MaterialTheme.typography.bodySmall, color = scheme.onSurface, maxLines = 2)
                    Spacer(Modifier.width(8.dp))
                    FilledTonalButton(onClick = {
                        clipboard.setText(AnnotatedString(epgUrl))
                        haptic.performHapticFeedback(HapticFeedbackType.VirtualKey)
                        android.widget.Toast.makeText(context, "הועתק ללוח", android.widget.Toast.LENGTH_SHORT).show()
                    }) {
                        Text("העתק")
                    }
                }

                Spacer(Modifier.height(16.dp))
                Text(
                    "כיצד להגדיר בטלוויזיית LG:\\n1. פתחו את Plex Web במחשב או בשרת ← הגדרות ← Live TV & DVR.\\n2. לחצו Add Device / Tuner ובחרו M3U Tuner והדביקו את כתובת הרשימה.\\n3. בלשונית XMLTV Guide הדביקו את כתובת מדריך השידורים.\\n4. בטלוויזיית LG פתחו את אפליקציית Plex מחנות webOS, ועברו אל Live TV!",
                    style = MaterialTheme.typography.bodySmall,
                    color = scheme.onSurfaceVariant,
                )

                Spacer(Modifier.height(20.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    FilledTonalButton(onClick = {
                        val shareText = "ישראל TV+ ב-Plex:\\nרשימת ערוצים: $m3uUrl\\nמדריך שידורים: $epgUrl"
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, shareText)
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "שיתוף הגדרות Plex"))
                    }) {
                        Text("שתף קישורים")
                    }
                    Button(onClick = onDismiss) {
                        Text("סגור")
                    }
                }
            }
        }
    }
}
'''

c = c + dialog_code

with open(player_controls, 'w', encoding='utf-8') as f:
    f.write(c)

print('Successfully patched PlayerControls.kt')


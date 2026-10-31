import os

settings_screen = r'C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo\android\app\src\main\kotlin\com\israeltv\plus\ui\screens\SettingsScreen.kt'

with open(settings_screen, 'r', encoding='utf-8') as f:
    c = f.read()

# Add imports
old_imp = 'import com.israeltv.plus.data.Updater\n'
new_imp = '''import com.israeltv.plus.data.Updater
import com.israeltv.plus.ui.player.PlexIntegrationDialog
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
'''
if old_imp in c:
    c = c.replace(old_imp, new_imp, 1)

# In SettingsScreen: add showPlexDialog state
old_fun_start = '''fun SettingsScreen(data: ChannelData?, prefs: AppPrefs, updater: Updater, onProfiles: () -> Unit, onStyle: () -> Unit) {
    val ctx = LocalContext.current
    val haptic = LocalHapticFeedback.current'''

new_fun_start = '''fun SettingsScreen(data: ChannelData?, prefs: AppPrefs, updater: Updater, onProfiles: () -> Unit, onStyle: () -> Unit) {
    val ctx = LocalContext.current
    val haptic = LocalHapticFeedback.current
    var showPlexDialog by remember { mutableStateOf(false) }'''

if old_fun_start in c:
    c = c.replace(old_fun_start, new_fun_start, 1)

# Add plex item before about item
old_about = '''        item(key = "about") {'''
new_about = '''        item(key = "plex") {
            SectionHeader("שידור, Plex ו-DLNA")
            SettingsGroup {
                SettingRow(
                    Icons.Rounded.Tv,
                    "חיבור ל-Plex ולטלוויזיות LG",
                    "הגדרת 269 הערוצים ולוח השידורים ב-Plex (Live TV & DVR) ו-DLNA",
                    onClick = { showPlexDialog = true },
                )
            }
            Spacer(Modifier.height(28.dp))
        }
        item(key = "about") {'''

if old_about in c:
    c = c.replace(old_about, new_about, 1)

# Add showPlexDialog at end of SettingsScreen
old_end = '''    }
}

@Composable
fun UpdateCard(updater: Updater, modifier: Modifier = Modifier) {'''

new_end = '''    }
    if (showPlexDialog) {
        PlexIntegrationDialog(onDismiss = { showPlexDialog = false })
    }
}

@Composable
fun UpdateCard(updater: Updater, modifier: Modifier = Modifier) {'''

if old_end in c:
    c = c.replace(old_end, new_end, 1)

with open(settings_screen, 'w', encoding='utf-8') as f:
    f.write(c)

print('Successfully patched SettingsScreen.kt')


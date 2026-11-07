package com.israeltv.plus.ui.screens

import android.app.Activity
import android.content.Intent
import android.text.format.DateUtils
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.SystemUpdate
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.TextButton
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.israeltv.plus.data.AppPrefs
import com.israeltv.plus.data.GoogleAccount
import com.israeltv.plus.data.ChannelData
import com.israeltv.plus.data.UpdateState
import com.israeltv.plus.data.Updater
import com.israeltv.plus.ui.player.PlexIntegrationDialog
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.israeltv.plus.ui.components.BoldIcon
import com.israeltv.plus.ui.components.ProfileAvatar
import com.israeltv.plus.ui.components.SectionHeader
import com.israeltv.plus.ui.components.feedPadding
import com.israeltv.plus.ui.components.rememberBitChordFlingBehavior
import com.israeltv.plus.ui.components.specularBorder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SettingsScreen(data: ChannelData?, prefs: AppPrefs, updater: Updater, google: GoogleAccount, onProfiles: () -> Unit, onStyle: () -> Unit) {
    val ctx = LocalContext.current
    val haptic = LocalHapticFeedback.current
    var showPlexDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var changelog by remember { mutableStateOf<List<Pair<String, List<String>>>?>(null) }
    changelog?.let { list ->
        AlertDialog(
            onDismissRequest = { changelog = null },
            confirmButton = { TextButton(onClick = { changelog = null }) { Text("סגירה") } },
            title = { Text("לוג שינויים") },
            text = {
                if (list.isEmpty()) Text("אין חיבור לשרת, נסו שוב מאוחר יותר")
                else LazyColumn {
                    items(list) { (version, notes) ->
                        Text(version, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 12.dp, bottom = 4.dp))
                        notes.forEach { Text("• $it", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 4.dp)) }
                    }
                }
            }
        )
    }
    val activity = ctx as Activity
    val googlePicker = rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
        google.onPickerResult(activity, it.data)
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(0.dp),
            flingBehavior = rememberBitChordFlingBehavior(),
        ) {
            item(key = "update") { UpdateCard(updater, Modifier.fillMaxWidth().padding(bottom = 16.dp)) }
            item(key = "look") {
                SectionHeader("תצוגה וצליל")
                SettingsGroup {
                    SettingRow(Icons.Rounded.Palette, "צבעים וסגנון", "צבעי הטפט, פלטות נוספות ומצב כהה", onClick = onStyle)
                    SettingRow(
                        Icons.Rounded.Palette,
                        "רקע שחור OLED (OLED Black)",
                        "רקע שחור מוחלט (#000000) למסכי OLED",
                        onClick = {
                            val next = !prefs.theme.oledBlack
                            haptic.performHapticFeedback(if (next) HapticFeedbackType.ToggleOn else HapticFeedbackType.ToggleOff)
                            prefs.setTheme(prefs.theme.copy(oledBlack = next))
                        },
                    ) {
                        Switch(
                            checked = prefs.theme.oledBlack,
                            onCheckedChange = {
                                haptic.performHapticFeedback(if (it) HapticFeedbackType.ToggleOn else HapticFeedbackType.ToggleOff)
                                prefs.setTheme(prefs.theme.copy(oledBlack = it))
                            },
                        )
                    }
                    SettingRow(Icons.Rounded.Face, "פרופילים", "${prefs.profile.name} · ${prefs.profiles.size} פרופילים", onClick = onProfiles) {
                        ProfileAvatar(prefs.avatar, glyphSize = 30.dp)
                    }
                    SettingRow(Icons.Rounded.MusicNote, "צליל פתיחה", "צליל קצר כשהאפליקציה נפתחת (שקט כשהטלפון על שקט)",
                        onClick = {
                            haptic.performHapticFeedback(if (prefs.launchSound) HapticFeedbackType.ToggleOff else HapticFeedbackType.ToggleOn)
                            prefs.setLaunchSoundOn(!prefs.launchSound)
                        }) {
                        Switch(prefs.launchSound, {
                            haptic.performHapticFeedback(if (it) HapticFeedbackType.ToggleOn else HapticFeedbackType.ToggleOff)
                            prefs.setLaunchSoundOn(it)
                        })
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
            item(key = "gdrive") {
                SectionHeader("חשבון Google וסנכרון ענן")
                SettingsGroup {
                    if (google.signedIn) {
                        SettingRow(Icons.Rounded.CheckCircle, google.name, google.problem.ifEmpty { google.email })
                        SettingRow(
                            Icons.Rounded.Refresh,
                            if (google.busy) "מסנכרן…" else "סנכרן עכשיו",
                            if (google.lastSync > 0L) "סונכרן " + DateUtils.getRelativeTimeSpanString(google.lastSync) else "מועדפים, היסטוריה ופרופילים",
                            onClick = { google.sync(activity) },
                        )
                        SettingRow(Icons.AutoMirrored.Rounded.Logout, "התנתקות", "הנתונים בטלפון נשארים", onClick = { google.signOut(activity) })
                    } else {
                        SettingRow(
                            Icons.Rounded.Cloud,
                            if (google.busy) "מתחבר…" else "התחברות עם חשבון Google",
                            google.problem.ifEmpty { "סנכרון מועדפים, היסטוריה ופרופילים בין המכשירים" },
                            onClick = { if (!google.busy) google.signIn(activity) { googlePicker.launch(it) } },
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
            item(key = "plex") {
                SectionHeader("שידור, Plex ו-DLNA")
                SettingsGroup {
                    SettingRow(
                        Icons.Rounded.Tv,
                        "חיבור ל-Plex ולטלוויזיות LG",
                        "הגדרת 269 הערוצים ולוח השידורים ב-Plex (Live TV & DVR) ו-DLNA",
                        onClick = { showPlexDialog = true },
                    )
                }
                Spacer(Modifier.height(16.dp))
            }
            item(key = "about") {
                SectionHeader("אודות")
                SettingsGroup {
                    SettingRow(Icons.Rounded.Tv, "ישראל TV+ " + updater.installedVersion,
                        "${data?.channels?.size ?: 0} ערוצים ותחנות · ${data?.categories?.size ?: 0} קטגוריות")
                    SettingRow(Icons.Rounded.SystemUpdate, "לוג שינויים", "מה השתנה בכל גרסה", onClick = {
                        scope.launch { changelog = updater.changelog() }
                    })
                    SettingRow(Icons.Rounded.Language, "האתר", "dor2500.github.io/israel-tv-plus", onClick = {
                        ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://dor2500.github.io/israel-tv-plus/")))
                    })
                    SettingRow(Icons.Rounded.Code, "קוד המקור ומקורות השידור", "GitHub · SOURCES.md", onClick = {
                        ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/dor2500/israel-tv-plus")))
                    })
                }
            }
        }
    }
    if (showPlexDialog) {
        PlexIntegrationDialog(onDismiss = { showPlexDialog = false })
    }
}

/** Pixel-style system update surface: a large tonal card with the state, the version and the action. */
@Composable
fun UpdateCard(updater: Updater, modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    val haptic = LocalHapticFeedback.current
    val state = updater.state
    val shape = RoundedCornerShape(0.dp)
    val available = state is UpdateState.Available || state is UpdateState.Downloading ||
        state is UpdateState.Installing || (state is UpdateState.Failed && state.info != null)
    Column(
        modifier
            .fillMaxWidth()
            .clip(shape)
            .background(if (available) scheme.primaryContainer else scheme.surfaceContainerHigh.copy(alpha = 0.8f))
            .padding(24.dp)
    ) {
        Box(
            Modifier.size(56.dp).clip(CircleShape)
                .background(if (available) scheme.primary else scheme.secondaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            BoldIcon(
                if (available) Icons.Rounded.SystemUpdate else Icons.Rounded.CheckCircle, null, Modifier.size(30.dp),
                tint = if (available) scheme.onPrimary else scheme.onSecondaryContainer, weight = 0.8.dp,
            )
        }
        Spacer(Modifier.height(18.dp))
        val onCard = if (available) scheme.onPrimaryContainer else scheme.onSurface
        val title = when (state) {
            is UpdateState.Available -> "עדכון זמין: ${state.info.version}"
            is UpdateState.Downloading -> "מוריד את ${state.info.version}…"
            is UpdateState.Installing -> "מתקין את ${state.info.version}"
            is UpdateState.Failed -> if (state.info != null) "עדכון ${state.info.version}" else "לא ניתן לבדוק עדכונים"
            UpdateState.Checking -> "בודק עדכונים…"
            else -> "האפליקציה מעודכנת"
        }
        Text(title, fontSize = 24.sp, fontWeight = FontWeight.SemiBold, color = onCard)
        val sub = when (state) {
            is UpdateState.UpToDate -> "גרסה ${updater.installedVersion} · נבדק ב-" +
                SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(state.checkedAt))
            is UpdateState.Available -> "גרסה חדשה של האפליקציה מ-GitHub"
            is UpdateState.Downloading -> "${(state.progress * 100).toInt()}%"
            is UpdateState.Installing -> "אשרו את ההתקנה בחלון של אנדרואיד"
            is UpdateState.Failed -> state.message
            else -> "גרסה ${updater.installedVersion}"
        }
        Text(sub, fontSize = 14.sp, fontWeight = FontWeight.Light, color = onCard.copy(alpha = 0.8f), modifier = Modifier.padding(top = 4.dp))
        if (state is UpdateState.Downloading) {
            LinearWavyProgressIndicator(
                progress = { state.progress },
                modifier = Modifier.fillMaxWidth().padding(top = 18.dp),
            )
        }
        Spacer(Modifier.height(18.dp))
        val action: Pair<String, () -> Unit>? = when (state) {
            is UpdateState.Available -> "הורדה והתקנה" to { updater.download(state.info) }
            is UpdateState.Failed -> "נסו שוב" to { state.info?.let(updater::download) ?: updater.check() }
            UpdateState.Idle, is UpdateState.UpToDate -> "בדיקת עדכונים" to { updater.check() }
            else -> null
        }
        if (action != null) {
            Button(
                onClick = { haptic.performHapticFeedback(HapticFeedbackType.Confirm); action.second() },
                shape = RoundedCornerShape(50),
                colors = if (available) ButtonDefaults.buttonColors() else ButtonDefaults.filledTonalButtonColors(),
                contentPadding = PaddingValues(horizontal = 24.dp),
                modifier = Modifier.height(48.dp),
            ) { Text(action.first, fontWeight = FontWeight.SemiBold) }
        }
    }
}

@Composable
private fun SettingsGroup(content: @Composable () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.7f)),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) { content() }
}

@Composable
private fun SettingRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: (() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
) {
    val scheme = MaterialTheme.colorScheme
    Row(
        Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(remember { MutableInteractionSource() }, null, onClick = onClick) else Modifier)
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(40.dp).clip(CircleShape).background(scheme.secondaryContainer), contentAlignment = Alignment.Center) {
            BoldIcon(icon, null, Modifier.size(22.dp), tint = scheme.onSecondaryContainer, weight = 0.55.dp)
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = scheme.onSurface)
            Text(subtitle, fontSize = 13.sp, fontWeight = FontWeight.Light, color = scheme.onSurfaceVariant)
        }
        when {
            trailing != null -> trailing()
            onClick != null -> BoldIcon(
                Icons.AutoMirrored.Rounded.KeyboardArrowLeft, null, Modifier.size(24.dp),
                tint = scheme.onSurfaceVariant, weight = 0.5.dp,
            )
        }
    }
}

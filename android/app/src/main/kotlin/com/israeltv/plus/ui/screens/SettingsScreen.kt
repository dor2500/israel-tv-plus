package com.israeltv.plus.ui.screens

import android.content.Intent
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
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.SystemUpdate
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material3.Button
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
import com.israeltv.plus.data.ChannelData
import com.israeltv.plus.data.UpdateState
import com.israeltv.plus.data.Updater
import com.israeltv.plus.ui.components.BoldIcon
import com.israeltv.plus.ui.components.ProfileAvatar
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Icon
import androidx.compose.material3.HorizontalDivider
import com.israeltv.plus.ui.components.feedPadding
import com.israeltv.plus.ui.components.rememberBitChordFlingBehavior
import com.israeltv.plus.ui.components.specularBorder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SettingsScreen(data: ChannelData?, prefs: AppPrefs, updater: Updater, onProfiles: () -> Unit, onStyle: () -> Unit) {
    val ctx = LocalContext.current
    val haptic = LocalHapticFeedback.current
    LazyColumn(Modifier.fillMaxSize(), contentPadding = feedPadding(withCategories = false), flingBehavior = rememberBitChordFlingBehavior()) {
        item(key = "update") { UpdateCard(updater, Modifier.padding(horizontal = 16.dp).padding(bottom = 28.dp)) }
        item(key = "look") {
            SettingsSectionLabel("תצוגה וצליל")
            SettingsGroup {
                SettingRow(Icons.Rounded.Palette, "צבעים וסגנון", "צבעי הטפט, פלטות נוספות ומצב כהה", onClick = onStyle)
                SettingsDivider()
                SettingRow(
                    Icons.Rounded.Palette,
                    "רקע שחור OLED",
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
                SettingsDivider()
                SettingRow(Icons.Rounded.Face, "פרופילים", "${prefs.profile.name} · ${prefs.profiles.size} פרופילים", onClick = onProfiles) {
                    ProfileAvatar(prefs.avatar, glyphSize = 30.dp)
                }
                SettingsDivider()
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
            Spacer(Modifier.height(28.dp))
        }
        item(key = "about") {
            SettingsSectionLabel("אודות")
            SettingsGroup {
                SettingRow(Icons.Rounded.Tv, "ישראל TV+ " + updater.installedVersion,
                    "${data?.channels?.size ?: 0} ערוצים ותחנות · ${data?.categories?.size ?: 0} קטגוריות")
                SettingsDivider()
                SettingRow(Icons.Rounded.Language, "האתר", "dor2500.github.io/israel-tv-plus", onClick = {
                    ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://dor2500.github.io/israel-tv-plus/")))
                })
                SettingsDivider()
                SettingRow(Icons.Rounded.Code, "קוד המקור ומקורות השידור", "GitHub · SOURCES.md", onClick = {
                    ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/dor2500/israel-tv-plus")))
                })
            }
        }
    }
}

/** Pixel-style system update surface: a large tonal card with the state, the version and the action. */
@Composable
fun UpdateCard(updater: Updater, modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    val haptic = LocalHapticFeedback.current
    val state = updater.state
    val shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp, bottomStart = 32.dp, bottomEnd = 12.dp)
    val available = state is UpdateState.Available || state is UpdateState.Downloading ||
        state is UpdateState.Installing || (state is UpdateState.Failed && state.info != null)
    Column(
        modifier
            .fillMaxWidth()
            .clip(shape)
            .background(if (available) scheme.primaryContainer else scheme.surfaceContainerHigh.copy(alpha = 0.8f))
            .specularBorder(shape)
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

/** Nuvio's SettingsGroup: one flat card (surface, 16 dp corners, hairline border); rows are split by inset hairlines. */
@Composable
private fun SettingsGroup(content: @Composable ColumnScope.() -> Unit) {
    val shape = RoundedCornerShape(16.dp)
    val scheme = MaterialTheme.colorScheme
    Column(
        Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(shape)
            .background(scheme.surfaceContainerHigh.copy(alpha = 0.7f))
            .border(0.5.dp, scheme.outlineVariant.copy(alpha = 0.5f), shape),
        content = content,
    )
}

/** Nuvio's NuvioSectionLabel: small bold muted caption above a card. */
@Composable
private fun SettingsSectionLabel(text: String) {
    Text(
        text,
        Modifier.padding(horizontal = 32.dp).padding(bottom = 10.dp),
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

/** Nuvio's SettingsNavigationRow / SettingsSwitchRow: 36 dp accent-tinted icon chip, 16/14 dp padding, title + muted description. */
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
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.size(36.dp).clip(RoundedCornerShape(12.dp)).background(scheme.primary.copy(alpha = 0.16f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, null, Modifier.size(22.dp), tint = scheme.primary)
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f).padding(end = 12.dp)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, color = scheme.onSurface)
            Spacer(Modifier.height(2.dp))
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = scheme.onSurfaceVariant)
        }
        when {
            trailing != null -> trailing()
            onClick != null -> Icon(
                Icons.AutoMirrored.Rounded.KeyboardArrowLeft, null, Modifier.size(24.dp), tint = scheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun SettingsDivider() {
    HorizontalDivider(Modifier.padding(start = 66.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
}

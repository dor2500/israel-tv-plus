package com.israeltv.plus.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import coil3.compose.AsyncImage
import com.israeltv.plus.data.AppPrefs
import com.israeltv.plus.data.ChannelData
import com.israeltv.plus.data.UpdateState
import com.israeltv.plus.data.Updater
import com.israeltv.plus.data.avatarUrl
import com.israeltv.plus.ui.components.SectionHeader
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
    LazyColumn(Modifier.fillMaxSize(), contentPadding = feedPadding(), flingBehavior = rememberBitChordFlingBehavior()) {
        item(key = "update") { UpdateCard(updater, Modifier.padding(horizontal = 16.dp).padding(bottom = 28.dp)) }
        item(key = "look") {
            SectionHeader("תצוגה וצליל")
            SettingsGroup {
                SettingRow(Icons.Rounded.Palette, "צבעים וסגנון", "צבעי הטפט, פלטות נוספות ומצב כהה", onClick = onStyle)
                SettingRow(Icons.Rounded.Face, "פרופילים", "${prefs.profile.name} · ${prefs.profiles.size} פרופילים", onClick = onProfiles) {
                    AsyncImage(avatarUrl(prefs.avatar), null, Modifier.size(34.dp))
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
            Spacer(Modifier.height(28.dp))
        }
        item(key = "about") {
            SectionHeader("אודות")
            SettingsGroup {
                SettingRow(Icons.Rounded.Tv, "ישראל TV+ " + updater.installedVersion,
                    "${data?.channels?.size ?: 0} ערוצים ותחנות · ${data?.categories?.size ?: 0} קטגוריות")
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
            Icon(
                if (available) Icons.Rounded.SystemUpdate else Icons.Rounded.CheckCircle, null, Modifier.size(30.dp),
                tint = if (available) scheme.onPrimary else scheme.onSecondaryContainer,
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
        Text(title, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = onCard)
        val sub = when (state) {
            is UpdateState.UpToDate -> "גרסה ${updater.installedVersion} · נבדק ב-" +
                SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(state.checkedAt))
            is UpdateState.Available -> "גרסה חדשה של האפליקציה מ-GitHub"
            is UpdateState.Downloading -> "${(state.progress * 100).toInt()}%"
            is UpdateState.Installing -> "אשרו את ההתקנה בחלון של אנדרואיד"
            is UpdateState.Failed -> state.message
            else -> "גרסה ${updater.installedVersion}"
        }
        Text(sub, fontSize = 14.sp, color = onCard.copy(alpha = 0.8f), modifier = Modifier.padding(top = 4.dp))
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
            ) { Text(action.first, fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
private fun SettingsGroup(content: @Composable () -> Unit) {
    val shape = RoundedCornerShape(28.dp)
    Column(
        Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.7f))
            .specularBorder(shape),
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
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(40.dp).clip(CircleShape).background(scheme.secondaryContainer), contentAlignment = Alignment.Center) {
            Icon(icon, null, Modifier.size(22.dp), tint = scheme.onSecondaryContainer)
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = scheme.onSurface)
            Text(subtitle, fontSize = 13.sp, color = scheme.onSurfaceVariant)
        }
        when {
            trailing != null -> trailing()
            onClick != null -> Icon(Icons.AutoMirrored.Rounded.KeyboardArrowLeft, null, tint = scheme.onSurfaceVariant)
        }
    }
}

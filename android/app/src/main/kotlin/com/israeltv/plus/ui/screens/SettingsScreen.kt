package com.israeltv.plus.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.CloudDone
import androidx.compose.material.icons.rounded.CloudSync
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.QrCode2
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material.icons.rounded.SystemUpdate
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.israeltv.plus.data.AppPrefs
import com.israeltv.plus.data.ChannelData
import com.israeltv.plus.data.GoogleSync
import com.israeltv.plus.data.GoogleUser
import com.israeltv.plus.data.UpdateState
import com.israeltv.plus.data.Updater
import com.israeltv.plus.ui.components.BoldIcon
import com.israeltv.plus.ui.components.ProfileAvatar
import com.israeltv.plus.ui.components.SectionHeader
import com.israeltv.plus.ui.components.feedPadding
import com.israeltv.plus.ui.components.rememberBitChordFlingBehavior
import com.israeltv.plus.ui.components.specularBorder
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SettingsScreen(data: ChannelData?, prefs: AppPrefs, updater: Updater, onProfiles: () -> Unit, onStyle: () -> Unit) {
    val ctx = LocalContext.current
    val haptic = LocalHapticFeedback.current
    LazyColumn(Modifier.fillMaxSize(), contentPadding = feedPadding(withCategories = false), flingBehavior = rememberBitChordFlingBehavior()) {
        item(key = "google_sync") {
            GoogleSyncCard(prefs, Modifier.padding(horizontal = 16.dp).padding(bottom = 22.dp))
        }
        item(key = "update") { UpdateCard(updater, Modifier.padding(horizontal = 16.dp).padding(bottom = 28.dp)) }
        item(key = "look") {
            SectionHeader("תצוגה וצליל")
            SettingsGroup {
                SettingRow(Icons.Rounded.Palette, "צבעים וסגנון", "צבעי הטפט, פלטות נוספות ומצב כהה", onClick = onStyle)
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

/**
 * Google Sign-In & Cloud Sync surface:
 * - Supports 1-tap Android Account Picker, QR Code Pairing (`pair.html#mode=google`), and direct Gmail input.
 * - Automatically backs up and syncs profiles, favorite channels, watch history, and theme colors across devices.
 */
@Composable
fun GoogleSyncCard(prefs: AppPrefs, modifier: Modifier = Modifier) {
    val ctx = LocalContext.current
    val scheme = MaterialTheme.colorScheme
    val haptic = LocalHapticFeedback.current
    val user = prefs.googleUser
    var manualDialog by remember { mutableStateOf(false) }
    var startWithQr by remember { mutableStateOf(false) }

    val accountPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val parsed = GoogleSync.parseSignInResult(result.data)
        if (parsed != null) {
            prefs.setGoogleAccount(parsed)
        } else {
            startWithQr = false
            manualDialog = true
        }
    }

    val shape = RoundedCornerShape(28.dp)
    Column(
        modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                Brush.linearGradient(
                    listOf(
                        scheme.surfaceContainerHigh.copy(alpha = 0.86f),
                        scheme.primaryContainer.copy(alpha = 0.34f),
                    )
                )
            )
            .specularBorder(shape)
            .padding(22.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(if (user != null) scheme.primary else Color.White.copy(alpha = 0.12f))
                    .border(1.dp, Color.White.copy(alpha = 0.24f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                if (user != null && user.photoUrl.isNotBlank()) {
                    AsyncImage(
                        model = user.photoUrl,
                        contentDescription = user.displayName,
                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                        contentScale = ContentScale.Crop,
                    )
                } else {
                    BoldIcon(
                        if (user != null) Icons.Rounded.CloudDone else Icons.Rounded.CloudSync,
                        null,
                        Modifier.size(28.dp),
                        tint = if (user != null) scheme.onPrimary else scheme.primary,
                        weight = 0.7.dp,
                    )
                }
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    if (user != null) user.displayName else "סנכרון חשבון Google",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = scheme.onSurface,
                )
                val subtitle = when {
                    user != null -> {
                        val timeStr = if (user.lastSyncedAt > 0L) {
                            "סונכרן ב-" + SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(user.lastSyncedAt))
                        } else "מחובר לענן"
                        "${user.email} · $timeStr"
                    }
                    else -> "גיבוי וסנכרון אוטומטי של פרופילים, מועדפים והיסטוריית צפייה עם QR או חשבון Google"
                }
                Text(
                    subtitle,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Normal,
                    color = scheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
        }

        AnimatedVisibility(visible = prefs.syncStatus != null) {
            prefs.syncStatus?.let { statusText ->
                Text(
                    statusText,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = scheme.primary,
                    modifier = Modifier.padding(top = 10.dp),
                )
            }
        }

        if (user != null) {
            val pairCode = remember(user.email) { GoogleSync.shortPairingCode(user.email) }
            Text(
                "קוד סנכרון לטלוויזיה חכמה / דפדפן: $pairCode (${user.email})",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = scheme.onSurfaceVariant.copy(alpha = 0.85f),
                modifier = Modifier.padding(top = 6.dp),
            )
        }

        Spacer(Modifier.height(16.dp))

        if (user == null) {
            Row(
                Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                        runCatching {
                            accountPickerLauncher.launch(GoogleSync.buildAccountPickerIntent())
                        }.onFailure {
                            startWithQr = false
                            manualDialog = true
                        }
                    },
                    shape = RoundedCornerShape(50),
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    modifier = Modifier.height(46.dp),
                ) {
                    BoldIcon(Icons.Rounded.AccountCircle, null, Modifier.size(20.dp), tint = scheme.onPrimary, weight = 0.6.dp)
                    Spacer(Modifier.width(8.dp))
                    Text("התחברות עם Google", fontWeight = FontWeight.Bold, fontSize = 14.5.sp)
                }

                FilledTonalButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                        startWithQr = true
                        manualDialog = true
                    },
                    shape = RoundedCornerShape(50),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    modifier = Modifier.height(46.dp),
                ) {
                    BoldIcon(Icons.Rounded.QrCode2, null, Modifier.size(19.dp), tint = scheme.onSecondaryContainer, weight = 0.55.dp)
                    Spacer(Modifier.width(6.dp))
                    Text("סריקת QR / אימייל", fontWeight = FontWeight.SemiBold, fontSize = 13.5.sp)
                }
            }
        } else {
            Row(
                Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                        prefs.syncNow(silent = false)
                    },
                    enabled = !prefs.syncBusy,
                    shape = RoundedCornerShape(50),
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    modifier = Modifier.height(44.dp),
                ) {
                    BoldIcon(Icons.Rounded.Sync, null, Modifier.size(19.dp), tint = scheme.onPrimary, weight = 0.6.dp)
                    Spacer(Modifier.width(8.dp))
                    Text(if (prefs.syncBusy) "מסנכרן…" else "סנכרן עכשיו", fontWeight = FontWeight.Bold)
                }

                FilledTonalButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                        startWithQr = true
                        manualDialog = true
                    },
                    shape = RoundedCornerShape(50),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    modifier = Modifier.height(44.dp),
                ) {
                    BoldIcon(Icons.Rounded.QrCode2, null, Modifier.size(18.dp), tint = scheme.onSecondaryContainer, weight = 0.55.dp)
                    Spacer(Modifier.width(6.dp))
                    Text("חיבור מכשיר ב-QR", fontWeight = FontWeight.SemiBold)
                }

                FilledTonalButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                        prefs.signOutGoogle(ctx)
                    },
                    shape = RoundedCornerShape(50),
                    contentPadding = PaddingValues(horizontal = 18.dp),
                    modifier = Modifier.height(44.dp),
                ) {
                    BoldIcon(Icons.AutoMirrored.Rounded.Logout, null, Modifier.size(18.dp), tint = scheme.onSecondaryContainer, weight = 0.55.dp)
                    Spacer(Modifier.width(6.dp))
                    Text("התנתקות", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }

    if (manualDialog) {
        GoogleAccountDialog(
            initialShowQr = startWithQr,
            onDismiss = { manualDialog = false },
            onPairedUser = { paired ->
                prefs.setGoogleAccount(paired)
                manualDialog = false
            },
            onConfirm = { email, name ->
                GoogleSync.createManualGoogleUser(email, name)?.let {
                    prefs.setGoogleAccount(it)
                }
                manualDialog = false
            },
        )
    }
}

@Composable
fun GoogleAccountDialog(
    initialShowQr: Boolean = false,
    onDismiss: () -> Unit,
    onPairedUser: (GoogleUser) -> Unit,
    onConfirm: (email: String, name: String) -> Unit,
) {
    val scheme = MaterialTheme.colorScheme
    var email by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var showQr by remember { mutableStateOf(initialShowQr) }
    val qrTopic = remember { GoogleSync.generateQrTopic() }
    val pairingUrl = remember(qrTopic) { GoogleSync.buildQrPairingUrl(qrTopic) }
    val qrImageUrl = remember(pairingUrl) { GoogleSync.buildQrImageUrl(pairingUrl) }

    // Poll ntfy.sh while the dialog is open so scanning the QR code on another phone/browser signs in automatically
    LaunchedEffect(qrTopic) {
        while (true) {
            delay(2200)
            val user = GoogleSync.pollQrPairingTopic(qrTopic)
            if (user != null) {
                onPairedUser(user)
                break
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("חיבור חשבון Google (QR או אימייל)", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        },
        text = {
            Column(
                Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    FilledTonalButton(
                        onClick = { showQr = true },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(50),
                        colors = if (showQr) ButtonDefaults.buttonColors() else ButtonDefaults.filledTonalButtonColors(),
                    ) {
                        Text("📱 סריקת QR", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    FilledTonalButton(
                        onClick = { showQr = false },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(50),
                        colors = if (!showQr) ButtonDefaults.buttonColors() else ButtonDefaults.filledTonalButtonColors(),
                    ) {
                        Text("✉️ הזנת Gmail", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }

                if (showQr) {
                    Text(
                        "סרקו את קוד ה-QR עם מצלמת הטלפון שלכם כדי להתחבר לחשבון Google בלחיצה אחת:",
                        fontSize = 13.5.sp,
                        textAlign = TextAlign.Center,
                        color = scheme.onSurfaceVariant,
                    )
                    Box(
                        Modifier
                            .size(210.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White)
                            .padding(10.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        AsyncImage(
                            model = qrImageUrl,
                            contentDescription = "QR Code",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit,
                        )
                    }
                    Text(
                        "ממתין לסריקת ה-QR… ברגע שתאשרו בטלפון, המכשיר יתחבר ויסנכרן אוטומטית.",
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        color = scheme.primary,
                        fontWeight = FontWeight.SemiBold,
                    )
                } else {
                    Text(
                        "הזינו את כתובת ה-Gmail שלכם כדי לסנכרן אוטומטית את הפרופילים, המועדפים והיסטוריית הצפייה בין הטלפון, הטאבלט והטלוויזיה:",
                        fontSize = 13.5.sp,
                        color = scheme.onSurfaceVariant,
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("כתובת Gmail (למשל user@gmail.com)") },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("שם תצוגה (אופציונלי)") },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        },
        confirmButton = {
            if (!showQr) {
                Button(
                    onClick = { if (email.isNotBlank()) onConfirm(email, name) },
                    enabled = email.isNotBlank(),
                    shape = RoundedCornerShape(50),
                ) {
                    Text("התחברות וסנכרון", fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("סגור")
            }
        },
    )
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

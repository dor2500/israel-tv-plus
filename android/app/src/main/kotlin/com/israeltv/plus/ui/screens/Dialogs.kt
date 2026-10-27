package com.israeltv.plus.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SystemUpdate
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.israeltv.plus.data.AVATARS
import com.israeltv.plus.data.AppPrefs
import com.israeltv.plus.data.Channel
import com.israeltv.plus.data.UpdateState
import com.israeltv.plus.data.Updater
import com.israeltv.plus.data.avatarUrl
import com.israeltv.plus.data.shortCategory
import com.israeltv.plus.ui.components.ChannelLogo
import com.israeltv.plus.ui.components.frostedGlass
import coil3.compose.AsyncImage

/** Dimmed backdrop that closes the overlay on tap; the panel on it does not. */
@Composable
private fun Scrim(onDismiss: () -> Unit, content: @Composable () -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.45f))
            .clickable(remember { MutableInteractionSource() }, null, onClick = onDismiss),
        contentAlignment = Alignment.Center,
    ) { content() }
}

private fun Modifier.swallowClicks(): Modifier = clickable(MutableInteractionSource(), null) {}

/** A new GitHub release: frosted-glass modal with what is new and a Material You (wavy) download progress bar. */
@Composable
fun UpdateModal(updater: Updater, onDismiss: () -> Unit) {
    val scheme = MaterialTheme.colorScheme
    val state = updater.state
    val info = when (state) {
        is UpdateState.Available -> state.info
        is UpdateState.Downloading -> state.info
        is UpdateState.Installing -> state.info
        is UpdateState.Failed -> state.info
        else -> null
    } ?: return
    Scrim(onDismiss) {
        Column(
            Modifier
                .padding(24.dp)
                .fillMaxWidth()
                .frostedGlass(RoundedCornerShape(32.dp), scheme.surfaceContainerHigh.copy(alpha = 0.85f), blur = 30.dp)
                .swallowClicks()
                .padding(24.dp)
        ) {
            Box(Modifier.size(52.dp).clip(CircleShape).background(scheme.primary), contentAlignment = Alignment.Center) {
                Icon(Icons.Rounded.SystemUpdate, null, tint = scheme.onPrimary)
            }
            Spacer(Modifier.height(14.dp))
            Text("גרסה ${info.version} זמינה", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = scheme.onSurface)
            Text("מה חדש", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = scheme.primary, modifier = Modifier.padding(top = 12.dp, bottom = 6.dp))
            Column(Modifier.heightIn(max = 220.dp).verticalScroll(rememberScrollState())) {
                info.notes.forEach { note ->
                    Text("•  " + note.replace("**", ""), fontSize = 14.sp, color = scheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 6.dp))
                }
            }
            Spacer(Modifier.height(16.dp))
            when (state) {
                is UpdateState.Downloading -> {
                    LinearWavyProgressIndicator(progress = { state.progress }, modifier = Modifier.fillMaxWidth())
                    Text("${(state.progress * 100).toInt()}%", fontSize = 13.sp, color = scheme.onSurfaceVariant, modifier = Modifier.padding(top = 8.dp))
                }
                is UpdateState.Installing -> Text("אשרו את ההתקנה בחלון של אנדרואיד", color = scheme.onSurfaceVariant)
                else -> {
                    if (state is UpdateState.Failed) Text(state.message, color = scheme.error, fontSize = 13.sp, modifier = Modifier.padding(bottom = 8.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Button({ updater.download(info) }, shape = RoundedCornerShape(50), modifier = Modifier.height(50.dp),
                            contentPadding = PaddingValues(horizontal = 24.dp)) {
                            Text("עדכון עכשיו", fontWeight = FontWeight.ExtraBold)
                        }
                        TextButton(onDismiss) { Text("אחר כך") }
                    }
                }
            }
        }
    }
}

/** Channel details: description, category, country, quality, favourite. */
@Composable
fun ChannelInfoSheet(channel: Channel, logo: String, prefs: AppPrefs, onPlay: () -> Unit, onDismiss: () -> Unit) {
    val scheme = MaterialTheme.colorScheme
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = scheme.surfaceContainer) {
        Column(Modifier.padding(horizontal = 24.dp).navigationBarsPadding().padding(bottom = 24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(width = 96.dp, height = 56.dp).clip(RoundedCornerShape(16.dp)).background(scheme.surfaceContainerHighest), contentAlignment = Alignment.Center) {
                    ChannelLogo(logo, Modifier.size(width = 72.dp, height = 40.dp), channel.isRadio)
                }
                Spacer(Modifier.width(14.dp))
                Column {
                    Text(channel.name, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = scheme.onSurface)
                    Text(
                        listOf(shortCategory(channel.category), channel.countryHe, channel.badge).filter { it.isNotEmpty() }.joinToString(" · "),
                        fontSize = 13.sp, color = scheme.onSurfaceVariant,
                    )
                }
            }
            if (channel.desc.isNotEmpty()) {
                Text(channel.desc, fontSize = 15.sp, color = scheme.onSurface, modifier = Modifier.padding(top = 16.dp))
            }
            Text("ערוץ ${channel.number}", fontSize = 13.sp, color = scheme.onSurfaceVariant, modifier = Modifier.padding(top = 8.dp))
            Spacer(Modifier.height(18.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onPlay, shape = RoundedCornerShape(50), modifier = Modifier.height(44.dp)) { Text("צפייה", fontSize = 14.5.sp, fontWeight = FontWeight.ExtraBold) }
                TextButton({ prefs.toggleFavorite(channel.id) }) {
                    Text(if (prefs.isFavorite(channel.id)) "★ הסר מהרשימה שלי" else "☆ שמור ברשימה שלי")
                }
            }
        }
    }
}

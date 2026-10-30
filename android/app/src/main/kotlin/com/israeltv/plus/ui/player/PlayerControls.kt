package com.israeltv.plus.ui.player

import android.os.Build
import android.view.WindowManager
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Audiotrack
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.Cast
import androidx.compose.material.icons.rounded.CastConnected
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.Crop
import androidx.compose.material.icons.rounded.FitScreen
import androidx.compose.material.icons.rounded.Fullscreen
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.HighQuality
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.PictureInPictureAlt
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.ScreenRotation
import androidx.compose.material.icons.rounded.ScreenShare
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindowProvider
import androidx.media3.common.C
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.common.Tracks
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import com.israeltv.plus.data.CastController
import com.israeltv.plus.data.CastDeviceItem
import com.israeltv.plus.data.CastProtocol
import com.israeltv.plus.data.Channel
import com.israeltv.plus.ui.components.pressPhysics
import com.israeltv.plus.ui.theme.LocalDimens

internal enum class VideoFit(val label: String, val icon: ImageVector, val resizeMode: Int) {
    Fit("16:9", Icons.Rounded.FitScreen, AspectRatioFrameLayout.RESIZE_MODE_FIT),
    Crop("חיתוך", Icons.Rounded.Crop, AspectRatioFrameLayout.RESIZE_MODE_ZOOM),
    Full("מלא", Icons.Rounded.Fullscreen, AspectRatioFrameLayout.RESIZE_MODE_FILL);

    fun next(): VideoFit = entries[(ordinal + 1) % entries.size]
}

private val SheetGlass = Color(0xFF0B0B0E).copy(alpha = 0.62f)

/**
 * The video is a SurfaceView, which an in-app BlurEffect or Haze cannot sample: only the compositor can blur it.
 * Below Android 12, or with cross-window blur off, the sheet's scrim stands in.
 */
@Composable
private fun WindowBlurBehind(radius: Int = 120) {
    val view = LocalView.current
    DisposableEffect(view) {
        val window = (view.parent as? DialogWindowProvider)?.window
        if (window != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            window.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
            window.attributes = window.attributes.apply { blurBehindRadius = radius }
        }
        onDispose { }
    }
}

/** Corners tighten on press with spring physics; the same morph on every player control. */
@Composable
private fun MorphAction(icon: ImageVector, label: String, tint: Color = Color.White, showLabel: Boolean = true, onClick: () -> Unit) {
    val d = LocalDimens.current
    val haptic = LocalHapticFeedback.current
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val corner by animateDpAsState(
        targetValue = if (pressed) 10.dp else d.playerActionHeight / 2,
        animationSpec = spring(dampingRatio = 0.58f, stiffness = Spring.StiffnessMedium),
        label = "playerActionMorph",
    )
    val shape = RoundedCornerShape(corner)
    Row(
        Modifier
            .heightIn(min = d.playerActionHeight)
            .pressPhysics(interaction, tilt = false, glowShape = shape)
            .clip(shape)
            .background(Color.White.copy(alpha = 0.12f))
            .clickable(interaction, null, role = Role.Button) {
                haptic.performHapticFeedback(HapticFeedbackType.VirtualKey)
                onClick()
            }
            .padding(horizontal = if (showLabel) 16.dp else 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, if (showLabel) null else label, Modifier.size(d.playerIcon), tint = tint)
        if (showLabel) {
            Spacer(Modifier.width(8.dp))
            Text(label, color = tint, style = MaterialTheme.typography.labelLarge, maxLines = 1)
        }
    }
}

/** Back and Cast only: the channel identity lives in the bottom bar, YouTube style. */
@Composable
internal fun PlayerTopBar(isCasting: Boolean, onCast: () -> Unit, onBack: () -> Unit) {
    val d = LocalDimens.current
    val statusTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    Row(
        Modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.72f), Color.Transparent)))
            .windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top))
            .padding(top = statusTop.coerceAtLeast(8.dp), bottom = 24.dp, start = d.playerEdge, end = d.playerEdge),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MorphAction(Icons.AutoMirrored.Rounded.ArrowBack, "חזרה", showLabel = false, onClick = onBack)
        MorphAction(
            if (isCasting) Icons.Rounded.CastConnected else Icons.Rounded.Cast,
            "שידור למסך",
            tint = if (isCasting) MaterialTheme.colorScheme.primary else Color.White,
            showLabel = false,
            onClick = onCast,
        )
    }
}

/** In RTL the first child sits on the physical right: Live + channel name there, fit + options on the far left. */
@Composable
internal fun PlayerBottomBar(ch: Channel, fit: VideoFit, onCycleFit: () -> Unit, onOptions: () -> Unit) {
    val d = LocalDimens.current
    val badgeShape = RoundedCornerShape(6.dp)
    Row(
        Modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.82f))))
            .navigationBarsPadding()
            .windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal))
            .padding(start = d.playerEdge, end = d.playerEdge, top = 32.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(Modifier.weight(1f, fill = false), verticalAlignment = Alignment.CenterVertically) {
            Text(
                if (ch.isRadio) "רדיו" else "Live",
                Modifier
                    .clip(badgeShape)
                    .border(1.dp, Color.White.copy(alpha = 0.42f), badgeShape)
                    .padding(horizontal = 8.dp, vertical = 2.dp),
                color = Color.White,
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

@Composable
private fun OptionRow(icon: ImageVector, title: String, value: String? = null, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, null, Modifier.size(22.dp), tint = Color.White.copy(alpha = 0.86f))
        Spacer(Modifier.width(16.dp))
        Text(title, Modifier.weight(1f), color = Color.White, style = MaterialTheme.typography.bodyLarge, maxLines = 1)
        if (value != null) Text(value, color = Color.White.copy(alpha = 0.6f), style = MaterialTheme.typography.bodyMedium, maxLines = 1)
    }
}

/** Everything that left the player's main UI: quality, audio, servers, picture-in-picture, split screen and the rest. */
@Composable
internal fun PlayerOptionsSheet(
    favorite: Boolean,
    qualityLabel: String,
    serverIndex: Int,
    serverCount: Int,
    onSelectServer: (Int) -> Unit,
    onQuality: () -> Unit,
    onAudio: () -> Unit,
    onPip: () -> Unit,
    onMultiView: () -> Unit,
    onChannels: () -> Unit,
    onRotate: () -> Unit,
    onFavorite: () -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = SheetGlass, scrimColor = Color.Black.copy(alpha = 0.32f)) {
        WindowBlurBehind()
        Column(
            Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(start = 8.dp, end = 8.dp, bottom = 24.dp),
        ) {
            OptionRow(Icons.Rounded.HighQuality, "איכות וידאו", qualityLabel, onQuality)
            OptionRow(Icons.Rounded.Audiotrack, "שמע", onClick = onAudio)
            OptionRow(Icons.Rounded.PictureInPictureAlt, "חלון צף", onClick = onPip)
            OptionRow(Icons.Rounded.GridView, "פיצול מסך", onClick = onMultiView)
            OptionRow(Icons.AutoMirrored.Rounded.List, "רשימת ערוצים", onClick = onChannels)
            OptionRow(Icons.Rounded.ScreenRotation, "סיבוב מסך", onClick = onRotate)
            OptionRow(
                if (favorite) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder,
                if (favorite) "הסר מהרשימה שלי" else "שמור ברשימה שלי",
                onClick = onFavorite,
            )
            if (serverCount > 1) {
                Row(Modifier.padding(horizontal = 16.dp).padding(top = 16.dp, bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.SwapHoriz, null, Modifier.size(22.dp), tint = Color.White.copy(alpha = 0.86f))
                    Spacer(Modifier.width(16.dp))
                    Text("שרתים", color = Color.White, style = MaterialTheme.typography.bodyLarge)
                }
                Row(
                    Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    for (i in 0 until serverCount) {
                        val active = i == serverIndex
                        Text(
                            if (i == 0) "ראשי" else "גיבוי $i",
                            Modifier
                                .clip(CircleShape)
                                .background(if (active) Color.White else Color.White.copy(alpha = 0.12f))
                                .clickable(role = Role.Button) { onSelectServer(i) }
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            color = if (active) Color.Black else Color.White,
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                }
            }
        }
    }
}

internal data class VideoQuality(val group: Tracks.Group, val trackIndex: Int, val height: Int, val bitrate: Int)

/** Every distinct resolution the stream offers and this device can decode, highest first. */
internal fun videoQualities(tracks: Tracks): List<VideoQuality> =
    tracks.groups.filter { it.type == C.TRACK_TYPE_VIDEO }
        .flatMap { g ->
            (0 until g.length).filter { g.isTrackSupported(it) }.map { i ->
                val f = g.getTrackFormat(i)
                VideoQuality(g, i, f.height, f.bitrate)
            }
        }
        .filter { it.height > 0 }
        .sortedByDescending { it.height }
        .distinctBy { it.height }

/** The forced height, or null while the player picks by itself. */
internal fun selectedVideoHeight(player: ExoPlayer, tracks: Tracks): Int? {
    val overrides = player.trackSelectionParameters.overrides
    return videoQualities(tracks).firstOrNull { q ->
        overrides[q.group.mediaTrackGroup]?.trackIndices?.contains(q.trackIndex) == true
    }?.height
}

@Composable
internal fun ResolutionDialog(player: ExoPlayer, tracks: Tracks, onDismiss: () -> Unit) {
    val qualities = remember(tracks) { videoQualities(tracks) }
    val selected = selectedVideoHeight(player, tracks)
    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(28.dp), color = MaterialTheme.colorScheme.surfaceContainerHigh) {
            Column(Modifier.padding(vertical = 24.dp, horizontal = 8.dp)) {
                Text(
                    "איכות וידאו",
                    Modifier.padding(horizontal = 16.dp).padding(bottom = 16.dp),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.headlineSmall,
                )
                QualityRow("אוטומטי", "לפי מהירות החיבור", selected == null) {
                    player.trackSelectionParameters = player.trackSelectionParameters.buildUpon()
                        .clearOverridesOfType(C.TRACK_TYPE_VIDEO).build()
                    onDismiss()
                }
                qualities.forEach { q ->
                    val mbps = if (q.bitrate > 0) "%.1f Mb/s".format(q.bitrate / 1_000_000f) else null
                    QualityRow("${q.height}p", mbps, selected == q.height) {
                        player.trackSelectionParameters = player.trackSelectionParameters.buildUpon()
                            .setOverrideForType(TrackSelectionOverride(q.group.mediaTrackGroup, q.trackIndex)).build()
                        onDismiss()
                    }
                }
                if (qualities.size <= 1) {
                    Text(
                        "השידור הזה מגיע באיכות אחת",
                        Modifier.padding(horizontal = 16.dp).padding(top = 8.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}

@Composable
private fun QualityRow(title: String, detail: String?, selected: Boolean, onClick: () -> Unit) {
    val scheme = MaterialTheme.colorScheme
    Row(
        Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) scheme.secondaryContainer else Color.Transparent)
            .clickable(role = Role.RadioButton, onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(title, color = scheme.onSurface, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.weight(1f))
        // the bitrate is Latin: without its own direction "5.7 Mb/s" flips in the RTL row
        if (detail != null) Text("‎$detail", color = scheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
        if (selected) {
            Spacer(Modifier.width(12.dp))
            Icon(Icons.Rounded.Check, null, Modifier.size(20.dp), tint = scheme.onSecondaryContainer)
        }
    }
}

@Composable
internal fun CastSheet(
    channel: Channel,
    castController: CastController,
    onDisconnectAndResumePhone: () -> Unit,
    onDismiss: () -> Unit,
) {
    val scheme = MaterialTheme.colorScheme
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = scheme.surfaceContainer) {
        Column(
            Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp),
        ) {
            Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("שידור למסך", Modifier.weight(1f), color = scheme.onSurface, style = MaterialTheme.typography.headlineSmall)
                if (castController.isScanning) {
                    CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Icon(
                        Icons.Rounded.Refresh,
                        "סריקה מחדש",
                        Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .clickable(role = Role.Button) { castController.startDiscovery(channel) }
                            .padding(8.dp),
                        tint = scheme.onSurfaceVariant,
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            castController.connectedDevice?.let { active ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(scheme.secondaryContainer)
                        .padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Rounded.CastConnected, null, tint = scheme.onSecondaryContainer)
                    Spacer(Modifier.width(16.dp))
                    Text(active.name, Modifier.weight(1f), color = scheme.onSecondaryContainer, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    FilledTonalButton(onDisconnectAndResumePhone) { Text("נתק") }
                }
                Spacer(Modifier.height(8.dp))
            }
            castController.errorMessage?.let { err ->
                Text(err, Modifier.padding(8.dp), color = scheme.error, style = MaterialTheme.typography.bodyMedium)
            }
            castController.devices.forEach { dev ->
                CastDeviceRow(dev, castController.connectedDevice?.id == dev.id) {
                    castController.connectAndCast(dev, channel)
                    onDismiss()
                }
            }
            if (castController.devices.isEmpty()) {
                Text(
                    if (castController.isScanning) "מחפש מכשירים ברשת..." else "לא נמצאו מכשירים. ודאו שהטלפון והטלוויזיה באותה רשת Wi-Fi.",
                    Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 16.dp),
                    color = scheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Spacer(Modifier.height(8.dp))
            CastQuietRow(Icons.Rounded.ScreenShare, "שיקוף מסך") {
                castController.openSystemMirroring()
                onDismiss()
            }
            CastQuietRow(Icons.AutoMirrored.Rounded.OpenInNew, "פתיחה באפליקציה חיצונית") {
                castController.openExternalCaster(channel)
                onDismiss()
            }
        }
    }
}

@Composable
private fun CastDeviceRow(device: CastDeviceItem, selected: Boolean, onClick: () -> Unit) {
    val scheme = MaterialTheme.colorScheme
    Row(
        Modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            if (device.protocol == CastProtocol.GOOGLE_CAST) Icons.Rounded.Cast else Icons.Rounded.Tv,
            null,
            Modifier.size(22.dp),
            tint = if (selected) scheme.primary else scheme.onSurfaceVariant,
        )
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(device.name, color = scheme.onSurface, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(device.protocol.badgeHe, color = scheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall, maxLines = 1)
        }
    }
}

@Composable
private fun CastQuietRow(icon: ImageVector, title: String, onClick: () -> Unit) {
    val scheme = MaterialTheme.colorScheme
    Row(
        Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, null, Modifier.size(20.dp), tint = scheme.onSurfaceVariant)
        Spacer(Modifier.width(16.dp))
        Text(title, color = scheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
    }
}

/** "שידור לא זמין": no card and no alarm colour, only a quiet icon, a line of reason and tonal actions over the black. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun PlayerErrorState(
    ch: Channel,
    reason: String,
    hasBackups: Boolean,
    onNextServer: () -> Unit,
    onRetry: () -> Unit,
    onNext: () -> Unit,
) {
    Column(
        Modifier.padding(32.dp).widthIn(max = 420.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(Modifier.size(56.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.08f)), contentAlignment = Alignment.Center) {
            Icon(Icons.Rounded.CloudOff, null, Modifier.size(24.dp), tint = Color.White.copy(alpha = 0.72f))
        }
        Spacer(Modifier.height(16.dp))
        Text("שידור לא זמין", color = Color.White, style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        Text(
            "${ch.name}: $reason",
            color = Color.White.copy(alpha = 0.64f),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(24.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)) {
            FilledTonalButton(onRetry) {
                Icon(Icons.Rounded.Refresh, null, Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("נסו שוב")
            }
            if (hasBackups) {
                FilledTonalButton(onNextServer) {
                    Icon(Icons.Rounded.SwapHoriz, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("שרת גיבוי")
                }
            }
            FilledTonalButton(onNext) {
                Icon(Icons.Rounded.SkipNext, null, Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("ערוץ הבא")
            }
        }
    }
}

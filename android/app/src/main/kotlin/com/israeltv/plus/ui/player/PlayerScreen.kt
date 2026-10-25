package com.israeltv.plus.ui.player

import com.israeltv.plus.ui.theme.RequestDarkSystemBars
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.Role
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.ripple
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.platform.LocalDensity
import com.israeltv.plus.ui.components.LocalPageScope
import com.israeltv.plus.ui.components.pressPhysics
import com.israeltv.plus.ui.components.sharedContainer
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.rounded.Audiotrack
import androidx.compose.material.icons.rounded.Fullscreen
import androidx.compose.material.icons.rounded.FullscreenExit
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PictureInPictureAlt
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.Button
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalAccessibilityManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.ForwardingPlayer
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.common.Tracks
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.session.MediaSession
import androidx.media3.ui.PlayerView
import com.israeltv.plus.data.AppPrefs
import com.israeltv.plus.data.Channel
import com.israeltv.plus.data.USER_AGENT
import com.israeltv.plus.data.playableUrl
import com.israeltv.plus.ui.components.ChannelLogo
import com.israeltv.plus.ui.components.GlassIconButton
import com.israeltv.plus.ui.components.specularBorder
import kotlinx.coroutines.delay
import java.util.Locale
import kotlin.math.abs

private val GlassDark = Color.Black.copy(alpha = 0.42f)

/**
 * The player. Media3 ExoPlayer (HLS + Icecast radio) with proper media AudioAttributes and audio focus.
 * Volume: there is no mute anywhere in the app – the volume is set to 1.0 when the player is built, on every
 * playback state change and on every channel switch. Only the device's own volume / mute keys change loudness.
 */
private fun buildPlayer(context: Context): ExoPlayer {
    val http = DefaultHttpDataSource.Factory()
        .setUserAgent(USER_AGENT)
        .setAllowCrossProtocolRedirects(true)
        .setConnectTimeoutMs(12_000)
        .setReadTimeoutMs(15_000)
    return ExoPlayer.Builder(context)
        .setMediaSourceFactory(DefaultMediaSourceFactory(http))
        .setAudioAttributes(
            AudioAttributes.Builder().setUsage(C.USAGE_MEDIA).setContentType(C.AUDIO_CONTENT_TYPE_MOVIE).build(),
            /* handleAudioFocus = */ true,
        )
        .setHandleAudioBecomingNoisy(true)
        .build()
        .apply { volume = 1.0f }
}

private val NetworkErrors = setOf(
    PlaybackException.ERROR_CODE_IO_UNSPECIFIED,
    PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED,
    PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT,
)

private fun describe(e: PlaybackException): String = when (e.errorCode) {
    PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED,
    PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT -> "השרת לא עונה"
    PlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS -> "השידור לא נמצא בשרת"
    PlaybackException.ERROR_CODE_PARSING_MANIFEST_MALFORMED,
    PlaybackException.ERROR_CODE_PARSING_CONTAINER_MALFORMED -> "פורמט לא נתמך"
    else -> "שגיאת ניגון"
}

@Composable
fun PlayerScreen(
    start: Channel,
    playlist: List<Channel>,
    prefs: AppPrefs,
    logo: (Channel) -> String,
    inPip: Boolean,
    sharedKey: String?,
    onPlaying: (Boolean) -> Unit,
    onEnterPip: () -> Unit,
    onClose: () -> Unit,
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    // the video surface joins once the card has grown into the player, and leaves as soon as it shrinks back
    // (a SurfaceView does not follow the container transform; until then the player is its black frame + logo)
    val page = LocalPageScope.current
    val settled = page == null ||
        (page.transition.currentState == EnterExitState.Visible && page.transition.targetState == EnterExitState.Visible)
    val haptic = LocalHapticFeedback.current
    val player = remember { buildPlayer(context) }
    val list = remember(playlist, start) { playlist.ifEmpty { listOf(start) } }
    var index by remember { mutableIntStateOf(list.indexOfFirst { it.id == start.id }.coerceAtLeast(0)) }
    val current = list[index]
    var buffering by remember { mutableStateOf(true) }
    var playing by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var controls by remember { mutableStateOf(true) }
    var touchTick by remember { mutableIntStateOf(0) }
    var sheet by remember { mutableStateOf<String?>(null) }
    var landscape by remember { mutableStateOf(false) }
    var tracks by remember { mutableStateOf(Tracks.EMPTY) }
    // a dropped connection (tunnel, Wi-Fi to mobile) is retried quietly twice before the error card shows
    var autoRetries by remember { mutableIntStateOf(0) }
    var retrying by remember { mutableStateOf(false) }

    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                player.volume = 1.0f
                buffering = state == Player.STATE_BUFFERING || (state == Player.STATE_IDLE && error == null)
                if (state == Player.STATE_READY) autoRetries = 0
            }
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                playing = isPlaying
                onPlaying(isPlaying)
            }
            override fun onPlayerError(e: PlaybackException) {
                // a live stream paused for long (or a slow network) falls behind the live window: back to the live edge
                if (e.errorCode == PlaybackException.ERROR_CODE_BEHIND_LIVE_WINDOW) {
                    player.seekToDefaultPosition()
                    player.prepare()
                    return
                }
                if (e.errorCode in NetworkErrors && autoRetries < 2) {
                    autoRetries++
                    retrying = true
                    buffering = true
                    return
                }
                error = describe(e)
                buffering = false
            }
            override fun onTracksChanged(t: Tracks) { tracks = t }
        }
        player.addListener(listener)
        onDispose {
            player.removeListener(listener)
            player.release()
            onPlaying(false)
        }
    }

    fun switchTo(i: Int) {
        index = (i + list.size) % list.size
        touchTick++
    }

    // a media session: headset / Bluetooth buttons and the system's media controls (picture-in-picture too).
    // "Next / previous" there change the channel; nothing outside can set the player's volume.
    val nextChannel by rememberUpdatedState { switchTo(index + 1) }
    val previousChannel by rememberUpdatedState { switchTo(index - 1) }
    DisposableEffect(player) {
        val channels = object : ForwardingPlayer(player) {
            override fun getAvailableCommands(): Player.Commands = super.getAvailableCommands().buildUpon()
                .addAll(Player.COMMAND_SEEK_TO_NEXT, Player.COMMAND_SEEK_TO_PREVIOUS)
                .removeAll(Player.COMMAND_SET_VOLUME)
                .build()
            override fun isCommandAvailable(command: Int): Boolean = when (command) {
                Player.COMMAND_SEEK_TO_NEXT, Player.COMMAND_SEEK_TO_PREVIOUS -> true
                Player.COMMAND_SET_VOLUME -> false
                else -> super.isCommandAvailable(command)
            }
            override fun seekToNext() = nextChannel()
            override fun seekToPrevious() = previousChannel()
            override fun seekToNextMediaItem() = nextChannel()
            override fun seekToPreviousMediaItem() = previousChannel()
        }
        val session = runCatching { MediaSession.Builder(context, channels).build() }.getOrNull()
        onDispose { session?.release() }
    }

    // every channel switch (and "נסו שוב"): the stream, full volume, play
    var attempt by remember { mutableIntStateOf(0) }
    LaunchedEffect(current.id, attempt) {
        error = null
        buffering = true
        prefs.addRecent(current.id)
        val url = runCatching { playableUrl(current) }.getOrElse {
            error = "לא התקבלה הרשאת צפייה מ-mako"
            buffering = false
            return@LaunchedEffect
        }
        val item = MediaItem.Builder().setUri(url).setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle(current.name)
                .setArtist("ישראל TV+")
                .apply { logo(current).takeIf { it.isNotEmpty() }?.let { setArtworkUri(Uri.parse(it)) } }
                .build()
        )
        // every TV channel is HLS, also the ones whose address has no .m3u8 (Samsung TV Plus redirects)
        if (current.url.contains(".m3u8", ignoreCase = true) || !current.isRadio) item.setMimeType(MimeTypes.APPLICATION_M3U8)
        player.setMediaItem(item.build())
        player.volume = 1.0f
        player.prepare()
        player.playWhenReady = true
    }

    LaunchedEffect(current.id) { autoRetries = 0 }
    LaunchedEffect(retrying) {
        if (!retrying) return@LaunchedEffect
        delay(2500)
        retrying = false
        attempt++   // a fresh URL too (mako's ticket may have expired meanwhile)
    }

    // controls hide after 4 s of playback without a touch (longer when the phone's accessibility timeout asks for it)
    val a11y = LocalAccessibilityManager.current
    LaunchedEffect(controls, touchTick, playing, buffering, error) {
        if (controls && playing && !buffering && error == null) {
            delay(a11y?.calculateRecommendedTimeoutMillis(4000, containsIcons = true, containsControls = true) ?: 4000)
            controls = false
        }
    }

    // full screen while watching: bars hidden (swipe from the edge shows them, light icons on the video), screen kept on
    RequestDarkSystemBars()
    val view = LocalView.current
    DisposableEffect(Unit) {
        val window = (context as Activity).window
        val ctl = WindowCompat.getInsetsController(window, view)
        ctl.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        ctl.hide(WindowInsetsCompat.Type.systemBars())
        view.keepScreenOn = true
        onDispose {
            ctl.show(WindowInsetsCompat.Type.systemBars())
            view.keepScreenOn = false
            context.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    // the app in the background: pause. A picture-in-picture window is never stopped while it is on screen, so a stop
    // there means the window was closed – that stays paused. Back in front, only what was playing plays on.
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val pipNow by rememberUpdatedState(inPip)
    DisposableEffect(lifecycle) {
        var resume = false
        val obs = LifecycleEventObserver { _, e ->
            when (e) {
                Lifecycle.Event.ON_STOP -> {
                    resume = player.playWhenReady && !pipNow
                    player.pause()
                }
                Lifecycle.Event.ON_START -> if (resume) {
                    resume = false
                    player.volume = 1.0f
                    player.play()
                }
                else -> Unit
            }
        }
        lifecycle.addObserver(obs)
        onDispose { lifecycle.removeObserver(obs) }
    }

    BackHandler {
        if (sheet != null) sheet = null else onClose()
    }

    Box(
        Modifier
            .fillMaxSize()
            .then(if (sharedKey != null) Modifier.sharedContainer(sharedKey, RectangleShape, scaleContent = true) else Modifier)
            .background(Color.Black)
    ) {
        if (settled || inPip) AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                    useController = false
                    artworkDisplayMode = PlayerView.ARTWORK_DISPLAY_MODE_OFF
                    setShutterBackgroundColor(android.graphics.Color.BLACK)
                    setKeepContentOnPlayerReset(false)
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                    this.player = player
                }
            },
            update = { view ->
                // subtitles move up above the dock while the controls are on screen
                val lift = if (controls && !inPip) with(density) { 92.dp.roundToPx() } else 0
                view.subtitleView?.setPadding(0, 0, 0, lift)
            },
            modifier = Modifier.fillMaxSize(),
        )

        // the touch layer over the video (the PlayerView itself would take the touches)
        Box(
            Modifier.fillMaxSize().pointerInputs(
                onTap = { controls = !controls; touchTick++ },
                onSwipe = { up ->
                    haptic.performHapticFeedback(HapticFeedbackType.GestureThresholdActivate)
                    switchTo(if (up) index + 1 else index - 1)
                },
            )
        )

        if (current.isRadio) {
            Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    Modifier.size(180.dp).clip(RoundedCornerShape(40.dp)).background(Color.White.copy(alpha = 0.08f)).specularBorder(RoundedCornerShape(40.dp)),
                    contentAlignment = Alignment.Center,
                ) { ChannelLogo(logo(current), Modifier.size(130.dp), true) }
                Text(current.name, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(top = 16.dp))
            }
        }

        if (inPip) return@Box

        // center: buffering pill OR play controls OR the error card – never two at once
        val center = when {
            error != null -> "error"
            buffering -> "buffering"
            controls -> "controls"
            else -> "none"
        }
        AnimatedContent(
            center,
            Modifier.align(Alignment.Center),
            transitionSpec = { (fadeIn(tween(220)) + scaleIn(initialScale = 0.92f)) togetherWith fadeOut(tween(150)) },
            label = "center",
        ) { mode ->
            when (mode) {
                "buffering" -> BufferingPill()
                "controls" -> Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(26.dp)) {
                    val scheme = MaterialTheme.colorScheme
                    ExpressiveButton(
                        Icons.Rounded.SkipNext, "ערוץ הבא", RoundedCornerShape(24.dp), 52.dp, 72.dp,
                        scheme.secondaryContainer, scheme.onSecondaryContainer,
                    ) { switchTo(index + 1) }
                    ExpressiveButton(
                        if (playing) Icons.Rounded.Pause else Icons.Rounded.PlayArrow, "נגן / השהה", CircleShape, 84.dp, 84.dp,
                        scheme.primaryContainer, scheme.onPrimaryContainer,
                    ) {
                        touchTick++
                        if (player.isPlaying) player.pause() else { player.volume = 1.0f; player.play() }
                    }
                    ExpressiveButton(
                        Icons.Rounded.SkipPrevious, "ערוץ קודם", RoundedCornerShape(24.dp), 52.dp, 72.dp,
                        scheme.secondaryContainer, scheme.onSecondaryContainer,
                    ) { switchTo(index - 1) }
                }
                "error" -> ErrorCard(current, error.orEmpty(), onRetry = { attempt++ }, onNext = { switchTo(index + 1) })
                else -> Spacer(Modifier.size(1.dp))
            }
        }

        AnimatedVisibility(controls || error != null, enter = fadeIn(), exit = fadeOut(), modifier = Modifier.align(Alignment.TopCenter)) {
            TopBar(current, logo(current), onBack = onClose)
        }
        AnimatedVisibility(controls, enter = fadeIn(), exit = fadeOut(), modifier = Modifier.align(Alignment.BottomCenter)) {
            Dock(
                landscape = landscape,
                onItem = { key ->
                    touchTick++
                    haptic.performHapticFeedback(HapticFeedbackType.VirtualKey)
                    when (key) {
                        "pip" -> onEnterPip()
                        "full" -> {
                            landscape = !landscape
                            (context as Activity).requestedOrientation =
                                if (landscape) ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE else ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                        }
                        else -> sheet = key
                    }
                },
            )
        }
    }

    when (sheet) {
        "list" -> ChannelListSheet(list, index, logo, onPick = { switchTo(it); sheet = null }, onDismiss = { sheet = null })
        "audio" -> AudioSheet(player, tracks, onDismiss = { sheet = null })
        "more" -> MoreSheet(current, prefs, onDismiss = { sheet = null })
    }
}

/** Tap toggles the controls; a vertical swipe of 90 px changes the channel (up = next). Only on the video. */
private fun Modifier.pointerInputs(onTap: () -> Unit, onSwipe: (Boolean) -> Unit): Modifier = this
    .pointerInput(Unit) { detectTapGestures(onTap = { onTap() }) }
    .pointerInput(Unit) {
        var total = 0f
        detectVerticalDragGestures(
            onDragStart = { total = 0f },
            onDragEnd = { if (abs(total) > 90f) onSwipe(total < 0) },
            onVerticalDrag = { _, dy -> total += dy },
        )
    }

/** Material 3 Expressive oversized control: a tonal geometric shape with a press bounce and a haptic tick. */
@Composable
private fun ExpressiveButton(
    icon: ImageVector,
    label: String,
    shape: Shape,
    width: Dp,
    height: Dp,
    container: Color,
    content: Color,
    onClick: () -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    val haptic = LocalHapticFeedback.current
    Box(
        Modifier
            .size(width, height)
            .pressPhysics(interaction, tilt = false)
            .clip(shape)
            .background(container)
            .clickable(interaction, ripple()) {
                haptic.performHapticFeedback(HapticFeedbackType.VirtualKey)
                onClick()
            },
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, label, Modifier.size(minOf(width, height) * 0.5f), tint = content)
    }
}

@Composable
private fun BufferingPill() {
    val shape = RoundedCornerShape(28.dp)
    Row(
        Modifier
            .clip(shape)
            .background(GlassDark)
            .specularBorder(shape)
            .padding(horizontal = 22.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CircularWavyProgressIndicator(Modifier.size(30.dp), color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(14.dp))
        Text("מתחבר לשידור...", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun ErrorCard(ch: Channel, reason: String, onRetry: () -> Unit, onNext: () -> Unit) {
    val shape = RoundedCornerShape(32.dp)
    Column(
        Modifier
            .padding(28.dp)
            .clip(shape)
            .background(Color.Black.copy(alpha = 0.6f))
            .specularBorder(shape)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("השידור לא זמין כרגע", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
        Text(
            "לא הצלחנו להתחבר ל\"${ch.name}\" ($reason). ייתכן שהשידור כבוי זמנית.",
            color = Color.White.copy(alpha = 0.75f), fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp, bottom = 18.dp),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(onRetry, shape = RoundedCornerShape(50)) {
                Icon(Icons.Rounded.Refresh, null)
                Spacer(Modifier.width(6.dp))
                Text("נסו שוב", fontWeight = FontWeight.Bold)
            }
            FilledTonalButton(onNext, shape = RoundedCornerShape(50)) { Text("ערוץ הבא", fontWeight = FontWeight.Bold) }
        }
    }
}

/** Back, the channel logo badge, the title, a pulsing red LIVE dot and the quality, over a top scrim. */
@Composable
private fun TopBar(ch: Channel, logo: String, onBack: () -> Unit) {
    val pulse by rememberInfiniteTransition(label = "live")
        .animateFloat(1f, 0.25f, infiniteRepeatable(tween(900), RepeatMode.Reverse), label = "dot")
    Row(
        Modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.75f), Color.Transparent)))
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        GlassIconButton(Icons.AutoMirrored.Rounded.ArrowBack, "חזרה", onBack, size = 44.dp, tint = GlassDark)
        Spacer(Modifier.width(12.dp))
        Box(
            Modifier.size(width = 64.dp, height = 40.dp).clip(RoundedCornerShape(12.dp)).background(Color.White.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) { ChannelLogo(logo, Modifier.size(width = 52.dp, height = 30.dp), ch.isRadio) }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(ch.name, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(8.dp).alpha(pulse).clip(CircleShape).background(Color(0xFFFF3B30)))
                Text(" LIVE", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                if (ch.badge.isNotEmpty()) {
                    Text(
                        ch.badge,
                        Modifier.padding(start = 8.dp).clip(RoundedCornerShape(6.dp)).background(Color.White.copy(alpha = 0.18f)).padding(horizontal = 6.dp),
                        color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold,
                    )
                }
                Text("  ${ch.number}", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
            }
        }
    }
}

/**
 * The compact player dock (max 400 dp wide, 58 dp): one capsule of the glass recipe (deep ink at 62% with the
 * palette tint), the specular edge, evenly spaced icon + label items.
 */
@Composable
private fun Dock(landscape: Boolean, onItem: (String) -> Unit) {
    val shape = RoundedCornerShape(50)
    val glass = MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.18f).compositeOver(Color(0xFF14141C).copy(alpha = 0.62f))
    Row(
        Modifier
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 22.dp)
            .widthIn(max = 400.dp)
            .fillMaxWidth()
            .height(58.dp)
            .clip(shape)
            .background(glass)
            .specularBorder(shape, alpha = 0.32f),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        DockItem(Icons.AutoMirrored.Rounded.List, "ערוצים") { onItem("list") }
        DockItem(Icons.Rounded.Audiotrack, "שמע") { onItem("audio") }
        DockItem(Icons.Rounded.PictureInPictureAlt, "חלון צף") { onItem("pip") }
        DockItem(Icons.Rounded.MoreHoriz, "אפשרויות") { onItem("more") }
        DockItem(if (landscape) Icons.Rounded.FullscreenExit else Icons.Rounded.Fullscreen, "מסך מלא") { onItem("full") }
    }
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.DockItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(
        Modifier.weight(1f).fillMaxHeight().clickable(role = Role.Button, onClick = onClick).semantics(mergeDescendants = true) {},
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(icon, null, Modifier.size(22.dp), tint = Color.White)
        Text(label, color = Color.White.copy(alpha = 0.85f), fontSize = 10.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun ChannelListSheet(list: List<Channel>, current: Int, logo: (Channel) -> String, onPick: (Int) -> Unit, onDismiss: () -> Unit) {
    val scheme = MaterialTheme.colorScheme
    val state = rememberLazyListState(initialFirstVisibleItemIndex = (current - 2).coerceAtLeast(0))
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = scheme.surfaceContainer) {
        LazyColumn(state = state, contentPadding = PaddingValues(bottom = 32.dp)) {
            items(list.size, key = { list[it].id }) { i ->
                val ch = list[i]
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable { onPick(i) }
                        .background(if (i == current) scheme.secondaryContainer else Color.Transparent)
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("${ch.number}", Modifier.width(44.dp), color = scheme.onSurfaceVariant, fontSize = 13.sp)
                    Box(Modifier.size(width = 64.dp, height = 38.dp).clip(RoundedCornerShape(10.dp)).background(scheme.surfaceContainerHighest), contentAlignment = Alignment.Center) {
                        ChannelLogo(logo(ch), Modifier.size(width = 52.dp, height = 28.dp), ch.isRadio)
                    }
                    Spacer(Modifier.width(14.dp))
                    Text(ch.name, color = scheme.onSurface, fontSize = 15.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }
    }
}

/** Audio tracks of the stream (language versions); choosing one never changes the volume. */
@Composable
private fun AudioSheet(player: ExoPlayer, tracks: Tracks, onDismiss: () -> Unit) {
    val scheme = MaterialTheme.colorScheme
    val groups = tracks.groups.filter { it.type == C.TRACK_TYPE_AUDIO }
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = scheme.surfaceContainer) {
        Column(Modifier.padding(horizontal = 24.dp).padding(bottom = 32.dp)) {
            Text("רצועת שמע", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = scheme.onSurface, modifier = Modifier.padding(bottom = 12.dp))
            if (groups.size <= 1) {
                Text("לשידור הזה יש רצועת שמע אחת", color = scheme.onSurfaceVariant)
            }
            if (groups.size > 1) groups.forEach { g ->
                val f = g.getTrackFormat(0)
                val name = f.label ?: f.language?.let { Locale.forLanguageTag(it).getDisplayLanguage(Locale.forLanguageTag("he")) } ?: "שמע"
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (g.isSelected) scheme.secondaryContainer else Color.Transparent)
                        .clickable {
                            player.trackSelectionParameters = player.trackSelectionParameters.buildUpon()
                                .setOverrideForType(TrackSelectionOverride(g.mediaTrackGroup, 0)).build()
                            player.volume = 1.0f
                            onDismiss()
                        }
                        .padding(16.dp)
                ) { Text(name, color = scheme.onSurface, fontSize = 16.sp, fontWeight = FontWeight.Bold) }
            }
        }
    }
}

@Composable
private fun MoreSheet(ch: Channel, prefs: AppPrefs, onDismiss: () -> Unit) {
    val scheme = MaterialTheme.colorScheme
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = scheme.surfaceContainer) {
        Column(Modifier.padding(horizontal = 24.dp).padding(bottom = 32.dp)) {
            Text(ch.name, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = scheme.onSurface)
            if (ch.desc.isNotEmpty()) Text(ch.desc, color = scheme.onSurfaceVariant, modifier = Modifier.padding(top = 6.dp))
            Spacer(Modifier.height(18.dp))
            FilledTonalButton({ prefs.toggleFavorite(ch.id) }, shape = RoundedCornerShape(50)) {
                Text(if (prefs.isFavorite(ch.id)) "★ הסרה מהמועדפים" else "☆ הוספה למועדפים", fontWeight = FontWeight.Bold)
            }
        }
    }
}

package com.israeltv.plus.ui.player

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.material.icons.rounded.BrightnessMedium
import androidx.compose.material.icons.rounded.ClosedCaption
import androidx.compose.material.icons.rounded.Forward10
import androidx.compose.material.icons.rounded.Fullscreen
import androidx.compose.material.icons.rounded.FullscreenExit
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Replay10
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.common.Tracks
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.session.MediaSession
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.israeltv.plus.data.AppPrefs
import com.israeltv.plus.data.Channel
import com.israeltv.plus.data.USER_AGENT
import com.israeltv.plus.data.playableUrl
import com.israeltv.plus.ui.components.ChannelLogo
import com.israeltv.plus.ui.components.GlassIconButton
import com.israeltv.plus.ui.components.LocalPageScope
import com.israeltv.plus.ui.components.sharedContainer
import com.israeltv.plus.ui.components.specularBorder
import com.israeltv.plus.ui.components.tactileBounce
import com.israeltv.plus.ui.components.triggerScrubTick
import kotlinx.coroutines.delay
import okhttp3.OkHttpClient
import java.util.Locale
import java.util.concurrent.TimeUnit

internal val playerHttpClient by lazy {
    OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .followRedirects(true)
        .followSslRedirects(true)
        .addInterceptor { chain ->
            val req = chain.request().newBuilder()
                .header("User-Agent", USER_AGENT)
                .header("Origin", "https://kondoplay.cfd")
                .header("Referer", "https://kondoplay.cfd/")
                .build()
            chain.proceed(req)
        }
        .build()
}

private fun buildPlayer(context: Context): ExoPlayer {
    val httpSourceFactory = OkHttpDataSource.Factory(playerHttpClient)
    return ExoPlayer.Builder(context)
        .setMediaSourceFactory(DefaultMediaSourceFactory(httpSourceFactory))
        .setAudioAttributes(
            AudioAttributes.Builder().setUsage(C.USAGE_MEDIA).setContentType(C.AUDIO_CONTENT_TYPE_MOVIE).build(),
            /* handleAudioFocus = */ true,
        )
        .setHandleAudioBecomingNoisy(true)
        .build()
        .apply { volume = 1.0f }
}

private fun formatTime(ms: Long): String {
    val totalSec = (ms / 1000).coerceAtLeast(0)
    val min = totalSec / 60
    val sec = totalSec % 60
    return String.format(Locale.US, "%02d:%02d", min, sec)
}

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
    val scheme = MaterialTheme.colorScheme
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

    // Time & scrubbing
    var positionMs by remember { mutableLongStateOf(153000L) }
    var durationMs by remember { mutableLongStateOf(2761000L) }

    // Chunky M3 gesture sliders: Brightness (left) & Volume (right)
    var showBrightness by remember { mutableStateOf(false) }
    var brightnessLevel by remember { mutableFloatStateOf(0.75f) }
    var showVolume by remember { mutableStateOf(false) }
    var volumeLevel by remember { mutableFloatStateOf(0.85f) }

    // Active dock action highlight
    var activeDockItem by remember { mutableStateOf<String?>(null) }

    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                buffering = state == Player.STATE_BUFFERING || (state == Player.STATE_IDLE && error == null)
            }
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                playing = isPlaying
                onPlaying(isPlaying)
            }
            override fun onPlayerError(e: PlaybackException) {
                if (e.errorCode == PlaybackException.ERROR_CODE_BEHIND_LIVE_WINDOW) {
                    player.seekToDefaultPosition()
                    player.prepare()
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

    // Media Session for PiP and Bluetooth
    DisposableEffect(player) {
        val session = runCatching {
            MediaSession.Builder(context, player).setId("player-" + System.nanoTime()).build()
        }.getOrNull()
        onDispose { session?.release() }
    }

    fun switchTo(i: Int) {
        index = (i + list.size) % list.size
        touchTick++
    }

    var attempt by remember { mutableIntStateOf(0) }
    LaunchedEffect(current.id, attempt) {
        error = null
        buffering = true
        prefs.addRecent(current.id)
        val url = runCatching { playableUrl(current) }.getOrElse {
            error = "לא התקבלה הרשאת צפייה"
            buffering = false
            return@LaunchedEffect
        }
        val item = MediaItem.Builder().setUri(url).setMediaId(current.id)
            .setMediaMetadata(MediaMetadata.Builder().setTitle(current.name).setStation(current.name).build())
        if (current.url.contains(".m3u8", ignoreCase = true) || !current.isRadio) item.setMimeType(MimeTypes.APPLICATION_M3U8)
        player.setMediaItem(item.build())
        player.volume = 1.0f
        player.prepare()
        player.playWhenReady = true
    }

    // Track playback timestamps
    LaunchedEffect(playing) {
        while (playing) {
            val pos = player.currentPosition
            val dur = player.duration
            if (dur > 0) {
                positionMs = pos
                durationMs = dur
            } else {
                positionMs += 1000L
            }
            delay(1000)
        }
    }

    // Auto-hide controls
    LaunchedEffect(controls, touchTick, playing, buffering, error) {
        if (controls && playing && !buffering && error == null) {
            delay(4500)
            controls = false
        }
    }

    // Auto-hide gesture sliders
    LaunchedEffect(showBrightness) {
        if (showBrightness) {
            delay(1500)
            showBrightness = false
        }
    }
    LaunchedEffect(showVolume) {
        if (showVolume) {
            delay(1500)
            showVolume = false
        }
    }

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

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        var resume = false
        val obs = LifecycleEventObserver { _, e ->
            if (e == Lifecycle.Event.ON_STOP) { resume = player.playWhenReady; player.pause() }
            if (e == Lifecycle.Event.ON_START && resume) { resume = false; player.volume = 1.0f; player.play() }
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
            .background(scheme.background)
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
            update = { playerView ->
                val lift = if (controls && !inPip) with(density) { 108.dp.roundToPx() } else 0
                playerView.subtitleView?.setPadding(0, 0, 0, lift)
            },
            modifier = Modifier.fillMaxSize(),
        )

        // Gesture detection on the video surface
        Box(
            Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { controls = !controls; touchTick++ })
                }
                .pointerInput(Unit) {
                    detectVerticalDragGestures(
                        onDragStart = {},
                        onDragEnd = {},
                        onVerticalDrag = { change, dragAmount ->
                            val screenWidth = size.width
                            val x = change.position.x
                            val delta = -dragAmount / 300f // Up increases, down decreases
                            if (x < screenWidth * 0.4f) {
                                // Left side: Brightness
                                showBrightness = true
                                val prevStep = (brightnessLevel * 20).toInt()
                                brightnessLevel = (brightnessLevel + delta).coerceIn(0.05f, 1f)
                                val newStep = (brightnessLevel * 20).toInt()
                                if (prevStep != newStep) triggerScrubTick(haptic)
                            } else if (x > screenWidth * 0.6f) {
                                // Right side: Volume
                                showVolume = true
                                val prevStep = (volumeLevel * 20).toInt()
                                volumeLevel = (volumeLevel + delta).coerceIn(0f, 1f)
                                player.volume = volumeLevel
                                val newStep = (volumeLevel * 20).toInt()
                                if (prevStep != newStep) triggerScrubTick(haptic)
                            }
                        }
                    )
                }
        )

        // Radio logo display
        if (current.isRadio) {
            Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    Modifier.size(180.dp).clip(RoundedCornerShape(40.dp)).background(scheme.surfaceContainerHighest.copy(alpha = 0.5f)).specularBorder(RoundedCornerShape(40.dp)),
                    contentAlignment = Alignment.Center,
                ) { ChannelLogo(logo(current), Modifier.size(130.dp), true) }
                Text(current.name, color = scheme.onSurface, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(top = 16.dp))
            }
        }

        // Chunky M3 Gesture Slider: Brightness (Left side)
        AnimatedVisibility(
            visible = showBrightness && !inPip,
            enter = fadeIn(tween(180)) + scaleIn(initialScale = 0.85f),
            exit = fadeOut(tween(220)),
            modifier = Modifier.align(Alignment.CenterStart).padding(start = 28.dp),
        ) {
            ChunkySlider(
                value = brightnessLevel,
                icon = Icons.Rounded.BrightnessMedium,
                label = "בהירות",
            )
        }

        // Chunky M3 Gesture Slider: Volume (Right side)
        AnimatedVisibility(
            visible = showVolume && !inPip,
            enter = fadeIn(tween(180)) + scaleIn(initialScale = 0.85f),
            exit = fadeOut(tween(220)),
            modifier = Modifier.align(Alignment.CenterEnd).padding(end = 28.dp),
        ) {
            ChunkySlider(
                value = volumeLevel,
                icon = Icons.AutoMirrored.Rounded.VolumeUp,
                label = "עוצמת שמע",
            )
        }

        if (inPip) return@Box

        // Center controls: Morphing Play/Pause Squircle + 10s skip buttons
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
            label = "center_controls",
        ) { mode ->
            when (mode) {
                "buffering" -> BufferingPill()
                "controls" -> Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(28.dp),
                ) {
                    // Rewind 10s button
                    ExpressiveSkipPill(
                        icon = Icons.Rounded.Replay10,
                        contentDescription = "10 שניות אחורה",
                    ) {
                        touchTick++
                        player.seekTo((player.currentPosition - 10000L).coerceAtLeast(0L))
                    }

                    // Morphing Center Play/Pause Squircle
                    val cornerRadius by animateDpAsState(
                        targetValue = if (playing) 42.dp else 18.dp,
                        animationSpec = spring(dampingRatio = 0.4f, stiffness = 220f),
                        label = "center_corner",
                    )
                    val pillWidth by animateDpAsState(
                        targetValue = if (playing) 84.dp else 108.dp,
                        animationSpec = spring(dampingRatio = 0.4f, stiffness = 220f),
                        label = "center_width",
                    )
                    val morphShape = RoundedCornerShape(cornerRadius)

                    Box(
                        Modifier
                            .size(width = pillWidth, height = 84.dp)
                            .tactileBounce(onClick = {
                                touchTick++
                                if (player.isPlaying) player.pause() else { player.volume = volumeLevel; player.play() }
                            })
                            .clip(morphShape)
                            .background(scheme.primaryContainer.copy(alpha = 0.85f))
                            .specularBorder(morphShape, alpha = 0.35f),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = if (playing) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                            contentDescription = "נגן / השהה",
                            modifier = Modifier.size(42.dp),
                            tint = scheme.onPrimaryContainer,
                        )
                    }

                    // Forward 10s button
                    ExpressiveSkipPill(
                        icon = Icons.Rounded.Forward10,
                        contentDescription = "10 שניות קדימה",
                    ) {
                        touchTick++
                        player.seekTo(player.currentPosition + 10000L)
                    }
                }
                "error" -> ErrorCard(current, error.orEmpty(), onRetry = { attempt++ }, onNext = { switchTo(index + 1) })
                else -> Spacer(Modifier.size(1.dp))
            }
        }

        // Top Bar: Rating tags and channel info
        AnimatedVisibility(
            visible = controls || error != null,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.TopCenter),
        ) {
            NuvioTopBar(current, logo(current), onBack = onClose)
        }

        // Bottom section: Gapped Scrub Bar + Time Capsules + Floating Dock
        AnimatedVisibility(
            visible = controls,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(bottom = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Gapped Scrub Bar & Time Capsules
                NuvioScrubBar(
                    positionMs = positionMs,
                    durationMs = durationMs,
                    onSeek = { targetMs -> player.seekTo(targetMs) },
                )

                Spacer(Modifier.height(8.dp))

                // Bottom floating frosted dock (height = 50.dp, RoundedCornerShape(50))
                NuvioDock(
                    landscape = landscape,
                    activeItem = activeDockItem,
                    onItem = { key ->
                        touchTick++
                        activeDockItem = key
                        when (key) {
                            "speed" -> {
                                val newSpeed = if (player.playbackParameters.speed >= 1.5f) 1.0f else player.playbackParameters.speed + 0.25f
                                player.playbackParameters = PlaybackParameters(newSpeed)
                            }
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
    }

    when (sheet) {
        "list" -> ChannelListSheet(list, index, logo, onPick = { switchTo(it); sheet = null }, onDismiss = { sheet = null })
        "audio" -> AudioSheet(player, tracks, onDismiss = { sheet = null })
        "subs" -> SubtitlesSheet(player, tracks, onDismiss = { sheet = null })
    }
}

/** Chunky Material 3 gesture pill slider (44.dp x 180.dp, RoundedCornerShape(50)). */
@Composable
private fun ChunkySlider(
    value: Float,
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
) {
    val scheme = MaterialTheme.colorScheme
    val pillShape = RoundedCornerShape(50)
    Box(
        modifier = modifier
            .width(44.dp)
            .height(180.dp)
            .clip(pillShape)
            .background(scheme.surfaceContainerHighest.copy(alpha = 0.85f))
            .specularBorder(pillShape, alpha = 0.32f),
        contentAlignment = Alignment.BottomCenter,
    ) {
        // Filled primary track
        Box(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight(value.coerceIn(0f, 1f))
                .clip(pillShape)
                .background(scheme.primary)
        )
        // Icon
        Box(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                icon,
                label,
                Modifier.size(22.dp),
                tint = if (value > 0.16f) scheme.onPrimary else scheme.onSurfaceVariant,
            )
        }
    }
}

/** 10s skip button with tactile bounce. */
@Composable
private fun ExpressiveSkipPill(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
) {
    val scheme = MaterialTheme.colorScheme
    val pillShape = RoundedCornerShape(26.dp)
    Box(
        Modifier
            .size(width = 54.dp, height = 74.dp)
            .tactileBounce(onClick = onClick)
            .clip(pillShape)
            .background(scheme.secondaryContainer.copy(alpha = 0.85f))
            .specularBorder(pillShape, alpha = 0.28f),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription, Modifier.size(30.dp), tint = scheme.onSecondaryContainer)
    }
}

/**
 * Nuvio-style gapped scrub bar and time pills:
 * Time capsules in secondaryContainer.copy(alpha = 0.7f) with stretched variable typography.
 */
@Composable
private fun NuvioScrubBar(
    positionMs: Long,
    durationMs: Long,
    onSeek: (Long) -> Unit,
) {
    val scheme = MaterialTheme.colorScheme
    val haptic = LocalHapticFeedback.current
    val progress = (positionMs.toFloat() / durationMs.coerceAtLeast(1L)).coerceIn(0f, 1f)
    var isDragging by remember { mutableStateOf(false) }
    var scrubProgress by remember { mutableFloatStateOf(progress) }
    val displayProgress = if (isDragging) scrubProgress else progress

    Row(
        Modifier
            .fillMaxWidth()
            .widthIn(max = 680.dp)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Left time capsule (02:33)
        Box(
            Modifier
                .clip(RoundedCornerShape(50))
                .background(scheme.secondaryContainer.copy(alpha = 0.70f))
                .padding(horizontal = 10.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = formatTime(if (isDragging) (scrubProgress * durationMs).toLong() else positionMs),
                color = scheme.onSecondaryContainer,
                fontSize = 12.sp,
                fontWeight = FontWeight(800),
                style = com.israeltv.plus.ui.theme.StretchedNumberStyle,
                modifier = Modifier.graphicsLayer { scaleY = 1.18f },
            )
        }

        // Gapped track
        Box(
            Modifier
                .weight(1f)
                .height(30.dp)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragStart = { offset ->
                            isDragging = true
                            scrubProgress = (offset.x / size.width).coerceIn(0f, 1f)
                            triggerScrubTick(haptic)
                        },
                        onDragEnd = {
                            isDragging = false
                            onSeek((scrubProgress * durationMs).toLong())
                        },
                        onDragCancel = { isDragging = false },
                        onHorizontalDrag = { change, _ ->
                            val newP = (change.position.x / size.width).coerceIn(0f, 1f)
                            val prevStep = (scrubProgress * 40).toInt()
                            val newStep = (newP * 40).toInt()
                            if (prevStep != newStep) triggerScrubTick(haptic)
                            scrubProgress = newP
                        }
                    )
                },
            contentAlignment = Alignment.CenterStart,
        ) {
            // Inactive track
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(scheme.surfaceContainerHighest.copy(alpha = 0.55f))
            )
            // Active track in colorScheme.primary
            Box(
                Modifier
                    .fillMaxWidth(displayProgress)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(scheme.primary)
            )
            // Vertical thumb separator with gap (matching Image 1)
            Box(
                Modifier
                    .fillMaxWidth(displayProgress)
                    .padding(end = 2.dp),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Box(
                    Modifier
                        .size(width = 4.dp, height = 14.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(scheme.primary)
                )
            }
        }

        // Right time capsule (46:01)
        Box(
            Modifier
                .clip(RoundedCornerShape(50))
                .background(scheme.secondaryContainer.copy(alpha = 0.70f))
                .padding(horizontal = 10.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = formatTime(durationMs),
                color = scheme.onSecondaryContainer,
                fontSize = 12.sp,
                fontWeight = FontWeight(800),
                style = com.israeltv.plus.ui.theme.StretchedNumberStyle,
                modifier = Modifier.graphicsLayer { scaleY = 1.18f },
            )
        }
    }
}

/**
 * Nuvio Player Bottom Floating Glass Dock (height = 50.dp, RoundedCornerShape(50)).
 * Tapping any action highlights it with an expanding primaryContainer tonal pill.
 */
@Composable
private fun NuvioDock(
    landscape: Boolean,
    activeItem: String?,
    onItem: (String) -> Unit,
) {
    val scheme = MaterialTheme.colorScheme
    val pillShape = RoundedCornerShape(50)

    Row(
        Modifier
            .padding(horizontal = 20.dp, vertical = 4.dp)
            .widthIn(max = 520.dp)
            .fillMaxWidth()
            .height(50.dp)
            .clip(pillShape)
            .background(scheme.surfaceContainerHigh.copy(alpha = 0.75f))
            .specularBorder(pillShape, alpha = 0.32f)
            .padding(horizontal = 6.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NuvioDockChip(Icons.AutoMirrored.Rounded.List, "ערוצים", active = activeItem == "list") { onItem("list") }
        NuvioDockChip(Icons.Rounded.Audiotrack, "שמע", active = activeItem == "audio") { onItem("audio") }
        NuvioDockChip(Icons.Rounded.ClosedCaption, "כתוביות", active = activeItem == "subs") { onItem("subs") }
        NuvioDockChip(Icons.Rounded.Speed, "מהירות", active = activeItem == "speed") { onItem("speed") }
        NuvioDockChip(
            if (landscape) Icons.Rounded.FullscreenExit else Icons.Rounded.Fullscreen,
            "מסך מלא",
            active = activeItem == "full",
        ) { onItem("full") }
    }
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.NuvioDockChip(
    icon: ImageVector,
    label: String,
    active: Boolean,
    onClick: () -> Unit,
) {
    val scheme = MaterialTheme.colorScheme
    val pillShape = RoundedCornerShape(50)
    val containerColor = if (active) scheme.primaryContainer else Color.Transparent
    val contentColor = if (active) scheme.onPrimaryContainer else scheme.onSurface

    Box(
        Modifier
            .weight(1f)
            .fillMaxHeight()
            .clip(pillShape)
            .background(containerColor)
            .tactileBounce(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Icon(icon, label, Modifier.size(17.dp), tint = contentColor)
            Text(label, color = contentColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

/** Top Bar with rating pills matching Image 1 layout. */
@Composable
private fun NuvioTopBar(ch: Channel, logo: String, onBack: () -> Unit) {
    val scheme = MaterialTheme.colorScheme
    val pulse by rememberInfiniteTransition(label = "live_pulse")
        .animateFloat(1f, 0.25f, infiniteRepeatable(tween(900), RepeatMode.Reverse), label = "dot")

    Row(
        Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(scheme.background.copy(alpha = 0.82f), Color.Transparent)
                )
            )
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        GlassIconButton(Icons.AutoMirrored.Rounded.ArrowBack, "חזרה", onBack, size = 42.dp, tint = scheme.surfaceContainerHighest.copy(alpha = 0.6f))
        Spacer(Modifier.width(12.dp))

        // Content rating metadata pills (Image 1 reference)
        Column(Modifier.weight(1f)) {
            Text(
                ch.name,
                color = scheme.onSurface,
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(top = 2.dp),
            ) {
                Box(Modifier.size(7.dp).alpha(pulse).clip(CircleShape).background(scheme.error))
                Text(
                    "LIVE",
                    color = scheme.onSurface,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    style = com.israeltv.plus.ui.theme.StretchedNumberStyle,
                    modifier = Modifier.graphicsLayer { scaleY = 1.18f },
                )
                if (ch.badge.isNotEmpty()) {
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(scheme.tertiaryContainer)
                            .padding(horizontal = 5.dp, vertical = 1.dp)
                    ) {
                        Text(
                            ch.badge,
                            color = scheme.onTertiaryContainer,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            style = com.israeltv.plus.ui.theme.StretchedNumberStyle,
                            modifier = Modifier.graphicsLayer { scaleY = 1.18f },
                        )
                    }
                }
                Text(
                    "${ch.number}",
                    color = scheme.onSurfaceVariant,
                    fontSize = 11.sp,
                    fontWeight = FontWeight(800),
                    style = com.israeltv.plus.ui.theme.StretchedNumberStyle,
                    modifier = Modifier.graphicsLayer { scaleY = 1.18f },
                )
            }
        }
    }
}

@Composable
private fun BufferingPill() {
    val scheme = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(28.dp)
    Row(
        Modifier
            .clip(shape)
            .background(scheme.surfaceContainerHighest.copy(alpha = 0.85f))
            .specularBorder(shape)
            .padding(horizontal = 22.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CircularWavyProgressIndicator(Modifier.size(28.dp), color = scheme.primary)
        Spacer(Modifier.width(14.dp))
        Text("מתחבר לשידור...", color = scheme.onSurface, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun ErrorCard(ch: Channel, reason: String, onRetry: () -> Unit, onNext: () -> Unit) {
    val scheme = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(32.dp)
    Column(
        Modifier
            .padding(28.dp)
            .clip(shape)
            .background(scheme.surfaceContainerHigh.copy(alpha = 0.9f))
            .specularBorder(shape)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("השידור לא זמין כרגע", color = scheme.onSurface, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
        Text(
            "לא הצלחנו להתחבר ל\"${ch.name}\" ($reason). ייתכן שהשידור כבוי זמנית.",
            color = scheme.onSurfaceVariant, fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp, bottom = 18.dp),
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
                    Text(
                        text = "${ch.number}",
                        modifier = Modifier
                            .width(44.dp)
                            .graphicsLayer { scaleY = 1.18f },
                        color = scheme.onSurfaceVariant,
                        fontSize = 13.sp,
                        fontWeight = FontWeight(800),
                        style = com.israeltv.plus.ui.theme.StretchedNumberStyle,
                    )
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
                            onDismiss()
                        }
                        .padding(16.dp)
                ) { Text(name, color = scheme.onSurface, fontSize = 16.sp, fontWeight = FontWeight.Bold) }
            }
        }
    }
}

@Composable
private fun SubtitlesSheet(player: ExoPlayer, tracks: Tracks, onDismiss: () -> Unit) {
    val scheme = MaterialTheme.colorScheme
    val groups = tracks.groups.filter { it.type == C.TRACK_TYPE_TEXT }
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = scheme.surfaceContainer) {
        Column(Modifier.padding(horizontal = 24.dp).padding(bottom = 32.dp)) {
            Text("כתוביות", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = scheme.onSurface, modifier = Modifier.padding(bottom = 12.dp))
            if (groups.isEmpty()) {
                Text("אין כתוביות זמינות לשידור זה", color = scheme.onSurfaceVariant)
            } else {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (groups.none { it.isSelected }) scheme.secondaryContainer else Color.Transparent)
                        .clickable {
                            player.trackSelectionParameters = player.trackSelectionParameters.buildUpon()
                                .setTrackTypeDisabled(C.TRACK_TYPE_TEXT, true).build()
                            onDismiss()
                        }
                        .padding(16.dp)
                ) { Text("כבוי", color = scheme.onSurface, fontSize = 16.sp, fontWeight = FontWeight.Bold) }

                groups.forEach { g ->
                    val f = g.getTrackFormat(0)
                    val name = f.label ?: f.language?.let { Locale.forLanguageTag(it).getDisplayLanguage(Locale.forLanguageTag("he")) } ?: "כתוביות"
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (g.isSelected) scheme.secondaryContainer else Color.Transparent)
                            .clickable {
                                player.trackSelectionParameters = player.trackSelectionParameters.buildUpon()
                                    .setTrackTypeDisabled(C.TRACK_TYPE_TEXT, false)
                                    .setOverrideForType(TrackSelectionOverride(g.mediaTrackGroup, 0)).build()
                                onDismiss()
                            }
                            .padding(16.dp)
                    ) { Text(name, color = scheme.onSurface, fontSize = 16.sp, fontWeight = FontWeight.Bold) }
                }
            }
        }
    }
}

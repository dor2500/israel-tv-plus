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
import androidx.compose.animation.core.Spring
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
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Audiotrack
import androidx.compose.material.icons.rounded.Cast
import androidx.compose.material.icons.rounded.CastConnected
import androidx.compose.material.icons.rounded.Fullscreen
import androidx.compose.material.icons.rounded.FullscreenExit
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PictureInPictureAlt
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.ScreenShare
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material.icons.rounded.StopCircle
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material.icons.rounded.VolumeOff
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.common.Tracks
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.israeltv.plus.data.AppPrefs
import com.israeltv.plus.data.CastController
import com.israeltv.plus.data.CastDeviceItem
import com.israeltv.plus.data.CastProtocol
import com.israeltv.plus.data.Channel
import com.israeltv.plus.data.USER_AGENT
import com.israeltv.plus.data.playableUrl
import com.israeltv.plus.ui.components.BoldIcon
import com.israeltv.plus.ui.components.ChannelLogo
import com.israeltv.plus.ui.components.GlassIconButton
import com.israeltv.plus.ui.components.LocalPageScope
import com.israeltv.plus.ui.components.pressPhysics
import com.israeltv.plus.ui.components.sharedContainer
import com.israeltv.plus.ui.components.specularBorder
import kotlinx.coroutines.delay
import java.util.Locale
import kotlin.math.abs

private val GlassDark = Color.Black.copy(alpha = 0.24f)

/**
 * The player. Media3 ExoPlayer (HLS + Icecast radio) with proper media AudioAttributes, fast startup buffer,
 * and automatic highest bitrate (1080p FHD) selection.
 */
private fun buildPlayer(context: Context, handleAudioFocus: Boolean = true): ExoPlayer {
    val http = DefaultHttpDataSource.Factory()
        .setUserAgent(USER_AGENT)
        .setAllowCrossProtocolRedirects(true)
        .setConnectTimeoutMs(7_000)
        .setReadTimeoutMs(9_000)
    val loadControl = DefaultLoadControl.Builder()
        .setBufferDurationsMs(
            /* minBufferMs = */ 12_000,
            /* maxBufferMs = */ 45_000,
            /* bufferForPlaybackMs = */ 800,
            /* bufferForPlaybackAfterRebufferMs = */ 1_600,
        )
        .build()
    val trackSelector = DefaultTrackSelector(context).apply {
        parameters = buildUponParameters()
            .setForceHighestSupportedBitrate(true)
            .build()
    }
    return ExoPlayer.Builder(context)
        .setTrackSelector(trackSelector)
        .setLoadControl(loadControl)
        .setMediaSourceFactory(DefaultMediaSourceFactory(http))
        .setAudioAttributes(
            AudioAttributes.Builder().setUsage(C.USAGE_MEDIA).setContentType(C.AUDIO_CONTENT_TYPE_MOVIE).build(),
            /* handleAudioFocus = */ handleAudioFocus,
        )
        .setHandleAudioBecomingNoisy(true)
        .build()
        .apply { volume = 1.0f }
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
    startInMultiView: Boolean = false,
    startInCast: Boolean = false,
    onPlaying: (Boolean) -> Unit,
    onEnterPip: () -> Unit,
    onClose: () -> Unit,
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val page = LocalPageScope.current
    val settled = page == null ||
        (page.transition.currentState == EnterExitState.Visible && page.transition.targetState == EnterExitState.Visible)
    val haptic = LocalHapticFeedback.current
    val player = remember { buildPlayer(context) }
    val castController = remember(context) { CastController(context, scope) }
    val list = remember(playlist, start) { playlist.ifEmpty { listOf(start) } }
    var index by remember { mutableIntStateOf(list.indexOfFirst { it.id == start.id }.coerceAtLeast(0)) }
    val current = list[index]
    var buffering by remember { mutableStateOf(true) }
    var playing by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var controls by remember { mutableStateOf(true) }
    var touchTick by remember { mutableIntStateOf(0) }
    var sheet by remember { mutableStateOf<String?>(if (startInCast) "cast" else null) }
    var landscape by remember { mutableStateOf(false) }
    var tracks by remember { mutableStateOf(Tracks.EMPTY) }
    var attempt by remember { mutableIntStateOf(0) }
    val candidates = remember(current.id, current.url, current.fallbackUrls, current.backups) { current.allCandidateUrls() }
    var sourceIndex by remember(current.id, attempt) { mutableIntStateOf(0) }
    var lastPlayedRawUrl by remember { mutableStateOf("") }
    var multiViewActive by remember { mutableStateOf(startInMultiView) }
    var statusToast by remember(current.id) { mutableStateOf<String?>(null) }

    LaunchedEffect(startInCast) {
        if (startInCast) {
            castController.startDiscovery(current)
        }
    }

    LaunchedEffect(statusToast) {
        if (statusToast != null) {
            delay(2400)
            statusToast = null
        }
    }

    DisposableEffect(castController) {
        onDispose { castController.dispose() }
    }

    // Pause local ExoPlayer while casting to a TV so audio doesn't play on both devices simultaneously
    LaunchedEffect(castController.isCasting, playing) {
        if (castController.isCasting && player.isPlaying) {
            player.pause()
        }
    }

    // When switching channels on the phone while connected to a TV, immediately cast the new channel
    LaunchedEffect(current.id, castController.isCasting) {
        if (castController.isCasting && castController.activeChannelId != current.id) {
            castController.loadChannel(current)
        }
    }

    DisposableEffect(player, candidates) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                player.volume = 1.0f
                buffering = state == Player.STATE_BUFFERING || (state == Player.STATE_IDLE && error == null)
            }
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                playing = isPlaying
                onPlaying(isPlaying || castController.isCasting || multiViewActive)
            }
            override fun onPlayerError(e: PlaybackException) {
                if (sourceIndex + 1 < candidates.size) {
                    buffering = true
                    error = null
                    val nextIdx = sourceIndex + 1
                    statusToast = "🔀 שרת גיבוי (${nextIdx + 1}/${candidates.size})"
                    sourceIndex = nextIdx
                } else {
                    error = describe(e)
                    buffering = false
                }
            }
            override fun onTracksChanged(t: Tracks) { tracks = t }
        }
        player.addListener(listener)
        onDispose {
            player.removeListener(listener)
        }
    }

    DisposableEffect(player) {
        onDispose {
            player.release()
            onPlaying(false)
        }
    }

    fun switchTo(i: Int) {
        index = (i + list.size) % list.size
        touchTick++
    }

    // every channel switch, automatic fallback step, or manual "נסו שוב": resolve stream, full volume, play
    LaunchedEffect(current.id, attempt, sourceIndex, multiViewActive) {
        if (multiViewActive) {
            player.stop()
            player.clearMediaItems()
            buffering = false
            return@LaunchedEffect
        }
        error = null
        buffering = true
        if (sourceIndex == 0) prefs.addRecent(current.id)
        val rawCandidate = candidates.getOrElse(sourceIndex) { current.url }
        val prevWasOttrast = lastPlayedRawUrl.contains("ottrast.com", ignoreCase = true)
        lastPlayedRawUrl = rawCandidate
        player.stop()
        player.clearMediaItems()
        if (prevWasOttrast && rawCandidate.contains("ottrast.com", ignoreCase = true)) {
            delay(250)
        }
        val url = runCatching { playableUrl(rawCandidate, current.auth) }.getOrElse {
            if (sourceIndex + 1 < candidates.size) {
                sourceIndex += 1
                return@LaunchedEffect
            }
            error = "לא התקבלה הרשאת צפייה מ-mako"
            buffering = false
            return@LaunchedEffect
        }
        val item = MediaItem.Builder().setUri(url)
        if (rawCandidate.contains(".m3u8", ignoreCase = true) || !current.isRadio) item.setMimeType(MimeTypes.APPLICATION_M3U8)
        player.setMediaItem(item.build())
        player.volume = 1.0f
        player.prepare()
        player.playWhenReady = !castController.isCasting
        delay(6000)
        if (buffering && !playing && error == null && !castController.isCasting && sourceIndex + 1 < candidates.size) {
            val nextIdx = sourceIndex + 1
            statusToast = "🔀 שרת גיבוי (${nextIdx + 1}/${candidates.size})"
            sourceIndex = nextIdx
        }
    }

    // Cinematic Idle: fade out ALL UI after 5s of no input during playback
    LaunchedEffect(controls, touchTick, playing, buffering, error, castController.isCasting) {
        if (controls && playing && !buffering && error == null && !castController.isCasting) {
            delay(5000)
            controls = false
        }
    }

    // full screen while watching: bars hidden (swipe from the edge shows them), screen kept on
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

    // the app in the background: pause local player (except in picture-in-picture or when casting)
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle, inPip, castController.isCasting, multiViewActive) {
        val obs = LifecycleEventObserver { _, e ->
            if (e == Lifecycle.Event.ON_STOP && !inPip) player.pause()
            if (e == Lifecycle.Event.ON_START && !castController.isCasting && !multiViewActive) {
                player.volume = 1.0f
                player.play()
            }
        }
        lifecycle.addObserver(obs)
        onDispose { lifecycle.removeObserver(obs) }
    }

    BackHandler {
        when {
            sheet != null -> sheet = null
            multiViewActive && !startInMultiView -> multiViewActive = false
            else -> onClose()
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .then(if (sharedKey != null) Modifier.sharedContainer(sharedKey, RectangleShape, scaleContent = true) else Modifier)
            .background(Color.Black)
    ) {
        if ((settled || inPip) && !multiViewActive) AndroidView(
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
            update = { v ->
                val lift = if (controls && !inPip) with(density) { 92.dp.roundToPx() } else 0
                v.subtitleView?.setPadding(0, 0, 0, lift)
            },
            modifier = Modifier.fillMaxSize(),
        )

        // the touch layer over the video
        Box(
            Modifier.fillMaxSize().pointerInputs(
                onTap = { controls = !controls; touchTick++ },
                onSwipe = { up ->
                    haptic.performHapticFeedback(HapticFeedbackType.GestureThresholdActivate)
                    switchTo(if (up) index + 1 else index - 1)
                },
            )
        )

        if (current.isRadio && !castController.isCasting && !castController.isConnecting && !multiViewActive) {
            Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    Modifier.size(180.dp).clip(RoundedCornerShape(40.dp)).background(Color.White.copy(alpha = 0.08f)).specularBorder(RoundedCornerShape(40.dp)),
                    contentAlignment = Alignment.Center,
                ) { ChannelLogo(logo(current), Modifier.size(130.dp), true) }
                Text(current.name, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(top = 16.dp))
            }
        }

        if (inPip) return@Box

        // Toast pill for backup server switch
        AnimatedVisibility(
            visible = statusToast != null,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 96.dp),
        ) {
            Box(
                Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color.Black.copy(alpha = 0.65f))
                    .specularBorder(RoundedCornerShape(50), alpha = 0.35f)
                    .padding(horizontal = 18.dp, vertical = 8.dp)
            ) {
                Text(statusToast.orEmpty(), color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }

        // center: active TV cast card OR error card OR buffering pill OR floating glass play controls
        val center = when {
            multiViewActive -> "none"
            castController.isCasting || castController.isConnecting -> "casting"
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
                "casting" -> CastActiveCard(
                    ch = current,
                    logoUrl = logo(current),
                    castController = castController,
                    onPrev = { switchTo(index - 1) },
                    onNext = { switchTo(index + 1) },
                    onOpenSheet = {
                        castController.startDiscovery(current)
                        sheet = "cast"
                    },
                    onDisconnect = {
                        castController.disconnect()
                        player.volume = 1.0f
                        player.play()
                    },
                )
                "buffering" -> BufferingPill()
                "controls" -> Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(28.dp)) {
                    ExpressiveGlassButton(
                        Icons.Rounded.SkipNext, "ערוץ הבא", 56.dp, 64.dp,
                    ) { switchTo(index + 1) }
                    ExpressiveGlassButton(
                        if (playing) Icons.Rounded.Pause else Icons.Rounded.PlayArrow, "נגן / השהה", 80.dp, 80.dp, primary = true,
                    ) {
                        touchTick++
                        if (player.isPlaying) player.pause() else { player.volume = 1.0f; player.play() }
                    }
                    ExpressiveGlassButton(
                        Icons.Rounded.SkipPrevious, "ערוץ קודם", 56.dp, 64.dp,
                    ) { switchTo(index - 1) }
                }
                "error" -> ErrorCard(
                    ch = current,
                    reason = error.orEmpty(),
                    hasBackups = candidates.size > 1,
                    onNextServer = {
                        val nextIdx = (sourceIndex + 1) % candidates.size
                        sourceIndex = nextIdx
                        statusToast = "🔀 שרת גיבוי (${nextIdx + 1}/${candidates.size})"
                    },
                    onRetry = { attempt++ },
                    onNext = { switchTo(index + 1) },
                )
                else -> Spacer(Modifier.size(1.dp))
            }
        }

        AnimatedVisibility(
            !multiViewActive && (controls || error != null || castController.isCasting || castController.isConnecting),
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.TopCenter),
        ) {
            TopBar(
                ch = current,
                logo = logo(current),
                serverIndex = sourceIndex,
                serverCount = candidates.size,
                isCasting = castController.isCasting || castController.isConnecting,
                onCycleServer = {
                    touchTick++
                    if (candidates.size > 1) {
                        val nextIdx = (sourceIndex + 1) % candidates.size
                        sourceIndex = nextIdx
                        statusToast = "🔀 שרת (${nextIdx + 1}/${candidates.size})"
                    }
                },
                onMultiView = {
                    touchTick++
                    multiViewActive = true
                },
                onCast = {
                    touchTick++
                    castController.startDiscovery(current)
                    sheet = "cast"
                },
                onBack = onClose,
            )
        }
        AnimatedVisibility(
            !multiViewActive && (controls || castController.isCasting),
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter),
        ) {
            Dock(
                landscape = landscape,
                isCasting = castController.isCasting || castController.isConnecting,
                serverIndex = sourceIndex,
                serverCount = candidates.size,
                onItem = { key ->
                    touchTick++
                    haptic.performHapticFeedback(HapticFeedbackType.VirtualKey)
                    when (key) {
                        "pip" -> onEnterPip()
                        "multiview" -> multiViewActive = true
                        "server" -> {
                            if (candidates.size > 1) {
                                val nextIdx = (sourceIndex + 1) % candidates.size
                                sourceIndex = nextIdx
                                statusToast = "🔀 שרת (${nextIdx + 1}/${candidates.size})"
                            } else {
                                statusToast = "לערוץ זה שרת שידור יחיד"
                            }
                        }
                        "full" -> {
                            landscape = !landscape
                            (context as Activity).requestedOrientation =
                                if (landscape) ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE else ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                        }
                        "cast" -> {
                            castController.startDiscovery(current)
                            sheet = "cast"
                        }
                        else -> sheet = key
                    }
                },
            )
        }

        if (multiViewActive) {
            MultiViewOverlay(
                initialChannel = current,
                allChannels = list,
                logo = logo,
                onExpandChannel = { ch ->
                    val targetIdx = list.indexOfFirst { it.id == ch.id }
                    if (targetIdx >= 0) index = targetIdx
                    multiViewActive = false
                },
                onClose = {
                    if (startInMultiView) onClose() else multiViewActive = false
                },
            )
        }
    }

    when (sheet) {
        "list" -> ChannelListSheet(list, index, logo, onPick = { switchTo(it); sheet = null }, onDismiss = { sheet = null })
        "audio" -> AudioSheet(player, tracks, onDismiss = { sheet = null })
        "cast" -> CastSheet(
            channel = current,
            castController = castController,
            onDisconnectAndResumePhone = {
                castController.disconnect()
                player.volume = 1.0f
                player.play()
                sheet = null
            },
            onDismiss = { sheet = null },
        )
        "more" -> MoreSheet(
            ch = current,
            prefs = prefs,
            serverIndex = sourceIndex,
            serverCount = candidates.size,
            onSelectServer = { idx ->
                sourceIndex = idx
                statusToast = "🔀 שרת (${idx + 1}/${candidates.size})"
                sheet = null
            },
            onOpenMultiView = {
                sheet = null
                multiViewActive = true
            },
            onOpenPip = {
                sheet = null
                onEnterPip()
            },
            onOpenCast = {
                castController.startDiscovery(current)
                sheet = "cast"
            },
            onDismiss = { sheet = null },
        )
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

/**
 * Floating BOLD Material Symbol control with subtle transparent glass pill & soft drop shadow
 * that dynamically morphs its shape on press (pill/circle -> squircle).
 */
@Composable
private fun ExpressiveGlassButton(
    icon: ImageVector,
    label: String,
    width: Dp,
    height: Dp,
    primary: Boolean = false,
    onClick: () -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val corner by animateDpAsState(
        targetValue = if (pressed) 16.dp else (minOf(width, height) / 2f),
        animationSpec = spring(dampingRatio = 0.56f, stiffness = Spring.StiffnessMedium),
        label = "playerCenterMorph",
    )
    val shape = RoundedCornerShape(corner)
    val haptic = LocalHapticFeedback.current
    val glassBg = if (primary) Color.White.copy(alpha = 0.20f) else Color.Black.copy(alpha = 0.28f)
    Box(
        Modifier
            .size(width, height)
            .pressPhysics(interaction, tilt = false, glowShape = shape)
            .graphicsLayer {
                shadowElevation = 18.dp.toPx()
                this.shape = shape
                ambientShadowColor = Color.Black.copy(alpha = 0.65f)
                spotShadowColor = Color.Black.copy(alpha = 0.75f)
            }
            .clip(shape)
            .background(glassBg)
            .specularBorder(shape, alpha = if (primary) 0.48f else 0.32f)
            .clickable(interaction, ripple()) {
                haptic.performHapticFeedback(HapticFeedbackType.VirtualKey)
                onClick()
            },
        contentAlignment = Alignment.Center,
    ) {
        BoldIcon(
            icon = icon,
            contentDescription = label,
            modifier = Modifier.size(minOf(width, height) * 0.52f),
            tint = Color.White,
            weight = if (primary) 0.85.dp else 0.7.dp,
        )
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
private fun ErrorCard(
    ch: Channel,
    reason: String,
    hasBackups: Boolean,
    onNextServer: () -> Unit,
    onRetry: () -> Unit,
    onNext: () -> Unit,
) {
    val shape = RoundedCornerShape(32.dp)
    Column(
        Modifier
            .padding(28.dp)
            .clip(shape)
            .background(Color.Black.copy(alpha = 0.55f))
            .specularBorder(shape)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("השידור לא זמין כרגע", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
        Text(
            "לא הצלחנו להתחבר ל\"${ch.name}\" ($reason).",
            color = Color.White.copy(alpha = 0.75f), fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp, bottom = 18.dp),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            if (hasBackups) {
                Button(onNextServer, shape = RoundedCornerShape(50)) {
                    BoldIcon(Icons.Rounded.SwapHoriz, null, Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onPrimary)
                    Spacer(Modifier.width(6.dp))
                    Text("שרת גיבוי", fontWeight = FontWeight.Bold)
                }
            }
            Button(onRetry, shape = RoundedCornerShape(50)) {
                BoldIcon(Icons.Rounded.Refresh, null, Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onPrimary)
                Spacer(Modifier.width(6.dp))
                Text("נסה שוב", fontWeight = FontWeight.Bold)
            }
            FilledTonalButton(onNext, shape = RoundedCornerShape(50)) { Text("ערוץ הבא", fontWeight = FontWeight.Bold) }
        }
    }
}

/** Displayed in the center of the player when actively casting to a TV (Chromecast / Google TV / LG DLNA). */
@Composable
private fun CastActiveCard(
    ch: Channel,
    logoUrl: String,
    castController: CastController,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onOpenSheet: () -> Unit,
    onDisconnect: () -> Unit,
) {
    val scheme = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(32.dp)
    val dev = castController.connectedDevice
    Column(
        Modifier
            .padding(horizontal = 24.dp)
            .widthIn(max = 420.dp)
            .clip(shape)
            .background(Color(0xFF10121A).copy(alpha = 0.84f))
            .specularBorder(shape, alpha = 0.35f)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            Modifier
                .clip(RoundedCornerShape(50))
                .background(scheme.primaryContainer.copy(alpha = 0.25f))
                .padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BoldIcon(Icons.Rounded.CastConnected, null, Modifier.size(18.dp), tint = scheme.primary)
            Spacer(Modifier.width(8.dp))
            Text(
                dev?.protocol?.badgeHe ?: "שידור לטלוויזיה",
                color = scheme.primary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(Modifier.height(14.dp))
        Box(
            Modifier
                .size(width = 96.dp, height = 60.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center,
        ) {
            ChannelLogo(logoUrl, Modifier.size(width = 76.dp, height = 44.dp), ch.isRadio)
        }
        Spacer(Modifier.height(12.dp))
        Text(ch.name, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
        Text(
            castController.statusMessage ?: "משדר אל ${dev?.name ?: "הטלוויזיה"}",
            color = Color.White.copy(alpha = 0.78f),
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp),
        )
        castController.errorMessage?.let { err ->
            Text(
                err,
                color = Color(0xFFFFB4AB),
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
        Spacer(Modifier.height(18.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            ExpressiveGlassButton(Icons.Rounded.SkipNext, "ערוץ הבא", 48.dp, 58.dp, onClick = onNext)
            ExpressiveGlassButton(
                if (castController.remotePlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                "נגן / השהה בטלוויזיה",
                68.dp,
                68.dp,
                primary = true,
                onClick = { castController.togglePlayPause() },
            )
            ExpressiveGlassButton(Icons.Rounded.SkipPrevious, "ערוץ קודם", 48.dp, 58.dp, onClick = onPrev)
        }
        Spacer(Modifier.height(18.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            FilledTonalButton(onClick = onOpenSheet, shape = RoundedCornerShape(50)) {
                BoldIcon(Icons.Rounded.Tv, null, Modifier.size(18.dp), tint = scheme.onSecondaryContainer)
                Spacer(Modifier.width(6.dp))
                Text("אפשרויות", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Button(onClick = onDisconnect, shape = RoundedCornerShape(50)) {
                BoldIcon(Icons.Rounded.StopCircle, null, Modifier.size(18.dp), tint = scheme.onPrimary)
                Spacer(Modifier.width(6.dp))
                Text("החזר לטלפון", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

/** Back, channel logo badge, title, pulsing red LIVE dot, Backup Server pill, Multi-View and Cast buttons over a transparent gradient-blur. */
@Composable
private fun TopBar(
    ch: Channel,
    logo: String,
    serverIndex: Int,
    serverCount: Int,
    isCasting: Boolean,
    onCycleServer: () -> Unit,
    onMultiView: () -> Unit,
    onCast: () -> Unit,
    onBack: () -> Unit,
) {
    val pulse by rememberInfiniteTransition(label = "live")
        .animateFloat(1f, 0.25f, infiniteRepeatable(tween(900), RepeatMode.Reverse), label = "dot")
    Row(
        Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color.Black.copy(alpha = 0.42f),
                        Color.Black.copy(alpha = 0.14f),
                        Color.Transparent,
                    )
                )
            )
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
                if (serverCount > 1) {
                    Text(
                        "שרת ${serverIndex + 1}/$serverCount",
                        Modifier
                            .padding(start = 8.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.White.copy(alpha = 0.16f))
                            .clickable(onClick = onCycleServer)
                            .padding(horizontal = 6.dp, vertical = 1.dp),
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Text("  ${ch.number}", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
            }
        }
        Spacer(Modifier.width(8.dp))
        GlassIconButton(
            icon = Icons.Rounded.GridView,
            contentDescription = "פיצול מסך (Multi-View)",
            onClick = onMultiView,
            size = 44.dp,
            tint = GlassDark,
        )
        Spacer(Modifier.width(8.dp))
        GlassIconButton(
            icon = if (isCasting) Icons.Rounded.CastConnected else Icons.Rounded.Cast,
            contentDescription = "שידור לטלוויזיה (Cast)",
            onClick = onCast,
            size = 44.dp,
            tint = if (isCasting) MaterialTheme.colorScheme.primary.copy(alpha = 0.85f) else GlassDark,
        )
    }
}

/**
 * The compact player dock with ultra-transparent glass (`alpha = 0.22f`), specular border, bold icons and condensed labels.
 */
@Composable
private fun Dock(
    landscape: Boolean,
    isCasting: Boolean,
    serverIndex: Int,
    serverCount: Int,
    onItem: (String) -> Unit,
) {
    val shape = RoundedCornerShape(50)
    val glass = MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.12f).compositeOver(Color(0xFF0E1017).copy(alpha = 0.28f))
    Row(
        Modifier
            .navigationBarsPadding()
            .padding(horizontal = 14.dp, vertical = 20.dp)
            .widthIn(max = 460.dp)
            .fillMaxWidth()
            .height(58.dp)
            .clip(shape)
            .background(glass)
            .specularBorder(shape, alpha = 0.32f),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        DockItem(Icons.AutoMirrored.Rounded.List, "ערוצים") { onItem("list") }
        DockItem(Icons.Rounded.GridView, "פיצול") { onItem("multiview") }
        if (serverCount > 1) {
            DockItem(Icons.Rounded.SwapHoriz, "שרת ${serverIndex + 1}/$serverCount") { onItem("server") }
        }
        DockItem(
            icon = if (isCasting) Icons.Rounded.CastConnected else Icons.Rounded.Cast,
            label = "שידור",
            highlight = isCasting,
        ) { onItem("cast") }
        DockItem(Icons.Rounded.Audiotrack, "שמע") { onItem("audio") }
        DockItem(Icons.Rounded.MoreHoriz, "עוד") { onItem("more") }
        DockItem(if (landscape) Icons.Rounded.FullscreenExit else Icons.Rounded.Fullscreen, "מסך") { onItem("full") }
    }
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.DockItem(
    icon: ImageVector,
    label: String,
    highlight: Boolean = false,
    onClick: () -> Unit,
) {
    val tint = if (highlight) MaterialTheme.colorScheme.primary else Color.White
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val corner by animateDpAsState(
        targetValue = if (pressed) 12.dp else 24.dp,
        animationSpec = spring(dampingRatio = 0.58f, stiffness = Spring.StiffnessMedium),
        label = "dockItemMorph",
    )
    val itemShape = RoundedCornerShape(corner)
    Column(
        Modifier
            .weight(1f)
            .fillMaxHeight()
            .padding(3.dp)
            .pressPhysics(interaction, tilt = false, glowShape = itemShape)
            .clip(itemShape)
            .clickable(interaction, null, onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        BoldIcon(icon, label, Modifier.size(21.dp), tint = tint, weight = 0.65.dp)
        Text(
            label,
            color = if (highlight) tint else Color.White.copy(alpha = 0.85f),
            fontSize = 9.5.sp,
            fontWeight = if (highlight) FontWeight.Bold else FontWeight.Medium,
            maxLines = 1,
        )
    }
}

/**
 * Multi-protocol Cast & Share bottom sheet.
 */
@Composable
private fun CastSheet(
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
                .padding(horizontal = 22.dp)
                .padding(bottom = 32.dp),
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        "שידור לטלוויזיה (Cast & Share)",
                        fontSize = 21.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = scheme.onSurface,
                    )
                    Text(
                        "צפייה ב״${channel.name}״ על המסך הגדול",
                        fontSize = 13.sp,
                        color = scheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
                FilledTonalButton(
                    onClick = { castController.startDiscovery(channel) },
                    shape = RoundedCornerShape(50),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                ) {
                    if (castController.isScanning) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = scheme.onSecondaryContainer,
                        )
                    } else {
                        Icon(Icons.Rounded.Refresh, null, modifier = Modifier.size(16.dp))
                    }
                    Spacer(Modifier.width(6.dp))
                    Text(if (castController.isScanning) "סורק…" else "סרוק שוב", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(16.dp))

            castController.connectedDevice?.let { active ->
                Column(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(scheme.primaryContainer.copy(alpha = 0.45f))
                        .padding(16.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.CastConnected, null, tint = scheme.primary)
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                "מחובר אל ${active.name}",
                                color = scheme.onSurface,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp,
                            )
                            Text(
                                castController.statusMessage ?: active.protocol.labelHe,
                                color = scheme.onSurfaceVariant,
                                fontSize = 12.sp,
                            )
                        }
                        Button(
                            onClick = onDisconnectAndResumePhone,
                            shape = RoundedCornerShape(50),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                        ) {
                            Text("נתק", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
                Spacer(Modifier.height(14.dp))
            }

            castController.errorMessage?.let { err ->
                Column(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(scheme.errorContainer)
                        .padding(14.dp),
                ) {
                    Text(
                        err,
                        color = scheme.onErrorContainer,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
                Spacer(Modifier.height(14.dp))
            }

            Text(
                "1. שידור ישיר לטלוויזיה (Google Cast / Chromecast / DLNA)",
                color = scheme.primary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                "הזרמה ישירה של הערוץ לטלוויזיה באיכות מלאה תוך שמירה על השליטה מהטלפון.",
                color = scheme.onSurfaceVariant,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 2.dp, bottom = 10.dp),
            )

            if (castController.devices.isEmpty()) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(scheme.surfaceContainerHigh)
                        .padding(16.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (castController.isScanning) {
                            CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.5.dp)
                            Spacer(Modifier.width(12.dp))
                            Text(
                                "מחפש מכשירי Chromecast, Android TV וטלוויזיות חכמות (LG / Samsung) ברשת ה-Wi-Fi…",
                                color = scheme.onSurfaceVariant,
                                fontSize = 13.sp,
                            )
                        } else {
                            Icon(Icons.Rounded.Tv, null, tint = scheme.onSurfaceVariant)
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    "לא נמצאו מקלטי Google Cast או DLNA ברשת הנוכחית",
                                    color = scheme.onSurface,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                                Text(
                                    "ודא שהטלפון והטלוויזיה מחוברים לאותה רשת Wi-Fi, או השתמש בשיקוף מסך (Miracast / LG Screen Share) למטה.",
                                    color = scheme.onSurfaceVariant,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(top = 2.dp),
                                )
                            }
                        }
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    castController.devices.forEach { dev ->
                        val isSelected = castController.connectedDevice?.id == dev.id
                        CastDeviceRow(
                            device = dev,
                            selected = isSelected,
                            onClick = {
                                castController.connectAndCast(dev, channel)
                                onDismiss()
                            },
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Text(
                "2. שיקוף מסך אלחוטי (טלוויזיות LG webOS / Miracast / Smart View)",
                color = scheme.primary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
            )
            Column(
                Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(scheme.surfaceContainerHigh)
                    .padding(16.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.ScreenShare, null, tint = scheme.primary, modifier = Modifier.size(26.dp))
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            "שיתוף מסך מלא (Miracast / LG Screen Share)",
                            color = scheme.onSurface,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            "מתאים לטלוויזיות LG webOS ולכל טלוויזיה תומכת Miracast. משקף את מסך הטלפון והשמע ישירות לטלוויזיה ותומך ב-100% מהערוצים.",
                            color = scheme.onSurfaceVariant,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 3.dp),
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                FilledTonalButton(
                    onClick = {
                        castController.openSystemMirroring()
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(50),
                ) {
                    Icon(Icons.Rounded.ScreenShare, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("פתח בורר שיקוף מסך במכשיר (Cast / Miracast)", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                "3. פתיחה באפליקציית שידור חיצונית",
                color = scheme.primary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
            )
            Column(
                Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(scheme.surfaceContainerHigh)
                    .padding(16.dp),
            ) {
                Text(
                    "שליחת קישור השידור החי לאפליקציות כגון Web Video Caster, LG ThinQ או VLC.",
                    color = scheme.onSurfaceVariant,
                    fontSize = 12.sp,
                )
                Spacer(Modifier.height(10.dp))
                OutlinedButton(
                    onClick = {
                        castController.openExternalCaster(channel)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(50),
                ) {
                    Icon(Icons.AutoMirrored.Rounded.OpenInNew, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("שלח שידור לאפליקציה חיצונית", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun CastDeviceRow(
    device: CastDeviceItem,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val scheme = MaterialTheme.colorScheme
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(if (selected) scheme.secondaryContainer else scheme.surfaceContainerHigh)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = if (device.protocol == CastProtocol.GOOGLE_CAST) Icons.Rounded.Cast else Icons.Rounded.Tv,
            contentDescription = null,
            tint = if (selected) scheme.primary else scheme.onSurface,
            modifier = Modifier.size(24.dp),
        )
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(
                device.name,
                color = scheme.onSurface,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                device.description,
                color = scheme.onSurfaceVariant,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(Modifier.width(8.dp))
        Text(
            device.protocol.badgeHe,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(scheme.primary.copy(alpha = 0.14f))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            color = scheme.primary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun ChannelListSheet(list: List<Channel>, current: Int, logo: (Channel) -> String, onPick: (Int) -> Unit, onDismiss: () -> Unit) {
    val scheme = MaterialTheme.colorScheme
    var searchQuery by remember { mutableStateOf("") }
    val filteredIndices = remember(list, searchQuery) {
        val q = searchQuery.trim()
        if (q.isEmpty()) list.indices.toList()
        else list.indices.filter { i ->
            val ch = list[i]
            ch.name.contains(q, ignoreCase = true) ||
                ch.category.contains(q, ignoreCase = true) ||
                ch.number.toString() == q
        }
    }
    val initialIdx = if (searchQuery.isEmpty()) (current - 2).coerceAtLeast(0) else 0
    val state = rememberLazyListState(initialFirstVisibleItemIndex = initialIdx)
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = scheme.surfaceContainer) {
        Column(Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("חיפוש ערוץ או מספר...") },
                leadingIcon = { Icon(Icons.Rounded.Search, null) },
                singleLine = true,
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 8.dp),
            )
            LazyColumn(state = state, contentPadding = PaddingValues(bottom = 32.dp)) {
                items(filteredIndices.size, key = { list[filteredIndices[it]].id }) { pos ->
                    val i = filteredIndices[pos]
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
                        Column(Modifier.weight(1f)) {
                            Text(ch.name, color = scheme.onSurface, fontSize = 15.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            if (ch.category.isNotEmpty()) {
                                Text(ch.category.substringBefore(" ("), color = scheme.onSurfaceVariant, fontSize = 11.sp, maxLines = 1)
                            }
                        }
                    }
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
private fun MoreSheet(
    ch: Channel,
    prefs: AppPrefs,
    serverIndex: Int,
    serverCount: Int,
    onSelectServer: (Int) -> Unit,
    onOpenMultiView: () -> Unit,
    onOpenPip: () -> Unit,
    onOpenCast: () -> Unit,
    onDismiss: () -> Unit,
) {
    val scheme = MaterialTheme.colorScheme
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = scheme.surfaceContainer) {
        Column(
            Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(ch.name, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = scheme.onSurface)
            if (ch.desc.isNotEmpty()) Text(ch.desc, color = scheme.onSurfaceVariant, modifier = Modifier.padding(top = 6.dp))
            Spacer(Modifier.height(18.dp))
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FilledTonalButton({ prefs.toggleFavorite(ch.id) }, shape = RoundedCornerShape(50)) {
                    Text(if (prefs.isFavorite(ch.id)) "★ הסר מהרשימה שלי" else "☆ הוסף לרשימה שלי", fontWeight = FontWeight.Bold)
                }
                FilledTonalButton(onClick = onOpenMultiView, shape = RoundedCornerShape(50)) {
                    Icon(Icons.Rounded.GridView, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("פיצול מסך", fontWeight = FontWeight.Bold)
                }
                FilledTonalButton(onClick = onOpenPip, shape = RoundedCornerShape(50)) {
                    Icon(Icons.Rounded.PictureInPictureAlt, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("חלון צף", fontWeight = FontWeight.Bold)
                }
                FilledTonalButton(onClick = onOpenCast, shape = RoundedCornerShape(50)) {
                    Icon(Icons.Rounded.Cast, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("שידור ל-TV", fontWeight = FontWeight.Bold)
                }
            }
            if (serverCount > 1) {
                Spacer(Modifier.height(18.dp))
                Text(
                    "🔀 שרתי גיבוי (${serverIndex + 1}/$serverCount)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = scheme.primary,
                )
                Spacer(Modifier.height(8.dp))
                Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (i in 0 until serverCount) {
                        val active = i == serverIndex
                        val label = if (i == 0) "שרת ראשי #1" else "שרת גיבוי #${i + 1}"
                        if (active) {
                            Button(onClick = { onSelectServer(i) }, shape = RoundedCornerShape(50)) {
                                Text("✓ $label", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        } else {
                            FilledTonalButton(onClick = { onSelectServer(i) }, shape = RoundedCornerShape(50)) {
                                Text(label, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Orders candidate URLs for a Multi-View cell so multi-connection CDNs (CloudFront, Akamai, MedOne, Redge, etc.)
 * are tried BEFORE single-connection `ottrast.com` streams when another cell is already using `ottrast.com`.
 */
private fun multiViewCandidatesFor(channel: Channel, otherCellsUsingOttrast: Boolean): List<String> {
    val all = channel.allCandidateUrls()
    if (!otherCellsUsingOttrast || all.size <= 1) return all
    val (ottrast, multiCdn) = all.partition {
        it.contains("ottrast.com", ignoreCase = true) || it.contains("212.80.215.72")
    }
    return multiCdn + ottrast
}

/**
 * Split-TV / Multi-View live screen (2, 3, or 4 simultaneous channels) working with ALL channels in the catalog.
 */
@Composable
private fun MultiViewOverlay(
    initialChannel: Channel,
    allChannels: List<Channel>,
    logo: (Channel) -> String,
    onExpandChannel: (Channel) -> Unit,
    onClose: () -> Unit,
) {
    val channels = remember(allChannels, initialChannel) {
        if (allChannels.isNotEmpty()) allChannels else listOf(initialChannel)
    }
    var cellCount by remember { mutableIntStateOf(4) }
    var activeAudioCell by remember { mutableIntStateOf(0) }
    var pickingCellIndex by remember { mutableIntStateOf(-1) }
    val activeUrlsBySlot = remember { mutableStateMapOf<Int, String>() }

    // Default initial 4 channels: current channel + top Israeli multi-CDN channels
    val defaultIds = listOf("kan11", "keshet12", "reshet13", "now14", "i24news_he", "channel16", "sport5")
    var cellChannels by remember(initialChannel.id) {
        val picked = mutableListOf(initialChannel)
        for (cid in defaultIds) {
            if (picked.size >= 4) break
            val found = channels.firstOrNull { it.id == cid && picked.none { p -> p.id == it.id } }
            if (found != null) picked.add(found)
        }
        for (ch in channels) {
            if (picked.size >= 4) break
            if (picked.none { it.id == ch.id }) picked.add(ch)
        }
        while (picked.size < 4) picked.add(initialChannel)
        mutableStateOf(picked.toList())
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0xFF07090E))
            .windowInsetsPadding(WindowInsets.statusBars)
            .navigationBarsPadding()
    ) {
        Column(Modifier.fillMaxSize()) {
            // Top Header
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F131D))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BoldIcon(Icons.Rounded.GridView, null, Modifier.size(22.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text("פיצול מסך · Split-TV", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                        Text("הקש על משבצת לשמע · 🔄 להחלפת כל ערוץ", color = Color.White.copy(alpha = 0.68f), fontSize = 11.sp)
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    listOf(2 to "2", 3 to "3", 4 to "4").forEach { (cnt, label) ->
                        val selected = cellCount == cnt
                        Box(
                            Modifier
                                .clip(RoundedCornerShape(50))
                                .background(if (selected) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.12f))
                                .clickable {
                                    cellCount = cnt
                                    if (activeAudioCell >= cnt) activeAudioCell = 0
                                }
                                .padding(horizontal = 11.dp, vertical = 6.dp),
                        ) {
                            Text(
                                "$label מסכים",
                                color = if (selected) MaterialTheme.colorScheme.onPrimary else Color.White,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Color(0xFFFF3B30).copy(alpha = 0.85f))
                            .clickable(onClick = onClose)
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                    ) {
                        Text("✕ סגור", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }

            // Grid of live cells
            @Composable
            fun RenderSlot(slot: Int, mod: Modifier) {
                val otherUsingOttrast = activeUrlsBySlot.any { (k, u) ->
                    k != slot && k < cellCount && (u.contains("ottrast.com", true) || u.contains("212.80.215.72"))
                }
                MultiViewCell(
                    slotIndex = slot,
                    channel = cellChannels[slot],
                    logoUrl = logo(cellChannels[slot]),
                    otherCellsUsingOttrast = otherUsingOttrast,
                    isAudioActive = activeAudioCell == slot,
                    onUrlChanged = { u -> activeUrlsBySlot[slot] = u },
                    onSelectAudio = { activeAudioCell = slot },
                    onPickChannel = { pickingCellIndex = slot },
                    onExpand = { onExpandChannel(cellChannels[slot]) },
                    modifier = mod,
                )
            }

            when (cellCount) {
                2 -> {
                    Column(Modifier.weight(1f).fillMaxWidth().padding(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        RenderSlot(0, Modifier.weight(1f).fillMaxWidth())
                        RenderSlot(1, Modifier.weight(1f).fillMaxWidth())
                    }
                }
                3 -> {
                    Column(Modifier.weight(1f).fillMaxWidth().padding(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        RenderSlot(0, Modifier.weight(1f).fillMaxWidth())
                        Row(Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            RenderSlot(1, Modifier.weight(1f).fillMaxHeight())
                            RenderSlot(2, Modifier.weight(1f).fillMaxHeight())
                        }
                    }
                }
                else -> {
                    Column(Modifier.weight(1f).fillMaxWidth().padding(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        for (row in 0 until 2) {
                            Row(Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                for (col in 0 until 2) {
                                    RenderSlot(row * 2 + col, Modifier.weight(1f).fillMaxHeight())
                                }
                            }
                        }
                    }
                }
            }
        }

        if (pickingCellIndex in 0..3) {
            val slot = pickingCellIndex
            val curIdx = channels.indexOfFirst { it.id == cellChannels[slot].id }.coerceAtLeast(0)
            ChannelListSheet(
                list = channels,
                current = curIdx,
                logo = logo,
                onPick = { pickedIdx ->
                    val updated = cellChannels.toMutableList()
                    updated[slot] = channels[pickedIdx]
                    cellChannels = updated
                    pickingCellIndex = -1
                },
                onDismiss = { pickingCellIndex = -1 },
            )
        }
    }
}

@Composable
private fun MultiViewCell(
    slotIndex: Int,
    channel: Channel,
    logoUrl: String,
    otherCellsUsingOttrast: Boolean,
    isAudioActive: Boolean,
    onUrlChanged: (String) -> Unit,
    onSelectAudio: () -> Unit,
    onPickChannel: () -> Unit,
    onExpand: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val candidates = remember(channel.id, channel.url, channel.fallbackUrls, channel.backups, otherCellsUsingOttrast) {
        multiViewCandidatesFor(channel, otherCellsUsingOttrast)
    }
    var srvIdx by remember(channel.id) { mutableIntStateOf(0) }
    var cellBuffering by remember(channel.id, srvIdx) { mutableStateOf(true) }
    var cellPlaying by remember(channel.id, srvIdx) { mutableStateOf(false) }
    val cellPlayer = remember { buildPlayer(context, handleAudioFocus = false) }

    DisposableEffect(cellPlayer) {
        onDispose { cellPlayer.release() }
    }

    LaunchedEffect(isAudioActive) {
        cellPlayer.volume = if (isAudioActive) 1.0f else 0f
    }

    DisposableEffect(cellPlayer, candidates) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                cellBuffering = state == Player.STATE_BUFFERING || state == Player.STATE_IDLE
                if (state == Player.STATE_READY) cellPlaying = true
            }
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) cellPlaying = true
            }
            override fun onPlayerError(error: PlaybackException) {
                if (srvIdx + 1 < candidates.size) {
                    srvIdx += 1
                }
            }
        }
        cellPlayer.addListener(listener)
        onDispose { cellPlayer.removeListener(listener) }
    }

    LaunchedEffect(channel.id, srvIdx, candidates) {
        cellBuffering = true
        cellPlaying = false
        val raw = candidates.getOrElse(srvIdx) { channel.url }
        onUrlChanged(raw)
        cellPlayer.stop()
        cellPlayer.clearMediaItems()
        // Stagger initial connection slightly per slot so simultaneous handshakes don't collide
        if (slotIndex > 0 && srvIdx == 0) delay(slotIndex * 90L)
        val resolved = runCatching { playableUrl(raw, channel.auth) }.getOrElse {
            if (srvIdx + 1 < candidates.size) {
                srvIdx += 1
            }
            return@LaunchedEffect
        }
        val itemBuilder = MediaItem.Builder().setUri(resolved)
        if (raw.contains(".m3u8", ignoreCase = true) || !channel.isRadio) {
            itemBuilder.setMimeType(MimeTypes.APPLICATION_M3U8)
        }
        cellPlayer.setMediaItem(itemBuilder.build())
        cellPlayer.volume = if (isAudioActive) 1.0f else 0f
        cellPlayer.prepare()
        cellPlayer.playWhenReady = true
        delay(5500)
        if (!cellPlaying && srvIdx + 1 < candidates.size) {
            srvIdx += 1
        }
    }

    val borderColor = if (isAudioActive) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.16f)
    val borderWidth = if (isAudioActive) 2.5.dp else 1.dp
    val shape = RoundedCornerShape(14.dp)

    Box(
        modifier
            .clip(shape)
            .background(Color.Black)
            .border(borderWidth, borderColor, shape)
            .clickable { onSelectAudio() }
    ) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                    useController = false
                    artworkDisplayMode = PlayerView.ARTWORK_DISPLAY_MODE_OFF
                    setShutterBackgroundColor(android.graphics.Color.BLACK)
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                    this.player = cellPlayer
                }
            },
            modifier = Modifier.fillMaxSize(),
        )

        if (channel.isRadio || cellBuffering) {
            Box(Modifier.align(Alignment.Center), contentAlignment = Alignment.Center) {
                if (channel.isRadio) {
                    ChannelLogo(logoUrl, Modifier.size(64.dp), true)
                } else if (cellBuffering) {
                    CircularProgressIndicator(Modifier.size(28.dp), strokeWidth = 2.5.dp, color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        // Top badge: channel name + audio state
        Row(
            Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.78f), Color.Transparent)))
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                "${channel.number} · ${channel.name}",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.width(6.dp))
            Row(
                Modifier
                    .clip(RoundedCornerShape(50))
                    .background(if (isAudioActive) MaterialTheme.colorScheme.primary else Color.Black.copy(alpha = 0.65f))
                    .padding(horizontal = 8.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    if (isAudioActive) Icons.Rounded.VolumeUp else Icons.Rounded.VolumeOff,
                    contentDescription = null,
                    tint = if (isAudioActive) MaterialTheme.colorScheme.onPrimary else Color.White.copy(alpha = 0.75f),
                    modifier = Modifier.size(13.dp),
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    if (isAudioActive) "שמע פעיל" else "מושתק",
                    color = if (isAudioActive) MaterialTheme.colorScheme.onPrimary else Color.White.copy(alpha = 0.8f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        // Bottom actions: Switch Channel, Backup Server, Expand Fullscreen
        Row(
            Modifier
                .align(Alignment.BottomEnd)
                .padding(6.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            if (candidates.size > 1) {
                Box(
                    Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color.Black.copy(alpha = 0.72f))
                        .clickable { srvIdx = (srvIdx + 1) % candidates.size }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                ) {
                    Text("🔀 ${srvIdx + 1}/${candidates.size}", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
            Box(
                Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color.Black.copy(alpha = 0.72f))
                    .clickable(onClick = onPickChannel)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            ) {
                Text("🔄 ערוץ", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
            Box(
                Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color.Black.copy(alpha = 0.72f))
                    .clickable(onClick = onExpand)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            ) {
                Text("⛶ הרחב", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}


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
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.material.icons.rounded.FastForward
import androidx.compose.material.icons.rounded.FastRewind
import androidx.compose.material.icons.rounded.Fullscreen
import androidx.compose.material.icons.rounded.FullscreenExit
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PictureInPictureAlt
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.ScreenShare
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material.icons.rounded.StopCircle
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material.icons.rounded.VolumeOff
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
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
import com.israeltv.plus.data.ChannelEpg
import com.israeltv.plus.data.EpgProgram
import com.israeltv.plus.data.EpgRepository
import com.israeltv.plus.data.USER_AGENT
import com.israeltv.plus.data.playableUrl
import com.israeltv.plus.ui.components.ChannelLogo
import com.israeltv.plus.ui.components.GlassIconButton
import com.israeltv.plus.ui.components.LocalPageScope
import com.israeltv.plus.ui.components.pressPhysics
import com.israeltv.plus.ui.components.sharedContainer
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
    var sheet by remember { mutableStateOf<String?>(null) }
    var landscape by remember { mutableStateOf(false) }
    var tracks by remember { mutableStateOf(Tracks.EMPTY) }
    var attempt by remember { mutableIntStateOf(0) }
    val candidates = remember(current.id, current.url, current.fallbackUrls, current.backups) { current.allCandidateUrls() }
    var sourceIndex by remember(current.id, attempt) { mutableIntStateOf(0) }
    var lastPlayedRawUrl by remember { mutableStateOf("") }
    var multiViewActive by remember { mutableStateOf(false) }

    // EPG & Catch-Up state
    var nowTickMs by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(30_000)
            nowTickMs = System.currentTimeMillis()
        }
    }
    val epg = remember(current.id, nowTickMs / 60_000L) { EpgRepository.getSchedule(current, nowTickMs) }
    var catchUpOffsetSec by remember(current.id) { mutableLongStateOf(0L) }
    var dvrReloadTrigger by remember(current.id) { mutableIntStateOf(0) }
    var statusToast by remember(current.id) { mutableStateOf<String?>(null) }

    LaunchedEffect(statusToast) {
        if (statusToast != null) {
            delay(2800)
            statusToast = null
        }
    }

    fun applyTimeShift(deltaSec: Long, label: String? = null) {
        touchTick++
        val targetOffset = (catchUpOffsetSec - deltaSec).coerceIn(0L, 6 * 3600L)
        catchUpOffsetSec = targetOffset
        val dur = player.duration
        val pos = player.currentPosition
        if (dur > 0 && pos >= 0) {
            val newPos = (pos + deltaSec * 1000L).coerceIn(0L, dur)
            player.seekTo(newPos)
            player.play()
            statusToast = label ?: if (targetOffset <= 5L) "🔴 שידור חי (LIVE)" else "⏪ Catch-Up: לפני ${targetOffset / 60} דק׳ ו-${targetOffset % 60} שנ׳"
        } else {
            dvrReloadTrigger++
            statusToast = label ?: "⏪ טוען שידור Catch-Up..."
        }
    }

    fun startOverCurrentProgram() {
        val elapsedSec = epg.current.offsetSecondsFromNow(System.currentTimeMillis()).coerceAtLeast(10L)
        val delta = catchUpOffsetSec - elapsedSec
        applyTimeShift(delta, "⏮ מתחיל מחדש: ${epg.current.title}")
    }

    fun playPastProgram(prog: EpgProgram) {
        val agoSec = prog.offsetSecondsFromNow(System.currentTimeMillis()).coerceAtLeast(30L)
        val delta = catchUpOffsetSec - agoSec
        applyTimeShift(delta, "⏪ Catch-Up: ${prog.title} (${prog.startLabel})")
    }

    fun returnToLive() {
        touchTick++
        catchUpOffsetSec = 0L
        if (player.duration > 0) {
            player.seekToDefaultPosition()
            player.play()
        } else {
            dvrReloadTrigger++
        }
        statusToast = "🔴 חזרה לשידור חי (LIVE)"
    }

    DisposableEffect(castController) {
        onDispose { castController.dispose() }
    }

    // Stop local ExoPlayer while casting to a TV so audio doesn't echo and single-connection streams (ottrast / mako) aren't blocked
    LaunchedEffect(castController.isCasting, castController.isConnecting) {
        if (castController.isCasting || castController.isConnecting) {
            player.stop()
            player.clearMediaItems()
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
                if (state == Player.STATE_READY && catchUpOffsetSec > 0L && player.duration > catchUpOffsetSec * 1000L) {
                    val targetPos = (player.duration - catchUpOffsetSec * 1000L).coerceAtLeast(0L)
                    if (abs(player.currentPosition - targetPos) > 15_000L) {
                        player.seekTo(targetPos)
                    }
                }
            }
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                playing = isPlaying
                onPlaying(isPlaying || castController.isCasting)
            }
            override fun onPlayerError(e: PlaybackException) {
                if (sourceIndex + 1 < candidates.size) {
                    buffering = true
                    error = null
                    val nextIdx = sourceIndex + 1
                    statusToast = "🔀 עובר לשרת גיבוי (${nextIdx + 1}/${candidates.size})..."
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

    // every channel switch, automatic fallback step, DVR reload, or manual "נסו שוב": resolve stream, full volume, play
    LaunchedEffect(current.id, attempt, sourceIndex, dvrReloadTrigger, multiViewActive) {
        if (multiViewActive) {
            player.stop()
            player.clearMediaItems()
            buffering = false
            return@LaunchedEffect
        }
        error = null
        buffering = true
        if (sourceIndex == 0 && dvrReloadTrigger == 0) prefs.addRecent(current.id)
        if (castController.isCasting || castController.isConnecting) {
            buffering = false
            return@LaunchedEffect
        }
        val rawCandidate = candidates.getOrElse(sourceIndex) { current.url }

        // Stop previous stream cleanly before opening a new connection (only delay when switching FROM an ottrast stream TO an ottrast stream)
        val prevWasOttrast = lastPlayedRawUrl.contains("ottrast.com", ignoreCase = true)
        lastPlayedRawUrl = rawCandidate
        player.stop()
        player.clearMediaItems()
        if (prevWasOttrast && rawCandidate.contains("ottrast.com", ignoreCase = true)) {
            delay(260)
        }

        val candidateWithDvr = EpgRepository.buildCatchUpUrl(rawCandidate, catchUpOffsetSec)
        val url = runCatching { playableUrl(candidateWithDvr, current.auth) }.getOrElse {
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
        delay(6200)
        if (buffering && !playing && error == null && !castController.isCasting && sourceIndex + 1 < candidates.size) {
            val nextIdx = sourceIndex + 1
            statusToast = "🔀 עובר לשרת גיבוי (${nextIdx + 1}/${candidates.size})..."
            sourceIndex = nextIdx
        }
    }

    // controls hide after 4.5 s of playback without a touch (stay visible while casting)
    LaunchedEffect(controls, touchTick, playing, buffering, error, castController.isCasting) {
        if (controls && playing && !buffering && error == null && !castController.isCasting) {
            delay(4500)
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
    DisposableEffect(lifecycle, inPip, castController.isCasting) {
        val obs = LifecycleEventObserver { _, e ->
            if (e == Lifecycle.Event.ON_STOP && !inPip) player.pause()
            if (e == Lifecycle.Event.ON_START && !castController.isCasting) {
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
            multiViewActive -> multiViewActive = false
            else -> onClose()
        }
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
                val lift = if (controls && !inPip) with(density) { 92.dp.roundToPx() } else 0
                view.subtitleView?.setPadding(0, 0, 0, lift)
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

        if (current.isRadio && !castController.isCasting && !castController.isConnecting) {
            Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    Modifier.size(180.dp).clip(RoundedCornerShape(40.dp)).background(Color.White.copy(alpha = 0.08f)).specularBorder(RoundedCornerShape(40.dp)),
                    contentAlignment = Alignment.Center,
                ) { ChannelLogo(logo(current), Modifier.size(130.dp), true) }
                Text(current.name, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(top = 16.dp))
                Text(epg.current.title, color = Color.White.copy(alpha = 0.78f), fontSize = 14.sp, modifier = Modifier.padding(top = 4.dp))
            }
        }

        if (inPip) return@Box

        // Status toast for Catch-Up / Rewind feedback
        AnimatedVisibility(
            visible = statusToast != null,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 96.dp),
        ) {
            Box(
                Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color.Black.copy(alpha = 0.78f))
                    .specularBorder(RoundedCornerShape(50), alpha = 0.35f)
                    .padding(horizontal = 18.dp, vertical = 8.dp)
            ) {
                Text(statusToast.orEmpty(), color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }

        // center: active TV cast card OR error card OR buffering pill OR play controls – never two at once
        val center = when {
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
                        attempt++
                    },
                )
                "buffering" -> BufferingPill()
                "controls" -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(26.dp)) {
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
                    if (!current.isRadio) {
                        Spacer(Modifier.height(18.dp))
                        CatchUpQuickBar(
                            catchUpOffsetSec = catchUpOffsetSec,
                            serverIndex = sourceIndex,
                            serverCount = candidates.size,
                            onCycleServer = {
                                if (candidates.size > 1) {
                                    val nextIdx = (sourceIndex + 1) % candidates.size
                                    sourceIndex = nextIdx
                                    statusToast = "🔀 שרת גיבוי (${nextIdx + 1}/${candidates.size})"
                                }
                            },
                            onOpenMultiView = { multiViewActive = true },
                            onRewind5m = { applyTimeShift(-300L) },
                            onRewind30s = { applyTimeShift(-30L) },
                            onStartOver = { startOverCurrentProgram() },
                            onForward30s = { applyTimeShift(30L) },
                            onLive = { returnToLive() },
                            onOpenEpg = { sheet = "epg" },
                        )
                    }
                }
                "error" -> ErrorCard(current, error.orEmpty(), onRetry = { attempt++ }, onNext = { switchTo(index + 1) })
                else -> Spacer(Modifier.size(1.dp))
            }
        }

        AnimatedVisibility(
            controls || error != null || castController.isCasting || castController.isConnecting,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.TopCenter),
        ) {
            TopBar(
                ch = current,
                epg = epg,
                catchUpOffsetSec = catchUpOffsetSec,
                logo = logo(current),
                isCasting = castController.isCasting || castController.isConnecting,
                onReturnLive = { returnToLive() },
                onOpenEpg = { touchTick++; sheet = "epg" },
                onCast = {
                    touchTick++
                    castController.startDiscovery(current)
                    sheet = "cast"
                },
                onBack = onClose,
            )
        }
        AnimatedVisibility(
            controls || castController.isCasting,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter),
        ) {
            Dock(
                landscape = landscape,
                isCasting = castController.isCasting || castController.isConnecting,
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
                        "cast" -> {
                            castController.startDiscovery(current)
                            sheet = "cast"
                        }
                        "multiview" -> {
                            multiViewActive = true
                        }
                        "server" -> {
                            if (candidates.size > 1) {
                                val nextIdx = (sourceIndex + 1) % candidates.size
                                sourceIndex = nextIdx
                                statusToast = "🔀 שרת גיבוי (${nextIdx + 1}/${candidates.size})"
                            } else {
                                statusToast = "לערוץ זה שרת שידור יחיד"
                            }
                        }
                        else -> sheet = key
                    }
                },
            )
        }

        if (multiViewActive) {
            MultiViewOverlay(
                initialChannel = current,
                allChannels = list.filter { !it.isRadio },
                logo = logo,
                onExpandChannel = { ch ->
                    val targetIdx = list.indexOfFirst { it.id == ch.id }
                    if (targetIdx >= 0) index = targetIdx
                    multiViewActive = false
                },
                onClose = { multiViewActive = false },
            )
        }
    }

    when (sheet) {
        "list" -> ChannelListSheet(list, index, logo, onPick = { switchTo(it); sheet = null }, onDismiss = { sheet = null })
        "epg" -> EpgCatchUpSheet(
            channel = current,
            epg = epg,
            catchUpOffsetSec = catchUpOffsetSec,
            onRewind = { sec -> applyTimeShift(sec) },
            onStartOver = { startOverCurrentProgram(); sheet = null },
            onPlayPastProgram = { prog -> playPastProgram(prog); sheet = null },
            onReturnLive = { returnToLive(); sheet = null },
            onDismiss = { sheet = null },
        )
        "audio" -> AudioSheet(player, tracks, onDismiss = { sheet = null })
        "cast" -> CastSheet(
            channel = current,
            castController = castController,
            onDisconnectAndResumePhone = {
                castController.disconnect()
                attempt++
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
            onOpenEpg = { sheet = "epg" },
            onOpenCast = {
                castController.startDiscovery(current)
                sheet = "cast"
            },
            onDismiss = { sheet = null },
        )
    }
}

@Composable
private fun CatchUpQuickBar(
    catchUpOffsetSec: Long,
    serverIndex: Int,
    serverCount: Int,
    onCycleServer: () -> Unit,
    onOpenMultiView: () -> Unit,
    onRewind5m: () -> Unit,
    onRewind30s: () -> Unit,
    onStartOver: () -> Unit,
    onForward30s: () -> Unit,
    onLive: () -> Unit,
    onOpenEpg: () -> Unit,
) {
    val shape = RoundedCornerShape(50)
    Row(
        Modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .clip(shape)
            .background(Color.Black.copy(alpha = 0.56f))
            .specularBorder(shape, alpha = 0.3f)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (serverCount > 1) {
            QuickChip("🔀 שרת (${serverIndex + 1}/$serverCount)", onCycleServer)
        }
        QuickChip("📺 פיצול מסך", onOpenMultiView)
        QuickChip("⏮ התחל מחדש", onStartOver)
        QuickChip("⏪ -5 דק׳", onRewind5m)
        QuickChip("⏪ -30 שנ׳", onRewind30s)
        if (catchUpOffsetSec > 0L) {
            QuickChip("⏩ +30 שנ׳", onForward30s)
            QuickChip("🔴 חזור ל-LIVE", onLive, highlight = true)
        }
        QuickChip("📅 לוח שידורים", onOpenEpg)
    }
}

@Composable
private fun QuickChip(label: String, onClick: () -> Unit, highlight: Boolean = false) {
    val bg = if (highlight) Color(0xFFFF3B30).copy(alpha = 0.85f) else Color.White.copy(alpha = 0.14f)
    Box(
        Modifier
            .clip(RoundedCornerShape(50))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
            .background(Color(0xFF10121A).copy(alpha = 0.88f))
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
            Icon(Icons.Rounded.CastConnected, null, tint = scheme.primary, modifier = Modifier.size(18.dp))
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
            ExpressiveButton(
                Icons.Rounded.SkipNext, "ערוץ הבא", RoundedCornerShape(20.dp), 48.dp, 58.dp,
                scheme.secondaryContainer, scheme.onSecondaryContainer, onClick = onNext,
            )
            ExpressiveButton(
                if (castController.remotePlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                "נגן / השהה בטלוויזיה",
                CircleShape,
                68.dp,
                68.dp,
                scheme.primaryContainer,
                scheme.onPrimaryContainer,
                onClick = { castController.togglePlayPause() },
            )
            ExpressiveButton(
                Icons.Rounded.SkipPrevious, "ערוץ קודם", RoundedCornerShape(20.dp), 48.dp, 58.dp,
                scheme.secondaryContainer, scheme.onSecondaryContainer, onClick = onPrev,
            )
        }
        Spacer(Modifier.height(18.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            FilledTonalButton(onClick = onOpenSheet, shape = RoundedCornerShape(50)) {
                Icon(Icons.Rounded.Tv, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("אפשרויות שידור", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Button(onClick = onDisconnect, shape = RoundedCornerShape(50)) {
                Icon(Icons.Rounded.StopCircle, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("החזר לטלפון", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

/** Back, the channel logo badge, the title, EPG current program, pulsing red LIVE dot, and the Cast to TV button. */
@Composable
private fun TopBar(
    ch: Channel,
    epg: ChannelEpg,
    catchUpOffsetSec: Long,
    logo: String,
    isCasting: Boolean,
    onReturnLive: () -> Unit,
    onOpenEpg: () -> Unit,
    onCast: () -> Unit,
    onBack: () -> Unit,
) {
    val pulse by rememberInfiniteTransition(label = "live")
        .animateFloat(1f, 0.25f, infiniteRepeatable(tween(900), RepeatMode.Reverse), label = "dot")
    Row(
        Modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.8f), Color.Transparent)))
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
        Column(Modifier.weight(1f).clickable { onOpenEpg() }) {
            Text(ch.name, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (catchUpOffsetSec > 5L) {
                    Text(
                        "⏪ Catch-Up (-${catchUpOffsetSec / 60} דק׳) · חזור ל-LIVE",
                        Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFFF9500).copy(alpha = 0.85f))
                            .clickable { onReturnLive() }
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                } else {
                    Box(Modifier.size(8.dp).alpha(pulse).clip(CircleShape).background(Color(0xFFFF3B30)))
                    Text(" LIVE", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                }
                if (ch.badge.isNotEmpty()) {
                    Text(
                        ch.badge,
                        Modifier.padding(start = 8.dp).clip(RoundedCornerShape(6.dp)).background(Color.White.copy(alpha = 0.18f)).padding(horizontal = 6.dp),
                        color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold,
                    )
                }
                Text("  ${ch.number}", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
            }
            Text(
                "${epg.current.startLabel}–${epg.current.endLabel} · ${epg.current.title}",
                color = Color.White.copy(alpha = 0.82f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
        Spacer(Modifier.width(8.dp))
        GlassIconButton(
            icon = Icons.Rounded.Schedule,
            contentDescription = "לוח שידורים ו-Catch-Up",
            onClick = onOpenEpg,
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
 * The compact player dock (max 460 dp wide, 58 dp): one capsule of the glass recipe (deep ink at 62% with the
 * palette tint), the specular edge, evenly spaced icon + label items including EPG / Catch-Up.
 */
@Composable
private fun Dock(landscape: Boolean, isCasting: Boolean, onItem: (String) -> Unit) {
    val shape = RoundedCornerShape(50)
    val glass = MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.18f).compositeOver(Color(0xFF14141C).copy(alpha = 0.62f))
    Row(
        Modifier
            .navigationBarsPadding()
            .padding(horizontal = 12.dp, vertical = 22.dp)
            .widthIn(max = 460.dp)
            .fillMaxWidth()
            .height(58.dp)
            .clip(shape)
            .background(glass)
            .specularBorder(shape, alpha = 0.32f),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        DockItem(Icons.AutoMirrored.Rounded.List, "ערוצים") { onItem("list") }
        DockItem(Icons.Rounded.Schedule, "לוח שידורים") { onItem("epg") }
        DockItem(Icons.Rounded.GridView, "פיצול") { onItem("multiview") }
        DockItem(Icons.Rounded.SwapHoriz, "שרת גיבוי") { onItem("server") }
        DockItem(
            icon = if (isCasting) Icons.Rounded.CastConnected else Icons.Rounded.Cast,
            label = "שידור ל-TV",
            highlight = isCasting,
        ) { onItem("cast") }
        DockItem(Icons.Rounded.Audiotrack, "שמע") { onItem("audio") }
        DockItem(Icons.Rounded.MoreHoriz, "אפשרויות") { onItem("more") }
        DockItem(if (landscape) Icons.Rounded.FullscreenExit else Icons.Rounded.Fullscreen, "מסך מלא") { onItem("full") }
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
    Column(
        Modifier.weight(1f).fillMaxHeight().clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(icon, label, Modifier.size(21.dp), tint = tint)
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
 * Full EPG Schedule (Past / Live / Upcoming) + Catch-Up / Live Rewind Bottom Sheet.
 */
@Composable
private fun EpgCatchUpSheet(
    channel: Channel,
    epg: ChannelEpg,
    catchUpOffsetSec: Long,
    onRewind: (Long) -> Unit,
    onStartOver: () -> Unit,
    onPlayPastProgram: (EpgProgram) -> Unit,
    onReturnLive: () -> Unit,
    onDismiss: () -> Unit,
) {
    val scheme = MaterialTheme.colorScheme
    val nowMs = System.currentTimeMillis()
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = scheme.surfaceContainer) {
        Column(
            Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp)
                .padding(bottom = 32.dp),
        ) {
            Text(
                "לוח שידורים ו-Catch-Up · ${channel.name}",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = scheme.onSurface,
            )
            Text(
                "צפייה בלוח השידורים המלא, חזרה אחורה בשידור החי והפעלת תוכניות ששודרו קודם.",
                fontSize = 13.sp,
                color = scheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp, bottom = 14.dp),
            )

            // Catch-Up / Live Rewind Controls Card
            if (!channel.isRadio) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(scheme.surfaceContainerHigh)
                        .padding(14.dp),
                ) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.History, null, tint = scheme.primary, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("שליטת Catch-Up והחזרה אחורה", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = scheme.onSurface)
                        }
                        if (catchUpOffsetSec > 5L) {
                            Text(
                                "לפני ${catchUpOffsetSec / 60} דק׳",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = scheme.primary,
                            )
                        } else {
                            Text("🔴 שידור חי", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF3B30))
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Row(
                        Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        FilledTonalButton(onClick = onStartOver, shape = RoundedCornerShape(50), contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)) {
                            Icon(Icons.Rounded.Replay, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("התחל תוכנית מחדש", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        FilledTonalButton(onClick = { onRewind(-300L) }, shape = RoundedCornerShape(50), contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)) {
                            Icon(Icons.Rounded.FastRewind, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("-5 דק׳", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        FilledTonalButton(onClick = { onRewind(-60L) }, shape = RoundedCornerShape(50), contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)) {
                            Text("-1 דק׳", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        FilledTonalButton(onClick = { onRewind(-30L) }, shape = RoundedCornerShape(50), contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)) {
                            Text("-30 שנ׳", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        if (catchUpOffsetSec > 0L) {
                            FilledTonalButton(onClick = { onRewind(30L) }, shape = RoundedCornerShape(50), contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)) {
                                Icon(Icons.Rounded.FastForward, null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("+30 שנ׳", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Button(onClick = onReturnLive, shape = RoundedCornerShape(50), contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)) {
                                Text("🔴 חזור ל-LIVE", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            // 1. Current Live Program
            Text("🔴 משודר כעת", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = scheme.primary)
            Spacer(Modifier.height(6.dp))
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(scheme.primaryContainer.copy(alpha = 0.42f))
                    .padding(16.dp),
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("${epg.current.startLabel} – ${epg.current.endLabel}", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = scheme.primary)
                    if (epg.current.badge.isNotEmpty()) {
                        Text(
                            epg.current.badge,
                            Modifier.clip(RoundedCornerShape(6.dp)).background(scheme.primary.copy(alpha = 0.16f)).padding(horizontal = 8.dp, vertical = 2.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = scheme.primary,
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(epg.current.title, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, color = scheme.onSurface)
                Text(epg.current.desc, fontSize = 13.sp, color = scheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp))
                Spacer(Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = { epg.current.progress(nowMs) },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(50)),
                    color = scheme.primary,
                    trackColor = scheme.surfaceContainerHighest,
                )
            }

            // 2. Past Programs (Catch-Up)
            if (epg.past.isNotEmpty()) {
                Spacer(Modifier.height(18.dp))
                Text("⏪ שודר קודם (לחץ לצפייה ב-Catch-Up)", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = scheme.primary)
                Spacer(Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    epg.past.reversed().forEach { prog ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(scheme.surfaceContainerHigh)
                                .clickable { onPlayPastProgram(prog) }
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text("${prog.startLabel} – ${prog.endLabel}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = scheme.primary)
                                Text(prog.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = scheme.onSurface)
                                Text(prog.desc, fontSize = 12.sp, color = scheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                            Spacer(Modifier.width(10.dp))
                            Text(
                                "⏪ נגן",
                                Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(scheme.secondaryContainer)
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = scheme.onSecondaryContainer,
                            )
                        }
                    }
                }
            }

            // 3. Upcoming Programs
            if (epg.upcoming.isNotEmpty()) {
                Spacer(Modifier.height(18.dp))
                Text("📅 בהמשך היום", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = scheme.primary)
                Spacer(Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    epg.upcoming.forEach { prog ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(scheme.surfaceContainerHigh)
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                "${prog.startLabel} – ${prog.endLabel}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = scheme.onSurfaceVariant,
                                modifier = Modifier.width(92.dp),
                            )
                            Column(Modifier.weight(1f)) {
                                Text(prog.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = scheme.onSurface)
                                Text(prog.desc, fontSize = 12.sp, color = scheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Multi-protocol Cast & Share bottom sheet supporting:
 *  1. Native media casting to Google Cast / Chromecast / Google TV / Android TV
 *  2. Native LAN media casting to LG webOS / Samsung / Smart TVs via DLNA / UPnP AV
 *  3. Direct Cloud Cast to any Smart TV running the Israel TV+ app
 *  4. System-level wireless screen mirroring (Miracast / LG Screen Share / Smart View)
 *  5. External caster app handoff (Web Video Caster / VLC)
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

            // Active connection banner
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

            // Actionable error banner when a direct stream is rejected by the TV receiver
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

            // Section 1: Native Direct Media Casting (Google Cast / Chromecast / Android TV + LG/Samsung DLNA)
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

            // Section 2: Screen Mirroring (LG Screen Share / Miracast / Samsung Smart View)
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
                            "מתאים לטלוויזיות LG webOS (שאינן כוללות Chromecast מובנה) ולכל טלוויזיה תומכת Miracast. משקף את מסך הטלפון והשמע ישירות לטלוויזיה ותומך ב-100% מהערוצים.",
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

            // Section 3: External Caster App Handoff (Web Video Caster / VLC / LocalCast)
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
                    "שליחת קישור השידור החי לאפליקציות כגון Web Video Caster, LG ThinQ או VLC עבור דגמי טלוויזיה נוספים.",
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
    val state = rememberLazyListState(initialFirstVisibleItemIndex = (current - 2).coerceAtLeast(0))
    val nowMs = System.currentTimeMillis()
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = scheme.surfaceContainer) {
        LazyColumn(state = state, contentPadding = PaddingValues(bottom = 32.dp)) {
            items(list.size, key = { list[it].id }) { i ->
                val ch = list[i]
                val curProg = remember(ch.id) { EpgRepository.getCurrentProgram(ch, nowMs) }
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
                        Text("${curProg.startLabel} · ${curProg.title}", color = scheme.onSurfaceVariant, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
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
    onOpenEpg: () -> Unit,
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
                    Text(if (prefs.isFavorite(ch.id)) "★ הסרה מהמועדפים" else "☆ הוספה למועדפים", fontWeight = FontWeight.Bold)
                }
                FilledTonalButton(onClick = onOpenMultiView, shape = RoundedCornerShape(50)) {
                    Icon(Icons.Rounded.GridView, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("פיצול מסך (Multi-View)", fontWeight = FontWeight.Bold)
                }
                FilledTonalButton(onClick = onOpenEpg, shape = RoundedCornerShape(50)) {
                    Icon(Icons.Rounded.Schedule, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("לוח שידורים ו-Catch-Up", fontWeight = FontWeight.Bold)
                }
                FilledTonalButton(onClick = onOpenCast, shape = RoundedCornerShape(50)) {
                    Icon(Icons.Rounded.Cast, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("שידור לטלוויזיה", fontWeight = FontWeight.Bold)
                }
            }
            if (serverCount > 1) {
                Spacer(Modifier.height(18.dp))
                Text(
                    "🔀 שרתי גיבוי (${serverIndex + 1}/$serverCount) — מעבר אוטומטי או ידני במקרה תקלה",
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
 * Split-TV / Multi-View live screen (2 or 4 simultaneous channels) with per-cell audio selection,
 * per-cell channel switcher, per-cell backup server failover/switcher, and fullscreen expand.
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

    // Default initial 4 channels: current channel + top Israeli / Sports channels
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
                    Icon(Icons.Rounded.GridView, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text("פיצול מסך · Split-TV", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                        Text("הקש על מסך להפעלת השמע שלו", color = Color.White.copy(alpha = 0.68f), fontSize = 11.sp)
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    listOf(2 to "2 מסכים", 4 to "4 מסכים").forEach { (cnt, label) ->
                        val selected = cellCount == cnt
                        Box(
                            Modifier
                                .clip(RoundedCornerShape(50))
                                .background(if (selected) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.12f))
                                .clickable {
                                    cellCount = cnt
                                    if (activeAudioCell >= cnt) activeAudioCell = 0
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                        ) {
                            Text(
                                label,
                                color = if (selected) MaterialTheme.colorScheme.onPrimary else Color.White,
                                fontSize = 12.sp,
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
            if (cellCount == 2) {
                Column(Modifier.weight(1f).fillMaxWidth().padding(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    for (slot in 0 until 2) {
                        MultiViewCell(
                            channel = cellChannels[slot],
                            isAudioActive = activeAudioCell == slot,
                            onSelectAudio = { activeAudioCell = slot },
                            onPickChannel = { pickingCellIndex = slot },
                            onExpand = { onExpandChannel(cellChannels[slot]) },
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                        )
                    }
                }
            } else {
                Column(Modifier.weight(1f).fillMaxWidth().padding(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    for (row in 0 until 2) {
                        Row(Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            for (col in 0 until 2) {
                                val slot = row * 2 + col
                                MultiViewCell(
                                    channel = cellChannels[slot],
                                    isAudioActive = activeAudioCell == slot,
                                    onSelectAudio = { activeAudioCell = slot },
                                    onPickChannel = { pickingCellIndex = slot },
                                    onExpand = { onExpandChannel(cellChannels[slot]) },
                                    modifier = Modifier.weight(1f).fillMaxHeight(),
                                )
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
    channel: Channel,
    isAudioActive: Boolean,
    onSelectAudio: () -> Unit,
    onPickChannel: () -> Unit,
    onExpand: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val candidates = remember(channel.id, channel.url, channel.fallbackUrls, channel.backups) { channel.allCandidateUrls() }
    var srvIdx by remember(channel.id) { mutableIntStateOf(0) }
    val cellPlayer = remember { buildPlayer(context, handleAudioFocus = false) }

    DisposableEffect(cellPlayer) {
        onDispose { cellPlayer.release() }
    }

    LaunchedEffect(isAudioActive) {
        cellPlayer.volume = if (isAudioActive) 1.0f else 0f
    }

    DisposableEffect(cellPlayer, candidates) {
        val listener = object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                if (srvIdx + 1 < candidates.size) {
                    srvIdx += 1
                }
            }
        }
        cellPlayer.addListener(listener)
        onDispose { cellPlayer.removeListener(listener) }
    }

    LaunchedEffect(channel.id, srvIdx) {
        val raw = candidates.getOrElse(srvIdx) { channel.url }
        cellPlayer.stop()
        cellPlayer.clearMediaItems()
        val resolved = runCatching { playableUrl(raw, channel.auth) }.getOrDefault(raw)
        val item = MediaItem.Builder().setUri(resolved).setMimeType(MimeTypes.APPLICATION_M3U8).build()
        cellPlayer.setMediaItem(item)
        cellPlayer.volume = if (isAudioActive) 1.0f else 0f
        cellPlayer.prepare()
        cellPlayer.playWhenReady = true
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

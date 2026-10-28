package com.israeltv.plus.ui.screens

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import coil3.compose.AsyncImage
import com.israeltv.plus.R
import com.israeltv.plus.data.AppPrefs
import com.israeltv.plus.data.Channel
import com.israeltv.plus.data.ChannelData
import com.israeltv.plus.data.USER_AGENT
import com.israeltv.plus.data.playableUrl
import com.israeltv.plus.data.shortCategory
import com.israeltv.plus.ui.components.BoldIcon
import com.israeltv.plus.ui.components.ChannelShelf
import com.israeltv.plus.ui.components.ContinueWatchingShelf
import com.israeltv.plus.ui.components.GlassIconButton
import com.israeltv.plus.ui.components.LocalPlayOrigin
import com.israeltv.plus.ui.components.ShelfSkeleton
import com.israeltv.plus.ui.components.cascadeIn
import com.israeltv.plus.ui.components.feedPadding
import com.israeltv.plus.ui.components.frostedGlass
import com.israeltv.plus.ui.components.glassShimmer
import com.israeltv.plus.ui.components.pressPhysics
import com.israeltv.plus.ui.components.rememberBitChordFlingBehavior
import com.israeltv.plus.ui.components.sharedContainer
import com.israeltv.plus.ui.theme.LocalDarkTheme
import kotlinx.coroutines.delay
import kotlin.math.abs

private val HeroShape = RoundedCornerShape(0.dp)

/**
 * Home: one master LazyColumn - the full-bleed hero with parallax & graceful fallback, the "רק ישראלי" switch
 * directly below the hero, the 16:9 "המשך צפייה" row with bottom progress bars, favourites, then a carousel per category.
 * Scroll physics (fling, rubber-band overscroll) are completely untouched.
 */
@Composable
fun HomeScreen(
    data: ChannelData?,
    prefs: AppPrefs,
    listState: LazyListState,
    israeliOnly: Boolean,
    onIsraeliOnlyChange: (Boolean) -> Unit,
    onSwitchPositioned: (Offset) -> Unit,
    macroCategory: String?,
    logo: (Channel) -> String,
    subtitle: (Channel) -> String,
    onPlay: (Channel, List<Channel>) -> Unit,
    onInfo: (Channel) -> Unit,
    onSeeAll: (String) -> Unit,
) {
    val padding = feedPadding(withCategories = true)
    val categories = data?.categories.orEmpty()
    val israeliCategories = remember(categories) {
        categories.filter { category -> category.channels.any { it.categoryEn == "israel" } }
    }
    val pool = if (israeliOnly) israeliCategories else categories
    val visibleCategories = if (macroCategory == null) pool else pool.filter { it.name == macroCategory }

    // Parallax offset derived from the LazyColumn's first item scroll position
    val heroScrollPx by remember(listState) {
        derivedStateOf {
            if (listState.firstVisibleItemIndex == 0) listState.firstVisibleItemScrollOffset.toFloat() else 900f
        }
    }

    LazyColumn(
        Modifier.fillMaxSize(),
        state = listState,
        contentPadding = padding,
        flingBehavior = rememberBitChordFlingBehavior(),
    ) {
        if (data == null) {
            item { Box(Modifier.bleedToTop(padding).padding(bottom = 28.dp).fillMaxWidth().height(580.dp).glassShimmer(HeroShape)) }
            items(4) { ShelfSkeleton() }
            return@LazyColumn
        }
        item(key = "hero") {
            HeroPager(
                featured = data.featured,
                prefs = prefs,
                logo = logo,
                scrollOffsetPx = heroScrollPx,
                topInset = padding.calculateTopPadding(),
                onPlay = { onPlay(it, data.featured) },
                onInfo = onInfo,
                modifier = Modifier.bleedToTop(padding).cascadeIn(0),
            )
        }
        item(key = "israeli-only-filter") {
            IsraeliOnlySwitch(
                checked = israeliOnly,
                modifier = Modifier.padding(top = 12.dp, bottom = 16.dp).cascadeIn(1),
                onPositioned = onSwitchPositioned,
                onCheckedChange = onIsraeliOnlyChange,
            )
        }
        var row = 2
        val recent = prefs.recents.mapNotNull { data.byId[it] }
        if (recent.isNotEmpty()) {
            val i = row++
            item(key = "recent") {
                ContinueWatchingShelf(
                    title = "המשך צפייה",
                    channels = recent,
                    logo = logo,
                    subtitle = subtitle,
                    onPlay = { onPlay(it, recent) },
                    modifier = Modifier.cascadeIn(i),
                )
            }
        }
        val favs = prefs.favorites.mapNotNull { data.byId[it] }
        if (favs.isNotEmpty()) {
            val i = row++
            item(key = "favs") {
                ChannelShelf(
                    title = "הרשימה שלי",
                    channels = favs,
                    logo = logo,
                    subtitle = subtitle,
                    onPlay = { onPlay(it, favs) },
                    modifier = Modifier.cascadeIn(i),
                )
            }
        }
        if (visibleCategories.isEmpty()) {
            item(key = "empty") {
                Text(
                    if (israeliOnly) "אין קטגוריה כזו בתוכן הישראלי" else "אין קטגוריה כזו",
                    Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 28.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Light,
                )
            }
        }
        visibleCategories.forEach { cat ->
            val i = row++
            item(key = "cat:" + cat.key) {
                ChannelShelf(
                    cat.name, cat.channels.take(24), logo, subtitle, { onPlay(it, cat.channels) },
                    count = cat.channels.size, onSeeAll = { onSeeAll(cat.key) }, modifier = Modifier.cascadeIn(i),
                )
            }
        }
    }
}

/**
 * The hero starts at the very top of the screen, behind the status bar and the glass header: it is laid out
 * [padding]'s top inset higher than the LazyColumn puts it (the list itself keeps its contentPadding).
 */
private fun Modifier.bleedToTop(padding: PaddingValues): Modifier = layout { measurable, constraints ->
    val top = padding.calculateTopPadding().roundToPx()
    val placeable = measurable.measure(constraints)
    layout(placeable.width, (placeable.height - top).coerceAtLeast(0)) { placeable.place(0, -top) }
}

/**
 * "רק ישראלי" native out-of-the-box Material You (Material 3) Switch component.
 * Uses the standard native M3 Switch behavior without altering or overriding its internal animations or physics.
 */
@Composable
internal fun IsraeliOnlySwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onPositioned: (Offset) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scheme = MaterialTheme.colorScheme
    Row(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            "רק ישראלי",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = scheme.onSurface,
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.onGloballyPositioned { onPositioned(it.boundsInRoot().center) },
        )
    }
}

@Composable
private fun HeroPager(
    featured: List<Channel>,
    prefs: AppPrefs,
    logo: (Channel) -> String,
    scrollOffsetPx: Float,
    topInset: androidx.compose.ui.unit.Dp,
    onPlay: (Channel) -> Unit,
    onInfo: (Channel) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (featured.isEmpty()) return
    val pager = rememberPagerState { featured.size }
    LaunchedEffect(pager.settledPage, pager.isScrollInProgress) {
        if (pager.isScrollInProgress) return@LaunchedEffect
        delay(8000)
        pager.animateScrollToPage((pager.currentPage + 1) % featured.size)
    }
    Box(modifier.padding(bottom = 14.dp)) {
        HorizontalPager(pager, key = { featured[it].id }) { page ->
            val ch = featured[page]
            HeroPage(
                ch = ch,
                logo = logo(ch),
                favorite = prefs.isFavorite(ch.id),
                playBackground = page == pager.settledPage,
                scrollOffsetPx = scrollOffsetPx,
                topInset = topInset,
                onPlay = onPlay,
                onInfo = onInfo,
                onFavorite = { prefs.toggleFavorite(ch.id) },
            )
        }
        // Capsule page indicator: the active dot stretches to 24 dp
        Row(
            Modifier.align(Alignment.BottomCenter).padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            repeat(featured.size) { i ->
                val on = i == pager.currentPage
                val w by animateDpAsState(if (on) 24.dp else 6.dp, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMediumLow), label = "dot")
                Box(
                    Modifier
                        .padding(horizontal = 3.dp)
                        .height(6.dp)
                        .width(w)
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = if (on) 1f else 0.3f))
                )
            }
        }
    }
}

/**
 * Lifecycle-scoped, TextureView-backed muted Media3 HLS background player with RESIZE_MODE_ZOOM.
 * Resolves [playableUrl] off the UI thread and reports [onFirstFrame] and [onError] so the Hero can gracefully
 * display a blurred static poster while buffering or if the stream fails.
 */
@Composable
private fun MutedHeroHls(
    channel: Channel,
    onFirstFrame: () -> Unit,
    onError: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val player = remember(context) {
        val http = DefaultHttpDataSource.Factory()
            .setUserAgent(USER_AGENT)
            .setAllowCrossProtocolRedirects(true)
            .setConnectTimeoutMs(10_000)
            .setReadTimeoutMs(12_000)
        ExoPlayer.Builder(context)
            .setMediaSourceFactory(DefaultMediaSourceFactory(http))
            .build()
            .apply {
                repeatMode = Player.REPEAT_MODE_ONE
                volume = 0f
            }
    }

    val candidates = remember(channel.id, channel.url, channel.fallbackUrls, channel.backups) { channel.allCandidateUrls() }
    var sourceIndex by remember(channel.id) { mutableIntStateOf(0) }

    DisposableEffect(player, lifecycle, candidates) {
        val listener = object : Player.Listener {
            override fun onRenderedFirstFrame() {
                onFirstFrame()
            }
            override fun onPlayerError(error: PlaybackException) {
                if (sourceIndex + 1 < candidates.size) {
                    sourceIndex += 1
                } else {
                    onError()
                }
            }
        }
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> player.pause()
                Lifecycle.Event.ON_START -> {
                    player.volume = 0f
                    player.play()
                }
                else -> Unit
            }
        }
        player.addListener(listener)
        lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycle.removeObserver(lifecycleObserver)
            player.removeListener(listener)
        }
    }

    DisposableEffect(player) {
        onDispose {
            player.release()
        }
    }

    LaunchedEffect(channel.id, sourceIndex, player) {
        val rawCandidate = candidates.getOrElse(sourceIndex) { channel.url }
        val resolvedUrl = runCatching { playableUrl(rawCandidate, channel.auth) }.getOrNull()
        if (resolvedUrl.isNullOrBlank()) {
            if (sourceIndex + 1 < candidates.size) {
                sourceIndex += 1
            } else {
                onError()
            }
            return@LaunchedEffect
        }
        val item = MediaItem.Builder()
            .setUri(resolvedUrl)
            .setMimeType(MimeTypes.APPLICATION_M3U8)
            .build()
        player.setMediaItem(item)
        player.volume = 0f
        player.prepare()
        player.playWhenReady = true
    }

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { ctx ->
            (LayoutInflater.from(ctx).inflate(R.layout.hero_player_view, null, false) as PlayerView).apply {
                useController = false
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                setShutterBackgroundColor(android.graphics.Color.TRANSPARENT)
                this.player = player
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                )
            }
        },
        update = { view ->
            view.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
            if (view.player !== player) view.player = player
        },
        onRelease = { view ->
            view.player = null
        },
    )
}

/**
 * Computes dominant saturated ambient glow colors for a channel poster/stream.
 */
private fun dominantHeroGlowColors(ch: Channel): Pair<Color, Color> {
    val id = ch.id.lowercase()
    return when {
        "kan" in id || "11" in id -> Color(0xFF0EA5E9) to Color(0xFF2563EB)
        "keshet" in id || "12" in id -> Color(0xFFF59E0B) to Color(0xFFE11D48)
        "reshet" in id || "13" in id -> Color(0xFF3B82F6) to Color(0xFF7C3AED)
        "14" in id || "now14" in id -> Color(0xFFE11D48) to Color(0xFF9F1239)
        "i24" in id || "news" in id -> Color(0xFF0284C7) to Color(0xFF1D4ED8)
        "sport" in id || ch.categoryEn == "sports" -> Color(0xFF10B981) to Color(0xFF0284C7)
        else -> {
            val palette = listOf(
                Color(0xFF0EA5E9) to Color(0xFF6366F1),
                Color(0xFFE11D48) to Color(0xFFF59E0B),
                Color(0xFF10B981) to Color(0xFF0EA5E9),
                Color(0xFF8B5CF6) to Color(0xFFEC4899),
            )
            palette[abs(ch.id.hashCode()) % palette.size]
        }
    }
}

/**
 * High-quality static hero fallback backdrop with a dynamic ambient color glow and slight blur.
 */
@Composable
private fun HeroStaticFallbackPoster(ch: Channel, logo: String, modifier: Modifier = Modifier) {
    val (glowA, glowB) = remember(ch.id) { dominantHeroGlowColors(ch) }
    Box(
        modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF070913),
                        glowA.copy(alpha = 0.42f),
                        glowB.copy(alpha = 0.28f),
                        Color(0xFF07080D),
                    )
                )
            )
    ) {
        if (logo.isNotEmpty()) {
            AsyncImage(
                model = logo,
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(0.85f)
                    .height(250.dp)
                    .blur(26.dp)
                    .graphicsLayer { alpha = 0.42f },
                contentScale = ContentScale.Fit,
            )
        }
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        listOf(
                            glowA.copy(alpha = 0.32f),
                            Color.Transparent,
                        )
                    )
                )
        )
    }
}

/**
 * The Kan Box / Apple TV+ hero:
 * - Heavily blurred dynamic Hero Ambient Glow matching dominant poster/channel colors behind the Hero preview video.
 * - Parallax scroll effect on the backdrop.
 * - Seamless TextureView-backed ExoPlayer (RESIZE_MODE_ZOOM) with graceful fallback to a blurred static poster.
 * - Ultra-thin bordered glass badges for FHD/Live (no solid chunky pills, no red dot emoji).
 * - Scaled-down interactive buttons with shape-morphing press physics.
 */
@Composable
private fun HeroPage(
    ch: Channel,
    logo: String,
    favorite: Boolean,
    playBackground: Boolean,
    scrollOffsetPx: Float,
    topInset: androidx.compose.ui.unit.Dp,
    onPlay: (Channel) -> Unit,
    onInfo: (Channel) -> Unit,
    onFavorite: () -> Unit,
) {
    val scheme = MaterialTheme.colorScheme
    val dark = LocalDarkTheme.current
    val haptic = LocalHapticFeedback.current
    val origin = LocalPlayOrigin.current
    val key = "hero:${ch.id}"
    val quality = ch.badge.ifBlank { if (ch.isRadio) "רדיו" else "FHD" }
    val where = if (ch.categoryEn == "israel") "ערוצי ישראל" else shortCategory(ch.category)
    val (glowPrimary, glowSecondary) = remember(ch.id) { dominantHeroGlowColors(ch) }

    var videoReady by remember(ch.id, playBackground) { mutableStateOf(false) }
    var videoFailed by remember(ch.id, playBackground) { mutableStateOf(false) }
    val videoAlpha by animateFloatAsState(
        targetValue = if (videoReady && !videoFailed) 1f else 0f,
        animationSpec = tween(420),
        label = "heroVideoAlpha",
    )

    Box(
        Modifier
            .fillMaxWidth()
            .height(588.dp)
            .sharedContainer(key, HeroShape)
            .clipToBounds()
            .background(Color.Black)
    ) {
        // Heavily blurred dynamic color Ambient Glow behind the Hero preview video (preserved untouched)
        Box(
            Modifier
                .fillMaxSize()
                .blur(76.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            glowPrimary.copy(alpha = 0.56f),
                            glowSecondary.copy(alpha = 0.34f),
                            Color.Transparent,
                        ),
                        radius = 950f,
                    )
                )
        )

        // Parallax backdrop container (both fallback poster and live video move with parallax)
        Box(
            Modifier
                .fillMaxSize()
                .graphicsLayer {
                    translationY = scrollOffsetPx * 0.42f
                    val s = 1f + (scrollOffsetPx / 2400f).coerceIn(0f, 0.10f)
                    scaleX = s
                    scaleY = s
                }
        ) {
            // Graceful degradation: static blurred hero poster shown while buffering or if video fails
            HeroStaticFallbackPoster(ch = ch, logo = logo)

            if (playBackground && !videoFailed && !ch.isRadio && (ch.url.contains(".m3u8", ignoreCase = true) || ch.auth == "mako")) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .graphicsLayer { alpha = videoAlpha }
                ) {
                    MutedHeroHls(
                        channel = ch,
                        onFirstFrame = { videoReady = true },
                        onError = { videoFailed = true },
                        modifier = Modifier.fillMaxSize(),
                    )
                    // Seamless Transition: gentle blur + ambient color glow + soft bottom edge fade
                    Box(
                        Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .height(144.dp)
                            .blur(18.dp)
                            .background(
                                Brush.verticalGradient(
                                    0f to Color.Transparent,
                                    0.45f to glowPrimary.copy(alpha = 0.22f),
                                    0.82f to scheme.surfaceContainerLowest.copy(alpha = 0.72f),
                                    1f to scheme.surfaceContainerLowest.copy(alpha = 0.94f),
                                )
                            )
                    )
                }
            }
        }

        // Ambient glow halo + bottom gradient blending the hero edge into the feed background
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    0f to Color.Transparent,
                    0.54f to Color.Transparent,
                    0.76f to glowPrimary.copy(alpha = 0.18f),
                    0.91f to scheme.surfaceContainerLowest.copy(alpha = 0.80f),
                    1f to scheme.surfaceContainerLowest,
                )
            )
        )

        Column(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(top = topInset + 36.dp, start = 20.dp, end = 20.dp, bottom = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Box(
                    Modifier
                        .size(width = 200.dp, height = 96.dp)
                        .blur(32.dp)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    glowPrimary.copy(alpha = 0.38f),
                                    Color.White.copy(alpha = if (dark) 0.22f else 0.45f),
                                    Color.Transparent,
                                )
                            )
                        )
                )
                if (logo.isNotEmpty()) {
                    AsyncImage(
                        model = logo,
                        contentDescription = ch.name,
                        modifier = Modifier
                            .fillMaxWidth(0.46f)
                            .heightIn(max = 80.dp),
                        contentScale = ContentScale.Fit,
                    )
                } else {
                    Text(ch.name, style = MaterialTheme.typography.headlineMedium, color = scheme.onSurface, maxLines = 2)
                }
            }
            Spacer(Modifier.height(14.dp))
            HeroMetadata(quality, where)
            if (logo.isEmpty()) {
                Spacer(Modifier.height(4.dp))
                Text(ch.name, style = MaterialTheme.typography.titleLarge, color = scheme.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Spacer(Modifier.height(18.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                GlassIconButton(
                    if (favorite) Icons.Rounded.Star else Icons.Rounded.StarBorder, "שמור", onFavorite, size = 43.dp,
                    tint = scheme.onSurface.copy(alpha = 0.14f), iconTint = if (favorite) Color(0xFFFFD54F) else scheme.onSurface,
                )
                val playInteraction = remember { MutableInteractionSource() }
                val playPressed by playInteraction.collectIsPressedAsState()
                val playCorner by animateDpAsState(
                    targetValue = if (playPressed) 12.dp else 22.dp,
                    animationSpec = spring(dampingRatio = 0.56f, stiffness = Spring.StiffnessMedium),
                    label = "heroPlayMorph",
                )
                val playShape = RoundedCornerShape(playCorner)
                Button(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                        origin.key = key
                        onPlay(ch)
                    },
                    interactionSource = playInteraction,
                    shape = playShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (dark) Color.White else scheme.onSurface,
                        contentColor = if (dark) Color.Black else scheme.surface,
                    ),
                    contentPadding = PaddingValues(horizontal = 28.dp),
                    modifier = Modifier
                        .height(43.dp)
                        .pressPhysics(playInteraction, tilt = false, glowShape = playShape),
                ) {
                    Icon(Icons.Rounded.PlayArrow, null, Modifier.size(21.dp))
                    Spacer(Modifier.width(5.dp))
                    Text("צפייה", fontSize = 14.5.sp, fontWeight = FontWeight.SemiBold)
                }
                GlassIconButton(
                    Icons.Rounded.Info, "מידע", { onInfo(ch) }, size = 43.dp,
                    tint = scheme.onSurface.copy(alpha = 0.14f), iconTint = scheme.onSurface,
                )
            }
        }
    }
}

/**
 * Ultra-thin bordered glass badges for FHD/Live and category.
 * Strictly no solid chunky pills and no red dot emoji.
 */
@Composable
private fun HeroMetadata(quality: String, where: String) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(7.dp, Alignment.CenterHorizontally),
        ) {
            HeroGlassBadge(quality)
            HeroGlassBadge(where)
            HeroGlassBadge("Live", highlight = true)
        }
    }
}

@Composable
private fun HeroGlassBadge(text: String, highlight: Boolean = false) {
    val badgeShape = RoundedCornerShape(6.dp)
    Box(
        Modifier
            .clip(badgeShape)
            .background(Color.White.copy(alpha = if (highlight) 0.10f else 0.06f))
            .border(
                width = 0.75.dp,
                color = Color.White.copy(alpha = if (highlight) 0.42f else 0.26f),
                shape = badgeShape,
            )
            .padding(horizontal = 8.dp, vertical = 2.5.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (highlight) 0.98f else 0.86f),
            fontSize = 11.sp,
            fontWeight = if (highlight) FontWeight.SemiBold else FontWeight.Medium,
            letterSpacing = 0.2.sp,
            maxLines = 1,
            style = TextStyle(textDirection = TextDirection.Ltr),
        )
    }
}

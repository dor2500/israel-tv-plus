package com.israeltv.plus.ui.screens

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import com.israeltv.plus.ui.components.rememberBitChordFlingBehavior
import com.israeltv.plus.ui.components.sharedContainer
import com.israeltv.plus.ui.theme.LocalDarkTheme
import kotlinx.coroutines.delay

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
                onPlay = { onPlay(it, data.featured) },
                onInfo = onInfo,
                modifier = Modifier.bleedToTop(padding).cascadeIn(0),
            )
        }
        item(key = "israeli-only-filter") {
            IsraeliOnlySwitch(
                checked = israeliOnly,
                modifier = Modifier.padding(top = 4.dp, bottom = 14.dp).cascadeIn(1),
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
                    title = "המועדפים שלי",
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
 * "רק ישראלי" switch: a compact centred label and the Material 3 switch. Its measured centre is kept
 * so the Zomato overlay can fly its badge back into the switch when the filter is turned off.
 */
@Composable
internal fun IsraeliOnlySwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onPositioned: (Offset) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scheme = MaterialTheme.colorScheme
    val haptic = LocalHapticFeedback.current
    Row(
        modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("רק ישראלי", color = scheme.onSurface.copy(alpha = 0.92f), fontSize = 15.sp, fontWeight = FontWeight.Medium)
        Spacer(Modifier.width(10.dp))
        Switch(
            checked = checked,
            onCheckedChange = {
                haptic.performHapticFeedback(if (it) HapticFeedbackType.ToggleOn else HapticFeedbackType.ToggleOff)
                onCheckedChange(it)
            },
            thumbContent = if (checked) {
                { BoldIcon(Icons.Rounded.Check, null, Modifier.size(14.dp), tint = scheme.onPrimary, weight = 0.5.dp) }
            } else null,
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
    Box(modifier.padding(bottom = 10.dp)) {
        HorizontalPager(pager, key = { featured[it].id }) { page ->
            val ch = featured[page]
            HeroPage(
                ch = ch,
                logo = logo(ch),
                favorite = prefs.isFavorite(ch.id),
                playBackground = page == pager.settledPage,
                scrollOffsetPx = scrollOffsetPx,
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
 * High-quality static hero fallback backdrop with a slight blur (graceful degradation while buffering or if video fails).
 */
@Composable
private fun HeroStaticFallbackPoster(logo: String, modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    Box(
        modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF0B132B),
                        scheme.primaryContainer.copy(alpha = 0.55f),
                        Color(0xFF090A10),
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
                    .height(260.dp)
                    .blur(20.dp)
                    .graphicsLayer { alpha = 0.38f },
                contentScale = ContentScale.Fit,
            )
        }
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        listOf(
                            scheme.primary.copy(alpha = 0.24f),
                            Color.Transparent,
                        )
                    )
                )
        )
    }
}

/**
 * The Kan Box / Apple TV+ hero:
 * - Parallax scroll effect on the backdrop.
 * - Seamless TextureView-backed ExoPlayer (RESIZE_MODE_ZOOM) with graceful fallback to a blurred static poster.
 * - Soft vertical gradient blending into the dark background.
 * - Clean metadata line ("HD • ערוצי ישראל • Live") centered below the channel logo with no colored pill backgrounds and no red dot.
 */
@Composable
private fun HeroPage(
    ch: Channel,
    logo: String,
    favorite: Boolean,
    playBackground: Boolean,
    scrollOffsetPx: Float,
    onPlay: (Channel) -> Unit,
    onInfo: (Channel) -> Unit,
    onFavorite: () -> Unit,
) {
    val scheme = MaterialTheme.colorScheme
    val dark = LocalDarkTheme.current
    val haptic = LocalHapticFeedback.current
    val origin = LocalPlayOrigin.current
    val key = "hero:${ch.id}"
    val quality = ch.badge.ifBlank { if (ch.isRadio) "רדיו" else "HD" }
    val where = if (ch.categoryEn == "israel") "ערוצי ישראל" else shortCategory(ch.category)

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
            .height(580.dp)
            .sharedContainer(key, HeroShape)
            .clipToBounds()
            .background(Color.Black)
    ) {
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
            HeroStaticFallbackPoster(logo = logo)

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
                    // Seamless Transition: gentle blur + soft bottom edge fade so the Hero video stays bright and clear
                    Box(
                        Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .height(136.dp)
                            .blur(14.dp)
                            .background(
                                Brush.verticalGradient(
                                    0f to Color.Transparent,
                                    0.48f to Color.Black.copy(alpha = 0.16f),
                                    0.82f to scheme.surfaceContainerLowest.copy(alpha = 0.68f),
                                    1f to scheme.surfaceContainerLowest.copy(alpha = 0.92f),
                                )
                            )
                    )
                }
            }
        }

        // Light bottom-only gradient blending the hero edge into the feed background without darkening the video
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    0f to Color.Transparent,
                    0.56f to Color.Transparent,
                    0.78f to Color.Black.copy(alpha = 0.24f),
                    0.92f to scheme.surfaceContainerLowest.copy(alpha = 0.76f),
                    1f to scheme.surfaceContainerLowest,
                )
            )
        )

        Column(
            Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(horizontal = 24.dp).padding(bottom = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Box(
                    Modifier
                        .size(width = 300.dp, height = 190.dp)
                        .background(Brush.radialGradient(listOf(Color.White.copy(alpha = if (dark) 0.30f else 0.55f), Color.Transparent)))
                )
                if (logo.isNotEmpty()) {
                    AsyncImage(logo, ch.name, Modifier.fillMaxWidth(0.62f).height(140.dp), contentScale = ContentScale.Fit)
                } else {
                    Text(ch.name, style = MaterialTheme.typography.displaySmall, color = scheme.onSurface, maxLines = 2)
                }
            }
            Spacer(Modifier.height(16.dp))
            HeroMetadata(quality, where)
            if (logo.isEmpty()) {
                Spacer(Modifier.height(4.dp))
                Text(ch.name, style = MaterialTheme.typography.headlineSmall, color = scheme.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                GlassIconButton(
                    if (favorite) Icons.Rounded.Star else Icons.Rounded.StarBorder, "מועדף", onFavorite, size = 50.dp,
                    tint = scheme.onSurface.copy(alpha = 0.14f), iconTint = scheme.onSurface,
                )
                Button(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                        origin.key = key
                        onPlay(ch)
                    },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (dark) Color.White else scheme.onSurface,
                        contentColor = if (dark) Color.Black else scheme.surface,
                    ),
                    contentPadding = PaddingValues(horizontal = 34.dp),
                    modifier = Modifier.height(50.dp),
                ) {
                    Icon(Icons.Rounded.PlayArrow, null, Modifier.size(26.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("צפייה", fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                }
                GlassIconButton(
                    Icons.Rounded.Info, "מידע", { onInfo(ch) }, size = 50.dp,
                    tint = scheme.onSurface.copy(alpha = 0.14f), iconTint = scheme.onSurface,
                )
            }
        }
    }
}

/**
 * Clean minimalist Hero Metadata: "HD • ערוצי ישראל • Live" centered below the channel logo.
 * No colored pill backgrounds around text, and no red circle/dot emoji next to "Live".
 */
@Composable
private fun HeroMetadata(quality: String, where: String) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            HeroMetaText(quality)
            HeroMetaSeparator()
            HeroMetaText(where)
            HeroMetaSeparator()
            HeroMetaText("Live", dim = false)
        }
    }
}

@Composable
private fun HeroMetaText(text: String, dim: Boolean = true) {
    Text(
        text,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (dim) 0.76f else 0.98f),
        fontSize = 14.sp,
        fontWeight = if (dim) FontWeight.Normal else FontWeight.Medium,
        letterSpacing = 0.2.sp,
        maxLines = 1,
        style = TextStyle(textDirection = TextDirection.Ltr),
    )
}

@Composable
private fun HeroMetaSeparator() {
    Box(
        Modifier.padding(horizontal = 9.dp).size(3.dp).clip(CircleShape)
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f))
    )
}

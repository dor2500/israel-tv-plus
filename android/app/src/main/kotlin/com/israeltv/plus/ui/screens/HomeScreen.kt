package com.israeltv.plus.ui.screens

import android.os.Build
import androidx.compose.foundation.background
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
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.getValue
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.style.TextOverflow
import com.israeltv.plus.ui.components.LocalPlayOrigin
import com.israeltv.plus.ui.components.cascadeIn
import com.israeltv.plus.ui.components.sharedContainer
import com.israeltv.plus.ui.theme.LocalDarkTheme
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.israeltv.plus.data.AppPrefs
import com.israeltv.plus.data.Channel
import com.israeltv.plus.data.ChannelData
import com.israeltv.plus.data.shortCategory
import com.israeltv.plus.ui.components.ChannelShelf
import com.israeltv.plus.ui.components.GlassIconButton
import com.israeltv.plus.ui.components.ShelfSkeleton
import com.israeltv.plus.ui.components.feedPadding
import com.israeltv.plus.ui.components.glassShimmer
import com.israeltv.plus.ui.components.rememberBitChordFlingBehavior
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import com.israeltv.plus.data.playableUrl
import com.israeltv.plus.ui.components.tactileBounce
import com.israeltv.plus.data.isIsraeli
import com.israeltv.plus.ui.player.playerHttpClient
import kotlinx.coroutines.delay

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.Dp
import com.israeltv.plus.ui.components.AppleIsraelFlag

private val HeroShape = RoundedCornerShape(0.dp)

/**
 * Home: one master LazyColumn with:
 * 1. Hero restricted to ~52% screen height, allowing content peeking from the bottom.
 * 2. Inline "רק ישראלי" animated Switch filter.
 * 3. Kan Box category pills directly below Hero:
 *    - Inactive: transparent with 1.dp thin white border
 *    - Active: Solid white background with dark text.
 * 4. "Continue Watching" (המשך צפייה) 16:9 shelf clearly peeking up from the bottom of the screen.
 */
@Composable
fun HomeScreen(
    data: ChannelData?,
    prefs: AppPrefs,
    listState: LazyListState,
    logo: (Channel) -> String,
    subtitle: (Channel) -> String,
    onPlay: (Channel, List<Channel>) -> Unit,
    onInfo: (Channel) -> Unit,
    israeliOnly: Boolean = false,
    onToggleIsraeli: () -> Unit = {},
    onSwitchPositioned: (Offset) -> Unit = {},
    onSeeAll: (String) -> Unit,
) {
    val padding = feedPadding()
    val scheme = MaterialTheme.colorScheme
    var selectedCategory by remember { mutableStateOf<String?>("all") }

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val heroHeight = (maxHeight * 0.52f).coerceIn(360.dp, 430.dp)

        LazyColumn(
            Modifier.fillMaxSize(),
            state = listState,
            contentPadding = padding,
            flingBehavior = rememberBitChordFlingBehavior(),
        ) {
            if (data == null) {
                item { Box(Modifier.bleedToTop(padding).padding(bottom = 28.dp).fillMaxWidth().height(heroHeight).glassShimmer(HeroShape)) }
                item { ShelfSkeleton(isWide = true) }
                items(3) { ShelfSkeleton(isWide = false) }
                return@LazyColumn
            }

            val allChannels = if (israeliOnly) data.channels.filter { it.isIsraeli() } else data.channels
            val featured = if (israeliOnly) data.featured.filter { it.isIsraeli() }.ifEmpty { allChannels.take(5) } else data.featured

            // 1. HERO PAGER (Restricted height allowing content peeking)
            item(key = "hero") {
                HeroPager(
                    featured = featured,
                    prefs = prefs,
                    logo = logo,
                    heroHeight = heroHeight,
                    onPlay = { onPlay(it, featured) },
                    onInfo = onInfo,
                    modifier = Modifier.bleedToTop(padding).cascadeIn(0),
                )
            }

            // 2. INLINE "רק ישראלי" ANIMATED SWITCH (Relocated out of top bar, filters content)
            item(key = "israeli_filter_toggle") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 2.dp, bottom = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        AppleIsraelFlag(Modifier.size(width = 22.dp, height = 15.dp))
                        Text(
                            text = "רק תוכן ישראלי",
                            color = if (israeliOnly) scheme.primary else scheme.onSurface,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                        )
                    }

                    // Real animated Switch component with spatial coordinates measuring
                    Switch(
                        checked = israeliOnly,
                        onCheckedChange = { onToggleIsraeli() },
                        modifier = Modifier.onGloballyPositioned { coordinates ->
                            val pos = coordinates.positionInRoot()
                            val size = coordinates.size
                            onSwitchPositioned(Offset(pos.x + size.width / 2f, pos.y + size.height / 2f))
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = scheme.primary,
                            uncheckedThumbColor = Color.LightGray,
                            uncheckedTrackColor = Color(0xFF1E202A),
                            checkedBorderColor = Color.Transparent,
                            uncheckedBorderColor = Color.White.copy(alpha = 0.25f),
                        ),
                    )
                }
            }

            // 3. KAN BOX CATEGORY PILLS (Directly below Hero section)
            item(key = "kan_box_categories") {
                val haptic = LocalHapticFeedback.current
                val categoriesList = remember(data) {
                    listOf("all" to "הכל") + data.categories.map { it.key to it.name }
                }

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 18.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    flingBehavior = rememberBitChordFlingBehavior(),
                ) {
                    items(categoriesList, key = { it.first }) { (key, label) ->
                        val isActive = (selectedCategory == key)
                        Box(
                            modifier = Modifier
                                .height(38.dp)
                                .clip(RoundedCornerShape(50))
                                .then(
                                    if (isActive) {
                                        Modifier.background(Color.White)
                                    } else {
                                        Modifier
                                            .border(1.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(50))
                                            .background(Color.Transparent)
                                    }
                                )
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.SegmentTick)
                                    selectedCategory = key
                                }
                                .padding(horizontal = 18.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = label,
                                color = if (isActive) Color(0xFF0A0B10) else Color.White,
                                fontSize = 13.sp,
                                fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.Bold,
                            )
                        }
                    }
                }
            }

            // 4. SHELVES (Peeking up: Continue Watching, Favourites, Categories)
            var row = 1
            val rawRecent = prefs.recents.mapNotNull { data.byId[it] }
            val recent = if (israeliOnly) rawRecent.filter { it.isIsraeli() } else rawRecent
            if (recent.isNotEmpty()) {
                val i = row++
                // Wide 16:9 cards (width = 240.dp, height = 135.dp) for 'Continue Watching' row
                item(key = "recent") {
                    ChannelShelf(
                        title = "המשך צפייה",
                        channels = recent,
                        logo = logo,
                        subtitle = subtitle,
                        onPlay = { onPlay(it, recent) },
                        isWide = true,
                        modifier = Modifier.cascadeIn(i),
                    )
                }
            }
            val rawFavs = prefs.favorites.mapNotNull { data.byId[it] }
            val favs = if (israeliOnly) rawFavs.filter { it.isIsraeli() } else rawFavs
            if (favs.isNotEmpty()) {
                val i = row++
                // Tall 2:3 poster cards (width = 130.dp, height = 195.dp)
                item(key = "favs") {
                    ChannelShelf(
                        title = "המועדפים שלי",
                        channels = favs,
                        logo = logo,
                        subtitle = subtitle,
                        onPlay = { onPlay(it, favs) },
                        isWide = false,
                        modifier = Modifier.cascadeIn(i),
                    )
                }
            }
            val allRow = row++
            item(key = "all_channels_shelf") {
                val title = if (israeliOnly) "כל הערוצים (100% ישראלי)" else "כל הערוצים (הכל)"
                ChannelShelf(
                    title = title,
                    channels = allChannels.take(30),
                    logo = logo,
                    subtitle = subtitle,
                    onPlay = { onPlay(it, allChannels) },
                    count = allChannels.size,
                    isWide = false,
                    onSeeAll = { onSeeAll("all") },
                    modifier = Modifier.cascadeIn(allRow),
                )
            }
            val filteredCategories = if (selectedCategory == null || selectedCategory == "all") {
                data.categories
            } else {
                data.categories.filter { it.key == selectedCategory }
            }
            filteredCategories.forEach { cat ->
                val catChannels = if (israeliOnly) cat.channels.filter { it.isIsraeli() } else cat.channels
                if (catChannels.isNotEmpty()) {
                    val i = row++
                    item(key = "cat:" + cat.key) {
                        ChannelShelf(
                            title = cat.name,
                            channels = catChannels.take(24),
                            logo = logo,
                            subtitle = subtitle,
                            onPlay = { onPlay(it, catChannels) },
                            count = catChannels.size,
                            isWide = false,
                            onSeeAll = { onSeeAll(cat.key) },
                            modifier = Modifier.cascadeIn(i),
                        )
                    }
                }
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

@Composable
private fun HeroPager(
    featured: List<Channel>,
    prefs: AppPrefs,
    logo: (Channel) -> String,
    heroHeight: Dp,
    onPlay: (Channel) -> Unit,
    onInfo: (Channel) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (featured.isEmpty()) return
    val pager = rememberPagerState { featured.size }
    LaunchedEffect(pager.settledPage, pager.isScrollInProgress) {
        if (pager.isScrollInProgress) return@LaunchedEffect
        delay(7000)
        pager.animateScrollToPage((pager.currentPage + 1) % featured.size)
    }
    Box(modifier.fillMaxWidth().height(heroHeight).padding(bottom = 12.dp)) {
        HorizontalPager(pager, key = { featured[it].id }, modifier = Modifier.fillMaxSize()) { page ->
            val ch = featured[page]
            HeroPage(ch, logo(ch), prefs.isFavorite(ch.id), heroHeight, onPlay, onInfo) { prefs.toggleFavorite(ch.id) }
        }
        // capsule page indicator: the active dot stretches to 24 dp
        Row(
            Modifier.align(Alignment.BottomCenter).padding(bottom = 4.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            repeat(featured.size) { i ->
                val on = i == pager.currentPage
                val w by animateDpAsState(if (on) 24.dp else 6.dp, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMediumLow), label = "dot")
                Box(
                    Modifier
                        .padding(horizontal = 3.dp)
                        .height(5.dp)
                        .width(w)
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = if (on) 1f else 0.3f))
                )
            }
        }
    }
}

/**
 * Apple TV+ / Essentials hero, 580 dp, edge to edge:
 * Plays the muted live HLS stream (volume = 0f, 400ms debounce), fading into colorScheme.background.
 * CTA 'צפייה' button in colorScheme.primary, and badges (LIVE, FHD, channel numbers) in tertiaryContainer
 * with stretched variable typography.
 */
@Composable
private fun HeroPage(
    ch: Channel,
    logo: String,
    favorite: Boolean,
    heroHeight: Dp,
    onPlay: (Channel) -> Unit,
    onInfo: (Channel) -> Unit,
    onFavorite: () -> Unit,
) {
    val context = LocalContext.current
    val scheme = MaterialTheme.colorScheme
    val dark = LocalDarkTheme.current
    val haptic = LocalHapticFeedback.current
    val origin = LocalPlayOrigin.current
    val key = "hero:${ch.id}"

    // Muted live stream preview (volume = 0f, 400ms debounce)
    var livePlayer by remember(ch.id) { mutableStateOf<ExoPlayer?>(null) }
    var liveReady by remember(ch.id) { mutableStateOf(false) }

    LaunchedEffect(ch.id) {
        liveReady = false
        delay(400)
        try {
            val url = playableUrl(ch)
            val p = ExoPlayer.Builder(context)
                .setMediaSourceFactory(DefaultMediaSourceFactory(OkHttpDataSource.Factory(playerHttpClient)))
                .build().apply {
                volume = 0f
                repeatMode = Player.REPEAT_MODE_OFF
                playWhenReady = true
            }
            val item = MediaItem.Builder().setUri(url).build()
            p.setMediaItem(item)
            p.prepare()
            livePlayer = p
            liveReady = true
        } catch (_: Throwable) {
            liveReady = false
        }
    }

    DisposableEffect(ch.id) {
        onDispose {
            livePlayer?.release()
            livePlayer = null
        }
    }

    Box(
        Modifier
            .fillMaxWidth()
            .height(heroHeight)
            .sharedContainer(key, HeroShape)
            .clipToBounds()
            .background(scheme.background)
    ) {
        // Edge-to-edge live muted HLS stream background
        if (liveReady && livePlayer != null) {
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        useController = false
                        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                        setShutterBackgroundColor(android.graphics.Color.TRANSPARENT)
                        this.player = livePlayer
                    }
                },
                modifier = Modifier.fillMaxSize(),
            )
        } else if (logo.isNotEmpty()) {
            AsyncImage(
                logo, null,
                Modifier
                    .fillMaxSize()
                    .graphicsLayer { scaleX = 2.2f; scaleY = 2.2f; alpha = if (Build.VERSION.SDK_INT >= 31) 0.6f else 0.2f }
                    .blur(80.dp),
                contentScale = ContentScale.Crop,
            )
        }

        // Vignette fading into colorScheme.background
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to scheme.background.copy(alpha = 0.45f),
                        0.35f to Color.Transparent,
                        0.75f to scheme.background.copy(alpha = 0.75f),
                        1f to scheme.background,
                    )
                )
        )

        Column(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(contentAlignment = Alignment.Center) {
                // soft ambient back-glow
                Box(
                    Modifier
                        .size(width = 240.dp, height = 120.dp)
                        .background(Brush.radialGradient(listOf(scheme.primaryContainer.copy(alpha = if (dark) 0.35f else 0.55f), Color.Transparent)))
                )
                if (logo.isNotEmpty()) {
                    AsyncImage(logo, ch.name, Modifier.fillMaxWidth(0.52f).height(85.dp), contentScale = ContentScale.Fit)
                } else {
                    Text(ch.name, style = MaterialTheme.typography.headlineSmall, color = scheme.onSurface, maxLines = 1)
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                ch.name,
                style = MaterialTheme.typography.titleMedium,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = scheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(6.dp))

            // TertiaryContainer badge pills with stretched variable numbers
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                // LIVE badge pill
                Box(
                    Modifier
                        .clip(RoundedCornerShape(50))
                        .background(scheme.tertiaryContainer)
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(5.dp).clip(CircleShape).background(scheme.error))
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "LIVE",
                            color = scheme.onTertiaryContainer,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            style = com.israeltv.plus.ui.theme.StretchedNumberStyle,
                            modifier = Modifier.graphicsLayer { scaleY = 1.15f },
                        )
                    }
                }

                // FHD / Format badge pill
                if (ch.badge.isNotEmpty()) {
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(50))
                            .background(scheme.tertiaryContainer)
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                    ) {
                        Text(
                            ch.badge,
                            color = scheme.onTertiaryContainer,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            style = com.israeltv.plus.ui.theme.StretchedNumberStyle,
                            modifier = Modifier.graphicsLayer { scaleY = 1.15f },
                        )
                    }
                }

                // Channel number badge pill
                Box(
                    Modifier
                        .clip(RoundedCornerShape(50))
                        .background(scheme.tertiaryContainer)
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                ) {
                    Text(
                        "${ch.number}",
                        color = scheme.onTertiaryContainer,
                        fontSize = 10.sp,
                        fontWeight = FontWeight(800),
                        style = com.israeltv.plus.ui.theme.StretchedNumberStyle,
                        modifier = Modifier.graphicsLayer { scaleY = 1.15f },
                    )
                }

                val category = shortCategory(ch.category)
                if (category.isNotEmpty()) {
                    Text(
                        category,
                        color = scheme.onSurfaceVariant,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(start = 4.dp),
                    )
                }
            }

            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                GlassIconButton(
                    if (favorite) Icons.Rounded.Star else Icons.Rounded.StarBorder, "מועדף", onFavorite, size = 44.dp,
                    tint = scheme.onSurface.copy(alpha = 0.14f), iconTint = scheme.onSurface,
                )
                // 'צפייה' CTA in colorScheme.primary with tactile bounce
                Button(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        origin.key = key
                        onPlay(ch)
                    },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = scheme.primary,
                        contentColor = scheme.onPrimary,
                    ),
                    contentPadding = PaddingValues(horizontal = 28.dp),
                    modifier = Modifier.height(44.dp).tactileBounce(),
                ) {
                    Icon(Icons.Rounded.PlayArrow, null, Modifier.size(24.dp), tint = scheme.onPrimary)
                    Spacer(Modifier.width(5.dp))
                    Text("צפייה", fontSize = 15.sp, fontWeight = FontWeight.Black, color = scheme.onPrimary)
                }
                GlassIconButton(
                    Icons.Rounded.Info, "מידע", { onInfo(ch) }, size = 44.dp,
                    tint = scheme.onSurface.copy(alpha = 0.14f), iconTint = scheme.onSurface,
                )
            }
        }
    }
}

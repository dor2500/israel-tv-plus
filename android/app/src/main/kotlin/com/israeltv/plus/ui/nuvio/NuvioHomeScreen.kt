package com.israeltv.plus.ui.nuvio

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.israeltv.plus.R
import com.israeltv.plus.data.AppPrefs
import com.israeltv.plus.data.Category
import com.israeltv.plus.data.Channel
import com.israeltv.plus.data.ChannelStore
import com.israeltv.plus.data.shortCategory
import com.israeltv.plus.data.subtitle
import com.israeltv.plus.ui.components.ChannelLogo
import com.israeltv.plus.ui.components.GlassIconButton
import com.israeltv.plus.ui.components.NuvioPosterCard
import com.israeltv.plus.ui.components.ShelfSkeleton
import com.israeltv.plus.ui.components.pressPhysics
import com.israeltv.plus.ui.components.rememberBitChordFlingBehavior
import com.israeltv.plus.ui.theme.LocalDarkTheme
import com.israeltv.plus.ui.theme.LocalDimens
import kotlinx.coroutines.delay

/** Room under the feed for the floating pill (52 dp), its bottom padding (14 dp) and a margin (120 dp total). */
private val NavClearance = 120.dp
private val PosterWidth = 126.dp
private val HeroCorner = 28.dp
private const val HeroFeaturedCount = 5
private const val HeroAutoAdvanceMillis = 8_000L

/**
 * Nuvio's Home feed bound to the israel-tv-plus [ChannelStore]: a featured-channel hero with gradient stack,
 * live badge, "צפייה" action, then LazyRows of 2:3 posters per category and favorites/recents shelves.
 */
@Composable
fun NuvioHomeScreen(
    store: ChannelStore,
    prefs: AppPrefs,
    onPlay: (Channel, List<Channel>) -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
) {
    val data by store.state.collectAsStateWithLifecycle()
    val categories = data?.categories
    val d = LocalDimens.current
    val top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 8.dp
    val bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + NavClearance
    val fling = rememberBitChordFlingBehavior()
    val featured = remember(data) {
        val f = data?.featured
        if (!f.isNullOrEmpty()) f else {
            val first = categories?.firstOrNull()
            first?.channels?.take(HeroFeaturedCount) ?: emptyList()
        }
    }
    val heroHeight = (LocalConfiguration.current.screenHeightDp.dp * 0.76f).coerceIn(380.dp, 580.dp)

    when {
        categories == null -> Column(modifier.fillMaxSize().padding(top = top)) {
            repeat(3) { ShelfSkeleton() }
        }
        categories.isEmpty() -> Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                stringResource(R.string.nuvio_empty_feed),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        else -> LazyColumn(
            modifier = modifier.fillMaxSize(),
            state = listState,
            flingBehavior = fling,
            contentPadding = PaddingValues(top = top, bottom = bottom),
            verticalArrangement = Arrangement.spacedBy(d.shelfGap),
        ) {
            if (featured.isNotEmpty()) {
                item(key = "hero", contentType = "hero") {
                    NuvioHero(
                        featured = featured,
                        store = store,
                        prefs = prefs,
                        onPlay = onPlay,
                        height = heroHeight,
                    )
                }
            }

            val recent = prefs.recents.mapNotNull { data?.byId?.get(it) }
            if (recent.isNotEmpty()) {
                item(key = "recents", contentType = "shelf") {
                    NuvioChannelRow(
                        title = "המשך צפייה",
                        channels = recent,
                        store = store,
                        onPlay = { onPlay(it, recent) },
                    )
                }
            }

            val favs = prefs.favorites.mapNotNull { data?.byId?.get(it) }
            if (favs.isNotEmpty()) {
                item(key = "favorites", contentType = "shelf") {
                    NuvioChannelRow(
                        title = "הרשימה שלי",
                        channels = favs,
                        store = store,
                        onPlay = { onPlay(it, favs) },
                    )
                }
            }

            items(categories, key = { it.key }, contentType = { "category" }) { category ->
                NuvioCategoryRow(category = category, store = store, onPlay = onPlay)
            }
        }
    }
}

/**
 * Computes dominant saturated ambient glow colors for a channel poster/stream,
 * preserving signature channel tints.
 */
private fun dominantHeroGlowColors(ch: Channel, schemePrimary: Color): Pair<Color, Color> {
    val id = ch.id.lowercase()
    return when {
        "kan" in id || "11" in id -> Color(0xFFE11D48) to Color(0xFF0EA5E9)
        "keshet" in id || "12" in id -> Color(0xFFE11D48) to Color(0xFFF59E0B)
        "reshet" in id || "13" in id -> Color(0xFFE11D48) to Color(0xFF3B82F6)
        "14" in id || "now14" in id -> Color(0xFFE11D48) to Color(0xFF9F1239)
        "i24" in id || "news" in id -> Color(0xFFE11D48) to Color(0xFF0284C7)
        "sport" in id || ch.categoryEn == "sports" -> Color(0xFFE11D48) to Color(0xFF10B981)
        else -> Color(0xFFE11D48) to schemePrimary
    }
}

/**
 * Ultra-thin bordered glass badge for FHD/Live and category.
 */
@Composable
private fun HeroGlassBadge(text: String, highlight: Boolean = false) {
    val badgeShape = RoundedCornerShape(6.dp)
    Box(
        Modifier
            .clip(badgeShape)
            .background(Color.White.copy(alpha = if (highlight) 0.12f else 0.06f))
            .border(
                width = 0.75.dp,
                color = Color.White.copy(alpha = if (highlight) 0.45f else 0.24f),
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

/**
 * Featured channels as a swipeable hero: gradient stack, live badge, "צפייה" button and capsule page dots.
 */
@Composable
private fun NuvioHero(
    featured: List<Channel>,
    store: ChannelStore,
    prefs: AppPrefs,
    onPlay: (Channel, List<Channel>) -> Unit,
    height: Dp,
) {
    if (featured.isEmpty()) return
    val pagerState = rememberPagerState(pageCount = { featured.size })
    val haptic = LocalHapticFeedback.current
    val dark = LocalDarkTheme.current
    val scheme = MaterialTheme.colorScheme

    LaunchedEffect(pagerState.settledPage, pagerState.isScrollInProgress, featured.size) {
        if (pagerState.isScrollInProgress || featured.size <= 1) return@LaunchedEffect
        delay(HeroAutoAdvanceMillis)
        pagerState.animateScrollToPage((pagerState.currentPage + 1) % featured.size)
    }

    Box(
        Modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(bottomStart = HeroCorner, bottomEnd = HeroCorner))
            .background(scheme.background)
    ) {
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
            val channel = featured[page]
            val logoUrl = store.logoUrl(channel)
            val isFav = prefs.isFavorite(channel.id)
            val (glowA, glowB) = remember(channel.id, scheme.primary) { dominantHeroGlowColors(channel, scheme.primary) }
            val quality = channel.badge.ifBlank { if (channel.isRadio) "רדיו" else "FHD" }
            val where = if (channel.categoryEn == "israel") "ערוצי ישראל" else shortCategory(channel.category)

            Box(Modifier.fillMaxSize()) {
                // Background ambient glow and blurred logo backdrop
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFF070913),
                                    glowA.copy(alpha = 0.40f),
                                    glowB.copy(alpha = 0.25f),
                                    scheme.background,
                                )
                            )
                        )
                ) {
                    if (logoUrl.isNotEmpty()) {
                        AsyncImage(
                            model = logoUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .fillMaxWidth(0.85f)
                                .height(220.dp)
                                .blur(32.dp),
                            contentScale = ContentScale.Fit,
                        )
                    }
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(
                                Brush.radialGradient(
                                    listOf(glowA.copy(alpha = 0.32f), Color.Transparent),
                                    radius = 800f,
                                )
                            )
                    )
                }

                // Bottom fade gradient stack
                Box(
                    Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(130.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, scheme.background.copy(alpha = 0.85f), scheme.background)
                            )
                        )
                )

                // Foreground content: Channel Logo, Badges, and Action buttons
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.65f)
                            .aspectRatio(2.6f),
                        contentAlignment = Alignment.Center,
                    ) {
                        ChannelLogo(
                            url = logoUrl,
                            isRadio = channel.isRadio,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }

                    Spacer(Modifier.height(14.dp))

                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                        ) {
                            HeroGlassBadge(quality)
                            HeroGlassBadge(where)
                            HeroGlassBadge("Live", highlight = true)
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        GlassIconButton(
                            icon = if (isFav) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                            contentDescription = "שמור",
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                                prefs.toggleFavorite(channel.id)
                            },
                            size = 46.dp,
                            tint = scheme.onSurface.copy(alpha = 0.14f),
                            iconTint = if (isFav) Color(0xFFFFD54F) else scheme.onSurface,
                        )

                        val playInteraction = remember { MutableInteractionSource() }
                        val playShape = RoundedCornerShape(24.dp)
                        Button(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                                onPlay(channel, featured)
                            },
                            interactionSource = playInteraction,
                            shape = playShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (dark) Color.White else scheme.onSurface,
                                contentColor = if (dark) Color.Black else scheme.surface,
                            ),
                            contentPadding = PaddingValues(horizontal = 28.dp, vertical = 12.dp),
                            modifier = Modifier
                                .height(46.dp)
                                .pressPhysics(playInteraction, tilt = false, glowShape = playShape),
                        ) {
                            Icon(Icons.Rounded.PlayArrow, null, Modifier.size(20.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(
                                "צפייה",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                            )
                        }

                        GlassIconButton(
                            icon = Icons.Rounded.Info,
                            contentDescription = "מידע",
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                                onPlay(channel, featured)
                            },
                            size = 46.dp,
                            tint = scheme.onSurface.copy(alpha = 0.14f),
                            iconTint = scheme.onSurface,
                        )
                    }
                }
            }
        }

        // Pager indicator dots
        Row(
            Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            repeat(featured.size) { index ->
                val active = index == pagerState.currentPage
                val dotWidth by animateDpAsState(
                    targetValue = if (active) 32.dp else 8.dp,
                    animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMediumLow),
                    label = "hero_dot",
                )
                Box(
                    Modifier
                        .width(dotWidth)
                        .height(8.dp)
                        .clip(CircleShape)
                        .background(
                            if (active) MaterialTheme.colorScheme.onSurface
                            else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f),
                        ),
                )
            }
        }
    }
}

@Composable
private fun NuvioChannelRow(
    title: String,
    channels: List<Channel>,
    store: ChannelStore,
    onPlay: (Channel) -> Unit,
) {
    val d = LocalDimens.current
    val fling = rememberBitChordFlingBehavior()
    Column {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = d.gutter),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(title, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
            Text(
                stringResource(R.string.nuvio_channel_count, channels.size),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(Modifier.height(12.dp))
        LazyRow(
            flingBehavior = fling,
            contentPadding = PaddingValues(horizontal = d.gutter),
            horizontalArrangement = Arrangement.spacedBy(d.cardGap),
        ) {
            items(channels, key = { it.id }, contentType = { "poster" }) { channel ->
                NuvioPosterCard(
                    channel = channel,
                    logoUrl = remember(channel.id) { store.logoUrl(channel) },
                    subtitle = remember(channel.id) { channel.subtitle() },
                    onClick = { onPlay(channel) },
                    modifier = Modifier.width(PosterWidth),
                )
            }
        }
    }
}

@Composable
private fun NuvioCategoryRow(category: Category, store: ChannelStore, onPlay: (Channel, List<Channel>) -> Unit) {
    val d = LocalDimens.current
    val fling = rememberBitChordFlingBehavior()
    Column {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = d.gutter),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(category.name, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
            Text(
                stringResource(R.string.nuvio_channel_count, category.channels.size),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(Modifier.height(12.dp))
        LazyRow(
            flingBehavior = fling,
            contentPadding = PaddingValues(horizontal = d.gutter),
            horizontalArrangement = Arrangement.spacedBy(d.cardGap),
        ) {
            items(category.channels, key = { it.id }, contentType = { "poster" }) { channel ->
                NuvioPosterCard(
                    channel = channel,
                    logoUrl = remember(channel.id) { store.logoUrl(channel) },
                    subtitle = remember(channel.id) { channel.subtitle() },
                    onClick = { onPlay(channel, category.channels) },
                    modifier = Modifier.width(PosterWidth),
                )
            }
        }
    }
}

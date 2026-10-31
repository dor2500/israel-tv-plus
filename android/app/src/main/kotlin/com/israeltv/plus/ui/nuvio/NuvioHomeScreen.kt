package com.israeltv.plus.ui.nuvio

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.israeltv.plus.R
import com.israeltv.plus.data.Category
import com.israeltv.plus.data.Channel
import com.israeltv.plus.data.ChannelStore
import com.israeltv.plus.data.subtitle
import com.israeltv.plus.ui.components.ChannelLogo
import com.israeltv.plus.ui.components.ShelfSkeleton
import com.israeltv.plus.ui.components.rememberBitChordFlingBehavior
import com.israeltv.plus.ui.theme.LocalDimens
import kotlinx.coroutines.delay

/** Room under the feed for the floating pill (52 dp), its bottom padding (14 dp) and a margin. */
private val NavClearance = 52.dp + 14.dp + 24.dp
private val PosterWidth = 126.dp
private val HeroCorner = 28.dp
private const val HeroFeaturedCount = 5
private const val HeroAutoAdvanceMillis = 8_000L

/**
 * Nuvio's Home feed bound to the israel-tv-plus [ChannelStore]: a featured-channel hero, then one LazyRow of 2:3
 * posters per category from channels.json. Flings use the BitChord decay; overscroll is the theme's rubber band.
 */
@Composable
fun NuvioHomeScreen(
    store: ChannelStore,
    onPlay: (Channel, List<Channel>) -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
) {
    val data by store.state.collectAsStateWithLifecycle()
    val categories = data?.categories
    val d = LocalDimens.current
    val top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 16.dp
    val bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + NavClearance
    val fling = rememberBitChordFlingBehavior()
    val featured = remember(categories) {
        val first = categories?.firstOrNull()
        first?.channels?.take(HeroFeaturedCount) ?: emptyList()
    }
    val heroHeight = (LocalConfiguration.current.screenHeightDp.dp * 0.82f).coerceIn(360.dp, 760.dp)

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
                    NuvioHero(featured = featured, store = store, onPlay = onPlay, height = heroHeight)
                }
            }
            items(categories, key = { it.key }, contentType = { "category" }) { category ->
                NuvioCategoryRow(category = category, store = store, onPlay = onPlay)
            }
        }
    }
}

/**
 * Featured channels as a swipeable hero: 82% of the screen tall (360–760 dp), bottom corners rounded, a fade into the
 * background and page dots. Advances itself every 8 s; a tap plays the channel on screen.
 */
@Composable
private fun NuvioHero(
    featured: List<Channel>,
    store: ChannelStore,
    onPlay: (Channel, List<Channel>) -> Unit,
    height: Dp,
) {
    val pagerState = rememberPagerState(pageCount = { featured.size })
    LaunchedEffect(featured) {
        while (featured.size > 1) {
            delay(HeroAutoAdvanceMillis)
            pagerState.animateScrollToPage((pagerState.currentPage + 1) % featured.size)
        }
    }
    val fadeColor = MaterialTheme.colorScheme.background
    Box(Modifier.fillMaxWidth().height(height)) {
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
            val channel = featured[page]
            Box(
                Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(bottomStart = HeroCorner, bottomEnd = HeroCorner))
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                    .clickable { onPlay(channel, featured) },
                contentAlignment = Alignment.Center,
            ) {
                ChannelLogo(
                    url = store.logoUrl(channel),
                    isRadio = channel.isRadio,
                    modifier = Modifier.fillMaxWidth(0.62f).aspectRatio(2.6f),
                )
            }
        }
        Box(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(96.dp)
                .background(Brush.verticalGradient(listOf(Color.Transparent, fadeColor))),
        )
        Row(
            Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            repeat(featured.size) { index ->
                val active = index == pagerState.currentPage
                Box(
                    Modifier
                        .width(if (active) 32.dp else 8.dp)
                        .height(8.dp)
                        .clip(CircleShape)
                        .background(
                            if (active) MaterialTheme.colorScheme.onSurface
                            else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        ),
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

package com.israeltv.plus.ui.nuvio

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.israeltv.plus.R
import com.israeltv.plus.data.Category
import com.israeltv.plus.data.Channel
import com.israeltv.plus.data.ChannelStore
import com.israeltv.plus.data.subtitle
import com.israeltv.plus.ui.components.ShelfSkeleton
import com.israeltv.plus.ui.components.rememberBitChordFlingBehavior
import com.israeltv.plus.ui.theme.LocalDimens

/** Room under the feed for the floating pill (52 dp) and its margin. */
private val NavClearance = 52.dp + 12.dp + 24.dp

/**
 * Nuvio's Home feed bound to the israel-tv-plus [ChannelStore]: one vertical LazyColumn, one LazyRow of 2:3 posters
 * per category from channels.json. Flings use the BitChord decay; overscroll is the theme's rubber band.
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
            items(categories, key = { it.key }, contentType = { "category" }) { category ->
                NuvioCategoryRow(category = category, store = store, onPlay = onPlay)
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
                    modifier = Modifier.width(d.posterWidth),
                )
            }
        }
    }
}

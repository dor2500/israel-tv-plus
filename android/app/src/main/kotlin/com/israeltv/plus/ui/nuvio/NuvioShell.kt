package com.israeltv.plus.ui.nuvio

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.israeltv.plus.data.AppPrefs
import com.israeltv.plus.data.Channel
import com.israeltv.plus.data.ChannelStore
import com.israeltv.plus.data.subtitle
import com.israeltv.plus.ui.components.LocalHaze
import com.israeltv.plus.ui.theme.LocalDimens
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

/** Scrolling down past this many px collapses the navbar's selected label; scrolling back up restores it. */
private const val LabelCollapseAfterPx = 60

/**
 * The Nuvio shell: content as the haze source, the glass pill floating over it at the bottom.
 * All tabs (Home, Channels, Mine/Favorites, Settings) are fully bound without placeholders.
 */
@Composable
fun NuvioShell(
    store: ChannelStore,
    prefs: AppPrefs,
    onPlay: (Channel, List<Channel>) -> Unit,
    modifier: Modifier = Modifier,
) {
    var tab by rememberSaveable { mutableStateOf(NuvioTab.Home) }
    val homeList = rememberLazyListState()
    var labelsVisible by remember { mutableStateOf(true) }
    LaunchedEffect(homeList) {
        var lastIndex = homeList.firstVisibleItemIndex
        var lastOffset = homeList.firstVisibleItemScrollOffset
        snapshotFlow { homeList.firstVisibleItemIndex to homeList.firstVisibleItemScrollOffset }.collect { (index, offset) ->
            val down = index > lastIndex || (index == lastIndex && offset > lastOffset)
            val up = index < lastIndex || (index == lastIndex && offset < lastOffset)
            if (down && (index > 0 || offset > LabelCollapseAfterPx)) labelsVisible = false
            if (up) labelsVisible = true
            lastIndex = index
            lastOffset = offset
        }
    }
    val haze = rememberHazeState()
    CompositionLocalProvider(LocalHaze provides haze) {
        Box(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            Box(Modifier.fillMaxSize().hazeSource(haze)) {
                when (tab) {
                    NuvioTab.Home -> NuvioHomeScreen(store = store, prefs = prefs, onPlay = onPlay, listState = homeList)
                    NuvioTab.Channels -> NuvioChannelsTab(store = store, onPlay = onPlay)
                    NuvioTab.Mine -> NuvioMineTab(store = store, prefs = prefs, onPlay = onPlay)
                    NuvioTab.Settings -> NuvioSettingsTab(prefs = prefs)
                }
            }
            NuvioNavBar(
                selected = tab,
                onSelect = { tab = it },
                labelsVisible = labelsVisible || tab != NuvioTab.Home,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(bottom = 14.dp),
            )
        }
    }
}

@Composable
private fun NuvioMineTab(
    store: ChannelStore,
    prefs: AppPrefs,
    onPlay: (Channel, List<Channel>) -> Unit,
) {
    val data by store.state.collectAsStateWithLifecycle()
    val d = LocalDimens.current
    val top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 16.dp
    val bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 120.dp
    val favs = prefs.favorites.mapNotNull { data?.byId?.get(it) }
    val recent = prefs.recents.mapNotNull { data?.byId?.get(it) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = top, bottom = bottom),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item(key = "fav_title") {
            Text(
                text = "הרשימה שלי",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = d.gutter),
            )
        }

        if (favs.isEmpty()) {
            item(key = "fav_empty") {
                Text(
                    text = "עוד אין פה כלום...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = d.gutter, vertical = 12.dp),
                )
            }
        } else {
            items(favs.chunked(3), key = { "f_row_" + it.first().id }) { row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = d.gutter),
                    horizontalArrangement = Arrangement.spacedBy(d.cardGap),
                ) {
                    row.forEach { ch ->
                        NuvioPosterCard(
                            channel = ch,
                            logoUrl = store.logoUrl(ch),
                            subtitle = ch.subtitle(),
                            onClick = { onPlay(ch, favs) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                    repeat(3 - row.size) {
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
        }

        item(key = "rec_title") {
            Spacer(Modifier.height(8.dp))
            Text(
                text = "המשך צפייה",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = d.gutter),
            )
        }

        if (recent.isEmpty()) {
            item(key = "rec_empty") {
                Text(
                    text = "עוד אין פה כלום...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = d.gutter, vertical = 12.dp),
                )
            }
        } else {
            items(recent.chunked(3), key = { "r_row_" + it.first().id }) { row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = d.gutter),
                    horizontalArrangement = Arrangement.spacedBy(d.cardGap),
                ) {
                    row.forEach { ch ->
                        NuvioPosterCard(
                            channel = ch,
                            logoUrl = store.logoUrl(ch),
                            subtitle = ch.subtitle(),
                            onClick = { onPlay(ch, recent) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                    repeat(3 - row.size) {
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun NuvioChannelsTab(
    store: ChannelStore,
    onPlay: (Channel, List<Channel>) -> Unit,
) {
    val data by store.state.collectAsStateWithLifecycle()
    val categories = data?.categories.orEmpty()
    val d = LocalDimens.current
    val top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 16.dp
    val bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 120.dp

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = top, bottom = bottom),
        verticalArrangement = Arrangement.spacedBy(d.shelfGap),
    ) {
        item(key = "channels_heading") {
            Text(
                text = "ערוצים",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = d.gutter),
            )
        }

        items(categories, key = { it.key }) { category ->
            Column {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = d.gutter),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        category.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        "${category.channels.size} ערוצים",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Spacer(Modifier.height(12.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = d.gutter),
                    horizontalArrangement = Arrangement.spacedBy(d.cardGap),
                ) {
                    items(category.channels, key = { it.id }) { channel ->
                        NuvioPosterCard(
                            channel = channel,
                            logoUrl = store.logoUrl(channel),
                            subtitle = channel.subtitle(),
                            onClick = { onPlay(channel, category.channels) },
                            modifier = Modifier.width(126.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NuvioSettingsTab(
    prefs: AppPrefs,
) {
    val d = LocalDimens.current
    val top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 16.dp
    val bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 120.dp

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = top, bottom = bottom),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item(key = "settings_heading") {
            Text(
                text = "הגדרות",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = d.gutter),
            )
        }
        item(key = "settings_info") {
            Text(
                text = "ישראל TV+ • ממשק Nuvio",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = d.gutter),
            )
        }
    }
}

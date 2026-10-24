package com.israeltv.plus.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.israeltv.plus.data.AppPrefs
import com.israeltv.plus.data.Channel
import com.israeltv.plus.data.ChannelStore
import com.israeltv.plus.data.UpdateState
import com.israeltv.plus.data.Updater
import com.israeltv.plus.data.subtitle
import com.israeltv.plus.ui.components.AmbientBackground
import com.israeltv.plus.ui.components.AppHeader
import com.israeltv.plus.ui.components.FloatingTabBar
import com.israeltv.plus.ui.components.LocalHaze
import com.israeltv.plus.ui.components.Tab
import com.israeltv.plus.ui.player.PlayerScreen
import com.israeltv.plus.ui.screens.AvatarDialog
import com.israeltv.plus.ui.screens.ChannelInfoSheet
import com.israeltv.plus.ui.screens.ChannelsScreen
import com.israeltv.plus.ui.screens.HomeScreen
import com.israeltv.plus.ui.screens.MineScreen
import com.israeltv.plus.ui.screens.SearchOverlay
import com.israeltv.plus.ui.screens.SettingsScreen
import com.israeltv.plus.ui.screens.UpdateModal
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

private data class PlayRequest(val channel: Channel, val playlist: List<Channel>)

/**
 * The phone app. Layers, back to front: the feed (the haze source the glass blurs), the header and the floating
 * tab bar (z 10), search, then the player, then the profile / update modals.
 */
@Composable
fun ItvApp(
    store: ChannelStore,
    prefs: AppPrefs,
    updater: Updater,
    inPip: Boolean,
    onPlaying: (Boolean) -> Unit,
    onEnterPip: () -> Unit,
) {
    val data by store.state.collectAsStateWithLifecycle()
    val haze = rememberHazeState()
    var tab by rememberSaveable { mutableStateOf(Tab.Home) }
    var category by rememberSaveable { mutableStateOf<String?>(null) }
    var searchOpen by rememberSaveable { mutableStateOf(false) }
    var avatarOpen by remember { mutableStateOf(false) }
    var updateOpen by remember { mutableStateOf(false) }
    var info by remember { mutableStateOf<Channel?>(null) }
    var play by remember { mutableStateOf<PlayRequest?>(null) }

    val logo: (Channel) -> String = remember(store) { { store.logoUrl(it) } }
    val subtitle: (Channel) -> String = remember { { it.subtitle() } }
    val onPlay: (Channel, List<Channel>) -> Unit = { ch, list -> info = null; play = PlayRequest(ch, list) }

    // a new release opens the update modal once per version
    val update = updater.state
    LaunchedEffect(update) {
        if (update is UpdateState.Available && prefs.updatePrompted != update.info.version) {
            prefs.markUpdatePrompted(update.info.version)
            updateOpen = true
        }
    }

    BackHandler(enabled = play == null && (searchOpen || tab != Tab.Home)) {
        if (searchOpen) searchOpen = false else tab = Tab.Home
    }

    Box(Modifier.fillMaxSize()) {
        // the feed: what the glass blurs. Glass inside it is a plain tint (LocalHaze = null), never blur in blur.
        Box(Modifier.fillMaxSize().hazeSource(haze)) {
            AmbientBackground()
            CompositionLocalProvider(LocalHaze provides null) {
                AnimatedContent(
                    tab,
                    transitionSpec = { fadeIn(tween(240)) togetherWith fadeOut(tween(160)) },
                    label = "tabs",
                ) { t ->
                    when (t) {
                        Tab.Home -> HomeScreen(data, prefs, logo, subtitle, onPlay, onInfo = { info = it }) {
                            category = it
                            tab = Tab.Channels
                        }
                        Tab.Channels -> ChannelsScreen(data, category, { category = it }, logo, subtitle, onPlay)
                        Tab.Mine -> MineScreen(data, prefs, logo, subtitle, onPlay)
                        Tab.Settings -> SettingsScreen(data, prefs, updater) { avatarOpen = true }
                    }
                }
            }
        }

        CompositionLocalProvider(LocalHaze provides haze) {
            AppHeader(tab.title, prefs.avatar, onAvatar = { avatarOpen = true }, modifier = Modifier.align(Alignment.TopCenter))
            FloatingTabBar(
                tab, searchOpen,
                onSelect = { searchOpen = false; tab = it },
                onSearch = { searchOpen = true },
                modifier = Modifier.align(Alignment.BottomCenter),
            )

            AnimatedVisibility(
                searchOpen,
                modifier = Modifier.zIndex(15f),
                enter = fadeIn(tween(200)) + slideInVertically { it / 8 },
                exit = fadeOut(tween(150)) + slideOutVertically { it / 8 },
            ) {
                SearchOverlay(data, logo, subtitle, onPlay, onClose = { searchOpen = false })
            }
        }

        play?.let { req ->
            Box(Modifier.fillMaxSize().zIndex(20f)) {
                PlayerScreen(
                    start = req.channel,
                    playlist = req.playlist,
                    prefs = prefs,
                    logo = logo,
                    inPip = inPip,
                    onPlaying = onPlaying,
                    onEnterPip = onEnterPip,
                    onClose = { play = null },
                )
            }
        }

        CompositionLocalProvider(LocalHaze provides haze) {
            Box(Modifier.fillMaxSize().zIndex(30f)) {
                if (avatarOpen) AvatarDialog(prefs, updater) { avatarOpen = false }
                if (updateOpen && play == null) UpdateModal(updater) { updateOpen = false }
            }
        }
        info?.let { ch -> ChannelInfoSheet(ch, logo(ch), prefs, onPlay = { onPlay(ch, listOf(ch)) }, onDismiss = { info = null }) }
    }
}

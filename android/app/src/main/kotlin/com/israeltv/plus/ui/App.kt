package com.israeltv.plus.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.israeltv.plus.data.AppPrefs
import com.israeltv.plus.data.Channel
import com.israeltv.plus.data.ChannelData
import com.israeltv.plus.data.ChannelStore
import com.israeltv.plus.data.UpdateState
import com.israeltv.plus.data.Updater
import com.israeltv.plus.data.shortCategory
import com.israeltv.plus.data.subtitle
import com.israeltv.plus.ui.components.AmbientBackground
import com.israeltv.plus.ui.components.AppHeader
import com.israeltv.plus.ui.components.CardMenuActions
import com.israeltv.plus.ui.components.Cascade
import com.israeltv.plus.ui.components.FloatingTabBar
import com.israeltv.plus.ui.components.IsraeliOnlyZomatoOverlay
import com.israeltv.plus.ui.components.IsraeliOverlayMode
import com.israeltv.plus.ui.components.LocalCardMenu
import com.israeltv.plus.ui.components.LocalCascade
import com.israeltv.plus.ui.components.LocalHaze
import com.israeltv.plus.ui.components.LocalPageScope
import com.israeltv.plus.ui.components.LocalPlayOrigin
import com.israeltv.plus.ui.components.LocalSharedScope
import com.israeltv.plus.ui.components.MacroCategories
import com.israeltv.plus.ui.components.MoreCategoriesSheet
import com.israeltv.plus.ui.components.PlayOrigin
import com.israeltv.plus.ui.components.Tab
import com.israeltv.plus.ui.player.PlayerScreen
import com.israeltv.plus.ui.screens.ChannelInfoSheet
import com.israeltv.plus.ui.screens.ChannelsScreen
import com.israeltv.plus.ui.screens.HomeScreen
import com.israeltv.plus.ui.screens.MineScreen
import com.israeltv.plus.ui.screens.ProfilesScreen
import com.israeltv.plus.ui.screens.SearchOverlay
import com.israeltv.plus.ui.screens.SettingsScreen
import com.israeltv.plus.ui.screens.StyleScreen
import com.israeltv.plus.ui.screens.UpdateModal
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

private data class PlayRequest(
    val channel: Channel,
    val playlist: List<Channel>,
    val origin: String?,
    val startInMultiView: Boolean = false,
    val startInCast: Boolean = false,
)

/** The app's pages: they cross over in one AnimatedContent, so elements can be shared between them. */
private sealed interface Page {
    data object Profiles : Page
    data object Feed : Page
    data class Player(val request: PlayRequest) : Page
}

/**
 * The phone app. One SharedTransitionLayout with three pages: "who's watching" (its avatar flies into the header),
 * the feed (the haze source the glass blurs, the header and the floating bar at z 10, search, the style page) and
 * the player, which grows out of the tapped card (container transform) and shrinks back into it. The cold-start
 * intro and the update modal sit above all of them.
 */
@Composable
fun ItvApp(
    store: ChannelStore,
    prefs: AppPrefs,
    updater: Updater,
    inPip: Boolean,
    coldStart: Boolean,
    onPlaying: (Boolean) -> Unit,
    onEnterPip: () -> Unit,
) {
    val data by store.state.collectAsStateWithLifecycle()
    var page by remember { mutableStateOf<Page>(if (coldStart) Page.Profiles else Page.Feed) }
    var intro by rememberSaveable { mutableStateOf(coldStart) }
    val cascade = remember { Cascade(active = coldStart) }
    val origin = remember { PlayOrigin() }
    var updateOpen by remember { mutableStateOf(false) }
    var info by remember { mutableStateOf<Channel?>(null) }
    val feed = remember { FeedState() }
    val haze = rememberHazeState()
    var profilesFromFeed by remember { mutableStateOf(false) }

    val logo: (Channel) -> String = remember(store) { { store.logoUrl(it) } }
    val onPlay: (Channel, List<Channel>) -> Unit = { ch, list ->
        info = null
        page = Page.Player(PlayRequest(ch, list, origin.key))
        origin.key = null
    }

    // Every poster card (home shelves, continue watching, channel grid, search, my list) shares this long-press menu
    val cardMenu = remember(prefs) {
        CardMenuActions(
            isFavorite = { prefs.isFavorite(it.id) },
            toggleFavorite = { prefs.toggleFavorite(it.id) },
            onInfo = { info = it },
        )
    }

    val update = updater.state
    LaunchedEffect(update) {
        if (update is UpdateState.Available && prefs.updatePrompted != update.info.version) {
            prefs.markUpdatePrompted(update.info.version)
            updateOpen = true
        }
    }

    Box(Modifier.fillMaxSize()) {
        CompositionLocalProvider(
            LocalCascade provides cascade,
            LocalPlayOrigin provides origin,
            LocalCardMenu provides cardMenu,
        ) {
            SharedTransitionLayout(Modifier.fillMaxSize()) {
                AnimatedContent(
                    page,
                    contentKey = { it::class },
                    transitionSpec = {
                        when {
                            initialState is Page.Profiles -> fadeIn(tween(420, 120)) + scaleIn(initialScale = 1.06f) togetherWith fadeOut(tween(260))
                            targetState is Page.Profiles -> fadeIn(tween(300)) + scaleIn(initialScale = 0.94f) togetherWith fadeOut(tween(200))
                            else -> fadeIn(tween(220)) togetherWith fadeOut(tween(260, 80))
                        }
                    },
                    label = "pages",
                ) { p ->
                    CompositionLocalProvider(LocalSharedScope provides this@SharedTransitionLayout, LocalPageScope provides this) {
                        when (p) {
                            Page.Profiles -> ProfilesScreen(
                                prefs, canClose = profilesFromFeed,
                                onPicked = { feed.reset(); page = Page.Feed },
                                onClose = { page = Page.Feed },
                            )
                            Page.Feed -> Feed(
                                data, prefs, updater, feed, haze, logo, onPlay,
                                onOpenMultiView = {
                                    val all = data?.channels.orEmpty()
                                    val first = prefs.recents.firstNotNullOfOrNull { data?.byId?.get(it) }
                                        ?: data?.featured?.firstOrNull()
                                        ?: all.firstOrNull()
                                    if (first != null) {
                                        page = Page.Player(PlayRequest(first, all, null, startInMultiView = true))
                                    }
                                },
                                onOpenCast = {
                                    val all = data?.channels.orEmpty()
                                    val first = prefs.recents.firstNotNullOfOrNull { data?.byId?.get(it) }
                                        ?: data?.featured?.firstOrNull()
                                        ?: all.firstOrNull()
                                    if (first != null) {
                                        page = Page.Player(PlayRequest(first, all, null, startInCast = true))
                                    }
                                },
                                onInfo = { info = it },
                                onProfiles = { profilesFromFeed = true; page = Page.Profiles },
                            )
                            is Page.Player -> PlayerScreen(
                                start = p.request.channel,
                                playlist = p.request.playlist,
                                prefs = prefs,
                                logo = logo,
                                inPip = inPip,
                                sharedKey = p.request.origin,
                                startInMultiView = p.request.startInMultiView,
                                startInCast = p.request.startInCast,
                                onPlaying = onPlaying,
                                onEnterPip = onEnterPip,
                                onClose = { page = Page.Feed },
                            )
                        }
                    }
                }
            }
        }

        CompositionLocalProvider(LocalHaze provides haze) {
            Box(Modifier.fillMaxSize().zIndex(30f)) {
                if (updateOpen && page == Page.Feed) UpdateModal(updater) { updateOpen = false }
            }
        }
        info?.let { ch -> ChannelInfoSheet(ch, logo(ch), prefs, onPlay = { onPlay(ch, listOf(ch)) }, onDismiss = { info = null }) }

        if (intro) {
            Box(Modifier.fillMaxSize().zIndex(100f)) {
                CinematicIntro(
                    seed = MaterialTheme.colorScheme.primary,
                    ready = data != null,
                    onPortal = { cascade.start() },
                    onDone = { intro = false },
                )
            }
        }
    }
}

/** What the feed keeps while the player or the profiles page is on screen. */
private class FeedState {
    var tab by mutableStateOf(Tab.Home)
    var category by mutableStateOf<String?>(null)
    var macroCategory by mutableStateOf<String?>(null)
    var moreCategoriesOpen by mutableStateOf(false)
    var israeliOnly by mutableStateOf(false)
    var overlayMode by mutableStateOf(IsraeliOverlayMode.Hidden)
    var switchCenter by mutableStateOf<Offset?>(null)
    var searchOpen by mutableStateOf(false)
    var styleOpen by mutableStateOf(false)
    var barCollapsed by mutableStateOf(false)
    val homeList = androidx.compose.foundation.lazy.LazyListState()
    val channelsList = androidx.compose.foundation.lazy.LazyListState()

    fun reset() {
        tab = Tab.Home
        searchOpen = false
        styleOpen = false
        barCollapsed = false
    }
}

@Composable
private fun Feed(
    data: ChannelData?,
    prefs: AppPrefs,
    updater: Updater,
    s: FeedState,
    haze: HazeState,
    logo: (Channel) -> String,
    onPlay: (Channel, List<Channel>) -> Unit,
    onOpenMultiView: () -> Unit,
    onOpenCast: () -> Unit,
    onInfo: (Channel) -> Unit,
    onProfiles: () -> Unit,
) {
    val subtitle: (Channel) -> String = remember { { it.subtitle() } }

    // Dynamic scroll fraction for the top header + categories glassmorphism blur
    val headerScrollFraction by remember(s) {
        derivedStateOf {
            when (s.tab) {
                Tab.Home -> {
                    if (s.homeList.firstVisibleItemIndex > 0) 1f
                    else (s.homeList.firstVisibleItemScrollOffset / 240f).coerceIn(0f, 1f)
                }
                Tab.Channels -> {
                    if (s.channelsList.firstVisibleItemIndex > 0) 1f
                    else (s.channelsList.firstVisibleItemScrollOffset / 120f).coerceIn(0f, 1f)
                }
                else -> 0.85f
            }
        }
    }

    // The floating bar folds into a compact pod while the feed scrolls down and opens again on the way up
    val barScroll = remember(s) {
        object : NestedScrollConnection {
            override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                if (consumed.y < -6f) s.barCollapsed = true else if (consumed.y > 6f || available.y > 0f) s.barCollapsed = false
                return Offset.Zero
            }
        }
    }

    val categories = data?.categories.orEmpty()
    val israeliCategories = remember(categories) {
        categories.filter { category -> category.channels.any { it.categoryEn == "israel" } }
    }
    val pool = if (s.israeliOnly) israeliCategories else categories
    val restCategories = remember(pool) { pool.filter { it.name !in MacroCategories } }

    BackHandler(enabled = s.searchOpen || s.tab != Tab.Home) {
        if (s.searchOpen) s.searchOpen = false else s.tab = Tab.Home
    }

    Box(Modifier.fillMaxSize()) {
        // The feed: what the glass blurs. Glass inside it is a plain tint (LocalHaze = null), never blur in blur.
        Box(Modifier.fillMaxSize().hazeSource(haze).nestedScroll(barScroll)) {
            AmbientBackground()
            CompositionLocalProvider(LocalHaze provides null) {
                AnimatedContent(
                    s.tab,
                    transitionSpec = {
                        val forward = targetState.ordinal >= initialState.ordinal
                        val enterSlide = { fullWidth: Int -> if (forward) -fullWidth / 14 else fullWidth / 14 }
                        val exitSlide = { fullWidth: Int -> if (forward) fullWidth / 14 else -fullWidth / 14 }
                        (fadeIn(tween(280, easing = FastOutSlowInEasing)) +
                            slideInHorizontally(tween(320, easing = FastOutSlowInEasing), initialOffsetX = enterSlide) +
                            scaleIn(tween(320, easing = FastOutSlowInEasing), initialScale = 0.96f))
                            .togetherWith(
                                fadeOut(tween(190, easing = FastOutSlowInEasing)) +
                                    slideOutHorizontally(tween(240, easing = FastOutSlowInEasing), targetOffsetX = exitSlide) +
                                    scaleOut(tween(240, easing = FastOutSlowInEasing), targetScale = 0.98f)
                            )
                    },
                    label = "tabs",
                ) { t ->
                    when (t) {
                        Tab.Home -> HomeScreen(
                            data = data,
                            prefs = prefs,
                            listState = s.homeList,
                            israeliOnly = s.israeliOnly,
                            onIsraeliOnlyChange = { checked ->
                                s.israeliOnly = checked
                                s.macroCategory = null
                                s.overlayMode = if (checked) IsraeliOverlayMode.Entering else IsraeliOverlayMode.Exiting
                            },
                            onSwitchPositioned = { s.switchCenter = it },
                            macroCategory = s.macroCategory,
                            logo = logo,
                            subtitle = subtitle,
                            onPlay = onPlay,
                            onInfo = onInfo,
                            onSeeAll = { catKey ->
                                s.category = catKey
                                s.macroCategory = shortCategory(catKey)
                                s.tab = Tab.Channels
                            },
                        )
                        Tab.Channels -> ChannelsScreen(
                            data = data,
                            macroCategory = s.macroCategory,
                            israeliOnly = s.israeliOnly,
                            onIsraeliOnlyChange = { checked ->
                                s.israeliOnly = checked
                                s.overlayMode = if (checked) IsraeliOverlayMode.Entering else IsraeliOverlayMode.Exiting
                            },
                            onSwitchPositioned = { s.switchCenter = it },
                            logo = logo,
                            subtitle = subtitle,
                            onPlay = onPlay,
                            listState = s.channelsList,
                        )
                        Tab.Mine -> MineScreen(data, prefs, logo, subtitle, onPlay)
                        Tab.Settings -> SettingsScreen(data, prefs, updater, onProfiles = onProfiles, onStyle = { s.styleOpen = true })
                    }
                }
            }
        }

        CompositionLocalProvider(LocalHaze provides haze) {
            AppHeader(
                profileId = prefs.profile.id,
                avatar = prefs.avatar,
                scrollFraction = headerScrollFraction,
                showCategories = (s.tab == Tab.Home || s.tab == Tab.Channels) && !s.searchOpen,
                selectedCategory = s.macroCategory,
                onSelectCategory = { s.macroCategory = it },
                onMoreCategories = { s.moreCategoriesOpen = true },
                onAvatar = onProfiles,
                onSettings = { s.searchOpen = false; s.tab = Tab.Settings },
                modifier = Modifier.align(Alignment.TopCenter),
            )
            FloatingTabBar(
                s.tab, s.searchOpen, s.barCollapsed && !s.searchOpen,
                onSelect = { s.searchOpen = false; s.barCollapsed = false; s.tab = it },
                onSearch = { s.searchOpen = true },
                modifier = Modifier.align(Alignment.BottomCenter),
            )

            AnimatedVisibility(
                s.searchOpen,
                modifier = Modifier.zIndex(15f),
                enter = fadeIn(tween(200)) + slideInVertically { it / 8 },
                exit = fadeOut(tween(150)) + slideOutVertically { it / 8 },
            ) {
                SearchOverlay(data, logo, subtitle, onPlay, onClose = { s.searchOpen = false })
            }
        }

        if (s.moreCategoriesOpen) {
            MoreCategoriesSheet(
                categories = restCategories,
                selected = s.macroCategory,
                onSelect = { s.macroCategory = it; s.moreCategoriesOpen = false },
                onDismiss = { s.moreCategoriesOpen = false },
            )
        }

        // True full-screen Zomato overlay (zIndex 100f) covering the entire screen, including header & bottom navbar
        IsraeliOnlyZomatoOverlay(
            mode = s.overlayMode,
            switchCenter = s.switchCenter,
            onDismiss = {
                s.overlayMode = if (s.overlayMode == IsraeliOverlayMode.Entering) IsraeliOverlayMode.Active else IsraeliOverlayMode.Hidden
            },
        )

        AnimatedVisibility(
            s.styleOpen,
            modifier = Modifier.zIndex(16f),
            enter = fadeIn(tween(220)) + slideInHorizontally { -it / 5 },
            exit = fadeOut(tween(180)) + slideOutHorizontally { -it / 5 },
        ) {
            val sample = remember(data) { data?.featured.orEmpty().take(1) + data?.categories?.firstOrNull()?.channels.orEmpty().take(6) }
            StyleScreen(prefs, sample, logo, onClose = { s.styleOpen = false })
        }
    }
}

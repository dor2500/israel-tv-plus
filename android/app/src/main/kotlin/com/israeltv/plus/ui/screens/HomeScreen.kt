package com.israeltv.plus.ui.screens

import com.israeltv.plus.data.isKids

import android.content.res.Configuration
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AspectRatio
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.scale
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
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
import com.israeltv.plus.data.CatchupManager
import com.israeltv.plus.data.Channel
import com.israeltv.plus.data.ChannelData
import com.israeltv.plus.data.USER_AGENT
import com.israeltv.plus.data.playableUrl
import com.israeltv.plus.data.VodSeries
import com.israeltv.plus.ui.components.VodShelf
import com.israeltv.plus.ui.components.VodSeriesSheet
import com.israeltv.plus.data.shortCategory
import com.israeltv.plus.ui.components.BoldIcon
import com.israeltv.plus.ui.components.ChannelLogo
import com.israeltv.plus.ui.components.ChannelShelf
import com.israeltv.plus.ui.components.ContinueWatchingShelf
import com.israeltv.plus.ui.components.GlassIconButton
import com.israeltv.plus.ui.components.LocalPlayOrigin
import com.israeltv.plus.ui.components.MacroCategories
import com.israeltv.plus.ui.components.ShelfSkeleton
import com.israeltv.plus.ui.components.cascadeIn
import com.israeltv.plus.ui.components.feedPadding
import com.israeltv.plus.ui.components.frostedGlass
import com.israeltv.plus.ui.components.glassShimmer
import com.israeltv.plus.ui.components.pressPhysics
import com.israeltv.plus.ui.components.rememberBitChordFlingBehavior
import com.israeltv.plus.ui.components.sharedContainer
import com.israeltv.plus.ui.theme.LocalDarkTheme
import com.israeltv.plus.ui.theme.LocalDimens
import kotlinx.coroutines.delay
import kotlin.math.abs

private val HeroShape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)
private val HeroHeight = 480.dp

/**
 * Home: on TV / Landscape displays, renders the non-overlapping 10-foot EPG / Home Split Layout
 * ([TvEpgHomeScreen]):
 * - Main categories navigation menu at the absolute TOP of the screen.
 * - Below the top menu, a horizontal `Row` splitting the remaining space:
 *   - Left Side (EPG Grid): `Modifier.weight(1f)` with colored action buttons ("אפשרויות", "גודל תמונה", etc.)
 *     in a horizontal `Row` directly UNDERNEATH the Left Side EPG Grid.
 *   - Right Side (Program Info Panel): Strictly defined `Modifier.width(350.dp)` so it sits securely
 *     on the right and never overlaps the channel grid.
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
    onSeeAllVod: (() -> Unit)? = null,
    onSelectMacroCategory: (String?) -> Unit = {},
    onOpenSettings: () -> Unit = {},
    onOpenSearch: () -> Unit = {},
) {
    val config = LocalConfiguration.current
    val isTvOrLandscape = config.orientation == Configuration.ORIENTATION_LANDSCAPE || config.screenWidthDp >= 720
    if (isTvOrLandscape) {
        TvEpgHomeScreen(
            data = data,
            prefs = prefs,
            israeliOnly = israeliOnly,
            onIsraeliOnlyChange = onIsraeliOnlyChange,
            onSwitchPositioned = onSwitchPositioned,
            macroCategory = macroCategory,
            onSelectMacroCategory = onSelectMacroCategory,
            logo = logo,
            subtitle = subtitle,
            onPlay = onPlay,
            onInfo = onInfo,
            onOpenSettings = onOpenSettings,
            onOpenSearch = onOpenSearch,
        )
        return
    }
    val padding = feedPadding(withCategories = true)
    val isKids = prefs.isKids
    val rawCategories = data?.categories.orEmpty()
    val categories = if (isKids) {
        rawCategories.mapNotNull { cat ->
            val filtered = cat.channels.filter { it.isKids() }
            if (filtered.isNotEmpty()) cat.copy(channels = filtered) else null
        }
    } else rawCategories

    val israeliCategories = remember(categories) {
        categories.filter { category -> category.channels.any { it.categoryEn == "israel" } }
    }
    val pool = if (israeliOnly && !isKids) israeliCategories else categories
    val visibleCategories = if (macroCategory == null) pool else pool.filter { it.name == macroCategory }

    // Parallax offset derived from the LazyColumn's first item scroll position
    val heroScrollPx by remember(listState) {
        derivedStateOf {
            if (listState.firstVisibleItemIndex == 0) listState.firstVisibleItemScrollOffset.toFloat() else 900f
        }
    }
    var selectedVodSeries by remember { mutableStateOf<VodSeries?>(null) }

    LazyColumn(
        Modifier.fillMaxSize(),
        state = listState,
        // Top clears the glass header; bottom keeps the last row and the "רק ישראלי" switch clear of the floating navbar
        contentPadding = padding,
        flingBehavior = rememberBitChordFlingBehavior(),
    ) {
        if (data == null) {
            item { Box(Modifier.bleedToTop(padding).padding(bottom = 28.dp).fillMaxWidth().height(HeroHeight).glassShimmer(HeroShape)) }
            items(4) { ShelfSkeleton() }
            return@LazyColumn
        }
        item(key = "hero") {
            val heroFeatured = if (prefs.isKids) {
                data.channels.filter { it.isKids() }.take(8)
            } else {
                data.featured
            }
            HeroPager(
                featured = heroFeatured,
                prefs = prefs,
                logo = logo,
                scrollOffsetPx = heroScrollPx,
                topInset = padding.calculateTopPadding(),
                onPlay = { onPlay(it, heroFeatured) },
                onInfo = onInfo,
                modifier = Modifier.bleedToTop(padding).cascadeIn(0),
            )
        }
        // The "רק ישראלי" chip docks into the trailing edge of the Israeli shelf header (else the first shelf)
        val israeliKey = visibleCategories.firstOrNull { "ישראל" in it.name }?.key
        val chipOnCategory = israeliKey != null
        val filterChip: @Composable () -> Unit = {
            IsraeliOnlyChip(israeliOnly, onIsraeliOnlyChange, onSwitchPositioned)
        }
        var row = 1
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
                    headerTrailing = if (!chipOnCategory) filterChip else null,
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
                    headerTrailing = if (!chipOnCategory && recent.isEmpty()) filterChip else null,
                    modifier = Modifier.cascadeIn(i),
                )
            }
        }
        val vodList = data.vod
        if (vodList.isNotEmpty() && !isKids && macroCategory == null) {
            val i = row++
            item(key = "vod_shelf") {
                VodShelf(
                    title = "סדרות VOD ישראליות",
                    seriesList = vodList,
                    onSeriesClick = { selectedVodSeries = it },
                    onSeeAll = onSeeAllVod,
                    modifier = Modifier.cascadeIn(i),
                )
            }
        }
        if (visibleCategories.isEmpty()) {
            item(key = "empty") {
                Box(Modifier.padding(horizontal = 16.dp)) { filterChip() }
                Text(
                    if (israeliOnly) "אין קטגוריה כזו בתוכן הישראלי" else "אין קטגוריה כזו",
                    Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 24.dp),
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
                    headerTrailing = when {
                        cat.key == israeliKey -> filterChip
                        !chipOnCategory && recent.isEmpty() && favs.isEmpty() && cat === visibleCategories.first() -> filterChip
                        else -> null
                    },
                )
            }
        }
    }
    selectedVodSeries?.let { series ->
        VodSeriesSheet(
            series = series,
            onPlayEpisode = { ep ->
                val ch = ep.toChannel(series)
                val playlist = series.episodes.map { it.toChannel(series) }
                onPlay(ch, playlist)
                selectedVodSeries = null
            },
            onDismiss = { selectedVodSeries = null }
        )
    }
}

/**
 * TV / Landscape Main EPG & Home Screen Layout (10-foot UI, D-pad navigation, zero overlap):
 * - Top: Main categories navigation menu pinned at the absolute TOP of the screen.
 * - Below Top Menu: Horizontal `Row` splitting the remaining space:
 *   - Left Side (EPG Grid): `Modifier.weight(1f)` filling the remaining space safely beside the info panel,
 *     with the colored Action Buttons ("אפשרויות", "גודל תמונה", etc.) in a horizontal `Row` directly UNDERNEATH
 *     the Left Side EPG Grid.
 *   - Right Side (Program Info Panel): Strictly defined `Modifier.width(350.dp)` so it sits securely on the right
 *     and never overlaps the channel grid.
 */
@Composable
fun TvEpgHomeScreen(
    data: ChannelData?,
    prefs: AppPrefs,
    israeliOnly: Boolean,
    onIsraeliOnlyChange: (Boolean) -> Unit,
    onSwitchPositioned: (Offset) -> Unit,
    macroCategory: String?,
    onSelectMacroCategory: (String?) -> Unit,
    logo: (Channel) -> String,
    subtitle: (Channel) -> String,
    onPlay: (Channel, List<Channel>) -> Unit,
    onInfo: (Channel) -> Unit,
    onOpenSettings: () -> Unit = {},
    onOpenSearch: () -> Unit = {},
) {
    val isKids = prefs.isKids
    val rawCategories = data?.categories.orEmpty()
    val categories = remember(rawCategories, isKids) {
        if (isKids) {
            rawCategories.mapNotNull { cat ->
                val filtered = cat.channels.filter { it.isKids() }
                if (filtered.isNotEmpty()) cat.copy(channels = filtered) else null
            }
        } else rawCategories
    }

    val israeliCategories = remember(categories) {
        categories.filter { category -> category.channels.any { it.categoryEn == "israel" } }
    }
    val pool = if (israeliOnly && !isKids) israeliCategories else categories
    val categoryNames = remember(pool) {
        val macroInPool = MacroCategories.filter { macro -> pool.any { it.name == macro } }
        val restInPool = pool.map { it.name }.filter { it !in macroInPool }
        macroInPool + restInPool
    }

    val displayedChannels = remember(pool, macroCategory) {
        if (macroCategory == null) {
            pool.flatMap { it.channels }.distinctBy { it.id }
        } else {
            pool.filter { it.name == macroCategory }.flatMap { it.channels }.distinctBy { it.id }
        }
    }

    var selectedChannel by remember(displayedChannels) {
        mutableStateOf(displayedChannels.firstOrNull())
    }
    var fitFillMode by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF090C12)),
    ) {
        // 1. Main Categories Navigation Menu at the absolute TOP of the screen
        TvTopCategoriesBar(
            categories = categoryNames,
            selectedCategory = macroCategory,
            onSelectCategory = onSelectMacroCategory,
            israeliOnly = israeliOnly,
            onIsraeliOnlyChange = onIsraeliOnlyChange,
            onSwitchPositioned = onSwitchPositioned,
            onOpenSearch = onOpenSearch,
            onOpenSettings = onOpenSettings,
            modifier = Modifier.fillMaxWidth(),
        )

        // 2. Below the top menu, split the remaining space horizontally using a Row.
        // Force Ltr on the outer split Row so Left Side is physically on the left and Right Side is physically on the right.
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // LEFT SIDE: EPG Channel Grid (Modifier.weight(1f)) + Colored Action Buttons Row directly UNDERNEATH
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .background(Color(0xFF101520))
                                .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(18.dp))
                                .padding(12.dp),
                        ) {
                            if (displayedChannels.isEmpty()) {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text(
                                        text = if (data == null) "טוען ערוצים ולוח שידורים…" else "אין ערוצים בקטגוריה זו",
                                        color = Color.White.copy(alpha = 0.7f),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                    )
                                }
                            } else {
                                LazyVerticalGrid(
                                    columns = GridCells.Adaptive(minSize = 190.dp),
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                ) {
                                    items(
                                        items = displayedChannels,
                                        key = { it.id },
                                    ) { channel ->
                                        val isSelected = selectedChannel?.id == channel.id
                                        TvEpgChannelGridCard(
                                            channel = channel,
                                            logoUrl = logo(channel),
                                            isSelected = isSelected,
                                            isFavorite = prefs.isFavorite(channel.id),
                                            onFocusChannel = { selectedChannel = channel },
                                            onClickChannel = {
                                                selectedChannel = channel
                                                onPlay(channel, displayedChannels)
                                            },
                                        )
                                    }
                                }
                            }
                        }

                        // Colored Action Buttons ("אפשרויות", "גודל תמונה", etc.) directly UNDERNEATH the Left Side EPG Grid
                        TvColoredActionButtonsRow(
                            fitFillMode = fitFillMode,
                            isFavorite = selectedChannel?.let { prefs.isFavorite(it.id) } ?: false,
                            onOptionsClick = { selectedChannel?.let(onInfo) },
                            onPictureSizeClick = { fitFillMode = !fitFillMode },
                            onFavoriteClick = { selectedChannel?.let { prefs.toggleFavorite(it.id) } },
                            onResetCategoryClick = { onSelectMacroCategory(null) },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }

                // RIGHT SIDE: Program Info Panel with strictly defined width (width(350.dp)) so it never overlaps the grid
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    TvProgramInfoPanel(
                        channel = selectedChannel,
                        isFavorite = selectedChannel?.let { prefs.isFavorite(it.id) } ?: false,
                        fitFillMode = fitFillMode,
                        logo = logo,
                        subtitle = subtitle,
                        onPlay = { ch -> onPlay(ch, displayedChannels) },
                        onInfo = onInfo,
                        modifier = Modifier
                            .width(350.dp)
                            .fillMaxHeight(),
                    )
                }
            }
        }
    }
}

@Composable
private fun TvTopCategoriesBar(
    categories: List<String>,
    selectedCategory: String?,
    onSelectCategory: (String?) -> Unit,
    israeliOnly: Boolean,
    onIsraeliOnlyChange: (Boolean) -> Unit,
    onSwitchPositioned: (Offset) -> Unit,
    onOpenSearch: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = Color(0xFF0E131D),
        border = BorderStroke(0.5.dp, Color.White.copy(alpha = 0.08f)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Categories Horizontal Row
            LazyRow(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                item(key = "__all__") {
                    TvCategoryPill(
                        label = "כל הערוצים",
                        selected = selectedCategory == null,
                        onClick = { onSelectCategory(null) },
                    )
                }
                items(categories, key = { it }) { catName ->
                    TvCategoryPill(
                        label = catName,
                        selected = selectedCategory == catName,
                        onClick = {
                            onSelectCategory(if (selectedCategory == catName) null else catName)
                        },
                    )
                }
            }

            IsraeliOnlyChip(
                checked = israeliOnly,
                onCheckedChange = onIsraeliOnlyChange,
                onPositioned = onSwitchPositioned,
            )

            GlassIconButton(
                icon = Icons.Rounded.Search,
                contentDescription = "חיפוש",
                onClick = onOpenSearch,
                size = 42.dp,
            )
            GlassIconButton(
                icon = Icons.Rounded.Settings,
                contentDescription = "הגדרות",
                onClick = onOpenSettings,
                size = 42.dp,
            )
        }
    }
}

@Composable
private fun TvCategoryPill(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val shape = RoundedCornerShape(50)

    Surface(
        modifier = Modifier
            .clip(shape)
            .focusable(interactionSource = interactionSource)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
        shape = shape,
        color = when {
            selected -> Color(0xFF2563EB)
            isFocused -> Color.White.copy(alpha = 0.20f)
            else -> Color.White.copy(alpha = 0.08f)
        },
        border = BorderStroke(
            width = if (isFocused || selected) 1.5.dp else 1.dp,
            color = when {
                isFocused -> Color.White
                selected -> Color(0xFF60A5FA)
                else -> Color.White.copy(alpha = 0.14f)
            },
        ),
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = if (selected || isFocused) FontWeight.Bold else FontWeight.Medium,
            maxLines = 1,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
    }
}

@Composable
private fun TvEpgChannelGridCard(
    channel: Channel,
    logoUrl: String,
    isSelected: Boolean,
    isFavorite: Boolean,
    onFocusChannel: () -> Unit,
    onClickChannel: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    LaunchedEffect(isFocused) {
        if (isFocused) onFocusChannel()
    }
    val liveProgram = remember(channel.id) { CatchupManager.getCurrentAndNextProgram(channel.id).first }
    val highlighted = isFocused || isSelected
    val shape = RoundedCornerShape(14.dp)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(92.dp)
            .clip(shape)
            .focusable(interactionSource = interactionSource)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClickChannel),
        shape = shape,
        color = when {
            isFocused -> Color(0xFF1E293B)
            isSelected -> Color(0xFF172033)
            else -> Color(0xFF131926)
        },
        border = BorderStroke(
            width = if (highlighted) 2.dp else 1.dp,
            color = when {
                isFocused -> Color(0xFF38BDF8)
                isSelected -> Color(0xFF3B82F6).copy(alpha = 0.85f)
                else -> Color.White.copy(alpha = 0.08f)
            },
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White.copy(alpha = 0.06f)),
                contentAlignment = Alignment.Center,
            ) {
                ChannelLogo(
                    url = logoUrl,
                    modifier = Modifier.size(38.dp),
                    isRadio = channel.isRadio,
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    if (channel.number > 0) {
                        Text(
                            text = "${channel.number}",
                            color = Color(0xFF38BDF8),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Text(
                        text = channel.name,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false),
                    )
                    if (isFavorite) {
                        Icon(
                            imageVector = Icons.Rounded.Star,
                            contentDescription = null,
                            tint = Color(0xFFFACC15),
                            modifier = Modifier.size(14.dp),
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = liveProgram?.title ?: shortCategory(channel.category),
                    color = Color.White.copy(alpha = 0.72f),
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (liveProgram != null) {
                    Text(
                        text = "${liveProgram.startTimeFormatted} - ${liveProgram.endTimeFormatted}",
                        color = Color.White.copy(alpha = 0.50f),
                        fontSize = 11.sp,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

/**
 * Colored TV Action Buttons Row placed directly UNDERNEATH the Left Side EPG Grid
 * (completely out of the top navigation area).
 */
@Composable
private fun TvColoredActionButtonsRow(
    fitFillMode: Boolean,
    isFavorite: Boolean,
    onOptionsClick: () -> Unit,
    onPictureSizeClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onResetCategoryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF101520),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TvColoredActionButton(
                dotColor = Color(0xFFEF4444), // Red button
                label = "אפשרויות",
                onClick = onOptionsClick,
            )
            TvColoredActionButton(
                dotColor = Color(0xFF22C55E), // Green button
                label = if (fitFillMode) "גודל תמונה: מלא" else "גודל תמונה: מקורי",
                onClick = onPictureSizeClick,
            )
            TvColoredActionButton(
                dotColor = Color(0xFFEAB308), // Yellow button
                label = if (isFavorite) "הסר ממועדפים" else "מועדפים",
                onClick = onFavoriteClick,
            )
            TvColoredActionButton(
                dotColor = Color(0xFF3B82F6), // Blue button
                label = "כל הערוצים",
                onClick = onResetCategoryClick,
            )
        }
    }
}

@Composable
private fun TvColoredActionButton(
    dotColor: Color,
    label: String,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val shape = RoundedCornerShape(50)

    Surface(
        modifier = Modifier
            .clip(shape)
            .focusable(interactionSource = interactionSource)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
        shape = shape,
        color = if (isFocused) Color.White.copy(alpha = 0.18f) else Color.White.copy(alpha = 0.07f),
        border = BorderStroke(
            width = if (isFocused) 1.5.dp else 1.dp,
            color = if (isFocused) Color.White else Color.White.copy(alpha = 0.14f),
        ),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(dotColor),
            )
            Text(
                text = label,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
            )
        }
    }
}

/**
 * Right Side Program Info Panel with a strictly defined width (`width(350.dp)`) so it sits securely
 * on the right and never overlaps the channel grid.
 */
@Composable
private fun TvProgramInfoPanel(
    channel: Channel?,
    isFavorite: Boolean,
    fitFillMode: Boolean,
    logo: (Channel) -> String,
    subtitle: (Channel) -> String,
    onPlay: (Channel) -> Unit,
    onInfo: (Channel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(18.dp)
    Surface(
        modifier = modifier,
        shape = shape,
        color = Color(0xFF101520),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.10f)),
    ) {
        if (channel == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "בחרו ערוץ להצגת פרטי השידור",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 14.sp,
                )
            }
            return@Surface
        }

        val (currentProg, nextProg) = remember(channel.id) {
            CatchupManager.getCurrentAndNextProgram(channel.id)
        }
        val nowMs = System.currentTimeMillis()
        val progress = remember(currentProg, nowMs) {
            if (currentProg != null && currentProg.endTimeEpochMs > currentProg.startTimeEpochMs) {
                ((nowMs - currentProg.startTimeEpochMs).toFloat() /
                    (currentProg.endTimeEpochMs - currentProg.startTimeEpochMs).toFloat()).coerceIn(0f, 1f)
            } else 0.35f
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Live 16:9 Stream Preview Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(if (fitFillMode) 16f / 9f else 16f / 9f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.Black)
                        .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(14.dp))
                        .clickable { onPlay(channel) },
                    contentAlignment = Alignment.Center,
                ) {
                    ChannelLogo(
                        url = logo(channel),
                        modifier = Modifier.size(72.dp),
                        isRadio = channel.isRadio,
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(10.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFEF4444))
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                    ) {
                        Text(
                            text = "LIVE",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                        )
                    }
                }

                // Channel Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    if (channel.number > 0) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF2563EB).copy(alpha = 0.22f),
                            border = BorderStroke(1.dp, Color(0xFF60A5FA).copy(alpha = 0.5f)),
                        ) {
                            Text(
                                text = "ערוץ ${channel.number}",
                                color = Color(0xFF60A5FA),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            )
                        }
                    }
                    Text(
                        text = channel.name,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                }

                Text(
                    text = subtitle(channel),
                    color = Color.White.copy(alpha = 0.65f),
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                // Current EPG Program Info Box
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White.copy(alpha = 0.05f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.10f)),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text(
                            text = "משודר כעת",
                            color = Color(0xFF38BDF8),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = currentProg?.title ?: "שידור חי · ${channel.name}",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                        if (currentProg != null) {
                            Text(
                                text = "${currentProg.startTimeFormatted} - ${currentProg.endTimeFormatted}",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 12.sp,
                            )
                        }
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(50)),
                            color = Color(0xFF38BDF8),
                            trackColor = Color.White.copy(alpha = 0.16f),
                        )
                        Text(
                            text = currentProg?.description?.ifBlank { null }
                                ?: "צפייה ישירה בשידורי ${channel.name} באיכות HD.",
                            color = Color.White.copy(alpha = 0.78f),
                            fontSize = 12.sp,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }

                // Next Program Summary
                if (nextProg != null) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.03f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.06f)),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                        ) {
                            Text(
                                text = "התוכנית הבאה · ${nextProg.startTimeFormatted}",
                                color = Color.White.copy(alpha = 0.55f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Text(
                                text = nextProg.title,
                                color = Color.White.copy(alpha = 0.88f),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
            }

            // Primary Watch & Info Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(
                    onClick = { onPlay(channel) },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2563EB),
                        contentColor = Color.White,
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                ) {
                    Icon(Icons.Rounded.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("צפייה בשידור חי", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                GlassIconButton(
                    icon = Icons.Rounded.Info,
                    contentDescription = "פרטים",
                    onClick = { onInfo(channel) },
                    size = 44.dp,
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
            .padding(horizontal = 16.dp, vertical = 8.dp),
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

/** Compact end-action chip: label and the native M3 Switch in one tonal pill (keeps the overlay anchor callback). */
@Composable
internal fun IsraeliOnlyChip(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onPositioned: (Offset) -> Unit,
) {
    val scheme = MaterialTheme.colorScheme
    Row(
        Modifier
            .height(32.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(scheme.secondaryContainer.copy(alpha = 0.55f))
            .padding(start = 12.dp, end = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("רק ישראלי", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = scheme.onSecondaryContainer)
        Box(Modifier.size(width = 40.dp, height = 24.dp), contentAlignment = Alignment.Center) {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                modifier = Modifier.requiredSize(52.dp, 32.dp).scale(0.72f).onGloballyPositioned { onPositioned(it.boundsInRoot().center) },
            )
        }
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
    Box(modifier.padding(bottom = 8.dp)) {
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
            Modifier.align(Alignment.BottomCenter).padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            repeat(featured.size) { i ->
                val on = i == pager.currentPage
                val w by animateDpAsState(if (on) 32.dp else 8.dp, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMediumLow), label = "dot")
                Box(
                    Modifier
                        .height(8.dp)
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

    val player = remember(context, channel.id) {
        val reqProps = buildMap {
            put("Referer", "https://stmify.com/")
            channel.headers.forEach { (k, v) -> if (k.isNotBlank() && v.isNotBlank()) put(k, v) }
        }
        val http = DefaultHttpDataSource.Factory()
            .setUserAgent(channel.headers["User-Agent"] ?: USER_AGENT)
            .setDefaultRequestProperties(reqProps)
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
 * Computes dominant saturated ambient glow colors for a channel poster/stream,
 * preserving the signature crimson/red Hero tint overlay alongside dynamic accents.
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
 * High-quality static hero fallback backdrop with a dynamic ambient color glow and slight blur.
 */
@Composable
private fun HeroStaticFallbackPoster(
    ch: Channel,
    logo: String,
    topInset: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier,
) {
    val scheme = MaterialTheme.colorScheme
    val (glowA, glowB) = remember(ch.id, scheme.primary) { dominantHeroGlowColors(ch, scheme.primary) }
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
                    .padding(top = topInset * 0.45f)
                    .fillMaxWidth(0.85f)
                    .height(220.dp)
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
                            glowA.copy(alpha = 0.34f),
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
    val (glowPrimary, glowSecondary) = remember(ch.id, scheme.primary) { dominantHeroGlowColors(ch, scheme.primary) }
    val d = LocalDimens.current
    // 82% of the viewport, within 360-760 dp
    val heroHeight = HeroHeight
    val foregroundAlpha = (1f - (scrollOffsetPx / 260f)).coerceIn(0f, 1f)

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
            .height(heroHeight)
            .sharedContainer(key, HeroShape)
            .clip(HeroShape)
            .background(scheme.background)
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
            HeroStaticFallbackPoster(ch = ch, logo = logo, topInset = topInset)

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

        // Overall veil, then a dedicated fade into the feed background
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    colorStops = arrayOf(
                        0.00f to Color.Transparent,
                        0.40f to scheme.background.copy(alpha = 0.35f),
                        0.75f to scheme.background.copy(alpha = 0.85f),
                        1.00f to scheme.background,
                    )
                )
            )
        )
        Box(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(d.heroBottomFade)
                .background(Brush.verticalGradient(listOf(scheme.background.copy(alpha = 0f), scheme.background)))
        )
        // Strong scrim over the bottom 40% so logo and buttons never clash with the video
        Box(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.4f)
                .background(Brush.verticalGradient(listOf(Color.Transparent, scheme.background.copy(alpha = 0.4f), scheme.background.copy(alpha = 0.9f))))
        )

        Column(
            // Centred in the space below the glass header, with room for the page dots underneath
            Modifier
                .align(Alignment.Center)
                .widthIn(max = d.heroContentMaxWidth)
                .fillMaxWidth()
                .padding(top = topInset + 8.dp, start = d.heroContentPadding, end = d.heroContentPadding, bottom = 28.dp)
                .graphicsLayer { alpha = foregroundAlpha },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                Modifier.fillMaxWidth(0.72f).aspectRatio(2.6f),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    Modifier
                        .size(width = 180.dp, height = 80.dp)
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
                            .fillMaxSize(),
                        contentScale = ContentScale.Fit,
                    )
                } else {
                    Text(ch.name, style = MaterialTheme.typography.headlineMedium, color = scheme.onSurface, maxLines = 2)
                }
            }
            Spacer(Modifier.height(14.dp))
            HeroMetadata(quality, where)
            val liveProg = remember(ch.id) { CatchupManager.getCurrentAndNextProgram(ch.id).first }
            if (liveProg != null) {
                Spacer(Modifier.height(8.dp))
                Row(
                    Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color.White.copy(alpha = 0.08f))
                        .border(0.75.dp, Color.White.copy(alpha = 0.22f), RoundedCornerShape(50))
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Box(Modifier.size(6.dp).clip(CircleShape).background(Color(0xFFFF3B30)))
                    Text(
                        "${liveProg.startTimeFormatted} · ${liveProg.title}",
                        color = scheme.onSurface.copy(alpha = 0.94f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            if (logo.isEmpty()) {
                Spacer(Modifier.height(4.dp))
                Text(ch.name, style = MaterialTheme.typography.titleLarge, color = scheme.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Spacer(Modifier.height(14.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                GlassIconButton(
                    if (favorite) Icons.Rounded.Star else Icons.Rounded.StarBorder, "שמור", onFavorite, size = 48.dp,
                    tint = scheme.onSurface.copy(alpha = 0.14f), iconTint = if (favorite) Color(0xFFFFD54F) else scheme.onSurface,
                )
                val playInteraction = remember { MutableInteractionSource() }
                val playPressed by playInteraction.collectIsPressedAsState()
                val playCorner by animateDpAsState(
                    targetValue = if (playPressed) 16.dp else 40.dp,
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
                    contentPadding = PaddingValues(horizontal = 28.dp, vertical = 12.dp),
                    modifier = Modifier
                        .height(48.dp)
                        .pressPhysics(playInteraction, tilt = false, glowShape = playShape),
                ) {
                    Icon(Icons.Rounded.PlayArrow, null, Modifier.size(20.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("צפייה", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                GlassIconButton(
                    Icons.Rounded.Info, "מידע", { onInfo(ch) }, size = 48.dp,
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
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
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

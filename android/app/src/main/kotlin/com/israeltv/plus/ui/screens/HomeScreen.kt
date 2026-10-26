package com.israeltv.plus.ui.screens

import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.text.style.TextOverflow
import com.israeltv.plus.ui.components.LocalPlayOrigin
import com.israeltv.plus.ui.components.IsraeliOnlyZomatoOverlay
import com.israeltv.plus.ui.components.IsraeliOverlayMode
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
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalContext
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
import kotlinx.coroutines.delay
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView

private val HeroShape = RoundedCornerShape(0.dp)

/** Home: one master LazyColumn – the full-bleed hero, "continue watching", favourites, then a carousel per category. */
@Composable
fun HomeScreen(
    data: ChannelData?,
    prefs: AppPrefs,
    listState: LazyListState,
    logo: (Channel) -> String,
    subtitle: (Channel) -> String,
    onPlay: (Channel, List<Channel>) -> Unit,
    onInfo: (Channel) -> Unit,
    onSeeAll: (String) -> Unit,
) {
    val padding = feedPadding()
    var israeliOnly by remember { mutableStateOf(false) }
    var overlayMode by remember { mutableStateOf(IsraeliOverlayMode.Hidden) }
    var switchCenter by remember { mutableStateOf<Offset?>(null) }
    Box(Modifier.fillMaxSize()) {
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
                data.featured, prefs, logo,
                onPlay = { onPlay(it, data.featured) }, onInfo = onInfo,
                modifier = Modifier.bleedToTop(padding).cascadeIn(0),
            )
        }
        var row = 1
        val recent = prefs.recents.mapNotNull { data.byId[it] }
        if (recent.isNotEmpty()) {
            val i = row++
            item(key = "recent") { ChannelShelf("המשך צפייה", recent, logo, subtitle, { onPlay(it, recent) }, modifier = Modifier.cascadeIn(i)) }
        }
        val favs = prefs.favorites.mapNotNull { data.byId[it] }
        if (favs.isNotEmpty()) {
            val i = row++
            item(key = "favs") { ChannelShelf("המועדפים שלי", favs, logo, subtitle, { onPlay(it, favs) }, modifier = Modifier.cascadeIn(i)) }
        }
        item(key = "israeli-only-filter") {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("רק ישראלי", color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.titleMedium)
                Switch(
                    checked = israeliOnly,
                    onCheckedChange = { checked ->
                        israeliOnly = checked
                        overlayMode = if (checked) IsraeliOverlayMode.Entering else IsraeliOverlayMode.Exiting
                    },
                    modifier = Modifier.onGloballyPositioned { switchCenter = it.boundsInRoot().center },
                )
            }
        }
        val visibleCategories = if (israeliOnly) data.categories.filter { category -> category.channels.any { it.categoryEn == "israel" } } else data.categories
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
    IsraeliOnlyZomatoOverlay(
        mode = overlayMode,
        switchCenter = switchCenter,
        onDismiss = { overlayMode = if (overlayMode == IsraeliOverlayMode.Entering) IsraeliOverlayMode.Active else IsraeliOverlayMode.Hidden },
    )
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
    Box(modifier.padding(bottom = 22.dp)) {
        HorizontalPager(pager, key = { featured[it].id }) { page ->
            val ch = featured[page]
            HeroPage(ch, logo(ch), prefs.isFavorite(ch.id), playBackground = page == pager.currentPage, onPlay, onInfo) { prefs.toggleFavorite(ch.id) }
        }
        // capsule page indicator: the active dot stretches to 24 dp
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

/** Muted foregroundless Media3 HLS background; only the settled pager page owns a player. */
@Composable
private fun MutedHeroHls(url: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val player = remember(url) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(url))
            repeatMode = Player.REPEAT_MODE_ONE
            volume = 0f
            playWhenReady = true
            prepare()
        }
    }
    DisposableEffect(player) { onDispose { player.release() } }
    AndroidView(
        modifier = modifier,
        factory = { ctx -> PlayerView(ctx).apply {
            useController = false
            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
            setShutterBackgroundColor(android.graphics.Color.TRANSPARENT)
            this.player = player
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        } },
        update = { if (it.player !== player) it.player = player },
    )
}

/**
 * Apple TV+ hero, 580 dp, edge to edge: the channel's artwork as ambient light, a vignette into the background from
 * 35% down, and centred: the big logo on a soft light glow (dark logos like Kan 11 stay readable), "Live • category •
 * quality", the white "צפייה" pill between the favourite and info glass buttons.
 */
@Composable
private fun HeroPage(
    ch: Channel,
    logo: String,
    favorite: Boolean,
    playBackground: Boolean,
    onPlay: (Channel) -> Unit,
    onInfo: (Channel) -> Unit,
    onFavorite: () -> Unit,
) {
    val scheme = MaterialTheme.colorScheme
    val dark = LocalDarkTheme.current
    val haptic = LocalHapticFeedback.current
    val origin = LocalPlayOrigin.current
    val key = "hero:${ch.id}"
    Box(
        Modifier
            .fillMaxWidth()
            .height(580.dp)
            .sharedContainer(key, HeroShape)
            .clipToBounds()
            .background(Color.Black)
    ) {
        if (playBackground && ch.url.contains(".m3u8", ignoreCase = true)) {
            Box(Modifier.fillMaxWidth().aspectRatio(16f / 9f).align(Alignment.TopCenter)) {
                MutedHeroHls(ch.url, Modifier.fillMaxSize())
            }
        }
        // Cinematic dark scrim over the live picture, strongest below the clear logo/title area.
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(
            0f to Color.Black.copy(alpha = 0.26f),
            0.38f to Color.Black.copy(alpha = 0.38f),
            0.72f to scheme.surfaceContainerLowest.copy(alpha = 0.82f),
            1f to scheme.surfaceContainerLowest,
        )))
        Column(
            Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(horizontal = 24.dp).padding(bottom = 34.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(contentAlignment = Alignment.Center) {
                // light back-glow so dark logos pop
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
            Spacer(Modifier.height(18.dp))
            Text(
                ch.name, style = MaterialTheme.typography.headlineSmall, color = scheme.onSurface,
                maxLines = 1, overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(7.dp).clip(CircleShape).background(Color(0xFFFF3B30)))
                val meta = listOf("Live", shortCategory(ch.category), ch.badge.ifEmpty { if (ch.isRadio) "רדיו" else "" })
                    .filter { it.isNotEmpty() }.joinToString("  •  ")
                Text("  $meta", color = scheme.onSurfaceVariant, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(18.dp))
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
                    Text("צפייה", fontSize = 17.sp, fontWeight = FontWeight.Black)
                }
                GlassIconButton(
                    Icons.Rounded.Info, "מידע", { onInfo(ch) }, size = 50.dp,
                    tint = scheme.onSurface.copy(alpha = 0.14f), iconTint = scheme.onSurface,
                )
            }
        }
    }
}

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
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import com.israeltv.plus.ui.components.LocalPlayOrigin
import com.israeltv.plus.ui.components.IsraeliOnlyZomatoOverlay
import com.israeltv.plus.ui.components.IsraeliOverlayMode
import com.israeltv.plus.ui.components.MacroCategories
import com.israeltv.plus.ui.components.MacroCategoryRow
import com.israeltv.plus.ui.components.MoreCategoriesSheet
import com.israeltv.plus.ui.components.cascadeIn
import com.israeltv.plus.ui.components.sharedContainer
import com.israeltv.plus.ui.theme.LocalDarkTheme
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import com.israeltv.plus.ui.components.BoldIcon
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

/**
 * Home: one master LazyColumn - the full-bleed hero, the "רק ישראלי" switch, the centred macro categories, "continue
 * watching", favourites, then a carousel per category. Everything below the hero is normal feed content; the scroll
 * physics (fling, rubber-band overscroll) are untouched.
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
    onSeeAll: (String) -> Unit,
) {
    val padding = feedPadding()
    var israeliOnly by remember { mutableStateOf(false) }
    var overlayMode by remember { mutableStateOf(IsraeliOverlayMode.Hidden) }
    var switchCenter by remember { mutableStateOf<Offset?>(null) }
    var macro by rememberSaveable { mutableStateOf<String?>(null) }
    var moreOpen by remember { mutableStateOf(false) }

    val categories = data?.categories.orEmpty()
    val israeliCategories = remember(categories) {
        categories.filter { category -> category.channels.any { it.categoryEn == "israel" } }
    }
    val pool = if (israeliOnly) israeliCategories else categories
    val visibleCategories = if (macro == null) pool else pool.filter { it.name == macro }
    val restCategories = pool.filter { it.name !in MacroCategories }

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
            item(key = "israeli-only-filter") {
                IsraeliOnlySwitch(
                    checked = israeliOnly,
                    modifier = Modifier.cascadeIn(1),
                    onPositioned = { switchCenter = it },
                    onCheckedChange = { checked ->
                        israeliOnly = checked
                        macro = null
                        overlayMode = if (checked) IsraeliOverlayMode.Entering else IsraeliOverlayMode.Exiting
                    },
                )
            }
            item(key = "macro-categories") {
                MacroCategoryRow(
                    selected = macro,
                    onSelect = { macro = it },
                    onMore = { moreOpen = true },
                    modifier = Modifier.padding(top = 10.dp, bottom = 20.dp).cascadeIn(2),
                )
            }
            var row = 3
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
            if (visibleCategories.isEmpty()) {
                item(key = "empty") {
                    Text(
                        if (israeliOnly) "אין קטגוריה כזו בתוכן הישראלי" else "אין קטגוריה כזו",
                        Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 28.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp,
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

        IsraeliOnlyZomatoOverlay(
            mode = overlayMode,
            switchCenter = switchCenter,
            onDismiss = { overlayMode = if (overlayMode == IsraeliOverlayMode.Entering) IsraeliOverlayMode.Active else IsraeliOverlayMode.Hidden },
        )

        if (moreOpen) {
            MoreCategoriesSheet(
                categories = restCategories,
                selected = macro,
                onSelect = { macro = it; moreOpen = false },
                onDismiss = { moreOpen = false },
            )
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
 * "רק ישראלי" directly under the hero: a compact centred label and the Material 3 switch. Its measured centre is kept
 * so the Zomato overlay can fly its badge back into the switch when the filter is turned off.
 */
@Composable
private fun IsraeliOnlySwitch(
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
        Text("רק ישראלי", color = scheme.onSurface.copy(alpha = 0.9f), fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
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
    Box(modifier.padding(bottom = 10.dp)) {
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
 * The Kan Box / Apple TV+ hero: the live picture as ambient light, a soft vertical gradient that melts it into the
 * background, and centred - the channel logo on a light glow, one clean metadata line (HD • ערוצי ישראל • Live •) with
 * no pill backgrounds, then the white "צפייה" pill between the info and favourite glass buttons.
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
    val quality = ch.badge.ifBlank { if (ch.isRadio) "רדיו" else "HD" }
    val where = if (ch.categoryEn == "israel") "ערוצי ישראל" else shortCategory(ch.category)
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
                // Soft vertical gradient over the 16:9 video frame so its bottom edge melts into the dark hero canvas.
                Box(
                    Modifier.fillMaxSize().background(
                        Brush.verticalGradient(
                            0f to Color.Black.copy(alpha = 0.18f),
                            0.45f to Color.Transparent,
                            0.78f to Color.Black.copy(alpha = 0.65f),
                            1f to Color.Black,
                        )
                    )
                )
            }
        }
        // Cinematic dark scrim over the live picture, blending smoothly into the dark feed background.
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(
            0f to Color.Black.copy(alpha = 0.14f),
            0.34f to Color.Black.copy(alpha = 0.28f),
            0.62f to Color.Black.copy(alpha = 0.62f),
            0.82f to scheme.surfaceContainerLowest.copy(alpha = 0.92f),
            1f to scheme.surfaceContainerLowest,
        )))
        Column(
            Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(horizontal = 24.dp).padding(bottom = 26.dp),
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

/**
 * "HD • ערוצי ישראל • Live •": one text baseline with hairline separators and no pill behind it, laid out left to
 * right like the Kan Box reference. The live dot breathes softly.
 */
@Composable
private fun HeroMetadata(quality: String, where: String) {
    val scheme = MaterialTheme.colorScheme
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            HeroMetaText(quality)
            HeroMetaSeparator()
            HeroMetaText(where)
            HeroMetaSeparator()
            HeroMetaText("Live", dim = false)
            LivePulseDot()
        }
    }
}

@Composable
private fun HeroMetaText(text: String, dim: Boolean = true) {
    Text(
        text,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (dim) 0.72f else 0.98f),
        fontSize = 14.sp,
        fontWeight = if (dim) FontWeight.SemiBold else FontWeight.Bold,
        letterSpacing = 0.1.sp,
        maxLines = 1,
        style = TextStyle(textDirection = TextDirection.Ltr),
    )
}

@Composable
private fun HeroMetaSeparator() {
    Box(
        Modifier.padding(horizontal = 9.dp).size(3.dp).clip(CircleShape)
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
    )
}

@Composable
private fun LivePulseDot() {
    val pulse by rememberInfiniteTransition(label = "live").animateFloat(
        initialValue = 0.55f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Reverse),
        label = "livePulse",
    )
    Box(
        Modifier.padding(start = 7.dp).size(7.dp).graphicsLayer {
            alpha = pulse
            val s = 0.85f + pulse * 0.18f
            scaleX = s
            scaleY = s
        }.background(Color(0xFFFF3B30), CircleShape)
    )
}

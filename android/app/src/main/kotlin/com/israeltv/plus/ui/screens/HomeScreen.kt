package com.israeltv.plus.ui.screens

import android.os.Build
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
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
import kotlinx.coroutines.delay

private val HeroShape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)

/** Home: one master LazyColumn – the full-bleed hero, "continue watching", favourites, then a carousel per category. */
@Composable
fun HomeScreen(
    data: ChannelData?,
    prefs: AppPrefs,
    logo: (Channel) -> String,
    subtitle: (Channel) -> String,
    onPlay: (Channel, List<Channel>) -> Unit,
    onInfo: (Channel) -> Unit,
    onSeeAll: (String) -> Unit,
) {
    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = feedPadding(topHeaderSpace = false),
        flingBehavior = rememberBitChordFlingBehavior(),
    ) {
        if (data == null) {
            item { Box(Modifier.fillMaxWidth().height(580.dp).glassShimmer(HeroShape)) }
            items(4) { ShelfSkeleton() }
            return@LazyColumn
        }
        item(key = "hero") {
            HeroPager(data.featured, prefs, logo, onPlay = { onPlay(it, data.featured) }, onInfo = onInfo)
        }
        val recent = prefs.recents.mapNotNull { data.byId[it] }
        if (recent.isNotEmpty()) {
            item(key = "recent") { ChannelShelf("המשך צפייה", recent, logo, subtitle, { onPlay(it, recent) }) }
        }
        val favs = prefs.favorites.mapNotNull { data.byId[it] }
        if (favs.isNotEmpty()) {
            item(key = "favs") { ChannelShelf("המועדפים שלי", favs, logo, subtitle, { onPlay(it, favs) }) }
        }
        data.categories.forEach { cat ->
            item(key = "cat:" + cat.key) {
                ChannelShelf(
                    cat.name, cat.channels.take(24), logo, subtitle, { onPlay(it, cat.channels) },
                    count = cat.channels.size, onSeeAll = { onSeeAll(cat.key) },
                )
            }
        }
    }
}

/**
 * 100% Full-Bleed Top Hero Carousel (Apple TV+ Reference):
 * Bleeding directly behind status bar (height = 580.dp), centered cinematic layout,
 * bottom vignette, bold white full-pill CTA and animated capsule indicator dots.
 */
@Composable
private fun HeroPager(
    featured: List<Channel>,
    prefs: AppPrefs,
    logo: (Channel) -> String,
    onPlay: (Channel) -> Unit,
    onInfo: (Channel) -> Unit,
) {
    if (featured.isEmpty()) return
    val pager = rememberPagerState { featured.size }
    LaunchedEffect(pager.settledPage, pager.isScrollInProgress) {
        if (pager.isScrollInProgress) return@LaunchedEffect
        delay(7000)
        pager.animateScrollToPage((pager.currentPage + 1) % featured.size)
    }
    Column(Modifier.fillMaxWidth().padding(bottom = 28.dp)) {
        HorizontalPager(
            pager,
            contentPadding = PaddingValues(0.dp),
            pageSpacing = 0.dp,
            key = { featured[it].id },
            modifier = Modifier.fillMaxWidth().height(580.dp),
        ) { page ->
            HeroCard(featured[page], logo(featured[page]), prefs.isFavorite(featured[page].id), onPlay, onInfo) {
                prefs.toggleFavorite(featured[page].id)
            }
        }
        // Animated capsule page indicator dots below Hero
        Row(
            Modifier.fillMaxWidth().padding(top = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(featured.size) { i ->
                val on = i == pager.currentPage
                val dotWidth by animateDpAsState(if (on) 24.dp else 6.dp, label = "dotWidth")
                Box(
                    Modifier
                        .padding(horizontal = 3.dp)
                        .height(6.dp)
                        .width(dotWidth)
                        .clip(RoundedCornerShape(50))
                        .background(if (on) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f))
                )
            }
        }
    }
}

/**
 * 580 dp Full-Bleed Hero:
 * - Edge-to-edge artwork bleeding behind status bar
 * - Center-aligned massive channel logo with subtle light radial back-glow
 * - Centered metadata row (Live • ערוצי ישראל • FHD)
 * - Bold white full-pill CTA button ("צפייה", height = 50.dp, RoundedCornerShape(50))
 */
@Composable
private fun HeroCard(
    ch: Channel,
    logo: String,
    favorite: Boolean,
    onPlay: (Channel) -> Unit,
    onInfo: (Channel) -> Unit,
    onFavorite: () -> Unit,
) {
    val scheme = MaterialTheme.colorScheme
    val haptic = LocalHapticFeedback.current
    Box(
        Modifier
            .fillMaxWidth()
            .height(580.dp)
            .clip(HeroShape)
            .background(Brush.linearGradient(listOf(scheme.primaryContainer, scheme.surfaceContainerHigh, scheme.tertiaryContainer)))
    ) {
        // Artwork ambient blurred light in background
        if (logo.isNotEmpty()) {
            AsyncImage(
                logo, null,
                Modifier
                    .fillMaxSize()
                    .graphicsLayer { scaleX = 2.0f; scaleY = 2.0f; alpha = if (Build.VERSION.SDK_INT >= 31) 0.55f else 0.22f }
                    .blur(90.dp),
                contentScale = ContentScale.Crop,
            )
        }

        // Smooth bottom gradient vignette
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0.35f to Color.Transparent,
                        1.0f to scheme.surfaceContainerLowest,
                    )
                )
        )

        // Centered Cinematic Content Layout
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Center-aligned massive channel logo with subtle light radial back-glow
            if (logo.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .size(190.dp, 120.dp)
                        .drawBehind {
                            drawCircle(Color.White.copy(alpha = 0.22f), radius = size.minDimension * 0.65f)
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    AsyncImage(
                        logo, ch.name,
                        Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit,
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            // Metadata Row: LIVE • Category • FHD
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    "LIVE",
                    Modifier.clip(RoundedCornerShape(6.dp)).background(Color(0xFFE53935)).padding(horizontal = 8.dp, vertical = 2.dp),
                    color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold,
                )
                Text(
                    " • ${shortCategory(ch.category)} • ${if (ch.badge.isNotEmpty()) ch.badge else "FHD"}",
                    color = scheme.onSurfaceVariant, fontSize = 13.sp, fontWeight = FontWeight.Bold,
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(
                ch.name,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = scheme.onSurface,
                maxLines = 1,
            )

            Spacer(Modifier.height(20.dp))

            // CTA Buttons Row: Bold white full-pill CTA button ("צפייה", height = 50.dp) + favourite & info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Button(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                        onPlay(ch)
                    },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black,
                    ),
                    contentPadding = PaddingValues(horizontal = 34.dp),
                    modifier = Modifier.height(50.dp),
                ) {
                    Icon(Icons.Rounded.PlayArrow, null, Modifier.size(26.dp), tint = Color.Black)
                    Spacer(Modifier.width(6.dp))
                    Text("צפייה", fontSize = 17.sp, fontWeight = FontWeight.Black, color = Color.Black)
                }
                GlassIconButton(if (favorite) Icons.Rounded.Star else Icons.Rounded.StarBorder, "מועדף", onFavorite)
                GlassIconButton(Icons.Rounded.Info, "מידע", { onInfo(ch) })
            }
        }
    }
}

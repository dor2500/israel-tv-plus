package com.israeltv.plus.ui.screens

import android.os.Build
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
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
import com.israeltv.plus.ui.components.specularBorder
import kotlinx.coroutines.delay

private val HeroShape = RoundedCornerShape(32.dp)

/** Home: one master LazyColumn – the hero, "continue watching", favourites, then a carousel per category. */
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
        contentPadding = feedPadding(),
        flingBehavior = rememberBitChordFlingBehavior(),
    ) {
        if (data == null) {
            item { Box(Modifier.padding(horizontal = 16.dp).padding(bottom = 28.dp).fillMaxWidth().height(380.dp).glassShimmer(HeroShape)) }
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
    Column(Modifier.padding(bottom = 28.dp)) {
        HorizontalPager(
            pager,
            contentPadding = PaddingValues(horizontal = 16.dp),
            pageSpacing = 12.dp,
            key = { featured[it].id },
        ) { page ->
            HeroCard(featured[page], logo(featured[page]), prefs.isFavorite(featured[page].id), onPlay, onInfo) {
                prefs.toggleFavorite(featured[page].id)
            }
        }
        Row(
            Modifier.fillMaxWidth().padding(top = 12.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            repeat(featured.size) { i ->
                val on = i == pager.currentPage
                Box(
                    Modifier
                        .padding(horizontal = 3.dp)
                        .height(6.dp)
                        .width(if (on) 22.dp else 6.dp)
                        .clip(RoundedCornerShape(50))
                        .background(if (on) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f))
                )
            }
        }
    }
}

/** 380 dp hero: the channel artwork edge to edge, a bottom-up scrim, "צפייה" pill + favourite / info glass buttons. */
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
            .height(380.dp)
            .clip(HeroShape)
            .background(Brush.linearGradient(listOf(scheme.primaryContainer, scheme.surfaceContainerHigh, scheme.tertiaryContainer)))
            .specularBorder(HeroShape)
    ) {
        // artwork: the logo, huge and blurred as ambient light (Android 12+), sharp in front
        if (logo.isNotEmpty()) {
            AsyncImage(
                logo, null,
                Modifier
                    .fillMaxSize()
                    .graphicsLayer { scaleX = 1.8f; scaleY = 1.8f; alpha = if (Build.VERSION.SDK_INT >= 31) 0.55f else 0.18f }
                    .blur(70.dp),
                contentScale = ContentScale.Crop,
            )
            AsyncImage(
                logo, ch.name,
                Modifier.align(Alignment.TopCenter).padding(top = 52.dp).fillMaxWidth(0.52f).height(150.dp),
                contentScale = ContentScale.Fit,
            )
        }
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0.35f to Color.Transparent,
                        1f to scheme.surfaceContainerLowest.copy(alpha = 0.95f),
                    )
                )
        )
        Column(Modifier.align(Alignment.BottomStart).fillMaxWidth().padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "LIVE",
                    Modifier.clip(RoundedCornerShape(6.dp)).background(Color(0xFFE53935)).padding(horizontal = 7.dp, vertical = 1.dp),
                    color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold,
                )
                Text(
                    "  ${shortCategory(ch.category)}${if (ch.badge.isNotEmpty()) " · " + ch.badge else ""}",
                    color = scheme.onSurfaceVariant, fontSize = 13.sp, fontWeight = FontWeight.Medium,
                )
            }
            Text(ch.name, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold, color = scheme.onSurface, maxLines = 1)
            Spacer(Modifier.height(14.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                        onPlay(ch)
                    },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = scheme.primaryContainer, contentColor = scheme.onPrimaryContainer),
                    contentPadding = PaddingValues(horizontal = 28.dp),
                    modifier = Modifier.height(52.dp),
                ) {
                    Icon(Icons.Rounded.PlayArrow, null, Modifier.size(26.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("צפייה", fontSize = 17.sp, fontWeight = FontWeight.ExtraBold)
                }
                GlassIconButton(if (favorite) Icons.Rounded.Star else Icons.Rounded.StarBorder, "מועדף", onFavorite)
                GlassIconButton(Icons.Rounded.Info, "מידע", { onInfo(ch) })
            }
        }
    }
}

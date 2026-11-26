package com.israeltv.plus.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.ui.platform.LocalContext
import com.israeltv.plus.data.ISRAELI_VOD_PORTALS
import com.israeltv.plus.data.VOD_PORTAL_CATEGORIES
import com.israeltv.plus.data.VodPortal
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.israeltv.plus.data.Channel
import com.israeltv.plus.data.ChannelData
import com.israeltv.plus.data.VodEpisode
import com.israeltv.plus.data.VodSeries
import com.israeltv.plus.data.resolveVodImage
import com.israeltv.plus.ui.components.PosterAspect
import com.israeltv.plus.ui.components.SectionHeader
import com.israeltv.plus.ui.components.VodCardShape
import com.israeltv.plus.ui.components.VodSeriesCard
import com.israeltv.plus.ui.components.VodSeriesSheet
import com.israeltv.plus.ui.components.feedPadding
import com.israeltv.plus.ui.components.rememberBitChordFlingBehavior

private val Rtl = androidx.compose.ui.text.TextStyle(
    textDirection = TextDirection.Rtl,
    textAlign = TextAlign.Start
)

val VodGenres = listOf(
    "הכל",
    "קשת 12",
    "רשת 13",
    "כאן 11",
    "עכשיו 14",
    "HOT & yes",
    "ילדים ונוער",
    "קומדיה",
    "דרמה",
    "מתח",
    "סאטירה",
    "דוקו-ריאליטי",
    "נוסטלגיה וקאלט",
    "אוכל ובישול",
    "ויוה וסדרות"
)

@Composable
fun VodScreen(
    data: ChannelData?,
    onPlay: (Channel, List<Channel>) -> Unit,
    modifier: Modifier = Modifier,
) {
    val seriesList = data?.vod.orEmpty()
    var selectedGenre by remember { mutableStateOf("הכל") }
    var selectedPortalCat by remember { mutableStateOf("הכל") }
    var inspectingSeries by remember { mutableStateOf<VodSeries?>(null) }
    val haptic = LocalHapticFeedback.current
    val ctx = LocalContext.current

    val filteredList = remember(seriesList, selectedGenre) {
        if (selectedGenre == "הכל") seriesList
        else seriesList.filter { s -> s.genres.any { it.contains(selectedGenre, ignoreCase = true) } }
    }
    val filteredPortals = remember(selectedPortalCat) {
        when (selectedPortalCat) {
            "הכל" -> ISRAELI_VOD_PORTALS
            "חינם" -> ISRAELI_VOD_PORTALS.filter { it.isFree }
            else -> ISRAELI_VOD_PORTALS.filter { it.category == selectedPortalCat }
        }
    }

    val hero = remember(seriesList) { seriesList.firstOrNull() }

    Box(modifier.fillMaxSize()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = feedPadding(withCategories = false),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            flingBehavior = rememberBitChordFlingBehavior(),
        ) {
            // Hero Featured Series
            if (hero != null) {
                item(span = { GridItemSpan(maxLineSpan) }, key = "vod_hero") {
                    VodHeroBanner(
                        series = hero,
                        onPlayClick = {
                            hero.episodes.firstOrNull()?.let { ep ->
                                onPlay(ep.toChannel(hero), hero.episodes.map { it.toChannel(hero) })
                            }
                        },
                        onDetailsClick = { inspectingSeries = hero }
                    )
                }
            }

            // Genre Filter Bar
            item(span = { GridItemSpan(maxLineSpan) }, key = "vod_genres") {
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(VodGenres) { genre ->
                        val isSelected = genre == selectedGenre
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) MaterialTheme.colorScheme.primary
                                    else Color.White.copy(alpha = 0.12f),
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.SegmentTick)
                                    selectedGenre = genre
                                }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                genre,
                                style = Rtl,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Section Header
            item(span = { GridItemSpan(maxLineSpan) }, key = "vod_title") {
                SectionHeader("סדרות לצפייה ישירה", count = filteredList.size)
            }

            // Series Grid Items
            items(filteredList, key = { it.id }) { s ->
                VodSeriesCard(
                    series = s,
                    onClick = { inspectingSeries = s },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // All Israeli & International VOD Services Hub
            item(span = { GridItemSpan(maxLineSpan) }, key = "vod_portals_title") {
                Column(Modifier.padding(top = 14.dp)) {
                    SectionHeader("פלטפורמות ושירותי VOD", count = filteredPortals.size)
                    LazyRow(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        items(VOD_PORTAL_CATEGORIES) { cat ->
                            val isSelected = cat == selectedPortalCat
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) MaterialTheme.colorScheme.primary
                                        else Color.White.copy(alpha = 0.12f),
                                        RoundedCornerShape(20.dp)
                                    )
                                    .clickable {
                                        haptic.performHapticFeedback(HapticFeedbackType.SegmentTick)
                                        selectedPortalCat = cat
                                    }
                                    .padding(horizontal = 14.dp, vertical = 7.dp)
                            ) {
                                Text(
                                    cat,
                                    style = Rtl,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 12.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            items(filteredPortals, key = { it.id }) { portal ->
                VodPortalCard(
                    portal = portal,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                        runCatching {
                            ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(portal.url)))
                        }
                    },
                )
            }
        }

        // Details Bottom Sheet
        inspectingSeries?.let { series ->
            VodSeriesSheet(
                series = series,
                onPlayEpisode = { ep ->
                    val ch = ep.toChannel(series)
                    val playlist = series.episodes.map { it.toChannel(series) }
                    onPlay(ch, playlist)
                    inspectingSeries = null
                },
                onDismiss = { inspectingSeries = null }
            )
        }
    }
}

@Composable
private fun VodPortalCard(
    portal: VodPortal,
    onClick: () -> Unit,
) {
    val scheme = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.06f))
            .border(1.dp, Color.White.copy(alpha = 0.13f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (portal.isFree) Color(0xFF10B981).copy(alpha = 0.22f) else scheme.primary.copy(alpha = 0.22f))
                    .border(
                        0.75.dp,
                        if (portal.isFree) Color(0xFF34D399).copy(alpha = 0.55f) else scheme.primary.copy(alpha = 0.5f),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 7.dp, vertical = 2.dp)
            ) {
                Text(
                    portal.badge,
                    color = if (portal.isFree) Color(0xFF6EE7B7) else Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                )
            }
            Icon(
                Icons.AutoMirrored.Rounded.OpenInNew,
                contentDescription = "פתח",
                tint = Color.White.copy(alpha = 0.65f),
                modifier = Modifier.size(16.dp),
            )
        }
        Text(
            portal.name,
            style = Rtl,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            portal.desc,
            style = Rtl,
            color = Color.White.copy(alpha = 0.68f),
            fontSize = 11.5.sp,
            lineHeight = 15.sp,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun VodHeroBanner(
    series: VodSeries,
    onPlayClick: () -> Unit,
    onDetailsClick: () -> Unit,
) {
    val haptic = LocalHapticFeedback.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp)
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
            .clickable { onDetailsClick() }
    ) {
        // Backdrop Image
        val backdropUrl = resolveVodImage(series.backdrop.ifEmpty { series.poster })
        if (backdropUrl.isNotEmpty()) {
            AsyncImage(
                model = backdropUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        }

        // Gradient Overlay
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.5f),
                            Color.Black.copy(alpha = 0.94f)
                        )
                    )
                )
        )

        // Content
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            // Badges
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(bottom = 6.dp)
            ) {
                Text(
                    "סדרת השבוע ב-VOD",
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(horizontal = 7.dp, vertical = 2.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )

                if (series.rating.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.Black.copy(alpha = 0.6f))
                            .border(0.75.dp, Color(0xFFFFD54F).copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(Icons.Rounded.Star, contentDescription = null, tint = Color(0xFFFFD54F), modifier = Modifier.size(11.dp))
                        Text(series.rating, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Text(
                series.title,
                style = Rtl,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
            )

            Text(
                series.desc,
                style = Rtl,
                color = Color.White.copy(alpha = 0.82f),
                fontSize = 12.sp,
                lineHeight = 16.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 2.dp, bottom = 10.dp)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.VirtualKey)
                        onPlayClick()
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(Icons.Rounded.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("צפה בפרק 1", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.SegmentTick)
                        onDetailsClick()
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(alpha = 0.16f),
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("כל הפרקים (${series.episodes.size})", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}


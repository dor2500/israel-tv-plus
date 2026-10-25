package com.israeltv.plus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Radio
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.israeltv.plus.data.Channel

import androidx.compose.ui.graphics.Brush

val CardShape = RoundedCornerShape(22.dp)

val RickAndMortyArtwork = listOf(
    "https://rickandmortyapi.com/api/character/avatar/1.jpeg",   // Rick Sanchez
    "https://rickandmortyapi.com/api/character/avatar/2.jpeg",   // Morty Smith
    "https://rickandmortyapi.com/api/character/avatar/3.jpeg",   // Summer Smith
    "https://rickandmortyapi.com/api/character/avatar/4.jpeg",   // Beth Smith
    "https://rickandmortyapi.com/api/character/avatar/5.jpeg",   // Jerry Smith
    "https://rickandmortyapi.com/api/character/avatar/242.jpeg", // Mr. Poopybutthole
    "https://rickandmortyapi.com/api/character/avatar/244.jpeg", // Mr. Meeseeks
    "https://rickandmortyapi.com/api/character/avatar/118.jpeg", // Evil Morty
    "https://rickandmortyapi.com/api/character/avatar/265.jpeg", // Pickle Rick
    "https://rickandmortyapi.com/api/character/avatar/180.jpeg", // Birdperson
)

fun rickAndMortyPlaceholder(id: String): String {
    val idx = (id.hashCode() and 0x7FFFFFFF) % RickAndMortyArtwork.size
    return RickAndMortyArtwork[idx]
}

/** The shared-element key of the card that was tapped last: the player grows out of it and shrinks back into it. */
@Stable
class PlayOrigin {
    var key: String? = null
}

val LocalPlayOrigin = staticCompositionLocalOf { PlayOrigin() }

/** Labels are always right-aligned (Hebrew), also when the channel name is Latin. */
private val Rtl = TextStyle(textDirection = TextDirection.Rtl, textAlign = TextAlign.Start)

@Composable
fun ChannelLogo(
    url: String,
    modifier: Modifier = Modifier,
    isRadio: Boolean = false,
    channelId: String = "",
    contentScale: ContentScale = ContentScale.Fit,
) {
    if (isRadio) {
        Box(modifier, contentAlignment = Alignment.Center) {
            Icon(Icons.Rounded.Radio, null, Modifier.size(36.dp), tint = MaterialTheme.colorScheme.primary)
        }
    } else if (url.isNotEmpty()) {
        AsyncImage(
            model = url,
            contentDescription = null,
            modifier = modifier,
            contentScale = contentScale,
        )
    } else {
        // Rick and Morty animated artwork placeholder (No human faces)
        AsyncImage(
            model = rickAndMortyPlaceholder(channelId),
            contentDescription = null,
            modifier = modifier,
            contentScale = ContentScale.Crop,
        )
    }
}

/**
 * Landscape Card (16:9 wide card, width = 240.dp, height = 135.dp):
 * Exclusively for 'Continue Watching' (המשך צפייה) row.
 * Glows with dynamic tonal radial gradient (primary 0.15f) behind artwork.
 */
@Composable
fun LandscapeCard(
    channel: Channel,
    logo: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    sharedKey: String? = null,
) {
    val origin = LocalPlayOrigin.current
    val interaction = remember { MutableInteractionSource() }
    val haptic = LocalHapticFeedback.current
    val scheme = MaterialTheme.colorScheme

    Column(modifier.width(240.dp)) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(135.dp)
                .tactileBounce(interaction) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    origin.key = sharedKey
                    onClick()
                }
                .then(if (sharedKey != null) Modifier.sharedContainer(sharedKey, CardShape) else Modifier)
                .clip(CardShape)
                .background(scheme.surfaceContainerHigh.copy(alpha = 0.75f))
                .specularBorder(CardShape, alpha = 0.28f),
            contentAlignment = Alignment.Center,
        ) {
            // Subtle glowing radial gradient behind artwork
            Box(
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            listOf(scheme.primary.copy(alpha = 0.15f), Color.Transparent),
                            radius = 240f,
                        )
                    )
            )

            ChannelLogo(
                url = logo,
                channelId = channel.id,
                modifier = Modifier.fillMaxSize(0.68f),
                isRadio = channel.isRadio,
                contentScale = ContentScale.Fit,
            )

            val badge = if (channel.isRadio) "רדיו" else channel.badge
            if (badge.isNotEmpty()) {
                Text(
                    badge,
                    Modifier
                        .align(Alignment.TopStart)
                        .padding(9.dp)
                        .clip(RoundedCornerShape(50))
                        .background(scheme.tertiaryContainer)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                        .graphicsLayer { scaleY = 1.15f },
                    color = scheme.onTertiaryContainer,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    style = com.israeltv.plus.ui.theme.StretchedNumberStyle,
                )
            }
            Box(
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(9.dp)
                    .clip(RoundedCornerShape(50))
                    .background(scheme.tertiaryContainer)
                    .padding(horizontal = 7.dp, vertical = 2.dp),
            ) {
                Text(
                    channel.number.toString(),
                    color = scheme.onTertiaryContainer,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    style = com.israeltv.plus.ui.theme.StretchedNumberStyle,
                    modifier = Modifier.graphicsLayer { scaleY = 1.15f },
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            channel.name,
            Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            style = Rtl,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = scheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            subtitle,
            Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            style = Rtl,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = scheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/**
 * Portrait Poster Card (2:3 tall poster card, width = 130.dp, height = 195.dp):
 * For all category rows (Live TV, Movies, Favorites, etc.).
 * Subtle radial gradient behind animated artwork tile.
 */
@Composable
fun PortraitPosterCard(
    channel: Channel,
    logo: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    sharedKey: String? = null,
) {
    val origin = LocalPlayOrigin.current
    val interaction = remember { MutableInteractionSource() }
    val haptic = LocalHapticFeedback.current
    val scheme = MaterialTheme.colorScheme

    Column(modifier.width(130.dp)) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(195.dp)
                .tactileBounce(interaction) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    origin.key = sharedKey
                    onClick()
                }
                .then(if (sharedKey != null) Modifier.sharedContainer(sharedKey, CardShape) else Modifier)
                .clip(CardShape)
                .background(scheme.surfaceContainerHigh.copy(alpha = 0.75f))
                .specularBorder(CardShape, alpha = 0.28f),
            contentAlignment = Alignment.Center,
        ) {
            // Subtle glowing radial gradient behind artwork
            Box(
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            listOf(scheme.primary.copy(alpha = 0.15f), Color.Transparent),
                            radius = 200f,
                        )
                    )
            )

            ChannelLogo(
                url = logo,
                channelId = channel.id,
                modifier = Modifier.fillMaxSize(0.72f),
                isRadio = channel.isRadio,
                contentScale = ContentScale.Fit,
            )

            val badge = if (channel.isRadio) "רדיו" else channel.badge
            if (badge.isNotEmpty()) {
                Text(
                    badge,
                    Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(50))
                        .background(scheme.tertiaryContainer)
                        .padding(horizontal = 7.dp, vertical = 2.dp)
                        .graphicsLayer { scaleY = 1.15f },
                    color = scheme.onTertiaryContainer,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    style = com.israeltv.plus.ui.theme.StretchedNumberStyle,
                )
            }
            Box(
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(50))
                    .background(scheme.tertiaryContainer)
                    .padding(horizontal = 6.dp, vertical = 2.dp),
            ) {
                Text(
                    channel.number.toString(),
                    color = scheme.onTertiaryContainer,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    style = com.israeltv.plus.ui.theme.StretchedNumberStyle,
                    modifier = Modifier.graphicsLayer { scaleY = 1.15f },
                )
            }
        }
        Spacer(Modifier.height(7.dp))
        Text(
            channel.name,
            Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            style = Rtl,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = scheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            subtitle,
            Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            style = Rtl,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = scheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/** Legacy/Grid ChannelCard: defaults to standard proportional tile with Rick & Morty placeholder. */
@Composable
fun ChannelCard(
    channel: Channel,
    logo: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    sharedKey: String? = null,
) {
    PortraitPosterCard(
        channel = channel,
        logo = logo,
        subtitle = subtitle,
        onClick = onClick,
        modifier = modifier,
        sharedKey = sharedKey,
    )
}

/** Section title (20 sp extra bold, no emoji) with a tonal "הכל" pill. */
@Composable
fun SectionHeader(title: String, count: Int? = null, onSeeAll: (() -> Unit)? = null) {
    val scheme = MaterialTheme.colorScheme
    val haptic = LocalHapticFeedback.current
    Row(
        Modifier.fillMaxWidth().padding(start = 20.dp, end = 16.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(title, Modifier.weight(1f), fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = scheme.onSurface)
        if (onSeeAll != null) {
            Row(
                Modifier
                    .clip(RoundedCornerShape(50))
                    .background(scheme.secondaryContainer.copy(alpha = 0.55f))
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.SegmentTick)
                        onSeeAll()
                    }
                    .padding(start = 14.dp, end = 8.dp, top = 6.dp, bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    if (count != null) "הכל · $count" else "הכל",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = scheme.onSecondaryContainer,
                )
                Icon(
                    Icons.AutoMirrored.Rounded.KeyboardArrowRight, null,
                    Modifier.size(18.dp), tint = scheme.onSecondaryContainer,
                )
            }
        }
    }
}

/**
 * A horizontal carousel of channel cards.
 * If [isWide] is true, renders wide 16:9 cards (Continue Watching: 240.dp x 135.dp).
 * Otherwise renders tall 2:3 poster cards (130.dp x 195.dp).
 */
@Composable
fun ChannelShelf(
    title: String,
    channels: List<Channel>,
    logo: (Channel) -> String,
    subtitle: (Channel) -> String,
    onPlay: (Channel) -> Unit,
    count: Int? = null,
    isWide: Boolean = false,
    onSeeAll: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Column(modifier.padding(bottom = 26.dp)) {
        SectionHeader(title, count, onSeeAll)
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            flingBehavior = rememberBitChordFlingBehavior(),
        ) {
            items(channels, key = { it.id }) { ch ->
                if (isWide) {
                    LandscapeCard(
                        channel = ch,
                        logo = logo(ch),
                        subtitle = subtitle(ch),
                        onClick = { onPlay(ch) },
                        sharedKey = "$title:${ch.id}",
                    )
                } else {
                    PortraitPosterCard(
                        channel = ch,
                        logo = logo(ch),
                        subtitle = subtitle(ch),
                        onClick = { onPlay(ch) },
                        sharedKey = "$title:${ch.id}",
                    )
                }
            }
        }
    }
}

/** Loading placeholders in the shape of a shelf, with glowing tonal shimmer (no solid black). */
@Composable
fun ShelfSkeleton(isWide: Boolean = false) {
    Column(Modifier.padding(bottom = 26.dp)) {
        Box(Modifier.padding(start = 20.dp, bottom = 14.dp).width(140.dp).height(22.dp).glassShimmer(RoundedCornerShape(11.dp)))
        Row(Modifier.padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            repeat(3) {
                if (isWide) {
                    Column(Modifier.width(240.dp)) {
                        Box(Modifier.fillMaxWidth().height(135.dp).glassShimmer(CardShape))
                        Spacer(Modifier.height(10.dp))
                        Box(Modifier.width(130.dp).height(14.dp).glassShimmer(RoundedCornerShape(7.dp)))
                    }
                } else {
                    Column(Modifier.width(130.dp)) {
                        Box(Modifier.fillMaxWidth().height(195.dp).glassShimmer(CardShape))
                        Spacer(Modifier.height(10.dp))
                        Box(Modifier.width(90.dp).height(14.dp).glassShimmer(RoundedCornerShape(7.dp)))
                    }
                }
            }
        }
    }
}

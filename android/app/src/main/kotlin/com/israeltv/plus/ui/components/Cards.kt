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

val CardShape = RoundedCornerShape(26.dp)

/** The shared-element key of the card that was tapped last: the player grows out of it and shrinks back into it. */
@Stable
class PlayOrigin {
    var key: String? = null
}

val LocalPlayOrigin = staticCompositionLocalOf { PlayOrigin() }

/** Labels are always right-aligned (Hebrew), also when the channel name is Latin. */
private val Rtl = TextStyle(textDirection = TextDirection.Rtl, textAlign = TextAlign.Start)

@Composable
fun ChannelLogo(url: String, modifier: Modifier = Modifier, isRadio: Boolean = false) {
    if (url.isEmpty()) {
        Box(modifier, contentAlignment = Alignment.Center) {
            if (isRadio) Icon(Icons.Rounded.Radio, null, Modifier.size(36.dp), tint = MaterialTheme.colorScheme.primary)
        }
    } else {
        AsyncImage(model = url, contentDescription = null, modifier = modifier, contentScale = ContentScale.Fit)
    }
}

/**
 * A channel: 16:9.5 frosted tile (26 dp corners, surfaceContainerHigh at 75%, specular edge) with the logo, the
 * quality badge and the channel number; title (15 sp bold) and subtitle (12 sp medium) under it.
 */
@Composable
fun ChannelCard(
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
    Column(modifier) {
        Box(
            Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9.5f)
                .pressPhysics(interaction)
                .then(if (sharedKey != null) Modifier.sharedContainer(sharedKey, CardShape) else Modifier)
                .clip(CardShape)
                .background(scheme.surfaceContainerHigh.copy(alpha = 0.75f))
                .specularBorder(CardShape)
                .clickable(interaction, ripple()) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    origin.key = sharedKey
                    onClick()
                },
            contentAlignment = Alignment.Center,
        ) {
            ChannelLogo(logo, Modifier.fillMaxSize(0.6f), channel.isRadio)
            val badge = if (channel.isRadio) "רדיו" else channel.badge
            if (badge.isNotEmpty()) {
                Text(
                    badge,
                    Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(50))
                        .background(scheme.tertiaryContainer)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                        .graphicsLayer { scaleY = 1.18f },
                    color = scheme.onTertiaryContainer,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    style = com.israeltv.plus.ui.theme.StretchedNumberStyle,
                )
            }
            Box(
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(50))
                    .background(scheme.tertiaryContainer)
                    .padding(horizontal = 7.dp, vertical = 2.dp),
            ) {
                Text(
                    channel.number.toString(),
                    color = scheme.onTertiaryContainer,
                    fontSize = 11.sp,
                    fontWeight = FontWeight(800),
                    style = com.israeltv.plus.ui.theme.StretchedNumberStyle,
                    modifier = Modifier.graphicsLayer { scaleY = 1.18f },
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

/** A horizontal carousel of channel cards. */
@Composable
fun ChannelShelf(
    title: String,
    channels: List<Channel>,
    logo: (Channel) -> String,
    subtitle: (Channel) -> String,
    onPlay: (Channel) -> Unit,
    count: Int? = null,
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
                ChannelCard(ch, logo(ch), subtitle(ch), { onPlay(ch) }, Modifier.width(184.dp), sharedKey = "$title:${ch.id}")
            }
        }
    }
}

/** Loading placeholders in the shape of a shelf. */
@Composable
fun ShelfSkeleton() {
    Column(Modifier.padding(bottom = 26.dp)) {
        Box(Modifier.padding(start = 20.dp, bottom = 14.dp).width(140.dp).height(22.dp).glassShimmer(RoundedCornerShape(11.dp)))
        Row(Modifier.padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            repeat(3) {
                Column(Modifier.width(184.dp)) {
                    Box(Modifier.fillMaxWidth().aspectRatio(16f / 9.5f).glassShimmer(CardShape))
                    Spacer(Modifier.height(10.dp))
                    Box(Modifier.width(110.dp).height(14.dp).glassShimmer(RoundedCornerShape(7.dp)))
                }
            }
        }
    }
}

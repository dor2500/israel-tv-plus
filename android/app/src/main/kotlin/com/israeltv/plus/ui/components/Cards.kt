package com.israeltv.plus.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Radio
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
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

/**
 * What a card's long-press menu can do. Provided once by the app (it owns the profile and the info sheet), so every card
 * - the home shelves, the channel grid, the search results and My List - opens the same menu without threading callbacks
 * through every screen.
 */
@Stable
class CardMenuActions(
    val isFavorite: (Channel) -> Boolean = { false },
    val toggleFavorite: (Channel) -> Unit = {},
    val onInfo: (Channel) -> Unit = {},
)

val LocalCardMenu = staticCompositionLocalOf { CardMenuActions() }

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
    val menu = LocalCardMenu.current
    val interaction = remember { MutableInteractionSource() }
    val haptic = LocalHapticFeedback.current
    val scheme = MaterialTheme.colorScheme
    var menuOpen by remember { mutableStateOf(false) }
    val favorite = menu.isFavorite(channel)
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
                .combinedClickable(
                    interactionSource = interaction,
                    indication = ripple(),
                    onLongClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        menuOpen = true
                    },
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                        origin.key = sharedKey
                        onClick()
                    },
                ),
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
                        .background(Color.Black.copy(alpha = 0.45f))
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            Text(
                channel.number.toString(),
                Modifier.align(Alignment.TopEnd).padding(10.dp),
                color = scheme.onSurfaceVariant.copy(alpha = 0.8f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
            )
            if (favorite) {
                BoldIcon(
                    Icons.Rounded.Star, null,
                    Modifier.align(Alignment.BottomEnd).padding(10.dp).size(16.dp),
                    tint = Color(0xFFFFD54F), weight = 0.45.dp,
                )
            }
            NuvioCardMenu(
                expanded = menuOpen,
                channel = channel,
                logo = logo,
                subtitle = subtitle,
                favorite = favorite,
                onDismiss = { menuOpen = false },
                onFavorite = {
                    menuOpen = false
                    menu.toggleFavorite(channel)
                },
                onInfo = {
                    menuOpen = false
                    menu.onInfo(channel)
                },
            )
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
 * The Nuvio long-press card menu: a heavy-glass popup anchored on the poster with the channel's identity on top and the
 * two actions the feed supports - favourite (star, filled once saved) and the channel's details. It is the same menu on
 * every card in the app because [LocalCardMenu] carries the actions. Opening is tactile (the card's press physics ticks);
 * choosing an action confirms with a haptic.
 */
@Composable
private fun NuvioCardMenu(
    expanded: Boolean,
    channel: Channel,
    logo: String,
    subtitle: String,
    favorite: Boolean,
    onDismiss: () -> Unit,
    onFavorite: () -> Unit,
    onInfo: () -> Unit,
) {
    val scheme = MaterialTheme.colorScheme
    val haptic = LocalHapticFeedback.current
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        modifier = Modifier.width(252.dp),
        offset = DpOffset(0.dp, 4.dp),
        shape = RoundedCornerShape(24.dp),
        containerColor = Color(0xFF16161E).copy(alpha = 0.96f),
        tonalElevation = 0.dp,
        shadowElevation = 24.dp,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
    ) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier.size(width = 46.dp, height = 28.dp).clip(RoundedCornerShape(8.dp))
                    .background(Color.White.copy(alpha = 0.06f)),
                contentAlignment = Alignment.Center,
            ) {
                ChannelLogo(logo, Modifier.size(width = 38.dp, height = 22.dp), channel.isRadio)
            }
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    channel.name, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold,
                    maxLines = 1, overflow = TextOverflow.Ellipsis,
                )
                Text(
                    subtitle, color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp,
                    maxLines = 1, overflow = TextOverflow.Ellipsis,
                )
            }
        }
        HorizontalDivider(color = Color.White.copy(alpha = 0.08f))
        CardMenuRow(
            if (favorite) Icons.Rounded.Star else Icons.Rounded.StarBorder,
            if (favorite) "הסר מהמועדפים" else "הוסף למועדפים",
            tint = if (favorite) Color(0xFFFFD54F) else Color.White,
        ) {
            haptic.performHapticFeedback(HapticFeedbackType.Confirm)
            onFavorite()
        }
        CardMenuRow(Icons.Rounded.Info, "פרטי הערוץ") {
            haptic.performHapticFeedback(HapticFeedbackType.Confirm)
            onInfo()
        }
        Spacer(Modifier.height(4.dp))
    }
}

@Composable
private fun CardMenuRow(icon: ImageVector, label: String, tint: Color = Color.White, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BoldIcon(icon, null, Modifier.size(20.dp), tint = tint, weight = 0.55.dp)
        Spacer(Modifier.width(12.dp))
        Text(label, color = tint, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
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


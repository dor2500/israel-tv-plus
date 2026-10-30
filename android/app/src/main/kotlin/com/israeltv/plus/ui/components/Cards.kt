package com.israeltv.plus.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Radio
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Brush
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
import com.israeltv.plus.ui.theme.LocalDimens
import kotlin.math.abs

const val PosterAspect = 2f / 3f

val CardShape = RoundedCornerShape(16.dp)
private val ContinueCardShape = RoundedCornerShape(16.dp)

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
 * The vertical 2:3 channel poster: a dark translucent plate with the logo centred on it, title and metadata below,
 * spring press/focus physics (no Android ripple) and the long-press context menu.
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
                .aspectRatio(PosterAspect)
                .pressPhysics(interaction, glowShape = CardShape)
                .then(if (sharedKey != null) Modifier.sharedContainer(sharedKey, CardShape) else Modifier)
                .frostedGlass(CardShape, Color.White.copy(alpha = 0.05f), blur = 24.dp)
                .combinedClickable(
                    interactionSource = interaction,
                    indication = null,
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
            ChannelLogo(logo, Modifier.fillMaxWidth(0.72f).aspectRatio(1f), channel.isRadio)
            val badge = if (channel.isRadio) "רדיו" else channel.badge
            if (badge.isNotEmpty()) {
                val badgeShape = RoundedCornerShape(6.dp)
                Text(
                    badge,
                    Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .clip(badgeShape)
                        .background(Color.White.copy(alpha = 0.07f))
                        .border(0.75.dp, Color.White.copy(alpha = 0.28f), badgeShape)
                        .padding(horizontal = 6.dp, vertical = 1.5.dp),
                    color = Color.White.copy(alpha = 0.92f),
                    style = MaterialTheme.typography.labelSmall,
                )
            }
            Text(
                channel.number.toString(),
                Modifier.align(Alignment.TopEnd).padding(8.dp),
                color = scheme.onSurfaceVariant.copy(alpha = 0.78f),
                style = MaterialTheme.typography.labelSmall,
            )
            if (favorite) {
                BoldIcon(
                    Icons.Rounded.Star, null,
                    Modifier.align(Alignment.BottomEnd).padding(8.dp).size(14.dp),
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
            style = MaterialTheme.typography.labelLarge.merge(Rtl),
            color = scheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            subtitle,
            Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            style = MaterialTheme.typography.labelSmall.merge(Rtl),
            color = scheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/**
 * Strictly 16:9 landscape card for the "המשך צפייה" (Continue Watching) shelf:
 * Features an atmospheric widescreen frame, embedded title & metadata overlay, ultra-thin bordered glass badge,
 * and a colored playback progress bar along the bottom edge.
 */
@Composable
fun ContinueWatchingCard(
    channel: Channel,
    logo: String,
    subtitle: String,
    progress: Float,
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
    val clampedProgress = progress.coerceIn(0.18f, 0.92f)

    Box(
        modifier
            .width(LocalDimens.current.continueWidth)
            .aspectRatio(16f / 9f)
            .pressPhysics(interaction, glowShape = ContinueCardShape)
            .then(if (sharedKey != null) Modifier.sharedContainer(sharedKey, ContinueCardShape) else Modifier)
            .frostedGlass(ContinueCardShape, Color.White.copy(alpha = 0.05f), blur = 26.dp)
            .combinedClickable(
                interactionSource = interaction,
                indication = null,
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
    ) {
        // Subtle radial spotlight behind logo
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        listOf(scheme.primary.copy(alpha = 0.22f), Color.Transparent),
                    )
                )
        )

        ChannelLogo(
            logo,
            Modifier
                .align(Alignment.Center)
                .padding(bottom = 14.dp)
                .fillMaxWidth(0.56f)
                .aspectRatio(16f / 9f),
            channel.isRadio,
        )

        // Top ultra-thin bordered glass badge
        val topBadgeShape = RoundedCornerShape(6.dp)
        Text(
            if (channel.isRadio) "רדיו חי" else "Live · ${channel.badge.ifBlank { "FHD" }}",
            Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .clip(topBadgeShape)
                .background(Color.White.copy(alpha = 0.07f))
                .border(0.75.dp, Color.White.copy(alpha = 0.28f), topBadgeShape)
                .padding(horizontal = 7.dp, vertical = 2.dp),
            color = Color.White.copy(alpha = 0.92f),
            fontSize = 9.sp,
            fontWeight = FontWeight.Medium,
        )

        if (favorite) {
            BoldIcon(
                Icons.Rounded.Star, null,
                Modifier.align(Alignment.TopStart).padding(8.dp).size(14.dp),
                tint = Color(0xFFFFD54F), weight = 0.45.dp,
            )
        }

        // Bottom dark scrim for embedded landscape title + metadata
        Box(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(60.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.84f))
                    )
                )
        )

        Row(
            Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp, bottom = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    channel.name,
                    style = Rtl,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    subtitle,
                    style = Rtl,
                    color = Color.White.copy(alpha = 0.72f),
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Light,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(Modifier.width(6.dp))
            Box(
                Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.White.copy(alpha = 0.20f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Rounded.PlayArrow, null, Modifier.size(15.dp), tint = Color.White)
            }
        }

        // Colored playback progress bar along the bottom edge of the 16:9 card
        Box(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(3.5.dp)
                .background(Color.White.copy(alpha = 0.18f))
        ) {
            Box(
                Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(clampedProgress)
                    .background(
                        Brush.horizontalGradient(
                            listOf(scheme.primary, Color(0xFF38BDF8))
                        )
                    )
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
}

/**
 * The Nuvio long-press card menu: a heavy-glass popup anchored on the poster with the channel's identity on top and the
 * two actions the feed supports - favourite (star, filled once saved) and the channel's details.
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
    val haptic = LocalHapticFeedback.current
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        modifier = Modifier.width(232.dp),
        offset = DpOffset(0.dp, 4.dp),
        shape = RoundedCornerShape(22.dp),
        containerColor = Color(0xFF16161E).copy(alpha = 0.96f),
        tonalElevation = 0.dp,
        shadowElevation = 24.dp,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
    ) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 13.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier.size(width = 42.dp, height = 26.dp).clip(RoundedCornerShape(7.dp))
                    .background(Color.White.copy(alpha = 0.06f)),
                contentAlignment = Alignment.Center,
            ) {
                ChannelLogo(logo, Modifier.size(width = 34.dp, height = 20.dp), channel.isRadio)
            }
            Spacer(Modifier.width(9.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    channel.name, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
                    maxLines = 1, overflow = TextOverflow.Ellipsis,
                )
                Text(
                    subtitle, color = Color.White.copy(alpha = 0.6f), fontSize = 10.5.sp, fontWeight = FontWeight.Light,
                    maxLines = 1, overflow = TextOverflow.Ellipsis,
                )
            }
        }
        HorizontalDivider(color = Color.White.copy(alpha = 0.08f))
        CardMenuRow(
            if (favorite) Icons.Rounded.Star else Icons.Rounded.StarBorder,
            if (favorite) "הסר מהרשימה שלי" else "שמור ברשימה שלי",
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
        Modifier
            .fillMaxWidth()
            .clickable(remember { MutableInteractionSource() }, null, onClick = onClick)
            .padding(horizontal = 13.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BoldIcon(icon, null, Modifier.size(18.dp), tint = tint, weight = 0.55.dp)
        Spacer(Modifier.width(10.dp))
        Text(label, color = tint, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

/**
 * Row Header: vertical white accent line (3.5.dp width, 16.dp height) next to the SemiBold row title,
 * with an optional shape-morphing tonal "הכל" pill on the trailing edge.
 */
@Composable
fun SectionHeader(title: String, count: Int? = null, onSeeAll: (() -> Unit)? = null) {
    val scheme = MaterialTheme.colorScheme
    val haptic = LocalHapticFeedback.current
    Row(
        Modifier.fillMaxWidth().padding(start = LocalDimens.current.gutter, end = LocalDimens.current.gutter, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .width(3.5.dp)
                .height(16.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color.White)
        )
        Spacer(Modifier.width(9.dp))
        Text(title, Modifier.weight(1f), style = MaterialTheme.typography.titleMedium, color = scheme.onSurface)
        if (onSeeAll != null) {
            val interaction = remember { MutableInteractionSource() }
            val pressed by interaction.collectIsPressedAsState()
            val corner by animateDpAsState(
                targetValue = if (pressed) 8.dp else 18.dp,
                animationSpec = spring(dampingRatio = 0.58f, stiffness = Spring.StiffnessMedium),
                label = "seeAllMorph",
            )
            val pillShape = RoundedCornerShape(corner)
            Row(
                Modifier
                    .pressPhysics(interaction, tilt = false, glowShape = pillShape)
                    .clip(pillShape)
                    .background(scheme.secondaryContainer.copy(alpha = 0.55f))
                    .clickable(interaction, null) {
                        haptic.performHapticFeedback(HapticFeedbackType.SegmentTick)
                        onSeeAll()
                    }
                    .padding(start = 12.dp, end = 6.dp, top = 4.5.dp, bottom = 4.5.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    if (count != null) "הכל · $count" else "הכל",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = scheme.onSecondaryContainer,
                )
                Icon(
                    Icons.AutoMirrored.Rounded.KeyboardArrowRight, null,
                    Modifier.size(16.dp), tint = scheme.onSecondaryContainer,
                )
            }
        }
    }
}

/** A horizontal carousel of 16:9 landscape "Continue Watching" (המשך צפייה) cards with bottom progress bars. */
@Composable
fun ContinueWatchingShelf(
    title: String,
    channels: List<Channel>,
    logo: (Channel) -> String,
    subtitle: (Channel) -> String,
    onPlay: (Channel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val d = LocalDimens.current
    Column(modifier.padding(bottom = d.shelfGap)) {
        SectionHeader(title)
        LazyRow(
            contentPadding = PaddingValues(horizontal = d.gutter),
            horizontalArrangement = Arrangement.spacedBy(d.cardGap),
            flingBehavior = rememberBitChordFlingBehavior(),
        ) {
            itemsIndexed(channels, key = { _, ch -> ch.id }) { idx, ch ->
                // Deterministic progress per channel/recency so the bar looks natural and consistent
                val p = remember(ch.id, idx) {
                    val hash = abs(ch.id.hashCode() % 60) / 100f
                    (0.32f + hash).coerceIn(0.24f, 0.88f)
                }
                ContinueWatchingCard(
                    channel = ch,
                    logo = logo(ch),
                    subtitle = subtitle(ch),
                    progress = p,
                    onClick = { onPlay(ch) },
                    sharedKey = "continue:${ch.id}",
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
    val d = LocalDimens.current
    Column(modifier.padding(bottom = d.shelfGap)) {
        SectionHeader(title, count, onSeeAll)
        LazyRow(
            contentPadding = PaddingValues(horizontal = d.gutter),
            horizontalArrangement = Arrangement.spacedBy(d.cardGap),
            flingBehavior = rememberBitChordFlingBehavior(),
        ) {
            items(channels, key = { it.id }) { ch ->
                ChannelCard(ch, logo(ch), subtitle(ch), { onPlay(ch) }, Modifier.width(d.posterWidth), sharedKey = "$title:${ch.id}")
            }
        }
    }
}

/** Dark-themed shimmer loading placeholders in the shape of a shelf. */
@Composable
fun ShelfSkeleton() {
    val d = LocalDimens.current
    Column(Modifier.padding(bottom = d.shelfGap)) {
        Row(
            Modifier.padding(start = d.gutter, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(Modifier.width(3.5.dp).height(16.dp).glassShimmer(RoundedCornerShape(2.dp)))
            Spacer(Modifier.width(9.dp))
            Box(Modifier.width(124.dp).height(18.dp).glassShimmer(RoundedCornerShape(9.dp)))
        }
        Row(Modifier.padding(horizontal = d.gutter), horizontalArrangement = Arrangement.spacedBy(d.cardGap)) {
            repeat(4) {
                Column(Modifier.width(d.posterWidth)) {
                    Box(Modifier.fillMaxWidth().aspectRatio(PosterAspect).glassShimmer(CardShape))
                    Spacer(Modifier.height(8.dp))
                    Box(Modifier.fillMaxWidth(0.8f).height(12.dp).glassShimmer(RoundedCornerShape(6.dp)))
                    Spacer(Modifier.height(5.dp))
                    Box(Modifier.fillMaxWidth(0.5f).height(10.dp).glassShimmer(RoundedCornerShape(5.dp)))
                }
            }
        }
    }
}

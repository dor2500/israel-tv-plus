package com.israeltv.plus.ui.player

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.israeltv.plus.data.CatchupProgram
import com.israeltv.plus.data.Channel
import com.israeltv.plus.ui.components.ChannelLogo
import com.israeltv.plus.ui.theme.LocalDimens

/**
 * The single bottom-anchored playback overlay:
 *
 * ```
 * ┌──────────────────────────────────────────────────────────────┐
 * │                         (video)                              │
 * │░░░░░░░░░░░░░░░░░░░░░ gradient: transparent → black 0.8 ░░░░░░░│
 * │            [prev]  [ CENTER (focused) ]  [next]   ┌────────┐ │
 * │                       [ אפשרויות ]                │ mini   │ │
 * │ 00:32                                             │ list   │ │
 * └──────────────────────────────────────────────────────────────┘
 * ```
 *
 * Left/right are PHYSICAL (AbsoluteAlignment), not Start/End: the app runs RTL, where BottomStart would be the right edge.
 * The carousel is inset by the mini list's width on both sides, so it stays centred and can never overlap the list.
 */
@Composable
internal fun PlayerOverlay(
    channel: Channel,
    programs: List<CatchupProgram>,
    positionMs: Long,
    durationMs: Long,
    showDuration: Boolean,
    channels: List<Channel>,
    currentIndex: Int,
    logo: (Channel) -> String,
    onProgramClick: (CatchupProgram?) -> Unit,
    onOptions: () -> Unit,
    onPickChannel: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val d = LocalDimens.current
    val centerFocus = remember { FocusRequester() }

    // D-pad users land on the centre card as soon as the overlay appears
    LaunchedEffect(Unit) {
        withFrameNanos { }
        runCatching { centerFocus.requestFocus() }
    }

    // 1. Root container
    Box(modifier.fillMaxSize()) {
        // 2. Bottom gradient area: every control lives inside it
        BoxWithConstraints(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))))
                .navigationBarsPadding()
                .windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal))
                .padding(start = d.playerEdge, end = d.playerEdge, top = 72.dp, bottom = 20.dp),
        ) {
            val wide = maxWidth >= 600.dp
            val listWidth: Dp = when {
                !wide -> 0.dp
                d.isTv -> 220.dp
                else -> 180.dp
            }
            // symmetric reservation keeps the carousel visually centred on screen
            val sideReserve = if (wide) listWidth + 24.dp else 0.dp
            val carouselWidth = maxWidth - sideReserve * 2
            val gap = if (d.isTv) 20.dp else 12.dp
            val showSides = carouselWidth >= 420.dp
            val cardWidth: Dp = if (showSides) {
                ((carouselWidth - gap * 2) / 3 / CENTER_SCALE).coerceAtMost(if (d.isTv) 280.dp else 220.dp)
            } else {
                carouselWidth.coerceAtMost(300.dp)
            }

            // 3 + 4. Centre carousel with the Options button directly beneath the focused card
            Column(
                Modifier
                    .align(Alignment.BottomCenter)
                    .width(carouselWidth),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                ProgramCarousel(
                    channel = channel,
                    programs = programs,
                    cardWidth = cardWidth,
                    gap = gap,
                    showSides = showSides,
                    centerFocus = centerFocus,
                    onProgramClick = onProgramClick,
                )
                Spacer(Modifier.height(if (d.isTv) 18.dp else 12.dp))
                OptionsButton(onOptions)
            }

            // 5. Time indicator, bottom-left (physical)
            Text(
                "‎" + formatTime(positionMs) + if (showDuration && durationMs > 0L) " / " + formatTime(durationMs) else "",
                Modifier.align(AbsoluteAlignment.BottomLeft).padding(bottom = 6.dp),
                color = Color.White,
                fontSize = if (d.isTv) 20.sp else 15.sp,
                fontWeight = FontWeight.SemiBold,
            )

            // 6. Compact mini channel list, pinned to the right edge (physical)
            if (wide && channels.size > 1) {
                MiniChannelList(
                    channels = channels,
                    currentIndex = currentIndex,
                    logo = logo,
                    onPick = onPickChannel,
                    modifier = Modifier.align(AbsoluteAlignment.BottomRight).width(listWidth),
                )
            }
        }
    }
}

private const val CENTER_SCALE = 1.08f
private const val SIDE_SCALE = 0.86f

@Composable
private fun ProgramCarousel(
    channel: Channel,
    programs: List<CatchupProgram>,
    cardWidth: Dp,
    gap: Dp,
    showSides: Boolean,
    centerFocus: FocusRequester,
    onProgramClick: (CatchupProgram?) -> Unit,
) {
    val liveIndex = remember(programs) {
        val now = System.currentTimeMillis()
        programs.indexOfFirst { now in it.startMs..it.endMs }
            .takeIf { it >= 0 }
            ?: programs.indexOfFirst { it.startMs > now }.coerceAtLeast(0)
    }
    var selected by remember(channel.id, programs) { mutableIntStateOf(liveIndex) }
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    val prev = programs.getOrNull(selected - 1)
    val center = programs.getOrNull(selected)
    val next = programs.getOrNull(selected + 1)

    // the timeline reads in the layout direction: in RTL, earlier programmes sit on the right
    fun step(physicalLeft: Boolean): Boolean {
        val delta = if (physicalLeft == rtl) 1 else -1
        val target = selected + delta
        if (target !in programs.indices) return false
        selected = target
        return true
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(gap),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (showSides) {
            SideCard(prev, cardWidth) { selected -= 1 }
        }
        ProgramCard(
            program = center,
            fallbackTitle = channel.name,
            width = cardWidth,
            prominent = true,
            modifier = Modifier
                .focusRequester(centerFocus)
                .onKeyEvent { e ->
                    if (e.type != KeyEventType.KeyDown) return@onKeyEvent false
                    when (e.key) {
                        Key.DirectionLeft -> step(physicalLeft = true)
                        Key.DirectionRight -> step(physicalLeft = false)
                        else -> false
                    }
                },
            onClick = { onProgramClick(center) },
        )
        if (showSides) {
            SideCard(next, cardWidth) { selected += 1 }
        }
    }
}

/** A dimmed neighbour; an empty slot of the same width keeps the centre card centred at either end of the guide. */
@Composable
private fun SideCard(program: CatchupProgram?, width: Dp, onClick: () -> Unit) {
    if (program == null) {
        Spacer(Modifier.width(width))
    } else {
        ProgramCard(program, fallbackTitle = "", width = width, prominent = false, focusable = false, onClick = onClick)
    }
}

@Composable
private fun ProgramCard(
    program: CatchupProgram?,
    fallbackTitle: String,
    width: Dp,
    prominent: Boolean,
    modifier: Modifier = Modifier,
    focusable: Boolean = true,
    onClick: () -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    val scale by animateFloatAsState(
        when {
            !prominent -> SIDE_SCALE
            focused -> CENTER_SCALE + 0.04f
            else -> CENTER_SCALE
        },
        tween(180),
        label = "programCardScale",
    )
    val alpha by animateFloatAsState(if (prominent) 1f else 0.5f, tween(180), label = "programCardAlpha")
    val shape = RoundedCornerShape(16.dp)
    val live = program?.isLiveNow ?: true

    Box(
        modifier
            .width(width)
            .aspectRatio(16f / 10f)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
            .clip(shape)
            .background(Color(0xFF1B1B22))
            .border(if (focused) 3.dp else 1.dp, if (focused) Color.White else Color.White.copy(alpha = 0.16f), shape)
            // side cards stay tappable but are skipped by D-pad focus: left/right on the centre card moves the guide
            .focusProperties { canFocus = focusable }
            .clickable(interaction, null, role = Role.Button, onClick = onClick),
    ) {
        if (!program?.poster.isNullOrEmpty()) {
            AsyncImage(
                model = program!!.poster,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }
        Box(
            Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.05f), Color.Black.copy(alpha = 0.85f))))
        )
        Column(
            Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(10.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (live) {
                    Box(Modifier.size(7.dp).clip(CircleShape).background(Color(0xFFE53935)))
                    Spacer(Modifier.width(5.dp))
                }
                Text(
                    program?.relativeLabel ?: "שידור חי",
                    color = if (live) Color(0xFFFF8A80) else Color.White.copy(alpha = 0.7f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                )
            }
            Text(
                program?.title ?: fallbackTitle,
                color = Color.White,
                fontSize = if (prominent) 15.sp else 13.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            if (program != null) {
                Text("‎" + program.timeLabel, color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp, maxLines = 1)
                if (program.isLiveNow) {
                    Spacer(Modifier.height(5.dp))
                    Box(Modifier.fillMaxWidth().height(3.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.25f))) {
                        Box(Modifier.fillMaxWidth(program.progressFraction).fillMaxHeight().background(Color(0xFFE53935)))
                    }
                }
            }
        }
    }
}

@Composable
private fun OptionsButton(onClick: () -> Unit) {
    val d = LocalDimens.current
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    val bg = if (focused) Color.White else Color.White.copy(alpha = 0.14f)
    val fg = if (focused) Color.Black else Color.White
    Row(
        Modifier
            .clip(CircleShape)
            .background(bg)
            .clickable(interaction, null, role = Role.Button, onClick = onClick)
            .padding(horizontal = 22.dp, vertical = if (d.isTv) 11.dp else 9.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(Icons.Rounded.MoreHoriz, null, Modifier.size(20.dp), tint = fg)
        Spacer(Modifier.width(8.dp))
        Text("אפשרויות", color = fg, fontSize = if (d.isTv) 16.sp else 14.sp, fontWeight = FontWeight.Bold)
    }
}

/** Five neighbouring channels around the current one, current highlighted; compact rows, no header. */
@Composable
private fun MiniChannelList(
    channels: List<Channel>,
    currentIndex: Int,
    logo: (Channel) -> String,
    onPick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val window = remember(channels, currentIndex) {
        val n = channels.size
        val span = minOf(2, (n - 1) / 2)
        (-span..span).map { off -> ((currentIndex + off) % n + n) % n }.distinct()
    }
    Column(
        modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Black.copy(alpha = 0.45f))
            .padding(6.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        window.forEach { i ->
            MiniChannelRow(channels[i], logo(channels[i]), isCurrent = i == currentIndex) { onPick(i) }
        }
    }
}

@Composable
private fun MiniChannelRow(ch: Channel, logoUrl: String, isCurrent: Boolean, onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    val bg = when {
        focused -> Color.White
        isCurrent -> MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
        else -> Color.Transparent
    }
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .clickable(interaction, null, role = Role.Button, onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ChannelLogo(logoUrl, Modifier.size(26.dp), ch.isRadio)
        Spacer(Modifier.width(8.dp))
        Text(
            ch.name,
            color = if (focused) Color.Black else Color.White,
            fontSize = 13.sp,
            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

package com.israeltv.plus.ui.player

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.israeltv.plus.data.CatchupProgram
import com.israeltv.plus.data.Channel
import com.israeltv.plus.ui.components.ChannelLogo
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val MAX_OFFSET = 8
private val FOCUS_RING = Color.White
private val CARD_BG = Color(0xFF16161B)
private val LIVE_RED = Color(0xFFE5202E)
private val CAPTION_GREY = Color(0xFFD0D2D6)
private val SUBTITLE_GREY = Color(0xFFB8BEC5)

/**
 * TV Landscape Video Player Overlay (10-foot UI, D-pad & Mouse navigation):
 * - Root: `Box(modifier = Modifier.fillMaxSize())` covering the entire screen.
 * - Bottom Control Bar: Aligned to `Alignment.BottomCenter` spanning the full width with
 *   `Brush.verticalGradient` (Transparent to Black `alpha = 0.9f`).
 * - Horizontal TV Carousel (Program Cards): Centered horizontal `Row` where the "Current Program"
 *   card is visually prominent and focused, and the "Next Program" card sits adjacent to it, slightly scaled down.
 * - Options Button: `"אפשרויות"` placed directly below the focused center program card; no other floating action buttons.
 * - Progress Bar & Time: Anchored neatly across the bottom of the container with current time (`Alignment.BottomStart`)
 *   and end time (`Alignment.BottomEnd`).
 */
@Composable
internal fun PlayerOverlay(
    channel: Channel,
    programs: List<CatchupProgram>,
    positionMs: Long,
    durationMs: Long,
    isVod: Boolean,
    inCatchup: Boolean,
    catchupTitle: String?,
    canCatchup: Boolean,
    channels: List<Channel>,
    currentIndex: Int,
    logo: (Channel) -> String,
    onProgramClick: (CatchupProgram?) -> Unit,
    onOptions: () -> Unit,
    onGoLive: () -> Unit,
    onPickChannel: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val centerCardFocus = remember { FocusRequester() }
    val optionsFocus = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        withFrameNanos { }
        runCatching { centerCardFocus.requestFocus() }
    }

    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(10_000)
            now = System.currentTimeMillis()
        }
    }

    val airIndex = remember(programs, now / 60_000L) {
        programs.indexOfFirst { now in it.startMs until it.endMs }
    }
    var offset by remember(channel.id) { mutableIntStateOf(0) }
    val hasSchedule = airIndex != -1 && !inCatchup
    val currentProgram = if (hasSchedule) programs.getOrNull(airIndex + offset) else null
    val nextProgram = if (hasSchedule) programs.getOrNull(airIndex + offset + 1) else null

    fun moveOffset(delta: Int): Boolean {
        val target = offset + delta
        val floor = if (canCatchup) -airIndex else -1
        if (!hasSchedule || target < minOf(-1, floor) || target > MAX_OFFSET || (airIndex + target) !in programs.indices) {
            return false
        }
        offset = target
        return true
    }

    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val airingNow = currentProgram != null && currentProgram.startMs <= now && now < currentProgram.endMs
    val live = !inCatchup && currentProgram != null && offset == 0 && airingNow

    val progressFraction = when {
        inCatchup || isVod -> if (durationMs > 0L) (positionMs.toFloat() / durationMs).coerceIn(0f, 1f) else 0f
        live && currentProgram != null -> {
            val total = (currentProgram.endMs - currentProgram.startMs).coerceAtLeast(1L)
            ((now - currentProgram.startMs).toFloat() / total).coerceIn(0f, 1f)
        }
        else -> 0f
    }

    val currentTimeLabel = when {
        inCatchup || isVod -> "‎" + formatTime(positionMs)
        currentProgram != null -> formatClock(currentProgram.startMs)
        else -> formatClock(now)
    }

    val endTimeLabel = when {
        inCatchup || isVod -> if (durationMs > 0L) "‎" + formatTime(durationMs) else ""
        currentProgram != null -> formatClock(currentProgram.endMs)
        else -> ""
    }

    val currentLabel = when {
        inCatchup -> "צפייה חוזרת"
        currentProgram == null -> "משודר עכשיו"
        offset < 0 -> if (canCatchup) "OK לצפייה חוזרת" else "שודר קודם"
        offset == 0 -> if (airingNow) "משודר עכשיו" else "בקרוב"
        offset == 1 -> "הבאה"
        else -> "בהמשך"
    }

    val currentTitle = when {
        inCatchup -> catchupTitle?.takeIf { it.isNotEmpty() } ?: channel.name
        currentProgram != null -> currentProgram.title
        else -> channel.name
    }

    val currentSub = when {
        inCatchup || currentProgram != null -> "${if (channel.number > 0) "${channel.number} · " else ""}${channel.name}"
        else -> "שידור חי ישיר"
    }

    // Root covering the entire screen
    Box(modifier = modifier.fillMaxSize()) {
        // Bottom Control Bar aligned to BottomCenter spanning the full width with vertical gradient (Transparent to Black 0.9f)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.65f),
                            Color.Black.copy(alpha = 0.9f),
                        )
                    )
                )
                .navigationBarsPadding()
                .windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal))
                .padding(top = 40.dp, start = 32.dp, end = 32.dp, bottom = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Horizontal TV Carousel (Program Cards) centered horizontally
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.Top,
                ) {
                    // Center Column: Prominent Focused "Current Program" Card + "אפשרויות" Button directly below it
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        TvProgramCard(
                            posterUrl = if (inCatchup) "" else currentProgram?.poster.orEmpty(),
                            logoUrl = logo(channel),
                            badgeText = currentLabel,
                            title = currentTitle,
                            subtitle = currentSub,
                            timeRangeText = currentProgram?.let { formatRange(it) },
                            isLive = live,
                            isProminent = true,
                            modifier = Modifier
                                .width(320.dp)
                                .height(176.dp)
                                .focusRequester(centerCardFocus)
                                .focusProperties { down = optionsFocus }
                                .onKeyEvent { e ->
                                    if (e.type != KeyEventType.KeyDown) return@onKeyEvent false
                                    when (e.key) {
                                        Key.DirectionLeft -> moveOffset(if (rtl) 1 else -1)
                                        Key.DirectionRight -> moveOffset(if (rtl) -1 else 1)
                                        Key.DirectionUp -> {
                                            if (channels.size > 1) {
                                                onPickChannel((currentIndex + 1) % channels.size)
                                                true
                                            } else false
                                        }
                                        else -> false
                                    }
                                },
                            onClick = { onProgramClick(currentProgram) },
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Options Button ("אפשרויות") directly below the focused center program card
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            TvOptionsButton(
                                text = "אפשרויות",
                                isOptions = true,
                                modifier = Modifier
                                    .focusRequester(optionsFocus)
                                    .focusProperties { up = centerCardFocus },
                                onClick = onOptions,
                            )
                            if (inCatchup) {
                                TvOptionsButton(
                                    text = "לשידור החי",
                                    isOptions = false,
                                    modifier = Modifier.focusProperties { up = centerCardFocus },
                                    onClick = onGoLive,
                                )
                            }
                        }
                    }

                    // Adjacent "Next Program" Card, slightly scaled down
                    if (nextProgram != null) {
                        Column(
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .scale(0.88f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            TvProgramCard(
                                posterUrl = nextProgram.poster,
                                logoUrl = logo(channel),
                                badgeText = when (offset) {
                                    -1 -> "משודר עכשיו"
                                    0 -> "הבאה"
                                    else -> "בהמשך"
                                },
                                title = nextProgram.title,
                                subtitle = formatRange(nextProgram),
                                timeRangeText = null,
                                isLive = false,
                                isProminent = false,
                                modifier = Modifier
                                    .width(256.dp)
                                    .height(148.dp),
                                onClick = { moveOffset(1) },
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Progress Bar & Time anchored neatly across the bottom of the container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp)
                ) {
                    // Current / Start Time at Alignment.BottomStart
                    Text(
                        text = currentTimeLabel,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(bottom = 12.dp),
                    )

                    // End Time at Alignment.BottomEnd
                    if (endTimeLabel.isNotEmpty()) {
                        Text(
                            text = endTimeLabel,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(bottom = 12.dp),
                        )
                    }

                    // Playback Progress Bar anchored across the bottom
                    LinearProgressIndicator(
                        progress = { progressFraction },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = LIVE_RED,
                        trackColor = Color.White.copy(alpha = 0.28f),
                        strokeCap = StrokeCap.Round,
                    )
                }
            }
        }
    }
}

@Composable
private fun TvProgramCard(
    posterUrl: String,
    logoUrl: String,
    badgeText: String,
    title: String,
    subtitle: String?,
    timeRangeText: String?,
    isLive: Boolean,
    isProminent: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    val scale by animateFloatAsState(
        targetValue = if (focused && isProminent) 1.03f else 1f,
        animationSpec = tween(160),
        label = "tvCardScale",
    )
    val shape = RoundedCornerShape(14.dp)

    Box(
        modifier = modifier
            .scale(scale)
            .clip(shape)
            .background(CARD_BG)
            .then(
                if (focused) {
                    Modifier.border(2.5.dp, FOCUS_RING, shape)
                } else {
                    Modifier.border(1.dp, Color.White.copy(alpha = 0.16f), shape)
                }
            )
            .focusable(interactionSource = interaction)
            .clickable(interactionSource = interaction, indication = null, role = Role.Button, onClick = onClick)
    ) {
        // Channel Logo fallback background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 36.dp),
            contentAlignment = Alignment.Center,
        ) {
            ChannelLogo(
                url = logoUrl,
                modifier = Modifier
                    .width(if (isProminent) 120.dp else 92.dp)
                    .height(if (isProminent) 64.dp else 48.dp),
            )
        }

        // Program Poster when available
        if (posterUrl.isNotEmpty()) {
            AsyncImage(
                model = posterUrl,
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }

        // Readability gradient overlay inside the card
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.76f)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.92f),
                        )
                    )
                )
        )

        // Program Metadata
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                if (isLive) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(LIVE_RED)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                }
                Text(
                    text = badgeText,
                    color = if (isLive) Color(0xFFFF8A80) else CAPTION_GREY,
                    fontSize = if (isProminent) 12.sp else 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                )
                if (!timeRangeText.isNullOrEmpty()) {
                    Text(
                        text = " · $timeRangeText",
                        color = CAPTION_GREY,
                        fontSize = 11.sp,
                        maxLines = 1,
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = title,
                color = Color.White,
                fontSize = if (isProminent) 17.sp else 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
            )

            if (!subtitle.isNullOrEmpty()) {
                Text(
                    text = subtitle,
                    color = SUBTITLE_GREY,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun TvOptionsButton(
    text: String,
    isOptions: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    val shape = RoundedCornerShape(50)
    val bg = if (focused) Color.White else Color.White.copy(alpha = 0.16f)
    val fg = if (focused) Color(0xFF0E0E12) else Color.White

    Row(
        modifier = modifier
            .widthIn(min = 132.dp)
            .height(40.dp)
            .clip(shape)
            .background(bg)
            .then(
                if (focused) Modifier.border(2.dp, FOCUS_RING, shape)
                else Modifier.border(1.dp, Color.White.copy(alpha = 0.25f), shape)
            )
            .focusable(interactionSource = interaction)
            .clickable(interactionSource = interaction, indication = null, role = Role.Button, onClick = onClick)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = if (isOptions) Icons.Rounded.MoreHoriz else Icons.Rounded.Tv,
            contentDescription = null,
            tint = fg,
            modifier = Modifier.size(18.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            color = fg,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
        )
    }
}

private fun formatClock(ms: Long): String =
    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(ms))

/** Isolated LTR time range so it never flips in RTL layout. */
private fun formatRange(p: CatchupProgram): String =
    "⁦${formatClock(p.startMs)} – ${formatClock(p.endMs)}⁩"

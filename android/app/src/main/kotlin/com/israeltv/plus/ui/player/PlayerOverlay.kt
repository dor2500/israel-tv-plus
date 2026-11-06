package com.israeltv.plus.ui.player

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
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import coil3.compose.AsyncImage
import com.israeltv.plus.data.CatchupProgram
import com.israeltv.plus.data.Channel
import com.israeltv.plus.ui.components.ChannelLogo
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * The phone/tablet copy of the web app's TV player OSD (tvguide.js, v4.8.58): one bottom block over a dark gradient.
 *
 * ```
 *  ┌────────────────────────────────────────────────────────────────────────────┐
 *  │                                (video)                                     │
 *  │ ░░░░░░░░░░░░░░░░░░ gradient: transparent → black 0.8 ░░░░░░░░░░░░░░░░░░░░ │
 *  │          <  [ next, small ]  [   PROGRAMME (focus)   ]  לתכנית   >   73 ▣ │
 *  │              01:08 – 01:32    ─────────────●──────────   הקודמת      73 ▣ │
 *  │                               00:45     היום     01:08               72 ▣ │
 *  │ 00:32                            [ אפשרויות ]                              │
 *  └────────────────────────────────────────────────────────────────────────────┘
 * ```
 *
 * Every size is in `u`, the web OSD's rem (CSS values copied 1:1), scaled to the screen width. Left/right are
 * physical (AbsoluteAlignment): clock on the left, channel strip on the right, whatever the layout direction.
 * The programme block is centred on screen and the u-scale guarantees it never reaches the strip.
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
    val bigFocus = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        withFrameNanos { }
        runCatching { bigFocus.requestFocus() }
    }
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(10_000)
            now = System.currentTimeMillis()
        }
    }

    val air = remember(programs, now / 60_000L) { programs.indexOfFirst { now in it.startMs until it.endMs } }
    var offset by remember(channel.id) { mutableIntStateOf(0) }
    val has = air != -1 && !inCatchup
    val p = if (has) programs.getOrNull(air + offset) else null
    val next = if (has) programs.getOrNull(air + offset + 1) else null
    val prev = if (has) programs.getOrNull(air + offset - 1) else null

    fun moveOffset(d: Int): Boolean {
        val n = offset + d
        val floor = if (canCatchup) -air else -1
        if (!has || n < minOf(-1, floor) || n > MAX_OFFSET || air + n !in programs.indices) return false
        offset = n
        return true
    }

    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    Box(modifier.fillMaxSize()) {
        BoxWithConstraints(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))))
                .navigationBarsPadding()
                .windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal)),
        ) {
            val wide = maxWidth >= 600.dp
            // the web layout is ~142rem wide (strip, peek, card, next card, clock column): one rem = one u
            val u: Dp = if (wide) (maxWidth / 142f).coerceAtMost(7.dp) else (maxWidth / 40f).coerceAtMost(8.dp)
            val side = 28 * u

            // ---- the programme block, centred: [peek] [card + options] [next card  <]
            Row(
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(top = 8 * u, bottom = 2.4f * u),
                verticalAlignment = Alignment.Bottom,
            ) {
                if (wide) {
                    Peek(
                        prev = prev?.takeIf { offset > -1 || canCatchup },
                        u = u,
                        rtl = rtl,
                        modifier = Modifier.width(side),
                        onClick = { moveOffset(-1) },
                    )
                    Spacer(Modifier.width(1.8f * u))
                }
                MainColumn(
                    channel = channel,
                    p = p,
                    offset = offset,
                    now = now,
                    inCatchup = inCatchup,
                    catchupTitle = catchupTitle,
                    positionMs = positionMs,
                    durationMs = durationMs,
                    canCatchup = canCatchup,
                    logo = logo(channel),
                    u = u,
                    bigModifier = Modifier
                        .focusRequester(bigFocus)
                        .onKeyEvent { e ->
                            if (e.type != KeyEventType.KeyDown) return@onKeyEvent false
                            when (e.key) {
                                // the later programmes sit on the physical left in RTL, on the right in LTR
                                Key.DirectionLeft -> moveOffset(if (rtl) 1 else -1)
                                Key.DirectionRight -> moveOffset(if (rtl) -1 else 1)
                                else -> false
                            }
                        },
                    onBig = { onProgramClick(p) },
                    onOptions = onOptions,
                    onGoLive = onGoLive,
                )
                if (wide) {
                    Spacer(Modifier.width(1.8f * u))
                    NextSlot(
                        next = next,
                        offset = offset,
                        logo = logo(channel),
                        u = u,
                        rtl = rtl,
                        modifier = Modifier.width(side),
                        onClick = { moveOffset(1) },
                    )
                }
            }

            // ---- clock (VOD: position / length), bottom-left
            val fmt = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
            Text(
                "‎" + if (isVod && durationMs > 0L) formatTime(positionMs) + " / " + formatTime(durationMs) else fmt.format(Date(now)),
                Modifier.align(AbsoluteAlignment.BottomLeft).padding(start = 3 * u, end = 3 * u, bottom = 2.4f * u),
                color = Color.White,
                fontSize = 2.2f.u(u),
                fontWeight = FontWeight.Medium,
            )

            // ---- channel strip, right edge
            if (wide && channels.size > 1) {
                ChannelStrip(
                    channels = channels,
                    currentIndex = currentIndex,
                    logo = logo,
                    u = u,
                    onPick = onPickChannel,
                    modifier = Modifier
                        .align(AbsoluteAlignment.BottomRight)
                        .padding(end = 3 * u, bottom = 2.4f * u)
                        .width(23 * u),
                )
            }
        }
    }
}

private const val MAX_OFFSET = 8
private val FOCUS_RING = Color.White
private val CARD_BG = Color(0xFF16161B)
private val LIVE_RED = Color(0xFFE5202E)
private val CAPTION_GREY = Color(0xFFD0D2D6)
private val NAME_GREY = Color(0xFFC4C6CB)

@Composable
private fun Float.u(u: Dp): TextUnit = with(LocalDensity.current) { (u * this@u).toSp() }

private fun clock(ms: Long): String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(ms))

/** "01:08 – 01:32", isolated LTR so it does not flip inside the RTL layout. */
private fun range(p: CatchupProgram): String = "⁦" + clock(p.startMs) + " – " + clock(p.endMs) + "⁩"

private val DAYS = listOf("ראשון", "שני", "שלישי", "רביעי", "חמישי", "שישי", "שבת")

private fun dayLabel(ms: Long): String {
    val c = Calendar.getInstance().apply { timeInMillis = ms }
    val t = Calendar.getInstance()
    val today = c.get(Calendar.YEAR) == t.get(Calendar.YEAR) && c.get(Calendar.DAY_OF_YEAR) == t.get(Calendar.DAY_OF_YEAR)
    return if (today) "היום" else "יום " + DAYS[c.get(Calendar.DAY_OF_WEEK) - 1]
}

/** "לתכנית הקודמת": chevron pointing out, then the previous programme's title and hours; text only, no card. */
@Composable
private fun Peek(prev: CatchupProgram?, u: Dp, rtl: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Box(modifier.padding(bottom = 10.6f * u), contentAlignment = if (rtl) Alignment.CenterEnd else Alignment.CenterStart) {
        if (prev == null) return@Box
        Row(
            Modifier
                .width(18 * u)
                .focusProperties { canFocus = false }
                .clickable(remember { MutableInteractionSource() }, null, role = Role.Button, onClick = onClick),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(if (rtl) Icons.Rounded.ChevronRight else Icons.Rounded.ChevronLeft, null, Modifier.size(3f * u), tint = Color.White)
            Spacer(Modifier.width(0.4f * u))
            Column(Modifier.weight(1f)) {
                Text("לתכנית הקודמת", color = Color.White, fontSize = 1.6f.u(u), maxLines = 1, softWrap = false)
                Text(prev.title, color = NAME_GREY, fontSize = 1.4f.u(u), maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 0.4f * u))
                Text(range(prev), color = NAME_GREY, fontSize = 1.4f.u(u), maxLines = 1, modifier = Modifier.padding(top = 0.4f * u))
            }
        }
    }
}

@Composable
private fun MainColumn(
    channel: Channel,
    p: CatchupProgram?,
    offset: Int,
    now: Long,
    inCatchup: Boolean,
    catchupTitle: String?,
    positionMs: Long,
    durationMs: Long,
    canCatchup: Boolean,
    logo: String,
    u: Dp,
    bigModifier: Modifier,
    onBig: () -> Unit,
    onOptions: () -> Unit,
    onGoLive: () -> Unit,
) {
    val airingNow = p != null && p.startMs <= now
    val label = when {
        inCatchup -> "צפייה חוזרת"
        p == null -> "משודר עכשיו"
        offset < 0 -> if (canCatchup) "OK לצפייה חוזרת" else "שודר קודם"
        offset == 0 -> if (airingNow) "משודר עכשיו" else "בקרוב"
        offset == 1 -> "הבאה"
        else -> "בהמשך"
    }
    val title = when {
        inCatchup -> catchupTitle?.takeIf { it.isNotEmpty() } ?: channel.name
        p == null -> channel.name
        else -> p.title
    }
    val sub = when {
        inCatchup || p != null -> channel.name
        else -> "אין מידע על התכנית"
    }
    val live = !inCatchup && p != null && offset == 0 && airingNow
    val fraction = when {
        inCatchup -> if (durationMs > 0L) positionMs.toFloat() / durationMs else 0f
        live -> ((now - p!!.startMs).toFloat() / (p.endMs - p.startMs).coerceAtLeast(1L)).coerceIn(0f, 1f)
        else -> 0f
    }

    Column(Modifier.width(31 * u), horizontalAlignment = Alignment.CenterHorizontally) {
        ProgrammeCard(
            poster = if (inCatchup) "" else p?.poster.orEmpty(),
            logo = logo,
            label = label,
            title = title,
            sub = sub,
            titleSize = 2.3f,
            u = u,
            modifier = bigModifier.fillMaxWidth().height(17.4f * u),
            onClick = onBig,
        )
        // the bar keeps its height when hidden, so the times below never jump
        Box(
            Modifier
                .padding(top = 0.6f * u)
                .fillMaxWidth()
                .height(0.4f * u)
                .alpha(if (live || inCatchup) 1f else 0f)
                .background(Color.White.copy(alpha = 0.34f)),
        ) {
            Box(Modifier.fillMaxHeight().fillMaxWidth(fraction.coerceIn(0f, 1f)).background(LIVE_RED))
        }
        Row(
            Modifier.padding(top = 0.6f * u).fillMaxWidth().height(3 * u),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = if (live || inCatchup) Arrangement.SpaceBetween else Arrangement.Center,
        ) {
            val big = 1.7f.u(u)
            when {
                inCatchup -> {
                    Text("‎" + formatTime(positionMs), color = Color.White, fontSize = big, fontWeight = FontWeight.Medium)
                    Text("‎" + formatTime(durationMs), color = Color.White, fontSize = big, fontWeight = FontWeight.Medium)
                }
                live -> {
                    Text(clock(p!!.startMs), color = Color.White, fontSize = big, fontWeight = FontWeight.Medium)
                    Text(dayLabel(p.startMs), color = NAME_GREY, fontSize = 1.5f.u(u))
                    Text(clock(p.endMs), color = Color.White, fontSize = big, fontWeight = FontWeight.Medium)
                }
                p != null -> Text(range(p), color = Color.White, fontSize = big, fontWeight = FontWeight.Medium)
            }
        }
        Row(
            Modifier.padding(top = 0.5f * u).height(5 * u),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(u),
        ) {
            if (inCatchup) OsdButton("לשידור החי", u, onGoLive)
            OsdButton("אפשרויות", u, onOptions)
        }
    }
}

/** The next programme's smaller card with its hours under it, then the chevron that walks the guide forward. */
@Composable
private fun NextSlot(
    next: CatchupProgram?,
    offset: Int,
    logo: String,
    u: Dp,
    rtl: Boolean,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    Row(modifier, verticalAlignment = Alignment.Bottom) {
        if (next == null) return@Row
        Column(Modifier.width(23 * u).padding(bottom = 5.5f * u)) {
            ProgrammeCard(
                poster = next.poster,
                logo = logo,
                label = when (offset) {
                    -1 -> "משודר עכשיו"
                    0 -> "הבאה"
                    else -> "בהמשך"
                },
                title = next.title,
                sub = null,
                titleSize = 1.9f,
                u = u,
                focusable = false,
                modifier = Modifier.fillMaxWidth().height(12.9f * u),
                onClick = onClick,
            )
            Box(Modifier.padding(top = 0.6f * u).fillMaxWidth().height(3 * u), contentAlignment = Alignment.Center) {
                Text(range(next), color = Color.White, fontSize = 1.7f.u(u), fontWeight = FontWeight.Medium)
            }
        }
        if (offset < MAX_OFFSET) {
            Icon(
                if (rtl) Icons.Rounded.ChevronLeft else Icons.Rounded.ChevronRight,
                "התכנית הבאה",
                Modifier
                    .padding(start = 0.4f * u, bottom = 13 * u)
                    .size(3.4f * u)
                    .focusProperties { canFocus = false }
                    .clickable(remember { MutableInteractionSource() }, null, role = Role.Button, onClick = onClick),
                tint = Color.White,
            )
        }
    }
}

@Composable
private fun ProgrammeCard(
    poster: String,
    logo: String,
    label: String,
    title: String,
    sub: String?,
    titleSize: Float,
    u: Dp,
    modifier: Modifier,
    focusable: Boolean = true,
    onClick: () -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    val shape = RoundedCornerShape(0.9f * u)
    Box(
        modifier
            .then(if (focused) Modifier.border(0.3f * u, FOCUS_RING, shape) else Modifier)
            .clip(shape)
            .background(CARD_BG)
            .focusProperties { canFocus = focusable }
            .clickable(interaction, null, role = Role.Button, onClick = onClick),
    ) {
        // the channel logo on the dark card; a programme picture, when the guide has one, covers it
        BoxWithConstraints(Modifier.fillMaxSize()) {
            ChannelLogo(
                logo,
                Modifier
                    .offset(x = maxWidth * 0.25f, y = maxHeight * 0.12f)
                    .width(maxWidth * 0.5f)
                    .height(maxHeight * 0.42f),
            )
        }
        if (poster.isNotEmpty()) {
            AsyncImage(poster, null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        }
        Box(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.7f)
                .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.92f)))),
        )
        Column(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 1.2f * u, vertical = 0.9f * u),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(label, color = CAPTION_GREY, fontSize = 1.5f.u(u), maxLines = 1, textAlign = TextAlign.Center)
            Text(
                title,
                color = Color.White,
                fontSize = titleSize.u(u),
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
            )
            if (sub != null) {
                Text(sub, color = Color.White, fontSize = 1.5f.u(u), maxLines = 1, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
private fun OsdButton(text: String, u: Dp, onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    Box(
        Modifier
            .widthIn(min = 13 * u)
            .height(4.6f * u)
            .clip(RoundedCornerShape(0.7f * u))
            .background(if (focused) Color(0xFFF5F5F5) else Color.White.copy(alpha = 0.15f))
            .clickable(interaction, null, role = Role.Button, onClick = onClick)
            .padding(horizontal = 2 * u),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, color = if (focused) Color(0xFF111111) else Color.White, fontSize = 1.75f.u(u), fontWeight = FontWeight.Medium, maxLines = 1)
    }
}

/** Previous channel, the current one (larger, full opacity), the next one: number, logo, name. */
@Composable
private fun ChannelStrip(
    channels: List<Channel>,
    currentIndex: Int,
    logo: (Channel) -> String,
    u: Dp,
    onPick: (Int) -> Unit,
    modifier: Modifier,
) {
    val n = channels.size
    val rows = listOf((currentIndex - 1 + n) % n, currentIndex, (currentIndex + 1) % n)
    Column(modifier) {
        rows.forEachIndexed { i, idx ->
            if (i > 0) Spacer(Modifier.height(1.5f * u))
            StripItem(channels[idx], logo(channels[idx]), mid = idx == currentIndex, u = u) { onPick(idx) }
        }
    }
}

@Composable
private fun StripItem(ch: Channel, logoUrl: String, mid: Boolean, u: Dp, onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    val shape = RoundedCornerShape(u)
    Row(
        Modifier
            .fillMaxWidth()
            .alpha(if (mid || focused) 1f else 0.45f)
            .clip(shape)
            .background(if (focused) Color.White.copy(alpha = 0.1f) else Color.Transparent)
            .then(if (focused) Modifier.border(0.22f * u, FOCUS_RING, shape) else Modifier)
            .clickable(interaction, null, role = Role.Button, onClick = onClick)
            .padding(horizontal = 1.2f * u, vertical = if (mid) 1.2f * u else 0.8f * u),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            if (ch.number > 0) ch.number.toString() else "",
            Modifier.width(4.4f * u),
            color = Color.White,
            fontSize = 1.8f.u(u),
            fontWeight = FontWeight.Medium,
        )
        Column(Modifier.weight(1f)) {
            Box(Modifier.heightIn(max = if (mid) 4.4f * u else 3 * u).height(if (mid) 4.4f * u else 3 * u)) {
                ChannelLogo(logoUrl, Modifier.fillMaxHeight().width(if (mid) 9 * u else 6 * u), ch.isRadio)
            }
            Text(
                ch.name,
                color = if (mid) Color.White else NAME_GREY,
                fontSize = (if (mid) 1.65f else 1.4f).u(u),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

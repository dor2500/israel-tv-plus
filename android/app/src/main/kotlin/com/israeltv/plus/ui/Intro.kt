package com.israeltv.plus.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * The cold-start intro, two stages.
 * 1. Chromatic pulse: pitch black, an ambient glow in the profile's seed colour, the emblem (the app icon's rounded
 *    play triangle and red live dot) drawing itself in, then filling, with a specular light band sweeping across it.
 *    The launch sound plays with it (PhoneActivity).
 * 2. Portal: the play triangle grows exponentially on a low-bouncy spring and becomes a window onto the app – the
 *    full-bleed hero – while the home rows cascade up ([onPortal] starts the cascade). A tap skips to stage 2.
 */
@Composable
fun CinematicIntro(seed: Color, ready: Boolean, onPortal: () -> Unit, onDone: () -> Unit) {
    val glow = remember { Animatable(0f) }
    val draw = remember { Animatable(0f) }
    val fill = remember { Animatable(0f) }
    val sweep = remember { Animatable(-0.6f) }
    val portal = remember { Animatable(1f) }
    var skip by remember { mutableStateOf(false) }
    val isReady by rememberUpdatedState(ready)

    LaunchedEffect(Unit) {
        coroutineScope {
            val stage1 = launch {
                launch { glow.animateTo(1f, tween(700, easing = FastOutSlowInEasing)) }
                launch { delay(120); draw.animateTo(1f, tween(820, easing = FastOutSlowInEasing)) }
                launch { delay(640); fill.animateTo(1f, tween(380)) }
                launch { delay(760); sweep.animateTo(1.6f, tween(900, easing = LinearEasing)) }
            }
            val minimum = launch { delay(1650) }
            val skipper = launch {
                snapshotFlow { skip }.first { it }
                stage1.cancel(); minimum.cancel()
            }
            minimum.join()
            skipper.cancel()
        }
        draw.snapTo(1f); fill.snapTo(1f)
        // never open onto an empty screen: wait for the channel list (PhoneActivity gives up after 2.5 s anyway)
        snapshotFlow { isReady }.first { it }
        onPortal()
        portal.animateTo(
            42f,
            spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessVeryLow, visibilityThreshold = 0.05f),
            initialVelocity = 6f,
        ) { if (value > 36f) onDone() }
        onDone()
    }

    Box(
        Modifier
            .fillMaxSize()
            .clickable(remember { MutableInteractionSource() }, null) { skip = true }
    ) {
        Canvas(Modifier.fillMaxSize().graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }) {
            val s = portal.value
            val opening = ((s - 1f) / 3f).coerceIn(0f, 1f)        // 0 -> 1 during the first part of the portal
            val unit = 120.dp.toPx()
            val center = Offset(size.width / 2f, size.height * 0.46f)
            val tri = playTriangle(center, unit)

            drawRect(Color.Black)
            // stage 1: glow, emblem, sweep (fading out as the portal opens)
            val a = 1f - opening
            drawCircle(
                Brush.radialGradient(listOf(seed.copy(alpha = 0.6f * glow.value * a), seed.copy(alpha = 0.18f * glow.value * a), Color.Transparent), center, size.width * 0.62f),
                size.width * 0.62f, center,
            )
            if (s < 1.02f) emblem(tri, center, unit, draw.value, fill.value, sweep.value)
            // stage 2: the triangle cuts a growing window through the black
            if (s > 1.001f) {
                scale(s, center) {
                    drawPath(tri, Color.Black, style = Fill, blendMode = BlendMode.Clear)
                }
            }
        }
        Text(
            "ישראל TV+‎",
            Modifier
                .align(Alignment.Center)
                .padding(top = 240.dp)
                .graphicsLayer { alpha = (fill.value * (2f - portal.value).coerceIn(0f, 1f)) },
            color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Black, letterSpacing = (-0.4).sp,
        )
    }
}

/** The app icon's play triangle, pointing right, its centroid at [c]; the corners are rounded when drawn. */
private fun playTriangle(c: Offset, unit: Float): Path = Path().apply {
    val h = unit * 1.1f
    val w = unit * 0.98f
    val left = c.x - w / 3f
    moveTo(left, c.y - h / 2f)
    lineTo(left + w, c.y)
    lineTo(left, c.y + h / 2f)
    close()
}

private fun DrawScope.emblem(tri: Path, c: Offset, unit: Float, draw: Float, fill: Float, sweep: Float) {
    val corners = PathEffect.cornerPathEffect(unit * 0.16f)
    // the outline drawing itself in
    if (draw > 0f && fill < 1f) {
        val measure = PathMeasure().apply { setPath(tri, false) }
        val part = Path()
        measure.getSegment(0f, measure.length * draw, part, true)
        drawPath(
            part, Color.White.copy(alpha = 1f - fill),
            style = Stroke(width = unit * 0.06f, cap = StrokeCap.Round, pathEffect = corners),
        )
    }
    if (fill > 0f) {
        // a rounded fill: the same path stroked thick with round corners plus filled
        val brush = Brush.linearGradient(listOf(Color.White, Color(0xFFDCE3FF)), Offset(c.x - unit / 2, c.y - unit / 2), Offset(c.x + unit / 2, c.y + unit / 2))
        drawPath(tri, brush, alpha = fill, style = Stroke(width = unit * 0.12f, pathEffect = corners))
        drawPath(tri, brush, alpha = fill)
        clipPath(tri) {
            val w = unit * 2f
            val x = c.x - unit + w * sweep
            drawRect(
                Brush.linearGradient(
                    listOf(Color.Transparent, Color.White.copy(alpha = 0.0f), Color(0xFF9DB7FF).copy(alpha = 0.55f), Color.Transparent),
                    Offset(x - unit * 0.5f, c.y - unit), Offset(x + unit * 0.3f, c.y + unit),
                )
            )
        }
        // the red live dot pops in
        val r = unit * 0.13f * fill
        val dot = Offset(c.x + unit * 0.52f, c.y - unit * 0.5f)
        drawCircle(Color(0xFF14141C), r * 1.35f, dot)
        drawCircle(Color(0xFFFF3B30), r, dot)
    }
}

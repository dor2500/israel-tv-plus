package com.israeltv.plus.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay

/**
 * Overlay state machine for the Zomato "רק ישראלי" experience.
 *
 * Hidden -> Entering (the full-screen showcase, ~1.1 s) -> Active (the invisible anchor while the filter is on).
 * Turning the filter off goes to Exiting, which flies the badge from the screen centre into the switch.
 */
enum class IsraeliOverlayMode {
    Hidden,
    Entering,
    Active,
    Exiting,
}

/**
 * The Apple-iOS-style flag of the reference: a clean rounded white capsule, two horizontal blue stripes and a vector
 * Magen David (res/drawable/flag_israel_apple.xml - never the Unicode flag emoji).
 */
@Composable
fun AppleIsraelFlag(modifier: Modifier = Modifier) {
    androidx.compose.foundation.Image(
        painter = androidx.compose.ui.res.painterResource(com.israeltv.plus.R.drawable.flag_israel_apple),
        contentDescription = "דגל ישראל",
        modifier = modifier.clip(RoundedCornerShape(4.dp)),
    )
}

/**
 * Zomato-style full-screen overlay for "רק ישראלי":
 * 1. True full-screen coverage (Modifier.fillMaxSize().zIndex(100f)) above header, categories, and bottom bar.
 * 2. Fluid pulsing ripples in the flag's blue and white expand from the centre during the showcase.
 * 3. The centre badge says "100% ישראלי" around the vector flag.
 * 4. The subtext "עושים כבוד ליצירה המקומית... מציג רק תוכן ישראלי" uses Large/Display typography as a bold hero statement
 *    with the Apple iOS vector flag drawable.
 * 5. Turning the filter off uses an updateTransition to scale down and fly back into the Switch's spatial coordinates.
 */
@Composable
fun IsraeliOnlyZomatoOverlay(
    mode: IsraeliOverlayMode,
    switchCenter: Offset? = null,
    onDismiss: () -> Unit,
) {
    if (mode == IsraeliOverlayMode.Hidden) return

    val scheme = MaterialTheme.colorScheme
    val density = LocalDensity.current

    val transition = updateTransition(targetState = mode, label = "spatial_zomato_transition")
    val flightSpec = spring<Float>(dampingRatio = 0.8f, stiffness = Spring.StiffnessMediumLow)
    val exitScale by transition.animateFloat(
        transitionSpec = { if (targetState == IsraeliOverlayMode.Exiting) flightSpec else tween(0) },
        label = "badge_flight_scale",
    ) { state -> if (state == IsraeliOverlayMode.Exiting) 0.05f else 1f }
    val exitAlpha by transition.animateFloat(
        transitionSpec = { tween(420) },
        label = "badge_flight_alpha",
    ) { state -> if (state == IsraeliOverlayMode.Exiting) 0f else 1f }

    val entrance = remember { Animatable(0f) }
    LaunchedEffect(mode) {
        when (mode) {
            IsraeliOverlayMode.Entering -> {
                entrance.snapTo(0f)
                entrance.animateTo(1f, spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessMediumLow))
                delay(1050)
                onDismiss()
            }
            IsraeliOverlayMode.Active -> entrance.snapTo(1f)
            IsraeliOverlayMode.Exiting -> {
                entrance.snapTo(1f)
                delay(640)
                onDismiss()
            }
            IsraeliOverlayMode.Hidden -> Unit
        }
    }

    BoxWithConstraints(Modifier.fillMaxSize().zIndex(100f)) {
        val screenW = with(density) { maxWidth.toPx() }
        val screenH = with(density) { maxHeight.toPx() }
        val targetX = (switchCenter?.x ?: (screenW * 0.5f)) - screenW / 2f
        val targetY = (switchCenter?.y ?: (screenH * 0.82f)) - screenH / 2f
        val exitX by transition.animateFloat(
            transitionSpec = { if (targetState == IsraeliOverlayMode.Exiting) flightSpec else tween(0) },
            label = "badge_flight_x",
        ) { state -> if (state == IsraeliOverlayMode.Exiting) targetX else 0f }
        val exitY by transition.animateFloat(
            transitionSpec = { if (targetState == IsraeliOverlayMode.Exiting) flightSpec else tween(0) },
            label = "badge_flight_y",
        ) { state -> if (state == IsraeliOverlayMode.Exiting) targetY else 0f }

        AnimatedVisibility(
            visible = mode == IsraeliOverlayMode.Entering,
            modifier = Modifier.fillMaxSize(),
            enter = fadeIn(tween(280)),
            exit = fadeOut(tween(220)),
        ) {
            IsraeliShowcaseBackdrop()
        }

        val hiddenAnchor = mode == IsraeliOverlayMode.Active
        Box(
            modifier = Modifier.fillMaxSize().graphicsLayer {
                val scale = entrance.value * exitScale
                scaleX = scale
                scaleY = scale
                translationX = exitX
                translationY = exitY
                alpha = if (hiddenAnchor) 0f else entrance.value * exitAlpha
            },
            contentAlignment = Alignment.Center,
        ) {
            Box(
                Modifier
                    .size(148.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                scheme.primaryContainer,
                                Color(0xFF0B1938),
                            )
                        )
                    )
                    .border(3.dp, Color(0xFF38BDF8).copy(alpha = 0.75f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    AppleIsraelFlag(Modifier.size(width = 44.dp, height = 30.dp))
                    Spacer(Modifier.height(7.dp))
                    Text(
                        "100% ישראלי",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = mode == IsraeliOverlayMode.Entering,
            modifier = Modifier.align(Alignment.Center).offset(y = 148.dp),
            enter = fadeIn(tween(380)),
            exit = fadeOut(tween(180)),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "עושים כבוד ליצירה המקומית...\nמציג רק תוכן ישראלי",
                    style = MaterialTheme.typography.displaySmall,
                    color = Color.White,
                    fontSize = 26.sp,
                    lineHeight = 34.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(14.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    AppleIsraelFlag(Modifier.size(width = 30.dp, height = 21.dp))
                }
            }
        }
    }
}

/** The deep, flag-coloured canvas with three fluid ripple rings that bloom out of the centre. */
@Composable
private fun IsraeliShowcaseBackdrop() {
    val scheme = MaterialTheme.colorScheme
    val ripple by rememberInfiniteTransition(label = "israeli_fluid_ripples").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(1400, easing = LinearEasing), repeatMode = RepeatMode.Restart),
        label = "fluid_ripple_progress",
    )
    Box(
        Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF060B19).copy(alpha = 0.98f),
                        scheme.background.copy(alpha = 0.99f),
                        Color(0xFF05070E),
                    )
                )
            )
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val maxRadius = size.width.coerceAtLeast(size.height) * 0.72f
            val baseRadius = 64.dp.toPx()
            for (i in 0..2) {
                val phase = (ripple + i * 0.33f) % 1f
                val r = baseRadius + phase * (maxRadius - baseRadius)
                val alpha = (1f - phase).coerceIn(0f, 1f) * 0.55f
                drawCircle(
                    brush = Brush.radialGradient(
                        0.0f to Color(0xFF0038B8).copy(alpha = alpha * 0.36f),
                        0.45f to Color.White.copy(alpha = alpha * 0.18f),
                        0.80f to Color(0xFF0A84FF).copy(alpha = alpha * 0.10f),
                        1.0f to Color.Transparent,
                        center = center,
                        radius = r,
                    ),
                    radius = r,
                    center = center,
                )
                drawCircle(
                    color = Color(0xFF38BDF8).copy(alpha = alpha * 0.65f),
                    radius = r,
                    center = center,
                    style = Stroke(width = 2.5.dp.toPx()),
                )
            }
        }
    }
}

/** Backwards-compatible overload. */
@Composable
fun IsraeliOnlyZomatoOverlay(
    visible: Boolean,
    switchCenter: Offset? = null,
    onDismiss: () -> Unit,
) {
    IsraeliOnlyZomatoOverlay(
        mode = if (visible) IsraeliOverlayMode.Entering else IsraeliOverlayMode.Hidden,
        switchCenter = switchCenter,
        onDismiss = onDismiss,
    )
}

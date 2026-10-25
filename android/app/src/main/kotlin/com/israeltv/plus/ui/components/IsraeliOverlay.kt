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
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.width
import androidx.compose.ui.platform.LocalDensity

/**
 * Overlay state machine for the Zomato "רק ישראלי" experience.
 */
enum class IsraeliOverlayMode {
    Hidden,
    Entering,
    Exiting,
}

/**
 * Premium Apple TV / iOS style Israeli flag asset drawn with pure vector geometry.
 * Clean rounded white capsule with two horizontal blue stripes and the central Magen David.
 */
@Composable
fun AppleIsraelFlag(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val blue = Color(0xFF0038B8)
        val corner = 3.dp.toPx()

        // 1. Crisp white flag base with subtle rounded corners
        drawRoundRect(
            color = Color.White,
            size = Size(w, h),
            cornerRadius = CornerRadius(corner, corner),
        )

        // 2. Specular border (subtle Apple glass edge)
        drawRoundRect(
            color = Color(0x33000000),
            size = Size(w, h),
            cornerRadius = CornerRadius(corner, corner),
            style = Stroke(width = 1.dp.toPx()),
        )

        // 3. Top and bottom royal blue stripes
        val stripeHeight = h * 0.14f
        val topStripeY = h * 0.13f
        val bottomStripeY = h * (1f - 0.13f - 0.14f)

        drawRect(
            color = blue,
            topLeft = Offset(0f, topStripeY),
            size = Size(w, stripeHeight),
        )
        drawRect(
            color = blue,
            topLeft = Offset(0f, bottomStripeY),
            size = Size(w, stripeHeight),
        )

        // 4. Center Star of David (Magen David)
        val cx = w / 2f
        val cy = h / 2f
        val r = h * 0.22f
        val strokeW = (h * 0.045f).coerceAtLeast(1.5f)

        fun trianglePath(pointUp: Boolean): Path {
            val path = Path()
            val startAngle = if (pointUp) -Math.PI / 2 else Math.PI / 2
            val p1 = Offset(
                (cx + r * cos(startAngle)).toFloat(),
                (cy + r * sin(startAngle)).toFloat(),
            )
            val p2 = Offset(
                (cx + r * cos(startAngle + 2 * Math.PI / 3)).toFloat(),
                (cy + r * sin(startAngle + 2 * Math.PI / 3)).toFloat(),
            )
            val p3 = Offset(
                (cx + r * cos(startAngle + 4 * Math.PI / 3)).toFloat(),
                (cy + r * sin(startAngle + 4 * Math.PI / 3)).toFloat(),
            )
            path.moveTo(p1.x, p1.y)
            path.lineTo(p2.x, p2.y)
            path.lineTo(p3.x, p3.y)
            path.close()
            return path
        }

        val strokeStyle = Stroke(
            width = strokeW,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round,
        )
        drawPath(trianglePath(pointUp = true), blue, style = strokeStyle)
        drawPath(trianglePath(pointUp = false), blue, style = strokeStyle)
    }
}

/**
 * Upgraded Zomato-Style Overlay for "רק ישראלי":
 * 1. Fluid pulsing background ripples with a subtle blue/white gradient (Israeli flag motif).
 * 2. Center Badge: "100% ישראלי" with Apple flag.
 * 3. Inline Apple iOS style vector flag Image composable inside subtext (Zero "🇮🇱" emoji string).
 * 4. SPATIAL REVERT ANIMATION (Toggle OFF): When turned OFF, the full-screen overlay does NOT just fade out.
 *    The center badge and overlay visually scale down and translate (fly back) directly INTO the exact
 *    spatial coordinates of the Switch using updateTransition and spatial bounds.
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

    // Ripples pulse animation (infinite loop during active state)
    val infiniteTransition = rememberInfiniteTransition(label = "israeli_fluid_ripples")
    val rippleProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "fluid_ripple_progress",
    )

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(100f)
    ) {
        val screenW = with(density) { maxWidth.toPx() }
        val screenH = with(density) { maxHeight.toPx() }
        val screenCenterX = screenW / 2f
        val screenCenterY = screenH / 2f

        // Calculate spatial vector to the Switch toggle coordinates
        val targetTx = if (switchCenter != null) switchCenter.x - screenCenterX else (screenW * 0.35f)
        val targetTy = if (switchCenter != null) switchCenter.y - screenCenterY else (-screenH * 0.35f)

        // updateTransition manages spatial state transition
        val transition = updateTransition(targetState = mode, label = "spatial_zomato_transition")

        val badgeScale by transition.animateFloat(
            transitionSpec = {
                when {
                    IsraeliOverlayMode.Hidden isTransitioningTo IsraeliOverlayMode.Entering ->
                        spring(dampingRatio = 0.52f, stiffness = Spring.StiffnessMediumLow)
                    targetState == IsraeliOverlayMode.Exiting ->
                        spring(dampingRatio = 0.75f, stiffness = Spring.StiffnessMedium)
                    else -> tween(300)
                }
            },
            label = "badge_scale",
        ) { state ->
            when (state) {
                IsraeliOverlayMode.Hidden -> 0f
                IsraeliOverlayMode.Entering -> 1f
                IsraeliOverlayMode.Exiting -> 0.04f
            }
        }

        val translationX by transition.animateFloat(
            transitionSpec = {
                if (targetState == IsraeliOverlayMode.Exiting) {
                    spring(dampingRatio = 0.75f, stiffness = Spring.StiffnessMedium)
                } else {
                    tween(300)
                }
            },
            label = "spatial_translation_x",
        ) { state ->
            if (state == IsraeliOverlayMode.Exiting) targetTx else 0f
        }

        val translationY by transition.animateFloat(
            transitionSpec = {
                if (targetState == IsraeliOverlayMode.Exiting) {
                    spring(dampingRatio = 0.75f, stiffness = Spring.StiffnessMedium)
                } else {
                    tween(300)
                }
            },
            label = "spatial_translation_y",
        ) { state ->
            if (state == IsraeliOverlayMode.Exiting) targetTy else 0f
        }

        val bgAlpha by transition.animateFloat(
            transitionSpec = { tween(320) },
            label = "bg_alpha",
        ) { state ->
            when (state) {
                IsraeliOverlayMode.Hidden -> 0f
                IsraeliOverlayMode.Entering -> 0.96f
                IsraeliOverlayMode.Exiting -> 0f
            }
        }

        val contentAlpha by transition.animateFloat(
            transitionSpec = { tween(280) },
            label = "content_alpha",
        ) { state ->
            when (state) {
                IsraeliOverlayMode.Hidden -> 0f
                IsraeliOverlayMode.Entering -> 1f
                IsraeliOverlayMode.Exiting -> 0.15f
            }
        }

        // Auto-dismiss timers
        LaunchedEffect(mode) {
            when (mode) {
                IsraeliOverlayMode.Entering -> {
                    // Full showcase experience (1400ms), then auto dismiss
                    delay(1400)
                    onDismiss()
                }
                IsraeliOverlayMode.Exiting -> {
                    // Wait for spatial fly-back animation to finish into switch bounds
                    delay(360)
                    onDismiss()
                }
                IsraeliOverlayMode.Hidden -> Unit
            }
        }

        // Fullscreen backdrop with fluid blue/white gradient ripples
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = bgAlpha }
                .background(
                    Brush.verticalGradient(
                        listOf(
                            scheme.background.copy(alpha = 0.98f),
                            Color(0xFF07080E),
                        )
                    )
                )
                .clickable(remember { MutableInteractionSource() }, indication = null) {},
        ) {
            // Fluid pulsing background ripples (Israeli flag motif: Royal Blue & Pure White)
            Canvas(Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val maxRadius = size.width.coerceAtLeast(size.height) * 0.7f
                val baseRadius = 60.dp.toPx()

                for (i in 0..2) {
                    val phase = (rippleProgress + i * 0.33f) % 1f
                    val r = baseRadius + phase * (maxRadius - baseRadius)
                    val alpha = (1f - phase).coerceIn(0f, 1f) * 0.5f

                    // 1. Fluid radial gradient pool (Royal Blue & Pure White flag motif)
                    drawCircle(
                        brush = Brush.radialGradient(
                            0.0f to Color(0xFF0038B8).copy(alpha = alpha * 0.32f),
                            0.45f to Color.White.copy(alpha = alpha * 0.16f),
                            0.80f to Color(0xFF0A84FF).copy(alpha = alpha * 0.08f),
                            1.0f to Color.Transparent,
                            center = center,
                            radius = r,
                        ),
                        radius = r,
                        center = center,
                    )

                    // 2. Soft fluid glow ring
                    drawCircle(
                        color = Color(0xFF0A84FF).copy(alpha = alpha * 0.6f),
                        radius = r,
                        center = center,
                        style = Stroke(width = 2.5.dp.toPx()),
                    )
                }
            }
        }

        // Center Content & Badge: Bound to spatial translation and scale
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    this.scaleX = badgeScale
                    this.scaleY = badgeScale
                    this.translationX = translationX
                    this.translationY = translationY
                    this.alpha = contentAlpha
                },
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 24.dp),
            ) {
                // Center Circular Badge: "100% ישראלי"
                Box(
                    modifier = Modifier
                        .size(136.dp)
                        .clip(CircleShape)
                        .background(scheme.primaryContainer)
                        .border(2.5.dp, Color(0xFF0A84FF).copy(alpha = 0.6f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        AppleIsraelFlag(
                            Modifier
                                .size(width = 38.dp, height = 26.dp)
                                .padding(bottom = 2.dp)
                        )
                        Spacer(Modifier.height(5.dp))
                        Text(
                            text = "100% ישראלי",
                            color = scheme.onPrimaryContainer,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center,
                        )
                    }
                }

                Spacer(Modifier.height(26.dp))

                // Subtext with inline iOS-style Apple Flag (NO emoji string)
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = "עושים כבוד ליצירה המקומית... מציג רק תוכן ישראלי",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.width(8.dp))
                    AppleIsraelFlag(
                        Modifier
                            .size(width = 24.dp, height = 16.dp)
                            .clip(RoundedCornerShape(3.dp))
                    )
                }
            }
        }
    }
}

/** Backwards-compatible overload */
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

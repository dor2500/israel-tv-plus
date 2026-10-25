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
 * Immersive Zomato-Style Overlay Animation for "רק ישראלי" toggle:
 * - Fullscreen overlay at zIndex(100f) that blocks UI for ~1500ms before fading out
 * - Deep cinematic gradient background
 * - Concentric circular radar ripples expanding outward 0f to 2.5f
 * - Circular badge scaling up using spring(dampingRatio = 0.5f) with "100% ישראלי"
 * - Subtext with Apple-style 🇮🇱 flag
 */
@Composable
fun IsraeliOnlyZomatoOverlay(
    visible: Boolean,
    onDismiss: () -> Unit,
) {
    if (!visible) return

    val scheme = MaterialTheme.colorScheme
    val badgeScale = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // 1. Badge springs up with bouncy spring (dampingRatio = 0.5f)
        badgeScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = 0.5f,
                stiffness = Spring.StiffnessMediumLow,
            ),
        )
        // 2. Subtext fades in
        textAlpha.animateTo(1f, tween(300))
        // 3. Keep for full 1500ms total experience, then dismiss
        delay(900)
        onDismiss()
    }

    // Concentric Ripples animation
    val infiniteTransition = rememberInfiniteTransition(label = "zomato_ripples")
    val rippleProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "ripple_progress",
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(100f)
            .background(
                Brush.verticalGradient(
                    listOf(
                        scheme.background.copy(alpha = 0.98f),
                        Color(0xFF0A0B10),
                    )
                )
            )
            .clickable(remember { MutableInteractionSource() }, indication = null) {},
        contentAlignment = Alignment.Center,
    ) {
        // Radar pulse concentric ripples (2-3 expanding rings)
        Canvas(Modifier.size(340.dp)) {
            val maxR = size.width / 2f
            val baseR = 64.dp.toPx()

            for (i in 0..2) {
                val phase = (rippleProgress + i * 0.33f) % 1f
                val scale = 1f + phase * 1.5f // 1.0 -> 2.5
                val currentR = baseR * scale
                val alpha = (1f - phase).coerceIn(0f, 1f) * 0.5f

                drawCircle(
                    color = scheme.primary.copy(alpha = alpha),
                    radius = currentR.coerceAtMost(maxR),
                    style = Stroke(width = 2.5.dp.toPx()),
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 32.dp),
        ) {
            // Center Circular Badge
            Box(
                modifier = Modifier
                    .size(136.dp)
                    .graphicsLayer {
                        scaleX = badgeScale.value
                        scaleY = badgeScale.value
                    }
                    .clip(CircleShape)
                    .background(scheme.primaryContainer)
                    .border(2.dp, scheme.primary.copy(alpha = 0.5f), CircleShape),
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
                    Spacer(Modifier.height(4.dp))
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

            // Subtext with inline iOS-style Apple Flag
            Row(
                modifier = Modifier
                    .graphicsLayer { alpha = textAlpha.value }
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "עושים כבוד ליצירה המקומית... מציג רק תוכן ישראלי",
                    color = scheme.onBackground,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.size(8.dp))
                AppleIsraelFlag(Modifier.size(width = 24.dp, height = 16.dp))
            }
        }
    }
}

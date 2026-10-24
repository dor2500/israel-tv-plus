package com.israeltv.plus.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay

/**
 * Next-Gen Cinematic Splash & Startup Animation (Two-Stage Flagship Intro):
 * Stage 1: Chromatic Pulse & ambient radial glow behind logo using active Material You seed color,
 *          specular light sweep across emblem.
 * Stage 2: Logo's play triangle expands exponentially with spring velocity acting as a portal zooming directly
 *          into the full-bleed Hero Banner.
 */
@Composable
fun CinematicSplashPortal(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scheme = MaterialTheme.colorScheme

    val scale = remember { Animatable(1f) }
    val portalAlpha = remember { Animatable(1f) }

    // Specular light sweep infinite translation
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val sweepOffset by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = LinearEasing),
        ),
        label = "sweep",
    )

    LaunchedEffect(Unit) {
        // Stage 1: Chromatic Pulse & Specular Sweep
        delay(900)

        // Stage 2: Portal Reveal – Logo play triangle expands exponentially
        scale.animateTo(
            targetValue = 18f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessLow,
            ),
        )
        portalAlpha.animateTo(0f, animationSpec = tween(250))
        onFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .zIndex(999f)
            .alpha(portalAlpha.value)
            .background(Color.Black),
        contentAlignment = Alignment.Center,
    ) {
        // Ambient Radial Glow behind the logo using active seed color
        Box(
            modifier = Modifier
                .size(280.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            scheme.primary.copy(alpha = 0.45f),
                            scheme.tertiary.copy(alpha = 0.2f),
                            Color.Transparent,
                        ),
                    )
                ),
        )

        // Emblem with Play Triangle Portal
        Box(
            modifier = Modifier
                .size(100.dp)
                .scale(scale.value),
            contentAlignment = Alignment.Center,
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Play Triangle Path
                val triangle = Path().apply {
                    moveTo(w * 0.32f, h * 0.25f)
                    lineTo(w * 0.78f, h * 0.5f)
                    lineTo(w * 0.32f, h * 0.75f)
                    close()
                }

                // Shimmering Specular Light Sweep LinearGradient
                val sweepBrush = Brush.linearGradient(
                    colors = listOf(
                        scheme.primary,
                        Color.White,
                        scheme.primary,
                    ),
                    start = Offset(w * sweepOffset, 0f),
                    end = Offset(w * (sweepOffset + 0.8f), h),
                )

                drawPath(
                    path = triangle,
                    brush = sweepBrush,
                )

                // Glowing outer stroke
                drawPath(
                    path = triangle,
                    color = Color.White.copy(alpha = 0.6f),
                    style = Stroke(width = 3.dp.toPx()),
                )
            }
        }
    }
}

package com.israeltv.plus.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.blur.HazeBlurStyle
import dev.chrisbanes.haze.blur.HazeColorEffect
import dev.chrisbanes.haze.blur.hazeBlur

/** The screen content the glass bars blur (hazeSource on the feed). Null where nothing is behind (player). */
val LocalHaze = staticCompositionLocalOf<HazeState?> { null }

/** Apple TV specular edge: a 1 dp stroke that is bright on top and fades out downwards. */
fun Modifier.specularBorder(shape: Shape, width: Dp = 1.dp, alpha: Float = 0.28f): Modifier =
    border(width, Brush.verticalGradient(listOf(Color.White.copy(alpha = alpha), Color.Transparent)), shape)

/**
 * Frosted glass: Real-time heavy backdrop blur refracting scrolling artwork underneath,
 * using the flagship Glass Surface Recipe: Color(0xFF14141C).copy(alpha = 0.62f) mixed with
 * MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.18f), topped with a crisp 1.dp specular border.
 */
fun Modifier.frostedGlass(
    shape: Shape,
    tint: Color? = null,
    blur: Dp = 90.dp,
    border: Boolean = true,
    mask: Brush? = null,
): Modifier = composed {
    val haze = LocalHaze.current
    val base = MaterialTheme.colorScheme.surfaceContainerLowest
    val defaultGlassTint = Color(0xFF14141C).copy(alpha = 0.62f)
    val tintMix = MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.18f)
    val effectiveTint = tint ?: defaultGlassTint

    val clipped = clip(shape)
    val glass = if (haze != null) {
        clipped.hazeBlur(
            HazeInput.Sources(haze),
            HazeBlurStyle {
                blurRadius(blur)
                backgroundColor(base)
                colorEffects(listOf(HazeColorEffect.tint(effectiveTint), HazeColorEffect.tint(tintMix)))
                noiseFactor(0.04f)
                if (mask != null) mask(mask)
            },
        )
    } else clipped.background(effectiveTint)
    if (border) glass.border(1.dp, Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.32f), Color.Transparent)), shape) else glass
}

/**
 * Press physics for cards: scale to 0.95 with a bouncy spring, a light tilt towards the finger (Apple TV parallax)
 * and a haptic tick. Pointer events are only observed (never consumed), so vertical drags still scroll the feed.
 */
fun Modifier.pressPhysics(interaction: MutableInteractionSource, tilt: Boolean = true): Modifier = composed {
    val pressed by interaction.collectIsPressedAsState()
    val haptic = LocalHapticFeedback.current
    LaunchedEffect(pressed) { if (pressed) haptic.performHapticFeedback(HapticFeedbackType.LongPress) }
    var touch by remember { mutableStateOf(Offset(0.5f, 0.5f)) }
    val bouncy = spring<Float>(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow)
    val scale by animateFloatAsState(if (pressed) 0.95f else 1f, bouncy, label = "press")
    val rotX by animateFloatAsState(if (pressed && tilt) (0.5f - touch.y) * 9f else 0f, bouncy, label = "tiltX")
    val rotY by animateFloatAsState(if (pressed && tilt) (touch.x - 0.5f) * 11f else 0f, bouncy, label = "tiltY")
    this
        .pointerInput(Unit) {
            awaitPointerEventScope {
                while (true) {
                    val e = awaitPointerEvent(PointerEventPass.Initial)
                    val c = e.changes.firstOrNull() ?: continue
                    if (size.width > 0 && size.height > 0) {
                        touch = Offset(c.position.x / size.width, c.position.y / size.height)
                    }
                }
            }
        }
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
            rotationX = rotX
            rotationY = rotY
            cameraDistance = 14f * density
        }
}

/** A circular frosted-glass icon button (hero, player). */
@Composable
fun GlassIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    tint: Color = Color.White.copy(alpha = 0.14f),
    iconSize: Dp = size * 0.46f,
) {
    val interaction = remember { MutableInteractionSource() }
    val haptic = LocalHapticFeedback.current
    Box(
        modifier
            .size(size)
            .pressPhysics(interaction, tilt = false)
            .frostedGlass(CircleShape, tint)
            .clickable(interaction, ripple()) {
                haptic.performHapticFeedback(HapticFeedbackType.VirtualKey)
                onClick()
            },
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription, Modifier.size(iconSize), tint = Color.White)
    }
}

/** VisionOS-style glass shimmer for loading placeholders: a soft light band sweeping across. */
fun Modifier.glassShimmer(shape: Shape): Modifier = composed {
    val t by rememberInfiniteTransition(label = "shimmer")
        .animateFloat(-1f, 2f, infiniteRepeatable(tween(1400)), label = "sweep")
    val base = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.55f)
    clip(shape)
        .background(base)
        .specularBorder(shape, alpha = 0.18f)
        .drawWithContent {
            drawContent()
            val w = size.width
            drawRect(
                Brush.linearGradient(
                    listOf(Color.Transparent, Color.White.copy(alpha = 0.10f), Color.Transparent),
                    start = Offset(w * (t - 0.5f), 0f),
                    end = Offset(w * t, size.height),
                )
            )
        }
}

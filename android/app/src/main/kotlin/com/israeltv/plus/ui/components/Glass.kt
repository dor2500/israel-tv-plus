package com.israeltv.plus.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.israeltv.plus.ui.theme.LocalDarkTheme
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.blur.HazeBlurStyle
import dev.chrisbanes.haze.blur.HazeColorEffect
import dev.chrisbanes.haze.blur.hazeBlur

/** The screen content the glass bars blur (hazeSource on the feed). Null where nothing is behind (player). */
val LocalHaze = staticCompositionLocalOf<HazeState?> { null }

/**
 * The glass of the bars and sheets: ultra-transparent clear glass (`alpha = 0.12f`) with subtle surface tint
 * on top of the real high-radius backdrop blur so scrolling content remains visibly glowing underneath.
 */
@Composable
fun glassTint(): Color {
    val tint = MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.08f)
    return tint.compositeOver(if (LocalDarkTheme.current) Color.Black.copy(alpha = 0.14f) else Color.White.copy(alpha = 0.24f))
}

/** Apple TV specular edge: a 1 dp stroke that is bright on top and fades out downwards. */
fun Modifier.specularBorder(shape: Shape, width: Dp = 1.dp, alpha: Float = 0.28f): Modifier =
    border(width, Brush.verticalGradient(listOf(Color.White.copy(alpha = alpha), Color.Transparent)), shape)

/**
 * Frosted glass: the content behind is blurred (real blur, Haze: RenderEffect on Android 12+, a tinted scrim
 * before), tinted with [tint], clipped to [shape], with the specular edge. [mask] fades the glass out (header).
 */
fun Modifier.frostedGlass(
    shape: Shape,
    tint: Color,
    blur: Dp = 36.dp,
    border: Boolean = true,
    mask: Brush? = null,
): Modifier = composed {
    val haze = LocalHaze.current
    val clipped = clip(shape)
    val glass = if (haze != null) {
        clipped.hazeBlur(
            HazeInput.Sources(haze),
            HazeBlurStyle {
                blurRadius(blur)
                backgroundColor(Color.Transparent)
                colorEffects(listOf(HazeColorEffect.tint(tint)))
                noiseFactor(0.03f)
                if (mask != null) mask(mask)
            },
        )
    } else clipped.background(tint)
    if (border) glass.specularBorder(shape, alpha = 0.32f) else glass
}

/**
 * Apple TV / tvOS touch & focus physics for interactive cards:
 * - On press: shrinks slightly to 0.94f with a smooth spring and subtle 3D tilt towards the finger, no Android ripple.
 * - On focus: scales up to 1.05f with a glowing ambient/spot shadow.
 * Pointer events are only observed (never consumed), so vertical drags still scroll the feed untouched.
 */
fun Modifier.pressPhysics(
    interaction: MutableInteractionSource,
    tilt: Boolean = true,
    glowShape: Shape? = null,
): Modifier = composed {
    val pressed by interaction.collectIsPressedAsState()
    val focused by interaction.collectIsFocusedAsState()
    val haptic = LocalHapticFeedback.current
    LaunchedEffect(pressed) { if (pressed) haptic.performHapticFeedback(HapticFeedbackType.LongPress) }
    var touch by remember { mutableStateOf(Offset(0.5f, 0.5f)) }
    val bouncy = spring<Float>(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow)
    val targetScale = when {
        pressed -> 0.94f
        focused -> 1.10f
        else -> 1f
    }
    val scale by animateFloatAsState(targetScale, bouncy, label = "pressScale")
    val glowElevation by animateFloatAsState(if (focused) 28f else 0f, bouncy, label = "focusGlow")
    val rotX by animateFloatAsState(if (pressed && tilt) (0.5f - touch.y) * 9f else 0f, bouncy, label = "tiltX")
    val rotY by animateFloatAsState(if (pressed && tilt) (touch.x - 0.5f) * 11f else 0f, bouncy, label = "tiltY")
    val glowColor = MaterialTheme.colorScheme.primary
    val focusShape = glowShape ?: RoundedCornerShape(22.dp)
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
            if (glowElevation > 0.5f) {
                shadowElevation = glowElevation * density
                shape = focusShape
                ambientShadowColor = glowColor.copy(alpha = 0.72f)
                spotShadowColor = Color.White.copy(alpha = 0.85f)
            }
        }
        .then(if (focused) Modifier.border(2.dp, Color.White, focusShape) else Modifier)
}

/** A frosted-glass icon button (hero, header, player) with Material 3 Expressive shape-morphing on press and spring bounce. */
@Composable
fun GlassIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    tint: Color = Color.White.copy(alpha = 0.14f),
    iconSize: Dp = size * 0.46f,
    iconTint: Color = Color.White,
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val cornerRadius by animateDpAsState(
        targetValue = if (pressed) (size * 0.28f) else (size / 2f),
        animationSpec = spring(dampingRatio = 0.58f, stiffness = Spring.StiffnessMedium),
        label = "glassBtnMorph",
    )
    val morphShape = RoundedCornerShape(cornerRadius)
    val haptic = LocalHapticFeedback.current
    Box(
        modifier
            .size(size)
            .pressPhysics(interaction, tilt = false, glowShape = morphShape)
            .frostedGlass(morphShape, tint)
            .clickable(interaction, null) {
                haptic.performHapticFeedback(HapticFeedbackType.VirtualKey)
                onClick()
            },
        contentAlignment = Alignment.Center,
    ) {
        BoldIcon(icon, contentDescription, Modifier.size(iconSize), tint = iconTint, weight = 0.55.dp)
    }
}

/** Dark-themed shimmer skeleton for loading content rows: deep obsidian surface with a soft specular sweep. */
fun Modifier.glassShimmer(shape: Shape): Modifier = composed {
    val t by rememberInfiniteTransition(label = "shimmer")
        .animateFloat(-1f, 2f, infiniteRepeatable(tween(1350)), label = "sweep")
    val dark = LocalDarkTheme.current
    val base = if (dark) Color(0xFF14141E).copy(alpha = 0.88f) else MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.65f)
    clip(shape)
        .background(base)
        .specularBorder(shape, alpha = 0.16f)
        .drawWithContent {
            drawContent()
            val w = size.width
            drawRect(
                Brush.linearGradient(
                    listOf(
                        Color.Transparent,
                        Color.White.copy(alpha = if (dark) 0.09f else 0.18f),
                        Color.White.copy(alpha = if (dark) 0.15f else 0.26f),
                        Color.White.copy(alpha = if (dark) 0.09f else 0.18f),
                        Color.Transparent,
                    ),
                    start = Offset(w * (t - 0.55f), 0f),
                    end = Offset(w * t, size.height),
                )
            )
        }
}

package com.israeltv.plus.ui.nuvio

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Press feedback shared by every Nuvio surface: scale and corner radius are springs, never tweens. */
internal object NuvioPress {
    const val Scale = 0.96f
    val RestRadius = 16.dp
    val PressedRadius = 28.dp
}

/**
 * BitChord touch feedback driven by [source]: scales to [NuvioPress.Scale] and morphs the corners from [restRadius]
 * to [pressedRadius] while held. Both values are read inside the graphicsLayer, so a press never recomposes.
 */
internal fun Modifier.nuvioPressFeedback(
    source: InteractionSource,
    restRadius: Dp = NuvioPress.RestRadius,
    pressedRadius: Dp = NuvioPress.PressedRadius,
): Modifier = composed {
    val pressed by source.collectIsPressedAsState()
    val progress by animateFloatAsState(
        targetValue = if (pressed) 1f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow),
        label = "nuvioPress",
    )
    graphicsLayer {
        val scale = 1f + (NuvioPress.Scale - 1f) * progress
        scaleX = scale
        scaleY = scale
        val radius = restRadius.toPx() + (pressedRadius.toPx() - restRadius.toPx()) * progress
        shape = RoundedCornerShape(radius)
        clip = true
    }
}

/** A click target with [nuvioPressFeedback]; no ripple, the motion is the feedback. */
internal fun Modifier.nuvioPressable(
    onClick: () -> Unit,
    restRadius: Dp = NuvioPress.RestRadius,
    pressedRadius: Dp = NuvioPress.PressedRadius,
): Modifier = composed {
    val source = remember { MutableInteractionSource() }
    nuvioPressFeedback(source, restRadius, pressedRadius)
        .clickable(interactionSource = source, indication = null, onClick = onClick)
}

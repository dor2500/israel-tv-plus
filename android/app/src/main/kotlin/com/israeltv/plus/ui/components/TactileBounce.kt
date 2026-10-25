package com.israeltv.plus.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback

/**
 * Pixel/Essentials-style tactile bounce animation & haptic feedback:
 * Scales down to 0.92f on down-press and springs back to 1.08f overshoot (dampingRatio = 0.4f).
 * Triggers crisp high-priority HapticFeedbackType.LongPress on press.
 */
fun Modifier.tactileBounce(
    interactionSource: MutableInteractionSource? = null,
    onClick: (() -> Unit)? = null,
): Modifier = composed {
    val interaction = interactionSource ?: remember { MutableInteractionSource() }
    val isPressed by interaction.collectIsPressedAsState()
    val haptic = LocalHapticFeedback.current

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(
            dampingRatio = 0.4f,
            stiffness = Spring.StiffnessMediumLow,
        ),
        label = "tactile_bounce",
    )

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .then(
            if (onClick != null) {
                Modifier.clickable(
                    interactionSource = interaction,
                    indication = null,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onClick()
                    },
                )
            } else Modifier
        )
}

/**
 * Helper to trigger mechanical notch tick haptics during scrubbing or slider drag.
 */
fun triggerScrubTick(haptic: HapticFeedback) {
    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
}

/**
 * Helper to trigger high-priority tactile thud/click for buttons.
 */
fun triggerBounceClick(haptic: HapticFeedback) {
    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
}

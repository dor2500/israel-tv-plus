package com.israeltv.plus.ui.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first

/** The app's SharedTransitionLayout and the AnimatedContent page the caller is on (null in previews). */
val LocalSharedScope = staticCompositionLocalOf<SharedTransitionScope?> { null }
val LocalPageScope = staticCompositionLocalOf<AnimatedVisibilityScope?> { null }

/** Container transform spring: a channel card grows into the player (and back). */
val ContainerTransform = BoundsTransform { _, _ ->
    spring(dampingRatio = 0.82f, stiffness = Spring.StiffnessMediumLow, visibilityThreshold = Rect.VisibilityThreshold)
}

/** Shared bounds between pages (a card and the player that grows out of it). No-op outside the shared layout. */
@Composable
fun Modifier.sharedContainer(key: String, shape: Shape, scaleContent: Boolean = false): Modifier {
    val shared = LocalSharedScope.current ?: return this
    val page = LocalPageScope.current ?: return this
    return with(shared) {
        this@sharedContainer.sharedBounds(
            rememberSharedContentState(key),
            page,
            boundsTransform = ContainerTransform,
            resizeMode = if (scaleContent) SharedTransitionScope.ResizeMode.scaleToBounds() else SharedTransitionScope.ResizeMode.RemeasureToBounds,
            clipInOverlayDuringTransition = OverlayClip(shape),
        )
    }
}

/** The same element on two pages (the profile avatar flying into the header). */
@Composable
fun Modifier.sharedAvatar(key: String): Modifier {
    val shared = LocalSharedScope.current ?: return this
    val page = LocalPageScope.current ?: return this
    return with(shared) {
        this@sharedAvatar.sharedElement(
            rememberSharedContentState(key),
            page,
            boundsTransform = BoundsTransform { _, _ ->
                spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow, visibilityThreshold = Rect.VisibilityThreshold)
            },
        )
    }
}

/**
 * The start-up cascade: after the intro's portal opens, the home rows rise into place one after another (40 ms
 * apart). Rows composed later (scrolling) just appear.
 */
@Stable
class Cascade(active: Boolean) {
    var started by mutableStateOf(!active)
        private set
    private var startedAt = if (active) Long.MAX_VALUE else 0L

    fun start() {
        if (started) return
        startedAt = System.currentTimeMillis()
        started = true
    }

    /** Rows first composed this long after the start appear without the cascade. */
    fun over() = started && System.currentTimeMillis() - startedAt > 1200
}

val LocalCascade = staticCompositionLocalOf { Cascade(active = false) }

@Composable
fun Modifier.cascadeIn(index: Int): Modifier {
    val cascade = LocalCascade.current
    val progress = remember { Animatable(if (cascade.over()) 1f else 0f) }
    LaunchedEffect(Unit) {
        if (progress.value >= 1f) return@LaunchedEffect
        snapshotFlow { cascade.started }.first { it }
        delay(index.coerceAtMost(12) * 40L)
        progress.animateTo(1f, spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow))
    }
    return graphicsLayer {
        val p = progress.value
        translationY = (1f - p) * 64.dp.toPx()
        alpha = p.coerceIn(0f, 1f)
    }
}

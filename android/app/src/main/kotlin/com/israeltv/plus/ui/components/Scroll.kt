package com.israeltv.plus.ui.components

import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateDecay
import androidx.compose.animation.core.spring
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.OverscrollEffect
import androidx.compose.foundation.OverscrollFactory
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Velocity
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.sign

/** Momentum for the master LazyColumn and every LazyRow: the platform spline decay with a 15% stronger throw. */
@Composable
fun rememberBitChordFlingBehavior(): FlingBehavior {
    val splineDecay = rememberSplineBasedDecay<Float>()
    return remember(splineDecay) {
        object : FlingBehavior {
            override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
                val adjustedVelocity = initialVelocity * 1.15f
                return if (abs(adjustedVelocity) > 1f) {
                    var velocityLeft = adjustedVelocity
                    var lastValue = 0f
                    AnimationState(
                        initialValue = 0f,
                        initialVelocity = adjustedVelocity,
                    ).animateDecay(splineDecay) {
                        val delta = value - lastValue
                        val consumed = scrollBy(delta)
                        lastValue = value
                        velocityLeft = this.velocity
                        if (abs(delta - consumed) > 0.5f) this.cancelAnimation()
                    }
                    velocityLeft
                } else {
                    adjustedVelocity
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------------------------- overscroll bounce
// The edge behaviour of BitChord (and of iOS): past the end the content follows the finger on UIKit's rubber-band
// curve, and comes back with a critically damped spring. Same physical constants: band constant 0.55, spring
// stiffness 247 (130 when the edge is hit faster than 5000 px/s), bounce velocity capped at 10000 px/s.
private const val BAND = 0.55f
private const val STIFFNESS = 247f
private const val STIFFNESS_FAST = 130f
private const val FAST = 5_000f
private const val MAX_BOUNCE_VELOCITY = 10_000f

/** How far the content moves for `stretch` px of finger travel past the edge, in a container `size` px long. */
private fun banded(stretch: Float, size: Float): Float {
    if (stretch == 0f) return 0f
    val d = if (size > 0f) size else 2_000f
    return sign(stretch) * (1f - 1f / (abs(stretch) * BAND / d + 1f)) * d / BAND
}

private class RubberBandOverscroll : OverscrollEffect {
    /** Finger travel past the edge, per axis (px). The drawn offset is banded(stretch). */
    private var stretch by mutableStateOf(Offset.Zero)
    private var width = 0f
    private var height = 0f

    override val isInProgress: Boolean get() = stretch != Offset.Zero

    override fun applyToScroll(delta: Offset, source: NestedScrollSource, performScroll: (Offset) -> Offset): Offset {
        // a stretch is undone first when the finger moves back, before the list scrolls again
        val keptX = undo(stretch.x, delta.x)
        val keptY = undo(stretch.y, delta.y)
        val spent = Offset(stretch.x - keptX, stretch.y - keptY) * -1f
        stretch = Offset(keptX, keptY)
        val rest = delta - spent
        val scrolled = performScroll(rest)
        val left = rest - scrolled
        // only a finger stretches; a fling's remainder goes back unconsumed so it ends here and reaches applyToFling
        if (source == NestedScrollSource.UserInput && left != Offset.Zero) {
            stretch += left
            return delta
        }
        return spent + scrolled
    }

    override suspend fun applyToFling(velocity: Velocity, performFling: suspend (Velocity) -> Velocity) {
        val left = velocity - performFling(velocity)
        if (stretch == Offset.Zero && left == Velocity.Zero) return
        coroutineScope {
            launch { settle(stretch.x, left.x) { stretch = stretch.copy(x = it) } }
            launch { settle(stretch.y, left.y) { stretch = stretch.copy(y = it) } }
        }
    }

    /** What remains of `current` after a delta pushing against it (a delta in the same direction leaves it). */
    private fun undo(current: Float, delta: Float): Float = when {
        current == 0f || delta == 0f || sign(current) == sign(delta) -> current
        abs(delta) >= abs(current) -> 0f
        else -> current + delta
    }

    private suspend fun settle(from: Float, velocity: Float, write: (Float) -> Unit) {
        if (from == 0f && velocity == 0f) return
        val v = velocity.coerceIn(-MAX_BOUNCE_VELOCITY, MAX_BOUNCE_VELOCITY)
        val stiffness = if (abs(v) > FAST) STIFFNESS_FAST else STIFFNESS
        animate(from, 0f, v, spring(dampingRatio = 1f, stiffness = stiffness)) { value, _ -> write(value) }
    }

    override val node: DelegatableNode = object : Modifier.Node(), LayoutModifierNode {
        override fun MeasureScope.measure(measurable: Measurable, constraints: Constraints): MeasureResult {
            val p = measurable.measure(constraints)
            width = (if (constraints.hasBoundedWidth) constraints.maxWidth else p.width).toFloat()
            height = (if (constraints.hasBoundedHeight) constraints.maxHeight else p.height).toFloat()
            return layout(p.width, p.height) {
                p.placeWithLayer(0, 0) {
                    translationX = banded(stretch.x, width)
                    translationY = banded(stretch.y, height)
                }
            }
        }
    }
}

/** Provided by ItvTheme to every scroll container (LocalOverscrollFactory). */
object RubberBandOverscrollFactory : OverscrollFactory {
    override fun createOverscrollEffect(): OverscrollEffect = RubberBandOverscroll()
    override fun equals(other: Any?): Boolean = other === this
    override fun hashCode(): Int = javaClass.hashCode()
}

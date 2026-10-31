package com.israeltv.plus.ui.components.jelly

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.israeltv.plus.ui.components.frostedGlass
import kotlin.math.abs
import kotlin.math.max

/**
 * The Nuvio floating navigation bar, ported from composeApp/.../FloatingNavigationBar.android.kt (+ jelly/JellyTabs.kt,
 * glass/GlassBarSurface.kt): a slim pill whose active tab is a spring-driven "jelly" capsule that stretches while it
 * travels and can be dragged. Per this app's brief inactive tabs are icon-only and the active tab expands inline with
 * its label (Nuvio's own bar stacks labels under the icons).
 */
class JellyNavItem(
    val label: String,
    val selected: Boolean,
    val onClick: () -> Unit,
    val icon: ImageVector,
)

@Composable
fun JellyNavigationBar(
    items: List<JellyNavItem>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(bottom = 16.dp),
) {
    if (items.isEmpty()) return
    val accent = MaterialTheme.colorScheme.primary
    val selectedSurface = accent.copy(alpha = 0.22f)
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val selectedIndex = items.indexOfFirst { it.selected }
    val visualSelectedIndex = visualNavIndex(selectedIndex, items.size, isRtl)
    val motion = remember(items.size, isRtl) { JellyMotion(visualSelectedIndex, items.size) }
    val currentItems by rememberUpdatedState(items)
    val currentIsRtl by rememberUpdatedState(isRtl)
    val density = LocalDensity.current
    val glowStrength by animateFloatAsState(1f, tween(420), label = "nav_glow_strength")

    LaunchedEffect(visualSelectedIndex, items.size) { motion.select(visualSelectedIndex) }
    LaunchedEffect(motion.running) {
        if (!motion.running) return@LaunchedEffect
        var previous = withFrameNanos { it }
        while (motion.running) {
            withFrameNanos { now ->
                motion.advance((now - previous) / 1_000_000_000.0)
                previous = now
            }
        }
    }

    Box(
        modifier = modifier.fillMaxWidth().padding(contentPadding).padding(horizontal = 24.dp),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 400.dp)
                .fillMaxWidth()
                .height(52.dp)
                .onSizeChanged { motion.resize(it.width / density.density, it.height / density.density, items.size) }
                .pointerInput(motion, density, items.size, isRtl) {
                    detectJellyTabGestures(motion, density.density, { currentItems }, { currentIsRtl })
                },
        ) {
            Box(
                Modifier.matchParentSize().graphicsLayer {
                    val frame = motion.frame
                    scaleX = frame.trackScale
                    scaleY = frame.trackScale
                },
            ) {
                Box(
                    Modifier.matchParentSize().graphicsLayer {
                        val frame = motion.frame
                        transformOrigin = TransformOrigin(
                            if (size.width > 0) frame.originX * density.density / size.width else 0.5f,
                            0.5f,
                        )
                        scaleX = frame.trackScaleX
                        translationY = frame.trackOffsetY * density.density
                    },
                ) {
                    Box(Modifier.matchParentSize().graphicsLayer { translationX = motion.frame.panelOffset * density.density }) {
                        Box(
                            Modifier.matchParentSize()
                                .clip(RoundedCornerShape(50))
                                .drawWithContent {
                                    drawContent()
                                    drawJellyGlow(motion.frame, accent.copy(alpha = accent.alpha * glowStrength))
                                },
                        ) {
                            GlassBarSurface(Modifier.matchParentSize())
                        }
                        // inactive layer: icon only, with the jelly pill cut out of it
                        Box(
                            Modifier.matchParentSize().drawWithContent {
                                if (selectedIndex >= 0) {
                                    clipPath(jellyPillPath(motion.frame, items.size), ClipOp.Difference) {
                                        this@drawWithContent.drawContent()
                                    }
                                } else {
                                    drawContent()
                                }
                            },
                        ) {
                            JellyTabRow(items, motion, active = false, modifier = Modifier.matchParentSize())
                        }
                        // active layer: icon + label, visible only inside the travelling pill
                        if (selectedIndex >= 0) {
                            Box(
                                Modifier.matchParentSize().clearAndSetSemantics {}.drawWithContent {
                                    drawJellyPill(
                                        motion.frame, items.size, selectedSurface,
                                        accent.copy(alpha = accent.alpha * glowStrength),
                                    ) { drawContent() }
                                },
                            ) {
                                JellyTabRow(items, motion, active = true, modifier = Modifier.matchParentSize())
                            }
                        }
                        JellyTabTargets(items, motion, Modifier.matchParentSize())
                    }
                }
            }
        }
    }
}

@Composable
private fun JellyTabRow(items: List<JellyNavItem>, motion: JellyMotion, active: Boolean, modifier: Modifier) {
    val scheme = MaterialTheme.colorScheme
    val color = if (active) scheme.primary else scheme.onSurface.copy(alpha = 0.6f)
    Row(
        modifier = modifier.padding(4.dp).clearAndSetSemantics {},
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items.forEach { item ->
            Box(
                Modifier.weight(1f).fillMaxHeight().graphicsLayer {
                    val scale = if (active) motion.frame.contentScale else 1f
                    scaleX = scale
                    scaleY = scale
                },
                contentAlignment = Alignment.Center,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(item.icon, null, Modifier.size(24.dp), tint = color)
                    if (active) {
                        Text(
                            text = item.label,
                            color = color,
                            style = TextStyle(fontSize = 13.sp, lineHeight = 16.sp, fontWeight = FontWeight.Bold),
                            maxLines = 1,
                            overflow = TextOverflow.Clip,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun JellyTabTargets(items: List<JellyNavItem>, motion: JellyMotion, modifier: Modifier) {
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    Row(modifier.padding(horizontal = 4.dp).selectableGroup()) {
        items.forEachIndexed { index, item ->
            val visualIndex = visualNavIndex(index, items.size, isRtl)
            val onClick = {
                motion.select(visualIndex)
                item.onClick()
            }
            Box(
                modifier = Modifier.weight(1f).fillMaxHeight()
                    .selectable(selected = item.selected, role = Role.Tab, interactionSource = null, indication = null, onClick = onClick)
                    .clearAndSetSemantics {
                        role = Role.Tab
                        selected = item.selected
                        contentDescription = item.label
                        onClick { onClick(); true }
                    },
            )
        }
    }
}

private val GlassSurfaceColor = Color(0xFF1C1C1E)

/** Nuvio's glass bar surface (blur + dark fill + a 0.75 dp light rim); the refraction shader is left out. */
@Composable
private fun GlassBarSurface(modifier: Modifier = Modifier) {
    Box(
        modifier
            .frostedGlass(RoundedCornerShape(50), GlassSurfaceColor.copy(alpha = 0.55f), blur = 24.dp, border = false)
            .drawWithCache {
                val edge = Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.27f), Color.White.copy(alpha = 0.02f)))
                val width = 0.75.dp.toPx()
                onDrawBehind {
                    drawRoundRect(
                        brush = edge,
                        topLeft = Offset(width / 2, width / 2),
                        size = Size(size.width - width, size.height - width),
                        cornerRadius = CornerRadius((size.height - width) / 2),
                        style = Stroke(width),
                    )
                }
            },
    )
}

internal fun visualNavIndex(logicalIndex: Int, count: Int, isRtl: Boolean): Int =
    if (logicalIndex in 0 until count && isRtl) count - 1 - logicalIndex else logicalIndex

internal fun logicalNavIndex(visualIndex: Int, count: Int, isRtl: Boolean): Int =
    if (visualIndex in 0 until count && isRtl) count - 1 - visualIndex else visualIndex

private suspend fun PointerInputScope.detectJellyTabGestures(
    motion: JellyMotion,
    density: Float,
    currentItems: () -> List<JellyNavItem>,
    isRtl: () -> Boolean,
) {
    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
        motion.begin(down.position.x / density, down.position.y / density)
        var claimed = false
        var finished = false
        try {
            while (true) {
                val event = awaitPointerEvent(PointerEventPass.Initial)
                if (!motion.dragging) {
                    finished = true
                    break
                }
                val change = event.changes.firstOrNull { it.id == down.id } ?: break
                if (event.changes.count { it.pressed } > 1) break
                val delta = change.position - down.position
                if (max(abs(delta.x), abs(delta.y)) > viewConfiguration.touchSlop) claimed = true
                if (claimed) change.consume()
                awaitPointerEvent(PointerEventPass.Main)
                if (!motion.dragging) {
                    finished = true
                    break
                }
                if (change.isConsumed && !claimed) break
                motion.drag(delta.x / density, delta.y / density)
                if (!change.pressed) {
                    val visualIndex = motion.finish()
                    val items = currentItems()
                    val logicalIndex = logicalNavIndex(visualIndex, items.size, isRtl())
                    finished = true
                    items.getOrNull(logicalIndex)?.onClick?.invoke()
                    change.consume()
                    break
                }
            }
        } finally {
            if (!finished) {
                val items = currentItems()
                val selectedIndex = items.indexOfFirst { it.selected }
                motion.cancel(visualNavIndex(selectedIndex, items.size, isRtl()))
            }
        }
    }
}

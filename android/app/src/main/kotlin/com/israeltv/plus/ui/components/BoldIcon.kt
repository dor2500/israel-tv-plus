package com.israeltv.plus.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

/**
 * The Nuvio "weight" axis for the Material icon set.
 *
 * `material-icons-extended` only ships one weight per glyph (the old Material Icons 400). Material Symbols Rounded at
 * weight 600-700 draws the same silhouette with a fatter stem. We get the same optical result without shipping a second
 * font: the vector is stamped once per point of a small ring (plus the centre), so its outline grows by [weight] on every
 * side. Corners stay round because the ring is round; fills stay solid. Cutout-disc glyphs (such as Info and CheckCircle)
 * are already solid filled circles with negative-space cutouts, so they are drawn without positive dilation to preserve
 * their inner glyph.
 */
@Composable
fun BoldIcon(
    icon: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
    weight: Dp = 0.45.dp,
) {
    val painter = rememberVectorPainter(icon)
    val colorFilter = remember(tint) { ColorFilter.tint(tint) }
    val label = contentDescription
    val semantics = if (label != null) {
        Modifier.semantics { this.contentDescription = label }
    } else {
        Modifier
    }
    val isCutoutDisc = icon == Icons.Rounded.Info || icon == Icons.Rounded.CheckCircle
    Canvas(modifier.then(semantics)) {
        val radius = if (isCutoutDisc) 0f else weight.toPx()
        if (radius > 0f) {
            // 8 compass points at full radius + the centre: a 1.4 dp outline growth, perfectly round
            for (step in 0 until 8) {
                val angle = Math.toRadians(step * 45.0)
                translate((cos(angle) * radius).toFloat(), (sin(angle) * radius).toFloat()) {
                    with(painter) { draw(size, colorFilter = colorFilter) }
                }
            }
        }
        with(painter) { draw(size, colorFilter = colorFilter) }
    }
}

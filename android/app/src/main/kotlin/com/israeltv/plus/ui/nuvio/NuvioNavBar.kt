package com.israeltv.plus.ui.nuvio

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.israeltv.plus.R
import com.israeltv.plus.ui.components.frostedGlass
import com.israeltv.plus.ui.components.glassTint

private val PillHeight = 52.dp
private val ItemHeight = 44.dp

/**
 * Nuvio's floating glass pill: frosted over whatever LocalHaze sources, 52 dp tall at most. The selected tab grows
 * its label out of the icon with a spring. Layout direction decides the order, so under RTL Home sits on the right.
 */
@Composable
fun NuvioNavBar(
    selected: NuvioTab,
    onSelect: (NuvioTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val navDescription = stringResource(R.string.nuvio_nav_description)
    Row(
        modifier = modifier
            .height(PillHeight)
            .frostedGlass(CircleShape, glassTint(), blur = 40.dp)
            .padding(horizontal = 4.dp)
            .selectableGroup()
            .semantics { contentDescription = navDescription },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        for (tab in NuvioTab.entries) {
            NuvioNavItem(tab = tab, selected = tab == selected, onClick = { onSelect(tab) })
        }
    }
}

@Composable
private fun NuvioNavItem(tab: NuvioTab, selected: Boolean, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val container by animateColorAsState(
        targetValue = if (selected) colors.primaryContainer else Color.Transparent,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "navContainer",
    )
    val content by animateColorAsState(
        targetValue = if (selected) colors.onPrimaryContainer else colors.onSurfaceVariant,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "navContent",
    )
    val label = stringResource(tab.label)
    val source = remember { MutableInteractionSource() }
    Row(
        modifier = Modifier
            .height(ItemHeight)
            .defaultMinSize(minWidth = ItemHeight)
            .nuvioPressFeedback(source, restRadius = 22.dp, pressedRadius = 14.dp)
            .background(container, CircleShape)
            .selectable(selected = selected, interactionSource = source, indication = null, role = Role.Tab, onClick = onClick)
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = tab.icon,
            contentDescription = if (selected) null else label,
            tint = content,
            modifier = Modifier.size(24.dp),
        )
        AnimatedVisibility(
            visible = selected,
            enter = expandHorizontally(spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMediumLow)) + fadeIn(),
            exit = shrinkHorizontally(spring(stiffness = Spring.StiffnessMedium)) + fadeOut(),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(Modifier.width(8.dp))
                Text(label, style = MaterialTheme.typography.labelLarge, color = content, maxLines = 1, overflow = TextOverflow.Clip)
            }
        }
    }
}

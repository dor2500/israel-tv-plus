package com.israeltv.plus.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LiveTv
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import com.israeltv.plus.data.avatarUrl

enum class Tab(val label: String, val title: String, val icon: ImageVector) {
    Home("בית", "ישראל TV+", Icons.Rounded.Home),
    Channels("ערוצים", "ערוצים", Icons.Rounded.LiveTv),
    Mine("שלי", "הרשימה שלי", Icons.Rounded.Favorite),
    Settings("הגדרות", "הגדרות", Icons.Rounded.Settings),
}

/** The master LazyColumn's insets: under the header (88 dp) or edge-to-edge for full-bleed hero. */
@Composable
fun feedPadding(topHeaderSpace: Boolean = true): PaddingValues {
    val top = if (topHeaderSpace) WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 88.dp else 0.dp
    val bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    return PaddingValues(top = top, bottom = bottom + 130.dp)
}

/** surfaceContainerLowest with a soft ambient glow of the wallpaper colour from the top. */
@Composable
fun AmbientBackground(modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    Box(
        modifier
            .fillMaxSize()
            .background(scheme.surfaceContainerLowest)
            .drawBehind {
                drawRect(
                    Brush.radialGradient(
                        listOf(scheme.primary.copy(alpha = 0.18f), Color.Transparent),
                        center = Offset(size.width / 2f, 0f),
                        radius = size.width * 1.15f,
                    )
                )
            }
    )
}

/**
 * Apple TV header: the screen title on the right, the 3D Noto Emoji avatar (42 dp) on the left, over a blurred
 * band that fades out downwards so the feed scrolls behind it.
 */
@Composable
fun AppHeader(title: String, avatar: String, onAvatar: () -> Unit, modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    val haptic = LocalHapticFeedback.current
    Box(modifier.fillMaxWidth().zIndex(10f)) {
        Box(
            Modifier
                .matchParentSize()
                .frostedGlass(
                    RectangleShape, scheme.surfaceContainerLowest.copy(alpha = 0.35f), blur = 22.dp, border = false,
                    mask = Brush.verticalGradient(0f to Color.Black, 0.7f to Color.Black, 1f to Color.Transparent),
                )
                .background(
                    Brush.verticalGradient(
                        listOf(scheme.surfaceContainerLowest.copy(alpha = 0.85f), scheme.surfaceContainerLowest.copy(alpha = 0f))
                    )
                )
        )
        Row(
            Modifier.statusBarsPadding().height(64.dp).fillMaxWidth().padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                title,
                Modifier.weight(1f),
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                color = scheme.onSurface,
                maxLines = 1,
            )
            val interaction = remember { MutableInteractionSource() }
            Box(
                Modifier
                    .size(42.dp)
                    .pressPhysics(interaction, tilt = false)
                    .frostedGlass(CircleShape, scheme.surfaceContainerHighest.copy(alpha = 0.6f))
                    .clickable(interaction, null) {
                        haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                        onAvatar()
                    },
                contentAlignment = Alignment.Center,
            ) {
                AsyncImage(avatarUrl(avatar), "פרופיל", Modifier.size(32.dp))
            }
        }
        Spacer(Modifier.statusBarsPadding().height(84.dp))
    }
}

/**
 * Dynamic Collapsing Pill Navbar (Video Reference Matched):
 * - Expanded State (At Top / Scrolling Up): Wide floating frosted-glass capsule (fillMaxWidth(0.92f), height = 70.dp).
 *   Displays Noveo-style solid icon on top and label underneath (11.sp). Active tab highlighted inside
 *   soft tonal pill (MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)).
 * - Collapsed State (Scrolling Down): Smoothly animates capsule down to compact centered pod (fillMaxWidth(0.72f), height = 56.dp)
 *   using spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessMediumLow). Fades out and shrinks text labels
 *   leaving icons centered inside the glass pill.
 */
@Composable
fun FloatingTabBar(
    selected: Tab,
    searchOpen: Boolean,
    onSelect: (Tab) -> Unit,
    onSearch: () -> Unit,
    collapsed: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val scheme = MaterialTheme.colorScheme
    val haptic = LocalHapticFeedback.current
    val pill = RoundedCornerShape(50)

    val capsuleWidthFraction by animateFloatAsState(
        targetValue = if (collapsed) 0.72f else 0.92f,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessMediumLow),
        label = "capsuleWidth",
    )

    val capsuleHeight by animateDpAsState(
        targetValue = if (collapsed) 56.dp else 70.dp,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessMediumLow),
        label = "capsuleHeight",
    )

    Box(
        modifier = modifier
            .zIndex(10f)
            .navigationBarsPadding()
            .padding(bottom = 16.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth(capsuleWidthFraction)
                .height(capsuleHeight)
                .frostedGlass(pill, blur = 90.dp)
        ) {
            val count = Tab.entries.size + 1
            val itemWidth: Dp = maxWidth / count
            val index = if (searchOpen) Tab.entries.size else selected.ordinal
            val x by animateDpAsState(
                itemWidth * index,
                spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessMediumLow),
                label = "indicator",
            )
            // Active tonal pill indicator
            Box(
                Modifier
                    .offset(x = x)
                    .width(itemWidth)
                    .fillMaxHeight()
                    .padding(vertical = 5.dp, horizontal = 4.dp)
                    .clip(pill)
                    .background(scheme.primaryContainer.copy(alpha = 0.4f))
            )
            Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Tab.entries.forEach { tab ->
                    BarItem(
                        icon = tab.icon,
                        label = tab.label,
                        active = !searchOpen && tab == selected,
                        collapsed = collapsed,
                        modifier = Modifier.weight(1f),
                    ) {
                        haptic.performHapticFeedback(HapticFeedbackType.SegmentTick)
                        onSelect(tab)
                    }
                }
                BarItem(
                    icon = Icons.Rounded.Search,
                    label = "חיפוש",
                    active = searchOpen,
                    collapsed = collapsed,
                    modifier = Modifier.weight(1f),
                ) {
                    haptic.performHapticFeedback(HapticFeedbackType.SegmentTick)
                    onSearch()
                }
            }
        }
    }
}

@Composable
private fun BarItem(
    icon: ImageVector,
    label: String,
    active: Boolean,
    collapsed: Boolean,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    val scheme = MaterialTheme.colorScheme
    val color = if (active) scheme.primary else scheme.onSurfaceVariant
    Column(
        modifier
            .fillMaxHeight()
            .clickable(remember { MutableInteractionSource() }, null, onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(icon, label, Modifier.size(24.dp), tint = color)
        AnimatedVisibility(
            visible = !collapsed,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
        ) {
            Text(
                label,
                fontSize = 11.sp,
                fontWeight = if (active) FontWeight.ExtraBold else FontWeight.Medium,
                color = color,
                maxLines = 1,
            )
        }
    }
}

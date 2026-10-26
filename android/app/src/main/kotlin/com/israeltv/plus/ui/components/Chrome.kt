package com.israeltv.plus.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.LayoutDirection
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
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import com.israeltv.plus.data.avatarUrl

enum class Tab(val label: String, val title: String, val icon: ImageVector) {
    Home("בית", "ישראל TV+‎", Icons.Rounded.Home),
    Channels("ערוצים", "ערוצים", Icons.Rounded.LiveTv),
    Mine("שלי", "הרשימה שלי", Icons.Rounded.Favorite),
    Settings("הגדרות", "הגדרות", Icons.Rounded.Settings),
}

/** The master LazyColumn's insets: under the header (88 dp) and clear of the floating bar (130 dp). */
@Composable
fun feedPadding(): PaddingValues {
    val top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    return PaddingValues(top = top + 88.dp, bottom = bottom + 130.dp)
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
 * Apple TV header: the screen title on the right, the profile's 3D Noto Emoji avatar (42 dp) on the left, over a
 * heavy frosted band (real backdrop blur, the glass recipe) that fades out downwards so the feed scrolls behind it.
 * The avatar is a shared element: it flies here from the "who's watching" screen.
 */
@Composable
fun AppHeader(title: String, profileId: String, avatar: String, onAvatar: () -> Unit, onSettings: () -> Unit, modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    val haptic = LocalHapticFeedback.current
    Box(modifier.fillMaxWidth().zIndex(10f)) {
        Box(Modifier.matchParentSize().frostedGlass(
            RectangleShape, glassTint(), blur = 36.dp, border = false,
            mask = Brush.verticalGradient(0f to Color.Black, 0.65f to Color.Black, 1f to Color.Transparent),
        ))
        Row(Modifier.statusBarsPadding().height(64.dp).fillMaxWidth().padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(title, Modifier.weight(1f), style = MaterialTheme.typography.headlineMedium, color = scheme.onSurface, maxLines = 1)
            val settingsInteraction = remember { MutableInteractionSource() }
            Box(Modifier.size(44.dp).frostedGlass(CircleShape, Color(0xFF16161E).copy(alpha = .65f), blur = 36.dp)
                .clickable(settingsInteraction, null) { haptic.performHapticFeedback(HapticFeedbackType.ContextClick); onSettings() }, contentAlignment = Alignment.Center) {
                Icon(Icons.Rounded.Settings, "הגדרות", tint = scheme.onSurface, modifier = Modifier.size(21.dp))
            }
            Spacer(Modifier.width(12.dp))
            val interaction = remember { MutableInteractionSource() }
            Box(Modifier.size(42.dp).pressPhysics(interaction, tilt = false).sharedAvatar("avatar:$profileId")
                .clip(RoundedCornerShape(14.dp)).background(scheme.primaryContainer).specularBorder(RoundedCornerShape(14.dp), alpha = 0.32f)
                .clickable(interaction, null) { haptic.performHapticFeedback(HapticFeedbackType.ContextClick); onAvatar() }, contentAlignment = Alignment.Center) {
                AsyncImage(avatarUrl(avatar), "פרופיל", Modifier.size(32.dp))
            }
        }
        Spacer(Modifier.statusBarsPadding().height(84.dp))
    }
}

/**
 * Search is its own 64 dp glass circle on the physical left; Home, Channels and My List live in a separate glass capsule.
 * Settings is deliberately absent here and lives in the top app bar.
 */
@Composable
fun FloatingTabBar(
    selected: Tab,
    searchOpen: Boolean,
    collapsed: Boolean,
    onSelect: (Tab) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scheme = MaterialTheme.colorScheme
    val haptic = LocalHapticFeedback.current
    val tabs = listOf(Tab.Home, Tab.Channels, Tab.Mine)
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        BoxWithConstraints(modifier.fillMaxWidth().zIndex(10f).navigationBarsPadding().padding(bottom = 18.dp)) {
            val capsuleTarget = (maxWidth - 80.dp - if (collapsed) 34.dp else 0.dp).coerceAtLeast(200.dp)
            val capsuleWidth by animateDpAsState(capsuleTarget, spring(dampingRatio = .82f, stiffness = Spring.StiffnessMediumLow), label = "capsuleWidth")
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    Modifier.size(64.dp).frostedGlass(CircleShape, Color(0xFF16161E).copy(alpha = .65f), blur = 36.dp)
                        .clickable(remember { MutableInteractionSource() }, null) { haptic.performHapticFeedback(HapticFeedbackType.SegmentTick); onSearch() },
                    contentAlignment = Alignment.Center,
                ) { Icon(Icons.Rounded.Search, "חיפוש", Modifier.size(25.dp), tint = scheme.onSurface) }

                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Row(
                        Modifier.width(capsuleWidth).height(64.dp).frostedGlass(RoundedCornerShape(50), Color(0xFF16161E).copy(alpha = .65f), blur = 36.dp)
                            .padding(horizontal = 7.dp, vertical = 7.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp), verticalAlignment = Alignment.CenterVertically,
                    ) {
                        tabs.forEach { tab ->
                            val active = !searchOpen && selected == tab
                            Row(
                                Modifier.weight(1f).fillMaxHeight().clip(RoundedCornerShape(50))
                                    .background(if (active) Color.White.copy(alpha = .16f) else Color.Transparent)
                                    .clickable(remember { MutableInteractionSource() }, null) {
                                        haptic.performHapticFeedback(HapticFeedbackType.SegmentTick); onSelect(tab)
                                    }.padding(horizontal = 5.dp),
                                horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(tab.icon, null, Modifier.size(19.dp), tint = if (active) scheme.onSurface else scheme.onSurfaceVariant)
                                Spacer(Modifier.width(5.dp))
                                Text(tab.label, color = if (active) scheme.onSurface else scheme.onSurfaceVariant, style = MaterialTheme.typography.labelLarge, maxLines = 1)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BarItem(icon: ImageVector, label: String, active: Boolean, showLabel: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val scheme = MaterialTheme.colorScheme
    val color = if (active) scheme.onPrimaryContainer else scheme.onSurfaceVariant
    Column(
        modifier
            .fillMaxHeight()
            .clickable(remember { MutableInteractionSource() }, null, onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(icon, label, Modifier.size(26.dp), tint = if (active) scheme.onSurface else color)
        AnimatedVisibility(showLabel, enter = expandVertically() + fadeIn(), exit = shrinkVertically() + fadeOut()) {
            Text(label, fontSize = 11.sp, fontWeight = if (active) FontWeight.ExtraBold else FontWeight.Medium, color = if (active) scheme.onSurface else color)
        }
    }
}

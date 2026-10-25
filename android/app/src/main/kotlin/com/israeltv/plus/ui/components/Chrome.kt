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
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LiveTv
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
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
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import com.israeltv.plus.data.avatarUrl

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

enum class Tab(
    val label: String,
    val title: String,
    val outlinedIcon: ImageVector,
    val solidIcon: ImageVector,
) {
    Home("בית", "ישראל TV+‎", Icons.Outlined.Home, Icons.Rounded.Home),
    Channels("ערוצים", "ערוצים", Icons.Outlined.LiveTv, Icons.Rounded.LiveTv),
    Mine("שלי", "הרשימה שלי", Icons.Outlined.FavoriteBorder, Icons.Rounded.Favorite),
    Settings("הגדרות", "הגדרות", Icons.Outlined.Settings, Icons.Rounded.Settings),
}

val MainNavTabs = listOf(Tab.Home, Tab.Channels, Tab.Mine)

/** The master LazyColumn's insets: under the header (88 dp) and clear of the floating bar (140 dp). */
@Composable
fun feedPadding(): PaddingValues {
    val top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    return PaddingValues(top = top + 88.dp, bottom = bottom + 140.dp)
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
 * Apple TV header: the screen title on the right, and on the left:
 * - Settings glass pill
 * - Profile avatar with tactile bounce and specular edge.
 * ("רק ישראלי" toggle has been relocated inline directly above/below Category Pills as an animated Switch).
 */
@Composable
fun AppHeader(
    title: String,
    profileId: String,
    avatar: String,
    israeliOnly: Boolean = false,
    onToggleIsraeli: () -> Unit = {},
    onAvatar: () -> Unit,
    onSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scheme = MaterialTheme.colorScheme
    val pillShape = RoundedCornerShape(50)

    Box(modifier.fillMaxWidth().zIndex(10f)) {
        Box(
            Modifier
                .matchParentSize()
                .frostedGlass(
                    RectangleShape, glassTint(), blur = 36.dp, border = false,
                    mask = Brush.verticalGradient(0f to scheme.background, 0.65f to scheme.background, 1f to Color.Transparent),
                )
        )
        Row(
            Modifier
                .statusBarsPadding()
                .height(64.dp)
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                title,
                Modifier.weight(1f),
                style = MaterialTheme.typography.headlineMedium,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = scheme.onSurface,
                maxLines = 1,
            )

            // Top-left actions: Settings glass pill + Profile avatar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                // Settings top glass pill
                Box(
                    Modifier
                        .height(42.dp)
                        .tactileBounce(onClick = onSettings)
                        .frostedGlass(pillShape, scheme.surfaceContainer.copy(alpha = 0.60f), blur = 24.dp)
                        .padding(horizontal = 14.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Icon(
                            Icons.Outlined.Settings,
                            "הגדרות",
                            Modifier.size(19.dp),
                            tint = scheme.onSurface,
                        )
                        Text(
                            "הגדרות",
                            color = scheme.onSurface,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }

                // Profile avatar
                Box(
                    Modifier
                        .size(42.dp)
                        .tactileBounce(onClick = onAvatar)
                        .sharedAvatar("avatar:$profileId")
                        .clip(RoundedCornerShape(14.dp))
                        .background(scheme.primaryContainer)
                        .specularBorder(RoundedCornerShape(14.dp), alpha = 0.32f),
                    contentAlignment = Alignment.Center,
                ) {
                    AsyncImage(avatarUrl(avatar), "פרופיל", Modifier.size(32.dp))
                }
            }
        }
        Spacer(Modifier.statusBarsPadding().height(84.dp))
    }
}

/**
 * STRICT NUVIO NAVBAR (Bottom Navigation):
 * Built to the exact custom architectural specification:
 * - Row with horizontalArrangement = Arrangement.spacedBy(16.dp)
 * - modifier.align(Alignment.BottomCenter).navigationBarsPadding().padding(bottom = 24.dp, start = 24.dp, end = 24.dp)
 * - 1. Standalone Search Circle (Left): size(64.dp), clip(CircleShape), Color(0xFF16161E).copy(alpha = 0.65f) + blur/haze.
 *      Icon(Search) ONLY. NO TEXT.
 * - 2. Main 3-Tab Capsule (Right): weight(1f), height(64.dp), clip(RoundedCornerShape(50)),
 *      Color(0xFF16161E).copy(alpha = 0.65f) + blur/haze, containing 3 Tabs: Home, Channels, Profile.
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
    val nuvioBlurColor = Color(0xFF16161E).copy(alpha = 0.65f)
    val pillShape = RoundedCornerShape(50)

    // Force LTR layout for the navbar Row so Search is strictly on the LEFT and 3-Tab Capsule is on the RIGHT
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .zIndex(10f)
                .navigationBarsPadding()
                .padding(bottom = 24.dp, start = 24.dp, end = 24.dp)
                .fillMaxWidth(),
        ) {
            // 1. Standalone Search Circle (Left)
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .tactileBounce(onClick = onSearch)
                    .clip(CircleShape)
                    .frostedGlass(CircleShape, nuvioBlurColor, blur = 28.dp, border = false)
                    .specularBorder(CircleShape, alpha = 0.18f)
                    .background(
                        if (searchOpen) scheme.primaryContainer
                        else Color.Transparent
                    ),
                contentAlignment = Alignment.Center,
            ) {
                // Icon(Search) ONLY. NO TEXT.
                Icon(
                    imageVector = if (searchOpen) Icons.Rounded.Search else Icons.Outlined.Search,
                    contentDescription = "חיפוש",
                    modifier = Modifier.size(28.dp),
                    tint = if (searchOpen) scheme.onPrimaryContainer else scheme.onSurface,
                )
            }

            // 2. Main 3-Tab Capsule (Right)
            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(64.dp)
                    .clip(pillShape)
                    .frostedGlass(pillShape, nuvioBlurColor, blur = 28.dp, border = false)
                    .specularBorder(pillShape, alpha = 0.18f),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // 3 Tabs: Home, Channels, Profile (Mine) in natural RTL Hebrew order
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                        val count = MainNavTabs.size
                        val itemWidth: Dp = maxWidth / count
                        val selectedIndex = MainNavTabs.indexOf(selected).coerceAtLeast(0)
                        val indicatorX by animateDpAsState(
                            targetValue = itemWidth * selectedIndex,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessMediumLow,
                            ),
                            label = "nuvio_nav_indicator",
                        )

                        // Smooth sliding active pill indicator
                        if (!searchOpen && selected in MainNavTabs) {
                            Box(
                                Modifier
                                    .offset(x = indicatorX)
                                    .width(itemWidth)
                                    .fillMaxHeight()
                                    .padding(horizontal = 4.dp, vertical = 6.dp)
                                    .clip(pillShape)
                                    .background(scheme.primaryContainer)
                                    .specularBorder(pillShape, alpha = 0.28f)
                            )
                        }

                        // 3 Tabs
                        Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            MainNavTabs.forEach { tab ->
                                val isActive = !searchOpen && tab == selected
                                BitChordBarItem(
                                    tab = tab,
                                    active = isActive,
                                    showLabel = !collapsed,
                                    modifier = Modifier.weight(1f),
                                ) {
                                    onSelect(tab)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BitChordBarItem(
    tab: Tab,
    active: Boolean,
    showLabel: Boolean,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    val scheme = MaterialTheme.colorScheme
    val icon = if (active) tab.solidIcon else tab.outlinedIcon
    val contentColor = if (active) scheme.onPrimaryContainer else scheme.onSurfaceVariant

    Column(
        modifier = modifier
            .fillMaxHeight()
            .tactileBounce(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = tab.label,
            modifier = Modifier.size(24.dp),
            tint = contentColor,
        )
        AnimatedVisibility(
            visible = showLabel,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut(),
        ) {
            Text(
                text = tab.label,
                fontSize = 11.sp,
                fontWeight = if (active) FontWeight.ExtraBold else FontWeight.Medium,
                color = contentColor,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
    }
}

package com.israeltv.plus.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.israeltv.plus.ui.theme.LocalOledBlack

enum class Tab(
    val label: String,
    val title: String,
    val icon: ImageVector,
) {
    Home("בית", "ישראל TV+‎", Icons.Rounded.Home),
    Channels("ערוצים", "ערוצים", Icons.Rounded.LiveTv),
    Mine("שלי", "הרשימה שלי", Icons.Rounded.Favorite),
    Settings("הגדרות", "הגדרות", Icons.Rounded.Settings),
}

/** The master LazyColumn's insets: under the header + macro categories (114 dp on Home, 72 dp on inner tabs) and 118 dp clear of the floating bar. */
@Composable
fun feedPadding(withCategories: Boolean = true): PaddingValues {
    val top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val headerHeight = if (withCategories) 114.dp else 72.dp
    return PaddingValues(top = top + headerHeight, bottom = bottom + 118.dp)
}

/**
 * Main app background. When OLED Black is enabled (default), forces pure #000000.
 */
@Composable
fun AmbientBackground(modifier: Modifier = Modifier) {
    val oledBlack = LocalOledBlack.current
    if (oledBlack) {
        Box(modifier.fillMaxSize().background(Color.Black))
        return
    }
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
 * The app wordmark, centred in the bar exactly like the Kan Box lockup: the Hebrew name in the app's black display
 * weight with the "TV+" in the profile's accent colour.
 */
@Composable
fun BrandWordmark(modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(
            "ישראל",
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = (-0.5).sp,
            color = scheme.onSurface,
            maxLines = 1,
        )
        Spacer(Modifier.width(3.5.dp))
        Text(
            "TV+",
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = (-0.2).sp,
            color = scheme.primary,
            maxLines = 1,
            style = TextStyle(textDirection = TextDirection.Ltr),
        )
    }
}

/**
 * Kan Box Style Top Header & Categories:
 * - Top Row: Profile Avatar on one side, Centered "ישראל TV+" logo, and Settings action orb on the other.
 * - Cast and Split-Screen icons are REMOVED from the global header (they belong ONLY inside the player).
 * - Second Row (when [showCategories] is true): The centered Macro-Categories Row (הכל, סרטים וסדרות, ילדים ונוער, ספורט, עוד...).
 * - Top Gradient-Blur: Heavy Glassmorphism (Gradient+Blur) that is ALWAYS visible at scroll position 0.
 */
@Composable
fun AppHeader(
    profileId: String,
    avatar: String,
    scrollFraction: Float,
    showCategories: Boolean,
    selectedCategory: String?,
    onSelectCategory: (String?) -> Unit,
    onMoreCategories: () -> Unit,
    onAvatar: () -> Unit,
    onSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scheme = MaterialTheme.colorScheme
    val haptic = LocalHapticFeedback.current
    val statusBarTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val blurAlpha by animateFloatAsState(
        targetValue = scrollFraction.coerceIn(0f, 1f),
        animationSpec = tween(180),
        label = "headerScrollBlurAlpha",
    )
    val topGradientMask = remember {
        Brush.verticalGradient(
            0f to Color.Black,
            0.72f to Color.Black.copy(alpha = 0.90f),
            0.90f to Color.Black.copy(alpha = 0.45f),
            1f to Color.Transparent,
        )
    }

    Box(modifier.fillMaxWidth().zIndex(10f)) {
        // Heavy Glassmorphism (Gradient + Blur) visible even at scroll position 0
        Box(
            Modifier
                .matchParentSize()
                .frostedGlass(
                    shape = RectangleShape,
                    tint = Color.Black.copy(alpha = 0.22f + 0.14f * blurAlpha),
                    blur = 44.dp,
                    border = false,
                    mask = topGradientMask,
                )
                .background(
                    Brush.verticalGradient(
                        0f to Color.Black.copy(alpha = 0.56f + 0.18f * blurAlpha),
                        0.62f to Color.Black.copy(alpha = 0.28f + 0.16f * blurAlpha),
                        0.88f to Color.Black.copy(alpha = 0.10f + 0.08f * blurAlpha),
                        1f to Color.Transparent,
                    )
                )
        )

        Column(
            Modifier
                .fillMaxWidth()
                .padding(top = statusBarTop, bottom = if (showCategories) 14.dp else 10.dp)
        ) {
            Box(Modifier.height(52.dp).fillMaxWidth()) {
                // Profile portrait: leading edge (physical right in Hebrew RTL)
                val avatarInteraction = remember { MutableInteractionSource() }
                val avatarPressed by avatarInteraction.collectIsPressedAsState()
                val avatarCorner by animateDpAsState(
                    targetValue = if (avatarPressed) 9.dp else 12.dp,
                    animationSpec = spring(dampingRatio = 0.58f, stiffness = Spring.StiffnessMedium),
                    label = "avatarMorph",
                )
                val avatarShape = RoundedCornerShape(avatarCorner)
                Box(
                    Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 16.dp)
                        .size(36.dp)
                        .pressPhysics(avatarInteraction, tilt = false, glowShape = avatarShape)
                        .sharedAvatar("avatar:$profileId")
                        .clip(avatarShape)
                        .background(Brush.linearGradient(listOf(scheme.primaryContainer, scheme.tertiaryContainer)))
                        .specularBorder(avatarShape, alpha = 0.34f)
                        .clickable(avatarInteraction, null) {
                            haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                            onAvatar()
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    ProfileAvatar(avatar, glyphSize = 24.dp)
                }

                // Centered App Logo ("ישראל TV+")
                BrandWordmark(Modifier.align(Alignment.Center))

                // Trailing action: Settings ONLY (Cast and Split-Screen removed from global header)
                Row(
                    Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    HeaderIconOrb(Icons.Rounded.Settings, "הגדרות", onSettings)
                }
            }

            // Macro-categories row moved UP into the header area (Kan Box style)
            AnimatedVisibility(
                visible = showCategories,
                enter = fadeIn(tween(200)) + expandVertically(tween(220)),
                exit = fadeOut(tween(150)) + shrinkVertically(tween(180)),
            ) {
                MacroCategoryRow(
                    selected = selectedCategory,
                    onSelect = onSelectCategory,
                    onMore = onMoreCategories,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
        }
    }
}

@Composable
private fun HeaderIconOrb(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
) {
    val scheme = MaterialTheme.colorScheme
    val haptic = LocalHapticFeedback.current
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val corner by animateDpAsState(
        targetValue = if (pressed) 10.dp else 17.dp,
        animationSpec = spring(dampingRatio = 0.58f, stiffness = Spring.StiffnessMedium),
        label = "headerOrbMorph",
    )
    val shape = RoundedCornerShape(corner)
    Box(
        Modifier
            .size(34.dp)
            .pressPhysics(interaction, tilt = false, glowShape = shape)
            .frostedGlass(shape, Color.Black.copy(alpha = 0.18f), blur = 40.dp)
            .clickable(interaction, null) {
                haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                onClick()
            },
        contentAlignment = Alignment.Center,
    ) {
        BoldIcon(icon, label, Modifier.size(17.5.dp), tint = scheme.onSurface, weight = 0.65.dp)
    }
}

/**
 * Exact Nuvio Floating Bottom Navbar Port:
 * - Reuses the exact Nuvio bottom navbar code, spacing, and icon set.
 * - Float Position: Elevated by adding bottom padding (Modifier.navigationBarsPadding().padding(bottom = 24.dp)) so it floats above the screen edge.
 * - Style: Completely removes solid backgrounds behind the icons inside the capsule.
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
    val glass = Color.Black.copy(alpha = 0.22f)
    val bottomGradientMask = remember {
        Brush.verticalGradient(
            0f to Color.Transparent,
            0.38f to Color.Black.copy(alpha = 0.48f),
            0.72f to Color.Black.copy(alpha = 0.88f),
            1f to Color.Black,
        )
    }

    Box(modifier.fillMaxWidth().zIndex(10f)) {
        // Bottom Gradient-Blur: heavy Glassmorphism fading smoothly upward
        Box(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(126.dp)
                .frostedGlass(
                    shape = RectangleShape,
                    tint = Color.Black.copy(alpha = 0.16f),
                    blur = 44.dp,
                    border = false,
                    mask = bottomGradientMask,
                )
                .background(
                    Brush.verticalGradient(
                        0f to Color.Transparent,
                        0.45f to Color.Black.copy(alpha = 0.10f),
                        0.78f to Color.Black.copy(alpha = 0.22f),
                        1f to Color.Black.copy(alpha = 0.34f),
                    )
                )
        )

        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            BoxWithConstraints(
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(bottom = 24.dp)
            ) {
                val bouncy = spring<androidx.compose.ui.unit.Dp>(
                    dampingRatio = 0.82f,
                    stiffness = Spring.StiffnessMediumLow,
                )
                val capsuleTarget = (maxWidth - 112.dp - if (collapsed) 36.dp else 0.dp).coerceAtLeast(184.dp)
                val capsuleWidth by animateDpAsState(capsuleTarget, bouncy, label = "capsuleWidth")
                val barHeight by animateDpAsState(if (collapsed) 54.dp else 64.dp, bouncy, label = "barHeight")
                val searchSize by animateDpAsState(if (collapsed) 54.dp else 64.dp, bouncy, label = "searchSize")

                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val searchInteraction = remember { MutableInteractionSource() }
                    Box(
                        Modifier
                            .size(searchSize)
                            .pressPhysics(searchInteraction, tilt = false, glowShape = CircleShape)
                            .frostedGlass(CircleShape, glass, blur = 44.dp)
                            .clickable(searchInteraction, null) {
                                haptic.performHapticFeedback(HapticFeedbackType.SegmentTick)
                                onSearch()
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        BoldIcon(
                            Icons.Rounded.Search,
                            "חיפוש",
                            Modifier.size(if (collapsed) 24.dp else 26.dp),
                            tint = scheme.onSurface,
                            weight = 0.75.dp,
                        )
                    }

                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                        Row(
                            Modifier
                                .width(capsuleWidth)
                                .height(barHeight)
                                .frostedGlass(RoundedCornerShape(50), glass, blur = 44.dp)
                                .padding(horizontal = 6.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            tabs.forEach { tab ->
                                val active = !searchOpen && selected == tab
                                val tabInteraction = remember { MutableInteractionSource() }
                                Column(
                                    Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .pressPhysics(tabInteraction, tilt = false, glowShape = RoundedCornerShape(50))
                                        .clip(RoundedCornerShape(50))
                                        .clickable(tabInteraction, null) {
                                            haptic.performHapticFeedback(HapticFeedbackType.SegmentTick)
                                            onSelect(tab)
                                        },
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                ) {
                                    BoldIcon(
                                        tab.icon,
                                        tab.label,
                                        Modifier.size(23.dp),
                                        tint = if (active) scheme.onSurface else scheme.onSurfaceVariant.copy(alpha = 0.62f),
                                        weight = if (active) 0.75.dp else 0.52.dp,
                                    )
                                    AnimatedVisibility(
                                        visible = !collapsed,
                                        enter = fadeIn(tween(180)) + expandVertically(tween(200)),
                                        exit = fadeOut(tween(130)) + shrinkVertically(tween(170)),
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Spacer(Modifier.height(3.dp))
                                            Text(
                                                tab.label,
                                                color = if (active) scheme.onSurface else scheme.onSurfaceVariant.copy(alpha = 0.62f),
                                                fontSize = 10.5.sp,
                                                fontWeight = if (active) FontWeight.SemiBold else FontWeight.Medium,
                                                maxLines = 1,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

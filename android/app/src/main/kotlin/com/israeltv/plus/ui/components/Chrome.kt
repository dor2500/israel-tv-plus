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
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.graphicsLayer
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

enum class Tab(val label: String, val title: String, val icon: ImageVector) {
    Home("בית", "ישראל TV+‎", Icons.Rounded.Home),
    Channels("ערוצים", "ערוצים", Icons.Rounded.LiveTv),
    Mine("שלי", "הרשימה שלי", Icons.Rounded.Favorite),
    Settings("הגדרות", "הגדרות", Icons.Rounded.Settings),
}

/** The master LazyColumn's insets: under the header + macro categories (118 dp on Home, 76 dp on inner tabs) and 120 dp clear of the floating bar. */
@Composable
fun feedPadding(withCategories: Boolean = true): PaddingValues {
    val top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val headerHeight = if (withCategories) 118.dp else 76.dp
    return PaddingValues(top = top + headerHeight, bottom = bottom + 120.dp)
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
 * The app wordmark, centred in the bar exactly like the Kan Box lockup: the Hebrew name in the app's black display
 * weight with the "TV+" in the profile's accent colour.
 */
@Composable
fun BrandWordmark(modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(
            "ישראל",
            fontSize = 21.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = (-0.6).sp,
            color = scheme.onSurface,
            maxLines = 1,
        )
        Spacer(Modifier.width(4.dp))
        Text(
            "TV+",
            fontSize = 21.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = (-0.2).sp,
            color = scheme.primary,
            maxLines = 1,
            style = TextStyle(textDirection = TextDirection.Ltr),
        )
    }
}

/**
 * Kan Box Style Top Header & Categories (Moved UP):
 * - Top Row: Profile Avatar on one side, Centered "ישראל TV+" logo, and a minimal glass Settings gear (no text) on the other.
 * - Second Row (when [showCategories] is true): The centered Macro-Categories Row (הכל, סרטים וסדרות, ילדים ונוער, ספורט, עוד...).
 * - Dynamic Scroll Glassmorphism: No static top gradient at rest; as the user scrolls down into the content ([scrollFraction] > 0f),
 *   a heavy frosted glassmorphism blur smoothly fades in behind this entire top header + categories area.
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
    val blurAlpha by animateFloatAsState(
        targetValue = scrollFraction.coerceIn(0f, 1f),
        animationSpec = tween(180),
        label = "headerScrollBlurAlpha",
    )

    Box(modifier.fillMaxWidth().zIndex(10f)) {
        // Dynamic Glassmorphism Blur: fades in smoothly as the user scrolls down into the content (no static top gradient)
        if (blurAlpha > 0.01f) {
            Box(
                Modifier
                    .matchParentSize()
                    .graphicsLayer { alpha = blurAlpha }
                    .frostedGlass(
                        shape = RectangleShape,
                        tint = Color(0xFF12121A).copy(alpha = 0.74f),
                        blur = 38.dp,
                        border = false,
                    )
                    .drawBehind {
                        // Subtle bottom specular hairline when blurred
                        drawLine(
                            color = Color.White.copy(alpha = 0.10f * blurAlpha),
                            start = Offset(0f, size.height),
                            end = Offset(size.width, size.height),
                            strokeWidth = 1.dp.toPx(),
                        )
                    }
            )
        }

        Column(
            Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(bottom = if (showCategories) 10.dp else 6.dp)
        ) {
            Box(Modifier.height(60.dp).fillMaxWidth()) {
                // Profile portrait: leading edge (physical right in Hebrew RTL)
                val avatarInteraction = remember { MutableInteractionSource() }
                Box(
                    Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 20.dp)
                        .size(42.dp)
                        .pressPhysics(avatarInteraction, tilt = false, glowShape = RoundedCornerShape(14.dp))
                        .sharedAvatar("avatar:$profileId")
                        .clip(RoundedCornerShape(14.dp))
                        .background(Brush.linearGradient(listOf(scheme.primaryContainer, scheme.tertiaryContainer)))
                        .specularBorder(RoundedCornerShape(14.dp), alpha = 0.34f)
                        .clickable(avatarInteraction, null) {
                            haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                            onAvatar()
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    ProfileAvatar(avatar, glyphSize = 28.dp)
                }

                // Centered App Logo ("ישראל TV+")
                BrandWordmark(Modifier.align(Alignment.Center))

                // Settings: trailing edge, minimal glass circle containing ONLY a bold gear icon
                val settingsInteraction = remember { MutableInteractionSource() }
                Box(
                    Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 20.dp)
                        .size(42.dp)
                        .pressPhysics(settingsInteraction, tilt = false, glowShape = CircleShape)
                        .frostedGlass(CircleShape, Color(0xFF16161E).copy(alpha = 0.65f), blur = 36.dp)
                        .clickable(settingsInteraction, null) {
                            haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                            onSettings()
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    BoldIcon(Icons.Rounded.Settings, "הגדרות", Modifier.size(21.dp), tint = scheme.onSurface, weight = 0.55.dp)
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
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
    }
}

/**
 * The Nuvio navigation: ONE floating pill holding all four destinations (בית, ערוצים, שלי, חיפוש). Every item shares the
 * same touch target; the active one gets a secondaryContainer indicator. Colours come straight from the Material You
 * scheme, so the bar follows the user's wallpaper palette. [collapsed] tightens the item width while the feed scrolls.
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
    val itemWidth by animateDpAsState(
        if (collapsed) 60.dp else 68.dp,
        spring(dampingRatio = 0.82f, stiffness = Spring.StiffnessMediumLow),
        label = "tabItemWidth",
    )
    // Home, Channels, Mine, Search: the first entry sits on the right edge in Hebrew RTL
    val items = listOf(
        Triple(Tab.Home.label, Tab.Home.icon, !searchOpen && selected == Tab.Home) to { onSelect(Tab.Home) },
        Triple(Tab.Channels.label, Tab.Channels.icon, !searchOpen && selected == Tab.Channels) to { onSelect(Tab.Channels) },
        Triple(Tab.Mine.label, Tab.Mine.icon, !searchOpen && selected == Tab.Mine) to { onSelect(Tab.Mine) },
        Triple("חיפוש", Icons.Rounded.Search, searchOpen) to onSearch,
    )
    Box(
        modifier.fillMaxWidth().zIndex(10f).navigationBarsPadding().padding(bottom = 24.dp),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Surface(
            shape = CircleShape,
            color = scheme.surfaceContainerHigh.copy(alpha = 0.94f),
            contentColor = scheme.onSurface,
            tonalElevation = 3.dp,
            shadowElevation = 10.dp,
            border = BorderStroke(1.dp, scheme.outlineVariant.copy(alpha = 0.4f)),
        ) {
            Row(
                Modifier.padding(6.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                items.forEach { (item, action) ->
                    val (label, icon, active) = item
                    val interaction = remember { MutableInteractionSource() }
                    val tint = if (active) scheme.onSecondaryContainer else scheme.onSurfaceVariant
                    Column(
                        Modifier
                            .width(itemWidth)
                            .height(56.dp)
                            .pressPhysics(interaction, tilt = false, glowShape = CircleShape)
                            .clip(CircleShape)
                            .background(if (active) scheme.secondaryContainer else Color.Transparent)
                            .clickable(interaction, null) {
                                haptic.performHapticFeedback(HapticFeedbackType.SegmentTick)
                                action()
                            },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        BoldIcon(icon, label, Modifier.size(22.dp), tint = tint, weight = if (active) 0.6.dp else 0.45.dp)
                        Spacer(Modifier.height(3.dp))
                        Text(
                            label,
                            color = tint,
                            fontSize = 10.5.sp,
                            fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal,
                            maxLines = 1,
                        )
                    }
                }
            }
        }
    }
}

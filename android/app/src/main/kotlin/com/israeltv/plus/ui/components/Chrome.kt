package com.israeltv.plus.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Tv
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LiveTv
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
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
import com.israeltv.plus.ui.theme.LocalDimens
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
    return PaddingValues(top = top + headerHeight, bottom = bottom + 120.dp)
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

private data class NavItem(
    val route: String,
    val label: String,
    val unselectedIcon: ImageVector,
    val selectedIcon: ImageVector,
)

/**
 * The Nuvio navigation: ONE compact floating glass pill (52 dp tall). Inactive tabs are icon-only; only the active tab
 * expands inline with its label on a secondaryContainer indicator. While [collapsed] (feed scrolling down) the pill
 * drops towards the edge and goes icon-only; it springs back open on the way up. Colours come from the Material You scheme.
 */
@Composable
fun NuvioFloatingNavbar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    collapsed: Boolean = false,
) {
    val items = listOf(
        NavItem("home", "בית", Icons.Outlined.Home, Icons.Filled.Home),
        NavItem("channels", "ערוצים", Icons.Outlined.Tv, Icons.Filled.Tv),
        NavItem("favorites", "שלי", Icons.Outlined.FavoriteBorder, Icons.Filled.Favorite),
        NavItem("search", "חיפוש", Icons.Outlined.Search, Icons.Filled.Search),
    )
    val haptic = LocalHapticFeedback.current
    val offsetY by animateDpAsState(
        targetValue = if (collapsed) 24.dp else 0.dp,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessMediumLow),
        label = "navbarOffset",
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(bottom = 16.dp)
            .offset(y = offsetY),
        contentAlignment = Alignment.Center
    ) {
        // Backdrop blur (Haze) under a translucent tonal fill; a plain tint where no blur source exists
        Box(
            Modifier
                .frostedGlass(CircleShape, MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.78f), blur = 14.dp, border = false)
                .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)), CircleShape)
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .height(52.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    val isSelected = currentRoute == item.route
                    val containerColor by animateColorAsState(
                        targetValue = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent,
                        label = "pill_container"
                    )
                    val contentColor by animateColorAsState(
                        targetValue = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        label = "pill_content"
                    )

                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(containerColor)
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.SegmentTick)
                                onNavigate(item.route)
                            }
                            .padding(horizontal = if (isSelected && !collapsed) 16.dp else 12.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.label,
                                tint = contentColor,
                                modifier = Modifier.size(24.dp)
                            )
                            AnimatedVisibility(
                                visible = isSelected && !collapsed,
                                enter = fadeIn() + expandHorizontally(),
                                exit = fadeOut() + shrinkHorizontally()
                            ) {
                                Text(
                                    text = item.label,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = contentColor
                                    ),
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/** App-level wrapper: maps [Tab]/search state to Nuvio routes and fades the feed to OLED black behind the pill. */
@Composable
fun FloatingTabBar(
    selected: Tab,
    searchOpen: Boolean,
    onSelect: (Tab) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier,
    collapsed: Boolean = false,
) {
    val route = when {
        searchOpen -> "search"
        selected == Tab.Home -> "home"
        selected == Tab.Channels -> "channels"
        selected == Tab.Mine -> "favorites"
        else -> ""
    }
    val bg = MaterialTheme.colorScheme.background
    Box(modifier.fillMaxWidth().zIndex(10f)) {
        Box(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(120.dp)
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(0.00f to Color.Transparent, 0.40f to bg.copy(alpha = 0.35f), 0.75f to bg.copy(alpha = 0.85f), 1.00f to bg),
                    )
                )
        )
        NuvioFloatingNavbar(
            currentRoute = route,
            onNavigate = {
                when (it) {
                    "home" -> onSelect(Tab.Home)
                    "channels" -> onSelect(Tab.Channels)
                    "favorites" -> onSelect(Tab.Mine)
                    "search" -> onSearch()
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter),
            collapsed = collapsed,
        )
    }
}

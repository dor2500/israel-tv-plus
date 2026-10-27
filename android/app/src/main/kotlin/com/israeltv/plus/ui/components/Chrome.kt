package com.israeltv.plus.ui.components

import androidx.compose.animation.core.Spring
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

enum class Tab(val label: String, val title: String, val icon: ImageVector) {
    Home("בית", "ישראל TV+‎", Icons.Rounded.Home),
    Channels("ערוצים", "ערוצים", Icons.Rounded.LiveTv),
    Mine("שלי", "הרשימה שלי", Icons.Rounded.Favorite),
    Settings("הגדרות", "הגדרות", Icons.Rounded.Settings),
}

/** The master LazyColumn's insets: under the header (88 dp) and 120 dp clear of the floating bar. */
@Composable
fun feedPadding(): PaddingValues {
    val top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    return PaddingValues(top = top + 88.dp, bottom = bottom + 120.dp)
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
 * weight with the "TV+" in the profile's accent colour. It scales with the display font like the rest of the chrome.
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
            // a Latin wordmark in a Hebrew layout would otherwise render as "+TV"
            style = TextStyle(textDirection = TextDirection.Ltr),
        )
    }
}

/**
 * Kan Box / Apple TV header: the app wordmark perfectly centred, the profile's emoji avatar on the leading side and a
 * minimal glass circle with only a bold gear on the trailing side. The frosted band fades out downwards so the feed
 * scrolls behind it. The avatar is a shared element: it flies here from the "who's watching" page.
 */
@Composable
fun AppHeader(
    profileId: String,
    avatar: String,
    onAvatar: () -> Unit,
    onSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scheme = MaterialTheme.colorScheme
    val haptic = LocalHapticFeedback.current
    Box(modifier.fillMaxWidth().zIndex(10f)) {
        Box(Modifier.matchParentSize().frostedGlass(
            RectangleShape, glassTint(), blur = 36.dp, border = false,
            mask = Brush.verticalGradient(0f to Color.Black, 0.65f to Color.Black, 1f to Color.Transparent),
        ))
        Box(Modifier.statusBarsPadding().height(64.dp).fillMaxWidth()) {
            // profile portrait: leading edge (the physical right in Hebrew)
            val avatarInteraction = remember { MutableInteractionSource() }
            Box(
                Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 20.dp)
                    .size(42.dp)
                    .pressPhysics(avatarInteraction, tilt = false)
                    .sharedAvatar("avatar:$profileId")
                    .clip(RoundedCornerShape(14.dp))
                    .background(Brush.linearGradient(listOf(scheme.primaryContainer, scheme.tertiaryContainer)))
                    .specularBorder(RoundedCornerShape(14.dp), alpha = 0.32f)
                    .clickable(avatarInteraction, null) {
                        haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                        onAvatar()
                    },
                contentAlignment = Alignment.Center,
            ) {
                ProfileAvatar(avatar, glyphSize = 27.dp)
            }

            BrandWordmark(Modifier.align(Alignment.Center))

            // settings: trailing edge, one glass circle and nothing else inside it
            val settingsInteraction = remember { MutableInteractionSource() }
            Box(
                Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 20.dp)
                    .size(44.dp)
                    .frostedGlass(CircleShape, Color(0xFF16161E).copy(alpha = .65f), blur = 36.dp)
                    .clickable(settingsInteraction, null) {
                        haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                        onSettings()
                    },
                contentAlignment = Alignment.Center,
            ) {
                BoldIcon(Icons.Rounded.Settings, "הגדרות", Modifier.size(21.dp), tint = scheme.onSurface, weight = 0.55.dp)
            }
        }
        Spacer(Modifier.statusBarsPadding().height(84.dp))
    }
}

/**
 * The Nuvio navigation: a standalone glass circle holding only a bold search glyph on the physical left, and 16 dp
 * away a glass capsule whose tabs stack a bold icon directly above its label - the shape of the reference bar, with the
 * heavy glass tint (Color(0xFF16161E) at 65%) on both elements.
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
    val glass = Color(0xFF16161E).copy(alpha = .65f)
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        BoxWithConstraints(modifier.fillMaxWidth().zIndex(10f).navigationBarsPadding().padding(bottom = 18.dp)) {
            // 16 dp side margins + 64 dp search circle + 16 dp gap + the capsule; collapses slightly on scroll
            val capsuleTarget = (maxWidth - 112.dp - if (collapsed) 30.dp else 0.dp).coerceAtLeast(200.dp)
            val capsuleWidth by animateDpAsState(
                capsuleTarget,
                spring(dampingRatio = .82f, stiffness = Spring.StiffnessMediumLow),
                label = "capsuleWidth",
            )
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    Modifier.size(64.dp).frostedGlass(CircleShape, glass, blur = 36.dp)
                        .clickable(remember { MutableInteractionSource() }, null) {
                            haptic.performHapticFeedback(HapticFeedbackType.SegmentTick)
                            onSearch()
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    BoldIcon(Icons.Rounded.Search, "חיפוש", Modifier.size(26.dp), tint = scheme.onSurface, weight = 0.6.dp)
                }

                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Row(
                        Modifier.width(capsuleWidth).height(68.dp).frostedGlass(RoundedCornerShape(50), glass, blur = 36.dp)
                            .padding(horizontal = 6.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp), verticalAlignment = Alignment.CenterVertically,
                    ) {
                        tabs.forEach { tab ->
                            val active = !searchOpen && selected == tab
                            Column(
                                Modifier.weight(1f).fillMaxHeight().clip(RoundedCornerShape(50))
                                    .background(if (active) Color.White.copy(alpha = .16f) else Color.Transparent)
                                    .clickable(remember { MutableInteractionSource() }, null) {
                                        haptic.performHapticFeedback(HapticFeedbackType.SegmentTick)
                                        onSelect(tab)
                                    },
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                            ) {
                                BoldIcon(
                                    tab.icon, tab.label, Modifier.size(22.dp),
                                    tint = if (active) scheme.onSurface else scheme.onSurfaceVariant,
                                    weight = if (active) 0.6.dp else 0.45.dp,
                                )
                                Spacer(Modifier.height(3.dp))
                                Text(
                                    tab.label,
                                    color = if (active) scheme.onSurface else scheme.onSurfaceVariant,
                                    fontSize = 10.5.sp,
                                    fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
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

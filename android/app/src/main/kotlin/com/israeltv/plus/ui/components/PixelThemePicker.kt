package com.israeltv.plus.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BatteryFull
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.israeltv.plus.data.AppPrefs
import com.israeltv.plus.ui.theme.PixelPalette
import com.israeltv.plus.ui.theme.getPixelPalettes

/**
 * Native Pixel 'Wallpaper & Style' Color Palette Picker:
 * - Live interactive phone device frame preview (190dp x 340dp)
 * - Pixel Tri-Tone color swatches with squircle morph & white selection ring
 * - M3 Expressive Dark theme switch with thumb checkmark
 * - Segmented pill switcher: Wallpaper colors vs Other colors
 */
@Composable
fun PixelThemePicker(
    prefs: AppPrefs,
    modifier: Modifier = Modifier,
) {
    val ctx = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val palettes = remember(ctx) { getPixelPalettes(ctx) }

    val isDark = prefs.isDarkTheme
    val isWallpaper = prefs.isWallpaperColors
    val selectedIndex = prefs.selectedPaletteIndex

    val activePalette = if (isWallpaper) {
        palettes.first()
    } else {
        palettes.getOrNull(selectedIndex.coerceIn(0, palettes.size - 1)) ?: palettes.first()
    }

    val previewScheme = if (isDark) activePalette.darkScheme else activePalette.lightScheme

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // 1. Live Interactive Miniature Phone Device Frame Preview
        LiveDevicePreview(
            palette = activePalette,
            isDark = isDark,
            modifier = Modifier.padding(bottom = 24.dp),
        )

        // 2. Rounded Surface Container holding Swatches, Dark Mode Switch and Segmented Switcher
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .specularBorder(RoundedCornerShape(28.dp), alpha = 0.2f)
                .padding(20.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Segmented Pill Switcher: 'צבעי מערכת' (Wallpaper) vs 'צבעים נוספים' (Other)
                SegmentedPillSwitcher(
                    isWallpaper = isWallpaper,
                    onToggle = { wallpaper ->
                        haptic.performHapticFeedback(HapticFeedbackType.SegmentTick)
                        prefs.setWallpaperColorsMode(wallpaper)
                    },
                    modifier = Modifier.padding(bottom = 20.dp),
                )

                // Swatches Row
                if (isWallpaper) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        PixelTriToneSwatch(
                            palette = palettes.first(),
                            isSelected = true,
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                            },
                        )
                    }
                } else {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp, Alignment.CenterHorizontally),
                    ) {
                        itemsIndexed(palettes.drop(1)) { index, palette ->
                            val actualIndex = index + 1
                            val isSelected = !isWallpaper && selectedIndex == actualIndex
                            PixelTriToneSwatch(
                                palette = palette,
                                isSelected = isSelected,
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                                    prefs.selectPalette(actualIndex)
                                    prefs.setWallpaperColorsMode(false)
                                },
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Divider
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                )

                Spacer(Modifier.height(14.dp))

                // Dark Theme Toggle Row with M3 Expressive Switch
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            val newDark = !isDark
                            haptic.performHapticFeedback(if (newDark) HapticFeedbackType.ToggleOn else HapticFeedbackType.ToggleOff)
                            prefs.updateDarkTheme(newDark)
                        }
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text(
                            text = "מצב כהה",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = if (isDark) "פעיל כעת" else "כבוי (מצב בהיר)",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }

                    Switch(
                        checked = isDark,
                        onCheckedChange = { dark ->
                            haptic.performHapticFeedback(if (dark) HapticFeedbackType.ToggleOn else HapticFeedbackType.ToggleOff)
                            prefs.updateDarkTheme(dark)
                        },
                        thumbContent = if (isDark) {
                            {
                                Icon(
                                    imageVector = Icons.Rounded.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize),
                                )
                            }
                        } else null,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                        ),
                    )
                }
            }
        }
    }
}

/**
 * Pixel Tri-Tone Swatch (56.dp):
 * Top half (180°): primaryContainer / accent tone
 * Bottom-Left quadrant (90°): surfaceVariant / dark neutral
 * Bottom-Right quadrant (90°): tertiary / secondary accent
 * Unselected: 50% circle
 * Selected: morphs to 18.dp squircle surrounded by a 2.dp crisp white ring with 4.dp gap
 */
@Composable
fun PixelTriToneSwatch(
    palette: PixelPalette,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val cornerRadius by animateDpAsState(
        targetValue = if (isSelected) 18.dp else 28.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow),
        label = "swatchCorner",
    )

    val ringAlpha by animateColorAsState(
        targetValue = if (isSelected) Color.White else Color.Transparent,
        label = "ringAlpha",
    )

    Box(
        modifier = modifier
            .size(68.dp)
            .clickable(remember { MutableInteractionSource() }, null, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        // Outer 2dp crisp white ring with 4dp gap when selected
        if (isSelected) {
            Box(
                Modifier
                    .size(68.dp)
                    .border(2.dp, ringAlpha, RoundedCornerShape(22.dp))
            )
        }

        // Inner 56dp Tri-Tone Swatch
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(cornerRadius))
                .shadow(elevation = if (isSelected) 8.dp else 2.dp, shape = RoundedCornerShape(cornerRadius)),
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Top Half: 180° arc (from 180° to 360°) -> primaryContainer / primary
                drawArc(
                    color = palette.primary,
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = true,
                    topLeft = Offset.Zero,
                    size = Size(w, h),
                )

                // Bottom-Left Quadrant: 90° arc (from 90° to 180°) -> surfaceVariant / dark neutral
                drawArc(
                    color = palette.surfaceVariant,
                    startAngle = 90f,
                    sweepAngle = 90f,
                    useCenter = true,
                    topLeft = Offset.Zero,
                    size = Size(w, h),
                )

                // Bottom-Right Quadrant: 90° arc (from 0° to 90°) -> tertiary / secondary accent
                drawArc(
                    color = palette.tertiary,
                    startAngle = 0f,
                    sweepAngle = 90f,
                    useCenter = true,
                    topLeft = Offset.Zero,
                    size = Size(w, h),
                )
            }
        }
    }
}

/** Segmented Pill Switcher toggling between Wallpaper colors and Other colors */
@Composable
private fun SegmentedPillSwitcher(
    isWallpaper: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pillShape = RoundedCornerShape(50)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(pillShape)
            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            .padding(4.dp),
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(pillShape)
                .background(if (isWallpaper) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                .clickable { onToggle(true) },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "צבעי מערכת",
                fontSize = 13.sp,
                fontWeight = if (isWallpaper) FontWeight.ExtraBold else FontWeight.Medium,
                color = if (isWallpaper) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(pillShape)
                .background(if (!isWallpaper) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                .clickable { onToggle(false) },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "צבעים נוספים",
                fontSize = 13.sp,
                fontWeight = if (!isWallpaper) FontWeight.ExtraBold else FontWeight.Medium,
                color = if (!isWallpaper) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/**
 * Live Miniature Phone Preview Card (190.dp x 340.dp):
 * Renders a mini-mockup of the app with mini hero, mini tiles and mini navbar,
 * reacting instantaneously to color and dark-mode changes.
 */
@Composable
fun LiveDevicePreview(
    palette: PixelPalette,
    isDark: Boolean,
    modifier: Modifier = Modifier,
) {
    val scheme = if (isDark) palette.darkScheme else palette.lightScheme
    val frameShape = RoundedCornerShape(28.dp)

    Box(
        modifier = modifier
            .width(190.dp)
            .height(340.dp)
            .shadow(16.dp, frameShape)
            .clip(frameShape)
            .background(scheme.surfaceContainerLowest)
            .border(1.5.dp, Color.White.copy(alpha = 0.22f), frameShape),
    ) {
        // Mini Status Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "09:41",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = scheme.onSurface,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(Icons.Rounded.Wifi, null, Modifier.size(10.dp), tint = scheme.onSurface)
                Icon(Icons.Rounded.BatteryFull, null, Modifier.size(10.dp), tint = scheme.onSurface)
            }
        }

        // Mini App Content Feed
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 22.dp, bottom = 44.dp)
                .padding(horizontal = 10.dp),
        ) {
            // Mini Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "ישראל TV+",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = scheme.onSurface,
                )
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(scheme.primaryContainer),
                )
            }

            Spacer(Modifier.height(4.dp))

            // Mini Hero Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(95.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(scheme.primaryContainer, scheme.surfaceContainerHigh)
                        )
                    ),
            ) {
                // Mini LIVE tag + title
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(6.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color(0xFFE53935))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text("LIVE", fontSize = 6.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Text("כאן 11 HD", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = scheme.onPrimaryContainer)
                    Spacer(Modifier.height(3.dp))
                    Box(
                        modifier = Modifier
                            .height(18.dp)
                            .clip(RoundedCornerShape(50))
                            .background(scheme.primary)
                            .padding(horizontal = 8.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.PlayArrow, null, Modifier.size(10.dp), tint = scheme.onPrimary)
                            Text("צפייה", fontSize = 7.sp, fontWeight = FontWeight.Bold, color = scheme.onPrimary)
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Mini Shelf Header
            Text(
                text = "ערוצי ישראל",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = scheme.onSurface,
            )

            Spacer(Modifier.height(4.dp))

            // Mini Channel Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                repeat(2) { idx ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(55.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(scheme.surfaceContainerHigh)
                            .border(0.5.dp, Color.White.copy(0.15f), RoundedCornerShape(10.dp))
                            .padding(4.dp),
                    ) {
                        Text(
                            text = if (idx == 0) "קשת 12" else "רשת 13",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = scheme.onSurface,
                            modifier = Modifier.align(Alignment.BottomStart)
                        )
                    }
                }
            }
        }

        // Mini Floating Glass Navbar at Bottom
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 6.dp)
                .width(160.dp)
                .height(32.dp)
                .clip(RoundedCornerShape(50))
                .background(scheme.surfaceContainerHighest.copy(alpha = 0.85f))
                .border(0.5.dp, Color.White.copy(0.2f), RoundedCornerShape(50)),
            contentAlignment = Alignment.Center,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Active pill tab
                Box(
                    modifier = Modifier
                        .height(22.dp)
                        .width(42.dp)
                        .clip(RoundedCornerShape(50))
                        .background(scheme.primaryContainer),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("בית", fontSize = 7.sp, fontWeight = FontWeight.Bold, color = scheme.onPrimaryContainer)
                }

                Text("ערוצים", fontSize = 7.sp, color = scheme.onSurfaceVariant)
                Text("שלי", fontSize = 7.sp, color = scheme.onSurfaceVariant)
                Text("הגדרות", fontSize = 7.sp, color = scheme.onSurfaceVariant)
            }
        }
    }
}

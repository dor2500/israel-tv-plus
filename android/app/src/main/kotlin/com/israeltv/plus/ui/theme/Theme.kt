package com.israeltv.plus.ui.theme

import android.os.Build
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import com.israeltv.plus.ui.components.RubberBandOverscrollFactory

/** Before Android 12 (no wallpaper colours): a deep-blue Material You palette. */
private val FallbackDark = darkColorScheme(
    primary = Color(0xFFA8C8FF),
    onPrimary = Color(0xFF05305F),
    primaryContainer = Color(0xFF254777),
    onPrimaryContainer = Color(0xFFD5E3FF),
    secondaryContainer = Color(0xFF3B4858),
    onSecondaryContainer = Color(0xFFD7E3F8),
    surface = Color(0xFF111318),
    surfaceContainerLowest = Color(0xFF0C0E13),
    surfaceContainerLow = Color(0xFF191C20),
    surfaceContainer = Color(0xFF1D2024),
    surfaceContainerHigh = Color(0xFF282A2F),
    surfaceContainerHighest = Color(0xFF33353A),
)

private val Base = Typography()

/** Bold display typography: Material 3 Expressive headings, heavier than the defaults. */
private val ItvTypography = Base.copy(
    displayLarge = Base.displayLarge.copy(fontWeight = FontWeight.ExtraBold),
    displayMedium = Base.displayMedium.copy(fontWeight = FontWeight.ExtraBold),
    displaySmall = Base.displaySmall.copy(fontWeight = FontWeight.ExtraBold),
    headlineLarge = Base.headlineLarge.copy(fontWeight = FontWeight.ExtraBold),
    headlineMedium = Base.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
    headlineSmall = Base.headlineSmall.copy(fontWeight = FontWeight.Bold),
    titleLarge = Base.titleLarge.copy(fontWeight = FontWeight.Bold),
    titleMedium = Base.titleMedium.copy(fontWeight = FontWeight.Bold),
    labelLarge = Base.labelLarge.copy(fontWeight = FontWeight.Bold),
)

/**
 * Material 3 Expressive in the phone's own colours (Android 12+ wallpaper palette, always dark like Apple TV),
 * the expressive motion scheme, Hebrew right-to-left layout everywhere and the rubber-band overscroll on every
 * scroll container.
 */
@Composable
fun ItvTheme(content: @Composable () -> Unit) {
    val scheme = if (Build.VERSION.SDK_INT >= 31) dynamicDarkColorScheme(LocalContext.current) else FallbackDark
    MaterialExpressiveTheme(colorScheme = scheme, motionScheme = MotionScheme.expressive(), typography = ItvTypography) {
        CompositionLocalProvider(
            LocalLayoutDirection provides LayoutDirection.Rtl,
            LocalOverscrollFactory provides RubberBandOverscrollFactory,
            content = content,
        )
    }
}

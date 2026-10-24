package com.israeltv.plus.ui.theme

import android.content.Context
import android.os.Build
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.sp
import com.israeltv.plus.data.AppPrefs
import com.israeltv.plus.ui.components.RubberBandOverscrollFactory

data class PixelPalette(
    val id: String,
    val name: String,
    val primary: Color,
    val surfaceVariant: Color,
    val tertiary: Color,
    val darkScheme: ColorScheme,
    val lightScheme: ColorScheme,
)

/** Fallback Monet Blue palette for pre-Android 12 or default. */
val FallbackMonetDark = darkColorScheme(
    primary = Color(0xFFA8C8FF),
    onPrimary = Color(0xFF05305F),
    primaryContainer = Color(0xFF254777),
    onPrimaryContainer = Color(0xFFD5E3FF),
    secondary = Color(0xFFBDC7DC),
    onSecondary = Color(0xFF273141),
    secondaryContainer = Color(0xFF3B4858),
    onSecondaryContainer = Color(0xFFD7E3F8),
    tertiary = Color(0xFFDABCE2),
    onTertiary = Color(0xFF3E2846),
    tertiaryContainer = Color(0xFF553E5E),
    onTertiaryContainer = Color(0xFFF7D8FF),
    surface = Color(0xFF111318),
    onSurface = Color(0xFFE2E2E9),
    surfaceVariant = Color(0xFF43474E),
    onSurfaceVariant = Color(0xFFC3C7D0),
    surfaceContainerLowest = Color(0xFF0C0E13),
    surfaceContainerLow = Color(0xFF191C20),
    surfaceContainer = Color(0xFF1D2024),
    surfaceContainerHigh = Color(0xFF282A2F),
    surfaceContainerHighest = Color(0xFF33353A),
)

val FallbackMonetLight = lightColorScheme(
    primary = Color(0xFF0061A4),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD1E4FF),
    onPrimaryContainer = Color(0xFF001D36),
    secondary = Color(0xFF535F70),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFD7E3F8),
    onSecondaryContainer = Color(0xFF101C2B),
    tertiary = Color(0xFF6B5778),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFF3DAFF),
    onTertiaryContainer = Color(0xFF251432),
    surface = Color(0xFFF8F9FF),
    onSurface = Color(0xFF191C20),
    surfaceVariant = Color(0xFFDFE2EB),
    onSurfaceVariant = Color(0xFF43474E),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF2F3FA),
    surfaceContainer = Color(0xFFECEEF4),
    surfaceContainerHigh = Color(0xFFE6E8EE),
    surfaceContainerHighest = Color(0xFFE1E2E8),
)

// Ocean Blue Curated Palette
val OceanDark = darkColorScheme(
    primary = Color(0xFF38BDF8),
    onPrimary = Color(0xFF082F49),
    primaryContainer = Color(0xFF0369A1),
    onPrimaryContainer = Color(0xFFE0F2FE),
    secondary = Color(0xFF7DD3FC),
    onSecondary = Color(0xFF0C4A6E),
    secondaryContainer = Color(0xFF1E293B),
    onSecondaryContainer = Color(0xFFBAE6FD),
    tertiary = Color(0xFF38BDF8),
    onTertiary = Color(0xFF082F49),
    tertiaryContainer = Color(0xFF0284C7),
    onTertiaryContainer = Color(0xFFF0F9FF),
    surface = Color(0xFF0B132B),
    onSurface = Color(0xFFF0F9FF),
    surfaceVariant = Color(0xFF1E293B),
    onSurfaceVariant = Color(0xFF94A3B8),
    surfaceContainerLowest = Color(0xFF060B18),
    surfaceContainerLow = Color(0xFF0B132B),
    surfaceContainer = Color(0xFF111E3D),
    surfaceContainerHigh = Color(0xFF1A2B50),
    surfaceContainerHighest = Color(0xFF233763),
)
val OceanLight = lightColorScheme(
    primary = Color(0xFF0284C7),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE0F2FE),
    onPrimaryContainer = Color(0xFF082F49),
    secondary = Color(0xFF0369A1),
    secondaryContainer = Color(0xFFBAE6FD),
    tertiary = Color(0xFF0EA5E9),
    surface = Color(0xFFF0F9FF),
    onSurface = Color(0xFF082F49),
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = Color(0xFF475569),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainer = Color(0xFFE0F2FE),
)

// Emerald Forest Curated Palette
val EmeraldDark = darkColorScheme(
    primary = Color(0xFF34D399),
    onPrimary = Color(0xFF064E3B),
    primaryContainer = Color(0xFF059669),
    onPrimaryContainer = Color(0xFFD1FAE5),
    secondary = Color(0xFF6EE7B7),
    onSecondary = Color(0xFF064E3B),
    secondaryContainer = Color(0xFF064E3B),
    onSecondaryContainer = Color(0xFFA7F3D0),
    tertiary = Color(0xFF10B981),
    onTertiary = Color(0xFF022C22),
    tertiaryContainer = Color(0xFF047857),
    onTertiaryContainer = Color(0xFFECFDF5),
    surface = Color(0xFF061A14),
    onSurface = Color(0xFFECFDF5),
    surfaceVariant = Color(0xFF132E24),
    onSurfaceVariant = Color(0xFF9CA3AF),
    surfaceContainerLowest = Color(0xFF030E0B),
    surfaceContainerLow = Color(0xFF061A14),
    surfaceContainer = Color(0xFF0C2B22),
    surfaceContainerHigh = Color(0xFF153C30),
    surfaceContainerHighest = Color(0xFF1F4C3E),
)
val EmeraldLight = lightColorScheme(
    primary = Color(0xFF059669),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD1FAE5),
    onPrimaryContainer = Color(0xFF064E3B),
    secondary = Color(0xFF10B981),
    secondaryContainer = Color(0xFFA7F3D0),
    tertiary = Color(0xFF047857),
    surface = Color(0xFFF2FBF7),
    onSurface = Color(0xFF064E3B),
    surfaceVariant = Color(0xFFE5E7EB),
    onSurfaceVariant = Color(0xFF4B5563),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainer = Color(0xFFD1FAE5),
)

// Sunset Coral Curated Palette
val SunsetDark = darkColorScheme(
    primary = Color(0xFFFF6B6B),
    onPrimary = Color(0xFF4A0E17),
    primaryContainer = Color(0xFFC92A2A),
    onPrimaryContainer = Color(0xFFFFE3E3),
    secondary = Color(0xFFFFA8A8),
    onSecondary = Color(0xFF4A0E17),
    secondaryContainer = Color(0xFF3D1E24),
    onSecondaryContainer = Color(0xFFFFC9C9),
    tertiary = Color(0xFFFA5252),
    onTertiary = Color(0xFF38070F),
    tertiaryContainer = Color(0xFFA61E4D),
    onTertiaryContainer = Color(0xFFFFF0F6),
    surface = Color(0xFF1A0A0E),
    onSurface = Color(0xFFFFF5F5),
    surfaceVariant = Color(0xFF3D1E24),
    onSurfaceVariant = Color(0xFFB09A9D),
    surfaceContainerLowest = Color(0xFF100508),
    surfaceContainerLow = Color(0xFF1A0A0E),
    surfaceContainer = Color(0xFF281117),
    surfaceContainerHigh = Color(0xFF361821),
    surfaceContainerHighest = Color(0xFF46212C),
)
val SunsetLight = lightColorScheme(
    primary = Color(0xFFE03131),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFE3E3),
    onPrimaryContainer = Color(0xFF4A0E17),
    secondary = Color(0xFFFA5252),
    secondaryContainer = Color(0xFFFFC9C9),
    tertiary = Color(0xFFC92A2A),
    surface = Color(0xFFFFF5F5),
    onSurface = Color(0xFF4A0E17),
    surfaceVariant = Color(0xFFF1F3F5),
    onSurfaceVariant = Color(0xFF495057),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainer = Color(0xFFFFE3E3),
)

// Royal Violet Curated Palette
val VioletDark = darkColorScheme(
    primary = Color(0xFFA855F7),
    onPrimary = Color(0xFF3B0764),
    primaryContainer = Color(0xFF7E22CE),
    onPrimaryContainer = Color(0xFFF3E8FF),
    secondary = Color(0xFFC084FC),
    onSecondary = Color(0xFF3B0764),
    secondaryContainer = Color(0xFF2E1065),
    onSecondaryContainer = Color(0xFFE9D5FF),
    tertiary = Color(0xFF9333EA),
    onTertiary = Color(0xFF240046),
    tertiaryContainer = Color(0xFF6B21A8),
    onTertiaryContainer = Color(0xFFFAF5FF),
    surface = Color(0xFF13091F),
    onSurface = Color(0xFFFAF5FF),
    surfaceVariant = Color(0xFF2A1B3D),
    onSurfaceVariant = Color(0xFFA89BB5),
    surfaceContainerLowest = Color(0xFF0C0514),
    surfaceContainerLow = Color(0xFF13091F),
    surfaceContainer = Color(0xFF1D102F),
    surfaceContainerHigh = Color(0xFF281740),
    surfaceContainerHighest = Color(0xFF341F53),
)
val VioletLight = lightColorScheme(
    primary = Color(0xFF7E22CE),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFF3E8FF),
    onPrimaryContainer = Color(0xFF3B0764),
    secondary = Color(0xFF9333EA),
    secondaryContainer = Color(0xFFE9D5FF),
    tertiary = Color(0xFFA855F7),
    surface = Color(0xFFFAF5FF),
    onSurface = Color(0xFF3B0764),
    surfaceVariant = Color(0xFFF3F4F6),
    onSurfaceVariant = Color(0xFF4B5563),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainer = Color(0xFFF3E8FF),
)

// Amber Gold Curated Palette
val AmberDark = darkColorScheme(
    primary = Color(0xFFFBBF24),
    onPrimary = Color(0xFF451A03),
    primaryContainer = Color(0xFFD97706),
    onPrimaryContainer = Color(0xFFFEF3C7),
    secondary = Color(0xFFFCD34D),
    onSecondary = Color(0xFF451A03),
    secondaryContainer = Color(0xFF451A03),
    onSecondaryContainer = Color(0xFFFDE68A),
    tertiary = Color(0xFFF59E0B),
    onTertiary = Color(0xFF270F02),
    tertiaryContainer = Color(0xFFB45309),
    onTertiaryContainer = Color(0xFFFFFBEB),
    surface = Color(0xFF191107),
    onSurface = Color(0xFFFFFBEB),
    surfaceVariant = Color(0xFF2E2010),
    onSurfaceVariant = Color(0xFFAFA392),
    surfaceContainerLowest = Color(0xFF0E0A04),
    surfaceContainerLow = Color(0xFF191107),
    surfaceContainer = Color(0xFF261B0B),
    surfaceContainerHigh = Color(0xFF352610),
    surfaceContainerHighest = Color(0xFF453216),
)
val AmberLight = lightColorScheme(
    primary = Color(0xFFD97706),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFEF3C7),
    onPrimaryContainer = Color(0xFF451A03),
    secondary = Color(0xFFF59E0B),
    secondaryContainer = Color(0xFFFDE68A),
    tertiary = Color(0xFFB45309),
    surface = Color(0xFFFFFBEB),
    onSurface = Color(0xFF451A03),
    surfaceVariant = Color(0xFFF3F4F6),
    onSurfaceVariant = Color(0xFF4B5563),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainer = Color(0xFFFEF3C7),
)

fun getPixelPalettes(context: Context): List<PixelPalette> {
    val dynamicDark = if (Build.VERSION.SDK_INT >= 31) dynamicDarkColorScheme(context) else FallbackMonetDark
    val dynamicLight = if (Build.VERSION.SDK_INT >= 31) dynamicLightColorScheme(context) else FallbackMonetLight

    return listOf(
        PixelPalette(
            id = "monet",
            name = "צבעי מערכת",
            primary = dynamicDark.primary,
            surfaceVariant = dynamicDark.surfaceVariant,
            tertiary = dynamicDark.tertiary,
            darkScheme = dynamicDark,
            lightScheme = dynamicLight,
        ),
        PixelPalette(
            id = "ocean",
            name = "אוקיינוס",
            primary = Color(0xFF38BDF8),
            surfaceVariant = Color(0xFF1E293B),
            tertiary = Color(0xFF0284C7),
            darkScheme = OceanDark,
            lightScheme = OceanLight,
        ),
        PixelPalette(
            id = "emerald",
            name = "אמרלד",
            primary = Color(0xFF10B981),
            surfaceVariant = Color(0xFF064E3B),
            tertiary = Color(0xFF059669),
            darkScheme = EmeraldDark,
            lightScheme = EmeraldLight,
        ),
        PixelPalette(
            id = "sunset",
            name = "שקיעה",
            primary = Color(0xFFFF6B6B),
            surfaceVariant = Color(0xFF3D1E24),
            tertiary = Color(0xFFFA5252),
            darkScheme = SunsetDark,
            lightScheme = SunsetLight,
        ),
        PixelPalette(
            id = "violet",
            name = "סגול רויאל",
            primary = Color(0xFFA855F7),
            surfaceVariant = Color(0xFF2E1065),
            tertiary = Color(0xFF9333EA),
            darkScheme = VioletDark,
            lightScheme = VioletLight,
        ),
        PixelPalette(
            id = "amber",
            name = "ענבר מוזהב",
            primary = Color(0xFFF59E0B),
            surfaceVariant = Color(0xFF451A03),
            tertiary = Color(0xFFD97706),
            darkScheme = AmberDark,
            lightScheme = AmberLight,
        ),
    )
}

private val Base = Typography()

/** Bold display typography with tight optical tracking and heavy display weights. */
private val ItvTypography = Base.copy(
    displayLarge = Base.displayLarge.copy(fontWeight = FontWeight.Black, letterSpacing = (-0.4).sp),
    displayMedium = Base.displayMedium.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.4).sp),
    displaySmall = Base.displaySmall.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.4).sp),
    headlineLarge = Base.headlineLarge.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.4).sp),
    headlineMedium = Base.headlineMedium.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.4).sp),
    headlineSmall = Base.headlineSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = (-0.2).sp),
    titleLarge = Base.titleLarge.copy(fontWeight = FontWeight.Bold, letterSpacing = (-0.2).sp),
    titleMedium = Base.titleMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = (-0.2).sp),
    titleSmall = Base.titleSmall.copy(fontWeight = FontWeight.Bold),
    labelLarge = Base.labelLarge.copy(fontWeight = FontWeight.Bold),
)

/**
 * Material 3 Expressive theme connected to the Pixel Wallpaper & Style engine,
 * supporting dynamic dark/light mode and custom curated tri-tone palettes.
 */
@Composable
fun ItvTheme(
    prefs: AppPrefs? = null,
    content: @Composable () -> Unit,
) {
    val ctx = LocalContext.current
    val palettes = getPixelPalettes(ctx)
    val isDark = prefs?.isDarkTheme ?: true
    val isWallpaper = prefs?.isWallpaperColors ?: true
    val paletteIdx = prefs?.selectedPaletteIndex ?: 0

    val activePalette = if (isWallpaper) {
        palettes.first()
    } else {
        palettes.getOrNull(paletteIdx.coerceIn(0, palettes.size - 1)) ?: palettes.first()
    }

    val scheme = if (isDark) activePalette.darkScheme else activePalette.lightScheme

    MaterialExpressiveTheme(colorScheme = scheme, motionScheme = MotionScheme.expressive(), typography = ItvTypography) {
        CompositionLocalProvider(
            LocalLayoutDirection provides LayoutDirection.Rtl,
            LocalOverscrollFactory provides RubberBandOverscrollFactory,
            content = content,
        )
    }
}

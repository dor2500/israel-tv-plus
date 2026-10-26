package com.israeltv.plus.ui.theme

import android.app.Activity
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.israeltv.plus.R
import com.israeltv.plus.data.ThemeChoice
import com.israeltv.plus.ui.components.RubberBandOverscrollFactory

/** True when the app is in its dark theme (the glass recipe and a few hero colours depend on it). */
val LocalDarkTheme = staticCompositionLocalOf { true }

/**
 * Rubik Variable (res/font/rubik.ttf, SIL OFL 1.1): Hebrew, Latin and numbers in one variable font, each weight a
 * real instance of the wght axis (300-900) rather than a synthesized bold.
 */
val Rubik = FontFamily(
    listOf(300, 400, 500, 600, 700, 800, 900).map { w ->
        Font(R.font.rubik, FontWeight(w), variationSettings = FontVariation.Settings(FontVariation.weight(w)))
    }
)

private val Base = Typography()
private val Tight = (-0.4).sp

private fun TextStyle.rubik(weight: FontWeight? = null, tracking: Boolean = false) = copy(
    fontFamily = Rubik,
    fontWeight = weight ?: fontWeight,
    letterSpacing = if (tracking) Tight else 0.sp,
)

/** Heavy display weights with tight optical tracking; body text in Rubik without the Latin letter-spacing. */
private val ItvTypography = Typography(
    displayLarge = Base.displayLarge.rubik(FontWeight.Black, true),
    displayMedium = Base.displayMedium.rubik(FontWeight.Black, true),
    displaySmall = Base.displaySmall.rubik(FontWeight.ExtraBold, true),
    headlineLarge = Base.headlineLarge.rubik(FontWeight.ExtraBold, true),
    headlineMedium = Base.headlineMedium.rubik(FontWeight.ExtraBold, true),
    headlineSmall = Base.headlineSmall.rubik(FontWeight.ExtraBold, true),
    titleLarge = Base.titleLarge.rubik(FontWeight.ExtraBold, true),
    titleMedium = Base.titleMedium.rubik(FontWeight.Bold),
    titleSmall = Base.titleSmall.rubik(FontWeight.Bold),
    bodyLarge = Base.bodyLarge.rubik(),
    bodyMedium = Base.bodyMedium.rubik(),
    bodySmall = Base.bodySmall.rubik(),
    labelLarge = Base.labelLarge.rubik(FontWeight.Bold),
    labelMedium = Base.labelMedium.rubik(FontWeight.Medium),
    labelSmall = Base.labelSmall.rubik(FontWeight.Medium),
)

/**
 * Material 3 Expressive in the active profile's colours (the phone's Material You palette, a wallpaper colour or a
 * curated one; dark or light), with a 450 ms cross-fade of every colour when the palette changes, Rubik Variable,
 * Hebrew right-to-left layout everywhere and the rubber-band overscroll on every scroll container.
 */
@Composable
fun ItvTheme(choice: ThemeChoice, content: @Composable () -> Unit) {
    val context = LocalContext.current
    val target = remember(choice) { schemeFor(context, choice) }
    val scheme = animateScheme(target)
    val view = LocalView.current
    if (!view.isInEditMode) SideEffect {
        val window = (view.context as? Activity)?.window ?: return@SideEffect
        WindowCompat.getInsetsController(window, view).apply {
            isAppearanceLightStatusBars = !choice.dark
            isAppearanceLightNavigationBars = !choice.dark
        }
    }
    MaterialExpressiveTheme(colorScheme = scheme, motionScheme = MotionScheme.expressive(), typography = ItvTypography) {
        CompositionLocalProvider(
            LocalDarkTheme provides choice.dark,
            LocalLayoutDirection provides LayoutDirection.Rtl,
            LocalOverscrollFactory provides RubberBandOverscrollFactory,
            content = content,
        )
    }
}

/** Every colour role animates to the new palette (Material You's own colour transition). */
@Composable
private fun animateScheme(t: ColorScheme): ColorScheme {
    val spec: AnimationSpec<Color> = tween(450)
    @Composable fun a(c: Color): Color = animateColorAsState(c, spec, label = "scheme").value
    return t.copy(
        primary = a(t.primary), onPrimary = a(t.onPrimary),
        primaryContainer = a(t.primaryContainer), onPrimaryContainer = a(t.onPrimaryContainer),
        secondary = a(t.secondary), onSecondary = a(t.onSecondary),
        secondaryContainer = a(t.secondaryContainer), onSecondaryContainer = a(t.onSecondaryContainer),
        tertiary = a(t.tertiary), onTertiary = a(t.onTertiary),
        tertiaryContainer = a(t.tertiaryContainer), onTertiaryContainer = a(t.onTertiaryContainer),
        background = a(t.background), onBackground = a(t.onBackground),
        surface = a(t.surface), onSurface = a(t.onSurface),
        surfaceVariant = a(t.surfaceVariant), onSurfaceVariant = a(t.onSurfaceVariant),
        surfaceTint = a(t.surfaceTint), outline = a(t.outline), outlineVariant = a(t.outlineVariant),
        surfaceContainerLowest = a(t.surfaceContainerLowest), surfaceContainerLow = a(t.surfaceContainerLow),
        surfaceContainer = a(t.surfaceContainer), surfaceContainerHigh = a(t.surfaceContainerHigh),
        surfaceContainerHighest = a(t.surfaceContainerHighest),
    )
}


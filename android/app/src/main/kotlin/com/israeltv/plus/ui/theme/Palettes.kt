package com.israeltv.plus.ui.theme

import android.app.WallpaperManager
import android.content.Context
import android.os.Build
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.israeltv.plus.data.ThemeChoice
import com.materialkolor.PaletteStyle
import com.materialkolor.dynamicColorScheme

/** One swatch of the colour picker: the phone's own palette (key "system") or a seed colour in a palette style. */
@Immutable
data class PaletteOption(val key: String, val seed: Color, val style: PaletteStyle, val wallpaper: Boolean) {
    fun choice(dark: Boolean) = ThemeChoice(key, seed.toArgb().toLong() and 0xFFFFFFFF, style.name, wallpaper, dark)
}

private val AppleTvBlue = Color(0xFF0A84FF)

/** "צבעים נוספים": Vibrant high-contrast Cinematic Neon seeds. */
val CuratedPalettes: List<PaletteOption> = listOf(
    PaletteOption("cyan", Color(0xFF00E5FF), PaletteStyle.Vibrant, wallpaper = false),      // Electric Cyan
    PaletteOption("crimson", Color(0xFFFF1744), PaletteStyle.Vibrant, wallpaper = false),   // Crimson Red
    PaletteOption("violet", Color(0xFFD500F9), PaletteStyle.Expressive, wallpaper = false), // Neon Violet
    PaletteOption("amber", Color(0xFFFFC400), PaletteStyle.Vibrant, wallpaper = false),     // Vibrant Amber
    PaletteOption("appletv", Color(0xFF0A84FF), PaletteStyle.Vibrant, wallpaper = false),   // Apple TV Blue
)

/**
 * "צבעי מערכת": the phone's own Material You palette first (Android 12+), then palettes from the wallpaper's main
 * colours in the styles Pixel offers (vibrant / expressive / tonal). Falls back to the curated set without a wallpaper.
 */
fun wallpaperPalettes(context: Context): List<PaletteOption> {
    val out = mutableListOf<PaletteOption>()
    if (Build.VERSION.SDK_INT >= 31) out += PaletteOption("system", AppleTvBlue, PaletteStyle.Vibrant, wallpaper = true)
    val colors = if (Build.VERSION.SDK_INT >= 27) runCatching {
        WallpaperManager.getInstance(context).getWallpaperColors(WallpaperManager.FLAG_SYSTEM)
    }.getOrNull() else null
    val seeds = listOfNotNull(colors?.primaryColor, colors?.secondaryColor, colors?.tertiaryColor)
        .map { Color(it.toArgb()) }.distinct()
    seeds.firstOrNull()?.let { p ->
        if (out.isEmpty()) out += PaletteOption("w-vibrant", p, PaletteStyle.Vibrant, true)
        out += PaletteOption("w-expressive", p, PaletteStyle.Expressive, true)
        out += PaletteOption("w-tonal", p, PaletteStyle.TonalSpot, true)
    }
    seeds.drop(1).forEachIndexed { i, s -> out += PaletteOption("w-seed$i", s, PaletteStyle.Vibrant, true) }
    return out.ifEmpty { CuratedPalettes.take(4).map { it.copy(wallpaper = true) } }
}

private val cache = HashMap<String, ColorScheme>()

/** The colour scheme of a choice (cached: the picker draws many swatches). */
fun schemeFor(context: Context, choice: ThemeChoice): ColorScheme =
    schemeFor(context, choice.key, Color(choice.seed.toInt()), runCatching { PaletteStyle.valueOf(choice.style) }.getOrDefault(PaletteStyle.Vibrant), choice.dark)

fun schemeFor(context: Context, option: PaletteOption, dark: Boolean): ColorScheme =
    schemeFor(context, option.key, option.seed, option.style, dark)

private fun schemeFor(context: Context, key: String, seed: Color, style: PaletteStyle, dark: Boolean): ColorScheme {
    val rawScheme = if (key == "system" && Build.VERSION.SDK_INT >= 31) {
        if (dark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    } else {
        val id = "${seed.toArgb()}:$style:$dark"
        synchronized(cache) {
            cache.getOrPut(id) { dynamicColorScheme(seedColor = seed, isDark = dark, style = style) }
        }
    }
    if (dark) {
        val cinematicDark = Color(0xFF0A0B10)
        return rawScheme.copy(
            background = cinematicDark,
            surfaceContainerLowest = cinematicDark,
            surface = Color(0xFF10121A),
            surfaceContainerLow = Color(0xFF131520),
            surfaceContainer = Color(0xFF161826),
        )
    }
    return rawScheme
}

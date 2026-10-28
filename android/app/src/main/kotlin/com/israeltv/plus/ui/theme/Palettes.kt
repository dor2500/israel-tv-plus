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
    fun choice(dark: Boolean, oledBlack: Boolean = true) =
        ThemeChoice(key, seed.toArgb().toLong() and 0xFFFFFFFF, style.name, wallpaper, dark, oledBlack)
}

private val GoogleBlue = Color(0xFF4285F4)

/** Curated "Premium Streaming" highly saturated shades (Netflix Crimson, Apple Azure, Hulu Mint, Vibrant Amber). */
val StreamingPalettes: List<PaletteOption> = listOf(
    PaletteOption("crimson", Color(0xFFE31837), PaletteStyle.Vibrant, wallpaper = false),
    PaletteOption("azure",   Color(0xFF0A7AF5), PaletteStyle.Vibrant, wallpaper = false),
    PaletteOption("mint",    Color(0xFF10B981), PaletteStyle.Vibrant, wallpaper = false),
    PaletteOption("amber",   Color(0xFFF59E0B), PaletteStyle.Vibrant, wallpaper = false),
)

private val StreamingSeeds = mapOf(
    "crimson" to Color(0xFFE31837),
    "azure"   to Color(0xFF0A7AF5),
    "mint"    to Color(0xFF10B981),
    "amber"   to Color(0xFFF59E0B),
)

/** "צבעים נוספים": Premium Streaming shades first, followed by Pixel-like curated seeds. */
val CuratedPalettes: List<PaletteOption> = StreamingPalettes + listOf(
    0xFF4285F4, 0xFF1B6EF3, 0xFF00A3BF, 0xFF00897B, 0xFF34A853, 0xFF9E9D24,
    0xFFF9AB00, 0xFFFF7043, 0xFFEA4335, 0xFFE91E63, 0xFFAB47BC, 0xFF7E57C2,
).mapIndexed { i, argb -> PaletteOption("c$i", Color(argb), PaletteStyle.TonalSpot, wallpaper = false) }

/**
 * "צבעי מערכת": the phone's own Material You palette first (Android 12+), then palettes from the wallpaper's main
 * colours in the styles Pixel offers (vibrant / expressive / tonal). Falls back to the curated set without a wallpaper.
 */
fun wallpaperPalettes(context: Context): List<PaletteOption> {
    val out = mutableListOf<PaletteOption>()
    if (Build.VERSION.SDK_INT >= 31) out += PaletteOption("system", GoogleBlue, PaletteStyle.TonalSpot, wallpaper = true)
    val colors = if (Build.VERSION.SDK_INT >= 27) runCatching {
        WallpaperManager.getInstance(context).getWallpaperColors(WallpaperManager.FLAG_SYSTEM)
    }.getOrNull() else null
    val seeds = listOfNotNull(colors?.primaryColor, colors?.secondaryColor, colors?.tertiaryColor)
        .map { Color(it.toArgb()) }.distinct()
    seeds.firstOrNull()?.let { p ->
        if (out.isEmpty()) out += PaletteOption("w-tonal", p, PaletteStyle.TonalSpot, true)
        out += PaletteOption("w-vibrant", p, PaletteStyle.Vibrant, true)
        out += PaletteOption("w-expressive", p, PaletteStyle.Expressive, true)
    }
    seeds.drop(1).forEachIndexed { i, s -> out += PaletteOption("w-seed$i", s, PaletteStyle.TonalSpot, true) }
    return out.ifEmpty { CuratedPalettes.take(4).map { it.copy(wallpaper = true) } }
}

private val cache = HashMap<String, ColorScheme>()

/** The colour scheme of a choice (cached: the picker draws many swatches). */
fun schemeFor(context: Context, choice: ThemeChoice): ColorScheme {
    val base = schemeFor(
        context,
        choice.key,
        Color(choice.seed.toInt()),
        runCatching { PaletteStyle.valueOf(choice.style) }.getOrDefault(PaletteStyle.TonalSpot),
        choice.dark,
    )
    return if (choice.dark && choice.oledBlack) {
        base.copy(
            background = Color.Black,
            surface = Color.Black,
            surfaceContainerLowest = Color.Black,
        )
    } else {
        base
    }
}

fun schemeFor(context: Context, option: PaletteOption, dark: Boolean): ColorScheme =
    schemeFor(context, option.key, option.seed, option.style, dark)

private fun schemeFor(context: Context, key: String, seed: Color, style: PaletteStyle, dark: Boolean): ColorScheme {
    if (key == "system" && Build.VERSION.SDK_INT >= 31) {
        return if (dark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    val id = "$key:${seed.toArgb()}:$style:$dark"
    return synchronized(cache) {
        cache.getOrPut(id) {
            val base = dynamicColorScheme(seedColor = seed, isDark = dark, style = style)
            val streamingSeed = StreamingSeeds[key]
            if (streamingSeed != null && dark) {
                base.copy(
                    primary = streamingSeed,
                    onPrimary = Color.White,
                    surfaceTint = streamingSeed,
                )
            } else {
                base
            }
        }
    }
}

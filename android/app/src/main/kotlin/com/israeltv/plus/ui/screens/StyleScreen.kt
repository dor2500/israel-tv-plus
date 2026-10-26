package com.israeltv.plus.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LiveTv
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.israeltv.plus.data.AppPrefs
import com.israeltv.plus.data.Channel
import com.israeltv.plus.ui.components.ChannelLogo
import com.israeltv.plus.ui.components.GlassIconButton
import com.israeltv.plus.ui.components.rememberBitChordFlingBehavior
import com.israeltv.plus.ui.theme.CuratedPalettes
import com.israeltv.plus.ui.theme.PaletteOption
import com.israeltv.plus.ui.theme.schemeFor
import com.israeltv.plus.ui.theme.wallpaperPalettes

/**
 * "צבעים וסגנון", Pixel's Wallpaper & style: a live miniature of the app on top, then one surfaceContainer card with
 * the tri-tone swatches, the dark-theme switch and the "צבעי מערכת" / "צבעים נוספים" pill switcher. Every choice
 * applies at once to the active profile (the whole app cross-fades to it).
 */
@Composable
fun StyleScreen(prefs: AppPrefs, sample: List<Channel>, logo: (Channel) -> String, onClose: () -> Unit) {
    val context = LocalContext.current
    val scheme = MaterialTheme.colorScheme
    val haptic = LocalHapticFeedback.current
    val theme = prefs.theme
    var wallpaperTab by rememberSaveable { mutableStateOf(theme.wallpaper) }
    val wallpaper = remember { wallpaperPalettes(context) }
    val options = if (wallpaperTab) wallpaper else CuratedPalettes
    BackHandler(onBack = onClose)

    Box(
        Modifier
            .fillMaxSize()
            .background(scheme.surfaceContainerLowest)
            .clickable(remember { MutableInteractionSource() }, null) {}
    ) {
        LazyColumn(
            Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 0.dp, bottom = 40.dp),
            flingBehavior = rememberBitChordFlingBehavior(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item(key = "bar") {
                Row(
                    Modifier.statusBarsPadding().fillMaxWidth().height(64.dp).padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    GlassIconButton(
                        Icons.AutoMirrored.Rounded.ArrowBack, "חזרה", onClose, size = 44.dp,
                        tint = scheme.surfaceContainerHigh, iconTint = scheme.onSurface,
                    )
                    Spacer(Modifier.width(14.dp))
                    Text("צבעים וסגנון", style = MaterialTheme.typography.headlineMedium, color = scheme.onSurface)
                }
            }
            item(key = "preview") {
                Text(
                    "הפרופיל של ${prefs.profile.name}",
                    color = scheme.onSurfaceVariant, fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp, bottom = 14.dp),
                )
                DevicePreview(sample, logo)
                Spacer(Modifier.height(24.dp))
            }
            item(key = "card") {
                Column(
                    Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(28.dp))
                        .background(scheme.surfaceContainer)
                        .padding(vertical = 20.dp)
                ) {
                    Text(
                        if (wallpaperTab) "מתוך הטפט והמערכת" else "פלטות נבחרות",
                        Modifier.padding(horizontal = 20.dp),
                        color = scheme.onSurfaceVariant, fontSize = 13.sp, fontWeight = FontWeight.Medium,
                    )
                    LazyRow(
                        Modifier.padding(top = 14.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        flingBehavior = rememberBitChordFlingBehavior(),
                    ) {
                        items(options, key = { it.key + it.wallpaper }) { option ->
                            val colors = remember(option) {
                                val s = schemeFor(context, option, dark = true)
                                Triple(s.primary, s.secondary, s.tertiary)
                            }
                            TriToneSwatch(
                                colors,
                                selected = theme.key == option.key && theme.wallpaper == option.wallpaper,
                            ) {
                                haptic.performHapticFeedback(HapticFeedbackType.SegmentTick)
                                prefs.setTheme(option.choice(theme.dark))
                            }
                        }
                    }
                    Row(
                        Modifier
                            .padding(top = 18.dp)
                            .fillMaxWidth()
                            .clickable { prefs.setTheme(theme.copy(dark = !theme.dark)) }
                            .padding(horizontal = 20.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text("מצב כהה", color = scheme.onSurface, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                            Text("רקע כהה, כמו באולם קולנוע", color = scheme.onSurfaceVariant, fontSize = 13.sp)
                        }
                        Switch(
                            theme.dark,
                            {
                                haptic.performHapticFeedback(if (it) HapticFeedbackType.ToggleOn else HapticFeedbackType.ToggleOff)
                                prefs.setTheme(theme.copy(dark = it))
                            },
                            thumbContent = if (theme.dark) {
                                { Icon(Icons.Rounded.Check, null, Modifier.size(SwitchDefaults.IconSize)) }
                            } else null,
                        )
                    }
                    PillSwitcher(
                        listOf("צבעי מערכת", "צבעים נוספים"),
                        if (wallpaperTab) 0 else 1,
                        Modifier.padding(top = 16.dp).padding(horizontal = 16.dp),
                    ) {
                        haptic.performHapticFeedback(HapticFeedbackType.SegmentTick)
                        wallpaperTab = it == 0
                    }
                }
            }
        }
    }
}

/**
 * A Pixel tri-tone swatch (56 dp): the top half the palette's primary, the bottom quarters its secondary and
 * tertiary. Selected, the circle morphs into an 18 dp squircle inside a crisp 2 dp ring with a 4 dp gap (bouncy).
 */
@Composable
internal fun TriToneSwatch(colors: Triple<Color, Color, Color>, selected: Boolean, onClick: () -> Unit) {
    val ring = MaterialTheme.colorScheme.onSurface
    val bouncy = spring<Dp>(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow)
    val inset by animateDpAsState(if (selected) 6.dp else 0.dp, bouncy, label = "inset")
    val corner by animateDpAsState(if (selected) 18.dp else 28.dp, bouncy, label = "corner")
    val ringAlpha by animateFloatAsState(if (selected) 1f else 0f, label = "ring")
    Canvas(
        Modifier
            .size(56.dp)
            .clip(RoundedCornerShape(if (selected) 22.dp else 28.dp))
            .clickable(onClick = onClick)
    ) {
        val i = inset.toPx()
        val r = (corner.toPx() - i * 0.4f).coerceAtLeast(0f)
        val inner = Size(size.width - 2 * i, size.height - 2 * i)
        if (ringAlpha > 0f) {
            val sw = 2.dp.toPx()
            drawRoundRect(
                ring.copy(alpha = ringAlpha), Offset(sw / 2, sw / 2), Size(size.width - sw, size.height - sw),
                CornerRadius(22.dp.toPx()), style = Stroke(sw),
            )
        }
        val path = Path().apply {
            addRoundRect(androidx.compose.ui.geometry.RoundRect(i, i, i + inner.width, i + inner.height, CornerRadius(r)))
        }
        clipPath(path) {
            val (top, bl, br) = colors
            drawRect(top, Offset(i, i), Size(inner.width, inner.height / 2))
            drawRect(bl, Offset(i, i + inner.height / 2), Size(inner.width / 2, inner.height / 2))
            drawRect(br, Offset(i + inner.width / 2, i + inner.height / 2), Size(inner.width / 2, inner.height / 2))
        }
    }
}

/** A segmented pill switcher with a sliding secondaryContainer indicator. */
@Composable
private fun PillSwitcher(labels: List<String>, selected: Int, modifier: Modifier = Modifier, onSelect: (Int) -> Unit) {
    val scheme = MaterialTheme.colorScheme
    val pill = RoundedCornerShape(50)
    BoxWithConstraints(
        modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(pill)
            .background(scheme.surfaceContainerHighest)
            .padding(4.dp)
    ) {
        val w = maxWidth / labels.size
        val x by animateDpAsState(w * selected, spring(Spring.DampingRatioLowBouncy, Spring.StiffnessMediumLow), label = "seg")
        Box(Modifier.offset(x = x).width(w).fillMaxHeight().clip(pill).background(scheme.secondaryContainer))
        Row(Modifier.fillMaxSize()) {
            labels.forEachIndexed { i, label ->
                Box(
                    Modifier.weight(1f).fillMaxHeight().clip(pill).clickable { onSelect(i) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        label,
                        color = if (i == selected) scheme.onSecondaryContainer else scheme.onSurfaceVariant,
                        fontSize = 14.sp,
                        fontWeight = if (i == selected) FontWeight.ExtraBold else FontWeight.Medium,
                    )
                }
            }
        }
    }
}

/**
 * The live preview: a 190 x 340 dp phone with a miniature of the app (hero, channel tiles, floating bar) drawn in
 * the current colours, so it changes the moment a swatch or the dark switch is touched.
 */
@Composable
private fun DevicePreview(sample: List<Channel>, logo: (Channel) -> String) {
    val scheme = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(28.dp)
    Box(
        Modifier
            .size(width = 190.dp, height = 340.dp)
            .clip(shape)
            .background(scheme.surfaceContainerLowest)
            .border(1.dp, scheme.outlineVariant, shape)
    ) {
        Column(Modifier.fillMaxSize()) {
            // mini hero
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(Brush.verticalGradient(listOf(scheme.primaryContainer, scheme.tertiaryContainer.copy(alpha = 0.7f), scheme.surfaceContainerLowest)))
            ) {
                Row(Modifier.padding(start = 12.dp, end = 12.dp, top = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("ישראל TV+‎", Modifier.weight(1f), color = scheme.onSurface, fontSize = 11.sp, fontWeight = FontWeight.Black)
                    Box(Modifier.size(14.dp).clip(RoundedCornerShape(5.dp)).background(scheme.primary))
                }
                Column(Modifier.align(Alignment.BottomCenter).padding(bottom = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    sample.firstOrNull()?.let { ChannelLogo(logo(it), Modifier.size(width = 70.dp, height = 34.dp)) }
                    Spacer(Modifier.height(6.dp))
                    Row(
                        Modifier.clip(RoundedCornerShape(50)).background(scheme.onSurface).padding(horizontal = 12.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Rounded.PlayArrow, null, Modifier.size(10.dp), tint = scheme.surface)
                        Text("צפייה", color = scheme.surface, fontSize = 8.sp, fontWeight = FontWeight.Black)
                    }
                }
            }
            Text(
                "ערוצי ישראל", Modifier.padding(start = 12.dp, top = 6.dp, bottom = 6.dp),
                color = scheme.onSurface, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold,
            )
            sample.drop(1).take(6).chunked(3).forEach { row ->
                Row(Modifier.padding(horizontal = 10.dp).padding(bottom = 6.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    row.forEach { ch ->
                        Box(
                            Modifier.weight(1f).aspectRatio(16f / 9.5f).clip(RoundedCornerShape(8.dp))
                                .background(scheme.surfaceContainerHigh),
                            contentAlignment = Alignment.Center,
                        ) { ChannelLogo(logo(ch), Modifier.fillMaxSize(0.6f), ch.isRadio) }
                    }
                }
            }
            Row(Modifier.padding(horizontal = 10.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(3) {
                    Box(Modifier.weight(1f).aspectRatio(16f / 9.5f).clip(RoundedCornerShape(8.dp)).background(scheme.secondaryContainer))
                }
            }
        }
        // mini floating bar
        Row(
            Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 10.dp)
                .fillMaxWidth(0.86f)
                .height(26.dp)
                .clip(RoundedCornerShape(50))
                .background(scheme.surfaceContainerHighest)
                .padding(3.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            listOf<ImageVector>(Icons.Rounded.Home, Icons.Rounded.LiveTv, Icons.Rounded.Favorite, Icons.Rounded.Settings, Icons.Rounded.Search)
                .forEachIndexed { i, icon ->
                    Box(
                        Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(CircleShape)
                            .background(if (i == 0) scheme.primaryContainer else Color.Transparent),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(icon, null, Modifier.size(11.dp), tint = if (i == 0) scheme.onPrimaryContainer else scheme.onSurfaceVariant)
                    }
                }
        }
    }
}

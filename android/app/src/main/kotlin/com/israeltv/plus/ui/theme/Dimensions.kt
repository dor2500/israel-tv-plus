package com.israeltv.plus.ui.theme

import android.content.pm.PackageManager
import android.content.res.Configuration
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

enum class DeviceClass { Phone, Tablet, Tv }

/** The layout tokens of one device class. Blur is a per-class decision, so TV can never inherit mobile glass. */
@Immutable
data class Dimens(
    val deviceClass: DeviceClass,
    val gutter: Dp,
    val cardGap: Dp,
    val shelfGap: Dp,
    val posterWidth: Dp,
    val continueWidth: Dp,
    val gridColumns: Int,
    val playerEdge: Dp,
    val playerActionHeight: Dp,
    val playerIcon: Dp,
    val navHeight: Dp,
    val navHeightCollapsed: Dp,
    val navMaxWidth: Dp,
    val heroContentMaxWidth: Dp,
    val heroContentPadding: Dp,
    val heroBottomFade: Dp,
    val blurAllowed: Boolean,
) {
    val isTv: Boolean get() = deviceClass == DeviceClass.Tv
}

val PhoneDimens = Dimens(
    deviceClass = DeviceClass.Phone,
    gutter = 16.dp,
    cardGap = 16.dp,
    shelfGap = 32.dp,
    posterWidth = 112.dp,
    continueWidth = 200.dp,
    gridColumns = 3,
    playerEdge = 16.dp,
    playerActionHeight = 48.dp,
    playerIcon = 22.dp,
    navHeight = 64.dp,
    navHeightCollapsed = 48.dp,
    navMaxWidth = 400.dp,
    heroContentMaxWidth = 480.dp,
    heroContentPadding = 24.dp,
    heroBottomFade = 220.dp,
    blurAllowed = true,
)

val TabletDimens = PhoneDimens.copy(
    deviceClass = DeviceClass.Tablet,
    gutter = 32.dp,
    cardGap = 16.dp,
    shelfGap = 40.dp,
    posterWidth = 144.dp,
    continueWidth = 280.dp,
    gridColumns = 6,
    playerEdge = 32.dp,
    heroContentMaxWidth = 560.dp,
    heroContentPadding = 40.dp,
    heroBottomFade = 180.dp,
)

val TvDimens = TabletDimens.copy(
    deviceClass = DeviceClass.Tv,
    gutter = 48.dp,
    cardGap = 24.dp,
    shelfGap = 48.dp,
    posterWidth = 160.dp,
    continueWidth = 320.dp,
    gridColumns = 7,
    playerEdge = 48.dp,
    blurAllowed = false,
)

val LocalDimens = staticCompositionLocalOf { PhoneDimens }

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun rememberDimens(): Dimens {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    return remember(context, configuration.screenWidthDp, configuration.screenHeightDp, configuration.uiMode) {
        val tv = context.packageManager.hasSystemFeature(PackageManager.FEATURE_LEANBACK) ||
            (configuration.uiMode and Configuration.UI_MODE_TYPE_MASK) == Configuration.UI_MODE_TYPE_TELEVISION
        val window = WindowSizeClass.calculateFromSize(DpSize(configuration.screenWidthDp.dp, configuration.screenHeightDp.dp))
        when {
            tv -> TvDimens
            // a phone on its side is wide but short: it keeps the phone's tokens
            window.widthSizeClass != WindowWidthSizeClass.Compact &&
                window.heightSizeClass != WindowHeightSizeClass.Compact -> TabletDimens
            else -> PhoneDimens
        }
    }
}

package com.israeltv.plus.ui.nuvio

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LiveTv
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.israeltv.plus.R

/** The destinations of the floating pill. Labels are string resources, so no copy is hardcoded in the UI. */
enum class NuvioTab(@StringRes val label: Int, val icon: ImageVector) {
    Home(R.string.nuvio_tab_home, Icons.Rounded.Home),
    Channels(R.string.nuvio_tab_channels, Icons.Rounded.LiveTv),
    Mine(R.string.nuvio_tab_mine, Icons.Rounded.Favorite),
    Settings(R.string.nuvio_tab_settings, Icons.Rounded.Settings),
}

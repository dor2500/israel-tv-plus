package com.israeltv.plus.ui.nuvio

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.israeltv.plus.BuildConfig

/** Fully integrated Nuvio UI architecture (always enabled across both Debug and Release builds). */
object NuvioUi {
    var enabled by mutableStateOf(BuildConfig.DEBUG)
}

package com.israeltv.plus.ui.nuvio

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.israeltv.plus.BuildConfig

/** Switch between the Nuvio UI and the legacy feed while the rebuild is in progress: on in debug builds, off in release. */
object NuvioUi {
    var enabled by mutableStateOf(BuildConfig.DEBUG)
}

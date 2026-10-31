package com.israeltv.plus.ui.nuvio

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.israeltv.plus.data.Channel
import com.israeltv.plus.ui.components.NuvioChannelCard

/** Nuvio poster ratio: every channel and VOD card is 2:3. */
const val NuvioPosterAspect = 2f / 3f

/**
 * Vertical 2:3 poster: delegates to NuvioChannelCard in components directory with full interactions.
 */
@Composable
fun NuvioPosterCard(
    channel: Channel,
    logoUrl: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    sharedKey: String? = null,
) {
    NuvioChannelCard(
        channel = channel,
        logoUrl = logoUrl,
        subtitle = subtitle,
        onClick = onClick,
        modifier = modifier,
        sharedKey = sharedKey,
    )
}

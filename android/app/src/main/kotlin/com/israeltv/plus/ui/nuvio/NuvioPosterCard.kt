package com.israeltv.plus.ui.nuvio

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.israeltv.plus.data.Channel
import com.israeltv.plus.ui.components.ChannelLogo

/** Nuvio poster ratio: every channel and VOD card is 2:3. */
const val NuvioPosterAspect = 2f / 3f

/** Resting corner radius of a poster; the press morphs it outwards from here. */
private val PosterRadius = 12.dp

/**
 * Vertical 2:3 poster: the logo on a tonal plate, name and subtitle below. Press = scale 0.96 with corners morphing
 * outwards. Only theme tokens are used for colour, so it follows Material You and the profile palette.
 */
@Composable
fun NuvioPosterCard(
    channel: Channel,
    logoUrl: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.nuvioPressable(onClick, restRadius = PosterRadius)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(NuvioPosterAspect)
                .clip(RoundedCornerShape(PosterRadius))
                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
            contentAlignment = Alignment.Center,
        ) {
            ChannelLogo(
                url = logoUrl,
                isRadio = channel.isRadio,
                modifier = Modifier.fillMaxWidth().padding(14.dp),
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = channel.name,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 2.dp),
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 2.dp),
        )
    }
}

package com.israeltv.plus.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.israeltv.plus.data.avatarDescription
import com.israeltv.plus.data.avatarEmoji
import com.israeltv.plus.data.avatarUrl
import com.israeltv.plus.data.isEmojiAvatar

/**
 * A profile portrait. Emoji profiles are drawn by the system emoji font, which gives the big, glossy, Apple-style
 * portraits the "who's watching" page wants at every size; profiles saved by older versions still resolve to their
 * bundled series artwork, so nothing is lost for an existing user.
 *
 * [glyphSize] is the emoji's type size: the caller sizes the square container around it.
 */
@Composable
fun ProfileAvatar(
    avatar: String,
    glyphSize: Dp,
    modifier: Modifier = Modifier,
) {
    val semantics = Modifier.semantics { contentDescription = avatarDescription(avatar) }
    if (isEmojiAvatar(avatar)) {
        Text(
            text = avatarEmoji(avatar),
            modifier = modifier.then(semantics),
            fontSize = glyphSize.value.sp,
            lineHeight = glyphSize.value.sp * 1.02f,
            textAlign = TextAlign.Center,
            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
            maxLines = 1,
        )
    } else {
        AsyncImage(
            model = avatarUrl(avatar),
            contentDescription = avatarDescription(avatar),
            modifier = modifier.size(glyphSize * 1.25f),
            contentScale = ContentScale.Fit,
        )
    }
}

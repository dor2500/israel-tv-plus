package com.israeltv.plus.ui.player

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

/**
 * The Nuvio player side panel (PlayerSidePanel.kt): a dimmed backdrop that fades in and a rounded panel that slides in
 * from the trailing edge (250 ms in / 200 ms out), tap outside to close. Nuvio hosts it inline in its player; here it
 * lives in a full-screen dialog window so the existing conditionally-composed option sheets can swap
 * `ModalBottomSheet` for it without touching their call sites.
 */
@Composable
internal fun PlayerSidePanelSheet(
    onDismiss: () -> Unit,
    width: Dp = 400.dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    val backgroundInteraction = remember { MutableInteractionSource() }
    val panelInteraction = remember { MutableInteractionSource() }
    val scheme = MaterialTheme.colorScheme

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false),
    ) {
        AnimatedVisibility(visible = visible, enter = fadeIn(tween(200)), exit = fadeOut(tween(160))) {
            BoxWithConstraints(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.34f))
                    .clickable(interactionSource = backgroundInteraction, indication = null, onClick = onDismiss),
            ) {
                val resolvedWidth = minOf(maxWidth, width)
                val shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
                AnimatedVisibility(
                    visible = visible,
                    enter = slideInHorizontally(tween(250)) { it },
                    exit = slideOutHorizontally(tween(200)) { it },
                    modifier = Modifier.align(Alignment.CenterEnd),
                ) {
                    Column(
                        Modifier
                            .width(resolvedWidth)
                            .fillMaxHeight()
                            .clip(shape)
                            .background(scheme.surfaceContainer.copy(alpha = 0.94f))
                            .clickable(interactionSource = panelInteraction, indication = null, onClick = {})
                            .statusBarsPadding()
                            .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.End)),
                        content = content,
                    )
                }
            }
        }
    }
}

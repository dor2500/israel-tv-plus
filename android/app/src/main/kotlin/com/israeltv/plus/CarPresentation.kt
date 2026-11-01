package com.israeltv.plus

import android.app.Presentation
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Display
import android.widget.FrameLayout
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView

/**
 * Renders video on an external car display (such as wired Android Auto, HDMI, or wireless display)
 * automatically when connected to the vehicle.
 */
class CarPresentation(
    context: Context,
    display: Display,
    private val player: ExoPlayer
) : Presentation(context, display) {
    private var playerView: PlayerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val frame = FrameLayout(context).apply {
            setBackgroundColor(Color.BLACK)
        }
        playerView = PlayerView(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            useController = false
            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
            setShutterBackgroundColor(Color.BLACK)
            this.player = this@CarPresentation.player
        }
        frame.addView(playerView)
        setContentView(frame)
    }

    override fun onDetachedFromWindow() {
        playerView?.player = null
        playerView = null
        super.onDetachedFromWindow()
    }
}


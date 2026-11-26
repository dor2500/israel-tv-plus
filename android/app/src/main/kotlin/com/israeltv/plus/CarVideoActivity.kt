package com.israeltv.plus

import android.app.Activity
import android.app.PictureInPictureParams
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Rational
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.media3.common.MediaItem
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import coil3.load
import com.israeltv.plus.data.Channel

/**
 * Dedicated In-Car Video Activity for Android Auto, Android Automotive, and Car Head Units.
 * Displays live TV streams and VOD video on the car screen with distraction-free controls,
 * automatic Picture-in-Picture (PiP) during navigation, and steering wheel / key controls.
 */
class CarVideoActivity : Activity() {
    private var playerView: PlayerView? = null
    private var overlay: LinearLayout? = null
    private var titleView: TextView? = null
    private var subtitleView: TextView? = null
    private var logoView: ImageView? = null
    private val handler = Handler(Looper.getMainLooper())
    private val hideOverlayRunnable = Runnable { hideOverlay() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        )

        val root = FrameLayout(this).apply {
            setBackgroundColor(Color.BLACK)
        }

        playerView = PlayerView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            useController = false
            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
            setShutterBackgroundColor(Color.BLACK)
        }
        root.addView(playerView)

        // Minimalist top overlay with channel info
        overlay = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setBackgroundColor(Color.argb(175, 15, 18, 28))
            setPadding(32, 20, 32, 20)
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.TOP
            )
        }

        logoView = ImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(64, 64).apply {
                setMargins(0, 0, 24, 0)
            }
        }
        overlay?.addView(logoView)

        val textLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        titleView = TextView(this).apply {
            setTextColor(Color.WHITE)
            textSize = 18f
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            text = "ישראל TV+ · וידאו פעיל"
        }
        textLayout.addView(titleView)

        subtitleView = TextView(this).apply {
            setTextColor(Color.argb(200, 255, 255, 255))
            textSize = 13f
            text = "שידור חי לרכב (Android Auto)"
        }
        textLayout.addView(subtitleView)

        overlay?.addView(textLayout)
        root.addView(overlay)

        root.setOnClickListener {
            toggleOverlay()
        }

        setContentView(root)
    }

    override fun onResume() {
        super.onResume()
        bindPlayer()
        showOverlay()
        scheduleHideOverlay()
    }

    private fun bindPlayer() {
        val player = AutoService.activePlayer
        playerView?.player = player
        updateChannelInfo(AutoService.currentChannel)
    }

    fun updateChannelInfo(ch: Channel?) {
        if (ch != null) {
            titleView?.text = ch.name
            subtitleView?.text = if (ch.isRadio) "רדיו בשידור חי" else "שידור וידאו חי · ${ch.category}"
            if (ch.logo.isNotEmpty()) {
                val uri = if (ch.logo.startsWith("http")) android.net.Uri.parse(ch.logo) else com.israeltv.plus.data.AutoArtworkProvider.buildUri(ch.logo)
                logoView?.load(uri)
                logoView?.visibility = View.VISIBLE
            } else {
                logoView?.visibility = View.GONE
            }
        } else {
            val item = AutoService.activePlayer?.currentMediaItem
            val title = item?.mediaMetadata?.title?.toString()
            if (!title.isNullOrEmpty()) {
                titleView?.text = title
                subtitleView?.text = "שידור וידאו פעיל ברכב"
            }
        }
    }

    private fun showOverlay() {
        overlay?.visibility = View.VISIBLE
        overlay?.animate()?.alpha(1f)?.setDuration(200)?.start()
    }

    private fun hideOverlay() {
        overlay?.animate()?.alpha(0f)?.setDuration(300)?.withEndAction {
            overlay?.visibility = View.GONE
        }?.start()
    }

    private fun toggleOverlay() {
        if (overlay?.visibility == View.VISIBLE) {
            handler.removeCallbacks(hideOverlayRunnable)
            hideOverlay()
        } else {
            showOverlay()
            scheduleHideOverlay()
        }
    }

    private fun scheduleHideOverlay() {
        handler.removeCallbacks(hideOverlayRunnable)
        handler.postDelayed(hideOverlayRunnable, 3500)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val player = AutoService.activePlayer
        when (keyCode) {
            KeyEvent.KEYCODE_MEDIA_NEXT, KeyEvent.KEYCODE_DPAD_DOWN, KeyEvent.KEYCODE_CHANNEL_DOWN -> {
                player?.seekToNextMediaItem()
                showOverlay()
                scheduleHideOverlay()
                return true
            }
            KeyEvent.KEYCODE_MEDIA_PREVIOUS, KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_CHANNEL_UP -> {
                player?.seekToPreviousMediaItem()
                showOverlay()
                scheduleHideOverlay()
                return true
            }
            KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, KeyEvent.KEYCODE_HEADSETHOOK, KeyEvent.KEYCODE_SPACE, KeyEvent.KEYCODE_DPAD_CENTER -> {
                if (player != null) {
                    if (player.isPlaying) player.pause() else player.play()
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        // Automatically enter Picture-in-Picture when driver switches to Waze / Maps navigation
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && AutoService.activePlayer?.isPlaying == true) {
            runCatching {
                val params = PictureInPictureParams.Builder()
                    .setAspectRatio(Rational(16, 9))
                    .build()
                enterPictureInPictureMode(params)
            }
        }
    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        if (isInPictureInPictureMode) {
            overlay?.visibility = View.GONE
        } else {
            showOverlay()
            scheduleHideOverlay()
        }
    }

    override fun onDestroy() {
        handler.removeCallbacks(hideOverlayRunnable)
        playerView?.player = null
        playerView = null
        super.onDestroy()
    }
}


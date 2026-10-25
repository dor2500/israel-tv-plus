package com.israeltv.plus

import android.app.PictureInPictureParams
import android.app.UiModeManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Rational
import android.view.View
import android.view.animation.PathInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.crossfade
import coil3.svg.SvgDecoder
import com.israeltv.plus.data.AppPrefs
import com.israeltv.plus.data.ChannelStore
import com.israeltv.plus.data.USER_AGENT
import com.israeltv.plus.data.Updater
import com.israeltv.plus.ui.ItvApp
import com.israeltv.plus.ui.theme.ItvTheme
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * Phones and tablets: the native app (Jetpack Compose). TVs: handed straight over to MainActivity (the web app in
 * a WebView, the same UI as on LG and Samsung).
 *
 * Start: the AndroidX splash screen stays until the channel list is loaded (at most 2.5 s), then the icon grows and
 * fades; a short launch sound plays on a cold start (setting; silent when the phone is on silent / vibrate).
 * The window asks for the display's fastest refresh rate (120 Hz where available).
 */
class PhoneActivity : ComponentActivity() {
    private val http by lazy {
        OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor { it.proceed(it.request().newBuilder().header("User-Agent", USER_AGENT).build()) }
            .build()
    }
    private val inPip = mutableStateOf(false)
    private var playing = false
    private val ready = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        if (isTv()) {
            super.onCreate(savedInstanceState)
            startActivity(Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
            finish()
            return
        }
        val splash = installSplashScreen()
        super.onCreate(savedInstanceState)
        // the system splash only covers the first frame; the cinematic intro (ui/Intro.kt) takes over on black
        splash.setOnExitAnimationListener { p ->
            val ease = PathInterpolator(0.05f, 0.7f, 0.1f, 1f)   // Material "emphasized decelerate"
            runCatching { p.iconView }.getOrNull()?.animate()?.scaleX(0.6f)?.scaleY(0.6f)?.alpha(0f)
                ?.setDuration(260)?.setInterpolator(ease)?.start()
            runCatching {
                p.view.animate().alpha(0f).setDuration(240).setInterpolator(ease).withEndAction { p.remove() }.start()
            }.onFailure { p.remove() }
        }

        val prefs = AppPrefs(this)
        if (savedInstanceState == null && prefs.launchSound) playLaunchSound()
        enableEdgeToEdge(SystemBarStyle.dark(Color.TRANSPARENT), SystemBarStyle.dark(Color.TRANSPARENT))
        if (Build.VERSION.SDK_INT >= 29) window.isNavigationBarContrastEnforced = false
        highRefreshRate()

        SingletonImageLoader.setSafe { ctx ->
            ImageLoader.Builder(ctx)
                .components {
                    add(SvgDecoder.Factory())
                    add(OkHttpNetworkFetcherFactory(callFactory = { http }))
                }
                .crossfade(true)
                .build()
        }

        val store = ChannelStore(applicationContext, http)
        val updater = Updater(applicationContext, http, lifecycleScope)
        ContextCompat.registerReceiver(
            this, updater.statusReceiver, IntentFilter(Updater.ACTION_INSTALL_STATUS), ContextCompat.RECEIVER_NOT_EXPORTED,
        )
        lifecycleScope.launch {
            store.load()
            ready.value = true
            store.refresh()
            updater.check()
        }

        setContent {
            ItvTheme(prefs.theme) {
                ItvApp(
                    store = store,
                    prefs = prefs,
                    updater = updater,
                    inPip = inPip.value,
                    coldStart = savedInstanceState == null,
                    onPlaying = { on -> playing = on; updatePip() },
                    onEnterPip = ::enterPip,
                )
            }
        }
    }

    private fun isTv(): Boolean {
        val ui = getSystemService(Context.UI_MODE_SERVICE) as? UiModeManager
        return ui?.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION ||
            packageManager.hasSystemFeature(PackageManager.FEATURE_LEANBACK)
    }

    // ------------------------------------------------------------------ picture-in-picture
    private fun pipParams(): PictureInPictureParams? {
        if (Build.VERSION.SDK_INT < 26) return null
        val b = PictureInPictureParams.Builder().setAspectRatio(Rational(16, 9))
        if (Build.VERSION.SDK_INT >= 31) b.setAutoEnterEnabled(playing).setSeamlessResizeEnabled(true)
        return b.build()
    }

    private fun updatePip() {
        if (Build.VERSION.SDK_INT >= 26) runCatching { pipParams()?.let(::setPictureInPictureParams) }
    }

    private fun enterPip() {
        if (Build.VERSION.SDK_INT >= 26) runCatching { pipParams()?.let(::enterPictureInPictureMode) }
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        // Android 8-11 (12+ enters by itself with auto-enter)
        if (playing && Build.VERSION.SDK_INT in 26..30) enterPip()
    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        inPip.value = isInPictureInPictureMode
    }

    // ------------------------------------------------------------------ 120 Hz, launch sound
    /** Ask for the screen's fastest mode at the current resolution (120 Hz on most phones), not the 60 Hz default. */
    @Suppress("DEPRECATION")
    private fun highRefreshRate() {
        runCatching {
            val d = if (Build.VERSION.SDK_INT >= 30) display else windowManager.defaultDisplay
            d ?: return
            val cur = d.mode
            val best = d.supportedModes
                .filter { it.physicalWidth == cur.physicalWidth && it.physicalHeight == cur.physicalHeight }
                .maxByOrNull { it.refreshRate } ?: cur
            window.attributes = window.attributes.apply {
                preferredDisplayModeId = best.modeId
                preferredRefreshRate = best.refreshRate
            }
            if (Build.VERSION.SDK_INT >= 35) window.decorView.requestedFrameRate = View.REQUESTED_FRAME_RATE_CATEGORY_HIGH
        }
    }

    /** res/raw/launch.wav (tools/make_launch_sound.py), only when the ringer is on. */
    private fun playLaunchSound() {
        val am = getSystemService(Context.AUDIO_SERVICE) as? AudioManager ?: return
        if (am.ringerMode != AudioManager.RINGER_MODE_NORMAL) return
        runCatching {
            val pool = SoundPool.Builder().setMaxStreams(1).setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            ).build()
            pool.setOnLoadCompleteListener { sp, id, status ->
                if (status == 0) sp.play(id, 0.55f, 0.55f, 1, 0, 1f)
                Handler(Looper.getMainLooper()).postDelayed({ sp.release() }, 2500)
            }
            pool.load(this, R.raw.launch, 1)
        }
    }
}

package com.israeltv.plus.wear

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * The one player of the app, shared by the screens and [PlaybackService] (which keeps it going with the screen off
 * and shows it in the watch's media controls). A channel always plays with its full sound: the app never mutes it,
 * the crown / volume buttons change the watch's own media volume.
 */
@OptIn(UnstableApi::class)
object Playback {
    private var exo: ExoPlayer? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var loading: Job? = null

    private val _current = MutableStateFlow<Channel?>(null)
    /** The channel on now (also while it connects), or null. */
    val current: StateFlow<Channel?> = _current
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun player(context: Context): ExoPlayer = exo ?: run {
        val http = DefaultHttpDataSource.Factory().setUserAgent(USER_AGENT).setAllowCrossProtocolRedirects(true)
            .setConnectTimeoutMs(12_000).setReadTimeoutMs(15_000)
        ExoPlayer.Builder(context.applicationContext)
            .setMediaSourceFactory(DefaultMediaSourceFactory(context.applicationContext).setDataSourceFactory(http))
            .setAudioAttributes(AudioAttributes.Builder().setUsage(C.USAGE_MEDIA).setContentType(C.AUDIO_CONTENT_TYPE_MUSIC).build(), true)
            .setHandleAudioBecomingNoisy(true)   // the headphones disconnect: pause, like every media app
            .setWakeMode(C.WAKE_MODE_NETWORK)    // radio with the screen off
            .build()
            .also { p ->
                p.volume = 1f
                p.addListener(object : Player.Listener {
                    override fun onPlayerError(error: PlaybackException) {
                        // a live stream paused for a while fell behind its window: back to the live edge
                        if (error.errorCode == PlaybackException.ERROR_CODE_BEHIND_LIVE_WINDOW) {
                            p.seekToDefaultPosition()
                            p.prepare()
                            return
                        }
                        _error.value = if (error.errorCode in 2000..2999) "אין חיבור לשידור" else "השידור לא זמין כרגע"
                    }
                })
                exo = p
            }
    }

    fun play(context: Context, ch: Channel) {
        val app = context.applicationContext
        val p = player(app)
        _current.value = ch
        _error.value = null
        // started now, on the tap (the app is in front): after the network wait below it may already be left
        runCatching { app.startService(Intent(app, PlaybackService::class.java)) }
        loading?.cancel()
        loading = scope.launch {
            val url = runCatching { playableUrl(ch) }.getOrElse {
                _error.value = "לא התקבלה הרשאת צפייה"
                return@launch
            }
            val meta = MediaMetadata.Builder().setTitle(ch.name).setArtist(ch.subtitle).setStation(ch.name)
                .apply { if (ch.logoUrl.isNotEmpty()) setArtworkUri(Uri.parse(ch.logoUrl)) }
                .build()
            val item = MediaItem.Builder().setUri(url).setMediaId(ch.id).setMediaMetadata(meta)
                // every TV channel is HLS, also the ones whose address has no .m3u8 (Samsung TV Plus redirects)
                .apply { if (url.contains(".m3u8", ignoreCase = true) || !ch.isRadio) setMimeType(MimeTypes.APPLICATION_M3U8) }
                .build()
            p.setMediaItem(item)
            p.volume = 1f
            p.prepare()
            p.play()
        }
    }

    fun retry(context: Context) { _current.value?.let { play(context, it) } }

    fun stop() {
        loading?.cancel()
        exo?.run { stop(); clearMediaItems() }
        _current.value = null
        _error.value = null
    }
}

/** Keeps the channel playing with the screen off / the app closed, and puts it in the watch's media controls. */
@OptIn(UnstableApi::class)
class PlaybackService : MediaSessionService() {
    private var session: MediaSession? = null

    override fun onCreate() {
        super.onCreate()
        // a tap on the watch's media controls opens the player itself, not the home screen
        val open = PendingIntent.getActivity(this, 0,
            Intent(this, WearActivity::class.java).putExtra(WearActivity.EXTRA_PLAYER, true),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        session = MediaSession.Builder(this, Playback.player(this)).setSessionActivity(open).build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = session

    override fun onTaskRemoved(rootIntent: Intent?) {
        val p = session?.player
        if (p == null || !p.playWhenReady || p.mediaItemCount == 0) stopSelf()
    }

    override fun onDestroy() {
        // the player itself stays: the screens hold it, and the next channel starts the service again
        session?.release()
        session = null
        Playback.stop()
        super.onDestroy()
    }
}

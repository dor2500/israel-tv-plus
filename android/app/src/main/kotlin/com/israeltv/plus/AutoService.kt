package com.israeltv.plus

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.net.Uri
import android.os.Bundle
import android.view.Display
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.ListenableFuture
import com.israeltv.plus.data.AppPrefs
import com.israeltv.plus.data.Channel
import com.israeltv.plus.data.ChannelData
import com.israeltv.plus.data.ChannelStore
import com.israeltv.plus.data.REMOTE
import com.israeltv.plus.data.USER_AGENT
import com.israeltv.plus.data.VodSeries
import com.israeltv.plus.data.playableUrl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.guava.future
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient

private const val ROOT = "root"
private const val RADIO = "radio"
private const val TV = "tv"
private const val VOD = "vod"
private const val FAVORITES = "favorites"
private const val CONTENT_STYLE_PLAYABLE = "android.media.browse.CONTENT_STYLE_PLAYABLE_HINT"
private const val CONTENT_STYLE_GRID = 2

/**
 * Android Auto and Automotive Media & Video Service.
 * Provides live Israeli TV channels with full video playback, radio stations, VOD series & movies,
 * and profile favourites. Also manages automatic presentation on secondary car displays and PiP.
 */
class AutoService : MediaLibraryService() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val store by lazy { ChannelStore(applicationContext, OkHttpClient()) }
    private var session: MediaLibrarySession? = null
    private var presentation: CarPresentation? = null
    private var displayManager: DisplayManager? = null

    /** Which stream of a channel is playing, so a failing one can move on to the next. */
    private val attempts = mutableMapOf<String, Int>()

    companion object {
        var activePlayer: ExoPlayer? = null
            private set
        var currentChannel: Channel? = null
            private set
    }

    private val displayListener = object : DisplayManager.DisplayListener {
        override fun onDisplayAdded(displayId: Int) = updatePresentation()
        override fun onDisplayRemoved(displayId: Int) = updatePresentation()
        override fun onDisplayChanged(displayId: Int) = updatePresentation()
    }

    override fun onCreate() {
        super.onCreate()
        val http = DefaultHttpDataSource.Factory().setUserAgent(USER_AGENT).setAllowCrossProtocolRedirects(true)
        val player = ExoPlayer.Builder(this)
            .setMediaSourceFactory(DefaultMediaSourceFactory(DefaultDataSource.Factory(this, http)))
            .setAudioAttributes(
                AudioAttributes.Builder().setUsage(C.USAGE_MEDIA).setContentType(C.AUDIO_CONTENT_TYPE_MOVIE).build(),
                true,
            )
            .setHandleAudioBecomingNoisy(true)
            .setWakeMode(C.WAKE_MODE_NETWORK)
            .build()

        activePlayer = player

        player.addListener(object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) = tryNextStream(player)
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)
                scope.launch {
                    val id = mediaItem?.mediaId ?: return@launch
                    val d = data()
                    currentChannel = d?.byId?.get(id)
                }
            }
        })

        val open = PendingIntent.getActivity(
            this,
            0,
            Intent(this, CarVideoActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        session = MediaLibrarySession.Builder(this, player, Callback()).setSessionActivity(open).build()

        displayManager = getSystemService(Context.DISPLAY_SERVICE) as? DisplayManager
        displayManager?.registerDisplayListener(displayListener, null)
        updatePresentation()

        scope.launch {
            store.load()
            store.refresh()
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? = session

    override fun onTaskRemoved(rootIntent: Intent?) {
        if (session?.player?.playWhenReady != true) stopSelf()
    }

    override fun onDestroy() {
        displayManager?.unregisterDisplayListener(displayListener)
        presentation?.dismiss()
        presentation = null
        if (activePlayer == session?.player) {
            activePlayer = null
        }
        session?.run {
            player.release()
            release()
        }
        session = null
        scope.cancel()
        super.onDestroy()
    }

    private fun updatePresentation() {
        val dm = displayManager ?: return
        val presentationDisplays = dm.getDisplays(DisplayManager.DISPLAY_CATEGORY_PRESENTATION)
        val carDisplay = presentationDisplays.firstOrNull() ?: dm.displays.firstOrNull { it.displayId != Display.DEFAULT_DISPLAY }

        if (carDisplay != null) {
            val currentPlayer = session?.player as? ExoPlayer
            if (currentPlayer != null && presentation?.display?.displayId != carDisplay.displayId) {
                presentation?.dismiss()
                presentation = CarPresentation(this, carDisplay, currentPlayer).apply {
                    runCatching { show() }
                }
            }
        } else {
            presentation?.dismiss()
            presentation = null
        }
    }

    private suspend fun data(): ChannelData? {
        if (store.state.value == null) store.load()
        return store.state.value
    }

    private fun radios(d: ChannelData) = d.channels.filter { it.isRadio }.sortedBy { it.number }
    private fun israeliTv(d: ChannelData) = d.channels.filter { !it.isRadio && it.categoryEn == "israel" }.sortedBy { it.number }

    private fun children(d: ChannelData, parent: String): List<MediaItem> = when (parent) {
        ROOT -> listOf(
            folder(RADIO, "תחנות רדיו"),
            folder(TV, "ערוצי טלוויזיה ווידאו"),
            folder(VOD, "סדרות וסרטים (VOD)"),
            folder(FAVORITES, "מועדפים")
        )
        RADIO -> radios(d).map(::playable)
        TV -> israeliTv(d).map(::playable)
        VOD -> d.vod.map { s ->
            MediaItem.Builder()
                .setMediaId("vod_series_${s.id}")
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(s.title)
                        .setArtist(s.genres.joinToString(", "))
                        .setArtworkUri(artworkUrl(s.poster))
                        .setIsBrowsable(true)
                        .setIsPlayable(false)
                        .setMediaType(MediaMetadata.MEDIA_TYPE_FOLDER_TV_SHOWS)
                        .build()
                )
                .build()
        }
        FAVORITES -> AppPrefs(this).favorites.mapNotNull { d.byId[it] }.map(::playable)
        else -> {
            if (parent.startsWith("vod_series_")) {
                val seriesId = parent.removePrefix("vod_series_")
                val series = d.vod.find { it.id == seriesId }
                if (series != null) {
                    series.episodes.map { ep ->
                        MediaItem.Builder()
                            .setMediaId("vod_ep_${series.id}_${ep.id}")
                            .setUri(ep.url)
                            .setMediaMetadata(
                                MediaMetadata.Builder()
                                    .setTitle("${series.title} · ${ep.title}")
                                    .setArtist(ep.duration.ifEmpty { "פרק מלא" })
                                    .setArtworkUri(artworkUrl(ep.thumb.ifEmpty { series.poster }))
                                    .setIsBrowsable(false)
                                    .setIsPlayable(true)
                                    .setMediaType(MediaMetadata.MEDIA_TYPE_VIDEO)
                                    .build()
                            )
                            .build()
                    }
                } else emptyList()
            } else emptyList()
        }
    }

    private fun search(d: ChannelData, query: String): List<Channel> {
        val q = query.trim()
        if (q.isEmpty()) return emptyList()
        return (radios(d) + israeliTv(d)).filter { it.name.contains(q, true) || it.title.contains(q, true) }
    }

    private fun folder(id: String, title: String) = MediaItem.Builder()
        .setMediaId(id)
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle(title)
                .setIsBrowsable(true)
                .setIsPlayable(false)
                .setMediaType(if (id == RADIO) MediaMetadata.MEDIA_TYPE_FOLDER_RADIO_STATIONS else MediaMetadata.MEDIA_TYPE_FOLDER_TV_CHANNELS)
                .build()
        )
        .build()

    private fun playable(ch: Channel, stream: String? = null) = MediaItem.Builder()
        .setMediaId(ch.id)
        .apply { if (stream != null) setUri(stream) }
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle(ch.name)
                .setArtist(if (ch.isRadio) "רדיו" else ch.category)
                .setArtworkUri(artwork(ch))
                .setIsBrowsable(false)
                .setIsPlayable(true)
                .setMediaType(if (ch.isRadio) MediaMetadata.MEDIA_TYPE_RADIO_STATION else MediaMetadata.MEDIA_TYPE_TV_CHANNEL)
                .build()
        )
        .build()

    private fun artworkUrl(path: String): Uri? = when {
        path.isBlank() -> null
        path.startsWith("http") -> Uri.parse(path)
        else -> Uri.parse(REMOTE + path.removePrefix("/"))
    }

    private fun artwork(ch: Channel): Uri? = artworkUrl(ch.logo)

    private fun tryNextStream(player: Player) {
        val item = player.currentMediaItem ?: return
        val index = player.currentMediaItemIndex
        scope.launch {
            val ch = data()?.byId?.get(item.mediaId) ?: return@launch
            val next = (attempts[ch.id] ?: 0) + 1
            val url = ch.allCandidateUrls().getOrNull(next) ?: return@launch
            attempts[ch.id] = next
            val stream = runCatching { playableUrl(url, ch.auth) }.getOrNull() ?: return@launch
            player.replaceMediaItem(index, playable(ch, stream))
            player.prepare()
            player.play()
        }
    }

    private suspend fun resolve(ch: Channel): MediaItem? {
        attempts[ch.id] = 0
        val stream = runCatching { playableUrl(ch) }.getOrNull() ?: return null
        return playable(ch, stream)
    }

    private inner class Callback : MediaLibrarySession.Callback {
        override fun onGetLibraryRoot(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            params: LibraryParams?,
        ): ListenableFuture<LibraryResult<MediaItem>> {
            val extras = Bundle().apply { putInt(CONTENT_STYLE_PLAYABLE, CONTENT_STYLE_GRID) }
            return scope.future {
                LibraryResult.ofItem(folder(ROOT, "ישראל TV+"), LibraryParams.Builder().setExtras(extras).build())
            }
        }

        override fun onGetItem(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            mediaId: String,
        ): ListenableFuture<LibraryResult<MediaItem>> = scope.future {
            val d = data()
            val ch = d?.byId?.get(mediaId)
            if (ch != null) {
                LibraryResult.ofItem(playable(ch), null)
            } else {
                LibraryResult.ofError(LibraryResult.RESULT_ERROR_BAD_VALUE)
            }
        }

        override fun onGetChildren(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            parentId: String,
            page: Int,
            pageSize: Int,
            params: LibraryParams?,
        ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> = scope.future {
            val all = data()?.let { children(it, parentId) }.orEmpty()
            LibraryResult.ofItemList(ImmutableList.copyOf(pageOf(all, page, pageSize)), params)
        }

        override fun onSearch(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            query: String,
            params: LibraryParams?,
        ): ListenableFuture<LibraryResult<Void>> = scope.future {
            val count = data()?.let { search(it, query).size } ?: 0
            session.notifySearchResultChanged(browser, query, count, params)
            LibraryResult.ofVoid()
        }

        override fun onGetSearchResult(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            query: String,
            page: Int,
            pageSize: Int,
            params: LibraryParams?,
        ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> = scope.future {
            val found = data()?.let { search(it, query) }.orEmpty().map { playable(it) }
            LibraryResult.ofItemList(ImmutableList.copyOf(pageOf(found, page, pageSize)), params)
        }

        override fun onSetMediaItems(
            mediaSession: MediaSession,
            controller: MediaSession.ControllerInfo,
            mediaItems: List<MediaItem>,
            startIndex: Int,
            startPositionMs: Long,
        ): ListenableFuture<MediaSession.MediaItemsWithStartPosition> = scope.future {
            val d = data()
            val first = mediaItems.firstOrNull()
            if (d == null || first == null) {
                return@future MediaSession.MediaItemsWithStartPosition(emptyList(), 0, C.TIME_UNSET)
            }

            // Check if VOD episode
            if (first.mediaId.startsWith("vod_ep_")) {
                for (s in d.vod) {
                    val ep = s.episodes.find { "vod_ep_${s.id}_${it.id}" == first.mediaId || it.id == first.mediaId }
                    if (ep != null) {
                        currentChannel = ep.toChannel(s)
                        val item = MediaItem.Builder()
                            .setMediaId(first.mediaId)
                            .setUri(ep.url)
                            .setMediaMetadata(
                                MediaMetadata.Builder()
                                    .setTitle("${s.title} · ${ep.title}")
                                    .setArtist(ep.duration.ifEmpty { "פרק מלא" })
                                    .setArtworkUri(artworkUrl(ep.thumb.ifEmpty { s.poster }))
                                    .setIsBrowsable(false)
                                    .setIsPlayable(true)
                                    .setMediaType(MediaMetadata.MEDIA_TYPE_VIDEO)
                                    .build()
                            )
                            .build()
                        return@future MediaSession.MediaItemsWithStartPosition(listOf(item), 0, startPositionMs)
                    }
                }
            }

            val ch = d.byId[first.mediaId] ?: search(d, first.requestMetadata.searchQuery.orEmpty()).firstOrNull()
            if (ch == null) {
                return@future MediaSession.MediaItemsWithStartPosition(emptyList(), 0, C.TIME_UNSET)
            }
            currentChannel = ch
            if (ch.isRadio) {
                val list = radios(d)
                list.forEach { attempts[it.id] = 0 }
                MediaSession.MediaItemsWithStartPosition(
                    list.map { playable(it, it.url) }, list.indexOfFirst { it.id == ch.id }.coerceAtLeast(0), C.TIME_UNSET,
                )
            } else {
                MediaSession.MediaItemsWithStartPosition(listOfNotNull(resolve(ch)), 0, C.TIME_UNSET)
            }
        }
    }

    private fun <T> pageOf(all: List<T>, page: Int, pageSize: Int): List<T> {
        val from = page.toLong() * pageSize
        if (from >= all.size) return emptyList()
        return all.subList(from.toInt(), minOf(from + pageSize, all.size.toLong()).toInt())
    }
}

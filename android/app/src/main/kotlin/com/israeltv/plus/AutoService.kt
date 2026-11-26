package com.israeltv.plus

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.hardware.display.DisplayManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Display
import androidx.core.app.NotificationCompat
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.ListenableFuture
import com.israeltv.plus.data.AppPrefs
import com.israeltv.plus.data.AutoArtworkProvider
import com.israeltv.plus.data.Channel
import com.israeltv.plus.data.ChannelData
import com.israeltv.plus.data.ChannelStore
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
private const val FAVORITES = "favorites"
private const val TV = "tv"
private const val TV_MAIN = "tv_main"
private const val TV_SPORTS = "tv_sports"
private const val TV_NEWS = "tv_news"
private const val TV_KIDS = "tv_kids"
private const val TV_ENTERTAINMENT = "tv_entertainment"
private const val TV_ALL = "tv_all"
private const val TV_CATEGORIES = "tv_categories"

private const val RADIO = "radio"
private const val RADIO_TOP = "radio_top"
private const val RADIO_REGIONAL = "radio_regional"
private const val RADIO_MUSIC = "radio_music"
private const val RADIO_HERITAGE = "radio_heritage"
private const val RADIO_ALL = "radio_all"
private const val RADIO_CATEGORIES = "radio_categories"

private const val VOD = "vod"
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

    @Volatile
    private var cachedData: ChannelData? = null

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
        override fun onDisplayChanged(displayId: Int) {
            // Intentionally ignore minor display metric/mode adjustments while driving
            // to prevent surface reconnections and video flickering.
        }
    }

    override fun onCreate() {
        super.onCreate()
        val http = DefaultHttpDataSource.Factory()
            .setUserAgent(USER_AGENT)
            .setAllowCrossProtocolRedirects(true)
            .setConnectTimeoutMs(8000)
            .setReadTimeoutMs(10000)

        // Car/road tuned load control: lower initial buffering for instant start while driving,
        // resilient buffer window to withstand cellular handovers between towers.
        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                /* minBufferMs = */ 15_000,
                /* maxBufferMs = */ 30_000,
                /* bufferForPlaybackMs = */ 1_500,
                /* bufferForPlaybackAfterRebufferMs = */ 2_500
            )
            .setPrioritizeTimeOverSizeThresholds(true)
            .build()

        val player = ExoPlayer.Builder(this)
            .setMediaSourceFactory(DefaultMediaSourceFactory(DefaultDataSource.Factory(this, http)))
            .setLoadControl(loadControl)
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
            store.state.collect { d ->
                if (d != null) cachedData = d
            }
        }
        scope.launch {
            store.load()
            store.refresh()
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? = session

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            if (nm != null && nm.getNotificationChannel("car_play") == null) {
                val channel = NotificationChannel("car_play", "שידור ברכב", NotificationManager.IMPORTANCE_LOW)
                nm.createNotificationChannel(channel)
            }
            val notif = NotificationCompat.Builder(this, "car_play")
                .setSmallIcon(R.drawable.ic_player_play)
                .setContentTitle("ישראל TV+")
                .setContentText("מחובר לרכב (Android Auto)")
                .setSilent(true)
                .setOngoing(true)
                .build()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                runCatching {
                    startForeground(101, notif, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
                }
            } else {
                runCatching {
                    startForeground(101, notif)
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

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
        cachedData?.let { return it }
        val s = store.state.value
        if (s != null) {
            cachedData = s
            return s
        }
        store.load()
        cachedData = store.state.value
        return cachedData
    }

    private fun isTv(c: Channel) = !c.isRadio
    private fun isRadio(c: Channel) = c.isRadio

    private val topTvIds = listOf(
        "kan11", "keshet12", "reshet13", "now14", "i24news_he",
        "channel9", "channel24_israel", "ch33_makan", "knesset",
        "ynet_live", "walla_news_il", "sport5_israel_hd", "sport1_israel"
    )

    private fun tvMain(d: ChannelData): List<Channel> {
        val topMap = d.channels.filter { isTv(it) && it.id in topTvIds }.associateBy { it.id }
        val orderedTop = topTvIds.mapNotNull { topMap[it] }
        val rest = d.channels.filter { isTv(it) && it.categoryEn == "israel" && it.id !in topTvIds }.sortedBy { it.number }
        return (orderedTop + rest).distinctBy { it.id }
    }

    private fun tvSports(d: ChannelData): List<Channel> =
        d.channels.filter { isTv(it) && (it.categoryEn == "sports" || "ספורט" in it.category || "sport" in it.name.lowercase() || "ספורט" in it.name || "one" in it.name.lowercase()) }
            .sortedBy { it.number }

    private fun tvNews(d: ChannelData): List<Channel> =
        d.channels.filter { isTv(it) && (it.categoryEn == "news" || "חדשות" in it.category || "חדשות" in it.name || "news" in it.name.lowercase() || it.id in setOf("kan11", "keshet12", "reshet13", "now14", "i24news_he", "knesset", "ynet_live")) }
            .sortedBy { it.number }

    private fun tvKids(d: ChannelData): List<Channel> =
        d.channels.filter { isTv(it) && it.isKidsChannel }
            .sortedBy { it.number }

    private fun tvEntertainment(d: ChannelData): List<Channel> =
        d.channels.filter { isTv(it) && (it.categoryEn in setOf("movies", "series", "lifestyle", "food", "docu") || "סרטים" in it.category || "סדרות" in it.category || "בידור" in it.category) }
            .sortedBy { it.number }

    private fun tvAll(d: ChannelData): List<Channel> =
        d.channels.filter { isTv(it) }.sortedBy { it.number }

    private val topRadioIds = listOf(
        "galgalatz_live", "glz_live", "kan_bet", "kan_gimel", "kan_88",
        "radio_103fm", "eco99fm", "radios_100fm", "radio_102fm", "radio_91fm",
        "kol_chai", "kol_barama_live"
    )

    private fun radioTop(d: ChannelData): List<Channel> {
        val topMap = d.channels.filter { isRadio(it) && it.id in topRadioIds }.associateBy { it.id }
        val ordered = topRadioIds.mapNotNull { topMap[it] }
        val rest = d.channels.filter { isRadio(it) && it.id !in topRadioIds }
        return (ordered + rest.take(10)).distinctBy { it.id }
    }

    private fun radioRegional(d: ChannelData): List<Channel> =
        d.channels.filter { isRadio(it) && ("אזורי" in it.category || it.id in setOf("radio_103fm", "eco99fm", "radios_100fm", "radio_102fm", "radio_91fm", "radio_haifa", "radio_darom_live", "radio_jerusalem", "radio_kol_rega", "radio_eilat")) }
            .sortedBy { it.number }

    private fun radioMusic(d: ChannelData): List<Channel> =
        d.channels.filter { isRadio(it) && ("מוזיקה" in it.category || "מוסיקה" in it.category || "music" in it.categoryEn || "galgalatz" in it.id || "kan_gimel" in it.id || "kan_88" in it.id || "kan_kol_hamusica" in it.id || "joint" in it.id || "mizrahit" in it.id) }
            .sortedBy { it.number }

    private fun radioHeritage(d: ChannelData): List<Channel> =
        d.channels.filter { isRadio(it) && ("מורשת" in it.category || "יהדות" in it.category || "דת" in it.category || "breslev" in it.id || "kol_chai" in it.id || "kol_barama" in it.id || "kan_moreshet" in it.id || "chabad" in it.id || "radio_2000" in it.id) }
            .sortedBy { it.number }

    private fun radioAll(d: ChannelData): List<Channel> =
        d.channels.filter { isRadio(it) }.sortedBy { it.number }

    private fun children(d: ChannelData, parent: String): List<MediaItem> = when (parent) {
        ROOT -> listOf(
            folder(FAVORITES, "⭐ ערוצים מועדפים"),
            folder(TV_MAIN, "📺 ערוצי טלוויזיה מובילים (שידור חי)"),
            folder(TV_SPORTS, "⚽ ערוצי ספורט (שידור חי)"),
            folder(RADIO_TOP, "📻 תחנות רדיו מובילות"),
            folder(TV_CATEGORIES, "📁 כל ערוצי הטלוויזיה לפי קטגוריות"),
            folder(RADIO_CATEGORIES, "📁 כל תחנות הרדיו"),
            folder(VOD, "🎬 סדרות וסרטים (VOD)")
        )
        FAVORITES -> AppPrefs(this).favorites.mapNotNull { d.byId[it] }.map(::playable)
        TV_MAIN, TV -> tvMain(d).map(::playable)
        TV_SPORTS -> tvSports(d).map(::playable)
        TV_NEWS -> tvNews(d).map(::playable)
        TV_KIDS -> tvKids(d).map(::playable)
        TV_ENTERTAINMENT -> tvEntertainment(d).map(::playable)
        TV_ALL -> tvAll(d).map(::playable)
        TV_CATEGORIES -> listOf(
            folder(TV_MAIN, "📺 ערוצים מובילים (11, 12, 13, 14, 9, 24...)"),
            folder(TV_SPORTS, "⚽ כל ערוצי הספורט (ספורט 1-6, 5SPORT, ONE)"),
            folder(TV_NEWS, "📰 חדשות ואקטואליה"),
            folder(TV_KIDS, "👶 ערוצי ילדים ונוער"),
            folder(TV_ENTERTAINMENT, "🍿 סרטים, סדרות ובידור"),
            folder(TV_ALL, "📋 כל הערוצים (א-ב)")
        )
        RADIO_TOP, RADIO -> radioTop(d).map(::playable)
        RADIO_REGIONAL -> radioRegional(d).map(::playable)
        RADIO_MUSIC -> radioMusic(d).map(::playable)
        RADIO_HERITAGE -> radioHeritage(d).map(::playable)
        RADIO_ALL -> radioAll(d).map(::playable)
        RADIO_CATEGORIES -> listOf(
            folder(RADIO_TOP, "📻 תחנות מובילות (גלגלצ, גלצ, כאן ב, 103FM...)"),
            folder(RADIO_REGIONAL, "🏙️ רדיו אזורי"),
            folder(RADIO_MUSIC, "🎵 מוזיקה ומגוון סגנונות"),
            folder(RADIO_HERITAGE, "🕍 מורשת ויהדות"),
            folder(RADIO_ALL, "📋 כל תחנות הרדיו (א-ב)")
        )
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
        return d.channels.filter { it.name.contains(q, true) || it.title.contains(q, true) }
    }

    private fun folder(id: String, title: String) = MediaItem.Builder()
        .setMediaId(id)
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle(title)
                .setArtworkUri(AutoArtworkProvider.buildUri("icon.png"))
                .setIsBrowsable(true)
                .setIsPlayable(false)
                .setMediaType(if (id.startsWith("radio")) MediaMetadata.MEDIA_TYPE_FOLDER_RADIO_STATIONS else MediaMetadata.MEDIA_TYPE_FOLDER_TV_CHANNELS)
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
        path.isBlank() -> AutoArtworkProvider.buildUri("icon.png")
        path.startsWith("content://") -> Uri.parse(path)
        path.startsWith("http://") || path.startsWith("https://") -> Uri.parse(path)
        else -> AutoArtworkProvider.buildUri(path)
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
                val list = radioAll(d)
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

package com.israeltv.plus.ui.screens

import android.content.Context
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.VolumeOff
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Fullscreen
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.israeltv.plus.data.AppPrefs
import com.israeltv.plus.data.Channel
import com.israeltv.plus.data.ChannelData
import com.israeltv.plus.data.USER_AGENT
import com.israeltv.plus.data.VodSeries
import com.israeltv.plus.data.playableUrl
import com.israeltv.plus.ui.components.ChannelLogo
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

enum class WowExperience {
    REELS,
    COMMAND_WALL,
    GLOBE_3D,
    TIME_MACHINE,
    TV_TRIVIA,
    JACKPOT_SLOT,
}

/**
 * Futuristic 6-card Neon Launchpad shown right at the top of the Home Screen.
 */
@Composable
fun WowLaunchpadStrip(
    onOpen: (WowExperience) -> Unit,
    modifier: Modifier = Modifier,
) {
    val cards = remember {
        listOf(
            Triple(
                WowExperience.REELS,
                "🔥 רילס TV חי",
                "זפזופ אנכי בסגנון טיקטוק",
            ) to listOf(Color(0xFFEC4899), Color(0xFF8B5CF6)),
            Triple(
                WowExperience.COMMAND_WALL,
                "🛰️ חמ״ל 4 ערוצים",
                "קיר שידורים 2×2 בו-זמנית",
            ) to listOf(Color(0xFF0EA5E9), Color(0xFF2563EB)),
            Triple(
                WowExperience.GLOBE_3D,
                "🌍 גלובוס 3D חי",
                "סובבו את כדור הארץ וצפו",
            ) to listOf(Color(0xFF10B981), Color(0xFF059669)),
            Triple(
                WowExperience.TIME_MACHINE,
                "🕰️ מכונת הזמן",
                "מסע בין עשורי הטלוויזיה",
            ) to listOf(Color(0xFFA855F7), Color(0xFF6366F1)),
            Triple(
                WowExperience.TV_TRIVIA,
                "🧠 טריוויה TV",
                "חידון חי נושא דרגות ורצף",
            ) to listOf(Color(0xFF06B6D4), Color(0xFF3B82F6)),
            Triple(
                WowExperience.JACKPOT_SLOT,
                "🎰 רולטת הקסם",
                "מכונת מזל לשידור הבא",
            ) to listOf(Color(0xFFF59E0B), Color(0xFFEA580C)),
        )
    }

    Column(modifier.fillMaxWidth().padding(top = 4.dp, bottom = 10.dp)) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                "✨ חוויות מיוחדות · מעבדת העתיד",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
            )
            Text(
                "חדש!",
                color = Color(0xFF38BDF8),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(Modifier.height(6.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(11.dp),
        ) {
            items(cards, key = { it.first.first.name }) { (info, grad) ->
                val (mode, title, sub) = info
                Box(
                    Modifier
                        .width(164.dp)
                        .height(82.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Brush.linearGradient(grad.map { it.copy(alpha = 0.28f) }))
                        .border(1.2.dp, Brush.linearGradient(grad.map { it.copy(alpha = 0.78f) }), RoundedCornerShape(20.dp))
                        .clickable { onOpen(mode) }
                        .padding(horizontal = 13.dp, vertical = 11.dp),
                ) {
                    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                        Text(
                            title,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1,
                        )
                        Text(
                            sub,
                            color = Color.White.copy(alpha = 0.82f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
    }
}

private fun buildLightPlayer(context: Context, lowBitrate: Boolean = true, muted: Boolean = false): ExoPlayer {
    val defaultHttp = DefaultHttpDataSource.Factory()
        .setUserAgent(USER_AGENT)
        .setAllowCrossProtocolRedirects(true)
        .setConnectTimeoutMs(6_000)
        .setReadTimeoutMs(10_000)
        .setDefaultRequestProperties(mapOf("Referer" to "https://stmify.com/"))
    val hamalHttp = DefaultHttpDataSource.Factory()
        .setUserAgent(USER_AGENT)
        .setAllowCrossProtocolRedirects(true)
        .setConnectTimeoutMs(6_000)
        .setReadTimeoutMs(10_000)
        .setDefaultRequestProperties(mapOf("Referer" to "https://hamalsport.tv/", "Origin" to "https://hamalsport.tv"))
    val routingHttp = androidx.media3.datasource.DataSource.Factory {
        val base = defaultHttp.createDataSource()
        val hamal = hamalHttp.createDataSource()
        object : androidx.media3.datasource.DataSource {
            private var active: androidx.media3.datasource.DataSource = base
            override fun addTransferListener(transferListener: androidx.media3.datasource.TransferListener) {
                base.addTransferListener(transferListener)
                hamal.addTransferListener(transferListener)
            }
            override fun open(dataSpec: androidx.media3.datasource.DataSpec): Long {
                val host = dataSpec.uri.host.orEmpty()
                val path = dataSpec.uri.encodedPath.orEmpty()
                val isHamal = host.contains("2lacasaba", true) || host.contains("hamalsport", true) ||
                    host.contains("hepbettv", true) || path.contains("/tracks-v1a1/", true) || path.contains("/yessport", true)
                active = if (isHamal) hamal else base
                return active.open(dataSpec)
            }
            override fun read(buffer: ByteArray, offset: Int, length: Int): Int = active.read(buffer, offset, length)
            override fun getUri(): android.net.Uri? = active.uri
            override fun close() = active.close()
        }
    }
    val trackSelector = DefaultTrackSelector(context).apply {
        parameters = buildUponParameters().apply {
            if (lowBitrate) {
                setMaxVideoSize(854, 480)
                setMaxVideoBitrate(1_500_000)
            }
        }.build()
    }
    val loadControl = DefaultLoadControl.Builder()
        .setBufferDurationsMs(6_000, 18_000, 800, 1_500)
        .build()
    return ExoPlayer.Builder(context)
        .setTrackSelector(trackSelector)
        .setLoadControl(loadControl)
        .setMediaSourceFactory(DefaultMediaSourceFactory(routingHttp))
        .setAudioAttributes(
            AudioAttributes.Builder().setUsage(C.USAGE_MEDIA).setContentType(C.AUDIO_CONTENT_TYPE_MOVIE).build(),
            !muted,
        )
        .build()
        .apply { volume = if (muted) 0f else 1f }
}

/**
 * 1. LIVE TV REELS (TikTok-style Fullscreen Vertical Live Broadcast Feed!)
 */
@Composable
fun LiveTvReelsScreen(
    data: ChannelData,
    prefs: AppPrefs,
    logo: (Channel) -> String,
    onOpenFullPlayer: (Channel, List<Channel>) -> Unit,
    onClose: () -> Unit,
) {
    BackHandler(onBack = onClose)
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    var deckSeed by remember { mutableIntStateOf(0) }
    val deck = remember(data, deckSeed) {
        val tvOnly = data.channels.filter { !it.isRadio && it.url.isNotBlank() && it.kind != "app" }
        val israeli = tvOnly.filter { it.country == "IL" || it.categoryEn == "israel" }
        val world = tvOnly - israeli.toSet()
        if (deckSeed == 0) (israeli.shuffled(Random(42)) + world.shuffled(Random(42))).ifEmpty { tvOnly }
        else tvOnly.shuffled()
    }

    val pagerState = rememberPagerState(pageCount = { deck.size.coerceAtLeast(1) })
    var muted by remember { mutableStateOf(false) }
    val player = remember { buildLightPlayer(context, lowBitrate = false, muted = false) }

    DisposableEffect(Unit) {
        onDispose { player.release() }
    }

    LaunchedEffect(muted) {
        player.volume = if (muted) 0f else 1f
    }

    val activeChannel = deck.getOrNull(pagerState.settledPage)
    var reelCandIdx by remember(activeChannel?.id) { mutableIntStateOf(0) }
    val reelCandidates = remember(activeChannel?.id) {
        activeChannel?.let { listOf(it.url) + it.backups + it.fallbackUrls }?.distinct()?.filter { it.isNotBlank() }.orEmpty()
    }

    DisposableEffect(player, activeChannel?.id, reelCandidates) {
        val listener = object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                if (reelCandIdx + 1 < reelCandidates.size) {
                    reelCandIdx++
                }
            }
        }
        player.addListener(listener)
        onDispose { player.removeListener(listener) }
    }

    LaunchedEffect(activeChannel?.id, reelCandIdx) {
        val ch = activeChannel ?: return@LaunchedEffect
        val raw = reelCandidates.getOrElse(reelCandIdx) { ch.url }
        if (reelCandIdx == 0) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        player.stop()
        player.clearMediaItems()
        val url = runCatching { playableUrl(raw, ch.auth, hamalSlug = ch.hamalSlug) }.getOrDefault(raw)
        player.setMediaItem(MediaItem.Builder().setUri(url).setMimeType(MimeTypes.APPLICATION_M3U8).build())
        player.prepare()
        player.playWhenReady = true
    }

    // Animated neon pulse for Ambilight border
    val inf = rememberInfiniteTransition(label = "reels_glow")
    val pulse by inf.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(tween(1400, easing = LinearEasing), RepeatMode.Reverse),
        label = "pulse",
    )

    Box(Modifier.fillMaxSize().background(Color.Black)) {
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            beyondViewportPageCount = 0,
        ) { pageIdx ->
            val ch = deck.getOrNull(pageIdx) ?: return@VerticalPager
            val isSettled = pageIdx == pagerState.settledPage
            val isFav = prefs.isFavorite(ch.id)

            Box(Modifier.fillMaxSize().background(Color(0xFF050812))) {
                // Dynamic Ambilight backdrop glow
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF6366F1).copy(alpha = 0.25f * pulse),
                                    Color(0xFFEC4899).copy(alpha = 0.14f * pulse),
                                    Color.Black,
                                )
                            )
                        )
                )

                if (isSettled) {
                    AndroidView(
                        factory = { ctx ->
                            PlayerView(ctx).apply {
                                layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                                useController = false
                                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                                this.player = player
                            }
                        },
                        update = { it.player = player },
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    Column(
                        Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        ChannelLogo(logo(ch), Modifier.size(92.dp), true)
                        Spacer(Modifier.height(12.dp))
                        Text(ch.name, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Bottom gradient + TikTok-style channel info & equalizer
                Box(
                    Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.88f))))
                        .navigationBarsPadding()
                        .padding(start = 18.dp, end = 82.dp, bottom = 26.dp, top = 48.dp),
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFEF4444))
                                    .padding(horizontal = 8.dp, vertical = 3.dp),
                            ) {
                                Text("● LIVE REEL #${pageIdx + 1}", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                            }
                            Text(ch.category, color = Color(0xFF7DD3FC), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Text(ch.name, color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.ExtraBold, maxLines = 2)
                        if (ch.desc.isNotBlank()) {
                            Text(ch.desc, color = Color.White.copy(alpha = 0.76f), fontSize = 12.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        }
                        Text("⬆️ החלק למעלה לערוץ הבא · מנגן בשידור חי", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                    }
                }

                // Floating TikTok Right/End Action Rail
                Column(
                    Modifier
                        .align(Alignment.BottomEnd)
                        .navigationBarsPadding()
                        .padding(end = 14.dp, bottom = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Box(
                        Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.14f))
                            .border(1.5.dp, Color(0xFFEC4899), CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        ChannelLogo(logo(ch), Modifier.size(36.dp), true)
                    }

                    ReelActionButton(
                        icon = if (isFav) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                        tint = if (isFav) Color(0xFFEF4444) else Color.White,
                        label = "מועדף",
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            prefs.toggleFavorite(ch.id)
                        },
                    )
                    ReelActionButton(
                        icon = if (muted) Icons.AutoMirrored.Rounded.VolumeOff else Icons.AutoMirrored.Rounded.VolumeUp,
                        tint = Color.White,
                        label = if (muted) "הפעל קול" else "השתק",
                        onClick = { muted = !muted },
                    )
                    ReelActionButton(
                        icon = Icons.Rounded.Refresh,
                        tint = Color(0xFFFACC15),
                        label = "ערבב",
                        onClick = {
                            deckSeed++
                            scope.launch { pagerState.scrollToPage(0) }
                        },
                    )
                    ReelActionButton(
                        icon = Icons.Rounded.Fullscreen,
                        tint = Color(0xFF38BDF8),
                        label = "מסך מלא",
                        onClick = { onOpenFullPlayer(ch, deck) },
                    )
                }
            }
        }

        // Top header bar
        Row(
            Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.55f))
                    .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                    .clickable { onClose() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "סגירה", tint = Color.White)
            }
            Text("🔥 רילס TV חי", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.size(42.dp))
        }
    }
}

@Composable
private fun ReelActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    label: String,
    onClick: () -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.55f))
                .border(1.dp, Color.White.copy(alpha = 0.22f), CircleShape)
                .clickable { onClick() },
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = label, tint = tint, modifier = Modifier.size(24.dp))
        }
        Spacer(Modifier.height(3.dp))
        Text(label, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
    }
}

/**
 * 2. LIVE 2x2 BROADCAST COMMAND WALL (חמ״ל 4 ערוצים בו-זמנית!)
 */
@Composable
fun CommandWallScreen(
    data: ChannelData,
    logo: (Channel) -> String,
    onExpandChannel: (Channel, List<Channel>) -> Unit,
    onClose: () -> Unit,
) {
    BackHandler(onBack = onClose)
    val context = LocalContext.current
    val tvChannels = remember(data) { data.channels.filter { !it.isRadio && it.url.isNotBlank() && it.kind != "app" } }

    fun pickByIds(ids: List<String>, fallbackFilter: (Channel) -> Boolean): List<Channel> {
        val matched = ids.mapNotNull { data.byId[it] }.toMutableList()
        if (matched.size < 4) {
            val extras = tvChannels.filter { fallbackFilter(it) && it !in matched }.take(4 - matched.size)
            matched.addAll(extras)
        }
        if (matched.size < 4) {
            matched.addAll(tvChannels.filter { it !in matched }.take(4 - matched.size))
        }
        return matched.take(4)
    }

    var preset by remember { mutableStateOf("news_il") }
    var randomTick by remember { mutableIntStateOf(0) }
    val quadChannels = remember(preset, randomTick, data) {
        when (preset) {
            "news_il" -> pickByIds(listOf("kan11", "keshet12", "reshet13", "now14")) { it.categoryEn == "israel" }
            "sports" -> pickByIds(listOf("sport5_israel_hd", "sport5_plus_hd", "one_1_hd", "one_2_hd")) { it.categoryEn == "sports" }
            "cinema" -> pickByIds(listOf("freetv_israeli_movies", "freetv_movies_action", "keshet_drama", "erez_nehederet_tv")) { it.categoryEn == "movies" || it.categoryEn == "israel" }
            "world" -> pickByIds(listOf("abc_news_live", "cbs_news_247", "aljazeera_en_hd", "bloomberg_tv_plus")) { it.categoryEn == "news" }
            else -> tvChannels.shuffled().take(4)
        }
    }

    var activeAudioIdx by remember { mutableIntStateOf(0) }
    val players = remember {
        List(4) { idx -> buildLightPlayer(context, lowBitrate = true, muted = idx != 0) }
    }

    DisposableEffect(Unit) {
        onDispose { players.forEach { runCatching { it.release() } } }
    }

    LaunchedEffect(activeAudioIdx) {
        players.forEachIndexed { i, p -> p.volume = if (i == activeAudioIdx) 1f else 0f }
    }

    quadChannels.forEachIndexed { idx, ch ->
        var qCandIdx by remember(idx, ch.id) { mutableIntStateOf(0) }
        val qCandidates = remember(ch.id) { (listOf(ch.url) + ch.backups + ch.fallbackUrls).distinct().filter { it.isNotBlank() } }
        DisposableEffect(players[idx], ch.id) {
            val l = object : Player.Listener {
                override fun onPlayerError(error: PlaybackException) {
                    if (qCandIdx + 1 < qCandidates.size) qCandIdx++
                }
            }
            players[idx].addListener(l)
            onDispose { players[idx].removeListener(l) }
        }
        LaunchedEffect(idx, ch.id, qCandIdx) {
            val p = players[idx]
            val raw = qCandidates.getOrElse(qCandIdx) { ch.url }
            p.stop()
            p.clearMediaItems()
            val url = runCatching { playableUrl(raw, ch.auth, hamalSlug = ch.hamalSlug) }.getOrDefault(raw)
            p.setMediaItem(MediaItem.Builder().setUri(url).setMimeType(MimeTypes.APPLICATION_M3U8).build())
            p.volume = if (idx == activeAudioIdx) 1f else 0f
            p.prepare()
            p.playWhenReady = true
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(Color(0xFF040711))
            .windowInsetsPadding(WindowInsets.statusBars)
            .navigationBarsPadding(),
    ) {
        // Header
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    Modifier.size(38.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.1f)).clickable { onClose() },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Rounded.Close, contentDescription = "סגירה", tint = Color.White)
                }
                Column {
                    Text("🛰️ חדר בקרה חמ״ל 2×2", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold)
                    Text("הקש על מסך לבחירת השמע הפעיל · ⛶ להגדלה", color = Color(0xFF94A3B8), fontSize = 11.sp)
                }
            }
        }

        // Preset Wall Switcher Chips
        Row(
            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 14.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            listOf(
                "news_il" to "🇮🇱 חמ״ל חדשות ישראל",
                "sports" to "⚽ חמ״ל ספורט חי",
                "cinema" to "🎬 קיר בידור וסרטים",
                "world" to "🌍 חדשות מהעולם",
                "random" to "🎲 4 ערוצים אקראיים",
            ).forEach { (id, label) ->
                val selected = preset == id
                Box(
                    Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(if (selected) Color(0xFF2563EB) else Color.White.copy(alpha = 0.09f))
                        .border(1.dp, if (selected) Color(0xFF60A5FA) else Color.White.copy(alpha = 0.14f), RoundedCornerShape(999.dp))
                        .clickable {
                            preset = id
                            if (id == "random") randomTick++
                        }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                ) {
                    Text(label, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // 2x2 Matrix
        Column(
            Modifier.weight(1f).fillMaxWidth().padding(horizontal = 10.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            for (rowIdx in 0..1) {
                Row(
                    Modifier.weight(1f).fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    for (colIdx in 0..1) {
                        val qIdx = rowIdx * 2 + colIdx
                        val ch = quadChannels.getOrNull(qIdx)
                        val audioOn = activeAudioIdx == qIdx
                        Box(
                            Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.Black)
                                .border(
                                    width = if (audioOn) 2.5.dp else 1.dp,
                                    color = if (audioOn) Color(0xFF38BDF8) else Color.White.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(16.dp),
                                )
                                .clickable { activeAudioIdx = qIdx },
                        ) {
                            if (ch != null) {
                                AndroidView(
                                    factory = { ctx ->
                                        PlayerView(ctx).apply {
                                            layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                                            useController = false
                                            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                                            this.player = players[qIdx]
                                        }
                                    },
                                    update = { it.player = players[qIdx] },
                                    modifier = Modifier.fillMaxSize(),
                                )
                                // Top bar inside quadrant
                                Row(
                                    Modifier
                                        .align(Alignment.TopCenter)
                                        .fillMaxWidth()
                                        .background(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.8f), Color.Transparent)))
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        (if (audioOn) "🔊 " else "🔇 ") + ch.name,
                                        color = if (audioOn) Color(0xFF7DD3FC) else Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f),
                                    )
                                    Box(
                                        Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(Color.Black.copy(alpha = 0.6f))
                                            .clickable { onExpandChannel(ch, quadChannels) },
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Icon(Icons.Rounded.Fullscreen, contentDescription = "הגדל", tint = Color.White, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private data class GlobeBeacon(
    val code: String,
    val nameHe: String,
    val flag: String,
    val latDeg: Float,
    val lonDeg: Float,
    val color: Color,
)

private val GLOBE_BEACONS = listOf(
    GlobeBeacon("IL", "ישראל", "🇮🇱", 31.8f, 35.2f, Color(0xFF38BDF8)),
    GlobeBeacon("US", "ארה״ב", "🇺🇸", 39.0f, -98.0f, Color(0xFFA855F7)),
    GlobeBeacon("GB", "בריטניה", "🇬🇧", 53.5f, -2.2f, Color(0xFF34D399)),
    GlobeBeacon("FR", "צרפת", "🇫🇷", 46.6f, 2.3f, Color(0xFFF472B6)),
    GlobeBeacon("DE", "גרמניה", "🇩🇪", 51.1f, 10.4f, Color(0xFFFBBF24)),
    GlobeBeacon("JP", "יפן", "🇯🇵", 36.2f, 138.2f, Color(0xFFFB7185)),
    GlobeBeacon("KR", "דרום קוריאה", "🇰🇷", 36.5f, 127.8f, Color(0xFF60A5FA)),
    GlobeBeacon("CN", "סין", "🇨🇳", 35.8f, 104.2f, Color(0xFFF97316)),
    GlobeBeacon("QA", "המזרח התיכון / עולם", "🌍", 25.3f, 51.2f, Color(0xFF2DD4BF)),
)

/**
 * 3. INTERACTIVE 3D HOLOGRAPHIC SPINNING EARTH GLOBE (גלובוס TV תלת-ממדי!)
 */
@Composable
fun Globe3dExplorerScreen(
    data: ChannelData,
    logo: (Channel) -> String,
    onPlay: (Channel, List<Channel>) -> Unit,
    onClose: () -> Unit,
) {
    BackHandler(onBack = onClose)
    var rotLon by remember { mutableFloatStateOf(-35f) } // Start facing Israel!
    var rotLat by remember { mutableFloatStateOf(22f) }
    var autoSpin by remember { mutableStateOf(true) }
    var selectedCountry by remember { mutableStateOf("IL") }

    LaunchedEffect(autoSpin) {
        while (isActive && autoSpin) {
            rotLon = (rotLon + 0.35f) % 360f
            delay(16L)
        }
    }

    val countryChannels = remember(selectedCountry, data) {
        val all = data.channels.filter { it.kind != "app" && it.url.isNotBlank() }
        if (selectedCountry == "IL") {
            all.filter { it.country == "IL" || it.categoryEn == "israel" }
        } else {
            all.filter { it.country.equals(selectedCountry, ignoreCase = true) }.ifEmpty {
                all.filter { it.country != "IL" }
            }
        }
    }

    val stars = remember {
        List(90) { Triple(Random.nextFloat(), Random.nextFloat(), Random.nextFloat() * 2.2f + 0.8f) }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF020614), Color(0xFF07132C), Color(0xFF030712))))
            .windowInsetsPadding(WindowInsets.statusBars)
            .navigationBarsPadding(),
    ) {
        // Top bar
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier.size(40.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.1f)).clickable { onClose() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Rounded.Close, contentDescription = "סגירה", tint = Color.White)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🌍 גלובוס שידורים 3D חי", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                Text("סובבו עם האצבע את כדור הארץ ובחרו מוקד שידור", color = Color(0xFF7DD3FC), fontSize = 11.sp)
            }
            Box(
                Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(if (autoSpin) Color(0xFF0EA5E9).copy(alpha = 0.3f) else Color.White.copy(alpha = 0.1f))
                    .clickable { autoSpin = !autoSpin }
                    .padding(horizontal = 10.dp, vertical = 6.dp),
            ) {
                Text(if (autoSpin) "⏸ עצור" else "▶ סובב", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Interactive 3D Hologram Sphere Canvas
        Box(
            Modifier
                .fillMaxWidth()
                .weight(1f)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { autoSpin = false },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            rotLon = (rotLon - dragAmount.x * 0.35f) % 360f
                            rotLat = (rotLat + dragAmount.y * 0.25f).coerceIn(-65f, 65f)
                        },
                    )
                },
            contentAlignment = Alignment.Center,
        ) {
            Canvas(Modifier.fillMaxSize()) {
                // Starfield
                stars.forEach { (sx, sy, sr) ->
                    drawCircle(
                        color = Color.White.copy(alpha = 0.45f),
                        radius = sr,
                        center = Offset(sx * size.width, sy * size.height),
                    )
                }

                val cx = size.width / 2f
                val cy = size.height / 2f
                val radius = minOf(size.width, size.height) * 0.40f

                // Outer atmospheric glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF0284C7).copy(alpha = 0.36f), Color.Transparent),
                        center = Offset(cx, cy),
                        radius = radius * 1.38f,
                    ),
                    radius = radius * 1.38f,
                    center = Offset(cx, cy),
                )
                // Deep ocean sphere core
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF0F294E), Color(0xFF061126)),
                        center = Offset(cx - radius * 0.25f, cy - radius * 0.25f),
                        radius = radius * 1.2f,
                    ),
                    radius = radius,
                    center = Offset(cx, cy),
                )

                val rLatRad = rotLat * (PI.toFloat() / 180f)
                val rLonRad = rotLon * (PI.toFloat() / 180f)

                fun project(latDeg: Float, lonDeg: Float): Triple<Float, Float, Float> {
                    val lat = latDeg * (PI.toFloat() / 180f)
                    val lon = lonDeg * (PI.toFloat() / 180f) + rLonRad
                    val x0 = cos(lat) * sin(lon)
                    val y0 = sin(lat)
                    val z0 = cos(lat) * cos(lon)
                    val y1 = y0 * cos(rLatRad) - z0 * sin(rLatRad)
                    val z1 = y0 * sin(rLatRad) + z0 * cos(rLatRad)
                    return Triple(cx + x0 * radius, cy - y1 * radius, z1)
                }

                // Draw 3D Holographic Latitude & Longitude Wireframe Grid
                for (latG in -60..60 step 20) {
                    var prev: Triple<Float, Float, Float>? = null
                    for (lonG in -180..180 step 10) {
                        val cur = project(latG.toFloat(), lonG.toFloat())
                        val p = prev
                        if (p != null && p.third > 0f && cur.third > 0f) {
                            drawLine(
                                color = Color(0xFF38BDF8).copy(alpha = 0.20f),
                                start = Offset(p.first, p.second),
                                end = Offset(cur.first, cur.second),
                                strokeWidth = 1.4f,
                            )
                        }
                        prev = cur
                    }
                }
                for (lonG in -180 until 180 step 20) {
                    var prev: Triple<Float, Float, Float>? = null
                    for (latG in -80..80 step 10) {
                        val cur = project(latG.toFloat(), lonG.toFloat())
                        val p = prev
                        if (p != null && p.third > 0f && cur.third > 0f) {
                            drawLine(
                                color = Color(0xFF38BDF8).copy(alpha = 0.18f),
                                start = Offset(p.first, p.second),
                                end = Offset(cur.first, cur.second),
                                strokeWidth = 1.4f,
                            )
                        }
                        prev = cur
                    }
                }

                // Rim highlight
                drawCircle(
                    color = Color(0xFF38BDF8).copy(alpha = 0.65f),
                    radius = radius,
                    center = Offset(cx, cy),
                    style = Stroke(width = 2.4f),
                )

                // Pulsing Broadcast Beacons on visible hemisphere
                GLOBE_BEACONS.forEach { b ->
                    val (px, py, pz) = project(b.latDeg, b.lonDeg)
                    if (pz > -0.05f) {
                        val isSel = b.code == selectedCountry
                        val haloR = if (isSel) 24f else 14f
                        drawCircle(b.color.copy(alpha = if (isSel) 0.42f else 0.24f), radius = haloR, center = Offset(px, py))
                        drawCircle(b.color, radius = if (isSel) 8.5f else 5.8f, center = Offset(px, py))
                        drawCircle(Color.White, radius = 2.6f, center = Offset(px, py))
                    }
                }
            }
        }

        // Country Beacon Selector Strip
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(GLOBE_BEACONS, key = { it.code }) { b ->
                val sel = b.code == selectedCountry
                Box(
                    Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(if (sel) b.color.copy(alpha = 0.28f) else Color.White.copy(alpha = 0.08f))
                        .border(1.2.dp, if (sel) b.color else Color.White.copy(alpha = 0.15f), RoundedCornerShape(999.dp))
                        .clickable {
                            selectedCountry = b.code
                            autoSpin = false
                            rotLon = -b.lonDeg
                            rotLat = b.latDeg * 0.65f
                        }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                ) {
                    Text("${b.flag} ${b.nameHe}", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        // Live Channels Shelf for Selected Beacon
        Text(
            "📡 משדרים עכשיו ממוקד זה (${countryChannels.size} ערוצים):",
            color = Color(0xFFBAE6FD),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 4.dp),
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(countryChannels.take(30), key = { it.id }) { ch ->
                Column(
                    Modifier
                        .width(132.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .border(1.dp, Color.White.copy(alpha = 0.14f), RoundedCornerShape(16.dp))
                        .clickable { onPlay(ch, countryChannels) }
                        .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    ChannelLogo(logo(ch), Modifier.size(48.dp), true)
                    Spacer(Modifier.height(6.dp))
                    Text(ch.name, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text("▶ שגר שידור", color = Color(0xFF38BDF8), fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}

/**
 * 4. NEON CASINO JACKPOT SLOT MACHINE ROULETTE (מכונת מזל הקסם!)
 */
@Composable
fun JackpotSlotModal(
    data: ChannelData,
    logo: (Channel) -> String,
    onPlay: (Channel, List<Channel>) -> Unit,
    onClose: () -> Unit,
) {
    BackHandler(onBack = onClose)
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    val pool = remember(data) { data.channels.filter { !it.isRadio && it.kind != "app" && it.url.isNotBlank() } }
    val vibes = remember {
        listOf("🍿 צפיית בינג׳", "🔥 מצב אדרנלין", "🌙 צפיית לילה", "🎉 הפתעת השף", "⚡ שידור חי לוהט", "📺 קלאסיקה")
    }

    var spinning by remember { mutableStateOf(false) }
    var currentPick by remember { mutableStateOf(pool.randomOrNull()) }
    var currentVibe by remember { mutableStateOf(vibes.first()) }
    var won by remember { mutableStateOf(false) }

    fun spinReels() {
        if (spinning || pool.isEmpty()) return
        spinning = true
        won = false
        scope.launch {
            var delayMs = 45L
            repeat(22) { step ->
                currentPick = pool.random()
                currentVibe = vibes.random()
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                delay(delayMs)
                if (step > 12) delayMs += 28L
            }
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            spinning = false
            won = true
        }
    }

    LaunchedEffect(Unit) {
        spinReels()
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0xEB050814))
            .clickable(enabled = false) {}
            .padding(22.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(Brush.verticalGradient(listOf(Color(0xFF171033), Color(0xFF0B0F24))))
                .border(2.dp, Brush.linearGradient(listOf(Color(0xFFF59E0B), Color(0xFFEC4899), Color(0xFF38BDF8))), RoundedCornerShape(28.dp))
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("🎰 מכונת המזל של ישראל TV+", color = Color(0xFFFBBF24), fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                Box(
                    Modifier.size(34.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.1f)).clickable { onClose() },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Rounded.Close, contentDescription = "סגירה", tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }

            Text(
                if (spinning) "מסובב את גלגלי השידור..." else "🎉 ג׳קפוט! מצאנו לך את השידור המושלם:",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
            )

            // 3 Slot Windows
            val pick = currentPick
            if (pick != null) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.Black.copy(alpha = 0.55f))
                        .border(1.5.dp, if (won) Color(0xFFFACC15) else Color.White.copy(alpha = 0.18f), RoundedCornerShape(20.dp))
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    ChannelLogo(logo(pick), Modifier.size(78.dp), true)
                    Text(
                        pick.name,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SlotPill(pick.category.substringBefore(" ("), Color(0xFF38BDF8))
                        SlotPill(currentVibe, Color(0xFFF472B6))
                    }
                }

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = { spinReels() },
                        enabled = !spinning,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1f).height(50.dp),
                    ) {
                        Text("🎰 סובב שוב!", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { onPlay(pick, pool) },
                        enabled = !spinning,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1.3f).height(50.dp),
                    ) {
                        Icon(Icons.Rounded.PlayArrow, contentDescription = null, tint = Color.Black)
                        Spacer(Modifier.width(4.dp))
                        Text("שגר לצפייה!", color = Color.Black, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
        }
    }
}

@Composable
private fun SlotPill(text: String, color: Color) {
    Box(
        Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(color.copy(alpha = 0.18f))
            .border(1.dp, color.copy(alpha = 0.55f), RoundedCornerShape(999.dp))
            .padding(horizontal = 11.dp, vertical = 5.dp),
    ) {
        Text(text, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
    }
}

/**
 * 5. ISRAELI TV TIME MACHINE (מכונת הזמן של הטלוויזיה הישראלית — שנות ה-70 עד 2026!)
 */
private data class TvEra(
    val id: String,
    val decade: String,
    val title: String,
    val subtitle: String,
    val accent: Color,
    val keywords: List<String>,
)

private val TV_ERAS = listOf(
    TvEra(
        "70s", "1970s", "🎞️ שנות ה-70 · שחור-לבן ובורקס",
        "עידן הערוץ היחיד, גבעת חלפון, חגיגה בסנוקר וצ׳רלי וחצי",
        Color(0xFFF59E0B),
        listOf("בורקס", "קולנוע ישראלי", "זהו זה", "הצבי", "קלאסי"),
    ),
    TvEra(
        "80s", "1980s", "📼 שנות ה-80 · קלטות VHS ומיתולוגיה",
        "זהו זה!, אנטי-מחיקון, סרטי קאלט ומוזיקה נוסטלגית",
        Color(0xFFEC4899),
        listOf("זהו זה", "בורקס", "שעת נעילה", "המזח", "חינוכית", "מוזיקה"),
    ),
    TvEra(
        "90s", "1990s", "📺 שנות ה-90 · תור הזהב של ערוץ 2",
        "החמישיה הקאמרית, מגרש הרוסים, קומדיות וסאטירה חדה",
        Color(0xFF38BDF8),
        listOf("החמישיה", "מגרש הרוסים", "קשת", "רשת", "קומדי", "ספורט"),
    ),
    TvEra(
        "2000s", "2000s", "💿 שנות ה-2000 · מהפכת הקאלט",
        "ארץ נהדרת, סברי מרנן, הבורר ודרמות המקור הגדולות",
        Color(0xFF10B981),
        listOf("ארץ נהדרת", "סברי מרנן", "התנתקות", "בואו לאכול", "HOT", "yes"),
    ),
    TvEra(
        "2020s", "2020s", "🚀 שנות ה-2020 · תור הזהב החדש",
        "קופה ראשית, מנאייכ, טהרן, המפקדת, איסט סייד וכראמל",
        Color(0xFFA855F7),
        listOf("קופה ראשית", "מנאייכ", "טהרן", "המפקדת", "כראמל", "איסט סייד", "חוליגנים", "החמאם"),
    ),
)

@Composable
fun TimeMachineScreen(
    data: ChannelData,
    logo: (Channel) -> String,
    onPlay: (Channel, List<Channel>) -> Unit,
    onClose: () -> Unit,
) {
    BackHandler(onBack = onClose)
    var selectedEraIdx by remember { mutableIntStateOf(0) }
    val era = TV_ERAS[selectedEraIdx]

    val matchingItems = remember(era, data) {
        val vodChannels = data.vod.mapNotNull { series ->
            val firstEp = series.episodes.firstOrNull() ?: return@mapNotNull null
            if (era.keywords.any { kw -> series.title.contains(kw, true) || series.desc.contains(kw, true) }) {
                firstEp.toChannel(series).copy(name = "🎬 ${series.title}")
            } else null
        }
        val liveMatches = data.channels.filter { ch ->
            !ch.isRadio && ch.kind != "app" && era.keywords.any { kw -> ch.name.contains(kw, true) || ch.desc.contains(kw, true) }
        }
        (vodChannels + liveMatches).distinctBy { it.id }.ifEmpty { data.featured }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF090518), Color(0xFF120B29), Color(0xFF040711))))
            .windowInsetsPadding(WindowInsets.statusBars)
            .navigationBarsPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier.size(40.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.1f)).clickable { onClose() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Rounded.Close, contentDescription = "סגירה", tint = Color.White)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🕰️ מכונת הזמן של הטלוויזיה", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                Text("סובבו את מחוג הזמן בין עשורי השידור בישראל", color = era.accent, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.size(40.dp))
        }

        // Decade Dial Pills
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(TV_ERAS.size) { idx ->
                val item = TV_ERAS[idx]
                val sel = idx == selectedEraIdx
                Box(
                    Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (sel) item.accent.copy(alpha = 0.28f) else Color.White.copy(alpha = 0.07f))
                        .border(1.5.dp, if (sel) item.accent else Color.White.copy(alpha = 0.14f), RoundedCornerShape(16.dp))
                        .clickable { selectedEraIdx = idx }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                ) {
                    Text(item.decade, color = if (sel) item.accent else Color.White, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                }
            }
        }

        // Era Portal Banner
        Column(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Brush.linearGradient(listOf(era.accent.copy(alpha = 0.25f), Color.Black.copy(alpha = 0.65f))))
                .border(1.5.dp, era.accent.copy(alpha = 0.6f), RoundedCornerShape(24.dp))
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(era.title, color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.ExtraBold)
            Text(era.subtitle, color = Color.White.copy(alpha = 0.84f), fontSize = 13.sp)
        }

        Text("📡 שידורים וסדרות מהתקופה (${matchingItems.size}):", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)

        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(matchingItems.take(24), key = { it.id }) { ch ->
                Column(
                    Modifier
                        .width(156.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .border(1.dp, era.accent.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
                        .clickable { onPlay(ch, matchingItems) }
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    ChannelLogo(logo(ch), Modifier.size(64.dp), true)
                    Text(ch.name, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, maxLines = 2, textAlign = TextAlign.Center)
                    Text("▶ שגר לתקופה זו", color = era.accent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * 6. LIVE TV & ISRAELI CULTURE TRIVIA CHALLENGE (טריוויה TV להפסקת פרסומות ולסלון!)
 */
data class TvTriviaQuestion(
    val q: String,
    val options: List<String>,
    val answerIdx: Int,
)

val ISRAELI_TV_TRIVIA_BANK = listOf(
    TvTriviaQuestion("איך קוראים לסופרמרקט המיתולוגי בסדרה 'קופה ראשית'?", listOf("שפע יששכר", "זול ובגדול", "סופר שמש", "מחסני כוכבה"), 0),
    TvTriviaQuestion("מי מגלם את ראמזי עבד ראמזי ב'קופה ראשית'?", listOf("אמיר שורוש", "יניב סוויסה", "דניאל סטיופין", "יעקב בודו"), 0),
    TvTriviaQuestion("באיזו עיר מתרחשת עלילת 'קופה ראשית'?", listOf("יבנה", "ראשון לציון", "פתח תקווה", "חולון"), 0),
    TvTriviaQuestion("מה שם סוכנת המוסד הראשית בסדרה 'טהרן'?", listOf("תמר רביניאן", "נועה הולנדר", "יעל כץ", "דנה לוי"), 0),
    TvTriviaQuestion("מה תפקידו של איזי בכר בסדרה 'מנאייכ'?", listOf("חוקר מח״ש", "מפקד תחנה", "שופט מחוזי", "עיתונאי חוקר"), 0),
    TvTriviaQuestion("איזה חתול קסום ומדבר עומד במרכז סדרת הלהיט של כאן חינוכית?", listOf("כראמל", "שקשוקה", "מיצי", "גארפילד"), 0),
    TvTriviaQuestion("איזו שלישייה מיתולוגית כיכבה בסרט 'גבעת חלפון אינה עונה'?", listOf("הגשש החיוור", "זהו זה", "שלישיית מה קשור", "החמישיה הקאמרית"), 0),
    TvTriviaQuestion("באיזה בסיס טירונים מתרחשת הסדרה 'המפקדת'?", listOf("חוות השומר", "מחנה 80", "זיקים", "ניצנים"), 0),
    TvTriviaQuestion("אילו שתי משפחות נפגשות לארוחות שישי בסדרה 'סברי מרנן'?", listOf("חסון ורוזן", "כהן ולוי", "מזרחי ושפירא", "אבוטבול וברק"), 0),
    TvTriviaQuestion("מי מקריין בהומור מושחז את התוכנית 'בואו לאכול איתי'?", listOf("שי אביבי", "איל קיציס", "ליאור שליין", "דרור קרן"), 0),
    TvTriviaQuestion("על איזו מלחמה מבוססות הסדרות 'שעת נעילה' ו'המזח'?", listOf("מלחמת יום הכיפורים", "מלחמת ששת הימים", "מלחמת לבנון הראשונה", "מבצע קדש"), 0),
    TvTriviaQuestion("באיזה אפיק משודר תאגיד השידור הישראלי 'כאן 11'?", listOf("ערוץ 11", "ערוץ 1", "ערוץ 22", "ערוץ 33"), 0),
    TvTriviaQuestion("כמה זוכים בלוטו משתתפים בסדנה בסדרה 'שישה אפסים'?", listOf("6", "5", "7", "10"), 0),
    TvTriviaQuestion("באיזו שכונה בירושלים מתרחשת עלילת הסדרה 'איסט סייד' בכיכובו של יהודה לוי?", listOf("מזרח ירושלים / עיר דוד", "מאה שערים", "רחביה", "עין כרם"), 0),
    TvTriviaQuestion("איזה שחקן מגלם את דורון קביליו בסדרה 'פאודה'?", listOf("ליאור רז", "צחי הלוי", "עידן עמדי", "איציק כהן"), 0),
    TvTriviaQuestion("איזו תוכנית סאטירה ישראלית משודרת בקשת מאז שנת 2003?", listOf("ארץ נהדרת", "מצב האומה", "גב האומה", "מועדון לילה"), 0),
)

@Composable
fun TvTriviaOverlayCard(
    compact: Boolean = false,
    onClose: () -> Unit,
) {
    BackHandler(onBack = onClose)
    val haptic = LocalHapticFeedback.current
    val questions = remember { ISRAELI_TV_TRIVIA_BANK.shuffled() }
    var qIdx by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var streak by remember { mutableIntStateOf(0) }
    var pickedOption by remember { mutableIntStateOf(-1) }

    val cur = questions[qIdx % questions.size]
    val shuffledOpts = remember(qIdx) {
        cur.options.mapIndexed { i, txt -> i to txt }.shuffled()
    }

    val rank = when {
        score >= 100 -> "👑 אגדת טלוויזיה"
        score >= 50 -> "🔥 אלוף השלט"
        score >= 20 -> "📺 חובב שידורים"
        else -> "🎬 צופה מתחיל"
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(if (compact) Color.Black.copy(alpha = 0.45f) else Color(0xEB040814))
            .padding(18.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(26.dp))
                .background(Brush.verticalGradient(listOf(Color(0xE60D1B38), Color(0xF2070D1E))))
                .border(1.5.dp, Color(0xFF38BDF8).copy(alpha = 0.65f), RoundedCornerShape(26.dp))
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("🧠 טריוויה TV ישראלית", color = Color(0xFF38BDF8), fontSize = 17.sp, fontWeight = FontWeight.ExtraBold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("⭐ $score · 🔥 $streak", color = Color(0xFFFACC15), fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                    Box(
                        Modifier.size(32.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.12f)).clickable { onClose() },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Rounded.Close, contentDescription = "סגירה", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Text("דרגה נוכחית: $rank", color = Color(0xFF94A3B8), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)

            Text(
                cur.q,
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 4.dp),
            )

            shuffledOpts.forEach { (origIdx, label) ->
                val revealed = pickedOption != -1
                val isCorrect = origIdx == cur.answerIdx
                val isChosen = pickedOption == origIdx
                val bg = when {
                    !revealed -> Color.White.copy(alpha = 0.08f)
                    isCorrect -> Color(0xFF16A34A).copy(alpha = 0.45f)
                    isChosen -> Color(0xFFDC2626).copy(alpha = 0.45f)
                    else -> Color.White.copy(alpha = 0.04f)
                }
                val borderCol = when {
                    !revealed -> Color.White.copy(alpha = 0.16f)
                    isCorrect -> Color(0xFF4ADE80)
                    isChosen -> Color(0xFFF87171)
                    else -> Color.Transparent
                }
                Box(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(bg)
                        .border(1.2.dp, borderCol, RoundedCornerShape(14.dp))
                        .clickable(enabled = !revealed) {
                            pickedOption = origIdx
                            if (isCorrect) {
                                score += 10 + streak * 2
                                streak++
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            } else {
                                streak = 0
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }
                        }
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                ) {
                    Text(label, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            if (pickedOption != -1) {
                Button(
                    onClick = {
                        pickedOption = -1
                        qIdx++
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0EA5E9)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().height(46.dp),
                ) {
                    Text("שאלה הבאה ⬅️", color = Color.White, fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }
}

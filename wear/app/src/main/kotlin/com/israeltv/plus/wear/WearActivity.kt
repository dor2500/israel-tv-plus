package com.israeltv.plus.wear

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.compose.PlayerSurface
import androidx.media3.ui.compose.SURFACE_TYPE_SURFACE_VIEW
import androidx.media3.ui.compose.modifiers.resizeWithContentScale
import androidx.media3.ui.compose.state.rememberPresentationState
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.CircularProgressIndicator
import androidx.wear.compose.material3.ColorScheme
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import coil3.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val Accent = Color(0xFF4D8DFF)
private val LiveRed = Color(0xFFFF3B30)

class WearActivity : ComponentActivity() {
    private lateinit var store: ChannelStore
    private lateinit var library: Library
    /** Opened from the watch's media controls: straight to the player. */
    private val openPlayer = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        store = ChannelStore(applicationContext)
        library = Library(applicationContext)
        openPlayer.value = savedInstanceState == null && intent.getBooleanExtra(EXTRA_PLAYER, false)
        lifecycleScope.launch {
            store.load()
            store.refresh()
        }
        setContent { WearApp(store, library, openPlayer.value) { openPlayer.value = false } }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.getBooleanExtra(EXTRA_PLAYER, false)) openPlayer.value = true
    }

    companion object {
        const val EXTRA_PLAYER = "player"
    }
}

@Composable
fun WearApp(store: ChannelStore, library: Library, openPlayer: Boolean, onPlayerOpened: () -> Unit) {
    val data by store.state.collectAsState()
    val nav = rememberSwipeDismissableNavController()
    // a shelf key is a category name: encoded, so a "/" or "?" in a name never breaks the route
    val route = { base: String, key: String -> base + Uri.encode(key) }
    LaunchedEffect(openPlayer, data != null) {
        if (openPlayer && data != null) {
            if (Playback.current.value != null) nav.navigate("player/recent") { launchSingleTop = true }
            onPlayerOpened()
        }
    }
    MaterialTheme(colorScheme = ColorScheme(primary = Accent)) {
        // the app is Hebrew: right-to-left on every watch, whatever its language
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            AppScaffold {
                val d = data
                if (d == null) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                } else {
                    SwipeDismissableNavHost(navController = nav, startDestination = "home") {
                        composable("home") {
                            HomeScreen(d, library, open = { nav.navigate(route("shelf/", it)) }, play = { ch, key ->
                                startChannel(nav.context, library, ch); nav.navigate(route("player/", key))
                            }, nowPlaying = { nav.navigate("player/recent") { launchSingleTop = true } })
                        }
                        composable("shelf/{key}") { entry ->
                            val key = entry.arguments?.getString("key").orEmpty()
                            if (key == "all") CategoriesScreen(d) { nav.navigate(route("shelf/", it)) }
                            else ChannelListScreen(d, library, key) { ch ->
                                startChannel(nav.context, library, ch); nav.navigate(route("player/", key)) { launchSingleTop = true }
                            }
                        }
                        composable("player/{key}") { entry ->
                            val key = entry.arguments?.getString("key").orEmpty()
                            // no clock over the picture: the player draws its own LIVE line at the top
                            ScreenScaffold(timeText = {}) { _ -> PlayerScreen(d, library, key) }
                        }
                    }
                }
            }
        }
    }
}

private fun startChannel(context: Context, library: Library, ch: Channel) {
    library.addRecent(ch.id)
    Playback.play(context, ch)
}

/** The channels a shelf key stands for (also the recents and favourites, which change while the app runs). */
private fun shelfChannels(d: ChannelData, library: Library, key: String): List<Channel> = when (key) {
    "recent" -> library.recents.value.mapNotNull { d.byId[it] }
    "favorites" -> library.favorites.value.mapNotNull { d.byId[it] }
    else -> d.shelf(key)?.channels.orEmpty()
}

// ---------------------------------------------------------------- home: what's on, recents, the shelves

@Composable
private fun HomeScreen(d: ChannelData, library: Library, open: (String) -> Unit, play: (Channel, String) -> Unit, nowPlaying: () -> Unit) {
    val state = rememberScalingLazyListState()
    val current by Playback.current.collectAsState()
    val recents by library.recents.collectAsState()
    val favorites by library.favorites.collectAsState()
    ScreenScaffold(scrollState = state) { padding ->
        ScalingLazyColumn(state = state, contentPadding = padding, modifier = Modifier.fillMaxSize()) {
            item { ListHeader { Text("ישראל TV+", fontWeight = FontWeight.Bold) } }
            current?.let { ch ->
                item {
                    Button(onClick = nowPlaying, modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Accent),
                        icon = { Logo(ch, 28) },
                        secondaryLabel = { Text("מתנגן עכשיו", maxLines = 1) },
                        label = { Text(ch.name, maxLines = 1, overflow = TextOverflow.Ellipsis) })
                }
            }
            val recent = recents.mapNotNull { d.byId[it] }.filter { it.id != current?.id }.take(2)
            items(recent) { ch ->
                ChannelButton(ch, favorite = ch.id in favorites, secondary = "המשך · " + ch.subtitle,
                    onClick = { play(ch, "recent") }, onLong = { ctx -> toggleFavorite(ctx, ch, library) })
            }
            item { ListHeader { Text("ערוצים") } }
            items(d.shelves) { s -> ShelfButton(s.icon, s.name, s.channels.size) { open(s.key) } }
            if (favorites.isNotEmpty()) item { ShelfButton("⭐", "מועדפים", favorites.size) { open("favorites") } }
            item { ShelfButton("🌍", "כל הקטגוריות", d.categories.size) { open("all") } }
            item {
                Text("לחיצה ארוכה על ערוץ = מועדף", fontSize = 11.sp, color = Color.White.copy(alpha = .55f),
                    textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(top = 6.dp))
            }
        }
    }
}

@Composable
private fun ShelfButton(icon: String, name: String, count: Int, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.filledTonalButtonColors(),
        icon = { Text(icon, fontSize = 18.sp) },
        secondaryLabel = { Text("$count ערוצים", maxLines = 1) },
        label = { Text(name, maxLines = 1, overflow = TextOverflow.Ellipsis) })
}

@Composable
private fun CategoriesScreen(d: ChannelData, open: (String) -> Unit) {
    val state = rememberScalingLazyListState()
    ScreenScaffold(scrollState = state) { padding ->
        ScalingLazyColumn(state = state, contentPadding = padding, modifier = Modifier.fillMaxSize()) {
            item { ListHeader { Text("כל הקטגוריות") } }
            items(d.categories) { s -> ShelfButton("", s.name, s.channels.size) { open(s.key) } }
        }
    }
}

// ---------------------------------------------------------------- a list of channels

@Composable
private fun ChannelListScreen(d: ChannelData, library: Library, key: String, play: (Channel) -> Unit) {
    val state = rememberScalingLazyListState()
    val favorites by library.favorites.collectAsState()
    val title = when (key) { "recent" -> "צפית לאחרונה"; "favorites" -> "מועדפים"; else -> d.shelf(key)?.name.orEmpty() }
    val list = remember(key, favorites) { shelfChannels(d, library, key) }
    ScreenScaffold(scrollState = state) { padding ->
        ScalingLazyColumn(state = state, contentPadding = padding, modifier = Modifier.fillMaxSize()) {
            item { ListHeader { Text(title, maxLines = 1, overflow = TextOverflow.Ellipsis) } }
            if (list.isEmpty()) item {
                Text("אין כאן ערוצים עדיין", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            }
            items(list, key = { it.id }) { ch ->
                ChannelButton(ch, favorite = ch.id in favorites, secondary = "#" + ch.number + " · " + ch.subtitle,
                    onClick = { play(ch) }, onLong = { ctx -> toggleFavorite(ctx, ch, library) })
            }
        }
    }
}

@Composable
private fun ChannelButton(ch: Channel, favorite: Boolean, secondary: String, onClick: () -> Unit, onLong: (Context) -> Unit) {
    val context = LocalContext.current
    Button(onClick = onClick, onLongClick = { onLong(context) }, onLongClickLabel = "מועדף",
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.filledTonalButtonColors(),
        icon = { Logo(ch, 30) },
        secondaryLabel = { Text(secondary, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        label = { Text((if (favorite) "★ " else "") + ch.name, maxLines = 1, overflow = TextOverflow.Ellipsis) })
}

private fun toggleFavorite(context: Context, ch: Channel, library: Library) {
    val added = library.toggleFavorite(ch.id)
    Toast.makeText(context, if (added) "⭐ נוסף למועדפים" else "הוסר מהמועדפים", Toast.LENGTH_SHORT).show()
}

@Composable
private fun Logo(ch: Channel, size: Int) {
    Box(Modifier.size(size.dp).clip(RoundedCornerShape(6.dp)).background(Color.White.copy(alpha = .1f)),
        contentAlignment = Alignment.Center) {
        if (ch.logoUrl.isNotEmpty()) {
            AsyncImage(model = ch.logoUrl, contentDescription = null, contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize().padding(2.dp))
        } else {
            Text(ch.name.take(2), fontSize = (size / 3).sp, fontWeight = FontWeight.Bold)
        }
    }
}

// ---------------------------------------------------------------- the player: round video or a radio face

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
private fun PlayerScreen(d: ChannelData, library: Library, key: String) {
    val context = LocalContext.current
    val view = LocalView.current
    val player = remember { Playback.player(context) }
    val current by Playback.current.collectAsState()
    val error by Playback.error.collectAsState()
    var playing by remember { mutableStateOf(player.isPlaying) }
    var buffering by remember { mutableStateOf(player.playbackState == Player.STATE_BUFFERING) }
    var controls by remember { mutableStateOf(true) }
    var touched by remember { mutableIntStateOf(0) }
    var crop by remember { mutableStateOf(false) }
    val ch = current
    val video = ch != null && !ch.isRadio

    DisposableEffect(player) {
        val l = object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) { playing = isPlaying }
            override fun onPlaybackStateChanged(state: Int) { buffering = state == Player.STATE_BUFFERING }
        }
        player.addListener(l)
        onDispose { player.removeListener(l) }
    }
    // a watched picture keeps the screen on; radio lets it sleep (it plays on in the background)
    DisposableEffect(video) {
        view.keepScreenOn = video
        onDispose { view.keepScreenOn = false }
    }
    // over a picture the controls fade away after a few seconds; a tap brings them back
    LaunchedEffect(touched, video, playing) {
        controls = true
        if (video && playing) { delay(4000); controls = false }
    }

    val audio = remember { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager }
    val focus = remember { FocusRequester() }
    var crown by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) { runCatching { focus.requestFocus() } }

    Box(
        Modifier.fillMaxSize().background(Color.Black)
            // the crown changes the watch's media volume (the channel itself always plays at full volume)
            .onRotaryScrollEvent {
                crown += it.verticalScrollPixels
                if (kotlin.math.abs(crown) > 40f) {
                    audio.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                        if (crown > 0) AudioManager.ADJUST_RAISE else AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI)
                    crown = 0f
                }
                true
            }
            .focusRequester(focus).focusable()
            .clickable { touched++ },
        contentAlignment = Alignment.Center,
    ) {
        if (ch == null) {
            Text("לא מתנגן כלום", textAlign = TextAlign.Center)
            return@Box
        }
        if (!ch.isRadio) {
            val presentation = rememberPresentationState(player)
            PlayerSurface(player = player, surfaceType = SURFACE_TYPE_SURFACE_VIEW,
                modifier = Modifier.resizeWithContentScale(if (crop) ContentScale.Crop else ContentScale.Fit, presentation.videoSizeDp))
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(bottom = 40.dp)) {
                Logo(ch, 64)
            }
        }
        if (buffering && error == null) CircularProgressIndicator(modifier = Modifier.size(48.dp))

        if (controls || ch.isRadio || error != null) {
            Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = if (ch.isRadio) 0f else .45f)))
            Column(Modifier.fillMaxSize().padding(horizontal = 22.dp, vertical = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceBetween) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(7.dp).clip(CircleShape).background(LiveRed))
                        Spacer(Modifier.width(4.dp))
                        Text("LIVE", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Text(ch.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center)
                    error?.let {
                        Text(it, fontSize = 11.sp, color = LiveRed, textAlign = TextAlign.Center)
                    }
                }
                val list = remember(key) { shelfChannels(d, library, key).ifEmpty { listOf(ch) } }
                val zap = { step: Int ->
                    val i = list.indexOfFirst { it.id == ch.id }
                    val next = list[((if (i == -1) 0 else i) + step + list.size) % list.size]
                    startChannel(context, library, next)
                    touched++
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    RoundIcon("⏭", 38) { zap(1) }          // RTL: the next channel is on the right
                    Spacer(Modifier.width(8.dp))
                    RoundIcon(if (error != null) "⟳" else if (playing) "⏸" else "▶", 52, Accent) {
                        if (error != null) Playback.retry(context)
                        else if (player.isPlaying) player.pause()
                        else { player.volume = 1f; if (player.playbackState == Player.STATE_IDLE) player.prepare(); player.play() }
                        touched++
                    }
                    Spacer(Modifier.width(8.dp))
                    RoundIcon("⏮", 38) { zap(-1) }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RoundIcon("－", 30) {
                        audio.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI); touched++
                    }
                    Spacer(Modifier.width(6.dp))
                    if (!ch.isRadio) {
                        RoundIcon(if (crop) "▣" else "⛶", 30) { crop = !crop; touched++ }
                        Spacer(Modifier.width(6.dp))
                    }
                    RoundIcon("＋", 30) {
                        audio.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI); touched++
                    }
                }
            }
        }
    }
}

@Composable
private fun RoundIcon(glyph: String, size: Int, color: Color = Color.White.copy(alpha = .18f), onClick: () -> Unit) {
    Box(Modifier.size(size.dp).clip(CircleShape).background(color).clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        Text(glyph, fontSize = (size * 0.42f).sp, color = Color.White, textAlign = TextAlign.Center)
    }
}

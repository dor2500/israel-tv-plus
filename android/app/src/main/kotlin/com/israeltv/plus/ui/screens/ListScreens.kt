package com.israeltv.plus.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.israeltv.plus.data.AppPrefs
import com.israeltv.plus.data.Channel
import com.israeltv.plus.data.ChannelData
import com.israeltv.plus.data.Category
import com.israeltv.plus.ui.components.BoldIcon
import com.israeltv.plus.ui.components.ChannelShelf
import com.israeltv.plus.ui.components.ChannelCard
import com.israeltv.plus.ui.components.SectionHeader
import com.israeltv.plus.ui.components.ShelfSkeleton
import com.israeltv.plus.ui.components.feedPadding
import com.israeltv.plus.ui.components.frostedGlass
import com.israeltv.plus.ui.components.rememberBitChordFlingBehavior
import com.israeltv.plus.ui.theme.LocalDimens

/** A poster grid inside the master LazyColumn: one row item per [columns] channels. */
fun LazyListScope.channelGrid(
    list: List<Channel>,
    keyPrefix: String,
    columns: Int,
    gutter: Dp,
    gap: Dp,
    logo: (Channel) -> String,
    subtitle: (Channel) -> String,
    onPlay: (Channel) -> Unit,
) {
    items(list.chunked(columns), key = { keyPrefix + it.first().id }) { row ->
        Row(
            Modifier.fillMaxWidth().padding(horizontal = gutter).padding(bottom = gap * 2),
            horizontalArrangement = Arrangement.spacedBy(gap),
        ) {
            row.forEach { ch -> ChannelCard(ch, logo(ch), subtitle(ch), { onPlay(ch) }, Modifier.weight(1f), sharedKey = keyPrefix + ch.id) }
            repeat(columns - row.size) { Spacer(Modifier.weight(1f)) }
        }
    }
}

@Composable
private fun EmptyState(text: String) {
    Text(
        text,
        Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 48.dp),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 15.sp,
        fontWeight = FontWeight.Light,
    )
}

/**
 * Channels page, under the pinned macro-categories and "רק ישראלי" switch: a vertical list of categories, each a
 * horizontal row of posters with a "הכל" action. Picking a category (either way) shows its full poster grid.
 */
@Composable
fun ChannelsScreen(
    data: ChannelData?,
    macroCategory: String?,
    israeliOnly: Boolean,
    onIsraeliOnlyChange: (Boolean) -> Unit,
    onSwitchPositioned: (Offset) -> Unit,
    logo: (Channel) -> String,
    subtitle: (Channel) -> String,
    onPlay: (Channel, List<Channel>) -> Unit,
    onViewAll: (String) -> Unit,
    listState: LazyListState = rememberLazyListState(),
) {
    val scheme = MaterialTheme.colorScheme
    val d = LocalDimens.current
    val basePadding = feedPadding(withCategories = true)
    val topInset = basePadding.calculateTopPadding()
    val bottomInset = basePadding.calculateBottomPadding()
    val pinnedSwitchHeight = 52.dp

    var shownCategory by remember { mutableStateOf(macroCategory) }
    var shownIsraeli by remember { mutableStateOf(israeliOnly) }
    LaunchedEffect(macroCategory, israeliOnly) {
        if (macroCategory != shownCategory || israeliOnly != shownIsraeli) {
            shownCategory = macroCategory
            shownIsraeli = israeliOnly
            listState.scrollToItem(0)
        }
    }

    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            Modifier.fillMaxSize(),
            state = listState,
            contentPadding = PaddingValues(top = topInset + pinnedSwitchHeight, bottom = bottomInset),
            flingBehavior = rememberBitChordFlingBehavior(),
        ) {
            if (data == null) {
                items(4) { ShelfSkeleton() }
                return@LazyColumn
            }
            val israeli: (Channel) -> Boolean = { it.categoryEn == "israel" || it.countryHe == "ישראל" }
            if (macroCategory == null) {
                val shelves = data.categories
                    .map { c -> if (israeliOnly) Category(c.key, c.name, c.channels.filter(israeli)) else c }
                    .filter { it.channels.isNotEmpty() }
                if (shelves.isEmpty()) item(key = "empty") { EmptyState("עוד אין פה כלום...") }
                items(shelves, key = { "cat:" + it.key }, contentType = { "shelf" }) { c ->
                    ChannelShelf(
                        c.name, c.channels.take(24), logo, subtitle, { onPlay(it, c.channels) },
                        count = c.channels.size, onSeeAll = { onViewAll(c.name) },
                    )
                }
                return@LazyColumn
            }
            val categoryChannels = data.categories.firstOrNull { it.name == macroCategory || it.key == macroCategory }?.channels.orEmpty()
            val list = if (israeliOnly) categoryChannels.filter(israeli) else categoryChannels
            item(key = "count") {
                SectionHeader(macroCategory)
                Text(
                    "${list.size} ערוצים",
                    Modifier.padding(start = d.gutter, bottom = 16.dp),
                    color = scheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            if (list.isEmpty()) {
                item(key = "empty") {
                    EmptyState("עוד אין פה כלום...")
                }
            } else {
                channelGrid(list, "c:", d.gridColumns, d.gutter, d.cardGap, logo, subtitle) { onPlay(it, list) }
            }
        }

        // Pinned "רק ישראלי" FilterChip right below the pinned macro-categories header with glass blur
        val switchGradientMask = remember {
            Brush.verticalGradient(
                0f to Color.Black,
                0.76f to Color.Black.copy(alpha = 0.72f),
                1f to Color.Transparent,
            )
        }
        Box(
            Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(top = topInset - 4.dp)
                .zIndex(5f)
                .frostedGlass(
                    shape = RectangleShape,
                    tint = Color.Black.copy(alpha = 0.14f),
                    blur = 38.dp,
                    border = false,
                    mask = switchGradientMask,
                )
                .padding(top = 2.dp, bottom = 8.dp),
            contentAlignment = Alignment.Center,
        ) {
            IsraeliOnlySwitch(
                checked = israeliOnly,
                onCheckedChange = onIsraeliOnlyChange,
                onPositioned = onSwitchPositioned,
            )
        }
    }
}

/** My list: favourites and recently watched. */
@Composable
fun MineScreen(
    data: ChannelData?,
    prefs: AppPrefs,
    logo: (Channel) -> String,
    subtitle: (Channel) -> String,
    onPlay: (Channel, List<Channel>) -> Unit,
) {
    val d = LocalDimens.current
    LazyColumn(Modifier.fillMaxSize(), contentPadding = feedPadding(withCategories = false), flingBehavior = rememberBitChordFlingBehavior()) {
        if (data == null) return@LazyColumn
        val favs = prefs.favorites.mapNotNull { data.byId[it] }
        val recent = prefs.recents.mapNotNull { data.byId[it] }
        item(key = "fh") { SectionHeader("הרשימה שלי") }
        if (favs.isEmpty()) item(key = "fe") { EmptyState("עוד אין פה כלום...") }
        else channelGrid(favs, "f:", d.gridColumns, d.gutter, d.cardGap, logo, subtitle) { onPlay(it, favs) }
        item(key = "rh") { SectionHeader("המשך צפייה") }
        if (recent.isEmpty()) item(key = "re") { EmptyState("עוד אין פה כלום...") }
        else channelGrid(recent, "r:", d.gridColumns, d.gutter, d.cardGap, logo, subtitle) { onPlay(it, recent) }
    }
}

/** Search: a clean overlay; the search pill sits right above the keyboard (imePadding). */
@Composable
fun SearchOverlay(
    data: ChannelData?,
    logo: (Channel) -> String,
    subtitle: (Channel) -> String,
    onPlay: (Channel, List<Channel>) -> Unit,
    onClose: () -> Unit,
) {
    val scheme = MaterialTheme.colorScheme
    val d = LocalDimens.current
    var query by rememberSaveable { mutableStateOf("") }
    val focus = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    LaunchedEffect(Unit) { focus.requestFocus(); keyboard?.show() }
    val q = query.trim()
    val results = remember(q, data) {
        if (data == null || q.isEmpty()) emptyList()
        else data.channels.filter { ch ->
            ch.name.contains(q, true) || ch.title.contains(q, true) || ch.category.contains(q, true) ||
                ch.countryHe.contains(q, true) || ch.number.toString() == q
        }.take(120)
    }
    Box(Modifier.fillMaxSize().background(scheme.surfaceContainerLowest.copy(alpha = 0.97f))) {
        val top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
        LazyColumn(
            Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = top + 18.dp, bottom = 104.dp),
            flingBehavior = rememberBitChordFlingBehavior(),
        ) {
            item(key = "h") {
                Text(
                    if (q.isEmpty()) "חיפוש" else "${results.size} תוצאות",
                    Modifier.padding(horizontal = 18.dp).padding(bottom = 14.dp),
                    fontSize = 25.sp, fontWeight = FontWeight.SemiBold, color = scheme.onSurface,
                )
            }
            if (q.isNotEmpty() && results.isEmpty()) item(key = "e") { EmptyState("עוד אין פה כלום...") }
            channelGrid(results, "s:", d.gridColumns, d.gutter, d.cardGap, logo, subtitle) { focusManager.clearFocus(); onPlay(it, results) }
        }
        Row(
            Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = 20.dp, vertical = 14.dp)
                .fillMaxWidth()
                .height(52.dp)
                .frostedGlass(RoundedCornerShape(50), scheme.surfaceContainerHighest.copy(alpha = 0.9f))
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BoldIcon(Icons.Rounded.Search, null, Modifier.size(20.dp), tint = scheme.onSurfaceVariant, weight = 0.65.dp)
            Box(Modifier.weight(1f).padding(horizontal = 10.dp)) {
                if (query.isEmpty()) Text("חפשו ערוץ, קטגוריה או מספר...", color = scheme.onSurfaceVariant, fontSize = 14.sp, fontWeight = FontWeight.Light)
                BasicTextField(
                    query, { query = it },
                    Modifier.fillMaxWidth().focusRequester(focus),
                    singleLine = true,
                    textStyle = TextStyle(color = scheme.onSurface, fontSize = 15.sp, fontWeight = FontWeight.Medium),
                    cursorBrush = SolidColor(scheme.primary),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                )
            }
            BoldIcon(
                Icons.Rounded.Close, "סגירה",
                Modifier.size(22.dp).clip(RoundedCornerShape(50)).clickable(remember { MutableInteractionSource() }, null) { if (query.isNotEmpty()) query = "" else onClose() },
                tint = scheme.onSurfaceVariant, weight = 0.6.dp,
            )
        }
    }
}

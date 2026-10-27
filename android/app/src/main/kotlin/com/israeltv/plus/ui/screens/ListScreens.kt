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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.israeltv.plus.data.AppPrefs
import com.israeltv.plus.data.Channel
import com.israeltv.plus.data.ChannelData
import com.israeltv.plus.ui.components.BoldIcon
import com.israeltv.plus.ui.components.CategoryNeo3DIcon
import com.israeltv.plus.ui.components.ChannelCard
import com.israeltv.plus.ui.components.SectionHeader
import com.israeltv.plus.ui.components.ShelfSkeleton
import com.israeltv.plus.ui.components.feedPadding
import com.israeltv.plus.ui.components.frostedGlass
import com.israeltv.plus.ui.components.pressPhysics
import com.israeltv.plus.ui.components.rememberBitChordFlingBehavior

/** A two-column grid inside the master LazyColumn: one row item per pair (items(list.chunked(2))). */
fun LazyListScope.channelGrid(
    list: List<Channel>,
    keyPrefix: String,
    logo: (Channel) -> String,
    subtitle: (Channel) -> String,
    onPlay: (Channel) -> Unit,
) {
    items(list.chunked(2), key = { keyPrefix + it.first().id }) { row ->
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            row.forEach { ch -> ChannelCard(ch, logo(ch), subtitle(ch), { onPlay(ch) }, Modifier.weight(1f), sharedKey = keyPrefix + ch.id) }
            if (row.size == 1) Spacer(Modifier.weight(1f))
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

/** Channels: category pills with Neo 3D icons, then every channel of the chosen category as a grid. */
@Composable
fun ChannelsScreen(
    data: ChannelData?,
    category: String?,
    onCategory: (String?) -> Unit,
    logo: (Channel) -> String,
    subtitle: (Channel) -> String,
    onPlay: (Channel, List<Channel>) -> Unit,
    listState: LazyListState = rememberLazyListState(),
) {
    var shown by remember { mutableStateOf(category) }
    LaunchedEffect(category) {
        if (category != shown) {
            shown = category
            listState.scrollToItem(0)
        }
    }
    LazyColumn(Modifier.fillMaxSize(), listState, feedPadding(withCategories = false), flingBehavior = rememberBitChordFlingBehavior()) {
        if (data == null) {
            items(4) { ShelfSkeleton() }
            return@LazyColumn
        }
        val list = data.categories.firstOrNull { it.key == category }?.channels ?: data.channels
        item(key = "chips") { CategoryPills(data, category, onCategory) }
        item(key = "count") {
            SectionHeader(
                data.categories.firstOrNull { it.key == category }?.name ?: "כל הערוצים",
            )
            Text(
                "${list.size} ערוצים",
                Modifier.padding(start = 20.dp, bottom = 14.dp).padding(top = 0.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp,
                fontWeight = FontWeight.Light,
            )
        }
        channelGrid(list, "c:", logo, subtitle) { onPlay(it, list) }
    }
}

@Composable
private fun CategoryPills(data: ChannelData, selected: String?, onSelect: (String?) -> Unit) {
    val scheme = MaterialTheme.colorScheme
    val haptic = LocalHapticFeedback.current
    val entries = listOf<Pair<String?, String>>(null to "הכל") + data.categories.map { it.key to it.name }
    LazyRow(
        Modifier.padding(bottom = 16.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        flingBehavior = rememberBitChordFlingBehavior(),
    ) {
        items(entries, key = { it.first ?: "*" }) { (key, name) ->
            val on = key == selected
            val interaction = remember { MutableInteractionSource() }
            Row(
                Modifier
                    .pressPhysics(interaction, tilt = false, glowShape = RoundedCornerShape(50))
                    .clip(RoundedCornerShape(50))
                    .background(if (on) scheme.secondaryContainer else scheme.surfaceContainerHigh.copy(alpha = 0.7f))
                    .clickable(interaction, null) {
                        haptic.performHapticFeedback(HapticFeedbackType.SegmentTick)
                        onSelect(key)
                    }
                    .padding(start = if (key != null) 10.dp else 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (key != null) {
                    CategoryNeo3DIcon(name, size = 22.dp)
                    Spacer(Modifier.width(8.dp))
                }
                Text(
                    name,
                    color = if (on) scheme.onSecondaryContainer else scheme.onSurfaceVariant,
                    fontSize = 14.sp,
                    fontWeight = if (on) FontWeight.SemiBold else FontWeight.Medium,
                )
            }
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
    LazyColumn(Modifier.fillMaxSize(), contentPadding = feedPadding(withCategories = false), flingBehavior = rememberBitChordFlingBehavior()) {
        if (data == null) return@LazyColumn
        val favs = prefs.favorites.mapNotNull { data.byId[it] }
        val recent = prefs.recents.mapNotNull { data.byId[it] }
        item(key = "fh") { SectionHeader("מועדפים") }
        if (favs.isEmpty()) item(key = "fe") { EmptyState("עוד אין מועדפים. לחצו לחיצה ארוכה על ערוץ כדי להוסיף אותו לכאן.") }
        else channelGrid(favs, "f:", logo, subtitle) { onPlay(it, favs) }
        item(key = "rh") { SectionHeader("צפיתם לאחרונה") }
        if (recent.isEmpty()) item(key = "re") { EmptyState("הערוצים שתצפו בהם יופיעו כאן.") }
        else channelGrid(recent, "r:", logo, subtitle) { onPlay(it, recent) }
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
            contentPadding = PaddingValues(top = top + 20.dp, bottom = 120.dp),
            flingBehavior = rememberBitChordFlingBehavior(),
        ) {
            item(key = "h") {
                Text(
                    if (q.isEmpty()) "חיפוש" else "${results.size} תוצאות",
                    Modifier.padding(horizontal = 20.dp).padding(bottom = 16.dp),
                    fontSize = 30.sp, fontWeight = FontWeight.SemiBold, color = scheme.onSurface,
                )
            }
            if (q.isNotEmpty() && results.isEmpty()) item(key = "e") { EmptyState("לא נמצא ערוץ בשם \"$q\"") }
            channelGrid(results, "s:", logo, subtitle) { focusManager.clearFocus(); onPlay(it, results) }
        }
        Row(
            Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .fillMaxWidth()
                .height(60.dp)
                .frostedGlass(RoundedCornerShape(50), scheme.surfaceContainerHighest.copy(alpha = 0.9f))
                .padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BoldIcon(Icons.Rounded.Search, null, Modifier.size(24.dp), tint = scheme.onSurfaceVariant, weight = 0.65.dp)
            Box(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                if (query.isEmpty()) Text("שם ערוץ, מדינה, קטגוריה או מספר", color = scheme.onSurfaceVariant, fontSize = 16.sp, fontWeight = FontWeight.Light)
                BasicTextField(
                    query, { query = it },
                    Modifier.fillMaxWidth().focusRequester(focus),
                    singleLine = true,
                    textStyle = TextStyle(color = scheme.onSurface, fontSize = 17.sp, fontWeight = FontWeight.Medium),
                    cursorBrush = SolidColor(scheme.primary),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                )
            }
            BoldIcon(
                Icons.Rounded.Close, "סגירה",
                Modifier.size(26.dp).clip(RoundedCornerShape(50)).clickable(remember { MutableInteractionSource() }, null) { if (query.isNotEmpty()) query = "" else onClose() },
                tint = scheme.onSurfaceVariant, weight = 0.6.dp,
            )
        }
    }
}

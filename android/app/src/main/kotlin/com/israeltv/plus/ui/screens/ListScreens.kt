package com.israeltv.plus.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import com.israeltv.plus.data.isIsraeli
import com.israeltv.plus.ui.components.AppleIsraelFlag
import com.israeltv.plus.ui.components.tactileBounce
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
import androidx.compose.foundation.border
import androidx.compose.ui.graphics.Color
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
import com.israeltv.plus.ui.components.ChannelCard
import com.israeltv.plus.ui.components.SectionHeader
import com.israeltv.plus.ui.components.ShelfSkeleton
import com.israeltv.plus.ui.components.feedPadding
import com.israeltv.plus.ui.components.frostedGlass
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
    )
}

/** Channels: macro-category pills, "עוד..." sheet, "רק ישראלי" toggle, and channel grid. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChannelsScreen(
    data: ChannelData?,
    category: String?,
    onCategory: (String?) -> Unit,
    logo: (Channel) -> String,
    subtitle: (Channel) -> String,
    onPlay: (Channel, List<Channel>) -> Unit,
    listState: LazyListState = rememberLazyListState(),
    israeliOnly: Boolean = false,
    onToggleIsraeli: () -> Unit = {},
) {
    // a new category starts at the top; coming back from the player keeps the place
    var shown by remember { mutableStateOf(category) }
    var moreSheetOpen by remember { mutableStateOf(false) }

    LaunchedEffect(category) {
        if (category != shown) {
            shown = category
            listState.scrollToItem(0)
        }
    }

    LazyColumn(Modifier.fillMaxSize(), listState, feedPadding(), flingBehavior = rememberBitChordFlingBehavior()) {
        if (data == null) {
            items(4) { ShelfSkeleton() }
            return@LazyColumn
        }
        val isAll = category == null || category == "all"
        val rawList = if (isAll) data.channels else data.categories.firstOrNull { it.key == category }?.channels ?: data.channels
        val list = if (israeliOnly) rawList.filter { it.isIsraeli() } else rawList

        item(key = "chips") {
            CategoryMacroBar(
                data = data,
                selected = category,
                israeliOnly = israeliOnly,
                onSelect = onCategory,
                onMore = { moreSheetOpen = true },
                onToggleIsraeli = onToggleIsraeli,
            )
        }
        item(key = "count") {
            val title = if (isAll) "כל הערוצים (הכל)" else data.categories.firstOrNull { it.key == category }?.name ?: "כל הערוצים"
            val displayTitle = if (israeliOnly) "$title · 100% ישראלי" else title
            SectionHeader(displayTitle)
            Text(
                "${list.size} ערוצים ותחנות",
                Modifier.padding(start = 20.dp, bottom = 14.dp).padding(top = 0.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp,
            )
        }
        channelGrid(list, "c:", logo, subtitle) { onPlay(it, list) }
    }

    if (moreSheetOpen && data != null) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val haptic = LocalHapticFeedback.current
        val scheme = MaterialTheme.colorScheme

        ModalBottomSheet(
            onDismissRequest = { moreSheetOpen = false },
            sheetState = sheetState,
            containerColor = scheme.surfaceContainerHigh,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 36.dp)
            ) {
                Text(
                    text = "עוד קטגוריות",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = scheme.onSurface,
                    modifier = Modifier.padding(bottom = 16.dp),
                )

                // Remaining niche categories in neat FlowRow
                val macroKeys = setOf("all", "movies", "kids", "sports")
                val nicheCategories = data.categories.filter { cat ->
                    val k = cat.key.lowercase()
                    !k.contains("movie") && !k.contains("kid") && !k.contains("sport") && cat.key != "all"
                }

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    val chipShape = RoundedCornerShape(50)
                    nicheCategories.forEach { cat ->
                        val isSelected = cat.key == category
                        Box(
                            modifier = Modifier
                                .height(34.dp)
                                .clip(chipShape)
                                .then(
                                    if (isSelected) {
                                        Modifier.background(Color.White)
                                    } else {
                                        Modifier
                                            .border(0.75.dp, Color.White.copy(alpha = 0.15f), chipShape)
                                            .background(Color.Transparent)
                                    }
                                )
                                .tactileBounce()
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.SegmentTick)
                                    onCategory(cat.key)
                                    moreSheetOpen = false
                                }
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = cat.name,
                                color = if (isSelected) Color(0xFF0A0B10) else Color.White,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Macro Categories Clean Row + "רק ישראלי" Toggle + "עוד..." BottomSheet Trigger:
 * 1. "רק ישראלי" pinned filter chip
 * 2. 4 Macro categories: הכל, סרטים וסדרות, ילדים ונוער, ספורט
 * 3. Static "עוד..." tab at the end of the row
 */
@Composable
private fun CategoryMacroBar(
    data: ChannelData,
    selected: String?,
    israeliOnly: Boolean,
    onSelect: (String?) -> Unit,
    onMore: () -> Unit,
    onToggleIsraeli: () -> Unit,
) {
    val scheme = MaterialTheme.colorScheme
    val haptic = LocalHapticFeedback.current

    // Locate the 3 key macro categories from the dataset
    val movieCat = data.categories.firstOrNull { it.key.contains("movie") || it.name.contains("סרטים") }
    val kidsCat = data.categories.firstOrNull { it.key.contains("kid") || it.name.contains("ילדים") }
    val sportCat = data.categories.firstOrNull { it.key.contains("sport") || it.name.contains("ספורט") }

    val macroEntries = listOfNotNull(
        null to "הכל",
        movieCat?.let { it.key to "סרטים וסדרות" },
        kidsCat?.let { it.key to "ילדים ונוער" },
        sportCat?.let { it.key to "ספורט" },
    )

    val chipShape = RoundedCornerShape(50)

    LazyRow(
        Modifier.padding(bottom = 16.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        flingBehavior = rememberBitChordFlingBehavior(),
    ) {
        // 1. "רק ישראלי" Filter Toggle Chip
        item(key = "israeli_toggle") {
            Box(
                modifier = Modifier
                    .height(34.dp)
                    .clip(chipShape)
                    .then(
                        if (israeliOnly) {
                            Modifier.background(Color.White)
                        } else {
                            Modifier
                                .border(0.75.dp, Color.White.copy(alpha = 0.15f), chipShape)
                                .background(Color.Transparent)
                        }
                    )
                    .tactileBounce {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onToggleIsraeli()
                    }
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.Center,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    AppleIsraelFlag(Modifier.size(18.dp))
                    Text(
                        text = "רק תוכן ישראלי",
                        color = if (israeliOnly) Color(0xFF0A0B10) else Color.White,
                        fontSize = 13.sp,
                        fontWeight = if (israeliOnly) FontWeight.ExtraBold else FontWeight.Bold,
                    )
                }
            }
        }

        // 2. 4 Clean Macro Categories: הכל, סרטים וסדרות, ילדים ונוער, ספורט
        items(macroEntries, key = { it.first ?: "all" }) { (key, name) ->
            val on = key == selected || (key == null && (selected == null || selected == "all"))
            Box(
                modifier = Modifier
                    .height(34.dp)
                    .clip(chipShape)
                    .then(
                        if (on) {
                            Modifier.background(Color.White)
                        } else {
                            Modifier
                                .border(0.75.dp, Color.White.copy(alpha = 0.15f), chipShape)
                                .background(Color.Transparent)
                        }
                    )
                    .tactileBounce {
                        haptic.performHapticFeedback(HapticFeedbackType.SegmentTick)
                        onSelect(key)
                    }
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = name,
                    color = if (on) Color(0xFF0A0B10) else Color.White,
                    fontSize = 13.sp,
                    fontWeight = if (on) FontWeight.ExtraBold else FontWeight.Bold,
                )
            }
        }

        // 3. Static "עוד..." tab at the end of the row
        item(key = "more_tab") {
            val isMoreSelected = selected != null && selected !in macroEntries.mapNotNull { it.first } && selected != "all"
            Box(
                modifier = Modifier
                    .height(34.dp)
                    .clip(chipShape)
                    .then(
                        if (isMoreSelected) {
                            Modifier.background(Color.White)
                        } else {
                            Modifier
                                .border(0.75.dp, Color.White.copy(alpha = 0.15f), chipShape)
                                .background(Color.Transparent)
                        }
                    )
                    .tactileBounce {
                        haptic.performHapticFeedback(HapticFeedbackType.SegmentTick)
                        onMore()
                    }
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.Center,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(
                        Icons.Rounded.MoreHoriz,
                        contentDescription = "עוד",
                        tint = if (isMoreSelected) Color(0xFF0A0B10) else Color.White,
                        modifier = Modifier.size(18.dp),
                    )
                    Text(
                        text = "עוד...",
                        color = if (isMoreSelected) Color(0xFF0A0B10) else Color.White,
                        fontSize = 13.sp,
                        fontWeight = if (isMoreSelected) FontWeight.ExtraBold else FontWeight.Bold,
                    )
                }
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
    LazyColumn(Modifier.fillMaxSize(), contentPadding = feedPadding(), flingBehavior = rememberBitChordFlingBehavior()) {
        if (data == null) return@LazyColumn
        val favs = prefs.favorites.mapNotNull { data.byId[it] }
        val recent = prefs.recents.mapNotNull { data.byId[it] }
        item(key = "fh") { SectionHeader("מועדפים") }
        if (favs.isEmpty()) item(key = "fe") { EmptyState("עוד אין מועדפים. לחצו על ☆ בערוץ כדי להוסיף אותו לכאן.") }
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
                    fontSize = 30.sp, fontWeight = FontWeight.ExtraBold, color = scheme.onSurface,
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
            Icon(Icons.Rounded.Search, null, tint = scheme.onSurfaceVariant)
            Box(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                if (query.isEmpty()) Text("שם ערוץ, מדינה, קטגוריה או מספר", color = scheme.onSurfaceVariant, fontSize = 16.sp)
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
            Icon(
                Icons.Rounded.Close, "סגירה",
                Modifier.size(26.dp).clip(RoundedCornerShape(50)).clickable { if (query.isNotEmpty()) query = "" else onClose() },
                tint = scheme.onSurfaceVariant,
            )
        }
    }
}

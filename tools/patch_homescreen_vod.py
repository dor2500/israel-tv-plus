import os

p = r"C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo\android\app\src\main\kotlin\com\israeltv\plus\ui\screens\HomeScreen.kt"
with open(p, "r", encoding="utf-8") as f:
    c = f.read()

c = c.replace("\r\n", "\n")

# 1. Imports
imports = """import com.israeltv.plus.data.VodSeries
import com.israeltv.plus.ui.components.VodShelf
import com.israeltv.plus.ui.components.VodSeriesSheet"""

if "import com.israeltv.plus.data.VodSeries" not in c:
    c = c.replace(
        "import com.israeltv.plus.data.playableUrl",
        "import com.israeltv.plus.data.playableUrl\n" + imports
    )

# 2. Add onSeeAllVod to signature
old_sig = """    onPlay: (Channel, List<Channel>) -> Unit,
    onInfo: (Channel) -> Unit,
    onSeeAll: (String) -> Unit,
) {"""

new_sig = """    onPlay: (Channel, List<Channel>) -> Unit,
    onInfo: (Channel) -> Unit,
    onSeeAll: (String) -> Unit,
    onSeeAllVod: (() -> Unit)? = null,
) {"""

if old_sig in c:
    c = c.replace(old_sig, new_sig)

# 3. Add selectedVodSeries state in HomeScreen
old_state = """    val heroScrollPx by remember(listState) {
        derivedStateOf {
            if (listState.firstVisibleItemIndex == 0) listState.firstVisibleItemScrollOffset.toFloat() else 900f
        }
    }"""

new_state = """    val heroScrollPx by remember(listState) {
        derivedStateOf {
            if (listState.firstVisibleItemIndex == 0) listState.firstVisibleItemScrollOffset.toFloat() else 900f
        }
    }
    var selectedVodSeries by remember { mutableStateOf<VodSeries?>(null) }"""

if old_state in c:
    c = c.replace(old_state, new_state)

# 4. Add VOD shelf in LazyColumn
old_shelf_target = """        val favs = prefs.favorites.mapNotNull { data.byId[it] }
        if (favs.isNotEmpty()) {
            val i = row++
            item(key = "favs") {
                ChannelShelf(
                    title = "הרשימה שלי",
                    channels = favs,
                    logo = logo,
                    subtitle = subtitle,
                    onPlay = { onPlay(it, favs) },
                    headerTrailing = if (!chipOnCategory && recent.isEmpty()) filterChip else null,
                    modifier = Modifier.cascadeIn(i),
                )
            }
        }"""

new_shelf_target = """        val favs = prefs.favorites.mapNotNull { data.byId[it] }
        if (favs.isNotEmpty()) {
            val i = row++
            item(key = "favs") {
                ChannelShelf(
                    title = "הרשימה שלי",
                    channels = favs,
                    logo = logo,
                    subtitle = subtitle,
                    onPlay = { onPlay(it, favs) },
                    headerTrailing = if (!chipOnCategory && recent.isEmpty()) filterChip else null,
                    modifier = Modifier.cascadeIn(i),
                )
            }
        }
        val vodList = data.vod
        if (vodList.isNotEmpty() && !isKids && macroCategory == null) {
            val i = row++
            item(key = "vod_shelf") {
                VodShelf(
                    title = "סדרות VOD ישראליות",
                    seriesList = vodList,
                    onSeriesClick = { selectedVodSeries = it },
                    onSeeAll = onSeeAllVod,
                    modifier = Modifier.cascadeIn(i),
                )
            }
        }"""

if old_shelf_target in c:
    c = c.replace(old_shelf_target, new_shelf_target)

# 5. Add bottom sheet dialog at the end of HomeScreen
target_lc_end = """                    headerTrailing = when {
                        cat.key == israeliKey -> filterChip
                        !chipOnCategory && recent.isEmpty() && favs.isEmpty() && cat === visibleCategories.first() -> filterChip
                        else -> null
                    },
                )
            }
        }
    }"""

sheet_code = """
    selectedVodSeries?.let { series ->
        VodSeriesSheet(
            series = series,
            onPlayEpisode = { ep ->
                val ch = ep.toChannel(series)
                val playlist = series.episodes.map { it.toChannel(series) }
                onPlay(ch, playlist)
                selectedVodSeries = null
            },
            onDismiss = { selectedVodSeries = null }
        )
    }"""

assert target_lc_end in c, "target_lc_end not found"
c = c.replace(target_lc_end, target_lc_end + sheet_code)

with open(p, "w", encoding="utf-8") as f:
    f.write(c)

print("HomeScreen.kt updated successfully with VOD shelf & details sheet!")

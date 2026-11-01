import os

p = r"C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo\android\app\src\main\kotlin\com\israeltv\plus\ui\screens\ListScreens.kt"
with open(p, "r", encoding="utf-8") as f:
    c = f.read()

c = c.replace("\r\n", "\n")

imports = """import com.israeltv.plus.data.VodSeries
import com.israeltv.plus.ui.components.VodShelf
import com.israeltv.plus.ui.components.VodSeriesSheet"""

if "import com.israeltv.plus.data.VodSeries" not in c:
    c = c.replace(
        "import com.israeltv.plus.data.subtitle",
        "import com.israeltv.plus.data.subtitle\n" + imports
    )

old_results = """    val results = remember(q, data) {
        if (data == null || q.isEmpty()) emptyList()
        else data.channels.filter { ch ->
            ch.name.contains(q, true) || ch.title.contains(q, true) || ch.category.contains(q, true) ||
                ch.countryHe.contains(q, true) || ch.number.toString() == q
        }.take(120)
    }"""

new_results = """    var inspectingSeries by remember { mutableStateOf<VodSeries?>(null) }
    val results = remember(q, data) {
        if (data == null || q.isEmpty()) emptyList()
        else data.channels.filter { ch ->
            ch.name.contains(q, true) || ch.title.contains(q, true) || ch.category.contains(q, true) ||
                ch.countryHe.contains(q, true) || ch.number.toString() == q
        }.take(120)
    }
    val vodResults = remember(q, data) {
        if (data == null || q.isEmpty()) emptyList()
        else data.vod.filter { s ->
            s.title.contains(q, true) || s.originalTitle.contains(q, true) || s.desc.contains(q, true) ||
                s.genres.any { g -> g.contains(q, true) }
        }
    }"""

assert old_results in c, "old_results not found"
c = c.replace(old_results, new_results)

old_header = """            item(key = "h") {
                Text(
                    if (q.isEmpty()) "חיפוש" else "${results.size} תוצאות",
                    Modifier.padding(horizontal = 18.dp).padding(bottom = 14.dp),
                    fontSize = 25.sp, fontWeight = FontWeight.SemiBold, color = scheme.onSurface,
                )
            }
            if (q.isNotEmpty() && results.isEmpty()) item(key = "e") { EmptyState("עוד אין פה כלום...") }"""

new_header = """            item(key = "h") {
                val totalCount = results.size + vodResults.size
                Text(
                    if (q.isEmpty()) "חיפוש" else "$totalCount תוצאות",
                    Modifier.padding(horizontal = 18.dp).padding(bottom = 14.dp),
                    fontSize = 25.sp, fontWeight = FontWeight.SemiBold, color = scheme.onSurface,
                )
            }
            if (vodResults.isNotEmpty()) {
                item(key = "vod_res") {
                    VodShelf(
                        title = "סדרות VOD תואמות",
                        seriesList = vodResults,
                        onSeriesClick = { inspectingSeries = it },
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }
            if (q.isNotEmpty() && results.isEmpty() && vodResults.isEmpty()) item(key = "e") { EmptyState("עוד אין פה כלום...") }"""

assert old_header in c, "old_header not found"
c = c.replace(old_header, new_header)

old_placeholder = 'if (query.isEmpty()) Text("חפשו ערוץ, קטגוריה או מספר...",'
new_placeholder = 'if (query.isEmpty()) Text("חפשו ערוץ, סדרת VOD או מספר...",'
if old_placeholder in c:
    c = c.replace(old_placeholder, new_placeholder)

sheet_end = """
        inspectingSeries?.let { series ->
            VodSeriesSheet(
                series = series,
                onPlayEpisode = { ep ->
                    val ch = ep.toChannel(series)
                    val playlist = series.episodes.map { it.toChannel(series) }
                    focusManager.clearFocus()
                    onPlay(ch, playlist)
                    inspectingSeries = null
                    onClose()
                },
                onDismiss = { inspectingSeries = null }
            )
        }
    }
}"""

c = c.rstrip()
if c.endswith("    }\n}"):
    c = c[:-6] + sheet_end

with open(p, "w", encoding="utf-8") as f:
    f.write(c)

print("ListScreens.kt updated successfully with VOD search!")


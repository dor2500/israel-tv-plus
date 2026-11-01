import os

p = r"C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo\android\app\src\main\kotlin\com\israeltv\plus\data\Channels.kt"
with open(p, "r", encoding="utf-8") as f:
    c = f.read()

c = c.replace("\r\n", "\n")

target1 = """data class ChannelData(
    val channels: List<Channel>,
    val categories: List<Category>,
    val byId: Map<String, Channel>,
    val featured: List<Channel>,
)"""

repl1 = """data class ChannelData(
    val channels: List<Channel>,
    val categories: List<Category>,
    val byId: Map<String, Channel>,
    val featured: List<Channel>,
    val vod: List<VodSeries> = emptyList(),
)"""

assert target1 in c, "target1 not found"
c = c.replace(target1, repl1)

target2 = """    suspend fun load() = withContext(Dispatchers.IO) {
        val text = cache.takeIf { it.isFile && it.length() > 1000 }?.let { runCatching { it.readText() }.getOrNull() }
            ?: context.assets.open("channels.json").bufferedReader().use { it.readText() }
        parse(text)?.let { _state.value = it }
            ?: parse(context.assets.open("channels.json").bufferedReader().use { it.readText() })?.let { _state.value = it }
    }

    suspend fun refresh() = withContext(Dispatchers.IO) {
        runCatching {
            val req = Request.Builder().url(REMOTE + "channels.json?t=" + System.currentTimeMillis()).build()
            http.newCall(req).execute().use { r ->
                if (!r.isSuccessful) return@runCatching
                val text = r.body.string()
                val data = parse(text) ?: return@runCatching
                cache.writeText(text)
                _state.value = data
            }
        }
    }"""

repl2 = """    suspend fun load() = withContext(Dispatchers.IO) {
        val vod = VodRepository.load(context)
        val text = cache.takeIf { it.isFile && it.length() > 1000 }?.let { runCatching { it.readText() }.getOrNull() }
            ?: context.assets.open("channels.json").bufferedReader().use { it.readText() }
        parse(text, vod)?.let { _state.value = it }
            ?: parse(context.assets.open("channels.json").bufferedReader().use { it.readText() }, vod)?.let { _state.value = it }
    }

    suspend fun refresh() = withContext(Dispatchers.IO) {
        val freshVod = VodRepository.refresh(context, http) ?: _state.value?.vod.orEmpty()
        runCatching {
            val req = Request.Builder().url(REMOTE + "channels.json?t=" + System.currentTimeMillis()).build()
            http.newCall(req).execute().use { r ->
                if (!r.isSuccessful) return@runCatching
                val text = r.body.string()
                val data = parse(text, freshVod) ?: return@runCatching
                cache.writeText(text)
                _state.value = data
            }
        }
    }"""

assert target2 in c, "target2 not found"
c = c.replace(target2, repl2)

target3 = """    private fun parse(text: String): ChannelData? = runCatching {
        val all = json.decodeFromString<List<Channel>>(text)
            .filter { it.kind != "app" && it.url.isNotEmpty() }
            .sortedBy { it.number }
        if (all.isEmpty()) return null
        val cats = all.groupBy { it.category }.map { (k, v) -> Category(k, shortCategory(k), v) }
        val israel = cats.firstOrNull { it.channels.first().categoryEn == "israel" }?.channels.orEmpty()
        ChannelData(all, cats, all.associateBy { it.id }, israel.filter { !it.isRadio }.take(8))
    }.getOrNull()"""

repl3 = """    private fun parse(text: String, vod: List<VodSeries> = emptyList()): ChannelData? = runCatching {
        val all = json.decodeFromString<List<Channel>>(text)
            .filter { it.kind != "app" && it.url.isNotEmpty() }
            .sortedBy { it.number }
        if (all.isEmpty()) return null
        val cats = all.groupBy { it.category }.map { (k, v) -> Category(k, shortCategory(k), v) }
        val israel = cats.firstOrNull { it.channels.first().categoryEn == "israel" }?.channels.orEmpty()
        ChannelData(all, cats, all.associateBy { it.id }, israel.filter { !it.isRadio }.take(8), vod)
    }.getOrNull()"""

assert target3 in c, "target3 not found"
c = c.replace(target3, repl3)

with open(p, "w", encoding="utf-8") as f:
    f.write(c)

print("Channels.kt updated with VOD data successfully!")


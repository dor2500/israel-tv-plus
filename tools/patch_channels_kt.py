with open("android/app/src/main/kotlin/com/israeltv/plus/data/Channels.kt", "r", encoding="utf-8") as f:
    code = f.read()

target = """    private val bundledLogos: Set<String> by lazy {
        runCatching { context.assets.list("www/assets/logos")?.toSet() }.getOrNull().orEmpty()
    }"""

replacement = """    private val bundledLogos: Set<String> by lazy {
        val s = mutableSetOf<String>()
        runCatching { context.assets.list("www/assets/logos")?.let { s.addAll(it) } }
        runCatching { context.assets.list("www/assets/vod")?.let { s.addAll(it) } }
        runCatching { context.assets.list("www/assets/app")?.let { s.addAll(it) } }
        s
    }"""

assert target in code, "target not found in Channels.kt"
code = code.replace(target, replacement, 1)

target2 = """    fun logoUrl(ch: Channel): String = when {
        ch.logo.isEmpty() -> ""
        ch.logo.startsWith("http") -> ch.logo
        ch.logo.substringAfterLast('/') in bundledLogos -> "file:///android_asset/www/" + ch.logo
        else -> REMOTE + ch.logo
    }"""

replacement2 = """    fun logoUrl(ch: Channel): String = when {
        ch.logo.isEmpty() -> ""
        ch.logo.startsWith("http") -> ch.logo
        ch.logo.substringAfterLast('/') in bundledLogos -> "file:///android_asset/www/" + ch.logo.removePrefix("/")
        else -> REMOTE + ch.logo.removePrefix("/")
    }"""

assert target2 in code, "target2 not found in Channels.kt"
code = code.replace(target2, replacement2, 1)

with open("android/app/src/main/kotlin/com/israeltv/plus/data/Channels.kt", "w", encoding="utf-8", newline="\n") as f:
    f.write(code)

print("Channels.kt updated successfully")


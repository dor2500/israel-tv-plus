import os

p = r"C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo\android\app\src\main\kotlin\com\israeltv\plus\ui\components\Chrome.kt"
with open(p, "r", encoding="utf-8") as f:
    c = f.read()

c = c.replace("\r\n", "\n")

# 1. Imports for VideoLibrary
if "import androidx.compose.material.icons.filled.VideoLibrary" not in c:
    c = c.replace(
        "import androidx.compose.material.icons.filled.Tv",
        "import androidx.compose.material.icons.filled.Tv\nimport androidx.compose.material.icons.filled.VideoLibrary"
    )
    c = c.replace(
        "import androidx.compose.material.icons.outlined.Tv",
        "import androidx.compose.material.icons.outlined.Tv\nimport androidx.compose.material.icons.outlined.VideoLibrary"
    )
    c = c.replace(
        "import androidx.compose.material.icons.rounded.Tv",
        "import androidx.compose.material.icons.rounded.Tv\nimport androidx.compose.material.icons.rounded.VideoLibrary"
    )

# 2. Add Vod to Tab enum
old_tab = """enum class Tab(
    val label: String,
    val title: String,
    val icon: ImageVector,
) {
    Home("בית", "ישראל TV+‎", Icons.Rounded.Home),
    Channels("ערוצים", "ערוצים", Icons.Rounded.LiveTv),
    Mine("שלי", "הרשימה שלי", Icons.Rounded.Favorite),
    Settings("הגדרות", "הגדרות", Icons.Rounded.Settings),
}"""

new_tab = """enum class Tab(
    val label: String,
    val title: String,
    val icon: ImageVector,
) {
    Home("בית", "ישראל TV+‎", Icons.Rounded.Home),
    Channels("ערוצים", "ערוצים", Icons.Rounded.LiveTv),
    Vod("VOD", "סדרות VOD", Icons.Rounded.VideoLibrary),
    Mine("שלי", "הרשימה שלי", Icons.Rounded.Favorite),
    Settings("הגדרות", "הגדרות", Icons.Rounded.Settings),
}"""

assert old_tab in c, "old_tab not found"
c = c.replace(old_tab, new_tab)

# 3. Add Vod to NuvioFloatingNavbar items
old_items = """    val items = listOf(
        NavItem("home", "בית", Icons.Outlined.Home, Icons.Filled.Home),
        NavItem("channels", "ערוצים", Icons.Outlined.Tv, Icons.Filled.Tv),
        NavItem("favorites", "שלי", Icons.Outlined.FavoriteBorder, Icons.Filled.Favorite),
        NavItem("search", "חיפוש", Icons.Outlined.Search, Icons.Filled.Search),
    )"""

new_items = """    val items = listOf(
        NavItem("home", "בית", Icons.Outlined.Home, Icons.Filled.Home),
        NavItem("channels", "ערוצים", Icons.Outlined.Tv, Icons.Filled.Tv),
        NavItem("vod", "VOD", Icons.Outlined.VideoLibrary, Icons.Filled.VideoLibrary),
        NavItem("favorites", "שלי", Icons.Outlined.FavoriteBorder, Icons.Filled.Favorite),
        NavItem("search", "חיפוש", Icons.Outlined.Search, Icons.Filled.Search),
    )"""

assert old_items in c, "old_items not found"
c = c.replace(old_items, new_items)

# 4. FloatingTabBar mapping
old_bar_route = """        selected == Tab.Home -> "home"
        selected == Tab.Channels -> "channels"
        selected == Tab.Mine -> "favorites"
        else -> ""
    }"""

new_bar_route = """        selected == Tab.Home -> "home"
        selected == Tab.Channels -> "channels"
        selected == Tab.Vod -> "vod"
        selected == Tab.Mine -> "favorites"
        else -> ""
    }"""

assert old_bar_route in c, "old_bar_route not found"
c = c.replace(old_bar_route, new_bar_route)

old_nav_when = """                when (it) {
                    "home" -> onSelect(Tab.Home)
                    "channels" -> onSelect(Tab.Channels)
                    "favorites" -> onSelect(Tab.Mine)
                    "search" -> onSearch()
                }"""

new_nav_when = """                when (it) {
                    "home" -> onSelect(Tab.Home)
                    "channels" -> onSelect(Tab.Channels)
                    "vod" -> onSelect(Tab.Vod)
                    "favorites" -> onSelect(Tab.Mine)
                    "search" -> onSearch()
                }"""

assert old_nav_when in c, "old_nav_when not found"
c = c.replace(old_nav_when, new_nav_when)

with open(p, "w", encoding="utf-8") as f:
    f.write(c)

print("Chrome.kt updated successfully with VOD tab!")


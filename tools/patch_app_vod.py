import os

p = r"C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo\android\app\src\main\kotlin\com\israeltv\plus\ui\App.kt"
with open(p, "r", encoding="utf-8") as f:
    c = f.read()

c = c.replace("\r\n", "\n")

# 1. Imports
if "import com.israeltv.plus.ui.screens.VodScreen" not in c:
    c = c.replace(
        "import com.israeltv.plus.ui.screens.SettingsScreen",
        "import com.israeltv.plus.ui.screens.SettingsScreen\nimport com.israeltv.plus.ui.screens.VodScreen"
    )

# 2. Add onSeeAllVod to HomeScreen call
old_home = """                            onSeeAll = { catKey ->
                                s.category = catKey
                                s.macroCategory = shortCategory(catKey)
                                s.tab = Tab.Channels
                            },
                        )"""

new_home = """                            onSeeAll = { catKey ->
                                s.category = catKey
                                s.macroCategory = shortCategory(catKey)
                                s.tab = Tab.Channels
                            },
                            onSeeAllVod = { s.tab = Tab.Vod },
                        )"""

assert old_home in c, "old_home not found"
c = c.replace(old_home, new_home)

# 3. Add Tab.Vod to tab branch
old_channels = """                        Tab.Channels -> ChannelsScreen(
                            data = data,
                            macroCategory = s.macroCategory,
                            israeliOnly = s.israeliOnly,
                            onIsraeliOnlyChange = { checked ->
                                s.israeliOnly = checked
                                s.overlayMode = if (checked) IsraeliOverlayMode.Entering else IsraeliOverlayMode.Exiting
                            },
                            onSwitchPositioned = { s.switchCenter = it },
                            logo = logo,
                            subtitle = subtitle,
                            onPlay = onPlay,
                            onViewAll = { s.macroCategory = it },
                            listState = s.channelsList,
                        )
                        Tab.Mine -> MineScreen(data, prefs, logo, subtitle, onPlay)"""

new_channels = """                        Tab.Channels -> ChannelsScreen(
                            data = data,
                            macroCategory = s.macroCategory,
                            israeliOnly = s.israeliOnly,
                            onIsraeliOnlyChange = { checked ->
                                s.israeliOnly = checked
                                s.overlayMode = if (checked) IsraeliOverlayMode.Entering else IsraeliOverlayMode.Exiting
                            },
                            onSwitchPositioned = { s.switchCenter = it },
                            logo = logo,
                            subtitle = subtitle,
                            onPlay = onPlay,
                            onViewAll = { s.macroCategory = it },
                            listState = s.channelsList,
                        )
                        Tab.Vod -> VodScreen(data = data, onPlay = onPlay)
                        Tab.Mine -> MineScreen(data, prefs, logo, subtitle, onPlay)"""

assert old_channels in c, "old_channels not found"
c = c.replace(old_channels, new_channels)

with open(p, "w", encoding="utf-8") as f:
    f.write(c)

print("App.kt updated successfully with VodScreen tab!")


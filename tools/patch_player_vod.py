import os

p = r"C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo\android\app\src\main\kotlin\com\israeltv\plus\ui\player\PlayerControls.kt"
with open(p, "r", encoding="utf-8") as f:
    c = f.read()

c = c.replace("\r\n", "\n")

old_badge = """            Text(
                if (ch.isRadio) "רדיו" else "Live",
                Modifier
                    .clip(badgeShape)
                    .border(1.dp, Color.White.copy(alpha = 0.42f), badgeShape)
                    .padding(horizontal = 8.dp, vertical = 2.dp),
                color = Color.White,
                style = MaterialTheme.typography.labelMedium,
            )"""

new_badge = """            val isVod = ch.kind == "vod" || ch.categoryEn == "vod"
            Text(
                when {
                    ch.isRadio -> "רדיו"
                    isVod -> "VOD"
                    else -> "Live"
                },
                Modifier
                    .clip(badgeShape)
                    .border(1.dp, if (isVod) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.42f), badgeShape)
                    .padding(horizontal = 8.dp, vertical = 2.dp),
                color = if (isVod) MaterialTheme.colorScheme.primary else Color.White,
                style = MaterialTheme.typography.labelMedium,
            )"""

assert old_badge in c, "old_badge not found"
c = c.replace(old_badge, new_badge)

with open(p, "w", encoding="utf-8") as f:
    f.write(c)

print("PlayerControls.kt updated with VOD badge successfully!")


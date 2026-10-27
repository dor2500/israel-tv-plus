package com.israeltv.plus.data

import android.content.Context
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Profile portraits are emojis: the system's own high-quality colour emoji font draws them, so they stay crisp at any
 * size and need no bundled artwork. Ids are prefixed ("emoji:👨🏽") so profiles saved by older versions - which used the
 * bundled series portraits ("series-rick", ...) - are recognised and migrated in [AppPrefs.loadProfiles].
 */
val AVATARS = listOf("emoji:👨🏽", "emoji:👩🏻‍🦱", "emoji:👽", "emoji:🦊", "emoji:🐯", "emoji:🐼")

private const val EMOJI_PREFIX = "emoji:"

fun isEmojiAvatar(id: String): Boolean = id.startsWith(EMOJI_PREFIX)

/** The glyph of an emoji avatar ("emoji:👨🏽" -> "👨🏽"); anything else is returned unchanged. */
fun avatarEmoji(id: String): String = id.removePrefix(EMOJI_PREFIX)

/** The series portraits of versions before 4.5.1, kept so a saved profile can be migrated and still shown as a URL. */
private val LEGACY_SERIES = listOf(
    "series-rick", "series-morty", "series-summer", "series-beth", "series-jerry", "series-ted-lasso",
)

fun avatarUrl(id: String) = if (id.startsWith("series-")) {
    "file:///android_asset/www/assets/avatars/$id.jpg"
} else {
    "file:///android_asset/www/assets/avatars/$id.webp"
}

/** Screen-reader labels for the emoji portraits (an emoji has no useful Hebrew name on its own). */
private val AVATAR_LABELS = listOf(
    "emoji:👨🏽" to "אביר",
    "emoji:👩🏻‍🦱" to "יוצרת",
    "emoji:👽" to "חוצן",
    "emoji:🦊" to "שועל",
    "emoji:🐯" to "טיגריס",
    "emoji:🐼" to "פנדה",
).toMap()

fun avatarDescription(id: String) = AVATAR_LABELS[id] ?: when (id) {
    "series-rick" -> "ריק סנצ׳ז — ריק ומורטי"
    "series-morty" -> "מורטי סמית׳ — ריק ומורטי"
    "series-summer" -> "סאמר סמית׳ — ריק ומורטי"
    "series-beth" -> "בת׳ סמית׳ — ריק ומורטי"
    "series-jerry" -> "ג׳רי סמית׳ — ריק ומורטי"
    "series-ted-lasso" -> "טד לאסו — טד לאסו"
    else -> "פרופיל"
}

/** The profile's emoji after migration; series portraits map one-to-one onto the new set. */
internal fun migratedAvatar(id: String, index: Int): String = when {
    isEmojiAvatar(id) -> id
    else -> {
        val legacy = LEGACY_SERIES.indexOf(id).takeIf { it >= 0 } ?: index
        AVATARS[legacy % AVATARS.size]
    }
}

/**
 * A profile's colours, like Android's "Wallpaper & style": [key] "system" is the phone's own Material You palette,
 * any other key is a palette made from [seed] (ARGB) in [style] (a materialkolor PaletteStyle name).
 * [wallpaper] says which tab of the picker it came from.
 */
@Immutable
@Serializable
data class ThemeChoice(
    val key: String = "system",
    val seed: Long = 0xFF4285F4,
    val style: String = "TonalSpot",
    val wallpaper: Boolean = true,
    val dark: Boolean = true,
)

/** A "who's watching" profile: name, avatar, colours, and its own favourites and history. */
@Immutable
@Serializable
data class Profile(
    val id: String,
    val name: String,
    val avatar: String,
    val theme: ThemeChoice = ThemeChoice(),
    val favorites: List<String> = emptyList(),
    val recents: List<String> = emptyList(),
)

private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

/**
 * What the phone app remembers: the profiles (each with favourites, recently watched, avatar and colours), the
 * active profile, the launch sound. Kept in the same SharedPreferences file as MainActivity ("itv"), so the
 * launch-sound switch is shared with it.
 * There is no mute setting of any kind: playback always runs at full volume.
 */
@Stable
class AppPrefs(context: Context) {
    private val sp = context.getSharedPreferences("itv", Context.MODE_PRIVATE)

    var profiles by mutableStateOf(loadProfiles())
        private set
    var activeId by mutableStateOf(sp.getString("phoneActiveProfile", null) ?: profiles.first().id)
        private set
    var launchSound by mutableStateOf(sp.getBoolean("launchSound", true))
        private set
    /** The update version whose modal was already shown on start (it opens once per version). */
    var updatePrompted by mutableStateOf(sp.getString("phoneUpdatePrompted", "") ?: "")
        private set

    val profile: Profile get() = profiles.firstOrNull { it.id == activeId } ?: profiles.first()
    val favorites: List<String> get() = profile.favorites
    val recents: List<String> get() = profile.recents
    val avatar: String get() = profile.avatar
    val theme: ThemeChoice get() = profile.theme

    fun isFavorite(id: String) = id in favorites

    fun toggleFavorite(id: String) = updateActive {
        it.copy(favorites = if (id in it.favorites) it.favorites - id else listOf(id) + it.favorites)
    }

    fun addRecent(id: String) = updateActive { it.copy(recents = (listOf(id) + (it.recents - id)).take(20)) }

    fun setTheme(theme: ThemeChoice) = updateActive { it.copy(theme = theme) }

    fun switchTo(id: String) {
        if (profiles.none { it.id == id }) return
        activeId = id
        sp.edit { putString("phoneActiveProfile", id) }
    }

    /** Adds or replaces a profile (matched by id). */
    fun saveProfile(p: Profile) {
        profiles = if (profiles.any { it.id == p.id }) profiles.map { if (it.id == p.id) p else it } else profiles + p
        persist()
    }

    fun deleteProfile(id: String) {
        if (profiles.size <= 1) return
        profiles = profiles.filter { it.id != id }
        if (activeId == id) switchTo(profiles.first().id)
        persist()
    }

    fun newProfileId() = "p" + System.currentTimeMillis().toString(36)

    fun setLaunchSoundOn(on: Boolean) {
        launchSound = on
        sp.edit { putBoolean("launchSound", on) }
    }

    fun markUpdatePrompted(version: String) {
        updatePrompted = version
        sp.edit { putString("phoneUpdatePrompted", version) }
    }

    private fun updateActive(change: (Profile) -> Profile) {
        val id = profile.id
        profiles = profiles.map { if (it.id == id) change(it) else it }
        persist()
    }

    private fun persist() = sp.edit { putString("phoneProfiles", json.encodeToString(profiles)) }

    /** The saved profiles; the first start after 3.4 turns the old single favourites / history / avatar into one. */
    private fun loadProfiles(): List<Profile> {
        sp.getString("phoneProfiles", null)?.let { s ->
            runCatching { json.decodeFromString<List<Profile>>(s) }.getOrNull()?.takeIf { it.isNotEmpty() }?.let { saved ->
                // 4.5.1: the old series portraits become the emoji set; names and viewing data stay untouched.
                val migrated = saved.mapIndexed { index, profile ->
                    profile.copy(avatar = migratedAvatar(profile.avatar, index))
                }
                if (migrated != saved) sp.edit { putString("phoneProfiles", json.encodeToString(migrated)) }
                return migrated
            }
        }
        val oldAvatar = sp.getString("phoneAvatar", null)
        return listOf(
            Profile(
                id = "p1",
                name = "אני",
                avatar = oldAvatar?.takeIf { isEmojiAvatar(it) } ?: AVATARS.first(),
                favorites = sp.list("phoneFavorites"),
                recents = sp.list("phoneRecents"),
            )
        )
    }

    private fun android.content.SharedPreferences.list(key: String): List<String> =
        getString(key, "").orEmpty().split(',').filter { it.isNotEmpty() }
}

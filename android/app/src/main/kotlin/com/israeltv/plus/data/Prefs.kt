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

/** The 3D Noto Emoji avatars in assets/avatars (Apache 2.0, see LICENSE.txt there). */
val AVATARS = listOf("1f60e", "1f431", "1f436", "1f98a", "1f981", "1f43c", "1f984", "1f47d", "1f916", "1f9b8", "1f9d1-1f680")

fun avatarUrl(id: String) = "file:///android_asset/www/assets/avatars/$id.webp"

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
            runCatching { json.decodeFromString<List<Profile>>(s) }.getOrNull()?.takeIf { it.isNotEmpty() }?.let { return it }
        }
        return listOf(
            Profile(
                id = "p1",
                name = "אני",
                avatar = sp.getString("phoneAvatar", AVATARS.first()) ?: AVATARS.first(),
                favorites = sp.list("phoneFavorites"),
                recents = sp.list("phoneRecents"),
            )
        )
    }

    private fun android.content.SharedPreferences.list(key: String): List<String> =
        getString(key, "").orEmpty().split(',').filter { it.isNotEmpty() }
}


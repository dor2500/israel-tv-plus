package com.israeltv.plus.data

import android.content.Context
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit

/** The 3D Noto Emoji avatars in assets/avatars (Apache 2.0, see LICENSE.txt there). */
val AVATARS = listOf("1f60e", "1f431", "1f436", "1f98a", "1f981", "1f43c", "1f984", "1f47d", "1f916", "1f9b8", "1f9d1-1f680")

fun avatarUrl(id: String) = "file:///android_asset/www/assets/avatars/$id.webp"

data class UserProfile(
    val id: String,
    val name: String,
    val avatar: String,
    val paletteIndex: Int = 0,
    val isWallpaper: Boolean = true,
)

/**
 * What the phone app remembers: favourites, recently watched, the avatar, the launch sound,
 * theme/palette preferences and user profiles.
 */
@Stable
class AppPrefs(context: Context) {
    private val sp = context.getSharedPreferences("itv", Context.MODE_PRIVATE)

    var favorites by mutableStateOf(sp.list("phoneFavorites"))
        private set
    var recents by mutableStateOf(sp.list("phoneRecents"))
        private set
    var avatar by mutableStateOf(sp.getString("phoneAvatar", AVATARS.first()) ?: AVATARS.first())
        private set
    var launchSound by mutableStateOf(sp.getBoolean("launchSound", true))
        private set
    var updatePrompted by mutableStateOf(sp.getString("phoneUpdatePrompted", "") ?: "")
        private set

    // Pixel Wallpaper & Style theme preferences
    var isDarkTheme by mutableStateOf(sp.getBoolean("isDarkTheme", true))
        private set
    var isWallpaperColors by mutableStateOf(sp.getBoolean("isWallpaperColors", true))
        private set
    var selectedPaletteIndex by mutableIntStateOf(sp.getInt("selectedPaletteIndex", 0))
        private set

    // Profiles
    var activeProfileId by mutableStateOf(sp.getString("activeProfileId", "default") ?: "default")
        private set

    fun isFavorite(id: String) = id in favorites

    fun toggleFavorite(id: String) {
        favorites = if (id in favorites) favorites - id else listOf(id) + favorites
        sp.edit { putString("phoneFavorites", favorites.joinToString(",")) }
    }

    fun addRecent(id: String) {
        recents = (listOf(id) + (recents - id)).take(20)
        sp.edit { putString("phoneRecents", recents.joinToString(",")) }
    }

    fun chooseAvatar(id: String) {
        avatar = id
        sp.edit { putString("phoneAvatar", id) }
    }

    fun setLaunchSoundOn(on: Boolean) {
        launchSound = on
        sp.edit { putBoolean("launchSound", on) }
    }

    fun markUpdatePrompted(version: String) {
        updatePrompted = version
        sp.edit { putString("phoneUpdatePrompted", version) }
    }

    fun updateDarkTheme(dark: Boolean) {
        isDarkTheme = dark
        sp.edit { putBoolean("isDarkTheme", dark) }
    }

    fun setWallpaperColorsMode(wallpaper: Boolean) {
        isWallpaperColors = wallpaper
        sp.edit { putBoolean("isWallpaperColors", wallpaper) }
    }

    fun selectPalette(index: Int) {
        selectedPaletteIndex = index
        sp.edit { putInt("selectedPaletteIndex", index) }
    }

    fun selectProfile(profileId: String, newAvatar: String) {
        activeProfileId = profileId
        avatar = newAvatar
        sp.edit {
            putString("activeProfileId", profileId)
            putString("phoneAvatar", newAvatar)
        }
    }

    private fun android.content.SharedPreferences.list(key: String): List<String> =
        getString(key, "").orEmpty().split(',').filter { it.isNotEmpty() }
}

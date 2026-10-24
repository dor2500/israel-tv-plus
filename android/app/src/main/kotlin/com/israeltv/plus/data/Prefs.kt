package com.israeltv.plus.data

import android.content.Context
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit

/** The 3D Noto Emoji avatars in assets/avatars (Apache 2.0, see LICENSE.txt there). */
val AVATARS = listOf("1f60e", "1f431", "1f436", "1f98a", "1f981", "1f43c", "1f984", "1f47d", "1f916", "1f9b8", "1f9d1-1f680")

fun avatarUrl(id: String) = "file:///android_asset/www/assets/avatars/$id.webp"

/**
 * What the phone app remembers: favourites, recently watched, the avatar, the launch sound. Kept in the same
 * SharedPreferences file as MainActivity ("itv"), so the launch-sound switch is shared with it.
 * There is no mute setting of any kind: playback always runs at full volume.
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
    /** The update version whose modal was already shown on start (it opens once per version). */
    var updatePrompted by mutableStateOf(sp.getString("phoneUpdatePrompted", "") ?: "")
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

    private fun android.content.SharedPreferences.list(key: String): List<String> =
        getString(key, "").orEmpty().split(',').filter { it.isNotEmpty() }
}

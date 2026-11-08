package com.israeltv.plus.data

import android.content.ContentProvider
import android.content.ContentValues
import android.content.res.AssetFileDescriptor
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.InputStream

/**
 * High-performance ContentProvider supplying channel logos, program thumbnails and folder icons
 * directly to Android Auto and Automotive head units.
 *
 * Reads directly from APK assets with zero network latency, eliminating blank/missing icons and
 * avoiding cellular data consumption while driving.
 */
class AutoArtworkProvider : ContentProvider(), ContentProvider.PipeDataWriter<InputStream> {

    companion object {
        const val AUTHORITY = "com.israeltv.plus.artwork"

        fun buildUri(path: String): Uri {
            val filename = File(path.removePrefix("/")).name
            return Uri.parse("content://$AUTHORITY/$filename")
        }
    }

    override fun onCreate(): Boolean = true

    override fun openAssetFile(uri: Uri, mode: String): AssetFileDescriptor? {
        val filename = uri.lastPathSegment ?: throw FileNotFoundException("Empty URI: $uri")
        val ctx = context ?: throw FileNotFoundException("No context")

        // 1. Try uncompressed direct AssetFileDescriptor
        val candidates = listOf(
            "www/assets/logos/$filename",
            "www/assets/vod/$filename",
            "www/assets/app/$filename",
            "www/$filename"
        )

        for (candidate in candidates) {
            try {
                return ctx.assets.openFd(candidate)
            } catch (_: Exception) {}
        }

        // 2. Try streaming compressed asset via openPipeHelper
        for (candidate in candidates) {
            try {
                val stream = ctx.assets.open(candidate)
                val pfd = openPipeHelper(uri, getType(uri), null, stream, this)
                return AssetFileDescriptor(pfd, 0, AssetFileDescriptor.UNKNOWN_LENGTH)
            } catch (_: Exception) {}
        }

        // 3. Try disk cache (e.g. VOD or downloaded images)
        val cached = File(ctx.cacheDir, "artwork/$filename")
        if (cached.exists() && cached.length() > 0) {
            val pfd = ParcelFileDescriptor.open(cached, ParcelFileDescriptor.MODE_READ_ONLY)
            return AssetFileDescriptor(pfd, 0, cached.length())
        }

        // 4. Fallback to default app icon
        try {
            val stream = ctx.assets.open("www/icon.png")
            val pfd = openPipeHelper(uri, "image/png", null, stream, this)
            return AssetFileDescriptor(pfd, 0, AssetFileDescriptor.UNKNOWN_LENGTH)
        } catch (_: Exception) {}

        throw FileNotFoundException("Artwork not found: $uri")
    }

    override fun writeDataToPipe(output: ParcelFileDescriptor, uri: Uri, mimeType: String, params: Bundle?, args: InputStream?) {
        if (args == null) return
        try {
            FileOutputStream(output.fileDescriptor).use { out ->
                args.use { inp ->
                    inp.copyTo(out)
                }
            }
        } catch (_: Exception) {}
    }

    override fun query(uri: Uri, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor? = null

    override fun getType(uri: Uri): String {
        val name = uri.lastPathSegment?.lowercase() ?: ""
        return when {
            name.endsWith(".jpg") || name.endsWith(".jpeg") -> "image/jpeg"
            name.endsWith(".webp") -> "image/webp"
            else -> "image/png"
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0
    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int = 0
}

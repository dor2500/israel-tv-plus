package com.israeltv.plus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.ChildCare
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Newspaper
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Radio
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.Science
import androidx.compose.material.icons.rounded.SportsSoccer
import androidx.compose.material.icons.rounded.TheaterComedy
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.israeltv.plus.data.avatarDescription
import com.israeltv.plus.data.avatarUrl

/**
 * A profile portrait rendered exclusively from the bundled Neo 3D WebP assets (assets/avatars).
 * Never uses flat standard Android text emojis.
 */
@Composable
fun ProfileAvatar(
    avatar: String,
    glyphSize: Dp,
    modifier: Modifier = Modifier,
) {
    val desc = avatarDescription(avatar)
    AsyncImage(
        model = avatarUrl(avatar),
        contentDescription = desc,
        modifier = modifier
            .size(glyphSize * 1.15f)
            .semantics { contentDescription = desc },
        contentScale = ContentScale.Fit,
    )
}

/**
 * Neo 3D category badge: volumetric 3D squircle with multi-stop radial lighting, top specular rim light,
 * and a bold sculpted glyph (zero flat Android emojis).
 */
@Composable
fun CategoryNeo3DIcon(
    categoryName: String,
    modifier: Modifier = Modifier,
    size: Dp = 32.dp,
) {
    val (topColor, bottomColor, icon) = neo3DCategoryStyle(categoryName)
    val shape = RoundedCornerShape(size * 0.34f)
    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(
                Brush.linearGradient(
                    colors = listOf(topColor, bottomColor),
                    start = Offset(0f, 0f),
                    end = Offset(120f, 120f),
                )
            )
            .drawWithContent {
                drawContent()
                // Top-left 3D volumetric dome highlight
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color.White.copy(alpha = 0.42f), Color.Transparent),
                        center = Offset(this.size.width * 0.28f, this.size.height * 0.22f),
                        radius = this.size.width * 0.65f,
                    ),
                )
            }
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(Color.White.copy(alpha = 0.55f), Color.White.copy(alpha = 0.08f))
                ),
                shape = shape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        BoldIcon(
            icon = icon,
            contentDescription = null,
            modifier = Modifier.size(size * 0.56f),
            tint = Color.White,
            weight = 0.55.dp,
        )
    }
}

private data class Neo3DStyle(val top: Color, val bottom: Color, val icon: ImageVector)

private fun neo3DCategoryStyle(name: String): Neo3DStyle = when {
    name.contains("ישראל") -> Neo3DStyle(Color(0xFF2E8BFF), Color(0xFF0045B5), Icons.Rounded.Tv)
    name.contains("סרטים") || name.contains("סדרות") -> Neo3DStyle(Color(0xFFFF4D79), Color(0xFF9E004F), Icons.Rounded.Movie)
    name.contains("ילדים") || name.contains("נוער") -> Neo3DStyle(Color(0xFFFFB800), Color(0xFFE65C00), Icons.Rounded.ChildCare)
    name.contains("ספורט") -> Neo3DStyle(Color(0xFF00E676), Color(0xFF007E33), Icons.Rounded.SportsSoccer)
    name.contains("מוזיקה") -> Neo3DStyle(Color(0xFFB388FF), Color(0xFF5E17EB), Icons.Rounded.MusicNote)
    name.contains("רדיו") -> Neo3DStyle(Color(0xFFFF7043), Color(0xFFBF360C), Icons.Rounded.Radio)
    name.contains("חדשות") -> Neo3DStyle(Color(0xFF29B6F6), Color(0xFF01579B), Icons.Rounded.Newspaper)
    name.contains("קומדיה") || name.contains("בידור") -> Neo3DStyle(Color(0xFFFFD54F), Color(0xFFFF6F00), Icons.Rounded.TheaterComedy)
    name.contains("מדע") -> Neo3DStyle(Color(0xFF00E5FF), Color(0xFF006064), Icons.Rounded.Science)
    name.contains("טבע") || name.contains("דוקו") || name.contains("טיולים") -> Neo3DStyle(Color(0xFF26A69A), Color(0xFF004D40), Icons.Rounded.Explore)
    name.contains("אוכל") -> Neo3DStyle(Color(0xFFFF8A65), Color(0xFFD84315), Icons.Rounded.Restaurant)
    name.contains("עולם") -> Neo3DStyle(Color(0xFF7986CB), Color(0xFF283593), Icons.Rounded.Public)
    else -> Neo3DStyle(Color(0xFF8E99F3), Color(0xFF3949AB), Icons.Rounded.AutoAwesome)
}

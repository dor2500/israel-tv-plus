package com.israeltv.plus.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.israeltv.plus.data.Category

/**
 * The four macro categories of the Kan Box reference. The five real categories of the catalog are mapped onto them by
 * their short Hebrew name ("סרטים וסדרות (Movies & Series)" -> "סרטים וסדרות"), and everything else lives behind "עוד...".
 */
val MacroCategories = listOf("סרטים וסדרות", "ילדים ונוער", "ספורט")

private val PillShape = RoundedCornerShape(50)

/**
 * The centred macro-category bar: exactly four main pills (הכל + the three [MacroCategories]) plus the static "עוד..."
 * pill that opens [MoreCategoriesSheet]. The group is centred with `Arrangement.Center`, exactly like Kan Box, and only
 * starts to scroll once the five pills no longer fit a narrow screen.
 *
 * Active: solid white. Inactive: transparent with a 1 dp border. Both colours cross-fade, and the haptic tick matches
 * the rest of the chrome.
 */
@Composable
fun MacroCategoryRow(
    selected: String?,
    onSelect: (String?) -> Unit,
    onMore: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val haptic = LocalHapticFeedback.current
    val entries = listOf<Pair<String?, String>>(null to "הכל") + MacroCategories.map { it to it }
    LazyRow(
        modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
        flingBehavior = rememberBitChordFlingBehavior(),
    ) {
        items(entries, key = { it.first ?: "*" }) { (key, label) ->
            MacroPill(label, active = selected == key, onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.SegmentTick)
                onSelect(key)
            })
        }
        item(key = "more") {
            MacroPill(
                "עוד...",
                active = selected != null && selected !in MacroCategories,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.SegmentTick)
                    onMore()
                },
            )
        }
    }
}

@Composable
private fun MacroPill(
    label: String,
    active: Boolean,
    onClick: () -> Unit,
    trailingIcon: ImageVector? = null,
) {
    val scheme = MaterialTheme.colorScheme
    val background by animateColorAsState(if (active) Color.White else Color.Transparent, label = "macroPillBg")
    val content by animateColorAsState(
        if (active) Color(0xFF0B0B12) else scheme.onSurface.copy(alpha = 0.92f),
        label = "macroPillFg",
    )
    val border by animateColorAsState(
        if (active) Color.Transparent else scheme.onSurface.copy(alpha = 0.34f),
        label = "macroPillBorder",
    )
    Row(
        Modifier
            .clip(PillShape)
            .background(background)
            .border(1.dp, border, PillShape)
            .clickable(onClick = onClick)
            .height(35.dp)
            .padding(horizontal = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            label,
            color = content,
            fontSize = 12.sp,
            fontWeight = if (active) FontWeight.ExtraBold else FontWeight.SemiBold,
            maxLines = 1,
        )
        if (trailingIcon != null) {
            Spacer(Modifier.width(4.dp))
            BoldIcon(trailingIcon, null, Modifier.size(15.dp), tint = content, weight = 0.45.dp)
        }
    }
}

/**
 * The "עוד..." sheet: every category that did not fit the four macro pills, with its channel count and a check on the
 * one currently filtering the feed. Picking one closes the sheet and filters the home shelves.
 */
@Composable
fun MoreCategoriesSheet(
    categories: List<Category>,
    selected: String?,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val scheme = MaterialTheme.colorScheme
    val haptic = LocalHapticFeedback.current
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = scheme.surfaceContainerLow,
        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
        dragHandle = {
            Box(
                Modifier.padding(top = 12.dp, bottom = 4.dp).size(width = 36.dp, height = 4.dp)
                    .clip(RoundedCornerShape(50)).background(scheme.onSurface.copy(alpha = 0.24f))
            )
        },
    ) {
        Column(Modifier.fillMaxWidth().navigationBarsPadding().padding(bottom = 12.dp)) {
            Text(
                "עוד קטגוריות",
                Modifier.padding(start = 22.dp, end = 22.dp, top = 4.dp),
                fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = scheme.onSurface,
            )
            Text(
                "בחרו איזו קטגוריה תוצג במסך הבית",
                Modifier.padding(start = 22.dp, end = 22.dp, top = 2.dp, bottom = 12.dp),
                fontSize = 13.sp, color = scheme.onSurfaceVariant,
            )
            if (categories.isEmpty()) {
                Text(
                    "כל הקטגוריות כבר למעלה",
                    Modifier.padding(horizontal = 22.dp, vertical = 24.dp),
                    fontSize = 14.sp, color = scheme.onSurfaceVariant,
                )
            } else {
                LazyColumn(Modifier.heightIn(max = 460.dp)) {
                    items(categories, key = { it.key }) { cat ->
                        val active = cat.name == selected
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.SegmentTick)
                                    onSelect(cat.name)
                                }
                                .padding(horizontal = 22.dp, vertical = 13.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(
                                    cat.name,
                                    color = if (active) scheme.primary else scheme.onSurface,
                                    fontSize = 16.sp,
                                    fontWeight = if (active) FontWeight.ExtraBold else FontWeight.SemiBold,
                                )
                                Text(
                                    "${cat.channels.size} ערוצים",
                                    color = scheme.onSurfaceVariant,
                                    fontSize = 12.sp,
                                )
                            }
                            if (active) {
                                BoldIcon(Icons.Rounded.Check, null, Modifier.size(20.dp), tint = scheme.primary, weight = 0.6.dp)
                            }
                        }
                    }
                }
            }
        }
    }
}

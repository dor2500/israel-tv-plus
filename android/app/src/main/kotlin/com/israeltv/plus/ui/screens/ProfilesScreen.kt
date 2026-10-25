package com.israeltv.plus.ui.screens

import com.israeltv.plus.ui.theme.RequestDarkSystemBars
import androidx.compose.ui.semantics.Role
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.israeltv.plus.ui.theme.LocalDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.israeltv.plus.data.AVATARS
import com.israeltv.plus.data.AppPrefs
import com.israeltv.plus.data.Profile
import com.israeltv.plus.data.ThemeChoice
import com.israeltv.plus.data.avatarUrl
import com.israeltv.plus.ui.components.sharedAvatar
import com.israeltv.plus.ui.theme.CuratedPalettes
import com.israeltv.plus.ui.theme.schemeFor
import com.israeltv.plus.ui.theme.wallpaperPalettes
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val TileShape = RoundedCornerShape(30.dp)

/**
 * Netflix-style "מי צופה?": every profile as a 100 dp squircle in its own Material You colour with its 3D emoji,
 * "+" to add one, and an edit mode. Choosing a profile bounces it (haptic tick) and its avatar flies into the
 * header corner of the home screen (shared element).
 */
@Composable
fun ProfilesScreen(prefs: AppPrefs, canClose: Boolean, onPicked: () -> Unit, onClose: () -> Unit) {
    // always the cinematic dark version of the active palette, light status-bar icons included
    val context = LocalContext.current
    RequestDarkSystemBars()
    val dark = remember(prefs.theme) { schemeFor(context, prefs.theme.copy(dark = true)) }
    MaterialTheme(colorScheme = dark, typography = MaterialTheme.typography) {
        CompositionLocalProvider(LocalDarkTheme provides true) {
            ProfilesContent(prefs, canClose, onPicked, onClose)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ProfilesContent(prefs: AppPrefs, canClose: Boolean, onPicked: () -> Unit, onClose: () -> Unit) {
    val context = LocalContext.current
    val scheme = MaterialTheme.colorScheme
    val haptic = LocalHapticFeedback.current
    var editing by remember { mutableStateOf(false) }
    var editor by remember { mutableStateOf<Profile?>(null) }
    BackHandler(enabled = editor != null || editing || canClose) {
        when {
            editor != null -> editor = null
            editing -> editing = false
            else -> onClose()
        }
    }

    val glows = remember(prefs.profiles) {
        prefs.profiles.take(3).flatMap { p -> schemeFor(context, p.theme.copy(dark = true)).let { listOf(it.primary, it.tertiary) } }
    }
    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0xFF07070B))
    ) {
        // the deep blurred canvas: the profiles' colours as large soft lights
        Box(
            Modifier
                .fillMaxSize()
                .blur(90.dp)
                .drawBehind {
                    glows.forEachIndexed { i, c ->
                        val x = size.width * (0.2f + 0.6f * ((i * 37) % 100) / 100f)
                        val y = size.height * (0.25f + 0.18f * i)
                        drawCircle(
                            Brush.radialGradient(listOf(c.copy(alpha = 0.55f), Color.Transparent), Offset(x, y), size.width * 0.6f),
                            size.width * 0.6f, Offset(x, y),
                        )
                    }
                }
        )
        Column(
            Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(72.dp))
            Text("מי צופה?", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.4).sp)
            Spacer(Modifier.height(36.dp))
            FlowRow(
                Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(22.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(26.dp),
                maxItemsInEachRow = 3,
            ) {
                prefs.profiles.forEach { p ->
                    ProfileTile(p, editing) {
                        if (editing) editor = p else {
                            prefs.switchTo(p.id)
                            onPicked()
                        }
                    }
                }
                if (prefs.profiles.size < 6) AddTile {
                    haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                    editor = Profile(prefs.newProfileId(), "", AVATARS[prefs.profiles.size % AVATARS.size], CuratedPalettes[prefs.profiles.size * 5 % CuratedPalettes.size].choice(dark = true))
                }
            }
            Spacer(Modifier.height(40.dp))
            Text(
                if (editing) "סיום" else "עריכת פרופילים",
                Modifier
                    .clip(RoundedCornerShape(50))
                    .border(1.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(50))
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.SegmentTick)
                        editing = !editing
                    }
                    .padding(horizontal = 22.dp, vertical = 10.dp),
                color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(40.dp))
        }

        AnimatedVisibility(
            editor != null,
            enter = fadeIn() + slideInVertically { it / 6 },
            exit = fadeOut() + slideOutVertically { it / 6 },
        ) {
            editor?.let { p ->
                ProfileEditor(
                    p,
                    isNew = prefs.profiles.none { it.id == p.id },
                    canDelete = prefs.profiles.size > 1 && prefs.profiles.any { it.id == p.id },
                    onSave = { prefs.saveProfile(it); editor = null },
                    onDelete = { prefs.deleteProfile(p.id); editor = null },
                    onCancel = { editor = null },
                )
            }
        }
    }
}

@Composable
private fun ProfileTile(p: Profile, editing: Boolean, onClick: () -> Unit) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    val bounce = remember { Animatable(1f) }
    val tone = remember(p.theme) { schemeFor(context, p.theme.copy(dark = true)) }
    Column(
        Modifier.width(100.dp).clickable(remember { MutableInteractionSource() }, null, role = Role.Button) {
            haptic.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
            scope.launch {
                bounce.animateTo(0.88f, spring(stiffness = Spring.StiffnessHigh))
                bounce.animateTo(1f, spring(Spring.DampingRatioHighBouncy, Spring.StiffnessMedium))
            }
            scope.launch { delay(if (editing) 0 else 140); onClick() }
        },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            Modifier
                .size(100.dp)
                .graphicsLayer { scaleX = bounce.value; scaleY = bounce.value }
                .sharedAvatar("avatar:${p.id}")
                .clip(TileShape)
                .background(Brush.linearGradient(listOf(tone.primaryContainer, tone.tertiaryContainer)))
                .border(1.dp, Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.35f), Color.Transparent)), TileShape),
            contentAlignment = Alignment.Center,
        ) {
            AsyncImage(avatarUrl(p.avatar), null, Modifier.size(66.dp))
            if (editing) {
                Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.45f)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Rounded.Edit, "עריכה", Modifier.size(30.dp), tint = Color.White)
                }
            }
        }
        Text(
            p.name, Modifier.padding(top = 10.dp), color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold,
            maxLines = 1, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun AddTile(onClick: () -> Unit) {
    Column(Modifier.width(100.dp).clickable(role = Role.Button, onClick = onClick), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier
                .size(100.dp)
                .clip(TileShape)
                .background(Color.White.copy(alpha = 0.06f))
                .border(1.5.dp, Color.White.copy(alpha = 0.3f), TileShape),
            contentAlignment = Alignment.Center,
        ) { Icon(Icons.Rounded.Add, null, Modifier.size(44.dp), tint = Color.White) }
        Text("הוספה", Modifier.padding(top = 10.dp), color = Color.White.copy(alpha = 0.8f), fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

/** New / edit profile: name, 3D emoji and the profile's own tri-tone palette. */
@Composable
private fun ProfileEditor(
    start: Profile,
    isNew: Boolean,
    canDelete: Boolean,
    onSave: (Profile) -> Unit,
    onDelete: () -> Unit,
    onCancel: () -> Unit,
) {
    val context = LocalContext.current
    val scheme = MaterialTheme.colorScheme
    var p by remember(start.id) { mutableStateOf(start) }
    // deleting takes favourites and history with it: the first tap asks, the second deletes
    var confirmDelete by remember(start.id) { mutableStateOf(false) }
    val palettes = remember { (wallpaperPalettes(context).take(1) + CuratedPalettes) }
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.55f))
            .clickable(remember { MutableInteractionSource() }, null, onClick = onCancel),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier
                .padding(20.dp)
                .imePadding()
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp))
                .background(scheme.surfaceContainerHigh)
                .border(1.dp, Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.32f), Color.Transparent)), RoundedCornerShape(32.dp))
                .clickable(remember { MutableInteractionSource() }, null) {}
                .verticalScroll(rememberScrollState())   // small phones with the keyboard up: save stays reachable
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val tone = remember(p.theme) { schemeFor(context, p.theme.copy(dark = true)) }
            Box(
                Modifier.size(84.dp).clip(TileShape).background(Brush.linearGradient(listOf(tone.primaryContainer, tone.tertiaryContainer))),
                contentAlignment = Alignment.Center,
            ) { AsyncImage(avatarUrl(p.avatar), null, Modifier.size(56.dp)) }
            Text(
                if (isNew) "פרופיל חדש" else "עריכת פרופיל",
                Modifier.padding(top = 12.dp, bottom = 12.dp),
                color = scheme.onSurface, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold,
            )
            OutlinedTextField(
                p.name, { p = p.copy(name = it.take(16)) },
                Modifier.fillMaxWidth(), singleLine = true, label = { Text("שם") }, shape = RoundedCornerShape(18.dp),
            )
            Text("אווטאר", Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 8.dp), color = scheme.onSurfaceVariant, fontSize = 13.sp)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(AVATARS) { id ->
                    val on = id == p.avatar
                    Box(
                        Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(if (on) scheme.primaryContainer else scheme.surfaceContainerHighest.copy(alpha = 0.6f))
                            .then(if (on) Modifier.border(2.dp, scheme.primary, CircleShape) else Modifier)
                            .clickable { p = p.copy(avatar = id) },
                        contentAlignment = Alignment.Center,
                    ) { AsyncImage(avatarUrl(id), null, Modifier.size(38.dp)) }
                }
            }
            Text("צבעים", Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 8.dp), color = scheme.onSurfaceVariant, fontSize = 13.sp)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(vertical = 2.dp)) {
                items(palettes, key = { it.key }) { option ->
                    val colors = remember(option) { schemeFor(context, option, true).let { Triple(it.primary, it.secondary, it.tertiary) } }
                    TriToneSwatch(colors, selected = p.theme.key == option.key, label = if (option.key == "system") "צבעי המערכת" else "פלטה") {
                        p = p.copy(theme = option.choice(p.theme.dark))
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Button(
                    { onSave(p.copy(name = p.name.trim().ifEmpty { "צופה" })) },
                    shape = RoundedCornerShape(50), modifier = Modifier.height(50.dp),
                ) { Text("שמירה", fontWeight = FontWeight.ExtraBold) }
                TextButton(onCancel) { Text("ביטול") }
                Spacer(Modifier.weight(1f))
                if (canDelete) TextButton({ if (confirmDelete) onDelete() else confirmDelete = true }) {
                    Text(if (confirmDelete) "למחוק לגמרי?" else "מחיקה", color = scheme.error, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

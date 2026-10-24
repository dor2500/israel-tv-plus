package com.israeltv.plus.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import com.israeltv.plus.data.AVATARS
import com.israeltv.plus.data.AppPrefs
import com.israeltv.plus.data.UserProfile
import com.israeltv.plus.data.avatarUrl
import com.israeltv.plus.ui.components.frostedGlass
import com.israeltv.plus.ui.components.specularBorder

/**
 * Netflix-Style 'Who's Watching?' Profile Switcher (מי צופה?):
 * - Deep blurred background canvas
 * - Responsive grid of User Profiles (100.dp squircle container, 3D Noto Emoji avatar, name)
 * - 'Add Profile' (+) card and 'Edit Profiles' toggle
 * - Spring scale bounce + haptic tick on selection
 */
@Composable
fun ProfileSelectionScreen(
    prefs: AppPrefs,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val haptic = LocalHapticFeedback.current
    val scheme = MaterialTheme.colorScheme

    val profiles = remember {
        mutableStateListOf(
            UserProfile(id = "1", name = "ראשי", avatar = prefs.avatar),
            UserProfile(id = "2", name = "ילדים", avatar = "1f43c"),
            UserProfile(id = "3", name = "סלון", avatar = "1f981"),
        )
    }

    var isEditing by remember { mutableStateOf(false) }
    var isAdding by remember { mutableStateOf(false) }
    var selectedProfileId by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .zIndex(100f)
            .background(Color.Black.copy(alpha = 0.88f))
            .frostedGlass(RoundedCornerShape(0.dp), blur = 90.dp, border = false),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Top close button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Rounded.Close, "סגור", tint = Color.White.copy(alpha = 0.7f))
                }
            }

            Spacer(Modifier.height(24.dp))

            // Centered Title: מי צופה?
            Text(
                text = "מי צופה?",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
            )

            Spacer(Modifier.height(36.dp))

            // Profiles Grid
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 110.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier.weight(1f, fill = false),
            ) {
                items(profiles, key = { it.id }) { profile ->
                    val isTapped = selectedProfileId == profile.id
                    val scale by animateFloatAsState(
                        targetValue = if (isTapped) 1.15f else 1f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow),
                        label = "profileScale",
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .scale(scale)
                            .clickable(remember { MutableInteractionSource() }, null) {
                                haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                                selectedProfileId = profile.id
                                prefs.selectProfile(profile.id, profile.avatar)
                                onDismiss()
                            },
                    ) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(26.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(scheme.primaryContainer, scheme.surfaceContainerHigh)
                                    )
                                )
                                .specularBorder(RoundedCornerShape(26.dp), alpha = 0.35f),
                            contentAlignment = Alignment.Center,
                        ) {
                            AsyncImage(
                                model = avatarUrl(profile.avatar),
                                contentDescription = profile.name,
                                modifier = Modifier.size(72.dp),
                            )

                            if (isEditing) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Black.copy(alpha = 0.45f)),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(Icons.Rounded.Edit, null, tint = Color.White, modifier = Modifier.size(28.dp))
                                }
                            }
                        }

                        Spacer(Modifier.height(10.dp))

                        Text(
                            text = profile.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                        )
                    }
                }

                // 'Add Profile' (+) card
                item(key = "add_profile") {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                            isAdding = true
                        },
                    ) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(26.dp))
                                .background(Color.White.copy(alpha = 0.08f))
                                .border(1.5.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(26.dp)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Rounded.Add, "הוסף פרופיל", tint = Color.White, modifier = Modifier.size(36.dp))
                        }

                        Spacer(Modifier.height(10.dp))

                        Text(
                            text = "הוסף פרופיל",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.7f),
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            // 'Edit Profiles' toggle button at bottom
            Button(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.VirtualKey)
                    isEditing = !isEditing
                },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isEditing) Color.White else Color.Transparent,
                    contentColor = if (isEditing) Color.Black else Color.White,
                ),
                border = if (!isEditing) androidx.compose.foundation.BorderStroke(1.5.dp, Color.White.copy(alpha = 0.4f)) else null,
                contentPadding = PaddingValues(horizontal = 28.dp, vertical = 12.dp),
            ) {
                Text(
                    text = if (isEditing) "סיום עריכה" else "ניהול פרופילים",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        // Add Profile Dialog
        AnimatedVisibility(
            visible = isAdding,
            enter = fadeIn() + scaleIn(initialScale = 0.9f),
            exit = fadeOut() + scaleOut(targetScale = 0.9f),
        ) {
            AddProfileDialog(
                onAdd = { name, avatar ->
                    profiles.add(UserProfile(id = "${profiles.size + 1}", name = name, avatar = avatar))
                    isAdding = false
                },
                onDismiss = { isAdding = false },
            )
        }
    }
}

@Composable
private fun AddProfileDialog(
    onAdd: (name: String, avatar: String) -> Unit,
    onDismiss: () -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var selectedAvatar by remember { mutableStateOf(AVATARS.first()) }
    val scheme = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.75f))
            .clickable(remember { MutableInteractionSource() }, null, onClick = onDismiss),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp))
                .background(scheme.surfaceContainerHigh)
                .border(1.dp, Color.White.copy(0.2f), RoundedCornerShape(32.dp))
                .clickable(remember { MutableInteractionSource() }, null) {}
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "פרופיל חדש",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = scheme.onSurface,
            )

            Spacer(Modifier.height(16.dp))

            // Avatar Preview
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(scheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                AsyncImage(avatarUrl(selectedAvatar), null, Modifier.size(60.dp))
            }

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("שם הפרופיל") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(16.dp))

            // Avatar picker row
            Text("בחירת אווטאר:", fontSize = 13.sp, color = scheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            ) {
                AVATARS.take(5).forEach { av ->
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (av == selectedAvatar) scheme.primary else scheme.surfaceContainerHighest)
                            .clickable { selectedAvatar = av }
                            .padding(4.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        AsyncImage(avatarUrl(av), null, Modifier.size(32.dp))
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                TextButton(onClick = onDismiss) { Text("ביטול") }
                Button(
                    onClick = { if (name.isNotBlank()) onAdd(name.trim(), selectedAvatar) },
                    enabled = name.isNotBlank(),
                    shape = RoundedCornerShape(50),
                ) { Text("הוספה") }
            }
        }
    }
}

package com.israeltv.plus.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Watch
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.israeltv.plus.data.WatchInstaller
import com.israeltv.plus.data.WatchState
import com.israeltv.plus.ui.components.GlassIconButton
import com.israeltv.plus.ui.components.rememberBitChordFlingBehavior
import com.israeltv.plus.ui.components.specularBorder

/** "host:port" -> (host, port), or null. */
private fun endpoint(text: String): Pair<String, Int>? {
    val t = text.trim()
    val i = t.lastIndexOf(':')
    if (i <= 0) return null
    val port = t.substring(i + 1).toIntOrNull() ?: return null
    return t.substring(0, i) to port
}

/**
 * Settings → "התקנה בשעון": the Wear OS app goes onto the watch from here. Once: Wireless debugging on the watch and
 * the six-digit pairing code; after that one tap installs or updates it.
 */
@Composable
fun WatchScreen(installer: WatchInstaller, onClose: () -> Unit) {
    val scheme = MaterialTheme.colorScheme
    val haptic = LocalHapticFeedback.current
    val state = installer.state
    BackHandler(onBack = onClose)
    DisposableEffect(Unit) {
        installer.reset()
        installer.startDiscovery()
        onDispose { installer.stopDiscovery() }
    }
    var pairText by rememberSaveable { mutableStateOf("") }
    var code by rememberSaveable { mutableStateOf("") }
    var connectText by rememberSaveable { mutableStateOf("") }
    // what the network tells us fills the empty fields (the watch announces both while its screens are open)
    val foundPair = installer.pairing.firstOrNull()
    val foundConnect = installer.connect.firstOrNull { foundPair == null || it.host == foundPair.host } ?: installer.connect.firstOrNull()
    if (pairText.isEmpty() && foundPair != null) pairText = "${foundPair.host}:${foundPair.port}"
    if (connectText.isEmpty() && foundConnect != null) connectText = "${foundConnect.host}:${foundConnect.port}"
    val busy = state is WatchState.Pairing || state is WatchState.Connecting || state is WatchState.Allow ||
        state is WatchState.Downloading || state is WatchState.Installing
    val ltr = TextStyle(textDirection = TextDirection.Ltr, fontSize = 17.sp)

    Box(
        Modifier
            .fillMaxSize()
            .background(scheme.surfaceContainerLowest)
            .clickable(remember { MutableInteractionSource() }, null) {}
    ) {
        LazyColumn(
            Modifier.fillMaxSize().imePadding(),
            contentPadding = PaddingValues(bottom = 40.dp),
            flingBehavior = rememberBitChordFlingBehavior(),
        ) {
            item(key = "bar") {
                Row(
                    Modifier.statusBarsPadding().fillMaxWidth().height(64.dp).padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    GlassIconButton(
                        Icons.AutoMirrored.Rounded.ArrowBack, "חזרה", onClose, size = 44.dp,
                        tint = scheme.surfaceContainerHigh, iconTint = scheme.onSurface,
                    )
                    Spacer(Modifier.width(14.dp))
                    Text("התקנה בשעון", style = MaterialTheme.typography.headlineMedium, color = scheme.onSurface)
                }
            }
            item(key = "hero") {
                Card {
                    Box(Modifier.size(56.dp).clip(CircleShape).background(scheme.primary), contentAlignment = Alignment.Center) {
                        Icon(Icons.Rounded.Watch, null, Modifier.size(30.dp), tint = scheme.onPrimary)
                    }
                    Text(
                        "ישראל TV+ לשעון Wear OS", Modifier.padding(top = 14.dp),
                        fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = scheme.onSurface,
                    )
                    Text(
                        "אפליקציה נפרדת לשעון: ערוצי ישראל, רדיו, ספורט וילדים במסך העגול, והרדיו ממשיך לנגן גם כשהמסך כבוי. " +
                            "מתקינים אותה מכאן דרך רשת ה-Wi-Fi, בלי Google Play.",
                        Modifier.padding(top = 6.dp), fontSize = 14.sp, color = scheme.onSurfaceVariant,
                    )
                }
            }
            // the watch paired before: one tap installs the latest version again
            if (installer.savedHost.isNotEmpty()) item(key = "saved") {
                Card {
                    Text("השעון שלך", fontSize = 13.sp, color = scheme.onSurfaceVariant)
                    Text(
                        installer.savedName.ifEmpty { "שעון" } + " · " + installer.savedHost,
                        fontSize = 17.sp, fontWeight = FontWeight.Bold, color = scheme.onSurface,
                    )
                    Spacer(Modifier.height(12.dp))
                    ActionButton("עדכון / התקנה מחדש בשעון", enabled = !busy) {
                        haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                        val c = endpoint(connectText)
                        if (c != null && c.first == installer.savedHost) installer.installAgain(c.first, c.second) else installer.installAgain()
                    }
                }
            }
            item(key = "progress") { Progress(state) }
            item(key = "steps") {
                Card {
                    Text("פעם אחת בשעון", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = scheme.onSurface)
                    Step(1, "חברו את השעון לאותה רשת Wi-Fi של הטלפון (הגדרות ← קישוריות ← Wi-Fi).")
                    Step(2, "הגדרות ← מערכת ← אודות ← גלו את \"מספר Build\" ולחצו עליו 7 פעמים: \"אפשרויות למפתחים\" נפתחות.")
                    Step(3, "הגדרות ← אפשרויות למפתחים ← הפעילו \"ניפוי באגים ב-ADB\" ו\"ניפוי באגים אלחוטי\".")
                    Step(4, "ב\"ניפוי באגים אלחוטי\" לחצו \"צימוד מכשיר חדש\": השעון מציג קוד בן 6 ספרות וכתובת. השאירו את המסך פתוח.")
                    Step(5, "הקלידו כאן את הקוד (הכתובת מתמלאת לבד כשהשעון נמצא ברשת) ולחצו \"צימוד והתקנה\".")
                }
            }
            item(key = "form") {
                Card {
                    if (foundPair != null) {
                        Text("✓ נמצא שעון שמחכה לצימוד: ${foundPair.host}", fontSize = 13.sp, color = scheme.primary)
                        Spacer(Modifier.height(8.dp))
                    }
                    OutlinedTextField(
                        code, { code = it.filter(Char::isDigit).take(6) }, Modifier.fillMaxWidth(),
                        label = { Text("קוד צימוד (6 ספרות)") }, singleLine = true, textStyle = ltr.copy(fontSize = 22.sp, letterSpacing = 6.sp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword), shape = RoundedCornerShape(18.dp),
                    )
                    Spacer(Modifier.height(10.dp))
                    OutlinedTextField(
                        pairText, { pairText = it.trim() }, Modifier.fillMaxWidth(),
                        label = { Text("כתובת IP ויציאה מחלון הצימוד") }, placeholder = { Text("192.168.1.20:37045") },
                        singleLine = true, textStyle = ltr, shape = RoundedCornerShape(18.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                    )
                    Spacer(Modifier.height(10.dp))
                    OutlinedTextField(
                        connectText, { connectText = it.trim() }, Modifier.fillMaxWidth(),
                        label = { Text("כתובת IP ויציאה של \"ניפוי באגים אלחוטי\"") },
                        supportingText = { Text("מופיעה במסך \"ניפוי באגים אלחוטי\" עצמו (לא בחלון הצימוד). בדרך כלל מתמלאת לבד") },
                        singleLine = true, textStyle = ltr, shape = RoundedCornerShape(18.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                    )
                    Spacer(Modifier.height(14.dp))
                    val pair = endpoint(pairText)
                    ActionButton("צימוד והתקנה", enabled = !busy && code.length == 6 && pair != null) {
                        haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                        val c = endpoint(connectText)
                        installer.pairAndInstall(pair!!.first, pair.second, code, if (c != null && c.first == pair.first) c.second else 0)
                    }
                }
            }
            item(key = "note") {
                Text(
                    "אחרי ההתקנה אפשר לכבות את \"ניפוי באגים אלחוטי\" בשעון. לעדכון בפעם הבאה מדליקים אותו שוב ולוחצים \"עדכון\" – בלי קוד חדש.",
                    Modifier.padding(horizontal = 28.dp, vertical = 8.dp).navigationBarsPadding(),
                    fontSize = 13.sp, color = scheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun Progress(state: WatchState) {
    val scheme = MaterialTheme.colorScheme
    val (text, progress) = when (state) {
        WatchState.Idle -> return
        WatchState.Pairing -> "מצמד עם השעון…" to null
        WatchState.Connecting -> "מתחבר לשעון…" to null
        WatchState.Allow -> "בשעון: אשרו \"לאפשר ניפוי באגים\" מהטלפון הזה" to null
        is WatchState.Downloading -> "מוריד את אפליקציית השעון… ${(state.progress * 100).toInt()}%" to state.progress
        is WatchState.Installing -> "מתקין בשעון… ${(state.progress * 100).toInt()}%" to state.progress
        is WatchState.Done -> "הותקן בשעון" + (if (state.watch.isNotEmpty()) " (${state.watch})" else "") + "! פתחו בשעון את רשימת האפליקציות ← ישראל TV+" to 1f
        is WatchState.Failed -> state.message to null
    }
    Card(highlight = state is WatchState.Done) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            when (state) {
                is WatchState.Done -> Icon(Icons.Rounded.CheckCircle, null, Modifier.size(26.dp), tint = scheme.primary)
                is WatchState.Failed -> Icon(Icons.Rounded.ErrorOutline, null, Modifier.size(26.dp), tint = scheme.error)
                else -> Icon(Icons.Rounded.Watch, null, Modifier.size(26.dp), tint = scheme.primary)
            }
            Spacer(Modifier.width(12.dp))
            Text(text, Modifier.weight(1f), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = scheme.onSurface)
        }
        if (state !is WatchState.Done && state !is WatchState.Failed) {
            if (progress != null) LinearWavyProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth().padding(top = 14.dp))
            else LinearWavyProgressIndicator(modifier = Modifier.fillMaxWidth().padding(top = 14.dp))
        }
    }
}

@Composable
private fun Card(highlight: Boolean = false, content: @Composable () -> Unit) {
    val scheme = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(28.dp)
    Column(
        Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .clip(shape)
            .background(if (highlight) scheme.primaryContainer else scheme.surfaceContainerHigh.copy(alpha = 0.7f))
            .specularBorder(shape)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) { content() }
}

@Composable
private fun Step(n: Int, text: String) {
    val scheme = MaterialTheme.colorScheme
    Row(Modifier.padding(top = 10.dp)) {
        Box(Modifier.size(26.dp).clip(CircleShape).background(scheme.secondaryContainer), contentAlignment = Alignment.Center) {
            Text("$n", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = scheme.onSecondaryContainer)
        }
        Spacer(Modifier.width(12.dp))
        Text(text, Modifier.weight(1f), fontSize = 14.sp, color = scheme.onSurface)
    }
}

@Composable
private fun ActionButton(label: String, enabled: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick, enabled = enabled, shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(), contentPadding = PaddingValues(horizontal = 24.dp),
        modifier = Modifier.fillMaxWidth().height(52.dp),
    ) { Text(label, fontWeight = FontWeight.Bold, fontSize = 16.sp) }
}

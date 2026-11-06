package com.israeltv.plus.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import androidx.compose.foundation.layout.heightIn
import com.israeltv.plus.data.CatchupProgram
import com.israeltv.plus.data.CatchupRepository
import com.israeltv.plus.data.Channel
import com.israeltv.plus.ui.components.ChannelLogo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatchupSheet(
    channel: Channel,
    logoUrl: String,
    onSelectProgram: (CatchupProgram) -> Unit,
    onRewindMinutes: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val scheme = MaterialTheme.colorScheme
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var programs by remember { mutableStateOf<List<CatchupProgram>?>(null) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(channel.id) {
        loading = true
        val progs = CatchupRepository.getProgramsForChannel(context, channel)
        programs = progs
        loading = false
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF131824),
        contentColor = Color.White,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp)
        ) {
            // Header
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .size(width = 54.dp, height = 34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.08f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        ChannelLogo(logoUrl, Modifier.size(width = 44.dp, height = 26.dp), channel.isRadio)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "קאץ' אפ · שידור חוזר",
                                color = Color.White,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(Modifier.width(6.dp))
                            Box(
                                Modifier
                                    .clip(CircleShape)
                                    .background(Color(0xFFE5A93C).copy(alpha = 0.2f))
                                    .padding(horizontal = 7.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    "24h DVR",
                                    color = Color(0xFFE5A93C),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                        Text(
                            "${channel.name} · בחר תוכנית לצפייה מההתחלה",
                            color = Color.White.copy(alpha = 0.65f),
                            fontSize = 12.sp,
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // Quick Rewind Chips
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(15 to "15 דק'", 30 to "30 דק'", 60 to "שעה אחורה", 120 to "שעתיים").forEach { (mins, label) ->
                    Box(
                        Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.08f))
                            .clickable {
                                onRewindMinutes(mins)
                                onDismiss()
                            }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Replay, null, Modifier.size(14.dp), tint = Color.White.copy(alpha = 0.8f))
                            Spacer(Modifier.width(4.dp))
                            Text(label, color = Color.White, fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // Programs list
            if (loading) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(Modifier.size(36.dp), color = scheme.primary)
                        Spacer(Modifier.height(10.dp))
                        Text("טוען לוח שידורים...", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
                    }
                }
            } else {
                val list = programs.orEmpty()
                if (list.isEmpty()) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Rounded.History, null, Modifier.size(40.dp), tint = Color.White.copy(alpha = 0.35f))
                            Spacer(Modifier.height(8.dp))
                            Text("אין לוח שידורים זמין לערוץ זה כרגע", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                            Text("ניתן להשתמש בכפתורי הדילוג המהיר למעלה לחזרה אחורה", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                        }
                    }
                } else {
                    LazyColumn(
                        Modifier.fillMaxWidth().heightIn(max = 480.dp),
                        contentPadding = PaddingValues(bottom = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        items(list, key = { "${it.startMin}_${it.title}" }) { prog ->
                            CatchupProgramCard(
                                program = prog,
                                onClick = {
                                    onSelectProgram(prog)
                                    onDismiss()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CatchupProgramCard(
    program: CatchupProgram,
    onClick: () -> Unit,
) {
    val isLive = program.isLiveNow
    val cardBg = if (isLive) Color(0xFF1E2838) else Color(0xFF181E2C)
    val borderCol = if (isLive) Color(0xFF4C8DFF).copy(alpha = 0.6f) else Color.White.copy(alpha = 0.08f)

    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(cardBg)
            .border(1.dp, borderCol, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Thumbnail poster
        if (program.poster.isNotEmpty()) {
            AsyncImage(
                model = program.poster,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(width = 64.dp, height = 48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.3f))
            )
            Spacer(Modifier.width(12.dp))
        }

        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    program.timeLabel,
                    color = if (isLive) Color(0xFF64B5F6) else Color.White.copy(alpha = 0.65f),
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.width(8.dp))
                Box(
                    Modifier
                        .clip(CircleShape)
                        .background(if (isLive) Color(0xFF2E7D32).copy(alpha = 0.25f) else Color.White.copy(alpha = 0.1f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        program.relativeLabel,
                        color = if (isLive) Color(0xFF81C784) else Color.White.copy(alpha = 0.7f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            Spacer(Modifier.height(3.dp))

            Text(
                program.title,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            if (program.description.isNotEmpty() && program.description != program.title) {
                Spacer(Modifier.height(2.dp))
                Text(
                    program.description,
                    color = Color.White.copy(alpha = 0.55f),
                    fontSize = 11.5.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        Spacer(Modifier.width(8.dp))

        // Play action pill
        Box(
            Modifier
                .clip(RoundedCornerShape(50))
                .background(if (isLive) Color(0xFF1E88E5) else Color.White.copy(alpha = 0.14f))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Rounded.PlayArrow,
                    null,
                    Modifier.size(16.dp),
                    tint = Color.White,
                )
                Spacer(Modifier.width(3.dp))
                Text(
                    if (isLive) "מההתחלה" else "צפה",
                    color = Color.White,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

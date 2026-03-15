package com.noxis.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import kotlinx.coroutines.delay

enum class NotifType { INFO, SUCCESS, ERROR, WARNING }

data class NoxisNotif(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val message: String = "",
    val type: NotifType = NotifType.INFO,
    val durationMs: Long = 3000L
)

@Composable
fun NotificationLayer(notifs: MutableList<NoxisNotif>, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.Bottom), horizontalAlignment = Alignment.End) {
        notifs.toList().forEach { n ->
            key(n.id) { NotifItem(n) { notifs.remove(n) } }
        }
    }
}

@Composable
fun NotifItem(n: NoxisNotif, onDismiss: () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
        delay(n.durationMs)
        visible = false
        delay(250)
        onDismiss()
    }
    AnimatedVisibility(
        visible = visible,
        enter = slideInHorizontally(animationSpec = tween(200)) { it } + fadeIn(tween(200)),
        exit = slideOutHorizontally(animationSpec = tween(200)) { it } + fadeOut(tween(200))
    ) {
        val accent = when (n.type) {
            NotifType.SUCCESS -> Color(0xFF4CAF50)
            NotifType.ERROR   -> Color(0xFFFF5252)
            NotifType.WARNING -> Color(0xFFFFB300)
            NotifType.INFO    -> Color(0xFF2196F3)
        }
        Row(
            modifier = Modifier.width(260.dp).shadow(6.dp, RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp)).background(Color(0xFF2A2A2A)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier.width(4.dp).height(if (n.message.isNotEmpty()) 52.dp else 38.dp).background(accent))
            Column(Modifier.weight(1f).padding(horizontal = 10.dp, vertical = 6.dp)) {
                Text(n.title, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                if (n.message.isNotEmpty()) Text(n.message, color = Color.White.copy(0.65f), fontSize = 11.sp)
            }
        }
    }
}

fun MutableList<NoxisNotif>.notify(title: String, message: String = "", type: NotifType = NotifType.INFO) {
    add(NoxisNotif(title = title, message = message, type = type))
}

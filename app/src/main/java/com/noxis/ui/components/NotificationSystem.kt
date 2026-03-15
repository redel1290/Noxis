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
import androidx.compose.ui.unit.*
import kotlinx.coroutines.delay

enum class NotificationType { INFO, SUCCESS, ERROR, WARNING }

data class NoxisNotification(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val message: String = "",
    val type: NotificationType = NotificationType.INFO,
    val durationMs: Long = 3000L
)

@Composable
fun NotificationLayer(
    notifications: MutableList<NoxisNotification>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Bottom),
        horizontalAlignment = Alignment.End
    ) {
        notifications.toList().forEach { notification ->
            key(notification.id) {
                NotificationItem(
                    notification = notification,
                    onDismiss = { notifications.remove(notification) }
                )
            }
        }
    }
}

@Composable
fun NotificationItem(
    notification: NoxisNotification,
    onDismiss: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
        delay(notification.durationMs)
        visible = false
        delay(300)
        onDismiss()
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInHorizontally(
            initialOffsetX = { it },
            animationSpec = tween(300)
        ) + fadeIn(),
        exit = slideOutHorizontally(
            targetOffsetX = { it },
            animationSpec = tween(300)
        ) + fadeOut()
    ) {
        val (bgColor, accentColor) = when (notification.type) {
            NotificationType.SUCCESS -> Pair(Color(0xFF1E3A2F), Color(0xFF4CAF50))
            NotificationType.ERROR -> Pair(Color(0xFF3A1E1E), Color(0xFFFF5252))
            NotificationType.WARNING -> Pair(Color(0xFF3A2E1E), Color(0xFFFFB300))
            NotificationType.INFO -> Pair(Color(0xFF1E2A3A), Color(0xFF2196F3))
        }

        Row(
            modifier = Modifier
                .width(280.dp)
                .shadow(8.dp, RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
                .background(bgColor)
                .padding(0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Кольорова смуга зліва
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(if (notification.message.isNotEmpty()) 56.dp else 40.dp)
                    .background(accentColor)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = notification.title,
                    color = Color.White,
                    fontSize = 13.sp,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                    )
                )
                if (notification.message.isNotEmpty()) {
                    Text(
                        text = notification.message,
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

// Хелпер для виклику нотифікацій
fun MutableList<NoxisNotification>.notify(
    title: String,
    message: String = "",
    type: NotificationType = NotificationType.INFO
) {
    add(NoxisNotification(title = title, message = message, type = type))
}

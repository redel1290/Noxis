package com.noxis.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import com.noxis.ui.window.WindowManager
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun Taskbar(
    windowManager: WindowManager,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    var currentTime by remember { mutableStateOf(getCurrentTime()) }

    // Оновлення часу кожну хвилину
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(30_000)
            currentTime = getCurrentTime()
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(
                if (isDark) Color(0xCC1A1A1A) else Color(0xCCEFEFEF)
            )
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Кнопка Пуск
        StartButton()

        Spacer(modifier = Modifier.width(4.dp))

        // Відкриті вікна
        Row(
            modifier = Modifier
                .weight(1f)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            windowManager.windows.forEach { window ->
                TaskbarItem(
                    title = window.title,
                    icon = window.icon,
                    isActive = window.isFocused && !window.isMinimized,
                    isDark = isDark,
                    onClick = {
                        if (window.isMinimized) {
                            windowManager.restoreWindow(window.id)
                        } else if (window.isFocused) {
                            windowManager.minimizeWindow(window.id)
                        } else {
                            windowManager.bringToFront(window.id)
                        }
                    }
                )
            }
        }

        // Час
        Text(
            text = currentTime,
            fontSize = 12.sp,
            color = if (isDark) Color.White else Color.Black,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
    }
}

@Composable
fun StartButton() {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(
                Icons.Default.Apps,
                contentDescription = "Пуск",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
        }

        // Меню пуск (поки заглушка)
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Провідник") },
                onClick = { expanded = false }
            )
            DropdownMenuItem(
                text = { Text("Налаштування") },
                onClick = { expanded = false }
            )
            HorizontalDivider()
            DropdownMenuItem(
                text = { Text("Вимкнути") },
                onClick = { expanded = false }
            )
        }
    }
}

@Composable
fun TaskbarItem(
    title: String,
    icon: String,
    isActive: Boolean,
    isDark: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .height(36.dp)
            .widthIn(min = 80.dp, max = 160.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(
                if (isActive)
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                else
                    Color.Transparent
            )
            .border(
                width = if (isActive) 1.dp else 0.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                shape = RoundedCornerShape(4.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = icon, fontSize = 14.sp)
            Text(
                text = title,
                fontSize = 12.sp,
                color = if (isDark) Color.White else Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

fun getCurrentTime(): String {
    return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
}

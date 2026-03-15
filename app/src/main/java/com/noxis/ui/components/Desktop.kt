package com.noxis.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import com.noxis.ui.window.WindowManager

data class DesktopIcon(
    val id: String,
    val name: String,
    val icon: String,
    val gridX: Int,  // колонка в сітці
    val gridY: Int   // рядок в сітці
)

@Composable
fun Desktop(
    windowManager: WindowManager,
    modifier: Modifier = Modifier
) {
    // Іконки на столі (початкові)
    val icons = remember {
        mutableStateListOf(
            DesktopIcon("files", "Провідник", "📁", 0, 0),
            DesktopIcon("settings", "Налаштування", "⚙️", 0, 1),
            DesktopIcon("notepad", "Блокнот", "📝", 0, 2),
            DesktopIcon("browser", "Браузер", "🌐", 0, 3),
        )
    }

    var selectedId by remember { mutableStateOf<String?>(null) }
    var contextMenu by remember { mutableStateOf<DesktopIcon?>(null) }
    var contextMenuOffset by remember { mutableStateOf(Offset.Zero) }

    BoxWithConstraints(modifier = modifier) {
        val cellSize = 80.dp

        // Іконки за сіткою
        icons.forEach { icon ->
            val x = (icon.gridX * (cellSize.value + 8)).dp
            val y = (icon.gridY * (cellSize.value + 16)).dp

            DesktopIconView(
                icon = icon,
                isSelected = selectedId == icon.id,
                modifier = Modifier.offset(x = x, y = y),
                onClick = { selectedId = icon.id },
                onDoubleClick = {
                    selectedId = null
                    // TODO: відкрити програму
                },
                onLongPress = { offset ->
                    contextMenu = icon
                    contextMenuOffset = offset
                }
            )
        }

        // ПКМ на порожньому місці
        Box(
            modifier = Modifier
                .fillMaxSize()
                .combinedClickable(
                    onClick = { selectedId = null },
                    onLongClick = {
                        // Контекстне меню робочого столу
                    }
                )
        )

        // Контекстне меню іконки
        contextMenu?.let { icon ->
            DropdownMenu(
                expanded = true,
                onDismissRequest = { contextMenu = null },
                offset = DpOffset(contextMenuOffset.x.dp, contextMenuOffset.y.dp)
            ) {
                DropdownMenuItem(
                    text = { Text("Відкрити") },
                    onClick = { contextMenu = null }
                )
                DropdownMenuItem(
                    text = { Text("Прибрати зі столу") },
                    onClick = {
                        icons.remove(icon)
                        contextMenu = null
                    }
                )
            }
        }
    }
}

@Composable
fun DesktopIconView(
    icon: DesktopIcon,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onDoubleClick: () -> Unit,
    onLongPress: (Offset) -> Unit
) {
    Column(
        modifier = modifier
            .size(80.dp, 90.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(
                if (isSelected) Color.White.copy(alpha = 0.2f)
                else Color.Transparent
            )
            .combinedClickable(
                onClick = onClick,
                onDoubleClick = onDoubleClick,
                onLongClick = { onLongPress(Offset.Zero) }
            )
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = icon.icon, fontSize = 36.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = icon.name,
            fontSize = 11.sp,
            color = Color.White,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = LocalTextStyle.current.copy(
                shadow = androidx.compose.ui.graphics.Shadow(
                    color = Color.Black.copy(alpha = 0.8f),
                    blurRadius = 4f
                )
            )
        )
    }
}

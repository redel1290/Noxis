package com.noxis.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import com.noxis.ui.window.WindowManager

data class DesktopIcon(
    val id: String,
    val name: String,
    val icon: String,
    val gridX: Int,
    val gridY: Int
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Desktop(
    windowManager: WindowManager,
    modifier: Modifier = Modifier
) {
    val icons = remember {
        mutableStateListOf(
            DesktopIcon("files", "Провідник", "📁", 0, 0),
            DesktopIcon("settings", "Налаштування", "⚙️", 0, 1),
            DesktopIcon("notepad", "Блокнот", "📝", 0, 2),
            DesktopIcon("browser", "Браузер", "🌐", 0, 3),
        )
    }

    var selectedId by remember { mutableStateOf<String?>(null) }
    var contextMenuIcon by remember { mutableStateOf<DesktopIcon?>(null) }

    BoxWithConstraints(modifier = modifier) {
        val cellSize = 80.dp

        icons.forEach { icon ->
            val x = (icon.gridX * (cellSize.value + 8)).dp
            val y = (icon.gridY * (cellSize.value + 16)).dp

            DesktopIconView(
                icon = icon,
                isSelected = selectedId == icon.id,
                modifier = Modifier.offset(x = x, y = y),
                onClick = { selectedId = icon.id },
                onDoubleClick = { selectedId = null },
                onLongPress = { contextMenuIcon = icon }
            )
        }

        contextMenuIcon?.let { icon ->
            DropdownMenu(
                expanded = true,
                onDismissRequest = { contextMenuIcon = null }
            ) {
                DropdownMenuItem(
                    text = { Text("Відкрити") },
                    onClick = { contextMenuIcon = null }
                )
                DropdownMenuItem(
                    text = { Text("Прибрати зі столу") },
                    onClick = {
                        icons.remove(icon)
                        contextMenuIcon = null
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DesktopIconView(
    icon: DesktopIcon,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onDoubleClick: () -> Unit,
    onLongPress: () -> Unit
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
                onLongClick = onLongPress
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
                shadow = Shadow(
                    color = Color.Black.copy(alpha = 0.8f),
                    blurRadius = 4f
                )
            )
        )
    }
}

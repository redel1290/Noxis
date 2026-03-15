package com.noxis.ui.components

import androidx.compose.foundation.*
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

data class DesktopIcon(val id: String, val name: String, val icon: String, val col: Int, val row: Int)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Desktop(
    icons: List<DesktopIcon>,
    onOpen: (DesktopIcon) -> Unit,
    onRemove: (DesktopIcon) -> Unit,
    modifier: Modifier = Modifier
) {
    var selected by remember { mutableStateOf<String?>(null) }
    var ctxMenu by remember { mutableStateOf<DesktopIcon?>(null) }
    var showDeskMenu by remember { mutableStateOf(false) }

    Box(
        modifier = modifier.combinedClickable(
            onClick = { selected = null; showDeskMenu = false },
            onLongClick = { showDeskMenu = true }
        )
    ) {
        // ПКМ на столі
        DropdownMenu(expanded = showDeskMenu, onDismissRequest = { showDeskMenu = false }) {
            DropdownMenuItem(text = { Text("Оновити", fontSize = 12.sp) }, onClick = { showDeskMenu = false })
            DropdownMenuItem(text = { Text("Змінити шпалери", fontSize = 12.sp) }, onClick = { showDeskMenu = false })
        }

        icons.forEach { icon ->
            val x = (icon.col * 76).dp
            val y = (icon.row * 80).dp
            Box(Modifier.offset(x = x, y = y)) {
                Column(
                    modifier = Modifier.size(68.dp, 76.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (selected == icon.id) Color.White.copy(0.18f) else Color.Transparent)
                        .combinedClickable(
                            onClick = { selected = icon.id },
                            onDoubleClick = { selected = null; onOpen(icon) },
                            onLongClick = { ctxMenu = icon }
                        )
                        .padding(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(icon.icon, fontSize = 28.sp)
                    Spacer(Modifier.height(2.dp))
                    Text(
                        icon.name, fontSize = 10.sp, color = Color.White,
                        textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis,
                        style = LocalTextStyle.current.copy(shadow = Shadow(Color.Black.copy(0.85f), blurRadius = 4f))
                    )
                }
            }
        }

        // ПКМ на іконці
        ctxMenu?.let { ic ->
            DropdownMenu(expanded = true, onDismissRequest = { ctxMenu = null }) {
                DropdownMenuItem(text = { Text("Відкрити", fontSize = 12.sp) }, onClick = { onOpen(ic); ctxMenu = null })
                DropdownMenuItem(text = { Text("Прибрати зі столу", fontSize = 12.sp) }, onClick = { onRemove(ic); ctxMenu = null })
            }
        }
    }
}

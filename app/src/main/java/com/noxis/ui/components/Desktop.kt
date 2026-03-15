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
    onIconDoubleClick: (DesktopIcon) -> Unit,
    onIconRemove: (DesktopIcon) -> Unit,
    modifier: Modifier = Modifier
) {
    var selected by remember { mutableStateOf<String?>(null) }
    var ctxMenu by remember { mutableStateOf<DesktopIcon?>(null) }
    var showDesktopMenu by remember { mutableStateOf(false) }

    Box(
        modifier = modifier.combinedClickable(
            onClick = { selected = null },
            onLongClick = { showDesktopMenu = true }
        )
    ) {
        // ПКМ на робочому столі
        DropdownMenu(expanded = showDesktopMenu, onDismissRequest = { showDesktopMenu = false }) {
            DropdownMenuItem(text = { Text("Оновити") }, onClick = { showDesktopMenu = false })
            DropdownMenuItem(text = { Text("Змінити шпалери") }, onClick = { showDesktopMenu = false })
        }

        icons.forEach { icon ->
            val x = (icon.col * 88).dp
            val y = (icon.row * 96).dp

            Box(modifier = Modifier.offset(x = x, y = y)) {
                Column(
                    modifier = Modifier
                        .size(80.dp, 88.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (selected == icon.id) Color.White.copy(0.22f) else Color.Transparent)
                        .combinedClickable(
                            onClick = { selected = icon.id },
                            onDoubleClick = { selected = null; onIconDoubleClick(icon) },
                            onLongClick = { ctxMenu = icon }
                        )
                        .padding(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(icon.icon, fontSize = 34.sp)
                    Spacer(Modifier.height(3.dp))
                    Text(
                        icon.name, fontSize = 11.sp, color = Color.White,
                        textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis,
                        style = LocalTextStyle.current.copy(shadow = Shadow(Color.Black.copy(0.9f), blurRadius = 5f))
                    )
                }
            }
        }

        // ПКМ на іконці
        ctxMenu?.let { ic ->
            DropdownMenu(expanded = true, onDismissRequest = { ctxMenu = null }) {
                DropdownMenuItem(text = { Text("Відкрити") }, onClick = { onIconDoubleClick(ic); ctxMenu = null })
                DropdownMenuItem(text = { Text("Прибрати зі столу") }, onClick = { onIconRemove(ic); ctxMenu = null })
            }
        }
    }
}

package com.noxis.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import com.noxis.ui.window.WindowManager
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun Taskbar(
    manager: WindowManager,
    isDark: Boolean,
    onOpenFileManager: () -> Unit,
    onOpenNotepad: () -> Unit,
    onOpenBrowser: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showStart by remember { mutableStateOf(false) }
    var time by remember { mutableStateOf(getTime()) }
    LaunchedEffect(Unit) {
        while (true) { kotlinx.coroutines.delay(30_000); time = getTime() }
    }

    val bg = if (isDark) Color(0xDD1A1A1A) else Color(0xDDEEEEEE)
    val fg = if (isDark) Color.White else Color.Black

    Box {
        Row(
            modifier = modifier.fillMaxWidth().height(48.dp).background(bg).padding(horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Start button
            Box {
                Box(
                    modifier = Modifier.size(38.dp).clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable { showStart = !showStart },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Menu, null, tint = Color.White, modifier = Modifier.size(22.dp))
                }
                DropdownMenu(expanded = showStart, onDismissRequest = { showStart = false }) {
                    Text("  Noxis", fontWeight = FontWeight.Bold, fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
                    HorizontalDivider()
                    DropdownMenuItem(
                        leadingIcon = { Icon(Icons.Default.Folder, null) },
                        text = { Text("Провідник") },
                        onClick = { showStart = false; onOpenFileManager() }
                    )
                    DropdownMenuItem(
                        leadingIcon = { Icon(Icons.Default.Edit, null) },
                        text = { Text("Блокнот") },
                        onClick = { showStart = false; onOpenNotepad() }
                    )
                    DropdownMenuItem(
                        leadingIcon = { Icon(Icons.Default.Search, null) },
                        text = { Text("Браузер") },
                        onClick = { showStart = false; onOpenBrowser() }
                    )
                    DropdownMenuItem(
                        leadingIcon = { Icon(Icons.Default.Settings, null) },
                        text = { Text("Налаштування") },
                        onClick = { showStart = false; onOpenSettings() }
                    )
                    HorizontalDivider()
                    DropdownMenuItem(
                        leadingIcon = { Icon(Icons.Default.Close, null, tint = Color(0xFFFF605C)) },
                        text = { Text("Вимкнути", color = Color(0xFFFF605C)) },
                        onClick = { showStart = false; android.os.Process.killProcess(android.os.Process.myPid()) }
                    )
                }
            }

            Spacer(Modifier.width(6.dp))

            // Швидкий доступ
            QuickBtn(Icons.Default.Folder) { onOpenFileManager() }
            QuickBtn(Icons.Default.Edit) { onOpenNotepad() }
            QuickBtn(Icons.Default.Search) { onOpenBrowser() }

            Spacer(Modifier.width(6.dp))
            HorizontalDivider(modifier = Modifier.width(1.dp).height(28.dp), color = fg.copy(0.2f))
            Spacer(Modifier.width(6.dp))

            // Відкриті вікна
            Row(
                modifier = Modifier.weight(1f).horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                manager.windows.forEach { w ->
                    val active = w.isFocused && !w.isMinimized
                    Box(
                        modifier = Modifier.height(34.dp).widthIn(min = 80.dp, max = 150.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (active) MaterialTheme.colorScheme.primary.copy(0.25f) else Color.Transparent)
                            .border(1.dp,
                                if (active) MaterialTheme.colorScheme.primary.copy(0.6f) else Color.Transparent,
                                RoundedCornerShape(4.dp))
                            .clickable {
                                if (w.isMinimized) manager.restore(w.id)
                                else if (w.isFocused) manager.minimize(w.id)
                                else manager.bringToFront(w.id)
                            }
                            .padding(horizontal = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(w.icon, fontSize = 13.sp)
                            Text(w.title, fontSize = 11.sp, color = fg, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }
            }

            // Час
            Text(time, fontSize = 12.sp, color = fg, modifier = Modifier.padding(horizontal = 12.dp))
        }
    }
}

@Composable
fun QuickBtn(icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Box(
        modifier = Modifier.size(36.dp).clip(RoundedCornerShape(4.dp)).clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, null, modifier = Modifier.size(18.dp))
    }
}

fun getTime(): String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

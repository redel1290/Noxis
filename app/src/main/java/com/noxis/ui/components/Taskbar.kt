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
    mgr: WindowManager,
    isDark: Boolean,
    onOpenFiles: () -> Unit,
    onOpenNotepad: () -> Unit,
    onOpenBrowser: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showStart by remember { mutableStateOf(false) }
    var time by remember { mutableStateOf(winTime()) }
    LaunchedEffect(Unit) { while (true) { kotlinx.coroutines.delay(30_000); time = winTime() } }

    val bg = if (isDark) Color(0xF0202020) else Color(0xF0F3F3F3)
    val fg = if (isDark) Color(0xFFE8E8E8) else Color(0xFF1A1A1A)
    val divider = if (isDark) Color(0xFF3D3D3D) else Color(0xFFE0E0E0)

    Box(modifier = modifier) {
        // Старт меню
        if (showStart) {
            StartMenu(
                isDark = isDark,
                onDismiss = { showStart = false },
                onOpenFiles = { showStart = false; onOpenFiles() },
                onOpenNotepad = { showStart = false; onOpenNotepad() },
                onOpenBrowser = { showStart = false; onOpenBrowser() },
                onOpenSettings = { showStart = false; onOpenSettings() }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth().height(40.dp)
                .background(bg)
                .border(BorderStroke(1.dp, divider), RoundedCornerShape(0.dp))
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Кнопка Пуск
            Box(
                modifier = Modifier.size(36.dp, 32.dp).clip(RoundedCornerShape(4.dp))
                    .background(if (showStart) MaterialTheme.colorScheme.primary.copy(0.15f) else Color.Transparent)
                    .clickable { showStart = !showStart },
                contentAlignment = Alignment.Center
            ) {
                // Windows logo — квадрати
                WinLogo()
            }

            Spacer(Modifier.width(2.dp))

            // Пошук
            Box(
                modifier = Modifier.width(140.dp).height(28.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (isDark) Color(0xFF2D2D2D) else Color(0xFFEAEAEA))
                    .border(1.dp, divider, RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(Modifier.padding(horizontal = 6.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.Search, null, modifier = Modifier.size(13.dp), tint = fg.copy(0.5f))
                    Text("Пошук", fontSize = 11.sp, color = fg.copy(0.4f))
                }
            }

            Spacer(Modifier.width(4.dp))
            Box(Modifier.width(1.dp).height(24.dp).background(divider))
            Spacer(Modifier.width(4.dp))

            // Швидкий запуск
            TbQuickBtn(isDark) { onOpenFiles() }; Icon(Icons.Default.Folder, null, modifier = Modifier.size(15.dp).clickable { onOpenFiles() }, tint = Color(0xFFFFC107))
            Spacer(Modifier.width(2.dp))
            TbQuickBtn(isDark) { onOpenBrowser() }; Icon(Icons.Default.Search, null, modifier = Modifier.size(15.dp).clickable { onOpenBrowser() }, tint = Color(0xFF4CAF50))
            Spacer(Modifier.width(2.dp))
            TbQuickBtn(isDark) { onOpenNotepad() }; Icon(Icons.Default.Edit, null, modifier = Modifier.size(15.dp).clickable { onOpenNotepad() }, tint = fg.copy(0.7f))

            Spacer(Modifier.width(4.dp))
            Box(Modifier.width(1.dp).height(24.dp).background(divider))
            Spacer(Modifier.width(4.dp))

            // Відкриті вікна
            Row(
                modifier = Modifier.weight(1f).horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                mgr.windows.forEach { w ->
                    val active = w.isFocused && !w.isMinimized
                    Box(
                        modifier = Modifier.height(32.dp).widthIn(min = 60.dp, max = 120.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                if (active) MaterialTheme.colorScheme.primary.copy(0.15f)
                                else if (isDark) Color(0xFF2D2D2D) else Color(0xFFE8E8E8)
                            )
                            .border(1.dp,
                                if (active) MaterialTheme.colorScheme.primary.copy(0.4f) else divider,
                                RoundedCornerShape(4.dp))
                            .clickable {
                                if (w.isMinimized) mgr.restore(w.id)
                                else if (w.isFocused) mgr.minimize(w.id)
                                else mgr.bringToFront(w.id)
                            }
                            .padding(horizontal = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                            Text(w.icon, fontSize = 11.sp)
                            Text(w.title, fontSize = 10.sp, color = fg, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }
            }

            // Системний трей — час
            Column(
                modifier = Modifier.clickable { }.padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text(time.split(" ")[0], fontSize = 11.sp, color = fg, fontWeight = FontWeight.Medium)
                Text(time.split(" ").getOrElse(1) { "" }, fontSize = 10.sp, color = fg.copy(0.7f))
            }
        }
    }
}

@Composable
fun TbQuickBtn(isDark: Boolean, onClick: () -> Unit) {
    // Порожній контейнер (іконка рендериться окремо)
}

@Composable
fun WinLogo() {
    val c = MaterialTheme.colorScheme.primary
    Box(Modifier.size(18.dp)) {
        Box(Modifier.size(8.dp).offset(0.dp, 0.dp).background(c))
        Box(Modifier.size(8.dp).offset(10.dp, 0.dp).background(c))
        Box(Modifier.size(8.dp).offset(0.dp, 10.dp).background(c))
        Box(Modifier.size(8.dp).offset(10.dp, 10.dp).background(c))
    }
}

@Composable
fun StartMenu(
    isDark: Boolean,
    onDismiss: () -> Unit,
    onOpenFiles: () -> Unit,
    onOpenNotepad: () -> Unit,
    onOpenBrowser: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val bg = if (isDark) Color(0xFF2B2B2B) else Color(0xFFF8F8F8)
    val fg = if (isDark) Color(0xFFE8E8E8) else Color(0xFF1A1A1A)

    Box(modifier = Modifier.fillMaxSize().clickable(onClick = onDismiss)) {
        Box(
            modifier = Modifier.width(280.dp).wrapContentHeight()
                .align(Alignment.BottomStart).offset(x = 4.dp, y = (-42.dp))
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomEnd = 8.dp))
                .background(bg)
                .border(1.dp, if (isDark) Color(0xFF3D3D3D) else Color(0xFFE0E0E0),
                    RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomEnd = 8.dp))
                .clickable { } // Блокуємо прохід кліку
        ) {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text("Noxis", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = fg,
                    modifier = Modifier.padding(bottom = 8.dp))
                HorizontalDivider(color = if (isDark) Color(0xFF3D3D3D) else Color(0xFFE0E0E0))
                Spacer(Modifier.height(4.dp))
                StartMenuItem(Icons.Default.Folder, "Провідник", Color(0xFFFFC107), fg, onOpenFiles)
                StartMenuItem(Icons.Default.Search, "Браузер", Color(0xFF4CAF50), fg, onOpenBrowser)
                StartMenuItem(Icons.Default.Edit, "Блокнот", fg.copy(0.7f), fg, onOpenNotepad)
                StartMenuItem(Icons.Default.Settings, "Налаштування", fg.copy(0.6f), fg, onOpenSettings)
                HorizontalDivider(color = if (isDark) Color(0xFF3D3D3D) else Color(0xFFE0E0E0), modifier = Modifier.padding(vertical = 4.dp))
                StartMenuItem(Icons.Default.Close, "Вимкнути", Color(0xFFFF605C), fg) {
                    android.os.Process.killProcess(android.os.Process.myPid())
                }
            }
        }
    }
}

@Composable
fun StartMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    iconTint: Color,
    textColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(4.dp))
            .clickable(onClick = onClick).padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(icon, null, modifier = Modifier.size(16.dp), tint = iconTint)
        Text(label, fontSize = 12.sp, color = textColor)
    }
}

fun winTime(): String {
    val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
    val date = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date())
    return "$time $date"
}

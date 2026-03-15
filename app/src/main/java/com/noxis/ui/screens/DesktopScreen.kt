package com.noxis.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.noxis.data.NoxisPreferences
import com.noxis.ui.components.*
import com.noxis.ui.window.WindowLayer
import com.noxis.ui.window.WindowManager

@Composable
fun DesktopScreen(prefs: NoxisPreferences) {
    val isDark by prefs.isDarkTheme.collectAsState(initial = false)
    val mgr = remember { WindowManager() }
    val notifs = remember { mutableStateListOf<NoxisNotif>() }
    val icons = remember {
        mutableStateListOf(
            DesktopIcon("files", "Провідник", "📁", 0, 0),
            DesktopIcon("notepad", "Блокнот", "📝", 0, 1),
            DesktopIcon("browser", "Браузер", "🌐", 0, 2),
            DesktopIcon("settings", "Налаштування", "⚙️", 0, 3),
        )
    }

    fun openFiles() = mgr.open("files", "Провідник", "📁", DpSize(480.dp, 300.dp)) { FileManagerScreen() }
    fun openNotepad() = mgr.open("notepad", "Блокнот", "📝", DpSize(380.dp, 260.dp)) { NotepadScreen() }
    fun openBrowser() = mgr.open("browser", "Браузер", "🌐", DpSize(560.dp, 340.dp)) { BrowserScreen() }
    fun openSettings() = mgr.open("settings", "Налаштування", "⚙️", DpSize(340.dp, 300.dp)) { SettingsScreen(prefs) }

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val sw = maxWidth
        val sh = maxHeight

        // Шпалери
        Box(Modifier.fillMaxSize().background(
            Brush.linearGradient(listOf(Color(0xFF0078D4), Color(0xFF005A9E), Color(0xFF003A6E)))
        ))

        // Іконки
        Desktop(
            icons = icons,
            onOpen = { when (it.id) { "files" -> openFiles(); "notepad" -> openNotepad(); "browser" -> openBrowser(); "settings" -> openSettings() } },
            onRemove = { icons.remove(it) },
            modifier = Modifier.fillMaxSize().padding(bottom = 40.dp).padding(8.dp)
        )

        // Вікна
        WindowLayer(mgr = mgr, sw = sw, sh = sh)

        // Нотифікації
        NotificationLayer(notifs = notifs, modifier = Modifier.align(Alignment.BottomEnd).padding(bottom = 48.dp, end = 6.dp))

        // Таскбар знизу
        Taskbar(
            mgr = mgr, isDark = isDark,
            onOpenFiles = ::openFiles, onOpenNotepad = ::openNotepad,
            onOpenBrowser = ::openBrowser, onOpenSettings = ::openSettings,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

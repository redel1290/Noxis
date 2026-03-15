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
import com.noxis.ui.window.WindowManager
import com.noxis.ui.window.WindowLayer

@Composable
fun DesktopScreen(prefs: NoxisPreferences) {
    val isDark by prefs.isDarkTheme.collectAsState(initial = false)
    val manager = remember { WindowManager() }
    val notifs = remember { mutableStateListOf<NoxisNotif>() }
    val desktopIcons = remember {
        mutableStateListOf(
            DesktopIcon("files", "Провідник", "📁", 0, 0),
            DesktopIcon("notepad", "Блокнот", "📝", 0, 1),
            DesktopIcon("browser", "Браузер", "🌐", 0, 2),
            DesktopIcon("settings", "Налаштування", "⚙️", 0, 3),
        )
    }

    fun openFileManager() = manager.open("files", "Провідник", "📁", DpSize(600.dp, 400.dp)) {
        FileManagerScreen()
    }
    fun openNotepad() = manager.open("notepad", "Блокнот", "📝", DpSize(500.dp, 350.dp)) {
        NotepadScreen()
    }
    fun openBrowser() = manager.open("browser", "Браузер", "🌐", DpSize(700.dp, 450.dp)) {
        BrowserScreen()
    }
    fun openSettings() = manager.open("settings", "Налаштування", "⚙️", DpSize(450.dp, 400.dp)) {
        SettingsScreen(prefs)
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val sw = maxWidth
        val sh = maxHeight

        // Шпалери
        Box(
            Modifier.fillMaxSize().background(
                Brush.linearGradient(listOf(Color(0xFF00C6FF), Color(0xFF0072FF), Color(0xFF7F00FF), Color(0xFFE100FF)))
            )
        )

        // Іконки на столі
        Desktop(
            icons = desktopIcons,
            onIconDoubleClick = { icon ->
                when (icon.id) {
                    "files" -> openFileManager()
                    "notepad" -> openNotepad()
                    "browser" -> openBrowser()
                    "settings" -> openSettings()
                }
            },
            onIconRemove = { desktopIcons.remove(it) },
            modifier = Modifier.fillMaxSize().padding(bottom = 48.dp)
        )

        // Вікна
        WindowLayer(manager = manager, screenW = sw, screenH = sh)

        // Нотифікації
        NotificationLayer(
            notifs = notifs,
            modifier = Modifier.align(Alignment.BottomEnd).padding(bottom = 56.dp, end = 8.dp)
        )

        // Таскбар
        Taskbar(
            manager = manager,
            isDark = isDark,
            onOpenFileManager = ::openFileManager,
            onOpenNotepad = ::openNotepad,
            onOpenBrowser = ::openBrowser,
            onOpenSettings = ::openSettings,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

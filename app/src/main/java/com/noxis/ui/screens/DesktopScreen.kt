package com.noxis.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.noxis.data.NoxisPreferences
import com.noxis.ui.components.*
import com.noxis.ui.window.WindowLayer
import com.noxis.ui.window.WindowManager

@Composable
fun DesktopScreen(prefs: NoxisPreferences) {
    val isDark by prefs.isDarkTheme.collectAsState(initial = false)
    val windowManager = remember { WindowManager() }
    val notifications = remember { mutableStateListOf<NoxisNotification>() }

    Box(modifier = Modifier.fillMaxSize()) {

        Wallpaper()

        Desktop(
            windowManager = windowManager,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 48.dp)
        )

        WindowLayer(windowManager = windowManager)

        NotificationLayer(
            notifications = notifications,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 56.dp, end = 8.dp)
        )

        Taskbar(
            windowManager = windowManager,
            isDark = isDark,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun Wallpaper() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF00C6FF),
                        Color(0xFF0072FF),
                        Color(0xFF7F00FF),
                        Color(0xFFE100FF)
                    )
                )
            )
    )
}

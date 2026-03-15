package com.noxis.ui.window

import androidx.compose.runtime.*
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

data class NoxisWindow(
    val id: String,
    val title: String,
    val icon: String = "📄",
    val position: DpOffset = DpOffset(60.dp, 40.dp),
    val size: DpSize = DpSize(500.dp, 350.dp),
    val isMinimized: Boolean = false,
    val isMaximized: Boolean = false,
    val isFocused: Boolean = true,
    val content: @Composable () -> Unit
)

class WindowManager {
    val windows = mutableStateListOf<NoxisWindow>()
    private val MAX_WINDOWS = 15

    fun open(id: String, title: String, icon: String, size: DpSize = DpSize(500.dp, 350.dp), content: @Composable () -> Unit) {
        if (windows.size >= MAX_WINDOWS) return
        // Якщо вже відкрите — просто фокусуємо
        val existing = windows.indexOfFirst { it.id == id }
        if (existing >= 0) {
            bringToFront(id)
            return
        }
        val offset = DpOffset((60 + windows.size * 30).dp, (40 + windows.size * 20).dp)
        unfocusAll()
        windows.add(NoxisWindow(id = id, title = title, icon = icon, position = offset, size = size, isFocused = true, content = content))
    }

    fun close(id: String) = windows.removeAll { it.id == id }

    fun minimize(id: String) {
        val i = windows.indexOfFirst { it.id == id }
        if (i >= 0) windows[i] = windows[i].copy(isMinimized = true, isFocused = false)
    }

    fun toggleMaximize(id: String) {
        val i = windows.indexOfFirst { it.id == id }
        if (i >= 0) windows[i] = windows[i].copy(isMaximized = !windows[i].isMaximized)
    }

    fun restore(id: String) {
        val i = windows.indexOfFirst { it.id == id }
        if (i >= 0) {
            unfocusAll()
            windows[i] = windows[i].copy(isMinimized = false, isFocused = true)
        }
    }

    fun bringToFront(id: String) {
        val w = windows.find { it.id == id } ?: return
        windows.remove(w)
        unfocusAll()
        windows.add(w.copy(isFocused = true, isMinimized = false))
    }

    fun updatePosition(id: String, pos: DpOffset) {
        val i = windows.indexOfFirst { it.id == id }
        if (i >= 0) windows[i] = windows[i].copy(position = pos)
    }

    private fun unfocusAll() {
        for (i in windows.indices) windows[i] = windows[i].copy(isFocused = false)
    }
}

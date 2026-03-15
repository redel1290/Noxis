package com.noxis.ui.window

import androidx.compose.runtime.*
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

data class NoxisWindow(
    val id: String,
    val title: String,
    val icon: String = "📄",
    var position: DpOffset = DpOffset(50.dp, 50.dp),
    var size: DpSize = DpSize(400.dp, 300.dp),
    var isMinimized: Boolean = false,
    var isMaximized: Boolean = false,
    var isFocused: Boolean = false,
    val content: @Composable () -> Unit
)

class WindowManager {
    val windows = mutableStateListOf<NoxisWindow>()
    var maxWindows = 15

    fun openWindow(window: NoxisWindow) {
        if (windows.size >= maxWindows) return
        // Зміщення нових вікон щоб не накладались
        val offset = DpOffset(
            (50 + windows.size * 20).dp,
            (50 + windows.size * 20).dp
        )
        focusWindow(window.id)
        windows.add(window.copy(position = offset, isFocused = true))
    }

    fun closeWindow(id: String) {
        windows.removeAll { it.id == id }
    }

    fun minimizeWindow(id: String) {
        val idx = windows.indexOfFirst { it.id == id }
        if (idx >= 0) windows[idx] = windows[idx].copy(isMinimized = true, isFocused = false)
    }

    fun maximizeWindow(id: String) {
        val idx = windows.indexOfFirst { it.id == id }
        if (idx >= 0) {
            val w = windows[idx]
            windows[idx] = w.copy(isMaximized = !w.isMaximized)
        }
    }

    fun focusWindow(id: String) {
        windows.replaceAll { it.copy(isFocused = it.id == id) }
    }

    fun restoreWindow(id: String) {
        val idx = windows.indexOfFirst { it.id == id }
        if (idx >= 0) windows[idx] = windows[idx].copy(isMinimized = false, isFocused = true)
    }

    fun updatePosition(id: String, offset: DpOffset) {
        val idx = windows.indexOfFirst { it.id == id }
        if (idx >= 0) windows[idx] = windows[idx].copy(position = offset)
    }

    fun updateSize(id: String, size: DpSize) {
        val idx = windows.indexOfFirst { it.id == id }
        if (idx >= 0) windows[idx] = windows[idx].copy(size = size)
    }

    fun bringToFront(id: String) {
        val window = windows.find { it.id == id } ?: return
        windows.remove(window)
        windows.add(window.copy(isFocused = true))
        focusWindow(id)
    }
}

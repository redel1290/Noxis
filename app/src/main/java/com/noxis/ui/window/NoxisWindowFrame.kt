package com.noxis.ui.window

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*

@Composable
fun NoxisWindowFrame(win: NoxisWindow, mgr: WindowManager, sw: Dp, sh: Dp) {
    val taskbarH = 40.dp
    val availH = sh - taskbarH

    val animAlpha by animateFloatAsState(if (win.isMinimized) 0f else 1f, tween(150), label = "a")
    val animScale by animateFloatAsState(if (win.isMinimized) 0.9f else 1f, spring(0.7f, 500f), label = "s")
    if (win.isMinimized && animAlpha == 0f) return

    val w = if (win.isMaximized) sw else win.size.width
    val h = if (win.isMaximized) availH else win.size.height
    val x = if (win.isMaximized) 0.dp else win.position.x
    val y = if (win.isMaximized) 0.dp else win.position.y

    var dx by remember(win.id) { mutableFloatStateOf(0f) }
    var dy by remember(win.id) { mutableFloatStateOf(0f) }

    Box(
        modifier = Modifier
            .offset(x = x + dx.dp, y = y + dy.dp)
            .size(w, h)
            .scale(animScale).alpha(animAlpha)
            .shadow(if (win.isFocused) 8.dp else 2.dp, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp,
                if (win.isFocused) MaterialTheme.colorScheme.primary.copy(0.5f)
                else MaterialTheme.colorScheme.outline,
                RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface)
            .pointerInput(win.id) { detectTapGestures { mgr.bringToFront(win.id) } }
    ) {
        Column(Modifier.fillMaxSize()) {
            // Title bar — Windows стиль
            Row(
                modifier = Modifier.fillMaxWidth().height(28.dp)
                    .background(if (win.isFocused) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant)
                    .pointerInput(win.id, win.isMaximized) {
                        if (!win.isMaximized) detectDragGestures(
                            onDragStart = { mgr.bringToFront(win.id) },
                            onDrag = { _, d -> dx += d.x / density; dy += d.y / density },
                            onDragEnd = {
                                mgr.updatePosition(win.id, DpOffset((x.value + dx).dp, (y.value + dy).dp))
                                dx = 0f; dy = 0f
                            }
                        )
                    }
                    .padding(start = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(win.icon, fontSize = 11.sp)
                Spacer(Modifier.width(4.dp))
                Text(
                    win.title,
                    fontSize = 11.sp, fontWeight = if (win.isFocused) FontWeight.Medium else FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1, overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                // Windows-style кнопки (без кольорів — як у Win11)
                WinTitleBtn(hoverColor = Color(0xFFE81123), onClick = { mgr.close(win.id) }) {
                    Icon(Icons.Default.Close, null, modifier = Modifier.size(10.dp))
                }
            }
            // Тонка лінія під title bar
            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline.copy(0.5f))
            // Контент
            Box(Modifier.fillMaxSize()) { win.content() }
        }
    }
}

@Composable
fun WinTitleBtn(hoverColor: Color, onClick: () -> Unit, content: @Composable () -> Unit) {
    var hovered by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier.size(40.dp, 28.dp)
            .background(if (hovered) hoverColor else Color.Transparent)
            .clickable(onClick = onClick)
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        hovered = event.changes.any { it.pressed }
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) { content() }
}

@Composable
fun WindowLayer(mgr: WindowManager, sw: Dp, sh: Dp) {
    mgr.windows.forEach { win ->
        key(win.id) { NoxisWindowFrame(win = win, mgr = mgr, sw = sw, sh = sh) }
    }
}

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
fun NoxisWindowFrame(window: NoxisWindow, manager: WindowManager, screenW: Dp, screenH: Dp) {
    val taskbarH = 48.dp
    val availH = screenH - taskbarH

    val animScale by animateFloatAsState(
        targetValue = if (window.isMinimized) 0.85f else 1f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = 500f),
        label = "scale"
    )
    val animAlpha by animateFloatAsState(
        targetValue = if (window.isMinimized) 0f else 1f,
        animationSpec = tween(180),
        label = "alpha"
    )

    if (window.isMinimized && animAlpha == 0f) return

    val w = if (window.isMaximized) screenW else window.size.width
    val h = if (window.isMaximized) availH else window.size.height
    val x = if (window.isMaximized) 0.dp else window.position.x
    val y = if (window.isMaximized) 0.dp else window.position.y

    var dragOffX by remember(window.id) { mutableFloatStateOf(0f) }
    var dragOffY by remember(window.id) { mutableFloatStateOf(0f) }

    Box(
        modifier = Modifier
            .offset(x = x + dragOffX.dp, y = y + dragOffY.dp)
            .size(w, h)
            .scale(animScale)
            .alpha(animAlpha)
            .shadow(if (window.isFocused) 12.dp else 3.dp, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .border(
                1.dp,
                if (window.isFocused) MaterialTheme.colorScheme.primary.copy(0.6f)
                else MaterialTheme.colorScheme.outline,
                RoundedCornerShape(8.dp)
            )
            .background(MaterialTheme.colorScheme.surface)
            .pointerInput(window.id) { detectTapGestures { manager.bringToFront(window.id) } }
    ) {
        Column(Modifier.fillMaxSize()) {
            // Title bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
                    .background(
                        if (window.isFocused) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                    .pointerInput(window.id, window.isMaximized) {
                        if (!window.isMaximized) {
                            detectDragGestures(
                                onDragStart = { manager.bringToFront(window.id) },
                                onDrag = { _, d ->
                                    dragOffX += d.x / density
                                    dragOffY += d.y / density
                                },
                                onDragEnd = {
                                    manager.updatePosition(window.id,
                                        DpOffset((x.value + dragOffX).dp, (y.value + dragOffY).dp))
                                    dragOffX = 0f; dragOffY = 0f
                                }
                            )
                        }
                    }
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${window.icon}  ${window.title}",
                    color = if (window.isFocused) Color.White else MaterialTheme.colorScheme.onSurface,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                // Кнопки
                TitleBtn(Color(0xFFFFBD44)) { manager.minimize(window.id) }
                Spacer(Modifier.width(4.dp))
                TitleBtn(Color(0xFF27C93F)) {
                    if (window.isMinimized) manager.restore(window.id)
                    else manager.toggleMaximize(window.id)
                }
                Spacer(Modifier.width(4.dp))
                TitleBtn(Color(0xFFFF605C)) { manager.close(window.id) }
            }
            // Content
            Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)) {
                window.content()
            }
        }
    }
}

@Composable
fun TitleBtn(color: Color, onClick: () -> Unit) {
    Box(
        Modifier.size(14.dp).clip(RoundedCornerShape(7.dp))
            .background(color).clickable(onClick = onClick)
    )
}

@Composable
fun WindowLayer(manager: WindowManager, screenW: Dp, screenH: Dp) {
    manager.windows.forEach { window ->
        key(window.id) {
            NoxisWindowFrame(window = window, manager = manager, screenW = screenW, screenH = screenH)
        }
    }
}

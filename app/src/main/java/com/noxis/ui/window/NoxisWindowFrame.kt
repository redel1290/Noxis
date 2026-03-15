package com.noxis.ui.window

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.CloseFullscreen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*

@Composable
fun NoxisWindowFrame(
    window: NoxisWindow,
    manager: WindowManager,
    screenSize: DpSize
) {
    // Анімація появи
    val scale by animateFloatAsState(
        targetValue = if (window.isMinimized) 0f else 1f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 400f),
        label = "window_scale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (window.isMinimized) 0f else 1f,
        animationSpec = tween(150),
        label = "window_alpha"
    )

    if (window.isMinimized && scale == 0f) return

    val isMaximized = window.isMaximized
    val position = if (isMaximized) DpOffset(0.dp, 0.dp) else window.position
    val size = if (isMaximized) DpSize(screenSize.width, screenSize.height - 48.dp) else window.size

    // Drag offset
    var dragX by remember(window.id) { mutableFloatStateOf(0f) }
    var dragY by remember(window.id) { mutableFloatStateOf(0f) }

    Box(
        modifier = Modifier
            .offset(
                x = position.x + dragX.dp,
                y = position.y + dragY.dp
            )
            .size(size.width, size.height)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
                transformOrigin = TransformOrigin(0.5f, 0.5f)
            }
            .shadow(
                elevation = if (window.isFocused) 16.dp else 4.dp,
                shape = RoundedCornerShape(8.dp)
            )
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = 1.dp,
                color = if (window.isFocused)
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                else
                    MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(8.dp)
            )
            .background(MaterialTheme.colorScheme.surface)
            .pointerInput(window.id) {
                detectTapGestures { manager.bringToFront(window.id) }
            }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // Title bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
                    .background(
                        if (window.isFocused)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                    )
                    .pointerInput(window.id, isMaximized) {
                        if (!isMaximized) {
                            detectDragGestures(
                                onDragStart = { manager.bringToFront(window.id) },
                                onDrag = { _, dragAmount ->
                                    dragX += dragAmount.x / density
                                    dragY += dragAmount.y / density
                                },
                                onDragEnd = {
                                    val newPos = DpOffset(
                                        (position.x.value + dragX).dp,
                                        (position.y.value + dragY).dp
                                    )
                                    manager.updatePosition(window.id, newPos)
                                    dragX = 0f
                                    dragY = 0f
                                }
                            )
                        }
                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${window.icon} ${window.title}",
                        color = if (window.isFocused) Color.White
                        else MaterialTheme.colorScheme.onSurface,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )

                    // Кнопки вікна
                    WindowButton(
                        onClick = { manager.minimizeWindow(window.id) },
                        color = Color(0xFFFFBD44)
                    ) { Icon(Icons.Default.Remove, null, tint = Color.Black, modifier = Modifier.size(10.dp)) }

                    Spacer(modifier = Modifier.width(4.dp))

                    WindowButton(
                        onClick = { manager.maximizeWindow(window.id) },
                        color = Color(0xFF00CA4E)
                    ) {
                        Icon(
                            if (isMaximized) Icons.Default.CloseFullscreen else Icons.Default.OpenInFull,
                            null, tint = Color.Black, modifier = Modifier.size(10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    WindowButton(
                        onClick = { manager.closeWindow(window.id) },
                        color = Color(0xFFFF605C)
                    ) { Icon(Icons.Default.Close, null, tint = Color.Black, modifier = Modifier.size(10.dp)) }
                }
            }

            // Контент вікна
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                window.content()
            }
        }
    }
}

@Composable
fun WindowButton(
    onClick: () -> Unit,
    color: Color,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .size(16.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(color)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
fun WindowLayer(windowManager: WindowManager) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val screenSize = DpSize(maxWidth, maxHeight)
        windowManager.windows.forEach { window ->
            key(window.id) {
                NoxisWindowFrame(
                    window = window,
                    manager = windowManager,
                    screenSize = screenSize
                )
            }
        }
    }
}

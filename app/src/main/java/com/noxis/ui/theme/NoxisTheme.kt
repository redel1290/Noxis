package com.noxis.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val LightColors = lightColorScheme(
    primary = Color(0xFF0078D4),
    onPrimary = Color.White,
    background = Color(0xFFF0F0F0),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFE5E5E5),
    onBackground = Color(0xFF1A1A1A),
    onSurface = Color(0xFF1A1A1A),
    outline = Color(0xFFCCCCCC),
)

val DarkColors = darkColorScheme(
    primary = Color(0xFF4CC2FF),
    onPrimary = Color(0xFF001E2F),
    background = Color(0xFF1E1E1E),
    surface = Color(0xFF2D2D2D),
    surfaceVariant = Color(0xFF3A3A3A),
    onBackground = Color(0xFFE8E8E8),
    onSurface = Color(0xFFE8E8E8),
    outline = Color(0xFF555555),
)

@Composable
fun NoxisTheme(darkTheme: Boolean = false, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}

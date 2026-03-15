package com.noxis.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

// Windows 11 кольори
val WinBlue = Color(0xFF0078D4)
val WinBlueDark = Color(0xFF4CC2FF)
val WinTaskbarLight = Color(0xF0F3F3F3)
val WinTaskbarDark = Color(0xF0202020)
val WinSurfaceLight = Color(0xFFFFFFFF)
val WinSurfaceDark = Color(0xFF2B2B2B)
val WinBgLight = Color(0xFFF3F3F3)
val WinBgDark = Color(0xFF1C1C1C)
val WinBorderLight = Color(0xFFE0E0E0)
val WinBorderDark = Color(0xFF3D3D3D)

val LightColors = lightColorScheme(
    primary = WinBlue,
    onPrimary = Color.White,
    background = WinBgLight,
    surface = WinSurfaceLight,
    surfaceVariant = Color(0xFFEEEEEE),
    onBackground = Color(0xFF1A1A1A),
    onSurface = Color(0xFF1A1A1A),
    outline = WinBorderLight,
    secondaryContainer = Color(0xFFE5F1FB),
    onSecondaryContainer = WinBlue,
)

val DarkColors = darkColorScheme(
    primary = WinBlueDark,
    onPrimary = Color(0xFF001E2F),
    background = WinBgDark,
    surface = WinSurfaceDark,
    surfaceVariant = Color(0xFF333333),
    onBackground = Color(0xFFE8E8E8),
    onSurface = Color(0xFFE8E8E8),
    outline = WinBorderDark,
    secondaryContainer = Color(0xFF1A3A5C),
    onSecondaryContainer = WinBlueDark,
)

@Composable
fun NoxisTheme(darkTheme: Boolean = false, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}

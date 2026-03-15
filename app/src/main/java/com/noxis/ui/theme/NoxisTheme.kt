package com.noxis.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Світла тема
private val LightColors = lightColorScheme(
    primary = Color(0xFF0078D4),           // Windows синій
    onPrimary = Color.White,
    secondary = Color(0xFF005A9E),
    background = Color(0xFFF3F3F3),
    surface = Color(0xFFFFFFFF),
    onBackground = Color(0xFF1A1A1A),
    onSurface = Color(0xFF1A1A1A),
    surfaceVariant = Color(0xFFE8E8E8),
    outline = Color(0xFFCCCCCC),
)

// Темна тема
private val DarkColors = darkColorScheme(
    primary = Color(0xFF4CC2FF),
    onPrimary = Color(0xFF001E2F),
    secondary = Color(0xFF60CDFF),
    background = Color(0xFF202020),
    surface = Color(0xFF2D2D2D),
    onBackground = Color(0xFFE8E8E8),
    onSurface = Color(0xFFE8E8E8),
    surfaceVariant = Color(0xFF383838),
    outline = Color(0xFF555555),
)

// Кольори таскбару
val TaskbarLight = Color(0xFFEFEFEF)
val TaskbarDark = Color(0xFF1A1A1A)

// Кольори вікна
val WindowTitleLight = Color(0xFFFFFFFF)
val WindowTitleDark = Color(0xFF2D2D2D)
val WindowBorderLight = Color(0xFFD0D0D0)
val WindowBorderDark = Color(0xFF454545)

@Composable
fun NoxisTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}

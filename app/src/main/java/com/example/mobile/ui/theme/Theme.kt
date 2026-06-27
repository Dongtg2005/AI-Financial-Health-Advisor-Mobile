package com.example.mobile.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary          = Blue40,
    onPrimary        = Color.White,
    primaryContainer = Blue90,
    onPrimaryContainer = Blue10,

    secondary        = BlueGrey40,
    onSecondary      = Color.White,
    secondaryContainer = BlueGrey90,
    onSecondaryContainer = BlueGrey10,

    // Tắt màu nền mặc định để nhường chỗ cho Aurora Gradient
    background       = Color.Transparent,
    onBackground     = Grey10,
    surface          = Color.White.copy(alpha = 0.7f), // Surface mặc định hơi trong suốt
    onSurface        = Grey10,
    surfaceVariant   = Grey95,
    onSurfaceVariant = Grey20,

    error            = RedDanger,
    errorContainer   = RedDanger10,
    outline          = Color(0xFFE0E0E6),
)

private val DarkColorScheme = darkColorScheme(
    primary          = Blue80,
    onPrimary        = Blue20,
    primaryContainer = Blue40,
    onPrimaryContainer = Blue90,

    secondary        = BlueGrey80,
    onSecondary      = BlueGrey10,
    secondaryContainer = BlueGrey40,
    onSecondaryContainer = BlueGrey90,

    background       = Color.Transparent,
    onBackground     = Grey90,
    surface          = Grey20.copy(alpha = 0.7f),
    onSurface        = Grey90,
    surfaceVariant   = Color(0xFF44474F),
    onSurfaceVariant = Grey90,

    error            = Color(0xFFFFB4AB),
    outline          = Color(0xFF44474F),
)

@Composable
fun FinanceAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = AppTypography,
        shapes      = AppShapes,
        content     = content
    )
}

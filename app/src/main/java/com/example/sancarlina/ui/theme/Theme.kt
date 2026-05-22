package com.example.sancarlina.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = SancarlinaPrimary,
    onPrimary = SancarlinaOnPrimary,
    secondary = SancarlinaAccent,
    onSecondary = SancarlinaOnAccent,
    background = SancarlinaBackground,
    onBackground = SancarlinaOnBackground,
    surface = SancarlinaSurface,
    onSurface = SancarlinaOnSurface,
    surfaceVariant = SancarlinaSurface,
    onSurfaceVariant = SancarlinaOnSurface
)

// Dark scheme can be adjusted later if needed, but for now we prioritize brand colors
private val DarkColorScheme = LightColorScheme 

@Composable
fun SancarlinaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

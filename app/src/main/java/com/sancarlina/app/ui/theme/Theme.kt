package com.sancarlina.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = SancarlinaPrimary,
    onPrimary = SancarlinaOnPrimary,
    primaryContainer = SancarlinaPrimaryContainer,
    onPrimaryContainer = SancarlinaOnPrimaryContainer,
    secondary = SancarlinaSecondary,
    onSecondary = SancarlinaOnSecondary,
    secondaryContainer = SancarlinaSecondaryContainer,
    onSecondaryContainer = SancarlinaOnSecondaryContainer,
    tertiary = SancarlinaTertiary,
    onTertiary = SancarlinaOnTertiary,
    tertiaryContainer = SancarlinaTertiaryContainer,
    onTertiaryContainer = SancarlinaOnTertiaryContainer,
    background = SancarlinaBackground,
    onBackground = SancarlinaOnBackground,
    surface = SancarlinaSurface,
    onSurface = SancarlinaOnSurface,
    surfaceVariant = SancarlinaSurfaceVariant,
    onSurfaceVariant = SancarlinaOnSurfaceVariant,
    outline = SancarlinaOutline,
    outlineVariant = SancarlinaOutlineVariant
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
            window.statusBarColor = Color.White.toArgb() // Use white status bar for better contrast with new theme
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}

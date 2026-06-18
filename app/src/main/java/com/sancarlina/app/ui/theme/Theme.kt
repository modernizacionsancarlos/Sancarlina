package com.sancarlina.app.ui.theme

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
    primaryContainer = SancarlinaPrimaryContainer,
    onPrimaryContainer = SancarlinaOnPrimaryContainer,
    inversePrimary = SancarlinaInversePrimary,
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
    surfaceDim = SancarlinaSurfaceDim,
    surfaceBright = SancarlinaSurfaceBright,
    surfaceContainerLowest = SancarlinaSurfaceContainerLowest,
    surfaceContainerLow = SancarlinaSurfaceContainerLow,
    surfaceContainer = SancarlinaSurfaceContainer,
    surfaceContainerHigh = SancarlinaSurfaceContainerHigh,
    surfaceContainerHighest = SancarlinaSurfaceContainerHighest,
    surfaceVariant = SancarlinaSurfaceVariant,
    onSurfaceVariant = SancarlinaOnSurfaceVariant,
    inverseSurface = SancarlinaInverseSurface,
    inverseOnSurface = SancarlinaInverseOnSurface,
    outline = SancarlinaOutline,
    outlineVariant = SancarlinaOutlineVariant,
    error = SancarlinaError,
    onError = SancarlinaOnError,
    errorContainer = SancarlinaErrorContainer,
    onErrorContainer = SancarlinaOnErrorContainer
)

// Dark scheme: misma paleta Stitch por ahora (app prioriza light brand)
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
            window.statusBarColor = SancarlinaBackground.toArgb()
            window.navigationBarColor = SancarlinaSurfaceContainer.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}

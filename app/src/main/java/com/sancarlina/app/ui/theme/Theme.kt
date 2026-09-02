package com.sancarlina.app.ui.theme

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
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

private val DarkColorScheme = darkColorScheme(
    primary = SancarlinaDarkPrimary,
    onPrimary = SancarlinaDarkOnPrimary,
    primaryContainer = SancarlinaDarkPrimaryContainer,
    onPrimaryContainer = SancarlinaDarkOnPrimaryContainer,
    inversePrimary = SancarlinaDarkInversePrimary,
    secondary = SancarlinaDarkSecondary,
    onSecondary = SancarlinaDarkOnSecondary,
    secondaryContainer = SancarlinaDarkSecondaryContainer,
    onSecondaryContainer = SancarlinaDarkOnSecondaryContainer,
    tertiary = SancarlinaDarkTertiary,
    onTertiary = SancarlinaDarkOnTertiary,
    tertiaryContainer = SancarlinaDarkTertiaryContainer,
    onTertiaryContainer = SancarlinaDarkOnTertiaryContainer,
    background = SancarlinaDarkBackground,
    onBackground = SancarlinaDarkOnBackground,
    surface = SancarlinaDarkSurface,
    onSurface = SancarlinaDarkOnSurface,
    surfaceDim = SancarlinaDarkSurfaceDim,
    surfaceBright = SancarlinaDarkSurfaceBright,
    surfaceContainerLowest = SancarlinaDarkSurfaceContainerLowest,
    surfaceContainerLow = SancarlinaDarkSurfaceContainerLow,
    surfaceContainer = SancarlinaDarkSurfaceContainer,
    surfaceContainerHigh = SancarlinaDarkSurfaceContainerHigh,
    surfaceContainerHighest = SancarlinaDarkSurfaceContainerHighest,
    surfaceVariant = SancarlinaDarkSurfaceVariant,
    onSurfaceVariant = SancarlinaDarkOnSurfaceVariant,
    inverseSurface = SancarlinaDarkInverseSurface,
    inverseOnSurface = SancarlinaDarkInverseOnSurface,
    outline = SancarlinaDarkOutline,
    outlineVariant = SancarlinaDarkOutlineVariant,
    error = SancarlinaDarkError,
    onError = SancarlinaDarkOnError,
    errorContainer = SancarlinaDarkErrorContainer,
    onErrorContainer = SancarlinaDarkOnErrorContainer
)

@Composable
fun SancarlinaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val context = view.context
            val activity = context as? Activity 
                ?: (context as? ContextWrapper)?.baseContext as? Activity
            
            activity?.window?.let { window ->
                WindowCompat.setDecorFitsSystemWindows(window, false)
                val controller = WindowCompat.getInsetsController(window, view)
                controller.isAppearanceLightStatusBars = !darkTheme
                controller.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}

package com.sancarlina.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sancarlina.app.ui.theme.AppTheme
import com.sancarlina.app.ui.theme.LocalThemeController

@Composable
fun ThemeSelectionDialog(
    onDismissRequest: () -> Unit
) {
    val themeController = LocalThemeController.current
    val currentTheme = themeController.currentTheme

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = "Tema de la aplicación",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Seleccioná cómo deseás ver la app. Por defecto se utiliza el modo claro.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))

                ThemeOptionItem(
                    icon = Icons.Default.LightMode,
                    title = "Modo Claro",
                    subtitle = "Predeterminado",
                    selected = currentTheme == AppTheme.LIGHT,
                    onClick = {
                        themeController.setTheme(AppTheme.LIGHT)
                        onDismissRequest()
                    }
                )

                ThemeOptionItem(
                    icon = Icons.Default.DarkMode,
                    title = "Modo Oscuro",
                    subtitle = "Ideal para entornos con poca luz",
                    selected = currentTheme == AppTheme.DARK,
                    onClick = {
                        themeController.setTheme(AppTheme.DARK)
                        onDismissRequest()
                    }
                )

                ThemeOptionItem(
                    icon = Icons.Default.BrightnessAuto,
                    title = "Seguir al sistema",
                    subtitle = "Sincronizar con el tema del dispositivo",
                    selected = currentTheme == AppTheme.SYSTEM,
                    onClick = {
                        themeController.setTheme(AppTheme.SYSTEM)
                        onDismissRequest()
                    }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text(
                    text = "Cerrar",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        shape = RoundedCornerShape(24.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}

@Composable
private fun ThemeOptionItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = if (selected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = if (selected) 1.5.dp else 1.dp,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
                    color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            RadioButton(
                selected = selected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = MaterialTheme.colorScheme.primary,
                    unselectedColor = MaterialTheme.colorScheme.outline
                )
            )
        }
    }
}

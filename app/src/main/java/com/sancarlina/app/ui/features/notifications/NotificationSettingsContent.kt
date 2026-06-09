package com.sancarlina.app.ui.features.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sancarlina.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsContent(onBack: () -> Unit) {
    var pointsEnabled by remember { mutableStateOf(true) }
    var newsEnabled by remember { mutableStateOf(true) }
    var offersEnabled by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(SancarlinaSurface)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = SancarlinaSurfaceContainerLow
            ) {
                Row(
                    modifier = Modifier.statusBarsPadding().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = SancarlinaPrimary)
                    }
                    Text(
                        text = "Ajustes de Alertas",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = SancarlinaOnSurface
                    )
                }
            }

            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    "Personaliza qué notificaciones deseas recibir en tu dispositivo.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SancarlinaOnSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                SettingToggle("Puntos y Beneficios", "Nuevos canjes y puntos sumados", pointsEnabled) { pointsEnabled = it }
                SettingToggle("Novedades Municipales", "Eventos, ferias e info importante", newsEnabled) { newsEnabled = it }
                SettingToggle("Ofertas Exclusivas", "Promociones de comercios cercanos", offersEnabled) { offersEnabled = it }
            }
        }
    }
}

@Composable
fun SettingToggle(title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SancarlinaSurfaceContainerLowest,
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = SancarlinaOutline)
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = SancarlinaPrimary,
                    checkedTrackColor = SancarlinaPrimary.copy(alpha = 0.3f)
                )
            )
        }
    }
}

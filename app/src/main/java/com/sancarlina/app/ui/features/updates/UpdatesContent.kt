package com.sancarlina.app.ui.features.updates

import androidx.compose.material3.MaterialTheme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.sancarlina.app.R
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sancarlina.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdatesContent(onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceContainerLow
            ) {
                Row(
                    modifier = Modifier.statusBarsPadding().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.cd_back), tint = MaterialTheme.colorScheme.primary)
                    }
                    Text(
                        text = "Historial de Versiones",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                // Current Version Hero
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surfaceContainerLowest,
                    shape = RoundedCornerShape(24.dp),
                    shadowElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            shape = CircleShape,
                            modifier = Modifier.size(64.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Verified, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "App Actualizada", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Text(text = "Versión actual: v8.1.0", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Novedades recientes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(16.dp))

                VersionUpdateItem(
                    version = "v8.1.0",
                    date = "Hoy",
                    changes = listOf("Rediseño visual completo (Hyper-Rounded)", "Nueva paleta de colores institucional", "Mejoras en navegación y mapas", "Optimización de perfiles y beneficios")
                )
                
                VersionUpdateItem(
                    version = "v8.0.0",
                    date = "Anterior",
                    changes = listOf("Versión estable inicial", "Integración con Firebase")
                )

                Spacer(modifier = Modifier.height(48.dp))
                
                Button(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Icon(Icons.Default.Sync, null)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("BUSCAR ACTUALIZACIONES", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun VersionUpdateItem(version: String, date: String, changes: List<String>) {
    Surface(
        modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth(),
        color = Color.Transparent
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = version, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text(text = date, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
            }
            Spacer(modifier = Modifier.height(8.dp))
            changes.forEach { change ->
                Row(modifier = Modifier.padding(vertical = 2.dp)) {
                    Icon(Icons.Default.Check, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp).padding(top = 2.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = change, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
        }
    }
}

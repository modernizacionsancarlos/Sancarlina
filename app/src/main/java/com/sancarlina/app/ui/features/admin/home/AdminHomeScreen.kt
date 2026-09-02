package com.sancarlina.app.ui.features.admin.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sancarlina.app.R
import com.sancarlina.app.navigation.Screen
import com.sancarlina.app.ui.components.LiveClockGreeting
import com.sancarlina.app.ui.features.admin.components.AdminMetricCard
import com.sancarlina.app.ui.theme.SancarlinaCardShape
import com.sancarlina.app.viewmodel.AdminHomeViewModel

data class AdminModuleItem(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val route: String,
    val accent: Color
)

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun AdminHomeScreen(
    viewModel: AdminHomeViewModel,
    onNavigateToModule: (String) -> Unit,
    onLogout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val stats = uiState.stats
    val colorScheme = MaterialTheme.colorScheme
    val modules = remember(colorScheme) {
        listOf(
            AdminModuleItem("Registro en calle", "Cargar formularios", Icons.AutoMirrored.Filled.Assignment, Screen.FieldRegistration.route, colorScheme.secondary),
            AdminModuleItem("Comercios", "Gestión de comercios", Icons.Default.Store, Screen.AdminComercios.route, colorScheme.primary),
            AdminModuleItem("Zonas", "Distritos y áreas", Icons.Default.Map, Screen.AdminZonas.route, colorScheme.tertiary),
            AdminModuleItem("Beneficios", "Catálogo de canjes", Icons.Default.CardGiftcard, Screen.AdminBeneficios.route, colorScheme.tertiary),
            AdminModuleItem("Usuarios", "Perfiles y puntos", Icons.Default.Group, Screen.AdminUsuarios.route, colorScheme.secondary),
            AdminModuleItem("Formularios", "Esquemas dinámicos", Icons.Default.Description, Screen.AdminFormularios.route, colorScheme.primary),
            AdminModuleItem("Notificaciones", "Alertas municipales", Icons.Default.Notifications, Screen.AdminNotificaciones.route, colorScheme.tertiary),
            AdminModuleItem("Reseñas", "Moderación ciudadana", Icons.Default.RateReview, Screen.AdminReviews.route, colorScheme.primary),
            AdminModuleItem("Administradores", "Accesos del sistema", Icons.Default.Security, Screen.AdminAdministradores.route, colorScheme.secondary)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Image(
                        painter = painterResource(R.drawable.ic_gondolapp_splash_logo),
                        contentDescription = "GondolApp",
                        modifier = Modifier
                            .width(116.dp)
                            .height(34.dp)
                    )
                },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Salir del panel",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(Modifier.padding(horizontal = 18.dp, vertical = 16.dp)) {
                    Text(
                        text = "PANEL MUNICIPAL",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(Modifier.height(5.dp))
                    LiveClockGreeting()
                    Text(
                        text = "Gestión, formularios y operativos en un solo lugar",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(136.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AdminMetricCard(
                        label = "Comercios",
                        value = stats.totalTenants.toString(),
                        modifier = Modifier.weight(1f)
                    )
                    AdminMetricCard(
                        label = "Activos",
                        value = stats.activeTenants.toString(),
                        modifier = Modifier.weight(1f),
                        emphasized = true
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AdminMetricCard(
                        label = "Formularios",
                        value = stats.activeForms.toString(),
                        modifier = Modifier.weight(1f)
                    )
                    AdminMetricCard(
                        label = "Pendientes",
                        value = stats.pendingSubmissions.toString(),
                        modifier = Modifier.weight(1f),
                        alert = stats.pendingSubmissions > 0
                    )
                }
            }

            if (stats.pendingSubmissions > 0) {
                Spacer(modifier = Modifier.height(14.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.error
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color.White)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Requieren revisión",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${stats.pendingSubmissions} respuestas pendientes",
                                color = Color.White.copy(alpha = 0.86f),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Módulos de gestión",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))

            modules.chunked(2).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowItems.forEach { module ->
                        ModuleCard(
                            item = module,
                            onClick = { onNavigateToModule(module.route) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (rowItems.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ModuleCard(
    item: AdminModuleItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(128.dp),
        shape = SancarlinaCardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(42.dp),
                shape = CircleShape,
                color = item.accent.copy(alpha = 0.14f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        tint = item.accent,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = item.description,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

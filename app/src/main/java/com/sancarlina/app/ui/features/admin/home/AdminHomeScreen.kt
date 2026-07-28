package com.sancarlina.app.ui.features.admin.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sancarlina.app.navigation.Screen
import com.sancarlina.app.ui.components.SancarlinaElevatedCard
import com.sancarlina.app.ui.theme.*
import com.sancarlina.app.viewmodel.AdminHomeViewModel

data class AdminModuleItem(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val route: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHomeScreen(
    viewModel: AdminHomeViewModel,
    onNavigateToModule: (String) -> Unit,
    onLogout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val stats = uiState.stats

    val modules = remember {
        listOf(
            AdminModuleItem("Comercios", "Gestión de comercios y bodegas", Icons.Default.Store, Screen.AdminComercios.route),
            AdminModuleItem("Zonas", "Administrar zonas turísticas", Icons.Default.Map, Screen.AdminZonas.route),
            AdminModuleItem("Beneficios", "Catálogo de puntos y canjes", Icons.Default.CardGiftcard, Screen.AdminBeneficios.route),
            AdminModuleItem("Usuarios", "Gestión de perfiles de usuario", Icons.Default.Group, Screen.AdminUsuarios.route),
            AdminModuleItem("Formularios", "Esquemas y plantillas dinámicas", Icons.Default.Description, Screen.AdminFormularios.route),
            AdminModuleItem("Notificaciones", "Alertas y comunicados", Icons.Default.Notifications, Screen.AdminNotificaciones.route),
            AdminModuleItem("Administradores", "Gestión de permisos superAdmin", Icons.Default.Security, Screen.AdminAdministradores.route)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Panel de Control", fontWeight = FontWeight.Bold)
                        Text(
                            "Municipalidad de San Carlos",
                            style = MaterialTheme.typography.labelMedium,
                            color = SancarlinaOnSurfaceVariant
                        )
                    }
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SancarlinaBackground,
                    titleContentColor = SancarlinaOnBackground
                )
            )
        },
        containerColor = SancarlinaBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = "Resumen del Sistema",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = SancarlinaPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = SancarlinaPrimary)
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Comercios",
                        value = "${stats.activeTenants} / ${stats.totalTenants}",
                        icon = Icons.Default.Store,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Respuestas",
                        value = "${stats.pendingSubmissions}",
                        icon = Icons.Default.Inbox,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Formularios",
                        value = "${stats.activeForms}",
                        icon = Icons.Default.Description,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Módulos de Gestión",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = SancarlinaPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            modules.forEach { module ->
                ModuleCard(
                    item = module,
                    onClick = { onNavigateToModule(module.route) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    SancarlinaElevatedCard(modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = SancarlinaPrimary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = SancarlinaPrimary
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = SancarlinaOnSurfaceVariant
            )
        }
    }
}

@Composable
private fun ModuleCard(
    item: AdminModuleItem,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = MaterialTheme.shapes.small,
                color = SancarlinaPrimary.copy(alpha = 0.1f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        tint = SancarlinaPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = SancarlinaOnSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = SancarlinaOnSurfaceVariant
            )
        }
    }
}

package com.sancarlina.app.ui.features.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sancarlina.app.ui.theme.*
import com.sancarlina.app.viewmodel.ProfileViewModel

@Composable
fun ProfileContent(
    viewModel: ProfileViewModel = viewModel(),
    onNavigateToLogin: () -> Unit = {},
    onOpenDrawer: () -> Unit = {},
    onNavigateToFavorites: () -> Unit = {},
    onNavigateToEditProfile: () -> Unit = {},
    onNavigateToEmprendimiento: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToSupport: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SancarlinaSurface)
            .verticalScroll(rememberScrollState())
    ) {
        // Profile Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = SancarlinaSurfaceContainerLow
        ) {
            Column(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    modifier = Modifier.size(96.dp),
                    shape = CircleShape,
                    color = SancarlinaPrimary.copy(alpha = 0.1f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Person, null, modifier = Modifier.size(48.dp), tint = SancarlinaPrimary)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = uiState.userName,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = SancarlinaOnSurface
                )
                Text(
                    text = uiState.userEmail.ifEmpty { "Inicia sesión para más beneficios" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = SancarlinaOnSurfaceVariant
                )
            }
        }

        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                "Mi Cuenta",
                style = MaterialTheme.typography.labelLarge,
                color = SancarlinaPrimary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            ProfileMenuItem(Icons.Default.Edit, "Editar Perfil", onNavigateToEditProfile)
            ProfileMenuItem(Icons.Default.Favorite, "Mis Favoritos", onNavigateToFavorites)
            ProfileMenuItem(Icons.Default.History, "Historial de Puntos", onNavigateToHistory)
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                "Comunidad",
                style = MaterialTheme.typography.labelLarge,
                color = SancarlinaPrimary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            ProfileMenuItem(Icons.Default.Store, "Sumá tu Emprendimiento", onNavigateToEmprendimiento)
            ProfileMenuItem(Icons.Default.Notifications, "Notificaciones", onNavigateToNotifications)
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                "Soporte",
                style = MaterialTheme.typography.labelLarge,
                color = SancarlinaPrimary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            ProfileMenuItem(Icons.AutoMirrored.Filled.Help, "Ayuda y Soporte", onNavigateToSupport)
            ProfileMenuItem(Icons.Default.Info, "Información Legal", { /* Navigate to legal */ })
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Button(
                onClick = { viewModel.logout(); onNavigateToLogin() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SancarlinaSurfaceContainer),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text("CERRAR SESIÓN", color = SancarlinaSecondary, fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun ProfileMenuItem(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        color = SancarlinaSurfaceContainerLowest,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(12.dp),
                color = SancarlinaSurfaceContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = SancarlinaPrimary, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = SancarlinaOnSurface,
                modifier = Modifier.weight(1f)
            )
            Icon(Icons.Default.ChevronRight, null, tint = SancarlinaOutlineVariant)
        }
    }
}

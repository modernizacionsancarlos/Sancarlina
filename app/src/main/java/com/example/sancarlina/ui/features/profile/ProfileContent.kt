package com.example.sancarlina.ui.features.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.sancarlina.ui.theme.SancarlinaAccent
import com.example.sancarlina.ui.theme.SancarlinaBackground
import com.example.sancarlina.ui.theme.SancarlinaPrimary

@Composable
fun ProfileContent(viewModel: ProfileViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SancarlinaBackground)
            .verticalScroll(rememberScrollState())
    ) {
        // Profile Header
        ProfileHeaderSection(uiState)

        Spacer(modifier = Modifier.height(24.dp))

        // Menu Section
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
        ) {
            MenuItem(Icons.Default.Person, "Mis Datos Personales") {}
            HorizontalDivider(color = SancarlinaBackground, thickness = 1.dp)
            MenuItem(Icons.Default.Favorite, "Mis Comercios Favoritos") {}
            HorizontalDivider(color = SancarlinaBackground, thickness = 1.dp)
            MenuItem(Icons.Default.History, "Historial de Puntos y Compras") {}
            HorizontalDivider(color = SancarlinaBackground, thickness = 1.dp)
            MenuItem(
                Icons.Default.Notifications, 
                "Notificaciones", 
                badgeCount = uiState.notificationCount
            ) {}
            HorizontalDivider(color = SancarlinaBackground, thickness = 1.dp)
            MenuItem(Icons.AutoMirrored.Filled.Help, "Ayuda y Soporte") {}
            HorizontalDivider(color = SancarlinaBackground, thickness = 1.dp)
            LogoutItem { viewModel.onLogoutClicked() }
        }

        // Version Info
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "SANCARLINA",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.Gray.copy(alpha = 0.5f),
                letterSpacing = 4.sp
            )
            Text(
                text = "Versión 1.0.0",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray.copy(alpha = 0.5f)
            )
        }
    }

    // Logout Confirmation [MODAL 32]
    if (uiState.showLogoutConfirmation) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissLogout() },
            confirmButton = {
                TextButton(onClick = { viewModel.confirmLogout() }) {
                    Text("CERRAR SESIÓN", color = SancarlinaAccent, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissLogout() }) {
                    Text("CANCELAR", color = Color.Gray)
                }
            },
            title = { Text("¿Cerrar sesión?") },
            text = { Text("¿Estás seguro que deseas salir de tu cuenta?") },
            containerColor = Color.White
        )
    }
}

@Composable
fun ProfileHeaderSection(uiState: ProfileUiState) {
    Surface(
        color = SancarlinaPrimary,
        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 60.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                AsyncImage(
                    model = uiState.profileImageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentScale = ContentScale.Crop
                )
                Surface(
                    modifier = Modifier.size(32.dp),
                    shape = CircleShape,
                    color = SancarlinaAccent,
                    shadowElevation = 2.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = uiState.userName,
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = uiState.userEmail,
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Surface(
                color = Color.White.copy(alpha = 0.2f),
                shape = CircleShape
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${uiState.pointsBalance} Puntos",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

@Composable
fun MenuItem(
    icon: ImageVector, 
    title: String, 
    badgeCount: Int = 0, 
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (badgeCount > 0) {
            Surface(
                color = SancarlinaAccent,
                shape = CircleShape
            ) {
                Text(
                    text = badgeCount.toString(),
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
    }
}

@Composable
fun LogoutItem(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = SancarlinaAccent, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = "Cerrar Sesión",
            style = MaterialTheme.typography.bodyMedium,
            color = SancarlinaAccent,
            fontWeight = FontWeight.Medium
        )
    }
}

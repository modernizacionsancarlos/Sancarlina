package com.example.sancarlina.ui.features.profile

import android.widget.Toast
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.sancarlina.ui.theme.SancarlinaAccent
import com.example.sancarlina.ui.theme.SancarlinaBackground
import com.example.sancarlina.ui.theme.SancarlinaPrimary
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileContent(
    viewModel: ProfileViewModel = viewModel(),
    onNavigateToUpdates: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {},
    onOpenDrawer: () -> Unit = {},
    onNavigateToFavorites: () -> Unit = {},
    onNavigateToEditProfile: () -> Unit = {},
    onNavigateToEmprendimiento: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToSupport: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val auth = remember { FirebaseAuth.getInstance() }

    if (auth.currentUser == null) {
        Box(
            modifier = Modifier.fillMaxSize().background(SancarlinaBackground),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(32.dp)
            ) {
                Surface(
                    color = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.size(120.dp),
                    shadowElevation = 4.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = SancarlinaAccent,
                            modifier = Modifier.size(64.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Tu Perfil Personal",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Iniciá sesión para gestionar tus favoritos, ver tu historial y personalizar tu experiencia.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = onNavigateToLogin,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SancarlinaAccent),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text("INICIAR SESIÓN", fontWeight = FontWeight.Bold)
                }
            }
        }
    } else {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "GÓNDOLA SANCARLINA",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp,
                            color = Color.White
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onOpenDrawer) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                        }
                    },
                    actions = {
                        IconButton(onClick = { Toast.makeText(context, "Buscador", Toast.LENGTH_SHORT).show() }) {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = SancarlinaPrimary
                    )
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(SancarlinaBackground)
                    .verticalScroll(rememberScrollState())
            ) {
                // Profile Header
                ProfileHeaderSection(uiState, onNavigateToEditProfile)

                Spacer(modifier = Modifier.height(24.dp))

                // Menu Section
                Column(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                ) {
                    MenuItem(Icons.Default.Person, "Mis Datos Personales") {
                        onNavigateToEditProfile()
                    }
                    HorizontalDivider(color = SancarlinaBackground, thickness = 1.dp)
                    MenuItem(Icons.Default.Favorite, "Mis Comercios Favoritos") {
                        onNavigateToFavorites()
                    }
                    HorizontalDivider(color = SancarlinaBackground, thickness = 1.dp)
                    MenuItem(Icons.Default.Storefront, "Sumá tu Emprendimiento") {
                        onNavigateToEmprendimiento()
                    }
                    HorizontalDivider(color = SancarlinaBackground, thickness = 1.dp)
                    MenuItem(Icons.Default.History, "Historial de Puntos y Compras") {
                        Toast.makeText(context, "Historial", Toast.LENGTH_SHORT).show()
                    }
                    HorizontalDivider(color = SancarlinaBackground, thickness = 1.dp)
                    MenuItem(
                        Icons.Default.Notifications, 
                        "Notificaciones", 
                        badgeCount = uiState.notificationCount
                    ) {
                        onNavigateToNotifications()
                    }
                    HorizontalDivider(color = SancarlinaBackground, thickness = 1.dp)
                    MenuItem(Icons.Default.Update, "Actualizaciones") {
                        onNavigateToUpdates()
                    }
                    HorizontalDivider(color = SancarlinaBackground, thickness = 1.dp)
                    MenuItem(Icons.AutoMirrored.Filled.Help, "Ayuda y Soporte") {
                        onNavigateToSupport()
                    }
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
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray.copy(alpha = 0.5f),
                        letterSpacing = 4.sp
                    )
                    Text(
                        text = "Versión 5.0.0",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray.copy(alpha = 0.5f)
                    )
                }
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
}

@Composable
fun ProfileHeaderSection(uiState: ProfileUiState, onEditClick: () -> Unit) {
    Surface(
        color = SancarlinaPrimary,
        shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier.clickable { onEditClick() }
            ) {
                AsyncImage(
                    model = uiState.profileImageUrl.ifEmpty { "https://via.placeholder.com/150" },
                    contentDescription = null,
                    modifier = Modifier
                        .size(96.dp)
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
                    Box(modifier = Modifier.fillMaxSize().clickable { onEditClick() }, contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = uiState.userName.ifEmpty { "Usuario Sancarlino" },
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = uiState.userEmail,
                style = MaterialTheme.typography.labelLarge,
                color = Color.White.copy(alpha = 0.8f)
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = CircleShape
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Star, 
                        contentDescription = null, 
                        tint = MaterialTheme.colorScheme.onPrimaryContainer, 
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${uiState.pointsBalance} Puntos",
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelLarge
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
        Icon(
            icon, 
            contentDescription = null, 
            tint = MaterialTheme.colorScheme.outline, 
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (badgeCount > 0) {
            Surface(
                color = MaterialTheme.colorScheme.secondary,
                shape = CircleShape
            ) {
                Text(
                    text = badgeCount.toString(),
                    color = MaterialTheme.colorScheme.onSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
        Icon(
            Icons.Default.ChevronRight, 
            contentDescription = null, 
            tint = MaterialTheme.colorScheme.outlineVariant
        )
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
        Icon(
            Icons.AutoMirrored.Filled.Logout, 
            contentDescription = null, 
            tint = MaterialTheme.colorScheme.secondary, 
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = "Cerrar Sesión",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary,
            fontWeight = FontWeight.Medium
        )
    }
}

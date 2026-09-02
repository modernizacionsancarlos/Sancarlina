package com.sancarlina.app.ui.features.profile

import androidx.compose.material3.MaterialTheme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sancarlina.app.R
import com.sancarlina.app.ui.components.SancarlinaSecondaryButton
import com.sancarlina.app.ui.features.profile.components.ProfileActionCard
import com.sancarlina.app.ui.features.profile.components.ProfileHeroCard
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
    onNavigateToHistory: () -> Unit = {},
    onNavigateToItinerary: () -> Unit = {},
    onNavigateToInterests: () -> Unit = {},
    onNavigateToAdminLogin: () -> Unit = {},
    onNavigateToAdminPanel: () -> Unit = {},
    onNavigateToFieldRegistration: () -> Unit = {},
    onNavigateToFormSubmissions: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        ProfileHeroCard(
            userName = uiState.userName,
            userEmail = uiState.userEmail,
            pointsBalance = uiState.pointsBalance,
            profileImageUrl = uiState.profileImageUrl
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (!uiState.isLoggedIn) {
            Button(
                onClick = onNavigateToLogin,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Icon(Icons.AutoMirrored.Filled.Login, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Iniciar sesión", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = onNavigateToAdminLogin,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.AdminPanelSettings, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Iniciar como administrador")
            }
            Spacer(modifier = Modifier.height(22.dp))
        }

        AnimatedVisibility(
            visible = uiState.hasAdminAccess,
            enter = fadeIn() + slideInVertically { it / 2 }
        ) {
            Column {
                ProfileSectionTitle("Gestión")
                Spacer(modifier = Modifier.height(8.dp))
                ProfileActionCard(
                    icon = Icons.Default.AdminPanelSettings,
                    title = "Panel administrativo",
                    supportingText = "Gestioná comercios, beneficios, formularios y usuarios",
                    highlighted = true,
                    onClick = onNavigateToAdminPanel
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        AnimatedVisibility(
            visible = uiState.hasFieldRegistrationAccess,
            enter = fadeIn() + slideInVertically { it / 2 }
        ) {
            Column {
                ProfileSectionTitle("Trabajo en calle")
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onNavigateToFieldRegistration,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 58.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    )
                ) {
                    Icon(Icons.AutoMirrored.Filled.Assignment, contentDescription = null)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Registro en calle", fontWeight = FontWeight.Bold)
                        Text(
                            "Seleccioná un formulario y comenzá el relevamiento",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null)
                }
                Spacer(modifier = Modifier.height(10.dp))
                ProfileActionCard(
                    icon = Icons.Default.CloudSync,
                    title = "Envíos de formularios",
                    supportingText = "Revisá pendientes, errores y sincronizá manualmente",
                    onClick = onNavigateToFormSubmissions
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        if (uiState.isLoggedIn) {
            ProfileSectionTitle(stringResource(R.string.profile_section_account))
            Spacer(modifier = Modifier.height(8.dp))
            ProfileActionCard(Icons.Default.Edit, "Editar perfil", onNavigateToEditProfile, supportingText = "Actualizá tus datos y tu foto")
            Spacer(modifier = Modifier.height(10.dp))
            ProfileActionCard(Icons.Default.Favorite, "Mis favoritos", onNavigateToFavorites, supportingText = "Guardá lugares para volver a encontrarlos")
            Spacer(modifier = Modifier.height(10.dp))
            ProfileActionCard(Icons.Default.History, "Historial de puntos", onNavigateToHistory, supportingText = "Consultá movimientos y canjes")
            Spacer(modifier = Modifier.height(20.dp))
        }
        ProfileSectionTitle("Planificación")
        Spacer(modifier = Modifier.height(8.dp))
        ProfileActionCard(Icons.Default.Route, "Mi recorrido", onNavigateToItinerary, supportingText = "Organizá tus paradas y abrí la ruta en el mapa")
        Spacer(modifier = Modifier.height(20.dp))
        ProfileActionCard(
            Icons.Default.Tune,
            "Mis intereses",
            onNavigateToInterests,
            supportingText = "Personalizá lugares y experiencias recomendadas"
        )
        Spacer(modifier = Modifier.height(20.dp))
        ProfileSectionTitle(stringResource(R.string.profile_section_community))
        Spacer(modifier = Modifier.height(8.dp))
        ProfileActionCard(Icons.Default.Store, "Sumá tu emprendimiento", onNavigateToEmprendimiento)
        Spacer(modifier = Modifier.height(10.dp))
        ProfileActionCard(Icons.Default.Notifications, "Notificaciones", onNavigateToNotifications)

        Spacer(modifier = Modifier.height(20.dp))
        ProfileSectionTitle(stringResource(R.string.profile_section_support))
        Spacer(modifier = Modifier.height(8.dp))
        ProfileActionCard(Icons.AutoMirrored.Filled.Help, "Ayuda y soporte", onNavigateToSupport)
        Spacer(modifier = Modifier.height(10.dp))
        ProfileActionCard(Icons.Default.Info, "Información legal", { /* ruta legal pendiente en NavGraph */ })

        Spacer(modifier = Modifier.height(32.dp))

        if (uiState.isLoggedIn) {
            SancarlinaSecondaryButton(
                text = stringResource(R.string.profile_logout),
                onClick = {
                    viewModel.logout()
                    onNavigateToLogin()
                }
            )
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
private fun ProfileSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.5.sp
    )
}

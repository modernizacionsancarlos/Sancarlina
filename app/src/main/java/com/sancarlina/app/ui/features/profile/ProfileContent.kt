package com.sancarlina.app.ui.features.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.*
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
    onNavigateToHistory: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SancarlinaBackground)
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

        Spacer(modifier = Modifier.height(24.dp))

        ProfileSectionTitle(stringResource(R.string.profile_section_account))
        Spacer(modifier = Modifier.height(12.dp))
        ProfileActionCard(Icons.Default.Edit, "Editar perfil", onNavigateToEditProfile)
        Spacer(modifier = Modifier.height(8.dp))
        ProfileActionCard(Icons.Default.Favorite, "Mis favoritos", onNavigateToFavorites)
        Spacer(modifier = Modifier.height(8.dp))
        ProfileActionCard(Icons.Default.History, "Historial de puntos", onNavigateToHistory)

        Spacer(modifier = Modifier.height(24.dp))
        ProfileSectionTitle(stringResource(R.string.profile_section_community))
        Spacer(modifier = Modifier.height(12.dp))
        ProfileActionCard(Icons.Default.Store, "Sumá tu emprendimiento", onNavigateToEmprendimiento)
        Spacer(modifier = Modifier.height(8.dp))
        ProfileActionCard(Icons.Default.Notifications, "Notificaciones", onNavigateToNotifications)

        Spacer(modifier = Modifier.height(24.dp))
        ProfileSectionTitle(stringResource(R.string.profile_section_support))
        Spacer(modifier = Modifier.height(12.dp))
        ProfileActionCard(Icons.AutoMirrored.Filled.Help, "Ayuda y soporte", onNavigateToSupport)
        Spacer(modifier = Modifier.height(8.dp))
        ProfileActionCard(Icons.Default.Info, "Información legal", { /* ruta legal pendiente en NavGraph */ })

        Spacer(modifier = Modifier.height(32.dp))

        SancarlinaSecondaryButton(
            text = stringResource(R.string.profile_logout),
            onClick = {
                viewModel.logout()
                onNavigateToLogin()
            }
        )

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
private fun ProfileSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = SancarlinaPrimary,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.5.sp
    )
}

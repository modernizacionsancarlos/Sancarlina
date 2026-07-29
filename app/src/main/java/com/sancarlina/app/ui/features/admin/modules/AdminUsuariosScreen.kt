package com.sancarlina.app.ui.features.admin.modules

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.sancarlina.app.data.repository.UserProfileAdmin
import com.sancarlina.app.ui.components.SancarlinaElevatedCard
import com.sancarlina.app.ui.components.SancarlinaTextField
import com.sancarlina.app.ui.theme.*
import com.sancarlina.app.viewmodel.admin.AdminUsuariosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUsuariosScreen(
    viewModel: AdminUsuariosViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedUserForReset by remember { mutableStateOf<UserProfileAdmin?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Usuarios", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
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
                .padding(16.dp)
        ) {
            SancarlinaTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                label = "Buscar usuario por nombre o correo",
                placeholder = "Ej. Maria, admin@sancarlos..."
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = SancarlinaPrimary)
                }
            } else if (uiState.filteredUsers.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No se encontraron usuarios.")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.filteredUsers, key = { it.uid }) { user ->
                        UserAdminItem(
                            user = user,
                            onResetPassword = {
                                selectedUserForReset = user
                            },
                            onToggleAdmin = {
                                viewModel.setSuperAdmin(user.uid, user.email, user.role != "admin")
                            }
                        )
                    }
                }
            }
        }
    }

    if (selectedUserForReset != null) {
        ResetPasswordDialog(
            user = selectedUserForReset!!,
            onDismiss = { selectedUserForReset = null },
            onConfirm = { newPass ->
                viewModel.resetPassword(selectedUserForReset!!.uid, newPass) {
                    selectedUserForReset = null
                }
            }
        )
    }
}

@Composable
private fun UserAdminItem(
    user: UserProfileAdmin,
    onResetPassword: () -> Unit,
    onToggleAdmin: () -> Unit
) {
    Card(
        shape = SancarlinaCardShape,
        colors = CardDefaults.cardColors(containerColor = SancarlinaSurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (user.role == "admin") Icons.Default.AdminPanelSettings else Icons.Default.Person,
                contentDescription = null,
                tint = if (user.role == "admin") SancarlinaPrimary else Color.Gray,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.user_name.ifBlank { user.email.ifBlank { "Usuario" } },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodySmall,
                    color = SancarlinaOnSurfaceVariant
                )
                Text(
                    text = "Puntos: ${if (user.points_balance > 0) user.points_balance else user.points} | Rol: ${user.role}",
                    style = MaterialTheme.typography.labelSmall,
                    color = SancarlinaPrimary
                )
            }
            IconButton(onClick = onResetPassword) {
                Icon(imageVector = Icons.Default.LockReset, contentDescription = "Blanquear Clave", tint = SancarlinaPrimary)
            }
            IconButton(onClick = onToggleAdmin) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = "Rol Admin",
                    tint = if (user.role == "admin") SancarlinaPrimary else Color.Gray
                )
            }
        }
    }
}

@Composable
private fun ResetPasswordDialog(
    user: UserProfileAdmin,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var newPassword by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Restablecer Contraseña") },
        text = {
            Column {
                Text("Ingresá la nueva contraseña para ${user.email}:")
                Spacer(modifier = Modifier.height(8.dp))
                SancarlinaTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = "Nueva Contraseña",
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (newPassword.length >= 6) {
                        onConfirm(newPassword)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SancarlinaPrimary)
            ) {
                Text("Restablecer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

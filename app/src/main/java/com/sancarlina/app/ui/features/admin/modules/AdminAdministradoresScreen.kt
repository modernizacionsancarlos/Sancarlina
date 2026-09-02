package com.sancarlina.app.ui.features.admin.modules

import androidx.compose.material3.MaterialTheme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ToggleOff
import androidx.compose.material.icons.filled.ToggleOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.sancarlina.app.data.models.SuperAdmin
import com.sancarlina.app.ui.components.SancarlinaElevatedCard
import com.sancarlina.app.ui.components.SancarlinaTextField
import com.sancarlina.app.ui.features.admin.components.AdminAddFab
import com.sancarlina.app.ui.features.admin.components.AdminMetricCard
import com.sancarlina.app.ui.features.admin.components.AdminScreenTopBar
import com.sancarlina.app.ui.features.admin.components.AdminStatusPill
import com.sancarlina.app.ui.theme.*
import com.sancarlina.app.viewmodel.admin.AdminAdministradoresViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAdministradoresScreen(
    viewModel: AdminAdministradoresViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AdminScreenTopBar(title = "Administrar Administradores", onBack = onBack)
        },
        floatingActionButton = {
            AdminAddFab(label = "Nuevo administrador", onClick = { showDialog = true })
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = SancarlinaCardShape,
                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Text(
                        text = "Zona de acceso sensible. Verificá cada alta o cambio de estado.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            val activeAdmins = uiState.superAdmins.count { it.active }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AdminMetricCard("Activos", activeAdmins.toString(), Modifier.weight(1f), emphasized = true)
                AdminMetricCard(
                    "Suspendidos",
                    (uiState.superAdmins.size - activeAdmins).toString(),
                    Modifier.weight(1f),
                    alert = uiState.superAdmins.size > activeAdmins
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else if (uiState.superAdmins.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No se encontraron super administradores registrados.")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.superAdmins, key = { it.uid }) { admin ->
                        SuperAdminItem(
                            admin = admin,
                            onToggleActive = {
                                viewModel.toggleSuperAdminActive(admin.uid, admin.email, admin.active)
                            }
                        )
                    }
                }
            }
        }
    }

    if (showDialog) {
        NewSuperAdminDialog(
            onDismiss = { showDialog = false },
            onConfirm = { email, password, userName ->
                viewModel.createSuperAdminUser(email, password, userName) {
                    showDialog = false
                }
            }
        )
    }
}

@Composable
private fun SuperAdminItem(
    admin: SuperAdmin,
    onToggleActive: () -> Unit
) {
    Card(
        shape = SancarlinaCardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Security,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = admin.email, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = "UID: ${admin.uid}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    text = if (admin.active) "Cuenta Activa" else "Cuenta Suspendida",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (admin.active) Color(0xFF2E7D32) else Color.Red
                )
                Spacer(modifier = Modifier.height(5.dp))
                AdminStatusPill(
                    text = if (admin.active) "Activo" else "Suspendido",
                    active = admin.active
                )
            }
            IconButton(onClick = onToggleActive) {
                Icon(
                    imageVector = if (admin.active) Icons.Default.ToggleOn else Icons.Default.ToggleOff,
                    contentDescription = "Estado SuperAdmin",
                    tint = if (admin.active) Color(0xFF4CAF50) else Color.Gray
                )
            }
        }
    }
}

@Composable
private fun NewSuperAdminDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var userName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo SuperAdmin") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SancarlinaTextField(value = userName, onValueChange = { userName = it }, label = "Nombre y Apellido *")
                SancarlinaTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Correo Institucional *",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
                SancarlinaTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Contraseña Inicial *",
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (email.isNotBlank() && password.length >= 6 && userName.isNotBlank()) {
                        onConfirm(email, password, userName)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) { Text("Crear Administrador") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

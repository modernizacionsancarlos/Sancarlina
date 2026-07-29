package com.sancarlina.app.ui.features.admin.modules

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
            TopAppBar(
                title = { Text("Gestión de Administradores", fontWeight = FontWeight.Bold) },
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = SancarlinaPrimary,
                contentColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Nuevo Administrador")
            }
        },
        containerColor = SancarlinaBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = SancarlinaPrimary)
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
            Icon(
                imageVector = Icons.Default.Security,
                contentDescription = null,
                tint = SancarlinaPrimary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = admin.email, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = "UID: ${admin.uid}", style = MaterialTheme.typography.labelSmall, color = SancarlinaOnSurfaceVariant)
                Text(
                    text = if (admin.active) "Cuenta Activa" else "Cuenta Suspendida",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (admin.active) Color(0xFF2E7D32) else Color.Red
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
                colors = ButtonDefaults.buttonColors(containerColor = SancarlinaPrimary)
            ) { Text("Crear Administrador") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

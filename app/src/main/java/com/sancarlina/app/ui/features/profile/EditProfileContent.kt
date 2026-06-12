package com.sancarlina.app.ui.features.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.sancarlina.app.R
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.sancarlina.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileContent(
    viewModel: EditProfileViewModel = viewModel(),
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            Toast.makeText(context, "Cambios guardados con éxito", Toast.LENGTH_SHORT).show()
            viewModel.resetSuccess()
            onBack()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(SancarlinaSurface)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = SancarlinaSurfaceContainerLow
            ) {
                Row(
                    modifier = Modifier.statusBarsPadding().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.cd_back), tint = SancarlinaPrimary)
                    }
                    Text(
                        text = "Editar Perfil",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = SancarlinaOnSurface
                    )
                }
            }

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = SancarlinaPrimary)
                }
            } else {
                // Errores de delete (sesión antigua, genérico) visibles fuera del diálogo
                uiState.error?.takeIf { !uiState.showDeletePasswordDialog }?.let { message ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        color = SancarlinaSecondary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = message,
                                modifier = Modifier.weight(1f),
                                color = SancarlinaSecondary,
                                style = MaterialTheme.typography.bodySmall
                            )
                            TextButton(onClick = { viewModel.clearError() }) {
                                Text("Cerrar", color = SancarlinaPrimary)
                            }
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Avatar Section
                    Box(contentAlignment = Alignment.BottomEnd) {
                        AsyncImage(
                            model = uiState.profileImageUrl.ifEmpty { "https://via.placeholder.com/150" },
                            contentDescription = null,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(SancarlinaSurfaceContainer),
                            contentScale = ContentScale.Crop
                        )
                        Surface(
                            modifier = Modifier
                                .size(32.dp)
                                .clickable { },
                            shape = CircleShape,
                            color = SancarlinaPrimary,
                            shadowElevation = 2.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.CameraAlt, null, tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    EditProfileTextField("Nombre Completo", uiState.fullName, Icons.Default.Person) { viewModel.onFullNameChange(it) }
                    Spacer(modifier = Modifier.height(16.dp))
                    EditProfileTextField("Teléfono", uiState.phone, Icons.Default.Call) { viewModel.onPhoneChange(it) }
                    Spacer(modifier = Modifier.height(16.dp))
                    EditProfileTextField("Localidad", uiState.location, Icons.Default.LocationOn) { viewModel.onLocationChange(it) }

                    Spacer(modifier = Modifier.height(48.dp))

                    Button(
                        onClick = { viewModel.saveProfile() },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SancarlinaPrimary),
                        shape = RoundedCornerShape(28.dp),
                        enabled = !uiState.isSaving
                    ) {
                        if (uiState.isSaving) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("GUARDAR CAMBIOS", fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(onClick = { viewModel.setShowDeleteDialog(true) }) {
                        Text("Eliminar mi cuenta", color = SancarlinaSecondary, fontWeight = FontWeight.Bold)
                    }
                    
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }

    if (uiState.showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.setShowDeleteDialog(false) },
            confirmButton = {
                Button(
                    onClick = { viewModel.proceedToDeletePassword() },
                    colors = ButtonDefaults.buttonColors(containerColor = SancarlinaSecondary)
                ) {
                    Text("CONTINUAR")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.setShowDeleteDialog(false) }) {
                    Text("CANCELAR", color = SancarlinaOutline)
                }
            },
            title = { Text("¿Eliminar cuenta?") },
            text = { Text("Esta acción es irreversible y perderás todos tus datos y puntos.") },
            containerColor = SancarlinaSurface,
            shape = RoundedCornerShape(28.dp)
        )
    }

    if (uiState.showDeletePasswordDialog) {
        AlertDialog(
            onDismissRequest = {
                if (!uiState.isDeletingAccount) {
                    viewModel.setShowDeletePasswordDialog(false)
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.deleteAccount { onLogout() } },
                    enabled = !uiState.isDeletingAccount && uiState.deletePassword.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = SancarlinaSecondary)
                ) {
                    if (uiState.isDeletingAccount) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text("ELIMINAR DEFINITIVAMENTE")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.setShowDeletePasswordDialog(false) },
                    enabled = !uiState.isDeletingAccount
                ) {
                    Text("CANCELAR", color = SancarlinaOutline)
                }
            },
            title = { Text("Confirmá tu contraseña") },
            text = {
                Column {
                    Text(
                        "Por seguridad, ingresá tu contraseña actual para eliminar la cuenta.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = uiState.deletePassword,
                        onValueChange = { viewModel.onDeletePasswordChange(it) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Contraseña actual") },
                        singleLine = true,
                        enabled = !uiState.isDeletingAccount,
                        visualTransformation = PasswordVisualTransformation(),
                        shape = RoundedCornerShape(16.dp)
                    )
                    uiState.error?.let { message ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(message, color = SancarlinaSecondary, style = MaterialTheme.typography.bodySmall)
                    }
                }
            },
            containerColor = SancarlinaSurface,
            shape = RoundedCornerShape(28.dp)
        )
    }
}

@Composable
fun EditProfileTextField(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onValueChange: (String) -> Unit) {
    Column {
        Text(text = label, style = MaterialTheme.typography.labelLarge, color = SancarlinaOnSurfaceVariant, modifier = Modifier.padding(bottom = 8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(icon, null, tint = SancarlinaOutline) },
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = SancarlinaSurfaceContainerLowest,
                focusedContainerColor = SancarlinaSurfaceContainerLowest,
                unfocusedBorderColor = SancarlinaOutlineVariant,
                focusedBorderColor = SancarlinaPrimary
            ),
            singleLine = true
        )
    }
}

package com.sancarlina.app.ui.features.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sancarlina.app.R
import com.sancarlina.app.ui.components.SancarlinaPrimaryButton
import com.sancarlina.app.ui.components.SancarlinaTextField
import com.sancarlina.app.ui.components.SancarlinaTopBar
import com.sancarlina.app.ui.features.profile.components.EditProfileAvatarSection
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        SancarlinaTopBar(
            title = stringResource(R.string.edit_profile_title),
            onBack = onBack
        )

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            uiState.error?.takeIf { !uiState.showDeletePasswordDialog }?.let { message ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = message,
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.secondary,
                            style = MaterialTheme.typography.bodySmall
                        )
                        TextButton(onClick = { viewModel.clearError() }) {
                            Text("Cerrar", color = MaterialTheme.colorScheme.primary)
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
                EditProfileAvatarSection(
                    fullName = uiState.fullName,
                    profileImageUrl = uiState.profileImageUrl
                )

                Spacer(modifier = Modifier.height(32.dp))

                SancarlinaTextField(
                    value = uiState.fullName,
                    onValueChange = viewModel::onFullNameChange,
                    label = stringResource(R.string.edit_profile_name)
                )
                Spacer(modifier = Modifier.height(16.dp))
                SancarlinaTextField(
                    value = uiState.phone,
                    onValueChange = viewModel::onPhoneChange,
                    label = stringResource(R.string.edit_profile_phone)
                )
                Spacer(modifier = Modifier.height(16.dp))
                SancarlinaTextField(
                    value = uiState.location,
                    onValueChange = viewModel::onLocationChange,
                    label = stringResource(R.string.edit_profile_location)
                )

                Spacer(modifier = Modifier.height(40.dp))

                if (uiState.isSaving) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                } else {
                    SancarlinaPrimaryButton(
                        text = stringResource(R.string.edit_profile_save),
                        onClick = { viewModel.saveProfile() }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = { viewModel.setShowDeleteDialog(true) }) {
                    Text(
                        stringResource(R.string.edit_profile_delete),
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    if (uiState.showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.setShowDeleteDialog(false) },
            confirmButton = {
                Button(
                    onClick = { viewModel.proceedToDeletePassword() },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("CONTINUAR")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.setShowDeleteDialog(false) }) {
                    Text("CANCELAR", color = MaterialTheme.colorScheme.outline)
                }
            },
            title = { Text("¿Eliminar cuenta?") },
            text = { Text("Esta acción es irreversible y perderás todos tus datos y puntos.") },
            containerColor = MaterialTheme.colorScheme.surface,
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
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    if (uiState.isDeletingAccount) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
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
                    Text("CANCELAR", color = MaterialTheme.colorScheme.outline)
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
                        Text(message, color = MaterialTheme.colorScheme.secondary, style = MaterialTheme.typography.bodySmall)
                    }
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(28.dp)
        )
    }
}

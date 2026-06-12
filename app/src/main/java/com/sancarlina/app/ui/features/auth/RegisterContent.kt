package com.sancarlina.app.ui.features.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sancarlina.app.R
import com.sancarlina.app.ui.theme.*
import com.sancarlina.app.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterContent(
    viewModel: AuthViewModel = viewModel(),
    onBack: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SancarlinaSurface)
    ) {
        // Top Header
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp),
            color = SancarlinaSurfaceContainerLow
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.align(Alignment.CenterStart).padding(start = 8.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.cd_back), tint = SancarlinaPrimary)
                }
                Image(
                    painter = painterResource(id = R.drawable.ic_sancarlina_logo),
                    contentDescription = "GondolApp",
                    modifier = Modifier.height(40.dp).align(Alignment.Center)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 96.dp)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Crear Cuenta",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                color = SancarlinaPrimary
            )
            
            Text(
                text = "Únete a la comunidad de San Carlos",
                style = MaterialTheme.typography.bodyMedium,
                color = SancarlinaOnSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Form Container
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = SancarlinaSurfaceContainerLowest,
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    // Name
                    Text(
                        "Nombre Completo",
                        style = MaterialTheme.typography.labelLarge,
                        color = SancarlinaOnSurfaceVariant,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Nombre y Apellido") },
                        leadingIcon = { Icon(Icons.Default.Person, null, tint = SancarlinaOutline) },
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = SancarlinaSurfaceContainer,
                            focusedContainerColor = SancarlinaSurfaceContainer,
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = SancarlinaPrimary
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Email
                    Text(
                        "Email",
                        style = MaterialTheme.typography.labelLarge,
                        color = SancarlinaOnSurfaceVariant,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("tu@email.com") },
                        leadingIcon = { Icon(Icons.Default.Mail, null, tint = SancarlinaOutline) },
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = SancarlinaSurfaceContainer,
                            focusedContainerColor = SancarlinaSurfaceContainer,
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = SancarlinaPrimary
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password
                    Text(
                        "Contraseña",
                        style = MaterialTheme.typography.labelLarge,
                        color = SancarlinaOnSurfaceVariant,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("••••••••") },
                        leadingIcon = { Icon(Icons.Default.Lock, null, tint = SancarlinaOutline) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    null,
                                    tint = SancarlinaOutline
                                )
                            }
                        },
                        shape = RoundedCornerShape(24.dp),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = SancarlinaSurfaceContainer,
                            focusedContainerColor = SancarlinaSurfaceContainer,
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = SancarlinaPrimary
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Confirm Password
                    Text(
                        "Confirmar Contraseña",
                        style = MaterialTheme.typography.labelLarge,
                        color = SancarlinaOnSurfaceVariant,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("••••••••") },
                        leadingIcon = { Icon(Icons.Default.LockReset, null, tint = SancarlinaOutline) },
                        shape = RoundedCornerShape(24.dp),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = SancarlinaSurfaceContainer,
                            focusedContainerColor = SancarlinaSurfaceContainer,
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = SancarlinaPrimary
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Register Button
                    Button(
                        onClick = { viewModel.register(name, email, password, confirmPassword, onRegisterSuccess) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SancarlinaSecondary),
                        shape = RoundedCornerShape(28.dp),
                        enabled = !uiState.isLoading
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(color = SancarlinaOnSecondary, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Registrarme", fontWeight = FontWeight.Medium, fontSize = 16.sp)
                        }
                    }

                    if (uiState.error != null) {
                        Text(
                            text = uiState.error ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(top = 8.dp).fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "¿Ya tienes cuenta?", color = SancarlinaOnSurfaceVariant)
                TextButton(onClick = onBack) {
                    Text(text = "Inicia sesión", color = SancarlinaPrimary, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

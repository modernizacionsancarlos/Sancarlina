package com.sancarlina.app.ui.features.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sancarlina.app.ui.theme.*
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordContent(onBack: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current

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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = SancarlinaPrimary)
                    }
                    Text(
                        text = "Recuperar Cuenta",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = SancarlinaOnSurface
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    modifier = Modifier.size(120.dp),
                    shape = RoundedCornerShape(32.dp),
                    color = SancarlinaSurfaceContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.LockReset,
                            contentDescription = null,
                            tint = SancarlinaPrimary.copy(alpha = 0.8f),
                            modifier = Modifier.size(64.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Ingresa tu correo electrónico registrado. Te enviaremos un enlace para restablecer tu contraseña.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = SancarlinaOnSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("tu@email.com") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    leadingIcon = { Icon(Icons.Default.Mail, null, tint = SancarlinaOutline) },
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = SancarlinaSurfaceContainerLowest,
                        focusedContainerColor = SancarlinaSurfaceContainerLowest,
                        unfocusedBorderColor = SancarlinaOutlineVariant,
                        focusedBorderColor = SancarlinaPrimary
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(48.dp))

                Button(
                    onClick = {
                        if (email.isBlank()) {
                            Toast.makeText(context, "Ingresa tu email", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        isLoading = true
                        auth.sendPasswordResetEmail(email)
                            .addOnSuccessListener {
                                isLoading = false
                                Toast.makeText(context, "Enlace enviado. Revisa tu correo.", Toast.LENGTH_LONG).show()
                                onBack()
                            }
                            .addOnFailureListener {
                                isLoading = false
                                Toast.makeText(context, "Error: ${it.localizedMessage}", Toast.LENGTH_LONG).show()
                            }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SancarlinaSecondary),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("ENVIAR ENLACE", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

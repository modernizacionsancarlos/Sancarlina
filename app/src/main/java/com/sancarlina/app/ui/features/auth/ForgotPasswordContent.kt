package com.sancarlina.app.ui.features.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.sancarlina.app.R
import com.sancarlina.app.ui.components.SancarlinaCard
import com.sancarlina.app.ui.components.SancarlinaPrimaryButton
import com.sancarlina.app.ui.components.SancarlinaTextField
import com.sancarlina.app.ui.components.SancarlinaTopBar
import com.sancarlina.app.ui.theme.*

@Composable
fun ForgotPasswordContent(onBack: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current
    val forgotEmailBlankMessage = stringResource(R.string.forgot_email_blank)
    val forgotEmailSentMessage = stringResource(R.string.forgot_email_sent)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        SancarlinaTopBar(
            title = stringResource(R.string.forgot_title),
            onBack = onBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            SancarlinaCard {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        modifier = Modifier.size(88.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.LockReset,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(44.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = stringResource(R.string.forgot_message),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    SancarlinaTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = stringResource(R.string.login_email_label),
                        placeholder = stringResource(R.string.login_email_hint)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    if (isLoading) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    } else {
                        SancarlinaPrimaryButton(
                            text = stringResource(R.string.forgot_cta),
                            onClick = {
                                val sanitizedEmail = com.sancarlina.app.utils.InputValidator.sanitizeText(email, 80)
                                if (sanitizedEmail.isBlank()) {
                                    Toast.makeText(
                                        context,
                                        forgotEmailBlankMessage,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@SancarlinaPrimaryButton
                                }
                                
                                if (!com.sancarlina.app.utils.InputValidator.isValidEmail(sanitizedEmail)) {
                                    Toast.makeText(
                                        context,
                                        "Formato de correo electrónico inválido",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@SancarlinaPrimaryButton
                                }

                                if (!com.sancarlina.app.utils.RateLimiter.isActionAllowed("forgot_password", 30000L)) {
                                    val remainingSecs = (com.sancarlina.app.utils.RateLimiter.getRemainingTime("forgot_password", 30000L) / 1000) + 1
                                    Toast.makeText(
                                        context,
                                        "Espera $remainingSecs segundos antes de enviar otra solicitud.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    return@SancarlinaPrimaryButton
                                }

                                isLoading = true
                                auth.sendPasswordResetEmail(sanitizedEmail)
                                    .addOnSuccessListener {
                                        isLoading = false
                                        Toast.makeText(
                                            context,
                                            forgotEmailSentMessage,
                                            Toast.LENGTH_LONG
                                        ).show()
                                        onBack()
                                    }
                                    .addOnFailureListener {
                                        isLoading = false
                                        Toast.makeText(
                                            context,
                                            "Error: ${it.localizedMessage}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                            }
                        )
                    }
                }
            }
        }
    }
}

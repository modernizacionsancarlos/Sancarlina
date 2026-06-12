package com.sancarlina.app.ui.features.map

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.sancarlina.app.R
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sancarlina.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RateCommerceContent(commerceId: String, onBack: () -> Unit) {
    var rating by remember { mutableIntStateOf(0) }
    var comment by remember { mutableStateOf("") }

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
                        text = "Calificar Comercio",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = SancarlinaOnSurface
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "¿Qué te pareció tu experiencia?",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = SancarlinaOnSurface,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(32.dp))

                // Stars
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(5) { index ->
                        val starIndex = index + 1
                        IconButton(onClick = { rating = starIndex }, modifier = Modifier.size(48.dp)) {
                            Icon(
                                imageVector = if (starIndex <= rating) Icons.Default.Star else Icons.Default.StarOutline,
                                contentDescription = null,
                                tint = if (starIndex <= rating) Color(0xFFF59E0B) else SancarlinaOutlineVariant,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    modifier = Modifier.fillMaxWidth().height(160.dp),
                    placeholder = { Text("Cuéntanos más detalles... (Opcional)") },
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = SancarlinaSurfaceContainerLowest,
                        focusedContainerColor = SancarlinaSurfaceContainerLowest,
                        unfocusedBorderColor = SancarlinaOutlineVariant,
                        focusedBorderColor = SancarlinaPrimary
                    )
                )

                Spacer(modifier = Modifier.height(48.dp))

                Button(
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SancarlinaPrimary),
                    shape = RoundedCornerShape(28.dp),
                    enabled = rating > 0
                ) {
                    Text("PUBLICAR RESEÑA", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

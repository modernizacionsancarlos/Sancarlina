package com.sancarlina.app.ui.features.emprendimiento

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
fun EmprendimientoContent(onBack: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

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
                        text = "Sumá tu Emprendimiento",
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
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    "Completa los datos de tu negocio para que podamos validarlo y sumarlo al mapa oficial de San Carlos.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SancarlinaOnSurfaceVariant
                )

                EmprendimientoTextField("Nombre del Emprendimiento", "Ej: Bodega del Valle", name) { name = it }
                EmprendimientoTextField("Rubro / Categoría", "Ej: Gastronomía", category) { category = it }
                
                Column {
                    Text("Descripción", style = MaterialTheme.typography.labelLarge, color = SancarlinaOnSurfaceVariant, modifier = Modifier.padding(bottom = 8.dp))
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        placeholder = { Text("Cuéntanos brevemente qué haces...") },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = SancarlinaSurfaceContainerLowest,
                            focusedContainerColor = SancarlinaSurfaceContainerLowest,
                            unfocusedBorderColor = SancarlinaOutlineVariant,
                            focusedBorderColor = SancarlinaPrimary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SancarlinaPrimary),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text("ENVIAR POSTULACIÓN", fontWeight = FontWeight.Bold)
                }
                
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun EmprendimientoTextField(label: String, placeholder: String, value: String, onValueChange: (String) -> Unit) {
    Column {
        Text(label, style = MaterialTheme.typography.labelLarge, color = SancarlinaOnSurfaceVariant, modifier = Modifier.padding(bottom = 8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder) },
            shape = RoundedCornerShape(28.dp),
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

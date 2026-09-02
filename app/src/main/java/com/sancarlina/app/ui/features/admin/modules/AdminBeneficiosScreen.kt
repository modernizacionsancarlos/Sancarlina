package com.sancarlina.app.ui.features.admin.modules

import androidx.compose.material3.MaterialTheme

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
import androidx.compose.ui.unit.dp
import com.sancarlina.app.data.repository.Benefit
import com.sancarlina.app.ui.components.SancarlinaElevatedCard
import com.sancarlina.app.ui.components.SancarlinaTextField
import com.sancarlina.app.ui.features.admin.components.AdminAddFab
import com.sancarlina.app.ui.features.admin.components.AdminMetricCard
import com.sancarlina.app.ui.features.admin.components.AdminScreenTopBar
import com.sancarlina.app.ui.features.admin.components.AdminStatusPill
import com.sancarlina.app.ui.theme.*
import com.sancarlina.app.viewmodel.admin.AdminBeneficiosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminBeneficiosScreen(
    viewModel: AdminBeneficiosViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var editingBenefit by remember { mutableStateOf<Benefit?>(null) }

    Scaffold(
        topBar = {
            AdminScreenTopBar(title = "Administrar Beneficios", onBack = onBack)
        },
        floatingActionButton = {
            AdminAddFab(
                label = "Nuevo beneficio",
                onClick = {
                    editingBenefit = Benefit()
                    showDialog = true
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            val activeBenefits = uiState.benefits.count { it.active }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AdminMetricCard(
                    "Total activos",
                    activeBenefits.toString(),
                    Modifier.weight(1.2f),
                    emphasized = true
                )
                AdminMetricCard(
                    "Pausados",
                    (uiState.benefits.size - activeBenefits).toString(),
                    Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else if (uiState.benefits.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No se encontraron beneficios registrados.")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.benefits, key = { it.id }) { benefit ->
                        BenefitAdminItem(
                            benefit = benefit,
                            onEdit = {
                                editingBenefit = benefit
                                showDialog = true
                            },
                            onToggleActive = {
                                viewModel.toggleActive(benefit.id, benefit.active)
                            },
                            onDelete = {
                                viewModel.deleteBenefit(benefit.id)
                            }
                        )
                    }
                }
            }
        }
    }

    if (showDialog && editingBenefit != null) {
        BenefitEditorDialog(
            benefit = editingBenefit!!,
            onDismiss = { showDialog = false },
            onSave = { updatedBenefit ->
                viewModel.saveBenefit(updatedBenefit) {
                    showDialog = false
                }
            }
        )
    }
}

@Composable
private fun BenefitAdminItem(
    benefit: Benefit,
    onEdit: () -> Unit,
    onToggleActive: () -> Unit,
    onDelete: () -> Unit
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
                imageVector = Icons.Default.CardGiftcard,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                AdminStatusPill(
                    text = if (benefit.active) "Activo" else "Pausado",
                    active = benefit.active
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = benefit.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Costo: ${if (benefit.points_cost > 0) benefit.points_cost else benefit.cost} Puntos",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                if (benefit.description.isNotBlank()) {
                    Text(
                        text = benefit.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            IconButton(onClick = onEdit) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = onToggleActive) {
                Icon(
                    imageVector = if (benefit.active) Icons.Default.ToggleOn else Icons.Default.ToggleOff,
                    contentDescription = "Estado",
                    tint = if (benefit.active) Color(0xFF4CAF50) else Color.Gray
                )
            }
            IconButton(onClick = onDelete) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun BenefitEditorDialog(
    benefit: Benefit,
    onDismiss: () -> Unit,
    onSave: (Benefit) -> Unit
) {
    var title by remember { mutableStateOf(benefit.title) }
    var costStr by remember { mutableStateOf((if (benefit.points_cost > 0) benefit.points_cost else benefit.cost).toString()) }
    var description by remember { mutableStateOf(benefit.description) }
    var industry by remember { mutableStateOf(benefit.industry) }
    var active by remember { mutableStateOf(benefit.active) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (benefit.id.isBlank()) "Nuevo Beneficio" else "Editar Beneficio") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SancarlinaTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = "Título del Beneficio *"
                )
                SancarlinaTextField(
                    value = costStr,
                    onValueChange = { costStr = it },
                    label = "Costo en Puntos *",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                SancarlinaTextField(
                    value = industry,
                    onValueChange = { industry = it },
                    label = "Rubro / Categoría"
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = active, onCheckedChange = { active = it })
                    Text("Beneficio activo")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val cost = costStr.toIntOrNull() ?: 0
                    if (title.isNotBlank()) {
                        onSave(
                            benefit.copy(
                                title = title,
                                cost = cost,
                                points_cost = cost,
                                description = description,
                                industry = industry,
                                active = active
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

package com.sancarlina.app.ui.features.admin.modules

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sancarlina.app.data.repository.Area
import com.sancarlina.app.ui.components.SancarlinaTextField
import com.sancarlina.app.ui.features.admin.components.AdminAddFab
import com.sancarlina.app.ui.features.admin.components.AdminMetricCard
import com.sancarlina.app.ui.features.admin.components.AdminScreenTopBar
import com.sancarlina.app.ui.features.admin.components.AdminStatusPill
import com.sancarlina.app.ui.theme.*
import com.sancarlina.app.viewmodel.admin.AdminZonasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminZonasScreen(
    viewModel: AdminZonasViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var editingArea by remember { mutableStateOf<Area?>(null) }

    Scaffold(
        topBar = {
            AdminScreenTopBar(
                title = "Administrar Zonas",
                onBack = onBack,
                actions = {
                    IconButton(
                        onClick = { viewModel.ensureSuggestedAreas() }
                    ) {
                        Icon(imageVector = Icons.Default.AutoFixHigh, contentDescription = "Cargar sugeridas", tint = SancarlinaPrimary)
                    }
                }
            )
        },
        floatingActionButton = {
            AdminAddFab(
                label = "Nueva zona",
                onClick = {
                    editingArea = Area()
                    showDialog = true
                }
            )
        },
        containerColor = SancarlinaBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            val activeAreas = uiState.areas.count { it.active }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AdminMetricCard("Total zonas", uiState.areas.size.toString(), Modifier.weight(1f))
                AdminMetricCard("Activas", activeAreas.toString(), Modifier.weight(1f), emphasized = true)
                AdminMetricCard(
                    "Revisión",
                    (uiState.areas.size - activeAreas).toString(),
                    Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            uiState.successMessage?.let { msg ->
                Surface(
                    color = SancarlinaPrimary.copy(alpha = 0.15f),
                    shape = SancarlinaChipShape,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                ) {
                    Text(
                        text = msg,
                        color = SancarlinaPrimary,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = SancarlinaPrimary)
                }
            } else if (uiState.areas.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No se encontraron zonas turísticas.")
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { viewModel.ensureSuggestedAreas() },
                            colors = ButtonDefaults.buttonColors(containerColor = SancarlinaPrimary)
                        ) {
                            Icon(imageVector = Icons.Default.AutoFixHigh, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Cargar 15 Zonas Sugeridas")
                        }
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.areas, key = { it.id }) { area ->
                        AreaAdminItem(
                            area = area,
                            onEdit = {
                                editingArea = area
                                showDialog = true
                            },
                            onDelete = {
                                viewModel.deleteArea(area.id)
                            }
                        )
                    }
                }
            }
        }
    }

    if (showDialog && editingArea != null) {
        AreaEditorDialog(
            area = editingArea!!,
            onDismiss = { showDialog = false },
            onSave = { updatedArea ->
                viewModel.saveArea(updatedArea) {
                    showDialog = false
                }
            }
        )
    }
}

@Composable
private fun AreaAdminItem(
    area: Area,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = SancarlinaCardShape,
        colors = CardDefaults.cardColors(containerColor = SancarlinaSurfaceContainerLowest),
        border = androidx.compose.foundation.BorderStroke(1.dp, SancarlinaOutlineVariant.copy(alpha = 0.35f)),
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
                imageVector = Icons.Default.Map,
                contentDescription = null,
                tint = SancarlinaPrimary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = area.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = SancarlinaChipShape,
                        color = if (area.category == "thematic") SancarlinaSecondary.copy(alpha = 0.15f) else SancarlinaPrimary.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = if (area.category == "thematic") "Temática" else "Geográfica",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (area.category == "thematic") SancarlinaSecondary else SancarlinaPrimary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                AdminStatusPill(
                    text = if (area.active) "Activa" else "Inactiva",
                    active = area.active
                )
                if (area.description.isNotBlank()) {
                    Text(
                        text = area.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = SancarlinaOnSurfaceVariant
                    )
                }
                Text(
                    text = "Orden: ${area.order} | Slug: ${area.slug}",
                    style = MaterialTheme.typography.labelSmall,
                    color = SancarlinaOnSurfaceVariant
                )
            }
            IconButton(onClick = onEdit) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Editar", tint = SancarlinaPrimary)
            }
            IconButton(onClick = onDelete) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun AreaEditorDialog(
    area: Area,
    onDismiss: () -> Unit,
    onSave: (Area) -> Unit
) {
    var name by remember { mutableStateOf(area.name) }
    var slug by remember { mutableStateOf(area.slug) }
    var description by remember { mutableStateOf(area.description) }
    var orderStr by remember { mutableStateOf(area.order.toString()) }
    var category by remember { mutableStateOf(area.category) }
    var active by remember { mutableStateOf(area.active) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (area.id.isBlank()) "Nueva Zona" else "Editar Zona") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SancarlinaTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Nombre de la Zona *"
                )
                SancarlinaTextField(
                    value = slug,
                    onValueChange = { slug = it },
                    label = "Slug (opcional)"
                )
                SancarlinaTextField(
                    value = orderStr,
                    onValueChange = { orderStr = it },
                    label = "Número de Orden (1, 2, 3...)"
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    FilterChip(
                        selected = category == "geographic",
                        onClick = { category = "geographic" },
                        label = { Text("Geográfica") }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    FilterChip(
                        selected = category == "thematic",
                        onClick = { category = "thematic" },
                        label = { Text("Temática") }
                    )
                }
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = active, onCheckedChange = { active = it })
                    Text("Zona activa")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val orderVal = orderStr.toIntOrNull() ?: 0
                        onSave(
                            area.copy(
                                name = name,
                                slug = slug,
                                description = description,
                                order = orderVal,
                                category = category,
                                active = active
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SancarlinaPrimary)
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

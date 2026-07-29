package com.sancarlina.app.ui.features.admin.modules

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
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
import com.sancarlina.app.ui.components.SancarlinaElevatedCard
import com.sancarlina.app.ui.components.SancarlinaTextField
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
            TopAppBar(
                title = { Text("Gestión de Zonas Turísticas", fontWeight = FontWeight.Bold) },
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
                onClick = {
                    editingArea = Area()
                    showDialog = true
                },
                containerColor = SancarlinaPrimary,
                contentColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Nueva Zona")
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
            } else if (uiState.areas.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No se encontraron zonas turísticas registradas.")
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
                imageVector = Icons.Default.Map,
                contentDescription = null,
                tint = SancarlinaPrimary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = area.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (area.description.isNotBlank()) {
                    Text(
                        text = area.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = SancarlinaOnSurfaceVariant
                    )
                }
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
    var description by remember { mutableStateOf(area.description) }

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
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onSave(area.copy(name = name, description = description))
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

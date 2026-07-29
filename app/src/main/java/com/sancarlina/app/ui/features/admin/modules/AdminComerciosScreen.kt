package com.sancarlina.app.ui.features.admin.modules

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.sancarlina.app.data.models.Tenant
import com.sancarlina.app.ui.components.SancarlinaElevatedCard
import com.sancarlina.app.ui.components.SancarlinaPrimaryButton
import com.sancarlina.app.ui.components.SancarlinaTextField
import com.sancarlina.app.ui.theme.*
import com.sancarlina.app.viewmodel.admin.AdminComerciosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminComerciosScreen(
    viewModel: AdminComerciosViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var editingTenant by remember { mutableStateOf<Tenant?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Comercios", fontWeight = FontWeight.Bold) },
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
                    editingTenant = Tenant()
                    showDialog = true
                },
                containerColor = SancarlinaPrimary,
                contentColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Nuevo Comercio")
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
            SancarlinaTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                label = "Buscar comercio o rubro",
                placeholder = "Ej. Almacén, Don Pedro..."
            )

            Spacer(modifier = Modifier.height(12.dp))

            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                SegmentedButton(
                    selected = uiState.selectedStatusFilter == "all",
                    onClick = { viewModel.onStatusFilterChanged("all") },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3)
                ) {
                    Text("Todos")
                }
                SegmentedButton(
                    selected = uiState.selectedStatusFilter == "active",
                    onClick = { viewModel.onStatusFilterChanged("active") },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3)
                ) {
                    Text("Activos")
                }
                SegmentedButton(
                    selected = uiState.selectedStatusFilter == "inactive",
                    onClick = { viewModel.onStatusFilterChanged("inactive") },
                    shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3)
                ) {
                    Text("Inactivos")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = SancarlinaPrimary)
                }
            } else if (uiState.filteredTenants.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No se encontraron comercios registrados.")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.filteredTenants, key = { it.id }) { tenant ->
                        TenantAdminItem(
                            tenant = tenant,
                            onEdit = {
                                editingTenant = tenant
                                showDialog = true
                            },
                            onToggleStatus = {
                                viewModel.toggleTenantStatus(tenant.id, tenant.status)
                            },
                            onDelete = {
                                viewModel.deleteTenant(tenant.id)
                            }
                        )
                    }
                }
            }
        }
    }

    if (showDialog && editingTenant != null) {
        TenantEditorDialog(
            tenant = editingTenant!!,
            onDismiss = { showDialog = false },
            onSave = { updatedTenant ->
                viewModel.saveTenant(updatedTenant) {
                    showDialog = false
                }
            }
        )
    }
}

@Composable
private fun TenantAdminItem(
    tenant: Tenant,
    onEdit: () -> Unit,
    onToggleStatus: () -> Unit,
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
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = tenant.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = SancarlinaChipShape,
                        color = if (tenant.status == "active") Color(0xFF4CAF50).copy(alpha = 0.15f) else Color.Red.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = if (tenant.status == "active") "Activo" else "Inactivo",
                            color = if (tenant.status == "active") Color(0xFF2E7D32) else Color.Red,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Rubro: ${tenant.industry.ifBlank { "General" }}",
                    style = MaterialTheme.typography.bodySmall,
                    color = SancarlinaOnSurfaceVariant
                )
                if (tenant.address.isNotBlank()) {
                    Text(
                        text = "Dirección: ${tenant.address}",
                        style = MaterialTheme.typography.labelSmall,
                        color = SancarlinaOnSurfaceVariant
                    )
                }
            }

            IconButton(onClick = onEdit) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Editar", tint = SancarlinaPrimary)
            }
            IconButton(onClick = onToggleStatus) {
                Icon(
                    imageVector = if (tenant.status == "active") Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = "Cambiar Estado",
                    tint = SancarlinaOnSurfaceVariant
                )
            }
            IconButton(onClick = onDelete) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun TenantEditorDialog(
    tenant: Tenant,
    onDismiss: () -> Unit,
    onSave: (Tenant) -> Unit
) {
    var name by remember { mutableStateOf(tenant.name) }
    var industry by remember { mutableStateOf(tenant.industry) }
    var status by remember { mutableStateOf(tenant.status.ifBlank { "active" }) }
    var address by remember { mutableStateOf(tenant.address) }
    var geoCoordinates by remember { mutableStateOf(tenant.geo_coordinates) }
    var contactEmail by remember { mutableStateOf(tenant.contact_email) }
    var contactPhone by remember { mutableStateOf(tenant.contact_phone) }
    var description by remember { mutableStateOf(tenant.description) }
    var coverUrl by remember { mutableStateOf(tenant.cover_url) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (tenant.id.isBlank()) "Nuevo Comercio" else "Editar Comercio") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SancarlinaTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Nombre del Comercio *"
                )
                SancarlinaTextField(
                    value = industry,
                    onValueChange = { industry = it },
                    label = "Rubro / Categoría *"
                )
                SancarlinaTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = "Dirección Escrita"
                )
                SancarlinaTextField(
                    value = geoCoordinates,
                    onValueChange = { geoCoordinates = it },
                    label = "Coordenadas GPS (lat,lng)"
                )
                SancarlinaTextField(
                    value = contactPhone,
                    onValueChange = { contactPhone = it },
                    label = "Teléfono de Contacto",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )
                SancarlinaTextField(
                    value = contactEmail,
                    onValueChange = { contactEmail = it },
                    label = "Correo de Contacto",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
                SancarlinaTextField(
                    value = coverUrl,
                    onValueChange = { coverUrl = it },
                    label = "URL Foto Portada"
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
                        val updated = tenant.copy(
                            name = name,
                            industry = industry,
                            status = status,
                            address = address,
                            geoCoordinates = geoCoordinates,
                            contactEmail = contactEmail,
                            contactPhone = contactPhone,
                            description = description,
                            coverUrl = coverUrl,
                            imageUrl = coverUrl
                        )
                        onSave(updated)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SancarlinaPrimary)
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

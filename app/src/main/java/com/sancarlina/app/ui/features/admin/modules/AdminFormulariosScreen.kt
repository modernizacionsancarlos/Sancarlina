package com.sancarlina.app.ui.features.admin.modules

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.unit.dp
import com.sancarlina.app.data.models.FormField
import com.sancarlina.app.data.models.FormSchema
import com.sancarlina.app.data.models.FormTemplate
import com.sancarlina.app.data.repository.SubmissionAdmin
import com.sancarlina.app.ui.components.SancarlinaElevatedCard
import com.sancarlina.app.ui.components.SancarlinaPrimaryButton
import com.sancarlina.app.ui.components.SancarlinaTextField
import com.sancarlina.app.ui.theme.*
import com.sancarlina.app.viewmodel.admin.AdminFormulariosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminFormulariosScreen(
    viewModel: AdminFormulariosViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val tabTitles = listOf("Dashboard", "Plantillas", "Editor", "Respuestas", "Aceptaciones")

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Gestión de Formularios", fontWeight = FontWeight.Bold) },
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
                TabRow(
                    selectedTabIndex = uiState.selectedTab,
                    containerColor = SancarlinaBackground,
                    contentColor = SancarlinaPrimary
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = uiState.selectedTab == index,
                            onClick = { viewModel.setTab(index) },
                            text = { Text(title, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
            }
        },
        containerColor = SancarlinaBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = SancarlinaPrimary)
                }
            } else {
                when (uiState.selectedTab) {
                    0 -> DashboardTab(uiState = uiState, viewModel = viewModel)
                    1 -> PlantillasTab(uiState = uiState, viewModel = viewModel)
                    2 -> EditorTab(uiState = uiState, viewModel = viewModel)
                    3 -> RespuestasTab(uiState = uiState, viewModel = viewModel)
                    4 -> AceptacionesTab(uiState = uiState, viewModel = viewModel)
                }
            }
        }
    }
}

// --- TAB 0: DASHBOARD ---
@Composable
private fun DashboardTab(
    uiState: com.sancarlina.app.viewmodel.admin.AdminFormulariosUiState,
    viewModel: AdminFormulariosViewModel
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Esquemas de Formularios",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Button(
                onClick = {
                    viewModel.selectSchemaForEditor(FormSchema(title = "Nuevo Formulario"))
                },
                colors = ButtonDefaults.buttonColors(containerColor = SancarlinaPrimary)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Crear Esquema")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (uiState.schemas.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hay formularios creados aún.")
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(uiState.schemas, key = { it.id }) { schema ->
                    Card(
                        shape = SancarlinaCardShape,
                        colors = CardDefaults.cardColors(containerColor = SancarlinaSurfaceContainerLowest),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = schema.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(onClick = { viewModel.selectSchemaForEditor(schema) }) {
                                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Editar", tint = SancarlinaPrimary)
                                }
                                IconButton(onClick = { viewModel.deleteSchema(schema.id) }) {
                                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                            if (schema.description.isNotBlank()) {
                                Text(
                                    text = schema.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SancarlinaOnSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Switch(
                                        checked = schema.isPublic,
                                        onCheckedChange = { viewModel.togglePublic(schema.id, schema.isPublic) }
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Público", style = MaterialTheme.typography.labelSmall)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Switch(
                                        checked = schema.acceptsResponses,
                                        onCheckedChange = { viewModel.toggleAcceptsResponses(schema.id, schema.acceptsResponses) }
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Acepta Respuestas", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- TAB 1: PLANTILLAS ---
@Composable
private fun PlantillasTab(
    uiState: com.sancarlina.app.viewmodel.admin.AdminFormulariosUiState,
    viewModel: AdminFormulariosViewModel
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(uiState.templates, key = { it.id }) { template ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        tint = SancarlinaPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = template.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(text = template.description, style = MaterialTheme.typography.bodySmall, color = SancarlinaOnSurfaceVariant)
                    }
                    Button(
                        onClick = { viewModel.createNewSchemaFromTemplate(template) },
                        colors = ButtonDefaults.buttonColors(containerColor = SancarlinaPrimary)
                    ) {
                        Text("Usar")
                    }
                }
            }
        }
    }
}

// --- TAB 2: EDITOR ---
@Composable
private fun EditorTab(
    uiState: com.sancarlina.app.viewmodel.admin.AdminFormulariosUiState,
    viewModel: AdminFormulariosViewModel
) {
    val schema = uiState.activeSchemaForEditor ?: FormSchema(title = "Nuevo Formulario")
    var title by remember(schema) { mutableStateOf(schema.title) }
    var description by remember(schema) { mutableStateOf(schema.description) }
    var fields by remember(schema) { mutableStateOf(schema.fields) }
    var showAddFieldDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Editor de Esquema", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = SancarlinaPrimary)

        SancarlinaTextField(
            value = title,
            onValueChange = { title = it },
            label = "Título del Formulario *"
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Campos Dinámicos (${fields.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            OutlinedButton(onClick = { showAddFieldDialog = true }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Agregar Campo")
            }
        }

        fields.forEachIndexed { index, field ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "${index + 1}. ${field.label}", fontWeight = FontWeight.Bold)
                        Text(text = "Tipo: ${field.type} | Mapping: ${field.tenantMapping ?: "Ninguno"}", style = MaterialTheme.typography.labelSmall)
                    }
                    IconButton(onClick = { fields = fields - field }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        SancarlinaPrimaryButton(
            text = "Guardar Esquema",
            onClick = {
                val updatedSchema = schema.copy(
                    title = title,
                    description = description,
                    fields = fields
                )
                viewModel.saveSchema(updatedSchema) {
                    viewModel.setTab(0)
                }
            }
        )
    }

    if (showAddFieldDialog) {
        AddFieldDialog(
            onDismiss = { showAddFieldDialog = false },
            onFieldAdded = { newField ->
                fields = fields + newField
                showAddFieldDialog = false
            }
        )
    }
}

@Composable
private fun AddFieldDialog(
    onDismiss: () -> Unit,
    onFieldAdded: (FormField) -> Unit
) {
    var label by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("text") }
    var required by remember { mutableStateOf(false) }
    var tenantMapping by remember { mutableStateOf("") }
    var helpText by remember { mutableStateOf("") }

    val fieldTypes = listOf("text", "textarea", "number", "selector", "radio", "boolean", "email", "phone", "image", "gps", "section")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar Campo Dinámico") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                SancarlinaTextField(value = label, onValueChange = { label = it }, label = "Etiqueta / Label *")

                Text("Tipo de Campo:", style = MaterialTheme.typography.labelMedium)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(fieldTypes) { t ->
                        FilterChip(
                            selected = type == t,
                            onClick = { type = t },
                            label = { Text(t) }
                        )
                    }
                }

                SancarlinaTextField(value = tenantMapping, onValueChange = { tenantMapping = it }, label = "Mapeo Comercio (ej. name, area_id, cover_url)")
                SancarlinaTextField(value = helpText, onValueChange = { helpText = it }, label = "Texto de Ayuda / Hint")

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = required, onCheckedChange = { required = it })
                    Text("Campo Obligatorio")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (label.isNotBlank()) {
                        val newField = FormField(
                            id = "field_${System.currentTimeMillis()}",
                            type = type,
                            label = label,
                            required = required,
                            tenantMapping = tenantMapping.ifBlank { null },
                            helpText = helpText.ifBlank { null }
                        )
                        onFieldAdded(newField)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SancarlinaPrimary)
            ) { Text("Agregar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

// --- TAB 3: RESPUESTAS ---
@Composable
private fun RespuestasTab(
    uiState: com.sancarlina.app.viewmodel.admin.AdminFormulariosUiState,
    viewModel: AdminFormulariosViewModel
) {
    if (uiState.submissions.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No hay respuestas recibidas.")
        }
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(uiState.submissions, key = { it.id }) { submission ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = submission.form_title.ifBlank { "Envío ${submission.id.take(8)}" },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            Surface(
                                shape = MaterialTheme.shapes.extraSmall,
                                color = when (submission.status) {
                                    "approved" -> Color(0xFF4CAF50).copy(alpha = 0.2f)
                                    "rejected" -> Color.Red.copy(alpha = 0.2f)
                                    else -> Color.Unspecified
                                }
                            ) {
                                Text(
                                    text = submission.status.uppercase(),
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "ID: ${submission.id}", style = MaterialTheme.typography.labelSmall, color = SancarlinaOnSurfaceVariant)
                        Text(text = "Campos enviados: ${submission.data.size}", style = MaterialTheme.typography.bodySmall)

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(onClick = { viewModel.updateSubmissionStatus(submission.id, "approved") }) {
                                Text("Aprobar")
                            }
                            OutlinedButton(onClick = { viewModel.updateSubmissionStatus(submission.id, "rejected") }) {
                                Text("Rechazar")
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- TAB 4: ACEPTACIONES ---
@Composable
private fun AceptacionesTab(
    uiState: com.sancarlina.app.viewmodel.admin.AdminFormulariosUiState,
    viewModel: AdminFormulariosViewModel
) {
    val pendingApproved = uiState.submissions.filter { it.status == "approved" || it.status == "pending" }

    if (pendingApproved.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No hay respuestas pendientes para publicar como comercio.")
        }
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(pendingApproved, key = { it.id }) { submission ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = submission.form_title.ifBlank { "Solicitud Comercio" }, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        submission.data.forEach { (k, v) ->
                            Text(text = "$k: $v", style = MaterialTheme.typography.bodySmall, maxLines = 1)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        SancarlinaPrimaryButton(
                            text = "Aceptar y Publicar en Comercios",
                            onClick = {
                                viewModel.publishSubmissionToTenant(submission) {}
                            }
                        )
                    }
                }
            }
        }
    }
}

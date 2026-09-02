package com.sancarlina.app.ui.features.admin.modules

import androidx.compose.material3.MaterialTheme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.sancarlina.app.data.models.FormField
import com.sancarlina.app.data.models.FormSchema
import com.sancarlina.app.data.models.FormTemplate
import com.sancarlina.app.data.repository.SubmissionAdmin
import com.sancarlina.app.ui.components.SancarlinaElevatedCard
import com.sancarlina.app.ui.components.SancarlinaPrimaryButton
import com.sancarlina.app.ui.components.SancarlinaTextField
import com.sancarlina.app.ui.features.admin.components.AdminMetricCard
import com.sancarlina.app.ui.features.admin.components.AdminScreenTopBar
import com.sancarlina.app.ui.features.admin.components.AdminStatusPill
import com.sancarlina.app.ui.features.forms.FormAnswerPresentation
import com.sancarlina.app.ui.theme.*
import com.sancarlina.app.viewmodel.admin.AdminFormulariosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminFormulariosScreen(
    viewModel: AdminFormulariosViewModel,
    onBack: () -> Unit,
    onStartRegistration: () -> Unit,
    onViewPending: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val tabTitles = listOf("Resumen", "Plantillas", "Editor", "Respuestas", "Aprobación")

    Scaffold(
        topBar = {
            Column {
                AdminScreenTopBar(
                    title = "Formularios",
                    onBack = onBack,
                    actions = {
                        IconButton(onClick = onViewPending) {
                            Icon(Icons.Default.CloudSync, contentDescription = "Ver envíos guardados")
                        }
                    }
                )
                PrimaryScrollableTabRow(
                    selectedTabIndex = uiState.selectedTab,
                    edgePadding = 12.dp,
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = uiState.selectedTab == index,
                            onClick = { viewModel.setTab(index) },
                            text = { Text(title, style = MaterialTheme.typography.labelLarge, maxLines = 1) }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (uiState.selectedTab == 0) {
                ExtendedFloatingActionButton(
                    onClick = onStartRegistration,
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = Color.White,
                    icon = { Icon(Icons.AutoMirrored.Filled.Assignment, contentDescription = null) },
                    text = { Text("Responder formulario", fontWeight = FontWeight.Bold) }
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
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
    var previewingSchema by remember { mutableStateOf<FormSchema?>(null) }
    var viewingSubmissionsSchema by remember { mutableStateOf<FormSchema?>(null) }

    // Ordenar y agrupar esquemas
    val sortedSchemas = remember(uiState.schemas, uiState.sortBy, uiState.sortDirection) {
        val sorted = if (uiState.sortBy == "name") {
            if (uiState.sortDirection == "asc") uiState.schemas.sortedBy { it.title.lowercase() }
            else uiState.schemas.sortedByDescending { it.title.lowercase() }
        } else {
            uiState.schemas
        }
        sorted
    }

    val groupedSchemas = remember(sortedSchemas, uiState.sortBy) {
        if (uiState.sortBy == "name") {
            sortedSchemas.groupBy { schema ->
                schema.title.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "#"
            }
        } else {
            mapOf("Formularios Registrados" to sortedSchemas)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        val publicCount = uiState.schemas.count { it.isPublic }
        val pendingCount = uiState.submissions.count { it.status == "pending" }
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item { AdminMetricCard("Total", uiState.schemas.size.toString(), Modifier.width(132.dp)) }
            item { AdminMetricCard("Publicados", publicCount.toString(), Modifier.width(150.dp), emphasized = true) }
            item {
                AdminMetricCard(
                    "Pendientes",
                    pendingCount.toString(),
                    Modifier.width(150.dp),
                    alert = pendingCount > 0
                )
            }
        }

        if (pendingCount > 0) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                shape = SancarlinaCardShape,
                color = MaterialTheme.colorScheme.errorContainer
            ) {
                Row(
                    modifier = Modifier.padding(13.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Text(
                        text = "$pendingCount respuestas pendientes de revisión",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Barra Superior de Controles (Lista/Grilla, Ordenamiento, Nuevo)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.toggleViewMode() }) {
                    Icon(
                        imageVector = if (uiState.isGridView) Icons.AutoMirrored.Filled.ViewList else Icons.Default.GridView,
                        contentDescription = "Cambiar vista",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = { viewModel.toggleSortDirection() }) {
                    Icon(
                        imageVector = if (uiState.sortDirection == "asc") Icons.Default.SortByAlpha else Icons.AutoMirrored.Filled.Sort,
                        contentDescription = "Cambiar orden",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Button(
                onClick = {
                    viewModel.selectSchemaForEditor(FormSchema(title = "Nuevo Formulario"))
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Nuevo formulario")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (uiState.schemas.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hay formularios creados aún.")
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 96.dp)
            ) {
                groupedSchemas.forEach { (groupHeader, schemaGroup) ->
                    item {
                        Text(
                            text = groupHeader,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }

                    items(schemaGroup, key = { it.id }) { schema ->
                        val submissionCount = uiState.submissions.count { it.form_id == schema.id }

                        FormSchemaCard(
                            schema = schema,
                            submissionCount = submissionCount,
                            onEdit = { viewModel.selectSchemaForEditor(schema) },
                            onDelete = { viewModel.deleteSchema(schema.id) },
                            onTogglePublic = { viewModel.togglePublic(schema.id, schema.isPublic) },
                            onToggleAccepts = { viewModel.toggleAcceptsResponses(schema.id, schema.acceptsResponses) },
                            onLivePreview = { previewingSchema = schema },
                            onViewSubmissions = { viewingSubmissionsSchema = schema }
                        )
                    }
                }
            }
        }
    }

    if (previewingSchema != null) {
        FormLivePreviewDialog(
            schema = previewingSchema!!,
            onDismiss = { previewingSchema = null }
        )
    }

    if (viewingSubmissionsSchema != null) {
        SchemaSubmissionsDialog(
            schema = viewingSubmissionsSchema!!,
            submissions = uiState.submissions.filter { it.form_id == viewingSubmissionsSchema!!.id },
            onDismiss = { viewingSubmissionsSchema = null },
            onUpdateStatus = { subId, status -> viewModel.updateSubmissionStatus(subId, status) }
        )
    }
}

@Composable
private fun FormSchemaCard(
    schema: FormSchema,
    submissionCount: Int,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onTogglePublic: (Boolean) -> Unit,
    onToggleAccepts: (Boolean) -> Unit,
    onLivePreview: () -> Unit,
    onViewSubmissions: () -> Unit
) {
    Card(
        shape = SancarlinaCardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    AdminStatusPill(
                        text = if (schema.isPublic) "Publicado" else "Borrador",
                        active = schema.isPublic
                    )
                    Text(
                        text = schema.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                FilledTonalIconButton(onClick = onEdit) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Editar formulario", tint = MaterialTheme.colorScheme.primary)
                }
                Spacer(Modifier.width(4.dp))
                FilledTonalIconButton(
                    onClick = onDelete,
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar formulario")
                }
            }
            if (schema.description.isNotBlank()) {
                Text(
                    text = schema.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            Surface(shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.surfaceContainerLow) {
                Column(Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.weight(1f)) {
                            Text("Visible en la aplicación", fontWeight = FontWeight.SemiBold)
                            Text("Los usuarios habilitados pueden encontrarlo", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(checked = schema.isPublic, onCheckedChange = onTogglePublic)
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.weight(1f)) {
                            Text("Recibir respuestas", fontWeight = FontWeight.SemiBold)
                            Text("Permite iniciar y enviar este formulario", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(checked = schema.acceptsResponses, onCheckedChange = onToggleAccepts)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(onClick = onLivePreview, modifier = Modifier.fillMaxWidth()) {
                Icon(imageVector = Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Ver formulario")
            }

            if (submissionCount > 0) {
                Button(
                    onClick = onViewSubmissions,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Icon(imageVector = Icons.Default.Inbox, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Revisar $submissionCount respuesta(s)", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// --- DIÁLOGOS DE PREVIEW Y RESPUESTAS ---
@Composable
private fun FormLivePreviewDialog(
    schema: FormSchema,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Vista Previa: ${schema.title}", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(text = schema.description.ifBlank { "Sin descripción." }, style = MaterialTheme.typography.bodyMedium)
                HorizontalDivider()
                schema.fields.forEachIndexed { index, field ->
                    Column {
                        Text(text = "${index + 1}. ${field.label} ${if (field.required) "*" else ""}", fontWeight = FontWeight.Bold)
                        field.helpText?.let {
                            Text(text = it, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        OutlinedTextField(
                            value = "",
                            onValueChange = {},
                            enabled = false,
                            placeholder = { Text(field.type) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) {
                Text("Cerrar Vista Previa")
            }
        }
    )
}

@Composable
private fun SchemaSubmissionsDialog(
    schema: FormSchema,
    submissions: List<SubmissionAdmin>,
    onDismiss: () -> Unit,
    onUpdateStatus: (String, String) -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.92f),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.background,
            tonalElevation = 4.dp
        ) {
            Column(Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.weight(1f)) {
                        Text("Respuestas recibidas", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        Text(schema.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                        Text("${submissions.size} registro(s)", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = "Cerrar") }
                }
                Spacer(Modifier.height(12.dp))
                if (submissions.isEmpty()) {
                    Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("Todavía no hay respuestas para este formulario.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(submissions, key = { it.id }) { submission ->
                            SubmissionReviewCard(
                                schema = schema,
                                submission = submission,
                                onUpdateStatus = onUpdateStatus
                            )
                        }
                    }
                }
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) { Text("Cerrar", fontWeight = FontWeight.Bold) }
            }
        }
    }
}

@Composable
private fun SubmissionReviewCard(
    schema: FormSchema?,
    submission: SubmissionAdmin,
    onUpdateStatus: (String, String) -> Unit,
    showActions: Boolean = true,
    maxAnswers: Int = Int.MAX_VALUE
) {
    val answers = FormAnswerPresentation.answers(schema, submission.data).take(maxAnswers)
    val statusColor = when (submission.status) {
        "approved", "published" -> MaterialTheme.colorScheme.primary
        "rejected" -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.tertiary
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f))
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = SancarlinaChipShape, color = statusColor.copy(alpha = 0.12f)) {
                    Text(
                        FormAnswerPresentation.statusLabel(submission.status),
                        color = statusColor,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                    )
                }
                Spacer(Modifier.weight(1f))
                submission.created_at?.let { timestamp ->
                    Text(
                        java.text.DateFormat.getDateTimeInstance(java.text.DateFormat.SHORT, java.text.DateFormat.SHORT)
                            .format(timestamp.toDate()),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
            if (answers.isEmpty()) {
                Text("Sin datos visibles.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                answers.forEachIndexed { index, answer ->
                    Text(answer.label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(answer.value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    if (index < answers.lastIndex) HorizontalDivider(Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
                }
            }
            if (showActions) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onUpdateStatus(submission.id, "approved") },
                        enabled = submission.status != "approved",
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) { Text("Aprobar") }
                    OutlinedButton(
                        onClick = { onUpdateStatus(submission.id, "rejected") },
                        enabled = submission.status != "rejected",
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) { Text("Rechazar") }
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
                shape = SancarlinaCardShape,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
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
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = template.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(text = template.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Button(
                        onClick = { viewModel.createNewSchemaFromTemplate(template) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
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
        Text("Editor de Esquema", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

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
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
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
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(uiState.submissions, key = { it.id }) { submission ->
                val schema = uiState.schemas.firstOrNull { it.id == submission.form_id }
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        submission.form_title.ifBlank { schema?.title ?: "Respuesta de formulario" },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    SubmissionReviewCard(
                        schema = schema,
                        submission = submission,
                        onUpdateStatus = viewModel::updateSubmissionStatus,
                        maxAnswers = 5
                    )
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
                val schema = uiState.schemas.firstOrNull { it.id == submission.form_id }
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(submission.form_title.ifBlank { "Solicitud de comercio" }, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    SubmissionReviewCard(
                        schema = schema,
                        submission = submission,
                        onUpdateStatus = viewModel::updateSubmissionStatus,
                        showActions = false,
                        maxAnswers = 6
                    )
                    SancarlinaPrimaryButton(
                        text = "Aprobar y publicar comercio",
                        onClick = { viewModel.publishSubmissionToTenant(submission) {} }
                    )
                }
            }
        }
    }
}

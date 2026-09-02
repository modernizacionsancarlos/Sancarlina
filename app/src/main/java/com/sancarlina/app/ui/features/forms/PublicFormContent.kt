package com.sancarlina.app.ui.features.forms

import androidx.compose.material3.MaterialTheme

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.sancarlina.app.data.models.SubmissionSyncStatus
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.sancarlina.app.data.models.FormField
import com.sancarlina.app.ui.components.SancarlinaElevatedCard
import com.sancarlina.app.ui.components.SancarlinaPrimaryButton
import com.sancarlina.app.ui.components.SancarlinaTextField
import com.sancarlina.app.ui.components.SancarlinaTopBar
import com.sancarlina.app.ui.theme.*
import com.sancarlina.app.utils.SanCarlosDistricts
import com.sancarlina.app.utils.captureBestLocation
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicFormContent(
    formId: String,
    submissionIdToEdit: String? = null,
    viewModel: PublicFormViewModel,
    onBack: () -> Unit,
    onViewPending: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(formId, submissionIdToEdit) {
        viewModel.loadSchema(formId, submissionIdToEdit)
    }

    Scaffold(
        topBar = {
            SancarlinaTopBar(title = "Registro de campo", onBack = onBack)
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
            return@Scaffold
        }

        val schema = uiState.formSchema
        if (schema == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(uiState.error ?: "No se pudo cargar el formulario.")
            }
            return@Scaffold
        }
        val registrationFields = visibleRegistrationFields(schema.fields)

        if (uiState.submissionId != null && uiState.submissionStatus != null) {
            SubmissionReviewContent(
                uiState = uiState,
                modifier = Modifier.padding(innerPadding),
                onEdit = viewModel::editCompletedSubmission,
                onNewSubmission = viewModel::resetForAnotherSubmission,
                onViewPending = onViewPending
            )
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                color = MaterialTheme.colorScheme.primary
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = Color.White.copy(alpha = 0.16f)
                    ) {
                        Text(
                            text = "GESTIÓN MUNICIPAL",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = schema.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    if (!schema.description.isNullOrBlank()) {
                        Text(
                            text = schema.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.86f),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "${registrationFields.count { it.type != "section" }} campos · Los marcados con * son obligatorios",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.82f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (!schema.description.isNullOrBlank()) {
                Text(
                    text = "Completá la información solicitada. Podés revisar todo antes de enviar.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            SancarlinaElevatedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                registrationFields.forEach { field ->
                    RenderFormField(
                        field = field,
                        value = uiState.fieldValues[field.id],
                        selectedImages = uiState.selectedImages[field.id] ?: emptyList(),
                        existingAttachmentCount = uiState.existingAttachments.count { it.fieldId == field.id },
                        uiState = uiState,
                        onValueChange = { newVal -> viewModel.updateFieldValue(field.id, newVal) },
                        onImagesChange = { uris -> viewModel.setSelectedImages(field.id, uris) },
                        onClearExistingAttachments = { viewModel.clearExistingAttachments(field.id) },
                        onGpsPinChange = { gpsStr -> viewModel.setUserGpsPin(gpsStr) }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (uiState.error != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = uiState.error ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (uiState.isSubmitting) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (uiState.isUploadingImages) "Guardando adjuntos…" else "Sincronizando respuesta…",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    SancarlinaPrimaryButton(
                        text = if (uiState.editingSubmissionId == null) "Guardar y enviar" else "Guardar cambios",
                        onClick = {
                            viewModel.submitForm()
                        }
                    )
                }
            }

            Text(
                text = "Tu información se utilizará únicamente para gestionar esta solicitud.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 18.dp)
            )
        }
    }
}

@Composable
private fun SubmissionReviewContent(
    uiState: PublicFormUiState,
    modifier: Modifier = Modifier,
    onEdit: () -> Unit,
    onNewSubmission: () -> Unit,
    onViewPending: () -> Unit
) {
    val schema = uiState.formSchema ?: return
    val status = uiState.submissionStatus ?: return
    val statusLabel = when (status) {
        SubmissionSyncStatus.SENT -> "Enviado correctamente"
        SubmissionSyncStatus.SENDING -> "Enviando"
        SubmissionSyncStatus.PENDING -> "Guardado en el dispositivo"
        SubmissionSyncStatus.ERROR -> "Guardado con error de sincronización"
    }
    val statusColor = when (status) {
        SubmissionSyncStatus.SENT -> MaterialTheme.colorScheme.primary
        SubmissionSyncStatus.ERROR -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.tertiary
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            color = statusColor.copy(alpha = 0.12f)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Icon(
                    imageVector = if (status == SubmissionSyncStatus.SENT) Icons.Default.CheckCircle else Icons.Default.CloudDone,
                    contentDescription = null,
                    tint = statusColor,
                    modifier = Modifier.size(34.dp)
                )
                Spacer(Modifier.height(10.dp))
                Text(statusLabel, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                Text(
                    if (status == SubmissionSyncStatus.SENT) {
                        "La respuesta ya llegó al sistema municipal."
                    } else {
                        "Se enviará automáticamente al recuperar conexión. También podés sincronizarla manualmente."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        Text("Vista previa", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(schema.title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)

        SancarlinaElevatedCard(modifier = Modifier.fillMaxWidth()) {
            val answers = FormAnswerPresentation.answers(schema, uiState.fieldValues)
            if (answers.isEmpty()) {
                Text("No hay respuestas de texto para mostrar.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                answers.forEachIndexed { index, answer ->
                    Text(answer.label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(answer.value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                    if (index < answers.lastIndex) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f))
                    }
                }
            }
            if (uiState.existingAttachments.isNotEmpty()) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f))
                Text("Adjuntos", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    "${uiState.existingAttachments.size} archivo(s) guardado(s)",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Button(
            onClick = onEdit,
            modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(Icons.Default.Edit, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Editar esta respuesta", fontWeight = FontWeight.Bold)
        }
        OutlinedButton(
            onClick = onNewSubmission,
            modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)
        ) {
            Icon(Icons.Default.AddCircle, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Registrar otro local", fontWeight = FontWeight.Bold)
        }
        TextButton(onClick = onViewPending, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.CloudSync, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Ver envíos y sincronizar")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
internal fun RenderFormField(
    field: FormField,
    value: Any?,
    selectedImages: List<Uri>,
    existingAttachmentCount: Int,
    uiState: PublicFormUiState,
    onValueChange: (Any?) -> Unit,
    onImagesChange: (List<Uri>) -> Unit,
    onClearExistingAttachments: () -> Unit,
    onGpsPinChange: (String?) -> Unit
) {
    val isDistrictField = field.tenantMapping == "area_id" ||
            field.label.contains("localidad", ignoreCase = true) ||
            field.label.contains("distrito", ignoreCase = true)

    when (field.type) {
        "section" -> {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            ) {
                Text(
                    text = field.label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
                )
            }
        }
        "text", "email", "phone" -> {
            val kType = when (field.type) {
                "email" -> KeyboardType.Email
                "phone" -> KeyboardType.Phone
                else -> KeyboardType.Text
            }
            SancarlinaTextField(
                value = (value as? String) ?: "",
                onValueChange = { onValueChange(it) },
                label = field.label + if (field.required) " *" else "",
                placeholder = field.placeholder ?: "",
                keyboardOptions = KeyboardOptions(keyboardType = kType)
            )
            if (!field.helpText.isNullOrBlank()) {
                Text(
                    text = field.helpText ?: "",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                )
            }
        }
        "textarea" -> {
            OutlinedTextField(
                value = (value as? String) ?: "",
                onValueChange = { onValueChange(it) },
                label = { Text(field.label + if (field.required) " *" else "") },
                placeholder = { Text(field.placeholder ?: "") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )
        }
        "boolean", "checkbox", "switch", "consent", "authorization" -> {
            val checked = when (value) {
                is Boolean -> value
                is Number -> value.toInt() != 0
                is String -> value.equals("true", ignoreCase = true) || value == "1"
                else -> false
            }
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onValueChange(!checked) },
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLow
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = checked,
                        onCheckedChange = onValueChange,
                        colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                    )
                    Column(modifier = Modifier.padding(start = 8.dp)) {
                        Text(
                            text = field.label + if (field.required) " *" else "",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        if (!field.helpText.isNullOrBlank()) {
                            Text(
                                text = field.helpText.orEmpty(),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
        "selector", "radio" -> {
            var expanded by remember { mutableStateOf(false) }
            val options = remember(field.options) {
                if (isDistrictField) {
                    SanCarlosDistricts.DISTRICT_CENTERS.keys.toList()
                } else {
                    field.options
                }
            }

            val currentSelected = (value as? String) ?: ""

            Column {
                Text(
                    text = field.label + if (field.required) " *" else " (opcional)",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )

                if (isDistrictField) {
                    Text(
                        text = "Si no sabes el distrito, puedes omitir este paso.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = if (currentSelected.isBlank()) "Omitir (no sé el distrito)" else currentSelected,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Omitir (no sé el distrito)") },
                            onClick = {
                                onValueChange(null)
                                expanded = false
                            }
                        )
                        options.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    onValueChange(option)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
        "gps" -> {
            val districtGeo = uiState.districtGeo
            val userPin = uiState.userSelectedGps
            val context = LocalContext.current
            val locationClient = remember(context) {
                LocationServices.getFusedLocationProviderClient(context)
            }
            var hasFinePermission by remember {
                mutableStateOf(
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                )
            }
            var hasCoarsePermission by remember {
                mutableStateOf(
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                )
            }
            var isLocating by remember { mutableStateOf(false) }
            var bestAccuracy by remember { mutableStateOf<Float?>(null) }
            var locationError by remember { mutableStateOf<String?>(null) }
            var cancelLocationCapture by remember { mutableStateOf<(() -> Unit)?>(null) }
            val hasLocationPermission = hasFinePermission || hasCoarsePermission

            fun startLocationCapture(permissionAvailable: Boolean = hasLocationPermission) {
                if (!permissionAvailable || isLocating) return
                cancelLocationCapture?.invoke()
                isLocating = true
                bestAccuracy = null
                locationError = null
                cancelLocationCapture = captureBestLocation(
                    client = locationClient,
                    onProgress = { reading ->
                        bestAccuracy = reading.accuracy.takeIf {
                            reading.hasAccuracy() && it.isFinite() && it >= 0f
                        }
                    },
                    onResult = { location ->
                        isLocating = false
                        cancelLocationCapture = null
                        onGpsPinChange("${location.latitude},${location.longitude}")
                    },
                    onError = { message ->
                        isLocating = false
                        cancelLocationCapture = null
                        locationError = message
                    }
                )
            }

            val permissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestMultiplePermissions()
            ) { result ->
                hasFinePermission = result[Manifest.permission.ACCESS_FINE_LOCATION] == true
                hasCoarsePermission = result[Manifest.permission.ACCESS_COARSE_LOCATION] == true
                val granted = hasFinePermission || hasCoarsePermission
                if (granted) {
                    startLocationCapture(permissionAvailable = true)
                } else {
                    locationError = "Permiso de ubicación denegado. Podés marcar el punto manualmente."
                }
            }

            DisposableEffect(Unit) {
                onDispose { cancelLocationCapture?.invoke() }
            }

            val mapCenter = remember(districtGeo, userPin) {
                if (userPin != null && userPin.contains(",")) {
                    val parts = userPin.split(",")
                    val lat = parts[0].toDoubleOrNull() ?: -33.77734
                    val lng = parts[1].toDoubleOrNull() ?: -69.07044
                    LatLng(lat, lng)
                } else if (districtGeo != null) {
                    LatLng(districtGeo.lat, districtGeo.lng)
                } else {
                    LatLng(-33.77734, -69.07044) // Centro Eugenio Bustos / San Carlos
                }
            }

            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(mapCenter, districtGeo?.zoom ?: 13f)
            }

            LaunchedEffect(districtGeo) {
                if (districtGeo != null && userPin == null) {
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(
                        LatLng(districtGeo.lat, districtGeo.lng),
                        districtGeo.zoom
                    )
                }
            }

            LaunchedEffect(userPin) {
                if (userPin != null && userPin.contains(",")) {
                    cameraPositionState.animate(
                        CameraUpdateFactory.newLatLngZoom(mapCenter, 17f)
                    )
                }
            }

            Column {
                Text(
                    text = field.label,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = field.helpText ?: "Tocá en el mapa para ubicar el marcador exacto de tu comercio.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                if (uiState.addressLocationMessage != null) {
                    Row(
                        modifier = Modifier.padding(bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (uiState.isGeocodingAddress) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = MaterialTheme.colorScheme.primary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = uiState.addressLocationMessage.orEmpty(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(18.dp))
                ) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
                        uiSettings = MapUiSettings(myLocationButtonEnabled = false),
                        onMapClick = { latLng ->
                            val pinStr = "${latLng.latitude},${latLng.longitude}"
                            onGpsPinChange(pinStr)
                        }
                    ) {
                        // Dibujar área semitransparente del distrito si está seleccionado
                        if (districtGeo != null) {
                            Circle(
                                center = LatLng(districtGeo.lat, districtGeo.lng),
                                radius = districtGeo.radiusM,
                                fillColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                strokeColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                strokeWidth = 2f
                            )
                        }

                        // Dibujar marcador manual del usuario si existe
                        if (userPin != null && userPin.contains(",")) {
                            val parts = userPin.split(",")
                            val lat = parts[0].toDoubleOrNull() ?: 0.0
                            val lng = parts[1].toDoubleOrNull() ?: 0.0
                            Marker(
                                state = rememberUpdatedMarkerState(position = LatLng(lat, lng)),
                                title = "Ubicación del Comercio"
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        if (hasLocationPermission) {
                            startLocationCapture()
                        } else {
                            permissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }
                    },
                    enabled = !isLocating,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    if (isLocating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(Icons.Default.MyLocation, contentDescription = null)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (isLocating) "Buscando mejor señal…"
                        else if (userPin == null) "Usar mi ubicación"
                        else "Actualizar con mi ubicación"
                    )
                }

                if (isLocating && bestAccuracy != null) {
                    val displayedAccuracy = bestAccuracy
                    Text(
                        text = "Mejor lectura actual: ±${displayedAccuracy?.roundToInt() ?: 0} m",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }

                if (!hasFinePermission && hasCoarsePermission) {
                    Text(
                        text = "La ubicación concedida es aproximada; podés corregir el punto tocando el mapa.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }

                if (locationError != null) {
                    Text(
                        text = locationError.orEmpty(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }

                if (userPin != null) {
                    Text(
                        text = "Coordenadas fijadas: $userPin",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
        "image", "file", "attachment" -> {
            val isImageField = field.type == "image"
            val pickerPolicy = attachmentPickerPolicy(field.maxImages)
            val maxImgs = pickerPolicy.selectionLimit
            val multiple = pickerPolicy.multiple

            val pickerLauncher = rememberLauncherForActivityResult(
                // El constructor por defecto usa el máximo seguro del selector
                // disponible en cada versión/dispositivo Android. El límite del
                // formulario se aplica nuevamente al recibir el resultado.
                contract = ActivityResultContracts.PickMultipleVisualMedia()
            ) { uris ->
                if (uris.isNotEmpty()) {
                    onImagesChange(uris.take(maxImgs))
                }
            }

            val singlePickerLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.PickVisualMedia()
            ) { uri ->
                if (uri != null) {
                    onImagesChange(listOf(uri))
                }
            }

            val filePickerLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.OpenMultipleDocuments()
            ) { uris ->
                if (uris.isNotEmpty()) {
                    onImagesChange(uris.take(maxImgs))
                }
            }

            val singleFilePickerLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.OpenDocument()
            ) { uri ->
                if (uri != null) {
                    onImagesChange(listOf(uri))
                }
            }

            Column {
                Text(
                    text = field.label + if (field.required) " *" else "",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                if (!field.helpText.isNullOrBlank()) {
                    Text(
                        text = field.helpText ?: "",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (existingAttachmentCount > 0 && selectedImages.isEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CloudDone, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Text(
                                "$existingAttachmentCount archivo(s) actual(es)",
                                modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            TextButton(onClick = onClearExistingAttachments) { Text("Quitar") }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (selectedImages.isNotEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(selectedImages) { uri ->
                            Box(
                                modifier = Modifier
                                    .size(90.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                            ) {
                                if (isImageField) {
                                    AsyncImage(
                                        model = uri,
                                        contentDescription = "Foto seleccionada",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.AttachFile,
                                        contentDescription = "Archivo seleccionado",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier
                                            .align(Alignment.Center)
                                            .size(40.dp)
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        onImagesChange(selectedImages - uri)
                                    },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(24.dp)
                                        .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Eliminar foto",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (selectedImages.size < maxImgs) {
                    OutlinedButton(
                        onClick = {
                            if (isImageField) {
                                if (multiple) {
                                    pickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                } else {
                                    singlePickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                }
                            } else if (multiple) {
                                filePickerLauncher.launch(arrayOf("*/*"))
                            } else {
                                singleFilePickerLauncher.launch(arrayOf("*/*"))
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(999.dp)
                    ) {
                        Icon(
                            imageVector = if (isImageField) Icons.Default.AddAPhoto else Icons.Default.AttachFile,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            if (isImageField) {
                                if (existingAttachmentCount > 0 && selectedImages.isEmpty()) "Reemplazar foto(s)" else if (selectedImages.isEmpty()) "Seleccionar foto (0/$maxImgs)" else "Agregar fotos (${selectedImages.size}/$maxImgs)"
                            } else {
                                if (existingAttachmentCount > 0 && selectedImages.isEmpty()) "Reemplazar archivo(s)" else if (selectedImages.isEmpty()) "Seleccionar archivo (0/$maxImgs)" else "Agregar archivos (${selectedImages.size}/$maxImgs)"
                            }
                        )
                    }
                }
            }
        }
    }
}

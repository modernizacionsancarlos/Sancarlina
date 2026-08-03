package com.sancarlina.app.ui.features.forms

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
import coil.compose.AsyncImage
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicFormContent(
    formId: String,
    viewModel: PublicFormViewModel,
    onBack: () -> Unit,
    onSuccess: (String) -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(formId) {
        viewModel.loadSchema(formId)
    }

    Scaffold(
        topBar = {
            SancarlinaTopBar(title = "Formulario público", onBack = onBack)
        },
        containerColor = SancarlinaBackground
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = SancarlinaPrimary)
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
                color = SancarlinaPrimary
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
                        text = "${schema.fields.count { it.type != "section" }} campos · Los marcados con * son obligatorios",
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
                    color = SancarlinaOnSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            SancarlinaElevatedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                schema.fields.forEach { field ->
                    RenderFormField(
                        field = field,
                        value = uiState.fieldValues[field.id],
                        selectedImages = uiState.selectedImages[field.id] ?: emptyList(),
                        uiState = uiState,
                        onValueChange = { newVal -> viewModel.updateFieldValue(field.id, newVal) },
                        onImagesChange = { uris -> viewModel.setSelectedImages(field.id, uris) },
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
                            CircularProgressIndicator(color = SancarlinaPrimary)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (uiState.isUploadingImages) "Subiendo imágenes a Storage..." else "Enviando respuesta...",
                                style = MaterialTheme.typography.labelSmall,
                                color = SancarlinaOnSurfaceVariant
                            )
                        }
                    }
                } else {
                    SancarlinaPrimaryButton(
                        text = "Enviar formulario",
                        onClick = {
                            viewModel.submitForm(context, onSuccess)
                        }
                    )
                }
            }

            Text(
                text = "Tu información se utilizará únicamente para gestionar esta solicitud.",
                style = MaterialTheme.typography.labelSmall,
                color = SancarlinaOnSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 18.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RenderFormField(
    field: FormField,
    value: Any?,
    selectedImages: List<Uri>,
    uiState: PublicFormUiState,
    onValueChange: (Any?) -> Unit,
    onImagesChange: (List<Uri>) -> Unit,
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
                color = SancarlinaPrimary.copy(alpha = 0.1f)
            ) {
                Text(
                    text = field.label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = SancarlinaPrimary,
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
                    color = SancarlinaOnSurfaceVariant,
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
                    focusedContainerColor = SancarlinaSurfaceContainerLow,
                    unfocusedContainerColor = SancarlinaSurfaceContainerLow,
                    focusedBorderColor = SancarlinaPrimary,
                    unfocusedBorderColor = SancarlinaOutlineVariant
                )
            )
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
                        color = SancarlinaOnSurfaceVariant,
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
                            focusedContainerColor = SancarlinaSurfaceContainerLow,
                            unfocusedContainerColor = SancarlinaSurfaceContainerLow,
                            focusedBorderColor = SancarlinaPrimary,
                            unfocusedBorderColor = SancarlinaOutlineVariant
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

            Column {
                Text(
                    text = field.label,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = field.helpText ?: "Tocá en el mapa para ubicar el marcador exacto de tu comercio.",
                    style = MaterialTheme.typography.labelSmall,
                    color = SancarlinaOnSurfaceVariant,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .border(1.dp, SancarlinaOutlineVariant, RoundedCornerShape(18.dp))
                ) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
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
                                fillColor = SancarlinaPrimary.copy(alpha = 0.15f),
                                strokeColor = SancarlinaPrimary.copy(alpha = 0.6f),
                                strokeWidth = 2f
                            )
                        }

                        // Dibujar marcador manual del usuario si existe
                        if (userPin != null && userPin.contains(",")) {
                            val parts = userPin.split(",")
                            val lat = parts[0].toDoubleOrNull() ?: 0.0
                            val lng = parts[1].toDoubleOrNull() ?: 0.0
                            Marker(
                                state = MarkerState(position = LatLng(lat, lng)),
                                title = "Ubicación del Comercio"
                            )
                        }
                    }
                }

                if (userPin != null) {
                    Text(
                        text = "Coordenadas fijadas: $userPin",
                        style = MaterialTheme.typography.labelSmall,
                        color = SancarlinaPrimary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
        "image" -> {
            val maxImgs = field.maxImages
            val multiple = field.allowMultiple || maxImgs > 1

            val pickerLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = maxImgs)
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
                        color = SancarlinaOnSurfaceVariant,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

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
                                    .border(1.dp, SancarlinaOutlineVariant, RoundedCornerShape(8.dp))
                            ) {
                                AsyncImage(
                                    model = uri,
                                    contentDescription = "Foto seleccionada",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
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
                            if (multiple) {
                                pickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            } else {
                                singlePickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(999.dp)
                    ) {
                        Icon(imageVector = Icons.Default.AddAPhoto, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            if (selectedImages.isEmpty()) "Seleccionar Foto (${selectedImages.size}/$maxImgs)"
                            else "Agregar más fotos (${selectedImages.size}/$maxImgs)"
                        )
                    }
                }
            }
        }
    }
}

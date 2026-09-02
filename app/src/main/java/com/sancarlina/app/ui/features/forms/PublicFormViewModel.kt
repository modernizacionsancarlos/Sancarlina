package com.sancarlina.app.ui.features.forms

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sancarlina.app.data.models.FormSchema
import com.sancarlina.app.data.models.OfflineAttachment
import com.sancarlina.app.data.models.SubmissionSyncStatus
import com.sancarlina.app.data.repository.FormsRepository
import com.sancarlina.app.data.repository.OfflineSubmissionsRepository
import com.sancarlina.app.data.templates.BuiltinFormTemplates
import com.sancarlina.app.utils.AddressGeocoder
import com.sancarlina.app.utils.DistrictGeo
import com.sancarlina.app.utils.SanCarlosDistricts
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PublicFormUiState(
    val isLoading: Boolean = true,
    val formSchema: FormSchema? = null,
    val fieldValues: Map<String, Any?> = emptyMap(),
    val selectedImages: Map<String, List<Uri>> = emptyMap(),
    val existingAttachments: List<OfflineAttachment> = emptyList(),
    val clearedAttachmentFields: Set<String> = emptySet(),
    val districtGeo: DistrictGeo? = null,
    val userSelectedGps: String? = null,
    val isGeocodingAddress: Boolean = false,
    val addressLocationMessage: String? = null,
    val isSubmitting: Boolean = false,
    val isUploadingImages: Boolean = false,
    val submissionId: String? = null,
    val submissionStatus: SubmissionSyncStatus? = null,
    val editingSubmissionId: String? = null,
    val error: String? = null
)

class PublicFormViewModel(
    private val formsRepository: FormsRepository,
    private val offlineSubmissionsRepository: OfflineSubmissionsRepository,
    private val addressGeocoder: AddressGeocoder
) : ViewModel() {

    private val _uiState = MutableStateFlow(PublicFormUiState())
    val uiState: StateFlow<PublicFormUiState> = _uiState.asStateFlow()
    private var addressGeocodeJob: Job? = null

    fun loadSchema(formId: String, submissionId: String? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            // Buscar plantilla builtin si coincide el ID
            val builtin = BuiltinFormTemplates.ALL_TEMPLATES.find { it.id == formId }
            if (builtin != null) {
                setLoadedSchema(builtin.schema)
                loadExistingSubmission(submissionId, builtin.schema)
                return@launch
            }

            // De lo contrario cargar de Firestore por ID
            try {
                val found = formsRepository.getFormById(formId)
                if (found != null) {
                    setLoadedSchema(found)
                    loadExistingSubmission(submissionId, found)
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Formulario no encontrado o no disponible."
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.localizedMessage ?: "Error al cargar el formulario."
                    )
                }
            }
        }
    }

    private fun setLoadedSchema(schema: FormSchema) {
        addressGeocodeJob?.cancel()
        _uiState.value = PublicFormUiState(
            isLoading = false,
            formSchema = schema,
            fieldValues = valuesWithImplicitAccountAuthorization(schema, emptyMap())
        )
    }

    private fun loadExistingSubmission(submissionId: String?, schema: FormSchema) {
        if (submissionId.isNullOrBlank()) return
        val submission = offlineSubmissionsRepository.getSubmission(submissionId)
        if (submission == null || submission.formId != schema.id) {
            _uiState.update { it.copy(error = "No se encontró el envío guardado para editar.") }
            return
        }
        val restoredValues = valuesWithImplicitAccountAuthorization(schema, submission.data)
        _uiState.update {
            it.copy(
                fieldValues = restoredValues,
                selectedImages = emptyMap(),
                existingAttachments = offlineSubmissionsRepository.getAttachments(submissionId),
                clearedAttachmentFields = emptySet(),
                editingSubmissionId = submissionId,
                submissionId = null,
                submissionStatus = null,
                districtGeo = SanCarlosDistricts.resolveDistrictGeo(
                    SanCarlosDistricts.findDistrictText(schema.fields, restoredValues)
                ),
                userSelectedGps = schema.fields.firstOrNull { field -> field.type == "gps" }
                    ?.let { field -> restoredValues[field.id]?.toString() }
            )
        }
    }

    fun updateFieldValue(fieldId: String, value: Any?) {
        _uiState.update { currentState ->
            val updatedValues = currentState.fieldValues.toMutableMap()
            if (value == null || (value is String && value.isBlank())) {
                updatedValues.remove(fieldId)
            } else {
                updatedValues[fieldId] = value
            }

            // Verificar si el campo modificado corresponde a Distrito/Localidad
            val schema = currentState.formSchema
            var newDistrictGeo = currentState.districtGeo

            if (schema != null) {
                val districtText = SanCarlosDistricts.findDistrictText(schema.fields, updatedValues)
                newDistrictGeo = SanCarlosDistricts.resolveDistrictGeo(districtText)
            }

            currentState.copy(
                fieldValues = updatedValues,
                districtGeo = newDistrictGeo
            )
        }
        scheduleAddressGeocodingIfNeeded(fieldId)
    }

    private fun scheduleAddressGeocodingIfNeeded(changedFieldId: String) {
        val state = uiState.value
        val schema = state.formSchema ?: return
        val changedField = schema.fields.firstOrNull { it.id == changedFieldId } ?: return
        val isDistrictField = changedField.tenantMapping == "area_id" ||
            changedField.label.contains("localidad", ignoreCase = true) ||
            changedField.label.contains("distrito", ignoreCase = true)
        if (!changedField.isAddressField() && !isDistrictField) return

        val addressField = schema.fields.firstOrNull { it.isAddressField() } ?: return
        val address = state.fieldValues[addressField.id]?.toString()?.trim().orEmpty()
        addressGeocodeJob?.cancel()
        if (address.length < MIN_ADDRESS_LENGTH) {
            _uiState.update {
                it.copy(isGeocodingAddress = false, addressLocationMessage = null)
            }
            return
        }

        val district = SanCarlosDistricts.findDistrictText(schema.fields, state.fieldValues)
        val query = buildMunicipalAddressQuery(address, district)
        addressGeocodeJob = viewModelScope.launch {
            delay(ADDRESS_GEOCODE_DEBOUNCE_MS)
            _uiState.update {
                it.copy(isGeocodingAddress = true, addressLocationMessage = "Buscando la dirección…")
            }
            val result = runCatching { addressGeocoder.find(query) }.getOrNull()
            if (result == null) {
                _uiState.update {
                    it.copy(
                        isGeocodingAddress = false,
                        addressLocationMessage = "No pudimos ubicar esa dirección todavía. Podés seguir escribiendo o usar tu ubicación."
                    )
                }
                return@launch
            }

            setGpsValue("${result.latitude},${result.longitude}")
            _uiState.update {
                it.copy(
                    isGeocodingAddress = false,
                    addressLocationMessage = result.displayName
                        ?.let { name -> "Mapa actualizado: $name" }
                        ?: "Mapa actualizado desde la dirección."
                )
            }
        }
    }

    fun setSelectedImages(fieldId: String, uris: List<Uri>) {
        _uiState.update { currentState ->
            val updatedImages = currentState.selectedImages.toMutableMap()
            if (uris.isEmpty()) {
                updatedImages.remove(fieldId)
            } else {
                updatedImages[fieldId] = uris
            }
            currentState.copy(
                selectedImages = updatedImages,
                clearedAttachmentFields = currentState.clearedAttachmentFields - fieldId
            )
        }
    }

    fun clearExistingAttachments(fieldId: String) {
        _uiState.update { state ->
            state.copy(
                existingAttachments = state.existingAttachments.filterNot { it.fieldId == fieldId },
                selectedImages = state.selectedImages - fieldId,
                clearedAttachmentFields = state.clearedAttachmentFields + fieldId
            )
        }
    }

    fun setUserGpsPin(gpsString: String?) {
        addressGeocodeJob?.cancel()
        setGpsValue(gpsString)
        _uiState.update {
            it.copy(
                isGeocodingAddress = false,
                addressLocationMessage = if (gpsString == null) null else "Ubicación ajustada manualmente."
            )
        }
    }

    private fun setGpsValue(gpsString: String?) {
        _uiState.update { current ->
            val gpsField = current.formSchema?.fields?.find { it.type == "gps" }
            val updatedValues = current.fieldValues.toMutableMap()
            if (gpsField != null) {
                if (gpsString.isNullOrBlank()) updatedValues.remove(gpsField.id)
                else updatedValues[gpsField.id] = gpsString
            }
            current.copy(userSelectedGps = gpsString, fieldValues = updatedValues)
        }
    }

    fun submitForm() {
        val schema = uiState.value.formSchema ?: return
        val values = valuesWithImplicitAccountAuthorization(schema, uiState.value.fieldValues)
        val attachments = uiState.value.selectedImages

        if (values !== uiState.value.fieldValues) {
            _uiState.update { it.copy(fieldValues = values) }
        }

        for (field in schema.fields) {
            if (field.type == "section" || field.isImplicitAccountAuthorization()) continue
            val valForField = values[field.id]
            val filesForField = attachments[field.id]
            val hasExistingFiles = uiState.value.existingAttachments.any { it.fieldId == field.id }

            if (field.required && valForField == null && filesForField.isNullOrEmpty() && !hasExistingFiles) {
                _uiState.update {
                    it.copy(error = "El campo '${field.label}' es obligatorio.")
                }
                return
            }
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isSubmitting = true,
                    isUploadingImages = attachments.isNotEmpty(),
                    submissionId = null,
                    submissionStatus = null,
                    error = null
                )
            }

            try {
                val editingId = uiState.value.editingSubmissionId
                val result = if (editingId == null) {
                    offlineSubmissionsRepository.enqueueAndTrySync(
                        schema = schema,
                        values = values,
                        attachmentUris = attachments
                    )
                } else {
                    offlineSubmissionsRepository.updateAndTrySync(
                        localId = editingId,
                        schema = schema,
                        values = values,
                        replacementAttachmentUris = attachments,
                        clearedAttachmentFields = uiState.value.clearedAttachmentFields
                    )
                }
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        isUploadingImages = false,
                        submissionId = result.localId,
                        submissionStatus = result.status,
                        editingSubmissionId = null,
                        selectedImages = emptyMap(),
                        clearedAttachmentFields = emptySet(),
                        existingAttachments = offlineSubmissionsRepository.getAttachments(result.localId)
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        isUploadingImages = false,
                        error = e.localizedMessage ?: "Error al procesar el envío."
                    )
                }
            }
        }
    }

    fun editCompletedSubmission() {
        val completedId = uiState.value.submissionId ?: return
        _uiState.update {
            it.copy(
                editingSubmissionId = completedId,
                submissionId = null,
                submissionStatus = null,
                error = null
            )
        }
    }

    fun resetForAnotherSubmission() {
        addressGeocodeJob?.cancel()
        _uiState.update {
            val defaults = it.formSchema
                ?.let { schema -> valuesWithImplicitAccountAuthorization(schema, emptyMap()) }
                .orEmpty()
            it.copy(
                fieldValues = defaults,
                selectedImages = emptyMap(),
                existingAttachments = emptyList(),
                clearedAttachmentFields = emptySet(),
                districtGeo = null,
                userSelectedGps = null,
                isGeocodingAddress = false,
                addressLocationMessage = null,
                isSubmitting = false,
                isUploadingImages = false,
                submissionId = null,
                submissionStatus = null,
                editingSubmissionId = null,
                error = null
            )
        }
    }

    override fun onCleared() {
        addressGeocodeJob?.cancel()
        super.onCleared()
    }

    private companion object {
        const val MIN_ADDRESS_LENGTH = 5
        const val ADDRESS_GEOCODE_DEBOUNCE_MS = 650L
    }
}

package com.sancarlina.app.ui.features.forms

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sancarlina.app.data.models.FormSchema
import com.sancarlina.app.data.remote.FormImageUploader
import com.sancarlina.app.data.repository.FormsRepository
import com.sancarlina.app.data.repository.SubmissionsRepository
import com.sancarlina.app.data.templates.BuiltinFormTemplates
import com.sancarlina.app.utils.DistrictGeo
import com.sancarlina.app.utils.SanCarlosDistricts
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
    val districtGeo: DistrictGeo? = null,
    val userSelectedGps: String? = null,
    val isSubmitting: Boolean = false,
    val isUploadingImages: Boolean = false,
    val submissionId: String? = null,
    val error: String? = null
)

class PublicFormViewModel(
    private val formsRepository: FormsRepository,
    private val submissionsRepository: SubmissionsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PublicFormUiState())
    val uiState: StateFlow<PublicFormUiState> = _uiState.asStateFlow()

    fun loadSchema(formId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            // Buscar plantilla builtin si coincide el ID
            val builtin = BuiltinFormTemplates.ALL_TEMPLATES.find { it.id == formId }
            if (builtin != null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        formSchema = builtin.schema
                    )
                }
                return@launch
            }

            // De lo contrario cargar de Firestore por ID
            try {
                val forms = formsRepository.getFormsByTenant(formId)
                val found = forms.firstOrNull { it.id == formId }
                if (found != null) {
                    _uiState.update { it.copy(isLoading = false, formSchema = found) }
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
    }

    fun setSelectedImages(fieldId: String, uris: List<Uri>) {
        _uiState.update { currentState ->
            val updatedImages = currentState.selectedImages.toMutableMap()
            if (uris.isEmpty()) {
                updatedImages.remove(fieldId)
            } else {
                updatedImages[fieldId] = uris
            }
            currentState.copy(selectedImages = updatedImages)
        }
    }

    fun setUserGpsPin(gpsString: String?) {
        _uiState.update { it.copy(userSelectedGps = gpsString) }
        if (gpsString != null) {
            val schema = uiState.value.formSchema
            val gpsField = schema?.fields?.find { it.type == "gps" }
            if (gpsField != null) {
                updateFieldValue(gpsField.id, gpsString)
            }
        }
    }

    fun submitForm(context: Context, onSuccess: (String) -> Unit) {
        val schema = uiState.value.formSchema ?: return
        val values = uiState.value.fieldValues.toMutableMap()
        val imagesMap = uiState.value.selectedImages

        // Validar campos requeridos
        for (field in schema.fields) {
            if (field.type == "section") continue
            val valForField = values[field.id]
            val imagesForField = imagesMap[field.id]

            if (field.required && valForField == null && (imagesForField == null || imagesForField.isEmpty())) {
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
                    isUploadingImages = imagesMap.isNotEmpty(),
                    error = null
                )
            }

            try {
                // Subir fotos a Firebase Storage si existen campos de tipo image
                for (field in schema.fields.filter { it.type == "image" }) {
                    val uris = imagesMap[field.id]
                    if (!uris.isNullOrEmpty()) {
                        val uploadResult = FormImageUploader.uploadImages(
                            context = context,
                            formId = schema.id,
                            fieldId = field.id,
                            imageUris = uris,
                            maxImages = field.maxImages
                        )
                        values[field.id] = uploadResult
                    }
                }

                _uiState.update { it.copy(isUploadingImages = false) }

                // Enviar respuesta a Firestore / Cloud Functions
                val cleanValues = values.filterValues { it != null }.mapValues { it.value!! }
                val submissionResult = submissionsRepository.submitForm(schema.id, cleanValues)
                if (submissionResult.isSuccess) {
                    val subId = submissionResult.getOrThrow()
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            submissionId = subId
                        )
                    }
                    onSuccess(subId)
                } else {
                    val err = submissionResult.exceptionOrNull()?.localizedMessage
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            error = err ?: "No se pudo enviar la respuesta del formulario."
                        )
                    }
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
}

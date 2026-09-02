package com.sancarlina.app.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sancarlina.app.data.models.FormSchema
import com.sancarlina.app.data.models.FormTemplate
import com.sancarlina.app.data.repository.AdminFormulariosRepository
import com.sancarlina.app.data.repository.SubmissionAdmin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminFormulariosUiState(
    val isLoading: Boolean = false,
    val selectedTab: Int = 0, // 0: Dashboard, 1: Plantillas, 2: Editor, 3: Respuestas, 4: Aceptaciones
    val schemas: List<FormSchema> = emptyList(),
    val templates: List<FormTemplate> = emptyList(),
    val submissions: List<SubmissionAdmin> = emptyList(),
    val activeSchemaForEditor: FormSchema? = null,
    val selectedSubmission: SubmissionAdmin? = null,
    val isGridView: Boolean = false,
    val sortBy: String = "name", // "name" | "date"
    val sortDirection: String = "asc", // "asc" | "desc"
    val error: String? = null,
    val successMessage: String? = null
)

class AdminFormulariosViewModel(
    private val repository: AdminFormulariosRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminFormulariosUiState())
    val uiState: StateFlow<AdminFormulariosUiState> = _uiState.asStateFlow()

    init {
        refreshAllData()
        viewModelScope.launch {
            repository.observeAllSubmissions().collect { submissions ->
                _uiState.update { it.copy(submissions = submissions, isLoading = false) }
            }
        }
    }

    fun setTab(tabIndex: Int) {
        _uiState.update { it.copy(selectedTab = tabIndex) }
        if (tabIndex == 0) loadSchemas()
        if (tabIndex == 1) loadTemplates()
        if (tabIndex == 3 || tabIndex == 4) loadSubmissions()
    }

    fun toggleViewMode() {
        _uiState.update { it.copy(isGridView = !it.isGridView) }
    }

    fun setSortBy(sortBy: String) {
        _uiState.update { it.copy(sortBy = sortBy) }
    }

    fun toggleSortDirection() {
        _uiState.update { it.copy(sortDirection = if (it.sortDirection == "asc") "desc" else "asc") }
    }

    fun refreshAllData() {
        loadSchemas()
        loadTemplates()
        loadSubmissions()
    }

    fun loadSchemas() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val res = repository.getAllSchemas()
            if (res.isSuccess) {
                _uiState.update { it.copy(isLoading = false, schemas = res.getOrDefault(emptyList())) }
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Error al cargar esquemas de formularios.") }
            }
        }
    }

    fun loadTemplates() {
        viewModelScope.launch {
            val res = repository.getAllTemplates()
            if (res.isSuccess) {
                _uiState.update { it.copy(templates = res.getOrDefault(emptyList())) }
            }
        }
    }

    fun loadSubmissions() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val res = repository.getAllSubmissions()
            if (res.isSuccess) {
                _uiState.update { it.copy(isLoading = false, submissions = res.getOrDefault(emptyList())) }
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Error al cargar respuestas.") }
            }
        }
    }

    fun saveSchema(schema: FormSchema, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val res = repository.saveSchema(schema)
            if (res.isSuccess) {
                _uiState.update { it.copy(isLoading = false, successMessage = "Esquema guardado exitosamente.") }
                loadSchemas()
                onSuccess()
            } else {
                _uiState.update { it.copy(isLoading = false, error = res.exceptionOrNull()?.localizedMessage) }
            }
        }
    }

    fun togglePublic(schemaId: String, currentPublic: Boolean) {
        viewModelScope.launch {
            repository.togglePublic(schemaId, !currentPublic)
            loadSchemas()
        }
    }

    fun toggleAcceptsResponses(schemaId: String, currentAccepts: Boolean) {
        viewModelScope.launch {
            repository.toggleAcceptsResponses(schemaId, !currentAccepts)
            loadSchemas()
        }
    }

    fun deleteSchema(schemaId: String) {
        viewModelScope.launch {
            repository.deleteSchema(schemaId)
            loadSchemas()
        }
    }

    fun selectSchemaForEditor(schema: FormSchema) {
        _uiState.update {
            it.copy(
                activeSchemaForEditor = schema,
                selectedTab = 2 // Tab Editor
            )
        }
    }

    fun createNewSchemaFromTemplate(template: FormTemplate) {
        val newSchema = template.schema.copy(
            id = "",
            title = "${template.name} - Copia",
            status = "draft",
            isPublic = true,
            acceptsResponses = true
        )
        selectSchemaForEditor(newSchema)
    }

    fun updateSubmissionStatus(submissionId: String, status: String) {
        viewModelScope.launch {
            repository.updateSubmissionStatus(submissionId, status)
            loadSubmissions()
        }
    }

    fun publishSubmissionToTenant(submission: SubmissionAdmin, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val schema = uiState.value.schemas.find { it.id == submission.form_id }
            val res = repository.publishSubmissionToTenant(submission, schema)
            if (res.isSuccess) {
                _uiState.update { it.copy(isLoading = false, successMessage = "¡Comercio publicado exitosamente!") }
                loadSubmissions()
                onSuccess()
            } else {
                _uiState.update { it.copy(isLoading = false, error = res.exceptionOrNull()?.localizedMessage ?: "Error al publicar comercio.") }
            }
        }
    }
}

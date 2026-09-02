package com.sancarlina.app.ui.features.forms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.sancarlina.app.data.models.FormSchema
import com.sancarlina.app.data.models.OfflineSubmission
import com.sancarlina.app.data.repository.FormsRepository
import com.sancarlina.app.data.repository.OfflineSubmissionsRepository
import com.sancarlina.app.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class FieldRegistrationUiState(
    val isLoading: Boolean = true,
    val forms: List<FormSchema> = emptyList(),
    val selectedFormId: String? = null,
    val submissions: List<OfflineSubmission> = emptyList(),
    val role: String = "citizen",
    val accessDenied: Boolean = false,
    val isSyncing: Boolean = false,
    val message: String? = null,
    val error: String? = null
)

class FieldRegistrationViewModel(
    private val formsRepository: FormsRepository,
    private val submissionsRepository: OfflineSubmissionsRepository,
    private val userRepository: UserRepository,
    private val auth: FirebaseAuth
) : ViewModel() {
    private val _uiState = MutableStateFlow(FieldRegistrationUiState())
    val uiState: StateFlow<FieldRegistrationUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            submissionsRepository.submissions.collect { submissions ->
                val uid = auth.currentUser?.uid
                _uiState.update { state ->
                    state.copy(submissions = submissions.filter { it.userId == uid })
                }
            }
        }
        load()
    }

    fun load(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, message = null) }
            val user = auth.currentUser
            if (user == null) {
                _uiState.update {
                    it.copy(isLoading = false, accessDenied = true, error = "Iniciá sesión con tu usuario de registrador.")
                }
                return@launch
            }

            val profileAccess = userRepository.getRegistrationAccess(user.uid, forceRefresh)
            val claimRole = runCatching {
                user.getIdToken(forceRefresh).await().claims["role"]?.toString()
            }.getOrNull()
            val role = FieldRegistrationAccessPolicy.resolveRole(claimRole, profileAccess.role)
            if (!FieldRegistrationAccessPolicy.isFieldStaffRole(role)) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        role = role,
                        accessDenied = true,
                        error = "Tu usuario no tiene permisos de registrador. Solicitalos al administrador."
                    )
                }
                return@launch
            }

            val forms = formsRepository.getAllAvailableForms(forceRefresh)
                .filter { schema ->
                    FieldRegistrationAccessPolicy.canComplete(
                        schema = schema,
                        userId = user.uid,
                        role = role,
                        profileAssignedFormIds = profileAccess.assignedFormIds
                    )
                }
                .sortedBy { it.title.lowercase() }

            _uiState.update { state ->
                state.copy(
                    isLoading = false,
                    forms = forms,
                    selectedFormId = state.selectedFormId?.takeIf { selected -> forms.any { it.id == selected } }
                        ?: forms.firstOrNull()?.id,
                    role = role,
                    accessDenied = false,
                    error = if (forms.isEmpty()) "No tenés formularios habilitados para completar." else null
                )
            }
        }
    }

    fun selectForm(formId: String) {
        _uiState.update { it.copy(selectedFormId = formId) }
    }

    fun syncNow() {
        if (_uiState.value.isSyncing) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true, message = null) }
            if (!submissionsRepository.hasValidatedConnection()) {
                submissionsRepository.scheduleSync()
                _uiState.update {
                    it.copy(isSyncing = false, message = "Sin conexión. La sincronización automática quedó programada.")
                }
                return@launch
            }
            val result = submissionsRepository.syncPending()
            _uiState.update {
                it.copy(
                    isSyncing = false,
                    message = when {
                        result.sent > 0 && result.failed == 0 -> "Se enviaron ${result.sent} respuesta(s)."
                        result.sent > 0 -> "Se enviaron ${result.sent}; ${result.failed} requieren revisión."
                        result.failed > 0 -> "No se pudieron enviar ${result.failed} respuesta(s)."
                        else -> "No hay respuestas pendientes."
                    }
                )
            }
        }
    }

}

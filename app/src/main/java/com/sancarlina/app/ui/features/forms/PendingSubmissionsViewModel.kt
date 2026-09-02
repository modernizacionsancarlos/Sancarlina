package com.sancarlina.app.ui.features.forms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sancarlina.app.data.models.OfflineSubmission
import com.sancarlina.app.data.models.SubmissionSyncStatus
import com.sancarlina.app.data.repository.OfflineSubmissionsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PendingSubmissionsUiState(
    val submissions: List<OfflineSubmission> = emptyList(),
    val isSyncing: Boolean = false,
    val message: String? = null
) {
    val pendingCount: Int
        get() = submissions.count { it.status != SubmissionSyncStatus.SENT }
}

class PendingSubmissionsViewModel(
    private val repository: OfflineSubmissionsRepository
) : ViewModel() {
    private val isSyncing = MutableStateFlow(false)
    private val message = MutableStateFlow<String?>(null)

    val uiState: StateFlow<PendingSubmissionsUiState> = combine(
        repository.submissions,
        isSyncing,
        message
    ) { submissions, syncing, currentMessage ->
        PendingSubmissionsUiState(submissions, syncing, currentMessage)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PendingSubmissionsUiState(repository.submissions.value)
    )

    fun syncNow() {
        if (isSyncing.value) return
        if (!repository.hasValidatedConnection()) {
            message.value = "No hay una conexión a internet validada. Los envíos siguen guardados."
            repository.scheduleSync()
            return
        }
        viewModelScope.launch {
            isSyncing.value = true
            message.value = null
            val summary = repository.syncPending()
            message.value = when {
                summary.failed > 0 && summary.sent > 0 ->
                    "Se enviaron ${summary.sent}; ${summary.failed} requieren otro intento."
                summary.failed > 0 ->
                    "No se pudieron sincronizar ${summary.failed} formularios. Revisá el detalle."
                summary.sent > 0 ->
                    "Se sincronizaron ${summary.sent} formularios."
                else -> "No hay formularios pendientes para sincronizar."
            }
            isSyncing.value = false
        }
    }

    fun retry(localId: String) {
        repository.retry(localId)
        syncNow()
    }

    fun clearMessage() {
        message.value = null
    }
}

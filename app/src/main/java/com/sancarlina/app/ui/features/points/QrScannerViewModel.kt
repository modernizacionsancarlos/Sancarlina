package com.sancarlina.app.ui.features.points

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sancarlina.app.data.repository.PointsRepository
import com.sancarlina.app.data.repository.TenantsRepository
import com.sancarlina.app.utils.Logger
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal data class PointsQrPayload(
    val tenantId: String,
    val points: Int
)

internal fun parsePointsQr(rawValue: String): PointsQrPayload? {
    val parts = rawValue.split(":", limit = 4)
    if (parts.size != 4 || parts[0] != "sancarlina" || parts[1] != "points") return null

    val tenantId = parts[2].trim()
    val points = parts[3].trim().toIntOrNull()
    return if (tenantId.isNotEmpty() && points != null && points > 0) {
        PointsQrPayload(tenantId = tenantId, points = points)
    } else {
        null
    }
}

data class QrScannerUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val successPoints: Int? = null
)

class QrScannerViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val tenantsRepository: TenantsRepository = TenantsRepository(),
    private val pointsRepository: PointsRepository = PointsRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(QrScannerUiState())
    val uiState: StateFlow<QrScannerUiState> = _uiState.asStateFlow()

    fun processQrCode(qrData: String) {
        if (_uiState.value.isLoading || _uiState.value.successPoints != null || _uiState.value.error != null) return

        if (auth.currentUser == null) {
            _uiState.update { it.copy(error = "Iniciá sesión antes de escanear puntos.") }
            return
        }
        
        _uiState.update { it.copy(isLoading = true, error = null) }
        
        val payload = parsePointsQr(qrData)
        if (payload == null) {
            _uiState.update { it.copy(isLoading = false, error = "El código no contiene datos de puntos válidos.") }
            return
        }

        val tenantId = payload.tenantId
        val amount = payload.points
        
        viewModelScope.launch {
            try {
                val tenantName = tenantsRepository.getTenantById(tenantId)?.name
                    ?.takeIf { it.isNotBlank() }
                    ?: "Comercio adherido"

                val result = pointsRepository.awardPoints(
                    points = amount,
                    reason = "Escaneo de código QR",
                    tenantId = tenantId,
                    tenantName = tenantName
                )

                if (result.isSuccess) {
                    _uiState.update { it.copy(isLoading = false, successPoints = amount) }
                } else {
                    Logger.e("QR points function failed", result.exceptionOrNull())
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "No se pudieron acreditar los puntos. Revisá tu conexión o consultá al comercio."
                        )
                    }
                }
            } catch (e: Exception) {
                Logger.e("QR Processing failed", e)
                _uiState.update { it.copy(isLoading = false, error = "Error inesperado al procesar el código") }
            }
        }
    }

    fun resetState() {
        _uiState.update { QrScannerUiState() }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null, isLoading = false) }
    }

    fun showCameraError() {
        _uiState.update {
            it.copy(
                isLoading = false,
                error = "No se pudo iniciar la cámara. Verificá el permiso e intentá nuevamente."
            )
        }
    }
}

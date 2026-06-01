package com.sancarlina.app.ui.features.points

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sancarlina.app.data.repository.PointsRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class QrScannerUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val successPoints: Int? = null
)

class QrScannerViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val pointsRepository = PointsRepository()
    
    private val _uiState = MutableStateFlow(QrScannerUiState())
    val uiState: StateFlow<QrScannerUiState> = _uiState.asStateFlow()

    fun processQrCode(qrData: String, onComplete: () -> Unit) {
        if (_uiState.value.isLoading || _uiState.value.successPoints != null) return
        
        _uiState.update { it.copy(isLoading = true, error = null) }
        
        // Logic: QR data should be a commerce ID or a specific transaction ID
        // For simplicity, let's assume QR data is "sancarlina:points:COM_ID:AMOUNT"
        if (!qrData.startsWith("sancarlina:points:")) {
            _uiState.update { it.copy(isLoading = false, error = "Código QR no válido para Góndola Sancarlina") }
            return
        }

        val parts = qrData.split(":")
        if (parts.size < 4) {
            _uiState.update { it.copy(isLoading = false, error = "Formato de código inválido") }
            return
        }

        val amount = parts[3].toIntOrNull() ?: 0
        val tenantId = parts[2]
        
        viewModelScope.launch {
            // Fetch tenant name first if needed, or just use a generic name if not in QR
            // For now, let's try to get it from Firestore since we have tenantId
            var tenantName = "Comercio Asociado"
            try {
                val tenantDoc = firestore.collection("tenants").document(tenantId).get().await()
                tenantName = tenantDoc.getString("name") ?: tenantName
            } catch (e: Exception) {
                // fallback to default name
            }

            val result = pointsRepository.awardPoints(
                points = amount,
                reason = "Escaneo de código QR",
                tenantId = tenantId,
                tenantName = tenantName
            )

            if (result.isSuccess) {
                _uiState.update { it.copy(isLoading = false, successPoints = amount) }
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Error al procesar puntos: ${result.exceptionOrNull()?.message}") }
            }
        }
    }

    fun resetState() {
        _uiState.update { QrScannerUiState() }
    }
}

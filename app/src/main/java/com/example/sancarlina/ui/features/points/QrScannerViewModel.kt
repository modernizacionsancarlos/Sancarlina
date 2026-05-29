package com.example.sancarlina.ui.features.points

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class QrScannerUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val successPoints: Int? = null
)

class QrScannerViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    
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
        val userId = auth.currentUser?.uid ?: return

        // 1. Update User Points in Firestore
        firestore.collection("users").document(userId)
            .update("pointsBalance", FieldValue.increment(amount.toLong()))
            .addOnSuccessListener {
                // 2. Log History
                val history = mapOf(
                    "type" to "EARNED",
                    "amount" to amount,
                    "title" to "Escaneo en Comercio",
                    "timestamp" to FieldValue.serverTimestamp(),
                    "userId" to userId
                )
                firestore.collection("points_history").add(history)
                
                _uiState.update { it.copy(isLoading = false, successPoints = amount) }
            }
            .addOnFailureListener {
                _uiState.update { it.copy(isLoading = false, error = "Error al procesar los puntos") }
            }
    }

    fun resetState() {
        _uiState.update { QrScannerUiState() }
    }
}

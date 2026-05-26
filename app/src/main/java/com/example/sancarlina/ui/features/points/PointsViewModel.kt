package com.example.sancarlina.ui.features.points

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PointsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PointsUiState())
    val uiState: StateFlow<PointsUiState> = _uiState.asStateFlow()

    private var timer: CountDownTimer? = null

    init {
        loadData()
    }

    private fun loadData() {
        _uiState.update { 
            it.copy(
                balance = 1250,
                benefits = listOf(
                    BenefitItem("1", "15% de descuento", "Bodega La Celia", 500, "BODEGA", "https://lh3.googleusercontent.com/aida-public/AB6AXuClK2UhtvJz8vKvWzElP6vhFOp4PVJlOojFvJBw4S1skZDmOdRwHJM1Nz9UF1I4qt97efjm2S_AIN8DxICNVZMRzGLDfj07hRQCIi-1UeXNuIhcTItqVcu29NvHIPf9Vc3HoG0riPFabQ2V8DXEu3_tdzr7O-fSWM3Tuqn8lpx_2SKuLOBnig02hXtK6oKaE3IbNgqKuFdAhTa5Bm6VqPKDM61qjAhJzzN1W-UkQKbJgSC1pTqouJE_IhGUHGpKM-YM4ozgiQDsibQ"),
                    BenefitItem("2", "Visita Guiada Estándar", "Finca El Retiro", 800, "EXPERIENCIA", "https://lh3.googleusercontent.com/aida-public/AB6AXuDw9EFruk8dtYQ3VOmMeX74BhP2e3DCbNJ2uQ2g4j2pqOv86Wl5qSie69ieAiZ--o0DQbu-aAm50puCwqXYNAb7Gv5YmlhZpO7hW-L-YqXlisMGIOaWRTEBY08t9jkI-L4dA6_3Hgo9JG2CQOHB-CPrV1voY2XADOsx6J936J44G3oRWAqCuuhLjriYjYswkEf5pWZiU_SMZFSanPOQNvYLyohZ5QcI-UO582RoDgOsn4eb2JtQIo-QLEilNaIWEyNht_INTw8D5Po"),
                    BenefitItem("3", "Botella Malbec Reserva", "Línea Exclusiva", 1200, "PRODUCTO", "https://lh3.googleusercontent.com/aida-public/AB6AXuDqTshp0Azn9yUBkZUs4tTljdslDWtWWUifMa3raDENN8A2jncVCf6UU-uqA4PvtSpOKqSIX35jR3BklCLiVdGeXSQIUxxKW7rmdbAc_RSiOOcUsSqfICaseHu2n8SQrNJFmT0_PuTPt9ECpyWdB5AXOr7X0gVPVctKuGtLmTzrYxmzFMaZI-W6NiAyKAYpD0AXJpgcyNQ7CqqWMtPz3bEsj0MKd_8Iy7A_o1kx6FFa5iIZyalr2dJH3Frl0T63bzLxBalb5jv1cfo")
                )
            )
        }
    }

    fun startQrGeneration() {
        _uiState.update { it.copy(qrCodeActive = true) }
        startTimer()
    }

    private fun startTimer() {
        timer?.cancel()
        timer = object : CountDownTimer(600000, 1000) { // 10 minutes
            override fun onTick(millisUntilFinished: Long) {
                val minutes = (millisUntilFinished / 1000) / 60
                val seconds = (millisUntilFinished / 1000) % 60
                val time = String.format("%02d:%02d", minutes, seconds)
                _uiState.update { it.copy(qrTimeRemaining = time) }
            }

            override fun onFinish() {
                _uiState.update { it.copy(qrCodeActive = false, qrTimeRemaining = "00:00") }
            }
        }.start()
    }

    fun simulateSuccessfulScan(pointsToDeduct: Int) {
        timer?.cancel()
        _uiState.update { 
            it.copy(
                balance = it.balance - pointsToDeduct,
                qrCodeActive = false,
                showSuccessModal = true
            )
        }
    }

    fun onBenefitClick(benefit: BenefitItem) {
        _uiState.update { it.copy(selectedBenefit = benefit) }
    }

    fun redeemBenefit() {
        _uiState.value.selectedBenefit?.let { benefit ->
            if (_uiState.value.balance >= benefit.cost) {
                _uiState.update { 
                    it.copy(
                        balance = it.balance - benefit.cost,
                        selectedBenefit = null,
                        showSuccessModal = true
                    )
                }
            }
        }
    }

    fun dismissModal() {
        _uiState.update { it.copy(showSuccessModal = false, selectedBenefit = null, qrCodeActive = false) }
    }

    override fun onCleared() {
        super.onCleared()
        timer?.cancel()
    }
}

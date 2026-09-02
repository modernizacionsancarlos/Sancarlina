package com.sancarlina.app.viewmodel

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sancarlina.app.data.repository.BenefitsRepository
import com.sancarlina.app.data.repository.UserRepository
import com.sancarlina.app.utils.Logger
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import java.util.Locale

class PointsViewModel(
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository,
    private val benefitsRepository: BenefitsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PointsUiState())
    val uiState: StateFlow<PointsUiState> = _uiState.asStateFlow()

    private var timer: CountDownTimer? = null

    init {
        loadData()
    }

    private fun loadData() {
        _uiState.update { it.copy(isLoading = true) }
        
        viewModelScope.launch {
            val uid = auth.currentUser?.uid
            val balanceFlow = uid?.let(userRepository::observeUserBalance) ?: flowOf(0)
            combine(balanceFlow, benefitsRepository.observeActiveBenefits()) { balance, remoteBenefits ->
                balance to remoteBenefits.map { b ->
                    BenefitItem(
                        id = b.id,
                        title = b.title,
                        brand = b.industry,
                        cost = if (b.points_cost > 0) b.points_cost else b.cost,
                        category = b.industry,
                        imageUrl = b.cover_url
                    )
                }
            }.collect { (balance, benefits) ->
                _uiState.update { 
                    it.copy(
                        balance = balance,
                        benefits = benefits,
                        isLoading = false
                    )
                }
            }
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
                val time = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
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
        _uiState.update { it.copy(selectedBenefit = benefit, error = null) }
    }

    fun redeemBenefit() {
        val uid = auth.currentUser?.uid ?: return
        val benefit = _uiState.value.selectedBenefit ?: return
        
        if (_uiState.value.balance < benefit.cost) {
            _uiState.update { it.copy(error = "No tenés suficientes puntos para canjear este beneficio") }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                val newBalance = _uiState.value.balance - benefit.cost
                userRepository.updateUserBalance(uid, newBalance)
                userRepository.addPointMovement(uid, "Canje: ${benefit.title}", -benefit.cost, false)
                _uiState.update { 
                    it.copy(
                        balance = newBalance,
                        selectedBenefit = null,
                        showSuccessModal = true,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                Logger.e("Error redeeming benefit", e)
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "No se pudo realizar el canje. Comprobá tu conexión e intentá de nuevo."
                    )
                }
            }
        }
    }

    fun cancelBenefitSelection() {
        _uiState.update { it.copy(selectedBenefit = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun dismissModal() {
        _uiState.update { it.copy(showSuccessModal = false, selectedBenefit = null, qrCodeActive = false, error = null) }
    }

    override fun onCleared() {
        super.onCleared()
        timer?.cancel()
    }
}

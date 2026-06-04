package com.sancarlina.app.viewmodel

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sancarlina.app.data.repository.BenefitsRepository
import com.sancarlina.app.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
            val balance = uid?.let { userRepository.getUserBalance(it) } ?: 0
            val benefits = benefitsRepository.getActiveBenefits().map { b ->
                BenefitItem(
                    id = b.id,
                    title = b.title,
                    brand = b.industry,
                    cost = if (b.points_cost > 0) b.points_cost else b.cost,
                    category = b.industry,
                    imageUrl = b.cover_url
                )
            }
            
            _uiState.update { 
                it.copy(
                    balance = balance,
                    benefits = benefits,
                    isLoading = false
                )
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

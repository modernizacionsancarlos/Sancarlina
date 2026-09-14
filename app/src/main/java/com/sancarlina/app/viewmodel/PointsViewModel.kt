package com.sancarlina.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sancarlina.app.data.repository.BenefitsRepository
import com.sancarlina.app.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class PointsViewModel(
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository,
    private val benefitsRepository: BenefitsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PointsUiState())
    val uiState: StateFlow<PointsUiState> = _uiState.asStateFlow()

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
                        imageUrl = b.cover_url,
                        description = b.description
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

    fun onBenefitClick(benefit: BenefitItem) {
        _uiState.update { it.copy(selectedBenefit = benefit) }
    }

    fun closeBenefitDetails() {
        _uiState.update { it.copy(selectedBenefit = null) }
    }

}

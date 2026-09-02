package com.sancarlina.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sancarlina.app.data.repository.ReviewsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.sancarlina.app.utils.ReviewValidator

data class RateCommerceUiState(
    val isSubmitting: Boolean = false,
    val submitted: Boolean = false,
    val error: String? = null
)

class RateCommerceViewModel(
    private val reviewsRepository: ReviewsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(RateCommerceUiState())
    val uiState: StateFlow<RateCommerceUiState> = _uiState.asStateFlow()

    fun submit(tenantId: String, rating: Int, comment: String) {
        if (_uiState.value.isSubmitting) return
        ReviewValidator.error(tenantId, rating, comment)?.let { validationError ->
            _uiState.update { it.copy(error = validationError) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, error = null) }
            runCatching { reviewsRepository.submitReview(tenantId, rating, comment) }
                .onSuccess {
                    _uiState.value = RateCommerceUiState(submitted = true)
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            error = error.message ?: "No pudimos enviar la reseña."
                        )
                    }
                }
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }
}

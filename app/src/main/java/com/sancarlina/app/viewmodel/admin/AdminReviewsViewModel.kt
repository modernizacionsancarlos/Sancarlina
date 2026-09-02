package com.sancarlina.app.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sancarlina.app.data.repository.AdminReview
import com.sancarlina.app.data.repository.AdminReviewsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminReviewsUiState(
    val reviews: List<AdminReview> = emptyList(),
    val isLoading: Boolean = true,
    val processingId: String? = null,
    val error: String? = null
)

class AdminReviewsViewModel(
    private val repository: AdminReviewsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AdminReviewsUiState())
    val uiState: StateFlow<AdminReviewsUiState> = _uiState.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching { repository.getReviews() }
                .onSuccess { reviews -> _uiState.value = AdminReviewsUiState(reviews = reviews, isLoading = false) }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message ?: "No se pudieron cargar las reseñas.") }
                }
        }
    }

    fun moderate(reviewId: String, status: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(processingId = reviewId, error = null) }
            runCatching { repository.moderate(reviewId, status) }
                .onSuccess { load() }
                .onFailure { error ->
                    _uiState.update { it.copy(processingId = null, error = error.message ?: "No se pudo moderar la reseña.") }
                }
        }
    }
}

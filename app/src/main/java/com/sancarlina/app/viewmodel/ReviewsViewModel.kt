package com.sancarlina.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.sancarlina.app.data.models.Tenant
import com.sancarlina.app.data.repository.ReviewRecord
import com.sancarlina.app.data.repository.ReviewsRepository
import com.sancarlina.app.data.repository.TenantsRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

data class UserReview(
    val id: String,
    val userName: String,
    val rating: Int,
    val timeAgo: String,
    val text: String,
    val userInitials: String = "",
    val userAvatarUrl: String = "",
    val verifiedVisit: Boolean = false
)

data class ReviewsUiState(
    val tenant: Tenant? = null,
    val reviews: List<UserReview> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class ReviewsViewModel(
    private val tenantsRepository: TenantsRepository,
    private val reviewsRepository: ReviewsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ReviewsUiState())
    val uiState: StateFlow<ReviewsUiState> = _uiState.asStateFlow()
    private var reviewsJob: Job? = null

    fun loadReviews(commerceId: String) {
        if (commerceId.isBlank()) {
            _uiState.update { it.copy(error = "ID de comercio no válido") }
            return
        }
        reviewsJob?.cancel()
        _uiState.update { it.copy(isLoading = true, error = null) }
        reviewsJob = viewModelScope.launch {
            val tenant = tenantsRepository.getTenantById(commerceId)
            _uiState.update { it.copy(tenant = tenant) }
            reviewsRepository.observeApprovedReviews(commerceId)
                .catch { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "No pudimos cargar las reseñas."
                        )
                    }
                }
                .collect { records ->
                    _uiState.update {
                        it.copy(
                            reviews = records.map(::toUserReview),
                            isLoading = false,
                            error = null
                        )
                    }
                }
        }
    }

    private fun toUserReview(record: ReviewRecord): UserReview {
        val initials = record.userName.split(" ")
            .filter(String::isNotBlank)
            .take(2)
            .joinToString("") { it.first().uppercaseChar().toString() }
        return UserReview(
            id = record.id,
            userName = record.userName,
            rating = record.rating,
            timeAgo = relativeTime(record.createdAt),
            text = record.comment,
            userInitials = initials,
            userAvatarUrl = record.userAvatarUrl,
            verifiedVisit = record.verifiedVisit
        )
    }

    private fun relativeTime(timestamp: Timestamp?): String {
        if (timestamp == null) return "Recientemente"
        val elapsed = (System.currentTimeMillis() - timestamp.toDate().time).coerceAtLeast(0L)
        val days = TimeUnit.MILLISECONDS.toDays(elapsed)
        return when {
            days == 0L -> "Hoy"
            days == 1L -> "Ayer"
            days < 30L -> "Hace $days días"
            days < 365L -> "Hace ${days / 30} meses"
            else -> "Hace ${days / 365} años"
        }
    }
}

package com.sancarlina.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sancarlina.app.BuildConfig
import com.sancarlina.app.data.models.Tenant
import com.sancarlina.app.data.repository.TenantsRepository
import com.sancarlina.app.utils.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UserReview(
    val id: String,
    val userName: String,
    val rating: Int,
    val timeAgo: String,
    val text: String,
    val userInitials: String = "",
    val userAvatarUrl: String = ""
)

data class ReviewsUiState(
    val tenant: Tenant? = null,
    val reviews: List<UserReview> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class ReviewsViewModel(
    private val tenantsRepository: TenantsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewsUiState())
    val uiState: StateFlow<ReviewsUiState> = _uiState.asStateFlow()

    fun loadReviews(commerceId: String) {
        if (commerceId.isBlank()) {
            _uiState.update { it.copy(error = "ID de comercio no válido") }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                // 1. Fetch Tenant details to show header info
                val tenants = tenantsRepository.getActiveTenants()
                val tenant = tenants.find { it.id == commerceId }

                // 2. Load reviews (mock for debug, empty list in release)
                val reviews = if (BuildConfig.DEBUG) {
                    getMockReviews()
                } else {
                    emptyList()
                }

                _uiState.update {
                    it.copy(
                        tenant = tenant,
                        reviews = reviews,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                Logger.e("Error loading reviews", e)
                _uiState.update { it.copy(isLoading = false, error = "Error al cargar las reseñas") }
            }
        }
    }

    private fun getMockReviews(): List<UserReview> {
        return listOf(
            UserReview(
                id = "r1",
                userName = "Carlos M.",
                rating = 5,
                timeAgo = "Hace 2 días",
                text = "Excelente experiencia. La visita a la bodega fue muy informativa y los vinos degustados superaron mis expectativas. El entorno es inmejorable, definitivamente volveremos en la próxima cosecha.",
                userInitials = "CM",
                userAvatarUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDYuo41r3zkpZ7uwbcoDXdjMcC7N1hZCVvT7ChpR-IgJwQpTyZhinHPz0PCklrP2_aQPz-73JeqziaqvBpJcA-Uh3kC6oq9J6TQf9zlmI4PFBZK3-iEiceivOqsH-kifYXYOcXDt_zVqJQS9h24pJksM2lsZN72eOx9T61ZXmXMJN1Vkt-WetRONxDBXX730fFVsSt8HmZ43u8OllfLi63-nRrtXKemwMkGEXkfunHH0MbzQOJufB3E-Ihva1QXq_mGrArbEn3inNOa"
            ),
            UserReview(
                id = "r2",
                userName = "María R.",
                rating = 4,
                timeAgo = "Hace 1 semana",
                text = "Muy buen servicio y atención. Los viñedos están preciosos en esta época del año. Un poco de espera al principio, pero valió la pena.",
                userInitials = "MR",
                userAvatarUrl = ""
            )
        )
    }
}

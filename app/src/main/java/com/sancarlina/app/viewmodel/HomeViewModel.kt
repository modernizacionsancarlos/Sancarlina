package com.sancarlina.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sancarlina.app.data.cache.AppCache
import com.sancarlina.app.data.models.displayImageUrl
import com.sancarlina.app.data.repository.TenantsRepository
import com.sancarlina.app.data.repository.DiscoveryPreferencesRepository
import com.sancarlina.app.utils.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class HomeViewModel(
    private val tenantsRepository: TenantsRepository,
    private val discoveryPreferencesRepository: DiscoveryPreferencesRepository
) : ViewModel() {
    // Si los datos ya fueron precargados por AppPreloader en el splash, no mostrar spinner
    private val _uiState = MutableStateFlow(HomeUiState(isLoading = AppCache.getTenants().isNullOrEmpty()))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        if (AppCache.getTenants().isNullOrEmpty()) {
            _uiState.update { it.copy(isLoading = true) }
        }

        viewModelScope.launch {
            combine(
                tenantsRepository.observeActiveTenants(),
                discoveryPreferencesRepository.interests
            ) { tenants, interests -> tenants to interests }
                .collect { (rawTenants, interests) ->
                try {
                    val tenants = rawTenants.sortedByDescending { tenant ->
                        if (interests.any { interest -> tenant.matchesInterest(interest) }) 1 else 0
                    }
                    val categories = tenants.asSequence()
                        .map { it.industry.trim() }
                        .filter { it.isNotBlank() }
                        .distinctBy { it.lowercase() }
                        .sortedBy { it.lowercase() }
                        .map { CategoryItem(name = it) }
                        .toList()

                    val highlighted = tenants
                        .filter { it.displayImageUrl().isNotBlank() }
                        .take(5)
                        .map { tenant ->
                            BannerItem(
                                id = tenant.id,
                                title = tenant.industry.ifBlank { "Conocé San Carlos" },
                                subtitle = tenant.name,
                                imageUrl = tenant.displayImageUrl(),
                                content = tenant.description,
                                tag = tenant.industry
                            )
                        }

                    val featured = tenants.firstOrNull()?.let { tenant ->
                        ProductItem(
                            id = tenant.id,
                            name = tenant.name,
                            brand = tenant.industry,
                            phone = tenant.contactPhone,
                            imageUrl = tenant.displayImageUrl()
                        )
                    }

                    _uiState.update {
                        it.copy(
                            tenants = tenants,
                            banners = highlighted,
                            categories = categories,
                            nearbyProduct = featured,
                            isLoading = false
                        )
                    }
                } catch (e: Exception) {
                    Logger.e("Error mapping home data", e)
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    private fun com.sancarlina.app.data.models.Tenant.matchesInterest(interest: String): Boolean {
        val content = listOf(name, industry, description, services.joinToString(" "))
            .joinToString(" ")
            .lowercase()
        val aliases = when (interest) {
            "vino" -> listOf("vino", "bodega", "vitivin")
            "gastronomía" -> listOf("gastronom", "restaurante", "comida", "café")
            "naturaleza" -> listOf("naturaleza", "montaña", "paisaje", "rural")
            "aventura" -> listOf("aventura", "trekking", "rafting", "cabalgata")
            "cultura" -> listOf("cultura", "museo", "historia", "artesanía")
            "familia" -> listOf("familia", "niños", "familiar")
            else -> listOf(interest)
        }
        return aliases.any(content::contains)
    }
}

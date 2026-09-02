package com.sancarlina.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sancarlina.app.data.repository.AreasRepository
import com.sancarlina.app.data.repository.TenantsRepository
import com.sancarlina.app.data.models.displayImageUrl
import com.sancarlina.app.utils.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.catch

class TurismoViewModel(
    private val tenantsRepository: TenantsRepository,
    private val areasRepository: AreasRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TurismoUiState(isLoading = true))
    val uiState: StateFlow<TurismoUiState> = _uiState.asStateFlow()

    init {
        loadTurismoData()
    }

    /** Carga puntos turísticos reales directamente desde la base de datos remota de Firestore. */
    private fun loadTurismoData() {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            combine(
                tenantsRepository.observeActiveTenants(),
                areasRepository.getAreasFlow()
            ) { tenants, areas ->
                val areaNames = areas.associate { it.id to it.name }
                val points = tenants.map { it.toTurismoPoint(areaNames[it.areaId].orEmpty()) }
                val banners = tenants.filter { it.displayImageUrl().isNotBlank() }.take(1).map { tenant ->
                    BannerItem(
                        id = tenant.id,
                        title = tenant.industry,
                        subtitle = tenant.name,
                        imageUrl = tenant.displayImageUrl(),
                        content = tenant.description
                    )
                }
                points to banners
            }.catch { exception ->
                Logger.e("Error loading tourism data", exception)
                _uiState.update {
                    it.copy(points = emptyList(), banners = emptyList(), categories = listOf("Todos"), isLoading = false)
                }
            }.collect { (points, banners) ->
                val categories = listOf("Todos") + points.asSequence()
                    .map { it.category.trim() }
                    .filter { it.isNotBlank() }
                    .distinctBy { it.lowercase() }
                    .sortedBy { it.lowercase() }
                    .toList()
                _uiState.update {
                    it.copy(points = points, banners = banners, categories = categories, isLoading = false)
                }
            }
        }
    }

    fun onCategorySelected(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }
}

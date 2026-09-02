package com.sancarlina.app.ui.features.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sancarlina.app.data.models.displayImageUrl
import com.sancarlina.app.data.repository.AreasRepository
import com.sancarlina.app.data.repository.TenantsRepository
import com.sancarlina.app.viewmodel.CommerceMarker
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.text.Normalizer

class CategoryListViewModel(
    private val tenantsRepository: TenantsRepository,
    private val areasRepository: AreasRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoryListUiState())
    val uiState: StateFlow<CategoryListUiState> = _uiState.asStateFlow()
    private var loadJob: kotlinx.coroutines.Job? = null

    fun loadCategory(categoryId: String) {
        if (categoryId.isBlank()) return

        _uiState.update { it.copy(isLoading = true, categoryName = categoryId.uppercase(), hasLoadError = false) }

        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            combine(
                tenantsRepository.observeActiveTenants(),
                areasRepository.getAreasFlow()
            ) { tenants, areas ->
                val areaNames = areas.associate { it.id to it.name }
                tenants.filter { tenant ->
                    categoryId.equals("Todos", ignoreCase = true) ||
                        categoriesMatch(tenant.industry, categoryId)
                }.mapNotNull { tenant ->
                    val lat = tenant.latitude ?: return@mapNotNull null
                    val lng = tenant.longitude ?: return@mapNotNull null
                    CommerceMarker(
                        id = tenant.id,
                        name = tenant.name,
                        locationName = areaNames[tenant.areaId] ?: tenant.address.ifBlank { "San Carlos" },
                        position = LatLng(lat, lng),
                        category = tenant.industry,
                        phone = tenant.contactPhone,
                        imageUrl = tenant.displayImageUrl(),
                        rating = tenant.rating.toFloat(),
                        distance = "GondolApp"
                    )
                }
            }.collect { commerceList ->
                if (commerceList.isEmpty()) {
                    showEmptyCategory()
                } else {
                    val locations = listOf("Todas") + commerceList.map { it.locationName }.distinct()
                    _uiState.update {
                        it.copy(
                            commerces = commerceList,
                            filteredCommerces = commerceList,
                            locations = locations,
                            isLoading = false,
                            hasLoadError = false
                        )
                    }
                }
            }
        }
    }

    private fun categoriesMatch(industry: String, requested: String): Boolean {
        fun normalized(value: String): String = Normalizer.normalize(value, Normalizer.Form.NFD)
            .replace("\\p{M}+".toRegex(), "")
            .lowercase()
            .replace("[^a-z0-9]".toRegex(), "")
            .removeSuffix("s")
        val left = normalized(industry)
        val right = normalized(requested)
        return left == right || left.contains(right) || right.contains(left)
    }

    private fun showEmptyCategory() {
        _uiState.update {
            it.copy(
                commerces = emptyList(),
                filteredCommerces = emptyList(),
                locations = listOf("Todas"),
                isLoading = false,
                hasLoadError = false
            )
        }
    }

    fun onLocationSelected(location: String) {
        _uiState.update { state ->
            val filtered = if (location == "Todas") {
                state.commerces
            } else {
                state.commerces.filter { it.locationName == location }
            }
            state.copy(selectedLocation = location, filteredCommerces = filtered)
        }
    }
}

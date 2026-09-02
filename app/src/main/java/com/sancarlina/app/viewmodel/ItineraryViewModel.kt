package com.sancarlina.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sancarlina.app.data.repository.ItineraryRepository
import com.sancarlina.app.data.repository.TenantsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.sancarlina.app.utils.ItineraryMath

data class ItineraryUiState(
    val points: List<TurismoPoint> = emptyList(),
    val selectedIds: Set<String> = emptySet(),
    val totalDistanceKm: Double = 0.0,
    val estimatedMinutes: Int = 0,
    val isLoading: Boolean = true,
    val error: String? = null
)

class ItineraryViewModel(
    private val repository: ItineraryRepository,
    private val tenantsRepository: TenantsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ItineraryUiState())
    val uiState: StateFlow<ItineraryUiState> = _uiState.asStateFlow()
    private var allPoints: Map<String, TurismoPoint> = emptyMap()
    private var ids: List<String> = emptyList()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching {
                val tenants = tenantsRepository.getActiveTenants()
                allPoints = tenants.associate { it.id to it.toTurismoPoint() }
                ids = repository.loadIds().filter(allPoints::containsKey)
                publish()
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, error = error.message) }
            }
        }
    }

    fun toggle(pointId: String) {
        viewModelScope.launch {
            ids = if (pointId in ids) ids - pointId else (ids + pointId).take(9)
            repository.saveIds(ids)
            publish()
        }
    }

    fun move(pointId: String, direction: Int) {
        val index = ids.indexOf(pointId)
        val target = index + direction
        if (index < 0 || target !in ids.indices) return
        viewModelScope.launch {
            val mutable = ids.toMutableList()
            val item = mutable.removeAt(index)
            mutable.add(target, item)
            ids = mutable
            repository.saveIds(ids)
            publish()
        }
    }

    fun clear() {
        viewModelScope.launch {
            ids = emptyList()
            repository.saveIds(ids)
            publish()
        }
    }

    private fun publish() {
        val points = ids.mapNotNull(allPoints::get)
        val distance = points.zipWithNext().sumOf { (a, b) ->
            ItineraryMath.haversineKm(a.latitude, a.longitude, b.latitude, b.longitude)
        }
        _uiState.value = ItineraryUiState(
            points = points,
            selectedIds = ids.toSet(),
            totalDistanceKm = distance,
            estimatedMinutes = ItineraryMath.estimatedMinutes(distance, points.size),
            isLoading = false
        )
    }

}

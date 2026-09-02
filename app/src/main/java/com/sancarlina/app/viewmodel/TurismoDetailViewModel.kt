package com.sancarlina.app.viewmodel
 
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sancarlina.app.data.repository.AreasRepository
import com.sancarlina.app.data.repository.TenantsRepository
import com.sancarlina.app.utils.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TurismoDetailViewModel(
    private val tenantsRepository: TenantsRepository,
    private val areasRepository: AreasRepository
) : ViewModel() {

    private val _point = MutableStateFlow<TurismoPoint?>(null)
    val point: StateFlow<TurismoPoint?> = _point.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadPointDetails(pointId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val tenant = tenantsRepository.getTenantById(pointId)
                val areaName = tenant?.areaId?.takeIf { it.isNotBlank() }?.let { areaId ->
                    areasRepository.getAreas().firstOrNull { it.id == areaId }?.name
                }.orEmpty()
                _point.value = tenant?.toTurismoPoint(areaName)
            } catch (e: Exception) {
                Logger.e("Error loading turismo point details from Firestore", e)
                _point.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }
}

package com.sancarlina.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sancarlina.app.data.repository.AreasRepository
import com.sancarlina.app.data.repository.TenantsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplashViewModel(
    private val tenantsRepository: TenantsRepository,
    private val areasRepository: AreasRepository
) : ViewModel() {

    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady.asStateFlow()

    init {
        prepareData()
    }

    private fun prepareData() {
        viewModelScope.launch {
            try {
                // Pre-fetch critical data
                tenantsRepository.getActiveTenants()
                areasRepository.getAreas()
                _isReady.value = true
            } catch (e: Exception) {
                // If it fails, we still allow entry (or we could show error)
                _isReady.value = true
            }
        }
    }
}

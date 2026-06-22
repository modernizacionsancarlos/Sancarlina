package com.sancarlina.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sancarlina.app.data.repository.AreasRepository
import com.sancarlina.app.data.repository.TenantsRepository
import com.sancarlina.app.utils.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

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
                // Pre-fetch critical data with a timeout to avoid hanging the splash screen (max 5s)
                withTimeoutOrNull(5000) {
                    tenantsRepository.getActiveTenants()
                    areasRepository.getAreas()
                }
            } catch (e: Exception) {
                Logger.e("Splash data pre-fetch failed", e)
            } finally {
                // Always set to ready to allow entry even if fetch failed or timed out
                _isReady.value = true
            }
        }
    }
}

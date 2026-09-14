package com.sancarlina.app.viewmodel

import androidx.lifecycle.ViewModel
import com.sancarlina.app.data.repository.AreasRepository
import com.sancarlina.app.data.repository.TenantsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

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
        // Inicia la carga cache-first en segundo plano. La navegación no espera a la red:
        // Home observa los mismos flujos y recibe la caché o la actualización cuando llegue.
        tenantsRepository.observeActiveTenants()
        areasRepository.getAreasFlow()
        _isReady.value = true
    }
}

package com.sancarlina.app.ui.features.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sancarlina.app.data.repository.AreasRepository
import com.sancarlina.app.data.repository.TenantsRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplashViewModel(
    private val tenantsRepository: TenantsRepository,
    private val areasRepository: AreasRepository
) : ViewModel() {

    private val _isReady = MutableStateFlow(false)
    val isReady = _isReady.asStateFlow()

    init {
        preloadData()
    }

    private fun preloadData() {
        viewModelScope.launch {
            // Iniciamos la carga de datos esenciales en paralelo mientras se muestra el splash
            val tenantsDeferred = async { tenantsRepository.getActiveTenants() }
            val areasDeferred = async { areasRepository.getAreas() }

            // Forzamos un tiempo mínimo de splash para branding si la carga es muy rápida
            val timerDeferred = async { delay(2000) }

            // Esperamos a que todo termine
            tenantsDeferred.await()
            areasDeferred.await()
            timerDeferred.await()

            _isReady.value = true
        }
    }
}

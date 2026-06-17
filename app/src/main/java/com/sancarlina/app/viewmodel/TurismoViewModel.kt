package com.sancarlina.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sancarlina.app.BuildConfig
import com.sancarlina.app.data.repository.AreasRepository
import com.sancarlina.app.data.repository.TenantsRepository
import com.sancarlina.app.utils.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TurismoViewModel(
    private val tenantsRepository: TenantsRepository,
    private val areasRepository: AreasRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TurismoUiState(isLoading = true))
    val uiState: StateFlow<TurismoUiState> = _uiState.asStateFlow()

    init {
        loadTurismoData()
    }

    /** Carga puntos turísticos reales; en release no inventa datos (2B-4.1). */
    private fun loadTurismoData() {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                // TODO: colección turismo_points o filtro tenants turismo cuando exista en Firestore
                if (BuildConfig.DEBUG) {
                    loadDebugMockPoints()
                } else {
                    _uiState.update {
                        it.copy(points = emptyList(), isLoading = false)
                    }
                }
            } catch (e: Exception) {
                Logger.e("Error loading turismo data", e)
                _uiState.update { it.copy(points = emptyList(), isLoading = false) }
            }
        }
    }

    /** Solo builds DEBUG — no visible en release. */
    private fun loadDebugMockPoints() {
        val mockPoints = listOf(
            TurismoPoint(
                "t1", "Laguna del Diamante",
                "Espejo de agua a los pies del Volcán Maipo. Un paisaje lunar único.",
                "https://lh3.googleusercontent.com/aida-public/AB6AXuDB8Z_Yh-Y_3H3_kZ_X_H_Y_Z_H_Y_Z_H_Y_Z_H_Y_Z_H_Y_Z",
                "Naturaleza"
            ),
            TurismoPoint(
                "t2", "Fuerte de San Carlos",
                "Lugar histórico fundado en 1772, cuna de nuestra identidad.",
                "https://lh3.googleusercontent.com/aida-public/AB6AXuDB8Z_Yh-Y_3H3_kZ_X_H_Y_Z_H_Y_Z_H_Y_Z_H_Y_Z_H_Y_Z",
                "Cultura"
            )
        )
        _uiState.update {
            it.copy(points = mockPoints, isLoading = false)
        }
    }

    fun onCategorySelected(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
    }
}

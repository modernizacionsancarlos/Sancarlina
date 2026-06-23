package com.sancarlina.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.sancarlina.app.BuildConfig
import com.sancarlina.app.data.repository.AreasRepository
import com.sancarlina.app.data.repository.TenantsRepository
import com.sancarlina.app.utils.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull

class TurismoViewModel(
    private val tenantsRepository: TenantsRepository,
    private val areasRepository: AreasRepository
) : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val _uiState = MutableStateFlow(TurismoUiState(isLoading = true))
    val uiState: StateFlow<TurismoUiState> = _uiState.asStateFlow()

    init {
        loadTurismoData()
    }

    /** Carga puntos turísticos reales directamente desde la base de datos remota de Firestore. */
    private fun loadTurismoData() {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                val snapshot = withTimeoutOrNull(5000) {
                    firestore.collection("turismo_points").get().await()
                }

                val points = if (snapshot != null && !snapshot.isEmpty) {
                    snapshot.documents.map { doc ->
                        TurismoPoint(
                            id = doc.id,
                            name = doc.getString("name") ?: "",
                            description = doc.getString("description") ?: "",
                            imageUrl = doc.getString("imageUrl") ?: "",
                            category = doc.getString("category") ?: "",
                            location = doc.getString("location") ?: "San Carlos Centro"
                        )
                    }
                } else {
                    if (BuildConfig.DEBUG) getDefaultMockPoints() else emptyList()
                }

                _uiState.update {
                    it.copy(points = points, isLoading = false)
                }
            } catch (e: Exception) {
                Logger.e("Error loading turismo data from Firestore", e)
                _uiState.update {
                    it.copy(
                        points = if (BuildConfig.DEBUG) getDefaultMockPoints() else emptyList(),
                        isLoading = false
                    )
                }
            }
        }
    }

    /** Mocks de respaldo solo para entorno local debug en caso de base de datos vacía o sin red. */
    private fun getDefaultMockPoints(): List<TurismoPoint> {
        return listOf(
            TurismoPoint(
                "t1", "Laguna del Diamante",
                "Espejo de agua a los pies del Volcán Maipo. Un paisaje lunar único.",
                "https://lh3.googleusercontent.com/aida-public/AB6AXuAjV-1Rz1ySHF1DsiSZuEGobN5EBTnYGI6yazVOQxji2lyXTHrPuh0HlYYS-IjU6Ovyh78zJkZp-jryHIPs3Uay1BndaYJSuCpe6rrWI7BplUF9eqzIch8QoKT4ATBfF_EJAIld2f982pctq-3643tIp9ZmRexEbATcGn6I4zXbIIULzJoIw6AEOcaBQ6dzzhp2yBeRiCHoh6ozUlhdWw1SRSXCrg-5Jveq-RI5gjOreWd-u-Q3QPcnYkkX96a3r2kTQ5-ibNCajlm9",
                "Naturaleza"
            ),
            TurismoPoint(
                "t2", "Fuerte de San Carlos",
                "Lugar histórico fundado en 1772, cuna de nuestra identidad.",
                "https://lh3.googleusercontent.com/aida-public/AB6AXuD1KKye6sPJABfxVlO1gK6Pvfbjv6tMRCCOaOcpn9-akT_NN-DanH3l6vJyj6y3rMsJLCWci9kF2gGRwuCGnGnhqukX_Diz4fPzrwGGxQ9OVQVdR5_48sl4WwApzaKf4OsDrbCkXM5YuclXvvkL12d0FZFAcAe7alY9joaX-NDMP5LOOwSfk6buPbiWyJDPi8abiES9UQdReTJzLBRkd_wrF0EgcTQt1qmVf0evDbEwxbBJU2I7h345aOzIVxkPaktyrUjBgJ5J07cg",
                "Cultura"
            )
        )
    }

    fun onCategorySelected(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
    }
}

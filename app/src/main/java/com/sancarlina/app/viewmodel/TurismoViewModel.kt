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
                            location = doc.getString("location") ?: "San Carlos Centro",
                            rating = doc.getDouble("rating") ?: 0.0
                        )
                    }
                } else {
                    emptyList()
                }

                _uiState.update {
                    it.copy(points = points, isLoading = false)
                }
            } catch (e: Exception) {
                Logger.e("Error loading turismo data from Firestore", e)
                _uiState.update {
                    it.copy(
                        points = emptyList(),
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onCategorySelected(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
    }
}

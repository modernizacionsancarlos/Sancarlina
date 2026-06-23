package com.sancarlina.app.ui.features.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sancarlina.app.BuildConfig
import com.sancarlina.app.viewmodel.CommerceMarker
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.sancarlina.app.utils.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CategoryListViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val _uiState = MutableStateFlow(CategoryListUiState())
    val uiState: StateFlow<CategoryListUiState> = _uiState.asStateFlow()

    fun loadCategory(categoryId: String) {
        if (categoryId.isBlank()) return

        _uiState.update { it.copy(isLoading = true, categoryName = categoryId.uppercase(), hasLoadError = false) }

        viewModelScope.launch {
            try {
                val result = firestore.collection("commerces")
                    .whereEqualTo("category", categoryId)
                    .get()
                    .await()

                val commerceList = result.documents.map { doc ->
                    val pos = doc.getGeoPoint("position")
                    CommerceMarker(
                        id = doc.id,
                        name = doc.getString("name") ?: "",
                        locationName = doc.getString("locationName") ?: "",
                        position = LatLng(pos?.latitude ?: 0.0, pos?.longitude ?: 0.0),
                        category = doc.getString("category") ?: "",
                        phone = doc.getString("phone") ?: "",
                        imageUrl = doc.getString("imageUrl") ?: "",
                        rating = (doc.getDouble("rating") ?: 5.0).toFloat(),
                        distance = doc.getString("distance") ?: "A 1.5 km de vos"
                    )
                }

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
            } catch (e: Exception) {
                Logger.e("Error loading category commerces", e)
                _uiState.update {
                    it.copy(
                        commerces = emptyList(),
                        filteredCommerces = emptyList(),
                        locations = listOf("Todas"),
                        isLoading = false,
                        hasLoadError = true
                    )
                }
            }
        }
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

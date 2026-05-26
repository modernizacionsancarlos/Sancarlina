package com.example.sancarlina.ui.features.category

import androidx.lifecycle.ViewModel
import com.example.sancarlina.ui.features.map.CommerceMarker
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CategoryListViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val _uiState = MutableStateFlow(CategoryListUiState())
    val uiState: StateFlow<CategoryListUiState> = _uiState.asStateFlow()

    fun loadCategory(categoryId: String) {
        _uiState.update { it.copy(isLoading = true, categoryName = categoryId.uppercase()) }

        firestore.collection("commerces")
            .whereEqualTo("category", categoryId)
            .get()
            .addOnSuccessListener { result ->
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

                val locations = listOf("Todas") + commerceList.map { it.locationName }.distinct()

                _uiState.update { 
                    it.copy(
                        commerces = commerceList,
                        filteredCommerces = commerceList,
                        locations = locations,
                        isLoading = false
                    )
                }
            }
            .addOnFailureListener {
                // Fallback to mock data if Firestore is empty or fails for now
                loadMockData(categoryId)
            }
    }

    private fun loadMockData(categoryId: String) {
        val mockList = listOf(
            CommerceMarker(
                id = "1",
                name = "Bodega O. Fournier",
                locationName = "La Consulta",
                position = LatLng(-33.7333, -69.1167),
                category = categoryId,
                phone = "5492622000001",
                imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDxrlCdWV-oLelj-JgQ0MijsJKJKVsP389UkDVE1BUSdv8p8PJjXpZInJvuNIDqNGGyPvYKHGFpQg0vo23GOxVWi2LL1gEBpApfvF-IA_KwNyKbZgrUXwDMN_xsj0QXVlIEuJ52VaIGlTrpvBV6fXC3GWvnl0bW-iXJWi1P6BX_CDhGES5l_7rPL5V-mCGLbgc9YR9xXI247rU99MMqEYY1UO6u4g8iFNTTwn3i8Iq-lsdUsGfpbxeVSVH0wkGR3Rq5Q0iLIa2KTOI",
                rating = 4.8f,
                distance = "A 2.5 km de vos"
            ),
            CommerceMarker(
                id = "2",
                name = "Finca La Celia",
                locationName = "Eugenio Bustos",
                position = LatLng(-33.7667, -69.1000),
                category = categoryId,
                phone = "5492622000002",
                imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDqRRXzgOsXEGjd7hU4sp0ogAWj6ziCETn5orGxGaxDrds4rajh2-Oq1xI_zo1E6Qbw39ZiwJRXQaViytdfnpj-iK57JI5Ka57vnmIGZ-5c-mS0RysfSO68JxDxhTxpuUzsW-j1a8Huvco3qhtfIncJdyjjH3EDvM5RY2ssw5MAaPOedn84EqcOryHb0VYn46dXKzbqDPuf8uOqHKEJoKMqmcZ0D18ZaDlFHbVRT2v-e9a-KR2pSkHDBfgKF9ril7fFuLfhytDtyPw",
                rating = 5.0f,
                distance = "A 1.2 km de vos"
            )
        )
        val locations = listOf("Todas") + mockList.map { it.locationName }.distinct()
        _uiState.update { 
            it.copy(
                commerces = mockList,
                filteredCommerces = mockList,
                locations = locations,
                isLoading = false
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

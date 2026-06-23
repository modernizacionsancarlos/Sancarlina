package com.sancarlina.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sancarlina.app.BuildConfig
import com.sancarlina.app.data.repository.TenantsRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.sancarlina.app.R
import com.sancarlina.app.utils.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull

class HomeViewModel(
    private val tenantsRepository: TenantsRepository
) : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    
    // Estado inicial vacío; datos reales desde Firestore (2B-4.1)
    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                // 1. Fetch Tenants con timeout
                val tenants = withTimeoutOrNull(5000) { tenantsRepository.getActiveTenants() } ?: emptyList()
                _uiState.update { it.copy(tenants = tenants) }

                // 2. Fetch Banners
                val bannerSnapshot = withTimeoutOrNull(5000) { firestore.collection("banners").get().await() }
                val banners = if (bannerSnapshot != null && !bannerSnapshot.isEmpty) {
                    bannerSnapshot.documents.map { doc ->
                        BannerItem(
                            title = doc.getString("title") ?: "",
                            subtitle = doc.getString("subtitle") ?: "",
                            imageUrl = doc.getString("imageUrl") ?: ""
                        )
                    }
                } else {
                    emptyList()
                }

                // 3. Fetch Categories
                val categorySnapshot = withTimeoutOrNull(5000) { firestore.collection("categories").orderBy("order").get().await() }
                val categories = if (categorySnapshot != null && !categorySnapshot.isEmpty) {
                    categorySnapshot.documents.map { doc ->
                        CategoryItem(
                            name = doc.getString("name") ?: "",
                            iconUrl = doc.getString("iconUrl") ?: ""
                        )
                    }
                } else {
                    emptyList()
                }

                // 4. Fetch Featured Product (Nearby)
                val productSnapshot = withTimeoutOrNull(5000) {
                    firestore.collection("products")
                        .whereEqualTo("featured", true)
                        .limit(1)
                        .get()
                        .await()
                }
                    
                val productDoc = productSnapshot?.documents?.firstOrNull()
                val nearbyProduct = if (productDoc != null) {
                    ProductItem(
                        id = productDoc.id,
                        name = productDoc.getString("name") ?: "",
                        brand = productDoc.getString("brand") ?: "",
                        price = productDoc.getString("price") ?: "",
                        phone = productDoc.getString("phone") ?: ""
                    )
                } else null

                _uiState.update {
                    it.copy(
                        banners = banners,
                        categories = categories,
                        nearbyProduct = nearbyProduct,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                Logger.e("Error loading home data", e)
                _uiState.update {
                    it.copy(
                        banners = emptyList(),
                        categories = emptyList(),
                        isLoading = false
                    )
                }
            }
        }
    }
}

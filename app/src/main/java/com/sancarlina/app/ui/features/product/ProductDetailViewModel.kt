package com.sancarlina.app.ui.features.product

import androidx.lifecycle.ViewModel
import com.sancarlina.app.BuildConfig
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ProductDetailViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    fun loadProduct(productId: String) {
        _uiState.update { it.copy(isLoading = true, notFound = false, product = null) }

        firestore.collection("products").document(productId).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    fun stringList(field: String): List<String> =
                        (doc.get(field) as? List<*>)
                            ?.mapNotNull { value -> value as? String }
                            ?.filter(String::isNotBlank)
                            .orEmpty()
                    val item = ProductDetail(
                        id = doc.id,
                        name = doc.getString("name") ?: "",
                        location = doc.getString("location") ?: "San Carlos",
                        description = doc.getString("description") ?: "Producto de calidad sancarlina.",
                        imageUrl = doc.getString("imageUrl") ?: "",
                        galleryImages = stringList("galleryImages"),
                        tags = stringList("tags"),
                        phone = doc.getString("phone") ?: "5492622000000"
                    )
                    _uiState.update { it.copy(product = item, isLoading = false, notFound = false) }
                } else {
                    _uiState.update { it.copy(product = null, isLoading = false, notFound = true) }
                }
            }
            .addOnFailureListener {
                _uiState.update { it.copy(product = null, isLoading = false, notFound = true) }
            }
    }
}

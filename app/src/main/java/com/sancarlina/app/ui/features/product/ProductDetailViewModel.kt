package com.sancarlina.app.ui.features.product

import androidx.lifecycle.ViewModel
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
        _uiState.update { it.copy(isLoading = true) }

        firestore.collection("products").document(productId).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val item = ProductDetail(
                        id = doc.id,
                        name = doc.getString("name") ?: "",
                        location = doc.getString("location") ?: "San Carlos",
                        description = doc.getString("description") ?: "Producto de calidad sancarlina.",
                        imageUrl = doc.getString("imageUrl") ?: "",
                        galleryImages = doc.get("galleryImages") as? List<String> ?: emptyList(),
                        tags = doc.get("tags") as? List<String> ?: emptyList(),
                        phone = doc.getString("phone") ?: "5492622000000"
                    )
                    _uiState.update { it.copy(product = item, isLoading = false) }
                } else {
                    loadMockProduct(productId)
                }
            }
            .addOnFailureListener {
                loadMockProduct(productId)
            }
    }

    private fun loadMockProduct(id: String) {
        val mock = ProductDetail(
            id = id,
            name = "Miel Sancarlina (Demo)",
            location = "Eugenio Bustos",
            description = "Nuestra miel es recolectada de las flores autóctonas del Valle de Uco, garantizando un sabor puro y natural.",
            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAw8yHTSfDNpOTxDnxzX1zQwB-zC4n_GoYRCOiJ9kDgy6JO-Mfi8wUELdjCqs1PD0RZ5rgMJQmWWTLKSeEZ5s9J6SuJuMf5wvcA-qBwpFgILnIurTUzqV6UHsl4IHcDN3v44YYf7tLACr0xnXhKm2e1UJe0mAbntvBcLEj3MrrM07MlyaibnOAdaAIdEBckuCO1s9B_Sy3C-y9b8SGELmQvO1IVAJAaXaJjo_gzlhpFWEUVcRS6dZU2dKaLZJCciPPaLQHK-kanZug",
            galleryImages = emptyList(),
            tags = listOf("Orgánico", "Local"),
            phone = "5492622000000"
        )
        _uiState.update { it.copy(product = mock, isLoading = false) }
    }
}

package com.example.sancarlina.ui.features.product

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ProductDetailViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    fun loadProduct(productId: String) {
        // In a real app, this would fetch from a repository
        // Mocking the data for "Miel Pura de Abeja" as per Vista 6
        _uiState.update {
            it.copy(
                product = ProductDetail(
                    id = productId,
                    name = "Miel Pura de Abeja",
                    location = "Eugenio Bustos, San Carlos",
                    description = "Nuestra miel es recolectada de forma 100% artesanal en las fincas de Eugenio Bustos. Extraída en frío para conservar todas sus propiedades naturales, vitaminas y sabor auténtico del Valle de Uco. Un producto directo de la colmena a tu mesa, sin conservantes ni aditivos.",
                    imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDR7fJTZFE8JC-jbBEEXqwKx4eQ9tKdpfStvBK4mTzoPzMk0SJns9EG6SD50Mpoz0XKOlTnz_LadQNg8nhvGMSodQbdkC-khhlnmMkF3u9kp6xdpW5HjOXWosku-khh2gMgGNb6yx_9GPoSx6beIl-Yro2pCcHCEJMlhC8lC8t-8ndz0YyOIGm3QQtk7lINPBoRkS7GdlzDmsbY9X-jeoGyk_tWogB_1aYLu-nrGO1KxCBm8HaaKV60PyGv5GuOh3RmDiS24P2qnyQ",
                    galleryImages = listOf(
                        "https://lh3.googleusercontent.com/aida-public/AB6AXuDskGhaHii9PH9sY5bjkC4-T66SVNhSYiT4yxb60L7qIBeBYjFp14RfXHZkiFTpnRtg18PzuIpBIvevcyeZNnOUh5ZUmfPWRT511xLs0kNTrPqRLQyYgncm8m1OtdjWqO-yie4C4WDZ6G9tN84ZNIHALKnjkAY7zDoWiIlInh6eVITmN6Tlpt_EmayyH5u4gK9vdrotuzbPLaqOcTNGDEpE93glq25DwvdbI8sMWJahyBfcXMMmKt20H5ZzJzpgRbjf0r5GdtYsK_I",
                        "https://lh3.googleusercontent.com/aida-public/AB6AXuDefgbIoWvW2yP8Nl4Pbmi7WqduDLvWwVpwB_RYQCy5rjG-xhflomQbXEUsFQ0CDHKOTKZhICXYkUxi1_hVEUX31ye_oBpSmOkjyPWWMgCHe9Q7R4Fa0qG1tUuKVDnX-MdiBQdxPG3chfgJqz1bLIM4SJGNR_4oaSvPf2l2v1cNGhbGaNQKDQYDuC49sTLPyWhjmW7k8q1tfIRb3XH6DRGPqAlg4QOiWu3H52mvGw-JCS4igfeDI5thecCKQxAAvTMnJhrsdxJ6Zc4",
                        "https://lh3.googleusercontent.com/aida-public/AB6AXuDnivKSWjzI07sZ4rWyR3_m1joPIwRBGUB-XBnoHZFQIapZbhrZBOhNmkgXZlv7eFdR23VeB9uCVnZmtEEt3TJDiDUjt24h9FF92RTgiKMZytCAWHuS9mL0FM5NC7h-UztEACVEZ7TDitHbNIrNAjtxky53NWg52vevxy-lYgI49Y7IujZe-mWxOTCbPygSbRv-_qDZqa8ojwaQns98xRLXO_gSoEaD2eK1TAIVS8E2kzPjVhKkwFgF4KASthLJtrkyFxahTQrNCPs"
                    ),
                    tags = listOf("Artesanal", "Productor Local"),
                    phone = "5492622000000",
                    isFavorite = false
                )
            )
        }
    }

    fun toggleFavorite() {
        _uiState.update {
            it.copy(product = it.product?.copy(isFavorite = !it.product.isFavorite))
        }
    }
}

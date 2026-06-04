package com.sancarlina.app.viewmodel

data class PointsUiState(
    val balance: Int = 0,
    val benefits: List<BenefitItem> = emptyList(),
    val selectedBenefit: BenefitItem? = null,
    val isLoading: Boolean = false,
    val qrCodeActive: Boolean = false,
    val qrTimeRemaining: String = "10:00",
    val showSuccessModal: Boolean = false
)

data class BenefitItem(
    val id: String,
    val title: String,
    val brand: String,
    val cost: Int,
    val category: String,
    val imageUrl: String = ""
)

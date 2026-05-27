package com.example.sancarlina.ui.features.emprendimiento

data class EmprendimientoUiState(
    val nombre: String = "",
    val categoria: String = "",
    val telefono: String = "",
    val ubicacion: String = "",
    val imageUri: String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

val categoriasEmprendimiento = listOf(
    "Gastronomía",
    "Artesanías",
    "Servicios",
    "Indumentaria",
    "Productores",
    "Otro"
)

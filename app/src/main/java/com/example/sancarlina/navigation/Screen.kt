package com.example.sancarlina.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    // Main Tabs
    object Home : Screen("home", "Inicio", Icons.Default.Home)
    object Map : Screen("map", "Mapa", Icons.Default.LocationOn)
    object Points : Screen("points", "Puntos", Icons.Default.Star)
    object Profile : Screen("profile", "Perfil", Icons.Default.Person)

    // Other screens
    object SplashScreen : Screen("splash", "Splash")
    object Onboarding : Screen("onboarding", "Onboarding")
    object Login : Screen("login", "Login")
    object Register : Screen("register", "Registro")
    object Offline : Screen("offline", "Offline")
    object Updates : Screen("updates", "Actualizaciones")
    object ProductDetail : Screen("product_detail/{productId}", "Detalle") {
        fun createRoute(productId: String) = "product_detail/$productId"
    }
}

val bottomNavItems = listOf(
    Screen.Home,
    Screen.Map,
    Screen.Points,
    Screen.Profile
)

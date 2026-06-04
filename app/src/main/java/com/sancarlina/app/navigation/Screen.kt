package com.sancarlina.app.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    // Main Tabs
    object Home : Screen("home", "Inicio", Icons.Default.Home)
    object Turismo : Screen("turismo", "Turismo", Icons.Default.Explore)
    object Map : Screen("map", "Mapa", Icons.Default.Map)
    object Points : Screen("points", "Puntos", Icons.Default.Stars)
    object QrScanner : Screen("qr_scanner", "Escanear QR")
    object Profile : Screen("profile", "Perfil", Icons.Default.Person)

    // Other screens
    object SplashScreen : Screen("splash", "Splash")
    object Onboarding : Screen("onboarding", "Onboarding")
    object Login : Screen("login", "Login")
    object Register : Screen("register", "Registro")
    object ForgotPassword : Screen("forgot_password", "Recuperar")
    object Offline : Screen("offline", "Offline")
    object Favorites : Screen("favorites", "Favoritos")
    object EditProfile : Screen("edit_profile", "Mis Datos")
    object Emprendimiento : Screen("sumar_emprendimiento", "Sumar Emprendimiento")
    object Notifications : Screen("notifications", "Notificaciones")
    object NotificationSettings : Screen("notification_settings", "Ajustes de Alertas")
    object Success : Screen("success", "Éxito")
    object Support : Screen("support", "Soporte")
    object Search : Screen("search", "Búsqueda")
    object NewsDetail : Screen("news_detail", "Novedad")
    object Legal : Screen("legal", "Legal")
    object PointsHistory : Screen("points_history", "Historial")
    
    object ProductDetail : Screen("product_detail/{productId}", "Detalle") {
        fun createRoute(productId: String) = "product_detail/$productId"
    }
    object CategoryList : Screen("category_list/{categoryId}", "Categoría") {
        fun createRoute(categoryId: String) = "category_list/$categoryId"
    }
    object CommerceProfile : Screen("commerce_profile/{commerceId}", "Comercio") {
        fun createRoute(commerceId: String) = "commerce_profile/$commerceId"
    }
    object RateCommerce : Screen("rate_commerce/{commerceId}", "Calificar") {
        fun createRoute(commerceId: String) = "rate_commerce/$commerceId"
    }
    object ServiciosSello : Screen("servicios_sello", "Servicios")
}

val bottomNavItems = listOf(
    Screen.Home,
    Screen.Turismo,
    Screen.Map,
    Screen.Points,
    Screen.Profile
)

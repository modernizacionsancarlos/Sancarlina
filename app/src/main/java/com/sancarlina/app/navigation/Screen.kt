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
    object BodegasTab : Screen("bodegas_tab", "Bodegas", Icons.Default.WineBar)

    // Other screens
    object SplashScreen : Screen("splash", "Splash")
    object Onboarding : Screen("onboarding", "Onboarding")
    object Login : Screen("login", "Login")
    object Register : Screen("register", "Registro")
    object ForgotPassword : Screen("forgot_password", "Recuperar")
    object Offline : Screen("offline", "Offline")
    object Favorites : Screen("favorites", "Favoritos", Icons.Default.FavoriteBorder)
    object EditProfile : Screen("edit_profile", "Mis Datos")
    object Emprendimiento : Screen("sumar_emprendimiento", "Sumar Emprendimiento")
    object Notifications : Screen("notifications", "Notificaciones")
    object NotificationSettings : Screen("notification_settings", "Ajustes de Alertas")
    object Success : Screen("success", "Éxito")
    object Support : Screen("support", "Soporte")
    object Search : Screen("search", "Búsqueda")
    object NewsList : Screen("news_list", "Revista", Icons.Default.Newspaper)
    object NewsDetail : Screen("news_detail/{newsId}", "Novedad") {
        fun createRoute(newsId: String) = "news_detail/$newsId"
    }
    object Legal : Screen("legal", "Legal")
    object PointsHistory : Screen("points_history", "Historial")
    object InstitutionalInfo : Screen("institutional_info", "Institucional")
    object TurismoDetail : Screen("turismo_detail/{pointId}", "Detalle Turismo") {
        fun createRoute(pointId: String) = "turismo_detail/$pointId"
    }
    
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
    object UserReviews : Screen("commerce_reviews/{commerceId}", "Reseñas") {
        fun createRoute(commerceId: String) = "commerce_reviews/$commerceId"
    }
    object ServiciosSello : Screen("servicios_sello", "Servicios")

    // Admin Panel Screens
    object AdminLogin : Screen("admin_login", "Acceso Administración")
    object AdminHome : Screen("admin_home", "Panel Admin", Icons.Default.AdminPanelSettings)
    object AdminComercios : Screen("admin_comercios", "Comercios", Icons.Default.Store)
    object AdminZonas : Screen("admin_zonas", "Zonas", Icons.Default.Map)
    object AdminBeneficios : Screen("admin_beneficios", "Beneficios", Icons.Default.CardGiftcard)
    object AdminUsuarios : Screen("admin_usuarios", "Usuarios", Icons.Default.Group)
    object AdminFormularios : Screen("admin_formularios", "Formularios", Icons.Default.Description)
    object AdminNotificaciones : Screen("admin_notificaciones", "Notificaciones", Icons.Default.Notifications)
    object AdminAdministradores : Screen("admin_administradores", "Administradores", Icons.Default.Security)
}

val bottomNavItems = listOf(
    Screen.Home,
    Screen.Turismo,
    Screen.Map,
    Screen.Points,
    Screen.Profile
)

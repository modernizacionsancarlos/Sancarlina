package com.example.sancarlina.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.sancarlina.ui.features.auth.LoginContent
import com.example.sancarlina.ui.features.auth.OnboardingContent
import com.example.sancarlina.ui.features.auth.RegisterContent
import com.example.sancarlina.ui.features.category.CategoryListContent
import com.example.sancarlina.ui.features.common.SuccessContent
import com.example.sancarlina.ui.features.emprendimiento.EmprendimientoContent
import com.example.sancarlina.ui.features.favorites.FavoritesContent
import com.example.sancarlina.ui.features.notifications.NotificationsContent
import com.example.sancarlina.ui.features.profile.EditProfileContent
import com.example.sancarlina.ui.features.home.HomeContent
import com.example.sancarlina.ui.features.map.MapContent
import com.example.sancarlina.ui.features.points.PointsContent
import com.example.sancarlina.ui.features.product.ProductDetailContent
import com.example.sancarlina.ui.features.profile.ProfileContent
import com.example.sancarlina.ui.features.servicios.ServiciosSelloContent
import com.example.sancarlina.ui.features.splash.SplashScreenContent
import com.example.sancarlina.ui.features.support.SupportContent
import com.example.sancarlina.ui.features.turismo.TurismoContent
import com.example.sancarlina.ui.features.updates.UpdatesContent

@Composable
fun SancarlinaNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    onOnboardingFinished: () -> Unit = {},
    onOpenDrawer: () -> Unit = {}
) {
    NavHost(
        navController = navController,
        startDestination = Screen.SplashScreen.route,
        modifier = modifier
    ) {
        composable(Screen.SplashScreen.route) {
            SplashScreenContent(
                onTimeout = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.SplashScreen.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Home.route) {
            HomeContent(
                onNavigateToDetail = { productId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId))
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route)
                },
                onNavigateToCategory = { categoryId ->
                    if (categoryId.contains("SERVICIOS", ignoreCase = true)) {
                        navController.navigate(Screen.ServiciosSello.route)
                    } else {
                        navController.navigate(Screen.CategoryList.createRoute(categoryId))
                    }
                },
                onOpenDrawer = onOpenDrawer
            )
        }
        composable(Screen.CategoryList.route) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
            CategoryListContent(
                categoryId = categoryId,
                onBack = { navController.popBackStack() },
                onNavigateToDetail = { productId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId))
                }
            )
        }
        composable(Screen.Turismo.route) {
            TurismoContent(onOpenDrawer = onOpenDrawer)
        }
        composable(Screen.Login.route) {
            LoginContent(
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onLoginSuccess = { 
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Onboarding.route) {
            OnboardingContent(
                onFinish = {
                    onOnboardingFinished()
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Register.route) {
            RegisterContent(
                onBack = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Map.route) {
            MapContent(onOpenDrawer = onOpenDrawer)
        }
        composable(Screen.Points.route) {
            PointsContent(
                onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                onOpenDrawer = onOpenDrawer
            )
        }
        composable(Screen.Profile.route) {
            ProfileContent(
                onNavigateToUpdates = { navController.navigate(Screen.Updates.route) },
                onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                onOpenDrawer = onOpenDrawer,
                onNavigateToFavorites = { navController.navigate(Screen.Favorites.route) },
                onNavigateToEditProfile = { navController.navigate(Screen.EditProfile.route) },
                onNavigateToEmprendimiento = { navController.navigate(Screen.Emprendimiento.route) },
                onNavigateToNotifications = { navController.navigate(Screen.Notifications.route) },
                onNavigateToSupport = { navController.navigate(Screen.Support.route) }
            )
        }
        composable(Screen.EditProfile.route) {
            EditProfileContent(onBack = { navController.popBackStack() })
        }
        composable(Screen.Emprendimiento.route) {
            EmprendimientoContent(onBack = { 
                navController.navigate(Screen.Success.route) {
                    popUpTo(Screen.Emprendimiento.route) { inclusive = true }
                }
            })
        }
        composable(Screen.Notifications.route) {
            NotificationsContent(onBack = { navController.popBackStack() })
        }
        composable(Screen.Success.route) {
            SuccessContent(
                onButtonClick = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(0)
                    }
                }
            )
        }
        composable(Screen.Support.route) {
            SupportContent(
                onBack = { navController.popBackStack() },
                onOpenDrawer = onOpenDrawer
            )
        }
        composable(Screen.Favorites.route) {
            FavoritesContent(
                onBack = { navController.popBackStack() },
                onNavigateToDetail = { productId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId))
                }
            )
        }
        composable(Screen.ProductDetail.route) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            ProductDetailContent(
                productId = productId,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Updates.route) {
            UpdatesContent(onBack = { navController.popBackStack() })
        }
        composable(Screen.ServiciosSello.route) {
            ServiciosSelloContent(onBack = { navController.popBackStack() })
        }
        composable(Screen.Offline.route) {
            PlaceholderScreen(Screen.Offline.title)
        }
    }
}

@Composable
fun PlaceholderScreen(name: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Pantalla: $name")
    }
}

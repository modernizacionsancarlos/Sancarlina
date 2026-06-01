package com.sancarlina.app.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sancarlina.app.ui.features.auth.LoginContent
import com.sancarlina.app.ui.features.auth.OnboardingContent
import com.sancarlina.app.ui.features.auth.RegisterContent
import com.sancarlina.app.ui.features.category.CategoryListContent
import com.sancarlina.app.ui.features.home.HomeContent
import com.sancarlina.app.ui.features.map.MapContent
import com.sancarlina.app.ui.features.points.PointsContent
import com.sancarlina.app.ui.features.product.ProductDetailContent
import com.sancarlina.app.ui.features.profile.ProfileContent
import com.sancarlina.app.ui.features.servicios.ServiciosSelloContent
import com.sancarlina.app.ui.features.turismo.TurismoContent
import com.sancarlina.app.ui.features.updates.UpdatesContent

@Composable
fun SancarlinaNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    onOnboardingFinished: () -> Unit = {},
    onOpenDrawer: () -> Unit = {}
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeContent(
                onNavigateToDetail = { productId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId))
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route)
                },
                onNavigateToCategory = { categoryId ->
                    navController.navigate(Screen.CategoryList.createRoute(categoryId))
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
                onOpenDrawer = onOpenDrawer
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

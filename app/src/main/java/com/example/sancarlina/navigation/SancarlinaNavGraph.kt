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
import com.example.sancarlina.ui.features.home.HomeContent
import com.example.sancarlina.ui.features.map.MapContent
import com.example.sancarlina.ui.features.points.PointsContent
import com.example.sancarlina.ui.features.product.ProductDetailContent
import com.example.sancarlina.ui.features.profile.ProfileContent
import com.example.sancarlina.ui.features.updates.UpdatesContent

@Composable
fun SancarlinaNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
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
                }
            )
        }
        composable(Screen.Map.route) {
            MapContent()
        }
        composable(Screen.Points.route) {
            PointsContent()
        }
        composable(Screen.Profile.route) {
            ProfileContent(
                onNavigateToUpdates = { navController.navigate(Screen.Updates.route) }
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

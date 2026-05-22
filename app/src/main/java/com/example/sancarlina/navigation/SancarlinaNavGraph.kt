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
import com.example.sancarlina.ui.features.profile.ProfileContent

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
            HomeContent()
        }
        composable(Screen.Map.route) {
            MapContent()
        }
        composable(Screen.Points.route) {
            PointsContent()
        }
        composable(Screen.Profile.route) {
            ProfileContent()
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

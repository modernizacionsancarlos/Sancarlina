package com.example.sancarlina.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.sancarlina.navigation.SancarlinaNavGraph
import com.example.sancarlina.navigation.Screen
import com.example.sancarlina.navigation.bottomNavItems
import com.example.sancarlina.utils.ConnectivityObserver
import com.example.sancarlina.utils.NetworkConnectivityObserver

@Composable
fun MainScaffold() {
    val context = LocalContext.current
    val connectivityObserver = NetworkConnectivityObserver(context)
    val status by connectivityObserver.observe().collectAsState(initial = ConnectivityObserver.Status.Available)
    val navController = rememberNavController()
    
    LaunchedEffect(status) {
        if (status != ConnectivityObserver.Status.Available) {
            navController.navigate(Screen.Offline.route) {
                popUpTo(0)
            }
        }
    }
    
    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            
            // Solo mostrar bottomBar en las 4 vistas maestras
            val showBottomBar = bottomNavItems.any { it.route == currentDestination?.route }
            
            if (showBottomBar) {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 8.dp
                ) {
                    bottomNavItems.forEach { screen ->
                        val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                        NavigationBarItem(
                            icon = { 
                                screen.icon?.let { 
                                    Icon(it, contentDescription = screen.title) 
                                } 
                            },
                            label = { 
                                Text(
                                    text = screen.title.uppercase(),
                                    fontSize = 10.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                ) 
                            },
                            selected = selected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.secondary,
                                selectedTextColor = MaterialTheme.colorScheme.secondary,
                                unselectedIconColor = Color.Gray,
                                unselectedTextColor = Color.Gray,
                                indicatorColor = Color.Transparent
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        SancarlinaNavGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

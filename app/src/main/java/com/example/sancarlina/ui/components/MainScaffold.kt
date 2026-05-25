package com.example.sancarlina.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.sancarlina.ui.components.QuickGuide
import com.example.sancarlina.utils.NetworkConnectivityObserver
import com.google.firebase.auth.FirebaseAuth

@Composable
fun MainScaffold() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val sharedPrefs = remember { context.getSharedPreferences("sancarlina_prefs", android.content.Context.MODE_PRIVATE) }
    
    val auth = remember { FirebaseAuth.getInstance() }
    val currentUser = remember { mutableStateOf(auth.currentUser) }

    // Flags
    val onboardingCompleted = remember { mutableStateOf(sharedPrefs.getBoolean("onboarding_completed", false)) }
    val guideCompleted = remember { mutableStateOf(sharedPrefs.getBoolean("guide_completed", false)) }
    var showQuickGuide by remember { mutableStateOf(false) }

    // Auth Listener
    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { 
            currentUser.value = it.currentUser 
        }
        auth.addAuthStateListener(listener)
        onDispose { auth.removeAuthStateListener(listener) }
    }

    LaunchedEffect(onboardingCompleted.value) {
        if (!onboardingCompleted.value) {
            navController.navigate(Screen.Onboarding.route) {
                popUpTo(0)
            }
        } else if (!guideCompleted.value) {
            showQuickGuide = true
        }
    }

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            
            // Only show bottom bar for main master views and if logged in
            val isMainView = bottomNavItems.any { it.route == currentDestination?.route }
            val showBottomBar = isMainView && currentUser.value != null
            
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
        if (showQuickGuide) {
            QuickGuide(onFinish = {
                sharedPrefs.edit().putBoolean("guide_completed", true).apply()
                showQuickGuide = false
            })
        }

        SancarlinaNavGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
            onOnboardingFinished = {
                sharedPrefs.edit().putBoolean("onboarding_completed", true).apply()
                onboardingCompleted.value = true
            }
        )
    }
}

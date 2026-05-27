package com.example.sancarlina.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.sancarlina.navigation.SancarlinaNavGraph
import com.example.sancarlina.navigation.Screen
import com.example.sancarlina.navigation.bottomNavItems
import com.example.sancarlina.ui.theme.SancarlinaAccent
import com.example.sancarlina.ui.theme.SancarlinaBackground
import com.example.sancarlina.ui.theme.SancarlinaPrimary
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

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

    // Sidebar state
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

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

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color.White,
                drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)
            ) {
                Spacer(Modifier.height(48.dp))
                Column(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = com.example.sancarlina.R.drawable.app_logo),
                        contentDescription = null,
                        modifier = Modifier.size(100.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "SANCARLINA",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = SancarlinaPrimary,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "Municipalidad de San Carlos",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = SancarlinaBackground)
                
                NavigationDrawerItem(
                    label = { Text("Inicio") },
                    selected = false,
                    onClick = { 
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.Home.route) 
                    },
                    icon = { Icon(Icons.Default.Home, null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                
                NavigationDrawerItem(
                    label = { Text("Turismo") },
                    selected = false,
                    onClick = { 
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.Turismo.route) 
                    },
                    icon = { Icon(Icons.Default.Explore, null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text("Mapa") },
                    selected = false,
                    onClick = { 
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.Map.route) 
                    },
                    icon = { Icon(Icons.Default.Map, null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                Spacer(modifier = Modifier.weight(1f))
                
                Text(
                    text = "v3.9.0",
                    modifier = Modifier.padding(24.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.LightGray
                )
            }
        }
    ) {
        Scaffold(
            containerColor = SancarlinaBackground,
            bottomBar = {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination?.route
                
                // Only show bottom bar for main master views
                val isMainView = bottomNavItems.any { it.route == currentDestination }
                
                if (isMainView) {
                    SancarlinaBottomBar(navController, currentDestination)
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
                modifier = Modifier.padding(
                    bottom = innerPadding.calculateBottomPadding() // FIX: Use dynamic padding from Scaffold
                ),
                onOnboardingFinished = {
                    sharedPrefs.edit().putBoolean("onboarding_completed", true).apply()
                    onboardingCompleted.value = true
                },
                onOpenDrawer = {
                    scope.launch { drawerState.open() }
                }
            )
        }
    }
}

@Composable
fun SancarlinaBottomBar(
    navController: NavHostController,
    currentDestination: String?
) {
    val bordeaux = SancarlinaAccent
    val olive = SancarlinaPrimary
    val inactive = Color.Gray

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF9F9F6)) // Match crema-claro
            .navigationBarsPadding() // FIX: Crucial for responsive system buttons
            .height(96.dp), 
        contentAlignment = Alignment.BottomCenter
    ) {
        // Main Bar Background
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            color = Color(0xFFF9F9F6),
            border = BorderStroke(0.5.dp, Color.LightGray.copy(alpha = 0.3f)),
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavItem(
                    screen = Screen.Home,
                    isSelected = currentDestination == Screen.Home.route,
                    activeColor = olive,
                    onClick = { navigateTo(navController, Screen.Home.route) }
                )

                BottomNavItem(
                    screen = Screen.Turismo,
                    isSelected = currentDestination == Screen.Turismo.route,
                    activeColor = bordeaux,
                    onClick = { navigateTo(navController, Screen.Turismo.route) }
                )

                // ITEM CENTRAL: MAPA
                Column(
                    modifier = Modifier
                        .width(64.dp)
                        .fillMaxHeight()
                        .clickable { navigateTo(navController, Screen.Map.route) },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Spacer(modifier = Modifier.size(24.dp))
                    Text(
                        text = "MAPA",
                        fontSize = 10.sp,
                        fontWeight = if (currentDestination == Screen.Map.route) FontWeight.Bold else FontWeight.Normal,
                        color = if (currentDestination == Screen.Map.route) bordeaux else inactive,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                BottomNavItem(
                    screen = Screen.Points,
                    isSelected = currentDestination == Screen.Points.route,
                    activeColor = bordeaux,
                    onClick = { navigateTo(navController, Screen.Points.route) }
                )

                BottomNavItem(
                    screen = Screen.Profile,
                    isSelected = currentDestination == Screen.Profile.route,
                    activeColor = bordeaux,
                    onClick = { navigateTo(navController, Screen.Profile.route) }
                )
            }
        }

        // Botón Circular Elevado
        Surface(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .size(64.dp)
                .shadow(12.dp, CircleShape)
                .clickable { navigateTo(navController, Screen.Map.route) },
            color = bordeaux,
            shape = CircleShape,
            border = BorderStroke(4.dp, Color(0xFFF9F9F6))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Screen.Map.icon!!,
                    contentDescription = "Mapa",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
fun BottomNavItem(
    screen: Screen,
    isSelected: Boolean,
    activeColor: Color,
    onClick: () -> Unit
) {
    val contentColor = if (isSelected) activeColor else Color.Gray
    Column(
        modifier = Modifier
            .width(64.dp)
            .fillMaxHeight()
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            screen.icon!!,
            contentDescription = screen.title,
            tint = contentColor,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = screen.title.uppercase(),
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = contentColor,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

private fun navigateTo(navController: NavHostController, route: String) {
    navController.navigate(route) {
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

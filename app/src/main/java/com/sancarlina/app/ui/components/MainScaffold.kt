package com.sancarlina.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sancarlina.app.R
import com.sancarlina.app.navigation.SancarlinaNavGraph
import com.sancarlina.app.navigation.Screen
import com.sancarlina.app.navigation.bottomNavItems
import com.sancarlina.app.ui.theme.*
import com.sancarlina.app.utils.ConnectivityObserver
import com.sancarlina.app.utils.NetworkConnectivityObserver
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination?.route
    val isMainView = bottomNavItems.any { it.route == currentDestination }

    // Observador de red: una instancia por composición (no recrear en cada recomposición)
    val connectivityObserver = remember { NetworkConnectivityObserver(context) }
    val offlineExcludedRoutes = remember {
        setOf(
            Screen.SplashScreen.route,
            Screen.Login.route,
            Screen.Register.route,
            Screen.Onboarding.route,
            Screen.ForgotPassword.route,
            Screen.Offline.route
        )
    }

    // Navegación automática a Offline / restauración al recuperar conexión
    LaunchedEffect(navController, connectivityObserver) {
        connectivityObserver.observe().collect { status ->
            val route = navController.currentBackStackEntry?.destination?.route
            val isOffline = status == ConnectivityObserver.Status.Lost ||
                status == ConnectivityObserver.Status.Unavailable

            if (isOffline && route != null && route !in offlineExcludedRoutes) {
                navController.navigate(Screen.Offline.route) {
                    launchSingleTop = true
                }
            } else if (status == ConnectivityObserver.Status.Available &&
                route == Screen.Offline.route
            ) {
                navController.popBackStack()
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = isMainView,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = SancarlinaSurface,
                drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)
            ) {
                Spacer(Modifier.height(48.dp))
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_gondolapp_symbol),
                        contentDescription = null,
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        stringResource(R.string.app_name),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = SancarlinaPrimary
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp), color = SancarlinaOutlineVariant.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(16.dp))
                
                NavigationDrawerItem(
                    label = { Text("Inicio") },
                    selected = currentDestination == Screen.Home.route,
                    onClick = { scope.launch { drawerState.close() }; navController.navigate(Screen.Home.route) },
                    icon = { Icon(Icons.Default.Home, null) },
                    colors = NavigationDrawerItemDefaults.colors(selectedContainerColor = SancarlinaPrimary.copy(alpha = 0.1f), selectedTextColor = SancarlinaPrimary, selectedIconColor = SancarlinaPrimary),
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
        }
    ) {
        Scaffold(
            containerColor = SancarlinaSurface,
            topBar = {
                if (isMainView) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = SancarlinaSurfaceContainerLow
                    ) {
                        CenterAlignedTopAppBar(
                            title = {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_sancarlina_logo),
                                    contentDescription = stringResource(R.string.app_name),
                                    modifier = Modifier.height(28.dp)
                                )
                            },
                            navigationIcon = {
                                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                    Icon(
                                        Icons.Default.Menu,
                                        stringResource(R.string.cd_menu),
                                        tint = SancarlinaPrimary
                                    )
                                }
                            },
                            actions = {
                                IconButton(onClick = { navController.navigate(Screen.Profile.route) }) {
                                    Surface(shape = CircleShape, color = SancarlinaPrimary.copy(alpha = 0.1f), modifier = Modifier.size(40.dp)) {
                                        Icon(Icons.Outlined.Person, "Perfil", tint = SancarlinaPrimary, modifier = Modifier.padding(8.dp))
                                    }
                                }
                            },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
                        )
                    }
                }
            },
            bottomBar = {
                if (isMainView) {
                    GondolappBottomBar(navController, currentDestination)
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                SancarlinaNavGraph(
                    navController = navController,
                    onOpenDrawer = { scope.launch { drawerState.open() } }
                )
            }
        }
    }
}

@Composable
fun GondolappBottomBar(navController: NavHostController, currentDestination: String?) {
    // Bottom nav Stitch: surface-container, esquinas superiores redondeadas, pill olive activo
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = SancarlinaBottomBarShape,
        tonalElevation = 3.dp,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .navigationBarsPadding()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            bottomNavItems.forEach { screen ->
                val isSelected = currentDestination == screen.route
                val contentColor = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(percent = 50))
                        .background(
                            if (isSelected) SancarlinaNavIndicator else Color.Transparent
                        )
                        .clickable {
                            if (!isSelected) {
                                navController.navigate(screen.route) {
                                    popUpTo(Screen.Home.route) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            screen.icon ?: Icons.Default.Circle,
                            screen.title,
                            tint = contentColor,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = screen.title,
                            style = MaterialTheme.typography.labelSmall,
                            color = contentColor,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

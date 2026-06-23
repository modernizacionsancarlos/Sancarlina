package com.sancarlina.app.ui.components

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.sancarlina.app.R
import com.sancarlina.app.SancarlinaApp
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
    val app = remember(context) { context.applicationContext as? SancarlinaApp }

    val auth = remember(app) { app?.container?.auth }
    var currentUserEmail by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(auth) {
        currentUserEmail = auth?.currentUser?.email
        auth?.addAuthStateListener { firebaseAuth ->
            currentUserEmail = firebaseAuth.currentUser?.email
        }
    }

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
        gesturesEnabled = isMainView && currentDestination != Screen.Map.route,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = SancarlinaSurface,
                drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)
            ) {
                // Header del menú lateral
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SancarlinaPrimary.copy(alpha = 0.08f))
                        .padding(top = 48.dp, bottom = 24.dp, start = 24.dp, end = 24.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_gondolapp_symbol),
                        contentDescription = null,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        stringResource(R.string.app_name),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = SancarlinaPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currentUserEmail ?: "Modo Invitado",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SancarlinaOnSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Opciones del menú con scroll vertical independiente
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 12.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = "Explorar",
                        style = MaterialTheme.typography.labelMedium,
                        color = SancarlinaPrimary,
                        modifier = Modifier.padding(start = 12.dp, bottom = 8.dp),
                        fontWeight = FontWeight.Bold
                    )
                    DrawerItem(
                        label = "Inicio",
                        icon = Icons.Default.Home,
                        selected = currentDestination == Screen.Home.route,
                        onClick = { scope.launch { drawerState.close() }; navController.navigate(Screen.Home.route) }
                    )
                    DrawerItem(
                        label = "Turismo",
                        icon = Icons.Default.Explore,
                        selected = currentDestination == Screen.Turismo.route,
                        onClick = { scope.launch { drawerState.close() }; navController.navigate(Screen.Turismo.route) }
                    )
                    DrawerItem(
                        label = "Mapa Interactivo",
                        icon = Icons.Default.Map,
                        selected = currentDestination == Screen.Map.route,
                        onClick = { scope.launch { drawerState.close() }; navController.navigate(Screen.Map.route) }
                    )
                    DrawerItem(
                        label = "Puntos y Beneficios",
                        icon = Icons.Default.Stars,
                        selected = currentDestination == Screen.Points.route,
                        onClick = { scope.launch { drawerState.close() }; navController.navigate(Screen.Points.route) }
                    )
                    
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = SancarlinaOutlineVariant.copy(alpha = 0.2f))
                    
                    Text(
                        text = "Comunidad y Servicios",
                        style = MaterialTheme.typography.labelMedium,
                        color = SancarlinaPrimary,
                        modifier = Modifier.padding(start = 12.dp, bottom = 8.dp),
                        fontWeight = FontWeight.Bold
                    )
                    DrawerItem(
                        label = "Mis Favoritos",
                        icon = Icons.Default.Favorite,
                        selected = currentDestination == Screen.Favorites.route,
                        onClick = { scope.launch { drawerState.close() }; navController.navigate(Screen.Favorites.route) }
                    )
                    DrawerItem(
                        label = "Notificaciones",
                        icon = Icons.Default.Notifications,
                        selected = currentDestination == Screen.Notifications.route,
                        onClick = { scope.launch { drawerState.close() }; navController.navigate(Screen.Notifications.route) }
                    )
                    DrawerItem(
                        label = "Sumar Emprendimiento",
                        icon = Icons.Default.Store,
                        selected = currentDestination == Screen.Emprendimiento.route,
                        onClick = { scope.launch { drawerState.close() }; navController.navigate(Screen.Emprendimiento.route) }
                    )
                    DrawerItem(
                        label = "Sello de Calidad",
                        icon = Icons.Default.Verified,
                        selected = currentDestination == Screen.ServiciosSello.route,
                        onClick = { scope.launch { drawerState.close() }; navController.navigate(Screen.ServiciosSello.route) }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = SancarlinaOutlineVariant.copy(alpha = 0.2f))

                    Text(
                        text = "Información",
                        style = MaterialTheme.typography.labelMedium,
                        color = SancarlinaPrimary,
                        modifier = Modifier.padding(start = 12.dp, bottom = 8.dp),
                        fontWeight = FontWeight.Bold
                    )
                    DrawerItem(
                        label = "Ayuda y Soporte",
                        icon = Icons.Default.Help,
                        selected = currentDestination == Screen.Support.route,
                        onClick = { scope.launch { drawerState.close() }; navController.navigate(Screen.Support.route) }
                    )
                    DrawerItem(
                        label = "Información Institucional",
                        icon = Icons.Default.Info,
                        selected = currentDestination == Screen.InstitutionalInfo.route,
                        onClick = { scope.launch { drawerState.close() }; navController.navigate(Screen.InstitutionalInfo.route) }
                    )
                    DrawerItem(
                        label = "Términos y Privacidad",
                        icon = Icons.Default.Gavel,
                        selected = currentDestination == Screen.Legal.route,
                        onClick = { scope.launch { drawerState.close() }; navController.navigate(Screen.Legal.route) }
                    )
                    
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = SancarlinaOutlineVariant.copy(alpha = 0.2f))

                    if (currentUserEmail != null) {
                        DrawerItem(
                            label = "Cerrar Sesión",
                            icon = Icons.Default.ExitToApp,
                            selected = false,
                            onClick = {
                                scope.launch {
                                    drawerState.close()
                                    auth?.signOut()
                                    navController.navigate(Screen.Login.route) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            }
                        )
                    } else {
                        DrawerItem(
                            label = "Iniciar Sesión",
                            icon = Icons.Default.Login,
                            selected = false,
                            onClick = {
                                scope.launch {
                                    drawerState.close()
                                    navController.navigate(Screen.Login.route)
                                }
                            }
                        )
                    }
                }
            }
        }
    ) {
        var showCoachmarks by remember { mutableStateOf(false) }
        val prefs = remember(context) { context.getSharedPreferences("gondolapp_prefs", Context.MODE_PRIVATE) }
        LaunchedEffect(Unit) {
            val hasSeen = prefs.getBoolean("seen_coachmarks", false)
            if (!hasSeen) {
                showCoachmarks = true
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                containerColor = SancarlinaSurface,
                topBar = {
                    if (isMainView && currentDestination != Screen.Map.route) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = SancarlinaSurfaceContainerLow
                        ) {
                            CenterAlignedTopAppBar(
                                modifier = Modifier.height(72.dp),
                                title = {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_sancarlina_logo),
                                        contentDescription = stringResource(R.string.app_name),
                                        modifier = Modifier.height(32.dp)
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

            if (showCoachmarks && isMainView) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.75f))
                        .clickable(enabled = false) { }
                ) {
                    // 1. Indicador e información del Menú (arriba a la izquierda)
                    Box(
                        modifier = Modifier
                            .statusBarsPadding()
                            .padding(start = 12.dp, top = 12.dp)
                            .align(Alignment.TopStart)
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(Color.Transparent)
                                    .border(2.dp, SancarlinaSecondaryContainer, CircleShape)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                color = SancarlinaSurfaceContainerHighest,
                                shape = RoundedCornerShape(topStart = 0.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
                                modifier = Modifier.width(200.dp)
                            ) {
                                Text(
                                    text = "Accedé a tu perfil y ajustes acá",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = SancarlinaOnSurface,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }

                    // 2. Personaje de Guía en el centro
                    AsyncImage(
                        model = "https://lh3.googleusercontent.com/aida-public/AB6AXuAyJUxTCh_qeS5DuYgWuIWKDpgqA0pCczBcEs95I0ztRORK4CuKFvenMIgRc4MW7Ip6o3_CSRyxptw4m2QkOpvjLe9QT4i3RgCYOlzLOyYss2el645cPBbUJBWH-nGqNfHl0JBU1axBqoRfQHBu-YzL-90E1sZEc7fKsq8sRbKbOIwjw1tW0dIc_CFX9Azk-5mjmY9P1Wp9RdNedH3FQqJPXTv_rGR-t1H8Cc-iKk_1nC6ZCnDNju09zGvuacYn9YZO84OzsDIrD1zP",
                        contentDescription = null,
                        modifier = Modifier
                            .size(180.dp)
                            .align(Alignment.Center),
                        contentScale = ContentScale.Fit
                    )

                    // 3. Indicador e información del mapa (abajo en el centro-izquierda)
                    Column(
                        modifier = Modifier
                            .navigationBarsPadding()
                            .padding(bottom = 96.dp)
                            .align(Alignment.BottomCenter),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            color = SancarlinaSurfaceContainerHighest,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .width(240.dp)
                                .padding(horizontal = 16.dp)
                        ) {
                            Text(
                                text = "¡Explorá San Carlos directamente desde el mapa!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = SancarlinaOnSurface,
                                modifier = Modifier.padding(12.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(Color.Transparent)
                                .border(2.dp, SancarlinaSecondaryContainer, CircleShape)
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Button(
                            onClick = {
                                prefs.edit().putBoolean("seen_coachmarks", true).apply()
                                showCoachmarks = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SancarlinaSecondary),
                            shape = RoundedCornerShape(percent = 50),
                            modifier = Modifier.width(180.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text("Entendido", style = MaterialTheme.typography.titleMedium, color = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DrawerItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        label = { Text(label, style = MaterialTheme.typography.bodyMedium) },
        selected = selected,
        onClick = onClick,
        icon = { Icon(icon, contentDescription = null) },
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f),
            selectedTextColor = MaterialTheme.colorScheme.secondary,
            selectedIconColor = MaterialTheme.colorScheme.secondary,
            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.padding(vertical = 2.dp)
    )
}

@Composable
fun GondolappBottomBar(navController: NavHostController, currentDestination: String?) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = SancarlinaBottomBarShape,
        tonalElevation = 3.dp,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .padding(horizontal = 4.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            bottomNavItems.forEach { screen ->
                val isSelected = currentDestination == screen.route
                if (screen == Screen.Map) {
                    // Botón de Mapa Resaltado (FAB Docked)
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.secondary 
                                else MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                            )
                            .clickable {
                                if (!isSelected) {
                                    navController.navigate(screen.route) {
                                        popUpTo(Screen.Home.route) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = screen.icon ?: Icons.Default.Map,
                            contentDescription = screen.title,
                            tint = if (isSelected) Color.White else MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                } else {
                    val contentColor = if (isSelected) {
                        MaterialTheme.colorScheme.secondary
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
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                screen.icon ?: Icons.Default.Circle,
                                screen.title,
                                tint = contentColor,
                                modifier = Modifier.size(22.dp)
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
}

package com.sancarlina.app.ui.components

import androidx.compose.material3.MaterialTheme

import androidx.compose.foundation.Image
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Stars
import androidx.compose.material.icons.outlined.Star
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.testTag
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.sancarlina.app.R
import com.sancarlina.app.SancarlinaApp
import com.sancarlina.app.navigation.SancarlinaNavGraph
import com.sancarlina.app.navigation.Screen
import com.sancarlina.app.navigation.bottomNavItems
import com.sancarlina.app.ui.theme.*
import com.sancarlina.app.utils.ConnectivityObserver
import com.sancarlina.app.utils.NetworkConnectivityObserver
import com.sancarlina.app.utils.PrefsManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    initialRoute: String? = null,
    onInitialRouteConsumed: () -> Unit = {}
) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val app = remember(context) { context.applicationContext as? SancarlinaApp }
    val prefsManager = remember(context) { PrefsManager(context.applicationContext) }
    val showOnboardingOnStart = remember(prefsManager) {
        !prefsManager.isOnboardingCompleted()
    }
    val auth = remember(app) { app?.container?.auth }
    var currentUserEmail by remember { mutableStateOf<String?>(null) }
    var currentUserPhotoUrl by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(auth) {
        currentUserEmail = auth?.currentUser?.email
        currentUserPhotoUrl = auth?.currentUser?.photoUrl?.toString()
        auth?.addAuthStateListener { firebaseAuth ->
            currentUserEmail = firebaseAuth.currentUser?.email
            currentUserPhotoUrl = firebaseAuth.currentUser?.photoUrl?.toString()
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination?.route
    val isMainView = bottomNavItems.any { it.route == currentDestination } || currentDestination == Screen.Search.route
    val isHomeDestination = currentDestination == Screen.Home.route
    var isOffline by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }

    LaunchedEffect(currentDestination, app) {
        currentDestination?.let { app?.container?.analytics?.logScreen(it) }
    }

    var initialRouteHandled by remember(initialRoute) { mutableStateOf(false) }
    LaunchedEffect(initialRoute, currentDestination) {
        if (
            initialRoute == null ||
            initialRouteHandled ||
            currentDestination == null ||
            currentDestination == Screen.SplashScreen.route ||
            currentDestination == Screen.Onboarding.route
        ) {
            return@LaunchedEffect
        }
        val allowedRoutes = setOf(
            Screen.Notifications.route,
            Screen.Points.route,
            Screen.Turismo.route,
            Screen.Map.route,
            Screen.Home.route,
            Screen.Profile.route
        )
        initialRoute?.takeIf(allowedRoutes::contains)?.let { route ->
            navController.navigate(route) { launchSingleTop = true }
        }
        if (initialRoute != null) {
            initialRouteHandled = true
            onInitialRouteConsumed()
        }
    }

    LaunchedEffect(currentDestination) {
        if (!isHomeDestination && drawerState.isOpen) {
            drawerState.close()
        }
    }

    // Observador de red: una instancia por composición (no recrear en cada recomposición)
    val connectivityObserver = remember { NetworkConnectivityObserver(context) }
    // Navegación automática a Offline / restauración al recuperar conexión
    LaunchedEffect(connectivityObserver) {
        connectivityObserver.observe().collect { status ->
            isOffline = status == ConnectivityObserver.Status.Lost ||
                status == ConnectivityObserver.Status.Unavailable
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = isHomeDestination,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.widthIn(max = 320.dp),
                drawerContainerColor = MaterialTheme.colorScheme.surface,
                drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)
            ) {
                // Header del menú lateral
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
                            )
                        )
                        .padding(top = 56.dp, bottom = 32.dp, start = 24.dp, end = 24.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White,
                            modifier = Modifier.size(72.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_gondolapp_symbol),
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxSize()
                            )
                        }
                        IconButton(onClick = { scope.launch { drawerState.close() } }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cerrar menú",
                                tint = Color.White
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        stringResource(R.string.app_name),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currentUserEmail ?: "Modo Invitado",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f),
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
                        text = "EXPLORAR",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.padding(start = 24.dp, top = 8.dp, bottom = 8.dp),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = androidx.compose.ui.unit.TextUnit.Unspecified
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
                    
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                    
                    Text(
                        text = "COMUNIDAD Y SERVICIOS",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.padding(start = 24.dp, bottom = 8.dp),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = androidx.compose.ui.unit.TextUnit.Unspecified
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

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))

                    Text(
                        text = "APARIENCIA",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = 24.dp, bottom = 8.dp),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    val themeController = LocalThemeController.current
                    val currentThemeName = when (themeController.currentTheme) {
                        AppTheme.LIGHT -> "Claro"
                        AppTheme.DARK -> "Oscuro"
                        AppTheme.SYSTEM -> "Sistema"
                    }
                    DrawerItem(
                        label = "Tema ($currentThemeName)",
                        icon = if (themeController.isDark) Icons.Default.DarkMode else Icons.Default.LightMode,
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            showThemeDialog = true
                        }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))

                    Text(
                        text = "INFORMACIÓN",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = 24.dp, bottom = 8.dp),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    DrawerItem(
                        label = "Ayuda y Soporte",
                        icon = Icons.AutoMirrored.Filled.Help,
                        selected = currentDestination == Screen.Support.route,
                        onClick = { scope.launch { drawerState.close() }; navController.navigate(Screen.Support.route) }
                    )
                    DrawerItem(
                        label = "Cómo usar GondolApp",
                        icon = Icons.Default.School,
                        selected = currentDestination == Screen.Onboarding.route,
                        onClick = { scope.launch { drawerState.close() }; navController.navigate(Screen.Onboarding.route) }
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
                    
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))

                    if (currentUserEmail != null) {
                        DrawerItem(
                            label = "Cerrar Sesión",
                            icon = Icons.AutoMirrored.Filled.ExitToApp,
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
                            icon = Icons.AutoMirrored.Filled.Login,
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
        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                containerColor = MaterialTheme.colorScheme.surface,
                topBar = {
                    if (isMainView && currentDestination != Screen.Map.route && currentDestination != Screen.Search.route) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.surfaceContainerLow
                        ) {
                            CenterAlignedTopAppBar(
                                title = {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_sancarlina_logo),
                                        contentDescription = stringResource(R.string.app_name),
                                        modifier = Modifier.height(32.dp)
                                    )
                                },
                                navigationIcon = {
                                    IconButton(
                                        onClick = {
                                            if (isHomeDestination) {
                                                scope.launch { drawerState.open() }
                                            } else if (currentDestination != Screen.Profile.route) {
                                                navController.navigate(Screen.Profile.route) { launchSingleTop = true }
                                            }
                                        }
                                    ) {
                                        Surface(
                                            modifier = Modifier.size(42.dp),
                                            shape = CircleShape,
                                            color = MaterialTheme.colorScheme.primaryContainer,
                                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                                        ) {
                                            if (!currentUserPhotoUrl.isNullOrBlank()) {
                                                AsyncImage(
                                                    model = currentUserPhotoUrl,
                                                    contentDescription = if (isHomeDestination) stringResource(R.string.cd_menu) else "Ir al perfil",
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                            } else {
                                                Icon(
                                                    Icons.Outlined.Person,
                                                    if (isHomeDestination) stringResource(R.string.cd_menu) else "Ir al perfil",
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.padding(9.dp)
                                                )
                                            }
                                        }
                                    }
                                },
                                actions = {
                                    IconButton(onClick = { navController.navigate(Screen.Notifications.route) }) {
                                        Icon(
                                            Icons.Default.NotificationsNone,
                                            "Notificaciones",
                                            tint = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.size(27.dp)
                                        )
                                    }
                                },
                                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
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
                        showOnboardingOnStart = showOnboardingOnStart,
                        onOnboardingFinished = {
                            prefsManager.setOnboardingCompleted(true)
                        },
                        onOpenDrawer = {
                            if (isHomeDestination) {
                                scope.launch { drawerState.open() }
                            }
                        }
                    )
                }
            }

            androidx.compose.animation.AnimatedVisibility(
                visible = isOffline,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(top = if (isMainView) 62.dp else 8.dp, start = 16.dp, end = 16.dp),
                enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.slideInVertically { -it },
                exit = androidx.compose.animation.fadeOut() + androidx.compose.animation.slideOutVertically { -it }
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.onSurface,
                    contentColor = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(18.dp),
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.CloudOff, contentDescription = null, modifier = Modifier.size(19.dp))
                        Text(
                            text = "Sin conexión · mostrando información guardada",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            if (showThemeDialog) {
                ThemeSelectionDialog(
                    onDismissRequest = { showThemeDialog = false }
                )
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
        label = { 
            Text(
                label, 
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            ) 
        },
        selected = selected,
        onClick = onClick,
        icon = { Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp)) },
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
            selectedTextColor = MaterialTheme.colorScheme.primary,
            selectedIconColor = MaterialTheme.colorScheme.primary,
            unselectedTextColor = MaterialTheme.colorScheme.onSurface,
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        shape = RoundedCornerShape(percent = 50),
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

@Composable
fun GondolappBottomBar(navController: NavHostController, currentDestination: String?) {
    val isMapSelected = currentDestination == Screen.Map.route
    val mapScale by animateFloatAsState(
        targetValue = if (isMapSelected) 1.08f else 1.0f,
        label = "map_scale"
    )
    val mapIndicatorWidth by animateDpAsState(
        targetValue = if (isMapSelected) 22.dp else 0.dp,
        label = "map_indicator_width"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("gondolapp_bottom_bar"),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Surface for the navigation bar anchored at bottom, with 12.dp top padding for headroom
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            color = MaterialTheme.colorScheme.surfaceContainerLowest,
            shape = SancarlinaBottomBarShape,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
            tonalElevation = 3.dp,
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .height(66.dp)
                    .padding(horizontal = 6.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                bottomNavItems.forEach { screen ->
                    if (screen == Screen.Map) {
                        // Empty slot reserving space for the elevated center Map button
                        Spacer(modifier = Modifier.weight(1.1f))
                    } else {
                        val isSelected = currentDestination == screen.route ||
                            (screen == Screen.Home && currentDestination == Screen.Search.route)

                        val tabScale by animateFloatAsState(
                            targetValue = if (isSelected) 1.05f else 1.0f,
                            label = "tab_scale"
                        )
                        val contentColor by animateColorAsState(
                            targetValue = if (isSelected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.82f)
                            },
                            label = "tab_color"
                        )
                        val indicatorWidth by animateDpAsState(
                            targetValue = if (isSelected) 22.dp else 0.dp,
                            label = "tab_indicator_width"
                        )

                        val resolvedIcon: ImageVector = when (screen) {
                            Screen.Home -> if (isSelected) Icons.Filled.Home else Icons.Outlined.Home
                            Screen.Turismo -> if (isSelected) Icons.Filled.Explore else Icons.Outlined.Explore
                            Screen.Points -> if (isSelected) Icons.Filled.Stars else Icons.Outlined.Stars
                            Screen.Profile -> if (isSelected) Icons.Filled.Person else Icons.Outlined.Person
                            else -> screen.icon ?: Icons.Default.Circle
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    if (!isSelected) {
                                        navController.navigate(screen.route) {
                                            popUpTo(Screen.Home.route) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                }
                                .testTag("nav_tab_${screen.route}")
                                .padding(vertical = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = resolvedIcon,
                                contentDescription = screen.title,
                                tint = contentColor,
                                modifier = Modifier
                                    .size(25.dp)
                                    .scale(tabScale)
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = screen.title,
                                style = MaterialTheme.typography.labelSmall,
                                color = contentColor,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Box(
                                modifier = Modifier
                                    .width(indicatorWidth)
                                    .height(2.5.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                            )
                        }
                    }
                }
            }
        }

        // Elevated Center Map Button
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 6.dp)
                .offset(y = (-11).dp)
                .clip(RoundedCornerShape(24.dp))
                .clickable {
                    if (!isMapSelected) {
                        navController.navigate(Screen.Map.route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
                .testTag("nav_tab_map")
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = if (isMapSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer,
                contentColor = if (isMapSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                border = BorderStroke(
                    3.dp,
                    MaterialTheme.colorScheme.surfaceContainerLowest
                ),
                shadowElevation = if (isMapSelected) 8.dp else 4.dp,
                modifier = Modifier
                    .size(50.dp)
                    .scale(mapScale)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = Icons.Default.Map,
                        contentDescription = Screen.Map.title,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = Screen.Map.title,
                style = MaterialTheme.typography.labelSmall,
                color = if (isMapSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = if (isMapSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Box(
                modifier = Modifier
                    .width(mapIndicatorWidth)
                    .height(2.5.dp)
                    .clip(CircleShape)
                    .background(if (isMapSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
            )
        }
    }
}

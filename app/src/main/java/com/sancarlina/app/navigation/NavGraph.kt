package com.sancarlina.app.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sancarlina.app.SancarlinaApp
import com.sancarlina.app.viewmodel.*
import com.sancarlina.app.ui.features.auth.ForgotPasswordContent
import com.sancarlina.app.ui.features.auth.LoginContent
import com.sancarlina.app.ui.features.auth.OnboardingContent
import com.sancarlina.app.ui.features.auth.OnboardingDestination
import com.sancarlina.app.ui.features.auth.RegisterContent
import com.sancarlina.app.ui.features.category.CategoryListContent
import com.sancarlina.app.ui.features.common.SuccessContent
import com.sancarlina.app.ui.features.common.OfflineContent
import com.sancarlina.app.ui.features.emprendimiento.EmprendimientoContent
import com.sancarlina.app.ui.features.favorites.FavoritesContent
import com.sancarlina.app.ui.features.profile.ProfileContent
import com.sancarlina.app.ui.features.profile.EditProfileContent
import com.sancarlina.app.ui.features.profile.DiscoveryPreferencesContent
import com.sancarlina.app.ui.features.home.HomeContent
import com.sancarlina.app.ui.features.turismo.TurismoContent
import com.sancarlina.app.ui.features.turismo.TurismoDetailContent
import com.sancarlina.app.ui.features.turismo.ItineraryContent
import com.sancarlina.app.ui.features.map.MapContent
import com.sancarlina.app.ui.features.home.SearchContent
import com.sancarlina.app.ui.features.home.NewsDetailContent
import com.sancarlina.app.ui.features.home.NewsListContent
import com.sancarlina.app.ui.features.legal.LegalContent
import com.sancarlina.app.ui.features.map.CommerceProfileContent
import com.sancarlina.app.ui.features.map.RateCommerceContent
import com.sancarlina.app.ui.features.map.UserReviewsContent
import com.sancarlina.app.ui.features.notifications.NotificationsContent
import com.sancarlina.app.ui.features.notifications.NotificationSettingsContent
import com.sancarlina.app.ui.features.points.BenefitsContent
import com.sancarlina.app.ui.features.points.PointsHistoryContent
import com.sancarlina.app.ui.features.points.QrScannerContent
import com.sancarlina.app.ui.features.product.ProductDetailContent
import com.sancarlina.app.ui.features.servicios.ServiciosSelloContent
import com.sancarlina.app.ui.features.support.SupportContent
import com.sancarlina.app.ui.features.support.InstitutionalInfoContent
import com.sancarlina.app.ui.features.splash.SplashContent
import com.sancarlina.app.ui.features.admin.login.AdminLoginScreen
import com.sancarlina.app.ui.features.admin.home.AdminHomeScreen
import com.sancarlina.app.ui.features.admin.modules.*
import com.sancarlina.app.viewmodel.admin.*
import com.sancarlina.app.ui.features.forms.PublicFormContent
import com.sancarlina.app.ui.features.forms.PublicFormViewModel
import com.sancarlina.app.ui.features.forms.PendingSubmissionsContent
import com.sancarlina.app.ui.features.forms.PendingSubmissionsViewModel
import com.sancarlina.app.ui.features.forms.FieldRegistrationContent
import com.sancarlina.app.ui.features.forms.FieldRegistrationViewModel
import com.sancarlina.app.R
import com.sancarlina.app.utils.BrowserUtils
import com.sancarlina.app.utils.ViewModelFactory

@Composable
fun SancarlinaNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    showOnboardingOnStart: Boolean = false,
    onOnboardingFinished: () -> Unit = {},
    onOpenDrawer: () -> Unit = {}
) {
    LaunchedEffect(Unit) {
        android.util.Log.i("GondolApp", "SancarlinaNavGraph: Composición iniciada.")
    }

    val context = LocalContext.current
    val app = remember(context) { context.applicationContext as? SancarlinaApp }
    
    if (app == null) {
        // Fallback simple si por alguna razón no podemos acceder al Application personalizado
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Error: Application Context is null or invalid")
        }
        return
    }

    val factory = remember(app) { 
        try {
            ViewModelFactory(app.container)
        } catch (e: Exception) {
            android.util.Log.e("GondolApp", "NavGraph: Error accessing app.container", e)
            null
        }
    }

    if (factory == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Error: Dependency container not initialized")
        }
        return
    }

    NavHost(
        navController = navController,
        startDestination = Screen.SplashScreen.route,
        modifier = modifier,
        enterTransition = { fadeIn(animationSpec = tween(durationMillis = 2)) },
        exitTransition = { fadeOut(animationSpec = tween(durationMillis = 2)) },
        popEnterTransition = { fadeIn(animationSpec = tween(durationMillis = 2)) },
        popExitTransition = { fadeOut(animationSpec = tween(durationMillis = 2)) }
    ) {
        composable(Screen.SplashScreen.route) {
            val splashViewModel: SplashViewModel = viewModel(factory = factory)
            val isReady by splashViewModel.isReady.collectAsState()
            var timeoutFinished by remember { mutableStateOf(false) }

            SplashContent(
                onTimeout = {
                    timeoutFinished = true
                }
            )

            // Navegar solo cuando AMBOS están listos: datos pre-cargados y tiempo de splash cumplido
            LaunchedEffect(isReady, timeoutFinished) {
                android.util.Log.d("GondolApp", "SplashState: isReady=$isReady, timeoutFinished=$timeoutFinished")
                if (isReady && timeoutFinished) {
                    val destination = if (showOnboardingOnStart) {
                        Screen.Onboarding.route
                    } else {
                        Screen.Home.route
                    }
                    android.util.Log.i("GondolApp", "Navigating to $destination from Splash")
                    navController.navigate(destination) {
                        popUpTo(Screen.SplashScreen.route) { inclusive = true }
                    }
                }
            }
        }
        composable(Screen.Home.route) {
            val homeViewModel: HomeViewModel = viewModel(factory = factory)
            HomeContent(
                viewModel = homeViewModel,
                onNavigateToDetail = { tenantId ->
                    navController.navigate(Screen.CommerceProfile.createRoute(tenantId))
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
                onOpenDrawer = onOpenDrawer,
                onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                onNavigateToNews = { navController.navigate(Screen.CategoryList.createRoute("Todos")) }
            )
        }
        composable(Screen.BodegasTab.route) {
            val categoryViewModel: com.sancarlina.app.ui.features.category.CategoryListViewModel = viewModel(factory = factory)
            CategoryListContent(
                categoryId = "BODEGAS",
                viewModel = categoryViewModel,
                onBack = { navController.popBackStack() },
                onNavigateToDetail = { tenantId ->
                    navController.navigate(Screen.CommerceProfile.createRoute(tenantId))
                }
            )
        }
        composable(Screen.Search.route) {
            val searchViewModel: com.sancarlina.app.ui.features.home.SearchViewModel = viewModel(factory = factory)
            SearchContent(
                viewModel = searchViewModel,
                onBack = { navController.popBackStack() },
                onNavigateToProduct = { productId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId))
                },
                onNavigateToCommerce = { commerceId ->
                    navController.navigate(Screen.CommerceProfile.createRoute(commerceId))
                }
            )
        }
        composable(Screen.CategoryList.route) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
            val categoryViewModel: com.sancarlina.app.ui.features.category.CategoryListViewModel = viewModel(factory = factory)
            CategoryListContent(
                categoryId = categoryId,
                viewModel = categoryViewModel,
                onBack = { navController.popBackStack() },
                onNavigateToDetail = { tenantId ->
                    navController.navigate(Screen.CommerceProfile.createRoute(tenantId))
                }
            )
        }
        composable(Screen.Turismo.route) {
            val turismoViewModel: TurismoViewModel = viewModel(factory = factory)
            val itineraryViewModel: ItineraryViewModel = viewModel(factory = factory)
            TurismoContent(
                viewModel = turismoViewModel,
                itineraryViewModel = itineraryViewModel,
                onOpenDrawer = onOpenDrawer,
                onNavigateToItinerary = { navController.navigate(Screen.Itinerary.route) },
                onNavigateToPoint = { pointId ->
                    navController.navigate(Screen.TurismoDetail.createRoute(pointId))
                }
            )
        }
        composable(Screen.TurismoDetail.route) { backStackEntry ->
            val pointId = backStackEntry.arguments?.getString("pointId") ?: ""
            val detailViewModel: TurismoDetailViewModel = viewModel(factory = factory)
            TurismoDetailContent(
                pointId = pointId,
                viewModel = detailViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Login.route) {
            val authViewModel: AuthViewModel = viewModel(factory = factory)
            LoginContent(
                viewModel = authViewModel,
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onNavigateToForgotPassword = { navController.navigate(Screen.ForgotPassword.route) },
                onNavigateToAdminLogin = { navController.navigate(Screen.AdminLogin.route) },
                onLoginSuccess = { 
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.ForgotPassword.route) {
            ForgotPasswordContent(onBack = { navController.popBackStack() })
        }
        composable(Screen.Onboarding.route) {
            OnboardingContent(
                onFinish = { destination ->
                    onOnboardingFinished()
                    val targetRoute = when (destination) {
                        OnboardingDestination.Home -> Screen.Home.route
                        OnboardingDestination.Login -> Screen.Login.route
                    }
                    navController.navigate(targetRoute) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Register.route) {
            val authViewModel: AuthViewModel = viewModel(factory = factory)
            RegisterContent(
                viewModel = authViewModel,
                onBack = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Map.route) {
            val mapViewModel: MapViewModel = viewModel(factory = factory)
            MapContent(
                viewModel = mapViewModel,
                onOpenDrawer = onOpenDrawer,
                onNavigateToCommerce = { commerceId ->
                    navController.navigate(Screen.CommerceProfile.createRoute(commerceId))
                }
            )
        }
        composable(Screen.Points.route) {
            val pointsViewModel: PointsViewModel = viewModel(factory = factory)
            BenefitsContent(
                viewModel = pointsViewModel,
                onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                onOpenDrawer = onOpenDrawer,
                onNavigateToScanner = { navController.navigate(Screen.QrScanner.route) }
            )
        }
        composable(Screen.QrScanner.route) {
            QrScannerContent(
                onBack = { navController.popBackStack() },
                onSuccess = { 
                    navController.popBackStack() 
                }
            )
        }
        composable(Screen.Profile.route) {
            val profileViewModel: ProfileViewModel = viewModel(factory = factory)
            ProfileContent(
                viewModel = profileViewModel,
                onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                onOpenDrawer = onOpenDrawer,
                onNavigateToFavorites = { navController.navigate(Screen.Favorites.route) },
                onNavigateToEditProfile = { navController.navigate(Screen.EditProfile.route) },
                onNavigateToEmprendimiento = { navController.navigate(Screen.Emprendimiento.route) },
                onNavigateToNotifications = { navController.navigate(Screen.Notifications.route) },
                onNavigateToSupport = { navController.navigate(Screen.Support.route) },
                onNavigateToHistory = { navController.navigate(Screen.PointsHistory.route) },
                onNavigateToItinerary = { navController.navigate(Screen.Itinerary.route) },
                onNavigateToInterests = { navController.navigate(Screen.DiscoveryPreferences.route) },
                onNavigateToAdminLogin = { navController.navigate(Screen.AdminLogin.route) },
                onNavigateToAdminPanel = { navController.navigate(Screen.AdminHome.route) },
                onNavigateToFieldRegistration = { navController.navigate(Screen.FieldRegistration.route) },
                onNavigateToFormSubmissions = { navController.navigate(Screen.PendingForms.route) }
            )
        }
        composable(Screen.PointsHistory.route) {
            PointsHistoryContent(onBack = { navController.popBackStack() })
        }
        composable(Screen.DiscoveryPreferences.route) {
            DiscoveryPreferencesContent(onBack = { navController.popBackStack() })
        }
        composable(Screen.EditProfile.route) {
            EditProfileContent(
                onBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Emprendimiento.route) {
            EmprendimientoContent(onBack = { 
                navController.navigate(Screen.Success.route) {
                    popUpTo(Screen.Emprendimiento.route) { inclusive = true }
                }
            })
        }
        composable(Screen.Notifications.route) {
            val notificationsViewModel: NotificationsViewModel = viewModel(factory = factory)
            NotificationsContent(
                viewModel = notificationsViewModel,
                onBack = { navController.popBackStack() },
                onNavigateToSettings = { navController.navigate(Screen.NotificationSettings.route) }
            )
        }
        composable(Screen.NotificationSettings.route) {
            NotificationSettingsContent(onBack = { navController.popBackStack() })
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
            val context = LocalContext.current
            val privacyPolicyUrl = stringResource(R.string.privacy_policy_url)
            SupportContent(
                onBack = { navController.popBackStack() },
                onOpenDrawer = onOpenDrawer,
                onNavigateToLegal = { navController.navigate(Screen.Legal.route) },
                onOpenPrivacyPolicy = {
                    BrowserUtils.openCustomTab(context, privacyPolicyUrl)
                },
                onNavigateToInstitutional = { navController.navigate(Screen.InstitutionalInfo.route) }
            )
        }
        composable(Screen.Favorites.route) {
            val favoritesViewModel: FavoritesViewModel = viewModel(factory = factory)
            FavoritesContent(
                viewModel = favoritesViewModel,
                onBack = { navController.popBackStack() },
                onNavigateToDetail = { tenantId ->
                    navController.navigate(Screen.CommerceProfile.createRoute(tenantId))
                }
            )
        }
        composable(Screen.CommerceProfile.route) { backStackEntry ->
            val commerceId = backStackEntry.arguments?.getString("commerceId") ?: ""
            val commerceProfileViewModel: CommerceProfileViewModel = viewModel(factory = factory)
            val itineraryViewModel: ItineraryViewModel = viewModel(factory = factory)
            val itineraryState by itineraryViewModel.uiState.collectAsState()
            
            CommerceProfileContent(
                commerceId = commerceId,
                viewModel = commerceProfileViewModel,
                onBack = { navController.popBackStack() },
                onNavigateToProduct = { productId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId))
                },
                onNavigateToForm = { formId ->
                    navController.navigate(Screen.PublicForm.createRoute(formId))
                },
                onNavigateToReviews = { id ->
                    navController.navigate(Screen.UserReviews.createRoute(id))
                },
                onNavigateToRate = { id ->
                    navController.navigate(Screen.RateCommerce.createRoute(id))
                },
                isInRoute = commerceId in itineraryState.selectedIds,
                onToggleRoute = itineraryViewModel::toggle
            )
        }
        composable(Screen.RateCommerce.route) { backStackEntry ->
            val commerceId = backStackEntry.arguments?.getString("commerceId") ?: ""
            val rateCommerceViewModel: RateCommerceViewModel = viewModel(factory = factory)
            RateCommerceContent(
                commerceId = commerceId,
                viewModel = rateCommerceViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Itinerary.route) {
            val itineraryViewModel: ItineraryViewModel = viewModel(factory = factory)
            ItineraryContent(
                viewModel = itineraryViewModel,
                onBack = { navController.popBackStack() },
                onNavigateToPoint = { id ->
                    navController.navigate(Screen.CommerceProfile.createRoute(id))
                }
            )
        }
        composable(Screen.UserReviews.route) { backStackEntry ->
            val commerceId = backStackEntry.arguments?.getString("commerceId") ?: ""
            val reviewsViewModel: ReviewsViewModel = viewModel(factory = factory)
            UserReviewsContent(
                commerceId = commerceId,
                viewModel = reviewsViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Legal.route) {
            LegalContent(onBack = { navController.popBackStack() })
        }
        composable(Screen.InstitutionalInfo.route) {
            InstitutionalInfoContent(onBack = { navController.popBackStack() })
        }
        composable(Screen.ProductDetail.route) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            ProductDetailContent(
                productId = productId,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.NewsList.route) {
            val newsListViewModel: NewsListViewModel = viewModel(factory = factory)
            NewsListContent(
                viewModel = newsListViewModel,
                onBack = { navController.popBackStack() },
                onNavigateToDetail = { newsId ->
                    navController.navigate(Screen.NewsDetail.createRoute(newsId))
                }
            )
        }
        composable(Screen.NewsDetail.route) { backStackEntry ->
            val newsId = backStackEntry.arguments?.getString("newsId") ?: ""
            val newsDetailViewModel: NewsDetailViewModel = viewModel(factory = factory)
            NewsDetailContent(
                newsId = newsId,
                viewModel = newsDetailViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.ServiciosSello.route) {
            ServiciosSelloContent(onBack = { navController.popBackStack() })
        }
        composable(Screen.PublicForm.route) { backStackEntry ->
            val formId = backStackEntry.arguments?.getString("formId") ?: ""
            val submissionId = backStackEntry.arguments?.getString("submissionId")
            val publicFormViewModel: PublicFormViewModel = viewModel(factory = factory)
            PublicFormContent(
                formId = formId,
                submissionIdToEdit = submissionId,
                viewModel = publicFormViewModel,
                onBack = { navController.popBackStack() },
                onViewPending = {
                    navController.navigate(Screen.PendingForms.route) {
                        popUpTo(Screen.PublicForm.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.PendingForms.route) {
            val pendingViewModel: PendingSubmissionsViewModel = viewModel(factory = factory)
            PendingSubmissionsContent(
                viewModel = pendingViewModel,
                onBack = { navController.popBackStack() },
                onEditSubmission = { formId, submissionId ->
                    navController.navigate(Screen.PublicForm.createRoute(formId, submissionId))
                }
            )
        }
        composable(Screen.FieldRegistration.route) {
            val fieldViewModel: FieldRegistrationViewModel = viewModel(factory = factory)
            FieldRegistrationContent(
                viewModel = fieldViewModel,
                onBack = { navController.popBackStack() },
                onStartForm = { formId -> navController.navigate(Screen.PublicForm.createRoute(formId)) },
                onEditSubmission = { formId, submissionId ->
                    navController.navigate(Screen.PublicForm.createRoute(formId, submissionId))
                },
                onViewAllSubmissions = { navController.navigate(Screen.PendingForms.route) }
            )
        }
        composable(Screen.Offline.route) {
            OfflineContent(onRetry = {
                // Volver atrás; MainScaffold re-dirige a Offline si la red sigue caída
                navController.popBackStack()
            })
        }
        composable(Screen.AdminLogin.route) {
            val adminAuthViewModel: AdminAuthViewModel = viewModel(factory = factory)
            AdminLoginScreen(
                viewModel = adminAuthViewModel,
                onBack = { navController.popBackStack() },
                onLoginSuccess = {
                    navController.navigate(Screen.AdminHome.route) {
                        popUpTo(Screen.AdminLogin.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.AdminHome.route) {
            val adminHomeViewModel: AdminHomeViewModel = viewModel(factory = factory)
            val adminAuthViewModel: AdminAuthViewModel = viewModel(factory = factory)
            AdminHomeScreen(
                viewModel = adminHomeViewModel,
                onNavigateToModule = { route ->
                    navController.navigate(route)
                },
                onLogout = {
                    adminAuthViewModel.logoutAdmin {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.AdminHome.route) { inclusive = true }
                        }
                    }
                }
            )
        }
        composable(Screen.AdminComercios.route) {
            val vm: AdminComerciosViewModel = viewModel(factory = factory)
            AdminComerciosScreen(viewModel = vm, onBack = { navController.popBackStack() })
        }
        composable(Screen.AdminZonas.route) {
            val vm: AdminZonasViewModel = viewModel(factory = factory)
            AdminZonasScreen(viewModel = vm, onBack = { navController.popBackStack() })
        }
        composable(Screen.AdminBeneficios.route) {
            val vm: AdminBeneficiosViewModel = viewModel(factory = factory)
            AdminBeneficiosScreen(viewModel = vm, onBack = { navController.popBackStack() })
        }
        composable(Screen.AdminUsuarios.route) {
            val vm: AdminUsuariosViewModel = viewModel(factory = factory)
            AdminUsuariosScreen(viewModel = vm, onBack = { navController.popBackStack() })
        }
        composable(Screen.AdminFormularios.route) {
            val vm: AdminFormulariosViewModel = viewModel(factory = factory)
            AdminFormulariosScreen(
                viewModel = vm,
                onBack = { navController.popBackStack() },
                onStartRegistration = { navController.navigate(Screen.FieldRegistration.route) },
                onViewPending = { navController.navigate(Screen.PendingForms.route) }
            )
        }
        composable(Screen.AdminNotificaciones.route) {
            val vm: AdminNotificacionesViewModel = viewModel(factory = factory)
            AdminNotificacionesScreen(viewModel = vm, onBack = { navController.popBackStack() })
        }
        composable(Screen.AdminReviews.route) {
            val vm: AdminReviewsViewModel = viewModel(factory = factory)
            AdminReviewsScreen(viewModel = vm, onBack = { navController.popBackStack() })
        }
        composable(Screen.AdminAdministradores.route) {
            val vm: AdminAdministradoresViewModel = viewModel(factory = factory)
            AdminAdministradoresScreen(viewModel = vm, onBack = { navController.popBackStack() })
        }
    }
}

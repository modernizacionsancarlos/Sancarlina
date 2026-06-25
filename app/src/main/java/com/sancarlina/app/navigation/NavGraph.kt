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
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sancarlina.app.SancarlinaApp
import com.sancarlina.app.viewmodel.*
import com.sancarlina.app.ui.features.auth.ForgotPasswordContent
import com.sancarlina.app.ui.features.auth.LoginContent
import com.sancarlina.app.ui.features.auth.OnboardingContent
import com.sancarlina.app.ui.features.auth.RegisterContent
import com.sancarlina.app.ui.features.category.CategoryListContent
import com.sancarlina.app.ui.features.common.SuccessContent
import com.sancarlina.app.ui.features.common.OfflineContent
import com.sancarlina.app.ui.features.emprendimiento.EmprendimientoContent
import com.sancarlina.app.ui.features.favorites.FavoritesContent
import com.sancarlina.app.ui.features.profile.ProfileContent
import com.sancarlina.app.ui.features.profile.EditProfileContent
import com.sancarlina.app.ui.features.home.HomeContent
import com.sancarlina.app.ui.features.turismo.TurismoContent
import com.sancarlina.app.ui.features.turismo.TurismoDetailContent
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
import com.sancarlina.app.R
import com.sancarlina.app.utils.BrowserUtils
import com.sancarlina.app.utils.ViewModelFactory

@Composable
fun SancarlinaNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
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
        enterTransition = { fadeIn(animationSpec = tween(250)) },
        exitTransition = { fadeOut(animationSpec = tween(250)) },
        popEnterTransition = { fadeIn(animationSpec = tween(250)) },
        popExitTransition = { fadeOut(animationSpec = tween(250)) }
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
                    android.util.Log.i("GondolApp", "Navigating to Home from Splash")
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.SplashScreen.route) { inclusive = true }
                    }
                }
            }
        }
        composable(Screen.Home.route) {
            val homeViewModel: HomeViewModel = viewModel(factory = factory)
            HomeContent(
                viewModel = homeViewModel,
                onNavigateToDetail = { productId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId))
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
                onNavigateToNews = { navController.navigate(Screen.NewsList.route) }
            )
        }
        composable(Screen.BodegasTab.route) {
            CategoryListContent(
                categoryId = "BODEGAS",
                onBack = { navController.popBackStack() },
                onNavigateToDetail = { productId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId))
                }
            )
        }
        composable(Screen.Search.route) {
            SearchContent(
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
            CategoryListContent(
                categoryId = categoryId,
                onBack = { navController.popBackStack() },
                onNavigateToDetail = { productId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId))
                }
            )
        }
        composable(Screen.Turismo.route) {
            val turismoViewModel: TurismoViewModel = viewModel(factory = factory)
            TurismoContent(
                viewModel = turismoViewModel,
                onOpenDrawer = onOpenDrawer,
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
                onFinish = {
                    onOnboardingFinished()
                    navController.navigate(Screen.Home.route) {
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
                onNavigateToHistory = { navController.navigate(Screen.PointsHistory.route) }
            )
        }
        composable(Screen.PointsHistory.route) {
            PointsHistoryContent(onBack = { navController.popBackStack() })
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
            NotificationsContent(
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
            FavoritesContent(
                onBack = { navController.popBackStack() },
                onNavigateToDetail = { productId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId))
                }
            )
        }
        composable(Screen.CommerceProfile.route) { backStackEntry ->
            val commerceId = backStackEntry.arguments?.getString("commerceId") ?: ""
            val commerceProfileViewModel: CommerceProfileViewModel = viewModel(factory = factory)
            
            CommerceProfileContent(
                commerceId = commerceId,
                viewModel = commerceProfileViewModel,
                onBack = { navController.popBackStack() },
                onNavigateToProduct = { productId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId))
                },
                onNavigateToReviews = { id ->
                    navController.navigate(Screen.UserReviews.createRoute(id))
                },
                onNavigateToRate = { id ->
                    navController.navigate(Screen.RateCommerce.createRoute(id))
                }
            )
        }
        composable(Screen.RateCommerce.route) { backStackEntry ->
            val commerceId = backStackEntry.arguments?.getString("commerceId") ?: ""
            RateCommerceContent(
                commerceId = commerceId,
                onBack = { navController.popBackStack() }
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
        composable(Screen.Offline.route) {
            OfflineContent(onRetry = {
                // Volver atrás; MainScaffold re-dirige a Offline si la red sigue caída
                navController.popBackStack()
            })
        }
    }
}

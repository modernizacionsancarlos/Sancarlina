package com.sancarlina.app.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sancarlina.app.SancarlinaApp
import com.sancarlina.app.ui.features.auth.AuthViewModel
import com.sancarlina.app.ui.features.auth.ForgotPasswordContent
import com.sancarlina.app.ui.features.auth.LoginContent
import com.sancarlina.app.ui.features.auth.OnboardingContent
import com.sancarlina.app.ui.features.auth.RegisterContent
import com.sancarlina.app.ui.features.category.CategoryListContent
import com.sancarlina.app.ui.features.common.SuccessContent
import com.sancarlina.app.ui.features.emprendimiento.EmprendimientoContent
import com.sancarlina.app.ui.features.favorites.FavoritesContent
import com.sancarlina.app.ui.features.profile.EditProfileContent
import com.sancarlina.app.ui.features.home.HomeContent
import com.sancarlina.app.ui.features.home.HomeViewModel
import com.sancarlina.app.ui.features.home.SearchContent
import com.sancarlina.app.ui.features.home.NewsDetailContent
import com.sancarlina.app.ui.features.legal.LegalContent
import com.sancarlina.app.ui.features.map.CommerceProfileContent
import com.sancarlina.app.ui.features.map.CommerceProfileViewModel
import com.sancarlina.app.ui.features.map.MapContent
import com.sancarlina.app.ui.features.map.MapViewModel
import com.sancarlina.app.ui.features.map.RateCommerceContent
import com.sancarlina.app.ui.features.notifications.NotificationsContent
import com.sancarlina.app.ui.features.notifications.NotificationSettingsContent
import com.sancarlina.app.ui.features.points.PointsContent
import com.sancarlina.app.ui.features.points.PointsViewModel
import com.sancarlina.app.ui.features.points.PointsHistoryContent
import com.sancarlina.app.ui.features.points.QrScannerContent
import com.sancarlina.app.ui.features.product.ProductDetailContent
import com.sancarlina.app.ui.features.profile.ProfileContent
import com.sancarlina.app.ui.features.servicios.ServiciosSelloContent
import com.sancarlina.app.ui.features.splash.SplashScreenContent
import com.sancarlina.app.ui.features.splash.SplashViewModel
import com.sancarlina.app.ui.features.support.SupportContent
import com.sancarlina.app.ui.features.turismo.TurismoContent
import com.sancarlina.app.ui.features.turismo.TurismoViewModel
import com.sancarlina.app.ui.features.updates.UpdatesContent
import com.sancarlina.app.utils.ViewModelFactory

@Composable
fun SancarlinaNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    onOnboardingFinished: () -> Unit = {},
    onOpenDrawer: () -> Unit = {}
) {
    val app = LocalContext.current.applicationContext as SancarlinaApp
    val factory = ViewModelFactory(app.container)

    NavHost(
        navController = navController,
        startDestination = Screen.SplashScreen.route,
        modifier = modifier
    ) {
        composable(Screen.SplashScreen.route) {
            val splashViewModel: SplashViewModel = viewModel(factory = factory)
            val isReady by splashViewModel.isReady.collectAsState()

            SplashScreenContent(
                onTimeout = {
                    if (isReady) {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.SplashScreen.route) { inclusive = true }
                        }
                    }
                }
            )
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
                onNavigateToNews = { navController.navigate(Screen.NewsDetail.route) }
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
                onOpenDrawer = onOpenDrawer
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
                onOpenDrawer = onOpenDrawer
            )
        }
        composable(Screen.Points.route) {
            val pointsViewModel: PointsViewModel = viewModel(factory = factory)
            PointsContent(
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
            ProfileContent(
                onNavigateToUpdates = { navController.navigate(Screen.Updates.route) },
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
            SupportContent(
                onBack = { navController.popBackStack() },
                onOpenDrawer = onOpenDrawer
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
        composable(Screen.Legal.route) {
            LegalContent(onBack = { navController.popBackStack() })
        }
        composable(Screen.ProductDetail.route) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            ProductDetailContent(
                productId = productId,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.NewsDetail.route) {
            NewsDetailContent(
                onBack = { navController.popBackStack() },
                onNavigateToMap = { navController.navigate(Screen.Map.route) }
            )
        }
        composable(Screen.Updates.route) {
            UpdatesContent(onBack = { navController.popBackStack() })
        }
        composable(Screen.ServiciosSello.route) {
            ServiciosSelloContent(onBack = { navController.popBackStack() })
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

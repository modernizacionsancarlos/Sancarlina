package com.sancarlina.app.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sancarlina.app.di.AppContainer
import com.sancarlina.app.viewmodel.*
import com.sancarlina.app.ui.features.category.CategoryListViewModel
import com.sancarlina.app.ui.features.home.SearchViewModel
import com.sancarlina.app.ui.features.points.QrScannerViewModel
import com.sancarlina.app.ui.features.profile.EditProfileViewModel

import com.sancarlina.app.viewmodel.admin.*
import com.sancarlina.app.ui.features.forms.PublicFormViewModel
import com.sancarlina.app.ui.features.forms.PendingSubmissionsViewModel
import com.sancarlina.app.ui.features.forms.FieldRegistrationViewModel

class ViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SplashViewModel::class.java) -> {
                SplashViewModel(container.tenantsRepository, container.areasRepository) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(container.tenantsRepository, container.discoveryPreferencesRepository) as T
            }
            modelClass.isAssignableFrom(MapViewModel::class.java) -> {
                MapViewModel(container.tenantsRepository, container.areasRepository) as T
            }
            modelClass.isAssignableFrom(PointsViewModel::class.java) -> {
                PointsViewModel(container.auth, container.userRepository, container.benefitsRepository) as T
            }
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(container.auth, container.userRepository) as T
            }
            modelClass.isAssignableFrom(TurismoViewModel::class.java) -> {
                TurismoViewModel(container.tenantsRepository, container.areasRepository) as T
            }
            modelClass.isAssignableFrom(ItineraryViewModel::class.java) -> {
                ItineraryViewModel(container.itineraryRepository, container.tenantsRepository) as T
            }
            modelClass.isAssignableFrom(TurismoDetailViewModel::class.java) -> {
                TurismoDetailViewModel(container.tenantsRepository, container.areasRepository) as T
            }
            modelClass.isAssignableFrom(CommerceProfileViewModel::class.java) -> {
                CommerceProfileViewModel(
                    container.auth,
                    container.userRepository,
                    container.tenantsRepository,
                    container.formsRepository,
                    container.engagementRepository
                ) as T
            }
            modelClass.isAssignableFrom(FavoritesViewModel::class.java) -> {
                FavoritesViewModel(container.auth, container.userRepository, container.tenantsRepository, container.areasRepository) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(container.auth, container.userRepository, container.firestore) as T
            }
            modelClass.isAssignableFrom(PointsHistoryViewModel::class.java) -> {
                PointsHistoryViewModel(container.auth, container.userRepository) as T
            }
            modelClass.isAssignableFrom(NotificationsViewModel::class.java) -> {
                NotificationsViewModel(container.auth, container.notificationsRepository) as T
            }
            modelClass.isAssignableFrom(EditProfileViewModel::class.java) -> {
                EditProfileViewModel() as T
            }
            modelClass.isAssignableFrom(CategoryListViewModel::class.java) -> {
                CategoryListViewModel(container.tenantsRepository, container.areasRepository) as T
            }
            modelClass.isAssignableFrom(SearchViewModel::class.java) -> {
                SearchViewModel(container.tenantsRepository) as T
            }
            modelClass.isAssignableFrom(QrScannerViewModel::class.java) -> {
                QrScannerViewModel() as T
            }
            modelClass.isAssignableFrom(ReviewsViewModel::class.java) -> {
                ReviewsViewModel(container.tenantsRepository, container.reviewsRepository) as T
            }
            modelClass.isAssignableFrom(RateCommerceViewModel::class.java) -> {
                RateCommerceViewModel(container.reviewsRepository) as T
            }
            modelClass.isAssignableFrom(NewsListViewModel::class.java) -> {
                NewsListViewModel() as T
            }
            modelClass.isAssignableFrom(NewsDetailViewModel::class.java) -> {
                NewsDetailViewModel() as T
            }
            modelClass.isAssignableFrom(AdminAuthViewModel::class.java) -> {
                AdminAuthViewModel(container.auth, container.firestore, container.adminRepository) as T
            }
            modelClass.isAssignableFrom(AdminHomeViewModel::class.java) -> {
                AdminHomeViewModel(container.adminRepository) as T
            }
            modelClass.isAssignableFrom(PublicFormViewModel::class.java) -> {
                PublicFormViewModel(
                    container.formsRepository,
                    container.offlineSubmissionsRepository,
                    container.addressGeocoder
                ) as T
            }
            modelClass.isAssignableFrom(PendingSubmissionsViewModel::class.java) -> {
                PendingSubmissionsViewModel(container.offlineSubmissionsRepository) as T
            }
            modelClass.isAssignableFrom(FieldRegistrationViewModel::class.java) -> {
                FieldRegistrationViewModel(
                    container.formsRepository,
                    container.offlineSubmissionsRepository,
                    container.userRepository,
                    container.auth
                ) as T
            }
            modelClass.isAssignableFrom(AdminComerciosViewModel::class.java) -> {
                AdminComerciosViewModel(container.adminComerciosRepository) as T
            }
            modelClass.isAssignableFrom(AdminZonasViewModel::class.java) -> {
                AdminZonasViewModel(container.adminZonasRepository) as T
            }
            modelClass.isAssignableFrom(AdminBeneficiosViewModel::class.java) -> {
                AdminBeneficiosViewModel(container.adminBeneficiosRepository) as T
            }
            modelClass.isAssignableFrom(AdminUsuariosViewModel::class.java) -> {
                AdminUsuariosViewModel(container.adminUsuariosRepository) as T
            }
            modelClass.isAssignableFrom(AdminFormulariosViewModel::class.java) -> {
                AdminFormulariosViewModel(container.adminFormulariosRepository) as T
            }
            modelClass.isAssignableFrom(AdminNotificacionesViewModel::class.java) -> {
                AdminNotificacionesViewModel(container.adminNotificacionesRepository) as T
            }
            modelClass.isAssignableFrom(AdminReviewsViewModel::class.java) -> {
                AdminReviewsViewModel(container.adminReviewsRepository) as T
            }
            modelClass.isAssignableFrom(AdminAdministradoresViewModel::class.java) -> {
                AdminAdministradoresViewModel(container.adminAdministradoresRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}

package com.sancarlina.app.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sancarlina.app.di.AppContainer
import com.sancarlina.app.viewmodel.*
import com.sancarlina.app.ui.features.category.CategoryListViewModel
import com.sancarlina.app.ui.features.home.SearchViewModel
import com.sancarlina.app.ui.features.points.QrScannerViewModel
import com.sancarlina.app.ui.features.profile.EditProfileViewModel

class ViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SplashViewModel::class.java) -> {
                SplashViewModel(container.tenantsRepository, container.areasRepository) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(container.tenantsRepository) as T
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
            modelClass.isAssignableFrom(CommerceProfileViewModel::class.java) -> {
                CommerceProfileViewModel(container.tenantsRepository, container.formsRepository) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel() as T
            }
            modelClass.isAssignableFrom(EditProfileViewModel::class.java) -> {
                EditProfileViewModel() as T
            }
            modelClass.isAssignableFrom(CategoryListViewModel::class.java) -> {
                CategoryListViewModel() as T
            }
            modelClass.isAssignableFrom(SearchViewModel::class.java) -> {
                SearchViewModel() as T
            }
            modelClass.isAssignableFrom(QrScannerViewModel::class.java) -> {
                QrScannerViewModel() as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}

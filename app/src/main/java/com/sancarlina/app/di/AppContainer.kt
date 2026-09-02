package com.sancarlina.app.di

import android.content.Context
import com.sancarlina.app.data.repository.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.PersistentCacheSettings
import com.google.firebase.functions.FirebaseFunctions
import com.sancarlina.app.data.remote.FirestoreCollections
import com.sancarlina.app.analytics.AppAnalytics
import com.google.firebase.storage.FirebaseStorage
import com.sancarlina.app.data.local.OfflineFormsStore
import com.sancarlina.app.utils.AndroidAddressGeocoder

/**
 * Contenedor de dependencias para la aplicación.
 * Mantiene instancias únicas de los repositorios para que los ViewModels no tengan que
 * reinicializar todo cada vez que se abren, mejorando drásticamente el rendimiento.
 */
class AppContainer(private val context: Context) {
    val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance().apply {
            firestoreSettings = FirebaseFirestoreSettings.Builder()
                .setLocalCacheSettings(
                    PersistentCacheSettings.newBuilder()
                        .setSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                        .build()
                )
                .build()
        }
    }
    val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    val storage: FirebaseStorage by lazy { FirebaseStorage.getInstance() }
    val functions: FirebaseFunctions by lazy {
        FirebaseFunctions.getInstance(FirestoreCollections.FUNCTIONS_REGION)
    }
    val analytics: AppAnalytics by lazy { AppAnalytics(context) }
    val offlineFormsStore: OfflineFormsStore by lazy { OfflineFormsStore(context) }
    val addressGeocoder: AndroidAddressGeocoder by lazy { AndroidAddressGeocoder(context) }

    val tenantsRepository: TenantsRepository by lazy {
        TenantsRepository(firestore)
    }

    val userRepository: UserRepository by lazy {
        UserRepository(firestore)
    }

    val areasRepository: AreasRepository by lazy {
        AreasRepository(firestore)
    }

    val benefitsRepository: BenefitsRepository by lazy {
        BenefitsRepository(firestore)
    }

    val notificationsRepository: NotificationsRepository by lazy {
        NotificationsRepository(firestore)
    }

    val reviewsRepository: ReviewsRepository by lazy {
        ReviewsRepository(firestore, auth, functions)
    }

    val itineraryRepository: ItineraryRepository by lazy {
        ItineraryRepository(context, firestore, auth)
    }

    val engagementRepository: EngagementRepository by lazy {
        EngagementRepository(functions, analytics)
    }

    val pushPreferencesRepository: PushPreferencesRepository by lazy {
        PushPreferencesRepository(context, firestore, auth)
    }

    val discoveryPreferencesRepository: DiscoveryPreferencesRepository by lazy {
        DiscoveryPreferencesRepository(context, firestore, auth)
    }

    val submissionsRepository: SubmissionsRepository by lazy {
        SubmissionsRepository(firestore, auth)
    }

    val pointsRepository: PointsRepository by lazy {
        PointsRepository()
    }

    val formsRepository: FormsRepository by lazy {
        FormsRepository(firestore, offlineFormsStore)
    }

    val offlineSubmissionsRepository: OfflineSubmissionsRepository by lazy {
        OfflineSubmissionsRepository(
            context = context,
            store = offlineFormsStore,
            firestore = firestore,
            storage = storage,
            auth = auth
        )
    }

    val adminRepository: AdminRepository by lazy {
        AdminRepository(firestore, auth)
    }

    val adminComerciosRepository: AdminComerciosRepository by lazy {
        AdminComerciosRepository(firestore)
    }

    val adminZonasRepository: AdminZonasRepository by lazy {
        AdminZonasRepository(firestore)
    }

    val adminBeneficiosRepository: AdminBeneficiosRepository by lazy {
        AdminBeneficiosRepository(firestore)
    }

    val adminUsuariosRepository: AdminUsuariosRepository by lazy {
        AdminUsuariosRepository(firestore)
    }

    val adminFormulariosRepository: AdminFormulariosRepository by lazy {
        AdminFormulariosRepository(firestore)
    }

    val adminNotificacionesRepository: AdminNotificacionesRepository by lazy {
        AdminNotificacionesRepository(firestore)
    }

    val adminReviewsRepository: AdminReviewsRepository by lazy {
        AdminReviewsRepository(firestore, functions)
    }

    val adminAdministradoresRepository: AdminAdministradoresRepository by lazy {
        AdminAdministradoresRepository(firestore)
    }
}

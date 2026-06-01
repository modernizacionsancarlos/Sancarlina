package com.sancarlina.app.di

import com.sancarlina.app.data.repository.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Contenedor de dependencias para la aplicación.
 * Mantiene instancias únicas de los repositorios para que los ViewModels no tengan que
 * reinicializar todo cada vez que se abren, mejorando drásticamente el rendimiento.
 */
class AppContainer {
    val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

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

    val submissionsRepository: SubmissionsRepository by lazy {
        SubmissionsRepository(firestore, auth)
    }

    val pointsRepository: PointsRepository by lazy {
        PointsRepository()
    }

    val formsRepository: FormsRepository by lazy {
        FormsRepository(firestore)
    }
}

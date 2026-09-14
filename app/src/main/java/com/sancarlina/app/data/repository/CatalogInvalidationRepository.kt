package com.sancarlina.app.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.sancarlina.app.data.cache.CacheDataset
import com.sancarlina.app.data.cache.DataAccessMetrics
import com.sancarlina.app.data.remote.FirestoreCollections
import com.sancarlina.app.utils.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

data class CatalogVersions(
    val tenants: Long = 0L,
    val areas: Long = 0L,
    val benefits: Long = 0L,
    val forms: Long = 0L,
    val notifications: Long = 0L
) {
    fun versionFor(dataset: CacheDataset): Long = when (dataset) {
        CacheDataset.TENANTS -> tenants
        CacheDataset.AREAS -> areas
        CacheDataset.BENEFITS -> benefits
        CacheDataset.FORMS -> forms
        CacheDataset.NOTIFICATIONS -> notifications
    }
}

/** Una única escucha pequeña reemplaza las escuchas completas repetidas del catálogo. */
class CatalogInvalidationRepository(
    private val firestore: FirebaseFirestore
) {
    private val _versions = MutableStateFlow(CatalogVersions())
    val versions: StateFlow<CatalogVersions> = _versions.asStateFlow()

    private val lock = Any()
    private var registration: ListenerRegistration? = null

    fun start() {
        synchronized(lock) {
            if (registration != null) return
            DataAccessMetrics.recordListenerStart(METRICS_LABEL)
            registration = firestore.collection(FirestoreCollections.APP_METADATA)
                .document(FirestoreCollections.PUBLIC_CATALOG_METADATA)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Logger.w("No se pudo escuchar la versión del catálogo; se usará el TTL local")
                        return@addSnapshotListener
                    }
                    if (snapshot == null || !snapshot.exists()) return@addSnapshotListener
                    _versions.value = CatalogVersions(
                        tenants = snapshot.getLong(CacheDataset.TENANTS.versionField) ?: 0L,
                        areas = snapshot.getLong(CacheDataset.AREAS.versionField) ?: 0L,
                        benefits = snapshot.getLong(CacheDataset.BENEFITS.versionField) ?: 0L,
                        forms = snapshot.getLong(CacheDataset.FORMS.versionField) ?: 0L,
                        notifications = snapshot.getLong(CacheDataset.NOTIFICATIONS.versionField) ?: 0L
                    )
                }
        }
    }

    fun stop() {
        synchronized(lock) {
            registration?.remove()
            registration = null
        }
    }

    /** Debe ejecutarse después de una escritura administrativa exitosa. */
    suspend fun notifyChanged(dataset: CacheDataset): Result<Unit> = runCatching {
        firestore.collection(FirestoreCollections.APP_METADATA)
            .document(FirestoreCollections.PUBLIC_CATALOG_METADATA)
            .set(
                mapOf(
                    dataset.versionField to FieldValue.increment(1L),
                    "updatedAt" to FieldValue.serverTimestamp()
                ),
                SetOptions.merge()
            )
            .await()
    }.onFailure {
        Logger.e("No se pudo actualizar la versión de ${dataset.storageKey}; queda activo el TTL", it)
    }.map { Unit }

    private companion object {
        const val METRICS_LABEL = "catalog_versions"
    }
}


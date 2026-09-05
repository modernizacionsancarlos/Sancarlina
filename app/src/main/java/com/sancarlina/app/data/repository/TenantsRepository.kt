package com.sancarlina.app.data.repository

import com.sancarlina.app.data.cache.AppCache
import com.sancarlina.app.data.models.Tenant
import com.sancarlina.app.data.remote.FirestoreCollections
import com.google.firebase.firestore.FirebaseFirestore
import com.sancarlina.app.utils.Logger
import com.sancarlina.app.utils.RateLimiter
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class TenantsRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    suspend fun getActiveTenants(forceRefresh: Boolean = false): List<Tenant> {
        // 1. Si el caché en memoria es válido y no se pide refresco forzado, devolver inmediatamente
        if (!forceRefresh && AppCache.isTenantsCacheValid()) {
            AppCache.getTenants()?.let { return it }
        }

        // 2. Rate limit para prevenir abusos / spam a la base de datos (máx 12 solicitudes por 10 seg)
        if (!RateLimiter.isWindowAllowed("fetch_tenants", 12, 10_000L)) {
            Logger.w("TenantsRepository: Rate limit excedido para fetch_tenants. Usando caché.")
            return AppCache.getTenants().orEmpty()
        }

        return try {
            val snapshot = firestore.collection(FirestoreCollections.TENANTS)
                .whereEqualTo("status", "active")
                .get()
                .await()
            
            val tenants = snapshot.documents.mapNotNull(::mapTenant)
            if (tenants.isNotEmpty()) {
                AppCache.setTenants(tenants)
            }
            Logger.d("Fetched ${tenants.size} active tenants from Firestore (Cache actualizado)")
            tenants
        } catch (e: Exception) {
            Logger.e("Error fetching tenants", e)
            AppCache.getTenants().orEmpty()
        }
    }

    suspend fun getTenantById(tenantId: String): Tenant? {
        if (tenantId.isBlank()) return null

        // 1. Búsqueda instantánea en caché en memoria O(1)
        val cached = AppCache.getTenant(tenantId)
        if (cached != null) return cached

        // 2. Protección de rate limit por comercio individual
        if (!RateLimiter.isActionAllowed("fetch_tenant_$tenantId", 800L)) {
            return cached
        }

        return try {
            val doc = firestore.collection(FirestoreCollections.TENANTS)
                .document(tenantId)
                .get()
                .await()
            if (doc.exists()) mapTenant(doc) else null
        } catch (e: Exception) {
            Logger.e("Error fetching tenant $tenantId", e)
            null
        }
    }

    /** Mantiene Android sincronizado con los mismos cambios que recibe la Web. */
    fun observeActiveTenants(): Flow<List<Tenant>> = callbackFlow {
        // Emitir datos en caché inmediatamente para evitar esperas y spinners vacíos
        AppCache.getTenants()?.takeIf { it.isNotEmpty() }?.let { trySend(it) }

        val listener = firestore.collection(FirestoreCollections.TENANTS)
            .whereEqualTo("status", "active")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Logger.e("Error observing active tenants", error)
                    return@addSnapshotListener
                }
                val tenants = snapshot?.documents?.mapNotNull(::mapTenant).orEmpty()
                if (tenants.isNotEmpty()) {
                    AppCache.setTenants(tenants)
                }
                trySend(tenants)
            }
        awaitClose { listener.remove() }
    }

    fun observeTenant(tenantId: String): Flow<Tenant?> = callbackFlow {
        if (tenantId.isBlank()) {
            trySend(null)
            close()
            return@callbackFlow
        }
        // Emitir inmediatamente del caché si existe
        AppCache.getTenant(tenantId)?.let { trySend(it) }

        val listener = firestore.collection(FirestoreCollections.TENANTS)
            .document(tenantId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Logger.e("Error observing tenant $tenantId", error)
                    trySend(null)
                    return@addSnapshotListener
                }
                trySend(snapshot?.takeIf { it.exists() }?.let(::mapTenant))
            }
        awaitClose { listener.remove() }
    }

    private fun mapTenant(doc: com.google.firebase.firestore.DocumentSnapshot): Tenant? {
        return try {
            Tenant.fromMap(doc.id, doc.data.orEmpty())
        } catch (e: Exception) {
            Logger.e("Error deserializing tenant ${doc.id}", e)
            null
        }
    }
}

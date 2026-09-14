package com.sancarlina.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.sancarlina.app.data.cache.AppCache
import com.sancarlina.app.data.cache.CacheDataset
import com.sancarlina.app.data.cache.CacheMetadataStore
import com.sancarlina.app.data.cache.DataAccessMetrics
import com.sancarlina.app.data.models.Tenant
import com.sancarlina.app.data.remote.FirestoreCollections
import com.sancarlina.app.utils.Logger
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await

class TenantsRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val cacheMetadata: CacheMetadataStore? = null,
    private val invalidation: CatalogInvalidationRepository? = null,
    private val repositoryScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
) {
    private val dataset = CacheDataset.TENANTS
    private val refreshMutex = Mutex()
    private val lastRefreshAttempt = AtomicLong(0L)
    private val started = AtomicBoolean(false)
    private val hasCatalogSnapshot = AtomicBoolean(AppCache.getTenants() != null)
    private val activeTenants = MutableStateFlow(AppCache.getTenants().orEmpty())

    suspend fun getActiveTenants(forceRefresh: Boolean = false): List<Tenant> {
        ensureStarted()
        if (activeTenants.value.isEmpty()) loadFromLocalCache()
        refreshIfNeeded(force = forceRefresh)
        return activeTenants.value
    }

    suspend fun getTenantById(tenantId: String): Tenant? {
        if (tenantId.isBlank()) return null
        activeTenants.value.firstOrNull { it.id == tenantId }?.let { return it }
        AppCache.getTenants()?.firstOrNull { it.id == tenantId }?.let { return it }

        val reference = firestore.collection(FirestoreCollections.TENANTS).document(tenantId)
        return try {
            DataAccessMetrics.recordCacheQuery("tenant_detail")
            val cached = reference.get(Source.CACHE).await()
            if (cached.exists()) return mapTenant(cached)

            val remote = reference.get(Source.SERVER).await()
            DataAccessMetrics.recordServerResult("tenant_detail", if (remote.exists()) 1 else 0)
            remote.takeIf { it.exists() }?.let(::mapTenant)
        } catch (exception: Exception) {
            Logger.e("Error fetching tenant $tenantId", exception)
            null
        }
    }

    fun observeActiveTenants(): Flow<List<Tenant>> {
        ensureStarted()
        return activeTenants
    }

    fun onAppForegrounded() {
        if (started.get()) repositoryScope.launch { refreshIfNeeded() }
    }

    fun observeTenant(tenantId: String): Flow<Tenant?> =
        observeActiveTenants()
            .map { tenants -> tenants.firstOrNull { it.id == tenantId } }
            .distinctUntilChanged()

    private fun ensureStarted() {
        if (!started.compareAndSet(false, true)) return
        repositoryScope.launch {
            loadFromLocalCache()
            refreshIfNeeded()
            invalidation?.versions
                ?.map { it.versionFor(dataset) }
                ?.distinctUntilChanged()
                ?.collect { remoteVersion ->
                    if (remoteVersion > (cacheMetadata?.version(dataset) ?: 0L)) {
                        refreshIfNeeded(force = true, targetVersion = remoteVersion)
                    }
                }
        }
    }

    private suspend fun loadFromLocalCache() {
        runCatching {
            DataAccessMetrics.recordCacheQuery(dataset.storageKey)
            firestore.collection(FirestoreCollections.TENANTS)
                .limit(MAX_CATALOG_TENANTS)
                .get(Source.CACHE)
                .await()
        }.onSuccess { snapshot ->
            if (!snapshot.isEmpty) {
                hasCatalogSnapshot.set(true)
                updateCache(snapshot.documents.mapNotNull(::mapTenant))
            }
        }.onFailure {
            Logger.d("No hay catálogo de comercios persistido todavía")
        }
    }

    private suspend fun refreshIfNeeded(force: Boolean = false, targetVersion: Long? = null) {
        refreshMutex.withLock {
            if (!force && hasCatalogSnapshot.get() && cacheMetadata?.isStale(dataset) == false) return
            val now = System.currentTimeMillis()
            if (!force && now - lastRefreshAttempt.get() < REFRESH_RETRY_BACKOFF_MILLIS) return
            lastRefreshAttempt.set(now)
            runCatching {
                firestore.collection(FirestoreCollections.TENANTS)
                    .limit(MAX_CATALOG_TENANTS)
                    .get(Source.SERVER)
                    .await()
            }.onSuccess { snapshot ->
                hasCatalogSnapshot.set(true)
                updateCache(snapshot.documents.mapNotNull(::mapTenant))
                DataAccessMetrics.recordServerResult(dataset.storageKey, snapshot.size())
                val version = targetVersion
                    ?: invalidation?.versions?.value?.versionFor(dataset)
                    ?: cacheMetadata?.version(dataset)
                    ?: 0L
                cacheMetadata?.markSynced(dataset, version)
            }.onFailure { exception ->
                Logger.e("Error refreshing tenants; se conserva la caché", exception)
            }
        }
    }

    private fun updateCache(tenants: List<Tenant>) {
        val active = tenants.filter(::isTenantActive)
        AppCache.putTenants(active)
        activeTenants.value = active
    }

    private fun isTenantActive(tenant: Tenant): Boolean {
        val status = tenant.status.lowercase().trim()
        if (status.isEmpty()) return true
        return status !in setOf("inactivo", "inactive", "deleted", "disabled", "baja")
    }

    private fun mapTenant(doc: com.google.firebase.firestore.DocumentSnapshot): Tenant? = try {
        Tenant.fromMap(doc.id, doc.data.orEmpty())
    } catch (exception: Exception) {
        Logger.e("Error deserializing tenant ${doc.id}", exception)
        null
    }

    private companion object {
        const val MAX_CATALOG_TENANTS = 500L
        const val REFRESH_RETRY_BACKOFF_MILLIS = 60_000L
    }
}

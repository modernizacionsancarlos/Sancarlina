package com.sancarlina.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.sancarlina.app.data.cache.AppCache
import com.sancarlina.app.data.cache.CacheDataset
import com.sancarlina.app.data.cache.CacheMetadataStore
import com.sancarlina.app.data.cache.DataAccessMetrics
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

data class Benefit(
    val id: String = "",
    val title: String = "",
    val cost: Int = 0,
    val points_cost: Int = 0,
    val description: String = "",
    val cover_url: String = "",
    val industry: String = "",
    val active: Boolean = true
)

class BenefitsRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val cacheMetadata: CacheMetadataStore? = null,
    private val invalidation: CatalogInvalidationRepository? = null,
    private val repositoryScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
) {
    private val dataset = CacheDataset.BENEFITS
    private val refreshMutex = Mutex()
    private val lastRefreshAttempt = AtomicLong(0L)
    private val started = AtomicBoolean(false)
    private val hasCatalogSnapshot = AtomicBoolean(AppCache.getBenefits() != null)
    private val activeBenefits = MutableStateFlow(AppCache.getBenefits().orEmpty())

    suspend fun getActiveBenefits(forceRefresh: Boolean = false): List<Benefit> {
        ensureStarted()
        if (activeBenefits.value.isEmpty()) loadFromLocalCache()
        refreshIfNeeded(force = forceRefresh)
        return activeBenefits.value
    }

    fun observeActiveBenefits(): Flow<List<Benefit>> {
        ensureStarted()
        return activeBenefits
    }

    fun onAppForegrounded() {
        if (started.get()) repositoryScope.launch { refreshIfNeeded() }
    }

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
            firestore.collection(FirestoreCollections.BENEFITS)
                .whereEqualTo("active", true)
                .limit(MAX_ACTIVE_BENEFITS)
                .get(Source.CACHE)
                .await()
        }.onSuccess { snapshot ->
            if (!snapshot.isEmpty) {
                hasCatalogSnapshot.set(true)
                updateCache(snapshot.documents.mapNotNull(::mapBenefit))
            }
        }
    }

    private suspend fun refreshIfNeeded(force: Boolean = false, targetVersion: Long? = null) {
        refreshMutex.withLock {
            if (!force && hasCatalogSnapshot.get() && cacheMetadata?.isStale(dataset) == false) return
            val now = System.currentTimeMillis()
            if (!force && now - lastRefreshAttempt.get() < REFRESH_RETRY_BACKOFF_MILLIS) return
            lastRefreshAttempt.set(now)
            runCatching {
                firestore.collection(FirestoreCollections.BENEFITS)
                    .whereEqualTo("active", true)
                    .limit(MAX_ACTIVE_BENEFITS)
                    .get(Source.SERVER)
                    .await()
            }.onSuccess { snapshot ->
                hasCatalogSnapshot.set(true)
                updateCache(snapshot.documents.mapNotNull(::mapBenefit))
                DataAccessMetrics.recordServerResult(dataset.storageKey, snapshot.size())
                val version = targetVersion
                    ?: invalidation?.versions?.value?.versionFor(dataset)
                    ?: cacheMetadata?.version(dataset)
                    ?: 0L
                cacheMetadata?.markSynced(dataset, version)
            }.onFailure { exception ->
                Logger.e("Error refreshing benefits; se conserva la caché", exception)
            }
        }
    }

    private fun updateCache(benefits: List<Benefit>) {
        AppCache.setBenefits(benefits)
        activeBenefits.value = benefits
    }

    private fun mapBenefit(doc: com.google.firebase.firestore.DocumentSnapshot): Benefit? =
        runCatching { doc.toObject(Benefit::class.java)?.copy(id = doc.id) }
            .onFailure { Logger.e("Error mapping benefit ${doc.id}", it) }
            .getOrNull()

    private companion object {
        const val MAX_ACTIVE_BENEFITS = 100L
        const val REFRESH_RETRY_BACKOFF_MILLIS = 60_000L
    }
}

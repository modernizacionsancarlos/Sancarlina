package com.sancarlina.app.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.Source
import com.sancarlina.app.data.cache.CacheDataset
import com.sancarlina.app.data.cache.CacheMetadataStore
import com.sancarlina.app.data.cache.DataAccessMetrics
import com.sancarlina.app.data.remote.FirestoreCollections
import com.sancarlina.app.utils.Logger
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await

data class AppNotification(
    val id: String,
    val title: String,
    val message: String,
    val target: String,
    val timestamp: Timestamp?
)

class NotificationsRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val cacheMetadata: CacheMetadataStore? = null,
    private val invalidation: CatalogInvalidationRepository? = null,
    private val repositoryScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
) {
    private val dataset = CacheDataset.NOTIFICATIONS
    private val notificationFlows = ConcurrentHashMap<String, Flow<List<AppNotification>>>()
    private val refreshMutexes = ConcurrentHashMap<String, Mutex>()

    fun observeNotifications(uid: String): Flow<List<AppNotification>> {
        if (uid.isBlank()) return flow { emit(emptyList()) }
        return notificationFlows.getOrPut(uid) { createNotificationsFlow(uid) }
    }

    private fun createNotificationsFlow(uid: String): Flow<List<AppNotification>> = flow {
        var current = load(uid, Source.CACHE)
        current?.let { emit(it) }

        current = refreshIfNeeded(uid = uid, fallback = current)
        emit(current)

        invalidation?.versions
            ?.map { it.versionFor(dataset) }
            ?.distinctUntilChanged()
            ?.collect { remoteVersion ->
                if (remoteVersion > (cacheMetadata?.version(dataset, uid) ?: 0L)) {
                    current = refreshIfNeeded(
                        uid = uid,
                        fallback = current,
                        force = true,
                        targetVersion = remoteVersion
                    )
                    emit(current)
                }
            }
    }.distinctUntilChanged().shareIn(
        scope = repositoryScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000L),
        replay = 1
    )

    private suspend fun refreshIfNeeded(
        uid: String,
        fallback: List<AppNotification>?,
        force: Boolean = false,
        targetVersion: Long? = null
    ): List<AppNotification> = refreshMutexes.getOrPut(uid) { Mutex() }.withLock {
        val remoteVersion = targetVersion ?: invalidation?.versions?.value?.versionFor(dataset) ?: 0L
        val localVersion = cacheMetadata?.version(dataset, uid) ?: 0L
        val cacheIsCurrent = cacheMetadata?.isStale(dataset, uid) == false && remoteVersion <= localVersion
        if (!force && fallback != null && cacheIsCurrent) return@withLock fallback

        runCatching { load(uid, Source.SERVER).orEmpty() }
            .onSuccess {
                cacheMetadata?.markSynced(dataset, remoteVersion, uid)
                DataAccessMetrics.recordServerResult(dataset.storageKey, it.size)
            }
            .onFailure { Logger.e("No se pudieron actualizar las notificaciones; se conserva la caché", it) }
            .getOrElse { fallback.orEmpty() }
    }

    private suspend fun load(uid: String, source: Source): List<AppNotification>? = runCatching {
        if (source == Source.CACHE) DataAccessMetrics.recordCacheQuery(dataset.storageKey)
        firestore.collection(FirestoreCollections.NOTIFICATIONS)
            .whereIn("target", listOf("all", uid))
            .limit(MAX_NOTIFICATIONS)
            .get(source)
            .await()
            .toNotifications()
    }.getOrElse { exception ->
        if (source == Source.CACHE) {
            Logger.d("No hay notificaciones persistidas para el usuario")
        } else {
            throw exception
        }
        null
    }

    private fun QuerySnapshot.toNotifications(): List<AppNotification> = documents.mapNotNull { doc ->
        runCatching {
            AppNotification(
                id = doc.id,
                title = doc.getString("title").orEmpty(),
                message = doc.getString("message") ?: doc.getString("body").orEmpty(),
                target = doc.getString("target") ?: "all",
                timestamp = doc.getTimestamp("createdAt")
                    ?: doc.getTimestamp("created_at")
                    ?: doc.getTimestamp("timestamp")
            )
        }.getOrNull()
    }.sortedByDescending { it.timestamp?.seconds ?: 0L }

    private companion object {
        const val MAX_NOTIFICATIONS = 50L
    }
}

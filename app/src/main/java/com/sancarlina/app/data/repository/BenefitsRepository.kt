package com.sancarlina.app.data.repository

import com.sancarlina.app.data.cache.AppCache
import com.sancarlina.app.data.remote.FirestoreCollections
import com.google.firebase.firestore.FirebaseFirestore
import com.sancarlina.app.utils.RateLimiter
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    suspend fun getActiveBenefits(forceRefresh: Boolean = false): List<Benefit> {
        if (!forceRefresh && AppCache.isBenefitsCacheValid()) {
            AppCache.getBenefits()?.let { return it }
        }

        if (!RateLimiter.isWindowAllowed("fetch_benefits", 12, 10_000L)) {
            return AppCache.getBenefits().orEmpty()
        }

        return try {
            val snapshot = firestore.collection(FirestoreCollections.BENEFITS)
                .whereEqualTo("active", true)
                .get()
                .await()
            
            val benefits = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Benefit::class.java)?.copy(id = doc.id)
            }
            if (benefits.isNotEmpty()) {
                AppCache.setBenefits(benefits)
            }
            benefits
        } catch (e: Exception) {
            AppCache.getBenefits().orEmpty()
        }
    }

    fun observeActiveBenefits(): Flow<List<Benefit>> = callbackFlow {
        // Emitir inmediatamente del caché
        AppCache.getBenefits()?.takeIf { it.isNotEmpty() }?.let { trySend(it) }

        val listener = firestore.collection(FirestoreCollections.BENEFITS)
            .whereEqualTo("active", true)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                val benefits = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        doc.toObject(Benefit::class.java)?.copy(id = doc.id)
                    } catch (_: Exception) {
                        null
                    }
                }.orEmpty()
                if (benefits.isNotEmpty()) {
                    AppCache.setBenefits(benefits)
                }
                trySend(benefits)
            }
        awaitClose { listener.remove() }
    }
}

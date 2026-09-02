package com.sancarlina.app.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.sancarlina.app.data.remote.FirestoreCollections
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

data class ReviewRecord(
    val id: String,
    val tenantId: String,
    val userId: String,
    val userName: String,
    val userAvatarUrl: String,
    val rating: Int,
    val comment: String,
    val verifiedVisit: Boolean,
    val createdAt: Timestamp?
)

class ReviewsRepository(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val functions: FirebaseFunctions
) {
    fun observeApprovedReviews(tenantId: String): Flow<List<ReviewRecord>> = callbackFlow {
        if (tenantId.isBlank()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val registration = firestore.collection(FirestoreCollections.REVIEWS)
            .whereEqualTo("tenantId", tenantId)
            .whereEqualTo("status", "approved")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val reviews = snapshot?.documents.orEmpty().mapNotNull { document ->
                    val rating = document.getLong("rating")?.toInt() ?: return@mapNotNull null
                    ReviewRecord(
                        id = document.id,
                        tenantId = document.getString("tenantId").orEmpty(),
                        userId = document.getString("userId").orEmpty(),
                        userName = document.getString("userName").orEmpty().ifBlank { "Visitante" },
                        userAvatarUrl = document.getString("userAvatarUrl").orEmpty(),
                        rating = rating.coerceIn(1, 5),
                        comment = document.getString("comment").orEmpty(),
                        verifiedVisit = document.getBoolean("verifiedVisit") == true,
                        createdAt = document.getTimestamp("createdAt")
                    )
                }.sortedByDescending { it.createdAt?.seconds ?: 0L }
                trySend(reviews)
            }
        awaitClose { registration.remove() }
    }

    suspend fun submitReview(tenantId: String, rating: Int, comment: String) {
        checkNotNull(auth.currentUser) { "Iniciá sesión para publicar una reseña." }
        val payload = mapOf(
            "tenantId" to tenantId,
            "rating" to rating,
            "comment" to comment.trim()
        )
        functions.getHttpsCallable("submitReview").call(payload).await()
    }
}

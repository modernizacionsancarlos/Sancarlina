package com.sancarlina.app.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.sancarlina.app.data.remote.FirestoreCollections
import kotlinx.coroutines.tasks.await

data class AdminReview(
    val id: String,
    val tenantName: String,
    val userName: String,
    val rating: Int,
    val comment: String,
    val status: String,
    val verifiedVisit: Boolean,
    val createdAt: Timestamp?
)

class AdminReviewsRepository(
    private val firestore: FirebaseFirestore,
    private val functions: FirebaseFunctions
) {
    suspend fun getReviews(): List<AdminReview> {
        return firestore.collection(FirestoreCollections.REVIEWS)
            .get()
            .await()
            .documents
            .mapNotNull { document ->
                val rating = document.getLong("rating")?.toInt() ?: return@mapNotNull null
                AdminReview(
                    id = document.id,
                    tenantName = document.getString("tenantName").orEmpty().ifBlank { "Comercio" },
                    userName = document.getString("userName").orEmpty().ifBlank { "Visitante" },
                    rating = rating,
                    comment = document.getString("comment").orEmpty(),
                    status = document.getString("status").orEmpty().ifBlank { "pending" },
                    verifiedVisit = document.getBoolean("verifiedVisit") == true,
                    createdAt = document.getTimestamp("createdAt")
                )
            }
            .sortedWith(
                compareBy<AdminReview> { it.status != "pending" }
                    .thenByDescending { it.createdAt?.seconds ?: 0L }
            )
    }

    suspend fun moderate(reviewId: String, status: String) {
        functions.getHttpsCallable("moderateReview")
            .call(mapOf("reviewId" to reviewId, "status" to status))
            .await()
    }
}

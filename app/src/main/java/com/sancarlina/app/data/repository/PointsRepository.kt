package com.sancarlina.app.data.repository

import com.sancarlina.app.data.remote.FirestoreCollections
import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.tasks.await

class PointsRepository(
    private val functions: FirebaseFunctions = FirebaseFunctions.getInstance(FirestoreCollections.FUNCTIONS_REGION)
) {
    suspend fun awardPoints(points: Int, reason: String, tenantId: String, tenantName: String): Result<Unit> {
        val data = hashMapOf(
            "points" to points,
            "reason" to reason,
            "tenantId" to tenantId,
            "tenantName" to tenantName
        )

        return try {
            functions
                .getHttpsCallable("awardPoints")
                .call(data)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

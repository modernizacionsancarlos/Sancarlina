package com.sancarlina.app.data.repository

import com.sancarlina.app.data.remote.FirestoreCollections
import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.tasks.await
import com.sancarlina.app.utils.RateLimiter

class PointsRepository(
    private val functions: FirebaseFunctions = FirebaseFunctions.getInstance(FirestoreCollections.FUNCTIONS_REGION)
) {
    suspend fun awardPoints(points: Int, reason: String, tenantId: String, tenantName: String): Result<Unit> {
        if (!RateLimiter.isActionAllowed("award_points:$tenantId", 5_000L)) {
            return Result.failure(IllegalStateException("Esperá unos segundos antes de volver a acreditar puntos."))
        }
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

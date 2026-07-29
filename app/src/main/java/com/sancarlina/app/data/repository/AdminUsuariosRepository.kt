package com.sancarlina.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.sancarlina.app.data.remote.AdminFunctionsService
import com.sancarlina.app.data.remote.FirestoreCollections
import kotlinx.coroutines.tasks.await

data class UserProfileAdmin(
    val uid: String = "",
    val email: String = "",
    val user_name: String = "",
    val phone: String = "",
    val role: String = "citizen",
    val points: Int = 0,
    val points_balance: Int = 0,
    val status: String = "active"
)

class AdminUsuariosRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val functionsService: AdminFunctionsService = AdminFunctionsService()
) {

    suspend fun getAllUsers(): Result<List<UserProfileAdmin>> {
        return try {
            val snapshot = firestore.collection(FirestoreCollections.USER_PROFILES)
                .get()
                .await()

            val users = snapshot.documents.mapNotNull { doc ->
                try {
                    doc.toObject(UserProfileAdmin::class.java)?.copy(uid = doc.id)
                } catch (e: Exception) {
                    null
                }
            }
            Result.success(users)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUserPoints(uid: String, newPoints: Int): Result<Unit> {
        return try {
            firestore.collection(FirestoreCollections.USER_PROFILES)
                .document(uid)
                .update(
                    mapOf(
                        "points" to newPoints,
                        "points_balance" to newPoints
                    )
                )
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resetPassword(uid: String, newPassword: String): Result<Map<String, Any>> {
        return functionsService.resetPassword(uid, newPassword)
    }

    suspend fun setSuperAdmin(uid: String, email: String, active: Boolean): Result<Map<String, Any>> {
        return functionsService.setSuperAdmin(uid, email, active)
    }
}

package com.example.sancarlina.data.repository

import com.example.sancarlina.data.remote.FirestoreCollections
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    suspend fun syncUserProfile(uid: String, email: String?, name: String? = null) {
        val profileData = mutableMapOf<String, Any>(
            "uid" to uid,
            "role" to "citizen",
            "tenantId" to FirestoreCollections.DEFAULT_TENANT_ID,
            "status" to "active"
        )
        
        email?.let { profileData["email"] = it }
        name?.let { profileData["user_name"] = it }

        val userDoc = firestore.collection(FirestoreCollections.USER_PROFILES).document(uid)
        val docSnapshot = userDoc.get().await()
        
        if (!docSnapshot.exists()) {
            profileData["points"] = 0
            profileData["points_balance"] = 0
            profileData["favoriteTenantIds"] = emptyList<String>()
        }

        userDoc.set(profileData, SetOptions.merge()).await()
    }

    suspend fun getUserBalance(uid: String): Int {
        return try {
            val doc = firestore.collection(FirestoreCollections.USER_PROFILES).document(uid).get().await()
            // Try points_balance first, then points
            (doc.getLong("points_balance") ?: doc.getLong("points") ?: 0L).toInt()
        } catch (e: Exception) {
            0
        }
    }
}

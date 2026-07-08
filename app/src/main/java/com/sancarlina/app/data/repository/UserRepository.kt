package com.sancarlina.app.data.repository

import com.sancarlina.app.data.remote.FirestoreCollections
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.sancarlina.app.data.models.PointMovement
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date

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
            (doc.getLong("points_balance") ?: doc.getLong("points") ?: 0L).toInt()
        } catch (e: Exception) {
            0
        }
    }

    suspend fun updateUserBalance(uid: String, newBalance: Int) {
        val userDoc = firestore.collection(FirestoreCollections.USER_PROFILES).document(uid)
        userDoc.update(
            mapOf(
                "points_balance" to newBalance,
                "points" to newBalance
            )
        ).await()
    }

    @Suppress("UNCHECKED_CAST")
    suspend fun getFavoriteTenantIds(uid: String): List<String> {
        return try {
            val doc = firestore.collection(FirestoreCollections.USER_PROFILES).document(uid).get().await()
            doc.get("favoriteTenantIds") as? List<String> ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun toggleFavoriteTenant(uid: String, tenantId: String): Boolean {
        val userDoc = firestore.collection(FirestoreCollections.USER_PROFILES).document(uid)
        val currentFavs = getFavoriteTenantIds(uid).toMutableList()
        val isNowFavorite = if (currentFavs.contains(tenantId)) {
            currentFavs.remove(tenantId)
            false
        } else {
            currentFavs.add(tenantId)
            true
        }
        userDoc.update("favoriteTenantIds", currentFavs).await()
        return isNowFavorite
    }

    suspend fun addPointMovement(uid: String, title: String, amount: Int, isEarned: Boolean) {
        val historyCollection = firestore.collection(FirestoreCollections.USER_PROFILES)
            .document(uid)
            .collection("points_history")
        
        val dateString = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
            
        val movementData = mapOf(
            "title" to title,
            "amount" to amount,
            "isEarned" to isEarned,
            "date" to dateString,
            "timestamp" to FieldValue.serverTimestamp()
        )
        historyCollection.add(movementData).await()
    }

    @Suppress("UNCHECKED_CAST")
    suspend fun getPointMovements(uid: String): List<PointMovement> {
        return try {
            val snapshot = firestore.collection(FirestoreCollections.USER_PROFILES)
                .document(uid)
                .collection("points_history")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()
                
            snapshot.documents.mapNotNull { doc ->
                try {
                    PointMovement(
                        id = doc.id,
                        title = doc.getString("title") ?: "",
                        date = doc.getString("date") ?: "",
                        amount = doc.getLong("amount")?.toInt() ?: 0,
                        isEarned = doc.getBoolean("isEarned") ?: false
                    )
                } catch (e: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}

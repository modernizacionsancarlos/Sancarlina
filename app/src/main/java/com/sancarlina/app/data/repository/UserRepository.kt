package com.sancarlina.app.data.repository

import com.sancarlina.app.data.remote.FirestoreCollections
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Source
import com.sancarlina.app.data.models.PointMovement
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date

data class UserProfileData(
    val userName: String = "Usuario",
    val email: String = "",
    val pointsBalance: Int = 0,
    val profileImageUrl: String? = null,
    val notificationCount: Int = 0,
    val favoriteTenantIds: List<String> = emptyList(),
    val role: String = "citizen",
    val assignedFormIds: List<String> = emptyList()
)

data class RegistrationAccess(
    val role: String = "citizen",
    val assignedFormIds: List<String> = emptyList()
) {
    val isFieldStaff: Boolean
        get() = role.lowercase() in setOf("admin", "registrar", "registrador", "field_registrar", "staff")
}

class UserRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    suspend fun syncUserProfile(uid: String, email: String?, name: String? = null) {
        val userDoc = firestore.collection(FirestoreCollections.USER_PROFILES).document(uid)
        val docSnapshot = userDoc.get().await()
        val profileData = mutableMapOf<String, Any>()
        email?.let { profileData["email"] = it }
        name?.let { profileData["user_name"] = it }
        profileData["updatedAt"] = FieldValue.serverTimestamp()

        if (!docSnapshot.exists()) {
            profileData["uid"] = uid
            profileData["role"] = "citizen"
            profileData["tenantId"] = FirestoreCollections.DEFAULT_TENANT_ID
            profileData["tenant_id"] = FirestoreCollections.DEFAULT_TENANT_ID
            profileData["status"] = "active"
            profileData["createdAt"] = FieldValue.serverTimestamp()
            profileData["points"] = 0
            profileData["points_balance"] = 0
            profileData["favoriteTenantIds"] = emptyList<String>()
        }

        userDoc.set(profileData, SetOptions.merge()).await()
    }

    suspend fun getUserBalance(uid: String): Int {
        return try {
            val doc = firestore.collection(FirestoreCollections.USER_PROFILES).document(uid).get().await()
            (doc.getLong("points") ?: doc.getLong("points_balance") ?: 0L).toInt()
        } catch (e: Exception) {
            0
        }
    }

    fun observeUserProfile(uid: String): Flow<UserProfileData?> = callbackFlow {
        if (uid.isBlank()) {
            trySend(null)
            close()
            return@callbackFlow
        }
        val listener = firestore.collection(FirestoreCollections.USER_PROFILES)
            .document(uid)
            .addSnapshotListener { doc, error ->
                if (error != null) {
                    trySend(null)
                    return@addSnapshotListener
                }
                if (doc == null || !doc.exists()) {
                    trySend(null)
                    return@addSnapshotListener
                }
                @Suppress("UNCHECKED_CAST")
                val favorites = (doc.get("favoriteTenantIds")
                    ?: doc.get("favorite_tenant_ids")
                    ?: doc.get("favorites")) as? List<*>
                val assignedForms = (doc.get("assigned_form_ids")
                    ?: doc.get("assignedFormIds")) as? List<*>
                trySend(
                    UserProfileData(
                        userName = doc.getString("user_name")
                            ?: doc.getString("name")
                            ?: doc.getString("displayName")
                            ?: "Usuario",
                        email = doc.getString("email").orEmpty(),
                        pointsBalance = (doc.getLong("points")
                            ?: doc.getLong("points_balance")
                            ?: 0L).toInt(),
                        profileImageUrl = doc.getString("imageUrl")
                            ?: doc.getString("photo_url")
                            ?: doc.getString("photoUrl"),
                        notificationCount = (doc.getLong("notification_count")
                            ?: doc.getLong("notificationCount")
                            ?: 0L).toInt(),
                        favoriteTenantIds = favorites?.mapNotNull { it?.toString() }.orEmpty(),
                        role = doc.getString("role") ?: "citizen",
                        assignedFormIds = assignedForms?.mapNotNull { it?.toString() }.orEmpty()
                    )
                )
            }
        awaitClose { listener.remove() }
    }

    fun observeUserBalance(uid: String): Flow<Int> =
        observeUserProfile(uid).map { it?.pointsBalance ?: 0 }

    fun observeFavoriteTenantIds(uid: String): Flow<List<String>> =
        observeUserProfile(uid).map { it?.favoriteTenantIds.orEmpty() }

    suspend fun getRegistrationAccess(uid: String, forceServer: Boolean = false): RegistrationAccess {
        if (uid.isBlank()) return RegistrationAccess()
        return try {
            val document = firestore.collection(FirestoreCollections.USER_PROFILES).document(uid)
            val doc = if (forceServer) {
                runCatching { document.get(Source.SERVER).await() }
                    .getOrElse { document.get(Source.CACHE).await() }
            } else {
                document.get().await()
            }
            @Suppress("UNCHECKED_CAST")
            val assigned = (doc.get("assigned_form_ids")
                ?: doc.get("assignedFormIds")) as? List<*>
            RegistrationAccess(
                role = doc.getString("role") ?: "citizen",
                assignedFormIds = assigned?.mapNotNull { it?.toString() }.orEmpty()
            )
        } catch (_: Exception) {
            RegistrationAccess()
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
            val raw = doc.get("favoriteTenantIds")
                ?: doc.get("favorite_tenant_ids")
                ?: doc.get("favorites")
            (raw as? List<*>)?.mapNotNull { it?.toString() }.orEmpty()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun toggleFavoriteTenant(uid: String, tenantId: String): Boolean {
        val userDoc = firestore.collection(FirestoreCollections.USER_PROFILES).document(uid)
        return firestore.runTransaction { transaction ->
            val snapshot = transaction.get(userDoc)
            @Suppress("UNCHECKED_CAST")
            val rawFavorites = snapshot.get("favoriteTenantIds")
                ?: snapshot.get("favorite_tenant_ids")
                ?: snapshot.get("favorites")
            val currentFavs = (rawFavorites as? List<*>)
                ?.mapNotNull { it?.toString() }
                ?.toMutableList()
                ?: mutableListOf()
            val isNowFavorite = if (currentFavs.contains(tenantId)) {
                currentFavs.remove(tenantId)
                false
            } else {
                currentFavs.add(tenantId)
                true
            }
            transaction.set(userDoc, mapOf("favoriteTenantIds" to currentFavs), SetOptions.merge())
            isNowFavorite
        }.await()
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

    fun observePointMovements(uid: String): Flow<List<PointMovement>> = callbackFlow {
        val listener = firestore.collection(FirestoreCollections.USER_PROFILES)
            .document(uid)
            .collection("points_history")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                val movements = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        PointMovement(
                            id = doc.id,
                            title = doc.getString("title") ?: "",
                            date = doc.getString("date") ?: "",
                            amount = doc.getLong("amount")?.toInt() ?: 0,
                            isEarned = doc.getBoolean("isEarned")
                                ?: doc.getBoolean("is_earned")
                                ?: ((doc.getLong("amount") ?: 0L) > 0)
                        )
                    } catch (_: Exception) {
                        null
                    }
                }.orEmpty()
                trySend(movements)
            }
        awaitClose { listener.remove() }
    }
}

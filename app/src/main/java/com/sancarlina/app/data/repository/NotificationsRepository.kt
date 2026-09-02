package com.sancarlina.app.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.sancarlina.app.data.remote.FirestoreCollections
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

data class AppNotification(
    val id: String,
    val title: String,
    val message: String,
    val target: String,
    val timestamp: Timestamp?
)

class NotificationsRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    fun observeNotifications(uid: String): Flow<List<AppNotification>> = callbackFlow {
        val listener = firestore.collection(FirestoreCollections.NOTIFICATIONS)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                val notifications = snapshot?.documents?.mapNotNull { doc ->
                    val target = doc.getString("target") ?: "all"
                    if (target != "all" && target != uid) return@mapNotNull null
                    AppNotification(
                        id = doc.id,
                        title = doc.getString("title").orEmpty(),
                        message = doc.getString("message") ?: doc.getString("body").orEmpty(),
                        target = target,
                        timestamp = doc.getTimestamp("createdAt")
                            ?: doc.getTimestamp("created_at")
                            ?: doc.getTimestamp("timestamp")
                    )
                }?.sortedByDescending { it.timestamp?.seconds ?: 0L }.orEmpty()
                trySend(notifications)
            }
        awaitClose { listener.remove() }
    }
}

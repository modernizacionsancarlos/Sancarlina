package com.sancarlina.app.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.sancarlina.app.data.remote.FirestoreCollections
import kotlinx.coroutines.tasks.await

data class NotificationAdmin(
    val id: String = "",
    val title: String = "",
    val message: String = "",
    val type: String = "general",
    val target: String = "all",
    val timestamp: Timestamp? = null
)

class AdminNotificacionesRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    suspend fun getAllNotifications(): Result<List<NotificationAdmin>> {
        return try {
            val snapshot = firestore.collection(FirestoreCollections.NOTIFICATIONS)
                .get()
                .await()

            val list = snapshot.documents.mapNotNull { doc ->
                try {
                    NotificationAdmin(
                        id = doc.id,
                        title = doc.getString("title") ?: "",
                        message = doc.getString("message") ?: doc.getString("body") ?: "",
                        type = doc.getString("type") ?: "general",
                        target = doc.getString("target") ?: "all",
                        timestamp = doc.getTimestamp("timestamp") ?: doc.getTimestamp("created_at")
                    )
                } catch (e: Exception) {
                    null
                }
            }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendNotification(notification: NotificationAdmin): Result<String> {
        return try {
            val docRef = firestore.collection(FirestoreCollections.NOTIFICATIONS).document()
            val data = mapOf(
                "title" to notification.title,
                "message" to notification.message,
                "body" to notification.message,
                "type" to notification.type,
                "target" to notification.target,
                "timestamp" to Timestamp.now(),
                "created_at" to Timestamp.now()
            )
            docRef.set(data).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteNotification(id: String): Result<Unit> {
        return try {
            firestore.collection(FirestoreCollections.NOTIFICATIONS)
                .document(id)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

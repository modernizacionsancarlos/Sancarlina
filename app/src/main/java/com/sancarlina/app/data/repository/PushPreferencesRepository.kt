package com.sancarlina.app.data.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.sancarlina.app.data.remote.FirestoreCollections
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest

data class PushPreferences(
    val points: Boolean = true,
    val news: Boolean = true,
    val offers: Boolean = false,
    val events: Boolean = true
)

class PushPreferencesRepository(
    private val context: Context,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val messaging: FirebaseMessaging = FirebaseMessaging.getInstance()
) {
    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun current(): PushPreferences = PushPreferences(
        points = preferences.getBoolean(KEY_POINTS, true),
        news = preferences.getBoolean(KEY_NEWS, true),
        offers = preferences.getBoolean(KEY_OFFERS, false),
        events = preferences.getBoolean(KEY_EVENTS, true)
    )

    private fun areNotificationsPermitted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
    }

    suspend fun initialize() {
        if (!areNotificationsPermitted()) {
            Log.d(TAG, "Notification permissions not granted, skipping automatic topic sync.")
            return
        }

        runCatching {
            syncTopics(current())
        }.onFailure { e ->
            handleFcmError("initialize.syncTopics", e)
        }

        runCatching {
            val token = messaging.token.await()
            registerToken(token)
        }.onFailure { e ->
            handleFcmError("initialize.token", e)
        }
    }

    suspend fun setEnabled(key: String, enabled: Boolean) {
        preferences.edit().putBoolean(key, enabled).apply()
        val topic = topicForKey(key) ?: return
        val syncedKey = syncedKeyForKey(key)

        runCatching {
            syncSingleTopic(topic, syncedKey, enabled)
        }.onFailure { e ->
            handleFcmError("setEnabled.$topic", e)
        }

        runCatching { registerCurrentToken() }
    }

    suspend fun registerCurrentToken() {
        if (!areNotificationsPermitted()) return
        runCatching {
            val token = messaging.token.await()
            registerToken(token)
        }.onFailure { e ->
            handleFcmError("registerCurrentToken", e)
        }
    }

    suspend fun registerToken(token: String) {
        if (token.isBlank()) return
        val uid = auth.currentUser?.uid ?: return
        runCatching {
            firestore.collection(FirestoreCollections.DEVICE_TOKENS)
                .document(token.sha256())
                .set(
                    mapOf(
                        "token" to token,
                        "userId" to uid,
                        "platform" to "android",
                        "active" to true,
                        "preferences" to mapOf(
                            "points" to current().points,
                            "news" to current().news,
                            "offers" to current().offers,
                            "events" to current().events
                        ),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
        }.onFailure { e ->
            Log.w(TAG, "Could not store device token in Firestore: ${e.message}")
        }
    }

    private suspend fun syncTopics(value: PushPreferences) {
        syncSingleTopic(TOPIC_POINTS, KEY_SYNCED_POINTS, value.points)
        syncSingleTopic(TOPIC_NEWS, KEY_SYNCED_NEWS, value.news)
        syncSingleTopic(TOPIC_OFFERS, KEY_SYNCED_OFFERS, value.offers)
        syncSingleTopic(TOPIC_EVENTS, KEY_SYNCED_EVENTS, value.events)
    }

    private suspend fun syncSingleTopic(topic: String, syncedKey: String?, shouldEnable: Boolean) {
        // Skip duplicate operations if already synchronized to the requested state
        if (syncedKey != null && preferences.contains(syncedKey) && preferences.getBoolean(syncedKey, !shouldEnable) == shouldEnable) {
            return
        }

        runCatching {
            if (shouldEnable) {
                messaging.subscribeToTopic(topic).await()
            } else {
                messaging.unsubscribeFromTopic(topic).await()
            }
            if (syncedKey != null) {
                preferences.edit().putBoolean(syncedKey, shouldEnable).apply()
            }
        }.onFailure { e ->
            handleFcmError("syncSingleTopic.$topic", e)
        }
    }

    private suspend fun handleFcmError(operation: String, e: Throwable) {
        val message = e.message ?: ""
        if (message.contains("TOO_MANY_REGISTRATIONS", ignoreCase = true)) {
            Log.w(TAG, "Registration limit reached in $operation. Freeing stale token registration.")
            runCatching {
                messaging.deleteToken().await()
            }.onFailure {
                Log.w(TAG, "Could not delete FCM token: ${it.message}")
            }
        } else {
            Log.w(TAG, "FCM operation $operation failed: ${e.message}")
        }
    }

    private fun topicForKey(key: String): String? = when (key) {
        KEY_POINTS -> TOPIC_POINTS
        KEY_NEWS -> TOPIC_NEWS
        KEY_OFFERS -> TOPIC_OFFERS
        KEY_EVENTS -> TOPIC_EVENTS
        else -> null
    }

    private fun syncedKeyForKey(key: String): String? = when (key) {
        KEY_POINTS -> KEY_SYNCED_POINTS
        KEY_NEWS -> KEY_SYNCED_NEWS
        KEY_OFFERS -> KEY_SYNCED_OFFERS
        KEY_EVENTS -> KEY_SYNCED_EVENTS
        else -> null
    }

    private fun String.sha256(): String = MessageDigest.getInstance("SHA-256")
        .digest(toByteArray())
        .joinToString("") { "%02x".format(it) }

    companion object {
        private const val TAG = "PushPreferencesRepo"
        const val KEY_POINTS = "push_points"
        const val KEY_NEWS = "push_news"
        const val KEY_OFFERS = "push_offers"
        const val KEY_EVENTS = "push_events"

        private const val KEY_SYNCED_POINTS = "synced_push_points"
        private const val KEY_SYNCED_NEWS = "synced_push_news"
        private const val KEY_SYNCED_OFFERS = "synced_push_offers"
        private const val KEY_SYNCED_EVENTS = "synced_push_events"

        const val TOPIC_POINTS = "gondolapp_points"
        const val TOPIC_NEWS = "gondolapp_news"
        const val TOPIC_OFFERS = "gondolapp_offers"
        const val TOPIC_EVENTS = "gondolapp_events"
        private const val PREFS_NAME = "push_preferences"
    }
}

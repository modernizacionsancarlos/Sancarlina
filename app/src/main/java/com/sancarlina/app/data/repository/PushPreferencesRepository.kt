package com.sancarlina.app.data.repository

import android.content.Context
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
    context: Context,
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

    suspend fun initialize() {
        syncTopics(current())
        runCatching { registerToken(messaging.token.await()) }
    }

    suspend fun setEnabled(key: String, enabled: Boolean) {
        preferences.edit().putBoolean(key, enabled).apply()
        val topic = topicForKey(key) ?: return
        if (enabled) messaging.subscribeToTopic(topic).await()
        else messaging.unsubscribeFromTopic(topic).await()
        runCatching { registerToken(messaging.token.await()) }
    }

    suspend fun registerCurrentToken() {
        runCatching { registerToken(messaging.token.await()) }
    }

    suspend fun registerToken(token: String) {
        if (token.isBlank()) return
        val uid = auth.currentUser?.uid ?: return
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
    }

    private suspend fun syncTopics(value: PushPreferences) {
        listOf(
            TOPIC_POINTS to value.points,
            TOPIC_NEWS to value.news,
            TOPIC_OFFERS to value.offers,
            TOPIC_EVENTS to value.events
        ).forEach { (topic, enabled) ->
            if (enabled) messaging.subscribeToTopic(topic).await()
            else messaging.unsubscribeFromTopic(topic).await()
        }
    }

    private fun topicForKey(key: String): String? = when (key) {
        KEY_POINTS -> TOPIC_POINTS
        KEY_NEWS -> TOPIC_NEWS
        KEY_OFFERS -> TOPIC_OFFERS
        KEY_EVENTS -> TOPIC_EVENTS
        else -> null
    }

    private fun String.sha256(): String = MessageDigest.getInstance("SHA-256")
        .digest(toByteArray())
        .joinToString("") { "%02x".format(it) }

    companion object {
        const val KEY_POINTS = "push_points"
        const val KEY_NEWS = "push_news"
        const val KEY_OFFERS = "push_offers"
        const val KEY_EVENTS = "push_events"
        const val TOPIC_POINTS = "gondolapp_points"
        const val TOPIC_NEWS = "gondolapp_news"
        const val TOPIC_OFFERS = "gondolapp_offers"
        const val TOPIC_EVENTS = "gondolapp_events"
        private const val PREFS_NAME = "push_preferences"
    }
}

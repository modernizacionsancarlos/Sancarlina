package com.sancarlina.app.data.repository

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sancarlina.app.data.remote.FirestoreCollections
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

class DiscoveryPreferencesRepository(
    context: Context,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private val preferences = context.getSharedPreferences("discovery_preferences", Context.MODE_PRIVATE)
    private val _interests = MutableStateFlow(readInterests())
    val interests: StateFlow<Set<String>> = _interests.asStateFlow()

    suspend fun saveInterests(values: Set<String>) {
        val normalized = values.map { it.trim().lowercase() }.filter { it.isNotBlank() }.toSet()
        preferences.edit().putStringSet(KEY_INTERESTS, normalized).apply()
        _interests.value = normalized
        auth.currentUser?.uid?.let { uid ->
            firestore.collection(FirestoreCollections.USER_PROFILES)
                .document(uid)
                .set(mapOf("interests" to normalized.toList()), com.google.firebase.firestore.SetOptions.merge())
                .await()
        }
    }

    private fun readInterests(): Set<String> =
        preferences.getStringSet(KEY_INTERESTS, emptySet()).orEmpty().toSet()

    companion object {
        private const val KEY_INTERESTS = "interests"
    }
}

package com.sancarlina.app.data.repository

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.sancarlina.app.data.remote.FirestoreCollections
import kotlinx.coroutines.tasks.await

class ItineraryRepository(
    context: Context,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private val preferences = context.getSharedPreferences("gondolapp_itinerary", Context.MODE_PRIVATE)
    private val separator = ""

    suspend fun loadIds(): List<String> {
        val local = localIds()
        val uid = auth.currentUser?.uid ?: return local
        return runCatching {
            val snapshot = firestore.collection(FirestoreCollections.ITINERARIES)
                .document(uid)
                .get()
                .await()
            val remote = (snapshot.get("tenantIds") as? List<*>)
                .orEmpty()
                .mapNotNull { it?.toString()?.takeIf(String::isNotBlank) }
                .distinct()
            if (remote.isNotEmpty()) {
                saveLocal(remote)
                remote
            } else {
                if (local.isNotEmpty()) saveRemote(uid, local)
                local
            }
        }.getOrElse { local }
    }

    suspend fun saveIds(ids: List<String>) {
        val normalized = ids.filter(String::isNotBlank).distinct().take(9)
        saveLocal(normalized)
        auth.currentUser?.uid?.let { uid ->
            runCatching { saveRemote(uid, normalized) }
        }
    }

    private fun localIds(): List<String> {
        return preferences.getString("tenant_ids", "")
            .orEmpty()
            .split(separator)
            .filter(String::isNotBlank)
            .distinct()
    }

    private fun saveLocal(ids: List<String>) {
        preferences.edit().putString("tenant_ids", ids.joinToString(separator)).apply()
    }

    private suspend fun saveRemote(uid: String, ids: List<String>) {
        firestore.collection(FirestoreCollections.ITINERARIES)
            .document(uid)
            .set(
                mapOf(
                    "userId" to uid,
                    "tenantIds" to ids,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            )
            .await()
    }
}

package com.sancarlina.app.data.repository

import com.sancarlina.app.data.remote.FirestoreCollections
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

data class Benefit(
    val id: String = "",
    val title: String = "",
    val cost: Int = 0,
    val points_cost: Int = 0,
    val description: String = "",
    val cover_url: String = "",
    val industry: String = "",
    val active: Boolean = true
)

class BenefitsRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    suspend fun getActiveBenefits(): List<Benefit> {
        return try {
            val snapshot = firestore.collection(FirestoreCollections.BENEFITS)
                .whereEqualTo("active", true)
                .get()
                .await()
            
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Benefit::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun observeActiveBenefits(): Flow<List<Benefit>> = callbackFlow {
        val listener = firestore.collection(FirestoreCollections.BENEFITS)
            .whereEqualTo("active", true)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                val benefits = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        doc.toObject(Benefit::class.java)?.copy(id = doc.id)
                    } catch (_: Exception) {
                        null
                    }
                }.orEmpty()
                trySend(benefits)
            }
        awaitClose { listener.remove() }
    }
}

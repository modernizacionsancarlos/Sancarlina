package com.sancarlina.app.data.repository

import com.sancarlina.app.data.model.FormSchema
import com.sancarlina.app.data.remote.FirestoreCollections
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FormsRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    suspend fun getFormsByTenant(tenantId: String): List<FormSchema> {
        return try {
            val snapshot = firestore.collection(FirestoreCollections.FORM_SCHEMAS)
                .whereEqualTo("tenantId", tenantId)
                .whereEqualTo("active", true)
                .get()
                .await()
            
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(FormSchema::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}

package com.sancarlina.app.data.repository

import com.sancarlina.app.data.models.FormSchema
import com.sancarlina.app.data.remote.FirestoreCollections
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

class FormsRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    suspend fun getFormsByTenant(commerceId: String): List<FormSchema> {
        if (commerceId.isBlank()) return emptyList()

        return try {
            // Buscamos por tenantId (camelCase)
            val snapshot1 = firestore.collection(FirestoreCollections.FORM_SCHEMAS)
                .whereEqualTo("tenantId", commerceId)
                .get()
                .await()
            
            // Buscamos por tenant_id (snake_case) como fallback/complemento
            val snapshot2 = firestore.collection(FirestoreCollections.FORM_SCHEMAS)
                .whereEqualTo("tenant_id", commerceId)
                .get()
                .await()
            
            val formsMap = mutableMapOf<String, FormSchema>()
            
            fun addFromSnapshot(snapshot: QuerySnapshot) {
                snapshot.documents.forEach { doc ->
                    try {
                        doc.toObject(FormSchema::class.java)?.let { form ->
                            formsMap[doc.id] = form.copy(id = doc.id)
                        }
                    } catch (e: Exception) {
                        // Skip corrupted documents
                    }
                }
            }
            
            addFromSnapshot(snapshot1)
            addFromSnapshot(snapshot2)
            
            formsMap.values.toList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}

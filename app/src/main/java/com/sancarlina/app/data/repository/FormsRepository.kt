package com.sancarlina.app.data.repository

import com.sancarlina.app.data.models.FormSchema
import com.sancarlina.app.data.local.OfflineFormsStore
import com.sancarlina.app.data.remote.FirestoreCollections
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.Source
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FormsRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val offlineStore: OfflineFormsStore? = null
) {
    suspend fun getAllAvailableForms(forceServer: Boolean = false): List<FormSchema> {
        return try {
            val source = if (forceServer) Source.SERVER else Source.DEFAULT
            val snapshot = firestore.collection(FirestoreCollections.FORM_SCHEMAS).get(source).await()
            snapshot.documents.mapNotNull { doc ->
                runCatching { FormSchema.fromMap(doc.id, doc.data.orEmpty()) }.getOrNull()
            }.onEach { offlineStore?.cacheSchema(it) }
        } catch (_: Exception) {
            offlineStore?.getAllCachedSchemas().orEmpty()
        }
    }

    suspend fun getFormById(formId: String): FormSchema? {
        if (formId.isBlank()) return null
        return try {
            val doc = firestore.collection(FirestoreCollections.FORM_SCHEMAS)
                .document(formId)
                .get()
                .await()
            if (doc.exists()) {
                FormSchema.fromMap(doc.id, doc.data.orEmpty()).also { offlineStore?.cacheSchema(it) }
            } else {
                offlineStore?.getCachedSchema(formId)
            }
        } catch (_: Exception) {
            offlineStore?.getCachedSchema(formId)
        }
    }

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
                        formsMap[doc.id] = FormSchema.fromMap(doc.id, doc.data.orEmpty())
                    } catch (e: Exception) {
                        // Skip corrupted documents
                    }
                }
            }
            
            addFromSnapshot(snapshot1)
            addFromSnapshot(snapshot2)
            
            formsMap.values.toList().onEach { offlineStore?.cacheSchema(it) }
        } catch (e: Exception) {
            offlineStore?.getCachedSchemasByTenant(commerceId).orEmpty()
        }
    }

    fun observeFormsByTenant(commerceId: String): Flow<List<FormSchema>> = callbackFlow {
        if (commerceId.isBlank()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        offlineStore?.getCachedSchemasByTenant(commerceId)
            ?.takeIf { it.isNotEmpty() }
            ?.let(::trySend)

        val listener = firestore.collection(FirestoreCollections.FORM_SCHEMAS)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                val forms = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        FormSchema.fromMap(doc.id, doc.data.orEmpty())
                            .takeIf { it.tenantId == commerceId || it.tenant_id == commerceId }
                    } catch (_: Exception) {
                        null
                    }
                }.orEmpty()
                forms.forEach { offlineStore?.cacheSchema(it) }
                trySend(forms)
            }
        awaitClose { listener.remove() }
    }
}

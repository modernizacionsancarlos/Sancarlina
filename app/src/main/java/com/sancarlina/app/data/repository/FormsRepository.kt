package com.sancarlina.app.data.repository

import com.sancarlina.app.data.models.FormSchema
import com.sancarlina.app.data.local.OfflineFormsStore
import com.sancarlina.app.data.cache.CacheDataset
import com.sancarlina.app.data.cache.CacheMetadataStore
import com.sancarlina.app.data.cache.DataAccessMetrics
import com.sancarlina.app.data.remote.FirestoreCollections
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.Source
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FormsRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val offlineStore: OfflineFormsStore? = null,
    private val cacheMetadata: CacheMetadataStore? = null,
    private val invalidation: CatalogInvalidationRepository? = null
) {
    private val dataset = CacheDataset.FORMS

    suspend fun getAllAvailableForms(forceServer: Boolean = false): List<FormSchema> {
        val local = offlineStore?.getAllCachedSchemas().orEmpty()
        val remoteVersion = invalidation?.versions?.value?.versionFor(dataset) ?: 0L
        val localVersion = cacheMetadata?.version(dataset) ?: 0L
        if (!forceServer && local.isNotEmpty() &&
            cacheMetadata?.isStale(dataset) == false && remoteVersion <= localVersion
        ) {
            return local
        }
        return try {
            val snapshot = firestore.collection(FirestoreCollections.FORM_SCHEMAS)
                .limit(MAX_ALL_FORMS)
                .get(Source.SERVER)
                .await()
            DataAccessMetrics.recordServerResult("forms_all", snapshot.size())
            snapshot.documents.mapNotNull { doc ->
                runCatching { FormSchema.fromMap(doc.id, doc.data.orEmpty()) }.getOrNull()
            }.onEach { offlineStore?.cacheSchema(it) }.also {
                cacheMetadata?.markSynced(dataset, remoteVersion)
            }
        } catch (_: Exception) {
            local
        }
    }

    suspend fun getFormById(formId: String): FormSchema? {
        if (formId.isBlank()) return null
        val local = offlineStore?.getCachedSchema(formId)
        val remoteVersion = invalidation?.versions?.value?.versionFor(dataset) ?: 0L
        val localVersion = cacheMetadata?.version(dataset) ?: 0L
        if (local != null && cacheMetadata?.isStale(dataset) == false && remoteVersion <= localVersion) {
            return local
        }

        val reference = firestore.collection(FirestoreCollections.FORM_SCHEMAS).document(formId)
        return try {
            val doc = reference.get(Source.SERVER).await()
            DataAccessMetrics.recordServerResult("form_detail", if (doc.exists()) 1 else 0)
            cacheMetadata?.markSynced(dataset, remoteVersion)
            if (doc.exists()) {
                FormSchema.fromMap(doc.id, doc.data.orEmpty()).also { offlineStore?.cacheSchema(it) }
            } else {
                null
            }
        } catch (_: Exception) {
            local ?: runCatching {
                DataAccessMetrics.recordCacheQuery("form_detail")
                reference.get(Source.CACHE).await()
            }.getOrNull()?.takeIf { it.exists() }?.let {
                FormSchema.fromMap(it.id, it.data.orEmpty())
            }
        }
    }

    suspend fun getFormsByTenant(commerceId: String, forceServer: Boolean = false): List<FormSchema> {
        if (commerceId.isBlank()) return emptyList()
        val local = offlineStore?.getCachedSchemasByTenant(commerceId).orEmpty()
        val remoteVersion = invalidation?.versions?.value?.versionFor(dataset) ?: 0L
        val localVersion = cacheMetadata?.version(dataset, commerceId) ?: 0L
        val shouldRefresh = forceServer || local.isEmpty() ||
            cacheMetadata?.isStale(dataset, commerceId) != false || remoteVersion > localVersion
        if (!shouldRefresh) return local

        return try {
            // Buscamos por tenantId (camelCase)
            val snapshot1 = firestore.collection(FirestoreCollections.FORM_SCHEMAS)
                .whereEqualTo("tenantId", commerceId)
                .limit(MAX_FORMS_PER_TENANT)
                .get(Source.SERVER)
                .await()
            
            // Buscamos por tenant_id (snake_case) como fallback/complemento
            val snapshot2 = firestore.collection(FirestoreCollections.FORM_SCHEMAS)
                .whereEqualTo("tenant_id", commerceId)
                .limit(MAX_FORMS_PER_TENANT)
                .get(Source.SERVER)
                .await()

            DataAccessMetrics.recordServerResult(
                "forms_by_tenant",
                snapshot1.size() + snapshot2.size()
            )
            
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
            
            formsMap.values.toList().onEach { offlineStore?.cacheSchema(it) }.also {
                cacheMetadata?.markSynced(dataset, remoteVersion, commerceId)
            }
        } catch (e: Exception) {
            local
        }
    }

    fun observeFormsByTenant(commerceId: String): Flow<List<FormSchema>> = flow {
        if (commerceId.isBlank()) {
            emit(emptyList())
            return@flow
        }
        offlineStore?.getCachedSchemasByTenant(commerceId)
            ?.takeIf { it.isNotEmpty() }
            ?.let { emit(it) }

        if (invalidation == null) {
            emit(getFormsByTenant(commerceId))
        } else {
            invalidation.versions
                .map { getFormsByTenant(commerceId) }
                .distinctUntilChanged()
                .collect { emit(it) }
        }
    }.distinctUntilChanged()

    private companion object {
        const val MAX_FORMS_PER_TENANT = 50L
        const val MAX_ALL_FORMS = 100L
    }
}

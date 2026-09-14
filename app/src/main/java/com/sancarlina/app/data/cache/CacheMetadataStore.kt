package com.sancarlina.app.data.cache

import android.content.Context

/** Guarda sólo metadatos de sincronización; los documentos siguen en la caché persistente de Firestore. */
class CacheMetadataStore(context: Context) {
    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun isStale(
        dataset: CacheDataset,
        scope: String? = null,
        nowMillis: Long = System.currentTimeMillis()
    ): Boolean {
        val lastSync = preferences.getLong(lastSyncKey(dataset, scope), 0L)
        return lastSync <= 0L || nowMillis - lastSync >= dataset.ttlMillis
    }

    fun version(dataset: CacheDataset, scope: String? = null): Long =
        preferences.getLong(versionKey(dataset, scope), 0L)

    fun markSynced(
        dataset: CacheDataset,
        version: Long = version(dataset),
        scope: String? = null,
        nowMillis: Long = System.currentTimeMillis()
    ) {
        preferences.edit()
            .putLong(lastSyncKey(dataset, scope), nowMillis)
            .putLong(versionKey(dataset, scope), version.coerceAtLeast(0L))
            .apply()
    }

    private fun lastSyncKey(dataset: CacheDataset, scope: String?) =
        "${dataset.storageKey}${scopeSuffix(scope)}_last_server_sync"

    private fun versionKey(dataset: CacheDataset, scope: String?) =
        "${dataset.storageKey}${scopeSuffix(scope)}_version"

    private fun scopeSuffix(scope: String?): String =
        scope?.takeIf { it.isNotBlank() }?.hashCode()?.toUInt()?.toString(16)?.let { "_$it" }.orEmpty()

    private companion object {
        const val PREFERENCES_NAME = "catalog_cache_metadata"
    }
}

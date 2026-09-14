package com.sancarlina.app.data.cache

import com.sancarlina.app.utils.Logger
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

/**
 * Contadores diagnósticos locales. No equivalen a la facturación de Firebase, pero permiten
 * comprobar cuántas consultas remotas intenta la app y cuántos documentos devuelve cada una.
 */
object DataAccessMetrics {
    private val cacheQueries = ConcurrentHashMap<String, AtomicLong>()
    private val serverQueries = ConcurrentHashMap<String, AtomicLong>()
    private val returnedDocuments = ConcurrentHashMap<String, AtomicLong>()
    private val listenerStarts = ConcurrentHashMap<String, AtomicLong>()

    fun recordCacheQuery(label: String) = increment(cacheQueries, label, "cache_query")

    fun recordServerResult(label: String, documentCount: Int) {
        increment(serverQueries, label, "server_query")
        returnedDocuments.computeIfAbsent(label) { AtomicLong() }.addAndGet(documentCount.toLong())
        Logger.d("DataAccess[$label]: respuesta remota con $documentCount documentos")
    }

    fun recordListenerStart(label: String) = increment(listenerStarts, label, "listener_start")

    fun snapshot(label: String): Snapshot = Snapshot(
        cacheQueries = cacheQueries[label]?.get() ?: 0L,
        serverQueries = serverQueries[label]?.get() ?: 0L,
        returnedDocuments = returnedDocuments[label]?.get() ?: 0L,
        listenerStarts = listenerStarts[label]?.get() ?: 0L
    )

    private fun increment(
        counters: ConcurrentHashMap<String, AtomicLong>,
        label: String,
        operation: String
    ) {
        val value = counters.computeIfAbsent(label) { AtomicLong() }.incrementAndGet()
        Logger.d("DataAccess[$label]: $operation #$value")
    }

    data class Snapshot(
        val cacheQueries: Long,
        val serverQueries: Long,
        val returnedDocuments: Long,
        val listenerStarts: Long
    )
}


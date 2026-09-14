package com.sancarlina.app.analytics

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.metrics.Trace
import com.sancarlina.app.BuildConfig
import com.sancarlina.app.data.remote.FirestoreCollections

/**
 * Punto único de configuración de Crashlytics y Performance Monitoring.
 *
 * Los builds de debug no envían reportes: de lo contrario el ruido de
 * desarrollo se mezcla con los fallos reales de los ciudadanos y las métricas
 * de arranque dejan de ser comparables entre versiones.
 */
object Telemetry {

    private val crashlytics: FirebaseCrashlytics by lazy { FirebaseCrashlytics.getInstance() }

    /** Traza personalizada que mide el arranque hasta que el catálogo queda disponible. */
    const val TRACE_COLD_START = "gondolapp_cold_start"

    /** Traza personalizada que mide una consulta completa del catálogo de comercios. */
    const val TRACE_CATALOG_REFRESH = "gondolapp_catalog_refresh"

    fun install() {
        val collectionEnabled = !BuildConfig.DEBUG
        crashlytics.isCrashlyticsCollectionEnabled = collectionEnabled
        FirebasePerformance.getInstance().isPerformanceCollectionEnabled = collectionEnabled

        crashlytics.setCustomKey("app_version", BuildConfig.VERSION_NAME)
        crashlytics.setCustomKey("app_build", BuildConfig.VERSION_CODE)
        crashlytics.setCustomKey("tenant_id", FirestoreCollections.DEFAULT_TENANT_ID)
    }

    /**
     * Asocia los reportes al identificador de Firebase Auth, no al correo ni al
     * documento del ciudadano. Es un identificador opaco, suficiente para
     * correlacionar un reclamo de soporte con su fallo y borrable a pedido.
     */
    fun setUser(uid: String?) {
        crashlytics.setUserId(uid.orEmpty())
    }

    /** Registra el paso por una pantalla para reconstruir el camino previo a un fallo. */
    fun breadcrumb(message: String) {
        crashlytics.log(message.take(200))
    }

    /**
     * Informa un error que la app resolvió sin cerrarse. Sirve para ver fallos
     * que hoy quedan silenciados dentro de un `runCatching`.
     */
    fun recordHandled(throwable: Throwable, context: String? = null) {
        if (context != null) crashlytics.setCustomKey("handled_context", context.take(100))
        crashlytics.recordException(throwable)
    }

    fun startTrace(name: String): Trace = FirebasePerformance.startTrace(name)
}

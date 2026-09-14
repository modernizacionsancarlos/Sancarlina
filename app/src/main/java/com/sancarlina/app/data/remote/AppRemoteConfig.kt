package com.sancarlina.app.data.remote

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.sancarlina.app.BuildConfig
import kotlinx.coroutines.tasks.await

/**
 * Parámetros que el municipio puede cambiar desde la consola de Firebase sin
 * publicar una versión nueva en Play.
 *
 * Cada clave tiene un valor por defecto compilado en la app, de modo que la
 * primera apertura y el modo sin conexión funcionan aunque la consulta remota
 * todavía no haya respondido.
 */
object AppRemoteConfig {

    /** Tope de comercios que la app descarga del catálogo. */
    const val KEY_CATALOG_MAX_TENANTS = "catalog_max_tenants"

    /** Minutos que la app considera vigente la caché del catálogo. */
    const val KEY_CATALOG_CACHE_MINUTES = "catalog_cache_minutes"

    /** Caracteres mínimos antes de disparar una búsqueda. */
    const val KEY_SEARCH_MIN_QUERY_LENGTH = "search_min_query_length"

    /** Mensaje de mantenimiento; cadena vacía significa operación normal. */
    const val KEY_MAINTENANCE_MESSAGE = "maintenance_message"

    /**
     * Build mínimo soportado. Si el build instalado es menor, la app debe pedir
     * actualización. Permite cortar una versión con un fallo grave sin esperar
     * la adopción voluntaria.
     */
    const val KEY_MIN_SUPPORTED_BUILD = "min_supported_build"

    /** Permite apagar el envío de reseñas si aparece un problema de moderación. */
    const val KEY_REVIEWS_ENABLED = "reviews_enabled"

    private val defaults: Map<String, Any> = mapOf(
        KEY_CATALOG_MAX_TENANTS to 500L,
        KEY_CATALOG_CACHE_MINUTES to 15L,
        KEY_SEARCH_MIN_QUERY_LENGTH to 3L,
        KEY_MAINTENANCE_MESSAGE to "",
        KEY_MIN_SUPPORTED_BUILD to 0L,
        KEY_REVIEWS_ENABLED to true
    )

    private val remoteConfig: FirebaseRemoteConfig by lazy {
        FirebaseRemoteConfig.getInstance().apply {
            setConfigSettingsAsync(
                remoteConfigSettings {
                    // En desarrollo se consulta en cada apertura; en producción una
                    // vez por hora, que es el intervalo recomendado por Firebase.
                    minimumFetchIntervalInSeconds = if (BuildConfig.DEBUG) 0 else 3600
                }
            )
            setDefaultsAsync(defaults)
        }
    }

    /** Descarga y aplica los valores remotos. Es seguro llamarla en cada arranque. */
    suspend fun refresh(): Boolean = runCatching {
        remoteConfig.fetchAndActivate().await()
    }.getOrDefault(false)

    val catalogMaxTenants: Long get() = remoteConfig.getLong(KEY_CATALOG_MAX_TENANTS)
    val catalogCacheMinutes: Long get() = remoteConfig.getLong(KEY_CATALOG_CACHE_MINUTES)
    val searchMinQueryLength: Int get() = remoteConfig.getLong(KEY_SEARCH_MIN_QUERY_LENGTH).toInt()
    val maintenanceMessage: String get() = remoteConfig.getString(KEY_MAINTENANCE_MESSAGE)
    val minSupportedBuild: Long get() = remoteConfig.getLong(KEY_MIN_SUPPORTED_BUILD)
    val reviewsEnabled: Boolean get() = remoteConfig.getBoolean(KEY_REVIEWS_ENABLED)

    /** true cuando el build instalado quedó por debajo del mínimo soportado. */
    val updateRequired: Boolean get() = BuildConfig.VERSION_CODE < minSupportedBuild
}

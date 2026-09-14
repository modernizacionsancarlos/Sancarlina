package com.sancarlina.app.data.cache

/**
 * Políticas de revalidación del contenido público.
 *
 * La caché local se muestra de inmediato. Firestore sólo se consulta al vencer el TTL o cuando
 * `app_metadata/public_catalog` anuncia una versión nueva para el conjunto correspondiente.
 */
enum class CacheDataset(
    val storageKey: String,
    val versionField: String,
    val ttlMillis: Long
) {
    TENANTS("tenants", "tenantsVersion", 6 * 60 * 60 * 1_000L),
    AREAS("areas", "areasVersion", 24 * 60 * 60 * 1_000L),
    BENEFITS("benefits", "benefitsVersion", 60 * 60 * 1_000L),
    FORMS("forms", "formsVersion", 60 * 60 * 1_000L),
    NOTIFICATIONS("notifications", "notificationsVersion", 15 * 60 * 1_000L)
}


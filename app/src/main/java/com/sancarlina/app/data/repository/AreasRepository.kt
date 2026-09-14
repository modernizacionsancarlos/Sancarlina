package com.sancarlina.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Source
import com.sancarlina.app.data.cache.AppCache
import com.sancarlina.app.data.cache.CacheDataset
import com.sancarlina.app.data.cache.CacheMetadataStore
import com.sancarlina.app.data.cache.DataAccessMetrics
import com.sancarlina.app.data.remote.FirestoreCollections
import com.sancarlina.app.utils.Logger
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await

data class Area(
    val id: String = "",
    val name: String = "",
    val slug: String = "",
    val description: String = "",
    val order: Int = 0,
    val category: String = "geographic", // "geographic" | "thematic"
    val icon: String = "",
    val active: Boolean = true
) {
    companion object {
        /** Mapeo explícito: no depende de reflexión y es seguro con R8 en Release. */
        fun fromMap(id: String, data: Map<String, Any?>): Area {
            fun value(vararg keys: String): Any? = keys.firstNotNullOfOrNull { data[it] }
            fun text(vararg keys: String): String = value(*keys)?.toString()?.trim().orEmpty()
            fun integer(vararg keys: String): Int = when (val raw = value(*keys)) {
                is Number -> raw.toInt()
                is String -> raw.toIntOrNull() ?: 0
                else -> 0
            }
            fun boolean(default: Boolean, vararg keys: String): Boolean = when (val raw = value(*keys)) {
                is Boolean -> raw
                is Number -> raw.toInt() != 0
                is String -> when {
                    raw.equals("true", ignoreCase = true) || raw == "1" -> true
                    raw.equals("false", ignoreCase = true) || raw == "0" -> false
                    else -> default
                }
                else -> default
            }

            return Area(
                id = id,
                name = text("name", "title"),
                slug = text("slug"),
                description = text("description"),
                order = integer("order", "position"),
                category = text("category", "type").ifBlank { "geographic" },
                icon = text("icon", "iconName", "icon_name"),
                active = boolean(true, "active", "isActive", "is_active")
            )
        }
    }
}

class AreasRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val cacheMetadata: CacheMetadataStore? = null,
    private val invalidation: CatalogInvalidationRepository? = null,
    private val repositoryScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
) {
    private val dataset = CacheDataset.AREAS
    private val refreshMutex = Mutex()
    private val lastRefreshAttempt = AtomicLong(0L)
    private val started = AtomicBoolean(false)
    private val hasCatalogSnapshot = AtomicBoolean(AppCache.getAreas() != null)
    val suggestedAreas = listOf(
        // Geográficas (1–7)
        Area(id = "area_centro", name = "Centro / Villa San Carlos", slug = "centro", description = "Centro cívico y comercial principal de San Carlos", order = 1, category = "geographic", icon = "location_city", active = true),
        Area(id = "area_eugenio_bustos", name = "Eugenio Bustos", slug = "eugenio-bustos", description = "Distrito comercial e histórico", order = 2, category = "geographic", icon = "storefront", active = true),
        Area(id = "area_la_consulta", name = "La Consulta", slug = "la-consulta", description = "Polo enoturístico y gastronómico del departamento", order = 3, category = "geographic", icon = "wine_bar", active = true),
        Area(id = "area_pareditas", name = "Pareditas", slug = "pareditas", description = "Puerta de entrada al sur sancarlino y circuito de artesanos", order = 4, category = "geographic", icon = "palette", active = true),
        Area(id = "area_chilecito", name = "Chilecito", slug = "chilecito", description = "Distrito agrícola y cultural", order = 5, category = "geographic", icon = "agriculture", active = true),
        Area(id = "area_tres_esquinas", name = "Tres Esquinas", slug = "tres-esquinas", description = "Zona de tradición agrícola y turística", order = 6, category = "geographic", icon = "landscape", active = true),
        Area(id = "area_villa_chacon", name = "Villa Chacón", slug = "villa-chacon", description = "Distrito de bodegas y emprendimientos familiares", order = 7, category = "geographic", icon = "cottage", active = true),
        // Temáticas (8–15)
        Area(id = "area_vinedos", name = "Viñedos y Bodegas", slug = "vinedos-bodegas", description = "Ruta del vino sancarlina y visitas guiadas", order = 8, category = "thematic", icon = "wine_bar", active = true),
        Area(id = "area_gastronomia", name = "Gastronomía local", slug = "gastronomia", description = "Restaurantes, comedores y sabores típicos", order = 9, category = "thematic", icon = "restaurant", active = true),
        Area(id = "area_experiencias", name = "Experiencias y Aventura", slug = "experiencias-aventura", description = "Turismo activo, cabalgatas y trekkings", order = 10, category = "thematic", icon = "hiking", active = true),
        Area(id = "area_hospedaje", name = "Hospedaje y Cabañas", slug = "hospedaje", description = "Alojamientos, cabañas y posadas", order = 11, category = "thematic", icon = "hotel", active = true),
        Area(id = "area_cultura", name = "Cultura e Historia", slug = "cultura-historia", description = "Museos, sitios históricos y patrimonio", order = 12, category = "thematic", icon = "museum", active = true),
        Area(id = "area_termas", name = "Termas y Bienestar", slug = "termas-bienestar", description = "Aguas termales y centros de relax", order = 13, category = "thematic", icon = "spa", active = true),
        Area(id = "area_artesanias", name = "Artesanías y Productos", slug = "artesanias", description = "Feriantes, productos regionales y artesanías", order = 14, category = "thematic", icon = "shopping_bag", active = true),
        Area(id = "area_eventos", name = "Eventos y Fiestas", slug = "eventos-fiestas", description = "Festivales, ferias y festejos departamentales", order = 15, category = "thematic", icon = "event", active = true)
    )

    private val areas = MutableStateFlow(AppCache.getAreas() ?: suggestedAreas)

    suspend fun getAreas(forceRefresh: Boolean = false): List<Area> {
        ensureStarted()
        if (AppCache.getAreas() == null) loadFromLocalCache()
        refreshIfNeeded(force = forceRefresh)
        return areas.value
    }

    fun getAreasFlow(): Flow<List<Area>> {
        ensureStarted()
        return areas
    }

    fun onAppForegrounded() {
        if (started.get()) repositoryScope.launch { refreshIfNeeded() }
    }

    private fun ensureStarted() {
        if (!started.compareAndSet(false, true)) return
        repositoryScope.launch {
            loadFromLocalCache()
            refreshIfNeeded()
            invalidation?.versions
                ?.map { it.versionFor(dataset) }
                ?.distinctUntilChanged()
                ?.collect { remoteVersion ->
                    if (remoteVersion > (cacheMetadata?.version(dataset) ?: 0L)) {
                        refreshIfNeeded(force = true, targetVersion = remoteVersion)
                    }
                }
        }
    }

    private suspend fun loadFromLocalCache() {
        runCatching {
            DataAccessMetrics.recordCacheQuery(dataset.storageKey)
            firestore.collection(FirestoreCollections.AREAS)
                .limit(MAX_CATALOG_AREAS)
                .get(Source.CACHE)
                .await()
        }.onSuccess { snapshot ->
            if (!snapshot.isEmpty) {
                hasCatalogSnapshot.set(true)
                updateCache(snapshot.documents.mapNotNull(::mapArea))
            }
        }
    }

    private suspend fun refreshIfNeeded(force: Boolean = false, targetVersion: Long? = null) {
        refreshMutex.withLock {
            if (!force && hasCatalogSnapshot.get() && cacheMetadata?.isStale(dataset) == false) return
            val now = System.currentTimeMillis()
            if (!force && now - lastRefreshAttempt.get() < REFRESH_RETRY_BACKOFF_MILLIS) return
            lastRefreshAttempt.set(now)
            runCatching {
                firestore.collection(FirestoreCollections.AREAS)
                    .limit(MAX_CATALOG_AREAS)
                    .get(Source.SERVER)
                    .await()
            }.onSuccess { snapshot ->
                hasCatalogSnapshot.set(true)
                updateCache(snapshot.documents.mapNotNull(::mapArea))
                DataAccessMetrics.recordServerResult(dataset.storageKey, snapshot.size())
                val version = targetVersion
                    ?: invalidation?.versions?.value?.versionFor(dataset)
                    ?: cacheMetadata?.version(dataset)
                    ?: 0L
                cacheMetadata?.markSynced(dataset, version)
            }.onFailure { exception ->
                Logger.e("Error refreshing areas; se conserva la caché", exception)
            }
        }
    }

    private fun updateCache(remoteAreas: List<Area>) {
        val resolved = remoteAreas.sortedBy { it.order }.ifEmpty { suggestedAreas }
        AppCache.setAreas(resolved)
        areas.value = resolved
    }

    private fun mapArea(document: DocumentSnapshot): Area? = try {
        Area.fromMap(document.id, document.data.orEmpty())
    } catch (exception: Exception) {
        Logger.e("Error mapping area ${document.id}", exception)
        null
    }

    /**
     * Crea las 15 zonas sugeridas en Firestore sin sobrescribir las existentes.
     */
    suspend fun ensureSuggestedAreas(): Result<Int> {
        return try {
            val currentAreas = getAreas(forceRefresh = true)
            val existingIds = currentAreas.map { it.id }.toSet()
            var addedCount = 0

            for (area in suggestedAreas) {
                if (!existingIds.contains(area.id)) {
                    val data = mapOf(
                        "name" to area.name,
                        "slug" to area.slug,
                        "description" to area.description,
                        "order" to area.order,
                        "category" to area.category,
                        "icon" to area.icon,
                        "active" to area.active
                    )
                    firestore.collection(FirestoreCollections.AREAS)
                        .document(area.id)
                        .set(data)
                        .await()
                    addedCount++
                }
            }
            Result.success(addedCount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private companion object {
        const val MAX_CATALOG_AREAS = 100L
        const val REFRESH_RETRY_BACKOFF_MILLIS = 60_000L
    }
}

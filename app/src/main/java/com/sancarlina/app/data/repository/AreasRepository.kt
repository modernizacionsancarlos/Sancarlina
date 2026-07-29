package com.sancarlina.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.sancarlina.app.data.remote.FirestoreCollections
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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
)

class AreasRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private var cachedAreas: List<Area>? = null

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

    suspend fun getAreas(): List<Area> {
        cachedAreas?.let { return it }

        return try {
            val snapshot = firestore.collection(FirestoreCollections.AREAS).get().await()
            val areas = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Area::class.java)?.copy(id = doc.id)
            }.sortedBy { it.order }

            cachedAreas = areas
            areas
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getAreasFlow(): Flow<List<Area>> = callbackFlow {
        val listener = firestore.collection(FirestoreCollections.AREAS)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val areas = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Area::class.java)?.copy(id = doc.id)
                }?.sortedBy { it.order } ?: emptyList()

                trySend(areas)
            }
        awaitClose { listener.remove() }
    }

    /**
     * Crea las 15 zonas sugeridas en Firestore sin sobrescribir las existentes.
     */
    suspend fun ensureSuggestedAreas(): Result<Int> {
        return try {
            val currentAreas = getAreas()
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
            cachedAreas = null
            Result.success(addedCount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

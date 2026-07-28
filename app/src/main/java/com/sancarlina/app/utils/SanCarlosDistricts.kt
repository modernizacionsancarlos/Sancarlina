package com.sancarlina.app.utils

import com.sancarlina.app.data.models.FormField
import java.text.Normalizer
import java.util.Locale

data class DistrictGeo(
    val name: String,
    val lat: Double,
    val lng: Double,
    val radiusM: Double,
    val zoom: Float = 14f
)

object SanCarlosDistricts {

    val DISTRICT_CENTERS = mapOf(
        "Pareditas" to DistrictGeo("Pareditas", -33.94107, -69.0784, 1800.0),
        "Chilecito" to DistrictGeo("Chilecito", -33.88817, -69.07198, 1600.0),
        "Tres Esquinas" to DistrictGeo("Tres Esquinas", -33.83333, -69.06667, 1400.0),
        "Eugenio Bustos" to DistrictGeo("Eugenio Bustos", -33.77734, -69.07044, 2000.0),
        "Villa San Carlos" to DistrictGeo("Villa San Carlos", -33.76928, -69.04446, 1800.0),
        "Villa Chacón" to DistrictGeo("Villa Chacón", -33.76, -69.055, 1400.0),
        "La Consulta" to DistrictGeo("La Consulta", -33.73579, -69.12182, 2000.0)
    )

    fun resolveDistrictGeo(name: String?): DistrictGeo? {
        val raw = name?.trim() ?: return null
        if (raw.isBlank() || raw.contains("Omitir", ignoreCase = true)) return null

        DISTRICT_CENTERS[raw]?.let { return it }

        val normalized = removeAccents(raw.lowercase(Locale.ROOT))
        for ((key, geo) in DISTRICT_CENTERS) {
            val keyNorm = removeAccents(key.lowercase(Locale.ROOT))
            if (keyNorm == normalized || keyNorm.contains(normalized) || normalized.contains(keyNorm)) {
                return geo
            }
        }
        return null
    }

    fun findDistrictText(fields: List<FormField>, values: Map<String, Any?>): String {
        val byMapping = fields.find { f ->
            val m = f.tenantMapping?.lowercase(Locale.ROOT) ?: ""
            m == "area_id" || m == "area" || m == "localidad" || m == "district"
        }
        if (byMapping != null) {
            val raw = values[byMapping.id]
            if (raw is String && raw.isNotBlank()) return raw.trim()
        }

        val byLabel = fields.find { f ->
            if (f.type == "section" || f.type == "gps") return@find false
            val label = f.label.lowercase(Locale.ROOT)
            label.contains("localidad") || label.contains("distrito") || label.contains("zona")
        }
        if (byLabel != null) {
            val raw = values[byLabel.id]
            if (raw is String && raw.isNotBlank()) return raw.trim()
        }

        return ""
    }

    private fun removeAccents(text: String): String {
        val normalized = Normalizer.normalize(text, Normalizer.Form.NFD)
        return normalized.replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
    }
}

package com.sancarlina.app.utils

import com.sancarlina.app.data.models.FormField
import org.junit.Assert.*
import org.junit.Test

class SanCarlosDistrictsTest {

    @Test
    fun testResolveDistrictGeo_exactAndNormalized() {
        val geoEugenio = SanCarlosDistricts.resolveDistrictGeo("Eugenio Bustos")
        assertNotNull(geoEugenio)
        assertEquals(-33.77734, geoEugenio!!.lat, 0.0001)
        assertEquals(-69.07044, geoEugenio.lng, 0.0001)

        val geoConsulta = SanCarlosDistricts.resolveDistrictGeo("la consulta")
        assertNotNull(geoConsulta)
        assertEquals(-33.73579, geoConsulta!!.lat, 0.0001)

        val geoOmitir = SanCarlosDistricts.resolveDistrictGeo("Omitir (no sé el distrito)")
        assertNull(geoOmitir)
    }

    @Test
    fun testFindDistrictText_findsFieldByMapping() {
        val fields = listOf(
            FormField(id = "f1", label = "Nombre", tenantMapping = "name"),
            FormField(id = "f2", label = "Distrito", tenantMapping = "area_id")
        )
        val values = mapOf("f1" to "Comercio Test", "f2" to "Pareditas")

        val districtText = SanCarlosDistricts.findDistrictText(fields, values)
        assertEquals("Pareditas", districtText)
    }
}

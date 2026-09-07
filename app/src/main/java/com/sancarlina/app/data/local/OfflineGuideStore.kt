package com.sancarlina.app.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.sancarlina.app.data.models.Tenant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

/**
 * Almacenamiento local SQLite para la Guía Offline de San Carlos y Valle de Uco.
 * Permite guardar comercios, bodegas y sitios turísticos para consultarlos sin conexión
 * a internet en zonas de cordillera, rutas del vino y parajes rurales.
 */
class OfflineGuideStore(context: Context) : SQLiteOpenHelper(
    context.applicationContext,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {
    private val lock = Any()
    private val _savedTenants = MutableStateFlow<List<Tenant>>(emptyList())
    val savedTenants: StateFlow<List<Tenant>> = _savedTenants.asStateFlow()

    init {
        refresh()
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS saved_merchants (
                id TEXT PRIMARY KEY NOT NULL,
                name TEXT NOT NULL,
                industry TEXT NOT NULL,
                description TEXT NOT NULL,
                address TEXT NOT NULL,
                contact_phone TEXT NOT NULL,
                whatsapp TEXT NOT NULL,
                image_url TEXT NOT NULL,
                schedule TEXT NOT NULL,
                coordinates TEXT NOT NULL,
                services TEXT NOT NULL,
                open_now INTEGER NOT NULL DEFAULT 0,
                points_multiplier REAL NOT NULL DEFAULT 1.0,
                saved_at INTEGER NOT NULL
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // No migration needed for v1
    }

    fun saveMerchant(tenant: Tenant) {
        synchronized(lock) {
            val db = writableDatabase
            val values = ContentValues().apply {
                put("id", tenant.id)
                put("name", tenant.name)
                put("industry", tenant.industry)
                put("description", tenant.description)
                put("address", tenant.address)
                put("contact_phone", tenant.contactPhone)
                put("whatsapp", tenant.whatsapp)
                put("image_url", tenant.coverUrl.ifBlank { tenant.imageUrl }.ifBlank { tenant.photoUrl })
                put("schedule", tenant.schedule)
                put("coordinates", tenant.geoCoordinates)
                put("services", tenant.services.joinToString(";"))
                put("open_now", if (tenant.openNow == true) 1 else 0)
                put("points_multiplier", tenant.pointsMultiplier ?: 1.0)
                put("saved_at", System.currentTimeMillis())
            }
            db.insertWithOnConflict(TABLE_SAVED, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        }
        refresh()
    }

    fun removeMerchant(tenantId: String) {
        synchronized(lock) {
            writableDatabase.delete(TABLE_SAVED, "id = ?", arrayOf(tenantId))
        }
        refresh()
    }

    fun toggleSaved(tenant: Tenant): Boolean {
        return if (isMerchantSaved(tenant.id)) {
            removeMerchant(tenant.id)
            false
        } else {
            saveMerchant(tenant)
            true
        }
    }

    fun isMerchantSaved(tenantId: String): Boolean {
        if (tenantId.isBlank()) return false
        synchronized(lock) {
            val cursor = readableDatabase.rawQuery(
                "SELECT 1 FROM $TABLE_SAVED WHERE id = ? LIMIT 1",
                arrayOf(tenantId)
            )
            return cursor.use { it.moveToFirst() }
        }
    }

    fun observeIsSaved(tenantId: String) = _savedTenants.map { list ->
        list.any { it.id == tenantId }
    }

    fun refresh() {
        val list = mutableListOf<Tenant>()
        synchronized(lock) {
            val cursor = readableDatabase.query(
                TABLE_SAVED,
                null,
                null,
                null,
                null,
                null,
                "saved_at DESC"
            )
            cursor.use { c ->
                while (c.moveToNext()) {
                    val id = c.getString(c.getColumnIndexOrThrow("id"))
                    val name = c.getString(c.getColumnIndexOrThrow("name"))
                    val industry = c.getString(c.getColumnIndexOrThrow("industry"))
                    val description = c.getString(c.getColumnIndexOrThrow("description"))
                    val address = c.getString(c.getColumnIndexOrThrow("address"))
                    val phone = c.getString(c.getColumnIndexOrThrow("contact_phone"))
                    val whatsapp = c.getString(c.getColumnIndexOrThrow("whatsapp"))
                    val imageUrl = c.getString(c.getColumnIndexOrThrow("image_url"))
                    val schedule = c.getString(c.getColumnIndexOrThrow("schedule"))
                    val coordinates = c.getString(c.getColumnIndexOrThrow("coordinates"))
                    val servicesRaw = c.getString(c.getColumnIndexOrThrow("services"))
                    val openNowInt = c.getInt(c.getColumnIndexOrThrow("open_now"))
                    val pointsMul = c.getDouble(c.getColumnIndexOrThrow("points_multiplier"))

                    val services = if (servicesRaw.isNotBlank()) servicesRaw.split(";").map(String::trim) else emptyList()

                    list.add(
                        Tenant(
                            id = id,
                            name = name,
                            industry = industry,
                            description = description,
                            address = address,
                            contactPhone = phone,
                            whatsapp = whatsapp,
                            coverUrl = imageUrl,
                            imageUrl = imageUrl,
                            schedule = schedule,
                            geoCoordinates = coordinates,
                            services = services,
                            openNow = openNowInt == 1,
                            pointsMultiplier = pointsMul
                        )
                    )
                }
            }
        }
        _savedTenants.value = list
    }

    companion object {
        private const val DATABASE_NAME = "sancarlina_offline_guide.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_SAVED = "saved_merchants"
    }
}

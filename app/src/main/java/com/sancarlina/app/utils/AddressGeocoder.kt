package com.sancarlina.app.utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import androidx.annotation.RequiresApi
import java.util.Locale
import kotlin.coroutines.resume
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

data class GeocodedAddress(
    val latitude: Double,
    val longitude: Double,
    val displayName: String?
)

interface AddressGeocoder {
    suspend fun find(query: String): GeocodedAddress?
}

class AndroidAddressGeocoder(context: Context) : AddressGeocoder {
    private val applicationContext = context.applicationContext

    override suspend fun find(query: String): GeocodedAddress? {
        if (query.isBlank() || !Geocoder.isPresent()) return null
        val geocoder = Geocoder(applicationContext, Locale.forLanguageTag("es-AR"))
        val addresses = withTimeoutOrNull(GEOCODER_TIMEOUT_MS) {
            runCatching {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    findModern(geocoder, query)
                } else {
                    findLegacy(geocoder, query)
                }
            }.getOrElse { emptyList() }
        }.orEmpty()

        return addresses.firstOrNull { address ->
            address.hasLatitude() &&
                address.hasLongitude() &&
                address.latitude.isFinite() &&
                address.longitude.isFinite()
        }?.let { address ->
            GeocodedAddress(
                latitude = address.latitude,
                longitude = address.longitude,
                displayName = runCatching { address.getAddressLine(0) }
                    .getOrNull()
                    ?.takeIf(String::isNotBlank)
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private suspend fun findModern(geocoder: Geocoder, query: String): List<Address> =
        suspendCancellableCoroutine { continuation ->
            geocoder.getFromLocationName(
                query,
                1,
                object : Geocoder.GeocodeListener {
                    override fun onGeocode(addresses: MutableList<Address>) {
                        if (continuation.isActive) continuation.resume(addresses)
                    }

                    override fun onError(errorMessage: String?) {
                        if (continuation.isActive) continuation.resume(emptyList())
                    }
                }
            )
        }

    @Suppress("DEPRECATION")
    private suspend fun findLegacy(geocoder: Geocoder, query: String): List<Address> =
        withContext(Dispatchers.IO) {
            geocoder.getFromLocationName(query, 1).orEmpty()
        }

    private companion object {
        const val GEOCODER_TIMEOUT_MS = 10_000L
    }
}

package com.sancarlina.app.utils

import android.annotation.SuppressLint
import android.location.Location
import android.os.Handler
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority

private const val LOCATION_CAPTURE_DURATION_MS = 18_000L
private const val LOCATION_CAPTURE_TIMEOUT_MS = 18_500L
private const val TARGET_ACCURACY_METERS = 25f

/**
 * Obtiene varias lecturas y entrega la de mejor precisión, en vez de depender de
 * una única lectura que puede ser nula o antigua.
 */
@SuppressLint("MissingPermission")
fun captureBestLocation(
    client: FusedLocationProviderClient,
    onProgress: (Location) -> Unit,
    onResult: (Location) -> Unit,
    onError: (String) -> Unit
): () -> Unit {
    var bestLocation: Location? = null
    var finished = false
    val handler = Handler(Looper.getMainLooper())
    lateinit var callback: LocationCallback
    lateinit var timeout: Runnable

    fun finish(error: String? = null, deliverResult: Boolean = true) {
        if (finished) return
        finished = true
        handler.removeCallbacks(timeout)
        client.removeLocationUpdates(callback)

        if (!deliverResult) return
        val result = bestLocation
        if (result != null) {
            onResult(result)
        } else {
            onError(
                error
                    ?: "No se obtuvo una ubicación. Verificá que el GPS esté activo e intentá nuevamente."
            )
        }
    }

    callback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            result.locations.forEach { reading ->
                val readingAccuracy = reading.validAccuracyOrMax()
                val bestAccuracy = bestLocation?.validAccuracyOrMax() ?: Float.MAX_VALUE
                if (bestLocation == null || readingAccuracy < bestAccuracy) {
                    bestLocation = reading
                    onProgress(reading)
                }
            }

            val best = bestLocation
            if (best != null && best.hasAccuracy() && best.accuracy <= TARGET_ACCURACY_METERS) {
                finish()
            }
        }
    }

    timeout = Runnable {
        finish("El GPS tardó demasiado. Reintentá en un lugar con mejor señal.")
    }

    val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1_000L)
        .setMinUpdateIntervalMillis(500L)
        .setDurationMillis(LOCATION_CAPTURE_DURATION_MS)
        .setMaxUpdates(12)
        .build()

    return try {
        client.requestLocationUpdates(request, callback, Looper.getMainLooper())
            .addOnFailureListener { error ->
                finish(error.localizedMessage ?: "No se pudo iniciar la ubicación.")
            }
        handler.postDelayed(timeout, LOCATION_CAPTURE_TIMEOUT_MS)
        val cancelCapture: () -> Unit = { finish(deliverResult = false) }
        cancelCapture
    } catch (_: SecurityException) {
        finished = true
        onError("No se concedieron permisos de ubicación.")
        val noOp: () -> Unit = {}
        noOp
    } catch (error: Exception) {
        finished = true
        onError(error.localizedMessage ?: "No se pudo iniciar la ubicación.")
        val noOp: () -> Unit = {}
        noOp
    }
}

private fun Location.validAccuracyOrMax(): Float =
    accuracy.takeIf { hasAccuracy() && it.isFinite() && it >= 0f } ?: Float.MAX_VALUE

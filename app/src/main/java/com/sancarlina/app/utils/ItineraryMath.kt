package com.sancarlina.app.utils

import kotlin.math.*

object ItineraryMath {
    fun haversineKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        if (!lat1.isFinite() || !lon1.isFinite() || !lat2.isFinite() || !lon2.isFinite()) return 0.0
        if ((lat1 == 0.0 && lon1 == 0.0) || (lat2 == 0.0 && lon2 == 0.0)) return 0.0
        val earthRadius = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val value = sin(dLat / 2).pow(2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
        return earthRadius * 2 * atan2(sqrt(value), sqrt(1 - value))
    }

    fun estimatedMinutes(distanceKm: Double, stops: Int): Int {
        val drivingMinutes = if (distanceKm > 0) (distanceKm / 45.0 * 60.0).roundToInt() else 0
        return drivingMinutes + stops.coerceAtLeast(0) * 60
    }
}

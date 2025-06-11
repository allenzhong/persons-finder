package com.persons.finder.domain.utils

import kotlin.math.*

object DistanceCalculator {
    
    private const val EARTH_RADIUS_KM = 6371.0
    
    /**
     * Calculate the distance between two points on Earth using the Haversine formula
     * @param lat1 Latitude of first point in degrees
     * @param lon1 Longitude of first point in degrees
     * @param lat2 Latitude of second point in degrees
     * @param lon2 Longitude of second point in degrees
     * @return Distance in kilometers
     */
    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return EARTH_RADIUS_KM * c
    }
    
    /**
     * Calculate bounding box coordinates for efficient filtering
     * @param centerLat Center latitude
     * @param centerLon Center longitude
     * @param radiusKm Radius in kilometers
     * @return Pair of (minLat, maxLat, minLon, maxLon)
     */
    fun calculateBoundingBox(centerLat: Double, centerLon: Double, radiusKm: Double): BoundingBox {
        val latDelta = radiusKm / 111.0
        val lonDelta = radiusKm / (111.0 * cos(Math.toRadians(centerLat)))
        
        return BoundingBox(
            minLat = centerLat - latDelta,
            maxLat = centerLat + latDelta,
            minLon = centerLon - lonDelta,
            maxLon = centerLon + lonDelta
        )
    }
}

data class BoundingBox(
    val minLat: Double,
    val maxLat: Double,
    val minLon: Double,
    val maxLon: Double
) 
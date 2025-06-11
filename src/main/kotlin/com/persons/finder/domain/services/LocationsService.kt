package com.persons.finder.domain.services

import com.persons.finder.domain.models.Location

data class BoundingBox(
    val minLat: Double,
    val maxLat: Double,
    val minLon: Double,
    val maxLon: Double
)

interface LocationsService {
    fun addLocation(location: Location)
    fun removeLocation(locationReferenceId: Long)
    fun findAround(latitude: Double, longitude: Double, radiusInKm: Double) : List<Location>
    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double
    fun calculateBoundingBox(centerLat: Double, centerLon: Double, radiusKm: Double): BoundingBox
}
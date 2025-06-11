package com.persons.finder.domain.services

import com.persons.finder.domain.models.Location
import com.persons.finder.domain.models.Person
import com.persons.finder.domain.utils.DistanceCalculator
import com.persons.finder.infrastructure.repositories.LocationRepository
import org.springframework.stereotype.Service

@Service
class LocationsServiceImpl(
    private val locationRepository: LocationRepository
) : LocationsService {

    override fun addLocation(location: Location) {
        val existingLocation = locationRepository.findByReferenceId(location.referenceId)
        if (existingLocation != null) {
            locationRepository.updateLocation(
                referenceId = location.referenceId,
                latitude = location.latitude,
                longitude = location.longitude
            )
        } else {
            locationRepository.insertLocation(
                referenceId = location.referenceId,
                latitude = location.latitude,
                longitude = location.longitude
            )
        }
    }

    override fun removeLocation(locationReferenceId: Long) {
        TODO("Not yet implemented")
    }

    /**
     * Returns a list of raw Location objects within the given radius of the specified coordinates.
     * This method does NOT perform any mapping or DTO conversion. Mapping should be done in the controller.
     */
    override fun findAround(latitude: Double, longitude: Double, radiusInKm: Double): List<Location> {
        // Calculate bounding box for efficient filtering
        val boundingBox = DistanceCalculator.calculateBoundingBox(latitude, longitude, radiusInKm)
        
        // Get locations within the bounding box
        val locations = locationRepository.findLocationsInBoundingBox(
            minLat = boundingBox.minLat,
            maxLat = boundingBox.maxLat,
            minLon = boundingBox.minLon,
            maxLon = boundingBox.maxLon
        )
        
        // Calculate exact distances and filter by radius, then sort by distance
        return locations
            .map { location ->
                val distance = DistanceCalculator.calculateDistance(
                    lat1 = latitude,
                    lon1 = longitude,
                    lat2 = location.latitude,
                    lon2 = location.longitude
                )
                
                LocationWithDistance(location, distance)
            }
            .filter { it.distanceKm <= radiusInKm }
            .sortedBy { it.distanceKm }
            .map { it.location }
    }
}

data class LocationWithDistance(
    val location: Location,
    val distanceKm: Double
)
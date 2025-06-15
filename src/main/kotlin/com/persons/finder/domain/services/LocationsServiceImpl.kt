package com.persons.finder.domain.services

import com.persons.finder.domain.models.Location
import com.persons.finder.domain.models.Person
import com.persons.finder.infrastructure.config.GeoConfig
import com.persons.finder.infrastructure.repositories.LocationRepository
import com.persons.finder.infrastructure.repositories.dto.PersonLocationDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import kotlin.math.*

@Service
class LocationsServiceImpl : LocationsService {
    @Autowired
    internal lateinit var locationRepository: LocationRepository

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

    override fun findPersonsWithLocationsAroundPaginated(
        latitude: Double, 
        longitude: Double, 
        radiusInKm: Double,
        page: Int,
        pageSize: Int
    ): PaginatedPersonLocationResult {
        // Calculate bounding box for efficient filtering
        val boundingBox = calculateBoundingBox(latitude, longitude, radiusInKm)
        
        // Calculate offset for pagination
        val offset = (page - 1) * pageSize
        
        // Get total count first
        val totalCount = locationRepository.countPersonsWithLocationsInBoundingBox(
            minLat = boundingBox.minLat,
            maxLat = boundingBox.maxLat,
            minLon = boundingBox.minLon,
            maxLon = boundingBox.maxLon
        )
        
        // Get paginated persons with locations within the bounding box
        val persons = locationRepository.findPersonsWithLocationsInBoundingBoxPaginated(
            minLat = boundingBox.minLat,
            maxLat = boundingBox.maxLat,
            minLon = boundingBox.minLon,
            maxLon = boundingBox.maxLon,
            limit = pageSize,
            offset = offset
        )
        
        return PaginatedPersonLocationResult(persons = persons, totalCount = totalCount)
    }

    override fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return GeoConfig.EARTH_RADIUS_KM * c
    }

    override fun calculateBoundingBox(centerLat: Double, centerLon: Double, radiusKm: Double): BoundingBox {
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
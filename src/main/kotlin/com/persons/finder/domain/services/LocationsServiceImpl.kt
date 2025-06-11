package com.persons.finder.domain.services

import com.persons.finder.domain.models.Location
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

    override fun findAround(latitude: Double, longitude: Double, radiusInKm: Double): List<Location> {
        // TODO: Implement distance calculation logic
        // For now, return all locations (this will be implemented with proper distance calculation)
        return locationRepository.findAll()
    }
}
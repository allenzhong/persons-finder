package com.persons.finder.application.usecases

import com.persons.finder.domain.services.LocationsService
import com.persons.finder.domain.services.PersonsService
import com.persons.finder.presentation.dto.mapper.PersonMapper
import com.persons.finder.presentation.dto.response.PersonWithDistanceResponseDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class GetNearbyPersonsUseCaseImpl : GetNearbyPersonsUseCase {
    @Autowired
    internal lateinit var locationsService: LocationsService

    @Autowired
    internal lateinit var personsService: PersonsService

    override fun execute(lat: Double, lon: Double, radiusKm: Double): List<PersonWithDistanceResponseDto> {
        // Get locations within bounding box (service layer only handles data access)
        val locations = locationsService.findAround(lat, lon, radiusKm)
        if (locations.isEmpty()) {
            return emptyList()
        }

        // Calculate distances and filter by radius (business logic in use case layer)
        val locationsWithDistances = locations
            .map { location ->
                val distance = locationsService.calculateDistance(
                    lat1 = lat,
                    lon1 = lon,
                    lat2 = location.latitude,
                    lon2 = location.longitude
                )
                location to distance
            }
            .filter { (_, distance) -> distance <= radiusKm }
            .sortedBy { (_, distance) -> distance }

        if (locationsWithDistances.isEmpty()) {
            return emptyList()
        }

        // Fetch person data for filtered locations
        val personIds = locationsWithDistances.map { (location, _) -> location.referenceId }
        val persons = personsService.getByIds(personIds)
        val personMap = persons.associateBy { it.id }

        // Create response DTOs
        return locationsWithDistances
            .mapNotNull { (location, distance) ->
                val person = personMap[location.referenceId]
                if (person != null) {
                    PersonWithDistanceResponseDto(
                        person = PersonMapper.toResponseDto(person),
                        distanceKm = distance
                    )
                } else null
            }
    }
} 
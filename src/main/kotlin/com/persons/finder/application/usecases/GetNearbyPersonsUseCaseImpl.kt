package com.persons.finder.application.usecases

import com.persons.finder.domain.services.LocationsService
import com.persons.finder.presentation.dto.response.PersonResponseDto
import com.persons.finder.presentation.dto.response.PersonWithDistanceResponseDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class GetNearbyPersonsUseCaseImpl : GetNearbyPersonsUseCase {
    @Autowired
    internal lateinit var locationsService: LocationsService

    override fun execute(lat: Double, lon: Double, radiusKm: Double): List<PersonWithDistanceResponseDto> {
        // Get persons with locations within bounding box using join query (single database call)
        val personsWithLocations = locationsService.findPersonsWithLocationsAround(lat, lon, radiusKm)
        if (personsWithLocations.isEmpty()) {
            return emptyList()
        }

        // Calculate distances and filter by radius (business logic in use case layer)
        val personsWithDistances = personsWithLocations
            .map { personLocation ->
                val distance = locationsService.calculateDistance(
                    lat1 = lat,
                    lon1 = lon,
                    lat2 = personLocation.latitude,
                    lon2 = personLocation.longitude
                )
                personLocation to distance
            }
            .filter { (_, distance) -> distance <= radiusKm }
            .sortedBy { (_, distance) -> distance }

        if (personsWithDistances.isEmpty()) {
            return emptyList()
        }

        // Create response DTOs
        return personsWithDistances
            .map { (personLocation, distance) ->
                PersonWithDistanceResponseDto(
                    person = PersonResponseDto(
                        id = personLocation.id,
                        name = personLocation.name
                    ),
                    distanceKm = distance
                )
            }
    }
} 
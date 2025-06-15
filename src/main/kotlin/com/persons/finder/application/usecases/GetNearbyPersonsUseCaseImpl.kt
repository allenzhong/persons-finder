package com.persons.finder.application.usecases

import com.persons.finder.domain.services.LocationsService
import com.persons.finder.presentation.dto.response.PaginatedResponseDto
import com.persons.finder.presentation.dto.response.PaginationInfoDto
import com.persons.finder.presentation.dto.response.PersonResponseDto
import com.persons.finder.presentation.dto.response.PersonWithDistanceResponseDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class GetNearbyPersonsUseCaseImpl : GetNearbyPersonsUseCase {
    @Autowired
    internal lateinit var locationsService: LocationsService

    override fun execute(lat: Double, lon: Double, radiusKm: Double, page: Int, pageSize: Int): PaginatedResponseDto<PersonWithDistanceResponseDto> {
        // Use paginated service method to prevent loading all records into memory
        val paginatedResult = locationsService.findPersonsWithLocationsAroundPaginated(lat, lon, radiusKm, page, pageSize)
        
        if (paginatedResult.persons.isEmpty()) {
            return createEmptyPaginatedResponse(page, pageSize)
        }

        // Calculate distances and filter by radius (business logic in use case layer)
        val personsWithDistances = paginatedResult.persons
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
            return createEmptyPaginatedResponse(page, pageSize)
        }

        // Create response DTOs
        val responseData = personsWithDistances
            .map { (personLocation, distance) ->
                PersonWithDistanceResponseDto(
                    person = PersonResponseDto(
                        id = personLocation.id,
                        name = personLocation.name
                    ),
                    distanceKm = distance
                )
            }

        // Calculate pagination info based on the total count from database
        val totalItems = paginatedResult.totalCount
        val totalPages = ((totalItems + pageSize - 1) / pageSize).toInt()

        return PaginatedResponseDto(
            data = responseData,
            pagination = PaginationInfoDto(
                page = page,
                pageSize = pageSize,
                totalItems = totalItems,
                totalPages = totalPages,
                hasNext = page < totalPages,
                hasPrevious = page > 1
            )
        )
    }

    private fun createEmptyPaginatedResponse(page: Int, pageSize: Int): PaginatedResponseDto<PersonWithDistanceResponseDto> {
        return PaginatedResponseDto(
            data = emptyList(),
            pagination = PaginationInfoDto(
                page = page,
                pageSize = pageSize,
                totalItems = 0L,
                totalPages = 0,
                hasNext = false,
                hasPrevious = false
            )
        )
    }
} 
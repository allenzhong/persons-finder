package com.persons.finder.application.usecases

import com.persons.finder.domain.services.LocationsService
import com.persons.finder.domain.services.PersonsService
import com.persons.finder.presentation.dto.mapper.LocationMapper
import com.persons.finder.presentation.dto.request.UpdateLocationRequestDto
import com.persons.finder.presentation.dto.response.LocationResponseDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class UpdatePersonLocationUseCaseImpl : UpdatePersonLocationUseCase {
    @Autowired
    internal lateinit var personsService: PersonsService

    @Autowired
    internal lateinit var locationsService: LocationsService

    override fun execute(personId: Long, request: UpdateLocationRequestDto): LocationResponseDto {
        // Verify person exists
        personsService.getById(personId)
        
        // Create location and save it
        val location = LocationMapper.toDomain(personId, request)
        locationsService.addLocation(location)
        
        // Return response
        return LocationMapper.toResponseDto(location)
    }
} 
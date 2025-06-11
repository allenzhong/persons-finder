package com.persons.finder.presentation.dto.mapper

import com.persons.finder.domain.models.Location
import com.persons.finder.presentation.dto.request.UpdateLocationRequestDto
import com.persons.finder.presentation.dto.response.LocationResponseDto

object LocationMapper {
    
    fun toDomain(referenceId: Long, updateLocationRequestDto: UpdateLocationRequestDto): Location {
        return Location(
            referenceId = referenceId,
            latitude = updateLocationRequestDto.latitude!!,
            longitude = updateLocationRequestDto.longitude!!
        )
    }
    
    fun toResponseDto(location: Location): LocationResponseDto {
        return LocationResponseDto(
            referenceId = location.referenceId,
            latitude = location.latitude,
            longitude = location.longitude
        )
    }
} 
package com.persons.finder.application.usecases

import com.persons.finder.presentation.dto.response.PaginatedResponseDto
import com.persons.finder.presentation.dto.response.PersonWithDistanceResponseDto

interface GetNearbyPersonsUseCase {
    fun execute(lat: Double, lon: Double, radiusKm: Double, page: Int = 1, pageSize: Int = 500): PaginatedResponseDto<PersonWithDistanceResponseDto>
} 
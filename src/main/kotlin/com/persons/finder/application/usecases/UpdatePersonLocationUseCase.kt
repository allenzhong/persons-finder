package com.persons.finder.application.usecases

import com.persons.finder.presentation.dto.request.UpdateLocationRequestDto
import com.persons.finder.presentation.dto.response.LocationResponseDto

interface UpdatePersonLocationUseCase {
    fun execute(personId: Long, request: UpdateLocationRequestDto): LocationResponseDto
} 
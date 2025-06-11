package com.persons.finder.application.usecases

import com.persons.finder.presentation.dto.request.CreatePersonRequestDto
import com.persons.finder.presentation.dto.response.PersonResponseDto

interface CreatePersonUseCase {
    fun execute(request: CreatePersonRequestDto): PersonResponseDto
} 
package com.persons.finder.application.usecases

import com.persons.finder.presentation.dto.response.PersonResponseDto

interface GetPersonsByIdsUseCase {
    fun execute(ids: List<Long>): List<PersonResponseDto>
} 
package com.persons.finder.application.usecases

import com.persons.finder.domain.services.PersonsService
import com.persons.finder.presentation.dto.mapper.PersonMapper
import com.persons.finder.presentation.dto.response.PersonResponseDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class GetPersonsByIdsUseCaseImpl : GetPersonsByIdsUseCase {
    @Autowired
    internal lateinit var personsService: PersonsService

    override fun execute(ids: List<Long>): List<PersonResponseDto> {
        val persons = personsService.getByIds(ids)
        return persons.map { PersonMapper.toResponseDto(it) }
    }
} 
package com.persons.finder.application.usecases

import com.persons.finder.domain.models.Person
import com.persons.finder.domain.services.PersonsService
import com.persons.finder.presentation.dto.mapper.PersonMapper
import com.persons.finder.presentation.dto.request.CreatePersonRequestDto
import com.persons.finder.presentation.dto.response.PersonResponseDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class CreatePersonUseCaseImpl : CreatePersonUseCase {
    @Autowired
    internal lateinit var personsService: PersonsService

    override fun execute(request: CreatePersonRequestDto): PersonResponseDto {
        val person = PersonMapper.toDomain(request)
        val createdPerson = personsService.save(person)
        return PersonMapper.toResponseDto(createdPerson)
    }
} 
package com.persons.finder.presentation.dto.mapper

import com.persons.finder.domain.models.Person
import com.persons.finder.presentation.dto.request.CreatePersonRequestDto
import com.persons.finder.presentation.dto.response.PersonResponseDto

object PersonMapper {
    
    fun toDomain(createPersonRequestDto: CreatePersonRequestDto): Person {
        return Person(
            name = createPersonRequestDto.name
        )
    }
    
    fun toResponseDto(person: Person): PersonResponseDto {
        return PersonResponseDto(
            id = person.id ?: 0L,
            name = person.name
        )
    }
}
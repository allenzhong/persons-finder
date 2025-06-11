package com.persons.finder.presentation.dto.mapper

import com.persons.finder.domain.models.Person
import com.persons.finder.presentation.dto.request.CreatePersonRequestDto
import com.persons.finder.presentation.dto.response.PersonResponseDto

object PersonMapper {
    
    fun toDomain(dto: CreatePersonRequestDto): Person {
        return Person(
            name = dto.name
        )
    }
    
    fun toResponseDto(person: Person): PersonResponseDto {
        return PersonResponseDto(
            id = person.id,
            name = person.name
        )
    }
}
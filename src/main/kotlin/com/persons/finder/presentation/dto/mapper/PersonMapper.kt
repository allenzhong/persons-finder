package com.persons.finder.presentation.dto.mapper

import com.persons.finder.data.Person
import com.persons.finder.presentation.dto.request.CreatePersonRequestDto
import com.persons.finder.presentation.dto.response.PersonResponseDto

object PersonMapper {
    
    fun toDomain(createPersonRequestDto: CreatePersonRequestDto): Person {
        return Person(
            id = 0L, // Assuming a new person has an ID of 0, to be set by the database
            name = createPersonRequestDto.name
        )
    }
    
    fun toResponseDto(person: Person): PersonResponseDto {
        return PersonResponseDto(
            id = person.id,
            name = person.name
        )
    }
}
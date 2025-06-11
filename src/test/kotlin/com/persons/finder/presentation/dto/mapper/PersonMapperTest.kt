package com.persons.finder.presentation.dto.mapper

import com.persons.finder.data.Person
import com.persons.finder.presentation.dto.request.CreatePersonRequestDto
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.Assertions.*

class PersonMapperTest {

    @Test
    fun `toDomain should map CreatePersonRequestDto to Person correctly`() {
        // Given
        val createPersonRequestDto = CreatePersonRequestDto(name = "John Doe")

        // When
        val result = PersonMapper.toDomain(createPersonRequestDto)

        // Then
        assertAll(
            { assertNotNull(result) },
            { assertEquals(0L, result.id) }, // Default value
            { assertEquals("John Doe", result.name) }
        )
    }

    @Test
    fun `toResponseDto should map Person to PersonResponseDto correctly`() {
        // Given
        val person = Person(id = 1L, name = "John Doe")

        // When
        val result = PersonMapper.toResponseDto(person)

        // Then
        assertAll(
            { assertNotNull(result) },
            { assertEquals(1L, result.id) },
            { assertEquals("John Doe", result.name) }
        )
    }
}
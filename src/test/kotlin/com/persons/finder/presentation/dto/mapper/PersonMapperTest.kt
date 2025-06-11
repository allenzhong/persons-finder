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
            { assertNull(result.id) }, // ID should be null for new person
            { assertEquals("John Doe", result.name) }
        )
    }

    @Test
    fun `toResponseDto should map Person to PersonResponseDto correctly`() {
        // Given
        val person = Person(name = "John Doe", id = 1L)

        // When
        val result = PersonMapper.toResponseDto(person)

        // Then
        assertAll(
            { assertNotNull(result) },
            { assertEquals(1L, result.id) },
            { assertEquals("John Doe", result.name) }
        )
    }

    @Test
    fun `toResponseDto should handle null id correctly`() {
        // Given
        val person = Person(name = "John Doe", id = null)

        // When
        val result = PersonMapper.toResponseDto(person)

        // Then
        assertAll(
            { assertNotNull(result) },
            { assertEquals(0L, result.id) }, // Should default to 0L when id is null
            { assertEquals("John Doe", result.name) }
        )
    }
}
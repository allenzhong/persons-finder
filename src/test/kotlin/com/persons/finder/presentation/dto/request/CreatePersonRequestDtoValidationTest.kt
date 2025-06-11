package com.persons.finder.presentation.dto.request

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.validation.Validation
import javax.validation.Validator

class CreatePersonRequestDtoValidationTest {

    private lateinit var validator: Validator

    @BeforeEach
    fun setUp() {
        validator = Validation.buildDefaultValidatorFactory().validator
    }

    @Test
    fun `should pass validation for valid name`() {
        // Given
        val dto = CreatePersonRequestDto(name = "Allen")

        // When
        val violations = validator.validate(dto)

        // Then
        assertEquals(0, violations.size, "Should have no validation violations")
    }

    @Test
    fun `should fail validation for empty name`() {
        // Given
        val dto = CreatePersonRequestDto(name = "")

        // When
        val violations = validator.validate(dto)

        // Then
        val messages = violations.map { it.message }
        assertTrue(messages.contains("Name is required"), "Should have NotBlank violation")
        assertTrue(messages.contains("Name must be between 1 and 100 characters"), "Should have Size violation")
    }

    @Test
    fun `should fail validation for blank name`() {
        // Given
        val dto = CreatePersonRequestDto(name = "   ")

        // When
        val violations = validator.validate(dto)

        // Then
        assertEquals(1, violations.size, "Should have one validation violation")
        assertEquals("Name is required", violations.first().message)
    }

    @Test
    fun `should fail validation for name with only tabs`() {
        // Given
        val dto = CreatePersonRequestDto(name = "\t\t")

        // When
        val violations = validator.validate(dto)

        // Then
        assertEquals(1, violations.size, "Should have one validation violation")
        assertEquals("Name is required", violations.first().message)
    }

    @Test
    fun `should fail validation for name with only newlines`() {
        // Given
        val dto = CreatePersonRequestDto(name = "\n\n")

        // When
        val violations = validator.validate(dto)

        // Then
        assertEquals(1, violations.size, "Should have one validation violation")
        assertEquals("Name is required", violations.first().message)
    }

    @Test
    fun `should pass validation for name with minimum length`() {
        // Given
        val dto = CreatePersonRequestDto(name = "A")

        // When
        val violations = validator.validate(dto)

        // Then
        assertEquals(0, violations.size, "Should have no validation violations")
    }

    @Test
    fun `should pass validation for name with maximum length`() {
        // Given
        val dto = CreatePersonRequestDto(name = "A".repeat(100))

        // When
        val violations = validator.validate(dto)

        // Then
        assertEquals(0, violations.size, "Should have no validation violations")
    }

    @Test
    fun `should fail validation for name exceeding maximum length`() {
        // Given
        val dto = CreatePersonRequestDto(name = "A".repeat(101))

        // When
        val violations = validator.validate(dto)

        // Then
        assertEquals(1, violations.size, "Should have one validation violation")
        assertEquals("Name must be between 1 and 100 characters", violations.first().message)
    }
}
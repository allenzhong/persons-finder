package com.persons.finder.application.usecases

import com.persons.finder.domain.models.Person
import com.persons.finder.domain.services.PersonsService
import com.persons.finder.presentation.dto.request.CreatePersonRequestDto
import com.persons.finder.presentation.dto.response.PersonResponseDto
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@ExtendWith(MockitoExtension::class)
class CreatePersonUseCaseImplTest {

    @Mock
    private lateinit var personsService: PersonsService

    private lateinit var createPersonUseCase: CreatePersonUseCaseImpl

    @BeforeEach
    fun setUp() {
        createPersonUseCase = CreatePersonUseCaseImpl()
        createPersonUseCase.personsService = personsService
    }

    @Test
    fun `execute should create person successfully`() {
        // Given
        val request = CreatePersonRequestDto(name = "John Doe")
        val createdPerson = Person(name = "John Doe", id = 1L)
        val expectedResponse = PersonResponseDto(id = 1L, name = "John Doe")

        whenever(personsService.save(any())).thenReturn(createdPerson)

        // When
        val result = createPersonUseCase.execute(request)

        // Then
        assertNotNull(result)
        assertEquals(expectedResponse.id, result.id)
        assertEquals(expectedResponse.name, result.name)
        verify(personsService).save(any())
    }

    @Test
    fun `execute should handle person with existing id`() {
        // Given
        val request = CreatePersonRequestDto(name = "Existing Person")
        val createdPerson = Person(name = "Existing Person", id = 123L)
        val expectedResponse = PersonResponseDto(id = 123L, name = "Existing Person")

        whenever(personsService.save(any())).thenReturn(createdPerson)

        // When
        val result = createPersonUseCase.execute(request)

        // Then
        assertNotNull(result)
        assertEquals(expectedResponse.id, result.id)
        assertEquals(expectedResponse.name, result.name)
        verify(personsService).save(any())
    }

    @Test
    fun `execute should handle empty name`() {
        // Given
        val request = CreatePersonRequestDto(name = "")
        val createdPerson = Person(name = "", id = 2L)
        val expectedResponse = PersonResponseDto(id = 2L, name = "")

        whenever(personsService.save(any())).thenReturn(createdPerson)

        // When
        val result = createPersonUseCase.execute(request)

        // Then
        assertNotNull(result)
        assertEquals(expectedResponse.id, result.id)
        assertEquals(expectedResponse.name, result.name)
        verify(personsService).save(any())
    }
} 
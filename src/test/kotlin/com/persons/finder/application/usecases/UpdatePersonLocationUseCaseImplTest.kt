package com.persons.finder.application.usecases

import com.persons.finder.domain.models.Person
import com.persons.finder.domain.services.LocationsService
import com.persons.finder.domain.services.PersonsService
import com.persons.finder.presentation.dto.mapper.LocationMapper
import com.persons.finder.presentation.dto.request.UpdateLocationRequestDto
import com.persons.finder.presentation.dto.response.LocationResponseDto
import com.persons.finder.presentation.exceptions.PersonNotFoundException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@ExtendWith(MockitoExtension::class)
class UpdatePersonLocationUseCaseImplTest {

    @Mock
    private lateinit var personsService: PersonsService

    @Mock
    private lateinit var locationsService: LocationsService

    private lateinit var updatePersonLocationUseCase: UpdatePersonLocationUseCaseImpl

    @BeforeEach
    fun setUp() {
        updatePersonLocationUseCase = UpdatePersonLocationUseCaseImpl()
        updatePersonLocationUseCase.apply {
            this.personsService = this@UpdatePersonLocationUseCaseImplTest.personsService
            this.locationsService = this@UpdatePersonLocationUseCaseImplTest.locationsService
        }
    }

    @Test
    fun `execute should update location successfully when person exists`() {
        // Given
        val personId = 1L
        val request = UpdateLocationRequestDto(latitude = 40.7128, longitude = -74.0060)
        val person = Person(name = "John Doe", id = personId)
        
        whenever(personsService.getById(personId)).thenReturn(person)

        // When
        val result = updatePersonLocationUseCase.execute(personId, request)

        // Then
        assertEquals(personId, result.referenceId)
        assertEquals(request.latitude, result.latitude)
        assertEquals(request.longitude, result.longitude)
        verify(personsService).getById(personId)
        verify(locationsService).addLocation(any())
    }

    @Test
    fun `execute should throw PersonNotFoundException when person does not exist`() {
        // Given
        val personId = 999L
        val request = UpdateLocationRequestDto(latitude = 40.7128, longitude = -74.0060)
        
        whenever(personsService.getById(personId)).thenThrow(PersonNotFoundException(personId))

        // When & Then
        assertThrows<PersonNotFoundException> {
            updatePersonLocationUseCase.execute(personId, request)
        }
        verify(personsService).getById(personId)
    }

    @Test
    fun `execute should handle different coordinate values`() {
        // Given
        val personId = 2L
        val request = UpdateLocationRequestDto(latitude = -33.8688, longitude = 151.2093)
        val person = Person(name = "Jane Doe", id = personId)
        
        whenever(personsService.getById(personId)).thenReturn(person)

        // When
        val result = updatePersonLocationUseCase.execute(personId, request)

        // Then
        assertEquals(personId, result.referenceId)
        assertEquals(-33.8688, result.latitude)
        assertEquals(151.2093, result.longitude)
        verify(personsService).getById(personId)
        verify(locationsService).addLocation(any())
    }

    @Test
    fun `execute should handle edge case coordinates`() {
        // Given
        val personId = 3L
        val request = UpdateLocationRequestDto(latitude = -90.0, longitude = -180.0)
        val person = Person(name = "Edge Case", id = personId)
        
        whenever(personsService.getById(personId)).thenReturn(person)

        // When
        val result = updatePersonLocationUseCase.execute(personId, request)

        // Then
        assertEquals(personId, result.referenceId)
        assertEquals(-90.0, result.latitude)
        assertEquals(-180.0, result.longitude)
        verify(personsService).getById(personId)
        verify(locationsService).addLocation(any())
    }

    @Test
    fun `execute should handle maximum coordinate values`() {
        // Given
        val personId = 4L
        val request = UpdateLocationRequestDto(latitude = 90.0, longitude = 180.0)
        val person = Person(name = "Max Values", id = personId)
        
        whenever(personsService.getById(personId)).thenReturn(person)

        // When
        val result = updatePersonLocationUseCase.execute(personId, request)

        // Then
        assertEquals(personId, result.referenceId)
        assertEquals(90.0, result.latitude)
        assertEquals(180.0, result.longitude)
        verify(personsService).getById(personId)
        verify(locationsService).addLocation(any())
    }
} 
package com.persons.finder.application.usecases

import com.persons.finder.domain.models.Person
import com.persons.finder.domain.services.LocationsService
import com.persons.finder.domain.services.PersonsService
import com.persons.finder.presentation.dto.mapper.LocationMapper
import com.persons.finder.presentation.dto.request.UpdateLocationRequestDto
import com.persons.finder.presentation.dto.response.LocationResponseDto
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
class UpdatePersonLocationUseCaseImplTest {

    @Mock
    private lateinit var personsService: PersonsService

    @Mock
    private lateinit var locationsService: LocationsService

    private lateinit var updatePersonLocationUseCase: UpdatePersonLocationUseCaseImpl

    @BeforeEach
    fun setUp() {
        updatePersonLocationUseCase = UpdatePersonLocationUseCaseImpl()
        updatePersonLocationUseCase.personsService = personsService
        updatePersonLocationUseCase.locationsService = locationsService
    }

    @Test
    fun `execute should update location and return response`() {
        // Given
        val personId = 1L
        val updateLocationRequestDto = UpdateLocationRequestDto(latitude = 40.7128, longitude = -74.0060)
        val person = Person(name = "John Doe", id = personId)
        val expectedLocation = LocationMapper.toDomain(personId, updateLocationRequestDto)
        val expectedResponse = LocationMapper.toResponseDto(expectedLocation)

        whenever(personsService.getById(personId)).thenReturn(person)

        // When
        val result = updatePersonLocationUseCase.execute(personId, updateLocationRequestDto)

        // Then
        assertNotNull(result)
        assertEquals(expectedResponse.referenceId, result.referenceId)
        assertEquals(expectedResponse.latitude, result.latitude)
        assertEquals(expectedResponse.longitude, result.longitude)
        verify(personsService).getById(personId)
        verify(locationsService).addLocation(any())
    }
} 
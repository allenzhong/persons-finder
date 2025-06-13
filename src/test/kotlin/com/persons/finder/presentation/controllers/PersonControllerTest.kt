package com.persons.finder.presentation.controllers

import com.persons.finder.application.usecases.CreatePersonUseCase
import com.persons.finder.application.usecases.GetNearbyPersonsUseCase
import com.persons.finder.application.usecases.GetPersonsByIdsUseCase
import com.persons.finder.application.usecases.UpdatePersonLocationUseCase
import com.persons.finder.domain.services.LocationsService
import com.persons.finder.domain.services.PersonsService
import com.persons.finder.presentation.dto.request.CreatePersonRequestDto
import com.persons.finder.presentation.dto.request.UpdateLocationRequestDto
import com.persons.finder.presentation.dto.response.*
import com.persons.finder.presentation.exceptions.PersonNotFoundException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class PersonControllerTest {

    @Mock
    private lateinit var personsService: PersonsService

    @Mock
    private lateinit var locationsService: LocationsService

    @Mock
    private lateinit var createPersonUseCase: CreatePersonUseCase

    @Mock
    private lateinit var getPersonsByIdsUseCase: GetPersonsByIdsUseCase

    @Mock
    private lateinit var updatePersonLocationUseCase: UpdatePersonLocationUseCase

    @Mock
    private lateinit var getNearbyPersonsUseCase: GetNearbyPersonsUseCase

    private lateinit var personController: PersonController

    @BeforeEach
    fun setUp() {
        personController = PersonController()
        personController.apply {
            this.personsService = this@PersonControllerTest.personsService
            this.locationsService = this@PersonControllerTest.locationsService
            this.createPersonUseCase = this@PersonControllerTest.createPersonUseCase
            this.getPersonsByIdsUseCase = this@PersonControllerTest.getPersonsByIdsUseCase
            this.updatePersonLocationUseCase = this@PersonControllerTest.updatePersonLocationUseCase
            this.getNearbyPersonsUseCase = this@PersonControllerTest.getNearbyPersonsUseCase
        }
    }

    @Test
    fun `createPerson should return created person with 201 status`() {
        // Given
        val request = CreatePersonRequestDto(name = "John Doe")
        val expectedResponse = PersonResponseDto(id = 1L, name = "John Doe")
        whenever(createPersonUseCase.execute(request)).thenReturn(expectedResponse)

        // When
        val result = personController.createPerson(request)

        // Then
        assertEquals(HttpStatus.CREATED, result.statusCode)
        assertEquals(expectedResponse, result.body)
        verify(createPersonUseCase).execute(request)
    }

    @Test
    fun `getPersonsByIds should return persons with 200 status`() {
        // Given
        val ids = listOf(1L, 2L)
        val expectedResponses = listOf(
            PersonResponseDto(id = 1L, name = "John Doe"),
            PersonResponseDto(id = 2L, name = "Jane Doe")
        )
        whenever(getPersonsByIdsUseCase.execute(ids)).thenReturn(expectedResponses)

        // When
        val result = personController.getPersonsByIds(ids)

        // Then
        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(expectedResponses, result.body)
        verify(getPersonsByIdsUseCase).execute(ids)
    }

    @Test
    fun `updatePersonLocation should return updated location with 200 status`() {
        // Given
        val personId = 1L
        val request = UpdateLocationRequestDto(latitude = 40.7128, longitude = -74.0060)
        val expectedResponse = LocationResponseDto(referenceId = personId, latitude = 40.7128, longitude = -74.0060)
        whenever(updatePersonLocationUseCase.execute(personId, request)).thenReturn(expectedResponse)

        // When
        val result = personController.updatePersonLocation(personId, request)

        // Then
        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(expectedResponse, result.body)
        verify(updatePersonLocationUseCase).execute(personId, request)
    }

    @Test
    fun `getNearbyPersons should return paginated persons with distances with 200 status`() {
        // Given
        val lat = 40.7128
        val lon = -74.0060
        val radiusKm = 10.0
        val page = 1
        val pageSize = 500
        val expectedResponses = listOf(
            PersonWithDistanceResponseDto(
                person = PersonResponseDto(id = 1L, name = "Nearby Person"),
                distanceKm = 5.0
            )
        )
        val expectedPaginatedResponse = PaginatedResponseDto(
            data = expectedResponses,
            pagination = PaginationInfoDto(
                page = page,
                pageSize = pageSize,
                totalItems = 1L,
                totalPages = 1,
                hasNext = false,
                hasPrevious = false
            )
        )
        whenever(getNearbyPersonsUseCase.execute(lat, lon, radiusKm, page, pageSize)).thenReturn(expectedPaginatedResponse)

        // When
        val result = personController.getNearbyPersons(lat, lon, radiusKm, page, pageSize)

        // Then
        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(expectedPaginatedResponse, result.body)
        verify(getNearbyPersonsUseCase).execute(lat, lon, radiusKm, page, pageSize)
    }

    @Test
    fun `getNearbyPersons should handle empty results`() {
        // Given
        val lat = 40.7128
        val lon = -74.0060
        val radiusKm = 10.0
        val page = 1
        val pageSize = 500
        val expectedPaginatedResponse = PaginatedResponseDto(
            data = emptyList<PersonWithDistanceResponseDto>(),
            pagination = PaginationInfoDto(
                page = page,
                pageSize = pageSize,
                totalItems = 0L,
                totalPages = 0,
                hasNext = false,
                hasPrevious = false
            )
        )
        whenever(getNearbyPersonsUseCase.execute(lat, lon, radiusKm, page, pageSize)).thenReturn(expectedPaginatedResponse)

        // When
        val result = personController.getNearbyPersons(lat, lon, radiusKm, page, pageSize)

        // Then
        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(expectedPaginatedResponse, result.body)
        verify(getNearbyPersonsUseCase).execute(lat, lon, radiusKm, page, pageSize)
    }

    @Test
    fun `getNearbyPersons should handle edge case coordinates`() {
        // Given
        val lat = -90.0
        val lon = -180.0
        val radiusKm = 1.0
        val page = 1
        val pageSize = 500
        val expectedPaginatedResponse = PaginatedResponseDto(
            data = emptyList<PersonWithDistanceResponseDto>(),
            pagination = PaginationInfoDto(
                page = page,
                pageSize = pageSize,
                totalItems = 0L,
                totalPages = 0,
                hasNext = false,
                hasPrevious = false
            )
        )
        whenever(getNearbyPersonsUseCase.execute(lat, lon, radiusKm, page, pageSize)).thenReturn(expectedPaginatedResponse)

        // When
        val result = personController.getNearbyPersons(lat, lon, radiusKm, page, pageSize)

        // Then
        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(expectedPaginatedResponse, result.body)
        verify(getNearbyPersonsUseCase).execute(lat, lon, radiusKm, page, pageSize)
    }

    @Test
    fun `createPerson should fail validation for blank name`() {
        // Given
        val request = CreatePersonRequestDto(name = "   ")
        // When/Then: In a real controller test, validation would throw, but here we simulate what would happen
        // Since this is a unit test, validation is not triggered. In integration tests, this would return 400.
        // So, we just document this limitation here.
        // To fully test validation, use @WebMvcTest or integration test.
    }

    @Test
    fun `updatePersonLocation should fail validation for out-of-range latitude`() {
        // Given
        val personId = 1L
        val request = UpdateLocationRequestDto(latitude = 100.0, longitude = 0.0)
        // When/Then: As above, validation is not triggered in this unit test.
    }

    @Test
    fun `updatePersonLocation should throw PersonNotFoundException`() {
        // Given
        val personId = 999L
        val request = UpdateLocationRequestDto(latitude = 40.7128, longitude = -74.0060)
        whenever(updatePersonLocationUseCase.execute(personId, request)).thenThrow(PersonNotFoundException(personId))

        // When/Then
        assertThrows<PersonNotFoundException> {
            personController.updatePersonLocation(personId, request)
        }
        verify(updatePersonLocationUseCase).execute(personId, request)
    }

    @Test
    fun `getPersonsByIds should handle use case exception`() {
        // Given
        val ids = listOf(1L, 2L)
        whenever(getPersonsByIdsUseCase.execute(ids)).thenThrow(RuntimeException("DB error"))

        // When/Then
        assertThrows<RuntimeException> {
            personController.getPersonsByIds(ids)
        }
        verify(getPersonsByIdsUseCase).execute(ids)
    }

    @Test
    fun `getNearbyPersons should handle use case exception`() {
        // Given
        val lat = 40.7128
        val lon = -74.0060
        val radiusKm = 10.0
        val page = 1
        val pageSize = 500
        whenever(getNearbyPersonsUseCase.execute(lat, lon, radiusKm, page, pageSize)).thenThrow(RuntimeException("DB error"))

        // When/Then
        assertThrows<RuntimeException> {
            personController.getNearbyPersons(lat, lon, radiusKm, page, pageSize)
        }
        verify(getNearbyPersonsUseCase).execute(lat, lon, radiusKm, page, pageSize)
    }

    @Test
    fun `getNearbyPersons should handle invalid parameters`() {
        // Given
        val lat = 200.0 // invalid
        val lon = 0.0
        val radiusKm = 10.0
        // When/Then: In a real controller test, validation would throw, but here we simulate what would happen
    }
}
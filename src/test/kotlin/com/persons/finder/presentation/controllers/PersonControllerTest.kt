package com.persons.finder.presentation.controllers

import com.persons.finder.domain.models.Location
import com.persons.finder.domain.models.Person
import com.persons.finder.domain.services.LocationsService
import com.persons.finder.domain.services.PersonsService
import com.persons.finder.application.usecases.CreatePersonUseCase
import com.persons.finder.application.usecases.GetPersonsByIdsUseCase
import com.persons.finder.application.usecases.UpdatePersonLocationUseCase
import com.persons.finder.application.usecases.GetNearbyPersonsUseCase
import com.persons.finder.presentation.dto.mapper.LocationMapper
import com.persons.finder.presentation.dto.mapper.PersonMapper
import com.persons.finder.presentation.dto.request.CreatePersonRequestDto
import com.persons.finder.presentation.dto.request.UpdateLocationRequestDto
import com.persons.finder.presentation.dto.response.LocationResponseDto
import com.persons.finder.presentation.dto.response.PersonResponseDto
import com.persons.finder.presentation.dto.response.PersonWithDistanceResponseDto
import com.persons.finder.presentation.exceptions.PersonNotFoundException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

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
    fun `createPerson should return 201 Created`() {
        // Given
        val createPersonRequestDto = CreatePersonRequestDto(name = "Allen")
        val expectedResponse = PersonResponseDto(id = 1L, name = "Allen")

        whenever(createPersonUseCase.execute(createPersonRequestDto)).thenReturn(expectedResponse)

        // When
        val response: ResponseEntity<PersonResponseDto> = personController.createPerson(createPersonRequestDto)

        // Then
        assertNotNull(response)
        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals(201, response.statusCodeValue)
        val responseBody = response.body
        assertNotNull(responseBody)
        assertEquals(expectedResponse.id, responseBody.id)
        assertEquals(expectedResponse.name, responseBody.name)
        verify(createPersonUseCase).execute(createPersonRequestDto)
    }

    @Test
    fun `createPerson should call useCase with correct person object`() {
        // Given
        val createPersonRequestDto = CreatePersonRequestDto(name = "Test Person")
        val expectedResponse = PersonResponseDto(id = 1L, name = "Test Person")

        whenever(createPersonUseCase.execute(createPersonRequestDto)).thenReturn(expectedResponse)

        // When
        val response = personController.createPerson(createPersonRequestDto)

        // Then
        assertNotNull(response)
        assertEquals(HttpStatus.CREATED, response.statusCode)
        val responseBody = response.body
        assertNotNull(responseBody)
        assertEquals(expectedResponse.id, responseBody.id)
        assertEquals(expectedResponse.name, responseBody.name)
        verify(createPersonUseCase).execute(createPersonRequestDto)
    }

    @Test
    fun `updatePersonLocation should return 200 OK with location data`() {
        // Given
        val personId = 1L
        val updateLocationRequestDto = UpdateLocationRequestDto(latitude = 40.7128, longitude = -74.0060)
        val expectedLocationResponse = LocationResponseDto(
            referenceId = personId,
            latitude = 40.7128,
            longitude = -74.0060
        )

        whenever(updatePersonLocationUseCase.execute(personId, updateLocationRequestDto))
            .thenReturn(expectedLocationResponse)

        // When
        val response: ResponseEntity<LocationResponseDto> = personController.updatePersonLocation(personId, updateLocationRequestDto)

        // Then
        assertNotNull(response)
        assertEquals(HttpStatus.OK, response.statusCode)
        val responseBody = response.body
        assertNotNull(responseBody)
        assertEquals(personId, responseBody.referenceId)
        assertEquals(40.7128, responseBody.latitude)
        assertEquals(-74.0060, responseBody.longitude)
        verify(updatePersonLocationUseCase).execute(personId, updateLocationRequestDto)
    }

    @Test
    fun `updatePersonLocation should call useCase with correct parameters`() {
        // Given
        val personId = 2L
        val updateLocationRequestDto = UpdateLocationRequestDto(latitude = 51.5074, longitude = -0.1278)
        val expectedLocationResponse = LocationResponseDto(
            referenceId = personId,
            latitude = 51.5074,
            longitude = -0.1278
        )

        whenever(updatePersonLocationUseCase.execute(personId, updateLocationRequestDto))
            .thenReturn(expectedLocationResponse)

        // When
        val response = personController.updatePersonLocation(personId, updateLocationRequestDto)

        // Then
        assertNotNull(response)
        assertEquals(HttpStatus.OK, response.statusCode)
        verify(updatePersonLocationUseCase).execute(personId, updateLocationRequestDto)
    }

    @Test
    fun `updatePersonLocation should throw when person is not found`() {
        // Given
        val personId = 999L
        val updateLocationRequestDto = UpdateLocationRequestDto(latitude = 40.7128, longitude = -74.0060)

        whenever(updatePersonLocationUseCase.execute(personId, updateLocationRequestDto))
            .thenThrow(PersonNotFoundException(personId))

        // When / Then
        // Here we are testing that the controller throws a PersonNotFoundException when the person is not found
        // And the exception is handled by the GlobalExceptionHandler to return a 404 status code
        assertThrows<PersonNotFoundException> {
            personController.updatePersonLocation(personId, updateLocationRequestDto)
        }
        verify(updatePersonLocationUseCase).execute(personId, updateLocationRequestDto)
    }

    @Test
    fun `getPersonsByIds should return 200 OK with list of persons`() {
        // Given
        val person1 = PersonResponseDto(id = 1L, name = "John Doe")
        val person2 = PersonResponseDto(id = 2L, name = "Jane Smith")
        val person3 = PersonResponseDto(id = 3L, name = "Bob Wilson")
        val ids = listOf(1L, 2L, 3L)

        whenever(getPersonsByIdsUseCase.execute(ids)).thenReturn(listOf(person1, person2, person3))

        // When
        val response: ResponseEntity<List<PersonResponseDto>> = personController.getPersonsByIds(ids)

        // Then
        assertNotNull(response)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(200, response.statusCodeValue)
        val responseBody = response.body
        assertNotNull(responseBody)
        assertEquals(3, responseBody.size)
        assertEquals(person1.id, responseBody[0].id)
        assertEquals(person1.name, responseBody[0].name)
        assertEquals(person2.id, responseBody[1].id)
        assertEquals(person2.name, responseBody[1].name)
        assertEquals(person3.id, responseBody[2].id)
        assertEquals(person3.name, responseBody[2].name)
        verify(getPersonsByIdsUseCase).execute(ids)
    }

    @Test
    fun `getPersonsByIds should return empty list when no persons found`() {
        // Given
        val ids = listOf(1L, 2L, 3L)
        whenever(getPersonsByIdsUseCase.execute(ids)).thenReturn(emptyList())

        // When
        val response: ResponseEntity<List<PersonResponseDto>> = personController.getPersonsByIds(ids)

        // Then
        assertNotNull(response)
        assertEquals(HttpStatus.OK, response.statusCode)
        val responseBody = response.body
        assertNotNull(responseBody)
        assertEquals(0, responseBody.size)
        verify(getPersonsByIdsUseCase).execute(ids)
    }

    @Test
    fun `getPersonsByIds should handle single id`() {
        // Given
        val person = PersonResponseDto(id = 1L, name = "Single Person")
        val ids = listOf(1L)

        whenever(getPersonsByIdsUseCase.execute(ids)).thenReturn(listOf(person))

        // When
        val response: ResponseEntity<List<PersonResponseDto>> = personController.getPersonsByIds(ids)

        // Then
        assertNotNull(response)
        assertEquals(HttpStatus.OK, response.statusCode)
        val responseBody = response.body
        assertNotNull(responseBody)
        assertEquals(1, responseBody.size)
        assertEquals(person.id, responseBody[0].id)
        assertEquals(person.name, responseBody[0].name)
        verify(getPersonsByIdsUseCase).execute(ids)
    }

    @Test
    fun `getPersonsByIds should handle empty ids list`() {
        // Given
        val ids = emptyList<Long>()
        whenever(getPersonsByIdsUseCase.execute(ids)).thenReturn(emptyList())

        // When
        val response: ResponseEntity<List<PersonResponseDto>> = personController.getPersonsByIds(ids)

        // Then
        assertNotNull(response)
        assertEquals(HttpStatus.OK, response.statusCode)
        val responseBody = response.body
        assertNotNull(responseBody)
        assertEquals(0, responseBody.size)
        verify(getPersonsByIdsUseCase).execute(ids)
    }

    @Test
    fun `getPersonsByIds should return partial results when some ids do not exist`() {
        // Given
        val person1 = PersonResponseDto(id = 1L, name = "John Doe")
        val person3 = PersonResponseDto(id = 3L, name = "Bob Wilson")
        val ids = listOf(1L, 2L, 3L)

        whenever(getPersonsByIdsUseCase.execute(ids)).thenReturn(listOf(person1, person3))

        // When
        val response: ResponseEntity<List<PersonResponseDto>> = personController.getPersonsByIds(ids)

        // Then
        assertNotNull(response)
        assertEquals(HttpStatus.OK, response.statusCode)
        val responseBody = response.body
        assertNotNull(responseBody)
        assertEquals(2, responseBody.size)
        assertEquals(person1.id, responseBody[0].id)
        assertEquals(person1.name, responseBody[0].name)
        assertEquals(person3.id, responseBody[1].id)
        assertEquals(person3.name, responseBody[1].name)
        verify(getPersonsByIdsUseCase).execute(ids)
    }

    @Test
    fun `getNearbyPersons should return 200 OK with nearby persons sorted by distance`() {
        // Given
        val lat = 40.7128
        val lon = -74.0060
        val radiusKm = 10.0

        val person1 = PersonResponseDto(id = 1L, name = "Nearby Person")
        val person2 = PersonResponseDto(id = 2L, name = "Far Person")
        val personWithDistance1 = PersonWithDistanceResponseDto(person = person1, distanceKm = 0.1)
        val personWithDistance2 = PersonWithDistanceResponseDto(person = person2, distanceKm = 5.0)

        whenever(getNearbyPersonsUseCase.execute(lat, lon, radiusKm))
            .thenReturn(listOf(personWithDistance1, personWithDistance2))

        // When
        val response = personController.getNearbyPersons(lat, lon, radiusKm)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        val responseBody = response.body
        assertNotNull(responseBody)
        assertEquals(2, responseBody.size)
        assertEquals("Nearby Person", responseBody[0].person.name)
        assertEquals("Far Person", responseBody[1].person.name)
        assertTrue(responseBody[0].distanceKm < responseBody[1].distanceKm)
        verify(getNearbyPersonsUseCase).execute(lat, lon, radiusKm)
    }

    @Test
    fun `getNearbyPersons should return empty list when no persons nearby`() {
        // Given
        val lat = 40.7128
        val lon = -74.0060
        val radiusKm = 1.0

        whenever(getNearbyPersonsUseCase.execute(lat, lon, radiusKm))
            .thenReturn(emptyList())

        // When
        val response = personController.getNearbyPersons(lat, lon, radiusKm)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        val responseBody = response.body
        assertNotNull(responseBody)
        assertTrue(responseBody.isEmpty())
        verify(getNearbyPersonsUseCase).execute(lat, lon, radiusKm)
    }

    @Test
    fun `getNearbyPersons should handle edge case coordinates`() {
        // Given
        val lat = 0.0
        val lon = 0.0
        val radiusKm = 100.0

        whenever(getNearbyPersonsUseCase.execute(lat, lon, radiusKm))
            .thenReturn(emptyList())

        // When
        val response = personController.getNearbyPersons(lat, lon, radiusKm)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        verify(getNearbyPersonsUseCase).execute(lat, lon, radiusKm)
    }

    @Test
    fun `getNearbyPersons should handle maximum radius`() {
        // Given
        val lat = 40.7128
        val lon = -74.0060
        val radiusKm = 1000.0

        whenever(getNearbyPersonsUseCase.execute(lat, lon, radiusKm))
            .thenReturn(emptyList())

        // When
        val response = personController.getNearbyPersons(lat, lon, radiusKm)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        verify(getNearbyPersonsUseCase).execute(lat, lon, radiusKm)
    }

    @Test
    fun `getNearbyPersons should handle minimum radius`() {
        // Given
        val lat = 40.7128
        val lon = -74.0060
        val radiusKm = 0.1

        whenever(getNearbyPersonsUseCase.execute(lat, lon, radiusKm))
            .thenReturn(emptyList())

        // When
        val response = personController.getNearbyPersons(lat, lon, radiusKm)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        verify(getNearbyPersonsUseCase).execute(lat, lon, radiusKm)
    }
}
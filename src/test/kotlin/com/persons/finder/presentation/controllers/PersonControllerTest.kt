package com.persons.finder.presentation.controllers

import com.persons.finder.domain.models.Person
import com.persons.finder.domain.services.LocationsService
import com.persons.finder.domain.services.PersonsService
import com.persons.finder.presentation.dto.mapper.LocationMapper
import com.persons.finder.presentation.dto.mapper.PersonMapper
import com.persons.finder.presentation.dto.request.CreatePersonRequestDto
import com.persons.finder.presentation.dto.request.UpdateLocationRequestDto
import com.persons.finder.presentation.dto.response.LocationResponseDto
import com.persons.finder.presentation.dto.response.PersonResponseDto
import com.persons.finder.presentation.exceptions.PersonNotFoundException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.mockito.kotlin.verify
import org.mockito.kotlin.never
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertTrue
import com.persons.finder.domain.models.Location
import com.persons.finder.domain.utils.DistanceCalculator

@ExtendWith(MockitoExtension::class)
class PersonControllerTest {

    @Mock
    private lateinit var personsService: PersonsService

    @Mock
    private lateinit var locationsService: LocationsService

    private lateinit var personController: PersonController

    @BeforeEach
    fun setUp() {
        personController = PersonController(personsService, locationsService)
    }

    @Test
    fun `createPerson should return 201 Created`() {
        // Given
        val createPersonRequestDto = CreatePersonRequestDto(name = "Allen")
        val expectedPerson = Person(name = "Allen", id = 1L)

        whenever(personsService.save(any())).thenReturn(expectedPerson)

        // When
        val response: ResponseEntity<PersonResponseDto> = personController.createPerson(createPersonRequestDto)

        // Then
        assertNotNull(response)
        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals(201, response.statusCodeValue)
        val responseBody = response.body
        assertNotNull(responseBody)
        assertEquals(expectedPerson.id, responseBody.id)
        assertEquals(expectedPerson.name, responseBody.name)
        Mockito.verify(personsService).save(any())
    }

    @Test
    fun `createPerson should call service with correct person object`() {
        // Given
        val createPersonRequestDto = CreatePersonRequestDto(name = "Test Person")
        val expectedPerson = Person(name = "Test Person", id = 1L)
        val expectedDomainPerson = PersonMapper.toDomain(createPersonRequestDto)

        whenever(personsService.save(expectedDomainPerson)).thenReturn(expectedPerson)

        // When
        val response = personController.createPerson(createPersonRequestDto)

        // Then
        assertNotNull(response)
        assertEquals(HttpStatus.CREATED, response.statusCode)
        val responseBody = response.body
        assertNotNull(responseBody)
        assertEquals(expectedPerson.id, responseBody.id)
        assertEquals(expectedPerson.name, responseBody.name)
        Mockito.verify(personsService).save(expectedDomainPerson)
    }

    @Test
    fun `updatePersonLocation should return 200 OK with location data`() {
        // Given
        val personId = 1L
        val updateLocationRequestDto = UpdateLocationRequestDto(latitude = 40.7128, longitude = -74.0060)
        val expectedPerson = Person(name = "John Doe", id = personId)

        whenever(personsService.getById(personId)).thenReturn(expectedPerson)
        whenever(locationsService.addLocation(any())).then { }

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
        Mockito.verify(personsService).getById(personId)
        Mockito.verify(locationsService).addLocation(any())
    }

    @Test
    fun `updatePersonLocation should call services with correct parameters`() {
        // Given
        val personId = 2L
        val updateLocationRequestDto = UpdateLocationRequestDto(latitude = 51.5074, longitude = -0.1278)
        val expectedPerson = Person(name = "Jane Smith", id = personId)
        val expectedLocation = LocationMapper.toDomain(personId, updateLocationRequestDto)

        whenever(personsService.getById(personId)).thenReturn(expectedPerson)
        whenever(locationsService.addLocation(expectedLocation)).then { }

        // When
        val response = personController.updatePersonLocation(personId, updateLocationRequestDto)

        // Then
        assertNotNull(response)
        assertEquals(HttpStatus.OK, response.statusCode)
        Mockito.verify(personsService).getById(personId)
        Mockito.verify(locationsService).addLocation(expectedLocation)
    }

    @Test
    fun `updatePersonLocation should throw when person is not found`() {
        // Given
        val personId = 999L
        val updateLocationRequestDto = UpdateLocationRequestDto(latitude = 40.7128, longitude = -74.0060)

        whenever(personsService.getById(personId)).thenThrow(PersonNotFoundException(personId))

        // When / Then
        // Here we are testing that the controller throws a PersonNotFoundException when the person is not found
        // And the exception is handled by the GlobalExceptionHandler to return a 404 status code
        assertThrows<PersonNotFoundException> {
            personController.updatePersonLocation(personId, updateLocationRequestDto)
        }
        verify(personsService).getById(personId)
        verify(locationsService, never()).addLocation(any())
    }

    @Test
    fun `getPersonsByIds should return 200 OK with list of persons`() {
        // Given
        val person1 = Person(name = "John Doe", id = 1L)
        val person2 = Person(name = "Jane Smith", id = 2L)
        val person3 = Person(name = "Bob Wilson", id = 3L)
        val ids = listOf(1L, 2L, 3L)

        whenever(personsService.getByIds(ids)).thenReturn(listOf(person1, person2, person3))

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
        Mockito.verify(personsService).getByIds(ids)
    }

    @Test
    fun `getPersonsByIds should return empty list when no persons found`() {
        // Given
        val ids = listOf(1L, 2L, 3L)
        whenever(personsService.getByIds(ids)).thenReturn(emptyList())

        // When
        val response: ResponseEntity<List<PersonResponseDto>> = personController.getPersonsByIds(ids)

        // Then
        assertNotNull(response)
        assertEquals(HttpStatus.OK, response.statusCode)
        val responseBody = response.body
        assertNotNull(responseBody)
        assertEquals(0, responseBody.size)
        Mockito.verify(personsService).getByIds(ids)
    }

    @Test
    fun `getPersonsByIds should handle single id`() {
        // Given
        val person = Person(name = "Single Person", id = 1L)
        val ids = listOf(1L)

        whenever(personsService.getByIds(ids)).thenReturn(listOf(person))

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
        Mockito.verify(personsService).getByIds(ids)
    }

    @Test
    fun `getPersonsByIds should handle empty ids list`() {
        // Given
        val ids = emptyList<Long>()
        whenever(personsService.getByIds(ids)).thenReturn(emptyList())

        // When
        val response: ResponseEntity<List<PersonResponseDto>> = personController.getPersonsByIds(ids)

        // Then
        assertNotNull(response)
        assertEquals(HttpStatus.OK, response.statusCode)
        val responseBody = response.body
        assertNotNull(responseBody)
        assertEquals(0, responseBody.size)
        Mockito.verify(personsService).getByIds(ids)
    }

    @Test
    fun `getPersonsByIds should return partial results when some ids do not exist`() {
        // Given
        val person1 = Person(name = "John Doe", id = 1L)
        val person3 = Person(name = "Bob Wilson", id = 3L)
        val ids = listOf(1L, 2L, 3L)

        whenever(personsService.getByIds(ids)).thenReturn(listOf(person1, person3))

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
        Mockito.verify(personsService).getByIds(ids)
    }

    @Test
    fun `getNearbyPersons should return 200 OK with nearby persons sorted by distance`() {
        // Given
        val lat = 40.7128
        val lon = -74.0060
        val radiusKm = 10.0
        
        val nearbyLocation = Location(
            referenceId = 1L,
            latitude = 40.7129,
            longitude = -74.0061
        )
        
        val farLocation = Location(
            referenceId = 2L,
            latitude = 40.7138,
            longitude = -74.0070
        )
        
        val nearbyPerson = Person(name = "Nearby Person", id = 1L)
        val farPerson = Person(name = "Far Person", id = 2L)
        
        whenever(locationsService.findAround(lat, lon, radiusKm))
            .thenReturn(listOf(nearbyLocation, farLocation))
        whenever(personsService.getByIds(listOf(1L, 2L)))
            .thenReturn(listOf(nearbyPerson, farPerson))

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
        verify(locationsService).findAround(lat, lon, radiusKm)
        verify(personsService).getByIds(listOf(1L, 2L))
    }

    @Test
    fun `getNearbyPersons should return empty list when no persons nearby`() {
        // Given
        val lat = 40.7128
        val lon = -74.0060
        val radiusKm = 1.0
        
        whenever(locationsService.findAround(lat, lon, radiusKm))
            .thenReturn(emptyList())

        // When
        val response = personController.getNearbyPersons(lat, lon, radiusKm)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        val responseBody = response.body
        assertNotNull(responseBody)
        assertTrue(responseBody.isEmpty())
        verify(locationsService).findAround(lat, lon, radiusKm)
        verify(personsService, never()).getByIds(any())
    }

    @Test
    fun `getNearbyPersons should handle edge case coordinates`() {
        // Given
        val lat = 0.0
        val lon = 0.0
        val radiusKm = 100.0
        
        whenever(locationsService.findAround(lat, lon, radiusKm))
            .thenReturn(emptyList())

        // When
        val response = personController.getNearbyPersons(lat, lon, radiusKm)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        verify(locationsService).findAround(lat, lon, radiusKm)
    }

    @Test
    fun `getNearbyPersons should handle maximum radius`() {
        // Given
        val lat = 40.7128
        val lon = -74.0060
        val radiusKm = 1000.0
        
        whenever(locationsService.findAround(lat, lon, radiusKm))
            .thenReturn(emptyList())

        // When
        val response = personController.getNearbyPersons(lat, lon, radiusKm)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        verify(locationsService).findAround(lat, lon, radiusKm)
    }

    @Test
    fun `getNearbyPersons should handle minimum radius`() {
        // Given
        val lat = 40.7128
        val lon = -74.0060
        val radiusKm = 0.1
        
        whenever(locationsService.findAround(lat, lon, radiusKm))
            .thenReturn(emptyList())

        // When
        val response = personController.getNearbyPersons(lat, lon, radiusKm)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        verify(locationsService).findAround(lat, lon, radiusKm)
    }

    @Test
    fun `getNearbyPersons should filter out locations with missing persons`() {
        // Given
        val lat = 40.7128
        val lon = -74.0060
        val radiusKm = 10.0
        
        val locationWithPerson = Location(
            referenceId = 1L,
            latitude = 40.7129,
            longitude = -74.0061
        )
        
        val locationWithoutPerson = Location(
            referenceId = 999L, // Non-existent person
            latitude = 40.7130,
            longitude = -74.0062
        )
        
        val person = Person(name = "Test Person", id = 1L)
        
        whenever(locationsService.findAround(lat, lon, radiusKm))
            .thenReturn(listOf(locationWithPerson, locationWithoutPerson))
        whenever(personsService.getByIds(listOf(1L, 999L)))
            .thenReturn(listOf(person)) // Only person 1 exists

        // When
        val response = personController.getNearbyPersons(lat, lon, radiusKm)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        val responseBody = response.body
        assertNotNull(responseBody)
        assertEquals(1, responseBody.size) // Only the person that exists
        assertEquals("Test Person", responseBody[0].person.name)
        verify(locationsService).findAround(lat, lon, radiusKm)
        verify(personsService).getByIds(listOf(1L, 999L))
    }
}
package com.persons.finder.application.usecases

import com.persons.finder.domain.models.Location
import com.persons.finder.domain.models.Person
import com.persons.finder.domain.services.LocationsService
import com.persons.finder.domain.services.PersonsService
import com.persons.finder.presentation.dto.response.PersonWithDistanceResponseDto
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExtendWith(MockitoExtension::class)
class GetNearbyPersonsUseCaseImplTest {

    @Mock
    private lateinit var locationsService: LocationsService

    @Mock
    private lateinit var personsService: PersonsService

    private lateinit var getNearbyPersonsUseCase: GetNearbyPersonsUseCaseImpl

    @BeforeEach
    fun setUp() {
        getNearbyPersonsUseCase = GetNearbyPersonsUseCaseImpl()
        getNearbyPersonsUseCase.apply {
            this.locationsService = this@GetNearbyPersonsUseCaseImplTest.locationsService
            this.personsService = this@GetNearbyPersonsUseCaseImplTest.personsService
        }
    }

    @Test
    fun `execute should return empty list when no locations found`() {
        // Given
        val lat = 40.7128
        val lon = -74.0060
        val radiusKm = 10.0

        whenever(locationsService.findAround(lat, lon, radiusKm)).thenReturn(emptyList())

        // When
        val result = getNearbyPersonsUseCase.execute(lat, lon, radiusKm)

        // Then
        assertTrue(result.isEmpty())
        verify(locationsService).findAround(lat, lon, radiusKm)
    }

    @Test
    fun `execute should return persons with distances sorted by distance`() {
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
        
        // Mock distance calculations
        whenever(locationsService.calculateDistance(lat, lon, nearbyLocation.latitude, nearbyLocation.longitude))
            .thenReturn(0.5) // Nearby person is 0.5km away
        whenever(locationsService.calculateDistance(lat, lon, farLocation.latitude, farLocation.longitude))
            .thenReturn(2.0) // Far person is 2.0km away

        // When
        val result = getNearbyPersonsUseCase.execute(lat, lon, radiusKm)

        // Then
        assertEquals(2, result.size)
        assertEquals("Nearby Person", result[0].person.name)
        assertEquals("Far Person", result[1].person.name)
        assertTrue(result[0].distanceKm < result[1].distanceKm)
        verify(locationsService).findAround(lat, lon, radiusKm)
        verify(personsService).getByIds(listOf(1L, 2L))
    }

    @Test
    fun `execute should filter out locations with missing persons`() {
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
        val result = getNearbyPersonsUseCase.execute(lat, lon, radiusKm)

        // Then
        assertEquals(1, result.size) // Only the person that exists
        assertEquals("Test Person", result[0].person.name)
        verify(locationsService).findAround(lat, lon, radiusKm)
        verify(personsService).getByIds(listOf(1L, 999L))
    }

    @Test
    fun `execute should calculate correct distances`() {
        // Given
        val lat = 0.0
        val lon = 0.0
        val radiusKm = 100.0

        val location = Location(
            referenceId = 1L,
            latitude = 0.5, // Approximately 55km from (0,0)
            longitude = 0.5
        )

        val person = Person(name = "Test Person", id = 1L)

        whenever(locationsService.findAround(lat, lon, radiusKm))
            .thenReturn(listOf(location))
        whenever(personsService.getByIds(listOf(1L)))
            .thenReturn(listOf(person))

        // When
        val result = getNearbyPersonsUseCase.execute(lat, lon, radiusKm)

        // Then
        assertEquals(1, result.size)
        val expectedDistance = locationsService.calculateDistance(0.0, 0.0, 0.5, 0.5)
        assertEquals(expectedDistance, result[0].distanceKm, 0.001)
        verify(locationsService).findAround(lat, lon, radiusKm)
        verify(personsService).getByIds(listOf(1L))
    }

    @Test
    fun `execute should handle edge case coordinates`() {
        // Given
        val lat = -90.0
        val lon = -180.0
        val radiusKm = 1.0

        whenever(locationsService.findAround(lat, lon, radiusKm))
            .thenReturn(emptyList())

        // When
        val result = getNearbyPersonsUseCase.execute(lat, lon, radiusKm)

        // Then
        assertTrue(result.isEmpty())
        verify(locationsService).findAround(lat, lon, radiusKm)
    }

    @Test
    fun `execute should handle maximum radius`() {
        // Given
        val lat = 40.7128
        val lon = -74.0060
        val radiusKm = 1000.0

        whenever(locationsService.findAround(lat, lon, radiusKm))
            .thenReturn(emptyList())

        // When
        val result = getNearbyPersonsUseCase.execute(lat, lon, radiusKm)

        // Then
        assertTrue(result.isEmpty())
        verify(locationsService).findAround(lat, lon, radiusKm)
    }

    @Test
    fun `execute should handle minimum radius`() {
        // Given
        val lat = 40.7128
        val lon = -74.0060
        val radiusKm = 0.1

        whenever(locationsService.findAround(lat, lon, radiusKm))
            .thenReturn(emptyList())

        // When
        val result = getNearbyPersonsUseCase.execute(lat, lon, radiusKm)

        // Then
        assertTrue(result.isEmpty())
        verify(locationsService).findAround(lat, lon, radiusKm)
    }
} 
package com.persons.finder.application.usecases

import com.persons.finder.domain.services.LocationsService
import com.persons.finder.infrastructure.repositories.dto.PersonLocationDto
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

    private lateinit var getNearbyPersonsUseCase: GetNearbyPersonsUseCaseImpl

    @BeforeEach
    fun setUp() {
        getNearbyPersonsUseCase = GetNearbyPersonsUseCaseImpl()
        getNearbyPersonsUseCase.locationsService = locationsService
    }

    @Test
    fun `execute should return empty list when no persons found`() {
        // Given
        val lat = 40.7128
        val lon = -74.0060
        val radiusKm = 10.0

        whenever(locationsService.findPersonsWithLocationsAround(lat, lon, radiusKm)).thenReturn(emptyList())

        // When
        val result = getNearbyPersonsUseCase.execute(lat, lon, radiusKm)

        // Then
        assertTrue(result.isEmpty())
        verify(locationsService).findPersonsWithLocationsAround(lat, lon, radiusKm)
    }

    @Test
    fun `execute should return persons with distances sorted by distance`() {
        // Given
        val lat = 40.7128
        val lon = -74.0060
        val radiusKm = 10.0

        val nearbyPersonLocation = PersonLocationDto(
            id = 1L,
            name = "Nearby Person",
            latitude = 40.7129,
            longitude = -74.0061
        )

        val farPersonLocation = PersonLocationDto(
            id = 2L,
            name = "Far Person",
            latitude = 40.7138,
            longitude = -74.0070
        )

        whenever(locationsService.findPersonsWithLocationsAround(lat, lon, radiusKm))
            .thenReturn(listOf(nearbyPersonLocation, farPersonLocation))
        
        // Mock distance calculations
        whenever(locationsService.calculateDistance(lat, lon, nearbyPersonLocation.latitude, nearbyPersonLocation.longitude))
            .thenReturn(0.5) // Nearby person is 0.5km away
        whenever(locationsService.calculateDistance(lat, lon, farPersonLocation.latitude, farPersonLocation.longitude))
            .thenReturn(2.0) // Far person is 2.0km away

        // When
        val result = getNearbyPersonsUseCase.execute(lat, lon, radiusKm)

        // Then
        assertEquals(2, result.size)
        assertEquals("Nearby Person", result[0].person.name)
        assertEquals("Far Person", result[1].person.name)
        assertTrue(result[0].distanceKm < result[1].distanceKm)
        verify(locationsService).findPersonsWithLocationsAround(lat, lon, radiusKm)
    }

    @Test
    fun `execute should filter out persons outside radius`() {
        // Given
        val lat = 40.7128
        val lon = -74.0060
        val radiusKm = 5.0 // Small radius

        val nearbyPersonLocation = PersonLocationDto(
            id = 1L,
            name = "Nearby Person",
            latitude = 40.7129,
            longitude = -74.0061
        )

        val farPersonLocation = PersonLocationDto(
            id = 2L,
            name = "Far Person",
            latitude = 40.7138,
            longitude = -74.0070
        )

        whenever(locationsService.findPersonsWithLocationsAround(lat, lon, radiusKm))
            .thenReturn(listOf(nearbyPersonLocation, farPersonLocation))
        
        // Mock distance calculations - one within radius, one outside
        whenever(locationsService.calculateDistance(lat, lon, nearbyPersonLocation.latitude, nearbyPersonLocation.longitude))
            .thenReturn(3.0) // Within 5km radius
        whenever(locationsService.calculateDistance(lat, lon, farPersonLocation.latitude, farPersonLocation.longitude))
            .thenReturn(7.0) // Outside 5km radius

        // When
        val result = getNearbyPersonsUseCase.execute(lat, lon, radiusKm)

        // Then
        assertEquals(1, result.size) // Only the nearby person
        assertEquals("Nearby Person", result[0].person.name)
        verify(locationsService).findPersonsWithLocationsAround(lat, lon, radiusKm)
    }

    @Test
    fun `execute should calculate correct distances`() {
        // Given
        val lat = 0.0
        val lon = 0.0
        val radiusKm = 100.0

        val personLocation = PersonLocationDto(
            id = 1L,
            name = "Test Person",
            latitude = 0.5, // Approximately 55km from (0,0)
            longitude = 0.5
        )

        whenever(locationsService.findPersonsWithLocationsAround(lat, lon, radiusKm))
            .thenReturn(listOf(personLocation))

        // When
        val result = getNearbyPersonsUseCase.execute(lat, lon, radiusKm)

        // Then
        assertEquals(1, result.size)
        val expectedDistance = locationsService.calculateDistance(0.0, 0.0, 0.5, 0.5)
        assertEquals(expectedDistance, result[0].distanceKm, 0.001)
        verify(locationsService).findPersonsWithLocationsAround(lat, lon, radiusKm)
    }

    @Test
    fun `execute should handle edge case coordinates`() {
        // Given
        val lat = -90.0
        val lon = -180.0
        val radiusKm = 1.0

        whenever(locationsService.findPersonsWithLocationsAround(lat, lon, radiusKm))
            .thenReturn(emptyList())

        // When
        val result = getNearbyPersonsUseCase.execute(lat, lon, radiusKm)

        // Then
        assertTrue(result.isEmpty())
        verify(locationsService).findPersonsWithLocationsAround(lat, lon, radiusKm)
    }

    @Test
    fun `execute should handle maximum radius`() {
        // Given
        val lat = 40.7128
        val lon = -74.0060
        val radiusKm = 1000.0

        whenever(locationsService.findPersonsWithLocationsAround(lat, lon, radiusKm))
            .thenReturn(emptyList())

        // When
        val result = getNearbyPersonsUseCase.execute(lat, lon, radiusKm)

        // Then
        assertTrue(result.isEmpty())
        verify(locationsService).findPersonsWithLocationsAround(lat, lon, radiusKm)
    }
} 
package com.persons.finder.domain.services

import com.persons.finder.domain.models.Location
import com.persons.finder.infrastructure.repositories.LocationRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.mockito.kotlin.any

@ExtendWith(MockitoExtension::class)
class LocationsServiceImplTest {

    @Mock
    private lateinit var locationRepository: LocationRepository

    private lateinit var locationsService: LocationsServiceImpl

    @BeforeEach
    fun setUp() {
        locationsService = LocationsServiceImpl()
        locationsService.locationRepository = locationRepository
    }

    @Test
    fun `findAround should return empty list when no locations in radius`() {
        // Given
        val lat = 40.7128
        val lon = -74.0060
        val radiusKm = 5.0

        whenever(locationRepository.findLocationsInBoundingBox(
            any<Double>(), any<Double>(), any<Double>(), any<Double>())
        )
            .thenReturn(emptyList())

        // When
        val result = locationsService.findAround(lat, lon, radiusKm)

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `findAround should return locations within radius sorted by distance`() {
        // Given
        val centerLat = 40.7128
        val centerLon = -74.0060
        val radiusKm = 10.0

        val nearbyLocation = Location(
            referenceId = 1L,
            latitude = 40.7129, // Very close
            longitude = -74.0061
        )

        val farLocation = Location(
            referenceId = 2L,
            latitude = 40.7138, // Further away
            longitude = -74.0070
        )

        whenever(locationRepository.findLocationsInBoundingBox(
            any<Double>(), any<Double>(), any<Double>(), any<Double>())
        )
            .thenReturn(listOf(nearbyLocation, farLocation))

        // When
        val result = locationsService.findAround(centerLat, centerLon, radiusKm)

        // Then
        assertEquals(2, result.size)
        assertEquals(1L, result[0].referenceId)
        assertEquals(2L, result[1].referenceId)
        // The first location should be closer (smaller distance)
        assertTrue(result[0].latitude > result[1].latitude || result[0].longitude > result[1].longitude)
    }

    @Test
    fun `findAround should filter out locations outside radius`() {
        // Given
        val centerLat = 40.7128
        val centerLon = -74.0060
        val radiusKm = 1.0 // Small radius

        val nearbyLocation = Location(
            referenceId = 1L,
            latitude = 40.7129, // Within 1km
            longitude = -74.0061
        )

        val farLocation = Location(
            referenceId = 2L,
            latitude = 40.7228, // Much further away (>1km)
            longitude = -74.0060
        )

        whenever(locationRepository.findLocationsInBoundingBox(
            any<Double>(), any<Double>(), any<Double>(), any<Double>())
        )
            .thenReturn(listOf(nearbyLocation, farLocation))

        // When
        val result = locationsService.findAround(centerLat, centerLon, radiusKm)

        // Then
        assertEquals(1, result.size)
        assertEquals(1L, result[0].referenceId)
    }

    @Test
    fun `findAround should handle exact distance calculations`() {
        // Given
        val centerLat = 0.0
        val centerLon = 0.0
        val radiusKm = 100.0

        val locationAtExactDistance = Location(
            referenceId = 1L,
            latitude = 0.899, // Approximately 100km away (0.899 degrees ≈ 100km)
            longitude = 0.0
        )

        whenever(locationRepository.findLocationsInBoundingBox(
            any<Double>(), any<Double>(), any<Double>(), any<Double>())
        )
            .thenReturn(listOf(locationAtExactDistance))

        // When
        val result = locationsService.findAround(centerLat, centerLon, radiusKm)

        // Then
        assertEquals(1, result.size)
        assertEquals(1L, result[0].referenceId)
    }

    @Test
    fun `findAround should return correct location data`() {
        // Given
        val centerLat = 40.7128
        val centerLon = -74.0060
        val radiusKm = 10.0

        val location = Location(
            referenceId = 123L,
            latitude = 40.7129,
            longitude = -74.0061
        )

        whenever(locationRepository.findLocationsInBoundingBox(
            any<Double>(), any<Double>(), any<Double>(), any<Double>())
        )
            .thenReturn(listOf(location))

        // When
        val result = locationsService.findAround(centerLat, centerLon, radiusKm)

        // Then
        assertEquals(1, result.size)
        assertEquals(123L, result[0].referenceId)
        assertEquals(40.7129, result[0].latitude)
        assertEquals(-74.0061, result[0].longitude)
    }
} 
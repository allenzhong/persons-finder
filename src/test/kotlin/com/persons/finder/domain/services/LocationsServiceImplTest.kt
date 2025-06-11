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
    fun `findAround should return empty list when no locations in bounding box`() {
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
    fun `findAround should return all locations within bounding box`() {
        // Given
        val centerLat = 40.7128
        val centerLon = -74.0060
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

        whenever(locationRepository.findLocationsInBoundingBox(
            any<Double>(), any<Double>(), any<Double>(), any<Double>())
        )
            .thenReturn(listOf(nearbyLocation, farLocation))

        // When
        val result = locationsService.findAround(centerLat, centerLon, radiusKm)

        // Then
        assertEquals(2, result.size)
        // Service should return all locations from repository without filtering or sorting
        assertTrue(result.any { it.referenceId == 1L })
        assertTrue(result.any { it.referenceId == 2L })
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
package com.persons.finder.domain.services

import com.persons.finder.domain.models.Location
import com.persons.finder.infrastructure.repositories.LocationRepository
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
    fun `addLocation should insert new location when location does not exist`() {
        // Given
        val location = Location(referenceId = 1L, latitude = 40.7128, longitude = -74.0060)
        whenever(locationRepository.findByReferenceId(1L)).thenReturn(null)

        // When
        locationsService.addLocation(location)

        // Then
        verify(locationRepository).insertLocation(
            referenceId = 1L,
            latitude = 40.7128,
            longitude = -74.0060
        )
    }

    @Test
    fun `addLocation should update existing location when location exists`() {
        // Given
        val location = Location(referenceId = 1L, latitude = 40.7128, longitude = -74.0060)
        val existingLocation = Location(referenceId = 1L, latitude = 40.0, longitude = -74.0)
        whenever(locationRepository.findByReferenceId(1L)).thenReturn(existingLocation)

        // When
        locationsService.addLocation(location)

        // Then
        verify(locationRepository).updateLocation(
            referenceId = 1L,
            latitude = 40.7128,
            longitude = -74.0060
        )
    }

    @Test
    fun `findPersonsWithLocationsAround should return empty list when no persons found`() {
        // Given
        val lat = 40.7128
        val lon = -74.0060
        val radiusKm = 10.0

        whenever(locationRepository.findPersonsWithLocationsInBoundingBox(
            any<Double>(), any<Double>(), any<Double>(), any<Double>()
        )).thenReturn(emptyList())

        // When
        val result = locationsService.findPersonsWithLocationsAround(lat, lon, radiusKm)

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `findPersonsWithLocationsAround should return persons with locations`() {
        // Given
        val lat = 40.7128
        val lon = -74.0060
        val radiusKm = 10.0

        val personLocation = PersonLocationDto(
            id = 1L,
            name = "John Doe",
            latitude = 40.7129,
            longitude = -74.0061
        )

        whenever(locationRepository.findPersonsWithLocationsInBoundingBox(
            any<Double>(), any<Double>(), any<Double>(), any<Double>()
        )).thenReturn(listOf(personLocation))

        // When
        val result = locationsService.findPersonsWithLocationsAround(lat, lon, radiusKm)

        // Then
        assertEquals(1, result.size)
        assertEquals(1L, result[0].id)
        assertEquals("John Doe", result[0].name)
        assertEquals(40.7129, result[0].latitude)
        assertEquals(-74.0061, result[0].longitude)
    }

    @Test
    fun `calculateDistance should return correct distance between two points`() {
        // Given
        val lat1 = 40.7128
        val lon1 = -74.0060
        val lat2 = 40.7129
        val lon2 = -74.0061

        // When
        val result = locationsService.calculateDistance(lat1, lon1, lat2, lon2)

        // Then
        assertTrue(result > 0)
        assertTrue(result < 1.0) // Should be very close since points are near each other
    }

    @Test
    fun `calculateDistance should return zero for same coordinates`() {
        // Given
        val lat = 40.7128
        val lon = -74.0060

        // When
        val result = locationsService.calculateDistance(lat, lon, lat, lon)

        // Then
        assertEquals(0.0, result, 0.001)
    }

    @Test
    fun `calculateBoundingBox should return correct bounding box`() {
        // Given
        val centerLat = 40.7128
        val centerLon = -74.0060
        val radiusKm = 10.0

        // When
        val result = locationsService.calculateBoundingBox(centerLat, centerLon, radiusKm)

        // Then
        assertTrue(result.minLat < centerLat)
        assertTrue(result.maxLat > centerLat)
        assertTrue(result.minLon < centerLon)
        assertTrue(result.maxLon > centerLon)
        assertTrue(result.maxLat - result.minLat > 0)
        assertTrue(result.maxLon - result.minLon > 0)
    }

    @Test
    fun `calculateBoundingBox should handle zero radius`() {
        // Given
        val centerLat = 40.7128
        val centerLon = -74.0060
        val radiusKm = 0.0

        // When
        val result = locationsService.calculateBoundingBox(centerLat, centerLon, radiusKm)

        // Then
        assertEquals(centerLat, result.minLat, 0.001)
        assertEquals(centerLat, result.maxLat, 0.001)
        assertEquals(centerLon, result.minLon, 0.001)
        assertEquals(centerLon, result.maxLon, 0.001)
    }

    @Test
    fun `calculateBoundingBox should handle different latitudes`() {
        // Given
        val equatorLat = 0.0
        val equatorLon = 0.0
        val polarLat = 80.0
        val polarLon = 0.0
        val radiusKm = 100.0

        // When
        val equatorBox = locationsService.calculateBoundingBox(equatorLat, equatorLon, radiusKm)
        val polarBox = locationsService.calculateBoundingBox(polarLat, polarLon, radiusKm)

        // Then
        assertTrue(equatorBox.maxLat - equatorBox.minLat > 0)
        assertTrue(equatorBox.maxLon - equatorBox.minLon > 0)
        assertTrue(polarBox.maxLat - polarBox.minLat > 0)
        assertTrue(polarBox.maxLon - polarBox.minLon > 0)
        // Both boxes should have valid dimensions
        assertTrue(equatorBox.maxLat > equatorBox.minLat)
        assertTrue(equatorBox.maxLon > equatorBox.minLon)
        assertTrue(polarBox.maxLat > polarBox.minLat)
        assertTrue(polarBox.maxLon > polarBox.minLon)
    }
} 
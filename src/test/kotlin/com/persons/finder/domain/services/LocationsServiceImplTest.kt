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
import org.mockito.kotlin.eq

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

    @Test
    fun `findPersonsWithLocationsAroundPaginated should return empty result when no persons found`() {
        // Given
        val lat = 40.7128
        val lon = -74.0060
        val radiusKm = 10.0
        val page = 1
        val pageSize = 10

        whenever(locationRepository.countPersonsWithLocationsInBoundingBox(
            any<Double>(), any<Double>(), any<Double>(), any<Double>()
        )).thenReturn(0L)
        
        whenever(locationRepository.findPersonsWithLocationsInBoundingBoxPaginated(
            any<Double>(), any<Double>(), any<Double>(), any<Double>(), any<Int>(), any<Int>()
        )).thenReturn(emptyList())

        // When
        val result = locationsService.findPersonsWithLocationsAroundPaginated(lat, lon, radiusKm, page, pageSize)

        // Then
        assertTrue(result.persons.isEmpty())
        assertEquals(0L, result.totalCount)
    }

    @Test
    fun `findPersonsWithLocationsAroundPaginated should return paginated persons with correct count`() {
        // Given
        val lat = 40.7128
        val lon = -74.0060
        val radiusKm = 10.0
        val page = 1
        val pageSize = 2

        val person1 = PersonLocationDto(id = 1L, name = "Person 1", latitude = 40.7129, longitude = -74.0061)
        val person2 = PersonLocationDto(id = 2L, name = "Person 2", latitude = 40.7130, longitude = -74.0062)

        whenever(locationRepository.countPersonsWithLocationsInBoundingBox(
            any<Double>(), any<Double>(), any<Double>(), any<Double>()
        )).thenReturn(5L) // Total 5 persons in bounding box
        
        whenever(locationRepository.findPersonsWithLocationsInBoundingBoxPaginated(
            any<Double>(), any<Double>(), any<Double>(), any<Double>(), eq(2), eq(0)
        )).thenReturn(listOf(person1, person2))

        // When
        val result = locationsService.findPersonsWithLocationsAroundPaginated(lat, lon, radiusKm, page, pageSize)

        // Then
        assertEquals(2, result.persons.size)
        assertEquals(5L, result.totalCount)
        assertEquals(1L, result.persons[0].id)
        assertEquals("Person 1", result.persons[0].name)
        assertEquals(2L, result.persons[1].id)
        assertEquals("Person 2", result.persons[1].name)
    }

    @Test
    fun `findPersonsWithLocationsAroundPaginated should calculate correct offset for page 2`() {
        // Given
        val lat = 40.7128
        val lon = -74.0060
        val radiusKm = 10.0
        val page = 2
        val pageSize = 3

        val person4 = PersonLocationDto(id = 4L, name = "Person 4", latitude = 40.7131, longitude = -74.0063)
        val person5 = PersonLocationDto(id = 5L, name = "Person 5", latitude = 40.7132, longitude = -74.0064)

        whenever(locationRepository.countPersonsWithLocationsInBoundingBox(
            any<Double>(), any<Double>(), any<Double>(), any<Double>()
        )).thenReturn(5L)
        
        // Should call with offset = (2-1) * 3 = 3
        whenever(locationRepository.findPersonsWithLocationsInBoundingBoxPaginated(
            any<Double>(), any<Double>(), any<Double>(), any<Double>(), eq(3), eq(3)
        )).thenReturn(listOf(person4, person5))

        // When
        val result = locationsService.findPersonsWithLocationsAroundPaginated(lat, lon, radiusKm, page, pageSize)

        // Then
        assertEquals(2, result.persons.size)
        assertEquals(5L, result.totalCount)
        assertEquals(4L, result.persons[0].id)
        assertEquals("Person 4", result.persons[0].name)
        assertEquals(5L, result.persons[1].id)
        assertEquals("Person 5", result.persons[1].name)
    }

    @Test
    fun `findPersonsWithLocationsAroundPaginated should handle edge case with page 1 and large page size`() {
        // Given
        val lat = 40.7128
        val lon = -74.0060
        val radiusKm = 10.0
        val page = 1
        val pageSize = 1000

        whenever(locationRepository.countPersonsWithLocationsInBoundingBox(
            any<Double>(), any<Double>(), any<Double>(), any<Double>()
        )).thenReturn(50L)
        
        whenever(locationRepository.findPersonsWithLocationsInBoundingBoxPaginated(
            any<Double>(), any<Double>(), any<Double>(), any<Double>(), eq(1000), eq(0)
        )).thenReturn(emptyList())

        // When
        val result = locationsService.findPersonsWithLocationsAroundPaginated(lat, lon, radiusKm, page, pageSize)

        // Then
        assertTrue(result.persons.isEmpty())
        assertEquals(50L, result.totalCount)
    }

    @Test
    fun `findPersonsWithLocationsAroundPaginated should use correct bounding box parameters`() {
        // Given
        val lat = 40.7128
        val lon = -74.0060
        val radiusKm = 10.0
        val page = 1
        val pageSize = 10

        // Calculate expected bounding box
        val expectedBoundingBox = locationsService.calculateBoundingBox(lat, lon, radiusKm)

        whenever(locationRepository.countPersonsWithLocationsInBoundingBox(
            eq(expectedBoundingBox.minLat), eq(expectedBoundingBox.maxLat),
            eq(expectedBoundingBox.minLon), eq(expectedBoundingBox.maxLon)
        )).thenReturn(1L)
        
        whenever(locationRepository.findPersonsWithLocationsInBoundingBoxPaginated(
            eq(expectedBoundingBox.minLat), eq(expectedBoundingBox.maxLat),
            eq(expectedBoundingBox.minLon), eq(expectedBoundingBox.maxLon),
            eq(10), eq(0)
        )).thenReturn(emptyList())

        // When
        locationsService.findPersonsWithLocationsAroundPaginated(lat, lon, radiusKm, page, pageSize)

        // Then - verify that the correct bounding box parameters were used
        verify(locationRepository).countPersonsWithLocationsInBoundingBox(
            eq(expectedBoundingBox.minLat), eq(expectedBoundingBox.maxLat),
            eq(expectedBoundingBox.minLon), eq(expectedBoundingBox.maxLon)
        )
        
        verify(locationRepository).findPersonsWithLocationsInBoundingBoxPaginated(
            eq(expectedBoundingBox.minLat), eq(expectedBoundingBox.maxLat),
            eq(expectedBoundingBox.minLon), eq(expectedBoundingBox.maxLon),
            eq(10), eq(0)
        )
    }
} 
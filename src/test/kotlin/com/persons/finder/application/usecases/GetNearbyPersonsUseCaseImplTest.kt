package com.persons.finder.application.usecases

import com.persons.finder.domain.services.LocationsService
import com.persons.finder.domain.services.PaginatedPersonLocationResult
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
import kotlin.test.assertFalse

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
    fun `execute should return empty paginated response when no persons found`() {
        // Given
        val lat = 40.7128
        val lon = -74.0060
        val radiusKm = 10.0
        val page = 1
        val pageSize = 500

        whenever(locationsService.findPersonsWithLocationsAroundPaginated(lat, lon, radiusKm, page, pageSize))
            .thenReturn(PaginatedPersonLocationResult(emptyList(), 0L))

        // When
        val result = getNearbyPersonsUseCase.execute(lat, lon, radiusKm, page, pageSize)

        // Then
        assertTrue(result.data.isEmpty())
        assertEquals(0L, result.pagination.totalItems)
        assertEquals(0, result.pagination.totalPages)
        assertEquals(page, result.pagination.page)
        assertEquals(pageSize, result.pagination.pageSize)
        verify(locationsService).findPersonsWithLocationsAroundPaginated(lat, lon, radiusKm, page, pageSize)
    }

    @Test
    fun `execute should return paginated persons with distances sorted by distance`() {
        // Given
        val lat = 40.7128
        val lon = -74.0060
        val radiusKm = 10.0
        val page = 1
        val pageSize = 500

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

        whenever(locationsService.findPersonsWithLocationsAroundPaginated(lat, lon, radiusKm, page, pageSize))
            .thenReturn(PaginatedPersonLocationResult(listOf(nearbyPersonLocation, farPersonLocation), 2L))
        
        // Mock distance calculations
        whenever(locationsService.calculateDistance(lat, lon, nearbyPersonLocation.latitude, nearbyPersonLocation.longitude))
            .thenReturn(0.5) // Nearby person is 0.5km away
        whenever(locationsService.calculateDistance(lat, lon, farPersonLocation.latitude, farPersonLocation.longitude))
            .thenReturn(2.0) // Far person is 2.0km away

        // When
        val result = getNearbyPersonsUseCase.execute(lat, lon, radiusKm, page, pageSize)

        // Then
        assertEquals(2, result.data.size)
        assertEquals("Nearby Person", result.data[0].person.name)
        assertEquals("Far Person", result.data[1].person.name)
        assertTrue(result.data[0].distanceKm < result.data[1].distanceKm)
        assertEquals(2L, result.pagination.totalItems)
        assertEquals(1, result.pagination.totalPages)
        assertEquals(page, result.pagination.page)
        assertEquals(pageSize, result.pagination.pageSize)
        verify(locationsService).findPersonsWithLocationsAroundPaginated(lat, lon, radiusKm, page, pageSize)
    }

    @Test
    fun `execute should filter out persons outside radius`() {
        // Given
        val lat = 40.7128
        val lon = -74.0060
        val radiusKm = 5.0 // Small radius
        val page = 1
        val pageSize = 500

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

        whenever(locationsService.findPersonsWithLocationsAroundPaginated(lat, lon, radiusKm, page, pageSize))
            .thenReturn(PaginatedPersonLocationResult(listOf(nearbyPersonLocation, farPersonLocation), 2L))
        
        // Mock distance calculations - one within radius, one outside
        whenever(locationsService.calculateDistance(lat, lon, nearbyPersonLocation.latitude, nearbyPersonLocation.longitude))
            .thenReturn(3.0) // Within 5km radius
        whenever(locationsService.calculateDistance(lat, lon, farPersonLocation.latitude, farPersonLocation.longitude))
            .thenReturn(7.0) // Outside 5km radius

        // When
        val result = getNearbyPersonsUseCase.execute(lat, lon, radiusKm, page, pageSize)

        // Then
        assertEquals(1, result.data.size) // Only the nearby person
        assertEquals("Nearby Person", result.data[0].person.name)
        assertEquals(2L, result.pagination.totalItems) // Total count from database
        verify(locationsService).findPersonsWithLocationsAroundPaginated(lat, lon, radiusKm, page, pageSize)
    }

    @Test
    fun `execute should calculate correct distances`() {
        // Given
        val lat = 0.0
        val lon = 0.0
        val radiusKm = 100.0
        val page = 1
        val pageSize = 500

        val personLocation = PersonLocationDto(
            id = 1L,
            name = "Test Person",
            latitude = 0.5, // Approximately 55km from (0,0)
            longitude = 0.5
        )

        whenever(locationsService.findPersonsWithLocationsAroundPaginated(lat, lon, radiusKm, page, pageSize))
            .thenReturn(PaginatedPersonLocationResult(listOf(personLocation), 1L))

        // When
        val result = getNearbyPersonsUseCase.execute(lat, lon, radiusKm, page, pageSize)

        // Then
        assertEquals(1, result.data.size)
        val expectedDistance = locationsService.calculateDistance(0.0, 0.0, 0.5, 0.5)
        assertEquals(expectedDistance, result.data[0].distanceKm, 0.001)
        verify(locationsService).findPersonsWithLocationsAroundPaginated(lat, lon, radiusKm, page, pageSize)
    }

    @Test
    fun `execute should handle edge case coordinates`() {
        // Given
        val lat = -90.0
        val lon = -180.0
        val radiusKm = 1.0
        val page = 1
        val pageSize = 500

        whenever(locationsService.findPersonsWithLocationsAroundPaginated(lat, lon, radiusKm, page, pageSize))
            .thenReturn(PaginatedPersonLocationResult(emptyList(), 0L))

        // When
        val result = getNearbyPersonsUseCase.execute(lat, lon, radiusKm, page, pageSize)

        // Then
        assertTrue(result.data.isEmpty())
        assertEquals(0L, result.pagination.totalItems)
        verify(locationsService).findPersonsWithLocationsAroundPaginated(lat, lon, radiusKm, page, pageSize)
    }

    @Test
    fun `execute should handle maximum radius`() {
        // Given
        val lat = 40.7128
        val lon = -74.0060
        val radiusKm = 1000.0
        val page = 1
        val pageSize = 500

        whenever(locationsService.findPersonsWithLocationsAroundPaginated(lat, lon, radiusKm, page, pageSize))
            .thenReturn(PaginatedPersonLocationResult(emptyList(), 0L))

        // When
        val result = getNearbyPersonsUseCase.execute(lat, lon, radiusKm, page, pageSize)

        // Then
        assertTrue(result.data.isEmpty())
        assertEquals(0L, result.pagination.totalItems)
        verify(locationsService).findPersonsWithLocationsAroundPaginated(lat, lon, radiusKm, page, pageSize)
    }

    @Test
    fun `execute should handle pagination correctly with multiple pages`() {
        // Given
        val lat = 40.7128
        val lon = -74.0060
        val radiusKm = 10.0
        val page = 2
        val pageSize = 1

        val person1 = PersonLocationDto(id = 1L, name = "Person 1", latitude = 40.7129, longitude = -74.0061)
        val person2 = PersonLocationDto(id = 2L, name = "Person 2", latitude = 40.7130, longitude = -74.0062)

        whenever(locationsService.findPersonsWithLocationsAroundPaginated(lat, lon, radiusKm, page, pageSize))
            .thenReturn(PaginatedPersonLocationResult(listOf(person2), 2L))
        
        whenever(locationsService.calculateDistance(lat, lon, person2.latitude, person2.longitude))
            .thenReturn(1.0)

        // When
        val result = getNearbyPersonsUseCase.execute(lat, lon, radiusKm, page, pageSize)

        // Then
        assertEquals(1, result.data.size)
        assertEquals("Person 2", result.data[0].person.name)
        assertEquals(2L, result.pagination.totalItems)
        assertEquals(2, result.pagination.totalPages)
        assertEquals(page, result.pagination.page)
        assertEquals(pageSize, result.pagination.pageSize)
        assertFalse(result.pagination.hasNext)
        verify(locationsService).findPersonsWithLocationsAroundPaginated(lat, lon, radiusKm, page, pageSize)
    }

    @Test
    fun `execute should handle pagination with hasNext and hasPrevious correctly`() {
        // Given
        val lat = 40.7128
        val lon = -74.0060
        val radiusKm = 10.0
        val page = 2
        val pageSize = 1

        val person2 = PersonLocationDto(id = 2L, name = "Person 2", latitude = 40.7130, longitude = -74.0062)

        whenever(locationsService.findPersonsWithLocationsAroundPaginated(lat, lon, radiusKm, page, pageSize))
            .thenReturn(PaginatedPersonLocationResult(listOf(person2), 3L)) // Total 3 items
        
        whenever(locationsService.calculateDistance(lat, lon, person2.latitude, person2.longitude))
            .thenReturn(1.0)

        // When
        val result = getNearbyPersonsUseCase.execute(lat, lon, radiusKm, page, pageSize)

        // Then
        assertEquals(1, result.data.size)
        assertEquals(3L, result.pagination.totalItems)
        assertEquals(3, result.pagination.totalPages)
        assertEquals(page, result.pagination.page)
        assertTrue(result.pagination.hasNext) // Page 2 of 3
        assertTrue(result.pagination.hasPrevious) // Page 2 > 1
        verify(locationsService).findPersonsWithLocationsAroundPaginated(lat, lon, radiusKm, page, pageSize)
    }

    @Test
    fun `execute should return empty response when all persons are filtered out by radius`() {
        // Given
        val lat = 40.7128
        val lon = -74.0060
        val radiusKm = 1.0 // Very small radius
        val page = 1
        val pageSize = 500

        val personLocation = PersonLocationDto(
            id = 1L,
            name = "Far Person",
            latitude = 40.7138,
            longitude = -74.0070
        )

        whenever(locationsService.findPersonsWithLocationsAroundPaginated(lat, lon, radiusKm, page, pageSize))
            .thenReturn(PaginatedPersonLocationResult(listOf(personLocation), 1L))
        
        // Person is outside the radius
        whenever(locationsService.calculateDistance(lat, lon, personLocation.latitude, personLocation.longitude))
            .thenReturn(5.0) // 5km away, outside 1km radius

        // When
        val result = getNearbyPersonsUseCase.execute(lat, lon, radiusKm, page, pageSize)

        // Then
        assertTrue(result.data.isEmpty())
        assertEquals(0L, result.pagination.totalItems) // Empty response when all filtered out
        assertEquals(0, result.pagination.totalPages)
        verify(locationsService).findPersonsWithLocationsAroundPaginated(lat, lon, radiusKm, page, pageSize)
    }
} 
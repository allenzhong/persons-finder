package com.persons.finder.infrastructure.repositories

import com.persons.finder.domain.models.Location
import com.persons.finder.infrastructure.repositories.dto.PersonLocationDto
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@DataJdbcTest
@ActiveProfiles("test")
class LocationRepositoryTest {

    @Autowired
    private lateinit var locationRepository: LocationRepository

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @Test
    fun `findPersonsWithLocationsInBoundingBoxPaginated should return paginated results`() {
        // Given - Insert test data
        insertTestData()
        
        val minLat = 40.0
        val maxLat = 41.0
        val minLon = -75.0
        val maxLon = -74.0
        val limit = 2
        val offset = 0

        // When
        val result = locationRepository.findPersonsWithLocationsInBoundingBoxPaginated(
            minLat, maxLat, minLon, maxLon, limit, offset
        )

        // Then
        assertEquals(2, result.size)
        assertTrue(result.all { it.latitude in minLat..maxLat })
        assertTrue(result.all { it.longitude in minLon..maxLon })
    }

    @Test
    fun `findPersonsWithLocationsInBoundingBoxPaginated should respect limit and offset`() {
        // Given - Insert test data
        insertTestData()
        
        val minLat = 40.0
        val maxLat = 41.0
        val minLon = -75.0
        val maxLon = -74.0
        val limit = 1
        val offset = 1

        // When
        val result = locationRepository.findPersonsWithLocationsInBoundingBoxPaginated(
            minLat, maxLat, minLon, maxLon, limit, offset
        )

        // Then
        assertEquals(1, result.size)
        assertTrue(result.all { it.latitude in minLat..maxLat })
        assertTrue(result.all { it.longitude in minLon..maxLon })
    }

    @Test
    fun `findPersonsWithLocationsInBoundingBoxPaginated should return empty list when no matches`() {
        // Given
        val minLat = 50.0 // Outside test data range
        val maxLat = 51.0
        val minLon = -80.0
        val maxLon = -79.0
        val limit = 10
        val offset = 0

        // When
        val result = locationRepository.findPersonsWithLocationsInBoundingBoxPaginated(
            minLat, maxLat, minLon, maxLon, limit, offset
        )

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `countPersonsWithLocationsInBoundingBox should return correct count`() {
        // Given - Insert test data
        insertTestData()
        
        val minLat = 40.0
        val maxLat = 41.0
        val minLon = -75.0
        val maxLon = -74.0

        // When
        val result = locationRepository.countPersonsWithLocationsInBoundingBox(
            minLat, maxLat, minLon, maxLon
        )

        // Then
        assertTrue(result > 0)
    }

    @Test
    fun `countPersonsWithLocationsInBoundingBox should return zero when no matches`() {
        // Given
        val minLat = 50.0 // Outside test data range
        val maxLat = 51.0
        val minLon = -80.0
        val maxLon = -79.0

        // When
        val result = locationRepository.countPersonsWithLocationsInBoundingBox(
            minLat, maxLat, minLon, maxLon
        )

        // Then
        assertEquals(0L, result)
    }

    @Test
    fun `findPersonsWithLocationsInBoundingBox should return all matching results`() {
        // Given - Insert test data
        insertTestData()
        
        val minLat = 40.0
        val maxLat = 41.0
        val minLon = -75.0
        val maxLon = -74.0

        // When
        val result = locationRepository.findPersonsWithLocationsInBoundingBox(
            minLat, maxLat, minLon, maxLon
        )

        // Then
        assertTrue(result.isNotEmpty())
        assertTrue(result.all { it.latitude in minLat..maxLat })
        assertTrue(result.all { it.longitude in minLon..maxLon })
    }

    @Test
    fun `findPersonsWithLocationsInBoundingBox should return empty list when no matches`() {
        // Given
        val minLat = 50.0 // Outside test data range
        val maxLat = 51.0
        val minLon = -80.0
        val maxLon = -79.0

        // When
        val result = locationRepository.findPersonsWithLocationsInBoundingBox(
            minLat, maxLat, minLon, maxLon
        )

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `findByReferenceId should return location when exists`() {
        // Given - Insert test data
        insertTestData()
        
        val referenceId = 1L

        // When
        val result = locationRepository.findByReferenceId(referenceId)

        // Then
        assertEquals(referenceId, result?.referenceId)
    }

    @Test
    fun `findByReferenceId should return null when not exists`() {
        // Given
        val referenceId = 999L

        // When
        val result = locationRepository.findByReferenceId(referenceId)

        // Then
        assertEquals(null, result)
    }

    private fun insertTestData() {
        // Insert test persons
        jdbcTemplate.update("INSERT INTO persons (id, name) VALUES (1, 'Person 1')")
        jdbcTemplate.update("INSERT INTO persons (id, name) VALUES (2, 'Person 2')")
        jdbcTemplate.update("INSERT INTO persons (id, name) VALUES (3, 'Person 3')")
        
        // Insert test locations
        jdbcTemplate.update("INSERT INTO locations (reference_id, latitude, longitude) VALUES (1, 40.7128, -74.0060)")
        jdbcTemplate.update("INSERT INTO locations (reference_id, latitude, longitude) VALUES (2, 40.7129, -74.0061)")
        jdbcTemplate.update("INSERT INTO locations (reference_id, latitude, longitude) VALUES (3, 40.7130, -74.0062)")
    }
} 
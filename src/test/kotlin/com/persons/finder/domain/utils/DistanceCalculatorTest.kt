package com.persons.finder.domain.utils

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DistanceCalculatorTest {

    @Test
    fun `calculateDistance should return correct distance between two points`() {
        // Given - New York to London (approximately 5570 km)
        val nyLat = 40.7128
        val nyLon = -74.0060
        val londonLat = 51.5074
        val londonLon = -0.1278

        // When
        val distance = DistanceCalculator.calculateDistance(nyLat, nyLon, londonLat, londonLon)

        // Then
        assertTrue(distance > 5500 && distance < 5700, "Distance should be approximately 5570 km, got: $distance")
    }

    @Test
    fun `calculateDistance should return 0 for same coordinates`() {
        // Given
        val lat = 40.7128
        val lon = -74.0060

        // When
        val distance = DistanceCalculator.calculateDistance(lat, lon, lat, lon)

        // Then
        assertEquals(0.0, distance, 0.001)
    }

    @Test
    fun `calculateDistance should handle antipodal points`() {
        // Given - Points on opposite sides of Earth (approximately 20000 km)
        val lat1 = 0.0
        val lon1 = 0.0
        val lat2 = 0.0
        val lon2 = 180.0

        // When
        val distance = DistanceCalculator.calculateDistance(lat1, lon1, lat2, lon2)

        // Then
        assertTrue(distance > 19000 && distance < 21000, "Distance should be approximately 20000 km, got: $distance")
    }

    @Test
    fun `calculateBoundingBox should return correct bounding box`() {
        // Given
        val centerLat = 40.7128
        val centerLon = -74.0060
        val radiusKm = 10.0

        // When
        val boundingBox = DistanceCalculator.calculateBoundingBox(centerLat, centerLon, radiusKm)

        // Then
        assertAll(
            { assertTrue(boundingBox.minLat < centerLat) },
            { assertTrue(boundingBox.maxLat > centerLat) },
            { assertTrue(boundingBox.minLon < centerLon) },
            { assertTrue(boundingBox.maxLon > centerLon) },
            { assertTrue(boundingBox.maxLat - boundingBox.minLat > 0) },
            { assertTrue(boundingBox.maxLon - boundingBox.minLon > 0) }
        )
    }

    @Test
    fun `calculateBoundingBox should handle zero radius`() {
        // Given
        val centerLat = 40.7128
        val centerLon = -74.0060
        val radiusKm = 0.0

        // When
        val boundingBox = DistanceCalculator.calculateBoundingBox(centerLat, centerLon, radiusKm)

        // Then
        assertAll(
            { assertEquals(centerLat, boundingBox.minLat, 0.001) },
            { assertEquals(centerLat, boundingBox.maxLat, 0.001) },
            { assertEquals(centerLon, boundingBox.minLon, 0.001) },
            { assertEquals(centerLon, boundingBox.maxLon, 0.001) }
        )
    }

    @Test
    fun `calculateBoundingBox should handle different latitudes`() {
        // Given - Test at equator and near poles
        val equatorLat = 0.0
        val equatorLon = 0.0
        val polarLat = 80.0
        val polarLon = 0.0
        val radiusKm = 100.0

        // When
        val equatorBox = DistanceCalculator.calculateBoundingBox(equatorLat, equatorLon, radiusKm)
        val polarBox = DistanceCalculator.calculateBoundingBox(polarLat, polarLon, radiusKm)

        // Then
        assertAll(
            { assertTrue(equatorBox.maxLat - equatorBox.minLat > 0) },
            { assertTrue(equatorBox.maxLon - equatorBox.minLon > 0) },
            { assertTrue(polarBox.maxLat - polarBox.minLat > 0) },
            { assertTrue(polarBox.maxLon - polarBox.minLon > 0) },
            { assertEquals(equatorBox.maxLat - equatorBox.minLat, polarBox.maxLat - polarBox.minLat, 0.1) }
        )
    }
} 
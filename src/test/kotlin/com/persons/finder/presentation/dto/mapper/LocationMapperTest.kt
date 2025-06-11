package com.persons.finder.presentation.dto.mapper

import com.persons.finder.domain.models.Location
import com.persons.finder.presentation.dto.request.UpdateLocationRequestDto
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.Assertions.*

class LocationMapperTest {

    @Test
    fun `toDomain should map UpdateLocationRequestDto to Location correctly`() {
        // Given
        val referenceId = 123L
        val dto = UpdateLocationRequestDto(latitude = 10.5, longitude = 20.5)

        // When
        val location = LocationMapper.toDomain(referenceId, dto)

        // Then
        assertAll(
            { assertNotNull(location) },
            { assertEquals(referenceId, location.referenceId) },
            { assertEquals(dto.latitude, location.latitude) },
            { assertEquals(dto.longitude, location.longitude) }
        )
    }

    @Test
    fun `toResponseDto should map Location to LocationResponseDto correctly`() {
        // Given
        val location = Location(referenceId = 456L, latitude = 30.0, longitude = 40.0)

        // When
        val response = LocationMapper.toResponseDto(location)

        // Then
        assertAll(
            { assertNotNull(response) },
            { assertEquals(location.referenceId, response.referenceId) },
            { assertEquals(location.latitude, response.latitude) },
            { assertEquals(location.longitude, response.longitude) }
        )
    }

    @Test
    fun `toDomain should handle different coordinate values`() {
        // Given
        val referenceId = 2L
        val updateLocationRequestDto = UpdateLocationRequestDto(latitude = -33.8688, longitude = 151.2093)

        // When
        val result = LocationMapper.toDomain(referenceId, updateLocationRequestDto)

        // Then
        assertAll(
            { assertNotNull(result) },
            { assertEquals(referenceId, result.referenceId) },
            { assertEquals(-33.8688, result.latitude) },
            { assertEquals(151.2093, result.longitude) }
        )
    }

    @Test
    fun `toResponseDto should handle different coordinate values`() {
        // Given
        val location = Location(referenceId = 3L, latitude = 35.6762, longitude = 139.6503)

        // When
        val result = LocationMapper.toResponseDto(location)

        // Then
        assertAll(
            { assertNotNull(result) },
            { assertEquals(3L, result.referenceId) },
            { assertEquals(35.6762, result.latitude) },
            { assertEquals(139.6503, result.longitude) }
        )
    }

    @Test
    fun `toDomain should throw NullPointerException when latitude is null`() {
        val referenceId = 1L
        val dto = UpdateLocationRequestDto(latitude = null, longitude = 10.0)
        assertThrows(NullPointerException::class.java) {
            LocationMapper.toDomain(referenceId, dto)
        }
    }

    @Test
    fun `toDomain should throw NullPointerException when longitude is null`() {
        val referenceId = 1L
        val dto = UpdateLocationRequestDto(latitude = 10.0, longitude = null)
        assertThrows(NullPointerException::class.java) {
            LocationMapper.toDomain(referenceId, dto)
        }
    }

    @Test
    fun `toDomain should throw NullPointerException when both latitude and longitude are null`() {
        val referenceId = 1L
        val dto = UpdateLocationRequestDto(latitude = null, longitude = null)
        assertThrows(NullPointerException::class.java) {
            LocationMapper.toDomain(referenceId, dto)
        }
    }
} 
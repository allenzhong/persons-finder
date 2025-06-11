package com.persons.finder.presentation.dto.response

data class PersonWithDistanceResponseDto(
    val person: PersonResponseDto,
    val distanceKm: Double
) 
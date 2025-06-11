package com.persons.finder.infrastructure.repositories.dto

data class PersonLocationDto(
    // Person fields
    val personId: Long,
    val personName: String,
    // Location fields
    val latitude: Double,
    val longitude: Double
) 
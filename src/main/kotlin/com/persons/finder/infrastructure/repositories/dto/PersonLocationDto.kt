package com.persons.finder.infrastructure.repositories.dto

data class PersonLocationDto(
    // Person fields
    val id: Long,
    val name: String,
    // Location fields
    val latitude: Double,
    val longitude: Double
) 
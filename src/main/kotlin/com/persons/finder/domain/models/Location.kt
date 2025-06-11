package com.persons.finder.domain.models

import org.springframework.data.relational.core.mapping.Table

@Table("LOCATIONS")
data class Location(
    // Tip: Person's id can be used for this field
    val referenceId: Long,
    val latitude: Double,
    val longitude: Double
)

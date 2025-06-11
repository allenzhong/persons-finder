package com.persons.finder.presentation.dto.request

import javax.validation.constraints.DecimalMax
import javax.validation.constraints.DecimalMin
import javax.validation.constraints.Max
import javax.validation.constraints.Min

data class NearbyPersonsRequestDto(
    @field:DecimalMin(value = "-90.0") @field:DecimalMax(value = "90.0")
    val lat: Double,
    
    @field:DecimalMin(value = "-180.0") @field:DecimalMax(value = "180.0")
    val lon: Double,
    
    @field:Min(value = 0) @field:Max(value = 1000)
    val radiusKm: Double
) 
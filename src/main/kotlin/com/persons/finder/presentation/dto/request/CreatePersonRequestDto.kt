package com.persons.finder.presentation.dto.request

import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

public data class CreatePersonRequestDto(
    @field:NotBlank(message = "Name is required")
    @field:Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    val name: String
) 
package com.persons.finder.presentation.dto.common

data class ErrorResponseDto(
    val timestamp: Long = System.currentTimeMillis(),
    val status: Int,
    val error: String,
    val message: Any,
    val path: String?
) 
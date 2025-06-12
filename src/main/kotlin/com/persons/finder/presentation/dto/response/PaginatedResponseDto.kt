package com.persons.finder.presentation.dto.response

data class PaginatedResponseDto<T>(
    val data: List<T>,
    val pagination: PaginationInfoDto
)

data class PaginationInfoDto(
    val page: Int,
    val pageSize: Int,
    val totalItems: Long,
    val totalPages: Int,
    val hasNext: Boolean,
    val hasPrevious: Boolean
) 
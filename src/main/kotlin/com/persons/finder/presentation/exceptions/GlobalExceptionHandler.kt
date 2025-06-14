package com.persons.finder.presentation.exceptions

import com.persons.finder.presentation.dto.common.ErrorResponseDto
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import javax.servlet.http.HttpServletRequest

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(
        ex: MethodArgumentNotValidException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponseDto> {
        val errors = ex.bindingResult.fieldErrors.associate { it.field to (it.defaultMessage ?: "Invalid") }
        val errorResponse = ErrorResponseDto(
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Bad Request",
            message = errors,
            path = request.requestURI
        )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(
        ex: HttpMessageNotReadableException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponseDto> {
        val errorResponse = ErrorResponseDto(
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Bad Request",
            message = "Invalid JSON format or type mismatch",
            path = request.requestURI
        )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(PersonNotFoundException::class)
    fun handlePersonNotFoundException(
        ex: PersonNotFoundException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponseDto> {
        val errorResponse = ErrorResponseDto(
            status = HttpStatus.NOT_FOUND.value(),
            error = "Not Found",
            message = ex.message ?: "Person not found",
            path = request.requestURI
        )
        return ResponseEntity(errorResponse, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(RuntimeException::class)
    fun handleRuntimeException(
        ex: RuntimeException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponseDto> {
        val errorResponse = ErrorResponseDto(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            error = "Internal Server Error",
            message = ex.message ?: "An unexpected error occurred",
            path = request.requestURI
        )
        return ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
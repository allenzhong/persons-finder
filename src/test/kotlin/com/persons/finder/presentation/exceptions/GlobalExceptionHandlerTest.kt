package com.persons.finder.presentation.exceptions

import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.core.MethodParameter
import org.springframework.http.HttpStatus
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import javax.servlet.http.HttpServletRequest
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GlobalExceptionHandlerTest {

    private val handler = GlobalExceptionHandler()

    @Test
    fun `should handle validation exceptions and return proper error response`() {
        // Given
        val fieldErrors = listOf(
            FieldError("createPersonRequestDto", "name", "dummy", false, null, null, "Name is required"),
            FieldError("createPersonRequestDto", "age", "dummy", false, null, null, "Age must be positive")
        )
        val bindingResult = mock<BindingResult>()
        whenever(bindingResult.fieldErrors).thenReturn(fieldErrors)
        val methodParameter = mock<MethodParameter>()
        val exception = MethodArgumentNotValidException(methodParameter, bindingResult)
        val request = mock<HttpServletRequest>()
        whenever(request.requestURI).thenReturn("/api/persons")

        // When
        val response = handler.handleValidationExceptions(exception, request)

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertNotNull(response.body)
        val errorResponse = response.body!!
        assertEquals(400, errorResponse.status)
        assertEquals("Bad Request", errorResponse.error)
        assertEquals("/api/persons", errorResponse.path)
        val errors = errorResponse.message as Map<*, *>
        assertEquals("Name is required", errors["name"])
        assertEquals("Age must be positive", errors["age"])
    }

    @Test
    fun `should handle validation exceptions with null default messages`() {
        // Given
        val fieldErrors = listOf(
            FieldError("createPersonRequestDto", "name", "dummy", false, null, null, null)
        )
        val bindingResult = mock<BindingResult>()
        whenever(bindingResult.fieldErrors).thenReturn(fieldErrors)
        val methodParameter = mock<MethodParameter>()
        val exception = MethodArgumentNotValidException(methodParameter, bindingResult)
        val request = mock<HttpServletRequest>()
        whenever(request.requestURI).thenReturn("/api/persons")

        // When
        val response = handler.handleValidationExceptions(exception, request)

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertNotNull(response.body)
        val errorResponse = response.body!!
        val errors = errorResponse.message as Map<*, *>
        assertEquals("Invalid", errors["name"])
    }

    @Test
    fun `should handle empty field errors`() {
        // Given
        val bindingResult = mock<BindingResult>()
        whenever(bindingResult.fieldErrors).thenReturn(emptyList())
        val methodParameter = mock<MethodParameter>()
        val exception = MethodArgumentNotValidException(methodParameter, bindingResult)
        val request = mock<HttpServletRequest>()
        whenever(request.requestURI).thenReturn("/api/persons")

        // When
        val response = handler.handleValidationExceptions(exception, request)

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertNotNull(response.body)
        val errorResponse = response.body!!
        val errors = errorResponse.message as Map<*, *>
        assertEquals(0, errors.size)
    }

    @Test
    fun `should handle PersonNotFoundException and return proper error response`() {
        // Given
        val personId = 999L
        val exception = PersonNotFoundException(personId)
        val request = mock<HttpServletRequest>()
        whenever(request.requestURI).thenReturn("/api/persons/999/location")

        // When
        val response = handler.handlePersonNotFoundException(exception, request)

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNotNull(response.body)
        val errorResponse = response.body!!
        assertEquals(404, errorResponse.status)
        assertEquals("Not Found", errorResponse.error)
        assertEquals("Person with ID $personId not found", errorResponse.message)
        assertEquals("/api/persons/999/location", errorResponse.path)
    }
} 
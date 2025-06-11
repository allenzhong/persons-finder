package com.persons.finder.presentation.controllers

import com.persons.finder.data.Person
import com.persons.finder.domain.services.PersonsService
import com.persons.finder.presentation.dto.mapper.PersonMapper
import com.persons.finder.presentation.dto.request.CreatePersonRequestDto
import com.persons.finder.presentation.dto.response.PersonResponseDto
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
class PersonControllerTest {

    @Mock
    private lateinit var personsService: PersonsService

    private lateinit var personController: PersonController

    @BeforeEach
    fun setUp() {
        personController = PersonController(personsService)
    }

    @Test
    fun `createPerson should return 201 Created`() {
        // Given
        val createPersonRequestDto = CreatePersonRequestDto(name = "Allen")
        val expectedPerson = Person(id = 1, name = "Allen")

        whenever(personsService.createPerson(any())).thenReturn(expectedPerson)

        // When
        val response: ResponseEntity<PersonResponseDto> = personController.createPerson(createPersonRequestDto)

        // Then
        assertNotNull(response)
        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals(201, response.statusCodeValue)
        val responseBody = response.body
        assertNotNull(responseBody)
        assertEquals(expectedPerson.id, responseBody.id)
        assertEquals(expectedPerson.name, responseBody.name)
        Mockito.verify(personsService).createPerson(any())
    }

    @Test
    fun `createPerson should call service with correct person object`() {
        // Given
        val createPersonRequestDto = CreatePersonRequestDto(name = "Test Person")
        val expectedPerson = Person(id = 1, name = "Test Person")
        val expectedDomainPerson = PersonMapper.toDomain(createPersonRequestDto)

        whenever(personsService.createPerson(expectedDomainPerson)).thenReturn(expectedPerson)

        // When
        val response = personController.createPerson(createPersonRequestDto)

        // Then
        assertNotNull(response)
        assertEquals(HttpStatus.CREATED, response.statusCode)
        val responseBody = response.body
        assertNotNull(responseBody)
        assertEquals(expectedPerson.id, responseBody.id)
        assertEquals(expectedPerson.name, responseBody.name)
        Mockito.verify(personsService).createPerson(expectedDomainPerson)
    }

}
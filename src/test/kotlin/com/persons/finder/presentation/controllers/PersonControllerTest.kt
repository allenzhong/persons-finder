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
        val expectedPerson = Person(name = "Allen", id = 1L)

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
        val expectedPerson = Person(name = "Test Person", id = 1L)
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

    @Test
    fun `getPersonsByIds should return 200 OK with list of persons`() {
        // Given
        val person1 = Person(name = "John Doe", id = 1L)
        val person2 = Person(name = "Jane Smith", id = 2L)
        val person3 = Person(name = "Bob Wilson", id = 3L)
        val ids = listOf(1L, 2L, 3L)

        whenever(personsService.getByIds(ids)).thenReturn(listOf(person1, person2, person3))

        // When
        val response: ResponseEntity<List<PersonResponseDto>> = personController.getPersonsByIds(ids)

        // Then
        assertNotNull(response)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(200, response.statusCodeValue)
        val responseBody = response.body
        assertNotNull(responseBody)
        assertEquals(3, responseBody.size)
        assertEquals(person1.id, responseBody[0].id)
        assertEquals(person1.name, responseBody[0].name)
        assertEquals(person2.id, responseBody[1].id)
        assertEquals(person2.name, responseBody[1].name)
        assertEquals(person3.id, responseBody[2].id)
        assertEquals(person3.name, responseBody[2].name)
        Mockito.verify(personsService).getByIds(ids)
    }

    @Test
    fun `getPersonsByIds should return empty list when no persons found`() {
        // Given
        val ids = listOf(1L, 2L, 3L)
        whenever(personsService.getByIds(ids)).thenReturn(emptyList())

        // When
        val response: ResponseEntity<List<PersonResponseDto>> = personController.getPersonsByIds(ids)

        // Then
        assertNotNull(response)
        assertEquals(HttpStatus.OK, response.statusCode)
        val responseBody = response.body
        assertNotNull(responseBody)
        assertEquals(0, responseBody.size)
        Mockito.verify(personsService).getByIds(ids)
    }

    @Test
    fun `getPersonsByIds should handle single id`() {
        // Given
        val person = Person(name = "Single Person", id = 1L)
        val ids = listOf(1L)

        whenever(personsService.getByIds(ids)).thenReturn(listOf(person))

        // When
        val response: ResponseEntity<List<PersonResponseDto>> = personController.getPersonsByIds(ids)

        // Then
        assertNotNull(response)
        assertEquals(HttpStatus.OK, response.statusCode)
        val responseBody = response.body
        assertNotNull(responseBody)
        assertEquals(1, responseBody.size)
        assertEquals(person.id, responseBody[0].id)
        assertEquals(person.name, responseBody[0].name)
        Mockito.verify(personsService).getByIds(ids)
    }

    @Test
    fun `getPersonsByIds should handle empty ids list`() {
        // Given
        val ids = emptyList<Long>()
        whenever(personsService.getByIds(ids)).thenReturn(emptyList())

        // When
        val response: ResponseEntity<List<PersonResponseDto>> = personController.getPersonsByIds(ids)

        // Then
        assertNotNull(response)
        assertEquals(HttpStatus.OK, response.statusCode)
        val responseBody = response.body
        assertNotNull(responseBody)
        assertEquals(0, responseBody.size)
        Mockito.verify(personsService).getByIds(ids)
    }

    @Test
    fun `getPersonsByIds should return partial results when some ids do not exist`() {
        // Given
        val person1 = Person(name = "John Doe", id = 1L)
        val person3 = Person(name = "Bob Wilson", id = 3L)
        val ids = listOf(1L, 2L, 3L)

        whenever(personsService.getByIds(ids)).thenReturn(listOf(person1, person3))

        // When
        val response: ResponseEntity<List<PersonResponseDto>> = personController.getPersonsByIds(ids)

        // Then
        assertNotNull(response)
        assertEquals(HttpStatus.OK, response.statusCode)
        val responseBody = response.body
        assertNotNull(responseBody)
        assertEquals(2, responseBody.size)
        assertEquals(person1.id, responseBody[0].id)
        assertEquals(person1.name, responseBody[0].name)
        assertEquals(person3.id, responseBody[1].id)
        assertEquals(person3.name, responseBody[1].name)
        Mockito.verify(personsService).getByIds(ids)
    }
}
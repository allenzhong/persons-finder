package com.persons.finder.domain.services

import com.persons.finder.data.Person
import com.persons.finder.infrastructure.repositories.PersonRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@ExtendWith(MockitoExtension::class)
class PersonsServiceImplTest {

    @Mock
    private lateinit var personRepository: PersonRepository

    private lateinit var personsService: PersonsServiceImpl

    @BeforeEach
    fun setUp() {
        // Initialize the service with the mocked repository
        personsService = PersonsServiceImpl(personRepository)
    }

    @Test
    fun `createPerson should call repository save and return saved person`() {
        // Given - Mock the repository behavior
        val inputPerson = Person(name = "John Doe")
        val savedPerson = Person(name = "John Doe", id = 1L)

        whenever(personRepository.save(inputPerson)).thenReturn(savedPerson)

        // When - Call the service method
        val result = personsService.createPerson(inputPerson)

        // Then - Verify the result and repository interaction
        assertNotNull(result)
        assertEquals(savedPerson.id, result.id)
        assertEquals(savedPerson.name, result.name)
        assertEquals("John Doe", result.name)
        assertEquals(1L, result.id)

        // Verify repository was called exactly once with the input person
        verify(personRepository, times(1)).save(inputPerson)
    }

    @Test
    fun `createPerson should pass the input person to repository`() {
        // Given - Mock the repository behavior
        val inputPerson = Person(name = "Jane Smith")
        val savedPerson = Person(name = "Jane Smith", id = 2L)

        whenever(personRepository.save(inputPerson)).thenReturn(savedPerson)

        // When - Call the service method
        val result = personsService.createPerson(inputPerson)

        // Then - Verify the result and repository interaction
        assertNotNull(result)
        assertEquals(savedPerson.id, result.id)
        assertEquals(savedPerson.name, result.name)

        // Verify repository was called with the exact input person
        verify(personRepository).save(inputPerson)
    }

    @Test
    fun `createPerson should handle person with existing id`() {
        // Given - Mock the repository behavior for existing person
        val inputPerson = Person(name = "Existing Person", id = 5L)
        val savedPerson = Person(name = "Existing Person", id = 5L)

        whenever(personRepository.save(inputPerson)).thenReturn(savedPerson)

        // When - Call the service method
        val result = personsService.createPerson(inputPerson)

        // Then - Verify the result and repository interaction
        assertNotNull(result)
        assertEquals(5L, result.id)
        assertEquals("Existing Person", result.name)

        // Verify repository was called with the exact input person
        verify(personRepository).save(inputPerson)
    }

    @Test
    fun `createPerson should handle different person names`() {
        // Given - Mock the repository behavior
        val inputPerson = Person(name = "Alice Johnson")
        val savedPerson = Person(name = "Alice Johnson", id = 3L)

        whenever(personRepository.save(inputPerson)).thenReturn(savedPerson)

        // When - Call the service method
        val result = personsService.createPerson(inputPerson)

        // Then - Verify the result and repository interaction
        assertNotNull(result)
        assertEquals(3L, result.id)
        assertEquals("Alice Johnson", result.name)

        // Verify repository was called with the exact input person
        verify(personRepository).save(inputPerson)
    }

    @Test
    fun `createPerson should verify repository method is called exactly once`() {
        // Given - Mock the repository behavior
        val inputPerson = Person(name = "Test Person")
        val savedPerson = Person(name = "Test Person", id = 4L)

        whenever(personRepository.save(inputPerson)).thenReturn(savedPerson)

        // When - Call the service method
        val result = personsService.createPerson(inputPerson)

        // Then - Verify the result and repository interaction
        assertNotNull(result)
        assertEquals(savedPerson.id, result.id)
        assertEquals(savedPerson.name, result.name)

        // Verify repository method was called exactly once
        verify(personRepository, times(1)).save(inputPerson)
    }

    @Test
    fun `createPerson should handle null id from repository`() {
        // Given - Mock the repository to return person with null id
        val inputPerson = Person(name = "Null ID Test")
        val savedPerson = Person(name = "Null ID Test", id = null)

        whenever(personRepository.save(inputPerson)).thenReturn(savedPerson)

        // When - Call the service method
        val result = personsService.createPerson(inputPerson)

        // Then - Verify the result and repository interaction
        assertNotNull(result)
        assertEquals(null, result.id)
        assertEquals("Null ID Test", result.name)
        assertEquals(savedPerson, result) // Verify the entire result matches

        // Verify repository was called
        verify(personRepository).save(inputPerson)
    }
}
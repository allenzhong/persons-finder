package com.persons.finder.domain.services

import com.persons.finder.domain.models.Person
import com.persons.finder.infrastructure.repositories.PersonRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
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
        val result = personsService.save(inputPerson)

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
        val result = personsService.save(inputPerson)

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
        val result = personsService.save(inputPerson)

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
        val result = personsService.save(inputPerson)

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
        val result = personsService.save(inputPerson)

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
        val result = personsService.save(inputPerson)

        // Then - Verify the result and repository interaction
        assertNotNull(result)
        assertEquals(null, result.id)
        assertEquals("Null ID Test", result.name)
        assertEquals(savedPerson, result) // Verify the entire result matches

        // Verify repository was called
        verify(personRepository).save(inputPerson)
    }

    @Test
    fun `getByIds should return list of persons when all ids exist`() {
        // Given - Mock the repository behavior
        val person1 = Person(name = "John Doe", id = 1L)
        val person2 = Person(name = "Jane Smith", id = 2L)
        val person3 = Person(name = "Bob Wilson", id = 3L)
        val ids = listOf(1L, 2L, 3L)

        whenever(personRepository.findByIds(ids)).thenReturn(listOf(person1, person2, person3))

        // When - Call the service method
        val result = personsService.getByIds(ids)

        // Then - Verify the result and repository interaction
        assertEquals(3, result.size)
        assertEquals(person1, result[0])
        assertEquals(person2, result[1])
        assertEquals(person3, result[2])

        // Verify repository was called once with all ids
        verify(personRepository).findByIds(ids)
    }

    @Test
    fun `getByIds should return only existing persons when some ids do not exist`() {
        // Given - Mock the repository behavior
        val person1 = Person(name = "John Doe", id = 1L)
        val person3 = Person(name = "Bob Wilson", id = 3L)
        val ids = listOf(1L, 2L, 3L)

        whenever(personRepository.findByIds(ids)).thenReturn(listOf(person1, person3))

        // When - Call the service method
        val result = personsService.getByIds(ids)

        // Then - Verify the result and repository interaction
        assertEquals(2, result.size)
        assertEquals(person1, result[0])
        assertEquals(person3, result[1])

        // Verify repository was called once with all ids
        verify(personRepository).findByIds(ids)
    }

    @Test
    fun `getByIds should return empty list when no ids exist`() {
        // Given - Mock the repository behavior
        val ids = listOf(1L, 2L)
        whenever(personRepository.findByIds(ids)).thenReturn(emptyList())

        // When - Call the service method
        val result = personsService.getByIds(ids)

        // Then - Verify the result and repository interaction
        assertEquals(0, result.size)

        // Verify repository was called once with all ids
        verify(personRepository).findByIds(ids)
    }

    @Test
    fun `getByIds should return empty list when empty ids list is provided`() {
        // When - Call the service method
        val result = personsService.getByIds(emptyList())

        // Then - Verify the result
        assertEquals(0, result.size)

        // Verify repository was not called
        verify(personRepository, times(0)).findByIds(any())
    }

    @Test
    fun `getByIds should handle single id`() {
        // Given - Mock the repository behavior
        val person = Person(name = "Single Person", id = 1L)
        val ids = listOf(1L)

        whenever(personRepository.findByIds(ids)).thenReturn(listOf(person))

        // When - Call the service method
        val result = personsService.getByIds(ids)

        // Then - Verify the result and repository interaction
        assertEquals(1, result.size)
        assertEquals(person, result[0])

        // Verify repository was called once with the id
        verify(personRepository).findByIds(ids)
    }
}
package com.persons.finder.domain.services

import com.persons.finder.data.Person
import com.persons.finder.data.repositories.PersonRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
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
        personsService = PersonsServiceImpl(personRepository)
    }

    @Test
    fun `createPerson should call repository save and return saved person`() {
        // Given
        val inputPerson = Person(id = 0, name = "John Doe")
        val savedPerson = Person(id = 1, name = "John Doe")

        whenever(personRepository.save(any())).thenReturn(savedPerson)

        // When
        val result = personsService.createPerson(inputPerson)

        // Then
        assertNotNull(result)
        assertEquals(savedPerson.id, result.id)
        assertEquals(savedPerson.name, result.name)
        assertEquals("John Doe", result.name)
        assertEquals(1L, result.id)

        // Verify repository was called
        verify(personRepository).save(any())
    }

    @Test
    fun `createPerson should pass the input person to repository`() {
        // Given
        val inputPerson = Person(id = 0, name = "Jane Smith")
        val savedPerson = Person(id = 2, name = "Jane Smith")

        whenever(personRepository.save(inputPerson)).thenReturn(savedPerson)

        // When
        val result = personsService.createPerson(inputPerson)

        // Then
        assertNotNull(result)
        assertEquals(savedPerson.id, result.id)
        assertEquals(savedPerson.name, result.name)

        // Verify repository was called with the exact input person
        verify(personRepository).save(inputPerson)
    }
}
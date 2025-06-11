package com.persons.finder.application.usecases

import com.persons.finder.domain.models.Person
import com.persons.finder.domain.services.PersonsService
import com.persons.finder.presentation.dto.response.PersonResponseDto
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@ExtendWith(MockitoExtension::class)
class GetPersonsByIdsUseCaseImplTest {

    @Mock
    private lateinit var personsService: PersonsService

    private lateinit var getPersonsByIdsUseCase: GetPersonsByIdsUseCaseImpl

    @BeforeEach
    fun setUp() {
        getPersonsByIdsUseCase = GetPersonsByIdsUseCaseImpl()
        getPersonsByIdsUseCase.personsService = personsService
    }

    @Test
    fun `execute should return list of persons when all ids exist`() {
        // Given
        val person1 = Person(name = "John Doe", id = 1L)
        val person2 = Person(name = "Jane Smith", id = 2L)
        val person3 = Person(name = "Bob Wilson", id = 3L)
        val ids = listOf(1L, 2L, 3L)

        whenever(personsService.getByIds(ids)).thenReturn(listOf(person1, person2, person3))

        // When
        val result = getPersonsByIdsUseCase.execute(ids)

        // Then
        assertNotNull(result)
        assertEquals(3, result.size)
        assertEquals(1L, result[0].id)
        assertEquals("John Doe", result[0].name)
        assertEquals(2L, result[1].id)
        assertEquals("Jane Smith", result[1].name)
        assertEquals(3L, result[2].id)
        assertEquals("Bob Wilson", result[2].name)
        verify(personsService).getByIds(ids)
    }

    @Test
    fun `execute should return only existing persons when some ids do not exist`() {
        // Given
        val person1 = Person(name = "John Doe", id = 1L)
        val person3 = Person(name = "Bob Wilson", id = 3L)
        val ids = listOf(1L, 2L, 3L)

        whenever(personsService.getByIds(ids)).thenReturn(listOf(person1, person3))

        // When
        val result = getPersonsByIdsUseCase.execute(ids)

        // Then
        assertNotNull(result)
        assertEquals(2, result.size)
        assertEquals(1L, result[0].id)
        assertEquals("John Doe", result[0].name)
        assertEquals(3L, result[1].id)
        assertEquals("Bob Wilson", result[1].name)
        verify(personsService).getByIds(ids)
    }

    @Test
    fun `execute should return empty list when no ids exist`() {
        // Given
        val ids = listOf(1L, 2L, 3L)
        whenever(personsService.getByIds(ids)).thenReturn(emptyList())

        // When
        val result = getPersonsByIdsUseCase.execute(ids)

        // Then
        assertNotNull(result)
        assertEquals(0, result.size)
        verify(personsService).getByIds(ids)
    }

    @Test
    fun `execute should return empty list when empty ids list is provided`() {
        // Given
        val ids = emptyList<Long>()
        whenever(personsService.getByIds(ids)).thenReturn(emptyList())

        // When
        val result = getPersonsByIdsUseCase.execute(ids)

        // Then
        assertNotNull(result)
        assertEquals(0, result.size)
        verify(personsService).getByIds(ids)
    }

    @Test
    fun `execute should handle single id`() {
        // Given
        val person = Person(name = "Single Person", id = 1L)
        val ids = listOf(1L)

        whenever(personsService.getByIds(ids)).thenReturn(listOf(person))

        // When
        val result = getPersonsByIdsUseCase.execute(ids)

        // Then
        assertNotNull(result)
        assertEquals(1, result.size)
        assertEquals(1L, result[0].id)
        assertEquals("Single Person", result[0].name)
        verify(personsService).getByIds(ids)
    }

    @Test
    fun `execute should handle persons with null id`() {
        // Given
        val person = Person(name = "Null ID Person", id = null)
        val ids = listOf(1L)

        whenever(personsService.getByIds(ids)).thenReturn(listOf(person))

        // When
        val result = getPersonsByIdsUseCase.execute(ids)

        // Then
        assertNotNull(result)
        assertEquals(1, result.size)
        assertEquals(null as Long?, result[0].id)
        assertEquals("Null ID Person", result[0].name)
        verify(personsService).getByIds(ids)
    }
} 
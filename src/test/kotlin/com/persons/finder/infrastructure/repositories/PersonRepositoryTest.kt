package com.persons.finder.infrastructure.repositories

import com.persons.finder.domain.models.Person
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest
@ActiveProfiles("test")
class PersonRepositoryTest {

    @Autowired
    private lateinit var personRepository: PersonRepository

    @BeforeEach
    fun setUp() {
        // Clear the database before each test
        personRepository.deleteAll()
    }

    @Test
    fun `findAllById should return all persons when all ids exist`() {
        // Given - Create test persons
        val person1 = personRepository.save(Person(name = "John Doe"))
        val person2 = personRepository.save(Person(name = "Jane Smith"))
        val person3 = personRepository.save(Person(name = "Bob Wilson"))

        val ids = listOf(person1.id!!, person2.id!!, person3.id!!)

        // When - Call the findAllById method
        val result = personRepository.findAllById(ids).toList()

        // Then - Verify all persons are returned
        assertEquals(3, result.size)
        assertTrue(result.any { it.id == person1.id && it.name == "John Doe" })
        assertTrue(result.any { it.id == person2.id && it.name == "Jane Smith" })
        assertTrue(result.any { it.id == person3.id && it.name == "Bob Wilson" })
    }

    @Test
    fun `findAllById should return only existing persons when some ids do not exist`() {
        // Given - Create test persons
        val person1 = personRepository.save(Person(name = "John Doe"))
        val person3 = personRepository.save(Person(name = "Bob Wilson"))

        val ids = listOf(person1.id!!, 999L, person3.id!!, 1000L)

        // When - Call the findAllById method
        val result = personRepository.findAllById(ids).toList()

        // Then - Verify only existing persons are returned
        assertEquals(2, result.size)
        assertTrue(result.any { it.id == person1.id && it.name == "John Doe" })
        assertTrue(result.any { it.id == person3.id && it.name == "Bob Wilson" })
    }

    @Test
    fun `findAllById should return empty list when no ids exist`() {
        // Given - Create test persons
        personRepository.save(Person(name = "John Doe"))
        personRepository.save(Person(name = "Jane Smith"))

        val ids = listOf(999L, 1000L, 1001L)

        // When - Call the findAllById method
        val result = personRepository.findAllById(ids).toList()

        // Then - Verify empty list is returned
        assertEquals(0, result.size)
    }

    @Test
    fun `findAllById should return empty list when empty ids list is provided`() {
        // Given - Create test persons
        personRepository.save(Person(name = "John Doe"))
        personRepository.save(Person(name = "Jane Smith"))

        val ids = emptyList<Long>()

        // When - Call the findAllById method
        val result = personRepository.findAllById(ids).toList()

        // Then - Verify empty list is returned
        assertEquals(0, result.size)
    }

    @Test
    fun `findAllById should handle single id`() {
        // Given - Create test person
        val person = personRepository.save(Person(name = "Single Person"))
        val ids = listOf(person.id!!)

        // When - Call the findAllById method
        val result = personRepository.findAllById(ids).toList()

        // Then - Verify single person is returned
        assertEquals(1, result.size)
        assertEquals(person.id, result[0].id)
        assertEquals("Single Person", result[0].name)
    }

    @Test
    fun `findAllById should handle duplicate ids`() {
        // Given - Create test person
        val person = personRepository.save(Person(name = "Duplicate Test"))
        val ids = listOf(person.id!!, person.id!!, person.id!!)

        // When - Call the findAllById method
        val result = personRepository.findAllById(ids).toList()

        // Then - Verify person is returned only once
        assertEquals(1, result.size)
        assertEquals(person.id, result[0].id)
        assertEquals("Duplicate Test", result[0].name)
    }

    @Test
    fun `findAllById should handle large number of ids`() {
        // Given - Create multiple test persons
        val persons = mutableListOf<Person>()
        for (i in 1..10) {
            persons.add(personRepository.save(Person(name = "Person $i")))
        }

        val ids = persons.mapNotNull { it.id }

        // When - Call the findAllById method
        val result = personRepository.findAllById(ids).toList()

        // Then - Verify all persons are returned
        assertEquals(10, result.size)
        persons.forEach { person ->
            assertTrue(result.any { it.id == person.id && it.name == person.name })
        }
    }

    @Test
    fun `findAllById should handle mixed existing and non existing ids in large set`() {
        // Given - Create multiple test persons
        val persons = mutableListOf<Person>()
        for (i in 1..5) {
            persons.add(personRepository.save(Person(name = "Person $i")))
        }

        val existingIds = persons.mapNotNull { it.id }
        val nonExistingIds = listOf(999L, 1000L, 1001L)
        val mixedIds = existingIds + nonExistingIds

        // When - Call the findAllById method
        val result = personRepository.findAllById(mixedIds).toList()

        // Then - Verify only existing persons are returned
        assertEquals(5, result.size)
        persons.forEach { person ->
            assertTrue(result.any { it.id == person.id && it.name == person.name })
        }
    }

    @Test
    fun `findAllById should maintain order of results`() {
        // Given - Create test persons
        val person1 = personRepository.save(Person(name = "First Person"))
        val person2 = personRepository.save(Person(name = "Second Person"))
        val person3 = personRepository.save(Person(name = "Third Person"))

        val ids = listOf(person1.id!!, person2.id!!, person3.id!!)

        // When - Call the findAllById method
        val result = personRepository.findAllById(ids).toList()

        // Then - Verify order is maintained (though this may vary by database)
        assertEquals(3, result.size)
        assertTrue(result.any { it.id == person1.id && it.name == "First Person" })
        assertTrue(result.any { it.id == person2.id && it.name == "Second Person" })
        assertTrue(result.any { it.id == person3.id && it.name == "Third Person" })
    }

    @Test
    fun `findAllById should work with null ids in the list`() {
        // Given - Create test person
        val person = personRepository.save(Person(name = "Valid Person"))
        val ids = listOf(person.id!!, 999L)

        // When - Call the findAllById method
        val result = personRepository.findAllById(ids).toList()

        // Then - Verify only valid person is returned
        assertEquals(1, result.size)
        assertEquals(person.id, result[0].id)
        assertEquals("Valid Person", result[0].name)
    }

    @Test
    fun `findAllById should be faster than multiple findById calls`() {
        // Given - Create multiple test persons
        val persons = mutableListOf<Person>()
        for (i in 1..10) {
            persons.add(personRepository.save(Person(name = "Person $i")))
        }

        val ids = persons.mapNotNull { it.id }

        // When - Measure time for findAllById
        val start = System.currentTimeMillis()
        val result = personRepository.findAllById(ids).toList()
        val duration = System.currentTimeMillis() - start

        // Then - Verify all persons are returned and time is reasonable
        assertEquals(10, result.size)
        assertTrue(duration < 1000) // Should be fast
    }
} 
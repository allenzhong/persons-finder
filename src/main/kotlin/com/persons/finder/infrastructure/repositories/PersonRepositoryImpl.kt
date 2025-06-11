package com.persons.finder.infrastructure.repositories

import com.persons.finder.data.Person
import com.persons.finder.data.repositories.PersonRepository
import org.springframework.stereotype.Repository

@Repository
class PersonRepositoryImpl : PersonRepository {

    // Temporary in-memory storage for now
    private val persons = mutableMapOf<Long, Person>()
    private var nextId = 1L

    override fun save(person: Person): Person {
        val id = if (person.id == 0L) nextId++ else person.id
        val personToSave = person.copy(id = id)
        persons[id] = personToSave
        return personToSave
    }

    override fun findById(id: Long): Person? {
        return persons[id]
    }

    override fun findAll(): List<Person> {
        return persons.values.toList()
    }
}
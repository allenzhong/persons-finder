package com.persons.finder.domain.services

import com.persons.finder.data.Person
import org.springframework.stereotype.Service

@Service
class PersonsServiceImpl : PersonsService {

    override fun getById(id: Long): Person {
        TODO("Not yet implemented")
    }

    override fun save(person: Person) {
        TODO("Not yet implemented")
    }

    override fun createPerson(person: Person): Person {
        // For now, just return the person as-is
        // In a real implementation, this would save to database and return with generated ID
        return person
    }
}
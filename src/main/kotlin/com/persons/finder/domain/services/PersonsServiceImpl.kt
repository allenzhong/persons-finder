package com.persons.finder.domain.services

import com.persons.finder.domain.models.Person
import com.persons.finder.infrastructure.repositories.PersonRepository
import com.persons.finder.presentation.exceptions.PersonNotFoundException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class PersonsServiceImpl(
    private val personRepository: PersonRepository
) : PersonsService {

    override fun getById(id: Long): Person {
        return personRepository.findByIdOrNull(id) 
            ?: throw PersonNotFoundException(id)
    }

    override fun save(person: Person): Person {
        return personRepository.save(person)
    }

    override fun getByIds(ids: List<Long>): List<Person> {
        return if (ids.isEmpty()) {
            emptyList()
        } else {
            personRepository.findByIds(ids)
        }
    }
}
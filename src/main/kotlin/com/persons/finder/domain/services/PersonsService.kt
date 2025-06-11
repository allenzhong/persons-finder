package com.persons.finder.domain.services

import com.persons.finder.domain.models.Person

interface PersonsService {
    fun getById(id: Long): Person
    fun save(person: Person): Person
    fun getByIds(ids: List<Long>): List<Person>
}
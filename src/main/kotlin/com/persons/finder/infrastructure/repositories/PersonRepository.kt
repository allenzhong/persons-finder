package com.persons.finder.data.repositories

import com.persons.finder.data.Person

interface PersonRepository {
    fun save(person: Person): Person
    fun findById(id: Long): Person?
    fun findAll(): List<Person>
} 
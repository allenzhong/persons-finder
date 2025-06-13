package com.persons.finder.infrastructure.repositories

import com.persons.finder.domain.models.Person
import org.springframework.data.repository.CrudRepository

interface PersonRepository : CrudRepository<Person, Long> 
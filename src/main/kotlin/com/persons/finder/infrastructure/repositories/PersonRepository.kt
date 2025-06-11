package com.persons.finder.infrastructure.repositories

import com.persons.finder.data.Person
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

interface PersonRepository : CrudRepository<Person, Long> {
    
    @Query("SELECT * FROM PERSONS WHERE id IN (:ids)")
    fun findByIds(@Param("ids") ids: List<Long>): List<Person>
} 
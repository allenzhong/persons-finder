package com.persons.finder.domain.models

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("PERSONS")
data class Person(
    val name: String,
    @Id val id: Long? = null
)

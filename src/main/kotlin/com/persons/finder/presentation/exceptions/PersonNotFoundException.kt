package com.persons.finder.presentation.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
class PersonNotFoundException(personId: Long) : RuntimeException("Person with ID $personId not found") 
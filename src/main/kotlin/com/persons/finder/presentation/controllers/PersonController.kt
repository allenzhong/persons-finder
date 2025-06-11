package com.persons.finder.presentation.controllers

import com.persons.finder.domain.services.LocationsService
import com.persons.finder.domain.services.PersonsService
import com.persons.finder.presentation.dto.mapper.LocationMapper
import com.persons.finder.presentation.dto.mapper.PersonMapper
import com.persons.finder.presentation.dto.request.CreatePersonRequestDto
import com.persons.finder.presentation.dto.request.UpdateLocationRequestDto
import com.persons.finder.presentation.dto.response.LocationResponseDto
import com.persons.finder.presentation.dto.response.PersonResponseDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("api/v1/persons")
class PersonController @Autowired constructor(
    private val personsService: PersonsService,
    private val locationsService: LocationsService
) {
    @PostMapping("")
    fun createPerson(@Valid @RequestBody createPersonRequestDto: CreatePersonRequestDto): ResponseEntity<PersonResponseDto> {
        val person = PersonMapper.toDomain(createPersonRequestDto)
        val createdPerson = personsService.save(person)
        val response = PersonMapper.toResponseDto(createdPerson)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping("")
    fun getPersonsByIds(@RequestParam("id") ids: List<Long>): ResponseEntity<List<PersonResponseDto>> {
        val persons = personsService.getByIds(ids)
        val responses = persons.map { PersonMapper.toResponseDto(it) }
        return ResponseEntity.ok(responses)
    }

    @PutMapping("/{id}/location")
    fun updatePersonLocation(
        @PathVariable id: Long,
        @Valid @RequestBody updateLocationRequestDto: UpdateLocationRequestDto
    ): ResponseEntity<LocationResponseDto> {
        // First verify the person exists
        personsService.getById(id)
        
        // Create or update the location
        val location = LocationMapper.toDomain(id, updateLocationRequestDto)
        locationsService.addLocation(location)
        
        val response = LocationMapper.toResponseDto(location)
        return ResponseEntity.ok(response)
    }




    /*
        TODO GET API to retrieve people around query location with a radius in KM, Use query param for radius.
        TODO API just return a list of persons ids (JSON)
        // Example
        // John wants to know who is around his location within a radius of 10km
        // API would be called using John's id and a radius 10km
     */
}
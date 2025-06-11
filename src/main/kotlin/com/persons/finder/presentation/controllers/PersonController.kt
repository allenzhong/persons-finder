package com.persons.finder.presentation.controllers

import com.persons.finder.domain.services.LocationsService
import com.persons.finder.domain.services.PersonsService
import com.persons.finder.domain.models.Location
import com.persons.finder.application.usecases.CreatePersonUseCase
import com.persons.finder.application.usecases.GetPersonsByIdsUseCase
import com.persons.finder.application.usecases.UpdatePersonLocationUseCase
import com.persons.finder.application.usecases.GetNearbyPersonsUseCase
import com.persons.finder.presentation.dto.mapper.LocationMapper
import com.persons.finder.presentation.dto.mapper.PersonMapper
import com.persons.finder.presentation.dto.request.CreatePersonRequestDto
import com.persons.finder.presentation.dto.request.UpdateLocationRequestDto
import com.persons.finder.presentation.dto.response.LocationResponseDto
import com.persons.finder.presentation.dto.response.PersonResponseDto
import com.persons.finder.presentation.dto.response.PersonWithDistanceResponseDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.validation.Valid
import javax.validation.constraints.DecimalMax
import javax.validation.constraints.DecimalMin
import javax.validation.constraints.Max
import javax.validation.constraints.Min

@RestController
@RequestMapping("api/v1/persons")
@Validated
class PersonController {
    @Autowired
    internal lateinit var personsService: PersonsService

    @Autowired
    internal lateinit var locationsService: LocationsService

    @Autowired
    internal lateinit var createPersonUseCase: CreatePersonUseCase

    @Autowired
    internal lateinit var getPersonsByIdsUseCase: GetPersonsByIdsUseCase

    @Autowired
    internal lateinit var updatePersonLocationUseCase: UpdatePersonLocationUseCase

    @Autowired
    internal lateinit var getNearbyPersonsUseCase: GetNearbyPersonsUseCase

    @PostMapping("")
    fun createPerson(@Valid @RequestBody createPersonRequestDto: CreatePersonRequestDto): ResponseEntity<PersonResponseDto> {
        val response = createPersonUseCase.execute(createPersonRequestDto)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping("")
    fun getPersonsByIds(@RequestParam("id") ids: List<Long>): ResponseEntity<List<PersonResponseDto>> {
        val responses = getPersonsByIdsUseCase.execute(ids)
        return ResponseEntity.ok(responses)
    }

    @PutMapping("/{id}/location")
    fun updatePersonLocation(
        @PathVariable id: Long,
        @Valid @RequestBody updateLocationRequestDto: UpdateLocationRequestDto
    ): ResponseEntity<LocationResponseDto> {
        val response = updatePersonLocationUseCase.execute(id, updateLocationRequestDto)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/nearby")
    fun getNearbyPersons(
        @RequestParam @DecimalMin(value = "-90.0") @DecimalMax(value = "90.0") lat: Double,
        @RequestParam @DecimalMin(value = "-180.0") @DecimalMax(value = "180.0") lon: Double,
        @RequestParam @Min(value = 0) @Max(value = 1000) radiusKm: Double
    ): ResponseEntity<List<PersonWithDistanceResponseDto>> {
        val responses = getNearbyPersonsUseCase.execute(lat, lon, radiusKm)
        return ResponseEntity.ok(responses)
    }
}
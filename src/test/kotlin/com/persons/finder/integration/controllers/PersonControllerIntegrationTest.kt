package com.persons.finder.integration.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.persons.finder.presentation.dto.request.CreatePersonRequestDto
import com.persons.finder.presentation.dto.request.UpdateLocationRequestDto
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("integration")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class PersonControllerIntegrationTest {

    @Autowired
    private lateinit var webApplicationContext: WebApplicationContext

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(webApplicationContext)
            .build()
    }

    @Test
    fun `should create person successfully`() {
        // Given
        val createPersonRequest = CreatePersonRequestDto(name = "John Doe")

        // When & Then
        mockMvc.perform(
            post("/api/v1/persons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createPersonRequest))
        )
            .andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.name").value("John Doe"))
    }

    @Test
    fun `should return 400 when creating person with empty name`() {
        // Given
        val createPersonRequest = CreatePersonRequestDto(name = "")

        // When & Then
        mockMvc.perform(
            post("/api/v1/persons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createPersonRequest))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should return 400 when creating person with name too long`() {
        // Given
        val createPersonRequest = CreatePersonRequestDto(name = "a".repeat(101))

        // When & Then
        mockMvc.perform(
            post("/api/v1/persons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createPersonRequest))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should get persons by ids successfully`() {
        // Given - Create persons first
        val person1 = CreatePersonRequestDto(name = "Alice")
        val person2 = CreatePersonRequestDto(name = "Bob")

        val person1Response = mockMvc.perform(
            post("/api/v1/persons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(person1))
        )
            .andExpect(status().isCreated)
            .andReturn()

        val person2Response = mockMvc.perform(
            post("/api/v1/persons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(person2))
        )
            .andExpect(status().isCreated)
            .andReturn()

        val person1Id = objectMapper.readTree(person1Response.response.contentAsString).get("id").asLong()
        val person2Id = objectMapper.readTree(person2Response.response.contentAsString).get("id").asLong()

        // When & Then
        mockMvc.perform(
            get("/api/v1/persons")
                .param("id", person1Id.toString(), person2Id.toString())
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$[0].id").value(person1Id))
            .andExpect(jsonPath("$[0].name").value("Alice"))
            .andExpect(jsonPath("$[1].id").value(person2Id))
            .andExpect(jsonPath("$[1].name").value("Bob"))
    }

    @Test
    fun `should return empty list when getting persons with non-existent ids`() {
        // When & Then
        mockMvc.perform(
            get("/api/v1/persons")
                .param("id", "999", "1000")
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$").isEmpty)
    }

    @Test
    fun `should update person location successfully`() {
        // Given - Create a person first
        val createPersonRequest = CreatePersonRequestDto(name = "Charlie")
        val personResponse = mockMvc.perform(
            post("/api/v1/persons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createPersonRequest))
        )
            .andExpect(status().isCreated)
            .andReturn()

        val personId = objectMapper.readTree(personResponse.response.contentAsString).get("id").asLong()
        val updateLocationRequest = UpdateLocationRequestDto(latitude = 40.7128, longitude = -74.0060)

        // When & Then
        mockMvc.perform(
            put("/api/v1/persons/$personId/location")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateLocationRequest))
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.latitude").value(40.7128))
            .andExpect(jsonPath("$.longitude").value(-74.0060))
    }

    @Test
    fun `should return 400 when updating location with invalid latitude`() {
        // Given - Create a person first
        val createPersonRequest = CreatePersonRequestDto(name = "David")
        val personResponse = mockMvc.perform(
            post("/api/v1/persons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createPersonRequest))
        )
            .andExpect(status().isCreated)
            .andReturn()

        val personId = objectMapper.readTree(personResponse.response.contentAsString).get("id").asLong()
        val updateLocationRequest = UpdateLocationRequestDto(latitude = 91.0, longitude = -74.0060)

        // When & Then
        mockMvc.perform(
            put("/api/v1/persons/$personId/location")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateLocationRequest))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should return 400 when updating location with invalid longitude`() {
        // Given - Create a person first
        val createPersonRequest = CreatePersonRequestDto(name = "Eve")
        val personResponse = mockMvc.perform(
            post("/api/v1/persons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createPersonRequest))
        )
            .andExpect(status().isCreated)
            .andReturn()

        val personId = objectMapper.readTree(personResponse.response.contentAsString).get("id").asLong()
        val updateLocationRequest = UpdateLocationRequestDto(latitude = 40.7128, longitude = 181.0)

        // When & Then
        mockMvc.perform(
            put("/api/v1/persons/$personId/location")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateLocationRequest))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should get nearby persons successfully`() {
        // Given - Create persons as in find-nearby.http
        val alice = CreatePersonRequestDto(name = "Alice Sherwood")
        val bob = CreatePersonRequestDto(name = "Bob Sherwood")
        val charlie = CreatePersonRequestDto(name = "Charlie Sherwood")
        val dave = CreatePersonRequestDto(name = "Dave Albany")
        val eve = CreatePersonRequestDto(name = "Eve Albany")

        val aliceId = objectMapper.readTree(
            mockMvc.perform(post("/api/v1/persons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(alice)))
                .andExpect(status().isCreated)
                .andReturn().response.contentAsString
        ).get("id").asLong()

        val bobId = objectMapper.readTree(
            mockMvc.perform(post("/api/v1/persons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bob)))
                .andExpect(status().isCreated)
                .andReturn().response.contentAsString
        ).get("id").asLong()

        val charlieId = objectMapper.readTree(
            mockMvc.perform(post("/api/v1/persons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(charlie)))
                .andExpect(status().isCreated)
                .andReturn().response.contentAsString
        ).get("id").asLong()

        val daveId = objectMapper.readTree(
            mockMvc.perform(post("/api/v1/persons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dave)))
                .andExpect(status().isCreated)
                .andReturn().response.contentAsString
        ).get("id").asLong()

        val eveId = objectMapper.readTree(
            mockMvc.perform(post("/api/v1/persons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eve)))
                .andExpect(status().isCreated)
                .andReturn().response.contentAsString
        ).get("id").asLong()

        // Set locations for Sherwood people
        mockMvc.perform(put("/api/v1/persons/$bobId/location")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""{"latitude": -36.71728717024384, "longitude": 174.73580751020845}"""))
            .andExpect(status().isOk)
        mockMvc.perform(put("/api/v1/persons/$charlieId/location")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""{"latitude": -36.717300, "longitude": 174.735800}"""))
            .andExpect(status().isOk)
        mockMvc.perform(put("/api/v1/persons/$charlieId/location")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""{"latitude": -36.717250, "longitude": 174.735850}"""))
            .andExpect(status().isOk)

        // Set locations for Albany people
        mockMvc.perform(put("/api/v1/persons/$daveId/location")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""{"latitude": -36.72789864941169, "longitude": 174.71023398191028}"""))
            .andExpect(status().isOk)
        mockMvc.perform(put("/api/v1/persons/$eveId/location")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""{"latitude": -36.727900, "longitude": 174.710200}"""))
            .andExpect(status().isOk)

        // When & Then - Find people near Sherwood Reserve (should only return Sherwood people with radius 2.6km)
        val sherwoodResult = mockMvc.perform(get("/api/v1/persons/nearby")
            .param("lat", "-36.71728717024384")
            .param("lon", "174.73580751020845")
            .param("radiusKm", "2")) //only in sherwood
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn()
        val sherwoodJson = objectMapper.readTree(sherwoodResult.response.contentAsString)
        println("Sherwood response: ${sherwoodResult.response.contentAsString}")
        // Should contain Bob and Charlie (Alice has no location set)
        val sherwoodIds = sherwoodJson["data"].map { it["person"]["id"].asLong() }.toSet()
        println("Sherwood IDs found: $sherwoodIds")
        println("Expected Bob ID: $bobId, Charlie ID: $charlieId")
        assert(sherwoodIds.contains(bobId))
        assert(sherwoodIds.contains(charlieId))
        assert(!sherwoodIds.contains(daveId))
        assert(!sherwoodIds.contains(eveId))
        // Assert pagination
        assert(sherwoodJson["pagination"].isObject)
        assert(sherwoodJson["pagination"]["totalItems"].asInt() >= 2)
        assert(sherwoodJson["pagination"]["page"].asInt() == 1)
        assert(sherwoodJson["pagination"]["pageSize"].asInt() == 500)

        // When & Then - Find people near Albany Northshore (should return all Sherwood people with radius 2.8km)
        val albanyResult = mockMvc.perform(get("/api/v1/persons/nearby")
            .param("lat", "-36.7172871702")
            .param("lon", "174.710233")
            .param("radiusKm", "2.8"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn()
        val albanyJson = objectMapper.readTree(albanyResult.response.contentAsString)
        println("Albany response: ${albanyResult.response.contentAsString}")
        val albanyIds = albanyJson["data"].map { it["person"]["id"].asLong() }.toSet()
        println("Albany IDs found: $albanyIds")
        println("Expected all IDs: $bobId, $charlieId, $daveId, $eveId")
        // Should contain Bob, Charlie, Dave, Eve (Alice has no location set)
        assert(albanyIds.contains(bobId))
        assert(albanyIds.contains(charlieId))
        assert(albanyIds.contains(daveId))
        assert(albanyIds.contains(eveId))
        // Assert pagination
        assert(albanyJson["pagination"].isObject)
        assert(albanyJson["pagination"]["totalItems"].asInt() >= 4)
        assert(albanyJson["pagination"]["page"].asInt() == 1)
        assert(albanyJson["pagination"]["pageSize"].asInt() == 500)
    }
}
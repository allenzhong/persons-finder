package com.persons.finder.infrastructure.repositories

import com.persons.finder.domain.models.Location
import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.Repository
import org.springframework.data.repository.query.Param

interface LocationRepository : Repository<Location, Long> {
    
    @Query("SELECT * FROM LOCATIONS WHERE reference_id = :referenceId")
    fun findByReferenceId(@Param("referenceId") referenceId: Long): Location?
    
    @Query("SELECT * FROM LOCATIONS")
    fun findAll(): List<Location>
    
    @Query("""
        SELECT l.*, p.id as person_id, p.name as person_name 
        FROM LOCATIONS l 
        JOIN PERSONS p ON l.reference_id = p.id 
        WHERE l.latitude BETWEEN :minLat AND :maxLat 
        AND l.longitude BETWEEN :minLon AND :maxLon
    """)
    fun findLocationsWithPersonsInBoundingBox(
        @Param("minLat") minLat: Double,
        @Param("maxLat") maxLat: Double,
        @Param("minLon") minLon: Double,
        @Param("maxLon") maxLon: Double
    ): List<LocationWithPerson>
    
    @Query("""
        SELECT * FROM LOCATIONS 
        WHERE latitude BETWEEN :minLat AND :maxLat 
        AND longitude BETWEEN :minLon AND :maxLon
    """)
    fun findLocationsInBoundingBox(
        @Param("minLat") minLat: Double,
        @Param("maxLat") maxLat: Double,
        @Param("minLon") minLon: Double,
        @Param("maxLon") maxLon: Double
    ): List<Location>
    
    @Modifying
    @Query("INSERT INTO LOCATIONS (reference_id, latitude, longitude) VALUES (:referenceId, :latitude, :longitude)")
    fun insertLocation(
        @Param("referenceId") referenceId: Long,
        @Param("latitude") latitude: Double,
        @Param("longitude") longitude: Double
    )
    
    @Modifying
    @Query("UPDATE LOCATIONS SET latitude = :latitude, longitude = :longitude WHERE reference_id = :referenceId")
    fun updateLocation(
        @Param("referenceId") referenceId: Long,
        @Param("latitude") latitude: Double,
        @Param("longitude") longitude: Double
    )
}

data class LocationWithPerson(
    val referenceId: Long,
    val latitude: Double,
    val longitude: Double,
    val personId: Long,
    val personName: String
) 
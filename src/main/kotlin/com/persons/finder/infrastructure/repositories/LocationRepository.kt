package com.persons.finder.infrastructure.repositories

import com.persons.finder.domain.models.Location
import com.persons.finder.infrastructure.repositories.dto.PersonLocationDto
import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.Repository
import org.springframework.data.repository.query.Param

interface LocationRepository : Repository<Location, Long> {
    
    @Query("SELECT * FROM locations WHERE reference_id = :referenceId")
    fun findByReferenceId(@Param("referenceId") referenceId: Long): Location?
    
    @Query("SELECT * FROM locations")
    fun findAll(): List<Location>
    
    @Query("""
        SELECT 
            p.id AS id,
            p.name AS name,
            l.latitude,
            l.longitude
        FROM persons p
        INNER JOIN locations l ON p.id = l.reference_id
        WHERE l.latitude BETWEEN :minLat AND :maxLat 
        AND l.longitude BETWEEN :minLon AND :maxLon
    """)
    fun findPersonsWithLocationsInBoundingBox(
        @Param("minLat") minLat: Double,
        @Param("maxLat") maxLat: Double,
        @Param("minLon") minLon: Double,
        @Param("maxLon") maxLon: Double
    ): List<PersonLocationDto>
    
    @Query("""
        SELECT 
            p.id AS id,
            p.name AS name,
            l.latitude,
            l.longitude
        FROM persons p
        INNER JOIN locations l ON p.id = l.reference_id
        WHERE l.latitude BETWEEN :minLat AND :maxLat 
        AND l.longitude BETWEEN :minLon AND :maxLon
        ORDER BY l.latitude, l.longitude
        LIMIT :limit OFFSET :offset
    """)
    fun findPersonsWithLocationsInBoundingBoxPaginated(
        @Param("minLat") minLat: Double,
        @Param("maxLat") maxLat: Double,
        @Param("minLon") minLon: Double,
        @Param("maxLon") maxLon: Double,
        @Param("limit") limit: Int,
        @Param("offset") offset: Int
    ): List<PersonLocationDto>
    
    @Query("""
        SELECT COUNT(*)
        FROM persons p
        INNER JOIN locations l ON p.id = l.reference_id
        WHERE l.latitude BETWEEN :minLat AND :maxLat 
        AND l.longitude BETWEEN :minLon AND :maxLon
    """)
    fun countPersonsWithLocationsInBoundingBox(
        @Param("minLat") minLat: Double,
        @Param("maxLat") maxLat: Double,
        @Param("minLon") minLon: Double,
        @Param("maxLon") maxLon: Double
    ): Long
    
    @Modifying
    @Query("INSERT INTO locations (reference_id, latitude, longitude) VALUES (:referenceId, :latitude, :longitude)")
    fun insertLocation(
        @Param("referenceId") referenceId: Long,
        @Param("latitude") latitude: Double,
        @Param("longitude") longitude: Double
    )
    
    @Modifying
    @Query("UPDATE locations SET latitude = :latitude, longitude = :longitude WHERE reference_id = :referenceId")
    fun updateLocation(
        @Param("referenceId") referenceId: Long,
        @Param("latitude") latitude: Double,
        @Param("longitude") longitude: Double
    )
    
    @Modifying
    @Query("DELETE FROM locations")
    fun deleteAll()
}
package com.persons.finder.seeding

import com.persons.finder.domain.models.Location
import com.persons.finder.domain.models.Person
import com.persons.finder.infrastructure.repositories.LocationRepository
import com.persons.finder.infrastructure.repositories.PersonRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.atomic.AtomicLong
import kotlin.random.Random

@Service
class DataSeeder {
    
    private val logger = LoggerFactory.getLogger(DataSeeder::class.java)
    
    @Autowired
    private lateinit var personRepository: PersonRepository
    
    @Autowired
    private lateinit var locationRepository: LocationRepository
    
    private val progressCounter = AtomicLong(0)
    
    companion object {
        private const val BATCH_SIZE = 1000
        private const val PROGRESS_LOG_INTERVAL = 10000
    }
    
    @Transactional
    fun seedPersons(count: Long): List<Long> {
        logger.info("Starting to seed $count persons...")
        val startTime = System.currentTimeMillis()
        val personIds = mutableListOf<Long>()
        
        val batches = (count / BATCH_SIZE) + if (count % BATCH_SIZE > 0) 1 else 0
        
        for (batch in 0 until batches) {
            val batchStart = batch * BATCH_SIZE
            val batchEnd = minOf(batchStart + BATCH_SIZE, count)
            val batchSize = (batchEnd - batchStart).toInt()
            
            val persons = (0 until batchSize).map { index ->
                val globalIndex = batchStart + index
                Person(name = generateRandomName(globalIndex))
            }
            
            val savedPersons = personRepository.saveAll(persons)
            personIds.addAll(savedPersons.mapNotNull { it.id })
            
            progressCounter.addAndGet(batchSize.toLong())
            logProgress("persons", count)
        }
        
        val duration = System.currentTimeMillis() - startTime
        logger.info("Completed seeding $count persons in ${duration}ms (${count * 1000 / duration} records/sec)")
        
        return personIds
    }
    
    @Transactional
    fun seedLocations(personIds: List<Long>, distribution: LocationDistribution = LocationDistribution.WORLDWIDE) {
        logger.info("Starting to seed ${personIds.size} locations with ${distribution.name} distribution...")
        val startTime = System.currentTimeMillis()
        
        val batches = (personIds.size / BATCH_SIZE) + if (personIds.size % BATCH_SIZE > 0) 1 else 0
        
        for (batch in 0 until batches) {
            val batchStart = batch * BATCH_SIZE
            val batchEnd = minOf(batchStart + BATCH_SIZE, personIds.size)
            val batchPersonIds = personIds.subList(batchStart, batchEnd)
            
            batchPersonIds.forEach { personId ->
                val (lat, lon) = generateRandomLocation(distribution)
                locationRepository.insertLocation(personId, lat, lon)
            }
            
            progressCounter.addAndGet(batchEnd - batchStart.toLong())
            logProgress("locations", personIds.size.toLong())
        }
        
        val duration = System.currentTimeMillis() - startTime
        logger.info("Completed seeding ${personIds.size} locations in ${duration}ms (${personIds.size * 1000 / duration} records/sec)")
    }
    
    @Transactional
    fun seedCompleteDataset(personCount: Long, distribution: LocationDistribution = LocationDistribution.WORLDWIDE): SeedingResult {
        logger.info("Starting complete dataset seeding: $personCount persons with locations")
        val startTime = System.currentTimeMillis()
        
        // Reset progress counter
        progressCounter.set(0)
        
        // Seed persons first
        val personIds = seedPersons(personCount)
        
        // Reset progress counter for locations
        progressCounter.set(0)
        
        // Seed locations
        seedLocations(personIds, distribution)
        
        val totalDuration = System.currentTimeMillis() - startTime
        logger.info("Completed complete dataset seeding in ${totalDuration}ms")
        
        return SeedingResult(
            personCount = personCount,
            locationCount = personCount,
            totalDurationMs = totalDuration,
            averageRecordsPerSecond = (personCount * 2 * 1000) / totalDuration
        )
    }
    
    private fun generateRandomName(index: Long): String {
        val firstNames = listOf(
            "Alice", "Bob", "Charlie", "Diana", "Eve", "Frank", "Grace", "Henry",
            "Ivy", "Jack", "Kate", "Liam", "Mia", "Noah", "Olivia", "Paul",
            "Quinn", "Rachel", "Sam", "Tina", "Uma", "Victor", "Wendy", "Xavier",
            "Yara", "Zack", "Amy", "Ben", "Cara", "Dan", "Emma", "Finn"
        )
        
        val lastNames = listOf(
            "Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller",
            "Davis", "Rodriguez", "Martinez", "Hernandez", "Lopez", "Gonzalez",
            "Wilson", "Anderson", "Thomas", "Taylor", "Moore", "Jackson", "Martin",
            "Lee", "Perez", "Thompson", "White", "Harris", "Sanchez", "Clark",
            "Ramirez", "Lewis", "Robinson", "Walker", "Young", "Allen", "King"
        )
        
        val firstName = firstNames[(index % firstNames.size).toInt()]
        val lastName = lastNames[(index % lastNames.size).toInt()]
        
        return "$firstName $lastName"
    }
    
    private fun generateRandomLocation(distribution: LocationDistribution): Pair<Double, Double> {
        return when (distribution) {
            LocationDistribution.WORLDWIDE -> {
                val lat = Random.nextDouble(-90.0, 90.0)
                val lon = Random.nextDouble(-180.0, 180.0)
                Pair(lat, lon)
            }
            LocationDistribution.URBAN_CENTERS -> {
                // Generate locations around major cities
                val cities = listOf(
                    Pair(40.7128, -74.0060), // NYC
                    Pair(34.0522, -118.2437), // LA
                    Pair(41.8781, -87.6298), // Chicago
                    Pair(29.7604, -95.3698), // Houston
                    Pair(33.4484, -112.0740), // Phoenix
                    Pair(39.7392, -104.9903), // Denver
                    Pair(47.6062, -122.3321), // Seattle
                    Pair(25.7617, -80.1918), // Miami
                    Pair(37.7749, -122.4194), // San Francisco
                    Pair(32.7767, -96.7970)  // Dallas
                )
                
                val city = cities[Random.nextInt(cities.size)]
                val lat = city.first + Random.nextDouble(-0.5, 0.5) // ±0.5 degrees (~55km)
                val lon = city.second + Random.nextDouble(-0.5, 0.5)
                
                Pair(lat.coerceIn(-90.0, 90.0), lon.coerceIn(-180.0, 180.0))
            }
            LocationDistribution.DENSE_CLUSTERS -> {
                // Create dense clusters in specific areas
                val clusters = listOf(
                    Pair(40.7128, -74.0060), // NYC area
                    Pair(34.0522, -118.2437), // LA area
                    Pair(51.5074, -0.1278), // London
                    Pair(48.8566, 2.3522), // Paris
                    Pair(35.6762, 139.6503) // Tokyo
                )
                
                val cluster = clusters[Random.nextInt(clusters.size)]
                val lat = cluster.first + Random.nextDouble(-0.1, 0.1) // ±0.1 degrees (~11km)
                val lon = cluster.second + Random.nextDouble(-0.1, 0.1)
                
                Pair(lat.coerceIn(-90.0, 90.0), lon.coerceIn(-180.0, 180.0))
            }
        }
    }
    
    private fun logProgress(entityType: String, total: Long) {
        val current = progressCounter.get()
        if (current % PROGRESS_LOG_INTERVAL == 0L || current == total) {
            val percentage = (current * 100 / total).toInt()
            logger.info("Seeding progress: $current/$total $entityType ($percentage%)")
        }
    }
    
    fun clearAllData() {
        logger.info("Clearing all data...")
        locationRepository.deleteAll()
        personRepository.deleteAll()
        logger.info("All data cleared")
    }
}

enum class LocationDistribution {
    WORLDWIDE,      // Random distribution across the globe
    URBAN_CENTERS,  // Concentrated around major cities
    DENSE_CLUSTERS  // Very dense clusters in specific areas
}

data class SeedingResult(
    val personCount: Long,
    val locationCount: Long,
    val totalDurationMs: Long,
    val averageRecordsPerSecond: Long
) 
package com.persons.finder.seeding

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(name = ["seeding.enabled"], havingValue = "true")
class SeedingCommandLineRunner(
    private val dataSeeder: DataSeeder
) : CommandLineRunner {
    
    private val logger = LoggerFactory.getLogger(SeedingCommandLineRunner::class.java)
    
    @Value("\${seeding.personCount:1000}")
    private lateinit var personCountStr: String
    
    @Value("\${seeding.distribution:WORLDWIDE}")
    private lateinit var distributionStr: String
    
    override fun run(vararg args: String) {
        logger.info("Starting seeding process...")
        
        val personCount = personCountStr.toLong()
        val distribution = try {
            LocationDistribution.valueOf(distributionStr)
        } catch (e: IllegalArgumentException) {
            logger.warn("Invalid distribution '$distributionStr', using WORLDWIDE")
            LocationDistribution.WORLDWIDE
        }
        
        logger.info("Seeding configuration: $personCount persons with $distribution distribution")
        
        try {
            val result = dataSeeder.seedCompleteDataset(personCount, distribution)
            logger.info("Seeding completed successfully!")
            logger.info("Result: ${result.personCount} persons, ${result.locationCount} locations")
            logger.info("Duration: ${result.totalDurationMs}ms")
            logger.info("Performance: ${result.averageRecordsPerSecond} records/sec")
        } catch (e: Exception) {
            logger.error("Seeding failed", e)
            throw e
        }
        
        // Exit after seeding
        System.exit(0)
    }
} 
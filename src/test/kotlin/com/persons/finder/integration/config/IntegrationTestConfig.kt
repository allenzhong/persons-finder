package com.persons.finder.integration.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.test.context.ActiveProfiles
import javax.sql.DataSource
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.boot.test.context.TestComponent

@TestConfiguration
@ActiveProfiles("integration")
class IntegrationTestConfig {

    @Bean
    @Primary
    fun testDataSource(): DataSource {
        return DataSourceBuilder.create()
            .url("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE")
            .driverClassName("org.h2.Driver")
            .username("sa")
            .password("")
            .build()
    }
} 
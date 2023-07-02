package com.mindbridgehealth.footing

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication(exclude = [SecurityAutoConfiguration::class])
@EnableJpaRepositories(basePackages =["com.mindbridgehealth.footing.*"])
@EntityScan("com.mindbridgehealth.footing.service.entity")
@ConfigurationPropertiesScan
class FootingApplication

fun main(args: Array<String>) {
    runApplication<com.mindbridgehealth.footing.FootingApplication>(*args)
}

package com.palaver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EnableJpaRepositories(basePackages =["com.palaver.*"])
@EntityScan("com.palaver.data.*")
class PalaverApplication

fun main(args: Array<String>) {
    runApplication<com.palaver.PalaverApplication>(*args)
}

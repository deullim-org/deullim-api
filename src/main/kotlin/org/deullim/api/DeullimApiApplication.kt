package org.deullim.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
class DeullimApiApplication

fun main(args: Array<String>) {
    runApplication<DeullimApiApplication>(*args)
}

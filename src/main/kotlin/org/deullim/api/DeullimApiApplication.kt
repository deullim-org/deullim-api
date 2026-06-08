package org.deullim.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DeullimApiApplication

fun main(args: Array<String>) {
    runApplication<DeullimApiApplication>(*args)
}

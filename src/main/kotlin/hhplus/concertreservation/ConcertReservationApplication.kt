package hhplus.concertreservation

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@EnableJpaAuditing
@ConfigurationPropertiesScan
@SpringBootApplication
class ConcertReservationApplication

fun main(args: Array<String>) {
    runApplication<ConcertReservationApplication>(*args)
}

package hhplus.concertreservation.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "reservation")
data class ReservationProperties (
    val expireMinutes: Long,
    val expireCheckRate: Int,
)

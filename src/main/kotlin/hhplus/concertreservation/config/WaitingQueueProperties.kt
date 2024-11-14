package hhplus.concertreservation.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "waiting-queue")
data class WaitingQueueProperties(
    val activateRate: String,
    val expireCheckRate: String,
    val activeUsers: Int,
    val expireMinutes: Long,
)

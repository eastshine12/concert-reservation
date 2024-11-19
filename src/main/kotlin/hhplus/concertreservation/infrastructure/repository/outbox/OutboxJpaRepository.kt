package hhplus.concertreservation.infrastructure.repository.outbox

import hhplus.concertreservation.domain.outbox.Outbox
import org.springframework.data.jpa.repository.JpaRepository

interface OutboxJpaRepository : JpaRepository<Outbox, Long> {
    fun findByTopicAndKey(
        topic: String,
        key: String,
    ): Outbox
}
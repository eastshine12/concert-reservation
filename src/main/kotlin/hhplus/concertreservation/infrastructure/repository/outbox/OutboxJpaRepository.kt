package hhplus.concertreservation.infrastructure.repository.outbox

import hhplus.concertreservation.domain.common.enums.OutboxStatus
import hhplus.concertreservation.domain.outbox.Outbox
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface OutboxJpaRepository : JpaRepository<Outbox, Long> {
    fun findByEventTypeAndKey(
        eventType: String,
        key: String,
    ): Outbox

    fun findByStatusAndCreatedAtAfter(
        status: OutboxStatus,
        createdAt: LocalDateTime,
    ): List<Outbox>
}

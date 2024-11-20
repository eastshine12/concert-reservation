package hhplus.concertreservation.domain.outbox

import hhplus.concertreservation.domain.common.enums.OutboxStatus
import java.time.LocalDateTime

interface OutboxRepository {
    fun save(outbox: Outbox): Outbox

    fun findByEventTypeAndKey(
        eventType: String,
        key: String,
    ): Outbox?

    fun findByStatusAndCreatedAtAfter(
        status: OutboxStatus,
        createdAt: LocalDateTime,
    ): List<Outbox>
}

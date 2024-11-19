package hhplus.concertreservation.domain.outbox

interface OutboxRepository {
    fun save(outbox: Outbox): Outbox

    fun findByEventTypeAndKey(
        eventType: String,
        key: String,
    ): Outbox?
}

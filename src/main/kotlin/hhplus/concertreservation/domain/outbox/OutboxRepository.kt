package hhplus.concertreservation.domain.outbox

interface OutboxRepository {
    fun save(outbox: Outbox): Outbox

    fun findByTopicAndKey(
        topic: String,
        key: String,
    ): Outbox?
}

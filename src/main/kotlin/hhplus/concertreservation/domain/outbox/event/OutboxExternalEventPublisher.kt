package hhplus.concertreservation.domain.outbox.event

interface OutboxExternalEventPublisher {
    fun publish(
        topic: String,
        key: String,
        payload: Any,
    )
}

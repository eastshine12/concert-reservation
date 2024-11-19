package hhplus.concertreservation.domain.common.event

interface EventPublisher {
    fun publish(
        topic: String,
        key: String,
        payload: Any,
    )
}

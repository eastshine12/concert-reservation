package hhplus.concertreservation.domain.concert.event

interface ReservationExternalEventPublisher {
    fun publish(
        topic: String,
        key: String,
        payload: Any,
    )
}

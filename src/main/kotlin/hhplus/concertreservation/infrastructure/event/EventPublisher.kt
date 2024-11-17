package hhplus.concertreservation.infrastructure.event

interface EventPublisher<T> {
    fun publish(event: T)
}

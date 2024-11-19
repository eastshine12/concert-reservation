package hhplus.concertreservation.domain.common.event

interface EventPublisher<T> {
    fun publish(event: T)
}

package hhplus.concertreservation.infrastructure.event

interface EventListener<T> {
    fun handle(event: T)
}

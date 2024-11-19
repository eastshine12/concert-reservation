package hhplus.concertreservation.domain.common.event

interface EventListener<T> {
    fun handle(event: T)
}

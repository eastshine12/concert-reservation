package hhplus.concertreservation.infrastructure.event.reservation

import hhplus.concertreservation.domain.concert.event.ReservationCreatedEvent
import hhplus.concertreservation.infrastructure.event.EventPublisher
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class ReservationSpringEventPublisher(
    private val applicationEventPublisher: ApplicationEventPublisher
) : EventPublisher<ReservationCreatedEvent> {

    override fun publish(event: ReservationCreatedEvent) {
        applicationEventPublisher.publishEvent(event)
    }
}

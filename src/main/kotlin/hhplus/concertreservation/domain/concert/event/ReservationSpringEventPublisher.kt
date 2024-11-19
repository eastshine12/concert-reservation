package hhplus.concertreservation.domain.concert.event

import hhplus.concertreservation.domain.common.event.EventPublisher
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

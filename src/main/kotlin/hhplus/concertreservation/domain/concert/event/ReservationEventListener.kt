package hhplus.concertreservation.domain.concert.event

import com.fasterxml.jackson.databind.ObjectMapper
import hhplus.concertreservation.domain.outbox.Outbox
import hhplus.concertreservation.domain.outbox.OutboxRepository
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class ReservationEventListener(
    private val outboxRepository: OutboxRepository,
    private val externalEventPublisher: ReservationExternalEventPublisher,
    private val objectMapper: ObjectMapper,
) {
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    fun saveOutbox(event: ReservationEvent.Created) {
        outboxRepository.save(
            Outbox(
                topic = "concert.reservation.created",
                key = event.reservationId.toString(),
                eventType = "RESERVATION_CREATED",
                payload = objectMapper.writeValueAsString(event),
            ),
        )
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun publishKafkaMessage(event: ReservationEvent.Created) {
        externalEventPublisher.publish(
            topic = "concert.reservation.created",
            key = event.reservationId.toString(),
            payload = event,
        )
    }
}

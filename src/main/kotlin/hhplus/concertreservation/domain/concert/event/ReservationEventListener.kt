package hhplus.concertreservation.domain.concert.event

import hhplus.concertreservation.domain.common.enums.OutboxStatus
import hhplus.concertreservation.domain.common.event.EventPublisher
import hhplus.concertreservation.domain.outbox.OutboxService
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class ReservationEventListener(
    private val outboxService: OutboxService,
    private val eventPublisher: EventPublisher,
) {
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    fun saveOutbox(event: ReservationCreatedEvent) {
        outboxService.save(
            topic = "reservation.created",
            key = event.reservationId.toString(),
            event = event,
        )
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun publishKafkaMessage(event: ReservationCreatedEvent) {
        try {
            eventPublisher.publish(
                topic = "reservation.created",
                key = event.reservationId.toString(),
                payload = event,
            )
        } catch (e: Exception) {
            outboxService.updateStatus(
                topic = "reservation.created",
                key = event.reservationId.toString(),
                status = OutboxStatus.FAILED,
            )
        }
    }
}

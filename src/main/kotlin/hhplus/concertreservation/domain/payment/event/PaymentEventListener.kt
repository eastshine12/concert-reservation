package hhplus.concertreservation.domain.payment.event

import com.fasterxml.jackson.databind.ObjectMapper
import hhplus.concertreservation.domain.outbox.Outbox
import hhplus.concertreservation.domain.outbox.OutboxRepository
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class PaymentEventListener(
    private val externalEventPublisher: PaymentExternalEventPublisher,
    private val outboxRepository: OutboxRepository,
    private val objectMapper: ObjectMapper,
) {
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    fun saveOutbox(event: PaymentEvent.Initiated) {
        outboxRepository.save(
            Outbox(
                topic = "payment.initiated",
                key = event.reservationId.toString(),
                eventType = "PAYMENT_INITIATED",
                payload = objectMapper.writeValueAsString(event),
            ),
        )
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun publishKafkaMessage(event: PaymentEvent.Initiated) {
        externalEventPublisher.publish(
            topic = "payment.initiated",
            key = event.reservationId.toString(),
            payload = event,
        )
    }
}

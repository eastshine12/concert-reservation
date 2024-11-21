package hhplus.concertreservation.interfaces.consumer.outbox

import hhplus.concertreservation.domain.common.enums.OutboxStatus
import hhplus.concertreservation.domain.outbox.OutboxRepository
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.stereotype.Component

@Component
class OutboxEventConsumer(
    private val outboxRepository: OutboxRepository,
) {
    @KafkaListener(topics = ["concert.reservation.created"], groupId = "outbox-consumer-group")
    fun consumeMessage(
        @Header(KafkaHeaders.RECEIVED_KEY) key: String,
        @Header(KafkaHeaders.RECEIVED_TOPIC) topic: String,
    ) {
        val eventType =
            when (topic) {
                "concert.reservation.created" -> "RESERVATION_CREATED"
                else -> return
            }
        val outboxEvent = outboxRepository.findByEventTypeAndKey(eventType, key)
        if (outboxEvent != null && outboxEvent.status == OutboxStatus.FAILED) {
            outboxEvent.confirmPublish()
            outboxRepository.save(outboxEvent)
        }
    }
}

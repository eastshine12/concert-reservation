package hhplus.concertreservation.interfaces.consumer.waitingQueue

import com.fasterxml.jackson.databind.ObjectMapper
import hhplus.concertreservation.domain.payment.event.PaymentEvent
import hhplus.concertreservation.domain.waitingQueue.WaitingQueueService
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component

@Component
class WaitingQueueEventConsumer(
    private val waitingQueueService: WaitingQueueService,
    private val objectMapper: ObjectMapper,
) {
    @KafkaListener(topics = ["payment.initiated"], groupId = "waitingQueue-consumer-group")
    fun consumeMessage(
        @Payload payload: String,
    ) {
        val event = objectMapper.readValue(payload, PaymentEvent.Initiated::class.java)
        waitingQueueService.expireToken(event.token)
    }
}

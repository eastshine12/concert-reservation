package hhplus.concertreservation.interfaces.consumer.payment

import com.fasterxml.jackson.databind.ObjectMapper
import hhplus.concertreservation.domain.payment.PaymentService
import hhplus.concertreservation.domain.payment.event.PaymentEvent
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component

@Component
class PaymentEventConsumer(
    private val paymentService: PaymentService,
    private val objectMapper: ObjectMapper,
) {
    @KafkaListener(topics = ["payment.initiated"], groupId = "payment-consumer-group")
    fun consumeMessage(
        @Payload payload: String,
    ) {
        val event = objectMapper.readValue(payload, PaymentEvent.Initiated::class.java)
        paymentService.savePaymentHistory(event.userId, event.reservationId, event.price)
    }
}

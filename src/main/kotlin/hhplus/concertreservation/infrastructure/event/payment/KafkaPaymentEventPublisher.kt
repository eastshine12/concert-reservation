package hhplus.concertreservation.infrastructure.event.payment

import com.fasterxml.jackson.databind.ObjectMapper
import hhplus.concertreservation.domain.payment.event.PaymentExternalEventPublisher
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class KafkaPaymentEventPublisher(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper,
) : PaymentExternalEventPublisher {
    override fun publish(
        topic: String,
        key: String,
        payload: Any,
    ) {
        val payloadString = objectMapper.writeValueAsString(payload)
        kafkaTemplate.send(topic, key, payloadString)
    }
}

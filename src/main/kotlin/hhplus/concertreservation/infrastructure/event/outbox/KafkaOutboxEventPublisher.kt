package hhplus.concertreservation.infrastructure.event.outbox

import com.fasterxml.jackson.databind.ObjectMapper
import hhplus.concertreservation.domain.outbox.event.OutboxExternalEventPublisher
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class KafkaOutboxEventPublisher(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper,
) : OutboxExternalEventPublisher {
    override fun publish(
        topic: String,
        key: String,
        payload: Any,
    ) {
        val payloadString = objectMapper.writeValueAsString(payload)
        kafkaTemplate.send(topic, key, payloadString)
    }
}

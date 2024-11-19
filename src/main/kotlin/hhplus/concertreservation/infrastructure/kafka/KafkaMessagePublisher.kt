package hhplus.concertreservation.infrastructure.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import hhplus.concertreservation.domain.common.event.EventPublisher
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class KafkaMessagePublisher(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper,
) : EventPublisher {
    override fun publish(
        topic: String,
        key: String,
        payload: Any,
    ) {
        val payloadString = objectMapper.writeValueAsString(payload)
        kafkaTemplate.send(topic, key, payloadString)
    }
}

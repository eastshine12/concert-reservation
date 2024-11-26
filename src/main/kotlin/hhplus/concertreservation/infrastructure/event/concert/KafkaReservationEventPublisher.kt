package hhplus.concertreservation.infrastructure.event.concert

import com.fasterxml.jackson.databind.ObjectMapper
import hhplus.concertreservation.domain.concert.event.ReservationExternalEventPublisher
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class KafkaReservationEventPublisher(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper,
) : ReservationExternalEventPublisher {
    override fun publish(
        topic: String,
        key: String,
        payload: Any,
    ) {
        val payloadString = objectMapper.writeValueAsString(payload)
        kafkaTemplate.send(topic, key, payloadString)
    }
}

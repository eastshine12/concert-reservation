package hhplus.concertreservation.interfaces.consumer.concert

import com.fasterxml.jackson.databind.ObjectMapper
import hhplus.concertreservation.domain.concert.event.ReservationEvent
import hhplus.concertreservation.domain.concert.service.ReservationService
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component

@Component
class ReservationEventConsumer(
    private val reservationService: ReservationService,
    private val objectMapper: ObjectMapper,
) {
    @KafkaListener(topics = ["concert.reservation.created"], groupId = "seat-consumer-group")
    fun consumeMessage(
        @Payload payload: String,
    ) {
        val event = objectMapper.readValue(payload, ReservationEvent.Created::class.java)
        reservationService.occupySeat(event.scheduleId)
    }
}

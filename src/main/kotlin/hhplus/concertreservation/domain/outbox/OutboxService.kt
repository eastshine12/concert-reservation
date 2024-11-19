package hhplus.concertreservation.domain.outbox

import com.fasterxml.jackson.databind.ObjectMapper
import hhplus.concertreservation.domain.common.enums.OutboxStatus
import hhplus.concertreservation.domain.common.error.ErrorType
import hhplus.concertreservation.domain.common.exception.CoreException
import org.springframework.stereotype.Service

@Service
class OutboxService(
    private val outboxRepository: OutboxRepository,
    private val objectMapper: ObjectMapper,
) {
    fun save(
        topic: String,
        key: String,
        event: Any,
    ) {
        outboxRepository.save(
            Outbox(
                topic = topic,
                key = key,
                payload = objectMapper.writeValueAsString(event),
            ),
        )
    }

    fun updateStatus(
        topic: String,
        key: String,
        status: OutboxStatus,
    ) {
        val outbox: Outbox =
            outboxRepository.findByTopicAndKey(topic, key)
                ?: throw CoreException(ErrorType.NO_OUTBOX_FOUND, "Outbox event not found with topic: $topic and key: $key")
        outbox.updateStatus(status)
        outboxRepository.save(outbox)
    }
}

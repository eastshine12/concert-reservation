package hhplus.concertreservation.domain.outbox

import hhplus.concertreservation.domain.common.enums.OutboxStatus
import hhplus.concertreservation.domain.outbox.event.OutboxExternalEventPublisher
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class OutboxScheduler(
    private val outboxRepository: OutboxRepository,
    private val externalEventPublisher: OutboxExternalEventPublisher,
    @Value("\${outbox.scheduler.timeLimitMinutes}") private val timeLimitMinutes: Long,
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @Scheduled(fixedRateString = "\${outbox.scheduler.fixedRate}")
    fun reprocessFailedEvents() {
        val timeLimit = LocalDateTime.now().minusMinutes(timeLimitMinutes)

        val failedEvents: List<Outbox> = outboxRepository.findByStatusAndCreatedAtAfter(
            status = OutboxStatus.FAILED,
            createdAt = timeLimit,
        )

        for (event in failedEvents) {
            try {
                externalEventPublisher.publish(
                    topic = event.topic,
                    key = event.key,
                    payload = event.payload,
                )
            } catch (e: Exception) {
                log.error("Failed to republish event with ID: ${event.id}, error: ${e.message}")
            }
        }
    }
}

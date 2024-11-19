package hhplus.concertreservation.infrastructure.repository.outbox

import hhplus.concertreservation.domain.outbox.Outbox
import hhplus.concertreservation.domain.outbox.OutboxRepository
import org.springframework.stereotype.Repository

@Repository
class OutboxRepositoryJpaImpl(
    private val outboxJpaRepository: OutboxJpaRepository,
) : OutboxRepository {
    override fun save(outbox: Outbox): Outbox {
        return outboxJpaRepository.save(outbox)
    }

    override fun findByEventTypeAndKey(
        eventType: String,
        key: String,
    ): Outbox? {
        return outboxJpaRepository.findByEventTypeAndKey(eventType, key)
    }
}

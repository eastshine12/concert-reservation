package hhplus.concertreservation.infrastructure.repository.waitingQueue

import hhplus.concertreservation.domain.common.enums.QueueStatus
import hhplus.concertreservation.domain.waitingQueue.WaitingQueue
import org.springframework.data.jpa.repository.JpaRepository

interface WaitingQueueJpaRepository : JpaRepository<WaitingQueue, Long> {
    fun findByToken(token: String): WaitingQueue?

    fun findAllByScheduleId(scheduleId: Long): List<WaitingQueue>

    fun findByStatus(status: QueueStatus): List<WaitingQueue>
}

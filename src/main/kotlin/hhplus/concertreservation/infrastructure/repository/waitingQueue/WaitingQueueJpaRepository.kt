package hhplus.concertreservation.infrastructure.repository.waitingQueue

import hhplus.concertreservation.domain.common.enums.QueueStatus
import hhplus.concertreservation.domain.waitingQueue.WaitingQueue
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface WaitingQueueJpaRepository : JpaRepository<WaitingQueue, Long> {
    fun findByToken(token: String): WaitingQueue?

    fun findAllByScheduleId(scheduleId: Long): List<WaitingQueue>

    @Query("SELECT COALESCE(MAX(w.queuePosition), 0) FROM WaitingQueue w WHERE w.scheduleId = :scheduleId")
    fun findMaxQueuePositionByScheduleId(scheduleId: Long): Int

    fun findByStatus(status: QueueStatus): List<WaitingQueue>
}

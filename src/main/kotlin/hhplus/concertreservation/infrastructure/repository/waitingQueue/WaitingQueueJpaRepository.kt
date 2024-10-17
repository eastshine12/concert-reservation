package hhplus.concertreservation.infrastructure.repository.waitingQueue

import hhplus.concertreservation.domain.common.enums.QueueStatus
import hhplus.concertreservation.domain.waitingQueue.WaitingQueue
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface WaitingQueueJpaRepository : JpaRepository<WaitingQueue, Long> {
    fun findByToken(token: String): WaitingQueue?
    @Query("SELECT MIN(w.queuePosition) FROM WaitingQueue w WHERE w.scheduleId = :scheduleId")
    fun findMinQueuePositionByScheduleId(scheduleId: Long): Int
    @Query("SELECT MAX(w.queuePosition) FROM WaitingQueue w WHERE w.scheduleId = :scheduleId")
    fun findMaxQueuePositionByScheduleId(scheduleId: Long): Int
    fun findByStatus(status: QueueStatus): List<WaitingQueue>
}

package hhplus.concertreservation.domain.waitingQueue

import hhplus.concertreservation.domain.common.enums.QueueStatus

interface WaitingQueueRepository {
    fun save(waitingQueue: WaitingQueue): WaitingQueue

    fun findByToken(token: String): WaitingQueue?

    fun findMinQueuePositionByScheduleId(scheduleId: Long): Int

    fun findMaxQueuePositionByScheduleId(scheduleId: Long): Int

    fun findByStatus(status: QueueStatus): List<WaitingQueue>

    fun saveAll(queues: List<WaitingQueue>): MutableList<WaitingQueue>
}

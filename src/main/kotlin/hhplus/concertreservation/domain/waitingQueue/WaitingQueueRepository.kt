package hhplus.concertreservation.domain.waitingQueue

import hhplus.concertreservation.domain.common.enums.QueueStatus

interface WaitingQueueRepository {
    fun save(waitingQueue: WaitingQueue): WaitingQueue

    fun findByToken(token: String): WaitingQueue?

    fun findAllByScheduleId(scheduleId: Long): List<WaitingQueue>

    fun findByStatus(status: QueueStatus): List<WaitingQueue>

    fun saveAll(queues: List<WaitingQueue>): MutableList<WaitingQueue>
}

package hhplus.concertreservation.domain.waitingQueue

import hhplus.concertreservation.domain.common.enums.QueueStatus

interface WaitingQueueRepository {
    fun save(waitingQueue: WaitingQueue): WaitingQueue

    fun findWaitingQueue(
        token: String,
        scheduleId: Long,
    ): WaitingQueue?

    fun findScheduleIdByToken(token: String): Long?

    fun findAllByScheduleId(scheduleId: Long): List<WaitingQueue>

    fun findByStatus(status: QueueStatus): List<WaitingQueue>

    fun saveAll(queues: List<WaitingQueue>): MutableList<WaitingQueue>

    fun delete(waitingQueue: WaitingQueue)

    fun getAllWaitingTokenKeys(): MutableSet<String>

    fun getAllActiveTokenKeys(): MutableSet<String>

    fun getTopWaitingTokens(
        scheduleId: Long,
        maxTokens: Int,
    ): Set<Any>

    fun removeWaitingTokens(
        scheduleId: Long,
        tokens: Set<Any>,
    )

    fun removeExpiredTokens(scheduleId: Long)

    fun addActiveTokens(
        scheduleId: Long,
        tokens: Set<Any>,
        expiresInMinutes: Long,
    )
}

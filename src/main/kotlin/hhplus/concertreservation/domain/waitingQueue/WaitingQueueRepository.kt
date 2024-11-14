package hhplus.concertreservation.domain.waitingQueue

import hhplus.concertreservation.domain.common.enums.QueueStatus

interface WaitingQueueRepository {
    fun addWaitingQueue(waitingQueue: WaitingQueue): WaitingQueue

    fun moveToActiveQueue(
        scheduleId: Long,
        tokens: Set<Any>,
        expiresInMinutes: Long,
    )

    fun findByToken(token: String): WaitingQueue?

    fun getAllTokenKeysByStatus(status: QueueStatus): MutableSet<String>

    fun getTokensFromTopToRange(
        scheduleId: Long,
        maxTokens: Int,
    ): Set<Any>

    fun getTokenRank(waitingQueue: WaitingQueue): Long?

    fun getTokenScore(waitingQueue: WaitingQueue): Double?

    fun remove(waitingQueue: WaitingQueue)

    fun removeExpiredTokens(scheduleId: Long)
}

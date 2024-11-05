package hhplus.concertreservation.domain.waitingQueue.component

import hhplus.concertreservation.config.WaitingQueueProperties
import hhplus.concertreservation.domain.common.enums.QueueStatus
import hhplus.concertreservation.domain.common.error.ErrorType
import hhplus.concertreservation.domain.common.exception.CoreException
import hhplus.concertreservation.domain.waitingQueue.WaitingQueue
import hhplus.concertreservation.domain.waitingQueue.WaitingQueueRepository
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class QueueManager(
    private val waitingQueueRepository: WaitingQueueRepository,
    private val waitingQueueProperties: WaitingQueueProperties,
) {
    fun enqueue(
        scheduleId: Long,
        token: String,
    ): WaitingQueue {
        val waitingQueue =
            waitingQueueRepository.save(
                WaitingQueue(
                    scheduleId = scheduleId,
                    token = token,
                    status = QueueStatus.PENDING,
                    expiresAt = null,
                ),
            )
        return waitingQueue
    }

    fun findQueueByToken(token: String): WaitingQueue? {
        val scheduleId: Long =
            waitingQueueRepository.findScheduleIdByToken(token)
                ?: throw CoreException(ErrorType.NO_QUEUE_FOUND)
        return waitingQueueRepository.findWaitingQueue(token, scheduleId)
    }

    fun validateTokenState(queue: WaitingQueue) {
        if (queue.status == QueueStatus.EXPIRED || queue.expiresAt?.isBefore(LocalDateTime.now()) == true) {
            throw CoreException(
                errorType = ErrorType.TOKEN_EXPIRED,
                details =
                    mapOf(
                        "token" to queue.token,
                    ),
            )
        }

        if (queue.status != QueueStatus.ACTIVE) {
            throw CoreException(
                errorType = ErrorType.INVALID_TOKEN,
            )
        }
    }

    fun countActiveQueuesByScheduleId(activeQueues: List<WaitingQueue>): MutableMap<Long, Int> {
        val activeCountMap = mutableMapOf<Long, Int>()
        activeQueues.forEach { queue ->
            activeCountMap[queue.scheduleId] = activeCountMap.getOrDefault(queue.scheduleId, 0) + 1
        }
        return activeCountMap
    }

    fun activatePendingQueues() {
        val allWaitingKeys = waitingQueueRepository.getAllWaitingTokenKeys()

        allWaitingKeys.forEach { waitingKey ->
            val scheduleId = waitingKey.split(":")[1].toLong()
            val tokensToActivate = waitingQueueRepository.getTopWaitingTokens(scheduleId, waitingQueueProperties.activeUsers)
            if (tokensToActivate.isNotEmpty()) {
                waitingQueueRepository.addActiveTokens(scheduleId, tokensToActivate, waitingQueueProperties.expireMinutes)
                waitingQueueRepository.removeWaitingTokens(scheduleId, tokensToActivate)
            }
        }
    }

    fun expireActiveQueues() {
        val allActiveKeys = waitingQueueRepository.getAllActiveTokenKeys()
        allActiveKeys.forEach { activeKey ->
            val scheduleId = activeKey.split(":")[1].toLong()
            waitingQueueRepository.removeExpiredTokens(scheduleId)
        }
    }
}

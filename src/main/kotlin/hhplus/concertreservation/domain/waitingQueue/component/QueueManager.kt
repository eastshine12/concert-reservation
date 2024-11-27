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
            waitingQueueRepository.addWaitingQueue(
                WaitingQueue(
                    scheduleId = scheduleId,
                    token = token,
                    status = QueueStatus.WAITING,
                    expiresAt = null,
                ),
            )
        // 부하 테스트를 위한 임시 코드 (토큰 활성화)
        waitingQueueRepository.moveToActiveQueue(
            scheduleId = scheduleId,
            tokens = setOf(token),
            expiresInMinutes = 10L,
        )
        return waitingQueue
    }

    fun findQueueByToken(token: String): WaitingQueue? {
        val waitingQueue: WaitingQueue =
            waitingQueueRepository.findByToken(token)
                ?: throw CoreException(ErrorType.NO_QUEUE_FOUND)
        if (waitingQueue.status == QueueStatus.WAITING) {
            waitingQueue.position = waitingQueueRepository.getTokenRank(waitingQueue)?.toInt() ?: 0
        }
        return waitingQueue
    }

    fun validateTokenState(queue: WaitingQueue) {
        if (queue.expiresAt?.isBefore(LocalDateTime.now()) == true) {
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

    fun activatePendingQueues() {
        val allWaitingKeys = waitingQueueRepository.getAllTokenKeysByStatus(QueueStatus.WAITING)

        allWaitingKeys.forEach { waitingKey ->
            val scheduleId = waitingKey.split(":")[1].toLong()
            val tokensToActivate = waitingQueueRepository.getTokensFromTopToRange(scheduleId, waitingQueueProperties.activeUsers)
            if (tokensToActivate.isNotEmpty()) {
                waitingQueueRepository.moveToActiveQueue(scheduleId, tokensToActivate, waitingQueueProperties.expireMinutes)
            }
        }
    }

    fun expireActiveQueues() {
        val allActiveKeys = waitingQueueRepository.getAllTokenKeysByStatus(QueueStatus.ACTIVE)
        allActiveKeys.forEach { activeKey ->
            val scheduleId = activeKey.split(":")[1].toLong()
            waitingQueueRepository.removeExpiredTokens(scheduleId)
        }
    }
}

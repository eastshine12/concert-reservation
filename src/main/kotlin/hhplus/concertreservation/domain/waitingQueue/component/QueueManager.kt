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
        return waitingQueueRepository.findByToken(token)
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

    fun activatePendingQueues(
        pendingQueues: List<WaitingQueue>,
        activeCountMap: MutableMap<Long, Int>,
    ) {
        pendingQueues.groupBy { it.scheduleId }.forEach { (scheduleId, pendingList) ->
            val activeCount = activeCountMap.getOrDefault(scheduleId, 0)
            if (activeCount < waitingQueueProperties.maxActiveUsers) {
                val availableSlots = waitingQueueProperties.maxActiveUsers - activeCount
                val queuesToActivate = pendingList.sortedBy { it.id }.take(availableSlots)
                queuesToActivate.forEach { queue ->
                    queue.activate(waitingQueueProperties.expireMinutes)
                }
                waitingQueueRepository.saveAll(queuesToActivate)
            }
        }
    }

    fun expireActiveQueues(expiredQueues: List<WaitingQueue>) {
        expiredQueues.forEach { it.expire() }
        waitingQueueRepository.saveAll(expiredQueues)
    }
}

package hhplus.concertreservation.domain.waitingQueue.component

import hhplus.concertreservation.config.WaitingQueueProperties
import hhplus.concertreservation.domain.common.enums.QueueStatus
import hhplus.concertreservation.domain.concert.entity.ConcertSchedule
import hhplus.concertreservation.domain.waitingQueue.WaitingQueue
import hhplus.concertreservation.domain.waitingQueue.WaitingQueueRepository
import hhplus.concertreservation.domain.waitingQueue.exception.InvalidTokenException
import hhplus.concertreservation.domain.waitingQueue.exception.TokenExpiredException
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class QueueManager(
    private val waitingQueueRepository: WaitingQueueRepository,
    private val waitingQueueProperties: WaitingQueueProperties,
) {
    fun enqueue(concertSchedule: ConcertSchedule, token: String, position: Int): WaitingQueue {
        return waitingQueueRepository.save(
            WaitingQueue(
                scheduleId = concertSchedule.id,
                token = token,
                status = QueueStatus.PENDING,
                queuePosition = position,
                expiresAt = null
            )
        )
    }

    fun findQueueByToken(token: String): WaitingQueue? {
        return waitingQueueRepository.findByToken(token)
    }

    fun calculateQueuePosition(scheduleId: Long): Int {
        val lastPosition = waitingQueueRepository.findMaxQueuePositionByScheduleId(scheduleId)
        return lastPosition + 1
    }

    fun validateTokenState(queue: WaitingQueue) {
        if (queue.status != QueueStatus.ACTIVE) {
            throw InvalidTokenException("Token is not active: ${queue.token}")
        }

        if (queue.expiresAt!!.isBefore(LocalDateTime.now())) {
            throw TokenExpiredException("Token has expired: ${queue.token}")
        }
    }

    fun countActiveQueuesByScheduleId(activeQueues: List<WaitingQueue>): MutableMap<Long, Int> {
        val activeCountMap = mutableMapOf<Long, Int>()
        activeQueues.forEach { queue ->
            activeCountMap[queue.scheduleId] = activeCountMap.getOrDefault(queue.scheduleId, 0) + 1
        }
        return activeCountMap
    }

    fun activatePendingQueues(pendingQueues: List<WaitingQueue>, activeCountMap: MutableMap<Long, Int>) {
        pendingQueues.groupBy { it.scheduleId }.forEach { (scheduleId, pendingList) ->
            val activeCount = activeCountMap.getOrDefault(scheduleId, 0)
            if (activeCount < waitingQueueProperties.maxActiveUsers) {
                val availableSlots = waitingQueueProperties.maxActiveUsers - activeCount
                val queuesToActivate = pendingList.sortedBy { it.queuePosition }.take(availableSlots)
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

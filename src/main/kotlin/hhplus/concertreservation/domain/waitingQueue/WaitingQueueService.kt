package hhplus.concertreservation.domain.waitingQueue

import hhplus.concertreservation.domain.concert.entity.ConcertSchedule
import hhplus.concertreservation.domain.waitingQueue.component.QueueManager
import hhplus.concertreservation.domain.waitingQueue.component.TokenManager
import hhplus.concertreservation.domain.waitingQueue.exception.InvalidTokenException
import hhplus.concertreservation.domain.waitingQueue.exception.QueueNotFoundException
import org.springframework.stereotype.Service

@Service
class WaitingQueueService(
    private val tokenManager: TokenManager,
    private val queueManager: QueueManager,
    private val waitingQueueRepository: WaitingQueueRepository,
) {
    fun getOrGenerateToken(token: String?, schedule: ConcertSchedule) : WaitingQueue {
        return token?.let {
            val validToken = tokenManager.validateAndGetToken(it)
            queueManager.findQueueByToken(validToken)?.takeIf { queue -> queue.scheduleId == schedule.id }
        } ?: queueManager.enqueue(
            concertSchedule = schedule,
            token = tokenManager.generateToken(),
            position = queueManager.calculateQueuePosition(schedule.id)
        )
    }

    fun validateAndGetToken(token: String): WaitingQueue {
        val validToken = tokenManager.validateAndGetToken(token)
        return queueManager.findQueueByToken(validToken)
            ?: throw QueueNotFoundException("No waiting-queue found for the token: $token")
    }

    fun validateTokenState(token: String): WaitingQueue {
        return validateTokenState(token, null)
    }

    fun validateTokenState(token: String, scheduleId: Long?): WaitingQueue {
        val queue = validateAndGetToken(token)
        queueManager.validateTokenState(queue)
        if (scheduleId != null && queue.scheduleId != scheduleId) {
            throw InvalidTokenException("Token does not belong to the concert schedule: $scheduleId")
        }
        return queue
    }

    fun calculateRemainingPosition(scheduleId: Long, myPosition: Int): Int {
        val lastPosition = waitingQueueRepository.findMinQueuePositionByScheduleId(scheduleId)
        return myPosition - lastPosition
    }

    fun expireToken(token: String) {
        val waitingQueue: WaitingQueue = validateAndGetToken(token)
        waitingQueue.expire()
        waitingQueueRepository.save(waitingQueue)
    }
}

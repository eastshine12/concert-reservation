package hhplus.concertreservation.domain.waitingQueue

import hhplus.concertreservation.domain.common.enums.QueueStatus
import hhplus.concertreservation.domain.common.error.ErrorType
import hhplus.concertreservation.domain.common.exception.CoreException
import hhplus.concertreservation.domain.concert.entity.ConcertSchedule
import hhplus.concertreservation.domain.waitingQueue.component.QueueManager
import hhplus.concertreservation.domain.waitingQueue.component.TokenManager
import org.springframework.stereotype.Service

@Service
class WaitingQueueService(
    private val tokenManager: TokenManager,
    private val queueManager: QueueManager,
    private val waitingQueueRepository: WaitingQueueRepository,
) {
    fun issueToken(
        token: String?,
        schedule: ConcertSchedule,
    ): WaitingQueue {
        token?.let {
            val validToken = tokenManager.validateAndGetToken(it)
            queueManager.findQueueByToken(validToken)
                ?.takeIf { queue -> queue.scheduleId == schedule.id && queue.status != QueueStatus.EXPIRED }
                ?.run { throw CoreException(errorType = ErrorType.QUEUE_ALREADY_EXISTS,) }
        }

        return queueManager.enqueue(
            concertSchedule = schedule,
            token = tokenManager.generateToken(),
            position = queueManager.calculateQueuePosition(schedule.id),
        )
    }

    fun validateAndGetToken(token: String): WaitingQueue {
        val validToken = tokenManager.validateAndGetToken(token)
        return queueManager.findQueueByToken(validToken)
            ?: throw CoreException(
                errorType = ErrorType.NO_QUEUE_FOUND,
                details = mapOf(
                    "token" to token,
                ),
            )
    }

    fun validateTokenState(token: String): WaitingQueue {
        return validateTokenState(token, null)
    }

    fun validateTokenState(
        token: String,
        scheduleId: Long?,
    ): WaitingQueue {
        val queue = validateAndGetToken(token)
        queueManager.validateTokenState(queue)
        if (scheduleId != null && queue.scheduleId != scheduleId) {
            throw CoreException(
                errorType = ErrorType.INVALID_TOKEN,
                message = "Token does not belong to the concert schedule.",
                details = mapOf(
                    "scheduleId" to scheduleId,
                ),
            )
        }
        return queue
    }

    fun calculateRemainingPosition(
        scheduleId: Long,
        myPosition: Int,
    ): Int {
        val lastPosition = waitingQueueRepository.findMinQueuePositionByScheduleId(scheduleId)
        return myPosition - lastPosition
    }

    fun expireToken(token: String) {
        val waitingQueue: WaitingQueue = validateAndGetToken(token)
        waitingQueue.expire()
        waitingQueueRepository.save(waitingQueue)
    }
}

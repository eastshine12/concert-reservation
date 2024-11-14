package hhplus.concertreservation.domain.waitingQueue

import hhplus.concertreservation.domain.common.error.ErrorType
import hhplus.concertreservation.domain.common.exception.CoreException
import hhplus.concertreservation.domain.waitingQueue.component.QueueManager
import hhplus.concertreservation.domain.waitingQueue.component.TokenManager
import hhplus.concertreservation.domain.waitingQueue.dto.info.TokenInfo
import org.springframework.stereotype.Service

@Service
class WaitingQueueService(
    private val tokenManager: TokenManager,
    private val queueManager: QueueManager,
    private val waitingQueueRepository: WaitingQueueRepository,
) {
    fun issueToken(
        token: String?,
        scheduleId: Long,
    ): TokenInfo {
        token?.let {
            val validToken = tokenManager.validateAndGetToken(it)
            queueManager.findQueueByToken(validToken)
                ?.takeIf { queue -> queue.scheduleId == scheduleId }
                ?.run { throw CoreException(errorType = ErrorType.QUEUE_ALREADY_EXISTS) }
        }

        return queueManager.enqueue(
            scheduleId = scheduleId,
            token = tokenManager.generateToken(),
        ).toTokenInfo()
    }

    fun validateAndGetToken(token: String): WaitingQueue {
        val validToken: String = tokenManager.validateAndGetToken(token)
        return queueManager.findQueueByToken(validToken)
            ?: throw CoreException(
                errorType = ErrorType.NO_QUEUE_FOUND,
                details =
                    mapOf(
                        "token" to token,
                    ),
            )
    }

    fun verifyMatchingScheduleId(
        token: String,
        scheduleId: Long,
    ) {
        val queue = validateAndGetToken(token)
        if (queue.scheduleId != scheduleId) {
            throw CoreException(
                errorType = ErrorType.INVALID_TOKEN,
                message = "Token does not belong to the concert schedule.",
                details =
                    mapOf(
                        "scheduleId" to scheduleId,
                    ),
            )
        }
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
                details =
                    mapOf(
                        "scheduleId" to scheduleId,
                    ),
            )
        }
        return queue
    }

    fun expireToken(token: String) {
        val waitingQueue: WaitingQueue = validateAndGetToken(token)
        waitingQueueRepository.remove(waitingQueue)
    }
}

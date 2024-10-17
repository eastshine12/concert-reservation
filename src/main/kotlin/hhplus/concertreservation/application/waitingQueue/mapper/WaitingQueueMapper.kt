package hhplus.concertreservation.application.waitingQueue.mapper

import hhplus.concertreservation.application.waitingQueue.dto.info.TokenInfo
import hhplus.concertreservation.application.waitingQueue.dto.info.WaitingQueueInfo
import hhplus.concertreservation.domain.waitingQueue.WaitingQueue
import org.springframework.stereotype.Component

@Component
class WaitingQueueMapper {

    fun toTokenInfo(waitingQueue: WaitingQueue): TokenInfo {
        return TokenInfo(
            queueId = waitingQueue.id,
            scheduleId = waitingQueue.scheduleId,
            token = waitingQueue.token,
            status = waitingQueue.status,
            queuePosition = waitingQueue.queuePosition,
            expiresAt = waitingQueue.expiresAt
        )
    }

    fun toWaitingQueueInfo(waitingQueue: WaitingQueue, remainingPosition: Int): WaitingQueueInfo {
        return WaitingQueueInfo(
            scheduleId = waitingQueue.scheduleId,
            status = waitingQueue.status,
            remainingPosition = remainingPosition,
            expiresAt = waitingQueue.expiresAt,
        )
    }
}

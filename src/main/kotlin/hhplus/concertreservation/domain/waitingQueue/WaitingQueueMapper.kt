package hhplus.concertreservation.domain.waitingQueue

import hhplus.concertreservation.application.waitingQueue.dto.info.TokenInfo
import hhplus.concertreservation.application.waitingQueue.dto.info.WaitingQueueInfo

fun WaitingQueue.toTokenInfo(): TokenInfo {
    return TokenInfo(
        queueId = this.id,
        scheduleId = this.scheduleId,
        token = this.token,
        status = this.status,
        queuePosition = this.queuePosition,
        expiresAt = this.expiresAt
    )
}

fun WaitingQueue.toWaitingQueueInfo(remainingPosition: Int): WaitingQueueInfo {
    return WaitingQueueInfo(
        scheduleId = this.scheduleId,
        status = this.status,
        remainingPosition = remainingPosition,
        expiresAt = this.expiresAt,
    )
}
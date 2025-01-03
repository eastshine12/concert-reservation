package hhplus.concertreservation.domain.waitingQueue

import hhplus.concertreservation.domain.waitingQueue.dto.info.TokenInfo
import hhplus.concertreservation.domain.waitingQueue.dto.info.WaitingQueueInfo

fun WaitingQueue.toTokenInfo(): TokenInfo {
    return TokenInfo(
        scheduleId = this.scheduleId,
        token = this.token,
        status = this.status,
        expiresAt = this.expiresAt,
    )
}

fun WaitingQueue.toWaitingQueueInfo(): WaitingQueueInfo {
    return WaitingQueueInfo(
        scheduleId = this.scheduleId,
        status = this.status,
        remainingPosition = this.position,
        expiresAt = this.expiresAt,
    )
}

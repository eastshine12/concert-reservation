package hhplus.concertreservation.application.waitingQueue

import hhplus.concertreservation.domain.concert.service.ConcertService
import hhplus.concertreservation.domain.waitingQueue.WaitingQueue
import hhplus.concertreservation.domain.waitingQueue.WaitingQueueService
import hhplus.concertreservation.domain.waitingQueue.dto.command.TokenCommand
import hhplus.concertreservation.domain.waitingQueue.dto.info.TokenInfo
import hhplus.concertreservation.domain.waitingQueue.dto.info.WaitingQueueInfo
import hhplus.concertreservation.domain.waitingQueue.toWaitingQueueInfo
import org.springframework.stereotype.Component

@Component
class WaitingQueueFacade(
    private val waitingQueueService: WaitingQueueService,
    private val concertService: ConcertService,
) {
    fun issueWaitingQueueToken(tokenCommand: TokenCommand): TokenInfo {
        concertService.checkScheduleAvailability(tokenCommand.concertScheduleId)
        return waitingQueueService.issueToken(tokenCommand.token, tokenCommand.concertScheduleId)
    }

    fun getWaitingQueueStatus(token: String): WaitingQueueInfo {
        val waitingQueue: WaitingQueue = waitingQueueService.validateAndGetToken(token)
        val remainingPosition: Int = waitingQueueService.calculateRemainingPosition(waitingQueue.scheduleId, token)
        return waitingQueue.toWaitingQueueInfo(remainingPosition)
    }
}

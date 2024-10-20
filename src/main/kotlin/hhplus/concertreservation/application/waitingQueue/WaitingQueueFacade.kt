package hhplus.concertreservation.application.waitingQueue

import hhplus.concertreservation.domain.concert.entity.ConcertSchedule
import hhplus.concertreservation.domain.concert.service.ConcertService
import hhplus.concertreservation.domain.waitingQueue.WaitingQueue
import hhplus.concertreservation.domain.waitingQueue.WaitingQueueService
import hhplus.concertreservation.domain.waitingQueue.dto.command.TokenCommand
import hhplus.concertreservation.domain.waitingQueue.dto.info.TokenInfo
import hhplus.concertreservation.domain.waitingQueue.dto.info.WaitingQueueInfo
import hhplus.concertreservation.domain.waitingQueue.toTokenInfo
import hhplus.concertreservation.domain.waitingQueue.toWaitingQueueInfo
import org.springframework.stereotype.Component

@Component
class WaitingQueueFacade(
    private val waitingQueueService: WaitingQueueService,
    private val concertService: ConcertService,
) {
    fun issueWaitingQueueToken(tokenCommand: TokenCommand): TokenInfo {
        val concertSchedule: ConcertSchedule = concertService.getScheduleById(tokenCommand.concertScheduleId)
        val waitingQueue: WaitingQueue = waitingQueueService.issueToken(tokenCommand.token, concertSchedule)
        return waitingQueue.toTokenInfo()
    }

    fun getWaitingQueueStatus(token: String): WaitingQueueInfo {
        val waitingQueue: WaitingQueue = waitingQueueService.validateAndGetToken(token)
        val remainingPosition: Int = waitingQueueService.calculateRemainingPosition(waitingQueue.scheduleId, waitingQueue.queuePosition)
        return waitingQueue.toWaitingQueueInfo(remainingPosition)
    }
}

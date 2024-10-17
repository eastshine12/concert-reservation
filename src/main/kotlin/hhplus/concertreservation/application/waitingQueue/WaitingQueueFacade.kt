package hhplus.concertreservation.application.waitingQueue

import hhplus.concertreservation.application.waitingQueue.dto.command.TokenCommand
import hhplus.concertreservation.application.waitingQueue.dto.info.TokenInfo
import hhplus.concertreservation.application.waitingQueue.dto.info.WaitingQueueInfo
import hhplus.concertreservation.application.waitingQueue.mapper.WaitingQueueMapper
import hhplus.concertreservation.domain.concert.entity.ConcertSchedule
import hhplus.concertreservation.domain.concert.service.ConcertScheduleService
import hhplus.concertreservation.domain.waitingQueue.WaitingQueue
import hhplus.concertreservation.domain.waitingQueue.WaitingQueueService
import org.springframework.stereotype.Component

@Component
class WaitingQueueFacade(
    private val waitingQueueMapper: WaitingQueueMapper,
    private val waitingQueueService: WaitingQueueService,
    private val scheduleService: ConcertScheduleService,
) {
    fun getWaitingQueueToken(tokenCommand: TokenCommand) : TokenInfo {
        val concertSchedule: ConcertSchedule = scheduleService.getScheduleById(tokenCommand.concertScheduleId)
        val waitingQueue: WaitingQueue = waitingQueueService.getOrGenerateToken(tokenCommand.token, concertSchedule)
        return waitingQueueMapper.toTokenInfo(waitingQueue)
    }

    fun getWaitingQueueStatus(token: String): WaitingQueueInfo {
        val waitingQueue: WaitingQueue = waitingQueueService.validateAndGetToken(token)
        val remainingPosition: Int = waitingQueueService.calculateRemainingPosition(waitingQueue.scheduleId, waitingQueue.queuePosition)
        return waitingQueueMapper.toWaitingQueueInfo(waitingQueue, remainingPosition)
    }
}

package hhplus.concertreservation.interfaces.api.waitingQueue.dto.req

import hhplus.concertreservation.application.waitingQueue.dto.command.TokenCommand

data class TokenRequest(
    val concertId: Long,
    val concertScheduleId: Long,
    val userId: Long,
) {
    fun toCommand(token: String?): TokenCommand {
        return TokenCommand(
            concertId = this.concertId,
            concertScheduleId = this.concertScheduleId,
            userId = this.userId,
            token = token
        )
    }
}

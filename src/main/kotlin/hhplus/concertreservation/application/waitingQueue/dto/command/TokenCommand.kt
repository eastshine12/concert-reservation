package hhplus.concertreservation.application.waitingQueue.dto.command

data class TokenCommand(
    val concertId: Long,
    val concertScheduleId: Long,
    val userId: Long,
    val token: String?,
)

package hhplus.concertreservation.interfaces.api.waitingQueue.dto.req

data class TokenRequest(
    val concertId: Long,
    val concertScheduleId: Long,
    val userId: Long,
)

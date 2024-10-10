package hhplus.concertreservation.interfaces.api.token.dto.req

data class TokenRequest(
    val concertId: Long,
    val concertScheduleId: Long,
    val userId: Long,
)

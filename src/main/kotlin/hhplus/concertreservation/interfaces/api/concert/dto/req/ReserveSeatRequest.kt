package hhplus.concertreservation.interfaces.api.concert.dto.req

data class ReserveSeatRequest(
    val concertId: Long,
    val scheduleId: Long,
    val seatId: Long,
)

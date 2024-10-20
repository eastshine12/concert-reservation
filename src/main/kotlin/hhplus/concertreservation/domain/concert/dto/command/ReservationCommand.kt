package hhplus.concertreservation.domain.concert.dto.command

data class ReservationCommand(
    val token: String,
    val userId: Long,
    val scheduleId: Long,
    val seatId: Long
)

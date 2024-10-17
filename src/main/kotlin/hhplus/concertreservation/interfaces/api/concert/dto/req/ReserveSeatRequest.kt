package hhplus.concertreservation.interfaces.api.concert.dto.req

import hhplus.concertreservation.application.concert.dto.command.ReservationCommand

data class ReserveSeatRequest(
    val userId: Long,
    val concertId: Long,
    val scheduleId: Long,
    val seatId: Long,
) {
    fun toCommand(token: String): ReservationCommand {
        return ReservationCommand(
            token = token,
            userId = userId,
            scheduleId = scheduleId,
            seatId = seatId,
        )
    }
}

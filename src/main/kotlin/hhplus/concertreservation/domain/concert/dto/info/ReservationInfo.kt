package hhplus.concertreservation.domain.concert.dto.info

import java.time.LocalDateTime

data class ReservationInfo(
    val reservationId: Long,
    val userId: Long,
    val scheduleId: Long,
    val seatId: Long,
    val status: String,
    val expiresAt: LocalDateTime,
)

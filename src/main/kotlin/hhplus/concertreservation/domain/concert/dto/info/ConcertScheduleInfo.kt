package hhplus.concertreservation.domain.concert.dto.info

import java.time.LocalDateTime

data class ConcertScheduleInfo(
    val scheduleId: Long,
    val startTime: LocalDateTime,
    val availableSeats: Int,
    val totalSeats: Int,
    val status: String,
)

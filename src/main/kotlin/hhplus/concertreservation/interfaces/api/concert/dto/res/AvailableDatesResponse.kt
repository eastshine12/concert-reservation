package hhplus.concertreservation.interfaces.api.concert.dto.res

import java.time.LocalDateTime

data class ConcertResponse(
    val concertId: Long,
    val title: String,
    val schedules: List<SchedulesResponse>,
)

data class SchedulesResponse(
    val scheduleId: Long,
    val startTime: String,
    val totalSeats: Int,
    val availableSeats: Int,
)

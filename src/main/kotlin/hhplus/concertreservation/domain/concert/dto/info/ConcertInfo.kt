package hhplus.concertreservation.domain.concert.dto.info

data class ConcertInfo(
    val id: Long,
    val title: String,
    val duration: Int,
    val schedules: List<ConcertScheduleInfo>,
)

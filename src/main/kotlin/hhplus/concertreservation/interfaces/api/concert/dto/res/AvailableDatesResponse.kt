package hhplus.concertreservation.interfaces.api.concert.dto.res

import hhplus.concertreservation.domain.concert.dto.info.ConcertInfo

data class ConcertResponse(
    val concertId: Long,
    val title: String,
    val schedules: List<SchedulesResponse>,
) {
    companion object {
        fun fromInfo(concertInfo: ConcertInfo): ConcertResponse {
            return ConcertResponse(
                concertId = concertInfo.id,
                title = concertInfo.title,
                schedules =
                    concertInfo.schedules.map { schedule ->
                        SchedulesResponse(
                            scheduleId = schedule.scheduleId,
                            startTime = schedule.startTime.toString(),
                            totalSeats = schedule.totalSeats,
                            availableSeats = schedule.availableSeats,
                        )
                    },
            )
        }
    }
}

data class SchedulesResponse(
    val scheduleId: Long,
    val startTime: String,
    val totalSeats: Int,
    val availableSeats: Int,
)

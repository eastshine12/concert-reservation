package hhplus.concertreservation.application.concert.mapper

import hhplus.concertreservation.application.concert.dto.info.ConcertInfo
import hhplus.concertreservation.application.concert.dto.info.ConcertScheduleInfo
import hhplus.concertreservation.application.concert.dto.info.ReservationInfo
import hhplus.concertreservation.domain.concert.entity.Concert
import hhplus.concertreservation.domain.concert.entity.ConcertSchedule
import hhplus.concertreservation.domain.concert.entity.Reservation
import org.springframework.stereotype.Component

@Component
class ConcertMapper {
    fun toConcertInfo(concert: Concert, schedules: List<ConcertSchedule>): ConcertInfo {
        val scheduleInfos = schedules.map { toConcertScheduleInfo(it) }

        return ConcertInfo(
            id = concert.id,
            title = concert.title,
            duration = concert.duration,
            schedules = scheduleInfos
        )
    }

    fun toConcertScheduleInfo(schedule: ConcertSchedule): ConcertScheduleInfo {
        return ConcertScheduleInfo(
            scheduleId = schedule.id,
            startTime = schedule.startTime,
            availableSeats = schedule.availableSeats,
            totalSeats = schedule.totalSeats,
            status = if (schedule.availableSeats > 0) "AVAILABLE" else "SOLD_OUT"
        )
    }
    fun toReservationInfo(reservation: Reservation): ReservationInfo {
        return ReservationInfo(
            success = true,
            reservationId = reservation.id,
        )
    }
}

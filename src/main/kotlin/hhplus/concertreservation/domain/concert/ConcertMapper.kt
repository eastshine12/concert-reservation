package hhplus.concertreservation.domain.concert

import hhplus.concertreservation.application.concert.dto.info.ConcertInfo
import hhplus.concertreservation.application.concert.dto.info.ConcertScheduleInfo
import hhplus.concertreservation.application.concert.dto.info.ReservationInfo
import hhplus.concertreservation.application.concert.dto.info.SeatInfo
import hhplus.concertreservation.domain.concert.entity.Concert
import hhplus.concertreservation.domain.concert.entity.ConcertSchedule
import hhplus.concertreservation.domain.concert.entity.Reservation
import hhplus.concertreservation.domain.concert.entity.Seat

fun Concert.toConcertInfo(schedules: List<ConcertSchedule>): ConcertInfo {
    val scheduleInfos = schedules.map { it.toConcertScheduleInfo() }
    return ConcertInfo(
        id = this.id,
        title = this.title,
        duration = this.duration,
        schedules = scheduleInfos
    )
}

fun ConcertSchedule.toConcertScheduleInfo(): ConcertScheduleInfo {
    return ConcertScheduleInfo(
        scheduleId = this.id,
        startTime = this.startTime,
        availableSeats = this.availableSeats,
        totalSeats = this.totalSeats,
        status = if (this.availableSeats > 0) "AVAILABLE" else "SOLD_OUT"
    )
}

fun Reservation.toReservationInfo(success: Boolean): ReservationInfo {
    return ReservationInfo(
        success = success,
        reservationId = this.id,
    )
}

fun Seat.toSeatInfo(): SeatInfo {
    return SeatInfo(
        seatNumber = this.seatNumber,
        status = this.status.name,
        price = this.price
    )
}

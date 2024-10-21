package hhplus.concertreservation.domain.concert

import hhplus.concertreservation.domain.concert.dto.info.*
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
        schedules = scheduleInfos,
    )
}

fun ConcertSchedule.toConcertScheduleInfo(): ConcertScheduleInfo {
    return ConcertScheduleInfo(
        scheduleId = this.id,
        startTime = this.startTime,
        availableSeats = this.availableSeats,
        totalSeats = this.totalSeats,
        status = if (this.availableSeats > 0) "AVAILABLE" else "SOLD_OUT",
    )
}

fun Reservation.toCreateReservationInfo(success: Boolean): CreateReservationInfo {
    return CreateReservationInfo(
        success = success,
        reservationId = this.id,
    )
}

fun Reservation.toReservationInfo(): ReservationInfo {
    return ReservationInfo(
        reservationId = this.id,
        userId = this.userId,
        scheduleId = this.scheduleId,
        seatId = this.seatId,
        status = this.status.name,
        expiresAt = this.expiresAt,
    )
}

fun Seat.toSeatInfo(): SeatInfo {
    return SeatInfo(
        seatNumber = this.seatNumber,
        status = this.status.name,
        price = this.price,
    )
}

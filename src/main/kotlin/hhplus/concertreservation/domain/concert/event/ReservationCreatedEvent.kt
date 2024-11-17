package hhplus.concertreservation.domain.concert.event

import hhplus.concertreservation.domain.concert.entity.Reservation

data class ReservationCreatedEvent(
    val userId: Long,
    val scheduleId: Long,
    val seatId: Long,
    val reservationId: Long,
) {
    companion object {
        fun from(reservation: Reservation): ReservationCreatedEvent {
            return ReservationCreatedEvent(
                userId = reservation.userId,
                scheduleId = reservation.scheduleId,
                seatId = reservation.seatId,
                reservationId = reservation.id,
            )
        }
    }
}

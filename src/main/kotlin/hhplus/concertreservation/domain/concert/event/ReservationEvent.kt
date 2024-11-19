package hhplus.concertreservation.domain.concert.event

import hhplus.concertreservation.domain.concert.entity.Reservation

class ReservationEvent {
    data class Created(
        val userId: Long,
        val scheduleId: Long,
        val seatId: Long,
        val reservationId: Long,
    ) {
        companion object {
            fun from(reservation: Reservation): Created {
                return Created(
                    userId = reservation.userId,
                    scheduleId = reservation.scheduleId,
                    seatId = reservation.seatId,
                    reservationId = reservation.id,
                )
            }
        }
    }
}

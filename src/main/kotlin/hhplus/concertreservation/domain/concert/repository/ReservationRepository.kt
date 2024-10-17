package hhplus.concertreservation.domain.concert.repository

import hhplus.concertreservation.domain.concert.entity.Reservation
import java.time.LocalDateTime

interface ReservationRepository {
    fun save(reservation: Reservation): Reservation
    fun findByIdOrNullWithLock(id: Long): Reservation?
    fun findExpiredReservations(currentTime: LocalDateTime): List<Reservation>
}

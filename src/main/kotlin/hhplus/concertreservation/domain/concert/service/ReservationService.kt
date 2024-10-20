package hhplus.concertreservation.domain.concert.service

import hhplus.concertreservation.domain.concert.component.ConcertManager
import hhplus.concertreservation.domain.concert.component.SeatFinder
import hhplus.concertreservation.domain.concert.dto.info.ReservationInfo
import hhplus.concertreservation.domain.concert.entity.Reservation
import hhplus.concertreservation.domain.concert.repository.ReservationRepository
import hhplus.concertreservation.domain.concert.toReservationInfo
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
class ReservationService(
    private val seatFinder: SeatFinder,
    private val concertManager: ConcertManager,
    private val reservationRepository: ReservationRepository,
) {
    @Transactional
    fun createPendingReservation(
        userId: Long,
        scheduleId: Long,
        seatId: Long,
    ): ReservationInfo {
        seatFinder.getAvailableSeatWithLock(scheduleId, seatId).reserve()
        occupySeatWithoutLock(scheduleId)
        val reservation = concertManager.createPendingReservation(userId, scheduleId, seatId)
        return reservation.toReservationInfo(success = true)
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun occupySeatWithoutLock(scheduleId: Long) {
        concertManager.getScheduleById(scheduleId).occupySeat()
    }

    fun confirmReservation(reservationId: Long): Reservation {
        val reservation: Reservation = concertManager.getReservationWithLock(reservationId)
        reservation.confirm()
        return reservationRepository.save(reservation)
    }
}

package hhplus.concertreservation.domain.concert.scheduler

import hhplus.concertreservation.domain.concert.component.ConcertManager
import hhplus.concertreservation.domain.concert.component.SeatFinder
import hhplus.concertreservation.domain.concert.entity.Reservation
import hhplus.concertreservation.domain.concert.repository.ReservationRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Component
class ConcertScheduler(
    private val reservationRepository: ReservationRepository,
    private val concertManager: ConcertManager,
    private val seatFinder: SeatFinder,
) {
    @Scheduled(fixedRateString = "\${reservation.expireCheckRate}")
    @Transactional
    fun expireReservations() {
        val expiredReservations = reservationRepository.findExpiredReservations(LocalDateTime.now())
        expiredReservations.forEach { reservation: Reservation ->
            reservation.cancel()
            seatFinder.getSeatWithLock(reservation.seatId).markAsAvailable()
            concertManager.getScheduleById(reservation.scheduleId).restoreSeat()
        }
    }
}

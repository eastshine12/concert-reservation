package hhplus.concertreservation.domain.concert.service

import hhplus.concertreservation.domain.concert.component.ConcertManager
import hhplus.concertreservation.domain.concert.component.SeatFinder
import hhplus.concertreservation.domain.concert.dto.info.CreateReservationInfo
import hhplus.concertreservation.domain.concert.dto.info.ReservationInfo
import hhplus.concertreservation.domain.concert.entity.Reservation
import hhplus.concertreservation.domain.concert.event.ReservationCreatedEvent
import hhplus.concertreservation.domain.concert.repository.ReservationRepository
import hhplus.concertreservation.domain.concert.toCreateReservationInfo
import hhplus.concertreservation.domain.concert.toReservationInfo
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
class ReservationService(
    private val seatFinder: SeatFinder,
    private val concertManager: ConcertManager,
    private val reservationRepository: ReservationRepository,
    private val applicationEventPublisher: ApplicationEventPublisher,
) {
    @Transactional
    fun createPendingReservation(
        userId: Long,
        scheduleId: Long,
        seatId: Long,
    ): CreateReservationInfo {
        seatFinder.getAvailableSeat(scheduleId, seatId).reserve()
        occupySeat(scheduleId)
        val reservation = concertManager.createPendingReservation(userId, scheduleId, seatId)
        applicationEventPublisher.publishEvent(ReservationCreatedEvent.from(reservation))
        return reservation.toCreateReservationInfo(success = true)
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun occupySeat(scheduleId: Long) {
        concertManager.getScheduleById(scheduleId).occupySeat()
    }

    fun confirmReservation(reservationId: Long): ReservationInfo {
        val reservation: Reservation = concertManager.getReservationWithLock(reservationId)
        reservation.confirm()
        return reservationRepository.save(reservation).toReservationInfo()
    }
}

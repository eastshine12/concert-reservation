package hhplus.concertreservation.domain.concert.scheduler

import hhplus.concertreservation.domain.concert.component.ConcertManager
import hhplus.concertreservation.domain.concert.component.SeatFinder
import hhplus.concertreservation.domain.concert.dto.ScheduleSeatCount
import hhplus.concertreservation.domain.concert.entity.ConcertSchedule
import hhplus.concertreservation.domain.concert.entity.Reservation
import hhplus.concertreservation.domain.concert.repository.ConcertScheduleRepository
import hhplus.concertreservation.domain.concert.repository.ReservationRepository
import hhplus.concertreservation.domain.concert.repository.SeatRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Component
class ConcertScheduler(
    private val reservationRepository: ReservationRepository,
    private val concertScheduleRepository: ConcertScheduleRepository,
    private val seatJpaRepository: SeatRepository,
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

    @Scheduled(fixedRateString = "\${reservation.syncAvailableSeatsRate}")
    fun syncAvailableSeats() {
        val allSchedules = concertScheduleRepository.findAll()
        val filterSchedules = allSchedules.filter { it.startTime.isAfter(LocalDateTime.now()) }
        val seatCountList: List<ScheduleSeatCount> = seatJpaRepository.countAvailableSeatsGroupByScheduleId()
        val schedulesToUpdate = mutableListOf<ConcertSchedule>()
        filterSchedules.forEach { schedule ->
            val seatCount = seatCountList.find { it.scheduleId == schedule.id }?.seatCount ?: 0L
            schedule.updateAvailableSeats(seatCount.toInt())
            schedulesToUpdate.add(schedule)
        }
        concertScheduleRepository.saveAll(schedulesToUpdate)
    }
}

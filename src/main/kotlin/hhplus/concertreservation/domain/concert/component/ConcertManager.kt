package hhplus.concertreservation.domain.concert.component

import hhplus.concertreservation.config.ReservationProperties
import hhplus.concertreservation.domain.common.enums.ReservationStatus
import hhplus.concertreservation.domain.concert.entity.ConcertSchedule
import hhplus.concertreservation.domain.concert.entity.Reservation
import hhplus.concertreservation.domain.concert.exception.ConcertScheduleNotFoundException
import hhplus.concertreservation.domain.concert.exception.ReservationNotFoundException
import hhplus.concertreservation.domain.concert.repository.ConcertScheduleRepository
import hhplus.concertreservation.domain.concert.repository.ReservationRepository
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class ConcertManager(
    private val scheduleRepository: ConcertScheduleRepository,
    private val reservationRepository: ReservationRepository,
    private val reservationProperties: ReservationProperties,
) {
    fun getScheduleById(scheduleId: Long): ConcertSchedule {
        return scheduleRepository.findByIdOrNull(scheduleId)
            ?: throw ConcertScheduleNotFoundException("Concert schedule not found with id $scheduleId")
    }

    fun getReservationWithLock(reservationId: Long): Reservation {
        return reservationRepository.findByIdOrNullWithLock(reservationId)
            ?: throw ReservationNotFoundException("Reservation not found with id $reservationId")
    }

    fun createPendingReservation(
        userId: Long,
        scheduleId: Long,
        seatId: Long,
    ): Reservation {
        val expiresAt = LocalDateTime.now().plusMinutes(reservationProperties.expireMinutes)
        val reservation =
            Reservation(
                userId = userId,
                scheduleId = scheduleId,
                seatId = seatId,
                status = ReservationStatus.PENDING,
                expiresAt = expiresAt,
            )
        return reservationRepository.save(reservation)
    }
}

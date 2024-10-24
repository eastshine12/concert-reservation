package hhplus.concertreservation.domain.concert.component

import hhplus.concertreservation.config.ReservationProperties
import hhplus.concertreservation.domain.common.enums.ReservationStatus
import hhplus.concertreservation.domain.common.error.ErrorType
import hhplus.concertreservation.domain.common.exception.CoreException
import hhplus.concertreservation.domain.concert.entity.ConcertSchedule
import hhplus.concertreservation.domain.concert.entity.Reservation
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
            ?: throw CoreException(
                errorType = ErrorType.NO_CONCERT_SCHEDULE_FOUND,
                details =
                    mapOf(
                        "scheduleId" to scheduleId,
                    ),
            )
    }

    fun getReservationWithLock(reservationId: Long): Reservation {
        return reservationRepository.findByIdOrNullWithLock(reservationId)
            ?: throw CoreException(
                errorType = ErrorType.NO_RESERVATION_FOUND,
                details =
                    mapOf(
                        "reservationId" to reservationId,
                    ),
            )
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

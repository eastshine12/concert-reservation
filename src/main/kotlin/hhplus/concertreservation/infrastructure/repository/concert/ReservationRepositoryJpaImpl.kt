package hhplus.concertreservation.infrastructure.repository.concert

import hhplus.concertreservation.domain.common.enums.ReservationStatus
import hhplus.concertreservation.domain.concert.entity.Reservation
import hhplus.concertreservation.domain.concert.repository.ReservationRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class ReservationRepositoryJpaImpl(
    private val reservationJpaRepository: ReservationJpaRepository,
) : ReservationRepository {
    override fun save(reservation: Reservation): Reservation {
        return reservationJpaRepository.save(reservation)
    }

    override fun findByIdOrNullWithLock(id: Long): Reservation? {
        return reservationJpaRepository.findByIdOrNullWithLock(id)
    }

    override fun findExpiredReservations(currentTime: LocalDateTime): List<Reservation> {
        return reservationJpaRepository.findAllByExpiresAtBeforeAndStatus(currentTime, ReservationStatus.PENDING)
    }
}

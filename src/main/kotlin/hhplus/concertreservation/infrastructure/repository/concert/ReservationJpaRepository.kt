package hhplus.concertreservation.infrastructure.repository.concert

import hhplus.concertreservation.domain.common.enums.ReservationStatus
import hhplus.concertreservation.domain.concert.entity.Reservation
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime

interface ReservationJpaRepository : JpaRepository<Reservation, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from Reservation r where r.id = :id")
    fun findByIdOrNullWithLock(id: Long): Reservation?

    fun findAllByExpiresAtBeforeAndStatus(
        expiresAt: LocalDateTime,
        status: ReservationStatus,
    ): List<Reservation>
}

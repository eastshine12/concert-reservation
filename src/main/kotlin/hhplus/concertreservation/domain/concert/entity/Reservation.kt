package hhplus.concertreservation.domain.concert.entity

import hhplus.concertreservation.domain.common.BaseEntity
import hhplus.concertreservation.domain.common.enums.ReservationStatus
import hhplus.concertreservation.domain.common.error.ErrorType
import hhplus.concertreservation.domain.common.exception.CoreException
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class Reservation(
    val userId: Long,
    val scheduleId: Long,
    val seatId: Long,
    status: ReservationStatus,
    expiresAt: LocalDateTime,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
) : BaseEntity() {
    @Enumerated(EnumType.STRING)
    var status: ReservationStatus = status
        protected set

    var expiresAt: LocalDateTime = expiresAt
        protected set

    fun confirm() {
        if (status != ReservationStatus.PENDING) {
            throw CoreException(
                errorType = ErrorType.INVALID_RESERVATION_STATUS,
                message = "Reservation is not in pending state",
            )
        }
        status = ReservationStatus.CONFIRMED
    }

    fun cancel() {
        if (status == ReservationStatus.CONFIRMED) {
            throw CoreException(
                errorType = ErrorType.INVALID_RESERVATION_STATUS,
                message = "Confirmed reservations cannot be canceled.",
            )
        }
        this.status = ReservationStatus.CANCELED
    }
}

package hhplus.concertreservation.domain.concert.entity

import hhplus.concertreservation.domain.common.BaseEntity
import hhplus.concertreservation.domain.common.enums.ReservationStatus
import hhplus.concertreservation.domain.concert.exception.InvalidReservationStatusException
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class Reservation(
    var userId: Long,
    var scheduleId: Long,
    var seatId: Long,
    @Enumerated(EnumType.STRING)
    var status: ReservationStatus,
    var expiresAt: LocalDateTime,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L,
) : BaseEntity() {
    fun confirm() {
        if (status != ReservationStatus.PENDING) {
            throw InvalidReservationStatusException("Reservation is not in pending state")
        }
        status = ReservationStatus.CONFIRMED
    }

    fun cancel() {
        if (status == ReservationStatus.CONFIRMED) {
            throw InvalidReservationStatusException("Confirmed reservations cannot be canceled.")
        }
        this.status = ReservationStatus.CANCELED
    }
}

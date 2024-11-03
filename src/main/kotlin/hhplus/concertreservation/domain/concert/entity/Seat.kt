package hhplus.concertreservation.domain.concert.entity

import hhplus.concertreservation.domain.common.BaseEntity
import hhplus.concertreservation.domain.common.enums.SeatStatus
import hhplus.concertreservation.domain.common.error.ErrorType
import hhplus.concertreservation.domain.common.exception.CoreException
import jakarta.persistence.*
import java.math.BigDecimal

@Entity
class Seat(
    val scheduleId: Long,
    seatNumber: Int,
    price: BigDecimal,
    status: SeatStatus,
    @Version
    val version: Long = 0L,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
) : BaseEntity() {
    var seatNumber: Int = seatNumber
        protected set

    var price: BigDecimal = price
        protected set

    @Enumerated(EnumType.STRING)
    var status: SeatStatus = status
        protected set

    fun reserve() {
        if (this.status != SeatStatus.AVAILABLE) {
            throw CoreException(
                errorType = ErrorType.SEAT_UNAVAILABLE,
            )
        }
        this.status = SeatStatus.UNAVAILABLE
    }

    fun markAsAvailable() {
        if (status == SeatStatus.AVAILABLE) {
            throw CoreException(
                errorType = ErrorType.SEAT_UNAVAILABLE,
                message = "Seat is already available.",
            )
        }
        this.status = SeatStatus.AVAILABLE
    }
}
